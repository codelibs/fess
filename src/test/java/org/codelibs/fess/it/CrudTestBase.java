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
package org.codelibs.fess.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public abstract class CrudTestBase extends ITBase {

    protected static final int NUM = 20;

    // ================
    // Abstract Methods
    // ================
    abstract protected String getNamePrefix();

    abstract protected String getApiPath();

    abstract protected String getKeyProperty();

    abstract protected String getListEndpointSuffix();

    abstract protected String getItemEndpointSuffix();

    abstract protected Map<String, Object> createTestParam(int id);

    abstract protected Map<String, Object> getUpdateMap();

    // ================

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
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", NUM * 10);
        List<String> idList = getPropList(searchBody, "id");
        idList.forEach(id -> {
            checkDeleteMethod(getItemEndpointSuffix() + "/" + id);
        });
    }

    @AfterAll
    static void tearDownAll() {
        deleteTestToken();
    }

    // ================
    // Bodies
    // ================
    protected void testCreate() {
        // Test: create setting api.
        for (int i = 0; i < NUM; i++) {
            final Map<String, Object> requestBody = createTestParam(i);
            checkPutMethod(requestBody, getItemEndpointSuffix()).then().body("response.created", equalTo(true))
                    .body("response.status", equalTo(0));
        }

        // Test: number of settings.
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", NUM * 2);
        checkGetMethod(searchBody, getListEndpointSuffix()).then().body(getJsonPath() + ".size()", equalTo(NUM));
    }

    protected void testRead() {
        // Test: get settings api.
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", NUM * 2);
        List<String> nameList = getPropList(searchBody, getKeyProperty());

        assertEquals(NUM, nameList.size());
        for (int i = 0; i < NUM; i++) {
            final String name = getNamePrefix() + i;
            assertTrue(nameList.contains(name), name);
        }

        List<String> idList = getPropList(searchBody, "id");
        idList.forEach(id -> {
            // Test: get setting api
            checkGetMethod(searchBody, getItemEndpointSuffix() + "/" + id).then()
                    .body("response." + getItemEndpointSuffix() + ".id", equalTo(id))
                    .body("response." + getItemEndpointSuffix() + "." + getKeyProperty(), startsWith(getNamePrefix()));
        });

        // Test: paging
        searchBody.put("size", 1);
        for (int i = 0; i < NUM; i++) {
            searchBody.put("page", i + 1);
            checkGetMethod(searchBody, getListEndpointSuffix()).then().body("response." + getListEndpointSuffix() + ".size()", equalTo(1));
        }

    }

    protected void testUpdate() {
        // Test: update settings api
        final Set<String> keySet = createTestParam(0).keySet();
        final Map<String, Object> updateMap = getUpdateMap();
        Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", NUM * 2);
        List<Map<String, Object>> settings = getItemList(searchBody);

        for (Map<String, Object> setting : settings) {
            final Map<String, Object> requestBody = new HashMap<>(updateMap);
            requestBody.put("version_no", 1);
            if (setting.containsKey("id")) {
                requestBody.put("id", setting.get("id"));
            }
            for (String key : keySet) {
                if (!requestBody.containsKey(key)) {
                    requestBody.put(key, setting.get(key));
                }
            }

            checkPostMethod(requestBody, getItemEndpointSuffix()).then().body("response.status", equalTo(0));
        }

        checkUpdate();
    }

    protected void checkUpdate() {
        final Map<String, Object> updateMap = getUpdateMap();
        Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", NUM * 2);
        for (Map.Entry<String, Object> entry : updateMap.entrySet()) {
            List<String> updatedList = getPropList(searchBody, entry.getKey());
            for (String val : updatedList) {
                assertEquals(val, entry.getValue().toString());
            }
        }
    }

    protected void testDelete() {
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", NUM * 2);
        List<String> idList = getPropList(searchBody, "id");

        idList.forEach(id -> {
            //Test: delete setting api
            checkDeleteMethod(getItemEndpointSuffix() + "/" + id).then().body("response.status", equalTo(0));
        });

        // Test: NUMber of settings.
        checkGetMethod(searchBody, getListEndpointSuffix()).then().body(getJsonPath() + ".size()", equalTo(0));
    }

    // ================
    // Utilities
    // ================
    protected Response checkGetMethod(final Map<String, Object> body, final String path) {
        return checkMethodBase(body).get(getApiPath() + "/" + path);
    }

    protected Response checkPutMethod(final Map<String, Object> body, final String path) {
        return checkMethodBase(body).put(getApiPath() + "/" + path);
    }

    protected Response checkPostMethod(final Map<String, Object> body, final String path) {
        return checkMethodBase(body).post(getApiPath() + "/" + path);
    }

    protected Response checkDeleteMethod(final String path) {
        return given().header("Authorization", getTestToken()).delete(getApiPath() + "/" + path);
    }

    protected List<Map<String, Object>> getItemList(final Map<String, Object> body) {
        String response = checkGetMethod(body, getListEndpointSuffix()).asString();
        return JsonPath.from(response).getList(getJsonPath());
    }

    protected List<String> getPropList(final Map<String, Object> body, final String prop) {
        String response = checkGetMethod(body, getListEndpointSuffix()).asString();
        return JsonPath.from(response).getList(getJsonPath() + "." + prop);
    }

    protected String getJsonPath() {
        return "response." + getListEndpointSuffix() + ".findAll {it." + getKeyProperty() + ".startsWith(\"" + getNamePrefix() + "\")}";
    }

    protected RequestSpecification checkMethodBase(final Map<String, Object> body) {
        return given().header("Authorization", getTestToken()).body(body, ObjectMapperType.JACKSON_2).when();
    }

}
