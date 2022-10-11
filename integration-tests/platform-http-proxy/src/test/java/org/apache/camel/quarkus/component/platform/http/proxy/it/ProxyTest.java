package org.apache.camel.quarkus.component.platform.http.proxy.it;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@QuarkusTestResource(PlatformHttpTestResource.class)
public class ProxyTest {

    @ConfigProperty(name = "platform.origin.url", defaultValue = "TODO")
    String url;

    @Test
    void testProxy() {

       final var proxyUrl = "http://localhost:" + RestAssured.port;
        System.out.println("proxyUrl :: " + proxyUrl);
        /*given()
                .body("hello")
                .proxy(proxyUrl)
                .contentType(ContentType.HTML)
                .when().get("http://neverssl.com:80")
                .then()
                .statusCode(200)
                .body(containsString("<html>"));

        String url = given()
                .get("/platform-http-proxy")
                        .body().asString();*/

        System.out.println("URL is :: " +url);
        given()
                .body("hello")
                .proxy(proxyUrl)
                .contentType(ContentType.HTML)
                .when().get(url)
                .then()
                .statusCode(200)
                .body(equalTo("{\"message\": \"Hello World!\"}"));
    }

}
