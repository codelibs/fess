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
package org.codelibs.fess.es.user.cbean.cq.bs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.codelibs.fess.es.user.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.user.cbean.cq.GroupCQ;
import org.dbflute.cbean.ckey.ConditionKey;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.CommonTermsQueryBuilder;
import org.opensearch.index.query.ExistsQueryBuilder;
import org.opensearch.index.query.IdsQueryBuilder;
import org.opensearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.opensearch.index.query.MatchPhraseQueryBuilder;
import org.opensearch.index.query.MatchQueryBuilder;
import org.opensearch.index.query.PrefixQueryBuilder;
import org.opensearch.index.query.RangeQueryBuilder;
import org.opensearch.index.query.RegexpQueryBuilder;
import org.opensearch.index.query.SpanTermQueryBuilder;
import org.opensearch.index.query.TermQueryBuilder;
import org.opensearch.index.query.TermsQueryBuilder;
import org.opensearch.index.query.WildcardQueryBuilder;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsGroupCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "group";
    }

    @Override
    public String xgetAliasName() {
        return "group";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<GroupCQ> queryLambda, ScoreFunctionCall<ScoreFunctionCreator<GroupCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        GroupCQ cq = new GroupCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                GroupCQ cf = new GroupCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<GroupCQ, GroupCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<GroupCQ, GroupCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<GroupCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<GroupCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<GroupCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<GroupCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        GroupCQ mustQuery = new GroupCQ();
        GroupCQ shouldQuery = new GroupCQ();
        GroupCQ mustNotQuery = new GroupCQ();
        GroupCQ filterQuery = new GroupCQ();
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
    public BsGroupCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsGroupCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setGidNumber_Equal(Long gidNumber) {
        setGidNumber_Term(gidNumber, null);
    }

    public void setGidNumber_Equal(Long gidNumber, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setGidNumber_Term(gidNumber, opLambda);
    }

    public void setGidNumber_Term(Long gidNumber) {
        setGidNumber_Term(gidNumber, null);
    }

    public void setGidNumber_Term(Long gidNumber, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("gidNumber", gidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_NotEqual(Long gidNumber) {
        setGidNumber_NotTerm(gidNumber, null);
    }

    public void setGidNumber_NotTerm(Long gidNumber) {
        setGidNumber_NotTerm(gidNumber, null);
    }

    public void setGidNumber_NotEqual(Long gidNumber, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setGidNumber_NotTerm(gidNumber, opLambda);
    }

    public void setGidNumber_NotTerm(Long gidNumber, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setGidNumber_Term(gidNumber), opLambda);
    }

    public void setGidNumber_Terms(Collection<Long> gidNumberList) {
        setGidNumber_Terms(gidNumberList, null);
    }

    public void setGidNumber_Terms(Collection<Long> gidNumberList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("gidNumber", gidNumberList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_InScope(Collection<Long> gidNumberList) {
        setGidNumber_Terms(gidNumberList, null);
    }

    public void setGidNumber_InScope(Collection<Long> gidNumberList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setGidNumber_Terms(gidNumberList, opLambda);
    }

    public void setGidNumber_Match(Long gidNumber) {
        setGidNumber_Match(gidNumber, null);
    }

    public void setGidNumber_Match(Long gidNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("gidNumber", gidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_MatchPhrase(Long gidNumber) {
        setGidNumber_MatchPhrase(gidNumber, null);
    }

    public void setGidNumber_MatchPhrase(Long gidNumber, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("gidNumber", gidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_MatchPhrasePrefix(Long gidNumber) {
        setGidNumber_MatchPhrasePrefix(gidNumber, null);
    }

    public void setGidNumber_MatchPhrasePrefix(Long gidNumber, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("gidNumber", gidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Fuzzy(Long gidNumber) {
        setGidNumber_Fuzzy(gidNumber, null);
    }

    public void setGidNumber_Fuzzy(Long gidNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("gidNumber", gidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_GreaterThan(Long gidNumber) {
        setGidNumber_GreaterThan(gidNumber, null);
    }

    public void setGidNumber_GreaterThan(Long gidNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = gidNumber;
        RangeQueryBuilder builder = regRangeQ("gidNumber", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_LessThan(Long gidNumber) {
        setGidNumber_LessThan(gidNumber, null);
    }

    public void setGidNumber_LessThan(Long gidNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = gidNumber;
        RangeQueryBuilder builder = regRangeQ("gidNumber", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_GreaterEqual(Long gidNumber) {
        setGidNumber_GreaterEqual(gidNumber, null);
    }

    public void setGidNumber_GreaterEqual(Long gidNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = gidNumber;
        RangeQueryBuilder builder = regRangeQ("gidNumber", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_LessEqual(Long gidNumber) {
        setGidNumber_LessEqual(gidNumber, null);
    }

    public void setGidNumber_LessEqual(Long gidNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = gidNumber;
        RangeQueryBuilder builder = regRangeQ("gidNumber", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Exists() {
        setGidNumber_Exists(null);
    }

    public void setGidNumber_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setGidNumber_CommonTerms(Long gidNumber) {
        setGidNumber_CommonTerms(gidNumber, null);
    }

    @Deprecated
    public void setGidNumber_CommonTerms(Long gidNumber, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("gidNumber", gidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsGroupCQ addOrderBy_GidNumber_Asc() {
        regOBA("gidNumber");
        return this;
    }

    public BsGroupCQ addOrderBy_GidNumber_Desc() {
        regOBD("gidNumber");
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

    public void setName_NotTerm(String name) {
        setName_NotTerm(name, null);
    }

    public void setName_NotEqual(String name, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setName_NotTerm(name, opLambda);
    }

    public void setName_NotTerm(String name, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setName_Term(name), opLambda);
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

    public void setName_MatchPhrase(String name, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_MatchPhrasePrefix(String name) {
        setName_MatchPhrasePrefix(name, null);
    }

    public void setName_MatchPhrasePrefix(String name, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Fuzzy(String name) {
        setName_Fuzzy(name, null);
    }

    public void setName_Fuzzy(String name, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("name", name);
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

    public void setName_Wildcard(String name) {
        setName_Wildcard(name, null);
    }

    public void setName_Wildcard(String name, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Regexp(String name) {
        setName_Regexp(name, null);
    }

    public void setName_Regexp(String name, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_SpanTerm(String name) {
        setName_SpanTerm("name", null);
    }

    public void setName_SpanTerm(String name, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_GreaterThan(String name) {
        setName_GreaterThan(name, null);
    }

    public void setName_GreaterThan(String name, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = name;
        RangeQueryBuilder builder = regRangeQ("name", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_LessThan(String name) {
        setName_LessThan(name, null);
    }

    public void setName_LessThan(String name, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = name;
        RangeQueryBuilder builder = regRangeQ("name", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_GreaterEqual(String name) {
        setName_GreaterEqual(name, null);
    }

    public void setName_GreaterEqual(String name, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = name;
        RangeQueryBuilder builder = regRangeQ("name", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_LessEqual(String name) {
        setName_LessEqual(name, null);
    }

    public void setName_LessEqual(String name, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = name;
        RangeQueryBuilder builder = regRangeQ("name", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Exists() {
        setName_Exists(null);
    }

    public void setName_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setName_CommonTerms(String name) {
        setName_CommonTerms(name, null);
    }

    @Deprecated
    public void setName_CommonTerms(String name, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("name", name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsGroupCQ addOrderBy_Name_Asc() {
        regOBA("name");
        return this;
    }

    public BsGroupCQ addOrderBy_Name_Desc() {
        regOBD("name");
        return this;
    }

}
