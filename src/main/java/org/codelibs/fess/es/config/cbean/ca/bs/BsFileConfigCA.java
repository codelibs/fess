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
import org.codelibs.fess.es.config.cbean.ca.FileConfigCA;
import org.codelibs.fess.es.config.cbean.cq.FileConfigCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsFileConfigCQ;
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
public abstract class BsFileConfigCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsFileConfigCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        FileConfigCQ cq = new FileConfigCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        GlobalBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
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

    // Boolean available
    public void setAvailable_Avg() {
        setAvailable_Avg(null);
    }

    public void setAvailable_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setAvailable_Avg("available", opLambda);
    }

    public void setAvailable_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Max() {
        setAvailable_Max(null);
    }

    public void setAvailable_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setAvailable_Max("available", opLambda);
    }

    public void setAvailable_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Min() {
        setAvailable_Min(null);
    }

    public void setAvailable_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setAvailable_Min("available", opLambda);
    }

    public void setAvailable_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Sum() {
        setAvailable_Sum(null);
    }

    public void setAvailable_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setAvailable_Sum("available", opLambda);
    }

    public void setAvailable_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_ExtendedStats() {
        setAvailable_ExtendedStats(null);
    }

    public void setAvailable_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setAvailable_ExtendedStats("available", opLambda);
    }

    public void setAvailable_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Stats() {
        setAvailable_Stats(null);
    }

    public void setAvailable_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setAvailable_Stats("available", opLambda);
    }

    public void setAvailable_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Percentiles() {
        setAvailable_Percentiles(null);
    }

    public void setAvailable_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setAvailable_Percentiles("available", opLambda);
    }

    public void setAvailable_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_PercentileRanks() {
        setAvailable_PercentileRanks(null);
    }

    public void setAvailable_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setAvailable_PercentileRanks("available", opLambda);
    }

    public void setAvailable_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Histogram() {
        setAvailable_Histogram(null);
    }

    public void setAvailable_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setAvailable_Histogram("available", opLambda, null);
    }

    public void setAvailable_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setAvailable_Histogram("available", opLambda, aggsLambda);
    }

    public void setAvailable_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setAvailable_Range() {
        setAvailable_Range(null);
    }

    public void setAvailable_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setAvailable_Range("available", opLambda, null);
    }

    public void setAvailable_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setAvailable_Range("available", opLambda, aggsLambda);
    }

    public void setAvailable_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setAvailable_Count() {
        setAvailable_Count(null);
    }

    public void setAvailable_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setAvailable_Count("available", opLambda);
    }

    public void setAvailable_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Cardinality() {
        setAvailable_Cardinality(null);
    }

    public void setAvailable_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setAvailable_Cardinality("available", opLambda);
    }

    public void setAvailable_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Missing() {
        setAvailable_Missing(null);
    }

    public void setAvailable_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setAvailable_Missing("available", opLambda, null);
    }

    public void setAvailable_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setAvailable_Missing("available", opLambda, aggsLambda);
    }

    public void setAvailable_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Float boost
    public void setBoost_Avg() {
        setBoost_Avg(null);
    }

    public void setBoost_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setBoost_Avg("boost", opLambda);
    }

    public void setBoost_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Max() {
        setBoost_Max(null);
    }

    public void setBoost_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setBoost_Max("boost", opLambda);
    }

    public void setBoost_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Min() {
        setBoost_Min(null);
    }

    public void setBoost_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setBoost_Min("boost", opLambda);
    }

    public void setBoost_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Sum() {
        setBoost_Sum(null);
    }

    public void setBoost_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setBoost_Sum("boost", opLambda);
    }

    public void setBoost_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_ExtendedStats() {
        setBoost_ExtendedStats(null);
    }

    public void setBoost_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setBoost_ExtendedStats("boost", opLambda);
    }

    public void setBoost_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Stats() {
        setBoost_Stats(null);
    }

    public void setBoost_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setBoost_Stats("boost", opLambda);
    }

    public void setBoost_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Percentiles() {
        setBoost_Percentiles(null);
    }

    public void setBoost_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setBoost_Percentiles("boost", opLambda);
    }

    public void setBoost_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_PercentileRanks() {
        setBoost_PercentileRanks(null);
    }

    public void setBoost_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setBoost_PercentileRanks("boost", opLambda);
    }

    public void setBoost_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Histogram() {
        setBoost_Histogram(null);
    }

    public void setBoost_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setBoost_Histogram("boost", opLambda, null);
    }

    public void setBoost_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setBoost_Histogram("boost", opLambda, aggsLambda);
    }

    public void setBoost_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setBoost_Range() {
        setBoost_Range(null);
    }

    public void setBoost_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setBoost_Range("boost", opLambda, null);
    }

    public void setBoost_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setBoost_Range("boost", opLambda, aggsLambda);
    }

    public void setBoost_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setBoost_Count() {
        setBoost_Count(null);
    }

    public void setBoost_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setBoost_Count("boost", opLambda);
    }

    public void setBoost_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Cardinality() {
        setBoost_Cardinality(null);
    }

    public void setBoost_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setBoost_Cardinality("boost", opLambda);
    }

    public void setBoost_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Missing() {
        setBoost_Missing(null);
    }

    public void setBoost_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setBoost_Missing("boost", opLambda, null);
    }

    public void setBoost_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setBoost_Missing("boost", opLambda, aggsLambda);
    }

    public void setBoost_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String configParameter

    public void setConfigParameter_Terms() {
        setConfigParameter_Terms(null);
    }

    public void setConfigParameter_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setConfigParameter_Terms("configParameter", opLambda, null);
    }

    public void setConfigParameter_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setConfigParameter_Terms("configParameter", opLambda, aggsLambda);
    }

    public void setConfigParameter_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "configParameter");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setConfigParameter_SignificantTerms() {
        setConfigParameter_SignificantTerms(null);
    }

    public void setConfigParameter_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setConfigParameter_SignificantTerms("configParameter", opLambda, null);
    }

    public void setConfigParameter_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setConfigParameter_SignificantTerms("configParameter", opLambda, aggsLambda);
    }

    public void setConfigParameter_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "configParameter");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setConfigParameter_IpRange() {
        setConfigParameter_IpRange(null);
    }

    public void setConfigParameter_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setConfigParameter_IpRange("configParameter", opLambda, null);
    }

    public void setConfigParameter_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setConfigParameter_IpRange("configParameter", opLambda, aggsLambda);
    }

    public void setConfigParameter_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "configParameter");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setConfigParameter_Count() {
        setConfigParameter_Count(null);
    }

    public void setConfigParameter_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setConfigParameter_Count("configParameter", opLambda);
    }

    public void setConfigParameter_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "configParameter");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_Cardinality() {
        setConfigParameter_Cardinality(null);
    }

    public void setConfigParameter_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setConfigParameter_Cardinality("configParameter", opLambda);
    }

    public void setConfigParameter_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "configParameter");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_Missing() {
        setConfigParameter_Missing(null);
    }

    public void setConfigParameter_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setConfigParameter_Missing("configParameter", opLambda, null);
    }

    public void setConfigParameter_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setConfigParameter_Missing("configParameter", opLambda, aggsLambda);
    }

    public void setConfigParameter_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "configParameter");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String createdBy

    public void setCreatedBy_Terms() {
        setCreatedBy_Terms(null);
    }

    public void setCreatedBy_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setCreatedBy_Terms("createdBy", opLambda, null);
    }

    public void setCreatedBy_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setCreatedBy_Terms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
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

    public void setCreatedBy_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setCreatedBy_SignificantTerms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
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

    public void setCreatedBy_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setCreatedBy_IpRange("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
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

    public void setCreatedBy_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setCreatedBy_Missing("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
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

    public void setCreatedTime_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setCreatedTime_Histogram("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
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

    public void setCreatedTime_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setCreatedTime_Range("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
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

    public void setCreatedTime_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setCreatedTime_Missing("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Integer depth
    public void setDepth_Avg() {
        setDepth_Avg(null);
    }

    public void setDepth_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setDepth_Avg("depth", opLambda);
    }

    public void setDepth_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Max() {
        setDepth_Max(null);
    }

    public void setDepth_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setDepth_Max("depth", opLambda);
    }

    public void setDepth_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Min() {
        setDepth_Min(null);
    }

    public void setDepth_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setDepth_Min("depth", opLambda);
    }

    public void setDepth_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Sum() {
        setDepth_Sum(null);
    }

    public void setDepth_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setDepth_Sum("depth", opLambda);
    }

    public void setDepth_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_ExtendedStats() {
        setDepth_ExtendedStats(null);
    }

    public void setDepth_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setDepth_ExtendedStats("depth", opLambda);
    }

    public void setDepth_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Stats() {
        setDepth_Stats(null);
    }

    public void setDepth_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setDepth_Stats("depth", opLambda);
    }

    public void setDepth_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Percentiles() {
        setDepth_Percentiles(null);
    }

    public void setDepth_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setDepth_Percentiles("depth", opLambda);
    }

    public void setDepth_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_PercentileRanks() {
        setDepth_PercentileRanks(null);
    }

    public void setDepth_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setDepth_PercentileRanks("depth", opLambda);
    }

    public void setDepth_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Histogram() {
        setDepth_Histogram(null);
    }

    public void setDepth_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setDepth_Histogram("depth", opLambda, null);
    }

    public void setDepth_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setDepth_Histogram("depth", opLambda, aggsLambda);
    }

    public void setDepth_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDepth_Range() {
        setDepth_Range(null);
    }

    public void setDepth_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setDepth_Range("depth", opLambda, null);
    }

    public void setDepth_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setDepth_Range("depth", opLambda, aggsLambda);
    }

    public void setDepth_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDepth_Count() {
        setDepth_Count(null);
    }

    public void setDepth_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setDepth_Count("depth", opLambda);
    }

    public void setDepth_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Cardinality() {
        setDepth_Cardinality(null);
    }

    public void setDepth_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setDepth_Cardinality("depth", opLambda);
    }

    public void setDepth_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Missing() {
        setDepth_Missing(null);
    }

    public void setDepth_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setDepth_Missing("depth", opLambda, null);
    }

    public void setDepth_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setDepth_Missing("depth", opLambda, aggsLambda);
    }

    public void setDepth_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String excludedDocPaths

    public void setExcludedDocPaths_Terms() {
        setExcludedDocPaths_Terms(null);
    }

    public void setExcludedDocPaths_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setExcludedDocPaths_Terms("excludedDocPaths", opLambda, null);
    }

    public void setExcludedDocPaths_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setExcludedDocPaths_Terms("excludedDocPaths", opLambda, aggsLambda);
    }

    public void setExcludedDocPaths_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "excludedDocPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setExcludedDocPaths_SignificantTerms() {
        setExcludedDocPaths_SignificantTerms(null);
    }

    public void setExcludedDocPaths_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setExcludedDocPaths_SignificantTerms("excludedDocPaths", opLambda, null);
    }

    public void setExcludedDocPaths_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setExcludedDocPaths_SignificantTerms("excludedDocPaths", opLambda, aggsLambda);
    }

    public void setExcludedDocPaths_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "excludedDocPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setExcludedDocPaths_IpRange() {
        setExcludedDocPaths_IpRange(null);
    }

    public void setExcludedDocPaths_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setExcludedDocPaths_IpRange("excludedDocPaths", opLambda, null);
    }

    public void setExcludedDocPaths_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setExcludedDocPaths_IpRange("excludedDocPaths", opLambda, aggsLambda);
    }

    public void setExcludedDocPaths_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "excludedDocPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setExcludedDocPaths_Count() {
        setExcludedDocPaths_Count(null);
    }

    public void setExcludedDocPaths_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setExcludedDocPaths_Count("excludedDocPaths", opLambda);
    }

    public void setExcludedDocPaths_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "excludedDocPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocPaths_Cardinality() {
        setExcludedDocPaths_Cardinality(null);
    }

    public void setExcludedDocPaths_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setExcludedDocPaths_Cardinality("excludedDocPaths", opLambda);
    }

    public void setExcludedDocPaths_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "excludedDocPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocPaths_Missing() {
        setExcludedDocPaths_Missing(null);
    }

    public void setExcludedDocPaths_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setExcludedDocPaths_Missing("excludedDocPaths", opLambda, null);
    }

    public void setExcludedDocPaths_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setExcludedDocPaths_Missing("excludedDocPaths", opLambda, aggsLambda);
    }

    public void setExcludedDocPaths_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "excludedDocPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String excludedPaths

    public void setExcludedPaths_Terms() {
        setExcludedPaths_Terms(null);
    }

    public void setExcludedPaths_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setExcludedPaths_Terms("excludedPaths", opLambda, null);
    }

    public void setExcludedPaths_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setExcludedPaths_Terms("excludedPaths", opLambda, aggsLambda);
    }

    public void setExcludedPaths_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "excludedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setExcludedPaths_SignificantTerms() {
        setExcludedPaths_SignificantTerms(null);
    }

    public void setExcludedPaths_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setExcludedPaths_SignificantTerms("excludedPaths", opLambda, null);
    }

    public void setExcludedPaths_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setExcludedPaths_SignificantTerms("excludedPaths", opLambda, aggsLambda);
    }

    public void setExcludedPaths_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "excludedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setExcludedPaths_IpRange() {
        setExcludedPaths_IpRange(null);
    }

    public void setExcludedPaths_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setExcludedPaths_IpRange("excludedPaths", opLambda, null);
    }

    public void setExcludedPaths_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setExcludedPaths_IpRange("excludedPaths", opLambda, aggsLambda);
    }

    public void setExcludedPaths_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "excludedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setExcludedPaths_Count() {
        setExcludedPaths_Count(null);
    }

    public void setExcludedPaths_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setExcludedPaths_Count("excludedPaths", opLambda);
    }

    public void setExcludedPaths_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "excludedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_Cardinality() {
        setExcludedPaths_Cardinality(null);
    }

    public void setExcludedPaths_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setExcludedPaths_Cardinality("excludedPaths", opLambda);
    }

    public void setExcludedPaths_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "excludedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_Missing() {
        setExcludedPaths_Missing(null);
    }

    public void setExcludedPaths_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setExcludedPaths_Missing("excludedPaths", opLambda, null);
    }

    public void setExcludedPaths_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setExcludedPaths_Missing("excludedPaths", opLambda, aggsLambda);
    }

    public void setExcludedPaths_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "excludedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String includedDocPaths

    public void setIncludedDocPaths_Terms() {
        setIncludedDocPaths_Terms(null);
    }

    public void setIncludedDocPaths_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setIncludedDocPaths_Terms("includedDocPaths", opLambda, null);
    }

    public void setIncludedDocPaths_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setIncludedDocPaths_Terms("includedDocPaths", opLambda, aggsLambda);
    }

    public void setIncludedDocPaths_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "includedDocPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setIncludedDocPaths_SignificantTerms() {
        setIncludedDocPaths_SignificantTerms(null);
    }

    public void setIncludedDocPaths_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setIncludedDocPaths_SignificantTerms("includedDocPaths", opLambda, null);
    }

    public void setIncludedDocPaths_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setIncludedDocPaths_SignificantTerms("includedDocPaths", opLambda, aggsLambda);
    }

    public void setIncludedDocPaths_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "includedDocPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setIncludedDocPaths_IpRange() {
        setIncludedDocPaths_IpRange(null);
    }

    public void setIncludedDocPaths_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setIncludedDocPaths_IpRange("includedDocPaths", opLambda, null);
    }

    public void setIncludedDocPaths_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setIncludedDocPaths_IpRange("includedDocPaths", opLambda, aggsLambda);
    }

    public void setIncludedDocPaths_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "includedDocPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setIncludedDocPaths_Count() {
        setIncludedDocPaths_Count(null);
    }

    public void setIncludedDocPaths_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setIncludedDocPaths_Count("includedDocPaths", opLambda);
    }

    public void setIncludedDocPaths_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "includedDocPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocPaths_Cardinality() {
        setIncludedDocPaths_Cardinality(null);
    }

    public void setIncludedDocPaths_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setIncludedDocPaths_Cardinality("includedDocPaths", opLambda);
    }

    public void setIncludedDocPaths_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "includedDocPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocPaths_Missing() {
        setIncludedDocPaths_Missing(null);
    }

    public void setIncludedDocPaths_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setIncludedDocPaths_Missing("includedDocPaths", opLambda, null);
    }

    public void setIncludedDocPaths_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setIncludedDocPaths_Missing("includedDocPaths", opLambda, aggsLambda);
    }

    public void setIncludedDocPaths_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "includedDocPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String includedPaths

    public void setIncludedPaths_Terms() {
        setIncludedPaths_Terms(null);
    }

    public void setIncludedPaths_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setIncludedPaths_Terms("includedPaths", opLambda, null);
    }

    public void setIncludedPaths_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setIncludedPaths_Terms("includedPaths", opLambda, aggsLambda);
    }

    public void setIncludedPaths_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "includedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setIncludedPaths_SignificantTerms() {
        setIncludedPaths_SignificantTerms(null);
    }

    public void setIncludedPaths_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setIncludedPaths_SignificantTerms("includedPaths", opLambda, null);
    }

    public void setIncludedPaths_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setIncludedPaths_SignificantTerms("includedPaths", opLambda, aggsLambda);
    }

    public void setIncludedPaths_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "includedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setIncludedPaths_IpRange() {
        setIncludedPaths_IpRange(null);
    }

    public void setIncludedPaths_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setIncludedPaths_IpRange("includedPaths", opLambda, null);
    }

    public void setIncludedPaths_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setIncludedPaths_IpRange("includedPaths", opLambda, aggsLambda);
    }

    public void setIncludedPaths_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "includedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setIncludedPaths_Count() {
        setIncludedPaths_Count(null);
    }

    public void setIncludedPaths_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setIncludedPaths_Count("includedPaths", opLambda);
    }

    public void setIncludedPaths_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "includedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_Cardinality() {
        setIncludedPaths_Cardinality(null);
    }

    public void setIncludedPaths_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setIncludedPaths_Cardinality("includedPaths", opLambda);
    }

    public void setIncludedPaths_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "includedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_Missing() {
        setIncludedPaths_Missing(null);
    }

    public void setIncludedPaths_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setIncludedPaths_Missing("includedPaths", opLambda, null);
    }

    public void setIncludedPaths_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setIncludedPaths_Missing("includedPaths", opLambda, aggsLambda);
    }

    public void setIncludedPaths_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "includedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Integer intervalTime
    public void setIntervalTime_Avg() {
        setIntervalTime_Avg(null);
    }

    public void setIntervalTime_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setIntervalTime_Avg("intervalTime", opLambda);
    }

    public void setIntervalTime_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Max() {
        setIntervalTime_Max(null);
    }

    public void setIntervalTime_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setIntervalTime_Max("intervalTime", opLambda);
    }

    public void setIntervalTime_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Min() {
        setIntervalTime_Min(null);
    }

    public void setIntervalTime_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setIntervalTime_Min("intervalTime", opLambda);
    }

    public void setIntervalTime_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Sum() {
        setIntervalTime_Sum(null);
    }

    public void setIntervalTime_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setIntervalTime_Sum("intervalTime", opLambda);
    }

    public void setIntervalTime_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_ExtendedStats() {
        setIntervalTime_ExtendedStats(null);
    }

    public void setIntervalTime_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setIntervalTime_ExtendedStats("intervalTime", opLambda);
    }

    public void setIntervalTime_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Stats() {
        setIntervalTime_Stats(null);
    }

    public void setIntervalTime_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setIntervalTime_Stats("intervalTime", opLambda);
    }

    public void setIntervalTime_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Percentiles() {
        setIntervalTime_Percentiles(null);
    }

    public void setIntervalTime_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setIntervalTime_Percentiles("intervalTime", opLambda);
    }

    public void setIntervalTime_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_PercentileRanks() {
        setIntervalTime_PercentileRanks(null);
    }

    public void setIntervalTime_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setIntervalTime_PercentileRanks("intervalTime", opLambda);
    }

    public void setIntervalTime_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Histogram() {
        setIntervalTime_Histogram(null);
    }

    public void setIntervalTime_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setIntervalTime_Histogram("intervalTime", opLambda, null);
    }

    public void setIntervalTime_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setIntervalTime_Histogram("intervalTime", opLambda, aggsLambda);
    }

    public void setIntervalTime_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setIntervalTime_Range() {
        setIntervalTime_Range(null);
    }

    public void setIntervalTime_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setIntervalTime_Range("intervalTime", opLambda, null);
    }

    public void setIntervalTime_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setIntervalTime_Range("intervalTime", opLambda, aggsLambda);
    }

    public void setIntervalTime_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setIntervalTime_Count() {
        setIntervalTime_Count(null);
    }

    public void setIntervalTime_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setIntervalTime_Count("intervalTime", opLambda);
    }

    public void setIntervalTime_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Cardinality() {
        setIntervalTime_Cardinality(null);
    }

    public void setIntervalTime_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setIntervalTime_Cardinality("intervalTime", opLambda);
    }

    public void setIntervalTime_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Missing() {
        setIntervalTime_Missing(null);
    }

    public void setIntervalTime_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setIntervalTime_Missing("intervalTime", opLambda, null);
    }

    public void setIntervalTime_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setIntervalTime_Missing("intervalTime", opLambda, aggsLambda);
    }

    public void setIntervalTime_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Integer timeToLive
    public void setTimeToLive_Avg() {
        setTimeToLive_Avg(null);
    }

    public void setTimeToLive_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setTimeToLive_Avg("timeToLive", opLambda);
    }

    public void setTimeToLive_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_Max() {
        setTimeToLive_Max(null);
    }

    public void setTimeToLive_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setTimeToLive_Max("timeToLive", opLambda);
    }

    public void setTimeToLive_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_Min() {
        setTimeToLive_Min(null);
    }

    public void setTimeToLive_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setTimeToLive_Min("timeToLive", opLambda);
    }

    public void setTimeToLive_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_Sum() {
        setTimeToLive_Sum(null);
    }

    public void setTimeToLive_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setTimeToLive_Sum("timeToLive", opLambda);
    }

    public void setTimeToLive_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_ExtendedStats() {
        setTimeToLive_ExtendedStats(null);
    }

    public void setTimeToLive_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setTimeToLive_ExtendedStats("timeToLive", opLambda);
    }

    public void setTimeToLive_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_Stats() {
        setTimeToLive_Stats(null);
    }

    public void setTimeToLive_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setTimeToLive_Stats("timeToLive", opLambda);
    }

    public void setTimeToLive_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_Percentiles() {
        setTimeToLive_Percentiles(null);
    }

    public void setTimeToLive_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setTimeToLive_Percentiles("timeToLive", opLambda);
    }

    public void setTimeToLive_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_PercentileRanks() {
        setTimeToLive_PercentileRanks(null);
    }

    public void setTimeToLive_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setTimeToLive_PercentileRanks("timeToLive", opLambda);
    }

    public void setTimeToLive_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_Histogram() {
        setTimeToLive_Histogram(null);
    }

    public void setTimeToLive_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setTimeToLive_Histogram("timeToLive", opLambda, null);
    }

    public void setTimeToLive_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setTimeToLive_Histogram("timeToLive", opLambda, aggsLambda);
    }

    public void setTimeToLive_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTimeToLive_Range() {
        setTimeToLive_Range(null);
    }

    public void setTimeToLive_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setTimeToLive_Range("timeToLive", opLambda, null);
    }

    public void setTimeToLive_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setTimeToLive_Range("timeToLive", opLambda, aggsLambda);
    }

    public void setTimeToLive_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTimeToLive_Count() {
        setTimeToLive_Count(null);
    }

    public void setTimeToLive_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setTimeToLive_Count("timeToLive", opLambda);
    }

    public void setTimeToLive_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_Cardinality() {
        setTimeToLive_Cardinality(null);
    }

    public void setTimeToLive_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setTimeToLive_Cardinality("timeToLive", opLambda);
    }

    public void setTimeToLive_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_Missing() {
        setTimeToLive_Missing(null);
    }

    public void setTimeToLive_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setTimeToLive_Missing("timeToLive", opLambda, null);
    }

    public void setTimeToLive_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setTimeToLive_Missing("timeToLive", opLambda, aggsLambda);
    }

    public void setTimeToLive_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Long maxAccessCount
    public void setMaxAccessCount_Avg() {
        setMaxAccessCount_Avg(null);
    }

    public void setMaxAccessCount_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setMaxAccessCount_Avg("maxAccessCount", opLambda);
    }

    public void setMaxAccessCount_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Max() {
        setMaxAccessCount_Max(null);
    }

    public void setMaxAccessCount_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setMaxAccessCount_Max("maxAccessCount", opLambda);
    }

    public void setMaxAccessCount_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Min() {
        setMaxAccessCount_Min(null);
    }

    public void setMaxAccessCount_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setMaxAccessCount_Min("maxAccessCount", opLambda);
    }

    public void setMaxAccessCount_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Sum() {
        setMaxAccessCount_Sum(null);
    }

    public void setMaxAccessCount_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setMaxAccessCount_Sum("maxAccessCount", opLambda);
    }

    public void setMaxAccessCount_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_ExtendedStats() {
        setMaxAccessCount_ExtendedStats(null);
    }

    public void setMaxAccessCount_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setMaxAccessCount_ExtendedStats("maxAccessCount", opLambda);
    }

    public void setMaxAccessCount_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Stats() {
        setMaxAccessCount_Stats(null);
    }

    public void setMaxAccessCount_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setMaxAccessCount_Stats("maxAccessCount", opLambda);
    }

    public void setMaxAccessCount_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Percentiles() {
        setMaxAccessCount_Percentiles(null);
    }

    public void setMaxAccessCount_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setMaxAccessCount_Percentiles("maxAccessCount", opLambda);
    }

    public void setMaxAccessCount_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_PercentileRanks() {
        setMaxAccessCount_PercentileRanks(null);
    }

    public void setMaxAccessCount_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setMaxAccessCount_PercentileRanks("maxAccessCount", opLambda);
    }

    public void setMaxAccessCount_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Histogram() {
        setMaxAccessCount_Histogram(null);
    }

    public void setMaxAccessCount_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setMaxAccessCount_Histogram("maxAccessCount", opLambda, null);
    }

    public void setMaxAccessCount_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setMaxAccessCount_Histogram("maxAccessCount", opLambda, aggsLambda);
    }

    public void setMaxAccessCount_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setMaxAccessCount_Range() {
        setMaxAccessCount_Range(null);
    }

    public void setMaxAccessCount_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setMaxAccessCount_Range("maxAccessCount", opLambda, null);
    }

    public void setMaxAccessCount_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setMaxAccessCount_Range("maxAccessCount", opLambda, aggsLambda);
    }

    public void setMaxAccessCount_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setMaxAccessCount_Count() {
        setMaxAccessCount_Count(null);
    }

    public void setMaxAccessCount_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setMaxAccessCount_Count("maxAccessCount", opLambda);
    }

    public void setMaxAccessCount_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Cardinality() {
        setMaxAccessCount_Cardinality(null);
    }

    public void setMaxAccessCount_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setMaxAccessCount_Cardinality("maxAccessCount", opLambda);
    }

    public void setMaxAccessCount_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Missing() {
        setMaxAccessCount_Missing(null);
    }

    public void setMaxAccessCount_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setMaxAccessCount_Missing("maxAccessCount", opLambda, null);
    }

    public void setMaxAccessCount_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setMaxAccessCount_Missing("maxAccessCount", opLambda, aggsLambda);
    }

    public void setMaxAccessCount_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String name

    public void setName_Terms() {
        setName_Terms(null);
    }

    public void setName_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setName_Terms("name", opLambda, null);
    }

    public void setName_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setName_Terms("name", opLambda, aggsLambda);
    }

    public void setName_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setName_SignificantTerms() {
        setName_SignificantTerms(null);
    }

    public void setName_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setName_SignificantTerms("name", opLambda, null);
    }

    public void setName_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setName_SignificantTerms("name", opLambda, aggsLambda);
    }

    public void setName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setName_IpRange() {
        setName_IpRange(null);
    }

    public void setName_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setName_IpRange("name", opLambda, null);
    }

    public void setName_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setName_IpRange("name", opLambda, aggsLambda);
    }

    public void setName_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setName_Count() {
        setName_Count(null);
    }

    public void setName_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setName_Count("name", opLambda);
    }

    public void setName_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Cardinality() {
        setName_Cardinality(null);
    }

    public void setName_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setName_Cardinality("name", opLambda);
    }

    public void setName_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Missing() {
        setName_Missing(null);
    }

    public void setName_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setName_Missing("name", opLambda, null);
    }

    public void setName_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setName_Missing("name", opLambda, aggsLambda);
    }

    public void setName_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Integer numOfThread
    public void setNumOfThread_Avg() {
        setNumOfThread_Avg(null);
    }

    public void setNumOfThread_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setNumOfThread_Avg("numOfThread", opLambda);
    }

    public void setNumOfThread_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Max() {
        setNumOfThread_Max(null);
    }

    public void setNumOfThread_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setNumOfThread_Max("numOfThread", opLambda);
    }

    public void setNumOfThread_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Min() {
        setNumOfThread_Min(null);
    }

    public void setNumOfThread_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setNumOfThread_Min("numOfThread", opLambda);
    }

    public void setNumOfThread_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Sum() {
        setNumOfThread_Sum(null);
    }

    public void setNumOfThread_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setNumOfThread_Sum("numOfThread", opLambda);
    }

    public void setNumOfThread_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_ExtendedStats() {
        setNumOfThread_ExtendedStats(null);
    }

    public void setNumOfThread_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setNumOfThread_ExtendedStats("numOfThread", opLambda);
    }

    public void setNumOfThread_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Stats() {
        setNumOfThread_Stats(null);
    }

    public void setNumOfThread_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setNumOfThread_Stats("numOfThread", opLambda);
    }

    public void setNumOfThread_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Percentiles() {
        setNumOfThread_Percentiles(null);
    }

    public void setNumOfThread_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setNumOfThread_Percentiles("numOfThread", opLambda);
    }

    public void setNumOfThread_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_PercentileRanks() {
        setNumOfThread_PercentileRanks(null);
    }

    public void setNumOfThread_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setNumOfThread_PercentileRanks("numOfThread", opLambda);
    }

    public void setNumOfThread_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Histogram() {
        setNumOfThread_Histogram(null);
    }

    public void setNumOfThread_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setNumOfThread_Histogram("numOfThread", opLambda, null);
    }

    public void setNumOfThread_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setNumOfThread_Histogram("numOfThread", opLambda, aggsLambda);
    }

    public void setNumOfThread_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setNumOfThread_Range() {
        setNumOfThread_Range(null);
    }

    public void setNumOfThread_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setNumOfThread_Range("numOfThread", opLambda, null);
    }

    public void setNumOfThread_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setNumOfThread_Range("numOfThread", opLambda, aggsLambda);
    }

    public void setNumOfThread_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setNumOfThread_Count() {
        setNumOfThread_Count(null);
    }

    public void setNumOfThread_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setNumOfThread_Count("numOfThread", opLambda);
    }

    public void setNumOfThread_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Cardinality() {
        setNumOfThread_Cardinality(null);
    }

    public void setNumOfThread_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setNumOfThread_Cardinality("numOfThread", opLambda);
    }

    public void setNumOfThread_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Missing() {
        setNumOfThread_Missing(null);
    }

    public void setNumOfThread_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setNumOfThread_Missing("numOfThread", opLambda, null);
    }

    public void setNumOfThread_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setNumOfThread_Missing("numOfThread", opLambda, aggsLambda);
    }

    public void setNumOfThread_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String paths

    public void setPaths_Terms() {
        setPaths_Terms(null);
    }

    public void setPaths_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setPaths_Terms("paths", opLambda, null);
    }

    public void setPaths_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setPaths_Terms("paths", opLambda, aggsLambda);
    }

    public void setPaths_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "paths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPaths_SignificantTerms() {
        setPaths_SignificantTerms(null);
    }

    public void setPaths_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setPaths_SignificantTerms("paths", opLambda, null);
    }

    public void setPaths_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setPaths_SignificantTerms("paths", opLambda, aggsLambda);
    }

    public void setPaths_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "paths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPaths_IpRange() {
        setPaths_IpRange(null);
    }

    public void setPaths_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setPaths_IpRange("paths", opLambda, null);
    }

    public void setPaths_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setPaths_IpRange("paths", opLambda, aggsLambda);
    }

    public void setPaths_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "paths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPaths_Count() {
        setPaths_Count(null);
    }

    public void setPaths_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setPaths_Count("paths", opLambda);
    }

    public void setPaths_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "paths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPaths_Cardinality() {
        setPaths_Cardinality(null);
    }

    public void setPaths_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setPaths_Cardinality("paths", opLambda);
    }

    public void setPaths_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "paths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPaths_Missing() {
        setPaths_Missing(null);
    }

    public void setPaths_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setPaths_Missing("paths", opLambda, null);
    }

    public void setPaths_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setPaths_Missing("paths", opLambda, aggsLambda);
    }

    public void setPaths_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "paths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String permissions

    public void setPermissions_Terms() {
        setPermissions_Terms(null);
    }

    public void setPermissions_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setPermissions_Terms("permissions", opLambda, null);
    }

    public void setPermissions_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setPermissions_Terms("permissions", opLambda, aggsLambda);
    }

    public void setPermissions_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "permissions");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPermissions_SignificantTerms() {
        setPermissions_SignificantTerms(null);
    }

    public void setPermissions_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setPermissions_SignificantTerms("permissions", opLambda, null);
    }

    public void setPermissions_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setPermissions_SignificantTerms("permissions", opLambda, aggsLambda);
    }

    public void setPermissions_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "permissions");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPermissions_IpRange() {
        setPermissions_IpRange(null);
    }

    public void setPermissions_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setPermissions_IpRange("permissions", opLambda, null);
    }

    public void setPermissions_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setPermissions_IpRange("permissions", opLambda, aggsLambda);
    }

    public void setPermissions_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "permissions");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPermissions_Count() {
        setPermissions_Count(null);
    }

    public void setPermissions_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setPermissions_Count("permissions", opLambda);
    }

    public void setPermissions_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "permissions");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPermissions_Cardinality() {
        setPermissions_Cardinality(null);
    }

    public void setPermissions_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setPermissions_Cardinality("permissions", opLambda);
    }

    public void setPermissions_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "permissions");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPermissions_Missing() {
        setPermissions_Missing(null);
    }

    public void setPermissions_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setPermissions_Missing("permissions", opLambda, null);
    }

    public void setPermissions_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setPermissions_Missing("permissions", opLambda, aggsLambda);
    }

    public void setPermissions_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "permissions");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Integer sortOrder
    public void setSortOrder_Avg() {
        setSortOrder_Avg(null);
    }

    public void setSortOrder_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setSortOrder_Avg("sortOrder", opLambda);
    }

    public void setSortOrder_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Max() {
        setSortOrder_Max(null);
    }

    public void setSortOrder_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setSortOrder_Max("sortOrder", opLambda);
    }

    public void setSortOrder_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Min() {
        setSortOrder_Min(null);
    }

    public void setSortOrder_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setSortOrder_Min("sortOrder", opLambda);
    }

    public void setSortOrder_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Sum() {
        setSortOrder_Sum(null);
    }

    public void setSortOrder_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setSortOrder_Sum("sortOrder", opLambda);
    }

    public void setSortOrder_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_ExtendedStats() {
        setSortOrder_ExtendedStats(null);
    }

    public void setSortOrder_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setSortOrder_ExtendedStats("sortOrder", opLambda);
    }

    public void setSortOrder_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Stats() {
        setSortOrder_Stats(null);
    }

    public void setSortOrder_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setSortOrder_Stats("sortOrder", opLambda);
    }

    public void setSortOrder_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Percentiles() {
        setSortOrder_Percentiles(null);
    }

    public void setSortOrder_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setSortOrder_Percentiles("sortOrder", opLambda);
    }

    public void setSortOrder_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_PercentileRanks() {
        setSortOrder_PercentileRanks(null);
    }

    public void setSortOrder_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setSortOrder_PercentileRanks("sortOrder", opLambda);
    }

    public void setSortOrder_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Histogram() {
        setSortOrder_Histogram(null);
    }

    public void setSortOrder_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setSortOrder_Histogram("sortOrder", opLambda, null);
    }

    public void setSortOrder_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setSortOrder_Histogram("sortOrder", opLambda, aggsLambda);
    }

    public void setSortOrder_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSortOrder_Range() {
        setSortOrder_Range(null);
    }

    public void setSortOrder_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setSortOrder_Range("sortOrder", opLambda, null);
    }

    public void setSortOrder_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setSortOrder_Range("sortOrder", opLambda, aggsLambda);
    }

    public void setSortOrder_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSortOrder_Count() {
        setSortOrder_Count(null);
    }

    public void setSortOrder_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setSortOrder_Count("sortOrder", opLambda);
    }

    public void setSortOrder_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Cardinality() {
        setSortOrder_Cardinality(null);
    }

    public void setSortOrder_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setSortOrder_Cardinality("sortOrder", opLambda);
    }

    public void setSortOrder_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Missing() {
        setSortOrder_Missing(null);
    }

    public void setSortOrder_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setSortOrder_Missing("sortOrder", opLambda, null);
    }

    public void setSortOrder_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setSortOrder_Missing("sortOrder", opLambda, aggsLambda);
    }

    public void setSortOrder_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String updatedBy

    public void setUpdatedBy_Terms() {
        setUpdatedBy_Terms(null);
    }

    public void setUpdatedBy_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setUpdatedBy_Terms("updatedBy", opLambda, null);
    }

    public void setUpdatedBy_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setUpdatedBy_Terms("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUpdatedBy_SignificantTerms() {
        setUpdatedBy_SignificantTerms(null);
    }

    public void setUpdatedBy_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setUpdatedBy_SignificantTerms("updatedBy", opLambda, null);
    }

    public void setUpdatedBy_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setUpdatedBy_SignificantTerms("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUpdatedBy_IpRange() {
        setUpdatedBy_IpRange(null);
    }

    public void setUpdatedBy_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setUpdatedBy_IpRange("updatedBy", opLambda, null);
    }

    public void setUpdatedBy_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setUpdatedBy_IpRange("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUpdatedBy_Count() {
        setUpdatedBy_Count(null);
    }

    public void setUpdatedBy_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setUpdatedBy_Count("updatedBy", opLambda);
    }

    public void setUpdatedBy_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Cardinality() {
        setUpdatedBy_Cardinality(null);
    }

    public void setUpdatedBy_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setUpdatedBy_Cardinality("updatedBy", opLambda);
    }

    public void setUpdatedBy_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Missing() {
        setUpdatedBy_Missing(null);
    }

    public void setUpdatedBy_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setUpdatedBy_Missing("updatedBy", opLambda, null);
    }

    public void setUpdatedBy_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setUpdatedBy_Missing("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Long updatedTime
    public void setUpdatedTime_Avg() {
        setUpdatedTime_Avg(null);
    }

    public void setUpdatedTime_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setUpdatedTime_Avg("updatedTime", opLambda);
    }

    public void setUpdatedTime_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Max() {
        setUpdatedTime_Max(null);
    }

    public void setUpdatedTime_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setUpdatedTime_Max("updatedTime", opLambda);
    }

    public void setUpdatedTime_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Min() {
        setUpdatedTime_Min(null);
    }

    public void setUpdatedTime_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setUpdatedTime_Min("updatedTime", opLambda);
    }

    public void setUpdatedTime_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Sum() {
        setUpdatedTime_Sum(null);
    }

    public void setUpdatedTime_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setUpdatedTime_Sum("updatedTime", opLambda);
    }

    public void setUpdatedTime_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_ExtendedStats() {
        setUpdatedTime_ExtendedStats(null);
    }

    public void setUpdatedTime_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setUpdatedTime_ExtendedStats("updatedTime", opLambda);
    }

    public void setUpdatedTime_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Stats() {
        setUpdatedTime_Stats(null);
    }

    public void setUpdatedTime_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setUpdatedTime_Stats("updatedTime", opLambda);
    }

    public void setUpdatedTime_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Percentiles() {
        setUpdatedTime_Percentiles(null);
    }

    public void setUpdatedTime_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setUpdatedTime_Percentiles("updatedTime", opLambda);
    }

    public void setUpdatedTime_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_PercentileRanks() {
        setUpdatedTime_PercentileRanks(null);
    }

    public void setUpdatedTime_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setUpdatedTime_PercentileRanks("updatedTime", opLambda);
    }

    public void setUpdatedTime_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Histogram() {
        setUpdatedTime_Histogram(null);
    }

    public void setUpdatedTime_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setUpdatedTime_Histogram("updatedTime", opLambda, null);
    }

    public void setUpdatedTime_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setUpdatedTime_Histogram("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUpdatedTime_Range() {
        setUpdatedTime_Range(null);
    }

    public void setUpdatedTime_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setUpdatedTime_Range("updatedTime", opLambda, null);
    }

    public void setUpdatedTime_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setUpdatedTime_Range("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUpdatedTime_Count() {
        setUpdatedTime_Count(null);
    }

    public void setUpdatedTime_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setUpdatedTime_Count("updatedTime", opLambda);
    }

    public void setUpdatedTime_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Cardinality() {
        setUpdatedTime_Cardinality(null);
    }

    public void setUpdatedTime_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setUpdatedTime_Cardinality("updatedTime", opLambda);
    }

    public void setUpdatedTime_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Missing() {
        setUpdatedTime_Missing(null);
    }

    public void setUpdatedTime_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setUpdatedTime_Missing("updatedTime", opLambda, null);
    }

    public void setUpdatedTime_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setUpdatedTime_Missing("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
