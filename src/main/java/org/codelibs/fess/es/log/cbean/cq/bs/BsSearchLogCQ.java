/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.es.log.cbean.cq.bs;

import java.time.*;
import java.util.Collection;

import org.codelibs.fess.es.log.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.log.cbean.cq.SearchLogCQ;
import org.dbflute.cbean.ckey.ConditionKey;
import org.dbflute.exception.IllegalConditionBeanOperationException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NotQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;


/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsSearchLogCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "search_log";
    }

    @Override
    public String xgetAliasName() {
        return "search_log";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void filtered(FilteredCall<SearchLogCQ, SearchLogCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<SearchLogCQ, SearchLogCQ> filteredLambda,
            ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter)->{
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<SearchLogCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<SearchLogCQ> notLambda, ConditionOptionCall<NotQueryBuilder> opLambda) {
        SearchLogCQ notQuery = new SearchLogCQ();
        notLambda.callback(notQuery);
        if (notQuery.hasQueries()) {
            if (notQuery.getQueryBuilderList().size() > 1) {
                final String msg = "not query must be one query.";
                throw new IllegalConditionBeanOperationException(msg);
            }
            NotQueryBuilder builder = QueryBuilders.notQuery(notQuery.getQueryBuilderList().get(0));
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
        SearchLogCQ filterQuery = new SearchLogCQ();
        boolLambda.callback(mustQuery, shouldQuery, mustNotQuery, filterQuery);
        if (mustQuery.hasQueries() || shouldQuery.hasQueries() || mustNotQuery.hasQueries() || filterQuery.hasQueries()) {
            BoolQueryBuilder builder = regBoolCQ(mustQuery.getQueryBuilderList(), shouldQuery.getQueryBuilderList(), mustNotQuery.getQueryBuilderList(), filterQuery.getQueryBuilderList());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    // ===================================================================================
    //                                                                           Query Set
    //                                                                           =========
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
        TermQueryBuilder builder = regTermQ("_id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_NotEqual(String id) {
        setId_NotTerm(id, null);
    }

    public void setId_NotEqual(String id, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setId_NotTerm(id, opLambda);
    }

    public void setId_NotTerm(String id) {
        setId_NotTerm(id, null);
    }

    public void setId_NotTerm(String id, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("_id", id));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Terms(Collection<String> idList) {
        setId_Terms(idList, null);
    }

    public void setId_Terms(Collection<String> idList, ConditionOptionCall<IdsQueryBuilder> opLambda) {
        IdsQueryBuilder builder = regIdsQ(idList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_InScope(Collection<String> idList) {
        setId_Terms(idList, null);
    }

    public void setId_InScope(Collection<String> idList, ConditionOptionCall<IdsQueryBuilder> opLambda) {
        setId_Terms(idList, opLambda);
    }

    public BsSearchLogCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    public BsSearchLogCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setAccessType_Equal(String accessType) {
        setAccessType_Term(accessType, null);
    }

    public void setAccessType_Equal(String accessType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setAccessType_Term(accessType, opLambda);
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

    public void setAccessType_NotEqual(String accessType) {
        setAccessType_NotTerm(accessType, null);
    }

    public void setAccessType_NotEqual(String accessType, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setAccessType_NotTerm(accessType, opLambda);
    }

    public void setAccessType_NotTerm(String accessType) {
        setAccessType_NotTerm(accessType, null);
    }

    public void setAccessType_NotTerm(String accessType, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("accessType", accessType));
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

    public void setUser_Equal(String user) {
        setUser_Term(user, null);
    }

    public void setUser_Equal(String user, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUser_Term(user, opLambda);
    }

    public void setUser_Term(String user) {
        setUser_Term(user, null);
    }

    public void setUser_Term(String user, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_NotEqual(String user) {
        setUser_NotTerm(user, null);
    }

    public void setUser_NotEqual(String user, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setUser_NotTerm(user, opLambda);
    }

    public void setUser_NotTerm(String user) {
        setUser_NotTerm(user, null);
    }

    public void setUser_NotTerm(String user, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("user", user));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_Terms(Collection<String> userList) {
        setUser_Terms(userList, null);
    }

    public void setUser_Terms(Collection<String> userList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("user", userList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_InScope(Collection<String> userList) {
        setUser_Terms(userList, null);
    }

    public void setUser_InScope(Collection<String> userList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUser_Terms(userList, opLambda);
    }

    public void setUser_Match(String user) {
        setUser_Match(user, null);
    }

    public void setUser_Match(String user, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_MatchPhrase(String user) {
        setUser_MatchPhrase(user, null);
    }

    public void setUser_MatchPhrase(String user, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_MatchPhrasePrefix(String user) {
        setUser_MatchPhrasePrefix(user, null);
    }

    public void setUser_MatchPhrasePrefix(String user, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_Fuzzy(String user) {
        setUser_Fuzzy(user, null);
    }

    public void setUser_Fuzzy(String user, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_Prefix(String user) {
        setUser_Prefix(user, null);
    }

    public void setUser_Prefix(String user, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_GreaterThan(String user) {
        setUser_GreaterThan(user, null);
    }

    public void setUser_GreaterThan(String user, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("user", ConditionKey.CK_GREATER_THAN, user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_LessThan(String user) {
        setUser_LessThan(user, null);
    }

    public void setUser_LessThan(String user, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("user", ConditionKey.CK_LESS_THAN, user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_GreaterEqual(String user) {
        setUser_GreaterEqual(user, null);
    }

    public void setUser_GreaterEqual(String user, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("user", ConditionKey.CK_GREATER_EQUAL, user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_LessEqual(String user) {
        setUser_LessEqual(user, null);
    }

    public void setUser_LessEqual(String user, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("user", ConditionKey.CK_LESS_EQUAL, user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_User_Asc() {
        regOBA("user");
        return this;
    }

    public BsSearchLogCQ addOrderBy_User_Desc() {
        regOBD("user");
        return this;
    }

    public void setQueryId_Equal(String queryId) {
        setQueryId_Term(queryId, null);
    }

    public void setQueryId_Equal(String queryId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setQueryId_Term(queryId, opLambda);
    }

    public void setQueryId_Term(String queryId) {
        setQueryId_Term(queryId, null);
    }

    public void setQueryId_Term(String queryId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("queryId", queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_NotEqual(String queryId) {
        setQueryId_NotTerm(queryId, null);
    }

    public void setQueryId_NotEqual(String queryId, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setQueryId_NotTerm(queryId, opLambda);
    }

    public void setQueryId_NotTerm(String queryId) {
        setQueryId_NotTerm(queryId, null);
    }

    public void setQueryId_NotTerm(String queryId, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("queryId", queryId));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_Terms(Collection<String> queryIdList) {
        setQueryId_Terms(queryIdList, null);
    }

    public void setQueryId_Terms(Collection<String> queryIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("queryId", queryIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_InScope(Collection<String> queryIdList) {
        setQueryId_Terms(queryIdList, null);
    }

    public void setQueryId_InScope(Collection<String> queryIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setQueryId_Terms(queryIdList, opLambda);
    }

    public void setQueryId_Match(String queryId) {
        setQueryId_Match(queryId, null);
    }

    public void setQueryId_Match(String queryId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("queryId", queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_MatchPhrase(String queryId) {
        setQueryId_MatchPhrase(queryId, null);
    }

    public void setQueryId_MatchPhrase(String queryId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("queryId", queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_MatchPhrasePrefix(String queryId) {
        setQueryId_MatchPhrasePrefix(queryId, null);
    }

    public void setQueryId_MatchPhrasePrefix(String queryId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("queryId", queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_Fuzzy(String queryId) {
        setQueryId_Fuzzy(queryId, null);
    }

    public void setQueryId_Fuzzy(String queryId, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("queryId", queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_Prefix(String queryId) {
        setQueryId_Prefix(queryId, null);
    }

    public void setQueryId_Prefix(String queryId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("queryId", queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_GreaterThan(String queryId) {
        setQueryId_GreaterThan(queryId, null);
    }

    public void setQueryId_GreaterThan(String queryId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryId", ConditionKey.CK_GREATER_THAN, queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_LessThan(String queryId) {
        setQueryId_LessThan(queryId, null);
    }

    public void setQueryId_LessThan(String queryId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryId", ConditionKey.CK_LESS_THAN, queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_GreaterEqual(String queryId) {
        setQueryId_GreaterEqual(queryId, null);
    }

    public void setQueryId_GreaterEqual(String queryId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryId", ConditionKey.CK_GREATER_EQUAL, queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_LessEqual(String queryId) {
        setQueryId_LessEqual(queryId, null);
    }

    public void setQueryId_LessEqual(String queryId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryId", ConditionKey.CK_LESS_EQUAL, queryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_QueryId_Asc() {
        regOBA("queryId");
        return this;
    }

    public BsSearchLogCQ addOrderBy_QueryId_Desc() {
        regOBD("queryId");
        return this;
    }

    public void setClientIp_Equal(String clientIp) {
        setClientIp_Term(clientIp, null);
    }

    public void setClientIp_Equal(String clientIp, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setClientIp_Term(clientIp, opLambda);
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

    public void setClientIp_NotEqual(String clientIp) {
        setClientIp_NotTerm(clientIp, null);
    }

    public void setClientIp_NotEqual(String clientIp, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setClientIp_NotTerm(clientIp, opLambda);
    }

    public void setClientIp_NotTerm(String clientIp) {
        setClientIp_NotTerm(clientIp, null);
    }

    public void setClientIp_NotTerm(String clientIp, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("clientIp", clientIp));
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

    public void setHitCount_Equal(Long hitCount) {
        setHitCount_Term(hitCount, null);
    }

    public void setHitCount_Equal(Long hitCount, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setHitCount_Term(hitCount, opLambda);
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

    public void setHitCount_NotEqual(Long hitCount) {
        setHitCount_NotTerm(hitCount, null);
    }

    public void setHitCount_NotEqual(Long hitCount, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setHitCount_NotTerm(hitCount, opLambda);
    }

    public void setHitCount_NotTerm(Long hitCount) {
        setHitCount_NotTerm(hitCount, null);
    }

    public void setHitCount_NotTerm(Long hitCount, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("hitCount", hitCount));
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

    public void setQueryOffset_Equal(Integer queryOffset) {
        setQueryOffset_Term(queryOffset, null);
    }

    public void setQueryOffset_Equal(Integer queryOffset, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setQueryOffset_Term(queryOffset, opLambda);
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

    public void setQueryOffset_NotEqual(Integer queryOffset) {
        setQueryOffset_NotTerm(queryOffset, null);
    }

    public void setQueryOffset_NotEqual(Integer queryOffset, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setQueryOffset_NotTerm(queryOffset, opLambda);
    }

    public void setQueryOffset_NotTerm(Integer queryOffset) {
        setQueryOffset_NotTerm(queryOffset, null);
    }

    public void setQueryOffset_NotTerm(Integer queryOffset, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("queryOffset", queryOffset));
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

    public void setQueryPageSize_Equal(Integer queryPageSize) {
        setQueryPageSize_Term(queryPageSize, null);
    }

    public void setQueryPageSize_Equal(Integer queryPageSize, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setQueryPageSize_Term(queryPageSize, opLambda);
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

    public void setQueryPageSize_NotEqual(Integer queryPageSize) {
        setQueryPageSize_NotTerm(queryPageSize, null);
    }

    public void setQueryPageSize_NotEqual(Integer queryPageSize, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setQueryPageSize_NotTerm(queryPageSize, opLambda);
    }

    public void setQueryPageSize_NotTerm(Integer queryPageSize) {
        setQueryPageSize_NotTerm(queryPageSize, null);
    }

    public void setQueryPageSize_NotTerm(Integer queryPageSize, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("queryPageSize", queryPageSize));
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

    public void setReferer_Equal(String referer) {
        setReferer_Term(referer, null);
    }

    public void setReferer_Equal(String referer, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setReferer_Term(referer, opLambda);
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

    public void setReferer_NotEqual(String referer) {
        setReferer_NotTerm(referer, null);
    }

    public void setReferer_NotEqual(String referer, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setReferer_NotTerm(referer, opLambda);
    }

    public void setReferer_NotTerm(String referer) {
        setReferer_NotTerm(referer, null);
    }

    public void setReferer_NotTerm(String referer, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("referer", referer));
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

    public void setRequestedAt_Equal(LocalDateTime requestedAt) {
        setRequestedAt_Term(requestedAt, null);
    }

    public void setRequestedAt_Equal(LocalDateTime requestedAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setRequestedAt_Term(requestedAt, opLambda);
    }

    public void setRequestedAt_Term(LocalDateTime requestedAt) {
        setRequestedAt_Term(requestedAt, null);
    }

    public void setRequestedAt_Term(LocalDateTime requestedAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("requestedAt", requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_NotEqual(LocalDateTime requestedAt) {
        setRequestedAt_NotTerm(requestedAt, null);
    }

    public void setRequestedAt_NotEqual(LocalDateTime requestedAt, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setRequestedAt_NotTerm(requestedAt, opLambda);
    }

    public void setRequestedAt_NotTerm(LocalDateTime requestedAt) {
        setRequestedAt_NotTerm(requestedAt, null);
    }

    public void setRequestedAt_NotTerm(LocalDateTime requestedAt, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("requestedAt", requestedAt));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_Terms(Collection<LocalDateTime> requestedAtList) {
        setRequestedAt_Terms(requestedAtList, null);
    }

    public void setRequestedAt_Terms(Collection<LocalDateTime> requestedAtList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("requestedAt", requestedAtList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_InScope(Collection<LocalDateTime> requestedAtList) {
        setRequestedAt_Terms(requestedAtList, null);
    }

    public void setRequestedAt_InScope(Collection<LocalDateTime> requestedAtList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setRequestedAt_Terms(requestedAtList, opLambda);
    }

    public void setRequestedAt_Match(LocalDateTime requestedAt) {
        setRequestedAt_Match(requestedAt, null);
    }

    public void setRequestedAt_Match(LocalDateTime requestedAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("requestedAt", requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_MatchPhrase(LocalDateTime requestedAt) {
        setRequestedAt_MatchPhrase(requestedAt, null);
    }

    public void setRequestedAt_MatchPhrase(LocalDateTime requestedAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("requestedAt", requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_MatchPhrasePrefix(LocalDateTime requestedAt) {
        setRequestedAt_MatchPhrasePrefix(requestedAt, null);
    }

    public void setRequestedAt_MatchPhrasePrefix(LocalDateTime requestedAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("requestedAt", requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_Fuzzy(LocalDateTime requestedAt) {
        setRequestedAt_Fuzzy(requestedAt, null);
    }

    public void setRequestedAt_Fuzzy(LocalDateTime requestedAt, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("requestedAt", requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_GreaterThan(LocalDateTime requestedAt) {
        setRequestedAt_GreaterThan(requestedAt, null);
    }

    public void setRequestedAt_GreaterThan(LocalDateTime requestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("requestedAt", ConditionKey.CK_GREATER_THAN, requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_LessThan(LocalDateTime requestedAt) {
        setRequestedAt_LessThan(requestedAt, null);
    }

    public void setRequestedAt_LessThan(LocalDateTime requestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("requestedAt", ConditionKey.CK_LESS_THAN, requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_GreaterEqual(LocalDateTime requestedAt) {
        setRequestedAt_GreaterEqual(requestedAt, null);
    }

    public void setRequestedAt_GreaterEqual(LocalDateTime requestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("requestedAt", ConditionKey.CK_GREATER_EQUAL, requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_LessEqual(LocalDateTime requestedAt) {
        setRequestedAt_LessEqual(requestedAt, null);
    }

    public void setRequestedAt_LessEqual(LocalDateTime requestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("requestedAt", ConditionKey.CK_LESS_EQUAL, requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_RequestedAt_Asc() {
        regOBA("requestedAt");
        return this;
    }

    public BsSearchLogCQ addOrderBy_RequestedAt_Desc() {
        regOBD("requestedAt");
        return this;
    }

    public void setResponseTime_Equal(Long responseTime) {
        setResponseTime_Term(responseTime, null);
    }

    public void setResponseTime_Equal(Long responseTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setResponseTime_Term(responseTime, opLambda);
    }

    public void setResponseTime_Term(Long responseTime) {
        setResponseTime_Term(responseTime, null);
    }

    public void setResponseTime_Term(Long responseTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_NotEqual(Long responseTime) {
        setResponseTime_NotTerm(responseTime, null);
    }

    public void setResponseTime_NotEqual(Long responseTime, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setResponseTime_NotTerm(responseTime, opLambda);
    }

    public void setResponseTime_NotTerm(Long responseTime) {
        setResponseTime_NotTerm(responseTime, null);
    }

    public void setResponseTime_NotTerm(Long responseTime, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("responseTime", responseTime));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Terms(Collection<Long> responseTimeList) {
        setResponseTime_Terms(responseTimeList, null);
    }

    public void setResponseTime_Terms(Collection<Long> responseTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("responseTime", responseTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_InScope(Collection<Long> responseTimeList) {
        setResponseTime_Terms(responseTimeList, null);
    }

    public void setResponseTime_InScope(Collection<Long> responseTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setResponseTime_Terms(responseTimeList, opLambda);
    }

    public void setResponseTime_Match(Long responseTime) {
        setResponseTime_Match(responseTime, null);
    }

    public void setResponseTime_Match(Long responseTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_MatchPhrase(Long responseTime) {
        setResponseTime_MatchPhrase(responseTime, null);
    }

    public void setResponseTime_MatchPhrase(Long responseTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_MatchPhrasePrefix(Long responseTime) {
        setResponseTime_MatchPhrasePrefix(responseTime, null);
    }

    public void setResponseTime_MatchPhrasePrefix(Long responseTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Fuzzy(Long responseTime) {
        setResponseTime_Fuzzy(responseTime, null);
    }

    public void setResponseTime_Fuzzy(Long responseTime, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_GreaterThan(Long responseTime) {
        setResponseTime_GreaterThan(responseTime, null);
    }

    public void setResponseTime_GreaterThan(Long responseTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("responseTime", ConditionKey.CK_GREATER_THAN, responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_LessThan(Long responseTime) {
        setResponseTime_LessThan(responseTime, null);
    }

    public void setResponseTime_LessThan(Long responseTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("responseTime", ConditionKey.CK_LESS_THAN, responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_GreaterEqual(Long responseTime) {
        setResponseTime_GreaterEqual(responseTime, null);
    }

    public void setResponseTime_GreaterEqual(Long responseTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("responseTime", ConditionKey.CK_GREATER_EQUAL, responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_LessEqual(Long responseTime) {
        setResponseTime_LessEqual(responseTime, null);
    }

    public void setResponseTime_LessEqual(Long responseTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
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

    public void setQueryTime_Equal(Long queryTime) {
        setQueryTime_Term(queryTime, null);
    }

    public void setQueryTime_Equal(Long queryTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setQueryTime_Term(queryTime, opLambda);
    }

    public void setQueryTime_Term(Long queryTime) {
        setQueryTime_Term(queryTime, null);
    }

    public void setQueryTime_Term(Long queryTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("queryTime", queryTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_NotEqual(Long queryTime) {
        setQueryTime_NotTerm(queryTime, null);
    }

    public void setQueryTime_NotEqual(Long queryTime, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setQueryTime_NotTerm(queryTime, opLambda);
    }

    public void setQueryTime_NotTerm(Long queryTime) {
        setQueryTime_NotTerm(queryTime, null);
    }

    public void setQueryTime_NotTerm(Long queryTime, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("queryTime", queryTime));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Terms(Collection<Long> queryTimeList) {
        setQueryTime_Terms(queryTimeList, null);
    }

    public void setQueryTime_Terms(Collection<Long> queryTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("queryTime", queryTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_InScope(Collection<Long> queryTimeList) {
        setQueryTime_Terms(queryTimeList, null);
    }

    public void setQueryTime_InScope(Collection<Long> queryTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setQueryTime_Terms(queryTimeList, opLambda);
    }

    public void setQueryTime_Match(Long queryTime) {
        setQueryTime_Match(queryTime, null);
    }

    public void setQueryTime_Match(Long queryTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("queryTime", queryTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_MatchPhrase(Long queryTime) {
        setQueryTime_MatchPhrase(queryTime, null);
    }

    public void setQueryTime_MatchPhrase(Long queryTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("queryTime", queryTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_MatchPhrasePrefix(Long queryTime) {
        setQueryTime_MatchPhrasePrefix(queryTime, null);
    }

    public void setQueryTime_MatchPhrasePrefix(Long queryTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("queryTime", queryTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Fuzzy(Long queryTime) {
        setQueryTime_Fuzzy(queryTime, null);
    }

    public void setQueryTime_Fuzzy(Long queryTime, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("queryTime", queryTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_GreaterThan(Long queryTime) {
        setQueryTime_GreaterThan(queryTime, null);
    }

    public void setQueryTime_GreaterThan(Long queryTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryTime", ConditionKey.CK_GREATER_THAN, queryTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_LessThan(Long queryTime) {
        setQueryTime_LessThan(queryTime, null);
    }

    public void setQueryTime_LessThan(Long queryTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryTime", ConditionKey.CK_LESS_THAN, queryTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_GreaterEqual(Long queryTime) {
        setQueryTime_GreaterEqual(queryTime, null);
    }

    public void setQueryTime_GreaterEqual(Long queryTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryTime", ConditionKey.CK_GREATER_EQUAL, queryTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_LessEqual(Long queryTime) {
        setQueryTime_LessEqual(queryTime, null);
    }

    public void setQueryTime_LessEqual(Long queryTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryTime", ConditionKey.CK_LESS_EQUAL, queryTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_QueryTime_Asc() {
        regOBA("queryTime");
        return this;
    }

    public BsSearchLogCQ addOrderBy_QueryTime_Desc() {
        regOBD("queryTime");
        return this;
    }

    public void setSearchWord_Equal(String searchWord) {
        setSearchWord_Term(searchWord, null);
    }

    public void setSearchWord_Equal(String searchWord, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setSearchWord_Term(searchWord, opLambda);
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

    public void setSearchWord_NotEqual(String searchWord) {
        setSearchWord_NotTerm(searchWord, null);
    }

    public void setSearchWord_NotEqual(String searchWord, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setSearchWord_NotTerm(searchWord, opLambda);
    }

    public void setSearchWord_NotTerm(String searchWord) {
        setSearchWord_NotTerm(searchWord, null);
    }

    public void setSearchWord_NotTerm(String searchWord, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("searchWord", searchWord));
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

    public void setUserAgent_Equal(String userAgent) {
        setUserAgent_Term(userAgent, null);
    }

    public void setUserAgent_Equal(String userAgent, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUserAgent_Term(userAgent, opLambda);
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

    public void setUserAgent_NotEqual(String userAgent) {
        setUserAgent_NotTerm(userAgent, null);
    }

    public void setUserAgent_NotEqual(String userAgent, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setUserAgent_NotTerm(userAgent, opLambda);
    }

    public void setUserAgent_NotTerm(String userAgent) {
        setUserAgent_NotTerm(userAgent, null);
    }

    public void setUserAgent_NotTerm(String userAgent, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("userAgent", userAgent));
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

    public void setUserInfoId_Equal(String userInfoId) {
        setUserInfoId_Term(userInfoId, null);
    }

    public void setUserInfoId_Equal(String userInfoId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUserInfoId_Term(userInfoId, opLambda);
    }

    public void setUserInfoId_Term(String userInfoId) {
        setUserInfoId_Term(userInfoId, null);
    }

    public void setUserInfoId_Term(String userInfoId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("userInfoId", userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_NotEqual(String userInfoId) {
        setUserInfoId_NotTerm(userInfoId, null);
    }

    public void setUserInfoId_NotEqual(String userInfoId, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setUserInfoId_NotTerm(userInfoId, opLambda);
    }

    public void setUserInfoId_NotTerm(String userInfoId) {
        setUserInfoId_NotTerm(userInfoId, null);
    }

    public void setUserInfoId_NotTerm(String userInfoId, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("userInfoId", userInfoId));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_Terms(Collection<String> userInfoIdList) {
        setUserInfoId_Terms(userInfoIdList, null);
    }

    public void setUserInfoId_Terms(Collection<String> userInfoIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("userInfoId", userInfoIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_InScope(Collection<String> userInfoIdList) {
        setUserInfoId_Terms(userInfoIdList, null);
    }

    public void setUserInfoId_InScope(Collection<String> userInfoIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUserInfoId_Terms(userInfoIdList, opLambda);
    }

    public void setUserInfoId_Match(String userInfoId) {
        setUserInfoId_Match(userInfoId, null);
    }

    public void setUserInfoId_Match(String userInfoId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("userInfoId", userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_MatchPhrase(String userInfoId) {
        setUserInfoId_MatchPhrase(userInfoId, null);
    }

    public void setUserInfoId_MatchPhrase(String userInfoId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("userInfoId", userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_MatchPhrasePrefix(String userInfoId) {
        setUserInfoId_MatchPhrasePrefix(userInfoId, null);
    }

    public void setUserInfoId_MatchPhrasePrefix(String userInfoId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("userInfoId", userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_Fuzzy(String userInfoId) {
        setUserInfoId_Fuzzy(userInfoId, null);
    }

    public void setUserInfoId_Fuzzy(String userInfoId, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("userInfoId", userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_Prefix(String userInfoId) {
        setUserInfoId_Prefix(userInfoId, null);
    }

    public void setUserInfoId_Prefix(String userInfoId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("userInfoId", userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_GreaterThan(String userInfoId) {
        setUserInfoId_GreaterThan(userInfoId, null);
    }

    public void setUserInfoId_GreaterThan(String userInfoId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("userInfoId", ConditionKey.CK_GREATER_THAN, userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_LessThan(String userInfoId) {
        setUserInfoId_LessThan(userInfoId, null);
    }

    public void setUserInfoId_LessThan(String userInfoId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("userInfoId", ConditionKey.CK_LESS_THAN, userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_GreaterEqual(String userInfoId) {
        setUserInfoId_GreaterEqual(userInfoId, null);
    }

    public void setUserInfoId_GreaterEqual(String userInfoId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("userInfoId", ConditionKey.CK_GREATER_EQUAL, userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_LessEqual(String userInfoId) {
        setUserInfoId_LessEqual(userInfoId, null);
    }

    public void setUserInfoId_LessEqual(String userInfoId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("userInfoId", ConditionKey.CK_LESS_EQUAL, userInfoId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSearchLogCQ addOrderBy_UserInfoId_Asc() {
        regOBA("userInfoId");
        return this;
    }

    public BsSearchLogCQ addOrderBy_UserInfoId_Desc() {
        regOBD("userInfoId");
        return this;
    }

    public void setUserSessionId_Equal(String userSessionId) {
        setUserSessionId_Term(userSessionId, null);
    }

    public void setUserSessionId_Equal(String userSessionId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUserSessionId_Term(userSessionId, opLambda);
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

    public void setUserSessionId_NotEqual(String userSessionId) {
        setUserSessionId_NotTerm(userSessionId, null);
    }

    public void setUserSessionId_NotEqual(String userSessionId, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setUserSessionId_NotTerm(userSessionId, opLambda);
    }

    public void setUserSessionId_NotTerm(String userSessionId) {
        setUserSessionId_NotTerm(userSessionId, null);
    }

    public void setUserSessionId_NotTerm(String userSessionId, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("userSessionId", userSessionId));
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
