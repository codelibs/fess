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
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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
import org.codelibs.fess.rank.fusion.RankFusionProcessor;
import org.codelibs.fess.util.BooleanFunction;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.QueryResponseList;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.dbflute.util.DfTypeUtil;
import org.lastaflute.taglib.function.LaFunctions;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaResponseUtil;
import org.opensearch.OpenSearchException;
import org.opensearch.action.DocWriteResponse.Result;
import org.opensearch.action.bulk.BulkRequestBuilder;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.update.UpdateRequestBuilder;
import org.opensearch.action.update.UpdateResponse;
import org.opensearch.common.document.DocumentField;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

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

    /** Array of search request parameter rewriters for modifying search parameters. */
    protected SearchRequestParamsRewriter[] searchRequestParamsRewriters = {};

    /** Jackson ObjectMapper for JSON serialization/deserialization. */
    protected ObjectMapper mapper = new ObjectMapper();;

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

        LaRequestUtil.getOptionalRequest().ifPresent(request -> {
            request.setAttribute(Constants.REQUEST_LANGUAGES, params.getLanguages());
            request.setAttribute(Constants.REQUEST_QUERIES, params.getQuery());
        });

        String query = ComponentUtil.getQueryStringBuilder().params(params).sortField(params.getSort()).build();
        List<Map<String, Object>> documentItems;
        try {
            documentItems = searchInternal(query, params, userBean);
        } catch (final InvalidQueryException e) {
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
        final NumberFormat nf = NumberFormat.getInstance(params.getLocale());
        nf.setMaximumIntegerDigits(2);
        nf.setMaximumFractionDigits(2);
        String execTime;
        try {
            execTime = nf.format((double) queryResponseList.getExecTime() / 1000);
        } catch (final Exception e) {
            execTime = StringUtil.EMPTY;
        }
        data.setExecTime(execTime);

        final String queryId = ComponentUtil.getQueryHelper().generateId();

        data.setPageSize(queryResponseList.getPageSize());
        data.setCurrentPageNumber(queryResponseList.getCurrentPageNumber());
        data.setAllRecordCount(queryResponseList.getAllRecordCount());
        data.setAllRecordCountRelation(queryResponseList.getAllRecordCountRelation());
        data.setAllPageCount(queryResponseList.getAllPageCount());
        data.setExistNextPage(queryResponseList.isExistNextPage());
        data.setExistPrevPage(queryResponseList.isExistPrevPage());
        data.setCurrentStartRecordNumber(queryResponseList.getCurrentStartRecordNumber());
        data.setCurrentEndRecordNumber(queryResponseList.getCurrentEndRecordNumber());
        data.setPageNumberList(queryResponseList.getPageNumberList());
        data.setPartialResults(queryResponseList.isPartialResults());
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
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return ComponentUtil.getSearchEngineClient().getDocument(fessConfig.getIndexDocumentSearchIndex(), builder -> {
            final BoolQueryBuilder boolQuery =
                    QueryBuilders.boolQuery().must(QueryBuilders.termQuery(fessConfig.getIndexFieldDocId(), docId));
            final Set<String> roleSet = ComponentUtil.getRoleQueryHelper().build(SearchRequestType.JSON); // TODO SearchRequestType?
            final QueryHelper queryHelper = ComponentUtil.getQueryHelper();
            if (!roleSet.isEmpty()) {
                queryHelper.buildRoleQuery(roleSet, boolQuery);
            }
            builder.setQuery(boolQuery);
            builder.setFetchSource(fields, null);
            queryHelper.processSearchPreference(builder, userBean, docId);
            return true;
        });

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
     * Applies registered parameter rewriters to modify search request parameters.
     *
     * @param params The original search request parameters
     * @return Modified search request parameters after applying all rewriters
     */
    protected SearchRequestParams rewrite(final SearchRequestParams params) {
        SearchRequestParams newParams = params;
        for (final SearchRequestParamsRewriter rewriter : searchRequestParamsRewriters) {
            newParams = rewriter.rewrite(newParams);
        }
        return newParams;
    }

    /**
     * Adds a search request parameter rewriter to the list of active rewriters.
     *
     * @param rewriter The parameter rewriter to add
     */
    public void addRewriter(final SearchRequestParamsRewriter rewriter) {
        searchRequestParamsRewriters = Arrays.copyOf(searchRequestParamsRewriters, searchRequestParamsRewriters.length + 1);
        searchRequestParamsRewriters[searchRequestParamsRewriters.length - 1] = rewriter;
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
            if (StringUtil.isNotBlank(requiredKeysStr) && StreamUtil.split(requiredKeysStr, ",")
                    .get(stream -> stream.map(String::trim).filter(StringUtil::isNotEmpty).anyMatch(name -> {
                        final String[] values = req.getParameterValues(name);
                        if (values == null || values.length == 0 || StringUtil.isEmpty(values[0])) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Required parameter '{}' is missing or empty. Skip storing search parameters.", name);
                            }
                            return true;
                        }
                        return false;
                    }))) {
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
                        final String secure = fessConfig.getCookieSearchParameterSecure();
                        if (StringUtil.isBlank(secure)) {
                            final String forwardedProto = req.getHeader("X-Forwarded-Proto");
                            if ("https".equalsIgnoreCase(forwardedProto)) {
                                cookie.setSecure(true);
                            } else {
                                cookie.setSecure(req.isSecure());
                            }
                        } else {
                            cookie.setSecure(Constants.TRUE.equalsIgnoreCase(secure));
                        }
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
                            final byte[] compressed = Base64.getUrlDecoder().decode(encoded);
                            final byte[] jsonBytes = gzipDecompress(compressed);
                            final List<?> list = mapper.readValue(jsonBytes, List.class);

                            final List<RequestParameter> result = new ArrayList<>();
                            for (Object item : list) {
                                if (item instanceof List<?> pair) {
                                    if (pair.size() == 2 && pair.get(0) instanceof String name
                                            && pair.get(1) instanceof List<?> valueList) {
                                        final String[] values =
                                                valueList.stream().filter(v -> v instanceof String).toArray(n -> new String[n]);
                                        result.add(new RequestParameter(name, values));
                                    }
                                }
                            }
                            LaResponseUtil.getOptionalResponse().ifPresent(res -> {
                                final Cookie invalidCookie = new Cookie(fessConfig.getCookieSearchParameterName(), StringUtil.EMPTY);
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
     * Decompresses GZIP-compressed data.
     *
     * @param compressed The GZIP-compressed data to decompress
     * @return Decompressed data
     * @throws IORuntimeException if decompression fails
     */
    protected byte[] gzipDecompress(final byte[] compressed) {
        try (final ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
                final GZIPInputStream gzipIn = new GZIPInputStream(bis);
                final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipIn.read(buffer)) > 0) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Interface for rewriting search request parameters.
     *
     * Implementations can modify search parameters before they are processed
     * by the search engine, allowing for custom parameter transformation logic.
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
