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
package org.codelibs.fess.helper;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.BytesRef;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.web.util.LaRequestUtil;

public class QueryHelper implements Serializable {

    protected static final String SCORE_SORT_VALUE = "score";

    protected static final long serialVersionUID = 1L;

    protected static final String SCORE_FIELD = "score";

    protected static final String INURL_FIELD = "inurl";

    @Resource
    protected DynamicProperties systemProperties;

    @Resource
    protected FessConfig fessConfig;

    @Resource
    protected RoleQueryHelper roleQueryHelper;

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected KeyMatchHelper keyMatchHelper;

    protected Set<String> apiResponseFieldSet;

    protected Set<String> highlightFieldSet = new HashSet<>();

    protected String[] responseFields;

    protected String[] cacheResponseFields;

    protected String[] responseDocValuesFields;

    protected String[] highlightedFields;

    protected String[] searchFields;

    protected String[] facetFields;

    protected String sortPrefix = "sort:";

    protected String[] supportedSortFields;

    protected int highlightFragmentSize = 100;

    protected String additionalQuery;

    protected long timeAllowed = -1;

    protected Map<String, String[]> requestParameterMap = new HashMap<>();

    protected int maxSearchResultOffset = 100000;

    protected SortBuilder[] defaultSortBuilders;

    protected String highlightPrefix = "hl_";

    protected FacetInfo defaultFacetInfo;

    protected GeoInfo defaultGeoInfo;

    protected Map<String, String[]> queryRequestHeaderMap = new HashMap<>();

    protected Map<String, String> fieldBoostMap = new HashMap<>();

