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
package org.codelibs.fess.es.log.cbean.cq.bs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.codelibs.fess.es.log.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.log.cbean.cq.ClickLogCQ;
import org.dbflute.cbean.ckey.ConditionKey;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.CommonTermsQueryBuilder;
import org.opensearch.index.query.ExistsQueryBuilder;
import org.opensearch.index.query.IdsQueryBuilder;
import org.opensearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.opensearch.index.query.MatchPhraseQueryBuilder;
import org.opensearch.index.query.MatchQueryBuilder;
import org.opensearch.index.query.PrefixQueryBuilder;
import org.opensearch.index.query.RangeQueryBuilder;
import org.opensearch.index.query.RegexpQueryBuilder;
import org.opensearch.index.query.SpanTermQueryBuilder;
import org.opensearch.index.query.TermQueryBuilder;
import org.opensearch.index.query.TermsQueryBuilder;
import org.opensearch.index.query.WildcardQueryBuilder;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsClickLogCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "click_log";
    }

    @Override
    public String xgetAliasName() {
        return "click_log";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<ClickLogCQ> queryLambda, ScoreFunctionCall<ScoreFunctionCreator<ClickLogCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        ClickLogCQ cq = new ClickLogCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                ClickLogCQ cf = new ClickLogCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<ClickLogCQ, ClickLogCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<ClickLogCQ, ClickLogCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<ClickLogCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<ClickLogCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<ClickLogCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<ClickLogCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        ClickLogCQ mustQuery = new ClickLogCQ();
        ClickLogCQ shouldQuery = new ClickLogCQ();
        ClickLogCQ mustNotQuery = new ClickLogCQ();
        ClickLogCQ filterQuery = new ClickLogCQ();
        boolLambda.callback(mustQuery, shouldQuery, mustNotQuery, filterQuery);
        if (mustQuery.hasQueries() || shouldQuery.hasQueries() || mustNotQuery.hasQueries() || filterQuery.hasQueries()) {
            BoolQueryBuilder builder = regBoolCQ(mustQuery.getQueryBuilderList(), shouldQuery.getQueryBuilderList(),
                    mustNotQuery.getQueryBuilderList(), filterQuery.getQueryBuilderList());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    // ===================================================================================
    //                                                                           Query Set
    //                                                                           =========
    public void setId_Equal(String id) {
        setId_Term(id, null);
    }

    public void setId_Equal(String id, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setId_Term(id, opLambda);
    }

    public void setId_Term(String id) {
        setId_Term(id, null);
    }

    public void setId_Term(String id, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("_id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_NotEqual(String id) {
        setId_NotTerm(id, null);
    }

    public void setId_NotTerm(String id) {
        setId_NotTerm(id, null);
    }

    public void setId_NotEqual(String id, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setId_NotTerm(id, opLambda);
    }

    public void setId_NotTerm(String id, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setId_Term(id), opLambda);
    }

    public void setId_Terms(Collection<String> idList) {
        setId_Terms(idList, null);
    }

    public void setId_Terms(Collection<String> idList, ConditionOptionCall<IdsQueryBuilder> opLambda) {
        IdsQueryBuilder builder = regIdsQ(idList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_InScope(Collection<String> idList) {
        setId_Terms(idList, null);
    }

    public void setId_InScope(Collection<String> idList, ConditionOptionCall<IdsQueryBuilder> opLambda) {
        setId_Terms(idList, opLambda);
    }

    @Deprecated
    public BsClickLogCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsClickLogCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setUrlId_Equal(String urlId) {
        setUrlId_Term(urlId, null);
    }

    public void setUrlId_Equal(String urlId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUrlId_Term(urlId, opLambda);
    }

    public void setUrlId_Term(String urlId) {
        setUrlId_Term(urlId, null);
    }

    public void setUrlId_Term(String urlId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("urlId", urlId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrlId_NotEqual(String urlId) {
        setUrlId_NotTerm(urlId, null);
    }

    public void setUrlId_NotTerm(String urlId) {
        setUrlId_NotTerm(urlId, null);
    }

    public void setUrlId_NotEqual(String urlId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setUrlId_NotTerm(urlId, opLambda);
    }

    public void setUrlId_NotTerm(String urlId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setUrlId_Term(urlId), opLambda);
    }

    public void setUrlId_Terms(Collection<String> urlIdList) {
        setUrlId_Terms(urlIdList, null);
    }

    public void setUrlId_Terms(Collection<String> urlIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("urlId", urlIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrlId_InScope(Collection<String> urlIdList) {
        setUrlId_Terms(urlIdList, null);
    }

    public void setUrlId_InScope(Collection<String> urlIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUrlId_Terms(urlIdList, opLambda);
    }

    public void setUrlId_Match(String urlId) {
        setUrlId_Match(urlId, null);
    }

    public void setUrlId_Match(String urlId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("urlId", urlId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrlId_MatchPhrase(String urlId) {
        setUrlId_MatchPhrase(urlId, null);
    }

    public void setUrlId_MatchPhrase(String urlId, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("urlId", urlId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrlId_MatchPhrasePrefix(String urlId) {
        setUrlId_MatchPhrasePrefix(urlId, null);
    }

    public void setUrlId_MatchPhrasePrefix(String urlId, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("urlId", urlId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrlId_Fuzzy(String urlId) {
        setUrlId_Fuzzy(urlId, null);
    }

    public void setUrlId_Fuzzy(String urlId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("urlId", urlId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrlId_Prefix(String urlId) {
        setUrlId_Prefix(urlId, null);
    }

    public void setUrlId_Prefix(String urlId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("urlId", urlId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrlId_Wildcard(String urlId) {
        setUrlId_Wildcard(urlId, null);
    }

    public void setUrlId_Wildcard(String urlId, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("urlId", urlId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrlId_Regexp(String urlId) {
        setUrlId_Regexp(urlId, null);
    }

    public void setUrlId_Regexp(String urlId, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("urlId", urlId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrlId_SpanTerm(String urlId) {
        setUrlId_SpanTerm("urlId", null);
    }

    public void setUrlId_SpanTerm(String urlId, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("urlId", urlId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrlId_GreaterThan(String urlId) {
        setUrlId_GreaterThan(urlId, null);
    }

    public void setUrlId_GreaterThan(String urlId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = urlId;
        RangeQueryBuilder builder = regRangeQ("urlId", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrlId_LessThan(String urlId) {
        setUrlId_LessThan(urlId, null);
    }

    public void setUrlId_LessThan(String urlId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = urlId;
        RangeQueryBuilder builder = regRangeQ("urlId", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrlId_GreaterEqual(String urlId) {
        setUrlId_GreaterEqual(urlId, null);
    }

    public void setUrlId_GreaterEqual(String urlId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = urlId;
        RangeQueryBuilder builder = regRangeQ("urlId", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrlId_LessEqual(String urlId) {
        setUrlId_LessEqual(urlId, null);
    }

    public void setUrlId_LessEqual(String urlId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = urlId;
        RangeQueryBuilder builder = regRangeQ("urlId", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrlId_Exists() {
        setUrlId_Exists(null);
    }

    public void setUrlId_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("urlId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setUrlId_CommonTerms(String urlId) {
        setUrlId_CommonTerms(urlId, null);
    }

    @Deprecated
    public void setUrlId_CommonTerms(String urlId, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("urlId", urlId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsClickLogCQ addOrderBy_UrlId_Asc() {
        regOBA("urlId");
        return this;
    }

    public BsClickLogCQ addOrderBy_UrlId_Desc() {
        regOBD("urlId");
        return this;
    }

    public void setDocId_Equal(String docId) {
        setDocId_Term(docId, null);
    }

    public void setDocId_Equal(String docId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setDocId_Term(docId, opLambda);
    }

    public void setDocId_Term(String docId) {
        setDocId_Term(docId, null);
    }

    public void setDocId_Term(String docId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_NotEqual(String docId) {
        setDocId_NotTerm(docId, null);
    }

    public void setDocId_NotTerm(String docId) {
        setDocId_NotTerm(docId, null);
    }

    public void setDocId_NotEqual(String docId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setDocId_NotTerm(docId, opLambda);
    }

    public void setDocId_NotTerm(String docId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setDocId_Term(docId), opLambda);
    }

    public void setDocId_Terms(Collection<String> docIdList) {
        setDocId_Terms(docIdList, null);
    }

    public void setDocId_Terms(Collection<String> docIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("docId", docIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_InScope(Collection<String> docIdList) {
        setDocId_Terms(docIdList, null);
    }

    public void setDocId_InScope(Collection<String> docIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setDocId_Terms(docIdList, opLambda);
    }

    public void setDocId_Match(String docId) {
        setDocId_Match(docId, null);
    }

    public void setDocId_Match(String docId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_MatchPhrase(String docId) {
        setDocId_MatchPhrase(docId, null);
    }

    public void setDocId_MatchPhrase(String docId, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_MatchPhrasePrefix(String docId) {
        setDocId_MatchPhrasePrefix(docId, null);
    }

    public void setDocId_MatchPhrasePrefix(String docId, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_Fuzzy(String docId) {
        setDocId_Fuzzy(docId, null);
    }

    public void setDocId_Fuzzy(String docId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_Prefix(String docId) {
        setDocId_Prefix(docId, null);
    }

    public void setDocId_Prefix(String docId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_Wildcard(String docId) {
        setDocId_Wildcard(docId, null);
    }

    public void setDocId_Wildcard(String docId, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_Regexp(String docId) {
        setDocId_Regexp(docId, null);
    }

    public void setDocId_Regexp(String docId, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_SpanTerm(String docId) {
        setDocId_SpanTerm("docId", null);
    }

    public void setDocId_SpanTerm(String docId, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_GreaterThan(String docId) {
        setDocId_GreaterThan(docId, null);
    }

    public void setDocId_GreaterThan(String docId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = docId;
        RangeQueryBuilder builder = regRangeQ("docId", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_LessThan(String docId) {
        setDocId_LessThan(docId, null);
    }

    public void setDocId_LessThan(String docId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = docId;
        RangeQueryBuilder builder = regRangeQ("docId", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_GreaterEqual(String docId) {
        setDocId_GreaterEqual(docId, null);
    }

    public void setDocId_GreaterEqual(String docId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = docId;
        RangeQueryBuilder builder = regRangeQ("docId", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_LessEqual(String docId) {
        setDocId_LessEqual(docId, null);
    }

    public void setDocId_LessEqual(String docId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = docId;
        RangeQueryBuilder builder = regRangeQ("docId", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_Exists() {
        setDocId_Exists(null);
    }

    public void setDocId_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setDocId_CommonTerms(String docId) {
        setDocId_CommonTerms(docId, null);
    }

    @Deprecated
    public void setDocId_CommonTerms(String docId, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsClickLogCQ addOrderBy_DocId_Asc() {
        regOBA("docId");
        return this;
    }

    public BsClickLogCQ addOrderBy_DocId_Desc() {
        regOBD("docId");
        return this;
    }

    public void setOrder_Equal(Integer order) {
        setOrder_Term(order, null);
    }

    public void setOrder_Equal(Integer order, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setOrder_Term(order, opLambda);
    }

    public void setOrder_Term(Integer order) {
        setOrder_Term(order, null);
    }

    public void setOrder_Term(Integer order, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("order", order);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_NotEqual(Integer order) {
        setOrder_NotTerm(order, null);
    }

    public void setOrder_NotTerm(Integer order) {
        setOrder_NotTerm(order, null);
    }

    public void setOrder_NotEqual(Integer order, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setOrder_NotTerm(order, opLambda);
    }

    public void setOrder_NotTerm(Integer order, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setOrder_Term(order), opLambda);
    }

    public void setOrder_Terms(Collection<Integer> orderList) {
        setOrder_Terms(orderList, null);
    }

    public void setOrder_Terms(Collection<Integer> orderList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("order", orderList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_InScope(Collection<Integer> orderList) {
        setOrder_Terms(orderList, null);
    }

    public void setOrder_InScope(Collection<Integer> orderList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setOrder_Terms(orderList, opLambda);
    }

    public void setOrder_Match(Integer order) {
        setOrder_Match(order, null);
    }

    public void setOrder_Match(Integer order, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("order", order);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_MatchPhrase(Integer order) {
        setOrder_MatchPhrase(order, null);
    }

    public void setOrder_MatchPhrase(Integer order, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("order", order);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_MatchPhrasePrefix(Integer order) {
        setOrder_MatchPhrasePrefix(order, null);
    }

    public void setOrder_MatchPhrasePrefix(Integer order, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("order", order);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Fuzzy(Integer order) {
        setOrder_Fuzzy(order, null);
    }

    public void setOrder_Fuzzy(Integer order, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("order", order);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_GreaterThan(Integer order) {
        setOrder_GreaterThan(order, null);
    }

    public void setOrder_GreaterThan(Integer order, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = order;
        RangeQueryBuilder builder = regRangeQ("order", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_LessThan(Integer order) {
        setOrder_LessThan(order, null);
    }

    public void setOrder_LessThan(Integer order, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = order;
        RangeQueryBuilder builder = regRangeQ("order", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_GreaterEqual(Integer order) {
        setOrder_GreaterEqual(order, null);
    }

    public void setOrder_GreaterEqual(Integer order, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = order;
        RangeQueryBuilder builder = regRangeQ("order", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_LessEqual(Integer order) {
        setOrder_LessEqual(order, null);
    }

    public void setOrder_LessEqual(Integer order, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = order;
        RangeQueryBuilder builder = regRangeQ("order", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Exists() {
        setOrder_Exists(null);
    }

    public void setOrder_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setOrder_CommonTerms(Integer order) {
        setOrder_CommonTerms(order, null);
    }

    @Deprecated
    public void setOrder_CommonTerms(Integer order, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("order", order);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsClickLogCQ addOrderBy_Order_Asc() {
        regOBA("order");
        return this;
    }

    public BsClickLogCQ addOrderBy_Order_Desc() {
        regOBD("order");
        return this;
    }

    public void setQueryId_Equal(String queryId) {
        setQueryId_Term(queryId, null);
    }

    public void setQueryId_Equal(String queryId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setQueryId_Term(queryId, opLambda);
    }

    public void setQueryId_Term(String queryId) {
        setQueryId_Term(queryId, null);
    }

    public void setQueryId_Term(String queryId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("queryId", queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_NotEqual(String queryId) {
        setQueryId_NotTerm(queryId, null);
    }

    public void setQueryId_NotTerm(String queryId) {
        setQueryId_NotTerm(queryId, null);
    }

    public void setQueryId_NotEqual(String queryId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setQueryId_NotTerm(queryId, opLambda);
    }

    public void setQueryId_NotTerm(String queryId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setQueryId_Term(queryId), opLambda);
    }

    public void setQueryId_Terms(Collection<String> queryIdList) {
        setQueryId_Terms(queryIdList, null);
    }

    public void setQueryId_Terms(Collection<String> queryIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("queryId", queryIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_InScope(Collection<String> queryIdList) {
        setQueryId_Terms(queryIdList, null);
    }

    public void setQueryId_InScope(Collection<String> queryIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setQueryId_Terms(queryIdList, opLambda);
    }

    public void setQueryId_Match(String queryId) {
        setQueryId_Match(queryId, null);
    }

    public void setQueryId_Match(String queryId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("queryId", queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_MatchPhrase(String queryId) {
        setQueryId_MatchPhrase(queryId, null);
    }

    public void setQueryId_MatchPhrase(String queryId, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("queryId", queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_MatchPhrasePrefix(String queryId) {
        setQueryId_MatchPhrasePrefix(queryId, null);
    }

    public void setQueryId_MatchPhrasePrefix(String queryId, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("queryId", queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_Fuzzy(String queryId) {
        setQueryId_Fuzzy(queryId, null);
    }

    public void setQueryId_Fuzzy(String queryId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("queryId", queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_Prefix(String queryId) {
        setQueryId_Prefix(queryId, null);
    }

    public void setQueryId_Prefix(String queryId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("queryId", queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_Wildcard(String queryId) {
        setQueryId_Wildcard(queryId, null);
    }

    public void setQueryId_Wildcard(String queryId, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("queryId", queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_Regexp(String queryId) {
        setQueryId_Regexp(queryId, null);
    }

    public void setQueryId_Regexp(String queryId, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("queryId", queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_SpanTerm(String queryId) {
        setQueryId_SpanTerm("queryId", null);
    }

    public void setQueryId_SpanTerm(String queryId, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("queryId", queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_GreaterThan(String queryId) {
        setQueryId_GreaterThan(queryId, null);
    }

    public void setQueryId_GreaterThan(String queryId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = queryId;
        RangeQueryBuilder builder = regRangeQ("queryId", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_LessThan(String queryId) {
        setQueryId_LessThan(queryId, null);
    }

    public void setQueryId_LessThan(String queryId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = queryId;
        RangeQueryBuilder builder = regRangeQ("queryId", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_GreaterEqual(String queryId) {
        setQueryId_GreaterEqual(queryId, null);
    }

    public void setQueryId_GreaterEqual(String queryId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = queryId;
        RangeQueryBuilder builder = regRangeQ("queryId", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_LessEqual(String queryId) {
        setQueryId_LessEqual(queryId, null);
    }

    public void setQueryId_LessEqual(String queryId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = queryId;
        RangeQueryBuilder builder = regRangeQ("queryId", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_Exists() {
        setQueryId_Exists(null);
    }

    public void setQueryId_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setQueryId_CommonTerms(String queryId) {
        setQueryId_CommonTerms(queryId, null);
    }

    @Deprecated
    public void setQueryId_CommonTerms(String queryId, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("queryId", queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsClickLogCQ addOrderBy_QueryId_Asc() {
        regOBA("queryId");
        return this;
    }

    public BsClickLogCQ addOrderBy_QueryId_Desc() {
        regOBD("queryId");
        return this;
    }

    public void setQueryRequestedAt_Equal(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_Term(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_Equal(LocalDateTime queryRequestedAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setQueryRequestedAt_Term(queryRequestedAt, opLambda);
    }

    public void setQueryRequestedAt_Term(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_Term(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_Term(LocalDateTime queryRequestedAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("queryRequestedAt", queryRequestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_NotEqual(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_NotTerm(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_NotTerm(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_NotTerm(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_NotEqual(LocalDateTime queryRequestedAt, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setQueryRequestedAt_NotTerm(queryRequestedAt, opLambda);
    }

    public void setQueryRequestedAt_NotTerm(LocalDateTime queryRequestedAt, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setQueryRequestedAt_Term(queryRequestedAt), opLambda);
    }

    public void setQueryRequestedAt_Terms(Collection<LocalDateTime> queryRequestedAtList) {
        setQueryRequestedAt_Terms(queryRequestedAtList, null);
    }

    public void setQueryRequestedAt_Terms(Collection<LocalDateTime> queryRequestedAtList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("queryRequestedAt", queryRequestedAtList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_InScope(Collection<LocalDateTime> queryRequestedAtList) {
        setQueryRequestedAt_Terms(queryRequestedAtList, null);
    }

    public void setQueryRequestedAt_InScope(Collection<LocalDateTime> queryRequestedAtList,
            ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setQueryRequestedAt_Terms(queryRequestedAtList, opLambda);
    }

    public void setQueryRequestedAt_Match(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_Match(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_Match(LocalDateTime queryRequestedAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("queryRequestedAt", queryRequestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_MatchPhrase(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_MatchPhrase(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_MatchPhrase(LocalDateTime queryRequestedAt, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("queryRequestedAt", queryRequestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_MatchPhrasePrefix(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_MatchPhrasePrefix(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_MatchPhrasePrefix(LocalDateTime queryRequestedAt,
            ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("queryRequestedAt", queryRequestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_Fuzzy(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_Fuzzy(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_Fuzzy(LocalDateTime queryRequestedAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("queryRequestedAt", queryRequestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_GreaterThan(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_GreaterThan(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_GreaterThan(LocalDateTime queryRequestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(queryRequestedAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("queryRequestedAt", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_LessThan(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_LessThan(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_LessThan(LocalDateTime queryRequestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(queryRequestedAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("queryRequestedAt", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_GreaterEqual(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_GreaterEqual(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_GreaterEqual(LocalDateTime queryRequestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(queryRequestedAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("queryRequestedAt", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_LessEqual(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_LessEqual(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_LessEqual(LocalDateTime queryRequestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(queryRequestedAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("queryRequestedAt", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_Exists() {
        setQueryRequestedAt_Exists(null);
    }

    public void setQueryRequestedAt_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("queryRequestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setQueryRequestedAt_CommonTerms(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_CommonTerms(queryRequestedAt, null);
    }

    @Deprecated
    public void setQueryRequestedAt_CommonTerms(LocalDateTime queryRequestedAt, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("queryRequestedAt", queryRequestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsClickLogCQ addOrderBy_QueryRequestedAt_Asc() {
        regOBA("queryRequestedAt");
        return this;
    }

    public BsClickLogCQ addOrderBy_QueryRequestedAt_Desc() {
        regOBD("queryRequestedAt");
        return this;
    }

    public void setRequestedAt_Equal(LocalDateTime requestedAt) {
        setRequestedAt_Term(requestedAt, null);
    }

    public void setRequestedAt_Equal(LocalDateTime requestedAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setRequestedAt_Term(requestedAt, opLambda);
    }

    public void setRequestedAt_Term(LocalDateTime requestedAt) {
        setRequestedAt_Term(requestedAt, null);
    }

    public void setRequestedAt_Term(LocalDateTime requestedAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("requestedAt", requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_NotEqual(LocalDateTime requestedAt) {
        setRequestedAt_NotTerm(requestedAt, null);
    }

    public void setRequestedAt_NotTerm(LocalDateTime requestedAt) {
        setRequestedAt_NotTerm(requestedAt, null);
    }

    public void setRequestedAt_NotEqual(LocalDateTime requestedAt, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setRequestedAt_NotTerm(requestedAt, opLambda);
    }

    public void setRequestedAt_NotTerm(LocalDateTime requestedAt, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setRequestedAt_Term(requestedAt), opLambda);
    }

    public void setRequestedAt_Terms(Collection<LocalDateTime> requestedAtList) {
        setRequestedAt_Terms(requestedAtList, null);
    }

    public void setRequestedAt_Terms(Collection<LocalDateTime> requestedAtList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("requestedAt", requestedAtList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_InScope(Collection<LocalDateTime> requestedAtList) {
        setRequestedAt_Terms(requestedAtList, null);
    }

    public void setRequestedAt_InScope(Collection<LocalDateTime> requestedAtList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setRequestedAt_Terms(requestedAtList, opLambda);
    }

    public void setRequestedAt_Match(LocalDateTime requestedAt) {
        setRequestedAt_Match(requestedAt, null);
    }

    public void setRequestedAt_Match(LocalDateTime requestedAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("requestedAt", requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_MatchPhrase(LocalDateTime requestedAt) {
        setRequestedAt_MatchPhrase(requestedAt, null);
    }

    public void setRequestedAt_MatchPhrase(LocalDateTime requestedAt, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("requestedAt", requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_MatchPhrasePrefix(LocalDateTime requestedAt) {
        setRequestedAt_MatchPhrasePrefix(requestedAt, null);
    }

    public void setRequestedAt_MatchPhrasePrefix(LocalDateTime requestedAt, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("requestedAt", requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_Fuzzy(LocalDateTime requestedAt) {
        setRequestedAt_Fuzzy(requestedAt, null);
    }

    public void setRequestedAt_Fuzzy(LocalDateTime requestedAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("requestedAt", requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_GreaterThan(LocalDateTime requestedAt) {
        setRequestedAt_GreaterThan(requestedAt, null);
    }

    public void setRequestedAt_GreaterThan(LocalDateTime requestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(requestedAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("requestedAt", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_LessThan(LocalDateTime requestedAt) {
        setRequestedAt_LessThan(requestedAt, null);
    }

    public void setRequestedAt_LessThan(LocalDateTime requestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(requestedAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("requestedAt", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_GreaterEqual(LocalDateTime requestedAt) {
        setRequestedAt_GreaterEqual(requestedAt, null);
    }

    public void setRequestedAt_GreaterEqual(LocalDateTime requestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(requestedAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("requestedAt", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_LessEqual(LocalDateTime requestedAt) {
        setRequestedAt_LessEqual(requestedAt, null);
    }

    public void setRequestedAt_LessEqual(LocalDateTime requestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(requestedAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("requestedAt", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_Exists() {
        setRequestedAt_Exists(null);
    }

    public void setRequestedAt_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("requestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setRequestedAt_CommonTerms(LocalDateTime requestedAt) {
        setRequestedAt_CommonTerms(requestedAt, null);
    }

    @Deprecated
    public void setRequestedAt_CommonTerms(LocalDateTime requestedAt, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("requestedAt", requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsClickLogCQ addOrderBy_RequestedAt_Asc() {
        regOBA("requestedAt");
        return this;
    }

    public BsClickLogCQ addOrderBy_RequestedAt_Desc() {
        regOBD("requestedAt");
        return this;
    }

    public void setUrl_Equal(String url) {
        setUrl_Term(url, null);
    }

    public void setUrl_Equal(String url, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUrl_Term(url, opLambda);
    }

    public void setUrl_Term(String url) {
        setUrl_Term(url, null);
    }

    public void setUrl_Term(String url, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_NotEqual(String url) {
        setUrl_NotTerm(url, null);
    }

    public void setUrl_NotTerm(String url) {
        setUrl_NotTerm(url, null);
    }

    public void setUrl_NotEqual(String url, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setUrl_NotTerm(url, opLambda);
    }

    public void setUrl_NotTerm(String url, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setUrl_Term(url), opLambda);
    }

    public void setUrl_Terms(Collection<String> urlList) {
        setUrl_Terms(urlList, null);
    }

    public void setUrl_Terms(Collection<String> urlList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("url", urlList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_InScope(Collection<String> urlList) {
        setUrl_Terms(urlList, null);
    }

    public void setUrl_InScope(Collection<String> urlList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUrl_Terms(urlList, opLambda);
    }

    public void setUrl_Match(String url) {
        setUrl_Match(url, null);
    }

    public void setUrl_Match(String url, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_MatchPhrase(String url) {
        setUrl_MatchPhrase(url, null);
    }

    public void setUrl_MatchPhrase(String url, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_MatchPhrasePrefix(String url) {
        setUrl_MatchPhrasePrefix(url, null);
    }

    public void setUrl_MatchPhrasePrefix(String url, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Fuzzy(String url) {
        setUrl_Fuzzy(url, null);
    }

    public void setUrl_Fuzzy(String url, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Prefix(String url) {
        setUrl_Prefix(url, null);
    }

    public void setUrl_Prefix(String url, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Wildcard(String url) {
        setUrl_Wildcard(url, null);
    }

    public void setUrl_Wildcard(String url, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Regexp(String url) {
        setUrl_Regexp(url, null);
    }

    public void setUrl_Regexp(String url, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_SpanTerm(String url) {
        setUrl_SpanTerm("url", null);
    }

    public void setUrl_SpanTerm(String url, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_GreaterThan(String url) {
        setUrl_GreaterThan(url, null);
    }

    public void setUrl_GreaterThan(String url, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = url;
        RangeQueryBuilder builder = regRangeQ("url", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_LessThan(String url) {
        setUrl_LessThan(url, null);
    }

    public void setUrl_LessThan(String url, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = url;
        RangeQueryBuilder builder = regRangeQ("url", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_GreaterEqual(String url) {
        setUrl_GreaterEqual(url, null);
    }

    public void setUrl_GreaterEqual(String url, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = url;
        RangeQueryBuilder builder = regRangeQ("url", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_LessEqual(String url) {
        setUrl_LessEqual(url, null);
    }

    public void setUrl_LessEqual(String url, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = url;
        RangeQueryBuilder builder = regRangeQ("url", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Exists() {
        setUrl_Exists(null);
    }

    public void setUrl_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setUrl_CommonTerms(String url) {
        setUrl_CommonTerms(url, null);
    }

    @Deprecated
    public void setUrl_CommonTerms(String url, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsClickLogCQ addOrderBy_Url_Asc() {
        regOBA("url");
        return this;
    }

    public BsClickLogCQ addOrderBy_Url_Desc() {
        regOBD("url");
        return this;
    }

    public void setUserSessionId_Equal(String userSessionId) {
        setUserSessionId_Term(userSessionId, null);
    }

    public void setUserSessionId_Equal(String userSessionId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUserSessionId_Term(userSessionId, opLambda);
    }

    public void setUserSessionId_Term(String userSessionId) {
        setUserSessionId_Term(userSessionId, null);
    }

    public void setUserSessionId_Term(String userSessionId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_NotEqual(String userSessionId) {
        setUserSessionId_NotTerm(userSessionId, null);
    }

    public void setUserSessionId_NotTerm(String userSessionId) {
        setUserSessionId_NotTerm(userSessionId, null);
    }

    public void setUserSessionId_NotEqual(String userSessionId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setUserSessionId_NotTerm(userSessionId, opLambda);
    }

    public void setUserSessionId_NotTerm(String userSessionId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setUserSessionId_Term(userSessionId), opLambda);
    }

    public void setUserSessionId_Terms(Collection<String> userSessionIdList) {
        setUserSessionId_Terms(userSessionIdList, null);
    }

    public void setUserSessionId_Terms(Collection<String> userSessionIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("userSessionId", userSessionIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_InScope(Collection<String> userSessionIdList) {
        setUserSessionId_Terms(userSessionIdList, null);
    }

    public void setUserSessionId_InScope(Collection<String> userSessionIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUserSessionId_Terms(userSessionIdList, opLambda);
    }

    public void setUserSessionId_Match(String userSessionId) {
        setUserSessionId_Match(userSessionId, null);
    }

    public void setUserSessionId_Match(String userSessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_MatchPhrase(String userSessionId) {
        setUserSessionId_MatchPhrase(userSessionId, null);
    }

    public void setUserSessionId_MatchPhrase(String userSessionId, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_MatchPhrasePrefix(String userSessionId) {
        setUserSessionId_MatchPhrasePrefix(userSessionId, null);
    }

    public void setUserSessionId_MatchPhrasePrefix(String userSessionId, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Fuzzy(String userSessionId) {
        setUserSessionId_Fuzzy(userSessionId, null);
    }

    public void setUserSessionId_Fuzzy(String userSessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Prefix(String userSessionId) {
        setUserSessionId_Prefix(userSessionId, null);
    }

    public void setUserSessionId_Prefix(String userSessionId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Wildcard(String userSessionId) {
        setUserSessionId_Wildcard(userSessionId, null);
    }

    public void setUserSessionId_Wildcard(String userSessionId, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Regexp(String userSessionId) {
        setUserSessionId_Regexp(userSessionId, null);
    }

    public void setUserSessionId_Regexp(String userSessionId, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_SpanTerm(String userSessionId) {
        setUserSessionId_SpanTerm("userSessionId", null);
    }

    public void setUserSessionId_SpanTerm(String userSessionId, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_GreaterThan(String userSessionId) {
        setUserSessionId_GreaterThan(userSessionId, null);
    }

    public void setUserSessionId_GreaterThan(String userSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userSessionId;
        RangeQueryBuilder builder = regRangeQ("userSessionId", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_LessThan(String userSessionId) {
        setUserSessionId_LessThan(userSessionId, null);
    }

    public void setUserSessionId_LessThan(String userSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userSessionId;
        RangeQueryBuilder builder = regRangeQ("userSessionId", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_GreaterEqual(String userSessionId) {
        setUserSessionId_GreaterEqual(userSessionId, null);
    }

    public void setUserSessionId_GreaterEqual(String userSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userSessionId;
        RangeQueryBuilder builder = regRangeQ("userSessionId", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_LessEqual(String userSessionId) {
        setUserSessionId_LessEqual(userSessionId, null);
    }

    public void setUserSessionId_LessEqual(String userSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userSessionId;
        RangeQueryBuilder builder = regRangeQ("userSessionId", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Exists() {
        setUserSessionId_Exists(null);
    }

    public void setUserSessionId_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setUserSessionId_CommonTerms(String userSessionId) {
        setUserSessionId_CommonTerms(userSessionId, null);
    }

    @Deprecated
    public void setUserSessionId_CommonTerms(String userSessionId, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsClickLogCQ addOrderBy_UserSessionId_Asc() {
        regOBA("userSessionId");
        return this;
    }

    public BsClickLogCQ addOrderBy_UserSessionId_Desc() {
        regOBD("userSessionId");
        return this;
    }

}
