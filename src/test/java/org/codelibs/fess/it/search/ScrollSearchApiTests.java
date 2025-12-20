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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.it.CrawlTestBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/**
 * Integration tests for the Scroll Search API (/api/v1/documents/all)
 * This test requires crawled documents and api.search.scroll to be enabled
 */
@Tag("it")
@Disabled("Requires api.search.scroll to be enabled in fess_config.properties")
public class ScrollSearchApiTests extends CrawlTestBase {
    private static final Logger logger = LogManager.getLogger(ScrollSearchApiTests.class);
    private static final String NAME_PREFIX = "scrollSearchApiTest_";
    private static final String DEFAULT_TESTDATA_PATH = "/tmp/fess-testdata";
    private static final String CRAWL_LABEL = NAME_PREFIX + "_label";
    private static String fileConfigId;
    private static String crawlLabelId;

    @BeforeAll
    protected static void initAll() {
        RestAssured.baseURI = getFessUrl();
        settingTestToken();

        // Create and execute a file crawler
        crawlLabelId = createCrawlLabel();

        createFileConfig();
        logger.info("FileConfig is created");
        refresh();
        fileConfigId = getFileConfigIds(NAME_PREFIX).get(0);

        createJob();
        logger.info("Job is created");
        refresh();

        startJob(NAME_PREFIX);
        waitJob(NAME_PREFIX);
        refresh();
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
        deleteDocuments("label:" + CRAWL_LABEL);
        deleteLabel(crawlLabelId);

        final List<Map<String, Object>> jobLogList = readJobLog(NAME_PREFIX);
        for (Map<String, Object> elem : jobLogList) {
            deleteMethod("/api/admin/joblog/log/" + elem.get("id"));
        }

        final List<Map<String, Object>> crawlingInfoList = readCrawlingInfo(fileConfigId);
        for (Map<String, Object> elem : crawlingInfoList) {
            deleteMethod("/api/admin/crawlinginfo/log/" + elem.get("id"));
        }

        final List<Map<String, Object>> failureUrlList = readFailureUrl(fileConfigId);
        for (Map<String, Object> elem : failureUrlList) {
            deleteMethod("/api/admin/failurelog/log/" + elem.get("id"));
        }

        for (String sId : getSchedulerIds(NAME_PREFIX)) {
            deleteMethod("/api/admin/scheduler/setting/" + sId);
        }

        for (String fId : getFileConfigIds(NAME_PREFIX)) {
            deleteMethod("/api/admin/fileconfig/setting/" + fId);
        }

        deleteTestToken();
    }

    @Test
    public void testScrollSearch_ok() {
        Map<String, String> params = new HashMap<>();
        params.put("q", "*");

        Response response = given().contentType("application/x-ndjson").params(params).when().get("/api/v1/documents/all");

        assertEquals(200, response.getStatusCode());
        String body = response.getBody().asString();
        assertTrue(body != null && body.length() > 0, "Response body should not be empty");
        // NDJSON format: each line is a JSON object
        String[] lines = body.trim().split("\n");
        assertTrue(lines.length > 0, "Should have at least one line in NDJSON response");
    }

    @Test
    public void testScrollSearch_withNum() {
        Map<String, String> params = new HashMap<>();
        params.put("q", "*");
        params.put("num", "5");

        Response response = given().contentType("application/x-ndjson").params(params).when().get("/api/v1/documents/all");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testScrollSearch_withSort() {
        Map<String, String> params = new HashMap<>();
        params.put("q", "*");
        params.put("sort", "content_length.asc");

        Response response = given().contentType("application/x-ndjson").params(params).when().get("/api/v1/documents/all");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testScrollSearch_noMatch() {
        Map<String, String> params = new HashMap<>();
        params.put("q", "zzzzxxxxyyyywwww_nomatch_query");

        Response response = given().contentType("application/x-ndjson").params(params).when().get("/api/v1/documents/all");

        assertEquals(200, response.getStatusCode());
    }

    private static void createFileConfig() {
        final Map<String, Object> requestBody = new HashMap<>();
        final String paths = "file:" + getTestDataPath();
        requestBody.put("name", NAME_PREFIX + "FileConfig");
        requestBody.put("paths", paths);
        requestBody.put("excluded_paths", ".*\\.git.*");
        requestBody.put("max_access_count", 10);
        requestBody.put("num_of_thread", 1);
        requestBody.put("interval_time", 0);
        requestBody.put("boost", 100);
        requestBody.put("permissions", "{role}guest");
        requestBody.put("available", true);
        requestBody.put("sort_order", 0);
        createFileConfig(requestBody);
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
        requestBody.put("script_data", buildFileConfigJobScript(fileConfigId));
        createJob(requestBody);
    }

    private static String createCrawlLabel() {
        Map<String, Object> labelBody = new HashMap<>();
        labelBody.put("name", CRAWL_LABEL);
        labelBody.put("value", CRAWL_LABEL);
        labelBody.put("included_paths", ".*");
        Response response = checkMethodBase(labelBody).post("/api/admin/labeltype/setting");
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertTrue(jsonPath.getBoolean("response.created"));
        assertEquals(0, jsonPath.getInt("response.status"));
        return jsonPath.get("response.id");
    }

    protected static void deleteLabel(String id) {
        checkMethodBase(new HashMap<>()).delete("/api/admin/labeltype/setting/" + id);
    }

    public static String getTestDataPath() {
        return System.getProperty("test.testdata.path", DEFAULT_TESTDATA_PATH);
    }
}
