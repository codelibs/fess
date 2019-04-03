/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
import java.util.Map;

import org.codelibs.fess.it.CrudTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.restassured.path.json.JsonPath;

@Tag("it")
public class SystemInfoTests extends CrudTestBase {

    private static final String NAME_PREFIX = "systemInfoTest_";
    private static final String API_PATH = "/api/admin/systeminfo";
    private static final String LIST_ENDPOINT_SUFFIX = "";
    private static final String ITEM_ENDPOINT_SUFFIX = "";

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
        assertTrue(false); // Unreachable
        return null;
    }

    @Override
    protected Map<String, Object> getUpdateMap() {
        assertTrue(false); // Unreachable
        return null;
    }

    @Override
    protected void testRead() {
        final Map<String, Object> searchBody = new HashMap<>();
        String response = checkGetMethod(searchBody, "").asString();
        final Map<String, Object> res = JsonPath.from(response).getMap("response");
        assertTrue(res.containsKey("env_props"));
        assertTrue(res.containsKey("system_props"));
        assertTrue(res.containsKey("fess_props"));
        assertTrue(res.containsKey("bug_report_props"));
        assertEquals(new Integer(0), JsonPath.from(response).get("response.status"));
    }

    @Override
    protected void tearDown() {
        // do nothing
    }

    @Test
    void crudTest() {
        testRead();
    }
}
