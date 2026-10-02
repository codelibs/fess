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
package org.codelibs.fess.opensearch.log.cbean.ca.bs;

import org.codelibs.fess.opensearch.log.allcommon.EsAbstractConditionAggregation;
import org.codelibs.fess.opensearch.log.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.opensearch.log.cbean.ca.ChatLogCA;
import org.codelibs.fess.opensearch.log.cbean.cq.ChatLogCQ;
import org.codelibs.fess.opensearch.log.cbean.cq.bs.BsChatLogCQ;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.global.GlobalAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.histogram.HistogramAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.missing.MissingAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.range.DateRangeAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.range.IpRangeAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.sampler.SamplerAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.terms.SignificantTermsAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.CardinalityAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.ExtendedStatsAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.MaxAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.MinAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.PercentileRanksAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.PercentilesAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.ScriptedMetricAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.StatsAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.SumAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.ValueCountAggregationBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsChatLogCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsChatLogCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        ChatLogCQ cq = new ChatLogCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void scriptedMetric(String name, ConditionOptionCall<ScriptedMetricAggregationBuilder> opLambda) {
        ScriptedMetricAggregationBuilder builder = regScriptedMetricA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void topHits(String name, ConditionOptionCall<TopHitsAggregationBuilder> opLambda) {
        TopHitsAggregationBuilder builder = regTopHitsA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Terms() {
        setAccessType_Terms(null);
    }

    public void setAccessType_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setAccessType_Terms("accessType", opLambda, null);
    }

    public void setAccessType_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setAccessType_Terms("accessType", opLambda, aggsLambda);
    }

    public void setAccessType_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "accessType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setAccessType_SignificantTerms() {
        setAccessType_SignificantTerms(null);
    }

    public void setAccessType_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setAccessType_SignificantTerms("accessType", opLambda, null);
    }

    public void setAccessType_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        setAccessType_SignificantTerms("accessType", opLambda, aggsLambda);
    }

    public void setAccessType_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "accessType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setAccessType_IpRange() {
        setAccessType_IpRange(null);
    }

    public void setAccessType_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setAccessType_IpRange("accessType", opLambda, null);
    }

    public void setAccessType_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setAccessType_IpRange("accessType", opLambda, aggsLambda);
    }

    public void setAccessType_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "accessType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setAccessType_Count() {
        setAccessType_Count(null);
    }

    public void setAccessType_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setAccessType_Count("accessType", opLambda);
    }

    public void setAccessType_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "accessType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Cardinality() {
        setAccessType_Cardinality(null);
    }

    public void setAccessType_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setAccessType_Cardinality("accessType", opLambda);
    }

    public void setAccessType_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "accessType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Missing() {
        setAccessType_Missing(null);
    }

    public void setAccessType_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setAccessType_Missing("accessType", opLambda, null);
    }

    public void setAccessType_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setAccessType_Missing("accessType", opLambda, aggsLambda);
    }

    public void setAccessType_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "accessType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setChatSessionId_Terms() {
        setChatSessionId_Terms(null);
    }

    public void setChatSessionId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setChatSessionId_Terms("chatSessionId", opLambda, null);
    }

    public void setChatSessionId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setChatSessionId_Terms("chatSessionId", opLambda, aggsLambda);
    }

    public void setChatSessionId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "chatSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setChatSessionId_SignificantTerms() {
        setChatSessionId_SignificantTerms(null);
    }

    public void setChatSessionId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setChatSessionId_SignificantTerms("chatSessionId", opLambda, null);
    }

    public void setChatSessionId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        setChatSessionId_SignificantTerms("chatSessionId", opLambda, aggsLambda);
    }

    public void setChatSessionId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "chatSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setChatSessionId_IpRange() {
        setChatSessionId_IpRange(null);
    }

    public void setChatSessionId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setChatSessionId_IpRange("chatSessionId", opLambda, null);
    }

    public void setChatSessionId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setChatSessionId_IpRange("chatSessionId", opLambda, aggsLambda);
    }

    public void setChatSessionId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "chatSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setChatSessionId_Count() {
        setChatSessionId_Count(null);
    }

    public void setChatSessionId_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setChatSessionId_Count("chatSessionId", opLambda);
    }

    public void setChatSessionId_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "chatSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatSessionId_Cardinality() {
        setChatSessionId_Cardinality(null);
    }

    public void setChatSessionId_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setChatSessionId_Cardinality("chatSessionId", opLambda);
    }

    public void setChatSessionId_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "chatSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatSessionId_Missing() {
        setChatSessionId_Missing(null);
    }

    public void setChatSessionId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setChatSessionId_Missing("chatSessionId", opLambda, null);
    }

    public void setChatSessionId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setChatSessionId_Missing("chatSessionId", opLambda, aggsLambda);
    }

    public void setChatSessionId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "chatSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setChatType_Terms() {
        setChatType_Terms(null);
    }

    public void setChatType_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setChatType_Terms("chatType", opLambda, null);
    }

    public void setChatType_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setChatType_Terms("chatType", opLambda, aggsLambda);
    }

    public void setChatType_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "chatType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setChatType_SignificantTerms() {
        setChatType_SignificantTerms(null);
    }

    public void setChatType_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setChatType_SignificantTerms("chatType", opLambda, null);
    }

    public void setChatType_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        setChatType_SignificantTerms("chatType", opLambda, aggsLambda);
    }

    public void setChatType_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "chatType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setChatType_IpRange() {
        setChatType_IpRange(null);
    }

    public void setChatType_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setChatType_IpRange("chatType", opLambda, null);
    }

    public void setChatType_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setChatType_IpRange("chatType", opLambda, aggsLambda);
    }

    public void setChatType_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "chatType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setChatType_Count() {
        setChatType_Count(null);
    }

    public void setChatType_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setChatType_Count("chatType", opLambda);
    }

    public void setChatType_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "chatType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatType_Cardinality() {
        setChatType_Cardinality(null);
    }

    public void setChatType_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setChatType_Cardinality("chatType", opLambda);
    }

    public void setChatType_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "chatType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setChatType_Missing() {
        setChatType_Missing(null);
    }

    public void setChatType_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setChatType_Missing("chatType", opLambda, null);
    }

    public void setChatType_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setChatType_Missing("chatType", opLambda, aggsLambda);
    }

    public void setChatType_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "chatType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCompletionTokens_Avg() {
        setCompletionTokens_Avg(null);
    }

    public void setCompletionTokens_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setCompletionTokens_Avg("completionTokens", opLambda);
    }

    public void setCompletionTokens_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "completionTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_Max() {
        setCompletionTokens_Max(null);
    }

    public void setCompletionTokens_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setCompletionTokens_Max("completionTokens", opLambda);
    }

    public void setCompletionTokens_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "completionTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_Min() {
        setCompletionTokens_Min(null);
    }

    public void setCompletionTokens_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setCompletionTokens_Min("completionTokens", opLambda);
    }

    public void setCompletionTokens_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "completionTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_Sum() {
        setCompletionTokens_Sum(null);
    }

    public void setCompletionTokens_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setCompletionTokens_Sum("completionTokens", opLambda);
    }

    public void setCompletionTokens_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "completionTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_ExtendedStats() {
        setCompletionTokens_ExtendedStats(null);
    }

    public void setCompletionTokens_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setCompletionTokens_ExtendedStats("completionTokens", opLambda);
    }

    public void setCompletionTokens_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "completionTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_Stats() {
        setCompletionTokens_Stats(null);
    }

    public void setCompletionTokens_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setCompletionTokens_Stats("completionTokens", opLambda);
    }

    public void setCompletionTokens_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "completionTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_Percentiles() {
        setCompletionTokens_Percentiles(null);
    }

    public void setCompletionTokens_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setCompletionTokens_Percentiles("completionTokens", opLambda);
    }

    public void setCompletionTokens_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "completionTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_PercentileRanks(double[] values) {
        setCompletionTokens_PercentileRanks(values, null);
    }

    public void setCompletionTokens_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setCompletionTokens_PercentileRanks("completionTokens", values, opLambda);
    }

    public void setCompletionTokens_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "completionTokens", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_Histogram() {
        setCompletionTokens_Histogram(null);
    }

    public void setCompletionTokens_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setCompletionTokens_Histogram("completionTokens", opLambda, null);
    }

    public void setCompletionTokens_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        setCompletionTokens_Histogram("completionTokens", opLambda, aggsLambda);
    }

    public void setCompletionTokens_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "completionTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCompletionTokens_Range() {
        setCompletionTokens_Range(null);
    }

    public void setCompletionTokens_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setCompletionTokens_Range("completionTokens", opLambda, null);
    }

    public void setCompletionTokens_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setCompletionTokens_Range("completionTokens", opLambda, aggsLambda);
    }

    public void setCompletionTokens_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "completionTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCompletionTokens_Count() {
        setCompletionTokens_Count(null);
    }

    public void setCompletionTokens_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setCompletionTokens_Count("completionTokens", opLambda);
    }

    public void setCompletionTokens_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "completionTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_Cardinality() {
        setCompletionTokens_Cardinality(null);
    }

    public void setCompletionTokens_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setCompletionTokens_Cardinality("completionTokens", opLambda);
    }

    public void setCompletionTokens_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "completionTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCompletionTokens_Missing() {
        setCompletionTokens_Missing(null);
    }

    public void setCompletionTokens_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setCompletionTokens_Missing("completionTokens", opLambda, null);
    }

    public void setCompletionTokens_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setCompletionTokens_Missing("completionTokens", opLambda, aggsLambda);
    }

    public void setCompletionTokens_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "completionTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setErrorCode_Terms() {
        setErrorCode_Terms(null);
    }

    public void setErrorCode_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setErrorCode_Terms("errorCode", opLambda, null);
    }

    public void setErrorCode_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setErrorCode_Terms("errorCode", opLambda, aggsLambda);
    }

    public void setErrorCode_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "errorCode");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setErrorCode_SignificantTerms() {
        setErrorCode_SignificantTerms(null);
    }

    public void setErrorCode_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setErrorCode_SignificantTerms("errorCode", opLambda, null);
    }

    public void setErrorCode_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        setErrorCode_SignificantTerms("errorCode", opLambda, aggsLambda);
    }

    public void setErrorCode_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "errorCode");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setErrorCode_IpRange() {
        setErrorCode_IpRange(null);
    }

    public void setErrorCode_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setErrorCode_IpRange("errorCode", opLambda, null);
    }

    public void setErrorCode_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setErrorCode_IpRange("errorCode", opLambda, aggsLambda);
    }

    public void setErrorCode_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "errorCode");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setErrorCode_Count() {
        setErrorCode_Count(null);
    }

    public void setErrorCode_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setErrorCode_Count("errorCode", opLambda);
    }

    public void setErrorCode_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "errorCode");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCode_Cardinality() {
        setErrorCode_Cardinality(null);
    }

    public void setErrorCode_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setErrorCode_Cardinality("errorCode", opLambda);
    }

    public void setErrorCode_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "errorCode");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCode_Missing() {
        setErrorCode_Missing(null);
    }

    public void setErrorCode_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setErrorCode_Missing("errorCode", opLambda, null);
    }

    public void setErrorCode_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setErrorCode_Missing("errorCode", opLambda, aggsLambda);
    }

    public void setErrorCode_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "errorCode");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setIntent_Terms() {
        setIntent_Terms(null);
    }

    public void setIntent_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setIntent_Terms("intent", opLambda, null);
    }

    public void setIntent_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setIntent_Terms("intent", opLambda, aggsLambda);
    }

    public void setIntent_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "intent");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setIntent_SignificantTerms() {
        setIntent_SignificantTerms(null);
    }

    public void setIntent_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setIntent_SignificantTerms("intent", opLambda, null);
    }

    public void setIntent_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        setIntent_SignificantTerms("intent", opLambda, aggsLambda);
    }

    public void setIntent_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "intent");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setIntent_IpRange() {
        setIntent_IpRange(null);
    }

    public void setIntent_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setIntent_IpRange("intent", opLambda, null);
    }

    public void setIntent_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setIntent_IpRange("intent", opLambda, aggsLambda);
    }

    public void setIntent_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "intent");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setIntent_Count() {
        setIntent_Count(null);
    }

    public void setIntent_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setIntent_Count("intent", opLambda);
    }

    public void setIntent_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "intent");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntent_Cardinality() {
        setIntent_Cardinality(null);
    }

    public void setIntent_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setIntent_Cardinality("intent", opLambda);
    }

    public void setIntent_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "intent");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntent_Missing() {
        setIntent_Missing(null);
    }

    public void setIntent_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setIntent_Missing("intent", opLambda, null);
    }

    public void setIntent_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setIntent_Missing("intent", opLambda, aggsLambda);
    }

    public void setIntent_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "intent");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLlmCalls_Avg() {
        setLlmCalls_Avg(null);
    }

    public void setLlmCalls_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setLlmCalls_Avg("llmCalls", opLambda);
    }

    public void setLlmCalls_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "llmCalls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_Max() {
        setLlmCalls_Max(null);
    }

    public void setLlmCalls_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setLlmCalls_Max("llmCalls", opLambda);
    }

    public void setLlmCalls_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "llmCalls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_Min() {
        setLlmCalls_Min(null);
    }

    public void setLlmCalls_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setLlmCalls_Min("llmCalls", opLambda);
    }

    public void setLlmCalls_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "llmCalls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_Sum() {
        setLlmCalls_Sum(null);
    }

    public void setLlmCalls_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setLlmCalls_Sum("llmCalls", opLambda);
    }

    public void setLlmCalls_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "llmCalls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_ExtendedStats() {
        setLlmCalls_ExtendedStats(null);
    }

    public void setLlmCalls_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setLlmCalls_ExtendedStats("llmCalls", opLambda);
    }

    public void setLlmCalls_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "llmCalls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_Stats() {
        setLlmCalls_Stats(null);
    }

    public void setLlmCalls_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setLlmCalls_Stats("llmCalls", opLambda);
    }

    public void setLlmCalls_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "llmCalls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_Percentiles() {
        setLlmCalls_Percentiles(null);
    }

    public void setLlmCalls_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setLlmCalls_Percentiles("llmCalls", opLambda);
    }

    public void setLlmCalls_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "llmCalls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_PercentileRanks(double[] values) {
        setLlmCalls_PercentileRanks(values, null);
    }

    public void setLlmCalls_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setLlmCalls_PercentileRanks("llmCalls", values, opLambda);
    }

    public void setLlmCalls_PercentileRanks(String name, double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "llmCalls", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_Histogram() {
        setLlmCalls_Histogram(null);
    }

    public void setLlmCalls_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setLlmCalls_Histogram("llmCalls", opLambda, null);
    }

    public void setLlmCalls_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setLlmCalls_Histogram("llmCalls", opLambda, aggsLambda);
    }

    public void setLlmCalls_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "llmCalls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLlmCalls_Range() {
        setLlmCalls_Range(null);
    }

    public void setLlmCalls_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setLlmCalls_Range("llmCalls", opLambda, null);
    }

    public void setLlmCalls_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setLlmCalls_Range("llmCalls", opLambda, aggsLambda);
    }

    public void setLlmCalls_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "llmCalls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLlmCalls_Count() {
        setLlmCalls_Count(null);
    }

    public void setLlmCalls_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setLlmCalls_Count("llmCalls", opLambda);
    }

    public void setLlmCalls_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "llmCalls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_Cardinality() {
        setLlmCalls_Cardinality(null);
    }

    public void setLlmCalls_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setLlmCalls_Cardinality("llmCalls", opLambda);
    }

    public void setLlmCalls_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "llmCalls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmCalls_Missing() {
        setLlmCalls_Missing(null);
    }

    public void setLlmCalls_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setLlmCalls_Missing("llmCalls", opLambda, null);
    }

    public void setLlmCalls_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setLlmCalls_Missing("llmCalls", opLambda, aggsLambda);
    }

    public void setLlmCalls_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "llmCalls");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLlmName_Terms() {
        setLlmName_Terms(null);
    }

    public void setLlmName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setLlmName_Terms("llmName", opLambda, null);
    }

    public void setLlmName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setLlmName_Terms("llmName", opLambda, aggsLambda);
    }

    public void setLlmName_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "llmName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLlmName_SignificantTerms() {
        setLlmName_SignificantTerms(null);
    }

    public void setLlmName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setLlmName_SignificantTerms("llmName", opLambda, null);
    }

    public void setLlmName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        setLlmName_SignificantTerms("llmName", opLambda, aggsLambda);
    }

    public void setLlmName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "llmName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLlmName_IpRange() {
        setLlmName_IpRange(null);
    }

    public void setLlmName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setLlmName_IpRange("llmName", opLambda, null);
    }

    public void setLlmName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setLlmName_IpRange("llmName", opLambda, aggsLambda);
    }

    public void setLlmName_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "llmName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLlmName_Count() {
        setLlmName_Count(null);
    }

    public void setLlmName_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setLlmName_Count("llmName", opLambda);
    }

    public void setLlmName_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "llmName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmName_Cardinality() {
        setLlmName_Cardinality(null);
    }

    public void setLlmName_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setLlmName_Cardinality("llmName", opLambda);
    }

    public void setLlmName_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "llmName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLlmName_Missing() {
        setLlmName_Missing(null);
    }

    public void setLlmName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setLlmName_Missing("llmName", opLambda, null);
    }

    public void setLlmName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setLlmName_Missing("llmName", opLambda, aggsLambda);
    }

    public void setLlmName_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "llmName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setModel_Terms() {
        setModel_Terms(null);
    }

    public void setModel_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setModel_Terms("model", opLambda, null);
    }

    public void setModel_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setModel_Terms("model", opLambda, aggsLambda);
    }

    public void setModel_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "model");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setModel_SignificantTerms() {
        setModel_SignificantTerms(null);
    }

    public void setModel_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setModel_SignificantTerms("model", opLambda, null);
    }

    public void setModel_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        setModel_SignificantTerms("model", opLambda, aggsLambda);
    }

    public void setModel_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "model");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setModel_IpRange() {
        setModel_IpRange(null);
    }

    public void setModel_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setModel_IpRange("model", opLambda, null);
    }

    public void setModel_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setModel_IpRange("model", opLambda, aggsLambda);
    }

    public void setModel_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "model");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setModel_Count() {
        setModel_Count(null);
    }

    public void setModel_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setModel_Count("model", opLambda);
    }

    public void setModel_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "model");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setModel_Cardinality() {
        setModel_Cardinality(null);
    }

    public void setModel_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setModel_Cardinality("model", opLambda);
    }

    public void setModel_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "model");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setModel_Missing() {
        setModel_Missing(null);
    }

    public void setModel_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setModel_Missing("model", opLambda, null);
    }

    public void setModel_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setModel_Missing("model", opLambda, aggsLambda);
    }

    public void setModel_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "model");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPromptTokens_Avg() {
        setPromptTokens_Avg(null);
    }

    public void setPromptTokens_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setPromptTokens_Avg("promptTokens", opLambda);
    }

    public void setPromptTokens_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "promptTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_Max() {
        setPromptTokens_Max(null);
    }

    public void setPromptTokens_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setPromptTokens_Max("promptTokens", opLambda);
    }

    public void setPromptTokens_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "promptTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_Min() {
        setPromptTokens_Min(null);
    }

    public void setPromptTokens_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setPromptTokens_Min("promptTokens", opLambda);
    }

    public void setPromptTokens_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "promptTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_Sum() {
        setPromptTokens_Sum(null);
    }

    public void setPromptTokens_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setPromptTokens_Sum("promptTokens", opLambda);
    }

    public void setPromptTokens_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "promptTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_ExtendedStats() {
        setPromptTokens_ExtendedStats(null);
    }

    public void setPromptTokens_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setPromptTokens_ExtendedStats("promptTokens", opLambda);
    }

    public void setPromptTokens_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "promptTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_Stats() {
        setPromptTokens_Stats(null);
    }

    public void setPromptTokens_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setPromptTokens_Stats("promptTokens", opLambda);
    }

    public void setPromptTokens_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "promptTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_Percentiles() {
        setPromptTokens_Percentiles(null);
    }

    public void setPromptTokens_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setPromptTokens_Percentiles("promptTokens", opLambda);
    }

    public void setPromptTokens_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "promptTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_PercentileRanks(double[] values) {
        setPromptTokens_PercentileRanks(values, null);
    }

    public void setPromptTokens_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setPromptTokens_PercentileRanks("promptTokens", values, opLambda);
    }

    public void setPromptTokens_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "promptTokens", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_Histogram() {
        setPromptTokens_Histogram(null);
    }

    public void setPromptTokens_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setPromptTokens_Histogram("promptTokens", opLambda, null);
    }

    public void setPromptTokens_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setPromptTokens_Histogram("promptTokens", opLambda, aggsLambda);
    }

    public void setPromptTokens_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "promptTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPromptTokens_Range() {
        setPromptTokens_Range(null);
    }

    public void setPromptTokens_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setPromptTokens_Range("promptTokens", opLambda, null);
    }

    public void setPromptTokens_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setPromptTokens_Range("promptTokens", opLambda, aggsLambda);
    }

    public void setPromptTokens_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "promptTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPromptTokens_Count() {
        setPromptTokens_Count(null);
    }

    public void setPromptTokens_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setPromptTokens_Count("promptTokens", opLambda);
    }

    public void setPromptTokens_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "promptTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_Cardinality() {
        setPromptTokens_Cardinality(null);
    }

    public void setPromptTokens_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setPromptTokens_Cardinality("promptTokens", opLambda);
    }

    public void setPromptTokens_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "promptTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPromptTokens_Missing() {
        setPromptTokens_Missing(null);
    }

    public void setPromptTokens_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setPromptTokens_Missing("promptTokens", opLambda, null);
    }

    public void setPromptTokens_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setPromptTokens_Missing("promptTokens", opLambda, aggsLambda);
    }

    public void setPromptTokens_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "promptTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRequestedAt_DateRange() {
        setRequestedAt_DateRange(null);
    }

    public void setRequestedAt_DateRange(ConditionOptionCall<DateRangeAggregationBuilder> opLambda) {
        setRequestedAt_DateRange("requestedAt", opLambda, null);
    }

    public void setRequestedAt_DateRange(ConditionOptionCall<DateRangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setRequestedAt_DateRange("requestedAt", opLambda, aggsLambda);
    }

    public void setRequestedAt_DateRange(String name, ConditionOptionCall<DateRangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        DateRangeAggregationBuilder builder = regDateRangeA(name, "requestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRequestedAt_DateHistogram() {
        setRequestedAt_DateHistogram(null);
    }

    public void setRequestedAt_DateHistogram(ConditionOptionCall<DateHistogramAggregationBuilder> opLambda) {
        setRequestedAt_DateHistogram("requestedAt", opLambda, null);
    }

    public void setRequestedAt_DateHistogram(ConditionOptionCall<DateHistogramAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        setRequestedAt_DateHistogram("requestedAt", opLambda, aggsLambda);
    }

    public void setRequestedAt_DateHistogram(String name, ConditionOptionCall<DateHistogramAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        DateHistogramAggregationBuilder builder = regDateHistogramA(name, "requestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRequestedAt_Count() {
        setRequestedAt_Count(null);
    }

    public void setRequestedAt_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setRequestedAt_Count("requestedAt", opLambda);
    }

    public void setRequestedAt_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "requestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_Cardinality() {
        setRequestedAt_Cardinality(null);
    }

    public void setRequestedAt_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setRequestedAt_Cardinality("requestedAt", opLambda);
    }

    public void setRequestedAt_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "requestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_Missing() {
        setRequestedAt_Missing(null);
    }

    public void setRequestedAt_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setRequestedAt_Missing("requestedAt", opLambda, null);
    }

    public void setRequestedAt_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setRequestedAt_Missing("requestedAt", opLambda, aggsLambda);
    }

    public void setRequestedAt_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "requestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setResponseTime_Avg() {
        setResponseTime_Avg(null);
    }

    public void setResponseTime_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setResponseTime_Avg("responseTime", opLambda);
    }

    public void setResponseTime_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Max() {
        setResponseTime_Max(null);
    }

    public void setResponseTime_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setResponseTime_Max("responseTime", opLambda);
    }

    public void setResponseTime_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Min() {
        setResponseTime_Min(null);
    }

    public void setResponseTime_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setResponseTime_Min("responseTime", opLambda);
    }

    public void setResponseTime_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Sum() {
        setResponseTime_Sum(null);
    }

    public void setResponseTime_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setResponseTime_Sum("responseTime", opLambda);
    }

    public void setResponseTime_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_ExtendedStats() {
        setResponseTime_ExtendedStats(null);
    }

    public void setResponseTime_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setResponseTime_ExtendedStats("responseTime", opLambda);
    }

    public void setResponseTime_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Stats() {
        setResponseTime_Stats(null);
    }

    public void setResponseTime_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setResponseTime_Stats("responseTime", opLambda);
    }

    public void setResponseTime_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Percentiles() {
        setResponseTime_Percentiles(null);
    }

    public void setResponseTime_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setResponseTime_Percentiles("responseTime", opLambda);
    }

    public void setResponseTime_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_PercentileRanks(double[] values) {
        setResponseTime_PercentileRanks(values, null);
    }

    public void setResponseTime_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setResponseTime_PercentileRanks("responseTime", values, opLambda);
    }

    public void setResponseTime_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "responseTime", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Histogram() {
        setResponseTime_Histogram(null);
    }

    public void setResponseTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setResponseTime_Histogram("responseTime", opLambda, null);
    }

    public void setResponseTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setResponseTime_Histogram("responseTime", opLambda, aggsLambda);
    }

    public void setResponseTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setResponseTime_Range() {
        setResponseTime_Range(null);
    }

    public void setResponseTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setResponseTime_Range("responseTime", opLambda, null);
    }

    public void setResponseTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setResponseTime_Range("responseTime", opLambda, aggsLambda);
    }

    public void setResponseTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setResponseTime_Count() {
        setResponseTime_Count(null);
    }

    public void setResponseTime_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setResponseTime_Count("responseTime", opLambda);
    }

    public void setResponseTime_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Cardinality() {
        setResponseTime_Cardinality(null);
    }

    public void setResponseTime_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setResponseTime_Cardinality("responseTime", opLambda);
    }

    public void setResponseTime_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Missing() {
        setResponseTime_Missing(null);
    }

    public void setResponseTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setResponseTime_Missing("responseTime", opLambda, null);
    }

    public void setResponseTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setResponseTime_Missing("responseTime", opLambda, aggsLambda);
    }

    public void setResponseTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRoles_Terms() {
        setRoles_Terms(null);
    }

    public void setRoles_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setRoles_Terms("roles", opLambda, null);
    }

    public void setRoles_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setRoles_Terms("roles", opLambda, aggsLambda);
    }

    public void setRoles_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRoles_SignificantTerms() {
        setRoles_SignificantTerms(null);
    }

    public void setRoles_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setRoles_SignificantTerms("roles", opLambda, null);
    }

    public void setRoles_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        setRoles_SignificantTerms("roles", opLambda, aggsLambda);
    }

    public void setRoles_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRoles_IpRange() {
        setRoles_IpRange(null);
    }

    public void setRoles_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setRoles_IpRange("roles", opLambda, null);
    }

    public void setRoles_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setRoles_IpRange("roles", opLambda, aggsLambda);
    }

    public void setRoles_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRoles_Count() {
        setRoles_Count(null);
    }

    public void setRoles_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setRoles_Count("roles", opLambda);
    }

    public void setRoles_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_Cardinality() {
        setRoles_Cardinality(null);
    }

    public void setRoles_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setRoles_Cardinality("roles", opLambda);
    }

    public void setRoles_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_Missing() {
        setRoles_Missing(null);
    }

    public void setRoles_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setRoles_Missing("roles", opLambda, null);
    }

    public void setRoles_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setRoles_Missing("roles", opLambda, aggsLambda);
    }

    public void setRoles_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSearchQueryId_Terms() {
        setSearchQueryId_Terms(null);
    }

    public void setSearchQueryId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setSearchQueryId_Terms("searchQueryId", opLambda, null);
    }

    public void setSearchQueryId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setSearchQueryId_Terms("searchQueryId", opLambda, aggsLambda);
    }

    public void setSearchQueryId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "searchQueryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSearchQueryId_SignificantTerms() {
        setSearchQueryId_SignificantTerms(null);
    }

    public void setSearchQueryId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setSearchQueryId_SignificantTerms("searchQueryId", opLambda, null);
    }

    public void setSearchQueryId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        setSearchQueryId_SignificantTerms("searchQueryId", opLambda, aggsLambda);
    }

    public void setSearchQueryId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "searchQueryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSearchQueryId_IpRange() {
        setSearchQueryId_IpRange(null);
    }

    public void setSearchQueryId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setSearchQueryId_IpRange("searchQueryId", opLambda, null);
    }

    public void setSearchQueryId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setSearchQueryId_IpRange("searchQueryId", opLambda, aggsLambda);
    }

    public void setSearchQueryId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "searchQueryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSearchQueryId_Count() {
        setSearchQueryId_Count(null);
    }

    public void setSearchQueryId_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setSearchQueryId_Count("searchQueryId", opLambda);
    }

    public void setSearchQueryId_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "searchQueryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchQueryId_Cardinality() {
        setSearchQueryId_Cardinality(null);
    }

    public void setSearchQueryId_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setSearchQueryId_Cardinality("searchQueryId", opLambda);
    }

    public void setSearchQueryId_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "searchQueryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchQueryId_Missing() {
        setSearchQueryId_Missing(null);
    }

    public void setSearchQueryId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setSearchQueryId_Missing("searchQueryId", opLambda, null);
    }

    public void setSearchQueryId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setSearchQueryId_Missing("searchQueryId", opLambda, aggsLambda);
    }

    public void setSearchQueryId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "searchQueryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSourceCount_Avg() {
        setSourceCount_Avg(null);
    }

    public void setSourceCount_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setSourceCount_Avg("sourceCount", opLambda);
    }

    public void setSourceCount_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "sourceCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_Max() {
        setSourceCount_Max(null);
    }

    public void setSourceCount_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setSourceCount_Max("sourceCount", opLambda);
    }

    public void setSourceCount_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "sourceCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_Min() {
        setSourceCount_Min(null);
    }

    public void setSourceCount_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setSourceCount_Min("sourceCount", opLambda);
    }

    public void setSourceCount_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "sourceCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_Sum() {
        setSourceCount_Sum(null);
    }

    public void setSourceCount_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setSourceCount_Sum("sourceCount", opLambda);
    }

    public void setSourceCount_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "sourceCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_ExtendedStats() {
        setSourceCount_ExtendedStats(null);
    }

    public void setSourceCount_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setSourceCount_ExtendedStats("sourceCount", opLambda);
    }

    public void setSourceCount_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "sourceCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_Stats() {
        setSourceCount_Stats(null);
    }

    public void setSourceCount_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setSourceCount_Stats("sourceCount", opLambda);
    }

    public void setSourceCount_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "sourceCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_Percentiles() {
        setSourceCount_Percentiles(null);
    }

    public void setSourceCount_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setSourceCount_Percentiles("sourceCount", opLambda);
    }

    public void setSourceCount_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "sourceCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_PercentileRanks(double[] values) {
        setSourceCount_PercentileRanks(values, null);
    }

    public void setSourceCount_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setSourceCount_PercentileRanks("sourceCount", values, opLambda);
    }

    public void setSourceCount_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "sourceCount", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_Histogram() {
        setSourceCount_Histogram(null);
    }

    public void setSourceCount_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setSourceCount_Histogram("sourceCount", opLambda, null);
    }

    public void setSourceCount_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setSourceCount_Histogram("sourceCount", opLambda, aggsLambda);
    }

    public void setSourceCount_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "sourceCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSourceCount_Range() {
        setSourceCount_Range(null);
    }

    public void setSourceCount_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setSourceCount_Range("sourceCount", opLambda, null);
    }

    public void setSourceCount_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setSourceCount_Range("sourceCount", opLambda, aggsLambda);
    }

    public void setSourceCount_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "sourceCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSourceCount_Count() {
        setSourceCount_Count(null);
    }

    public void setSourceCount_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setSourceCount_Count("sourceCount", opLambda);
    }

    public void setSourceCount_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "sourceCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_Cardinality() {
        setSourceCount_Cardinality(null);
    }

    public void setSourceCount_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setSourceCount_Cardinality("sourceCount", opLambda);
    }

    public void setSourceCount_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "sourceCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSourceCount_Missing() {
        setSourceCount_Missing(null);
    }

    public void setSourceCount_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setSourceCount_Missing("sourceCount", opLambda, null);
    }

    public void setSourceCount_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setSourceCount_Missing("sourceCount", opLambda, aggsLambda);
    }

    public void setSourceCount_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "sourceCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setStatus_Terms() {
        setStatus_Terms(null);
    }

    public void setStatus_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setStatus_Terms("status", opLambda, null);
    }

    public void setStatus_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setStatus_Terms("status", opLambda, aggsLambda);
    }

    public void setStatus_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "status");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setStatus_SignificantTerms() {
        setStatus_SignificantTerms(null);
    }

    public void setStatus_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setStatus_SignificantTerms("status", opLambda, null);
    }

    public void setStatus_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        setStatus_SignificantTerms("status", opLambda, aggsLambda);
    }

    public void setStatus_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "status");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setStatus_IpRange() {
        setStatus_IpRange(null);
    }

    public void setStatus_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setStatus_IpRange("status", opLambda, null);
    }

    public void setStatus_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setStatus_IpRange("status", opLambda, aggsLambda);
    }

    public void setStatus_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "status");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setStatus_Count() {
        setStatus_Count(null);
    }

    public void setStatus_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setStatus_Count("status", opLambda);
    }

    public void setStatus_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "status");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStatus_Cardinality() {
        setStatus_Cardinality(null);
    }

    public void setStatus_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setStatus_Cardinality("status", opLambda);
    }

    public void setStatus_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "status");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStatus_Missing() {
        setStatus_Missing(null);
    }

    public void setStatus_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setStatus_Missing("status", opLambda, null);
    }

    public void setStatus_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setStatus_Missing("status", opLambda, aggsLambda);
    }

    public void setStatus_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "status");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTotalTokens_Avg() {
        setTotalTokens_Avg(null);
    }

    public void setTotalTokens_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setTotalTokens_Avg("totalTokens", opLambda);
    }

    public void setTotalTokens_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "totalTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_Max() {
        setTotalTokens_Max(null);
    }

    public void setTotalTokens_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setTotalTokens_Max("totalTokens", opLambda);
    }

    public void setTotalTokens_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "totalTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_Min() {
        setTotalTokens_Min(null);
    }

    public void setTotalTokens_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setTotalTokens_Min("totalTokens", opLambda);
    }

    public void setTotalTokens_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "totalTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_Sum() {
        setTotalTokens_Sum(null);
    }

    public void setTotalTokens_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setTotalTokens_Sum("totalTokens", opLambda);
    }

    public void setTotalTokens_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "totalTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_ExtendedStats() {
        setTotalTokens_ExtendedStats(null);
    }

    public void setTotalTokens_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setTotalTokens_ExtendedStats("totalTokens", opLambda);
    }

    public void setTotalTokens_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "totalTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_Stats() {
        setTotalTokens_Stats(null);
    }

    public void setTotalTokens_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setTotalTokens_Stats("totalTokens", opLambda);
    }

    public void setTotalTokens_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "totalTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_Percentiles() {
        setTotalTokens_Percentiles(null);
    }

    public void setTotalTokens_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setTotalTokens_Percentiles("totalTokens", opLambda);
    }

    public void setTotalTokens_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "totalTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_PercentileRanks(double[] values) {
        setTotalTokens_PercentileRanks(values, null);
    }

    public void setTotalTokens_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setTotalTokens_PercentileRanks("totalTokens", values, opLambda);
    }

    public void setTotalTokens_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "totalTokens", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_Histogram() {
        setTotalTokens_Histogram(null);
    }

    public void setTotalTokens_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setTotalTokens_Histogram("totalTokens", opLambda, null);
    }

    public void setTotalTokens_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setTotalTokens_Histogram("totalTokens", opLambda, aggsLambda);
    }

    public void setTotalTokens_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "totalTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTotalTokens_Range() {
        setTotalTokens_Range(null);
    }

    public void setTotalTokens_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setTotalTokens_Range("totalTokens", opLambda, null);
    }

    public void setTotalTokens_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setTotalTokens_Range("totalTokens", opLambda, aggsLambda);
    }

    public void setTotalTokens_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "totalTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTotalTokens_Count() {
        setTotalTokens_Count(null);
    }

    public void setTotalTokens_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setTotalTokens_Count("totalTokens", opLambda);
    }

    public void setTotalTokens_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "totalTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_Cardinality() {
        setTotalTokens_Cardinality(null);
    }

    public void setTotalTokens_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setTotalTokens_Cardinality("totalTokens", opLambda);
    }

    public void setTotalTokens_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "totalTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTotalTokens_Missing() {
        setTotalTokens_Missing(null);
    }

    public void setTotalTokens_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setTotalTokens_Missing("totalTokens", opLambda, null);
    }

    public void setTotalTokens_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setTotalTokens_Missing("totalTokens", opLambda, aggsLambda);
    }

    public void setTotalTokens_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "totalTokens");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUser_Terms() {
        setUser_Terms(null);
    }

    public void setUser_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setUser_Terms("user", opLambda, null);
    }

    public void setUser_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setUser_Terms("user", opLambda, aggsLambda);
    }

    public void setUser_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUser_SignificantTerms() {
        setUser_SignificantTerms(null);
    }

    public void setUser_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setUser_SignificantTerms("user", opLambda, null);
    }

    public void setUser_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        setUser_SignificantTerms("user", opLambda, aggsLambda);
    }

    public void setUser_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUser_IpRange() {
        setUser_IpRange(null);
    }

    public void setUser_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setUser_IpRange("user", opLambda, null);
    }

    public void setUser_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setUser_IpRange("user", opLambda, aggsLambda);
    }

    public void setUser_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUser_Count() {
        setUser_Count(null);
    }

    public void setUser_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setUser_Count("user", opLambda);
    }

    public void setUser_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_Cardinality() {
        setUser_Cardinality(null);
    }

    public void setUser_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setUser_Cardinality("user", opLambda);
    }

    public void setUser_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_Missing() {
        setUser_Missing(null);
    }

    public void setUser_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setUser_Missing("user", opLambda, null);
    }

    public void setUser_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setUser_Missing("user", opLambda, aggsLambda);
    }

    public void setUser_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserSessionId_Terms() {
        setUserSessionId_Terms(null);
    }

    public void setUserSessionId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setUserSessionId_Terms("userSessionId", opLambda, null);
    }

    public void setUserSessionId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setUserSessionId_Terms("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserSessionId_SignificantTerms() {
        setUserSessionId_SignificantTerms(null);
    }

    public void setUserSessionId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setUserSessionId_SignificantTerms("userSessionId", opLambda, null);
    }

    public void setUserSessionId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        setUserSessionId_SignificantTerms("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserSessionId_IpRange() {
        setUserSessionId_IpRange(null);
    }

    public void setUserSessionId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setUserSessionId_IpRange("userSessionId", opLambda, null);
    }

    public void setUserSessionId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setUserSessionId_IpRange("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserSessionId_Count() {
        setUserSessionId_Count(null);
    }

    public void setUserSessionId_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setUserSessionId_Count("userSessionId", opLambda);
    }

    public void setUserSessionId_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Cardinality() {
        setUserSessionId_Cardinality(null);
    }

    public void setUserSessionId_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setUserSessionId_Cardinality("userSessionId", opLambda);
    }

    public void setUserSessionId_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Missing() {
        setUserSessionId_Missing(null);
    }

    public void setUserSessionId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setUserSessionId_Missing("userSessionId", opLambda, null);
    }

    public void setUserSessionId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setUserSessionId_Missing("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setVirtualHost_Terms() {
        setVirtualHost_Terms(null);
    }

    public void setVirtualHost_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setVirtualHost_Terms("virtualHost", opLambda, null);
    }

    public void setVirtualHost_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setVirtualHost_Terms("virtualHost", opLambda, aggsLambda);
    }

    public void setVirtualHost_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "virtualHost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setVirtualHost_SignificantTerms() {
        setVirtualHost_SignificantTerms(null);
    }

    public void setVirtualHost_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setVirtualHost_SignificantTerms("virtualHost", opLambda, null);
    }

    public void setVirtualHost_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        setVirtualHost_SignificantTerms("virtualHost", opLambda, aggsLambda);
    }

    public void setVirtualHost_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "virtualHost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setVirtualHost_IpRange() {
        setVirtualHost_IpRange(null);
    }

    public void setVirtualHost_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setVirtualHost_IpRange("virtualHost", opLambda, null);
    }

    public void setVirtualHost_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setVirtualHost_IpRange("virtualHost", opLambda, aggsLambda);
    }

    public void setVirtualHost_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "virtualHost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setVirtualHost_Count() {
        setVirtualHost_Count(null);
    }

    public void setVirtualHost_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setVirtualHost_Count("virtualHost", opLambda);
    }

    public void setVirtualHost_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "virtualHost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_Cardinality() {
        setVirtualHost_Cardinality(null);
    }

    public void setVirtualHost_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setVirtualHost_Cardinality("virtualHost", opLambda);
    }

    public void setVirtualHost_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "virtualHost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_Missing() {
        setVirtualHost_Missing(null);
    }

    public void setVirtualHost_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setVirtualHost_Missing("virtualHost", opLambda, null);
    }

    public void setVirtualHost_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsChatLogCA> aggsLambda) {
        setVirtualHost_Missing("virtualHost", opLambda, aggsLambda);
    }

    public void setVirtualHost_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsChatLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "virtualHost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ChatLogCA ca = new ChatLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