    @PostConstruct
    public void init() {
        if (responseFields == null) {
            responseFields =
                    new String[] { SCORE_FIELD, fessConfig.getIndexFieldId(), fessConfig.getIndexFieldDocId(),
                            fessConfig.getIndexFieldBoost(), fessConfig.getIndexFieldContentLength(), fessConfig.getIndexFieldHost(),
                            fessConfig.getIndexFieldSite(), fessConfig.getIndexFieldLastModified(), fessConfig.getIndexFieldTimestamp(),
                            fessConfig.getIndexFieldMimetype(), fessConfig.getIndexFieldFiletype(), fessConfig.getIndexFieldCreated(),
                            fessConfig.getIndexFieldTitle(), fessConfig.getIndexFieldDigest(), fessConfig.getIndexFieldUrl(),
                            fessConfig.getIndexFieldClickCount(), fessConfig.getIndexFieldFavoriteCount(),
                            fessConfig.getIndexFieldConfigId(), fessConfig.getIndexFieldLang(), fessConfig.getIndexFieldHasCache() };
        }
        if (cacheResponseFields == null) {
            cacheResponseFields =
                    new String[] { SCORE_FIELD, fessConfig.getIndexFieldId(), fessConfig.getIndexFieldDocId(),
                            fessConfig.getIndexFieldBoost(), fessConfig.getIndexFieldContentLength(), fessConfig.getIndexFieldHost(),
                            fessConfig.getIndexFieldSite(), fessConfig.getIndexFieldLastModified(), fessConfig.getIndexFieldTimestamp(),
                            fessConfig.getIndexFieldMimetype(), fessConfig.getIndexFieldFiletype(), fessConfig.getIndexFieldCreated(),
                            fessConfig.getIndexFieldTitle(), fessConfig.getIndexFieldDigest(), fessConfig.getIndexFieldUrl(),
                            fessConfig.getIndexFieldClickCount(), fessConfig.getIndexFieldFavoriteCount(),
                            fessConfig.getIndexFieldConfigId(), fessConfig.getIndexFieldLang(), fessConfig.getIndexFieldCache() };
        }
        if (responseDocValuesFields == null) {
            responseDocValuesFields = new String[] { fessConfig.getIndexFieldClickCount(), fessConfig.getIndexFieldFavoriteCount() };
        }
        if (highlightedFields == null) {
            highlightedFields = new String[] { fessConfig.getIndexFieldContent() };
        }
        if (searchFields == null) {
            searchFields =
                    new String[] { INURL_FIELD, fessConfig.getIndexFieldUrl(), fessConfig.getIndexFieldDocId(),
                            fessConfig.getIndexFieldHost(), fessConfig.getIndexFieldTitle(), fessConfig.getIndexFieldContent(),
                            fessConfig.getIndexFieldContentLength(), fessConfig.getIndexFieldLastModified(),
                            fessConfig.getIndexFieldTimestamp(), fessConfig.getIndexFieldMimetype(), fessConfig.getIndexFieldFiletype(),
                            fessConfig.getIndexFieldLabel(), fessConfig.getIndexFieldSegment(), fessConfig.getIndexFieldClickCount(),
                            fessConfig.getIndexFieldFavoriteCount(), fessConfig.getIndexFieldLang() };
        }
        if (facetFields == null) {
            facetFields =
                    new String[] { fessConfig.getIndexFieldUrl(), fessConfig.getIndexFieldHost(), fessConfig.getIndexFieldTitle(),
                            fessConfig.getIndexFieldContent(), fessConfig.getIndexFieldContentLength(),
                            fessConfig.getIndexFieldLastModified(), fessConfig.getIndexFieldTimestamp(),
                            fessConfig.getIndexFieldMimetype(), fessConfig.getIndexFieldFiletype(), fessConfig.getIndexFieldLabel(),
                            fessConfig.getIndexFieldSegment() };
        }
        if (supportedSortFields == null) {
            supportedSortFields =
                    new String[] { SCORE_SORT_VALUE, fessConfig.getIndexFieldCreated(), fessConfig.getIndexFieldContentLength(),
                            fessConfig.getIndexFieldLastModified(), fessConfig.getIndexFieldTimestamp(),
                            fessConfig.getIndexFieldClickCount(), fessConfig.getIndexFieldFavoriteCount() };
        }
        if (apiResponseFieldSet == null) {
            setApiResponseFields(new String[] { fessConfig.getResponseFieldContentDescription(), fessConfig.getResponseFieldContentTitle(),
                    fessConfig.getResponseFieldSitePath(), fessConfig.getResponseFieldUrlLink(), fessConfig.getIndexFieldId(),
                    fessConfig.getIndexFieldDocId(), fessConfig.getIndexFieldBoost(), fessConfig.getIndexFieldContentLength(),
                    fessConfig.getIndexFieldHost(), fessConfig.getIndexFieldSite(), fessConfig.getIndexFieldLastModified(),
                    fessConfig.getIndexFieldTimestamp(), fessConfig.getIndexFieldMimetype(), fessConfig.getIndexFieldFiletype(),
                    fessConfig.getIndexFieldCreated(), fessConfig.getIndexFieldTitle(), fessConfig.getIndexFieldDigest(),
                    fessConfig.getIndexFieldUrl() });
        }
    }

    public QueryContext build(final String query, final Consumer<QueryContext> context) {
        String q;
        if (additionalQuery != null && StringUtil.isNotBlank(query)) {
            q = query + " " + additionalQuery;
        } else {
            q = query;
        }

        final QueryContext queryContext = new QueryContext(q, true);
        buildBaseQuery(queryContext, context);
        buildBoostQuery(queryContext);
        buildRoleQuery(queryContext);

        if (!queryContext.hasSorts() && defaultSortBuilders != null) {
            queryContext.addSorts(defaultSortBuilders);
        }
        return queryContext;
    }

    protected void buildRoleQuery(final QueryContext queryContext) {
        if (roleQueryHelper != null && queryContext.roleQueryEnabled()) {
            final Set<String> roleSet = roleQueryHelper.build();
            if (!roleSet.isEmpty()) {
                queryContext.addQuery(boolQuery -> {
                    final BoolQueryBuilder roleQuery = QueryBuilders.boolQuery();
                    roleSet.stream().forEach(name -> {
                        roleQuery.should(QueryBuilders.termQuery(fessConfig.getIndexFieldRole(), name));
                    });
                    boolQuery.filter(roleQuery);
                });
            }
        }
    }

