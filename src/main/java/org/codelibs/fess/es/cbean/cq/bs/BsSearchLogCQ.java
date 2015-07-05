package org.codelibs.fess.es.cbean.cq.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cq.SearchLogCQ;
import org.codelibs.fess.es.cbean.cf.SearchLogCF;
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
public abstract class BsSearchLogCQ extends AbstractConditionQuery {

    @Override
    public String asTableDbName() {
        return "search_log";
    }

    @Override
    public String xgetAliasName() {
        return "search_log";
    }

    public void filtered(FilteredCall<SearchLogCQ, SearchLogCF> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<SearchLogCQ, SearchLogCF> filteredLambda, ConditionOptionCall<FilteredQueryBuilder> opLambda) {
        SearchLogCQ query = new SearchLogCQ();
        SearchLogCF filter = new SearchLogCF();
        filteredLambda.callback(query, filter);
        if (query.hasQueries()) {
            FilteredQueryBuilder builder = regFilteredQ(query.getQuery(), filter.getFilter());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void bool(BoolCall<SearchLogCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<SearchLogCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        SearchLogCQ mustQuery = new SearchLogCQ();
        SearchLogCQ shouldQuery = new SearchLogCQ();
        SearchLogCQ mustNotQuery = new SearchLogCQ();
        boolLambda.callback(mustQuery, shouldQuery, mustNotQuery);
        if (mustQuery.hasQueries() || shouldQuery.hasQueries() || mustNotQuery.hasQueries()) {
            BoolQueryBuilder builder = regBoolCQ(mustQuery.queryBuilderList, shouldQuery.queryBuilderList, mustNotQuery.queryBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void setAccessType_Term(String accessType) {
        setAccessType_Term(accessType, null);
    }

    public void setAccessType_Term(String accessType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Terms(Collection<String> accessTypeList) {
        setAccessType_Terms(accessTypeList, null);
    }

    public void setAccessType_Terms(Collection<String> accessTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("accessType", accessTypeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_InScope(Collection<String> accessTypeList) {
        setAccessType_Terms(accessTypeList, null);
    }

    public void setAccessType_InScope(Collection<String> accessTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setAccessType_Terms(accessTypeList, opLambda);
    }

    public void setAccessType_Match(String accessType) {
        setAccessType_Match(accessType, null);
    }

    public void setAccessType_Match(String accessType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_MatchPhrase(String accessType) {
        setAccessType_MatchPhrase(accessType, null);
    }

    public void setAccessType_MatchPhrase(String accessType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_MatchPhrasePrefix(String accessType) {
        setAccessType_MatchPhrasePrefix(accessType, null);
    }

    public void setAccessType_MatchPhrasePrefix(String accessType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Fuzzy(String accessType) {
        setAccessType_Fuzzy(accessType, null);
    }

    public void setAccessType_Fuzzy(String accessType, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Prefix(String accessType) {
        setAccessType_Prefix(accessType, null);
    }

    public void setAccessType_Prefix(String accessType, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_GreaterThan(String accessType) {
        setAccessType_GreaterThan(accessType, null);
    }

    public void setAccessType_GreaterThan(String accessType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("accessType", ConditionKey.CK_GREATER_THAN, accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_LessThan(String accessType) {
        setAccessType_LessThan(accessType, null);
    }

    public void setAccessType_LessThan(String accessType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("accessType", ConditionKey.CK_LESS_THAN, accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_GreaterEqual(String accessType) {
        setAccessType_GreaterEqual(accessType, null);
    }

    public void setAccessType_GreaterEqual(String accessType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("accessType", ConditionKey.CK_GREATER_EQUAL, accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_LessEqual(String accessType) {
        setAccessType_LessEqual(accessType, null);
    }

    public void setAccessType_LessEqual(String accessType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("accessType", ConditionKey.CK_LESS_EQUAL, accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_AccessType_Asc() {
        regOBA("accessType");
        return this;
    }

    public BsSearchLogCQ addOrderBy_AccessType_Desc() {
        regOBD("accessType");
        return this;
    }

    public void setClientIp_Term(String clientIp) {
        setClientIp_Term(clientIp, null);
    }

    public void setClientIp_Term(String clientIp, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("clientIp", clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_Terms(Collection<String> clientIpList) {
        setClientIp_Terms(clientIpList, null);
    }

    public void setClientIp_Terms(Collection<String> clientIpList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("clientIp", clientIpList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_InScope(Collection<String> clientIpList) {
        setClientIp_Terms(clientIpList, null);
    }

    public void setClientIp_InScope(Collection<String> clientIpList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setClientIp_Terms(clientIpList, opLambda);
    }

    public void setClientIp_Match(String clientIp) {
        setClientIp_Match(clientIp, null);
    }

    public void setClientIp_Match(String clientIp, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("clientIp", clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_MatchPhrase(String clientIp) {
        setClientIp_MatchPhrase(clientIp, null);
    }

    public void setClientIp_MatchPhrase(String clientIp, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("clientIp", clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_MatchPhrasePrefix(String clientIp) {
        setClientIp_MatchPhrasePrefix(clientIp, null);
    }

    public void setClientIp_MatchPhrasePrefix(String clientIp, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("clientIp", clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_Fuzzy(String clientIp) {
        setClientIp_Fuzzy(clientIp, null);
    }

    public void setClientIp_Fuzzy(String clientIp, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("clientIp", clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_Prefix(String clientIp) {
        setClientIp_Prefix(clientIp, null);
    }

    public void setClientIp_Prefix(String clientIp, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("clientIp", clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_GreaterThan(String clientIp) {
        setClientIp_GreaterThan(clientIp, null);
    }

    public void setClientIp_GreaterThan(String clientIp, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("clientIp", ConditionKey.CK_GREATER_THAN, clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_LessThan(String clientIp) {
        setClientIp_LessThan(clientIp, null);
    }

    public void setClientIp_LessThan(String clientIp, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("clientIp", ConditionKey.CK_LESS_THAN, clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_GreaterEqual(String clientIp) {
        setClientIp_GreaterEqual(clientIp, null);
    }

    public void setClientIp_GreaterEqual(String clientIp, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("clientIp", ConditionKey.CK_GREATER_EQUAL, clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_LessEqual(String clientIp) {
        setClientIp_LessEqual(clientIp, null);
    }

    public void setClientIp_LessEqual(String clientIp, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("clientIp", ConditionKey.CK_LESS_EQUAL, clientIp);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_ClientIp_Asc() {
        regOBA("clientIp");
        return this;
    }

    public BsSearchLogCQ addOrderBy_ClientIp_Desc() {
        regOBD("clientIp");
        return this;
    }

    public void setHitCount_Term(Long hitCount) {
        setHitCount_Term(hitCount, null);
    }

    public void setHitCount_Term(Long hitCount, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("hitCount", hitCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Terms(Collection<Long> hitCountList) {
        setHitCount_Terms(hitCountList, null);
    }

    public void setHitCount_Terms(Collection<Long> hitCountList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("hitCount", hitCountList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_InScope(Collection<Long> hitCountList) {
        setHitCount_Terms(hitCountList, null);
    }

    public void setHitCount_InScope(Collection<Long> hitCountList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setHitCount_Terms(hitCountList, opLambda);
    }

    public void setHitCount_Match(Long hitCount) {
        setHitCount_Match(hitCount, null);
    }

    public void setHitCount_Match(Long hitCount, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("hitCount", hitCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_MatchPhrase(Long hitCount) {
        setHitCount_MatchPhrase(hitCount, null);
    }

    public void setHitCount_MatchPhrase(Long hitCount, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("hitCount", hitCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_MatchPhrasePrefix(Long hitCount) {
        setHitCount_MatchPhrasePrefix(hitCount, null);
    }

    public void setHitCount_MatchPhrasePrefix(Long hitCount, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("hitCount", hitCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Fuzzy(Long hitCount) {
        setHitCount_Fuzzy(hitCount, null);
    }

    public void setHitCount_Fuzzy(Long hitCount, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("hitCount", hitCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_GreaterThan(Long hitCount) {
        setHitCount_GreaterThan(hitCount, null);
    }

    public void setHitCount_GreaterThan(Long hitCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("hitCount", ConditionKey.CK_GREATER_THAN, hitCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_LessThan(Long hitCount) {
        setHitCount_LessThan(hitCount, null);
    }

    public void setHitCount_LessThan(Long hitCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("hitCount", ConditionKey.CK_LESS_THAN, hitCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_GreaterEqual(Long hitCount) {
        setHitCount_GreaterEqual(hitCount, null);
    }

    public void setHitCount_GreaterEqual(Long hitCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("hitCount", ConditionKey.CK_GREATER_EQUAL, hitCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_LessEqual(Long hitCount) {
        setHitCount_LessEqual(hitCount, null);
    }

    public void setHitCount_LessEqual(Long hitCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("hitCount", ConditionKey.CK_LESS_EQUAL, hitCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_HitCount_Asc() {
        regOBA("hitCount");
        return this;
    }

    public BsSearchLogCQ addOrderBy_HitCount_Desc() {
        regOBD("hitCount");
        return this;
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

    public BsSearchLogCQ addOrderBy_Id_Asc() {
        regOBA("id");
        return this;
    }

    public BsSearchLogCQ addOrderBy_Id_Desc() {
        regOBD("id");
        return this;
    }

    public void setQueryOffset_Term(Integer queryOffset) {
        setQueryOffset_Term(queryOffset, null);
    }

    public void setQueryOffset_Term(Integer queryOffset, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("queryOffset", queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Terms(Collection<Integer> queryOffsetList) {
        setQueryOffset_Terms(queryOffsetList, null);
    }

    public void setQueryOffset_Terms(Collection<Integer> queryOffsetList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("queryOffset", queryOffsetList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_InScope(Collection<Integer> queryOffsetList) {
        setQueryOffset_Terms(queryOffsetList, null);
    }

    public void setQueryOffset_InScope(Collection<Integer> queryOffsetList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setQueryOffset_Terms(queryOffsetList, opLambda);
    }

    public void setQueryOffset_Match(Integer queryOffset) {
        setQueryOffset_Match(queryOffset, null);
    }

    public void setQueryOffset_Match(Integer queryOffset, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("queryOffset", queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_MatchPhrase(Integer queryOffset) {
        setQueryOffset_MatchPhrase(queryOffset, null);
    }

    public void setQueryOffset_MatchPhrase(Integer queryOffset, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("queryOffset", queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_MatchPhrasePrefix(Integer queryOffset) {
        setQueryOffset_MatchPhrasePrefix(queryOffset, null);
    }

    public void setQueryOffset_MatchPhrasePrefix(Integer queryOffset, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("queryOffset", queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Fuzzy(Integer queryOffset) {
        setQueryOffset_Fuzzy(queryOffset, null);
    }

    public void setQueryOffset_Fuzzy(Integer queryOffset, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("queryOffset", queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_GreaterThan(Integer queryOffset) {
        setQueryOffset_GreaterThan(queryOffset, null);
    }

    public void setQueryOffset_GreaterThan(Integer queryOffset, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryOffset", ConditionKey.CK_GREATER_THAN, queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_LessThan(Integer queryOffset) {
        setQueryOffset_LessThan(queryOffset, null);
    }

    public void setQueryOffset_LessThan(Integer queryOffset, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryOffset", ConditionKey.CK_LESS_THAN, queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_GreaterEqual(Integer queryOffset) {
        setQueryOffset_GreaterEqual(queryOffset, null);
    }

    public void setQueryOffset_GreaterEqual(Integer queryOffset, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryOffset", ConditionKey.CK_GREATER_EQUAL, queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_LessEqual(Integer queryOffset) {
        setQueryOffset_LessEqual(queryOffset, null);
    }

    public void setQueryOffset_LessEqual(Integer queryOffset, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryOffset", ConditionKey.CK_LESS_EQUAL, queryOffset);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_QueryOffset_Asc() {
        regOBA("queryOffset");
        return this;
    }

    public BsSearchLogCQ addOrderBy_QueryOffset_Desc() {
        regOBD("queryOffset");
        return this;
    }

    public void setQueryPageSize_Term(Integer queryPageSize) {
        setQueryPageSize_Term(queryPageSize, null);
    }

    public void setQueryPageSize_Term(Integer queryPageSize, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("queryPageSize", queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Terms(Collection<Integer> queryPageSizeList) {
        setQueryPageSize_Terms(queryPageSizeList, null);
    }

    public void setQueryPageSize_Terms(Collection<Integer> queryPageSizeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("queryPageSize", queryPageSizeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_InScope(Collection<Integer> queryPageSizeList) {
        setQueryPageSize_Terms(queryPageSizeList, null);
    }

    public void setQueryPageSize_InScope(Collection<Integer> queryPageSizeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setQueryPageSize_Terms(queryPageSizeList, opLambda);
    }

    public void setQueryPageSize_Match(Integer queryPageSize) {
        setQueryPageSize_Match(queryPageSize, null);
    }

    public void setQueryPageSize_Match(Integer queryPageSize, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("queryPageSize", queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_MatchPhrase(Integer queryPageSize) {
        setQueryPageSize_MatchPhrase(queryPageSize, null);
    }

    public void setQueryPageSize_MatchPhrase(Integer queryPageSize, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("queryPageSize", queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_MatchPhrasePrefix(Integer queryPageSize) {
        setQueryPageSize_MatchPhrasePrefix(queryPageSize, null);
    }

    public void setQueryPageSize_MatchPhrasePrefix(Integer queryPageSize, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("queryPageSize", queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Fuzzy(Integer queryPageSize) {
        setQueryPageSize_Fuzzy(queryPageSize, null);
    }

    public void setQueryPageSize_Fuzzy(Integer queryPageSize, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("queryPageSize", queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_GreaterThan(Integer queryPageSize) {
        setQueryPageSize_GreaterThan(queryPageSize, null);
    }

    public void setQueryPageSize_GreaterThan(Integer queryPageSize, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryPageSize", ConditionKey.CK_GREATER_THAN, queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_LessThan(Integer queryPageSize) {
        setQueryPageSize_LessThan(queryPageSize, null);
    }

    public void setQueryPageSize_LessThan(Integer queryPageSize, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryPageSize", ConditionKey.CK_LESS_THAN, queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_GreaterEqual(Integer queryPageSize) {
        setQueryPageSize_GreaterEqual(queryPageSize, null);
    }

    public void setQueryPageSize_GreaterEqual(Integer queryPageSize, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryPageSize", ConditionKey.CK_GREATER_EQUAL, queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_LessEqual(Integer queryPageSize) {
        setQueryPageSize_LessEqual(queryPageSize, null);
    }

    public void setQueryPageSize_LessEqual(Integer queryPageSize, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryPageSize", ConditionKey.CK_LESS_EQUAL, queryPageSize);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_QueryPageSize_Asc() {
        regOBA("queryPageSize");
        return this;
    }

    public BsSearchLogCQ addOrderBy_QueryPageSize_Desc() {
        regOBD("queryPageSize");
        return this;
    }

    public void setReferer_Term(String referer) {
        setReferer_Term(referer, null);
    }

    public void setReferer_Term(String referer, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("referer", referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_Terms(Collection<String> refererList) {
        setReferer_Terms(refererList, null);
    }

    public void setReferer_Terms(Collection<String> refererList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("referer", refererList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_InScope(Collection<String> refererList) {
        setReferer_Terms(refererList, null);
    }

    public void setReferer_InScope(Collection<String> refererList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setReferer_Terms(refererList, opLambda);
    }

    public void setReferer_Match(String referer) {
        setReferer_Match(referer, null);
    }

    public void setReferer_Match(String referer, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("referer", referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_MatchPhrase(String referer) {
        setReferer_MatchPhrase(referer, null);
    }

    public void setReferer_MatchPhrase(String referer, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("referer", referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_MatchPhrasePrefix(String referer) {
        setReferer_MatchPhrasePrefix(referer, null);
    }

    public void setReferer_MatchPhrasePrefix(String referer, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("referer", referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_Fuzzy(String referer) {
        setReferer_Fuzzy(referer, null);
    }

    public void setReferer_Fuzzy(String referer, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("referer", referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_Prefix(String referer) {
        setReferer_Prefix(referer, null);
    }

    public void setReferer_Prefix(String referer, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("referer", referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_GreaterThan(String referer) {
        setReferer_GreaterThan(referer, null);
    }

    public void setReferer_GreaterThan(String referer, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("referer", ConditionKey.CK_GREATER_THAN, referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_LessThan(String referer) {
        setReferer_LessThan(referer, null);
    }

    public void setReferer_LessThan(String referer, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("referer", ConditionKey.CK_LESS_THAN, referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_GreaterEqual(String referer) {
        setReferer_GreaterEqual(referer, null);
    }

    public void setReferer_GreaterEqual(String referer, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("referer", ConditionKey.CK_GREATER_EQUAL, referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_LessEqual(String referer) {
        setReferer_LessEqual(referer, null);
    }

    public void setReferer_LessEqual(String referer, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("referer", ConditionKey.CK_LESS_EQUAL, referer);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_Referer_Asc() {
        regOBA("referer");
        return this;
    }

    public BsSearchLogCQ addOrderBy_Referer_Desc() {
        regOBD("referer");
        return this;
    }

    public void setRequestedTime_Term(Long requestedTime) {
        setRequestedTime_Term(requestedTime, null);
    }

    public void setRequestedTime_Term(Long requestedTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("requestedTime", requestedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedTime_Terms(Collection<Long> requestedTimeList) {
        setRequestedTime_Terms(requestedTimeList, null);
    }

    public void setRequestedTime_Terms(Collection<Long> requestedTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("requestedTime", requestedTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedTime_InScope(Collection<Long> requestedTimeList) {
        setRequestedTime_Terms(requestedTimeList, null);
    }

    public void setRequestedTime_InScope(Collection<Long> requestedTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setRequestedTime_Terms(requestedTimeList, opLambda);
    }

    public void setRequestedTime_Match(Long requestedTime) {
        setRequestedTime_Match(requestedTime, null);
    }

    public void setRequestedTime_Match(Long requestedTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("requestedTime", requestedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedTime_MatchPhrase(Long requestedTime) {
        setRequestedTime_MatchPhrase(requestedTime, null);
    }

    public void setRequestedTime_MatchPhrase(Long requestedTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("requestedTime", requestedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedTime_MatchPhrasePrefix(Long requestedTime) {
        setRequestedTime_MatchPhrasePrefix(requestedTime, null);
    }

    public void setRequestedTime_MatchPhrasePrefix(Long requestedTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("requestedTime", requestedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedTime_Fuzzy(Long requestedTime) {
        setRequestedTime_Fuzzy(requestedTime, null);
    }

    public void setRequestedTime_Fuzzy(Long requestedTime, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("requestedTime", requestedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedTime_GreaterThan(Long requestedTime) {
        setRequestedTime_GreaterThan(requestedTime, null);
    }

    public void setRequestedTime_GreaterThan(Long requestedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("requestedTime", ConditionKey.CK_GREATER_THAN, requestedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedTime_LessThan(Long requestedTime) {
        setRequestedTime_LessThan(requestedTime, null);
    }

    public void setRequestedTime_LessThan(Long requestedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("requestedTime", ConditionKey.CK_LESS_THAN, requestedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedTime_GreaterEqual(Long requestedTime) {
        setRequestedTime_GreaterEqual(requestedTime, null);
    }

    public void setRequestedTime_GreaterEqual(Long requestedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("requestedTime", ConditionKey.CK_GREATER_EQUAL, requestedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedTime_LessEqual(Long requestedTime) {
        setRequestedTime_LessEqual(requestedTime, null);
    }

    public void setRequestedTime_LessEqual(Long requestedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("requestedTime", ConditionKey.CK_LESS_EQUAL, requestedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_RequestedTime_Asc() {
        regOBA("requestedTime");
        return this;
    }

    public BsSearchLogCQ addOrderBy_RequestedTime_Desc() {
        regOBD("requestedTime");
        return this;
    }

    public void setResponseTime_Term(Integer responseTime) {
        setResponseTime_Term(responseTime, null);
    }

    public void setResponseTime_Term(Integer responseTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Terms(Collection<Integer> responseTimeList) {
        setResponseTime_Terms(responseTimeList, null);
    }

    public void setResponseTime_Terms(Collection<Integer> responseTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("responseTime", responseTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_InScope(Collection<Integer> responseTimeList) {
        setResponseTime_Terms(responseTimeList, null);
    }

    public void setResponseTime_InScope(Collection<Integer> responseTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setResponseTime_Terms(responseTimeList, opLambda);
    }

    public void setResponseTime_Match(Integer responseTime) {
        setResponseTime_Match(responseTime, null);
    }

    public void setResponseTime_Match(Integer responseTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_MatchPhrase(Integer responseTime) {
        setResponseTime_MatchPhrase(responseTime, null);
    }

    public void setResponseTime_MatchPhrase(Integer responseTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_MatchPhrasePrefix(Integer responseTime) {
        setResponseTime_MatchPhrasePrefix(responseTime, null);
    }

    public void setResponseTime_MatchPhrasePrefix(Integer responseTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Fuzzy(Integer responseTime) {
        setResponseTime_Fuzzy(responseTime, null);
    }

    public void setResponseTime_Fuzzy(Integer responseTime, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_GreaterThan(Integer responseTime) {
        setResponseTime_GreaterThan(responseTime, null);
    }

    public void setResponseTime_GreaterThan(Integer responseTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("responseTime", ConditionKey.CK_GREATER_THAN, responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_LessThan(Integer responseTime) {
        setResponseTime_LessThan(responseTime, null);
    }

    public void setResponseTime_LessThan(Integer responseTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("responseTime", ConditionKey.CK_LESS_THAN, responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_GreaterEqual(Integer responseTime) {
        setResponseTime_GreaterEqual(responseTime, null);
    }

    public void setResponseTime_GreaterEqual(Integer responseTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("responseTime", ConditionKey.CK_GREATER_EQUAL, responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_LessEqual(Integer responseTime) {
        setResponseTime_LessEqual(responseTime, null);
    }

    public void setResponseTime_LessEqual(Integer responseTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("responseTime", ConditionKey.CK_LESS_EQUAL, responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_ResponseTime_Asc() {
        regOBA("responseTime");
        return this;
    }

    public BsSearchLogCQ addOrderBy_ResponseTime_Desc() {
        regOBD("responseTime");
        return this;
    }

    public void setSearchWord_Term(String searchWord) {
        setSearchWord_Term(searchWord, null);
    }

    public void setSearchWord_Term(String searchWord, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("searchWord", searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_Terms(Collection<String> searchWordList) {
        setSearchWord_Terms(searchWordList, null);
    }

    public void setSearchWord_Terms(Collection<String> searchWordList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("searchWord", searchWordList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_InScope(Collection<String> searchWordList) {
        setSearchWord_Terms(searchWordList, null);
    }

    public void setSearchWord_InScope(Collection<String> searchWordList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setSearchWord_Terms(searchWordList, opLambda);
    }

    public void setSearchWord_Match(String searchWord) {
        setSearchWord_Match(searchWord, null);
    }

    public void setSearchWord_Match(String searchWord, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("searchWord", searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_MatchPhrase(String searchWord) {
        setSearchWord_MatchPhrase(searchWord, null);
    }

    public void setSearchWord_MatchPhrase(String searchWord, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("searchWord", searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_MatchPhrasePrefix(String searchWord) {
        setSearchWord_MatchPhrasePrefix(searchWord, null);
    }

    public void setSearchWord_MatchPhrasePrefix(String searchWord, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("searchWord", searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_Fuzzy(String searchWord) {
        setSearchWord_Fuzzy(searchWord, null);
    }

    public void setSearchWord_Fuzzy(String searchWord, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("searchWord", searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_Prefix(String searchWord) {
        setSearchWord_Prefix(searchWord, null);
    }

    public void setSearchWord_Prefix(String searchWord, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("searchWord", searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_GreaterThan(String searchWord) {
        setSearchWord_GreaterThan(searchWord, null);
    }

    public void setSearchWord_GreaterThan(String searchWord, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("searchWord", ConditionKey.CK_GREATER_THAN, searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_LessThan(String searchWord) {
        setSearchWord_LessThan(searchWord, null);
    }

    public void setSearchWord_LessThan(String searchWord, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("searchWord", ConditionKey.CK_LESS_THAN, searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_GreaterEqual(String searchWord) {
        setSearchWord_GreaterEqual(searchWord, null);
    }

    public void setSearchWord_GreaterEqual(String searchWord, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("searchWord", ConditionKey.CK_GREATER_EQUAL, searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_LessEqual(String searchWord) {
        setSearchWord_LessEqual(searchWord, null);
    }

    public void setSearchWord_LessEqual(String searchWord, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("searchWord", ConditionKey.CK_LESS_EQUAL, searchWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_SearchWord_Asc() {
        regOBA("searchWord");
        return this;
    }

    public BsSearchLogCQ addOrderBy_SearchWord_Desc() {
        regOBD("searchWord");
        return this;
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

    public void setUserAgent_MatchPhrase(String userAgent, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("userAgent", userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_MatchPhrasePrefix(String userAgent) {
        setUserAgent_MatchPhrasePrefix(userAgent, null);
    }

    public void setUserAgent_MatchPhrasePrefix(String userAgent, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("userAgent", userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_Fuzzy(String userAgent) {
        setUserAgent_Fuzzy(userAgent, null);
    }

    public void setUserAgent_Fuzzy(String userAgent, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("userAgent", userAgent);
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

    public void setUserAgent_GreaterThan(String userAgent) {
        setUserAgent_GreaterThan(userAgent, null);
    }

    public void setUserAgent_GreaterThan(String userAgent, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("userAgent", ConditionKey.CK_GREATER_THAN, userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_LessThan(String userAgent) {
        setUserAgent_LessThan(userAgent, null);
    }

    public void setUserAgent_LessThan(String userAgent, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("userAgent", ConditionKey.CK_LESS_THAN, userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_GreaterEqual(String userAgent) {
        setUserAgent_GreaterEqual(userAgent, null);
    }

    public void setUserAgent_GreaterEqual(String userAgent, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("userAgent", ConditionKey.CK_GREATER_EQUAL, userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_LessEqual(String userAgent) {
        setUserAgent_LessEqual(userAgent, null);
    }

    public void setUserAgent_LessEqual(String userAgent, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("userAgent", ConditionKey.CK_LESS_EQUAL, userAgent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_UserAgent_Asc() {
        regOBA("userAgent");
        return this;
    }

    public BsSearchLogCQ addOrderBy_UserAgent_Desc() {
        regOBD("userAgent");
        return this;
    }

    public void setUserId_Term(Long userId) {
        setUserId_Term(userId, null);
    }

    public void setUserId_Term(Long userId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("userId", userId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserId_Terms(Collection<Long> userIdList) {
        setUserId_Terms(userIdList, null);
    }

    public void setUserId_Terms(Collection<Long> userIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("userId", userIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserId_InScope(Collection<Long> userIdList) {
        setUserId_Terms(userIdList, null);
    }

    public void setUserId_InScope(Collection<Long> userIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUserId_Terms(userIdList, opLambda);
    }

    public void setUserId_Match(Long userId) {
        setUserId_Match(userId, null);
    }

    public void setUserId_Match(Long userId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("userId", userId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserId_MatchPhrase(Long userId) {
        setUserId_MatchPhrase(userId, null);
    }

    public void setUserId_MatchPhrase(Long userId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("userId", userId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserId_MatchPhrasePrefix(Long userId) {
        setUserId_MatchPhrasePrefix(userId, null);
    }

    public void setUserId_MatchPhrasePrefix(Long userId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("userId", userId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserId_Fuzzy(Long userId) {
        setUserId_Fuzzy(userId, null);
    }

    public void setUserId_Fuzzy(Long userId, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("userId", userId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserId_GreaterThan(Long userId) {
        setUserId_GreaterThan(userId, null);
    }

    public void setUserId_GreaterThan(Long userId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("userId", ConditionKey.CK_GREATER_THAN, userId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserId_LessThan(Long userId) {
        setUserId_LessThan(userId, null);
    }

    public void setUserId_LessThan(Long userId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("userId", ConditionKey.CK_LESS_THAN, userId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserId_GreaterEqual(Long userId) {
        setUserId_GreaterEqual(userId, null);
    }

    public void setUserId_GreaterEqual(Long userId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("userId", ConditionKey.CK_GREATER_EQUAL, userId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserId_LessEqual(Long userId) {
        setUserId_LessEqual(userId, null);
    }

    public void setUserId_LessEqual(Long userId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("userId", ConditionKey.CK_LESS_EQUAL, userId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_UserId_Asc() {
        regOBA("userId");
        return this;
    }

    public BsSearchLogCQ addOrderBy_UserId_Desc() {
        regOBD("userId");
        return this;
    }

    public void setUserSessionId_Term(String userSessionId) {
        setUserSessionId_Term(userSessionId, null);
    }

    public void setUserSessionId_Term(String userSessionId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Terms(Collection<String> userSessionIdList) {
        setUserSessionId_Terms(userSessionIdList, null);
    }

    public void setUserSessionId_Terms(Collection<String> userSessionIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("userSessionId", userSessionIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_InScope(Collection<String> userSessionIdList) {
        setUserSessionId_Terms(userSessionIdList, null);
    }

    public void setUserSessionId_InScope(Collection<String> userSessionIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUserSessionId_Terms(userSessionIdList, opLambda);
    }

    public void setUserSessionId_Match(String userSessionId) {
        setUserSessionId_Match(userSessionId, null);
    }

    public void setUserSessionId_Match(String userSessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_MatchPhrase(String userSessionId) {
        setUserSessionId_MatchPhrase(userSessionId, null);
    }

    public void setUserSessionId_MatchPhrase(String userSessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_MatchPhrasePrefix(String userSessionId) {
        setUserSessionId_MatchPhrasePrefix(userSessionId, null);
    }

    public void setUserSessionId_MatchPhrasePrefix(String userSessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Fuzzy(String userSessionId) {
        setUserSessionId_Fuzzy(userSessionId, null);
    }

    public void setUserSessionId_Fuzzy(String userSessionId, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Prefix(String userSessionId) {
        setUserSessionId_Prefix(userSessionId, null);
    }

    public void setUserSessionId_Prefix(String userSessionId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_GreaterThan(String userSessionId) {
        setUserSessionId_GreaterThan(userSessionId, null);
    }

    public void setUserSessionId_GreaterThan(String userSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("userSessionId", ConditionKey.CK_GREATER_THAN, userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_LessThan(String userSessionId) {
        setUserSessionId_LessThan(userSessionId, null);
    }

    public void setUserSessionId_LessThan(String userSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("userSessionId", ConditionKey.CK_LESS_THAN, userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_GreaterEqual(String userSessionId) {
        setUserSessionId_GreaterEqual(userSessionId, null);
    }

    public void setUserSessionId_GreaterEqual(String userSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("userSessionId", ConditionKey.CK_GREATER_EQUAL, userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_LessEqual(String userSessionId) {
        setUserSessionId_LessEqual(userSessionId, null);
    }

    public void setUserSessionId_LessEqual(String userSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("userSessionId", ConditionKey.CK_LESS_EQUAL, userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_UserSessionId_Asc() {
        regOBA("userSessionId");
        return this;
    }

    public BsSearchLogCQ addOrderBy_UserSessionId_Desc() {
        regOBD("userSessionId");
        return this;
    }

}
