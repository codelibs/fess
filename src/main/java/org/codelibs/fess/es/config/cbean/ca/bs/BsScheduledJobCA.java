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
import org.codelibs.fess.es.config.cbean.ca.ScheduledJobCA;
import org.codelibs.fess.es.config.cbean.cq.ScheduledJobCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsScheduledJobCQ;
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
public abstract class BsScheduledJobCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsScheduledJobCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        ScheduledJobCQ cq = new ScheduledJobCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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

    public void setAvailable_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        setAvailable_Histogram("available", opLambda, aggsLambda);
    }

    public void setAvailable_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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

    public void setAvailable_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setAvailable_Range("available", opLambda, aggsLambda);
    }

    public void setAvailable_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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

    public void setAvailable_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setAvailable_Missing("available", opLambda, aggsLambda);
    }

    public void setAvailable_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "available");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCrawler_Avg() {
        setCrawler_Avg(null);
    }

    public void setCrawler_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setCrawler_Avg("crawler", opLambda);
    }

    public void setCrawler_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "crawler");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_Max() {
        setCrawler_Max(null);
    }

    public void setCrawler_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setCrawler_Max("crawler", opLambda);
    }

    public void setCrawler_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "crawler");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_Min() {
        setCrawler_Min(null);
    }

    public void setCrawler_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setCrawler_Min("crawler", opLambda);
    }

    public void setCrawler_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "crawler");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_Sum() {
        setCrawler_Sum(null);
    }

    public void setCrawler_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setCrawler_Sum("crawler", opLambda);
    }

    public void setCrawler_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "crawler");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_ExtendedStats() {
        setCrawler_ExtendedStats(null);
    }

    public void setCrawler_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setCrawler_ExtendedStats("crawler", opLambda);
    }

    public void setCrawler_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "crawler");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_Stats() {
        setCrawler_Stats(null);
    }

    public void setCrawler_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setCrawler_Stats("crawler", opLambda);
    }

    public void setCrawler_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "crawler");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_Percentiles() {
        setCrawler_Percentiles(null);
    }

    public void setCrawler_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setCrawler_Percentiles("crawler", opLambda);
    }

    public void setCrawler_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "crawler");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_PercentileRanks(double[] values) {
        setCrawler_PercentileRanks(values, null);
    }

    public void setCrawler_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setCrawler_PercentileRanks("crawler", values, opLambda);
    }

    public void setCrawler_PercentileRanks(String name, double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "crawler", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_Histogram() {
        setCrawler_Histogram(null);
    }

    public void setCrawler_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setCrawler_Histogram("crawler", opLambda, null);
    }

    public void setCrawler_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setCrawler_Histogram("crawler", opLambda, aggsLambda);
    }

    public void setCrawler_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "crawler");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCrawler_Range() {
        setCrawler_Range(null);
    }

    public void setCrawler_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setCrawler_Range("crawler", opLambda, null);
    }

    public void setCrawler_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setCrawler_Range("crawler", opLambda, aggsLambda);
    }

    public void setCrawler_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "crawler");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCrawler_Count() {
        setCrawler_Count(null);
    }

    public void setCrawler_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setCrawler_Count("crawler", opLambda);
    }

    public void setCrawler_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "crawler");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_Cardinality() {
        setCrawler_Cardinality(null);
    }

    public void setCrawler_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setCrawler_Cardinality("crawler", opLambda);
    }

    public void setCrawler_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "crawler");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawler_Missing() {
        setCrawler_Missing(null);
    }

    public void setCrawler_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setCrawler_Missing("crawler", opLambda, null);
    }

    public void setCrawler_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setCrawler_Missing("crawler", opLambda, aggsLambda);
    }

    public void setCrawler_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "crawler");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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

    public void setCreatedBy_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setCreatedBy_Terms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        setCreatedBy_SignificantTerms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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

    public void setCreatedBy_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setCreatedBy_IpRange("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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

    public void setCreatedBy_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setCreatedBy_Missing("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        setCreatedTime_Histogram("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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

    public void setCreatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setCreatedTime_Range("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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

    public void setCreatedTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setCreatedTime_Missing("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCronExpression_Terms() {
        setCronExpression_Terms(null);
    }

    public void setCronExpression_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setCronExpression_Terms("cronExpression", opLambda, null);
    }

    public void setCronExpression_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setCronExpression_Terms("cronExpression", opLambda, aggsLambda);
    }

    public void setCronExpression_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "cronExpression");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCronExpression_SignificantTerms() {
        setCronExpression_SignificantTerms(null);
    }

    public void setCronExpression_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setCronExpression_SignificantTerms("cronExpression", opLambda, null);
    }

    public void setCronExpression_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        setCronExpression_SignificantTerms("cronExpression", opLambda, aggsLambda);
    }

    public void setCronExpression_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "cronExpression");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCronExpression_IpRange() {
        setCronExpression_IpRange(null);
    }

    public void setCronExpression_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setCronExpression_IpRange("cronExpression", opLambda, null);
    }

    public void setCronExpression_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        setCronExpression_IpRange("cronExpression", opLambda, aggsLambda);
    }

    public void setCronExpression_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "cronExpression");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCronExpression_Count() {
        setCronExpression_Count(null);
    }

    public void setCronExpression_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setCronExpression_Count("cronExpression", opLambda);
    }

    public void setCronExpression_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "cronExpression");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_Cardinality() {
        setCronExpression_Cardinality(null);
    }

    public void setCronExpression_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setCronExpression_Cardinality("cronExpression", opLambda);
    }

    public void setCronExpression_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "cronExpression");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCronExpression_Missing() {
        setCronExpression_Missing(null);
    }

    public void setCronExpression_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setCronExpression_Missing("cronExpression", opLambda, null);
    }

    public void setCronExpression_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        setCronExpression_Missing("cronExpression", opLambda, aggsLambda);
    }

    public void setCronExpression_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "cronExpression");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setJobLogging_Avg() {
        setJobLogging_Avg(null);
    }

    public void setJobLogging_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setJobLogging_Avg("jobLogging", opLambda);
    }

    public void setJobLogging_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "jobLogging");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_Max() {
        setJobLogging_Max(null);
    }

    public void setJobLogging_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setJobLogging_Max("jobLogging", opLambda);
    }

    public void setJobLogging_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "jobLogging");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_Min() {
        setJobLogging_Min(null);
    }

    public void setJobLogging_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setJobLogging_Min("jobLogging", opLambda);
    }

    public void setJobLogging_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "jobLogging");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_Sum() {
        setJobLogging_Sum(null);
    }

    public void setJobLogging_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setJobLogging_Sum("jobLogging", opLambda);
    }

    public void setJobLogging_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "jobLogging");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_ExtendedStats() {
        setJobLogging_ExtendedStats(null);
    }

    public void setJobLogging_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setJobLogging_ExtendedStats("jobLogging", opLambda);
    }

    public void setJobLogging_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "jobLogging");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_Stats() {
        setJobLogging_Stats(null);
    }

    public void setJobLogging_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setJobLogging_Stats("jobLogging", opLambda);
    }

    public void setJobLogging_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "jobLogging");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_Percentiles() {
        setJobLogging_Percentiles(null);
    }

    public void setJobLogging_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setJobLogging_Percentiles("jobLogging", opLambda);
    }

    public void setJobLogging_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "jobLogging");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_PercentileRanks(double[] values) {
        setJobLogging_PercentileRanks(values, null);
    }

    public void setJobLogging_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setJobLogging_PercentileRanks("jobLogging", values, opLambda);
    }

    public void setJobLogging_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "jobLogging", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_Histogram() {
        setJobLogging_Histogram(null);
    }

    public void setJobLogging_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setJobLogging_Histogram("jobLogging", opLambda, null);
    }

    public void setJobLogging_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        setJobLogging_Histogram("jobLogging", opLambda, aggsLambda);
    }

    public void setJobLogging_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "jobLogging");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setJobLogging_Range() {
        setJobLogging_Range(null);
    }

    public void setJobLogging_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setJobLogging_Range("jobLogging", opLambda, null);
    }

    public void setJobLogging_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setJobLogging_Range("jobLogging", opLambda, aggsLambda);
    }

    public void setJobLogging_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "jobLogging");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setJobLogging_Count() {
        setJobLogging_Count(null);
    }

    public void setJobLogging_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setJobLogging_Count("jobLogging", opLambda);
    }

    public void setJobLogging_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "jobLogging");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_Cardinality() {
        setJobLogging_Cardinality(null);
    }

    public void setJobLogging_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setJobLogging_Cardinality("jobLogging", opLambda);
    }

    public void setJobLogging_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "jobLogging");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobLogging_Missing() {
        setJobLogging_Missing(null);
    }

    public void setJobLogging_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setJobLogging_Missing("jobLogging", opLambda, null);
    }

    public void setJobLogging_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setJobLogging_Missing("jobLogging", opLambda, aggsLambda);
    }

    public void setJobLogging_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "jobLogging");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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

    public void setName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setName_Terms("name", opLambda, aggsLambda);
    }

    public void setName_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        setName_SignificantTerms("name", opLambda, aggsLambda);
    }

    public void setName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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

    public void setName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setName_IpRange("name", opLambda, aggsLambda);
    }

    public void setName_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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

    public void setName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setName_Missing("name", opLambda, aggsLambda);
    }

    public void setName_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setScriptData_Terms() {
        setScriptData_Terms(null);
    }

    public void setScriptData_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setScriptData_Terms("scriptData", opLambda, null);
    }

    public void setScriptData_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setScriptData_Terms("scriptData", opLambda, aggsLambda);
    }

    public void setScriptData_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "scriptData");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setScriptData_SignificantTerms() {
        setScriptData_SignificantTerms(null);
    }

    public void setScriptData_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setScriptData_SignificantTerms("scriptData", opLambda, null);
    }

    public void setScriptData_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        setScriptData_SignificantTerms("scriptData", opLambda, aggsLambda);
    }

    public void setScriptData_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "scriptData");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setScriptData_IpRange() {
        setScriptData_IpRange(null);
    }

    public void setScriptData_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setScriptData_IpRange("scriptData", opLambda, null);
    }

    public void setScriptData_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setScriptData_IpRange("scriptData", opLambda, aggsLambda);
    }

    public void setScriptData_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "scriptData");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setScriptData_Count() {
        setScriptData_Count(null);
    }

    public void setScriptData_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setScriptData_Count("scriptData", opLambda);
    }

    public void setScriptData_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "scriptData");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Cardinality() {
        setScriptData_Cardinality(null);
    }

    public void setScriptData_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setScriptData_Cardinality("scriptData", opLambda);
    }

    public void setScriptData_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "scriptData");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Missing() {
        setScriptData_Missing(null);
    }

    public void setScriptData_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setScriptData_Missing("scriptData", opLambda, null);
    }

    public void setScriptData_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setScriptData_Missing("scriptData", opLambda, aggsLambda);
    }

    public void setScriptData_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "scriptData");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setScriptType_Terms() {
        setScriptType_Terms(null);
    }

    public void setScriptType_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setScriptType_Terms("scriptType", opLambda, null);
    }

    public void setScriptType_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setScriptType_Terms("scriptType", opLambda, aggsLambda);
    }

    public void setScriptType_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "scriptType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setScriptType_SignificantTerms() {
        setScriptType_SignificantTerms(null);
    }

    public void setScriptType_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setScriptType_SignificantTerms("scriptType", opLambda, null);
    }

    public void setScriptType_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        setScriptType_SignificantTerms("scriptType", opLambda, aggsLambda);
    }

    public void setScriptType_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "scriptType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setScriptType_IpRange() {
        setScriptType_IpRange(null);
    }

    public void setScriptType_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setScriptType_IpRange("scriptType", opLambda, null);
    }

    public void setScriptType_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setScriptType_IpRange("scriptType", opLambda, aggsLambda);
    }

    public void setScriptType_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "scriptType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setScriptType_Count() {
        setScriptType_Count(null);
    }

    public void setScriptType_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setScriptType_Count("scriptType", opLambda);
    }

    public void setScriptType_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "scriptType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Cardinality() {
        setScriptType_Cardinality(null);
    }

    public void setScriptType_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setScriptType_Cardinality("scriptType", opLambda);
    }

    public void setScriptType_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "scriptType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Missing() {
        setScriptType_Missing(null);
    }

    public void setScriptType_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setScriptType_Missing("scriptType", opLambda, null);
    }

    public void setScriptType_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setScriptType_Missing("scriptType", opLambda, aggsLambda);
    }

    public void setScriptType_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "scriptType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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

    public void setSortOrder_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        setSortOrder_Histogram("sortOrder", opLambda, aggsLambda);
    }

    public void setSortOrder_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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

    public void setSortOrder_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setSortOrder_Range("sortOrder", opLambda, aggsLambda);
    }

    public void setSortOrder_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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

    public void setSortOrder_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setSortOrder_Missing("sortOrder", opLambda, aggsLambda);
    }

    public void setSortOrder_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "sortOrder");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTarget_Terms() {
        setTarget_Terms(null);
    }

    public void setTarget_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setTarget_Terms("target", opLambda, null);
    }

    public void setTarget_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setTarget_Terms("target", opLambda, aggsLambda);
    }

    public void setTarget_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTarget_SignificantTerms() {
        setTarget_SignificantTerms(null);
    }

    public void setTarget_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setTarget_SignificantTerms("target", opLambda, null);
    }

    public void setTarget_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        setTarget_SignificantTerms("target", opLambda, aggsLambda);
    }

    public void setTarget_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTarget_IpRange() {
        setTarget_IpRange(null);
    }

    public void setTarget_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setTarget_IpRange("target", opLambda, null);
    }

    public void setTarget_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setTarget_IpRange("target", opLambda, aggsLambda);
    }

    public void setTarget_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTarget_Count() {
        setTarget_Count(null);
    }

    public void setTarget_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setTarget_Count("target", opLambda);
    }

    public void setTarget_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Cardinality() {
        setTarget_Cardinality(null);
    }

    public void setTarget_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setTarget_Cardinality("target", opLambda);
    }

    public void setTarget_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Missing() {
        setTarget_Missing(null);
    }

    public void setTarget_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setTarget_Missing("target", opLambda, null);
    }

    public void setTarget_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setTarget_Missing("target", opLambda, aggsLambda);
    }

    public void setTarget_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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

    public void setUpdatedBy_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setUpdatedBy_Terms("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        setUpdatedBy_SignificantTerms("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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

    public void setUpdatedBy_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setUpdatedBy_IpRange("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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

    public void setUpdatedBy_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setUpdatedBy_Missing("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        setUpdatedTime_Histogram("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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

    public void setUpdatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setUpdatedTime_Range("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
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

    public void setUpdatedTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsScheduledJobCA> aggsLambda) {
        setUpdatedTime_Missing("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsScheduledJobCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ScheduledJobCA ca = new ScheduledJobCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
