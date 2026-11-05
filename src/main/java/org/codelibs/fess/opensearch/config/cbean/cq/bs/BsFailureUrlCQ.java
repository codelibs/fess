/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.opensearch.config.cbean.cq.bs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.codelibs.fess.opensearch.config.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.opensearch.config.cbean.cq.FailureUrlCQ;
import org.dbflute.cbean.ckey.ConditionKey;
import org.codelibs.fesen.opensearch.index.query.BoolQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.CommonTermsQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.ExistsQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.IdsQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.MatchPhraseQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.MatchQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.PrefixQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.RangeQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.RegexpQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.SpanTermQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.TermQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.TermsQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.WildcardQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsFailureUrlCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "failure_url";
    }

    @Override
    public String xgetAliasName() {
        return "failure_url";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<FailureUrlCQ> queryLambda, ScoreFunctionCall<ScoreFunctionCreator<FailureUrlCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        FailureUrlCQ cq = new FailureUrlCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                FailureUrlCQ cf = new FailureUrlCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<FailureUrlCQ, FailureUrlCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<FailureUrlCQ, FailureUrlCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<FailureUrlCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<FailureUrlCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<FailureUrlCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<FailureUrlCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        FailureUrlCQ mustQuery = new FailureUrlCQ();
        FailureUrlCQ shouldQuery = new FailureUrlCQ();
        FailureUrlCQ mustNotQuery = new FailureUrlCQ();
        FailureUrlCQ filterQuery = new FailureUrlCQ();
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
    public BsFailureUrlCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsFailureUrlCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setConfigId_Equal(String configId) {
        setConfigId_Term(configId, null);
    }

    public void setConfigId_Equal(String configId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setConfigId_Term(configId, opLambda);
    }

    public void setConfigId_Term(String configId) {
        setConfigId_Term(configId, null);
    }

    public void setConfigId_Term(String configId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("configId", configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_NotEqual(String configId) {
        setConfigId_NotTerm(configId, null);
    }

    public void setConfigId_NotTerm(String configId) {
        setConfigId_NotTerm(configId, null);
    }

    public void setConfigId_NotEqual(String configId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setConfigId_NotTerm(configId, opLambda);
    }

    public void setConfigId_NotTerm(String configId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setConfigId_Term(configId), opLambda);
    }

    public void setConfigId_Terms(Collection<String> configIdList) {
        setConfigId_Terms(configIdList, null);
    }

    public void setConfigId_Terms(Collection<String> configIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("configId", configIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_InScope(Collection<String> configIdList) {
        setConfigId_Terms(configIdList, null);
    }

    public void setConfigId_InScope(Collection<String> configIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setConfigId_Terms(configIdList, opLambda);
    }

    public void setConfigId_Match(String configId) {
        setConfigId_Match(configId, null);
    }

    public void setConfigId_Match(String configId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("configId", configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_MatchPhrase(String configId) {
        setConfigId_MatchPhrase(configId, null);
    }

    public void setConfigId_MatchPhrase(String configId, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("configId", configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_MatchPhrasePrefix(String configId) {
        setConfigId_MatchPhrasePrefix(configId, null);
    }

    public void setConfigId_MatchPhrasePrefix(String configId, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("configId", configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_Fuzzy(String configId) {
        setConfigId_Fuzzy(configId, null);
    }

    public void setConfigId_Fuzzy(String configId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("configId", configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_Prefix(String configId) {
        setConfigId_Prefix(configId, null);
    }

    public void setConfigId_Prefix(String configId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("configId", configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_Wildcard(String configId) {
        setConfigId_Wildcard(configId, null);
    }

    public void setConfigId_Wildcard(String configId, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("configId", configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_Regexp(String configId) {
        setConfigId_Regexp(configId, null);
    }

    public void setConfigId_Regexp(String configId, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("configId", configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_SpanTerm(String configId) {
        setConfigId_SpanTerm("configId", null);
    }

    public void setConfigId_SpanTerm(String configId, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("configId", configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_GreaterThan(String configId) {
        setConfigId_GreaterThan(configId, null);
    }

    public void setConfigId_GreaterThan(String configId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = configId;
        RangeQueryBuilder builder = regRangeQ("configId", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_LessThan(String configId) {
        setConfigId_LessThan(configId, null);
    }

    public void setConfigId_LessThan(String configId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = configId;
        RangeQueryBuilder builder = regRangeQ("configId", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_GreaterEqual(String configId) {
        setConfigId_GreaterEqual(configId, null);
    }

    public void setConfigId_GreaterEqual(String configId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = configId;
        RangeQueryBuilder builder = regRangeQ("configId", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_LessEqual(String configId) {
        setConfigId_LessEqual(configId, null);
    }

    public void setConfigId_LessEqual(String configId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = configId;
        RangeQueryBuilder builder = regRangeQ("configId", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_Exists() {
        setConfigId_Exists(null);
    }

    public void setConfigId_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("configId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setConfigId_CommonTerms(String configId) {
        setConfigId_CommonTerms(configId, null);
    }

    @Deprecated
    public void setConfigId_CommonTerms(String configId, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("configId", configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFailureUrlCQ addOrderBy_ConfigId_Asc() {
        regOBA("configId");
        return this;
    }

    public BsFailureUrlCQ addOrderBy_ConfigId_Desc() {
        regOBD("configId");
        return this;
    }

    public void setContentLength_Equal(Long contentLength) {
        setContentLength_Term(contentLength, null);
    }

    public void setContentLength_Equal(Long contentLength, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setContentLength_Term(contentLength, opLambda);
    }

    public void setContentLength_Term(Long contentLength) {
        setContentLength_Term(contentLength, null);
    }

    public void setContentLength_Term(Long contentLength, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("contentLength", contentLength);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setContentLength_NotEqual(Long contentLength) {
        setContentLength_NotTerm(contentLength, null);
    }

    public void setContentLength_NotTerm(Long contentLength) {
        setContentLength_NotTerm(contentLength, null);
    }

    public void setContentLength_NotEqual(Long contentLength, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setContentLength_NotTerm(contentLength, opLambda);
    }

    public void setContentLength_NotTerm(Long contentLength, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setContentLength_Term(contentLength), opLambda);
    }

    public void setContentLength_Terms(Collection<Long> contentLengthList) {
        setContentLength_Terms(contentLengthList, null);
    }

    public void setContentLength_Terms(Collection<Long> contentLengthList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("contentLength", contentLengthList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setContentLength_InScope(Collection<Long> contentLengthList) {
        setContentLength_Terms(contentLengthList, null);
    }

    public void setContentLength_InScope(Collection<Long> contentLengthList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setContentLength_Terms(contentLengthList, opLambda);
    }

    public void setContentLength_Match(Long contentLength) {
        setContentLength_Match(contentLength, null);
    }

    public void setContentLength_Match(Long contentLength, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("contentLength", contentLength);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setContentLength_MatchPhrase(Long contentLength) {
        setContentLength_MatchPhrase(contentLength, null);
    }

    public void setContentLength_MatchPhrase(Long contentLength, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("contentLength", contentLength);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setContentLength_MatchPhrasePrefix(Long contentLength) {
        setContentLength_MatchPhrasePrefix(contentLength, null);
    }

    public void setContentLength_MatchPhrasePrefix(Long contentLength, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("contentLength", contentLength);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setContentLength_Fuzzy(Long contentLength) {
        setContentLength_Fuzzy(contentLength, null);
    }

    public void setContentLength_Fuzzy(Long contentLength, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("contentLength", contentLength);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setContentLength_GreaterThan(Long contentLength) {
        setContentLength_GreaterThan(contentLength, null);
    }

    public void setContentLength_GreaterThan(Long contentLength, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = contentLength;
        RangeQueryBuilder builder = regRangeQ("contentLength", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setContentLength_LessThan(Long contentLength) {
        setContentLength_LessThan(contentLength, null);
    }

    public void setContentLength_LessThan(Long contentLength, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = contentLength;
        RangeQueryBuilder builder = regRangeQ("contentLength", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setContentLength_GreaterEqual(Long contentLength) {
        setContentLength_GreaterEqual(contentLength, null);
    }

    public void setContentLength_GreaterEqual(Long contentLength, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = contentLength;
        RangeQueryBuilder builder = regRangeQ("contentLength", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setContentLength_LessEqual(Long contentLength) {
        setContentLength_LessEqual(contentLength, null);
    }

    public void setContentLength_LessEqual(Long contentLength, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = contentLength;
        RangeQueryBuilder builder = regRangeQ("contentLength", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setContentLength_Exists() {
        setContentLength_Exists(null);
    }

    public void setContentLength_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("contentLength");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setContentLength_CommonTerms(Long contentLength) {
        setContentLength_CommonTerms(contentLength, null);
    }

    @Deprecated
    public void setContentLength_CommonTerms(Long contentLength, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("contentLength", contentLength);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFailureUrlCQ addOrderBy_ContentLength_Asc() {
        regOBA("contentLength");
        return this;
    }

    public BsFailureUrlCQ addOrderBy_ContentLength_Desc() {
        regOBD("contentLength");
        return this;
    }

    public void setErrorCount_Equal(Integer errorCount) {
        setErrorCount_Term(errorCount, null);
    }

    public void setErrorCount_Equal(Integer errorCount, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setErrorCount_Term(errorCount, opLambda);
    }

    public void setErrorCount_Term(Integer errorCount) {
        setErrorCount_Term(errorCount, null);
    }

    public void setErrorCount_Term(Integer errorCount, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("errorCount", errorCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_NotEqual(Integer errorCount) {
        setErrorCount_NotTerm(errorCount, null);
    }

    public void setErrorCount_NotTerm(Integer errorCount) {
        setErrorCount_NotTerm(errorCount, null);
    }

    public void setErrorCount_NotEqual(Integer errorCount, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setErrorCount_NotTerm(errorCount, opLambda);
    }

    public void setErrorCount_NotTerm(Integer errorCount, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setErrorCount_Term(errorCount), opLambda);
    }

    public void setErrorCount_Terms(Collection<Integer> errorCountList) {
        setErrorCount_Terms(errorCountList, null);
    }

    public void setErrorCount_Terms(Collection<Integer> errorCountList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("errorCount", errorCountList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_InScope(Collection<Integer> errorCountList) {
        setErrorCount_Terms(errorCountList, null);
    }

    public void setErrorCount_InScope(Collection<Integer> errorCountList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setErrorCount_Terms(errorCountList, opLambda);
    }

    public void setErrorCount_Match(Integer errorCount) {
        setErrorCount_Match(errorCount, null);
    }

    public void setErrorCount_Match(Integer errorCount, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("errorCount", errorCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_MatchPhrase(Integer errorCount) {
        setErrorCount_MatchPhrase(errorCount, null);
    }

    public void setErrorCount_MatchPhrase(Integer errorCount, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("errorCount", errorCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_MatchPhrasePrefix(Integer errorCount) {
        setErrorCount_MatchPhrasePrefix(errorCount, null);
    }

    public void setErrorCount_MatchPhrasePrefix(Integer errorCount, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("errorCount", errorCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_Fuzzy(Integer errorCount) {
        setErrorCount_Fuzzy(errorCount, null);
    }

    public void setErrorCount_Fuzzy(Integer errorCount, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("errorCount", errorCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_GreaterThan(Integer errorCount) {
        setErrorCount_GreaterThan(errorCount, null);
    }

    public void setErrorCount_GreaterThan(Integer errorCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = errorCount;
        RangeQueryBuilder builder = regRangeQ("errorCount", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_LessThan(Integer errorCount) {
        setErrorCount_LessThan(errorCount, null);
    }

    public void setErrorCount_LessThan(Integer errorCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = errorCount;
        RangeQueryBuilder builder = regRangeQ("errorCount", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_GreaterEqual(Integer errorCount) {
        setErrorCount_GreaterEqual(errorCount, null);
    }

    public void setErrorCount_GreaterEqual(Integer errorCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = errorCount;
        RangeQueryBuilder builder = regRangeQ("errorCount", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_LessEqual(Integer errorCount) {
        setErrorCount_LessEqual(errorCount, null);
    }

    public void setErrorCount_LessEqual(Integer errorCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = errorCount;
        RangeQueryBuilder builder = regRangeQ("errorCount", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_Exists() {
        setErrorCount_Exists(null);
    }

    public void setErrorCount_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("errorCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setErrorCount_CommonTerms(Integer errorCount) {
        setErrorCount_CommonTerms(errorCount, null);
    }

    @Deprecated
    public void setErrorCount_CommonTerms(Integer errorCount, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("errorCount", errorCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFailureUrlCQ addOrderBy_ErrorCount_Asc() {
        regOBA("errorCount");
        return this;
    }

    public BsFailureUrlCQ addOrderBy_ErrorCount_Desc() {
        regOBD("errorCount");
        return this;
    }

    public void setErrorLog_Equal(String errorLog) {
        setErrorLog_Term(errorLog, null);
    }

    public void setErrorLog_Equal(String errorLog, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setErrorLog_Term(errorLog, opLambda);
    }

    public void setErrorLog_Term(String errorLog) {
        setErrorLog_Term(errorLog, null);
    }

    public void setErrorLog_Term(String errorLog, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("errorLog", errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_NotEqual(String errorLog) {
        setErrorLog_NotTerm(errorLog, null);
    }

    public void setErrorLog_NotTerm(String errorLog) {
        setErrorLog_NotTerm(errorLog, null);
    }

    public void setErrorLog_NotEqual(String errorLog, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setErrorLog_NotTerm(errorLog, opLambda);
    }

    public void setErrorLog_NotTerm(String errorLog, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setErrorLog_Term(errorLog), opLambda);
    }

    public void setErrorLog_Terms(Collection<String> errorLogList) {
        setErrorLog_Terms(errorLogList, null);
    }

    public void setErrorLog_Terms(Collection<String> errorLogList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("errorLog", errorLogList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_InScope(Collection<String> errorLogList) {
        setErrorLog_Terms(errorLogList, null);
    }

    public void setErrorLog_InScope(Collection<String> errorLogList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setErrorLog_Terms(errorLogList, opLambda);
    }

    public void setErrorLog_Match(String errorLog) {
        setErrorLog_Match(errorLog, null);
    }

    public void setErrorLog_Match(String errorLog, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("errorLog", errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_MatchPhrase(String errorLog) {
        setErrorLog_MatchPhrase(errorLog, null);
    }

    public void setErrorLog_MatchPhrase(String errorLog, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("errorLog", errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_MatchPhrasePrefix(String errorLog) {
        setErrorLog_MatchPhrasePrefix(errorLog, null);
    }

    public void setErrorLog_MatchPhrasePrefix(String errorLog, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("errorLog", errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_Fuzzy(String errorLog) {
        setErrorLog_Fuzzy(errorLog, null);
    }

    public void setErrorLog_Fuzzy(String errorLog, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("errorLog", errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_Prefix(String errorLog) {
        setErrorLog_Prefix(errorLog, null);
    }

    public void setErrorLog_Prefix(String errorLog, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("errorLog", errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_Wildcard(String errorLog) {
        setErrorLog_Wildcard(errorLog, null);
    }

    public void setErrorLog_Wildcard(String errorLog, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("errorLog", errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_Regexp(String errorLog) {
        setErrorLog_Regexp(errorLog, null);
    }

    public void setErrorLog_Regexp(String errorLog, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("errorLog", errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_SpanTerm(String errorLog) {
        setErrorLog_SpanTerm("errorLog", null);
    }

    public void setErrorLog_SpanTerm(String errorLog, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("errorLog", errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_GreaterThan(String errorLog) {
        setErrorLog_GreaterThan(errorLog, null);
    }

    public void setErrorLog_GreaterThan(String errorLog, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = errorLog;
        RangeQueryBuilder builder = regRangeQ("errorLog", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_LessThan(String errorLog) {
        setErrorLog_LessThan(errorLog, null);
    }

    public void setErrorLog_LessThan(String errorLog, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = errorLog;
        RangeQueryBuilder builder = regRangeQ("errorLog", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_GreaterEqual(String errorLog) {
        setErrorLog_GreaterEqual(errorLog, null);
    }

    public void setErrorLog_GreaterEqual(String errorLog, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = errorLog;
        RangeQueryBuilder builder = regRangeQ("errorLog", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_LessEqual(String errorLog) {
        setErrorLog_LessEqual(errorLog, null);
    }

    public void setErrorLog_LessEqual(String errorLog, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = errorLog;
        RangeQueryBuilder builder = regRangeQ("errorLog", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_Exists() {
        setErrorLog_Exists(null);
    }

    public void setErrorLog_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("errorLog");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setErrorLog_CommonTerms(String errorLog) {
        setErrorLog_CommonTerms(errorLog, null);
    }

    @Deprecated
    public void setErrorLog_CommonTerms(String errorLog, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("errorLog", errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFailureUrlCQ addOrderBy_ErrorLog_Asc() {
        regOBA("errorLog");
        return this;
    }

    public BsFailureUrlCQ addOrderBy_ErrorLog_Desc() {
        regOBD("errorLog");
        return this;
    }

    public void setErrorName_Equal(String errorName) {
        setErrorName_Term(errorName, null);
    }

    public void setErrorName_Equal(String errorName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setErrorName_Term(errorName, opLambda);
    }

    public void setErrorName_Term(String errorName) {
        setErrorName_Term(errorName, null);
    }

    public void setErrorName_Term(String errorName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("errorName", errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_NotEqual(String errorName) {
        setErrorName_NotTerm(errorName, null);
    }

    public void setErrorName_NotTerm(String errorName) {
        setErrorName_NotTerm(errorName, null);
    }

    public void setErrorName_NotEqual(String errorName, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setErrorName_NotTerm(errorName, opLambda);
    }

    public void setErrorName_NotTerm(String errorName, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setErrorName_Term(errorName), opLambda);
    }

    public void setErrorName_Terms(Collection<String> errorNameList) {
        setErrorName_Terms(errorNameList, null);
    }

    public void setErrorName_Terms(Collection<String> errorNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("errorName", errorNameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_InScope(Collection<String> errorNameList) {
        setErrorName_Terms(errorNameList, null);
    }

    public void setErrorName_InScope(Collection<String> errorNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setErrorName_Terms(errorNameList, opLambda);
    }

    public void setErrorName_Match(String errorName) {
        setErrorName_Match(errorName, null);
    }

    public void setErrorName_Match(String errorName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("errorName", errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_MatchPhrase(String errorName) {
        setErrorName_MatchPhrase(errorName, null);
    }

    public void setErrorName_MatchPhrase(String errorName, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("errorName", errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_MatchPhrasePrefix(String errorName) {
        setErrorName_MatchPhrasePrefix(errorName, null);
    }

    public void setErrorName_MatchPhrasePrefix(String errorName, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("errorName", errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_Fuzzy(String errorName) {
        setErrorName_Fuzzy(errorName, null);
    }

    public void setErrorName_Fuzzy(String errorName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("errorName", errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_Prefix(String errorName) {
        setErrorName_Prefix(errorName, null);
    }

    public void setErrorName_Prefix(String errorName, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("errorName", errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_Wildcard(String errorName) {
        setErrorName_Wildcard(errorName, null);
    }

    public void setErrorName_Wildcard(String errorName, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("errorName", errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_Regexp(String errorName) {
        setErrorName_Regexp(errorName, null);
    }

    public void setErrorName_Regexp(String errorName, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("errorName", errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_SpanTerm(String errorName) {
        setErrorName_SpanTerm("errorName", null);
    }

    public void setErrorName_SpanTerm(String errorName, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("errorName", errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_GreaterThan(String errorName) {
        setErrorName_GreaterThan(errorName, null);
    }

    public void setErrorName_GreaterThan(String errorName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = errorName;
        RangeQueryBuilder builder = regRangeQ("errorName", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_LessThan(String errorName) {
        setErrorName_LessThan(errorName, null);
    }

    public void setErrorName_LessThan(String errorName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = errorName;
        RangeQueryBuilder builder = regRangeQ("errorName", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_GreaterEqual(String errorName) {
        setErrorName_GreaterEqual(errorName, null);
    }

    public void setErrorName_GreaterEqual(String errorName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = errorName;
        RangeQueryBuilder builder = regRangeQ("errorName", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_LessEqual(String errorName) {
        setErrorName_LessEqual(errorName, null);
    }

    public void setErrorName_LessEqual(String errorName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = errorName;
        RangeQueryBuilder builder = regRangeQ("errorName", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_Exists() {
        setErrorName_Exists(null);
    }

    public void setErrorName_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("errorName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setErrorName_CommonTerms(String errorName) {
        setErrorName_CommonTerms(errorName, null);
    }

    @Deprecated
    public void setErrorName_CommonTerms(String errorName, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("errorName", errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFailureUrlCQ addOrderBy_ErrorName_Asc() {
        regOBA("errorName");
        return this;
    }

    public BsFailureUrlCQ addOrderBy_ErrorName_Desc() {
        regOBD("errorName");
        return this;
    }

    public void setHttpStatusCode_Equal(Integer httpStatusCode) {
        setHttpStatusCode_Term(httpStatusCode, null);
    }

    public void setHttpStatusCode_Equal(Integer httpStatusCode, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setHttpStatusCode_Term(httpStatusCode, opLambda);
    }

    public void setHttpStatusCode_Term(Integer httpStatusCode) {
        setHttpStatusCode_Term(httpStatusCode, null);
    }

    public void setHttpStatusCode_Term(Integer httpStatusCode, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("httpStatusCode", httpStatusCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHttpStatusCode_NotEqual(Integer httpStatusCode) {
        setHttpStatusCode_NotTerm(httpStatusCode, null);
    }

    public void setHttpStatusCode_NotTerm(Integer httpStatusCode) {
        setHttpStatusCode_NotTerm(httpStatusCode, null);
    }

    public void setHttpStatusCode_NotEqual(Integer httpStatusCode, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setHttpStatusCode_NotTerm(httpStatusCode, opLambda);
    }

    public void setHttpStatusCode_NotTerm(Integer httpStatusCode, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setHttpStatusCode_Term(httpStatusCode), opLambda);
    }

    public void setHttpStatusCode_Terms(Collection<Integer> httpStatusCodeList) {
        setHttpStatusCode_Terms(httpStatusCodeList, null);
    }

    public void setHttpStatusCode_Terms(Collection<Integer> httpStatusCodeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("httpStatusCode", httpStatusCodeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHttpStatusCode_InScope(Collection<Integer> httpStatusCodeList) {
        setHttpStatusCode_Terms(httpStatusCodeList, null);
    }

    public void setHttpStatusCode_InScope(Collection<Integer> httpStatusCodeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setHttpStatusCode_Terms(httpStatusCodeList, opLambda);
    }

    public void setHttpStatusCode_Match(Integer httpStatusCode) {
        setHttpStatusCode_Match(httpStatusCode, null);
    }

    public void setHttpStatusCode_Match(Integer httpStatusCode, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("httpStatusCode", httpStatusCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHttpStatusCode_MatchPhrase(Integer httpStatusCode) {
        setHttpStatusCode_MatchPhrase(httpStatusCode, null);
    }

    public void setHttpStatusCode_MatchPhrase(Integer httpStatusCode, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("httpStatusCode", httpStatusCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHttpStatusCode_MatchPhrasePrefix(Integer httpStatusCode) {
        setHttpStatusCode_MatchPhrasePrefix(httpStatusCode, null);
    }

    public void setHttpStatusCode_MatchPhrasePrefix(Integer httpStatusCode, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("httpStatusCode", httpStatusCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHttpStatusCode_Fuzzy(Integer httpStatusCode) {
        setHttpStatusCode_Fuzzy(httpStatusCode, null);
    }

    public void setHttpStatusCode_Fuzzy(Integer httpStatusCode, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("httpStatusCode", httpStatusCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHttpStatusCode_GreaterThan(Integer httpStatusCode) {
        setHttpStatusCode_GreaterThan(httpStatusCode, null);
    }

    public void setHttpStatusCode_GreaterThan(Integer httpStatusCode, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = httpStatusCode;
        RangeQueryBuilder builder = regRangeQ("httpStatusCode", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHttpStatusCode_LessThan(Integer httpStatusCode) {
        setHttpStatusCode_LessThan(httpStatusCode, null);
    }

    public void setHttpStatusCode_LessThan(Integer httpStatusCode, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = httpStatusCode;
        RangeQueryBuilder builder = regRangeQ("httpStatusCode", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHttpStatusCode_GreaterEqual(Integer httpStatusCode) {
        setHttpStatusCode_GreaterEqual(httpStatusCode, null);
    }

    public void setHttpStatusCode_GreaterEqual(Integer httpStatusCode, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = httpStatusCode;
        RangeQueryBuilder builder = regRangeQ("httpStatusCode", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHttpStatusCode_LessEqual(Integer httpStatusCode) {
        setHttpStatusCode_LessEqual(httpStatusCode, null);
    }

    public void setHttpStatusCode_LessEqual(Integer httpStatusCode, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = httpStatusCode;
        RangeQueryBuilder builder = regRangeQ("httpStatusCode", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHttpStatusCode_Exists() {
        setHttpStatusCode_Exists(null);
    }

    public void setHttpStatusCode_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("httpStatusCode");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setHttpStatusCode_CommonTerms(Integer httpStatusCode) {
        setHttpStatusCode_CommonTerms(httpStatusCode, null);
    }

    @Deprecated
    public void setHttpStatusCode_CommonTerms(Integer httpStatusCode, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("httpStatusCode", httpStatusCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFailureUrlCQ addOrderBy_HttpStatusCode_Asc() {
        regOBA("httpStatusCode");
        return this;
    }

    public BsFailureUrlCQ addOrderBy_HttpStatusCode_Desc() {
        regOBD("httpStatusCode");
        return this;
    }

    public void setLastAccessTime_Equal(Long lastAccessTime) {
        setLastAccessTime_Term(lastAccessTime, null);
    }

    public void setLastAccessTime_Equal(Long lastAccessTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setLastAccessTime_Term(lastAccessTime, opLambda);
    }

    public void setLastAccessTime_Term(Long lastAccessTime) {
        setLastAccessTime_Term(lastAccessTime, null);
    }

    public void setLastAccessTime_Term(Long lastAccessTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("lastAccessTime", lastAccessTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_NotEqual(Long lastAccessTime) {
        setLastAccessTime_NotTerm(lastAccessTime, null);
    }

    public void setLastAccessTime_NotTerm(Long lastAccessTime) {
        setLastAccessTime_NotTerm(lastAccessTime, null);
    }

    public void setLastAccessTime_NotEqual(Long lastAccessTime, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setLastAccessTime_NotTerm(lastAccessTime, opLambda);
    }

    public void setLastAccessTime_NotTerm(Long lastAccessTime, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setLastAccessTime_Term(lastAccessTime), opLambda);
    }

    public void setLastAccessTime_Terms(Collection<Long> lastAccessTimeList) {
        setLastAccessTime_Terms(lastAccessTimeList, null);
    }

    public void setLastAccessTime_Terms(Collection<Long> lastAccessTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("lastAccessTime", lastAccessTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_InScope(Collection<Long> lastAccessTimeList) {
        setLastAccessTime_Terms(lastAccessTimeList, null);
    }

    public void setLastAccessTime_InScope(Collection<Long> lastAccessTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setLastAccessTime_Terms(lastAccessTimeList, opLambda);
    }

    public void setLastAccessTime_Match(Long lastAccessTime) {
        setLastAccessTime_Match(lastAccessTime, null);
    }

    public void setLastAccessTime_Match(Long lastAccessTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("lastAccessTime", lastAccessTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_MatchPhrase(Long lastAccessTime) {
        setLastAccessTime_MatchPhrase(lastAccessTime, null);
    }

    public void setLastAccessTime_MatchPhrase(Long lastAccessTime, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("lastAccessTime", lastAccessTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_MatchPhrasePrefix(Long lastAccessTime) {
        setLastAccessTime_MatchPhrasePrefix(lastAccessTime, null);
    }

    public void setLastAccessTime_MatchPhrasePrefix(Long lastAccessTime, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("lastAccessTime", lastAccessTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_Fuzzy(Long lastAccessTime) {
        setLastAccessTime_Fuzzy(lastAccessTime, null);
    }

    public void setLastAccessTime_Fuzzy(Long lastAccessTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("lastAccessTime", lastAccessTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_GreaterThan(Long lastAccessTime) {
        setLastAccessTime_GreaterThan(lastAccessTime, null);
    }

    public void setLastAccessTime_GreaterThan(Long lastAccessTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = lastAccessTime;
        RangeQueryBuilder builder = regRangeQ("lastAccessTime", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_LessThan(Long lastAccessTime) {
        setLastAccessTime_LessThan(lastAccessTime, null);
    }

    public void setLastAccessTime_LessThan(Long lastAccessTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = lastAccessTime;
        RangeQueryBuilder builder = regRangeQ("lastAccessTime", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_GreaterEqual(Long lastAccessTime) {
        setLastAccessTime_GreaterEqual(lastAccessTime, null);
    }

    public void setLastAccessTime_GreaterEqual(Long lastAccessTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = lastAccessTime;
        RangeQueryBuilder builder = regRangeQ("lastAccessTime", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_LessEqual(Long lastAccessTime) {
        setLastAccessTime_LessEqual(lastAccessTime, null);
    }

    public void setLastAccessTime_LessEqual(Long lastAccessTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = lastAccessTime;
        RangeQueryBuilder builder = regRangeQ("lastAccessTime", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_Exists() {
        setLastAccessTime_Exists(null);
    }

    public void setLastAccessTime_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("lastAccessTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setLastAccessTime_CommonTerms(Long lastAccessTime) {
        setLastAccessTime_CommonTerms(lastAccessTime, null);
    }

    @Deprecated
    public void setLastAccessTime_CommonTerms(Long lastAccessTime, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("lastAccessTime", lastAccessTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFailureUrlCQ addOrderBy_LastAccessTime_Asc() {
        regOBA("lastAccessTime");
        return this;
    }

    public BsFailureUrlCQ addOrderBy_LastAccessTime_Desc() {
        regOBD("lastAccessTime");
        return this;
    }

    public void setMimeType_Equal(String mimeType) {
        setMimeType_Term(mimeType, null);
    }

    public void setMimeType_Equal(String mimeType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setMimeType_Term(mimeType, opLambda);
    }

    public void setMimeType_Term(String mimeType) {
        setMimeType_Term(mimeType, null);
    }

    public void setMimeType_Term(String mimeType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("mimeType", mimeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMimeType_NotEqual(String mimeType) {
        setMimeType_NotTerm(mimeType, null);
    }

    public void setMimeType_NotTerm(String mimeType) {
        setMimeType_NotTerm(mimeType, null);
    }

    public void setMimeType_NotEqual(String mimeType, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setMimeType_NotTerm(mimeType, opLambda);
    }

    public void setMimeType_NotTerm(String mimeType, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setMimeType_Term(mimeType), opLambda);
    }

    public void setMimeType_Terms(Collection<String> mimeTypeList) {
        setMimeType_Terms(mimeTypeList, null);
    }

    public void setMimeType_Terms(Collection<String> mimeTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("mimeType", mimeTypeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMimeType_InScope(Collection<String> mimeTypeList) {
        setMimeType_Terms(mimeTypeList, null);
    }

    public void setMimeType_InScope(Collection<String> mimeTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setMimeType_Terms(mimeTypeList, opLambda);
    }

    public void setMimeType_Match(String mimeType) {
        setMimeType_Match(mimeType, null);
    }

    public void setMimeType_Match(String mimeType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("mimeType", mimeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMimeType_MatchPhrase(String mimeType) {
        setMimeType_MatchPhrase(mimeType, null);
    }

    public void setMimeType_MatchPhrase(String mimeType, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("mimeType", mimeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMimeType_MatchPhrasePrefix(String mimeType) {
        setMimeType_MatchPhrasePrefix(mimeType, null);
    }

    public void setMimeType_MatchPhrasePrefix(String mimeType, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("mimeType", mimeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMimeType_Fuzzy(String mimeType) {
        setMimeType_Fuzzy(mimeType, null);
    }

    public void setMimeType_Fuzzy(String mimeType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("mimeType", mimeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMimeType_Prefix(String mimeType) {
        setMimeType_Prefix(mimeType, null);
    }

    public void setMimeType_Prefix(String mimeType, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("mimeType", mimeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMimeType_Wildcard(String mimeType) {
        setMimeType_Wildcard(mimeType, null);
    }

    public void setMimeType_Wildcard(String mimeType, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("mimeType", mimeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMimeType_Regexp(String mimeType) {
        setMimeType_Regexp(mimeType, null);
    }

    public void setMimeType_Regexp(String mimeType, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("mimeType", mimeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMimeType_SpanTerm(String mimeType) {
        setMimeType_SpanTerm("mimeType", null);
    }

    public void setMimeType_SpanTerm(String mimeType, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("mimeType", mimeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMimeType_GreaterThan(String mimeType) {
        setMimeType_GreaterThan(mimeType, null);
    }

    public void setMimeType_GreaterThan(String mimeType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = mimeType;
        RangeQueryBuilder builder = regRangeQ("mimeType", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMimeType_LessThan(String mimeType) {
        setMimeType_LessThan(mimeType, null);
    }

    public void setMimeType_LessThan(String mimeType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = mimeType;
        RangeQueryBuilder builder = regRangeQ("mimeType", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMimeType_GreaterEqual(String mimeType) {
        setMimeType_GreaterEqual(mimeType, null);
    }

    public void setMimeType_GreaterEqual(String mimeType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = mimeType;
        RangeQueryBuilder builder = regRangeQ("mimeType", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMimeType_LessEqual(String mimeType) {
        setMimeType_LessEqual(mimeType, null);
    }

    public void setMimeType_LessEqual(String mimeType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = mimeType;
        RangeQueryBuilder builder = regRangeQ("mimeType", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMimeType_Exists() {
        setMimeType_Exists(null);
    }

    public void setMimeType_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("mimeType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setMimeType_CommonTerms(String mimeType) {
        setMimeType_CommonTerms(mimeType, null);
    }

    @Deprecated
    public void setMimeType_CommonTerms(String mimeType, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("mimeType", mimeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFailureUrlCQ addOrderBy_MimeType_Asc() {
        regOBA("mimeType");
        return this;
    }

    public BsFailureUrlCQ addOrderBy_MimeType_Desc() {
        regOBD("mimeType");
        return this;
    }

    public void setThreadName_Equal(String threadName) {
        setThreadName_Term(threadName, null);
    }

    public void setThreadName_Equal(String threadName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setThreadName_Term(threadName, opLambda);
    }

    public void setThreadName_Term(String threadName) {
        setThreadName_Term(threadName, null);
    }

    public void setThreadName_Term(String threadName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("threadName", threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_NotEqual(String threadName) {
        setThreadName_NotTerm(threadName, null);
    }

    public void setThreadName_NotTerm(String threadName) {
        setThreadName_NotTerm(threadName, null);
    }

    public void setThreadName_NotEqual(String threadName, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setThreadName_NotTerm(threadName, opLambda);
    }

    public void setThreadName_NotTerm(String threadName, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setThreadName_Term(threadName), opLambda);
    }

    public void setThreadName_Terms(Collection<String> threadNameList) {
        setThreadName_Terms(threadNameList, null);
    }

    public void setThreadName_Terms(Collection<String> threadNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("threadName", threadNameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_InScope(Collection<String> threadNameList) {
        setThreadName_Terms(threadNameList, null);
    }

    public void setThreadName_InScope(Collection<String> threadNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setThreadName_Terms(threadNameList, opLambda);
    }

    public void setThreadName_Match(String threadName) {
        setThreadName_Match(threadName, null);
    }

    public void setThreadName_Match(String threadName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("threadName", threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_MatchPhrase(String threadName) {
        setThreadName_MatchPhrase(threadName, null);
    }

    public void setThreadName_MatchPhrase(String threadName, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("threadName", threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_MatchPhrasePrefix(String threadName) {
        setThreadName_MatchPhrasePrefix(threadName, null);
    }

    public void setThreadName_MatchPhrasePrefix(String threadName, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("threadName", threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_Fuzzy(String threadName) {
        setThreadName_Fuzzy(threadName, null);
    }

    public void setThreadName_Fuzzy(String threadName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("threadName", threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_Prefix(String threadName) {
        setThreadName_Prefix(threadName, null);
    }

    public void setThreadName_Prefix(String threadName, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("threadName", threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_Wildcard(String threadName) {
        setThreadName_Wildcard(threadName, null);
    }

    public void setThreadName_Wildcard(String threadName, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("threadName", threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_Regexp(String threadName) {
        setThreadName_Regexp(threadName, null);
    }

    public void setThreadName_Regexp(String threadName, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("threadName", threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_SpanTerm(String threadName) {
        setThreadName_SpanTerm("threadName", null);
    }

    public void setThreadName_SpanTerm(String threadName, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("threadName", threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_GreaterThan(String threadName) {
        setThreadName_GreaterThan(threadName, null);
    }

    public void setThreadName_GreaterThan(String threadName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = threadName;
        RangeQueryBuilder builder = regRangeQ("threadName", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_LessThan(String threadName) {
        setThreadName_LessThan(threadName, null);
    }

    public void setThreadName_LessThan(String threadName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = threadName;
        RangeQueryBuilder builder = regRangeQ("threadName", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_GreaterEqual(String threadName) {
        setThreadName_GreaterEqual(threadName, null);
    }

    public void setThreadName_GreaterEqual(String threadName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = threadName;
        RangeQueryBuilder builder = regRangeQ("threadName", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_LessEqual(String threadName) {
        setThreadName_LessEqual(threadName, null);
    }

    public void setThreadName_LessEqual(String threadName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = threadName;
        RangeQueryBuilder builder = regRangeQ("threadName", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_Exists() {
        setThreadName_Exists(null);
    }

    public void setThreadName_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("threadName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setThreadName_CommonTerms(String threadName) {
        setThreadName_CommonTerms(threadName, null);
    }

    @Deprecated
    public void setThreadName_CommonTerms(String threadName, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("threadName", threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFailureUrlCQ addOrderBy_ThreadName_Asc() {
        regOBA("threadName");
        return this;
    }

    public BsFailureUrlCQ addOrderBy_ThreadName_Desc() {
        regOBD("threadName");
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

    public BsFailureUrlCQ addOrderBy_Url_Asc() {
        regOBA("url");
        return this;
    }

    public BsFailureUrlCQ addOrderBy_Url_Desc() {
        regOBD("url");
        return this;
    }

}
