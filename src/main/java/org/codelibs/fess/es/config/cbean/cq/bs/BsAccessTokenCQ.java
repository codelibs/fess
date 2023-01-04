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
import org.codelibs.fess.es.config.cbean.cq.AccessTokenCQ;
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
public abstract class BsAccessTokenCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "access_token";
    }

    @Override
    public String xgetAliasName() {
        return "access_token";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<AccessTokenCQ> queryLambda,
            ScoreFunctionCall<ScoreFunctionCreator<AccessTokenCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        AccessTokenCQ cq = new AccessTokenCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                AccessTokenCQ cf = new AccessTokenCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<AccessTokenCQ, AccessTokenCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<AccessTokenCQ, AccessTokenCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<AccessTokenCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<AccessTokenCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<AccessTokenCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<AccessTokenCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        AccessTokenCQ mustQuery = new AccessTokenCQ();
        AccessTokenCQ shouldQuery = new AccessTokenCQ();
        AccessTokenCQ mustNotQuery = new AccessTokenCQ();
        AccessTokenCQ filterQuery = new AccessTokenCQ();
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
    public BsAccessTokenCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsAccessTokenCQ addOrderBy_Id_Desc() {
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

    public BsAccessTokenCQ addOrderBy_CreatedBy_Asc() {
        regOBA("createdBy");
        return this;
    }

    public BsAccessTokenCQ addOrderBy_CreatedBy_Desc() {
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

    public BsAccessTokenCQ addOrderBy_CreatedTime_Asc() {
        regOBA("createdTime");
        return this;
    }

    public BsAccessTokenCQ addOrderBy_CreatedTime_Desc() {
        regOBD("createdTime");
        return this;
    }

    public void setExpiredTime_Equal(Long expiredTime) {
        setExpiredTime_Term(expiredTime, null);
    }

    public void setExpiredTime_Equal(Long expiredTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setExpiredTime_Term(expiredTime, opLambda);
    }

    public void setExpiredTime_Term(Long expiredTime) {
        setExpiredTime_Term(expiredTime, null);
    }

    public void setExpiredTime_Term(Long expiredTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("expiredTime", expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_NotEqual(Long expiredTime) {
        setExpiredTime_NotTerm(expiredTime, null);
    }

    public void setExpiredTime_NotTerm(Long expiredTime) {
        setExpiredTime_NotTerm(expiredTime, null);
    }

    public void setExpiredTime_NotEqual(Long expiredTime, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setExpiredTime_NotTerm(expiredTime, opLambda);
    }

    public void setExpiredTime_NotTerm(Long expiredTime, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setExpiredTime_Term(expiredTime), opLambda);
    }

    public void setExpiredTime_Terms(Collection<Long> expiredTimeList) {
        setExpiredTime_Terms(expiredTimeList, null);
    }

    public void setExpiredTime_Terms(Collection<Long> expiredTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("expiredTime", expiredTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_InScope(Collection<Long> expiredTimeList) {
        setExpiredTime_Terms(expiredTimeList, null);
    }

    public void setExpiredTime_InScope(Collection<Long> expiredTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setExpiredTime_Terms(expiredTimeList, opLambda);
    }

    public void setExpiredTime_Match(Long expiredTime) {
        setExpiredTime_Match(expiredTime, null);
    }

    public void setExpiredTime_Match(Long expiredTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("expiredTime", expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_MatchPhrase(Long expiredTime) {
        setExpiredTime_MatchPhrase(expiredTime, null);
    }

    public void setExpiredTime_MatchPhrase(Long expiredTime, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("expiredTime", expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_MatchPhrasePrefix(Long expiredTime) {
        setExpiredTime_MatchPhrasePrefix(expiredTime, null);
    }

    public void setExpiredTime_MatchPhrasePrefix(Long expiredTime, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("expiredTime", expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_Fuzzy(Long expiredTime) {
        setExpiredTime_Fuzzy(expiredTime, null);
    }

    public void setExpiredTime_Fuzzy(Long expiredTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("expiredTime", expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_GreaterThan(Long expiredTime) {
        setExpiredTime_GreaterThan(expiredTime, null);
    }

    public void setExpiredTime_GreaterThan(Long expiredTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = expiredTime;
        RangeQueryBuilder builder = regRangeQ("expiredTime", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_LessThan(Long expiredTime) {
        setExpiredTime_LessThan(expiredTime, null);
    }

    public void setExpiredTime_LessThan(Long expiredTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = expiredTime;
        RangeQueryBuilder builder = regRangeQ("expiredTime", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_GreaterEqual(Long expiredTime) {
        setExpiredTime_GreaterEqual(expiredTime, null);
    }

    public void setExpiredTime_GreaterEqual(Long expiredTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = expiredTime;
        RangeQueryBuilder builder = regRangeQ("expiredTime", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_LessEqual(Long expiredTime) {
        setExpiredTime_LessEqual(expiredTime, null);
    }

    public void setExpiredTime_LessEqual(Long expiredTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = expiredTime;
        RangeQueryBuilder builder = regRangeQ("expiredTime", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_Exists() {
        setExpiredTime_Exists(null);
    }

    public void setExpiredTime_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("expiredTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setExpiredTime_CommonTerms(Long expiredTime) {
        setExpiredTime_CommonTerms(expiredTime, null);
    }

    @Deprecated
    public void setExpiredTime_CommonTerms(Long expiredTime, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("expiredTime", expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsAccessTokenCQ addOrderBy_ExpiredTime_Asc() {
        regOBA("expiredTime");
        return this;
    }

    public BsAccessTokenCQ addOrderBy_ExpiredTime_Desc() {
        regOBD("expiredTime");
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

    public BsAccessTokenCQ addOrderBy_Name_Asc() {
        regOBA("name");
        return this;
    }

    public BsAccessTokenCQ addOrderBy_Name_Desc() {
        regOBD("name");
        return this;
    }

    public void setParameterName_Equal(String parameterName) {
        setParameterName_Term(parameterName, null);
    }

    public void setParameterName_Equal(String parameterName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setParameterName_Term(parameterName, opLambda);
    }

    public void setParameterName_Term(String parameterName) {
        setParameterName_Term(parameterName, null);
    }

    public void setParameterName_Term(String parameterName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("parameter_name", parameterName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameterName_NotEqual(String parameterName) {
        setParameterName_NotTerm(parameterName, null);
    }

    public void setParameterName_NotTerm(String parameterName) {
        setParameterName_NotTerm(parameterName, null);
    }

    public void setParameterName_NotEqual(String parameterName, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setParameterName_NotTerm(parameterName, opLambda);
    }

    public void setParameterName_NotTerm(String parameterName, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setParameterName_Term(parameterName), opLambda);
    }

    public void setParameterName_Terms(Collection<String> parameterNameList) {
        setParameterName_Terms(parameterNameList, null);
    }

    public void setParameterName_Terms(Collection<String> parameterNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("parameter_name", parameterNameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameterName_InScope(Collection<String> parameterNameList) {
        setParameterName_Terms(parameterNameList, null);
    }

    public void setParameterName_InScope(Collection<String> parameterNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setParameterName_Terms(parameterNameList, opLambda);
    }

    public void setParameterName_Match(String parameterName) {
        setParameterName_Match(parameterName, null);
    }

    public void setParameterName_Match(String parameterName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("parameter_name", parameterName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameterName_MatchPhrase(String parameterName) {
        setParameterName_MatchPhrase(parameterName, null);
    }

    public void setParameterName_MatchPhrase(String parameterName, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("parameter_name", parameterName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameterName_MatchPhrasePrefix(String parameterName) {
        setParameterName_MatchPhrasePrefix(parameterName, null);
    }

    public void setParameterName_MatchPhrasePrefix(String parameterName, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("parameter_name", parameterName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameterName_Fuzzy(String parameterName) {
        setParameterName_Fuzzy(parameterName, null);
    }

    public void setParameterName_Fuzzy(String parameterName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("parameter_name", parameterName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameterName_Prefix(String parameterName) {
        setParameterName_Prefix(parameterName, null);
    }

    public void setParameterName_Prefix(String parameterName, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("parameter_name", parameterName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameterName_Wildcard(String parameterName) {
        setParameterName_Wildcard(parameterName, null);
    }

    public void setParameterName_Wildcard(String parameterName, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("parameter_name", parameterName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameterName_Regexp(String parameterName) {
        setParameterName_Regexp(parameterName, null);
    }

    public void setParameterName_Regexp(String parameterName, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("parameter_name", parameterName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameterName_SpanTerm(String parameterName) {
        setParameterName_SpanTerm("parameter_name", null);
    }

    public void setParameterName_SpanTerm(String parameterName, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("parameter_name", parameterName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameterName_GreaterThan(String parameterName) {
        setParameterName_GreaterThan(parameterName, null);
    }

    public void setParameterName_GreaterThan(String parameterName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = parameterName;
        RangeQueryBuilder builder = regRangeQ("parameter_name", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameterName_LessThan(String parameterName) {
        setParameterName_LessThan(parameterName, null);
    }

    public void setParameterName_LessThan(String parameterName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = parameterName;
        RangeQueryBuilder builder = regRangeQ("parameter_name", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameterName_GreaterEqual(String parameterName) {
        setParameterName_GreaterEqual(parameterName, null);
    }

    public void setParameterName_GreaterEqual(String parameterName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = parameterName;
        RangeQueryBuilder builder = regRangeQ("parameter_name", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameterName_LessEqual(String parameterName) {
        setParameterName_LessEqual(parameterName, null);
    }

    public void setParameterName_LessEqual(String parameterName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = parameterName;
        RangeQueryBuilder builder = regRangeQ("parameter_name", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameterName_Exists() {
        setParameterName_Exists(null);
    }

    public void setParameterName_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("parameter_name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setParameterName_CommonTerms(String parameterName) {
        setParameterName_CommonTerms(parameterName, null);
    }

    @Deprecated
    public void setParameterName_CommonTerms(String parameterName, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("parameter_name", parameterName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsAccessTokenCQ addOrderBy_ParameterName_Asc() {
        regOBA("parameter_name");
        return this;
    }

    public BsAccessTokenCQ addOrderBy_ParameterName_Desc() {
        regOBD("parameter_name");
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

    public BsAccessTokenCQ addOrderBy_Permissions_Asc() {
        regOBA("permissions");
        return this;
    }

    public BsAccessTokenCQ addOrderBy_Permissions_Desc() {
        regOBD("permissions");
        return this;
    }

    public void setToken_Equal(String token) {
        setToken_Term(token, null);
    }

    public void setToken_Equal(String token, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setToken_Term(token, opLambda);
    }

    public void setToken_Term(String token) {
        setToken_Term(token, null);
    }

    public void setToken_Term(String token, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("token", token);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setToken_NotEqual(String token) {
        setToken_NotTerm(token, null);
    }

    public void setToken_NotTerm(String token) {
        setToken_NotTerm(token, null);
    }

    public void setToken_NotEqual(String token, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setToken_NotTerm(token, opLambda);
    }

    public void setToken_NotTerm(String token, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setToken_Term(token), opLambda);
    }

    public void setToken_Terms(Collection<String> tokenList) {
        setToken_Terms(tokenList, null);
    }

    public void setToken_Terms(Collection<String> tokenList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("token", tokenList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setToken_InScope(Collection<String> tokenList) {
        setToken_Terms(tokenList, null);
    }

    public void setToken_InScope(Collection<String> tokenList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setToken_Terms(tokenList, opLambda);
    }

    public void setToken_Match(String token) {
        setToken_Match(token, null);
    }

    public void setToken_Match(String token, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("token", token);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setToken_MatchPhrase(String token) {
        setToken_MatchPhrase(token, null);
    }

    public void setToken_MatchPhrase(String token, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("token", token);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setToken_MatchPhrasePrefix(String token) {
        setToken_MatchPhrasePrefix(token, null);
    }

    public void setToken_MatchPhrasePrefix(String token, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("token", token);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setToken_Fuzzy(String token) {
        setToken_Fuzzy(token, null);
    }

    public void setToken_Fuzzy(String token, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("token", token);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setToken_Prefix(String token) {
        setToken_Prefix(token, null);
    }

    public void setToken_Prefix(String token, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("token", token);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setToken_Wildcard(String token) {
        setToken_Wildcard(token, null);
    }

    public void setToken_Wildcard(String token, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("token", token);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setToken_Regexp(String token) {
        setToken_Regexp(token, null);
    }

    public void setToken_Regexp(String token, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("token", token);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setToken_SpanTerm(String token) {
        setToken_SpanTerm("token", null);
    }

    public void setToken_SpanTerm(String token, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("token", token);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setToken_GreaterThan(String token) {
        setToken_GreaterThan(token, null);
    }

    public void setToken_GreaterThan(String token, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = token;
        RangeQueryBuilder builder = regRangeQ("token", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setToken_LessThan(String token) {
        setToken_LessThan(token, null);
    }

    public void setToken_LessThan(String token, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = token;
        RangeQueryBuilder builder = regRangeQ("token", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setToken_GreaterEqual(String token) {
        setToken_GreaterEqual(token, null);
    }

    public void setToken_GreaterEqual(String token, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = token;
        RangeQueryBuilder builder = regRangeQ("token", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setToken_LessEqual(String token) {
        setToken_LessEqual(token, null);
    }

    public void setToken_LessEqual(String token, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = token;
        RangeQueryBuilder builder = regRangeQ("token", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setToken_Exists() {
        setToken_Exists(null);
    }

    public void setToken_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("token");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setToken_CommonTerms(String token) {
        setToken_CommonTerms(token, null);
    }

    @Deprecated
    public void setToken_CommonTerms(String token, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("token", token);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsAccessTokenCQ addOrderBy_Token_Asc() {
        regOBA("token");
        return this;
    }

    public BsAccessTokenCQ addOrderBy_Token_Desc() {
        regOBD("token");
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

    public BsAccessTokenCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("updatedBy");
        return this;
    }

    public BsAccessTokenCQ addOrderBy_UpdatedBy_Desc() {
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

    public BsAccessTokenCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("updatedTime");
        return this;
    }

    public BsAccessTokenCQ addOrderBy_UpdatedTime_Desc() {
        regOBD("updatedTime");
        return this;
    }

}
