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
import org.codelibs.fess.es.config.cbean.cq.BadWordCQ;
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
public abstract class BsBadWordCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "bad_word";
    }

    @Override
    public String xgetAliasName() {
        return "bad_word";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<BadWordCQ> queryLambda, ScoreFunctionCall<ScoreFunctionCreator<BadWordCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        BadWordCQ cq = new BadWordCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                BadWordCQ cf = new BadWordCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<BadWordCQ, BadWordCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<BadWordCQ, BadWordCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<BadWordCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<BadWordCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<BadWordCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<BadWordCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        BadWordCQ mustQuery = new BadWordCQ();
        BadWordCQ shouldQuery = new BadWordCQ();
        BadWordCQ mustNotQuery = new BadWordCQ();
        BadWordCQ filterQuery = new BadWordCQ();
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
    public BsBadWordCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsBadWordCQ addOrderBy_Id_Desc() {
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

    public BsBadWordCQ addOrderBy_CreatedBy_Asc() {
        regOBA("createdBy");
        return this;
    }

    public BsBadWordCQ addOrderBy_CreatedBy_Desc() {
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

    public BsBadWordCQ addOrderBy_CreatedTime_Asc() {
        regOBA("createdTime");
        return this;
    }

    public BsBadWordCQ addOrderBy_CreatedTime_Desc() {
        regOBD("createdTime");
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

    public BsBadWordCQ addOrderBy_SuggestWord_Asc() {
        regOBA("suggestWord");
        return this;
    }

    public BsBadWordCQ addOrderBy_SuggestWord_Desc() {
        regOBD("suggestWord");
        return this;
    }

    public void setTargetLabel_Equal(String targetLabel) {
        setTargetLabel_Term(targetLabel, null);
    }

    public void setTargetLabel_Equal(String targetLabel, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setTargetLabel_Term(targetLabel, opLambda);
    }

    public void setTargetLabel_Term(String targetLabel) {
        setTargetLabel_Term(targetLabel, null);
    }

    public void setTargetLabel_Term(String targetLabel, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("targetLabel", targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_NotEqual(String targetLabel) {
        setTargetLabel_NotTerm(targetLabel, null);
    }

    public void setTargetLabel_NotTerm(String targetLabel) {
        setTargetLabel_NotTerm(targetLabel, null);
    }

    public void setTargetLabel_NotEqual(String targetLabel, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setTargetLabel_NotTerm(targetLabel, opLambda);
    }

    public void setTargetLabel_NotTerm(String targetLabel, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setTargetLabel_Term(targetLabel), opLambda);
    }

    public void setTargetLabel_Terms(Collection<String> targetLabelList) {
        setTargetLabel_Terms(targetLabelList, null);
    }

    public void setTargetLabel_Terms(Collection<String> targetLabelList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("targetLabel", targetLabelList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_InScope(Collection<String> targetLabelList) {
        setTargetLabel_Terms(targetLabelList, null);
    }

    public void setTargetLabel_InScope(Collection<String> targetLabelList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setTargetLabel_Terms(targetLabelList, opLambda);
    }

    public void setTargetLabel_Match(String targetLabel) {
        setTargetLabel_Match(targetLabel, null);
    }

    public void setTargetLabel_Match(String targetLabel, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("targetLabel", targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_MatchPhrase(String targetLabel) {
        setTargetLabel_MatchPhrase(targetLabel, null);
    }

    public void setTargetLabel_MatchPhrase(String targetLabel, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("targetLabel", targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_MatchPhrasePrefix(String targetLabel) {
        setTargetLabel_MatchPhrasePrefix(targetLabel, null);
    }

    public void setTargetLabel_MatchPhrasePrefix(String targetLabel, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("targetLabel", targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_Fuzzy(String targetLabel) {
        setTargetLabel_Fuzzy(targetLabel, null);
    }

    public void setTargetLabel_Fuzzy(String targetLabel, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("targetLabel", targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_Prefix(String targetLabel) {
        setTargetLabel_Prefix(targetLabel, null);
    }

    public void setTargetLabel_Prefix(String targetLabel, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("targetLabel", targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_Wildcard(String targetLabel) {
        setTargetLabel_Wildcard(targetLabel, null);
    }

    public void setTargetLabel_Wildcard(String targetLabel, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("targetLabel", targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_Regexp(String targetLabel) {
        setTargetLabel_Regexp(targetLabel, null);
    }

    public void setTargetLabel_Regexp(String targetLabel, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("targetLabel", targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_SpanTerm(String targetLabel) {
        setTargetLabel_SpanTerm("targetLabel", null);
    }

    public void setTargetLabel_SpanTerm(String targetLabel, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("targetLabel", targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_GreaterThan(String targetLabel) {
        setTargetLabel_GreaterThan(targetLabel, null);
    }

    public void setTargetLabel_GreaterThan(String targetLabel, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = targetLabel;
        RangeQueryBuilder builder = regRangeQ("targetLabel", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_LessThan(String targetLabel) {
        setTargetLabel_LessThan(targetLabel, null);
    }

    public void setTargetLabel_LessThan(String targetLabel, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = targetLabel;
        RangeQueryBuilder builder = regRangeQ("targetLabel", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_GreaterEqual(String targetLabel) {
        setTargetLabel_GreaterEqual(targetLabel, null);
    }

    public void setTargetLabel_GreaterEqual(String targetLabel, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = targetLabel;
        RangeQueryBuilder builder = regRangeQ("targetLabel", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_LessEqual(String targetLabel) {
        setTargetLabel_LessEqual(targetLabel, null);
    }

    public void setTargetLabel_LessEqual(String targetLabel, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = targetLabel;
        RangeQueryBuilder builder = regRangeQ("targetLabel", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_Exists() {
        setTargetLabel_Exists(null);
    }

    public void setTargetLabel_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("targetLabel");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setTargetLabel_CommonTerms(String targetLabel) {
        setTargetLabel_CommonTerms(targetLabel, null);
    }

    @Deprecated
    public void setTargetLabel_CommonTerms(String targetLabel, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("targetLabel", targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsBadWordCQ addOrderBy_TargetLabel_Asc() {
        regOBA("targetLabel");
        return this;
    }

    public BsBadWordCQ addOrderBy_TargetLabel_Desc() {
        regOBD("targetLabel");
        return this;
    }

    public void setTargetRole_Equal(String targetRole) {
        setTargetRole_Term(targetRole, null);
    }

    public void setTargetRole_Equal(String targetRole, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setTargetRole_Term(targetRole, opLambda);
    }

    public void setTargetRole_Term(String targetRole) {
        setTargetRole_Term(targetRole, null);
    }

    public void setTargetRole_Term(String targetRole, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("targetRole", targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_NotEqual(String targetRole) {
        setTargetRole_NotTerm(targetRole, null);
    }

    public void setTargetRole_NotTerm(String targetRole) {
        setTargetRole_NotTerm(targetRole, null);
    }

    public void setTargetRole_NotEqual(String targetRole, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setTargetRole_NotTerm(targetRole, opLambda);
    }

    public void setTargetRole_NotTerm(String targetRole, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setTargetRole_Term(targetRole), opLambda);
    }

    public void setTargetRole_Terms(Collection<String> targetRoleList) {
        setTargetRole_Terms(targetRoleList, null);
    }

    public void setTargetRole_Terms(Collection<String> targetRoleList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("targetRole", targetRoleList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_InScope(Collection<String> targetRoleList) {
        setTargetRole_Terms(targetRoleList, null);
    }

    public void setTargetRole_InScope(Collection<String> targetRoleList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setTargetRole_Terms(targetRoleList, opLambda);
    }

    public void setTargetRole_Match(String targetRole) {
        setTargetRole_Match(targetRole, null);
    }

    public void setTargetRole_Match(String targetRole, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("targetRole", targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_MatchPhrase(String targetRole) {
        setTargetRole_MatchPhrase(targetRole, null);
    }

    public void setTargetRole_MatchPhrase(String targetRole, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("targetRole", targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_MatchPhrasePrefix(String targetRole) {
        setTargetRole_MatchPhrasePrefix(targetRole, null);
    }

    public void setTargetRole_MatchPhrasePrefix(String targetRole, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("targetRole", targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_Fuzzy(String targetRole) {
        setTargetRole_Fuzzy(targetRole, null);
    }

    public void setTargetRole_Fuzzy(String targetRole, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("targetRole", targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_Prefix(String targetRole) {
        setTargetRole_Prefix(targetRole, null);
    }

    public void setTargetRole_Prefix(String targetRole, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("targetRole", targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_Wildcard(String targetRole) {
        setTargetRole_Wildcard(targetRole, null);
    }

    public void setTargetRole_Wildcard(String targetRole, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("targetRole", targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_Regexp(String targetRole) {
        setTargetRole_Regexp(targetRole, null);
    }

    public void setTargetRole_Regexp(String targetRole, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("targetRole", targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_SpanTerm(String targetRole) {
        setTargetRole_SpanTerm("targetRole", null);
    }

    public void setTargetRole_SpanTerm(String targetRole, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("targetRole", targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_GreaterThan(String targetRole) {
        setTargetRole_GreaterThan(targetRole, null);
    }

    public void setTargetRole_GreaterThan(String targetRole, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = targetRole;
        RangeQueryBuilder builder = regRangeQ("targetRole", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_LessThan(String targetRole) {
        setTargetRole_LessThan(targetRole, null);
    }

    public void setTargetRole_LessThan(String targetRole, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = targetRole;
        RangeQueryBuilder builder = regRangeQ("targetRole", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_GreaterEqual(String targetRole) {
        setTargetRole_GreaterEqual(targetRole, null);
    }

    public void setTargetRole_GreaterEqual(String targetRole, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = targetRole;
        RangeQueryBuilder builder = regRangeQ("targetRole", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_LessEqual(String targetRole) {
        setTargetRole_LessEqual(targetRole, null);
    }

    public void setTargetRole_LessEqual(String targetRole, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = targetRole;
        RangeQueryBuilder builder = regRangeQ("targetRole", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_Exists() {
        setTargetRole_Exists(null);
    }

    public void setTargetRole_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("targetRole");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setTargetRole_CommonTerms(String targetRole) {
        setTargetRole_CommonTerms(targetRole, null);
    }

    @Deprecated
    public void setTargetRole_CommonTerms(String targetRole, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("targetRole", targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsBadWordCQ addOrderBy_TargetRole_Asc() {
        regOBA("targetRole");
        return this;
    }

    public BsBadWordCQ addOrderBy_TargetRole_Desc() {
        regOBD("targetRole");
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

    public BsBadWordCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("updatedBy");
        return this;
    }

    public BsBadWordCQ addOrderBy_UpdatedBy_Desc() {
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

    public BsBadWordCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("updatedTime");
        return this;
    }

    public BsBadWordCQ addOrderBy_UpdatedTime_Desc() {
        regOBD("updatedTime");
        return this;
    }

}
