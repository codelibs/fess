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
package org.codelibs.fess.rank.fusion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.TotalHits.Relation;
import org.codelibs.core.collection.ArrayUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.codelibs.fess.util.FacetResponse;
import org.codelibs.fess.util.QueryResponseList;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.di.core.ExternalContext;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaResponseUtil;

public class RankFusionProcessor implements AutoCloseable {

    private static final Logger logger = LogManager.getLogger(RankFusionProcessor.class);

    protected RankFusionSearcher[] searchers = new RankFusionSearcher[1];

    protected ExecutorService executorService;

    protected int windowSize;

    @PostConstruct
    public void init() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int maxPageSize = fessConfig.getPagingSearchPageMaxSizeAsInteger();
        final int windowSize = fessConfig.getRankFusionWindowSizeAsInteger();
        if (maxPageSize * 2 < windowSize) {
            logger.warn("rank.fusion.window_size is lower than paging.search.page.max.size. "
                    + "The window size should be 2x more than the page size. ({} * 2 <= {})", maxPageSize, windowSize);
            this.windowSize = 2 * maxPageSize;
        } else {
            this.windowSize = windowSize;
        }
    }

    @Override
    @PreDestroy
    public void close() throws Exception {
        if (executorService != null) {
            try {
                executorService.shutdown();
                executorService.awaitTermination(60, TimeUnit.SECONDS);
            } catch (final InterruptedException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Interrupted.", e);
                }
            } finally {
                executorService.shutdownNow();
            }
        }
    }

    public List<Map<String, Object>> search(final String query, final SearchRequestParams params,
            final OptionalThing<FessUserBean> userBean) {
        if (searchers.length == 1) {
            return searchWithMainSearcher(query, params, userBean);
        }
        return searchWithMultipleSearchers(query, params, userBean);
    }

    protected List<Map<String, Object>> searchWithMultipleSearchers(final String query, final SearchRequestParams params,
            final OptionalThing<FessUserBean> userBean) {
        if (logger.isDebugEnabled()) {
            logger.debug("Send {} to the searchers.", query);
        }
        final int pageSize = params.getPageSize();
        final int startPosition = params.getStartPosition();
        if (startPosition * 2 >= windowSize) {
            int offset = params.getOffset();
            if (offset < 0) {
                offset = 0;
            } else if (offset > windowSize / 2) {
                offset = windowSize / 2;
            }
            int start = startPosition - offset;
            if (start < 0) {
                start = 0;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("start:{} -> start:{} with offset:{}.", startPosition, start, offset);
            }
            final SearchRequestParams reqParams = new SearchRequestParamsWrapper(params, start, pageSize);
            final SearchResult searchResult = searchers[0].search(query, reqParams, userBean);
            long allRecordCount = searchResult.getAllRecordCount();
            if (Relation.EQUAL_TO.toString().equals(searchResult.getAllRecordCountRelation())) {
                allRecordCount += offset;
            }
            return createResponseList(searchResult.getDocumentList(), allRecordCount, searchResult.getAllRecordCountRelation(),
                    searchResult.getQueryTime(), searchResult.isPartialResults(), searchResult.getFacetResponse(),
                    params.getStartPosition(), pageSize, offset);
        }

        final ExternalContext externalContext = SingletonLaContainerFactory.getExternalContext();
        final OptionalThing<HttpServletRequest> requestOpt = LaRequestUtil.getOptionalRequest();
        final OptionalThing<HttpServletResponse> responseOpt = LaResponseUtil.getOptionalResponse();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int rankConstant = fessConfig.getRankFusionRankConstantAsInteger();
        final int size = windowSize / searchers.length;
        if (logger.isDebugEnabled()) {
            logger.debug("The searcher window size is {} and a rank constant is {}.", size, rankConstant);
        }
        final List<Future<SearchResult>> resultList = new ArrayList<>();
        for (int i = 0; i < searchers.length; i++) {
            final SearchRequestParams reqParams = new SearchRequestParamsWrapper(params, 0, i == 0 ? windowSize : size);
            final RankFusionSearcher searcher = searchers[i];
            resultList.add(executorService.submit(() -> {
                try {
                    if (externalContext != null) {
                        requestOpt.ifPresent(externalContext::setRequest);
                        responseOpt.ifPresent(externalContext::setResponse);
                    }
                    return searcher.search(query, reqParams, userBean);
                } finally {
                    if (externalContext != null) {
                        externalContext.setRequest(null);
                        externalContext.setResponse(null);
                    }
                }
            }));
        }
        final SearchResult[] results = resultList.stream().map(f -> {
            try {
                return f.get();
            } catch (InterruptedException | ExecutionException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to process a search result.", e);
                }
                return SearchResult.create().build();
            }
        }).toArray(n -> new SearchResult[n]);

        final String scoreField = fessConfig.getRankFusionScoreField();
        final Map<String, Map<String, Object>> scoreDocMap = new HashMap<>();
        final String idField = fessConfig.getIndexFieldId();
        final Set<Object> mainIdSet = new HashSet<>();
        for (int i = 0; i < results.length; i++) {
            final List<Map<String, Object>> docList = results[i].getDocumentList();
            if (logger.isDebugEnabled()) {
                logger.debug("[{}] {} docs / {} docs.", i, docList.size(), results[i].getAllRecordCount());
            }
            for (int j = 0; j < docList.size(); j++) {
                final Map<String, Object> doc = docList.get(j);
                if (doc.get(idField) instanceof final String id) {
                    final float score = 1.0f / (rankConstant + j);
                    if (scoreDocMap.containsKey(id)) {
                        final Map<String, Object> baseDoc = scoreDocMap.get(id);
                        final float oldScore = toFloat(baseDoc.get(scoreField));
                        baseDoc.put(scoreField, oldScore + score);
                        final String[] searchers = DocumentUtil.getValue(doc, Constants.SEARCHER, String[].class);
                        if (searchers != null) {
                            final String[] baseSearchers = DocumentUtil.getValue(baseDoc, Constants.SEARCHER, String[].class);
                            if (baseSearchers != null) {
                                baseDoc.put(Constants.SEARCHER, ArrayUtil.addAll(baseSearchers, searchers));
                            } else {
                                baseDoc.put(Constants.SEARCHER, searchers);
                            }
                        }
                    } else {
                        doc.put(scoreField, Float.valueOf(score));
                        scoreDocMap.put(id, doc);
                    }
                    if (i == 0 && j < windowSize / 2) {
                        mainIdSet.add(id);
                    }
                }
            }
        }

        final var docs = scoreDocMap.values().stream()
                .sorted((e1, e2) -> Float.compare(toFloat(e2.get(scoreField)), toFloat(e1.get(scoreField)))).toList();
        int offset = 0;
        for (int i = 0; i < windowSize / 2 && i < docs.size(); i++) {
            if (!mainIdSet.contains(docs.get(i).get(idField))) {
                offset++;
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("The offset is {} and the fused docs is {}.", offset, docs.size());
        }
        final SearchResult mainResult = results[0];
        long allRecordCount = mainResult.getAllRecordCount();
        if (Relation.EQUAL_TO.toString().equals(mainResult.getAllRecordCountRelation())) {
            allRecordCount += offset;
        }
        return createResponseList(extractList(docs, pageSize, startPosition), allRecordCount, mainResult.getAllRecordCountRelation(),
                mainResult.getQueryTime(), mainResult.isPartialResults(), mainResult.getFacetResponse(), startPosition, pageSize, offset);
    }

    protected List<Map<String, Object>> extractList(final List<Map<String, Object>> docs, final int pageSize, final int startPosition) {
        final int size = docs.size();
        if (size == 0) {
            return docs; // empty
        }
        int fromIndex = startPosition;
        if (fromIndex >= size) {
            fromIndex = size - 1;
        }
        int toIndex = startPosition + pageSize;
        if (toIndex >= size) {
            toIndex = size;
        }
        return docs.subList(fromIndex, toIndex);
    }

    protected List<Map<String, Object>> searchWithMainSearcher(final String query, final SearchRequestParams params,
            final OptionalThing<FessUserBean> userBean) {
        if (logger.isDebugEnabled()) {
            logger.debug("Send {} to the main searcher.", query);
        }
        final int pageSize = params.getPageSize();
        final SearchResult searchResult = searchers[0].search(query, params, userBean);
        return createResponseList(searchResult.getDocumentList(), searchResult.getAllRecordCount(),
                searchResult.getAllRecordCountRelation(), searchResult.getQueryTime(), searchResult.isPartialResults(),
                searchResult.getFacetResponse(), params.getStartPosition(), pageSize, 0);
    }

    protected QueryResponseList createResponseList(final List<Map<String, Object>> documentList, final long allRecordCount,
            final String allRecordCountRelation, final long queryTime, final boolean partialResults, final FacetResponse facetResponse,
            final int start, final int pageSize, final int offset) {
        return new QueryResponseList(documentList, allRecordCount, allRecordCountRelation, queryTime, partialResults, facetResponse, start,
                pageSize, offset);
    }

    protected float toFloat(final Object value) {
        if (value instanceof final Float f) {
            return f;
        }
        if (value instanceof final String s) {
            return Float.parseFloat(s);
        }
        return 0.0f;
    }

    protected static class SearchRequestParamsWrapper extends SearchRequestParams {
        private final SearchRequestParams parent;
        private final int startPosition;
        private final int pageSize;

        SearchRequestParamsWrapper(final SearchRequestParams parent, final int startPosition, final int pageSize) {
            this.parent = parent;
            this.startPosition = startPosition;
            this.pageSize = pageSize;
        }

        public SearchRequestParams getParent() {
            return parent;
        }

        @Override
        public String getQuery() {
            return parent.getQuery();
        }

        @Override
        public Map<String, String[]> getFields() {
            return parent.getFields();
        }

        @Override
        public Map<String, String[]> getConditions() {
            return parent.getConditions();
        }

        @Override
        public String[] getLanguages() {
            return parent.getLanguages();
        }

        @Override
        public GeoInfo getGeoInfo() {
            return parent.getGeoInfo();
        }

        @Override
        public FacetInfo getFacetInfo() {
            return parent.getFacetInfo();
        }

        @Override
        public HighlightInfo getHighlightInfo() {
            return parent.getHighlightInfo();
        }

        @Override
        public String getSort() {
            return parent.getSort();
        }

        @Override
        public int getStartPosition() {
            return startPosition;
        }

        @Override
        public int getOffset() {
            return 0;
        }

        @Override
        public int getPageSize() {
            return pageSize;
        }

        @Override
        public String[] getExtraQueries() {
            return parent.getExtraQueries();
        }

        @Override
        public Object getAttribute(final String name) {
            return parent.getAttribute(name);
        }

        @Override
        public Locale getLocale() {
            return parent.getLocale();
        }

        @Override
        public SearchRequestType getType() {
            return parent.getType();
        }

        @Override
        public String getSimilarDocHash() {
            return parent.getSimilarDocHash();
        }

        @Override
        public String getTrackTotalHits() {
            return parent.getTrackTotalHits();
        }

        @Override
        public Float getMinScore() {
            return parent.getMinScore();
        }

        @Override
        public boolean hasConditionQuery() {
            return parent.hasConditionQuery();
        }

        @Override
        public String[] getResponseFields() {
            return parent.getResponseFields();
        }

        @Override
        public int hashCode() {
            return parent.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            return parent.equals(obj);
        }

        @Override
        public String toString() {
            return parent.toString();
        }
    }

    public void setSeacher(final RankFusionSearcher searcher) {
        this.searchers[0] = searcher;
    }

    public void register(final RankFusionSearcher searcher) {
        if (logger.isDebugEnabled()) {
            logger.debug("Load {}", searcher.getClass().getSimpleName());
        }
        final RankFusionSearcher[] newSearchers = Arrays.copyOf(searchers, searchers.length + 1);
        newSearchers[newSearchers.length - 1] = searcher;
        searchers = newSearchers;
        synchronized (this) {
            if (executorService == null) {
                int numThreads = ComponentUtil.getFessConfig().getRankFusionThreadsAsInteger();
                if (numThreads <= 0) {
                    numThreads = (Runtime.getRuntime().availableProcessors() * 3) / 2 + 1;
                }
                executorService = Executors.newFixedThreadPool(numThreads);
            }
        }
    }
}
