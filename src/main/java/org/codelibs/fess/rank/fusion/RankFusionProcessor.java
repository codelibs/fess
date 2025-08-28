/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
import java.util.Collections;
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
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.TotalHits.Relation;
import org.codelibs.core.collection.ArrayUtil;
import org.codelibs.core.concurrent.CommonPoolUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.stream.StreamUtil;
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

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * RankFusionProcessor manages multiple search engines and combines their results using rank fusion algorithms.
 * This processor supports searching with multiple searchers concurrently and merging their results based on
 * ranking scores to provide more comprehensive and accurate search results.
 *
 * The processor maintains a pool of searchers and an executor service for concurrent operations.
 * It implements rank fusion techniques to combine results from different search engines
 * and provides a unified search interface.
 */
public class RankFusionProcessor implements AutoCloseable {

    private static final Logger logger = LogManager.getLogger(RankFusionProcessor.class);

    /** Array of rank fusion searchers available for processing search requests */
    protected RankFusionSearcher[] searchers = new RankFusionSearcher[1];

    /** Executor service for concurrent search operations across multiple searchers */
    protected ExecutorService executorService;

    /** Size of the window for rank fusion processing, determines how many results to consider */
    protected int windowSize;

    /** Set of available searcher names that can be used for search processing */
    protected Set<String> availableSearcherNameSet;

    /**
     * Default constructor for RankFusionProcessor.
     * Initializes the processor with default values. The actual initialization
     * is performed by the init() method which is called after construction.
     */
    public RankFusionProcessor() {
        // Default constructor - initialization is done in init() method
    }

