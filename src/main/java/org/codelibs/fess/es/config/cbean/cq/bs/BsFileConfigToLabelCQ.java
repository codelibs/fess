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
import org.codelibs.fess.es.config.cbean.cq.FileConfigToLabelCQ;
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
public abstract class BsFileConfigToLabelCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "file_config_to_label";
    }

    @Override
    public String xgetAliasName() {
        return "file_config_to_label";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<FileConfigToLabelCQ> queryLambda,
            ScoreFunctionCall<ScoreFunctionCreator<FileConfigToLabelCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        FileConfigToLabelCQ cq = new FileConfigToLabelCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                FileConfigToLabelCQ cf = new FileConfigToLabelCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<FileConfigToLabelCQ, FileConfigToLabelCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<FileConfigToLabelCQ, FileConfigToLabelCQ> filteredLambda,
            ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<FileConfigToLabelCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<FileConfigToLabelCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<FileConfigToLabelCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<FileConfigToLabelCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        FileConfigToLabelCQ mustQuery = new FileConfigToLabelCQ();
        FileConfigToLabelCQ shouldQuery = new FileConfigToLabelCQ();
        FileConfigToLabelCQ mustNotQuery = new FileConfigToLabelCQ();
        FileConfigToLabelCQ filterQuery = new FileConfigToLabelCQ();
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

    public BsFileConfigToLabelCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    public BsFileConfigToLabelCQ addOrderBy_Id_Desc() {
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

    public BsFileConfigToLabelCQ addOrderBy_FileConfigId_Asc() {
        regOBA("fileConfigId");
        return this;
    }

    public BsFileConfigToLabelCQ addOrderBy_FileConfigId_Desc() {
        regOBD("fileConfigId");
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

    public BsFileConfigToLabelCQ addOrderBy_LabelTypeId_Asc() {
        regOBA("labelTypeId");
        return this;
    }

    public BsFileConfigToLabelCQ addOrderBy_LabelTypeId_Desc() {
        regOBD("labelTypeId");
        return this;
    }

}
