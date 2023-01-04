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
package org.codelibs.fess.es.log.cbean.ca.bs;

import org.codelibs.fess.es.log.allcommon.EsAbstractConditionAggregation;
import org.codelibs.fess.es.log.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.log.cbean.ca.ClickLogCA;
import org.codelibs.fess.es.log.cbean.cq.ClickLogCQ;
import org.codelibs.fess.es.log.cbean.cq.bs.BsClickLogCQ;
import org.opensearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.opensearch.search.aggregations.bucket.global.GlobalAggregationBuilder;
import org.opensearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.opensearch.search.aggregations.bucket.histogram.HistogramAggregationBuilder;
import org.opensearch.search.aggregations.bucket.missing.MissingAggregationBuilder;
import org.opensearch.search.aggregations.bucket.range.DateRangeAggregationBuilder;
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
public abstract class BsClickLogCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsClickLogCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        ClickLogCQ cq = new ClickLogCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
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

    public void setUrlId_Terms() {
        setUrlId_Terms(null);
    }

    public void setUrlId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setUrlId_Terms("urlId", opLambda, null);
    }

    public void setUrlId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setUrlId_Terms("urlId", opLambda, aggsLambda);
    }

    public void setUrlId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "urlId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUrlId_SignificantTerms() {
        setUrlId_SignificantTerms(null);
    }

    public void setUrlId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setUrlId_SignificantTerms("urlId", opLambda, null);
    }

    public void setUrlId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        setUrlId_SignificantTerms("urlId", opLambda, aggsLambda);
    }

    public void setUrlId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "urlId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUrlId_IpRange() {
        setUrlId_IpRange(null);
    }

    public void setUrlId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setUrlId_IpRange("urlId", opLambda, null);
    }

    public void setUrlId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setUrlId_IpRange("urlId", opLambda, aggsLambda);
    }

    public void setUrlId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "urlId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUrlId_Count() {
        setUrlId_Count(null);
    }

    public void setUrlId_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setUrlId_Count("urlId", opLambda);
    }

    public void setUrlId_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "urlId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrlId_Cardinality() {
        setUrlId_Cardinality(null);
    }

    public void setUrlId_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setUrlId_Cardinality("urlId", opLambda);
    }

    public void setUrlId_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "urlId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrlId_Missing() {
        setUrlId_Missing(null);
    }

    public void setUrlId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setUrlId_Missing("urlId", opLambda, null);
    }

    public void setUrlId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setUrlId_Missing("urlId", opLambda, aggsLambda);
    }

    public void setUrlId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "urlId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDocId_Terms() {
        setDocId_Terms(null);
    }

    public void setDocId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setDocId_Terms("docId", opLambda, null);
    }

    public void setDocId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setDocId_Terms("docId", opLambda, aggsLambda);
    }

    public void setDocId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDocId_SignificantTerms() {
        setDocId_SignificantTerms(null);
    }

    public void setDocId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setDocId_SignificantTerms("docId", opLambda, null);
    }

    public void setDocId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        setDocId_SignificantTerms("docId", opLambda, aggsLambda);
    }

    public void setDocId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDocId_IpRange() {
        setDocId_IpRange(null);
    }

    public void setDocId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setDocId_IpRange("docId", opLambda, null);
    }

    public void setDocId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setDocId_IpRange("docId", opLambda, aggsLambda);
    }

    public void setDocId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDocId_Count() {
        setDocId_Count(null);
    }

    public void setDocId_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setDocId_Count("docId", opLambda);
    }

    public void setDocId_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_Cardinality() {
        setDocId_Cardinality(null);
    }

    public void setDocId_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setDocId_Cardinality("docId", opLambda);
    }

    public void setDocId_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_Missing() {
        setDocId_Missing(null);
    }

    public void setDocId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setDocId_Missing("docId", opLambda, null);
    }

    public void setDocId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setDocId_Missing("docId", opLambda, aggsLambda);
    }

    public void setDocId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setOrder_Avg() {
        setOrder_Avg(null);
    }

    public void setOrder_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setOrder_Avg("order", opLambda);
    }

    public void setOrder_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Max() {
        setOrder_Max(null);
    }

    public void setOrder_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setOrder_Max("order", opLambda);
    }

    public void setOrder_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Min() {
        setOrder_Min(null);
    }

    public void setOrder_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setOrder_Min("order", opLambda);
    }

    public void setOrder_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Sum() {
        setOrder_Sum(null);
    }

    public void setOrder_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setOrder_Sum("order", opLambda);
    }

    public void setOrder_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_ExtendedStats() {
        setOrder_ExtendedStats(null);
    }

    public void setOrder_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setOrder_ExtendedStats("order", opLambda);
    }

    public void setOrder_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Stats() {
        setOrder_Stats(null);
    }

    public void setOrder_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setOrder_Stats("order", opLambda);
    }

    public void setOrder_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Percentiles() {
        setOrder_Percentiles(null);
    }

    public void setOrder_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setOrder_Percentiles("order", opLambda);
    }

    public void setOrder_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_PercentileRanks(double[] values) {
        setOrder_PercentileRanks(values, null);
    }

    public void setOrder_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setOrder_PercentileRanks("order", values, opLambda);
    }

    public void setOrder_PercentileRanks(String name, double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "order", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Histogram() {
        setOrder_Histogram(null);
    }

    public void setOrder_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setOrder_Histogram("order", opLambda, null);
    }

    public void setOrder_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setOrder_Histogram("order", opLambda, aggsLambda);
    }

    public void setOrder_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setOrder_Range() {
        setOrder_Range(null);
    }

    public void setOrder_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setOrder_Range("order", opLambda, null);
    }

    public void setOrder_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setOrder_Range("order", opLambda, aggsLambda);
    }

    public void setOrder_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setOrder_Count() {
        setOrder_Count(null);
    }

    public void setOrder_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setOrder_Count("order", opLambda);
    }

    public void setOrder_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Cardinality() {
        setOrder_Cardinality(null);
    }

    public void setOrder_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setOrder_Cardinality("order", opLambda);
    }

    public void setOrder_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Missing() {
        setOrder_Missing(null);
    }

    public void setOrder_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setOrder_Missing("order", opLambda, null);
    }

    public void setOrder_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setOrder_Missing("order", opLambda, aggsLambda);
    }

    public void setOrder_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQueryId_Terms() {
        setQueryId_Terms(null);
    }

    public void setQueryId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setQueryId_Terms("queryId", opLambda, null);
    }

    public void setQueryId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setQueryId_Terms("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQueryId_SignificantTerms() {
        setQueryId_SignificantTerms(null);
    }

    public void setQueryId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setQueryId_SignificantTerms("queryId", opLambda, null);
    }

    public void setQueryId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        setQueryId_SignificantTerms("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQueryId_IpRange() {
        setQueryId_IpRange(null);
    }

    public void setQueryId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setQueryId_IpRange("queryId", opLambda, null);
    }

    public void setQueryId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setQueryId_IpRange("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQueryId_Count() {
        setQueryId_Count(null);
    }

    public void setQueryId_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setQueryId_Count("queryId", opLambda);
    }

    public void setQueryId_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_Cardinality() {
        setQueryId_Cardinality(null);
    }

    public void setQueryId_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setQueryId_Cardinality("queryId", opLambda);
    }

    public void setQueryId_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_Missing() {
        setQueryId_Missing(null);
    }

    public void setQueryId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setQueryId_Missing("queryId", opLambda, null);
    }

    public void setQueryId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setQueryId_Missing("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQueryRequestedAt_DateRange() {
        setQueryRequestedAt_DateRange(null);
    }

    public void setQueryRequestedAt_DateRange(ConditionOptionCall<DateRangeAggregationBuilder> opLambda) {
        setQueryRequestedAt_DateRange("queryRequestedAt", opLambda, null);
    }

    public void setQueryRequestedAt_DateRange(ConditionOptionCall<DateRangeAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        setQueryRequestedAt_DateRange("queryRequestedAt", opLambda, aggsLambda);
    }

    public void setQueryRequestedAt_DateRange(String name, ConditionOptionCall<DateRangeAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        DateRangeAggregationBuilder builder = regDateRangeA(name, "queryRequestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQueryRequestedAt_DateHistogram() {
        setQueryRequestedAt_DateHistogram(null);
    }

    public void setQueryRequestedAt_DateHistogram(ConditionOptionCall<DateHistogramAggregationBuilder> opLambda) {
        setQueryRequestedAt_DateHistogram("queryRequestedAt", opLambda, null);
    }

    public void setQueryRequestedAt_DateHistogram(ConditionOptionCall<DateHistogramAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        setQueryRequestedAt_DateHistogram("queryRequestedAt", opLambda, aggsLambda);
    }

    public void setQueryRequestedAt_DateHistogram(String name, ConditionOptionCall<DateHistogramAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        DateHistogramAggregationBuilder builder = regDateHistogramA(name, "queryRequestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQueryRequestedAt_Count() {
        setQueryRequestedAt_Count(null);
    }

    public void setQueryRequestedAt_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setQueryRequestedAt_Count("queryRequestedAt", opLambda);
    }

    public void setQueryRequestedAt_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "queryRequestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_Cardinality() {
        setQueryRequestedAt_Cardinality(null);
    }

    public void setQueryRequestedAt_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setQueryRequestedAt_Cardinality("queryRequestedAt", opLambda);
    }

    public void setQueryRequestedAt_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "queryRequestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_Missing() {
        setQueryRequestedAt_Missing(null);
    }

    public void setQueryRequestedAt_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setQueryRequestedAt_Missing("queryRequestedAt", opLambda, null);
    }

    public void setQueryRequestedAt_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        setQueryRequestedAt_Missing("queryRequestedAt", opLambda, aggsLambda);
    }

    public void setQueryRequestedAt_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "queryRequestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRequestedAt_DateRange() {
        setRequestedAt_DateRange(null);
    }

    public void setRequestedAt_DateRange(ConditionOptionCall<DateRangeAggregationBuilder> opLambda) {
        setRequestedAt_DateRange("requestedAt", opLambda, null);
    }

    public void setRequestedAt_DateRange(ConditionOptionCall<DateRangeAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setRequestedAt_DateRange("requestedAt", opLambda, aggsLambda);
    }

    public void setRequestedAt_DateRange(String name, ConditionOptionCall<DateRangeAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        DateRangeAggregationBuilder builder = regDateRangeA(name, "requestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRequestedAt_DateHistogram() {
        setRequestedAt_DateHistogram(null);
    }

    public void setRequestedAt_DateHistogram(ConditionOptionCall<DateHistogramAggregationBuilder> opLambda) {
        setRequestedAt_DateHistogram("requestedAt", opLambda, null);
    }

    public void setRequestedAt_DateHistogram(ConditionOptionCall<DateHistogramAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        setRequestedAt_DateHistogram("requestedAt", opLambda, aggsLambda);
    }

    public void setRequestedAt_DateHistogram(String name, ConditionOptionCall<DateHistogramAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        DateHistogramAggregationBuilder builder = regDateHistogramA(name, "requestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRequestedAt_Count() {
        setRequestedAt_Count(null);
    }

    public void setRequestedAt_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setRequestedAt_Count("requestedAt", opLambda);
    }

    public void setRequestedAt_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "requestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_Cardinality() {
        setRequestedAt_Cardinality(null);
    }

    public void setRequestedAt_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setRequestedAt_Cardinality("requestedAt", opLambda);
    }

    public void setRequestedAt_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "requestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_Missing() {
        setRequestedAt_Missing(null);
    }

    public void setRequestedAt_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setRequestedAt_Missing("requestedAt", opLambda, null);
    }

    public void setRequestedAt_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setRequestedAt_Missing("requestedAt", opLambda, aggsLambda);
    }

    public void setRequestedAt_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "requestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
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

    public void setUrl_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setUrl_Terms("url", opLambda, aggsLambda);
    }

    public void setUrl_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
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
            OperatorCall<BsClickLogCA> aggsLambda) {
        setUrl_SignificantTerms("url", opLambda, aggsLambda);
    }

    public void setUrl_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
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

    public void setUrl_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setUrl_IpRange("url", opLambda, aggsLambda);
    }

    public void setUrl_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
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

    public void setUrl_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setUrl_Missing("url", opLambda, aggsLambda);
    }

    public void setUrl_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserSessionId_Terms() {
        setUserSessionId_Terms(null);
    }

    public void setUserSessionId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setUserSessionId_Terms("userSessionId", opLambda, null);
    }

    public void setUserSessionId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setUserSessionId_Terms("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserSessionId_SignificantTerms() {
        setUserSessionId_SignificantTerms(null);
    }

    public void setUserSessionId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setUserSessionId_SignificantTerms("userSessionId", opLambda, null);
    }

    public void setUserSessionId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        setUserSessionId_SignificantTerms("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserSessionId_IpRange() {
        setUserSessionId_IpRange(null);
    }

    public void setUserSessionId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setUserSessionId_IpRange("userSessionId", opLambda, null);
    }

    public void setUserSessionId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setUserSessionId_IpRange("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserSessionId_Count() {
        setUserSessionId_Count(null);
    }

    public void setUserSessionId_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setUserSessionId_Count("userSessionId", opLambda);
    }

    public void setUserSessionId_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Cardinality() {
        setUserSessionId_Cardinality(null);
    }

    public void setUserSessionId_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setUserSessionId_Cardinality("userSessionId", opLambda);
    }

    public void setUserSessionId_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Missing() {
        setUserSessionId_Missing(null);
    }

    public void setUserSessionId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setUserSessionId_Missing("userSessionId", opLambda, null);
    }

    public void setUserSessionId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setUserSessionId_Missing("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
