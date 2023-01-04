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
import org.codelibs.fess.es.log.cbean.ca.SearchLogCA;
import org.codelibs.fess.es.log.cbean.cq.SearchLogCQ;
import org.codelibs.fess.es.log.cbean.cq.bs.BsSearchLogCQ;
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
public abstract class BsSearchLogCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsSearchLogCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        SearchLogCQ cq = new SearchLogCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
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

    public void setAccessType_Terms() {
        setAccessType_Terms(null);
    }

    public void setAccessType_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setAccessType_Terms("accessType", opLambda, null);
    }

    public void setAccessType_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setAccessType_Terms("accessType", opLambda, aggsLambda);
    }

    public void setAccessType_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "accessType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setAccessType_SignificantTerms() {
        setAccessType_SignificantTerms(null);
    }

    public void setAccessType_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setAccessType_SignificantTerms("accessType", opLambda, null);
    }

    public void setAccessType_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setAccessType_SignificantTerms("accessType", opLambda, aggsLambda);
    }

    public void setAccessType_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "accessType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setAccessType_IpRange() {
        setAccessType_IpRange(null);
    }

    public void setAccessType_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setAccessType_IpRange("accessType", opLambda, null);
    }

    public void setAccessType_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setAccessType_IpRange("accessType", opLambda, aggsLambda);
    }

    public void setAccessType_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "accessType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setAccessType_Count() {
        setAccessType_Count(null);
    }

    public void setAccessType_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setAccessType_Count("accessType", opLambda);
    }

    public void setAccessType_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "accessType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Cardinality() {
        setAccessType_Cardinality(null);
    }

    public void setAccessType_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setAccessType_Cardinality("accessType", opLambda);
    }

    public void setAccessType_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "accessType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Missing() {
        setAccessType_Missing(null);
    }

    public void setAccessType_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setAccessType_Missing("accessType", opLambda, null);
    }

    public void setAccessType_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setAccessType_Missing("accessType", opLambda, aggsLambda);
    }

    public void setAccessType_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "accessType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setClientIp_Terms() {
        setClientIp_Terms(null);
    }

    public void setClientIp_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setClientIp_Terms("clientIp", opLambda, null);
    }

    public void setClientIp_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setClientIp_Terms("clientIp", opLambda, aggsLambda);
    }

    public void setClientIp_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "clientIp");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setClientIp_SignificantTerms() {
        setClientIp_SignificantTerms(null);
    }

    public void setClientIp_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setClientIp_SignificantTerms("clientIp", opLambda, null);
    }

    public void setClientIp_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setClientIp_SignificantTerms("clientIp", opLambda, aggsLambda);
    }

    public void setClientIp_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "clientIp");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setClientIp_IpRange() {
        setClientIp_IpRange(null);
    }

    public void setClientIp_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setClientIp_IpRange("clientIp", opLambda, null);
    }

    public void setClientIp_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setClientIp_IpRange("clientIp", opLambda, aggsLambda);
    }

    public void setClientIp_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "clientIp");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setClientIp_Count() {
        setClientIp_Count(null);
    }

    public void setClientIp_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setClientIp_Count("clientIp", opLambda);
    }

    public void setClientIp_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "clientIp");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_Cardinality() {
        setClientIp_Cardinality(null);
    }

    public void setClientIp_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setClientIp_Cardinality("clientIp", opLambda);
    }

    public void setClientIp_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "clientIp");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_Missing() {
        setClientIp_Missing(null);
    }

    public void setClientIp_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setClientIp_Missing("clientIp", opLambda, null);
    }

    public void setClientIp_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setClientIp_Missing("clientIp", opLambda, aggsLambda);
    }

    public void setClientIp_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "clientIp");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHitCount_Avg() {
        setHitCount_Avg(null);
    }

    public void setHitCount_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setHitCount_Avg("hitCount", opLambda);
    }

    public void setHitCount_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Max() {
        setHitCount_Max(null);
    }

    public void setHitCount_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setHitCount_Max("hitCount", opLambda);
    }

    public void setHitCount_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Min() {
        setHitCount_Min(null);
    }

    public void setHitCount_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setHitCount_Min("hitCount", opLambda);
    }

    public void setHitCount_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Sum() {
        setHitCount_Sum(null);
    }

    public void setHitCount_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setHitCount_Sum("hitCount", opLambda);
    }

    public void setHitCount_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_ExtendedStats() {
        setHitCount_ExtendedStats(null);
    }

    public void setHitCount_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setHitCount_ExtendedStats("hitCount", opLambda);
    }

    public void setHitCount_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Stats() {
        setHitCount_Stats(null);
    }

    public void setHitCount_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setHitCount_Stats("hitCount", opLambda);
    }

    public void setHitCount_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Percentiles() {
        setHitCount_Percentiles(null);
    }

    public void setHitCount_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setHitCount_Percentiles("hitCount", opLambda);
    }

    public void setHitCount_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_PercentileRanks(double[] values) {
        setHitCount_PercentileRanks(values, null);
    }

    public void setHitCount_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setHitCount_PercentileRanks("hitCount", values, opLambda);
    }

    public void setHitCount_PercentileRanks(String name, double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "hitCount", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Histogram() {
        setHitCount_Histogram(null);
    }

    public void setHitCount_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setHitCount_Histogram("hitCount", opLambda, null);
    }

    public void setHitCount_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setHitCount_Histogram("hitCount", opLambda, aggsLambda);
    }

    public void setHitCount_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHitCount_Range() {
        setHitCount_Range(null);
    }

    public void setHitCount_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setHitCount_Range("hitCount", opLambda, null);
    }

    public void setHitCount_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setHitCount_Range("hitCount", opLambda, aggsLambda);
    }

    public void setHitCount_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHitCount_Count() {
        setHitCount_Count(null);
    }

    public void setHitCount_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setHitCount_Count("hitCount", opLambda);
    }

    public void setHitCount_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Cardinality() {
        setHitCount_Cardinality(null);
    }

    public void setHitCount_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setHitCount_Cardinality("hitCount", opLambda);
    }

    public void setHitCount_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Missing() {
        setHitCount_Missing(null);
    }

    public void setHitCount_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setHitCount_Missing("hitCount", opLambda, null);
    }

    public void setHitCount_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setHitCount_Missing("hitCount", opLambda, aggsLambda);
    }

    public void setHitCount_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHitCountRelation_Terms() {
        setHitCountRelation_Terms(null);
    }

    public void setHitCountRelation_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setHitCountRelation_Terms("hitCountRelation", opLambda, null);
    }

    public void setHitCountRelation_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setHitCountRelation_Terms("hitCountRelation", opLambda, aggsLambda);
    }

    public void setHitCountRelation_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "hitCountRelation");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHitCountRelation_SignificantTerms() {
        setHitCountRelation_SignificantTerms(null);
    }

    public void setHitCountRelation_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setHitCountRelation_SignificantTerms("hitCountRelation", opLambda, null);
    }

    public void setHitCountRelation_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setHitCountRelation_SignificantTerms("hitCountRelation", opLambda, aggsLambda);
    }

    public void setHitCountRelation_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "hitCountRelation");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHitCountRelation_IpRange() {
        setHitCountRelation_IpRange(null);
    }

    public void setHitCountRelation_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setHitCountRelation_IpRange("hitCountRelation", opLambda, null);
    }

    public void setHitCountRelation_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setHitCountRelation_IpRange("hitCountRelation", opLambda, aggsLambda);
    }

    public void setHitCountRelation_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "hitCountRelation");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHitCountRelation_Count() {
        setHitCountRelation_Count(null);
    }

    public void setHitCountRelation_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setHitCountRelation_Count("hitCountRelation", opLambda);
    }

    public void setHitCountRelation_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "hitCountRelation");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCountRelation_Cardinality() {
        setHitCountRelation_Cardinality(null);
    }

    public void setHitCountRelation_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setHitCountRelation_Cardinality("hitCountRelation", opLambda);
    }

    public void setHitCountRelation_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "hitCountRelation");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCountRelation_Missing() {
        setHitCountRelation_Missing(null);
    }

    public void setHitCountRelation_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setHitCountRelation_Missing("hitCountRelation", opLambda, null);
    }

    public void setHitCountRelation_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setHitCountRelation_Missing("hitCountRelation", opLambda, aggsLambda);
    }

    public void setHitCountRelation_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "hitCountRelation");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLanguages_Terms() {
        setLanguages_Terms(null);
    }

    public void setLanguages_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setLanguages_Terms("languages", opLambda, null);
    }

    public void setLanguages_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setLanguages_Terms("languages", opLambda, aggsLambda);
    }

    public void setLanguages_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "languages");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLanguages_SignificantTerms() {
        setLanguages_SignificantTerms(null);
    }

    public void setLanguages_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setLanguages_SignificantTerms("languages", opLambda, null);
    }

    public void setLanguages_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setLanguages_SignificantTerms("languages", opLambda, aggsLambda);
    }

    public void setLanguages_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "languages");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLanguages_IpRange() {
        setLanguages_IpRange(null);
    }

    public void setLanguages_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setLanguages_IpRange("languages", opLambda, null);
    }

    public void setLanguages_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setLanguages_IpRange("languages", opLambda, aggsLambda);
    }

    public void setLanguages_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "languages");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLanguages_Count() {
        setLanguages_Count(null);
    }

    public void setLanguages_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setLanguages_Count("languages", opLambda);
    }

    public void setLanguages_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "languages");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLanguages_Cardinality() {
        setLanguages_Cardinality(null);
    }

    public void setLanguages_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setLanguages_Cardinality("languages", opLambda);
    }

    public void setLanguages_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "languages");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLanguages_Missing() {
        setLanguages_Missing(null);
    }

    public void setLanguages_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setLanguages_Missing("languages", opLambda, null);
    }

    public void setLanguages_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setLanguages_Missing("languages", opLambda, aggsLambda);
    }

    public void setLanguages_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "languages");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
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

    public void setQueryId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryId_Terms("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
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
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryId_SignificantTerms("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
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

    public void setQueryId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryId_IpRange("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
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

    public void setQueryId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryId_Missing("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQueryOffset_Avg() {
        setQueryOffset_Avg(null);
    }

    public void setQueryOffset_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setQueryOffset_Avg("queryOffset", opLambda);
    }

    public void setQueryOffset_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Max() {
        setQueryOffset_Max(null);
    }

    public void setQueryOffset_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setQueryOffset_Max("queryOffset", opLambda);
    }

    public void setQueryOffset_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Min() {
        setQueryOffset_Min(null);
    }

    public void setQueryOffset_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setQueryOffset_Min("queryOffset", opLambda);
    }

    public void setQueryOffset_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Sum() {
        setQueryOffset_Sum(null);
    }

    public void setQueryOffset_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setQueryOffset_Sum("queryOffset", opLambda);
    }

    public void setQueryOffset_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_ExtendedStats() {
        setQueryOffset_ExtendedStats(null);
    }

    public void setQueryOffset_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setQueryOffset_ExtendedStats("queryOffset", opLambda);
    }

    public void setQueryOffset_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Stats() {
        setQueryOffset_Stats(null);
    }

    public void setQueryOffset_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setQueryOffset_Stats("queryOffset", opLambda);
    }

    public void setQueryOffset_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Percentiles() {
        setQueryOffset_Percentiles(null);
    }

    public void setQueryOffset_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setQueryOffset_Percentiles("queryOffset", opLambda);
    }

    public void setQueryOffset_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_PercentileRanks(double[] values) {
        setQueryOffset_PercentileRanks(values, null);
    }

    public void setQueryOffset_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setQueryOffset_PercentileRanks("queryOffset", values, opLambda);
    }

    public void setQueryOffset_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "queryOffset", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Histogram() {
        setQueryOffset_Histogram(null);
    }

    public void setQueryOffset_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setQueryOffset_Histogram("queryOffset", opLambda, null);
    }

    public void setQueryOffset_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryOffset_Histogram("queryOffset", opLambda, aggsLambda);
    }

    public void setQueryOffset_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQueryOffset_Range() {
        setQueryOffset_Range(null);
    }

    public void setQueryOffset_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setQueryOffset_Range("queryOffset", opLambda, null);
    }

    public void setQueryOffset_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryOffset_Range("queryOffset", opLambda, aggsLambda);
    }

    public void setQueryOffset_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQueryOffset_Count() {
        setQueryOffset_Count(null);
    }

    public void setQueryOffset_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setQueryOffset_Count("queryOffset", opLambda);
    }

    public void setQueryOffset_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Cardinality() {
        setQueryOffset_Cardinality(null);
    }

    public void setQueryOffset_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setQueryOffset_Cardinality("queryOffset", opLambda);
    }

    public void setQueryOffset_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Missing() {
        setQueryOffset_Missing(null);
    }

    public void setQueryOffset_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setQueryOffset_Missing("queryOffset", opLambda, null);
    }

    public void setQueryOffset_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryOffset_Missing("queryOffset", opLambda, aggsLambda);
    }

    public void setQueryOffset_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQueryPageSize_Avg() {
        setQueryPageSize_Avg(null);
    }

    public void setQueryPageSize_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setQueryPageSize_Avg("queryPageSize", opLambda);
    }

    public void setQueryPageSize_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Max() {
        setQueryPageSize_Max(null);
    }

    public void setQueryPageSize_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setQueryPageSize_Max("queryPageSize", opLambda);
    }

    public void setQueryPageSize_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Min() {
        setQueryPageSize_Min(null);
    }

    public void setQueryPageSize_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setQueryPageSize_Min("queryPageSize", opLambda);
    }

    public void setQueryPageSize_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Sum() {
        setQueryPageSize_Sum(null);
    }

    public void setQueryPageSize_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setQueryPageSize_Sum("queryPageSize", opLambda);
    }

    public void setQueryPageSize_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_ExtendedStats() {
        setQueryPageSize_ExtendedStats(null);
    }

    public void setQueryPageSize_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setQueryPageSize_ExtendedStats("queryPageSize", opLambda);
    }

    public void setQueryPageSize_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Stats() {
        setQueryPageSize_Stats(null);
    }

    public void setQueryPageSize_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setQueryPageSize_Stats("queryPageSize", opLambda);
    }

    public void setQueryPageSize_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Percentiles() {
        setQueryPageSize_Percentiles(null);
    }

    public void setQueryPageSize_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setQueryPageSize_Percentiles("queryPageSize", opLambda);
    }

    public void setQueryPageSize_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_PercentileRanks(double[] values) {
        setQueryPageSize_PercentileRanks(values, null);
    }

    public void setQueryPageSize_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setQueryPageSize_PercentileRanks("queryPageSize", values, opLambda);
    }

    public void setQueryPageSize_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "queryPageSize", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Histogram() {
        setQueryPageSize_Histogram(null);
    }

    public void setQueryPageSize_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setQueryPageSize_Histogram("queryPageSize", opLambda, null);
    }

    public void setQueryPageSize_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryPageSize_Histogram("queryPageSize", opLambda, aggsLambda);
    }

    public void setQueryPageSize_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQueryPageSize_Range() {
        setQueryPageSize_Range(null);
    }

    public void setQueryPageSize_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setQueryPageSize_Range("queryPageSize", opLambda, null);
    }

    public void setQueryPageSize_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryPageSize_Range("queryPageSize", opLambda, aggsLambda);
    }

    public void setQueryPageSize_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQueryPageSize_Count() {
        setQueryPageSize_Count(null);
    }

    public void setQueryPageSize_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setQueryPageSize_Count("queryPageSize", opLambda);
    }

    public void setQueryPageSize_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Cardinality() {
        setQueryPageSize_Cardinality(null);
    }

    public void setQueryPageSize_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setQueryPageSize_Cardinality("queryPageSize", opLambda);
    }

    public void setQueryPageSize_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Missing() {
        setQueryPageSize_Missing(null);
    }

    public void setQueryPageSize_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setQueryPageSize_Missing("queryPageSize", opLambda, null);
    }

    public void setQueryPageSize_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryPageSize_Missing("queryPageSize", opLambda, aggsLambda);
    }

    public void setQueryPageSize_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQueryTime_Avg() {
        setQueryTime_Avg(null);
    }

    public void setQueryTime_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setQueryTime_Avg("queryTime", opLambda);
    }

    public void setQueryTime_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Max() {
        setQueryTime_Max(null);
    }

    public void setQueryTime_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setQueryTime_Max("queryTime", opLambda);
    }

    public void setQueryTime_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Min() {
        setQueryTime_Min(null);
    }

    public void setQueryTime_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setQueryTime_Min("queryTime", opLambda);
    }

    public void setQueryTime_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Sum() {
        setQueryTime_Sum(null);
    }

    public void setQueryTime_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setQueryTime_Sum("queryTime", opLambda);
    }

    public void setQueryTime_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_ExtendedStats() {
        setQueryTime_ExtendedStats(null);
    }

    public void setQueryTime_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setQueryTime_ExtendedStats("queryTime", opLambda);
    }

    public void setQueryTime_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Stats() {
        setQueryTime_Stats(null);
    }

    public void setQueryTime_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setQueryTime_Stats("queryTime", opLambda);
    }

    public void setQueryTime_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Percentiles() {
        setQueryTime_Percentiles(null);
    }

    public void setQueryTime_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setQueryTime_Percentiles("queryTime", opLambda);
    }

    public void setQueryTime_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_PercentileRanks(double[] values) {
        setQueryTime_PercentileRanks(values, null);
    }

    public void setQueryTime_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setQueryTime_PercentileRanks("queryTime", values, opLambda);
    }

    public void setQueryTime_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "queryTime", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Histogram() {
        setQueryTime_Histogram(null);
    }

    public void setQueryTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setQueryTime_Histogram("queryTime", opLambda, null);
    }

    public void setQueryTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryTime_Histogram("queryTime", opLambda, aggsLambda);
    }

    public void setQueryTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQueryTime_Range() {
        setQueryTime_Range(null);
    }

    public void setQueryTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setQueryTime_Range("queryTime", opLambda, null);
    }

    public void setQueryTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryTime_Range("queryTime", opLambda, aggsLambda);
    }

    public void setQueryTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setQueryTime_Count() {
        setQueryTime_Count(null);
    }

    public void setQueryTime_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setQueryTime_Count("queryTime", opLambda);
    }

    public void setQueryTime_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Cardinality() {
        setQueryTime_Cardinality(null);
    }

    public void setQueryTime_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setQueryTime_Cardinality("queryTime", opLambda);
    }

    public void setQueryTime_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Missing() {
        setQueryTime_Missing(null);
    }

    public void setQueryTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setQueryTime_Missing("queryTime", opLambda, null);
    }

    public void setQueryTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryTime_Missing("queryTime", opLambda, aggsLambda);
    }

    public void setQueryTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setReferer_Terms() {
        setReferer_Terms(null);
    }

    public void setReferer_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setReferer_Terms("referer", opLambda, null);
    }

    public void setReferer_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setReferer_Terms("referer", opLambda, aggsLambda);
    }

    public void setReferer_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "referer");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setReferer_SignificantTerms() {
        setReferer_SignificantTerms(null);
    }

    public void setReferer_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setReferer_SignificantTerms("referer", opLambda, null);
    }

    public void setReferer_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setReferer_SignificantTerms("referer", opLambda, aggsLambda);
    }

    public void setReferer_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "referer");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setReferer_IpRange() {
        setReferer_IpRange(null);
    }

    public void setReferer_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setReferer_IpRange("referer", opLambda, null);
    }

    public void setReferer_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setReferer_IpRange("referer", opLambda, aggsLambda);
    }

    public void setReferer_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "referer");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setReferer_Count() {
        setReferer_Count(null);
    }

    public void setReferer_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setReferer_Count("referer", opLambda);
    }

    public void setReferer_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "referer");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_Cardinality() {
        setReferer_Cardinality(null);
    }

    public void setReferer_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setReferer_Cardinality("referer", opLambda);
    }

    public void setReferer_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "referer");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_Missing() {
        setReferer_Missing(null);
    }

    public void setReferer_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setReferer_Missing("referer", opLambda, null);
    }

    public void setReferer_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setReferer_Missing("referer", opLambda, aggsLambda);
    }

    public void setReferer_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "referer");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
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

    public void setRequestedAt_DateRange(ConditionOptionCall<DateRangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setRequestedAt_DateRange("requestedAt", opLambda, aggsLambda);
    }

    public void setRequestedAt_DateRange(String name, ConditionOptionCall<DateRangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        DateRangeAggregationBuilder builder = regDateRangeA(name, "requestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
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
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setRequestedAt_DateHistogram("requestedAt", opLambda, aggsLambda);
    }

    public void setRequestedAt_DateHistogram(String name, ConditionOptionCall<DateHistogramAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        DateHistogramAggregationBuilder builder = regDateHistogramA(name, "requestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
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

    public void setRequestedAt_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setRequestedAt_Missing("requestedAt", opLambda, aggsLambda);
    }

    public void setRequestedAt_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "requestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setResponseTime_Avg() {
        setResponseTime_Avg(null);
    }

    public void setResponseTime_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setResponseTime_Avg("responseTime", opLambda);
    }

    public void setResponseTime_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Max() {
        setResponseTime_Max(null);
    }

    public void setResponseTime_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setResponseTime_Max("responseTime", opLambda);
    }

    public void setResponseTime_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Min() {
        setResponseTime_Min(null);
    }

    public void setResponseTime_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setResponseTime_Min("responseTime", opLambda);
    }

    public void setResponseTime_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Sum() {
        setResponseTime_Sum(null);
    }

    public void setResponseTime_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setResponseTime_Sum("responseTime", opLambda);
    }

    public void setResponseTime_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_ExtendedStats() {
        setResponseTime_ExtendedStats(null);
    }

    public void setResponseTime_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setResponseTime_ExtendedStats("responseTime", opLambda);
    }

    public void setResponseTime_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Stats() {
        setResponseTime_Stats(null);
    }

    public void setResponseTime_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setResponseTime_Stats("responseTime", opLambda);
    }

    public void setResponseTime_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Percentiles() {
        setResponseTime_Percentiles(null);
    }

    public void setResponseTime_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setResponseTime_Percentiles("responseTime", opLambda);
    }

    public void setResponseTime_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_PercentileRanks(double[] values) {
        setResponseTime_PercentileRanks(values, null);
    }

    public void setResponseTime_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setResponseTime_PercentileRanks("responseTime", values, opLambda);
    }

    public void setResponseTime_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "responseTime", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Histogram() {
        setResponseTime_Histogram(null);
    }

    public void setResponseTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setResponseTime_Histogram("responseTime", opLambda, null);
    }

    public void setResponseTime_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setResponseTime_Histogram("responseTime", opLambda, aggsLambda);
    }

    public void setResponseTime_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setResponseTime_Range() {
        setResponseTime_Range(null);
    }

    public void setResponseTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setResponseTime_Range("responseTime", opLambda, null);
    }

    public void setResponseTime_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setResponseTime_Range("responseTime", opLambda, aggsLambda);
    }

    public void setResponseTime_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setResponseTime_Count() {
        setResponseTime_Count(null);
    }

    public void setResponseTime_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setResponseTime_Count("responseTime", opLambda);
    }

    public void setResponseTime_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Cardinality() {
        setResponseTime_Cardinality(null);
    }

    public void setResponseTime_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setResponseTime_Cardinality("responseTime", opLambda);
    }

    public void setResponseTime_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Missing() {
        setResponseTime_Missing(null);
    }

    public void setResponseTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setResponseTime_Missing("responseTime", opLambda, null);
    }

    public void setResponseTime_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setResponseTime_Missing("responseTime", opLambda, aggsLambda);
    }

    public void setResponseTime_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRoles_Terms() {
        setRoles_Terms(null);
    }

    public void setRoles_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setRoles_Terms("roles", opLambda, null);
    }

    public void setRoles_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setRoles_Terms("roles", opLambda, aggsLambda);
    }

    public void setRoles_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRoles_SignificantTerms() {
        setRoles_SignificantTerms(null);
    }

    public void setRoles_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setRoles_SignificantTerms("roles", opLambda, null);
    }

    public void setRoles_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setRoles_SignificantTerms("roles", opLambda, aggsLambda);
    }

    public void setRoles_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRoles_IpRange() {
        setRoles_IpRange(null);
    }

    public void setRoles_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setRoles_IpRange("roles", opLambda, null);
    }

    public void setRoles_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setRoles_IpRange("roles", opLambda, aggsLambda);
    }

    public void setRoles_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRoles_Count() {
        setRoles_Count(null);
    }

    public void setRoles_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setRoles_Count("roles", opLambda);
    }

    public void setRoles_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_Cardinality() {
        setRoles_Cardinality(null);
    }

    public void setRoles_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setRoles_Cardinality("roles", opLambda);
    }

    public void setRoles_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_Missing() {
        setRoles_Missing(null);
    }

    public void setRoles_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setRoles_Missing("roles", opLambda, null);
    }

    public void setRoles_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setRoles_Missing("roles", opLambda, aggsLambda);
    }

    public void setRoles_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSearchWord_Terms() {
        setSearchWord_Terms(null);
    }

    public void setSearchWord_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setSearchWord_Terms("searchWord", opLambda, null);
    }

    public void setSearchWord_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setSearchWord_Terms("searchWord", opLambda, aggsLambda);
    }

    public void setSearchWord_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "searchWord");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSearchWord_SignificantTerms() {
        setSearchWord_SignificantTerms(null);
    }

    public void setSearchWord_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setSearchWord_SignificantTerms("searchWord", opLambda, null);
    }

    public void setSearchWord_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setSearchWord_SignificantTerms("searchWord", opLambda, aggsLambda);
    }

    public void setSearchWord_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "searchWord");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSearchWord_IpRange() {
        setSearchWord_IpRange(null);
    }

    public void setSearchWord_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setSearchWord_IpRange("searchWord", opLambda, null);
    }

    public void setSearchWord_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setSearchWord_IpRange("searchWord", opLambda, aggsLambda);
    }

    public void setSearchWord_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "searchWord");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSearchWord_Count() {
        setSearchWord_Count(null);
    }

    public void setSearchWord_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setSearchWord_Count("searchWord", opLambda);
    }

    public void setSearchWord_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "searchWord");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_Cardinality() {
        setSearchWord_Cardinality(null);
    }

    public void setSearchWord_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setSearchWord_Cardinality("searchWord", opLambda);
    }

    public void setSearchWord_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "searchWord");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_Missing() {
        setSearchWord_Missing(null);
    }

    public void setSearchWord_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setSearchWord_Missing("searchWord", opLambda, null);
    }

    public void setSearchWord_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setSearchWord_Missing("searchWord", opLambda, aggsLambda);
    }

    public void setSearchWord_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "searchWord");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUser_Terms() {
        setUser_Terms(null);
    }

    public void setUser_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setUser_Terms("user", opLambda, null);
    }

    public void setUser_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUser_Terms("user", opLambda, aggsLambda);
    }

    public void setUser_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUser_SignificantTerms() {
        setUser_SignificantTerms(null);
    }

    public void setUser_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setUser_SignificantTerms("user", opLambda, null);
    }

    public void setUser_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setUser_SignificantTerms("user", opLambda, aggsLambda);
    }

    public void setUser_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUser_IpRange() {
        setUser_IpRange(null);
    }

    public void setUser_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setUser_IpRange("user", opLambda, null);
    }

    public void setUser_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUser_IpRange("user", opLambda, aggsLambda);
    }

    public void setUser_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUser_Count() {
        setUser_Count(null);
    }

    public void setUser_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setUser_Count("user", opLambda);
    }

    public void setUser_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_Cardinality() {
        setUser_Cardinality(null);
    }

    public void setUser_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setUser_Cardinality("user", opLambda);
    }

    public void setUser_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_Missing() {
        setUser_Missing(null);
    }

    public void setUser_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setUser_Missing("user", opLambda, null);
    }

    public void setUser_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUser_Missing("user", opLambda, aggsLambda);
    }

    public void setUser_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserAgent_Terms() {
        setUserAgent_Terms(null);
    }

    public void setUserAgent_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setUserAgent_Terms("userAgent", opLambda, null);
    }

    public void setUserAgent_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserAgent_Terms("userAgent", opLambda, aggsLambda);
    }

    public void setUserAgent_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "userAgent");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserAgent_SignificantTerms() {
        setUserAgent_SignificantTerms(null);
    }

    public void setUserAgent_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setUserAgent_SignificantTerms("userAgent", opLambda, null);
    }

    public void setUserAgent_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserAgent_SignificantTerms("userAgent", opLambda, aggsLambda);
    }

    public void setUserAgent_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "userAgent");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserAgent_IpRange() {
        setUserAgent_IpRange(null);
    }

    public void setUserAgent_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setUserAgent_IpRange("userAgent", opLambda, null);
    }

    public void setUserAgent_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserAgent_IpRange("userAgent", opLambda, aggsLambda);
    }

    public void setUserAgent_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "userAgent");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserAgent_Count() {
        setUserAgent_Count(null);
    }

    public void setUserAgent_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setUserAgent_Count("userAgent", opLambda);
    }

    public void setUserAgent_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "userAgent");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_Cardinality() {
        setUserAgent_Cardinality(null);
    }

    public void setUserAgent_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setUserAgent_Cardinality("userAgent", opLambda);
    }

    public void setUserAgent_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "userAgent");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_Missing() {
        setUserAgent_Missing(null);
    }

    public void setUserAgent_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setUserAgent_Missing("userAgent", opLambda, null);
    }

    public void setUserAgent_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserAgent_Missing("userAgent", opLambda, aggsLambda);
    }

    public void setUserAgent_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "userAgent");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserInfoId_Terms() {
        setUserInfoId_Terms(null);
    }

    public void setUserInfoId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setUserInfoId_Terms("userInfoId", opLambda, null);
    }

    public void setUserInfoId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserInfoId_Terms("userInfoId", opLambda, aggsLambda);
    }

    public void setUserInfoId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "userInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserInfoId_SignificantTerms() {
        setUserInfoId_SignificantTerms(null);
    }

    public void setUserInfoId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setUserInfoId_SignificantTerms("userInfoId", opLambda, null);
    }

    public void setUserInfoId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserInfoId_SignificantTerms("userInfoId", opLambda, aggsLambda);
    }

    public void setUserInfoId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "userInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserInfoId_IpRange() {
        setUserInfoId_IpRange(null);
    }

    public void setUserInfoId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setUserInfoId_IpRange("userInfoId", opLambda, null);
    }

    public void setUserInfoId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserInfoId_IpRange("userInfoId", opLambda, aggsLambda);
    }

    public void setUserInfoId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "userInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserInfoId_Count() {
        setUserInfoId_Count(null);
    }

    public void setUserInfoId_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setUserInfoId_Count("userInfoId", opLambda);
    }

    public void setUserInfoId_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "userInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_Cardinality() {
        setUserInfoId_Cardinality(null);
    }

    public void setUserInfoId_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setUserInfoId_Cardinality("userInfoId", opLambda);
    }

    public void setUserInfoId_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "userInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_Missing() {
        setUserInfoId_Missing(null);
    }

    public void setUserInfoId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setUserInfoId_Missing("userInfoId", opLambda, null);
    }

    public void setUserInfoId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserInfoId_Missing("userInfoId", opLambda, aggsLambda);
    }

    public void setUserInfoId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "userInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
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

    public void setUserSessionId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserSessionId_Terms("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
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
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserSessionId_SignificantTerms("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
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

    public void setUserSessionId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserSessionId_IpRange("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
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

    public void setUserSessionId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserSessionId_Missing("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setVirtualHost_Terms() {
        setVirtualHost_Terms(null);
    }

    public void setVirtualHost_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setVirtualHost_Terms("virtualHost", opLambda, null);
    }

    public void setVirtualHost_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setVirtualHost_Terms("virtualHost", opLambda, aggsLambda);
    }

    public void setVirtualHost_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "virtualHost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setVirtualHost_SignificantTerms() {
        setVirtualHost_SignificantTerms(null);
    }

    public void setVirtualHost_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setVirtualHost_SignificantTerms("virtualHost", opLambda, null);
    }

    public void setVirtualHost_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setVirtualHost_SignificantTerms("virtualHost", opLambda, aggsLambda);
    }

    public void setVirtualHost_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "virtualHost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setVirtualHost_IpRange() {
        setVirtualHost_IpRange(null);
    }

    public void setVirtualHost_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setVirtualHost_IpRange("virtualHost", opLambda, null);
    }

    public void setVirtualHost_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setVirtualHost_IpRange("virtualHost", opLambda, aggsLambda);
    }

    public void setVirtualHost_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "virtualHost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setVirtualHost_Count() {
        setVirtualHost_Count(null);
    }

    public void setVirtualHost_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setVirtualHost_Count("virtualHost", opLambda);
    }

    public void setVirtualHost_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "virtualHost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_Cardinality() {
        setVirtualHost_Cardinality(null);
    }

    public void setVirtualHost_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setVirtualHost_Cardinality("virtualHost", opLambda);
    }

    public void setVirtualHost_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "virtualHost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setVirtualHost_Missing() {
        setVirtualHost_Missing(null);
    }

    public void setVirtualHost_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setVirtualHost_Missing("virtualHost", opLambda, null);
    }

    public void setVirtualHost_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setVirtualHost_Missing("virtualHost", opLambda, aggsLambda);
    }

    public void setVirtualHost_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "virtualHost");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
