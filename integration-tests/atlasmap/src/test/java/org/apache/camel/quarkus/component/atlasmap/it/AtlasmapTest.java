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
package org.apache.camel.quarkus.component.atlasmap.it;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.camel.quarkus.component.atlasmap.it.model.Person;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestHTTPEndpoint(AtlasmapResource.class)
class AtlasmapTest {

    @Test
    void testJavaToJson() {
        Date birthDay = new GregorianCalendar(1985, Calendar.APRIL, 13).getTime();
        Person person = new Person("foo", "bar", 35, birthDay);
        given()
                .contentType(ContentType.JSON)
                .body(person)
                .when()
                .get("/json/java2json")
                .then()
                .body("name2", equalTo("bar"))
                .body("age", equalTo(35))
                .body("$", hasKey("birthDate"));
    }

    @Test
    void testJsonToJava() {
        String person = "{\"name1\":\"foo\", \"name2\":\"bar\", \"age\":35, \"birthDate\":\"2012-04-23T18:25:43.511Z\"}";
        given()
                .contentType(ContentType.JSON)
                .body(person)
                .when()
                .get("/json/json2java")
                .then()
                .body("firstName", equalTo("foo"))
                .body("lastName", equalTo("bar"))
                .body("age", equalTo(35))
                .body("$", hasKey("dateOfBirth"));
    }

    @Test
    void testXml2Xml() {
        String request = "<tns:Patient xmlns:tns=\"http://hl7.org/fhir\"><tns:id value=\"101138\"></tns:id></tns:Patient>";
        String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><tns:Person xmlns:tns=\"http://hl7.org/fhir\"><tns:id value=\"101138\"/></tns:Person>";
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get("/json/xml2xml")
                .then()
                .body(equalTo(expectedResponse));
    }

    @Test
    void testJson2Xml() {
        String request = "{\"id\":\"101138\"}";
        String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><tns:Person xmlns:tns=\"http://hl7.org/fhir\"><tns:id value=\"101138\"/></tns:Person>";
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get("/json/json2xml")
                .then()
                .body(equalTo(expectedResponse));
    }

    @Test
    void testXml2Json() {
        String request = "<tns:Patient xmlns:tns=\"http://hl7.org/fhir\"><tns:id value=\"101138\"></tns:id></tns:Patient>";
        String expectedResponse = "{\"id\":\"101138\"}";
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get("/json/xml2json")
                .then()
                .body(equalTo(expectedResponse));
    }

    @Test
    void testJava2Xml() {
        Person request = new Person("foo", "bar", 35, null);
        String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><tns:Person xmlns:tns=\"http://hl7.org/fhir\"><tns:firstName value=\"foo\"/><tns:lastName value=\"bar\"/><tns:age value=\"35\"/></tns:Person>";
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get("/json/java2xml")
                .then()
                .body(equalTo(expectedResponse));
    }

    @Test
    void testXml2Java() {
        String request = "<tns:Person xmlns:tns=\"http://hl7.org/fhir\"><tns:firstName value=\"foo\"/><tns:lastName value=\"bar\"/><tns:age value=\"35\"/></tns:Person>";
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get("/json/xml2java")
                .then()
                .body("firstName", equalTo("foo"))
                .body("lastName", equalTo("bar"))
                .body("age", equalTo(35));
    }

    @Test
    void testXml2XmlWithAdm() {
        String request = "<tns:Patient xmlns:tns=\"http://hl7.org/fhir\"><tns:id>101138</tns:id></tns:Patient>";
        String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><tns:Person xmlns:tns=\"http://hl7.org/fhir\"><tns:id>101138</tns:id></tns:Person>";
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get("/adm/xml2xml")
                .then()
                .body(equalTo(expectedResponse));
    }

    @Test
    void testJson2JsonWithAdm() {
        String request = "{\"name1\":\"foo\", \"name2\":\"bar\", \"age\":35}";
        String expectedResponse = "{\"age\":35,\"firstName\":\"foo\",\"lastName\":\"bar\"}";

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get("/adm/json2json")
                .then()
                .body(equalTo(expectedResponse));
    }

    @Test
    void testXml2JsonWithAdm() {
        String request = "<tns:Patient xmlns:tns=\"http://hl7.org/fhir\"><tns:id>101138</tns:id></tns:Patient>";
        String expectedResponse = "{\"id\":\"101138\"}";
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get("/adm/xml2json")
                .then()
                .body(equalTo(expectedResponse));
    }

    @Test
    void testJson2XmlWithAdm() {
        String request = "{\"id\":\"101138\"}";
        String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><tns:Patient xmlns:tns=\"http://hl7.org/fhir\"><tns:id>101138</tns:id></tns:Patient>";
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get("/adm/json2xml")
                .then()
                .body(equalTo(expectedResponse));
    }

    @Test
    void testJsonToCsv() {
        String person = "{\"name1\":\"foo\", \"name2\":\"bar\", \"age\":35}";
        String experctedResult = "firstName,lastName,age\r\n" +
                "foo,bar,35\r\n";
        String result = given()
                .contentType(ContentType.JSON)
                .body(person)
                .when()
                .get("/json/json2csv")
                .then()
                .extract()
                .asString();
        assertEquals(experctedResult, result);
    }

    @Test
    void testCsvToJsonWithAdm() {
        String request = "foo,bar,35\r\n";
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get("/adm/csv2json")
                .then()
                .body(equalTo("{\"firstName\":\"foo\",\"lastName\":\"bar\",\"age\":35}"));
    }

    @Test
    void testCsvToJsonWithJson() {
        String request = "firstName,lastName,age\r\n" +
                "foo,bar,35\r\n";
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get("/json/csv2json")
                .then()
                .body(equalTo("{\"firstName\":\"foo\",\"lastName\":\"bar\",\"age\":\"35\"}"));
    }

    @Test
    void testCsvToJavaWithJson() {
        String request = "firstName,lastName,age\r\n" +
                "foo,bar,35\r\n";
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get("/json/csv2java")
                .then()
                .body("firstName", equalTo("foo"))
                .body("lastName", equalTo("bar"))
                .body("age", equalTo(35));
    }

    @Test
    void testCsvToXmlWithJson() {
        String request = "firstName,lastName,age\r\n" +
                "foo,bar,35\r\n";
        String expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><tns:Person xmlns:tns=\"http://hl7.org/fhir\"><tns:firstName value=\"foo\"/><tns:lastName value=\"bar\"/><tns:age value=\"35\"/></tns:Person>";
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get("/json/csv2xml")
                .then()
                .body(equalTo(expectedResult));
    }

    @Test
    void testXmlToCsvWithJson() {
        String request = "<tns:Person xmlns:tns=\"http://hl7.org/fhir\"><tns:firstName value=\"foo\"/><tns:lastName value=\"bar\"/><tns:age value=\"35\"/></tns:Person>";
        String expectedResult = "firstName,lastName,age\r\n" +
                "foo,bar,35\r\n";
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get("/json/xml2csv")
                .then()
                .body(equalTo(expectedResult));
    }

    @Test
    void testJavaToCsvWithJson() {
        String expectedResult = "firstName,lastName,age\r\n" +
                "foo,bar,35\r\n";
        Person person = new Person("foo", "bar", 35, null);
        given()
                .contentType(ContentType.JSON)
                .body(person)
                .when()
                .get("/json/java2csv")
                .then()
                .body(equalTo(expectedResult));
    }

}
