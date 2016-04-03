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
import org.codelibs.fess.es.user.cbean.cq.UserCQ;
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
    public void filtered(FilteredCall<UserCQ, UserCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<UserCQ, UserCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<UserCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<UserCQ> notLambda, ConditionOptionCall<NotQueryBuilder> opLambda) {
        UserCQ notQuery = new UserCQ();
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

    public void bool(BoolCall<UserCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<UserCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        UserCQ mustQuery = new UserCQ();
        UserCQ shouldQuery = new UserCQ();
        UserCQ mustNotQuery = new UserCQ();
        UserCQ filterQuery = new UserCQ();
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

    public BsUserCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    public BsUserCQ addOrderBy_Id_Desc() {
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

    public void setPassword_NotEqual(String password) {
        setPassword_NotTerm(password, null);
    }

    public void setPassword_NotEqual(String password, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setPassword_NotTerm(password, opLambda);
    }

    public void setPassword_NotTerm(String password) {
        setPassword_NotTerm(password, null);
    }

    public void setPassword_NotTerm(String password, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("password", password));
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

    public void setSurname_Equal(String surname) {
        setSurname_Term(surname, null);
    }

    public void setSurname_Equal(String surname, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setSurname_Term(surname, opLambda);
    }

    public void setSurname_Term(String surname) {
        setSurname_Term(surname, null);
    }

    public void setSurname_Term(String surname, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("surname", surname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSurname_NotEqual(String surname) {
        setSurname_NotTerm(surname, null);
    }

    public void setSurname_NotEqual(String surname, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setSurname_NotTerm(surname, opLambda);
    }

    public void setSurname_NotTerm(String surname) {
        setSurname_NotTerm(surname, null);
    }

    public void setSurname_NotTerm(String surname, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("surname", surname));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSurname_Terms(Collection<String> surnameList) {
        setSurname_Terms(surnameList, null);
    }

    public void setSurname_Terms(Collection<String> surnameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("surname", surnameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSurname_InScope(Collection<String> surnameList) {
        setSurname_Terms(surnameList, null);
    }

    public void setSurname_InScope(Collection<String> surnameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setSurname_Terms(surnameList, opLambda);
    }

    public void setSurname_Match(String surname) {
        setSurname_Match(surname, null);
    }

    public void setSurname_Match(String surname, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("surname", surname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSurname_MatchPhrase(String surname) {
        setSurname_MatchPhrase(surname, null);
    }

    public void setSurname_MatchPhrase(String surname, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("surname", surname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSurname_MatchPhrasePrefix(String surname) {
        setSurname_MatchPhrasePrefix(surname, null);
    }

    public void setSurname_MatchPhrasePrefix(String surname, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("surname", surname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSurname_Fuzzy(String surname) {
        setSurname_Fuzzy(surname, null);
    }

    public void setSurname_Fuzzy(String surname, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("surname", surname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSurname_Prefix(String surname) {
        setSurname_Prefix(surname, null);
    }

    public void setSurname_Prefix(String surname, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("surname", surname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSurname_GreaterThan(String surname) {
        setSurname_GreaterThan(surname, null);
    }

    public void setSurname_GreaterThan(String surname, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("surname", ConditionKey.CK_GREATER_THAN, surname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSurname_LessThan(String surname) {
        setSurname_LessThan(surname, null);
    }

    public void setSurname_LessThan(String surname, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("surname", ConditionKey.CK_LESS_THAN, surname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSurname_GreaterEqual(String surname) {
        setSurname_GreaterEqual(surname, null);
    }

    public void setSurname_GreaterEqual(String surname, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("surname", ConditionKey.CK_GREATER_EQUAL, surname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSurname_LessEqual(String surname) {
        setSurname_LessEqual(surname, null);
    }

    public void setSurname_LessEqual(String surname, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("surname", ConditionKey.CK_LESS_EQUAL, surname);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_Surname_Asc() {
        regOBA("surname");
        return this;
    }

    public BsUserCQ addOrderBy_Surname_Desc() {
        regOBD("surname");
        return this;
    }

    public void setGivenName_Equal(String givenName) {
        setGivenName_Term(givenName, null);
    }

    public void setGivenName_Equal(String givenName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setGivenName_Term(givenName, opLambda);
    }

    public void setGivenName_Term(String givenName) {
        setGivenName_Term(givenName, null);
    }

    public void setGivenName_Term(String givenName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("givenName", givenName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGivenName_NotEqual(String givenName) {
        setGivenName_NotTerm(givenName, null);
    }

    public void setGivenName_NotEqual(String givenName, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setGivenName_NotTerm(givenName, opLambda);
    }

    public void setGivenName_NotTerm(String givenName) {
        setGivenName_NotTerm(givenName, null);
    }

    public void setGivenName_NotTerm(String givenName, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("givenName", givenName));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGivenName_Terms(Collection<String> givenNameList) {
        setGivenName_Terms(givenNameList, null);
    }

    public void setGivenName_Terms(Collection<String> givenNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("givenName", givenNameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGivenName_InScope(Collection<String> givenNameList) {
        setGivenName_Terms(givenNameList, null);
    }

    public void setGivenName_InScope(Collection<String> givenNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setGivenName_Terms(givenNameList, opLambda);
    }

    public void setGivenName_Match(String givenName) {
        setGivenName_Match(givenName, null);
    }

    public void setGivenName_Match(String givenName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("givenName", givenName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGivenName_MatchPhrase(String givenName) {
        setGivenName_MatchPhrase(givenName, null);
    }

    public void setGivenName_MatchPhrase(String givenName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("givenName", givenName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGivenName_MatchPhrasePrefix(String givenName) {
        setGivenName_MatchPhrasePrefix(givenName, null);
    }

    public void setGivenName_MatchPhrasePrefix(String givenName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("givenName", givenName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGivenName_Fuzzy(String givenName) {
        setGivenName_Fuzzy(givenName, null);
    }

    public void setGivenName_Fuzzy(String givenName, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("givenName", givenName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGivenName_Prefix(String givenName) {
        setGivenName_Prefix(givenName, null);
    }

    public void setGivenName_Prefix(String givenName, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("givenName", givenName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGivenName_GreaterThan(String givenName) {
        setGivenName_GreaterThan(givenName, null);
    }

    public void setGivenName_GreaterThan(String givenName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("givenName", ConditionKey.CK_GREATER_THAN, givenName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGivenName_LessThan(String givenName) {
        setGivenName_LessThan(givenName, null);
    }

    public void setGivenName_LessThan(String givenName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("givenName", ConditionKey.CK_LESS_THAN, givenName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGivenName_GreaterEqual(String givenName) {
        setGivenName_GreaterEqual(givenName, null);
    }

    public void setGivenName_GreaterEqual(String givenName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("givenName", ConditionKey.CK_GREATER_EQUAL, givenName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGivenName_LessEqual(String givenName) {
        setGivenName_LessEqual(givenName, null);
    }

    public void setGivenName_LessEqual(String givenName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("givenName", ConditionKey.CK_LESS_EQUAL, givenName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_GivenName_Asc() {
        regOBA("givenName");
        return this;
    }

    public BsUserCQ addOrderBy_GivenName_Desc() {
        regOBD("givenName");
        return this;
    }

    public void setEmployeeNumber_Equal(String employeeNumber) {
        setEmployeeNumber_Term(employeeNumber, null);
    }

    public void setEmployeeNumber_Equal(String employeeNumber, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setEmployeeNumber_Term(employeeNumber, opLambda);
    }

    public void setEmployeeNumber_Term(String employeeNumber) {
        setEmployeeNumber_Term(employeeNumber, null);
    }

    public void setEmployeeNumber_Term(String employeeNumber, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("employeeNumber", employeeNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeNumber_NotEqual(String employeeNumber) {
        setEmployeeNumber_NotTerm(employeeNumber, null);
    }

    public void setEmployeeNumber_NotEqual(String employeeNumber, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setEmployeeNumber_NotTerm(employeeNumber, opLambda);
    }

    public void setEmployeeNumber_NotTerm(String employeeNumber) {
        setEmployeeNumber_NotTerm(employeeNumber, null);
    }

    public void setEmployeeNumber_NotTerm(String employeeNumber, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("employeeNumber", employeeNumber));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeNumber_Terms(Collection<String> employeeNumberList) {
        setEmployeeNumber_Terms(employeeNumberList, null);
    }

    public void setEmployeeNumber_Terms(Collection<String> employeeNumberList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("employeeNumber", employeeNumberList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeNumber_InScope(Collection<String> employeeNumberList) {
        setEmployeeNumber_Terms(employeeNumberList, null);
    }

    public void setEmployeeNumber_InScope(Collection<String> employeeNumberList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setEmployeeNumber_Terms(employeeNumberList, opLambda);
    }

    public void setEmployeeNumber_Match(String employeeNumber) {
        setEmployeeNumber_Match(employeeNumber, null);
    }

    public void setEmployeeNumber_Match(String employeeNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("employeeNumber", employeeNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeNumber_MatchPhrase(String employeeNumber) {
        setEmployeeNumber_MatchPhrase(employeeNumber, null);
    }

    public void setEmployeeNumber_MatchPhrase(String employeeNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("employeeNumber", employeeNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeNumber_MatchPhrasePrefix(String employeeNumber) {
        setEmployeeNumber_MatchPhrasePrefix(employeeNumber, null);
    }

    public void setEmployeeNumber_MatchPhrasePrefix(String employeeNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("employeeNumber", employeeNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeNumber_Fuzzy(String employeeNumber) {
        setEmployeeNumber_Fuzzy(employeeNumber, null);
    }

    public void setEmployeeNumber_Fuzzy(String employeeNumber, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("employeeNumber", employeeNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeNumber_Prefix(String employeeNumber) {
        setEmployeeNumber_Prefix(employeeNumber, null);
    }

    public void setEmployeeNumber_Prefix(String employeeNumber, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("employeeNumber", employeeNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeNumber_GreaterThan(String employeeNumber) {
        setEmployeeNumber_GreaterThan(employeeNumber, null);
    }

    public void setEmployeeNumber_GreaterThan(String employeeNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("employeeNumber", ConditionKey.CK_GREATER_THAN, employeeNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeNumber_LessThan(String employeeNumber) {
        setEmployeeNumber_LessThan(employeeNumber, null);
    }

    public void setEmployeeNumber_LessThan(String employeeNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("employeeNumber", ConditionKey.CK_LESS_THAN, employeeNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeNumber_GreaterEqual(String employeeNumber) {
        setEmployeeNumber_GreaterEqual(employeeNumber, null);
    }

    public void setEmployeeNumber_GreaterEqual(String employeeNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("employeeNumber", ConditionKey.CK_GREATER_EQUAL, employeeNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeNumber_LessEqual(String employeeNumber) {
        setEmployeeNumber_LessEqual(employeeNumber, null);
    }

    public void setEmployeeNumber_LessEqual(String employeeNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("employeeNumber", ConditionKey.CK_LESS_EQUAL, employeeNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_EmployeeNumber_Asc() {
        regOBA("employeeNumber");
        return this;
    }

    public BsUserCQ addOrderBy_EmployeeNumber_Desc() {
        regOBD("employeeNumber");
        return this;
    }

    public void setMail_Equal(String mail) {
        setMail_Term(mail, null);
    }

    public void setMail_Equal(String mail, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setMail_Term(mail, opLambda);
    }

    public void setMail_Term(String mail) {
        setMail_Term(mail, null);
    }

    public void setMail_Term(String mail, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("mail", mail);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMail_NotEqual(String mail) {
        setMail_NotTerm(mail, null);
    }

    public void setMail_NotEqual(String mail, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setMail_NotTerm(mail, opLambda);
    }

    public void setMail_NotTerm(String mail) {
        setMail_NotTerm(mail, null);
    }

    public void setMail_NotTerm(String mail, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("mail", mail));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMail_Terms(Collection<String> mailList) {
        setMail_Terms(mailList, null);
    }

    public void setMail_Terms(Collection<String> mailList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("mail", mailList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMail_InScope(Collection<String> mailList) {
        setMail_Terms(mailList, null);
    }

    public void setMail_InScope(Collection<String> mailList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setMail_Terms(mailList, opLambda);
    }

    public void setMail_Match(String mail) {
        setMail_Match(mail, null);
    }

    public void setMail_Match(String mail, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("mail", mail);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMail_MatchPhrase(String mail) {
        setMail_MatchPhrase(mail, null);
    }

    public void setMail_MatchPhrase(String mail, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("mail", mail);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMail_MatchPhrasePrefix(String mail) {
        setMail_MatchPhrasePrefix(mail, null);
    }

    public void setMail_MatchPhrasePrefix(String mail, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("mail", mail);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMail_Fuzzy(String mail) {
        setMail_Fuzzy(mail, null);
    }

    public void setMail_Fuzzy(String mail, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("mail", mail);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMail_Prefix(String mail) {
        setMail_Prefix(mail, null);
    }

    public void setMail_Prefix(String mail, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("mail", mail);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMail_GreaterThan(String mail) {
        setMail_GreaterThan(mail, null);
    }

    public void setMail_GreaterThan(String mail, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("mail", ConditionKey.CK_GREATER_THAN, mail);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMail_LessThan(String mail) {
        setMail_LessThan(mail, null);
    }

    public void setMail_LessThan(String mail, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("mail", ConditionKey.CK_LESS_THAN, mail);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMail_GreaterEqual(String mail) {
        setMail_GreaterEqual(mail, null);
    }

    public void setMail_GreaterEqual(String mail, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("mail", ConditionKey.CK_GREATER_EQUAL, mail);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMail_LessEqual(String mail) {
        setMail_LessEqual(mail, null);
    }

    public void setMail_LessEqual(String mail, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("mail", ConditionKey.CK_LESS_EQUAL, mail);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_Mail_Asc() {
        regOBA("mail");
        return this;
    }

    public BsUserCQ addOrderBy_Mail_Desc() {
        regOBD("mail");
        return this;
    }

    public void setTelephoneNumber_Equal(String telephoneNumber) {
        setTelephoneNumber_Term(telephoneNumber, null);
    }

    public void setTelephoneNumber_Equal(String telephoneNumber, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setTelephoneNumber_Term(telephoneNumber, opLambda);
    }

    public void setTelephoneNumber_Term(String telephoneNumber) {
        setTelephoneNumber_Term(telephoneNumber, null);
    }

    public void setTelephoneNumber_Term(String telephoneNumber, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("telephoneNumber", telephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTelephoneNumber_NotEqual(String telephoneNumber) {
        setTelephoneNumber_NotTerm(telephoneNumber, null);
    }

    public void setTelephoneNumber_NotEqual(String telephoneNumber, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setTelephoneNumber_NotTerm(telephoneNumber, opLambda);
    }

    public void setTelephoneNumber_NotTerm(String telephoneNumber) {
        setTelephoneNumber_NotTerm(telephoneNumber, null);
    }

    public void setTelephoneNumber_NotTerm(String telephoneNumber, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("telephoneNumber", telephoneNumber));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTelephoneNumber_Terms(Collection<String> telephoneNumberList) {
        setTelephoneNumber_Terms(telephoneNumberList, null);
    }

    public void setTelephoneNumber_Terms(Collection<String> telephoneNumberList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("telephoneNumber", telephoneNumberList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTelephoneNumber_InScope(Collection<String> telephoneNumberList) {
        setTelephoneNumber_Terms(telephoneNumberList, null);
    }

    public void setTelephoneNumber_InScope(Collection<String> telephoneNumberList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setTelephoneNumber_Terms(telephoneNumberList, opLambda);
    }

    public void setTelephoneNumber_Match(String telephoneNumber) {
        setTelephoneNumber_Match(telephoneNumber, null);
    }

    public void setTelephoneNumber_Match(String telephoneNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("telephoneNumber", telephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTelephoneNumber_MatchPhrase(String telephoneNumber) {
        setTelephoneNumber_MatchPhrase(telephoneNumber, null);
    }

    public void setTelephoneNumber_MatchPhrase(String telephoneNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("telephoneNumber", telephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTelephoneNumber_MatchPhrasePrefix(String telephoneNumber) {
        setTelephoneNumber_MatchPhrasePrefix(telephoneNumber, null);
    }

    public void setTelephoneNumber_MatchPhrasePrefix(String telephoneNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("telephoneNumber", telephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTelephoneNumber_Fuzzy(String telephoneNumber) {
        setTelephoneNumber_Fuzzy(telephoneNumber, null);
    }

    public void setTelephoneNumber_Fuzzy(String telephoneNumber, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("telephoneNumber", telephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTelephoneNumber_Prefix(String telephoneNumber) {
        setTelephoneNumber_Prefix(telephoneNumber, null);
    }

    public void setTelephoneNumber_Prefix(String telephoneNumber, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("telephoneNumber", telephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTelephoneNumber_GreaterThan(String telephoneNumber) {
        setTelephoneNumber_GreaterThan(telephoneNumber, null);
    }

    public void setTelephoneNumber_GreaterThan(String telephoneNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("telephoneNumber", ConditionKey.CK_GREATER_THAN, telephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTelephoneNumber_LessThan(String telephoneNumber) {
        setTelephoneNumber_LessThan(telephoneNumber, null);
    }

    public void setTelephoneNumber_LessThan(String telephoneNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("telephoneNumber", ConditionKey.CK_LESS_THAN, telephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTelephoneNumber_GreaterEqual(String telephoneNumber) {
        setTelephoneNumber_GreaterEqual(telephoneNumber, null);
    }

    public void setTelephoneNumber_GreaterEqual(String telephoneNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("telephoneNumber", ConditionKey.CK_GREATER_EQUAL, telephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTelephoneNumber_LessEqual(String telephoneNumber) {
        setTelephoneNumber_LessEqual(telephoneNumber, null);
    }

    public void setTelephoneNumber_LessEqual(String telephoneNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("telephoneNumber", ConditionKey.CK_LESS_EQUAL, telephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_TelephoneNumber_Asc() {
        regOBA("telephoneNumber");
        return this;
    }

    public BsUserCQ addOrderBy_TelephoneNumber_Desc() {
        regOBD("telephoneNumber");
        return this;
    }

    public void setHomePhone_Equal(String homePhone) {
        setHomePhone_Term(homePhone, null);
    }

    public void setHomePhone_Equal(String homePhone, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setHomePhone_Term(homePhone, opLambda);
    }

    public void setHomePhone_Term(String homePhone) {
        setHomePhone_Term(homePhone, null);
    }

    public void setHomePhone_Term(String homePhone, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("homePhone", homePhone);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePhone_NotEqual(String homePhone) {
        setHomePhone_NotTerm(homePhone, null);
    }

    public void setHomePhone_NotEqual(String homePhone, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setHomePhone_NotTerm(homePhone, opLambda);
    }

    public void setHomePhone_NotTerm(String homePhone) {
        setHomePhone_NotTerm(homePhone, null);
    }

    public void setHomePhone_NotTerm(String homePhone, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("homePhone", homePhone));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePhone_Terms(Collection<String> homePhoneList) {
        setHomePhone_Terms(homePhoneList, null);
    }

    public void setHomePhone_Terms(Collection<String> homePhoneList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("homePhone", homePhoneList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePhone_InScope(Collection<String> homePhoneList) {
        setHomePhone_Terms(homePhoneList, null);
    }

    public void setHomePhone_InScope(Collection<String> homePhoneList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setHomePhone_Terms(homePhoneList, opLambda);
    }

    public void setHomePhone_Match(String homePhone) {
        setHomePhone_Match(homePhone, null);
    }

    public void setHomePhone_Match(String homePhone, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("homePhone", homePhone);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePhone_MatchPhrase(String homePhone) {
        setHomePhone_MatchPhrase(homePhone, null);
    }

    public void setHomePhone_MatchPhrase(String homePhone, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("homePhone", homePhone);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePhone_MatchPhrasePrefix(String homePhone) {
        setHomePhone_MatchPhrasePrefix(homePhone, null);
    }

    public void setHomePhone_MatchPhrasePrefix(String homePhone, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("homePhone", homePhone);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePhone_Fuzzy(String homePhone) {
        setHomePhone_Fuzzy(homePhone, null);
    }

    public void setHomePhone_Fuzzy(String homePhone, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("homePhone", homePhone);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePhone_Prefix(String homePhone) {
        setHomePhone_Prefix(homePhone, null);
    }

    public void setHomePhone_Prefix(String homePhone, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("homePhone", homePhone);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePhone_GreaterThan(String homePhone) {
        setHomePhone_GreaterThan(homePhone, null);
    }

    public void setHomePhone_GreaterThan(String homePhone, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("homePhone", ConditionKey.CK_GREATER_THAN, homePhone);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePhone_LessThan(String homePhone) {
        setHomePhone_LessThan(homePhone, null);
    }

    public void setHomePhone_LessThan(String homePhone, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("homePhone", ConditionKey.CK_LESS_THAN, homePhone);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePhone_GreaterEqual(String homePhone) {
        setHomePhone_GreaterEqual(homePhone, null);
    }

    public void setHomePhone_GreaterEqual(String homePhone, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("homePhone", ConditionKey.CK_GREATER_EQUAL, homePhone);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePhone_LessEqual(String homePhone) {
        setHomePhone_LessEqual(homePhone, null);
    }

    public void setHomePhone_LessEqual(String homePhone, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("homePhone", ConditionKey.CK_LESS_EQUAL, homePhone);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_HomePhone_Asc() {
        regOBA("homePhone");
        return this;
    }

    public BsUserCQ addOrderBy_HomePhone_Desc() {
        regOBD("homePhone");
        return this;
    }

    public void setHomePostalAddress_Equal(String homePostalAddress) {
        setHomePostalAddress_Term(homePostalAddress, null);
    }

    public void setHomePostalAddress_Equal(String homePostalAddress, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setHomePostalAddress_Term(homePostalAddress, opLambda);
    }

    public void setHomePostalAddress_Term(String homePostalAddress) {
        setHomePostalAddress_Term(homePostalAddress, null);
    }

    public void setHomePostalAddress_Term(String homePostalAddress, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("homePostalAddress", homePostalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePostalAddress_NotEqual(String homePostalAddress) {
        setHomePostalAddress_NotTerm(homePostalAddress, null);
    }

    public void setHomePostalAddress_NotEqual(String homePostalAddress, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setHomePostalAddress_NotTerm(homePostalAddress, opLambda);
    }

    public void setHomePostalAddress_NotTerm(String homePostalAddress) {
        setHomePostalAddress_NotTerm(homePostalAddress, null);
    }

    public void setHomePostalAddress_NotTerm(String homePostalAddress, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("homePostalAddress", homePostalAddress));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePostalAddress_Terms(Collection<String> homePostalAddressList) {
        setHomePostalAddress_Terms(homePostalAddressList, null);
    }

    public void setHomePostalAddress_Terms(Collection<String> homePostalAddressList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("homePostalAddress", homePostalAddressList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePostalAddress_InScope(Collection<String> homePostalAddressList) {
        setHomePostalAddress_Terms(homePostalAddressList, null);
    }

    public void setHomePostalAddress_InScope(Collection<String> homePostalAddressList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setHomePostalAddress_Terms(homePostalAddressList, opLambda);
    }

    public void setHomePostalAddress_Match(String homePostalAddress) {
        setHomePostalAddress_Match(homePostalAddress, null);
    }

    public void setHomePostalAddress_Match(String homePostalAddress, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("homePostalAddress", homePostalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePostalAddress_MatchPhrase(String homePostalAddress) {
        setHomePostalAddress_MatchPhrase(homePostalAddress, null);
    }

    public void setHomePostalAddress_MatchPhrase(String homePostalAddress, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("homePostalAddress", homePostalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePostalAddress_MatchPhrasePrefix(String homePostalAddress) {
        setHomePostalAddress_MatchPhrasePrefix(homePostalAddress, null);
    }

    public void setHomePostalAddress_MatchPhrasePrefix(String homePostalAddress, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("homePostalAddress", homePostalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePostalAddress_Fuzzy(String homePostalAddress) {
        setHomePostalAddress_Fuzzy(homePostalAddress, null);
    }

    public void setHomePostalAddress_Fuzzy(String homePostalAddress, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("homePostalAddress", homePostalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePostalAddress_Prefix(String homePostalAddress) {
        setHomePostalAddress_Prefix(homePostalAddress, null);
    }

    public void setHomePostalAddress_Prefix(String homePostalAddress, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("homePostalAddress", homePostalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePostalAddress_GreaterThan(String homePostalAddress) {
        setHomePostalAddress_GreaterThan(homePostalAddress, null);
    }

    public void setHomePostalAddress_GreaterThan(String homePostalAddress, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("homePostalAddress", ConditionKey.CK_GREATER_THAN, homePostalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePostalAddress_LessThan(String homePostalAddress) {
        setHomePostalAddress_LessThan(homePostalAddress, null);
    }

    public void setHomePostalAddress_LessThan(String homePostalAddress, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("homePostalAddress", ConditionKey.CK_LESS_THAN, homePostalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePostalAddress_GreaterEqual(String homePostalAddress) {
        setHomePostalAddress_GreaterEqual(homePostalAddress, null);
    }

    public void setHomePostalAddress_GreaterEqual(String homePostalAddress, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("homePostalAddress", ConditionKey.CK_GREATER_EQUAL, homePostalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePostalAddress_LessEqual(String homePostalAddress) {
        setHomePostalAddress_LessEqual(homePostalAddress, null);
    }

    public void setHomePostalAddress_LessEqual(String homePostalAddress, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("homePostalAddress", ConditionKey.CK_LESS_EQUAL, homePostalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_HomePostalAddress_Asc() {
        regOBA("homePostalAddress");
        return this;
    }

    public BsUserCQ addOrderBy_HomePostalAddress_Desc() {
        regOBD("homePostalAddress");
        return this;
    }

    public void setLabeledURI_Equal(String labeledURI) {
        setLabeledURI_Term(labeledURI, null);
    }

    public void setLabeledURI_Equal(String labeledURI, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setLabeledURI_Term(labeledURI, opLambda);
    }

    public void setLabeledURI_Term(String labeledURI) {
        setLabeledURI_Term(labeledURI, null);
    }

    public void setLabeledURI_Term(String labeledURI, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("labeledURI", labeledURI);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabeledURI_NotEqual(String labeledURI) {
        setLabeledURI_NotTerm(labeledURI, null);
    }

    public void setLabeledURI_NotEqual(String labeledURI, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setLabeledURI_NotTerm(labeledURI, opLambda);
    }

    public void setLabeledURI_NotTerm(String labeledURI) {
        setLabeledURI_NotTerm(labeledURI, null);
    }

    public void setLabeledURI_NotTerm(String labeledURI, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("labeledURI", labeledURI));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabeledURI_Terms(Collection<String> labeledURIList) {
        setLabeledURI_Terms(labeledURIList, null);
    }

    public void setLabeledURI_Terms(Collection<String> labeledURIList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("labeledURI", labeledURIList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabeledURI_InScope(Collection<String> labeledURIList) {
        setLabeledURI_Terms(labeledURIList, null);
    }

    public void setLabeledURI_InScope(Collection<String> labeledURIList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setLabeledURI_Terms(labeledURIList, opLambda);
    }

    public void setLabeledURI_Match(String labeledURI) {
        setLabeledURI_Match(labeledURI, null);
    }

    public void setLabeledURI_Match(String labeledURI, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("labeledURI", labeledURI);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabeledURI_MatchPhrase(String labeledURI) {
        setLabeledURI_MatchPhrase(labeledURI, null);
    }

    public void setLabeledURI_MatchPhrase(String labeledURI, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("labeledURI", labeledURI);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabeledURI_MatchPhrasePrefix(String labeledURI) {
        setLabeledURI_MatchPhrasePrefix(labeledURI, null);
    }

    public void setLabeledURI_MatchPhrasePrefix(String labeledURI, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("labeledURI", labeledURI);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabeledURI_Fuzzy(String labeledURI) {
        setLabeledURI_Fuzzy(labeledURI, null);
    }

    public void setLabeledURI_Fuzzy(String labeledURI, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("labeledURI", labeledURI);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabeledURI_Prefix(String labeledURI) {
        setLabeledURI_Prefix(labeledURI, null);
    }

    public void setLabeledURI_Prefix(String labeledURI, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("labeledURI", labeledURI);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabeledURI_GreaterThan(String labeledURI) {
        setLabeledURI_GreaterThan(labeledURI, null);
    }

    public void setLabeledURI_GreaterThan(String labeledURI, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("labeledURI", ConditionKey.CK_GREATER_THAN, labeledURI);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabeledURI_LessThan(String labeledURI) {
        setLabeledURI_LessThan(labeledURI, null);
    }

    public void setLabeledURI_LessThan(String labeledURI, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("labeledURI", ConditionKey.CK_LESS_THAN, labeledURI);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabeledURI_GreaterEqual(String labeledURI) {
        setLabeledURI_GreaterEqual(labeledURI, null);
    }

    public void setLabeledURI_GreaterEqual(String labeledURI, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("labeledURI", ConditionKey.CK_GREATER_EQUAL, labeledURI);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabeledURI_LessEqual(String labeledURI) {
        setLabeledURI_LessEqual(labeledURI, null);
    }

    public void setLabeledURI_LessEqual(String labeledURI, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("labeledURI", ConditionKey.CK_LESS_EQUAL, labeledURI);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_LabeledURI_Asc() {
        regOBA("labeledURI");
        return this;
    }

    public BsUserCQ addOrderBy_LabeledURI_Desc() {
        regOBD("labeledURI");
        return this;
    }

    public void setRoomNumber_Equal(String roomNumber) {
        setRoomNumber_Term(roomNumber, null);
    }

    public void setRoomNumber_Equal(String roomNumber, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setRoomNumber_Term(roomNumber, opLambda);
    }

    public void setRoomNumber_Term(String roomNumber) {
        setRoomNumber_Term(roomNumber, null);
    }

    public void setRoomNumber_Term(String roomNumber, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("roomNumber", roomNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoomNumber_NotEqual(String roomNumber) {
        setRoomNumber_NotTerm(roomNumber, null);
    }

    public void setRoomNumber_NotEqual(String roomNumber, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setRoomNumber_NotTerm(roomNumber, opLambda);
    }

    public void setRoomNumber_NotTerm(String roomNumber) {
        setRoomNumber_NotTerm(roomNumber, null);
    }

    public void setRoomNumber_NotTerm(String roomNumber, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("roomNumber", roomNumber));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoomNumber_Terms(Collection<String> roomNumberList) {
        setRoomNumber_Terms(roomNumberList, null);
    }

    public void setRoomNumber_Terms(Collection<String> roomNumberList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("roomNumber", roomNumberList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoomNumber_InScope(Collection<String> roomNumberList) {
        setRoomNumber_Terms(roomNumberList, null);
    }

    public void setRoomNumber_InScope(Collection<String> roomNumberList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setRoomNumber_Terms(roomNumberList, opLambda);
    }

    public void setRoomNumber_Match(String roomNumber) {
        setRoomNumber_Match(roomNumber, null);
    }

    public void setRoomNumber_Match(String roomNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("roomNumber", roomNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoomNumber_MatchPhrase(String roomNumber) {
        setRoomNumber_MatchPhrase(roomNumber, null);
    }

    public void setRoomNumber_MatchPhrase(String roomNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("roomNumber", roomNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoomNumber_MatchPhrasePrefix(String roomNumber) {
        setRoomNumber_MatchPhrasePrefix(roomNumber, null);
    }

    public void setRoomNumber_MatchPhrasePrefix(String roomNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("roomNumber", roomNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoomNumber_Fuzzy(String roomNumber) {
        setRoomNumber_Fuzzy(roomNumber, null);
    }

    public void setRoomNumber_Fuzzy(String roomNumber, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("roomNumber", roomNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoomNumber_Prefix(String roomNumber) {
        setRoomNumber_Prefix(roomNumber, null);
    }

    public void setRoomNumber_Prefix(String roomNumber, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("roomNumber", roomNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoomNumber_GreaterThan(String roomNumber) {
        setRoomNumber_GreaterThan(roomNumber, null);
    }

    public void setRoomNumber_GreaterThan(String roomNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("roomNumber", ConditionKey.CK_GREATER_THAN, roomNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoomNumber_LessThan(String roomNumber) {
        setRoomNumber_LessThan(roomNumber, null);
    }

    public void setRoomNumber_LessThan(String roomNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("roomNumber", ConditionKey.CK_LESS_THAN, roomNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoomNumber_GreaterEqual(String roomNumber) {
        setRoomNumber_GreaterEqual(roomNumber, null);
    }

    public void setRoomNumber_GreaterEqual(String roomNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("roomNumber", ConditionKey.CK_GREATER_EQUAL, roomNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoomNumber_LessEqual(String roomNumber) {
        setRoomNumber_LessEqual(roomNumber, null);
    }

    public void setRoomNumber_LessEqual(String roomNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("roomNumber", ConditionKey.CK_LESS_EQUAL, roomNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_RoomNumber_Asc() {
        regOBA("roomNumber");
        return this;
    }

    public BsUserCQ addOrderBy_RoomNumber_Desc() {
        regOBD("roomNumber");
        return this;
    }

    public void setDescription_Equal(String description) {
        setDescription_Term(description, null);
    }

    public void setDescription_Equal(String description, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setDescription_Term(description, opLambda);
    }

    public void setDescription_Term(String description) {
        setDescription_Term(description, null);
    }

    public void setDescription_Term(String description, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("description", description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_NotEqual(String description) {
        setDescription_NotTerm(description, null);
    }

    public void setDescription_NotEqual(String description, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setDescription_NotTerm(description, opLambda);
    }

    public void setDescription_NotTerm(String description) {
        setDescription_NotTerm(description, null);
    }

    public void setDescription_NotTerm(String description, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("description", description));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_Terms(Collection<String> descriptionList) {
        setDescription_Terms(descriptionList, null);
    }

    public void setDescription_Terms(Collection<String> descriptionList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("description", descriptionList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_InScope(Collection<String> descriptionList) {
        setDescription_Terms(descriptionList, null);
    }

    public void setDescription_InScope(Collection<String> descriptionList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setDescription_Terms(descriptionList, opLambda);
    }

    public void setDescription_Match(String description) {
        setDescription_Match(description, null);
    }

    public void setDescription_Match(String description, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("description", description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_MatchPhrase(String description) {
        setDescription_MatchPhrase(description, null);
    }

    public void setDescription_MatchPhrase(String description, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("description", description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_MatchPhrasePrefix(String description) {
        setDescription_MatchPhrasePrefix(description, null);
    }

    public void setDescription_MatchPhrasePrefix(String description, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("description", description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_Fuzzy(String description) {
        setDescription_Fuzzy(description, null);
    }

    public void setDescription_Fuzzy(String description, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("description", description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_Prefix(String description) {
        setDescription_Prefix(description, null);
    }

    public void setDescription_Prefix(String description, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("description", description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_GreaterThan(String description) {
        setDescription_GreaterThan(description, null);
    }

    public void setDescription_GreaterThan(String description, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("description", ConditionKey.CK_GREATER_THAN, description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_LessThan(String description) {
        setDescription_LessThan(description, null);
    }

    public void setDescription_LessThan(String description, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("description", ConditionKey.CK_LESS_THAN, description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_GreaterEqual(String description) {
        setDescription_GreaterEqual(description, null);
    }

    public void setDescription_GreaterEqual(String description, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("description", ConditionKey.CK_GREATER_EQUAL, description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_LessEqual(String description) {
        setDescription_LessEqual(description, null);
    }

    public void setDescription_LessEqual(String description, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("description", ConditionKey.CK_LESS_EQUAL, description);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_Description_Asc() {
        regOBA("description");
        return this;
    }

    public BsUserCQ addOrderBy_Description_Desc() {
        regOBD("description");
        return this;
    }

    public void setTitle_Equal(String title) {
        setTitle_Term(title, null);
    }

    public void setTitle_Equal(String title, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setTitle_Term(title, opLambda);
    }

    public void setTitle_Term(String title) {
        setTitle_Term(title, null);
    }

    public void setTitle_Term(String title, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("title", title);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTitle_NotEqual(String title) {
        setTitle_NotTerm(title, null);
    }

    public void setTitle_NotEqual(String title, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setTitle_NotTerm(title, opLambda);
    }

    public void setTitle_NotTerm(String title) {
        setTitle_NotTerm(title, null);
    }

    public void setTitle_NotTerm(String title, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("title", title));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTitle_Terms(Collection<String> titleList) {
        setTitle_Terms(titleList, null);
    }

    public void setTitle_Terms(Collection<String> titleList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("title", titleList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTitle_InScope(Collection<String> titleList) {
        setTitle_Terms(titleList, null);
    }

    public void setTitle_InScope(Collection<String> titleList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setTitle_Terms(titleList, opLambda);
    }

    public void setTitle_Match(String title) {
        setTitle_Match(title, null);
    }

    public void setTitle_Match(String title, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("title", title);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTitle_MatchPhrase(String title) {
        setTitle_MatchPhrase(title, null);
    }

    public void setTitle_MatchPhrase(String title, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("title", title);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTitle_MatchPhrasePrefix(String title) {
        setTitle_MatchPhrasePrefix(title, null);
    }

    public void setTitle_MatchPhrasePrefix(String title, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("title", title);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTitle_Fuzzy(String title) {
        setTitle_Fuzzy(title, null);
    }

    public void setTitle_Fuzzy(String title, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("title", title);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTitle_Prefix(String title) {
        setTitle_Prefix(title, null);
    }

    public void setTitle_Prefix(String title, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("title", title);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTitle_GreaterThan(String title) {
        setTitle_GreaterThan(title, null);
    }

    public void setTitle_GreaterThan(String title, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("title", ConditionKey.CK_GREATER_THAN, title);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTitle_LessThan(String title) {
        setTitle_LessThan(title, null);
    }

    public void setTitle_LessThan(String title, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("title", ConditionKey.CK_LESS_THAN, title);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTitle_GreaterEqual(String title) {
        setTitle_GreaterEqual(title, null);
    }

    public void setTitle_GreaterEqual(String title, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("title", ConditionKey.CK_GREATER_EQUAL, title);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTitle_LessEqual(String title) {
        setTitle_LessEqual(title, null);
    }

    public void setTitle_LessEqual(String title, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("title", ConditionKey.CK_LESS_EQUAL, title);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_Title_Asc() {
        regOBA("title");
        return this;
    }

    public BsUserCQ addOrderBy_Title_Desc() {
        regOBD("title");
        return this;
    }

    public void setPager_Equal(String pager) {
        setPager_Term(pager, null);
    }

    public void setPager_Equal(String pager, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setPager_Term(pager, opLambda);
    }

    public void setPager_Term(String pager) {
        setPager_Term(pager, null);
    }

    public void setPager_Term(String pager, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("pager", pager);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPager_NotEqual(String pager) {
        setPager_NotTerm(pager, null);
    }

    public void setPager_NotEqual(String pager, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setPager_NotTerm(pager, opLambda);
    }

    public void setPager_NotTerm(String pager) {
        setPager_NotTerm(pager, null);
    }

    public void setPager_NotTerm(String pager, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("pager", pager));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPager_Terms(Collection<String> pagerList) {
        setPager_Terms(pagerList, null);
    }

    public void setPager_Terms(Collection<String> pagerList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("pager", pagerList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPager_InScope(Collection<String> pagerList) {
        setPager_Terms(pagerList, null);
    }

    public void setPager_InScope(Collection<String> pagerList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setPager_Terms(pagerList, opLambda);
    }

    public void setPager_Match(String pager) {
        setPager_Match(pager, null);
    }

    public void setPager_Match(String pager, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("pager", pager);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPager_MatchPhrase(String pager) {
        setPager_MatchPhrase(pager, null);
    }

    public void setPager_MatchPhrase(String pager, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("pager", pager);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPager_MatchPhrasePrefix(String pager) {
        setPager_MatchPhrasePrefix(pager, null);
    }

    public void setPager_MatchPhrasePrefix(String pager, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("pager", pager);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPager_Fuzzy(String pager) {
        setPager_Fuzzy(pager, null);
    }

    public void setPager_Fuzzy(String pager, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("pager", pager);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPager_Prefix(String pager) {
        setPager_Prefix(pager, null);
    }

    public void setPager_Prefix(String pager, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("pager", pager);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPager_GreaterThan(String pager) {
        setPager_GreaterThan(pager, null);
    }

    public void setPager_GreaterThan(String pager, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("pager", ConditionKey.CK_GREATER_THAN, pager);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPager_LessThan(String pager) {
        setPager_LessThan(pager, null);
    }

    public void setPager_LessThan(String pager, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("pager", ConditionKey.CK_LESS_THAN, pager);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPager_GreaterEqual(String pager) {
        setPager_GreaterEqual(pager, null);
    }

    public void setPager_GreaterEqual(String pager, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("pager", ConditionKey.CK_GREATER_EQUAL, pager);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPager_LessEqual(String pager) {
        setPager_LessEqual(pager, null);
    }

    public void setPager_LessEqual(String pager, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("pager", ConditionKey.CK_LESS_EQUAL, pager);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_Pager_Asc() {
        regOBA("pager");
        return this;
    }

    public BsUserCQ addOrderBy_Pager_Desc() {
        regOBD("pager");
        return this;
    }

    public void setStreet_Equal(String street) {
        setStreet_Term(street, null);
    }

    public void setStreet_Equal(String street, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setStreet_Term(street, opLambda);
    }

    public void setStreet_Term(String street) {
        setStreet_Term(street, null);
    }

    public void setStreet_Term(String street, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("street", street);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStreet_NotEqual(String street) {
        setStreet_NotTerm(street, null);
    }

    public void setStreet_NotEqual(String street, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setStreet_NotTerm(street, opLambda);
    }

    public void setStreet_NotTerm(String street) {
        setStreet_NotTerm(street, null);
    }

    public void setStreet_NotTerm(String street, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("street", street));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStreet_Terms(Collection<String> streetList) {
        setStreet_Terms(streetList, null);
    }

    public void setStreet_Terms(Collection<String> streetList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("street", streetList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStreet_InScope(Collection<String> streetList) {
        setStreet_Terms(streetList, null);
    }

    public void setStreet_InScope(Collection<String> streetList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setStreet_Terms(streetList, opLambda);
    }

    public void setStreet_Match(String street) {
        setStreet_Match(street, null);
    }

    public void setStreet_Match(String street, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("street", street);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStreet_MatchPhrase(String street) {
        setStreet_MatchPhrase(street, null);
    }

    public void setStreet_MatchPhrase(String street, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("street", street);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStreet_MatchPhrasePrefix(String street) {
        setStreet_MatchPhrasePrefix(street, null);
    }

    public void setStreet_MatchPhrasePrefix(String street, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("street", street);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStreet_Fuzzy(String street) {
        setStreet_Fuzzy(street, null);
    }

    public void setStreet_Fuzzy(String street, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("street", street);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStreet_Prefix(String street) {
        setStreet_Prefix(street, null);
    }

    public void setStreet_Prefix(String street, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("street", street);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStreet_GreaterThan(String street) {
        setStreet_GreaterThan(street, null);
    }

    public void setStreet_GreaterThan(String street, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("street", ConditionKey.CK_GREATER_THAN, street);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStreet_LessThan(String street) {
        setStreet_LessThan(street, null);
    }

    public void setStreet_LessThan(String street, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("street", ConditionKey.CK_LESS_THAN, street);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStreet_GreaterEqual(String street) {
        setStreet_GreaterEqual(street, null);
    }

    public void setStreet_GreaterEqual(String street, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("street", ConditionKey.CK_GREATER_EQUAL, street);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStreet_LessEqual(String street) {
        setStreet_LessEqual(street, null);
    }

    public void setStreet_LessEqual(String street, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("street", ConditionKey.CK_LESS_EQUAL, street);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_Street_Asc() {
        regOBA("street");
        return this;
    }

    public BsUserCQ addOrderBy_Street_Desc() {
        regOBD("street");
        return this;
    }

    public void setPostalCode_Equal(String postalCode) {
        setPostalCode_Term(postalCode, null);
    }

    public void setPostalCode_Equal(String postalCode, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setPostalCode_Term(postalCode, opLambda);
    }

    public void setPostalCode_Term(String postalCode) {
        setPostalCode_Term(postalCode, null);
    }

    public void setPostalCode_Term(String postalCode, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("postalCode", postalCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalCode_NotEqual(String postalCode) {
        setPostalCode_NotTerm(postalCode, null);
    }

    public void setPostalCode_NotEqual(String postalCode, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setPostalCode_NotTerm(postalCode, opLambda);
    }

    public void setPostalCode_NotTerm(String postalCode) {
        setPostalCode_NotTerm(postalCode, null);
    }

    public void setPostalCode_NotTerm(String postalCode, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("postalCode", postalCode));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalCode_Terms(Collection<String> postalCodeList) {
        setPostalCode_Terms(postalCodeList, null);
    }

    public void setPostalCode_Terms(Collection<String> postalCodeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("postalCode", postalCodeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalCode_InScope(Collection<String> postalCodeList) {
        setPostalCode_Terms(postalCodeList, null);
    }

    public void setPostalCode_InScope(Collection<String> postalCodeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setPostalCode_Terms(postalCodeList, opLambda);
    }

    public void setPostalCode_Match(String postalCode) {
        setPostalCode_Match(postalCode, null);
    }

    public void setPostalCode_Match(String postalCode, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("postalCode", postalCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalCode_MatchPhrase(String postalCode) {
        setPostalCode_MatchPhrase(postalCode, null);
    }

    public void setPostalCode_MatchPhrase(String postalCode, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("postalCode", postalCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalCode_MatchPhrasePrefix(String postalCode) {
        setPostalCode_MatchPhrasePrefix(postalCode, null);
    }

    public void setPostalCode_MatchPhrasePrefix(String postalCode, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("postalCode", postalCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalCode_Fuzzy(String postalCode) {
        setPostalCode_Fuzzy(postalCode, null);
    }

    public void setPostalCode_Fuzzy(String postalCode, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("postalCode", postalCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalCode_Prefix(String postalCode) {
        setPostalCode_Prefix(postalCode, null);
    }

    public void setPostalCode_Prefix(String postalCode, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("postalCode", postalCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalCode_GreaterThan(String postalCode) {
        setPostalCode_GreaterThan(postalCode, null);
    }

    public void setPostalCode_GreaterThan(String postalCode, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("postalCode", ConditionKey.CK_GREATER_THAN, postalCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalCode_LessThan(String postalCode) {
        setPostalCode_LessThan(postalCode, null);
    }

    public void setPostalCode_LessThan(String postalCode, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("postalCode", ConditionKey.CK_LESS_THAN, postalCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalCode_GreaterEqual(String postalCode) {
        setPostalCode_GreaterEqual(postalCode, null);
    }

    public void setPostalCode_GreaterEqual(String postalCode, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("postalCode", ConditionKey.CK_GREATER_EQUAL, postalCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalCode_LessEqual(String postalCode) {
        setPostalCode_LessEqual(postalCode, null);
    }

    public void setPostalCode_LessEqual(String postalCode, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("postalCode", ConditionKey.CK_LESS_EQUAL, postalCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_PostalCode_Asc() {
        regOBA("postalCode");
        return this;
    }

    public BsUserCQ addOrderBy_PostalCode_Desc() {
        regOBD("postalCode");
        return this;
    }

    public void setPhysicalDeliveryOfficeName_Equal(String physicalDeliveryOfficeName) {
        setPhysicalDeliveryOfficeName_Term(physicalDeliveryOfficeName, null);
    }

    public void setPhysicalDeliveryOfficeName_Equal(String physicalDeliveryOfficeName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setPhysicalDeliveryOfficeName_Term(physicalDeliveryOfficeName, opLambda);
    }

    public void setPhysicalDeliveryOfficeName_Term(String physicalDeliveryOfficeName) {
        setPhysicalDeliveryOfficeName_Term(physicalDeliveryOfficeName, null);
    }

    public void setPhysicalDeliveryOfficeName_Term(String physicalDeliveryOfficeName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("physicalDeliveryOfficeName", physicalDeliveryOfficeName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPhysicalDeliveryOfficeName_NotEqual(String physicalDeliveryOfficeName) {
        setPhysicalDeliveryOfficeName_NotTerm(physicalDeliveryOfficeName, null);
    }

    public void setPhysicalDeliveryOfficeName_NotEqual(String physicalDeliveryOfficeName, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setPhysicalDeliveryOfficeName_NotTerm(physicalDeliveryOfficeName, opLambda);
    }

    public void setPhysicalDeliveryOfficeName_NotTerm(String physicalDeliveryOfficeName) {
        setPhysicalDeliveryOfficeName_NotTerm(physicalDeliveryOfficeName, null);
    }

    public void setPhysicalDeliveryOfficeName_NotTerm(String physicalDeliveryOfficeName, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("physicalDeliveryOfficeName", physicalDeliveryOfficeName));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPhysicalDeliveryOfficeName_Terms(Collection<String> physicalDeliveryOfficeNameList) {
        setPhysicalDeliveryOfficeName_Terms(physicalDeliveryOfficeNameList, null);
    }

    public void setPhysicalDeliveryOfficeName_Terms(Collection<String> physicalDeliveryOfficeNameList,
            ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("physicalDeliveryOfficeName", physicalDeliveryOfficeNameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPhysicalDeliveryOfficeName_InScope(Collection<String> physicalDeliveryOfficeNameList) {
        setPhysicalDeliveryOfficeName_Terms(physicalDeliveryOfficeNameList, null);
    }

    public void setPhysicalDeliveryOfficeName_InScope(Collection<String> physicalDeliveryOfficeNameList,
            ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setPhysicalDeliveryOfficeName_Terms(physicalDeliveryOfficeNameList, opLambda);
    }

    public void setPhysicalDeliveryOfficeName_Match(String physicalDeliveryOfficeName) {
        setPhysicalDeliveryOfficeName_Match(physicalDeliveryOfficeName, null);
    }

    public void setPhysicalDeliveryOfficeName_Match(String physicalDeliveryOfficeName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("physicalDeliveryOfficeName", physicalDeliveryOfficeName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPhysicalDeliveryOfficeName_MatchPhrase(String physicalDeliveryOfficeName) {
        setPhysicalDeliveryOfficeName_MatchPhrase(physicalDeliveryOfficeName, null);
    }

    public void setPhysicalDeliveryOfficeName_MatchPhrase(String physicalDeliveryOfficeName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("physicalDeliveryOfficeName", physicalDeliveryOfficeName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPhysicalDeliveryOfficeName_MatchPhrasePrefix(String physicalDeliveryOfficeName) {
        setPhysicalDeliveryOfficeName_MatchPhrasePrefix(physicalDeliveryOfficeName, null);
    }

    public void setPhysicalDeliveryOfficeName_MatchPhrasePrefix(String physicalDeliveryOfficeName,
            ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("physicalDeliveryOfficeName", physicalDeliveryOfficeName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPhysicalDeliveryOfficeName_Fuzzy(String physicalDeliveryOfficeName) {
        setPhysicalDeliveryOfficeName_Fuzzy(physicalDeliveryOfficeName, null);
    }

    public void setPhysicalDeliveryOfficeName_Fuzzy(String physicalDeliveryOfficeName, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("physicalDeliveryOfficeName", physicalDeliveryOfficeName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPhysicalDeliveryOfficeName_Prefix(String physicalDeliveryOfficeName) {
        setPhysicalDeliveryOfficeName_Prefix(physicalDeliveryOfficeName, null);
    }

    public void setPhysicalDeliveryOfficeName_Prefix(String physicalDeliveryOfficeName, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("physicalDeliveryOfficeName", physicalDeliveryOfficeName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPhysicalDeliveryOfficeName_GreaterThan(String physicalDeliveryOfficeName) {
        setPhysicalDeliveryOfficeName_GreaterThan(physicalDeliveryOfficeName, null);
    }

    public void setPhysicalDeliveryOfficeName_GreaterThan(String physicalDeliveryOfficeName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("physicalDeliveryOfficeName", ConditionKey.CK_GREATER_THAN, physicalDeliveryOfficeName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPhysicalDeliveryOfficeName_LessThan(String physicalDeliveryOfficeName) {
        setPhysicalDeliveryOfficeName_LessThan(physicalDeliveryOfficeName, null);
    }

    public void setPhysicalDeliveryOfficeName_LessThan(String physicalDeliveryOfficeName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("physicalDeliveryOfficeName", ConditionKey.CK_LESS_THAN, physicalDeliveryOfficeName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPhysicalDeliveryOfficeName_GreaterEqual(String physicalDeliveryOfficeName) {
        setPhysicalDeliveryOfficeName_GreaterEqual(physicalDeliveryOfficeName, null);
    }

    public void setPhysicalDeliveryOfficeName_GreaterEqual(String physicalDeliveryOfficeName,
            ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("physicalDeliveryOfficeName", ConditionKey.CK_GREATER_EQUAL, physicalDeliveryOfficeName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPhysicalDeliveryOfficeName_LessEqual(String physicalDeliveryOfficeName) {
        setPhysicalDeliveryOfficeName_LessEqual(physicalDeliveryOfficeName, null);
    }

    public void setPhysicalDeliveryOfficeName_LessEqual(String physicalDeliveryOfficeName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("physicalDeliveryOfficeName", ConditionKey.CK_LESS_EQUAL, physicalDeliveryOfficeName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_PhysicalDeliveryOfficeName_Asc() {
        regOBA("physicalDeliveryOfficeName");
        return this;
    }

    public BsUserCQ addOrderBy_PhysicalDeliveryOfficeName_Desc() {
        regOBD("physicalDeliveryOfficeName");
        return this;
    }

    public void setDestinationIndicator_Equal(String destinationIndicator) {
        setDestinationIndicator_Term(destinationIndicator, null);
    }

    public void setDestinationIndicator_Equal(String destinationIndicator, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setDestinationIndicator_Term(destinationIndicator, opLambda);
    }

    public void setDestinationIndicator_Term(String destinationIndicator) {
        setDestinationIndicator_Term(destinationIndicator, null);
    }

    public void setDestinationIndicator_Term(String destinationIndicator, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("destinationIndicator", destinationIndicator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDestinationIndicator_NotEqual(String destinationIndicator) {
        setDestinationIndicator_NotTerm(destinationIndicator, null);
    }

    public void setDestinationIndicator_NotEqual(String destinationIndicator, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setDestinationIndicator_NotTerm(destinationIndicator, opLambda);
    }

    public void setDestinationIndicator_NotTerm(String destinationIndicator) {
        setDestinationIndicator_NotTerm(destinationIndicator, null);
    }

    public void setDestinationIndicator_NotTerm(String destinationIndicator, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("destinationIndicator", destinationIndicator));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDestinationIndicator_Terms(Collection<String> destinationIndicatorList) {
        setDestinationIndicator_Terms(destinationIndicatorList, null);
    }

    public void setDestinationIndicator_Terms(Collection<String> destinationIndicatorList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("destinationIndicator", destinationIndicatorList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDestinationIndicator_InScope(Collection<String> destinationIndicatorList) {
        setDestinationIndicator_Terms(destinationIndicatorList, null);
    }

    public void setDestinationIndicator_InScope(Collection<String> destinationIndicatorList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setDestinationIndicator_Terms(destinationIndicatorList, opLambda);
    }

    public void setDestinationIndicator_Match(String destinationIndicator) {
        setDestinationIndicator_Match(destinationIndicator, null);
    }

    public void setDestinationIndicator_Match(String destinationIndicator, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("destinationIndicator", destinationIndicator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDestinationIndicator_MatchPhrase(String destinationIndicator) {
        setDestinationIndicator_MatchPhrase(destinationIndicator, null);
    }

    public void setDestinationIndicator_MatchPhrase(String destinationIndicator, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("destinationIndicator", destinationIndicator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDestinationIndicator_MatchPhrasePrefix(String destinationIndicator) {
        setDestinationIndicator_MatchPhrasePrefix(destinationIndicator, null);
    }

    public void setDestinationIndicator_MatchPhrasePrefix(String destinationIndicator, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("destinationIndicator", destinationIndicator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDestinationIndicator_Fuzzy(String destinationIndicator) {
        setDestinationIndicator_Fuzzy(destinationIndicator, null);
    }

    public void setDestinationIndicator_Fuzzy(String destinationIndicator, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("destinationIndicator", destinationIndicator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDestinationIndicator_Prefix(String destinationIndicator) {
        setDestinationIndicator_Prefix(destinationIndicator, null);
    }

    public void setDestinationIndicator_Prefix(String destinationIndicator, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("destinationIndicator", destinationIndicator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDestinationIndicator_GreaterThan(String destinationIndicator) {
        setDestinationIndicator_GreaterThan(destinationIndicator, null);
    }

    public void setDestinationIndicator_GreaterThan(String destinationIndicator, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("destinationIndicator", ConditionKey.CK_GREATER_THAN, destinationIndicator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDestinationIndicator_LessThan(String destinationIndicator) {
        setDestinationIndicator_LessThan(destinationIndicator, null);
    }

    public void setDestinationIndicator_LessThan(String destinationIndicator, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("destinationIndicator", ConditionKey.CK_LESS_THAN, destinationIndicator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDestinationIndicator_GreaterEqual(String destinationIndicator) {
        setDestinationIndicator_GreaterEqual(destinationIndicator, null);
    }

    public void setDestinationIndicator_GreaterEqual(String destinationIndicator, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("destinationIndicator", ConditionKey.CK_GREATER_EQUAL, destinationIndicator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDestinationIndicator_LessEqual(String destinationIndicator) {
        setDestinationIndicator_LessEqual(destinationIndicator, null);
    }

    public void setDestinationIndicator_LessEqual(String destinationIndicator, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("destinationIndicator", ConditionKey.CK_LESS_EQUAL, destinationIndicator);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_DestinationIndicator_Asc() {
        regOBA("destinationIndicator");
        return this;
    }

    public BsUserCQ addOrderBy_DestinationIndicator_Desc() {
        regOBD("destinationIndicator");
        return this;
    }

    public void setInternationaliSDNNumber_Equal(String internationaliSDNNumber) {
        setInternationaliSDNNumber_Term(internationaliSDNNumber, null);
    }

    public void setInternationaliSDNNumber_Equal(String internationaliSDNNumber, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setInternationaliSDNNumber_Term(internationaliSDNNumber, opLambda);
    }

    public void setInternationaliSDNNumber_Term(String internationaliSDNNumber) {
        setInternationaliSDNNumber_Term(internationaliSDNNumber, null);
    }

    public void setInternationaliSDNNumber_Term(String internationaliSDNNumber, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("internationaliSDNNumber", internationaliSDNNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInternationaliSDNNumber_NotEqual(String internationaliSDNNumber) {
        setInternationaliSDNNumber_NotTerm(internationaliSDNNumber, null);
    }

    public void setInternationaliSDNNumber_NotEqual(String internationaliSDNNumber, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setInternationaliSDNNumber_NotTerm(internationaliSDNNumber, opLambda);
    }

    public void setInternationaliSDNNumber_NotTerm(String internationaliSDNNumber) {
        setInternationaliSDNNumber_NotTerm(internationaliSDNNumber, null);
    }

    public void setInternationaliSDNNumber_NotTerm(String internationaliSDNNumber, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("internationaliSDNNumber", internationaliSDNNumber));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInternationaliSDNNumber_Terms(Collection<String> internationaliSDNNumberList) {
        setInternationaliSDNNumber_Terms(internationaliSDNNumberList, null);
    }

    public void setInternationaliSDNNumber_Terms(Collection<String> internationaliSDNNumberList,
            ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("internationaliSDNNumber", internationaliSDNNumberList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInternationaliSDNNumber_InScope(Collection<String> internationaliSDNNumberList) {
        setInternationaliSDNNumber_Terms(internationaliSDNNumberList, null);
    }

    public void setInternationaliSDNNumber_InScope(Collection<String> internationaliSDNNumberList,
            ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setInternationaliSDNNumber_Terms(internationaliSDNNumberList, opLambda);
    }

    public void setInternationaliSDNNumber_Match(String internationaliSDNNumber) {
        setInternationaliSDNNumber_Match(internationaliSDNNumber, null);
    }

    public void setInternationaliSDNNumber_Match(String internationaliSDNNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("internationaliSDNNumber", internationaliSDNNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInternationaliSDNNumber_MatchPhrase(String internationaliSDNNumber) {
        setInternationaliSDNNumber_MatchPhrase(internationaliSDNNumber, null);
    }

    public void setInternationaliSDNNumber_MatchPhrase(String internationaliSDNNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("internationaliSDNNumber", internationaliSDNNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInternationaliSDNNumber_MatchPhrasePrefix(String internationaliSDNNumber) {
        setInternationaliSDNNumber_MatchPhrasePrefix(internationaliSDNNumber, null);
    }

    public void setInternationaliSDNNumber_MatchPhrasePrefix(String internationaliSDNNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("internationaliSDNNumber", internationaliSDNNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInternationaliSDNNumber_Fuzzy(String internationaliSDNNumber) {
        setInternationaliSDNNumber_Fuzzy(internationaliSDNNumber, null);
    }

    public void setInternationaliSDNNumber_Fuzzy(String internationaliSDNNumber, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("internationaliSDNNumber", internationaliSDNNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInternationaliSDNNumber_Prefix(String internationaliSDNNumber) {
        setInternationaliSDNNumber_Prefix(internationaliSDNNumber, null);
    }

    public void setInternationaliSDNNumber_Prefix(String internationaliSDNNumber, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("internationaliSDNNumber", internationaliSDNNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInternationaliSDNNumber_GreaterThan(String internationaliSDNNumber) {
        setInternationaliSDNNumber_GreaterThan(internationaliSDNNumber, null);
    }

    public void setInternationaliSDNNumber_GreaterThan(String internationaliSDNNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("internationaliSDNNumber", ConditionKey.CK_GREATER_THAN, internationaliSDNNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInternationaliSDNNumber_LessThan(String internationaliSDNNumber) {
        setInternationaliSDNNumber_LessThan(internationaliSDNNumber, null);
    }

    public void setInternationaliSDNNumber_LessThan(String internationaliSDNNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("internationaliSDNNumber", ConditionKey.CK_LESS_THAN, internationaliSDNNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInternationaliSDNNumber_GreaterEqual(String internationaliSDNNumber) {
        setInternationaliSDNNumber_GreaterEqual(internationaliSDNNumber, null);
    }

    public void setInternationaliSDNNumber_GreaterEqual(String internationaliSDNNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("internationaliSDNNumber", ConditionKey.CK_GREATER_EQUAL, internationaliSDNNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInternationaliSDNNumber_LessEqual(String internationaliSDNNumber) {
        setInternationaliSDNNumber_LessEqual(internationaliSDNNumber, null);
    }

    public void setInternationaliSDNNumber_LessEqual(String internationaliSDNNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("internationaliSDNNumber", ConditionKey.CK_LESS_EQUAL, internationaliSDNNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_InternationaliSDNNumber_Asc() {
        regOBA("internationaliSDNNumber");
        return this;
    }

    public BsUserCQ addOrderBy_InternationaliSDNNumber_Desc() {
        regOBD("internationaliSDNNumber");
        return this;
    }

    public void setState_Equal(String state) {
        setState_Term(state, null);
    }

    public void setState_Equal(String state, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setState_Term(state, opLambda);
    }

    public void setState_Term(String state) {
        setState_Term(state, null);
    }

    public void setState_Term(String state, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("state", state);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setState_NotEqual(String state) {
        setState_NotTerm(state, null);
    }

    public void setState_NotEqual(String state, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setState_NotTerm(state, opLambda);
    }

    public void setState_NotTerm(String state) {
        setState_NotTerm(state, null);
    }

    public void setState_NotTerm(String state, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("state", state));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setState_Terms(Collection<String> stateList) {
        setState_Terms(stateList, null);
    }

    public void setState_Terms(Collection<String> stateList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("state", stateList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setState_InScope(Collection<String> stateList) {
        setState_Terms(stateList, null);
    }

    public void setState_InScope(Collection<String> stateList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setState_Terms(stateList, opLambda);
    }

    public void setState_Match(String state) {
        setState_Match(state, null);
    }

    public void setState_Match(String state, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("state", state);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setState_MatchPhrase(String state) {
        setState_MatchPhrase(state, null);
    }

    public void setState_MatchPhrase(String state, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("state", state);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setState_MatchPhrasePrefix(String state) {
        setState_MatchPhrasePrefix(state, null);
    }

    public void setState_MatchPhrasePrefix(String state, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("state", state);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setState_Fuzzy(String state) {
        setState_Fuzzy(state, null);
    }

    public void setState_Fuzzy(String state, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("state", state);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setState_Prefix(String state) {
        setState_Prefix(state, null);
    }

    public void setState_Prefix(String state, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("state", state);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setState_GreaterThan(String state) {
        setState_GreaterThan(state, null);
    }

    public void setState_GreaterThan(String state, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("state", ConditionKey.CK_GREATER_THAN, state);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setState_LessThan(String state) {
        setState_LessThan(state, null);
    }

    public void setState_LessThan(String state, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("state", ConditionKey.CK_LESS_THAN, state);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setState_GreaterEqual(String state) {
        setState_GreaterEqual(state, null);
    }

    public void setState_GreaterEqual(String state, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("state", ConditionKey.CK_GREATER_EQUAL, state);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setState_LessEqual(String state) {
        setState_LessEqual(state, null);
    }

    public void setState_LessEqual(String state, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("state", ConditionKey.CK_LESS_EQUAL, state);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_State_Asc() {
        regOBA("state");
        return this;
    }

    public BsUserCQ addOrderBy_State_Desc() {
        regOBD("state");
        return this;
    }

    public void setEmployeeType_Equal(String employeeType) {
        setEmployeeType_Term(employeeType, null);
    }

    public void setEmployeeType_Equal(String employeeType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setEmployeeType_Term(employeeType, opLambda);
    }

    public void setEmployeeType_Term(String employeeType) {
        setEmployeeType_Term(employeeType, null);
    }

    public void setEmployeeType_Term(String employeeType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("employeeType", employeeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeType_NotEqual(String employeeType) {
        setEmployeeType_NotTerm(employeeType, null);
    }

    public void setEmployeeType_NotEqual(String employeeType, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setEmployeeType_NotTerm(employeeType, opLambda);
    }

    public void setEmployeeType_NotTerm(String employeeType) {
        setEmployeeType_NotTerm(employeeType, null);
    }

    public void setEmployeeType_NotTerm(String employeeType, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("employeeType", employeeType));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeType_Terms(Collection<String> employeeTypeList) {
        setEmployeeType_Terms(employeeTypeList, null);
    }

    public void setEmployeeType_Terms(Collection<String> employeeTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("employeeType", employeeTypeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeType_InScope(Collection<String> employeeTypeList) {
        setEmployeeType_Terms(employeeTypeList, null);
    }

    public void setEmployeeType_InScope(Collection<String> employeeTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setEmployeeType_Terms(employeeTypeList, opLambda);
    }

    public void setEmployeeType_Match(String employeeType) {
        setEmployeeType_Match(employeeType, null);
    }

    public void setEmployeeType_Match(String employeeType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("employeeType", employeeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeType_MatchPhrase(String employeeType) {
        setEmployeeType_MatchPhrase(employeeType, null);
    }

    public void setEmployeeType_MatchPhrase(String employeeType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("employeeType", employeeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeType_MatchPhrasePrefix(String employeeType) {
        setEmployeeType_MatchPhrasePrefix(employeeType, null);
    }

    public void setEmployeeType_MatchPhrasePrefix(String employeeType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("employeeType", employeeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeType_Fuzzy(String employeeType) {
        setEmployeeType_Fuzzy(employeeType, null);
    }

    public void setEmployeeType_Fuzzy(String employeeType, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("employeeType", employeeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeType_Prefix(String employeeType) {
        setEmployeeType_Prefix(employeeType, null);
    }

    public void setEmployeeType_Prefix(String employeeType, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("employeeType", employeeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeType_GreaterThan(String employeeType) {
        setEmployeeType_GreaterThan(employeeType, null);
    }

    public void setEmployeeType_GreaterThan(String employeeType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("employeeType", ConditionKey.CK_GREATER_THAN, employeeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeType_LessThan(String employeeType) {
        setEmployeeType_LessThan(employeeType, null);
    }

    public void setEmployeeType_LessThan(String employeeType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("employeeType", ConditionKey.CK_LESS_THAN, employeeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeType_GreaterEqual(String employeeType) {
        setEmployeeType_GreaterEqual(employeeType, null);
    }

    public void setEmployeeType_GreaterEqual(String employeeType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("employeeType", ConditionKey.CK_GREATER_EQUAL, employeeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeType_LessEqual(String employeeType) {
        setEmployeeType_LessEqual(employeeType, null);
    }

    public void setEmployeeType_LessEqual(String employeeType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("employeeType", ConditionKey.CK_LESS_EQUAL, employeeType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_EmployeeType_Asc() {
        regOBA("employeeType");
        return this;
    }

    public BsUserCQ addOrderBy_EmployeeType_Desc() {
        regOBD("employeeType");
        return this;
    }

    public void setFacsimileTelephoneNumber_Equal(String facsimileTelephoneNumber) {
        setFacsimileTelephoneNumber_Term(facsimileTelephoneNumber, null);
    }

    public void setFacsimileTelephoneNumber_Equal(String facsimileTelephoneNumber, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setFacsimileTelephoneNumber_Term(facsimileTelephoneNumber, opLambda);
    }

    public void setFacsimileTelephoneNumber_Term(String facsimileTelephoneNumber) {
        setFacsimileTelephoneNumber_Term(facsimileTelephoneNumber, null);
    }

    public void setFacsimileTelephoneNumber_Term(String facsimileTelephoneNumber, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("facsimileTelephoneNumber", facsimileTelephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFacsimileTelephoneNumber_NotEqual(String facsimileTelephoneNumber) {
        setFacsimileTelephoneNumber_NotTerm(facsimileTelephoneNumber, null);
    }

    public void setFacsimileTelephoneNumber_NotEqual(String facsimileTelephoneNumber, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setFacsimileTelephoneNumber_NotTerm(facsimileTelephoneNumber, opLambda);
    }

    public void setFacsimileTelephoneNumber_NotTerm(String facsimileTelephoneNumber) {
        setFacsimileTelephoneNumber_NotTerm(facsimileTelephoneNumber, null);
    }

    public void setFacsimileTelephoneNumber_NotTerm(String facsimileTelephoneNumber, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("facsimileTelephoneNumber", facsimileTelephoneNumber));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFacsimileTelephoneNumber_Terms(Collection<String> facsimileTelephoneNumberList) {
        setFacsimileTelephoneNumber_Terms(facsimileTelephoneNumberList, null);
    }

    public void setFacsimileTelephoneNumber_Terms(Collection<String> facsimileTelephoneNumberList,
            ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("facsimileTelephoneNumber", facsimileTelephoneNumberList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFacsimileTelephoneNumber_InScope(Collection<String> facsimileTelephoneNumberList) {
        setFacsimileTelephoneNumber_Terms(facsimileTelephoneNumberList, null);
    }

    public void setFacsimileTelephoneNumber_InScope(Collection<String> facsimileTelephoneNumberList,
            ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setFacsimileTelephoneNumber_Terms(facsimileTelephoneNumberList, opLambda);
    }

    public void setFacsimileTelephoneNumber_Match(String facsimileTelephoneNumber) {
        setFacsimileTelephoneNumber_Match(facsimileTelephoneNumber, null);
    }

    public void setFacsimileTelephoneNumber_Match(String facsimileTelephoneNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("facsimileTelephoneNumber", facsimileTelephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFacsimileTelephoneNumber_MatchPhrase(String facsimileTelephoneNumber) {
        setFacsimileTelephoneNumber_MatchPhrase(facsimileTelephoneNumber, null);
    }

    public void setFacsimileTelephoneNumber_MatchPhrase(String facsimileTelephoneNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("facsimileTelephoneNumber", facsimileTelephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFacsimileTelephoneNumber_MatchPhrasePrefix(String facsimileTelephoneNumber) {
        setFacsimileTelephoneNumber_MatchPhrasePrefix(facsimileTelephoneNumber, null);
    }

    public void setFacsimileTelephoneNumber_MatchPhrasePrefix(String facsimileTelephoneNumber,
            ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("facsimileTelephoneNumber", facsimileTelephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFacsimileTelephoneNumber_Fuzzy(String facsimileTelephoneNumber) {
        setFacsimileTelephoneNumber_Fuzzy(facsimileTelephoneNumber, null);
    }

    public void setFacsimileTelephoneNumber_Fuzzy(String facsimileTelephoneNumber, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("facsimileTelephoneNumber", facsimileTelephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFacsimileTelephoneNumber_Prefix(String facsimileTelephoneNumber) {
        setFacsimileTelephoneNumber_Prefix(facsimileTelephoneNumber, null);
    }

    public void setFacsimileTelephoneNumber_Prefix(String facsimileTelephoneNumber, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("facsimileTelephoneNumber", facsimileTelephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFacsimileTelephoneNumber_GreaterThan(String facsimileTelephoneNumber) {
        setFacsimileTelephoneNumber_GreaterThan(facsimileTelephoneNumber, null);
    }

    public void setFacsimileTelephoneNumber_GreaterThan(String facsimileTelephoneNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("facsimileTelephoneNumber", ConditionKey.CK_GREATER_THAN, facsimileTelephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFacsimileTelephoneNumber_LessThan(String facsimileTelephoneNumber) {
        setFacsimileTelephoneNumber_LessThan(facsimileTelephoneNumber, null);
    }

    public void setFacsimileTelephoneNumber_LessThan(String facsimileTelephoneNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("facsimileTelephoneNumber", ConditionKey.CK_LESS_THAN, facsimileTelephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFacsimileTelephoneNumber_GreaterEqual(String facsimileTelephoneNumber) {
        setFacsimileTelephoneNumber_GreaterEqual(facsimileTelephoneNumber, null);
    }

    public void setFacsimileTelephoneNumber_GreaterEqual(String facsimileTelephoneNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("facsimileTelephoneNumber", ConditionKey.CK_GREATER_EQUAL, facsimileTelephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFacsimileTelephoneNumber_LessEqual(String facsimileTelephoneNumber) {
        setFacsimileTelephoneNumber_LessEqual(facsimileTelephoneNumber, null);
    }

    public void setFacsimileTelephoneNumber_LessEqual(String facsimileTelephoneNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("facsimileTelephoneNumber", ConditionKey.CK_LESS_EQUAL, facsimileTelephoneNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_FacsimileTelephoneNumber_Asc() {
        regOBA("facsimileTelephoneNumber");
        return this;
    }

    public BsUserCQ addOrderBy_FacsimileTelephoneNumber_Desc() {
        regOBD("facsimileTelephoneNumber");
        return this;
    }

    public void setPostOfficeBox_Equal(String postOfficeBox) {
        setPostOfficeBox_Term(postOfficeBox, null);
    }

    public void setPostOfficeBox_Equal(String postOfficeBox, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setPostOfficeBox_Term(postOfficeBox, opLambda);
    }

    public void setPostOfficeBox_Term(String postOfficeBox) {
        setPostOfficeBox_Term(postOfficeBox, null);
    }

    public void setPostOfficeBox_Term(String postOfficeBox, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("postOfficeBox", postOfficeBox);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostOfficeBox_NotEqual(String postOfficeBox) {
        setPostOfficeBox_NotTerm(postOfficeBox, null);
    }

    public void setPostOfficeBox_NotEqual(String postOfficeBox, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setPostOfficeBox_NotTerm(postOfficeBox, opLambda);
    }

    public void setPostOfficeBox_NotTerm(String postOfficeBox) {
        setPostOfficeBox_NotTerm(postOfficeBox, null);
    }

    public void setPostOfficeBox_NotTerm(String postOfficeBox, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("postOfficeBox", postOfficeBox));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostOfficeBox_Terms(Collection<String> postOfficeBoxList) {
        setPostOfficeBox_Terms(postOfficeBoxList, null);
    }

    public void setPostOfficeBox_Terms(Collection<String> postOfficeBoxList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("postOfficeBox", postOfficeBoxList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostOfficeBox_InScope(Collection<String> postOfficeBoxList) {
        setPostOfficeBox_Terms(postOfficeBoxList, null);
    }

    public void setPostOfficeBox_InScope(Collection<String> postOfficeBoxList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setPostOfficeBox_Terms(postOfficeBoxList, opLambda);
    }

    public void setPostOfficeBox_Match(String postOfficeBox) {
        setPostOfficeBox_Match(postOfficeBox, null);
    }

    public void setPostOfficeBox_Match(String postOfficeBox, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("postOfficeBox", postOfficeBox);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostOfficeBox_MatchPhrase(String postOfficeBox) {
        setPostOfficeBox_MatchPhrase(postOfficeBox, null);
    }

    public void setPostOfficeBox_MatchPhrase(String postOfficeBox, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("postOfficeBox", postOfficeBox);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostOfficeBox_MatchPhrasePrefix(String postOfficeBox) {
        setPostOfficeBox_MatchPhrasePrefix(postOfficeBox, null);
    }

    public void setPostOfficeBox_MatchPhrasePrefix(String postOfficeBox, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("postOfficeBox", postOfficeBox);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostOfficeBox_Fuzzy(String postOfficeBox) {
        setPostOfficeBox_Fuzzy(postOfficeBox, null);
    }

    public void setPostOfficeBox_Fuzzy(String postOfficeBox, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("postOfficeBox", postOfficeBox);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostOfficeBox_Prefix(String postOfficeBox) {
        setPostOfficeBox_Prefix(postOfficeBox, null);
    }

    public void setPostOfficeBox_Prefix(String postOfficeBox, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("postOfficeBox", postOfficeBox);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostOfficeBox_GreaterThan(String postOfficeBox) {
        setPostOfficeBox_GreaterThan(postOfficeBox, null);
    }

    public void setPostOfficeBox_GreaterThan(String postOfficeBox, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("postOfficeBox", ConditionKey.CK_GREATER_THAN, postOfficeBox);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostOfficeBox_LessThan(String postOfficeBox) {
        setPostOfficeBox_LessThan(postOfficeBox, null);
    }

    public void setPostOfficeBox_LessThan(String postOfficeBox, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("postOfficeBox", ConditionKey.CK_LESS_THAN, postOfficeBox);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostOfficeBox_GreaterEqual(String postOfficeBox) {
        setPostOfficeBox_GreaterEqual(postOfficeBox, null);
    }

    public void setPostOfficeBox_GreaterEqual(String postOfficeBox, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("postOfficeBox", ConditionKey.CK_GREATER_EQUAL, postOfficeBox);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostOfficeBox_LessEqual(String postOfficeBox) {
        setPostOfficeBox_LessEqual(postOfficeBox, null);
    }

    public void setPostOfficeBox_LessEqual(String postOfficeBox, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("postOfficeBox", ConditionKey.CK_LESS_EQUAL, postOfficeBox);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_PostOfficeBox_Asc() {
        regOBA("postOfficeBox");
        return this;
    }

    public BsUserCQ addOrderBy_PostOfficeBox_Desc() {
        regOBD("postOfficeBox");
        return this;
    }

    public void setInitials_Equal(String initials) {
        setInitials_Term(initials, null);
    }

    public void setInitials_Equal(String initials, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setInitials_Term(initials, opLambda);
    }

    public void setInitials_Term(String initials) {
        setInitials_Term(initials, null);
    }

    public void setInitials_Term(String initials, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("initials", initials);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInitials_NotEqual(String initials) {
        setInitials_NotTerm(initials, null);
    }

    public void setInitials_NotEqual(String initials, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setInitials_NotTerm(initials, opLambda);
    }

    public void setInitials_NotTerm(String initials) {
        setInitials_NotTerm(initials, null);
    }

    public void setInitials_NotTerm(String initials, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("initials", initials));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInitials_Terms(Collection<String> initialsList) {
        setInitials_Terms(initialsList, null);
    }

    public void setInitials_Terms(Collection<String> initialsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("initials", initialsList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInitials_InScope(Collection<String> initialsList) {
        setInitials_Terms(initialsList, null);
    }

    public void setInitials_InScope(Collection<String> initialsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setInitials_Terms(initialsList, opLambda);
    }

    public void setInitials_Match(String initials) {
        setInitials_Match(initials, null);
    }

    public void setInitials_Match(String initials, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("initials", initials);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInitials_MatchPhrase(String initials) {
        setInitials_MatchPhrase(initials, null);
    }

    public void setInitials_MatchPhrase(String initials, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("initials", initials);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInitials_MatchPhrasePrefix(String initials) {
        setInitials_MatchPhrasePrefix(initials, null);
    }

    public void setInitials_MatchPhrasePrefix(String initials, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("initials", initials);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInitials_Fuzzy(String initials) {
        setInitials_Fuzzy(initials, null);
    }

    public void setInitials_Fuzzy(String initials, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("initials", initials);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInitials_Prefix(String initials) {
        setInitials_Prefix(initials, null);
    }

    public void setInitials_Prefix(String initials, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("initials", initials);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInitials_GreaterThan(String initials) {
        setInitials_GreaterThan(initials, null);
    }

    public void setInitials_GreaterThan(String initials, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("initials", ConditionKey.CK_GREATER_THAN, initials);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInitials_LessThan(String initials) {
        setInitials_LessThan(initials, null);
    }

    public void setInitials_LessThan(String initials, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("initials", ConditionKey.CK_LESS_THAN, initials);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInitials_GreaterEqual(String initials) {
        setInitials_GreaterEqual(initials, null);
    }

    public void setInitials_GreaterEqual(String initials, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("initials", ConditionKey.CK_GREATER_EQUAL, initials);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInitials_LessEqual(String initials) {
        setInitials_LessEqual(initials, null);
    }

    public void setInitials_LessEqual(String initials, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("initials", ConditionKey.CK_LESS_EQUAL, initials);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_Initials_Asc() {
        regOBA("initials");
        return this;
    }

    public BsUserCQ addOrderBy_Initials_Desc() {
        regOBD("initials");
        return this;
    }

    public void setCarLicense_Equal(String carLicense) {
        setCarLicense_Term(carLicense, null);
    }

    public void setCarLicense_Equal(String carLicense, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setCarLicense_Term(carLicense, opLambda);
    }

    public void setCarLicense_Term(String carLicense) {
        setCarLicense_Term(carLicense, null);
    }

    public void setCarLicense_Term(String carLicense, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("carLicense", carLicense);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCarLicense_NotEqual(String carLicense) {
        setCarLicense_NotTerm(carLicense, null);
    }

    public void setCarLicense_NotEqual(String carLicense, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setCarLicense_NotTerm(carLicense, opLambda);
    }

    public void setCarLicense_NotTerm(String carLicense) {
        setCarLicense_NotTerm(carLicense, null);
    }

    public void setCarLicense_NotTerm(String carLicense, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("carLicense", carLicense));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCarLicense_Terms(Collection<String> carLicenseList) {
        setCarLicense_Terms(carLicenseList, null);
    }

    public void setCarLicense_Terms(Collection<String> carLicenseList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("carLicense", carLicenseList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCarLicense_InScope(Collection<String> carLicenseList) {
        setCarLicense_Terms(carLicenseList, null);
    }

    public void setCarLicense_InScope(Collection<String> carLicenseList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setCarLicense_Terms(carLicenseList, opLambda);
    }

    public void setCarLicense_Match(String carLicense) {
        setCarLicense_Match(carLicense, null);
    }

    public void setCarLicense_Match(String carLicense, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("carLicense", carLicense);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCarLicense_MatchPhrase(String carLicense) {
        setCarLicense_MatchPhrase(carLicense, null);
    }

    public void setCarLicense_MatchPhrase(String carLicense, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("carLicense", carLicense);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCarLicense_MatchPhrasePrefix(String carLicense) {
        setCarLicense_MatchPhrasePrefix(carLicense, null);
    }

    public void setCarLicense_MatchPhrasePrefix(String carLicense, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("carLicense", carLicense);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCarLicense_Fuzzy(String carLicense) {
        setCarLicense_Fuzzy(carLicense, null);
    }

    public void setCarLicense_Fuzzy(String carLicense, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("carLicense", carLicense);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCarLicense_Prefix(String carLicense) {
        setCarLicense_Prefix(carLicense, null);
    }

    public void setCarLicense_Prefix(String carLicense, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("carLicense", carLicense);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCarLicense_GreaterThan(String carLicense) {
        setCarLicense_GreaterThan(carLicense, null);
    }

    public void setCarLicense_GreaterThan(String carLicense, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("carLicense", ConditionKey.CK_GREATER_THAN, carLicense);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCarLicense_LessThan(String carLicense) {
        setCarLicense_LessThan(carLicense, null);
    }

    public void setCarLicense_LessThan(String carLicense, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("carLicense", ConditionKey.CK_LESS_THAN, carLicense);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCarLicense_GreaterEqual(String carLicense) {
        setCarLicense_GreaterEqual(carLicense, null);
    }

    public void setCarLicense_GreaterEqual(String carLicense, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("carLicense", ConditionKey.CK_GREATER_EQUAL, carLicense);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCarLicense_LessEqual(String carLicense) {
        setCarLicense_LessEqual(carLicense, null);
    }

    public void setCarLicense_LessEqual(String carLicense, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("carLicense", ConditionKey.CK_LESS_EQUAL, carLicense);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_CarLicense_Asc() {
        regOBA("carLicense");
        return this;
    }

    public BsUserCQ addOrderBy_CarLicense_Desc() {
        regOBD("carLicense");
        return this;
    }

    public void setMobile_Equal(String mobile) {
        setMobile_Term(mobile, null);
    }

    public void setMobile_Equal(String mobile, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setMobile_Term(mobile, opLambda);
    }

    public void setMobile_Term(String mobile) {
        setMobile_Term(mobile, null);
    }

    public void setMobile_Term(String mobile, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("mobile", mobile);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMobile_NotEqual(String mobile) {
        setMobile_NotTerm(mobile, null);
    }

    public void setMobile_NotEqual(String mobile, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setMobile_NotTerm(mobile, opLambda);
    }

    public void setMobile_NotTerm(String mobile) {
        setMobile_NotTerm(mobile, null);
    }

    public void setMobile_NotTerm(String mobile, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("mobile", mobile));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMobile_Terms(Collection<String> mobileList) {
        setMobile_Terms(mobileList, null);
    }

    public void setMobile_Terms(Collection<String> mobileList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("mobile", mobileList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMobile_InScope(Collection<String> mobileList) {
        setMobile_Terms(mobileList, null);
    }

    public void setMobile_InScope(Collection<String> mobileList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setMobile_Terms(mobileList, opLambda);
    }

    public void setMobile_Match(String mobile) {
        setMobile_Match(mobile, null);
    }

    public void setMobile_Match(String mobile, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("mobile", mobile);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMobile_MatchPhrase(String mobile) {
        setMobile_MatchPhrase(mobile, null);
    }

    public void setMobile_MatchPhrase(String mobile, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("mobile", mobile);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMobile_MatchPhrasePrefix(String mobile) {
        setMobile_MatchPhrasePrefix(mobile, null);
    }

    public void setMobile_MatchPhrasePrefix(String mobile, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("mobile", mobile);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMobile_Fuzzy(String mobile) {
        setMobile_Fuzzy(mobile, null);
    }

    public void setMobile_Fuzzy(String mobile, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("mobile", mobile);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMobile_Prefix(String mobile) {
        setMobile_Prefix(mobile, null);
    }

    public void setMobile_Prefix(String mobile, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("mobile", mobile);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMobile_GreaterThan(String mobile) {
        setMobile_GreaterThan(mobile, null);
    }

    public void setMobile_GreaterThan(String mobile, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("mobile", ConditionKey.CK_GREATER_THAN, mobile);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMobile_LessThan(String mobile) {
        setMobile_LessThan(mobile, null);
    }

    public void setMobile_LessThan(String mobile, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("mobile", ConditionKey.CK_LESS_THAN, mobile);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMobile_GreaterEqual(String mobile) {
        setMobile_GreaterEqual(mobile, null);
    }

    public void setMobile_GreaterEqual(String mobile, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("mobile", ConditionKey.CK_GREATER_EQUAL, mobile);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMobile_LessEqual(String mobile) {
        setMobile_LessEqual(mobile, null);
    }

    public void setMobile_LessEqual(String mobile, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("mobile", ConditionKey.CK_LESS_EQUAL, mobile);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_Mobile_Asc() {
        regOBA("mobile");
        return this;
    }

    public BsUserCQ addOrderBy_Mobile_Desc() {
        regOBD("mobile");
        return this;
    }

    public void setPostalAddress_Equal(String postalAddress) {
        setPostalAddress_Term(postalAddress, null);
    }

    public void setPostalAddress_Equal(String postalAddress, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setPostalAddress_Term(postalAddress, opLambda);
    }

    public void setPostalAddress_Term(String postalAddress) {
        setPostalAddress_Term(postalAddress, null);
    }

    public void setPostalAddress_Term(String postalAddress, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("postalAddress", postalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalAddress_NotEqual(String postalAddress) {
        setPostalAddress_NotTerm(postalAddress, null);
    }

    public void setPostalAddress_NotEqual(String postalAddress, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setPostalAddress_NotTerm(postalAddress, opLambda);
    }

    public void setPostalAddress_NotTerm(String postalAddress) {
        setPostalAddress_NotTerm(postalAddress, null);
    }

    public void setPostalAddress_NotTerm(String postalAddress, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("postalAddress", postalAddress));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalAddress_Terms(Collection<String> postalAddressList) {
        setPostalAddress_Terms(postalAddressList, null);
    }

    public void setPostalAddress_Terms(Collection<String> postalAddressList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("postalAddress", postalAddressList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalAddress_InScope(Collection<String> postalAddressList) {
        setPostalAddress_Terms(postalAddressList, null);
    }

    public void setPostalAddress_InScope(Collection<String> postalAddressList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setPostalAddress_Terms(postalAddressList, opLambda);
    }

    public void setPostalAddress_Match(String postalAddress) {
        setPostalAddress_Match(postalAddress, null);
    }

    public void setPostalAddress_Match(String postalAddress, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("postalAddress", postalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalAddress_MatchPhrase(String postalAddress) {
        setPostalAddress_MatchPhrase(postalAddress, null);
    }

    public void setPostalAddress_MatchPhrase(String postalAddress, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("postalAddress", postalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalAddress_MatchPhrasePrefix(String postalAddress) {
        setPostalAddress_MatchPhrasePrefix(postalAddress, null);
    }

    public void setPostalAddress_MatchPhrasePrefix(String postalAddress, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("postalAddress", postalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalAddress_Fuzzy(String postalAddress) {
        setPostalAddress_Fuzzy(postalAddress, null);
    }

    public void setPostalAddress_Fuzzy(String postalAddress, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("postalAddress", postalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalAddress_Prefix(String postalAddress) {
        setPostalAddress_Prefix(postalAddress, null);
    }

    public void setPostalAddress_Prefix(String postalAddress, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("postalAddress", postalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalAddress_GreaterThan(String postalAddress) {
        setPostalAddress_GreaterThan(postalAddress, null);
    }

    public void setPostalAddress_GreaterThan(String postalAddress, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("postalAddress", ConditionKey.CK_GREATER_THAN, postalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalAddress_LessThan(String postalAddress) {
        setPostalAddress_LessThan(postalAddress, null);
    }

    public void setPostalAddress_LessThan(String postalAddress, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("postalAddress", ConditionKey.CK_LESS_THAN, postalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalAddress_GreaterEqual(String postalAddress) {
        setPostalAddress_GreaterEqual(postalAddress, null);
    }

    public void setPostalAddress_GreaterEqual(String postalAddress, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("postalAddress", ConditionKey.CK_GREATER_EQUAL, postalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalAddress_LessEqual(String postalAddress) {
        setPostalAddress_LessEqual(postalAddress, null);
    }

    public void setPostalAddress_LessEqual(String postalAddress, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("postalAddress", ConditionKey.CK_LESS_EQUAL, postalAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_PostalAddress_Asc() {
        regOBA("postalAddress");
        return this;
    }

    public BsUserCQ addOrderBy_PostalAddress_Desc() {
        regOBD("postalAddress");
        return this;
    }

    public void setCity_Equal(String city) {
        setCity_Term(city, null);
    }

    public void setCity_Equal(String city, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setCity_Term(city, opLambda);
    }

    public void setCity_Term(String city) {
        setCity_Term(city, null);
    }

    public void setCity_Term(String city, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("city", city);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCity_NotEqual(String city) {
        setCity_NotTerm(city, null);
    }

    public void setCity_NotEqual(String city, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setCity_NotTerm(city, opLambda);
    }

    public void setCity_NotTerm(String city) {
        setCity_NotTerm(city, null);
    }

    public void setCity_NotTerm(String city, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("city", city));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCity_Terms(Collection<String> cityList) {
        setCity_Terms(cityList, null);
    }

    public void setCity_Terms(Collection<String> cityList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("city", cityList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCity_InScope(Collection<String> cityList) {
        setCity_Terms(cityList, null);
    }

    public void setCity_InScope(Collection<String> cityList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setCity_Terms(cityList, opLambda);
    }

    public void setCity_Match(String city) {
        setCity_Match(city, null);
    }

    public void setCity_Match(String city, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("city", city);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCity_MatchPhrase(String city) {
        setCity_MatchPhrase(city, null);
    }

    public void setCity_MatchPhrase(String city, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("city", city);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCity_MatchPhrasePrefix(String city) {
        setCity_MatchPhrasePrefix(city, null);
    }

    public void setCity_MatchPhrasePrefix(String city, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("city", city);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCity_Fuzzy(String city) {
        setCity_Fuzzy(city, null);
    }

    public void setCity_Fuzzy(String city, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("city", city);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCity_Prefix(String city) {
        setCity_Prefix(city, null);
    }

    public void setCity_Prefix(String city, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("city", city);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCity_GreaterThan(String city) {
        setCity_GreaterThan(city, null);
    }

    public void setCity_GreaterThan(String city, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("city", ConditionKey.CK_GREATER_THAN, city);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCity_LessThan(String city) {
        setCity_LessThan(city, null);
    }

    public void setCity_LessThan(String city, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("city", ConditionKey.CK_LESS_THAN, city);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCity_GreaterEqual(String city) {
        setCity_GreaterEqual(city, null);
    }

    public void setCity_GreaterEqual(String city, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("city", ConditionKey.CK_GREATER_EQUAL, city);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCity_LessEqual(String city) {
        setCity_LessEqual(city, null);
    }

    public void setCity_LessEqual(String city, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("city", ConditionKey.CK_LESS_EQUAL, city);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_City_Asc() {
        regOBA("city");
        return this;
    }

    public BsUserCQ addOrderBy_City_Desc() {
        regOBD("city");
        return this;
    }

    public void setTeletexTerminalIdentifier_Equal(String teletexTerminalIdentifier) {
        setTeletexTerminalIdentifier_Term(teletexTerminalIdentifier, null);
    }

    public void setTeletexTerminalIdentifier_Equal(String teletexTerminalIdentifier, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setTeletexTerminalIdentifier_Term(teletexTerminalIdentifier, opLambda);
    }

    public void setTeletexTerminalIdentifier_Term(String teletexTerminalIdentifier) {
        setTeletexTerminalIdentifier_Term(teletexTerminalIdentifier, null);
    }

    public void setTeletexTerminalIdentifier_Term(String teletexTerminalIdentifier, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("teletexTerminalIdentifier", teletexTerminalIdentifier);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTeletexTerminalIdentifier_NotEqual(String teletexTerminalIdentifier) {
        setTeletexTerminalIdentifier_NotTerm(teletexTerminalIdentifier, null);
    }

    public void setTeletexTerminalIdentifier_NotEqual(String teletexTerminalIdentifier, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setTeletexTerminalIdentifier_NotTerm(teletexTerminalIdentifier, opLambda);
    }

    public void setTeletexTerminalIdentifier_NotTerm(String teletexTerminalIdentifier) {
        setTeletexTerminalIdentifier_NotTerm(teletexTerminalIdentifier, null);
    }

    public void setTeletexTerminalIdentifier_NotTerm(String teletexTerminalIdentifier, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("teletexTerminalIdentifier", teletexTerminalIdentifier));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTeletexTerminalIdentifier_Terms(Collection<String> teletexTerminalIdentifierList) {
        setTeletexTerminalIdentifier_Terms(teletexTerminalIdentifierList, null);
    }

    public void setTeletexTerminalIdentifier_Terms(Collection<String> teletexTerminalIdentifierList,
            ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("teletexTerminalIdentifier", teletexTerminalIdentifierList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTeletexTerminalIdentifier_InScope(Collection<String> teletexTerminalIdentifierList) {
        setTeletexTerminalIdentifier_Terms(teletexTerminalIdentifierList, null);
    }

    public void setTeletexTerminalIdentifier_InScope(Collection<String> teletexTerminalIdentifierList,
            ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setTeletexTerminalIdentifier_Terms(teletexTerminalIdentifierList, opLambda);
    }

    public void setTeletexTerminalIdentifier_Match(String teletexTerminalIdentifier) {
        setTeletexTerminalIdentifier_Match(teletexTerminalIdentifier, null);
    }

    public void setTeletexTerminalIdentifier_Match(String teletexTerminalIdentifier, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("teletexTerminalIdentifier", teletexTerminalIdentifier);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTeletexTerminalIdentifier_MatchPhrase(String teletexTerminalIdentifier) {
        setTeletexTerminalIdentifier_MatchPhrase(teletexTerminalIdentifier, null);
    }

    public void setTeletexTerminalIdentifier_MatchPhrase(String teletexTerminalIdentifier, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("teletexTerminalIdentifier", teletexTerminalIdentifier);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTeletexTerminalIdentifier_MatchPhrasePrefix(String teletexTerminalIdentifier) {
        setTeletexTerminalIdentifier_MatchPhrasePrefix(teletexTerminalIdentifier, null);
    }

    public void setTeletexTerminalIdentifier_MatchPhrasePrefix(String teletexTerminalIdentifier,
            ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("teletexTerminalIdentifier", teletexTerminalIdentifier);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTeletexTerminalIdentifier_Fuzzy(String teletexTerminalIdentifier) {
        setTeletexTerminalIdentifier_Fuzzy(teletexTerminalIdentifier, null);
    }

    public void setTeletexTerminalIdentifier_Fuzzy(String teletexTerminalIdentifier, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("teletexTerminalIdentifier", teletexTerminalIdentifier);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTeletexTerminalIdentifier_Prefix(String teletexTerminalIdentifier) {
        setTeletexTerminalIdentifier_Prefix(teletexTerminalIdentifier, null);
    }

    public void setTeletexTerminalIdentifier_Prefix(String teletexTerminalIdentifier, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("teletexTerminalIdentifier", teletexTerminalIdentifier);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTeletexTerminalIdentifier_GreaterThan(String teletexTerminalIdentifier) {
        setTeletexTerminalIdentifier_GreaterThan(teletexTerminalIdentifier, null);
    }

    public void setTeletexTerminalIdentifier_GreaterThan(String teletexTerminalIdentifier, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("teletexTerminalIdentifier", ConditionKey.CK_GREATER_THAN, teletexTerminalIdentifier);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTeletexTerminalIdentifier_LessThan(String teletexTerminalIdentifier) {
        setTeletexTerminalIdentifier_LessThan(teletexTerminalIdentifier, null);
    }

    public void setTeletexTerminalIdentifier_LessThan(String teletexTerminalIdentifier, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("teletexTerminalIdentifier", ConditionKey.CK_LESS_THAN, teletexTerminalIdentifier);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTeletexTerminalIdentifier_GreaterEqual(String teletexTerminalIdentifier) {
        setTeletexTerminalIdentifier_GreaterEqual(teletexTerminalIdentifier, null);
    }

    public void setTeletexTerminalIdentifier_GreaterEqual(String teletexTerminalIdentifier, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("teletexTerminalIdentifier", ConditionKey.CK_GREATER_EQUAL, teletexTerminalIdentifier);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTeletexTerminalIdentifier_LessEqual(String teletexTerminalIdentifier) {
        setTeletexTerminalIdentifier_LessEqual(teletexTerminalIdentifier, null);
    }

    public void setTeletexTerminalIdentifier_LessEqual(String teletexTerminalIdentifier, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("teletexTerminalIdentifier", ConditionKey.CK_LESS_EQUAL, teletexTerminalIdentifier);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_TeletexTerminalIdentifier_Asc() {
        regOBA("teletexTerminalIdentifier");
        return this;
    }

    public BsUserCQ addOrderBy_TeletexTerminalIdentifier_Desc() {
        regOBD("teletexTerminalIdentifier");
        return this;
    }

    public void setX121Address_Equal(String x121Address) {
        setX121Address_Term(x121Address, null);
    }

    public void setX121Address_Equal(String x121Address, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setX121Address_Term(x121Address, opLambda);
    }

    public void setX121Address_Term(String x121Address) {
        setX121Address_Term(x121Address, null);
    }

    public void setX121Address_Term(String x121Address, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("x121Address", x121Address);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setX121Address_NotEqual(String x121Address) {
        setX121Address_NotTerm(x121Address, null);
    }

    public void setX121Address_NotEqual(String x121Address, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setX121Address_NotTerm(x121Address, opLambda);
    }

    public void setX121Address_NotTerm(String x121Address) {
        setX121Address_NotTerm(x121Address, null);
    }

    public void setX121Address_NotTerm(String x121Address, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("x121Address", x121Address));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setX121Address_Terms(Collection<String> x121AddressList) {
        setX121Address_Terms(x121AddressList, null);
    }

    public void setX121Address_Terms(Collection<String> x121AddressList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("x121Address", x121AddressList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setX121Address_InScope(Collection<String> x121AddressList) {
        setX121Address_Terms(x121AddressList, null);
    }

    public void setX121Address_InScope(Collection<String> x121AddressList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setX121Address_Terms(x121AddressList, opLambda);
    }

    public void setX121Address_Match(String x121Address) {
        setX121Address_Match(x121Address, null);
    }

    public void setX121Address_Match(String x121Address, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("x121Address", x121Address);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setX121Address_MatchPhrase(String x121Address) {
        setX121Address_MatchPhrase(x121Address, null);
    }

    public void setX121Address_MatchPhrase(String x121Address, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("x121Address", x121Address);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setX121Address_MatchPhrasePrefix(String x121Address) {
        setX121Address_MatchPhrasePrefix(x121Address, null);
    }

    public void setX121Address_MatchPhrasePrefix(String x121Address, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("x121Address", x121Address);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setX121Address_Fuzzy(String x121Address) {
        setX121Address_Fuzzy(x121Address, null);
    }

    public void setX121Address_Fuzzy(String x121Address, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("x121Address", x121Address);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setX121Address_Prefix(String x121Address) {
        setX121Address_Prefix(x121Address, null);
    }

    public void setX121Address_Prefix(String x121Address, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("x121Address", x121Address);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setX121Address_GreaterThan(String x121Address) {
        setX121Address_GreaterThan(x121Address, null);
    }

    public void setX121Address_GreaterThan(String x121Address, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("x121Address", ConditionKey.CK_GREATER_THAN, x121Address);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setX121Address_LessThan(String x121Address) {
        setX121Address_LessThan(x121Address, null);
    }

    public void setX121Address_LessThan(String x121Address, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("x121Address", ConditionKey.CK_LESS_THAN, x121Address);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setX121Address_GreaterEqual(String x121Address) {
        setX121Address_GreaterEqual(x121Address, null);
    }

    public void setX121Address_GreaterEqual(String x121Address, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("x121Address", ConditionKey.CK_GREATER_EQUAL, x121Address);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setX121Address_LessEqual(String x121Address) {
        setX121Address_LessEqual(x121Address, null);
    }

    public void setX121Address_LessEqual(String x121Address, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("x121Address", ConditionKey.CK_LESS_EQUAL, x121Address);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_X121Address_Asc() {
        regOBA("x121Address");
        return this;
    }

    public BsUserCQ addOrderBy_X121Address_Desc() {
        regOBD("x121Address");
        return this;
    }

    public void setBusinessCategory_Equal(String businessCategory) {
        setBusinessCategory_Term(businessCategory, null);
    }

    public void setBusinessCategory_Equal(String businessCategory, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setBusinessCategory_Term(businessCategory, opLambda);
    }

    public void setBusinessCategory_Term(String businessCategory) {
        setBusinessCategory_Term(businessCategory, null);
    }

    public void setBusinessCategory_Term(String businessCategory, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("businessCategory", businessCategory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBusinessCategory_NotEqual(String businessCategory) {
        setBusinessCategory_NotTerm(businessCategory, null);
    }

    public void setBusinessCategory_NotEqual(String businessCategory, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setBusinessCategory_NotTerm(businessCategory, opLambda);
    }

    public void setBusinessCategory_NotTerm(String businessCategory) {
        setBusinessCategory_NotTerm(businessCategory, null);
    }

    public void setBusinessCategory_NotTerm(String businessCategory, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("businessCategory", businessCategory));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBusinessCategory_Terms(Collection<String> businessCategoryList) {
        setBusinessCategory_Terms(businessCategoryList, null);
    }

    public void setBusinessCategory_Terms(Collection<String> businessCategoryList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("businessCategory", businessCategoryList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBusinessCategory_InScope(Collection<String> businessCategoryList) {
        setBusinessCategory_Terms(businessCategoryList, null);
    }

    public void setBusinessCategory_InScope(Collection<String> businessCategoryList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setBusinessCategory_Terms(businessCategoryList, opLambda);
    }

    public void setBusinessCategory_Match(String businessCategory) {
        setBusinessCategory_Match(businessCategory, null);
    }

    public void setBusinessCategory_Match(String businessCategory, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("businessCategory", businessCategory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBusinessCategory_MatchPhrase(String businessCategory) {
        setBusinessCategory_MatchPhrase(businessCategory, null);
    }

    public void setBusinessCategory_MatchPhrase(String businessCategory, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("businessCategory", businessCategory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBusinessCategory_MatchPhrasePrefix(String businessCategory) {
        setBusinessCategory_MatchPhrasePrefix(businessCategory, null);
    }

    public void setBusinessCategory_MatchPhrasePrefix(String businessCategory, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("businessCategory", businessCategory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBusinessCategory_Fuzzy(String businessCategory) {
        setBusinessCategory_Fuzzy(businessCategory, null);
    }

    public void setBusinessCategory_Fuzzy(String businessCategory, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("businessCategory", businessCategory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBusinessCategory_Prefix(String businessCategory) {
        setBusinessCategory_Prefix(businessCategory, null);
    }

    public void setBusinessCategory_Prefix(String businessCategory, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("businessCategory", businessCategory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBusinessCategory_GreaterThan(String businessCategory) {
        setBusinessCategory_GreaterThan(businessCategory, null);
    }

    public void setBusinessCategory_GreaterThan(String businessCategory, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("businessCategory", ConditionKey.CK_GREATER_THAN, businessCategory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBusinessCategory_LessThan(String businessCategory) {
        setBusinessCategory_LessThan(businessCategory, null);
    }

    public void setBusinessCategory_LessThan(String businessCategory, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("businessCategory", ConditionKey.CK_LESS_THAN, businessCategory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBusinessCategory_GreaterEqual(String businessCategory) {
        setBusinessCategory_GreaterEqual(businessCategory, null);
    }

    public void setBusinessCategory_GreaterEqual(String businessCategory, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("businessCategory", ConditionKey.CK_GREATER_EQUAL, businessCategory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBusinessCategory_LessEqual(String businessCategory) {
        setBusinessCategory_LessEqual(businessCategory, null);
    }

    public void setBusinessCategory_LessEqual(String businessCategory, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("businessCategory", ConditionKey.CK_LESS_EQUAL, businessCategory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_BusinessCategory_Asc() {
        regOBA("businessCategory");
        return this;
    }

    public BsUserCQ addOrderBy_BusinessCategory_Desc() {
        regOBD("businessCategory");
        return this;
    }

    public void setRegisteredAddress_Equal(String registeredAddress) {
        setRegisteredAddress_Term(registeredAddress, null);
    }

    public void setRegisteredAddress_Equal(String registeredAddress, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setRegisteredAddress_Term(registeredAddress, opLambda);
    }

    public void setRegisteredAddress_Term(String registeredAddress) {
        setRegisteredAddress_Term(registeredAddress, null);
    }

    public void setRegisteredAddress_Term(String registeredAddress, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("registeredAddress", registeredAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegisteredAddress_NotEqual(String registeredAddress) {
        setRegisteredAddress_NotTerm(registeredAddress, null);
    }

    public void setRegisteredAddress_NotEqual(String registeredAddress, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setRegisteredAddress_NotTerm(registeredAddress, opLambda);
    }

    public void setRegisteredAddress_NotTerm(String registeredAddress) {
        setRegisteredAddress_NotTerm(registeredAddress, null);
    }

    public void setRegisteredAddress_NotTerm(String registeredAddress, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("registeredAddress", registeredAddress));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegisteredAddress_Terms(Collection<String> registeredAddressList) {
        setRegisteredAddress_Terms(registeredAddressList, null);
    }

    public void setRegisteredAddress_Terms(Collection<String> registeredAddressList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("registeredAddress", registeredAddressList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegisteredAddress_InScope(Collection<String> registeredAddressList) {
        setRegisteredAddress_Terms(registeredAddressList, null);
    }

    public void setRegisteredAddress_InScope(Collection<String> registeredAddressList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setRegisteredAddress_Terms(registeredAddressList, opLambda);
    }

    public void setRegisteredAddress_Match(String registeredAddress) {
        setRegisteredAddress_Match(registeredAddress, null);
    }

    public void setRegisteredAddress_Match(String registeredAddress, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("registeredAddress", registeredAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegisteredAddress_MatchPhrase(String registeredAddress) {
        setRegisteredAddress_MatchPhrase(registeredAddress, null);
    }

    public void setRegisteredAddress_MatchPhrase(String registeredAddress, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("registeredAddress", registeredAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegisteredAddress_MatchPhrasePrefix(String registeredAddress) {
        setRegisteredAddress_MatchPhrasePrefix(registeredAddress, null);
    }

    public void setRegisteredAddress_MatchPhrasePrefix(String registeredAddress, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("registeredAddress", registeredAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegisteredAddress_Fuzzy(String registeredAddress) {
        setRegisteredAddress_Fuzzy(registeredAddress, null);
    }

    public void setRegisteredAddress_Fuzzy(String registeredAddress, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("registeredAddress", registeredAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegisteredAddress_Prefix(String registeredAddress) {
        setRegisteredAddress_Prefix(registeredAddress, null);
    }

    public void setRegisteredAddress_Prefix(String registeredAddress, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("registeredAddress", registeredAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegisteredAddress_GreaterThan(String registeredAddress) {
        setRegisteredAddress_GreaterThan(registeredAddress, null);
    }

    public void setRegisteredAddress_GreaterThan(String registeredAddress, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("registeredAddress", ConditionKey.CK_GREATER_THAN, registeredAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegisteredAddress_LessThan(String registeredAddress) {
        setRegisteredAddress_LessThan(registeredAddress, null);
    }

    public void setRegisteredAddress_LessThan(String registeredAddress, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("registeredAddress", ConditionKey.CK_LESS_THAN, registeredAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegisteredAddress_GreaterEqual(String registeredAddress) {
        setRegisteredAddress_GreaterEqual(registeredAddress, null);
    }

    public void setRegisteredAddress_GreaterEqual(String registeredAddress, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("registeredAddress", ConditionKey.CK_GREATER_EQUAL, registeredAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegisteredAddress_LessEqual(String registeredAddress) {
        setRegisteredAddress_LessEqual(registeredAddress, null);
    }

    public void setRegisteredAddress_LessEqual(String registeredAddress, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("registeredAddress", ConditionKey.CK_LESS_EQUAL, registeredAddress);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_RegisteredAddress_Asc() {
        regOBA("registeredAddress");
        return this;
    }

    public BsUserCQ addOrderBy_RegisteredAddress_Desc() {
        regOBD("registeredAddress");
        return this;
    }

    public void setDisplayName_Equal(String displayName) {
        setDisplayName_Term(displayName, null);
    }

    public void setDisplayName_Equal(String displayName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setDisplayName_Term(displayName, opLambda);
    }

    public void setDisplayName_Term(String displayName) {
        setDisplayName_Term(displayName, null);
    }

    public void setDisplayName_Term(String displayName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("displayName", displayName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDisplayName_NotEqual(String displayName) {
        setDisplayName_NotTerm(displayName, null);
    }

    public void setDisplayName_NotEqual(String displayName, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setDisplayName_NotTerm(displayName, opLambda);
    }

    public void setDisplayName_NotTerm(String displayName) {
        setDisplayName_NotTerm(displayName, null);
    }

    public void setDisplayName_NotTerm(String displayName, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("displayName", displayName));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDisplayName_Terms(Collection<String> displayNameList) {
        setDisplayName_Terms(displayNameList, null);
    }

    public void setDisplayName_Terms(Collection<String> displayNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("displayName", displayNameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDisplayName_InScope(Collection<String> displayNameList) {
        setDisplayName_Terms(displayNameList, null);
    }

    public void setDisplayName_InScope(Collection<String> displayNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setDisplayName_Terms(displayNameList, opLambda);
    }

    public void setDisplayName_Match(String displayName) {
        setDisplayName_Match(displayName, null);
    }

    public void setDisplayName_Match(String displayName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("displayName", displayName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDisplayName_MatchPhrase(String displayName) {
        setDisplayName_MatchPhrase(displayName, null);
    }

    public void setDisplayName_MatchPhrase(String displayName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("displayName", displayName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDisplayName_MatchPhrasePrefix(String displayName) {
        setDisplayName_MatchPhrasePrefix(displayName, null);
    }

    public void setDisplayName_MatchPhrasePrefix(String displayName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("displayName", displayName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDisplayName_Fuzzy(String displayName) {
        setDisplayName_Fuzzy(displayName, null);
    }

    public void setDisplayName_Fuzzy(String displayName, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("displayName", displayName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDisplayName_Prefix(String displayName) {
        setDisplayName_Prefix(displayName, null);
    }

    public void setDisplayName_Prefix(String displayName, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("displayName", displayName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDisplayName_GreaterThan(String displayName) {
        setDisplayName_GreaterThan(displayName, null);
    }

    public void setDisplayName_GreaterThan(String displayName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("displayName", ConditionKey.CK_GREATER_THAN, displayName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDisplayName_LessThan(String displayName) {
        setDisplayName_LessThan(displayName, null);
    }

    public void setDisplayName_LessThan(String displayName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("displayName", ConditionKey.CK_LESS_THAN, displayName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDisplayName_GreaterEqual(String displayName) {
        setDisplayName_GreaterEqual(displayName, null);
    }

    public void setDisplayName_GreaterEqual(String displayName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("displayName", ConditionKey.CK_GREATER_EQUAL, displayName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDisplayName_LessEqual(String displayName) {
        setDisplayName_LessEqual(displayName, null);
    }

    public void setDisplayName_LessEqual(String displayName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("displayName", ConditionKey.CK_LESS_EQUAL, displayName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_DisplayName_Asc() {
        regOBA("displayName");
        return this;
    }

    public BsUserCQ addOrderBy_DisplayName_Desc() {
        regOBD("displayName");
        return this;
    }

    public void setPreferredLanguage_Equal(String preferredLanguage) {
        setPreferredLanguage_Term(preferredLanguage, null);
    }

    public void setPreferredLanguage_Equal(String preferredLanguage, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setPreferredLanguage_Term(preferredLanguage, opLambda);
    }

    public void setPreferredLanguage_Term(String preferredLanguage) {
        setPreferredLanguage_Term(preferredLanguage, null);
    }

    public void setPreferredLanguage_Term(String preferredLanguage, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("preferredLanguage", preferredLanguage);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPreferredLanguage_NotEqual(String preferredLanguage) {
        setPreferredLanguage_NotTerm(preferredLanguage, null);
    }

    public void setPreferredLanguage_NotEqual(String preferredLanguage, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setPreferredLanguage_NotTerm(preferredLanguage, opLambda);
    }

    public void setPreferredLanguage_NotTerm(String preferredLanguage) {
        setPreferredLanguage_NotTerm(preferredLanguage, null);
    }

    public void setPreferredLanguage_NotTerm(String preferredLanguage, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("preferredLanguage", preferredLanguage));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPreferredLanguage_Terms(Collection<String> preferredLanguageList) {
        setPreferredLanguage_Terms(preferredLanguageList, null);
    }

    public void setPreferredLanguage_Terms(Collection<String> preferredLanguageList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("preferredLanguage", preferredLanguageList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPreferredLanguage_InScope(Collection<String> preferredLanguageList) {
        setPreferredLanguage_Terms(preferredLanguageList, null);
    }

    public void setPreferredLanguage_InScope(Collection<String> preferredLanguageList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setPreferredLanguage_Terms(preferredLanguageList, opLambda);
    }

    public void setPreferredLanguage_Match(String preferredLanguage) {
        setPreferredLanguage_Match(preferredLanguage, null);
    }

    public void setPreferredLanguage_Match(String preferredLanguage, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("preferredLanguage", preferredLanguage);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPreferredLanguage_MatchPhrase(String preferredLanguage) {
        setPreferredLanguage_MatchPhrase(preferredLanguage, null);
    }

    public void setPreferredLanguage_MatchPhrase(String preferredLanguage, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("preferredLanguage", preferredLanguage);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPreferredLanguage_MatchPhrasePrefix(String preferredLanguage) {
        setPreferredLanguage_MatchPhrasePrefix(preferredLanguage, null);
    }

    public void setPreferredLanguage_MatchPhrasePrefix(String preferredLanguage, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("preferredLanguage", preferredLanguage);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPreferredLanguage_Fuzzy(String preferredLanguage) {
        setPreferredLanguage_Fuzzy(preferredLanguage, null);
    }

    public void setPreferredLanguage_Fuzzy(String preferredLanguage, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("preferredLanguage", preferredLanguage);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPreferredLanguage_Prefix(String preferredLanguage) {
        setPreferredLanguage_Prefix(preferredLanguage, null);
    }

    public void setPreferredLanguage_Prefix(String preferredLanguage, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("preferredLanguage", preferredLanguage);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPreferredLanguage_GreaterThan(String preferredLanguage) {
        setPreferredLanguage_GreaterThan(preferredLanguage, null);
    }

    public void setPreferredLanguage_GreaterThan(String preferredLanguage, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("preferredLanguage", ConditionKey.CK_GREATER_THAN, preferredLanguage);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPreferredLanguage_LessThan(String preferredLanguage) {
        setPreferredLanguage_LessThan(preferredLanguage, null);
    }

    public void setPreferredLanguage_LessThan(String preferredLanguage, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("preferredLanguage", ConditionKey.CK_LESS_THAN, preferredLanguage);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPreferredLanguage_GreaterEqual(String preferredLanguage) {
        setPreferredLanguage_GreaterEqual(preferredLanguage, null);
    }

    public void setPreferredLanguage_GreaterEqual(String preferredLanguage, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("preferredLanguage", ConditionKey.CK_GREATER_EQUAL, preferredLanguage);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPreferredLanguage_LessEqual(String preferredLanguage) {
        setPreferredLanguage_LessEqual(preferredLanguage, null);
    }

    public void setPreferredLanguage_LessEqual(String preferredLanguage, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("preferredLanguage", ConditionKey.CK_LESS_EQUAL, preferredLanguage);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_PreferredLanguage_Asc() {
        regOBA("preferredLanguage");
        return this;
    }

    public BsUserCQ addOrderBy_PreferredLanguage_Desc() {
        regOBD("preferredLanguage");
        return this;
    }

    public void setDepartmentNumber_Equal(String departmentNumber) {
        setDepartmentNumber_Term(departmentNumber, null);
    }

    public void setDepartmentNumber_Equal(String departmentNumber, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setDepartmentNumber_Term(departmentNumber, opLambda);
    }

    public void setDepartmentNumber_Term(String departmentNumber) {
        setDepartmentNumber_Term(departmentNumber, null);
    }

    public void setDepartmentNumber_Term(String departmentNumber, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("departmentNumber", departmentNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepartmentNumber_NotEqual(String departmentNumber) {
        setDepartmentNumber_NotTerm(departmentNumber, null);
    }

    public void setDepartmentNumber_NotEqual(String departmentNumber, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setDepartmentNumber_NotTerm(departmentNumber, opLambda);
    }

    public void setDepartmentNumber_NotTerm(String departmentNumber) {
        setDepartmentNumber_NotTerm(departmentNumber, null);
    }

    public void setDepartmentNumber_NotTerm(String departmentNumber, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("departmentNumber", departmentNumber));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepartmentNumber_Terms(Collection<String> departmentNumberList) {
        setDepartmentNumber_Terms(departmentNumberList, null);
    }

    public void setDepartmentNumber_Terms(Collection<String> departmentNumberList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("departmentNumber", departmentNumberList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepartmentNumber_InScope(Collection<String> departmentNumberList) {
        setDepartmentNumber_Terms(departmentNumberList, null);
    }

    public void setDepartmentNumber_InScope(Collection<String> departmentNumberList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setDepartmentNumber_Terms(departmentNumberList, opLambda);
    }

    public void setDepartmentNumber_Match(String departmentNumber) {
        setDepartmentNumber_Match(departmentNumber, null);
    }

    public void setDepartmentNumber_Match(String departmentNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("departmentNumber", departmentNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepartmentNumber_MatchPhrase(String departmentNumber) {
        setDepartmentNumber_MatchPhrase(departmentNumber, null);
    }

    public void setDepartmentNumber_MatchPhrase(String departmentNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("departmentNumber", departmentNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepartmentNumber_MatchPhrasePrefix(String departmentNumber) {
        setDepartmentNumber_MatchPhrasePrefix(departmentNumber, null);
    }

    public void setDepartmentNumber_MatchPhrasePrefix(String departmentNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("departmentNumber", departmentNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepartmentNumber_Fuzzy(String departmentNumber) {
        setDepartmentNumber_Fuzzy(departmentNumber, null);
    }

    public void setDepartmentNumber_Fuzzy(String departmentNumber, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("departmentNumber", departmentNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepartmentNumber_Prefix(String departmentNumber) {
        setDepartmentNumber_Prefix(departmentNumber, null);
    }

    public void setDepartmentNumber_Prefix(String departmentNumber, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("departmentNumber", departmentNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepartmentNumber_GreaterThan(String departmentNumber) {
        setDepartmentNumber_GreaterThan(departmentNumber, null);
    }

    public void setDepartmentNumber_GreaterThan(String departmentNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("departmentNumber", ConditionKey.CK_GREATER_THAN, departmentNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepartmentNumber_LessThan(String departmentNumber) {
        setDepartmentNumber_LessThan(departmentNumber, null);
    }

    public void setDepartmentNumber_LessThan(String departmentNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("departmentNumber", ConditionKey.CK_LESS_THAN, departmentNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepartmentNumber_GreaterEqual(String departmentNumber) {
        setDepartmentNumber_GreaterEqual(departmentNumber, null);
    }

    public void setDepartmentNumber_GreaterEqual(String departmentNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("departmentNumber", ConditionKey.CK_GREATER_EQUAL, departmentNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepartmentNumber_LessEqual(String departmentNumber) {
        setDepartmentNumber_LessEqual(departmentNumber, null);
    }

    public void setDepartmentNumber_LessEqual(String departmentNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("departmentNumber", ConditionKey.CK_LESS_EQUAL, departmentNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_DepartmentNumber_Asc() {
        regOBA("departmentNumber");
        return this;
    }

    public BsUserCQ addOrderBy_DepartmentNumber_Desc() {
        regOBD("departmentNumber");
        return this;
    }

    public void setUidNumber_Equal(Long uidNumber) {
        setUidNumber_Term(uidNumber, null);
    }

    public void setUidNumber_Equal(Long uidNumber, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUidNumber_Term(uidNumber, opLambda);
    }

    public void setUidNumber_Term(Long uidNumber) {
        setUidNumber_Term(uidNumber, null);
    }

    public void setUidNumber_Term(Long uidNumber, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("uidNumber", uidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_NotEqual(Long uidNumber) {
        setUidNumber_NotTerm(uidNumber, null);
    }

    public void setUidNumber_NotEqual(Long uidNumber, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setUidNumber_NotTerm(uidNumber, opLambda);
    }

    public void setUidNumber_NotTerm(Long uidNumber) {
        setUidNumber_NotTerm(uidNumber, null);
    }

    public void setUidNumber_NotTerm(Long uidNumber, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("uidNumber", uidNumber));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_Terms(Collection<Long> uidNumberList) {
        setUidNumber_Terms(uidNumberList, null);
    }

    public void setUidNumber_Terms(Collection<Long> uidNumberList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("uidNumber", uidNumberList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_InScope(Collection<Long> uidNumberList) {
        setUidNumber_Terms(uidNumberList, null);
    }

    public void setUidNumber_InScope(Collection<Long> uidNumberList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUidNumber_Terms(uidNumberList, opLambda);
    }

    public void setUidNumber_Match(Long uidNumber) {
        setUidNumber_Match(uidNumber, null);
    }

    public void setUidNumber_Match(Long uidNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("uidNumber", uidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_MatchPhrase(Long uidNumber) {
        setUidNumber_MatchPhrase(uidNumber, null);
    }

    public void setUidNumber_MatchPhrase(Long uidNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("uidNumber", uidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_MatchPhrasePrefix(Long uidNumber) {
        setUidNumber_MatchPhrasePrefix(uidNumber, null);
    }

    public void setUidNumber_MatchPhrasePrefix(Long uidNumber, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("uidNumber", uidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_Fuzzy(Long uidNumber) {
        setUidNumber_Fuzzy(uidNumber, null);
    }

    public void setUidNumber_Fuzzy(Long uidNumber, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("uidNumber", uidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_GreaterThan(Long uidNumber) {
        setUidNumber_GreaterThan(uidNumber, null);
    }

    public void setUidNumber_GreaterThan(Long uidNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("uidNumber", ConditionKey.CK_GREATER_THAN, uidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_LessThan(Long uidNumber) {
        setUidNumber_LessThan(uidNumber, null);
    }

    public void setUidNumber_LessThan(Long uidNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("uidNumber", ConditionKey.CK_LESS_THAN, uidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_GreaterEqual(Long uidNumber) {
        setUidNumber_GreaterEqual(uidNumber, null);
    }

    public void setUidNumber_GreaterEqual(Long uidNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("uidNumber", ConditionKey.CK_GREATER_EQUAL, uidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_LessEqual(Long uidNumber) {
        setUidNumber_LessEqual(uidNumber, null);
    }

    public void setUidNumber_LessEqual(Long uidNumber, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("uidNumber", ConditionKey.CK_LESS_EQUAL, uidNumber);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_UidNumber_Asc() {
        regOBA("uidNumber");
        return this;
    }

    public BsUserCQ addOrderBy_UidNumber_Desc() {
        regOBD("uidNumber");
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

    public BsUserCQ addOrderBy_GidNumber_Asc() {
        regOBA("gidNumber");
        return this;
    }

    public BsUserCQ addOrderBy_GidNumber_Desc() {
        regOBD("gidNumber");
        return this;
    }

    public void setHomeDirectory_Equal(String homeDirectory) {
        setHomeDirectory_Term(homeDirectory, null);
    }

    public void setHomeDirectory_Equal(String homeDirectory, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setHomeDirectory_Term(homeDirectory, opLambda);
    }

    public void setHomeDirectory_Term(String homeDirectory) {
        setHomeDirectory_Term(homeDirectory, null);
    }

    public void setHomeDirectory_Term(String homeDirectory, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("homeDirectory", homeDirectory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomeDirectory_NotEqual(String homeDirectory) {
        setHomeDirectory_NotTerm(homeDirectory, null);
    }

    public void setHomeDirectory_NotEqual(String homeDirectory, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setHomeDirectory_NotTerm(homeDirectory, opLambda);
    }

    public void setHomeDirectory_NotTerm(String homeDirectory) {
        setHomeDirectory_NotTerm(homeDirectory, null);
    }

    public void setHomeDirectory_NotTerm(String homeDirectory, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("homeDirectory", homeDirectory));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomeDirectory_Terms(Collection<String> homeDirectoryList) {
        setHomeDirectory_Terms(homeDirectoryList, null);
    }

    public void setHomeDirectory_Terms(Collection<String> homeDirectoryList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("homeDirectory", homeDirectoryList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomeDirectory_InScope(Collection<String> homeDirectoryList) {
        setHomeDirectory_Terms(homeDirectoryList, null);
    }

    public void setHomeDirectory_InScope(Collection<String> homeDirectoryList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setHomeDirectory_Terms(homeDirectoryList, opLambda);
    }

    public void setHomeDirectory_Match(String homeDirectory) {
        setHomeDirectory_Match(homeDirectory, null);
    }

    public void setHomeDirectory_Match(String homeDirectory, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("homeDirectory", homeDirectory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomeDirectory_MatchPhrase(String homeDirectory) {
        setHomeDirectory_MatchPhrase(homeDirectory, null);
    }

    public void setHomeDirectory_MatchPhrase(String homeDirectory, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("homeDirectory", homeDirectory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomeDirectory_MatchPhrasePrefix(String homeDirectory) {
        setHomeDirectory_MatchPhrasePrefix(homeDirectory, null);
    }

    public void setHomeDirectory_MatchPhrasePrefix(String homeDirectory, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("homeDirectory", homeDirectory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomeDirectory_Fuzzy(String homeDirectory) {
        setHomeDirectory_Fuzzy(homeDirectory, null);
    }

    public void setHomeDirectory_Fuzzy(String homeDirectory, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("homeDirectory", homeDirectory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomeDirectory_Prefix(String homeDirectory) {
        setHomeDirectory_Prefix(homeDirectory, null);
    }

    public void setHomeDirectory_Prefix(String homeDirectory, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("homeDirectory", homeDirectory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomeDirectory_GreaterThan(String homeDirectory) {
        setHomeDirectory_GreaterThan(homeDirectory, null);
    }

    public void setHomeDirectory_GreaterThan(String homeDirectory, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("homeDirectory", ConditionKey.CK_GREATER_THAN, homeDirectory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomeDirectory_LessThan(String homeDirectory) {
        setHomeDirectory_LessThan(homeDirectory, null);
    }

    public void setHomeDirectory_LessThan(String homeDirectory, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("homeDirectory", ConditionKey.CK_LESS_THAN, homeDirectory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomeDirectory_GreaterEqual(String homeDirectory) {
        setHomeDirectory_GreaterEqual(homeDirectory, null);
    }

    public void setHomeDirectory_GreaterEqual(String homeDirectory, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("homeDirectory", ConditionKey.CK_GREATER_EQUAL, homeDirectory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomeDirectory_LessEqual(String homeDirectory) {
        setHomeDirectory_LessEqual(homeDirectory, null);
    }

    public void setHomeDirectory_LessEqual(String homeDirectory, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("homeDirectory", ConditionKey.CK_LESS_EQUAL, homeDirectory);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsUserCQ addOrderBy_HomeDirectory_Asc() {
        regOBA("homeDirectory");
        return this;
    }

    public BsUserCQ addOrderBy_HomeDirectory_Desc() {
        regOBD("homeDirectory");
        return this;
    }

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

    public void setGroups_NotEqual(String groups) {
        setGroups_NotTerm(groups, null);
    }

    public void setGroups_NotEqual(String groups, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setGroups_NotTerm(groups, opLambda);
    }

    public void setGroups_NotTerm(String groups) {
        setGroups_NotTerm(groups, null);
    }

    public void setGroups_NotTerm(String groups, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("groups", groups));
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

    public void setRoles_NotEqual(String roles) {
        setRoles_NotTerm(roles, null);
    }

    public void setRoles_NotEqual(String roles, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setRoles_NotTerm(roles, opLambda);
    }

    public void setRoles_NotTerm(String roles) {
        setRoles_NotTerm(roles, null);
    }

    public void setRoles_NotTerm(String roles, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("roles", roles));
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
