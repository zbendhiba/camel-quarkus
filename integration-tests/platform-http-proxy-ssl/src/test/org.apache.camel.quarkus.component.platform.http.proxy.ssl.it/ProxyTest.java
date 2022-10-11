package test.it;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@QuarkusTestResource(PlatformHttpTestResource.class)
public class ProxyTest {
    @Test
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
    }

}
