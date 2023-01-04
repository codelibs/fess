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
import org.codelibs.fess.es.config.cbean.cq.ElevateWordCQ;
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
public abstract class BsElevateWordCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "elevate_word";
    }

    @Override
    public String xgetAliasName() {
        return "elevate_word";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<ElevateWordCQ> queryLambda,
            ScoreFunctionCall<ScoreFunctionCreator<ElevateWordCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        ElevateWordCQ cq = new ElevateWordCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                ElevateWordCQ cf = new ElevateWordCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<ElevateWordCQ, ElevateWordCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<ElevateWordCQ, ElevateWordCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<ElevateWordCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<ElevateWordCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<ElevateWordCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<ElevateWordCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        ElevateWordCQ mustQuery = new ElevateWordCQ();
        ElevateWordCQ shouldQuery = new ElevateWordCQ();
        ElevateWordCQ mustNotQuery = new ElevateWordCQ();
        ElevateWordCQ filterQuery = new ElevateWordCQ();
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
    public BsElevateWordCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsElevateWordCQ addOrderBy_Id_Desc() {
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

    public BsElevateWordCQ addOrderBy_Boost_Asc() {
        regOBA("boost");
        return this;
    }

    public BsElevateWordCQ addOrderBy_Boost_Desc() {
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

    public BsElevateWordCQ addOrderBy_CreatedBy_Asc() {
        regOBA("createdBy");
        return this;
    }

    public BsElevateWordCQ addOrderBy_CreatedBy_Desc() {
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

    public BsElevateWordCQ addOrderBy_CreatedTime_Asc() {
        regOBA("createdTime");
        return this;
    }

    public BsElevateWordCQ addOrderBy_CreatedTime_Desc() {
        regOBD("createdTime");
        return this;
    }

    public void setPermissions_Equal(String permissions) {
        setPermissions_Term(permissions, null);
    }

    public void setPermissions_Equal(String permissions, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setPermissions_Term(permissions, opLambda);
    }

    public void setPermissions_Term(String permissions) {
        setPermissions_Term(permissions, null);
    }

    public void setPermissions_Term(String permissions, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("permissions", permissions);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPermissions_NotEqual(String permissions) {
        setPermissions_NotTerm(permissions, null);
    }

    public void setPermissions_NotTerm(String permissions) {
        setPermissions_NotTerm(permissions, null);
    }

    public void setPermissions_NotEqual(String permissions, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setPermissions_NotTerm(permissions, opLambda);
    }

    public void setPermissions_NotTerm(String permissions, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setPermissions_Term(permissions), opLambda);
    }

    public void setPermissions_Terms(Collection<String> permissionsList) {
        setPermissions_Terms(permissionsList, null);
    }

    public void setPermissions_Terms(Collection<String> permissionsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("permissions", permissionsList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPermissions_InScope(Collection<String> permissionsList) {
        setPermissions_Terms(permissionsList, null);
    }

    public void setPermissions_InScope(Collection<String> permissionsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setPermissions_Terms(permissionsList, opLambda);
    }

    public void setPermissions_Match(String permissions) {
        setPermissions_Match(permissions, null);
    }

    public void setPermissions_Match(String permissions, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("permissions", permissions);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPermissions_MatchPhrase(String permissions) {
        setPermissions_MatchPhrase(permissions, null);
    }

    public void setPermissions_MatchPhrase(String permissions, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("permissions", permissions);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPermissions_MatchPhrasePrefix(String permissions) {
        setPermissions_MatchPhrasePrefix(permissions, null);
    }

    public void setPermissions_MatchPhrasePrefix(String permissions, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("permissions", permissions);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPermissions_Fuzzy(String permissions) {
        setPermissions_Fuzzy(permissions, null);
    }

    public void setPermissions_Fuzzy(String permissions, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("permissions", permissions);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPermissions_Prefix(String permissions) {
        setPermissions_Prefix(permissions, null);
    }

    public void setPermissions_Prefix(String permissions, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("permissions", permissions);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPermissions_Wildcard(String permissions) {
        setPermissions_Wildcard(permissions, null);
    }

    public void setPermissions_Wildcard(String permissions, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("permissions", permissions);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPermissions_Regexp(String permissions) {
        setPermissions_Regexp(permissions, null);
    }

    public void setPermissions_Regexp(String permissions, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("permissions", permissions);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPermissions_SpanTerm(String permissions) {
        setPermissions_SpanTerm("permissions", null);
    }

    public void setPermissions_SpanTerm(String permissions, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("permissions", permissions);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPermissions_GreaterThan(String permissions) {
        setPermissions_GreaterThan(permissions, null);
    }

    public void setPermissions_GreaterThan(String permissions, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = permissions;
        RangeQueryBuilder builder = regRangeQ("permissions", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPermissions_LessThan(String permissions) {
        setPermissions_LessThan(permissions, null);
    }

    public void setPermissions_LessThan(String permissions, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = permissions;
        RangeQueryBuilder builder = regRangeQ("permissions", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPermissions_GreaterEqual(String permissions) {
        setPermissions_GreaterEqual(permissions, null);
    }

    public void setPermissions_GreaterEqual(String permissions, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = permissions;
        RangeQueryBuilder builder = regRangeQ("permissions", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPermissions_LessEqual(String permissions) {
        setPermissions_LessEqual(permissions, null);
    }

    public void setPermissions_LessEqual(String permissions, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = permissions;
        RangeQueryBuilder builder = regRangeQ("permissions", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPermissions_Exists() {
        setPermissions_Exists(null);
    }

    public void setPermissions_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("permissions");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setPermissions_CommonTerms(String permissions) {
        setPermissions_CommonTerms(permissions, null);
    }

    @Deprecated
    public void setPermissions_CommonTerms(String permissions, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("permissions", permissions);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsElevateWordCQ addOrderBy_Permissions_Asc() {
        regOBA("permissions");
        return this;
    }

    public BsElevateWordCQ addOrderBy_Permissions_Desc() {
        regOBD("permissions");
        return this;
    }

    public void setReading_Equal(String reading) {
        setReading_Term(reading, null);
    }

    public void setReading_Equal(String reading, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setReading_Term(reading, opLambda);
    }

    public void setReading_Term(String reading) {
        setReading_Term(reading, null);
    }

    public void setReading_Term(String reading, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("reading", reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_NotEqual(String reading) {
        setReading_NotTerm(reading, null);
    }

    public void setReading_NotTerm(String reading) {
        setReading_NotTerm(reading, null);
    }

    public void setReading_NotEqual(String reading, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setReading_NotTerm(reading, opLambda);
    }

    public void setReading_NotTerm(String reading, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setReading_Term(reading), opLambda);
    }

    public void setReading_Terms(Collection<String> readingList) {
        setReading_Terms(readingList, null);
    }

    public void setReading_Terms(Collection<String> readingList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("reading", readingList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_InScope(Collection<String> readingList) {
        setReading_Terms(readingList, null);
    }

    public void setReading_InScope(Collection<String> readingList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setReading_Terms(readingList, opLambda);
    }

    public void setReading_Match(String reading) {
        setReading_Match(reading, null);
    }

    public void setReading_Match(String reading, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("reading", reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_MatchPhrase(String reading) {
        setReading_MatchPhrase(reading, null);
    }

    public void setReading_MatchPhrase(String reading, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("reading", reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_MatchPhrasePrefix(String reading) {
        setReading_MatchPhrasePrefix(reading, null);
    }

    public void setReading_MatchPhrasePrefix(String reading, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("reading", reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_Fuzzy(String reading) {
        setReading_Fuzzy(reading, null);
    }

    public void setReading_Fuzzy(String reading, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("reading", reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_Prefix(String reading) {
        setReading_Prefix(reading, null);
    }

    public void setReading_Prefix(String reading, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("reading", reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_Wildcard(String reading) {
        setReading_Wildcard(reading, null);
    }

    public void setReading_Wildcard(String reading, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("reading", reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_Regexp(String reading) {
        setReading_Regexp(reading, null);
    }

    public void setReading_Regexp(String reading, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("reading", reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_SpanTerm(String reading) {
        setReading_SpanTerm("reading", null);
    }

    public void setReading_SpanTerm(String reading, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("reading", reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_GreaterThan(String reading) {
        setReading_GreaterThan(reading, null);
    }

    public void setReading_GreaterThan(String reading, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = reading;
        RangeQueryBuilder builder = regRangeQ("reading", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_LessThan(String reading) {
        setReading_LessThan(reading, null);
    }

    public void setReading_LessThan(String reading, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = reading;
        RangeQueryBuilder builder = regRangeQ("reading", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_GreaterEqual(String reading) {
        setReading_GreaterEqual(reading, null);
    }

    public void setReading_GreaterEqual(String reading, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = reading;
        RangeQueryBuilder builder = regRangeQ("reading", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_LessEqual(String reading) {
        setReading_LessEqual(reading, null);
    }

    public void setReading_LessEqual(String reading, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = reading;
        RangeQueryBuilder builder = regRangeQ("reading", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_Exists() {
        setReading_Exists(null);
    }

    public void setReading_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("reading");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setReading_CommonTerms(String reading) {
        setReading_CommonTerms(reading, null);
    }

    @Deprecated
    public void setReading_CommonTerms(String reading, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("reading", reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsElevateWordCQ addOrderBy_Reading_Asc() {
        regOBA("reading");
        return this;
    }

    public BsElevateWordCQ addOrderBy_Reading_Desc() {
        regOBD("reading");
        return this;
    }

    public void setSuggestWord_Equal(String suggestWord) {
        setSuggestWord_Term(suggestWord, null);
    }

    public void setSuggestWord_Equal(String suggestWord, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setSuggestWord_Term(suggestWord, opLambda);
    }

    public void setSuggestWord_Term(String suggestWord) {
        setSuggestWord_Term(suggestWord, null);
    }

    public void setSuggestWord_Term(String suggestWord, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("suggestWord", suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_NotEqual(String suggestWord) {
        setSuggestWord_NotTerm(suggestWord, null);
    }

    public void setSuggestWord_NotTerm(String suggestWord) {
        setSuggestWord_NotTerm(suggestWord, null);
    }

    public void setSuggestWord_NotEqual(String suggestWord, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setSuggestWord_NotTerm(suggestWord, opLambda);
    }

    public void setSuggestWord_NotTerm(String suggestWord, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setSuggestWord_Term(suggestWord), opLambda);
    }

    public void setSuggestWord_Terms(Collection<String> suggestWordList) {
        setSuggestWord_Terms(suggestWordList, null);
    }

    public void setSuggestWord_Terms(Collection<String> suggestWordList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("suggestWord", suggestWordList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_InScope(Collection<String> suggestWordList) {
        setSuggestWord_Terms(suggestWordList, null);
    }

    public void setSuggestWord_InScope(Collection<String> suggestWordList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setSuggestWord_Terms(suggestWordList, opLambda);
    }

    public void setSuggestWord_Match(String suggestWord) {
        setSuggestWord_Match(suggestWord, null);
    }

    public void setSuggestWord_Match(String suggestWord, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("suggestWord", suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_MatchPhrase(String suggestWord) {
        setSuggestWord_MatchPhrase(suggestWord, null);
    }

    public void setSuggestWord_MatchPhrase(String suggestWord, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("suggestWord", suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_MatchPhrasePrefix(String suggestWord) {
        setSuggestWord_MatchPhrasePrefix(suggestWord, null);
    }

    public void setSuggestWord_MatchPhrasePrefix(String suggestWord, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("suggestWord", suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_Fuzzy(String suggestWord) {
        setSuggestWord_Fuzzy(suggestWord, null);
    }

    public void setSuggestWord_Fuzzy(String suggestWord, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("suggestWord", suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_Prefix(String suggestWord) {
        setSuggestWord_Prefix(suggestWord, null);
    }

    public void setSuggestWord_Prefix(String suggestWord, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("suggestWord", suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_Wildcard(String suggestWord) {
        setSuggestWord_Wildcard(suggestWord, null);
    }

    public void setSuggestWord_Wildcard(String suggestWord, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("suggestWord", suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_Regexp(String suggestWord) {
        setSuggestWord_Regexp(suggestWord, null);
    }

    public void setSuggestWord_Regexp(String suggestWord, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("suggestWord", suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_SpanTerm(String suggestWord) {
        setSuggestWord_SpanTerm("suggestWord", null);
    }

    public void setSuggestWord_SpanTerm(String suggestWord, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("suggestWord", suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_GreaterThan(String suggestWord) {
        setSuggestWord_GreaterThan(suggestWord, null);
    }

    public void setSuggestWord_GreaterThan(String suggestWord, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = suggestWord;
        RangeQueryBuilder builder = regRangeQ("suggestWord", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_LessThan(String suggestWord) {
        setSuggestWord_LessThan(suggestWord, null);
    }

    public void setSuggestWord_LessThan(String suggestWord, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = suggestWord;
        RangeQueryBuilder builder = regRangeQ("suggestWord", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_GreaterEqual(String suggestWord) {
        setSuggestWord_GreaterEqual(suggestWord, null);
    }

    public void setSuggestWord_GreaterEqual(String suggestWord, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = suggestWord;
        RangeQueryBuilder builder = regRangeQ("suggestWord", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_LessEqual(String suggestWord) {
        setSuggestWord_LessEqual(suggestWord, null);
    }

    public void setSuggestWord_LessEqual(String suggestWord, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = suggestWord;
        RangeQueryBuilder builder = regRangeQ("suggestWord", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_Exists() {
        setSuggestWord_Exists(null);
    }

    public void setSuggestWord_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("suggestWord");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setSuggestWord_CommonTerms(String suggestWord) {
        setSuggestWord_CommonTerms(suggestWord, null);
    }

    @Deprecated
    public void setSuggestWord_CommonTerms(String suggestWord, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("suggestWord", suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsElevateWordCQ addOrderBy_SuggestWord_Asc() {
        regOBA("suggestWord");
        return this;
    }

    public BsElevateWordCQ addOrderBy_SuggestWord_Desc() {
        regOBD("suggestWord");
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

    public BsElevateWordCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("updatedBy");
        return this;
    }

    public BsElevateWordCQ addOrderBy_UpdatedBy_Desc() {
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

    public BsElevateWordCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("updatedTime");
        return this;
    }

    public BsElevateWordCQ addOrderBy_UpdatedTime_Desc() {
        regOBD("updatedTime");
        return this;
    }

}
