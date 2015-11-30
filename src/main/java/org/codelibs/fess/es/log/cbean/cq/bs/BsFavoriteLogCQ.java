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
import org.codelibs.fess.es.log.cbean.cq.FavoriteLogCQ;
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
public abstract class BsFavoriteLogCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "favorite_log";
    }

    @Override
    public String xgetAliasName() {
        return "favorite_log";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void filtered(FilteredCall<FavoriteLogCQ, FavoriteLogCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<FavoriteLogCQ, FavoriteLogCQ> filteredLambda,
            ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter)->{
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<FavoriteLogCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<FavoriteLogCQ> notLambda, ConditionOptionCall<NotQueryBuilder> opLambda) {
        FavoriteLogCQ notQuery = new FavoriteLogCQ();
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

    public void bool(BoolCall<FavoriteLogCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<FavoriteLogCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        FavoriteLogCQ mustQuery = new FavoriteLogCQ();
        FavoriteLogCQ shouldQuery = new FavoriteLogCQ();
        FavoriteLogCQ mustNotQuery = new FavoriteLogCQ();
        FavoriteLogCQ filterQuery = new FavoriteLogCQ();
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

    public BsFavoriteLogCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    public BsFavoriteLogCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setCreatedAt_Equal(LocalDateTime createdAt) {
        setCreatedAt_Term(createdAt, null);
    }

    public void setCreatedAt_Equal(LocalDateTime createdAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setCreatedAt_Term(createdAt, opLambda);
    }

    public void setCreatedAt_Term(LocalDateTime createdAt) {
        setCreatedAt_Term(createdAt, null);
    }

    public void setCreatedAt_Term(LocalDateTime createdAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_NotEqual(LocalDateTime createdAt) {
        setCreatedAt_NotTerm(createdAt, null);
    }

    public void setCreatedAt_NotEqual(LocalDateTime createdAt, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setCreatedAt_NotTerm(createdAt, opLambda);
    }

    public void setCreatedAt_NotTerm(LocalDateTime createdAt) {
        setCreatedAt_NotTerm(createdAt, null);
    }

    public void setCreatedAt_NotTerm(LocalDateTime createdAt, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("createdAt", createdAt));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Terms(Collection<LocalDateTime> createdAtList) {
        setCreatedAt_Terms(createdAtList, null);
    }

    public void setCreatedAt_Terms(Collection<LocalDateTime> createdAtList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("createdAt", createdAtList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_InScope(Collection<LocalDateTime> createdAtList) {
        setCreatedAt_Terms(createdAtList, null);
    }

    public void setCreatedAt_InScope(Collection<LocalDateTime> createdAtList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setCreatedAt_Terms(createdAtList, opLambda);
    }

    public void setCreatedAt_Match(LocalDateTime createdAt) {
        setCreatedAt_Match(createdAt, null);
    }

    public void setCreatedAt_Match(LocalDateTime createdAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_MatchPhrase(LocalDateTime createdAt) {
        setCreatedAt_MatchPhrase(createdAt, null);
    }

    public void setCreatedAt_MatchPhrase(LocalDateTime createdAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_MatchPhrasePrefix(LocalDateTime createdAt) {
        setCreatedAt_MatchPhrasePrefix(createdAt, null);
    }

    public void setCreatedAt_MatchPhrasePrefix(LocalDateTime createdAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Fuzzy(LocalDateTime createdAt) {
        setCreatedAt_Fuzzy(createdAt, null);
    }

    public void setCreatedAt_Fuzzy(LocalDateTime createdAt, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_GreaterThan(LocalDateTime createdAt) {
        setCreatedAt_GreaterThan(createdAt, null);
    }

    public void setCreatedAt_GreaterThan(LocalDateTime createdAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdAt", ConditionKey.CK_GREATER_THAN, createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_LessThan(LocalDateTime createdAt) {
        setCreatedAt_LessThan(createdAt, null);
    }

    public void setCreatedAt_LessThan(LocalDateTime createdAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdAt", ConditionKey.CK_LESS_THAN, createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_GreaterEqual(LocalDateTime createdAt) {
        setCreatedAt_GreaterEqual(createdAt, null);
    }

    public void setCreatedAt_GreaterEqual(LocalDateTime createdAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdAt", ConditionKey.CK_GREATER_EQUAL, createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_LessEqual(LocalDateTime createdAt) {
        setCreatedAt_LessEqual(createdAt, null);
    }

    public void setCreatedAt_LessEqual(LocalDateTime createdAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdAt", ConditionKey.CK_LESS_EQUAL, createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFavoriteLogCQ addOrderBy_CreatedAt_Asc() {
        regOBA("createdAt");
        return this;
    }

    public BsFavoriteLogCQ addOrderBy_CreatedAt_Desc() {
        regOBD("createdAt");
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

    public void setUrl_NotEqual(String url) {
        setUrl_NotTerm(url, null);
    }

    public void setUrl_NotEqual(String url, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setUrl_NotTerm(url, opLambda);
    }

    public void setUrl_NotTerm(String url) {
        setUrl_NotTerm(url, null);
    }

    public void setUrl_NotTerm(String url, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("url", url));
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

    public BsFavoriteLogCQ addOrderBy_Url_Asc() {
        regOBA("url");
        return this;
    }

    public BsFavoriteLogCQ addOrderBy_Url_Desc() {
        regOBD("url");
        return this;
    }

    public void setDocId_Equal(String docId) {
        setDocId_Term(docId, null);
    }

    public void setDocId_Equal(String docId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setDocId_Term(docId, opLambda);
    }

    public void setDocId_Term(String docId) {
        setDocId_Term(docId, null);
    }

    public void setDocId_Term(String docId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_NotEqual(String docId) {
        setDocId_NotTerm(docId, null);
    }

    public void setDocId_NotEqual(String docId, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setDocId_NotTerm(docId, opLambda);
    }

    public void setDocId_NotTerm(String docId) {
        setDocId_NotTerm(docId, null);
    }

    public void setDocId_NotTerm(String docId, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("docId", docId));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_Terms(Collection<String> docIdList) {
        setDocId_Terms(docIdList, null);
    }

    public void setDocId_Terms(Collection<String> docIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("docId", docIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_InScope(Collection<String> docIdList) {
        setDocId_Terms(docIdList, null);
    }

    public void setDocId_InScope(Collection<String> docIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setDocId_Terms(docIdList, opLambda);
    }

    public void setDocId_Match(String docId) {
        setDocId_Match(docId, null);
    }

    public void setDocId_Match(String docId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_MatchPhrase(String docId) {
        setDocId_MatchPhrase(docId, null);
    }

    public void setDocId_MatchPhrase(String docId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_MatchPhrasePrefix(String docId) {
        setDocId_MatchPhrasePrefix(docId, null);
    }

    public void setDocId_MatchPhrasePrefix(String docId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_Fuzzy(String docId) {
        setDocId_Fuzzy(docId, null);
    }

    public void setDocId_Fuzzy(String docId, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_Prefix(String docId) {
        setDocId_Prefix(docId, null);
    }

    public void setDocId_Prefix(String docId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_GreaterThan(String docId) {
        setDocId_GreaterThan(docId, null);
    }

    public void setDocId_GreaterThan(String docId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("docId", ConditionKey.CK_GREATER_THAN, docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_LessThan(String docId) {
        setDocId_LessThan(docId, null);
    }

    public void setDocId_LessThan(String docId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("docId", ConditionKey.CK_LESS_THAN, docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_GreaterEqual(String docId) {
        setDocId_GreaterEqual(docId, null);
    }

    public void setDocId_GreaterEqual(String docId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("docId", ConditionKey.CK_GREATER_EQUAL, docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_LessEqual(String docId) {
        setDocId_LessEqual(docId, null);
    }

    public void setDocId_LessEqual(String docId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("docId", ConditionKey.CK_LESS_EQUAL, docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFavoriteLogCQ addOrderBy_DocId_Asc() {
        regOBA("docId");
        return this;
    }

    public BsFavoriteLogCQ addOrderBy_DocId_Desc() {
        regOBD("docId");
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

    public BsFavoriteLogCQ addOrderBy_QueryId_Asc() {
        regOBA("queryId");
        return this;
    }

    public BsFavoriteLogCQ addOrderBy_QueryId_Desc() {
        regOBD("queryId");
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

    public BsFavoriteLogCQ addOrderBy_UserInfoId_Asc() {
        regOBA("userInfoId");
        return this;
    }

    public BsFavoriteLogCQ addOrderBy_UserInfoId_Desc() {
        regOBD("userInfoId");
        return this;
    }

}
