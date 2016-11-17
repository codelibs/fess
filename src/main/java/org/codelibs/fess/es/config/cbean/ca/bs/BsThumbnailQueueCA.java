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
package org.codelibs.fess.es.config.cbean.ca.bs;

import org.codelibs.fess.es.config.allcommon.EsAbstractConditionAggregation;
import org.codelibs.fess.es.config.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.config.cbean.ca.ThumbnailQueueCA;
import org.codelibs.fess.es.config.cbean.cq.ThumbnailQueueCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsThumbnailQueueCQ;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.global.GlobalBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.HistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.missing.MissingBuilder;
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder;
import org.elasticsearch.search.aggregations.bucket.range.ipv4.IPv4RangeBuilder;
import org.elasticsearch.search.aggregations.bucket.sampler.SamplerAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.significant.SignificantTermsBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.AvgBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityBuilder;
import org.elasticsearch.search.aggregations.metrics.max.MaxBuilder;
import org.elasticsearch.search.aggregations.metrics.min.MinBuilder;
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentileRanksBuilder;
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesBuilder;
import org.elasticsearch.search.aggregations.metrics.scripted.ScriptedMetricBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.StatsBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStatsBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.SumBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHitsBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsThumbnailQueueCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsThumbnailQueueCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        ThumbnailQueueCQ cq = new ThumbnailQueueCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        GlobalBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void scriptedMetric(String name, ConditionOptionCall<ScriptedMetricBuilder> opLambda) {
        ScriptedMetricBuilder builder = regScriptedMetricA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void topHits(String name, ConditionOptionCall<TopHitsBuilder> opLambda) {
        TopHitsBuilder builder = regTopHitsA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    // String createdBy

    public void setCreatedBy_Terms() {
        setCreatedBy_Terms(null);
    }

    public void setCreatedBy_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setCreatedBy_Terms("createdBy", opLambda, null);
    }

    public void setCreatedBy_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setCreatedBy_Terms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCreatedBy_SignificantTerms() {
        setCreatedBy_SignificantTerms(null);
    }

    public void setCreatedBy_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setCreatedBy_SignificantTerms("createdBy", opLambda, null);
    }

    public void setCreatedBy_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setCreatedBy_SignificantTerms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCreatedBy_IpRange() {
        setCreatedBy_IpRange(null);
    }

    public void setCreatedBy_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setCreatedBy_IpRange("createdBy", opLambda, null);
    }

    public void setCreatedBy_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setCreatedBy_IpRange("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCreatedBy_Count() {
        setCreatedBy_Count(null);
    }

    public void setCreatedBy_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setCreatedBy_Count("createdBy", opLambda);
    }

    public void setCreatedBy_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Cardinality() {
        setCreatedBy_Cardinality(null);
    }

    public void setCreatedBy_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setCreatedBy_Cardinality("createdBy", opLambda);
    }

    public void setCreatedBy_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Missing() {
        setCreatedBy_Missing(null);
    }

    public void setCreatedBy_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setCreatedBy_Missing("createdBy", opLambda, null);
    }

    public void setCreatedBy_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setCreatedBy_Missing("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Long createdTime
    public void setCreatedTime_Avg() {
        setCreatedTime_Avg(null);
    }

    public void setCreatedTime_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setCreatedTime_Avg("createdTime", opLambda);
    }

    public void setCreatedTime_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Max() {
        setCreatedTime_Max(null);
    }

    public void setCreatedTime_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setCreatedTime_Max("createdTime", opLambda);
    }

    public void setCreatedTime_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Min() {
        setCreatedTime_Min(null);
    }

    public void setCreatedTime_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setCreatedTime_Min("createdTime", opLambda);
    }

    public void setCreatedTime_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Sum() {
        setCreatedTime_Sum(null);
    }

    public void setCreatedTime_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setCreatedTime_Sum("createdTime", opLambda);
    }

    public void setCreatedTime_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_ExtendedStats() {
        setCreatedTime_ExtendedStats(null);
    }

    public void setCreatedTime_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setCreatedTime_ExtendedStats("createdTime", opLambda);
    }

    public void setCreatedTime_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Stats() {
        setCreatedTime_Stats(null);
    }

    public void setCreatedTime_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setCreatedTime_Stats("createdTime", opLambda);
    }

    public void setCreatedTime_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Percentiles() {
        setCreatedTime_Percentiles(null);
    }

    public void setCreatedTime_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setCreatedTime_Percentiles("createdTime", opLambda);
    }

    public void setCreatedTime_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_PercentileRanks() {
        setCreatedTime_PercentileRanks(null);
    }

    public void setCreatedTime_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setCreatedTime_PercentileRanks("createdTime", opLambda);
    }

    public void setCreatedTime_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Histogram() {
        setCreatedTime_Histogram(null);
    }

    public void setCreatedTime_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setCreatedTime_Histogram("createdTime", opLambda, null);
    }

    public void setCreatedTime_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setCreatedTime_Histogram("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCreatedTime_Range() {
        setCreatedTime_Range(null);
    }

    public void setCreatedTime_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setCreatedTime_Range("createdTime", opLambda, null);
    }

    public void setCreatedTime_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setCreatedTime_Range("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCreatedTime_Count() {
        setCreatedTime_Count(null);
    }

    public void setCreatedTime_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setCreatedTime_Count("createdTime", opLambda);
    }

    public void setCreatedTime_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Cardinality() {
        setCreatedTime_Cardinality(null);
    }

    public void setCreatedTime_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setCreatedTime_Cardinality("createdTime", opLambda);
    }

    public void setCreatedTime_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Missing() {
        setCreatedTime_Missing(null);
    }

    public void setCreatedTime_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setCreatedTime_Missing("createdTime", opLambda, null);
    }

    public void setCreatedTime_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setCreatedTime_Missing("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String target

    public void setTarget_Terms() {
        setTarget_Terms(null);
    }

    public void setTarget_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setTarget_Terms("target", opLambda, null);
    }

    public void setTarget_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setTarget_Terms("target", opLambda, aggsLambda);
    }

    public void setTarget_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTarget_SignificantTerms() {
        setTarget_SignificantTerms(null);
    }

    public void setTarget_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setTarget_SignificantTerms("target", opLambda, null);
    }

    public void setTarget_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setTarget_SignificantTerms("target", opLambda, aggsLambda);
    }

    public void setTarget_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTarget_IpRange() {
        setTarget_IpRange(null);
    }

    public void setTarget_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setTarget_IpRange("target", opLambda, null);
    }

    public void setTarget_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setTarget_IpRange("target", opLambda, aggsLambda);
    }

    public void setTarget_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTarget_Count() {
        setTarget_Count(null);
    }

    public void setTarget_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setTarget_Count("target", opLambda);
    }

    public void setTarget_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Cardinality() {
        setTarget_Cardinality(null);
    }

    public void setTarget_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setTarget_Cardinality("target", opLambda);
    }

    public void setTarget_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Missing() {
        setTarget_Missing(null);
    }

    public void setTarget_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setTarget_Missing("target", opLambda, null);
    }

    public void setTarget_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setTarget_Missing("target", opLambda, aggsLambda);
    }

    public void setTarget_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String generator

    public void setGenerator_Terms() {
        setGenerator_Terms(null);
    }

    public void setGenerator_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setGenerator_Terms("generator", opLambda, null);
    }

    public void setGenerator_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setGenerator_Terms("generator", opLambda, aggsLambda);
    }

    public void setGenerator_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "generator");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setGenerator_SignificantTerms() {
        setGenerator_SignificantTerms(null);
    }

    public void setGenerator_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setGenerator_SignificantTerms("generator", opLambda, null);
    }

    public void setGenerator_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setGenerator_SignificantTerms("generator", opLambda, aggsLambda);
    }

    public void setGenerator_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "generator");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setGenerator_IpRange() {
        setGenerator_IpRange(null);
    }

    public void setGenerator_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setGenerator_IpRange("generator", opLambda, null);
    }

    public void setGenerator_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setGenerator_IpRange("generator", opLambda, aggsLambda);
    }

    public void setGenerator_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "generator");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setGenerator_Count() {
        setGenerator_Count(null);
    }

    public void setGenerator_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setGenerator_Count("generator", opLambda);
    }

    public void setGenerator_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "generator");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGenerator_Cardinality() {
        setGenerator_Cardinality(null);
    }

    public void setGenerator_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setGenerator_Cardinality("generator", opLambda);
    }

    public void setGenerator_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "generator");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGenerator_Missing() {
        setGenerator_Missing(null);
    }

    public void setGenerator_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setGenerator_Missing("generator", opLambda, null);
    }

    public void setGenerator_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setGenerator_Missing("generator", opLambda, aggsLambda);
    }

    public void setGenerator_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "generator");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String path

    public void setPath_Terms() {
        setPath_Terms(null);
    }

    public void setPath_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setPath_Terms("path", opLambda, null);
    }

    public void setPath_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setPath_Terms("path", opLambda, aggsLambda);
    }

    public void setPath_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "path");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPath_SignificantTerms() {
        setPath_SignificantTerms(null);
    }

    public void setPath_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setPath_SignificantTerms("path", opLambda, null);
    }

    public void setPath_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setPath_SignificantTerms("path", opLambda, aggsLambda);
    }

    public void setPath_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "path");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPath_IpRange() {
        setPath_IpRange(null);
    }

    public void setPath_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setPath_IpRange("path", opLambda, null);
    }

    public void setPath_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setPath_IpRange("path", opLambda, aggsLambda);
    }

    public void setPath_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "path");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPath_Count() {
        setPath_Count(null);
    }

    public void setPath_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setPath_Count("path", opLambda);
    }

    public void setPath_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "path");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_Cardinality() {
        setPath_Cardinality(null);
    }

    public void setPath_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setPath_Cardinality("path", opLambda);
    }

    public void setPath_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "path");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_Missing() {
        setPath_Missing(null);
    }

    public void setPath_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setPath_Missing("path", opLambda, null);
    }

    public void setPath_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setPath_Missing("path", opLambda, aggsLambda);
    }

    public void setPath_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "path");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String url

    public void setUrl_Terms() {
        setUrl_Terms(null);
    }

    public void setUrl_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setUrl_Terms("url", opLambda, null);
    }

    public void setUrl_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setUrl_Terms("url", opLambda, aggsLambda);
    }

    public void setUrl_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUrl_SignificantTerms() {
        setUrl_SignificantTerms(null);
    }

    public void setUrl_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setUrl_SignificantTerms("url", opLambda, null);
    }

    public void setUrl_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setUrl_SignificantTerms("url", opLambda, aggsLambda);
    }

    public void setUrl_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUrl_IpRange() {
        setUrl_IpRange(null);
    }

    public void setUrl_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setUrl_IpRange("url", opLambda, null);
    }

    public void setUrl_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setUrl_IpRange("url", opLambda, aggsLambda);
    }

    public void setUrl_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUrl_Count() {
        setUrl_Count(null);
    }

    public void setUrl_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setUrl_Count("url", opLambda);
    }

    public void setUrl_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Cardinality() {
        setUrl_Cardinality(null);
    }

    public void setUrl_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setUrl_Cardinality("url", opLambda);
    }

    public void setUrl_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Missing() {
        setUrl_Missing(null);
    }

    public void setUrl_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setUrl_Missing("url", opLambda, null);
    }

    public void setUrl_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setUrl_Missing("url", opLambda, aggsLambda);
    }

    public void setUrl_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
