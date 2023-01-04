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
import org.codelibs.fess.es.config.cbean.cq.LabelTypeCQ;
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
public abstract class BsLabelTypeCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "label_type";
    }

    @Override
    public String xgetAliasName() {
        return "label_type";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<LabelTypeCQ> queryLambda, ScoreFunctionCall<ScoreFunctionCreator<LabelTypeCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        LabelTypeCQ cq = new LabelTypeCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                LabelTypeCQ cf = new LabelTypeCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<LabelTypeCQ, LabelTypeCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<LabelTypeCQ, LabelTypeCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<LabelTypeCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<LabelTypeCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<LabelTypeCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<LabelTypeCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        LabelTypeCQ mustQuery = new LabelTypeCQ();
        LabelTypeCQ shouldQuery = new LabelTypeCQ();
        LabelTypeCQ mustNotQuery = new LabelTypeCQ();
        LabelTypeCQ filterQuery = new LabelTypeCQ();
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
    public BsLabelTypeCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsLabelTypeCQ addOrderBy_Id_Desc() {
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

    public BsLabelTypeCQ addOrderBy_CreatedBy_Asc() {
        regOBA("createdBy");
        return this;
    }

    public BsLabelTypeCQ addOrderBy_CreatedBy_Desc() {
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

    public BsLabelTypeCQ addOrderBy_CreatedTime_Asc() {
        regOBA("createdTime");
        return this;
    }

    public BsLabelTypeCQ addOrderBy_CreatedTime_Desc() {
        regOBD("createdTime");
        return this;
    }

    public void setExcludedPaths_Equal(String excludedPaths) {
        setExcludedPaths_Term(excludedPaths, null);
    }

    public void setExcludedPaths_Equal(String excludedPaths, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setExcludedPaths_Term(excludedPaths, opLambda);
    }

    public void setExcludedPaths_Term(String excludedPaths) {
        setExcludedPaths_Term(excludedPaths, null);
    }

    public void setExcludedPaths_Term(String excludedPaths, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("excludedPaths", excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_NotEqual(String excludedPaths) {
        setExcludedPaths_NotTerm(excludedPaths, null);
    }

    public void setExcludedPaths_NotTerm(String excludedPaths) {
        setExcludedPaths_NotTerm(excludedPaths, null);
    }

    public void setExcludedPaths_NotEqual(String excludedPaths, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setExcludedPaths_NotTerm(excludedPaths, opLambda);
    }

    public void setExcludedPaths_NotTerm(String excludedPaths, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setExcludedPaths_Term(excludedPaths), opLambda);
    }

    public void setExcludedPaths_Terms(Collection<String> excludedPathsList) {
        setExcludedPaths_Terms(excludedPathsList, null);
    }

    public void setExcludedPaths_Terms(Collection<String> excludedPathsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("excludedPaths", excludedPathsList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_InScope(Collection<String> excludedPathsList) {
        setExcludedPaths_Terms(excludedPathsList, null);
    }

    public void setExcludedPaths_InScope(Collection<String> excludedPathsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setExcludedPaths_Terms(excludedPathsList, opLambda);
    }

    public void setExcludedPaths_Match(String excludedPaths) {
        setExcludedPaths_Match(excludedPaths, null);
    }

    public void setExcludedPaths_Match(String excludedPaths, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("excludedPaths", excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_MatchPhrase(String excludedPaths) {
        setExcludedPaths_MatchPhrase(excludedPaths, null);
    }

    public void setExcludedPaths_MatchPhrase(String excludedPaths, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("excludedPaths", excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_MatchPhrasePrefix(String excludedPaths) {
        setExcludedPaths_MatchPhrasePrefix(excludedPaths, null);
    }

    public void setExcludedPaths_MatchPhrasePrefix(String excludedPaths, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("excludedPaths", excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_Fuzzy(String excludedPaths) {
        setExcludedPaths_Fuzzy(excludedPaths, null);
    }

    public void setExcludedPaths_Fuzzy(String excludedPaths, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("excludedPaths", excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_Prefix(String excludedPaths) {
        setExcludedPaths_Prefix(excludedPaths, null);
    }

    public void setExcludedPaths_Prefix(String excludedPaths, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("excludedPaths", excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_Wildcard(String excludedPaths) {
        setExcludedPaths_Wildcard(excludedPaths, null);
    }

    public void setExcludedPaths_Wildcard(String excludedPaths, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("excludedPaths", excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_Regexp(String excludedPaths) {
        setExcludedPaths_Regexp(excludedPaths, null);
    }

    public void setExcludedPaths_Regexp(String excludedPaths, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("excludedPaths", excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_SpanTerm(String excludedPaths) {
        setExcludedPaths_SpanTerm("excludedPaths", null);
    }

    public void setExcludedPaths_SpanTerm(String excludedPaths, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("excludedPaths", excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_GreaterThan(String excludedPaths) {
        setExcludedPaths_GreaterThan(excludedPaths, null);
    }

    public void setExcludedPaths_GreaterThan(String excludedPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = excludedPaths;
        RangeQueryBuilder builder = regRangeQ("excludedPaths", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_LessThan(String excludedPaths) {
        setExcludedPaths_LessThan(excludedPaths, null);
    }

    public void setExcludedPaths_LessThan(String excludedPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = excludedPaths;
        RangeQueryBuilder builder = regRangeQ("excludedPaths", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_GreaterEqual(String excludedPaths) {
        setExcludedPaths_GreaterEqual(excludedPaths, null);
    }

    public void setExcludedPaths_GreaterEqual(String excludedPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = excludedPaths;
        RangeQueryBuilder builder = regRangeQ("excludedPaths", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_LessEqual(String excludedPaths) {
        setExcludedPaths_LessEqual(excludedPaths, null);
    }

    public void setExcludedPaths_LessEqual(String excludedPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = excludedPaths;
        RangeQueryBuilder builder = regRangeQ("excludedPaths", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_Exists() {
        setExcludedPaths_Exists(null);
    }

    public void setExcludedPaths_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("excludedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setExcludedPaths_CommonTerms(String excludedPaths) {
        setExcludedPaths_CommonTerms(excludedPaths, null);
    }

    @Deprecated
    public void setExcludedPaths_CommonTerms(String excludedPaths, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("excludedPaths", excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsLabelTypeCQ addOrderBy_ExcludedPaths_Asc() {
        regOBA("excludedPaths");
        return this;
    }

    public BsLabelTypeCQ addOrderBy_ExcludedPaths_Desc() {
        regOBD("excludedPaths");
        return this;
    }

    public void setIncludedPaths_Equal(String includedPaths) {
        setIncludedPaths_Term(includedPaths, null);
    }

    public void setIncludedPaths_Equal(String includedPaths, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setIncludedPaths_Term(includedPaths, opLambda);
    }

    public void setIncludedPaths_Term(String includedPaths) {
        setIncludedPaths_Term(includedPaths, null);
    }

    public void setIncludedPaths_Term(String includedPaths, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("includedPaths", includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_NotEqual(String includedPaths) {
        setIncludedPaths_NotTerm(includedPaths, null);
    }

    public void setIncludedPaths_NotTerm(String includedPaths) {
        setIncludedPaths_NotTerm(includedPaths, null);
    }

    public void setIncludedPaths_NotEqual(String includedPaths, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setIncludedPaths_NotTerm(includedPaths, opLambda);
    }

    public void setIncludedPaths_NotTerm(String includedPaths, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setIncludedPaths_Term(includedPaths), opLambda);
    }

    public void setIncludedPaths_Terms(Collection<String> includedPathsList) {
        setIncludedPaths_Terms(includedPathsList, null);
    }

    public void setIncludedPaths_Terms(Collection<String> includedPathsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("includedPaths", includedPathsList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_InScope(Collection<String> includedPathsList) {
        setIncludedPaths_Terms(includedPathsList, null);
    }

    public void setIncludedPaths_InScope(Collection<String> includedPathsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setIncludedPaths_Terms(includedPathsList, opLambda);
    }

    public void setIncludedPaths_Match(String includedPaths) {
        setIncludedPaths_Match(includedPaths, null);
    }

    public void setIncludedPaths_Match(String includedPaths, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("includedPaths", includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_MatchPhrase(String includedPaths) {
        setIncludedPaths_MatchPhrase(includedPaths, null);
    }

    public void setIncludedPaths_MatchPhrase(String includedPaths, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("includedPaths", includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_MatchPhrasePrefix(String includedPaths) {
        setIncludedPaths_MatchPhrasePrefix(includedPaths, null);
    }

    public void setIncludedPaths_MatchPhrasePrefix(String includedPaths, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("includedPaths", includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_Fuzzy(String includedPaths) {
        setIncludedPaths_Fuzzy(includedPaths, null);
    }

    public void setIncludedPaths_Fuzzy(String includedPaths, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("includedPaths", includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_Prefix(String includedPaths) {
        setIncludedPaths_Prefix(includedPaths, null);
    }

    public void setIncludedPaths_Prefix(String includedPaths, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("includedPaths", includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_Wildcard(String includedPaths) {
        setIncludedPaths_Wildcard(includedPaths, null);
    }

    public void setIncludedPaths_Wildcard(String includedPaths, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("includedPaths", includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_Regexp(String includedPaths) {
        setIncludedPaths_Regexp(includedPaths, null);
    }

    public void setIncludedPaths_Regexp(String includedPaths, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("includedPaths", includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_SpanTerm(String includedPaths) {
        setIncludedPaths_SpanTerm("includedPaths", null);
    }

    public void setIncludedPaths_SpanTerm(String includedPaths, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("includedPaths", includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_GreaterThan(String includedPaths) {
        setIncludedPaths_GreaterThan(includedPaths, null);
    }

    public void setIncludedPaths_GreaterThan(String includedPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = includedPaths;
        RangeQueryBuilder builder = regRangeQ("includedPaths", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_LessThan(String includedPaths) {
        setIncludedPaths_LessThan(includedPaths, null);
    }

    public void setIncludedPaths_LessThan(String includedPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = includedPaths;
        RangeQueryBuilder builder = regRangeQ("includedPaths", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_GreaterEqual(String includedPaths) {
        setIncludedPaths_GreaterEqual(includedPaths, null);
    }

    public void setIncludedPaths_GreaterEqual(String includedPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = includedPaths;
        RangeQueryBuilder builder = regRangeQ("includedPaths", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_LessEqual(String includedPaths) {
        setIncludedPaths_LessEqual(includedPaths, null);
    }

    public void setIncludedPaths_LessEqual(String includedPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = includedPaths;
        RangeQueryBuilder builder = regRangeQ("includedPaths", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_Exists() {
        setIncludedPaths_Exists(null);
    }

    public void setIncludedPaths_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("includedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setIncludedPaths_CommonTerms(String includedPaths) {
        setIncludedPaths_CommonTerms(includedPaths, null);
    }

    @Deprecated
    public void setIncludedPaths_CommonTerms(String includedPaths, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("includedPaths", includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsLabelTypeCQ addOrderBy_IncludedPaths_Asc() {
        regOBA("includedPaths");
        return this;
    }

    public BsLabelTypeCQ addOrderBy_IncludedPaths_Desc() {
        regOBD("includedPaths");
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

    public BsLabelTypeCQ addOrderBy_Name_Asc() {
        regOBA("name");
        return this;
    }

    public BsLabelTypeCQ addOrderBy_Name_Desc() {
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

    public BsLabelTypeCQ addOrderBy_Permissions_Asc() {
        regOBA("permissions");
        return this;
    }

    public BsLabelTypeCQ addOrderBy_Permissions_Desc() {
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

    public BsLabelTypeCQ addOrderBy_SortOrder_Asc() {
        regOBA("sortOrder");
        return this;
    }

    public BsLabelTypeCQ addOrderBy_SortOrder_Desc() {
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

    public BsLabelTypeCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("updatedBy");
        return this;
    }

    public BsLabelTypeCQ addOrderBy_UpdatedBy_Desc() {
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

    public BsLabelTypeCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("updatedTime");
        return this;
    }

    public BsLabelTypeCQ addOrderBy_UpdatedTime_Desc() {
        regOBD("updatedTime");
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

    public BsLabelTypeCQ addOrderBy_Value_Asc() {
        regOBA("value");
        return this;
    }

    public BsLabelTypeCQ addOrderBy_Value_Desc() {
        regOBD("value");
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

    public BsLabelTypeCQ addOrderBy_VirtualHost_Asc() {
        regOBA("virtualHost");
        return this;
    }

    public BsLabelTypeCQ addOrderBy_VirtualHost_Desc() {
        regOBD("virtualHost");
        return this;
    }

}
