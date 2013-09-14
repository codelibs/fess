/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.helper;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.sf.fess.Constants;
import jp.sf.fess.FessSystemException;
import jp.sf.fess.db.exentity.CrawlingSession;
import jp.sf.fess.db.exentity.CrawlingSessionInfo;
import jp.sf.fess.service.CrawlingSessionService;

import org.apache.commons.lang.time.DateUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.codelibs.solr.lib.SolrGroup;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.StringUtil;

public class CrawlingSessionHelper implements Serializable {

    public static final String FACET_COUNT_KEY = "count";

    public static final String FACET_SEGMENT_KEY = "segment";

    private static final long serialVersionUID = 1L;

    public int defaultExpiredDays = 365;

    public int expiredMinutesByZeroDay = 1;

    protected Map<String, String> infoMap;

    protected Set<String> expiredSessionIdSet = new HashSet<String>();

    protected CrawlingSessionService getCrawlingSessionService() {
        return SingletonS2Container.getComponent(CrawlingSessionService.class);
    }

    public void addExpiredSessions(final String sessionId) {
        if (sessionId != null) {
            expiredSessionIdSet.add(sessionId);
        }
    }

    public Set<String> getExpiredSessionIdSet() {
        return expiredSessionIdSet;
    }

    public boolean expired(final String sessionId) {
        return expiredSessionIdSet.contains(sessionId);
    }

    public String getCanonicalSessionId(final String sessionId) {
        final int idx = sessionId.indexOf('-');
        if (idx >= 0) {
            return sessionId.substring(0, idx);
        }
        return sessionId;
    }

    public synchronized void store(final String sessionId) {
        CrawlingSession crawlingSession = getCrawlingSessionService().get(
                sessionId);
        if (crawlingSession == null) {
            crawlingSession = new CrawlingSession(sessionId);
            crawlingSession
                    .setExpiredTime(getExpiredTimestamp(defaultExpiredDays));
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
                crawlingSessionInfo.setCrawlingSessionId(crawlingSession
                        .getId());
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
            infoMap = Collections
                    .synchronizedMap(new LinkedHashMap<String, String>());
        }
        infoMap.put(key, value);
    }

    public void updateParams(final String sessionId, final String name,
            final int dayForCleanup) {
        CrawlingSession crawlingSession = getCrawlingSessionService().get(
                sessionId);
        if (crawlingSession == null) {
            crawlingSession = new CrawlingSession(sessionId);
        }
        if (StringUtil.isNotBlank(name)) {
            crawlingSession.setName(name);
        } else {
            crawlingSession.setName(Constants.CRAWLING_SESSION_SYSTEM_NAME);
        }
        if (dayForCleanup < 0) {
            crawlingSession
                    .setExpiredTime(getExpiredTimestamp(defaultExpiredDays));
        } else {
            crawlingSession.setExpiredTime(getExpiredTimestamp(dayForCleanup));
        }
        try {
            getCrawlingSessionService().store(crawlingSession);
        } catch (final Exception e) {
            throw new FessSystemException("No crawling session.", e);
        }

    }

    protected Timestamp getExpiredTimestamp(final int days) {
        if (days > 0) {
            return new Timestamp(DateUtils.addDays(new Date(), days).getTime());
        } else {
            return new Timestamp(DateUtils.addMinutes(new Date(),
                    expiredMinutesByZeroDay).getTime());
        }
    }

    public Map<String, String> getInfoMap(final String sessionId) {
        final List<CrawlingSessionInfo> crawlingSessionInfoList = getCrawlingSessionService()
                .getCrawlingSessionInfoList(sessionId);
        final Map<String, String> map = new HashMap<String, String>();
        for (final CrawlingSessionInfo crawlingSessionInfo : crawlingSessionInfoList) {
            map.put(crawlingSessionInfo.getKey(),
                    crawlingSessionInfo.getValue());
        }
        return map;
    }

    public String generateId(final Map<String, Object> dataMap) {
        final String url = (String) dataMap.get("url");
        final List<String> browserTypeList = (List<String>) dataMap.get("type");
        final List<String> roleTypeList = (List<String>) dataMap.get("role");
        return generateId(url, browserTypeList, roleTypeList);
    }

    public List<Map<String, String>> getSessionIdList(
            final SolrGroup serverGroup) {
        final List<Map<String, String>> sessionIdList = new ArrayList<Map<String, String>>();

        final SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setFacet(true);
        query.addFacetField(FACET_SEGMENT_KEY);
        query.addSort(FACET_SEGMENT_KEY, ORDER.desc);

        final QueryResponse queryResponse = serverGroup.query(query);
        final List<FacetField> facets = queryResponse.getFacetFields();
        for (final FacetField facet : facets) {
            final List<FacetField.Count> facetEntries = facet.getValues();
            if (facetEntries != null) {
                for (final FacetField.Count fcount : facetEntries) {
                    final Map<String, String> map = new HashMap<String, String>(
                            2);
                    map.put(FACET_SEGMENT_KEY, fcount.getName());
                    map.put(FACET_COUNT_KEY, Long.toString(fcount.getCount()));
                    sessionIdList.add(map);
                }
            }
        }
        return sessionIdList;
    }

    private String generateId(final String url,
            final List<String> browserTypeList, final List<String> roleTypeList) {
        final StringBuilder buf = new StringBuilder(1000);
        buf.append(url);
        if (browserTypeList != null && !browserTypeList.isEmpty()) {
            Collections.sort(browserTypeList);
            buf.append(";type=");
            for (int i = 0; i < browserTypeList.size(); i++) {
                if (i != 0) {
                    buf.append(',');
                }
                buf.append(browserTypeList.get(i));
            }
        }
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
