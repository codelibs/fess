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
package org.codelibs.fess.es.log.cbean.ca.bs;

import org.codelibs.fess.es.log.allcommon.EsAbstractConditionAggregation;
import org.codelibs.fess.es.log.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.log.cbean.ca.ClickLogCA;
import org.codelibs.fess.es.log.cbean.cq.ClickLogCQ;
import org.codelibs.fess.es.log.cbean.cq.bs.BsClickLogCQ;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.global.GlobalBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.HistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.missing.MissingBuilder;
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder;
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeBuilder;
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

    public void global(String name, ConditionOptionCall<GlobalBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        GlobalBuilder builder = regGlobalA(name);
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

    // LocalDateTime queryRequestedAt

    public void setQueryRequestedAt_DateRange() {
        setQueryRequestedAt_DateRange(null);
    }

    public void setQueryRequestedAt_DateRange(ConditionOptionCall<DateRangeBuilder> opLambda) {
        setQueryRequestedAt_DateRange("queryRequestedAt", opLambda, null);
    }

    public void setQueryRequestedAt_DateRange(ConditionOptionCall<DateRangeBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setQueryRequestedAt_DateRange("queryRequestedAt", opLambda, aggsLambda);
    }

    public void setQueryRequestedAt_DateRange(String name, ConditionOptionCall<DateRangeBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        DateRangeBuilder builder = regDateRangeA(name, "queryRequestedAt");
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

    public void setQueryRequestedAt_DateHistogram(ConditionOptionCall<DateHistogramBuilder> opLambda) {
        setQueryRequestedAt_DateHistogram("queryRequestedAt", opLambda, null);
    }

    public void setQueryRequestedAt_DateHistogram(ConditionOptionCall<DateHistogramBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setQueryRequestedAt_DateHistogram("queryRequestedAt", opLambda, aggsLambda);
    }

    public void setQueryRequestedAt_DateHistogram(String name, ConditionOptionCall<DateHistogramBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        DateHistogramBuilder builder = regDateHistogramA(name, "queryRequestedAt");
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

    public void setQueryRequestedAt_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setQueryRequestedAt_Count("queryRequestedAt", opLambda);
    }

    public void setQueryRequestedAt_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "queryRequestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_Cardinality() {
        setQueryRequestedAt_Cardinality(null);
    }

    public void setQueryRequestedAt_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setQueryRequestedAt_Cardinality("queryRequestedAt", opLambda);
    }

    public void setQueryRequestedAt_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "queryRequestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryRequestedAt_Missing() {
        setQueryRequestedAt_Missing(null);
    }

    public void setQueryRequestedAt_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setQueryRequestedAt_Missing("queryRequestedAt", opLambda, null);
    }

    public void setQueryRequestedAt_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setQueryRequestedAt_Missing("queryRequestedAt", opLambda, aggsLambda);
    }

    public void setQueryRequestedAt_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "queryRequestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // LocalDateTime requestedAt

    public void setRequestedAt_DateRange() {
        setRequestedAt_DateRange(null);
    }

    public void setRequestedAt_DateRange(ConditionOptionCall<DateRangeBuilder> opLambda) {
        setRequestedAt_DateRange("requestedAt", opLambda, null);
    }

    public void setRequestedAt_DateRange(ConditionOptionCall<DateRangeBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setRequestedAt_DateRange("requestedAt", opLambda, aggsLambda);
    }

    public void setRequestedAt_DateRange(String name, ConditionOptionCall<DateRangeBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        DateRangeBuilder builder = regDateRangeA(name, "requestedAt");
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

    public void setRequestedAt_DateHistogram(ConditionOptionCall<DateHistogramBuilder> opLambda) {
        setRequestedAt_DateHistogram("requestedAt", opLambda, null);
    }

    public void setRequestedAt_DateHistogram(ConditionOptionCall<DateHistogramBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setRequestedAt_DateHistogram("requestedAt", opLambda, aggsLambda);
    }

    public void setRequestedAt_DateHistogram(String name, ConditionOptionCall<DateHistogramBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        DateHistogramBuilder builder = regDateHistogramA(name, "requestedAt");
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

    public void setRequestedAt_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setRequestedAt_Count("requestedAt", opLambda);
    }

    public void setRequestedAt_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "requestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_Cardinality() {
        setRequestedAt_Cardinality(null);
    }

    public void setRequestedAt_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setRequestedAt_Cardinality("requestedAt", opLambda);
    }

    public void setRequestedAt_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "requestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRequestedAt_Missing() {
        setRequestedAt_Missing(null);
    }

    public void setRequestedAt_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setRequestedAt_Missing("requestedAt", opLambda, null);
    }

    public void setRequestedAt_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setRequestedAt_Missing("requestedAt", opLambda, aggsLambda);
    }

    public void setRequestedAt_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "requestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String queryId

    public void setQueryId_Terms() {
        setQueryId_Terms(null);
    }

    public void setQueryId_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setQueryId_Terms("queryId", opLambda, null);
    }

    public void setQueryId_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setQueryId_Terms("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "queryId");
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

    public void setQueryId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setQueryId_SignificantTerms("queryId", opLambda, null);
    }

    public void setQueryId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setQueryId_SignificantTerms("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "queryId");
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

    public void setQueryId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setQueryId_IpRange("queryId", opLambda, null);
    }

    public void setQueryId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setQueryId_IpRange("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "queryId");
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

    public void setQueryId_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setQueryId_Count("queryId", opLambda);
    }

    public void setQueryId_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_Cardinality() {
        setQueryId_Cardinality(null);
    }

    public void setQueryId_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setQueryId_Cardinality("queryId", opLambda);
    }

    public void setQueryId_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryId_Missing() {
        setQueryId_Missing(null);
    }

    public void setQueryId_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setQueryId_Missing("queryId", opLambda, null);
    }

    public void setQueryId_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setQueryId_Missing("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String docId

    public void setDocId_Terms() {
        setDocId_Terms(null);
    }

    public void setDocId_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setDocId_Terms("docId", opLambda, null);
    }

    public void setDocId_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setDocId_Terms("docId", opLambda, aggsLambda);
    }

    public void setDocId_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "docId");
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

    public void setDocId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setDocId_SignificantTerms("docId", opLambda, null);
    }

    public void setDocId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setDocId_SignificantTerms("docId", opLambda, aggsLambda);
    }

    public void setDocId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "docId");
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

    public void setDocId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setDocId_IpRange("docId", opLambda, null);
    }

    public void setDocId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setDocId_IpRange("docId", opLambda, aggsLambda);
    }

    public void setDocId_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "docId");
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

    public void setDocId_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setDocId_Count("docId", opLambda);
    }

    public void setDocId_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_Cardinality() {
        setDocId_Cardinality(null);
    }

    public void setDocId_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setDocId_Cardinality("docId", opLambda);
    }

    public void setDocId_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDocId_Missing() {
        setDocId_Missing(null);
    }

    public void setDocId_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setDocId_Missing("docId", opLambda, null);
    }

    public void setDocId_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setDocId_Missing("docId", opLambda, aggsLambda);
    }

    public void setDocId_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String userSessionId

    public void setUserSessionId_Terms() {
        setUserSessionId_Terms(null);
    }

    public void setUserSessionId_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setUserSessionId_Terms("userSessionId", opLambda, null);
    }

    public void setUserSessionId_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setUserSessionId_Terms("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "userSessionId");
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

    public void setUserSessionId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setUserSessionId_SignificantTerms("userSessionId", opLambda, null);
    }

    public void setUserSessionId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        setUserSessionId_SignificantTerms("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "userSessionId");
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

    public void setUserSessionId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setUserSessionId_IpRange("userSessionId", opLambda, null);
    }

    public void setUserSessionId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setUserSessionId_IpRange("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "userSessionId");
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

    public void setUserSessionId_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setUserSessionId_Count("userSessionId", opLambda);
    }

    public void setUserSessionId_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Cardinality() {
        setUserSessionId_Cardinality(null);
    }

    public void setUserSessionId_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setUserSessionId_Cardinality("userSessionId", opLambda);
    }

    public void setUserSessionId_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserSessionId_Missing() {
        setUserSessionId_Missing(null);
    }

    public void setUserSessionId_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setUserSessionId_Missing("userSessionId", opLambda, null);
    }

    public void setUserSessionId_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setUserSessionId_Missing("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String url

    public void setUrl_Terms() {
        setUrl_Terms(null);
    }

    public void setUrl_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setUrl_Terms("url", opLambda, null);
    }

    public void setUrl_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setUrl_Terms("url", opLambda, aggsLambda);
    }

    public void setUrl_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "url");
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

    public void setUrl_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setUrl_SignificantTerms("url", opLambda, null);
    }

    public void setUrl_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setUrl_SignificantTerms("url", opLambda, aggsLambda);
    }

    public void setUrl_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsClickLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "url");
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

    public void setUrl_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setUrl_IpRange("url", opLambda, null);
    }

    public void setUrl_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setUrl_IpRange("url", opLambda, aggsLambda);
    }

    public void setUrl_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "url");
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

    public void setUrl_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setUrl_Count("url", opLambda);
    }

    public void setUrl_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Cardinality() {
        setUrl_Cardinality(null);
    }

    public void setUrl_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setUrl_Cardinality("url", opLambda);
    }

    public void setUrl_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUrl_Missing() {
        setUrl_Missing(null);
    }

    public void setUrl_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setUrl_Missing("url", opLambda, null);
    }

    public void setUrl_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setUrl_Missing("url", opLambda, aggsLambda);
    }

    public void setUrl_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ClickLogCA ca = new ClickLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Integer order
    public void setOrder_Avg() {
        setOrder_Avg(null);
    }

    public void setOrder_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setOrder_Avg("order", opLambda);
    }

    public void setOrder_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Max() {
        setOrder_Max(null);
    }

    public void setOrder_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setOrder_Max("order", opLambda);
    }

    public void setOrder_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Min() {
        setOrder_Min(null);
    }

    public void setOrder_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setOrder_Min("order", opLambda);
    }

    public void setOrder_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Sum() {
        setOrder_Sum(null);
    }

    public void setOrder_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setOrder_Sum("order", opLambda);
    }

    public void setOrder_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_ExtendedStats() {
        setOrder_ExtendedStats(null);
    }

    public void setOrder_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setOrder_ExtendedStats("order", opLambda);
    }

    public void setOrder_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Stats() {
        setOrder_Stats(null);
    }

    public void setOrder_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setOrder_Stats("order", opLambda);
    }

    public void setOrder_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Percentiles() {
        setOrder_Percentiles(null);
    }

    public void setOrder_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setOrder_Percentiles("order", opLambda);
    }

    public void setOrder_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_PercentileRanks() {
        setOrder_PercentileRanks(null);
    }

    public void setOrder_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setOrder_PercentileRanks("order", opLambda);
    }

    public void setOrder_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Histogram() {
        setOrder_Histogram(null);
    }

    public void setOrder_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setOrder_Histogram("order", opLambda, null);
    }

    public void setOrder_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setOrder_Histogram("order", opLambda, aggsLambda);
    }

    public void setOrder_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "order");
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

    public void setOrder_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setOrder_Range("order", opLambda, null);
    }

    public void setOrder_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setOrder_Range("order", opLambda, aggsLambda);
    }

    public void setOrder_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "order");
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

    public void setOrder_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setOrder_Count("order", opLambda);
    }

    public void setOrder_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Cardinality() {
        setOrder_Cardinality(null);
    }

    public void setOrder_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setOrder_Cardinality("order", opLambda);
    }

    public void setOrder_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "order");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setOrder_Missing() {
        setOrder_Missing(null);
    }

    public void setOrder_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setOrder_Missing("order", opLambda, null);
    }

    public void setOrder_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        setOrder_Missing("order", opLambda, aggsLambda);
    }

    public void setOrder_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsClickLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "order");
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
