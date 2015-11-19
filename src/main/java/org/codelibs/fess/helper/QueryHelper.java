/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.ext.ExtendableQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
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
import org.codelibs.fess.util.StreamUtil;
import org.dbflute.optional.OptionalEntity;
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
import org.lastaflute.web.ruts.message.ActionMessages;
import org.lastaflute.web.util.LaRequestUtil;

public class QueryHelper implements Serializable {

    protected static final long serialVersionUID = 1L;

    private static final String DEFAULT_FIELD = "_default";

    protected static final String SCORE_FIELD = "score";

    protected static final String INURL_FIELD = "inurl";

    protected static final String AND = "AND";

    protected static final String OR = "OR";

    protected static final String NOT = "NOT";

    protected static final String _OR_ = " OR ";

    protected static final String _AND_ = " AND ";

    protected static final String DEFAULT_OPERATOR = _AND_;

    protected static final int DEFAULT_START_POSITION = 0;

    protected static final int DEFAULT_PAGE_SIZE = 20;

    protected static final int MAX_PAGE_SIZE = 100;

    @Resource
    protected DynamicProperties crawlerProperties;

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

    protected Map<String, String> fieldLanguageMap = new HashMap<>();

    protected int maxSearchResultOffset = 100000;

    protected SortBuilder[] defaultSortBuilders;

    protected String highlightPrefix = "hl_";

    protected FacetInfo defaultFacetInfo;

    protected GeoInfo defaultGeoInfo;

    protected String defaultQueryLanguage;

    protected Map<String, String[]> additionalQueryParamMap = new HashMap<>();

    protected Map<String, String> fieldBoostMap = new HashMap<>();

    protected int defaultPageSize = DEFAULT_PAGE_SIZE;

    protected int defaultStartPosition = DEFAULT_START_POSITION;

