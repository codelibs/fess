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
package org.codelibs.fess.it;

import static io.restassured.RestAssured.given;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public abstract class CrudTestBase extends ITBase {

    // ================
    // Abstract Methods
    // ================
    abstract protected String getNamePrefix();

    abstract protected String getApiPath();

    abstract protected String getKeyProperty();

    abstract protected String getListEndpointSuffix();

    abstract protected String getItemEndpointSuffix();

    abstract protected void testCreate();

    abstract protected void testRead();

    abstract protected void testUpdate();

    abstract protected void testDelete();

    abstract protected void clearTestData();

    // ================

    @BeforeAll
    static void initAll() {
        RestAssured.baseURI = getFessUrl();
        settingTestToken();
    }

    @BeforeEach
    void init() {
    }

    @AfterEach
    void tearDown() {
        clearTestData();
    }

    @AfterAll
    static void tearDownAll() {
        deleteTestToken();
    }

    // ================
    // Utilities
    // ================
    protected Response checkGetMethod(final Map<String, Object> body, final String path) {
        return checkMethodBase(body).get(getApiPath() + "/" + path);
    }

    protected Response checkPutMethod(final Map<String, Object> body, final String path) {
        return checkMethodBase(body).put(getApiPath() + "/" + path);
    }

    protected Response checkPostMethod(final Map<String, Object> body, final String path) {
        return checkMethodBase(body).post(getApiPath() + "/" + path);
    }

    protected Response checkDeleteMethod(final String path) {
        return given().header("Authorization", getTestToken()).delete(getApiPath() + "/" + path);
    }

    protected List<Map<String, Object>> getItemList(final Map<String, Object> body) {
        String response = checkGetMethod(body, getListEndpointSuffix()).asString();
        return JsonPath.from(response).getList(getJsonPath());
    }

    protected List<String> getPropList(final Map<String, Object> body, final String prop) {
        String response = checkGetMethod(body, getListEndpointSuffix()).asString();
        return JsonPath.from(response).getList(getJsonPath() + "." + prop);
    }

    protected String getJsonPath() {
        return "response." + getListEndpointSuffix() + ".findAll {it." + getKeyProperty() + ".startsWith(\"" + getNamePrefix() + "\")}";
    }

    private RequestSpecification checkMethodBase(final Map<String, Object> body) {
        return given().header("Authorization", getTestToken()).body(body, ObjectMapperType.JACKSON_2).when();
    }

}
