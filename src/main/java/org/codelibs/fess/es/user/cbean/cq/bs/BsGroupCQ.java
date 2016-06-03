/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
import java.util.Collection;

import org.codelibs.fess.es.user.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.user.cbean.cq.GroupCQ;
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

    public void not(OperatorCall<GroupCQ> notLambda, ConditionOptionCall<NotQueryBuilder> opLambda) {
        GroupCQ notQuery = new GroupCQ();
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

    public BsGroupCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    public BsGroupCQ addOrderBy_Id_Desc() {
        regOBD("_id");
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

    public BsGroupCQ addOrderBy_Name_Asc() {
        regOBA("name");
        return this;
    }

    public BsGroupCQ addOrderBy_Name_Desc() {
        regOBD("name");
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

    public void setGidNumber_NotEqual(Long gidNumber, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setGidNumber_NotTerm(gidNumber, opLambda);
    }

    public void setGidNumber_NotTerm(Long gidNumber) {
        setGidNumber_NotTerm(gidNumber, null);
    }

    public void setGidNumber_NotTerm(Long gidNumber, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("gidNumber", gidNumber));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
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

    public void setGidNumber_MatchPhrase(Long gidNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("gidNumber", gidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_MatchPhrasePrefix(Long gidNumber) {
        setGidNumber_MatchPhrasePrefix(gidNumber, null);
    }

    public void setGidNumber_MatchPhrasePrefix(Long gidNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("gidNumber", gidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Fuzzy(Long gidNumber) {
        setGidNumber_Fuzzy(gidNumber, null);
    }

    public void setGidNumber_Fuzzy(Long gidNumber, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("gidNumber", gidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_GreaterThan(Long gidNumber) {
        setGidNumber_GreaterThan(gidNumber, null);
    }

    public void setGidNumber_GreaterThan(Long gidNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("gidNumber", ConditionKey.CK_GREATER_THAN, gidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_LessThan(Long gidNumber) {
        setGidNumber_LessThan(gidNumber, null);
    }

    public void setGidNumber_LessThan(Long gidNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("gidNumber", ConditionKey.CK_LESS_THAN, gidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_GreaterEqual(Long gidNumber) {
        setGidNumber_GreaterEqual(gidNumber, null);
    }

    public void setGidNumber_GreaterEqual(Long gidNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("gidNumber", ConditionKey.CK_GREATER_EQUAL, gidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_LessEqual(Long gidNumber) {
        setGidNumber_LessEqual(gidNumber, null);
    }

    public void setGidNumber_LessEqual(Long gidNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("gidNumber", ConditionKey.CK_LESS_EQUAL, gidNumber);
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

}