    @PostConstruct
    public void init() {
        if (responseFields == null) {
            responseFields =
                    new String[] { SCORE_FIELD, fessConfig.getIndexFieldId(), fessConfig.getIndexFieldDocId(),
                            fessConfig.getIndexFieldBoost(), fessConfig.getIndexFieldContentLength(), fessConfig.getIndexFieldHost(),
                            fessConfig.getIndexFieldSite(), fessConfig.getIndexFieldLastModified(), fessConfig.getIndexFieldMimetype(),
                            fessConfig.getIndexFieldFiletype(), fessConfig.getIndexFieldCreated(), fessConfig.getIndexFieldTitle(),
                            fessConfig.getIndexFieldDigest(), fessConfig.getIndexFieldUrl(), fessConfig.getIndexFieldClickCount(),
                            fessConfig.getIndexFieldFavoriteCount(), fessConfig.getIndexFieldConfigId(), fessConfig.getIndexFieldLang(),
                            fessConfig.getIndexFieldHasCache() };
        }
        if (cacheResponseFields == null) {
            cacheResponseFields =
                    new String[] { SCORE_FIELD, fessConfig.getIndexFieldId(), fessConfig.getIndexFieldDocId(),
                            fessConfig.getIndexFieldBoost(), fessConfig.getIndexFieldContentLength(), fessConfig.getIndexFieldHost(),
                            fessConfig.getIndexFieldSite(), fessConfig.getIndexFieldLastModified(), fessConfig.getIndexFieldMimetype(),
                            fessConfig.getIndexFieldFiletype(), fessConfig.getIndexFieldCreated(), fessConfig.getIndexFieldTitle(),
                            fessConfig.getIndexFieldDigest(), fessConfig.getIndexFieldUrl(), fessConfig.getIndexFieldClickCount(),
                            fessConfig.getIndexFieldFavoriteCount(), fessConfig.getIndexFieldConfigId(), fessConfig.getIndexFieldLang(),
                            fessConfig.getIndexFieldCache() };
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
                            fessConfig.getIndexFieldMimetype(), fessConfig.getIndexFieldFiletype(), fessConfig.getIndexFieldLabel(),
                            fessConfig.getIndexFieldSegment(), fessConfig.getIndexFieldClickCount(),
                            fessConfig.getIndexFieldFavoriteCount(), fessConfig.getIndexFieldLang() };
        }
        if (facetFields == null) {
            facetFields =
                    new String[] { fessConfig.getIndexFieldUrl(), fessConfig.getIndexFieldHost(), fessConfig.getIndexFieldTitle(),
                            fessConfig.getIndexFieldContent(), fessConfig.getIndexFieldContentLength(),
                            fessConfig.getIndexFieldLastModified(), fessConfig.getIndexFieldMimetype(), fessConfig.getIndexFieldFiletype(),
                            fessConfig.getIndexFieldLabel(), fessConfig.getIndexFieldSegment() };
        }
        if (supportedSortFields == null) {
            supportedSortFields =
                    new String[] { fessConfig.getIndexFieldCreated(), fessConfig.getIndexFieldContentLength(),
                            fessConfig.getIndexFieldLastModified(), fessConfig.getIndexFieldClickCount(),
                            fessConfig.getIndexFieldFavoriteCount() };
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

        if (keyMatchHelper != null) {
            final List<String> docIdQueryList = keyMatchHelper.getDocIdQueryList();
            if (docIdQueryList != null && !docIdQueryList.isEmpty()) {
                queryContext.addQuery(boolQuery -> {
                    for (final String docIdQuery : docIdQueryList) {
                        // TODO id query?
                        boolQuery.should(QueryBuilders.queryStringQuery(docIdQuery));
                    }
                });
            }
        }

        if (roleQueryHelper != null) {
            final Set<String> roleSet = roleQueryHelper.build();
            if (!roleSet.isEmpty()) {
                queryContext.addQuery(boolQuery -> {
                    BoolQueryBuilder roleQuery = QueryBuilders.boolQuery();
                    roleSet.stream().forEach(name -> {
                        roleQuery.filter(QueryBuilders.termQuery(fessConfig.getIndexFieldRole(), name));
                    });
                    boolQuery.filter(roleQuery);
                });
            }
        }

        if (!queryContext.hasSorts() && defaultSortBuilders != null) {
            queryContext.addSorts(defaultSortBuilders);
        }
        return queryContext;
    }

    private void buildBoostQuery(final QueryContext queryContext) {
        queryContext.addFunctionScore(functionScoreQuery -> {
            functionScoreQuery.add(ScoreFunctionBuilders.fieldValueFactorFunction(fessConfig.getIndexFieldBoost()));
        });
    }

