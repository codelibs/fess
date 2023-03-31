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
package org.codelibs.fess.it.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/**
 * Integration Tests which need an execution of crawler
 * - /api/v1/documents
 * */
@Tag("it")
public class SearchApiTests extends CrawlTestBase {
    private static final Logger logger = LogManager.getLogger(SearchApiTests.class);
    private static final String NAME_PREFIX = "searchApiTest_";
    private static final String DEFAULT_TESTDATA_PATH = "/tmp/fess-testdata";
    private static final String CRAWL_LABEL = NAME_PREFIX + "_label";
    private static final String TEST_LABEL = "tools";
    private static String fileConfigId;
    private static String labelId;
    private static String crawlLabelId;

    @BeforeAll
    protected static void initAll() {
        RestAssured.baseURI = getFessUrl();
        settingTestToken();

        // create and execute a file crawler
        labelId = createLabel();
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
        deleteLabel(labelId);
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

        final Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("q", "Example Domain");
        checkMethodBase(requestBody).delete("/api/admin/searchlist/query");
        refresh();

        for (String sId : getSchedulerIds(NAME_PREFIX)) {
            deleteMethod("/api/admin/scheduler/setting/" + sId);
        }

        for (String fId : getFileConfigIds(NAME_PREFIX)) {
            deleteMethod("/api/admin/fileconfig/setting/" + fId);
        }

        deleteTestToken();
    }

    @Test
    public void searchTestWith1Word() throws Exception {
        String query = "java";
        Map<String, String> params = new HashMap<>();
        params.put("q", query);
        params.put("num", "100");
        String response = checkMethodBase(new HashMap<>()).params(params).get("/api/v1/documents").asString();
        assertTrue(JsonPath.from(response).getInt("record_count") > 0);
        List<Map<String, Object>> docs = JsonPath.from(response).getList("data");
        for (Map<String, Object> doc : docs) {
            assertTrue(doc.entrySet().stream().anyMatch(entry -> entry.getValue().toString().toLowerCase().contains(query.toLowerCase())),
                    doc.toString());
        }
    }

    @Test
    public void searchTestWithMultipleWord() throws Exception {
        String query = "java public";
        Map<String, String> params = new HashMap<>();
        params.put("q", query);
        params.put("num", "100");
        String response = checkMethodBase(new HashMap<>()).params(params).get("/api/v1/documents").asString();
        assertTrue(JsonPath.from(response).getInt("record_count") > 0);
        List<Map<String, Object>> docs = JsonPath.from(response).getList("data");
        for (Map<String, Object> doc : docs) {
            for (String keyword : query.split(" ")) {
                assertTrue(doc.entrySet().stream()
                        .anyMatch(entry -> entry.getValue().toString().toLowerCase().contains(keyword.toLowerCase())), doc.toString());
            }
        }
    }

    @Test
    public void searchTestWithAndOperation() throws Exception {
        String query = "java AND public";
        Map<String, String> params = new HashMap<>();
        params.put("q", query);
        params.put("num", "100");
        String response = checkMethodBase(new HashMap<>()).params(params).get("/api/v1/documents").asString();
        assertTrue(JsonPath.from(response).getInt("record_count") > 0);
        List<Map<String, Object>> docs = JsonPath.from(response).getList("data");
        for (Map<String, Object> doc : docs) {
            for (String keyword : query.split(" ")) {
                if (keyword.equals("AND")) {
                    continue;
                }
                assertTrue(doc.entrySet().stream()
                        .anyMatch(entry -> entry.getValue().toString().toLowerCase().contains(keyword.toLowerCase())), doc.toString());
            }
        }
    }

    @Test
    public void searchTestWithOrOperation() throws Exception {
        String query = "java OR public";

        Map<String, String> andParams = new HashMap<>();
        andParams.put("q", query.replace("OR", "AND"));
        String andResponse = checkMethodBase(new HashMap<>()).params(andParams).get("/api/v1/documents").asString();
        int andRecordCount = JsonPath.from(andResponse).getInt("record_count");

        Map<String, String> params = new HashMap<>();
        params.put("q", query);
        params.put("num", "100");
        String response = checkMethodBase(new HashMap<>()).params(params).get("/api/v1/documents").asString();
        assertTrue(JsonPath.from(response).getInt("record_count") > andRecordCount);
        List<Map<String, Object>> docs = JsonPath.from(response).getList("data");
        for (Map<String, Object> doc : docs) {
            boolean match = false;
            for (String keyword : query.split(" ")) {
                if (keyword.equals("OR")) {
                    continue;
                }
                if (doc.entrySet().stream().anyMatch(entry -> entry.getValue().toString().toLowerCase().contains(keyword.toLowerCase()))) {
                    match = true;
                    break;
                }
            }
            assertTrue(match, doc.toString());
        }
    }

