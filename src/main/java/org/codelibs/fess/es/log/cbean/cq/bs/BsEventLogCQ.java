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
import org.codelibs.fess.es.log.cbean.cf.EventLogCF;
import org.codelibs.fess.es.log.cbean.cq.EventLogCQ;
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
 * @author ESFlute (using FreeGen)
 */
public abstract class BsEventLogCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "event_log";
    }

    @Override
    public String xgetAliasName() {
        return "event_log";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void filtered(FilteredCall<EventLogCQ, EventLogCF> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<EventLogCQ, EventLogCF> filteredLambda, ConditionOptionCall<FilteredQueryBuilder> opLambda) {
        EventLogCQ query = new EventLogCQ();
        EventLogCF filter = new EventLogCF();
        filteredLambda.callback(query, filter);
        if (query.hasQueries()) {
            FilteredQueryBuilder builder = regFilteredQ(query.getQuery(), filter.getFilter());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void bool(BoolCall<EventLogCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<EventLogCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        EventLogCQ mustQuery = new EventLogCQ();
        EventLogCQ shouldQuery = new EventLogCQ();
        EventLogCQ mustNotQuery = new EventLogCQ();
        boolLambda.callback(mustQuery, shouldQuery, mustNotQuery);
        if (mustQuery.hasQueries() || shouldQuery.hasQueries() || mustNotQuery.hasQueries()) {
            BoolQueryBuilder builder = regBoolCQ(mustQuery.queryBuilderList, shouldQuery.queryBuilderList, mustNotQuery.queryBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    // ===================================================================================
    //                                                                           Query Set
    //                                                                           =========
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

    public BsEventLogCQ addOrderBy_CreatedAt_Asc() {
        regOBA("createdAt");
        return this;
    }

    public BsEventLogCQ addOrderBy_CreatedAt_Desc() {
        regOBD("createdAt");
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

    public BsEventLogCQ addOrderBy_CreatedBy_Asc() {
        regOBA("createdBy");
        return this;
    }

    public BsEventLogCQ addOrderBy_CreatedBy_Desc() {
        regOBD("createdBy");
        return this;
    }

    public void setEventType_Equal(String eventType) {
        setEventType_Term(eventType, null);
    }

    public void setEventType_Equal(String eventType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setEventType_Term(eventType, opLambda);
    }

    public void setEventType_Term(String eventType) {
        setEventType_Term(eventType, null);
    }

    public void setEventType_Term(String eventType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("eventType", eventType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEventType_Terms(Collection<String> eventTypeList) {
        setEventType_Terms(eventTypeList, null);
    }

    public void setEventType_Terms(Collection<String> eventTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("eventType", eventTypeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEventType_InScope(Collection<String> eventTypeList) {
        setEventType_Terms(eventTypeList, null);
    }

    public void setEventType_InScope(Collection<String> eventTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setEventType_Terms(eventTypeList, opLambda);
    }

    public void setEventType_Match(String eventType) {
        setEventType_Match(eventType, null);
    }

    public void setEventType_Match(String eventType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("eventType", eventType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEventType_MatchPhrase(String eventType) {
        setEventType_MatchPhrase(eventType, null);
    }

    public void setEventType_MatchPhrase(String eventType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("eventType", eventType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEventType_MatchPhrasePrefix(String eventType) {
        setEventType_MatchPhrasePrefix(eventType, null);
    }

    public void setEventType_MatchPhrasePrefix(String eventType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("eventType", eventType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEventType_Fuzzy(String eventType) {
        setEventType_Fuzzy(eventType, null);
    }

    public void setEventType_Fuzzy(String eventType, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("eventType", eventType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEventType_Prefix(String eventType) {
        setEventType_Prefix(eventType, null);
    }

    public void setEventType_Prefix(String eventType, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("eventType", eventType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEventType_GreaterThan(String eventType) {
        setEventType_GreaterThan(eventType, null);
    }

    public void setEventType_GreaterThan(String eventType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("eventType", ConditionKey.CK_GREATER_THAN, eventType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEventType_LessThan(String eventType) {
        setEventType_LessThan(eventType, null);
    }

    public void setEventType_LessThan(String eventType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("eventType", ConditionKey.CK_LESS_THAN, eventType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEventType_GreaterEqual(String eventType) {
        setEventType_GreaterEqual(eventType, null);
    }

    public void setEventType_GreaterEqual(String eventType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("eventType", ConditionKey.CK_GREATER_EQUAL, eventType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEventType_LessEqual(String eventType) {
        setEventType_LessEqual(eventType, null);
    }

    public void setEventType_LessEqual(String eventType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("eventType", ConditionKey.CK_LESS_EQUAL, eventType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsEventLogCQ addOrderBy_EventType_Asc() {
        regOBA("eventType");
        return this;
    }

    public BsEventLogCQ addOrderBy_EventType_Desc() {
        regOBD("eventType");
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

    public BsEventLogCQ addOrderBy_Id_Asc() {
        regOBA("id");
        return this;
    }

    public BsEventLogCQ addOrderBy_Id_Desc() {
        regOBD("id");
        return this;
    }

    public void setMessage_Equal(String message) {
        setMessage_Term(message, null);
    }

    public void setMessage_Equal(String message, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setMessage_Term(message, opLambda);
    }

    public void setMessage_Term(String message) {
        setMessage_Term(message, null);
    }

    public void setMessage_Term(String message, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("message", message);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMessage_Terms(Collection<String> messageList) {
        setMessage_Terms(messageList, null);
    }

    public void setMessage_Terms(Collection<String> messageList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("message", messageList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMessage_InScope(Collection<String> messageList) {
        setMessage_Terms(messageList, null);
    }

    public void setMessage_InScope(Collection<String> messageList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setMessage_Terms(messageList, opLambda);
    }

    public void setMessage_Match(String message) {
        setMessage_Match(message, null);
    }

    public void setMessage_Match(String message, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("message", message);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMessage_MatchPhrase(String message) {
        setMessage_MatchPhrase(message, null);
    }

    public void setMessage_MatchPhrase(String message, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("message", message);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMessage_MatchPhrasePrefix(String message) {
        setMessage_MatchPhrasePrefix(message, null);
    }

    public void setMessage_MatchPhrasePrefix(String message, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("message", message);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMessage_Fuzzy(String message) {
        setMessage_Fuzzy(message, null);
    }

    public void setMessage_Fuzzy(String message, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("message", message);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMessage_Prefix(String message) {
        setMessage_Prefix(message, null);
    }

    public void setMessage_Prefix(String message, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("message", message);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMessage_GreaterThan(String message) {
        setMessage_GreaterThan(message, null);
    }

    public void setMessage_GreaterThan(String message, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("message", ConditionKey.CK_GREATER_THAN, message);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMessage_LessThan(String message) {
        setMessage_LessThan(message, null);
    }

    public void setMessage_LessThan(String message, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("message", ConditionKey.CK_LESS_THAN, message);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMessage_GreaterEqual(String message) {
        setMessage_GreaterEqual(message, null);
    }

    public void setMessage_GreaterEqual(String message, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("message", ConditionKey.CK_GREATER_EQUAL, message);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMessage_LessEqual(String message) {
        setMessage_LessEqual(message, null);
    }

    public void setMessage_LessEqual(String message, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("message", ConditionKey.CK_LESS_EQUAL, message);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsEventLogCQ addOrderBy_Message_Asc() {
        regOBA("message");
        return this;
    }

    public BsEventLogCQ addOrderBy_Message_Desc() {
        regOBD("message");
        return this;
    }

    public void setPath_Equal(String path) {
        setPath_Term(path, null);
    }

    public void setPath_Equal(String path, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setPath_Term(path, opLambda);
    }

    public void setPath_Term(String path) {
        setPath_Term(path, null);
    }

    public void setPath_Term(String path, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("path", path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_Terms(Collection<String> pathList) {
        setPath_Terms(pathList, null);
    }

    public void setPath_Terms(Collection<String> pathList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("path", pathList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_InScope(Collection<String> pathList) {
        setPath_Terms(pathList, null);
    }

    public void setPath_InScope(Collection<String> pathList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setPath_Terms(pathList, opLambda);
    }

    public void setPath_Match(String path) {
        setPath_Match(path, null);
    }

    public void setPath_Match(String path, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("path", path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_MatchPhrase(String path) {
        setPath_MatchPhrase(path, null);
    }

    public void setPath_MatchPhrase(String path, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("path", path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_MatchPhrasePrefix(String path) {
        setPath_MatchPhrasePrefix(path, null);
    }

    public void setPath_MatchPhrasePrefix(String path, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("path", path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_Fuzzy(String path) {
        setPath_Fuzzy(path, null);
    }

    public void setPath_Fuzzy(String path, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("path", path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_Prefix(String path) {
        setPath_Prefix(path, null);
    }

    public void setPath_Prefix(String path, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("path", path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_GreaterThan(String path) {
        setPath_GreaterThan(path, null);
    }

    public void setPath_GreaterThan(String path, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("path", ConditionKey.CK_GREATER_THAN, path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_LessThan(String path) {
        setPath_LessThan(path, null);
    }

    public void setPath_LessThan(String path, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("path", ConditionKey.CK_LESS_THAN, path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_GreaterEqual(String path) {
        setPath_GreaterEqual(path, null);
    }

    public void setPath_GreaterEqual(String path, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("path", ConditionKey.CK_GREATER_EQUAL, path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_LessEqual(String path) {
        setPath_LessEqual(path, null);
    }

    public void setPath_LessEqual(String path, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("path", ConditionKey.CK_LESS_EQUAL, path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsEventLogCQ addOrderBy_Path_Asc() {
        regOBA("path");
        return this;
    }

    public BsEventLogCQ addOrderBy_Path_Desc() {
        regOBD("path");
        return this;
    }

}