    public void buildBaseQuery(final QueryContext queryContext, final Consumer<QueryContext> context) {
        final QueryParser queryParser = getQueryParser();
        try {
            final Query query = queryParser.parse(queryContext.getQueryString());
            final QueryBuilder queryBuilder = convertQuery(queryContext, query);
            if (queryBuilder != null) {
                queryContext.setQueryBuilder(queryBuilder);
            } else {
                queryContext.setQueryBuilder(QueryBuilders.matchAllQuery());
            }
            context.accept(queryContext);
        } catch (final ParseException e) {
            throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryParseError(ActionMessages.GLOBAL_PROPERTY_KEY),
                    "Invalid query: " + queryContext.getQueryString());
        }
    }

    protected QueryParser getQueryParser() {
        return new ExtendableQueryParser(DEFAULT_FIELD, new WhitespaceAnalyzer());
    }

    protected QueryBuilder convertQuery(final QueryContext context, final Query query) {
        if (query instanceof TermQuery) {
            return convertTermQuery(context, (TermQuery) query);
        } else if (query instanceof TermRangeQuery) {
            return convertTermRangeQuery(context, (TermRangeQuery) query);
        } else if (query instanceof FuzzyQuery) {
            return convertFuzzyQuery(context, (FuzzyQuery) query);
        } else if (query instanceof PrefixQuery) {
            return convertPrefixQuery(context, (PrefixQuery) query);
        } else if (query instanceof WildcardQuery) {
            return convertWildcardQuery(context, (WildcardQuery) query);
        } else if (query instanceof BooleanQuery) {
            final BooleanQuery booleanQuery = (BooleanQuery) query;
            return convertBooleanQuery(context, booleanQuery);
        }
        throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryUnknown(ActionMessages.GLOBAL_PROPERTY_KEY),
                "Unknown query: " + query.getClass() + " => " + query);
    }

    protected QueryBuilder convertBooleanQuery(final QueryContext context, final BooleanQuery booleanQuery) {
        final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        for (final BooleanClause clause : booleanQuery.getClauses()) {
            final QueryBuilder queryBuilder = convertQuery(context, clause.getQuery());
            if (queryBuilder != null) {
                switch (clause.getOccur()) {
                case MUST:
                    boolQuery.must(queryBuilder);
                    break;
                case SHOULD:
                    boolQuery.must(queryBuilder);
                    break;
                case MUST_NOT:
                    boolQuery.must(queryBuilder);
                    break;
                default:
                    break;
                }
            }
        }
        return boolQuery;
    }

    protected QueryBuilder convertWildcardQuery(final QueryContext context, final WildcardQuery wildcardQuery) {
        final String field = wildcardQuery.getField();
        if (DEFAULT_FIELD.equals(field)) {
            context.addFieldLog(field, wildcardQuery.getTerm().text());
            return buildDefaultQueryBuilder(f -> QueryBuilders.wildcardQuery(f, wildcardQuery.getTerm().text()));
        } else if (isSearchField(field)) {
            context.addFieldLog(field, wildcardQuery.getTerm().text());
            return QueryBuilders.wildcardQuery(field, wildcardQuery.getTerm().text()).boost(wildcardQuery.getBoost());
        } else {
            final String origQuery = wildcardQuery.getTerm().toString();
            context.addFieldLog(DEFAULT_FIELD, origQuery);
            context.addHighlightedQuery(origQuery);
            return buildDefaultQueryBuilder(f -> QueryBuilders.wildcardQuery(f, origQuery));
        }
    }

    protected QueryBuilder convertPrefixQuery(final QueryContext context, final PrefixQuery prefixQuery) {
        final String field = prefixQuery.getField();
        if (DEFAULT_FIELD.equals(field)) {
            context.addFieldLog(field, prefixQuery.getPrefix().text());
            return buildDefaultQueryBuilder(f -> QueryBuilders.prefixQuery(f, prefixQuery.getPrefix().text()));
        } else if (isSearchField(field)) {
            context.addFieldLog(field, prefixQuery.getPrefix().text());
            return QueryBuilders.prefixQuery(field, prefixQuery.getPrefix().text()).boost(prefixQuery.getBoost());
        } else {
            final String origQuery = prefixQuery.getPrefix().toString();
            context.addFieldLog(DEFAULT_FIELD, origQuery);
            context.addHighlightedQuery(origQuery);
            return buildDefaultQueryBuilder(f -> QueryBuilders.prefixQuery(f, origQuery));
        }
    }

    protected QueryBuilder convertFuzzyQuery(final QueryContext context, final FuzzyQuery fuzzyQuery) {
        final Term term = fuzzyQuery.getTerm();
        final String field = term.field();
        // TODO fuzzy value
        if (DEFAULT_FIELD.equals(field)) {
            context.addFieldLog(field, term.text());
            return buildDefaultQueryBuilder(f -> QueryBuilders.fuzzyQuery(f, term.text()).fuzziness(
                    Fuzziness.fromEdits(fuzzyQuery.getMaxEdits())));
        } else if (isSearchField(field)) {
            context.addFieldLog(field, term.text());
            return QueryBuilders.fuzzyQuery(field, term.text()).boost(fuzzyQuery.getBoost())
                    .fuzziness(Fuzziness.fromEdits(fuzzyQuery.getMaxEdits()));
        } else {
            final String origQuery = fuzzyQuery.toString();
            context.addFieldLog(DEFAULT_FIELD, origQuery);
            context.addHighlightedQuery(origQuery);
            return buildDefaultQueryBuilder(f -> QueryBuilders.fuzzyQuery(f, origQuery).fuzziness(
                    Fuzziness.fromEdits(fuzzyQuery.getMaxEdits())));
        }
    }

    protected QueryBuilder convertTermRangeQuery(final QueryContext context, final TermRangeQuery termRangeQuery) {
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
            rangeQuery.boost(termRangeQuery.getBoost());
            return rangeQuery;
        } else {
            final String origQuery = termRangeQuery.toString();
            context.addFieldLog(DEFAULT_FIELD, origQuery);
            context.addHighlightedQuery(origQuery);
            return buildDefaultQueryBuilder(f -> QueryBuilders.matchPhraseQuery(f, origQuery));
        }
    }

    protected QueryBuilder convertTermQuery(final QueryContext context, final TermQuery termQuery) {
        final String field = termQuery.getTerm().field();
        final String text = termQuery.getTerm().text();
        if (DEFAULT_FIELD.equals(field)) {
            context.addFieldLog(field, text);
            context.addHighlightedQuery(text);
            return buildDefaultQueryBuilder(f -> QueryBuilders.matchPhraseQuery(f, text));
        } else if ("sort".equals(field)) {
            final String[] values = text.split("\\.");
            if (values.length > 2) {
                throw new InvalidQueryException(messages -> messages.addErrorsInvalidQuerySortValue(ActionMessages.GLOBAL_PROPERTY_KEY,
                        text), "Invalid sort field: " + termQuery);
            }
            final String sortField = values[0];
            if (!isSortField(sortField)) {
                throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryUnsupportedSortField(
                        ActionMessages.GLOBAL_PROPERTY_KEY, sortField), "Unsupported sort field: " + termQuery);
            }
            SortOrder sortOrder;
            if (values.length == 2) {
                sortOrder = SortOrder.valueOf(values[1]);
                if (sortOrder == null) {
                    throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryUnsupportedSortOrder(
                            ActionMessages.GLOBAL_PROPERTY_KEY, values[1]), "Invalid sort order: " + termQuery);
                }
            } else {
                sortOrder = SortOrder.ASC;
            }
            context.addSorts(SortBuilders.fieldSort(sortField).order(sortOrder));
            return null;
        } else if (INURL_FIELD.equals(field)) {
            return QueryBuilders.wildcardQuery(field, text).boost(termQuery.getBoost());
        } else if (isSearchField(field)) {
            context.addFieldLog(field, text);
            context.addHighlightedQuery(text);
            return QueryBuilders.matchPhraseQuery(field, text).boost(termQuery.getBoost());
        } else {
            final String origQuery = termQuery.toString();
            context.addFieldLog(DEFAULT_FIELD, origQuery);
            context.addHighlightedQuery(origQuery);
            return buildDefaultQueryBuilder(f -> QueryBuilders.matchPhraseQuery(f, origQuery));
        }
    }

    private boolean isSearchField(final String field) {
        for (final String searchField : searchFields) {
            if (searchField.equals(field)) {
                return true;
            }
        }
        return false;
    }

    private QueryBuilder buildDefaultQueryBuilder(final Function<String, QueryBuilder> builder) {
        // TODO boost
        final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        final QueryBuilder titleQuery = builder.apply(fessConfig.getIndexFieldTitle());
        boolQuery.should(titleQuery);
        final QueryBuilder contentQuery = builder.apply(fessConfig.getIndexFieldContent());
        boolQuery.should(contentQuery);
        getQueryLanguage().ifPresent(lang -> {
            final QueryBuilder titleLangQuery = builder.apply(fessConfig.getIndexFieldTitle() + "_" + lang);
            boolQuery.should(titleLangQuery);
            final QueryBuilder contentLangQuery = builder.apply(fessConfig.getIndexFieldContent() + "_" + lang);
            boolQuery.should(contentLangQuery);
        });
        return boolQuery;
    }

    protected OptionalThing<String> getQueryLanguage() {
        if (defaultQueryLanguage != null) {
            return OptionalEntity.of(defaultQueryLanguage);
        }
        return LaRequestUtil.getOptionalRequest().map(request -> {
            final Locale locale = request.getLocale();
            if (locale == null) {
                return null;
            }
            final String language = locale.getLanguage();
            final String country = locale.getCountry();
            if (StringUtil.isNotBlank(language)) {
                if (StringUtil.isNotBlank(country)) {
                    final String lang = language + "-" + country;
                    if (fieldLanguageMap.containsKey(lang)) {
                        return fieldLanguageMap.get(lang);
                    }
                }
                if (fieldLanguageMap.containsKey(language)) {
                    return fieldLanguageMap.get(language);
                }
            }
            return null;
        });

    }

    public String buildOptionQuery(final Map<String, String[]> optionMap) {
        if (optionMap == null) {
            return StringUtil.EMPTY;
        }

        // TODO
        final StringBuilder buf = new StringBuilder();

        //        final String[] qs = optionMap.get(Constants.OPTION_QUERY_Q);
        //        if (qs != null) {
        //            for (final String q : qs) {
        //                if (StringUtil.isNotBlank(q)) {
        //                    buf.append(' ');
        //                    buf.append(q);
        //                }
        //            }
        //        }
        //
        //        final String[] cqs = optionMap.get(Constants.OPTION_QUERY_CQ);
        //        if (cqs != null) {
        //            for (final String cq : cqs) {
        //                if (StringUtil.isNotBlank(cq)) {
        //                    buf.append(' ');
        //                    char split = 0;
        //                    final List<QueryPart> partList = splitQuery(cq.indexOf('"') >= 0 ? cq : "\"" + cq + "\"", null, null, null);
        //                    for (final QueryPart part : partList) {
        //                        if (split == 0) {
        //                            split = ' ';
        //                        } else {
        //                            buf.append(split);
        //                        }
        //                        final String value = part.getValue();
        //                        buf.append('"');
        //                        buf.append(value);
        //                        buf.append('"');
        //                    }
        //                }
        //            }
        //        }
        //
        //        final String[] oqs = optionMap.get(Constants.OPTION_QUERY_OQ);
        //        if (oqs != null) {
        //            for (final String oq : oqs) {
        //                if (StringUtil.isNotBlank(oq)) {
        //                    buf.append(' ');
        //                    final List<QueryPart> partList = splitQuery(oq, null, null, null);
        //                    final boolean append = partList.size() > 1 && optionMap.size() > 1;
        //                    if (append) {
        //                        buf.append('(');
        //                    }
        //                    String split = null;
        //                    for (final QueryPart part : partList) {
        //                        if (split == null) {
        //                            split = _OR_;
        //                        } else {
        //                            buf.append(split);
        //                        }
        //                        final String value = part.getValue();
        //                        final boolean hasSpace = value.matches(".*\\s.*");
        //                        if (hasSpace) {
        //                            buf.append('"');
        //                        }
        //                        buf.append(value);
        //                        if (hasSpace) {
        //                            buf.append('"');
        //                        }
        //                    }
        //                    if (append) {
        //                        buf.append(')');
        //                    }
        //                }
        //            }
        //        }
        //
        //        final String[] nqs = optionMap.get(Constants.OPTION_QUERY_NQ);
        //        if (nqs != null) {
        //            for (final String nq : nqs) {
        //                if (StringUtil.isNotBlank(nq)) {
        //                    buf.append(' ');
        //                    String split = StringUtil.EMPTY;
        //                    final List<QueryPart> partList = splitQuery(nq, null, null, null);
        //                    for (final QueryPart part : partList) {
        //                        buf.append(split);
        //                        if (split.length() == 0) {
        //                            split = " ";
        //                        }
        //                        buf.append(NOT_);
        //                        final String value = part.getValue();
        //                        final boolean hasSpace = value.matches(".*\\s.*");
        //                        if (hasSpace) {
        //                            buf.append('"');
        //                        }
        //                        buf.append(value);
        //                        if (hasSpace) {
        //                            buf.append('"');
        //                        }
        //                    }
        //                }
        //            }
        //        }

        return buf.toString().trim();
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
        if (apiResponseFieldSet == null) {
            return true;
        }
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

    public Stream<String> highlightedFields() {
        return StreamUtil.of(highlightedFields);
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

    public void addFieldLanguage(final String lang, final String fieldLang) {
        fieldLanguageMap.put(lang, fieldLang);
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
            StreamUtil.of(defaultSortBuilders).forEach(builder -> list.add(builder));
        }
        list.add(SortBuilders.fieldSort(fieldName).order(SortOrder.ASC.toString().equalsIgnoreCase(order) ? SortOrder.ASC : SortOrder.DESC));
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

    public String getDefaultQueryLanguage() {
        return defaultQueryLanguage;
    }

    public void setDefaultQueryLanguage(final String defaultQueryLanguage) {
        this.defaultQueryLanguage = defaultQueryLanguage;
    }

    public int getMaxPageSize() {
        final Object maxPageSize = crawlerProperties.get(Constants.SEARCH_RESULT_MAX_PAGE_SIZE);
        if (maxPageSize == null) {
            return MAX_PAGE_SIZE;
        }
        try {
            return Integer.parseInt(maxPageSize.toString());
        } catch (final NumberFormatException e) {
            return MAX_PAGE_SIZE;
        }
    }

    public int getDefaultPageSize() {
        return defaultPageSize;
    }

    public void setDefaultPageSize(final int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    public int getDefaultStart() {
        return defaultStartPosition;
    }

    public void setDefaultStart(final int defaultStartPosition) {
        this.defaultStartPosition = defaultStartPosition;
    }

    public Map<String, String[]> getQueryParamMap() {
        if (additionalQueryParamMap.isEmpty()) {
            return additionalQueryParamMap;
        }

        final HttpServletRequest request = LaRequestUtil.getOptionalRequest().orElse(null);
        final Map<String, String[]> queryParamMap = new HashMap<String, String[]>();
        for (final Map.Entry<String, String[]> entry : additionalQueryParamMap.entrySet()) {
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

    public void addQueryParam(final String key, final String[] values) {
        additionalQueryParamMap.put(key, values);
    }

    public void addFieldBoost(final String field, final String value) {
        try {
            Float.parseFloat(value);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException(value + " was not number.", e);
        }
        fieldBoostMap.put(field, value);
    }

    protected String getDefaultOperator() {
        final HttpServletRequest request = LaRequestUtil.getOptionalRequest().orElse(null);
        if (request != null) {
            final String defaultOperator = (String) request.getAttribute(Constants.DEFAULT_OPERATOR);
            if (AND.equalsIgnoreCase(defaultOperator)) {
                return _AND_;
            } else if (OR.equalsIgnoreCase(defaultOperator)) {
                return _OR_;
            }
        }
        return DEFAULT_OPERATOR;
    }

    protected void appendFieldBoostValue(final StringBuilder buf, final String field, final String value) {
        if (fieldBoostMap.containsKey(field) && value.indexOf('^') == -1 && value.indexOf('~') == -1) {
            buf.append('^').append(fieldBoostMap.get(field));
        }
    }
}
