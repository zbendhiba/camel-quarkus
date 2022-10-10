package org.apache.camel.quarkus.component.platform.http.proxy.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
public class ProxyTest {

    @Test
    void testProxy() {

        final var proxyUrl = "http://localhost:" + RestAssured.port;
        System.out.println("proxyUrl :: " + proxyUrl);
        given()
                .body("hello")
                .proxy(proxyUrl)
                .contentType(ContentType.HTML)
                .when().get("http://neverssl.com:80")
                .then()
                .statusCode(200)
                .body(containsString("<html>"));
    }

}
