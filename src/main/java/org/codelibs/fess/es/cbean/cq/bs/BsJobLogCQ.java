package org.codelibs.fess.es.cbean.cq.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cq.JobLogCQ;
import org.dbflute.cbean.ckey.ConditionKey;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;

/**
 * @author FreeGen
 */
public abstract class BsJobLogCQ extends AbstractConditionQuery {

    @Override
    public String asTableDbName() {
        return "job_log";
    }

    @Override
    public String xgetAliasName() {
        return "job_log";
    }

    public void filtered(FilteredCall<JobLogCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<JobLogCQ> filteredLambda, ConditionOptionCall<FilteredQueryBuilder> opLambda) {
        JobLogCQ query = new JobLogCQ();
        filteredLambda.callback(query);
        if (!query.queryBuilderList.isEmpty()) {
            // TODO filter
            FilteredQueryBuilder builder = reqFilteredQ(query.getQuery(), null);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void bool(BoolCall<JobLogCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<JobLogCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        JobLogCQ mustQuery = new JobLogCQ();
        JobLogCQ shouldQuery = new JobLogCQ();
        JobLogCQ mustNotQuery = new JobLogCQ();
        boolLambda.callback(mustQuery, shouldQuery, mustNotQuery);
        if (!mustQuery.queryBuilderList.isEmpty() || !shouldQuery.queryBuilderList.isEmpty() || !mustNotQuery.queryBuilderList.isEmpty()) {
            BoolQueryBuilder builder = reqBoolCQ(mustQuery.queryBuilderList, shouldQuery.queryBuilderList, mustNotQuery.queryBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void setEndTime_Term(Long endTime) {
        setEndTime_Term(endTime, null);
    }

    public void setEndTime_Term(Long endTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("endTime", endTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Terms(Collection<Long> endTimeList) {
        setEndTime_MatchPhrasePrefix(endTimeList, null);
    }

    public void setEndTime_MatchPhrasePrefix(Collection<Long> endTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("endTime", endTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_InScope(Collection<Long> endTimeList) {
        setEndTime_MatchPhrasePrefix(endTimeList, null);
    }

    public void setEndTime_InScope(Collection<Long> endTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setEndTime_MatchPhrasePrefix(endTimeList, opLambda);
    }

    public void setEndTime_Match(Long endTime) {
        setEndTime_Match(endTime, null);
    }

    public void setEndTime_Match(Long endTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("endTime", endTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_MatchPhrase(Long endTime) {
        setEndTime_MatchPhrase(endTime, null);
    }

    public void setEndTime_MatchPhrase(Long endTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("endTime", endTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_MatchPhrasePrefix(Long endTime) {
        setEndTime_MatchPhrasePrefix(endTime, null);
    }

    public void setEndTime_MatchPhrasePrefix(Long endTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("endTime", endTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Fuzzy(Long endTime) {
        setEndTime_Fuzzy(endTime, null);
    }

    public void setEndTime_Fuzzy(Long endTime, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("endTime", endTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_GreaterThan(Long endTime) {
        setEndTime_GreaterThan(endTime, null);
    }

    public void setEndTime_GreaterThan(Long endTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("endTime", ConditionKey.CK_GREATER_THAN, endTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_LessThan(Long endTime) {
        setEndTime_LessThan(endTime, null);
    }

    public void setEndTime_LessThan(Long endTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("endTime", ConditionKey.CK_LESS_THAN, endTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_GreaterEqual(Long endTime) {
        setEndTime_GreaterEqual(endTime, null);
    }

    public void setEndTime_GreaterEqual(Long endTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("endTime", ConditionKey.CK_GREATER_EQUAL, endTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_LessEqual(Long endTime) {
        setEndTime_LessEqual(endTime, null);
    }

    public void setEndTime_LessEqual(Long endTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("endTime", ConditionKey.CK_LESS_EQUAL, endTime);
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

    public void setId_Term(String id) {
        setId_Term(id, null);
    }

    public void setId_Term(String id, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Terms(Collection<String> idList) {
        setId_MatchPhrasePrefix(idList, null);
    }

    public void setId_MatchPhrasePrefix(Collection<String> idList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("id", idList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_InScope(Collection<String> idList) {
        setId_MatchPhrasePrefix(idList, null);
    }

    public void setId_InScope(Collection<String> idList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setId_MatchPhrasePrefix(idList, opLambda);
    }

    public void setId_Match(String id) {
        setId_Match(id, null);
    }

    public void setId_Match(String id, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_MatchPhrase(String id) {
        setId_MatchPhrase(id, null);
    }

    public void setId_MatchPhrase(String id, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_MatchPhrasePrefix(String id) {
        setId_MatchPhrasePrefix(id, null);
    }

    public void setId_MatchPhrasePrefix(String id, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Fuzzy(String id) {
        setId_Fuzzy(id, null);
    }

    public void setId_Fuzzy(String id, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Prefix(String id) {
        setId_Prefix(id, null);
    }

    public void setId_Prefix(String id, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_GreaterThan(String id) {
        setId_GreaterThan(id, null);
    }

    public void setId_GreaterThan(String id, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("id", ConditionKey.CK_GREATER_THAN, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_LessThan(String id) {
        setId_LessThan(id, null);
    }

    public void setId_LessThan(String id, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("id", ConditionKey.CK_LESS_THAN, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_GreaterEqual(String id) {
        setId_GreaterEqual(id, null);
    }

    public void setId_GreaterEqual(String id, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("id", ConditionKey.CK_GREATER_EQUAL, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_LessEqual(String id) {
        setId_LessEqual(id, null);
    }

    public void setId_LessEqual(String id, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("id", ConditionKey.CK_LESS_EQUAL, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsJobLogCQ addOrderBy_Id_Asc() {
        regOBA("id");
        return this;
    }

    public BsJobLogCQ addOrderBy_Id_Desc() {
        regOBD("id");
        return this;
    }

    public void setJobName_Term(String jobName) {
        setJobName_Term(jobName, null);
    }

    public void setJobName_Term(String jobName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("jobName", jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_Terms(Collection<String> jobNameList) {
        setJobName_MatchPhrasePrefix(jobNameList, null);
    }

    public void setJobName_MatchPhrasePrefix(Collection<String> jobNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("jobName", jobNameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_InScope(Collection<String> jobNameList) {
        setJobName_MatchPhrasePrefix(jobNameList, null);
    }

    public void setJobName_InScope(Collection<String> jobNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setJobName_MatchPhrasePrefix(jobNameList, opLambda);
    }

    public void setJobName_Match(String jobName) {
        setJobName_Match(jobName, null);
    }

    public void setJobName_Match(String jobName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("jobName", jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_MatchPhrase(String jobName) {
        setJobName_MatchPhrase(jobName, null);
    }

    public void setJobName_MatchPhrase(String jobName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("jobName", jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_MatchPhrasePrefix(String jobName) {
        setJobName_MatchPhrasePrefix(jobName, null);
    }

    public void setJobName_MatchPhrasePrefix(String jobName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("jobName", jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_Fuzzy(String jobName) {
        setJobName_Fuzzy(jobName, null);
    }

    public void setJobName_Fuzzy(String jobName, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("jobName", jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_Prefix(String jobName) {
        setJobName_Prefix(jobName, null);
    }

    public void setJobName_Prefix(String jobName, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("jobName", jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_GreaterThan(String jobName) {
        setJobName_GreaterThan(jobName, null);
    }

    public void setJobName_GreaterThan(String jobName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("jobName", ConditionKey.CK_GREATER_THAN, jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_LessThan(String jobName) {
        setJobName_LessThan(jobName, null);
    }

    public void setJobName_LessThan(String jobName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("jobName", ConditionKey.CK_LESS_THAN, jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_GreaterEqual(String jobName) {
        setJobName_GreaterEqual(jobName, null);
    }

    public void setJobName_GreaterEqual(String jobName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("jobName", ConditionKey.CK_GREATER_EQUAL, jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_LessEqual(String jobName) {
        setJobName_LessEqual(jobName, null);
    }

    public void setJobName_LessEqual(String jobName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("jobName", ConditionKey.CK_LESS_EQUAL, jobName);
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

    public void setJobStatus_Term(String jobStatus) {
        setJobStatus_Term(jobStatus, null);
    }

    public void setJobStatus_Term(String jobStatus, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("jobStatus", jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_Terms(Collection<String> jobStatusList) {
        setJobStatus_MatchPhrasePrefix(jobStatusList, null);
    }

    public void setJobStatus_MatchPhrasePrefix(Collection<String> jobStatusList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("jobStatus", jobStatusList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_InScope(Collection<String> jobStatusList) {
        setJobStatus_MatchPhrasePrefix(jobStatusList, null);
    }

    public void setJobStatus_InScope(Collection<String> jobStatusList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setJobStatus_MatchPhrasePrefix(jobStatusList, opLambda);
    }

    public void setJobStatus_Match(String jobStatus) {
        setJobStatus_Match(jobStatus, null);
    }

    public void setJobStatus_Match(String jobStatus, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("jobStatus", jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_MatchPhrase(String jobStatus) {
        setJobStatus_MatchPhrase(jobStatus, null);
    }

    public void setJobStatus_MatchPhrase(String jobStatus, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("jobStatus", jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_MatchPhrasePrefix(String jobStatus) {
        setJobStatus_MatchPhrasePrefix(jobStatus, null);
    }

    public void setJobStatus_MatchPhrasePrefix(String jobStatus, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("jobStatus", jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_Fuzzy(String jobStatus) {
        setJobStatus_Fuzzy(jobStatus, null);
    }

    public void setJobStatus_Fuzzy(String jobStatus, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("jobStatus", jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_Prefix(String jobStatus) {
        setJobStatus_Prefix(jobStatus, null);
    }

    public void setJobStatus_Prefix(String jobStatus, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("jobStatus", jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_GreaterThan(String jobStatus) {
        setJobStatus_GreaterThan(jobStatus, null);
    }

    public void setJobStatus_GreaterThan(String jobStatus, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("jobStatus", ConditionKey.CK_GREATER_THAN, jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_LessThan(String jobStatus) {
        setJobStatus_LessThan(jobStatus, null);
    }

    public void setJobStatus_LessThan(String jobStatus, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("jobStatus", ConditionKey.CK_LESS_THAN, jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_GreaterEqual(String jobStatus) {
        setJobStatus_GreaterEqual(jobStatus, null);
    }

    public void setJobStatus_GreaterEqual(String jobStatus, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("jobStatus", ConditionKey.CK_GREATER_EQUAL, jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_LessEqual(String jobStatus) {
        setJobStatus_LessEqual(jobStatus, null);
    }

    public void setJobStatus_LessEqual(String jobStatus, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("jobStatus", ConditionKey.CK_LESS_EQUAL, jobStatus);
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

    public void setScriptData_Term(String scriptData) {
        setScriptData_Term(scriptData, null);
    }

    public void setScriptData_Term(String scriptData, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Terms(Collection<String> scriptDataList) {
        setScriptData_MatchPhrasePrefix(scriptDataList, null);
    }

    public void setScriptData_MatchPhrasePrefix(Collection<String> scriptDataList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("scriptData", scriptDataList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_InScope(Collection<String> scriptDataList) {
        setScriptData_MatchPhrasePrefix(scriptDataList, null);
    }

    public void setScriptData_InScope(Collection<String> scriptDataList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setScriptData_MatchPhrasePrefix(scriptDataList, opLambda);
    }

    public void setScriptData_Match(String scriptData) {
        setScriptData_Match(scriptData, null);
    }

    public void setScriptData_Match(String scriptData, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_MatchPhrase(String scriptData) {
        setScriptData_MatchPhrase(scriptData, null);
    }

    public void setScriptData_MatchPhrase(String scriptData, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_MatchPhrasePrefix(String scriptData) {
        setScriptData_MatchPhrasePrefix(scriptData, null);
    }

    public void setScriptData_MatchPhrasePrefix(String scriptData, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Fuzzy(String scriptData) {
        setScriptData_Fuzzy(scriptData, null);
    }

    public void setScriptData_Fuzzy(String scriptData, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Prefix(String scriptData) {
        setScriptData_Prefix(scriptData, null);
    }

    public void setScriptData_Prefix(String scriptData, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_GreaterThan(String scriptData) {
        setScriptData_GreaterThan(scriptData, null);
    }

    public void setScriptData_GreaterThan(String scriptData, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("scriptData", ConditionKey.CK_GREATER_THAN, scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_LessThan(String scriptData) {
        setScriptData_LessThan(scriptData, null);
    }

    public void setScriptData_LessThan(String scriptData, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("scriptData", ConditionKey.CK_LESS_THAN, scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_GreaterEqual(String scriptData) {
        setScriptData_GreaterEqual(scriptData, null);
    }

    public void setScriptData_GreaterEqual(String scriptData, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("scriptData", ConditionKey.CK_GREATER_EQUAL, scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_LessEqual(String scriptData) {
        setScriptData_LessEqual(scriptData, null);
    }

    public void setScriptData_LessEqual(String scriptData, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("scriptData", ConditionKey.CK_LESS_EQUAL, scriptData);
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

    public void setScriptResult_Term(String scriptResult) {
        setScriptResult_Term(scriptResult, null);
    }

    public void setScriptResult_Term(String scriptResult, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("scriptResult", scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_Terms(Collection<String> scriptResultList) {
        setScriptResult_MatchPhrasePrefix(scriptResultList, null);
    }

    public void setScriptResult_MatchPhrasePrefix(Collection<String> scriptResultList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("scriptResult", scriptResultList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_InScope(Collection<String> scriptResultList) {
        setScriptResult_MatchPhrasePrefix(scriptResultList, null);
    }

    public void setScriptResult_InScope(Collection<String> scriptResultList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setScriptResult_MatchPhrasePrefix(scriptResultList, opLambda);
    }

    public void setScriptResult_Match(String scriptResult) {
        setScriptResult_Match(scriptResult, null);
    }

    public void setScriptResult_Match(String scriptResult, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("scriptResult", scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_MatchPhrase(String scriptResult) {
        setScriptResult_MatchPhrase(scriptResult, null);
    }

    public void setScriptResult_MatchPhrase(String scriptResult, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("scriptResult", scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_MatchPhrasePrefix(String scriptResult) {
        setScriptResult_MatchPhrasePrefix(scriptResult, null);
    }

    public void setScriptResult_MatchPhrasePrefix(String scriptResult, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("scriptResult", scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_Fuzzy(String scriptResult) {
        setScriptResult_Fuzzy(scriptResult, null);
    }

    public void setScriptResult_Fuzzy(String scriptResult, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("scriptResult", scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_Prefix(String scriptResult) {
        setScriptResult_Prefix(scriptResult, null);
    }

    public void setScriptResult_Prefix(String scriptResult, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("scriptResult", scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_GreaterThan(String scriptResult) {
        setScriptResult_GreaterThan(scriptResult, null);
    }

    public void setScriptResult_GreaterThan(String scriptResult, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("scriptResult", ConditionKey.CK_GREATER_THAN, scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_LessThan(String scriptResult) {
        setScriptResult_LessThan(scriptResult, null);
    }

    public void setScriptResult_LessThan(String scriptResult, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("scriptResult", ConditionKey.CK_LESS_THAN, scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_GreaterEqual(String scriptResult) {
        setScriptResult_GreaterEqual(scriptResult, null);
    }

    public void setScriptResult_GreaterEqual(String scriptResult, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("scriptResult", ConditionKey.CK_GREATER_EQUAL, scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_LessEqual(String scriptResult) {
        setScriptResult_LessEqual(scriptResult, null);
    }

    public void setScriptResult_LessEqual(String scriptResult, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("scriptResult", ConditionKey.CK_LESS_EQUAL, scriptResult);
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

    public void setScriptType_Term(String scriptType) {
        setScriptType_Term(scriptType, null);
    }

    public void setScriptType_Term(String scriptType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Terms(Collection<String> scriptTypeList) {
        setScriptType_MatchPhrasePrefix(scriptTypeList, null);
    }

    public void setScriptType_MatchPhrasePrefix(Collection<String> scriptTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("scriptType", scriptTypeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_InScope(Collection<String> scriptTypeList) {
        setScriptType_MatchPhrasePrefix(scriptTypeList, null);
    }

    public void setScriptType_InScope(Collection<String> scriptTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setScriptType_MatchPhrasePrefix(scriptTypeList, opLambda);
    }

    public void setScriptType_Match(String scriptType) {
        setScriptType_Match(scriptType, null);
    }

    public void setScriptType_Match(String scriptType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_MatchPhrase(String scriptType) {
        setScriptType_MatchPhrase(scriptType, null);
    }

    public void setScriptType_MatchPhrase(String scriptType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_MatchPhrasePrefix(String scriptType) {
        setScriptType_MatchPhrasePrefix(scriptType, null);
    }

    public void setScriptType_MatchPhrasePrefix(String scriptType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Fuzzy(String scriptType) {
        setScriptType_Fuzzy(scriptType, null);
    }

    public void setScriptType_Fuzzy(String scriptType, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Prefix(String scriptType) {
        setScriptType_Prefix(scriptType, null);
    }

    public void setScriptType_Prefix(String scriptType, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_GreaterThan(String scriptType) {
        setScriptType_GreaterThan(scriptType, null);
    }

    public void setScriptType_GreaterThan(String scriptType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("scriptType", ConditionKey.CK_GREATER_THAN, scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_LessThan(String scriptType) {
        setScriptType_LessThan(scriptType, null);
    }

    public void setScriptType_LessThan(String scriptType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("scriptType", ConditionKey.CK_LESS_THAN, scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_GreaterEqual(String scriptType) {
        setScriptType_GreaterEqual(scriptType, null);
    }

    public void setScriptType_GreaterEqual(String scriptType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("scriptType", ConditionKey.CK_GREATER_EQUAL, scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_LessEqual(String scriptType) {
        setScriptType_LessEqual(scriptType, null);
    }

    public void setScriptType_LessEqual(String scriptType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("scriptType", ConditionKey.CK_LESS_EQUAL, scriptType);
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

    public void setStartTime_Term(Long startTime) {
        setStartTime_Term(startTime, null);
    }

    public void setStartTime_Term(Long startTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("startTime", startTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Terms(Collection<Long> startTimeList) {
        setStartTime_MatchPhrasePrefix(startTimeList, null);
    }

    public void setStartTime_MatchPhrasePrefix(Collection<Long> startTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("startTime", startTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_InScope(Collection<Long> startTimeList) {
        setStartTime_MatchPhrasePrefix(startTimeList, null);
    }

    public void setStartTime_InScope(Collection<Long> startTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setStartTime_MatchPhrasePrefix(startTimeList, opLambda);
    }

    public void setStartTime_Match(Long startTime) {
        setStartTime_Match(startTime, null);
    }

    public void setStartTime_Match(Long startTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("startTime", startTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_MatchPhrase(Long startTime) {
        setStartTime_MatchPhrase(startTime, null);
    }

    public void setStartTime_MatchPhrase(Long startTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("startTime", startTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_MatchPhrasePrefix(Long startTime) {
        setStartTime_MatchPhrasePrefix(startTime, null);
    }

    public void setStartTime_MatchPhrasePrefix(Long startTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("startTime", startTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Fuzzy(Long startTime) {
        setStartTime_Fuzzy(startTime, null);
    }

    public void setStartTime_Fuzzy(Long startTime, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("startTime", startTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_GreaterThan(Long startTime) {
        setStartTime_GreaterThan(startTime, null);
    }

    public void setStartTime_GreaterThan(Long startTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("startTime", ConditionKey.CK_GREATER_THAN, startTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_LessThan(Long startTime) {
        setStartTime_LessThan(startTime, null);
    }

    public void setStartTime_LessThan(Long startTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("startTime", ConditionKey.CK_LESS_THAN, startTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_GreaterEqual(Long startTime) {
        setStartTime_GreaterEqual(startTime, null);
    }

    public void setStartTime_GreaterEqual(Long startTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("startTime", ConditionKey.CK_GREATER_EQUAL, startTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_LessEqual(Long startTime) {
        setStartTime_LessEqual(startTime, null);
    }

    public void setStartTime_LessEqual(Long startTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("startTime", ConditionKey.CK_LESS_EQUAL, startTime);
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

    public void setTarget_Term(String target) {
        setTarget_Term(target, null);
    }

    public void setTarget_Term(String target, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Terms(Collection<String> targetList) {
        setTarget_MatchPhrasePrefix(targetList, null);
    }

    public void setTarget_MatchPhrasePrefix(Collection<String> targetList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("target", targetList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_InScope(Collection<String> targetList) {
        setTarget_MatchPhrasePrefix(targetList, null);
    }

    public void setTarget_InScope(Collection<String> targetList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setTarget_MatchPhrasePrefix(targetList, opLambda);
    }

    public void setTarget_Match(String target) {
        setTarget_Match(target, null);
    }

    public void setTarget_Match(String target, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_MatchPhrase(String target) {
        setTarget_MatchPhrase(target, null);
    }

    public void setTarget_MatchPhrase(String target, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_MatchPhrasePrefix(String target) {
        setTarget_MatchPhrasePrefix(target, null);
    }

    public void setTarget_MatchPhrasePrefix(String target, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Fuzzy(String target) {
        setTarget_Fuzzy(target, null);
    }

    public void setTarget_Fuzzy(String target, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Prefix(String target) {
        setTarget_Prefix(target, null);
    }

    public void setTarget_Prefix(String target, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_GreaterThan(String target) {
        setTarget_GreaterThan(target, null);
    }

    public void setTarget_GreaterThan(String target, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("target", ConditionKey.CK_GREATER_THAN, target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_LessThan(String target) {
        setTarget_LessThan(target, null);
    }

    public void setTarget_LessThan(String target, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("target", ConditionKey.CK_LESS_THAN, target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_GreaterEqual(String target) {
        setTarget_GreaterEqual(target, null);
    }

    public void setTarget_GreaterEqual(String target, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("target", ConditionKey.CK_GREATER_EQUAL, target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_LessEqual(String target) {
        setTarget_LessEqual(target, null);
    }

    public void setTarget_LessEqual(String target, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("target", ConditionKey.CK_LESS_EQUAL, target);
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
