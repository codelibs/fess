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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.it.CrudTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.restassured.path.json.JsonPath;

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
        final Map<String, Object> requestBody = new HashMap<>();
        final Map<String, Object> doc = new HashMap<>();
        final String keyProp = NAME_PREFIX + id;
        doc.put(KEY_PROPERTY, keyProp);
        doc.put("url", "http://example.com/" + id);
        doc.put("boost", id);
        doc.put("role", "Rguest");
        requestBody.put("doc", doc);
        return requestBody;
    }

    @Override
    protected Map<String, Object> createSearchBody(final int size) {
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", size);
        searchBody.put("q", NAME_PREFIX + "*");
        return searchBody;
    }

    @Override
    protected Map<String, Object> getUpdateMap() {
        final Map<String, Object> updateMap = new HashMap<>();
        //        updateMap.put("click_count", 100);
        return updateMap;
    }

    @Override
    protected void testUpdate() {

        // Test: update settings api
        final Map<String, Object> updateMap = getUpdateMap();
        final Map<String, Object> searchBody = createSearchBody(SEARCH_ALL_NUM);
        List<Map<String, Object>> settings = getItemList(searchBody);

        for (Map<String, Object> setting : settings) {
            final Map<String, Object> requestBody = new HashMap<>(updateMap);
            final String idKey = getIdKey();

            requestBody.put("version", 1);
            requestBody.put("crud_mode", 2);

            if (setting.containsKey(idKey)) {
                requestBody.put(idKey, setting.get(idKey));
            }

            final Map<String, Object> doc = new HashMap<>();
            doc.put("doc_id", setting.get("doc_id"));
            doc.put("url", setting.get("url_link"));
            doc.put("title", setting.get("title"));
            doc.put("role", "Rguest");
            doc.put("boost", setting.get("boost"));
            //            doc.put("click_count", 100);  // Validation Error
            requestBody.put("doc", doc);

            checkPostMethod(requestBody, getItemEndpointSuffix()).then().body("response.status", equalTo(0));
            refresh();
        }

        checkUpdate();
    }

    @Override
    protected List<Map<String, Object>> getItemList(final Map<String, Object> body) {
        final String response = checkMethodBase(body).get(getApiPath() + "/" + getListEndpointSuffix()).asString();
        final List<Map<String, Object>> results = JsonPath.from(response).getList("response.docs");
        return results;
    }

    @Test
    void crudTest() {
        testCreate();
        testRead();
        testUpdate();
        testDelete();
    }
}
