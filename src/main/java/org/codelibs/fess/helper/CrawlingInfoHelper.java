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
package org.codelibs.fess.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.security.MessageDigestUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.CrawlingInfoService;
import org.codelibs.fess.es.client.SearchEngineClient;
import org.codelibs.fess.es.config.exentity.CrawlingConfig;
import org.codelibs.fess.es.config.exentity.CrawlingInfo;
import org.codelibs.fess.es.config.exentity.CrawlingInfoParam;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.aggregations.AggregationBuilders;
import org.opensearch.search.aggregations.BucketOrder;
import org.opensearch.search.aggregations.bucket.terms.Terms;
import org.opensearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.opensearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

public class CrawlingInfoHelper {
    private static final Logger logger = LogManager.getLogger(CrawlingInfoHelper.class);

    public static final String FACET_COUNT_KEY = "count";

    protected Map<String, String> infoMap;

    protected Long documentExpires;

    protected int maxSessionIdsInList;

    protected CrawlingInfoService getCrawlingInfoService() {
        return ComponentUtil.getComponent(CrawlingInfoService.class);
    }

    public String getCanonicalSessionId(final String sessionId) {
        final int idx = sessionId.indexOf('-');
        if (idx >= 0) {
            return sessionId.substring(0, idx);
        }
        return sessionId;
    }

    public synchronized void store(final String sessionId, final boolean create) {
        CrawlingInfo crawlingInfo = create ? null : getCrawlingInfoService().getLast(sessionId);
        if (crawlingInfo == null) {
            crawlingInfo = new CrawlingInfo(sessionId);
            try {
                getCrawlingInfoService().store(crawlingInfo);
            } catch (final Exception e) {
                throw new FessSystemException("No crawling session.", e);
            }
        }

        if (infoMap != null) {
            final List<CrawlingInfoParam> crawlingInfoParamList = new ArrayList<>();
            for (final Map.Entry<String, String> entry : infoMap.entrySet()) {
                final CrawlingInfoParam crawlingInfoParam = new CrawlingInfoParam();
                crawlingInfoParam.setCrawlingInfoId(crawlingInfo.getId());
                crawlingInfoParam.setKey(entry.getKey());
                crawlingInfoParam.setValue(entry.getValue());
                crawlingInfoParamList.add(crawlingInfoParam);
            }
            getCrawlingInfoService().storeInfo(crawlingInfoParamList);
        }

        infoMap = null;
    }

    public synchronized void putToInfoMap(final String key, final String value) {
        if (infoMap == null) {
            infoMap = Collections.synchronizedMap(new LinkedHashMap<>());
        }
        logger.debug("infoMap: {}={} => {}", key, value, infoMap);
        infoMap.put(key, value);
    }

    public void updateParams(final String sessionId, final String name, final int dayForCleanup) {
        final CrawlingInfo crawlingInfo = getCrawlingInfoService().getLast(sessionId);
        if (crawlingInfo == null) {
            logger.warn("No crawling session: {}", sessionId);
            return;
        }
        if (StringUtil.isNotBlank(name)) {
            crawlingInfo.setName(name);
        } else {
            crawlingInfo.setName(Constants.CRAWLING_INFO_SYSTEM_NAME);
        }
        if (dayForCleanup >= 0) {
            final long expires = getExpiredTime(dayForCleanup);
            crawlingInfo.setExpiredTime(expires);
            documentExpires = expires;
        }
        try {
            getCrawlingInfoService().store(crawlingInfo);
        } catch (final Exception e) {
            throw new FessSystemException("No crawling session.", e);
        }

    }

    public Date getDocumentExpires(final CrawlingConfig config) {
        if (config != null) {
            final Integer timeToLive = config.getTimeToLive();
            if (timeToLive != null) {
                // timeToLive minutes
                final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
                return new Date(now + timeToLive.longValue() * 1000 * 60);
            }
        }
        return documentExpires != null ? new Date(documentExpires) : null;
    }

    protected long getExpiredTime(final int days) {
        final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        return now + days * Constants.ONE_DAY_IN_MILLIS;
    }

    public Map<String, String> getInfoMap(final String sessionId) {
        final List<CrawlingInfoParam> crawlingInfoParamList = getCrawlingInfoService().getLastCrawlingInfoParamList(sessionId);
        final Map<String, String> map = new HashMap<>();
        for (final CrawlingInfoParam crawlingInfoParam : crawlingInfoParamList) {
            map.put(crawlingInfoParam.getKey(), crawlingInfoParam.getValue());
        }
        return map;
    }

