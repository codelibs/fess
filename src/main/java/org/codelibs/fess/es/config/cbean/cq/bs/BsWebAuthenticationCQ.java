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
import org.codelibs.fess.es.config.cbean.cq.WebAuthenticationCQ;
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
public abstract class BsWebAuthenticationCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "web_authentication";
    }

    @Override
    public String xgetAliasName() {
        return "web_authentication";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<WebAuthenticationCQ> queryLambda,
            ScoreFunctionCall<ScoreFunctionCreator<WebAuthenticationCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        WebAuthenticationCQ cq = new WebAuthenticationCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                WebAuthenticationCQ cf = new WebAuthenticationCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<WebAuthenticationCQ, WebAuthenticationCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<WebAuthenticationCQ, WebAuthenticationCQ> filteredLambda,
            ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<WebAuthenticationCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<WebAuthenticationCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<WebAuthenticationCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<WebAuthenticationCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        WebAuthenticationCQ mustQuery = new WebAuthenticationCQ();
        WebAuthenticationCQ shouldQuery = new WebAuthenticationCQ();
        WebAuthenticationCQ mustNotQuery = new WebAuthenticationCQ();
        WebAuthenticationCQ filterQuery = new WebAuthenticationCQ();
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
    public BsWebAuthenticationCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsWebAuthenticationCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setAuthRealm_Equal(String authRealm) {
        setAuthRealm_Term(authRealm, null);
    }

    public void setAuthRealm_Equal(String authRealm, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setAuthRealm_Term(authRealm, opLambda);
    }

    public void setAuthRealm_Term(String authRealm) {
        setAuthRealm_Term(authRealm, null);
    }

    public void setAuthRealm_Term(String authRealm, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("authRealm", authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_NotEqual(String authRealm) {
        setAuthRealm_NotTerm(authRealm, null);
    }

    public void setAuthRealm_NotTerm(String authRealm) {
        setAuthRealm_NotTerm(authRealm, null);
    }

    public void setAuthRealm_NotEqual(String authRealm, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setAuthRealm_NotTerm(authRealm, opLambda);
    }

    public void setAuthRealm_NotTerm(String authRealm, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setAuthRealm_Term(authRealm), opLambda);
    }

    public void setAuthRealm_Terms(Collection<String> authRealmList) {
        setAuthRealm_Terms(authRealmList, null);
    }

    public void setAuthRealm_Terms(Collection<String> authRealmList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("authRealm", authRealmList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_InScope(Collection<String> authRealmList) {
        setAuthRealm_Terms(authRealmList, null);
    }

    public void setAuthRealm_InScope(Collection<String> authRealmList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setAuthRealm_Terms(authRealmList, opLambda);
    }

    public void setAuthRealm_Match(String authRealm) {
        setAuthRealm_Match(authRealm, null);
    }

    public void setAuthRealm_Match(String authRealm, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("authRealm", authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_MatchPhrase(String authRealm) {
        setAuthRealm_MatchPhrase(authRealm, null);
    }

    public void setAuthRealm_MatchPhrase(String authRealm, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("authRealm", authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_MatchPhrasePrefix(String authRealm) {
        setAuthRealm_MatchPhrasePrefix(authRealm, null);
    }

    public void setAuthRealm_MatchPhrasePrefix(String authRealm, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("authRealm", authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_Fuzzy(String authRealm) {
        setAuthRealm_Fuzzy(authRealm, null);
    }

    public void setAuthRealm_Fuzzy(String authRealm, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("authRealm", authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_Prefix(String authRealm) {
        setAuthRealm_Prefix(authRealm, null);
    }

    public void setAuthRealm_Prefix(String authRealm, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("authRealm", authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_Wildcard(String authRealm) {
        setAuthRealm_Wildcard(authRealm, null);
    }

    public void setAuthRealm_Wildcard(String authRealm, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("authRealm", authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_Regexp(String authRealm) {
        setAuthRealm_Regexp(authRealm, null);
    }

    public void setAuthRealm_Regexp(String authRealm, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("authRealm", authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_SpanTerm(String authRealm) {
        setAuthRealm_SpanTerm("authRealm", null);
    }

    public void setAuthRealm_SpanTerm(String authRealm, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("authRealm", authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_GreaterThan(String authRealm) {
        setAuthRealm_GreaterThan(authRealm, null);
    }

    public void setAuthRealm_GreaterThan(String authRealm, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = authRealm;
        RangeQueryBuilder builder = regRangeQ("authRealm", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_LessThan(String authRealm) {
        setAuthRealm_LessThan(authRealm, null);
    }

    public void setAuthRealm_LessThan(String authRealm, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = authRealm;
        RangeQueryBuilder builder = regRangeQ("authRealm", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_GreaterEqual(String authRealm) {
        setAuthRealm_GreaterEqual(authRealm, null);
    }

    public void setAuthRealm_GreaterEqual(String authRealm, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = authRealm;
        RangeQueryBuilder builder = regRangeQ("authRealm", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_LessEqual(String authRealm) {
        setAuthRealm_LessEqual(authRealm, null);
    }

    public void setAuthRealm_LessEqual(String authRealm, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = authRealm;
        RangeQueryBuilder builder = regRangeQ("authRealm", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_Exists() {
        setAuthRealm_Exists(null);
    }

    public void setAuthRealm_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("authRealm");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setAuthRealm_CommonTerms(String authRealm) {
        setAuthRealm_CommonTerms(authRealm, null);
    }

    @Deprecated
    public void setAuthRealm_CommonTerms(String authRealm, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("authRealm", authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebAuthenticationCQ addOrderBy_AuthRealm_Asc() {
        regOBA("authRealm");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_AuthRealm_Desc() {
        regOBD("authRealm");
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

    public BsWebAuthenticationCQ addOrderBy_CreatedBy_Asc() {
        regOBA("createdBy");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_CreatedBy_Desc() {
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

    public BsWebAuthenticationCQ addOrderBy_CreatedTime_Asc() {
        regOBA("createdTime");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_CreatedTime_Desc() {
        regOBD("createdTime");
        return this;
    }

    public void setHostname_Equal(String hostname) {
        setHostname_Term(hostname, null);
    }

    public void setHostname_Equal(String hostname, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setHostname_Term(hostname, opLambda);
    }

    public void setHostname_Term(String hostname) {
        setHostname_Term(hostname, null);
    }

    public void setHostname_Term(String hostname, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("hostname", hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_NotEqual(String hostname) {
        setHostname_NotTerm(hostname, null);
    }

    public void setHostname_NotTerm(String hostname) {
        setHostname_NotTerm(hostname, null);
    }

    public void setHostname_NotEqual(String hostname, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setHostname_NotTerm(hostname, opLambda);
    }

    public void setHostname_NotTerm(String hostname, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setHostname_Term(hostname), opLambda);
    }

    public void setHostname_Terms(Collection<String> hostnameList) {
        setHostname_Terms(hostnameList, null);
    }

    public void setHostname_Terms(Collection<String> hostnameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("hostname", hostnameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_InScope(Collection<String> hostnameList) {
        setHostname_Terms(hostnameList, null);
    }

    public void setHostname_InScope(Collection<String> hostnameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setHostname_Terms(hostnameList, opLambda);
    }

    public void setHostname_Match(String hostname) {
        setHostname_Match(hostname, null);
    }

    public void setHostname_Match(String hostname, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("hostname", hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_MatchPhrase(String hostname) {
        setHostname_MatchPhrase(hostname, null);
    }

    public void setHostname_MatchPhrase(String hostname, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("hostname", hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_MatchPhrasePrefix(String hostname) {
        setHostname_MatchPhrasePrefix(hostname, null);
    }

    public void setHostname_MatchPhrasePrefix(String hostname, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("hostname", hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_Fuzzy(String hostname) {
        setHostname_Fuzzy(hostname, null);
    }

    public void setHostname_Fuzzy(String hostname, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("hostname", hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_Prefix(String hostname) {
        setHostname_Prefix(hostname, null);
    }

    public void setHostname_Prefix(String hostname, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("hostname", hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_Wildcard(String hostname) {
        setHostname_Wildcard(hostname, null);
    }

    public void setHostname_Wildcard(String hostname, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("hostname", hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_Regexp(String hostname) {
        setHostname_Regexp(hostname, null);
    }

    public void setHostname_Regexp(String hostname, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("hostname", hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_SpanTerm(String hostname) {
        setHostname_SpanTerm("hostname", null);
    }

    public void setHostname_SpanTerm(String hostname, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("hostname", hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_GreaterThan(String hostname) {
        setHostname_GreaterThan(hostname, null);
    }

    public void setHostname_GreaterThan(String hostname, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = hostname;
        RangeQueryBuilder builder = regRangeQ("hostname", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_LessThan(String hostname) {
        setHostname_LessThan(hostname, null);
    }

    public void setHostname_LessThan(String hostname, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = hostname;
        RangeQueryBuilder builder = regRangeQ("hostname", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_GreaterEqual(String hostname) {
        setHostname_GreaterEqual(hostname, null);
    }

    public void setHostname_GreaterEqual(String hostname, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = hostname;
        RangeQueryBuilder builder = regRangeQ("hostname", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_LessEqual(String hostname) {
        setHostname_LessEqual(hostname, null);
    }

    public void setHostname_LessEqual(String hostname, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = hostname;
        RangeQueryBuilder builder = regRangeQ("hostname", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_Exists() {
        setHostname_Exists(null);
    }

    public void setHostname_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("hostname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setHostname_CommonTerms(String hostname) {
        setHostname_CommonTerms(hostname, null);
    }

    @Deprecated
    public void setHostname_CommonTerms(String hostname, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("hostname", hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebAuthenticationCQ addOrderBy_Hostname_Asc() {
        regOBA("hostname");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_Hostname_Desc() {
        regOBD("hostname");
        return this;
    }

    public void setParameters_Equal(String parameters) {
        setParameters_Term(parameters, null);
    }

    public void setParameters_Equal(String parameters, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setParameters_Term(parameters, opLambda);
    }

    public void setParameters_Term(String parameters) {
        setParameters_Term(parameters, null);
    }

    public void setParameters_Term(String parameters, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("parameters", parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_NotEqual(String parameters) {
        setParameters_NotTerm(parameters, null);
    }

    public void setParameters_NotTerm(String parameters) {
        setParameters_NotTerm(parameters, null);
    }

    public void setParameters_NotEqual(String parameters, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setParameters_NotTerm(parameters, opLambda);
    }

    public void setParameters_NotTerm(String parameters, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setParameters_Term(parameters), opLambda);
    }

    public void setParameters_Terms(Collection<String> parametersList) {
        setParameters_Terms(parametersList, null);
    }

    public void setParameters_Terms(Collection<String> parametersList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("parameters", parametersList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_InScope(Collection<String> parametersList) {
        setParameters_Terms(parametersList, null);
    }

    public void setParameters_InScope(Collection<String> parametersList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setParameters_Terms(parametersList, opLambda);
    }

    public void setParameters_Match(String parameters) {
        setParameters_Match(parameters, null);
    }

    public void setParameters_Match(String parameters, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("parameters", parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_MatchPhrase(String parameters) {
        setParameters_MatchPhrase(parameters, null);
    }

    public void setParameters_MatchPhrase(String parameters, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("parameters", parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_MatchPhrasePrefix(String parameters) {
        setParameters_MatchPhrasePrefix(parameters, null);
    }

    public void setParameters_MatchPhrasePrefix(String parameters, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("parameters", parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_Fuzzy(String parameters) {
        setParameters_Fuzzy(parameters, null);
    }

    public void setParameters_Fuzzy(String parameters, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("parameters", parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_Prefix(String parameters) {
        setParameters_Prefix(parameters, null);
    }

    public void setParameters_Prefix(String parameters, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("parameters", parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_Wildcard(String parameters) {
        setParameters_Wildcard(parameters, null);
    }

    public void setParameters_Wildcard(String parameters, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("parameters", parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_Regexp(String parameters) {
        setParameters_Regexp(parameters, null);
    }

    public void setParameters_Regexp(String parameters, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("parameters", parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_SpanTerm(String parameters) {
        setParameters_SpanTerm("parameters", null);
    }

    public void setParameters_SpanTerm(String parameters, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("parameters", parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_GreaterThan(String parameters) {
        setParameters_GreaterThan(parameters, null);
    }

    public void setParameters_GreaterThan(String parameters, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = parameters;
        RangeQueryBuilder builder = regRangeQ("parameters", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_LessThan(String parameters) {
        setParameters_LessThan(parameters, null);
    }

    public void setParameters_LessThan(String parameters, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = parameters;
        RangeQueryBuilder builder = regRangeQ("parameters", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_GreaterEqual(String parameters) {
        setParameters_GreaterEqual(parameters, null);
    }

    public void setParameters_GreaterEqual(String parameters, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = parameters;
        RangeQueryBuilder builder = regRangeQ("parameters", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_LessEqual(String parameters) {
        setParameters_LessEqual(parameters, null);
    }

    public void setParameters_LessEqual(String parameters, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = parameters;
        RangeQueryBuilder builder = regRangeQ("parameters", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_Exists() {
        setParameters_Exists(null);
    }

    public void setParameters_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("parameters");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setParameters_CommonTerms(String parameters) {
        setParameters_CommonTerms(parameters, null);
    }

    @Deprecated
    public void setParameters_CommonTerms(String parameters, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("parameters", parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebAuthenticationCQ addOrderBy_Parameters_Asc() {
        regOBA("parameters");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_Parameters_Desc() {
        regOBD("parameters");
        return this;
    }

    public void setPassword_Equal(String password) {
        setPassword_Term(password, null);
    }

    public void setPassword_Equal(String password, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setPassword_Term(password, opLambda);
    }

    public void setPassword_Term(String password) {
        setPassword_Term(password, null);
    }

    public void setPassword_Term(String password, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_NotEqual(String password) {
        setPassword_NotTerm(password, null);
    }

    public void setPassword_NotTerm(String password) {
        setPassword_NotTerm(password, null);
    }

    public void setPassword_NotEqual(String password, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setPassword_NotTerm(password, opLambda);
    }

    public void setPassword_NotTerm(String password, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setPassword_Term(password), opLambda);
    }

    public void setPassword_Terms(Collection<String> passwordList) {
        setPassword_Terms(passwordList, null);
    }

    public void setPassword_Terms(Collection<String> passwordList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("password", passwordList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_InScope(Collection<String> passwordList) {
        setPassword_Terms(passwordList, null);
    }

    public void setPassword_InScope(Collection<String> passwordList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setPassword_Terms(passwordList, opLambda);
    }

    public void setPassword_Match(String password) {
        setPassword_Match(password, null);
    }

    public void setPassword_Match(String password, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_MatchPhrase(String password) {
        setPassword_MatchPhrase(password, null);
    }

    public void setPassword_MatchPhrase(String password, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_MatchPhrasePrefix(String password) {
        setPassword_MatchPhrasePrefix(password, null);
    }

    public void setPassword_MatchPhrasePrefix(String password, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Fuzzy(String password) {
        setPassword_Fuzzy(password, null);
    }

    public void setPassword_Fuzzy(String password, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Prefix(String password) {
        setPassword_Prefix(password, null);
    }

    public void setPassword_Prefix(String password, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Wildcard(String password) {
        setPassword_Wildcard(password, null);
    }

    public void setPassword_Wildcard(String password, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Regexp(String password) {
        setPassword_Regexp(password, null);
    }

    public void setPassword_Regexp(String password, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_SpanTerm(String password) {
        setPassword_SpanTerm("password", null);
    }

    public void setPassword_SpanTerm(String password, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_GreaterThan(String password) {
        setPassword_GreaterThan(password, null);
    }

    public void setPassword_GreaterThan(String password, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = password;
        RangeQueryBuilder builder = regRangeQ("password", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_LessThan(String password) {
        setPassword_LessThan(password, null);
    }

    public void setPassword_LessThan(String password, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = password;
        RangeQueryBuilder builder = regRangeQ("password", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_GreaterEqual(String password) {
        setPassword_GreaterEqual(password, null);
    }

    public void setPassword_GreaterEqual(String password, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = password;
        RangeQueryBuilder builder = regRangeQ("password", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_LessEqual(String password) {
        setPassword_LessEqual(password, null);
    }

    public void setPassword_LessEqual(String password, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = password;
        RangeQueryBuilder builder = regRangeQ("password", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Exists() {
        setPassword_Exists(null);
    }

    public void setPassword_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setPassword_CommonTerms(String password) {
        setPassword_CommonTerms(password, null);
    }

    @Deprecated
    public void setPassword_CommonTerms(String password, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebAuthenticationCQ addOrderBy_Password_Asc() {
        regOBA("password");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_Password_Desc() {
        regOBD("password");
        return this;
    }

    public void setPort_Equal(Integer port) {
        setPort_Term(port, null);
    }

    public void setPort_Equal(Integer port, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setPort_Term(port, opLambda);
    }

    public void setPort_Term(Integer port) {
        setPort_Term(port, null);
    }

    public void setPort_Term(Integer port, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("port", port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_NotEqual(Integer port) {
        setPort_NotTerm(port, null);
    }

    public void setPort_NotTerm(Integer port) {
        setPort_NotTerm(port, null);
    }

    public void setPort_NotEqual(Integer port, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setPort_NotTerm(port, opLambda);
    }

    public void setPort_NotTerm(Integer port, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setPort_Term(port), opLambda);
    }

    public void setPort_Terms(Collection<Integer> portList) {
        setPort_Terms(portList, null);
    }

    public void setPort_Terms(Collection<Integer> portList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("port", portList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_InScope(Collection<Integer> portList) {
        setPort_Terms(portList, null);
    }

    public void setPort_InScope(Collection<Integer> portList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setPort_Terms(portList, opLambda);
    }

    public void setPort_Match(Integer port) {
        setPort_Match(port, null);
    }

    public void setPort_Match(Integer port, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("port", port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_MatchPhrase(Integer port) {
        setPort_MatchPhrase(port, null);
    }

    public void setPort_MatchPhrase(Integer port, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("port", port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_MatchPhrasePrefix(Integer port) {
        setPort_MatchPhrasePrefix(port, null);
    }

    public void setPort_MatchPhrasePrefix(Integer port, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("port", port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Fuzzy(Integer port) {
        setPort_Fuzzy(port, null);
    }

    public void setPort_Fuzzy(Integer port, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("port", port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_GreaterThan(Integer port) {
        setPort_GreaterThan(port, null);
    }

    public void setPort_GreaterThan(Integer port, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = port;
        RangeQueryBuilder builder = regRangeQ("port", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_LessThan(Integer port) {
        setPort_LessThan(port, null);
    }

    public void setPort_LessThan(Integer port, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = port;
        RangeQueryBuilder builder = regRangeQ("port", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_GreaterEqual(Integer port) {
        setPort_GreaterEqual(port, null);
    }

    public void setPort_GreaterEqual(Integer port, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = port;
        RangeQueryBuilder builder = regRangeQ("port", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_LessEqual(Integer port) {
        setPort_LessEqual(port, null);
    }

    public void setPort_LessEqual(Integer port, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = port;
        RangeQueryBuilder builder = regRangeQ("port", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Exists() {
        setPort_Exists(null);
    }

    public void setPort_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setPort_CommonTerms(Integer port) {
        setPort_CommonTerms(port, null);
    }

    @Deprecated
    public void setPort_CommonTerms(Integer port, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("port", port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebAuthenticationCQ addOrderBy_Port_Asc() {
        regOBA("port");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_Port_Desc() {
        regOBD("port");
        return this;
    }

    public void setProtocolScheme_Equal(String protocolScheme) {
        setProtocolScheme_Term(protocolScheme, null);
    }

    public void setProtocolScheme_Equal(String protocolScheme, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setProtocolScheme_Term(protocolScheme, opLambda);
    }

    public void setProtocolScheme_Term(String protocolScheme) {
        setProtocolScheme_Term(protocolScheme, null);
    }

    public void setProtocolScheme_Term(String protocolScheme, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("protocolScheme", protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_NotEqual(String protocolScheme) {
        setProtocolScheme_NotTerm(protocolScheme, null);
    }

    public void setProtocolScheme_NotTerm(String protocolScheme) {
        setProtocolScheme_NotTerm(protocolScheme, null);
    }

    public void setProtocolScheme_NotEqual(String protocolScheme, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setProtocolScheme_NotTerm(protocolScheme, opLambda);
    }

    public void setProtocolScheme_NotTerm(String protocolScheme, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setProtocolScheme_Term(protocolScheme), opLambda);
    }

    public void setProtocolScheme_Terms(Collection<String> protocolSchemeList) {
        setProtocolScheme_Terms(protocolSchemeList, null);
    }

    public void setProtocolScheme_Terms(Collection<String> protocolSchemeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("protocolScheme", protocolSchemeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_InScope(Collection<String> protocolSchemeList) {
        setProtocolScheme_Terms(protocolSchemeList, null);
    }

    public void setProtocolScheme_InScope(Collection<String> protocolSchemeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setProtocolScheme_Terms(protocolSchemeList, opLambda);
    }

    public void setProtocolScheme_Match(String protocolScheme) {
        setProtocolScheme_Match(protocolScheme, null);
    }

    public void setProtocolScheme_Match(String protocolScheme, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("protocolScheme", protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_MatchPhrase(String protocolScheme) {
        setProtocolScheme_MatchPhrase(protocolScheme, null);
    }

    public void setProtocolScheme_MatchPhrase(String protocolScheme, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("protocolScheme", protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_MatchPhrasePrefix(String protocolScheme) {
        setProtocolScheme_MatchPhrasePrefix(protocolScheme, null);
    }

    public void setProtocolScheme_MatchPhrasePrefix(String protocolScheme, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("protocolScheme", protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_Fuzzy(String protocolScheme) {
        setProtocolScheme_Fuzzy(protocolScheme, null);
    }

    public void setProtocolScheme_Fuzzy(String protocolScheme, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("protocolScheme", protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_Prefix(String protocolScheme) {
        setProtocolScheme_Prefix(protocolScheme, null);
    }

    public void setProtocolScheme_Prefix(String protocolScheme, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("protocolScheme", protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_Wildcard(String protocolScheme) {
        setProtocolScheme_Wildcard(protocolScheme, null);
    }

    public void setProtocolScheme_Wildcard(String protocolScheme, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("protocolScheme", protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_Regexp(String protocolScheme) {
        setProtocolScheme_Regexp(protocolScheme, null);
    }

    public void setProtocolScheme_Regexp(String protocolScheme, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("protocolScheme", protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_SpanTerm(String protocolScheme) {
        setProtocolScheme_SpanTerm("protocolScheme", null);
    }

    public void setProtocolScheme_SpanTerm(String protocolScheme, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("protocolScheme", protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_GreaterThan(String protocolScheme) {
        setProtocolScheme_GreaterThan(protocolScheme, null);
    }

    public void setProtocolScheme_GreaterThan(String protocolScheme, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = protocolScheme;
        RangeQueryBuilder builder = regRangeQ("protocolScheme", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_LessThan(String protocolScheme) {
        setProtocolScheme_LessThan(protocolScheme, null);
    }

    public void setProtocolScheme_LessThan(String protocolScheme, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = protocolScheme;
        RangeQueryBuilder builder = regRangeQ("protocolScheme", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_GreaterEqual(String protocolScheme) {
        setProtocolScheme_GreaterEqual(protocolScheme, null);
    }

    public void setProtocolScheme_GreaterEqual(String protocolScheme, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = protocolScheme;
        RangeQueryBuilder builder = regRangeQ("protocolScheme", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_LessEqual(String protocolScheme) {
        setProtocolScheme_LessEqual(protocolScheme, null);
    }

    public void setProtocolScheme_LessEqual(String protocolScheme, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = protocolScheme;
        RangeQueryBuilder builder = regRangeQ("protocolScheme", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_Exists() {
        setProtocolScheme_Exists(null);
    }

    public void setProtocolScheme_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("protocolScheme");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setProtocolScheme_CommonTerms(String protocolScheme) {
        setProtocolScheme_CommonTerms(protocolScheme, null);
    }

    @Deprecated
    public void setProtocolScheme_CommonTerms(String protocolScheme, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("protocolScheme", protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebAuthenticationCQ addOrderBy_ProtocolScheme_Asc() {
        regOBA("protocolScheme");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_ProtocolScheme_Desc() {
        regOBD("protocolScheme");
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

    public BsWebAuthenticationCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("updatedBy");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_UpdatedBy_Desc() {
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

    public BsWebAuthenticationCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("updatedTime");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_UpdatedTime_Desc() {
        regOBD("updatedTime");
        return this;
    }

    public void setUsername_Equal(String username) {
        setUsername_Term(username, null);
    }

    public void setUsername_Equal(String username, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUsername_Term(username, opLambda);
    }

    public void setUsername_Term(String username) {
        setUsername_Term(username, null);
    }

    public void setUsername_Term(String username, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("username", username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_NotEqual(String username) {
        setUsername_NotTerm(username, null);
    }

    public void setUsername_NotTerm(String username) {
        setUsername_NotTerm(username, null);
    }

    public void setUsername_NotEqual(String username, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setUsername_NotTerm(username, opLambda);
    }

    public void setUsername_NotTerm(String username, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setUsername_Term(username), opLambda);
    }

    public void setUsername_Terms(Collection<String> usernameList) {
        setUsername_Terms(usernameList, null);
    }

    public void setUsername_Terms(Collection<String> usernameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("username", usernameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_InScope(Collection<String> usernameList) {
        setUsername_Terms(usernameList, null);
    }

    public void setUsername_InScope(Collection<String> usernameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUsername_Terms(usernameList, opLambda);
    }

    public void setUsername_Match(String username) {
        setUsername_Match(username, null);
    }

    public void setUsername_Match(String username, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("username", username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_MatchPhrase(String username) {
        setUsername_MatchPhrase(username, null);
    }

    public void setUsername_MatchPhrase(String username, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("username", username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_MatchPhrasePrefix(String username) {
        setUsername_MatchPhrasePrefix(username, null);
    }

    public void setUsername_MatchPhrasePrefix(String username, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("username", username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_Fuzzy(String username) {
        setUsername_Fuzzy(username, null);
    }

    public void setUsername_Fuzzy(String username, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("username", username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_Prefix(String username) {
        setUsername_Prefix(username, null);
    }

    public void setUsername_Prefix(String username, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("username", username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_Wildcard(String username) {
        setUsername_Wildcard(username, null);
    }

    public void setUsername_Wildcard(String username, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("username", username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_Regexp(String username) {
        setUsername_Regexp(username, null);
    }

    public void setUsername_Regexp(String username, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("username", username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_SpanTerm(String username) {
        setUsername_SpanTerm("username", null);
    }

    public void setUsername_SpanTerm(String username, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("username", username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_GreaterThan(String username) {
        setUsername_GreaterThan(username, null);
    }

    public void setUsername_GreaterThan(String username, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = username;
        RangeQueryBuilder builder = regRangeQ("username", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_LessThan(String username) {
        setUsername_LessThan(username, null);
    }

    public void setUsername_LessThan(String username, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = username;
        RangeQueryBuilder builder = regRangeQ("username", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_GreaterEqual(String username) {
        setUsername_GreaterEqual(username, null);
    }

    public void setUsername_GreaterEqual(String username, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = username;
        RangeQueryBuilder builder = regRangeQ("username", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_LessEqual(String username) {
        setUsername_LessEqual(username, null);
    }

    public void setUsername_LessEqual(String username, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = username;
        RangeQueryBuilder builder = regRangeQ("username", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_Exists() {
        setUsername_Exists(null);
    }

    public void setUsername_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("username");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setUsername_CommonTerms(String username) {
        setUsername_CommonTerms(username, null);
    }

    @Deprecated
    public void setUsername_CommonTerms(String username, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("username", username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebAuthenticationCQ addOrderBy_Username_Asc() {
        regOBA("username");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_Username_Desc() {
        regOBD("username");
        return this;
    }

    public void setWebConfigId_Equal(String webConfigId) {
        setWebConfigId_Term(webConfigId, null);
    }

    public void setWebConfigId_Equal(String webConfigId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setWebConfigId_Term(webConfigId, opLambda);
    }

    public void setWebConfigId_Term(String webConfigId) {
        setWebConfigId_Term(webConfigId, null);
    }

    public void setWebConfigId_Term(String webConfigId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_NotEqual(String webConfigId) {
        setWebConfigId_NotTerm(webConfigId, null);
    }

    public void setWebConfigId_NotTerm(String webConfigId) {
        setWebConfigId_NotTerm(webConfigId, null);
    }

    public void setWebConfigId_NotEqual(String webConfigId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setWebConfigId_NotTerm(webConfigId, opLambda);
    }

    public void setWebConfigId_NotTerm(String webConfigId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setWebConfigId_Term(webConfigId), opLambda);
    }

    public void setWebConfigId_Terms(Collection<String> webConfigIdList) {
        setWebConfigId_Terms(webConfigIdList, null);
    }

    public void setWebConfigId_Terms(Collection<String> webConfigIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("webConfigId", webConfigIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_InScope(Collection<String> webConfigIdList) {
        setWebConfigId_Terms(webConfigIdList, null);
    }

    public void setWebConfigId_InScope(Collection<String> webConfigIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setWebConfigId_Terms(webConfigIdList, opLambda);
    }

    public void setWebConfigId_Match(String webConfigId) {
        setWebConfigId_Match(webConfigId, null);
    }

    public void setWebConfigId_Match(String webConfigId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_MatchPhrase(String webConfigId) {
        setWebConfigId_MatchPhrase(webConfigId, null);
    }

    public void setWebConfigId_MatchPhrase(String webConfigId, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_MatchPhrasePrefix(String webConfigId) {
        setWebConfigId_MatchPhrasePrefix(webConfigId, null);
    }

    public void setWebConfigId_MatchPhrasePrefix(String webConfigId, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Fuzzy(String webConfigId) {
        setWebConfigId_Fuzzy(webConfigId, null);
    }

    public void setWebConfigId_Fuzzy(String webConfigId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Prefix(String webConfigId) {
        setWebConfigId_Prefix(webConfigId, null);
    }

    public void setWebConfigId_Prefix(String webConfigId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Wildcard(String webConfigId) {
        setWebConfigId_Wildcard(webConfigId, null);
    }

    public void setWebConfigId_Wildcard(String webConfigId, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Regexp(String webConfigId) {
        setWebConfigId_Regexp(webConfigId, null);
    }

    public void setWebConfigId_Regexp(String webConfigId, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_SpanTerm(String webConfigId) {
        setWebConfigId_SpanTerm("webConfigId", null);
    }

    public void setWebConfigId_SpanTerm(String webConfigId, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_GreaterThan(String webConfigId) {
        setWebConfigId_GreaterThan(webConfigId, null);
    }

    public void setWebConfigId_GreaterThan(String webConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = webConfigId;
        RangeQueryBuilder builder = regRangeQ("webConfigId", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_LessThan(String webConfigId) {
        setWebConfigId_LessThan(webConfigId, null);
    }

    public void setWebConfigId_LessThan(String webConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = webConfigId;
        RangeQueryBuilder builder = regRangeQ("webConfigId", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_GreaterEqual(String webConfigId) {
        setWebConfigId_GreaterEqual(webConfigId, null);
    }

    public void setWebConfigId_GreaterEqual(String webConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = webConfigId;
        RangeQueryBuilder builder = regRangeQ("webConfigId", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_LessEqual(String webConfigId) {
        setWebConfigId_LessEqual(webConfigId, null);
    }

    public void setWebConfigId_LessEqual(String webConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = webConfigId;
        RangeQueryBuilder builder = regRangeQ("webConfigId", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Exists() {
        setWebConfigId_Exists(null);
    }

    public void setWebConfigId_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("webConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setWebConfigId_CommonTerms(String webConfigId) {
        setWebConfigId_CommonTerms(webConfigId, null);
    }

    @Deprecated
    public void setWebConfigId_CommonTerms(String webConfigId, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebAuthenticationCQ addOrderBy_WebConfigId_Asc() {
        regOBA("webConfigId");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_WebConfigId_Desc() {
        regOBD("webConfigId");
        return this;
    }

}
