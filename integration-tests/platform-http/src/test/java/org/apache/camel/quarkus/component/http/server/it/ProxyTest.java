package org.apache.camel.quarkus.component.http.server.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
public class ProxyTest {

    @Test
    void testProxy() {
        given()
                .body("hello")
                .proxy("http://localhost:8081")
                .contentType(ContentType.HTML)
                .when().get("http://neverssl.com:80")
                .then()
                .statusCode(200)
                .body(containsString("<html>"));
    }

}
