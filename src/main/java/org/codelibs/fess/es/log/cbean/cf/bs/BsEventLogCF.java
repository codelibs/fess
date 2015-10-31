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
package org.codelibs.fess.es.log.cbean.cf.bs;

import java.time.LocalDateTime;
import java.util.Collection;

import org.codelibs.fess.es.log.allcommon.EsAbstractConditionFilter;
import org.codelibs.fess.es.log.cbean.cf.EventLogCF;
import org.codelibs.fess.es.log.cbean.cq.EventLogCQ;
import org.dbflute.cbean.ckey.ConditionKey;
import org.dbflute.exception.IllegalConditionBeanOperationException;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.ExistsFilterBuilder;
import org.elasticsearch.index.query.MissingFilterBuilder;
import org.elasticsearch.index.query.NotFilterBuilder;
import org.elasticsearch.index.query.OrFilterBuilder;
import org.elasticsearch.index.query.PrefixFilterBuilder;
import org.elasticsearch.index.query.QueryFilterBuilder;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.index.query.TermsFilterBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsEventLogCF extends EsAbstractConditionFilter {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void bool(BoolCall<EventLogCF> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<EventLogCF> boolLambda, ConditionOptionCall<BoolFilterBuilder> opLambda) {
        EventLogCF mustFilter = new EventLogCF();
        EventLogCF shouldFilter = new EventLogCF();
        EventLogCF mustNotFilter = new EventLogCF();
        boolLambda.callback(mustFilter, shouldFilter, mustNotFilter);
        if (mustFilter.hasFilters() || shouldFilter.hasFilters() || mustNotFilter.hasFilters()) {
            BoolFilterBuilder builder =
                    regBoolF(mustFilter.filterBuilderList, shouldFilter.filterBuilderList, mustNotFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void and(OperatorCall<EventLogCF> andLambda) {
        and(andLambda, null);
    }

    public void and(OperatorCall<EventLogCF> andLambda, ConditionOptionCall<AndFilterBuilder> opLambda) {
        EventLogCF andFilter = new EventLogCF();
        andLambda.callback(andFilter);
        if (andFilter.hasFilters()) {
            AndFilterBuilder builder = regAndF(andFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void or(OperatorCall<EventLogCF> orLambda) {
        or(orLambda, null);
    }

    public void or(OperatorCall<EventLogCF> orLambda, ConditionOptionCall<OrFilterBuilder> opLambda) {
        EventLogCF orFilter = new EventLogCF();
        orLambda.callback(orFilter);
        if (orFilter.hasFilters()) {
            OrFilterBuilder builder = regOrF(orFilter.filterBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void not(OperatorCall<EventLogCF> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<EventLogCF> notLambda, ConditionOptionCall<NotFilterBuilder> opLambda) {
        EventLogCF notFilter = new EventLogCF();
        notLambda.callback(notFilter);
        if (notFilter.hasFilters()) {
            if (notFilter.filterBuilderList.size() > 1) {
                final String msg = "not filter must be one filter.";
                throw new IllegalConditionBeanOperationException(msg);
            }
            NotFilterBuilder builder = regNotF(notFilter.filterBuilderList.get(0));
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void query(org.codelibs.fess.es.log.allcommon.EsAbstractConditionQuery.OperatorCall<EventLogCQ> queryLambda) {
        query(queryLambda, null);
    }

    public void query(org.codelibs.fess.es.log.allcommon.EsAbstractConditionQuery.OperatorCall<EventLogCQ> queryLambda,
            ConditionOptionCall<QueryFilterBuilder> opLambda) {
        EventLogCQ query = new EventLogCQ();
        queryLambda.callback(query);
        if (query.hasQueries()) {
            QueryFilterBuilder builder = regQueryF(query.getQuery());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    // ===================================================================================
    //                                                                           Query Set
    //                                                                           =========
    public void setCreatedAt_NotEqual(LocalDateTime createdAt) {
        setCreatedAt_NotEqual(createdAt, null, null);
    }

    public void setCreatedAt_NotEqual(LocalDateTime createdAt, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setCreatedAt_Equal(createdAt, eqOpLambda);
        }, notOpLambda);
    }

    public void setCreatedAt_Equal(LocalDateTime createdAt) {
        setCreatedAt_Term(createdAt, null);
    }

    public void setCreatedAt_Equal(LocalDateTime createdAt, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setCreatedAt_Term(createdAt, opLambda);
    }

    public void setCreatedAt_Term(LocalDateTime createdAt) {
        setCreatedAt_Term(createdAt, null);
    }

    public void setCreatedAt_Term(LocalDateTime createdAt, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Terms(Collection<LocalDateTime> createdAtList) {
        setCreatedAt_Terms(createdAtList, null);
    }

    public void setCreatedAt_Terms(Collection<LocalDateTime> createdAtList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("createdAt", createdAtList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_InScope(Collection<LocalDateTime> createdAtList) {
        setCreatedAt_Terms(createdAtList, null);
    }

    public void setCreatedAt_InScope(Collection<LocalDateTime> createdAtList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setCreatedAt_Terms(createdAtList, opLambda);
    }

    public void setCreatedAt_Exists() {
        setCreatedAt_Exists(null);
    }

    public void setCreatedAt_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Missing() {
        setCreatedAt_Missing(null);
    }

    public void setCreatedAt_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_GreaterThan(LocalDateTime createdAt) {
        setCreatedAt_GreaterThan(createdAt, null);
    }

    public void setCreatedAt_GreaterThan(LocalDateTime createdAt, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("createdAt", ConditionKey.CK_GREATER_THAN, createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_LessThan(LocalDateTime createdAt) {
        setCreatedAt_LessThan(createdAt, null);
    }

    public void setCreatedAt_LessThan(LocalDateTime createdAt, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("createdAt", ConditionKey.CK_LESS_THAN, createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_GreaterEqual(LocalDateTime createdAt) {
        setCreatedAt_GreaterEqual(createdAt, null);
    }

    public void setCreatedAt_GreaterEqual(LocalDateTime createdAt, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("createdAt", ConditionKey.CK_GREATER_EQUAL, createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_LessEqual(LocalDateTime createdAt) {
        setCreatedAt_LessEqual(createdAt, null);
    }

    public void setCreatedAt_LessEqual(LocalDateTime createdAt, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("createdAt", ConditionKey.CK_LESS_EQUAL, createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_NotEqual(String createdBy) {
        setCreatedBy_NotEqual(createdBy, null, null);
    }

    public void setCreatedBy_NotEqual(String createdBy, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setCreatedBy_Equal(createdBy, eqOpLambda);
        }, notOpLambda);
    }

    public void setCreatedBy_Equal(String createdBy) {
        setCreatedBy_Term(createdBy, null);
    }

    public void setCreatedBy_Equal(String createdBy, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setCreatedBy_Term(createdBy, opLambda);
    }

    public void setCreatedBy_Term(String createdBy) {
        setCreatedBy_Term(createdBy, null);
    }

    public void setCreatedBy_Term(String createdBy, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Terms(Collection<String> createdByList) {
        setCreatedBy_Terms(createdByList, null);
    }

    public void setCreatedBy_Terms(Collection<String> createdByList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("createdBy", createdByList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_InScope(Collection<String> createdByList) {
        setCreatedBy_Terms(createdByList, null);
    }

    public void setCreatedBy_InScope(Collection<String> createdByList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setCreatedBy_Terms(createdByList, opLambda);
    }

    public void setCreatedBy_Prefix(String createdBy) {
        setCreatedBy_Prefix(createdBy, null);
    }

    public void setCreatedBy_Prefix(String createdBy, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Exists() {
        setCreatedBy_Exists(null);
    }

    public void setCreatedBy_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Missing() {
        setCreatedBy_Missing(null);
    }

    public void setCreatedBy_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_GreaterThan(String createdBy) {
        setCreatedBy_GreaterThan(createdBy, null);
    }

    public void setCreatedBy_GreaterThan(String createdBy, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("createdBy", ConditionKey.CK_GREATER_THAN, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_LessThan(String createdBy) {
        setCreatedBy_LessThan(createdBy, null);
    }

    public void setCreatedBy_LessThan(String createdBy, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("createdBy", ConditionKey.CK_LESS_THAN, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_GreaterEqual(String createdBy) {
        setCreatedBy_GreaterEqual(createdBy, null);
    }

    public void setCreatedBy_GreaterEqual(String createdBy, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("createdBy", ConditionKey.CK_GREATER_EQUAL, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_LessEqual(String createdBy) {
        setCreatedBy_LessEqual(createdBy, null);
    }

    public void setCreatedBy_LessEqual(String createdBy, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("createdBy", ConditionKey.CK_LESS_EQUAL, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEventType_NotEqual(String eventType) {
        setEventType_NotEqual(eventType, null, null);
    }

    public void setEventType_NotEqual(String eventType, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setEventType_Equal(eventType, eqOpLambda);
        }, notOpLambda);
    }

    public void setEventType_Equal(String eventType) {
        setEventType_Term(eventType, null);
    }

    public void setEventType_Equal(String eventType, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setEventType_Term(eventType, opLambda);
    }

    public void setEventType_Term(String eventType) {
        setEventType_Term(eventType, null);
    }

    public void setEventType_Term(String eventType, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("eventType", eventType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEventType_Terms(Collection<String> eventTypeList) {
        setEventType_Terms(eventTypeList, null);
    }

    public void setEventType_Terms(Collection<String> eventTypeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("eventType", eventTypeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEventType_InScope(Collection<String> eventTypeList) {
        setEventType_Terms(eventTypeList, null);
    }

    public void setEventType_InScope(Collection<String> eventTypeList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setEventType_Terms(eventTypeList, opLambda);
    }

    public void setEventType_Prefix(String eventType) {
        setEventType_Prefix(eventType, null);
    }

    public void setEventType_Prefix(String eventType, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("eventType", eventType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEventType_Exists() {
        setEventType_Exists(null);
    }

    public void setEventType_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("eventType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEventType_Missing() {
        setEventType_Missing(null);
    }

    public void setEventType_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("eventType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEventType_GreaterThan(String eventType) {
        setEventType_GreaterThan(eventType, null);
    }

    public void setEventType_GreaterThan(String eventType, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("eventType", ConditionKey.CK_GREATER_THAN, eventType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEventType_LessThan(String eventType) {
        setEventType_LessThan(eventType, null);
    }

    public void setEventType_LessThan(String eventType, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("eventType", ConditionKey.CK_LESS_THAN, eventType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEventType_GreaterEqual(String eventType) {
        setEventType_GreaterEqual(eventType, null);
    }

    public void setEventType_GreaterEqual(String eventType, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("eventType", ConditionKey.CK_GREATER_EQUAL, eventType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEventType_LessEqual(String eventType) {
        setEventType_LessEqual(eventType, null);
    }

    public void setEventType_LessEqual(String eventType, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("eventType", ConditionKey.CK_LESS_EQUAL, eventType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_NotEqual(String id) {
        setId_NotEqual(id, null, null);
    }

    public void setId_NotEqual(String id, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setId_Equal(id, eqOpLambda);
        }, notOpLambda);
    }

    public void setId_Equal(String id) {
        setId_Term(id, null);
    }

    public void setId_Equal(String id, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setId_Term(id, opLambda);
    }

    public void setId_Term(String id) {
        setId_Term(id, null);
    }

    public void setId_Term(String id, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Terms(Collection<String> idList) {
        setId_Terms(idList, null);
    }

    public void setId_Terms(Collection<String> idList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("id", idList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_InScope(Collection<String> idList) {
        setId_Terms(idList, null);
    }

    public void setId_InScope(Collection<String> idList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setId_Terms(idList, opLambda);
    }

    public void setId_Prefix(String id) {
        setId_Prefix(id, null);
    }

    public void setId_Prefix(String id, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Exists() {
        setId_Exists(null);
    }

    public void setId_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("id");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Missing() {
        setId_Missing(null);
    }

    public void setId_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("id");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_GreaterThan(String id) {
        setId_GreaterThan(id, null);
    }

    public void setId_GreaterThan(String id, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("id", ConditionKey.CK_GREATER_THAN, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_LessThan(String id) {
        setId_LessThan(id, null);
    }

    public void setId_LessThan(String id, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("id", ConditionKey.CK_LESS_THAN, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_GreaterEqual(String id) {
        setId_GreaterEqual(id, null);
    }

    public void setId_GreaterEqual(String id, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("id", ConditionKey.CK_GREATER_EQUAL, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_LessEqual(String id) {
        setId_LessEqual(id, null);
    }

    public void setId_LessEqual(String id, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("id", ConditionKey.CK_LESS_EQUAL, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMessage_NotEqual(String message) {
        setMessage_NotEqual(message, null, null);
    }

    public void setMessage_NotEqual(String message, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setMessage_Equal(message, eqOpLambda);
        }, notOpLambda);
    }

    public void setMessage_Equal(String message) {
        setMessage_Term(message, null);
    }

    public void setMessage_Equal(String message, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setMessage_Term(message, opLambda);
    }

    public void setMessage_Term(String message) {
        setMessage_Term(message, null);
    }

    public void setMessage_Term(String message, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("message", message);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMessage_Terms(Collection<String> messageList) {
        setMessage_Terms(messageList, null);
    }

    public void setMessage_Terms(Collection<String> messageList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("message", messageList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMessage_InScope(Collection<String> messageList) {
        setMessage_Terms(messageList, null);
    }

    public void setMessage_InScope(Collection<String> messageList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setMessage_Terms(messageList, opLambda);
    }

    public void setMessage_Prefix(String message) {
        setMessage_Prefix(message, null);
    }

    public void setMessage_Prefix(String message, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("message", message);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMessage_Exists() {
        setMessage_Exists(null);
    }

    public void setMessage_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("message");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMessage_Missing() {
        setMessage_Missing(null);
    }

    public void setMessage_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("message");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMessage_GreaterThan(String message) {
        setMessage_GreaterThan(message, null);
    }

    public void setMessage_GreaterThan(String message, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("message", ConditionKey.CK_GREATER_THAN, message);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMessage_LessThan(String message) {
        setMessage_LessThan(message, null);
    }

    public void setMessage_LessThan(String message, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("message", ConditionKey.CK_LESS_THAN, message);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMessage_GreaterEqual(String message) {
        setMessage_GreaterEqual(message, null);
    }

    public void setMessage_GreaterEqual(String message, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("message", ConditionKey.CK_GREATER_EQUAL, message);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMessage_LessEqual(String message) {
        setMessage_LessEqual(message, null);
    }

    public void setMessage_LessEqual(String message, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("message", ConditionKey.CK_LESS_EQUAL, message);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_NotEqual(String path) {
        setPath_NotEqual(path, null, null);
    }

    public void setPath_NotEqual(String path, ConditionOptionCall<NotFilterBuilder> notOpLambda,
            ConditionOptionCall<TermFilterBuilder> eqOpLambda) {
        not(subCf -> {
            subCf.setPath_Equal(path, eqOpLambda);
        }, notOpLambda);
    }

    public void setPath_Equal(String path) {
        setPath_Term(path, null);
    }

    public void setPath_Equal(String path, ConditionOptionCall<TermFilterBuilder> opLambda) {
        setPath_Term(path, opLambda);
    }

    public void setPath_Term(String path) {
        setPath_Term(path, null);
    }

    public void setPath_Term(String path, ConditionOptionCall<TermFilterBuilder> opLambda) {
        TermFilterBuilder builder = regTermF("path", path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_Terms(Collection<String> pathList) {
        setPath_Terms(pathList, null);
    }

    public void setPath_Terms(Collection<String> pathList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        TermsFilterBuilder builder = regTermsF("path", pathList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_InScope(Collection<String> pathList) {
        setPath_Terms(pathList, null);
    }

    public void setPath_InScope(Collection<String> pathList, ConditionOptionCall<TermsFilterBuilder> opLambda) {
        setPath_Terms(pathList, opLambda);
    }

    public void setPath_Prefix(String path) {
        setPath_Prefix(path, null);
    }

    public void setPath_Prefix(String path, ConditionOptionCall<PrefixFilterBuilder> opLambda) {
        PrefixFilterBuilder builder = regPrefixF("path", path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_Exists() {
        setPath_Exists(null);
    }

    public void setPath_Exists(ConditionOptionCall<ExistsFilterBuilder> opLambda) {
        ExistsFilterBuilder builder = regExistsF("path");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_Missing() {
        setPath_Missing(null);
    }

    public void setPath_Missing(ConditionOptionCall<MissingFilterBuilder> opLambda) {
        MissingFilterBuilder builder = regMissingF("path");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_GreaterThan(String path) {
        setPath_GreaterThan(path, null);
    }

    public void setPath_GreaterThan(String path, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("path", ConditionKey.CK_GREATER_THAN, path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_LessThan(String path) {
        setPath_LessThan(path, null);
    }

    public void setPath_LessThan(String path, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("path", ConditionKey.CK_LESS_THAN, path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_GreaterEqual(String path) {
        setPath_GreaterEqual(path, null);
    }

    public void setPath_GreaterEqual(String path, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("path", ConditionKey.CK_GREATER_EQUAL, path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_LessEqual(String path) {
        setPath_LessEqual(path, null);
    }

    public void setPath_LessEqual(String path, ConditionOptionCall<RangeFilterBuilder> opLambda) {
        RangeFilterBuilder builder = regRangeF("path", ConditionKey.CK_LESS_EQUAL, path);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

}
