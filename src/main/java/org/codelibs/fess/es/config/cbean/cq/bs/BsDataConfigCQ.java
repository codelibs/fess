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
import org.codelibs.fess.es.config.cbean.cq.DataConfigCQ;
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
public abstract class BsDataConfigCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "data_config";
    }

    @Override
    public String xgetAliasName() {
        return "data_config";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<DataConfigCQ> queryLambda, ScoreFunctionCall<ScoreFunctionCreator<DataConfigCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        DataConfigCQ cq = new DataConfigCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                DataConfigCQ cf = new DataConfigCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<DataConfigCQ, DataConfigCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<DataConfigCQ, DataConfigCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<DataConfigCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<DataConfigCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<DataConfigCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<DataConfigCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        DataConfigCQ mustQuery = new DataConfigCQ();
        DataConfigCQ shouldQuery = new DataConfigCQ();
        DataConfigCQ mustNotQuery = new DataConfigCQ();
        DataConfigCQ filterQuery = new DataConfigCQ();
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
    public BsDataConfigCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsDataConfigCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setAvailable_Equal(Boolean available) {
        setAvailable_Term(available, null);
    }

    public void setAvailable_Equal(Boolean available, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setAvailable_Term(available, opLambda);
    }

    public void setAvailable_Term(Boolean available) {
        setAvailable_Term(available, null);
    }

    public void setAvailable_Term(Boolean available, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("available", available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_NotEqual(Boolean available) {
        setAvailable_NotTerm(available, null);
    }

    public void setAvailable_NotTerm(Boolean available) {
        setAvailable_NotTerm(available, null);
    }

    public void setAvailable_NotEqual(Boolean available, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setAvailable_NotTerm(available, opLambda);
    }

    public void setAvailable_NotTerm(Boolean available, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setAvailable_Term(available), opLambda);
    }

    public void setAvailable_Terms(Collection<Boolean> availableList) {
        setAvailable_Terms(availableList, null);
    }

    public void setAvailable_Terms(Collection<Boolean> availableList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("available", availableList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_InScope(Collection<Boolean> availableList) {
        setAvailable_Terms(availableList, null);
    }

    public void setAvailable_InScope(Collection<Boolean> availableList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setAvailable_Terms(availableList, opLambda);
    }

    public void setAvailable_Match(Boolean available) {
        setAvailable_Match(available, null);
    }

    public void setAvailable_Match(Boolean available, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("available", available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_MatchPhrase(Boolean available) {
        setAvailable_MatchPhrase(available, null);
    }

    public void setAvailable_MatchPhrase(Boolean available, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("available", available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_MatchPhrasePrefix(Boolean available) {
        setAvailable_MatchPhrasePrefix(available, null);
    }

    public void setAvailable_MatchPhrasePrefix(Boolean available, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("available", available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Fuzzy(Boolean available) {
        setAvailable_Fuzzy(available, null);
    }

    public void setAvailable_Fuzzy(Boolean available, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("available", available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_GreaterThan(Boolean available) {
        setAvailable_GreaterThan(available, null);
    }

    public void setAvailable_GreaterThan(Boolean available, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = available;
        RangeQueryBuilder builder = regRangeQ("available", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_LessThan(Boolean available) {
        setAvailable_LessThan(available, null);
    }

    public void setAvailable_LessThan(Boolean available, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = available;
        RangeQueryBuilder builder = regRangeQ("available", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_GreaterEqual(Boolean available) {
        setAvailable_GreaterEqual(available, null);
    }

    public void setAvailable_GreaterEqual(Boolean available, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = available;
        RangeQueryBuilder builder = regRangeQ("available", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_LessEqual(Boolean available) {
        setAvailable_LessEqual(available, null);
    }

    public void setAvailable_LessEqual(Boolean available, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = available;
        RangeQueryBuilder builder = regRangeQ("available", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Exists() {
        setAvailable_Exists(null);
    }

    public void setAvailable_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setAvailable_CommonTerms(Boolean available) {
        setAvailable_CommonTerms(available, null);
    }

    @Deprecated
    public void setAvailable_CommonTerms(Boolean available, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("available", available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsDataConfigCQ addOrderBy_Available_Asc() {
        regOBA("available");
        return this;
    }

    public BsDataConfigCQ addOrderBy_Available_Desc() {
        regOBD("available");
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

    public BsDataConfigCQ addOrderBy_Boost_Asc() {
        regOBA("boost");
        return this;
    }

    public BsDataConfigCQ addOrderBy_Boost_Desc() {
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

    public BsDataConfigCQ addOrderBy_CreatedBy_Asc() {
        regOBA("createdBy");
        return this;
    }

    public BsDataConfigCQ addOrderBy_CreatedBy_Desc() {
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

    public BsDataConfigCQ addOrderBy_CreatedTime_Asc() {
        regOBA("createdTime");
        return this;
    }

    public BsDataConfigCQ addOrderBy_CreatedTime_Desc() {
        regOBD("createdTime");
        return this;
    }

    public void setDescription_Equal(String description) {
        setDescription_Term(description, null);
    }

    public void setDescription_Equal(String description, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setDescription_Term(description, opLambda);
    }

    public void setDescription_Term(String description) {
        setDescription_Term(description, null);
    }

    public void setDescription_Term(String description, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("description", description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_NotEqual(String description) {
        setDescription_NotTerm(description, null);
    }

    public void setDescription_NotTerm(String description) {
        setDescription_NotTerm(description, null);
    }

    public void setDescription_NotEqual(String description, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setDescription_NotTerm(description, opLambda);
    }

    public void setDescription_NotTerm(String description, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setDescription_Term(description), opLambda);
    }

    public void setDescription_Terms(Collection<String> descriptionList) {
        setDescription_Terms(descriptionList, null);
    }

    public void setDescription_Terms(Collection<String> descriptionList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("description", descriptionList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_InScope(Collection<String> descriptionList) {
        setDescription_Terms(descriptionList, null);
    }

    public void setDescription_InScope(Collection<String> descriptionList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setDescription_Terms(descriptionList, opLambda);
    }

    public void setDescription_Match(String description) {
        setDescription_Match(description, null);
    }

    public void setDescription_Match(String description, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("description", description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_MatchPhrase(String description) {
        setDescription_MatchPhrase(description, null);
    }

    public void setDescription_MatchPhrase(String description, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("description", description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_MatchPhrasePrefix(String description) {
        setDescription_MatchPhrasePrefix(description, null);
    }

    public void setDescription_MatchPhrasePrefix(String description, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("description", description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_Fuzzy(String description) {
        setDescription_Fuzzy(description, null);
    }

    public void setDescription_Fuzzy(String description, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("description", description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_Prefix(String description) {
        setDescription_Prefix(description, null);
    }

    public void setDescription_Prefix(String description, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("description", description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_Wildcard(String description) {
        setDescription_Wildcard(description, null);
    }

    public void setDescription_Wildcard(String description, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("description", description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_Regexp(String description) {
        setDescription_Regexp(description, null);
    }

    public void setDescription_Regexp(String description, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("description", description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_SpanTerm(String description) {
        setDescription_SpanTerm("description", null);
    }

    public void setDescription_SpanTerm(String description, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("description", description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_GreaterThan(String description) {
        setDescription_GreaterThan(description, null);
    }

    public void setDescription_GreaterThan(String description, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = description;
        RangeQueryBuilder builder = regRangeQ("description", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_LessThan(String description) {
        setDescription_LessThan(description, null);
    }

    public void setDescription_LessThan(String description, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = description;
        RangeQueryBuilder builder = regRangeQ("description", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_GreaterEqual(String description) {
        setDescription_GreaterEqual(description, null);
    }

    public void setDescription_GreaterEqual(String description, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = description;
        RangeQueryBuilder builder = regRangeQ("description", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_LessEqual(String description) {
        setDescription_LessEqual(description, null);
    }

    public void setDescription_LessEqual(String description, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = description;
        RangeQueryBuilder builder = regRangeQ("description", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_Exists() {
        setDescription_Exists(null);
    }

    public void setDescription_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("description");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setDescription_CommonTerms(String description) {
        setDescription_CommonTerms(description, null);
    }

    @Deprecated
    public void setDescription_CommonTerms(String description, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("description", description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerName_Equal(String handlerName) {
        setHandlerName_Term(handlerName, null);
    }

    public void setHandlerName_Equal(String handlerName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setHandlerName_Term(handlerName, opLambda);
    }

    public void setHandlerName_Term(String handlerName) {
        setHandlerName_Term(handlerName, null);
    }

    public void setHandlerName_Term(String handlerName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("handlerName", handlerName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerName_NotEqual(String handlerName) {
        setHandlerName_NotTerm(handlerName, null);
    }

    public void setHandlerName_NotTerm(String handlerName) {
        setHandlerName_NotTerm(handlerName, null);
    }

    public void setHandlerName_NotEqual(String handlerName, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setHandlerName_NotTerm(handlerName, opLambda);
    }

    public void setHandlerName_NotTerm(String handlerName, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setHandlerName_Term(handlerName), opLambda);
    }

    public void setHandlerName_Terms(Collection<String> handlerNameList) {
        setHandlerName_Terms(handlerNameList, null);
    }

    public void setHandlerName_Terms(Collection<String> handlerNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("handlerName", handlerNameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerName_InScope(Collection<String> handlerNameList) {
        setHandlerName_Terms(handlerNameList, null);
    }

    public void setHandlerName_InScope(Collection<String> handlerNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setHandlerName_Terms(handlerNameList, opLambda);
    }

    public void setHandlerName_Match(String handlerName) {
        setHandlerName_Match(handlerName, null);
    }

    public void setHandlerName_Match(String handlerName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("handlerName", handlerName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerName_MatchPhrase(String handlerName) {
        setHandlerName_MatchPhrase(handlerName, null);
    }

    public void setHandlerName_MatchPhrase(String handlerName, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("handlerName", handlerName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerName_MatchPhrasePrefix(String handlerName) {
        setHandlerName_MatchPhrasePrefix(handlerName, null);
    }

    public void setHandlerName_MatchPhrasePrefix(String handlerName, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("handlerName", handlerName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerName_Fuzzy(String handlerName) {
        setHandlerName_Fuzzy(handlerName, null);
    }

    public void setHandlerName_Fuzzy(String handlerName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("handlerName", handlerName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerName_Prefix(String handlerName) {
        setHandlerName_Prefix(handlerName, null);
    }

    public void setHandlerName_Prefix(String handlerName, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("handlerName", handlerName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerName_Wildcard(String handlerName) {
        setHandlerName_Wildcard(handlerName, null);
    }

    public void setHandlerName_Wildcard(String handlerName, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("handlerName", handlerName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerName_Regexp(String handlerName) {
        setHandlerName_Regexp(handlerName, null);
    }

    public void setHandlerName_Regexp(String handlerName, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("handlerName", handlerName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerName_SpanTerm(String handlerName) {
        setHandlerName_SpanTerm("handlerName", null);
    }

    public void setHandlerName_SpanTerm(String handlerName, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("handlerName", handlerName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerName_GreaterThan(String handlerName) {
        setHandlerName_GreaterThan(handlerName, null);
    }

    public void setHandlerName_GreaterThan(String handlerName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = handlerName;
        RangeQueryBuilder builder = regRangeQ("handlerName", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerName_LessThan(String handlerName) {
        setHandlerName_LessThan(handlerName, null);
    }

    public void setHandlerName_LessThan(String handlerName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = handlerName;
        RangeQueryBuilder builder = regRangeQ("handlerName", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerName_GreaterEqual(String handlerName) {
        setHandlerName_GreaterEqual(handlerName, null);
    }

    public void setHandlerName_GreaterEqual(String handlerName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = handlerName;
        RangeQueryBuilder builder = regRangeQ("handlerName", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerName_LessEqual(String handlerName) {
        setHandlerName_LessEqual(handlerName, null);
    }

    public void setHandlerName_LessEqual(String handlerName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = handlerName;
        RangeQueryBuilder builder = regRangeQ("handlerName", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerName_Exists() {
        setHandlerName_Exists(null);
    }

    public void setHandlerName_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("handlerName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setHandlerName_CommonTerms(String handlerName) {
        setHandlerName_CommonTerms(handlerName, null);
    }

    @Deprecated
    public void setHandlerName_CommonTerms(String handlerName, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("handlerName", handlerName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsDataConfigCQ addOrderBy_HandlerName_Asc() {
        regOBA("handlerName");
        return this;
    }

    public BsDataConfigCQ addOrderBy_HandlerName_Desc() {
        regOBD("handlerName");
        return this;
    }

    public void setHandlerParameter_Equal(String handlerParameter) {
        setHandlerParameter_Term(handlerParameter, null);
    }

    public void setHandlerParameter_Equal(String handlerParameter, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setHandlerParameter_Term(handlerParameter, opLambda);
    }

    public void setHandlerParameter_Term(String handlerParameter) {
        setHandlerParameter_Term(handlerParameter, null);
    }

    public void setHandlerParameter_Term(String handlerParameter, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("handlerParameter", handlerParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerParameter_NotEqual(String handlerParameter) {
        setHandlerParameter_NotTerm(handlerParameter, null);
    }

    public void setHandlerParameter_NotTerm(String handlerParameter) {
        setHandlerParameter_NotTerm(handlerParameter, null);
    }

    public void setHandlerParameter_NotEqual(String handlerParameter, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setHandlerParameter_NotTerm(handlerParameter, opLambda);
    }

    public void setHandlerParameter_NotTerm(String handlerParameter, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setHandlerParameter_Term(handlerParameter), opLambda);
    }

    public void setHandlerParameter_Terms(Collection<String> handlerParameterList) {
        setHandlerParameter_Terms(handlerParameterList, null);
    }

    public void setHandlerParameter_Terms(Collection<String> handlerParameterList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("handlerParameter", handlerParameterList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerParameter_InScope(Collection<String> handlerParameterList) {
        setHandlerParameter_Terms(handlerParameterList, null);
    }

    public void setHandlerParameter_InScope(Collection<String> handlerParameterList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setHandlerParameter_Terms(handlerParameterList, opLambda);
    }

    public void setHandlerParameter_Match(String handlerParameter) {
        setHandlerParameter_Match(handlerParameter, null);
    }

    public void setHandlerParameter_Match(String handlerParameter, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("handlerParameter", handlerParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerParameter_MatchPhrase(String handlerParameter) {
        setHandlerParameter_MatchPhrase(handlerParameter, null);
    }

    public void setHandlerParameter_MatchPhrase(String handlerParameter, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("handlerParameter", handlerParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerParameter_MatchPhrasePrefix(String handlerParameter) {
        setHandlerParameter_MatchPhrasePrefix(handlerParameter, null);
    }

    public void setHandlerParameter_MatchPhrasePrefix(String handlerParameter,
            ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("handlerParameter", handlerParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerParameter_Fuzzy(String handlerParameter) {
        setHandlerParameter_Fuzzy(handlerParameter, null);
    }

    public void setHandlerParameter_Fuzzy(String handlerParameter, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("handlerParameter", handlerParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerParameter_Prefix(String handlerParameter) {
        setHandlerParameter_Prefix(handlerParameter, null);
    }

    public void setHandlerParameter_Prefix(String handlerParameter, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("handlerParameter", handlerParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerParameter_Wildcard(String handlerParameter) {
        setHandlerParameter_Wildcard(handlerParameter, null);
    }

    public void setHandlerParameter_Wildcard(String handlerParameter, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("handlerParameter", handlerParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerParameter_Regexp(String handlerParameter) {
        setHandlerParameter_Regexp(handlerParameter, null);
    }

    public void setHandlerParameter_Regexp(String handlerParameter, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("handlerParameter", handlerParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerParameter_SpanTerm(String handlerParameter) {
        setHandlerParameter_SpanTerm("handlerParameter", null);
    }

    public void setHandlerParameter_SpanTerm(String handlerParameter, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("handlerParameter", handlerParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerParameter_GreaterThan(String handlerParameter) {
        setHandlerParameter_GreaterThan(handlerParameter, null);
    }

    public void setHandlerParameter_GreaterThan(String handlerParameter, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = handlerParameter;
        RangeQueryBuilder builder = regRangeQ("handlerParameter", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerParameter_LessThan(String handlerParameter) {
        setHandlerParameter_LessThan(handlerParameter, null);
    }

    public void setHandlerParameter_LessThan(String handlerParameter, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = handlerParameter;
        RangeQueryBuilder builder = regRangeQ("handlerParameter", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerParameter_GreaterEqual(String handlerParameter) {
        setHandlerParameter_GreaterEqual(handlerParameter, null);
    }

    public void setHandlerParameter_GreaterEqual(String handlerParameter, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = handlerParameter;
        RangeQueryBuilder builder = regRangeQ("handlerParameter", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerParameter_LessEqual(String handlerParameter) {
        setHandlerParameter_LessEqual(handlerParameter, null);
    }

    public void setHandlerParameter_LessEqual(String handlerParameter, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = handlerParameter;
        RangeQueryBuilder builder = regRangeQ("handlerParameter", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerParameter_Exists() {
        setHandlerParameter_Exists(null);
    }

    public void setHandlerParameter_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("handlerParameter");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setHandlerParameter_CommonTerms(String handlerParameter) {
        setHandlerParameter_CommonTerms(handlerParameter, null);
    }

    @Deprecated
    public void setHandlerParameter_CommonTerms(String handlerParameter, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("handlerParameter", handlerParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsDataConfigCQ addOrderBy_HandlerParameter_Asc() {
        regOBA("handlerParameter");
        return this;
    }

    public BsDataConfigCQ addOrderBy_HandlerParameter_Desc() {
        regOBD("handlerParameter");
        return this;
    }

    public void setHandlerScript_Equal(String handlerScript) {
        setHandlerScript_Term(handlerScript, null);
    }

    public void setHandlerScript_Equal(String handlerScript, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setHandlerScript_Term(handlerScript, opLambda);
    }

    public void setHandlerScript_Term(String handlerScript) {
        setHandlerScript_Term(handlerScript, null);
    }

    public void setHandlerScript_Term(String handlerScript, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("handlerScript", handlerScript);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerScript_NotEqual(String handlerScript) {
        setHandlerScript_NotTerm(handlerScript, null);
    }

    public void setHandlerScript_NotTerm(String handlerScript) {
        setHandlerScript_NotTerm(handlerScript, null);
    }

    public void setHandlerScript_NotEqual(String handlerScript, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setHandlerScript_NotTerm(handlerScript, opLambda);
    }

    public void setHandlerScript_NotTerm(String handlerScript, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setHandlerScript_Term(handlerScript), opLambda);
    }

    public void setHandlerScript_Terms(Collection<String> handlerScriptList) {
        setHandlerScript_Terms(handlerScriptList, null);
    }

    public void setHandlerScript_Terms(Collection<String> handlerScriptList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("handlerScript", handlerScriptList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerScript_InScope(Collection<String> handlerScriptList) {
        setHandlerScript_Terms(handlerScriptList, null);
    }

    public void setHandlerScript_InScope(Collection<String> handlerScriptList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setHandlerScript_Terms(handlerScriptList, opLambda);
    }

    public void setHandlerScript_Match(String handlerScript) {
        setHandlerScript_Match(handlerScript, null);
    }

    public void setHandlerScript_Match(String handlerScript, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("handlerScript", handlerScript);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerScript_MatchPhrase(String handlerScript) {
        setHandlerScript_MatchPhrase(handlerScript, null);
    }

    public void setHandlerScript_MatchPhrase(String handlerScript, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("handlerScript", handlerScript);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerScript_MatchPhrasePrefix(String handlerScript) {
        setHandlerScript_MatchPhrasePrefix(handlerScript, null);
    }

    public void setHandlerScript_MatchPhrasePrefix(String handlerScript, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("handlerScript", handlerScript);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerScript_Fuzzy(String handlerScript) {
        setHandlerScript_Fuzzy(handlerScript, null);
    }

    public void setHandlerScript_Fuzzy(String handlerScript, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("handlerScript", handlerScript);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerScript_Prefix(String handlerScript) {
        setHandlerScript_Prefix(handlerScript, null);
    }

    public void setHandlerScript_Prefix(String handlerScript, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("handlerScript", handlerScript);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerScript_Wildcard(String handlerScript) {
        setHandlerScript_Wildcard(handlerScript, null);
    }

    public void setHandlerScript_Wildcard(String handlerScript, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("handlerScript", handlerScript);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerScript_Regexp(String handlerScript) {
        setHandlerScript_Regexp(handlerScript, null);
    }

    public void setHandlerScript_Regexp(String handlerScript, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("handlerScript", handlerScript);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerScript_SpanTerm(String handlerScript) {
        setHandlerScript_SpanTerm("handlerScript", null);
    }

    public void setHandlerScript_SpanTerm(String handlerScript, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("handlerScript", handlerScript);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerScript_GreaterThan(String handlerScript) {
        setHandlerScript_GreaterThan(handlerScript, null);
    }

    public void setHandlerScript_GreaterThan(String handlerScript, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = handlerScript;
        RangeQueryBuilder builder = regRangeQ("handlerScript", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerScript_LessThan(String handlerScript) {
        setHandlerScript_LessThan(handlerScript, null);
    }

    public void setHandlerScript_LessThan(String handlerScript, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = handlerScript;
        RangeQueryBuilder builder = regRangeQ("handlerScript", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerScript_GreaterEqual(String handlerScript) {
        setHandlerScript_GreaterEqual(handlerScript, null);
    }

    public void setHandlerScript_GreaterEqual(String handlerScript, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = handlerScript;
        RangeQueryBuilder builder = regRangeQ("handlerScript", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerScript_LessEqual(String handlerScript) {
        setHandlerScript_LessEqual(handlerScript, null);
    }

    public void setHandlerScript_LessEqual(String handlerScript, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = handlerScript;
        RangeQueryBuilder builder = regRangeQ("handlerScript", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerScript_Exists() {
        setHandlerScript_Exists(null);
    }

    public void setHandlerScript_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("handlerScript");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setHandlerScript_CommonTerms(String handlerScript) {
        setHandlerScript_CommonTerms(handlerScript, null);
    }

    @Deprecated
    public void setHandlerScript_CommonTerms(String handlerScript, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("handlerScript", handlerScript);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsDataConfigCQ addOrderBy_HandlerScript_Asc() {
        regOBA("handlerScript");
        return this;
    }

    public BsDataConfigCQ addOrderBy_HandlerScript_Desc() {
        regOBD("handlerScript");
        return this;
    }

    public void setName_Equal(String name) {
        setName_Term(name, null);
    }

    public void setName_Equal(String name, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setName_Term(name, opLambda);
    }

    public void setName_Term(String name) {
        setName_Term(name, null);
    }

    public void setName_Term(String name, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_NotEqual(String name) {
        setName_NotTerm(name, null);
    }

    public void setName_NotTerm(String name) {
        setName_NotTerm(name, null);
    }

    public void setName_NotEqual(String name, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setName_NotTerm(name, opLambda);
    }

    public void setName_NotTerm(String name, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setName_Term(name), opLambda);
    }

    public void setName_Terms(Collection<String> nameList) {
        setName_Terms(nameList, null);
    }

    public void setName_Terms(Collection<String> nameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("name", nameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_InScope(Collection<String> nameList) {
        setName_Terms(nameList, null);
    }

    public void setName_InScope(Collection<String> nameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setName_Terms(nameList, opLambda);
    }

    public void setName_Match(String name) {
        setName_Match(name, null);
    }

    public void setName_Match(String name, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_MatchPhrase(String name) {
        setName_MatchPhrase(name, null);
    }

    public void setName_MatchPhrase(String name, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_MatchPhrasePrefix(String name) {
        setName_MatchPhrasePrefix(name, null);
    }

    public void setName_MatchPhrasePrefix(String name, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Fuzzy(String name) {
        setName_Fuzzy(name, null);
    }

    public void setName_Fuzzy(String name, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Prefix(String name) {
        setName_Prefix(name, null);
    }

    public void setName_Prefix(String name, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Wildcard(String name) {
        setName_Wildcard(name, null);
    }

    public void setName_Wildcard(String name, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Regexp(String name) {
        setName_Regexp(name, null);
    }

    public void setName_Regexp(String name, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_SpanTerm(String name) {
        setName_SpanTerm("name", null);
    }

    public void setName_SpanTerm(String name, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_GreaterThan(String name) {
        setName_GreaterThan(name, null);
    }

    public void setName_GreaterThan(String name, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = name;
        RangeQueryBuilder builder = regRangeQ("name", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_LessThan(String name) {
        setName_LessThan(name, null);
    }

    public void setName_LessThan(String name, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = name;
        RangeQueryBuilder builder = regRangeQ("name", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_GreaterEqual(String name) {
        setName_GreaterEqual(name, null);
    }

    public void setName_GreaterEqual(String name, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = name;
        RangeQueryBuilder builder = regRangeQ("name", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_LessEqual(String name) {
        setName_LessEqual(name, null);
    }

    public void setName_LessEqual(String name, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = name;
        RangeQueryBuilder builder = regRangeQ("name", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Exists() {
        setName_Exists(null);
    }

    public void setName_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setName_CommonTerms(String name) {
        setName_CommonTerms(name, null);
    }

    @Deprecated
    public void setName_CommonTerms(String name, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsDataConfigCQ addOrderBy_Name_Asc() {
        regOBA("name");
        return this;
    }

    public BsDataConfigCQ addOrderBy_Name_Desc() {
        regOBD("name");
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

    public BsDataConfigCQ addOrderBy_Permissions_Asc() {
        regOBA("permissions");
        return this;
    }

    public BsDataConfigCQ addOrderBy_Permissions_Desc() {
        regOBD("permissions");
        return this;
    }

    public void setSortOrder_Equal(Integer sortOrder) {
        setSortOrder_Term(sortOrder, null);
    }

    public void setSortOrder_Equal(Integer sortOrder, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setSortOrder_Term(sortOrder, opLambda);
    }

    public void setSortOrder_Term(Integer sortOrder) {
        setSortOrder_Term(sortOrder, null);
    }

    public void setSortOrder_Term(Integer sortOrder, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("sortOrder", sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_NotEqual(Integer sortOrder) {
        setSortOrder_NotTerm(sortOrder, null);
    }

    public void setSortOrder_NotTerm(Integer sortOrder) {
        setSortOrder_NotTerm(sortOrder, null);
    }

    public void setSortOrder_NotEqual(Integer sortOrder, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setSortOrder_NotTerm(sortOrder, opLambda);
    }

    public void setSortOrder_NotTerm(Integer sortOrder, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setSortOrder_Term(sortOrder), opLambda);
    }

    public void setSortOrder_Terms(Collection<Integer> sortOrderList) {
        setSortOrder_Terms(sortOrderList, null);
    }

    public void setSortOrder_Terms(Collection<Integer> sortOrderList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("sortOrder", sortOrderList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_InScope(Collection<Integer> sortOrderList) {
        setSortOrder_Terms(sortOrderList, null);
    }

    public void setSortOrder_InScope(Collection<Integer> sortOrderList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setSortOrder_Terms(sortOrderList, opLambda);
    }

    public void setSortOrder_Match(Integer sortOrder) {
        setSortOrder_Match(sortOrder, null);
    }

    public void setSortOrder_Match(Integer sortOrder, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("sortOrder", sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_MatchPhrase(Integer sortOrder) {
        setSortOrder_MatchPhrase(sortOrder, null);
    }

    public void setSortOrder_MatchPhrase(Integer sortOrder, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("sortOrder", sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_MatchPhrasePrefix(Integer sortOrder) {
        setSortOrder_MatchPhrasePrefix(sortOrder, null);
    }

    public void setSortOrder_MatchPhrasePrefix(Integer sortOrder, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("sortOrder", sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Fuzzy(Integer sortOrder) {
        setSortOrder_Fuzzy(sortOrder, null);
    }

    public void setSortOrder_Fuzzy(Integer sortOrder, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("sortOrder", sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_GreaterThan(Integer sortOrder) {
        setSortOrder_GreaterThan(sortOrder, null);
    }

    public void setSortOrder_GreaterThan(Integer sortOrder, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = sortOrder;
        RangeQueryBuilder builder = regRangeQ("sortOrder", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_LessThan(Integer sortOrder) {
        setSortOrder_LessThan(sortOrder, null);
    }

    public void setSortOrder_LessThan(Integer sortOrder, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = sortOrder;
        RangeQueryBuilder builder = regRangeQ("sortOrder", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_GreaterEqual(Integer sortOrder) {
        setSortOrder_GreaterEqual(sortOrder, null);
    }

    public void setSortOrder_GreaterEqual(Integer sortOrder, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = sortOrder;
        RangeQueryBuilder builder = regRangeQ("sortOrder", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_LessEqual(Integer sortOrder) {
        setSortOrder_LessEqual(sortOrder, null);
    }

    public void setSortOrder_LessEqual(Integer sortOrder, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = sortOrder;
        RangeQueryBuilder builder = regRangeQ("sortOrder", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Exists() {
        setSortOrder_Exists(null);
    }

    public void setSortOrder_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setSortOrder_CommonTerms(Integer sortOrder) {
        setSortOrder_CommonTerms(sortOrder, null);
    }

    @Deprecated
    public void setSortOrder_CommonTerms(Integer sortOrder, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("sortOrder", sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsDataConfigCQ addOrderBy_SortOrder_Asc() {
        regOBA("sortOrder");
        return this;
    }

    public BsDataConfigCQ addOrderBy_SortOrder_Desc() {
        regOBD("sortOrder");
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

    public BsDataConfigCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("updatedBy");
        return this;
    }

    public BsDataConfigCQ addOrderBy_UpdatedBy_Desc() {
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

    public BsDataConfigCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("updatedTime");
        return this;
    }

    public BsDataConfigCQ addOrderBy_UpdatedTime_Desc() {
        regOBD("updatedTime");
        return this;
    }

    public void setVirtualHosts_Equal(String virtualHosts) {
        setVirtualHosts_Term(virtualHosts, null);
    }

    public void setVirtualHosts_Equal(String virtualHosts, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setVirtualHosts_Term(virtualHosts, opLambda);
    }

    public void setVirtualHosts_Term(String virtualHosts) {
        setVirtualHosts_Term(virtualHosts, null);
    }

    public void setVirtualHosts_Term(String virtualHosts, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("virtualHosts", virtualHosts);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHosts_NotEqual(String virtualHosts) {
        setVirtualHosts_NotTerm(virtualHosts, null);
    }

    public void setVirtualHosts_NotTerm(String virtualHosts) {
        setVirtualHosts_NotTerm(virtualHosts, null);
    }

    public void setVirtualHosts_NotEqual(String virtualHosts, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setVirtualHosts_NotTerm(virtualHosts, opLambda);
    }

    public void setVirtualHosts_NotTerm(String virtualHosts, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setVirtualHosts_Term(virtualHosts), opLambda);
    }

    public void setVirtualHosts_Terms(Collection<String> virtualHostsList) {
        setVirtualHosts_Terms(virtualHostsList, null);
    }

    public void setVirtualHosts_Terms(Collection<String> virtualHostsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("virtualHosts", virtualHostsList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHosts_InScope(Collection<String> virtualHostsList) {
        setVirtualHosts_Terms(virtualHostsList, null);
    }

    public void setVirtualHosts_InScope(Collection<String> virtualHostsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setVirtualHosts_Terms(virtualHostsList, opLambda);
    }

    public void setVirtualHosts_Match(String virtualHosts) {
        setVirtualHosts_Match(virtualHosts, null);
    }

    public void setVirtualHosts_Match(String virtualHosts, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("virtualHosts", virtualHosts);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHosts_MatchPhrase(String virtualHosts) {
        setVirtualHosts_MatchPhrase(virtualHosts, null);
    }

    public void setVirtualHosts_MatchPhrase(String virtualHosts, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("virtualHosts", virtualHosts);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHosts_MatchPhrasePrefix(String virtualHosts) {
        setVirtualHosts_MatchPhrasePrefix(virtualHosts, null);
    }

    public void setVirtualHosts_MatchPhrasePrefix(String virtualHosts, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("virtualHosts", virtualHosts);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHosts_Fuzzy(String virtualHosts) {
        setVirtualHosts_Fuzzy(virtualHosts, null);
    }

    public void setVirtualHosts_Fuzzy(String virtualHosts, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("virtualHosts", virtualHosts);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHosts_Prefix(String virtualHosts) {
        setVirtualHosts_Prefix(virtualHosts, null);
    }

    public void setVirtualHosts_Prefix(String virtualHosts, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("virtualHosts", virtualHosts);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHosts_Wildcard(String virtualHosts) {
        setVirtualHosts_Wildcard(virtualHosts, null);
    }

    public void setVirtualHosts_Wildcard(String virtualHosts, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("virtualHosts", virtualHosts);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHosts_Regexp(String virtualHosts) {
        setVirtualHosts_Regexp(virtualHosts, null);
    }

    public void setVirtualHosts_Regexp(String virtualHosts, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("virtualHosts", virtualHosts);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHosts_SpanTerm(String virtualHosts) {
        setVirtualHosts_SpanTerm("virtualHosts", null);
    }

    public void setVirtualHosts_SpanTerm(String virtualHosts, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("virtualHosts", virtualHosts);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHosts_GreaterThan(String virtualHosts) {
        setVirtualHosts_GreaterThan(virtualHosts, null);
    }

    public void setVirtualHosts_GreaterThan(String virtualHosts, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = virtualHosts;
        RangeQueryBuilder builder = regRangeQ("virtualHosts", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHosts_LessThan(String virtualHosts) {
        setVirtualHosts_LessThan(virtualHosts, null);
    }

    public void setVirtualHosts_LessThan(String virtualHosts, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = virtualHosts;
        RangeQueryBuilder builder = regRangeQ("virtualHosts", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHosts_GreaterEqual(String virtualHosts) {
        setVirtualHosts_GreaterEqual(virtualHosts, null);
    }

    public void setVirtualHosts_GreaterEqual(String virtualHosts, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = virtualHosts;
        RangeQueryBuilder builder = regRangeQ("virtualHosts", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHosts_LessEqual(String virtualHosts) {
        setVirtualHosts_LessEqual(virtualHosts, null);
    }

    public void setVirtualHosts_LessEqual(String virtualHosts, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = virtualHosts;
        RangeQueryBuilder builder = regRangeQ("virtualHosts", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHosts_Exists() {
        setVirtualHosts_Exists(null);
    }

    public void setVirtualHosts_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("virtualHosts");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setVirtualHosts_CommonTerms(String virtualHosts) {
        setVirtualHosts_CommonTerms(virtualHosts, null);
    }

    @Deprecated
    public void setVirtualHosts_CommonTerms(String virtualHosts, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("virtualHosts", virtualHosts);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsDataConfigCQ addOrderBy_VirtualHosts_Asc() {
        regOBA("virtualHosts");
        return this;
    }

    public BsDataConfigCQ addOrderBy_VirtualHosts_Desc() {
        regOBD("virtualHosts");
        return this;
    }

}
