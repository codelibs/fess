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
package org.codelibs.fess.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.TotalHits.Relation;
import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.core.exception.InterruptedRuntimeException;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.entity.RequestParameter;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.SearchQueryException;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchConditionBuilder;
import org.codelibs.fess.opensearch.client.SearchEngineClientException;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.query.StructuredQuerySplitter;
import org.codelibs.fess.rank.fusion.DefaultSearcher;
import org.codelibs.fess.rank.fusion.RankFusionProcessor;
import org.codelibs.fess.util.BooleanFunction;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.codelibs.fess.util.LogUtil;
import org.codelibs.fess.util.QueryResponseList;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.dbflute.util.DfTypeUtil;
import org.lastaflute.taglib.function.LaFunctions;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaResponseUtil;
import org.codelibs.fesen.opensearch.OpenSearchException;
import org.codelibs.fesen.opensearch.action.DocWriteResponse.Result;
import org.codelibs.fesen.opensearch.action.bulk.BulkRequestBuilder;
import org.codelibs.fesen.opensearch.action.bulk.BulkResponse;
import org.codelibs.fesen.opensearch.action.update.UpdateRequestBuilder;
import org.codelibs.fesen.opensearch.action.update.UpdateResponse;
import org.codelibs.fesen.opensearch.action.search.SearchResponse;
import org.codelibs.fesen.opensearch.common.document.DocumentField;
import org.codelibs.fesen.opensearch.index.query.BoolQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;
import org.codelibs.fesen.opensearch.search.SearchHit;
import org.codelibs.fesen.opensearch.search.sort.SortOrder;

import tools.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Helper class for handling search operations in Fess.
 *
 * This class provides comprehensive search functionality including document search,
 * scroll search, and bulk operations. It handles search request parameter processing,
 * query building, response formatting, and search log management.
 *
 * Key features:
 * - Document search with pagination and faceting
 * - Scroll search for large result sets
 * - Document retrieval by ID
 * - Bulk document updates
 * - Search parameter serialization/deserialization for cookies
 * - Integration with search engines and logging systems
 */
public class SearchHelper {

    // ===================================================================================
    //                                                                            Constant
    //

    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(SearchHelper.class);

    // ===================================================================================
    //                                                                            Variable
    //

    /**
     * Array of search request parameter rewriters for modifying search parameters. Request threads
     * read the array while {@link #addRewriter} replaces it, so it is never modified in place.
     */
    protected volatile SearchRequestParamsRewriter[] searchRequestParamsRewriters = {};

    /** Reads the additional query of the query helper as conditions. */
    private final StructuredQuerySplitter querySplitter = new StructuredQuerySplitter();

    /** Jackson ObjectMapper for JSON serialization/deserialization. */
    protected ObjectMapper mapper = new ObjectMapper();

    /**
     * Default constructor for creating a new SearchHelper instance.
     */
    public SearchHelper() {
        // Default constructor
    }

    // ===================================================================================
    //                                                                              Method
    //                                                                      ==============

    /**
     * Performs a search operation and populates the search render data with results.
     *
     * This method handles the complete search workflow including parameter processing,
     * query execution, result formatting, and logging. It supports automatic retry
     * with escaped queries if the initial search fails.
     *
     * @param searchRequestParams The search request parameters
     * @param data The search render data to populate with results
     * @param userBean Optional user information for permission checking
     */
    public void search(final SearchRequestParams searchRequestParams, final SearchRenderData data,
            final OptionalThing<FessUserBean> userBean) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final long startTime = systemHelper.getCurrentTimeAsLong();
        final long requestedTime = startTime;

        final SearchRequestParams params = rewrite(searchRequestParams);
        if (params.isRedirected()) {
            data.setRedirectUrl(params.getRedirectUrl());
            data.setDocumentItems(new QueryResponseList(Collections.emptyList(), 0, Relation.EQUAL_TO.toString(), 0, false, null,
                    params.getStartPosition(), params.getPageSize(), 0));
            data.setRequestedTime(requestedTime);
            return;
        }

        LaRequestUtil.getOptionalRequest().ifPresent(request -> {
            request.setAttribute(Constants.REQUEST_LANGUAGES, params.getLanguages());
            request.setAttribute(Constants.REQUEST_QUERIES, params.getQuery());
        });

