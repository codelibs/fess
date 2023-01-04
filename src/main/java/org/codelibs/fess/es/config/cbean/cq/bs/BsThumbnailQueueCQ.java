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
import org.codelibs.fess.es.config.cbean.cq.ThumbnailQueueCQ;
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
public abstract class BsThumbnailQueueCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "thumbnail_queue";
    }

    @Override
    public String xgetAliasName() {
        return "thumbnail_queue";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<ThumbnailQueueCQ> queryLambda,
            ScoreFunctionCall<ScoreFunctionCreator<ThumbnailQueueCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        ThumbnailQueueCQ cq = new ThumbnailQueueCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                ThumbnailQueueCQ cf = new ThumbnailQueueCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<ThumbnailQueueCQ, ThumbnailQueueCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<ThumbnailQueueCQ, ThumbnailQueueCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<ThumbnailQueueCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<ThumbnailQueueCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<ThumbnailQueueCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<ThumbnailQueueCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        ThumbnailQueueCQ mustQuery = new ThumbnailQueueCQ();
        ThumbnailQueueCQ shouldQuery = new ThumbnailQueueCQ();
        ThumbnailQueueCQ mustNotQuery = new ThumbnailQueueCQ();
        ThumbnailQueueCQ filterQuery = new ThumbnailQueueCQ();
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
    public BsThumbnailQueueCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsThumbnailQueueCQ addOrderBy_Id_Desc() {
        regOBD("_id");
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

    public BsThumbnailQueueCQ addOrderBy_CreatedBy_Asc() {
        regOBA("createdBy");
        return this;
    }

    public BsThumbnailQueueCQ addOrderBy_CreatedBy_Desc() {
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

    public BsThumbnailQueueCQ addOrderBy_CreatedTime_Asc() {
        regOBA("createdTime");
        return this;
    }

    public BsThumbnailQueueCQ addOrderBy_CreatedTime_Desc() {
        regOBD("createdTime");
        return this;
    }

    public void setGenerator_Equal(String generator) {
        setGenerator_Term(generator, null);
    }

    public void setGenerator_Equal(String generator, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setGenerator_Term(generator, opLambda);
    }

    public void setGenerator_Term(String generator) {
        setGenerator_Term(generator, null);
    }

    public void setGenerator_Term(String generator, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("generator", generator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGenerator_NotEqual(String generator) {
        setGenerator_NotTerm(generator, null);
    }

    public void setGenerator_NotTerm(String generator) {
        setGenerator_NotTerm(generator, null);
    }

    public void setGenerator_NotEqual(String generator, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setGenerator_NotTerm(generator, opLambda);
    }

    public void setGenerator_NotTerm(String generator, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setGenerator_Term(generator), opLambda);
    }

    public void setGenerator_Terms(Collection<String> generatorList) {
        setGenerator_Terms(generatorList, null);
    }

    public void setGenerator_Terms(Collection<String> generatorList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("generator", generatorList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGenerator_InScope(Collection<String> generatorList) {
        setGenerator_Terms(generatorList, null);
    }

    public void setGenerator_InScope(Collection<String> generatorList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setGenerator_Terms(generatorList, opLambda);
    }

    public void setGenerator_Match(String generator) {
        setGenerator_Match(generator, null);
    }

    public void setGenerator_Match(String generator, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("generator", generator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGenerator_MatchPhrase(String generator) {
        setGenerator_MatchPhrase(generator, null);
    }

    public void setGenerator_MatchPhrase(String generator, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("generator", generator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGenerator_MatchPhrasePrefix(String generator) {
        setGenerator_MatchPhrasePrefix(generator, null);
    }

    public void setGenerator_MatchPhrasePrefix(String generator, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("generator", generator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGenerator_Fuzzy(String generator) {
        setGenerator_Fuzzy(generator, null);
    }

    public void setGenerator_Fuzzy(String generator, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("generator", generator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGenerator_Prefix(String generator) {
        setGenerator_Prefix(generator, null);
    }

    public void setGenerator_Prefix(String generator, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("generator", generator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGenerator_Wildcard(String generator) {
        setGenerator_Wildcard(generator, null);
    }

    public void setGenerator_Wildcard(String generator, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("generator", generator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGenerator_Regexp(String generator) {
        setGenerator_Regexp(generator, null);
    }

    public void setGenerator_Regexp(String generator, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("generator", generator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGenerator_SpanTerm(String generator) {
        setGenerator_SpanTerm("generator", null);
    }

    public void setGenerator_SpanTerm(String generator, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("generator", generator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGenerator_GreaterThan(String generator) {
        setGenerator_GreaterThan(generator, null);
    }

    public void setGenerator_GreaterThan(String generator, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = generator;
        RangeQueryBuilder builder = regRangeQ("generator", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGenerator_LessThan(String generator) {
        setGenerator_LessThan(generator, null);
    }

    public void setGenerator_LessThan(String generator, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = generator;
        RangeQueryBuilder builder = regRangeQ("generator", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGenerator_GreaterEqual(String generator) {
        setGenerator_GreaterEqual(generator, null);
    }

    public void setGenerator_GreaterEqual(String generator, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = generator;
        RangeQueryBuilder builder = regRangeQ("generator", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGenerator_LessEqual(String generator) {
        setGenerator_LessEqual(generator, null);
    }

    public void setGenerator_LessEqual(String generator, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = generator;
        RangeQueryBuilder builder = regRangeQ("generator", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGenerator_Exists() {
        setGenerator_Exists(null);
    }

    public void setGenerator_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("generator");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setGenerator_CommonTerms(String generator) {
        setGenerator_CommonTerms(generator, null);
    }

    @Deprecated
    public void setGenerator_CommonTerms(String generator, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("generator", generator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsThumbnailQueueCQ addOrderBy_Generator_Asc() {
        regOBA("generator");
        return this;
    }

    public BsThumbnailQueueCQ addOrderBy_Generator_Desc() {
        regOBD("generator");
        return this;
    }

    public void setPath_Equal(String path) {
        setPath_Term(path, null);
    }

    public void setPath_Equal(String path, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setPath_Term(path, opLambda);
    }

    public void setPath_Term(String path) {
        setPath_Term(path, null);
    }

    public void setPath_Term(String path, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("path", path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_NotEqual(String path) {
        setPath_NotTerm(path, null);
    }

    public void setPath_NotTerm(String path) {
        setPath_NotTerm(path, null);
    }

    public void setPath_NotEqual(String path, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setPath_NotTerm(path, opLambda);
    }

    public void setPath_NotTerm(String path, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setPath_Term(path), opLambda);
    }

    public void setPath_Terms(Collection<String> pathList) {
        setPath_Terms(pathList, null);
    }

    public void setPath_Terms(Collection<String> pathList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("path", pathList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_InScope(Collection<String> pathList) {
        setPath_Terms(pathList, null);
    }

    public void setPath_InScope(Collection<String> pathList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setPath_Terms(pathList, opLambda);
    }

    public void setPath_Match(String path) {
        setPath_Match(path, null);
    }

    public void setPath_Match(String path, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("path", path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_MatchPhrase(String path) {
        setPath_MatchPhrase(path, null);
    }

    public void setPath_MatchPhrase(String path, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("path", path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_MatchPhrasePrefix(String path) {
        setPath_MatchPhrasePrefix(path, null);
    }

    public void setPath_MatchPhrasePrefix(String path, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("path", path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_Fuzzy(String path) {
        setPath_Fuzzy(path, null);
    }

    public void setPath_Fuzzy(String path, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("path", path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_Prefix(String path) {
        setPath_Prefix(path, null);
    }

    public void setPath_Prefix(String path, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("path", path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_Wildcard(String path) {
        setPath_Wildcard(path, null);
    }

    public void setPath_Wildcard(String path, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("path", path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_Regexp(String path) {
        setPath_Regexp(path, null);
    }

    public void setPath_Regexp(String path, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("path", path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_SpanTerm(String path) {
        setPath_SpanTerm("path", null);
    }

    public void setPath_SpanTerm(String path, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("path", path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_GreaterThan(String path) {
        setPath_GreaterThan(path, null);
    }

    public void setPath_GreaterThan(String path, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = path;
        RangeQueryBuilder builder = regRangeQ("path", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_LessThan(String path) {
        setPath_LessThan(path, null);
    }

    public void setPath_LessThan(String path, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = path;
        RangeQueryBuilder builder = regRangeQ("path", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_GreaterEqual(String path) {
        setPath_GreaterEqual(path, null);
    }

    public void setPath_GreaterEqual(String path, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = path;
        RangeQueryBuilder builder = regRangeQ("path", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_LessEqual(String path) {
        setPath_LessEqual(path, null);
    }

    public void setPath_LessEqual(String path, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = path;
        RangeQueryBuilder builder = regRangeQ("path", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_Exists() {
        setPath_Exists(null);
    }

    public void setPath_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("path");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setPath_CommonTerms(String path) {
        setPath_CommonTerms(path, null);
    }

    @Deprecated
    public void setPath_CommonTerms(String path, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("path", path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsThumbnailQueueCQ addOrderBy_Path_Asc() {
        regOBA("path");
        return this;
    }

    public BsThumbnailQueueCQ addOrderBy_Path_Desc() {
        regOBD("path");
        return this;
    }

    public void setTarget_Equal(String target) {
        setTarget_Term(target, null);
    }

    public void setTarget_Equal(String target, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setTarget_Term(target, opLambda);
    }

    public void setTarget_Term(String target) {
        setTarget_Term(target, null);
    }

    public void setTarget_Term(String target, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_NotEqual(String target) {
        setTarget_NotTerm(target, null);
    }

    public void setTarget_NotTerm(String target) {
        setTarget_NotTerm(target, null);
    }

    public void setTarget_NotEqual(String target, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setTarget_NotTerm(target, opLambda);
    }

    public void setTarget_NotTerm(String target, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setTarget_Term(target), opLambda);
    }

    public void setTarget_Terms(Collection<String> targetList) {
        setTarget_Terms(targetList, null);
    }

    public void setTarget_Terms(Collection<String> targetList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("target", targetList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_InScope(Collection<String> targetList) {
        setTarget_Terms(targetList, null);
    }

    public void setTarget_InScope(Collection<String> targetList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setTarget_Terms(targetList, opLambda);
    }

    public void setTarget_Match(String target) {
        setTarget_Match(target, null);
    }

    public void setTarget_Match(String target, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_MatchPhrase(String target) {
        setTarget_MatchPhrase(target, null);
    }

    public void setTarget_MatchPhrase(String target, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_MatchPhrasePrefix(String target) {
        setTarget_MatchPhrasePrefix(target, null);
    }

    public void setTarget_MatchPhrasePrefix(String target, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Fuzzy(String target) {
        setTarget_Fuzzy(target, null);
    }

    public void setTarget_Fuzzy(String target, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Prefix(String target) {
        setTarget_Prefix(target, null);
    }

    public void setTarget_Prefix(String target, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Wildcard(String target) {
        setTarget_Wildcard(target, null);
    }

    public void setTarget_Wildcard(String target, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Regexp(String target) {
        setTarget_Regexp(target, null);
    }

    public void setTarget_Regexp(String target, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_SpanTerm(String target) {
        setTarget_SpanTerm("target", null);
    }

    public void setTarget_SpanTerm(String target, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_GreaterThan(String target) {
        setTarget_GreaterThan(target, null);
    }

    public void setTarget_GreaterThan(String target, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = target;
        RangeQueryBuilder builder = regRangeQ("target", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_LessThan(String target) {
        setTarget_LessThan(target, null);
    }

    public void setTarget_LessThan(String target, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = target;
        RangeQueryBuilder builder = regRangeQ("target", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_GreaterEqual(String target) {
        setTarget_GreaterEqual(target, null);
    }

    public void setTarget_GreaterEqual(String target, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = target;
        RangeQueryBuilder builder = regRangeQ("target", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_LessEqual(String target) {
        setTarget_LessEqual(target, null);
    }

    public void setTarget_LessEqual(String target, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = target;
        RangeQueryBuilder builder = regRangeQ("target", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Exists() {
        setTarget_Exists(null);
    }

    public void setTarget_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setTarget_CommonTerms(String target) {
        setTarget_CommonTerms(target, null);
    }

    @Deprecated
    public void setTarget_CommonTerms(String target, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsThumbnailQueueCQ addOrderBy_Target_Asc() {
        regOBA("target");
        return this;
    }

    public BsThumbnailQueueCQ addOrderBy_Target_Desc() {
        regOBD("target");
        return this;
    }

    public void setThumbnailId_Equal(String thumbnailId) {
        setThumbnailId_Term(thumbnailId, null);
    }

    public void setThumbnailId_Equal(String thumbnailId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setThumbnailId_Term(thumbnailId, opLambda);
    }

    public void setThumbnailId_Term(String thumbnailId) {
        setThumbnailId_Term(thumbnailId, null);
    }

    public void setThumbnailId_Term(String thumbnailId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("thumbnail_id", thumbnailId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThumbnailId_NotEqual(String thumbnailId) {
        setThumbnailId_NotTerm(thumbnailId, null);
    }

    public void setThumbnailId_NotTerm(String thumbnailId) {
        setThumbnailId_NotTerm(thumbnailId, null);
    }

    public void setThumbnailId_NotEqual(String thumbnailId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setThumbnailId_NotTerm(thumbnailId, opLambda);
    }

    public void setThumbnailId_NotTerm(String thumbnailId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setThumbnailId_Term(thumbnailId), opLambda);
    }

    public void setThumbnailId_Terms(Collection<String> thumbnailIdList) {
        setThumbnailId_Terms(thumbnailIdList, null);
    }

    public void setThumbnailId_Terms(Collection<String> thumbnailIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("thumbnail_id", thumbnailIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThumbnailId_InScope(Collection<String> thumbnailIdList) {
        setThumbnailId_Terms(thumbnailIdList, null);
    }

    public void setThumbnailId_InScope(Collection<String> thumbnailIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setThumbnailId_Terms(thumbnailIdList, opLambda);
    }

    public void setThumbnailId_Match(String thumbnailId) {
        setThumbnailId_Match(thumbnailId, null);
    }

    public void setThumbnailId_Match(String thumbnailId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("thumbnail_id", thumbnailId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThumbnailId_MatchPhrase(String thumbnailId) {
        setThumbnailId_MatchPhrase(thumbnailId, null);
    }

    public void setThumbnailId_MatchPhrase(String thumbnailId, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("thumbnail_id", thumbnailId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThumbnailId_MatchPhrasePrefix(String thumbnailId) {
        setThumbnailId_MatchPhrasePrefix(thumbnailId, null);
    }

    public void setThumbnailId_MatchPhrasePrefix(String thumbnailId, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("thumbnail_id", thumbnailId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThumbnailId_Fuzzy(String thumbnailId) {
        setThumbnailId_Fuzzy(thumbnailId, null);
    }

    public void setThumbnailId_Fuzzy(String thumbnailId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("thumbnail_id", thumbnailId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThumbnailId_Prefix(String thumbnailId) {
        setThumbnailId_Prefix(thumbnailId, null);
    }

    public void setThumbnailId_Prefix(String thumbnailId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("thumbnail_id", thumbnailId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThumbnailId_Wildcard(String thumbnailId) {
        setThumbnailId_Wildcard(thumbnailId, null);
    }

    public void setThumbnailId_Wildcard(String thumbnailId, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("thumbnail_id", thumbnailId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThumbnailId_Regexp(String thumbnailId) {
        setThumbnailId_Regexp(thumbnailId, null);
    }

    public void setThumbnailId_Regexp(String thumbnailId, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("thumbnail_id", thumbnailId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThumbnailId_SpanTerm(String thumbnailId) {
        setThumbnailId_SpanTerm("thumbnail_id", null);
    }

    public void setThumbnailId_SpanTerm(String thumbnailId, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("thumbnail_id", thumbnailId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThumbnailId_GreaterThan(String thumbnailId) {
        setThumbnailId_GreaterThan(thumbnailId, null);
    }

    public void setThumbnailId_GreaterThan(String thumbnailId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = thumbnailId;
        RangeQueryBuilder builder = regRangeQ("thumbnail_id", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThumbnailId_LessThan(String thumbnailId) {
        setThumbnailId_LessThan(thumbnailId, null);
    }

    public void setThumbnailId_LessThan(String thumbnailId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = thumbnailId;
        RangeQueryBuilder builder = regRangeQ("thumbnail_id", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThumbnailId_GreaterEqual(String thumbnailId) {
        setThumbnailId_GreaterEqual(thumbnailId, null);
    }

    public void setThumbnailId_GreaterEqual(String thumbnailId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = thumbnailId;
        RangeQueryBuilder builder = regRangeQ("thumbnail_id", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThumbnailId_LessEqual(String thumbnailId) {
        setThumbnailId_LessEqual(thumbnailId, null);
    }

    public void setThumbnailId_LessEqual(String thumbnailId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = thumbnailId;
        RangeQueryBuilder builder = regRangeQ("thumbnail_id", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThumbnailId_Exists() {
        setThumbnailId_Exists(null);
    }

    public void setThumbnailId_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("thumbnail_id");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setThumbnailId_CommonTerms(String thumbnailId) {
        setThumbnailId_CommonTerms(thumbnailId, null);
    }

    @Deprecated
    public void setThumbnailId_CommonTerms(String thumbnailId, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("thumbnail_id", thumbnailId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsThumbnailQueueCQ addOrderBy_ThumbnailId_Asc() {
        regOBA("thumbnail_id");
        return this;
    }

    public BsThumbnailQueueCQ addOrderBy_ThumbnailId_Desc() {
        regOBD("thumbnail_id");
        return this;
    }

}
