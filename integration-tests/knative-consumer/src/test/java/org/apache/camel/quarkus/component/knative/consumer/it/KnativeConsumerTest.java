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
package org.apache.camel.quarkus.component.knative.consumer.it;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.MediaType;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.path.json.JsonPath;
import org.apache.camel.component.cloudevents.CloudEvents;
import org.apache.camel.component.knative.http.KnativeHttpConsumerFactory;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class KnativeConsumerTest {
    @Test
    void inspect() {
        JsonPath p = given()
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.APPLICATION_JSON)
                .get("/knative-consumer/inspect")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath();

        assertEquals(KnativeHttpConsumerFactory.class.getName(), p.getString("consumer-factory"));
    }

    @Test
    void consumeEventsFromChannel() {
        // consume from channel
        given()
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.APPLICATION_JSON)
                .header("ce-specversion", CloudEvents.v1_0.version())
                .header("ce-id", UUID.randomUUID())
                .header("ce-time", "2018-04-05T17:31:00Z")
                .header("ce-source", "camel-channel-test")
                .body("Hello World - Testing Knative Channel Camel consumer")
                .when()
                .post("/channel")
                .then()
                .statusCode(200);

        Awaitility.await().pollInterval(1, TimeUnit.SECONDS).atMost(10, TimeUnit.SECONDS).until(() -> {
            final String body = given()
                    .get("/knative-consumer/read/queue-channel")
                    .then()
                    .extract().body().asString();
            return body != null && body.contains("Hello World - Testing Knative Channel Camel consumer");
        });

    }

    @Test
    void consumeFromBroker() {
        given()
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.TEXT_PLAIN)
                .header("ce-type", "event-test")
                .header("ce-specversion", CloudEvents.v1_0.version())
                .header("ce-id", UUID.randomUUID())
                .header("ce-time", "2018-04-05T17:31:00Z")
                .header("ce-source", "camel-event-test")
                .body("Hello World - Testing Knative Broker Camel consumer")
                .when()
                .post("/event")
                .then()
                .statusCode(200);

        Awaitility.await().pollInterval(1, TimeUnit.SECONDS).atMost(10, TimeUnit.SECONDS).until(() -> {
            final String body = given()
                    .get("/knative-consumer/read/queue-event")
                    .then()
                    .extract().body().asString();
            return body != null && body.contains("Hello World - Testing Knative Broker Camel consumer");
        });
    }

    @Test
    void consumeFromCamelService() {
        given()
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.TEXT_PLAIN)
                .header("ce-specversion", CloudEvents.v1_0.version())
                .header("ce-id", UUID.randomUUID())
                .header("ce-time", "2018-04-05T17:31:00Z")
                .header("ce-source", "camel-endpoint-test")
                .body("Hello World - Testing Knative Services Camel consumer")
                .when()
                .post("/my-endpoint")
                .then()
                .statusCode(200);

        Awaitility.await().pollInterval(1, TimeUnit.SECONDS).atMost(10, TimeUnit.SECONDS).until(() -> {
            final String body = given()
                    .get("/knative-consumer/read/queue-endpoint")
                    .then()
                    .extract().body().asString();
            return body != null && body.contains("Hello World - Testing Knative Services Camel consumer");
        });
    }

}
