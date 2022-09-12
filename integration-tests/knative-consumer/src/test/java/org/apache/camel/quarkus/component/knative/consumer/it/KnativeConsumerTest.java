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

import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.MediaType;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.apache.camel.ServiceStatus;
import org.apache.camel.component.knative.http.KnativeHttpConsumerFactory;
import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
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
    void consumeEvents() {
        // consume from channel
        String cloudEvent = "{\"specversion\": \"1.0\", \"type\": \"org.camel.event\", \"id\": \"123456\", \"data\": \"Hello World Channel\", \"source\": \"camel\"}";
        given()
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.APPLICATION_JSON)
                .body(cloudEvent)
                .post("/channel")
                .then()
                .statusCode(200);

        Awaitility.await().pollInterval(1, TimeUnit.SECONDS).atMost(10, TimeUnit.SECONDS).until(() -> {
            final String body = given()
                    .get("/knative-consumer/read/queue-channel")
                    .then()
                    .extract().body().asString();
            return body != null && body.contains("Hello World Channel");
        });

        cloudEvent = "{\"specversion\": \"1.0\", \"type\": \"org.apache.camel.event\", \"id\": \"123456\", \"data\": \"Hello World Broker\", \"source\": \"camel\"}";
        given()
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.APPLICATION_JSON)
                .header("ce-type", "broker-test")
                .body(cloudEvent)
                .when()
                .post("/event")
                .then()
                .statusCode(200);

        Awaitility.await().pollInterval(1, TimeUnit.SECONDS).atMost(10, TimeUnit.SECONDS).until(() -> {
            final String body = given()
                    .get("/knative-consumer/read/queue-broker")
                    .then()
                    .extract().body().asString();
            return body != null && body.contains("Hello World Broker");
        });
    }

}
