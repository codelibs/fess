package org.codelibs.fess.es.cbean.cq.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cf.WebAuthenticationCF;
import org.codelibs.fess.es.cbean.cq.WebAuthenticationCQ;
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
public abstract class BsWebAuthenticationCQ extends AbstractConditionQuery {

    @Override
    public String asTableDbName() {
        return "web_authentication";
    }

    @Override
    public String xgetAliasName() {
        return "web_authentication";
    }

    public void filtered(FilteredCall<WebAuthenticationCQ, WebAuthenticationCF> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<WebAuthenticationCQ, WebAuthenticationCF> filteredLambda,
            ConditionOptionCall<FilteredQueryBuilder> opLambda) {
        WebAuthenticationCQ query = new WebAuthenticationCQ();
        WebAuthenticationCF filter = new WebAuthenticationCF();
        filteredLambda.callback(query, filter);
        if (query.hasQueries()) {
            FilteredQueryBuilder builder = regFilteredQ(query.getQuery(), filter.getFilter());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void bool(BoolCall<WebAuthenticationCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<WebAuthenticationCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        WebAuthenticationCQ mustQuery = new WebAuthenticationCQ();
        WebAuthenticationCQ shouldQuery = new WebAuthenticationCQ();
        WebAuthenticationCQ mustNotQuery = new WebAuthenticationCQ();
        boolLambda.callback(mustQuery, shouldQuery, mustNotQuery);
        if (mustQuery.hasQueries() || shouldQuery.hasQueries() || mustNotQuery.hasQueries()) {
            BoolQueryBuilder builder = regBoolCQ(mustQuery.queryBuilderList, shouldQuery.queryBuilderList, mustNotQuery.queryBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void setAuthRealm_Equal(String authRealm) {
        setAuthRealm_Term(authRealm, null);
    }

    public void setAuthRealm_Equal(String authRealm, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setAuthRealm_Term(authRealm, opLambda);
    }

    public void setAuthRealm_Term(String authRealm) {
        setAuthRealm_Term(authRealm, null);
    }

    public void setAuthRealm_Term(String authRealm, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("authRealm", authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_Terms(Collection<String> authRealmList) {
        setAuthRealm_Terms(authRealmList, null);
    }

    public void setAuthRealm_Terms(Collection<String> authRealmList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("authRealm", authRealmList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_InScope(Collection<String> authRealmList) {
        setAuthRealm_Terms(authRealmList, null);
    }

    public void setAuthRealm_InScope(Collection<String> authRealmList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setAuthRealm_Terms(authRealmList, opLambda);
    }

    public void setAuthRealm_Match(String authRealm) {
        setAuthRealm_Match(authRealm, null);
    }

    public void setAuthRealm_Match(String authRealm, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("authRealm", authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_MatchPhrase(String authRealm) {
        setAuthRealm_MatchPhrase(authRealm, null);
    }

    public void setAuthRealm_MatchPhrase(String authRealm, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("authRealm", authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_MatchPhrasePrefix(String authRealm) {
        setAuthRealm_MatchPhrasePrefix(authRealm, null);
    }

    public void setAuthRealm_MatchPhrasePrefix(String authRealm, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("authRealm", authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_Fuzzy(String authRealm) {
        setAuthRealm_Fuzzy(authRealm, null);
    }

    public void setAuthRealm_Fuzzy(String authRealm, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("authRealm", authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_Prefix(String authRealm) {
        setAuthRealm_Prefix(authRealm, null);
    }

    public void setAuthRealm_Prefix(String authRealm, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("authRealm", authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_GreaterThan(String authRealm) {
        setAuthRealm_GreaterThan(authRealm, null);
    }

    public void setAuthRealm_GreaterThan(String authRealm, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("authRealm", ConditionKey.CK_GREATER_THAN, authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_LessThan(String authRealm) {
        setAuthRealm_LessThan(authRealm, null);
    }

    public void setAuthRealm_LessThan(String authRealm, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("authRealm", ConditionKey.CK_LESS_THAN, authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_GreaterEqual(String authRealm) {
        setAuthRealm_GreaterEqual(authRealm, null);
    }

    public void setAuthRealm_GreaterEqual(String authRealm, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("authRealm", ConditionKey.CK_GREATER_EQUAL, authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAuthRealm_LessEqual(String authRealm) {
        setAuthRealm_LessEqual(authRealm, null);
    }

    public void setAuthRealm_LessEqual(String authRealm, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("authRealm", ConditionKey.CK_LESS_EQUAL, authRealm);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebAuthenticationCQ addOrderBy_AuthRealm_Asc() {
        regOBA("authRealm");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_AuthRealm_Desc() {
        regOBD("authRealm");
        return this;
    }

    public void setCreatedBy_Equal(String createdBy) {
        setCreatedBy_Term(createdBy, null);
    }

    public void setCreatedBy_Equal(String createdBy, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setCreatedBy_Term(createdBy, opLambda);
    }

    public void setCreatedBy_Term(String createdBy) {
        setCreatedBy_Term(createdBy, null);
    }

    public void setCreatedBy_Term(String createdBy, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Terms(Collection<String> createdByList) {
        setCreatedBy_Terms(createdByList, null);
    }

    public void setCreatedBy_Terms(Collection<String> createdByList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("createdBy", createdByList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_InScope(Collection<String> createdByList) {
        setCreatedBy_Terms(createdByList, null);
    }

    public void setCreatedBy_InScope(Collection<String> createdByList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setCreatedBy_Terms(createdByList, opLambda);
    }

    public void setCreatedBy_Match(String createdBy) {
        setCreatedBy_Match(createdBy, null);
    }

    public void setCreatedBy_Match(String createdBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_MatchPhrase(String createdBy) {
        setCreatedBy_MatchPhrase(createdBy, null);
    }

    public void setCreatedBy_MatchPhrase(String createdBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_MatchPhrasePrefix(String createdBy) {
        setCreatedBy_MatchPhrasePrefix(createdBy, null);
    }

    public void setCreatedBy_MatchPhrasePrefix(String createdBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Fuzzy(String createdBy) {
        setCreatedBy_Fuzzy(createdBy, null);
    }

    public void setCreatedBy_Fuzzy(String createdBy, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Prefix(String createdBy) {
        setCreatedBy_Prefix(createdBy, null);
    }

    public void setCreatedBy_Prefix(String createdBy, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_GreaterThan(String createdBy) {
        setCreatedBy_GreaterThan(createdBy, null);
    }

    public void setCreatedBy_GreaterThan(String createdBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdBy", ConditionKey.CK_GREATER_THAN, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_LessThan(String createdBy) {
        setCreatedBy_LessThan(createdBy, null);
    }

    public void setCreatedBy_LessThan(String createdBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdBy", ConditionKey.CK_LESS_THAN, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_GreaterEqual(String createdBy) {
        setCreatedBy_GreaterEqual(createdBy, null);
    }

    public void setCreatedBy_GreaterEqual(String createdBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdBy", ConditionKey.CK_GREATER_EQUAL, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_LessEqual(String createdBy) {
        setCreatedBy_LessEqual(createdBy, null);
    }

    public void setCreatedBy_LessEqual(String createdBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdBy", ConditionKey.CK_LESS_EQUAL, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebAuthenticationCQ addOrderBy_CreatedBy_Asc() {
        regOBA("createdBy");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_CreatedBy_Desc() {
        regOBD("createdBy");
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

    public BsWebAuthenticationCQ addOrderBy_CreatedTime_Asc() {
        regOBA("createdTime");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_CreatedTime_Desc() {
        regOBD("createdTime");
        return this;
    }

    public void setHostname_Equal(String hostname) {
        setHostname_Term(hostname, null);
    }

    public void setHostname_Equal(String hostname, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setHostname_Term(hostname, opLambda);
    }

    public void setHostname_Term(String hostname) {
        setHostname_Term(hostname, null);
    }

    public void setHostname_Term(String hostname, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("hostname", hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_Terms(Collection<String> hostnameList) {
        setHostname_Terms(hostnameList, null);
    }

    public void setHostname_Terms(Collection<String> hostnameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("hostname", hostnameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_InScope(Collection<String> hostnameList) {
        setHostname_Terms(hostnameList, null);
    }

    public void setHostname_InScope(Collection<String> hostnameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setHostname_Terms(hostnameList, opLambda);
    }

    public void setHostname_Match(String hostname) {
        setHostname_Match(hostname, null);
    }

    public void setHostname_Match(String hostname, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("hostname", hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_MatchPhrase(String hostname) {
        setHostname_MatchPhrase(hostname, null);
    }

    public void setHostname_MatchPhrase(String hostname, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("hostname", hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_MatchPhrasePrefix(String hostname) {
        setHostname_MatchPhrasePrefix(hostname, null);
    }

    public void setHostname_MatchPhrasePrefix(String hostname, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("hostname", hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_Fuzzy(String hostname) {
        setHostname_Fuzzy(hostname, null);
    }

    public void setHostname_Fuzzy(String hostname, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("hostname", hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_Prefix(String hostname) {
        setHostname_Prefix(hostname, null);
    }

    public void setHostname_Prefix(String hostname, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("hostname", hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_GreaterThan(String hostname) {
        setHostname_GreaterThan(hostname, null);
    }

    public void setHostname_GreaterThan(String hostname, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("hostname", ConditionKey.CK_GREATER_THAN, hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_LessThan(String hostname) {
        setHostname_LessThan(hostname, null);
    }

    public void setHostname_LessThan(String hostname, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("hostname", ConditionKey.CK_LESS_THAN, hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_GreaterEqual(String hostname) {
        setHostname_GreaterEqual(hostname, null);
    }

    public void setHostname_GreaterEqual(String hostname, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("hostname", ConditionKey.CK_GREATER_EQUAL, hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_LessEqual(String hostname) {
        setHostname_LessEqual(hostname, null);
    }

    public void setHostname_LessEqual(String hostname, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("hostname", ConditionKey.CK_LESS_EQUAL, hostname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebAuthenticationCQ addOrderBy_Hostname_Asc() {
        regOBA("hostname");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_Hostname_Desc() {
        regOBD("hostname");
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

    public BsWebAuthenticationCQ addOrderBy_Id_Asc() {
        regOBA("id");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_Id_Desc() {
        regOBD("id");
        return this;
    }

    public void setParameters_Equal(String parameters) {
        setParameters_Term(parameters, null);
    }

    public void setParameters_Equal(String parameters, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setParameters_Term(parameters, opLambda);
    }

    public void setParameters_Term(String parameters) {
        setParameters_Term(parameters, null);
    }

    public void setParameters_Term(String parameters, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("parameters", parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_Terms(Collection<String> parametersList) {
        setParameters_Terms(parametersList, null);
    }

    public void setParameters_Terms(Collection<String> parametersList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("parameters", parametersList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_InScope(Collection<String> parametersList) {
        setParameters_Terms(parametersList, null);
    }

    public void setParameters_InScope(Collection<String> parametersList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setParameters_Terms(parametersList, opLambda);
    }

    public void setParameters_Match(String parameters) {
        setParameters_Match(parameters, null);
    }

    public void setParameters_Match(String parameters, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("parameters", parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_MatchPhrase(String parameters) {
        setParameters_MatchPhrase(parameters, null);
    }

    public void setParameters_MatchPhrase(String parameters, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("parameters", parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_MatchPhrasePrefix(String parameters) {
        setParameters_MatchPhrasePrefix(parameters, null);
    }

    public void setParameters_MatchPhrasePrefix(String parameters, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("parameters", parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_Fuzzy(String parameters) {
        setParameters_Fuzzy(parameters, null);
    }

    public void setParameters_Fuzzy(String parameters, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("parameters", parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_Prefix(String parameters) {
        setParameters_Prefix(parameters, null);
    }

    public void setParameters_Prefix(String parameters, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("parameters", parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_GreaterThan(String parameters) {
        setParameters_GreaterThan(parameters, null);
    }

    public void setParameters_GreaterThan(String parameters, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("parameters", ConditionKey.CK_GREATER_THAN, parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_LessThan(String parameters) {
        setParameters_LessThan(parameters, null);
    }

    public void setParameters_LessThan(String parameters, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("parameters", ConditionKey.CK_LESS_THAN, parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_GreaterEqual(String parameters) {
        setParameters_GreaterEqual(parameters, null);
    }

    public void setParameters_GreaterEqual(String parameters, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("parameters", ConditionKey.CK_GREATER_EQUAL, parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_LessEqual(String parameters) {
        setParameters_LessEqual(parameters, null);
    }

    public void setParameters_LessEqual(String parameters, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("parameters", ConditionKey.CK_LESS_EQUAL, parameters);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebAuthenticationCQ addOrderBy_Parameters_Asc() {
        regOBA("parameters");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_Parameters_Desc() {
        regOBD("parameters");
        return this;
    }

    public void setPassword_Equal(String password) {
        setPassword_Term(password, null);
    }

    public void setPassword_Equal(String password, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setPassword_Term(password, opLambda);
    }

    public void setPassword_Term(String password) {
        setPassword_Term(password, null);
    }

    public void setPassword_Term(String password, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Terms(Collection<String> passwordList) {
        setPassword_Terms(passwordList, null);
    }

    public void setPassword_Terms(Collection<String> passwordList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("password", passwordList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_InScope(Collection<String> passwordList) {
        setPassword_Terms(passwordList, null);
    }

    public void setPassword_InScope(Collection<String> passwordList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setPassword_Terms(passwordList, opLambda);
    }

    public void setPassword_Match(String password) {
        setPassword_Match(password, null);
    }

    public void setPassword_Match(String password, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_MatchPhrase(String password) {
        setPassword_MatchPhrase(password, null);
    }

    public void setPassword_MatchPhrase(String password, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_MatchPhrasePrefix(String password) {
        setPassword_MatchPhrasePrefix(password, null);
    }

    public void setPassword_MatchPhrasePrefix(String password, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Fuzzy(String password) {
        setPassword_Fuzzy(password, null);
    }

    public void setPassword_Fuzzy(String password, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Prefix(String password) {
        setPassword_Prefix(password, null);
    }

    public void setPassword_Prefix(String password, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_GreaterThan(String password) {
        setPassword_GreaterThan(password, null);
    }

    public void setPassword_GreaterThan(String password, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("password", ConditionKey.CK_GREATER_THAN, password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_LessThan(String password) {
        setPassword_LessThan(password, null);
    }

    public void setPassword_LessThan(String password, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("password", ConditionKey.CK_LESS_THAN, password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_GreaterEqual(String password) {
        setPassword_GreaterEqual(password, null);
    }

    public void setPassword_GreaterEqual(String password, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("password", ConditionKey.CK_GREATER_EQUAL, password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_LessEqual(String password) {
        setPassword_LessEqual(password, null);
    }

    public void setPassword_LessEqual(String password, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("password", ConditionKey.CK_LESS_EQUAL, password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebAuthenticationCQ addOrderBy_Password_Asc() {
        regOBA("password");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_Password_Desc() {
        regOBD("password");
        return this;
    }

    public void setPort_Equal(Integer port) {
        setPort_Term(port, null);
    }

    public void setPort_Equal(Integer port, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setPort_Term(port, opLambda);
    }

    public void setPort_Term(Integer port) {
        setPort_Term(port, null);
    }

    public void setPort_Term(Integer port, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("port", port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Terms(Collection<Integer> portList) {
        setPort_Terms(portList, null);
    }

    public void setPort_Terms(Collection<Integer> portList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("port", portList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_InScope(Collection<Integer> portList) {
        setPort_Terms(portList, null);
    }

    public void setPort_InScope(Collection<Integer> portList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setPort_Terms(portList, opLambda);
    }

    public void setPort_Match(Integer port) {
        setPort_Match(port, null);
    }

    public void setPort_Match(Integer port, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("port", port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_MatchPhrase(Integer port) {
        setPort_MatchPhrase(port, null);
    }

    public void setPort_MatchPhrase(Integer port, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("port", port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_MatchPhrasePrefix(Integer port) {
        setPort_MatchPhrasePrefix(port, null);
    }

    public void setPort_MatchPhrasePrefix(Integer port, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("port", port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Fuzzy(Integer port) {
        setPort_Fuzzy(port, null);
    }

    public void setPort_Fuzzy(Integer port, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("port", port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_GreaterThan(Integer port) {
        setPort_GreaterThan(port, null);
    }

    public void setPort_GreaterThan(Integer port, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("port", ConditionKey.CK_GREATER_THAN, port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_LessThan(Integer port) {
        setPort_LessThan(port, null);
    }

    public void setPort_LessThan(Integer port, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("port", ConditionKey.CK_LESS_THAN, port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_GreaterEqual(Integer port) {
        setPort_GreaterEqual(port, null);
    }

    public void setPort_GreaterEqual(Integer port, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("port", ConditionKey.CK_GREATER_EQUAL, port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_LessEqual(Integer port) {
        setPort_LessEqual(port, null);
    }

    public void setPort_LessEqual(Integer port, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("port", ConditionKey.CK_LESS_EQUAL, port);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebAuthenticationCQ addOrderBy_Port_Asc() {
        regOBA("port");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_Port_Desc() {
        regOBD("port");
        return this;
    }

    public void setProtocolScheme_Equal(String protocolScheme) {
        setProtocolScheme_Term(protocolScheme, null);
    }

    public void setProtocolScheme_Equal(String protocolScheme, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setProtocolScheme_Term(protocolScheme, opLambda);
    }

    public void setProtocolScheme_Term(String protocolScheme) {
        setProtocolScheme_Term(protocolScheme, null);
    }

    public void setProtocolScheme_Term(String protocolScheme, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("protocolScheme", protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_Terms(Collection<String> protocolSchemeList) {
        setProtocolScheme_Terms(protocolSchemeList, null);
    }

    public void setProtocolScheme_Terms(Collection<String> protocolSchemeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("protocolScheme", protocolSchemeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_InScope(Collection<String> protocolSchemeList) {
        setProtocolScheme_Terms(protocolSchemeList, null);
    }

    public void setProtocolScheme_InScope(Collection<String> protocolSchemeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setProtocolScheme_Terms(protocolSchemeList, opLambda);
    }

    public void setProtocolScheme_Match(String protocolScheme) {
        setProtocolScheme_Match(protocolScheme, null);
    }

    public void setProtocolScheme_Match(String protocolScheme, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("protocolScheme", protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_MatchPhrase(String protocolScheme) {
        setProtocolScheme_MatchPhrase(protocolScheme, null);
    }

    public void setProtocolScheme_MatchPhrase(String protocolScheme, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("protocolScheme", protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_MatchPhrasePrefix(String protocolScheme) {
        setProtocolScheme_MatchPhrasePrefix(protocolScheme, null);
    }

    public void setProtocolScheme_MatchPhrasePrefix(String protocolScheme, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("protocolScheme", protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_Fuzzy(String protocolScheme) {
        setProtocolScheme_Fuzzy(protocolScheme, null);
    }

    public void setProtocolScheme_Fuzzy(String protocolScheme, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("protocolScheme", protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_Prefix(String protocolScheme) {
        setProtocolScheme_Prefix(protocolScheme, null);
    }

    public void setProtocolScheme_Prefix(String protocolScheme, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("protocolScheme", protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_GreaterThan(String protocolScheme) {
        setProtocolScheme_GreaterThan(protocolScheme, null);
    }

    public void setProtocolScheme_GreaterThan(String protocolScheme, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("protocolScheme", ConditionKey.CK_GREATER_THAN, protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_LessThan(String protocolScheme) {
        setProtocolScheme_LessThan(protocolScheme, null);
    }

    public void setProtocolScheme_LessThan(String protocolScheme, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("protocolScheme", ConditionKey.CK_LESS_THAN, protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_GreaterEqual(String protocolScheme) {
        setProtocolScheme_GreaterEqual(protocolScheme, null);
    }

    public void setProtocolScheme_GreaterEqual(String protocolScheme, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("protocolScheme", ConditionKey.CK_GREATER_EQUAL, protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_LessEqual(String protocolScheme) {
        setProtocolScheme_LessEqual(protocolScheme, null);
    }

    public void setProtocolScheme_LessEqual(String protocolScheme, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("protocolScheme", ConditionKey.CK_LESS_EQUAL, protocolScheme);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebAuthenticationCQ addOrderBy_ProtocolScheme_Asc() {
        regOBA("protocolScheme");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_ProtocolScheme_Desc() {
        regOBD("protocolScheme");
        return this;
    }

    public void setUpdatedBy_Equal(String updatedBy) {
        setUpdatedBy_Term(updatedBy, null);
    }

    public void setUpdatedBy_Equal(String updatedBy, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUpdatedBy_Term(updatedBy, opLambda);
    }

    public void setUpdatedBy_Term(String updatedBy) {
        setUpdatedBy_Term(updatedBy, null);
    }

    public void setUpdatedBy_Term(String updatedBy, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Terms(Collection<String> updatedByList) {
        setUpdatedBy_Terms(updatedByList, null);
    }

    public void setUpdatedBy_Terms(Collection<String> updatedByList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("updatedBy", updatedByList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_InScope(Collection<String> updatedByList) {
        setUpdatedBy_Terms(updatedByList, null);
    }

    public void setUpdatedBy_InScope(Collection<String> updatedByList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUpdatedBy_Terms(updatedByList, opLambda);
    }

    public void setUpdatedBy_Match(String updatedBy) {
        setUpdatedBy_Match(updatedBy, null);
    }

    public void setUpdatedBy_Match(String updatedBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_MatchPhrase(String updatedBy) {
        setUpdatedBy_MatchPhrase(updatedBy, null);
    }

    public void setUpdatedBy_MatchPhrase(String updatedBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_MatchPhrasePrefix(String updatedBy) {
        setUpdatedBy_MatchPhrasePrefix(updatedBy, null);
    }

    public void setUpdatedBy_MatchPhrasePrefix(String updatedBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Fuzzy(String updatedBy) {
        setUpdatedBy_Fuzzy(updatedBy, null);
    }

    public void setUpdatedBy_Fuzzy(String updatedBy, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Prefix(String updatedBy) {
        setUpdatedBy_Prefix(updatedBy, null);
    }

    public void setUpdatedBy_Prefix(String updatedBy, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_GreaterThan(String updatedBy) {
        setUpdatedBy_GreaterThan(updatedBy, null);
    }

    public void setUpdatedBy_GreaterThan(String updatedBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("updatedBy", ConditionKey.CK_GREATER_THAN, updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_LessThan(String updatedBy) {
        setUpdatedBy_LessThan(updatedBy, null);
    }

    public void setUpdatedBy_LessThan(String updatedBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("updatedBy", ConditionKey.CK_LESS_THAN, updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_GreaterEqual(String updatedBy) {
        setUpdatedBy_GreaterEqual(updatedBy, null);
    }

    public void setUpdatedBy_GreaterEqual(String updatedBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("updatedBy", ConditionKey.CK_GREATER_EQUAL, updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_LessEqual(String updatedBy) {
        setUpdatedBy_LessEqual(updatedBy, null);
    }

    public void setUpdatedBy_LessEqual(String updatedBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("updatedBy", ConditionKey.CK_LESS_EQUAL, updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebAuthenticationCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("updatedBy");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_UpdatedBy_Desc() {
        regOBD("updatedBy");
        return this;
    }

    public void setUpdatedTime_Equal(Long updatedTime) {
        setUpdatedTime_Term(updatedTime, null);
    }

    public void setUpdatedTime_Equal(Long updatedTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUpdatedTime_Term(updatedTime, opLambda);
    }

    public void setUpdatedTime_Term(Long updatedTime) {
        setUpdatedTime_Term(updatedTime, null);
    }

    public void setUpdatedTime_Term(Long updatedTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Terms(Collection<Long> updatedTimeList) {
        setUpdatedTime_Terms(updatedTimeList, null);
    }

    public void setUpdatedTime_Terms(Collection<Long> updatedTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("updatedTime", updatedTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_InScope(Collection<Long> updatedTimeList) {
        setUpdatedTime_Terms(updatedTimeList, null);
    }

    public void setUpdatedTime_InScope(Collection<Long> updatedTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUpdatedTime_Terms(updatedTimeList, opLambda);
    }

    public void setUpdatedTime_Match(Long updatedTime) {
        setUpdatedTime_Match(updatedTime, null);
    }

    public void setUpdatedTime_Match(Long updatedTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_MatchPhrase(Long updatedTime) {
        setUpdatedTime_MatchPhrase(updatedTime, null);
    }

    public void setUpdatedTime_MatchPhrase(Long updatedTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_MatchPhrasePrefix(Long updatedTime) {
        setUpdatedTime_MatchPhrasePrefix(updatedTime, null);
    }

    public void setUpdatedTime_MatchPhrasePrefix(Long updatedTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Fuzzy(Long updatedTime) {
        setUpdatedTime_Fuzzy(updatedTime, null);
    }

    public void setUpdatedTime_Fuzzy(Long updatedTime, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_GreaterThan(Long updatedTime) {
        setUpdatedTime_GreaterThan(updatedTime, null);
    }

    public void setUpdatedTime_GreaterThan(Long updatedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("updatedTime", ConditionKey.CK_GREATER_THAN, updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_LessThan(Long updatedTime) {
        setUpdatedTime_LessThan(updatedTime, null);
    }

    public void setUpdatedTime_LessThan(Long updatedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("updatedTime", ConditionKey.CK_LESS_THAN, updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_GreaterEqual(Long updatedTime) {
        setUpdatedTime_GreaterEqual(updatedTime, null);
    }

    public void setUpdatedTime_GreaterEqual(Long updatedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("updatedTime", ConditionKey.CK_GREATER_EQUAL, updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_LessEqual(Long updatedTime) {
        setUpdatedTime_LessEqual(updatedTime, null);
    }

    public void setUpdatedTime_LessEqual(Long updatedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("updatedTime", ConditionKey.CK_LESS_EQUAL, updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebAuthenticationCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("updatedTime");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_UpdatedTime_Desc() {
        regOBD("updatedTime");
        return this;
    }

    public void setUsername_Equal(String username) {
        setUsername_Term(username, null);
    }

    public void setUsername_Equal(String username, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUsername_Term(username, opLambda);
    }

    public void setUsername_Term(String username) {
        setUsername_Term(username, null);
    }

    public void setUsername_Term(String username, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("username", username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_Terms(Collection<String> usernameList) {
        setUsername_Terms(usernameList, null);
    }

    public void setUsername_Terms(Collection<String> usernameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("username", usernameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_InScope(Collection<String> usernameList) {
        setUsername_Terms(usernameList, null);
    }

    public void setUsername_InScope(Collection<String> usernameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUsername_Terms(usernameList, opLambda);
    }

    public void setUsername_Match(String username) {
        setUsername_Match(username, null);
    }

    public void setUsername_Match(String username, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("username", username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_MatchPhrase(String username) {
        setUsername_MatchPhrase(username, null);
    }

    public void setUsername_MatchPhrase(String username, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("username", username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_MatchPhrasePrefix(String username) {
        setUsername_MatchPhrasePrefix(username, null);
    }

    public void setUsername_MatchPhrasePrefix(String username, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("username", username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_Fuzzy(String username) {
        setUsername_Fuzzy(username, null);
    }

    public void setUsername_Fuzzy(String username, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("username", username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_Prefix(String username) {
        setUsername_Prefix(username, null);
    }

    public void setUsername_Prefix(String username, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("username", username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_GreaterThan(String username) {
        setUsername_GreaterThan(username, null);
    }

    public void setUsername_GreaterThan(String username, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("username", ConditionKey.CK_GREATER_THAN, username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_LessThan(String username) {
        setUsername_LessThan(username, null);
    }

    public void setUsername_LessThan(String username, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("username", ConditionKey.CK_LESS_THAN, username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_GreaterEqual(String username) {
        setUsername_GreaterEqual(username, null);
    }

    public void setUsername_GreaterEqual(String username, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("username", ConditionKey.CK_GREATER_EQUAL, username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_LessEqual(String username) {
        setUsername_LessEqual(username, null);
    }

    public void setUsername_LessEqual(String username, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("username", ConditionKey.CK_LESS_EQUAL, username);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebAuthenticationCQ addOrderBy_Username_Asc() {
        regOBA("username");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_Username_Desc() {
        regOBD("username");
        return this;
    }

    public void setWebConfigId_Equal(String webConfigId) {
        setWebConfigId_Term(webConfigId, null);
    }

    public void setWebConfigId_Equal(String webConfigId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setWebConfigId_Term(webConfigId, opLambda);
    }

    public void setWebConfigId_Term(String webConfigId) {
        setWebConfigId_Term(webConfigId, null);
    }

    public void setWebConfigId_Term(String webConfigId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Terms(Collection<String> webConfigIdList) {
        setWebConfigId_Terms(webConfigIdList, null);
    }

    public void setWebConfigId_Terms(Collection<String> webConfigIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("webConfigId", webConfigIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_InScope(Collection<String> webConfigIdList) {
        setWebConfigId_Terms(webConfigIdList, null);
    }

    public void setWebConfigId_InScope(Collection<String> webConfigIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setWebConfigId_Terms(webConfigIdList, opLambda);
    }

    public void setWebConfigId_Match(String webConfigId) {
        setWebConfigId_Match(webConfigId, null);
    }

    public void setWebConfigId_Match(String webConfigId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_MatchPhrase(String webConfigId) {
        setWebConfigId_MatchPhrase(webConfigId, null);
    }

    public void setWebConfigId_MatchPhrase(String webConfigId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_MatchPhrasePrefix(String webConfigId) {
        setWebConfigId_MatchPhrasePrefix(webConfigId, null);
    }

    public void setWebConfigId_MatchPhrasePrefix(String webConfigId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Fuzzy(String webConfigId) {
        setWebConfigId_Fuzzy(webConfigId, null);
    }

    public void setWebConfigId_Fuzzy(String webConfigId, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Prefix(String webConfigId) {
        setWebConfigId_Prefix(webConfigId, null);
    }

    public void setWebConfigId_Prefix(String webConfigId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_GreaterThan(String webConfigId) {
        setWebConfigId_GreaterThan(webConfigId, null);
    }

    public void setWebConfigId_GreaterThan(String webConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("webConfigId", ConditionKey.CK_GREATER_THAN, webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_LessThan(String webConfigId) {
        setWebConfigId_LessThan(webConfigId, null);
    }

    public void setWebConfigId_LessThan(String webConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("webConfigId", ConditionKey.CK_LESS_THAN, webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_GreaterEqual(String webConfigId) {
        setWebConfigId_GreaterEqual(webConfigId, null);
    }

    public void setWebConfigId_GreaterEqual(String webConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("webConfigId", ConditionKey.CK_GREATER_EQUAL, webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_LessEqual(String webConfigId) {
        setWebConfigId_LessEqual(webConfigId, null);
    }

    public void setWebConfigId_LessEqual(String webConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("webConfigId", ConditionKey.CK_LESS_EQUAL, webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebAuthenticationCQ addOrderBy_WebConfigId_Asc() {
        regOBA("webConfigId");
        return this;
    }

    public BsWebAuthenticationCQ addOrderBy_WebConfigId_Desc() {
        regOBD("webConfigId");
        return this;
    }

}
