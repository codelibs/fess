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
import org.codelibs.fess.es.config.cbean.cq.PathMappingCQ;
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
public abstract class BsPathMappingCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "path_mapping";
    }

    @Override
    public String xgetAliasName() {
        return "path_mapping";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<PathMappingCQ> queryLambda,
            ScoreFunctionCall<ScoreFunctionCreator<PathMappingCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        PathMappingCQ cq = new PathMappingCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                PathMappingCQ cf = new PathMappingCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<PathMappingCQ, PathMappingCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<PathMappingCQ, PathMappingCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<PathMappingCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<PathMappingCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<PathMappingCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<PathMappingCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        PathMappingCQ mustQuery = new PathMappingCQ();
        PathMappingCQ shouldQuery = new PathMappingCQ();
        PathMappingCQ mustNotQuery = new PathMappingCQ();
        PathMappingCQ filterQuery = new PathMappingCQ();
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
    public BsPathMappingCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsPathMappingCQ addOrderBy_Id_Desc() {
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

    public BsPathMappingCQ addOrderBy_CreatedBy_Asc() {
        regOBA("createdBy");
        return this;
    }

    public BsPathMappingCQ addOrderBy_CreatedBy_Desc() {
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

    public BsPathMappingCQ addOrderBy_CreatedTime_Asc() {
        regOBA("createdTime");
        return this;
    }

    public BsPathMappingCQ addOrderBy_CreatedTime_Desc() {
        regOBD("createdTime");
        return this;
    }

    public void setProcessType_Equal(String processType) {
        setProcessType_Term(processType, null);
    }

    public void setProcessType_Equal(String processType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setProcessType_Term(processType, opLambda);
    }

    public void setProcessType_Term(String processType) {
        setProcessType_Term(processType, null);
    }

    public void setProcessType_Term(String processType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("processType", processType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProcessType_NotEqual(String processType) {
        setProcessType_NotTerm(processType, null);
    }

    public void setProcessType_NotTerm(String processType) {
        setProcessType_NotTerm(processType, null);
    }

    public void setProcessType_NotEqual(String processType, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setProcessType_NotTerm(processType, opLambda);
    }

    public void setProcessType_NotTerm(String processType, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setProcessType_Term(processType), opLambda);
    }

    public void setProcessType_Terms(Collection<String> processTypeList) {
        setProcessType_Terms(processTypeList, null);
    }

    public void setProcessType_Terms(Collection<String> processTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("processType", processTypeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProcessType_InScope(Collection<String> processTypeList) {
        setProcessType_Terms(processTypeList, null);
    }

    public void setProcessType_InScope(Collection<String> processTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setProcessType_Terms(processTypeList, opLambda);
    }

    public void setProcessType_Match(String processType) {
        setProcessType_Match(processType, null);
    }

    public void setProcessType_Match(String processType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("processType", processType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProcessType_MatchPhrase(String processType) {
        setProcessType_MatchPhrase(processType, null);
    }

    public void setProcessType_MatchPhrase(String processType, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("processType", processType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProcessType_MatchPhrasePrefix(String processType) {
        setProcessType_MatchPhrasePrefix(processType, null);
    }

    public void setProcessType_MatchPhrasePrefix(String processType, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("processType", processType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProcessType_Fuzzy(String processType) {
        setProcessType_Fuzzy(processType, null);
    }

    public void setProcessType_Fuzzy(String processType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("processType", processType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProcessType_Prefix(String processType) {
        setProcessType_Prefix(processType, null);
    }

    public void setProcessType_Prefix(String processType, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("processType", processType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProcessType_Wildcard(String processType) {
        setProcessType_Wildcard(processType, null);
    }

    public void setProcessType_Wildcard(String processType, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("processType", processType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProcessType_Regexp(String processType) {
        setProcessType_Regexp(processType, null);
    }

    public void setProcessType_Regexp(String processType, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("processType", processType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProcessType_SpanTerm(String processType) {
        setProcessType_SpanTerm("processType", null);
    }

    public void setProcessType_SpanTerm(String processType, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("processType", processType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProcessType_GreaterThan(String processType) {
        setProcessType_GreaterThan(processType, null);
    }

    public void setProcessType_GreaterThan(String processType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = processType;
        RangeQueryBuilder builder = regRangeQ("processType", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProcessType_LessThan(String processType) {
        setProcessType_LessThan(processType, null);
    }

    public void setProcessType_LessThan(String processType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = processType;
        RangeQueryBuilder builder = regRangeQ("processType", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProcessType_GreaterEqual(String processType) {
        setProcessType_GreaterEqual(processType, null);
    }

    public void setProcessType_GreaterEqual(String processType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = processType;
        RangeQueryBuilder builder = regRangeQ("processType", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProcessType_LessEqual(String processType) {
        setProcessType_LessEqual(processType, null);
    }

    public void setProcessType_LessEqual(String processType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = processType;
        RangeQueryBuilder builder = regRangeQ("processType", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProcessType_Exists() {
        setProcessType_Exists(null);
    }

    public void setProcessType_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("processType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setProcessType_CommonTerms(String processType) {
        setProcessType_CommonTerms(processType, null);
    }

    @Deprecated
    public void setProcessType_CommonTerms(String processType, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("processType", processType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsPathMappingCQ addOrderBy_ProcessType_Asc() {
        regOBA("processType");
        return this;
    }

    public BsPathMappingCQ addOrderBy_ProcessType_Desc() {
        regOBD("processType");
        return this;
    }

    public void setRegex_Equal(String regex) {
        setRegex_Term(regex, null);
    }

    public void setRegex_Equal(String regex, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setRegex_Term(regex, opLambda);
    }

    public void setRegex_Term(String regex) {
        setRegex_Term(regex, null);
    }

    public void setRegex_Term(String regex, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("regex", regex);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegex_NotEqual(String regex) {
        setRegex_NotTerm(regex, null);
    }

    public void setRegex_NotTerm(String regex) {
        setRegex_NotTerm(regex, null);
    }

    public void setRegex_NotEqual(String regex, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setRegex_NotTerm(regex, opLambda);
    }

    public void setRegex_NotTerm(String regex, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setRegex_Term(regex), opLambda);
    }

    public void setRegex_Terms(Collection<String> regexList) {
        setRegex_Terms(regexList, null);
    }

    public void setRegex_Terms(Collection<String> regexList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("regex", regexList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegex_InScope(Collection<String> regexList) {
        setRegex_Terms(regexList, null);
    }

    public void setRegex_InScope(Collection<String> regexList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setRegex_Terms(regexList, opLambda);
    }

    public void setRegex_Match(String regex) {
        setRegex_Match(regex, null);
    }

    public void setRegex_Match(String regex, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("regex", regex);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegex_MatchPhrase(String regex) {
        setRegex_MatchPhrase(regex, null);
    }

    public void setRegex_MatchPhrase(String regex, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("regex", regex);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegex_MatchPhrasePrefix(String regex) {
        setRegex_MatchPhrasePrefix(regex, null);
    }

    public void setRegex_MatchPhrasePrefix(String regex, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("regex", regex);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegex_Fuzzy(String regex) {
        setRegex_Fuzzy(regex, null);
    }

    public void setRegex_Fuzzy(String regex, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("regex", regex);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegex_Prefix(String regex) {
        setRegex_Prefix(regex, null);
    }

    public void setRegex_Prefix(String regex, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("regex", regex);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegex_Wildcard(String regex) {
        setRegex_Wildcard(regex, null);
    }

    public void setRegex_Wildcard(String regex, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("regex", regex);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegex_Regexp(String regex) {
        setRegex_Regexp(regex, null);
    }

    public void setRegex_Regexp(String regex, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("regex", regex);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegex_SpanTerm(String regex) {
        setRegex_SpanTerm("regex", null);
    }

    public void setRegex_SpanTerm(String regex, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("regex", regex);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegex_GreaterThan(String regex) {
        setRegex_GreaterThan(regex, null);
    }

    public void setRegex_GreaterThan(String regex, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = regex;
        RangeQueryBuilder builder = regRangeQ("regex", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegex_LessThan(String regex) {
        setRegex_LessThan(regex, null);
    }

    public void setRegex_LessThan(String regex, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = regex;
        RangeQueryBuilder builder = regRangeQ("regex", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegex_GreaterEqual(String regex) {
        setRegex_GreaterEqual(regex, null);
    }

    public void setRegex_GreaterEqual(String regex, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = regex;
        RangeQueryBuilder builder = regRangeQ("regex", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegex_LessEqual(String regex) {
        setRegex_LessEqual(regex, null);
    }

    public void setRegex_LessEqual(String regex, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = regex;
        RangeQueryBuilder builder = regRangeQ("regex", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegex_Exists() {
        setRegex_Exists(null);
    }

    public void setRegex_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("regex");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setRegex_CommonTerms(String regex) {
        setRegex_CommonTerms(regex, null);
    }

    @Deprecated
    public void setRegex_CommonTerms(String regex, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("regex", regex);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsPathMappingCQ addOrderBy_Regex_Asc() {
        regOBA("regex");
        return this;
    }

    public BsPathMappingCQ addOrderBy_Regex_Desc() {
        regOBD("regex");
        return this;
    }

    public void setReplacement_Equal(String replacement) {
        setReplacement_Term(replacement, null);
    }

    public void setReplacement_Equal(String replacement, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setReplacement_Term(replacement, opLambda);
    }

    public void setReplacement_Term(String replacement) {
        setReplacement_Term(replacement, null);
    }

    public void setReplacement_Term(String replacement, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("replacement", replacement);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReplacement_NotEqual(String replacement) {
        setReplacement_NotTerm(replacement, null);
    }

    public void setReplacement_NotTerm(String replacement) {
        setReplacement_NotTerm(replacement, null);
    }

    public void setReplacement_NotEqual(String replacement, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setReplacement_NotTerm(replacement, opLambda);
    }

    public void setReplacement_NotTerm(String replacement, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setReplacement_Term(replacement), opLambda);
    }

    public void setReplacement_Terms(Collection<String> replacementList) {
        setReplacement_Terms(replacementList, null);
    }

    public void setReplacement_Terms(Collection<String> replacementList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("replacement", replacementList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReplacement_InScope(Collection<String> replacementList) {
        setReplacement_Terms(replacementList, null);
    }

    public void setReplacement_InScope(Collection<String> replacementList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setReplacement_Terms(replacementList, opLambda);
    }

    public void setReplacement_Match(String replacement) {
        setReplacement_Match(replacement, null);
    }

    public void setReplacement_Match(String replacement, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("replacement", replacement);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReplacement_MatchPhrase(String replacement) {
        setReplacement_MatchPhrase(replacement, null);
    }

    public void setReplacement_MatchPhrase(String replacement, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("replacement", replacement);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReplacement_MatchPhrasePrefix(String replacement) {
        setReplacement_MatchPhrasePrefix(replacement, null);
    }

    public void setReplacement_MatchPhrasePrefix(String replacement, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("replacement", replacement);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReplacement_Fuzzy(String replacement) {
        setReplacement_Fuzzy(replacement, null);
    }

    public void setReplacement_Fuzzy(String replacement, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("replacement", replacement);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReplacement_Prefix(String replacement) {
        setReplacement_Prefix(replacement, null);
    }

    public void setReplacement_Prefix(String replacement, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("replacement", replacement);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReplacement_Wildcard(String replacement) {
        setReplacement_Wildcard(replacement, null);
    }

    public void setReplacement_Wildcard(String replacement, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("replacement", replacement);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReplacement_Regexp(String replacement) {
        setReplacement_Regexp(replacement, null);
    }

    public void setReplacement_Regexp(String replacement, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("replacement", replacement);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReplacement_SpanTerm(String replacement) {
        setReplacement_SpanTerm("replacement", null);
    }

    public void setReplacement_SpanTerm(String replacement, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("replacement", replacement);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReplacement_GreaterThan(String replacement) {
        setReplacement_GreaterThan(replacement, null);
    }

    public void setReplacement_GreaterThan(String replacement, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = replacement;
        RangeQueryBuilder builder = regRangeQ("replacement", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReplacement_LessThan(String replacement) {
        setReplacement_LessThan(replacement, null);
    }

    public void setReplacement_LessThan(String replacement, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = replacement;
        RangeQueryBuilder builder = regRangeQ("replacement", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReplacement_GreaterEqual(String replacement) {
        setReplacement_GreaterEqual(replacement, null);
    }

    public void setReplacement_GreaterEqual(String replacement, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = replacement;
        RangeQueryBuilder builder = regRangeQ("replacement", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReplacement_LessEqual(String replacement) {
        setReplacement_LessEqual(replacement, null);
    }

    public void setReplacement_LessEqual(String replacement, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = replacement;
        RangeQueryBuilder builder = regRangeQ("replacement", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReplacement_Exists() {
        setReplacement_Exists(null);
    }

    public void setReplacement_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("replacement");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setReplacement_CommonTerms(String replacement) {
        setReplacement_CommonTerms(replacement, null);
    }

    @Deprecated
    public void setReplacement_CommonTerms(String replacement, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("replacement", replacement);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsPathMappingCQ addOrderBy_Replacement_Asc() {
        regOBA("replacement");
        return this;
    }

    public BsPathMappingCQ addOrderBy_Replacement_Desc() {
        regOBD("replacement");
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

    public BsPathMappingCQ addOrderBy_SortOrder_Asc() {
        regOBA("sortOrder");
        return this;
    }

    public BsPathMappingCQ addOrderBy_SortOrder_Desc() {
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

    public BsPathMappingCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("updatedBy");
        return this;
    }

    public BsPathMappingCQ addOrderBy_UpdatedBy_Desc() {
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

    public BsPathMappingCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("updatedTime");
        return this;
    }

    public BsPathMappingCQ addOrderBy_UpdatedTime_Desc() {
        regOBD("updatedTime");
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

    public BsPathMappingCQ addOrderBy_UserAgent_Asc() {
        regOBA("userAgent");
        return this;
    }

    public BsPathMappingCQ addOrderBy_UserAgent_Desc() {
        regOBD("userAgent");
        return this;
    }

}
