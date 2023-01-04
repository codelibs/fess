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
package org.codelibs.fess.it.admin;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.it.CrudTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.restassured.path.json.JsonPath;

@Tag("it")
public class AccessTokenTests extends CrudTestBase {
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

    @Override
    protected Map<String, Object> createTestParam(int id) {
        final Map<String, Object> requestBody = new HashMap<>();
        final String keyProp = NAME_PREFIX + id;
        requestBody.put(KEY_PROPERTY, keyProp);
        requestBody.put("permissions", "Radmin-api");
        return requestBody;
    }

    @Override
    protected Map<String, Object> getUpdateMap() {
        final Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("permissions", "Radmin-api2");
        return updateMap;
    }

    @Override
    protected void checkUpdate() {
        Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", NUM * 2);
        List<String> updatedList = getPropList(searchBody, "permissions");
        for (String val : updatedList) {
            assertEquals(val, "{role}admin-api2");
        }
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
}