    public String generateId(final Map<String, Object> dataMap) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String url = (String) dataMap.get(fessConfig.getIndexFieldUrl());
        final StringBuilder buf = new StringBuilder(1000);

        @SuppressWarnings("unchecked")
        final List<String> roleTypeList = (List<String>) dataMap.get(fessConfig.getIndexFieldRole());
        buf.append(url);
        if (roleTypeList != null && !roleTypeList.isEmpty()) {
            buf.append(";r=");
            buf.append(roleTypeList.stream().sorted().collect(Collectors.joining(",")));
        }

        @SuppressWarnings("unchecked")
        final List<String> virtualHostList = (List<String>) dataMap.get(fessConfig.getIndexFieldVirtualHost());
        if (virtualHostList != null && !virtualHostList.isEmpty()) {
            buf.append(";v=");
            buf.append(virtualHostList.stream().sorted().collect(Collectors.joining(",")));
        }

        final String urlId = buf.toString().trim();
        return generateId(urlId);
    }

    public List<Map<String, String>> getSessionIdList(final SearchEngineClient searchEngineClient) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return searchEngineClient.search(fessConfig.getIndexDocumentSearchIndex(), queryRequestBuilder -> {
            queryRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
            final TermsAggregationBuilder termsBuilder = AggregationBuilders.terms(fessConfig.getIndexFieldSegment())
                    .field(fessConfig.getIndexFieldSegment()).size(maxSessionIdsInList).order(BucketOrder.key(false));
            queryRequestBuilder.addAggregation(termsBuilder);
            queryRequestBuilder.setPreference(Constants.SEARCH_PREFERENCE_LOCAL);
            return true;
        }, (queryRequestBuilder, execTime, searchResponse) -> {
            final List<Map<String, String>> sessionIdList = new ArrayList<>();
            searchResponse.ifPresent(response -> {
                final Terms terms = response.getAggregations().get(fessConfig.getIndexFieldSegment());
                for (final Bucket bucket : terms.getBuckets()) {
                    final Map<String, String> map = new HashMap<>(2);
                    map.put(fessConfig.getIndexFieldSegment(), bucket.getKey().toString());
                    map.put(FACET_COUNT_KEY, Long.toString(bucket.getDocCount()));
                    sessionIdList.add(map);
                }
            });
            return sessionIdList;
        });
    }

    protected String generateId(final String urlId) {
        final StringBuilder encodedBuf = new StringBuilder(urlId.length() + 100);
        for (int i = 0; i < urlId.length(); i++) {
            final char c = urlId.charAt(i);
            if (c >= 'a' && c <= 'z' //
                    || c >= 'A' && c <= 'Z' //
                    || c >= '0' && c <= '9' //
                    || c == '.' //
                    || c == '-' //
                    || c == '*' //
                    || c == '_' //
                    || c == ':' //
                    || c == '+' //
                    || c == '%' //
                    || c == '=' //
                    || c == '&' //
                    || c == '?' //
                    || c == '#' //
                    || c == '[' //
                    || c == ']' //
                    || c == '@' //
                    || c == '~' //
                    || c == '!' //
                    || c == '$' //
                    || c == '\'' //
                    || c == '(' //
                    || c == ')' //
                    || c == ',' //
                    || c == ';' //
            ) {
                encodedBuf.append(c);
            } else {
                try {
                    final String target = String.valueOf(c);
                    final String converted = URLEncoder.encode(target, Constants.UTF_8);
                    if (target.equals(converted)) {
                        encodedBuf.append(Base64.getUrlEncoder().encodeToString(target.getBytes(Constants.CHARSET_UTF_8)));
                    } else {
                        encodedBuf.append(converted);
                    }
                } catch (final UnsupportedEncodingException e) {
                    // NOP
                }
            }
        }

        final String id = encodedBuf.toString();
        return MessageDigestUtil.digest(ComponentUtil.getFessConfig().getIndexIdDigestAlgorithm(), id);
    }

    public void setMaxSessionIdsInList(final int maxSessionIdsInList) {
        this.maxSessionIdsInList = maxSessionIdsInList;
    }
}
