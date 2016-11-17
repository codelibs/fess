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
package org.codelibs.fess.es.log.cbean.ca.bs;

import org.codelibs.fess.es.log.allcommon.EsAbstractConditionAggregation;
import org.codelibs.fess.es.log.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.log.cbean.ca.FavoriteLogCA;
import org.codelibs.fess.es.log.cbean.cq.FavoriteLogCQ;
import org.codelibs.fess.es.log.cbean.cq.bs.BsFavoriteLogCQ;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.global.GlobalBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.missing.MissingBuilder;
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeBuilder;
import org.elasticsearch.search.aggregations.bucket.range.ipv4.IPv4RangeBuilder;
import org.elasticsearch.search.aggregations.bucket.sampler.SamplerAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.significant.SignificantTermsBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityBuilder;
import org.elasticsearch.search.aggregations.metrics.scripted.ScriptedMetricBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHitsBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsFavoriteLogCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsFavoriteLogCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        FavoriteLogCQ cq = new FavoriteLogCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        GlobalBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
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

    // LocalDateTime createdAt

    public void setCreatedAt_DateRange() {
        setCreatedAt_DateRange(null);
    }

    public void setCreatedAt_DateRange(ConditionOptionCall<DateRangeBuilder> opLambda) {
        setCreatedAt_DateRange("createdAt", opLambda, null);
    }

    public void setCreatedAt_DateRange(ConditionOptionCall<DateRangeBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setCreatedAt_DateRange("createdAt", opLambda, aggsLambda);
    }

    public void setCreatedAt_DateRange(String name, ConditionOptionCall<DateRangeBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        DateRangeBuilder builder = regDateRangeA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCreatedAt_DateHistogram() {
        setCreatedAt_DateHistogram(null);
    }

    public void setCreatedAt_DateHistogram(ConditionOptionCall<DateHistogramBuilder> opLambda) {
        setCreatedAt_DateHistogram("createdAt", opLambda, null);
    }

    public void setCreatedAt_DateHistogram(ConditionOptionCall<DateHistogramBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setCreatedAt_DateHistogram("createdAt", opLambda, aggsLambda);
    }

    public void setCreatedAt_DateHistogram(String name, ConditionOptionCall<DateHistogramBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        DateHistogramBuilder builder = regDateHistogramA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCreatedAt_Count() {
        setCreatedAt_Count(null);
    }

    public void setCreatedAt_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setCreatedAt_Count("createdAt", opLambda);
    }

    public void setCreatedAt_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Cardinality() {
        setCreatedAt_Cardinality(null);
    }

    public void setCreatedAt_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setCreatedAt_Cardinality("createdAt", opLambda);
    }

    public void setCreatedAt_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Missing() {
        setCreatedAt_Missing(null);
    }

    public void setCreatedAt_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setCreatedAt_Missing("createdAt", opLambda, null);
    }

    public void setCreatedAt_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setCreatedAt_Missing("createdAt", opLambda, aggsLambda);
    }

    public void setCreatedAt_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
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

    public void setUrl_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setUrl_Terms("url", opLambda, aggsLambda);
    }

    public void setUrl_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
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

    public void setUrl_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setUrl_SignificantTerms("url", opLambda, aggsLambda);
    }

    public void setUrl_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
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

    public void setUrl_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setUrl_IpRange("url", opLambda, aggsLambda);
    }

    public void setUrl_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
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

    public void setUrl_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setUrl_Missing("url", opLambda, aggsLambda);
    }

    public void setUrl_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
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

    public void setDocId_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setDocId_Terms("docId", opLambda, aggsLambda);
    }

    public void setDocId_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
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

    public void setDocId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setDocId_SignificantTerms("docId", opLambda, aggsLambda);
    }

    public void setDocId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
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

    public void setDocId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setDocId_IpRange("docId", opLambda, aggsLambda);
    }

    public void setDocId_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
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

    public void setDocId_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setDocId_Missing("docId", opLambda, aggsLambda);
    }

    public void setDocId_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
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

    public void setQueryId_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setQueryId_Terms("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
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

    public void setQueryId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setQueryId_SignificantTerms("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
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

    public void setQueryId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setQueryId_IpRange("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
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

    public void setQueryId_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setQueryId_Missing("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String userInfoId

    public void setUserInfoId_Terms() {
        setUserInfoId_Terms(null);
    }

    public void setUserInfoId_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setUserInfoId_Terms("userInfoId", opLambda, null);
    }

    public void setUserInfoId_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setUserInfoId_Terms("userInfoId", opLambda, aggsLambda);
    }

    public void setUserInfoId_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "userInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserInfoId_SignificantTerms() {
        setUserInfoId_SignificantTerms(null);
    }

    public void setUserInfoId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setUserInfoId_SignificantTerms("userInfoId", opLambda, null);
    }

    public void setUserInfoId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setUserInfoId_SignificantTerms("userInfoId", opLambda, aggsLambda);
    }

    public void setUserInfoId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "userInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserInfoId_IpRange() {
        setUserInfoId_IpRange(null);
    }

    public void setUserInfoId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setUserInfoId_IpRange("userInfoId", opLambda, null);
    }

    public void setUserInfoId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setUserInfoId_IpRange("userInfoId", opLambda, aggsLambda);
    }

    public void setUserInfoId_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "userInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUserInfoId_Count() {
        setUserInfoId_Count(null);
    }

    public void setUserInfoId_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setUserInfoId_Count("userInfoId", opLambda);
    }

    public void setUserInfoId_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "userInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_Cardinality() {
        setUserInfoId_Cardinality(null);
    }

    public void setUserInfoId_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setUserInfoId_Cardinality("userInfoId", opLambda);
    }

    public void setUserInfoId_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "userInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserInfoId_Missing() {
        setUserInfoId_Missing(null);
    }

    public void setUserInfoId_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setUserInfoId_Missing("userInfoId", opLambda, null);
    }

    public void setUserInfoId_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setUserInfoId_Missing("userInfoId", opLambda, aggsLambda);
    }

    public void setUserInfoId_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "userInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
