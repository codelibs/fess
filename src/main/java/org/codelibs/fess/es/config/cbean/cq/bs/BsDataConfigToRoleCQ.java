/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
import java.util.ArrayList;
import java.util.Collection;

import org.codelibs.fess.es.config.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.config.cbean.cq.DataConfigToRoleCQ;
import org.dbflute.cbean.ckey.ConditionKey;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.CommonTermsQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.RegexpQueryBuilder;
import org.elasticsearch.index.query.SpanTermQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsDataConfigToRoleCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "data_config_to_role";
    }

    @Override
    public String xgetAliasName() {
        return "data_config_to_role";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<DataConfigToRoleCQ> queryLambda,
            ScoreFunctionCall<ScoreFunctionCreator<DataConfigToRoleCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        DataConfigToRoleCQ cq = new DataConfigToRoleCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                DataConfigToRoleCQ cf = new DataConfigToRoleCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<DataConfigToRoleCQ, DataConfigToRoleCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<DataConfigToRoleCQ, DataConfigToRoleCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<DataConfigToRoleCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<DataConfigToRoleCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<DataConfigToRoleCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<DataConfigToRoleCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        DataConfigToRoleCQ mustQuery = new DataConfigToRoleCQ();
        DataConfigToRoleCQ shouldQuery = new DataConfigToRoleCQ();
        DataConfigToRoleCQ mustNotQuery = new DataConfigToRoleCQ();
        DataConfigToRoleCQ filterQuery = new DataConfigToRoleCQ();
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

    public BsDataConfigToRoleCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    public BsDataConfigToRoleCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setDataConfigId_Equal(String dataConfigId) {
        setDataConfigId_Term(dataConfigId, null);
    }

    public void setDataConfigId_Equal(String dataConfigId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setDataConfigId_Term(dataConfigId, opLambda);
    }

    public void setDataConfigId_Term(String dataConfigId) {
        setDataConfigId_Term(dataConfigId, null);
    }

    public void setDataConfigId_Term(String dataConfigId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("dataConfigId", dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_NotEqual(String dataConfigId) {
        setDataConfigId_NotTerm(dataConfigId, null);
    }

    public void setDataConfigId_NotTerm(String dataConfigId) {
        setDataConfigId_NotTerm(dataConfigId, null);
    }

    public void setDataConfigId_NotEqual(String dataConfigId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setDataConfigId_NotTerm(dataConfigId, opLambda);
    }

    public void setDataConfigId_NotTerm(String dataConfigId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setDataConfigId_Term(dataConfigId), opLambda);
    }

    public void setDataConfigId_Terms(Collection<String> dataConfigIdList) {
        setDataConfigId_Terms(dataConfigIdList, null);
    }

    public void setDataConfigId_Terms(Collection<String> dataConfigIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("dataConfigId", dataConfigIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_InScope(Collection<String> dataConfigIdList) {
        setDataConfigId_Terms(dataConfigIdList, null);
    }

    public void setDataConfigId_InScope(Collection<String> dataConfigIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setDataConfigId_Terms(dataConfigIdList, opLambda);
    }

    public void setDataConfigId_Match(String dataConfigId) {
        setDataConfigId_Match(dataConfigId, null);
    }

    public void setDataConfigId_Match(String dataConfigId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("dataConfigId", dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_MatchPhrase(String dataConfigId) {
        setDataConfigId_MatchPhrase(dataConfigId, null);
    }

    public void setDataConfigId_MatchPhrase(String dataConfigId, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("dataConfigId", dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_MatchPhrasePrefix(String dataConfigId) {
        setDataConfigId_MatchPhrasePrefix(dataConfigId, null);
    }

    public void setDataConfigId_MatchPhrasePrefix(String dataConfigId, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("dataConfigId", dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_Fuzzy(String dataConfigId) {
        setDataConfigId_Fuzzy(dataConfigId, null);
    }

    public void setDataConfigId_Fuzzy(String dataConfigId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("dataConfigId", dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_Prefix(String dataConfigId) {
        setDataConfigId_Prefix(dataConfigId, null);
    }

    public void setDataConfigId_Prefix(String dataConfigId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("dataConfigId", dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_Wildcard(String dataConfigId) {
        setDataConfigId_Wildcard(dataConfigId, null);
    }

    public void setDataConfigId_Wildcard(String dataConfigId, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("dataConfigId", dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_Regexp(String dataConfigId) {
        setDataConfigId_Regexp(dataConfigId, null);
    }

    public void setDataConfigId_Regexp(String dataConfigId, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("dataConfigId", dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_SpanTerm(String dataConfigId) {
        setDataConfigId_SpanTerm("dataConfigId", null);
    }

    public void setDataConfigId_SpanTerm(String dataConfigId, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("dataConfigId", dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_GreaterThan(String dataConfigId) {
        setDataConfigId_GreaterThan(dataConfigId, null);
    }

    public void setDataConfigId_GreaterThan(String dataConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = dataConfigId;
        RangeQueryBuilder builder = regRangeQ("dataConfigId", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_LessThan(String dataConfigId) {
        setDataConfigId_LessThan(dataConfigId, null);
    }

    public void setDataConfigId_LessThan(String dataConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = dataConfigId;
        RangeQueryBuilder builder = regRangeQ("dataConfigId", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_GreaterEqual(String dataConfigId) {
        setDataConfigId_GreaterEqual(dataConfigId, null);
    }

    public void setDataConfigId_GreaterEqual(String dataConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = dataConfigId;
        RangeQueryBuilder builder = regRangeQ("dataConfigId", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_LessEqual(String dataConfigId) {
        setDataConfigId_LessEqual(dataConfigId, null);
    }

    public void setDataConfigId_LessEqual(String dataConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = dataConfigId;
        RangeQueryBuilder builder = regRangeQ("dataConfigId", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_Exists() {
        setDataConfigId_Exists(null);
    }

    public void setDataConfigId_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("dataConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_CommonTerms(String dataConfigId) {
        setDataConfigId_CommonTerms(dataConfigId, null);
    }

    public void setDataConfigId_CommonTerms(String dataConfigId, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("dataConfigId", dataConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsDataConfigToRoleCQ addOrderBy_DataConfigId_Asc() {
        regOBA("dataConfigId");
        return this;
    }

    public BsDataConfigToRoleCQ addOrderBy_DataConfigId_Desc() {
        regOBD("dataConfigId");
        return this;
    }

    public void setRoleTypeId_Equal(String roleTypeId) {
        setRoleTypeId_Term(roleTypeId, null);
    }

    public void setRoleTypeId_Equal(String roleTypeId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setRoleTypeId_Term(roleTypeId, opLambda);
    }

    public void setRoleTypeId_Term(String roleTypeId) {
        setRoleTypeId_Term(roleTypeId, null);
    }

    public void setRoleTypeId_Term(String roleTypeId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("roleTypeId", roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_NotEqual(String roleTypeId) {
        setRoleTypeId_NotTerm(roleTypeId, null);
    }

    public void setRoleTypeId_NotTerm(String roleTypeId) {
        setRoleTypeId_NotTerm(roleTypeId, null);
    }

    public void setRoleTypeId_NotEqual(String roleTypeId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setRoleTypeId_NotTerm(roleTypeId, opLambda);
    }

    public void setRoleTypeId_NotTerm(String roleTypeId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setRoleTypeId_Term(roleTypeId), opLambda);
    }

    public void setRoleTypeId_Terms(Collection<String> roleTypeIdList) {
        setRoleTypeId_Terms(roleTypeIdList, null);
    }

    public void setRoleTypeId_Terms(Collection<String> roleTypeIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("roleTypeId", roleTypeIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_InScope(Collection<String> roleTypeIdList) {
        setRoleTypeId_Terms(roleTypeIdList, null);
    }

    public void setRoleTypeId_InScope(Collection<String> roleTypeIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setRoleTypeId_Terms(roleTypeIdList, opLambda);
    }

    public void setRoleTypeId_Match(String roleTypeId) {
        setRoleTypeId_Match(roleTypeId, null);
    }

    public void setRoleTypeId_Match(String roleTypeId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("roleTypeId", roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_MatchPhrase(String roleTypeId) {
        setRoleTypeId_MatchPhrase(roleTypeId, null);
    }

    public void setRoleTypeId_MatchPhrase(String roleTypeId, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("roleTypeId", roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_MatchPhrasePrefix(String roleTypeId) {
        setRoleTypeId_MatchPhrasePrefix(roleTypeId, null);
    }

    public void setRoleTypeId_MatchPhrasePrefix(String roleTypeId, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("roleTypeId", roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_Fuzzy(String roleTypeId) {
        setRoleTypeId_Fuzzy(roleTypeId, null);
    }

    public void setRoleTypeId_Fuzzy(String roleTypeId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("roleTypeId", roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_Prefix(String roleTypeId) {
        setRoleTypeId_Prefix(roleTypeId, null);
    }

    public void setRoleTypeId_Prefix(String roleTypeId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("roleTypeId", roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_Wildcard(String roleTypeId) {
        setRoleTypeId_Wildcard(roleTypeId, null);
    }

    public void setRoleTypeId_Wildcard(String roleTypeId, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("roleTypeId", roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_Regexp(String roleTypeId) {
        setRoleTypeId_Regexp(roleTypeId, null);
    }

    public void setRoleTypeId_Regexp(String roleTypeId, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("roleTypeId", roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_SpanTerm(String roleTypeId) {
        setRoleTypeId_SpanTerm("roleTypeId", null);
    }

    public void setRoleTypeId_SpanTerm(String roleTypeId, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("roleTypeId", roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_GreaterThan(String roleTypeId) {
        setRoleTypeId_GreaterThan(roleTypeId, null);
    }

    public void setRoleTypeId_GreaterThan(String roleTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = roleTypeId;
        RangeQueryBuilder builder = regRangeQ("roleTypeId", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_LessThan(String roleTypeId) {
        setRoleTypeId_LessThan(roleTypeId, null);
    }

    public void setRoleTypeId_LessThan(String roleTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = roleTypeId;
        RangeQueryBuilder builder = regRangeQ("roleTypeId", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_GreaterEqual(String roleTypeId) {
        setRoleTypeId_GreaterEqual(roleTypeId, null);
    }

    public void setRoleTypeId_GreaterEqual(String roleTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = roleTypeId;
        RangeQueryBuilder builder = regRangeQ("roleTypeId", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_LessEqual(String roleTypeId) {
        setRoleTypeId_LessEqual(roleTypeId, null);
    }

    public void setRoleTypeId_LessEqual(String roleTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = roleTypeId;
        RangeQueryBuilder builder = regRangeQ("roleTypeId", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_Exists() {
        setRoleTypeId_Exists(null);
    }

    public void setRoleTypeId_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("roleTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_CommonTerms(String roleTypeId) {
        setRoleTypeId_CommonTerms(roleTypeId, null);
    }

    public void setRoleTypeId_CommonTerms(String roleTypeId, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("roleTypeId", roleTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsDataConfigToRoleCQ addOrderBy_RoleTypeId_Asc() {
        regOBA("roleTypeId");
        return this;
    }

    public BsDataConfigToRoleCQ addOrderBy_RoleTypeId_Desc() {
        regOBD("roleTypeId");
        return this;
    }

}
