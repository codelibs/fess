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
package org.codelibs.fess.it.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.it.CrudTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.restassured.path.json.JsonPath;

@Tag("it")
public class SuggestTests extends CrudTestBase {

    private static final String NAME_PREFIX = "";
    private static final String API_PATH = "/api/admin/suggest";
    private static final String LIST_ENDPOINT_SUFFIX = "";
    private static final String ITEM_ENDPOINT_SUFFIX = "";

    private static final String KEY_PROPERTY = "";

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
        fail(); // Unreachable
        return null;
    }

    @Override
    protected Map<String, Object> getUpdateMap() {
        fail(); // Unreachable
        return null;
    }

    @Override
    protected void testRead() {
        final Map<String, Object> searchBody = new HashMap<>();
        String response = checkGetMethod(searchBody, "").asString();
        final Map<String, Object> res = JsonPath.from(response).getMap("response.setting");
        assertTrue(res.containsKey("total_words_num"));
        assertTrue(res.containsKey("document_words_num"));
        assertTrue(res.containsKey("query_words_num"));
        assertEquals(Integer.valueOf(0), JsonPath.from(response).get("response.status"));
    }

    @Override
    @AfterEach
    protected void tearDown() {
        // do nothing
    }

    @Test
    void crudTest() {
        testRead();
    }

    @Test
    void testDeleteAllWords_ok() {
        // Test deleting all suggest words
        String response = checkDeleteMethod("all").asString();
        assertEquals(Integer.valueOf(0), JsonPath.from(response).get("response.status"));
    }

    @Test
    void testDeleteDocumentWords_ok() {
        // Test deleting document-based suggest words
        String response = checkDeleteMethod("document").asString();
        assertEquals(Integer.valueOf(0), JsonPath.from(response).get("response.status"));
    }

    @Test
    void testDeleteQueryWords_ok() {
        // Test deleting query-based suggest words
        String response = checkDeleteMethod("query").asString();
        assertEquals(Integer.valueOf(0), JsonPath.from(response).get("response.status"));
    }
}
