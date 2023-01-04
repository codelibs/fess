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
public class GroupTests extends CrudTestBase {

    private static final String NAME_PREFIX = "groupTest_";
    private static final String API_PATH = "/api/admin/group";
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
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("gidNumber", new Integer(id).toString());
        requestBody.put("attributes", attributes);

        return requestBody;
    }

    @Override
    protected Map<String, Object> getUpdateMap() {
        final Map<String, Object> updateMap = new HashMap<>();
        final Map<String, String> newAttributes = new HashMap<>();
        newAttributes.put("gidNumber", "100");
        updateMap.put("attributes", newAttributes);

        return updateMap;
    }

    @Override
    protected void checkUpdate() {
        Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", NUM * 2);
        String response = checkGetMethod(searchBody, getListEndpointSuffix()).asString();
        List<Map<String, String>> attrList = JsonPath.from(response).getList(getJsonPath() + ".attributes");
        assertEquals(NUM, attrList.size());
        for (Map<String, String> attr : attrList) {
            assertTrue(attr.containsKey("gidNumber"));
            assertEquals(attr.get("gidNumber"), "100");
        }
    }

    @Test
    void crudTest() {
        testCreate();
        testRead();
        testUpdate();
        testDelete();
    }
}
