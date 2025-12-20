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
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.codelibs.fess.it.ITBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;

/**
 * Integration tests for the Health API (/api/v1/health)
 */
@Tag("it")
public class HealthApiTests extends ITBase {

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
    public void testHealthCheck_ok() {
        given().contentType("application/json")
                .when()
                .get("/api/v1/health")
                .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("data.status", anyOf(equalTo("green"), equalTo("yellow"), equalTo("red")));
    }

    @Test
    public void testHealthCheck_withoutContentType() {
        given().when()
                .get("/api/v1/health")
                .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("data.status", anyOf(equalTo("green"), equalTo("yellow"), equalTo("red")));
    }
}
