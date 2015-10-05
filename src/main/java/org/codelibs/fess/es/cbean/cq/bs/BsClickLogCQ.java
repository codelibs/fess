package org.codelibs.fess.es.cbean.cq.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cf.ClickLogCF;
import org.codelibs.fess.es.cbean.cq.ClickLogCQ;
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
public abstract class BsClickLogCQ extends AbstractConditionQuery {

    @Override
    public String asTableDbName() {
        return "click_log";
    }

    @Override
    public String xgetAliasName() {
        return "click_log";
    }

    public void filtered(FilteredCall<ClickLogCQ, ClickLogCF> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<ClickLogCQ, ClickLogCF> filteredLambda, ConditionOptionCall<FilteredQueryBuilder> opLambda) {
        ClickLogCQ query = new ClickLogCQ();
        ClickLogCF filter = new ClickLogCF();
        filteredLambda.callback(query, filter);
        if (query.hasQueries()) {
            FilteredQueryBuilder builder = regFilteredQ(query.getQuery(), filter.getFilter());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void bool(BoolCall<ClickLogCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<ClickLogCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        ClickLogCQ mustQuery = new ClickLogCQ();
        ClickLogCQ shouldQuery = new ClickLogCQ();
        ClickLogCQ mustNotQuery = new ClickLogCQ();
        boolLambda.callback(mustQuery, shouldQuery, mustNotQuery);
        if (mustQuery.hasQueries() || shouldQuery.hasQueries() || mustNotQuery.hasQueries()) {
            BoolQueryBuilder builder = regBoolCQ(mustQuery.queryBuilderList, shouldQuery.queryBuilderList, mustNotQuery.queryBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
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

    public BsClickLogCQ addOrderBy_Id_Asc() {
        regOBA("id");
        return this;
    }

    public BsClickLogCQ addOrderBy_Id_Desc() {
        regOBD("id");
        return this;
    }

    public void setRequestedTime_Equal(Long requestedTime) {
        setRequestedTime_Term(requestedTime, null);
    }

    public void setRequestedTime_Equal(Long requestedTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setRequestedTime_Term(requestedTime, opLambda);
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

    public BsClickLogCQ addOrderBy_RequestedTime_Asc() {
        regOBA("requestedTime");
        return this;
    }

    public BsClickLogCQ addOrderBy_RequestedTime_Desc() {
        regOBD("requestedTime");
        return this;
    }

    public void setSearchLogId_Equal(String searchLogId) {
        setSearchLogId_Term(searchLogId, null);
    }

    public void setSearchLogId_Equal(String searchLogId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setSearchLogId_Term(searchLogId, opLambda);
    }

    public void setSearchLogId_Term(String searchLogId) {
        setSearchLogId_Term(searchLogId, null);
    }

    public void setSearchLogId_Term(String searchLogId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("searchLogId", searchLogId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchLogId_Terms(Collection<String> searchLogIdList) {
        setSearchLogId_Terms(searchLogIdList, null);
    }

    public void setSearchLogId_Terms(Collection<String> searchLogIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("searchLogId", searchLogIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchLogId_InScope(Collection<String> searchLogIdList) {
        setSearchLogId_Terms(searchLogIdList, null);
    }

    public void setSearchLogId_InScope(Collection<String> searchLogIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setSearchLogId_Terms(searchLogIdList, opLambda);
    }

    public void setSearchLogId_Match(String searchLogId) {
        setSearchLogId_Match(searchLogId, null);
    }

    public void setSearchLogId_Match(String searchLogId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("searchLogId", searchLogId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchLogId_MatchPhrase(String searchLogId) {
        setSearchLogId_MatchPhrase(searchLogId, null);
    }

    public void setSearchLogId_MatchPhrase(String searchLogId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("searchLogId", searchLogId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchLogId_MatchPhrasePrefix(String searchLogId) {
        setSearchLogId_MatchPhrasePrefix(searchLogId, null);
    }

    public void setSearchLogId_MatchPhrasePrefix(String searchLogId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("searchLogId", searchLogId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchLogId_Fuzzy(String searchLogId) {
        setSearchLogId_Fuzzy(searchLogId, null);
    }

    public void setSearchLogId_Fuzzy(String searchLogId, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("searchLogId", searchLogId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchLogId_Prefix(String searchLogId) {
        setSearchLogId_Prefix(searchLogId, null);
    }

    public void setSearchLogId_Prefix(String searchLogId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("searchLogId", searchLogId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchLogId_GreaterThan(String searchLogId) {
        setSearchLogId_GreaterThan(searchLogId, null);
    }

    public void setSearchLogId_GreaterThan(String searchLogId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("searchLogId", ConditionKey.CK_GREATER_THAN, searchLogId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchLogId_LessThan(String searchLogId) {
        setSearchLogId_LessThan(searchLogId, null);
    }

    public void setSearchLogId_LessThan(String searchLogId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("searchLogId", ConditionKey.CK_LESS_THAN, searchLogId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchLogId_GreaterEqual(String searchLogId) {
        setSearchLogId_GreaterEqual(searchLogId, null);
    }

    public void setSearchLogId_GreaterEqual(String searchLogId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("searchLogId", ConditionKey.CK_GREATER_EQUAL, searchLogId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchLogId_LessEqual(String searchLogId) {
        setSearchLogId_LessEqual(searchLogId, null);
    }

    public void setSearchLogId_LessEqual(String searchLogId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("searchLogId", ConditionKey.CK_LESS_EQUAL, searchLogId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsClickLogCQ addOrderBy_SearchLogId_Asc() {
        regOBA("searchLogId");
        return this;
    }

    public BsClickLogCQ addOrderBy_SearchLogId_Desc() {
        regOBD("searchLogId");
        return this;
    }

    public void setUrl_Equal(String url) {
        setUrl_Term(url, null);
    }

    public void setUrl_Equal(String url, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUrl_Term(url, opLambda);
    }

    public void setUrl_Term(String url) {
        setUrl_Term(url, null);
    }

    public void setUrl_Term(String url, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Terms(Collection<String> urlList) {
        setUrl_Terms(urlList, null);
    }

    public void setUrl_Terms(Collection<String> urlList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("url", urlList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_InScope(Collection<String> urlList) {
        setUrl_Terms(urlList, null);
    }

    public void setUrl_InScope(Collection<String> urlList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUrl_Terms(urlList, opLambda);
    }

    public void setUrl_Match(String url) {
        setUrl_Match(url, null);
    }

    public void setUrl_Match(String url, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_MatchPhrase(String url) {
        setUrl_MatchPhrase(url, null);
    }

    public void setUrl_MatchPhrase(String url, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_MatchPhrasePrefix(String url) {
        setUrl_MatchPhrasePrefix(url, null);
    }

    public void setUrl_MatchPhrasePrefix(String url, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Fuzzy(String url) {
        setUrl_Fuzzy(url, null);
    }

    public void setUrl_Fuzzy(String url, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Prefix(String url) {
        setUrl_Prefix(url, null);
    }

    public void setUrl_Prefix(String url, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_GreaterThan(String url) {
        setUrl_GreaterThan(url, null);
    }

    public void setUrl_GreaterThan(String url, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("url", ConditionKey.CK_GREATER_THAN, url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_LessThan(String url) {
        setUrl_LessThan(url, null);
    }

    public void setUrl_LessThan(String url, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("url", ConditionKey.CK_LESS_THAN, url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_GreaterEqual(String url) {
        setUrl_GreaterEqual(url, null);
    }

    public void setUrl_GreaterEqual(String url, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("url", ConditionKey.CK_GREATER_EQUAL, url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_LessEqual(String url) {
        setUrl_LessEqual(url, null);
    }

    public void setUrl_LessEqual(String url, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("url", ConditionKey.CK_LESS_EQUAL, url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsClickLogCQ addOrderBy_Url_Asc() {
        regOBA("url");
        return this;
    }

    public BsClickLogCQ addOrderBy_Url_Desc() {
        regOBD("url");
        return this;
    }

}
