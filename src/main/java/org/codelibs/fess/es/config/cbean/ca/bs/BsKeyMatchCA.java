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
import org.codelibs.fess.es.config.cbean.ca.KeyMatchCA;
import org.codelibs.fess.es.config.cbean.cq.KeyMatchCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsKeyMatchCQ;
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
public abstract class BsKeyMatchCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsKeyMatchCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        KeyMatchCQ cq = new KeyMatchCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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

    public void setBoost_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setBoost_Histogram("boost", opLambda, aggsLambda);
    }

    public void setBoost_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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

    public void setBoost_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setBoost_Range("boost", opLambda, aggsLambda);
    }

    public void setBoost_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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

    public void setBoost_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setBoost_Missing("boost", opLambda, aggsLambda);
    }

    public void setBoost_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "boost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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

    public void setCreatedBy_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setCreatedBy_Terms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        setCreatedBy_SignificantTerms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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

    public void setCreatedBy_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setCreatedBy_IpRange("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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

    public void setCreatedBy_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setCreatedBy_Missing("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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

    public void setCreatedTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setCreatedTime_Histogram("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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

    public void setCreatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setCreatedTime_Range("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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

    public void setCreatedTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setCreatedTime_Missing("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setMaxSize_Avg() {
        setMaxSize_Avg(null);
    }

    public void setMaxSize_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setMaxSize_Avg("maxSize", opLambda);
    }

    public void setMaxSize_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "maxSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_Max() {
        setMaxSize_Max(null);
    }

    public void setMaxSize_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setMaxSize_Max("maxSize", opLambda);
    }

    public void setMaxSize_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "maxSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_Min() {
        setMaxSize_Min(null);
    }

    public void setMaxSize_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setMaxSize_Min("maxSize", opLambda);
    }

    public void setMaxSize_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "maxSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_Sum() {
        setMaxSize_Sum(null);
    }

    public void setMaxSize_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setMaxSize_Sum("maxSize", opLambda);
    }

    public void setMaxSize_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "maxSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_ExtendedStats() {
        setMaxSize_ExtendedStats(null);
    }

    public void setMaxSize_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setMaxSize_ExtendedStats("maxSize", opLambda);
    }

    public void setMaxSize_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "maxSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_Stats() {
        setMaxSize_Stats(null);
    }

    public void setMaxSize_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setMaxSize_Stats("maxSize", opLambda);
    }

    public void setMaxSize_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "maxSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_Percentiles() {
        setMaxSize_Percentiles(null);
    }

    public void setMaxSize_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setMaxSize_Percentiles("maxSize", opLambda);
    }

    public void setMaxSize_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "maxSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_PercentileRanks(double[] values) {
        setMaxSize_PercentileRanks(values, null);
    }

    public void setMaxSize_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setMaxSize_PercentileRanks("maxSize", values, opLambda);
    }

    public void setMaxSize_PercentileRanks(String name, double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "maxSize", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_Histogram() {
        setMaxSize_Histogram(null);
    }

    public void setMaxSize_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setMaxSize_Histogram("maxSize", opLambda, null);
    }

    public void setMaxSize_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setMaxSize_Histogram("maxSize", opLambda, aggsLambda);
    }

    public void setMaxSize_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "maxSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setMaxSize_Range() {
        setMaxSize_Range(null);
    }

    public void setMaxSize_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setMaxSize_Range("maxSize", opLambda, null);
    }

    public void setMaxSize_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setMaxSize_Range("maxSize", opLambda, aggsLambda);
    }

    public void setMaxSize_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "maxSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setMaxSize_Count() {
        setMaxSize_Count(null);
    }

    public void setMaxSize_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setMaxSize_Count("maxSize", opLambda);
    }

    public void setMaxSize_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "maxSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_Cardinality() {
        setMaxSize_Cardinality(null);
    }

    public void setMaxSize_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setMaxSize_Cardinality("maxSize", opLambda);
    }

    public void setMaxSize_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "maxSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMaxSize_Missing() {
        setMaxSize_Missing(null);
    }

    public void setMaxSize_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setMaxSize_Missing("maxSize", opLambda, null);
    }

    public void setMaxSize_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setMaxSize_Missing("maxSize", opLambda, aggsLambda);
    }

    public void setMaxSize_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "maxSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQuery_Terms() {
        setQuery_Terms(null);
    }

    public void setQuery_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setQuery_Terms("query", opLambda, null);
    }

    public void setQuery_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setQuery_Terms("query", opLambda, aggsLambda);
    }

    public void setQuery_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "query");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQuery_SignificantTerms() {
        setQuery_SignificantTerms(null);
    }

    public void setQuery_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setQuery_SignificantTerms("query", opLambda, null);
    }

    public void setQuery_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        setQuery_SignificantTerms("query", opLambda, aggsLambda);
    }

    public void setQuery_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "query");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQuery_IpRange() {
        setQuery_IpRange(null);
    }

    public void setQuery_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setQuery_IpRange("query", opLambda, null);
    }

    public void setQuery_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setQuery_IpRange("query", opLambda, aggsLambda);
    }

    public void setQuery_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "query");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQuery_Count() {
        setQuery_Count(null);
    }

    public void setQuery_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setQuery_Count("query", opLambda);
    }

    public void setQuery_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "query");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQuery_Cardinality() {
        setQuery_Cardinality(null);
    }

    public void setQuery_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setQuery_Cardinality("query", opLambda);
    }

    public void setQuery_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "query");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQuery_Missing() {
        setQuery_Missing(null);
    }

    public void setQuery_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setQuery_Missing("query", opLambda, null);
    }

    public void setQuery_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setQuery_Missing("query", opLambda, aggsLambda);
    }

    public void setQuery_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "query");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTerm_Terms() {
        setTerm_Terms(null);
    }

    public void setTerm_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setTerm_Terms("term", opLambda, null);
    }

    public void setTerm_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setTerm_Terms("term", opLambda, aggsLambda);
    }

    public void setTerm_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "term");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTerm_SignificantTerms() {
        setTerm_SignificantTerms(null);
    }

    public void setTerm_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setTerm_SignificantTerms("term", opLambda, null);
    }

    public void setTerm_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        setTerm_SignificantTerms("term", opLambda, aggsLambda);
    }

    public void setTerm_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "term");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTerm_IpRange() {
        setTerm_IpRange(null);
    }

    public void setTerm_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setTerm_IpRange("term", opLambda, null);
    }

    public void setTerm_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setTerm_IpRange("term", opLambda, aggsLambda);
    }

    public void setTerm_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "term");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTerm_Count() {
        setTerm_Count(null);
    }

    public void setTerm_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setTerm_Count("term", opLambda);
    }

    public void setTerm_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "term");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTerm_Cardinality() {
        setTerm_Cardinality(null);
    }

    public void setTerm_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setTerm_Cardinality("term", opLambda);
    }

    public void setTerm_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "term");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTerm_Missing() {
        setTerm_Missing(null);
    }

    public void setTerm_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setTerm_Missing("term", opLambda, null);
    }

    public void setTerm_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setTerm_Missing("term", opLambda, aggsLambda);
    }

    public void setTerm_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "term");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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

    public void setUpdatedBy_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setUpdatedBy_Terms("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        setUpdatedBy_SignificantTerms("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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

    public void setUpdatedBy_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setUpdatedBy_IpRange("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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

    public void setUpdatedBy_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setUpdatedBy_Missing("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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

    public void setUpdatedTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setUpdatedTime_Histogram("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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

    public void setUpdatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setUpdatedTime_Range("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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

    public void setUpdatedTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setUpdatedTime_Missing("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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

    public void setVirtualHost_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setVirtualHost_Terms("virtualHost", opLambda, aggsLambda);
    }

    public void setVirtualHost_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "virtualHost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        setVirtualHost_SignificantTerms("virtualHost", opLambda, aggsLambda);
    }

    public void setVirtualHost_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "virtualHost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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

    public void setVirtualHost_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setVirtualHost_IpRange("virtualHost", opLambda, aggsLambda);
    }

    public void setVirtualHost_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "virtualHost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
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

    public void setVirtualHost_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsKeyMatchCA> aggsLambda) {
        setVirtualHost_Missing("virtualHost", opLambda, aggsLambda);
    }

    public void setVirtualHost_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsKeyMatchCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "virtualHost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            KeyMatchCA ca = new KeyMatchCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
