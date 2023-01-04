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
import org.codelibs.fess.es.log.cbean.cq.SearchLogCQ;
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
public abstract class BsSearchLogCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "search_log";
    }

    @Override
    public String xgetAliasName() {
        return "search_log";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<SearchLogCQ> queryLambda, ScoreFunctionCall<ScoreFunctionCreator<SearchLogCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        SearchLogCQ cq = new SearchLogCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                SearchLogCQ cf = new SearchLogCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<SearchLogCQ, SearchLogCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<SearchLogCQ, SearchLogCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<SearchLogCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<SearchLogCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<SearchLogCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<SearchLogCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        SearchLogCQ mustQuery = new SearchLogCQ();
        SearchLogCQ shouldQuery = new SearchLogCQ();
        SearchLogCQ mustNotQuery = new SearchLogCQ();
        SearchLogCQ filterQuery = new SearchLogCQ();
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
    public BsSearchLogCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsSearchLogCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setAccessType_Equal(String accessType) {
        setAccessType_Term(accessType, null);
    }

    public void setAccessType_Equal(String accessType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setAccessType_Term(accessType, opLambda);
    }

    public void setAccessType_Term(String accessType) {
        setAccessType_Term(accessType, null);
    }

    public void setAccessType_Term(String accessType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_NotEqual(String accessType) {
        setAccessType_NotTerm(accessType, null);
    }

    public void setAccessType_NotTerm(String accessType) {
        setAccessType_NotTerm(accessType, null);
    }

    public void setAccessType_NotEqual(String accessType, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setAccessType_NotTerm(accessType, opLambda);
    }

    public void setAccessType_NotTerm(String accessType, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setAccessType_Term(accessType), opLambda);
    }

    public void setAccessType_Terms(Collection<String> accessTypeList) {
        setAccessType_Terms(accessTypeList, null);
    }

    public void setAccessType_Terms(Collection<String> accessTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("accessType", accessTypeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_InScope(Collection<String> accessTypeList) {
        setAccessType_Terms(accessTypeList, null);
    }

    public void setAccessType_InScope(Collection<String> accessTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setAccessType_Terms(accessTypeList, opLambda);
    }

    public void setAccessType_Match(String accessType) {
        setAccessType_Match(accessType, null);
    }

    public void setAccessType_Match(String accessType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_MatchPhrase(String accessType) {
        setAccessType_MatchPhrase(accessType, null);
    }

    public void setAccessType_MatchPhrase(String accessType, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_MatchPhrasePrefix(String accessType) {
        setAccessType_MatchPhrasePrefix(accessType, null);
    }

    public void setAccessType_MatchPhrasePrefix(String accessType, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Fuzzy(String accessType) {
        setAccessType_Fuzzy(accessType, null);
    }

    public void setAccessType_Fuzzy(String accessType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Prefix(String accessType) {
        setAccessType_Prefix(accessType, null);
    }

    public void setAccessType_Prefix(String accessType, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Wildcard(String accessType) {
        setAccessType_Wildcard(accessType, null);
    }

    public void setAccessType_Wildcard(String accessType, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Regexp(String accessType) {
        setAccessType_Regexp(accessType, null);
    }

    public void setAccessType_Regexp(String accessType, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_SpanTerm(String accessType) {
        setAccessType_SpanTerm("accessType", null);
    }

    public void setAccessType_SpanTerm(String accessType, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_GreaterThan(String accessType) {
        setAccessType_GreaterThan(accessType, null);
    }

    public void setAccessType_GreaterThan(String accessType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = accessType;
        RangeQueryBuilder builder = regRangeQ("accessType", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_LessThan(String accessType) {
        setAccessType_LessThan(accessType, null);
    }

    public void setAccessType_LessThan(String accessType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = accessType;
        RangeQueryBuilder builder = regRangeQ("accessType", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_GreaterEqual(String accessType) {
        setAccessType_GreaterEqual(accessType, null);
    }

    public void setAccessType_GreaterEqual(String accessType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = accessType;
        RangeQueryBuilder builder = regRangeQ("accessType", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_LessEqual(String accessType) {
        setAccessType_LessEqual(accessType, null);
    }

    public void setAccessType_LessEqual(String accessType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = accessType;
        RangeQueryBuilder builder = regRangeQ("accessType", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Exists() {
        setAccessType_Exists(null);
    }

    public void setAccessType_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("accessType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setAccessType_CommonTerms(String accessType) {
        setAccessType_CommonTerms(accessType, null);
    }

    @Deprecated
    public void setAccessType_CommonTerms(String accessType, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_AccessType_Asc() {
        regOBA("accessType");
        return this;
    }

    public BsSearchLogCQ addOrderBy_AccessType_Desc() {
        regOBD("accessType");
        return this;
    }

    public void setClientIp_Equal(String clientIp) {
        setClientIp_Term(clientIp, null);
    }

    public void setClientIp_Equal(String clientIp, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setClientIp_Term(clientIp, opLambda);
    }

    public void setClientIp_Term(String clientIp) {
        setClientIp_Term(clientIp, null);
    }

    public void setClientIp_Term(String clientIp, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("clientIp", clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_NotEqual(String clientIp) {
        setClientIp_NotTerm(clientIp, null);
    }

    public void setClientIp_NotTerm(String clientIp) {
        setClientIp_NotTerm(clientIp, null);
    }

    public void setClientIp_NotEqual(String clientIp, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setClientIp_NotTerm(clientIp, opLambda);
    }

    public void setClientIp_NotTerm(String clientIp, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setClientIp_Term(clientIp), opLambda);
    }

    public void setClientIp_Terms(Collection<String> clientIpList) {
        setClientIp_Terms(clientIpList, null);
    }

    public void setClientIp_Terms(Collection<String> clientIpList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("clientIp", clientIpList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_InScope(Collection<String> clientIpList) {
        setClientIp_Terms(clientIpList, null);
    }

    public void setClientIp_InScope(Collection<String> clientIpList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setClientIp_Terms(clientIpList, opLambda);
    }

    public void setClientIp_Match(String clientIp) {
        setClientIp_Match(clientIp, null);
    }

    public void setClientIp_Match(String clientIp, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("clientIp", clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_MatchPhrase(String clientIp) {
        setClientIp_MatchPhrase(clientIp, null);
    }

    public void setClientIp_MatchPhrase(String clientIp, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("clientIp", clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_MatchPhrasePrefix(String clientIp) {
        setClientIp_MatchPhrasePrefix(clientIp, null);
    }

    public void setClientIp_MatchPhrasePrefix(String clientIp, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("clientIp", clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_Fuzzy(String clientIp) {
        setClientIp_Fuzzy(clientIp, null);
    }

    public void setClientIp_Fuzzy(String clientIp, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("clientIp", clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_Prefix(String clientIp) {
        setClientIp_Prefix(clientIp, null);
    }

    public void setClientIp_Prefix(String clientIp, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("clientIp", clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_Wildcard(String clientIp) {
        setClientIp_Wildcard(clientIp, null);
    }

    public void setClientIp_Wildcard(String clientIp, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("clientIp", clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_Regexp(String clientIp) {
        setClientIp_Regexp(clientIp, null);
    }

    public void setClientIp_Regexp(String clientIp, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("clientIp", clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_SpanTerm(String clientIp) {
        setClientIp_SpanTerm("clientIp", null);
    }

    public void setClientIp_SpanTerm(String clientIp, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("clientIp", clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_GreaterThan(String clientIp) {
        setClientIp_GreaterThan(clientIp, null);
    }

    public void setClientIp_GreaterThan(String clientIp, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = clientIp;
        RangeQueryBuilder builder = regRangeQ("clientIp", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_LessThan(String clientIp) {
        setClientIp_LessThan(clientIp, null);
    }

    public void setClientIp_LessThan(String clientIp, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = clientIp;
        RangeQueryBuilder builder = regRangeQ("clientIp", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_GreaterEqual(String clientIp) {
        setClientIp_GreaterEqual(clientIp, null);
    }

    public void setClientIp_GreaterEqual(String clientIp, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = clientIp;
        RangeQueryBuilder builder = regRangeQ("clientIp", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_LessEqual(String clientIp) {
        setClientIp_LessEqual(clientIp, null);
    }

    public void setClientIp_LessEqual(String clientIp, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = clientIp;
        RangeQueryBuilder builder = regRangeQ("clientIp", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_Exists() {
        setClientIp_Exists(null);
    }

    public void setClientIp_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("clientIp");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setClientIp_CommonTerms(String clientIp) {
        setClientIp_CommonTerms(clientIp, null);
    }

    @Deprecated
    public void setClientIp_CommonTerms(String clientIp, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("clientIp", clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_ClientIp_Asc() {
        regOBA("clientIp");
        return this;
    }

    public BsSearchLogCQ addOrderBy_ClientIp_Desc() {
        regOBD("clientIp");
        return this;
    }

    public void setHitCount_Equal(Long hitCount) {
        setHitCount_Term(hitCount, null);
    }

    public void setHitCount_Equal(Long hitCount, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setHitCount_Term(hitCount, opLambda);
    }

    public void setHitCount_Term(Long hitCount) {
        setHitCount_Term(hitCount, null);
    }

    public void setHitCount_Term(Long hitCount, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("hitCount", hitCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_NotEqual(Long hitCount) {
        setHitCount_NotTerm(hitCount, null);
    }

    public void setHitCount_NotTerm(Long hitCount) {
        setHitCount_NotTerm(hitCount, null);
    }

    public void setHitCount_NotEqual(Long hitCount, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setHitCount_NotTerm(hitCount, opLambda);
    }

    public void setHitCount_NotTerm(Long hitCount, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setHitCount_Term(hitCount), opLambda);
    }

    public void setHitCount_Terms(Collection<Long> hitCountList) {
        setHitCount_Terms(hitCountList, null);
    }

    public void setHitCount_Terms(Collection<Long> hitCountList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("hitCount", hitCountList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_InScope(Collection<Long> hitCountList) {
        setHitCount_Terms(hitCountList, null);
    }

    public void setHitCount_InScope(Collection<Long> hitCountList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setHitCount_Terms(hitCountList, opLambda);
    }

    public void setHitCount_Match(Long hitCount) {
        setHitCount_Match(hitCount, null);
    }

    public void setHitCount_Match(Long hitCount, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("hitCount", hitCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_MatchPhrase(Long hitCount) {
        setHitCount_MatchPhrase(hitCount, null);
    }

    public void setHitCount_MatchPhrase(Long hitCount, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("hitCount", hitCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_MatchPhrasePrefix(Long hitCount) {
        setHitCount_MatchPhrasePrefix(hitCount, null);
    }

    public void setHitCount_MatchPhrasePrefix(Long hitCount, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("hitCount", hitCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Fuzzy(Long hitCount) {
        setHitCount_Fuzzy(hitCount, null);
    }

    public void setHitCount_Fuzzy(Long hitCount, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("hitCount", hitCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_GreaterThan(Long hitCount) {
        setHitCount_GreaterThan(hitCount, null);
    }

    public void setHitCount_GreaterThan(Long hitCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = hitCount;
        RangeQueryBuilder builder = regRangeQ("hitCount", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_LessThan(Long hitCount) {
        setHitCount_LessThan(hitCount, null);
    }

    public void setHitCount_LessThan(Long hitCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = hitCount;
        RangeQueryBuilder builder = regRangeQ("hitCount", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_GreaterEqual(Long hitCount) {
        setHitCount_GreaterEqual(hitCount, null);
    }

    public void setHitCount_GreaterEqual(Long hitCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = hitCount;
        RangeQueryBuilder builder = regRangeQ("hitCount", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_LessEqual(Long hitCount) {
        setHitCount_LessEqual(hitCount, null);
    }

    public void setHitCount_LessEqual(Long hitCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = hitCount;
        RangeQueryBuilder builder = regRangeQ("hitCount", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Exists() {
        setHitCount_Exists(null);
    }

    public void setHitCount_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setHitCount_CommonTerms(Long hitCount) {
        setHitCount_CommonTerms(hitCount, null);
    }

    @Deprecated
    public void setHitCount_CommonTerms(Long hitCount, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("hitCount", hitCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_HitCount_Asc() {
        regOBA("hitCount");
        return this;
    }

    public BsSearchLogCQ addOrderBy_HitCount_Desc() {
        regOBD("hitCount");
        return this;
    }

    public void setHitCountRelation_Equal(String hitCountRelation) {
        setHitCountRelation_Term(hitCountRelation, null);
    }

    public void setHitCountRelation_Equal(String hitCountRelation, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setHitCountRelation_Term(hitCountRelation, opLambda);
    }

    public void setHitCountRelation_Term(String hitCountRelation) {
        setHitCountRelation_Term(hitCountRelation, null);
    }

    public void setHitCountRelation_Term(String hitCountRelation, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("hitCountRelation", hitCountRelation);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCountRelation_NotEqual(String hitCountRelation) {
        setHitCountRelation_NotTerm(hitCountRelation, null);
    }

    public void setHitCountRelation_NotTerm(String hitCountRelation) {
        setHitCountRelation_NotTerm(hitCountRelation, null);
    }

    public void setHitCountRelation_NotEqual(String hitCountRelation, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setHitCountRelation_NotTerm(hitCountRelation, opLambda);
    }

    public void setHitCountRelation_NotTerm(String hitCountRelation, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setHitCountRelation_Term(hitCountRelation), opLambda);
    }

    public void setHitCountRelation_Terms(Collection<String> hitCountRelationList) {
        setHitCountRelation_Terms(hitCountRelationList, null);
    }

    public void setHitCountRelation_Terms(Collection<String> hitCountRelationList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("hitCountRelation", hitCountRelationList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCountRelation_InScope(Collection<String> hitCountRelationList) {
        setHitCountRelation_Terms(hitCountRelationList, null);
    }

    public void setHitCountRelation_InScope(Collection<String> hitCountRelationList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setHitCountRelation_Terms(hitCountRelationList, opLambda);
    }

    public void setHitCountRelation_Match(String hitCountRelation) {
        setHitCountRelation_Match(hitCountRelation, null);
    }

    public void setHitCountRelation_Match(String hitCountRelation, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("hitCountRelation", hitCountRelation);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCountRelation_MatchPhrase(String hitCountRelation) {
        setHitCountRelation_MatchPhrase(hitCountRelation, null);
    }

    public void setHitCountRelation_MatchPhrase(String hitCountRelation, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("hitCountRelation", hitCountRelation);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCountRelation_MatchPhrasePrefix(String hitCountRelation) {
        setHitCountRelation_MatchPhrasePrefix(hitCountRelation, null);
    }

    public void setHitCountRelation_MatchPhrasePrefix(String hitCountRelation,
            ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("hitCountRelation", hitCountRelation);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCountRelation_Fuzzy(String hitCountRelation) {
        setHitCountRelation_Fuzzy(hitCountRelation, null);
    }

    public void setHitCountRelation_Fuzzy(String hitCountRelation, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("hitCountRelation", hitCountRelation);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCountRelation_Prefix(String hitCountRelation) {
        setHitCountRelation_Prefix(hitCountRelation, null);
    }

    public void setHitCountRelation_Prefix(String hitCountRelation, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("hitCountRelation", hitCountRelation);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCountRelation_Wildcard(String hitCountRelation) {
        setHitCountRelation_Wildcard(hitCountRelation, null);
    }

    public void setHitCountRelation_Wildcard(String hitCountRelation, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("hitCountRelation", hitCountRelation);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCountRelation_Regexp(String hitCountRelation) {
        setHitCountRelation_Regexp(hitCountRelation, null);
    }

    public void setHitCountRelation_Regexp(String hitCountRelation, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("hitCountRelation", hitCountRelation);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCountRelation_SpanTerm(String hitCountRelation) {
        setHitCountRelation_SpanTerm("hitCountRelation", null);
    }

    public void setHitCountRelation_SpanTerm(String hitCountRelation, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("hitCountRelation", hitCountRelation);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCountRelation_GreaterThan(String hitCountRelation) {
        setHitCountRelation_GreaterThan(hitCountRelation, null);
    }

    public void setHitCountRelation_GreaterThan(String hitCountRelation, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = hitCountRelation;
        RangeQueryBuilder builder = regRangeQ("hitCountRelation", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCountRelation_LessThan(String hitCountRelation) {
        setHitCountRelation_LessThan(hitCountRelation, null);
    }

    public void setHitCountRelation_LessThan(String hitCountRelation, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = hitCountRelation;
        RangeQueryBuilder builder = regRangeQ("hitCountRelation", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCountRelation_GreaterEqual(String hitCountRelation) {
        setHitCountRelation_GreaterEqual(hitCountRelation, null);
    }

    public void setHitCountRelation_GreaterEqual(String hitCountRelation, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = hitCountRelation;
        RangeQueryBuilder builder = regRangeQ("hitCountRelation", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCountRelation_LessEqual(String hitCountRelation) {
        setHitCountRelation_LessEqual(hitCountRelation, null);
    }

    public void setHitCountRelation_LessEqual(String hitCountRelation, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = hitCountRelation;
        RangeQueryBuilder builder = regRangeQ("hitCountRelation", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCountRelation_Exists() {
        setHitCountRelation_Exists(null);
    }

    public void setHitCountRelation_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("hitCountRelation");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setHitCountRelation_CommonTerms(String hitCountRelation) {
        setHitCountRelation_CommonTerms(hitCountRelation, null);
    }

    @Deprecated
    public void setHitCountRelation_CommonTerms(String hitCountRelation, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("hitCountRelation", hitCountRelation);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_HitCountRelation_Asc() {
        regOBA("hitCountRelation");
        return this;
    }

    public BsSearchLogCQ addOrderBy_HitCountRelation_Desc() {
        regOBD("hitCountRelation");
        return this;
    }

    public void setLanguages_Equal(String languages) {
        setLanguages_Term(languages, null);
    }

    public void setLanguages_Equal(String languages, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setLanguages_Term(languages, opLambda);
    }

    public void setLanguages_Term(String languages) {
        setLanguages_Term(languages, null);
    }

    public void setLanguages_Term(String languages, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("languages", languages);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLanguages_NotEqual(String languages) {
        setLanguages_NotTerm(languages, null);
    }

    public void setLanguages_NotTerm(String languages) {
        setLanguages_NotTerm(languages, null);
    }

    public void setLanguages_NotEqual(String languages, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setLanguages_NotTerm(languages, opLambda);
    }

    public void setLanguages_NotTerm(String languages, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setLanguages_Term(languages), opLambda);
    }

    public void setLanguages_Terms(Collection<String> languagesList) {
        setLanguages_Terms(languagesList, null);
    }

    public void setLanguages_Terms(Collection<String> languagesList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("languages", languagesList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLanguages_InScope(Collection<String> languagesList) {
        setLanguages_Terms(languagesList, null);
    }

    public void setLanguages_InScope(Collection<String> languagesList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setLanguages_Terms(languagesList, opLambda);
    }

    public void setLanguages_Match(String languages) {
        setLanguages_Match(languages, null);
    }

    public void setLanguages_Match(String languages, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("languages", languages);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLanguages_MatchPhrase(String languages) {
        setLanguages_MatchPhrase(languages, null);
    }

    public void setLanguages_MatchPhrase(String languages, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("languages", languages);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLanguages_MatchPhrasePrefix(String languages) {
        setLanguages_MatchPhrasePrefix(languages, null);
    }

    public void setLanguages_MatchPhrasePrefix(String languages, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("languages", languages);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLanguages_Fuzzy(String languages) {
        setLanguages_Fuzzy(languages, null);
    }

    public void setLanguages_Fuzzy(String languages, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("languages", languages);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLanguages_Prefix(String languages) {
        setLanguages_Prefix(languages, null);
    }

    public void setLanguages_Prefix(String languages, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("languages", languages);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLanguages_Wildcard(String languages) {
        setLanguages_Wildcard(languages, null);
    }

    public void setLanguages_Wildcard(String languages, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("languages", languages);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLanguages_Regexp(String languages) {
        setLanguages_Regexp(languages, null);
    }

    public void setLanguages_Regexp(String languages, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("languages", languages);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLanguages_SpanTerm(String languages) {
        setLanguages_SpanTerm("languages", null);
    }

    public void setLanguages_SpanTerm(String languages, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("languages", languages);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLanguages_GreaterThan(String languages) {
        setLanguages_GreaterThan(languages, null);
    }

    public void setLanguages_GreaterThan(String languages, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = languages;
        RangeQueryBuilder builder = regRangeQ("languages", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLanguages_LessThan(String languages) {
        setLanguages_LessThan(languages, null);
    }

    public void setLanguages_LessThan(String languages, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = languages;
        RangeQueryBuilder builder = regRangeQ("languages", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLanguages_GreaterEqual(String languages) {
        setLanguages_GreaterEqual(languages, null);
    }

    public void setLanguages_GreaterEqual(String languages, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = languages;
        RangeQueryBuilder builder = regRangeQ("languages", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLanguages_LessEqual(String languages) {
        setLanguages_LessEqual(languages, null);
    }

    public void setLanguages_LessEqual(String languages, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = languages;
        RangeQueryBuilder builder = regRangeQ("languages", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLanguages_Exists() {
        setLanguages_Exists(null);
    }

    public void setLanguages_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("languages");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setLanguages_CommonTerms(String languages) {
        setLanguages_CommonTerms(languages, null);
    }

    @Deprecated
    public void setLanguages_CommonTerms(String languages, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("languages", languages);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_Languages_Asc() {
        regOBA("languages");
        return this;
    }

    public BsSearchLogCQ addOrderBy_Languages_Desc() {
        regOBD("languages");
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

    public BsSearchLogCQ addOrderBy_QueryId_Asc() {
        regOBA("queryId");
        return this;
    }

    public BsSearchLogCQ addOrderBy_QueryId_Desc() {
        regOBD("queryId");
        return this;
    }

    public void setQueryOffset_Equal(Integer queryOffset) {
        setQueryOffset_Term(queryOffset, null);
    }

    public void setQueryOffset_Equal(Integer queryOffset, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setQueryOffset_Term(queryOffset, opLambda);
    }

    public void setQueryOffset_Term(Integer queryOffset) {
        setQueryOffset_Term(queryOffset, null);
    }

    public void setQueryOffset_Term(Integer queryOffset, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("queryOffset", queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_NotEqual(Integer queryOffset) {
        setQueryOffset_NotTerm(queryOffset, null);
    }

    public void setQueryOffset_NotTerm(Integer queryOffset) {
        setQueryOffset_NotTerm(queryOffset, null);
    }

    public void setQueryOffset_NotEqual(Integer queryOffset, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setQueryOffset_NotTerm(queryOffset, opLambda);
    }

    public void setQueryOffset_NotTerm(Integer queryOffset, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setQueryOffset_Term(queryOffset), opLambda);
    }

    public void setQueryOffset_Terms(Collection<Integer> queryOffsetList) {
        setQueryOffset_Terms(queryOffsetList, null);
    }

    public void setQueryOffset_Terms(Collection<Integer> queryOffsetList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("queryOffset", queryOffsetList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_InScope(Collection<Integer> queryOffsetList) {
        setQueryOffset_Terms(queryOffsetList, null);
    }

    public void setQueryOffset_InScope(Collection<Integer> queryOffsetList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setQueryOffset_Terms(queryOffsetList, opLambda);
    }

    public void setQueryOffset_Match(Integer queryOffset) {
        setQueryOffset_Match(queryOffset, null);
    }

    public void setQueryOffset_Match(Integer queryOffset, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("queryOffset", queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_MatchPhrase(Integer queryOffset) {
        setQueryOffset_MatchPhrase(queryOffset, null);
    }

    public void setQueryOffset_MatchPhrase(Integer queryOffset, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("queryOffset", queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_MatchPhrasePrefix(Integer queryOffset) {
        setQueryOffset_MatchPhrasePrefix(queryOffset, null);
    }

    public void setQueryOffset_MatchPhrasePrefix(Integer queryOffset, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("queryOffset", queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Fuzzy(Integer queryOffset) {
        setQueryOffset_Fuzzy(queryOffset, null);
    }

    public void setQueryOffset_Fuzzy(Integer queryOffset, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("queryOffset", queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_GreaterThan(Integer queryOffset) {
        setQueryOffset_GreaterThan(queryOffset, null);
    }

    public void setQueryOffset_GreaterThan(Integer queryOffset, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = queryOffset;
        RangeQueryBuilder builder = regRangeQ("queryOffset", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_LessThan(Integer queryOffset) {
        setQueryOffset_LessThan(queryOffset, null);
    }

    public void setQueryOffset_LessThan(Integer queryOffset, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = queryOffset;
        RangeQueryBuilder builder = regRangeQ("queryOffset", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_GreaterEqual(Integer queryOffset) {
        setQueryOffset_GreaterEqual(queryOffset, null);
    }

    public void setQueryOffset_GreaterEqual(Integer queryOffset, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = queryOffset;
        RangeQueryBuilder builder = regRangeQ("queryOffset", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_LessEqual(Integer queryOffset) {
        setQueryOffset_LessEqual(queryOffset, null);
    }

    public void setQueryOffset_LessEqual(Integer queryOffset, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = queryOffset;
        RangeQueryBuilder builder = regRangeQ("queryOffset", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Exists() {
        setQueryOffset_Exists(null);
    }

    public void setQueryOffset_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setQueryOffset_CommonTerms(Integer queryOffset) {
        setQueryOffset_CommonTerms(queryOffset, null);
    }

    @Deprecated
    public void setQueryOffset_CommonTerms(Integer queryOffset, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("queryOffset", queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_QueryOffset_Asc() {
        regOBA("queryOffset");
        return this;
    }

    public BsSearchLogCQ addOrderBy_QueryOffset_Desc() {
        regOBD("queryOffset");
        return this;
    }

    public void setQueryPageSize_Equal(Integer queryPageSize) {
        setQueryPageSize_Term(queryPageSize, null);
    }

    public void setQueryPageSize_Equal(Integer queryPageSize, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setQueryPageSize_Term(queryPageSize, opLambda);
    }

    public void setQueryPageSize_Term(Integer queryPageSize) {
        setQueryPageSize_Term(queryPageSize, null);
    }

    public void setQueryPageSize_Term(Integer queryPageSize, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("queryPageSize", queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_NotEqual(Integer queryPageSize) {
        setQueryPageSize_NotTerm(queryPageSize, null);
    }

    public void setQueryPageSize_NotTerm(Integer queryPageSize) {
        setQueryPageSize_NotTerm(queryPageSize, null);
    }

    public void setQueryPageSize_NotEqual(Integer queryPageSize, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setQueryPageSize_NotTerm(queryPageSize, opLambda);
    }

    public void setQueryPageSize_NotTerm(Integer queryPageSize, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setQueryPageSize_Term(queryPageSize), opLambda);
    }

    public void setQueryPageSize_Terms(Collection<Integer> queryPageSizeList) {
        setQueryPageSize_Terms(queryPageSizeList, null);
    }

    public void setQueryPageSize_Terms(Collection<Integer> queryPageSizeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("queryPageSize", queryPageSizeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_InScope(Collection<Integer> queryPageSizeList) {
        setQueryPageSize_Terms(queryPageSizeList, null);
    }

    public void setQueryPageSize_InScope(Collection<Integer> queryPageSizeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setQueryPageSize_Terms(queryPageSizeList, opLambda);
    }

    public void setQueryPageSize_Match(Integer queryPageSize) {
        setQueryPageSize_Match(queryPageSize, null);
    }

    public void setQueryPageSize_Match(Integer queryPageSize, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("queryPageSize", queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_MatchPhrase(Integer queryPageSize) {
        setQueryPageSize_MatchPhrase(queryPageSize, null);
    }

    public void setQueryPageSize_MatchPhrase(Integer queryPageSize, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("queryPageSize", queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_MatchPhrasePrefix(Integer queryPageSize) {
        setQueryPageSize_MatchPhrasePrefix(queryPageSize, null);
    }

    public void setQueryPageSize_MatchPhrasePrefix(Integer queryPageSize, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("queryPageSize", queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Fuzzy(Integer queryPageSize) {
        setQueryPageSize_Fuzzy(queryPageSize, null);
    }

    public void setQueryPageSize_Fuzzy(Integer queryPageSize, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("queryPageSize", queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_GreaterThan(Integer queryPageSize) {
        setQueryPageSize_GreaterThan(queryPageSize, null);
    }

    public void setQueryPageSize_GreaterThan(Integer queryPageSize, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = queryPageSize;
        RangeQueryBuilder builder = regRangeQ("queryPageSize", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_LessThan(Integer queryPageSize) {
        setQueryPageSize_LessThan(queryPageSize, null);
    }

    public void setQueryPageSize_LessThan(Integer queryPageSize, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = queryPageSize;
        RangeQueryBuilder builder = regRangeQ("queryPageSize", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_GreaterEqual(Integer queryPageSize) {
        setQueryPageSize_GreaterEqual(queryPageSize, null);
    }

    public void setQueryPageSize_GreaterEqual(Integer queryPageSize, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = queryPageSize;
        RangeQueryBuilder builder = regRangeQ("queryPageSize", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_LessEqual(Integer queryPageSize) {
        setQueryPageSize_LessEqual(queryPageSize, null);
    }

    public void setQueryPageSize_LessEqual(Integer queryPageSize, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = queryPageSize;
        RangeQueryBuilder builder = regRangeQ("queryPageSize", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Exists() {
        setQueryPageSize_Exists(null);
    }

    public void setQueryPageSize_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setQueryPageSize_CommonTerms(Integer queryPageSize) {
        setQueryPageSize_CommonTerms(queryPageSize, null);
    }

    @Deprecated
    public void setQueryPageSize_CommonTerms(Integer queryPageSize, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("queryPageSize", queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_QueryPageSize_Asc() {
        regOBA("queryPageSize");
        return this;
    }

    public BsSearchLogCQ addOrderBy_QueryPageSize_Desc() {
        regOBD("queryPageSize");
        return this;
    }

    public void setQueryTime_Equal(Long queryTime) {
        setQueryTime_Term(queryTime, null);
    }

    public void setQueryTime_Equal(Long queryTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setQueryTime_Term(queryTime, opLambda);
    }

    public void setQueryTime_Term(Long queryTime) {
        setQueryTime_Term(queryTime, null);
    }

    public void setQueryTime_Term(Long queryTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("queryTime", queryTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_NotEqual(Long queryTime) {
        setQueryTime_NotTerm(queryTime, null);
    }

    public void setQueryTime_NotTerm(Long queryTime) {
        setQueryTime_NotTerm(queryTime, null);
    }

    public void setQueryTime_NotEqual(Long queryTime, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setQueryTime_NotTerm(queryTime, opLambda);
    }

    public void setQueryTime_NotTerm(Long queryTime, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setQueryTime_Term(queryTime), opLambda);
    }

    public void setQueryTime_Terms(Collection<Long> queryTimeList) {
        setQueryTime_Terms(queryTimeList, null);
    }

    public void setQueryTime_Terms(Collection<Long> queryTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("queryTime", queryTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_InScope(Collection<Long> queryTimeList) {
        setQueryTime_Terms(queryTimeList, null);
    }

    public void setQueryTime_InScope(Collection<Long> queryTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setQueryTime_Terms(queryTimeList, opLambda);
    }

    public void setQueryTime_Match(Long queryTime) {
        setQueryTime_Match(queryTime, null);
    }

    public void setQueryTime_Match(Long queryTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("queryTime", queryTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_MatchPhrase(Long queryTime) {
        setQueryTime_MatchPhrase(queryTime, null);
    }

    public void setQueryTime_MatchPhrase(Long queryTime, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("queryTime", queryTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_MatchPhrasePrefix(Long queryTime) {
        setQueryTime_MatchPhrasePrefix(queryTime, null);
    }

    public void setQueryTime_MatchPhrasePrefix(Long queryTime, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("queryTime", queryTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Fuzzy(Long queryTime) {
        setQueryTime_Fuzzy(queryTime, null);
    }

    public void setQueryTime_Fuzzy(Long queryTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("queryTime", queryTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_GreaterThan(Long queryTime) {
        setQueryTime_GreaterThan(queryTime, null);
    }

    public void setQueryTime_GreaterThan(Long queryTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = queryTime;
        RangeQueryBuilder builder = regRangeQ("queryTime", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_LessThan(Long queryTime) {
        setQueryTime_LessThan(queryTime, null);
    }

    public void setQueryTime_LessThan(Long queryTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = queryTime;
        RangeQueryBuilder builder = regRangeQ("queryTime", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_GreaterEqual(Long queryTime) {
        setQueryTime_GreaterEqual(queryTime, null);
    }

    public void setQueryTime_GreaterEqual(Long queryTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = queryTime;
        RangeQueryBuilder builder = regRangeQ("queryTime", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_LessEqual(Long queryTime) {
        setQueryTime_LessEqual(queryTime, null);
    }

    public void setQueryTime_LessEqual(Long queryTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = queryTime;
        RangeQueryBuilder builder = regRangeQ("queryTime", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Exists() {
        setQueryTime_Exists(null);
    }

    public void setQueryTime_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setQueryTime_CommonTerms(Long queryTime) {
        setQueryTime_CommonTerms(queryTime, null);
    }

    @Deprecated
    public void setQueryTime_CommonTerms(Long queryTime, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("queryTime", queryTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_QueryTime_Asc() {
        regOBA("queryTime");
        return this;
    }

    public BsSearchLogCQ addOrderBy_QueryTime_Desc() {
        regOBD("queryTime");
        return this;
    }

    public void setReferer_Equal(String referer) {
        setReferer_Term(referer, null);
    }

    public void setReferer_Equal(String referer, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setReferer_Term(referer, opLambda);
    }

    public void setReferer_Term(String referer) {
        setReferer_Term(referer, null);
    }

    public void setReferer_Term(String referer, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("referer", referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_NotEqual(String referer) {
        setReferer_NotTerm(referer, null);
    }

    public void setReferer_NotTerm(String referer) {
        setReferer_NotTerm(referer, null);
    }

    public void setReferer_NotEqual(String referer, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setReferer_NotTerm(referer, opLambda);
    }

    public void setReferer_NotTerm(String referer, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setReferer_Term(referer), opLambda);
    }

    public void setReferer_Terms(Collection<String> refererList) {
        setReferer_Terms(refererList, null);
    }

    public void setReferer_Terms(Collection<String> refererList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("referer", refererList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_InScope(Collection<String> refererList) {
        setReferer_Terms(refererList, null);
    }

    public void setReferer_InScope(Collection<String> refererList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setReferer_Terms(refererList, opLambda);
    }

    public void setReferer_Match(String referer) {
        setReferer_Match(referer, null);
    }

    public void setReferer_Match(String referer, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("referer", referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_MatchPhrase(String referer) {
        setReferer_MatchPhrase(referer, null);
    }

    public void setReferer_MatchPhrase(String referer, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("referer", referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_MatchPhrasePrefix(String referer) {
        setReferer_MatchPhrasePrefix(referer, null);
    }

    public void setReferer_MatchPhrasePrefix(String referer, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("referer", referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_Fuzzy(String referer) {
        setReferer_Fuzzy(referer, null);
    }

    public void setReferer_Fuzzy(String referer, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("referer", referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_Prefix(String referer) {
        setReferer_Prefix(referer, null);
    }

    public void setReferer_Prefix(String referer, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("referer", referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_Wildcard(String referer) {
        setReferer_Wildcard(referer, null);
    }

    public void setReferer_Wildcard(String referer, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("referer", referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_Regexp(String referer) {
        setReferer_Regexp(referer, null);
    }

    public void setReferer_Regexp(String referer, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("referer", referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_SpanTerm(String referer) {
        setReferer_SpanTerm("referer", null);
    }

    public void setReferer_SpanTerm(String referer, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("referer", referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_GreaterThan(String referer) {
        setReferer_GreaterThan(referer, null);
    }

    public void setReferer_GreaterThan(String referer, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = referer;
        RangeQueryBuilder builder = regRangeQ("referer", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_LessThan(String referer) {
        setReferer_LessThan(referer, null);
    }

    public void setReferer_LessThan(String referer, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = referer;
        RangeQueryBuilder builder = regRangeQ("referer", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_GreaterEqual(String referer) {
        setReferer_GreaterEqual(referer, null);
    }

    public void setReferer_GreaterEqual(String referer, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = referer;
        RangeQueryBuilder builder = regRangeQ("referer", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_LessEqual(String referer) {
        setReferer_LessEqual(referer, null);
    }

    public void setReferer_LessEqual(String referer, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = referer;
        RangeQueryBuilder builder = regRangeQ("referer", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_Exists() {
        setReferer_Exists(null);
    }

    public void setReferer_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("referer");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setReferer_CommonTerms(String referer) {
        setReferer_CommonTerms(referer, null);
    }

    @Deprecated
    public void setReferer_CommonTerms(String referer, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("referer", referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_Referer_Asc() {
        regOBA("referer");
        return this;
    }

    public BsSearchLogCQ addOrderBy_Referer_Desc() {
        regOBD("referer");
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

    public BsSearchLogCQ addOrderBy_RequestedAt_Asc() {
        regOBA("requestedAt");
        return this;
    }

    public BsSearchLogCQ addOrderBy_RequestedAt_Desc() {
        regOBD("requestedAt");
        return this;
    }

    public void setResponseTime_Equal(Long responseTime) {
        setResponseTime_Term(responseTime, null);
    }

    public void setResponseTime_Equal(Long responseTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setResponseTime_Term(responseTime, opLambda);
    }

    public void setResponseTime_Term(Long responseTime) {
        setResponseTime_Term(responseTime, null);
    }

    public void setResponseTime_Term(Long responseTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_NotEqual(Long responseTime) {
        setResponseTime_NotTerm(responseTime, null);
    }

    public void setResponseTime_NotTerm(Long responseTime) {
        setResponseTime_NotTerm(responseTime, null);
    }

    public void setResponseTime_NotEqual(Long responseTime, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setResponseTime_NotTerm(responseTime, opLambda);
    }

    public void setResponseTime_NotTerm(Long responseTime, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setResponseTime_Term(responseTime), opLambda);
    }

    public void setResponseTime_Terms(Collection<Long> responseTimeList) {
        setResponseTime_Terms(responseTimeList, null);
    }

    public void setResponseTime_Terms(Collection<Long> responseTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("responseTime", responseTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_InScope(Collection<Long> responseTimeList) {
        setResponseTime_Terms(responseTimeList, null);
    }

    public void setResponseTime_InScope(Collection<Long> responseTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setResponseTime_Terms(responseTimeList, opLambda);
    }

    public void setResponseTime_Match(Long responseTime) {
        setResponseTime_Match(responseTime, null);
    }

    public void setResponseTime_Match(Long responseTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_MatchPhrase(Long responseTime) {
        setResponseTime_MatchPhrase(responseTime, null);
    }

    public void setResponseTime_MatchPhrase(Long responseTime, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_MatchPhrasePrefix(Long responseTime) {
        setResponseTime_MatchPhrasePrefix(responseTime, null);
    }

    public void setResponseTime_MatchPhrasePrefix(Long responseTime, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Fuzzy(Long responseTime) {
        setResponseTime_Fuzzy(responseTime, null);
    }

    public void setResponseTime_Fuzzy(Long responseTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_GreaterThan(Long responseTime) {
        setResponseTime_GreaterThan(responseTime, null);
    }

    public void setResponseTime_GreaterThan(Long responseTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = responseTime;
        RangeQueryBuilder builder = regRangeQ("responseTime", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_LessThan(Long responseTime) {
        setResponseTime_LessThan(responseTime, null);
    }

    public void setResponseTime_LessThan(Long responseTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = responseTime;
        RangeQueryBuilder builder = regRangeQ("responseTime", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_GreaterEqual(Long responseTime) {
        setResponseTime_GreaterEqual(responseTime, null);
    }

    public void setResponseTime_GreaterEqual(Long responseTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = responseTime;
        RangeQueryBuilder builder = regRangeQ("responseTime", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_LessEqual(Long responseTime) {
        setResponseTime_LessEqual(responseTime, null);
    }

    public void setResponseTime_LessEqual(Long responseTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = responseTime;
        RangeQueryBuilder builder = regRangeQ("responseTime", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Exists() {
        setResponseTime_Exists(null);
    }

    public void setResponseTime_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setResponseTime_CommonTerms(Long responseTime) {
        setResponseTime_CommonTerms(responseTime, null);
    }

    @Deprecated
    public void setResponseTime_CommonTerms(Long responseTime, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_ResponseTime_Asc() {
        regOBA("responseTime");
        return this;
    }

    public BsSearchLogCQ addOrderBy_ResponseTime_Desc() {
        regOBD("responseTime");
        return this;
    }

    public void setRoles_Equal(String roles) {
        setRoles_Term(roles, null);
    }

    public void setRoles_Equal(String roles, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setRoles_Term(roles, opLambda);
    }

    public void setRoles_Term(String roles) {
        setRoles_Term(roles, null);
    }

    public void setRoles_Term(String roles, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_NotEqual(String roles) {
        setRoles_NotTerm(roles, null);
    }

    public void setRoles_NotTerm(String roles) {
        setRoles_NotTerm(roles, null);
    }

    public void setRoles_NotEqual(String roles, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setRoles_NotTerm(roles, opLambda);
    }

    public void setRoles_NotTerm(String roles, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setRoles_Term(roles), opLambda);
    }

    public void setRoles_Terms(Collection<String> rolesList) {
        setRoles_Terms(rolesList, null);
    }

    public void setRoles_Terms(Collection<String> rolesList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("roles", rolesList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_InScope(Collection<String> rolesList) {
        setRoles_Terms(rolesList, null);
    }

    public void setRoles_InScope(Collection<String> rolesList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setRoles_Terms(rolesList, opLambda);
    }

    public void setRoles_Match(String roles) {
        setRoles_Match(roles, null);
    }

    public void setRoles_Match(String roles, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_MatchPhrase(String roles) {
        setRoles_MatchPhrase(roles, null);
    }

    public void setRoles_MatchPhrase(String roles, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_MatchPhrasePrefix(String roles) {
        setRoles_MatchPhrasePrefix(roles, null);
    }

    public void setRoles_MatchPhrasePrefix(String roles, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_Fuzzy(String roles) {
        setRoles_Fuzzy(roles, null);
    }

    public void setRoles_Fuzzy(String roles, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_Prefix(String roles) {
        setRoles_Prefix(roles, null);
    }

    public void setRoles_Prefix(String roles, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_Wildcard(String roles) {
        setRoles_Wildcard(roles, null);
    }

    public void setRoles_Wildcard(String roles, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_Regexp(String roles) {
        setRoles_Regexp(roles, null);
    }

    public void setRoles_Regexp(String roles, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_SpanTerm(String roles) {
        setRoles_SpanTerm("roles", null);
    }

    public void setRoles_SpanTerm(String roles, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_GreaterThan(String roles) {
        setRoles_GreaterThan(roles, null);
    }

    public void setRoles_GreaterThan(String roles, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = roles;
        RangeQueryBuilder builder = regRangeQ("roles", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_LessThan(String roles) {
        setRoles_LessThan(roles, null);
    }

    public void setRoles_LessThan(String roles, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = roles;
        RangeQueryBuilder builder = regRangeQ("roles", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_GreaterEqual(String roles) {
        setRoles_GreaterEqual(roles, null);
    }

    public void setRoles_GreaterEqual(String roles, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = roles;
        RangeQueryBuilder builder = regRangeQ("roles", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_LessEqual(String roles) {
        setRoles_LessEqual(roles, null);
    }

    public void setRoles_LessEqual(String roles, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = roles;
        RangeQueryBuilder builder = regRangeQ("roles", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_Exists() {
        setRoles_Exists(null);
    }

    public void setRoles_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setRoles_CommonTerms(String roles) {
        setRoles_CommonTerms(roles, null);
    }

    @Deprecated
    public void setRoles_CommonTerms(String roles, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_Roles_Asc() {
        regOBA("roles");
        return this;
    }

    public BsSearchLogCQ addOrderBy_Roles_Desc() {
        regOBD("roles");
        return this;
    }

    public void setSearchWord_Equal(String searchWord) {
        setSearchWord_Term(searchWord, null);
    }

    public void setSearchWord_Equal(String searchWord, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setSearchWord_Term(searchWord, opLambda);
    }

    public void setSearchWord_Term(String searchWord) {
        setSearchWord_Term(searchWord, null);
    }

    public void setSearchWord_Term(String searchWord, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("searchWord", searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_NotEqual(String searchWord) {
        setSearchWord_NotTerm(searchWord, null);
    }

    public void setSearchWord_NotTerm(String searchWord) {
        setSearchWord_NotTerm(searchWord, null);
    }

    public void setSearchWord_NotEqual(String searchWord, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setSearchWord_NotTerm(searchWord, opLambda);
    }

    public void setSearchWord_NotTerm(String searchWord, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setSearchWord_Term(searchWord), opLambda);
    }

    public void setSearchWord_Terms(Collection<String> searchWordList) {
        setSearchWord_Terms(searchWordList, null);
    }

    public void setSearchWord_Terms(Collection<String> searchWordList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("searchWord", searchWordList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_InScope(Collection<String> searchWordList) {
        setSearchWord_Terms(searchWordList, null);
    }

    public void setSearchWord_InScope(Collection<String> searchWordList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setSearchWord_Terms(searchWordList, opLambda);
    }

    public void setSearchWord_Match(String searchWord) {
        setSearchWord_Match(searchWord, null);
    }

    public void setSearchWord_Match(String searchWord, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("searchWord", searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_MatchPhrase(String searchWord) {
        setSearchWord_MatchPhrase(searchWord, null);
    }

    public void setSearchWord_MatchPhrase(String searchWord, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("searchWord", searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_MatchPhrasePrefix(String searchWord) {
        setSearchWord_MatchPhrasePrefix(searchWord, null);
    }

    public void setSearchWord_MatchPhrasePrefix(String searchWord, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("searchWord", searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_Fuzzy(String searchWord) {
        setSearchWord_Fuzzy(searchWord, null);
    }

    public void setSearchWord_Fuzzy(String searchWord, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("searchWord", searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_Prefix(String searchWord) {
        setSearchWord_Prefix(searchWord, null);
    }

    public void setSearchWord_Prefix(String searchWord, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("searchWord", searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_Wildcard(String searchWord) {
        setSearchWord_Wildcard(searchWord, null);
    }

    public void setSearchWord_Wildcard(String searchWord, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("searchWord", searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_Regexp(String searchWord) {
        setSearchWord_Regexp(searchWord, null);
    }

    public void setSearchWord_Regexp(String searchWord, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("searchWord", searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_SpanTerm(String searchWord) {
        setSearchWord_SpanTerm("searchWord", null);
    }

    public void setSearchWord_SpanTerm(String searchWord, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("searchWord", searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_GreaterThan(String searchWord) {
        setSearchWord_GreaterThan(searchWord, null);
    }

    public void setSearchWord_GreaterThan(String searchWord, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = searchWord;
        RangeQueryBuilder builder = regRangeQ("searchWord", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_LessThan(String searchWord) {
        setSearchWord_LessThan(searchWord, null);
    }

    public void setSearchWord_LessThan(String searchWord, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = searchWord;
        RangeQueryBuilder builder = regRangeQ("searchWord", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_GreaterEqual(String searchWord) {
        setSearchWord_GreaterEqual(searchWord, null);
    }

    public void setSearchWord_GreaterEqual(String searchWord, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = searchWord;
        RangeQueryBuilder builder = regRangeQ("searchWord", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_LessEqual(String searchWord) {
        setSearchWord_LessEqual(searchWord, null);
    }

    public void setSearchWord_LessEqual(String searchWord, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = searchWord;
        RangeQueryBuilder builder = regRangeQ("searchWord", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_Exists() {
        setSearchWord_Exists(null);
    }

    public void setSearchWord_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("searchWord");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setSearchWord_CommonTerms(String searchWord) {
        setSearchWord_CommonTerms(searchWord, null);
    }

    @Deprecated
    public void setSearchWord_CommonTerms(String searchWord, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("searchWord", searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_SearchWord_Asc() {
        regOBA("searchWord");
        return this;
    }

    public BsSearchLogCQ addOrderBy_SearchWord_Desc() {
        regOBD("searchWord");
        return this;
    }

    public void setUser_Equal(String user) {
        setUser_Term(user, null);
    }

    public void setUser_Equal(String user, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUser_Term(user, opLambda);
    }

    public void setUser_Term(String user) {
        setUser_Term(user, null);
    }

    public void setUser_Term(String user, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_NotEqual(String user) {
        setUser_NotTerm(user, null);
    }

    public void setUser_NotTerm(String user) {
        setUser_NotTerm(user, null);
    }

    public void setUser_NotEqual(String user, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setUser_NotTerm(user, opLambda);
    }

    public void setUser_NotTerm(String user, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setUser_Term(user), opLambda);
    }

    public void setUser_Terms(Collection<String> userList) {
        setUser_Terms(userList, null);
    }

    public void setUser_Terms(Collection<String> userList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("user", userList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_InScope(Collection<String> userList) {
        setUser_Terms(userList, null);
    }

    public void setUser_InScope(Collection<String> userList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUser_Terms(userList, opLambda);
    }

    public void setUser_Match(String user) {
        setUser_Match(user, null);
    }

    public void setUser_Match(String user, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_MatchPhrase(String user) {
        setUser_MatchPhrase(user, null);
    }

    public void setUser_MatchPhrase(String user, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_MatchPhrasePrefix(String user) {
        setUser_MatchPhrasePrefix(user, null);
    }

    public void setUser_MatchPhrasePrefix(String user, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_Fuzzy(String user) {
        setUser_Fuzzy(user, null);
    }

    public void setUser_Fuzzy(String user, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_Prefix(String user) {
        setUser_Prefix(user, null);
    }

    public void setUser_Prefix(String user, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_Wildcard(String user) {
        setUser_Wildcard(user, null);
    }

    public void setUser_Wildcard(String user, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_Regexp(String user) {
        setUser_Regexp(user, null);
    }

    public void setUser_Regexp(String user, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_SpanTerm(String user) {
        setUser_SpanTerm("user", null);
    }

    public void setUser_SpanTerm(String user, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_GreaterThan(String user) {
        setUser_GreaterThan(user, null);
    }

    public void setUser_GreaterThan(String user, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = user;
        RangeQueryBuilder builder = regRangeQ("user", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_LessThan(String user) {
        setUser_LessThan(user, null);
    }

    public void setUser_LessThan(String user, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = user;
        RangeQueryBuilder builder = regRangeQ("user", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_GreaterEqual(String user) {
        setUser_GreaterEqual(user, null);
    }

    public void setUser_GreaterEqual(String user, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = user;
        RangeQueryBuilder builder = regRangeQ("user", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_LessEqual(String user) {
        setUser_LessEqual(user, null);
    }

    public void setUser_LessEqual(String user, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = user;
        RangeQueryBuilder builder = regRangeQ("user", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_Exists() {
        setUser_Exists(null);
    }

    public void setUser_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setUser_CommonTerms(String user) {
        setUser_CommonTerms(user, null);
    }

    @Deprecated
    public void setUser_CommonTerms(String user, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_User_Asc() {
        regOBA("user");
        return this;
    }

    public BsSearchLogCQ addOrderBy_User_Desc() {
        regOBD("user");
        return this;
    }

    public void setUserAgent_Equal(String userAgent) {
        setUserAgent_Term(userAgent, null);
    }

    public void setUserAgent_Equal(String userAgent, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUserAgent_Term(userAgent, opLambda);
    }

    public void setUserAgent_Term(String userAgent) {
        setUserAgent_Term(userAgent, null);
    }

    public void setUserAgent_Term(String userAgent, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("userAgent", userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_NotEqual(String userAgent) {
        setUserAgent_NotTerm(userAgent, null);
    }

    public void setUserAgent_NotTerm(String userAgent) {
        setUserAgent_NotTerm(userAgent, null);
    }

    public void setUserAgent_NotEqual(String userAgent, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setUserAgent_NotTerm(userAgent, opLambda);
    }

    public void setUserAgent_NotTerm(String userAgent, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setUserAgent_Term(userAgent), opLambda);
    }

    public void setUserAgent_Terms(Collection<String> userAgentList) {
        setUserAgent_Terms(userAgentList, null);
    }

    public void setUserAgent_Terms(Collection<String> userAgentList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("userAgent", userAgentList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_InScope(Collection<String> userAgentList) {
        setUserAgent_Terms(userAgentList, null);
    }

    public void setUserAgent_InScope(Collection<String> userAgentList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUserAgent_Terms(userAgentList, opLambda);
    }

    public void setUserAgent_Match(String userAgent) {
        setUserAgent_Match(userAgent, null);
    }

    public void setUserAgent_Match(String userAgent, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("userAgent", userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_MatchPhrase(String userAgent) {
        setUserAgent_MatchPhrase(userAgent, null);
    }

    public void setUserAgent_MatchPhrase(String userAgent, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("userAgent", userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_MatchPhrasePrefix(String userAgent) {
        setUserAgent_MatchPhrasePrefix(userAgent, null);
    }

    public void setUserAgent_MatchPhrasePrefix(String userAgent, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("userAgent", userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_Fuzzy(String userAgent) {
        setUserAgent_Fuzzy(userAgent, null);
    }

    public void setUserAgent_Fuzzy(String userAgent, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("userAgent", userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_Prefix(String userAgent) {
        setUserAgent_Prefix(userAgent, null);
    }

    public void setUserAgent_Prefix(String userAgent, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("userAgent", userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_Wildcard(String userAgent) {
        setUserAgent_Wildcard(userAgent, null);
    }

    public void setUserAgent_Wildcard(String userAgent, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("userAgent", userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_Regexp(String userAgent) {
        setUserAgent_Regexp(userAgent, null);
    }

    public void setUserAgent_Regexp(String userAgent, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("userAgent", userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_SpanTerm(String userAgent) {
        setUserAgent_SpanTerm("userAgent", null);
    }

    public void setUserAgent_SpanTerm(String userAgent, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("userAgent", userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_GreaterThan(String userAgent) {
        setUserAgent_GreaterThan(userAgent, null);
    }

    public void setUserAgent_GreaterThan(String userAgent, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userAgent;
        RangeQueryBuilder builder = regRangeQ("userAgent", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_LessThan(String userAgent) {
        setUserAgent_LessThan(userAgent, null);
    }

    public void setUserAgent_LessThan(String userAgent, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userAgent;
        RangeQueryBuilder builder = regRangeQ("userAgent", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_GreaterEqual(String userAgent) {
        setUserAgent_GreaterEqual(userAgent, null);
    }

    public void setUserAgent_GreaterEqual(String userAgent, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userAgent;
        RangeQueryBuilder builder = regRangeQ("userAgent", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_LessEqual(String userAgent) {
        setUserAgent_LessEqual(userAgent, null);
    }

    public void setUserAgent_LessEqual(String userAgent, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userAgent;
        RangeQueryBuilder builder = regRangeQ("userAgent", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_Exists() {
        setUserAgent_Exists(null);
    }

    public void setUserAgent_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("userAgent");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setUserAgent_CommonTerms(String userAgent) {
        setUserAgent_CommonTerms(userAgent, null);
    }

    @Deprecated
    public void setUserAgent_CommonTerms(String userAgent, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("userAgent", userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_UserAgent_Asc() {
        regOBA("userAgent");
        return this;
    }

    public BsSearchLogCQ addOrderBy_UserAgent_Desc() {
        regOBD("userAgent");
        return this;
    }

    public void setUserInfoId_Equal(String userInfoId) {
        setUserInfoId_Term(userInfoId, null);
    }

    public void setUserInfoId_Equal(String userInfoId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUserInfoId_Term(userInfoId, opLambda);
    }

    public void setUserInfoId_Term(String userInfoId) {
        setUserInfoId_Term(userInfoId, null);
    }

    public void setUserInfoId_Term(String userInfoId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("userInfoId", userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_NotEqual(String userInfoId) {
        setUserInfoId_NotTerm(userInfoId, null);
    }

    public void setUserInfoId_NotTerm(String userInfoId) {
        setUserInfoId_NotTerm(userInfoId, null);
    }

    public void setUserInfoId_NotEqual(String userInfoId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setUserInfoId_NotTerm(userInfoId, opLambda);
    }

    public void setUserInfoId_NotTerm(String userInfoId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setUserInfoId_Term(userInfoId), opLambda);
    }

    public void setUserInfoId_Terms(Collection<String> userInfoIdList) {
        setUserInfoId_Terms(userInfoIdList, null);
    }

    public void setUserInfoId_Terms(Collection<String> userInfoIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("userInfoId", userInfoIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_InScope(Collection<String> userInfoIdList) {
        setUserInfoId_Terms(userInfoIdList, null);
    }

    public void setUserInfoId_InScope(Collection<String> userInfoIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUserInfoId_Terms(userInfoIdList, opLambda);
    }

    public void setUserInfoId_Match(String userInfoId) {
        setUserInfoId_Match(userInfoId, null);
    }

    public void setUserInfoId_Match(String userInfoId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("userInfoId", userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_MatchPhrase(String userInfoId) {
        setUserInfoId_MatchPhrase(userInfoId, null);
    }

    public void setUserInfoId_MatchPhrase(String userInfoId, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("userInfoId", userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_MatchPhrasePrefix(String userInfoId) {
        setUserInfoId_MatchPhrasePrefix(userInfoId, null);
    }

    public void setUserInfoId_MatchPhrasePrefix(String userInfoId, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("userInfoId", userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_Fuzzy(String userInfoId) {
        setUserInfoId_Fuzzy(userInfoId, null);
    }

    public void setUserInfoId_Fuzzy(String userInfoId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("userInfoId", userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_Prefix(String userInfoId) {
        setUserInfoId_Prefix(userInfoId, null);
    }

    public void setUserInfoId_Prefix(String userInfoId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("userInfoId", userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_Wildcard(String userInfoId) {
        setUserInfoId_Wildcard(userInfoId, null);
    }

    public void setUserInfoId_Wildcard(String userInfoId, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("userInfoId", userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_Regexp(String userInfoId) {
        setUserInfoId_Regexp(userInfoId, null);
    }

    public void setUserInfoId_Regexp(String userInfoId, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("userInfoId", userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_SpanTerm(String userInfoId) {
        setUserInfoId_SpanTerm("userInfoId", null);
    }

    public void setUserInfoId_SpanTerm(String userInfoId, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("userInfoId", userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_GreaterThan(String userInfoId) {
        setUserInfoId_GreaterThan(userInfoId, null);
    }

    public void setUserInfoId_GreaterThan(String userInfoId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userInfoId;
        RangeQueryBuilder builder = regRangeQ("userInfoId", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_LessThan(String userInfoId) {
        setUserInfoId_LessThan(userInfoId, null);
    }

    public void setUserInfoId_LessThan(String userInfoId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userInfoId;
        RangeQueryBuilder builder = regRangeQ("userInfoId", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_GreaterEqual(String userInfoId) {
        setUserInfoId_GreaterEqual(userInfoId, null);
    }

    public void setUserInfoId_GreaterEqual(String userInfoId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userInfoId;
        RangeQueryBuilder builder = regRangeQ("userInfoId", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_LessEqual(String userInfoId) {
        setUserInfoId_LessEqual(userInfoId, null);
    }

    public void setUserInfoId_LessEqual(String userInfoId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userInfoId;
        RangeQueryBuilder builder = regRangeQ("userInfoId", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_Exists() {
        setUserInfoId_Exists(null);
    }

    public void setUserInfoId_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("userInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setUserInfoId_CommonTerms(String userInfoId) {
        setUserInfoId_CommonTerms(userInfoId, null);
    }

    @Deprecated
    public void setUserInfoId_CommonTerms(String userInfoId, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("userInfoId", userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_UserInfoId_Asc() {
        regOBA("userInfoId");
        return this;
    }

    public BsSearchLogCQ addOrderBy_UserInfoId_Desc() {
        regOBD("userInfoId");
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

    public BsSearchLogCQ addOrderBy_UserSessionId_Asc() {
        regOBA("userSessionId");
        return this;
    }

    public BsSearchLogCQ addOrderBy_UserSessionId_Desc() {
        regOBD("userSessionId");
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

    public BsSearchLogCQ addOrderBy_VirtualHost_Asc() {
        regOBA("virtualHost");
        return this;
    }

    public BsSearchLogCQ addOrderBy_VirtualHost_Desc() {
        regOBD("virtualHost");
        return this;
    }

}
