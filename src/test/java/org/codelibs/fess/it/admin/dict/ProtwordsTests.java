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
package org.codelibs.fess.it.admin.dict;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("it")
public class ProtwordsTests extends DictCrudTestBase {

    private static final String NAME_PREFIX = "protwordsTest_";
    private static final String API_PATH = "/api/admin/dict/protwords";
    private static final String LIST_ENDPOINT_SUFFIX = "settings";
    private static final String ITEM_ENDPOINT_SUFFIX = "setting";
    private static final String DICT_TYPE = "protwords";

    private static final String KEY_PROPERTY = "input";

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
        return LIST_ENDPOINT_SUFFIX + "/" + dictId;
    }

    @Override
    protected String getItemEndpointSuffix() {
        return ITEM_ENDPOINT_SUFFIX + "/" + dictId;
    }

    @Override
    protected String getDictType() {
        return DICT_TYPE;
    }

    @Override
    protected Map<String, Object> createTestParam(int id) {
        final Map<String, Object> requestBody = new HashMap<>();
        final String keyProp = NAME_PREFIX + id;
        requestBody.put(KEY_PROPERTY, keyProp);
        return requestBody;
    }

    @Override
    protected Map<String, Object> getUpdateMap() {
        fail(); // Unreachable
        return null;
    }

    @Test
    void crudTest() {
        testCreate();
        testRead();
        testDelete();
    }
}
