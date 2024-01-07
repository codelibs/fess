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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.RequestHeaderService;
import org.codelibs.fess.app.service.WebAuthenticationService;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.client.http.Authentication;
import org.codelibs.fess.crawler.client.http.HcHttpClient;
import org.codelibs.fess.es.config.bsentity.BsWebConfig;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.Param.Client;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ParameterUtil;

/**
 * @author FreeGen
 */
public class WebConfig extends BsWebConfig implements CrawlingConfig {

    private static final long serialVersionUID = 1L;

    protected volatile Pattern[] includedDocUrlPatterns;

    protected volatile Pattern[] excludedDocUrlPatterns;

    protected transient volatile Map<ConfigName, Map<String, String>> configParameterMap;

    protected CrawlerClientFactory crawlerClientFactory = null;

    public WebConfig() {
        setBoost(1.0f);
    }

    @Override
    public String getDocumentBoost() {
        return getBoost().toString();
    }

    @Override
    public String getIndexingTarget(final String input) {
        if (includedDocUrlPatterns == null || excludedDocUrlPatterns == null) {
            initDocUrlPattern();
        }

        if (includedDocUrlPatterns.length == 0 && excludedDocUrlPatterns.length == 0) {
            return Constants.TRUE;
        }

        for (final Pattern pattern : includedDocUrlPatterns) {
            if (pattern.matcher(input).matches()) {
                return Constants.TRUE;
            }
        }

        for (final Pattern pattern : excludedDocUrlPatterns) {
            if (pattern.matcher(input).matches()) {
                return Constants.FALSE;
            }
        }

        return Constants.TRUE;
    }

    protected synchronized void initDocUrlPattern() {

        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        if (includedDocUrlPatterns == null) {
            if (StringUtil.isNotBlank(getIncludedDocUrls())) {
                final List<Pattern> urlPatterList = new ArrayList<>();
                final String[] urls = getIncludedDocUrls().split("[\r\n]");
                for (final String u : urls) {
                    final String v = systemHelper.normalizeConfigPath(u);
                    if (StringUtil.isNotBlank(v)) {
                        urlPatterList.add(Pattern.compile(v));
                    }
                }
                includedDocUrlPatterns = urlPatterList.toArray(new Pattern[urlPatterList.size()]);
            } else {
                includedDocUrlPatterns = new Pattern[0];
            }
        }

        if (excludedDocUrlPatterns == null) {
            if (StringUtil.isNotBlank(getExcludedDocUrls())) {
                final List<Pattern> urlPatterList = new ArrayList<>();
                final String[] urls = getExcludedDocUrls().split("[\r\n]");
                for (final String u : urls) {
                    final String v = systemHelper.normalizeConfigPath(u);
                    if (StringUtil.isNotBlank(v)) {
                        urlPatterList.add(Pattern.compile(v));
                    }
                }
                excludedDocUrlPatterns = urlPatterList.toArray(new Pattern[urlPatterList.size()]);
            } else if (includedDocUrlPatterns.length > 0) {
                excludedDocUrlPatterns = new Pattern[] { Pattern.compile(".*") };
            } else {
                excludedDocUrlPatterns = new Pattern[0];
            }
        }
    }

    public String getBoostValue() {
        if (boost != null) {
            return boost.toString();
        }
        return null;
    }

    public void setBoostValue(final String value) {
        if (value != null) {
            try {
                boost = Float.parseFloat(value);
            } catch (final Exception e) {}
        }
    }

    @Override
    public String getConfigId() {
        return ConfigType.WEB.getConfigId(getId());
    }

