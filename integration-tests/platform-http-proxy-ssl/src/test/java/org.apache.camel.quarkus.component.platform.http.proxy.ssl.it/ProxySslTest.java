package org.apache.camel.quarkus.component.platform.http.proxy.ssl.it;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@QuarkusTestResource(PlatformHttpSSLTestResource.class)
public class ProxySslTest {
    /* @Test
    void testProxy() {
    
        final var proxyUrl = "http://localhost:" + RestAssured.port;
        System.out.println("proxyUrl :: " + proxyUrl);
    
        String url = given()
                .get("/platform-http-proxy")
                .body().asString();
    
        System.out.println("URL is :: " + url);
        given()
                .proxy(proxyUrl)
                .contentType(ContentType.JSON)
                .when().get(url)
                .then()
                .statusCode(200)
                .body(equalTo("{\"message\": \"Hello World!\"}"));
    
        given()
                .body("hello")
                .proxy(proxyUrl)
                .contentType(ContentType.JSON)
                .when().post(url)
                .then()
                .statusCode(200)
                .body(equalTo("{\"message\": \"Hello World!\"}"));
    }*/
    @Test
    void test() {
        final var proxyUrl = "http://localhost:" + RestAssured.port;
        System.out.println("proxyUrl :: " + proxyUrl);

        RestAssured.useRelaxedHTTPSValidation();
        /*RestAssured.trustStore("ssl/keystore.jks", "changeit");

         RestAssured.config().getSSLConfig().with().keyStore("ssl/keystore.jks", "changeit");*/

        String url = given()
                .get("/platform-http-proxy")
                .body().asString();

        System.out.println("URL is :: " + url);
        given()
                .proxy(proxyUrl)
                .contentType(ContentType.JSON)
                .when().get(url+"?sslContextParameters=#sslContextParameters&x509HostnameVerifier=#x509HostnameVerifier")
                .then()
                .statusCode(200)
                .body(equalTo("{\"message\": \"Hello World!\"}"));
    }

    // @Test
    void test2() {
        /* final var proxyUrl = "http://localhost:" + RestAssured.port;
        System.out.println("proxyUrl :: " + proxyUrl);
        
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.trustStore("ssl/keystore.jks", "changeit");
        
        // RestAssured.config().getSSLConfig().with().keyStore("ssl/keystore.jks", "changeit");
        
        String url = given()
                .get("/platform-http-proxy")
                .body().asString();*/

        //  System.out.println("URL is :: " + url);
        given()
                //   .proxy(proxyUrl)
                .contentType(ContentType.JSON)
                .when().get("/hello")
                .then()
                .statusCode(200)
                .body(equalTo("{\"message\": \"Hello World!\"}"));
    }

    @Test
    void test3() {
        final var proxyUrl = "http://localhost:" + RestAssured.port+"/platform-http-proxy/hey";
        System.out.println("proxyUrl :: " + proxyUrl);
        given()
                //   .proxy(proxyUrl)
                .contentType(ContentType.JSON)
                .when().get(proxyUrl)
                .then()
                .statusCode(200)
                .body(equalTo("{\"message\": \"Hello World!\"}"));
    }

    // @Test
    void testMockURI() {

        RestAssured.useRelaxedHTTPSValidation();
        //RestAssured.trustStore("ssl/truststore.jks", "changeit");
        //RestAssured.config().getSSLConfig().with().keyStore("ssl/keystore.jks", "changeit");
        RestAssured.config().getSSLConfig().with().keyStore("test/baeldung.p12", "changeit");

        String url = given()
                .get("/platform-http-proxy")
                .body().asString();

        System.out.println("URL is :: " + url);

        given()
                .contentType(ContentType.JSON)
                .when().get(url)
                .then()
                .statusCode(200)
                .body(equalTo("{\"message\": \"Hello World!\"}"));
    }

    /*private static SSLContext buildSSLContextWithTrustStore(KeyStoreSettings trustStoreSettings) {
        try {
            KeyStore trustStore = trustStoreSettings.loadStore();
            return SSLContexts.custom()
                    .loadTrustMaterial(new TrustSelfSignedStrategy())
                    .loadKeyMaterial(trustStore, trustStoreSettings.password().toCharArray())
                    .useTLS()
                    .build();
        } catch (Exception e) {
            return throwUnchecked(e, SSLContext.class);
        }
    }*/

}
