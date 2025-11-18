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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.it.CrawlTestBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

/**
 * Integration Tests for /api/admin/failureurl
 */
@Tag("it")
public class FailureUrlTests extends CrawlTestBase {
    private static final Logger logger = LogManager.getLogger(FailureUrlTests.class);

    private static final String NAME_PREFIX = "failureUrlTest_";
    private static final String API_PATH = "/api/admin/failureurl";

    private static String webConfigId;

    @BeforeAll
    protected static void initAll() {
        RestAssured.baseURI = getFessUrl();
        settingTestToken();

        // Create and execute a web crawler to generate failure URL logs
        createWebConfig();
        logger.info("WebConfig is created");
        refresh();
        webConfigId = getWebConfigIds(NAME_PREFIX).get(0);

        createJob();
        logger.info("Job is created: {}", webConfigId);
        refresh();

        startJob(NAME_PREFIX);
        waitJob(NAME_PREFIX);
        refresh();
    }

    @AfterAll
    protected static void tearDownAll() {
        // Clean up failure URL logs
        final Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("size", 1000);
        checkMethodBase(requestBody).delete(API_PATH + "/all");
        refresh();

        // Clean up job logs
        final List<Map<String, Object>> jobLogList = readJobLog(NAME_PREFIX);
        for (Map<String, Object> elem : jobLogList) {
            deleteMethod("/api/admin/joblog/log/" + elem.get("id"));
        }

        // Clean up crawling info
        final List<Map<String, Object>> crawlingInfoList = readCrawlingInfo(webConfigId);
        for (Map<String, Object> elem : crawlingInfoList) {
            deleteMethod("/api/admin/crawlinginfo/log/" + elem.get("id"));
        }

        // Clean up schedulers
        for (String sId : getSchedulerIds(NAME_PREFIX)) {
            deleteMethod("/api/admin/scheduler/setting/" + sId);
        }

        // Clean up web configs
        for (String wId : getWebConfigIds(NAME_PREFIX)) {
            deleteMethod("/api/admin/webconfig/setting/" + wId);
        }

        deleteTestToken();
    }

    @Test
    void crudTest() {
        testReadFailureUrlLogs();
        testReadFailureUrlLog();
        testDeleteFailureUrlLog();
        testDeleteAllFailureUrlLogs();
    }

    @Test
    void searchParametersTest() {
        testSearchByUrl();
        testSearchByErrorCount();
        testSearchByErrorName();
        testPagination();
    }

    /**
     * Test: Read failure URL logs list
     */
    private void testReadFailureUrlLogs() {
        logger.info("[BEGIN] testReadFailureUrlLogs");
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", 100);

        String response = checkMethodBase(searchBody).get(API_PATH + "/logs").asString();
        List<Map<String, Object>> logs = JsonPath.from(response).getList("response.logs");

        logger.info("Failure URL logs: {}", logs);
        assertTrue(logs.size() > 0, "Should have at least one failure URL log");

        // Verify response structure
        Integer status = JsonPath.from(response).get("response.status");
        assertEquals(0, status);

        // Verify log structure
        Map<String, Object> firstLog = logs.get(0);
        assertTrue(firstLog.containsKey("id"), "Log should have id field");
        assertTrue(firstLog.containsKey("url"), "Log should have url field");

        logger.info("[END] testReadFailureUrlLogs");
    }

    /**
     * Test: Read a specific failure URL log
     */
    private void testReadFailureUrlLog() {
        logger.info("[BEGIN] testReadFailureUrlLog");

        // Get a failure URL log ID
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", 1);
        String response = checkMethodBase(searchBody).get(API_PATH + "/logs").asString();
        List<Map<String, Object>> logs = JsonPath.from(response).getList("response.logs");

        if (logs.isEmpty()) {
            logger.warn("No failure URL logs found, skipping testReadFailureUrlLog");
            return;
        }

        String logId = logs.get(0).get("id").toString();

        // Get the specific log
        response = checkMethodBase(searchBody).get(API_PATH + "/log/" + logId).asString();
        Map<String, Object> log = JsonPath.from(response).getMap("response.log");

        logger.info("Failure URL log detail: {}", log);
        assertEquals(logId, log.get("id").toString());
        assertTrue(log.containsKey("url"), "Log should have url field");

        logger.info("[END] testReadFailureUrlLog");
    }

    /**
     * Test: Delete a specific failure URL log
     */
    private void testDeleteFailureUrlLog() {
        logger.info("[BEGIN] testDeleteFailureUrlLog");

        // Get a failure URL log ID
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", 1);
        String response = checkMethodBase(searchBody).get(API_PATH + "/logs").asString();
        List<Map<String, Object>> logs = JsonPath.from(response).getList("response.logs");

        if (logs.isEmpty()) {
            logger.warn("No failure URL logs found, skipping testDeleteFailureUrlLog");
            return;
        }

        String logId = logs.get(0).get("id").toString();
        int initialCount = logs.size();

        // Delete the log
        deleteMethod(API_PATH + "/log/" + logId).then().body("response.status", equalTo(0));
        refresh();

        // Verify deletion
        response = checkMethodBase(searchBody).get(API_PATH + "/logs").asString();
        List<Map<String, Object>> afterLogs = JsonPath.from(response).getList("response.logs");

        boolean logDeleted = afterLogs.stream().noneMatch(log -> logId.equals(log.get("id").toString()));
        assertTrue(logDeleted, "Log should be deleted");

        logger.info("[END] testDeleteFailureUrlLog");
    }

