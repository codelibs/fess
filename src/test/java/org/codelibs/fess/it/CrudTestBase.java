/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public abstract class CrudTestBase extends ITBase {

    protected static final int NUM = 20;
    protected static final int SEARCH_ALL_NUM = 1000;

    private static final Logger logger = LogManager.getLogger(CrudTestBase.class);

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
    // Constant
    // ================
    protected String getIdKey() {
        return "id";
    }

    @BeforeAll
    protected static void initAll() {
        RestAssured.baseURI = getFessUrl();
        settingTestToken();
    }

    @BeforeEach
    protected void init() {
    }

    @AfterEach
    protected void tearDown() {
        final Map<String, Object> searchBody = createSearchBody(SEARCH_ALL_NUM);
        int count = 0;
        List<String> idList = getIdList(searchBody);
        while (idList.size() > 0 && count < NUM) {
            final String id = idList.get(0);
            checkDeleteMethod(getItemEndpointSuffix() + "/" + id.toString());
            refresh();
            idList = getIdList(searchBody);
            count += 1;
        }
    }

    @AfterAll
    protected static void tearDownAll() {
        deleteTestToken();
    }

    // ================
    // Bodies
    // ================
    protected void testCreate() {
        // Test: create setting api.
        for (int i = 0; i < NUM; i++) {
            final Map<String, Object> requestBody = createTestParam(i);
            checkPutMethod(requestBody, getItemEndpointSuffix()).then().body("response.created", equalTo(true)).body("response.status",
                    equalTo(0));

            //logger.info("create {}{}", i, checkPutMethod(requestBody, getItemEndpointSuffix()).asString()); // for debugging
            refresh();
        }

        // Test: number of settings.
        final Map<String, Object> searchBody = createSearchBody(SEARCH_ALL_NUM);
        checkGetMethod(searchBody, getListEndpointSuffix()).then().body(getJsonPath() + ".size()", equalTo(NUM));
    }

    protected void testRead() {
        // Test: get settings api.
        final Map<String, Object> searchBody = createSearchBody(SEARCH_ALL_NUM);
        List<String> nameList = getPropList(searchBody, getKeyProperty());

        assertEquals(NUM, nameList.size());
        for (int i = 0; i < NUM; i++) {
            final String name = getNamePrefix() + i;
            assertTrue(nameList.contains(name), name);
        }

        List<String> idList = getIdList(searchBody);
        idList.forEach(id -> {
            // Test: get setting api
            checkGetMethod(searchBody, getItemEndpointSuffix() + "/" + id).then()
                    .body("response." + getItemEndpointSuffix() + "." + getIdKey(), equalTo(id))
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
        final Map<String, Object> searchBody = createSearchBody(SEARCH_ALL_NUM);
        List<Map<String, Object>> settings = getItemList(searchBody);

        for (Map<String, Object> setting : settings) {
            final Map<String, Object> requestBody = new HashMap<>(updateMap);
            requestBody.put("version_no", 1);
            final String idKey = getIdKey();
            if (setting.containsKey(idKey)) {
                requestBody.put(idKey, setting.get(idKey));
            }
            for (String key : keySet) {
                if (!requestBody.containsKey(key)) {
                    requestBody.put(key, setting.get(key));
                }
            }

            checkPostMethod(requestBody, getItemEndpointSuffix()).then().body("response.status", equalTo(0));
            refresh();
        }

        checkUpdate();
    }

    protected void checkUpdate() {
        final Map<String, Object> updateMap = getUpdateMap();
        Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", SEARCH_ALL_NUM);
        for (Map.Entry<String, Object> entry : updateMap.entrySet()) {
            List<String> updatedList = getPropList(searchBody, entry.getKey());
            for (String val : updatedList) {
                assertEquals(entry.getValue().toString(), val);
            }
        }
    }

    protected void testDelete() {
        final Map<String, Object> searchBody = createSearchBody(SEARCH_ALL_NUM);

        for (int count = 0; count < NUM; count++) {
            final String id = getIdList(searchBody).get(0);
            checkDeleteMethod(getItemEndpointSuffix() + "/" + id.toString()).then().body("response.status", equalTo(0));
            refresh();
        }

        // Test: number of settings.
        checkGetMethod(searchBody, getListEndpointSuffix()).then().body(getJsonPath() + ".size()", equalTo(0));
    }

    // ================
    // Utilities
    // ================
    protected Response checkGetMethod(final Map<String, Object> body, final String path) {
        Response response = checkMethodBase(body).get(getApiPath() + "/" + path);
        // logger.debug(response.asString());
        return response;
    }

    protected Response checkPutMethod(final Map<String, Object> body, final String path) {
        Response response = checkMethodBase(body).put(getApiPath() + "/" + path);
        // logger.debug(response.asString());
        return response;
    }

    protected Response checkPostMethod(final Map<String, Object> body, final String path) {
        Response response = checkMethodBase(body).post(getApiPath() + "/" + path);
        // logger.debug(response.asString());
        return response;
    }

    protected Response checkDeleteMethod(final String path) {
        return given().contentType("application/json").header("Authorization", getTestToken()).delete(getApiPath() + "/" + path);
    }

    protected List<Map<String, Object>> getItemList(final Map<String, Object> body) {
        String response = checkGetMethod(body, getListEndpointSuffix()).asString();
        return JsonPath.from(response).getList(getJsonPath());
    }

    protected List<String> getIdList(final Map<String, Object> body) {
        return getPropList(body, getIdKey());
    }

    protected List<String> getPropList(final Map<String, Object> body, final String prop) {
        String response = checkGetMethod(body, getListEndpointSuffix()).asString();
        return JsonPath.from(response).getList(getJsonPath() + "." + prop);
    }

    protected String getJsonPath() {
        return "response." + getListEndpointSuffix() + ".findAll {it." + getKeyProperty() + ".startsWith(\"" + getNamePrefix() + "\")}";
    }

    protected Map<String, Object> createSearchBody(final int size) {
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", size);
        return searchBody;
    }
}
