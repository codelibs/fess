/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.opensearch.log.cbean.cq.bs;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.Collection;

import org.codelibs.fess.opensearch.log.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.opensearch.log.cbean.cq.TagLogCQ;
import org.dbflute.cbean.ckey.ConditionKey;
import org.codelibs.fesen.opensearch.index.query.*;
import org.codelibs.fesen.opensearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsTagLogCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "tag_log";
    }

    @Override
    public String xgetAliasName() {
        return "tag_log";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<TagLogCQ> queryLambda, ScoreFunctionCall<ScoreFunctionCreator<TagLogCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        TagLogCQ cq = new TagLogCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                TagLogCQ cf = new TagLogCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<TagLogCQ, TagLogCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<TagLogCQ, TagLogCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<TagLogCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<TagLogCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<TagLogCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<TagLogCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        TagLogCQ mustQuery = new TagLogCQ();
        TagLogCQ shouldQuery = new TagLogCQ();
        TagLogCQ mustNotQuery = new TagLogCQ();
        TagLogCQ filterQuery = new TagLogCQ();
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
    public BsTagLogCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsTagLogCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setCreatedAt_Equal(LocalDateTime createdAt) {
        setCreatedAt_Term(createdAt, null);
    }

    public void setCreatedAt_Equal(LocalDateTime createdAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setCreatedAt_Term(createdAt, opLambda);
    }

    public void setCreatedAt_Term(LocalDateTime createdAt) {
        setCreatedAt_Term(createdAt, null);
    }

    public void setCreatedAt_Term(LocalDateTime createdAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_NotEqual(LocalDateTime createdAt) {
        setCreatedAt_NotTerm(createdAt, null);
    }

    public void setCreatedAt_NotTerm(LocalDateTime createdAt) {
        setCreatedAt_NotTerm(createdAt, null);
    }

    public void setCreatedAt_NotEqual(LocalDateTime createdAt, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setCreatedAt_NotTerm(createdAt, opLambda);
    }

    public void setCreatedAt_NotTerm(LocalDateTime createdAt, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setCreatedAt_Term(createdAt), opLambda);
    }

    public void setCreatedAt_Terms(Collection<LocalDateTime> createdAtList) {
        setCreatedAt_Terms(createdAtList, null);
    }

    public void setCreatedAt_Terms(Collection<LocalDateTime> createdAtList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("createdAt", createdAtList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_InScope(Collection<LocalDateTime> createdAtList) {
        setCreatedAt_Terms(createdAtList, null);
    }

    public void setCreatedAt_InScope(Collection<LocalDateTime> createdAtList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setCreatedAt_Terms(createdAtList, opLambda);
    }

    public void setCreatedAt_Match(LocalDateTime createdAt) {
        setCreatedAt_Match(createdAt, null);
    }

    public void setCreatedAt_Match(LocalDateTime createdAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_MatchPhrase(LocalDateTime createdAt) {
        setCreatedAt_MatchPhrase(createdAt, null);
    }

    public void setCreatedAt_MatchPhrase(LocalDateTime createdAt, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_MatchPhrasePrefix(LocalDateTime createdAt) {
        setCreatedAt_MatchPhrasePrefix(createdAt, null);
    }

    public void setCreatedAt_MatchPhrasePrefix(LocalDateTime createdAt, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Fuzzy(LocalDateTime createdAt) {
        setCreatedAt_Fuzzy(createdAt, null);
    }

    public void setCreatedAt_Fuzzy(LocalDateTime createdAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_GreaterThan(LocalDateTime createdAt) {
        setCreatedAt_GreaterThan(createdAt, null);
    }

    public void setCreatedAt_GreaterThan(LocalDateTime createdAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(createdAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("createdAt", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_LessThan(LocalDateTime createdAt) {
        setCreatedAt_LessThan(createdAt, null);
    }

    public void setCreatedAt_LessThan(LocalDateTime createdAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(createdAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("createdAt", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_GreaterEqual(LocalDateTime createdAt) {
        setCreatedAt_GreaterEqual(createdAt, null);
    }

    public void setCreatedAt_GreaterEqual(LocalDateTime createdAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(createdAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("createdAt", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_LessEqual(LocalDateTime createdAt) {
        setCreatedAt_LessEqual(createdAt, null);
    }

    public void setCreatedAt_LessEqual(LocalDateTime createdAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(createdAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("createdAt", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Exists() {
        setCreatedAt_Exists(null);
    }

    public void setCreatedAt_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setCreatedAt_CommonTerms(LocalDateTime createdAt) {
        setCreatedAt_CommonTerms(createdAt, null);
    }

    @Deprecated
    public void setCreatedAt_CommonTerms(LocalDateTime createdAt, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("createdAt", createdAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsTagLogCQ addOrderBy_CreatedAt_Asc() {
        regOBA("createdAt");
        return this;
    }

    public BsTagLogCQ addOrderBy_CreatedAt_Desc() {
        regOBD("createdAt");
        return this;
    }

    public void setDocId_Equal(String docId) {
        setDocId_Term(docId, null);
    }

    public void setDocId_Equal(String docId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setDocId_Term(docId, opLambda);
    }

    public void setDocId_Term(String docId) {
        setDocId_Term(docId, null);
    }

    public void setDocId_Term(String docId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_NotEqual(String docId) {
        setDocId_NotTerm(docId, null);
    }

    public void setDocId_NotTerm(String docId) {
        setDocId_NotTerm(docId, null);
    }

    public void setDocId_NotEqual(String docId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setDocId_NotTerm(docId, opLambda);
    }

    public void setDocId_NotTerm(String docId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setDocId_Term(docId), opLambda);
    }

    public void setDocId_Terms(Collection<String> docIdList) {
        setDocId_Terms(docIdList, null);
    }

    public void setDocId_Terms(Collection<String> docIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("docId", docIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_InScope(Collection<String> docIdList) {
        setDocId_Terms(docIdList, null);
    }

    public void setDocId_InScope(Collection<String> docIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setDocId_Terms(docIdList, opLambda);
    }

    public void setDocId_Match(String docId) {
        setDocId_Match(docId, null);
    }

    public void setDocId_Match(String docId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_MatchPhrase(String docId) {
        setDocId_MatchPhrase(docId, null);
    }

    public void setDocId_MatchPhrase(String docId, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_MatchPhrasePrefix(String docId) {
        setDocId_MatchPhrasePrefix(docId, null);
    }

    public void setDocId_MatchPhrasePrefix(String docId, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_Fuzzy(String docId) {
        setDocId_Fuzzy(docId, null);
    }

    public void setDocId_Fuzzy(String docId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_Prefix(String docId) {
        setDocId_Prefix(docId, null);
    }

    public void setDocId_Prefix(String docId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_Wildcard(String docId) {
        setDocId_Wildcard(docId, null);
    }

    public void setDocId_Wildcard(String docId, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_Regexp(String docId) {
        setDocId_Regexp(docId, null);
    }

    public void setDocId_Regexp(String docId, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_SpanTerm(String docId) {
        setDocId_SpanTerm("docId", null);
    }

    public void setDocId_SpanTerm(String docId, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_GreaterThan(String docId) {
        setDocId_GreaterThan(docId, null);
    }

    public void setDocId_GreaterThan(String docId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = docId;
        RangeQueryBuilder builder = regRangeQ("docId", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_LessThan(String docId) {
        setDocId_LessThan(docId, null);
    }

    public void setDocId_LessThan(String docId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = docId;
        RangeQueryBuilder builder = regRangeQ("docId", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_GreaterEqual(String docId) {
        setDocId_GreaterEqual(docId, null);
    }

    public void setDocId_GreaterEqual(String docId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = docId;
        RangeQueryBuilder builder = regRangeQ("docId", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_LessEqual(String docId) {
        setDocId_LessEqual(docId, null);
    }

    public void setDocId_LessEqual(String docId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = docId;
        RangeQueryBuilder builder = regRangeQ("docId", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_Exists() {
        setDocId_Exists(null);
    }

    public void setDocId_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setDocId_CommonTerms(String docId) {
        setDocId_CommonTerms(docId, null);
    }

    @Deprecated
    public void setDocId_CommonTerms(String docId, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("docId", docId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsTagLogCQ addOrderBy_DocId_Asc() {
        regOBA("docId");
        return this;
    }

    public BsTagLogCQ addOrderBy_DocId_Desc() {
        regOBD("docId");
        return this;
    }

    public void setTag_Equal(String tag) {
        setTag_Term(tag, null);
    }

    public void setTag_Equal(String tag, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setTag_Term(tag, opLambda);
    }

    public void setTag_Term(String tag) {
        setTag_Term(tag, null);
    }

    public void setTag_Term(String tag, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("tag", tag);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTag_NotEqual(String tag) {
        setTag_NotTerm(tag, null);
    }

    public void setTag_NotTerm(String tag) {
        setTag_NotTerm(tag, null);
    }

    public void setTag_NotEqual(String tag, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setTag_NotTerm(tag, opLambda);
    }

    public void setTag_NotTerm(String tag, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setTag_Term(tag), opLambda);
    }

    public void setTag_Terms(Collection<String> tagList) {
        setTag_Terms(tagList, null);
    }

    public void setTag_Terms(Collection<String> tagList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("tag", tagList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTag_InScope(Collection<String> tagList) {
        setTag_Terms(tagList, null);
    }

    public void setTag_InScope(Collection<String> tagList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setTag_Terms(tagList, opLambda);
    }

    public void setTag_Match(String tag) {
        setTag_Match(tag, null);
    }

    public void setTag_Match(String tag, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("tag", tag);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTag_MatchPhrase(String tag) {
        setTag_MatchPhrase(tag, null);
    }

    public void setTag_MatchPhrase(String tag, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("tag", tag);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTag_MatchPhrasePrefix(String tag) {
        setTag_MatchPhrasePrefix(tag, null);
    }

    public void setTag_MatchPhrasePrefix(String tag, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("tag", tag);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTag_Fuzzy(String tag) {
        setTag_Fuzzy(tag, null);
    }

    public void setTag_Fuzzy(String tag, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("tag", tag);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTag_Prefix(String tag) {
        setTag_Prefix(tag, null);
    }

    public void setTag_Prefix(String tag, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("tag", tag);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTag_Wildcard(String tag) {
        setTag_Wildcard(tag, null);
    }

    public void setTag_Wildcard(String tag, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("tag", tag);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTag_Regexp(String tag) {
        setTag_Regexp(tag, null);
    }

    public void setTag_Regexp(String tag, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("tag", tag);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTag_SpanTerm(String tag) {
        setTag_SpanTerm("tag", null);
    }

    public void setTag_SpanTerm(String tag, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("tag", tag);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTag_GreaterThan(String tag) {
        setTag_GreaterThan(tag, null);
    }

    public void setTag_GreaterThan(String tag, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = tag;
        RangeQueryBuilder builder = regRangeQ("tag", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTag_LessThan(String tag) {
        setTag_LessThan(tag, null);
    }

    public void setTag_LessThan(String tag, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = tag;
        RangeQueryBuilder builder = regRangeQ("tag", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTag_GreaterEqual(String tag) {
        setTag_GreaterEqual(tag, null);
    }

    public void setTag_GreaterEqual(String tag, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = tag;
        RangeQueryBuilder builder = regRangeQ("tag", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTag_LessEqual(String tag) {
        setTag_LessEqual(tag, null);
    }

    public void setTag_LessEqual(String tag, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = tag;
        RangeQueryBuilder builder = regRangeQ("tag", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTag_Exists() {
        setTag_Exists(null);
    }

    public void setTag_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("tag");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setTag_CommonTerms(String tag) {
        setTag_CommonTerms(tag, null);
    }

    @Deprecated
    public void setTag_CommonTerms(String tag, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("tag", tag);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsTagLogCQ addOrderBy_Tag_Asc() {
        regOBA("tag");
        return this;
    }

    public BsTagLogCQ addOrderBy_Tag_Desc() {
        regOBD("tag");
        return this;
    }

    public void setUrl_Equal(String url) {
        setUrl_Term(url, null);
    }

    public void setUrl_Equal(String url, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUrl_Term(url, opLambda);
    }

    public void setUrl_Term(String url) {
        setUrl_Term(url, null);
    }

    public void setUrl_Term(String url, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_NotEqual(String url) {
        setUrl_NotTerm(url, null);
    }

    public void setUrl_NotTerm(String url) {
        setUrl_NotTerm(url, null);
    }

    public void setUrl_NotEqual(String url, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setUrl_NotTerm(url, opLambda);
    }

    public void setUrl_NotTerm(String url, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setUrl_Term(url), opLambda);
    }

    public void setUrl_Terms(Collection<String> urlList) {
        setUrl_Terms(urlList, null);
    }

    public void setUrl_Terms(Collection<String> urlList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("url", urlList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_InScope(Collection<String> urlList) {
        setUrl_Terms(urlList, null);
    }

    public void setUrl_InScope(Collection<String> urlList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUrl_Terms(urlList, opLambda);
    }

    public void setUrl_Match(String url) {
        setUrl_Match(url, null);
    }

    public void setUrl_Match(String url, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_MatchPhrase(String url) {
        setUrl_MatchPhrase(url, null);
    }

    public void setUrl_MatchPhrase(String url, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_MatchPhrasePrefix(String url) {
        setUrl_MatchPhrasePrefix(url, null);
    }

    public void setUrl_MatchPhrasePrefix(String url, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Fuzzy(String url) {
        setUrl_Fuzzy(url, null);
    }

    public void setUrl_Fuzzy(String url, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Prefix(String url) {
        setUrl_Prefix(url, null);
    }

    public void setUrl_Prefix(String url, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Wildcard(String url) {
        setUrl_Wildcard(url, null);
    }

    public void setUrl_Wildcard(String url, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Regexp(String url) {
        setUrl_Regexp(url, null);
    }

    public void setUrl_Regexp(String url, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_SpanTerm(String url) {
        setUrl_SpanTerm("url", null);
    }

    public void setUrl_SpanTerm(String url, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_GreaterThan(String url) {
        setUrl_GreaterThan(url, null);
    }

    public void setUrl_GreaterThan(String url, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = url;
        RangeQueryBuilder builder = regRangeQ("url", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_LessThan(String url) {
        setUrl_LessThan(url, null);
    }

    public void setUrl_LessThan(String url, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = url;
        RangeQueryBuilder builder = regRangeQ("url", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_GreaterEqual(String url) {
        setUrl_GreaterEqual(url, null);
    }

    public void setUrl_GreaterEqual(String url, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = url;
        RangeQueryBuilder builder = regRangeQ("url", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_LessEqual(String url) {
        setUrl_LessEqual(url, null);
    }

    public void setUrl_LessEqual(String url, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = url;
        RangeQueryBuilder builder = regRangeQ("url", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Exists() {
        setUrl_Exists(null);
    }

    public void setUrl_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setUrl_CommonTerms(String url) {
        setUrl_CommonTerms(url, null);
    }

    @Deprecated
    public void setUrl_CommonTerms(String url, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("url", url);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsTagLogCQ addOrderBy_Url_Asc() {
        regOBA("url");
        return this;
    }

    public BsTagLogCQ addOrderBy_Url_Desc() {
        regOBD("url");
        return this;
    }

    public void setUser_Equal(String user) {
        setUser_Term(user, null);
    }

    public void setUser_Equal(String user, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUser_Term(user, opLambda);
    }

    public void setUser_Term(String user) {
        setUser_Term(user, null);
    }

    public void setUser_Term(String user, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_NotEqual(String user) {
        setUser_NotTerm(user, null);
    }

    public void setUser_NotTerm(String user) {
        setUser_NotTerm(user, null);
    }

    public void setUser_NotEqual(String user, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setUser_NotTerm(user, opLambda);
    }

    public void setUser_NotTerm(String user, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setUser_Term(user), opLambda);
    }

    public void setUser_Terms(Collection<String> userList) {
        setUser_Terms(userList, null);
    }

    public void setUser_Terms(Collection<String> userList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("user", userList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_InScope(Collection<String> userList) {
        setUser_Terms(userList, null);
    }

    public void setUser_InScope(Collection<String> userList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUser_Terms(userList, opLambda);
    }

    public void setUser_Match(String user) {
        setUser_Match(user, null);
    }

    public void setUser_Match(String user, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_MatchPhrase(String user) {
        setUser_MatchPhrase(user, null);
    }

    public void setUser_MatchPhrase(String user, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_MatchPhrasePrefix(String user) {
        setUser_MatchPhrasePrefix(user, null);
    }

    public void setUser_MatchPhrasePrefix(String user, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_Fuzzy(String user) {
        setUser_Fuzzy(user, null);
    }

    public void setUser_Fuzzy(String user, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_Prefix(String user) {
        setUser_Prefix(user, null);
    }

    public void setUser_Prefix(String user, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_Wildcard(String user) {
        setUser_Wildcard(user, null);
    }

    public void setUser_Wildcard(String user, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_Regexp(String user) {
        setUser_Regexp(user, null);
    }

    public void setUser_Regexp(String user, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_SpanTerm(String user) {
        setUser_SpanTerm("user", null);
    }

    public void setUser_SpanTerm(String user, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_GreaterThan(String user) {
        setUser_GreaterThan(user, null);
    }

    public void setUser_GreaterThan(String user, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = user;
        RangeQueryBuilder builder = regRangeQ("user", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_LessThan(String user) {
        setUser_LessThan(user, null);
    }

    public void setUser_LessThan(String user, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = user;
        RangeQueryBuilder builder = regRangeQ("user", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_GreaterEqual(String user) {
        setUser_GreaterEqual(user, null);
    }

    public void setUser_GreaterEqual(String user, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = user;
        RangeQueryBuilder builder = regRangeQ("user", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_LessEqual(String user) {
        setUser_LessEqual(user, null);
    }

    public void setUser_LessEqual(String user, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = user;
        RangeQueryBuilder builder = regRangeQ("user", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_Exists() {
        setUser_Exists(null);
    }

    public void setUser_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setUser_CommonTerms(String user) {
        setUser_CommonTerms(user, null);
    }

    @Deprecated
    public void setUser_CommonTerms(String user, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("user", user);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsTagLogCQ addOrderBy_User_Asc() {
        regOBA("user");
        return this;
    }

    public BsTagLogCQ addOrderBy_User_Desc() {
        regOBD("user");
        return this;
    }

}
