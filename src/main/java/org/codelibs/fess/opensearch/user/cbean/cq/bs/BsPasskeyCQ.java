/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.opensearch.user.cbean.cq.bs;

import java.time.*;
import java.util.ArrayList;

import java.util.Collection;

import org.codelibs.fess.opensearch.user.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.opensearch.user.cbean.cq.PasskeyCQ;
import org.dbflute.cbean.ckey.ConditionKey;
import org.codelibs.fesen.opensearch.index.query.*;
import org.codelibs.fesen.opensearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsPasskeyCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "passkey";
    }

    @Override
    public String xgetAliasName() {
        return "passkey";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<PasskeyCQ> queryLambda, ScoreFunctionCall<ScoreFunctionCreator<PasskeyCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        PasskeyCQ cq = new PasskeyCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                PasskeyCQ cf = new PasskeyCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<PasskeyCQ, PasskeyCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<PasskeyCQ, PasskeyCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<PasskeyCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<PasskeyCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<PasskeyCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<PasskeyCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        PasskeyCQ mustQuery = new PasskeyCQ();
        PasskeyCQ shouldQuery = new PasskeyCQ();
        PasskeyCQ mustNotQuery = new PasskeyCQ();
        PasskeyCQ filterQuery = new PasskeyCQ();
        boolLambda.callback(mustQuery, shouldQuery, mustNotQuery, filterQuery);
        if (mustQuery.hasQueries() || shouldQuery.hasQueries() || mustNotQuery.hasQueries() || filterQuery.hasQueries()) {
            BoolQueryBuilder builder = regBoolCQ(mustQuery.getQueryBuilderList(), shouldQuery.getQueryBuilderList(),
                    mustNotQuery.getQueryBuilderList(), filterQuery.getQueryBuilderList());
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

    public void setId_NotTerm(String id) {
        setId_NotTerm(id, null);
    }

    public void setId_NotEqual(String id, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setId_NotTerm(id, opLambda);
    }

    public void setId_NotTerm(String id, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setId_Term(id), opLambda);
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

    @Deprecated
    public BsPasskeyCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsPasskeyCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setBackupEligible_Equal(Boolean backupEligible) {
        setBackupEligible_Term(backupEligible, null);
    }

    public void setBackupEligible_Equal(Boolean backupEligible, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setBackupEligible_Term(backupEligible, opLambda);
    }

    public void setBackupEligible_Term(Boolean backupEligible) {
        setBackupEligible_Term(backupEligible, null);
    }

    public void setBackupEligible_Term(Boolean backupEligible, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("backupEligible", backupEligible);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_NotEqual(Boolean backupEligible) {
        setBackupEligible_NotTerm(backupEligible, null);
    }

    public void setBackupEligible_NotTerm(Boolean backupEligible) {
        setBackupEligible_NotTerm(backupEligible, null);
    }

    public void setBackupEligible_NotEqual(Boolean backupEligible, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setBackupEligible_NotTerm(backupEligible, opLambda);
    }

    public void setBackupEligible_NotTerm(Boolean backupEligible, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setBackupEligible_Term(backupEligible), opLambda);
    }

    public void setBackupEligible_Terms(Collection<Boolean> backupEligibleList) {
        setBackupEligible_Terms(backupEligibleList, null);
    }

    public void setBackupEligible_Terms(Collection<Boolean> backupEligibleList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("backupEligible", backupEligibleList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_InScope(Collection<Boolean> backupEligibleList) {
        setBackupEligible_Terms(backupEligibleList, null);
    }

    public void setBackupEligible_InScope(Collection<Boolean> backupEligibleList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setBackupEligible_Terms(backupEligibleList, opLambda);
    }

    public void setBackupEligible_Match(Boolean backupEligible) {
        setBackupEligible_Match(backupEligible, null);
    }

    public void setBackupEligible_Match(Boolean backupEligible, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("backupEligible", backupEligible);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_MatchPhrase(Boolean backupEligible) {
        setBackupEligible_MatchPhrase(backupEligible, null);
    }

    public void setBackupEligible_MatchPhrase(Boolean backupEligible, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("backupEligible", backupEligible);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_MatchPhrasePrefix(Boolean backupEligible) {
        setBackupEligible_MatchPhrasePrefix(backupEligible, null);
    }

    public void setBackupEligible_MatchPhrasePrefix(Boolean backupEligible, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("backupEligible", backupEligible);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_Fuzzy(Boolean backupEligible) {
        setBackupEligible_Fuzzy(backupEligible, null);
    }

    public void setBackupEligible_Fuzzy(Boolean backupEligible, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("backupEligible", backupEligible);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_GreaterThan(Boolean backupEligible) {
        setBackupEligible_GreaterThan(backupEligible, null);
    }

    public void setBackupEligible_GreaterThan(Boolean backupEligible, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = backupEligible;
        RangeQueryBuilder builder = regRangeQ("backupEligible", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_LessThan(Boolean backupEligible) {
        setBackupEligible_LessThan(backupEligible, null);
    }

    public void setBackupEligible_LessThan(Boolean backupEligible, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = backupEligible;
        RangeQueryBuilder builder = regRangeQ("backupEligible", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_GreaterEqual(Boolean backupEligible) {
        setBackupEligible_GreaterEqual(backupEligible, null);
    }

    public void setBackupEligible_GreaterEqual(Boolean backupEligible, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = backupEligible;
        RangeQueryBuilder builder = regRangeQ("backupEligible", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_LessEqual(Boolean backupEligible) {
        setBackupEligible_LessEqual(backupEligible, null);
    }

    public void setBackupEligible_LessEqual(Boolean backupEligible, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = backupEligible;
        RangeQueryBuilder builder = regRangeQ("backupEligible", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_Exists() {
        setBackupEligible_Exists(null);
    }

    public void setBackupEligible_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("backupEligible");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setBackupEligible_CommonTerms(Boolean backupEligible) {
        setBackupEligible_CommonTerms(backupEligible, null);
    }

    @Deprecated
    public void setBackupEligible_CommonTerms(Boolean backupEligible, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("backupEligible", backupEligible);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsPasskeyCQ addOrderBy_BackupEligible_Asc() {
        regOBA("backupEligible");
        return this;
    }

    public BsPasskeyCQ addOrderBy_BackupEligible_Desc() {
        regOBD("backupEligible");
        return this;
    }

    public void setBackupState_Equal(Boolean backupState) {
        setBackupState_Term(backupState, null);
    }

    public void setBackupState_Equal(Boolean backupState, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setBackupState_Term(backupState, opLambda);
    }

    public void setBackupState_Term(Boolean backupState) {
        setBackupState_Term(backupState, null);
    }

    public void setBackupState_Term(Boolean backupState, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("backupState", backupState);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_NotEqual(Boolean backupState) {
        setBackupState_NotTerm(backupState, null);
    }

    public void setBackupState_NotTerm(Boolean backupState) {
        setBackupState_NotTerm(backupState, null);
    }

    public void setBackupState_NotEqual(Boolean backupState, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setBackupState_NotTerm(backupState, opLambda);
    }

    public void setBackupState_NotTerm(Boolean backupState, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setBackupState_Term(backupState), opLambda);
    }

    public void setBackupState_Terms(Collection<Boolean> backupStateList) {
        setBackupState_Terms(backupStateList, null);
    }

    public void setBackupState_Terms(Collection<Boolean> backupStateList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("backupState", backupStateList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_InScope(Collection<Boolean> backupStateList) {
        setBackupState_Terms(backupStateList, null);
    }

    public void setBackupState_InScope(Collection<Boolean> backupStateList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setBackupState_Terms(backupStateList, opLambda);
    }

    public void setBackupState_Match(Boolean backupState) {
        setBackupState_Match(backupState, null);
    }

    public void setBackupState_Match(Boolean backupState, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("backupState", backupState);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_MatchPhrase(Boolean backupState) {
        setBackupState_MatchPhrase(backupState, null);
    }

    public void setBackupState_MatchPhrase(Boolean backupState, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("backupState", backupState);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_MatchPhrasePrefix(Boolean backupState) {
        setBackupState_MatchPhrasePrefix(backupState, null);
    }

    public void setBackupState_MatchPhrasePrefix(Boolean backupState, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("backupState", backupState);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_Fuzzy(Boolean backupState) {
        setBackupState_Fuzzy(backupState, null);
    }

    public void setBackupState_Fuzzy(Boolean backupState, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("backupState", backupState);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_GreaterThan(Boolean backupState) {
        setBackupState_GreaterThan(backupState, null);
    }

    public void setBackupState_GreaterThan(Boolean backupState, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = backupState;
        RangeQueryBuilder builder = regRangeQ("backupState", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_LessThan(Boolean backupState) {
        setBackupState_LessThan(backupState, null);
    }

    public void setBackupState_LessThan(Boolean backupState, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = backupState;
        RangeQueryBuilder builder = regRangeQ("backupState", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_GreaterEqual(Boolean backupState) {
        setBackupState_GreaterEqual(backupState, null);
    }

    public void setBackupState_GreaterEqual(Boolean backupState, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = backupState;
        RangeQueryBuilder builder = regRangeQ("backupState", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_LessEqual(Boolean backupState) {
        setBackupState_LessEqual(backupState, null);
    }

    public void setBackupState_LessEqual(Boolean backupState, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = backupState;
        RangeQueryBuilder builder = regRangeQ("backupState", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_Exists() {
        setBackupState_Exists(null);
    }

    public void setBackupState_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("backupState");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setBackupState_CommonTerms(Boolean backupState) {
        setBackupState_CommonTerms(backupState, null);
    }

    @Deprecated
    public void setBackupState_CommonTerms(Boolean backupState, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("backupState", backupState);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsPasskeyCQ addOrderBy_BackupState_Asc() {
        regOBA("backupState");
        return this;
    }

    public BsPasskeyCQ addOrderBy_BackupState_Desc() {
        regOBD("backupState");
        return this;
    }

    public void setCreatedAt_Equal(Long createdAt) {
        setCreatedAt_Term(createdAt, null);
    }

    public void setCreatedAt_Equal(Long createdAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setCreatedAt_Term(createdAt, opLambda);
    }

    public void setCreatedAt_Term(Long createdAt) {
        setCreatedAt_Term(createdAt, null);
    }

    public void setCreatedAt_Term(Long createdAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_NotEqual(Long createdAt) {
        setCreatedAt_NotTerm(createdAt, null);
    }

    public void setCreatedAt_NotTerm(Long createdAt) {
        setCreatedAt_NotTerm(createdAt, null);
    }

    public void setCreatedAt_NotEqual(Long createdAt, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setCreatedAt_NotTerm(createdAt, opLambda);
    }

    public void setCreatedAt_NotTerm(Long createdAt, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setCreatedAt_Term(createdAt), opLambda);
    }

    public void setCreatedAt_Terms(Collection<Long> createdAtList) {
        setCreatedAt_Terms(createdAtList, null);
    }

    public void setCreatedAt_Terms(Collection<Long> createdAtList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("createdAt", createdAtList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_InScope(Collection<Long> createdAtList) {
        setCreatedAt_Terms(createdAtList, null);
    }

    public void setCreatedAt_InScope(Collection<Long> createdAtList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setCreatedAt_Terms(createdAtList, opLambda);
    }

    public void setCreatedAt_Match(Long createdAt) {
        setCreatedAt_Match(createdAt, null);
    }

    public void setCreatedAt_Match(Long createdAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_MatchPhrase(Long createdAt) {
        setCreatedAt_MatchPhrase(createdAt, null);
    }

    public void setCreatedAt_MatchPhrase(Long createdAt, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_MatchPhrasePrefix(Long createdAt) {
        setCreatedAt_MatchPhrasePrefix(createdAt, null);
    }

    public void setCreatedAt_MatchPhrasePrefix(Long createdAt, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Fuzzy(Long createdAt) {
        setCreatedAt_Fuzzy(createdAt, null);
    }

    public void setCreatedAt_Fuzzy(Long createdAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_GreaterThan(Long createdAt) {
        setCreatedAt_GreaterThan(createdAt, null);
    }

    public void setCreatedAt_GreaterThan(Long createdAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = createdAt;
        RangeQueryBuilder builder = regRangeQ("createdAt", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_LessThan(Long createdAt) {
        setCreatedAt_LessThan(createdAt, null);
    }

    public void setCreatedAt_LessThan(Long createdAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = createdAt;
        RangeQueryBuilder builder = regRangeQ("createdAt", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_GreaterEqual(Long createdAt) {
        setCreatedAt_GreaterEqual(createdAt, null);
    }

    public void setCreatedAt_GreaterEqual(Long createdAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = createdAt;
        RangeQueryBuilder builder = regRangeQ("createdAt", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_LessEqual(Long createdAt) {
        setCreatedAt_LessEqual(createdAt, null);
    }

    public void setCreatedAt_LessEqual(Long createdAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = createdAt;
        RangeQueryBuilder builder = regRangeQ("createdAt", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Exists() {
        setCreatedAt_Exists(null);
    }

    public void setCreatedAt_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setCreatedAt_CommonTerms(Long createdAt) {
        setCreatedAt_CommonTerms(createdAt, null);
    }

    @Deprecated
    public void setCreatedAt_CommonTerms(Long createdAt, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsPasskeyCQ addOrderBy_CreatedAt_Asc() {
        regOBA("createdAt");
        return this;
    }

    public BsPasskeyCQ addOrderBy_CreatedAt_Desc() {
        regOBD("createdAt");
        return this;
    }

    public void setLabel_Equal(String label) {
        setLabel_Term(label, null);
    }

    public void setLabel_Equal(String label, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setLabel_Term(label, opLambda);
    }

    public void setLabel_Term(String label) {
        setLabel_Term(label, null);
    }

    public void setLabel_Term(String label, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("label", label);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabel_NotEqual(String label) {
        setLabel_NotTerm(label, null);
    }

    public void setLabel_NotTerm(String label) {
        setLabel_NotTerm(label, null);
    }

    public void setLabel_NotEqual(String label, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setLabel_NotTerm(label, opLambda);
    }

    public void setLabel_NotTerm(String label, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setLabel_Term(label), opLambda);
    }

    public void setLabel_Terms(Collection<String> labelList) {
        setLabel_Terms(labelList, null);
    }

    public void setLabel_Terms(Collection<String> labelList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("label", labelList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabel_InScope(Collection<String> labelList) {
        setLabel_Terms(labelList, null);
    }

    public void setLabel_InScope(Collection<String> labelList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setLabel_Terms(labelList, opLambda);
    }

    public void setLabel_Match(String label) {
        setLabel_Match(label, null);
    }

    public void setLabel_Match(String label, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("label", label);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabel_MatchPhrase(String label) {
        setLabel_MatchPhrase(label, null);
    }

    public void setLabel_MatchPhrase(String label, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("label", label);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabel_MatchPhrasePrefix(String label) {
        setLabel_MatchPhrasePrefix(label, null);
    }

    public void setLabel_MatchPhrasePrefix(String label, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("label", label);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabel_Fuzzy(String label) {
        setLabel_Fuzzy(label, null);
    }

    public void setLabel_Fuzzy(String label, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("label", label);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabel_Prefix(String label) {
        setLabel_Prefix(label, null);
    }

    public void setLabel_Prefix(String label, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("label", label);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabel_Wildcard(String label) {
        setLabel_Wildcard(label, null);
    }

    public void setLabel_Wildcard(String label, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("label", label);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabel_Regexp(String label) {
        setLabel_Regexp(label, null);
    }

    public void setLabel_Regexp(String label, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("label", label);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabel_SpanTerm(String label) {
        setLabel_SpanTerm("label", null);
    }

    public void setLabel_SpanTerm(String label, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("label", label);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabel_GreaterThan(String label) {
        setLabel_GreaterThan(label, null);
    }

    public void setLabel_GreaterThan(String label, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = label;
        RangeQueryBuilder builder = regRangeQ("label", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabel_LessThan(String label) {
        setLabel_LessThan(label, null);
    }

    public void setLabel_LessThan(String label, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = label;
        RangeQueryBuilder builder = regRangeQ("label", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabel_GreaterEqual(String label) {
        setLabel_GreaterEqual(label, null);
    }

    public void setLabel_GreaterEqual(String label, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = label;
        RangeQueryBuilder builder = regRangeQ("label", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabel_LessEqual(String label) {
        setLabel_LessEqual(label, null);
    }

    public void setLabel_LessEqual(String label, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = label;
        RangeQueryBuilder builder = regRangeQ("label", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabel_Exists() {
        setLabel_Exists(null);
    }

    public void setLabel_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("label");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setLabel_CommonTerms(String label) {
        setLabel_CommonTerms(label, null);
    }

    @Deprecated
    public void setLabel_CommonTerms(String label, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("label", label);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsPasskeyCQ addOrderBy_Label_Asc() {
        regOBA("label");
        return this;
    }

    public BsPasskeyCQ addOrderBy_Label_Desc() {
        regOBD("label");
        return this;
    }

    public void setLastUsedAt_Equal(Long lastUsedAt) {
        setLastUsedAt_Term(lastUsedAt, null);
    }

    public void setLastUsedAt_Equal(Long lastUsedAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setLastUsedAt_Term(lastUsedAt, opLambda);
    }

    public void setLastUsedAt_Term(Long lastUsedAt) {
        setLastUsedAt_Term(lastUsedAt, null);
    }

    public void setLastUsedAt_Term(Long lastUsedAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("lastUsedAt", lastUsedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_NotEqual(Long lastUsedAt) {
        setLastUsedAt_NotTerm(lastUsedAt, null);
    }

    public void setLastUsedAt_NotTerm(Long lastUsedAt) {
        setLastUsedAt_NotTerm(lastUsedAt, null);
    }

    public void setLastUsedAt_NotEqual(Long lastUsedAt, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setLastUsedAt_NotTerm(lastUsedAt, opLambda);
    }

    public void setLastUsedAt_NotTerm(Long lastUsedAt, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setLastUsedAt_Term(lastUsedAt), opLambda);
    }

    public void setLastUsedAt_Terms(Collection<Long> lastUsedAtList) {
        setLastUsedAt_Terms(lastUsedAtList, null);
    }

    public void setLastUsedAt_Terms(Collection<Long> lastUsedAtList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("lastUsedAt", lastUsedAtList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_InScope(Collection<Long> lastUsedAtList) {
        setLastUsedAt_Terms(lastUsedAtList, null);
    }

    public void setLastUsedAt_InScope(Collection<Long> lastUsedAtList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setLastUsedAt_Terms(lastUsedAtList, opLambda);
    }

    public void setLastUsedAt_Match(Long lastUsedAt) {
        setLastUsedAt_Match(lastUsedAt, null);
    }

    public void setLastUsedAt_Match(Long lastUsedAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("lastUsedAt", lastUsedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_MatchPhrase(Long lastUsedAt) {
        setLastUsedAt_MatchPhrase(lastUsedAt, null);
    }

    public void setLastUsedAt_MatchPhrase(Long lastUsedAt, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("lastUsedAt", lastUsedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_MatchPhrasePrefix(Long lastUsedAt) {
        setLastUsedAt_MatchPhrasePrefix(lastUsedAt, null);
    }

    public void setLastUsedAt_MatchPhrasePrefix(Long lastUsedAt, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("lastUsedAt", lastUsedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_Fuzzy(Long lastUsedAt) {
        setLastUsedAt_Fuzzy(lastUsedAt, null);
    }

    public void setLastUsedAt_Fuzzy(Long lastUsedAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("lastUsedAt", lastUsedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_GreaterThan(Long lastUsedAt) {
        setLastUsedAt_GreaterThan(lastUsedAt, null);
    }

    public void setLastUsedAt_GreaterThan(Long lastUsedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = lastUsedAt;
        RangeQueryBuilder builder = regRangeQ("lastUsedAt", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_LessThan(Long lastUsedAt) {
        setLastUsedAt_LessThan(lastUsedAt, null);
    }

    public void setLastUsedAt_LessThan(Long lastUsedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = lastUsedAt;
        RangeQueryBuilder builder = regRangeQ("lastUsedAt", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_GreaterEqual(Long lastUsedAt) {
        setLastUsedAt_GreaterEqual(lastUsedAt, null);
    }

    public void setLastUsedAt_GreaterEqual(Long lastUsedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = lastUsedAt;
        RangeQueryBuilder builder = regRangeQ("lastUsedAt", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_LessEqual(Long lastUsedAt) {
        setLastUsedAt_LessEqual(lastUsedAt, null);
    }

    public void setLastUsedAt_LessEqual(Long lastUsedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = lastUsedAt;
        RangeQueryBuilder builder = regRangeQ("lastUsedAt", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_Exists() {
        setLastUsedAt_Exists(null);
    }

    public void setLastUsedAt_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("lastUsedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setLastUsedAt_CommonTerms(Long lastUsedAt) {
        setLastUsedAt_CommonTerms(lastUsedAt, null);
    }

    @Deprecated
    public void setLastUsedAt_CommonTerms(Long lastUsedAt, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("lastUsedAt", lastUsedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsPasskeyCQ addOrderBy_LastUsedAt_Asc() {
        regOBA("lastUsedAt");
        return this;
    }

    public BsPasskeyCQ addOrderBy_LastUsedAt_Desc() {
        regOBD("lastUsedAt");
        return this;
    }

    public void setPublicKey_Equal(String publicKey) {
        setPublicKey_Term(publicKey, null);
    }

    public void setPublicKey_Equal(String publicKey, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setPublicKey_Term(publicKey, opLambda);
    }

    public void setPublicKey_Term(String publicKey) {
        setPublicKey_Term(publicKey, null);
    }

    public void setPublicKey_Term(String publicKey, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("publicKey", publicKey);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPublicKey_NotEqual(String publicKey) {
        setPublicKey_NotTerm(publicKey, null);
    }

    public void setPublicKey_NotTerm(String publicKey) {
        setPublicKey_NotTerm(publicKey, null);
    }

    public void setPublicKey_NotEqual(String publicKey, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setPublicKey_NotTerm(publicKey, opLambda);
    }

    public void setPublicKey_NotTerm(String publicKey, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setPublicKey_Term(publicKey), opLambda);
    }

    public void setPublicKey_Terms(Collection<String> publicKeyList) {
        setPublicKey_Terms(publicKeyList, null);
    }

    public void setPublicKey_Terms(Collection<String> publicKeyList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("publicKey", publicKeyList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPublicKey_InScope(Collection<String> publicKeyList) {
        setPublicKey_Terms(publicKeyList, null);
    }

    public void setPublicKey_InScope(Collection<String> publicKeyList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setPublicKey_Terms(publicKeyList, opLambda);
    }

    public void setPublicKey_Match(String publicKey) {
        setPublicKey_Match(publicKey, null);
    }

    public void setPublicKey_Match(String publicKey, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("publicKey", publicKey);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPublicKey_MatchPhrase(String publicKey) {
        setPublicKey_MatchPhrase(publicKey, null);
    }

    public void setPublicKey_MatchPhrase(String publicKey, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("publicKey", publicKey);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPublicKey_MatchPhrasePrefix(String publicKey) {
        setPublicKey_MatchPhrasePrefix(publicKey, null);
    }

    public void setPublicKey_MatchPhrasePrefix(String publicKey, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("publicKey", publicKey);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPublicKey_Fuzzy(String publicKey) {
        setPublicKey_Fuzzy(publicKey, null);
    }

    public void setPublicKey_Fuzzy(String publicKey, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("publicKey", publicKey);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPublicKey_Prefix(String publicKey) {
        setPublicKey_Prefix(publicKey, null);
    }

    public void setPublicKey_Prefix(String publicKey, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("publicKey", publicKey);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPublicKey_Wildcard(String publicKey) {
        setPublicKey_Wildcard(publicKey, null);
    }

    public void setPublicKey_Wildcard(String publicKey, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("publicKey", publicKey);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPublicKey_Regexp(String publicKey) {
        setPublicKey_Regexp(publicKey, null);
    }

    public void setPublicKey_Regexp(String publicKey, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("publicKey", publicKey);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPublicKey_SpanTerm(String publicKey) {
        setPublicKey_SpanTerm("publicKey", null);
    }

    public void setPublicKey_SpanTerm(String publicKey, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("publicKey", publicKey);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPublicKey_GreaterThan(String publicKey) {
        setPublicKey_GreaterThan(publicKey, null);
    }

    public void setPublicKey_GreaterThan(String publicKey, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = publicKey;
        RangeQueryBuilder builder = regRangeQ("publicKey", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPublicKey_LessThan(String publicKey) {
        setPublicKey_LessThan(publicKey, null);
    }

    public void setPublicKey_LessThan(String publicKey, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = publicKey;
        RangeQueryBuilder builder = regRangeQ("publicKey", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPublicKey_GreaterEqual(String publicKey) {
        setPublicKey_GreaterEqual(publicKey, null);
    }

    public void setPublicKey_GreaterEqual(String publicKey, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = publicKey;
        RangeQueryBuilder builder = regRangeQ("publicKey", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPublicKey_LessEqual(String publicKey) {
        setPublicKey_LessEqual(publicKey, null);
    }

    public void setPublicKey_LessEqual(String publicKey, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = publicKey;
        RangeQueryBuilder builder = regRangeQ("publicKey", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPublicKey_Exists() {
        setPublicKey_Exists(null);
    }

    public void setPublicKey_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("publicKey");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setPublicKey_CommonTerms(String publicKey) {
        setPublicKey_CommonTerms(publicKey, null);
    }

    @Deprecated
    public void setPublicKey_CommonTerms(String publicKey, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("publicKey", publicKey);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsPasskeyCQ addOrderBy_PublicKey_Asc() {
        regOBA("publicKey");
        return this;
    }

    public BsPasskeyCQ addOrderBy_PublicKey_Desc() {
        regOBD("publicKey");
        return this;
    }

    public void setSignCount_Equal(Long signCount) {
        setSignCount_Term(signCount, null);
    }

    public void setSignCount_Equal(Long signCount, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setSignCount_Term(signCount, opLambda);
    }

    public void setSignCount_Term(Long signCount) {
        setSignCount_Term(signCount, null);
    }

    public void setSignCount_Term(Long signCount, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("signCount", signCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_NotEqual(Long signCount) {
        setSignCount_NotTerm(signCount, null);
    }

    public void setSignCount_NotTerm(Long signCount) {
        setSignCount_NotTerm(signCount, null);
    }

    public void setSignCount_NotEqual(Long signCount, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setSignCount_NotTerm(signCount, opLambda);
    }

    public void setSignCount_NotTerm(Long signCount, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setSignCount_Term(signCount), opLambda);
    }

    public void setSignCount_Terms(Collection<Long> signCountList) {
        setSignCount_Terms(signCountList, null);
    }

    public void setSignCount_Terms(Collection<Long> signCountList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("signCount", signCountList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_InScope(Collection<Long> signCountList) {
        setSignCount_Terms(signCountList, null);
    }

    public void setSignCount_InScope(Collection<Long> signCountList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setSignCount_Terms(signCountList, opLambda);
    }

    public void setSignCount_Match(Long signCount) {
        setSignCount_Match(signCount, null);
    }

    public void setSignCount_Match(Long signCount, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("signCount", signCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_MatchPhrase(Long signCount) {
        setSignCount_MatchPhrase(signCount, null);
    }

    public void setSignCount_MatchPhrase(Long signCount, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("signCount", signCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_MatchPhrasePrefix(Long signCount) {
        setSignCount_MatchPhrasePrefix(signCount, null);
    }

    public void setSignCount_MatchPhrasePrefix(Long signCount, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("signCount", signCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_Fuzzy(Long signCount) {
        setSignCount_Fuzzy(signCount, null);
    }

    public void setSignCount_Fuzzy(Long signCount, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("signCount", signCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_GreaterThan(Long signCount) {
        setSignCount_GreaterThan(signCount, null);
    }

    public void setSignCount_GreaterThan(Long signCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = signCount;
        RangeQueryBuilder builder = regRangeQ("signCount", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_LessThan(Long signCount) {
        setSignCount_LessThan(signCount, null);
    }

    public void setSignCount_LessThan(Long signCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = signCount;
        RangeQueryBuilder builder = regRangeQ("signCount", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_GreaterEqual(Long signCount) {
        setSignCount_GreaterEqual(signCount, null);
    }

    public void setSignCount_GreaterEqual(Long signCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = signCount;
        RangeQueryBuilder builder = regRangeQ("signCount", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_LessEqual(Long signCount) {
        setSignCount_LessEqual(signCount, null);
    }

    public void setSignCount_LessEqual(Long signCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = signCount;
        RangeQueryBuilder builder = regRangeQ("signCount", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_Exists() {
        setSignCount_Exists(null);
    }

    public void setSignCount_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("signCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setSignCount_CommonTerms(Long signCount) {
        setSignCount_CommonTerms(signCount, null);
    }

    @Deprecated
    public void setSignCount_CommonTerms(Long signCount, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("signCount", signCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsPasskeyCQ addOrderBy_SignCount_Asc() {
        regOBA("signCount");
        return this;
    }

    public BsPasskeyCQ addOrderBy_SignCount_Desc() {
        regOBD("signCount");
        return this;
    }

    public void setTransports_Equal(String transports) {
        setTransports_Term(transports, null);
    }

    public void setTransports_Equal(String transports, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setTransports_Term(transports, opLambda);
    }

    public void setTransports_Term(String transports) {
        setTransports_Term(transports, null);
    }

    public void setTransports_Term(String transports, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("transports", transports);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTransports_NotEqual(String transports) {
        setTransports_NotTerm(transports, null);
    }

    public void setTransports_NotTerm(String transports) {
        setTransports_NotTerm(transports, null);
    }

    public void setTransports_NotEqual(String transports, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setTransports_NotTerm(transports, opLambda);
    }

    public void setTransports_NotTerm(String transports, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setTransports_Term(transports), opLambda);
    }

    public void setTransports_Terms(Collection<String> transportsList) {
        setTransports_Terms(transportsList, null);
    }

    public void setTransports_Terms(Collection<String> transportsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("transports", transportsList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTransports_InScope(Collection<String> transportsList) {
        setTransports_Terms(transportsList, null);
    }

    public void setTransports_InScope(Collection<String> transportsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setTransports_Terms(transportsList, opLambda);
    }

    public void setTransports_Match(String transports) {
        setTransports_Match(transports, null);
    }

    public void setTransports_Match(String transports, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("transports", transports);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTransports_MatchPhrase(String transports) {
        setTransports_MatchPhrase(transports, null);
    }

    public void setTransports_MatchPhrase(String transports, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("transports", transports);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTransports_MatchPhrasePrefix(String transports) {
        setTransports_MatchPhrasePrefix(transports, null);
    }

    public void setTransports_MatchPhrasePrefix(String transports, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("transports", transports);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTransports_Fuzzy(String transports) {
        setTransports_Fuzzy(transports, null);
    }

    public void setTransports_Fuzzy(String transports, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("transports", transports);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTransports_Prefix(String transports) {
        setTransports_Prefix(transports, null);
    }

    public void setTransports_Prefix(String transports, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("transports", transports);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTransports_Wildcard(String transports) {
        setTransports_Wildcard(transports, null);
    }

    public void setTransports_Wildcard(String transports, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("transports", transports);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTransports_Regexp(String transports) {
        setTransports_Regexp(transports, null);
    }

    public void setTransports_Regexp(String transports, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("transports", transports);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTransports_SpanTerm(String transports) {
        setTransports_SpanTerm("transports", null);
    }

    public void setTransports_SpanTerm(String transports, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("transports", transports);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTransports_GreaterThan(String transports) {
        setTransports_GreaterThan(transports, null);
    }

    public void setTransports_GreaterThan(String transports, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = transports;
        RangeQueryBuilder builder = regRangeQ("transports", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTransports_LessThan(String transports) {
        setTransports_LessThan(transports, null);
    }

    public void setTransports_LessThan(String transports, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = transports;
        RangeQueryBuilder builder = regRangeQ("transports", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTransports_GreaterEqual(String transports) {
        setTransports_GreaterEqual(transports, null);
    }

    public void setTransports_GreaterEqual(String transports, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = transports;
        RangeQueryBuilder builder = regRangeQ("transports", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTransports_LessEqual(String transports) {
        setTransports_LessEqual(transports, null);
    }

    public void setTransports_LessEqual(String transports, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = transports;
        RangeQueryBuilder builder = regRangeQ("transports", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTransports_Exists() {
        setTransports_Exists(null);
    }

    public void setTransports_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("transports");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setTransports_CommonTerms(String transports) {
        setTransports_CommonTerms(transports, null);
    }

    @Deprecated
    public void setTransports_CommonTerms(String transports, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("transports", transports);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsPasskeyCQ addOrderBy_Transports_Asc() {
        regOBA("transports");
        return this;
    }

    public BsPasskeyCQ addOrderBy_Transports_Desc() {
        regOBD("transports");
        return this;
    }

    public void setUserHandle_Equal(String userHandle) {
        setUserHandle_Term(userHandle, null);
    }

    public void setUserHandle_Equal(String userHandle, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUserHandle_Term(userHandle, opLambda);
    }

    public void setUserHandle_Term(String userHandle) {
        setUserHandle_Term(userHandle, null);
    }

    public void setUserHandle_Term(String userHandle, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("userHandle", userHandle);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserHandle_NotEqual(String userHandle) {
        setUserHandle_NotTerm(userHandle, null);
    }

    public void setUserHandle_NotTerm(String userHandle) {
        setUserHandle_NotTerm(userHandle, null);
    }

    public void setUserHandle_NotEqual(String userHandle, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setUserHandle_NotTerm(userHandle, opLambda);
    }

    public void setUserHandle_NotTerm(String userHandle, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setUserHandle_Term(userHandle), opLambda);
    }

    public void setUserHandle_Terms(Collection<String> userHandleList) {
        setUserHandle_Terms(userHandleList, null);
    }

    public void setUserHandle_Terms(Collection<String> userHandleList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("userHandle", userHandleList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserHandle_InScope(Collection<String> userHandleList) {
        setUserHandle_Terms(userHandleList, null);
    }

    public void setUserHandle_InScope(Collection<String> userHandleList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUserHandle_Terms(userHandleList, opLambda);
    }

    public void setUserHandle_Match(String userHandle) {
        setUserHandle_Match(userHandle, null);
    }

    public void setUserHandle_Match(String userHandle, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("userHandle", userHandle);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserHandle_MatchPhrase(String userHandle) {
        setUserHandle_MatchPhrase(userHandle, null);
    }

    public void setUserHandle_MatchPhrase(String userHandle, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("userHandle", userHandle);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserHandle_MatchPhrasePrefix(String userHandle) {
        setUserHandle_MatchPhrasePrefix(userHandle, null);
    }

    public void setUserHandle_MatchPhrasePrefix(String userHandle, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("userHandle", userHandle);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserHandle_Fuzzy(String userHandle) {
        setUserHandle_Fuzzy(userHandle, null);
    }

    public void setUserHandle_Fuzzy(String userHandle, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("userHandle", userHandle);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserHandle_Prefix(String userHandle) {
        setUserHandle_Prefix(userHandle, null);
    }

    public void setUserHandle_Prefix(String userHandle, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("userHandle", userHandle);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserHandle_Wildcard(String userHandle) {
        setUserHandle_Wildcard(userHandle, null);
    }

    public void setUserHandle_Wildcard(String userHandle, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("userHandle", userHandle);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserHandle_Regexp(String userHandle) {
        setUserHandle_Regexp(userHandle, null);
    }

    public void setUserHandle_Regexp(String userHandle, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("userHandle", userHandle);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserHandle_SpanTerm(String userHandle) {
        setUserHandle_SpanTerm("userHandle", null);
    }

    public void setUserHandle_SpanTerm(String userHandle, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("userHandle", userHandle);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserHandle_GreaterThan(String userHandle) {
        setUserHandle_GreaterThan(userHandle, null);
    }

    public void setUserHandle_GreaterThan(String userHandle, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userHandle;
        RangeQueryBuilder builder = regRangeQ("userHandle", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserHandle_LessThan(String userHandle) {
        setUserHandle_LessThan(userHandle, null);
    }

    public void setUserHandle_LessThan(String userHandle, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userHandle;
        RangeQueryBuilder builder = regRangeQ("userHandle", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserHandle_GreaterEqual(String userHandle) {
        setUserHandle_GreaterEqual(userHandle, null);
    }

    public void setUserHandle_GreaterEqual(String userHandle, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userHandle;
        RangeQueryBuilder builder = regRangeQ("userHandle", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserHandle_LessEqual(String userHandle) {
        setUserHandle_LessEqual(userHandle, null);
    }

    public void setUserHandle_LessEqual(String userHandle, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userHandle;
        RangeQueryBuilder builder = regRangeQ("userHandle", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserHandle_Exists() {
        setUserHandle_Exists(null);
    }

    public void setUserHandle_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("userHandle");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setUserHandle_CommonTerms(String userHandle) {
        setUserHandle_CommonTerms(userHandle, null);
    }

    @Deprecated
    public void setUserHandle_CommonTerms(String userHandle, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("userHandle", userHandle);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsPasskeyCQ addOrderBy_UserHandle_Asc() {
        regOBA("userHandle");
        return this;
    }

    public BsPasskeyCQ addOrderBy_UserHandle_Desc() {
        regOBD("userHandle");
        return this;
    }

    public void setUserName_Equal(String userName) {
        setUserName_Term(userName, null);
    }

    public void setUserName_Equal(String userName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUserName_Term(userName, opLambda);
    }

    public void setUserName_Term(String userName) {
        setUserName_Term(userName, null);
    }

    public void setUserName_Term(String userName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("userName", userName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserName_NotEqual(String userName) {
        setUserName_NotTerm(userName, null);
    }

    public void setUserName_NotTerm(String userName) {
        setUserName_NotTerm(userName, null);
    }

    public void setUserName_NotEqual(String userName, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setUserName_NotTerm(userName, opLambda);
    }

    public void setUserName_NotTerm(String userName, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setUserName_Term(userName), opLambda);
    }

    public void setUserName_Terms(Collection<String> userNameList) {
        setUserName_Terms(userNameList, null);
    }

    public void setUserName_Terms(Collection<String> userNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("userName", userNameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserName_InScope(Collection<String> userNameList) {
        setUserName_Terms(userNameList, null);
    }

    public void setUserName_InScope(Collection<String> userNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUserName_Terms(userNameList, opLambda);
    }

    public void setUserName_Match(String userName) {
        setUserName_Match(userName, null);
    }

    public void setUserName_Match(String userName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("userName", userName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserName_MatchPhrase(String userName) {
        setUserName_MatchPhrase(userName, null);
    }

    public void setUserName_MatchPhrase(String userName, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("userName", userName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserName_MatchPhrasePrefix(String userName) {
        setUserName_MatchPhrasePrefix(userName, null);
    }

    public void setUserName_MatchPhrasePrefix(String userName, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("userName", userName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserName_Fuzzy(String userName) {
        setUserName_Fuzzy(userName, null);
    }

    public void setUserName_Fuzzy(String userName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("userName", userName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserName_Prefix(String userName) {
        setUserName_Prefix(userName, null);
    }

    public void setUserName_Prefix(String userName, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("userName", userName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserName_Wildcard(String userName) {
        setUserName_Wildcard(userName, null);
    }

    public void setUserName_Wildcard(String userName, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("userName", userName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserName_Regexp(String userName) {
        setUserName_Regexp(userName, null);
    }

    public void setUserName_Regexp(String userName, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("userName", userName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserName_SpanTerm(String userName) {
        setUserName_SpanTerm("userName", null);
    }

    public void setUserName_SpanTerm(String userName, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("userName", userName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserName_GreaterThan(String userName) {
        setUserName_GreaterThan(userName, null);
    }

    public void setUserName_GreaterThan(String userName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userName;
        RangeQueryBuilder builder = regRangeQ("userName", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserName_LessThan(String userName) {
        setUserName_LessThan(userName, null);
    }

    public void setUserName_LessThan(String userName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userName;
        RangeQueryBuilder builder = regRangeQ("userName", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserName_GreaterEqual(String userName) {
        setUserName_GreaterEqual(userName, null);
    }

    public void setUserName_GreaterEqual(String userName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userName;
        RangeQueryBuilder builder = regRangeQ("userName", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserName_LessEqual(String userName) {
        setUserName_LessEqual(userName, null);
    }

    public void setUserName_LessEqual(String userName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userName;
        RangeQueryBuilder builder = regRangeQ("userName", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserName_Exists() {
        setUserName_Exists(null);
    }

    public void setUserName_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("userName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setUserName_CommonTerms(String userName) {
        setUserName_CommonTerms(userName, null);
    }

    @Deprecated
    public void setUserName_CommonTerms(String userName, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("userName", userName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsPasskeyCQ addOrderBy_UserName_Asc() {
        regOBA("userName");
        return this;
    }

    public BsPasskeyCQ addOrderBy_UserName_Desc() {
        regOBD("userName");
        return this;
    }

}
