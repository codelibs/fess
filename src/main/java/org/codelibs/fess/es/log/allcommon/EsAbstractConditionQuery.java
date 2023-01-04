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
package org.codelibs.fess.es.log.allcommon;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.ConditionQuery;
import org.dbflute.cbean.ckey.ConditionKey;
import org.dbflute.cbean.coption.ConditionOption;
import org.dbflute.cbean.coption.ParameterOption;
import org.dbflute.cbean.cvalue.ConditionValue;
import org.dbflute.cbean.sqlclause.SqlClause;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.name.ColumnRealName;
import org.dbflute.dbmeta.name.ColumnSqlName;
import org.dbflute.exception.InvalidQueryRegisteredException;
import org.dbflute.util.Srl;
import org.opensearch.common.unit.Fuzziness;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.CommonTermsQueryBuilder;
import org.opensearch.index.query.ExistsQueryBuilder;
import org.opensearch.index.query.IdsQueryBuilder;
import org.opensearch.index.query.MatchAllQueryBuilder;
import org.opensearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.opensearch.index.query.MatchPhraseQueryBuilder;
import org.opensearch.index.query.MatchQueryBuilder;
import org.opensearch.index.query.MoreLikeThisQueryBuilder;
import org.opensearch.index.query.PrefixQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.QueryStringQueryBuilder;
import org.opensearch.index.query.RangeQueryBuilder;
import org.opensearch.index.query.RegexpQueryBuilder;
import org.opensearch.index.query.SpanTermQueryBuilder;
import org.opensearch.index.query.TermQueryBuilder;
import org.opensearch.index.query.TermsQueryBuilder;
import org.opensearch.index.query.WildcardQueryBuilder;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;
import org.opensearch.index.query.functionscore.ScoreFunctionBuilder;
import org.opensearch.search.sort.FieldSortBuilder;
import org.opensearch.search.sort.SortBuilders;
import org.opensearch.search.sort.SortOrder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class EsAbstractConditionQuery implements ConditionQuery {

    protected static final String CQ_PROPERTY = "conditionQuery";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<QueryBuilder> queryBuilderList;
    protected List<FieldSortBuilder> fieldSortBuilderList;
    private DocMetaCQ docMetaCQ;

    // ===================================================================================
    //                                                                             Control
    //                                                                             =======
    public DocMetaCQ docMeta() {
        if (docMetaCQ == null) {
            docMetaCQ = new DocMetaCQ();
        }
        return docMetaCQ;
    }

    public List<FieldSortBuilder> getFieldSortBuilderList() {
        return fieldSortBuilderList == null ? Collections.emptyList() : fieldSortBuilderList;
    }

    public boolean hasQueries() {
        return queryBuilderList != null && !queryBuilderList.isEmpty();
    }

    public QueryBuilder getQuery() {
        if (queryBuilderList == null) {
            return null;
        } else if (queryBuilderList.size() == 1) {
            return queryBuilderList.get(0);
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        queryBuilderList.forEach(query -> {
            boolQuery.must(query);
        });
        return boolQuery;
    }

    public List<QueryBuilder> getQueryBuilderList() {
        return queryBuilderList != null ? queryBuilderList : Collections.emptyList();
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    public void addQuery(QueryBuilder queryBuilder) {
        assertObjectNotNull("queryBuilder", queryBuilder);
        regQ(queryBuilder);
    }

    public void queryString(String queryString) {
        checkEsInvalidQuery("queryString", queryString);
        doQueryString(queryString, null);
    }

    public void queryString(String queryString, ConditionOptionCall<QueryStringQueryBuilder> opLambda) {
        checkEsInvalidQuery("queryString", queryString);
        assertObjectNotNull("opLambda", opLambda);
        doQueryString(queryString, opLambda);
    }

    protected void doQueryString(String queryString, ConditionOptionCall<QueryStringQueryBuilder> opLambda) {
        QueryStringQueryBuilder queryStringQuery = QueryBuilders.queryStringQuery(queryString);
        regQ(queryStringQuery);
        if (opLambda != null) {
            opLambda.callback(queryStringQuery);
        }
    }

    public void matchAll() {
        doMatchAll(null);
    }

    public void matchAll(ConditionOptionCall<MatchAllQueryBuilder> opLambda) {
        assertObjectNotNull("opLambda", opLambda);
        doMatchAll(opLambda);
    }

    protected void doMatchAll(ConditionOptionCall<MatchAllQueryBuilder> opLambda) {
        MatchAllQueryBuilder builder = QueryBuilders.matchAllQuery();
        regQ(builder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    // ===================================================================================
    //                                                                            Register
    //                                                                            ========

    protected FunctionScoreQueryBuilder regFunctionScoreQ(QueryBuilder queryBuilder, Collection<FilterFunctionBuilder> list) {
        FunctionScoreQueryBuilder functionScoreQuery =
                QueryBuilders.functionScoreQuery(queryBuilder, list.toArray(new FilterFunctionBuilder[list.size()]));
        regQ(functionScoreQuery);
        return functionScoreQuery;
    }

    protected BoolQueryBuilder regBoolCQ(List<QueryBuilder> mustList, List<QueryBuilder> shouldList, List<QueryBuilder> mustNotList,
            List<QueryBuilder> filterList) {
        assertObjectNotNull("mustList", mustList);
        assertObjectNotNull("shouldList", shouldList);
        assertObjectNotNull("mustNotList", mustNotList);
        assertObjectNotNull("filterList", filterList);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        mustList.forEach(query -> {
            boolQuery.must(query);
        });
        shouldList.forEach(query -> {
            boolQuery.should(query);
        });
        mustNotList.forEach(query -> {
            boolQuery.mustNot(query);
        });
        filterList.forEach(query -> {
            boolQuery.filter(query);
        });
        regQ(boolQuery);
        return boolQuery;
    }

    protected TermQueryBuilder regTermQ(String name, Object value) {
        checkEsInvalidQuery(name, value);
        TermQueryBuilder termQuery = QueryBuilders.termQuery(name, value);
        regQ(termQuery);
        return termQuery;
    }

    protected TermsQueryBuilder regTermsQ(String name, Collection<?> values) {
        checkEsInvalidQueryCollection(name, values);
        TermsQueryBuilder termsQuery = QueryBuilders.termsQuery(name, values);
        regQ(termsQuery);
        return termsQuery;
    }

    protected IdsQueryBuilder regIdsQ(Collection<String> values) {
        checkEsInvalidQueryCollection("_id", values);
        IdsQueryBuilder idsQuery = QueryBuilders.idsQuery().addIds(values.toArray(new String[values.size()]));
        regQ(idsQuery);
        return idsQuery;
    }

    protected MatchQueryBuilder regMatchQ(String name, Object value) {
        checkEsInvalidQuery(name, value);
        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery(name, value);
        regQ(matchQuery);
        return matchQuery;
    }

    protected MatchPhraseQueryBuilder regMatchPhraseQ(String name, Object value) {
        checkEsInvalidQuery(name, value);
        MatchPhraseQueryBuilder matchQuery = QueryBuilders.matchPhraseQuery(name, value);
        regQ(matchQuery);
        return matchQuery;
    }

    protected MatchPhrasePrefixQueryBuilder regMatchPhrasePrefixQ(String name, Object value) {
        checkEsInvalidQuery(name, value);
        MatchPhrasePrefixQueryBuilder matchQuery = QueryBuilders.matchPhrasePrefixQuery(name, value);
        regQ(matchQuery);
        return matchQuery;
    }

    protected MatchQueryBuilder regFuzzyQ(String name, Object value) {
        checkEsInvalidQuery(name, value);
        MatchQueryBuilder fuzzyQuery = QueryBuilders.matchQuery(name, value).fuzziness(Fuzziness.AUTO);
        regQ(fuzzyQuery);
        return fuzzyQuery;
    }

    protected PrefixQueryBuilder regPrefixQ(String name, String prefix) {
        checkEsInvalidQuery(name, prefix);
        PrefixQueryBuilder prefixQuery = QueryBuilders.prefixQuery(name, prefix);
        regQ(prefixQuery);
        return prefixQuery;
    }

    protected RangeQueryBuilder regRangeQ(String name, ConditionKey ck, Object value) {
        checkEsInvalidQuery(name, value);
        assertObjectNotNull("ck", ck);
        if (queryBuilderList != null) {
            for (QueryBuilder builder : queryBuilderList) {
                if (builder instanceof RangeQueryBuilder) {
                    RangeQueryBuilder rangeQueryBuilder = (RangeQueryBuilder) builder;
                    if (rangeQueryBuilder.toString().replaceAll("\\s", "").startsWith("{\"range\":{\"" + name + "\"")) {
                        addRangeC(rangeQueryBuilder, ck, value);
                        return rangeQueryBuilder;
                    }
                }
            }
        }
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(name);
        addRangeC(rangeQueryBuilder, ck, value);
        regQ(rangeQueryBuilder);
        return rangeQueryBuilder;
    }

    protected void addRangeC(RangeQueryBuilder builder, ConditionKey ck, Object value) {
        assertObjectNotNull("ck", ck);
        if (ck.equals(ConditionKey.CK_GREATER_THAN)) {
            builder.gt(value);
        } else if (ck.equals(ConditionKey.CK_GREATER_EQUAL)) {
            builder.gte(value);
        } else if (ck.equals(ConditionKey.CK_LESS_THAN)) {
            builder.lt(value);
        } else if (ck.equals(ConditionKey.CK_LESS_EQUAL)) {
            builder.lte(value);
        }
    }

    protected ExistsQueryBuilder regExistsQ(String name) {
        ExistsQueryBuilder existsQuery = QueryBuilders.existsQuery(name);
        regQ(existsQuery);
        return existsQuery;
    }

    protected WildcardQueryBuilder regWildcardQ(String name, String wildcard) {
        checkEsInvalidQuery(name, wildcard);
        WildcardQueryBuilder wildcardQuery = QueryBuilders.wildcardQuery(name, wildcard);
        regQ(wildcardQuery);
        return wildcardQuery;
    }

    protected RegexpQueryBuilder regRegexpQ(String name, String regexp) {
        checkEsInvalidQuery(name, regexp);
        RegexpQueryBuilder regexpQuery = QueryBuilders.regexpQuery(name, regexp);
        regQ(regexpQuery);
        return regexpQuery;
    }

    protected CommonTermsQueryBuilder regCommonTermsQ(String name, Object text) {
        checkEsInvalidQuery(name, text);
        CommonTermsQueryBuilder commonTermsQuery = QueryBuilders.commonTermsQuery(name, text);
        regQ(commonTermsQuery);
        return commonTermsQuery;
    }

    protected MoreLikeThisQueryBuilder regMoreLikeThisQueryQ(String name, String[] likeTexts) {
        MoreLikeThisQueryBuilder moreLikeThisQuery = QueryBuilders.moreLikeThisQuery(new String[] { name }, likeTexts, null);
        regQ(moreLikeThisQuery);
        return moreLikeThisQuery;
    }

    protected SpanTermQueryBuilder regSpanTermQ(String name, String value) {
        checkEsInvalidQuery(name, value);
        SpanTermQueryBuilder spanTermQuery = QueryBuilders.spanTermQuery(name, value);
        regQ(spanTermQuery);
        return spanTermQuery;
    }

    protected void regQ(QueryBuilder builder) {
        assertObjectNotNull("builder", builder);
        if (queryBuilderList == null) {
            queryBuilderList = new ArrayList<>();
        }
        queryBuilderList.add(builder);
    }

    protected void regOBA(String field) {
        registerOrderBy(field, true);
    }

    protected void regOBD(String field) {
        registerOrderBy(field, false);
    }

    protected void registerOrderBy(String field, boolean ascOrDesc) {
        assertObjectNotNull("field", field);
        if (fieldSortBuilderList == null) {
            fieldSortBuilderList = new ArrayList<>();
        }
        fieldSortBuilderList.add(SortBuilders.fieldSort(field).order(ascOrDesc ? SortOrder.ASC : SortOrder.DESC));
    }

    // ===================================================================================
    //                                                                       Invalid Query
    //                                                                       =============
    protected void checkEsInvalidQuery(String name, Object value) {
        if (value == null || (value instanceof String && ((String) value).isEmpty())) {
            String msg = "Cannot register null or empty query: name=" + name + " value=" + value;
            throw new InvalidQueryRegisteredException(msg);
        }
    }

    protected void checkEsInvalidQueryCollection(String name, Collection<?> values) {
        if (values == null || values.isEmpty()) {
            String msg = "Cannot register null or empty query collection: name=" + name + " values=" + values;
            throw new InvalidQueryRegisteredException(msg);
        }
    }

    // ===================================================================================
    //                                                              DBFlute Implementation
    //                                                              ======================
    @Override
    public ColumnRealName toColumnRealName(String columnDbName) {
        return ColumnRealName.create(xgetAliasName(), toColumnSqlName(columnDbName));
    }

    @Override
    public ColumnRealName toColumnRealName(ColumnInfo columnInfo) {
        return ColumnRealName.create(xgetAliasName(), columnInfo.getColumnSqlName());
    }

    @Override
    public ColumnSqlName toColumnSqlName(String columnDbName) {
        return new ColumnSqlName(columnDbName);
    }

    @Override
    public ConditionBean xgetBaseCB() {
        return null;
    }

    @Override
    public ConditionQuery xgetBaseQuery() {
        return null;
    }

    @Override
    public ConditionQuery xgetReferrerQuery() {
        return null;
    }

    @Override
    public SqlClause xgetSqlClause() {
        return null;
    }

    @Override
    public int xgetNestLevel() {
        return 0;
    }

    @Override
    public int xgetNextNestLevel() {
        return 0;
    }

    @Override
    public boolean isBaseQuery() {
        return false;
    }

    @Override
    public String xgetForeignPropertyName() {
        return null;
    }

    @Override
    public String xgetRelationPath() {
        return null;
    }

    @Override
    public String xgetLocationBase() {
        final StringBuilder sb = new StringBuilder();
        ConditionQuery query = this;
        while (true) {
            if (query.isBaseQuery()) {
                sb.insert(0, CQ_PROPERTY + ".");
                break;
            } else {
                final String foreignPropertyName = query.xgetForeignPropertyName();
                if (foreignPropertyName == null) {
                    String msg = "The foreignPropertyName of the query should not be null:";
                    msg = msg + " query=" + query;
                    throw new IllegalStateException(msg);
                }
                sb.insert(0, CQ_PROPERTY + Srl.initCap(foreignPropertyName) + ".");
            }
            query = query.xgetReferrerQuery();
        }
        return sb.toString();
    }

    @Override
    public ConditionValue invokeValue(String columnFlexibleName) {
        return null;
    }

    @Override
    public void invokeQuery(String columnFlexibleName, String conditionKeyName, Object conditionValue) {
        // nothing
    }

    @Override
    public void invokeQuery(String columnFlexibleName, String conditionKeyName, Object conditionValue, ConditionOption conditionOption) {
        // nothing
    }

    @Override
    public void invokeQueryEqual(String columnFlexibleName, Object conditionValue) {
        // nothing
    }

    @Override
    public void invokeQueryNotEqual(String columnFlexibleName, Object conditionValue) {
        // nothing
    }

    @Override
    public void invokeOrderBy(String columnFlexibleName, boolean isAsc) {
        // nothing
    }

    @Override
    public ConditionQuery invokeForeignCQ(String foreignPropertyName) {
        return null;
    }

    @Override
    public boolean invokeHasForeignCQ(String foreignPropertyName) {
        return false;
    }

    @Override
    public void xregisterParameterOption(ParameterOption option) {
        // nothing
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    protected String toRangeDateString(Date date, String format) {
        if (format.contains("epoch_millis")) {
            return Long.toString(date.getTime());
        } else if (format.contains("date_optional_time")) {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.format(date);
        } else {
            return Long.toString(date.getTime());
        }
    }

    protected String toRangeLocalDateTimeString(LocalDateTime date, String format) {
        if (format.contains("epoch_millis")) {
            return Long.toString(date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        } else if (format.contains("date_optional_time")) {
            return DateTimeFormatter.ISO_DATE_TIME.format(date);
        } else {
            return Long.toString(date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
    }

    // ===================================================================================
    //                                                                        Assist Class
    //                                                                        ============
    public class DocMetaCQ {

        public void setId_Equal(String id) {
            regQ(QueryBuilders.idsQuery().addIds(id));
        }
    }

    @FunctionalInterface
    public interface ConditionOptionCall<OP extends QueryBuilder> {

        /**
         * @param op The option of condition to be set up. (NotNull)
         */
        void callback(OP op);
    }

    @FunctionalInterface
    public interface BoolCall<CQ extends EsAbstractConditionQuery> {

        void callback(CQ must, CQ should, CQ mustNot, CQ filter);
    }

    @FunctionalInterface
    public interface FilteredCall<CQ extends EsAbstractConditionQuery, CF extends EsAbstractConditionQuery> {

        void callback(CQ query, CF filter);
    }

    @FunctionalInterface
    public interface OperatorCall<CQ extends EsAbstractConditionQuery> {

        void callback(CQ query);
    }

    @FunctionalInterface
    public interface ScoreFunctionCall<CC extends ScoreFunctionCreator<?>> {

        void callback(CC creator);
    }

    @FunctionalInterface
    public interface ScoreFunctionCreator<T extends EsAbstractConditionQuery> {
        void filter(final OperatorCall<T> cqLambda, final ScoreFunctionBuilder<?> scoreFunctionBuilder);
    }
}
