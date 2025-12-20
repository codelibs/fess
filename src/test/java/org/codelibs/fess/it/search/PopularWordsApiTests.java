/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.it.search;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.it.ITBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

/**
 * Integration tests for the Popular Words API (/api/v1/popular-words)
 */
@Tag("it")
public class PopularWordsApiTests extends ITBase {

    @BeforeAll
    protected static void initAll() {
        RestAssured.baseURI = getFessUrl();
        settingTestToken();
    }

    @AfterAll
    protected static void tearDownAll() {
        deleteTestToken();
    }

    @Test
    public void testPopularWords_ok() {
        given().contentType("application/json")
                .when()
                .get("/api/v1/popular-words")
                .then()
                .statusCode(200)
                .body("record_count", greaterThanOrEqualTo(0))
                .body("data", notNullValue());
    }

    @Test
    public void testPopularWords_withSeed() {
        Map<String, String> params = new HashMap<>();
        params.put("seed", "12345");

        given().contentType("application/json")
                .params(params)
                .when()
                .get("/api/v1/popular-words")
                .then()
                .statusCode(200)
                .body("record_count", greaterThanOrEqualTo(0))
                .body("data", notNullValue());
    }

    @Test
    public void testPopularWords_withLabel() {
        Map<String, String> params = new HashMap<>();
        params.put("label", "testlabel");

        given().contentType("application/json")
                .params(params)
                .when()
                .get("/api/v1/popular-words")
                .then()
                .statusCode(200)
                .body("record_count", greaterThanOrEqualTo(0))
                .body("data", notNullValue());
    }

    @Test
    public void testPopularWords_withField() {
        Map<String, String> params = new HashMap<>();
        params.put("field", "content");

        given().contentType("application/json")
                .params(params)
                .when()
                .get("/api/v1/popular-words")
                .then()
                .statusCode(200)
                .body("record_count", greaterThanOrEqualTo(0))
                .body("data", notNullValue());
    }

    @Test
    public void testPopularWords_responseStructure() {
        String response = given().contentType("application/json").when().get("/api/v1/popular-words").asString();

        JsonPath jsonPath = JsonPath.from(response);
        assertTrue(jsonPath.getInt("record_count") >= 0);
        // data is an array of strings
        assertTrue(jsonPath.getList("data") != null);
    }
}
