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
package org.codelibs.fess.es.config.cbean.cq.bs;

import java.time.LocalDateTime;
import java.util.Collection;

import org.codelibs.fess.es.config.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.config.cbean.cq.FileConfigCQ;
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
public abstract class BsFileConfigCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "file_config";
    }

    @Override
    public String xgetAliasName() {
        return "file_config";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void filtered(FilteredCall<FileConfigCQ, FileConfigCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<FileConfigCQ, FileConfigCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<FileConfigCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<FileConfigCQ> notLambda, ConditionOptionCall<NotQueryBuilder> opLambda) {
        FileConfigCQ notQuery = new FileConfigCQ();
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

    public void bool(BoolCall<FileConfigCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<FileConfigCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        FileConfigCQ mustQuery = new FileConfigCQ();
        FileConfigCQ shouldQuery = new FileConfigCQ();
        FileConfigCQ mustNotQuery = new FileConfigCQ();
        FileConfigCQ filterQuery = new FileConfigCQ();
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

    public BsFileConfigCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    public BsFileConfigCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setAvailable_Equal(Boolean available) {
        setAvailable_Term(available, null);
    }

    public void setAvailable_Equal(Boolean available, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setAvailable_Term(available, opLambda);
    }

    public void setAvailable_Term(Boolean available) {
        setAvailable_Term(available, null);
    }

    public void setAvailable_Term(Boolean available, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("available", available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_NotEqual(Boolean available) {
        setAvailable_NotTerm(available, null);
    }

    public void setAvailable_NotEqual(Boolean available, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setAvailable_NotTerm(available, opLambda);
    }

    public void setAvailable_NotTerm(Boolean available) {
        setAvailable_NotTerm(available, null);
    }

    public void setAvailable_NotTerm(Boolean available, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("available", available));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Terms(Collection<Boolean> availableList) {
        setAvailable_Terms(availableList, null);
    }

    public void setAvailable_Terms(Collection<Boolean> availableList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("available", availableList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_InScope(Collection<Boolean> availableList) {
        setAvailable_Terms(availableList, null);
    }

    public void setAvailable_InScope(Collection<Boolean> availableList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setAvailable_Terms(availableList, opLambda);
    }

    public void setAvailable_Match(Boolean available) {
        setAvailable_Match(available, null);
    }

    public void setAvailable_Match(Boolean available, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("available", available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_MatchPhrase(Boolean available) {
        setAvailable_MatchPhrase(available, null);
    }

    public void setAvailable_MatchPhrase(Boolean available, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("available", available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_MatchPhrasePrefix(Boolean available) {
        setAvailable_MatchPhrasePrefix(available, null);
    }

    public void setAvailable_MatchPhrasePrefix(Boolean available, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("available", available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Fuzzy(Boolean available) {
        setAvailable_Fuzzy(available, null);
    }

    public void setAvailable_Fuzzy(Boolean available, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("available", available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_GreaterThan(Boolean available) {
        setAvailable_GreaterThan(available, null);
    }

    public void setAvailable_GreaterThan(Boolean available, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("available", ConditionKey.CK_GREATER_THAN, available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_LessThan(Boolean available) {
        setAvailable_LessThan(available, null);
    }

    public void setAvailable_LessThan(Boolean available, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("available", ConditionKey.CK_LESS_THAN, available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_GreaterEqual(Boolean available) {
        setAvailable_GreaterEqual(available, null);
    }

    public void setAvailable_GreaterEqual(Boolean available, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("available", ConditionKey.CK_GREATER_EQUAL, available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_LessEqual(Boolean available) {
        setAvailable_LessEqual(available, null);
    }

    public void setAvailable_LessEqual(Boolean available, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("available", ConditionKey.CK_LESS_EQUAL, available);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFileConfigCQ addOrderBy_Available_Asc() {
        regOBA("available");
        return this;
    }

    public BsFileConfigCQ addOrderBy_Available_Desc() {
        regOBD("available");
        return this;
    }

    public void setBoost_Equal(Float boost) {
        setBoost_Term(boost, null);
    }

    public void setBoost_Equal(Float boost, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setBoost_Term(boost, opLambda);
    }

    public void setBoost_Term(Float boost) {
        setBoost_Term(boost, null);
    }

    public void setBoost_Term(Float boost, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("boost", boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_NotEqual(Float boost) {
        setBoost_NotTerm(boost, null);
    }

    public void setBoost_NotEqual(Float boost, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setBoost_NotTerm(boost, opLambda);
    }

    public void setBoost_NotTerm(Float boost) {
        setBoost_NotTerm(boost, null);
    }

    public void setBoost_NotTerm(Float boost, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("boost", boost));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Terms(Collection<Float> boostList) {
        setBoost_Terms(boostList, null);
    }

    public void setBoost_Terms(Collection<Float> boostList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("boost", boostList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_InScope(Collection<Float> boostList) {
        setBoost_Terms(boostList, null);
    }

    public void setBoost_InScope(Collection<Float> boostList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setBoost_Terms(boostList, opLambda);
    }

    public void setBoost_Match(Float boost) {
        setBoost_Match(boost, null);
    }

    public void setBoost_Match(Float boost, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("boost", boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_MatchPhrase(Float boost) {
        setBoost_MatchPhrase(boost, null);
    }

    public void setBoost_MatchPhrase(Float boost, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("boost", boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_MatchPhrasePrefix(Float boost) {
        setBoost_MatchPhrasePrefix(boost, null);
    }

    public void setBoost_MatchPhrasePrefix(Float boost, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("boost", boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Fuzzy(Float boost) {
        setBoost_Fuzzy(boost, null);
    }

    public void setBoost_Fuzzy(Float boost, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("boost", boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_GreaterThan(Float boost) {
        setBoost_GreaterThan(boost, null);
    }

    public void setBoost_GreaterThan(Float boost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("boost", ConditionKey.CK_GREATER_THAN, boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_LessThan(Float boost) {
        setBoost_LessThan(boost, null);
    }

    public void setBoost_LessThan(Float boost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("boost", ConditionKey.CK_LESS_THAN, boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_GreaterEqual(Float boost) {
        setBoost_GreaterEqual(boost, null);
    }

    public void setBoost_GreaterEqual(Float boost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("boost", ConditionKey.CK_GREATER_EQUAL, boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_LessEqual(Float boost) {
        setBoost_LessEqual(boost, null);
    }

    public void setBoost_LessEqual(Float boost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("boost", ConditionKey.CK_LESS_EQUAL, boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFileConfigCQ addOrderBy_Boost_Asc() {
        regOBA("boost");
        return this;
    }

    public BsFileConfigCQ addOrderBy_Boost_Desc() {
        regOBD("boost");
        return this;
    }

    public void setConfigParameter_Equal(String configParameter) {
        setConfigParameter_Term(configParameter, null);
    }

    public void setConfigParameter_Equal(String configParameter, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setConfigParameter_Term(configParameter, opLambda);
    }

    public void setConfigParameter_Term(String configParameter) {
        setConfigParameter_Term(configParameter, null);
    }

    public void setConfigParameter_Term(String configParameter, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("configParameter", configParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_NotEqual(String configParameter) {
        setConfigParameter_NotTerm(configParameter, null);
    }

    public void setConfigParameter_NotEqual(String configParameter, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setConfigParameter_NotTerm(configParameter, opLambda);
    }

    public void setConfigParameter_NotTerm(String configParameter) {
        setConfigParameter_NotTerm(configParameter, null);
    }

    public void setConfigParameter_NotTerm(String configParameter, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("configParameter", configParameter));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_Terms(Collection<String> configParameterList) {
        setConfigParameter_Terms(configParameterList, null);
    }

    public void setConfigParameter_Terms(Collection<String> configParameterList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("configParameter", configParameterList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_InScope(Collection<String> configParameterList) {
        setConfigParameter_Terms(configParameterList, null);
    }

    public void setConfigParameter_InScope(Collection<String> configParameterList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setConfigParameter_Terms(configParameterList, opLambda);
    }

    public void setConfigParameter_Match(String configParameter) {
        setConfigParameter_Match(configParameter, null);
    }

    public void setConfigParameter_Match(String configParameter, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("configParameter", configParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_MatchPhrase(String configParameter) {
        setConfigParameter_MatchPhrase(configParameter, null);
    }

    public void setConfigParameter_MatchPhrase(String configParameter, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("configParameter", configParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_MatchPhrasePrefix(String configParameter) {
        setConfigParameter_MatchPhrasePrefix(configParameter, null);
    }

    public void setConfigParameter_MatchPhrasePrefix(String configParameter, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("configParameter", configParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_Fuzzy(String configParameter) {
        setConfigParameter_Fuzzy(configParameter, null);
    }

    public void setConfigParameter_Fuzzy(String configParameter, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("configParameter", configParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_Prefix(String configParameter) {
        setConfigParameter_Prefix(configParameter, null);
    }

    public void setConfigParameter_Prefix(String configParameter, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("configParameter", configParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_GreaterThan(String configParameter) {
        setConfigParameter_GreaterThan(configParameter, null);
    }

    public void setConfigParameter_GreaterThan(String configParameter, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("configParameter", ConditionKey.CK_GREATER_THAN, configParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_LessThan(String configParameter) {
        setConfigParameter_LessThan(configParameter, null);
    }

    public void setConfigParameter_LessThan(String configParameter, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("configParameter", ConditionKey.CK_LESS_THAN, configParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_GreaterEqual(String configParameter) {
        setConfigParameter_GreaterEqual(configParameter, null);
    }

    public void setConfigParameter_GreaterEqual(String configParameter, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("configParameter", ConditionKey.CK_GREATER_EQUAL, configParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_LessEqual(String configParameter) {
        setConfigParameter_LessEqual(configParameter, null);
    }

    public void setConfigParameter_LessEqual(String configParameter, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("configParameter", ConditionKey.CK_LESS_EQUAL, configParameter);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFileConfigCQ addOrderBy_ConfigParameter_Asc() {
        regOBA("configParameter");
        return this;
    }

    public BsFileConfigCQ addOrderBy_ConfigParameter_Desc() {
        regOBD("configParameter");
        return this;
    }

    public void setCreatedBy_Equal(String createdBy) {
        setCreatedBy_Term(createdBy, null);
    }

    public void setCreatedBy_Equal(String createdBy, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setCreatedBy_Term(createdBy, opLambda);
    }

    public void setCreatedBy_Term(String createdBy) {
        setCreatedBy_Term(createdBy, null);
    }

    public void setCreatedBy_Term(String createdBy, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_NotEqual(String createdBy) {
        setCreatedBy_NotTerm(createdBy, null);
    }

    public void setCreatedBy_NotEqual(String createdBy, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setCreatedBy_NotTerm(createdBy, opLambda);
    }

    public void setCreatedBy_NotTerm(String createdBy) {
        setCreatedBy_NotTerm(createdBy, null);
    }

    public void setCreatedBy_NotTerm(String createdBy, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("createdBy", createdBy));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Terms(Collection<String> createdByList) {
        setCreatedBy_Terms(createdByList, null);
    }

    public void setCreatedBy_Terms(Collection<String> createdByList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("createdBy", createdByList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_InScope(Collection<String> createdByList) {
        setCreatedBy_Terms(createdByList, null);
    }

    public void setCreatedBy_InScope(Collection<String> createdByList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setCreatedBy_Terms(createdByList, opLambda);
    }

    public void setCreatedBy_Match(String createdBy) {
        setCreatedBy_Match(createdBy, null);
    }

    public void setCreatedBy_Match(String createdBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_MatchPhrase(String createdBy) {
        setCreatedBy_MatchPhrase(createdBy, null);
    }

    public void setCreatedBy_MatchPhrase(String createdBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_MatchPhrasePrefix(String createdBy) {
        setCreatedBy_MatchPhrasePrefix(createdBy, null);
    }

    public void setCreatedBy_MatchPhrasePrefix(String createdBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Fuzzy(String createdBy) {
        setCreatedBy_Fuzzy(createdBy, null);
    }

    public void setCreatedBy_Fuzzy(String createdBy, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Prefix(String createdBy) {
        setCreatedBy_Prefix(createdBy, null);
    }

    public void setCreatedBy_Prefix(String createdBy, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_GreaterThan(String createdBy) {
        setCreatedBy_GreaterThan(createdBy, null);
    }

    public void setCreatedBy_GreaterThan(String createdBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdBy", ConditionKey.CK_GREATER_THAN, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_LessThan(String createdBy) {
        setCreatedBy_LessThan(createdBy, null);
    }

    public void setCreatedBy_LessThan(String createdBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdBy", ConditionKey.CK_LESS_THAN, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_GreaterEqual(String createdBy) {
        setCreatedBy_GreaterEqual(createdBy, null);
    }

    public void setCreatedBy_GreaterEqual(String createdBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdBy", ConditionKey.CK_GREATER_EQUAL, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_LessEqual(String createdBy) {
        setCreatedBy_LessEqual(createdBy, null);
    }

    public void setCreatedBy_LessEqual(String createdBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdBy", ConditionKey.CK_LESS_EQUAL, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFileConfigCQ addOrderBy_CreatedBy_Asc() {
        regOBA("createdBy");
        return this;
    }

    public BsFileConfigCQ addOrderBy_CreatedBy_Desc() {
        regOBD("createdBy");
        return this;
    }

    public void setCreatedTime_Equal(Long createdTime) {
        setCreatedTime_Term(createdTime, null);
    }

    public void setCreatedTime_Equal(Long createdTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setCreatedTime_Term(createdTime, opLambda);
    }

    public void setCreatedTime_Term(Long createdTime) {
        setCreatedTime_Term(createdTime, null);
    }

    public void setCreatedTime_Term(Long createdTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_NotEqual(Long createdTime) {
        setCreatedTime_NotTerm(createdTime, null);
    }

    public void setCreatedTime_NotEqual(Long createdTime, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setCreatedTime_NotTerm(createdTime, opLambda);
    }

    public void setCreatedTime_NotTerm(Long createdTime) {
        setCreatedTime_NotTerm(createdTime, null);
    }

    public void setCreatedTime_NotTerm(Long createdTime, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("createdTime", createdTime));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Terms(Collection<Long> createdTimeList) {
        setCreatedTime_Terms(createdTimeList, null);
    }

    public void setCreatedTime_Terms(Collection<Long> createdTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("createdTime", createdTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_InScope(Collection<Long> createdTimeList) {
        setCreatedTime_Terms(createdTimeList, null);
    }

    public void setCreatedTime_InScope(Collection<Long> createdTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setCreatedTime_Terms(createdTimeList, opLambda);
    }

    public void setCreatedTime_Match(Long createdTime) {
        setCreatedTime_Match(createdTime, null);
    }

    public void setCreatedTime_Match(Long createdTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_MatchPhrase(Long createdTime) {
        setCreatedTime_MatchPhrase(createdTime, null);
    }

    public void setCreatedTime_MatchPhrase(Long createdTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_MatchPhrasePrefix(Long createdTime) {
        setCreatedTime_MatchPhrasePrefix(createdTime, null);
    }

    public void setCreatedTime_MatchPhrasePrefix(Long createdTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Fuzzy(Long createdTime) {
        setCreatedTime_Fuzzy(createdTime, null);
    }

    public void setCreatedTime_Fuzzy(Long createdTime, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_GreaterThan(Long createdTime) {
        setCreatedTime_GreaterThan(createdTime, null);
    }

    public void setCreatedTime_GreaterThan(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdTime", ConditionKey.CK_GREATER_THAN, createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_LessThan(Long createdTime) {
        setCreatedTime_LessThan(createdTime, null);
    }

    public void setCreatedTime_LessThan(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdTime", ConditionKey.CK_LESS_THAN, createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_GreaterEqual(Long createdTime) {
        setCreatedTime_GreaterEqual(createdTime, null);
    }

    public void setCreatedTime_GreaterEqual(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdTime", ConditionKey.CK_GREATER_EQUAL, createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_LessEqual(Long createdTime) {
        setCreatedTime_LessEqual(createdTime, null);
    }

    public void setCreatedTime_LessEqual(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("createdTime", ConditionKey.CK_LESS_EQUAL, createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFileConfigCQ addOrderBy_CreatedTime_Asc() {
        regOBA("createdTime");
        return this;
    }

    public BsFileConfigCQ addOrderBy_CreatedTime_Desc() {
        regOBD("createdTime");
        return this;
    }

    public void setDepth_Equal(Integer depth) {
        setDepth_Term(depth, null);
    }

    public void setDepth_Equal(Integer depth, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setDepth_Term(depth, opLambda);
    }

    public void setDepth_Term(Integer depth) {
        setDepth_Term(depth, null);
    }

    public void setDepth_Term(Integer depth, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("depth", depth);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_NotEqual(Integer depth) {
        setDepth_NotTerm(depth, null);
    }

    public void setDepth_NotEqual(Integer depth, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setDepth_NotTerm(depth, opLambda);
    }

    public void setDepth_NotTerm(Integer depth) {
        setDepth_NotTerm(depth, null);
    }

    public void setDepth_NotTerm(Integer depth, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("depth", depth));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Terms(Collection<Integer> depthList) {
        setDepth_Terms(depthList, null);
    }

    public void setDepth_Terms(Collection<Integer> depthList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("depth", depthList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_InScope(Collection<Integer> depthList) {
        setDepth_Terms(depthList, null);
    }

    public void setDepth_InScope(Collection<Integer> depthList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setDepth_Terms(depthList, opLambda);
    }

    public void setDepth_Match(Integer depth) {
        setDepth_Match(depth, null);
    }

    public void setDepth_Match(Integer depth, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("depth", depth);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_MatchPhrase(Integer depth) {
        setDepth_MatchPhrase(depth, null);
    }

    public void setDepth_MatchPhrase(Integer depth, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("depth", depth);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_MatchPhrasePrefix(Integer depth) {
        setDepth_MatchPhrasePrefix(depth, null);
    }

    public void setDepth_MatchPhrasePrefix(Integer depth, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("depth", depth);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Fuzzy(Integer depth) {
        setDepth_Fuzzy(depth, null);
    }

    public void setDepth_Fuzzy(Integer depth, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("depth", depth);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_GreaterThan(Integer depth) {
        setDepth_GreaterThan(depth, null);
    }

    public void setDepth_GreaterThan(Integer depth, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("depth", ConditionKey.CK_GREATER_THAN, depth);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_LessThan(Integer depth) {
        setDepth_LessThan(depth, null);
    }

    public void setDepth_LessThan(Integer depth, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("depth", ConditionKey.CK_LESS_THAN, depth);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_GreaterEqual(Integer depth) {
        setDepth_GreaterEqual(depth, null);
    }

    public void setDepth_GreaterEqual(Integer depth, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("depth", ConditionKey.CK_GREATER_EQUAL, depth);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_LessEqual(Integer depth) {
        setDepth_LessEqual(depth, null);
    }

    public void setDepth_LessEqual(Integer depth, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("depth", ConditionKey.CK_LESS_EQUAL, depth);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFileConfigCQ addOrderBy_Depth_Asc() {
        regOBA("depth");
        return this;
    }

    public BsFileConfigCQ addOrderBy_Depth_Desc() {
        regOBD("depth");
        return this;
    }

    public void setExcludedDocPaths_Equal(String excludedDocPaths) {
        setExcludedDocPaths_Term(excludedDocPaths, null);
    }

    public void setExcludedDocPaths_Equal(String excludedDocPaths, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setExcludedDocPaths_Term(excludedDocPaths, opLambda);
    }

    public void setExcludedDocPaths_Term(String excludedDocPaths) {
        setExcludedDocPaths_Term(excludedDocPaths, null);
    }

    public void setExcludedDocPaths_Term(String excludedDocPaths, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("excludedDocPaths", excludedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocPaths_NotEqual(String excludedDocPaths) {
        setExcludedDocPaths_NotTerm(excludedDocPaths, null);
    }

    public void setExcludedDocPaths_NotEqual(String excludedDocPaths, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setExcludedDocPaths_NotTerm(excludedDocPaths, opLambda);
    }

    public void setExcludedDocPaths_NotTerm(String excludedDocPaths) {
        setExcludedDocPaths_NotTerm(excludedDocPaths, null);
    }

    public void setExcludedDocPaths_NotTerm(String excludedDocPaths, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("excludedDocPaths", excludedDocPaths));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocPaths_Terms(Collection<String> excludedDocPathsList) {
        setExcludedDocPaths_Terms(excludedDocPathsList, null);
    }

    public void setExcludedDocPaths_Terms(Collection<String> excludedDocPathsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("excludedDocPaths", excludedDocPathsList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocPaths_InScope(Collection<String> excludedDocPathsList) {
        setExcludedDocPaths_Terms(excludedDocPathsList, null);
    }

    public void setExcludedDocPaths_InScope(Collection<String> excludedDocPathsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setExcludedDocPaths_Terms(excludedDocPathsList, opLambda);
    }

    public void setExcludedDocPaths_Match(String excludedDocPaths) {
        setExcludedDocPaths_Match(excludedDocPaths, null);
    }

    public void setExcludedDocPaths_Match(String excludedDocPaths, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("excludedDocPaths", excludedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocPaths_MatchPhrase(String excludedDocPaths) {
        setExcludedDocPaths_MatchPhrase(excludedDocPaths, null);
    }

    public void setExcludedDocPaths_MatchPhrase(String excludedDocPaths, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("excludedDocPaths", excludedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocPaths_MatchPhrasePrefix(String excludedDocPaths) {
        setExcludedDocPaths_MatchPhrasePrefix(excludedDocPaths, null);
    }

    public void setExcludedDocPaths_MatchPhrasePrefix(String excludedDocPaths, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("excludedDocPaths", excludedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocPaths_Fuzzy(String excludedDocPaths) {
        setExcludedDocPaths_Fuzzy(excludedDocPaths, null);
    }

    public void setExcludedDocPaths_Fuzzy(String excludedDocPaths, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("excludedDocPaths", excludedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocPaths_Prefix(String excludedDocPaths) {
        setExcludedDocPaths_Prefix(excludedDocPaths, null);
    }

    public void setExcludedDocPaths_Prefix(String excludedDocPaths, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("excludedDocPaths", excludedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocPaths_GreaterThan(String excludedDocPaths) {
        setExcludedDocPaths_GreaterThan(excludedDocPaths, null);
    }

    public void setExcludedDocPaths_GreaterThan(String excludedDocPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("excludedDocPaths", ConditionKey.CK_GREATER_THAN, excludedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocPaths_LessThan(String excludedDocPaths) {
        setExcludedDocPaths_LessThan(excludedDocPaths, null);
    }

    public void setExcludedDocPaths_LessThan(String excludedDocPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("excludedDocPaths", ConditionKey.CK_LESS_THAN, excludedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocPaths_GreaterEqual(String excludedDocPaths) {
        setExcludedDocPaths_GreaterEqual(excludedDocPaths, null);
    }

    public void setExcludedDocPaths_GreaterEqual(String excludedDocPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("excludedDocPaths", ConditionKey.CK_GREATER_EQUAL, excludedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocPaths_LessEqual(String excludedDocPaths) {
        setExcludedDocPaths_LessEqual(excludedDocPaths, null);
    }

    public void setExcludedDocPaths_LessEqual(String excludedDocPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("excludedDocPaths", ConditionKey.CK_LESS_EQUAL, excludedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFileConfigCQ addOrderBy_ExcludedDocPaths_Asc() {
        regOBA("excludedDocPaths");
        return this;
    }

    public BsFileConfigCQ addOrderBy_ExcludedDocPaths_Desc() {
        regOBD("excludedDocPaths");
        return this;
    }

    public void setExcludedPaths_Equal(String excludedPaths) {
        setExcludedPaths_Term(excludedPaths, null);
    }

    public void setExcludedPaths_Equal(String excludedPaths, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setExcludedPaths_Term(excludedPaths, opLambda);
    }

    public void setExcludedPaths_Term(String excludedPaths) {
        setExcludedPaths_Term(excludedPaths, null);
    }

    public void setExcludedPaths_Term(String excludedPaths, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("excludedPaths", excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_NotEqual(String excludedPaths) {
        setExcludedPaths_NotTerm(excludedPaths, null);
    }

    public void setExcludedPaths_NotEqual(String excludedPaths, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setExcludedPaths_NotTerm(excludedPaths, opLambda);
    }

    public void setExcludedPaths_NotTerm(String excludedPaths) {
        setExcludedPaths_NotTerm(excludedPaths, null);
    }

    public void setExcludedPaths_NotTerm(String excludedPaths, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("excludedPaths", excludedPaths));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_Terms(Collection<String> excludedPathsList) {
        setExcludedPaths_Terms(excludedPathsList, null);
    }

    public void setExcludedPaths_Terms(Collection<String> excludedPathsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("excludedPaths", excludedPathsList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_InScope(Collection<String> excludedPathsList) {
        setExcludedPaths_Terms(excludedPathsList, null);
    }

    public void setExcludedPaths_InScope(Collection<String> excludedPathsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setExcludedPaths_Terms(excludedPathsList, opLambda);
    }

    public void setExcludedPaths_Match(String excludedPaths) {
        setExcludedPaths_Match(excludedPaths, null);
    }

    public void setExcludedPaths_Match(String excludedPaths, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("excludedPaths", excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_MatchPhrase(String excludedPaths) {
        setExcludedPaths_MatchPhrase(excludedPaths, null);
    }

    public void setExcludedPaths_MatchPhrase(String excludedPaths, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("excludedPaths", excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_MatchPhrasePrefix(String excludedPaths) {
        setExcludedPaths_MatchPhrasePrefix(excludedPaths, null);
    }

    public void setExcludedPaths_MatchPhrasePrefix(String excludedPaths, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("excludedPaths", excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_Fuzzy(String excludedPaths) {
        setExcludedPaths_Fuzzy(excludedPaths, null);
    }

    public void setExcludedPaths_Fuzzy(String excludedPaths, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("excludedPaths", excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_Prefix(String excludedPaths) {
        setExcludedPaths_Prefix(excludedPaths, null);
    }

    public void setExcludedPaths_Prefix(String excludedPaths, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("excludedPaths", excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_GreaterThan(String excludedPaths) {
        setExcludedPaths_GreaterThan(excludedPaths, null);
    }

    public void setExcludedPaths_GreaterThan(String excludedPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("excludedPaths", ConditionKey.CK_GREATER_THAN, excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_LessThan(String excludedPaths) {
        setExcludedPaths_LessThan(excludedPaths, null);
    }

    public void setExcludedPaths_LessThan(String excludedPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("excludedPaths", ConditionKey.CK_LESS_THAN, excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_GreaterEqual(String excludedPaths) {
        setExcludedPaths_GreaterEqual(excludedPaths, null);
    }

    public void setExcludedPaths_GreaterEqual(String excludedPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("excludedPaths", ConditionKey.CK_GREATER_EQUAL, excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_LessEqual(String excludedPaths) {
        setExcludedPaths_LessEqual(excludedPaths, null);
    }

    public void setExcludedPaths_LessEqual(String excludedPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("excludedPaths", ConditionKey.CK_LESS_EQUAL, excludedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFileConfigCQ addOrderBy_ExcludedPaths_Asc() {
        regOBA("excludedPaths");
        return this;
    }

    public BsFileConfigCQ addOrderBy_ExcludedPaths_Desc() {
        regOBD("excludedPaths");
        return this;
    }

    public void setIncludedDocPaths_Equal(String includedDocPaths) {
        setIncludedDocPaths_Term(includedDocPaths, null);
    }

    public void setIncludedDocPaths_Equal(String includedDocPaths, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setIncludedDocPaths_Term(includedDocPaths, opLambda);
    }

    public void setIncludedDocPaths_Term(String includedDocPaths) {
        setIncludedDocPaths_Term(includedDocPaths, null);
    }

    public void setIncludedDocPaths_Term(String includedDocPaths, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("includedDocPaths", includedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocPaths_NotEqual(String includedDocPaths) {
        setIncludedDocPaths_NotTerm(includedDocPaths, null);
    }

    public void setIncludedDocPaths_NotEqual(String includedDocPaths, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setIncludedDocPaths_NotTerm(includedDocPaths, opLambda);
    }

    public void setIncludedDocPaths_NotTerm(String includedDocPaths) {
        setIncludedDocPaths_NotTerm(includedDocPaths, null);
    }

    public void setIncludedDocPaths_NotTerm(String includedDocPaths, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("includedDocPaths", includedDocPaths));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocPaths_Terms(Collection<String> includedDocPathsList) {
        setIncludedDocPaths_Terms(includedDocPathsList, null);
    }

    public void setIncludedDocPaths_Terms(Collection<String> includedDocPathsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("includedDocPaths", includedDocPathsList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocPaths_InScope(Collection<String> includedDocPathsList) {
        setIncludedDocPaths_Terms(includedDocPathsList, null);
    }

    public void setIncludedDocPaths_InScope(Collection<String> includedDocPathsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setIncludedDocPaths_Terms(includedDocPathsList, opLambda);
    }

    public void setIncludedDocPaths_Match(String includedDocPaths) {
        setIncludedDocPaths_Match(includedDocPaths, null);
    }

    public void setIncludedDocPaths_Match(String includedDocPaths, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("includedDocPaths", includedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocPaths_MatchPhrase(String includedDocPaths) {
        setIncludedDocPaths_MatchPhrase(includedDocPaths, null);
    }

    public void setIncludedDocPaths_MatchPhrase(String includedDocPaths, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("includedDocPaths", includedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocPaths_MatchPhrasePrefix(String includedDocPaths) {
        setIncludedDocPaths_MatchPhrasePrefix(includedDocPaths, null);
    }

    public void setIncludedDocPaths_MatchPhrasePrefix(String includedDocPaths, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("includedDocPaths", includedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocPaths_Fuzzy(String includedDocPaths) {
        setIncludedDocPaths_Fuzzy(includedDocPaths, null);
    }

    public void setIncludedDocPaths_Fuzzy(String includedDocPaths, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("includedDocPaths", includedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocPaths_Prefix(String includedDocPaths) {
        setIncludedDocPaths_Prefix(includedDocPaths, null);
    }

    public void setIncludedDocPaths_Prefix(String includedDocPaths, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("includedDocPaths", includedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocPaths_GreaterThan(String includedDocPaths) {
        setIncludedDocPaths_GreaterThan(includedDocPaths, null);
    }

    public void setIncludedDocPaths_GreaterThan(String includedDocPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("includedDocPaths", ConditionKey.CK_GREATER_THAN, includedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocPaths_LessThan(String includedDocPaths) {
        setIncludedDocPaths_LessThan(includedDocPaths, null);
    }

    public void setIncludedDocPaths_LessThan(String includedDocPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("includedDocPaths", ConditionKey.CK_LESS_THAN, includedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocPaths_GreaterEqual(String includedDocPaths) {
        setIncludedDocPaths_GreaterEqual(includedDocPaths, null);
    }

    public void setIncludedDocPaths_GreaterEqual(String includedDocPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("includedDocPaths", ConditionKey.CK_GREATER_EQUAL, includedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocPaths_LessEqual(String includedDocPaths) {
        setIncludedDocPaths_LessEqual(includedDocPaths, null);
    }

    public void setIncludedDocPaths_LessEqual(String includedDocPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("includedDocPaths", ConditionKey.CK_LESS_EQUAL, includedDocPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFileConfigCQ addOrderBy_IncludedDocPaths_Asc() {
        regOBA("includedDocPaths");
        return this;
    }

    public BsFileConfigCQ addOrderBy_IncludedDocPaths_Desc() {
        regOBD("includedDocPaths");
        return this;
    }

    public void setIncludedPaths_Equal(String includedPaths) {
        setIncludedPaths_Term(includedPaths, null);
    }

    public void setIncludedPaths_Equal(String includedPaths, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setIncludedPaths_Term(includedPaths, opLambda);
    }

    public void setIncludedPaths_Term(String includedPaths) {
        setIncludedPaths_Term(includedPaths, null);
    }

    public void setIncludedPaths_Term(String includedPaths, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("includedPaths", includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_NotEqual(String includedPaths) {
        setIncludedPaths_NotTerm(includedPaths, null);
    }

    public void setIncludedPaths_NotEqual(String includedPaths, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setIncludedPaths_NotTerm(includedPaths, opLambda);
    }

    public void setIncludedPaths_NotTerm(String includedPaths) {
        setIncludedPaths_NotTerm(includedPaths, null);
    }

    public void setIncludedPaths_NotTerm(String includedPaths, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("includedPaths", includedPaths));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_Terms(Collection<String> includedPathsList) {
        setIncludedPaths_Terms(includedPathsList, null);
    }

    public void setIncludedPaths_Terms(Collection<String> includedPathsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("includedPaths", includedPathsList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_InScope(Collection<String> includedPathsList) {
        setIncludedPaths_Terms(includedPathsList, null);
    }

    public void setIncludedPaths_InScope(Collection<String> includedPathsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setIncludedPaths_Terms(includedPathsList, opLambda);
    }

    public void setIncludedPaths_Match(String includedPaths) {
        setIncludedPaths_Match(includedPaths, null);
    }

    public void setIncludedPaths_Match(String includedPaths, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("includedPaths", includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_MatchPhrase(String includedPaths) {
        setIncludedPaths_MatchPhrase(includedPaths, null);
    }

    public void setIncludedPaths_MatchPhrase(String includedPaths, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("includedPaths", includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_MatchPhrasePrefix(String includedPaths) {
        setIncludedPaths_MatchPhrasePrefix(includedPaths, null);
    }

    public void setIncludedPaths_MatchPhrasePrefix(String includedPaths, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("includedPaths", includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_Fuzzy(String includedPaths) {
        setIncludedPaths_Fuzzy(includedPaths, null);
    }

    public void setIncludedPaths_Fuzzy(String includedPaths, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("includedPaths", includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_Prefix(String includedPaths) {
        setIncludedPaths_Prefix(includedPaths, null);
    }

    public void setIncludedPaths_Prefix(String includedPaths, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("includedPaths", includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_GreaterThan(String includedPaths) {
        setIncludedPaths_GreaterThan(includedPaths, null);
    }

    public void setIncludedPaths_GreaterThan(String includedPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("includedPaths", ConditionKey.CK_GREATER_THAN, includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_LessThan(String includedPaths) {
        setIncludedPaths_LessThan(includedPaths, null);
    }

    public void setIncludedPaths_LessThan(String includedPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("includedPaths", ConditionKey.CK_LESS_THAN, includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_GreaterEqual(String includedPaths) {
        setIncludedPaths_GreaterEqual(includedPaths, null);
    }

    public void setIncludedPaths_GreaterEqual(String includedPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("includedPaths", ConditionKey.CK_GREATER_EQUAL, includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_LessEqual(String includedPaths) {
        setIncludedPaths_LessEqual(includedPaths, null);
    }

    public void setIncludedPaths_LessEqual(String includedPaths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("includedPaths", ConditionKey.CK_LESS_EQUAL, includedPaths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFileConfigCQ addOrderBy_IncludedPaths_Asc() {
        regOBA("includedPaths");
        return this;
    }

    public BsFileConfigCQ addOrderBy_IncludedPaths_Desc() {
        regOBD("includedPaths");
        return this;
    }

    public void setIntervalTime_Equal(Integer intervalTime) {
        setIntervalTime_Term(intervalTime, null);
    }

    public void setIntervalTime_Equal(Integer intervalTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setIntervalTime_Term(intervalTime, opLambda);
    }

    public void setIntervalTime_Term(Integer intervalTime) {
        setIntervalTime_Term(intervalTime, null);
    }

    public void setIntervalTime_Term(Integer intervalTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("intervalTime", intervalTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_NotEqual(Integer intervalTime) {
        setIntervalTime_NotTerm(intervalTime, null);
    }

    public void setIntervalTime_NotEqual(Integer intervalTime, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setIntervalTime_NotTerm(intervalTime, opLambda);
    }

    public void setIntervalTime_NotTerm(Integer intervalTime) {
        setIntervalTime_NotTerm(intervalTime, null);
    }

    public void setIntervalTime_NotTerm(Integer intervalTime, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("intervalTime", intervalTime));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Terms(Collection<Integer> intervalTimeList) {
        setIntervalTime_Terms(intervalTimeList, null);
    }

    public void setIntervalTime_Terms(Collection<Integer> intervalTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("intervalTime", intervalTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_InScope(Collection<Integer> intervalTimeList) {
        setIntervalTime_Terms(intervalTimeList, null);
    }

    public void setIntervalTime_InScope(Collection<Integer> intervalTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setIntervalTime_Terms(intervalTimeList, opLambda);
    }

    public void setIntervalTime_Match(Integer intervalTime) {
        setIntervalTime_Match(intervalTime, null);
    }

    public void setIntervalTime_Match(Integer intervalTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("intervalTime", intervalTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_MatchPhrase(Integer intervalTime) {
        setIntervalTime_MatchPhrase(intervalTime, null);
    }

    public void setIntervalTime_MatchPhrase(Integer intervalTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("intervalTime", intervalTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_MatchPhrasePrefix(Integer intervalTime) {
        setIntervalTime_MatchPhrasePrefix(intervalTime, null);
    }

    public void setIntervalTime_MatchPhrasePrefix(Integer intervalTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("intervalTime", intervalTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Fuzzy(Integer intervalTime) {
        setIntervalTime_Fuzzy(intervalTime, null);
    }

    public void setIntervalTime_Fuzzy(Integer intervalTime, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("intervalTime", intervalTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_GreaterThan(Integer intervalTime) {
        setIntervalTime_GreaterThan(intervalTime, null);
    }

    public void setIntervalTime_GreaterThan(Integer intervalTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("intervalTime", ConditionKey.CK_GREATER_THAN, intervalTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_LessThan(Integer intervalTime) {
        setIntervalTime_LessThan(intervalTime, null);
    }

    public void setIntervalTime_LessThan(Integer intervalTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("intervalTime", ConditionKey.CK_LESS_THAN, intervalTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_GreaterEqual(Integer intervalTime) {
        setIntervalTime_GreaterEqual(intervalTime, null);
    }

    public void setIntervalTime_GreaterEqual(Integer intervalTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("intervalTime", ConditionKey.CK_GREATER_EQUAL, intervalTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_LessEqual(Integer intervalTime) {
        setIntervalTime_LessEqual(intervalTime, null);
    }

    public void setIntervalTime_LessEqual(Integer intervalTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("intervalTime", ConditionKey.CK_LESS_EQUAL, intervalTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFileConfigCQ addOrderBy_IntervalTime_Asc() {
        regOBA("intervalTime");
        return this;
    }

    public BsFileConfigCQ addOrderBy_IntervalTime_Desc() {
        regOBD("intervalTime");
        return this;
    }

    public void setTimeToLive_Equal(Integer timeToLive) {
        setTimeToLive_Term(timeToLive, null);
    }

    public void setTimeToLive_Equal(Integer timeToLive, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setTimeToLive_Term(timeToLive, opLambda);
    }

    public void setTimeToLive_Term(Integer timeToLive) {
        setTimeToLive_Term(timeToLive, null);
    }

    public void setTimeToLive_Term(Integer timeToLive, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("timeToLive", timeToLive);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_NotEqual(Integer timeToLive) {
        setTimeToLive_NotTerm(timeToLive, null);
    }

    public void setTimeToLive_NotEqual(Integer timeToLive, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setTimeToLive_NotTerm(timeToLive, opLambda);
    }

    public void setTimeToLive_NotTerm(Integer timeToLive) {
        setTimeToLive_NotTerm(timeToLive, null);
    }

    public void setTimeToLive_NotTerm(Integer timeToLive, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("timeToLive", timeToLive));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_Terms(Collection<Integer> timeToLiveList) {
        setTimeToLive_Terms(timeToLiveList, null);
    }

    public void setTimeToLive_Terms(Collection<Integer> timeToLiveList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("timeToLive", timeToLiveList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_InScope(Collection<Integer> timeToLiveList) {
        setTimeToLive_Terms(timeToLiveList, null);
    }

    public void setTimeToLive_InScope(Collection<Integer> timeToLiveList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setTimeToLive_Terms(timeToLiveList, opLambda);
    }

    public void setTimeToLive_Match(Integer timeToLive) {
        setTimeToLive_Match(timeToLive, null);
    }

    public void setTimeToLive_Match(Integer timeToLive, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("timeToLive", timeToLive);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_MatchPhrase(Integer timeToLive) {
        setTimeToLive_MatchPhrase(timeToLive, null);
    }

    public void setTimeToLive_MatchPhrase(Integer timeToLive, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("timeToLive", timeToLive);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_MatchPhrasePrefix(Integer timeToLive) {
        setTimeToLive_MatchPhrasePrefix(timeToLive, null);
    }

    public void setTimeToLive_MatchPhrasePrefix(Integer timeToLive, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("timeToLive", timeToLive);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_Fuzzy(Integer timeToLive) {
        setTimeToLive_Fuzzy(timeToLive, null);
    }

    public void setTimeToLive_Fuzzy(Integer timeToLive, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("timeToLive", timeToLive);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_GreaterThan(Integer timeToLive) {
        setTimeToLive_GreaterThan(timeToLive, null);
    }

    public void setTimeToLive_GreaterThan(Integer timeToLive, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("timeToLive", ConditionKey.CK_GREATER_THAN, timeToLive);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_LessThan(Integer timeToLive) {
        setTimeToLive_LessThan(timeToLive, null);
    }

    public void setTimeToLive_LessThan(Integer timeToLive, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("timeToLive", ConditionKey.CK_LESS_THAN, timeToLive);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_GreaterEqual(Integer timeToLive) {
        setTimeToLive_GreaterEqual(timeToLive, null);
    }

    public void setTimeToLive_GreaterEqual(Integer timeToLive, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("timeToLive", ConditionKey.CK_GREATER_EQUAL, timeToLive);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_LessEqual(Integer timeToLive) {
        setTimeToLive_LessEqual(timeToLive, null);
    }

    public void setTimeToLive_LessEqual(Integer timeToLive, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("timeToLive", ConditionKey.CK_LESS_EQUAL, timeToLive);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFileConfigCQ addOrderBy_TimeToLive_Asc() {
        regOBA("timeToLive");
        return this;
    }

    public BsFileConfigCQ addOrderBy_TimeToLive_Desc() {
        regOBD("timeToLive");
        return this;
    }

    public void setMaxAccessCount_Equal(Long maxAccessCount) {
        setMaxAccessCount_Term(maxAccessCount, null);
    }

    public void setMaxAccessCount_Equal(Long maxAccessCount, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setMaxAccessCount_Term(maxAccessCount, opLambda);
    }

    public void setMaxAccessCount_Term(Long maxAccessCount) {
        setMaxAccessCount_Term(maxAccessCount, null);
    }

    public void setMaxAccessCount_Term(Long maxAccessCount, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("maxAccessCount", maxAccessCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_NotEqual(Long maxAccessCount) {
        setMaxAccessCount_NotTerm(maxAccessCount, null);
    }

    public void setMaxAccessCount_NotEqual(Long maxAccessCount, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setMaxAccessCount_NotTerm(maxAccessCount, opLambda);
    }

    public void setMaxAccessCount_NotTerm(Long maxAccessCount) {
        setMaxAccessCount_NotTerm(maxAccessCount, null);
    }

    public void setMaxAccessCount_NotTerm(Long maxAccessCount, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("maxAccessCount", maxAccessCount));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Terms(Collection<Long> maxAccessCountList) {
        setMaxAccessCount_Terms(maxAccessCountList, null);
    }

    public void setMaxAccessCount_Terms(Collection<Long> maxAccessCountList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("maxAccessCount", maxAccessCountList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_InScope(Collection<Long> maxAccessCountList) {
        setMaxAccessCount_Terms(maxAccessCountList, null);
    }

    public void setMaxAccessCount_InScope(Collection<Long> maxAccessCountList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setMaxAccessCount_Terms(maxAccessCountList, opLambda);
    }

    public void setMaxAccessCount_Match(Long maxAccessCount) {
        setMaxAccessCount_Match(maxAccessCount, null);
    }

    public void setMaxAccessCount_Match(Long maxAccessCount, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("maxAccessCount", maxAccessCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_MatchPhrase(Long maxAccessCount) {
        setMaxAccessCount_MatchPhrase(maxAccessCount, null);
    }

    public void setMaxAccessCount_MatchPhrase(Long maxAccessCount, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("maxAccessCount", maxAccessCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_MatchPhrasePrefix(Long maxAccessCount) {
        setMaxAccessCount_MatchPhrasePrefix(maxAccessCount, null);
    }

    public void setMaxAccessCount_MatchPhrasePrefix(Long maxAccessCount, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("maxAccessCount", maxAccessCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Fuzzy(Long maxAccessCount) {
        setMaxAccessCount_Fuzzy(maxAccessCount, null);
    }

    public void setMaxAccessCount_Fuzzy(Long maxAccessCount, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("maxAccessCount", maxAccessCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_GreaterThan(Long maxAccessCount) {
        setMaxAccessCount_GreaterThan(maxAccessCount, null);
    }

    public void setMaxAccessCount_GreaterThan(Long maxAccessCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("maxAccessCount", ConditionKey.CK_GREATER_THAN, maxAccessCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_LessThan(Long maxAccessCount) {
        setMaxAccessCount_LessThan(maxAccessCount, null);
    }

    public void setMaxAccessCount_LessThan(Long maxAccessCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("maxAccessCount", ConditionKey.CK_LESS_THAN, maxAccessCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_GreaterEqual(Long maxAccessCount) {
        setMaxAccessCount_GreaterEqual(maxAccessCount, null);
    }

    public void setMaxAccessCount_GreaterEqual(Long maxAccessCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("maxAccessCount", ConditionKey.CK_GREATER_EQUAL, maxAccessCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_LessEqual(Long maxAccessCount) {
        setMaxAccessCount_LessEqual(maxAccessCount, null);
    }

    public void setMaxAccessCount_LessEqual(Long maxAccessCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("maxAccessCount", ConditionKey.CK_LESS_EQUAL, maxAccessCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFileConfigCQ addOrderBy_MaxAccessCount_Asc() {
        regOBA("maxAccessCount");
        return this;
    }

    public BsFileConfigCQ addOrderBy_MaxAccessCount_Desc() {
        regOBD("maxAccessCount");
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

    public BsFileConfigCQ addOrderBy_Name_Asc() {
        regOBA("name");
        return this;
    }

    public BsFileConfigCQ addOrderBy_Name_Desc() {
        regOBD("name");
        return this;
    }

    public void setNumOfThread_Equal(Integer numOfThread) {
        setNumOfThread_Term(numOfThread, null);
    }

    public void setNumOfThread_Equal(Integer numOfThread, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setNumOfThread_Term(numOfThread, opLambda);
    }

    public void setNumOfThread_Term(Integer numOfThread) {
        setNumOfThread_Term(numOfThread, null);
    }

    public void setNumOfThread_Term(Integer numOfThread, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("numOfThread", numOfThread);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_NotEqual(Integer numOfThread) {
        setNumOfThread_NotTerm(numOfThread, null);
    }

    public void setNumOfThread_NotEqual(Integer numOfThread, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setNumOfThread_NotTerm(numOfThread, opLambda);
    }

    public void setNumOfThread_NotTerm(Integer numOfThread) {
        setNumOfThread_NotTerm(numOfThread, null);
    }

    public void setNumOfThread_NotTerm(Integer numOfThread, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("numOfThread", numOfThread));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Terms(Collection<Integer> numOfThreadList) {
        setNumOfThread_Terms(numOfThreadList, null);
    }

    public void setNumOfThread_Terms(Collection<Integer> numOfThreadList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("numOfThread", numOfThreadList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_InScope(Collection<Integer> numOfThreadList) {
        setNumOfThread_Terms(numOfThreadList, null);
    }

    public void setNumOfThread_InScope(Collection<Integer> numOfThreadList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setNumOfThread_Terms(numOfThreadList, opLambda);
    }

    public void setNumOfThread_Match(Integer numOfThread) {
        setNumOfThread_Match(numOfThread, null);
    }

    public void setNumOfThread_Match(Integer numOfThread, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("numOfThread", numOfThread);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_MatchPhrase(Integer numOfThread) {
        setNumOfThread_MatchPhrase(numOfThread, null);
    }

    public void setNumOfThread_MatchPhrase(Integer numOfThread, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("numOfThread", numOfThread);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_MatchPhrasePrefix(Integer numOfThread) {
        setNumOfThread_MatchPhrasePrefix(numOfThread, null);
    }

    public void setNumOfThread_MatchPhrasePrefix(Integer numOfThread, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("numOfThread", numOfThread);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Fuzzy(Integer numOfThread) {
        setNumOfThread_Fuzzy(numOfThread, null);
    }

    public void setNumOfThread_Fuzzy(Integer numOfThread, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("numOfThread", numOfThread);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_GreaterThan(Integer numOfThread) {
        setNumOfThread_GreaterThan(numOfThread, null);
    }

    public void setNumOfThread_GreaterThan(Integer numOfThread, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("numOfThread", ConditionKey.CK_GREATER_THAN, numOfThread);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_LessThan(Integer numOfThread) {
        setNumOfThread_LessThan(numOfThread, null);
    }

    public void setNumOfThread_LessThan(Integer numOfThread, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("numOfThread", ConditionKey.CK_LESS_THAN, numOfThread);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_GreaterEqual(Integer numOfThread) {
        setNumOfThread_GreaterEqual(numOfThread, null);
    }

    public void setNumOfThread_GreaterEqual(Integer numOfThread, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("numOfThread", ConditionKey.CK_GREATER_EQUAL, numOfThread);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_LessEqual(Integer numOfThread) {
        setNumOfThread_LessEqual(numOfThread, null);
    }

    public void setNumOfThread_LessEqual(Integer numOfThread, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("numOfThread", ConditionKey.CK_LESS_EQUAL, numOfThread);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFileConfigCQ addOrderBy_NumOfThread_Asc() {
        regOBA("numOfThread");
        return this;
    }

    public BsFileConfigCQ addOrderBy_NumOfThread_Desc() {
        regOBD("numOfThread");
        return this;
    }

    public void setPaths_Equal(String paths) {
        setPaths_Term(paths, null);
    }

    public void setPaths_Equal(String paths, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setPaths_Term(paths, opLambda);
    }

    public void setPaths_Term(String paths) {
        setPaths_Term(paths, null);
    }

    public void setPaths_Term(String paths, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("paths", paths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPaths_NotEqual(String paths) {
        setPaths_NotTerm(paths, null);
    }

    public void setPaths_NotEqual(String paths, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setPaths_NotTerm(paths, opLambda);
    }

    public void setPaths_NotTerm(String paths) {
        setPaths_NotTerm(paths, null);
    }

    public void setPaths_NotTerm(String paths, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("paths", paths));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPaths_Terms(Collection<String> pathsList) {
        setPaths_Terms(pathsList, null);
    }

    public void setPaths_Terms(Collection<String> pathsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("paths", pathsList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPaths_InScope(Collection<String> pathsList) {
        setPaths_Terms(pathsList, null);
    }

    public void setPaths_InScope(Collection<String> pathsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setPaths_Terms(pathsList, opLambda);
    }

    public void setPaths_Match(String paths) {
        setPaths_Match(paths, null);
    }

    public void setPaths_Match(String paths, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("paths", paths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPaths_MatchPhrase(String paths) {
        setPaths_MatchPhrase(paths, null);
    }

    public void setPaths_MatchPhrase(String paths, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("paths", paths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPaths_MatchPhrasePrefix(String paths) {
        setPaths_MatchPhrasePrefix(paths, null);
    }

    public void setPaths_MatchPhrasePrefix(String paths, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("paths", paths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPaths_Fuzzy(String paths) {
        setPaths_Fuzzy(paths, null);
    }

    public void setPaths_Fuzzy(String paths, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("paths", paths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPaths_Prefix(String paths) {
        setPaths_Prefix(paths, null);
    }

    public void setPaths_Prefix(String paths, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("paths", paths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPaths_GreaterThan(String paths) {
        setPaths_GreaterThan(paths, null);
    }

    public void setPaths_GreaterThan(String paths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("paths", ConditionKey.CK_GREATER_THAN, paths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPaths_LessThan(String paths) {
        setPaths_LessThan(paths, null);
    }

    public void setPaths_LessThan(String paths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("paths", ConditionKey.CK_LESS_THAN, paths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPaths_GreaterEqual(String paths) {
        setPaths_GreaterEqual(paths, null);
    }

    public void setPaths_GreaterEqual(String paths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("paths", ConditionKey.CK_GREATER_EQUAL, paths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPaths_LessEqual(String paths) {
        setPaths_LessEqual(paths, null);
    }

    public void setPaths_LessEqual(String paths, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("paths", ConditionKey.CK_LESS_EQUAL, paths);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFileConfigCQ addOrderBy_Paths_Asc() {
        regOBA("paths");
        return this;
    }

    public BsFileConfigCQ addOrderBy_Paths_Desc() {
        regOBD("paths");
        return this;
    }

    public void setSortOrder_Equal(Integer sortOrder) {
        setSortOrder_Term(sortOrder, null);
    }

    public void setSortOrder_Equal(Integer sortOrder, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setSortOrder_Term(sortOrder, opLambda);
    }

    public void setSortOrder_Term(Integer sortOrder) {
        setSortOrder_Term(sortOrder, null);
    }

    public void setSortOrder_Term(Integer sortOrder, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("sortOrder", sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_NotEqual(Integer sortOrder) {
        setSortOrder_NotTerm(sortOrder, null);
    }

    public void setSortOrder_NotEqual(Integer sortOrder, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setSortOrder_NotTerm(sortOrder, opLambda);
    }

    public void setSortOrder_NotTerm(Integer sortOrder) {
        setSortOrder_NotTerm(sortOrder, null);
    }

    public void setSortOrder_NotTerm(Integer sortOrder, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("sortOrder", sortOrder));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Terms(Collection<Integer> sortOrderList) {
        setSortOrder_Terms(sortOrderList, null);
    }

    public void setSortOrder_Terms(Collection<Integer> sortOrderList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("sortOrder", sortOrderList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_InScope(Collection<Integer> sortOrderList) {
        setSortOrder_Terms(sortOrderList, null);
    }

    public void setSortOrder_InScope(Collection<Integer> sortOrderList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setSortOrder_Terms(sortOrderList, opLambda);
    }

    public void setSortOrder_Match(Integer sortOrder) {
        setSortOrder_Match(sortOrder, null);
    }

    public void setSortOrder_Match(Integer sortOrder, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("sortOrder", sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_MatchPhrase(Integer sortOrder) {
        setSortOrder_MatchPhrase(sortOrder, null);
    }

    public void setSortOrder_MatchPhrase(Integer sortOrder, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("sortOrder", sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_MatchPhrasePrefix(Integer sortOrder) {
        setSortOrder_MatchPhrasePrefix(sortOrder, null);
    }

    public void setSortOrder_MatchPhrasePrefix(Integer sortOrder, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("sortOrder", sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Fuzzy(Integer sortOrder) {
        setSortOrder_Fuzzy(sortOrder, null);
    }

    public void setSortOrder_Fuzzy(Integer sortOrder, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("sortOrder", sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_GreaterThan(Integer sortOrder) {
        setSortOrder_GreaterThan(sortOrder, null);
    }

    public void setSortOrder_GreaterThan(Integer sortOrder, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("sortOrder", ConditionKey.CK_GREATER_THAN, sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_LessThan(Integer sortOrder) {
        setSortOrder_LessThan(sortOrder, null);
    }

    public void setSortOrder_LessThan(Integer sortOrder, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("sortOrder", ConditionKey.CK_LESS_THAN, sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_GreaterEqual(Integer sortOrder) {
        setSortOrder_GreaterEqual(sortOrder, null);
    }

    public void setSortOrder_GreaterEqual(Integer sortOrder, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("sortOrder", ConditionKey.CK_GREATER_EQUAL, sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_LessEqual(Integer sortOrder) {
        setSortOrder_LessEqual(sortOrder, null);
    }

    public void setSortOrder_LessEqual(Integer sortOrder, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("sortOrder", ConditionKey.CK_LESS_EQUAL, sortOrder);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFileConfigCQ addOrderBy_SortOrder_Asc() {
        regOBA("sortOrder");
        return this;
    }

    public BsFileConfigCQ addOrderBy_SortOrder_Desc() {
        regOBD("sortOrder");
        return this;
    }

    public void setUpdatedBy_Equal(String updatedBy) {
        setUpdatedBy_Term(updatedBy, null);
    }

    public void setUpdatedBy_Equal(String updatedBy, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUpdatedBy_Term(updatedBy, opLambda);
    }

    public void setUpdatedBy_Term(String updatedBy) {
        setUpdatedBy_Term(updatedBy, null);
    }

    public void setUpdatedBy_Term(String updatedBy, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_NotEqual(String updatedBy) {
        setUpdatedBy_NotTerm(updatedBy, null);
    }

    public void setUpdatedBy_NotEqual(String updatedBy, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setUpdatedBy_NotTerm(updatedBy, opLambda);
    }

    public void setUpdatedBy_NotTerm(String updatedBy) {
        setUpdatedBy_NotTerm(updatedBy, null);
    }

    public void setUpdatedBy_NotTerm(String updatedBy, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("updatedBy", updatedBy));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Terms(Collection<String> updatedByList) {
        setUpdatedBy_Terms(updatedByList, null);
    }

    public void setUpdatedBy_Terms(Collection<String> updatedByList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("updatedBy", updatedByList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_InScope(Collection<String> updatedByList) {
        setUpdatedBy_Terms(updatedByList, null);
    }

    public void setUpdatedBy_InScope(Collection<String> updatedByList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUpdatedBy_Terms(updatedByList, opLambda);
    }

    public void setUpdatedBy_Match(String updatedBy) {
        setUpdatedBy_Match(updatedBy, null);
    }

    public void setUpdatedBy_Match(String updatedBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_MatchPhrase(String updatedBy) {
        setUpdatedBy_MatchPhrase(updatedBy, null);
    }

    public void setUpdatedBy_MatchPhrase(String updatedBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_MatchPhrasePrefix(String updatedBy) {
        setUpdatedBy_MatchPhrasePrefix(updatedBy, null);
    }

    public void setUpdatedBy_MatchPhrasePrefix(String updatedBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Fuzzy(String updatedBy) {
        setUpdatedBy_Fuzzy(updatedBy, null);
    }

    public void setUpdatedBy_Fuzzy(String updatedBy, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Prefix(String updatedBy) {
        setUpdatedBy_Prefix(updatedBy, null);
    }

    public void setUpdatedBy_Prefix(String updatedBy, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_GreaterThan(String updatedBy) {
        setUpdatedBy_GreaterThan(updatedBy, null);
    }

    public void setUpdatedBy_GreaterThan(String updatedBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("updatedBy", ConditionKey.CK_GREATER_THAN, updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_LessThan(String updatedBy) {
        setUpdatedBy_LessThan(updatedBy, null);
    }

    public void setUpdatedBy_LessThan(String updatedBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("updatedBy", ConditionKey.CK_LESS_THAN, updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_GreaterEqual(String updatedBy) {
        setUpdatedBy_GreaterEqual(updatedBy, null);
    }

    public void setUpdatedBy_GreaterEqual(String updatedBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("updatedBy", ConditionKey.CK_GREATER_EQUAL, updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_LessEqual(String updatedBy) {
        setUpdatedBy_LessEqual(updatedBy, null);
    }

    public void setUpdatedBy_LessEqual(String updatedBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("updatedBy", ConditionKey.CK_LESS_EQUAL, updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFileConfigCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("updatedBy");
        return this;
    }

    public BsFileConfigCQ addOrderBy_UpdatedBy_Desc() {
        regOBD("updatedBy");
        return this;
    }

    public void setUpdatedTime_Equal(Long updatedTime) {
        setUpdatedTime_Term(updatedTime, null);
    }

    public void setUpdatedTime_Equal(Long updatedTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUpdatedTime_Term(updatedTime, opLambda);
    }

    public void setUpdatedTime_Term(Long updatedTime) {
        setUpdatedTime_Term(updatedTime, null);
    }

    public void setUpdatedTime_Term(Long updatedTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_NotEqual(Long updatedTime) {
        setUpdatedTime_NotTerm(updatedTime, null);
    }

    public void setUpdatedTime_NotEqual(Long updatedTime, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setUpdatedTime_NotTerm(updatedTime, opLambda);
    }

    public void setUpdatedTime_NotTerm(Long updatedTime) {
        setUpdatedTime_NotTerm(updatedTime, null);
    }

    public void setUpdatedTime_NotTerm(Long updatedTime, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("updatedTime", updatedTime));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Terms(Collection<Long> updatedTimeList) {
        setUpdatedTime_Terms(updatedTimeList, null);
    }

    public void setUpdatedTime_Terms(Collection<Long> updatedTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("updatedTime", updatedTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_InScope(Collection<Long> updatedTimeList) {
        setUpdatedTime_Terms(updatedTimeList, null);
    }

    public void setUpdatedTime_InScope(Collection<Long> updatedTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUpdatedTime_Terms(updatedTimeList, opLambda);
    }

    public void setUpdatedTime_Match(Long updatedTime) {
        setUpdatedTime_Match(updatedTime, null);
    }

    public void setUpdatedTime_Match(Long updatedTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_MatchPhrase(Long updatedTime) {
        setUpdatedTime_MatchPhrase(updatedTime, null);
    }

    public void setUpdatedTime_MatchPhrase(Long updatedTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_MatchPhrasePrefix(Long updatedTime) {
        setUpdatedTime_MatchPhrasePrefix(updatedTime, null);
    }

    public void setUpdatedTime_MatchPhrasePrefix(Long updatedTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Fuzzy(Long updatedTime) {
        setUpdatedTime_Fuzzy(updatedTime, null);
    }

    public void setUpdatedTime_Fuzzy(Long updatedTime, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_GreaterThan(Long updatedTime) {
        setUpdatedTime_GreaterThan(updatedTime, null);
    }

    public void setUpdatedTime_GreaterThan(Long updatedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("updatedTime", ConditionKey.CK_GREATER_THAN, updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_LessThan(Long updatedTime) {
        setUpdatedTime_LessThan(updatedTime, null);
    }

    public void setUpdatedTime_LessThan(Long updatedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("updatedTime", ConditionKey.CK_LESS_THAN, updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_GreaterEqual(Long updatedTime) {
        setUpdatedTime_GreaterEqual(updatedTime, null);
    }

    public void setUpdatedTime_GreaterEqual(Long updatedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("updatedTime", ConditionKey.CK_GREATER_EQUAL, updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_LessEqual(Long updatedTime) {
        setUpdatedTime_LessEqual(updatedTime, null);
    }

    public void setUpdatedTime_LessEqual(Long updatedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("updatedTime", ConditionKey.CK_LESS_EQUAL, updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsFileConfigCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("updatedTime");
        return this;
    }

    public BsFileConfigCQ addOrderBy_UpdatedTime_Desc() {
        regOBD("updatedTime");
        return this;
    }

}
