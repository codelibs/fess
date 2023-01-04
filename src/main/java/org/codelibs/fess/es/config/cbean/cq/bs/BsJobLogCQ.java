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
import org.codelibs.fess.es.config.cbean.cq.JobLogCQ;
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
public abstract class BsJobLogCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "job_log";
    }

    @Override
    public String xgetAliasName() {
        return "job_log";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<JobLogCQ> queryLambda, ScoreFunctionCall<ScoreFunctionCreator<JobLogCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        JobLogCQ cq = new JobLogCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                JobLogCQ cf = new JobLogCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<JobLogCQ, JobLogCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<JobLogCQ, JobLogCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<JobLogCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<JobLogCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<JobLogCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<JobLogCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        JobLogCQ mustQuery = new JobLogCQ();
        JobLogCQ shouldQuery = new JobLogCQ();
        JobLogCQ mustNotQuery = new JobLogCQ();
        JobLogCQ filterQuery = new JobLogCQ();
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
    public BsJobLogCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsJobLogCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setEndTime_Equal(Long endTime) {
        setEndTime_Term(endTime, null);
    }

    public void setEndTime_Equal(Long endTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setEndTime_Term(endTime, opLambda);
    }

    public void setEndTime_Term(Long endTime) {
        setEndTime_Term(endTime, null);
    }

    public void setEndTime_Term(Long endTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("endTime", endTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_NotEqual(Long endTime) {
        setEndTime_NotTerm(endTime, null);
    }

    public void setEndTime_NotTerm(Long endTime) {
        setEndTime_NotTerm(endTime, null);
    }

    public void setEndTime_NotEqual(Long endTime, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setEndTime_NotTerm(endTime, opLambda);
    }

    public void setEndTime_NotTerm(Long endTime, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setEndTime_Term(endTime), opLambda);
    }

    public void setEndTime_Terms(Collection<Long> endTimeList) {
        setEndTime_Terms(endTimeList, null);
    }

    public void setEndTime_Terms(Collection<Long> endTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("endTime", endTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_InScope(Collection<Long> endTimeList) {
        setEndTime_Terms(endTimeList, null);
    }

    public void setEndTime_InScope(Collection<Long> endTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setEndTime_Terms(endTimeList, opLambda);
    }

    public void setEndTime_Match(Long endTime) {
        setEndTime_Match(endTime, null);
    }

    public void setEndTime_Match(Long endTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("endTime", endTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_MatchPhrase(Long endTime) {
        setEndTime_MatchPhrase(endTime, null);
    }

    public void setEndTime_MatchPhrase(Long endTime, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("endTime", endTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_MatchPhrasePrefix(Long endTime) {
        setEndTime_MatchPhrasePrefix(endTime, null);
    }

    public void setEndTime_MatchPhrasePrefix(Long endTime, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("endTime", endTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Fuzzy(Long endTime) {
        setEndTime_Fuzzy(endTime, null);
    }

    public void setEndTime_Fuzzy(Long endTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("endTime", endTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_GreaterThan(Long endTime) {
        setEndTime_GreaterThan(endTime, null);
    }

    public void setEndTime_GreaterThan(Long endTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = endTime;
        RangeQueryBuilder builder = regRangeQ("endTime", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_LessThan(Long endTime) {
        setEndTime_LessThan(endTime, null);
    }

    public void setEndTime_LessThan(Long endTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = endTime;
        RangeQueryBuilder builder = regRangeQ("endTime", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_GreaterEqual(Long endTime) {
        setEndTime_GreaterEqual(endTime, null);
    }

    public void setEndTime_GreaterEqual(Long endTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = endTime;
        RangeQueryBuilder builder = regRangeQ("endTime", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_LessEqual(Long endTime) {
        setEndTime_LessEqual(endTime, null);
    }

    public void setEndTime_LessEqual(Long endTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = endTime;
        RangeQueryBuilder builder = regRangeQ("endTime", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Exists() {
        setEndTime_Exists(null);
    }

    public void setEndTime_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setEndTime_CommonTerms(Long endTime) {
        setEndTime_CommonTerms(endTime, null);
    }

    @Deprecated
    public void setEndTime_CommonTerms(Long endTime, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("endTime", endTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsJobLogCQ addOrderBy_EndTime_Asc() {
        regOBA("endTime");
        return this;
    }

    public BsJobLogCQ addOrderBy_EndTime_Desc() {
        regOBD("endTime");
        return this;
    }

    public void setJobName_Equal(String jobName) {
        setJobName_Term(jobName, null);
    }

    public void setJobName_Equal(String jobName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setJobName_Term(jobName, opLambda);
    }

    public void setJobName_Term(String jobName) {
        setJobName_Term(jobName, null);
    }

    public void setJobName_Term(String jobName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("jobName", jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_NotEqual(String jobName) {
        setJobName_NotTerm(jobName, null);
    }

    public void setJobName_NotTerm(String jobName) {
        setJobName_NotTerm(jobName, null);
    }

    public void setJobName_NotEqual(String jobName, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setJobName_NotTerm(jobName, opLambda);
    }

    public void setJobName_NotTerm(String jobName, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setJobName_Term(jobName), opLambda);
    }

    public void setJobName_Terms(Collection<String> jobNameList) {
        setJobName_Terms(jobNameList, null);
    }

    public void setJobName_Terms(Collection<String> jobNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("jobName", jobNameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_InScope(Collection<String> jobNameList) {
        setJobName_Terms(jobNameList, null);
    }

    public void setJobName_InScope(Collection<String> jobNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setJobName_Terms(jobNameList, opLambda);
    }

    public void setJobName_Match(String jobName) {
        setJobName_Match(jobName, null);
    }

    public void setJobName_Match(String jobName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("jobName", jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_MatchPhrase(String jobName) {
        setJobName_MatchPhrase(jobName, null);
    }

    public void setJobName_MatchPhrase(String jobName, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("jobName", jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_MatchPhrasePrefix(String jobName) {
        setJobName_MatchPhrasePrefix(jobName, null);
    }

    public void setJobName_MatchPhrasePrefix(String jobName, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("jobName", jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_Fuzzy(String jobName) {
        setJobName_Fuzzy(jobName, null);
    }

    public void setJobName_Fuzzy(String jobName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("jobName", jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_Prefix(String jobName) {
        setJobName_Prefix(jobName, null);
    }

    public void setJobName_Prefix(String jobName, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("jobName", jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_Wildcard(String jobName) {
        setJobName_Wildcard(jobName, null);
    }

    public void setJobName_Wildcard(String jobName, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("jobName", jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_Regexp(String jobName) {
        setJobName_Regexp(jobName, null);
    }

    public void setJobName_Regexp(String jobName, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("jobName", jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_SpanTerm(String jobName) {
        setJobName_SpanTerm("jobName", null);
    }

    public void setJobName_SpanTerm(String jobName, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("jobName", jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_GreaterThan(String jobName) {
        setJobName_GreaterThan(jobName, null);
    }

    public void setJobName_GreaterThan(String jobName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = jobName;
        RangeQueryBuilder builder = regRangeQ("jobName", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_LessThan(String jobName) {
        setJobName_LessThan(jobName, null);
    }

    public void setJobName_LessThan(String jobName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = jobName;
        RangeQueryBuilder builder = regRangeQ("jobName", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_GreaterEqual(String jobName) {
        setJobName_GreaterEqual(jobName, null);
    }

    public void setJobName_GreaterEqual(String jobName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = jobName;
        RangeQueryBuilder builder = regRangeQ("jobName", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_LessEqual(String jobName) {
        setJobName_LessEqual(jobName, null);
    }

    public void setJobName_LessEqual(String jobName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = jobName;
        RangeQueryBuilder builder = regRangeQ("jobName", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_Exists() {
        setJobName_Exists(null);
    }

    public void setJobName_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("jobName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setJobName_CommonTerms(String jobName) {
        setJobName_CommonTerms(jobName, null);
    }

    @Deprecated
    public void setJobName_CommonTerms(String jobName, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("jobName", jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsJobLogCQ addOrderBy_JobName_Asc() {
        regOBA("jobName");
        return this;
    }

    public BsJobLogCQ addOrderBy_JobName_Desc() {
        regOBD("jobName");
        return this;
    }

    public void setJobStatus_Equal(String jobStatus) {
        setJobStatus_Term(jobStatus, null);
    }

    public void setJobStatus_Equal(String jobStatus, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setJobStatus_Term(jobStatus, opLambda);
    }

    public void setJobStatus_Term(String jobStatus) {
        setJobStatus_Term(jobStatus, null);
    }

    public void setJobStatus_Term(String jobStatus, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("jobStatus", jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_NotEqual(String jobStatus) {
        setJobStatus_NotTerm(jobStatus, null);
    }

    public void setJobStatus_NotTerm(String jobStatus) {
        setJobStatus_NotTerm(jobStatus, null);
    }

    public void setJobStatus_NotEqual(String jobStatus, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setJobStatus_NotTerm(jobStatus, opLambda);
    }

    public void setJobStatus_NotTerm(String jobStatus, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setJobStatus_Term(jobStatus), opLambda);
    }

    public void setJobStatus_Terms(Collection<String> jobStatusList) {
        setJobStatus_Terms(jobStatusList, null);
    }

    public void setJobStatus_Terms(Collection<String> jobStatusList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("jobStatus", jobStatusList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_InScope(Collection<String> jobStatusList) {
        setJobStatus_Terms(jobStatusList, null);
    }

    public void setJobStatus_InScope(Collection<String> jobStatusList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setJobStatus_Terms(jobStatusList, opLambda);
    }

    public void setJobStatus_Match(String jobStatus) {
        setJobStatus_Match(jobStatus, null);
    }

    public void setJobStatus_Match(String jobStatus, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("jobStatus", jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_MatchPhrase(String jobStatus) {
        setJobStatus_MatchPhrase(jobStatus, null);
    }

    public void setJobStatus_MatchPhrase(String jobStatus, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("jobStatus", jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_MatchPhrasePrefix(String jobStatus) {
        setJobStatus_MatchPhrasePrefix(jobStatus, null);
    }

    public void setJobStatus_MatchPhrasePrefix(String jobStatus, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("jobStatus", jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_Fuzzy(String jobStatus) {
        setJobStatus_Fuzzy(jobStatus, null);
    }

    public void setJobStatus_Fuzzy(String jobStatus, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("jobStatus", jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_Prefix(String jobStatus) {
        setJobStatus_Prefix(jobStatus, null);
    }

    public void setJobStatus_Prefix(String jobStatus, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("jobStatus", jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_Wildcard(String jobStatus) {
        setJobStatus_Wildcard(jobStatus, null);
    }

    public void setJobStatus_Wildcard(String jobStatus, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("jobStatus", jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_Regexp(String jobStatus) {
        setJobStatus_Regexp(jobStatus, null);
    }

    public void setJobStatus_Regexp(String jobStatus, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("jobStatus", jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_SpanTerm(String jobStatus) {
        setJobStatus_SpanTerm("jobStatus", null);
    }

    public void setJobStatus_SpanTerm(String jobStatus, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("jobStatus", jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_GreaterThan(String jobStatus) {
        setJobStatus_GreaterThan(jobStatus, null);
    }

    public void setJobStatus_GreaterThan(String jobStatus, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = jobStatus;
        RangeQueryBuilder builder = regRangeQ("jobStatus", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_LessThan(String jobStatus) {
        setJobStatus_LessThan(jobStatus, null);
    }

    public void setJobStatus_LessThan(String jobStatus, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = jobStatus;
        RangeQueryBuilder builder = regRangeQ("jobStatus", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_GreaterEqual(String jobStatus) {
        setJobStatus_GreaterEqual(jobStatus, null);
    }

    public void setJobStatus_GreaterEqual(String jobStatus, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = jobStatus;
        RangeQueryBuilder builder = regRangeQ("jobStatus", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_LessEqual(String jobStatus) {
        setJobStatus_LessEqual(jobStatus, null);
    }

    public void setJobStatus_LessEqual(String jobStatus, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = jobStatus;
        RangeQueryBuilder builder = regRangeQ("jobStatus", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_Exists() {
        setJobStatus_Exists(null);
    }

    public void setJobStatus_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("jobStatus");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setJobStatus_CommonTerms(String jobStatus) {
        setJobStatus_CommonTerms(jobStatus, null);
    }

    @Deprecated
    public void setJobStatus_CommonTerms(String jobStatus, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("jobStatus", jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsJobLogCQ addOrderBy_JobStatus_Asc() {
        regOBA("jobStatus");
        return this;
    }

    public BsJobLogCQ addOrderBy_JobStatus_Desc() {
        regOBD("jobStatus");
        return this;
    }

    public void setLastUpdated_Equal(Long lastUpdated) {
        setLastUpdated_Term(lastUpdated, null);
    }

    public void setLastUpdated_Equal(Long lastUpdated, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setLastUpdated_Term(lastUpdated, opLambda);
    }

    public void setLastUpdated_Term(Long lastUpdated) {
        setLastUpdated_Term(lastUpdated, null);
    }

    public void setLastUpdated_Term(Long lastUpdated, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("lastUpdated", lastUpdated);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_NotEqual(Long lastUpdated) {
        setLastUpdated_NotTerm(lastUpdated, null);
    }

    public void setLastUpdated_NotTerm(Long lastUpdated) {
        setLastUpdated_NotTerm(lastUpdated, null);
    }

    public void setLastUpdated_NotEqual(Long lastUpdated, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setLastUpdated_NotTerm(lastUpdated, opLambda);
    }

    public void setLastUpdated_NotTerm(Long lastUpdated, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setLastUpdated_Term(lastUpdated), opLambda);
    }

    public void setLastUpdated_Terms(Collection<Long> lastUpdatedList) {
        setLastUpdated_Terms(lastUpdatedList, null);
    }

    public void setLastUpdated_Terms(Collection<Long> lastUpdatedList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("lastUpdated", lastUpdatedList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_InScope(Collection<Long> lastUpdatedList) {
        setLastUpdated_Terms(lastUpdatedList, null);
    }

    public void setLastUpdated_InScope(Collection<Long> lastUpdatedList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setLastUpdated_Terms(lastUpdatedList, opLambda);
    }

    public void setLastUpdated_Match(Long lastUpdated) {
        setLastUpdated_Match(lastUpdated, null);
    }

    public void setLastUpdated_Match(Long lastUpdated, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("lastUpdated", lastUpdated);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_MatchPhrase(Long lastUpdated) {
        setLastUpdated_MatchPhrase(lastUpdated, null);
    }

    public void setLastUpdated_MatchPhrase(Long lastUpdated, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("lastUpdated", lastUpdated);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_MatchPhrasePrefix(Long lastUpdated) {
        setLastUpdated_MatchPhrasePrefix(lastUpdated, null);
    }

    public void setLastUpdated_MatchPhrasePrefix(Long lastUpdated, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("lastUpdated", lastUpdated);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_Fuzzy(Long lastUpdated) {
        setLastUpdated_Fuzzy(lastUpdated, null);
    }

    public void setLastUpdated_Fuzzy(Long lastUpdated, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("lastUpdated", lastUpdated);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_GreaterThan(Long lastUpdated) {
        setLastUpdated_GreaterThan(lastUpdated, null);
    }

    public void setLastUpdated_GreaterThan(Long lastUpdated, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = lastUpdated;
        RangeQueryBuilder builder = regRangeQ("lastUpdated", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_LessThan(Long lastUpdated) {
        setLastUpdated_LessThan(lastUpdated, null);
    }

    public void setLastUpdated_LessThan(Long lastUpdated, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = lastUpdated;
        RangeQueryBuilder builder = regRangeQ("lastUpdated", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_GreaterEqual(Long lastUpdated) {
        setLastUpdated_GreaterEqual(lastUpdated, null);
    }

    public void setLastUpdated_GreaterEqual(Long lastUpdated, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = lastUpdated;
        RangeQueryBuilder builder = regRangeQ("lastUpdated", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_LessEqual(Long lastUpdated) {
        setLastUpdated_LessEqual(lastUpdated, null);
    }

    public void setLastUpdated_LessEqual(Long lastUpdated, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = lastUpdated;
        RangeQueryBuilder builder = regRangeQ("lastUpdated", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_Exists() {
        setLastUpdated_Exists(null);
    }

    public void setLastUpdated_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setLastUpdated_CommonTerms(Long lastUpdated) {
        setLastUpdated_CommonTerms(lastUpdated, null);
    }

    @Deprecated
    public void setLastUpdated_CommonTerms(Long lastUpdated, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("lastUpdated", lastUpdated);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsJobLogCQ addOrderBy_LastUpdated_Asc() {
        regOBA("lastUpdated");
        return this;
    }

    public BsJobLogCQ addOrderBy_LastUpdated_Desc() {
        regOBD("lastUpdated");
        return this;
    }

    public void setScriptData_Equal(String scriptData) {
        setScriptData_Term(scriptData, null);
    }

    public void setScriptData_Equal(String scriptData, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setScriptData_Term(scriptData, opLambda);
    }

    public void setScriptData_Term(String scriptData) {
        setScriptData_Term(scriptData, null);
    }

    public void setScriptData_Term(String scriptData, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_NotEqual(String scriptData) {
        setScriptData_NotTerm(scriptData, null);
    }

    public void setScriptData_NotTerm(String scriptData) {
        setScriptData_NotTerm(scriptData, null);
    }

    public void setScriptData_NotEqual(String scriptData, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setScriptData_NotTerm(scriptData, opLambda);
    }

    public void setScriptData_NotTerm(String scriptData, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setScriptData_Term(scriptData), opLambda);
    }

    public void setScriptData_Terms(Collection<String> scriptDataList) {
        setScriptData_Terms(scriptDataList, null);
    }

    public void setScriptData_Terms(Collection<String> scriptDataList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("scriptData", scriptDataList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_InScope(Collection<String> scriptDataList) {
        setScriptData_Terms(scriptDataList, null);
    }

    public void setScriptData_InScope(Collection<String> scriptDataList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setScriptData_Terms(scriptDataList, opLambda);
    }

    public void setScriptData_Match(String scriptData) {
        setScriptData_Match(scriptData, null);
    }

    public void setScriptData_Match(String scriptData, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_MatchPhrase(String scriptData) {
        setScriptData_MatchPhrase(scriptData, null);
    }

    public void setScriptData_MatchPhrase(String scriptData, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_MatchPhrasePrefix(String scriptData) {
        setScriptData_MatchPhrasePrefix(scriptData, null);
    }

    public void setScriptData_MatchPhrasePrefix(String scriptData, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Fuzzy(String scriptData) {
        setScriptData_Fuzzy(scriptData, null);
    }

    public void setScriptData_Fuzzy(String scriptData, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Prefix(String scriptData) {
        setScriptData_Prefix(scriptData, null);
    }

    public void setScriptData_Prefix(String scriptData, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Wildcard(String scriptData) {
        setScriptData_Wildcard(scriptData, null);
    }

    public void setScriptData_Wildcard(String scriptData, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Regexp(String scriptData) {
        setScriptData_Regexp(scriptData, null);
    }

    public void setScriptData_Regexp(String scriptData, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_SpanTerm(String scriptData) {
        setScriptData_SpanTerm("scriptData", null);
    }

    public void setScriptData_SpanTerm(String scriptData, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_GreaterThan(String scriptData) {
        setScriptData_GreaterThan(scriptData, null);
    }

    public void setScriptData_GreaterThan(String scriptData, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = scriptData;
        RangeQueryBuilder builder = regRangeQ("scriptData", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_LessThan(String scriptData) {
        setScriptData_LessThan(scriptData, null);
    }

    public void setScriptData_LessThan(String scriptData, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = scriptData;
        RangeQueryBuilder builder = regRangeQ("scriptData", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_GreaterEqual(String scriptData) {
        setScriptData_GreaterEqual(scriptData, null);
    }

    public void setScriptData_GreaterEqual(String scriptData, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = scriptData;
        RangeQueryBuilder builder = regRangeQ("scriptData", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_LessEqual(String scriptData) {
        setScriptData_LessEqual(scriptData, null);
    }

    public void setScriptData_LessEqual(String scriptData, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = scriptData;
        RangeQueryBuilder builder = regRangeQ("scriptData", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Exists() {
        setScriptData_Exists(null);
    }

    public void setScriptData_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("scriptData");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setScriptData_CommonTerms(String scriptData) {
        setScriptData_CommonTerms(scriptData, null);
    }

    @Deprecated
    public void setScriptData_CommonTerms(String scriptData, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsJobLogCQ addOrderBy_ScriptData_Asc() {
        regOBA("scriptData");
        return this;
    }

    public BsJobLogCQ addOrderBy_ScriptData_Desc() {
        regOBD("scriptData");
        return this;
    }

    public void setScriptResult_Equal(String scriptResult) {
        setScriptResult_Term(scriptResult, null);
    }

    public void setScriptResult_Equal(String scriptResult, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setScriptResult_Term(scriptResult, opLambda);
    }

    public void setScriptResult_Term(String scriptResult) {
        setScriptResult_Term(scriptResult, null);
    }

    public void setScriptResult_Term(String scriptResult, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("scriptResult", scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_NotEqual(String scriptResult) {
        setScriptResult_NotTerm(scriptResult, null);
    }

    public void setScriptResult_NotTerm(String scriptResult) {
        setScriptResult_NotTerm(scriptResult, null);
    }

    public void setScriptResult_NotEqual(String scriptResult, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setScriptResult_NotTerm(scriptResult, opLambda);
    }

    public void setScriptResult_NotTerm(String scriptResult, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setScriptResult_Term(scriptResult), opLambda);
    }

    public void setScriptResult_Terms(Collection<String> scriptResultList) {
        setScriptResult_Terms(scriptResultList, null);
    }

    public void setScriptResult_Terms(Collection<String> scriptResultList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("scriptResult", scriptResultList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_InScope(Collection<String> scriptResultList) {
        setScriptResult_Terms(scriptResultList, null);
    }

    public void setScriptResult_InScope(Collection<String> scriptResultList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setScriptResult_Terms(scriptResultList, opLambda);
    }

    public void setScriptResult_Match(String scriptResult) {
        setScriptResult_Match(scriptResult, null);
    }

    public void setScriptResult_Match(String scriptResult, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("scriptResult", scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_MatchPhrase(String scriptResult) {
        setScriptResult_MatchPhrase(scriptResult, null);
    }

    public void setScriptResult_MatchPhrase(String scriptResult, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("scriptResult", scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_MatchPhrasePrefix(String scriptResult) {
        setScriptResult_MatchPhrasePrefix(scriptResult, null);
    }

    public void setScriptResult_MatchPhrasePrefix(String scriptResult, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("scriptResult", scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_Fuzzy(String scriptResult) {
        setScriptResult_Fuzzy(scriptResult, null);
    }

    public void setScriptResult_Fuzzy(String scriptResult, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("scriptResult", scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_Prefix(String scriptResult) {
        setScriptResult_Prefix(scriptResult, null);
    }

    public void setScriptResult_Prefix(String scriptResult, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("scriptResult", scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_Wildcard(String scriptResult) {
        setScriptResult_Wildcard(scriptResult, null);
    }

    public void setScriptResult_Wildcard(String scriptResult, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("scriptResult", scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_Regexp(String scriptResult) {
        setScriptResult_Regexp(scriptResult, null);
    }

    public void setScriptResult_Regexp(String scriptResult, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("scriptResult", scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_SpanTerm(String scriptResult) {
        setScriptResult_SpanTerm("scriptResult", null);
    }

    public void setScriptResult_SpanTerm(String scriptResult, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("scriptResult", scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_GreaterThan(String scriptResult) {
        setScriptResult_GreaterThan(scriptResult, null);
    }

    public void setScriptResult_GreaterThan(String scriptResult, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = scriptResult;
        RangeQueryBuilder builder = regRangeQ("scriptResult", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_LessThan(String scriptResult) {
        setScriptResult_LessThan(scriptResult, null);
    }

    public void setScriptResult_LessThan(String scriptResult, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = scriptResult;
        RangeQueryBuilder builder = regRangeQ("scriptResult", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_GreaterEqual(String scriptResult) {
        setScriptResult_GreaterEqual(scriptResult, null);
    }

    public void setScriptResult_GreaterEqual(String scriptResult, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = scriptResult;
        RangeQueryBuilder builder = regRangeQ("scriptResult", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_LessEqual(String scriptResult) {
        setScriptResult_LessEqual(scriptResult, null);
    }

    public void setScriptResult_LessEqual(String scriptResult, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = scriptResult;
        RangeQueryBuilder builder = regRangeQ("scriptResult", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_Exists() {
        setScriptResult_Exists(null);
    }

    public void setScriptResult_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("scriptResult");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setScriptResult_CommonTerms(String scriptResult) {
        setScriptResult_CommonTerms(scriptResult, null);
    }

    @Deprecated
    public void setScriptResult_CommonTerms(String scriptResult, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("scriptResult", scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsJobLogCQ addOrderBy_ScriptResult_Asc() {
        regOBA("scriptResult");
        return this;
    }

    public BsJobLogCQ addOrderBy_ScriptResult_Desc() {
        regOBD("scriptResult");
        return this;
    }

    public void setScriptType_Equal(String scriptType) {
        setScriptType_Term(scriptType, null);
    }

    public void setScriptType_Equal(String scriptType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setScriptType_Term(scriptType, opLambda);
    }

    public void setScriptType_Term(String scriptType) {
        setScriptType_Term(scriptType, null);
    }

    public void setScriptType_Term(String scriptType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_NotEqual(String scriptType) {
        setScriptType_NotTerm(scriptType, null);
    }

    public void setScriptType_NotTerm(String scriptType) {
        setScriptType_NotTerm(scriptType, null);
    }

    public void setScriptType_NotEqual(String scriptType, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setScriptType_NotTerm(scriptType, opLambda);
    }

    public void setScriptType_NotTerm(String scriptType, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setScriptType_Term(scriptType), opLambda);
    }

    public void setScriptType_Terms(Collection<String> scriptTypeList) {
        setScriptType_Terms(scriptTypeList, null);
    }

    public void setScriptType_Terms(Collection<String> scriptTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("scriptType", scriptTypeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_InScope(Collection<String> scriptTypeList) {
        setScriptType_Terms(scriptTypeList, null);
    }

    public void setScriptType_InScope(Collection<String> scriptTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setScriptType_Terms(scriptTypeList, opLambda);
    }

    public void setScriptType_Match(String scriptType) {
        setScriptType_Match(scriptType, null);
    }

    public void setScriptType_Match(String scriptType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_MatchPhrase(String scriptType) {
        setScriptType_MatchPhrase(scriptType, null);
    }

    public void setScriptType_MatchPhrase(String scriptType, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_MatchPhrasePrefix(String scriptType) {
        setScriptType_MatchPhrasePrefix(scriptType, null);
    }

    public void setScriptType_MatchPhrasePrefix(String scriptType, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Fuzzy(String scriptType) {
        setScriptType_Fuzzy(scriptType, null);
    }

    public void setScriptType_Fuzzy(String scriptType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Prefix(String scriptType) {
        setScriptType_Prefix(scriptType, null);
    }

    public void setScriptType_Prefix(String scriptType, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Wildcard(String scriptType) {
        setScriptType_Wildcard(scriptType, null);
    }

    public void setScriptType_Wildcard(String scriptType, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Regexp(String scriptType) {
        setScriptType_Regexp(scriptType, null);
    }

    public void setScriptType_Regexp(String scriptType, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_SpanTerm(String scriptType) {
        setScriptType_SpanTerm("scriptType", null);
    }

    public void setScriptType_SpanTerm(String scriptType, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_GreaterThan(String scriptType) {
        setScriptType_GreaterThan(scriptType, null);
    }

    public void setScriptType_GreaterThan(String scriptType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = scriptType;
        RangeQueryBuilder builder = regRangeQ("scriptType", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_LessThan(String scriptType) {
        setScriptType_LessThan(scriptType, null);
    }

    public void setScriptType_LessThan(String scriptType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = scriptType;
        RangeQueryBuilder builder = regRangeQ("scriptType", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_GreaterEqual(String scriptType) {
        setScriptType_GreaterEqual(scriptType, null);
    }

    public void setScriptType_GreaterEqual(String scriptType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = scriptType;
        RangeQueryBuilder builder = regRangeQ("scriptType", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_LessEqual(String scriptType) {
        setScriptType_LessEqual(scriptType, null);
    }

    public void setScriptType_LessEqual(String scriptType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = scriptType;
        RangeQueryBuilder builder = regRangeQ("scriptType", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Exists() {
        setScriptType_Exists(null);
    }

    public void setScriptType_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("scriptType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setScriptType_CommonTerms(String scriptType) {
        setScriptType_CommonTerms(scriptType, null);
    }

    @Deprecated
    public void setScriptType_CommonTerms(String scriptType, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsJobLogCQ addOrderBy_ScriptType_Asc() {
        regOBA("scriptType");
        return this;
    }

    public BsJobLogCQ addOrderBy_ScriptType_Desc() {
        regOBD("scriptType");
        return this;
    }

    public void setStartTime_Equal(Long startTime) {
        setStartTime_Term(startTime, null);
    }

    public void setStartTime_Equal(Long startTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setStartTime_Term(startTime, opLambda);
    }

    public void setStartTime_Term(Long startTime) {
        setStartTime_Term(startTime, null);
    }

    public void setStartTime_Term(Long startTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("startTime", startTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_NotEqual(Long startTime) {
        setStartTime_NotTerm(startTime, null);
    }

    public void setStartTime_NotTerm(Long startTime) {
        setStartTime_NotTerm(startTime, null);
    }

    public void setStartTime_NotEqual(Long startTime, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setStartTime_NotTerm(startTime, opLambda);
    }

    public void setStartTime_NotTerm(Long startTime, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setStartTime_Term(startTime), opLambda);
    }

    public void setStartTime_Terms(Collection<Long> startTimeList) {
        setStartTime_Terms(startTimeList, null);
    }

    public void setStartTime_Terms(Collection<Long> startTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("startTime", startTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_InScope(Collection<Long> startTimeList) {
        setStartTime_Terms(startTimeList, null);
    }

    public void setStartTime_InScope(Collection<Long> startTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setStartTime_Terms(startTimeList, opLambda);
    }

    public void setStartTime_Match(Long startTime) {
        setStartTime_Match(startTime, null);
    }

    public void setStartTime_Match(Long startTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("startTime", startTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_MatchPhrase(Long startTime) {
        setStartTime_MatchPhrase(startTime, null);
    }

    public void setStartTime_MatchPhrase(Long startTime, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("startTime", startTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_MatchPhrasePrefix(Long startTime) {
        setStartTime_MatchPhrasePrefix(startTime, null);
    }

    public void setStartTime_MatchPhrasePrefix(Long startTime, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("startTime", startTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Fuzzy(Long startTime) {
        setStartTime_Fuzzy(startTime, null);
    }

    public void setStartTime_Fuzzy(Long startTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("startTime", startTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_GreaterThan(Long startTime) {
        setStartTime_GreaterThan(startTime, null);
    }

    public void setStartTime_GreaterThan(Long startTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = startTime;
        RangeQueryBuilder builder = regRangeQ("startTime", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_LessThan(Long startTime) {
        setStartTime_LessThan(startTime, null);
    }

    public void setStartTime_LessThan(Long startTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = startTime;
        RangeQueryBuilder builder = regRangeQ("startTime", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_GreaterEqual(Long startTime) {
        setStartTime_GreaterEqual(startTime, null);
    }

    public void setStartTime_GreaterEqual(Long startTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = startTime;
        RangeQueryBuilder builder = regRangeQ("startTime", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_LessEqual(Long startTime) {
        setStartTime_LessEqual(startTime, null);
    }

    public void setStartTime_LessEqual(Long startTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = startTime;
        RangeQueryBuilder builder = regRangeQ("startTime", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Exists() {
        setStartTime_Exists(null);
    }

    public void setStartTime_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setStartTime_CommonTerms(Long startTime) {
        setStartTime_CommonTerms(startTime, null);
    }

    @Deprecated
    public void setStartTime_CommonTerms(Long startTime, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("startTime", startTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsJobLogCQ addOrderBy_StartTime_Asc() {
        regOBA("startTime");
        return this;
    }

    public BsJobLogCQ addOrderBy_StartTime_Desc() {
        regOBD("startTime");
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

    public BsJobLogCQ addOrderBy_Target_Asc() {
        regOBA("target");
        return this;
    }

    public BsJobLogCQ addOrderBy_Target_Desc() {
        regOBD("target");
        return this;
    }

}
