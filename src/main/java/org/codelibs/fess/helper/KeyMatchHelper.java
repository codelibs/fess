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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Tuple3;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.es.client.SearchEngineClient;
import org.codelibs.fess.es.client.SearchEngineClient.SearchConditionBuilder;
import org.codelibs.fess.es.config.exbhv.KeyMatchBhv;
import org.codelibs.fess.es.config.exentity.KeyMatch;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;
import org.opensearch.index.query.functionscore.ScoreFunctionBuilder;
import org.opensearch.index.query.functionscore.ScoreFunctionBuilders;

public class KeyMatchHelper extends AbstractConfigHelper {
    private static final Logger logger = LogManager.getLogger(KeyMatchHelper.class);

    protected volatile Map<String, Map<String, List<Tuple3<String, QueryBuilder, ScoreFunctionBuilder<?>>>>> keyMatchQueryMap =
            Collections.emptyMap();

    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        load();
    }

    public List<KeyMatch> getAvailableKeyMatchList() {
        return ComponentUtil.getComponent(KeyMatchBhv.class).selectList(cb -> {
            cb.query().matchAll();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPageKeymatchMaxFetchSizeAsInteger());
        });
    }

    @Override
    public int load() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Map<String, Map<String, List<Tuple3<String, QueryBuilder, ScoreFunctionBuilder<?>>>>> keyMatchQueryMap = new HashMap<>();
        getAvailableKeyMatchList().stream().forEach(keyMatch -> {
            try {
                final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                if (logger.isDebugEnabled()) {
                    logger.debug("Loading KeyMatch Query: {}, Size: {}", keyMatch.getQuery(), keyMatch.getMaxSize());
                }
                getDocumentList(keyMatch).stream().map(doc -> {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Loaded KeyMatch doc: {}", doc);
                    }
                    return DocumentUtil.getValue(doc, fessConfig.getIndexFieldDocId(), String.class);
                }).forEach(docId -> {
                    boolQuery.should(QueryBuilders.termQuery(fessConfig.getIndexFieldDocId(), docId));
                });

                if (boolQuery.hasClauses()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Loaded KeyMatch Boost Query: {}", boolQuery);
                    }
                    String virtualHost = keyMatch.getVirtualHost();
                    if (StringUtil.isBlank(virtualHost)) {
                        virtualHost = StringUtil.EMPTY;
                    }
                    Map<String, List<Tuple3<String, QueryBuilder, ScoreFunctionBuilder<?>>>> queryMap = keyMatchQueryMap.get(virtualHost);
                    if (queryMap == null) {
                        queryMap = new HashMap<>();
                        keyMatchQueryMap.put(virtualHost, queryMap);
                    }
                    final String termKey = toLowerCase(keyMatch.getTerm());
                    List<Tuple3<String, QueryBuilder, ScoreFunctionBuilder<?>>> boostList = queryMap.get(termKey);
                    if (boostList == null) {
                        boostList = new ArrayList<>();
                        queryMap.put(termKey, boostList);
                    }
                    boostList.add(
                            new Tuple3<>(keyMatch.getId(), boolQuery, ScoreFunctionBuilders.weightFactorFunction(keyMatch.getBoost())));
                } else if (logger.isDebugEnabled()) {
                    logger.debug("No KeyMatch boost docs");
                }

                waitForNext();
            } catch (final Exception e) {
                logger.warn("Cannot load {}", keyMatch, e);
            }
        });
        this.keyMatchQueryMap = keyMatchQueryMap;
        return keyMatchQueryMap.size();
    }

    protected List<Map<String, Object>> getDocumentList(final KeyMatch keyMatch) {
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return searchEngineClient.getDocumentList(fessConfig.getIndexDocumentSearchIndex(),
                searchRequestBuilder -> SearchConditionBuilder
                        .builder(searchRequestBuilder.setPreference(Constants.SEARCH_PREFERENCE_LOCAL))
                        .searchRequestType(SearchRequestType.ADMIN_SEARCH).size(keyMatch.getMaxSize()).query(keyMatch.getQuery())
                        .responseFields(new String[] { fessConfig.getIndexFieldDocId() }).build());
    }

    protected Map<String, List<Tuple3<String, QueryBuilder, ScoreFunctionBuilder<?>>>> getQueryMap(final String key) {
        final Map<String, List<Tuple3<String, QueryBuilder, ScoreFunctionBuilder<?>>>> map = keyMatchQueryMap.get(key);
        if (map != null) {
            return map;
        }
        return Collections.emptyMap();
    }

    public void buildQuery(final List<String> keywordList, final List<FilterFunctionBuilder> list) {
        final String key = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
        keywordList.stream().forEach(keyword -> {
            final List<Tuple3<String, QueryBuilder, ScoreFunctionBuilder<?>>> boostList = getQueryMap(key).get(toLowerCase(keyword));
            if (boostList != null) {
                boostList.forEach(pair -> list.add(new FilterFunctionBuilder(pair.getValue2(), pair.getValue3())));
            }
        });
    }

    public List<Map<String, Object>> getBoostedDocumentList(final KeyMatch keyMatch) {
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        String virtualHost = keyMatch.getVirtualHost();
        if (StringUtil.isBlank(virtualHost)) {
            virtualHost = StringUtil.EMPTY;
        }
        final List<Tuple3<String, QueryBuilder, ScoreFunctionBuilder<?>>> boostList =
                getQueryMap(virtualHost).get(toLowerCase(keyMatch.getTerm()));
        if (boostList == null) {
            return Collections.emptyList();
        }
        for (final Tuple3<String, QueryBuilder, ScoreFunctionBuilder<?>> pair : boostList) {
            if (!keyMatch.getId().equals(pair.getValue1())) {
                continue;
            }
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            return searchEngineClient.getDocumentList(fessConfig.getIndexDocumentSearchIndex(), searchRequestBuilder -> {
                searchRequestBuilder.setPreference(Constants.SEARCH_PREFERENCE_LOCAL).setQuery(pair.getValue2())
                        .setSize(keyMatch.getMaxSize());
                return true;
            });
        }
        return Collections.emptyList();
    }

    private String toLowerCase(final String term) {
        return term != null ? term.toLowerCase(Locale.ROOT) : term;
    }

}
