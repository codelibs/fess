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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.it.CrudTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.restassured.path.json.JsonPath;

/**
 * Integration Tests for /api/admin/stats
 */
@Tag("it")
public class StatsTests extends CrudTestBase {
    private static final Logger logger = LogManager.getLogger(StatsTests.class);

    private static final String API_PATH = "/api/admin/stats";

    @Override
    protected String getNamePrefix() {
        fail("getNamePrefix is not supported for StatsTests");
        return null;
    }

    @Override
    protected String getApiPath() {
        return API_PATH;
    }

    @Override
    protected String getKeyProperty() {
        fail("getKeyProperty is not supported for StatsTests");
        return null;
    }

    @Override
    protected String getListEndpointSuffix() {
        fail("getListEndpointSuffix is not supported for StatsTests");
        return null;
    }

    @Override
    protected String getItemEndpointSuffix() {
        fail("getItemEndpointSuffix is not supported for StatsTests");
        return null;
    }

    @Override
    protected Map<String, Object> createTestParam(int id) {
        fail("createTestParam is not supported for StatsTests");
        return null;
    }

    @Override
    protected Map<String, Object> getUpdateMap() {
        fail("getUpdateMap is not supported for StatsTests");
        return null;
    }

    @Override
    protected void testRead() {
        logger.info("[BEGIN] testRead");

        final Map<String, Object> searchBody = new HashMap<>();
        String response = checkGetMethod(searchBody, "").asString();

        logger.info("Stats response: {}", response);

        // Verify response structure
        final Map<String, Object> res = JsonPath.from(response).getMap("response");
        assertNotNull(res, "Response should not be null");

        // Verify status code
        Integer status = JsonPath.from(response).get("response.status");
        assertEquals(Integer.valueOf(0), status, "Status should be 0");

        // Verify version field exists
        String version = JsonPath.from(response).get("response.version");
        assertNotNull(version, "Version should not be null");

        logger.info("Stats version: {}", version);
        logger.info("Stats status: {}", status);

        // Verify stats contain expected fields
        // Note: The actual fields may vary depending on the implementation
        // Common fields might include: document count, index size, etc.
        assertTrue(res.size() > 0, "Response should contain stats data");

        logger.info("[END] testRead");
    }

    @Override
    protected void tearDown() {
        // do nothing
    }

    @Test
    void crudTest() {
        testRead();
    }

    @Test
    void statsContentTest() {
        logger.info("[BEGIN] statsContentTest");

        final Map<String, Object> searchBody = new HashMap<>();
        String response = checkGetMethod(searchBody, "").asString();

        final Map<String, Object> res = JsonPath.from(response).getMap("response");

        // Log all available stats fields for debugging
        logger.info("Available stats fields: {}", res.keySet());

        // Verify response contains meaningful data
        assertNotNull(res, "Stats response should not be null");
        assertTrue(res.size() > 0, "Stats should contain at least some data");

        // Check for common stats fields (these may need to be adjusted based on actual implementation)
        // Examples of fields that might be present:
        // - Document count
        // - Index size
        // - Memory usage
        // - System stats
        // etc.

        for (Map.Entry<String, Object> entry : res.entrySet()) {
            logger.info("Stats field: {} = {}", entry.getKey(), entry.getValue());
        }

        logger.info("[END] statsContentTest");
    }

    @Test
    void statsResponseFormatTest() {
        logger.info("[BEGIN] statsResponseFormatTest");

        final Map<String, Object> searchBody = new HashMap<>();
        String response = checkGetMethod(searchBody, "").asString();

        // Verify JSON structure
        assertNotNull(response, "Response should not be null");
        assertTrue(response.contains("response"), "Response should contain 'response' field");
        assertTrue(response.contains("status"), "Response should contain 'status' field");
        assertTrue(response.contains("version"), "Response should contain 'version' field");

        // Verify response can be parsed as JSON
        final Map<String, Object> res = JsonPath.from(response).getMap("response");
        assertNotNull(res, "Response should be valid JSON");

        logger.info("[END] statsResponseFormatTest");
    }

    @Test
    void multipleStatsCallsTest() {
        logger.info("[BEGIN] multipleStatsCallsTest");

        // Call stats endpoint multiple times to ensure consistency
        final Map<String, Object> searchBody = new HashMap<>();

        String response1 = checkGetMethod(searchBody, "").asString();
        Integer status1 = JsonPath.from(response1).get("response.status");
        assertEquals(Integer.valueOf(0), status1, "First call should return status 0");

        String response2 = checkGetMethod(searchBody, "").asString();
        Integer status2 = JsonPath.from(response2).get("response.status");
        assertEquals(Integer.valueOf(0), status2, "Second call should return status 0");

        String response3 = checkGetMethod(searchBody, "").asString();
        Integer status3 = JsonPath.from(response3).get("response.status");
        assertEquals(Integer.valueOf(0), status3, "Third call should return status 0");

        logger.info("All stats calls returned successfully");

        logger.info("[END] multipleStatsCallsTest");
    }
}
