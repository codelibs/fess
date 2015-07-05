package org.codelibs.fess.es.cbean.cf.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cf.JobLogCF;
import org.codelibs.fess.es.cbean.cq.JobLogCQ;
import org.dbflute.exception.IllegalConditionBeanOperationException;
import org.dbflute.cbean.ckey.ConditionKey;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.ExistsFilterBuilder;
import org.elasticsearch.index.query.MissingFilterBuilder;
import org.elasticsearch.index.query.NotFilterBuilder;
import org.elasticsearch.index.query.OrFilterBuilder;
import org.elasticsearch.index.query.PrefixFilterBuilder;
import org.elasticsearch.index.query.QueryFilterBuilder;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.index.query.TermsFilterBuilder;

/**
 * @author FreeGen
 */
public abstract class BsJobLogCF extends AbstractConditionFilter {

    public void bool(BoolCall<JobLogCF> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<JobLogCF> boolLambda, ConditionOptionCall<BoolFilterBuilder> opLambda) {
        JobLogCF mustFilter = new JobLogCF();
        JobLogCF shouldFilter = new JobLogCF();
        JobLogCF mustNotFilter = new JobLogCF();
        boolLambda.callback(mustFilter, shouldFilter, mustNotFilter);
        if (mustFilter.hasFilters() || shouldFilter.hasFilters() || mustNotFilter.hasFilters()) {
            BoolFilterBuilder builder =
                    regBoolF(mustFilter.filterBuilderList, shouldFilter.filterBuilderList, mustNotFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void and(OperatorCall<JobLogCF> andLambda) {
        and(andLambda, null);
    }

    public void and(OperatorCall<JobLogCF> andLambda, ConditionOptionCall<AndFilterBuilder> opLambda) {
        JobLogCF andFilter = new JobLogCF();
        andLambda.callback(andFilter);
        if (andFilter.hasFilters()) {
            AndFilterBuilder builder = regAndF(andFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void or(OperatorCall<JobLogCF> orLambda) {
        or(orLambda, null);
    }

    public void or(OperatorCall<JobLogCF> orLambda, ConditionOptionCall<OrFilterBuilder> opLambda) {
        JobLogCF orFilter = new JobLogCF();
        orLambda.callback(orFilter);
        if (orFilter.hasFilters()) {
            OrFilterBuilder builder = regOrF(orFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void not(OperatorCall<JobLogCF> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<JobLogCF> notLambda, ConditionOptionCall<NotFilterBuilder> opLambda) {
        JobLogCF notFilter = new JobLogCF();
        notLambda.callback(notFilter);
        if (notFilter.hasFilters()) {
            if (notFilter.filterBuilderList.size() > 1) {
                final String msg = "not filter must be one filter.";
                throw new IllegalConditionBeanOperationException(msg);
            }
            NotFilterBuilder builder = regNotF(notFilter.filterBuilderList.get(0));
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<JobLogCQ> queryLambda) {
        query(queryLambda, null);
    }

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<JobLogCQ> queryLambda,
            ConditionOptionCall<QueryFilterBuilder> opLambda) {
        JobLogCQ query = new JobLogCQ();
        queryLambda.callback(query);
        if (query.hasQueries()) {
            QueryFilterBuilder builder = regQueryF(query.getQuery());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void setEndTime_Term(Long endTime) {
        setEndTime_Term(endTime, null);
    }

    public void setEndTime_Term(Long endTime, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("endTime", endTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Terms(Collection<Long> endTimeList) {
        setEndTime_Terms(endTimeList, null);
    }

    public void setEndTime_Terms(Collection<Long> endTimeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("endTime", endTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_InScope(Collection<Long> endTimeList) {
        setEndTime_Terms(endTimeList, null);
    }

    public void setEndTime_InScope(Collection<Long> endTimeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setEndTime_Terms(endTimeList, opLambda);
    }

    public void setEndTime_Exists() {
        setEndTime_Exists(null);
    }

    public void setEndTime_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Missing() {
        setEndTime_Missing(null);
    }

    public void setEndTime_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_GreaterThan(Long endTime) {
        setEndTime_GreaterThan(endTime, null);
    }

    public void setEndTime_GreaterThan(Long endTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("endTime", ConditionKey.CK_GREATER_THAN, endTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_LessThan(Long endTime) {
        setEndTime_LessThan(endTime, null);
    }

    public void setEndTime_LessThan(Long endTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("endTime", ConditionKey.CK_LESS_THAN, endTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_GreaterEqual(Long endTime) {
        setEndTime_GreaterEqual(endTime, null);
    }

    public void setEndTime_GreaterEqual(Long endTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("endTime", ConditionKey.CK_GREATER_EQUAL, endTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_LessEqual(Long endTime) {
        setEndTime_LessEqual(endTime, null);
    }

    public void setEndTime_LessEqual(Long endTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("endTime", ConditionKey.CK_LESS_EQUAL, endTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Term(String id) {
        setId_Term(id, null);
    }

    public void setId_Term(String id, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Terms(Collection<String> idList) {
        setId_Terms(idList, null);
    }

    public void setId_Terms(Collection<String> idList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("id", idList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_InScope(Collection<String> idList) {
        setId_Terms(idList, null);
    }

    public void setId_InScope(Collection<String> idList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setId_Terms(idList, opLambda);
    }

    public void setId_Prefix(String id) {
        setId_Prefix(id, null);
    }

    public void setId_Prefix(String id, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Exists() {
        setId_Exists(null);
    }

    public void setId_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("id");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Missing() {
        setId_Missing(null);
    }

    public void setId_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("id");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_GreaterThan(String id) {
        setId_GreaterThan(id, null);
    }

    public void setId_GreaterThan(String id, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("id", ConditionKey.CK_GREATER_THAN, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_LessThan(String id) {
        setId_LessThan(id, null);
    }

    public void setId_LessThan(String id, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("id", ConditionKey.CK_LESS_THAN, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_GreaterEqual(String id) {
        setId_GreaterEqual(id, null);
    }

    public void setId_GreaterEqual(String id, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("id", ConditionKey.CK_GREATER_EQUAL, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_LessEqual(String id) {
        setId_LessEqual(id, null);
    }

    public void setId_LessEqual(String id, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("id", ConditionKey.CK_LESS_EQUAL, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_Term(String jobName) {
        setJobName_Term(jobName, null);
    }

    public void setJobName_Term(String jobName, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("jobName", jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_Terms(Collection<String> jobNameList) {
        setJobName_Terms(jobNameList, null);
    }

    public void setJobName_Terms(Collection<String> jobNameList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("jobName", jobNameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_InScope(Collection<String> jobNameList) {
        setJobName_Terms(jobNameList, null);
    }

    public void setJobName_InScope(Collection<String> jobNameList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setJobName_Terms(jobNameList, opLambda);
    }

    public void setJobName_Prefix(String jobName) {
        setJobName_Prefix(jobName, null);
    }

    public void setJobName_Prefix(String jobName, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("jobName", jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_Exists() {
        setJobName_Exists(null);
    }

    public void setJobName_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("jobName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_Missing() {
        setJobName_Missing(null);
    }

    public void setJobName_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("jobName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_GreaterThan(String jobName) {
        setJobName_GreaterThan(jobName, null);
    }

    public void setJobName_GreaterThan(String jobName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("jobName", ConditionKey.CK_GREATER_THAN, jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_LessThan(String jobName) {
        setJobName_LessThan(jobName, null);
    }

    public void setJobName_LessThan(String jobName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("jobName", ConditionKey.CK_LESS_THAN, jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_GreaterEqual(String jobName) {
        setJobName_GreaterEqual(jobName, null);
    }

    public void setJobName_GreaterEqual(String jobName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("jobName", ConditionKey.CK_GREATER_EQUAL, jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_LessEqual(String jobName) {
        setJobName_LessEqual(jobName, null);
    }

    public void setJobName_LessEqual(String jobName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("jobName", ConditionKey.CK_LESS_EQUAL, jobName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_Term(String jobStatus) {
        setJobStatus_Term(jobStatus, null);
    }

    public void setJobStatus_Term(String jobStatus, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("jobStatus", jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_Terms(Collection<String> jobStatusList) {
        setJobStatus_Terms(jobStatusList, null);
    }

    public void setJobStatus_Terms(Collection<String> jobStatusList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("jobStatus", jobStatusList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_InScope(Collection<String> jobStatusList) {
        setJobStatus_Terms(jobStatusList, null);
    }

    public void setJobStatus_InScope(Collection<String> jobStatusList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setJobStatus_Terms(jobStatusList, opLambda);
    }

    public void setJobStatus_Prefix(String jobStatus) {
        setJobStatus_Prefix(jobStatus, null);
    }

    public void setJobStatus_Prefix(String jobStatus, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("jobStatus", jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_Exists() {
        setJobStatus_Exists(null);
    }

    public void setJobStatus_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("jobStatus");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_Missing() {
        setJobStatus_Missing(null);
    }

    public void setJobStatus_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("jobStatus");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_GreaterThan(String jobStatus) {
        setJobStatus_GreaterThan(jobStatus, null);
    }

    public void setJobStatus_GreaterThan(String jobStatus, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("jobStatus", ConditionKey.CK_GREATER_THAN, jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_LessThan(String jobStatus) {
        setJobStatus_LessThan(jobStatus, null);
    }

    public void setJobStatus_LessThan(String jobStatus, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("jobStatus", ConditionKey.CK_LESS_THAN, jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_GreaterEqual(String jobStatus) {
        setJobStatus_GreaterEqual(jobStatus, null);
    }

    public void setJobStatus_GreaterEqual(String jobStatus, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("jobStatus", ConditionKey.CK_GREATER_EQUAL, jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_LessEqual(String jobStatus) {
        setJobStatus_LessEqual(jobStatus, null);
    }

    public void setJobStatus_LessEqual(String jobStatus, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("jobStatus", ConditionKey.CK_LESS_EQUAL, jobStatus);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Term(String scriptData) {
        setScriptData_Term(scriptData, null);
    }

    public void setScriptData_Term(String scriptData, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Terms(Collection<String> scriptDataList) {
        setScriptData_Terms(scriptDataList, null);
    }

    public void setScriptData_Terms(Collection<String> scriptDataList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("scriptData", scriptDataList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_InScope(Collection<String> scriptDataList) {
        setScriptData_Terms(scriptDataList, null);
    }

    public void setScriptData_InScope(Collection<String> scriptDataList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setScriptData_Terms(scriptDataList, opLambda);
    }

    public void setScriptData_Prefix(String scriptData) {
        setScriptData_Prefix(scriptData, null);
    }

    public void setScriptData_Prefix(String scriptData, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Exists() {
        setScriptData_Exists(null);
    }

    public void setScriptData_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("scriptData");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Missing() {
        setScriptData_Missing(null);
    }

    public void setScriptData_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("scriptData");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_GreaterThan(String scriptData) {
        setScriptData_GreaterThan(scriptData, null);
    }

    public void setScriptData_GreaterThan(String scriptData, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptData", ConditionKey.CK_GREATER_THAN, scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_LessThan(String scriptData) {
        setScriptData_LessThan(scriptData, null);
    }

    public void setScriptData_LessThan(String scriptData, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptData", ConditionKey.CK_LESS_THAN, scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_GreaterEqual(String scriptData) {
        setScriptData_GreaterEqual(scriptData, null);
    }

    public void setScriptData_GreaterEqual(String scriptData, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptData", ConditionKey.CK_GREATER_EQUAL, scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_LessEqual(String scriptData) {
        setScriptData_LessEqual(scriptData, null);
    }

    public void setScriptData_LessEqual(String scriptData, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptData", ConditionKey.CK_LESS_EQUAL, scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_Term(String scriptResult) {
        setScriptResult_Term(scriptResult, null);
    }

    public void setScriptResult_Term(String scriptResult, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("scriptResult", scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_Terms(Collection<String> scriptResultList) {
        setScriptResult_Terms(scriptResultList, null);
    }

    public void setScriptResult_Terms(Collection<String> scriptResultList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("scriptResult", scriptResultList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_InScope(Collection<String> scriptResultList) {
        setScriptResult_Terms(scriptResultList, null);
    }

    public void setScriptResult_InScope(Collection<String> scriptResultList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setScriptResult_Terms(scriptResultList, opLambda);
    }

    public void setScriptResult_Prefix(String scriptResult) {
        setScriptResult_Prefix(scriptResult, null);
    }

    public void setScriptResult_Prefix(String scriptResult, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("scriptResult", scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_Exists() {
        setScriptResult_Exists(null);
    }

    public void setScriptResult_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("scriptResult");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_Missing() {
        setScriptResult_Missing(null);
    }

    public void setScriptResult_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("scriptResult");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_GreaterThan(String scriptResult) {
        setScriptResult_GreaterThan(scriptResult, null);
    }

    public void setScriptResult_GreaterThan(String scriptResult, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptResult", ConditionKey.CK_GREATER_THAN, scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_LessThan(String scriptResult) {
        setScriptResult_LessThan(scriptResult, null);
    }

    public void setScriptResult_LessThan(String scriptResult, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptResult", ConditionKey.CK_LESS_THAN, scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_GreaterEqual(String scriptResult) {
        setScriptResult_GreaterEqual(scriptResult, null);
    }

    public void setScriptResult_GreaterEqual(String scriptResult, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptResult", ConditionKey.CK_GREATER_EQUAL, scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_LessEqual(String scriptResult) {
        setScriptResult_LessEqual(scriptResult, null);
    }

    public void setScriptResult_LessEqual(String scriptResult, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptResult", ConditionKey.CK_LESS_EQUAL, scriptResult);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Term(String scriptType) {
        setScriptType_Term(scriptType, null);
    }

    public void setScriptType_Term(String scriptType, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Terms(Collection<String> scriptTypeList) {
        setScriptType_Terms(scriptTypeList, null);
    }

    public void setScriptType_Terms(Collection<String> scriptTypeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("scriptType", scriptTypeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_InScope(Collection<String> scriptTypeList) {
        setScriptType_Terms(scriptTypeList, null);
    }

    public void setScriptType_InScope(Collection<String> scriptTypeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setScriptType_Terms(scriptTypeList, opLambda);
    }

    public void setScriptType_Prefix(String scriptType) {
        setScriptType_Prefix(scriptType, null);
    }

    public void setScriptType_Prefix(String scriptType, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Exists() {
        setScriptType_Exists(null);
    }

    public void setScriptType_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("scriptType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Missing() {
        setScriptType_Missing(null);
    }

    public void setScriptType_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("scriptType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_GreaterThan(String scriptType) {
        setScriptType_GreaterThan(scriptType, null);
    }

    public void setScriptType_GreaterThan(String scriptType, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptType", ConditionKey.CK_GREATER_THAN, scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_LessThan(String scriptType) {
        setScriptType_LessThan(scriptType, null);
    }

    public void setScriptType_LessThan(String scriptType, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptType", ConditionKey.CK_LESS_THAN, scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_GreaterEqual(String scriptType) {
        setScriptType_GreaterEqual(scriptType, null);
    }

    public void setScriptType_GreaterEqual(String scriptType, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptType", ConditionKey.CK_GREATER_EQUAL, scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_LessEqual(String scriptType) {
        setScriptType_LessEqual(scriptType, null);
    }

    public void setScriptType_LessEqual(String scriptType, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("scriptType", ConditionKey.CK_LESS_EQUAL, scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Term(Long startTime) {
        setStartTime_Term(startTime, null);
    }

    public void setStartTime_Term(Long startTime, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("startTime", startTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Terms(Collection<Long> startTimeList) {
        setStartTime_Terms(startTimeList, null);
    }

    public void setStartTime_Terms(Collection<Long> startTimeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("startTime", startTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_InScope(Collection<Long> startTimeList) {
        setStartTime_Terms(startTimeList, null);
    }

    public void setStartTime_InScope(Collection<Long> startTimeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setStartTime_Terms(startTimeList, opLambda);
    }

    public void setStartTime_Exists() {
        setStartTime_Exists(null);
    }

    public void setStartTime_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Missing() {
        setStartTime_Missing(null);
    }

    public void setStartTime_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_GreaterThan(Long startTime) {
        setStartTime_GreaterThan(startTime, null);
    }

    public void setStartTime_GreaterThan(Long startTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("startTime", ConditionKey.CK_GREATER_THAN, startTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_LessThan(Long startTime) {
        setStartTime_LessThan(startTime, null);
    }

    public void setStartTime_LessThan(Long startTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("startTime", ConditionKey.CK_LESS_THAN, startTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_GreaterEqual(Long startTime) {
        setStartTime_GreaterEqual(startTime, null);
    }

    public void setStartTime_GreaterEqual(Long startTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("startTime", ConditionKey.CK_GREATER_EQUAL, startTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_LessEqual(Long startTime) {
        setStartTime_LessEqual(startTime, null);
    }

    public void setStartTime_LessEqual(Long startTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("startTime", ConditionKey.CK_LESS_EQUAL, startTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Term(String target) {
        setTarget_Term(target, null);
    }

    public void setTarget_Term(String target, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Terms(Collection<String> targetList) {
        setTarget_Terms(targetList, null);
    }

    public void setTarget_Terms(Collection<String> targetList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("target", targetList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_InScope(Collection<String> targetList) {
        setTarget_Terms(targetList, null);
    }

    public void setTarget_InScope(Collection<String> targetList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setTarget_Terms(targetList, opLambda);
    }

    public void setTarget_Prefix(String target) {
        setTarget_Prefix(target, null);
    }

    public void setTarget_Prefix(String target, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Exists() {
        setTarget_Exists(null);
    }

    public void setTarget_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Missing() {
        setTarget_Missing(null);
    }

    public void setTarget_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_GreaterThan(String target) {
        setTarget_GreaterThan(target, null);
    }

    public void setTarget_GreaterThan(String target, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("target", ConditionKey.CK_GREATER_THAN, target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_LessThan(String target) {
        setTarget_LessThan(target, null);
    }

    public void setTarget_LessThan(String target, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("target", ConditionKey.CK_LESS_THAN, target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_GreaterEqual(String target) {
        setTarget_GreaterEqual(target, null);
    }

    public void setTarget_GreaterEqual(String target, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("target", ConditionKey.CK_GREATER_EQUAL, target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_LessEqual(String target) {
        setTarget_LessEqual(target, null);
    }

    public void setTarget_LessEqual(String target, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("target", ConditionKey.CK_LESS_EQUAL, target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

}