    @Override
    public CrawlerClientFactory initializeClientFactory(final Supplier<CrawlerClientFactory> creator) {
        if (crawlerClientFactory != null) {
            return crawlerClientFactory;
        }
        final CrawlerClientFactory factory = creator.get();

        final WebAuthenticationService webAuthenticationService = ComponentUtil.getComponent(WebAuthenticationService.class);
        final RequestHeaderService requestHeaderService = ComponentUtil.getComponent(RequestHeaderService.class);
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        // HttpClient Parameters
        final Map<String, Object> paramMap = new HashMap<>();
        factory.setInitParameterMap(paramMap);

        final Map<String, String> clientConfigMap = getConfigParameterMap(ConfigName.CLIENT);
        if (clientConfigMap != null) {
            paramMap.putAll(clientConfigMap);
        }

        // robots txt enabled
        if (paramMap.get(Param.Client.ROBOTS_TXT_ENABLED) == null) {
            paramMap.put(Param.Client.ROBOTS_TXT_ENABLED, !fessConfig.isCrawlerIgnoreRobotsTxt());
        }

        final String userAgent = getUserAgent();
        if (StringUtil.isNotBlank(userAgent)) {
            if (userAgent.startsWith(Constants.CRAWLING_USER_AGENT_PREFIX) && userAgent.endsWith(Constants.CRAWLING_USER_AGENT_SUFFIX)) {
                paramMap.put(Client.USER_AGENT, fessConfig.getUserAgentName());
            } else {
                paramMap.put(Client.USER_AGENT, userAgent);
            }
        } else {
            paramMap.put(Client.USER_AGENT, fessConfig.getUserAgentName());
        }

        final List<WebAuthentication> webAuthList = webAuthenticationService.getWebAuthenticationList(getId());
        final List<Authentication> basicAuthList = new ArrayList<>();
        for (final WebAuthentication webAuth : webAuthList) {
            basicAuthList.add(webAuth.getAuthentication());
        }
        paramMap.put(HcHttpClient.BASIC_AUTHENTICATIONS_PROPERTY, basicAuthList.toArray(new Authentication[basicAuthList.size()]));

        // request header
        final List<RequestHeader> requestHeaderList = requestHeaderService.getRequestHeaderList(getId());
        final List<org.codelibs.fess.crawler.client.http.RequestHeader> rhList = new ArrayList<>();
        for (final RequestHeader requestHeader : requestHeaderList) {
            rhList.add(requestHeader.getCrawlerRequestHeader());
        }
        paramMap.put(HcHttpClient.REQUERT_HEADERS_PROPERTY,
                rhList.toArray(new org.codelibs.fess.crawler.client.http.RequestHeader[rhList.size()]));

        final String proxyHost = (String) paramMap.get(Param.Client.PROXY_HOST);
        final String proxyPort = (String) paramMap.get(Param.Client.PROXY_PORT);
        if (StringUtil.isNotBlank(proxyHost) && StringUtil.isNotBlank(proxyPort)) {
            // proxy credentials
            if (paramMap.get(Param.Client.PROXY_USERNAME) != null && paramMap.get(Param.Client.PROXY_PASSWORD) != null) {
                paramMap.put(HcHttpClient.PROXY_CREDENTIALS_PROPERTY, new UsernamePasswordCredentials(
                        paramMap.remove(Param.Client.PROXY_USERNAME).toString(), paramMap.remove(Param.Client.PROXY_PASSWORD).toString()));
            }
        } else {
            initializeDefaultHttpProxy(paramMap);
        }

        crawlerClientFactory = factory;
        return factory;
    }

    @Override
    public Map<String, String> getConfigParameterMap(final ConfigName name) {
        if (configParameterMap == null) {
            configParameterMap = ParameterUtil.createConfigParameterMap(getConfigParameter());
        }

        final Map<String, String> configMap = configParameterMap.get(name);
        if (configMap == null) {
            return Collections.emptyMap();
        }
        return configMap;
    }

    @Override
    public String getId() {
        return asDocMeta().id();
    }

    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    @Override
    public String toString() {
        return "WebConfig [available=" + available + ", boost=" + boost + ", configParameter=" + configParameter + ", createdBy="
                + createdBy + ", createdTime=" + createdTime + ", depth=" + depth + ", excludedDocUrls=" + excludedDocUrls
                + ", excludedUrls=" + excludedUrls + ", includedDocUrls=" + includedDocUrls + ", includedUrls=" + includedUrls
                + ", intervalTime=" + intervalTime + ", timeToLive=" + timeToLive + ", maxAccessCount=" + maxAccessCount + ", name=" + name
                + ", numOfThread=" + numOfThread + ", permissions=" + Arrays.toString(permissions) + ", sortOrder=" + sortOrder
                + ", updatedBy=" + updatedBy + ", updatedTime=" + updatedTime + ", urls=" + urls + ", userAgent=" + userAgent + "]";
    }
}
