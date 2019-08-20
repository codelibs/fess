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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

import io.restassured.response.Response;
import org.codelibs.fess.it.CrudTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

@Tag("it")
public class PluginTests extends CrudTestBase {
    // TODO delete
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CrudTestBase.class);

    private static final String NAME_PREFIX = "pluginTests_";
    private static final String API_PATH = "/api/admin/plugin";
    private static final String INSTALLED_ENDPOINT_SUFFIX = "installed";
    private static final String AVAILABLE_ENDPOINT_SUFFIX = "available";

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
        final Map<String, Object> requestBody = new HashMap<>();
        return requestBody;
    }

    @Override
    protected Map<String, Object> getUpdateMap() {
        final Map<String, Object> updateMap = new HashMap<>();
        return updateMap;
    }

    @AfterEach
    protected void tearDown() {
        // do nothing
    }

    protected String getInstalledEndpointSuffix() {
        return INSTALLED_ENDPOINT_SUFFIX;
    }

    protected String getAvailableEndpointSuffix() {
        return AVAILABLE_ENDPOINT_SUFFIX;
    }

    protected Response checkDeleteMethod(final Map<String, Object> body) {
        return checkMethodBase(body).delete(getApiPath() + "/");
    }

    @Test
    void testInstalled() {
        checkGetMethod(Collections.emptyMap(), getInstalledEndpointSuffix() + "/").then().body("response.status", equalTo(0));
    }

    @Test
    void testAvailable() {
        checkGetMethod(Collections.emptyMap(), getAvailableEndpointSuffix() + "/").then().body("response.status", equalTo(0));
    }

    @Test
    void testIntall() {
        checkPutMethod(Collections.emptyMap(), "").then().body("response.status", equalTo(1));
    }

    @Test
    void testUninstall() {
        checkDeleteMethod(Collections.emptyMap()).then().body("response.status", equalTo(1));
    }

    @Test
    void testCRUD() {
        // TODO
    }
}
