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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.codelibs.core.misc.Pair;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.es.client.FessEsClient.SearchConditionBuilder;
import org.codelibs.fess.es.config.exbhv.KeyMatchBhv;
import org.codelibs.fess.es.config.exentity.KeyMatch;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyMatchHelper {
    private static final Logger logger = LoggerFactory.getLogger(KeyMatchHelper.class);

    protected volatile Map<String, Map<String, Pair<QueryBuilder, ScoreFunctionBuilder<?>>>> keyMatchQueryMap = Collections.emptyMap();

    protected long reloadInterval = 1000L;

    @PostConstruct
    public void init() {
        reload(0);
    }

    public void update() {
        new Thread(() -> reload(reloadInterval)).start();
    }

    public List<KeyMatch> getAvailableKeyMatchList() {
        return ComponentUtil.getComponent(KeyMatchBhv.class).selectList(cb -> {
            cb.query().matchAll();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPageKeymatchMaxFetchSizeAsInteger());
        });
    }

    protected void reload(final long interval) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Map<String, Map<String, Pair<QueryBuilder, ScoreFunctionBuilder<?>>>> keyMatchQueryMap = new HashMap<>();
        getAvailableKeyMatchList().stream().forEach(
                keyMatch -> {
                    final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                    getDocumentList(keyMatch).stream().map(doc -> {
                        return DocumentUtil.getValue(doc, fessConfig.getIndexFieldDocId(), String.class);
                    }).forEach(docId -> {
                        boolQuery.should(QueryBuilders.termQuery(fessConfig.getIndexFieldDocId(), docId));
                    });

                    if (boolQuery.hasClauses()) {
                        final String virtualHost = keyMatch.getVirtualHost();
                        Map<String, Pair<QueryBuilder, ScoreFunctionBuilder<?>>> queryMap = keyMatchQueryMap.get(virtualHost);
                        if (queryMap == null) {
                            queryMap = new HashMap<>();
                            keyMatchQueryMap.put(virtualHost, queryMap);
                        }
                        queryMap.put(toLowerCase(keyMatch.getTerm()),
                                new Pair<>(boolQuery, ScoreFunctionBuilders.weightFactorFunction(keyMatch.getBoost())));
                    }

                    if (interval > 0) {
                        try {
                            Thread.sleep(interval);
                        } catch (final InterruptedException e) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Interrupted.", e);
                            }
                        }
                    }
                });
        this.keyMatchQueryMap = keyMatchQueryMap;
    }

    protected List<Map<String, Object>> getDocumentList(final KeyMatch keyMatch) {
        final FessEsClient fessEsClient = ComponentUtil.getFessEsClient();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessEsClient.getDocumentList(fessConfig.getIndexDocumentSearchIndex(), fessConfig.getIndexDocumentType(),
                searchRequestBuilder -> {
                    return SearchConditionBuilder.builder(searchRequestBuilder.setPreference(Constants.SEARCH_PREFERENCE_PRIMARY))
                            .searchRequestType(SearchRequestType.ADMIN_SEARCH).size(keyMatch.getMaxSize()).query(keyMatch.getQuery())
                            .responseFields(new String[] { fessConfig.getIndexFieldDocId() }).build();
                });
    }

    public long getReloadInterval() {
        return reloadInterval;
    }

    public void setReloadInterval(final long reloadInterval) {
        this.reloadInterval = reloadInterval;
    }

    protected Map<String, Pair<QueryBuilder, ScoreFunctionBuilder<?>>> getQueryMap() {
        final String key = ComponentUtil.getFessConfig().getVirtualHostKey();
        final Map<String, Pair<QueryBuilder, ScoreFunctionBuilder<?>>> map = keyMatchQueryMap.get(key);
        if (map != null) {
            return map;
        }
        return Collections.emptyMap();
    }

    public void buildQuery(final List<String> keywordList, final List<FilterFunctionBuilder> list) {
        keywordList.stream().forEach(keyword -> {
            final Pair<QueryBuilder, ScoreFunctionBuilder<?>> pair = getQueryMap().get(toLowerCase(keyword));
            if (pair != null) {
                list.add(new FilterFunctionBuilder(pair.getFirst(), pair.getSecond()));
            }
        });
    }

    public List<Map<String, Object>> getBoostedDocumentList(final String term, final int size) {
        final FessEsClient fessEsClient = ComponentUtil.getFessEsClient();
        final Pair<QueryBuilder, ScoreFunctionBuilder<?>> pair = getQueryMap().get(toLowerCase(term));
        if (pair == null) {
            return Collections.emptyList();
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessEsClient.getDocumentList(fessConfig.getIndexDocumentSearchIndex(), fessConfig.getIndexDocumentType(),
                searchRequestBuilder -> {
                    searchRequestBuilder.setPreference(Constants.SEARCH_PREFERENCE_PRIMARY).setQuery(pair.getFirst()).setSize(size);
                    return true;
                });
    }

    private String toLowerCase(final String term) {
        return term != null ? term.toLowerCase(Locale.ROOT) : term;
    }

}
