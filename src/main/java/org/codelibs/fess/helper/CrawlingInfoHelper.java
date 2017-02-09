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
package org.codelibs.fess.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.security.MessageDigestUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.CrawlingInfoService;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.es.config.exentity.CrawlingConfig;
import org.codelibs.fess.es.config.exentity.CrawlingInfo;
import org.codelibs.fess.es.config.exentity.CrawlingInfoParam;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlingInfoHelper {
    private static final Logger logger = LoggerFactory.getLogger(CrawlingInfoHelper.class);

    public static final String FACET_COUNT_KEY = "count";

    protected Map<String, String> infoMap;

    protected Long documentExpires;

    protected int maxSessionIdsInList;

    protected int urlIdPrefixLength = 445;

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
            infoMap = Collections.synchronizedMap(new LinkedHashMap<String, String>());
        }
        logger.debug("infoMap: {}={} => {}", key, value, infoMap);
        infoMap.put(key, value);
    }

    public void updateParams(final String sessionId, final String name, final int dayForCleanup) {
        final CrawlingInfo crawlingInfo = getCrawlingInfoService().getLast(sessionId);
        if (crawlingInfo == null) {
            logger.warn("No crawling session: " + sessionId);
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
        @SuppressWarnings("unchecked")
        final List<String> roleTypeList = (List<String>) dataMap.get(fessConfig.getIndexFieldRole());
        return generateId(url, roleTypeList);
    }

    public List<Map<String, String>> getSessionIdList(final FessEsClient fessEsClient) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessEsClient.search(
                fessConfig.getIndexDocumentSearchIndex(),
                fessConfig.getIndexDocumentType(),
                queryRequestBuilder -> {
                    queryRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
                    final TermsAggregationBuilder termsBuilder =
                            AggregationBuilders.terms(fessConfig.getIndexFieldSegment()).field(fessConfig.getIndexFieldSegment())
                                    .size(maxSessionIdsInList).order(Order.term(false));
                    queryRequestBuilder.addAggregation(termsBuilder);
                    queryRequestBuilder.setPreference(Constants.SEARCH_PREFERENCE_PRIMARY);
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

    protected String generateId(final String url, final List<String> roleTypeList) {
        final StringBuilder buf = new StringBuilder(1000);
        buf.append(url);
        if (roleTypeList != null && !roleTypeList.isEmpty()) {
            Collections.sort(roleTypeList);
            buf.append(";role=");
            buf.append(String.join(",", roleTypeList));
        }
        final String urlId = buf.toString().trim();
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
                    encodedBuf.append(URLEncoder.encode(String.valueOf(c), Constants.UTF_8));
                } catch (final UnsupportedEncodingException e) {
                    // NOP
                }
            }
        }

        final String id = encodedBuf.toString();
        if (id.length() <= urlIdPrefixLength) {
            return id;
        }
        return id.substring(0, urlIdPrefixLength) + MessageDigestUtil.digest("SHA-256", id.substring(urlIdPrefixLength));
    }

    public void setMaxSessionIdsInList(final int maxSessionIdsInList) {
        this.maxSessionIdsInList = maxSessionIdsInList;
    }

    public void setUrlIdPrefixLength(final int urlIdPrefixLength) {
        this.urlIdPrefixLength = urlIdPrefixLength;
    }

}
