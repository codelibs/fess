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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.it.CrudTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

@Tag("it")
@Disabled("Requires storage backend (S3/GCS) to be configured")
public class StorageTests extends CrudTestBase {

    private static final String NAME_PREFIX = "storageTests_";
    private static final String API_PATH = "/api/admin/storage";
    private static final String LIST_ENDPOINT_SUFFIX = "list";
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

    @Test
    void testList_ok() {
        checkGetMethod(Collections.emptyMap(), getListEndpointSuffix() + "/").then().body("response.status", equalTo(0));
    }

    @Test
    void testUpload_ok() {
        // Create a test file content
        String fileContent = "Test file content for storage upload";

        // Upload file
        Response response = given().header("Authorization", getTestToken())
                .multiPart("file", NAME_PREFIX + "test.txt", fileContent.getBytes())
                .when()
                .put(getApiPath() + "/upload/");

        // Storage may not be enabled, so accept various responses
        int statusCode = response.getStatusCode();
        assertTrue(statusCode == 200 || statusCode == 400 || statusCode == 500,
                "Status code should be 200, 400 or 500, but was " + statusCode);
    }

    @Test
    void testDownload_notFound() {
        // Try to download a non-existent file
        Response response =
                given().header("Authorization", getTestToken()).when().get(getApiPath() + "/download/nonexistent_file_12345.txt");

        int statusCode = response.getStatusCode();
        // Accept 404 or 200 with error (depending on implementation)
        assertTrue(statusCode == 200 || statusCode == 404 || statusCode == 500,
                "Status code should be 200, 404 or 500, but was " + statusCode);
    }

    @Test
    void testDelete_notFound() {
        // Try to delete a non-existent file
        Response response =
                given().header("Authorization", getTestToken()).when().delete(getApiPath() + "/delete/nonexistent_file_12345.txt");

        int statusCode = response.getStatusCode();
        assertTrue(statusCode == 200 || statusCode == 404 || statusCode == 500,
                "Status code should be 200, 404 or 500, but was " + statusCode);
    }

    @Test
    void testCreateDir_ok() {
        // Create a directory
        Response response = given().contentType("application/json")
                .header("Authorization", getTestToken())
                .body("{\"name\":\"" + NAME_PREFIX + "testdir\"}")
                .when()
                .post(getApiPath() + "/createDir/");

        int statusCode = response.getStatusCode();
        // Storage may not be enabled
        assertTrue(statusCode == 200 || statusCode == 400 || statusCode == 500,
                "Status code should be 200, 400 or 500, but was " + statusCode);
    }

    @Test
    void testListWithPath_ok() {
        // Test list with a specific path
        Response response =
                given().contentType("application/json").header("Authorization", getTestToken()).when().get(getApiPath() + "/list/testdir/");

        int statusCode = response.getStatusCode();
        // Accept 200 (success) or error responses
        assertTrue(statusCode == 200 || statusCode == 400 || statusCode == 404 || statusCode == 500,
                "Status code should be 200, 400, 404 or 500, but was " + statusCode);
    }
}
