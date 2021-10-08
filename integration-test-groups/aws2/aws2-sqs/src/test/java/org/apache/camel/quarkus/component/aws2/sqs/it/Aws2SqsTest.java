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
package org.apache.camel.quarkus.component.aws2.sqs.it;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.camel.quarkus.test.support.aws2.Aws2TestResource;
import org.awaitility.Awaitility;
import org.eclipse.microprofile.config.ConfigProvider;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.core.Is.is;

@QuarkusTest
@QuarkusTestResource(Aws2TestResource.class)
public class Aws2SqsTest {

    @Test
    void testSqs() {

        final String queueName = getPredefinedQueueName();

        try {
            // auto create the queue
            /*   RestAssured.post("/aws2-sqs/sqs/queue/autocreate/" + queueName)
                    .then()
                    .statusCode(200);*/
            Assertions.assertTrue(Stream.of(listQueues(queueName)).anyMatch(url -> url.contains(queueName)));

            // send and receive a message
            final String msg = sendSingleMessageToQueue(queueName);
            awaitMessageWithExpectedContentFromQueue(msg, queueName);

            // send and receive a receipt
            sendSingleMessageToQueue(queueName);
            final String receipt = receiveReceiptOfMessageFromQueue(queueName);
            deleteMessageFromQueue(queueName, receipt);

        } finally {
            // delete the queue
            deleteQueue(queueName);
        }

    }

    private String getPredefinedQueueName() {
        return ConfigProvider.getConfig().getValue("aws-sqs.queue-name", String.class);
    }

    private String[] listQueues(String queueName) {
        return RestAssured.get("/aws2-sqs/sqs/queues/" + queueName)
                .then()
                .statusCode(200)
                .extract()
                .body().as(String[].class);
    }

    private String sendSingleMessageToQueue(String queueName) {
        final String msg = "sqs" + UUID.randomUUID().toString().replace("-", "");
        RestAssured.given()
                .contentType(ContentType.TEXT)
                .body(msg)
                .post("/aws2-sqs/sqs/send/" + queueName)
                .then()
                .statusCode(201);
        return msg;
    }

    private void awaitMessageWithExpectedContentFromQueue(String expectedContent, String queueName) {
        Awaitility.await().pollInterval(1, TimeUnit.SECONDS).atMost(120, TimeUnit.SECONDS).until(
                () -> receiveMessageFromQueue(queueName),
                Matchers.is(expectedContent));
    }

    private String receiveMessageFromQueue(String queueName) {
        return receiveMessageFromQueue(queueName, true);
    }

    private String receiveMessageFromQueue(String queueName, boolean deleteMessage) {
        return RestAssured.get("/aws2-sqs/sqs/receive/" + queueName + "/" + deleteMessage)
                .then()
                .statusCode(anyOf(is(200), is(204)))
                .extract()
                .body()
                .asString();
    }

    private void deleteQueue(String queueName) {
        RestAssured.delete("/aws2-sqs/sqs/delete/queue/" + queueName)
                .then()
                .statusCode(200);
        awaitQueueDeleted(queueName);
    }

    private void awaitQueueDeleted(String queueName) {
        Awaitility.await().pollInterval(1, TimeUnit.SECONDS).atMost(120, TimeUnit.SECONDS).until(
                () -> Stream.of(listQueues(queueName)).noneMatch(url -> url.contains(queueName)));
    }

    private String receiveReceiptOfMessageFromQueue(String queueName) {
        return RestAssured.get("/aws2-sqs/sqs/receive/receipt/" + queueName)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();
    }

    private void deleteMessageFromQueue(String queueName, String receipt) {
        RestAssured.delete("/aws2-sqs/sqs/delete/message/" + queueName + "/" + receipt)
                .then()
                .statusCode(200);
    }

}
