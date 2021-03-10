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
package org.apache.camel.quarkus.component.couchbase.it;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;

@QuarkusTest
@TestHTTPEndpoint(CouchbaseResource.class)
@QuarkusTestResource(CouchbaseTestResource.class)
class CouchbaseTest {

    @Test
    void test() {
        // adding multiple documents
        for (int i = 0; i < 15; i++) {
            String message = "hello" + i;
            String documentId = "DocumentID_" + i;
            given()
                    .contentType(ContentType.TEXT)
                    .body(message)
                    .when()
                    .put("/id/" + documentId)
                    .then()
                    .statusCode(200)
                    .body(equalTo("true"));
        }

        // adding this to wait end polling on simple
    //    CouchbaseTestResource.waitForCluster();



        // make sure only the 10 first messages were consumed
       await().atMost(30L, TimeUnit.SECONDS).until(() -> {
            long resultNumber = RestAssured.get("/consumer").then().extract().body().as(Long.class);
            return resultNumber == 10;
        });

        // getting one document
        given()
                .when()
                .get("/DocumentID_1")
                .then()
                .statusCode(200)
                .body(equalTo("hello1"));

        // deleting the document
        given()
                .when()
                .delete("DocumentID_1")
                .then()
                .statusCode(200);

        // getting another document
        given()
                .when()
                .get("/DocumentID_2")
                .then()
                .statusCode(200)
                .body(equalTo("hello2"));

        // updating the document
        given()
                .contentType(ContentType.TEXT)
                .body("updating hello2")
                .when()
                .put("/id/DocumentID_2")
                .then()
                .statusCode(200)
                .body(equalTo("true"));

        // check the result of update
        given()
                .when()
                .get("/DocumentID_2")
                .then()
                .statusCode(200)
                .body(equalTo("updating hello2"));

    }

}
