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
import org.codelibs.fess.es.config.cbean.cq.WebConfigToRoleCQ;
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
public abstract class BsWebConfigToRoleCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "web_config_to_role";
    }

    @Override
    public String xgetAliasName() {
        return "web_config_to_role";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<WebConfigToRoleCQ> queryLambda,
            ScoreFunctionCall<ScoreFunctionCreator<WebConfigToRoleCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        WebConfigToRoleCQ cq = new WebConfigToRoleCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                WebConfigToRoleCQ cf = new WebConfigToRoleCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<WebConfigToRoleCQ, WebConfigToRoleCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<WebConfigToRoleCQ, WebConfigToRoleCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<WebConfigToRoleCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<WebConfigToRoleCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<WebConfigToRoleCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<WebConfigToRoleCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        WebConfigToRoleCQ mustQuery = new WebConfigToRoleCQ();
        WebConfigToRoleCQ shouldQuery = new WebConfigToRoleCQ();
        WebConfigToRoleCQ mustNotQuery = new WebConfigToRoleCQ();
        WebConfigToRoleCQ filterQuery = new WebConfigToRoleCQ();
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

    public BsWebConfigToRoleCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    public BsWebConfigToRoleCQ addOrderBy_Id_Desc() {
        regOBD("_id");
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

    public BsWebConfigToRoleCQ addOrderBy_RoleTypeId_Asc() {
        regOBA("roleTypeId");
        return this;
    }

    public BsWebConfigToRoleCQ addOrderBy_RoleTypeId_Desc() {
        regOBD("roleTypeId");
        return this;
    }

    public void setWebConfigId_Equal(String webConfigId) {
        setWebConfigId_Term(webConfigId, null);
    }

    public void setWebConfigId_Equal(String webConfigId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setWebConfigId_Term(webConfigId, opLambda);
    }

    public void setWebConfigId_Term(String webConfigId) {
        setWebConfigId_Term(webConfigId, null);
    }

    public void setWebConfigId_Term(String webConfigId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_NotEqual(String webConfigId) {
        setWebConfigId_NotTerm(webConfigId, null);
    }

    public void setWebConfigId_NotTerm(String webConfigId) {
        setWebConfigId_NotTerm(webConfigId, null);
    }

    public void setWebConfigId_NotEqual(String webConfigId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setWebConfigId_NotTerm(webConfigId, opLambda);
    }

    public void setWebConfigId_NotTerm(String webConfigId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setWebConfigId_Term(webConfigId), opLambda);
    }

    public void setWebConfigId_Terms(Collection<String> webConfigIdList) {
        setWebConfigId_Terms(webConfigIdList, null);
    }

    public void setWebConfigId_Terms(Collection<String> webConfigIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("webConfigId", webConfigIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_InScope(Collection<String> webConfigIdList) {
        setWebConfigId_Terms(webConfigIdList, null);
    }

    public void setWebConfigId_InScope(Collection<String> webConfigIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setWebConfigId_Terms(webConfigIdList, opLambda);
    }

    public void setWebConfigId_Match(String webConfigId) {
        setWebConfigId_Match(webConfigId, null);
    }

    public void setWebConfigId_Match(String webConfigId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_MatchPhrase(String webConfigId) {
        setWebConfigId_MatchPhrase(webConfigId, null);
    }

    public void setWebConfigId_MatchPhrase(String webConfigId, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_MatchPhrasePrefix(String webConfigId) {
        setWebConfigId_MatchPhrasePrefix(webConfigId, null);
    }

    public void setWebConfigId_MatchPhrasePrefix(String webConfigId, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Fuzzy(String webConfigId) {
        setWebConfigId_Fuzzy(webConfigId, null);
    }

    public void setWebConfigId_Fuzzy(String webConfigId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Prefix(String webConfigId) {
        setWebConfigId_Prefix(webConfigId, null);
    }

    public void setWebConfigId_Prefix(String webConfigId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Wildcard(String webConfigId) {
        setWebConfigId_Wildcard(webConfigId, null);
    }

    public void setWebConfigId_Wildcard(String webConfigId, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Regexp(String webConfigId) {
        setWebConfigId_Regexp(webConfigId, null);
    }

    public void setWebConfigId_Regexp(String webConfigId, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_SpanTerm(String webConfigId) {
        setWebConfigId_SpanTerm("webConfigId", null);
    }

    public void setWebConfigId_SpanTerm(String webConfigId, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_GreaterThan(String webConfigId) {
        setWebConfigId_GreaterThan(webConfigId, null);
    }

    public void setWebConfigId_GreaterThan(String webConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = webConfigId;
        RangeQueryBuilder builder = regRangeQ("webConfigId", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_LessThan(String webConfigId) {
        setWebConfigId_LessThan(webConfigId, null);
    }

    public void setWebConfigId_LessThan(String webConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = webConfigId;
        RangeQueryBuilder builder = regRangeQ("webConfigId", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_GreaterEqual(String webConfigId) {
        setWebConfigId_GreaterEqual(webConfigId, null);
    }

    public void setWebConfigId_GreaterEqual(String webConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = webConfigId;
        RangeQueryBuilder builder = regRangeQ("webConfigId", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_LessEqual(String webConfigId) {
        setWebConfigId_LessEqual(webConfigId, null);
    }

    public void setWebConfigId_LessEqual(String webConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = webConfigId;
        RangeQueryBuilder builder = regRangeQ("webConfigId", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Exists() {
        setWebConfigId_Exists(null);
    }

    public void setWebConfigId_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("webConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_CommonTerms(String webConfigId) {
        setWebConfigId_CommonTerms(webConfigId, null);
    }

    public void setWebConfigId_CommonTerms(String webConfigId, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("webConfigId", webConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsWebConfigToRoleCQ addOrderBy_WebConfigId_Asc() {
        regOBA("webConfigId");
        return this;
    }

    public BsWebConfigToRoleCQ addOrderBy_WebConfigId_Desc() {
        regOBD("webConfigId");
        return this;
    }

}
