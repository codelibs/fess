package org.codelibs.fess.es.cbean.cq.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cq.CrawlingSessionCQ;
import org.codelibs.fess.es.cbean.cf.CrawlingSessionCF;
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
public abstract class BsCrawlingSessionCQ extends AbstractConditionQuery {

    @Override
    public String asTableDbName() {
        return "crawling_session";
    }

    @Override
    public String xgetAliasName() {
        return "crawling_session";
    }

    public void filtered(FilteredCall<CrawlingSessionCQ, CrawlingSessionCF> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<CrawlingSessionCQ, CrawlingSessionCF> filteredLambda,
            ConditionOptionCall<FilteredQueryBuilder> opLambda) {
        CrawlingSessionCQ query = new CrawlingSessionCQ();
        CrawlingSessionCF filter = new CrawlingSessionCF();
        filteredLambda.callback(query, filter);
        if (query.hasQueries()) {
            FilteredQueryBuilder builder = regFilteredQ(query.getQuery(), filter.getFilter());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void bool(BoolCall<CrawlingSessionCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<CrawlingSessionCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        CrawlingSessionCQ mustQuery = new CrawlingSessionCQ();
        CrawlingSessionCQ shouldQuery = new CrawlingSessionCQ();
        CrawlingSessionCQ mustNotQuery = new CrawlingSessionCQ();
        boolLambda.callback(mustQuery, shouldQuery, mustNotQuery);
        if (mustQuery.hasQueries() || shouldQuery.hasQueries() || mustNotQuery.hasQueries()) {
            BoolQueryBuilder builder = regBoolCQ(mustQuery.queryBuilderList, shouldQuery.queryBuilderList, mustNotQuery.queryBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
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

    public BsCrawlingSessionCQ addOrderBy_CreatedTime_Asc() {
        regOBA("createdTime");
        return this;
    }

    public BsCrawlingSessionCQ addOrderBy_CreatedTime_Desc() {
        regOBD("createdTime");
        return this;
    }

    public void setExpiredTime_Equal(Long expiredTime) {
        setExpiredTime_Term(expiredTime, null);
    }

    public void setExpiredTime_Equal(Long expiredTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setExpiredTime_Term(expiredTime, opLambda);
    }

    public void setExpiredTime_Term(Long expiredTime) {
        setExpiredTime_Term(expiredTime, null);
    }

    public void setExpiredTime_Term(Long expiredTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("expiredTime", expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_Terms(Collection<Long> expiredTimeList) {
        setExpiredTime_Terms(expiredTimeList, null);
    }

    public void setExpiredTime_Terms(Collection<Long> expiredTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("expiredTime", expiredTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_InScope(Collection<Long> expiredTimeList) {
        setExpiredTime_Terms(expiredTimeList, null);
    }

    public void setExpiredTime_InScope(Collection<Long> expiredTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setExpiredTime_Terms(expiredTimeList, opLambda);
    }

    public void setExpiredTime_Match(Long expiredTime) {
        setExpiredTime_Match(expiredTime, null);
    }

    public void setExpiredTime_Match(Long expiredTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("expiredTime", expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_MatchPhrase(Long expiredTime) {
        setExpiredTime_MatchPhrase(expiredTime, null);
    }

    public void setExpiredTime_MatchPhrase(Long expiredTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("expiredTime", expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_MatchPhrasePrefix(Long expiredTime) {
        setExpiredTime_MatchPhrasePrefix(expiredTime, null);
    }

    public void setExpiredTime_MatchPhrasePrefix(Long expiredTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("expiredTime", expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_Fuzzy(Long expiredTime) {
        setExpiredTime_Fuzzy(expiredTime, null);
    }

    public void setExpiredTime_Fuzzy(Long expiredTime, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("expiredTime", expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_GreaterThan(Long expiredTime) {
        setExpiredTime_GreaterThan(expiredTime, null);
    }

    public void setExpiredTime_GreaterThan(Long expiredTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("expiredTime", ConditionKey.CK_GREATER_THAN, expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_LessThan(Long expiredTime) {
        setExpiredTime_LessThan(expiredTime, null);
    }

    public void setExpiredTime_LessThan(Long expiredTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("expiredTime", ConditionKey.CK_LESS_THAN, expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_GreaterEqual(Long expiredTime) {
        setExpiredTime_GreaterEqual(expiredTime, null);
    }

    public void setExpiredTime_GreaterEqual(Long expiredTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("expiredTime", ConditionKey.CK_GREATER_EQUAL, expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_LessEqual(Long expiredTime) {
        setExpiredTime_LessEqual(expiredTime, null);
    }

    public void setExpiredTime_LessEqual(Long expiredTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("expiredTime", ConditionKey.CK_LESS_EQUAL, expiredTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsCrawlingSessionCQ addOrderBy_ExpiredTime_Asc() {
        regOBA("expiredTime");
        return this;
    }

    public BsCrawlingSessionCQ addOrderBy_ExpiredTime_Desc() {
        regOBD("expiredTime");
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

    public BsCrawlingSessionCQ addOrderBy_Id_Asc() {
        regOBA("id");
        return this;
    }

    public BsCrawlingSessionCQ addOrderBy_Id_Desc() {
        regOBD("id");
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

    public void setName_MatchPhrase(String name, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_MatchPhrasePrefix(String name) {
        setName_MatchPhrasePrefix(name, null);
    }

    public void setName_MatchPhrasePrefix(String name, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Fuzzy(String name) {
        setName_Fuzzy(name, null);
    }

    public void setName_Fuzzy(String name, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("name", name);
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

    public void setName_GreaterThan(String name) {
        setName_GreaterThan(name, null);
    }

    public void setName_GreaterThan(String name, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("name", ConditionKey.CK_GREATER_THAN, name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_LessThan(String name) {
        setName_LessThan(name, null);
    }

    public void setName_LessThan(String name, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("name", ConditionKey.CK_LESS_THAN, name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_GreaterEqual(String name) {
        setName_GreaterEqual(name, null);
    }

    public void setName_GreaterEqual(String name, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("name", ConditionKey.CK_GREATER_EQUAL, name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_LessEqual(String name) {
        setName_LessEqual(name, null);
    }

    public void setName_LessEqual(String name, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("name", ConditionKey.CK_LESS_EQUAL, name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsCrawlingSessionCQ addOrderBy_Name_Asc() {
        regOBA("name");
        return this;
    }

    public BsCrawlingSessionCQ addOrderBy_Name_Desc() {
        regOBD("name");
        return this;
    }

    public void setSessionId_Equal(String sessionId) {
        setSessionId_Term(sessionId, null);
    }

    public void setSessionId_Equal(String sessionId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setSessionId_Term(sessionId, opLambda);
    }

    public void setSessionId_Term(String sessionId) {
        setSessionId_Term(sessionId, null);
    }

    public void setSessionId_Term(String sessionId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("sessionId", sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_Terms(Collection<String> sessionIdList) {
        setSessionId_Terms(sessionIdList, null);
    }

    public void setSessionId_Terms(Collection<String> sessionIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("sessionId", sessionIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_InScope(Collection<String> sessionIdList) {
        setSessionId_Terms(sessionIdList, null);
    }

    public void setSessionId_InScope(Collection<String> sessionIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setSessionId_Terms(sessionIdList, opLambda);
    }

    public void setSessionId_Match(String sessionId) {
        setSessionId_Match(sessionId, null);
    }

    public void setSessionId_Match(String sessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("sessionId", sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_MatchPhrase(String sessionId) {
        setSessionId_MatchPhrase(sessionId, null);
    }

    public void setSessionId_MatchPhrase(String sessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("sessionId", sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_MatchPhrasePrefix(String sessionId) {
        setSessionId_MatchPhrasePrefix(sessionId, null);
    }

    public void setSessionId_MatchPhrasePrefix(String sessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("sessionId", sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_Fuzzy(String sessionId) {
        setSessionId_Fuzzy(sessionId, null);
    }

    public void setSessionId_Fuzzy(String sessionId, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("sessionId", sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_Prefix(String sessionId) {
        setSessionId_Prefix(sessionId, null);
    }

    public void setSessionId_Prefix(String sessionId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("sessionId", sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_GreaterThan(String sessionId) {
        setSessionId_GreaterThan(sessionId, null);
    }

    public void setSessionId_GreaterThan(String sessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("sessionId", ConditionKey.CK_GREATER_THAN, sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_LessThan(String sessionId) {
        setSessionId_LessThan(sessionId, null);
    }

    public void setSessionId_LessThan(String sessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("sessionId", ConditionKey.CK_LESS_THAN, sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_GreaterEqual(String sessionId) {
        setSessionId_GreaterEqual(sessionId, null);
    }

    public void setSessionId_GreaterEqual(String sessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("sessionId", ConditionKey.CK_GREATER_EQUAL, sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_LessEqual(String sessionId) {
        setSessionId_LessEqual(sessionId, null);
    }

    public void setSessionId_LessEqual(String sessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("sessionId", ConditionKey.CK_LESS_EQUAL, sessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsCrawlingSessionCQ addOrderBy_SessionId_Asc() {
        regOBA("sessionId");
        return this;
    }

    public BsCrawlingSessionCQ addOrderBy_SessionId_Desc() {
        regOBD("sessionId");
        return this;
    }

}
