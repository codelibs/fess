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
import org.codelibs.fess.es.config.cbean.cq.FileConfigToRoleCQ;
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
public abstract class BsFileConfigToRoleCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "file_config_to_role";
    }

    @Override
    public String xgetAliasName() {
        return "file_config_to_role";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<FileConfigToRoleCQ> queryLambda,
            ScoreFunctionCall<ScoreFunctionCreator<FileConfigToRoleCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        FileConfigToRoleCQ cq = new FileConfigToRoleCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                FileConfigToRoleCQ cf = new FileConfigToRoleCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<FileConfigToRoleCQ, FileConfigToRoleCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<FileConfigToRoleCQ, FileConfigToRoleCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<FileConfigToRoleCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<FileConfigToRoleCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<FileConfigToRoleCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<FileConfigToRoleCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        FileConfigToRoleCQ mustQuery = new FileConfigToRoleCQ();
        FileConfigToRoleCQ shouldQuery = new FileConfigToRoleCQ();
        FileConfigToRoleCQ mustNotQuery = new FileConfigToRoleCQ();
        FileConfigToRoleCQ filterQuery = new FileConfigToRoleCQ();
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

    public BsFileConfigToRoleCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    public BsFileConfigToRoleCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setFileConfigId_Equal(String fileConfigId) {
        setFileConfigId_Term(fileConfigId, null);
    }

    public void setFileConfigId_Equal(String fileConfigId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setFileConfigId_Term(fileConfigId, opLambda);
    }

    public void setFileConfigId_Term(String fileConfigId) {
        setFileConfigId_Term(fileConfigId, null);
    }

    public void setFileConfigId_Term(String fileConfigId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("fileConfigId", fileConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_NotEqual(String fileConfigId) {
        setFileConfigId_NotTerm(fileConfigId, null);
    }

    public void setFileConfigId_NotTerm(String fileConfigId) {
        setFileConfigId_NotTerm(fileConfigId, null);
    }

    public void setFileConfigId_NotEqual(String fileConfigId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setFileConfigId_NotTerm(fileConfigId, opLambda);
    }

    public void setFileConfigId_NotTerm(String fileConfigId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setFileConfigId_Term(fileConfigId), opLambda);
    }

    public void setFileConfigId_Terms(Collection<String> fileConfigIdList) {
        setFileConfigId_Terms(fileConfigIdList, null);
    }

    public void setFileConfigId_Terms(Collection<String> fileConfigIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("fileConfigId", fileConfigIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_InScope(Collection<String> fileConfigIdList) {
        setFileConfigId_Terms(fileConfigIdList, null);
    }

    public void setFileConfigId_InScope(Collection<String> fileConfigIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setFileConfigId_Terms(fileConfigIdList, opLambda);
    }

    public void setFileConfigId_Match(String fileConfigId) {
        setFileConfigId_Match(fileConfigId, null);
    }

    public void setFileConfigId_Match(String fileConfigId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("fileConfigId", fileConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_MatchPhrase(String fileConfigId) {
        setFileConfigId_MatchPhrase(fileConfigId, null);
    }

    public void setFileConfigId_MatchPhrase(String fileConfigId, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("fileConfigId", fileConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_MatchPhrasePrefix(String fileConfigId) {
        setFileConfigId_MatchPhrasePrefix(fileConfigId, null);
    }

    public void setFileConfigId_MatchPhrasePrefix(String fileConfigId, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("fileConfigId", fileConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_Fuzzy(String fileConfigId) {
        setFileConfigId_Fuzzy(fileConfigId, null);
    }

    public void setFileConfigId_Fuzzy(String fileConfigId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("fileConfigId", fileConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_Prefix(String fileConfigId) {
        setFileConfigId_Prefix(fileConfigId, null);
    }

    public void setFileConfigId_Prefix(String fileConfigId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("fileConfigId", fileConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_Wildcard(String fileConfigId) {
        setFileConfigId_Wildcard(fileConfigId, null);
    }

    public void setFileConfigId_Wildcard(String fileConfigId, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("fileConfigId", fileConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_Regexp(String fileConfigId) {
        setFileConfigId_Regexp(fileConfigId, null);
    }

    public void setFileConfigId_Regexp(String fileConfigId, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("fileConfigId", fileConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_SpanTerm(String fileConfigId) {
        setFileConfigId_SpanTerm("fileConfigId", null);
    }

    public void setFileConfigId_SpanTerm(String fileConfigId, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("fileConfigId", fileConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_GreaterThan(String fileConfigId) {
        setFileConfigId_GreaterThan(fileConfigId, null);
    }

    public void setFileConfigId_GreaterThan(String fileConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = fileConfigId;
        RangeQueryBuilder builder = regRangeQ("fileConfigId", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_LessThan(String fileConfigId) {
        setFileConfigId_LessThan(fileConfigId, null);
    }

    public void setFileConfigId_LessThan(String fileConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = fileConfigId;
        RangeQueryBuilder builder = regRangeQ("fileConfigId", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_GreaterEqual(String fileConfigId) {
        setFileConfigId_GreaterEqual(fileConfigId, null);
    }

    public void setFileConfigId_GreaterEqual(String fileConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = fileConfigId;
        RangeQueryBuilder builder = regRangeQ("fileConfigId", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_LessEqual(String fileConfigId) {
        setFileConfigId_LessEqual(fileConfigId, null);
    }

    public void setFileConfigId_LessEqual(String fileConfigId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = fileConfigId;
        RangeQueryBuilder builder = regRangeQ("fileConfigId", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_Exists() {
        setFileConfigId_Exists(null);
    }

    public void setFileConfigId_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("fileConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_CommonTerms(String fileConfigId) {
        setFileConfigId_CommonTerms(fileConfigId, null);
    }

    public void setFileConfigId_CommonTerms(String fileConfigId, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("fileConfigId", fileConfigId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFileConfigToRoleCQ addOrderBy_FileConfigId_Asc() {
        regOBA("fileConfigId");
        return this;
    }

    public BsFileConfigToRoleCQ addOrderBy_FileConfigId_Desc() {
        regOBD("fileConfigId");
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

    public BsFileConfigToRoleCQ addOrderBy_RoleTypeId_Asc() {
        regOBA("roleTypeId");
        return this;
    }

    public BsFileConfigToRoleCQ addOrderBy_RoleTypeId_Desc() {
        regOBD("roleTypeId");
        return this;
    }

}
