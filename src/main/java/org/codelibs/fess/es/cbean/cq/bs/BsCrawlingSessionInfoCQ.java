package org.codelibs.fess.es.cbean.cq.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cq.CrawlingSessionInfoCQ;
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
public abstract class BsCrawlingSessionInfoCQ extends AbstractConditionQuery {

    @Override
    public String asTableDbName() {
        return "crawling_session_info";
    }

    @Override
    public String xgetAliasName() {
        return "crawling_session_info";
    }

    public void filtered(FilteredCall<CrawlingSessionInfoCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<CrawlingSessionInfoCQ> filteredLambda, ConditionOptionCall<FilteredQueryBuilder> opLambda) {
        CrawlingSessionInfoCQ query = new CrawlingSessionInfoCQ();
        filteredLambda.callback(query);
        if (!query.queryBuilderList.isEmpty()) {
            // TODO filter
            FilteredQueryBuilder builder = reqFilteredQ(query.getQuery(), null);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void bool(BoolCall<CrawlingSessionInfoCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<CrawlingSessionInfoCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        CrawlingSessionInfoCQ mustQuery = new CrawlingSessionInfoCQ();
        CrawlingSessionInfoCQ shouldQuery = new CrawlingSessionInfoCQ();
        CrawlingSessionInfoCQ mustNotQuery = new CrawlingSessionInfoCQ();
        boolLambda.callback(mustQuery, shouldQuery, mustNotQuery);
        if (!mustQuery.queryBuilderList.isEmpty() || !shouldQuery.queryBuilderList.isEmpty() || !mustNotQuery.queryBuilderList.isEmpty()) {
            BoolQueryBuilder builder = reqBoolCQ(mustQuery.queryBuilderList, shouldQuery.queryBuilderList, mustNotQuery.queryBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void setCrawlingSessionId_Term(String crawlingSessionId) {
        setCrawlingSessionId_Term(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_Term(String crawlingSessionId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("crawlingSessionId", crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_Terms(Collection<String> crawlingSessionIdList) {
        setCrawlingSessionId_MatchPhrasePrefix(crawlingSessionIdList, null);
    }

    public void setCrawlingSessionId_MatchPhrasePrefix(Collection<String> crawlingSessionIdList,
            ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("crawlingSessionId", crawlingSessionIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_InScope(Collection<String> crawlingSessionIdList) {
        setCrawlingSessionId_MatchPhrasePrefix(crawlingSessionIdList, null);
    }

    public void setCrawlingSessionId_InScope(Collection<String> crawlingSessionIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setCrawlingSessionId_MatchPhrasePrefix(crawlingSessionIdList, opLambda);
    }

    public void setCrawlingSessionId_Match(String crawlingSessionId) {
        setCrawlingSessionId_Match(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_Match(String crawlingSessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("crawlingSessionId", crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_MatchPhrase(String crawlingSessionId) {
        setCrawlingSessionId_MatchPhrase(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_MatchPhrase(String crawlingSessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("crawlingSessionId", crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_MatchPhrasePrefix(String crawlingSessionId) {
        setCrawlingSessionId_MatchPhrasePrefix(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_MatchPhrasePrefix(String crawlingSessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("crawlingSessionId", crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_Fuzzy(String crawlingSessionId) {
        setCrawlingSessionId_Fuzzy(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_Fuzzy(String crawlingSessionId, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("crawlingSessionId", crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_Prefix(String crawlingSessionId) {
        setCrawlingSessionId_Prefix(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_Prefix(String crawlingSessionId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("crawlingSessionId", crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_GreaterThan(String crawlingSessionId) {
        setCrawlingSessionId_GreaterThan(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_GreaterThan(String crawlingSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("crawlingSessionId", ConditionKey.CK_GREATER_THAN, crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_LessThan(String crawlingSessionId) {
        setCrawlingSessionId_LessThan(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_LessThan(String crawlingSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("crawlingSessionId", ConditionKey.CK_LESS_THAN, crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_GreaterEqual(String crawlingSessionId) {
        setCrawlingSessionId_GreaterEqual(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_GreaterEqual(String crawlingSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("crawlingSessionId", ConditionKey.CK_GREATER_EQUAL, crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_LessEqual(String crawlingSessionId) {
        setCrawlingSessionId_LessEqual(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_LessEqual(String crawlingSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("crawlingSessionId", ConditionKey.CK_LESS_EQUAL, crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsCrawlingSessionInfoCQ addOrderBy_CrawlingSessionId_Asc() {
        regOBA("crawlingSessionId");
        return this;
    }

    public BsCrawlingSessionInfoCQ addOrderBy_CrawlingSessionId_Desc() {
        regOBD("crawlingSessionId");
        return this;
    }

    public void setCreatedTime_Term(Long createdTime) {
        setCreatedTime_Term(createdTime, null);
    }

    public void setCreatedTime_Term(Long createdTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Terms(Collection<Long> createdTimeList) {
        setCreatedTime_MatchPhrasePrefix(createdTimeList, null);
    }

    public void setCreatedTime_MatchPhrasePrefix(Collection<Long> createdTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("createdTime", createdTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_InScope(Collection<Long> createdTimeList) {
        setCreatedTime_MatchPhrasePrefix(createdTimeList, null);
    }

    public void setCreatedTime_InScope(Collection<Long> createdTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setCreatedTime_MatchPhrasePrefix(createdTimeList, opLambda);
    }

    public void setCreatedTime_Match(Long createdTime) {
        setCreatedTime_Match(createdTime, null);
    }

    public void setCreatedTime_Match(Long createdTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_MatchPhrase(Long createdTime) {
        setCreatedTime_MatchPhrase(createdTime, null);
    }

    public void setCreatedTime_MatchPhrase(Long createdTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_MatchPhrasePrefix(Long createdTime) {
        setCreatedTime_MatchPhrasePrefix(createdTime, null);
    }

    public void setCreatedTime_MatchPhrasePrefix(Long createdTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Fuzzy(Long createdTime) {
        setCreatedTime_Fuzzy(createdTime, null);
    }

    public void setCreatedTime_Fuzzy(Long createdTime, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_GreaterThan(Long createdTime) {
        setCreatedTime_GreaterThan(createdTime, null);
    }

    public void setCreatedTime_GreaterThan(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("createdTime", ConditionKey.CK_GREATER_THAN, createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_LessThan(Long createdTime) {
        setCreatedTime_LessThan(createdTime, null);
    }

    public void setCreatedTime_LessThan(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("createdTime", ConditionKey.CK_LESS_THAN, createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_GreaterEqual(Long createdTime) {
        setCreatedTime_GreaterEqual(createdTime, null);
    }

    public void setCreatedTime_GreaterEqual(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("createdTime", ConditionKey.CK_GREATER_EQUAL, createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_LessEqual(Long createdTime) {
        setCreatedTime_LessEqual(createdTime, null);
    }

    public void setCreatedTime_LessEqual(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("createdTime", ConditionKey.CK_LESS_EQUAL, createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsCrawlingSessionInfoCQ addOrderBy_CreatedTime_Asc() {
        regOBA("createdTime");
        return this;
    }

    public BsCrawlingSessionInfoCQ addOrderBy_CreatedTime_Desc() {
        regOBD("createdTime");
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

    public BsCrawlingSessionInfoCQ addOrderBy_Id_Asc() {
        regOBA("id");
        return this;
    }

    public BsCrawlingSessionInfoCQ addOrderBy_Id_Desc() {
        regOBD("id");
        return this;
    }

    public void setKey_Term(String key) {
        setKey_Term(key, null);
    }

    public void setKey_Term(String key, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_Terms(Collection<String> keyList) {
        setKey_MatchPhrasePrefix(keyList, null);
    }

    public void setKey_MatchPhrasePrefix(Collection<String> keyList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("key", keyList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_InScope(Collection<String> keyList) {
        setKey_MatchPhrasePrefix(keyList, null);
    }

    public void setKey_InScope(Collection<String> keyList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setKey_MatchPhrasePrefix(keyList, opLambda);
    }

    public void setKey_Match(String key) {
        setKey_Match(key, null);
    }

    public void setKey_Match(String key, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_MatchPhrase(String key) {
        setKey_MatchPhrase(key, null);
    }

    public void setKey_MatchPhrase(String key, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_MatchPhrasePrefix(String key) {
        setKey_MatchPhrasePrefix(key, null);
    }

    public void setKey_MatchPhrasePrefix(String key, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_Fuzzy(String key) {
        setKey_Fuzzy(key, null);
    }

    public void setKey_Fuzzy(String key, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_Prefix(String key) {
        setKey_Prefix(key, null);
    }

    public void setKey_Prefix(String key, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_GreaterThan(String key) {
        setKey_GreaterThan(key, null);
    }

    public void setKey_GreaterThan(String key, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("key", ConditionKey.CK_GREATER_THAN, key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_LessThan(String key) {
        setKey_LessThan(key, null);
    }

    public void setKey_LessThan(String key, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("key", ConditionKey.CK_LESS_THAN, key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_GreaterEqual(String key) {
        setKey_GreaterEqual(key, null);
    }

    public void setKey_GreaterEqual(String key, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("key", ConditionKey.CK_GREATER_EQUAL, key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_LessEqual(String key) {
        setKey_LessEqual(key, null);
    }

    public void setKey_LessEqual(String key, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("key", ConditionKey.CK_LESS_EQUAL, key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsCrawlingSessionInfoCQ addOrderBy_Key_Asc() {
        regOBA("key");
        return this;
    }

    public BsCrawlingSessionInfoCQ addOrderBy_Key_Desc() {
        regOBD("key");
        return this;
    }

    public void setValue_Term(String value) {
        setValue_Term(value, null);
    }

    public void setValue_Term(String value, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_Terms(Collection<String> valueList) {
        setValue_MatchPhrasePrefix(valueList, null);
    }

    public void setValue_MatchPhrasePrefix(Collection<String> valueList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("value", valueList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_InScope(Collection<String> valueList) {
        setValue_MatchPhrasePrefix(valueList, null);
    }

    public void setValue_InScope(Collection<String> valueList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setValue_MatchPhrasePrefix(valueList, opLambda);
    }

    public void setValue_Match(String value) {
        setValue_Match(value, null);
    }

    public void setValue_Match(String value, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_MatchPhrase(String value) {
        setValue_MatchPhrase(value, null);
    }

    public void setValue_MatchPhrase(String value, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_MatchPhrasePrefix(String value) {
        setValue_MatchPhrasePrefix(value, null);
    }

    public void setValue_MatchPhrasePrefix(String value, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_Fuzzy(String value) {
        setValue_Fuzzy(value, null);
    }

    public void setValue_Fuzzy(String value, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_Prefix(String value) {
        setValue_Prefix(value, null);
    }

    public void setValue_Prefix(String value, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_GreaterThan(String value) {
        setValue_GreaterThan(value, null);
    }

    public void setValue_GreaterThan(String value, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("value", ConditionKey.CK_GREATER_THAN, value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_LessThan(String value) {
        setValue_LessThan(value, null);
    }

    public void setValue_LessThan(String value, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("value", ConditionKey.CK_LESS_THAN, value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_GreaterEqual(String value) {
        setValue_GreaterEqual(value, null);
    }

    public void setValue_GreaterEqual(String value, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("value", ConditionKey.CK_GREATER_EQUAL, value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_LessEqual(String value) {
        setValue_LessEqual(value, null);
    }

    public void setValue_LessEqual(String value, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("value", ConditionKey.CK_LESS_EQUAL, value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsCrawlingSessionInfoCQ addOrderBy_Value_Asc() {
        regOBA("value");
        return this;
    }

    public BsCrawlingSessionInfoCQ addOrderBy_Value_Desc() {
        regOBD("value");
        return this;
    }

}
