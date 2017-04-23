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

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.path.json.JsonPath;
import org.codelibs.fess.it.ITBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag("it")
public class AccessTokenTests extends ITBase {
    private static final String NAME_PREFIX = "accessTokenTest_";
    private static final int NUM = 20;

    @BeforeAll
    static void initAll() {
        RestAssured.baseURI = getFessUrl();
        settingTestToken();
        clearTestData(NUM);
    }

    @BeforeEach
    void init() {
    }

    @AfterEach
    void tearDown() {
    }

    @AfterAll
    static void tearDownAll() {
        clearTestData(NUM);
        deleteTestToken();
    }

    @Test
    void curdTest() {
        testCreate(NUM);
        testRead(NUM);
        testUpdate(NUM);
        testDelete(NUM);
    }

    private void testCreate(int num) {
        for (int i = 0; i < num; i++) {
            final String name = NAME_PREFIX + i;
            final Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("name", name);
            requestBody.put("permissions", "Radmin-api");
            given().header("Authorization", getTestToken()).body(requestBody, ObjectMapperType.JACKSON_2).when()
                    .put("/api/admin/accesstoken/setting").then().body("response.created", equalTo(true))
                    .body("response.status", equalTo(0));
        }

        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", num * 2);
        given().header("Authorization", getTestToken()).body(searchBody, ObjectMapperType.JACKSON_2).when()
                .get("/api/admin/accesstoken/settings").then()
                .body("response.settings.findAll {it.name.startsWith(\"" + NAME_PREFIX + "\")}.size()", equalTo(num));
    }

    private void testRead(int num) {
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", num * 2);
        String response =
                given().header("Authorization", getTestToken()).body(searchBody, ObjectMapperType.JACKSON_2).when()
                        .get("/api/admin/accesstoken/settings").asString();
        List<String> nameList = JsonPath.from(response).getList("response.settings.name");
        assertEquals(num + 1, nameList.size());

        for (int i = 0; i < num; i++) {
            final String name = NAME_PREFIX + i;
            assertTrue(nameList.contains(name), name);
        }

        searchBody.put("size", 1);
        for (int i = 0; i < num + 1; i++) {
            searchBody.put("page", i + 1);
            given().header("Authorization", getTestToken()).body(searchBody, ObjectMapperType.JACKSON_2).when()
                    .get("/api/admin/accesstoken/settings").then().body("response.settings.size()", equalTo(1));
        }
    }

    private void testUpdate(int num) {
        Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", num * 2);
        String response =
                given().header("Authorization", getTestToken()).body(searchBody, ObjectMapperType.JACKSON_2).when()
                        .get("/api/admin/accesstoken/settings").asString();
        List<Map<String, Object>> settings =
                JsonPath.from(response).getList("response.settings.findAll {it.name.startsWith(\"" + NAME_PREFIX + "\")}");

        String newPermission = "Radmin-api2";
        for (Map<String, Object> setting : settings) {
            final Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("id", setting.get("id"));
            requestBody.put("name", setting.get("name"));
            requestBody.put("permissions", newPermission);
            requestBody.put("version_no", 1);
            given().header("Authorization", getTestToken()).body(requestBody, ObjectMapperType.JACKSON_2).when()
                    .post("/api/admin/accesstoken/setting").then().body("response.status", equalTo(0));
        }

        searchBody = new HashMap<>();
        searchBody.put("size", num * 2);
        response =
                given().header("Authorization", getTestToken()).body(searchBody, ObjectMapperType.JACKSON_2).when()
                        .get("/api/admin/accesstoken/settings").asString();
        List<String> permissionsList =
                JsonPath.from(response).getList("response.settings.findAll {it.name.startsWith(\"" + NAME_PREFIX + "\")}.permissions");
        for (String permissions : permissionsList) {
            assertEquals(newPermission.replace("R", "{role}"), permissions);
        }
    }

    private void testDelete(int num) {
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", num * 2);
        String response =
                given().header("Authorization", getTestToken()).body(searchBody, ObjectMapperType.JACKSON_2).when()
                        .get("/api/admin/accesstoken/settings").asString();
        List<String> idList = JsonPath.from(response).getList("response.settings.findAll {it.name.startsWith(\"" + NAME_PREFIX + "\")}.id");
        idList.forEach(id -> {
            given().header("Authorization", getTestToken()).delete("/api/admin/accesstoken/setting/" + id).then()
                    .body("response.status", equalTo(0));
        });

        given().header("Authorization", getTestToken()).body(searchBody, ObjectMapperType.JACKSON_2).when()
                .get("/api/admin/accesstoken/settings").then()
                .body("response.settings.findAll {it.name.startsWith(\"" + NAME_PREFIX + "\")}.size()", equalTo(0));
    }

    private static void clearTestData(int num) {
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", num * 10);
        String response =
                given().header("Authorization", getTestToken()).body(searchBody, ObjectMapperType.JACKSON_2).when()
                        .get("/api/admin/accesstoken/settings").asString();
        List<String> idList = JsonPath.from(response).getList("response.settings.findAll {it.name.startsWith(\"" + NAME_PREFIX + "\")}.id");
        idList.forEach(id -> {
            given().header("Authorization", getTestToken()).delete("/api/admin/accesstoken/setting/" + id);
        });
    }
}