    /**
     * Test: Delete all failure URL logs
     */
    private void testDeleteAllFailureUrlLogs() {
        logger.info("[BEGIN] testDeleteAllFailureUrlLogs");

        // Delete all logs
        final Map<String, Object> requestBody = new HashMap<>();
        checkMethodBase(requestBody).delete(API_PATH + "/all").then().body("response.status", equalTo(0));
        refresh();

        // Verify all logs are deleted
        requestBody.put("size", 100);
        String response = checkMethodBase(requestBody).get(API_PATH + "/logs").asString();
        List<Map<String, Object>> logs = JsonPath.from(response).getList("response.logs");

        assertEquals(0, logs.size(), "All logs should be deleted");

        logger.info("[END] testDeleteAllFailureUrlLogs");
    }

    /**
     * Test: Search failure URL logs by URL parameter
     */
    private void testSearchByUrl() {
        logger.info("[BEGIN] testSearchByUrl");

        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("url", "failure");
        searchBody.put("size", 100);

        String response = checkMethodBase(searchBody).get(API_PATH + "/logs").asString();
        List<Map<String, Object>> logs = JsonPath.from(response).getList("response.logs");

        logger.info("Filtered logs by URL: {}", logs);

        // Verify all returned logs contain the search term in URL
        for (Map<String, Object> log : logs) {
            String url = log.get("url").toString();
            assertTrue(url.contains("failure"), "URL should contain 'failure'");
        }

        logger.info("[END] testSearchByUrl");
    }

    /**
     * Test: Search failure URL logs by error count range
     */
    private void testSearchByErrorCount() {
        logger.info("[BEGIN] testSearchByErrorCount");

        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("error_count_min", 1);
        searchBody.put("error_count_max", 100);
        searchBody.put("size", 100);

        String response = checkMethodBase(searchBody).get(API_PATH + "/logs").asString();
        List<Map<String, Object>> logs = JsonPath.from(response).getList("response.logs");

        logger.info("Filtered logs by error count: {}", logs);

        logger.info("[END] testSearchByErrorCount");
    }

    /**
     * Test: Search failure URL logs by error name
     */
    private void testSearchByErrorName() {
        logger.info("[BEGIN] testSearchByErrorName");

        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("error_name", "Exception");
        searchBody.put("size", 100);

        String response = checkMethodBase(searchBody).get(API_PATH + "/logs").asString();
        List<Map<String, Object>> logs = JsonPath.from(response).getList("response.logs");

        logger.info("Filtered logs by error name: {}", logs);

        logger.info("[END] testSearchByErrorName");
    }

    /**
     * Test: Pagination of failure URL logs
     */
    private void testPagination() {
        logger.info("[BEGIN] testPagination");

        // Get first page
        final Map<String, Object> searchBody = new HashMap<>();
        searchBody.put("size", 5);
        searchBody.put("page", 1);

        String response = checkMethodBase(searchBody).get(API_PATH + "/logs").asString();
        List<Map<String, Object>> page1 = JsonPath.from(response).getList("response.logs");

        logger.info("Page 1: {}", page1);

        // Get second page
        searchBody.put("page", 2);
        response = checkMethodBase(searchBody).get(API_PATH + "/logs").asString();
        List<Map<String, Object>> page2 = JsonPath.from(response).getList("response.logs");

        logger.info("Page 2: {}", page2);

        logger.info("[END] testPagination");
    }

    /**
     * Helper: Create a web crawler config
     */
    private static void createWebConfig() {
        final Map<String, Object> requestBody = new HashMap<>();
        final String urls = "http://failure.test.url";
        final String includedUrls = "http://failure.test.url.*";
        requestBody.put("name", NAME_PREFIX + "WebConfig");
        requestBody.put("urls", urls);
        requestBody.put("included_urls", includedUrls);
        requestBody.put("user_agent", "Mozilla/5.0");
        requestBody.put("depth", 0);
        requestBody.put("max_access_count", 1L); // Already minimal
        requestBody.put("num_of_thread", 1);
        requestBody.put("interval_time", 0); // No delay between requests
        requestBody.put("boost", 100);
        requestBody.put("available", true);
        requestBody.put("sort_order", 0);
        createWebConfig(requestBody);
    }

    /**
     * Helper: Create a crawling job
     */
    private static void createJob() {
        final Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", NAME_PREFIX + "Scheduler");
        requestBody.put("target", "all");
        requestBody.put("script_type", "groovy");
        requestBody.put("sort_order", 0);
        requestBody.put("crawler", true);
        requestBody.put("job_logging", true);
        requestBody.put("available", true);
        requestBody.put("script_data", buildWebConfigJobScript(webConfigId));
        createJob(requestBody);
    }
}
