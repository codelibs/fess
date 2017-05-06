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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.it.CrudTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.restassured.path.json.JsonPath;

@Tag("it")
public class AccessTokenTests extends CrudTestBase {
    private static final int NUM = 20;

    private static final String NAME_PREFIX = "accessTokenTest_";
    private static final String API_PATH = "/api/admin/accesstoken";
    private static final String LIST_ENDPOINT_SUFFIX = "settings";
    private static final String ITEM_ENDPOINT_SUFFIX = "setting";

    private static final String KEY_PROPERTY = "name";

    @Override
    protected String getNamePrefix() {
        return NAME_PREFIX;
    }

    @Override
    protected String getApiPath() {
        return API_PATH;
    }

    @Override
    protected String getKeyProperty() {
        return KEY_PROPERTY;
    }

    @Override
    protected String getListEndpointSuffix() {
        return LIST_ENDPOINT_SUFFIX;
    }

    @Override
    protected String getItemEndpointSuffix() {
        return ITEM_ENDPOINT_SUFFIX;
    }

    @Test
    void crudTest() {
        testCreate();
        testRead();
        testUpdate();
        testDelete();
    }

    @Test
    void functionTest() {
        testPermission();
    }

    @Override
    protected void testCreate() {
        // Test: create setting api.
        for (int i = 0; i < NUM; i++) {
            final String name = NAME_PREFIX + i;
            final Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("name", name);
            requestBody.put("permissions", "Radmin-api");

            checkPutMethod(requestBody, ITEM_ENDPOINT_SUFFIX).then().body("response.created", equalTo(true))
                    .body("response.status", equalTo(0));
        }

        // Test: NUMber of settings.
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", NUM * 2);
        checkGetMethod(searchBody, LIST_ENDPOINT_SUFFIX).then().body(getJsonPath() + ".size()", equalTo(NUM));
    }

    @Override
    protected void testRead() {
        // Test: get settings api.
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", NUM * 2);
        List<String> nameList = getPropList(searchBody, "name");
        assertEquals(NUM, nameList.size());

        for (int i = 0; i < NUM; i++) {
            final String name = NAME_PREFIX + i;
            assertTrue(nameList.contains(name), name);
        }

        List<String> idList = getPropList(searchBody, "id");
        idList.forEach(id -> {
            // Test: get setting api
            checkGetMethod(searchBody, ITEM_ENDPOINT_SUFFIX + "/" + id).then()
                    .body("response." + ITEM_ENDPOINT_SUFFIX + ".id", equalTo(id))
                    .body("response." + ITEM_ENDPOINT_SUFFIX + ".name", startsWith(NAME_PREFIX))
                    .body("response." + ITEM_ENDPOINT_SUFFIX + ".token.length()", greaterThan(0));
        });

        // Test: paging
        searchBody.put("size", 1);
        for (int i = 0; i < NUM + 1; i++) {
            searchBody.put("page", i + 1);
            checkGetMethod(searchBody, LIST_ENDPOINT_SUFFIX).then().body("response." + LIST_ENDPOINT_SUFFIX + ".size()", equalTo(1));
        }
    }

    @Override
    protected void testUpdate() {
        // Test: update settings api
        Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", NUM * 2);
        List<Map<String, Object>> settings = getItemList(searchBody);

        String newPermission = "Radmin-api2";
        for (Map<String, Object> setting : settings) {
            final Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("id", setting.get("id"));
            requestBody.put("name", setting.get("name"));
            requestBody.put("permissions", newPermission);
            requestBody.put("version_no", 1);

            checkPostMethod(requestBody, "setting").then().body("response.status", equalTo(0));
        }

        searchBody = new HashMap<>();
        searchBody.put("size", NUM * 2);
        List<String> permissionsList = getPropList(searchBody, "permissions");
        for (String permissions : permissionsList) {
            assertEquals(newPermission.replace("R", "{role}"), permissions);
        }
    }

    @Override
    protected void testDelete() {
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", NUM * 2);
        List<String> idList = getPropList(searchBody, "id");
        idList.forEach(id -> {
            //Test: delete setting api
            checkDeleteMethod(ITEM_ENDPOINT_SUFFIX + "/" + id).then().body("response.status", equalTo(0));
        });

        // Test: NUMber of settings.
        checkGetMethod(searchBody, LIST_ENDPOINT_SUFFIX).then().body(getJsonPath() + ".size()", equalTo(0));
    }

    private void testPermission() {
        // Create access token
        final String name = NAME_PREFIX + 0;
        final Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("permissions", "Radmin-api");
        String response = checkPutMethod(requestBody, ITEM_ENDPOINT_SUFFIX).asString();

        // Test: access admin api using a new token
        String id = JsonPath.from(response).get("response.id");
        response = checkGetMethod(requestBody, ITEM_ENDPOINT_SUFFIX + "/" + id).asString();
        String token = JsonPath.from(response).get("response.setting.token");
        checkGetMethod(requestBody, ITEM_ENDPOINT_SUFFIX + "/" + id).then()
                .body("response." + ITEM_ENDPOINT_SUFFIX + ".name", equalTo(name))
                .body("response." + ITEM_ENDPOINT_SUFFIX + ".token", equalTo(token));
    }

    @Override
    protected void clearTestData() {
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", NUM * 10);
        List<String> idList = getPropList(searchBody, "id");
        idList.forEach(id -> {
            checkDeleteMethod(ITEM_ENDPOINT_SUFFIX + "/" + id);
        });
    }
}
