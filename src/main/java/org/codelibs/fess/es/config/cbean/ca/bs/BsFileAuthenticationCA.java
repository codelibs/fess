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
import org.codelibs.fess.es.config.cbean.ca.FileAuthenticationCA;
import org.codelibs.fess.es.config.cbean.cq.FileAuthenticationCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsFileAuthenticationCQ;
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

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
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

    public void setCreatedBy_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setCreatedBy_Terms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "createdBy");
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

    public void setCreatedBy_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setCreatedBy_SignificantTerms("createdBy", opLambda, null);
    }

    public void setCreatedBy_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setCreatedBy_SignificantTerms("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "createdBy");
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

    public void setCreatedBy_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setCreatedBy_IpRange("createdBy", opLambda, null);
    }

    public void setCreatedBy_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setCreatedBy_IpRange("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "createdBy");
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

    public void setCreatedBy_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setCreatedBy_Missing("createdBy", opLambda, aggsLambda);
    }

    public void setCreatedBy_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "createdBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
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
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setCreatedTime_Histogram("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "createdTime");
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

    public void setCreatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setCreatedTime_Range("createdTime", opLambda, null);
    }

    public void setCreatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setCreatedTime_Range("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "createdTime");
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
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setCreatedTime_Missing("createdTime", opLambda, aggsLambda);
    }

    public void setCreatedTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "createdTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setFileConfigId_Terms() {
        setFileConfigId_Terms(null);
    }

    public void setFileConfigId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setFileConfigId_Terms("fileConfigId", opLambda, null);
    }

    public void setFileConfigId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setFileConfigId_Terms("fileConfigId", opLambda, aggsLambda);
    }

    public void setFileConfigId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "fileConfigId");
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

    public void setFileConfigId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setFileConfigId_SignificantTerms("fileConfigId", opLambda, null);
    }

    public void setFileConfigId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setFileConfigId_SignificantTerms("fileConfigId", opLambda, aggsLambda);
    }

    public void setFileConfigId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "fileConfigId");
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

    public void setFileConfigId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setFileConfigId_IpRange("fileConfigId", opLambda, null);
    }

    public void setFileConfigId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setFileConfigId_IpRange("fileConfigId", opLambda, aggsLambda);
    }

    public void setFileConfigId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "fileConfigId");
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

    public void setFileConfigId_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setFileConfigId_Count("fileConfigId", opLambda);
    }

    public void setFileConfigId_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "fileConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_Cardinality() {
        setFileConfigId_Cardinality(null);
    }

    public void setFileConfigId_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setFileConfigId_Cardinality("fileConfigId", opLambda);
    }

    public void setFileConfigId_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "fileConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFileConfigId_Missing() {
        setFileConfigId_Missing(null);
    }

    public void setFileConfigId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setFileConfigId_Missing("fileConfigId", opLambda, null);
    }

    public void setFileConfigId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setFileConfigId_Missing("fileConfigId", opLambda, aggsLambda);
    }

    public void setFileConfigId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "fileConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHostname_Terms() {
        setHostname_Terms(null);
    }

    public void setHostname_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setHostname_Terms("hostname", opLambda, null);
    }

    public void setHostname_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setHostname_Terms("hostname", opLambda, aggsLambda);
    }

    public void setHostname_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "hostname");
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

    public void setHostname_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setHostname_SignificantTerms("hostname", opLambda, null);
    }

    public void setHostname_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setHostname_SignificantTerms("hostname", opLambda, aggsLambda);
    }

    public void setHostname_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "hostname");
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

    public void setHostname_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setHostname_IpRange("hostname", opLambda, null);
    }

    public void setHostname_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setHostname_IpRange("hostname", opLambda, aggsLambda);
    }

    public void setHostname_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "hostname");
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

    public void setHostname_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setHostname_Count("hostname", opLambda);
    }

    public void setHostname_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "hostname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_Cardinality() {
        setHostname_Cardinality(null);
    }

    public void setHostname_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setHostname_Cardinality("hostname", opLambda);
    }

    public void setHostname_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "hostname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHostname_Missing() {
        setHostname_Missing(null);
    }

    public void setHostname_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setHostname_Missing("hostname", opLambda, null);
    }

    public void setHostname_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setHostname_Missing("hostname", opLambda, aggsLambda);
    }

    public void setHostname_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "hostname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setParameters_Terms() {
        setParameters_Terms(null);
    }

    public void setParameters_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setParameters_Terms("parameters", opLambda, null);
    }

    public void setParameters_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setParameters_Terms("parameters", opLambda, aggsLambda);
    }

    public void setParameters_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "parameters");
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

    public void setParameters_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setParameters_SignificantTerms("parameters", opLambda, null);
    }

    public void setParameters_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setParameters_SignificantTerms("parameters", opLambda, aggsLambda);
    }

    public void setParameters_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "parameters");
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

    public void setParameters_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setParameters_IpRange("parameters", opLambda, null);
    }

    public void setParameters_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setParameters_IpRange("parameters", opLambda, aggsLambda);
    }

    public void setParameters_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "parameters");
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

    public void setParameters_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setParameters_Count("parameters", opLambda);
    }

    public void setParameters_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "parameters");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_Cardinality() {
        setParameters_Cardinality(null);
    }

    public void setParameters_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setParameters_Cardinality("parameters", opLambda);
    }

    public void setParameters_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "parameters");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setParameters_Missing() {
        setParameters_Missing(null);
    }

    public void setParameters_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setParameters_Missing("parameters", opLambda, null);
    }

    public void setParameters_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setParameters_Missing("parameters", opLambda, aggsLambda);
    }

    public void setParameters_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "parameters");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPassword_Terms() {
        setPassword_Terms(null);
    }

    public void setPassword_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setPassword_Terms("password", opLambda, null);
    }

    public void setPassword_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setPassword_Terms("password", opLambda, aggsLambda);
    }

    public void setPassword_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "password");
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

    public void setPassword_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setPassword_SignificantTerms("password", opLambda, null);
    }

    public void setPassword_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setPassword_SignificantTerms("password", opLambda, aggsLambda);
    }

    public void setPassword_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "password");
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

    public void setPassword_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setPassword_IpRange("password", opLambda, null);
    }

    public void setPassword_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setPassword_IpRange("password", opLambda, aggsLambda);
    }

    public void setPassword_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "password");
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

    public void setPassword_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setPassword_Count("password", opLambda);
    }

    public void setPassword_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Cardinality() {
        setPassword_Cardinality(null);
    }

    public void setPassword_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setPassword_Cardinality("password", opLambda);
    }

    public void setPassword_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Missing() {
        setPassword_Missing(null);
    }

    public void setPassword_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setPassword_Missing("password", opLambda, null);
    }

    public void setPassword_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setPassword_Missing("password", opLambda, aggsLambda);
    }

    public void setPassword_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPort_Avg() {
        setPort_Avg(null);
    }

    public void setPort_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setPort_Avg("port", opLambda);
    }

    public void setPort_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Max() {
        setPort_Max(null);
    }

    public void setPort_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setPort_Max("port", opLambda);
    }

    public void setPort_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Min() {
        setPort_Min(null);
    }

    public void setPort_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setPort_Min("port", opLambda);
    }

    public void setPort_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Sum() {
        setPort_Sum(null);
    }

    public void setPort_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setPort_Sum("port", opLambda);
    }

    public void setPort_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_ExtendedStats() {
        setPort_ExtendedStats(null);
    }

    public void setPort_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setPort_ExtendedStats("port", opLambda);
    }

    public void setPort_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Stats() {
        setPort_Stats(null);
    }

    public void setPort_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setPort_Stats("port", opLambda);
    }

    public void setPort_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Percentiles() {
        setPort_Percentiles(null);
    }

    public void setPort_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setPort_Percentiles("port", opLambda);
    }

    public void setPort_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_PercentileRanks(double[] values) {
        setPort_PercentileRanks(values, null);
    }

    public void setPort_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setPort_PercentileRanks("port", values, opLambda);
    }

    public void setPort_PercentileRanks(String name, double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "port", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Histogram() {
        setPort_Histogram(null);
    }

    public void setPort_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setPort_Histogram("port", opLambda, null);
    }

    public void setPort_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setPort_Histogram("port", opLambda, aggsLambda);
    }

    public void setPort_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "port");
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

    public void setPort_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setPort_Range("port", opLambda, null);
    }

    public void setPort_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setPort_Range("port", opLambda, aggsLambda);
    }

    public void setPort_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "port");
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

    public void setPort_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setPort_Count("port", opLambda);
    }

    public void setPort_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Cardinality() {
        setPort_Cardinality(null);
    }

    public void setPort_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setPort_Cardinality("port", opLambda);
    }

    public void setPort_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPort_Missing() {
        setPort_Missing(null);
    }

    public void setPort_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setPort_Missing("port", opLambda, null);
    }

    public void setPort_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setPort_Missing("port", opLambda, aggsLambda);
    }

    public void setPort_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "port");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setProtocolScheme_Terms() {
        setProtocolScheme_Terms(null);
    }

    public void setProtocolScheme_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setProtocolScheme_Terms("protocolScheme", opLambda, null);
    }

    public void setProtocolScheme_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setProtocolScheme_Terms("protocolScheme", opLambda, aggsLambda);
    }

    public void setProtocolScheme_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "protocolScheme");
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

    public void setProtocolScheme_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setProtocolScheme_SignificantTerms("protocolScheme", opLambda, null);
    }

    public void setProtocolScheme_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setProtocolScheme_SignificantTerms("protocolScheme", opLambda, aggsLambda);
    }

    public void setProtocolScheme_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "protocolScheme");
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

    public void setProtocolScheme_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setProtocolScheme_IpRange("protocolScheme", opLambda, null);
    }

    public void setProtocolScheme_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setProtocolScheme_IpRange("protocolScheme", opLambda, aggsLambda);
    }

    public void setProtocolScheme_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "protocolScheme");
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

    public void setProtocolScheme_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setProtocolScheme_Count("protocolScheme", opLambda);
    }

    public void setProtocolScheme_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "protocolScheme");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_Cardinality() {
        setProtocolScheme_Cardinality(null);
    }

    public void setProtocolScheme_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setProtocolScheme_Cardinality("protocolScheme", opLambda);
    }

    public void setProtocolScheme_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "protocolScheme");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setProtocolScheme_Missing() {
        setProtocolScheme_Missing(null);
    }

    public void setProtocolScheme_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setProtocolScheme_Missing("protocolScheme", opLambda, null);
    }

    public void setProtocolScheme_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setProtocolScheme_Missing("protocolScheme", opLambda, aggsLambda);
    }

    public void setProtocolScheme_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "protocolScheme");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
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

    public void setUpdatedBy_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUpdatedBy_Terms("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "updatedBy");
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

    public void setUpdatedBy_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setUpdatedBy_SignificantTerms("updatedBy", opLambda, null);
    }

    public void setUpdatedBy_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUpdatedBy_SignificantTerms("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "updatedBy");
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

    public void setUpdatedBy_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setUpdatedBy_IpRange("updatedBy", opLambda, null);
    }

    public void setUpdatedBy_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUpdatedBy_IpRange("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "updatedBy");
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

    public void setUpdatedBy_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUpdatedBy_Missing("updatedBy", opLambda, aggsLambda);
    }

    public void setUpdatedBy_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "updatedBy");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
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
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUpdatedTime_Histogram("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "updatedTime");
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

    public void setUpdatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setUpdatedTime_Range("updatedTime", opLambda, null);
    }

    public void setUpdatedTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUpdatedTime_Range("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "updatedTime");
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

    public void setUpdatedTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUpdatedTime_Missing("updatedTime", opLambda, aggsLambda);
    }

    public void setUpdatedTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "updatedTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FileAuthenticationCA ca = new FileAuthenticationCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUsername_Terms() {
        setUsername_Terms(null);
    }

    public void setUsername_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setUsername_Terms("username", opLambda, null);
    }

    public void setUsername_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUsername_Terms("username", opLambda, aggsLambda);
    }

    public void setUsername_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "username");
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

    public void setUsername_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setUsername_SignificantTerms("username", opLambda, null);
    }

    public void setUsername_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUsername_SignificantTerms("username", opLambda, aggsLambda);
    }

    public void setUsername_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "username");
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

    public void setUsername_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setUsername_IpRange("username", opLambda, null);
    }

    public void setUsername_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUsername_IpRange("username", opLambda, aggsLambda);
    }

    public void setUsername_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "username");
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

    public void setUsername_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setUsername_Count("username", opLambda);
    }

    public void setUsername_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "username");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_Cardinality() {
        setUsername_Cardinality(null);
    }

    public void setUsername_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setUsername_Cardinality("username", opLambda);
    }

    public void setUsername_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "username");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUsername_Missing() {
        setUsername_Missing(null);
    }

    public void setUsername_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setUsername_Missing("username", opLambda, null);
    }

    public void setUsername_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        setUsername_Missing("username", opLambda, aggsLambda);
    }

    public void setUsername_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFileAuthenticationCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "username");
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