    /**
     * Initializes the rank fusion processor after construction.
     * Sets up the window size based on configuration and loads available searchers.
     * This method is called automatically after the bean is constructed.
     */
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
        load();
    }

    /**
     * Updates the processor configuration by reloading available searchers.
     * This method executes the load operation asynchronously in a separate thread.
     */
    public void update() {
        CommonPoolUtil.execute(this::load);
    }

    /**
     * Loads the available searcher names from system properties.
     * Parses the "rank.fusion.searchers" system property to determine which searchers
     * are available for use in rank fusion processing.
     */
    protected void load() {
        final String value = System.getProperty("rank.fusion.searchers");
        if (StringUtil.isBlank(value)) {
            availableSearcherNameSet = Collections.emptySet();
        } else {
            availableSearcherNameSet = StreamUtil.split(value, ",")
                    .get(stream -> stream.map(String::trim).filter(StringUtil::isNotBlank).collect(Collectors.toUnmodifiableSet()));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("availableSearcherNameSet={}", availableSearcherNameSet);
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

    /**
     * Performs a search operation using rank fusion across available searchers.
     * If only one searcher is available, uses the main searcher. Otherwise, performs
     * concurrent searches across multiple searchers and fuses the results.
     *
     * @param query the search query string
     * @param params search request parameters including pagination and filters
     * @param userBean optional user information for personalized search
     * @return list of search result documents with fused ranking scores
     */
    public List<Map<String, Object>> search(final String query, final SearchRequestParams params,
            final OptionalThing<FessUserBean> userBean) {
        final RankFusionSearcher[] availableSearchers = getAvailableSearchers();
        if (availableSearchers.length == 1) {
            return searchWithMainSearcher(availableSearchers[0], query, params, userBean);
        }
        return searchWithMultipleSearchers(availableSearchers, query, params, userBean);
    }

    /**
     * Gets the array of available searchers based on configuration.
     * Filters the searchers array to include only those specified in the available searcher name set.
     * If no specific searchers are configured, returns all searchers.
     *
     * @return array of available RankFusionSearcher instances
     */
    protected RankFusionSearcher[] getAvailableSearchers() {
        if (availableSearcherNameSet.isEmpty()) {
            return searchers;
        }
        final RankFusionSearcher[] availableSearchers = Arrays.stream(searchers)
                .filter(searcher -> availableSearcherNameSet.contains(searcher.getName()))
                .toArray(n -> new RankFusionSearcher[n]);
        if (availableSearchers.length == 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("No available searchers from {}", availableSearcherNameSet);
            }
            return new RankFusionSearcher[] { searchers[0] };
        }
        return availableSearchers;
    }

    /**
     * Performs concurrent searches using multiple searchers and fuses the results.
     * Executes searches in parallel across all provided searchers, then combines the results
     * using rank fusion algorithms to produce a unified result set.
     *
     * @param searchers array of searchers to use for concurrent searching
     * @param query the search query string
     * @param params search request parameters including pagination and filters
     * @param userBean optional user information for personalized search
     * @return list of search result documents with fused ranking scores
     */
    protected List<Map<String, Object>> searchWithMultipleSearchers(final RankFusionSearcher[] searchers, final String query,
            final SearchRequestParams params, final OptionalThing<FessUserBean> userBean) {
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
                        final String[] searcherNames = DocumentUtil.getValue(doc, Constants.SEARCHER, String[].class);
                        if (searcherNames != null) {
                            final String[] baseSearchers = DocumentUtil.getValue(baseDoc, Constants.SEARCHER, String[].class);
                            if (baseSearchers != null) {
                                baseDoc.put(Constants.SEARCHER, ArrayUtil.addAll(baseSearchers, searcherNames));
                            } else {
                                baseDoc.put(Constants.SEARCHER, searcherNames);
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

        final var docs = scoreDocMap.values()
                .stream()
                .sorted((e1, e2) -> Float.compare(toFloat(e2.get(scoreField)), toFloat(e1.get(scoreField))))
                .toList();
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

    /**
     * Extracts a subset of documents from the full result list based on pagination parameters.
     * Applies proper bounds checking to ensure the extracted range is within the document list size.
     *
     * @param docs the full list of search result documents
     * @param pageSize the number of documents to include in the page
     * @param startPosition the starting position for pagination
     * @return sublist of documents for the requested page
     */
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

    /**
     * Performs a search using only the main searcher without rank fusion.
     * This method is used when only one searcher is available or configured.
     *
     * @param searcher the main searcher to use for the search operation
     * @param query the search query string
     * @param params search request parameters including pagination and filters
     * @param userBean optional user information for personalized search
     * @return list of search result documents from the main searcher
     */
    protected List<Map<String, Object>> searchWithMainSearcher(final RankFusionSearcher searcher, final String query,
            final SearchRequestParams params, final OptionalThing<FessUserBean> userBean) {
        if (logger.isDebugEnabled()) {
            logger.debug("Send {} to the main searcher.", query);
        }
        final int pageSize = params.getPageSize();
        final SearchResult searchResult = searcher.search(query, params, userBean);
        return createResponseList(searchResult.getDocumentList(), searchResult.getAllRecordCount(),
                searchResult.getAllRecordCountRelation(), searchResult.getQueryTime(), searchResult.isPartialResults(),
                searchResult.getFacetResponse(), params.getStartPosition(), pageSize, 0);
    }

    /**
     * Creates a QueryResponseList containing the search results and metadata.
     * Wraps the document list with additional information about the search operation
     * including record counts, timing, and pagination details.
     *
     * @param documentList the list of search result documents
     * @param allRecordCount the total number of records found
     * @param allRecordCountRelation the relationship of the record count (exact, approximate, etc.)
     * @param queryTime the time taken to execute the search query
     * @param partialResults whether the results are partial due to timeout or other constraints
     * @param facetResponse the facet information for the search results
     * @param start the starting position for pagination
     * @param pageSize the size of the current page
     * @param offset the offset applied to the results
     * @return QueryResponseList containing the search results and metadata
     */
    protected QueryResponseList createResponseList(final List<Map<String, Object>> documentList, final long allRecordCount,
            final String allRecordCountRelation, final long queryTime, final boolean partialResults, final FacetResponse facetResponse,
            final int start, final int pageSize, final int offset) {
        return new QueryResponseList(documentList, allRecordCount, allRecordCountRelation, queryTime, partialResults, facetResponse, start,
                pageSize, offset);
    }

    /**
     * Converts an object value to a float for score calculations.
     * Handles Float and String types, returning 0.0f for unsupported types.
     *
     * @param value the object to convert to float
     * @return float representation of the value, or 0.0f if conversion fails
     */
    protected float toFloat(final Object value) {
        if (value instanceof final Float f) {
            return f;
        }
        if (value instanceof final String s) {
            return Float.parseFloat(s);
        }
        return 0.0f;
    }

    /**
     * Wrapper class for SearchRequestParams that allows overriding specific parameters.
     * This wrapper is used to modify pagination parameters while preserving all other
     * search request parameters from the parent object.
     */
    protected static class SearchRequestParamsWrapper extends SearchRequestParams {
        private final SearchRequestParams parent;
        private final int startPosition;
        private final int pageSize;

        SearchRequestParamsWrapper(final SearchRequestParams parent, final int startPosition, final int pageSize) {
            this.parent = parent;
            this.startPosition = startPosition;
            this.pageSize = pageSize;
        }

        /**
         * Gets the parent SearchRequestParams object that this wrapper delegates to.
         *
         * @return the parent SearchRequestParams instance
         */
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

    /**
     * Sets the main searcher at index 0 of the searchers array.
     * This method is used to configure the primary searcher for rank fusion processing.
     *
     * @param searcher the RankFusionSearcher to set as the main searcher
     */
    public void setSeacher(final RankFusionSearcher searcher) {
        searchers[0] = searcher;
    }

    /**
     * Registers a new searcher with the rank fusion processor.
     * Adds the searcher to the searchers array and initializes the executor service
     * if it hasn't been created yet. The executor service is created with a thread pool
     * sized based on configuration or system capabilities.
     *
     * @param searcher the RankFusionSearcher to register
     */
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
                    numThreads = Runtime.getRuntime().availableProcessors() * 3 / 2 + 1;
                }
                executorService = Executors.newFixedThreadPool(numThreads);
            }
        }
    }
}
