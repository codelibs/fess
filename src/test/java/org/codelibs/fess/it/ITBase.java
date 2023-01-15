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

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.restassured.mapper.ObjectMapperType;
import io.restassured.specification.RequestSpecification;

public class ITBase {
    private static final Logger logger = LogManager.getLogger(ITBase.class);
    public static final String DEFAULT_FESS_URL = "http://localhost:8080";
    public static final String DEFAULT_SEARCH_ENGINE_URL = "http://localhost:9200";
    public static final String DEFAULT_TEST_TOKEN = "E44TjYrJQadtGBFFuECA0SBqqVtqj7lRGmhYep53ixNdvlRxnkhwqCVCpRoO";
    public static final String DEFAULT_TEST_TOKEN_ID = "testToken";
    private static final String TEST_TOKEN = "test.token";

    public static String getTestToken() {
        return System.getProperty(TEST_TOKEN, DEFAULT_TEST_TOKEN);
    }

    public static String settingTestToken() {
        final String testToken = System.getProperty(TEST_TOKEN);
        if (testToken != null) {
            logger.info("Token: {}", testToken);
            return testToken;
        }

        given().contentType("application/json").body("{\"index\":{\"_index\":\"fess_config.access_token\",\"_id\":\""
                + DEFAULT_TEST_TOKEN_ID
                + "\"}}\n{\"updatedTime\":1490250145200,\"updatedBy\":\"admin\",\"createdBy\":\"admin\",\"permissions\":[\"Radmin-api\",\"Rguest\"],\"name\":\"Admin API\",\"createdTime\":1490250145200,\"token\":\""
                + DEFAULT_TEST_TOKEN + "\"}\n").when().post(getEsUrl() + "/_bulk");
        given().contentType("application/json").when().post(getEsUrl() + "/_refresh");
        logger.info("Created Token: {}", DEFAULT_TEST_TOKEN);
        return DEFAULT_TEST_TOKEN;
    }

    public static void deleteTestToken() {
        final String testToken = System.getProperty(TEST_TOKEN);
        if (testToken != null) {
            return;
        }
        given().contentType("application/json").delete(getEsUrl() + "/fess_config.access_token/_doc/" + DEFAULT_TEST_TOKEN_ID);
    }

    public static void refresh() {
        given().contentType("application/json").post(getEsUrl() + "/_refresh");
    }

    public static String getFessUrl() {
        return System.getProperty("test.fess.url", DEFAULT_FESS_URL);
    }

    public static String getEsUrl() {
        return System.getProperty("test.search_engine.url", DEFAULT_SEARCH_ENGINE_URL);
    }

    protected static RequestSpecification checkMethodBase(final Map<String, Object> body) {
        return given().contentType("application/json").header("Authorization", getTestToken()).body(body, ObjectMapperType.JACKSON_2)
                .when();
    }
}
