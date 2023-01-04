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
package org.codelibs.fess.es.user.cbean.ca.bs;

import org.codelibs.fess.es.user.allcommon.EsAbstractConditionAggregation;
import org.codelibs.fess.es.user.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.user.cbean.ca.GroupCA;
import org.codelibs.fess.es.user.cbean.cq.GroupCQ;
import org.codelibs.fess.es.user.cbean.cq.bs.BsGroupCQ;
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
public abstract class BsGroupCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsGroupCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        GroupCQ cq = new GroupCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            GroupCA ca = new GroupCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            GroupCA ca = new GroupCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            GroupCA ca = new GroupCA();
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

    public void setGidNumber_Avg() {
        setGidNumber_Avg(null);
    }

    public void setGidNumber_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setGidNumber_Avg("gidNumber", opLambda);
    }

    public void setGidNumber_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Max() {
        setGidNumber_Max(null);
    }

    public void setGidNumber_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setGidNumber_Max("gidNumber", opLambda);
    }

    public void setGidNumber_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Min() {
        setGidNumber_Min(null);
    }

    public void setGidNumber_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setGidNumber_Min("gidNumber", opLambda);
    }

    public void setGidNumber_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Sum() {
        setGidNumber_Sum(null);
    }

    public void setGidNumber_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setGidNumber_Sum("gidNumber", opLambda);
    }

    public void setGidNumber_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_ExtendedStats() {
        setGidNumber_ExtendedStats(null);
    }

    public void setGidNumber_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setGidNumber_ExtendedStats("gidNumber", opLambda);
    }

    public void setGidNumber_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Stats() {
        setGidNumber_Stats(null);
    }

    public void setGidNumber_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setGidNumber_Stats("gidNumber", opLambda);
    }

    public void setGidNumber_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Percentiles() {
        setGidNumber_Percentiles(null);
    }

    public void setGidNumber_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setGidNumber_Percentiles("gidNumber", opLambda);
    }

    public void setGidNumber_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_PercentileRanks(double[] values) {
        setGidNumber_PercentileRanks(values, null);
    }

    public void setGidNumber_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setGidNumber_PercentileRanks("gidNumber", values, opLambda);
    }

    public void setGidNumber_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "gidNumber", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Histogram() {
        setGidNumber_Histogram(null);
    }

    public void setGidNumber_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setGidNumber_Histogram("gidNumber", opLambda, null);
    }

    public void setGidNumber_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        setGidNumber_Histogram("gidNumber", opLambda, aggsLambda);
    }

    public void setGidNumber_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsGroupCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            GroupCA ca = new GroupCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setGidNumber_Range() {
        setGidNumber_Range(null);
    }

    public void setGidNumber_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setGidNumber_Range("gidNumber", opLambda, null);
    }

    public void setGidNumber_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        setGidNumber_Range("gidNumber", opLambda, aggsLambda);
    }

    public void setGidNumber_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            GroupCA ca = new GroupCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setGidNumber_Count() {
        setGidNumber_Count(null);
    }

    public void setGidNumber_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setGidNumber_Count("gidNumber", opLambda);
    }

    public void setGidNumber_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Cardinality() {
        setGidNumber_Cardinality(null);
    }

    public void setGidNumber_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setGidNumber_Cardinality("gidNumber", opLambda);
    }

    public void setGidNumber_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Missing() {
        setGidNumber_Missing(null);
    }

    public void setGidNumber_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setGidNumber_Missing("gidNumber", opLambda, null);
    }

    public void setGidNumber_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        setGidNumber_Missing("gidNumber", opLambda, aggsLambda);
    }

    public void setGidNumber_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsGroupCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            GroupCA ca = new GroupCA();
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

    public void setName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        setName_Terms("name", opLambda, aggsLambda);
    }

    public void setName_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            GroupCA ca = new GroupCA();
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
            OperatorCall<BsGroupCA> aggsLambda) {
        setName_SignificantTerms("name", opLambda, aggsLambda);
    }

    public void setName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsGroupCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            GroupCA ca = new GroupCA();
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

    public void setName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        setName_IpRange("name", opLambda, aggsLambda);
    }

    public void setName_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            GroupCA ca = new GroupCA();
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

    public void setName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        setName_Missing("name", opLambda, aggsLambda);
    }

    public void setName_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            GroupCA ca = new GroupCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
