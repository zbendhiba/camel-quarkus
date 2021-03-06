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
package org.apache.camel.quarkus.component.opentracing.it;

import java.util.List;
import java.util.Map;

import io.opentracing.tag.Tags;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class OpenTracingTest {

    @AfterEach
    public void afterEach() {
        RestAssured.post("/opentracing/mock/tracer/reset")
                .then()
                .statusCode(204);
    }

    @Test
    public void testTraceRoute() {
        // Generate messages
        for (int i = 0; i < 5; i++) {
            RestAssured.get("/opentracing/test/trace")
                    .then()
                    .statusCode(200);

            // No spans should be recorded for this route as they are excluded by camel.opentracing.exclude-patterns in
            // application.properties
            RestAssured.get("/opentracing/test/trace/filtered")
                    .then()
                    .statusCode(200);
        }

        // Retrieve recorded spans
        List<Map<String, String>> spans = getSpans();
        assertEquals(5, spans.size());

        for (Map<String, String> span : spans) {
            assertEquals("server", span.get(Tags.SPAN_KIND.getKey()));
            assertEquals("camel-platform-http", span.get(Tags.COMPONENT.getKey()));
            assertEquals("200", span.get(Tags.HTTP_STATUS.getKey()));
            assertEquals("GET", span.get(Tags.HTTP_METHOD.getKey()));
            assertEquals("platform-http:///opentracing/test/trace?httpMethodRestrict=GET", span.get("camel.uri"));
            assertTrue(span.get(Tags.HTTP_URL.getKey()).endsWith("/opentracing/test/trace"));
        }
    }

    @Test
    public void testTracedBeanInvokedFromRoute() {
        RestAssured.get("/opentracing/test/bean")
                .then()
                .statusCode(200);

        // Verify the span hierarchy is Platform HTTP Endpoint -> TracedBean
        List<Map<String, String>> spans = getSpans();
        assertEquals(2, spans.size());
        assertEquals(spans.get(0).get("parentId"), spans.get(1).get("spanId"));
        assertEquals(0, spans.get(1).get("parentId"));
        assertEquals("camel-platform-http", spans.get(1).get(Tags.COMPONENT.getKey()));
    }

    @Test
    public void testTracedCamelRouteInvokedFromJaxRsService() {
        RestAssured.get("/opentracing/trace")
                .then()
                .statusCode(200)
                .body(equalTo("Traced direct:start"));

        // Verify the span hierarchy is JAX-RS Service -> Platform HTTP Endpoint -> TracedBean
        List<Map<String, String>> spans = getSpans();
        assertEquals(3, spans.size());
        assertEquals(spans.get(0).get("parentId"), spans.get(1).get("spanId"));
        assertEquals(spans.get(1).get("parentId"), spans.get(2).get("spanId"));
        assertEquals(0, spans.get(2).get("parentId"));
        assertEquals("jaxrs", spans.get(2).get(Tags.COMPONENT.getKey()));
    }

    private List<Map<String, String>> getSpans() {
        return RestAssured.given()
                .get("/opentracing/spans")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .get();
    }
}
