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
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.it.ITBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

/**
 * Integration tests for the Suggest Words API (/api/v1/suggest-words)
 */
@Tag("it")
public class SuggestWordsApiTests extends ITBase {

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
    public void testSuggestWords_ok() {
        Map<String, String> params = new HashMap<>();
        params.put("q", "test");

        given().contentType("application/json")
                .header("Referer", getFessUrl())
                .params(params)
                .when()
                .get("/api/v1/suggest-words")
                .then()
                .statusCode(200)
                .body("record_count", greaterThanOrEqualTo(0));
    }

    @Test
    public void testSuggestWords_withNum() {
        Map<String, String> params = new HashMap<>();
        params.put("q", "a");
        params.put("num", "5");

        String response = given().contentType("application/json")
                .header("Referer", getFessUrl())
                .params(params)
                .when()
                .get("/api/v1/suggest-words")
                .asString();

        JsonPath jsonPath = JsonPath.from(response);
        assertTrue(jsonPath.getInt("record_count") >= 0);
        List<Object> data = jsonPath.getList("data");
        if (data != null) {
            assertTrue(data.size() <= 5, "Number of results should be at most 5");
        }
    }

    @Test
    public void testSuggestWords_withLabel() {
        Map<String, String> params = new HashMap<>();
        params.put("q", "test");
        params.put("label", "testlabel");

        given().contentType("application/json")
                .header("Referer", getFessUrl())
                .params(params)
                .when()
                .get("/api/v1/suggest-words")
                .then()
                .statusCode(200)
                .body("record_count", greaterThanOrEqualTo(0));
    }

    @Test
    public void testSuggestWords_withField() {
        Map<String, String> params = new HashMap<>();
        params.put("q", "test");
        params.put("field", "content");

        given().contentType("application/json")
                .header("Referer", getFessUrl())
                .params(params)
                .when()
                .get("/api/v1/suggest-words")
                .then()
                .statusCode(200)
                .body("record_count", greaterThanOrEqualTo(0));
    }

    @Test
    public void testSuggestWords_withLang() {
        Map<String, String> params = new HashMap<>();
        params.put("q", "test");
        params.put("lang", "en");

        given().contentType("application/json")
                .header("Referer", getFessUrl())
                .params(params)
                .when()
                .get("/api/v1/suggest-words")
                .then()
                .statusCode(200)
                .body("record_count", greaterThanOrEqualTo(0));
    }

    @Test
    public void testSuggestWords_noMatch() {
        Map<String, String> params = new HashMap<>();
        params.put("q", "zzzzxxxxyyyywwww");

        given().contentType("application/json")
                .header("Referer", getFessUrl())
                .params(params)
                .when()
                .get("/api/v1/suggest-words")
                .then()
                .statusCode(200)
                .body("record_count", greaterThanOrEqualTo(0));
    }

    @Test
    public void testSuggestWords_responseStructure() {
        Map<String, String> params = new HashMap<>();
        params.put("q", "test");

        String response = given().contentType("application/json")
                .header("Referer", getFessUrl())
                .params(params)
                .when()
                .get("/api/v1/suggest-words")
                .asString();

        JsonPath jsonPath = JsonPath.from(response);
        assertTrue(jsonPath.getInt("record_count") >= 0);
        assertTrue(jsonPath.getInt("page_size") >= 0);
        assertTrue(jsonPath.getLong("query_time") >= 0);
    }

    @Test
    public void testSuggestWords_pageSizeDefault() {
        Map<String, String> params = new HashMap<>();
        params.put("q", "a");

        given().contentType("application/json")
                .header("Referer", getFessUrl())
                .params(params)
                .when()
                .get("/api/v1/suggest-words")
                .then()
                .statusCode(200)
                .body("page_size", lessThanOrEqualTo(10));
    }
}
