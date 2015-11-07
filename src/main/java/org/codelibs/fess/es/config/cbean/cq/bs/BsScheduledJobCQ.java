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
package org.codelibs.fess.es.config.cbean.cq.bs;

import java.time.LocalDateTime;
import java.util.Collection;

import org.codelibs.fess.es.config.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.config.cbean.cq.ScheduledJobCQ;
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
public abstract class BsScheduledJobCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "scheduled_job";
    }

    @Override
    public String xgetAliasName() {
        return "scheduled_job";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void filtered(FilteredCall<ScheduledJobCQ, ScheduledJobCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<ScheduledJobCQ, ScheduledJobCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<ScheduledJobCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<ScheduledJobCQ> notLambda, ConditionOptionCall<NotQueryBuilder> opLambda) {
        ScheduledJobCQ notQuery = new ScheduledJobCQ();
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

    public void bool(BoolCall<ScheduledJobCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<ScheduledJobCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        ScheduledJobCQ mustQuery = new ScheduledJobCQ();
        ScheduledJobCQ shouldQuery = new ScheduledJobCQ();
        ScheduledJobCQ mustNotQuery = new ScheduledJobCQ();
        ScheduledJobCQ filterQuery = new ScheduledJobCQ();
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

    public BsScheduledJobCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    public BsScheduledJobCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setAvailable_Equal(Boolean available) {
        setAvailable_Term(available, null);
    }

    public void setAvailable_Equal(Boolean available, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setAvailable_Term(available, opLambda);
    }

    public void setAvailable_Term(Boolean available) {
        setAvailable_Term(available, null);
    }

    public void setAvailable_Term(Boolean available, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("available", available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_NotEqual(Boolean available) {
        setAvailable_NotTerm(available, null);
    }

    public void setAvailable_NotEqual(Boolean available, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setAvailable_NotTerm(available, opLambda);
    }

    public void setAvailable_NotTerm(Boolean available) {
        setAvailable_NotTerm(available, null);
    }

    public void setAvailable_NotTerm(Boolean available, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("available", available));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Terms(Collection<Boolean> availableList) {
        setAvailable_Terms(availableList, null);
    }

    public void setAvailable_Terms(Collection<Boolean> availableList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("available", availableList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_InScope(Collection<Boolean> availableList) {
        setAvailable_Terms(availableList, null);
    }

    public void setAvailable_InScope(Collection<Boolean> availableList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setAvailable_Terms(availableList, opLambda);
    }

    public void setAvailable_Match(Boolean available) {
        setAvailable_Match(available, null);
    }

    public void setAvailable_Match(Boolean available, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("available", available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_MatchPhrase(Boolean available) {
        setAvailable_MatchPhrase(available, null);
    }

    public void setAvailable_MatchPhrase(Boolean available, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("available", available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_MatchPhrasePrefix(Boolean available) {
        setAvailable_MatchPhrasePrefix(available, null);
    }

    public void setAvailable_MatchPhrasePrefix(Boolean available, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("available", available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Fuzzy(Boolean available) {
        setAvailable_Fuzzy(available, null);
    }

    public void setAvailable_Fuzzy(Boolean available, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("available", available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_GreaterThan(Boolean available) {
        setAvailable_GreaterThan(available, null);
    }

    public void setAvailable_GreaterThan(Boolean available, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("available", ConditionKey.CK_GREATER_THAN, available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_LessThan(Boolean available) {
        setAvailable_LessThan(available, null);
    }

    public void setAvailable_LessThan(Boolean available, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("available", ConditionKey.CK_LESS_THAN, available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_GreaterEqual(Boolean available) {
        setAvailable_GreaterEqual(available, null);
    }

    public void setAvailable_GreaterEqual(Boolean available, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("available", ConditionKey.CK_GREATER_EQUAL, available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_LessEqual(Boolean available) {
        setAvailable_LessEqual(available, null);
    }

    public void setAvailable_LessEqual(Boolean available, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("available", ConditionKey.CK_LESS_EQUAL, available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsScheduledJobCQ addOrderBy_Available_Asc() {
        regOBA("available");
        return this;
    }

    public BsScheduledJobCQ addOrderBy_Available_Desc() {
        regOBD("available");
        return this;
    }

    public void setCrawler_Equal(Boolean crawler) {
        setCrawler_Term(crawler, null);
    }

    public void setCrawler_Equal(Boolean crawler, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setCrawler_Term(crawler, opLambda);
    }

    public void setCrawler_Term(Boolean crawler) {
        setCrawler_Term(crawler, null);
    }

    public void setCrawler_Term(Boolean crawler, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("crawler", crawler);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_NotEqual(Boolean crawler) {
        setCrawler_NotTerm(crawler, null);
    }

    public void setCrawler_NotEqual(Boolean crawler, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setCrawler_NotTerm(crawler, opLambda);
    }

    public void setCrawler_NotTerm(Boolean crawler) {
        setCrawler_NotTerm(crawler, null);
    }

    public void setCrawler_NotTerm(Boolean crawler, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("crawler", crawler));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_Terms(Collection<Boolean> crawlerList) {
        setCrawler_Terms(crawlerList, null);
    }

    public void setCrawler_Terms(Collection<Boolean> crawlerList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("crawler", crawlerList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_InScope(Collection<Boolean> crawlerList) {
        setCrawler_Terms(crawlerList, null);
    }

    public void setCrawler_InScope(Collection<Boolean> crawlerList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setCrawler_Terms(crawlerList, opLambda);
    }

    public void setCrawler_Match(Boolean crawler) {
        setCrawler_Match(crawler, null);
    }

    public void setCrawler_Match(Boolean crawler, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("crawler", crawler);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_MatchPhrase(Boolean crawler) {
        setCrawler_MatchPhrase(crawler, null);
    }

    public void setCrawler_MatchPhrase(Boolean crawler, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("crawler", crawler);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_MatchPhrasePrefix(Boolean crawler) {
        setCrawler_MatchPhrasePrefix(crawler, null);
    }

    public void setCrawler_MatchPhrasePrefix(Boolean crawler, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("crawler", crawler);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_Fuzzy(Boolean crawler) {
        setCrawler_Fuzzy(crawler, null);
    }

    public void setCrawler_Fuzzy(Boolean crawler, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("crawler", crawler);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_GreaterThan(Boolean crawler) {
        setCrawler_GreaterThan(crawler, null);
    }

    public void setCrawler_GreaterThan(Boolean crawler, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("crawler", ConditionKey.CK_GREATER_THAN, crawler);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_LessThan(Boolean crawler) {
        setCrawler_LessThan(crawler, null);
    }

    public void setCrawler_LessThan(Boolean crawler, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("crawler", ConditionKey.CK_LESS_THAN, crawler);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_GreaterEqual(Boolean crawler) {
        setCrawler_GreaterEqual(crawler, null);
    }

    public void setCrawler_GreaterEqual(Boolean crawler, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("crawler", ConditionKey.CK_GREATER_EQUAL, crawler);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_LessEqual(Boolean crawler) {
        setCrawler_LessEqual(crawler, null);
    }

    public void setCrawler_LessEqual(Boolean crawler, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("crawler", ConditionKey.CK_LESS_EQUAL, crawler);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsScheduledJobCQ addOrderBy_Crawler_Asc() {
        regOBA("crawler");
        return this;
    }

    public BsScheduledJobCQ addOrderBy_Crawler_Desc() {
        regOBD("crawler");
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

    public void setCreatedBy_NotEqual(String createdBy) {
        setCreatedBy_NotTerm(createdBy, null);
    }

    public void setCreatedBy_NotEqual(String createdBy, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setCreatedBy_NotTerm(createdBy, opLambda);
    }

    public void setCreatedBy_NotTerm(String createdBy) {
        setCreatedBy_NotTerm(createdBy, null);
    }

    public void setCreatedBy_NotTerm(String createdBy, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("createdBy", createdBy));
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

    public BsScheduledJobCQ addOrderBy_CreatedBy_Asc() {
        regOBA("createdBy");
        return this;
    }

    public BsScheduledJobCQ addOrderBy_CreatedBy_Desc() {
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

    public void setCreatedTime_NotEqual(Long createdTime) {
        setCreatedTime_NotTerm(createdTime, null);
    }

    public void setCreatedTime_NotEqual(Long createdTime, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setCreatedTime_NotTerm(createdTime, opLambda);
    }

    public void setCreatedTime_NotTerm(Long createdTime) {
        setCreatedTime_NotTerm(createdTime, null);
    }

    public void setCreatedTime_NotTerm(Long createdTime, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("createdTime", createdTime));
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

    public BsScheduledJobCQ addOrderBy_CreatedTime_Asc() {
        regOBA("createdTime");
        return this;
    }

    public BsScheduledJobCQ addOrderBy_CreatedTime_Desc() {
        regOBD("createdTime");
        return this;
    }

    public void setCronExpression_Equal(String cronExpression) {
        setCronExpression_Term(cronExpression, null);
    }

    public void setCronExpression_Equal(String cronExpression, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setCronExpression_Term(cronExpression, opLambda);
    }

    public void setCronExpression_Term(String cronExpression) {
        setCronExpression_Term(cronExpression, null);
    }

    public void setCronExpression_Term(String cronExpression, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("cronExpression", cronExpression);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_NotEqual(String cronExpression) {
        setCronExpression_NotTerm(cronExpression, null);
    }

    public void setCronExpression_NotEqual(String cronExpression, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setCronExpression_NotTerm(cronExpression, opLambda);
    }

    public void setCronExpression_NotTerm(String cronExpression) {
        setCronExpression_NotTerm(cronExpression, null);
    }

    public void setCronExpression_NotTerm(String cronExpression, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("cronExpression", cronExpression));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_Terms(Collection<String> cronExpressionList) {
        setCronExpression_Terms(cronExpressionList, null);
    }

    public void setCronExpression_Terms(Collection<String> cronExpressionList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("cronExpression", cronExpressionList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_InScope(Collection<String> cronExpressionList) {
        setCronExpression_Terms(cronExpressionList, null);
    }

    public void setCronExpression_InScope(Collection<String> cronExpressionList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setCronExpression_Terms(cronExpressionList, opLambda);
    }

    public void setCronExpression_Match(String cronExpression) {
        setCronExpression_Match(cronExpression, null);
    }

    public void setCronExpression_Match(String cronExpression, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("cronExpression", cronExpression);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_MatchPhrase(String cronExpression) {
        setCronExpression_MatchPhrase(cronExpression, null);
    }

    public void setCronExpression_MatchPhrase(String cronExpression, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("cronExpression", cronExpression);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_MatchPhrasePrefix(String cronExpression) {
        setCronExpression_MatchPhrasePrefix(cronExpression, null);
    }

    public void setCronExpression_MatchPhrasePrefix(String cronExpression, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("cronExpression", cronExpression);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_Fuzzy(String cronExpression) {
        setCronExpression_Fuzzy(cronExpression, null);
    }

    public void setCronExpression_Fuzzy(String cronExpression, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("cronExpression", cronExpression);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_Prefix(String cronExpression) {
        setCronExpression_Prefix(cronExpression, null);
    }

    public void setCronExpression_Prefix(String cronExpression, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("cronExpression", cronExpression);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_GreaterThan(String cronExpression) {
        setCronExpression_GreaterThan(cronExpression, null);
    }

    public void setCronExpression_GreaterThan(String cronExpression, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("cronExpression", ConditionKey.CK_GREATER_THAN, cronExpression);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_LessThan(String cronExpression) {
        setCronExpression_LessThan(cronExpression, null);
    }

    public void setCronExpression_LessThan(String cronExpression, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("cronExpression", ConditionKey.CK_LESS_THAN, cronExpression);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_GreaterEqual(String cronExpression) {
        setCronExpression_GreaterEqual(cronExpression, null);
    }

    public void setCronExpression_GreaterEqual(String cronExpression, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("cronExpression", ConditionKey.CK_GREATER_EQUAL, cronExpression);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_LessEqual(String cronExpression) {
        setCronExpression_LessEqual(cronExpression, null);
    }

    public void setCronExpression_LessEqual(String cronExpression, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("cronExpression", ConditionKey.CK_LESS_EQUAL, cronExpression);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsScheduledJobCQ addOrderBy_CronExpression_Asc() {
        regOBA("cronExpression");
        return this;
    }

    public BsScheduledJobCQ addOrderBy_CronExpression_Desc() {
        regOBD("cronExpression");
        return this;
    }

    public void setJobLogging_Equal(Boolean jobLogging) {
        setJobLogging_Term(jobLogging, null);
    }

    public void setJobLogging_Equal(Boolean jobLogging, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setJobLogging_Term(jobLogging, opLambda);
    }

    public void setJobLogging_Term(Boolean jobLogging) {
        setJobLogging_Term(jobLogging, null);
    }

    public void setJobLogging_Term(Boolean jobLogging, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("jobLogging", jobLogging);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_NotEqual(Boolean jobLogging) {
        setJobLogging_NotTerm(jobLogging, null);
    }

    public void setJobLogging_NotEqual(Boolean jobLogging, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setJobLogging_NotTerm(jobLogging, opLambda);
    }

    public void setJobLogging_NotTerm(Boolean jobLogging) {
        setJobLogging_NotTerm(jobLogging, null);
    }

    public void setJobLogging_NotTerm(Boolean jobLogging, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("jobLogging", jobLogging));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_Terms(Collection<Boolean> jobLoggingList) {
        setJobLogging_Terms(jobLoggingList, null);
    }

    public void setJobLogging_Terms(Collection<Boolean> jobLoggingList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("jobLogging", jobLoggingList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_InScope(Collection<Boolean> jobLoggingList) {
        setJobLogging_Terms(jobLoggingList, null);
    }

    public void setJobLogging_InScope(Collection<Boolean> jobLoggingList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setJobLogging_Terms(jobLoggingList, opLambda);
    }

    public void setJobLogging_Match(Boolean jobLogging) {
        setJobLogging_Match(jobLogging, null);
    }

    public void setJobLogging_Match(Boolean jobLogging, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("jobLogging", jobLogging);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_MatchPhrase(Boolean jobLogging) {
        setJobLogging_MatchPhrase(jobLogging, null);
    }

    public void setJobLogging_MatchPhrase(Boolean jobLogging, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("jobLogging", jobLogging);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_MatchPhrasePrefix(Boolean jobLogging) {
        setJobLogging_MatchPhrasePrefix(jobLogging, null);
    }

    public void setJobLogging_MatchPhrasePrefix(Boolean jobLogging, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("jobLogging", jobLogging);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_Fuzzy(Boolean jobLogging) {
        setJobLogging_Fuzzy(jobLogging, null);
    }

    public void setJobLogging_Fuzzy(Boolean jobLogging, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("jobLogging", jobLogging);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_GreaterThan(Boolean jobLogging) {
        setJobLogging_GreaterThan(jobLogging, null);
    }

    public void setJobLogging_GreaterThan(Boolean jobLogging, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("jobLogging", ConditionKey.CK_GREATER_THAN, jobLogging);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_LessThan(Boolean jobLogging) {
        setJobLogging_LessThan(jobLogging, null);
    }

    public void setJobLogging_LessThan(Boolean jobLogging, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("jobLogging", ConditionKey.CK_LESS_THAN, jobLogging);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_GreaterEqual(Boolean jobLogging) {
        setJobLogging_GreaterEqual(jobLogging, null);
    }

    public void setJobLogging_GreaterEqual(Boolean jobLogging, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("jobLogging", ConditionKey.CK_GREATER_EQUAL, jobLogging);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_LessEqual(Boolean jobLogging) {
        setJobLogging_LessEqual(jobLogging, null);
    }

    public void setJobLogging_LessEqual(Boolean jobLogging, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("jobLogging", ConditionKey.CK_LESS_EQUAL, jobLogging);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsScheduledJobCQ addOrderBy_JobLogging_Asc() {
        regOBA("jobLogging");
        return this;
    }

    public BsScheduledJobCQ addOrderBy_JobLogging_Desc() {
        regOBD("jobLogging");
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

    public void setName_NotEqual(String name) {
        setName_NotTerm(name, null);
    }

    public void setName_NotEqual(String name, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setName_NotTerm(name, opLambda);
    }

    public void setName_NotTerm(String name) {
        setName_NotTerm(name, null);
    }

    public void setName_NotTerm(String name, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("name", name));
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

    public BsScheduledJobCQ addOrderBy_Name_Asc() {
        regOBA("name");
        return this;
    }

    public BsScheduledJobCQ addOrderBy_Name_Desc() {
        regOBD("name");
        return this;
    }

    public void setScriptData_Equal(String scriptData) {
        setScriptData_Term(scriptData, null);
    }

    public void setScriptData_Equal(String scriptData, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setScriptData_Term(scriptData, opLambda);
    }

    public void setScriptData_Term(String scriptData) {
        setScriptData_Term(scriptData, null);
    }

    public void setScriptData_Term(String scriptData, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_NotEqual(String scriptData) {
        setScriptData_NotTerm(scriptData, null);
    }

    public void setScriptData_NotEqual(String scriptData, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setScriptData_NotTerm(scriptData, opLambda);
    }

    public void setScriptData_NotTerm(String scriptData) {
        setScriptData_NotTerm(scriptData, null);
    }

    public void setScriptData_NotTerm(String scriptData, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("scriptData", scriptData));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Terms(Collection<String> scriptDataList) {
        setScriptData_Terms(scriptDataList, null);
    }

    public void setScriptData_Terms(Collection<String> scriptDataList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("scriptData", scriptDataList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_InScope(Collection<String> scriptDataList) {
        setScriptData_Terms(scriptDataList, null);
    }

    public void setScriptData_InScope(Collection<String> scriptDataList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setScriptData_Terms(scriptDataList, opLambda);
    }

    public void setScriptData_Match(String scriptData) {
        setScriptData_Match(scriptData, null);
    }

    public void setScriptData_Match(String scriptData, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_MatchPhrase(String scriptData) {
        setScriptData_MatchPhrase(scriptData, null);
    }

    public void setScriptData_MatchPhrase(String scriptData, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_MatchPhrasePrefix(String scriptData) {
        setScriptData_MatchPhrasePrefix(scriptData, null);
    }

    public void setScriptData_MatchPhrasePrefix(String scriptData, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Fuzzy(String scriptData) {
        setScriptData_Fuzzy(scriptData, null);
    }

    public void setScriptData_Fuzzy(String scriptData, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Prefix(String scriptData) {
        setScriptData_Prefix(scriptData, null);
    }

    public void setScriptData_Prefix(String scriptData, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("scriptData", scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_GreaterThan(String scriptData) {
        setScriptData_GreaterThan(scriptData, null);
    }

    public void setScriptData_GreaterThan(String scriptData, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("scriptData", ConditionKey.CK_GREATER_THAN, scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_LessThan(String scriptData) {
        setScriptData_LessThan(scriptData, null);
    }

    public void setScriptData_LessThan(String scriptData, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("scriptData", ConditionKey.CK_LESS_THAN, scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_GreaterEqual(String scriptData) {
        setScriptData_GreaterEqual(scriptData, null);
    }

    public void setScriptData_GreaterEqual(String scriptData, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("scriptData", ConditionKey.CK_GREATER_EQUAL, scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_LessEqual(String scriptData) {
        setScriptData_LessEqual(scriptData, null);
    }

    public void setScriptData_LessEqual(String scriptData, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("scriptData", ConditionKey.CK_LESS_EQUAL, scriptData);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsScheduledJobCQ addOrderBy_ScriptData_Asc() {
        regOBA("scriptData");
        return this;
    }

    public BsScheduledJobCQ addOrderBy_ScriptData_Desc() {
        regOBD("scriptData");
        return this;
    }

    public void setScriptType_Equal(String scriptType) {
        setScriptType_Term(scriptType, null);
    }

    public void setScriptType_Equal(String scriptType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setScriptType_Term(scriptType, opLambda);
    }

    public void setScriptType_Term(String scriptType) {
        setScriptType_Term(scriptType, null);
    }

    public void setScriptType_Term(String scriptType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_NotEqual(String scriptType) {
        setScriptType_NotTerm(scriptType, null);
    }

    public void setScriptType_NotEqual(String scriptType, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setScriptType_NotTerm(scriptType, opLambda);
    }

    public void setScriptType_NotTerm(String scriptType) {
        setScriptType_NotTerm(scriptType, null);
    }

    public void setScriptType_NotTerm(String scriptType, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("scriptType", scriptType));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Terms(Collection<String> scriptTypeList) {
        setScriptType_Terms(scriptTypeList, null);
    }

    public void setScriptType_Terms(Collection<String> scriptTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("scriptType", scriptTypeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_InScope(Collection<String> scriptTypeList) {
        setScriptType_Terms(scriptTypeList, null);
    }

    public void setScriptType_InScope(Collection<String> scriptTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setScriptType_Terms(scriptTypeList, opLambda);
    }

    public void setScriptType_Match(String scriptType) {
        setScriptType_Match(scriptType, null);
    }

    public void setScriptType_Match(String scriptType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_MatchPhrase(String scriptType) {
        setScriptType_MatchPhrase(scriptType, null);
    }

    public void setScriptType_MatchPhrase(String scriptType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_MatchPhrasePrefix(String scriptType) {
        setScriptType_MatchPhrasePrefix(scriptType, null);
    }

    public void setScriptType_MatchPhrasePrefix(String scriptType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Fuzzy(String scriptType) {
        setScriptType_Fuzzy(scriptType, null);
    }

    public void setScriptType_Fuzzy(String scriptType, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Prefix(String scriptType) {
        setScriptType_Prefix(scriptType, null);
    }

    public void setScriptType_Prefix(String scriptType, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("scriptType", scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_GreaterThan(String scriptType) {
        setScriptType_GreaterThan(scriptType, null);
    }

    public void setScriptType_GreaterThan(String scriptType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("scriptType", ConditionKey.CK_GREATER_THAN, scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_LessThan(String scriptType) {
        setScriptType_LessThan(scriptType, null);
    }

    public void setScriptType_LessThan(String scriptType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("scriptType", ConditionKey.CK_LESS_THAN, scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_GreaterEqual(String scriptType) {
        setScriptType_GreaterEqual(scriptType, null);
    }

    public void setScriptType_GreaterEqual(String scriptType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("scriptType", ConditionKey.CK_GREATER_EQUAL, scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_LessEqual(String scriptType) {
        setScriptType_LessEqual(scriptType, null);
    }

    public void setScriptType_LessEqual(String scriptType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("scriptType", ConditionKey.CK_LESS_EQUAL, scriptType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsScheduledJobCQ addOrderBy_ScriptType_Asc() {
        regOBA("scriptType");
        return this;
    }

    public BsScheduledJobCQ addOrderBy_ScriptType_Desc() {
        regOBD("scriptType");
        return this;
    }

    public void setSortOrder_Equal(Integer sortOrder) {
        setSortOrder_Term(sortOrder, null);
    }

    public void setSortOrder_Equal(Integer sortOrder, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setSortOrder_Term(sortOrder, opLambda);
    }

    public void setSortOrder_Term(Integer sortOrder) {
        setSortOrder_Term(sortOrder, null);
    }

    public void setSortOrder_Term(Integer sortOrder, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("sortOrder", sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_NotEqual(Integer sortOrder) {
        setSortOrder_NotTerm(sortOrder, null);
    }

    public void setSortOrder_NotEqual(Integer sortOrder, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setSortOrder_NotTerm(sortOrder, opLambda);
    }

    public void setSortOrder_NotTerm(Integer sortOrder) {
        setSortOrder_NotTerm(sortOrder, null);
    }

    public void setSortOrder_NotTerm(Integer sortOrder, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("sortOrder", sortOrder));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Terms(Collection<Integer> sortOrderList) {
        setSortOrder_Terms(sortOrderList, null);
    }

    public void setSortOrder_Terms(Collection<Integer> sortOrderList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("sortOrder", sortOrderList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_InScope(Collection<Integer> sortOrderList) {
        setSortOrder_Terms(sortOrderList, null);
    }

    public void setSortOrder_InScope(Collection<Integer> sortOrderList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setSortOrder_Terms(sortOrderList, opLambda);
    }

    public void setSortOrder_Match(Integer sortOrder) {
        setSortOrder_Match(sortOrder, null);
    }

    public void setSortOrder_Match(Integer sortOrder, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("sortOrder", sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_MatchPhrase(Integer sortOrder) {
        setSortOrder_MatchPhrase(sortOrder, null);
    }

    public void setSortOrder_MatchPhrase(Integer sortOrder, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("sortOrder", sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_MatchPhrasePrefix(Integer sortOrder) {
        setSortOrder_MatchPhrasePrefix(sortOrder, null);
    }

    public void setSortOrder_MatchPhrasePrefix(Integer sortOrder, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("sortOrder", sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Fuzzy(Integer sortOrder) {
        setSortOrder_Fuzzy(sortOrder, null);
    }

    public void setSortOrder_Fuzzy(Integer sortOrder, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("sortOrder", sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_GreaterThan(Integer sortOrder) {
        setSortOrder_GreaterThan(sortOrder, null);
    }

    public void setSortOrder_GreaterThan(Integer sortOrder, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("sortOrder", ConditionKey.CK_GREATER_THAN, sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_LessThan(Integer sortOrder) {
        setSortOrder_LessThan(sortOrder, null);
    }

    public void setSortOrder_LessThan(Integer sortOrder, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("sortOrder", ConditionKey.CK_LESS_THAN, sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_GreaterEqual(Integer sortOrder) {
        setSortOrder_GreaterEqual(sortOrder, null);
    }

    public void setSortOrder_GreaterEqual(Integer sortOrder, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("sortOrder", ConditionKey.CK_GREATER_EQUAL, sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_LessEqual(Integer sortOrder) {
        setSortOrder_LessEqual(sortOrder, null);
    }

    public void setSortOrder_LessEqual(Integer sortOrder, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("sortOrder", ConditionKey.CK_LESS_EQUAL, sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsScheduledJobCQ addOrderBy_SortOrder_Asc() {
        regOBA("sortOrder");
        return this;
    }

    public BsScheduledJobCQ addOrderBy_SortOrder_Desc() {
        regOBD("sortOrder");
        return this;
    }

    public void setTarget_Equal(String target) {
        setTarget_Term(target, null);
    }

    public void setTarget_Equal(String target, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setTarget_Term(target, opLambda);
    }

    public void setTarget_Term(String target) {
        setTarget_Term(target, null);
    }

    public void setTarget_Term(String target, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_NotEqual(String target) {
        setTarget_NotTerm(target, null);
    }

    public void setTarget_NotEqual(String target, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setTarget_NotTerm(target, opLambda);
    }

    public void setTarget_NotTerm(String target) {
        setTarget_NotTerm(target, null);
    }

    public void setTarget_NotTerm(String target, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("target", target));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Terms(Collection<String> targetList) {
        setTarget_Terms(targetList, null);
    }

    public void setTarget_Terms(Collection<String> targetList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("target", targetList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_InScope(Collection<String> targetList) {
        setTarget_Terms(targetList, null);
    }

    public void setTarget_InScope(Collection<String> targetList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setTarget_Terms(targetList, opLambda);
    }

    public void setTarget_Match(String target) {
        setTarget_Match(target, null);
    }

    public void setTarget_Match(String target, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_MatchPhrase(String target) {
        setTarget_MatchPhrase(target, null);
    }

    public void setTarget_MatchPhrase(String target, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_MatchPhrasePrefix(String target) {
        setTarget_MatchPhrasePrefix(target, null);
    }

    public void setTarget_MatchPhrasePrefix(String target, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Fuzzy(String target) {
        setTarget_Fuzzy(target, null);
    }

    public void setTarget_Fuzzy(String target, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Prefix(String target) {
        setTarget_Prefix(target, null);
    }

    public void setTarget_Prefix(String target, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("target", target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_GreaterThan(String target) {
        setTarget_GreaterThan(target, null);
    }

    public void setTarget_GreaterThan(String target, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("target", ConditionKey.CK_GREATER_THAN, target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_LessThan(String target) {
        setTarget_LessThan(target, null);
    }

    public void setTarget_LessThan(String target, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("target", ConditionKey.CK_LESS_THAN, target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_GreaterEqual(String target) {
        setTarget_GreaterEqual(target, null);
    }

    public void setTarget_GreaterEqual(String target, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("target", ConditionKey.CK_GREATER_EQUAL, target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_LessEqual(String target) {
        setTarget_LessEqual(target, null);
    }

    public void setTarget_LessEqual(String target, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("target", ConditionKey.CK_LESS_EQUAL, target);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsScheduledJobCQ addOrderBy_Target_Asc() {
        regOBA("target");
        return this;
    }

    public BsScheduledJobCQ addOrderBy_Target_Desc() {
        regOBD("target");
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

    public void setUpdatedBy_NotEqual(String updatedBy) {
        setUpdatedBy_NotTerm(updatedBy, null);
    }

    public void setUpdatedBy_NotEqual(String updatedBy, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setUpdatedBy_NotTerm(updatedBy, opLambda);
    }

    public void setUpdatedBy_NotTerm(String updatedBy) {
        setUpdatedBy_NotTerm(updatedBy, null);
    }

    public void setUpdatedBy_NotTerm(String updatedBy, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("updatedBy", updatedBy));
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

    public BsScheduledJobCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("updatedBy");
        return this;
    }

    public BsScheduledJobCQ addOrderBy_UpdatedBy_Desc() {
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

    public void setUpdatedTime_NotEqual(Long updatedTime) {
        setUpdatedTime_NotTerm(updatedTime, null);
    }

    public void setUpdatedTime_NotEqual(Long updatedTime, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setUpdatedTime_NotTerm(updatedTime, opLambda);
    }

    public void setUpdatedTime_NotTerm(Long updatedTime) {
        setUpdatedTime_NotTerm(updatedTime, null);
    }

    public void setUpdatedTime_NotTerm(Long updatedTime, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("updatedTime", updatedTime));
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

    public BsScheduledJobCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("updatedTime");
        return this;
    }

    public BsScheduledJobCQ addOrderBy_UpdatedTime_Desc() {
        regOBD("updatedTime");
        return this;
    }

}
