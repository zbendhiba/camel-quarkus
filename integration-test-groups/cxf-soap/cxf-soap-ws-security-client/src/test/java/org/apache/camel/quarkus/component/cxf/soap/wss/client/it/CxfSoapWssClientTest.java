/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.quarkus.component.cxf.soap.wss.client.it;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.DisabledOnIntegrationTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebServiceException;
import org.apache.camel.quarkus.components.cxf.soap.wss.client.helloworld.SayHelloService;
import org.apache.camel.quarkus.components.cxf.soap.wss.client.helloworld.SayHelloWrongWS;
import org.apache.cxf.ws.security.SecurityConstants;
import org.awaitility.Awaitility;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@QuarkusTestResource(value = CxfWssClientTestResource.class, restrictToAnnotatedClass = false)
class CxfSoapWssClientTest {

    @Test
    public void wsSecurityClient() {
        RestAssured.given()
                .queryParam("a", "12")
                .queryParam("b", "8")
                .post("/cxf-soap/wss/client/modulo")
                .then()
                .statusCode(201)
                .body(equalTo("4"));
    }

    /**
     * Make sure that our static copy is the same as the WSDL served by the container
     *
     * @throws IOException
     */
    @Test
    void wsdlUpToDate() throws IOException {
        final String wsdlUrl = ConfigProvider.getConfig()
                .getValue("camel-quarkus.it.wss.client.baseUri", String.class);

        final String wsdlRelPath = "wsdl/WssCalculatorService.wsdl";
        final Path staticCopyPath = Paths.get("src/main/resources/" + wsdlRelPath);
        Assumptions.assumeTrue(Files.isRegularFile(staticCopyPath),
                staticCopyPath + " does not exist - we probably run inside Quarkus Platform");

        /* The changing Docker IP address in the WSDL should not matter */
        final String sanitizerRegex = "<soap:address location=\"http://[^/]*/calculator-ws/WssCalculatorService\"></soap:address>";
        final String staticCopyContent = Files
                .readString(staticCopyPath, StandardCharsets.UTF_8)
                .replaceAll(sanitizerRegex, "")
                //remove a comment with license
                .replaceAll("<!--[.\\s\\S]*?-->", "\n")
                //remove all whitesaces to ignore formatting changes
                .replaceAll("\\s", "");

        final String expected = RestAssured.given()
                .get(wsdlUrl + "/calculator-ws/WssCalculatorService?wsdl")
                .then()
                .statusCode(200)
                .extract().body().asString();

        final String expectedContent = expected.replaceAll(sanitizerRegex, "");

        if (!expected.replaceAll(sanitizerRegex, "").replaceAll("\\s", "").equals(staticCopyContent)) {
            Files.writeString(staticCopyPath, expectedContent, StandardCharsets.UTF_8);
            Assertions.fail("The static WSDL copy in " + staticCopyPath
                    + " went out of sync with the WSDL served by the container. The content was updated by the test, you just need to review and commit the changes.");
        }
    }

    /**
     * In case of the wrong security configuration and before this
     * <a href="https://github.com/jboss-fuse/cxf/pull/496">fix<a/>
     * in CXF (can be simulated on camel-quarkus tag 3.0.0-RC2), the client would hang indefinitely.
     * This tests covers such wrong configuration and verifies that no regression causing indefinite hang is present in
     * the current code.
     */
    @Test
    @DisabledOnIntegrationTest("Test doesn't have native part, no server for SayHello.wsdl exists")
    public void testWrongClientNotHanging() {

        Awaitility.await().atMost(30, TimeUnit.SECONDS).pollInterval(10, TimeUnit.SECONDS).until(() -> {
            try {
                //always fails because there is no server implementation
                createSayHelloWrongClient().sayHelloWrong("Sheldon");
            } catch (WebServiceException e) {
                return e.getCause() != null && e.getCause() instanceof ConnectException;
            }
            //can not happen (client does not work)
            return false;
        });
    }

    SayHelloWrongWS createSayHelloWrongClient() {

        final URL serviceUrl = Thread.currentThread().getContextClassLoader().getResource("wsdl/HelloWorld.wsdl");
        final Service service = Service.create(serviceUrl, SayHelloService.SERVICE);

        SayHelloWrongWS port = service.getPort(SayHelloWrongWS.class);
        BindingProvider bp = (BindingProvider) port;

        Map<String, Object> requestContext = bp.getRequestContext();

        //non-existing server url is used and
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:45698/soapservice/sayHelloWrong");

        Properties securityProps = new Properties();
        securityProps.put("org.apache.wss4j.crypto.provider", "org.apache.wss4j.common.crypto.Merlin");
        securityProps.put("org.apache.wss4j.crypto.merlin.keystore.type", "pkcs12");
        securityProps.put("org.apache.wss4j.crypto.merlin.keystore.file", "alice_wrong.jks");
        securityProps.put("org.apache.wss4j.crypto.merlin.keystore.password", "password");
        securityProps.put("org.apache.wss4j.crypto.merlin.keystore.alias", "alice_wrong");
        securityProps.put("org.apache.wss4j.crypto.merlin.keystore.private.password", "password");
        securityProps.put("org.apache.wss4j.crypto.merlin.keystore.private.caching", "true");

        requestContext.put(SecurityConstants.SIGNATURE_PROPERTIES, securityProps);

        return port;
    }

}
