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
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import io.restassured.response.Response;

/**
 * Integration tests for /api/admin/crawlinginfo endpoints
 */
@Tag("it")
public class CrawlingInfoTests extends CrawlTestBase {
    private static final Logger logger = LogManager.getLogger(CrawlingInfoTests.class);

    private static final String NAME_PREFIX = "crawlingInfoTest_";
    private static final String API_PATH = "/api/admin/crawlinginfo";

    private static String webConfigId;

    @BeforeAll
    protected static void initAll() {
        RestAssured.baseURI = getFessUrl();
        settingTestToken();

        // Create and execute a web crawler to generate crawling info logs
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
        // Clean up crawling info logs
        final List<Map<String, Object>> crawlingInfoList = readCrawlingInfo(webConfigId);
        for (Map<String, Object> elem : crawlingInfoList) {
            deleteMethod(API_PATH + "/log/" + elem.get("id"));
        }

        // Clean up job logs
        final List<Map<String, Object>> jobLogList = readJobLog(NAME_PREFIX);
        for (Map<String, Object> elem : jobLogList) {
            deleteMethod("/api/admin/joblog/log/" + elem.get("id"));
        }

        // Clean up failure URL logs
        final List<Map<String, Object>> failureUrlList = readFailureUrl(webConfigId);
        for (Map<String, Object> elem : failureUrlList) {
            deleteMethod("/api/admin/failurelog/log/" + elem.get("id"));
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
    void crawlingInfoApiTest() {
        logger.info("start crawlingInfoApiTest");
        testReadCrawlingInfo();
        testDeleteCrawlingInfo();
    }

    /**
     * Test reading crawling info logs via various endpoints
     */
    private void testReadCrawlingInfo() {
        // Test basic list endpoint
        final List<Map<String, Object>> logList = readCrawlingInfo(webConfigId);
        logger.info("Crawling info logs: {}", logList.size());
        assertEquals(1, logList.size());

        // Test GET /api/admin/crawlinginfo/logs
        final Map<String, Object> searchBody = new HashMap<>();
        Response response = checkMethodBase(searchBody).get(API_PATH + "/logs");
        response.then()
                .body("response.status", equalTo(0))
                .body("response.logs", notNullValue());

        // Test with pagination
        searchBody.put("size", 10);
        searchBody.put("page", 1);
        response = checkMethodBase(searchBody).get(API_PATH + "/logs");
        response.then()
                .body("response.status", equalTo(0))
                .body("response.logs", notNullValue());

        // Test with session ID filter
        if (!logList.isEmpty()) {
            final String sessionId = (String) logList.get(0).get("session_id");
            assertNotNull(sessionId, "Session ID should not be null");

            final Map<String, Object> filterBody = new HashMap<>();
            filterBody.put("session_id", sessionId);
            response = checkMethodBase(filterBody).get(API_PATH + "/logs");
            response.then()
                    .body("response.status", equalTo(0))
                    .body("response.logs", notNullValue());

            final List<Map<String, Object>> filteredLogs = JsonPath.from(response.asString()).getList("response.logs");
            for (Map<String, Object> log : filteredLogs) {
                assertEquals(sessionId, log.get("session_id"), "All logs should have the same session ID");
            }
            logger.info("Session ID filter test: filtered {} logs", filteredLogs.size());
        }

        // Test GET /api/admin/crawlinginfo/log/{id}
        if (!logList.isEmpty()) {
            final String logId = (String) logList.get(0).get("id");
            response = checkMethodBase(new HashMap<>()).get(API_PATH + "/log/" + logId);
            response.then()
                    .body("response.status", equalTo(0))
                    .body("response.log", notNullValue())
                    .body("response.log.id", equalTo(logId));
            logger.info("Get crawling info log by ID: {}", logId);
        }

        // Test GET with non-existent ID
        final String nonExistentId = "nonexistent_id_12345";
        response = checkMethodBase(new HashMap<>()).get(API_PATH + "/log/" + nonExistentId);
        response.then().body("response.status", equalTo(1));
        logger.info("Get non-existent crawling info log test completed");

        // Test PUT /api/admin/crawlinginfo/logs
        final Map<String, Object> putBody = new HashMap<>();
        putBody.put("size", 10);
        response = checkMethodBase(putBody).put(API_PATH + "/logs");
        response.then()
                .body("response.status", equalTo(0))
                .body("response.logs", notNullValue());
        logger.info("PUT list crawling info logs test completed");

        // Test log structure
        final List<Map<String, Object>> logs = JsonPath.from(checkMethodBase(new HashMap<>()).get(API_PATH + "/logs").asString())
                .getList("response.logs");
        if (!logs.isEmpty()) {
            final Map<String, Object> firstLog = logs.get(0);
            assertTrue(firstLog.containsKey("id"), "Log should have 'id' field");
            assertTrue(firstLog.containsKey("session_id"), "Log should have 'session_id' field");
            logger.info("Crawling info log structure verified: {}", firstLog.keySet());
        }
    }

    /**
     * Test deleting crawling info logs
     */
    private void testDeleteCrawlingInfo() {
        // Test DELETE /api/admin/crawlinginfo/log/{id}
        final List<Map<String, Object>> logsBefore = readCrawlingInfo(webConfigId);
        if (!logsBefore.isEmpty()) {
            final int countBefore = logsBefore.size();
            final String idToDelete = (String) logsBefore.get(0).get("id");
            final Response deleteResponse = deleteMethod(API_PATH + "/log/" + idToDelete);
            deleteResponse.then().body("response.status", equalTo(0));

            refresh();
            final List<Map<String, Object>> logsAfter = readCrawlingInfo(webConfigId);
            assertEquals(countBefore - 1, logsAfter.size(), "Should have one less crawling info log after deletion");
            logger.info("Delete crawling info log test completed");
        }

        // Test DELETE /api/admin/crawlinginfo/all
        final List<Map<String, Object>> logsBeforeAll = readCrawlingInfo(webConfigId);
        final int countBeforeAll = logsBeforeAll.size();

        final Map<String, Object> requestBody = new HashMap<>();
        final Response deleteAllResponse = checkMethodBase(requestBody).delete(API_PATH + "/all");
        deleteAllResponse.then().body("response.status", equalTo(0));

        refresh();
        final List<Map<String, Object>> logsAfterAll = readCrawlingInfo(webConfigId);
        logger.info("Delete all crawling info logs: before={}, after={}", countBeforeAll, logsAfterAll.size());
    }

    /**
     * Create a web config for testing
     */
    private static void createWebConfig() {
        final Map<String, Object> requestBody = new HashMap<>();
        final String urls = "https://www.codelibs.org/" + "\n" + "http://failure.url";
        final String includedUrls = "https://www.codelibs.org/.*" + "\n" + "http://failure.url.*";
        requestBody.put("name", NAME_PREFIX + "WebConfig");
        requestBody.put("urls", urls);
        requestBody.put("included_urls", includedUrls);
        requestBody.put("user_agent", "Mozilla/5.0");
        requestBody.put("depth", 1);
        requestBody.put("num_of_thread", 1);
        requestBody.put("interval_time", 1000);
        requestBody.put("boost", 100);
        requestBody.put("available", true);
        requestBody.put("sort_order", 0);
        createWebConfig(requestBody);
    }

    /**
     * Create a job for testing
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
