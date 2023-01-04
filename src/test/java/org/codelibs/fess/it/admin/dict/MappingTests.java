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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("it")
public class MappingTests extends DictCrudTestBase {

    private static final String NAME_PREFIX = "mappingTest_";
    private static final String API_PATH = "/api/admin/dict/mapping";
    private static final String LIST_ENDPOINT_SUFFIX = "settings";
    private static final String ITEM_ENDPOINT_SUFFIX = "setting";
    private static final String DICT_TYPE = "mapping";

    private static final String KEY_PROPERTY = "inputs";

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
        requestBody.put("output", "output");
        return requestBody;
    }

    @Override
    protected Map<String, Object> getUpdateMap() {
        final Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("output", "new_output");
        return updateMap;
    }

    @Test
    void crudTest() {
        testCreate();
        testRead();
        testUpdate();
        testDelete();
    }
}
