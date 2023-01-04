/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.fess.it.CrawlTestBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

/**
 * Integration Tests which need an execution of crawler
 * - /api/admin/joblog
 * - /api/admin/crawlinginfo
 * - /api/admin/failureurl
 * - /api/admin/searchlist
 * */
@Tag("it")
public class CrawlerLogTests extends CrawlTestBase {
    private static final Logger logger = LogManager.getLogger(CrawlerLogTests.class);

    private static final String NAME_PREFIX = "crawlingInfoTest_";

    private static String webConfigId;

    @BeforeAll
    protected static void initAll() {
        RestAssured.baseURI = getFessUrl();
        settingTestToken();

        // create and execute a web crawler
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

        ThreadUtil.sleep(3000);
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
        final List<Map<String, Object>> jobLogList = readJobLog(NAME_PREFIX);
        for (Map<String, Object> elem : jobLogList) {
            deleteMethod("/api/admin/joblog/log/" + elem.get("id"));
        }

        final List<Map<String, Object>> crawlingInfoList = readCrawlingInfo(webConfigId);
        for (Map<String, Object> elem : crawlingInfoList) {
            deleteMethod("/api/admin/crawlinginfo/log/" + elem.get("id"));
        }

        final List<Map<String, Object>> failureUrlList = readFailureUrl(webConfigId);
        for (Map<String, Object> elem : failureUrlList) {
            deleteMethod("/api/admin/failurelog/log/" + elem.get("id"));
        }

        final Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("q", "CodeLibs");
        checkMethodBase(requestBody).delete("/api/admin/searchlist/query");
        refresh();

        for (String sId : getSchedulerIds(NAME_PREFIX)) {
            deleteMethod("/api/admin/scheduler/setting/" + sId);
        }

        for (String wId : getWebConfigIds(NAME_PREFIX)) {
            deleteMethod("/api/admin/webconfig/setting/" + wId);
        }

        deleteTestToken();
    }

    @Test
    void jobLogTest() {
        logger.info("start jobLogTest");
        testReadJobLog();
        testDeleteJobLog();
    }

    @Test
    void crawlingInfoTest() {
        logger.info("start crawlingInfoTest");
        testReadCrawlingInfo();
        testDeleteCrawlingInfo();
    }

    @Test
    void failureUrlTest() {
        logger.info("start failureUrlTest");
        testReadFailureUrl();
        testDeleteFailureUrl();
    }

    @Test
    void searchListTest() {
        logger.info("start searchListTest");
        testReadSearchList();
        testDeleteSearchList();
    }

    /**
     * Methods for a Web Crawling Job
     * */
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

    /**
     * Test for JobLog
     * */
    private void testReadJobLog() {
        final List<Map<String, Object>> logList = readJobLog(NAME_PREFIX);
        logger.info("logList: {}", logList);
        assertEquals(1, logList.size());
    }

    private void testDeleteJobLog() {
        final List<Map<String, Object>> logList = readJobLog(NAME_PREFIX);
        logger.info("logList: {}", logList);
        for (Map<String, Object> elem : logList) {
            deleteMethod("/api/admin/joblog/log/" + elem.get("id")).then().body("response.status", equalTo(0));
        }

        final List<Map<String, Object>> afterList = readJobLog(NAME_PREFIX);
        assertEquals(0, afterList.size()); // check if logs are successfully deleted
    }

    /**
     * Test for CrawlingInfo
     * */
    private void testReadCrawlingInfo() {
        final List<Map<String, Object>> logList = readCrawlingInfo(webConfigId);
        logger.info("logList: {}", logList);
        assertEquals(1, logList.size());
    }

    private void testDeleteCrawlingInfo() {
        final List<Map<String, Object>> logList = readCrawlingInfo(webConfigId);
        logger.info("logList: {}", logList);
        for (Map<String, Object> elem : logList) {
            deleteMethod("/api/admin/crawlinginfo/log/" + elem.get("id")).then().body("response.status", equalTo(0));
        }

        final List<Map<String, Object>> afterList = readCrawlingInfo(webConfigId);
        assertEquals(0, afterList.size()); // check if logs are successfully deleted
    }

    /**
     * Test for FailureUrl
     * */
    private void testReadFailureUrl() {
        final List<Map<String, Object>> logList = readFailureUrl(webConfigId);
        logger.info("logList: {}", logList);
        assertEquals(1, logList.size());
    }

    private void testDeleteFailureUrl() {
        final List<Map<String, Object>> logList = readFailureUrl(webConfigId);
        logger.info("logList: {}", logList);
        for (Map<String, Object> elem : logList) {
            deleteMethod("/api/admin/failureurl/log/" + elem.get("id")).then().body("response.status", equalTo(0));
        }

        final List<Map<String, Object>> afterList = readFailureUrl(webConfigId);
        assertEquals(0, afterList.size()); // check if logs are successfully deleted
    }

    /**
     * Test for SearchList
     * */
    private void testReadSearchList() {
        final List<Map<String, Object>> results = getSearchResults();
        assertTrue(results.size() >= 1);
        logger.info("results: {}", results);
        Map<String, Object> item = results.get(0);
        logger.info("item: {}", item);
        assertTrue(item.containsKey("content_title"));
        assertEquals("<strong>CodeLibs</strong> Project", item.get("content_title"));
    }

    private void testDeleteSearchList() {
        final Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("q", "CodeLibs");

        checkMethodBase(requestBody).delete("/api/admin/searchlist/query").then().body("response.status", equalTo(0));
        refresh();

        final List<Map<String, Object>> results = getSearchResults();
        assertTrue(results.size() == 0);
    }

    private List<Map<String, Object>> getSearchResults() {
        final Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("q", "CodeLibs");

        final String response = checkMethodBase(requestBody).get("/api/admin/searchlist/docs").asString();
        final List<Map<String, Object>> results = JsonPath.from(response).getList("response.docs");
        return results;
    }
}
