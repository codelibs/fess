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
import org.codelibs.fess.es.config.cbean.cq.SuggestElevateWordToLabelCQ;
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
public abstract class BsSuggestElevateWordToLabelCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "suggest_elevate_word_to_label";
    }

    @Override
    public String xgetAliasName() {
        return "suggest_elevate_word_to_label";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void filtered(FilteredCall<SuggestElevateWordToLabelCQ, SuggestElevateWordToLabelCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<SuggestElevateWordToLabelCQ, SuggestElevateWordToLabelCQ> filteredLambda,
            ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<SuggestElevateWordToLabelCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(OperatorCall<SuggestElevateWordToLabelCQ> notLambda, ConditionOptionCall<NotQueryBuilder> opLambda) {
        SuggestElevateWordToLabelCQ notQuery = new SuggestElevateWordToLabelCQ();
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

    public void bool(BoolCall<SuggestElevateWordToLabelCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<SuggestElevateWordToLabelCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        SuggestElevateWordToLabelCQ mustQuery = new SuggestElevateWordToLabelCQ();
        SuggestElevateWordToLabelCQ shouldQuery = new SuggestElevateWordToLabelCQ();
        SuggestElevateWordToLabelCQ mustNotQuery = new SuggestElevateWordToLabelCQ();
        SuggestElevateWordToLabelCQ filterQuery = new SuggestElevateWordToLabelCQ();
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

    public BsSuggestElevateWordToLabelCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    public BsSuggestElevateWordToLabelCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setSuggestElevateWordId_Equal(String suggestElevateWordId) {
        setSuggestElevateWordId_Term(suggestElevateWordId, null);
    }

    public void setSuggestElevateWordId_Equal(String suggestElevateWordId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setSuggestElevateWordId_Term(suggestElevateWordId, opLambda);
    }

    public void setSuggestElevateWordId_Term(String suggestElevateWordId) {
        setSuggestElevateWordId_Term(suggestElevateWordId, null);
    }

    public void setSuggestElevateWordId_Term(String suggestElevateWordId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("suggestElevateWordId", suggestElevateWordId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestElevateWordId_NotEqual(String suggestElevateWordId) {
        setSuggestElevateWordId_NotTerm(suggestElevateWordId, null);
    }

    public void setSuggestElevateWordId_NotEqual(String suggestElevateWordId, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setSuggestElevateWordId_NotTerm(suggestElevateWordId, opLambda);
    }

    public void setSuggestElevateWordId_NotTerm(String suggestElevateWordId) {
        setSuggestElevateWordId_NotTerm(suggestElevateWordId, null);
    }

    public void setSuggestElevateWordId_NotTerm(String suggestElevateWordId, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("suggestElevateWordId", suggestElevateWordId));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestElevateWordId_Terms(Collection<String> suggestElevateWordIdList) {
        setSuggestElevateWordId_Terms(suggestElevateWordIdList, null);
    }

    public void setSuggestElevateWordId_Terms(Collection<String> suggestElevateWordIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("suggestElevateWordId", suggestElevateWordIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestElevateWordId_InScope(Collection<String> suggestElevateWordIdList) {
        setSuggestElevateWordId_Terms(suggestElevateWordIdList, null);
    }

    public void setSuggestElevateWordId_InScope(Collection<String> suggestElevateWordIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setSuggestElevateWordId_Terms(suggestElevateWordIdList, opLambda);
    }

    public void setSuggestElevateWordId_Match(String suggestElevateWordId) {
        setSuggestElevateWordId_Match(suggestElevateWordId, null);
    }

    public void setSuggestElevateWordId_Match(String suggestElevateWordId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("suggestElevateWordId", suggestElevateWordId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestElevateWordId_MatchPhrase(String suggestElevateWordId) {
        setSuggestElevateWordId_MatchPhrase(suggestElevateWordId, null);
    }

    public void setSuggestElevateWordId_MatchPhrase(String suggestElevateWordId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("suggestElevateWordId", suggestElevateWordId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestElevateWordId_MatchPhrasePrefix(String suggestElevateWordId) {
        setSuggestElevateWordId_MatchPhrasePrefix(suggestElevateWordId, null);
    }

    public void setSuggestElevateWordId_MatchPhrasePrefix(String suggestElevateWordId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("suggestElevateWordId", suggestElevateWordId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestElevateWordId_Fuzzy(String suggestElevateWordId) {
        setSuggestElevateWordId_Fuzzy(suggestElevateWordId, null);
    }

    public void setSuggestElevateWordId_Fuzzy(String suggestElevateWordId, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("suggestElevateWordId", suggestElevateWordId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestElevateWordId_Prefix(String suggestElevateWordId) {
        setSuggestElevateWordId_Prefix(suggestElevateWordId, null);
    }

    public void setSuggestElevateWordId_Prefix(String suggestElevateWordId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("suggestElevateWordId", suggestElevateWordId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestElevateWordId_GreaterThan(String suggestElevateWordId) {
        setSuggestElevateWordId_GreaterThan(suggestElevateWordId, null);
    }

    public void setSuggestElevateWordId_GreaterThan(String suggestElevateWordId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("suggestElevateWordId", ConditionKey.CK_GREATER_THAN, suggestElevateWordId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestElevateWordId_LessThan(String suggestElevateWordId) {
        setSuggestElevateWordId_LessThan(suggestElevateWordId, null);
    }

    public void setSuggestElevateWordId_LessThan(String suggestElevateWordId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("suggestElevateWordId", ConditionKey.CK_LESS_THAN, suggestElevateWordId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestElevateWordId_GreaterEqual(String suggestElevateWordId) {
        setSuggestElevateWordId_GreaterEqual(suggestElevateWordId, null);
    }

    public void setSuggestElevateWordId_GreaterEqual(String suggestElevateWordId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("suggestElevateWordId", ConditionKey.CK_GREATER_EQUAL, suggestElevateWordId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestElevateWordId_LessEqual(String suggestElevateWordId) {
        setSuggestElevateWordId_LessEqual(suggestElevateWordId, null);
    }

    public void setSuggestElevateWordId_LessEqual(String suggestElevateWordId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("suggestElevateWordId", ConditionKey.CK_LESS_EQUAL, suggestElevateWordId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSuggestElevateWordToLabelCQ addOrderBy_SuggestElevateWordId_Asc() {
        regOBA("suggestElevateWordId");
        return this;
    }

    public BsSuggestElevateWordToLabelCQ addOrderBy_SuggestElevateWordId_Desc() {
        regOBD("suggestElevateWordId");
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

    public void setLabelTypeId_NotEqual(String labelTypeId, ConditionOptionCall<NotQueryBuilder> opLambda) {
        setLabelTypeId_NotTerm(labelTypeId, opLambda);
    }

    public void setLabelTypeId_NotTerm(String labelTypeId) {
        setLabelTypeId_NotTerm(labelTypeId, null);
    }

    public void setLabelTypeId_NotTerm(String labelTypeId, ConditionOptionCall<NotQueryBuilder> opLambda) {
        NotQueryBuilder builder = QueryBuilders.notQuery(regTermQ("labelTypeId", labelTypeId));
        if (opLambda != null) {
            opLambda.callback(builder);
        }
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

    public void setLabelTypeId_MatchPhrase(String labelTypeId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhraseQ("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_MatchPhrasePrefix(String labelTypeId) {
        setLabelTypeId_MatchPhrasePrefix(labelTypeId, null);
    }

    public void setLabelTypeId_MatchPhrasePrefix(String labelTypeId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchPhrasePrefixQ("labelTypeId", labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_Fuzzy(String labelTypeId) {
        setLabelTypeId_Fuzzy(labelTypeId, null);
    }

    public void setLabelTypeId_Fuzzy(String labelTypeId, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = regFuzzyQ("labelTypeId", labelTypeId);
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

    public void setLabelTypeId_GreaterThan(String labelTypeId) {
        setLabelTypeId_GreaterThan(labelTypeId, null);
    }

    public void setLabelTypeId_GreaterThan(String labelTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("labelTypeId", ConditionKey.CK_GREATER_THAN, labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_LessThan(String labelTypeId) {
        setLabelTypeId_LessThan(labelTypeId, null);
    }

    public void setLabelTypeId_LessThan(String labelTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("labelTypeId", ConditionKey.CK_LESS_THAN, labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_GreaterEqual(String labelTypeId) {
        setLabelTypeId_GreaterEqual(labelTypeId, null);
    }

    public void setLabelTypeId_GreaterEqual(String labelTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("labelTypeId", ConditionKey.CK_GREATER_EQUAL, labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_LessEqual(String labelTypeId) {
        setLabelTypeId_LessEqual(labelTypeId, null);
    }

    public void setLabelTypeId_LessEqual(String labelTypeId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = regRangeQ("labelTypeId", ConditionKey.CK_LESS_EQUAL, labelTypeId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSuggestElevateWordToLabelCQ addOrderBy_LabelTypeId_Asc() {
        regOBA("labelTypeId");
        return this;
    }

    public BsSuggestElevateWordToLabelCQ addOrderBy_LabelTypeId_Desc() {
        regOBD("labelTypeId");
        return this;
    }

}
