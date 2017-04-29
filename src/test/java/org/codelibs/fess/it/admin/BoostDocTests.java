/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.it.admin;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.it.ITBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.path.json.JsonPath;

@Tag("it")
public class BoostDocTests extends ITBase {
    private static final String NAME_PREFIX = "boostDocTest_";
    private static final int NUM = 20;

    @BeforeAll
    static void initAll() {
        RestAssured.baseURI = getFessUrl();
        settingTestToken();
    }

    @BeforeEach
    void init() {
    }

    @AfterEach
    void tearDown() {
        clearTestData(NUM);
    }

    @AfterAll
    static void tearDownAll() {
        deleteTestToken();
    }

    @Test
    void crudTest() {
        testCreate(NUM);
        testRead(NUM);
        testUpdate(NUM);
        testDelete(NUM);
    }

    @Test
    void functionTest() {
        // TODO
    }

    private void testCreate(int num) {
        // Test: create setting api.
        for (int i = 0; i < num; i++) {
            final String url_expr = NAME_PREFIX + i;
            final Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("url_expr", url_expr);
            requestBody.put("boost_expr", new Integer(i).toString());
            requestBody.put("sort_order", i);
            requestBody.put("created_time", 0); // Dummy
            requestBody.put("created_by", "IntegrationTest");
            given().header("Authorization", getTestToken()).body(requestBody, ObjectMapperType.JACKSON_2).when()
                    .put("/api/admin/boostdoc/setting").then().body("response.created", equalTo(true)).body("response.status", equalTo(0));
        }

        // Test: number of settings.
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", num * 2);
        given().header("Authorization", getTestToken()).body(searchBody, ObjectMapperType.JACKSON_2).when()
                .get("/api/admin/boostdoc/settings").then()
                .body("response.settings.findAll {it.url_expr.startsWith(\"" + NAME_PREFIX + "\")}.size()", equalTo(num));
    }

    private void testRead(int num) {
        // Test: get settings api.
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", num * 2);
        String response =
                given().header("Authorization", getTestToken()).body(searchBody, ObjectMapperType.JACKSON_2).when()
                        .get("/api/admin/boostdoc/settings").asString();
        List<String> nameList =
                JsonPath.from(response).getList("response.settings.findAll {it.url_expr.startsWith(\"" + NAME_PREFIX + "\")}.url_expr");
        assertEquals(num, nameList.size());
        for (int i = 0; i < num; i++) {
            final String name = NAME_PREFIX + i;
            assertTrue(nameList.contains(name), name);
        }

        response =
                given().header("Authorization", getTestToken()).body(searchBody, ObjectMapperType.JACKSON_2).when()
                        .get("/api/admin/boostdoc/settings").asString();
        List<String> idList =
                JsonPath.from(response).getList("response.settings.findAll {it.url_expr.startsWith(\"" + NAME_PREFIX + "\")}.id");
        idList.forEach(id -> {
            // Test: get setting api
            given().header("Authorization", getTestToken()).get("/api/admin/boostdoc/setting/" + id).then()
                    .body("response.setting.id", equalTo(id)).body("response.setting.url_expr", startsWith(NAME_PREFIX));
        });

        // Test: paging
        searchBody.put("size", 1);
        for (int i = 0; i < num; i++) {
            searchBody.put("page", i + 1);
            given().header("Authorization", getTestToken()).body(searchBody, ObjectMapperType.JACKSON_2).when()
                    .get("/api/admin/boostdoc/settings").then().body("response.settings.size()", equalTo(1));
        }

    }

    private void testUpdate(int num) {
        // Test: update settings api
        Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", num * 2);
        String response =
                given().header("Authorization", getTestToken()).body(searchBody, ObjectMapperType.JACKSON_2).when()
                        .get("/api/admin/boostdoc/settings").asString();
        List<Map<String, Object>> settings =
                JsonPath.from(response).getList("response.settings.findAll {it.url_expr.startsWith(\"" + NAME_PREFIX + "\")}");

        final String newBoostExpr = "new_boost_expr";
        for (Map<String, Object> setting : settings) {
            final Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("id", setting.get("id"));
            requestBody.put("url_expr", setting.get("url_expr"));
            requestBody.put("boost_expr", newBoostExpr);
            requestBody.put("sort_order", setting.get("sort_order"));
            requestBody.put("created_by", setting.get("created_by"));
            requestBody.put("created_time", setting.get("created_time"));
            requestBody.put("version_no", 1);
            given().header("Authorization", getTestToken()).body(requestBody, ObjectMapperType.JACKSON_2).when()
                    .post("/api/admin/boostdoc/setting").then().body("response.status", equalTo(0));
        }

        searchBody = new HashMap<>();
        searchBody.put("size", num * 2);
        response =
                given().header("Authorization", getTestToken()).body(searchBody, ObjectMapperType.JACKSON_2).when()
                        .get("/api/admin/boostdoc/settings").asString();
        List<String> boostExprList =
                JsonPath.from(response).getList("response.settings.findAll {it.url_expr.startsWith(\"" + NAME_PREFIX + "\")}.boost_expr");
        for (String boostExpr : boostExprList) {
            assertEquals(boostExpr, newBoostExpr);
        }
    }

    private void testDelete(int num) {
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", num * 2);
        String response =
                given().header("Authorization", getTestToken()).body(searchBody, ObjectMapperType.JACKSON_2).when()
                        .get("/api/admin/boostdoc/settings").asString();
        List<String> idList =
                JsonPath.from(response).getList("response.settings.findAll {it.url_expr.startsWith(\"" + NAME_PREFIX + "\")}.id");
        idList.forEach(id -> {
            //Test: delete setting api
            given().header("Authorization", getTestToken()).delete("/api/admin/boostdoc/setting/" + id).then()
                    .body("response.status", equalTo(0));
        });

        // Test: number of settings.
        given().header("Authorization", getTestToken()).body(searchBody, ObjectMapperType.JACKSON_2).when()
                .get("/api/admin/boostdoc/settings").then()
                .body("response.settings.findAll {it.url_expr.startsWith(\"" + NAME_PREFIX + "\")}.size()", equalTo(0));
    }

    private static void clearTestData(int num) {
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", num * 10);
        String response =
                given().header("Authorization", getTestToken()).body(searchBody, ObjectMapperType.JACKSON_2).when()
                        .get("/api/admin/boostdoc/settings").asString();
        List<String> idList =
                JsonPath.from(response).getList("response.settings.findAll {it.url_expr.startsWith(\"" + NAME_PREFIX + "\")}.id");
        idList.forEach(id -> {
            given().header("Authorization", getTestToken()).delete("/api/admin/boostdoc/setting/" + id);
        });
    }
}
