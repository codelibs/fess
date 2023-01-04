/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
import org.opensearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.opensearch.search.aggregations.bucket.global.GlobalAggregationBuilder;
import org.opensearch.search.aggregations.bucket.histogram.HistogramAggregationBuilder;
import org.opensearch.search.aggregations.bucket.missing.MissingAggregationBuilder;
import org.opensearch.search.aggregations.bucket.range.IpRangeAggregationBuilder;
import org.opensearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.opensearch.search.aggregations.bucket.sampler.SamplerAggregationBuilder;
import org.opensearch.search.aggregations.bucket.terms.SignificantTermsAggregationBuilder;
import org.opensearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.opensearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.opensearch.search.aggregations.metrics.CardinalityAggregationBuilder;
import org.opensearch.search.aggregations.metrics.ExtendedStatsAggregationBuilder;
import org.opensearch.search.aggregations.metrics.MaxAggregationBuilder;
import org.opensearch.search.aggregations.metrics.MinAggregationBuilder;
import org.opensearch.search.aggregations.metrics.PercentileRanksAggregationBuilder;
import org.opensearch.search.aggregations.metrics.PercentilesAggregationBuilder;
import org.opensearch.search.aggregations.metrics.ScriptedMetricAggregationBuilder;
import org.opensearch.search.aggregations.metrics.StatsAggregationBuilder;
import org.opensearch.search.aggregations.metrics.SumAggregationBuilder;
import org.opensearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.opensearch.search.aggregations.metrics.ValueCountAggregationBuilder;

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

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
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

    public void setAvailable_Avg() {
        setAvailable_Avg(null);
    }

    public void setAvailable_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setAvailable_Avg("available", opLambda);
    }

    public void setAvailable_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Max() {
        setAvailable_Max(null);
    }

    public void setAvailable_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setAvailable_Max("available", opLambda);
    }

    public void setAvailable_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Min() {
        setAvailable_Min(null);
    }

    public void setAvailable_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setAvailable_Min("available", opLambda);
    }

    public void setAvailable_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Sum() {
        setAvailable_Sum(null);
    }

    public void setAvailable_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setAvailable_Sum("available", opLambda);
    }

    public void setAvailable_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_ExtendedStats() {
        setAvailable_ExtendedStats(null);
    }

    public void setAvailable_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setAvailable_ExtendedStats("available", opLambda);
    }

    public void setAvailable_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Stats() {
        setAvailable_Stats(null);
    }

    public void setAvailable_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setAvailable_Stats("available", opLambda);
    }

    public void setAvailable_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Percentiles() {
        setAvailable_Percentiles(null);
    }

    public void setAvailable_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setAvailable_Percentiles("available", opLambda);
    }

    public void setAvailable_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_PercentileRanks(double[] values) {
        setAvailable_PercentileRanks(values, null);
    }

    public void setAvailable_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setAvailable_PercentileRanks("available", values, opLambda);
    }

    public void setAvailable_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "available", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Histogram() {
        setAvailable_Histogram(null);
    }

    public void setAvailable_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setAvailable_Histogram("available", opLambda, null);
    }

    public void setAvailable_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setAvailable_Histogram("available", opLambda, aggsLambda);
    }

    public void setAvailable_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "available");
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

    public void setAvailable_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setAvailable_Range("available", opLambda, null);
    }

    public void setAvailable_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setAvailable_Range("available", opLambda, aggsLambda);
    }

    public void setAvailable_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "available");
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

    public void setAvailable_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setAvailable_Count("available", opLambda);
    }

    public void setAvailable_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Cardinality() {
        setAvailable_Cardinality(null);
    }

    public void setAvailable_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setAvailable_Cardinality("available", opLambda);
    }

    public void setAvailable_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAvailable_Missing() {
        setAvailable_Missing(null);
    }

    public void setAvailable_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setAvailable_Missing("available", opLambda, null);
    }

    public void setAvailable_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setAvailable_Missing("available", opLambda, aggsLambda);
    }

    public void setAvailable_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setBoost_Avg() {
        setBoost_Avg(null);
    }

    public void setBoost_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setBoost_Avg("boost", opLambda);
    }

    public void setBoost_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Max() {
        setBoost_Max(null);
    }

    public void setBoost_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setBoost_Max("boost", opLambda);
    }

    public void setBoost_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Min() {
        setBoost_Min(null);
    }

    public void setBoost_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setBoost_Min("boost", opLambda);
    }

    public void setBoost_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Sum() {
        setBoost_Sum(null);
    }

    public void setBoost_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setBoost_Sum("boost", opLambda);
    }

    public void setBoost_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_ExtendedStats() {
        setBoost_ExtendedStats(null);
    }

    public void setBoost_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setBoost_ExtendedStats("boost", opLambda);
    }

    public void setBoost_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Stats() {
        setBoost_Stats(null);
    }

    public void setBoost_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setBoost_Stats("boost", opLambda);
    }

    public void setBoost_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Percentiles() {
        setBoost_Percentiles(null);
    }

    public void setBoost_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setBoost_Percentiles("boost", opLambda);
    }

    public void setBoost_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_PercentileRanks(double[] values) {
        setBoost_PercentileRanks(values, null);
    }

    public void setBoost_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setBoost_PercentileRanks("boost", values, opLambda);
    }

    public void setBoost_PercentileRanks(String name, double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "boost", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Histogram() {
        setBoost_Histogram(null);
    }

    public void setBoost_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setBoost_Histogram("boost", opLambda, null);
    }

    public void setBoost_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setBoost_Histogram("boost", opLambda, aggsLambda);
    }

    public void setBoost_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "boost");
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

    public void setBoost_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setBoost_Range("boost", opLambda, null);
    }

    public void setBoost_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setBoost_Range("boost", opLambda, aggsLambda);
    }

    public void setBoost_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "boost");
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

    public void setBoost_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setBoost_Count("boost", opLambda);
    }

    public void setBoost_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Cardinality() {
        setBoost_Cardinality(null);
    }

    public void setBoost_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setBoost_Cardinality("boost", opLambda);
    }

    public void setBoost_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBoost_Missing() {
        setBoost_Missing(null);
    }

    public void setBoost_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setBoost_Missing("boost", opLambda, null);
    }

    public void setBoost_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setBoost_Missing("boost", opLambda, aggsLambda);
    }

    public void setBoost_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setConfigParameter_Terms() {
        setConfigParameter_Terms(null);
    }

    public void setConfigParameter_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setConfigParameter_Terms("configParameter", opLambda, null);
    }

    public void setConfigParameter_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setConfigParameter_Terms("configParameter", opLambda, aggsLambda);
    }

    public void setConfigParameter_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "configParameter");
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

    public void setConfigParameter_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setConfigParameter_SignificantTerms("configParameter", opLambda, null);
    }

    public void setConfigParameter_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setConfigParameter_SignificantTerms("configParameter", opLambda, aggsLambda);
    }

    public void setConfigParameter_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "configParameter");
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

    public void setConfigParameter_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setConfigParameter_IpRange("configParameter", opLambda, null);
    }

    public void setConfigParameter_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setConfigParameter_IpRange("configParameter", opLambda, aggsLambda);
    }

    public void setConfigParameter_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "configParameter");
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

    public void setConfigParameter_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setConfigParameter_Count("configParameter", opLambda);
    }

    public void setConfigParameter_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "configParameter");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_Cardinality() {
        setConfigParameter_Cardinality(null);
    }

    public void setConfigParameter_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setConfigParameter_Cardinality("configParameter", opLambda);
    }

    public void setConfigParameter_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "configParameter");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigParameter_Missing() {
        setConfigParameter_Missing(null);
    }

    public void setConfigParameter_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setConfigParameter_Missing("configParameter", opLambda, null);
    }

    public void setConfigParameter_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setConfigParameter_Missing("configParameter", opLambda, aggsLambda);
    }

    public void setConfigParameter_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "configParameter");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCreatedBy_Terms() {
        setCreatedBy_Terms(null);
    }

    public void setCreatedBy_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setCreatedBy_Terms("createdBy", opLambda, null);
    }

    public void setCreatedBy_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setCreatedBy_Terms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "createdBy");
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

    public void setCreatedBy_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setCreatedBy_SignificantTerms("createdBy", opLambda, null);
    }

    public void setCreatedBy_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setCreatedBy_SignificantTerms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "createdBy");
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

    public void setCreatedBy_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setCreatedBy_IpRange("createdBy", opLambda, null);
    }

    public void setCreatedBy_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setCreatedBy_IpRange("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "createdBy");
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

    public void setCreatedBy_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setCreatedBy_Count("createdBy", opLambda);
    }

    public void setCreatedBy_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Cardinality() {
        setCreatedBy_Cardinality(null);
    }

    public void setCreatedBy_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setCreatedBy_Cardinality("createdBy", opLambda);
    }

    public void setCreatedBy_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Missing() {
        setCreatedBy_Missing(null);
    }

    public void setCreatedBy_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setCreatedBy_Missing("createdBy", opLambda, null);
    }

    public void setCreatedBy_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setCreatedBy_Missing("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCreatedTime_Avg() {
        setCreatedTime_Avg(null);
    }

    public void setCreatedTime_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setCreatedTime_Avg("createdTime", opLambda);
    }

    public void setCreatedTime_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Max() {
        setCreatedTime_Max(null);
    }

    public void setCreatedTime_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setCreatedTime_Max("createdTime", opLambda);
    }

    public void setCreatedTime_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Min() {
        setCreatedTime_Min(null);
    }

    public void setCreatedTime_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setCreatedTime_Min("createdTime", opLambda);
    }

    public void setCreatedTime_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Sum() {
        setCreatedTime_Sum(null);
    }

    public void setCreatedTime_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setCreatedTime_Sum("createdTime", opLambda);
    }

    public void setCreatedTime_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_ExtendedStats() {
        setCreatedTime_ExtendedStats(null);
    }

    public void setCreatedTime_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setCreatedTime_ExtendedStats("createdTime", opLambda);
    }

    public void setCreatedTime_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Stats() {
        setCreatedTime_Stats(null);
    }

    public void setCreatedTime_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setCreatedTime_Stats("createdTime", opLambda);
    }

    public void setCreatedTime_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Percentiles() {
        setCreatedTime_Percentiles(null);
    }

    public void setCreatedTime_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setCreatedTime_Percentiles("createdTime", opLambda);
    }

    public void setCreatedTime_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_PercentileRanks(double[] values) {
        setCreatedTime_PercentileRanks(values, null);
    }

    public void setCreatedTime_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setCreatedTime_PercentileRanks("createdTime", values, opLambda);
    }

    public void setCreatedTime_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "createdTime", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Histogram() {
        setCreatedTime_Histogram(null);
    }

    public void setCreatedTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setCreatedTime_Histogram("createdTime", opLambda, null);
    }

    public void setCreatedTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setCreatedTime_Histogram("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "createdTime");
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

    public void setCreatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setCreatedTime_Range("createdTime", opLambda, null);
    }

    public void setCreatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setCreatedTime_Range("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "createdTime");
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

    public void setCreatedTime_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setCreatedTime_Count("createdTime", opLambda);
    }

    public void setCreatedTime_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Cardinality() {
        setCreatedTime_Cardinality(null);
    }

    public void setCreatedTime_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setCreatedTime_Cardinality("createdTime", opLambda);
    }

    public void setCreatedTime_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedTime_Missing() {
        setCreatedTime_Missing(null);
    }

    public void setCreatedTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setCreatedTime_Missing("createdTime", opLambda, null);
    }

    public void setCreatedTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setCreatedTime_Missing("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDepth_Avg() {
        setDepth_Avg(null);
    }

    public void setDepth_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setDepth_Avg("depth", opLambda);
    }

    public void setDepth_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Max() {
        setDepth_Max(null);
    }

    public void setDepth_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setDepth_Max("depth", opLambda);
    }

    public void setDepth_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Min() {
        setDepth_Min(null);
    }

    public void setDepth_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setDepth_Min("depth", opLambda);
    }

    public void setDepth_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Sum() {
        setDepth_Sum(null);
    }

    public void setDepth_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setDepth_Sum("depth", opLambda);
    }

    public void setDepth_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_ExtendedStats() {
        setDepth_ExtendedStats(null);
    }

    public void setDepth_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setDepth_ExtendedStats("depth", opLambda);
    }

    public void setDepth_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Stats() {
        setDepth_Stats(null);
    }

    public void setDepth_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setDepth_Stats("depth", opLambda);
    }

    public void setDepth_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Percentiles() {
        setDepth_Percentiles(null);
    }

    public void setDepth_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setDepth_Percentiles("depth", opLambda);
    }

    public void setDepth_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_PercentileRanks(double[] values) {
        setDepth_PercentileRanks(values, null);
    }

    public void setDepth_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setDepth_PercentileRanks("depth", values, opLambda);
    }

    public void setDepth_PercentileRanks(String name, double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "depth", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Histogram() {
        setDepth_Histogram(null);
    }

    public void setDepth_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setDepth_Histogram("depth", opLambda, null);
    }

    public void setDepth_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setDepth_Histogram("depth", opLambda, aggsLambda);
    }

    public void setDepth_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "depth");
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

    public void setDepth_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setDepth_Range("depth", opLambda, null);
    }

    public void setDepth_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setDepth_Range("depth", opLambda, aggsLambda);
    }

    public void setDepth_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "depth");
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

    public void setDepth_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setDepth_Count("depth", opLambda);
    }

    public void setDepth_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Cardinality() {
        setDepth_Cardinality(null);
    }

    public void setDepth_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setDepth_Cardinality("depth", opLambda);
    }

    public void setDepth_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepth_Missing() {
        setDepth_Missing(null);
    }

    public void setDepth_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setDepth_Missing("depth", opLambda, null);
    }

    public void setDepth_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setDepth_Missing("depth", opLambda, aggsLambda);
    }

    public void setDepth_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "depth");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDescription_Count() {
        setDescription_Count(null);
    }

    public void setDescription_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setDescription_Count("description", opLambda);
    }

    public void setDescription_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "description");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_Cardinality() {
        setDescription_Cardinality(null);
    }

    public void setDescription_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setDescription_Cardinality("description", opLambda);
    }

    public void setDescription_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "description");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_Missing() {
        setDescription_Missing(null);
    }

    public void setDescription_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setDescription_Missing("description", opLambda, null);
    }

    public void setDescription_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setDescription_Missing("description", opLambda, aggsLambda);
    }

    public void setDescription_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "description");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setExcludedDocPaths_Terms() {
        setExcludedDocPaths_Terms(null);
    }

    public void setExcludedDocPaths_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setExcludedDocPaths_Terms("excludedDocPaths", opLambda, null);
    }

    public void setExcludedDocPaths_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setExcludedDocPaths_Terms("excludedDocPaths", opLambda, aggsLambda);
    }

    public void setExcludedDocPaths_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "excludedDocPaths");
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

    public void setExcludedDocPaths_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setExcludedDocPaths_SignificantTerms("excludedDocPaths", opLambda, null);
    }

    public void setExcludedDocPaths_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setExcludedDocPaths_SignificantTerms("excludedDocPaths", opLambda, aggsLambda);
    }

    public void setExcludedDocPaths_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "excludedDocPaths");
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

    public void setExcludedDocPaths_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setExcludedDocPaths_IpRange("excludedDocPaths", opLambda, null);
    }

    public void setExcludedDocPaths_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setExcludedDocPaths_IpRange("excludedDocPaths", opLambda, aggsLambda);
    }

    public void setExcludedDocPaths_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "excludedDocPaths");
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

    public void setExcludedDocPaths_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setExcludedDocPaths_Count("excludedDocPaths", opLambda);
    }

    public void setExcludedDocPaths_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "excludedDocPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocPaths_Cardinality() {
        setExcludedDocPaths_Cardinality(null);
    }

    public void setExcludedDocPaths_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setExcludedDocPaths_Cardinality("excludedDocPaths", opLambda);
    }

    public void setExcludedDocPaths_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "excludedDocPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedDocPaths_Missing() {
        setExcludedDocPaths_Missing(null);
    }

    public void setExcludedDocPaths_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setExcludedDocPaths_Missing("excludedDocPaths", opLambda, null);
    }

    public void setExcludedDocPaths_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setExcludedDocPaths_Missing("excludedDocPaths", opLambda, aggsLambda);
    }

    public void setExcludedDocPaths_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "excludedDocPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setExcludedPaths_Terms() {
        setExcludedPaths_Terms(null);
    }

    public void setExcludedPaths_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setExcludedPaths_Terms("excludedPaths", opLambda, null);
    }

    public void setExcludedPaths_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setExcludedPaths_Terms("excludedPaths", opLambda, aggsLambda);
    }

    public void setExcludedPaths_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "excludedPaths");
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

    public void setExcludedPaths_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setExcludedPaths_SignificantTerms("excludedPaths", opLambda, null);
    }

    public void setExcludedPaths_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setExcludedPaths_SignificantTerms("excludedPaths", opLambda, aggsLambda);
    }

    public void setExcludedPaths_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "excludedPaths");
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

    public void setExcludedPaths_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setExcludedPaths_IpRange("excludedPaths", opLambda, null);
    }

    public void setExcludedPaths_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setExcludedPaths_IpRange("excludedPaths", opLambda, aggsLambda);
    }

    public void setExcludedPaths_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "excludedPaths");
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

    public void setExcludedPaths_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setExcludedPaths_Count("excludedPaths", opLambda);
    }

    public void setExcludedPaths_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "excludedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_Cardinality() {
        setExcludedPaths_Cardinality(null);
    }

    public void setExcludedPaths_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setExcludedPaths_Cardinality("excludedPaths", opLambda);
    }

    public void setExcludedPaths_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "excludedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExcludedPaths_Missing() {
        setExcludedPaths_Missing(null);
    }

    public void setExcludedPaths_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setExcludedPaths_Missing("excludedPaths", opLambda, null);
    }

    public void setExcludedPaths_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setExcludedPaths_Missing("excludedPaths", opLambda, aggsLambda);
    }

    public void setExcludedPaths_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "excludedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setIncludedDocPaths_Terms() {
        setIncludedDocPaths_Terms(null);
    }

    public void setIncludedDocPaths_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setIncludedDocPaths_Terms("includedDocPaths", opLambda, null);
    }

    public void setIncludedDocPaths_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setIncludedDocPaths_Terms("includedDocPaths", opLambda, aggsLambda);
    }

    public void setIncludedDocPaths_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "includedDocPaths");
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

    public void setIncludedDocPaths_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setIncludedDocPaths_SignificantTerms("includedDocPaths", opLambda, null);
    }

    public void setIncludedDocPaths_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setIncludedDocPaths_SignificantTerms("includedDocPaths", opLambda, aggsLambda);
    }

    public void setIncludedDocPaths_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "includedDocPaths");
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

    public void setIncludedDocPaths_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setIncludedDocPaths_IpRange("includedDocPaths", opLambda, null);
    }

    public void setIncludedDocPaths_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setIncludedDocPaths_IpRange("includedDocPaths", opLambda, aggsLambda);
    }

    public void setIncludedDocPaths_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "includedDocPaths");
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

    public void setIncludedDocPaths_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setIncludedDocPaths_Count("includedDocPaths", opLambda);
    }

    public void setIncludedDocPaths_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "includedDocPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocPaths_Cardinality() {
        setIncludedDocPaths_Cardinality(null);
    }

    public void setIncludedDocPaths_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setIncludedDocPaths_Cardinality("includedDocPaths", opLambda);
    }

    public void setIncludedDocPaths_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "includedDocPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedDocPaths_Missing() {
        setIncludedDocPaths_Missing(null);
    }

    public void setIncludedDocPaths_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setIncludedDocPaths_Missing("includedDocPaths", opLambda, null);
    }

    public void setIncludedDocPaths_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setIncludedDocPaths_Missing("includedDocPaths", opLambda, aggsLambda);
    }

    public void setIncludedDocPaths_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "includedDocPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setIncludedPaths_Terms() {
        setIncludedPaths_Terms(null);
    }

    public void setIncludedPaths_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setIncludedPaths_Terms("includedPaths", opLambda, null);
    }

    public void setIncludedPaths_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setIncludedPaths_Terms("includedPaths", opLambda, aggsLambda);
    }

    public void setIncludedPaths_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "includedPaths");
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

    public void setIncludedPaths_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setIncludedPaths_SignificantTerms("includedPaths", opLambda, null);
    }

    public void setIncludedPaths_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setIncludedPaths_SignificantTerms("includedPaths", opLambda, aggsLambda);
    }

    public void setIncludedPaths_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "includedPaths");
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

    public void setIncludedPaths_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setIncludedPaths_IpRange("includedPaths", opLambda, null);
    }

    public void setIncludedPaths_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setIncludedPaths_IpRange("includedPaths", opLambda, aggsLambda);
    }

    public void setIncludedPaths_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "includedPaths");
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

    public void setIncludedPaths_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setIncludedPaths_Count("includedPaths", opLambda);
    }

    public void setIncludedPaths_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "includedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_Cardinality() {
        setIncludedPaths_Cardinality(null);
    }

    public void setIncludedPaths_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setIncludedPaths_Cardinality("includedPaths", opLambda);
    }

    public void setIncludedPaths_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "includedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIncludedPaths_Missing() {
        setIncludedPaths_Missing(null);
    }

    public void setIncludedPaths_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setIncludedPaths_Missing("includedPaths", opLambda, null);
    }

    public void setIncludedPaths_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setIncludedPaths_Missing("includedPaths", opLambda, aggsLambda);
    }

    public void setIncludedPaths_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "includedPaths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setIntervalTime_Avg() {
        setIntervalTime_Avg(null);
    }

    public void setIntervalTime_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setIntervalTime_Avg("intervalTime", opLambda);
    }

    public void setIntervalTime_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Max() {
        setIntervalTime_Max(null);
    }

    public void setIntervalTime_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setIntervalTime_Max("intervalTime", opLambda);
    }

    public void setIntervalTime_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Min() {
        setIntervalTime_Min(null);
    }

    public void setIntervalTime_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setIntervalTime_Min("intervalTime", opLambda);
    }

    public void setIntervalTime_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Sum() {
        setIntervalTime_Sum(null);
    }

    public void setIntervalTime_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setIntervalTime_Sum("intervalTime", opLambda);
    }

    public void setIntervalTime_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_ExtendedStats() {
        setIntervalTime_ExtendedStats(null);
    }

    public void setIntervalTime_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setIntervalTime_ExtendedStats("intervalTime", opLambda);
    }

    public void setIntervalTime_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Stats() {
        setIntervalTime_Stats(null);
    }

    public void setIntervalTime_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setIntervalTime_Stats("intervalTime", opLambda);
    }

    public void setIntervalTime_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Percentiles() {
        setIntervalTime_Percentiles(null);
    }

    public void setIntervalTime_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setIntervalTime_Percentiles("intervalTime", opLambda);
    }

    public void setIntervalTime_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_PercentileRanks(double[] values) {
        setIntervalTime_PercentileRanks(values, null);
    }

    public void setIntervalTime_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setIntervalTime_PercentileRanks("intervalTime", values, opLambda);
    }

    public void setIntervalTime_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "intervalTime", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Histogram() {
        setIntervalTime_Histogram(null);
    }

    public void setIntervalTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setIntervalTime_Histogram("intervalTime", opLambda, null);
    }

    public void setIntervalTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setIntervalTime_Histogram("intervalTime", opLambda, aggsLambda);
    }

    public void setIntervalTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "intervalTime");
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

    public void setIntervalTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setIntervalTime_Range("intervalTime", opLambda, null);
    }

    public void setIntervalTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setIntervalTime_Range("intervalTime", opLambda, aggsLambda);
    }

    public void setIntervalTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "intervalTime");
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

    public void setIntervalTime_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setIntervalTime_Count("intervalTime", opLambda);
    }

    public void setIntervalTime_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Cardinality() {
        setIntervalTime_Cardinality(null);
    }

    public void setIntervalTime_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setIntervalTime_Cardinality("intervalTime", opLambda);
    }

    public void setIntervalTime_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setIntervalTime_Missing() {
        setIntervalTime_Missing(null);
    }

    public void setIntervalTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setIntervalTime_Missing("intervalTime", opLambda, null);
    }

    public void setIntervalTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setIntervalTime_Missing("intervalTime", opLambda, aggsLambda);
    }

    public void setIntervalTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "intervalTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setMaxAccessCount_Avg() {
        setMaxAccessCount_Avg(null);
    }

    public void setMaxAccessCount_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setMaxAccessCount_Avg("maxAccessCount", opLambda);
    }

    public void setMaxAccessCount_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Max() {
        setMaxAccessCount_Max(null);
    }

    public void setMaxAccessCount_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setMaxAccessCount_Max("maxAccessCount", opLambda);
    }

    public void setMaxAccessCount_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Min() {
        setMaxAccessCount_Min(null);
    }

    public void setMaxAccessCount_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setMaxAccessCount_Min("maxAccessCount", opLambda);
    }

    public void setMaxAccessCount_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Sum() {
        setMaxAccessCount_Sum(null);
    }

    public void setMaxAccessCount_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setMaxAccessCount_Sum("maxAccessCount", opLambda);
    }

    public void setMaxAccessCount_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_ExtendedStats() {
        setMaxAccessCount_ExtendedStats(null);
    }

    public void setMaxAccessCount_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setMaxAccessCount_ExtendedStats("maxAccessCount", opLambda);
    }

    public void setMaxAccessCount_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Stats() {
        setMaxAccessCount_Stats(null);
    }

    public void setMaxAccessCount_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setMaxAccessCount_Stats("maxAccessCount", opLambda);
    }

    public void setMaxAccessCount_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Percentiles() {
        setMaxAccessCount_Percentiles(null);
    }

    public void setMaxAccessCount_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setMaxAccessCount_Percentiles("maxAccessCount", opLambda);
    }

    public void setMaxAccessCount_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_PercentileRanks(double[] values) {
        setMaxAccessCount_PercentileRanks(values, null);
    }

    public void setMaxAccessCount_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setMaxAccessCount_PercentileRanks("maxAccessCount", values, opLambda);
    }

    public void setMaxAccessCount_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "maxAccessCount", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Histogram() {
        setMaxAccessCount_Histogram(null);
    }

    public void setMaxAccessCount_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setMaxAccessCount_Histogram("maxAccessCount", opLambda, null);
    }

    public void setMaxAccessCount_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setMaxAccessCount_Histogram("maxAccessCount", opLambda, aggsLambda);
    }

    public void setMaxAccessCount_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "maxAccessCount");
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

    public void setMaxAccessCount_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setMaxAccessCount_Range("maxAccessCount", opLambda, null);
    }

    public void setMaxAccessCount_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setMaxAccessCount_Range("maxAccessCount", opLambda, aggsLambda);
    }

    public void setMaxAccessCount_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "maxAccessCount");
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

    public void setMaxAccessCount_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setMaxAccessCount_Count("maxAccessCount", opLambda);
    }

    public void setMaxAccessCount_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Cardinality() {
        setMaxAccessCount_Cardinality(null);
    }

    public void setMaxAccessCount_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setMaxAccessCount_Cardinality("maxAccessCount", opLambda);
    }

    public void setMaxAccessCount_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxAccessCount_Missing() {
        setMaxAccessCount_Missing(null);
    }

    public void setMaxAccessCount_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setMaxAccessCount_Missing("maxAccessCount", opLambda, null);
    }

    public void setMaxAccessCount_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setMaxAccessCount_Missing("maxAccessCount", opLambda, aggsLambda);
    }

    public void setMaxAccessCount_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "maxAccessCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setName_Terms() {
        setName_Terms(null);
    }

    public void setName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setName_Terms("name", opLambda, null);
    }

    public void setName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setName_Terms("name", opLambda, aggsLambda);
    }

    public void setName_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "name");
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

    public void setName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setName_SignificantTerms("name", opLambda, null);
    }

    public void setName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setName_SignificantTerms("name", opLambda, aggsLambda);
    }

    public void setName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "name");
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

    public void setName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setName_IpRange("name", opLambda, null);
    }

    public void setName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setName_IpRange("name", opLambda, aggsLambda);
    }

    public void setName_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "name");
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

    public void setName_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setName_Count("name", opLambda);
    }

    public void setName_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Cardinality() {
        setName_Cardinality(null);
    }

    public void setName_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setName_Cardinality("name", opLambda);
    }

    public void setName_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Missing() {
        setName_Missing(null);
    }

    public void setName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setName_Missing("name", opLambda, null);
    }

    public void setName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setName_Missing("name", opLambda, aggsLambda);
    }

    public void setName_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setNumOfThread_Avg() {
        setNumOfThread_Avg(null);
    }

    public void setNumOfThread_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setNumOfThread_Avg("numOfThread", opLambda);
    }

    public void setNumOfThread_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Max() {
        setNumOfThread_Max(null);
    }

    public void setNumOfThread_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setNumOfThread_Max("numOfThread", opLambda);
    }

    public void setNumOfThread_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Min() {
        setNumOfThread_Min(null);
    }

    public void setNumOfThread_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setNumOfThread_Min("numOfThread", opLambda);
    }

    public void setNumOfThread_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Sum() {
        setNumOfThread_Sum(null);
    }

    public void setNumOfThread_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setNumOfThread_Sum("numOfThread", opLambda);
    }

    public void setNumOfThread_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_ExtendedStats() {
        setNumOfThread_ExtendedStats(null);
    }

    public void setNumOfThread_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setNumOfThread_ExtendedStats("numOfThread", opLambda);
    }

    public void setNumOfThread_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Stats() {
        setNumOfThread_Stats(null);
    }

    public void setNumOfThread_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setNumOfThread_Stats("numOfThread", opLambda);
    }

    public void setNumOfThread_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Percentiles() {
        setNumOfThread_Percentiles(null);
    }

    public void setNumOfThread_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setNumOfThread_Percentiles("numOfThread", opLambda);
    }

    public void setNumOfThread_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_PercentileRanks(double[] values) {
        setNumOfThread_PercentileRanks(values, null);
    }

    public void setNumOfThread_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setNumOfThread_PercentileRanks("numOfThread", values, opLambda);
    }

    public void setNumOfThread_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "numOfThread", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Histogram() {
        setNumOfThread_Histogram(null);
    }

    public void setNumOfThread_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setNumOfThread_Histogram("numOfThread", opLambda, null);
    }

    public void setNumOfThread_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setNumOfThread_Histogram("numOfThread", opLambda, aggsLambda);
    }

    public void setNumOfThread_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "numOfThread");
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

    public void setNumOfThread_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setNumOfThread_Range("numOfThread", opLambda, null);
    }

    public void setNumOfThread_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setNumOfThread_Range("numOfThread", opLambda, aggsLambda);
    }

    public void setNumOfThread_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "numOfThread");
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

    public void setNumOfThread_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setNumOfThread_Count("numOfThread", opLambda);
    }

    public void setNumOfThread_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Cardinality() {
        setNumOfThread_Cardinality(null);
    }

    public void setNumOfThread_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setNumOfThread_Cardinality("numOfThread", opLambda);
    }

    public void setNumOfThread_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setNumOfThread_Missing() {
        setNumOfThread_Missing(null);
    }

    public void setNumOfThread_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setNumOfThread_Missing("numOfThread", opLambda, null);
    }

    public void setNumOfThread_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setNumOfThread_Missing("numOfThread", opLambda, aggsLambda);
    }

    public void setNumOfThread_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "numOfThread");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPaths_Terms() {
        setPaths_Terms(null);
    }

    public void setPaths_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setPaths_Terms("paths", opLambda, null);
    }

    public void setPaths_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setPaths_Terms("paths", opLambda, aggsLambda);
    }

    public void setPaths_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "paths");
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

    public void setPaths_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setPaths_SignificantTerms("paths", opLambda, null);
    }

    public void setPaths_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setPaths_SignificantTerms("paths", opLambda, aggsLambda);
    }

    public void setPaths_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "paths");
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

    public void setPaths_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setPaths_IpRange("paths", opLambda, null);
    }

    public void setPaths_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setPaths_IpRange("paths", opLambda, aggsLambda);
    }

    public void setPaths_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "paths");
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

    public void setPaths_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setPaths_Count("paths", opLambda);
    }

    public void setPaths_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "paths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPaths_Cardinality() {
        setPaths_Cardinality(null);
    }

    public void setPaths_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setPaths_Cardinality("paths", opLambda);
    }

    public void setPaths_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "paths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPaths_Missing() {
        setPaths_Missing(null);
    }

    public void setPaths_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setPaths_Missing("paths", opLambda, null);
    }

    public void setPaths_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setPaths_Missing("paths", opLambda, aggsLambda);
    }

    public void setPaths_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "paths");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPermissions_Terms() {
        setPermissions_Terms(null);
    }

    public void setPermissions_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setPermissions_Terms("permissions", opLambda, null);
    }

    public void setPermissions_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setPermissions_Terms("permissions", opLambda, aggsLambda);
    }

    public void setPermissions_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "permissions");
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

    public void setPermissions_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setPermissions_SignificantTerms("permissions", opLambda, null);
    }

    public void setPermissions_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setPermissions_SignificantTerms("permissions", opLambda, aggsLambda);
    }

    public void setPermissions_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "permissions");
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

    public void setPermissions_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setPermissions_IpRange("permissions", opLambda, null);
    }

    public void setPermissions_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setPermissions_IpRange("permissions", opLambda, aggsLambda);
    }

    public void setPermissions_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "permissions");
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

    public void setPermissions_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setPermissions_Count("permissions", opLambda);
    }

    public void setPermissions_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "permissions");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPermissions_Cardinality() {
        setPermissions_Cardinality(null);
    }

    public void setPermissions_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setPermissions_Cardinality("permissions", opLambda);
    }

    public void setPermissions_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "permissions");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPermissions_Missing() {
        setPermissions_Missing(null);
    }

    public void setPermissions_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setPermissions_Missing("permissions", opLambda, null);
    }

    public void setPermissions_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setPermissions_Missing("permissions", opLambda, aggsLambda);
    }

    public void setPermissions_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "permissions");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSortOrder_Avg() {
        setSortOrder_Avg(null);
    }

    public void setSortOrder_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setSortOrder_Avg("sortOrder", opLambda);
    }

    public void setSortOrder_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Max() {
        setSortOrder_Max(null);
    }

    public void setSortOrder_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setSortOrder_Max("sortOrder", opLambda);
    }

    public void setSortOrder_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Min() {
        setSortOrder_Min(null);
    }

    public void setSortOrder_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setSortOrder_Min("sortOrder", opLambda);
    }

    public void setSortOrder_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Sum() {
        setSortOrder_Sum(null);
    }

    public void setSortOrder_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setSortOrder_Sum("sortOrder", opLambda);
    }

    public void setSortOrder_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_ExtendedStats() {
        setSortOrder_ExtendedStats(null);
    }

    public void setSortOrder_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setSortOrder_ExtendedStats("sortOrder", opLambda);
    }

    public void setSortOrder_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Stats() {
        setSortOrder_Stats(null);
    }

    public void setSortOrder_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setSortOrder_Stats("sortOrder", opLambda);
    }

    public void setSortOrder_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Percentiles() {
        setSortOrder_Percentiles(null);
    }

    public void setSortOrder_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setSortOrder_Percentiles("sortOrder", opLambda);
    }

    public void setSortOrder_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_PercentileRanks(double[] values) {
        setSortOrder_PercentileRanks(values, null);
    }

    public void setSortOrder_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setSortOrder_PercentileRanks("sortOrder", values, opLambda);
    }

    public void setSortOrder_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "sortOrder", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Histogram() {
        setSortOrder_Histogram(null);
    }

    public void setSortOrder_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setSortOrder_Histogram("sortOrder", opLambda, null);
    }

    public void setSortOrder_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setSortOrder_Histogram("sortOrder", opLambda, aggsLambda);
    }

    public void setSortOrder_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "sortOrder");
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

    public void setSortOrder_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setSortOrder_Range("sortOrder", opLambda, null);
    }

    public void setSortOrder_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setSortOrder_Range("sortOrder", opLambda, aggsLambda);
    }

    public void setSortOrder_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "sortOrder");
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

    public void setSortOrder_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setSortOrder_Count("sortOrder", opLambda);
    }

    public void setSortOrder_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Cardinality() {
        setSortOrder_Cardinality(null);
    }

    public void setSortOrder_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setSortOrder_Cardinality("sortOrder", opLambda);
    }

    public void setSortOrder_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSortOrder_Missing() {
        setSortOrder_Missing(null);
    }

    public void setSortOrder_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setSortOrder_Missing("sortOrder", opLambda, null);
    }

    public void setSortOrder_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setSortOrder_Missing("sortOrder", opLambda, aggsLambda);
    }

    public void setSortOrder_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTimeToLive_Avg() {
        setTimeToLive_Avg(null);
    }

    public void setTimeToLive_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setTimeToLive_Avg("timeToLive", opLambda);
    }

    public void setTimeToLive_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_Max() {
        setTimeToLive_Max(null);
    }

    public void setTimeToLive_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setTimeToLive_Max("timeToLive", opLambda);
    }

    public void setTimeToLive_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_Min() {
        setTimeToLive_Min(null);
    }

    public void setTimeToLive_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setTimeToLive_Min("timeToLive", opLambda);
    }

    public void setTimeToLive_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_Sum() {
        setTimeToLive_Sum(null);
    }

    public void setTimeToLive_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setTimeToLive_Sum("timeToLive", opLambda);
    }

    public void setTimeToLive_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_ExtendedStats() {
        setTimeToLive_ExtendedStats(null);
    }

    public void setTimeToLive_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setTimeToLive_ExtendedStats("timeToLive", opLambda);
    }

    public void setTimeToLive_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_Stats() {
        setTimeToLive_Stats(null);
    }

    public void setTimeToLive_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setTimeToLive_Stats("timeToLive", opLambda);
    }

    public void setTimeToLive_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_Percentiles() {
        setTimeToLive_Percentiles(null);
    }

    public void setTimeToLive_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setTimeToLive_Percentiles("timeToLive", opLambda);
    }

    public void setTimeToLive_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_PercentileRanks(double[] values) {
        setTimeToLive_PercentileRanks(values, null);
    }

    public void setTimeToLive_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setTimeToLive_PercentileRanks("timeToLive", values, opLambda);
    }

    public void setTimeToLive_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "timeToLive", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_Histogram() {
        setTimeToLive_Histogram(null);
    }

    public void setTimeToLive_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setTimeToLive_Histogram("timeToLive", opLambda, null);
    }

    public void setTimeToLive_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setTimeToLive_Histogram("timeToLive", opLambda, aggsLambda);
    }

    public void setTimeToLive_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "timeToLive");
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

    public void setTimeToLive_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setTimeToLive_Range("timeToLive", opLambda, null);
    }

    public void setTimeToLive_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setTimeToLive_Range("timeToLive", opLambda, aggsLambda);
    }

    public void setTimeToLive_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "timeToLive");
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

    public void setTimeToLive_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setTimeToLive_Count("timeToLive", opLambda);
    }

    public void setTimeToLive_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_Cardinality() {
        setTimeToLive_Cardinality(null);
    }

    public void setTimeToLive_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setTimeToLive_Cardinality("timeToLive", opLambda);
    }

    public void setTimeToLive_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTimeToLive_Missing() {
        setTimeToLive_Missing(null);
    }

    public void setTimeToLive_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setTimeToLive_Missing("timeToLive", opLambda, null);
    }

    public void setTimeToLive_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setTimeToLive_Missing("timeToLive", opLambda, aggsLambda);
    }

    public void setTimeToLive_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "timeToLive");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUpdatedBy_Terms() {
        setUpdatedBy_Terms(null);
    }

    public void setUpdatedBy_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setUpdatedBy_Terms("updatedBy", opLambda, null);
    }

    public void setUpdatedBy_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setUpdatedBy_Terms("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "updatedBy");
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

    public void setUpdatedBy_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setUpdatedBy_SignificantTerms("updatedBy", opLambda, null);
    }

    public void setUpdatedBy_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setUpdatedBy_SignificantTerms("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "updatedBy");
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

    public void setUpdatedBy_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setUpdatedBy_IpRange("updatedBy", opLambda, null);
    }

    public void setUpdatedBy_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setUpdatedBy_IpRange("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "updatedBy");
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

    public void setUpdatedBy_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setUpdatedBy_Count("updatedBy", opLambda);
    }

    public void setUpdatedBy_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Cardinality() {
        setUpdatedBy_Cardinality(null);
    }

    public void setUpdatedBy_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setUpdatedBy_Cardinality("updatedBy", opLambda);
    }

    public void setUpdatedBy_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Missing() {
        setUpdatedBy_Missing(null);
    }

    public void setUpdatedBy_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setUpdatedBy_Missing("updatedBy", opLambda, null);
    }

    public void setUpdatedBy_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setUpdatedBy_Missing("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUpdatedTime_Avg() {
        setUpdatedTime_Avg(null);
    }

    public void setUpdatedTime_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setUpdatedTime_Avg("updatedTime", opLambda);
    }

    public void setUpdatedTime_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Max() {
        setUpdatedTime_Max(null);
    }

    public void setUpdatedTime_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setUpdatedTime_Max("updatedTime", opLambda);
    }

    public void setUpdatedTime_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Min() {
        setUpdatedTime_Min(null);
    }

    public void setUpdatedTime_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setUpdatedTime_Min("updatedTime", opLambda);
    }

    public void setUpdatedTime_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Sum() {
        setUpdatedTime_Sum(null);
    }

    public void setUpdatedTime_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setUpdatedTime_Sum("updatedTime", opLambda);
    }

    public void setUpdatedTime_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_ExtendedStats() {
        setUpdatedTime_ExtendedStats(null);
    }

    public void setUpdatedTime_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setUpdatedTime_ExtendedStats("updatedTime", opLambda);
    }

    public void setUpdatedTime_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Stats() {
        setUpdatedTime_Stats(null);
    }

    public void setUpdatedTime_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setUpdatedTime_Stats("updatedTime", opLambda);
    }

    public void setUpdatedTime_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Percentiles() {
        setUpdatedTime_Percentiles(null);
    }

    public void setUpdatedTime_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setUpdatedTime_Percentiles("updatedTime", opLambda);
    }

    public void setUpdatedTime_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_PercentileRanks(double[] values) {
        setUpdatedTime_PercentileRanks(values, null);
    }

    public void setUpdatedTime_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setUpdatedTime_PercentileRanks("updatedTime", values, opLambda);
    }

    public void setUpdatedTime_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "updatedTime", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Histogram() {
        setUpdatedTime_Histogram(null);
    }

    public void setUpdatedTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setUpdatedTime_Histogram("updatedTime", opLambda, null);
    }

    public void setUpdatedTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setUpdatedTime_Histogram("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "updatedTime");
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

    public void setUpdatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setUpdatedTime_Range("updatedTime", opLambda, null);
    }

    public void setUpdatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setUpdatedTime_Range("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "updatedTime");
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

    public void setUpdatedTime_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setUpdatedTime_Count("updatedTime", opLambda);
    }

    public void setUpdatedTime_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Cardinality() {
        setUpdatedTime_Cardinality(null);
    }

    public void setUpdatedTime_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setUpdatedTime_Cardinality("updatedTime", opLambda);
    }

    public void setUpdatedTime_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Missing() {
        setUpdatedTime_Missing(null);
    }

    public void setUpdatedTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setUpdatedTime_Missing("updatedTime", opLambda, null);
    }

    public void setUpdatedTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setUpdatedTime_Missing("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setVirtualHosts_Terms() {
        setVirtualHosts_Terms(null);
    }

    public void setVirtualHosts_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setVirtualHosts_Terms("virtualHosts", opLambda, null);
    }

    public void setVirtualHosts_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setVirtualHosts_Terms("virtualHosts", opLambda, aggsLambda);
    }

    public void setVirtualHosts_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "virtualHosts");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setVirtualHosts_SignificantTerms() {
        setVirtualHosts_SignificantTerms(null);
    }

    public void setVirtualHosts_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setVirtualHosts_SignificantTerms("virtualHosts", opLambda, null);
    }

    public void setVirtualHosts_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        setVirtualHosts_SignificantTerms("virtualHosts", opLambda, aggsLambda);
    }

    public void setVirtualHosts_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "virtualHosts");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setVirtualHosts_IpRange() {
        setVirtualHosts_IpRange(null);
    }

    public void setVirtualHosts_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setVirtualHosts_IpRange("virtualHosts", opLambda, null);
    }

    public void setVirtualHosts_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setVirtualHosts_IpRange("virtualHosts", opLambda, aggsLambda);
    }

    public void setVirtualHosts_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "virtualHosts");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileConfigCA ca = new FileConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setVirtualHosts_Count() {
        setVirtualHosts_Count(null);
    }

    public void setVirtualHosts_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setVirtualHosts_Count("virtualHosts", opLambda);
    }

    public void setVirtualHosts_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "virtualHosts");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHosts_Cardinality() {
        setVirtualHosts_Cardinality(null);
    }

    public void setVirtualHosts_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setVirtualHosts_Cardinality("virtualHosts", opLambda);
    }

    public void setVirtualHosts_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "virtualHosts");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHosts_Missing() {
        setVirtualHosts_Missing(null);
    }

    public void setVirtualHosts_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setVirtualHosts_Missing("virtualHosts", opLambda, null);
    }

    public void setVirtualHosts_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFileConfigCA> aggsLambda) {
        setVirtualHosts_Missing("virtualHosts", opLambda, aggsLambda);
    }

    public void setVirtualHosts_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "virtualHosts");
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
