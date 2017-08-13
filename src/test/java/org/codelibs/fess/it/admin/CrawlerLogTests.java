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
package org.codelibs.fess.it.admin;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.Constants;
import org.codelibs.fess.it.ITBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/**
 * Integration Tests which need an execution of crawler
 * - /api/admin/joblog
 * - /api/admin/crawlinginfo
 * - /api/admin/failureurl
 * - /api/admin/searchlist
 * */
@Tag("it")
public class CrawlerLogTests extends ITBase {
    private static final Logger logger = LoggerFactory.getLogger(CrawlerLogTests.class);

    private static final String NAME_PREFIX = "crawlingInfoTest_";

    private static String webConfigId;

    @BeforeAll
    protected static void initAll() {
        RestAssured.baseURI = getFessUrl();
        settingTestToken();

        // create and execute a web crawler
        try {
            createWebConfig();
            logger.info("WebConfig is created");
            refresh();
            webConfigId = getWebConfigIds().get(0);

            createJob();
            logger.info("Job is created");
            refresh();

            startJob();

            waitJob();
            refresh();
        } catch (InterruptedException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @BeforeEach
    protected void init() {
        refresh();
    }

    @AfterEach
    protected void tearDown() {
    }

    @AfterAll
    protected static void tearDownAll() {
        final List<Map<String, Object>> jobLogList = readJobLog();
        for (Map<String, Object> elem : jobLogList) {
            deleteMethod("/api/admin/joblog/log/" + elem.get("id"));
        }

        final List<Map<String, Object>> crawlingInfoList = readCrawlingInfo();
        for (Map<String, Object> elem : crawlingInfoList) {
            deleteMethod("/api/admin/crawlinginfo/log/" + elem.get("id"));
        }

        final List<Map<String, Object>> failureUrlList = readFailureUrl();
        for (Map<String, Object> elem : failureUrlList) {
            deleteMethod("/api/admin/failurelog/log/" + elem.get("id"));
        }

        final Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("q", "Example Domain");
        checkMethodBase(requestBody).delete("/api/admin/searchlist/query");
        refresh();

        for (String sId : getSchedulerIds()) {
            deleteMethod("/api/admin/scheduler/setting/" + sId);
        }

        for (String wId : getWebConfigIds()) {
            deleteMethod("/api/admin/webconfig/setting/" + wId);
        }

        deleteTestToken();
    }

    @Test
    void jobLogTest() {
        testReadJobLog();
        testDeleteJobLog();
    }

    @Test
    void crawlingInfoTest() {
        testReadCrawlingInfo();
        testDeleteCrawlingInfo();
    }

    @Test
    void failureUrlTest() {
        testReadFailureUrl();
        testDeleteFailureUrl();
    }

    @Test
    void searchListTest() {
        testReadSearchList();
        testDeleteSearchList();
    }

    /**
     * Methods for a Web Crawling Job
     * */
    private static void createWebConfig() {
        final Map<String, Object> requestBody = new HashMap<>();
        final String urls = "http://example.com" + "\n" + "http://failure.url";
        requestBody.put("name", NAME_PREFIX + "WebConfig");
        requestBody.put("urls", urls);
        requestBody.put("user_agent", "Mozilla/5.0");
        requestBody.put("depth", 1);
        requestBody.put("num_of_thread", 1);
        requestBody.put("interval_time", 1000);
        requestBody.put("boost", 100);
        requestBody.put("available", true);
        requestBody.put("sort_order", 0);

        checkMethodBase(requestBody).put("/api/admin/webconfig/setting").then().body("response.created", equalTo(true))
                .body("response.status", equalTo(0));
    }

    private static void createJob() {
        final Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", NAME_PREFIX + "Scheduler");
        requestBody.put("target", "all");
        requestBody.put("script_type", "groovy");
        requestBody.put("sort_order", 0);
        requestBody.put("crawler", Constants.ON);
        requestBody.put("job_logging", Constants.ON);
        requestBody.put("available", Constants.ON);
        requestBody.put("script_data", buildJobScript());

        checkMethodBase(requestBody).put("/api/admin/scheduler/setting").then().body("response.created", equalTo(true))
                .body("response.status", equalTo(0));
    }

    private static void startJob() {
        for (int i = 0; i < 30; i++) {
            final Map<String, Object> requestBody = new HashMap<>();
            final String schedulerId = getSchedulerIds().get(0);
            final Response response = checkMethodBase(requestBody).post("/api/admin/scheduler/" + schedulerId + "/start");
            if (response.getBody().jsonPath().getInt("response.status") == 0) {
                logger.info("Start scheduler \"" + schedulerId + "\"");
                return;
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                // ignore
            }
        }
        assertTrue(false, "could not start job.");
    }

    private static void waitJob() throws InterruptedException {
        Boolean isRunning = false;
        int count = 0;

        while (count < 300 && !isRunning) { // Wait until the crawler starts
            Thread.sleep(500);
            count++;
            final Map<String, Object> scheduler = getSchedulerItem();
            assertTrue(scheduler.containsKey("running"));
            isRunning = (Boolean) scheduler.get("running");
        }
        if (300 <= count) {
            logger.info("Time out: Failed to start crawler)");
            assertTrue(false); // Time Out
        }

        logger.info("Crawler is running");
        count = 0;
        isRunning = true;
        while (count < 300 && isRunning) { // Wait until the crawler terminates
            Thread.sleep(1000);
            count++;
            final Map<String, Object> scheduler = getSchedulerItem();
            assertTrue(scheduler.containsKey("running"));
            isRunning = (Boolean) scheduler.get("running");

        }
        if (300 <= count) {
            logger.info("Time out: Crawler takes too much time");
            assertTrue(false); // Time Out
        }

        logger.info("Crawler terminated");
    }

    /**
     * Test for JobLog
     * */
    private void testReadJobLog() {
        final List<Map<String, Object>> logList = readJobLog();
        assertEquals(1, logList.size());
    }

    private void testDeleteJobLog() {
        final List<Map<String, Object>> logList = readJobLog();
        for (Map<String, Object> elem : logList) {
            deleteMethod("/api/admin/joblog/log/" + elem.get("id")).then().body("response.status", equalTo(0));
        }

        final List<Map<String, Object>> afterList = readJobLog();
        assertEquals(0, afterList.size()); // check if logs are successfully deleted
    }

    private static List<Map<String, Object>> readJobLog() {
        final List<Map<String, Object>> logList = readLogItems("joblog");
        final List<Map<String, Object>> resList = new ArrayList<>();
        for (Map<String, Object> elem : logList) {
            if (elem.containsKey("job_name") && elem.get("job_name").equals(NAME_PREFIX + "Scheduler")) {
                resList.add(elem);
            }
        }
        return resList;
    }

    /**
     * Test for CrawlingInfo
     * */
    private void testReadCrawlingInfo() {
        final List<Map<String, Object>> logList = readCrawlingInfo();
        assertEquals(1, logList.size());
    }

    private void testDeleteCrawlingInfo() {
        final List<Map<String, Object>> logList = readCrawlingInfo();
        for (Map<String, Object> elem : logList) {
            deleteMethod("/api/admin/crawlinginfo/log/" + elem.get("id")).then().body("response.status", equalTo(0));
        }

        final List<Map<String, Object>> afterList = readCrawlingInfo();
        assertEquals(0, afterList.size()); // check if logs are successfully deleted
    }

    private static List<Map<String, Object>> readCrawlingInfo() {
        final List<Map<String, Object>> logList = readLogItems("crawlinginfo");
        final List<Map<String, Object>> resList = new ArrayList<>();
        for (Map<String, Object> elem : logList) {
            if (elem.containsKey("session_id") && elem.get("session_id").equals(webConfigId)) {
                resList.add(elem);
            }
        }
        return resList;
    }

    /**
     * Test for FailureUrl
     * */
    private void testReadFailureUrl() {
        final List<Map<String, Object>> logList = readFailureUrl();
        assertEquals(1, logList.size());
    }

    private void testDeleteFailureUrl() {
        final List<Map<String, Object>> logList = readFailureUrl();
        for (Map<String, Object> elem : logList) {
            deleteMethod("/api/admin/failureurl/log/" + elem.get("id")).then().body("response.status", equalTo(0));
        }

        final List<Map<String, Object>> afterList = readFailureUrl();
        assertEquals(0, afterList.size()); // check if logs are successfully deleted
    }

    private static List<Map<String, Object>> readFailureUrl() {
        final List<Map<String, Object>> logList = readLogItems("failureurl");
        final List<Map<String, Object>> resList = new ArrayList<>();
        for (Map<String, Object> elem : logList) {
            if (elem.containsKey("thread_name") && elem.get("thread_name").toString().startsWith("Crawler-" + webConfigId)) {
                resList.add(elem);
            }
        }
        return resList;
    }

    /**
     * Test for SearchList
     * */
    private void testReadSearchList() {
        final List<Map<String, Object>> results = getSearchResults();
        assertTrue(results.size() >= 1);
        Map<String, Object> item = results.get(0);
        assertTrue(item.containsKey("content_title"));
        assertEquals("Example Domain", item.get("content_title"));
    }

    private void testDeleteSearchList() {
        final Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("q", "Example Domain");

        checkMethodBase(requestBody).delete("/api/admin/searchlist/query").then().body("response.status", equalTo(0));
        refresh();

        final List<Map<String, Object>> results = getSearchResults();
        assertTrue(results.size() == 0);
    }

    private List<Map<String, Object>> getSearchResults() {
        final Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("q", "Example Domain");

        final String response = checkMethodBase(requestBody).get("/api/admin/searchlist/docs").asString();
        final List<Map<String, Object>> results = JsonPath.from(response).getList("response.docs");
        return results;
    }

    /**
     * Utilities
     * */
    private static List<Map<String, Object>> readLogItems(final String apiName) {
        final Map<String, Object> searchBody = new HashMap<>();
        final String response = checkMethodBase(searchBody).get("/api/admin/" + apiName + "/logs").asString();
        final List<Map<String, Object>> itemList = JsonPath.from(response).getList("response.logs");
        return itemList;
    }

    private static String getJsonResponse(final String path) {
        final Map<String, Object> searchBody = new HashMap<>();
        final String response = checkMethodBase(searchBody).get(path).asString();
        return response;
    }

    private static String getResponsePath() {
        return "response.settings.findAll {it.name.startsWith(\"" + NAME_PREFIX + "\")}";
    }

    private static List<String> getWebConfigIds() {
        final String response = getJsonResponse("/api/admin/webconfig/settings");
        final List<String> idList = JsonPath.from(response).getList(getResponsePath() + ".id");
        return idList;
    }

    private static List<String> getSchedulerIds() {
        final String response = getJsonResponse("/api/admin/scheduler/settings");
        final List<String> idList = JsonPath.from(response).getList(getResponsePath() + ".id");
        return idList;
    }

    private static Map<String, Object> getSchedulerItem() {
        final String response = getJsonResponse("/api/admin/scheduler/settings");
        final List<Map<String, Object>> itemList = JsonPath.from(response).getList(getResponsePath());
        assertEquals(1, itemList.size());
        return itemList.get(0);
    }

    private static String buildJobScript() {
        return String.format("return container.getComponent(\"crawlJob\")" + ".logLevel(\"info\")" + ".sessionId(\"%s\")"
                + ".webConfigIds([\"%s\"] as String[])" + ".jobExecutor(executor).execute();", webConfigId, webConfigId);
    }

    private static Response deleteMethod(final String path) {
        return given().header("Authorization", getTestToken()).delete(path);
    }
}
