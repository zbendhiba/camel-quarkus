package org.apache.camel.quarkus.component.salesforce;

import java.util.UUID;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.hamcrest.Matchers.is;

@EnabledIfEnvironmentVariable(named = "SALESFORCE_USERNAME", matches = ".+")
@EnabledIfEnvironmentVariable(named = "SALESFORCE_PASSWORD", matches = ".+")
@EnabledIfEnvironmentVariable(named = "SALESFORCE_CLIENTID", matches = ".+")
@EnabledIfEnvironmentVariable(named = "SALESFORCE_CLIENTSECRET", matches = ".+")
@QuarkusTest
public class SalesforceIntegrationTest {

    @Test
    public void testChangeDataCaptureEvents() {
        String accountId = null;
        try {
            // Start the Salesforce CDC consumer
            RestAssured.post("/salesforce/cdc/start")
                    .then()
                    .statusCode(200);

            // Create an account
            String accountName = "Camel Quarkus Account Test: " + UUID.randomUUID().toString();
            accountId = RestAssured.given()
                    .body(accountName)
                    .post("/salesforce/account")
                    .then()
                    .statusCode(200)
                    .extract()
                    .body()
                    .asString();

            // Verify we captured the account creation event
            RestAssured.given()
                    .get("/salesforce/cdc")
                    .then()
                    .statusCode(200)
                    .body("Name", is(accountName));
        } finally {
            // Shut down the CDC consumer
            RestAssured.post("/salesforce/cdc/stop")
                    .then()
                    .statusCode(200);

            // Clean up
            if (accountId != null) {
                RestAssured.delete("/salesforce/account/" + accountId)
                        .then()
                        .statusCode(204);
            }
        }
    }
}
