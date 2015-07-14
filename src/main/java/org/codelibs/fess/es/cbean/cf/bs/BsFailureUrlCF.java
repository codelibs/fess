package org.codelibs.fess.es.cbean.cf.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cf.FailureUrlCF;
import org.codelibs.fess.es.cbean.cq.FailureUrlCQ;
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
public abstract class BsFailureUrlCF extends AbstractConditionFilter {

    public void bool(BoolCall<FailureUrlCF> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<FailureUrlCF> boolLambda, ConditionOptionCall<BoolFilterBuilder> opLambda) {
        FailureUrlCF mustFilter = new FailureUrlCF();
        FailureUrlCF shouldFilter = new FailureUrlCF();
        FailureUrlCF mustNotFilter = new FailureUrlCF();
        boolLambda.callback(mustFilter, shouldFilter, mustNotFilter);
        if (mustFilter.hasFilters() || shouldFilter.hasFilters() || mustNotFilter.hasFilters()) {
            BoolFilterBuilder builder =
                    regBoolF(mustFilter.filterBuilderList, shouldFilter.filterBuilderList, mustNotFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void and(OperatorCall<FailureUrlCF> andLambda) {
        and(andLambda, null);
    }

    public void and(OperatorCall<FailureUrlCF> andLambda, ConditionOptionCall<AndFilterBuilder> opLambda) {
        FailureUrlCF andFilter = new FailureUrlCF();
        andLambda.callback(andFilter);
        if (andFilter.hasFilters()) {
            AndFilterBuilder builder = regAndF(andFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void or(OperatorCall<FailureUrlCF> orLambda) {
        or(orLambda, null);
    }

    public void or(OperatorCall<FailureUrlCF> orLambda, ConditionOptionCall<OrFilterBuilder> opLambda) {
        FailureUrlCF orFilter = new FailureUrlCF();
        orLambda.callback(orFilter);
        if (orFilter.hasFilters()) {
            OrFilterBuilder builder = regOrF(orFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void not(OperatorCall<FailureUrlCF> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<FailureUrlCF> notLambda, ConditionOptionCall<NotFilterBuilder> opLambda) {
        FailureUrlCF notFilter = new FailureUrlCF();
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

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<FailureUrlCQ> queryLambda) {
        query(queryLambda, null);
    }

    public void query(org.codelibs.fess.es.cbean.cq.bs.AbstractConditionQuery.OperatorCall<FailureUrlCQ> queryLambda,
            ConditionOptionCall<QueryFilterBuilder> opLambda) {
        FailureUrlCQ query = new FailureUrlCQ();
        queryLambda.callback(query);
        if (query.hasQueries()) {
            QueryFilterBuilder builder = regQueryF(query.getQuery());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void setConfigId_NotEqual(String configId) {
        setConfigId_NotEqual(configId, null, null);
    }

    public void setConfigId_NotEqual(String configId, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setConfigId_Equal(configId, eqOpLambda);
        }, notOpLambda);
    }

    public void setConfigId_Equal(String configId) {
        setConfigId_Term(configId, null);
    }

    public void setConfigId_Equal(String configId, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setConfigId_Term(configId, opLambda);
    }

    public void setConfigId_Term(String configId) {
        setConfigId_Term(configId, null);
    }

    public void setConfigId_Term(String configId, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("configId", configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_Terms(Collection<String> configIdList) {
        setConfigId_Terms(configIdList, null);
    }

    public void setConfigId_Terms(Collection<String> configIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("configId", configIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_InScope(Collection<String> configIdList) {
        setConfigId_Terms(configIdList, null);
    }

    public void setConfigId_InScope(Collection<String> configIdList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setConfigId_Terms(configIdList, opLambda);
    }

    public void setConfigId_Prefix(String configId) {
        setConfigId_Prefix(configId, null);
    }

    public void setConfigId_Prefix(String configId, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("configId", configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_Exists() {
        setConfigId_Exists(null);
    }

    public void setConfigId_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("configId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_Missing() {
        setConfigId_Missing(null);
    }

    public void setConfigId_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("configId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_GreaterThan(String configId) {
        setConfigId_GreaterThan(configId, null);
    }

    public void setConfigId_GreaterThan(String configId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("configId", ConditionKey.CK_GREATER_THAN, configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_LessThan(String configId) {
        setConfigId_LessThan(configId, null);
    }

    public void setConfigId_LessThan(String configId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("configId", ConditionKey.CK_LESS_THAN, configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_GreaterEqual(String configId) {
        setConfigId_GreaterEqual(configId, null);
    }

    public void setConfigId_GreaterEqual(String configId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("configId", ConditionKey.CK_GREATER_EQUAL, configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_LessEqual(String configId) {
        setConfigId_LessEqual(configId, null);
    }

    public void setConfigId_LessEqual(String configId, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("configId", ConditionKey.CK_LESS_EQUAL, configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_NotEqual(Integer errorCount) {
        setErrorCount_NotEqual(errorCount, null, null);
    }

    public void setErrorCount_NotEqual(Integer errorCount, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setErrorCount_Equal(errorCount, eqOpLambda);
        }, notOpLambda);
    }

    public void setErrorCount_Equal(Integer errorCount) {
        setErrorCount_Term(errorCount, null);
    }

    public void setErrorCount_Equal(Integer errorCount, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setErrorCount_Term(errorCount, opLambda);
    }

    public void setErrorCount_Term(Integer errorCount) {
        setErrorCount_Term(errorCount, null);
    }

    public void setErrorCount_Term(Integer errorCount, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("errorCount", errorCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_Terms(Collection<Integer> errorCountList) {
        setErrorCount_Terms(errorCountList, null);
    }

    public void setErrorCount_Terms(Collection<Integer> errorCountList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("errorCount", errorCountList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_InScope(Collection<Integer> errorCountList) {
        setErrorCount_Terms(errorCountList, null);
    }

    public void setErrorCount_InScope(Collection<Integer> errorCountList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setErrorCount_Terms(errorCountList, opLambda);
    }

    public void setErrorCount_Exists() {
        setErrorCount_Exists(null);
    }

    public void setErrorCount_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("errorCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_Missing() {
        setErrorCount_Missing(null);
    }

    public void setErrorCount_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("errorCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_GreaterThan(Integer errorCount) {
        setErrorCount_GreaterThan(errorCount, null);
    }

    public void setErrorCount_GreaterThan(Integer errorCount, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("errorCount", ConditionKey.CK_GREATER_THAN, errorCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_LessThan(Integer errorCount) {
        setErrorCount_LessThan(errorCount, null);
    }

    public void setErrorCount_LessThan(Integer errorCount, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("errorCount", ConditionKey.CK_LESS_THAN, errorCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_GreaterEqual(Integer errorCount) {
        setErrorCount_GreaterEqual(errorCount, null);
    }

    public void setErrorCount_GreaterEqual(Integer errorCount, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("errorCount", ConditionKey.CK_GREATER_EQUAL, errorCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_LessEqual(Integer errorCount) {
        setErrorCount_LessEqual(errorCount, null);
    }

    public void setErrorCount_LessEqual(Integer errorCount, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("errorCount", ConditionKey.CK_LESS_EQUAL, errorCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_NotEqual(String errorLog) {
        setErrorLog_NotEqual(errorLog, null, null);
    }

    public void setErrorLog_NotEqual(String errorLog, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setErrorLog_Equal(errorLog, eqOpLambda);
        }, notOpLambda);
    }

    public void setErrorLog_Equal(String errorLog) {
        setErrorLog_Term(errorLog, null);
    }

    public void setErrorLog_Equal(String errorLog, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setErrorLog_Term(errorLog, opLambda);
    }

    public void setErrorLog_Term(String errorLog) {
        setErrorLog_Term(errorLog, null);
    }

    public void setErrorLog_Term(String errorLog, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("errorLog", errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_Terms(Collection<String> errorLogList) {
        setErrorLog_Terms(errorLogList, null);
    }

    public void setErrorLog_Terms(Collection<String> errorLogList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("errorLog", errorLogList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_InScope(Collection<String> errorLogList) {
        setErrorLog_Terms(errorLogList, null);
    }

    public void setErrorLog_InScope(Collection<String> errorLogList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setErrorLog_Terms(errorLogList, opLambda);
    }

    public void setErrorLog_Prefix(String errorLog) {
        setErrorLog_Prefix(errorLog, null);
    }

    public void setErrorLog_Prefix(String errorLog, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("errorLog", errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_Exists() {
        setErrorLog_Exists(null);
    }

    public void setErrorLog_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("errorLog");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_Missing() {
        setErrorLog_Missing(null);
    }

    public void setErrorLog_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("errorLog");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_GreaterThan(String errorLog) {
        setErrorLog_GreaterThan(errorLog, null);
    }

    public void setErrorLog_GreaterThan(String errorLog, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("errorLog", ConditionKey.CK_GREATER_THAN, errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_LessThan(String errorLog) {
        setErrorLog_LessThan(errorLog, null);
    }

    public void setErrorLog_LessThan(String errorLog, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("errorLog", ConditionKey.CK_LESS_THAN, errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_GreaterEqual(String errorLog) {
        setErrorLog_GreaterEqual(errorLog, null);
    }

    public void setErrorLog_GreaterEqual(String errorLog, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("errorLog", ConditionKey.CK_GREATER_EQUAL, errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_LessEqual(String errorLog) {
        setErrorLog_LessEqual(errorLog, null);
    }

    public void setErrorLog_LessEqual(String errorLog, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("errorLog", ConditionKey.CK_LESS_EQUAL, errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_NotEqual(String errorName) {
        setErrorName_NotEqual(errorName, null, null);
    }

    public void setErrorName_NotEqual(String errorName, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setErrorName_Equal(errorName, eqOpLambda);
        }, notOpLambda);
    }

    public void setErrorName_Equal(String errorName) {
        setErrorName_Term(errorName, null);
    }

    public void setErrorName_Equal(String errorName, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setErrorName_Term(errorName, opLambda);
    }

    public void setErrorName_Term(String errorName) {
        setErrorName_Term(errorName, null);
    }

    public void setErrorName_Term(String errorName, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("errorName", errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_Terms(Collection<String> errorNameList) {
        setErrorName_Terms(errorNameList, null);
    }

    public void setErrorName_Terms(Collection<String> errorNameList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("errorName", errorNameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_InScope(Collection<String> errorNameList) {
        setErrorName_Terms(errorNameList, null);
    }

    public void setErrorName_InScope(Collection<String> errorNameList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setErrorName_Terms(errorNameList, opLambda);
    }

    public void setErrorName_Prefix(String errorName) {
        setErrorName_Prefix(errorName, null);
    }

    public void setErrorName_Prefix(String errorName, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("errorName", errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_Exists() {
        setErrorName_Exists(null);
    }

    public void setErrorName_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("errorName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_Missing() {
        setErrorName_Missing(null);
    }

    public void setErrorName_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("errorName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_GreaterThan(String errorName) {
        setErrorName_GreaterThan(errorName, null);
    }

    public void setErrorName_GreaterThan(String errorName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("errorName", ConditionKey.CK_GREATER_THAN, errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_LessThan(String errorName) {
        setErrorName_LessThan(errorName, null);
    }

    public void setErrorName_LessThan(String errorName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("errorName", ConditionKey.CK_LESS_THAN, errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_GreaterEqual(String errorName) {
        setErrorName_GreaterEqual(errorName, null);
    }

    public void setErrorName_GreaterEqual(String errorName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("errorName", ConditionKey.CK_GREATER_EQUAL, errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_LessEqual(String errorName) {
        setErrorName_LessEqual(errorName, null);
    }

    public void setErrorName_LessEqual(String errorName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("errorName", ConditionKey.CK_LESS_EQUAL, errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_NotEqual(String id) {
        setId_NotEqual(id, null, null);
    }

    public void setId_NotEqual(String id, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setId_Equal(id, eqOpLambda);
        }, notOpLambda);
    }

    public void setId_Equal(String id) {
        setId_Term(id, null);
    }

    public void setId_Equal(String id, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setId_Term(id, opLambda);
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

    public void setLastAccessTime_NotEqual(Long lastAccessTime) {
        setLastAccessTime_NotEqual(lastAccessTime, null, null);
    }

    public void setLastAccessTime_NotEqual(Long lastAccessTime, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setLastAccessTime_Equal(lastAccessTime, eqOpLambda);
        }, notOpLambda);
    }

    public void setLastAccessTime_Equal(Long lastAccessTime) {
        setLastAccessTime_Term(lastAccessTime, null);
    }

    public void setLastAccessTime_Equal(Long lastAccessTime, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setLastAccessTime_Term(lastAccessTime, opLambda);
    }

    public void setLastAccessTime_Term(Long lastAccessTime) {
        setLastAccessTime_Term(lastAccessTime, null);
    }

    public void setLastAccessTime_Term(Long lastAccessTime, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("lastAccessTime", lastAccessTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_Terms(Collection<Long> lastAccessTimeList) {
        setLastAccessTime_Terms(lastAccessTimeList, null);
    }

    public void setLastAccessTime_Terms(Collection<Long> lastAccessTimeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("lastAccessTime", lastAccessTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_InScope(Collection<Long> lastAccessTimeList) {
        setLastAccessTime_Terms(lastAccessTimeList, null);
    }

    public void setLastAccessTime_InScope(Collection<Long> lastAccessTimeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setLastAccessTime_Terms(lastAccessTimeList, opLambda);
    }

    public void setLastAccessTime_Exists() {
        setLastAccessTime_Exists(null);
    }

    public void setLastAccessTime_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("lastAccessTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_Missing() {
        setLastAccessTime_Missing(null);
    }

    public void setLastAccessTime_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("lastAccessTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_GreaterThan(Long lastAccessTime) {
        setLastAccessTime_GreaterThan(lastAccessTime, null);
    }

    public void setLastAccessTime_GreaterThan(Long lastAccessTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("lastAccessTime", ConditionKey.CK_GREATER_THAN, lastAccessTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_LessThan(Long lastAccessTime) {
        setLastAccessTime_LessThan(lastAccessTime, null);
    }

    public void setLastAccessTime_LessThan(Long lastAccessTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("lastAccessTime", ConditionKey.CK_LESS_THAN, lastAccessTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_GreaterEqual(Long lastAccessTime) {
        setLastAccessTime_GreaterEqual(lastAccessTime, null);
    }

    public void setLastAccessTime_GreaterEqual(Long lastAccessTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("lastAccessTime", ConditionKey.CK_GREATER_EQUAL, lastAccessTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_LessEqual(Long lastAccessTime) {
        setLastAccessTime_LessEqual(lastAccessTime, null);
    }

    public void setLastAccessTime_LessEqual(Long lastAccessTime, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("lastAccessTime", ConditionKey.CK_LESS_EQUAL, lastAccessTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_NotEqual(String threadName) {
        setThreadName_NotEqual(threadName, null, null);
    }

    public void setThreadName_NotEqual(String threadName, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setThreadName_Equal(threadName, eqOpLambda);
        }, notOpLambda);
    }

    public void setThreadName_Equal(String threadName) {
        setThreadName_Term(threadName, null);
    }

    public void setThreadName_Equal(String threadName, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setThreadName_Term(threadName, opLambda);
    }

    public void setThreadName_Term(String threadName) {
        setThreadName_Term(threadName, null);
    }

    public void setThreadName_Term(String threadName, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("threadName", threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_Terms(Collection<String> threadNameList) {
        setThreadName_Terms(threadNameList, null);
    }

    public void setThreadName_Terms(Collection<String> threadNameList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("threadName", threadNameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_InScope(Collection<String> threadNameList) {
        setThreadName_Terms(threadNameList, null);
    }

    public void setThreadName_InScope(Collection<String> threadNameList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setThreadName_Terms(threadNameList, opLambda);
    }

    public void setThreadName_Prefix(String threadName) {
        setThreadName_Prefix(threadName, null);
    }

    public void setThreadName_Prefix(String threadName, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("threadName", threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_Exists() {
        setThreadName_Exists(null);
    }

    public void setThreadName_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("threadName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_Missing() {
        setThreadName_Missing(null);
    }

    public void setThreadName_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("threadName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_GreaterThan(String threadName) {
        setThreadName_GreaterThan(threadName, null);
    }

    public void setThreadName_GreaterThan(String threadName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("threadName", ConditionKey.CK_GREATER_THAN, threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_LessThan(String threadName) {
        setThreadName_LessThan(threadName, null);
    }

    public void setThreadName_LessThan(String threadName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("threadName", ConditionKey.CK_LESS_THAN, threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_GreaterEqual(String threadName) {
        setThreadName_GreaterEqual(threadName, null);
    }

    public void setThreadName_GreaterEqual(String threadName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("threadName", ConditionKey.CK_GREATER_EQUAL, threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_LessEqual(String threadName) {
        setThreadName_LessEqual(threadName, null);
    }

    public void setThreadName_LessEqual(String threadName, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("threadName", ConditionKey.CK_LESS_EQUAL, threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_NotEqual(String url) {
        setUrl_NotEqual(url, null, null);
    }

    public void setUrl_NotEqual(String url, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setUrl_Equal(url, eqOpLambda);
        }, notOpLambda);
    }

    public void setUrl_Equal(String url) {
        setUrl_Term(url, null);
    }

    public void setUrl_Equal(String url, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setUrl_Term(url, opLambda);
    }

    public void setUrl_Term(String url) {
        setUrl_Term(url, null);
    }

    public void setUrl_Term(String url, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Terms(Collection<String> urlList) {
        setUrl_Terms(urlList, null);
    }

    public void setUrl_Terms(Collection<String> urlList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("url", urlList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_InScope(Collection<String> urlList) {
        setUrl_Terms(urlList, null);
    }

    public void setUrl_InScope(Collection<String> urlList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setUrl_Terms(urlList, opLambda);
    }

    public void setUrl_Prefix(String url) {
        setUrl_Prefix(url, null);
    }

    public void setUrl_Prefix(String url, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Exists() {
        setUrl_Exists(null);
    }

    public void setUrl_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Missing() {
        setUrl_Missing(null);
    }

    public void setUrl_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_GreaterThan(String url) {
        setUrl_GreaterThan(url, null);
    }

    public void setUrl_GreaterThan(String url, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("url", ConditionKey.CK_GREATER_THAN, url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_LessThan(String url) {
        setUrl_LessThan(url, null);
    }

    public void setUrl_LessThan(String url, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("url", ConditionKey.CK_LESS_THAN, url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_GreaterEqual(String url) {
        setUrl_GreaterEqual(url, null);
    }

    public void setUrl_GreaterEqual(String url, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("url", ConditionKey.CK_GREATER_EQUAL, url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_LessEqual(String url) {
        setUrl_LessEqual(url, null);
    }

    public void setUrl_LessEqual(String url, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("url", ConditionKey.CK_LESS_EQUAL, url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

}
