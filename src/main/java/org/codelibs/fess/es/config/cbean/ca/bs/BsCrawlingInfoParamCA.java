/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

    public void global(String name, ConditionOptionCall<GlobalBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        GlobalBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
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

    // String crawlingInfoId

    public void setCrawlingInfoId_Terms() {
        setCrawlingInfoId_Terms(null);
    }

    public void setCrawlingInfoId_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setCrawlingInfoId_Terms("crawlingInfoId", opLambda, null);
    }

    public void setCrawlingInfoId_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setCrawlingInfoId_Terms("crawlingInfoId", opLambda, aggsLambda);
    }

    public void setCrawlingInfoId_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "crawlingInfoId");
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

    public void setCrawlingInfoId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setCrawlingInfoId_SignificantTerms("crawlingInfoId", opLambda, null);
    }

    public void setCrawlingInfoId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setCrawlingInfoId_SignificantTerms("crawlingInfoId", opLambda, aggsLambda);
    }

    public void setCrawlingInfoId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "crawlingInfoId");
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

    public void setCrawlingInfoId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setCrawlingInfoId_IpRange("crawlingInfoId", opLambda, null);
    }

    public void setCrawlingInfoId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setCrawlingInfoId_IpRange("crawlingInfoId", opLambda, aggsLambda);
    }

    public void setCrawlingInfoId_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "crawlingInfoId");
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

    public void setCrawlingInfoId_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setCrawlingInfoId_Count("crawlingInfoId", opLambda);
    }

    public void setCrawlingInfoId_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "crawlingInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingInfoId_Cardinality() {
        setCrawlingInfoId_Cardinality(null);
    }

    public void setCrawlingInfoId_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setCrawlingInfoId_Cardinality("crawlingInfoId", opLambda);
    }

    public void setCrawlingInfoId_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "crawlingInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCrawlingInfoId_Missing() {
        setCrawlingInfoId_Missing(null);
    }

    public void setCrawlingInfoId_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setCrawlingInfoId_Missing("crawlingInfoId", opLambda, null);
    }

    public void setCrawlingInfoId_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setCrawlingInfoId_Missing("crawlingInfoId", opLambda, aggsLambda);
    }

    public void setCrawlingInfoId_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "crawlingInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
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

    public void setCreatedTime_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setCreatedTime_Histogram("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "createdTime");
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

    public void setCreatedTime_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setCreatedTime_Range("createdTime", opLambda, null);
    }

    public void setCreatedTime_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setCreatedTime_Range("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "createdTime");
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

    public void setCreatedTime_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setCreatedTime_Missing("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String key

    public void setKey_Terms() {
        setKey_Terms(null);
    }

    public void setKey_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setKey_Terms("key", opLambda, null);
    }

    public void setKey_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setKey_Terms("key", opLambda, aggsLambda);
    }

    public void setKey_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "key");
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

    public void setKey_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setKey_SignificantTerms("key", opLambda, null);
    }

    public void setKey_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setKey_SignificantTerms("key", opLambda, aggsLambda);
    }

    public void setKey_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "key");
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

    public void setKey_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setKey_IpRange("key", opLambda, null);
    }

    public void setKey_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setKey_IpRange("key", opLambda, aggsLambda);
    }

    public void setKey_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "key");
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

    public void setKey_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setKey_Count("key", opLambda);
    }

    public void setKey_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "key");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_Cardinality() {
        setKey_Cardinality(null);
    }

    public void setKey_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setKey_Cardinality("key", opLambda);
    }

    public void setKey_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "key");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setKey_Missing() {
        setKey_Missing(null);
    }

    public void setKey_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setKey_Missing("key", opLambda, null);
    }

    public void setKey_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setKey_Missing("key", opLambda, aggsLambda);
    }

    public void setKey_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "key");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            CrawlingInfoParamCA ca = new CrawlingInfoParamCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String value

    public void setValue_Terms() {
        setValue_Terms(null);
    }

    public void setValue_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setValue_Terms("value", opLambda, null);
    }

    public void setValue_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setValue_Terms("value", opLambda, aggsLambda);
    }

    public void setValue_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "value");
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

    public void setValue_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setValue_SignificantTerms("value", opLambda, null);
    }

    public void setValue_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setValue_SignificantTerms("value", opLambda, aggsLambda);
    }

    public void setValue_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "value");
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

    public void setValue_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setValue_IpRange("value", opLambda, null);
    }

    public void setValue_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setValue_IpRange("value", opLambda, aggsLambda);
    }

    public void setValue_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "value");
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

    public void setValue_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setValue_Count("value", opLambda);
    }

    public void setValue_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "value");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_Cardinality() {
        setValue_Cardinality(null);
    }

    public void setValue_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setValue_Cardinality("value", opLambda);
    }

    public void setValue_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "value");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_Missing() {
        setValue_Missing(null);
    }

    public void setValue_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setValue_Missing("value", opLambda, null);
    }

    public void setValue_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        setValue_Missing("value", opLambda, aggsLambda);
    }

    public void setValue_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsCrawlingInfoParamCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "value");
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
