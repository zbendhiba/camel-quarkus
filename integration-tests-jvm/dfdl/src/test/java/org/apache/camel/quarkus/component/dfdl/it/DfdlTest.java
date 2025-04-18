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
package org.apache.camel.quarkus.component.dfdl.it;

import java.nio.charset.StandardCharsets;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xmlunit.builder.DiffBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@QuarkusTest
class DfdlTest {
    private static String EDI_STRING;
    private static String XML_STRING;

    @BeforeAll
    public static void beforeAll() throws Exception {
        var ediStream = DfdlTest.class.getResourceAsStream("/X12-837P-message.edi.txt");
        EDI_STRING = IOUtils.toString(ediStream, StandardCharsets.UTF_8);
        var xmlStream = DfdlTest.class.getResourceAsStream("/X12-837P-message.xml");
        XML_STRING = IOUtils.toString(xmlStream, StandardCharsets.UTF_8);
    }

    @Test
    public void loadComponentDfdl() {
        /* A simple autogenerated test */
        RestAssured.get("/dfdl/load/component/dfdl")
                .then()
                .statusCode(200);
    }

    @Test
    public void testParse() {
        var response = RestAssured.given().body(EDI_STRING).post("/dfdl/parse").asString();
        assertFalse(DiffBuilder.compare(XML_STRING).withTest(response).ignoreComments().ignoreWhitespace().build()
                .hasDifferences());
    }

    @Test
    public void testUnparse() {
        var response = RestAssured.given().body(XML_STRING).post("/dfdl/unparse").asString();
        var testArray = response.split("\\r?\\n|\\r");
        var compArray = EDI_STRING.split("\\r?\\n|\\r");
        assertEquals(compArray.length, testArray.length);
        for (int i = 0; i < testArray.length; i++) {
            assertEquals(compArray[i], testArray[i], "Line " + i);
        }
    }

    @Test
    public void loadDataformatDfdl() {
        /* A simple autogenerated test */
        RestAssured.get("/dfdl/load/dataformat/dfdl")
                .then()
                .statusCode(200);
    }

    @Test
    public void testMarshal() {
        var response = RestAssured.given().body(XML_STRING).post("/dfdl/marshal").asString();
        var testArray = response.split("\\r?\\n|\\r");
        var compArray = EDI_STRING.split("\\r?\\n|\\r");
        assertEquals(compArray.length, testArray.length);
        for (int i = 0; i < testArray.length; i++) {
            assertEquals(compArray[i], testArray[i], "Line " + i);
        }
    }

    @Test
    public void testUnmarshal() {
        var response = RestAssured.given().body(EDI_STRING).post("/dfdl/unmarshal").asString();
        assertFalse(DiffBuilder.compare(XML_STRING).withTest(response).ignoreComments().ignoreWhitespace().build()
                .hasDifferences());
    }

}