        String query = ComponentUtil.getQueryStringBuilder().params(params).sortField(params.getSort()).build();
        List<Map<String, Object>> documentItems;
        try {
            documentItems = searchInternal(query, params, userBean);
        } catch (final InvalidQueryException e) {
            // A SearchEngineUnavailableException is not caught here on purpose: a search engine that
            // has just refused the search for lack of capacity is not helped by a second one.
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid query: {}", query, e);
            }
            query = ComponentUtil.getQueryStringBuilder().params(params).sortField(params.getSort()).escape(true).build();
            documentItems = searchInternal(query, params, userBean);
        }

        data.setDocumentItems(documentItems);

        // search
        final QueryResponseList queryResponseList = (QueryResponseList) documentItems;
        data.setFacetResponse(queryResponseList.getFacetResponse());

        @SuppressWarnings("unchecked")
        final Set<String> highlightQueries = (Set<String>) params.getAttribute(Constants.HIGHLIGHT_QUERIES);
        if (highlightQueries != null) {
            final StringBuilder buf = new StringBuilder(100);
            highlightQueries.stream().forEach(q -> {
                buf.append("&hq=").append(LaFunctions.u(q));
            });
            data.setAppendHighlightParams(buf.toString());
        }

        queryResponseList.setExecTime(systemHelper.getCurrentTimeAsLong() - startTime);
        data.setExecTime(formatExecTime(queryResponseList.getExecTime(), params.getLocale()));

        final String queryId = ComponentUtil.getQueryHelper().generateId();

        data.setPageSize(queryResponseList.getPageSize());
        data.setCurrentPageNumber(queryResponseList.getCurrentPageNumber());
        data.setAllRecordCount(queryResponseList.getAllRecordCount());
        data.setAllRecordCountRelation(queryResponseList.getAllRecordCountRelation());
        data.setAllPageCount(queryResponseList.getAllPageCount());
        data.setOffset(queryResponseList.getOffset());
        data.setExistNextPage(queryResponseList.isExistNextPage());
        data.setExistPrevPage(queryResponseList.isExistPrevPage());
        data.setCurrentStartRecordNumber(queryResponseList.getCurrentStartRecordNumber());
        data.setCurrentEndRecordNumber(queryResponseList.getCurrentEndRecordNumber());
        data.setPageNumberList(queryResponseList.getPageNumberList());
        data.setPartialResults(queryResponseList.isPartialResults());
        data.setTimedOut(queryResponseList.isTimedOut());
        data.setShardFailed(queryResponseList.isShardFailed());
        data.setQueryTime(queryResponseList.getQueryTime());
        data.setSearchQuery(query);
        data.setRequestedTime(requestedTime);
        data.setQueryId(queryId);

        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        // search log
        if (fessConfig.isSearchLog()) {
            ComponentUtil.getSearchLogHelper()
                    .addSearchLog(params, DfTypeUtil.toLocalDateTime(requestedTime), queryId, query, params.getStartPosition(),
                            params.getPageSize(), queryResponseList);
        }

        // favorite
        if (fessConfig.isUserFavorite()) {
            ComponentUtil.getUserInfoHelper().storeQueryId(queryId, documentItems);
        }

    }

    /**
     * Formats the time a search took the way the search result shows it: seconds with at most two
     * decimals and two integer digits.
     *
     * @param execTimeMillis the time in milliseconds
     * @param locale the locale of the number format
     * @return the time in seconds, or an empty string if it cannot be formatted
     */
    public String formatExecTime(final long execTimeMillis, final Locale locale) {
        final NumberFormat nf = NumberFormat.getInstance(locale);
        nf.setMaximumIntegerDigits(2);
        nf.setMaximumFractionDigits(2);
        try {
            return nf.format((double) execTimeMillis / 1000);
        } catch (final Exception e) {
            return StringUtil.EMPTY;
        }
    }

    /**
     * Internal search method that executes the actual search query.
     *
     * This method performs the search using the rank fusion processor and may retry
     * with OR operator if the hit count is below the configured minimum threshold.
     *
     * @param query The search query string
     * @param params The search request parameters
     * @param userBean Optional user information for permission checking
     * @return List of search result documents
     */
    protected List<Map<String, Object>> searchInternal(final String query, final SearchRequestParams params,
            final OptionalThing<FessUserBean> userBean) {
        final RankFusionProcessor rankFusionProcessor = ComponentUtil.getRankFusionProcessor();
        final List<Map<String, Object>> documentItems = rankFusionProcessor.search(query, params, userBean);
        if (documentItems instanceof final QueryResponseList queryResponseList) {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            if (queryResponseList.getAllRecordCount() <= fessConfig.getQueryOrsearchMinHitCountAsInteger()) {
                return LaRequestUtil.getOptionalRequest().map(request -> {
                    request.setAttribute(Constants.DEFAULT_QUERY_OPERATOR, "OR");
                    if (logger.isDebugEnabled()) {
                        logger.debug("The number of hits is {}<={}. Searching again with OR operator.",
                                queryResponseList.getAllRecordCount(), fessConfig.getQueryOrsearchMinHitCountAsInteger());
                    }
                    return rankFusionProcessor.search(query, params, userBean);
                }).orElse(queryResponseList);
            }
        }
        return documentItems;
    }

    /**
     * Performs a scroll search for processing large result sets efficiently.
     *
     * This method uses OpenSearch scroll API to iterate through large numbers of
     * documents without loading them all into memory at once.
     *
     * @param params The search request parameters
     * @param cursor Function to process each document in the result set
     * @param userBean Optional user information for permission checking
     * @return Total number of documents processed
     */
    public long scrollSearch(final SearchRequestParams params, final BooleanFunction<Map<String, Object>> cursor,
            final OptionalThing<FessUserBean> userBean) {
        LaRequestUtil.getOptionalRequest().ifPresent(request -> {
            request.setAttribute(Constants.REQUEST_LANGUAGES, params.getLanguages());
            request.setAttribute(Constants.REQUEST_QUERIES, params.getQuery());
        });

        final int pageSize = params.getPageSize();
        final String query = ComponentUtil.getQueryStringBuilder().params(params).sortField(params.getSort()).build();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return ComponentUtil.getSearchEngineClient()
                .<Map<String, Object>> scrollSearch(fessConfig.getIndexDocumentSearchIndex(), searchRequestBuilder -> {
                    final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
                    final QueryFieldConfig queryFieldConfig = ComponentUtil.getQueryFieldConfig();
                    queryHelper.processSearchPreference(searchRequestBuilder, userBean, query);
                    return SearchConditionBuilder.builder(searchRequestBuilder)
                            .scroll()
                            .query(query)
                            .size(pageSize)
                            .responseFields(queryFieldConfig.getScrollResponseFields())
                            .searchRequestType(params.getType())
                            .filterQueries(params.getFilterQueries())
                            .build();
                }, (searchResponse, hit) -> {
                    final Map<String, Object> docMap = new HashMap<>();
                    final Map<String, Object> source = hit.getSourceAsMap();
                    if (source != null) {
                        docMap.putAll(source);
                    }
                    final Map<String, DocumentField> fields = hit.getFields();
                    if (fields != null) {
                        docMap.putAll(fields.entrySet()
                                .stream()
                                .collect(Collectors.toMap(Entry::getKey, e -> (Object) e.getValue().getValues())));
                    }

                    final ViewHelper viewHelper = ComponentUtil.getViewHelper();
                    if (viewHelper != null && !docMap.isEmpty()) {
                        docMap.put(fessConfig.getResponseFieldContentTitle(), viewHelper.getContentTitle(docMap));
                        docMap.put(fessConfig.getResponseFieldContentDescription(), viewHelper.getContentDescription(docMap));
                        docMap.put(fessConfig.getResponseFieldUrlLink(), viewHelper.getUrlLink(docMap));
                        docMap.put(fessConfig.getResponseFieldSitePath(), viewHelper.getSitePath(docMap));
                    }

                    if (!docMap.containsKey(Constants.SCORE)) {
                        final float score = hit.getScore();
                        if (Float.isFinite(score)) {
                            docMap.put(Constants.SCORE, score);
                        }
                    }

                    docMap.put(fessConfig.getIndexFieldId(), hit.getId());
                    docMap.put(fessConfig.getIndexFieldVersion(), hit.getVersion());
                    docMap.put(fessConfig.getIndexFieldSeqNo(), hit.getSeqNo());
                    docMap.put(fessConfig.getIndexFieldPrimaryTerm(), hit.getPrimaryTerm());
                    return docMap;
                }, cursor);
    }

    /**
     * Deletes documents matching the specified search parameters.
     *
     * @param request The HTTP servlet request
     * @param params The search request parameters to identify documents to delete
     * @return Number of documents deleted
     */
    public long deleteByQuery(final HttpServletRequest request, final SearchRequestParams params) {
        final String query = ComponentUtil.getQueryStringBuilder().params(params).build();

        final QueryContext queryContext = ComponentUtil.getQueryHelper().build(params.getType(), query, context -> {
            context.skipRoleQuery();
            final List<QueryBuilder> filterQueries = params.getFilterQueries();
            if (!filterQueries.isEmpty()) {
                context.addQuery(boolQuery -> filterQueries.forEach(boolQuery::filter));
            }
        });
        return ComponentUtil.getSearchEngineClient()
                .deleteByQuery(ComponentUtil.getFessConfig().getIndexDocumentUpdateIndex(), queryContext.getQueryBuilder());
    }

    /**
     * Extracts and normalizes language preferences from request parameters or browser locale.
     *
     * This method prioritizes explicit language parameters over browser locale settings
     * and handles special cases like "all languages" selection.
     *
     * @param request The HTTP servlet request
     * @param params The search request parameters
     * @return Array of normalized language codes
     */
    public String[] getLanguages(final HttpServletRequest request, final SearchRequestParams params) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        if (params.getLanguages() != null) {
            final Set<String> langSet = new HashSet<>();
            for (final String lang : params.getLanguages()) {
                if (StringUtil.isNotBlank(lang) && lang.length() < 1000) {
                    if (Constants.ALL_LANGUAGES.equalsIgnoreCase(lang)) {
                        langSet.add(Constants.ALL_LANGUAGES);
                    } else {
                        final String normalizeLang = systemHelper.normalizeLang(lang);
                        if (normalizeLang != null) {
                            langSet.add(normalizeLang);
                        }
                    }
                }
            }
            if (langSet.size() > 1 && langSet.contains(Constants.ALL_LANGUAGES)) {
                return new String[] { Constants.ALL_LANGUAGES };
            }
            langSet.remove(Constants.ALL_LANGUAGES);
            return langSet.toArray(new String[langSet.size()]);
        }
        if (ComponentUtil.getFessConfig().isBrowserLocaleForSearchUsed()) {
            final Set<String> langSet = new HashSet<>();
            final Enumeration<Locale> locales = request.getLocales();
            if (locales != null) {
                while (locales.hasMoreElements()) {
                    final Locale locale = locales.nextElement();
                    final String normalizeLang = systemHelper.normalizeLang(locale.toString());
                    if (normalizeLang != null) {
                        langSet.add(normalizeLang);
                    }
                }
                if (!langSet.isEmpty()) {
                    return langSet.toArray(new String[langSet.size()]);
                }
            }
        }
        return StringUtil.EMPTY_STRINGS;
    }

    /**
     * Retrieves a single document by its document ID.
     *
     * @param docId The document ID to retrieve
     * @param fields Array of field names to include in the result
     * @param userBean Optional user information for permission checking
     * @return Optional entity containing the document data if found
     */
    public OptionalEntity<Map<String, Object>> getDocumentByDocId(final String docId, final String[] fields,
            final OptionalThing<FessUserBean> userBean) {
        return getDocumentByDocId(docId, fields, userBean, Collections.emptyList());
    }

    /**
     * Retrieves a single document by its document ID, constrained by extra filter clauses.
     * Like a search, the lookup is limited to the roles of the caller and to the virtual host of the request.
     *
     * @param docId The document ID to retrieve
     * @param fields Array of field names to include in the result
     * @param userBean Optional user information for permission checking
     * @param filterQueries Filter clauses the document must also match, applied next to the role filter
     * @return Optional entity containing the document data if found
     */
    public OptionalEntity<Map<String, Object>> getDocumentByDocId(final String docId, final String[] fields,
            final OptionalThing<FessUserBean> userBean, final List<QueryBuilder> filterQueries) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return ComponentUtil.getSearchEngineClient().getDocument(fessConfig.getIndexDocumentSearchIndex(), builder -> {
            final BoolQueryBuilder boolQuery =
                    QueryBuilders.boolQuery().must(QueryBuilders.termQuery(fessConfig.getIndexFieldDocId(), docId));
            final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
            buildVisibilityQuery(boolQuery);
            if (filterQueries != null) {
                filterQueries.forEach(boolQuery::filter);
            }
            builder.setQuery(boolQuery);
            builder.setFetchSource(fields, null);
            queryHelper.processSearchPreference(builder, userBean, docId);
            return true;
        });

    }

    /**
     * Retrieves the documents that have the same content signature as a document, that is, the same
     * or nearly the same content.
     *
     * <p>The content signature ({@code content_minhash_bits}) is computed by the search engine when a
     * document is indexed. It is the signature that the duplicate report of the admin screen and the
     * {@code sdh} search parameter group documents by, so this finds the members of that group without
     * the result collapsing that shows them. A document whose content was edited enough to change the
     * signature is not found.</p>
     *
     * <p>The source document is looked up first, through the same role and virtual host filters as
     * {@link #getDocumentByDocId} and the additional query of the query helper. A document the caller cannot see therefore ends the call before the
     * search for the others, and looks exactly like a document that does not exist. The documents are
     * searched through the caller's role and virtual host filters and the additional query of the query
     * helper, so only documents the caller can see come back. The source document is excluded, and so
     * is every other document with the URL of the source, which is the same page under another set of
     * roles. The documents are ordered by URL and then document ID.</p>
     *
     * @param docId the document ID of the source document
     * @param size the maximum number of documents to return
     * @param responseFields the fields to read of each document, as a search reads them
     * @param userBean Optional user information for permission checking
     * @return the documents with the same signature; empty when the source document does not exist or is not visible to the caller
     */
    public OptionalEntity<QueryResponseList> getSimilarDocuments(final String docId, final int size, final String[] responseFields,
            final OptionalThing<FessUserBean> userBean) {
        final long startTime = System.currentTimeMillis();
        final OptionalEntity<SimilarSource> sourceOpt = findSimilarSource(docId, userBean);
        if (!sourceOpt.isPresent()) {
            return OptionalEntity.empty();
        }
        final SimilarSource source = sourceOpt.get();
        // Without the plugin there is no signature. A document without any word has every bit set, as
        // every other such document, and a document without content has no signature at all.
        if (ComponentUtil.getFessConfig().isFesenPluginless() || !isSignatureUsable(source.signature())) {
            return OptionalEntity
                    .of(createSimilarDocuments(Collections.emptyList(), 0, Relation.EQUAL_TO.toString(), 0, false, false, size));
        }
        final QueryBuilder query = buildSimilarDocumentsQuery(docId, source);
        if (query == null) {
            return OptionalEntity
                    .of(createSimilarDocuments(Collections.emptyList(), 0, Relation.EQUAL_TO.toString(), 0, false, false, size));
        }
        final QueryResponseList documents = searchSimilarDocuments(query, docId, size, responseFields, userBean);
        documents.setExecTime(System.currentTimeMillis() - startTime);
        return OptionalEntity.of(documents);
    }

    /**
     * The signature and the URL of the document a similar-document search starts from.
     *
     * @param signature the signature of the document
     * @param url the URL of the document
     */
    protected record SimilarSource(String signature, String url) {
    }

    /**
     * Adds the restrictions on what the caller may see to a query: the role filter and the virtual host
     * filter, the same ones {@link #getDocumentByDocId} applies.
     *
     * @param boolQuery the query to restrict
     */
    protected void buildVisibilityQuery(final BoolQueryBuilder boolQuery) {
        final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
        final Set<String> roleSet = ComponentUtil.getRoleQueryHelper().build(SearchRequestType.JSON);
        if (!roleSet.isEmpty()) {
            queryHelper.buildRoleQuery(roleSet, boolQuery);
        }
        queryHelper.buildVirtualHostQuery(ComponentUtil.getVirtualHostHelper().getVirtualHostKey(), boolQuery);
    }

    /**
     * Adds the additional query of the query helper to a query as a filter. The additional query is
     * appended to every search, usually to keep documents out of it (for example "-label:internal"),
     * so a document it excludes is not visible. It is a restriction: when it cannot be applied as a
     * filter, nothing can be returned safely.
     *
     * @param boolQuery the query to restrict
     * @return false when the additional query holds more than conditions and could not be applied
     */
    protected boolean applyAdditionalQuery(final BoolQueryBuilder boolQuery) {
        final String additionalQuery = ComponentUtil.getQueryHelper().getAdditionalQuery();
        if (StringUtil.isBlank(additionalQuery)) {
            return true;
        }
        final QueryBuilder additional = getQuerySplitter().conditionsOf(additionalQuery, SearchRequestType.JSON);
        if (additional == null) {
            LogUtil.warnOnce(logger, "similarAdditionalQuery",
                    "The additional query of the query helper holds more than conditions, so no similar documents are returned: {}",
                    additionalQuery);
            return false;
        }
        boolQuery.filter(additional);
        return true;
    }

    /**
     * Gets the splitter that reads the additional query of the query helper as conditions.
     *
     * @return the splitter
     */
    protected StructuredQuerySplitter getQuerySplitter() {
        return querySplitter;
    }

    /**
     * Looks up the document a similar-document search starts from, through the role and virtual host
     * filters of the caller and the additional query of the query helper, so that it is visible under
     * the same filters as in a search, and reads its content signature.
     *
     * @param docId the document ID
     * @param userBean Optional user information for permission checking
     * @return the source, empty when the document does not exist or is not visible to the caller
     */
    protected OptionalEntity<SimilarSource> findSimilarSource(final String docId, final OptionalThing<FessUserBean> userBean) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String hashField = fessConfig.getIndexFieldContentMinhashBits();
        return ComponentUtil.getSearchEngineClient().search(fessConfig.getIndexDocumentSearchIndex(), builder -> {
            final BoolQueryBuilder boolQuery =
                    QueryBuilders.boolQuery().must(QueryBuilders.termQuery(fessConfig.getIndexFieldDocId(), docId));
            buildVisibilityQuery(boolQuery);
            if (!applyAdditionalQuery(boolQuery)) {
                // no search: a source that the additional query might exclude is not visible
                return false;
            }
            ComponentUtil.getQueryHelper().processSearchPreference(builder, userBean, docId);
            builder.setQuery(boolQuery).setSize(1).setFetchSource(new String[] { fessConfig.getIndexFieldUrl() }, null);
            builder.request().source().storedField(hashField);
            return true;
        }, (builder, execTime, searchResponse) -> {
            if (!searchResponse.isPresent() || searchResponse.get().getHits().getHits().length == 0) {
                return OptionalEntity.<SimilarSource> empty();
            }
            final SearchHit hit = searchResponse.get().getHits().getHits()[0];
            final DocumentField signature = hit.getFields().get(hashField);
            final Map<String, Object> hitSource = hit.getSourceAsMap();
            return OptionalEntity
                    .of(new SimilarSource(signature != null && signature.getValue() != null ? signature.getValue().toString() : null,
                            hitSource != null ? DocumentUtil.getValue(hitSource, fessConfig.getIndexFieldUrl(), String.class) : null));
        });
    }

    /**
     * Checks that a signature can be searched for: it is not empty and not the signature of a document
     * without a single word, which has every bit set and is shared by every such document.
     *
     * @param signature the content signature, or null
     * @return true if documents with the same signature are similar
     */
    protected static boolean isSignatureUsable(final String signature) {
        return StringUtil.isNotBlank(signature) && signature.chars().anyMatch(c -> c != '1');
    }

    /**
     * Builds the query for the documents with the same signature as the source: the signature is an
     * exact {@code term}, and everything that decides what the caller may see is a {@code filter}
     * clause next to it. The source and the other documents with its URL are excluded. The signature of
     * a document without a word is not searched for, as {@link #isSignatureUsable} rejects it.
     *
     * @param docId the document ID of the source document
     * @param source the source document
     * @return the query, or null when a restriction of the search cannot be applied to it and no document can be returned safely
     */
    protected QueryBuilder buildSimilarDocumentsQuery(final String docId, final SimilarSource source) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String hashField = fessConfig.getIndexFieldContentMinhashBits();
        final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(hashField, source.signature()))
                .mustNot(QueryBuilders.termQuery(fessConfig.getIndexFieldDocId(), docId));
        if (StringUtil.isNotBlank(source.url())) {
            boolQuery.mustNot(QueryBuilders.termQuery(fessConfig.getIndexFieldUrl(), source.url()));
        }
        buildVisibilityQuery(boolQuery);
        if (!applyAdditionalQuery(boolQuery)) {
            return null;
        }
        return boolQuery;
    }

    /**
     * Searches the documents with the same signature. Every document matches with the same score, so
     * they are ordered by URL and then document ID, which does not change between requests. A hit
     * becomes a document the way a hit of a search does, and the total is counted the way a search
     * counts it.
     *
     * @param query the query of {@link #buildSimilarDocumentsQuery}
     * @param docId the document ID of the source document
     * @param size the maximum number of documents
     * @param responseFields the fields to read of each document
     * @param userBean Optional user information for permission checking
     * @return the documents
     */
    protected QueryResponseList searchSimilarDocuments(final QueryBuilder query, final String docId, final int size,
            final String[] responseFields, final OptionalThing<FessUserBean> userBean) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return ComponentUtil.getSearchEngineClient().search(fessConfig.getIndexDocumentSearchIndex(), searchRequestBuilder -> {
            ComponentUtil.getQueryHelper().processSearchPreference(searchRequestBuilder, userBean, docId);
            searchRequestBuilder.setQuery(query).setSize(size).setFetchSource(responseFields, null);
            if (fessConfig.getQueryTrackTotalHitsValue() instanceof final Number upTo) {
                searchRequestBuilder.setTrackTotalHitsUpTo(upTo.intValue());
            } else {
                searchRequestBuilder.setTrackTotalHits(true);
            }
            searchRequestBuilder.addSort(fessConfig.getIndexFieldUrl(), SortOrder.ASC)
                    .addSort(fessConfig.getIndexFieldDocId(), SortOrder.ASC);
            return true;
        }, (searchRequestBuilder, execTime, searchResponse) -> {
            if (!searchResponse.isPresent()) {
                return createSimilarDocuments(Collections.emptyList(), 0, Relation.EQUAL_TO.toString(), execTime, false, false, size);
            }
            final SearchResponse response = searchResponse.get();
            final String highlightPrefix = ComponentUtil.getQueryHelper().getHighlightPrefix();
            final List<Map<String, Object>> documents = new ArrayList<>();
            for (final SearchHit hit : response.getHits().getHits()) {
                documents.add(hitConverter.convert(fessConfig, highlightPrefix, hit));
            }
            return createSimilarDocuments(documents, response.getHits().getTotalHits().value(),
                    response.getHits().getTotalHits().relation().toString(), response.getTook().millis(), response.isTimedOut(),
                    response.getTotalShards() != response.getSuccessfulShards(), size);
        });
    }

    /** Turns a hit into a document with the code a search uses, which a searcher keeps to itself. */
    private static class HitConverter extends DefaultSearcher {
        Map<String, Object> convert(final FessConfig fessConfig, final String highlightPrefix, final SearchHit hit) {
            return parseSearchHit(fessConfig, highlightPrefix, hit);
        }
    }

    private final HitConverter hitConverter = new HitConverter();

    private QueryResponseList createSimilarDocuments(final List<Map<String, Object>> documents, final long total, final String relation,
            final long queryTime, final boolean timedOut, final boolean shardFailed, final int size) {
        return new QueryResponseList(documents, total, relation, queryTime, false, timedOut, shardFailed, null, 0, size, 0);
    }

    /**
     * Retrieves multiple documents by their document IDs.
     *
     * @param docIds Array of document IDs to retrieve
     * @param fields Array of field names to include in the results
     * @param userBean Optional user information for permission checking
     * @param searchRequestType Type of search request for role-based access control
     * @return List of document data maps
     */
    public List<Map<String, Object>> getDocumentListByDocIds(final String[] docIds, final String[] fields,
            final OptionalThing<FessUserBean> userBean, final SearchRequestType searchRequestType) {
        return getDocumentListByDocIds(docIds, fields, userBean, searchRequestType, Collections.emptyList());
    }

    /**
     * Retrieves multiple documents by their document IDs, constrained by extra filter clauses.
     * Like a search, the lookup is limited to the roles of the caller and to the virtual host of the request,
     * except for an admin search, which has neither.
     *
     * @param docIds Array of document IDs to retrieve
     * @param fields Array of field names to include in the results
     * @param userBean Optional user information for permission checking
     * @param searchRequestType Type of search request for role-based access control
     * @param filterQueries Filter clauses the documents must also match, applied next to the role filter
     * @return List of document data maps
     */
    public List<Map<String, Object>> getDocumentListByDocIds(final String[] docIds, final String[] fields,
            final OptionalThing<FessUserBean> userBean, final SearchRequestType searchRequestType, final List<QueryBuilder> filterQueries) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return ComponentUtil.getSearchEngineClient().getDocumentList(fessConfig.getIndexDocumentSearchIndex(), builder -> {
            final BoolQueryBuilder boolQuery =
                    QueryBuilders.boolQuery().must(QueryBuilders.termsQuery(fessConfig.getIndexFieldDocId(), docIds));
            final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
            if (searchRequestType != SearchRequestType.ADMIN_SEARCH) {
                final Set<String> roleSet = ComponentUtil.getRoleQueryHelper().build(searchRequestType);
                if (!roleSet.isEmpty()) {
                    queryHelper.buildRoleQuery(roleSet, boolQuery);
                }
                queryHelper.buildVirtualHostQuery(ComponentUtil.getVirtualHostHelper().getVirtualHostKey(), boolQuery);
            }
            if (filterQueries != null) {
                filterQueries.forEach(boolQuery::filter);
            }
            builder.setQuery(boolQuery);
            builder.setSize(fessConfig.getPagingSearchPageMaxSizeAsInteger());
            builder.setFetchSource(fields, null);
            queryHelper.processSearchPreference(builder, userBean, String.join(StringUtil.EMPTY, docIds));
            return true;
        });
    }

    /**
     * Updates a single field of a document.
     *
     * @param id The document ID to update
     * @param field The field name to update
     * @param value The new value for the field
     * @return true if the update was successful, false otherwise
     */
    public boolean update(final String id, final String field, final Object value) {
        return ComponentUtil.getSearchEngineClient().update(ComponentUtil.getFessConfig().getIndexDocumentUpdateIndex(), id, field, value);
    }

    /**
     * Updates a document using a custom update request builder.
     *
     * @param id The document ID to update
     * @param builderLambda Consumer function to configure the update request builder
     * @return true if the update was successful, false otherwise
     */
    public boolean update(final String id, final Consumer<UpdateRequestBuilder> builderLambda) {
        try {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final UpdateRequestBuilder builder =
                    ComponentUtil.getSearchEngineClient().prepareUpdate().setIndex(fessConfig.getIndexDocumentUpdateIndex()).setId(id);
            builderLambda.accept(builder);
            final UpdateResponse response = builder.execute().actionGet(fessConfig.getIndexIndexTimeout());
            return response.getResult() == Result.CREATED || response.getResult() == Result.UPDATED;
        } catch (final OpenSearchException e) {
            throw new SearchEngineClientException("Failed to update doc  " + id, e);
        }
    }

    /**
     * Performs bulk update operations using a custom bulk request builder.
     *
     * @param consumer Consumer function to configure the bulk request builder
     * @return true if all bulk operations were successful, false otherwise
     * @throws InterruptedRuntimeException if the operation is interrupted
     * @throws SearchEngineClientException if the bulk update fails
     */
    public boolean bulkUpdate(final Consumer<BulkRequestBuilder> consumer) {
        final BulkRequestBuilder builder = ComponentUtil.getSearchEngineClient().prepareBulk();
        consumer.accept(builder);
        try {
            final BulkResponse response = builder.execute().get();
            if (response.hasFailures()) {
                throw new SearchEngineClientException(response.buildFailureMessage());
            }
            return true;
        } catch (final InterruptedException e) {
            throw new InterruptedRuntimeException(e);
        } catch (final ExecutionException e) {
            throw new SearchEngineClientException("Failed to update bulk data.", e);
        }
    }

    /**
     * Checks whether the value is an absolute http or https URL with a host.
     *
     * @param url the value to check
     * @return true if it is an absolute http(s) URL
     */
    protected boolean isHttpUrl(final String url) {
        try {
            final URI uri = new URI(url);
            return ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                    && StringUtil.isNotBlank(uri.getHost());
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * Applies registered parameter rewriters to modify search request parameters.
     * Once a rewriter redirects the request, the rest are skipped. A redirect is dropped when
     * the caller did not {@link SearchRequestParams#enableRedirect() enable} it, and with a
     * warning when it is not an absolute http(s) URL.
     *
     * @param params The original search request parameters
     * @return Modified search request parameters after applying all rewriters
     */
    protected SearchRequestParams rewrite(final SearchRequestParams params) {
        // Decided by the caller: a rewriter may return parameters that do not carry the flag.
        final boolean redirectable = params.isRedirectable();
        SearchRequestParams newParams = params;
        for (final SearchRequestParamsRewriter rewriter : searchRequestParamsRewriters) {
            newParams = rewriter.rewrite(newParams);
            if (newParams.isRedirected()) {
                if (!redirectable) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Ignoring a redirect the caller cannot follow: rewriter={}, url={}",
                                rewriter.getClass().getSimpleName(), newParams.getRedirectUrl());
                    }
                    newParams.redirectTo(null);
                    continue;
                }
                if (isHttpUrl(newParams.getRedirectUrl())) {
                    break;
                }
                logger.warn("Ignoring a redirect that is not an absolute http(s) URL: rewriter={}, url={}",
                        rewriter.getClass().getSimpleName(), newParams.getRedirectUrl());
                newParams.redirectTo(null);
            }
        }
        return newParams;
    }

    /**
     * Adds a search request parameter rewriter to the list of active rewriters.
     *
     * @param rewriter The parameter rewriter to add
     */
    public synchronized void addRewriter(final SearchRequestParamsRewriter rewriter) {
        final SearchRequestParamsRewriter[] rewriters =
                Arrays.copyOf(searchRequestParamsRewriters, searchRequestParamsRewriters.length + 1);
        rewriters[rewriters.length - 1] = rewriter;
        searchRequestParamsRewriters = rewriters;
    }

    /**
     * Tells whether the current request carries every name in
     * {@code cookie.search.parameter.required_keys} with a value, which is what
     * {@link #storeSearchParameters()} requires before it stores anything.
     *
     * @return true if the required keys are configured and all present; false otherwise
     */
    public boolean hasRequiredSearchParameters() {
        return LaRequestUtil.getOptionalRequest().map(req -> {
            final String requiredKeysStr = ComponentUtil.getFessConfig().getCookieSearchParameterRequiredKeys();
            return StringUtil.isNotBlank(requiredKeysStr) && !isMissingRequiredParameter(req, requiredKeysStr);
        }).orElse(false);
    }

    private boolean isMissingRequiredParameter(final HttpServletRequest req, final String requiredKeysStr) {
        return StreamUtil.split(requiredKeysStr, ",")
                .get(stream -> stream.map(String::trim).filter(StringUtil::isNotEmpty).anyMatch(name -> {
                    final String[] values = req.getParameterValues(name);
                    if (values == null || values.length == 0 || StringUtil.isEmpty(values[0])) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Required parameter '{}' is missing or empty.", name);
                        }
                        return true;
                    }
                    return false;
                }));
    }

    /**
     * Stores current search parameters in a browser cookie for later retrieval.
     *
     * This method serializes the current request parameters, compresses them using GZIP,
     * encodes them with Base64, and stores them in a secure HTTP cookie.
     */
    public void storeSearchParameters() {
        LaRequestUtil.getOptionalRequest().ifPresent(req -> {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final String requiredKeysStr = fessConfig.getCookieSearchParameterRequiredKeys();
            if (StringUtil.isNotBlank(requiredKeysStr) && isMissingRequiredParameter(req, requiredKeysStr)) {
                return;
            }
            final String keysStr = fessConfig.getCookieSearchParameterKeys();
            if (StringUtil.isNotBlank(keysStr)) {
                final RequestParameter[] parameters = StreamUtil.split(keysStr, ",").get(stream -> stream.map(String::trim).map(s -> {
                    if (StringUtil.isEmpty(s)) {
                        return null;
                    }
                    final String[] values = req.getParameterValues(s);
                    if (values == null || values.length == 0) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Parameter '{}' is not present or has no value.", s);
                        }
                        return null;
                    }
                    return new RequestParameter(s, values);
                }).filter(o -> o != null).toArray(n -> new RequestParameter[n]));
                if (parameters.length == 0) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("No valid parameters found in request. Nothing to store.");
                    }
                    return;
                }
                try {
                    final String encoded = serializeParameters(parameters);
                    if (encoded.length() > fessConfig.getCookieSearchParameterMaxLengthAsInteger()) {
                        logger.warn("Encoded search parameters exceed the maximum cookie length: {} > {}. Skipping cookie storage.",
                                encoded.length(), fessConfig.getCookieSearchParameterMaxLengthAsInteger());
                        return;
                    }
                    LaResponseUtil.getOptionalResponse().ifPresent(res -> {
                        final Cookie cookie = new Cookie(fessConfig.getCookieSearchParameterName(), encoded);
                        cookie.setHttpOnly(Constants.TRUE.equalsIgnoreCase(fessConfig.getCookieSearchParameterHttpOnly()));
                        cookie.setSecure(isSearchParameterCookieSecure(req));
                        final String domain = fessConfig.getCookieSearchParameterDomain();
                        if (StringUtil.isNotBlank(domain)) {
                            cookie.setDomain(domain);
                        }
                        final String path = fessConfig.getCookieSearchParameterPath();
                        if (StringUtil.isNotBlank(path)) {
                            cookie.setPath(path);
                        }
                        cookie.setMaxAge(fessConfig.getCookieSearchParameterMaxAgeAsInteger());
                        cookie.setAttribute("SameSite", fessConfig.getCookieSearchParameterSameSite());
                        res.addCookie(cookie);
                        if (logger.isDebugEnabled()) {
                            logger.debug(
                                    "Stored search parameters in cookie: name={}, size={}, maxAge={}, path={}, domain={}, secure={}, httpOnly={}, sameSite={}",
                                    cookie.getName(), encoded.length(), cookie.getMaxAge(), cookie.getPath(), cookie.getDomain(),
                                    cookie.getSecure(), cookie.isHttpOnly(), fessConfig.getCookieSearchParameterSameSite());
                        }
                    });
                } catch (final Exception e) {
                    logger.warn("Failed to store search parameters in cookie.", e);
                }
            }
        });
    }

    /**
     * Serializes request parameters to a compressed and encoded string.
     *
     * @param parameters Array of request parameters to serialize
     * @return Base64-encoded, GZIP-compressed JSON string of parameters
     * @throws SearchQueryException if serialization fails
     */
    protected String serializeParameters(final RequestParameter[] parameters) {
        final List<Object[]> compactList = new ArrayList<>();
        for (final RequestParameter p : parameters) {
            compactList.add(new Object[] { p.getName(), p.getValues() });
        }
        try {
            final String json = mapper.writeValueAsString(compactList);
            final byte[] compressed = gzipCompress(json.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(compressed);
        } catch (final Exception e) {
            throw new SearchQueryException("Failed to serialize a query: " + Arrays.toString(parameters), e);
        }
    }

    /**
     * Compresses data using GZIP compression.
     *
     * @param data The data to compress
     * @return GZIP-compressed data
     * @throws IORuntimeException if compression fails
     */
    protected byte[] gzipCompress(final byte[] data) {
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (final GZIPOutputStream gzipOut = new GZIPOutputStream(bos)) {
                gzipOut.write(data);
            }
            return bos.toByteArray();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Retrieves and deserializes search parameters from browser cookies.
     *
     * @return Array of request parameters from the cookie, or empty array if none found
     */
    public RequestParameter[] getSearchParameters() {
        return LaRequestUtil.getOptionalRequest().map(req -> {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final String cookieName = fessConfig.getCookieSearchParameterName();
            final Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (final Cookie cookie : cookies) {
                    if (cookieName.equals(cookie.getName())) {
                        try {
                            final String encoded = cookie.getValue();
                            // The write side refuses to store a cookie longer than this, so a
                            // longer one did not come from this server. Applying the same bound
                            // here keeps the two sides symmetric and rejects an oversized value
                            // before it is decompressed.
                            final int maxLength = fessConfig.getCookieSearchParameterMaxLengthAsInteger();
                            if (encoded.length() > maxLength) {
                                logger.warn("Ignoring stored search parameters: the cookie is longer than {} characters.", maxLength);
                                return new RequestParameter[0];
                            }
                            final byte[] compressed = Base64.getUrlDecoder().decode(encoded);
                            final byte[] jsonBytes = gzipDecompress(compressed, getMaxDecompressedParameterLength(fessConfig));
                            final List<?> list = mapper.readValue(jsonBytes, List.class);

                            // The write side stores only the names in cookie.search.parameter.keys.
                            // The cookie is supplied by the client, so a name outside that list did
                            // not come from this server and there is nothing to restore it to.
                            final Set<String> allowedKeys = getCookieSearchParameterKeySet(fessConfig);
                            final List<RequestParameter> result = new ArrayList<>();
                            for (final Object item : list) {
                                if ((item instanceof final List<?> pair) && (pair.size() == 2 && pair.get(0) instanceof final String name
                                        && pair.get(1) instanceof final List<?> valueList)) {
                                    if (!allowedKeys.contains(name)) {
                                        if (logger.isDebugEnabled()) {
                                            logger.debug("Skipping a stored search parameter outside {}: {}",
                                                    fessConfig.getCookieSearchParameterKeys(), name);
                                        }
                                        continue;
                                    }
                                    final String[] values = valueList.stream().filter(String.class::isInstance).toArray(n -> new String[n]);
                                    result.add(new RequestParameter(name, values));
                                }
                            }
                            LaResponseUtil.getOptionalResponse().ifPresent(res -> {
                                final Cookie invalidCookie = new Cookie(fessConfig.getCookieSearchParameterName(), StringUtil.EMPTY);
                                invalidCookie.setHttpOnly(Constants.TRUE.equalsIgnoreCase(fessConfig.getCookieSearchParameterHttpOnly()));
                                invalidCookie.setSecure(isSearchParameterCookieSecure(req));
                                invalidCookie.setPath(fessConfig.getCookieSearchParameterPath());
                                invalidCookie.setMaxAge(0);
                                res.addCookie(invalidCookie);
                            });
                            return result.toArray(n -> new RequestParameter[n]);
                        } catch (final Exception e) {
                            logger.warn("Failed to deserialize search parameters from cookie.", e);
                            return new RequestParameter[0];
                        }
                    }
                }
            }
            return new RequestParameter[0];
        }).orElse(new RequestParameter[0]);
    }

    /**
     * Resolves {@code cookie.search.parameter.keys} into the set of names that may be restored.
     *
     * @param fessConfig the configuration to read the key list from
     * @return the allowed parameter names, empty when the list is blank
     */
    protected Set<String> getCookieSearchParameterKeySet(final FessConfig fessConfig) {
        final String keysStr = fessConfig.getCookieSearchParameterKeys();
        if (StringUtil.isBlank(keysStr)) {
            return Set.of();
        }
        return StreamUtil.split(keysStr, ",")
                .get(stream -> stream.map(String::trim).filter(StringUtil::isNotEmpty).collect(Collectors.toSet()));
    }

    /**
     * Resolves how far the stored search parameters may be allowed to decompress.
     *
     * <p>{@code cookie.search.parameter.max.length} bounds the <em>encoded</em> cookie, which is
     * gzip, so it is no bound on what that cookie expands to -- and the cookie arrives from the
     * client. Hence a bound of its own, and a configurable one: the shipped value is far above
     * anything the write side can produce, but a deployment that raises the encoded bound has to
     * be able to raise this with it.</p>
     *
     * <p>The generated accessor answers null for a <em>blank</em> value, and this is read on the
     * login path, so a blank one falls back to the shipped default rather than throwing.</p>
     *
     * @param fessConfig the configuration to read the bound from
     * @return the maximum number of decompressed bytes to accept
     */
    protected int getMaxDecompressedParameterLength(final FessConfig fessConfig) {
        final Integer value = fessConfig.getCookieSearchParameterMaxDecompressedLengthAsInteger();
        return value != null ? value : 65536;
    }

    /**
     * Decompresses GZIP-compressed data, refusing to expand beyond the given bound.
     *
     * The only caller that does not compress its own input is the stored-parameter cookie, which
     * the client supplies. Stopping at the bound rather than reading to EOF keeps a small, highly
     * compressible cookie from allocating without limit.
     *
     * @param compressed The GZIP-compressed data to decompress
     * @param maxLength The maximum number of decompressed bytes to accept
     * @return Decompressed data
     * @throws IORuntimeException if decompression fails or the data expands beyond maxLength
     */
    protected byte[] gzipDecompress(final byte[] compressed, final int maxLength) {
        try (final ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
                final GZIPInputStream gzipIn = new GZIPInputStream(bis);
                final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipIn.read(buffer)) > 0) {
                if (bos.size() + len > maxLength) {
                    throw new IORuntimeException(new IOException("Compressed data expands beyond " + maxLength + " bytes."));
                }
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Determines if the search parameter cookie should be secure based on configuration and request.
     *
     * @param request The HTTP request
     * @return true if the cookie should have the secure flag set
     */
    protected boolean isSearchParameterCookieSecure(final HttpServletRequest request) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String secure = fessConfig.getCookieSearchParameterSecure();
        if (StringUtil.isBlank(secure)) {
            final String forwardedProto = request.getHeader("X-Forwarded-Proto");
            if ("https".equalsIgnoreCase(forwardedProto)) {
                return true;
            }
            return request.isSecure();
        }
        return Constants.TRUE.equalsIgnoreCase(secure);
    }

    /**
     * Interface for rewriting search request parameters.
     *
     * Implementations can modify search parameters before they are processed
     * by the search engine, allowing for custom parameter transformation logic.
     * A rewriter can also call {@link SearchRequestParams#redirectTo} to send the
     * request elsewhere instead of searching: the search page redirects and
     * {@code /api/v2/search} returns the URL as {@code redirect_url}. Other callers
     * (chat, MCP, the v1 and classic JSON APIs, admin search, facet caching) cannot
     * follow a redirect, so it is ignored for them and the request is searched;
     * {@link SearchRequestParams#isRedirectable()} tells which case applies.
     * To look at the parsed query, parse {@link SearchRequestParams#getQuery()} with
     * the query parser; the query markers are applied.
     */
    public interface SearchRequestParamsRewriter {
        /**
         * Rewrites the given search request parameters.
         *
         * @param params The original search request parameters
         * @return Modified search request parameters
         */
        SearchRequestParams rewrite(SearchRequestParams params);
    }

}
