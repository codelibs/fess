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

import java.time.LocalDateTime;
import java.util.Collection;

import org.codelibs.fess.es.log.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.log.cbean.cq.ClickLogCQ;
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
public abstract class BsClickLogCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "click_log";
    }

    @Override
    public String xgetAliasName() {
        return "click_log";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void filtered(FilteredCall<ClickLogCQ, ClickLogCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<ClickLogCQ, ClickLogCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<ClickLogCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<ClickLogCQ> notLambda, ConditionOptionCall<NotQueryBuilder> opLambda) {
        ClickLogCQ notQuery = new ClickLogCQ();
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

    public void bool(BoolCall<ClickLogCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<ClickLogCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        ClickLogCQ mustQuery = new ClickLogCQ();
        ClickLogCQ shouldQuery = new ClickLogCQ();
        ClickLogCQ mustNotQuery = new ClickLogCQ();
        ClickLogCQ filterQuery = new ClickLogCQ();
        boolLambda.callback(mustQuery, shouldQuery, mustNotQuery, filterQuery);
        if (mustQuery.hasQueries() || shouldQuery.hasQueries() || mustNotQuery.hasQueries() || filterQuery.hasQueries()) {
            BoolQueryBuilder builder =
                    regBoolCQ(mustQuery.getQueryBuilderList(), shouldQuery.getQueryBuilderList(), mustNotQuery.getQueryBuilderList(),
                            filterQuery.getQueryBuilderList());
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

    public BsClickLogCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    public BsClickLogCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setQueryRequestedAt_Equal(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_Term(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_Equal(LocalDateTime queryRequestedAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setQueryRequestedAt_Term(queryRequestedAt, opLambda);
    }

    public void setQueryRequestedAt_Term(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_Term(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_Term(LocalDateTime queryRequestedAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("queryRequestedAt", queryRequestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_NotEqual(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_NotTerm(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_NotEqual(LocalDateTime queryRequestedAt, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setQueryRequestedAt_NotTerm(queryRequestedAt, opLambda);
    }

    public void setQueryRequestedAt_NotTerm(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_NotTerm(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_NotTerm(LocalDateTime queryRequestedAt, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("queryRequestedAt", queryRequestedAt));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_Terms(Collection<LocalDateTime> queryRequestedAtList) {
        setQueryRequestedAt_Terms(queryRequestedAtList, null);
    }

    public void setQueryRequestedAt_Terms(Collection<LocalDateTime> queryRequestedAtList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("queryRequestedAt", queryRequestedAtList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_InScope(Collection<LocalDateTime> queryRequestedAtList) {
        setQueryRequestedAt_Terms(queryRequestedAtList, null);
    }

    public void setQueryRequestedAt_InScope(Collection<LocalDateTime> queryRequestedAtList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setQueryRequestedAt_Terms(queryRequestedAtList, opLambda);
    }

    public void setQueryRequestedAt_Match(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_Match(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_Match(LocalDateTime queryRequestedAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("queryRequestedAt", queryRequestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_MatchPhrase(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_MatchPhrase(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_MatchPhrase(LocalDateTime queryRequestedAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("queryRequestedAt", queryRequestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_MatchPhrasePrefix(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_MatchPhrasePrefix(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_MatchPhrasePrefix(LocalDateTime queryRequestedAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("queryRequestedAt", queryRequestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_Fuzzy(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_Fuzzy(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_Fuzzy(LocalDateTime queryRequestedAt, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("queryRequestedAt", queryRequestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_GreaterThan(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_GreaterThan(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_GreaterThan(LocalDateTime queryRequestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryRequestedAt", ConditionKey.CK_GREATER_THAN, queryRequestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_LessThan(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_LessThan(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_LessThan(LocalDateTime queryRequestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryRequestedAt", ConditionKey.CK_LESS_THAN, queryRequestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_GreaterEqual(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_GreaterEqual(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_GreaterEqual(LocalDateTime queryRequestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryRequestedAt", ConditionKey.CK_GREATER_EQUAL, queryRequestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_LessEqual(LocalDateTime queryRequestedAt) {
        setQueryRequestedAt_LessEqual(queryRequestedAt, null);
    }

    public void setQueryRequestedAt_LessEqual(LocalDateTime queryRequestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("queryRequestedAt", ConditionKey.CK_LESS_EQUAL, queryRequestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsClickLogCQ addOrderBy_QueryRequestedAt_Asc() {
        regOBA("queryRequestedAt");
        return this;
    }

    public BsClickLogCQ addOrderBy_QueryRequestedAt_Desc() {
        regOBD("queryRequestedAt");
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

    public BsClickLogCQ addOrderBy_RequestedAt_Asc() {
        regOBA("requestedAt");
        return this;
    }

    public BsClickLogCQ addOrderBy_RequestedAt_Desc() {
        regOBD("requestedAt");
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

    public BsClickLogCQ addOrderBy_QueryId_Asc() {
        regOBA("queryId");
        return this;
    }

    public BsClickLogCQ addOrderBy_QueryId_Desc() {
        regOBD("queryId");
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

    public BsClickLogCQ addOrderBy_DocId_Asc() {
        regOBA("docId");
        return this;
    }

    public BsClickLogCQ addOrderBy_DocId_Desc() {
        regOBD("docId");
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

    public BsClickLogCQ addOrderBy_UserSessionId_Asc() {
        regOBA("userSessionId");
        return this;
    }

    public BsClickLogCQ addOrderBy_UserSessionId_Desc() {
        regOBD("userSessionId");
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

    public BsClickLogCQ addOrderBy_Url_Asc() {
        regOBA("url");
        return this;
    }

    public BsClickLogCQ addOrderBy_Url_Desc() {
        regOBD("url");
        return this;
    }

    public void setOrder_Equal(Integer order) {
        setOrder_Term(order, null);
    }

    public void setOrder_Equal(Integer order, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setOrder_Term(order, opLambda);
    }

    public void setOrder_Term(Integer order) {
        setOrder_Term(order, null);
    }

    public void setOrder_Term(Integer order, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("order", order);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_NotEqual(Integer order) {
        setOrder_NotTerm(order, null);
    }

    public void setOrder_NotEqual(Integer order, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setOrder_NotTerm(order, opLambda);
    }

    public void setOrder_NotTerm(Integer order) {
        setOrder_NotTerm(order, null);
    }

    public void setOrder_NotTerm(Integer order, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("order", order));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Terms(Collection<Integer> orderList) {
        setOrder_Terms(orderList, null);
    }

    public void setOrder_Terms(Collection<Integer> orderList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("order", orderList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_InScope(Collection<Integer> orderList) {
        setOrder_Terms(orderList, null);
    }

    public void setOrder_InScope(Collection<Integer> orderList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setOrder_Terms(orderList, opLambda);
    }

    public void setOrder_Match(Integer order) {
        setOrder_Match(order, null);
    }

    public void setOrder_Match(Integer order, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("order", order);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_MatchPhrase(Integer order) {
        setOrder_MatchPhrase(order, null);
    }

    public void setOrder_MatchPhrase(Integer order, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("order", order);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_MatchPhrasePrefix(Integer order) {
        setOrder_MatchPhrasePrefix(order, null);
    }

    public void setOrder_MatchPhrasePrefix(Integer order, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("order", order);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Fuzzy(Integer order) {
        setOrder_Fuzzy(order, null);
    }

    public void setOrder_Fuzzy(Integer order, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("order", order);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_GreaterThan(Integer order) {
        setOrder_GreaterThan(order, null);
    }

    public void setOrder_GreaterThan(Integer order, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("order", ConditionKey.CK_GREATER_THAN, order);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_LessThan(Integer order) {
        setOrder_LessThan(order, null);
    }

    public void setOrder_LessThan(Integer order, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("order", ConditionKey.CK_LESS_THAN, order);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_GreaterEqual(Integer order) {
        setOrder_GreaterEqual(order, null);
    }

    public void setOrder_GreaterEqual(Integer order, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("order", ConditionKey.CK_GREATER_EQUAL, order);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_LessEqual(Integer order) {
        setOrder_LessEqual(order, null);
    }

    public void setOrder_LessEqual(Integer order, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("order", ConditionKey.CK_LESS_EQUAL, order);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsClickLogCQ addOrderBy_Order_Asc() {
        regOBA("order");
        return this;
    }

    public BsClickLogCQ addOrderBy_Order_Desc() {
        regOBD("order");
        return this;
    }

}
