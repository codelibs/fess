package org.codelibs.fess.es.cbean.cq.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cq.CrawlingSessionInfoCQ;
import org.codelibs.fess.es.cbean.cf.CrawlingSessionInfoCF;
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

    public void filtered(FilteredCall<CrawlingSessionInfoCQ, CrawlingSessionInfoCF> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<CrawlingSessionInfoCQ, CrawlingSessionInfoCF> filteredLambda,
            ConditionOptionCall<FilteredQueryBuilder> opLambda) {
        CrawlingSessionInfoCQ query = new CrawlingSessionInfoCQ();
        CrawlingSessionInfoCF filter = new CrawlingSessionInfoCF();
        filteredLambda.callback(query, filter);
        if (query.hasQueries()) {
            FilteredQueryBuilder builder = regFilteredQ(query.getQuery(), filter.getFilter());
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
        if (mustQuery.hasQueries() || shouldQuery.hasQueries() || mustNotQuery.hasQueries()) {
            BoolQueryBuilder builder = regBoolCQ(mustQuery.queryBuilderList, shouldQuery.queryBuilderList, mustNotQuery.queryBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void setCrawlingSessionId_Equal(String crawlingSessionId) {
        setCrawlingSessionId_Term(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_Equal(String crawlingSessionId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setCrawlingSessionId_Term(crawlingSessionId, opLambda);
    }

    public void setCrawlingSessionId_Term(String crawlingSessionId) {
        setCrawlingSessionId_Term(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_Term(String crawlingSessionId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("crawlingSessionId", crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_Terms(Collection<String> crawlingSessionIdList) {
        setCrawlingSessionId_Terms(crawlingSessionIdList, null);
    }

    public void setCrawlingSessionId_Terms(Collection<String> crawlingSessionIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("crawlingSessionId", crawlingSessionIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_InScope(Collection<String> crawlingSessionIdList) {
        setCrawlingSessionId_Terms(crawlingSessionIdList, null);
    }

    public void setCrawlingSessionId_InScope(Collection<String> crawlingSessionIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setCrawlingSessionId_Terms(crawlingSessionIdList, opLambda);
    }

    public void setCrawlingSessionId_Match(String crawlingSessionId) {
        setCrawlingSessionId_Match(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_Match(String crawlingSessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("crawlingSessionId", crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_MatchPhrase(String crawlingSessionId) {
        setCrawlingSessionId_MatchPhrase(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_MatchPhrase(String crawlingSessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("crawlingSessionId", crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_MatchPhrasePrefix(String crawlingSessionId) {
        setCrawlingSessionId_MatchPhrasePrefix(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_MatchPhrasePrefix(String crawlingSessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("crawlingSessionId", crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_Fuzzy(String crawlingSessionId) {
        setCrawlingSessionId_Fuzzy(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_Fuzzy(String crawlingSessionId, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("crawlingSessionId", crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_Prefix(String crawlingSessionId) {
        setCrawlingSessionId_Prefix(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_Prefix(String crawlingSessionId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("crawlingSessionId", crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_GreaterThan(String crawlingSessionId) {
        setCrawlingSessionId_GreaterThan(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_GreaterThan(String crawlingSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("crawlingSessionId", ConditionKey.CK_GREATER_THAN, crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_LessThan(String crawlingSessionId) {
        setCrawlingSessionId_LessThan(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_LessThan(String crawlingSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("crawlingSessionId", ConditionKey.CK_LESS_THAN, crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_GreaterEqual(String crawlingSessionId) {
        setCrawlingSessionId_GreaterEqual(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_GreaterEqual(String crawlingSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("crawlingSessionId", ConditionKey.CK_GREATER_EQUAL, crawlingSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingSessionId_LessEqual(String crawlingSessionId) {
        setCrawlingSessionId_LessEqual(crawlingSessionId, null);
    }

    public void setCrawlingSessionId_LessEqual(String crawlingSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("crawlingSessionId", ConditionKey.CK_LESS_EQUAL, crawlingSessionId);
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

    public void setCreatedTime_MatchPhrase(Long createdTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_MatchPhrasePrefix(Long createdTime) {
        setCreatedTime_MatchPhrasePrefix(createdTime, null);
    }

    public void setCreatedTime_MatchPhrasePrefix(Long createdTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Fuzzy(Long createdTime) {
        setCreatedTime_Fuzzy(createdTime, null);
    }

    public void setCreatedTime_Fuzzy(Long createdTime, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_GreaterThan(Long createdTime) {
        setCreatedTime_GreaterThan(createdTime, null);
    }

    public void setCreatedTime_GreaterThan(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdTime", ConditionKey.CK_GREATER_THAN, createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_LessThan(Long createdTime) {
        setCreatedTime_LessThan(createdTime, null);
    }

    public void setCreatedTime_LessThan(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdTime", ConditionKey.CK_LESS_THAN, createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_GreaterEqual(Long createdTime) {
        setCreatedTime_GreaterEqual(createdTime, null);
    }

    public void setCreatedTime_GreaterEqual(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdTime", ConditionKey.CK_GREATER_EQUAL, createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_LessEqual(Long createdTime) {
        setCreatedTime_LessEqual(createdTime, null);
    }

    public void setCreatedTime_LessEqual(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdTime", ConditionKey.CK_LESS_EQUAL, createdTime);
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
        TermQueryBuilder builder = regTermQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Terms(Collection<String> idList) {
        setId_Terms(idList, null);
    }

    public void setId_Terms(Collection<String> idList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("id", idList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_InScope(Collection<String> idList) {
        setId_Terms(idList, null);
    }

    public void setId_InScope(Collection<String> idList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setId_Terms(idList, opLambda);
    }

    public void setId_Match(String id) {
        setId_Match(id, null);
    }

    public void setId_Match(String id, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_MatchPhrase(String id) {
        setId_MatchPhrase(id, null);
    }

    public void setId_MatchPhrase(String id, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_MatchPhrasePrefix(String id) {
        setId_MatchPhrasePrefix(id, null);
    }

    public void setId_MatchPhrasePrefix(String id, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Fuzzy(String id) {
        setId_Fuzzy(id, null);
    }

    public void setId_Fuzzy(String id, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Prefix(String id) {
        setId_Prefix(id, null);
    }

    public void setId_Prefix(String id, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_GreaterThan(String id) {
        setId_GreaterThan(id, null);
    }

    public void setId_GreaterThan(String id, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("id", ConditionKey.CK_GREATER_THAN, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_LessThan(String id) {
        setId_LessThan(id, null);
    }

    public void setId_LessThan(String id, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("id", ConditionKey.CK_LESS_THAN, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_GreaterEqual(String id) {
        setId_GreaterEqual(id, null);
    }

    public void setId_GreaterEqual(String id, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("id", ConditionKey.CK_GREATER_EQUAL, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_LessEqual(String id) {
        setId_LessEqual(id, null);
    }

    public void setId_LessEqual(String id, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("id", ConditionKey.CK_LESS_EQUAL, id);
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

    public void setKey_Equal(String key) {
        setKey_Term(key, null);
    }

    public void setKey_Equal(String key, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setKey_Term(key, opLambda);
    }

    public void setKey_Term(String key) {
        setKey_Term(key, null);
    }

    public void setKey_Term(String key, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_Terms(Collection<String> keyList) {
        setKey_Terms(keyList, null);
    }

    public void setKey_Terms(Collection<String> keyList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("key", keyList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_InScope(Collection<String> keyList) {
        setKey_Terms(keyList, null);
    }

    public void setKey_InScope(Collection<String> keyList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setKey_Terms(keyList, opLambda);
    }

    public void setKey_Match(String key) {
        setKey_Match(key, null);
    }

    public void setKey_Match(String key, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_MatchPhrase(String key) {
        setKey_MatchPhrase(key, null);
    }

    public void setKey_MatchPhrase(String key, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_MatchPhrasePrefix(String key) {
        setKey_MatchPhrasePrefix(key, null);
    }

    public void setKey_MatchPhrasePrefix(String key, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_Fuzzy(String key) {
        setKey_Fuzzy(key, null);
    }

    public void setKey_Fuzzy(String key, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_Prefix(String key) {
        setKey_Prefix(key, null);
    }

    public void setKey_Prefix(String key, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("key", key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_GreaterThan(String key) {
        setKey_GreaterThan(key, null);
    }

    public void setKey_GreaterThan(String key, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("key", ConditionKey.CK_GREATER_THAN, key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_LessThan(String key) {
        setKey_LessThan(key, null);
    }

    public void setKey_LessThan(String key, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("key", ConditionKey.CK_LESS_THAN, key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_GreaterEqual(String key) {
        setKey_GreaterEqual(key, null);
    }

    public void setKey_GreaterEqual(String key, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("key", ConditionKey.CK_GREATER_EQUAL, key);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_LessEqual(String key) {
        setKey_LessEqual(key, null);
    }

    public void setKey_LessEqual(String key, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("key", ConditionKey.CK_LESS_EQUAL, key);
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

    public void setValue_MatchPhrase(String value, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_MatchPhrasePrefix(String value) {
        setValue_MatchPhrasePrefix(value, null);
    }

    public void setValue_MatchPhrasePrefix(String value, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("value", value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_Fuzzy(String value) {
        setValue_Fuzzy(value, null);
    }

    public void setValue_Fuzzy(String value, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("value", value);
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

    public void setValue_GreaterThan(String value) {
        setValue_GreaterThan(value, null);
    }

    public void setValue_GreaterThan(String value, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("value", ConditionKey.CK_GREATER_THAN, value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_LessThan(String value) {
        setValue_LessThan(value, null);
    }

    public void setValue_LessThan(String value, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("value", ConditionKey.CK_LESS_THAN, value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_GreaterEqual(String value) {
        setValue_GreaterEqual(value, null);
    }

    public void setValue_GreaterEqual(String value, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("value", ConditionKey.CK_GREATER_EQUAL, value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_LessEqual(String value) {
        setValue_LessEqual(value, null);
    }

    public void setValue_LessEqual(String value, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("value", ConditionKey.CK_LESS_EQUAL, value);
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
