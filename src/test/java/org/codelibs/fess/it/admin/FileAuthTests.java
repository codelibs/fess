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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.it.CrudTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.restassured.path.json.JsonPath;

@Tag("it")
public class FileAuthTests extends CrudTestBase {

    private static final String NAME_PREFIX = "fileAuthTest_";
    private static final String API_PATH = "/api/admin/fileauth";
    private static final String LIST_ENDPOINT_SUFFIX = "settings";
    private static final String ITEM_ENDPOINT_SUFFIX = "setting";

    private static final String KEY_PROPERTY = "username";

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

    @BeforeEach
    void createFileConfig() {
        final Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "test_fileconfig");
        requestBody.put("paths", "file:///example/path/");
        requestBody.put("num_of_thread", 5);
        requestBody.put("interval_time", 1000);
        requestBody.put("boost", 100.0);
        requestBody.put("available", true);
        requestBody.put("sort_order", 1);
        checkMethodBase(requestBody).post("/api/admin/fileconfig/setting").then().body("response.created", equalTo(true))
                .body("response.status", equalTo(0));
    }

    String getFileConfigId() {
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("name", "test_fileconfig");
        String res = checkMethodBase(searchBody).get("/api/admin/fileconfig/settings").asString();
        List<String> fileConfigList = JsonPath.from(res).getList("response.settings.findAll {it.name.startsWith(\"test_fileconfig\")}.id");
        return fileConfigList.get(0);
    }

    @AfterEach
    void deleteFileConfig() {
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", NUM * 2);
        final String fileConfigId = getFileConfigId();
        checkMethodBase(searchBody).delete("/api/admin/fileconfig/setting/" + fileConfigId).then().body("response.status", equalTo(0));
    }

    @Override
    protected Map<String, Object> createTestParam(int id) {
        final Map<String, Object> requestBody = new HashMap<>();
        final String keyProp = NAME_PREFIX + id;
        requestBody.put(KEY_PROPERTY, keyProp);
        requestBody.put("file_config_id", getFileConfigId());
        return requestBody;
    }

    @Override
    protected Map<String, Object> getUpdateMap() {
        final Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("parameters", "new_parameters=test");
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
