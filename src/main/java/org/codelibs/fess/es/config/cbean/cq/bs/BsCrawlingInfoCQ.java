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
import org.codelibs.fess.es.config.cbean.cq.CrawlingInfoCQ;
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
public abstract class BsCrawlingInfoCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "crawling_info";
    }

    @Override
    public String xgetAliasName() {
        return "crawling_info";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<CrawlingInfoCQ> queryLambda,
            ScoreFunctionCall<ScoreFunctionCreator<CrawlingInfoCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        CrawlingInfoCQ cq = new CrawlingInfoCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                CrawlingInfoCQ cf = new CrawlingInfoCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<CrawlingInfoCQ, CrawlingInfoCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<CrawlingInfoCQ, CrawlingInfoCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<CrawlingInfoCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<CrawlingInfoCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<CrawlingInfoCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<CrawlingInfoCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        CrawlingInfoCQ mustQuery = new CrawlingInfoCQ();
        CrawlingInfoCQ shouldQuery = new CrawlingInfoCQ();
        CrawlingInfoCQ mustNotQuery = new CrawlingInfoCQ();
        CrawlingInfoCQ filterQuery = new CrawlingInfoCQ();
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
    public BsCrawlingInfoCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsCrawlingInfoCQ addOrderBy_Id_Desc() {
        regOBD("_id");
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

    public BsCrawlingInfoCQ addOrderBy_CreatedTime_Asc() {
        regOBA("createdTime");
        return this;
    }

    public BsCrawlingInfoCQ addOrderBy_CreatedTime_Desc() {
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

    public BsCrawlingInfoCQ addOrderBy_ExpiredTime_Asc() {
        regOBA("expiredTime");
        return this;
    }

    public BsCrawlingInfoCQ addOrderBy_ExpiredTime_Desc() {
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

    public BsCrawlingInfoCQ addOrderBy_Name_Asc() {
        regOBA("name");
        return this;
    }

    public BsCrawlingInfoCQ addOrderBy_Name_Desc() {
        regOBD("name");
        return this;
    }

    public void setSessionId_Equal(String sessionId) {
        setSessionId_Term(sessionId, null);
    }

    public void setSessionId_Equal(String sessionId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setSessionId_Term(sessionId, opLambda);
    }

    public void setSessionId_Term(String sessionId) {
        setSessionId_Term(sessionId, null);
    }

    public void setSessionId_Term(String sessionId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("sessionId", sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_NotEqual(String sessionId) {
        setSessionId_NotTerm(sessionId, null);
    }

    public void setSessionId_NotTerm(String sessionId) {
        setSessionId_NotTerm(sessionId, null);
    }

    public void setSessionId_NotEqual(String sessionId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setSessionId_NotTerm(sessionId, opLambda);
    }

    public void setSessionId_NotTerm(String sessionId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setSessionId_Term(sessionId), opLambda);
    }

    public void setSessionId_Terms(Collection<String> sessionIdList) {
        setSessionId_Terms(sessionIdList, null);
    }

    public void setSessionId_Terms(Collection<String> sessionIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("sessionId", sessionIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_InScope(Collection<String> sessionIdList) {
        setSessionId_Terms(sessionIdList, null);
    }

    public void setSessionId_InScope(Collection<String> sessionIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setSessionId_Terms(sessionIdList, opLambda);
    }

    public void setSessionId_Match(String sessionId) {
        setSessionId_Match(sessionId, null);
    }

    public void setSessionId_Match(String sessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("sessionId", sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_MatchPhrase(String sessionId) {
        setSessionId_MatchPhrase(sessionId, null);
    }

    public void setSessionId_MatchPhrase(String sessionId, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("sessionId", sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_MatchPhrasePrefix(String sessionId) {
        setSessionId_MatchPhrasePrefix(sessionId, null);
    }

    public void setSessionId_MatchPhrasePrefix(String sessionId, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("sessionId", sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_Fuzzy(String sessionId) {
        setSessionId_Fuzzy(sessionId, null);
    }

    public void setSessionId_Fuzzy(String sessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("sessionId", sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_Prefix(String sessionId) {
        setSessionId_Prefix(sessionId, null);
    }

    public void setSessionId_Prefix(String sessionId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("sessionId", sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_Wildcard(String sessionId) {
        setSessionId_Wildcard(sessionId, null);
    }

    public void setSessionId_Wildcard(String sessionId, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("sessionId", sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_Regexp(String sessionId) {
        setSessionId_Regexp(sessionId, null);
    }

    public void setSessionId_Regexp(String sessionId, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("sessionId", sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_SpanTerm(String sessionId) {
        setSessionId_SpanTerm("sessionId", null);
    }

    public void setSessionId_SpanTerm(String sessionId, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("sessionId", sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_GreaterThan(String sessionId) {
        setSessionId_GreaterThan(sessionId, null);
    }

    public void setSessionId_GreaterThan(String sessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = sessionId;
        RangeQueryBuilder builder = regRangeQ("sessionId", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_LessThan(String sessionId) {
        setSessionId_LessThan(sessionId, null);
    }

    public void setSessionId_LessThan(String sessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = sessionId;
        RangeQueryBuilder builder = regRangeQ("sessionId", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_GreaterEqual(String sessionId) {
        setSessionId_GreaterEqual(sessionId, null);
    }

    public void setSessionId_GreaterEqual(String sessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = sessionId;
        RangeQueryBuilder builder = regRangeQ("sessionId", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_LessEqual(String sessionId) {
        setSessionId_LessEqual(sessionId, null);
    }

    public void setSessionId_LessEqual(String sessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = sessionId;
        RangeQueryBuilder builder = regRangeQ("sessionId", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_Exists() {
        setSessionId_Exists(null);
    }

    public void setSessionId_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("sessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setSessionId_CommonTerms(String sessionId) {
        setSessionId_CommonTerms(sessionId, null);
    }

    @Deprecated
    public void setSessionId_CommonTerms(String sessionId, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("sessionId", sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsCrawlingInfoCQ addOrderBy_SessionId_Asc() {
        regOBA("sessionId");
        return this;
    }

    public BsCrawlingInfoCQ addOrderBy_SessionId_Desc() {
        regOBD("sessionId");
        return this;
    }

}
