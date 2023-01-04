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
import org.codelibs.fess.es.config.cbean.ca.FailureUrlCA;
import org.codelibs.fess.es.config.cbean.cq.FailureUrlCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsFailureUrlCQ;
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
public abstract class BsFailureUrlCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsFailureUrlCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        FailureUrlCQ cq = new FailureUrlCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
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

    public void setConfigId_Terms() {
        setConfigId_Terms(null);
    }

    public void setConfigId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setConfigId_Terms("configId", opLambda, null);
    }

    public void setConfigId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        setConfigId_Terms("configId", opLambda, aggsLambda);
    }

    public void setConfigId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "configId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setConfigId_SignificantTerms() {
        setConfigId_SignificantTerms(null);
    }

    public void setConfigId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setConfigId_SignificantTerms("configId", opLambda, null);
    }

    public void setConfigId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        setConfigId_SignificantTerms("configId", opLambda, aggsLambda);
    }

    public void setConfigId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "configId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setConfigId_IpRange() {
        setConfigId_IpRange(null);
    }

    public void setConfigId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setConfigId_IpRange("configId", opLambda, null);
    }

    public void setConfigId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        setConfigId_IpRange("configId", opLambda, aggsLambda);
    }

    public void setConfigId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "configId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setConfigId_Count() {
        setConfigId_Count(null);
    }

    public void setConfigId_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setConfigId_Count("configId", opLambda);
    }

    public void setConfigId_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "configId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_Cardinality() {
        setConfigId_Cardinality(null);
    }

    public void setConfigId_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setConfigId_Cardinality("configId", opLambda);
    }

    public void setConfigId_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "configId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setConfigId_Missing() {
        setConfigId_Missing(null);
    }

    public void setConfigId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setConfigId_Missing("configId", opLambda, null);
    }

    public void setConfigId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        setConfigId_Missing("configId", opLambda, aggsLambda);
    }

    public void setConfigId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "configId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setErrorCount_Avg() {
        setErrorCount_Avg(null);
    }

    public void setErrorCount_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setErrorCount_Avg("errorCount", opLambda);
    }

    public void setErrorCount_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "errorCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_Max() {
        setErrorCount_Max(null);
    }

    public void setErrorCount_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setErrorCount_Max("errorCount", opLambda);
    }

    public void setErrorCount_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "errorCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_Min() {
        setErrorCount_Min(null);
    }

    public void setErrorCount_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setErrorCount_Min("errorCount", opLambda);
    }

    public void setErrorCount_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "errorCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_Sum() {
        setErrorCount_Sum(null);
    }

    public void setErrorCount_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setErrorCount_Sum("errorCount", opLambda);
    }

    public void setErrorCount_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "errorCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_ExtendedStats() {
        setErrorCount_ExtendedStats(null);
    }

    public void setErrorCount_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setErrorCount_ExtendedStats("errorCount", opLambda);
    }

    public void setErrorCount_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "errorCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_Stats() {
        setErrorCount_Stats(null);
    }

    public void setErrorCount_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setErrorCount_Stats("errorCount", opLambda);
    }

    public void setErrorCount_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "errorCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_Percentiles() {
        setErrorCount_Percentiles(null);
    }

    public void setErrorCount_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setErrorCount_Percentiles("errorCount", opLambda);
    }

    public void setErrorCount_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "errorCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_PercentileRanks(double[] values) {
        setErrorCount_PercentileRanks(values, null);
    }

    public void setErrorCount_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setErrorCount_PercentileRanks("errorCount", values, opLambda);
    }

    public void setErrorCount_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "errorCount", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_Histogram() {
        setErrorCount_Histogram(null);
    }

    public void setErrorCount_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setErrorCount_Histogram("errorCount", opLambda, null);
    }

    public void setErrorCount_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        setErrorCount_Histogram("errorCount", opLambda, aggsLambda);
    }

    public void setErrorCount_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "errorCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setErrorCount_Range() {
        setErrorCount_Range(null);
    }

    public void setErrorCount_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setErrorCount_Range("errorCount", opLambda, null);
    }

    public void setErrorCount_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        setErrorCount_Range("errorCount", opLambda, aggsLambda);
    }

    public void setErrorCount_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "errorCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setErrorCount_Count() {
        setErrorCount_Count(null);
    }

    public void setErrorCount_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setErrorCount_Count("errorCount", opLambda);
    }

    public void setErrorCount_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "errorCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_Cardinality() {
        setErrorCount_Cardinality(null);
    }

    public void setErrorCount_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setErrorCount_Cardinality("errorCount", opLambda);
    }

    public void setErrorCount_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "errorCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorCount_Missing() {
        setErrorCount_Missing(null);
    }

    public void setErrorCount_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setErrorCount_Missing("errorCount", opLambda, null);
    }

    public void setErrorCount_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        setErrorCount_Missing("errorCount", opLambda, aggsLambda);
    }

    public void setErrorCount_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "errorCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setErrorLog_Terms() {
        setErrorLog_Terms(null);
    }

    public void setErrorLog_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setErrorLog_Terms("errorLog", opLambda, null);
    }

    public void setErrorLog_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        setErrorLog_Terms("errorLog", opLambda, aggsLambda);
    }

    public void setErrorLog_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "errorLog");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setErrorLog_SignificantTerms() {
        setErrorLog_SignificantTerms(null);
    }

    public void setErrorLog_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setErrorLog_SignificantTerms("errorLog", opLambda, null);
    }

    public void setErrorLog_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        setErrorLog_SignificantTerms("errorLog", opLambda, aggsLambda);
    }

    public void setErrorLog_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "errorLog");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setErrorLog_IpRange() {
        setErrorLog_IpRange(null);
    }

    public void setErrorLog_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setErrorLog_IpRange("errorLog", opLambda, null);
    }

    public void setErrorLog_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        setErrorLog_IpRange("errorLog", opLambda, aggsLambda);
    }

    public void setErrorLog_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "errorLog");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setErrorLog_Count() {
        setErrorLog_Count(null);
    }

    public void setErrorLog_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setErrorLog_Count("errorLog", opLambda);
    }

    public void setErrorLog_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "errorLog");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_Cardinality() {
        setErrorLog_Cardinality(null);
    }

    public void setErrorLog_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setErrorLog_Cardinality("errorLog", opLambda);
    }

    public void setErrorLog_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "errorLog");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorLog_Missing() {
        setErrorLog_Missing(null);
    }

    public void setErrorLog_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setErrorLog_Missing("errorLog", opLambda, null);
    }

    public void setErrorLog_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        setErrorLog_Missing("errorLog", opLambda, aggsLambda);
    }

    public void setErrorLog_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "errorLog");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setErrorName_Terms() {
        setErrorName_Terms(null);
    }

    public void setErrorName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setErrorName_Terms("errorName", opLambda, null);
    }

    public void setErrorName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        setErrorName_Terms("errorName", opLambda, aggsLambda);
    }

    public void setErrorName_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "errorName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setErrorName_SignificantTerms() {
        setErrorName_SignificantTerms(null);
    }

    public void setErrorName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setErrorName_SignificantTerms("errorName", opLambda, null);
    }

    public void setErrorName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        setErrorName_SignificantTerms("errorName", opLambda, aggsLambda);
    }

    public void setErrorName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "errorName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setErrorName_IpRange() {
        setErrorName_IpRange(null);
    }

    public void setErrorName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setErrorName_IpRange("errorName", opLambda, null);
    }

    public void setErrorName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        setErrorName_IpRange("errorName", opLambda, aggsLambda);
    }

    public void setErrorName_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "errorName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setErrorName_Count() {
        setErrorName_Count(null);
    }

    public void setErrorName_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setErrorName_Count("errorName", opLambda);
    }

    public void setErrorName_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "errorName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_Cardinality() {
        setErrorName_Cardinality(null);
    }

    public void setErrorName_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setErrorName_Cardinality("errorName", opLambda);
    }

    public void setErrorName_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "errorName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setErrorName_Missing() {
        setErrorName_Missing(null);
    }

    public void setErrorName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setErrorName_Missing("errorName", opLambda, null);
    }

    public void setErrorName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        setErrorName_Missing("errorName", opLambda, aggsLambda);
    }

    public void setErrorName_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "errorName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLastAccessTime_Avg() {
        setLastAccessTime_Avg(null);
    }

    public void setLastAccessTime_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setLastAccessTime_Avg("lastAccessTime", opLambda);
    }

    public void setLastAccessTime_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "lastAccessTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_Max() {
        setLastAccessTime_Max(null);
    }

    public void setLastAccessTime_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setLastAccessTime_Max("lastAccessTime", opLambda);
    }

    public void setLastAccessTime_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "lastAccessTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_Min() {
        setLastAccessTime_Min(null);
    }

    public void setLastAccessTime_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setLastAccessTime_Min("lastAccessTime", opLambda);
    }

    public void setLastAccessTime_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "lastAccessTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_Sum() {
        setLastAccessTime_Sum(null);
    }

    public void setLastAccessTime_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setLastAccessTime_Sum("lastAccessTime", opLambda);
    }

    public void setLastAccessTime_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "lastAccessTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_ExtendedStats() {
        setLastAccessTime_ExtendedStats(null);
    }

    public void setLastAccessTime_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setLastAccessTime_ExtendedStats("lastAccessTime", opLambda);
    }

    public void setLastAccessTime_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "lastAccessTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_Stats() {
        setLastAccessTime_Stats(null);
    }

    public void setLastAccessTime_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setLastAccessTime_Stats("lastAccessTime", opLambda);
    }

    public void setLastAccessTime_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "lastAccessTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_Percentiles() {
        setLastAccessTime_Percentiles(null);
    }

    public void setLastAccessTime_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setLastAccessTime_Percentiles("lastAccessTime", opLambda);
    }

    public void setLastAccessTime_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "lastAccessTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_PercentileRanks(double[] values) {
        setLastAccessTime_PercentileRanks(values, null);
    }

    public void setLastAccessTime_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setLastAccessTime_PercentileRanks("lastAccessTime", values, opLambda);
    }

    public void setLastAccessTime_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "lastAccessTime", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_Histogram() {
        setLastAccessTime_Histogram(null);
    }

    public void setLastAccessTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setLastAccessTime_Histogram("lastAccessTime", opLambda, null);
    }

    public void setLastAccessTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        setLastAccessTime_Histogram("lastAccessTime", opLambda, aggsLambda);
    }

    public void setLastAccessTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "lastAccessTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLastAccessTime_Range() {
        setLastAccessTime_Range(null);
    }

    public void setLastAccessTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setLastAccessTime_Range("lastAccessTime", opLambda, null);
    }

    public void setLastAccessTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        setLastAccessTime_Range("lastAccessTime", opLambda, aggsLambda);
    }

    public void setLastAccessTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "lastAccessTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLastAccessTime_Count() {
        setLastAccessTime_Count(null);
    }

    public void setLastAccessTime_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setLastAccessTime_Count("lastAccessTime", opLambda);
    }

    public void setLastAccessTime_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "lastAccessTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_Cardinality() {
        setLastAccessTime_Cardinality(null);
    }

    public void setLastAccessTime_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setLastAccessTime_Cardinality("lastAccessTime", opLambda);
    }

    public void setLastAccessTime_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "lastAccessTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLastAccessTime_Missing() {
        setLastAccessTime_Missing(null);
    }

    public void setLastAccessTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setLastAccessTime_Missing("lastAccessTime", opLambda, null);
    }

    public void setLastAccessTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        setLastAccessTime_Missing("lastAccessTime", opLambda, aggsLambda);
    }

    public void setLastAccessTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "lastAccessTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setThreadName_Terms() {
        setThreadName_Terms(null);
    }

    public void setThreadName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setThreadName_Terms("threadName", opLambda, null);
    }

    public void setThreadName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        setThreadName_Terms("threadName", opLambda, aggsLambda);
    }

    public void setThreadName_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "threadName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setThreadName_SignificantTerms() {
        setThreadName_SignificantTerms(null);
    }

    public void setThreadName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setThreadName_SignificantTerms("threadName", opLambda, null);
    }

    public void setThreadName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        setThreadName_SignificantTerms("threadName", opLambda, aggsLambda);
    }

    public void setThreadName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "threadName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setThreadName_IpRange() {
        setThreadName_IpRange(null);
    }

    public void setThreadName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setThreadName_IpRange("threadName", opLambda, null);
    }

    public void setThreadName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        setThreadName_IpRange("threadName", opLambda, aggsLambda);
    }

    public void setThreadName_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "threadName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setThreadName_Count() {
        setThreadName_Count(null);
    }

    public void setThreadName_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setThreadName_Count("threadName", opLambda);
    }

    public void setThreadName_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "threadName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_Cardinality() {
        setThreadName_Cardinality(null);
    }

    public void setThreadName_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setThreadName_Cardinality("threadName", opLambda);
    }

    public void setThreadName_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "threadName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setThreadName_Missing() {
        setThreadName_Missing(null);
    }

    public void setThreadName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setThreadName_Missing("threadName", opLambda, null);
    }

    public void setThreadName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        setThreadName_Missing("threadName", opLambda, aggsLambda);
    }

    public void setThreadName_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "threadName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUrl_Terms() {
        setUrl_Terms(null);
    }

    public void setUrl_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setUrl_Terms("url", opLambda, null);
    }

    public void setUrl_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        setUrl_Terms("url", opLambda, aggsLambda);
    }

    public void setUrl_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUrl_SignificantTerms() {
        setUrl_SignificantTerms(null);
    }

    public void setUrl_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setUrl_SignificantTerms("url", opLambda, null);
    }

    public void setUrl_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        setUrl_SignificantTerms("url", opLambda, aggsLambda);
    }

    public void setUrl_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUrl_IpRange() {
        setUrl_IpRange(null);
    }

    public void setUrl_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setUrl_IpRange("url", opLambda, null);
    }

    public void setUrl_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        setUrl_IpRange("url", opLambda, aggsLambda);
    }

    public void setUrl_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUrl_Count() {
        setUrl_Count(null);
    }

    public void setUrl_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setUrl_Count("url", opLambda);
    }

    public void setUrl_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Cardinality() {
        setUrl_Cardinality(null);
    }

    public void setUrl_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setUrl_Cardinality("url", opLambda);
    }

    public void setUrl_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Missing() {
        setUrl_Missing(null);
    }

    public void setUrl_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setUrl_Missing("url", opLambda, null);
    }

    public void setUrl_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFailureUrlCA> aggsLambda) {
        setUrl_Missing("url", opLambda, aggsLambda);
    }

    public void setUrl_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFailureUrlCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FailureUrlCA ca = new FailureUrlCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
