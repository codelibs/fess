/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.core.stream.StreamUtil.stream;

import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import org.codelibs.core.misc.Pair;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.score.QueryRescorer;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.rescore.RescorerBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.web.util.LaRequestUtil;

public class QueryHelper {

    protected static final String PREFERENCE_QUERY = "_query";

    protected static final String ES_SCORE_FIELD = "_score";

    protected static final String SCORE_SORT_VALUE = "score";

    protected static final String SCORE_FIELD = "score";

    protected static final String INURL_FIELD = "inurl";

    protected static final String SITE_FIELD = "site";

    protected Set<String> apiResponseFieldSet;

    protected Set<String> highlightFieldSet = new HashSet<>();

    protected Set<String> notAnalyzedFieldSet;

    protected String[] responseFields;

    protected String[] scrollResponseFields;

    protected String[] cacheResponseFields;

    protected String[] highlightedFields;

    protected String[] searchFields;

    protected String[] facetFields;

    protected String sortPrefix = "sort:";

    protected String[] sortFields;

    protected String additionalQuery;

    protected boolean lowercaseWildcard = true;

    protected SortBuilder<?>[] defaultSortBuilders;

    protected String highlightPrefix = "hl_";

    protected FacetInfo defaultFacetInfo;

    protected GeoInfo defaultGeoInfo;

    protected Map<String, String> fieldBoostMap = new HashMap<>();

    protected List<FilterFunctionBuilder> boostFunctionList = new ArrayList<>();

    protected List<QueryRescorer> queryRescorerList = new ArrayList<>();

    protected List<Pair<String, Float>> additionalDefaultList = new ArrayList<>();

