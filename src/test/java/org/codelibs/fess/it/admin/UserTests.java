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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.it.CrudTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("it")
public class UserTests extends CrudTestBase {

    private static final String NAME_PREFIX = "userTest_";
    private static final String API_PATH = "/api/admin/user";
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
        requestBody.put("password", "password" + id);
        requestBody.put("confirm_password", "password" + id);
        return requestBody;
    }

    @Override
    protected Map<String, Object> getUpdateMap() {
        final Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("name", NAME_PREFIX + "new");
        return updateMap;
    }

    /**
     * Renames every user. A user cannot take the name of another user, so each one gets a name of its
     * own (the base class would give all of them the same name), and the refusal is checked too.
     */
    @Override
    protected void testUpdate() {
        final List<Map<String, Object>> users = getItemList(createSearchBody(SEARCH_ALL_NUM));
        final String idKey = getIdKey();
        for (int i = 0; i < users.size(); i++) {
            final Map<String, Object> requestBody = new HashMap<>();
            // the version read with the user: the API refuses a body whose version is not the stored one
            requestBody.put("version_no", users.get(i).get("version_no"));
            requestBody.put(idKey, users.get(i).get(idKey));
            requestBody.put(KEY_PROPERTY, NAME_PREFIX + "new" + i);
            checkPutMethod(requestBody, getItemEndpointSuffix()).then().body("response.status", equalTo(0));
            refresh();
        }

        final List<String> names = getPropList(createSearchBody(SEARCH_ALL_NUM), KEY_PROPERTY);
        assertEquals(users.size(), names.size());
        for (final String name : names) {
            assertEquals(true, name.startsWith(NAME_PREFIX + "new"), name);
        }

        // the first user cannot take the name of the second
        // (read the user again: the rename above changed its version, and a stale version is refused for that reason)
        final Map<String, Object> current = getItemList(createSearchBody(SEARCH_ALL_NUM)).stream()
                .filter(u -> users.get(0).get(idKey).equals(u.get(idKey)))
                .findFirst()
                .orElseThrow();
        final Map<String, Object> refused = new HashMap<>();
        refused.put("version_no", current.get("version_no"));
        refused.put(idKey, users.get(0).get(idKey));
        refused.put(KEY_PROPERTY, NAME_PREFIX + "new1");
        checkPutMethod(refused, getItemEndpointSuffix()).then().body("response.status", equalTo(1));

        // a body that carries the version the user had before it was changed is refused, and the user stays as it is
        final Map<String, Object> stale = new HashMap<>();
        stale.put("version_no", users.get(0).get("version_no"));
        stale.put(idKey, users.get(0).get(idKey));
        stale.put(KEY_PROPERTY, NAME_PREFIX + "stale");
        checkPutMethod(stale, getItemEndpointSuffix()).then().body("response.status", equalTo(1));
        assertEquals(false, getPropList(createSearchBody(SEARCH_ALL_NUM), KEY_PROPERTY).contains(NAME_PREFIX + "stale"));
    }

    @Test
    void crudTest() {
        testCreate();
        testRead();
        testUpdate();
        testDelete();
    }
}
