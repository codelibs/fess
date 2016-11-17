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
import org.codelibs.fess.es.config.cbean.ca.JobLogCA;
import org.codelibs.fess.es.config.cbean.cq.JobLogCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsJobLogCQ;
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
public abstract class BsJobLogCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsJobLogCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        JobLogCQ cq = new JobLogCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        GlobalBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
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

    // Long endTime
    public void setEndTime_Avg() {
        setEndTime_Avg(null);
    }

    public void setEndTime_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setEndTime_Avg("endTime", opLambda);
    }

    public void setEndTime_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Max() {
        setEndTime_Max(null);
    }

    public void setEndTime_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setEndTime_Max("endTime", opLambda);
    }

    public void setEndTime_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Min() {
        setEndTime_Min(null);
    }

    public void setEndTime_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setEndTime_Min("endTime", opLambda);
    }

    public void setEndTime_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Sum() {
        setEndTime_Sum(null);
    }

    public void setEndTime_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setEndTime_Sum("endTime", opLambda);
    }

    public void setEndTime_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_ExtendedStats() {
        setEndTime_ExtendedStats(null);
    }

    public void setEndTime_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setEndTime_ExtendedStats("endTime", opLambda);
    }

    public void setEndTime_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Stats() {
        setEndTime_Stats(null);
    }

    public void setEndTime_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setEndTime_Stats("endTime", opLambda);
    }

    public void setEndTime_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Percentiles() {
        setEndTime_Percentiles(null);
    }

    public void setEndTime_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setEndTime_Percentiles("endTime", opLambda);
    }

    public void setEndTime_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_PercentileRanks() {
        setEndTime_PercentileRanks(null);
    }

    public void setEndTime_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setEndTime_PercentileRanks("endTime", opLambda);
    }

    public void setEndTime_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Histogram() {
        setEndTime_Histogram(null);
    }

    public void setEndTime_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setEndTime_Histogram("endTime", opLambda, null);
    }

    public void setEndTime_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setEndTime_Histogram("endTime", opLambda, aggsLambda);
    }

    public void setEndTime_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setEndTime_Range() {
        setEndTime_Range(null);
    }

    public void setEndTime_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setEndTime_Range("endTime", opLambda, null);
    }

    public void setEndTime_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setEndTime_Range("endTime", opLambda, aggsLambda);
    }

    public void setEndTime_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setEndTime_Count() {
        setEndTime_Count(null);
    }

    public void setEndTime_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setEndTime_Count("endTime", opLambda);
    }

    public void setEndTime_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Cardinality() {
        setEndTime_Cardinality(null);
    }

    public void setEndTime_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setEndTime_Cardinality("endTime", opLambda);
    }

    public void setEndTime_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Missing() {
        setEndTime_Missing(null);
    }

    public void setEndTime_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setEndTime_Missing("endTime", opLambda, null);
    }

    public void setEndTime_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setEndTime_Missing("endTime", opLambda, aggsLambda);
    }

    public void setEndTime_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String jobName

    public void setJobName_Terms() {
        setJobName_Terms(null);
    }

    public void setJobName_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setJobName_Terms("jobName", opLambda, null);
    }

    public void setJobName_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setJobName_Terms("jobName", opLambda, aggsLambda);
    }

    public void setJobName_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "jobName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setJobName_SignificantTerms() {
        setJobName_SignificantTerms(null);
    }

    public void setJobName_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setJobName_SignificantTerms("jobName", opLambda, null);
    }

    public void setJobName_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setJobName_SignificantTerms("jobName", opLambda, aggsLambda);
    }

    public void setJobName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "jobName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setJobName_IpRange() {
        setJobName_IpRange(null);
    }

    public void setJobName_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setJobName_IpRange("jobName", opLambda, null);
    }

    public void setJobName_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setJobName_IpRange("jobName", opLambda, aggsLambda);
    }

    public void setJobName_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "jobName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setJobName_Count() {
        setJobName_Count(null);
    }

    public void setJobName_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setJobName_Count("jobName", opLambda);
    }

    public void setJobName_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "jobName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_Cardinality() {
        setJobName_Cardinality(null);
    }

    public void setJobName_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setJobName_Cardinality("jobName", opLambda);
    }

    public void setJobName_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "jobName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_Missing() {
        setJobName_Missing(null);
    }

    public void setJobName_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setJobName_Missing("jobName", opLambda, null);
    }

    public void setJobName_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setJobName_Missing("jobName", opLambda, aggsLambda);
    }

    public void setJobName_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "jobName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String jobStatus

    public void setJobStatus_Terms() {
        setJobStatus_Terms(null);
    }

    public void setJobStatus_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setJobStatus_Terms("jobStatus", opLambda, null);
    }

    public void setJobStatus_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setJobStatus_Terms("jobStatus", opLambda, aggsLambda);
    }

    public void setJobStatus_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "jobStatus");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setJobStatus_SignificantTerms() {
        setJobStatus_SignificantTerms(null);
    }

    public void setJobStatus_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setJobStatus_SignificantTerms("jobStatus", opLambda, null);
    }

    public void setJobStatus_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setJobStatus_SignificantTerms("jobStatus", opLambda, aggsLambda);
    }

    public void setJobStatus_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "jobStatus");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setJobStatus_IpRange() {
        setJobStatus_IpRange(null);
    }

    public void setJobStatus_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setJobStatus_IpRange("jobStatus", opLambda, null);
    }

    public void setJobStatus_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setJobStatus_IpRange("jobStatus", opLambda, aggsLambda);
    }

    public void setJobStatus_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "jobStatus");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setJobStatus_Count() {
        setJobStatus_Count(null);
    }

    public void setJobStatus_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setJobStatus_Count("jobStatus", opLambda);
    }

    public void setJobStatus_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "jobStatus");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_Cardinality() {
        setJobStatus_Cardinality(null);
    }

    public void setJobStatus_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setJobStatus_Cardinality("jobStatus", opLambda);
    }

    public void setJobStatus_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "jobStatus");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_Missing() {
        setJobStatus_Missing(null);
    }

    public void setJobStatus_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setJobStatus_Missing("jobStatus", opLambda, null);
    }

    public void setJobStatus_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setJobStatus_Missing("jobStatus", opLambda, aggsLambda);
    }

    public void setJobStatus_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "jobStatus");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String scriptData

    public void setScriptData_Terms() {
        setScriptData_Terms(null);
    }

    public void setScriptData_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setScriptData_Terms("scriptData", opLambda, null);
    }

    public void setScriptData_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptData_Terms("scriptData", opLambda, aggsLambda);
    }

    public void setScriptData_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "scriptData");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setScriptData_SignificantTerms() {
        setScriptData_SignificantTerms(null);
    }

    public void setScriptData_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setScriptData_SignificantTerms("scriptData", opLambda, null);
    }

    public void setScriptData_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptData_SignificantTerms("scriptData", opLambda, aggsLambda);
    }

    public void setScriptData_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "scriptData");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setScriptData_IpRange() {
        setScriptData_IpRange(null);
    }

    public void setScriptData_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setScriptData_IpRange("scriptData", opLambda, null);
    }

    public void setScriptData_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptData_IpRange("scriptData", opLambda, aggsLambda);
    }

    public void setScriptData_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "scriptData");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setScriptData_Count() {
        setScriptData_Count(null);
    }

    public void setScriptData_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setScriptData_Count("scriptData", opLambda);
    }

    public void setScriptData_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "scriptData");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Cardinality() {
        setScriptData_Cardinality(null);
    }

    public void setScriptData_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setScriptData_Cardinality("scriptData", opLambda);
    }

    public void setScriptData_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "scriptData");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptData_Missing() {
        setScriptData_Missing(null);
    }

    public void setScriptData_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setScriptData_Missing("scriptData", opLambda, null);
    }

    public void setScriptData_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptData_Missing("scriptData", opLambda, aggsLambda);
    }

    public void setScriptData_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "scriptData");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String scriptResult

    public void setScriptResult_Terms() {
        setScriptResult_Terms(null);
    }

    public void setScriptResult_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setScriptResult_Terms("scriptResult", opLambda, null);
    }

    public void setScriptResult_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptResult_Terms("scriptResult", opLambda, aggsLambda);
    }

    public void setScriptResult_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "scriptResult");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setScriptResult_SignificantTerms() {
        setScriptResult_SignificantTerms(null);
    }

    public void setScriptResult_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setScriptResult_SignificantTerms("scriptResult", opLambda, null);
    }

    public void setScriptResult_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptResult_SignificantTerms("scriptResult", opLambda, aggsLambda);
    }

    public void setScriptResult_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "scriptResult");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setScriptResult_IpRange() {
        setScriptResult_IpRange(null);
    }

    public void setScriptResult_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setScriptResult_IpRange("scriptResult", opLambda, null);
    }

    public void setScriptResult_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptResult_IpRange("scriptResult", opLambda, aggsLambda);
    }

    public void setScriptResult_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "scriptResult");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setScriptResult_Count() {
        setScriptResult_Count(null);
    }

    public void setScriptResult_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setScriptResult_Count("scriptResult", opLambda);
    }

    public void setScriptResult_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "scriptResult");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_Cardinality() {
        setScriptResult_Cardinality(null);
    }

    public void setScriptResult_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setScriptResult_Cardinality("scriptResult", opLambda);
    }

    public void setScriptResult_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "scriptResult");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_Missing() {
        setScriptResult_Missing(null);
    }

    public void setScriptResult_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setScriptResult_Missing("scriptResult", opLambda, null);
    }

    public void setScriptResult_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptResult_Missing("scriptResult", opLambda, aggsLambda);
    }

    public void setScriptResult_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "scriptResult");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String scriptType

    public void setScriptType_Terms() {
        setScriptType_Terms(null);
    }

    public void setScriptType_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setScriptType_Terms("scriptType", opLambda, null);
    }

    public void setScriptType_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptType_Terms("scriptType", opLambda, aggsLambda);
    }

    public void setScriptType_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "scriptType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setScriptType_SignificantTerms() {
        setScriptType_SignificantTerms(null);
    }

    public void setScriptType_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setScriptType_SignificantTerms("scriptType", opLambda, null);
    }

    public void setScriptType_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptType_SignificantTerms("scriptType", opLambda, aggsLambda);
    }

    public void setScriptType_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "scriptType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setScriptType_IpRange() {
        setScriptType_IpRange(null);
    }

    public void setScriptType_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setScriptType_IpRange("scriptType", opLambda, null);
    }

    public void setScriptType_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptType_IpRange("scriptType", opLambda, aggsLambda);
    }

    public void setScriptType_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "scriptType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setScriptType_Count() {
        setScriptType_Count(null);
    }

    public void setScriptType_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setScriptType_Count("scriptType", opLambda);
    }

    public void setScriptType_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "scriptType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Cardinality() {
        setScriptType_Cardinality(null);
    }

    public void setScriptType_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setScriptType_Cardinality("scriptType", opLambda);
    }

    public void setScriptType_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "scriptType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptType_Missing() {
        setScriptType_Missing(null);
    }

    public void setScriptType_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setScriptType_Missing("scriptType", opLambda, null);
    }

    public void setScriptType_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptType_Missing("scriptType", opLambda, aggsLambda);
    }

    public void setScriptType_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "scriptType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Long startTime
    public void setStartTime_Avg() {
        setStartTime_Avg(null);
    }

    public void setStartTime_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setStartTime_Avg("startTime", opLambda);
    }

    public void setStartTime_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Max() {
        setStartTime_Max(null);
    }

    public void setStartTime_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setStartTime_Max("startTime", opLambda);
    }

    public void setStartTime_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Min() {
        setStartTime_Min(null);
    }

    public void setStartTime_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setStartTime_Min("startTime", opLambda);
    }

    public void setStartTime_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Sum() {
        setStartTime_Sum(null);
    }

    public void setStartTime_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setStartTime_Sum("startTime", opLambda);
    }

    public void setStartTime_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_ExtendedStats() {
        setStartTime_ExtendedStats(null);
    }

    public void setStartTime_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setStartTime_ExtendedStats("startTime", opLambda);
    }

    public void setStartTime_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Stats() {
        setStartTime_Stats(null);
    }

    public void setStartTime_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setStartTime_Stats("startTime", opLambda);
    }

    public void setStartTime_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Percentiles() {
        setStartTime_Percentiles(null);
    }

    public void setStartTime_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setStartTime_Percentiles("startTime", opLambda);
    }

    public void setStartTime_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_PercentileRanks() {
        setStartTime_PercentileRanks(null);
    }

    public void setStartTime_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setStartTime_PercentileRanks("startTime", opLambda);
    }

    public void setStartTime_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Histogram() {
        setStartTime_Histogram(null);
    }

    public void setStartTime_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setStartTime_Histogram("startTime", opLambda, null);
    }

    public void setStartTime_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setStartTime_Histogram("startTime", opLambda, aggsLambda);
    }

    public void setStartTime_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setStartTime_Range() {
        setStartTime_Range(null);
    }

    public void setStartTime_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setStartTime_Range("startTime", opLambda, null);
    }

    public void setStartTime_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setStartTime_Range("startTime", opLambda, aggsLambda);
    }

    public void setStartTime_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setStartTime_Count() {
        setStartTime_Count(null);
    }

    public void setStartTime_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setStartTime_Count("startTime", opLambda);
    }

    public void setStartTime_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Cardinality() {
        setStartTime_Cardinality(null);
    }

    public void setStartTime_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setStartTime_Cardinality("startTime", opLambda);
    }

    public void setStartTime_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Missing() {
        setStartTime_Missing(null);
    }

    public void setStartTime_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setStartTime_Missing("startTime", opLambda, null);
    }

    public void setStartTime_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setStartTime_Missing("startTime", opLambda, aggsLambda);
    }

    public void setStartTime_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String target

    public void setTarget_Terms() {
        setTarget_Terms(null);
    }

    public void setTarget_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setTarget_Terms("target", opLambda, null);
    }

    public void setTarget_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setTarget_Terms("target", opLambda, aggsLambda);
    }

    public void setTarget_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTarget_SignificantTerms() {
        setTarget_SignificantTerms(null);
    }

    public void setTarget_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setTarget_SignificantTerms("target", opLambda, null);
    }

    public void setTarget_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setTarget_SignificantTerms("target", opLambda, aggsLambda);
    }

    public void setTarget_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTarget_IpRange() {
        setTarget_IpRange(null);
    }

    public void setTarget_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setTarget_IpRange("target", opLambda, null);
    }

    public void setTarget_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setTarget_IpRange("target", opLambda, aggsLambda);
    }

    public void setTarget_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTarget_Count() {
        setTarget_Count(null);
    }

    public void setTarget_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setTarget_Count("target", opLambda);
    }

    public void setTarget_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Cardinality() {
        setTarget_Cardinality(null);
    }

    public void setTarget_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setTarget_Cardinality("target", opLambda);
    }

    public void setTarget_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTarget_Missing() {
        setTarget_Missing(null);
    }

    public void setTarget_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setTarget_Missing("target", opLambda, null);
    }

    public void setTarget_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setTarget_Missing("target", opLambda, aggsLambda);
    }

    public void setTarget_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "target");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Long lastUpdated
    public void setLastUpdated_Avg() {
        setLastUpdated_Avg(null);
    }

    public void setLastUpdated_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setLastUpdated_Avg("lastUpdated", opLambda);
    }

    public void setLastUpdated_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_Max() {
        setLastUpdated_Max(null);
    }

    public void setLastUpdated_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setLastUpdated_Max("lastUpdated", opLambda);
    }

    public void setLastUpdated_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_Min() {
        setLastUpdated_Min(null);
    }

    public void setLastUpdated_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setLastUpdated_Min("lastUpdated", opLambda);
    }

    public void setLastUpdated_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_Sum() {
        setLastUpdated_Sum(null);
    }

    public void setLastUpdated_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setLastUpdated_Sum("lastUpdated", opLambda);
    }

    public void setLastUpdated_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_ExtendedStats() {
        setLastUpdated_ExtendedStats(null);
    }

    public void setLastUpdated_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setLastUpdated_ExtendedStats("lastUpdated", opLambda);
    }

    public void setLastUpdated_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_Stats() {
        setLastUpdated_Stats(null);
    }

    public void setLastUpdated_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setLastUpdated_Stats("lastUpdated", opLambda);
    }

    public void setLastUpdated_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_Percentiles() {
        setLastUpdated_Percentiles(null);
    }

    public void setLastUpdated_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setLastUpdated_Percentiles("lastUpdated", opLambda);
    }

    public void setLastUpdated_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_PercentileRanks() {
        setLastUpdated_PercentileRanks(null);
    }

    public void setLastUpdated_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setLastUpdated_PercentileRanks("lastUpdated", opLambda);
    }

    public void setLastUpdated_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_Histogram() {
        setLastUpdated_Histogram(null);
    }

    public void setLastUpdated_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setLastUpdated_Histogram("lastUpdated", opLambda, null);
    }

    public void setLastUpdated_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setLastUpdated_Histogram("lastUpdated", opLambda, aggsLambda);
    }

    public void setLastUpdated_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLastUpdated_Range() {
        setLastUpdated_Range(null);
    }

    public void setLastUpdated_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setLastUpdated_Range("lastUpdated", opLambda, null);
    }

    public void setLastUpdated_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setLastUpdated_Range("lastUpdated", opLambda, aggsLambda);
    }

    public void setLastUpdated_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLastUpdated_Count() {
        setLastUpdated_Count(null);
    }

    public void setLastUpdated_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setLastUpdated_Count("lastUpdated", opLambda);
    }

    public void setLastUpdated_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_Cardinality() {
        setLastUpdated_Cardinality(null);
    }

    public void setLastUpdated_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setLastUpdated_Cardinality("lastUpdated", opLambda);
    }

    public void setLastUpdated_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_Missing() {
        setLastUpdated_Missing(null);
    }

    public void setLastUpdated_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setLastUpdated_Missing("lastUpdated", opLambda, null);
    }

    public void setLastUpdated_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setLastUpdated_Missing("lastUpdated", opLambda, aggsLambda);
    }

    public void setLastUpdated_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
