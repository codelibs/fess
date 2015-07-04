package org.codelibs.fess.es.cbean.cq.bs;

import java.util.Collection;

import org.codelibs.fess.es.cbean.cq.SuggestElevateWordCQ;
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
 * @author FreeGen
 */
public abstract class BsSuggestElevateWordCQ extends AbstractConditionQuery {

    @Override
    public String asTableDbName() {
        return "suggest_elevate_word";
    }

    @Override
    public String xgetAliasName() {
        return "suggest_elevate_word";
    }

    public void filtered(FilteredCall<SuggestElevateWordCQ> filteredLambda) {
        filtered(filteredLambda, null);
    }

    public void filtered(FilteredCall<SuggestElevateWordCQ> filteredLambda, ConditionOptionCall<FilteredQueryBuilder> opLambda) {
        SuggestElevateWordCQ query = new SuggestElevateWordCQ();
        filteredLambda.callback(query);
        if (!query.queryBuilderList.isEmpty()) {
            // TODO filter
            FilteredQueryBuilder builder = reqFilteredQ(query.getQuery(), null);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void bool(BoolCall<SuggestElevateWordCQ> boolLambda) {
        bool(boolLambda, null);
    }

    public void bool(BoolCall<SuggestElevateWordCQ> boolLambda, ConditionOptionCall<BoolQueryBuilder> opLambda) {
        SuggestElevateWordCQ mustQuery = new SuggestElevateWordCQ();
        SuggestElevateWordCQ shouldQuery = new SuggestElevateWordCQ();
        SuggestElevateWordCQ mustNotQuery = new SuggestElevateWordCQ();
        boolLambda.callback(mustQuery, shouldQuery, mustNotQuery);
        if (!mustQuery.queryBuilderList.isEmpty() || !shouldQuery.queryBuilderList.isEmpty() || !mustNotQuery.queryBuilderList.isEmpty()) {
            BoolQueryBuilder builder = reqBoolCQ(mustQuery.queryBuilderList, shouldQuery.queryBuilderList, mustNotQuery.queryBuilderList);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
        }
    }

    public void setBoost_Term(Float boost) {
        setBoost_Term(boost, null);
    }

    public void setBoost_Term(Float boost, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("boost", boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Terms(Collection<Float> boostList) {
        setBoost_MatchPhrasePrefix(boostList, null);
    }

    public void setBoost_MatchPhrasePrefix(Collection<Float> boostList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("boost", boostList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_InScope(Collection<Float> boostList) {
        setBoost_MatchPhrasePrefix(boostList, null);
    }

    public void setBoost_InScope(Collection<Float> boostList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setBoost_MatchPhrasePrefix(boostList, opLambda);
    }

    public void setBoost_Match(Float boost) {
        setBoost_Match(boost, null);
    }

    public void setBoost_Match(Float boost, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("boost", boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_MatchPhrase(Float boost) {
        setBoost_MatchPhrase(boost, null);
    }

    public void setBoost_MatchPhrase(Float boost, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("boost", boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_MatchPhrasePrefix(Float boost) {
        setBoost_MatchPhrasePrefix(boost, null);
    }

    public void setBoost_MatchPhrasePrefix(Float boost, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("boost", boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Fuzzy(Float boost) {
        setBoost_Fuzzy(boost, null);
    }

    public void setBoost_Fuzzy(Float boost, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("boost", boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_GreaterThan(Float boost) {
        setBoost_GreaterThan(boost, null);
    }

    public void setBoost_GreaterThan(Float boost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("boost", ConditionKey.CK_GREATER_THAN, boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_LessThan(Float boost) {
        setBoost_LessThan(boost, null);
    }

    public void setBoost_LessThan(Float boost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("boost", ConditionKey.CK_LESS_THAN, boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_GreaterEqual(Float boost) {
        setBoost_GreaterEqual(boost, null);
    }

    public void setBoost_GreaterEqual(Float boost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("boost", ConditionKey.CK_GREATER_EQUAL, boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_LessEqual(Float boost) {
        setBoost_LessEqual(boost, null);
    }

    public void setBoost_LessEqual(Float boost, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("boost", ConditionKey.CK_LESS_EQUAL, boost);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSuggestElevateWordCQ addOrderBy_Boost_Asc() {
        regOBA("boost");
        return this;
    }

    public BsSuggestElevateWordCQ addOrderBy_Boost_Desc() {
        regOBD("boost");
        return this;
    }

    public void setCreatedBy_Term(String createdBy) {
        setCreatedBy_Term(createdBy, null);
    }

    public void setCreatedBy_Term(String createdBy, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Terms(Collection<String> createdByList) {
        setCreatedBy_MatchPhrasePrefix(createdByList, null);
    }

    public void setCreatedBy_MatchPhrasePrefix(Collection<String> createdByList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("createdBy", createdByList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_InScope(Collection<String> createdByList) {
        setCreatedBy_MatchPhrasePrefix(createdByList, null);
    }

    public void setCreatedBy_InScope(Collection<String> createdByList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setCreatedBy_MatchPhrasePrefix(createdByList, opLambda);
    }

    public void setCreatedBy_Match(String createdBy) {
        setCreatedBy_Match(createdBy, null);
    }

    public void setCreatedBy_Match(String createdBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_MatchPhrase(String createdBy) {
        setCreatedBy_MatchPhrase(createdBy, null);
    }

    public void setCreatedBy_MatchPhrase(String createdBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_MatchPhrasePrefix(String createdBy) {
        setCreatedBy_MatchPhrasePrefix(createdBy, null);
    }

    public void setCreatedBy_MatchPhrasePrefix(String createdBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Fuzzy(String createdBy) {
        setCreatedBy_Fuzzy(createdBy, null);
    }

    public void setCreatedBy_Fuzzy(String createdBy, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Prefix(String createdBy) {
        setCreatedBy_Prefix(createdBy, null);
    }

    public void setCreatedBy_Prefix(String createdBy, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("createdBy", createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_GreaterThan(String createdBy) {
        setCreatedBy_GreaterThan(createdBy, null);
    }

    public void setCreatedBy_GreaterThan(String createdBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("createdBy", ConditionKey.CK_GREATER_THAN, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_LessThan(String createdBy) {
        setCreatedBy_LessThan(createdBy, null);
    }

    public void setCreatedBy_LessThan(String createdBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("createdBy", ConditionKey.CK_LESS_THAN, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_GreaterEqual(String createdBy) {
        setCreatedBy_GreaterEqual(createdBy, null);
    }

    public void setCreatedBy_GreaterEqual(String createdBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("createdBy", ConditionKey.CK_GREATER_EQUAL, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_LessEqual(String createdBy) {
        setCreatedBy_LessEqual(createdBy, null);
    }

    public void setCreatedBy_LessEqual(String createdBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("createdBy", ConditionKey.CK_LESS_EQUAL, createdBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSuggestElevateWordCQ addOrderBy_CreatedBy_Asc() {
        regOBA("createdBy");
        return this;
    }

    public BsSuggestElevateWordCQ addOrderBy_CreatedBy_Desc() {
        regOBD("createdBy");
        return this;
    }

    public void setCreatedTime_Term(Long createdTime) {
        setCreatedTime_Term(createdTime, null);
    }

    public void setCreatedTime_Term(Long createdTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Terms(Collection<Long> createdTimeList) {
        setCreatedTime_MatchPhrasePrefix(createdTimeList, null);
    }

    public void setCreatedTime_MatchPhrasePrefix(Collection<Long> createdTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("createdTime", createdTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_InScope(Collection<Long> createdTimeList) {
        setCreatedTime_MatchPhrasePrefix(createdTimeList, null);
    }

    public void setCreatedTime_InScope(Collection<Long> createdTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setCreatedTime_MatchPhrasePrefix(createdTimeList, opLambda);
    }

    public void setCreatedTime_Match(Long createdTime) {
        setCreatedTime_Match(createdTime, null);
    }

    public void setCreatedTime_Match(Long createdTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_MatchPhrase(Long createdTime) {
        setCreatedTime_MatchPhrase(createdTime, null);
    }

    public void setCreatedTime_MatchPhrase(Long createdTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_MatchPhrasePrefix(Long createdTime) {
        setCreatedTime_MatchPhrasePrefix(createdTime, null);
    }

    public void setCreatedTime_MatchPhrasePrefix(Long createdTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Fuzzy(Long createdTime) {
        setCreatedTime_Fuzzy(createdTime, null);
    }

    public void setCreatedTime_Fuzzy(Long createdTime, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("createdTime", createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_GreaterThan(Long createdTime) {
        setCreatedTime_GreaterThan(createdTime, null);
    }

    public void setCreatedTime_GreaterThan(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("createdTime", ConditionKey.CK_GREATER_THAN, createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_LessThan(Long createdTime) {
        setCreatedTime_LessThan(createdTime, null);
    }

    public void setCreatedTime_LessThan(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("createdTime", ConditionKey.CK_LESS_THAN, createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_GreaterEqual(Long createdTime) {
        setCreatedTime_GreaterEqual(createdTime, null);
    }

    public void setCreatedTime_GreaterEqual(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("createdTime", ConditionKey.CK_GREATER_EQUAL, createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_LessEqual(Long createdTime) {
        setCreatedTime_LessEqual(createdTime, null);
    }

    public void setCreatedTime_LessEqual(Long createdTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("createdTime", ConditionKey.CK_LESS_EQUAL, createdTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSuggestElevateWordCQ addOrderBy_CreatedTime_Asc() {
        regOBA("createdTime");
        return this;
    }

    public BsSuggestElevateWordCQ addOrderBy_CreatedTime_Desc() {
        regOBD("createdTime");
        return this;
    }

    public void setId_Term(String id) {
        setId_Term(id, null);
    }

    public void setId_Term(String id, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Terms(Collection<String> idList) {
        setId_MatchPhrasePrefix(idList, null);
    }

    public void setId_MatchPhrasePrefix(Collection<String> idList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("id", idList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_InScope(Collection<String> idList) {
        setId_MatchPhrasePrefix(idList, null);
    }

    public void setId_InScope(Collection<String> idList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setId_MatchPhrasePrefix(idList, opLambda);
    }

    public void setId_Match(String id) {
        setId_Match(id, null);
    }

    public void setId_Match(String id, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_MatchPhrase(String id) {
        setId_MatchPhrase(id, null);
    }

    public void setId_MatchPhrase(String id, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_MatchPhrasePrefix(String id) {
        setId_MatchPhrasePrefix(id, null);
    }

    public void setId_MatchPhrasePrefix(String id, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Fuzzy(String id) {
        setId_Fuzzy(id, null);
    }

    public void setId_Fuzzy(String id, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_Prefix(String id) {
        setId_Prefix(id, null);
    }

    public void setId_Prefix(String id, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("id", id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_GreaterThan(String id) {
        setId_GreaterThan(id, null);
    }

    public void setId_GreaterThan(String id, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("id", ConditionKey.CK_GREATER_THAN, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_LessThan(String id) {
        setId_LessThan(id, null);
    }

    public void setId_LessThan(String id, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("id", ConditionKey.CK_LESS_THAN, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_GreaterEqual(String id) {
        setId_GreaterEqual(id, null);
    }

    public void setId_GreaterEqual(String id, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("id", ConditionKey.CK_GREATER_EQUAL, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setId_LessEqual(String id) {
        setId_LessEqual(id, null);
    }

    public void setId_LessEqual(String id, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("id", ConditionKey.CK_LESS_EQUAL, id);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSuggestElevateWordCQ addOrderBy_Id_Asc() {
        regOBA("id");
        return this;
    }

    public BsSuggestElevateWordCQ addOrderBy_Id_Desc() {
        regOBD("id");
        return this;
    }

    public void setReading_Term(String reading) {
        setReading_Term(reading, null);
    }

    public void setReading_Term(String reading, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("reading", reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_Terms(Collection<String> readingList) {
        setReading_MatchPhrasePrefix(readingList, null);
    }

    public void setReading_MatchPhrasePrefix(Collection<String> readingList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("reading", readingList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_InScope(Collection<String> readingList) {
        setReading_MatchPhrasePrefix(readingList, null);
    }

    public void setReading_InScope(Collection<String> readingList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setReading_MatchPhrasePrefix(readingList, opLambda);
    }

    public void setReading_Match(String reading) {
        setReading_Match(reading, null);
    }

    public void setReading_Match(String reading, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("reading", reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_MatchPhrase(String reading) {
        setReading_MatchPhrase(reading, null);
    }

    public void setReading_MatchPhrase(String reading, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("reading", reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_MatchPhrasePrefix(String reading) {
        setReading_MatchPhrasePrefix(reading, null);
    }

    public void setReading_MatchPhrasePrefix(String reading, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("reading", reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_Fuzzy(String reading) {
        setReading_Fuzzy(reading, null);
    }

    public void setReading_Fuzzy(String reading, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("reading", reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_Prefix(String reading) {
        setReading_Prefix(reading, null);
    }

    public void setReading_Prefix(String reading, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("reading", reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_GreaterThan(String reading) {
        setReading_GreaterThan(reading, null);
    }

    public void setReading_GreaterThan(String reading, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("reading", ConditionKey.CK_GREATER_THAN, reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_LessThan(String reading) {
        setReading_LessThan(reading, null);
    }

    public void setReading_LessThan(String reading, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("reading", ConditionKey.CK_LESS_THAN, reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_GreaterEqual(String reading) {
        setReading_GreaterEqual(reading, null);
    }

    public void setReading_GreaterEqual(String reading, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("reading", ConditionKey.CK_GREATER_EQUAL, reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_LessEqual(String reading) {
        setReading_LessEqual(reading, null);
    }

    public void setReading_LessEqual(String reading, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("reading", ConditionKey.CK_LESS_EQUAL, reading);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSuggestElevateWordCQ addOrderBy_Reading_Asc() {
        regOBA("reading");
        return this;
    }

    public BsSuggestElevateWordCQ addOrderBy_Reading_Desc() {
        regOBD("reading");
        return this;
    }

    public void setSuggestWord_Term(String suggestWord) {
        setSuggestWord_Term(suggestWord, null);
    }

    public void setSuggestWord_Term(String suggestWord, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("suggestWord", suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_Terms(Collection<String> suggestWordList) {
        setSuggestWord_MatchPhrasePrefix(suggestWordList, null);
    }

    public void setSuggestWord_MatchPhrasePrefix(Collection<String> suggestWordList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("suggestWord", suggestWordList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_InScope(Collection<String> suggestWordList) {
        setSuggestWord_MatchPhrasePrefix(suggestWordList, null);
    }

    public void setSuggestWord_InScope(Collection<String> suggestWordList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setSuggestWord_MatchPhrasePrefix(suggestWordList, opLambda);
    }

    public void setSuggestWord_Match(String suggestWord) {
        setSuggestWord_Match(suggestWord, null);
    }

    public void setSuggestWord_Match(String suggestWord, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("suggestWord", suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_MatchPhrase(String suggestWord) {
        setSuggestWord_MatchPhrase(suggestWord, null);
    }

    public void setSuggestWord_MatchPhrase(String suggestWord, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("suggestWord", suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_MatchPhrasePrefix(String suggestWord) {
        setSuggestWord_MatchPhrasePrefix(suggestWord, null);
    }

    public void setSuggestWord_MatchPhrasePrefix(String suggestWord, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("suggestWord", suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_Fuzzy(String suggestWord) {
        setSuggestWord_Fuzzy(suggestWord, null);
    }

    public void setSuggestWord_Fuzzy(String suggestWord, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("suggestWord", suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_Prefix(String suggestWord) {
        setSuggestWord_Prefix(suggestWord, null);
    }

    public void setSuggestWord_Prefix(String suggestWord, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("suggestWord", suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_GreaterThan(String suggestWord) {
        setSuggestWord_GreaterThan(suggestWord, null);
    }

    public void setSuggestWord_GreaterThan(String suggestWord, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("suggestWord", ConditionKey.CK_GREATER_THAN, suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_LessThan(String suggestWord) {
        setSuggestWord_LessThan(suggestWord, null);
    }

    public void setSuggestWord_LessThan(String suggestWord, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("suggestWord", ConditionKey.CK_LESS_THAN, suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_GreaterEqual(String suggestWord) {
        setSuggestWord_GreaterEqual(suggestWord, null);
    }

    public void setSuggestWord_GreaterEqual(String suggestWord, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("suggestWord", ConditionKey.CK_GREATER_EQUAL, suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_LessEqual(String suggestWord) {
        setSuggestWord_LessEqual(suggestWord, null);
    }

    public void setSuggestWord_LessEqual(String suggestWord, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("suggestWord", ConditionKey.CK_LESS_EQUAL, suggestWord);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSuggestElevateWordCQ addOrderBy_SuggestWord_Asc() {
        regOBA("suggestWord");
        return this;
    }

    public BsSuggestElevateWordCQ addOrderBy_SuggestWord_Desc() {
        regOBD("suggestWord");
        return this;
    }

    public void setTargetLabel_Term(String targetLabel) {
        setTargetLabel_Term(targetLabel, null);
    }

    public void setTargetLabel_Term(String targetLabel, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("targetLabel", targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_Terms(Collection<String> targetLabelList) {
        setTargetLabel_MatchPhrasePrefix(targetLabelList, null);
    }

    public void setTargetLabel_MatchPhrasePrefix(Collection<String> targetLabelList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("targetLabel", targetLabelList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_InScope(Collection<String> targetLabelList) {
        setTargetLabel_MatchPhrasePrefix(targetLabelList, null);
    }

    public void setTargetLabel_InScope(Collection<String> targetLabelList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setTargetLabel_MatchPhrasePrefix(targetLabelList, opLambda);
    }

    public void setTargetLabel_Match(String targetLabel) {
        setTargetLabel_Match(targetLabel, null);
    }

    public void setTargetLabel_Match(String targetLabel, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("targetLabel", targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_MatchPhrase(String targetLabel) {
        setTargetLabel_MatchPhrase(targetLabel, null);
    }

    public void setTargetLabel_MatchPhrase(String targetLabel, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("targetLabel", targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_MatchPhrasePrefix(String targetLabel) {
        setTargetLabel_MatchPhrasePrefix(targetLabel, null);
    }

    public void setTargetLabel_MatchPhrasePrefix(String targetLabel, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("targetLabel", targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_Fuzzy(String targetLabel) {
        setTargetLabel_Fuzzy(targetLabel, null);
    }

    public void setTargetLabel_Fuzzy(String targetLabel, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("targetLabel", targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_Prefix(String targetLabel) {
        setTargetLabel_Prefix(targetLabel, null);
    }

    public void setTargetLabel_Prefix(String targetLabel, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("targetLabel", targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_GreaterThan(String targetLabel) {
        setTargetLabel_GreaterThan(targetLabel, null);
    }

    public void setTargetLabel_GreaterThan(String targetLabel, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("targetLabel", ConditionKey.CK_GREATER_THAN, targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_LessThan(String targetLabel) {
        setTargetLabel_LessThan(targetLabel, null);
    }

    public void setTargetLabel_LessThan(String targetLabel, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("targetLabel", ConditionKey.CK_LESS_THAN, targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_GreaterEqual(String targetLabel) {
        setTargetLabel_GreaterEqual(targetLabel, null);
    }

    public void setTargetLabel_GreaterEqual(String targetLabel, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("targetLabel", ConditionKey.CK_GREATER_EQUAL, targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetLabel_LessEqual(String targetLabel) {
        setTargetLabel_LessEqual(targetLabel, null);
    }

    public void setTargetLabel_LessEqual(String targetLabel, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("targetLabel", ConditionKey.CK_LESS_EQUAL, targetLabel);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSuggestElevateWordCQ addOrderBy_TargetLabel_Asc() {
        regOBA("targetLabel");
        return this;
    }

    public BsSuggestElevateWordCQ addOrderBy_TargetLabel_Desc() {
        regOBD("targetLabel");
        return this;
    }

    public void setTargetRole_Term(String targetRole) {
        setTargetRole_Term(targetRole, null);
    }

    public void setTargetRole_Term(String targetRole, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("targetRole", targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_Terms(Collection<String> targetRoleList) {
        setTargetRole_MatchPhrasePrefix(targetRoleList, null);
    }

    public void setTargetRole_MatchPhrasePrefix(Collection<String> targetRoleList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("targetRole", targetRoleList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_InScope(Collection<String> targetRoleList) {
        setTargetRole_MatchPhrasePrefix(targetRoleList, null);
    }

    public void setTargetRole_InScope(Collection<String> targetRoleList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setTargetRole_MatchPhrasePrefix(targetRoleList, opLambda);
    }

    public void setTargetRole_Match(String targetRole) {
        setTargetRole_Match(targetRole, null);
    }

    public void setTargetRole_Match(String targetRole, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("targetRole", targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_MatchPhrase(String targetRole) {
        setTargetRole_MatchPhrase(targetRole, null);
    }

    public void setTargetRole_MatchPhrase(String targetRole, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("targetRole", targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_MatchPhrasePrefix(String targetRole) {
        setTargetRole_MatchPhrasePrefix(targetRole, null);
    }

    public void setTargetRole_MatchPhrasePrefix(String targetRole, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("targetRole", targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_Fuzzy(String targetRole) {
        setTargetRole_Fuzzy(targetRole, null);
    }

    public void setTargetRole_Fuzzy(String targetRole, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("targetRole", targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_Prefix(String targetRole) {
        setTargetRole_Prefix(targetRole, null);
    }

    public void setTargetRole_Prefix(String targetRole, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("targetRole", targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_GreaterThan(String targetRole) {
        setTargetRole_GreaterThan(targetRole, null);
    }

    public void setTargetRole_GreaterThan(String targetRole, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("targetRole", ConditionKey.CK_GREATER_THAN, targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_LessThan(String targetRole) {
        setTargetRole_LessThan(targetRole, null);
    }

    public void setTargetRole_LessThan(String targetRole, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("targetRole", ConditionKey.CK_LESS_THAN, targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_GreaterEqual(String targetRole) {
        setTargetRole_GreaterEqual(targetRole, null);
    }

    public void setTargetRole_GreaterEqual(String targetRole, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("targetRole", ConditionKey.CK_GREATER_EQUAL, targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTargetRole_LessEqual(String targetRole) {
        setTargetRole_LessEqual(targetRole, null);
    }

    public void setTargetRole_LessEqual(String targetRole, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("targetRole", ConditionKey.CK_LESS_EQUAL, targetRole);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSuggestElevateWordCQ addOrderBy_TargetRole_Asc() {
        regOBA("targetRole");
        return this;
    }

    public BsSuggestElevateWordCQ addOrderBy_TargetRole_Desc() {
        regOBD("targetRole");
        return this;
    }

    public void setUpdatedBy_Term(String updatedBy) {
        setUpdatedBy_Term(updatedBy, null);
    }

    public void setUpdatedBy_Term(String updatedBy, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Terms(Collection<String> updatedByList) {
        setUpdatedBy_MatchPhrasePrefix(updatedByList, null);
    }

    public void setUpdatedBy_MatchPhrasePrefix(Collection<String> updatedByList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("updatedBy", updatedByList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_InScope(Collection<String> updatedByList) {
        setUpdatedBy_MatchPhrasePrefix(updatedByList, null);
    }

    public void setUpdatedBy_InScope(Collection<String> updatedByList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUpdatedBy_MatchPhrasePrefix(updatedByList, opLambda);
    }

    public void setUpdatedBy_Match(String updatedBy) {
        setUpdatedBy_Match(updatedBy, null);
    }

    public void setUpdatedBy_Match(String updatedBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_MatchPhrase(String updatedBy) {
        setUpdatedBy_MatchPhrase(updatedBy, null);
    }

    public void setUpdatedBy_MatchPhrase(String updatedBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_MatchPhrasePrefix(String updatedBy) {
        setUpdatedBy_MatchPhrasePrefix(updatedBy, null);
    }

    public void setUpdatedBy_MatchPhrasePrefix(String updatedBy, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Fuzzy(String updatedBy) {
        setUpdatedBy_Fuzzy(updatedBy, null);
    }

    public void setUpdatedBy_Fuzzy(String updatedBy, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Prefix(String updatedBy) {
        setUpdatedBy_Prefix(updatedBy, null);
    }

    public void setUpdatedBy_Prefix(String updatedBy, ConditionOptionCall<PrefixQueryBuilder> opLambda) {
        PrefixQueryBuilder builder = reqPrefixQ("updatedBy", updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_GreaterThan(String updatedBy) {
        setUpdatedBy_GreaterThan(updatedBy, null);
    }

    public void setUpdatedBy_GreaterThan(String updatedBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("updatedBy", ConditionKey.CK_GREATER_THAN, updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_LessThan(String updatedBy) {
        setUpdatedBy_LessThan(updatedBy, null);
    }

    public void setUpdatedBy_LessThan(String updatedBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("updatedBy", ConditionKey.CK_LESS_THAN, updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_GreaterEqual(String updatedBy) {
        setUpdatedBy_GreaterEqual(updatedBy, null);
    }

    public void setUpdatedBy_GreaterEqual(String updatedBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("updatedBy", ConditionKey.CK_GREATER_EQUAL, updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_LessEqual(String updatedBy) {
        setUpdatedBy_LessEqual(updatedBy, null);
    }

    public void setUpdatedBy_LessEqual(String updatedBy, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("updatedBy", ConditionKey.CK_LESS_EQUAL, updatedBy);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSuggestElevateWordCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("updatedBy");
        return this;
    }

    public BsSuggestElevateWordCQ addOrderBy_UpdatedBy_Desc() {
        regOBD("updatedBy");
        return this;
    }

    public void setUpdatedTime_Term(Long updatedTime) {
        setUpdatedTime_Term(updatedTime, null);
    }

    public void setUpdatedTime_Term(Long updatedTime, ConditionOptionCall<TermQueryBuilder> opLambda) {
        TermQueryBuilder builder = reqTermQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Terms(Collection<Long> updatedTimeList) {
        setUpdatedTime_MatchPhrasePrefix(updatedTimeList, null);
    }

    public void setUpdatedTime_MatchPhrasePrefix(Collection<Long> updatedTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        TermsQueryBuilder builder = reqTermsQ("updatedTime", updatedTimeList);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_InScope(Collection<Long> updatedTimeList) {
        setUpdatedTime_MatchPhrasePrefix(updatedTimeList, null);
    }

    public void setUpdatedTime_InScope(Collection<Long> updatedTimeList, ConditionOptionCall<TermsQueryBuilder> opLambda) {
        setUpdatedTime_MatchPhrasePrefix(updatedTimeList, opLambda);
    }

    public void setUpdatedTime_Match(Long updatedTime) {
        setUpdatedTime_Match(updatedTime, null);
    }

    public void setUpdatedTime_Match(Long updatedTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_MatchPhrase(Long updatedTime) {
        setUpdatedTime_MatchPhrase(updatedTime, null);
    }

    public void setUpdatedTime_MatchPhrase(Long updatedTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhraseQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_MatchPhrasePrefix(Long updatedTime) {
        setUpdatedTime_MatchPhrasePrefix(updatedTime, null);
    }

    public void setUpdatedTime_MatchPhrasePrefix(Long updatedTime, ConditionOptionCall<MatchQueryBuilder> opLambda) {
        MatchQueryBuilder builder = reqMatchPhrasePrefixQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Fuzzy(Long updatedTime) {
        setUpdatedTime_Fuzzy(updatedTime, null);
    }

    public void setUpdatedTime_Fuzzy(Long updatedTime, ConditionOptionCall<FuzzyQueryBuilder> opLambda) {
        FuzzyQueryBuilder builder = reqFuzzyQ("updatedTime", updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_GreaterThan(Long updatedTime) {
        setUpdatedTime_GreaterThan(updatedTime, null);
    }

    public void setUpdatedTime_GreaterThan(Long updatedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("updatedTime", ConditionKey.CK_GREATER_THAN, updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_LessThan(Long updatedTime) {
        setUpdatedTime_LessThan(updatedTime, null);
    }

    public void setUpdatedTime_LessThan(Long updatedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("updatedTime", ConditionKey.CK_LESS_THAN, updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_GreaterEqual(Long updatedTime) {
        setUpdatedTime_GreaterEqual(updatedTime, null);
    }

    public void setUpdatedTime_GreaterEqual(Long updatedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("updatedTime", ConditionKey.CK_GREATER_EQUAL, updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_LessEqual(Long updatedTime) {
        setUpdatedTime_LessEqual(updatedTime, null);
    }

    public void setUpdatedTime_LessEqual(Long updatedTime, ConditionOptionCall<RangeQueryBuilder> opLambda) {
        RangeQueryBuilder builder = reqRangeQ("updatedTime", ConditionKey.CK_LESS_EQUAL, updatedTime);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public BsSuggestElevateWordCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("updatedTime");
        return this;
    }

    public BsSuggestElevateWordCQ addOrderBy_UpdatedTime_Desc() {
        regOBD("updatedTime");
        return this;
    }

}
