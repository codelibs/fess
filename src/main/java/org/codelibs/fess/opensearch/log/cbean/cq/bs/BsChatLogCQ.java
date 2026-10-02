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
import org.codelibs.fess.opensearch.log.cbean.cq.ChatLogCQ;
import org.dbflute.cbean.ckey.ConditionKey;
import org.codelibs.fesen.opensearch.index.query.BoolQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.CommonTermsQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.ExistsQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.IdsQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.MatchPhrasePrefixQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.MatchPhraseQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.MatchQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.PrefixQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.RangeQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.RegexpQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.SpanTermQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.TermQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.TermsQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.WildcardQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsChatLogCQ extends EsAbstractConditionQuery {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                       Name Override
    //                                                                       =============
    @Override
    public String asTableDbName() {
        return "chat_log";
    }

    @Override
    public String xgetAliasName() {
        return "chat_log";
    }

    // ===================================================================================
    //                                                                       Query Control
    //                                                                       =============
    public void functionScore(OperatorCall<ChatLogCQ> queryLambda, ScoreFunctionCall<ScoreFunctionCreator<ChatLogCQ>> functionsLambda,
            final ConditionOptionCall<FunctionScoreQueryBuilder> opLambda) {
        ChatLogCQ cq = new ChatLogCQ();
        queryLambda.callback(cq);
        final Collection<FilterFunctionBuilder> list = new ArrayList<>();
        if (functionsLambda != null) {
            functionsLambda.callback((cqLambda, scoreFunctionBuilder) -> {
                ChatLogCQ cf = new ChatLogCQ();
                cqLambda.callback(cf);
                list.add(new FilterFunctionBuilder(cf.getQuery(), scoreFunctionBuilder));
            });
        }
        final FunctionScoreQueryBuilder builder = regFunctionScoreQ(cq.getQuery(), list);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void filtered(FilteredCall<ChatLogCQ, ChatLogCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<ChatLogCQ, ChatLogCQ> filteredLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> {
            filteredLambda.callback(must, filter);
        }, opLambda);
    }

    public void not(OperatorCall<ChatLogCQ> notLambda) {
        not(notLambda, null);
    }

    public void not(final OperatorCall<ChatLogCQ> notLambda, final ConditionOptionCall<BoolQueryBuilder> opLambda) {
        bool((must, should, mustNot, filter) -> notLambda.callback(mustNot), opLambda);
    }

    public void bool(BoolCall<ChatLogCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<ChatLogCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        ChatLogCQ mustQuery = new ChatLogCQ();
        ChatLogCQ shouldQuery = new ChatLogCQ();
        ChatLogCQ mustNotQuery = new ChatLogCQ();
        ChatLogCQ filterQuery = new ChatLogCQ();
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
    public BsChatLogCQ addOrderBy_Id_Asc() {
        regOBA("_id");
        return this;
    }

    @Deprecated
    public BsChatLogCQ addOrderBy_Id_Desc() {
        regOBD("_id");
        return this;
    }

    public void setAccessType_Equal(String accessType) {
        setAccessType_Term(accessType, null);
    }

    public void setAccessType_Equal(String accessType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setAccessType_Term(accessType, opLambda);
    }

    public void setAccessType_Term(String accessType) {
        setAccessType_Term(accessType, null);
    }

    public void setAccessType_Term(String accessType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_NotEqual(String accessType) {
        setAccessType_NotTerm(accessType, null);
    }

    public void setAccessType_NotTerm(String accessType) {
        setAccessType_NotTerm(accessType, null);
    }

    public void setAccessType_NotEqual(String accessType, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setAccessType_NotTerm(accessType, opLambda);
    }

    public void setAccessType_NotTerm(String accessType, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setAccessType_Term(accessType), opLambda);
    }

    public void setAccessType_Terms(Collection<String> accessTypeList) {
        setAccessType_Terms(accessTypeList, null);
    }

    public void setAccessType_Terms(Collection<String> accessTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("accessType", accessTypeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_InScope(Collection<String> accessTypeList) {
        setAccessType_Terms(accessTypeList, null);
    }

    public void setAccessType_InScope(Collection<String> accessTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setAccessType_Terms(accessTypeList, opLambda);
    }

    public void setAccessType_Match(String accessType) {
        setAccessType_Match(accessType, null);
    }

    public void setAccessType_Match(String accessType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_MatchPhrase(String accessType) {
        setAccessType_MatchPhrase(accessType, null);
    }

    public void setAccessType_MatchPhrase(String accessType, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_MatchPhrasePrefix(String accessType) {
        setAccessType_MatchPhrasePrefix(accessType, null);
    }

    public void setAccessType_MatchPhrasePrefix(String accessType, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Fuzzy(String accessType) {
        setAccessType_Fuzzy(accessType, null);
    }

    public void setAccessType_Fuzzy(String accessType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Prefix(String accessType) {
        setAccessType_Prefix(accessType, null);
    }

    public void setAccessType_Prefix(String accessType, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Wildcard(String accessType) {
        setAccessType_Wildcard(accessType, null);
    }

    public void setAccessType_Wildcard(String accessType, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Regexp(String accessType) {
        setAccessType_Regexp(accessType, null);
    }

    public void setAccessType_Regexp(String accessType, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_SpanTerm(String accessType) {
        setAccessType_SpanTerm("accessType", null);
    }

    public void setAccessType_SpanTerm(String accessType, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_GreaterThan(String accessType) {
        setAccessType_GreaterThan(accessType, null);
    }

    public void setAccessType_GreaterThan(String accessType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = accessType;
        RangeQueryBuilder builder = regRangeQ("accessType", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_LessThan(String accessType) {
        setAccessType_LessThan(accessType, null);
    }

    public void setAccessType_LessThan(String accessType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = accessType;
        RangeQueryBuilder builder = regRangeQ("accessType", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_GreaterEqual(String accessType) {
        setAccessType_GreaterEqual(accessType, null);
    }

    public void setAccessType_GreaterEqual(String accessType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = accessType;
        RangeQueryBuilder builder = regRangeQ("accessType", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_LessEqual(String accessType) {
        setAccessType_LessEqual(accessType, null);
    }

    public void setAccessType_LessEqual(String accessType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = accessType;
        RangeQueryBuilder builder = regRangeQ("accessType", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Exists() {
        setAccessType_Exists(null);
    }

    public void setAccessType_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("accessType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setAccessType_CommonTerms(String accessType) {
        setAccessType_CommonTerms(accessType, null);
    }

    @Deprecated
    public void setAccessType_CommonTerms(String accessType, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("accessType", accessType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsChatLogCQ addOrderBy_AccessType_Asc() {
        regOBA("accessType");
        return this;
    }

    public BsChatLogCQ addOrderBy_AccessType_Desc() {
        regOBD("accessType");
        return this;
    }

    public void setChatSessionId_Equal(String chatSessionId) {
        setChatSessionId_Term(chatSessionId, null);
    }

    public void setChatSessionId_Equal(String chatSessionId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setChatSessionId_Term(chatSessionId, opLambda);
    }

    public void setChatSessionId_Term(String chatSessionId) {
        setChatSessionId_Term(chatSessionId, null);
    }

    public void setChatSessionId_Term(String chatSessionId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("chatSessionId", chatSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatSessionId_NotEqual(String chatSessionId) {
        setChatSessionId_NotTerm(chatSessionId, null);
    }

    public void setChatSessionId_NotTerm(String chatSessionId) {
        setChatSessionId_NotTerm(chatSessionId, null);
    }

    public void setChatSessionId_NotEqual(String chatSessionId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setChatSessionId_NotTerm(chatSessionId, opLambda);
    }

    public void setChatSessionId_NotTerm(String chatSessionId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setChatSessionId_Term(chatSessionId), opLambda);
    }

    public void setChatSessionId_Terms(Collection<String> chatSessionIdList) {
        setChatSessionId_Terms(chatSessionIdList, null);
    }

    public void setChatSessionId_Terms(Collection<String> chatSessionIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("chatSessionId", chatSessionIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatSessionId_InScope(Collection<String> chatSessionIdList) {
        setChatSessionId_Terms(chatSessionIdList, null);
    }

    public void setChatSessionId_InScope(Collection<String> chatSessionIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setChatSessionId_Terms(chatSessionIdList, opLambda);
    }

    public void setChatSessionId_Match(String chatSessionId) {
        setChatSessionId_Match(chatSessionId, null);
    }

    public void setChatSessionId_Match(String chatSessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("chatSessionId", chatSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatSessionId_MatchPhrase(String chatSessionId) {
        setChatSessionId_MatchPhrase(chatSessionId, null);
    }

    public void setChatSessionId_MatchPhrase(String chatSessionId, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("chatSessionId", chatSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatSessionId_MatchPhrasePrefix(String chatSessionId) {
        setChatSessionId_MatchPhrasePrefix(chatSessionId, null);
    }

    public void setChatSessionId_MatchPhrasePrefix(String chatSessionId, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("chatSessionId", chatSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatSessionId_Fuzzy(String chatSessionId) {
        setChatSessionId_Fuzzy(chatSessionId, null);
    }

    public void setChatSessionId_Fuzzy(String chatSessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("chatSessionId", chatSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatSessionId_Prefix(String chatSessionId) {
        setChatSessionId_Prefix(chatSessionId, null);
    }

    public void setChatSessionId_Prefix(String chatSessionId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("chatSessionId", chatSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatSessionId_Wildcard(String chatSessionId) {
        setChatSessionId_Wildcard(chatSessionId, null);
    }

    public void setChatSessionId_Wildcard(String chatSessionId, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("chatSessionId", chatSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatSessionId_Regexp(String chatSessionId) {
        setChatSessionId_Regexp(chatSessionId, null);
    }

    public void setChatSessionId_Regexp(String chatSessionId, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("chatSessionId", chatSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatSessionId_SpanTerm(String chatSessionId) {
        setChatSessionId_SpanTerm("chatSessionId", null);
    }

    public void setChatSessionId_SpanTerm(String chatSessionId, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("chatSessionId", chatSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatSessionId_GreaterThan(String chatSessionId) {
        setChatSessionId_GreaterThan(chatSessionId, null);
    }

    public void setChatSessionId_GreaterThan(String chatSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = chatSessionId;
        RangeQueryBuilder builder = regRangeQ("chatSessionId", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatSessionId_LessThan(String chatSessionId) {
        setChatSessionId_LessThan(chatSessionId, null);
    }

    public void setChatSessionId_LessThan(String chatSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = chatSessionId;
        RangeQueryBuilder builder = regRangeQ("chatSessionId", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatSessionId_GreaterEqual(String chatSessionId) {
        setChatSessionId_GreaterEqual(chatSessionId, null);
    }

    public void setChatSessionId_GreaterEqual(String chatSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = chatSessionId;
        RangeQueryBuilder builder = regRangeQ("chatSessionId", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatSessionId_LessEqual(String chatSessionId) {
        setChatSessionId_LessEqual(chatSessionId, null);
    }

    public void setChatSessionId_LessEqual(String chatSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = chatSessionId;
        RangeQueryBuilder builder = regRangeQ("chatSessionId", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatSessionId_Exists() {
        setChatSessionId_Exists(null);
    }

    public void setChatSessionId_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("chatSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setChatSessionId_CommonTerms(String chatSessionId) {
        setChatSessionId_CommonTerms(chatSessionId, null);
    }

    @Deprecated
    public void setChatSessionId_CommonTerms(String chatSessionId, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("chatSessionId", chatSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsChatLogCQ addOrderBy_ChatSessionId_Asc() {
        regOBA("chatSessionId");
        return this;
    }

    public BsChatLogCQ addOrderBy_ChatSessionId_Desc() {
        regOBD("chatSessionId");
        return this;
    }

    public void setChatType_Equal(String chatType) {
        setChatType_Term(chatType, null);
    }

    public void setChatType_Equal(String chatType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setChatType_Term(chatType, opLambda);
    }

    public void setChatType_Term(String chatType) {
        setChatType_Term(chatType, null);
    }

    public void setChatType_Term(String chatType, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("chatType", chatType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatType_NotEqual(String chatType) {
        setChatType_NotTerm(chatType, null);
    }

    public void setChatType_NotTerm(String chatType) {
        setChatType_NotTerm(chatType, null);
    }

    public void setChatType_NotEqual(String chatType, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setChatType_NotTerm(chatType, opLambda);
    }

    public void setChatType_NotTerm(String chatType, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setChatType_Term(chatType), opLambda);
    }

    public void setChatType_Terms(Collection<String> chatTypeList) {
        setChatType_Terms(chatTypeList, null);
    }

    public void setChatType_Terms(Collection<String> chatTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("chatType", chatTypeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatType_InScope(Collection<String> chatTypeList) {
        setChatType_Terms(chatTypeList, null);
    }

    public void setChatType_InScope(Collection<String> chatTypeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setChatType_Terms(chatTypeList, opLambda);
    }

    public void setChatType_Match(String chatType) {
        setChatType_Match(chatType, null);
    }

    public void setChatType_Match(String chatType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("chatType", chatType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatType_MatchPhrase(String chatType) {
        setChatType_MatchPhrase(chatType, null);
    }

    public void setChatType_MatchPhrase(String chatType, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("chatType", chatType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatType_MatchPhrasePrefix(String chatType) {
        setChatType_MatchPhrasePrefix(chatType, null);
    }

    public void setChatType_MatchPhrasePrefix(String chatType, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("chatType", chatType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatType_Fuzzy(String chatType) {
        setChatType_Fuzzy(chatType, null);
    }

    public void setChatType_Fuzzy(String chatType, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("chatType", chatType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatType_Prefix(String chatType) {
        setChatType_Prefix(chatType, null);
    }

    public void setChatType_Prefix(String chatType, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("chatType", chatType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatType_Wildcard(String chatType) {
        setChatType_Wildcard(chatType, null);
    }

    public void setChatType_Wildcard(String chatType, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("chatType", chatType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatType_Regexp(String chatType) {
        setChatType_Regexp(chatType, null);
    }

    public void setChatType_Regexp(String chatType, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("chatType", chatType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatType_SpanTerm(String chatType) {
        setChatType_SpanTerm("chatType", null);
    }

    public void setChatType_SpanTerm(String chatType, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("chatType", chatType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatType_GreaterThan(String chatType) {
        setChatType_GreaterThan(chatType, null);
    }

    public void setChatType_GreaterThan(String chatType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = chatType;
        RangeQueryBuilder builder = regRangeQ("chatType", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatType_LessThan(String chatType) {
        setChatType_LessThan(chatType, null);
    }

    public void setChatType_LessThan(String chatType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = chatType;
        RangeQueryBuilder builder = regRangeQ("chatType", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatType_GreaterEqual(String chatType) {
        setChatType_GreaterEqual(chatType, null);
    }

    public void setChatType_GreaterEqual(String chatType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = chatType;
        RangeQueryBuilder builder = regRangeQ("chatType", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatType_LessEqual(String chatType) {
        setChatType_LessEqual(chatType, null);
    }

    public void setChatType_LessEqual(String chatType, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = chatType;
        RangeQueryBuilder builder = regRangeQ("chatType", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatType_Exists() {
        setChatType_Exists(null);
    }

    public void setChatType_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("chatType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setChatType_CommonTerms(String chatType) {
        setChatType_CommonTerms(chatType, null);
    }

    @Deprecated
    public void setChatType_CommonTerms(String chatType, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("chatType", chatType);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsChatLogCQ addOrderBy_ChatType_Asc() {
        regOBA("chatType");
        return this;
    }

    public BsChatLogCQ addOrderBy_ChatType_Desc() {
        regOBD("chatType");
        return this;
    }

    public void setCompletionTokens_Equal(Long completionTokens) {
        setCompletionTokens_Term(completionTokens, null);
    }

    public void setCompletionTokens_Equal(Long completionTokens, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setCompletionTokens_Term(completionTokens, opLambda);
    }

    public void setCompletionTokens_Term(Long completionTokens) {
        setCompletionTokens_Term(completionTokens, null);
    }

    public void setCompletionTokens_Term(Long completionTokens, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("completionTokens", completionTokens);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_NotEqual(Long completionTokens) {
        setCompletionTokens_NotTerm(completionTokens, null);
    }

    public void setCompletionTokens_NotTerm(Long completionTokens) {
        setCompletionTokens_NotTerm(completionTokens, null);
    }

    public void setCompletionTokens_NotEqual(Long completionTokens, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setCompletionTokens_NotTerm(completionTokens, opLambda);
    }

    public void setCompletionTokens_NotTerm(Long completionTokens, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setCompletionTokens_Term(completionTokens), opLambda);
    }

    public void setCompletionTokens_Terms(Collection<Long> completionTokensList) {
        setCompletionTokens_Terms(completionTokensList, null);
    }

    public void setCompletionTokens_Terms(Collection<Long> completionTokensList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("completionTokens", completionTokensList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_InScope(Collection<Long> completionTokensList) {
        setCompletionTokens_Terms(completionTokensList, null);
    }

    public void setCompletionTokens_InScope(Collection<Long> completionTokensList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setCompletionTokens_Terms(completionTokensList, opLambda);
    }

    public void setCompletionTokens_Match(Long completionTokens) {
        setCompletionTokens_Match(completionTokens, null);
    }

    public void setCompletionTokens_Match(Long completionTokens, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("completionTokens", completionTokens);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_MatchPhrase(Long completionTokens) {
        setCompletionTokens_MatchPhrase(completionTokens, null);
    }

    public void setCompletionTokens_MatchPhrase(Long completionTokens, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("completionTokens", completionTokens);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_MatchPhrasePrefix(Long completionTokens) {
        setCompletionTokens_MatchPhrasePrefix(completionTokens, null);
    }

    public void setCompletionTokens_MatchPhrasePrefix(Long completionTokens, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("completionTokens", completionTokens);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_Fuzzy(Long completionTokens) {
        setCompletionTokens_Fuzzy(completionTokens, null);
    }

    public void setCompletionTokens_Fuzzy(Long completionTokens, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("completionTokens", completionTokens);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_GreaterThan(Long completionTokens) {
        setCompletionTokens_GreaterThan(completionTokens, null);
    }

    public void setCompletionTokens_GreaterThan(Long completionTokens, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = completionTokens;
        RangeQueryBuilder builder = regRangeQ("completionTokens", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_LessThan(Long completionTokens) {
        setCompletionTokens_LessThan(completionTokens, null);
    }

    public void setCompletionTokens_LessThan(Long completionTokens, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = completionTokens;
        RangeQueryBuilder builder = regRangeQ("completionTokens", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_GreaterEqual(Long completionTokens) {
        setCompletionTokens_GreaterEqual(completionTokens, null);
    }

    public void setCompletionTokens_GreaterEqual(Long completionTokens, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = completionTokens;
        RangeQueryBuilder builder = regRangeQ("completionTokens", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_LessEqual(Long completionTokens) {
        setCompletionTokens_LessEqual(completionTokens, null);
    }

    public void setCompletionTokens_LessEqual(Long completionTokens, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = completionTokens;
        RangeQueryBuilder builder = regRangeQ("completionTokens", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_Exists() {
        setCompletionTokens_Exists(null);
    }

    public void setCompletionTokens_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("completionTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setCompletionTokens_CommonTerms(Long completionTokens) {
        setCompletionTokens_CommonTerms(completionTokens, null);
    }

    @Deprecated
    public void setCompletionTokens_CommonTerms(Long completionTokens, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("completionTokens", completionTokens);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsChatLogCQ addOrderBy_CompletionTokens_Asc() {
        regOBA("completionTokens");
        return this;
    }

    public BsChatLogCQ addOrderBy_CompletionTokens_Desc() {
        regOBD("completionTokens");
        return this;
    }

    public void setErrorCode_Equal(String errorCode) {
        setErrorCode_Term(errorCode, null);
    }

    public void setErrorCode_Equal(String errorCode, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setErrorCode_Term(errorCode, opLambda);
    }

    public void setErrorCode_Term(String errorCode) {
        setErrorCode_Term(errorCode, null);
    }

    public void setErrorCode_Term(String errorCode, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("errorCode", errorCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCode_NotEqual(String errorCode) {
        setErrorCode_NotTerm(errorCode, null);
    }

    public void setErrorCode_NotTerm(String errorCode) {
        setErrorCode_NotTerm(errorCode, null);
    }

    public void setErrorCode_NotEqual(String errorCode, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setErrorCode_NotTerm(errorCode, opLambda);
    }

    public void setErrorCode_NotTerm(String errorCode, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setErrorCode_Term(errorCode), opLambda);
    }

    public void setErrorCode_Terms(Collection<String> errorCodeList) {
        setErrorCode_Terms(errorCodeList, null);
    }

    public void setErrorCode_Terms(Collection<String> errorCodeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("errorCode", errorCodeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCode_InScope(Collection<String> errorCodeList) {
        setErrorCode_Terms(errorCodeList, null);
    }

    public void setErrorCode_InScope(Collection<String> errorCodeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setErrorCode_Terms(errorCodeList, opLambda);
    }

    public void setErrorCode_Match(String errorCode) {
        setErrorCode_Match(errorCode, null);
    }

    public void setErrorCode_Match(String errorCode, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("errorCode", errorCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCode_MatchPhrase(String errorCode) {
        setErrorCode_MatchPhrase(errorCode, null);
    }

    public void setErrorCode_MatchPhrase(String errorCode, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("errorCode", errorCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCode_MatchPhrasePrefix(String errorCode) {
        setErrorCode_MatchPhrasePrefix(errorCode, null);
    }

    public void setErrorCode_MatchPhrasePrefix(String errorCode, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("errorCode", errorCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCode_Fuzzy(String errorCode) {
        setErrorCode_Fuzzy(errorCode, null);
    }

    public void setErrorCode_Fuzzy(String errorCode, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("errorCode", errorCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCode_Prefix(String errorCode) {
        setErrorCode_Prefix(errorCode, null);
    }

    public void setErrorCode_Prefix(String errorCode, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("errorCode", errorCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCode_Wildcard(String errorCode) {
        setErrorCode_Wildcard(errorCode, null);
    }

    public void setErrorCode_Wildcard(String errorCode, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("errorCode", errorCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCode_Regexp(String errorCode) {
        setErrorCode_Regexp(errorCode, null);
    }

    public void setErrorCode_Regexp(String errorCode, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("errorCode", errorCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCode_SpanTerm(String errorCode) {
        setErrorCode_SpanTerm("errorCode", null);
    }

    public void setErrorCode_SpanTerm(String errorCode, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("errorCode", errorCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCode_GreaterThan(String errorCode) {
        setErrorCode_GreaterThan(errorCode, null);
    }

    public void setErrorCode_GreaterThan(String errorCode, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = errorCode;
        RangeQueryBuilder builder = regRangeQ("errorCode", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCode_LessThan(String errorCode) {
        setErrorCode_LessThan(errorCode, null);
    }

    public void setErrorCode_LessThan(String errorCode, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = errorCode;
        RangeQueryBuilder builder = regRangeQ("errorCode", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCode_GreaterEqual(String errorCode) {
        setErrorCode_GreaterEqual(errorCode, null);
    }

    public void setErrorCode_GreaterEqual(String errorCode, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = errorCode;
        RangeQueryBuilder builder = regRangeQ("errorCode", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCode_LessEqual(String errorCode) {
        setErrorCode_LessEqual(errorCode, null);
    }

    public void setErrorCode_LessEqual(String errorCode, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = errorCode;
        RangeQueryBuilder builder = regRangeQ("errorCode", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCode_Exists() {
        setErrorCode_Exists(null);
    }

    public void setErrorCode_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("errorCode");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setErrorCode_CommonTerms(String errorCode) {
        setErrorCode_CommonTerms(errorCode, null);
    }

    @Deprecated
    public void setErrorCode_CommonTerms(String errorCode, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("errorCode", errorCode);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsChatLogCQ addOrderBy_ErrorCode_Asc() {
        regOBA("errorCode");
        return this;
    }

    public BsChatLogCQ addOrderBy_ErrorCode_Desc() {
        regOBD("errorCode");
        return this;
    }

    public void setIntent_Equal(String intent) {
        setIntent_Term(intent, null);
    }

    public void setIntent_Equal(String intent, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setIntent_Term(intent, opLambda);
    }

    public void setIntent_Term(String intent) {
        setIntent_Term(intent, null);
    }

    public void setIntent_Term(String intent, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("intent", intent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntent_NotEqual(String intent) {
        setIntent_NotTerm(intent, null);
    }

    public void setIntent_NotTerm(String intent) {
        setIntent_NotTerm(intent, null);
    }

    public void setIntent_NotEqual(String intent, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setIntent_NotTerm(intent, opLambda);
    }

    public void setIntent_NotTerm(String intent, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setIntent_Term(intent), opLambda);
    }

    public void setIntent_Terms(Collection<String> intentList) {
        setIntent_Terms(intentList, null);
    }

    public void setIntent_Terms(Collection<String> intentList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("intent", intentList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntent_InScope(Collection<String> intentList) {
        setIntent_Terms(intentList, null);
    }

    public void setIntent_InScope(Collection<String> intentList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setIntent_Terms(intentList, opLambda);
    }

    public void setIntent_Match(String intent) {
        setIntent_Match(intent, null);
    }

    public void setIntent_Match(String intent, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("intent", intent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntent_MatchPhrase(String intent) {
        setIntent_MatchPhrase(intent, null);
    }

    public void setIntent_MatchPhrase(String intent, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("intent", intent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntent_MatchPhrasePrefix(String intent) {
        setIntent_MatchPhrasePrefix(intent, null);
    }

    public void setIntent_MatchPhrasePrefix(String intent, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("intent", intent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntent_Fuzzy(String intent) {
        setIntent_Fuzzy(intent, null);
    }

    public void setIntent_Fuzzy(String intent, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("intent", intent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntent_Prefix(String intent) {
        setIntent_Prefix(intent, null);
    }

    public void setIntent_Prefix(String intent, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("intent", intent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntent_Wildcard(String intent) {
        setIntent_Wildcard(intent, null);
    }

    public void setIntent_Wildcard(String intent, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("intent", intent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntent_Regexp(String intent) {
        setIntent_Regexp(intent, null);
    }

    public void setIntent_Regexp(String intent, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("intent", intent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntent_SpanTerm(String intent) {
        setIntent_SpanTerm("intent", null);
    }

    public void setIntent_SpanTerm(String intent, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("intent", intent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntent_GreaterThan(String intent) {
        setIntent_GreaterThan(intent, null);
    }

    public void setIntent_GreaterThan(String intent, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = intent;
        RangeQueryBuilder builder = regRangeQ("intent", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntent_LessThan(String intent) {
        setIntent_LessThan(intent, null);
    }

    public void setIntent_LessThan(String intent, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = intent;
        RangeQueryBuilder builder = regRangeQ("intent", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntent_GreaterEqual(String intent) {
        setIntent_GreaterEqual(intent, null);
    }

    public void setIntent_GreaterEqual(String intent, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = intent;
        RangeQueryBuilder builder = regRangeQ("intent", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntent_LessEqual(String intent) {
        setIntent_LessEqual(intent, null);
    }

    public void setIntent_LessEqual(String intent, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = intent;
        RangeQueryBuilder builder = regRangeQ("intent", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntent_Exists() {
        setIntent_Exists(null);
    }

    public void setIntent_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("intent");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setIntent_CommonTerms(String intent) {
        setIntent_CommonTerms(intent, null);
    }

    @Deprecated
    public void setIntent_CommonTerms(String intent, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("intent", intent);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsChatLogCQ addOrderBy_Intent_Asc() {
        regOBA("intent");
        return this;
    }

    public BsChatLogCQ addOrderBy_Intent_Desc() {
        regOBD("intent");
        return this;
    }

    public void setLlmCalls_Equal(Integer llmCalls) {
        setLlmCalls_Term(llmCalls, null);
    }

    public void setLlmCalls_Equal(Integer llmCalls, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setLlmCalls_Term(llmCalls, opLambda);
    }

    public void setLlmCalls_Term(Integer llmCalls) {
        setLlmCalls_Term(llmCalls, null);
    }

    public void setLlmCalls_Term(Integer llmCalls, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("llmCalls", llmCalls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_NotEqual(Integer llmCalls) {
        setLlmCalls_NotTerm(llmCalls, null);
    }

    public void setLlmCalls_NotTerm(Integer llmCalls) {
        setLlmCalls_NotTerm(llmCalls, null);
    }

    public void setLlmCalls_NotEqual(Integer llmCalls, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setLlmCalls_NotTerm(llmCalls, opLambda);
    }

    public void setLlmCalls_NotTerm(Integer llmCalls, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setLlmCalls_Term(llmCalls), opLambda);
    }

    public void setLlmCalls_Terms(Collection<Integer> llmCallsList) {
        setLlmCalls_Terms(llmCallsList, null);
    }

    public void setLlmCalls_Terms(Collection<Integer> llmCallsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("llmCalls", llmCallsList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_InScope(Collection<Integer> llmCallsList) {
        setLlmCalls_Terms(llmCallsList, null);
    }

    public void setLlmCalls_InScope(Collection<Integer> llmCallsList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setLlmCalls_Terms(llmCallsList, opLambda);
    }

    public void setLlmCalls_Match(Integer llmCalls) {
        setLlmCalls_Match(llmCalls, null);
    }

    public void setLlmCalls_Match(Integer llmCalls, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("llmCalls", llmCalls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_MatchPhrase(Integer llmCalls) {
        setLlmCalls_MatchPhrase(llmCalls, null);
    }

    public void setLlmCalls_MatchPhrase(Integer llmCalls, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("llmCalls", llmCalls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_MatchPhrasePrefix(Integer llmCalls) {
        setLlmCalls_MatchPhrasePrefix(llmCalls, null);
    }

    public void setLlmCalls_MatchPhrasePrefix(Integer llmCalls, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("llmCalls", llmCalls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_Fuzzy(Integer llmCalls) {
        setLlmCalls_Fuzzy(llmCalls, null);
    }

    public void setLlmCalls_Fuzzy(Integer llmCalls, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("llmCalls", llmCalls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_GreaterThan(Integer llmCalls) {
        setLlmCalls_GreaterThan(llmCalls, null);
    }

    public void setLlmCalls_GreaterThan(Integer llmCalls, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = llmCalls;
        RangeQueryBuilder builder = regRangeQ("llmCalls", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_LessThan(Integer llmCalls) {
        setLlmCalls_LessThan(llmCalls, null);
    }

    public void setLlmCalls_LessThan(Integer llmCalls, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = llmCalls;
        RangeQueryBuilder builder = regRangeQ("llmCalls", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_GreaterEqual(Integer llmCalls) {
        setLlmCalls_GreaterEqual(llmCalls, null);
    }

    public void setLlmCalls_GreaterEqual(Integer llmCalls, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = llmCalls;
        RangeQueryBuilder builder = regRangeQ("llmCalls", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_LessEqual(Integer llmCalls) {
        setLlmCalls_LessEqual(llmCalls, null);
    }

    public void setLlmCalls_LessEqual(Integer llmCalls, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = llmCalls;
        RangeQueryBuilder builder = regRangeQ("llmCalls", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_Exists() {
        setLlmCalls_Exists(null);
    }

    public void setLlmCalls_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("llmCalls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setLlmCalls_CommonTerms(Integer llmCalls) {
        setLlmCalls_CommonTerms(llmCalls, null);
    }

    @Deprecated
    public void setLlmCalls_CommonTerms(Integer llmCalls, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("llmCalls", llmCalls);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsChatLogCQ addOrderBy_LlmCalls_Asc() {
        regOBA("llmCalls");
        return this;
    }

    public BsChatLogCQ addOrderBy_LlmCalls_Desc() {
        regOBD("llmCalls");
        return this;
    }

    public void setLlmName_Equal(String llmName) {
        setLlmName_Term(llmName, null);
    }

    public void setLlmName_Equal(String llmName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setLlmName_Term(llmName, opLambda);
    }

    public void setLlmName_Term(String llmName) {
        setLlmName_Term(llmName, null);
    }

    public void setLlmName_Term(String llmName, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("llmName", llmName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmName_NotEqual(String llmName) {
        setLlmName_NotTerm(llmName, null);
    }

    public void setLlmName_NotTerm(String llmName) {
        setLlmName_NotTerm(llmName, null);
    }

    public void setLlmName_NotEqual(String llmName, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setLlmName_NotTerm(llmName, opLambda);
    }

    public void setLlmName_NotTerm(String llmName, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setLlmName_Term(llmName), opLambda);
    }

    public void setLlmName_Terms(Collection<String> llmNameList) {
        setLlmName_Terms(llmNameList, null);
    }

    public void setLlmName_Terms(Collection<String> llmNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("llmName", llmNameList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmName_InScope(Collection<String> llmNameList) {
        setLlmName_Terms(llmNameList, null);
    }

    public void setLlmName_InScope(Collection<String> llmNameList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setLlmName_Terms(llmNameList, opLambda);
    }

    public void setLlmName_Match(String llmName) {
        setLlmName_Match(llmName, null);
    }

    public void setLlmName_Match(String llmName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("llmName", llmName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmName_MatchPhrase(String llmName) {
        setLlmName_MatchPhrase(llmName, null);
    }

    public void setLlmName_MatchPhrase(String llmName, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("llmName", llmName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmName_MatchPhrasePrefix(String llmName) {
        setLlmName_MatchPhrasePrefix(llmName, null);
    }

    public void setLlmName_MatchPhrasePrefix(String llmName, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("llmName", llmName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmName_Fuzzy(String llmName) {
        setLlmName_Fuzzy(llmName, null);
    }

    public void setLlmName_Fuzzy(String llmName, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("llmName", llmName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmName_Prefix(String llmName) {
        setLlmName_Prefix(llmName, null);
    }

    public void setLlmName_Prefix(String llmName, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("llmName", llmName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmName_Wildcard(String llmName) {
        setLlmName_Wildcard(llmName, null);
    }

    public void setLlmName_Wildcard(String llmName, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("llmName", llmName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmName_Regexp(String llmName) {
        setLlmName_Regexp(llmName, null);
    }

    public void setLlmName_Regexp(String llmName, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("llmName", llmName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmName_SpanTerm(String llmName) {
        setLlmName_SpanTerm("llmName", null);
    }

    public void setLlmName_SpanTerm(String llmName, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("llmName", llmName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmName_GreaterThan(String llmName) {
        setLlmName_GreaterThan(llmName, null);
    }

    public void setLlmName_GreaterThan(String llmName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = llmName;
        RangeQueryBuilder builder = regRangeQ("llmName", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmName_LessThan(String llmName) {
        setLlmName_LessThan(llmName, null);
    }

    public void setLlmName_LessThan(String llmName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = llmName;
        RangeQueryBuilder builder = regRangeQ("llmName", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmName_GreaterEqual(String llmName) {
        setLlmName_GreaterEqual(llmName, null);
    }

    public void setLlmName_GreaterEqual(String llmName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = llmName;
        RangeQueryBuilder builder = regRangeQ("llmName", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmName_LessEqual(String llmName) {
        setLlmName_LessEqual(llmName, null);
    }

    public void setLlmName_LessEqual(String llmName, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = llmName;
        RangeQueryBuilder builder = regRangeQ("llmName", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmName_Exists() {
        setLlmName_Exists(null);
    }

    public void setLlmName_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("llmName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setLlmName_CommonTerms(String llmName) {
        setLlmName_CommonTerms(llmName, null);
    }

    @Deprecated
    public void setLlmName_CommonTerms(String llmName, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("llmName", llmName);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsChatLogCQ addOrderBy_LlmName_Asc() {
        regOBA("llmName");
        return this;
    }

    public BsChatLogCQ addOrderBy_LlmName_Desc() {
        regOBD("llmName");
        return this;
    }

    public void setModel_Equal(String model) {
        setModel_Term(model, null);
    }

    public void setModel_Equal(String model, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setModel_Term(model, opLambda);
    }

    public void setModel_Term(String model) {
        setModel_Term(model, null);
    }

    public void setModel_Term(String model, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("model", model);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setModel_NotEqual(String model) {
        setModel_NotTerm(model, null);
    }

    public void setModel_NotTerm(String model) {
        setModel_NotTerm(model, null);
    }

    public void setModel_NotEqual(String model, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setModel_NotTerm(model, opLambda);
    }

    public void setModel_NotTerm(String model, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setModel_Term(model), opLambda);
    }

    public void setModel_Terms(Collection<String> modelList) {
        setModel_Terms(modelList, null);
    }

    public void setModel_Terms(Collection<String> modelList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("model", modelList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setModel_InScope(Collection<String> modelList) {
        setModel_Terms(modelList, null);
    }

    public void setModel_InScope(Collection<String> modelList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setModel_Terms(modelList, opLambda);
    }

    public void setModel_Match(String model) {
        setModel_Match(model, null);
    }

    public void setModel_Match(String model, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("model", model);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setModel_MatchPhrase(String model) {
        setModel_MatchPhrase(model, null);
    }

    public void setModel_MatchPhrase(String model, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("model", model);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setModel_MatchPhrasePrefix(String model) {
        setModel_MatchPhrasePrefix(model, null);
    }

    public void setModel_MatchPhrasePrefix(String model, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("model", model);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setModel_Fuzzy(String model) {
        setModel_Fuzzy(model, null);
    }

    public void setModel_Fuzzy(String model, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("model", model);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setModel_Prefix(String model) {
        setModel_Prefix(model, null);
    }

    public void setModel_Prefix(String model, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("model", model);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setModel_Wildcard(String model) {
        setModel_Wildcard(model, null);
    }

    public void setModel_Wildcard(String model, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("model", model);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setModel_Regexp(String model) {
        setModel_Regexp(model, null);
    }

    public void setModel_Regexp(String model, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("model", model);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setModel_SpanTerm(String model) {
        setModel_SpanTerm("model", null);
    }

    public void setModel_SpanTerm(String model, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("model", model);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setModel_GreaterThan(String model) {
        setModel_GreaterThan(model, null);
    }

    public void setModel_GreaterThan(String model, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = model;
        RangeQueryBuilder builder = regRangeQ("model", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setModel_LessThan(String model) {
        setModel_LessThan(model, null);
    }

    public void setModel_LessThan(String model, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = model;
        RangeQueryBuilder builder = regRangeQ("model", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setModel_GreaterEqual(String model) {
        setModel_GreaterEqual(model, null);
    }

    public void setModel_GreaterEqual(String model, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = model;
        RangeQueryBuilder builder = regRangeQ("model", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setModel_LessEqual(String model) {
        setModel_LessEqual(model, null);
    }

    public void setModel_LessEqual(String model, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = model;
        RangeQueryBuilder builder = regRangeQ("model", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setModel_Exists() {
        setModel_Exists(null);
    }

    public void setModel_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("model");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setModel_CommonTerms(String model) {
        setModel_CommonTerms(model, null);
    }

    @Deprecated
    public void setModel_CommonTerms(String model, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("model", model);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsChatLogCQ addOrderBy_Model_Asc() {
        regOBA("model");
        return this;
    }

    public BsChatLogCQ addOrderBy_Model_Desc() {
        regOBD("model");
        return this;
    }

    public void setPromptTokens_Equal(Long promptTokens) {
        setPromptTokens_Term(promptTokens, null);
    }

    public void setPromptTokens_Equal(Long promptTokens, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setPromptTokens_Term(promptTokens, opLambda);
    }

    public void setPromptTokens_Term(Long promptTokens) {
        setPromptTokens_Term(promptTokens, null);
    }

    public void setPromptTokens_Term(Long promptTokens, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("promptTokens", promptTokens);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_NotEqual(Long promptTokens) {
        setPromptTokens_NotTerm(promptTokens, null);
    }

    public void setPromptTokens_NotTerm(Long promptTokens) {
        setPromptTokens_NotTerm(promptTokens, null);
    }

    public void setPromptTokens_NotEqual(Long promptTokens, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setPromptTokens_NotTerm(promptTokens, opLambda);
    }

    public void setPromptTokens_NotTerm(Long promptTokens, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setPromptTokens_Term(promptTokens), opLambda);
    }

    public void setPromptTokens_Terms(Collection<Long> promptTokensList) {
        setPromptTokens_Terms(promptTokensList, null);
    }

    public void setPromptTokens_Terms(Collection<Long> promptTokensList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("promptTokens", promptTokensList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_InScope(Collection<Long> promptTokensList) {
        setPromptTokens_Terms(promptTokensList, null);
    }

    public void setPromptTokens_InScope(Collection<Long> promptTokensList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setPromptTokens_Terms(promptTokensList, opLambda);
    }

    public void setPromptTokens_Match(Long promptTokens) {
        setPromptTokens_Match(promptTokens, null);
    }

    public void setPromptTokens_Match(Long promptTokens, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("promptTokens", promptTokens);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_MatchPhrase(Long promptTokens) {
        setPromptTokens_MatchPhrase(promptTokens, null);
    }

    public void setPromptTokens_MatchPhrase(Long promptTokens, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("promptTokens", promptTokens);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_MatchPhrasePrefix(Long promptTokens) {
        setPromptTokens_MatchPhrasePrefix(promptTokens, null);
    }

    public void setPromptTokens_MatchPhrasePrefix(Long promptTokens, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("promptTokens", promptTokens);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_Fuzzy(Long promptTokens) {
        setPromptTokens_Fuzzy(promptTokens, null);
    }

    public void setPromptTokens_Fuzzy(Long promptTokens, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("promptTokens", promptTokens);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_GreaterThan(Long promptTokens) {
        setPromptTokens_GreaterThan(promptTokens, null);
    }

    public void setPromptTokens_GreaterThan(Long promptTokens, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = promptTokens;
        RangeQueryBuilder builder = regRangeQ("promptTokens", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_LessThan(Long promptTokens) {
        setPromptTokens_LessThan(promptTokens, null);
    }

    public void setPromptTokens_LessThan(Long promptTokens, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = promptTokens;
        RangeQueryBuilder builder = regRangeQ("promptTokens", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_GreaterEqual(Long promptTokens) {
        setPromptTokens_GreaterEqual(promptTokens, null);
    }

    public void setPromptTokens_GreaterEqual(Long promptTokens, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = promptTokens;
        RangeQueryBuilder builder = regRangeQ("promptTokens", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_LessEqual(Long promptTokens) {
        setPromptTokens_LessEqual(promptTokens, null);
    }

    public void setPromptTokens_LessEqual(Long promptTokens, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = promptTokens;
        RangeQueryBuilder builder = regRangeQ("promptTokens", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_Exists() {
        setPromptTokens_Exists(null);
    }

    public void setPromptTokens_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("promptTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setPromptTokens_CommonTerms(Long promptTokens) {
        setPromptTokens_CommonTerms(promptTokens, null);
    }

    @Deprecated
    public void setPromptTokens_CommonTerms(Long promptTokens, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("promptTokens", promptTokens);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsChatLogCQ addOrderBy_PromptTokens_Asc() {
        regOBA("promptTokens");
        return this;
    }

    public BsChatLogCQ addOrderBy_PromptTokens_Desc() {
        regOBD("promptTokens");
        return this;
    }

    public void setRequestedAt_Equal(LocalDateTime requestedAt) {
        setRequestedAt_Term(requestedAt, null);
    }

    public void setRequestedAt_Equal(LocalDateTime requestedAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setRequestedAt_Term(requestedAt, opLambda);
    }

    public void setRequestedAt_Term(LocalDateTime requestedAt) {
        setRequestedAt_Term(requestedAt, null);
    }

    public void setRequestedAt_Term(LocalDateTime requestedAt, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("requestedAt", requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_NotEqual(LocalDateTime requestedAt) {
        setRequestedAt_NotTerm(requestedAt, null);
    }

    public void setRequestedAt_NotTerm(LocalDateTime requestedAt) {
        setRequestedAt_NotTerm(requestedAt, null);
    }

    public void setRequestedAt_NotEqual(LocalDateTime requestedAt, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setRequestedAt_NotTerm(requestedAt, opLambda);
    }

    public void setRequestedAt_NotTerm(LocalDateTime requestedAt, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setRequestedAt_Term(requestedAt), opLambda);
    }

    public void setRequestedAt_Terms(Collection<LocalDateTime> requestedAtList) {
        setRequestedAt_Terms(requestedAtList, null);
    }

    public void setRequestedAt_Terms(Collection<LocalDateTime> requestedAtList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("requestedAt", requestedAtList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_InScope(Collection<LocalDateTime> requestedAtList) {
        setRequestedAt_Terms(requestedAtList, null);
    }

    public void setRequestedAt_InScope(Collection<LocalDateTime> requestedAtList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setRequestedAt_Terms(requestedAtList, opLambda);
    }

    public void setRequestedAt_Match(LocalDateTime requestedAt) {
        setRequestedAt_Match(requestedAt, null);
    }

    public void setRequestedAt_Match(LocalDateTime requestedAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("requestedAt", requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_MatchPhrase(LocalDateTime requestedAt) {
        setRequestedAt_MatchPhrase(requestedAt, null);
    }

    public void setRequestedAt_MatchPhrase(LocalDateTime requestedAt, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("requestedAt", requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_MatchPhrasePrefix(LocalDateTime requestedAt) {
        setRequestedAt_MatchPhrasePrefix(requestedAt, null);
    }

    public void setRequestedAt_MatchPhrasePrefix(LocalDateTime requestedAt, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("requestedAt", requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_Fuzzy(LocalDateTime requestedAt) {
        setRequestedAt_Fuzzy(requestedAt, null);
    }

    public void setRequestedAt_Fuzzy(LocalDateTime requestedAt, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("requestedAt", requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_GreaterThan(LocalDateTime requestedAt) {
        setRequestedAt_GreaterThan(requestedAt, null);
    }

    public void setRequestedAt_GreaterThan(LocalDateTime requestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(requestedAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("requestedAt", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_LessThan(LocalDateTime requestedAt) {
        setRequestedAt_LessThan(requestedAt, null);
    }

    public void setRequestedAt_LessThan(LocalDateTime requestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(requestedAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("requestedAt", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_GreaterEqual(LocalDateTime requestedAt) {
        setRequestedAt_GreaterEqual(requestedAt, null);
    }

    public void setRequestedAt_GreaterEqual(LocalDateTime requestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(requestedAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("requestedAt", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_LessEqual(LocalDateTime requestedAt) {
        setRequestedAt_LessEqual(requestedAt, null);
    }

    public void setRequestedAt_LessEqual(LocalDateTime requestedAt, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = toRangeLocalDateTimeString(requestedAt, "date_optional_time");
        RangeQueryBuilder builder = regRangeQ("requestedAt", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_Exists() {
        setRequestedAt_Exists(null);
    }

    public void setRequestedAt_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("requestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setRequestedAt_CommonTerms(LocalDateTime requestedAt) {
        setRequestedAt_CommonTerms(requestedAt, null);
    }

    @Deprecated
    public void setRequestedAt_CommonTerms(LocalDateTime requestedAt, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("requestedAt", requestedAt);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsChatLogCQ addOrderBy_RequestedAt_Asc() {
        regOBA("requestedAt");
        return this;
    }

    public BsChatLogCQ addOrderBy_RequestedAt_Desc() {
        regOBD("requestedAt");
        return this;
    }

    public void setResponseTime_Equal(Long responseTime) {
        setResponseTime_Term(responseTime, null);
    }

    public void setResponseTime_Equal(Long responseTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setResponseTime_Term(responseTime, opLambda);
    }

    public void setResponseTime_Term(Long responseTime) {
        setResponseTime_Term(responseTime, null);
    }

    public void setResponseTime_Term(Long responseTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_NotEqual(Long responseTime) {
        setResponseTime_NotTerm(responseTime, null);
    }

    public void setResponseTime_NotTerm(Long responseTime) {
        setResponseTime_NotTerm(responseTime, null);
    }

    public void setResponseTime_NotEqual(Long responseTime, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setResponseTime_NotTerm(responseTime, opLambda);
    }

    public void setResponseTime_NotTerm(Long responseTime, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setResponseTime_Term(responseTime), opLambda);
    }

    public void setResponseTime_Terms(Collection<Long> responseTimeList) {
        setResponseTime_Terms(responseTimeList, null);
    }

    public void setResponseTime_Terms(Collection<Long> responseTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("responseTime", responseTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_InScope(Collection<Long> responseTimeList) {
        setResponseTime_Terms(responseTimeList, null);
    }

    public void setResponseTime_InScope(Collection<Long> responseTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setResponseTime_Terms(responseTimeList, opLambda);
    }

    public void setResponseTime_Match(Long responseTime) {
        setResponseTime_Match(responseTime, null);
    }

    public void setResponseTime_Match(Long responseTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_MatchPhrase(Long responseTime) {
        setResponseTime_MatchPhrase(responseTime, null);
    }

    public void setResponseTime_MatchPhrase(Long responseTime, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_MatchPhrasePrefix(Long responseTime) {
        setResponseTime_MatchPhrasePrefix(responseTime, null);
    }

    public void setResponseTime_MatchPhrasePrefix(Long responseTime, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Fuzzy(Long responseTime) {
        setResponseTime_Fuzzy(responseTime, null);
    }

    public void setResponseTime_Fuzzy(Long responseTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_GreaterThan(Long responseTime) {
        setResponseTime_GreaterThan(responseTime, null);
    }

    public void setResponseTime_GreaterThan(Long responseTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = responseTime;
        RangeQueryBuilder builder = regRangeQ("responseTime", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_LessThan(Long responseTime) {
        setResponseTime_LessThan(responseTime, null);
    }

    public void setResponseTime_LessThan(Long responseTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = responseTime;
        RangeQueryBuilder builder = regRangeQ("responseTime", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_GreaterEqual(Long responseTime) {
        setResponseTime_GreaterEqual(responseTime, null);
    }

    public void setResponseTime_GreaterEqual(Long responseTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = responseTime;
        RangeQueryBuilder builder = regRangeQ("responseTime", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_LessEqual(Long responseTime) {
        setResponseTime_LessEqual(responseTime, null);
    }

    public void setResponseTime_LessEqual(Long responseTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = responseTime;
        RangeQueryBuilder builder = regRangeQ("responseTime", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Exists() {
        setResponseTime_Exists(null);
    }

    public void setResponseTime_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setResponseTime_CommonTerms(Long responseTime) {
        setResponseTime_CommonTerms(responseTime, null);
    }

    @Deprecated
    public void setResponseTime_CommonTerms(Long responseTime, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("responseTime", responseTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsChatLogCQ addOrderBy_ResponseTime_Asc() {
        regOBA("responseTime");
        return this;
    }

    public BsChatLogCQ addOrderBy_ResponseTime_Desc() {
        regOBD("responseTime");
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

    public void setRoles_NotTerm(String roles) {
        setRoles_NotTerm(roles, null);
    }

    public void setRoles_NotEqual(String roles, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setRoles_NotTerm(roles, opLambda);
    }

    public void setRoles_NotTerm(String roles, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setRoles_Term(roles), opLambda);
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

    public void setRoles_MatchPhrase(String roles, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_MatchPhrasePrefix(String roles) {
        setRoles_MatchPhrasePrefix(roles, null);
    }

    public void setRoles_MatchPhrasePrefix(String roles, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_Fuzzy(String roles) {
        setRoles_Fuzzy(roles, null);
    }

    public void setRoles_Fuzzy(String roles, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("roles", roles);
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

    public void setRoles_Wildcard(String roles) {
        setRoles_Wildcard(roles, null);
    }

    public void setRoles_Wildcard(String roles, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_Regexp(String roles) {
        setRoles_Regexp(roles, null);
    }

    public void setRoles_Regexp(String roles, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_SpanTerm(String roles) {
        setRoles_SpanTerm("roles", null);
    }

    public void setRoles_SpanTerm(String roles, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_GreaterThan(String roles) {
        setRoles_GreaterThan(roles, null);
    }

    public void setRoles_GreaterThan(String roles, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = roles;
        RangeQueryBuilder builder = regRangeQ("roles", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_LessThan(String roles) {
        setRoles_LessThan(roles, null);
    }

    public void setRoles_LessThan(String roles, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = roles;
        RangeQueryBuilder builder = regRangeQ("roles", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_GreaterEqual(String roles) {
        setRoles_GreaterEqual(roles, null);
    }

    public void setRoles_GreaterEqual(String roles, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = roles;
        RangeQueryBuilder builder = regRangeQ("roles", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_LessEqual(String roles) {
        setRoles_LessEqual(roles, null);
    }

    public void setRoles_LessEqual(String roles, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = roles;
        RangeQueryBuilder builder = regRangeQ("roles", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_Exists() {
        setRoles_Exists(null);
    }

    public void setRoles_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setRoles_CommonTerms(String roles) {
        setRoles_CommonTerms(roles, null);
    }

    @Deprecated
    public void setRoles_CommonTerms(String roles, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("roles", roles);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsChatLogCQ addOrderBy_Roles_Asc() {
        regOBA("roles");
        return this;
    }

    public BsChatLogCQ addOrderBy_Roles_Desc() {
        regOBD("roles");
        return this;
    }

    public void setSearchQueryId_Equal(String searchQueryId) {
        setSearchQueryId_Term(searchQueryId, null);
    }

    public void setSearchQueryId_Equal(String searchQueryId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setSearchQueryId_Term(searchQueryId, opLambda);
    }

    public void setSearchQueryId_Term(String searchQueryId) {
        setSearchQueryId_Term(searchQueryId, null);
    }

    public void setSearchQueryId_Term(String searchQueryId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("searchQueryId", searchQueryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchQueryId_NotEqual(String searchQueryId) {
        setSearchQueryId_NotTerm(searchQueryId, null);
    }

    public void setSearchQueryId_NotTerm(String searchQueryId) {
        setSearchQueryId_NotTerm(searchQueryId, null);
    }

    public void setSearchQueryId_NotEqual(String searchQueryId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setSearchQueryId_NotTerm(searchQueryId, opLambda);
    }

    public void setSearchQueryId_NotTerm(String searchQueryId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setSearchQueryId_Term(searchQueryId), opLambda);
    }

    public void setSearchQueryId_Terms(Collection<String> searchQueryIdList) {
        setSearchQueryId_Terms(searchQueryIdList, null);
    }

    public void setSearchQueryId_Terms(Collection<String> searchQueryIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("searchQueryId", searchQueryIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchQueryId_InScope(Collection<String> searchQueryIdList) {
        setSearchQueryId_Terms(searchQueryIdList, null);
    }

    public void setSearchQueryId_InScope(Collection<String> searchQueryIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setSearchQueryId_Terms(searchQueryIdList, opLambda);
    }

    public void setSearchQueryId_Match(String searchQueryId) {
        setSearchQueryId_Match(searchQueryId, null);
    }

    public void setSearchQueryId_Match(String searchQueryId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("searchQueryId", searchQueryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchQueryId_MatchPhrase(String searchQueryId) {
        setSearchQueryId_MatchPhrase(searchQueryId, null);
    }

    public void setSearchQueryId_MatchPhrase(String searchQueryId, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("searchQueryId", searchQueryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchQueryId_MatchPhrasePrefix(String searchQueryId) {
        setSearchQueryId_MatchPhrasePrefix(searchQueryId, null);
    }

    public void setSearchQueryId_MatchPhrasePrefix(String searchQueryId, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("searchQueryId", searchQueryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchQueryId_Fuzzy(String searchQueryId) {
        setSearchQueryId_Fuzzy(searchQueryId, null);
    }

    public void setSearchQueryId_Fuzzy(String searchQueryId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("searchQueryId", searchQueryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchQueryId_Prefix(String searchQueryId) {
        setSearchQueryId_Prefix(searchQueryId, null);
    }

    public void setSearchQueryId_Prefix(String searchQueryId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("searchQueryId", searchQueryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchQueryId_Wildcard(String searchQueryId) {
        setSearchQueryId_Wildcard(searchQueryId, null);
    }

    public void setSearchQueryId_Wildcard(String searchQueryId, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("searchQueryId", searchQueryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchQueryId_Regexp(String searchQueryId) {
        setSearchQueryId_Regexp(searchQueryId, null);
    }

    public void setSearchQueryId_Regexp(String searchQueryId, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("searchQueryId", searchQueryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchQueryId_SpanTerm(String searchQueryId) {
        setSearchQueryId_SpanTerm("searchQueryId", null);
    }

    public void setSearchQueryId_SpanTerm(String searchQueryId, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("searchQueryId", searchQueryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchQueryId_GreaterThan(String searchQueryId) {
        setSearchQueryId_GreaterThan(searchQueryId, null);
    }

    public void setSearchQueryId_GreaterThan(String searchQueryId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = searchQueryId;
        RangeQueryBuilder builder = regRangeQ("searchQueryId", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchQueryId_LessThan(String searchQueryId) {
        setSearchQueryId_LessThan(searchQueryId, null);
    }

    public void setSearchQueryId_LessThan(String searchQueryId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = searchQueryId;
        RangeQueryBuilder builder = regRangeQ("searchQueryId", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchQueryId_GreaterEqual(String searchQueryId) {
        setSearchQueryId_GreaterEqual(searchQueryId, null);
    }

    public void setSearchQueryId_GreaterEqual(String searchQueryId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = searchQueryId;
        RangeQueryBuilder builder = regRangeQ("searchQueryId", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchQueryId_LessEqual(String searchQueryId) {
        setSearchQueryId_LessEqual(searchQueryId, null);
    }

    public void setSearchQueryId_LessEqual(String searchQueryId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = searchQueryId;
        RangeQueryBuilder builder = regRangeQ("searchQueryId", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchQueryId_Exists() {
        setSearchQueryId_Exists(null);
    }

    public void setSearchQueryId_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("searchQueryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setSearchQueryId_CommonTerms(String searchQueryId) {
        setSearchQueryId_CommonTerms(searchQueryId, null);
    }

    @Deprecated
    public void setSearchQueryId_CommonTerms(String searchQueryId, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("searchQueryId", searchQueryId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsChatLogCQ addOrderBy_SearchQueryId_Asc() {
        regOBA("searchQueryId");
        return this;
    }

    public BsChatLogCQ addOrderBy_SearchQueryId_Desc() {
        regOBD("searchQueryId");
        return this;
    }

    public void setSourceCount_Equal(Integer sourceCount) {
        setSourceCount_Term(sourceCount, null);
    }

    public void setSourceCount_Equal(Integer sourceCount, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setSourceCount_Term(sourceCount, opLambda);
    }

    public void setSourceCount_Term(Integer sourceCount) {
        setSourceCount_Term(sourceCount, null);
    }

    public void setSourceCount_Term(Integer sourceCount, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("sourceCount", sourceCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_NotEqual(Integer sourceCount) {
        setSourceCount_NotTerm(sourceCount, null);
    }

    public void setSourceCount_NotTerm(Integer sourceCount) {
        setSourceCount_NotTerm(sourceCount, null);
    }

    public void setSourceCount_NotEqual(Integer sourceCount, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setSourceCount_NotTerm(sourceCount, opLambda);
    }

    public void setSourceCount_NotTerm(Integer sourceCount, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setSourceCount_Term(sourceCount), opLambda);
    }

    public void setSourceCount_Terms(Collection<Integer> sourceCountList) {
        setSourceCount_Terms(sourceCountList, null);
    }

    public void setSourceCount_Terms(Collection<Integer> sourceCountList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("sourceCount", sourceCountList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_InScope(Collection<Integer> sourceCountList) {
        setSourceCount_Terms(sourceCountList, null);
    }

    public void setSourceCount_InScope(Collection<Integer> sourceCountList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setSourceCount_Terms(sourceCountList, opLambda);
    }

    public void setSourceCount_Match(Integer sourceCount) {
        setSourceCount_Match(sourceCount, null);
    }

    public void setSourceCount_Match(Integer sourceCount, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("sourceCount", sourceCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_MatchPhrase(Integer sourceCount) {
        setSourceCount_MatchPhrase(sourceCount, null);
    }

    public void setSourceCount_MatchPhrase(Integer sourceCount, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("sourceCount", sourceCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_MatchPhrasePrefix(Integer sourceCount) {
        setSourceCount_MatchPhrasePrefix(sourceCount, null);
    }

    public void setSourceCount_MatchPhrasePrefix(Integer sourceCount, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("sourceCount", sourceCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_Fuzzy(Integer sourceCount) {
        setSourceCount_Fuzzy(sourceCount, null);
    }

    public void setSourceCount_Fuzzy(Integer sourceCount, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("sourceCount", sourceCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_GreaterThan(Integer sourceCount) {
        setSourceCount_GreaterThan(sourceCount, null);
    }

    public void setSourceCount_GreaterThan(Integer sourceCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = sourceCount;
        RangeQueryBuilder builder = regRangeQ("sourceCount", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_LessThan(Integer sourceCount) {
        setSourceCount_LessThan(sourceCount, null);
    }

    public void setSourceCount_LessThan(Integer sourceCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = sourceCount;
        RangeQueryBuilder builder = regRangeQ("sourceCount", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_GreaterEqual(Integer sourceCount) {
        setSourceCount_GreaterEqual(sourceCount, null);
    }

    public void setSourceCount_GreaterEqual(Integer sourceCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = sourceCount;
        RangeQueryBuilder builder = regRangeQ("sourceCount", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_LessEqual(Integer sourceCount) {
        setSourceCount_LessEqual(sourceCount, null);
    }

    public void setSourceCount_LessEqual(Integer sourceCount, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = sourceCount;
        RangeQueryBuilder builder = regRangeQ("sourceCount", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_Exists() {
        setSourceCount_Exists(null);
    }

    public void setSourceCount_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("sourceCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setSourceCount_CommonTerms(Integer sourceCount) {
        setSourceCount_CommonTerms(sourceCount, null);
    }

    @Deprecated
    public void setSourceCount_CommonTerms(Integer sourceCount, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("sourceCount", sourceCount);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsChatLogCQ addOrderBy_SourceCount_Asc() {
        regOBA("sourceCount");
        return this;
    }

    public BsChatLogCQ addOrderBy_SourceCount_Desc() {
        regOBD("sourceCount");
        return this;
    }

    public void setStatus_Equal(String status) {
        setStatus_Term(status, null);
    }

    public void setStatus_Equal(String status, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setStatus_Term(status, opLambda);
    }

    public void setStatus_Term(String status) {
        setStatus_Term(status, null);
    }

    public void setStatus_Term(String status, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("status", status);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStatus_NotEqual(String status) {
        setStatus_NotTerm(status, null);
    }

    public void setStatus_NotTerm(String status) {
        setStatus_NotTerm(status, null);
    }

    public void setStatus_NotEqual(String status, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setStatus_NotTerm(status, opLambda);
    }

    public void setStatus_NotTerm(String status, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setStatus_Term(status), opLambda);
    }

    public void setStatus_Terms(Collection<String> statusList) {
        setStatus_Terms(statusList, null);
    }

    public void setStatus_Terms(Collection<String> statusList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("status", statusList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStatus_InScope(Collection<String> statusList) {
        setStatus_Terms(statusList, null);
    }

    public void setStatus_InScope(Collection<String> statusList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setStatus_Terms(statusList, opLambda);
    }

    public void setStatus_Match(String status) {
        setStatus_Match(status, null);
    }

    public void setStatus_Match(String status, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("status", status);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStatus_MatchPhrase(String status) {
        setStatus_MatchPhrase(status, null);
    }

    public void setStatus_MatchPhrase(String status, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("status", status);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStatus_MatchPhrasePrefix(String status) {
        setStatus_MatchPhrasePrefix(status, null);
    }

    public void setStatus_MatchPhrasePrefix(String status, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("status", status);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStatus_Fuzzy(String status) {
        setStatus_Fuzzy(status, null);
    }

    public void setStatus_Fuzzy(String status, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("status", status);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStatus_Prefix(String status) {
        setStatus_Prefix(status, null);
    }

    public void setStatus_Prefix(String status, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("status", status);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStatus_Wildcard(String status) {
        setStatus_Wildcard(status, null);
    }

    public void setStatus_Wildcard(String status, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("status", status);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStatus_Regexp(String status) {
        setStatus_Regexp(status, null);
    }

    public void setStatus_Regexp(String status, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("status", status);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStatus_SpanTerm(String status) {
        setStatus_SpanTerm("status", null);
    }

    public void setStatus_SpanTerm(String status, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("status", status);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStatus_GreaterThan(String status) {
        setStatus_GreaterThan(status, null);
    }

    public void setStatus_GreaterThan(String status, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = status;
        RangeQueryBuilder builder = regRangeQ("status", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStatus_LessThan(String status) {
        setStatus_LessThan(status, null);
    }

    public void setStatus_LessThan(String status, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = status;
        RangeQueryBuilder builder = regRangeQ("status", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStatus_GreaterEqual(String status) {
        setStatus_GreaterEqual(status, null);
    }

    public void setStatus_GreaterEqual(String status, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = status;
        RangeQueryBuilder builder = regRangeQ("status", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStatus_LessEqual(String status) {
        setStatus_LessEqual(status, null);
    }

    public void setStatus_LessEqual(String status, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = status;
        RangeQueryBuilder builder = regRangeQ("status", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStatus_Exists() {
        setStatus_Exists(null);
    }

    public void setStatus_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("status");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setStatus_CommonTerms(String status) {
        setStatus_CommonTerms(status, null);
    }

    @Deprecated
    public void setStatus_CommonTerms(String status, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("status", status);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsChatLogCQ addOrderBy_Status_Asc() {
        regOBA("status");
        return this;
    }

    public BsChatLogCQ addOrderBy_Status_Desc() {
        regOBD("status");
        return this;
    }

    public void setTotalTokens_Equal(Long totalTokens) {
        setTotalTokens_Term(totalTokens, null);
    }

    public void setTotalTokens_Equal(Long totalTokens, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setTotalTokens_Term(totalTokens, opLambda);
    }

    public void setTotalTokens_Term(Long totalTokens) {
        setTotalTokens_Term(totalTokens, null);
    }

    public void setTotalTokens_Term(Long totalTokens, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("totalTokens", totalTokens);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_NotEqual(Long totalTokens) {
        setTotalTokens_NotTerm(totalTokens, null);
    }

    public void setTotalTokens_NotTerm(Long totalTokens) {
        setTotalTokens_NotTerm(totalTokens, null);
    }

    public void setTotalTokens_NotEqual(Long totalTokens, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setTotalTokens_NotTerm(totalTokens, opLambda);
    }

    public void setTotalTokens_NotTerm(Long totalTokens, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setTotalTokens_Term(totalTokens), opLambda);
    }

    public void setTotalTokens_Terms(Collection<Long> totalTokensList) {
        setTotalTokens_Terms(totalTokensList, null);
    }

    public void setTotalTokens_Terms(Collection<Long> totalTokensList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("totalTokens", totalTokensList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_InScope(Collection<Long> totalTokensList) {
        setTotalTokens_Terms(totalTokensList, null);
    }

    public void setTotalTokens_InScope(Collection<Long> totalTokensList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setTotalTokens_Terms(totalTokensList, opLambda);
    }

    public void setTotalTokens_Match(Long totalTokens) {
        setTotalTokens_Match(totalTokens, null);
    }

    public void setTotalTokens_Match(Long totalTokens, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("totalTokens", totalTokens);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_MatchPhrase(Long totalTokens) {
        setTotalTokens_MatchPhrase(totalTokens, null);
    }

    public void setTotalTokens_MatchPhrase(Long totalTokens, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("totalTokens", totalTokens);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_MatchPhrasePrefix(Long totalTokens) {
        setTotalTokens_MatchPhrasePrefix(totalTokens, null);
    }

    public void setTotalTokens_MatchPhrasePrefix(Long totalTokens, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("totalTokens", totalTokens);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_Fuzzy(Long totalTokens) {
        setTotalTokens_Fuzzy(totalTokens, null);
    }

    public void setTotalTokens_Fuzzy(Long totalTokens, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("totalTokens", totalTokens);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_GreaterThan(Long totalTokens) {
        setTotalTokens_GreaterThan(totalTokens, null);
    }

    public void setTotalTokens_GreaterThan(Long totalTokens, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = totalTokens;
        RangeQueryBuilder builder = regRangeQ("totalTokens", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_LessThan(Long totalTokens) {
        setTotalTokens_LessThan(totalTokens, null);
    }

    public void setTotalTokens_LessThan(Long totalTokens, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = totalTokens;
        RangeQueryBuilder builder = regRangeQ("totalTokens", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_GreaterEqual(Long totalTokens) {
        setTotalTokens_GreaterEqual(totalTokens, null);
    }

    public void setTotalTokens_GreaterEqual(Long totalTokens, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = totalTokens;
        RangeQueryBuilder builder = regRangeQ("totalTokens", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_LessEqual(Long totalTokens) {
        setTotalTokens_LessEqual(totalTokens, null);
    }

    public void setTotalTokens_LessEqual(Long totalTokens, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = totalTokens;
        RangeQueryBuilder builder = regRangeQ("totalTokens", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_Exists() {
        setTotalTokens_Exists(null);
    }

    public void setTotalTokens_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("totalTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setTotalTokens_CommonTerms(Long totalTokens) {
        setTotalTokens_CommonTerms(totalTokens, null);
    }

    @Deprecated
    public void setTotalTokens_CommonTerms(Long totalTokens, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("totalTokens", totalTokens);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsChatLogCQ addOrderBy_TotalTokens_Asc() {
        regOBA("totalTokens");
        return this;
    }

    public BsChatLogCQ addOrderBy_TotalTokens_Desc() {
        regOBD("totalTokens");
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

    public BsChatLogCQ addOrderBy_User_Asc() {
        regOBA("user");
        return this;
    }

    public BsChatLogCQ addOrderBy_User_Desc() {
        regOBD("user");
        return this;
    }

    public void setUserSessionId_Equal(String userSessionId) {
        setUserSessionId_Term(userSessionId, null);
    }

    public void setUserSessionId_Equal(String userSessionId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setUserSessionId_Term(userSessionId, opLambda);
    }

    public void setUserSessionId_Term(String userSessionId) {
        setUserSessionId_Term(userSessionId, null);
    }

    public void setUserSessionId_Term(String userSessionId, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_NotEqual(String userSessionId) {
        setUserSessionId_NotTerm(userSessionId, null);
    }

    public void setUserSessionId_NotTerm(String userSessionId) {
        setUserSessionId_NotTerm(userSessionId, null);
    }

    public void setUserSessionId_NotEqual(String userSessionId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setUserSessionId_NotTerm(userSessionId, opLambda);
    }

    public void setUserSessionId_NotTerm(String userSessionId, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setUserSessionId_Term(userSessionId), opLambda);
    }

    public void setUserSessionId_Terms(Collection<String> userSessionIdList) {
        setUserSessionId_Terms(userSessionIdList, null);
    }

    public void setUserSessionId_Terms(Collection<String> userSessionIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("userSessionId", userSessionIdList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_InScope(Collection<String> userSessionIdList) {
        setUserSessionId_Terms(userSessionIdList, null);
    }

    public void setUserSessionId_InScope(Collection<String> userSessionIdList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUserSessionId_Terms(userSessionIdList, opLambda);
    }

    public void setUserSessionId_Match(String userSessionId) {
        setUserSessionId_Match(userSessionId, null);
    }

    public void setUserSessionId_Match(String userSessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_MatchPhrase(String userSessionId) {
        setUserSessionId_MatchPhrase(userSessionId, null);
    }

    public void setUserSessionId_MatchPhrase(String userSessionId, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_MatchPhrasePrefix(String userSessionId) {
        setUserSessionId_MatchPhrasePrefix(userSessionId, null);
    }

    public void setUserSessionId_MatchPhrasePrefix(String userSessionId, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Fuzzy(String userSessionId) {
        setUserSessionId_Fuzzy(userSessionId, null);
    }

    public void setUserSessionId_Fuzzy(String userSessionId, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Prefix(String userSessionId) {
        setUserSessionId_Prefix(userSessionId, null);
    }

    public void setUserSessionId_Prefix(String userSessionId, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Wildcard(String userSessionId) {
        setUserSessionId_Wildcard(userSessionId, null);
    }

    public void setUserSessionId_Wildcard(String userSessionId, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Regexp(String userSessionId) {
        setUserSessionId_Regexp(userSessionId, null);
    }

    public void setUserSessionId_Regexp(String userSessionId, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_SpanTerm(String userSessionId) {
        setUserSessionId_SpanTerm("userSessionId", null);
    }

    public void setUserSessionId_SpanTerm(String userSessionId, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_GreaterThan(String userSessionId) {
        setUserSessionId_GreaterThan(userSessionId, null);
    }

    public void setUserSessionId_GreaterThan(String userSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userSessionId;
        RangeQueryBuilder builder = regRangeQ("userSessionId", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_LessThan(String userSessionId) {
        setUserSessionId_LessThan(userSessionId, null);
    }

    public void setUserSessionId_LessThan(String userSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userSessionId;
        RangeQueryBuilder builder = regRangeQ("userSessionId", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_GreaterEqual(String userSessionId) {
        setUserSessionId_GreaterEqual(userSessionId, null);
    }

    public void setUserSessionId_GreaterEqual(String userSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userSessionId;
        RangeQueryBuilder builder = regRangeQ("userSessionId", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_LessEqual(String userSessionId) {
        setUserSessionId_LessEqual(userSessionId, null);
    }

    public void setUserSessionId_LessEqual(String userSessionId, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = userSessionId;
        RangeQueryBuilder builder = regRangeQ("userSessionId", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Exists() {
        setUserSessionId_Exists(null);
    }

    public void setUserSessionId_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setUserSessionId_CommonTerms(String userSessionId) {
        setUserSessionId_CommonTerms(userSessionId, null);
    }

    @Deprecated
    public void setUserSessionId_CommonTerms(String userSessionId, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("userSessionId", userSessionId);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsChatLogCQ addOrderBy_UserSessionId_Asc() {
        regOBA("userSessionId");
        return this;
    }

    public BsChatLogCQ addOrderBy_UserSessionId_Desc() {
        regOBD("userSessionId");
        return this;
    }

    public void setVirtualHost_Equal(String virtualHost) {
        setVirtualHost_Term(virtualHost, null);
    }

    public void setVirtualHost_Equal(String virtualHost, ConditionOptionCall<TermQueryBuilder> opLambda) {
        setVirtualHost_Term(virtualHost, opLambda);
    }

    public void setVirtualHost_Term(String virtualHost) {
        setVirtualHost_Term(virtualHost, null);
    }

    public void setVirtualHost_Term(String virtualHost, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = regTermQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_NotEqual(String virtualHost) {
        setVirtualHost_NotTerm(virtualHost, null);
    }

    public void setVirtualHost_NotTerm(String virtualHost) {
        setVirtualHost_NotTerm(virtualHost, null);
    }

    public void setVirtualHost_NotEqual(String virtualHost, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        setVirtualHost_NotTerm(virtualHost, opLambda);
    }

    public void setVirtualHost_NotTerm(String virtualHost, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        not(not -> not.setVirtualHost_Term(virtualHost), opLambda);
    }

    public void setVirtualHost_Terms(Collection<String> virtualHostList) {
        setVirtualHost_Terms(virtualHostList, null);
    }

    public void setVirtualHost_Terms(Collection<String> virtualHostList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = regTermsQ("virtualHost", virtualHostList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_InScope(Collection<String> virtualHostList) {
        setVirtualHost_Terms(virtualHostList, null);
    }

    public void setVirtualHost_InScope(Collection<String> virtualHostList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setVirtualHost_Terms(virtualHostList, opLambda);
    }

    public void setVirtualHost_Match(String virtualHost) {
        setVirtualHost_Match(virtualHost, null);
    }

    public void setVirtualHost_Match(String virtualHost, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regMatchQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_MatchPhrase(String virtualHost) {
        setVirtualHost_MatchPhrase(virtualHost, null);
    }

    public void setVirtualHost_MatchPhrase(String virtualHost, ConditionOptionCall<MatchPhraseQueryBuilder> opLambda) {
        MatchPhraseQueryBuilder builder = regMatchPhraseQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_MatchPhrasePrefix(String virtualHost) {
        setVirtualHost_MatchPhrasePrefix(virtualHost, null);
    }

    public void setVirtualHost_MatchPhrasePrefix(String virtualHost, ConditionOptionCall<MatchPhrasePrefixQueryBuilder> opLambda) {
        MatchPhrasePrefixQueryBuilder builder = regMatchPhrasePrefixQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_Fuzzy(String virtualHost) {
        setVirtualHost_Fuzzy(virtualHost, null);
    }

    public void setVirtualHost_Fuzzy(String virtualHost, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = regFuzzyQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_Prefix(String virtualHost) {
        setVirtualHost_Prefix(virtualHost, null);
    }

    public void setVirtualHost_Prefix(String virtualHost, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = regPrefixQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_Wildcard(String virtualHost) {
        setVirtualHost_Wildcard(virtualHost, null);
    }

    public void setVirtualHost_Wildcard(String virtualHost, ConditionOptionCall<WildcardQueryBuilder> opLambda) {
        WildcardQueryBuilder builder = regWildcardQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_Regexp(String virtualHost) {
        setVirtualHost_Regexp(virtualHost, null);
    }

    public void setVirtualHost_Regexp(String virtualHost, ConditionOptionCall<RegexpQueryBuilder> opLambda) {
        RegexpQueryBuilder builder = regRegexpQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_SpanTerm(String virtualHost) {
        setVirtualHost_SpanTerm("virtualHost", null);
    }

    public void setVirtualHost_SpanTerm(String virtualHost, ConditionOptionCall<SpanTermQueryBuilder> opLambda) {
        SpanTermQueryBuilder builder = regSpanTermQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_GreaterThan(String virtualHost) {
        setVirtualHost_GreaterThan(virtualHost, null);
    }

    public void setVirtualHost_GreaterThan(String virtualHost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = virtualHost;
        RangeQueryBuilder builder = regRangeQ("virtualHost", ConditionKey.CK_GREATER_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_LessThan(String virtualHost) {
        setVirtualHost_LessThan(virtualHost, null);
    }

    public void setVirtualHost_LessThan(String virtualHost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = virtualHost;
        RangeQueryBuilder builder = regRangeQ("virtualHost", ConditionKey.CK_LESS_THAN, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_GreaterEqual(String virtualHost) {
        setVirtualHost_GreaterEqual(virtualHost, null);
    }

    public void setVirtualHost_GreaterEqual(String virtualHost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = virtualHost;
        RangeQueryBuilder builder = regRangeQ("virtualHost", ConditionKey.CK_GREATER_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_LessEqual(String virtualHost) {
        setVirtualHost_LessEqual(virtualHost, null);
    }

    public void setVirtualHost_LessEqual(String virtualHost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        final Object _value = virtualHost;
        RangeQueryBuilder builder = regRangeQ("virtualHost", ConditionKey.CK_LESS_EQUAL, _value);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_Exists() {
        setVirtualHost_Exists(null);
    }

    public void setVirtualHost_Exists(ConditionOptionCall<ExistsQueryBuilder> opLambda) {
        ExistsQueryBuilder builder = regExistsQ("virtualHost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    @Deprecated
    public void setVirtualHost_CommonTerms(String virtualHost) {
        setVirtualHost_CommonTerms(virtualHost, null);
    }

    @Deprecated
    public void setVirtualHost_CommonTerms(String virtualHost, ConditionOptionCall<CommonTermsQueryBuilder> opLambda) {
        CommonTermsQueryBuilder builder = regCommonTermsQ("virtualHost", virtualHost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsChatLogCQ addOrderBy_VirtualHost_Asc() {
        regOBA("virtualHost");
        return this;
    }

    public BsChatLogCQ addOrderBy_VirtualHost_Desc() {
        regOBD("virtualHost");
        return this;
    }

}
