package org.codelibs.fess.es.cbean.cq.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cq.FailureUrlCQ;
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
public abstract class BsFailureUrlCQ extends AbstractConditionQuery {

    @Override
    public String asTableDbName() {
        return "failure_url";
    }

    @Override
    public String xgetAliasName() {
        return "failure_url";
    }

    public void filtered(FilteredCall<FailureUrlCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<FailureUrlCQ> filteredLambda, ConditionOptionCall<FilteredQueryBuilder> opLambda) {
        FailureUrlCQ query = new FailureUrlCQ();
        filteredLambda.callback(query);
        if (!query.queryBuilderList.isEmpty()) {
            // TODO filter
            FilteredQueryBuilder builder = reqFilteredQ(query.getQuery(), null);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void bool(BoolCall<FailureUrlCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<FailureUrlCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        FailureUrlCQ mustQuery = new FailureUrlCQ();
        FailureUrlCQ shouldQuery = new FailureUrlCQ();
        FailureUrlCQ mustNotQuery = new FailureUrlCQ();
        boolLambda.callback(mustQuery, shouldQuery, mustNotQuery);
        if (!mustQuery.queryBuilderList.isEmpty() || !shouldQuery.queryBuilderList.isEmpty() || !mustNotQuery.queryBuilderList.isEmpty()) {
            BoolQueryBuilder builder = reqBoolCQ(mustQuery.queryBuilderList, shouldQuery.queryBuilderList, mustNotQuery.queryBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void setConfigId_Term(String configId) {
        setConfigId_Term(configId, null);
    }

    public void setConfigId_Term(String configId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("configId", configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_Terms(Collection<String> configIdList) {
        setConfigId_MatchPhrasePrefix(configIdList, null);
    }

    public void setConfigId_MatchPhrasePrefix(Collection<String> configIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("configId", configIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_InScope(Collection<String> configIdList) {
        setConfigId_MatchPhrasePrefix(configIdList, null);
    }

    public void setConfigId_InScope(Collection<String> configIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setConfigId_MatchPhrasePrefix(configIdList, opLambda);
    }

    public void setConfigId_Match(String configId) {
        setConfigId_Match(configId, null);
    }

    public void setConfigId_Match(String configId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("configId", configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_MatchPhrase(String configId) {
        setConfigId_MatchPhrase(configId, null);
    }

    public void setConfigId_MatchPhrase(String configId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("configId", configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_MatchPhrasePrefix(String configId) {
        setConfigId_MatchPhrasePrefix(configId, null);
    }

    public void setConfigId_MatchPhrasePrefix(String configId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("configId", configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_Fuzzy(String configId) {
        setConfigId_Fuzzy(configId, null);
    }

    public void setConfigId_Fuzzy(String configId, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("configId", configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_Prefix(String configId) {
        setConfigId_Prefix(configId, null);
    }

    public void setConfigId_Prefix(String configId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("configId", configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_GreaterThan(String configId) {
        setConfigId_GreaterThan(configId, null);
    }

    public void setConfigId_GreaterThan(String configId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("configId", ConditionKey.CK_GREATER_THAN, configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_LessThan(String configId) {
        setConfigId_LessThan(configId, null);
    }

    public void setConfigId_LessThan(String configId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("configId", ConditionKey.CK_LESS_THAN, configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_GreaterEqual(String configId) {
        setConfigId_GreaterEqual(configId, null);
    }

    public void setConfigId_GreaterEqual(String configId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("configId", ConditionKey.CK_GREATER_EQUAL, configId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_LessEqual(String configId) {
        setConfigId_LessEqual(configId, null);
    }

    public void setConfigId_LessEqual(String configId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("configId", ConditionKey.CK_LESS_EQUAL, configId);
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

    public void setErrorCount_Term(Integer errorCount) {
        setErrorCount_Term(errorCount, null);
    }

    public void setErrorCount_Term(Integer errorCount, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("errorCount", errorCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_Terms(Collection<Integer> errorCountList) {
        setErrorCount_MatchPhrasePrefix(errorCountList, null);
    }

    public void setErrorCount_MatchPhrasePrefix(Collection<Integer> errorCountList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("errorCount", errorCountList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_InScope(Collection<Integer> errorCountList) {
        setErrorCount_MatchPhrasePrefix(errorCountList, null);
    }

    public void setErrorCount_InScope(Collection<Integer> errorCountList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setErrorCount_MatchPhrasePrefix(errorCountList, opLambda);
    }

    public void setErrorCount_Match(Integer errorCount) {
        setErrorCount_Match(errorCount, null);
    }

    public void setErrorCount_Match(Integer errorCount, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("errorCount", errorCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_MatchPhrase(Integer errorCount) {
        setErrorCount_MatchPhrase(errorCount, null);
    }

    public void setErrorCount_MatchPhrase(Integer errorCount, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("errorCount", errorCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_MatchPhrasePrefix(Integer errorCount) {
        setErrorCount_MatchPhrasePrefix(errorCount, null);
    }

    public void setErrorCount_MatchPhrasePrefix(Integer errorCount, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("errorCount", errorCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_Fuzzy(Integer errorCount) {
        setErrorCount_Fuzzy(errorCount, null);
    }

    public void setErrorCount_Fuzzy(Integer errorCount, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("errorCount", errorCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_GreaterThan(Integer errorCount) {
        setErrorCount_GreaterThan(errorCount, null);
    }

    public void setErrorCount_GreaterThan(Integer errorCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("errorCount", ConditionKey.CK_GREATER_THAN, errorCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_LessThan(Integer errorCount) {
        setErrorCount_LessThan(errorCount, null);
    }

    public void setErrorCount_LessThan(Integer errorCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("errorCount", ConditionKey.CK_LESS_THAN, errorCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_GreaterEqual(Integer errorCount) {
        setErrorCount_GreaterEqual(errorCount, null);
    }

    public void setErrorCount_GreaterEqual(Integer errorCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("errorCount", ConditionKey.CK_GREATER_EQUAL, errorCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_LessEqual(Integer errorCount) {
        setErrorCount_LessEqual(errorCount, null);
    }

    public void setErrorCount_LessEqual(Integer errorCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("errorCount", ConditionKey.CK_LESS_EQUAL, errorCount);
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

    public void setErrorLog_Term(String errorLog) {
        setErrorLog_Term(errorLog, null);
    }

    public void setErrorLog_Term(String errorLog, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("errorLog", errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_Terms(Collection<String> errorLogList) {
        setErrorLog_MatchPhrasePrefix(errorLogList, null);
    }

    public void setErrorLog_MatchPhrasePrefix(Collection<String> errorLogList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("errorLog", errorLogList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_InScope(Collection<String> errorLogList) {
        setErrorLog_MatchPhrasePrefix(errorLogList, null);
    }

    public void setErrorLog_InScope(Collection<String> errorLogList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setErrorLog_MatchPhrasePrefix(errorLogList, opLambda);
    }

    public void setErrorLog_Match(String errorLog) {
        setErrorLog_Match(errorLog, null);
    }

    public void setErrorLog_Match(String errorLog, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("errorLog", errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_MatchPhrase(String errorLog) {
        setErrorLog_MatchPhrase(errorLog, null);
    }

    public void setErrorLog_MatchPhrase(String errorLog, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("errorLog", errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_MatchPhrasePrefix(String errorLog) {
        setErrorLog_MatchPhrasePrefix(errorLog, null);
    }

    public void setErrorLog_MatchPhrasePrefix(String errorLog, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("errorLog", errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_Fuzzy(String errorLog) {
        setErrorLog_Fuzzy(errorLog, null);
    }

    public void setErrorLog_Fuzzy(String errorLog, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("errorLog", errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_Prefix(String errorLog) {
        setErrorLog_Prefix(errorLog, null);
    }

    public void setErrorLog_Prefix(String errorLog, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("errorLog", errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_GreaterThan(String errorLog) {
        setErrorLog_GreaterThan(errorLog, null);
    }

    public void setErrorLog_GreaterThan(String errorLog, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("errorLog", ConditionKey.CK_GREATER_THAN, errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_LessThan(String errorLog) {
        setErrorLog_LessThan(errorLog, null);
    }

    public void setErrorLog_LessThan(String errorLog, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("errorLog", ConditionKey.CK_LESS_THAN, errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_GreaterEqual(String errorLog) {
        setErrorLog_GreaterEqual(errorLog, null);
    }

    public void setErrorLog_GreaterEqual(String errorLog, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("errorLog", ConditionKey.CK_GREATER_EQUAL, errorLog);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_LessEqual(String errorLog) {
        setErrorLog_LessEqual(errorLog, null);
    }

    public void setErrorLog_LessEqual(String errorLog, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("errorLog", ConditionKey.CK_LESS_EQUAL, errorLog);
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

    public void setErrorName_Term(String errorName) {
        setErrorName_Term(errorName, null);
    }

    public void setErrorName_Term(String errorName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("errorName", errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_Terms(Collection<String> errorNameList) {
        setErrorName_MatchPhrasePrefix(errorNameList, null);
    }

    public void setErrorName_MatchPhrasePrefix(Collection<String> errorNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("errorName", errorNameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_InScope(Collection<String> errorNameList) {
        setErrorName_MatchPhrasePrefix(errorNameList, null);
    }

    public void setErrorName_InScope(Collection<String> errorNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setErrorName_MatchPhrasePrefix(errorNameList, opLambda);
    }

    public void setErrorName_Match(String errorName) {
        setErrorName_Match(errorName, null);
    }

    public void setErrorName_Match(String errorName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("errorName", errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_MatchPhrase(String errorName) {
        setErrorName_MatchPhrase(errorName, null);
    }

    public void setErrorName_MatchPhrase(String errorName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("errorName", errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_MatchPhrasePrefix(String errorName) {
        setErrorName_MatchPhrasePrefix(errorName, null);
    }

    public void setErrorName_MatchPhrasePrefix(String errorName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("errorName", errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_Fuzzy(String errorName) {
        setErrorName_Fuzzy(errorName, null);
    }

    public void setErrorName_Fuzzy(String errorName, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("errorName", errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_Prefix(String errorName) {
        setErrorName_Prefix(errorName, null);
    }

    public void setErrorName_Prefix(String errorName, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("errorName", errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_GreaterThan(String errorName) {
        setErrorName_GreaterThan(errorName, null);
    }

    public void setErrorName_GreaterThan(String errorName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("errorName", ConditionKey.CK_GREATER_THAN, errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_LessThan(String errorName) {
        setErrorName_LessThan(errorName, null);
    }

    public void setErrorName_LessThan(String errorName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("errorName", ConditionKey.CK_LESS_THAN, errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_GreaterEqual(String errorName) {
        setErrorName_GreaterEqual(errorName, null);
    }

    public void setErrorName_GreaterEqual(String errorName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("errorName", ConditionKey.CK_GREATER_EQUAL, errorName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_LessEqual(String errorName) {
        setErrorName_LessEqual(errorName, null);
    }

    public void setErrorName_LessEqual(String errorName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("errorName", ConditionKey.CK_LESS_EQUAL, errorName);
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

    public BsFailureUrlCQ addOrderBy_Id_Asc() {
        regOBA("id");
        return this;
    }

    public BsFailureUrlCQ addOrderBy_Id_Desc() {
        regOBD("id");
        return this;
    }

    public void setLastAccessTime_Term(Long lastAccessTime) {
        setLastAccessTime_Term(lastAccessTime, null);
    }

    public void setLastAccessTime_Term(Long lastAccessTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("lastAccessTime", lastAccessTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_Terms(Collection<Long> lastAccessTimeList) {
        setLastAccessTime_MatchPhrasePrefix(lastAccessTimeList, null);
    }

    public void setLastAccessTime_MatchPhrasePrefix(Collection<Long> lastAccessTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("lastAccessTime", lastAccessTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_InScope(Collection<Long> lastAccessTimeList) {
        setLastAccessTime_MatchPhrasePrefix(lastAccessTimeList, null);
    }

    public void setLastAccessTime_InScope(Collection<Long> lastAccessTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setLastAccessTime_MatchPhrasePrefix(lastAccessTimeList, opLambda);
    }

    public void setLastAccessTime_Match(Long lastAccessTime) {
        setLastAccessTime_Match(lastAccessTime, null);
    }

    public void setLastAccessTime_Match(Long lastAccessTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("lastAccessTime", lastAccessTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_MatchPhrase(Long lastAccessTime) {
        setLastAccessTime_MatchPhrase(lastAccessTime, null);
    }

    public void setLastAccessTime_MatchPhrase(Long lastAccessTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("lastAccessTime", lastAccessTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_MatchPhrasePrefix(Long lastAccessTime) {
        setLastAccessTime_MatchPhrasePrefix(lastAccessTime, null);
    }

    public void setLastAccessTime_MatchPhrasePrefix(Long lastAccessTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("lastAccessTime", lastAccessTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_Fuzzy(Long lastAccessTime) {
        setLastAccessTime_Fuzzy(lastAccessTime, null);
    }

    public void setLastAccessTime_Fuzzy(Long lastAccessTime, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("lastAccessTime", lastAccessTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_GreaterThan(Long lastAccessTime) {
        setLastAccessTime_GreaterThan(lastAccessTime, null);
    }

    public void setLastAccessTime_GreaterThan(Long lastAccessTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("lastAccessTime", ConditionKey.CK_GREATER_THAN, lastAccessTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_LessThan(Long lastAccessTime) {
        setLastAccessTime_LessThan(lastAccessTime, null);
    }

    public void setLastAccessTime_LessThan(Long lastAccessTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("lastAccessTime", ConditionKey.CK_LESS_THAN, lastAccessTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_GreaterEqual(Long lastAccessTime) {
        setLastAccessTime_GreaterEqual(lastAccessTime, null);
    }

    public void setLastAccessTime_GreaterEqual(Long lastAccessTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("lastAccessTime", ConditionKey.CK_GREATER_EQUAL, lastAccessTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_LessEqual(Long lastAccessTime) {
        setLastAccessTime_LessEqual(lastAccessTime, null);
    }

    public void setLastAccessTime_LessEqual(Long lastAccessTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("lastAccessTime", ConditionKey.CK_LESS_EQUAL, lastAccessTime);
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

    public void setThreadName_Term(String threadName) {
        setThreadName_Term(threadName, null);
    }

    public void setThreadName_Term(String threadName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("threadName", threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_Terms(Collection<String> threadNameList) {
        setThreadName_MatchPhrasePrefix(threadNameList, null);
    }

    public void setThreadName_MatchPhrasePrefix(Collection<String> threadNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("threadName", threadNameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_InScope(Collection<String> threadNameList) {
        setThreadName_MatchPhrasePrefix(threadNameList, null);
    }

    public void setThreadName_InScope(Collection<String> threadNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setThreadName_MatchPhrasePrefix(threadNameList, opLambda);
    }

    public void setThreadName_Match(String threadName) {
        setThreadName_Match(threadName, null);
    }

    public void setThreadName_Match(String threadName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("threadName", threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_MatchPhrase(String threadName) {
        setThreadName_MatchPhrase(threadName, null);
    }

    public void setThreadName_MatchPhrase(String threadName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("threadName", threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_MatchPhrasePrefix(String threadName) {
        setThreadName_MatchPhrasePrefix(threadName, null);
    }

    public void setThreadName_MatchPhrasePrefix(String threadName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("threadName", threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_Fuzzy(String threadName) {
        setThreadName_Fuzzy(threadName, null);
    }

    public void setThreadName_Fuzzy(String threadName, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("threadName", threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_Prefix(String threadName) {
        setThreadName_Prefix(threadName, null);
    }

    public void setThreadName_Prefix(String threadName, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("threadName", threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_GreaterThan(String threadName) {
        setThreadName_GreaterThan(threadName, null);
    }

    public void setThreadName_GreaterThan(String threadName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("threadName", ConditionKey.CK_GREATER_THAN, threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_LessThan(String threadName) {
        setThreadName_LessThan(threadName, null);
    }

    public void setThreadName_LessThan(String threadName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("threadName", ConditionKey.CK_LESS_THAN, threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_GreaterEqual(String threadName) {
        setThreadName_GreaterEqual(threadName, null);
    }

    public void setThreadName_GreaterEqual(String threadName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("threadName", ConditionKey.CK_GREATER_EQUAL, threadName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_LessEqual(String threadName) {
        setThreadName_LessEqual(threadName, null);
    }

    public void setThreadName_LessEqual(String threadName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("threadName", ConditionKey.CK_LESS_EQUAL, threadName);
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

    public void setUrl_Term(String url) {
        setUrl_Term(url, null);
    }

    public void setUrl_Term(String url, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Terms(Collection<String> urlList) {
        setUrl_MatchPhrasePrefix(urlList, null);
    }

    public void setUrl_MatchPhrasePrefix(Collection<String> urlList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("url", urlList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_InScope(Collection<String> urlList) {
        setUrl_MatchPhrasePrefix(urlList, null);
    }

    public void setUrl_InScope(Collection<String> urlList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUrl_MatchPhrasePrefix(urlList, opLambda);
    }

    public void setUrl_Match(String url) {
        setUrl_Match(url, null);
    }

    public void setUrl_Match(String url, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_MatchPhrase(String url) {
        setUrl_MatchPhrase(url, null);
    }

    public void setUrl_MatchPhrase(String url, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_MatchPhrasePrefix(String url) {
        setUrl_MatchPhrasePrefix(url, null);
    }

    public void setUrl_MatchPhrasePrefix(String url, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Fuzzy(String url) {
        setUrl_Fuzzy(url, null);
    }

    public void setUrl_Fuzzy(String url, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Prefix(String url) {
        setUrl_Prefix(url, null);
    }

    public void setUrl_Prefix(String url, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_GreaterThan(String url) {
        setUrl_GreaterThan(url, null);
    }

    public void setUrl_GreaterThan(String url, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("url", ConditionKey.CK_GREATER_THAN, url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_LessThan(String url) {
        setUrl_LessThan(url, null);
    }

    public void setUrl_LessThan(String url, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("url", ConditionKey.CK_LESS_THAN, url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_GreaterEqual(String url) {
        setUrl_GreaterEqual(url, null);
    }

    public void setUrl_GreaterEqual(String url, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("url", ConditionKey.CK_GREATER_EQUAL, url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_LessEqual(String url) {
        setUrl_LessEqual(url, null);
    }

    public void setUrl_LessEqual(String url, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("url", ConditionKey.CK_LESS_EQUAL, url);
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
