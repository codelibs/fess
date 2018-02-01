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
import org.codelibs.fess.es.config.cbean.cq.LabelToRoleCQ;
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
public abstract class BsLabelToRoleCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "label_to_role";
    }

    @Override
    public String xgetAliasName() {
        return "label_to_role";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<LabelToRoleCQ> queryLambda,
            ScoreFunctionCall<ScoreFunctionCreator<LabelToRoleCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        LabelToRoleCQ cq = new LabelToRoleCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                LabelToRoleCQ cf = new LabelToRoleCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<LabelToRoleCQ, LabelToRoleCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<LabelToRoleCQ, LabelToRoleCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<LabelToRoleCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<LabelToRoleCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<LabelToRoleCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<LabelToRoleCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        LabelToRoleCQ mustQuery = new LabelToRoleCQ();
        LabelToRoleCQ shouldQuery = new LabelToRoleCQ();
        LabelToRoleCQ mustNotQuery = new LabelToRoleCQ();
        LabelToRoleCQ filterQuery = new LabelToRoleCQ();
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

    public BsLabelToRoleCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    public BsLabelToRoleCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setLabelTypeId_Equal(String labelTypeId) {
        setLabelTypeId_Term(labelTypeId, null);
    }

    public void setLabelTypeId_Equal(String labelTypeId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setLabelTypeId_Term(labelTypeId, opLambda);
    }

    public void setLabelTypeId_Term(String labelTypeId) {
        setLabelTypeId_Term(labelTypeId, null);
    }

    public void setLabelTypeId_Term(String labelTypeId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_NotEqual(String labelTypeId) {
        setLabelTypeId_NotTerm(labelTypeId, null);
    }

    public void setLabelTypeId_NotTerm(String labelTypeId) {
        setLabelTypeId_NotTerm(labelTypeId, null);
    }

    public void setLabelTypeId_NotEqual(String labelTypeId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setLabelTypeId_NotTerm(labelTypeId, opLambda);
    }

    public void setLabelTypeId_NotTerm(String labelTypeId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setLabelTypeId_Term(labelTypeId), opLambda);
    }

    public void setLabelTypeId_Terms(Collection<String> labelTypeIdList) {
        setLabelTypeId_Terms(labelTypeIdList, null);
    }

    public void setLabelTypeId_Terms(Collection<String> labelTypeIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("labelTypeId", labelTypeIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_InScope(Collection<String> labelTypeIdList) {
        setLabelTypeId_Terms(labelTypeIdList, null);
    }

    public void setLabelTypeId_InScope(Collection<String> labelTypeIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setLabelTypeId_Terms(labelTypeIdList, opLambda);
    }

    public void setLabelTypeId_Match(String labelTypeId) {
        setLabelTypeId_Match(labelTypeId, null);
    }

    public void setLabelTypeId_Match(String labelTypeId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_MatchPhrase(String labelTypeId) {
        setLabelTypeId_MatchPhrase(labelTypeId, null);
    }

    public void setLabelTypeId_MatchPhrase(String labelTypeId, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_MatchPhrasePrefix(String labelTypeId) {
        setLabelTypeId_MatchPhrasePrefix(labelTypeId, null);
    }

    public void setLabelTypeId_MatchPhrasePrefix(String labelTypeId, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_Fuzzy(String labelTypeId) {
        setLabelTypeId_Fuzzy(labelTypeId, null);
    }

    public void setLabelTypeId_Fuzzy(String labelTypeId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_Prefix(String labelTypeId) {
        setLabelTypeId_Prefix(labelTypeId, null);
    }

    public void setLabelTypeId_Prefix(String labelTypeId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_Wildcard(String labelTypeId) {
        setLabelTypeId_Wildcard(labelTypeId, null);
    }

    public void setLabelTypeId_Wildcard(String labelTypeId, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_Regexp(String labelTypeId) {
        setLabelTypeId_Regexp(labelTypeId, null);
    }

    public void setLabelTypeId_Regexp(String labelTypeId, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_SpanTerm(String labelTypeId) {
        setLabelTypeId_SpanTerm("labelTypeId", null);
    }

    public void setLabelTypeId_SpanTerm(String labelTypeId, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_GreaterThan(String labelTypeId) {
        setLabelTypeId_GreaterThan(labelTypeId, null);
    }

    public void setLabelTypeId_GreaterThan(String labelTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = labelTypeId;
        RangeQueryBuilder builder = regRangeQ("labelTypeId", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_LessThan(String labelTypeId) {
        setLabelTypeId_LessThan(labelTypeId, null);
    }

    public void setLabelTypeId_LessThan(String labelTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = labelTypeId;
        RangeQueryBuilder builder = regRangeQ("labelTypeId", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_GreaterEqual(String labelTypeId) {
        setLabelTypeId_GreaterEqual(labelTypeId, null);
    }

    public void setLabelTypeId_GreaterEqual(String labelTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = labelTypeId;
        RangeQueryBuilder builder = regRangeQ("labelTypeId", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_LessEqual(String labelTypeId) {
        setLabelTypeId_LessEqual(labelTypeId, null);
    }

    public void setLabelTypeId_LessEqual(String labelTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = labelTypeId;
        RangeQueryBuilder builder = regRangeQ("labelTypeId", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_Exists() {
        setLabelTypeId_Exists(null);
    }

    public void setLabelTypeId_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_CommonTerms(String labelTypeId) {
        setLabelTypeId_CommonTerms(labelTypeId, null);
    }

    public void setLabelTypeId_CommonTerms(String labelTypeId, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsLabelToRoleCQ addOrderBy_LabelTypeId_Asc() {
        regOBA("labelTypeId");
        return this;
    }

    public BsLabelToRoleCQ addOrderBy_LabelTypeId_Desc() {
        regOBD("labelTypeId");
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

    public BsLabelToRoleCQ addOrderBy_RoleTypeId_Asc() {
        regOBA("roleTypeId");
        return this;
    }

    public BsLabelToRoleCQ addOrderBy_RoleTypeId_Desc() {
        regOBD("roleTypeId");
        return this;
    }

}
