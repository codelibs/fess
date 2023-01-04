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
import org.codelibs.fess.es.config.cbean.cq.CrawlingInfoParamCQ;
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
public abstract class BsCrawlingInfoParamCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "crawling_info_param";
    }

    @Override
    public String xgetAliasName() {
        return "crawling_info_param";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<CrawlingInfoParamCQ> queryLambda,
            ScoreFunctionCall<ScoreFunctionCreator<CrawlingInfoParamCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        CrawlingInfoParamCQ cq = new CrawlingInfoParamCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                CrawlingInfoParamCQ cf = new CrawlingInfoParamCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<CrawlingInfoParamCQ, CrawlingInfoParamCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<CrawlingInfoParamCQ, CrawlingInfoParamCQ> filteredLambda,
            ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<CrawlingInfoParamCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<CrawlingInfoParamCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<CrawlingInfoParamCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<CrawlingInfoParamCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        CrawlingInfoParamCQ mustQuery = new CrawlingInfoParamCQ();
        CrawlingInfoParamCQ shouldQuery = new CrawlingInfoParamCQ();
        CrawlingInfoParamCQ mustNotQuery = new CrawlingInfoParamCQ();
        CrawlingInfoParamCQ filterQuery = new CrawlingInfoParamCQ();
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
    public BsCrawlingInfoParamCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsCrawlingInfoParamCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setCrawlingInfoId_Equal(String crawlingInfoId) {
        setCrawlingInfoId_Term(crawlingInfoId, null);
    }

    public void setCrawlingInfoId_Equal(String crawlingInfoId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setCrawlingInfoId_Term(crawlingInfoId, opLambda);
    }

    public void setCrawlingInfoId_Term(String crawlingInfoId) {
        setCrawlingInfoId_Term(crawlingInfoId, null);
    }

    public void setCrawlingInfoId_Term(String crawlingInfoId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("crawlingInfoId", crawlingInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingInfoId_NotEqual(String crawlingInfoId) {
        setCrawlingInfoId_NotTerm(crawlingInfoId, null);
    }

    public void setCrawlingInfoId_NotTerm(String crawlingInfoId) {
        setCrawlingInfoId_NotTerm(crawlingInfoId, null);
    }

    public void setCrawlingInfoId_NotEqual(String crawlingInfoId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setCrawlingInfoId_NotTerm(crawlingInfoId, opLambda);
    }

    public void setCrawlingInfoId_NotTerm(String crawlingInfoId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setCrawlingInfoId_Term(crawlingInfoId), opLambda);
    }

    public void setCrawlingInfoId_Terms(Collection<String> crawlingInfoIdList) {
        setCrawlingInfoId_Terms(crawlingInfoIdList, null);
    }

    public void setCrawlingInfoId_Terms(Collection<String> crawlingInfoIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("crawlingInfoId", crawlingInfoIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingInfoId_InScope(Collection<String> crawlingInfoIdList) {
        setCrawlingInfoId_Terms(crawlingInfoIdList, null);
    }

    public void setCrawlingInfoId_InScope(Collection<String> crawlingInfoIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setCrawlingInfoId_Terms(crawlingInfoIdList, opLambda);
    }

    public void setCrawlingInfoId_Match(String crawlingInfoId) {
        setCrawlingInfoId_Match(crawlingInfoId, null);
    }

    public void setCrawlingInfoId_Match(String crawlingInfoId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("crawlingInfoId", crawlingInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingInfoId_MatchPhrase(String crawlingInfoId) {
        setCrawlingInfoId_MatchPhrase(crawlingInfoId, null);
    }

    public void setCrawlingInfoId_MatchPhrase(String crawlingInfoId, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("crawlingInfoId", crawlingInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingInfoId_MatchPhrasePrefix(String crawlingInfoId) {
        setCrawlingInfoId_MatchPhrasePrefix(crawlingInfoId, null);
    }

    public void setCrawlingInfoId_MatchPhrasePrefix(String crawlingInfoId, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("crawlingInfoId", crawlingInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingInfoId_Fuzzy(String crawlingInfoId) {
        setCrawlingInfoId_Fuzzy(crawlingInfoId, null);
    }

    public void setCrawlingInfoId_Fuzzy(String crawlingInfoId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("crawlingInfoId", crawlingInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingInfoId_Prefix(String crawlingInfoId) {
        setCrawlingInfoId_Prefix(crawlingInfoId, null);
    }

    public void setCrawlingInfoId_Prefix(String crawlingInfoId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("crawlingInfoId", crawlingInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingInfoId_Wildcard(String crawlingInfoId) {
        setCrawlingInfoId_Wildcard(crawlingInfoId, null);
    }

    public void setCrawlingInfoId_Wildcard(String crawlingInfoId, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("crawlingInfoId", crawlingInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingInfoId_Regexp(String crawlingInfoId) {
        setCrawlingInfoId_Regexp(crawlingInfoId, null);
    }

    public void setCrawlingInfoId_Regexp(String crawlingInfoId, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("crawlingInfoId", crawlingInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingInfoId_SpanTerm(String crawlingInfoId) {
        setCrawlingInfoId_SpanTerm("crawlingInfoId", null);
    }

    public void setCrawlingInfoId_SpanTerm(String crawlingInfoId, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("crawlingInfoId", crawlingInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingInfoId_GreaterThan(String crawlingInfoId) {
        setCrawlingInfoId_GreaterThan(crawlingInfoId, null);
    }

    public void setCrawlingInfoId_GreaterThan(String crawlingInfoId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = crawlingInfoId;
        RangeQueryBuilder builder = regRangeQ("crawlingInfoId", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingInfoId_LessThan(String crawlingInfoId) {
        setCrawlingInfoId_LessThan(crawlingInfoId, null);
    }

    public void setCrawlingInfoId_LessThan(String crawlingInfoId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = crawlingInfoId;
        RangeQueryBuilder builder = regRangeQ("crawlingInfoId", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingInfoId_GreaterEqual(String crawlingInfoId) {
        setCrawlingInfoId_GreaterEqual(crawlingInfoId, null);
    }

    public void setCrawlingInfoId_GreaterEqual(String crawlingInfoId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = crawlingInfoId;
        RangeQueryBuilder builder = regRangeQ("crawlingInfoId", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingInfoId_LessEqual(String crawlingInfoId) {
        setCrawlingInfoId_LessEqual(crawlingInfoId, null);
    }

    public void setCrawlingInfoId_LessEqual(String crawlingInfoId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = crawlingInfoId;
        RangeQueryBuilder builder = regRangeQ("crawlingInfoId", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingInfoId_Exists() {
        setCrawlingInfoId_Exists(null);
    }

    public void setCrawlingInfoId_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("crawlingInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setCrawlingInfoId_CommonTerms(String crawlingInfoId) {
        setCrawlingInfoId_CommonTerms(crawlingInfoId, null);
    }

    @Deprecated
    public void setCrawlingInfoId_CommonTerms(String crawlingInfoId, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("crawlingInfoId", crawlingInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsCrawlingInfoParamCQ addOrderBy_CrawlingInfoId_Asc() {
        regOBA("crawlingInfoId");
        return this;
    }

    public BsCrawlingInfoParamCQ addOrderBy_CrawlingInfoId_Desc() {
        regOBD("crawlingInfoId");
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

    public BsCrawlingInfoParamCQ addOrderBy_CreatedTime_Asc() {
        regOBA("createdTime");
        return this;
    }

    public BsCrawlingInfoParamCQ addOrderBy_CreatedTime_Desc() {
        regOBD("createdTime");
        return this;
    }

    public void setKey_Equal(String key) {
        setKey_Term(key, null);
    }

    public void setKey_Equal(String key, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setKey_Term(key, opLambda);
    }

    public void setKey_Term(String key) {
        setKey_Term(key, null);
    }

    public void setKey_Term(String key, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_NotEqual(String key) {
        setKey_NotTerm(key, null);
    }

    public void setKey_NotTerm(String key) {
        setKey_NotTerm(key, null);
    }

    public void setKey_NotEqual(String key, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setKey_NotTerm(key, opLambda);
    }

    public void setKey_NotTerm(String key, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setKey_Term(key), opLambda);
    }

    public void setKey_Terms(Collection<String> keyList) {
        setKey_Terms(keyList, null);
    }

    public void setKey_Terms(Collection<String> keyList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("key", keyList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_InScope(Collection<String> keyList) {
        setKey_Terms(keyList, null);
    }

    public void setKey_InScope(Collection<String> keyList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setKey_Terms(keyList, opLambda);
    }

    public void setKey_Match(String key) {
        setKey_Match(key, null);
    }

    public void setKey_Match(String key, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_MatchPhrase(String key) {
        setKey_MatchPhrase(key, null);
    }

    public void setKey_MatchPhrase(String key, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_MatchPhrasePrefix(String key) {
        setKey_MatchPhrasePrefix(key, null);
    }

    public void setKey_MatchPhrasePrefix(String key, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_Fuzzy(String key) {
        setKey_Fuzzy(key, null);
    }

    public void setKey_Fuzzy(String key, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_Prefix(String key) {
        setKey_Prefix(key, null);
    }

    public void setKey_Prefix(String key, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_Wildcard(String key) {
        setKey_Wildcard(key, null);
    }

    public void setKey_Wildcard(String key, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_Regexp(String key) {
        setKey_Regexp(key, null);
    }

    public void setKey_Regexp(String key, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_SpanTerm(String key) {
        setKey_SpanTerm("key", null);
    }

    public void setKey_SpanTerm(String key, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_GreaterThan(String key) {
        setKey_GreaterThan(key, null);
    }

    public void setKey_GreaterThan(String key, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = key;
        RangeQueryBuilder builder = regRangeQ("key", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_LessThan(String key) {
        setKey_LessThan(key, null);
    }

    public void setKey_LessThan(String key, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = key;
        RangeQueryBuilder builder = regRangeQ("key", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_GreaterEqual(String key) {
        setKey_GreaterEqual(key, null);
    }

    public void setKey_GreaterEqual(String key, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = key;
        RangeQueryBuilder builder = regRangeQ("key", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_LessEqual(String key) {
        setKey_LessEqual(key, null);
    }

    public void setKey_LessEqual(String key, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = key;
        RangeQueryBuilder builder = regRangeQ("key", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_Exists() {
        setKey_Exists(null);
    }

    public void setKey_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("key");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setKey_CommonTerms(String key) {
        setKey_CommonTerms(key, null);
    }

    @Deprecated
    public void setKey_CommonTerms(String key, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsCrawlingInfoParamCQ addOrderBy_Key_Asc() {
        regOBA("key");
        return this;
    }

    public BsCrawlingInfoParamCQ addOrderBy_Key_Desc() {
        regOBD("key");
        return this;
    }

    public void setValue_Equal(String value) {
        setValue_Term(value, null);
    }

    public void setValue_Equal(String value, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setValue_Term(value, opLambda);
    }

    public void setValue_Term(String value) {
        setValue_Term(value, null);
    }

    public void setValue_Term(String value, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_NotEqual(String value) {
        setValue_NotTerm(value, null);
    }

    public void setValue_NotTerm(String value) {
        setValue_NotTerm(value, null);
    }

    public void setValue_NotEqual(String value, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setValue_NotTerm(value, opLambda);
    }

    public void setValue_NotTerm(String value, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setValue_Term(value), opLambda);
    }

    public void setValue_Terms(Collection<String> valueList) {
        setValue_Terms(valueList, null);
    }

    public void setValue_Terms(Collection<String> valueList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("value", valueList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_InScope(Collection<String> valueList) {
        setValue_Terms(valueList, null);
    }

    public void setValue_InScope(Collection<String> valueList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setValue_Terms(valueList, opLambda);
    }

    public void setValue_Match(String value) {
        setValue_Match(value, null);
    }

    public void setValue_Match(String value, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_MatchPhrase(String value) {
        setValue_MatchPhrase(value, null);
    }

    public void setValue_MatchPhrase(String value, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_MatchPhrasePrefix(String value) {
        setValue_MatchPhrasePrefix(value, null);
    }

    public void setValue_MatchPhrasePrefix(String value, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_Fuzzy(String value) {
        setValue_Fuzzy(value, null);
    }

    public void setValue_Fuzzy(String value, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_Prefix(String value) {
        setValue_Prefix(value, null);
    }

    public void setValue_Prefix(String value, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_Wildcard(String value) {
        setValue_Wildcard(value, null);
    }

    public void setValue_Wildcard(String value, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_Regexp(String value) {
        setValue_Regexp(value, null);
    }

    public void setValue_Regexp(String value, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_SpanTerm(String value) {
        setValue_SpanTerm("value", null);
    }

    public void setValue_SpanTerm(String value, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_GreaterThan(String value) {
        setValue_GreaterThan(value, null);
    }

    public void setValue_GreaterThan(String value, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = value;
        RangeQueryBuilder builder = regRangeQ("value", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_LessThan(String value) {
        setValue_LessThan(value, null);
    }

    public void setValue_LessThan(String value, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = value;
        RangeQueryBuilder builder = regRangeQ("value", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_GreaterEqual(String value) {
        setValue_GreaterEqual(value, null);
    }

    public void setValue_GreaterEqual(String value, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = value;
        RangeQueryBuilder builder = regRangeQ("value", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_LessEqual(String value) {
        setValue_LessEqual(value, null);
    }

    public void setValue_LessEqual(String value, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = value;
        RangeQueryBuilder builder = regRangeQ("value", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_Exists() {
        setValue_Exists(null);
    }

    public void setValue_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("value");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setValue_CommonTerms(String value) {
        setValue_CommonTerms(value, null);
    }

    @Deprecated
    public void setValue_CommonTerms(String value, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsCrawlingInfoParamCQ addOrderBy_Value_Asc() {
        regOBA("value");
        return this;
    }

    public BsCrawlingInfoParamCQ addOrderBy_Value_Desc() {
        regOBD("value");
        return this;
    }

}
