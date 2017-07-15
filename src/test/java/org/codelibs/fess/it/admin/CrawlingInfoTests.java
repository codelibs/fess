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

@Tag("it")
public class CrawlingInfoTests extends ITBase {
    private static final Logger logger = LoggerFactory.getLogger(CrawlingInfoTests.class);

    private static final String NAME_PREFIX = "crawlingInfoTest_";

    @BeforeAll
    protected static void initAll() {
        RestAssured.baseURI = getFessUrl();
        settingTestToken();
    }

    @BeforeEach
    protected void init() {
    }

    @AfterEach
    protected void tearDown() {
        deleteMethod("/api/admin/webconfig/setting/" + getWebConfigId());
        deleteMethod("/api/admin/scheduler/setting/" + getSchedulerId());
        // TODO delete searchlist & job log
    }

    @AfterAll
    protected static void tearDownAll() {
        deleteTestToken();
    }

    @Test
    void crawlingTest() {
        try {
            createWebConfig();
            logger.info("WebConfig is created");
            Thread.sleep(10000);

            createJob();
            logger.info("Job is created");
            Thread.sleep(30000);

            startJob();

            waitJob();

            testReadCrawlingInfo();
            testDeleteCrawlingInfo();
        } catch (InterruptedException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    private void createWebConfig() {
        final Map<String, Object> requestBody = new HashMap<>();
        final String urls = "http://example.com";
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

    private void createJob() {
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

    private void startJob() {
        final Map<String, Object> requestBody = new HashMap<>();
        final String schedulerId = getSchedulerId();
        final Response response = checkMethodBase(requestBody).post("/api/admin/scheduler/" + schedulerId + "/start");
        response.then().body("response.status", equalTo(0));
        logger.info("Start scheduler \"" + schedulerId + "\"");
    }

    private void waitJob() throws InterruptedException {
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

    private void testReadCrawlingInfo() {
        final String webConfigId = getWebConfigId();
        final Map<String, Object> searchBody = new HashMap<>();
        final String response = checkMethodBase(searchBody).get("/api/admin/crawlinginfo/logs").asString();
        final List<Map<String, Object>> itemList = JsonPath.from(response).getList("response.logs");
        //logger.info("crawling info" + response);

        int count = 0;
        for (Map<String, Object> elem : itemList) {
            if (elem.containsKey("session_id") && elem.get("session_id").equals(webConfigId)) {
                count += 1;
            }
        }
        assertEquals(1, count);
    }

    private void testDeleteCrawlingInfo() {
        final String webConfigId = getWebConfigId();
        final Map<String, Object> searchBody = new HashMap<>();
        final String response = checkMethodBase(searchBody).get("/api/admin/crawlinginfo/logs").asString();
        final List<Map<String, Object>> itemList = JsonPath.from(response).getList("response.logs");
        //logger.info("crawling info" + response);

        for (Map<String, Object> elem : itemList) {
            if (elem.containsKey("session_id") && elem.get("session_id").equals(webConfigId)) {
                deleteMethod("/api/admin/crawlinginfo/log/" + elem.get("id")).then().body("response.status", equalTo(0));
            }
        }
    }

    /**
     * Utilities
     * */
    private String getJsonResponse(final String path) {
        final Map<String, Object> searchBody = new HashMap<>();
        final String response = checkMethodBase(searchBody).get(path).asString();
        return response;
    }

    private String getResponsePath() {
        return "response.settings.findAll {it.name.startsWith(\"" + NAME_PREFIX + "\")}";
    }

    private String getWebConfigId() {
        final String response = getJsonResponse("/api/admin/webconfig/settings");
        final List<String> idList = JsonPath.from(response).getList(getResponsePath() + ".id");
        return idList.get(0);
    }

    private String getSchedulerId() {
        final String response = getJsonResponse("/api/admin/scheduler/settings");
        final List<String> idList = JsonPath.from(response).getList(getResponsePath() + ".id");
        return idList.get(0);
    }

    private Map<String, Object> getSchedulerItem() {
        final String response = getJsonResponse("/api/admin/scheduler/settings");
        final List<Map<String, Object>> itemList = JsonPath.from(response).getList(getResponsePath());
        assertEquals(1, itemList.size());
        return itemList.get(0);
    }

    private String buildJobScript() {
        String webConfigId = getWebConfigId();
        return String.format("return container.getComponent(\"crawlJob\")" + ".logLevel(\"info\")" + ".sessionId(\"%s\")"
                + ".webConfigIds([\"%s\"] as String[])" + ".jobExecutor(executor).execute();", webConfigId, webConfigId);
    }

    private Response deleteMethod(final String path) {
        return given().header("Authorization", getTestToken()).delete(path);
    }
}
