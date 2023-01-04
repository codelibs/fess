/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
import java.util.ArrayList;
import java.util.Collection;

import org.codelibs.fess.es.log.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.log.cbean.cq.UserInfoCQ;
import org.dbflute.cbean.ckey.ConditionKey;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.CommonTermsQueryBuilder;
import org.opensearch.index.query.ExistsQueryBuilder;
import org.opensearch.index.query.IdsQueryBuilder;
import org.opensearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.opensearch.index.query.MatchPhraseQueryBuilder;
import org.opensearch.index.query.MatchQueryBuilder;
import org.opensearch.index.query.RangeQueryBuilder;
import org.opensearch.index.query.TermQueryBuilder;
import org.opensearch.index.query.TermsQueryBuilder;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsUserInfoCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "user_info";
    }

    @Override
    public String xgetAliasName() {
        return "user_info";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<UserInfoCQ> queryLambda, ScoreFunctionCall<ScoreFunctionCreator<UserInfoCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        UserInfoCQ cq = new UserInfoCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                UserInfoCQ cf = new UserInfoCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<UserInfoCQ, UserInfoCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<UserInfoCQ, UserInfoCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<UserInfoCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<UserInfoCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<UserInfoCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<UserInfoCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        UserInfoCQ mustQuery = new UserInfoCQ();
        UserInfoCQ shouldQuery = new UserInfoCQ();
        UserInfoCQ mustNotQuery = new UserInfoCQ();
        UserInfoCQ filterQuery = new UserInfoCQ();
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
    public BsUserInfoCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsUserInfoCQ addOrderBy_Id_Desc() {
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

    public void setCreatedAt_NotTerm(LocalDateTime createdAt) {
        setCreatedAt_NotTerm(createdAt, null);
    }

    public void setCreatedAt_NotEqual(LocalDateTime createdAt, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setCreatedAt_NotTerm(createdAt, opLambda);
    }

    public void setCreatedAt_NotTerm(LocalDateTime createdAt, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setCreatedAt_Term(createdAt), opLambda);
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

    public void setCreatedAt_MatchPhrase(LocalDateTime createdAt, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_MatchPhrasePrefix(LocalDateTime createdAt) {
        setCreatedAt_MatchPhrasePrefix(createdAt, null);
    }

    public void setCreatedAt_MatchPhrasePrefix(LocalDateTime createdAt, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Fuzzy(LocalDateTime createdAt) {
        setCreatedAt_Fuzzy(createdAt, null);
    }

    public void setCreatedAt_Fuzzy(LocalDateTime createdAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_GreaterThan(LocalDateTime createdAt) {
        setCreatedAt_GreaterThan(createdAt, null);
    }

    public void setCreatedAt_GreaterThan(LocalDateTime createdAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(createdAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("createdAt", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_LessThan(LocalDateTime createdAt) {
        setCreatedAt_LessThan(createdAt, null);
    }

    public void setCreatedAt_LessThan(LocalDateTime createdAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(createdAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("createdAt", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_GreaterEqual(LocalDateTime createdAt) {
        setCreatedAt_GreaterEqual(createdAt, null);
    }

    public void setCreatedAt_GreaterEqual(LocalDateTime createdAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(createdAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("createdAt", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_LessEqual(LocalDateTime createdAt) {
        setCreatedAt_LessEqual(createdAt, null);
    }

    public void setCreatedAt_LessEqual(LocalDateTime createdAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(createdAt, "date_optional_time");
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
    public void setCreatedAt_CommonTerms(LocalDateTime createdAt) {
        setCreatedAt_CommonTerms(createdAt, null);
    }

    @Deprecated
    public void setCreatedAt_CommonTerms(LocalDateTime createdAt, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserInfoCQ addOrderBy_CreatedAt_Asc() {
        regOBA("createdAt");
        return this;
    }

    public BsUserInfoCQ addOrderBy_CreatedAt_Desc() {
        regOBD("createdAt");
        return this;
    }

    public void setUpdatedAt_Equal(LocalDateTime updatedAt) {
        setUpdatedAt_Term(updatedAt, null);
    }

    public void setUpdatedAt_Equal(LocalDateTime updatedAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUpdatedAt_Term(updatedAt, opLambda);
    }

    public void setUpdatedAt_Term(LocalDateTime updatedAt) {
        setUpdatedAt_Term(updatedAt, null);
    }

    public void setUpdatedAt_Term(LocalDateTime updatedAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("updatedAt", updatedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedAt_NotEqual(LocalDateTime updatedAt) {
        setUpdatedAt_NotTerm(updatedAt, null);
    }

    public void setUpdatedAt_NotTerm(LocalDateTime updatedAt) {
        setUpdatedAt_NotTerm(updatedAt, null);
    }

    public void setUpdatedAt_NotEqual(LocalDateTime updatedAt, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setUpdatedAt_NotTerm(updatedAt, opLambda);
    }

    public void setUpdatedAt_NotTerm(LocalDateTime updatedAt, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setUpdatedAt_Term(updatedAt), opLambda);
    }

    public void setUpdatedAt_Terms(Collection<LocalDateTime> updatedAtList) {
        setUpdatedAt_Terms(updatedAtList, null);
    }

    public void setUpdatedAt_Terms(Collection<LocalDateTime> updatedAtList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("updatedAt", updatedAtList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedAt_InScope(Collection<LocalDateTime> updatedAtList) {
        setUpdatedAt_Terms(updatedAtList, null);
    }

    public void setUpdatedAt_InScope(Collection<LocalDateTime> updatedAtList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUpdatedAt_Terms(updatedAtList, opLambda);
    }

    public void setUpdatedAt_Match(LocalDateTime updatedAt) {
        setUpdatedAt_Match(updatedAt, null);
    }

    public void setUpdatedAt_Match(LocalDateTime updatedAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("updatedAt", updatedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedAt_MatchPhrase(LocalDateTime updatedAt) {
        setUpdatedAt_MatchPhrase(updatedAt, null);
    }

    public void setUpdatedAt_MatchPhrase(LocalDateTime updatedAt, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("updatedAt", updatedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedAt_MatchPhrasePrefix(LocalDateTime updatedAt) {
        setUpdatedAt_MatchPhrasePrefix(updatedAt, null);
    }

    public void setUpdatedAt_MatchPhrasePrefix(LocalDateTime updatedAt, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("updatedAt", updatedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedAt_Fuzzy(LocalDateTime updatedAt) {
        setUpdatedAt_Fuzzy(updatedAt, null);
    }

    public void setUpdatedAt_Fuzzy(LocalDateTime updatedAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("updatedAt", updatedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedAt_GreaterThan(LocalDateTime updatedAt) {
        setUpdatedAt_GreaterThan(updatedAt, null);
    }

    public void setUpdatedAt_GreaterThan(LocalDateTime updatedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(updatedAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("updatedAt", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedAt_LessThan(LocalDateTime updatedAt) {
        setUpdatedAt_LessThan(updatedAt, null);
    }

    public void setUpdatedAt_LessThan(LocalDateTime updatedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(updatedAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("updatedAt", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedAt_GreaterEqual(LocalDateTime updatedAt) {
        setUpdatedAt_GreaterEqual(updatedAt, null);
    }

    public void setUpdatedAt_GreaterEqual(LocalDateTime updatedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(updatedAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("updatedAt", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedAt_LessEqual(LocalDateTime updatedAt) {
        setUpdatedAt_LessEqual(updatedAt, null);
    }

    public void setUpdatedAt_LessEqual(LocalDateTime updatedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(updatedAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("updatedAt", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedAt_Exists() {
        setUpdatedAt_Exists(null);
    }

    public void setUpdatedAt_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("updatedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setUpdatedAt_CommonTerms(LocalDateTime updatedAt) {
        setUpdatedAt_CommonTerms(updatedAt, null);
    }

    @Deprecated
    public void setUpdatedAt_CommonTerms(LocalDateTime updatedAt, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("updatedAt", updatedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserInfoCQ addOrderBy_UpdatedAt_Asc() {
        regOBA("updatedAt");
        return this;
    }

    public BsUserInfoCQ addOrderBy_UpdatedAt_Desc() {
        regOBD("updatedAt");
        return this;
    }

}
