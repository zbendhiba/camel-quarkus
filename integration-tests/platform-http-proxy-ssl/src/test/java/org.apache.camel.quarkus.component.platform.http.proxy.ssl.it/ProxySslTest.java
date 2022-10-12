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
    @Test
    void test() {
        final var proxyUrl = "http://localhost:" + RestAssured.port;
        String url = given()
                .get("/platform-http-proxy")
                .body().asString();

        // forcing RestAssured to send a GET instead of CONNECT with proxy settings
        url = url.replace("https", "http");

        given()
                .proxy(proxyUrl)
                .contentType(ContentType.JSON)
                .when().get(url)
                .then()
                .statusCode(200)
                .body(equalTo("{\"message\": \"Hello World!\"}"));
    }
}
