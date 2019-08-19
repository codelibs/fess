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

import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import org.codelibs.fess.it.ITBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Tag("it")
public class PluginTests extends ITBase {

    private static final String API_PATH = "/api/admin/plugin";
    private static final String INSTALLEDPLUGINS_ENDPOINT_SUFFIX = "installedplugins";
    private static final String AVAILABLEPLUGINS_ENDPOINT_SUFFIX = "availableplugins";
    private static final String INSTALL_ENDPOINT_SUFFIX = "install";
    private static final String DELETE_ENDPOINT_SUFFIX = "delete";

    @BeforeAll
    protected static void initAll() {
        RestAssured.baseURI = getFessUrl();
        settingTestToken();
    }

    @BeforeEach
    protected void init() {
    }

    @AfterEach
    protected void tearDown() {
    }

    @AfterAll
    protected static void tearDownAll() {
        deleteTestToken();
    }

    protected String getApiPath() {
        return API_PATH;
    }

    protected String getInstalledpluginsEndpointSuffix() {
        return INSTALLEDPLUGINS_ENDPOINT_SUFFIX;
    }

    protected String getAvailablepluginsEndpointSuffix() {
        return AVAILABLEPLUGINS_ENDPOINT_SUFFIX;
    }

    protected String getInstallEndpointSuffix() {
        return INSTALL_ENDPOINT_SUFFIX;
    }

    protected String getDeleteEndpointSuffix() {
        return DELETE_ENDPOINT_SUFFIX;
    }

    @Test
    void testInstalledplugins() {
        checkMethodBase(Collections.emptyMap()).get(getApiPath() + "/" + getInstalledpluginsEndpointSuffix() + "/").then()
                .body("response.status", equalTo(0));
    }

    @Test
    void testAvailavleplugins() {
        checkMethodBase(Collections.emptyMap()).get(getApiPath() + "/" + getAvailablepluginsEndpointSuffix() + "/").then()
                .body("response.status", equalTo(0));
    }

    @Test
    void testIntall() {
        checkMethodBase(Collections.emptyMap()).get(getApiPath() + "/" + getInstallEndpointSuffix() + "/").then()
                .body("response.status", equalTo(0));
    }

    @Test
    void testDelete() {
        checkMethodBase(Collections.emptyMap()).get(getApiPath() + "/" + getDeleteEndpointSuffix() + "/").then()
                .body("response.status", equalTo(0));
    }
}
