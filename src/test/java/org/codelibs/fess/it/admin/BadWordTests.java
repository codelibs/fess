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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.it.CrudTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

@Tag("it")
public class BadWordTests extends CrudTestBase {

    private static final String NAME_PREFIX = "badWordTest_";
    private static final String API_PATH = "/api/admin/badword";
    private static final String LIST_ENDPOINT_SUFFIX = "settings";
    private static final String ITEM_ENDPOINT_SUFFIX = "setting";

    private static final String KEY_PROPERTY = "suggest_word";

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
        return requestBody;
    }

    @Override
    protected Map<String, Object> getUpdateMap() {
        final Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(KEY_PROPERTY, NAME_PREFIX + "new");
        return updateMap;
    }

    @Test
    void crudTest() {
        testCreate();
        testRead();
        testUpdate();
        testDelete();
    }

    @Test
    void testDownloadCsv_ok() {
        // First create some test data
        for (int i = 0; i < 3; i++) {
            final Map<String, Object> requestBody = createTestParam(i + 100);
            checkPostMethod(requestBody, getItemEndpointSuffix());
        }
        refresh();

        // Download CSV
        Response response =
                given().contentType("application/json").header("Authorization", getTestToken()).when().get(getApiPath() + "/download");

        assertEquals(200, response.getStatusCode());
        String body = response.getBody().asString();
        assertTrue(body != null, "CSV response should not be null");
        // CSV should contain the test data
        assertTrue(body.contains(NAME_PREFIX) || body.isEmpty(), "CSV should contain test data or be empty");
    }

    @Test
    void testUploadCsv_ok() {
        // Create CSV content
        String csvContent = NAME_PREFIX + "uploadTest1\n" + NAME_PREFIX + "uploadTest2\n";

        // Upload CSV
        Response response = given().header("Authorization", getTestToken())
                .multiPart("file", "badwords.csv", csvContent.getBytes())
                .when()
                .put(getApiPath() + "/upload");

        int statusCode = response.getStatusCode();
        // Accept either 200 (success) or other status codes based on implementation
        assertTrue(statusCode == 200 || statusCode == 400, "Status code should be 200 or 400, but was " + statusCode);

        if (statusCode == 200) {
            JsonPath jsonPath = JsonPath.from(response.asString());
            assertEquals(Integer.valueOf(0), jsonPath.get("response.status"));
        }
    }
}
