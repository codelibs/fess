/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.FessSystemException;
import org.codelibs.fess.client.FessEsClient;
import org.codelibs.fess.es.exentity.CrawlingSession;
import org.codelibs.fess.es.exentity.CrawlingSessionInfo;
import org.codelibs.fess.service.CrawlingSessionService;
import org.codelibs.fess.util.ComponentUtil;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.seasar.framework.container.SingletonS2Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlingSessionHelper implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(CrawlingSessionHelper.class);

    public static final String FACET_COUNT_KEY = "count";

    private static final long serialVersionUID = 1L;

    protected Map<String, String> infoMap;

    protected Long documentExpires;

    private final int maxSessionIdsInList = 100;

    protected CrawlingSessionService getCrawlingSessionService() {
        return SingletonS2Container.getComponent(CrawlingSessionService.class);
    }

    public String getCanonicalSessionId(final String sessionId) {
        final int idx = sessionId.indexOf('-');
        if (idx >= 0) {
            return sessionId.substring(0, idx);
        }
        return sessionId;
    }

    public synchronized void store(final String sessionId, final boolean create) {
        CrawlingSession crawlingSession = create ? null : getCrawlingSessionService().getLast(sessionId);
        if (crawlingSession == null) {
            crawlingSession = new CrawlingSession(sessionId);
            try {
                getCrawlingSessionService().store(crawlingSession);
            } catch (final Exception e) {
                throw new FessSystemException("No crawling session.", e);
            }
        }

        if (infoMap != null) {
            final List<CrawlingSessionInfo> crawlingSessionInfoList = new ArrayList<CrawlingSessionInfo>();
            for (final Map.Entry<String, String> entry : infoMap.entrySet()) {
                final CrawlingSessionInfo crawlingSessionInfo = new CrawlingSessionInfo();
                crawlingSessionInfo.setCrawlingSessionId(crawlingSession.getId());
                crawlingSessionInfo.setKey(entry.getKey());
                crawlingSessionInfo.setValue(entry.getValue());
                crawlingSessionInfoList.add(crawlingSessionInfo);
            }
            getCrawlingSessionService().storeInfo(crawlingSessionInfoList);
        }

        infoMap = null;
    }

    public synchronized void putToInfoMap(final String key, final String value) {
        if (infoMap == null) {
            infoMap = Collections.synchronizedMap(new LinkedHashMap<String, String>());
        }
        infoMap.put(key, value);
    }

    public void updateParams(final String sessionId, final String name, final int dayForCleanup) {
        final CrawlingSession crawlingSession = getCrawlingSessionService().getLast(sessionId);
        if (crawlingSession == null) {
            logger.warn("No crawling session: " + sessionId);
            return;
        }
        if (StringUtil.isNotBlank(name)) {
            crawlingSession.setName(name);
        } else {
            crawlingSession.setName(Constants.CRAWLING_SESSION_SYSTEM_NAME);
        }
        if (dayForCleanup >= 0) {
            final long expires = getExpiredTime(dayForCleanup);
            crawlingSession.setExpiredTime(expires);
            documentExpires = expires;
        }
        try {
            getCrawlingSessionService().store(crawlingSession);
        } catch (final Exception e) {
            throw new FessSystemException("No crawling session.", e);
        }

    }

    public Long getDocumentExpires() {
        return documentExpires;
    }

    protected long getExpiredTime(final int days) {
        final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        return now + days * Constants.ONE_DAY_IN_MILLIS;
    }

    public Map<String, String> getInfoMap(final String sessionId) {
        final List<CrawlingSessionInfo> crawlingSessionInfoList = getCrawlingSessionService().getLastCrawlingSessionInfoList(sessionId);
        final Map<String, String> map = new HashMap<String, String>();
        for (final CrawlingSessionInfo crawlingSessionInfo : crawlingSessionInfoList) {
            map.put(crawlingSessionInfo.getKey(), crawlingSessionInfo.getValue());
        }
        return map;
    }

    public String generateId(final Map<String, Object> dataMap) {
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        final String url = (String) dataMap.get(fieldHelper.urlField);
        @SuppressWarnings("unchecked")
        final List<String> roleTypeList = (List<String>) dataMap.get(fieldHelper.roleField);
        return generateId(url, roleTypeList);
    }

    public List<Map<String, String>> getSessionIdList(final FessEsClient fessEsClient) {
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        return fessEsClient.search(fieldHelper.docIndex, fieldHelper.docType,
                queryRequestBuilder -> {
                    queryRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
                    final TermsBuilder termsBuilder =
                            AggregationBuilders.terms(fieldHelper.segmentField).field(fieldHelper.segmentField).size(maxSessionIdsInList)
                                    .order(Order.term(false));
                    queryRequestBuilder.addAggregation(termsBuilder);
                    return true;
                }, (queryRequestBuilder, execTime, searchResponse) -> {
                    final List<Map<String, String>> sessionIdList = new ArrayList<Map<String, String>>();
                    searchResponse.ifPresent(response -> {
                        final Terms terms = response.getAggregations().get(fieldHelper.segmentField);
                        for (final Bucket bucket : terms.getBuckets()) {
                            final Map<String, String> map = new HashMap<String, String>(2);
                            map.put(fieldHelper.segmentField, bucket.getKey());
                            map.put(FACET_COUNT_KEY, Long.toString(bucket.getDocCount()));
                            sessionIdList.add(map);
                        }
                    });
                    return sessionIdList;
                });
    }

    private String generateId(final String url, final List<String> roleTypeList) {
        final StringBuilder buf = new StringBuilder(1000);
        buf.append(url);
        if (roleTypeList != null && !roleTypeList.isEmpty()) {
            Collections.sort(roleTypeList);
            buf.append(";role=");
            for (int i = 0; i < roleTypeList.size(); i++) {
                if (i != 0) {
                    buf.append(',');
                }
                buf.append(roleTypeList.get(i));
            }
        }

        return normalize(buf.toString());
    }

    private String normalize(final String value) {
        return value.replace('"', ' ');
    }

}