    protected void buildBoostQuery(final QueryContext queryContext) {
        queryContext.addFunctionScore(functionScoreQuery -> {
            functionScoreQuery.add(ScoreFunctionBuilders.fieldValueFactorFunction(fessConfig.getIndexFieldBoost()));
            if (keyMatchHelper != null) {
                keyMatchHelper.buildQuery(queryContext.getDefaultKeyword(), functionScoreQuery);
            }
        });
    }

    public void buildBaseQuery(final QueryContext queryContext, final Consumer<QueryContext> context) {
        try {
            final Query query = getQueryParser().parse(queryContext.getQueryString());
            final QueryBuilder queryBuilder = convertQuery(queryContext, query, 1.0f);
            if (queryBuilder != null) {
                queryContext.setQueryBuilder(queryBuilder);
            } else {
                queryContext.setQueryBuilder(QueryBuilders.matchAllQuery());
            }
            // TODO options query
            context.accept(queryContext);
        } catch (final ParseException e) {
            throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryParseError(UserMessages.GLOBAL_PROPERTY_KEY),
                    "Invalid query: " + queryContext.getQueryString(), e);
        }
    }

    protected QueryParser getQueryParser() {
        return ComponentUtil.getQueryParser();
    }

    protected QueryBuilder convertQuery(final QueryContext context, final Query query, final float boost) {
        if (query instanceof TermQuery) {
            return convertTermQuery(context, (TermQuery) query, boost);
        } else if (query instanceof TermRangeQuery) {
            return convertTermRangeQuery(context, (TermRangeQuery) query, boost);
        } else if (query instanceof PhraseQuery) {
            return convertPhraseQuery(context, (PhraseQuery) query, boost);
        } else if (query instanceof FuzzyQuery) {
            return convertFuzzyQuery(context, (FuzzyQuery) query, boost);
        } else if (query instanceof PrefixQuery) {
            return convertPrefixQuery(context, (PrefixQuery) query, boost);
        } else if (query instanceof WildcardQuery) {
            return convertWildcardQuery(context, (WildcardQuery) query, boost);
        } else if (query instanceof BooleanQuery) {
            final BooleanQuery booleanQuery = (BooleanQuery) query;
            return convertBooleanQuery(context, booleanQuery, boost);
        } else if (query instanceof MatchAllDocsQuery) {
            return QueryBuilders.matchAllQuery();
        } else if (query instanceof BoostQuery) {
            final BoostQuery boostQuery = (BoostQuery) query;
            return convertQuery(context, boostQuery.getQuery(), boostQuery.getBoost());
        }
        throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY), "Unknown q: "
                + query.getClass() + " => " + query);
    }

    protected QueryBuilder convertBooleanQuery(final QueryContext context, final BooleanQuery booleanQuery, final float boost) {
        final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        for (final BooleanClause clause : booleanQuery.clauses()) {
            final QueryBuilder queryBuilder = convertQuery(context, clause.getQuery(), boost);
            if (queryBuilder != null) {
                switch (clause.getOccur()) {
                case MUST:
                    boolQuery.must(queryBuilder);
                    break;
                case SHOULD:
                    boolQuery.should(queryBuilder);
                    break;
                case MUST_NOT:
                    boolQuery.mustNot(queryBuilder);
                    break;
                default:
                    break;
                }
            }
        }
        return boolQuery;
    }

    protected QueryBuilder convertWildcardQuery(final QueryContext context, final WildcardQuery wildcardQuery, final float boost) {
        final String field = wildcardQuery.getField();
        if (Constants.DEFAULT_FIELD.equals(field)) {
            context.addFieldLog(field, wildcardQuery.getTerm().text());
            return buildDefaultQueryBuilder((f, b) -> QueryBuilders.wildcardQuery(f, wildcardQuery.getTerm().text()).boost(b * boost));
        } else if (isSearchField(field)) {
            context.addFieldLog(field, wildcardQuery.getTerm().text());
            return QueryBuilders.wildcardQuery(field, wildcardQuery.getTerm().text()).boost(boost);
        } else {
            final String origQuery = wildcardQuery.getTerm().toString();
            context.addFieldLog(Constants.DEFAULT_FIELD, origQuery);
            context.addHighlightedQuery(origQuery);
            return buildDefaultQueryBuilder((f, b) -> QueryBuilders.wildcardQuery(f, origQuery).boost(b * boost));
        }
    }

    protected QueryBuilder convertPrefixQuery(final QueryContext context, final PrefixQuery prefixQuery, final float boost) {
        final String field = prefixQuery.getField();
        if (Constants.DEFAULT_FIELD.equals(field)) {
            context.addFieldLog(field, prefixQuery.getPrefix().text());
            return buildDefaultQueryBuilder((f, b) -> QueryBuilders.prefixQuery(f, prefixQuery.getPrefix().text()).boost(b * boost));
        } else if (isSearchField(field)) {
            context.addFieldLog(field, prefixQuery.getPrefix().text());
            return QueryBuilders.prefixQuery(field, prefixQuery.getPrefix().text()).boost(boost);
        } else {
            final String origQuery = prefixQuery.getPrefix().toString();
            context.addFieldLog(Constants.DEFAULT_FIELD, origQuery);
            context.addHighlightedQuery(origQuery);
            return buildDefaultQueryBuilder((f, b) -> QueryBuilders.prefixQuery(f, origQuery).boost(b * boost));
        }
    }

    protected QueryBuilder convertFuzzyQuery(final QueryContext context, final FuzzyQuery fuzzyQuery, final float boost) {
        final Term term = fuzzyQuery.getTerm();
        final String field = term.field();
        // TODO fuzzy value
        if (Constants.DEFAULT_FIELD.equals(field)) {
            context.addFieldLog(field, term.text());
            return buildDefaultQueryBuilder((f, b) -> QueryBuilders.fuzzyQuery(f, term.text())
                    .fuzziness(Fuzziness.fromEdits(fuzzyQuery.getMaxEdits())).boost(b * boost));
        } else if (isSearchField(field)) {
            context.addFieldLog(field, term.text());
            return QueryBuilders.fuzzyQuery(field, term.text()).boost(boost).fuzziness(Fuzziness.fromEdits(fuzzyQuery.getMaxEdits()));
        } else {
            final String origQuery = fuzzyQuery.toString();
            context.addFieldLog(Constants.DEFAULT_FIELD, origQuery);
            context.addHighlightedQuery(origQuery);
            return buildDefaultQueryBuilder((f, b) -> QueryBuilders.fuzzyQuery(f, origQuery)
                    .fuzziness(Fuzziness.fromEdits(fuzzyQuery.getMaxEdits())).boost(b * boost));
        }
    }

    protected QueryBuilder convertTermRangeQuery(final QueryContext context, final TermRangeQuery termRangeQuery, final float boost) {
        final String field = termRangeQuery.getField();
        if (isSearchField(field)) {
            context.addFieldLog(field, termRangeQuery.toString(field));
            final RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(field);
            final BytesRef min = termRangeQuery.getLowerTerm();
            if (min != null) {
                if (termRangeQuery.includesLower()) {
                    rangeQuery.gte(min.utf8ToString());
                } else {
                    rangeQuery.gt(min.utf8ToString());
                }
            }
            final BytesRef max = termRangeQuery.getUpperTerm();
            if (max != null) {
                if (termRangeQuery.includesUpper()) {
                    rangeQuery.lte(max.utf8ToString());
                } else {
                    rangeQuery.lt(max.utf8ToString());
                }
            }
            rangeQuery.boost(boost);
            return rangeQuery;
        } else {
            final String origQuery = termRangeQuery.toString();
            context.addFieldLog(Constants.DEFAULT_FIELD, origQuery);
            context.addHighlightedQuery(origQuery);
            return buildDefaultQueryBuilder((f, b) -> QueryBuilders.matchPhraseQuery(f, origQuery).boost(b));
        }
    }

    protected QueryBuilder convertTermQuery(final QueryContext context, final TermQuery termQuery, final float boost) {
        final String field = termQuery.getTerm().field();
        final String text = termQuery.getTerm().text();
        if (fessConfig.getQueryReplaceTermWithPrefixQueryAsBoolean() && text.length() > 1 && text.endsWith("*")) {
            return convertPrefixQuery(context, new PrefixQuery(new Term(field, text.substring(0, text.length() - 1))), boost);
        } else if (Constants.DEFAULT_FIELD.equals(field)) {
            context.addFieldLog(field, text);
            context.addHighlightedQuery(text);
            return buildDefaultQueryBuilder((f, b) -> QueryBuilders.matchPhraseQuery(f, text).boost(b * boost));
        } else if ("sort".equals(field)) {
            final String[] values = text.split("\\.");
            if (values.length > 2) {
                throw new InvalidQueryException(
                        messages -> messages.addErrorsInvalidQuerySortValue(UserMessages.GLOBAL_PROPERTY_KEY, text), "Invalid sort field: "
                                + termQuery);
            }
            final String sortField = values[0];
            if (!isSortField(sortField)) {
                throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryUnsupportedSortField(
                        UserMessages.GLOBAL_PROPERTY_KEY, sortField), "Unsupported sort field: " + termQuery);
            }
            SortOrder sortOrder;
            if (values.length == 2) {
                sortOrder = SortOrder.DESC.toString().equalsIgnoreCase(values[1]) ? SortOrder.DESC : SortOrder.ASC;
                if (sortOrder == null) {
                    throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryUnsupportedSortOrder(
                            UserMessages.GLOBAL_PROPERTY_KEY, values[1]), "Invalid sort order: " + termQuery);
                }
            } else {
                sortOrder = SortOrder.ASC;
            }
            context.addSorts(SortBuilders.fieldSort(SCORE_SORT_VALUE.equals(sortField) ? "_score" : sortField).order(sortOrder));
            return null;
        } else if (INURL_FIELD.equals(field)) {
            return QueryBuilders.wildcardQuery(fessConfig.getIndexFieldUrl(), "*" + text + "*").boost(boost);
        } else if (isSearchField(field)) {
            context.addFieldLog(field, text);
            context.addHighlightedQuery(text);
            return QueryBuilders.matchPhraseQuery(field, text).boost(boost);
        } else {
            final String origQuery = termQuery.toString();
            context.addFieldLog(Constants.DEFAULT_FIELD, origQuery);
            context.addHighlightedQuery(origQuery);
            return buildDefaultQueryBuilder((f, b) -> QueryBuilders.matchPhraseQuery(f, origQuery).boost(b * boost));
        }
    }

    private QueryBuilder convertPhraseQuery(final QueryContext context, final PhraseQuery query, final float boost) {
        final Term[] terms = query.getTerms();
        if (terms.length == 0) {
            throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryUnknown(UserMessages.GLOBAL_PROPERTY_KEY),
                    "Unknown phrase query: " + query);
        }
        final String field = terms[0].field();
        final String[] texts = stream(terms).get(stream -> stream.map(term -> term.text()).toArray(n -> new String[n]));
        final String text = String.join(" ", texts);
        context.addFieldLog(field, text);
        stream(texts).of(stream -> stream.forEach(t -> context.addHighlightedQuery(t)));
        return buildDefaultQueryBuilder((f, b) -> QueryBuilders.matchPhraseQuery(f, text).boost(b * boost));
    }

    private boolean isSearchField(final String field) {
        for (final String searchField : searchFields) {
            if (searchField.equals(field)) {
                return true;
            }
        }
        return false;
    }

    private QueryBuilder buildDefaultQueryBuilder(final DefaultQueryBuilderFunction builder) {
        final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        final QueryBuilder titleQuery =
                builder.apply(fessConfig.getIndexFieldTitle(), fessConfig.getQueryBoostTitleAsDecimal().floatValue());
        boolQuery.should(titleQuery);
        final QueryBuilder contentQuery =
                builder.apply(fessConfig.getIndexFieldContent(), fessConfig.getQueryBoostContentAsDecimal().floatValue());
        boolQuery.should(contentQuery);
        getQueryLanguages().ifPresent(
                langs -> {
                    stream(langs).of(
                            stream -> stream.forEach(lang -> {
                                final QueryBuilder titleLangQuery =
                                        builder.apply(fessConfig.getIndexFieldTitle() + "_" + lang, fessConfig
                                                .getQueryBoostTitleLangAsDecimal().floatValue());
                                boolQuery.should(titleLangQuery);
                                final QueryBuilder contentLangQuery =
                                        builder.apply(fessConfig.getIndexFieldContent() + "_" + lang, fessConfig
                                                .getQueryBoostContentLangAsDecimal().floatValue());
                                boolQuery.should(contentLangQuery);
                            }));
                });
        return boolQuery;
    }

    interface DefaultQueryBuilderFunction {
        QueryBuilder apply(String field, float boost);
    }

    protected OptionalThing<String[]> getQueryLanguages() {
        return LaRequestUtil.getOptionalRequest()
                .map(request -> fessConfig.getQueryLanguages(request.getLocales(),
                        (String[]) request.getAttribute(Constants.REQUEST_LANGUAGES)));
    }

    private boolean isSortField(final String field) {
        for (final String f : supportedSortFields) {
            if (f.equals(field)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFacetField(final String field) {
        if (StringUtil.isBlank(field)) {
            return false;
        }
        boolean flag = false;
        for (final String f : facetFields) {
            if (field.equals(f)) {
                flag = true;
            }
        }
        return flag;
    }

    public boolean isFacetSortValue(final String sort) {
        return "count".equals(sort) || "index".equals(sort);
    }

    public void setApiResponseFields(final String[] fields) {
        apiResponseFieldSet = new HashSet<>();
        for (final String field : fields) {
            apiResponseFieldSet.add(field);
        }
    }

    public boolean isApiResponseField(final String field) {
        return apiResponseFieldSet.contains(field);
    }

    /**
     * @return the responseFields
     */
    public String[] getResponseFields() {
        return responseFields;
    }

    /**
     * @param responseFields the responseFields to set
     */
    public void setResponseFields(final String[] responseFields) {
        this.responseFields = responseFields;
    }

    public String[] getCacheResponseFields() {
        return cacheResponseFields;
    }

    public void setCacheResponseFields(final String[] cacheResponseFields) {
        this.cacheResponseFields = cacheResponseFields;
    }

    public String[] getResponseDocValuesFields() {
        return responseDocValuesFields;
    }

    public void setResponseDocValuesFields(final String[] responseDocValuesFields) {
        this.responseDocValuesFields = responseDocValuesFields;
    }

    /**
     * @return the highlightedFields
     */
    public String[] getHighlightedFields() {
        return highlightedFields;
    }

    /**
     * @param highlightedFields the highlightedFields to set
     */
    public void setHighlightedFields(final String[] highlightedFields) {
        this.highlightedFields = highlightedFields;
    }

    public void highlightedFields(final Consumer<Stream<String>> stream) {
        stream(highlightedFields).of(stream);
        ;
    }

    /**
     * @return the supportedFields
     */
    public String[] getSearchFields() {
        return searchFields;
    }

    /**
     * @param supportedFields the supportedFields to set
     */
    public void setSearchFields(final String[] supportedFields) {
        searchFields = supportedFields;
    }

    /**
     * @return the facetFields
     */
    public String[] getFacetFields() {
        return facetFields;
    }

    /**
     * @param facetFields the facetFields to set
     */
    public void setFacetFields(final String[] facetFields) {
        this.facetFields = facetFields;
    }

    /**
     * @return the sortPrefix
     */
    public String getSortPrefix() {
        return sortPrefix;
    }

    /**
     * @param sortPrefix the sortPrefix to set
     */
    public void setSortPrefix(final String sortPrefix) {
        this.sortPrefix = sortPrefix;
    }

    /**
     * @return the supportedSortFields
     */
    public String[] getSupportedSortFields() {
        return supportedSortFields;
    }

    /**
     * @param supportedSortFields the supportedSortFields to set
     */
    public void setSupportedSortFields(final String[] supportedSortFields) {
        this.supportedSortFields = supportedSortFields;
    }

    public void addHighlightField(final String field) {
        highlightFieldSet.add(field);
    }

    /**
     * @return the highlightFragmentSize
     */
    public int getHighlightFragmentSize() {
        return highlightFragmentSize;
    }

    /**
     * @param highlightFragmentSize the highlightFragmentSize to set
     */
    public void setHighlightFragmentSize(final int highlightFragmentSize) {
        this.highlightFragmentSize = highlightFragmentSize;
    }

    /**
     * @return the additionalQuery
     */
    public String getAdditionalQuery() {
        return additionalQuery;
    }

    /**
     * @param additionalQuery the additionalQuery to set
     */
    public void setAdditionalQuery(final String additionalQuery) {
        this.additionalQuery = additionalQuery;
    }

    /**
     * @return the timeAllowed
     */
    public long getTimeAllowed() {
        return timeAllowed;
    }

    /**
     * @param timeAllowed the timeAllowed to set
     */
    public void setTimeAllowed(final long timeAllowed) {
        this.timeAllowed = timeAllowed;
    }

    public void addRequestParameter(final String name, final String... values) {
        requestParameterMap.put(name, values);
    }

    public void addRequestParameter(final String name, final String value) {
        if (value != null) {
            requestParameterMap.put(name, new String[] { value });
        }
    }

    public Set<Entry<String, String[]>> getRequestParameterSet() {
        return requestParameterMap.entrySet();
    }

    public int getMaxSearchResultOffset() {
        return maxSearchResultOffset;
    }

    public void setMaxSearchResultOffset(final int maxSearchResultOffset) {
        this.maxSearchResultOffset = maxSearchResultOffset;
    }

    public void addDefaultSort(final String fieldName, final String order) {
        final List<SortBuilder> list = new ArrayList<>();
        if (defaultSortBuilders != null) {
            stream(defaultSortBuilders).of(stream -> stream.forEach(builder -> list.add(builder)));
        }
        list.add(SortBuilders.fieldSort(fieldName)
                .order(SortOrder.DESC.toString().equalsIgnoreCase(order) ? SortOrder.DESC : SortOrder.ASC));
        defaultSortBuilders = list.toArray(new SortBuilder[list.size()]);
    }

    public void setHighlightPrefix(final String highlightPrefix) {
        this.highlightPrefix = highlightPrefix;
    }

    public String getHighlightPrefix() {
        return highlightPrefix;
    }

    public FacetInfo getDefaultFacetInfo() {
        return defaultFacetInfo;
    }

    public void setDefaultFacetInfo(final FacetInfo defaultFacetInfo) {
        this.defaultFacetInfo = defaultFacetInfo;
    }

    public GeoInfo getDefaultGeoInfo() {
        return defaultGeoInfo;
    }

    public void setDefaultGeoInfo(final GeoInfo defaultGeoInfo) {
        this.defaultGeoInfo = defaultGeoInfo;
    }

    public Map<String, String[]> getQueryRequestHeaderMap() {
        if (queryRequestHeaderMap.isEmpty()) {
            return queryRequestHeaderMap;
        }

        final HttpServletRequest request = LaRequestUtil.getOptionalRequest().orElse(null);
        final Map<String, String[]> queryParamMap = new HashMap<>();
        for (final Map.Entry<String, String[]> entry : queryRequestHeaderMap.entrySet()) {
            final String[] values = entry.getValue();
            final String[] newValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                final String value = values[i];
                if (value.length() > 1 && value.charAt(0) == '$' && request != null) {
                    final String param = request.getParameter(value.substring(1));
                    if (StringUtil.isNotBlank(param)) {
                        newValues[i] = param;
                    } else {
                        newValues[i] = StringUtil.EMPTY;
                    }
                } else {
                    newValues[i] = value;
                }
            }
            queryParamMap.put(entry.getKey(), newValues);
        }

        return queryParamMap;
    }

    public void addQueryRequestHeader(final String key, final String[] values) {
        queryRequestHeaderMap.put(key, values);
    }

    public String generateId() {
        return UUID.randomUUID().toString().replace("-", StringUtil.EMPTY);
    }

}
