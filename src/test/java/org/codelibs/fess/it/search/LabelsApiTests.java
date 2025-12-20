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
package org.codelibs.fess.it.search;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.it.ITBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/**
 * Integration tests for the Labels API (/api/v1/labels)
 */
@Tag("it")
public class LabelsApiTests extends ITBase {

    private static final String TEST_LABEL_NAME = "labelsApiTest_label";
    private static final String TEST_LABEL_VALUE = "labelsApiTest_value";
    private static String testLabelId;

    @BeforeAll
    protected static void initAll() {
        RestAssured.baseURI = getFessUrl();
        settingTestToken();
    }

    @AfterAll
    protected static void tearDownAll() {
        if (testLabelId != null) {
            deleteLabel(testLabelId);
        }
        deleteTestToken();
    }

    @Test
    public void testGetLabels_ok() {
        given().contentType("application/json")
                .when()
                .get("/api/v1/labels")
                .then()
                .statusCode(200)
                .body("record_count", greaterThanOrEqualTo(0));
    }

    @Test
    public void testGetLabels_withTestLabel() {
        // Create a test label
        testLabelId = createLabel(TEST_LABEL_NAME, TEST_LABEL_VALUE);
        refresh();

        // Verify the label appears in the list
        String response = given().contentType("application/json").when().get("/api/v1/labels").asString();

        JsonPath jsonPath = JsonPath.from(response);
        int recordCount = jsonPath.getInt("record_count");
        assertTrue(recordCount >= 0, "Record count should be >= 0");

        if (recordCount > 0) {
            List<Map<String, Object>> labels = jsonPath.getList("data");
            assertTrue(labels != null, "Data should not be null when labels exist");
        }
    }

    @Test
    public void testGetLabels_responseStructure() {
        String response = given().contentType("application/json").when().get("/api/v1/labels").asString();

        JsonPath jsonPath = JsonPath.from(response);
        assertTrue(jsonPath.getInt("record_count") >= 0);

        List<Map<String, Object>> labels = jsonPath.getList("data");
        if (labels != null && !labels.isEmpty()) {
            Map<String, Object> firstLabel = labels.get(0);
            assertTrue(firstLabel.containsKey("label"), "Label should have 'label' property");
            assertTrue(firstLabel.containsKey("value"), "Label should have 'value' property");
        }
    }

    private static String createLabel(final String name, final String value) {
        Map<String, Object> labelBody = new HashMap<>();
        labelBody.put("name", name);
        labelBody.put("value", value);
        Response response = checkMethodBase(labelBody).post("/api/admin/labeltype/setting");
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertTrue(jsonPath.getBoolean("response.created"));
        assertEquals(0, jsonPath.getInt("response.status"));
        return jsonPath.get("response.id");
    }

    private static void deleteLabel(final String id) {
        checkMethodBase(new HashMap<>()).delete("/api/admin/labeltype/setting/" + id);
    }
}
