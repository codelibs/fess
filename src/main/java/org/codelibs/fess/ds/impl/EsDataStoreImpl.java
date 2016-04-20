/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
package org.codelibs.fess.ds.impl;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.FailureUrlService;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.exception.MultipleCrawlingAccessException;
import org.codelibs.fess.ds.IndexUpdateCallback;
import org.codelibs.fess.es.config.exentity.DataConfig;
import org.codelibs.fess.exception.DataStoreCrawlingException;
import org.codelibs.fess.exception.DataStoreException;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.StreamUtil;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsDataStoreImpl extends AbstractDataStoreImpl {
    private static final String PREFERENCE = "preference";

    private static final String QUERY = "query";

    private static final String FIELDS = "fields";

    private static final String SIZE = "size";

    private static final String TYPE = "type";

    private static final String TIMEOUT = "timeout";

    private static final String SCROLL = "scroll";

    private static final String INDEX = "index";

    private static final String HOSTS = "hosts";

    private static final String SETTINGS_PREFIX = "settings.";

    private static final Logger logger = LoggerFactory.getLogger(EsDataStoreImpl.class);

    @Override
    protected void storeData(final DataConfig dataConfig, final IndexUpdateCallback callback, final Map<String, String> paramMap,
            final Map<String, String> scriptMap, final Map<String, Object> defaultDataMap) {
        final String hostsStr = paramMap.get(HOSTS);
        if (StringUtil.isBlank(hostsStr)) {
            logger.info("hosts is empty.");
            return;
        }

        final long readInterval = getReadInterval(paramMap);

        final Settings settings =
                Settings.settingsBuilder()
                        .put(paramMap
                                .entrySet()
                                .stream()
                                .filter(e -> e.getKey().startsWith(SETTINGS_PREFIX))
                                .collect(
                                        Collectors.toMap(e -> e.getKey().replaceFirst("^settings\\.", StringUtil.EMPTY), e -> e.getValue())))
                        .build();
        logger.info("Connecting to " + hostsStr + " with [" + settings.toDelimitedString(',') + "]");
        final InetSocketTransportAddress[] addresses = StreamUtil.of(hostsStr.split(",")).map(h -> {
            String[] values = h.trim().split(":");
            try {
                if (values.length == 1) {
                    return new InetSocketTransportAddress(InetAddress.getByName(values[0]), 9300);
                } else if (values.length == 2) {
                    return new InetSocketTransportAddress(InetAddress.getByName(values[0]), Integer.parseInt(values[1]));
                }
            } catch (Exception e) {
                logger.warn("Failed to parse address: " + h, e);
            }
            return null;
        }).filter(v -> v != null).toArray(n -> new InetSocketTransportAddress[n]);
        try (Client client = TransportClient.builder().settings(settings).build().addTransportAddresses(addresses)) {
            processData(dataConfig, callback, paramMap, scriptMap, defaultDataMap, readInterval, client);
        }
    }

    protected void processData(final DataConfig dataConfig, final IndexUpdateCallback callback, final Map<String, String> paramMap,
            final Map<String, String> scriptMap, final Map<String, Object> defaultDataMap, final long readInterval, final Client client) {

        final boolean deleteProcessedDoc = paramMap.getOrDefault("delete.processed.doc", Constants.FALSE).equalsIgnoreCase(Constants.TRUE);
        final String[] indices;
        if (paramMap.containsKey(INDEX)) {
            indices = paramMap.get(INDEX).trim().split(",");
        } else {
            indices = new String[] { "_all" };
        }
        final String scroll = paramMap.containsKey(SCROLL) ? paramMap.get(SCROLL).trim() : "1m";
        final String timeout = paramMap.containsKey(TIMEOUT) ? paramMap.get(TIMEOUT).trim() : "1m";
        final SearchRequestBuilder builder = client.prepareSearch(indices);
        if (paramMap.containsKey(TYPE)) {
            builder.setTypes(paramMap.get(TYPE).trim().split(","));
        }
        if (paramMap.containsKey(SIZE)) {
            builder.setSize(Integer.parseInt(paramMap.get(SIZE)));
        }
        if (paramMap.containsKey(FIELDS)) {
            builder.addFields(paramMap.get(FIELDS).trim().split(","));
        }
        builder.setQuery(paramMap.containsKey(QUERY) ? paramMap.get(QUERY).trim() : "{\"query\":{\"match_all\":{}}}");
        builder.setScroll(scroll);
        builder.setPreference(paramMap.containsKey(PREFERENCE) ? paramMap.get(PREFERENCE).trim() : Constants.SEARCH_PREFERENCE_PRIMARY);
        try {
            SearchResponse response = builder.execute().actionGet(timeout);

            String scrollId = response.getScrollId();
            while (scrollId != null) {
                final SearchHits searchHits = response.getHits();
                final SearchHit[] hits = searchHits.getHits();
                if (hits.length == 0) {
                    scrollId = null;
                    break;
                }

                boolean loop = true;
                final BulkRequestBuilder bulkRequest = deleteProcessedDoc ? client.prepareBulk() : null;
                for (final SearchHit hit : hits) {
                    if (!alive || !loop) {
                        break;
                    }

                    final Map<String, Object> dataMap = new HashMap<String, Object>();
                    dataMap.putAll(defaultDataMap);
                    final Map<String, Object> resultMap = new LinkedHashMap<>();
                    resultMap.putAll(paramMap);
                    resultMap.put("index", hit.getIndex());
                    resultMap.put("type", hit.getType());
                    resultMap.put("id", hit.getId());
                    resultMap.put("version", Long.valueOf(hit.getVersion()));
                    resultMap.put("hit", hit);
                    resultMap.put("source", hit.getSource());

                    if (logger.isDebugEnabled()) {
                        for (final Map.Entry<String, Object> entry : resultMap.entrySet()) {
                            logger.debug(entry.getKey() + "=" + entry.getValue());
                        }
                    }

                    for (final Map.Entry<String, String> entry : scriptMap.entrySet()) {
                        final Object convertValue = convertValue(entry.getValue(), resultMap);
                        if (convertValue != null) {
                            dataMap.put(entry.getKey(), convertValue);
                        }
                    }

                    if (logger.isDebugEnabled()) {
                        for (final Map.Entry<String, Object> entry : dataMap.entrySet()) {
                            logger.debug(entry.getKey() + "=" + entry.getValue());
                        }
                    }

                    try {
                        callback.store(paramMap, dataMap);
                    } catch (final CrawlingAccessException e) {
                        logger.warn("Crawling Access Exception at : " + dataMap, e);

                        Throwable target = e;
                        if (target instanceof MultipleCrawlingAccessException) {
                            final Throwable[] causes = ((MultipleCrawlingAccessException) target).getCauses();
                            if (causes.length > 0) {
                                target = causes[causes.length - 1];
                            }
                        }

                        String errorName;
                        final Throwable cause = target.getCause();
                        if (cause != null) {
                            errorName = cause.getClass().getCanonicalName();
                        } else {
                            errorName = target.getClass().getCanonicalName();
                        }

                        String url;
                        if (target instanceof DataStoreCrawlingException) {
                            DataStoreCrawlingException dce = (DataStoreCrawlingException) target;
                            url = dce.getUrl();
                            if (dce.aborted()) {
                                loop = false;
                            }
                        } else {
                            url = hit.getIndex() + "/" + hit.getType() + "/" + hit.getId();
                        }
                        final FailureUrlService failureUrlService = ComponentUtil.getComponent(FailureUrlService.class);
                        failureUrlService.store(dataConfig, errorName, url, target);
                    } catch (final Throwable t) {
                        logger.warn("Crawling Access Exception at : " + dataMap, t);
                        final String url = hit.getIndex() + "/" + hit.getType() + "/" + hit.getId();
                        final FailureUrlService failureUrlService = ComponentUtil.getComponent(FailureUrlService.class);
                        failureUrlService.store(dataConfig, t.getClass().getCanonicalName(), url, t);
                    }

                    if (bulkRequest != null) {
                        bulkRequest.add(client.prepareDelete(hit.getIndex(), hit.getType(), hit.getId()));
                    }

                    if (readInterval > 0) {
                        sleep(readInterval);
                    }
                }

                if (bulkRequest != null && bulkRequest.numberOfActions() > 0) {
                    final BulkResponse bulkResponse = bulkRequest.execute().actionGet(timeout);
                    if (bulkResponse.hasFailures()) {
                        logger.warn(bulkResponse.buildFailureMessage());
                    }
                }

                if (!alive) {
                    break;
                }
                response = client.prepareSearchScroll(scrollId).setScroll(scroll).execute().actionGet(timeout);
                scrollId = response.getScrollId();
            }
        } catch (final Exception e) {
            throw new DataStoreException("Failed to crawl data when acessing elasticsearch.", e);
        }
    }

}
