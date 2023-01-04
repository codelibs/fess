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
import org.codelibs.fess.es.config.cbean.ca.CrawlingInfoCA;
import org.codelibs.fess.es.config.cbean.cq.CrawlingInfoCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsCrawlingInfoCQ;
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
public abstract class BsCrawlingInfoCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsCrawlingInfoCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        CrawlingInfoCQ cq = new CrawlingInfoCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoCA ca = new CrawlingInfoCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoCA ca = new CrawlingInfoCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoCA ca = new CrawlingInfoCA();
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
            OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        setCreatedTime_Histogram("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoCA ca = new CrawlingInfoCA();
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

    public void setCreatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        setCreatedTime_Range("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoCA ca = new CrawlingInfoCA();
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

    public void setCreatedTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        setCreatedTime_Missing("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoCA ca = new CrawlingInfoCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setExpiredTime_Avg() {
        setExpiredTime_Avg(null);
    }

    public void setExpiredTime_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setExpiredTime_Avg("expiredTime", opLambda);
    }

    public void setExpiredTime_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "expiredTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_Max() {
        setExpiredTime_Max(null);
    }

    public void setExpiredTime_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setExpiredTime_Max("expiredTime", opLambda);
    }

    public void setExpiredTime_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "expiredTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_Min() {
        setExpiredTime_Min(null);
    }

    public void setExpiredTime_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setExpiredTime_Min("expiredTime", opLambda);
    }

    public void setExpiredTime_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "expiredTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_Sum() {
        setExpiredTime_Sum(null);
    }

    public void setExpiredTime_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setExpiredTime_Sum("expiredTime", opLambda);
    }

    public void setExpiredTime_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "expiredTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_ExtendedStats() {
        setExpiredTime_ExtendedStats(null);
    }

    public void setExpiredTime_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setExpiredTime_ExtendedStats("expiredTime", opLambda);
    }

    public void setExpiredTime_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "expiredTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_Stats() {
        setExpiredTime_Stats(null);
    }

    public void setExpiredTime_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setExpiredTime_Stats("expiredTime", opLambda);
    }

    public void setExpiredTime_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "expiredTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_Percentiles() {
        setExpiredTime_Percentiles(null);
    }

    public void setExpiredTime_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setExpiredTime_Percentiles("expiredTime", opLambda);
    }

    public void setExpiredTime_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "expiredTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_PercentileRanks(double[] values) {
        setExpiredTime_PercentileRanks(values, null);
    }

    public void setExpiredTime_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setExpiredTime_PercentileRanks("expiredTime", values, opLambda);
    }

    public void setExpiredTime_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "expiredTime", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_Histogram() {
        setExpiredTime_Histogram(null);
    }

    public void setExpiredTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setExpiredTime_Histogram("expiredTime", opLambda, null);
    }

    public void setExpiredTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        setExpiredTime_Histogram("expiredTime", opLambda, aggsLambda);
    }

    public void setExpiredTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "expiredTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoCA ca = new CrawlingInfoCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setExpiredTime_Range() {
        setExpiredTime_Range(null);
    }

    public void setExpiredTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setExpiredTime_Range("expiredTime", opLambda, null);
    }

    public void setExpiredTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        setExpiredTime_Range("expiredTime", opLambda, aggsLambda);
    }

    public void setExpiredTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "expiredTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoCA ca = new CrawlingInfoCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setExpiredTime_Count() {
        setExpiredTime_Count(null);
    }

    public void setExpiredTime_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setExpiredTime_Count("expiredTime", opLambda);
    }

    public void setExpiredTime_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "expiredTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_Cardinality() {
        setExpiredTime_Cardinality(null);
    }

    public void setExpiredTime_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setExpiredTime_Cardinality("expiredTime", opLambda);
    }

    public void setExpiredTime_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "expiredTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setExpiredTime_Missing() {
        setExpiredTime_Missing(null);
    }

    public void setExpiredTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setExpiredTime_Missing("expiredTime", opLambda, null);
    }

    public void setExpiredTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        setExpiredTime_Missing("expiredTime", opLambda, aggsLambda);
    }

    public void setExpiredTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "expiredTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoCA ca = new CrawlingInfoCA();
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

    public void setName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        setName_Terms("name", opLambda, aggsLambda);
    }

    public void setName_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoCA ca = new CrawlingInfoCA();
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
            OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        setName_SignificantTerms("name", opLambda, aggsLambda);
    }

    public void setName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoCA ca = new CrawlingInfoCA();
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

    public void setName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        setName_IpRange("name", opLambda, aggsLambda);
    }

    public void setName_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoCA ca = new CrawlingInfoCA();
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

    public void setName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        setName_Missing("name", opLambda, aggsLambda);
    }

    public void setName_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoCA ca = new CrawlingInfoCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSessionId_Terms() {
        setSessionId_Terms(null);
    }

    public void setSessionId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setSessionId_Terms("sessionId", opLambda, null);
    }

    public void setSessionId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        setSessionId_Terms("sessionId", opLambda, aggsLambda);
    }

    public void setSessionId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "sessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoCA ca = new CrawlingInfoCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSessionId_SignificantTerms() {
        setSessionId_SignificantTerms(null);
    }

    public void setSessionId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setSessionId_SignificantTerms("sessionId", opLambda, null);
    }

    public void setSessionId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        setSessionId_SignificantTerms("sessionId", opLambda, aggsLambda);
    }

    public void setSessionId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "sessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoCA ca = new CrawlingInfoCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSessionId_IpRange() {
        setSessionId_IpRange(null);
    }

    public void setSessionId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setSessionId_IpRange("sessionId", opLambda, null);
    }

    public void setSessionId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        setSessionId_IpRange("sessionId", opLambda, aggsLambda);
    }

    public void setSessionId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "sessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoCA ca = new CrawlingInfoCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSessionId_Count() {
        setSessionId_Count(null);
    }

    public void setSessionId_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setSessionId_Count("sessionId", opLambda);
    }

    public void setSessionId_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "sessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_Cardinality() {
        setSessionId_Cardinality(null);
    }

    public void setSessionId_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setSessionId_Cardinality("sessionId", opLambda);
    }

    public void setSessionId_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "sessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSessionId_Missing() {
        setSessionId_Missing(null);
    }

    public void setSessionId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setSessionId_Missing("sessionId", opLambda, null);
    }

    public void setSessionId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        setSessionId_Missing("sessionId", opLambda, aggsLambda);
    }

    public void setSessionId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "sessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoCA ca = new CrawlingInfoCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
