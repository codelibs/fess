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
package org.codelibs.fess.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.ThreadUtil;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class CrawlTestBase extends ITBase {
    private static final Logger logger = LogManager.getLogger(CrawlTestBase.class);

    private static final String DOC_INDEX_NAME = "fess.search";

    protected static void createJob(final Map<String, Object> requestBody) {
        checkMethodBase(requestBody).put("/api/admin/scheduler/setting").then().body("response.created", equalTo(true))
                .body("response.status", equalTo(0));
    }

    protected static void startJob(final String namePrefix) {
        for (int i = 0; i < 30; i++) {
            final Map<String, Object> requestBody = new HashMap<>();
            final String schedulerId = getSchedulerIds(namePrefix).get(0);
            final Response response = checkMethodBase(requestBody).post("/api/admin/scheduler/" + schedulerId + "/start");
            if (response.getBody().jsonPath().getInt("response.status") == 0) {
                logger.info("Start scheduler \"{}\"", schedulerId);
                return;
            }
            ThreadUtil.sleep(1000L);
        }
        fail("could not start job.");
    }

    protected static void waitJob(final String namePrefix) {
        Boolean isRunning = false;
        int count = 0;

        while (count < 300 && !isRunning) { // Wait until the crawler starts
            ThreadUtil.sleep(500);
            count++;
            final Map<String, Object> scheduler = getSchedulerItem(namePrefix);
            assertTrue(scheduler.containsKey("running"));
            isRunning = (Boolean) scheduler.get("running");
        }
        if (300 <= count) {
            logger.info("Time out: Failed to start crawler)");
            fail(); // Time Out
        }

        logger.info("Crawler is running");
        count = 0;
        isRunning = true;
        while (count < 300 && isRunning) { // Wait until the crawler terminates
            ThreadUtil.sleep(1000);
            count++;
            final Map<String, Object> scheduler = getSchedulerItem(namePrefix);
            assertTrue(scheduler.containsKey("running"));
            isRunning = (Boolean) scheduler.get("running");

        }
        if (300 <= count) {
            logger.info("Time out: Crawler takes too much time");
            //TODO fail(); // Time Out
        }

        logger.info("Crawler terminated");
    }

    protected static String createWebConfig(final Map<String, Object> requestBody) {
        String response = checkMethodBase(requestBody).put("/api/admin/webconfig/setting").asString();
        JsonPath jsonPath = JsonPath.from(response);
        assertTrue(jsonPath.getBoolean("response.created"));
        assertEquals(0, jsonPath.getInt("response.status"));
        return jsonPath.getString("response.id");
    }

    protected static List<String> getWebConfigIds(final String namePrefix) {
        final String response = getJsonResponse("/api/admin/webconfig/settings");
        final List<String> idList = JsonPath.from(response).getList(getResponsePath(namePrefix) + ".id");
        return idList;
    }

    protected static String createFileConfig(final Map<String, Object> requestBody) {
        String response = checkMethodBase(requestBody).put("/api/admin/fileconfig/setting").asString();
        JsonPath jsonPath = JsonPath.from(response);
        assertTrue(jsonPath.getBoolean("response.created"));
        assertEquals(0, jsonPath.getInt("response.status"));
        return jsonPath.getString("response.id");
    }

    protected static List<String> getFileConfigIds(final String namePrefix) {
        final String response = getJsonResponse("/api/admin/fileconfig/settings");
        final List<String> idList = JsonPath.from(response).getList(getResponsePath(namePrefix) + ".id");
        return idList;
    }

    protected static String getResponsePath(final String namePrefix) {
        return "response.settings.findAll {it.name.startsWith(\"" + namePrefix + "\")}";
    }

    protected static String getJsonResponse(final String path) {
        final Map<String, Object> searchBody = new HashMap<>();
        final String response = checkMethodBase(searchBody).get(path).asString();
        return response;
    }

    protected static String buildWebConfigJobScript(final String webCofigId) {
        return String.format("return container.getComponent(\"crawlJob\")" + ".logLevel(\"info\")" + ".sessionId(\"%s\")"
                + ".webConfigIds([\"%s\"] as String[])" + ".jobExecutor(executor).execute();", webCofigId, webCofigId);
    }

    protected static String buildFileConfigJobScript(final String fileConfigId) {
        return String.format("return container.getComponent(\"crawlJob\")" + ".logLevel(\"info\")" + ".sessionId(\"%s\")"
                + ".fileConfigIds([\"%s\"] as String[])" + ".jobExecutor(executor).execute();", fileConfigId, fileConfigId);
    }

    protected static Map<String, Object> getSchedulerItem(final String namePrefix) {
        final String response = getJsonResponse("/api/admin/scheduler/settings");
        final List<Map<String, Object>> itemList = JsonPath.from(response).getList(getResponsePath(namePrefix));
        assertEquals(1, itemList.size());
        return itemList.get(0);
    }

    protected static List<String> getSchedulerIds(final String namePrefix) {
        final String response = getJsonResponse("/api/admin/scheduler/settings");
        final List<String> idList = JsonPath.from(response).getList(getResponsePath(namePrefix) + ".id");
        return idList;
    }

    protected static List<Map<String, Object>> readJobLog(final String namePrefix) {
        final List<Map<String, Object>> logList = readLogItems("joblog");
        final List<Map<String, Object>> resList = new ArrayList<>();
        for (Map<String, Object> elem : logList) {
            if (elem.containsKey("job_name") && elem.get("job_name").equals(namePrefix + "Scheduler")) {
                resList.add(elem);
            }
        }
        return resList;
    }

    protected static List<Map<String, Object>> readLogItems(final String apiName) {
        final Map<String, Object> searchBody = new HashMap<>();
        final String response = checkMethodBase(searchBody).get("/api/admin/" + apiName + "/logs").asString();
        final List<Map<String, Object>> itemList = JsonPath.from(response).getList("response.logs");
        logger.info("itemList: {}", itemList);
        return itemList;
    }

    protected static Response deleteMethod(final String path) {
        return given().contentType("application/json").header("Authorization", getTestToken()).delete(path);
    }

    protected static void deleteDocuments(final String queryString) {
        List<String> docIds = new ArrayList<>();
        Response response = given().contentType("application/json").param("scroll", "1m").param("q", queryString)
                .get(getEsUrl() + "/" + DOC_INDEX_NAME + "/_search");
        JsonPath jsonPath = JsonPath.from(response.asString());
        String scrollId = jsonPath.getString("_scroll_id");
        while (true) {
            List<String> resultIds = jsonPath.getList("hits.hits._id");
            if (resultIds.size() == 0) {
                break;
            }
            docIds.addAll(resultIds);
            Map<String, Object> scrollBody = new HashMap<>();
            scrollBody.put("scroll", "1m");
            scrollBody.put("scroll_id", scrollId);
            response = given().contentType("application/json").body(scrollBody).get(getEsUrl() + "/_search/scroll");
            jsonPath = JsonPath.from(response.asString());
        }

        for (String docId : docIds) {
            given().contentType("application/json").delete(getEsUrl() + "/" + DOC_INDEX_NAME + "/" + docId);
        }
    }

    protected static List<Map<String, Object>> readCrawlingInfo(final String configId) {
        final List<Map<String, Object>> logList = readLogItems("crawlinginfo");
        final List<Map<String, Object>> resList = new ArrayList<>();
        for (Map<String, Object> elem : logList) {
            logger.info("config_id: {}, session_id: {}", configId, elem.get("session_id"));
            if (elem.containsKey("session_id") && elem.get("session_id").equals(configId.replace('-', '_'))) {
                resList.add(elem);
            }
        }
        return resList;
    }

    protected static List<Map<String, Object>> readFailureUrl(final String configId) {
        final List<Map<String, Object>> logList = readLogItems("failureurl");
        final List<Map<String, Object>> resList = new ArrayList<>();
        for (Map<String, Object> elem : logList) {
            logger.info("config_id: {}, thread_name: {}", configId, elem.get("thread_name"));
            if (elem.containsKey("thread_name") && elem.get("thread_name").toString().startsWith("Crawler-" + configId.replace('-', '_'))) {
                resList.add(elem);
            }
        }
        return resList;
    }
}