    @PostConstruct
    public void init() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (responseFields == null) {
            responseFields = fessConfig.getQueryAdditionalResponseFields(//
                    SCORE_FIELD, //
                    fessConfig.getIndexFieldId(), //
                    fessConfig.getIndexFieldDocId(), //
                    fessConfig.getIndexFieldBoost(), //
                    fessConfig.getIndexFieldContentLength(), //
                    fessConfig.getIndexFieldHost(), //
                    fessConfig.getIndexFieldSite(), //
                    fessConfig.getIndexFieldLastModified(), //
                    fessConfig.getIndexFieldTimestamp(), //
                    fessConfig.getIndexFieldMimetype(), //
                    fessConfig.getIndexFieldFiletype(), //
                    fessConfig.getIndexFieldFilename(), //
                    fessConfig.getIndexFieldCreated(), //
                    fessConfig.getIndexFieldTitle(), //
                    fessConfig.getIndexFieldDigest(), //
                    fessConfig.getIndexFieldUrl(), //
                    fessConfig.getIndexFieldThumbnail(), //
                    fessConfig.getIndexFieldClickCount(), //
                    fessConfig.getIndexFieldFavoriteCount(), //
                    fessConfig.getIndexFieldConfigId(), //
                    fessConfig.getIndexFieldLang(), //
                    fessConfig.getIndexFieldHasCache());
        }
        if (scrollResponseFields == null) {
            scrollResponseFields = fessConfig.getQueryAdditionalScrollResponseFields(//
                    SCORE_FIELD, //
                    fessConfig.getIndexFieldId(), //
                    fessConfig.getIndexFieldDocId(), //
                    fessConfig.getIndexFieldBoost(), //
                    fessConfig.getIndexFieldContentLength(), //
                    fessConfig.getIndexFieldHost(), //
                    fessConfig.getIndexFieldSite(), //
                    fessConfig.getIndexFieldLastModified(), //
                    fessConfig.getIndexFieldTimestamp(), //
                    fessConfig.getIndexFieldMimetype(), //
                    fessConfig.getIndexFieldFiletype(), //
                    fessConfig.getIndexFieldFilename(), //
                    fessConfig.getIndexFieldCreated(), //
                    fessConfig.getIndexFieldTitle(), //
                    fessConfig.getIndexFieldDigest(), //
                    fessConfig.getIndexFieldUrl(), //
                    fessConfig.getIndexFieldThumbnail(), //
                    fessConfig.getIndexFieldClickCount(), //
                    fessConfig.getIndexFieldFavoriteCount(), //
                    fessConfig.getIndexFieldConfigId(), //
                    fessConfig.getIndexFieldLang(), //
                    fessConfig.getIndexFieldHasCache());
        }
        if (cacheResponseFields == null) {
            cacheResponseFields = fessConfig.getQueryAdditionalCacheResponseFields(//
                    SCORE_FIELD, //
                    fessConfig.getIndexFieldId(), //
                    fessConfig.getIndexFieldDocId(), //
                    fessConfig.getIndexFieldBoost(), //
                    fessConfig.getIndexFieldContentLength(), //
                    fessConfig.getIndexFieldHost(), //
                    fessConfig.getIndexFieldSite(), //
                    fessConfig.getIndexFieldLastModified(), //
                    fessConfig.getIndexFieldTimestamp(), //
                    fessConfig.getIndexFieldMimetype(), //
                    fessConfig.getIndexFieldFiletype(), //
                    fessConfig.getIndexFieldFilename(), //
                    fessConfig.getIndexFieldCreated(), //
                    fessConfig.getIndexFieldTitle(), //
                    fessConfig.getIndexFieldDigest(), //
                    fessConfig.getIndexFieldUrl(), //
                    fessConfig.getIndexFieldClickCount(), //
                    fessConfig.getIndexFieldFavoriteCount(), //
                    fessConfig.getIndexFieldConfigId(), //
                    fessConfig.getIndexFieldLang(), //
                    fessConfig.getIndexFieldCache());
        }
        if (highlightedFields == null) {
            highlightedFields = fessConfig.getQueryAdditionalHighlightedFields( //
                    fessConfig.getIndexFieldContent());
        }
        if (searchFields == null) {
            searchFields = fessConfig.getQueryAdditionalSearchFields(//
                    INURL_FIELD, //
                    fessConfig.getIndexFieldUrl(), //
                    fessConfig.getIndexFieldDocId(), //
                    fessConfig.getIndexFieldHost(), //
                    fessConfig.getIndexFieldSite(), //
                    fessConfig.getIndexFieldTitle(), //
                    fessConfig.getIndexFieldContent(), //
                    fessConfig.getIndexFieldContentLength(), //
                    fessConfig.getIndexFieldLastModified(), //
                    fessConfig.getIndexFieldTimestamp(), //
                    fessConfig.getIndexFieldMimetype(), //
                    fessConfig.getIndexFieldFiletype(), //
                    fessConfig.getIndexFieldFilename(), //
                    fessConfig.getIndexFieldLabel(), //
                    fessConfig.getIndexFieldSegment(), //
                    fessConfig.getIndexFieldAnchor(), //
                    fessConfig.getIndexFieldClickCount(), //
                    fessConfig.getIndexFieldFavoriteCount(), //
                    fessConfig.getIndexFieldLang());
        }
        if (facetFields == null) {
            facetFields = fessConfig.getQueryAdditionalFacetFields(//
                    fessConfig.getIndexFieldUrl(), //
                    fessConfig.getIndexFieldHost(), //
                    fessConfig.getIndexFieldTitle(), //
                    fessConfig.getIndexFieldContent(), //
                    fessConfig.getIndexFieldContentLength(), //
                    fessConfig.getIndexFieldLastModified(), //
                    fessConfig.getIndexFieldTimestamp(), //
                    fessConfig.getIndexFieldMimetype(), //
                    fessConfig.getIndexFieldFiletype(), //
                    fessConfig.getIndexFieldLabel(), //
                    fessConfig.getIndexFieldSegment());
        }
        if (sortFields == null) {
            sortFields = fessConfig.getQueryAdditionalSortFields(//
                    SCORE_SORT_VALUE, //
                    fessConfig.getIndexFieldFilename(), //
                    fessConfig.getIndexFieldCreated(), //
                    fessConfig.getIndexFieldContentLength(), //
                    fessConfig.getIndexFieldLastModified(), //
                    fessConfig.getIndexFieldTimestamp(), //
                    fessConfig.getIndexFieldClickCount(), //
                    fessConfig.getIndexFieldFavoriteCount());
        }
        if (apiResponseFieldSet == null) {
            setApiResponseFields(fessConfig.getQueryAdditionalApiResponseFields(//
                    fessConfig.getResponseFieldContentDescription(), //
                    fessConfig.getResponseFieldContentTitle(), //
                    fessConfig.getResponseFieldSitePath(), //
                    fessConfig.getResponseFieldUrlLink(), //
                    fessConfig.getIndexFieldId(), //
                    fessConfig.getIndexFieldDocId(), //
                    fessConfig.getIndexFieldBoost(), //
                    fessConfig.getIndexFieldContentLength(), //
                    fessConfig.getIndexFieldHost(), //
                    fessConfig.getIndexFieldSite(), //
                    fessConfig.getIndexFieldLastModified(), //
                    fessConfig.getIndexFieldTimestamp(), //
                    fessConfig.getIndexFieldMimetype(), //
                    fessConfig.getIndexFieldFiletype(), //
                    fessConfig.getIndexFieldFilename(), //
                    fessConfig.getIndexFieldCreated(), //
                    fessConfig.getIndexFieldTitle(), //
                    fessConfig.getIndexFieldDigest(), //
                    fessConfig.getIndexFieldUrl()));
        }
        if (notAnalyzedFieldSet == null) {
            setNotAnalyzedFields(fessConfig.getQueryAdditionalNotAnalyzedFields(//
                    fessConfig.getIndexFieldAnchor(), //
                    fessConfig.getIndexFieldBoost(), //
                    fessConfig.getIndexFieldClickCount(), //
                    fessConfig.getIndexFieldConfigId(), //
                    fessConfig.getIndexFieldContentLength(), //
                    fessConfig.getIndexFieldCreated(), //
                    fessConfig.getIndexFieldDocId(), //
                    fessConfig.getIndexFieldExpires(), //
                    fessConfig.getIndexFieldFavoriteCount(), //
                    fessConfig.getIndexFieldFiletype(), //
                    fessConfig.getIndexFieldFilename(), //
                    fessConfig.getIndexFieldHasCache(), //
                    fessConfig.getIndexFieldHost(), //
                    fessConfig.getIndexFieldId(), //
                    fessConfig.getIndexFieldLabel(), //
                    fessConfig.getIndexFieldLang(), //
                    fessConfig.getIndexFieldLastModified(), //
                    fessConfig.getIndexFieldMimetype(), //
                    fessConfig.getIndexFieldParentId(), //
                    fessConfig.getIndexFieldPrimaryTerm(), //
                    fessConfig.getIndexFieldRole(), //
                    fessConfig.getIndexFieldSegment(), //
                    fessConfig.getIndexFieldSeqNo(), //
                    fessConfig.getIndexFieldSite(), //
                    fessConfig.getIndexFieldTimestamp(), //
                    fessConfig.getIndexFieldUrl(), //
                    fessConfig.getIndexFieldVersion()));
        }
        split(fessConfig.getQueryAdditionalAnalyzedFields(), ",").of(
                stream -> stream.map(s -> s.trim()).filter(StringUtil::isNotBlank).forEach(s -> notAnalyzedFieldSet.remove(s)));
        split(fessConfig.getQueryAdditionalDefaultFields(), ",").of(stream -> stream.filter(StringUtil::isNotBlank).map(s -> {
            final Pair<String, Float> pair = new Pair<>();
            final String[] values = s.split(":");
            if (values.length == 1) {
                pair.setFirst(values[0].trim());
                pair.setSecond(1.0f);
            } else if (values.length > 1) {
                pair.setFirst(values[0]);
                pair.setSecond(Float.parseFloat(values[1]));
            } else {
                return null;
            }
            return pair;
        }).forEach(additionalDefaultList::add));
    }

    public QueryContext build(final SearchRequestType searchRequestType, final String query, final Consumer<QueryContext> context) {
        String q;
        if (additionalQuery != null && StringUtil.isNotBlank(query)) {
            q = query + " " + additionalQuery;
        } else {
            q = query;
        }

        final QueryContext queryContext = new QueryContext(q, true);
        buildBaseQuery(queryContext, context);
        buildBoostQuery(queryContext);
        buildRoleQuery(queryContext, searchRequestType);
        buildVirtualHostQuery(queryContext, searchRequestType);

        if (!queryContext.hasSorts() && defaultSortBuilders != null) {
            queryContext.addSorts(defaultSortBuilders);
        }
        return queryContext;
    }

    protected void buildVirtualHostQuery(final QueryContext queryContext, final SearchRequestType searchRequestType) {
        switch (searchRequestType) {
        case ADMIN_SEARCH:
            // nothing to do
            break;
        default:
            final String key = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
            if (StringUtil.isNotBlank(key)) {
                queryContext.addQuery(boolQuery -> {
                    boolQuery.filter(QueryBuilders.termQuery(ComponentUtil.getFessConfig().getIndexFieldVirtualHost(), key));
                });
            }
            break;
        }
    }

    protected void buildRoleQuery(final QueryContext queryContext, final SearchRequestType searchRequestType) {
        if (queryContext.roleQueryEnabled()) {
            final Set<String> roleSet = ComponentUtil.getRoleQueryHelper().build(searchRequestType);
            if (!roleSet.isEmpty()) {
                queryContext.addQuery(boolQuery -> buildRoleQuery(roleSet, boolQuery));
            }
        }
    }

    public void buildRoleQuery(final Set<String> roleSet, final BoolQueryBuilder boolQuery) {
        final BoolQueryBuilder roleQuery = QueryBuilders.boolQuery();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String roleField = fessConfig.getIndexFieldRole();
        roleSet.stream().forEach(name -> roleQuery.should(QueryBuilders.termQuery(roleField, name)));
        final String deniedPrefix = fessConfig.getRoleSearchDeniedPrefix();
        roleSet.stream().forEach(name -> roleQuery.mustNot(QueryBuilders.termQuery(roleField, deniedPrefix + name)));
        boolQuery.filter(roleQuery);
    }

    protected void buildBoostQuery(final QueryContext queryContext) {
        queryContext.addFunctionScore(list -> {
            list.add(new FilterFunctionBuilder(ScoreFunctionBuilders.fieldValueFactorFunction(ComponentUtil.getFessConfig()
                    .getIndexFieldBoost())));
            ComponentUtil.getKeyMatchHelper().buildQuery(queryContext.getDefaultKeyword(), list);
            list.addAll(boostFunctionList);
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
        if (boolQuery.hasClauses()) {
            return boolQuery;
        }
        return null;
    }

    protected String toLowercaseWildcard(final String value) {
        if (lowercaseWildcard) {
            return value.toLowerCase(Locale.ROOT);
        }
        return value;
    }

    protected String getSearchField(final QueryContext context, final String field) {
        if (Constants.DEFAULT_FIELD.equals(field) && context.getDefaultField() != null) {
            return context.getDefaultField();
        }
        return field;
    }

    protected QueryBuilder convertWildcardQuery(final QueryContext context, final WildcardQuery wildcardQuery, final float boost) {
        final String field = getSearchField(context, wildcardQuery.getField());
        if (Constants.DEFAULT_FIELD.equals(field)) {
            context.addFieldLog(field, wildcardQuery.getTerm().text());
            return buildDefaultQueryBuilder((f, b) -> QueryBuilders.wildcardQuery(f, toLowercaseWildcard(wildcardQuery.getTerm().text()))
                    .boost(b * boost));
        } else if (isSearchField(field)) {
            context.addFieldLog(field, wildcardQuery.getTerm().text());
            return QueryBuilders.wildcardQuery(field, toLowercaseWildcard(wildcardQuery.getTerm().text())).boost(boost);
        } else {
            final String query = wildcardQuery.getTerm().toString();
            final String origQuery = toLowercaseWildcard(query);
            context.addFieldLog(Constants.DEFAULT_FIELD, query);
            context.addHighlightedQuery(origQuery);
            return buildDefaultQueryBuilder((f, b) -> QueryBuilders.wildcardQuery(f, origQuery).boost(b * boost));
        }
    }

    protected QueryBuilder convertPrefixQuery(final QueryContext context, final PrefixQuery prefixQuery, final float boost) {
        final String field = getSearchField(context, prefixQuery.getField());
        if (Constants.DEFAULT_FIELD.equals(field)) {
            context.addFieldLog(field, prefixQuery.getPrefix().text());
            return buildDefaultQueryBuilder((f, b) -> QueryBuilders.prefixQuery(f, toLowercaseWildcard(prefixQuery.getPrefix().text()))
                    .boost(b * boost));
        } else if (isSearchField(field)) {
            context.addFieldLog(field, prefixQuery.getPrefix().text());
            return QueryBuilders.prefixQuery(field, toLowercaseWildcard(prefixQuery.getPrefix().text())).boost(boost);
        } else {
            final String query = prefixQuery.getPrefix().toString();
            final String origQuery = toLowercaseWildcard(query);
            context.addFieldLog(Constants.DEFAULT_FIELD, query);
            context.addHighlightedQuery(origQuery);
            return buildDefaultQueryBuilder((f, b) -> QueryBuilders.prefixQuery(f, origQuery).boost(b * boost));
        }
    }

    protected QueryBuilder convertFuzzyQuery(final QueryContext context, final FuzzyQuery fuzzyQuery, final float boost) {
        final Term term = fuzzyQuery.getTerm();
        final String field = getSearchField(context, term.field());
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
        final String field = getSearchField(context, termRangeQuery.getField());
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

    protected QueryBuilder buildMatchPhraseQuery(final String f, final String text) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (text == null || text.length() != 1
                || (!fessConfig.getIndexFieldTitle().equals(f) && !fessConfig.getIndexFieldContent().equals(f))) {
            return QueryBuilders.matchPhraseQuery(f, text);
        }

        final UnicodeBlock block = UnicodeBlock.of(text.codePointAt(0));
        if (block == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS //
                || block == UnicodeBlock.HIRAGANA //
                || block == UnicodeBlock.KATAKANA //
                || block == UnicodeBlock.HANGUL_SYLLABLES //
        ) {
            return QueryBuilders.prefixQuery(f, text);
        }
        return QueryBuilders.matchPhraseQuery(f, text);
    }

    protected QueryBuilder convertTermQuery(final QueryContext context, final TermQuery termQuery, final float boost) {
        final String field = getSearchField(context, termQuery.getTerm().field());
        final String text = termQuery.getTerm().text();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (fessConfig.getQueryReplaceTermWithPrefixQueryAsBoolean() && text.length() > 1 && text.endsWith("*")) {
            return convertPrefixQuery(context, new PrefixQuery(new Term(field, text.substring(0, text.length() - 1))), boost);
        } else if (Constants.DEFAULT_FIELD.equals(field)) {
            context.addFieldLog(field, text);
            context.addHighlightedQuery(text);
            return buildDefaultQueryBuilder((f, b) -> buildMatchPhraseQuery(f, text).boost(b * boost));
        } else if ("sort".equals(field)) {
            split(text, ",").of(
                    stream -> stream.filter(StringUtil::isNotBlank).forEach(
                            t -> {
                                final String[] values = t.split("\\.");
                                if (values.length > 2) {
                                    throw new InvalidQueryException(messages -> messages.addErrorsInvalidQuerySortValue(
                                            UserMessages.GLOBAL_PROPERTY_KEY, text), "Invalid sort field: " + termQuery);
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
                                context.addSorts(createFieldSortBuilder(sortField, sortOrder));
                            }));
            return null;
        } else if (INURL_FIELD.equals(field)
                || (StringUtil.equals(field, context.getDefaultField()) && fessConfig.getIndexFieldUrl().equals(context.getDefaultField()))) {
            return QueryBuilders.wildcardQuery(fessConfig.getIndexFieldUrl(), "*" + text + "*").boost(boost);
        } else if (SITE_FIELD.equals(field)) {
            return convertSiteQuery(context, text, boost);
        } else if (isSearchField(field)) {
            context.addFieldLog(field, text);
            context.addHighlightedQuery(text);
            if (notAnalyzedFieldSet.contains(field)) {
                return QueryBuilders.termQuery(field, text).boost(boost);
            } else {
                return buildMatchPhraseQuery(field, text).boost(boost);
            }
        } else {
            final String origQuery = termQuery.toString();
            context.addFieldLog(Constants.DEFAULT_FIELD, origQuery);
            context.addHighlightedQuery(origQuery);
            return buildDefaultQueryBuilder((f, b) -> buildMatchPhraseQuery(f, origQuery).boost(b * boost));
        }
    }

    protected QueryBuilder convertSiteQuery(final QueryContext context, final String text, final float boost) {
        return QueryBuilders.prefixQuery(ComponentUtil.getFessConfig().getIndexFieldSite(), text).boost(boost);
    }

    protected QueryBuilder convertPhraseQuery(final QueryContext context, final PhraseQuery query, final float boost) {
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
        return buildDefaultQueryBuilder((f, b) -> buildMatchPhraseQuery(f, text).boost(b * boost));
    }

    protected boolean isSearchField(final String field) {
        for (final String searchField : searchFields) {
            if (searchField.equals(field)) {
                return true;
            }
        }
        return false;
    }

    protected QueryBuilder buildDefaultQueryBuilder(final DefaultQueryBuilderFunction builder) {
        final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final QueryBuilder titleQuery =
                builder.apply(fessConfig.getIndexFieldTitle(), fessConfig.getQueryBoostTitleAsDecimal().floatValue());
        boolQuery.should(titleQuery);
        final QueryBuilder contentQuery =
                builder.apply(fessConfig.getIndexFieldContent(), fessConfig.getQueryBoostContentAsDecimal().floatValue());
        boolQuery.should(contentQuery);
        getQueryLanguages().ifPresent(
                langs -> stream(langs).of(
                        stream -> stream.forEach(lang -> {
                            final QueryBuilder titleLangQuery =
                                    builder.apply(fessConfig.getIndexFieldTitle() + "_" + lang, fessConfig
                                            .getQueryBoostTitleLangAsDecimal().floatValue());
                            boolQuery.should(titleLangQuery);
                            final QueryBuilder contentLangQuery =
                                    builder.apply(fessConfig.getIndexFieldContent() + "_" + lang, fessConfig
                                            .getQueryBoostContentLangAsDecimal().floatValue());
                            boolQuery.should(contentLangQuery);
                        })));
        additionalDefaultList.stream().forEach(f -> {
            final QueryBuilder query = builder.apply(f.getFirst(), f.getSecond());
            boolQuery.should(query);
        });
        return boolQuery;
    }

    interface DefaultQueryBuilderFunction {
        QueryBuilder apply(String field, float boost);
    }

    protected OptionalThing<String[]> getQueryLanguages() {
        return LaRequestUtil.getOptionalRequest().map(
                request -> ComponentUtil.getFessConfig().getQueryLanguages(request.getLocales(),
                        (String[]) request.getAttribute(Constants.REQUEST_LANGUAGES)));
    }

    protected boolean isSortField(final String field) {
        for (final String f : sortFields) {
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

    public void setNotAnalyzedFields(final String[] fields) {
        notAnalyzedFieldSet = new HashSet<>();
        for (final String field : fields) {
            notAnalyzedFieldSet.add(field);
        }
    }

    public boolean isApiResponseField(final String field) {
        return apiResponseFieldSet.contains(field);
    }

    public void processSearchPreference(final SearchRequestBuilder searchRequestBuilder, final OptionalThing<FessUserBean> userBean,
            final String query) {
        userBean.map(user -> {
            if (user.hasRoles(ComponentUtil.getFessConfig().getAuthenticationAdminRolesAsArray())) {
                return Constants.SEARCH_PREFERENCE_LOCAL;
            }
            return user.getUserId();
        }).ifPresent(p -> searchRequestBuilder.setPreference(p)).orElse(() -> LaRequestUtil.getOptionalRequest().map(r -> {
            final HttpSession session = r.getSession(false);
            if (session != null) {
                return session.getId();
            }
            final String preference = r.getParameter("preference");
            if (preference != null) {
                return Integer.toString(preference.hashCode());
            }
            final Object accessType = r.getAttribute(Constants.SEARCH_LOG_ACCESS_TYPE);
            if (Constants.SEARCH_LOG_ACCESS_TYPE_JSON.equals(accessType)) {
                return processJsonSearchPreference(r, query);
            } else if (Constants.SEARCH_LOG_ACCESS_TYPE_GSA.equals(accessType)) {
                return processGsaSearchPreference(r, query);
            }
            return null;
        }).ifPresent(p -> searchRequestBuilder.setPreference(p)));
    }

    protected String processJsonSearchPreference(final HttpServletRequest req, final String query) {
        final String pref = ComponentUtil.getFessConfig().getQueryJsonDefaultPreference();
        if (PREFERENCE_QUERY.equals(pref)) {
            return Integer.toString(query.hashCode());
        } else if (StringUtil.isNotBlank(pref)) {
            return pref;
        }
        return null;
    }

    protected String processGsaSearchPreference(final HttpServletRequest req, final String query) {
        final String pref = ComponentUtil.getFessConfig().getQueryGsaDefaultPreference();
        if (PREFERENCE_QUERY.equals(pref)) {
            return Integer.toString(query.hashCode());
        } else if (StringUtil.isNotBlank(pref)) {
            return pref;
        }
        return null;
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

    public String[] getScrollResponseFields() {
        return scrollResponseFields;
    }

    public void setScrollResponseFields(final String[] scrollResponseFields) {
        this.scrollResponseFields = scrollResponseFields;
    }

    public String[] getCacheResponseFields() {
        return cacheResponseFields;
    }

    public void setCacheResponseFields(final String[] cacheResponseFields) {
        this.cacheResponseFields = cacheResponseFields;
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
     * @return the sortFields
     */
    public String[] getSortFields() {
        return sortFields;
    }

    /**
     * @param sortFields the sortFields to set
     */
    public void setSortFields(final String[] sortFields) {
        this.sortFields = sortFields;
    }

    public void addHighlightField(final String field) {
        highlightFieldSet.add(field);
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

    public void addDefaultSort(final String fieldName, final String order) {
        final List<SortBuilder<?>> list = new ArrayList<>();
        if (defaultSortBuilders != null) {
            stream(defaultSortBuilders).of(stream -> stream.forEach(builder -> list.add(builder)));
        }
        list.add(createFieldSortBuilder(fieldName, SortOrder.DESC.toString().equalsIgnoreCase(order) ? SortOrder.DESC : SortOrder.ASC));
        defaultSortBuilders = list.toArray(new SortBuilder[list.size()]);
    }

    protected SortBuilder<?> createFieldSortBuilder(final String field, final SortOrder order) {
        if (SCORE_FIELD.equals(field) || ES_SCORE_FIELD.equals(field)) {
            return SortBuilders.scoreSort().order(order);
        } else {
            return SortBuilders.fieldSort(field).order(order);
        }
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

    public String generateId() {
        return UUID.randomUUID().toString().replace("-", StringUtil.EMPTY);
    }

    public void setLowercaseWildcard(final boolean lowercaseWildcard) {
        this.lowercaseWildcard = lowercaseWildcard;
    }

    public void addBoostFunction(final ScoreFunctionBuilder<?> scoreFunction) {
        boostFunctionList.add(new FilterFunctionBuilder(scoreFunction));
    }

    public void addBoostFunction(final QueryBuilder filter, final ScoreFunctionBuilder<?> scoreFunction) {
        boostFunctionList.add(new FilterFunctionBuilder(filter, scoreFunction));
    }

    public RescorerBuilder<?>[] getRescorers(final Map<String, Object> params) {
        return queryRescorerList.stream().map(r -> r.evaluate(params)).filter(b -> b != null).toArray(n -> new RescorerBuilder<?>[n]);
    }

    public void addQueryRescorer(final QueryRescorer rescorer) {
        queryRescorerList.add(rescorer);
    }
}
