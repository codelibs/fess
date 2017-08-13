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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.it.CrudTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("it")
public class SearchListTests extends CrudTestBase {

    private static final String NAME_PREFIX = "searchListTest_";
    private static final String API_PATH = "/api/admin/searchlist";
    private static final String LIST_ENDPOINT_SUFFIX = "docs";
    private static final String ITEM_ENDPOINT_SUFFIX = "doc";

    private static final String KEY_PROPERTY = "title";

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
    protected String getIdKey() {
        return "doc_id";
    }

    @Override
    protected Map<String, Object> createTestParam(int id) {

        final Map<String, Object> doc = new HashMap<>();
        final String keyProp = NAME_PREFIX + id;
        doc.put(KEY_PROPERTY, keyProp);
        doc.put("url", "http://example.com/" + id);
        doc.put("boost", id);
        doc.put("role", "Rguest");

        final Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("doc", doc);
        return requestBody;
    }

    @Override
    protected Map<String, Object> createSearchBody(final int size) {
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", size);
        searchBody.put("q", NAME_PREFIX);
        return searchBody;
    }

    @Override
    protected Map<String, Object> getUpdateMap() {
        // TODO
        assertTrue(false);

        final Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("click_count", 100);
        return updateMap;
    }

    @Test
    void crudTest() {
        testCreate();
        testRead();
        //testUpdate(); // TODO
        testDelete();
    }
}
