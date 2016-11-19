/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
import org.codelibs.fess.es.config.cbean.ca.FileAuthenticationCA;
import org.codelibs.fess.es.config.cbean.cq.FileAuthenticationCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsFileAuthenticationCQ;
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
public abstract class BsFileAuthenticationCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsFileAuthenticationCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        FileAuthenticationCQ cq = new FileAuthenticationCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        GlobalBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
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

    // String createdBy

    public void setCreatedBy_Terms() {
        setCreatedBy_Terms(null);
    }

    public void setCreatedBy_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setCreatedBy_Terms("createdBy", opLambda, null);
    }

    public void setCreatedBy_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setCreatedBy_Terms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCreatedBy_SignificantTerms() {
        setCreatedBy_SignificantTerms(null);
    }

    public void setCreatedBy_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setCreatedBy_SignificantTerms("createdBy", opLambda, null);
    }

    public void setCreatedBy_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setCreatedBy_SignificantTerms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCreatedBy_IpRange() {
        setCreatedBy_IpRange(null);
    }

    public void setCreatedBy_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setCreatedBy_IpRange("createdBy", opLambda, null);
    }

    public void setCreatedBy_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setCreatedBy_IpRange("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCreatedBy_Count() {
        setCreatedBy_Count(null);
    }

    public void setCreatedBy_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setCreatedBy_Count("createdBy", opLambda);
    }

    public void setCreatedBy_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Cardinality() {
        setCreatedBy_Cardinality(null);
    }

    public void setCreatedBy_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setCreatedBy_Cardinality("createdBy", opLambda);
    }

    public void setCreatedBy_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedBy_Missing() {
        setCreatedBy_Missing(null);
    }

    public void setCreatedBy_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setCreatedBy_Missing("createdBy", opLambda, null);
    }

    public void setCreatedBy_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setCreatedBy_Missing("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
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

    public void setCreatedTime_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setCreatedTime_Histogram("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
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

    public void setCreatedTime_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setCreatedTime_Range("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Range(String name, ConditionOptionCall<RangeBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
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

    public void setCreatedTime_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setCreatedTime_Missing("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String fileConfigId

    public void setFileConfigId_Terms() {
        setFileConfigId_Terms(null);
    }

    public void setFileConfigId_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setFileConfigId_Terms("fileConfigId", opLambda, null);
    }

    public void setFileConfigId_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setFileConfigId_Terms("fileConfigId", opLambda, aggsLambda);
    }

    public void setFileConfigId_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "fileConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setFileConfigId_SignificantTerms() {
        setFileConfigId_SignificantTerms(null);
    }

    public void setFileConfigId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setFileConfigId_SignificantTerms("fileConfigId", opLambda, null);
    }

    public void setFileConfigId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setFileConfigId_SignificantTerms("fileConfigId", opLambda, aggsLambda);
    }

    public void setFileConfigId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "fileConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setFileConfigId_IpRange() {
        setFileConfigId_IpRange(null);
    }

    public void setFileConfigId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setFileConfigId_IpRange("fileConfigId", opLambda, null);
    }

    public void setFileConfigId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setFileConfigId_IpRange("fileConfigId", opLambda, aggsLambda);
    }

    public void setFileConfigId_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "fileConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setFileConfigId_Count() {
        setFileConfigId_Count(null);
    }

    public void setFileConfigId_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setFileConfigId_Count("fileConfigId", opLambda);
    }

    public void setFileConfigId_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "fileConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_Cardinality() {
        setFileConfigId_Cardinality(null);
    }

    public void setFileConfigId_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setFileConfigId_Cardinality("fileConfigId", opLambda);
    }

    public void setFileConfigId_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "fileConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_Missing() {
        setFileConfigId_Missing(null);
    }

    public void setFileConfigId_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setFileConfigId_Missing("fileConfigId", opLambda, null);
    }

    public void setFileConfigId_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setFileConfigId_Missing("fileConfigId", opLambda, aggsLambda);
    }

    public void setFileConfigId_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "fileConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String hostname

    public void setHostname_Terms() {
        setHostname_Terms(null);
    }

    public void setHostname_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setHostname_Terms("hostname", opLambda, null);
    }

    public void setHostname_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setHostname_Terms("hostname", opLambda, aggsLambda);
    }

    public void setHostname_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "hostname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHostname_SignificantTerms() {
        setHostname_SignificantTerms(null);
    }

    public void setHostname_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setHostname_SignificantTerms("hostname", opLambda, null);
    }

    public void setHostname_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setHostname_SignificantTerms("hostname", opLambda, aggsLambda);
    }

    public void setHostname_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "hostname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHostname_IpRange() {
        setHostname_IpRange(null);
    }

    public void setHostname_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setHostname_IpRange("hostname", opLambda, null);
    }

    public void setHostname_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setHostname_IpRange("hostname", opLambda, aggsLambda);
    }

    public void setHostname_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "hostname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHostname_Count() {
        setHostname_Count(null);
    }

    public void setHostname_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setHostname_Count("hostname", opLambda);
    }

    public void setHostname_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "hostname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_Cardinality() {
        setHostname_Cardinality(null);
    }

    public void setHostname_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setHostname_Cardinality("hostname", opLambda);
    }

    public void setHostname_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "hostname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_Missing() {
        setHostname_Missing(null);
    }

    public void setHostname_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setHostname_Missing("hostname", opLambda, null);
    }

    public void setHostname_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setHostname_Missing("hostname", opLambda, aggsLambda);
    }

    public void setHostname_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "hostname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String parameters

    public void setParameters_Terms() {
        setParameters_Terms(null);
    }

    public void setParameters_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setParameters_Terms("parameters", opLambda, null);
    }

    public void setParameters_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setParameters_Terms("parameters", opLambda, aggsLambda);
    }

    public void setParameters_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "parameters");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setParameters_SignificantTerms() {
        setParameters_SignificantTerms(null);
    }

    public void setParameters_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setParameters_SignificantTerms("parameters", opLambda, null);
    }

    public void setParameters_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setParameters_SignificantTerms("parameters", opLambda, aggsLambda);
    }

    public void setParameters_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "parameters");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setParameters_IpRange() {
        setParameters_IpRange(null);
    }

    public void setParameters_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setParameters_IpRange("parameters", opLambda, null);
    }

    public void setParameters_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setParameters_IpRange("parameters", opLambda, aggsLambda);
    }

    public void setParameters_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "parameters");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setParameters_Count() {
        setParameters_Count(null);
    }

    public void setParameters_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setParameters_Count("parameters", opLambda);
    }

    public void setParameters_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "parameters");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_Cardinality() {
        setParameters_Cardinality(null);
    }

    public void setParameters_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setParameters_Cardinality("parameters", opLambda);
    }

    public void setParameters_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "parameters");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_Missing() {
        setParameters_Missing(null);
    }

    public void setParameters_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setParameters_Missing("parameters", opLambda, null);
    }

    public void setParameters_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setParameters_Missing("parameters", opLambda, aggsLambda);
    }

    public void setParameters_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "parameters");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String password

    public void setPassword_Terms() {
        setPassword_Terms(null);
    }

    public void setPassword_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setPassword_Terms("password", opLambda, null);
    }

    public void setPassword_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setPassword_Terms("password", opLambda, aggsLambda);
    }

    public void setPassword_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPassword_SignificantTerms() {
        setPassword_SignificantTerms(null);
    }

    public void setPassword_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setPassword_SignificantTerms("password", opLambda, null);
    }

    public void setPassword_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setPassword_SignificantTerms("password", opLambda, aggsLambda);
    }

    public void setPassword_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPassword_IpRange() {
        setPassword_IpRange(null);
    }

    public void setPassword_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setPassword_IpRange("password", opLambda, null);
    }

    public void setPassword_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setPassword_IpRange("password", opLambda, aggsLambda);
    }

    public void setPassword_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPassword_Count() {
        setPassword_Count(null);
    }

    public void setPassword_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setPassword_Count("password", opLambda);
    }

    public void setPassword_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Cardinality() {
        setPassword_Cardinality(null);
    }

    public void setPassword_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setPassword_Cardinality("password", opLambda);
    }

    public void setPassword_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Missing() {
        setPassword_Missing(null);
    }

    public void setPassword_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setPassword_Missing("password", opLambda, null);
    }

    public void setPassword_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setPassword_Missing("password", opLambda, aggsLambda);
    }

    public void setPassword_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Integer port
    public void setPort_Avg() {
        setPort_Avg(null);
    }

    public void setPort_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setPort_Avg("port", opLambda);
    }

    public void setPort_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Max() {
        setPort_Max(null);
    }

    public void setPort_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setPort_Max("port", opLambda);
    }

    public void setPort_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Min() {
        setPort_Min(null);
    }

    public void setPort_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setPort_Min("port", opLambda);
    }

    public void setPort_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Sum() {
        setPort_Sum(null);
    }

    public void setPort_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setPort_Sum("port", opLambda);
    }

    public void setPort_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_ExtendedStats() {
        setPort_ExtendedStats(null);
    }

    public void setPort_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setPort_ExtendedStats("port", opLambda);
    }

    public void setPort_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Stats() {
        setPort_Stats(null);
    }

    public void setPort_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setPort_Stats("port", opLambda);
    }

    public void setPort_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Percentiles() {
        setPort_Percentiles(null);
    }

    public void setPort_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setPort_Percentiles("port", opLambda);
    }

    public void setPort_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_PercentileRanks() {
        setPort_PercentileRanks(null);
    }

    public void setPort_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setPort_PercentileRanks("port", opLambda);
    }

    public void setPort_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Histogram() {
        setPort_Histogram(null);
    }

    public void setPort_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setPort_Histogram("port", opLambda, null);
    }

    public void setPort_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setPort_Histogram("port", opLambda, aggsLambda);
    }

    public void setPort_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPort_Range() {
        setPort_Range(null);
    }

    public void setPort_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setPort_Range("port", opLambda, null);
    }

    public void setPort_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setPort_Range("port", opLambda, aggsLambda);
    }

    public void setPort_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPort_Count() {
        setPort_Count(null);
    }

    public void setPort_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setPort_Count("port", opLambda);
    }

    public void setPort_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Cardinality() {
        setPort_Cardinality(null);
    }

    public void setPort_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setPort_Cardinality("port", opLambda);
    }

    public void setPort_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Missing() {
        setPort_Missing(null);
    }

    public void setPort_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setPort_Missing("port", opLambda, null);
    }

    public void setPort_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setPort_Missing("port", opLambda, aggsLambda);
    }

    public void setPort_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String protocolScheme

    public void setProtocolScheme_Terms() {
        setProtocolScheme_Terms(null);
    }

    public void setProtocolScheme_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setProtocolScheme_Terms("protocolScheme", opLambda, null);
    }

    public void setProtocolScheme_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setProtocolScheme_Terms("protocolScheme", opLambda, aggsLambda);
    }

    public void setProtocolScheme_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "protocolScheme");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setProtocolScheme_SignificantTerms() {
        setProtocolScheme_SignificantTerms(null);
    }

    public void setProtocolScheme_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setProtocolScheme_SignificantTerms("protocolScheme", opLambda, null);
    }

    public void setProtocolScheme_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setProtocolScheme_SignificantTerms("protocolScheme", opLambda, aggsLambda);
    }

    public void setProtocolScheme_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "protocolScheme");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setProtocolScheme_IpRange() {
        setProtocolScheme_IpRange(null);
    }

    public void setProtocolScheme_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setProtocolScheme_IpRange("protocolScheme", opLambda, null);
    }

    public void setProtocolScheme_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setProtocolScheme_IpRange("protocolScheme", opLambda, aggsLambda);
    }

    public void setProtocolScheme_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "protocolScheme");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setProtocolScheme_Count() {
        setProtocolScheme_Count(null);
    }

    public void setProtocolScheme_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setProtocolScheme_Count("protocolScheme", opLambda);
    }

    public void setProtocolScheme_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "protocolScheme");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_Cardinality() {
        setProtocolScheme_Cardinality(null);
    }

    public void setProtocolScheme_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setProtocolScheme_Cardinality("protocolScheme", opLambda);
    }

    public void setProtocolScheme_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "protocolScheme");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_Missing() {
        setProtocolScheme_Missing(null);
    }

    public void setProtocolScheme_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setProtocolScheme_Missing("protocolScheme", opLambda, null);
    }

    public void setProtocolScheme_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setProtocolScheme_Missing("protocolScheme", opLambda, aggsLambda);
    }

    public void setProtocolScheme_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "protocolScheme");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String updatedBy

    public void setUpdatedBy_Terms() {
        setUpdatedBy_Terms(null);
    }

    public void setUpdatedBy_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setUpdatedBy_Terms("updatedBy", opLambda, null);
    }

    public void setUpdatedBy_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUpdatedBy_Terms("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUpdatedBy_SignificantTerms() {
        setUpdatedBy_SignificantTerms(null);
    }

    public void setUpdatedBy_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setUpdatedBy_SignificantTerms("updatedBy", opLambda, null);
    }

    public void setUpdatedBy_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUpdatedBy_SignificantTerms("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUpdatedBy_IpRange() {
        setUpdatedBy_IpRange(null);
    }

    public void setUpdatedBy_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setUpdatedBy_IpRange("updatedBy", opLambda, null);
    }

    public void setUpdatedBy_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUpdatedBy_IpRange("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUpdatedBy_Count() {
        setUpdatedBy_Count(null);
    }

    public void setUpdatedBy_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setUpdatedBy_Count("updatedBy", opLambda);
    }

    public void setUpdatedBy_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Cardinality() {
        setUpdatedBy_Cardinality(null);
    }

    public void setUpdatedBy_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setUpdatedBy_Cardinality("updatedBy", opLambda);
    }

    public void setUpdatedBy_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedBy_Missing() {
        setUpdatedBy_Missing(null);
    }

    public void setUpdatedBy_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setUpdatedBy_Missing("updatedBy", opLambda, null);
    }

    public void setUpdatedBy_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUpdatedBy_Missing("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Long updatedTime
    public void setUpdatedTime_Avg() {
        setUpdatedTime_Avg(null);
    }

    public void setUpdatedTime_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setUpdatedTime_Avg("updatedTime", opLambda);
    }

    public void setUpdatedTime_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Max() {
        setUpdatedTime_Max(null);
    }

    public void setUpdatedTime_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setUpdatedTime_Max("updatedTime", opLambda);
    }

    public void setUpdatedTime_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Min() {
        setUpdatedTime_Min(null);
    }

    public void setUpdatedTime_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setUpdatedTime_Min("updatedTime", opLambda);
    }

    public void setUpdatedTime_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Sum() {
        setUpdatedTime_Sum(null);
    }

    public void setUpdatedTime_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setUpdatedTime_Sum("updatedTime", opLambda);
    }

    public void setUpdatedTime_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_ExtendedStats() {
        setUpdatedTime_ExtendedStats(null);
    }

    public void setUpdatedTime_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setUpdatedTime_ExtendedStats("updatedTime", opLambda);
    }

    public void setUpdatedTime_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Stats() {
        setUpdatedTime_Stats(null);
    }

    public void setUpdatedTime_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setUpdatedTime_Stats("updatedTime", opLambda);
    }

    public void setUpdatedTime_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Percentiles() {
        setUpdatedTime_Percentiles(null);
    }

    public void setUpdatedTime_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setUpdatedTime_Percentiles("updatedTime", opLambda);
    }

    public void setUpdatedTime_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_PercentileRanks() {
        setUpdatedTime_PercentileRanks(null);
    }

    public void setUpdatedTime_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setUpdatedTime_PercentileRanks("updatedTime", opLambda);
    }

    public void setUpdatedTime_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Histogram() {
        setUpdatedTime_Histogram(null);
    }

    public void setUpdatedTime_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setUpdatedTime_Histogram("updatedTime", opLambda, null);
    }

    public void setUpdatedTime_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUpdatedTime_Histogram("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUpdatedTime_Range() {
        setUpdatedTime_Range(null);
    }

    public void setUpdatedTime_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setUpdatedTime_Range("updatedTime", opLambda, null);
    }

    public void setUpdatedTime_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUpdatedTime_Range("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Range(String name, ConditionOptionCall<RangeBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUpdatedTime_Count() {
        setUpdatedTime_Count(null);
    }

    public void setUpdatedTime_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setUpdatedTime_Count("updatedTime", opLambda);
    }

    public void setUpdatedTime_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Cardinality() {
        setUpdatedTime_Cardinality(null);
    }

    public void setUpdatedTime_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setUpdatedTime_Cardinality("updatedTime", opLambda);
    }

    public void setUpdatedTime_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedTime_Missing() {
        setUpdatedTime_Missing(null);
    }

    public void setUpdatedTime_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setUpdatedTime_Missing("updatedTime", opLambda, null);
    }

    public void setUpdatedTime_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUpdatedTime_Missing("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String username

    public void setUsername_Terms() {
        setUsername_Terms(null);
    }

    public void setUsername_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setUsername_Terms("username", opLambda, null);
    }

    public void setUsername_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUsername_Terms("username", opLambda, aggsLambda);
    }

    public void setUsername_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "username");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUsername_SignificantTerms() {
        setUsername_SignificantTerms(null);
    }

    public void setUsername_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setUsername_SignificantTerms("username", opLambda, null);
    }

    public void setUsername_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUsername_SignificantTerms("username", opLambda, aggsLambda);
    }

    public void setUsername_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "username");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUsername_IpRange() {
        setUsername_IpRange(null);
    }

    public void setUsername_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setUsername_IpRange("username", opLambda, null);
    }

    public void setUsername_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUsername_IpRange("username", opLambda, aggsLambda);
    }

    public void setUsername_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "username");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUsername_Count() {
        setUsername_Count(null);
    }

    public void setUsername_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setUsername_Count("username", opLambda);
    }

    public void setUsername_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "username");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_Cardinality() {
        setUsername_Cardinality(null);
    }

    public void setUsername_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setUsername_Cardinality("username", opLambda);
    }

    public void setUsername_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "username");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_Missing() {
        setUsername_Missing(null);
    }

    public void setUsername_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setUsername_Missing("username", opLambda, null);
    }

    public void setUsername_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUsername_Missing("username", opLambda, aggsLambda);
    }

    public void setUsername_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "username");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
