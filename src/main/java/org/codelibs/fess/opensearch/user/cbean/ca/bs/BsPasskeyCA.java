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
package org.codelibs.fess.opensearch.user.cbean.ca.bs;

import java.util.List;

import org.codelibs.fess.opensearch.user.allcommon.EsAbstractConditionAggregation;
import org.codelibs.fess.opensearch.user.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.opensearch.user.cbean.ca.PasskeyCA;
import org.codelibs.fess.opensearch.user.cbean.cq.PasskeyCQ;
import org.codelibs.fess.opensearch.user.cbean.cq.bs.BsPasskeyCQ;

import org.codelibs.fesen.opensearch.search.aggregations.AbstractAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.AggregationBuilders;
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
import org.codelibs.fesen.opensearch.search.aggregations.metrics.*;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsPasskeyCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsPasskeyCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        PasskeyCQ cq = new PasskeyCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
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

    public void setBackupEligible_Avg() {
        setBackupEligible_Avg(null);
    }

    public void setBackupEligible_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setBackupEligible_Avg("backupEligible", opLambda);
    }

    public void setBackupEligible_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "backupEligible");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_Max() {
        setBackupEligible_Max(null);
    }

    public void setBackupEligible_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setBackupEligible_Max("backupEligible", opLambda);
    }

    public void setBackupEligible_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "backupEligible");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_Min() {
        setBackupEligible_Min(null);
    }

    public void setBackupEligible_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setBackupEligible_Min("backupEligible", opLambda);
    }

    public void setBackupEligible_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "backupEligible");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_Sum() {
        setBackupEligible_Sum(null);
    }

    public void setBackupEligible_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setBackupEligible_Sum("backupEligible", opLambda);
    }

    public void setBackupEligible_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "backupEligible");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_ExtendedStats() {
        setBackupEligible_ExtendedStats(null);
    }

    public void setBackupEligible_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setBackupEligible_ExtendedStats("backupEligible", opLambda);
    }

    public void setBackupEligible_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "backupEligible");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_Stats() {
        setBackupEligible_Stats(null);
    }

    public void setBackupEligible_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setBackupEligible_Stats("backupEligible", opLambda);
    }

    public void setBackupEligible_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "backupEligible");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_Percentiles() {
        setBackupEligible_Percentiles(null);
    }

    public void setBackupEligible_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setBackupEligible_Percentiles("backupEligible", opLambda);
    }

    public void setBackupEligible_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "backupEligible");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_PercentileRanks(double[] values) {
        setBackupEligible_PercentileRanks(values, null);
    }

    public void setBackupEligible_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setBackupEligible_PercentileRanks("backupEligible", values, opLambda);
    }

    public void setBackupEligible_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "backupEligible", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_Histogram() {
        setBackupEligible_Histogram(null);
    }

    public void setBackupEligible_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setBackupEligible_Histogram("backupEligible", opLambda, null);
    }

    public void setBackupEligible_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        setBackupEligible_Histogram("backupEligible", opLambda, aggsLambda);
    }

    public void setBackupEligible_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "backupEligible");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setBackupEligible_Range() {
        setBackupEligible_Range(null);
    }

    public void setBackupEligible_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setBackupEligible_Range("backupEligible", opLambda, null);
    }

    public void setBackupEligible_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setBackupEligible_Range("backupEligible", opLambda, aggsLambda);
    }

    public void setBackupEligible_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "backupEligible");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setBackupEligible_Count() {
        setBackupEligible_Count(null);
    }

    public void setBackupEligible_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setBackupEligible_Count("backupEligible", opLambda);
    }

    public void setBackupEligible_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "backupEligible");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_Cardinality() {
        setBackupEligible_Cardinality(null);
    }

    public void setBackupEligible_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setBackupEligible_Cardinality("backupEligible", opLambda);
    }

    public void setBackupEligible_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "backupEligible");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupEligible_Missing() {
        setBackupEligible_Missing(null);
    }

    public void setBackupEligible_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setBackupEligible_Missing("backupEligible", opLambda, null);
    }

    public void setBackupEligible_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setBackupEligible_Missing("backupEligible", opLambda, aggsLambda);
    }

    public void setBackupEligible_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "backupEligible");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setBackupState_Avg() {
        setBackupState_Avg(null);
    }

    public void setBackupState_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setBackupState_Avg("backupState", opLambda);
    }

    public void setBackupState_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "backupState");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_Max() {
        setBackupState_Max(null);
    }

    public void setBackupState_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setBackupState_Max("backupState", opLambda);
    }

    public void setBackupState_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "backupState");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_Min() {
        setBackupState_Min(null);
    }

    public void setBackupState_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setBackupState_Min("backupState", opLambda);
    }

    public void setBackupState_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "backupState");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_Sum() {
        setBackupState_Sum(null);
    }

    public void setBackupState_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setBackupState_Sum("backupState", opLambda);
    }

    public void setBackupState_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "backupState");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_ExtendedStats() {
        setBackupState_ExtendedStats(null);
    }

    public void setBackupState_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setBackupState_ExtendedStats("backupState", opLambda);
    }

    public void setBackupState_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "backupState");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_Stats() {
        setBackupState_Stats(null);
    }

    public void setBackupState_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setBackupState_Stats("backupState", opLambda);
    }

    public void setBackupState_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "backupState");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_Percentiles() {
        setBackupState_Percentiles(null);
    }

    public void setBackupState_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setBackupState_Percentiles("backupState", opLambda);
    }

    public void setBackupState_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "backupState");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_PercentileRanks(double[] values) {
        setBackupState_PercentileRanks(values, null);
    }

    public void setBackupState_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setBackupState_PercentileRanks("backupState", values, opLambda);
    }

    public void setBackupState_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "backupState", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_Histogram() {
        setBackupState_Histogram(null);
    }

    public void setBackupState_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setBackupState_Histogram("backupState", opLambda, null);
    }

    public void setBackupState_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setBackupState_Histogram("backupState", opLambda, aggsLambda);
    }

    public void setBackupState_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "backupState");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setBackupState_Range() {
        setBackupState_Range(null);
    }

    public void setBackupState_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setBackupState_Range("backupState", opLambda, null);
    }

    public void setBackupState_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setBackupState_Range("backupState", opLambda, aggsLambda);
    }

    public void setBackupState_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "backupState");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setBackupState_Count() {
        setBackupState_Count(null);
    }

    public void setBackupState_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setBackupState_Count("backupState", opLambda);
    }

    public void setBackupState_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "backupState");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_Cardinality() {
        setBackupState_Cardinality(null);
    }

    public void setBackupState_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setBackupState_Cardinality("backupState", opLambda);
    }

    public void setBackupState_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "backupState");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBackupState_Missing() {
        setBackupState_Missing(null);
    }

    public void setBackupState_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setBackupState_Missing("backupState", opLambda, null);
    }

    public void setBackupState_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setBackupState_Missing("backupState", opLambda, aggsLambda);
    }

    public void setBackupState_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "backupState");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCreatedAt_Avg() {
        setCreatedAt_Avg(null);
    }

    public void setCreatedAt_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setCreatedAt_Avg("createdAt", opLambda);
    }

    public void setCreatedAt_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Max() {
        setCreatedAt_Max(null);
    }

    public void setCreatedAt_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setCreatedAt_Max("createdAt", opLambda);
    }

    public void setCreatedAt_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Min() {
        setCreatedAt_Min(null);
    }

    public void setCreatedAt_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setCreatedAt_Min("createdAt", opLambda);
    }

    public void setCreatedAt_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Sum() {
        setCreatedAt_Sum(null);
    }

    public void setCreatedAt_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setCreatedAt_Sum("createdAt", opLambda);
    }

    public void setCreatedAt_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_ExtendedStats() {
        setCreatedAt_ExtendedStats(null);
    }

    public void setCreatedAt_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setCreatedAt_ExtendedStats("createdAt", opLambda);
    }

    public void setCreatedAt_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Stats() {
        setCreatedAt_Stats(null);
    }

    public void setCreatedAt_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setCreatedAt_Stats("createdAt", opLambda);
    }

    public void setCreatedAt_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Percentiles() {
        setCreatedAt_Percentiles(null);
    }

    public void setCreatedAt_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setCreatedAt_Percentiles("createdAt", opLambda);
    }

    public void setCreatedAt_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_PercentileRanks(double[] values) {
        setCreatedAt_PercentileRanks(values, null);
    }

    public void setCreatedAt_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setCreatedAt_PercentileRanks("createdAt", values, opLambda);
    }

    public void setCreatedAt_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "createdAt", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Histogram() {
        setCreatedAt_Histogram(null);
    }

    public void setCreatedAt_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setCreatedAt_Histogram("createdAt", opLambda, null);
    }

    public void setCreatedAt_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setCreatedAt_Histogram("createdAt", opLambda, aggsLambda);
    }

    public void setCreatedAt_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCreatedAt_Range() {
        setCreatedAt_Range(null);
    }

    public void setCreatedAt_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setCreatedAt_Range("createdAt", opLambda, null);
    }

    public void setCreatedAt_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setCreatedAt_Range("createdAt", opLambda, aggsLambda);
    }

    public void setCreatedAt_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCreatedAt_Count() {
        setCreatedAt_Count(null);
    }

    public void setCreatedAt_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setCreatedAt_Count("createdAt", opLambda);
    }

    public void setCreatedAt_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Cardinality() {
        setCreatedAt_Cardinality(null);
    }

    public void setCreatedAt_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setCreatedAt_Cardinality("createdAt", opLambda);
    }

    public void setCreatedAt_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Missing() {
        setCreatedAt_Missing(null);
    }

    public void setCreatedAt_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setCreatedAt_Missing("createdAt", opLambda, null);
    }

    public void setCreatedAt_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setCreatedAt_Missing("createdAt", opLambda, aggsLambda);
    }

    public void setCreatedAt_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLabel_Terms() {
        setLabel_Terms(null);
    }

    public void setLabel_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setLabel_Terms("label", opLambda, null);
    }

    public void setLabel_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setLabel_Terms("label", opLambda, aggsLambda);
    }

    public void setLabel_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "label");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLabel_SignificantTerms() {
        setLabel_SignificantTerms(null);
    }

    public void setLabel_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setLabel_SignificantTerms("label", opLambda, null);
    }

    public void setLabel_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        setLabel_SignificantTerms("label", opLambda, aggsLambda);
    }

    public void setLabel_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "label");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLabel_IpRange() {
        setLabel_IpRange(null);
    }

    public void setLabel_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setLabel_IpRange("label", opLambda, null);
    }

    public void setLabel_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setLabel_IpRange("label", opLambda, aggsLambda);
    }

    public void setLabel_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "label");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLabel_Count() {
        setLabel_Count(null);
    }

    public void setLabel_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setLabel_Count("label", opLambda);
    }

    public void setLabel_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "label");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabel_Cardinality() {
        setLabel_Cardinality(null);
    }

    public void setLabel_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setLabel_Cardinality("label", opLambda);
    }

    public void setLabel_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "label");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabel_Missing() {
        setLabel_Missing(null);
    }

    public void setLabel_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setLabel_Missing("label", opLambda, null);
    }

    public void setLabel_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setLabel_Missing("label", opLambda, aggsLambda);
    }

    public void setLabel_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "label");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLastUsedAt_Avg() {
        setLastUsedAt_Avg(null);
    }

    public void setLastUsedAt_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setLastUsedAt_Avg("lastUsedAt", opLambda);
    }

    public void setLastUsedAt_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "lastUsedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_Max() {
        setLastUsedAt_Max(null);
    }

    public void setLastUsedAt_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setLastUsedAt_Max("lastUsedAt", opLambda);
    }

    public void setLastUsedAt_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "lastUsedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_Min() {
        setLastUsedAt_Min(null);
    }

    public void setLastUsedAt_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setLastUsedAt_Min("lastUsedAt", opLambda);
    }

    public void setLastUsedAt_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "lastUsedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_Sum() {
        setLastUsedAt_Sum(null);
    }

    public void setLastUsedAt_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setLastUsedAt_Sum("lastUsedAt", opLambda);
    }

    public void setLastUsedAt_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "lastUsedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_ExtendedStats() {
        setLastUsedAt_ExtendedStats(null);
    }

    public void setLastUsedAt_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setLastUsedAt_ExtendedStats("lastUsedAt", opLambda);
    }

    public void setLastUsedAt_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "lastUsedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_Stats() {
        setLastUsedAt_Stats(null);
    }

    public void setLastUsedAt_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setLastUsedAt_Stats("lastUsedAt", opLambda);
    }

    public void setLastUsedAt_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "lastUsedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_Percentiles() {
        setLastUsedAt_Percentiles(null);
    }

    public void setLastUsedAt_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setLastUsedAt_Percentiles("lastUsedAt", opLambda);
    }

    public void setLastUsedAt_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "lastUsedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_PercentileRanks(double[] values) {
        setLastUsedAt_PercentileRanks(values, null);
    }

    public void setLastUsedAt_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setLastUsedAt_PercentileRanks("lastUsedAt", values, opLambda);
    }

    public void setLastUsedAt_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "lastUsedAt", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_Histogram() {
        setLastUsedAt_Histogram(null);
    }

    public void setLastUsedAt_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setLastUsedAt_Histogram("lastUsedAt", opLambda, null);
    }

    public void setLastUsedAt_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setLastUsedAt_Histogram("lastUsedAt", opLambda, aggsLambda);
    }

    public void setLastUsedAt_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "lastUsedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLastUsedAt_Range() {
        setLastUsedAt_Range(null);
    }

    public void setLastUsedAt_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setLastUsedAt_Range("lastUsedAt", opLambda, null);
    }

    public void setLastUsedAt_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setLastUsedAt_Range("lastUsedAt", opLambda, aggsLambda);
    }

    public void setLastUsedAt_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "lastUsedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLastUsedAt_Count() {
        setLastUsedAt_Count(null);
    }

    public void setLastUsedAt_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setLastUsedAt_Count("lastUsedAt", opLambda);
    }

    public void setLastUsedAt_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "lastUsedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_Cardinality() {
        setLastUsedAt_Cardinality(null);
    }

    public void setLastUsedAt_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setLastUsedAt_Cardinality("lastUsedAt", opLambda);
    }

    public void setLastUsedAt_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "lastUsedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUsedAt_Missing() {
        setLastUsedAt_Missing(null);
    }

    public void setLastUsedAt_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setLastUsedAt_Missing("lastUsedAt", opLambda, null);
    }

    public void setLastUsedAt_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setLastUsedAt_Missing("lastUsedAt", opLambda, aggsLambda);
    }

    public void setLastUsedAt_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "lastUsedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPublicKey_Terms() {
        setPublicKey_Terms(null);
    }

    public void setPublicKey_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setPublicKey_Terms("publicKey", opLambda, null);
    }

    public void setPublicKey_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setPublicKey_Terms("publicKey", opLambda, aggsLambda);
    }

    public void setPublicKey_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "publicKey");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPublicKey_SignificantTerms() {
        setPublicKey_SignificantTerms(null);
    }

    public void setPublicKey_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setPublicKey_SignificantTerms("publicKey", opLambda, null);
    }

    public void setPublicKey_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        setPublicKey_SignificantTerms("publicKey", opLambda, aggsLambda);
    }

    public void setPublicKey_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "publicKey");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPublicKey_IpRange() {
        setPublicKey_IpRange(null);
    }

    public void setPublicKey_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setPublicKey_IpRange("publicKey", opLambda, null);
    }

    public void setPublicKey_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setPublicKey_IpRange("publicKey", opLambda, aggsLambda);
    }

    public void setPublicKey_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "publicKey");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPublicKey_Count() {
        setPublicKey_Count(null);
    }

    public void setPublicKey_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setPublicKey_Count("publicKey", opLambda);
    }

    public void setPublicKey_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "publicKey");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPublicKey_Cardinality() {
        setPublicKey_Cardinality(null);
    }

    public void setPublicKey_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setPublicKey_Cardinality("publicKey", opLambda);
    }

    public void setPublicKey_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "publicKey");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPublicKey_Missing() {
        setPublicKey_Missing(null);
    }

    public void setPublicKey_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setPublicKey_Missing("publicKey", opLambda, null);
    }

    public void setPublicKey_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setPublicKey_Missing("publicKey", opLambda, aggsLambda);
    }

    public void setPublicKey_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "publicKey");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSignCount_Avg() {
        setSignCount_Avg(null);
    }

    public void setSignCount_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setSignCount_Avg("signCount", opLambda);
    }

    public void setSignCount_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "signCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_Max() {
        setSignCount_Max(null);
    }

    public void setSignCount_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setSignCount_Max("signCount", opLambda);
    }

    public void setSignCount_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "signCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_Min() {
        setSignCount_Min(null);
    }

    public void setSignCount_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setSignCount_Min("signCount", opLambda);
    }

    public void setSignCount_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "signCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_Sum() {
        setSignCount_Sum(null);
    }

    public void setSignCount_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setSignCount_Sum("signCount", opLambda);
    }

    public void setSignCount_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "signCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_ExtendedStats() {
        setSignCount_ExtendedStats(null);
    }

    public void setSignCount_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setSignCount_ExtendedStats("signCount", opLambda);
    }

    public void setSignCount_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "signCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_Stats() {
        setSignCount_Stats(null);
    }

    public void setSignCount_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setSignCount_Stats("signCount", opLambda);
    }

    public void setSignCount_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "signCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_Percentiles() {
        setSignCount_Percentiles(null);
    }

    public void setSignCount_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setSignCount_Percentiles("signCount", opLambda);
    }

    public void setSignCount_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "signCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_PercentileRanks(double[] values) {
        setSignCount_PercentileRanks(values, null);
    }

    public void setSignCount_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setSignCount_PercentileRanks("signCount", values, opLambda);
    }

    public void setSignCount_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "signCount", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_Histogram() {
        setSignCount_Histogram(null);
    }

    public void setSignCount_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setSignCount_Histogram("signCount", opLambda, null);
    }

    public void setSignCount_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setSignCount_Histogram("signCount", opLambda, aggsLambda);
    }

    public void setSignCount_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "signCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSignCount_Range() {
        setSignCount_Range(null);
    }

    public void setSignCount_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setSignCount_Range("signCount", opLambda, null);
    }

    public void setSignCount_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setSignCount_Range("signCount", opLambda, aggsLambda);
    }

    public void setSignCount_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "signCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSignCount_Count() {
        setSignCount_Count(null);
    }

    public void setSignCount_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setSignCount_Count("signCount", opLambda);
    }

    public void setSignCount_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "signCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_Cardinality() {
        setSignCount_Cardinality(null);
    }

    public void setSignCount_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setSignCount_Cardinality("signCount", opLambda);
    }

    public void setSignCount_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "signCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSignCount_Missing() {
        setSignCount_Missing(null);
    }

    public void setSignCount_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setSignCount_Missing("signCount", opLambda, null);
    }

    public void setSignCount_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setSignCount_Missing("signCount", opLambda, aggsLambda);
    }

    public void setSignCount_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "signCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTransports_Terms() {
        setTransports_Terms(null);
    }

    public void setTransports_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setTransports_Terms("transports", opLambda, null);
    }

    public void setTransports_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setTransports_Terms("transports", opLambda, aggsLambda);
    }

    public void setTransports_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "transports");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTransports_SignificantTerms() {
        setTransports_SignificantTerms(null);
    }

    public void setTransports_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setTransports_SignificantTerms("transports", opLambda, null);
    }

    public void setTransports_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        setTransports_SignificantTerms("transports", opLambda, aggsLambda);
    }

    public void setTransports_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "transports");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTransports_IpRange() {
        setTransports_IpRange(null);
    }

    public void setTransports_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setTransports_IpRange("transports", opLambda, null);
    }

    public void setTransports_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setTransports_IpRange("transports", opLambda, aggsLambda);
    }

    public void setTransports_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "transports");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTransports_Count() {
        setTransports_Count(null);
    }

    public void setTransports_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setTransports_Count("transports", opLambda);
    }

    public void setTransports_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "transports");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTransports_Cardinality() {
        setTransports_Cardinality(null);
    }

    public void setTransports_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setTransports_Cardinality("transports", opLambda);
    }

    public void setTransports_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "transports");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTransports_Missing() {
        setTransports_Missing(null);
    }

    public void setTransports_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setTransports_Missing("transports", opLambda, null);
    }

    public void setTransports_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setTransports_Missing("transports", opLambda, aggsLambda);
    }

    public void setTransports_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "transports");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserHandle_Terms() {
        setUserHandle_Terms(null);
    }

    public void setUserHandle_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setUserHandle_Terms("userHandle", opLambda, null);
    }

    public void setUserHandle_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setUserHandle_Terms("userHandle", opLambda, aggsLambda);
    }

    public void setUserHandle_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "userHandle");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserHandle_SignificantTerms() {
        setUserHandle_SignificantTerms(null);
    }

    public void setUserHandle_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setUserHandle_SignificantTerms("userHandle", opLambda, null);
    }

    public void setUserHandle_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        setUserHandle_SignificantTerms("userHandle", opLambda, aggsLambda);
    }

    public void setUserHandle_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "userHandle");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserHandle_IpRange() {
        setUserHandle_IpRange(null);
    }

    public void setUserHandle_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setUserHandle_IpRange("userHandle", opLambda, null);
    }

    public void setUserHandle_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setUserHandle_IpRange("userHandle", opLambda, aggsLambda);
    }

    public void setUserHandle_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "userHandle");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserHandle_Count() {
        setUserHandle_Count(null);
    }

    public void setUserHandle_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setUserHandle_Count("userHandle", opLambda);
    }

    public void setUserHandle_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "userHandle");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserHandle_Cardinality() {
        setUserHandle_Cardinality(null);
    }

    public void setUserHandle_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setUserHandle_Cardinality("userHandle", opLambda);
    }

    public void setUserHandle_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "userHandle");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserHandle_Missing() {
        setUserHandle_Missing(null);
    }

    public void setUserHandle_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setUserHandle_Missing("userHandle", opLambda, null);
    }

    public void setUserHandle_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setUserHandle_Missing("userHandle", opLambda, aggsLambda);
    }

    public void setUserHandle_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "userHandle");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserName_Terms() {
        setUserName_Terms(null);
    }

    public void setUserName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setUserName_Terms("userName", opLambda, null);
    }

    public void setUserName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setUserName_Terms("userName", opLambda, aggsLambda);
    }

    public void setUserName_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "userName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserName_SignificantTerms() {
        setUserName_SignificantTerms(null);
    }

    public void setUserName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setUserName_SignificantTerms("userName", opLambda, null);
    }

    public void setUserName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        setUserName_SignificantTerms("userName", opLambda, aggsLambda);
    }

    public void setUserName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "userName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserName_IpRange() {
        setUserName_IpRange(null);
    }

    public void setUserName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setUserName_IpRange("userName", opLambda, null);
    }

    public void setUserName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setUserName_IpRange("userName", opLambda, aggsLambda);
    }

    public void setUserName_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "userName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserName_Count() {
        setUserName_Count(null);
    }

    public void setUserName_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setUserName_Count("userName", opLambda);
    }

    public void setUserName_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "userName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserName_Cardinality() {
        setUserName_Cardinality(null);
    }

    public void setUserName_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setUserName_Cardinality("userName", opLambda);
    }

    public void setUserName_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "userName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserName_Missing() {
        setUserName_Missing(null);
    }

    public void setUserName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setUserName_Missing("userName", opLambda, null);
    }

    public void setUserName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsPasskeyCA> aggsLambda) {
        setUserName_Missing("userName", opLambda, aggsLambda);
    }

    public void setUserName_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsPasskeyCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "userName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            PasskeyCA ca = new PasskeyCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