    @Test
    public void searchTestWithNotOperation() throws Exception {
        String query = "java NOT public";

        Map<String, String> params = new HashMap<>();
        params.put("q", query);
        params.put("num", "100");
        String response = checkMethodBase(new HashMap<>()).params(params).get("/api/v1/documents").asString();
        assertTrue(JsonPath.from(response).getInt("record_count") > 0);
        List<Map<String, Object>> docs = JsonPath.from(response).getList("data");
        for (Map<String, Object> doc : docs) {
            String[] keywords = query.split(" ");
            assertTrue(doc.entrySet().stream()
                    .anyMatch(entry -> entry.getValue().toString().toLowerCase().contains(keywords[0].toLowerCase())), doc.toString());
            assertFalse(doc.entrySet().stream()
                    .anyMatch(entry -> entry.getValue().toString().toLowerCase().contains(keywords[2].toLowerCase())), doc.toString());
        }
    }

    @Test
    public void searchTestWithLabel() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("fields.label", TEST_LABEL);
        params.put("num", "100");
        String response = checkMethodBase(new HashMap<>()).params(params).get("/api/v1/documents").asString();
        assertTrue(JsonPath.from(response).getInt("record_count") > 0);
        List<Map<String, Object>> docs = JsonPath.from(response).getList("data");
        for (Map<String, Object> doc : docs) {
            assertTrue(doc.get("url").toString().toLowerCase().contains(TEST_LABEL), doc.toString());
        }
    }

    @Test
    public void searchTestWithFieldQery() throws Exception {
        String query = "content:java";
        Map<String, String> params = new HashMap<>();
        params.put("q", query);
        params.put("num", "100");
        String response = checkMethodBase(new HashMap<>()).params(params).get("/api/v1/documents").asString();
        assertTrue(JsonPath.from(response).getInt("record_count") > 0);
        List<Map<String, Object>> docs = JsonPath.from(response).getList("data");
        for (Map<String, Object> doc : docs) {
            String[] fieldAndValue = query.split(":");
            assertTrue(doc.get("content_description").toString().toLowerCase().contains(fieldAndValue[1]), doc.toString());
        }
    }

    @Test
    public void searchTestWithSort() throws Exception {
        String sortField = "content_length";
        Map<String, String> params = new HashMap<>();
        params.put("q", "*");
        params.put("sort", sortField + ".asc");
        params.put("num", "100");
        String response = checkMethodBase(new HashMap<>()).params(params).get("/api/v1/documents").asString();
        assertTrue(JsonPath.from(response).getInt("record_count") > 10);
        List<Map<String, Object>> docs = JsonPath.from(response).getList("data");
        int prevVal = 0;
        for (Map<String, Object> doc : docs) {
            int sortValue = Integer.parseInt(doc.get(sortField).toString());
            assertTrue(sortValue >= prevVal);
            prevVal = sortValue;
        }
    }

    @Test
    public void searchTestWithWildcard() throws Exception {
        String field = "filetype";
        String query = "others";
        String wcQuery1 = "othe*";
        String wcQuery2 = "oth??s";
        Map<String, String> params = new HashMap<>();
        params.put("q", field + ":" + query);
        params.put("num", "100");
        String response = checkMethodBase(new HashMap<>()).params(params).get("/api/v1/documents").asString();
        int recordCount = JsonPath.from(response).getInt("record_count");
        assertTrue(recordCount > 0);

        Map<String, String> wcParams1 = new HashMap<>();
        wcParams1.put("q", field + ":" + wcQuery1);
        wcParams1.put("num", "100");
        String wcResponse1 = checkMethodBase(new HashMap<>()).params(wcParams1).get("/api/v1/documents").asString();
        int wcRecordCount1 = JsonPath.from(wcResponse1).getInt("record_count");
        assertEquals(recordCount, wcRecordCount1);

        Map<String, String> wcParams2 = new HashMap<>();
        wcParams2.put("q", field + ":" + wcQuery2);
        wcParams2.put("num", "100");
        String wcResponse2 = checkMethodBase(new HashMap<>()).params(wcParams2).get("/api/v1/documents").asString();
        int wcRecordCount2 = JsonPath.from(wcResponse2).getInt("record_count");
        assertEquals(recordCount, wcRecordCount2);

        List<Map<String, Object>> docs1 = JsonPath.from(wcResponse1).getList("data");
        for (Map<String, Object> doc : docs1) {
            assertTrue(doc.get(field).toString().toLowerCase().contains(query), doc.toString());
        }
        List<Map<String, Object>> docs2 = JsonPath.from(wcResponse2).getList("data");
        for (Map<String, Object> doc : docs2) {
            assertTrue(doc.get(field).toString().toLowerCase().contains(query), doc.toString());
        }
    }

    @Test
    public void searchTestWithRange() throws Exception {
        String field = "content_length";
        int from = 100;
        int to = 1000;
        Map<String, String> params = new HashMap<>();
        params.put("q", field + ":[" + from + " TO " + to + "]");
        params.put("num", "100");
        String response = checkMethodBase(new HashMap<>()).params(params).get("/api/v1/documents").asString();
        assertTrue(JsonPath.from(response).getInt("record_count") > 0);
        List<Map<String, Object>> docs = JsonPath.from(response).getList("data");
        for (Map<String, Object> doc : docs) {
            int value = Integer.parseInt(doc.get(field).toString());
            assertTrue(value >= from);
            assertTrue(value <= to);
        }
    }

    @Test
    public void searchTestWithBoost() throws Exception {
        String field = "content";
        String query1 = "java";
        String query2 = "sample";
        Map<String, String> params = new HashMap<>();
        params.put("q", field + ":" + query1 + "^1000 OR " + field + ":" + query2);
        params.put("num", "100");
        String response = checkMethodBase(new HashMap<>()).params(params).get("/api/v1/documents").asString();
        assertTrue(JsonPath.from(response).getInt("record_count") > 0);
        List<Map<String, Object>> docs = JsonPath.from(response).getList("data");
        boolean switchFlg = false;
        for (Map<String, Object> doc : docs) {
            if (!switchFlg) {
                boolean contains = doc.get("content_description").toString().toLowerCase().contains(query1);
                if (!contains) {
                    switchFlg = true;
                }
            } else {
                assertTrue(doc.get("content_description").toString().toLowerCase().contains(query2), doc.toString());
            }
        }
    }

    @Test
    public void searchTestWithFuzzy() throws Exception {
        String field = "content";
        String query = "java";
        Map<String, String> params = new HashMap<>();
        params.put("q", field + ":" + query);
        params.put("num", "100");
        String response = checkMethodBase(new HashMap<>()).params(params).get("/api/v1/documents").asString();
        int recordCount = JsonPath.from(response).getInt("record_count");
        assertTrue(recordCount > 0);

        Map<String, String> fuzzyParams1 = new HashMap<>();
        fuzzyParams1.put("q", field + ":" + query + "~");
        fuzzyParams1.put("num", "100");
        String fuzzyResponse1 = checkMethodBase(new HashMap<>()).params(fuzzyParams1).get("/api/v1/documents").asString();
        int wcRecordCount1 = JsonPath.from(fuzzyResponse1).getInt("record_count");
        assertTrue(recordCount < wcRecordCount1);
    }

    @Test
    public void searchTestWithInUrl() throws Exception {
        String query = "tools";
        Map<String, String> params = new HashMap<>();
        params.put("q", "inurl:" + query);
        params.put("num", "100");
        String response = checkMethodBase(new HashMap<>()).params(params).get("/api/v1/documents").asString();
        assertTrue(JsonPath.from(response).getInt("record_count") > 0);
        List<Map<String, Object>> docs = JsonPath.from(response).getList("data");
        for (Map<String, Object> doc : docs) {
            assertTrue(doc.get("url").toString().toLowerCase().contains(query), doc.toString());
        }
    }

    private static void createFileConfig() {
        final Map<String, Object> requestBody = new HashMap<>();
        final String paths = "file:" + getTestDataPath();
        requestBody.put("name", NAME_PREFIX + "FileConfig");
        requestBody.put("paths", paths);
        requestBody.put("excluded_paths", ".*\\.git.*");
        requestBody.put("max_access_count", 100);
        requestBody.put("num_of_thread", 1);
        requestBody.put("interval_time", 100);
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

    private static String createLabel() {
        Map<String, Object> labelBody = new HashMap<>();
        labelBody.put("name", TEST_LABEL);
        labelBody.put("value", TEST_LABEL);
        labelBody.put("included_paths", ".*tools.*");
        Response response = checkMethodBase(labelBody).put("/api/admin/labeltype/setting");
        JsonPath jsonPath = JsonPath.from(response.asString());
        assertTrue(jsonPath.getBoolean("response.created"));
        assertEquals(0, jsonPath.getInt("response.status"));
        return jsonPath.get("response.id");
    }

    private static String createCrawlLabel() {
        Map<String, Object> labelBody = new HashMap<>();
        labelBody.put("name", CRAWL_LABEL);
        labelBody.put("value", CRAWL_LABEL);
        labelBody.put("included_paths", ".*");
        Response response = checkMethodBase(labelBody).put("/api/admin/labeltype/setting");
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

    protected String getApiPath() {
        return "json";
    }
}
