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
import org.codelibs.fess.es.config.cbean.ca.ThumbnailQueueCA;
import org.codelibs.fess.es.config.cbean.cq.ThumbnailQueueCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsThumbnailQueueCQ;
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

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
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

    public void setCreatedBy_Terms() {
        setCreatedBy_Terms(null);
    }

    public void setCreatedBy_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setCreatedBy_Terms("createdBy", opLambda, null);
    }

    public void setCreatedBy_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setCreatedBy_Terms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "createdBy");
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

    public void setCreatedBy_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setCreatedBy_SignificantTerms("createdBy", opLambda, null);
    }

    public void setCreatedBy_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setCreatedBy_SignificantTerms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "createdBy");
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

    public void setCreatedBy_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setCreatedBy_IpRange("createdBy", opLambda, null);
    }

    public void setCreatedBy_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setCreatedBy_IpRange("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "createdBy");
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

    public void setCreatedBy_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setCreatedBy_Missing("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
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
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setCreatedTime_Histogram("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "createdTime");
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

    public void setCreatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setCreatedTime_Range("createdTime", opLambda, null);
    }

    public void setCreatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setCreatedTime_Range("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "createdTime");
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

    public void setCreatedTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setCreatedTime_Missing("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setGenerator_Terms() {
        setGenerator_Terms(null);
    }

    public void setGenerator_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setGenerator_Terms("generator", opLambda, null);
    }

    public void setGenerator_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setGenerator_Terms("generator", opLambda, aggsLambda);
    }

    public void setGenerator_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "generator");
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

    public void setGenerator_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setGenerator_SignificantTerms("generator", opLambda, null);
    }

    public void setGenerator_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setGenerator_SignificantTerms("generator", opLambda, aggsLambda);
    }

    public void setGenerator_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "generator");
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

    public void setGenerator_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setGenerator_IpRange("generator", opLambda, null);
    }

    public void setGenerator_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setGenerator_IpRange("generator", opLambda, aggsLambda);
    }

    public void setGenerator_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "generator");
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

    public void setGenerator_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setGenerator_Count("generator", opLambda);
    }

    public void setGenerator_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "generator");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGenerator_Cardinality() {
        setGenerator_Cardinality(null);
    }

    public void setGenerator_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setGenerator_Cardinality("generator", opLambda);
    }

    public void setGenerator_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "generator");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGenerator_Missing() {
        setGenerator_Missing(null);
    }

    public void setGenerator_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setGenerator_Missing("generator", opLambda, null);
    }

    public void setGenerator_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setGenerator_Missing("generator", opLambda, aggsLambda);
    }

    public void setGenerator_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "generator");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPath_Terms() {
        setPath_Terms(null);
    }

    public void setPath_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setPath_Terms("path", opLambda, null);
    }

    public void setPath_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setPath_Terms("path", opLambda, aggsLambda);
    }

    public void setPath_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "path");
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

    public void setPath_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setPath_SignificantTerms("path", opLambda, null);
    }

    public void setPath_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setPath_SignificantTerms("path", opLambda, aggsLambda);
    }

    public void setPath_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "path");
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

    public void setPath_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setPath_IpRange("path", opLambda, null);
    }

    public void setPath_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setPath_IpRange("path", opLambda, aggsLambda);
    }

    public void setPath_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "path");
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

    public void setPath_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setPath_Count("path", opLambda);
    }

    public void setPath_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "path");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_Cardinality() {
        setPath_Cardinality(null);
    }

    public void setPath_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setPath_Cardinality("path", opLambda);
    }

    public void setPath_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "path");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPath_Missing() {
        setPath_Missing(null);
    }

    public void setPath_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setPath_Missing("path", opLambda, null);
    }

    public void setPath_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setPath_Missing("path", opLambda, aggsLambda);
    }

    public void setPath_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "path");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
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

    public void setTarget_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setTarget_Terms("target", opLambda, aggsLambda);
    }

    public void setTarget_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "target");
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

    public void setTarget_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setTarget_SignificantTerms("target", opLambda, null);
    }

    public void setTarget_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setTarget_SignificantTerms("target", opLambda, aggsLambda);
    }

    public void setTarget_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "target");
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

    public void setTarget_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setTarget_IpRange("target", opLambda, null);
    }

    public void setTarget_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setTarget_IpRange("target", opLambda, aggsLambda);
    }

    public void setTarget_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "target");
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

    public void setTarget_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setTarget_Missing("target", opLambda, aggsLambda);
    }

    public void setTarget_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setThumbnailId_Terms() {
        setThumbnailId_Terms(null);
    }

    public void setThumbnailId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setThumbnailId_Terms("thumbnail_id", opLambda, null);
    }

    public void setThumbnailId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setThumbnailId_Terms("thumbnail_id", opLambda, aggsLambda);
    }

    public void setThumbnailId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "thumbnail_id");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setThumbnailId_SignificantTerms() {
        setThumbnailId_SignificantTerms(null);
    }

    public void setThumbnailId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setThumbnailId_SignificantTerms("thumbnail_id", opLambda, null);
    }

    public void setThumbnailId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setThumbnailId_SignificantTerms("thumbnail_id", opLambda, aggsLambda);
    }

    public void setThumbnailId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "thumbnail_id");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setThumbnailId_IpRange() {
        setThumbnailId_IpRange(null);
    }

    public void setThumbnailId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setThumbnailId_IpRange("thumbnail_id", opLambda, null);
    }

    public void setThumbnailId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setThumbnailId_IpRange("thumbnail_id", opLambda, aggsLambda);
    }

    public void setThumbnailId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "thumbnail_id");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ThumbnailQueueCA ca = new ThumbnailQueueCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setThumbnailId_Count() {
        setThumbnailId_Count(null);
    }

    public void setThumbnailId_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setThumbnailId_Count("thumbnail_id", opLambda);
    }

    public void setThumbnailId_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "thumbnail_id");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThumbnailId_Cardinality() {
        setThumbnailId_Cardinality(null);
    }

    public void setThumbnailId_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setThumbnailId_Cardinality("thumbnail_id", opLambda);
    }

    public void setThumbnailId_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "thumbnail_id");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThumbnailId_Missing() {
        setThumbnailId_Missing(null);
    }

    public void setThumbnailId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setThumbnailId_Missing("thumbnail_id", opLambda, null);
    }

    public void setThumbnailId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        setThumbnailId_Missing("thumbnail_id", opLambda, aggsLambda);
    }

    public void setThumbnailId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsThumbnailQueueCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "thumbnail_id");
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
