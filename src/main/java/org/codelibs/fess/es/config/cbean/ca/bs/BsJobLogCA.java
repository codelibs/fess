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
import org.codelibs.fess.es.config.cbean.ca.JobLogCA;
import org.codelibs.fess.es.config.cbean.cq.JobLogCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsJobLogCQ;
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

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
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

    public void setEndTime_Avg() {
        setEndTime_Avg(null);
    }

    public void setEndTime_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setEndTime_Avg("endTime", opLambda);
    }

    public void setEndTime_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Max() {
        setEndTime_Max(null);
    }

    public void setEndTime_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setEndTime_Max("endTime", opLambda);
    }

    public void setEndTime_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Min() {
        setEndTime_Min(null);
    }

    public void setEndTime_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setEndTime_Min("endTime", opLambda);
    }

    public void setEndTime_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Sum() {
        setEndTime_Sum(null);
    }

    public void setEndTime_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setEndTime_Sum("endTime", opLambda);
    }

    public void setEndTime_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_ExtendedStats() {
        setEndTime_ExtendedStats(null);
    }

    public void setEndTime_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setEndTime_ExtendedStats("endTime", opLambda);
    }

    public void setEndTime_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Stats() {
        setEndTime_Stats(null);
    }

    public void setEndTime_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setEndTime_Stats("endTime", opLambda);
    }

    public void setEndTime_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Percentiles() {
        setEndTime_Percentiles(null);
    }

    public void setEndTime_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setEndTime_Percentiles("endTime", opLambda);
    }

    public void setEndTime_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_PercentileRanks(double[] values) {
        setEndTime_PercentileRanks(values, null);
    }

    public void setEndTime_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setEndTime_PercentileRanks("endTime", values, opLambda);
    }

    public void setEndTime_PercentileRanks(String name, double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "endTime", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Histogram() {
        setEndTime_Histogram(null);
    }

    public void setEndTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setEndTime_Histogram("endTime", opLambda, null);
    }

    public void setEndTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setEndTime_Histogram("endTime", opLambda, aggsLambda);
    }

    public void setEndTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "endTime");
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

    public void setEndTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setEndTime_Range("endTime", opLambda, null);
    }

    public void setEndTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setEndTime_Range("endTime", opLambda, aggsLambda);
    }

    public void setEndTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "endTime");
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

    public void setEndTime_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setEndTime_Count("endTime", opLambda);
    }

    public void setEndTime_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Cardinality() {
        setEndTime_Cardinality(null);
    }

    public void setEndTime_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setEndTime_Cardinality("endTime", opLambda);
    }

    public void setEndTime_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEndTime_Missing() {
        setEndTime_Missing(null);
    }

    public void setEndTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setEndTime_Missing("endTime", opLambda, null);
    }

    public void setEndTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setEndTime_Missing("endTime", opLambda, aggsLambda);
    }

    public void setEndTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "endTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setJobName_Terms() {
        setJobName_Terms(null);
    }

    public void setJobName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setJobName_Terms("jobName", opLambda, null);
    }

    public void setJobName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setJobName_Terms("jobName", opLambda, aggsLambda);
    }

    public void setJobName_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "jobName");
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

    public void setJobName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setJobName_SignificantTerms("jobName", opLambda, null);
    }

    public void setJobName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        setJobName_SignificantTerms("jobName", opLambda, aggsLambda);
    }

    public void setJobName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "jobName");
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

    public void setJobName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setJobName_IpRange("jobName", opLambda, null);
    }

    public void setJobName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setJobName_IpRange("jobName", opLambda, aggsLambda);
    }

    public void setJobName_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "jobName");
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

    public void setJobName_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setJobName_Count("jobName", opLambda);
    }

    public void setJobName_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "jobName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_Cardinality() {
        setJobName_Cardinality(null);
    }

    public void setJobName_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setJobName_Cardinality("jobName", opLambda);
    }

    public void setJobName_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "jobName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobName_Missing() {
        setJobName_Missing(null);
    }

    public void setJobName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setJobName_Missing("jobName", opLambda, null);
    }

    public void setJobName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setJobName_Missing("jobName", opLambda, aggsLambda);
    }

    public void setJobName_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "jobName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setJobStatus_Terms() {
        setJobStatus_Terms(null);
    }

    public void setJobStatus_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setJobStatus_Terms("jobStatus", opLambda, null);
    }

    public void setJobStatus_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setJobStatus_Terms("jobStatus", opLambda, aggsLambda);
    }

    public void setJobStatus_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "jobStatus");
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

    public void setJobStatus_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setJobStatus_SignificantTerms("jobStatus", opLambda, null);
    }

    public void setJobStatus_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        setJobStatus_SignificantTerms("jobStatus", opLambda, aggsLambda);
    }

    public void setJobStatus_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "jobStatus");
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

    public void setJobStatus_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setJobStatus_IpRange("jobStatus", opLambda, null);
    }

    public void setJobStatus_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setJobStatus_IpRange("jobStatus", opLambda, aggsLambda);
    }

    public void setJobStatus_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "jobStatus");
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

    public void setJobStatus_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setJobStatus_Count("jobStatus", opLambda);
    }

    public void setJobStatus_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "jobStatus");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_Cardinality() {
        setJobStatus_Cardinality(null);
    }

    public void setJobStatus_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setJobStatus_Cardinality("jobStatus", opLambda);
    }

    public void setJobStatus_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "jobStatus");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setJobStatus_Missing() {
        setJobStatus_Missing(null);
    }

    public void setJobStatus_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setJobStatus_Missing("jobStatus", opLambda, null);
    }

    public void setJobStatus_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setJobStatus_Missing("jobStatus", opLambda, aggsLambda);
    }

    public void setJobStatus_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "jobStatus");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLastUpdated_Avg() {
        setLastUpdated_Avg(null);
    }

    public void setLastUpdated_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setLastUpdated_Avg("lastUpdated", opLambda);
    }

    public void setLastUpdated_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_Max() {
        setLastUpdated_Max(null);
    }

    public void setLastUpdated_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setLastUpdated_Max("lastUpdated", opLambda);
    }

    public void setLastUpdated_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_Min() {
        setLastUpdated_Min(null);
    }

    public void setLastUpdated_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setLastUpdated_Min("lastUpdated", opLambda);
    }

    public void setLastUpdated_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_Sum() {
        setLastUpdated_Sum(null);
    }

    public void setLastUpdated_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setLastUpdated_Sum("lastUpdated", opLambda);
    }

    public void setLastUpdated_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_ExtendedStats() {
        setLastUpdated_ExtendedStats(null);
    }

    public void setLastUpdated_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setLastUpdated_ExtendedStats("lastUpdated", opLambda);
    }

    public void setLastUpdated_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_Stats() {
        setLastUpdated_Stats(null);
    }

    public void setLastUpdated_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setLastUpdated_Stats("lastUpdated", opLambda);
    }

    public void setLastUpdated_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_Percentiles() {
        setLastUpdated_Percentiles(null);
    }

    public void setLastUpdated_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setLastUpdated_Percentiles("lastUpdated", opLambda);
    }

    public void setLastUpdated_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_PercentileRanks(double[] values) {
        setLastUpdated_PercentileRanks(values, null);
    }

    public void setLastUpdated_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setLastUpdated_PercentileRanks("lastUpdated", values, opLambda);
    }

    public void setLastUpdated_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "lastUpdated", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_Histogram() {
        setLastUpdated_Histogram(null);
    }

    public void setLastUpdated_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setLastUpdated_Histogram("lastUpdated", opLambda, null);
    }

    public void setLastUpdated_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setLastUpdated_Histogram("lastUpdated", opLambda, aggsLambda);
    }

    public void setLastUpdated_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "lastUpdated");
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

    public void setLastUpdated_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setLastUpdated_Range("lastUpdated", opLambda, null);
    }

    public void setLastUpdated_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setLastUpdated_Range("lastUpdated", opLambda, aggsLambda);
    }

    public void setLastUpdated_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "lastUpdated");
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

    public void setLastUpdated_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setLastUpdated_Count("lastUpdated", opLambda);
    }

    public void setLastUpdated_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_Cardinality() {
        setLastUpdated_Cardinality(null);
    }

    public void setLastUpdated_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setLastUpdated_Cardinality("lastUpdated", opLambda);
    }

    public void setLastUpdated_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastUpdated_Missing() {
        setLastUpdated_Missing(null);
    }

    public void setLastUpdated_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setLastUpdated_Missing("lastUpdated", opLambda, null);
    }

    public void setLastUpdated_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setLastUpdated_Missing("lastUpdated", opLambda, aggsLambda);
    }

    public void setLastUpdated_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "lastUpdated");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
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

    public void setScriptData_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptData_Terms("scriptData", opLambda, aggsLambda);
    }

    public void setScriptData_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "scriptData");
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

    public void setScriptData_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setScriptData_SignificantTerms("scriptData", opLambda, null);
    }

    public void setScriptData_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptData_SignificantTerms("scriptData", opLambda, aggsLambda);
    }

    public void setScriptData_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "scriptData");
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

    public void setScriptData_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setScriptData_IpRange("scriptData", opLambda, null);
    }

    public void setScriptData_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptData_IpRange("scriptData", opLambda, aggsLambda);
    }

    public void setScriptData_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "scriptData");
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

    public void setScriptData_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptData_Missing("scriptData", opLambda, aggsLambda);
    }

    public void setScriptData_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "scriptData");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setScriptResult_Terms() {
        setScriptResult_Terms(null);
    }

    public void setScriptResult_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setScriptResult_Terms("scriptResult", opLambda, null);
    }

    public void setScriptResult_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptResult_Terms("scriptResult", opLambda, aggsLambda);
    }

    public void setScriptResult_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "scriptResult");
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

    public void setScriptResult_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setScriptResult_SignificantTerms("scriptResult", opLambda, null);
    }

    public void setScriptResult_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptResult_SignificantTerms("scriptResult", opLambda, aggsLambda);
    }

    public void setScriptResult_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "scriptResult");
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

    public void setScriptResult_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setScriptResult_IpRange("scriptResult", opLambda, null);
    }

    public void setScriptResult_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptResult_IpRange("scriptResult", opLambda, aggsLambda);
    }

    public void setScriptResult_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "scriptResult");
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

    public void setScriptResult_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setScriptResult_Count("scriptResult", opLambda);
    }

    public void setScriptResult_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "scriptResult");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_Cardinality() {
        setScriptResult_Cardinality(null);
    }

    public void setScriptResult_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setScriptResult_Cardinality("scriptResult", opLambda);
    }

    public void setScriptResult_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "scriptResult");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setScriptResult_Missing() {
        setScriptResult_Missing(null);
    }

    public void setScriptResult_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setScriptResult_Missing("scriptResult", opLambda, null);
    }

    public void setScriptResult_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptResult_Missing("scriptResult", opLambda, aggsLambda);
    }

    public void setScriptResult_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "scriptResult");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
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

    public void setScriptType_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptType_Terms("scriptType", opLambda, aggsLambda);
    }

    public void setScriptType_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "scriptType");
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

    public void setScriptType_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setScriptType_SignificantTerms("scriptType", opLambda, null);
    }

    public void setScriptType_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptType_SignificantTerms("scriptType", opLambda, aggsLambda);
    }

    public void setScriptType_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "scriptType");
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

    public void setScriptType_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setScriptType_IpRange("scriptType", opLambda, null);
    }

    public void setScriptType_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptType_IpRange("scriptType", opLambda, aggsLambda);
    }

    public void setScriptType_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "scriptType");
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

    public void setScriptType_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setScriptType_Missing("scriptType", opLambda, aggsLambda);
    }

    public void setScriptType_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "scriptType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setStartTime_Avg() {
        setStartTime_Avg(null);
    }

    public void setStartTime_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setStartTime_Avg("startTime", opLambda);
    }

    public void setStartTime_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Max() {
        setStartTime_Max(null);
    }

    public void setStartTime_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setStartTime_Max("startTime", opLambda);
    }

    public void setStartTime_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Min() {
        setStartTime_Min(null);
    }

    public void setStartTime_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setStartTime_Min("startTime", opLambda);
    }

    public void setStartTime_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Sum() {
        setStartTime_Sum(null);
    }

    public void setStartTime_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setStartTime_Sum("startTime", opLambda);
    }

    public void setStartTime_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_ExtendedStats() {
        setStartTime_ExtendedStats(null);
    }

    public void setStartTime_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setStartTime_ExtendedStats("startTime", opLambda);
    }

    public void setStartTime_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Stats() {
        setStartTime_Stats(null);
    }

    public void setStartTime_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setStartTime_Stats("startTime", opLambda);
    }

    public void setStartTime_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Percentiles() {
        setStartTime_Percentiles(null);
    }

    public void setStartTime_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setStartTime_Percentiles("startTime", opLambda);
    }

    public void setStartTime_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_PercentileRanks(double[] values) {
        setStartTime_PercentileRanks(values, null);
    }

    public void setStartTime_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setStartTime_PercentileRanks("startTime", values, opLambda);
    }

    public void setStartTime_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "startTime", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Histogram() {
        setStartTime_Histogram(null);
    }

    public void setStartTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setStartTime_Histogram("startTime", opLambda, null);
    }

    public void setStartTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setStartTime_Histogram("startTime", opLambda, aggsLambda);
    }

    public void setStartTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "startTime");
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

    public void setStartTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setStartTime_Range("startTime", opLambda, null);
    }

    public void setStartTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setStartTime_Range("startTime", opLambda, aggsLambda);
    }

    public void setStartTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "startTime");
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

    public void setStartTime_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setStartTime_Count("startTime", opLambda);
    }

    public void setStartTime_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Cardinality() {
        setStartTime_Cardinality(null);
    }

    public void setStartTime_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setStartTime_Cardinality("startTime", opLambda);
    }

    public void setStartTime_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStartTime_Missing() {
        setStartTime_Missing(null);
    }

    public void setStartTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setStartTime_Missing("startTime", opLambda, null);
    }

    public void setStartTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setStartTime_Missing("startTime", opLambda, aggsLambda);
    }

    public void setStartTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "startTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            JobLogCA ca = new JobLogCA();
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

    public void setTarget_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setTarget_Terms("target", opLambda, aggsLambda);
    }

    public void setTarget_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "target");
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

    public void setTarget_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setTarget_SignificantTerms("target", opLambda, null);
    }

    public void setTarget_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        setTarget_SignificantTerms("target", opLambda, aggsLambda);
    }

    public void setTarget_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "target");
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

    public void setTarget_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setTarget_IpRange("target", opLambda, null);
    }

    public void setTarget_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setTarget_IpRange("target", opLambda, aggsLambda);
    }

    public void setTarget_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "target");
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

    public void setTarget_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsJobLogCA> aggsLambda) {
        setTarget_Missing("target", opLambda, aggsLambda);
    }

    public void setTarget_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsJobLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "target");
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
