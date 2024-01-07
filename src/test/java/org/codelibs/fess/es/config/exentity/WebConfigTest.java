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
package org.codelibs.fess.es.config.exentity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.RequestHeaderService;
import org.codelibs.fess.app.service.WebAuthenticationService;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.client.http.Authentication;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.mylasta.direction.FessProp;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.common.SetOnce;

public class WebConfigTest extends UnitFessTestCase {

    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    public void test_initializeClientFactory() {
        final Map<String, String> systemPropMap = new HashMap<>();
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getSystemProperty(final String key, final String defaultValue) {
                return systemPropMap.getOrDefault(key, defaultValue);
            }

            @Override
            public boolean isCrawlerIgnoreRobotsTxt() {
                return false;
            }

            @Override
            public String getHttpProxyHost() {
                return StringUtil.EMPTY;
            }

            @Override
            public String getHttpProxyPort() {
                return StringUtil.EMPTY;
            }
        };
        ComponentUtil.setFessConfig(fessConfig);
        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public String getProductVersion() {
                return "98.76";
            }
        };
        ComponentUtil.register(systemHelper, "systemHelper");
        WebAuthenticationService webAuthenticationService = new WebAuthenticationService() {
            @Override
            public List<WebAuthentication> getWebAuthenticationList(final String webConfigId) {
                return Collections.emptyList();
            }
        };
        ComponentUtil.register(webAuthenticationService, WebAuthenticationService.class.getCanonicalName());
        RequestHeaderService requestHeaderService = new RequestHeaderService() {
            @Override
            public List<RequestHeader> getRequestHeaderList(final String webConfigId) {
                return Collections.emptyList();
            }
        };
        ComponentUtil.register(requestHeaderService, RequestHeaderService.class.getCanonicalName());

        final SetOnce<Map<String, Object>> initParamMapSet = new SetOnce<>();
        WebConfig webConfig = new WebConfig();
        webConfig.setUserAgent(Constants.CRAWLING_USER_AGENT_PREFIX + "1.0" + Constants.CRAWLING_USER_AGENT_SUFFIX);
        CrawlerClientFactory crawlerClientFactory = webConfig.initializeClientFactory(() -> new CrawlerClientFactory() {
            public void setInitParameterMap(final Map<String, Object> params) {
                initParamMapSet.set(params);
            }
        });
        assertNotNull(crawlerClientFactory);
        Map<String, Object> initParamMap = initParamMapSet.get();
        assertNotNull(initParamMap);
        assertEquals(0, ((org.codelibs.fess.crawler.client.http.RequestHeader[]) initParamMap.get("requestHeaders")).length);
        assertEquals("Mozilla/5.0 (compatible; Fess/98.76; +http://fess.codelibs.org/bot.html)", initParamMap.get("userAgent"));
        assertEquals(0, ((Authentication[]) initParamMap.get("basicAuthentications")).length);
        assertTrue(Boolean.valueOf(initParamMap.get("robotsTxtEnabled").toString()).booleanValue());
    }

    public void test_initializeClientFactoryWithConfigParameter() {
        final Map<String, String> systemPropMap = new HashMap<>();
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getSystemProperty(final String key, final String defaultValue) {
                return systemPropMap.getOrDefault(key, defaultValue);
            }

            @Override
            public boolean isCrawlerIgnoreRobotsTxt() {
                return false;
            }

            @Override
            public String getHttpProxyHost() {
                return StringUtil.EMPTY;
            }

            @Override
            public String getHttpProxyPort() {
                return StringUtil.EMPTY;
            }

            @Override
            public String getAppEncryptPropertyPattern() {
                return ".*password|.*key|.*token|.*secret";
            }
        };
        ComponentUtil.setFessConfig(fessConfig);
        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public String getProductVersion() {
                return "98.76";
            }
        };
        ComponentUtil.register(systemHelper, "systemHelper");
        WebAuthenticationService webAuthenticationService = new WebAuthenticationService() {
            @Override
            public List<WebAuthentication> getWebAuthenticationList(final String webConfigId) {
                return Collections.emptyList();
            }
        };
        ComponentUtil.register(webAuthenticationService, WebAuthenticationService.class.getCanonicalName());
        RequestHeaderService requestHeaderService = new RequestHeaderService() {
            @Override
            public List<RequestHeader> getRequestHeaderList(final String webConfigId) {
                return Collections.emptyList();
            }
        };
        ComponentUtil.register(requestHeaderService, RequestHeaderService.class.getCanonicalName());

        final SetOnce<Map<String, Object>> initParamMapSet = new SetOnce<>();
        WebConfig webConfig = new WebConfig();
        final String userAgent = "TestAgent";
        webConfig.setUserAgent(userAgent);
        webConfig.setConfigParameter("""
                client.robotsTxtEnabled=false
                """);
        CrawlerClientFactory crawlerClientFactory = webConfig.initializeClientFactory(() -> new CrawlerClientFactory() {
            public void setInitParameterMap(final Map<String, Object> params) {
                initParamMapSet.set(params);
            }
        });
        assertNotNull(crawlerClientFactory);
        Map<String, Object> initParamMap = initParamMapSet.get();
        assertNotNull(initParamMap);
        assertEquals(0, ((org.codelibs.fess.crawler.client.http.RequestHeader[]) initParamMap.get("requestHeaders")).length);
        assertEquals(userAgent, initParamMap.get("userAgent"));
        assertEquals(0, ((Authentication[]) initParamMap.get("basicAuthentications")).length);
        assertFalse(Boolean.valueOf(initParamMap.get("robotsTxtEnabled").toString()).booleanValue());
    }
}
