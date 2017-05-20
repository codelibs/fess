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
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.it.CrudTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("it")
public class KeyMatchTests extends CrudTestBase {
    private static final int NUM = 20;

    private static final String NAME_PREFIX = "keyMatchTest_";
    private static final String API_PATH = "/api/admin/keymatch";
    private static final String LIST_ENDPOINT_SUFFIX = "settings";
    private static final String ITEM_ENDPOINT_SUFFIX = "setting";

    private static final String KEY_PROPERTY = "term";

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

    @Override
    protected void testCreate() {
        // Test: create setting api.
        for (int i = 0; i < NUM; i++) {
            final Map<String, Object> requestBody = new HashMap<>();
            final String keyProp = NAME_PREFIX + i;
            requestBody.put(KEY_PROPERTY, keyProp);
            requestBody.put("query", "query" + i);
            requestBody.put("max_size", new Integer(i).toString());
            requestBody.put("boost", 100);

            checkPutMethod(requestBody, ITEM_ENDPOINT_SUFFIX).then().body("response.created", equalTo(true))
                    .body("response.status", equalTo(0));
        }

        // Test: number of settings.
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", NUM * 2);
        checkGetMethod(searchBody, LIST_ENDPOINT_SUFFIX).then().body(getJsonPath() + ".size()", equalTo(NUM));
    }

    @Override
    protected void testRead() {
        // Test: get settings api.
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", NUM * 2);
        List<String> propList = getPropList(searchBody, KEY_PROPERTY);

        assertEquals(NUM, propList.size());
        for (int i = 0; i < NUM; i++) {
            final String keyProp = NAME_PREFIX + i;
            assertTrue(propList.contains(keyProp), keyProp);
        }

        List<String> idList = getPropList(searchBody, "id");
        idList.forEach(id -> {
            // Test: get setting api
            checkGetMethod(searchBody, ITEM_ENDPOINT_SUFFIX + "/" + id).then()
                    .body("response." + ITEM_ENDPOINT_SUFFIX + ".id", equalTo(id))
                    .body("response." + ITEM_ENDPOINT_SUFFIX + "." + KEY_PROPERTY, startsWith(NAME_PREFIX));
        });

        // Test: paging
        searchBody.put("size", 1);
        for (int i = 0; i < NUM; i++) {
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

        final String newQuery = "new_query";
        for (Map<String, Object> setting : settings) {
            final Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("id", setting.get("id"));
            requestBody.put(KEY_PROPERTY, setting.get(KEY_PROPERTY));
            requestBody.put("query", newQuery);
            requestBody.put("max_size", setting.get("max_size"));
            requestBody.put("boost", setting.get("boost"));
            requestBody.put("version_no", 1);

            checkPostMethod(requestBody, ITEM_ENDPOINT_SUFFIX).then().body("response.status", equalTo(0));
        }

        searchBody = new HashMap<>();
        searchBody.put("size", NUM * 2);
        List<String> valueList = getPropList(searchBody, "query");
        for (String value : valueList) {
            assertEquals(value, newQuery);
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
