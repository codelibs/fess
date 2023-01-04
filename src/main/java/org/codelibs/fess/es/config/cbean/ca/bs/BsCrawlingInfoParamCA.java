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
import org.codelibs.fess.es.config.cbean.ca.CrawlingInfoParamCA;
import org.codelibs.fess.es.config.cbean.cq.CrawlingInfoParamCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsCrawlingInfoParamCQ;
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
public abstract class BsCrawlingInfoParamCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsCrawlingInfoParamCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        CrawlingInfoParamCQ cq = new CrawlingInfoParamCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
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

    public void setCrawlingInfoId_Terms() {
        setCrawlingInfoId_Terms(null);
    }

    public void setCrawlingInfoId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setCrawlingInfoId_Terms("crawlingInfoId", opLambda, null);
    }

    public void setCrawlingInfoId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setCrawlingInfoId_Terms("crawlingInfoId", opLambda, aggsLambda);
    }

    public void setCrawlingInfoId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "crawlingInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCrawlingInfoId_SignificantTerms() {
        setCrawlingInfoId_SignificantTerms(null);
    }

    public void setCrawlingInfoId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setCrawlingInfoId_SignificantTerms("crawlingInfoId", opLambda, null);
    }

    public void setCrawlingInfoId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setCrawlingInfoId_SignificantTerms("crawlingInfoId", opLambda, aggsLambda);
    }

    public void setCrawlingInfoId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "crawlingInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCrawlingInfoId_IpRange() {
        setCrawlingInfoId_IpRange(null);
    }

    public void setCrawlingInfoId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setCrawlingInfoId_IpRange("crawlingInfoId", opLambda, null);
    }

    public void setCrawlingInfoId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setCrawlingInfoId_IpRange("crawlingInfoId", opLambda, aggsLambda);
    }

    public void setCrawlingInfoId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "crawlingInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCrawlingInfoId_Count() {
        setCrawlingInfoId_Count(null);
    }

    public void setCrawlingInfoId_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setCrawlingInfoId_Count("crawlingInfoId", opLambda);
    }

    public void setCrawlingInfoId_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "crawlingInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingInfoId_Cardinality() {
        setCrawlingInfoId_Cardinality(null);
    }

    public void setCrawlingInfoId_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setCrawlingInfoId_Cardinality("crawlingInfoId", opLambda);
    }

    public void setCrawlingInfoId_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "crawlingInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingInfoId_Missing() {
        setCrawlingInfoId_Missing(null);
    }

    public void setCrawlingInfoId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setCrawlingInfoId_Missing("crawlingInfoId", opLambda, null);
    }

    public void setCrawlingInfoId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setCrawlingInfoId_Missing("crawlingInfoId", opLambda, aggsLambda);
    }

    public void setCrawlingInfoId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "crawlingInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
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
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setCreatedTime_Histogram("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
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

    public void setCreatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setCreatedTime_Range("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
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
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setCreatedTime_Missing("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setKey_Terms() {
        setKey_Terms(null);
    }

    public void setKey_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setKey_Terms("key", opLambda, null);
    }

    public void setKey_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setKey_Terms("key", opLambda, aggsLambda);
    }

    public void setKey_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "key");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setKey_SignificantTerms() {
        setKey_SignificantTerms(null);
    }

    public void setKey_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setKey_SignificantTerms("key", opLambda, null);
    }

    public void setKey_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setKey_SignificantTerms("key", opLambda, aggsLambda);
    }

    public void setKey_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "key");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setKey_IpRange() {
        setKey_IpRange(null);
    }

    public void setKey_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setKey_IpRange("key", opLambda, null);
    }

    public void setKey_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setKey_IpRange("key", opLambda, aggsLambda);
    }

    public void setKey_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "key");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setKey_Count() {
        setKey_Count(null);
    }

    public void setKey_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setKey_Count("key", opLambda);
    }

    public void setKey_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "key");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_Cardinality() {
        setKey_Cardinality(null);
    }

    public void setKey_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setKey_Cardinality("key", opLambda);
    }

    public void setKey_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "key");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_Missing() {
        setKey_Missing(null);
    }

    public void setKey_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setKey_Missing("key", opLambda, null);
    }

    public void setKey_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setKey_Missing("key", opLambda, aggsLambda);
    }

    public void setKey_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "key");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setValue_Terms() {
        setValue_Terms(null);
    }

    public void setValue_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setValue_Terms("value", opLambda, null);
    }

    public void setValue_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setValue_Terms("value", opLambda, aggsLambda);
    }

    public void setValue_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "value");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setValue_SignificantTerms() {
        setValue_SignificantTerms(null);
    }

    public void setValue_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setValue_SignificantTerms("value", opLambda, null);
    }

    public void setValue_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setValue_SignificantTerms("value", opLambda, aggsLambda);
    }

    public void setValue_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "value");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setValue_IpRange() {
        setValue_IpRange(null);
    }

    public void setValue_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setValue_IpRange("value", opLambda, null);
    }

    public void setValue_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setValue_IpRange("value", opLambda, aggsLambda);
    }

    public void setValue_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "value");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setValue_Count() {
        setValue_Count(null);
    }

    public void setValue_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setValue_Count("value", opLambda);
    }

    public void setValue_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "value");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_Cardinality() {
        setValue_Cardinality(null);
    }

    public void setValue_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setValue_Cardinality("value", opLambda);
    }

    public void setValue_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "value");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_Missing() {
        setValue_Missing(null);
    }

    public void setValue_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setValue_Missing("value", opLambda, null);
    }

    public void setValue_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setValue_Missing("value", opLambda, aggsLambda);
    }

    public void setValue_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "value");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
