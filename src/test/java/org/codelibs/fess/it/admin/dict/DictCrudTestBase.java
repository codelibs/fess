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
package org.codelibs.fess.it.admin.dict;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.it.CrudTestBase;
import org.junit.jupiter.api.BeforeEach;

import io.restassured.path.json.JsonPath;

public abstract class DictCrudTestBase extends CrudTestBase {
    protected String dictId;

    abstract protected String getDictType();

    private static final String LIST_ENDPOINT_SUFFIX = "settings";
    private static final String ITEM_ENDPOINT_SUFFIX = "setting";

    @Override
    protected String getListEndpointSuffix() {
        return LIST_ENDPOINT_SUFFIX + "/" + dictId;
    }

    @Override
    protected String getItemEndpointSuffix() {
        return ITEM_ENDPOINT_SUFFIX + "/" + dictId;
    }

    @BeforeEach
    protected void initializeDictId() {
        final Map<String, Object> searchBody = new HashMap<>();
        final String response = checkMethodBase(searchBody).get("/api/admin/dict").asString();
        final List<Map<String, String>> dicts = JsonPath.from(response).getList("response.settings");

        for (Map<String, String> item : dicts) {
            assertTrue(item.containsKey("id"));
            assertTrue(item.containsKey("type"));
            if (getDictType().equals(item.get("type"))) {
                dictId = item.get("id");
                return;
            }
        }

        assertTrue(false);
    }
}
