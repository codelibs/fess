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
package org.codelibs.fess.es.config.cbean.cq.bs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.codelibs.fess.es.config.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.config.cbean.cq.KeyMatchCQ;
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
public abstract class BsKeyMatchCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "key_match";
    }

    @Override
    public String xgetAliasName() {
        return "key_match";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<KeyMatchCQ> queryLambda, ScoreFunctionCall<ScoreFunctionCreator<KeyMatchCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        KeyMatchCQ cq = new KeyMatchCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                KeyMatchCQ cf = new KeyMatchCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<KeyMatchCQ, KeyMatchCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<KeyMatchCQ, KeyMatchCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<KeyMatchCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<KeyMatchCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<KeyMatchCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<KeyMatchCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        KeyMatchCQ mustQuery = new KeyMatchCQ();
        KeyMatchCQ shouldQuery = new KeyMatchCQ();
        KeyMatchCQ mustNotQuery = new KeyMatchCQ();
        KeyMatchCQ filterQuery = new KeyMatchCQ();
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
    public BsKeyMatchCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsKeyMatchCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setBoost_Equal(Float boost) {
        setBoost_Term(boost, null);
    }

    public void setBoost_Equal(Float boost, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setBoost_Term(boost, opLambda);
    }

    public void setBoost_Term(Float boost) {
        setBoost_Term(boost, null);
    }

    public void setBoost_Term(Float boost, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("boost", boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_NotEqual(Float boost) {
        setBoost_NotTerm(boost, null);
    }

    public void setBoost_NotTerm(Float boost) {
        setBoost_NotTerm(boost, null);
    }

    public void setBoost_NotEqual(Float boost, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setBoost_NotTerm(boost, opLambda);
    }

    public void setBoost_NotTerm(Float boost, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setBoost_Term(boost), opLambda);
    }

    public void setBoost_Terms(Collection<Float> boostList) {
        setBoost_Terms(boostList, null);
    }

    public void setBoost_Terms(Collection<Float> boostList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("boost", boostList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_InScope(Collection<Float> boostList) {
        setBoost_Terms(boostList, null);
    }

    public void setBoost_InScope(Collection<Float> boostList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setBoost_Terms(boostList, opLambda);
    }

    public void setBoost_Match(Float boost) {
        setBoost_Match(boost, null);
    }

    public void setBoost_Match(Float boost, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("boost", boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_MatchPhrase(Float boost) {
        setBoost_MatchPhrase(boost, null);
    }

    public void setBoost_MatchPhrase(Float boost, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("boost", boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_MatchPhrasePrefix(Float boost) {
        setBoost_MatchPhrasePrefix(boost, null);
    }

    public void setBoost_MatchPhrasePrefix(Float boost, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("boost", boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Fuzzy(Float boost) {
        setBoost_Fuzzy(boost, null);
    }

    public void setBoost_Fuzzy(Float boost, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("boost", boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_GreaterThan(Float boost) {
        setBoost_GreaterThan(boost, null);
    }

    public void setBoost_GreaterThan(Float boost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = boost;
        RangeQueryBuilder builder = regRangeQ("boost", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_LessThan(Float boost) {
        setBoost_LessThan(boost, null);
    }

    public void setBoost_LessThan(Float boost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = boost;
        RangeQueryBuilder builder = regRangeQ("boost", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_GreaterEqual(Float boost) {
        setBoost_GreaterEqual(boost, null);
    }

    public void setBoost_GreaterEqual(Float boost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = boost;
        RangeQueryBuilder builder = regRangeQ("boost", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_LessEqual(Float boost) {
        setBoost_LessEqual(boost, null);
    }

    public void setBoost_LessEqual(Float boost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = boost;
        RangeQueryBuilder builder = regRangeQ("boost", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Exists() {
        setBoost_Exists(null);
    }

    public void setBoost_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setBoost_CommonTerms(Float boost) {
        setBoost_CommonTerms(boost, null);
    }

    @Deprecated
    public void setBoost_CommonTerms(Float boost, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("boost", boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsKeyMatchCQ addOrderBy_Boost_Asc() {
        regOBA("boost");
        return this;
    }

    public BsKeyMatchCQ addOrderBy_Boost_Desc() {
        regOBD("boost");
        return this;
    }

    public void setCreatedBy_Equal(String createdBy) {
        setCreatedBy_Term(createdBy, null);
    }

    public void setCreatedBy_Equal(String createdBy, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setCreatedBy_Term(createdBy, opLambda);
    }

    public void setCreatedBy_Term(String createdBy) {
        setCreatedBy_Term(createdBy, null);
    }

    public void setCreatedBy_Term(String createdBy, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_NotEqual(String createdBy) {
        setCreatedBy_NotTerm(createdBy, null);
    }

    public void setCreatedBy_NotTerm(String createdBy) {
        setCreatedBy_NotTerm(createdBy, null);
    }

    public void setCreatedBy_NotEqual(String createdBy, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setCreatedBy_NotTerm(createdBy, opLambda);
    }

    public void setCreatedBy_NotTerm(String createdBy, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setCreatedBy_Term(createdBy), opLambda);
    }

    public void setCreatedBy_Terms(Collection<String> createdByList) {
        setCreatedBy_Terms(createdByList, null);
    }

    public void setCreatedBy_Terms(Collection<String> createdByList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("createdBy", createdByList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_InScope(Collection<String> createdByList) {
        setCreatedBy_Terms(createdByList, null);
    }

    public void setCreatedBy_InScope(Collection<String> createdByList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setCreatedBy_Terms(createdByList, opLambda);
    }

    public void setCreatedBy_Match(String createdBy) {
        setCreatedBy_Match(createdBy, null);
    }

    public void setCreatedBy_Match(String createdBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_MatchPhrase(String createdBy) {
        setCreatedBy_MatchPhrase(createdBy, null);
    }

    public void setCreatedBy_MatchPhrase(String createdBy, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_MatchPhrasePrefix(String createdBy) {
        setCreatedBy_MatchPhrasePrefix(createdBy, null);
    }

    public void setCreatedBy_MatchPhrasePrefix(String createdBy, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Fuzzy(String createdBy) {
        setCreatedBy_Fuzzy(createdBy, null);
    }

    public void setCreatedBy_Fuzzy(String createdBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Prefix(String createdBy) {
        setCreatedBy_Prefix(createdBy, null);
    }

    public void setCreatedBy_Prefix(String createdBy, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Wildcard(String createdBy) {
        setCreatedBy_Wildcard(createdBy, null);
    }

    public void setCreatedBy_Wildcard(String createdBy, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Regexp(String createdBy) {
        setCreatedBy_Regexp(createdBy, null);
    }

    public void setCreatedBy_Regexp(String createdBy, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_SpanTerm(String createdBy) {
        setCreatedBy_SpanTerm("createdBy", null);
    }

    public void setCreatedBy_SpanTerm(String createdBy, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_GreaterThan(String createdBy) {
        setCreatedBy_GreaterThan(createdBy, null);
    }

    public void setCreatedBy_GreaterThan(String createdBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = createdBy;
        RangeQueryBuilder builder = regRangeQ("createdBy", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_LessThan(String createdBy) {
        setCreatedBy_LessThan(createdBy, null);
    }

    public void setCreatedBy_LessThan(String createdBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = createdBy;
        RangeQueryBuilder builder = regRangeQ("createdBy", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_GreaterEqual(String createdBy) {
        setCreatedBy_GreaterEqual(createdBy, null);
    }

    public void setCreatedBy_GreaterEqual(String createdBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = createdBy;
        RangeQueryBuilder builder = regRangeQ("createdBy", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_LessEqual(String createdBy) {
        setCreatedBy_LessEqual(createdBy, null);
    }

    public void setCreatedBy_LessEqual(String createdBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = createdBy;
        RangeQueryBuilder builder = regRangeQ("createdBy", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Exists() {
        setCreatedBy_Exists(null);
    }

    public void setCreatedBy_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setCreatedBy_CommonTerms(String createdBy) {
        setCreatedBy_CommonTerms(createdBy, null);
    }

    @Deprecated
    public void setCreatedBy_CommonTerms(String createdBy, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsKeyMatchCQ addOrderBy_CreatedBy_Asc() {
        regOBA("createdBy");
        return this;
    }

    public BsKeyMatchCQ addOrderBy_CreatedBy_Desc() {
        regOBD("createdBy");
        return this;
    }

    public void setCreatedTime_Equal(Long createdTime) {
        setCreatedTime_Term(createdTime, null);
    }

    public void setCreatedTime_Equal(Long createdTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setCreatedTime_Term(createdTime, opLambda);
    }

    public void setCreatedTime_Term(Long createdTime) {
        setCreatedTime_Term(createdTime, null);
    }

    public void setCreatedTime_Term(Long createdTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_NotEqual(Long createdTime) {
        setCreatedTime_NotTerm(createdTime, null);
    }

    public void setCreatedTime_NotTerm(Long createdTime) {
        setCreatedTime_NotTerm(createdTime, null);
    }

    public void setCreatedTime_NotEqual(Long createdTime, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setCreatedTime_NotTerm(createdTime, opLambda);
    }

    public void setCreatedTime_NotTerm(Long createdTime, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setCreatedTime_Term(createdTime), opLambda);
    }

    public void setCreatedTime_Terms(Collection<Long> createdTimeList) {
        setCreatedTime_Terms(createdTimeList, null);
    }

    public void setCreatedTime_Terms(Collection<Long> createdTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("createdTime", createdTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_InScope(Collection<Long> createdTimeList) {
        setCreatedTime_Terms(createdTimeList, null);
    }

    public void setCreatedTime_InScope(Collection<Long> createdTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setCreatedTime_Terms(createdTimeList, opLambda);
    }

    public void setCreatedTime_Match(Long createdTime) {
        setCreatedTime_Match(createdTime, null);
    }

    public void setCreatedTime_Match(Long createdTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_MatchPhrase(Long createdTime) {
        setCreatedTime_MatchPhrase(createdTime, null);
    }

    public void setCreatedTime_MatchPhrase(Long createdTime, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_MatchPhrasePrefix(Long createdTime) {
        setCreatedTime_MatchPhrasePrefix(createdTime, null);
    }

    public void setCreatedTime_MatchPhrasePrefix(Long createdTime, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Fuzzy(Long createdTime) {
        setCreatedTime_Fuzzy(createdTime, null);
    }

    public void setCreatedTime_Fuzzy(Long createdTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_GreaterThan(Long createdTime) {
        setCreatedTime_GreaterThan(createdTime, null);
    }

    public void setCreatedTime_GreaterThan(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = createdTime;
        RangeQueryBuilder builder = regRangeQ("createdTime", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_LessThan(Long createdTime) {
        setCreatedTime_LessThan(createdTime, null);
    }

    public void setCreatedTime_LessThan(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = createdTime;
        RangeQueryBuilder builder = regRangeQ("createdTime", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_GreaterEqual(Long createdTime) {
        setCreatedTime_GreaterEqual(createdTime, null);
    }

    public void setCreatedTime_GreaterEqual(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = createdTime;
        RangeQueryBuilder builder = regRangeQ("createdTime", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_LessEqual(Long createdTime) {
        setCreatedTime_LessEqual(createdTime, null);
    }

    public void setCreatedTime_LessEqual(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = createdTime;
        RangeQueryBuilder builder = regRangeQ("createdTime", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Exists() {
        setCreatedTime_Exists(null);
    }

    public void setCreatedTime_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setCreatedTime_CommonTerms(Long createdTime) {
        setCreatedTime_CommonTerms(createdTime, null);
    }

    @Deprecated
    public void setCreatedTime_CommonTerms(Long createdTime, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsKeyMatchCQ addOrderBy_CreatedTime_Asc() {
        regOBA("createdTime");
        return this;
    }

    public BsKeyMatchCQ addOrderBy_CreatedTime_Desc() {
        regOBD("createdTime");
        return this;
    }

    public void setMaxSize_Equal(Integer maxSize) {
        setMaxSize_Term(maxSize, null);
    }

    public void setMaxSize_Equal(Integer maxSize, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setMaxSize_Term(maxSize, opLambda);
    }

    public void setMaxSize_Term(Integer maxSize) {
        setMaxSize_Term(maxSize, null);
    }

    public void setMaxSize_Term(Integer maxSize, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("maxSize", maxSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_NotEqual(Integer maxSize) {
        setMaxSize_NotTerm(maxSize, null);
    }

    public void setMaxSize_NotTerm(Integer maxSize) {
        setMaxSize_NotTerm(maxSize, null);
    }

    public void setMaxSize_NotEqual(Integer maxSize, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setMaxSize_NotTerm(maxSize, opLambda);
    }

    public void setMaxSize_NotTerm(Integer maxSize, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setMaxSize_Term(maxSize), opLambda);
    }

    public void setMaxSize_Terms(Collection<Integer> maxSizeList) {
        setMaxSize_Terms(maxSizeList, null);
    }

    public void setMaxSize_Terms(Collection<Integer> maxSizeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("maxSize", maxSizeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_InScope(Collection<Integer> maxSizeList) {
        setMaxSize_Terms(maxSizeList, null);
    }

    public void setMaxSize_InScope(Collection<Integer> maxSizeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setMaxSize_Terms(maxSizeList, opLambda);
    }

    public void setMaxSize_Match(Integer maxSize) {
        setMaxSize_Match(maxSize, null);
    }

    public void setMaxSize_Match(Integer maxSize, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("maxSize", maxSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_MatchPhrase(Integer maxSize) {
        setMaxSize_MatchPhrase(maxSize, null);
    }

    public void setMaxSize_MatchPhrase(Integer maxSize, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("maxSize", maxSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_MatchPhrasePrefix(Integer maxSize) {
        setMaxSize_MatchPhrasePrefix(maxSize, null);
    }

    public void setMaxSize_MatchPhrasePrefix(Integer maxSize, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("maxSize", maxSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_Fuzzy(Integer maxSize) {
        setMaxSize_Fuzzy(maxSize, null);
    }

    public void setMaxSize_Fuzzy(Integer maxSize, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("maxSize", maxSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_GreaterThan(Integer maxSize) {
        setMaxSize_GreaterThan(maxSize, null);
    }

    public void setMaxSize_GreaterThan(Integer maxSize, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = maxSize;
        RangeQueryBuilder builder = regRangeQ("maxSize", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_LessThan(Integer maxSize) {
        setMaxSize_LessThan(maxSize, null);
    }

    public void setMaxSize_LessThan(Integer maxSize, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = maxSize;
        RangeQueryBuilder builder = regRangeQ("maxSize", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_GreaterEqual(Integer maxSize) {
        setMaxSize_GreaterEqual(maxSize, null);
    }

    public void setMaxSize_GreaterEqual(Integer maxSize, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = maxSize;
        RangeQueryBuilder builder = regRangeQ("maxSize", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_LessEqual(Integer maxSize) {
        setMaxSize_LessEqual(maxSize, null);
    }

    public void setMaxSize_LessEqual(Integer maxSize, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = maxSize;
        RangeQueryBuilder builder = regRangeQ("maxSize", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_Exists() {
        setMaxSize_Exists(null);
    }

    public void setMaxSize_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("maxSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setMaxSize_CommonTerms(Integer maxSize) {
        setMaxSize_CommonTerms(maxSize, null);
    }

    @Deprecated
    public void setMaxSize_CommonTerms(Integer maxSize, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("maxSize", maxSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsKeyMatchCQ addOrderBy_MaxSize_Asc() {
        regOBA("maxSize");
        return this;
    }

    public BsKeyMatchCQ addOrderBy_MaxSize_Desc() {
        regOBD("maxSize");
        return this;
    }

    public void setQuery_Equal(String query) {
        setQuery_Term(query, null);
    }

    public void setQuery_Equal(String query, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setQuery_Term(query, opLambda);
    }

    public void setQuery_Term(String query) {
        setQuery_Term(query, null);
    }

    public void setQuery_Term(String query, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("query", query);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQuery_NotEqual(String query) {
        setQuery_NotTerm(query, null);
    }

    public void setQuery_NotTerm(String query) {
        setQuery_NotTerm(query, null);
    }

    public void setQuery_NotEqual(String query, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setQuery_NotTerm(query, opLambda);
    }

    public void setQuery_NotTerm(String query, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setQuery_Term(query), opLambda);
    }

    public void setQuery_Terms(Collection<String> queryList) {
        setQuery_Terms(queryList, null);
    }

    public void setQuery_Terms(Collection<String> queryList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("query", queryList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQuery_InScope(Collection<String> queryList) {
        setQuery_Terms(queryList, null);
    }

    public void setQuery_InScope(Collection<String> queryList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setQuery_Terms(queryList, opLambda);
    }

    public void setQuery_Match(String query) {
        setQuery_Match(query, null);
    }

    public void setQuery_Match(String query, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("query", query);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQuery_MatchPhrase(String query) {
        setQuery_MatchPhrase(query, null);
    }

    public void setQuery_MatchPhrase(String query, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("query", query);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQuery_MatchPhrasePrefix(String query) {
        setQuery_MatchPhrasePrefix(query, null);
    }

    public void setQuery_MatchPhrasePrefix(String query, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("query", query);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQuery_Fuzzy(String query) {
        setQuery_Fuzzy(query, null);
    }

    public void setQuery_Fuzzy(String query, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("query", query);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQuery_Prefix(String query) {
        setQuery_Prefix(query, null);
    }

    public void setQuery_Prefix(String query, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("query", query);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQuery_Wildcard(String query) {
        setQuery_Wildcard(query, null);
    }

    public void setQuery_Wildcard(String query, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("query", query);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQuery_Regexp(String query) {
        setQuery_Regexp(query, null);
    }

    public void setQuery_Regexp(String query, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("query", query);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQuery_SpanTerm(String query) {
        setQuery_SpanTerm("query", null);
    }

    public void setQuery_SpanTerm(String query, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("query", query);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQuery_GreaterThan(String query) {
        setQuery_GreaterThan(query, null);
    }

    public void setQuery_GreaterThan(String query, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = query;
        RangeQueryBuilder builder = regRangeQ("query", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQuery_LessThan(String query) {
        setQuery_LessThan(query, null);
    }

    public void setQuery_LessThan(String query, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = query;
        RangeQueryBuilder builder = regRangeQ("query", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQuery_GreaterEqual(String query) {
        setQuery_GreaterEqual(query, null);
    }

    public void setQuery_GreaterEqual(String query, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = query;
        RangeQueryBuilder builder = regRangeQ("query", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQuery_LessEqual(String query) {
        setQuery_LessEqual(query, null);
    }

    public void setQuery_LessEqual(String query, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = query;
        RangeQueryBuilder builder = regRangeQ("query", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQuery_Exists() {
        setQuery_Exists(null);
    }

    public void setQuery_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("query");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setQuery_CommonTerms(String query) {
        setQuery_CommonTerms(query, null);
    }

    @Deprecated
    public void setQuery_CommonTerms(String query, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("query", query);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsKeyMatchCQ addOrderBy_Query_Asc() {
        regOBA("query");
        return this;
    }

    public BsKeyMatchCQ addOrderBy_Query_Desc() {
        regOBD("query");
        return this;
    }

    public void setTerm_Equal(String term) {
        setTerm_Term(term, null);
    }

    public void setTerm_Equal(String term, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setTerm_Term(term, opLambda);
    }

    public void setTerm_Term(String term) {
        setTerm_Term(term, null);
    }

    public void setTerm_Term(String term, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("term", term);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTerm_NotEqual(String term) {
        setTerm_NotTerm(term, null);
    }

    public void setTerm_NotTerm(String term) {
        setTerm_NotTerm(term, null);
    }

    public void setTerm_NotEqual(String term, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setTerm_NotTerm(term, opLambda);
    }

    public void setTerm_NotTerm(String term, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setTerm_Term(term), opLambda);
    }

    public void setTerm_Terms(Collection<String> termList) {
        setTerm_Terms(termList, null);
    }

    public void setTerm_Terms(Collection<String> termList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("term", termList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTerm_InScope(Collection<String> termList) {
        setTerm_Terms(termList, null);
    }

    public void setTerm_InScope(Collection<String> termList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setTerm_Terms(termList, opLambda);
    }

    public void setTerm_Match(String term) {
        setTerm_Match(term, null);
    }

    public void setTerm_Match(String term, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("term", term);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTerm_MatchPhrase(String term) {
        setTerm_MatchPhrase(term, null);
    }

    public void setTerm_MatchPhrase(String term, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("term", term);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTerm_MatchPhrasePrefix(String term) {
        setTerm_MatchPhrasePrefix(term, null);
    }

    public void setTerm_MatchPhrasePrefix(String term, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("term", term);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTerm_Fuzzy(String term) {
        setTerm_Fuzzy(term, null);
    }

    public void setTerm_Fuzzy(String term, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("term", term);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTerm_Prefix(String term) {
        setTerm_Prefix(term, null);
    }

    public void setTerm_Prefix(String term, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("term", term);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTerm_Wildcard(String term) {
        setTerm_Wildcard(term, null);
    }

    public void setTerm_Wildcard(String term, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("term", term);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTerm_Regexp(String term) {
        setTerm_Regexp(term, null);
    }

    public void setTerm_Regexp(String term, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("term", term);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTerm_SpanTerm(String term) {
        setTerm_SpanTerm("term", null);
    }

    public void setTerm_SpanTerm(String term, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("term", term);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTerm_GreaterThan(String term) {
        setTerm_GreaterThan(term, null);
    }

    public void setTerm_GreaterThan(String term, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = term;
        RangeQueryBuilder builder = regRangeQ("term", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTerm_LessThan(String term) {
        setTerm_LessThan(term, null);
    }

    public void setTerm_LessThan(String term, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = term;
        RangeQueryBuilder builder = regRangeQ("term", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTerm_GreaterEqual(String term) {
        setTerm_GreaterEqual(term, null);
    }

    public void setTerm_GreaterEqual(String term, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = term;
        RangeQueryBuilder builder = regRangeQ("term", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTerm_LessEqual(String term) {
        setTerm_LessEqual(term, null);
    }

    public void setTerm_LessEqual(String term, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = term;
        RangeQueryBuilder builder = regRangeQ("term", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTerm_Exists() {
        setTerm_Exists(null);
    }

    public void setTerm_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("term");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setTerm_CommonTerms(String term) {
        setTerm_CommonTerms(term, null);
    }

    @Deprecated
    public void setTerm_CommonTerms(String term, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("term", term);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsKeyMatchCQ addOrderBy_Term_Asc() {
        regOBA("term");
        return this;
    }

    public BsKeyMatchCQ addOrderBy_Term_Desc() {
        regOBD("term");
        return this;
    }

    public void setUpdatedBy_Equal(String updatedBy) {
        setUpdatedBy_Term(updatedBy, null);
    }

    public void setUpdatedBy_Equal(String updatedBy, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUpdatedBy_Term(updatedBy, opLambda);
    }

    public void setUpdatedBy_Term(String updatedBy) {
        setUpdatedBy_Term(updatedBy, null);
    }

    public void setUpdatedBy_Term(String updatedBy, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_NotEqual(String updatedBy) {
        setUpdatedBy_NotTerm(updatedBy, null);
    }

    public void setUpdatedBy_NotTerm(String updatedBy) {
        setUpdatedBy_NotTerm(updatedBy, null);
    }

    public void setUpdatedBy_NotEqual(String updatedBy, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setUpdatedBy_NotTerm(updatedBy, opLambda);
    }

    public void setUpdatedBy_NotTerm(String updatedBy, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setUpdatedBy_Term(updatedBy), opLambda);
    }

    public void setUpdatedBy_Terms(Collection<String> updatedByList) {
        setUpdatedBy_Terms(updatedByList, null);
    }

    public void setUpdatedBy_Terms(Collection<String> updatedByList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("updatedBy", updatedByList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_InScope(Collection<String> updatedByList) {
        setUpdatedBy_Terms(updatedByList, null);
    }

    public void setUpdatedBy_InScope(Collection<String> updatedByList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUpdatedBy_Terms(updatedByList, opLambda);
    }

    public void setUpdatedBy_Match(String updatedBy) {
        setUpdatedBy_Match(updatedBy, null);
    }

    public void setUpdatedBy_Match(String updatedBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_MatchPhrase(String updatedBy) {
        setUpdatedBy_MatchPhrase(updatedBy, null);
    }

    public void setUpdatedBy_MatchPhrase(String updatedBy, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_MatchPhrasePrefix(String updatedBy) {
        setUpdatedBy_MatchPhrasePrefix(updatedBy, null);
    }

    public void setUpdatedBy_MatchPhrasePrefix(String updatedBy, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Fuzzy(String updatedBy) {
        setUpdatedBy_Fuzzy(updatedBy, null);
    }

    public void setUpdatedBy_Fuzzy(String updatedBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Prefix(String updatedBy) {
        setUpdatedBy_Prefix(updatedBy, null);
    }

    public void setUpdatedBy_Prefix(String updatedBy, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Wildcard(String updatedBy) {
        setUpdatedBy_Wildcard(updatedBy, null);
    }

    public void setUpdatedBy_Wildcard(String updatedBy, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Regexp(String updatedBy) {
        setUpdatedBy_Regexp(updatedBy, null);
    }

    public void setUpdatedBy_Regexp(String updatedBy, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_SpanTerm(String updatedBy) {
        setUpdatedBy_SpanTerm("updatedBy", null);
    }

    public void setUpdatedBy_SpanTerm(String updatedBy, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_GreaterThan(String updatedBy) {
        setUpdatedBy_GreaterThan(updatedBy, null);
    }

    public void setUpdatedBy_GreaterThan(String updatedBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = updatedBy;
        RangeQueryBuilder builder = regRangeQ("updatedBy", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_LessThan(String updatedBy) {
        setUpdatedBy_LessThan(updatedBy, null);
    }

    public void setUpdatedBy_LessThan(String updatedBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = updatedBy;
        RangeQueryBuilder builder = regRangeQ("updatedBy", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_GreaterEqual(String updatedBy) {
        setUpdatedBy_GreaterEqual(updatedBy, null);
    }

    public void setUpdatedBy_GreaterEqual(String updatedBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = updatedBy;
        RangeQueryBuilder builder = regRangeQ("updatedBy", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_LessEqual(String updatedBy) {
        setUpdatedBy_LessEqual(updatedBy, null);
    }

    public void setUpdatedBy_LessEqual(String updatedBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = updatedBy;
        RangeQueryBuilder builder = regRangeQ("updatedBy", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Exists() {
        setUpdatedBy_Exists(null);
    }

    public void setUpdatedBy_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setUpdatedBy_CommonTerms(String updatedBy) {
        setUpdatedBy_CommonTerms(updatedBy, null);
    }

    @Deprecated
    public void setUpdatedBy_CommonTerms(String updatedBy, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsKeyMatchCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("updatedBy");
        return this;
    }

    public BsKeyMatchCQ addOrderBy_UpdatedBy_Desc() {
        regOBD("updatedBy");
        return this;
    }

    public void setUpdatedTime_Equal(Long updatedTime) {
        setUpdatedTime_Term(updatedTime, null);
    }

    public void setUpdatedTime_Equal(Long updatedTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUpdatedTime_Term(updatedTime, opLambda);
    }

    public void setUpdatedTime_Term(Long updatedTime) {
        setUpdatedTime_Term(updatedTime, null);
    }

    public void setUpdatedTime_Term(Long updatedTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_NotEqual(Long updatedTime) {
        setUpdatedTime_NotTerm(updatedTime, null);
    }

    public void setUpdatedTime_NotTerm(Long updatedTime) {
        setUpdatedTime_NotTerm(updatedTime, null);
    }

    public void setUpdatedTime_NotEqual(Long updatedTime, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setUpdatedTime_NotTerm(updatedTime, opLambda);
    }

    public void setUpdatedTime_NotTerm(Long updatedTime, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setUpdatedTime_Term(updatedTime), opLambda);
    }

    public void setUpdatedTime_Terms(Collection<Long> updatedTimeList) {
        setUpdatedTime_Terms(updatedTimeList, null);
    }

    public void setUpdatedTime_Terms(Collection<Long> updatedTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("updatedTime", updatedTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_InScope(Collection<Long> updatedTimeList) {
        setUpdatedTime_Terms(updatedTimeList, null);
    }

    public void setUpdatedTime_InScope(Collection<Long> updatedTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUpdatedTime_Terms(updatedTimeList, opLambda);
    }

    public void setUpdatedTime_Match(Long updatedTime) {
        setUpdatedTime_Match(updatedTime, null);
    }

    public void setUpdatedTime_Match(Long updatedTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_MatchPhrase(Long updatedTime) {
        setUpdatedTime_MatchPhrase(updatedTime, null);
    }

    public void setUpdatedTime_MatchPhrase(Long updatedTime, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_MatchPhrasePrefix(Long updatedTime) {
        setUpdatedTime_MatchPhrasePrefix(updatedTime, null);
    }

    public void setUpdatedTime_MatchPhrasePrefix(Long updatedTime, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Fuzzy(Long updatedTime) {
        setUpdatedTime_Fuzzy(updatedTime, null);
    }

    public void setUpdatedTime_Fuzzy(Long updatedTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_GreaterThan(Long updatedTime) {
        setUpdatedTime_GreaterThan(updatedTime, null);
    }

    public void setUpdatedTime_GreaterThan(Long updatedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = updatedTime;
        RangeQueryBuilder builder = regRangeQ("updatedTime", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_LessThan(Long updatedTime) {
        setUpdatedTime_LessThan(updatedTime, null);
    }

    public void setUpdatedTime_LessThan(Long updatedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = updatedTime;
        RangeQueryBuilder builder = regRangeQ("updatedTime", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_GreaterEqual(Long updatedTime) {
        setUpdatedTime_GreaterEqual(updatedTime, null);
    }

    public void setUpdatedTime_GreaterEqual(Long updatedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = updatedTime;
        RangeQueryBuilder builder = regRangeQ("updatedTime", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_LessEqual(Long updatedTime) {
        setUpdatedTime_LessEqual(updatedTime, null);
    }

    public void setUpdatedTime_LessEqual(Long updatedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = updatedTime;
        RangeQueryBuilder builder = regRangeQ("updatedTime", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Exists() {
        setUpdatedTime_Exists(null);
    }

    public void setUpdatedTime_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setUpdatedTime_CommonTerms(Long updatedTime) {
        setUpdatedTime_CommonTerms(updatedTime, null);
    }

    @Deprecated
    public void setUpdatedTime_CommonTerms(Long updatedTime, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsKeyMatchCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("updatedTime");
        return this;
    }

    public BsKeyMatchCQ addOrderBy_UpdatedTime_Desc() {
        regOBD("updatedTime");
        return this;
    }

    public void setVirtualHost_Equal(String virtualHost) {
        setVirtualHost_Term(virtualHost, null);
    }

    public void setVirtualHost_Equal(String virtualHost, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setVirtualHost_Term(virtualHost, opLambda);
    }

    public void setVirtualHost_Term(String virtualHost) {
        setVirtualHost_Term(virtualHost, null);
    }

    public void setVirtualHost_Term(String virtualHost, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_NotEqual(String virtualHost) {
        setVirtualHost_NotTerm(virtualHost, null);
    }

    public void setVirtualHost_NotTerm(String virtualHost) {
        setVirtualHost_NotTerm(virtualHost, null);
    }

    public void setVirtualHost_NotEqual(String virtualHost, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setVirtualHost_NotTerm(virtualHost, opLambda);
    }

    public void setVirtualHost_NotTerm(String virtualHost, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setVirtualHost_Term(virtualHost), opLambda);
    }

    public void setVirtualHost_Terms(Collection<String> virtualHostList) {
        setVirtualHost_Terms(virtualHostList, null);
    }

    public void setVirtualHost_Terms(Collection<String> virtualHostList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("virtualHost", virtualHostList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_InScope(Collection<String> virtualHostList) {
        setVirtualHost_Terms(virtualHostList, null);
    }

    public void setVirtualHost_InScope(Collection<String> virtualHostList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setVirtualHost_Terms(virtualHostList, opLambda);
    }

    public void setVirtualHost_Match(String virtualHost) {
        setVirtualHost_Match(virtualHost, null);
    }

    public void setVirtualHost_Match(String virtualHost, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_MatchPhrase(String virtualHost) {
        setVirtualHost_MatchPhrase(virtualHost, null);
    }

    public void setVirtualHost_MatchPhrase(String virtualHost, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_MatchPhrasePrefix(String virtualHost) {
        setVirtualHost_MatchPhrasePrefix(virtualHost, null);
    }

    public void setVirtualHost_MatchPhrasePrefix(String virtualHost, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_Fuzzy(String virtualHost) {
        setVirtualHost_Fuzzy(virtualHost, null);
    }

    public void setVirtualHost_Fuzzy(String virtualHost, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_Prefix(String virtualHost) {
        setVirtualHost_Prefix(virtualHost, null);
    }

    public void setVirtualHost_Prefix(String virtualHost, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_Wildcard(String virtualHost) {
        setVirtualHost_Wildcard(virtualHost, null);
    }

    public void setVirtualHost_Wildcard(String virtualHost, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_Regexp(String virtualHost) {
        setVirtualHost_Regexp(virtualHost, null);
    }

    public void setVirtualHost_Regexp(String virtualHost, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_SpanTerm(String virtualHost) {
        setVirtualHost_SpanTerm("virtualHost", null);
    }

    public void setVirtualHost_SpanTerm(String virtualHost, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_GreaterThan(String virtualHost) {
        setVirtualHost_GreaterThan(virtualHost, null);
    }

    public void setVirtualHost_GreaterThan(String virtualHost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = virtualHost;
        RangeQueryBuilder builder = regRangeQ("virtualHost", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_LessThan(String virtualHost) {
        setVirtualHost_LessThan(virtualHost, null);
    }

    public void setVirtualHost_LessThan(String virtualHost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = virtualHost;
        RangeQueryBuilder builder = regRangeQ("virtualHost", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_GreaterEqual(String virtualHost) {
        setVirtualHost_GreaterEqual(virtualHost, null);
    }

    public void setVirtualHost_GreaterEqual(String virtualHost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = virtualHost;
        RangeQueryBuilder builder = regRangeQ("virtualHost", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_LessEqual(String virtualHost) {
        setVirtualHost_LessEqual(virtualHost, null);
    }

    public void setVirtualHost_LessEqual(String virtualHost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = virtualHost;
        RangeQueryBuilder builder = regRangeQ("virtualHost", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_Exists() {
        setVirtualHost_Exists(null);
    }

    public void setVirtualHost_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("virtualHost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setVirtualHost_CommonTerms(String virtualHost) {
        setVirtualHost_CommonTerms(virtualHost, null);
    }

    @Deprecated
    public void setVirtualHost_CommonTerms(String virtualHost, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsKeyMatchCQ addOrderBy_VirtualHost_Asc() {
        regOBA("virtualHost");
        return this;
    }

    public BsKeyMatchCQ addOrderBy_VirtualHost_Desc() {
        regOBD("virtualHost");
        return this;
    }

}
