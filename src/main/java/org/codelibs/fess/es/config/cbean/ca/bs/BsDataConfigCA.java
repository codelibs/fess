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
import org.codelibs.fess.es.config.cbean.ca.DataConfigCA;
import org.codelibs.fess.es.config.cbean.cq.DataConfigCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsDataConfigCQ;
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
public abstract class BsDataConfigCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsDataConfigCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        DataConfigCQ cq = new DataConfigCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setAvailable_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setAvailable_Histogram("available", opLambda, aggsLambda);
    }

    public void setAvailable_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setAvailable_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setAvailable_Range("available", opLambda, aggsLambda);
    }

    public void setAvailable_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setAvailable_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setAvailable_Missing("available", opLambda, aggsLambda);
    }

    public void setAvailable_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setBoost_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setBoost_Histogram("boost", opLambda, aggsLambda);
    }

    public void setBoost_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setBoost_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setBoost_Range("boost", opLambda, aggsLambda);
    }

    public void setBoost_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setBoost_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setBoost_Missing("boost", opLambda, aggsLambda);
    }

    public void setBoost_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setCreatedBy_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setCreatedBy_Terms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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
            OperatorCall<BsDataConfigCA> aggsLambda) {
        setCreatedBy_SignificantTerms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setCreatedBy_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setCreatedBy_IpRange("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setCreatedBy_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setCreatedBy_Missing("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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
            OperatorCall<BsDataConfigCA> aggsLambda) {
        setCreatedTime_Histogram("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setCreatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setCreatedTime_Range("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setCreatedTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setCreatedTime_Missing("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setDescription_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setDescription_Missing("description", opLambda, aggsLambda);
    }

    public void setDescription_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "description");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHandlerName_Terms() {
        setHandlerName_Terms(null);
    }

    public void setHandlerName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setHandlerName_Terms("handlerName", opLambda, null);
    }

    public void setHandlerName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setHandlerName_Terms("handlerName", opLambda, aggsLambda);
    }

    public void setHandlerName_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "handlerName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHandlerName_SignificantTerms() {
        setHandlerName_SignificantTerms(null);
    }

    public void setHandlerName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setHandlerName_SignificantTerms("handlerName", opLambda, null);
    }

    public void setHandlerName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        setHandlerName_SignificantTerms("handlerName", opLambda, aggsLambda);
    }

    public void setHandlerName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "handlerName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHandlerName_IpRange() {
        setHandlerName_IpRange(null);
    }

    public void setHandlerName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setHandlerName_IpRange("handlerName", opLambda, null);
    }

    public void setHandlerName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setHandlerName_IpRange("handlerName", opLambda, aggsLambda);
    }

    public void setHandlerName_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "handlerName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHandlerName_Count() {
        setHandlerName_Count(null);
    }

    public void setHandlerName_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setHandlerName_Count("handlerName", opLambda);
    }

    public void setHandlerName_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "handlerName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerName_Cardinality() {
        setHandlerName_Cardinality(null);
    }

    public void setHandlerName_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setHandlerName_Cardinality("handlerName", opLambda);
    }

    public void setHandlerName_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "handlerName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerName_Missing() {
        setHandlerName_Missing(null);
    }

    public void setHandlerName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setHandlerName_Missing("handlerName", opLambda, null);
    }

    public void setHandlerName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setHandlerName_Missing("handlerName", opLambda, aggsLambda);
    }

    public void setHandlerName_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "handlerName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHandlerParameter_Terms() {
        setHandlerParameter_Terms(null);
    }

    public void setHandlerParameter_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setHandlerParameter_Terms("handlerParameter", opLambda, null);
    }

    public void setHandlerParameter_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setHandlerParameter_Terms("handlerParameter", opLambda, aggsLambda);
    }

    public void setHandlerParameter_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "handlerParameter");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHandlerParameter_SignificantTerms() {
        setHandlerParameter_SignificantTerms(null);
    }

    public void setHandlerParameter_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setHandlerParameter_SignificantTerms("handlerParameter", opLambda, null);
    }

    public void setHandlerParameter_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        setHandlerParameter_SignificantTerms("handlerParameter", opLambda, aggsLambda);
    }

    public void setHandlerParameter_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "handlerParameter");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHandlerParameter_IpRange() {
        setHandlerParameter_IpRange(null);
    }

    public void setHandlerParameter_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setHandlerParameter_IpRange("handlerParameter", opLambda, null);
    }

    public void setHandlerParameter_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        setHandlerParameter_IpRange("handlerParameter", opLambda, aggsLambda);
    }

    public void setHandlerParameter_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "handlerParameter");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHandlerParameter_Count() {
        setHandlerParameter_Count(null);
    }

    public void setHandlerParameter_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setHandlerParameter_Count("handlerParameter", opLambda);
    }

    public void setHandlerParameter_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "handlerParameter");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerParameter_Cardinality() {
        setHandlerParameter_Cardinality(null);
    }

    public void setHandlerParameter_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setHandlerParameter_Cardinality("handlerParameter", opLambda);
    }

    public void setHandlerParameter_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "handlerParameter");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerParameter_Missing() {
        setHandlerParameter_Missing(null);
    }

    public void setHandlerParameter_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setHandlerParameter_Missing("handlerParameter", opLambda, null);
    }

    public void setHandlerParameter_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        setHandlerParameter_Missing("handlerParameter", opLambda, aggsLambda);
    }

    public void setHandlerParameter_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "handlerParameter");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHandlerScript_Terms() {
        setHandlerScript_Terms(null);
    }

    public void setHandlerScript_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setHandlerScript_Terms("handlerScript", opLambda, null);
    }

    public void setHandlerScript_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setHandlerScript_Terms("handlerScript", opLambda, aggsLambda);
    }

    public void setHandlerScript_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "handlerScript");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHandlerScript_SignificantTerms() {
        setHandlerScript_SignificantTerms(null);
    }

    public void setHandlerScript_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setHandlerScript_SignificantTerms("handlerScript", opLambda, null);
    }

    public void setHandlerScript_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        setHandlerScript_SignificantTerms("handlerScript", opLambda, aggsLambda);
    }

    public void setHandlerScript_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "handlerScript");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHandlerScript_IpRange() {
        setHandlerScript_IpRange(null);
    }

    public void setHandlerScript_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setHandlerScript_IpRange("handlerScript", opLambda, null);
    }

    public void setHandlerScript_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setHandlerScript_IpRange("handlerScript", opLambda, aggsLambda);
    }

    public void setHandlerScript_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "handlerScript");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHandlerScript_Count() {
        setHandlerScript_Count(null);
    }

    public void setHandlerScript_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setHandlerScript_Count("handlerScript", opLambda);
    }

    public void setHandlerScript_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "handlerScript");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerScript_Cardinality() {
        setHandlerScript_Cardinality(null);
    }

    public void setHandlerScript_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setHandlerScript_Cardinality("handlerScript", opLambda);
    }

    public void setHandlerScript_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "handlerScript");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHandlerScript_Missing() {
        setHandlerScript_Missing(null);
    }

    public void setHandlerScript_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setHandlerScript_Missing("handlerScript", opLambda, null);
    }

    public void setHandlerScript_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setHandlerScript_Missing("handlerScript", opLambda, aggsLambda);
    }

    public void setHandlerScript_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "handlerScript");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setName_Terms("name", opLambda, aggsLambda);
    }

    public void setName_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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
            OperatorCall<BsDataConfigCA> aggsLambda) {
        setName_SignificantTerms("name", opLambda, aggsLambda);
    }

    public void setName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setName_IpRange("name", opLambda, aggsLambda);
    }

    public void setName_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setName_Missing("name", opLambda, aggsLambda);
    }

    public void setName_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setPermissions_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setPermissions_Terms("permissions", opLambda, aggsLambda);
    }

    public void setPermissions_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "permissions");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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
            OperatorCall<BsDataConfigCA> aggsLambda) {
        setPermissions_SignificantTerms("permissions", opLambda, aggsLambda);
    }

    public void setPermissions_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "permissions");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setPermissions_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setPermissions_IpRange("permissions", opLambda, aggsLambda);
    }

    public void setPermissions_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "permissions");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setPermissions_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setPermissions_Missing("permissions", opLambda, aggsLambda);
    }

    public void setPermissions_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "permissions");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setSortOrder_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setSortOrder_Histogram("sortOrder", opLambda, aggsLambda);
    }

    public void setSortOrder_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setSortOrder_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setSortOrder_Range("sortOrder", opLambda, aggsLambda);
    }

    public void setSortOrder_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setSortOrder_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setSortOrder_Missing("sortOrder", opLambda, aggsLambda);
    }

    public void setSortOrder_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setUpdatedBy_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setUpdatedBy_Terms("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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
            OperatorCall<BsDataConfigCA> aggsLambda) {
        setUpdatedBy_SignificantTerms("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setUpdatedBy_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setUpdatedBy_IpRange("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setUpdatedBy_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setUpdatedBy_Missing("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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
            OperatorCall<BsDataConfigCA> aggsLambda) {
        setUpdatedTime_Histogram("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setUpdatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setUpdatedTime_Range("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setUpdatedTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setUpdatedTime_Missing("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setVirtualHosts_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setVirtualHosts_Terms("virtualHosts", opLambda, aggsLambda);
    }

    public void setVirtualHosts_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "virtualHosts");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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
            OperatorCall<BsDataConfigCA> aggsLambda) {
        setVirtualHosts_SignificantTerms("virtualHosts", opLambda, aggsLambda);
    }

    public void setVirtualHosts_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "virtualHosts");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setVirtualHosts_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setVirtualHosts_IpRange("virtualHosts", opLambda, aggsLambda);
    }

    public void setVirtualHosts_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "virtualHosts");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
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

    public void setVirtualHosts_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsDataConfigCA> aggsLambda) {
        setVirtualHosts_Missing("virtualHosts", opLambda, aggsLambda);
    }

    public void setVirtualHosts_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "virtualHosts");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigCA ca = new DataConfigCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
