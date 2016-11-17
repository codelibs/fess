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
package org.codelibs.fess.es.user.cbean.ca.bs;

import org.codelibs.fess.es.user.allcommon.EsAbstractConditionAggregation;
import org.codelibs.fess.es.user.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.user.cbean.ca.GroupCA;
import org.codelibs.fess.es.user.cbean.cq.GroupCQ;
import org.codelibs.fess.es.user.cbean.cq.bs.BsGroupCQ;
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

    public void global(String name, ConditionOptionCall<GlobalBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        GlobalBuilder builder = regGlobalA(name);
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

    // String name

    public void setName_Terms() {
        setName_Terms(null);
    }

    public void setName_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setName_Terms("name", opLambda, null);
    }

    public void setName_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        setName_Terms("name", opLambda, aggsLambda);
    }

    public void setName_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "name");
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

    public void setName_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setName_SignificantTerms("name", opLambda, null);
    }

    public void setName_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        setName_SignificantTerms("name", opLambda, aggsLambda);
    }

    public void setName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsGroupCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "name");
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

    public void setName_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setName_IpRange("name", opLambda, null);
    }

    public void setName_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        setName_IpRange("name", opLambda, aggsLambda);
    }

    public void setName_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "name");
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

    public void setName_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setName_Count("name", opLambda);
    }

    public void setName_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Cardinality() {
        setName_Cardinality(null);
    }

    public void setName_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setName_Cardinality("name", opLambda);
    }

    public void setName_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Missing() {
        setName_Missing(null);
    }

    public void setName_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setName_Missing("name", opLambda, null);
    }

    public void setName_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        setName_Missing("name", opLambda, aggsLambda);
    }

    public void setName_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            GroupCA ca = new GroupCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Long gidNumber
    public void setGidNumber_Avg() {
        setGidNumber_Avg(null);
    }

    public void setGidNumber_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setGidNumber_Avg("gidNumber", opLambda);
    }

    public void setGidNumber_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Max() {
        setGidNumber_Max(null);
    }

    public void setGidNumber_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setGidNumber_Max("gidNumber", opLambda);
    }

    public void setGidNumber_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Min() {
        setGidNumber_Min(null);
    }

    public void setGidNumber_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setGidNumber_Min("gidNumber", opLambda);
    }

    public void setGidNumber_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Sum() {
        setGidNumber_Sum(null);
    }

    public void setGidNumber_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setGidNumber_Sum("gidNumber", opLambda);
    }

    public void setGidNumber_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_ExtendedStats() {
        setGidNumber_ExtendedStats(null);
    }

    public void setGidNumber_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setGidNumber_ExtendedStats("gidNumber", opLambda);
    }

    public void setGidNumber_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Stats() {
        setGidNumber_Stats(null);
    }

    public void setGidNumber_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setGidNumber_Stats("gidNumber", opLambda);
    }

    public void setGidNumber_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Percentiles() {
        setGidNumber_Percentiles(null);
    }

    public void setGidNumber_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setGidNumber_Percentiles("gidNumber", opLambda);
    }

    public void setGidNumber_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_PercentileRanks() {
        setGidNumber_PercentileRanks(null);
    }

    public void setGidNumber_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setGidNumber_PercentileRanks("gidNumber", opLambda);
    }

    public void setGidNumber_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Histogram() {
        setGidNumber_Histogram(null);
    }

    public void setGidNumber_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setGidNumber_Histogram("gidNumber", opLambda, null);
    }

    public void setGidNumber_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        setGidNumber_Histogram("gidNumber", opLambda, aggsLambda);
    }

    public void setGidNumber_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "gidNumber");
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

    public void setGidNumber_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setGidNumber_Range("gidNumber", opLambda, null);
    }

    public void setGidNumber_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        setGidNumber_Range("gidNumber", opLambda, aggsLambda);
    }

    public void setGidNumber_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "gidNumber");
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

    public void setGidNumber_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setGidNumber_Count("gidNumber", opLambda);
    }

    public void setGidNumber_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Cardinality() {
        setGidNumber_Cardinality(null);
    }

    public void setGidNumber_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setGidNumber_Cardinality("gidNumber", opLambda);
    }

    public void setGidNumber_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Missing() {
        setGidNumber_Missing(null);
    }

    public void setGidNumber_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setGidNumber_Missing("gidNumber", opLambda, null);
    }

    public void setGidNumber_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        setGidNumber_Missing("gidNumber", opLambda, aggsLambda);
    }

    public void setGidNumber_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsGroupCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "gidNumber");
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
