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
package org.codelibs.fess.es.user.cbean.cq.bs;

import java.time.LocalDateTime;
import java.util.Collection;

import org.codelibs.fess.es.user.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.user.cbean.cf.UserCF;
import org.codelibs.fess.es.user.cbean.cq.UserCQ;
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
public abstract class BsUserCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "user";
    }

    @Override
    public String xgetAliasName() {
        return "user";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void filtered(FilteredCall<UserCQ, UserCF> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<UserCQ, UserCF> filteredLambda, ConditionOptionCall<FilteredQueryBuilder> opLambda) {
        UserCQ query = new UserCQ();
        UserCF filter = new UserCF();
        filteredLambda.callback(query, filter);
        if (query.hasQueries()) {
            FilteredQueryBuilder builder = regFilteredQ(query.getQuery(), filter.getFilter());
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void bool(BoolCall<UserCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<UserCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        UserCQ mustQuery = new UserCQ();
        UserCQ shouldQuery = new UserCQ();
        UserCQ mustNotQuery = new UserCQ();
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
    public void setGroups_Equal(String groups) {
        setGroups_Term(groups, null);
    }

    public void setGroups_Equal(String groups, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setGroups_Term(groups, opLambda);
    }

    public void setGroups_Term(String groups) {
        setGroups_Term(groups, null);
    }

    public void setGroups_Term(String groups, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("groups", groups);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroups_Terms(Collection<String> groupsList) {
        setGroups_Terms(groupsList, null);
    }

    public void setGroups_Terms(Collection<String> groupsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("groups", groupsList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroups_InScope(Collection<String> groupsList) {
        setGroups_Terms(groupsList, null);
    }

    public void setGroups_InScope(Collection<String> groupsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setGroups_Terms(groupsList, opLambda);
    }

    public void setGroups_Match(String groups) {
        setGroups_Match(groups, null);
    }

    public void setGroups_Match(String groups, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("groups", groups);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroups_MatchPhrase(String groups) {
        setGroups_MatchPhrase(groups, null);
    }

    public void setGroups_MatchPhrase(String groups, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("groups", groups);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroups_MatchPhrasePrefix(String groups) {
        setGroups_MatchPhrasePrefix(groups, null);
    }

    public void setGroups_MatchPhrasePrefix(String groups, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("groups", groups);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroups_Fuzzy(String groups) {
        setGroups_Fuzzy(groups, null);
    }

    public void setGroups_Fuzzy(String groups, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("groups", groups);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroups_Prefix(String groups) {
        setGroups_Prefix(groups, null);
    }

    public void setGroups_Prefix(String groups, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("groups", groups);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroups_GreaterThan(String groups) {
        setGroups_GreaterThan(groups, null);
    }

    public void setGroups_GreaterThan(String groups, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("groups", ConditionKey.CK_GREATER_THAN, groups);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroups_LessThan(String groups) {
        setGroups_LessThan(groups, null);
    }

    public void setGroups_LessThan(String groups, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("groups", ConditionKey.CK_LESS_THAN, groups);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroups_GreaterEqual(String groups) {
        setGroups_GreaterEqual(groups, null);
    }

    public void setGroups_GreaterEqual(String groups, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("groups", ConditionKey.CK_GREATER_EQUAL, groups);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroups_LessEqual(String groups) {
        setGroups_LessEqual(groups, null);
    }

    public void setGroups_LessEqual(String groups, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("groups", ConditionKey.CK_LESS_EQUAL, groups);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_Groups_Asc() {
        regOBA("groups");
        return this;
    }

    public BsUserCQ addOrderBy_Groups_Desc() {
        regOBD("groups");
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

    public BsUserCQ addOrderBy_Id_Asc() {
        regOBA("id");
        return this;
    }

    public BsUserCQ addOrderBy_Id_Desc() {
        regOBD("id");
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

    public BsUserCQ addOrderBy_Name_Asc() {
        regOBA("name");
        return this;
    }

    public BsUserCQ addOrderBy_Name_Desc() {
        regOBD("name");
        return this;
    }

    public void setPassword_Equal(String password) {
        setPassword_Term(password, null);
    }

    public void setPassword_Equal(String password, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setPassword_Term(password, opLambda);
    }

    public void setPassword_Term(String password) {
        setPassword_Term(password, null);
    }

    public void setPassword_Term(String password, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Terms(Collection<String> passwordList) {
        setPassword_Terms(passwordList, null);
    }

    public void setPassword_Terms(Collection<String> passwordList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("password", passwordList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_InScope(Collection<String> passwordList) {
        setPassword_Terms(passwordList, null);
    }

    public void setPassword_InScope(Collection<String> passwordList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setPassword_Terms(passwordList, opLambda);
    }

    public void setPassword_Match(String password) {
        setPassword_Match(password, null);
    }

    public void setPassword_Match(String password, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_MatchPhrase(String password) {
        setPassword_MatchPhrase(password, null);
    }

    public void setPassword_MatchPhrase(String password, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_MatchPhrasePrefix(String password) {
        setPassword_MatchPhrasePrefix(password, null);
    }

    public void setPassword_MatchPhrasePrefix(String password, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Fuzzy(String password) {
        setPassword_Fuzzy(password, null);
    }

    public void setPassword_Fuzzy(String password, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Prefix(String password) {
        setPassword_Prefix(password, null);
    }

    public void setPassword_Prefix(String password, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("password", password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_GreaterThan(String password) {
        setPassword_GreaterThan(password, null);
    }

    public void setPassword_GreaterThan(String password, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("password", ConditionKey.CK_GREATER_THAN, password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_LessThan(String password) {
        setPassword_LessThan(password, null);
    }

    public void setPassword_LessThan(String password, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("password", ConditionKey.CK_LESS_THAN, password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_GreaterEqual(String password) {
        setPassword_GreaterEqual(password, null);
    }

    public void setPassword_GreaterEqual(String password, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("password", ConditionKey.CK_GREATER_EQUAL, password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_LessEqual(String password) {
        setPassword_LessEqual(password, null);
    }

    public void setPassword_LessEqual(String password, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("password", ConditionKey.CK_LESS_EQUAL, password);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_Password_Asc() {
        regOBA("password");
        return this;
    }

    public BsUserCQ addOrderBy_Password_Desc() {
        regOBD("password");
        return this;
    }

    public void setRoles_Equal(String roles) {
        setRoles_Term(roles, null);
    }

    public void setRoles_Equal(String roles, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setRoles_Term(roles, opLambda);
    }

    public void setRoles_Term(String roles) {
        setRoles_Term(roles, null);
    }

    public void setRoles_Term(String roles, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_Terms(Collection<String> rolesList) {
        setRoles_Terms(rolesList, null);
    }

    public void setRoles_Terms(Collection<String> rolesList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("roles", rolesList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_InScope(Collection<String> rolesList) {
        setRoles_Terms(rolesList, null);
    }

    public void setRoles_InScope(Collection<String> rolesList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setRoles_Terms(rolesList, opLambda);
    }

    public void setRoles_Match(String roles) {
        setRoles_Match(roles, null);
    }

    public void setRoles_Match(String roles, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_MatchPhrase(String roles) {
        setRoles_MatchPhrase(roles, null);
    }

    public void setRoles_MatchPhrase(String roles, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_MatchPhrasePrefix(String roles) {
        setRoles_MatchPhrasePrefix(roles, null);
    }

    public void setRoles_MatchPhrasePrefix(String roles, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_Fuzzy(String roles) {
        setRoles_Fuzzy(roles, null);
    }

    public void setRoles_Fuzzy(String roles, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_Prefix(String roles) {
        setRoles_Prefix(roles, null);
    }

    public void setRoles_Prefix(String roles, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_GreaterThan(String roles) {
        setRoles_GreaterThan(roles, null);
    }

    public void setRoles_GreaterThan(String roles, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("roles", ConditionKey.CK_GREATER_THAN, roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_LessThan(String roles) {
        setRoles_LessThan(roles, null);
    }

    public void setRoles_LessThan(String roles, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("roles", ConditionKey.CK_LESS_THAN, roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_GreaterEqual(String roles) {
        setRoles_GreaterEqual(roles, null);
    }

    public void setRoles_GreaterEqual(String roles, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("roles", ConditionKey.CK_GREATER_EQUAL, roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_LessEqual(String roles) {
        setRoles_LessEqual(roles, null);
    }

    public void setRoles_LessEqual(String roles, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("roles", ConditionKey.CK_LESS_EQUAL, roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_Roles_Asc() {
        regOBA("roles");
        return this;
    }

    public BsUserCQ addOrderBy_Roles_Desc() {
        regOBD("roles");
        return this;
    }

}
