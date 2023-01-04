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
import org.codelibs.fess.es.config.cbean.ca.ElevateWordCA;
import org.codelibs.fess.es.config.cbean.cq.ElevateWordCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsElevateWordCQ;
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
public abstract class BsElevateWordCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsElevateWordCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        ElevateWordCQ cq = new ElevateWordCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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

    public void setBoost_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setBoost_Histogram("boost", opLambda, aggsLambda);
    }

    public void setBoost_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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

    public void setBoost_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setBoost_Range("boost", opLambda, aggsLambda);
    }

    public void setBoost_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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

    public void setBoost_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setBoost_Missing("boost", opLambda, aggsLambda);
    }

    public void setBoost_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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

    public void setCreatedBy_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setCreatedBy_Terms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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
            OperatorCall<BsElevateWordCA> aggsLambda) {
        setCreatedBy_SignificantTerms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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

    public void setCreatedBy_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setCreatedBy_IpRange("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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

    public void setCreatedBy_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setCreatedBy_Missing("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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
            OperatorCall<BsElevateWordCA> aggsLambda) {
        setCreatedTime_Histogram("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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

    public void setCreatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setCreatedTime_Range("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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

    public void setCreatedTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setCreatedTime_Missing("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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

    public void setPermissions_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setPermissions_Terms("permissions", opLambda, aggsLambda);
    }

    public void setPermissions_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "permissions");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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
            OperatorCall<BsElevateWordCA> aggsLambda) {
        setPermissions_SignificantTerms("permissions", opLambda, aggsLambda);
    }

    public void setPermissions_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "permissions");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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

    public void setPermissions_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setPermissions_IpRange("permissions", opLambda, aggsLambda);
    }

    public void setPermissions_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "permissions");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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

    public void setPermissions_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setPermissions_Missing("permissions", opLambda, aggsLambda);
    }

    public void setPermissions_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "permissions");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setReading_Terms() {
        setReading_Terms(null);
    }

    public void setReading_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setReading_Terms("reading", opLambda, null);
    }

    public void setReading_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setReading_Terms("reading", opLambda, aggsLambda);
    }

    public void setReading_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "reading");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setReading_SignificantTerms() {
        setReading_SignificantTerms(null);
    }

    public void setReading_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setReading_SignificantTerms("reading", opLambda, null);
    }

    public void setReading_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        setReading_SignificantTerms("reading", opLambda, aggsLambda);
    }

    public void setReading_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "reading");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setReading_IpRange() {
        setReading_IpRange(null);
    }

    public void setReading_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setReading_IpRange("reading", opLambda, null);
    }

    public void setReading_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setReading_IpRange("reading", opLambda, aggsLambda);
    }

    public void setReading_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "reading");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setReading_Count() {
        setReading_Count(null);
    }

    public void setReading_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setReading_Count("reading", opLambda);
    }

    public void setReading_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "reading");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_Cardinality() {
        setReading_Cardinality(null);
    }

    public void setReading_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setReading_Cardinality("reading", opLambda);
    }

    public void setReading_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "reading");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReading_Missing() {
        setReading_Missing(null);
    }

    public void setReading_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setReading_Missing("reading", opLambda, null);
    }

    public void setReading_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setReading_Missing("reading", opLambda, aggsLambda);
    }

    public void setReading_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "reading");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSuggestWord_Terms() {
        setSuggestWord_Terms(null);
    }

    public void setSuggestWord_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setSuggestWord_Terms("suggestWord", opLambda, null);
    }

    public void setSuggestWord_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setSuggestWord_Terms("suggestWord", opLambda, aggsLambda);
    }

    public void setSuggestWord_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "suggestWord");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSuggestWord_SignificantTerms() {
        setSuggestWord_SignificantTerms(null);
    }

    public void setSuggestWord_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setSuggestWord_SignificantTerms("suggestWord", opLambda, null);
    }

    public void setSuggestWord_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        setSuggestWord_SignificantTerms("suggestWord", opLambda, aggsLambda);
    }

    public void setSuggestWord_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "suggestWord");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSuggestWord_IpRange() {
        setSuggestWord_IpRange(null);
    }

    public void setSuggestWord_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setSuggestWord_IpRange("suggestWord", opLambda, null);
    }

    public void setSuggestWord_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setSuggestWord_IpRange("suggestWord", opLambda, aggsLambda);
    }

    public void setSuggestWord_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "suggestWord");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSuggestWord_Count() {
        setSuggestWord_Count(null);
    }

    public void setSuggestWord_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setSuggestWord_Count("suggestWord", opLambda);
    }

    public void setSuggestWord_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "suggestWord");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_Cardinality() {
        setSuggestWord_Cardinality(null);
    }

    public void setSuggestWord_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setSuggestWord_Cardinality("suggestWord", opLambda);
    }

    public void setSuggestWord_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "suggestWord");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSuggestWord_Missing() {
        setSuggestWord_Missing(null);
    }

    public void setSuggestWord_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setSuggestWord_Missing("suggestWord", opLambda, null);
    }

    public void setSuggestWord_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setSuggestWord_Missing("suggestWord", opLambda, aggsLambda);
    }

    public void setSuggestWord_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "suggestWord");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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

    public void setUpdatedBy_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setUpdatedBy_Terms("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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
            OperatorCall<BsElevateWordCA> aggsLambda) {
        setUpdatedBy_SignificantTerms("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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

    public void setUpdatedBy_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setUpdatedBy_IpRange("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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

    public void setUpdatedBy_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setUpdatedBy_Missing("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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
            OperatorCall<BsElevateWordCA> aggsLambda) {
        setUpdatedTime_Histogram("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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

    public void setUpdatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setUpdatedTime_Range("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
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

    public void setUpdatedTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsElevateWordCA> aggsLambda) {
        setUpdatedTime_Missing("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordCA ca = new ElevateWordCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
