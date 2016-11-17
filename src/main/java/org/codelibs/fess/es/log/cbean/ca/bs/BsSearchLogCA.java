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
import org.codelibs.fess.es.log.cbean.ca.SearchLogCA;
import org.codelibs.fess.es.log.cbean.cq.SearchLogCQ;
import org.codelibs.fess.es.log.cbean.cq.bs.BsSearchLogCQ;
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

    public void global(String name, ConditionOptionCall<GlobalBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        GlobalBuilder builder = regGlobalA(name);
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

    // String accessType

    public void setAccessType_Terms() {
        setAccessType_Terms(null);
    }

    public void setAccessType_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setAccessType_Terms("accessType", opLambda, null);
    }

    public void setAccessType_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setAccessType_Terms("accessType", opLambda, aggsLambda);
    }

    public void setAccessType_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "accessType");
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

    public void setAccessType_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setAccessType_SignificantTerms("accessType", opLambda, null);
    }

    public void setAccessType_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setAccessType_SignificantTerms("accessType", opLambda, aggsLambda);
    }

    public void setAccessType_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "accessType");
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

    public void setAccessType_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setAccessType_IpRange("accessType", opLambda, null);
    }

    public void setAccessType_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setAccessType_IpRange("accessType", opLambda, aggsLambda);
    }

    public void setAccessType_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "accessType");
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

    public void setAccessType_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setAccessType_Count("accessType", opLambda);
    }

    public void setAccessType_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "accessType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Cardinality() {
        setAccessType_Cardinality(null);
    }

    public void setAccessType_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setAccessType_Cardinality("accessType", opLambda);
    }

    public void setAccessType_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "accessType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setAccessType_Missing() {
        setAccessType_Missing(null);
    }

    public void setAccessType_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setAccessType_Missing("accessType", opLambda, null);
    }

    public void setAccessType_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setAccessType_Missing("accessType", opLambda, aggsLambda);
    }

    public void setAccessType_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "accessType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String user

    public void setUser_Terms() {
        setUser_Terms(null);
    }

    public void setUser_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setUser_Terms("user", opLambda, null);
    }

    public void setUser_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUser_Terms("user", opLambda, aggsLambda);
    }

    public void setUser_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "user");
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

    public void setUser_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setUser_SignificantTerms("user", opLambda, null);
    }

    public void setUser_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUser_SignificantTerms("user", opLambda, aggsLambda);
    }

    public void setUser_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "user");
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

    public void setUser_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setUser_IpRange("user", opLambda, null);
    }

    public void setUser_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUser_IpRange("user", opLambda, aggsLambda);
    }

    public void setUser_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "user");
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

    public void setUser_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setUser_Count("user", opLambda);
    }

    public void setUser_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_Cardinality() {
        setUser_Cardinality(null);
    }

    public void setUser_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setUser_Cardinality("user", opLambda);
    }

    public void setUser_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUser_Missing() {
        setUser_Missing(null);
    }

    public void setUser_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setUser_Missing("user", opLambda, null);
    }

    public void setUser_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUser_Missing("user", opLambda, aggsLambda);
    }

    public void setUser_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String roles

    public void setRoles_Terms() {
        setRoles_Terms(null);
    }

    public void setRoles_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setRoles_Terms("roles", opLambda, null);
    }

    public void setRoles_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setRoles_Terms("roles", opLambda, aggsLambda);
    }

    public void setRoles_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "roles");
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

    public void setRoles_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setRoles_SignificantTerms("roles", opLambda, null);
    }

    public void setRoles_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setRoles_SignificantTerms("roles", opLambda, aggsLambda);
    }

    public void setRoles_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "roles");
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

    public void setRoles_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setRoles_IpRange("roles", opLambda, null);
    }

    public void setRoles_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setRoles_IpRange("roles", opLambda, aggsLambda);
    }

    public void setRoles_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "roles");
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

    public void setRoles_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setRoles_Count("roles", opLambda);
    }

    public void setRoles_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_Cardinality() {
        setRoles_Cardinality(null);
    }

    public void setRoles_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setRoles_Cardinality("roles", opLambda);
    }

    public void setRoles_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_Missing() {
        setRoles_Missing(null);
    }

    public void setRoles_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setRoles_Missing("roles", opLambda, null);
    }

    public void setRoles_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setRoles_Missing("roles", opLambda, aggsLambda);
    }

    public void setRoles_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
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

    public void setQueryId_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryId_Terms("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "queryId");
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

    public void setQueryId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setQueryId_SignificantTerms("queryId", opLambda, null);
    }

    public void setQueryId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryId_SignificantTerms("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "queryId");
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

    public void setQueryId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setQueryId_IpRange("queryId", opLambda, null);
    }

    public void setQueryId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryId_IpRange("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "queryId");
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

    public void setQueryId_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryId_Missing("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String clientIp

    public void setClientIp_Terms() {
        setClientIp_Terms(null);
    }

    public void setClientIp_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setClientIp_Terms("clientIp", opLambda, null);
    }

    public void setClientIp_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setClientIp_Terms("clientIp", opLambda, aggsLambda);
    }

    public void setClientIp_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "clientIp");
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

    public void setClientIp_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setClientIp_SignificantTerms("clientIp", opLambda, null);
    }

    public void setClientIp_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setClientIp_SignificantTerms("clientIp", opLambda, aggsLambda);
    }

    public void setClientIp_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "clientIp");
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

    public void setClientIp_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setClientIp_IpRange("clientIp", opLambda, null);
    }

    public void setClientIp_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setClientIp_IpRange("clientIp", opLambda, aggsLambda);
    }

    public void setClientIp_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "clientIp");
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

    public void setClientIp_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setClientIp_Count("clientIp", opLambda);
    }

    public void setClientIp_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "clientIp");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_Cardinality() {
        setClientIp_Cardinality(null);
    }

    public void setClientIp_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setClientIp_Cardinality("clientIp", opLambda);
    }

    public void setClientIp_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "clientIp");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setClientIp_Missing() {
        setClientIp_Missing(null);
    }

    public void setClientIp_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setClientIp_Missing("clientIp", opLambda, null);
    }

    public void setClientIp_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setClientIp_Missing("clientIp", opLambda, aggsLambda);
    }

    public void setClientIp_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "clientIp");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Long hitCount
    public void setHitCount_Avg() {
        setHitCount_Avg(null);
    }

    public void setHitCount_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setHitCount_Avg("hitCount", opLambda);
    }

    public void setHitCount_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Max() {
        setHitCount_Max(null);
    }

    public void setHitCount_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setHitCount_Max("hitCount", opLambda);
    }

    public void setHitCount_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Min() {
        setHitCount_Min(null);
    }

    public void setHitCount_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setHitCount_Min("hitCount", opLambda);
    }

    public void setHitCount_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Sum() {
        setHitCount_Sum(null);
    }

    public void setHitCount_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setHitCount_Sum("hitCount", opLambda);
    }

    public void setHitCount_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_ExtendedStats() {
        setHitCount_ExtendedStats(null);
    }

    public void setHitCount_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setHitCount_ExtendedStats("hitCount", opLambda);
    }

    public void setHitCount_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Stats() {
        setHitCount_Stats(null);
    }

    public void setHitCount_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setHitCount_Stats("hitCount", opLambda);
    }

    public void setHitCount_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Percentiles() {
        setHitCount_Percentiles(null);
    }

    public void setHitCount_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setHitCount_Percentiles("hitCount", opLambda);
    }

    public void setHitCount_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_PercentileRanks() {
        setHitCount_PercentileRanks(null);
    }

    public void setHitCount_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setHitCount_PercentileRanks("hitCount", opLambda);
    }

    public void setHitCount_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Histogram() {
        setHitCount_Histogram(null);
    }

    public void setHitCount_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setHitCount_Histogram("hitCount", opLambda, null);
    }

    public void setHitCount_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setHitCount_Histogram("hitCount", opLambda, aggsLambda);
    }

    public void setHitCount_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "hitCount");
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

    public void setHitCount_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setHitCount_Range("hitCount", opLambda, null);
    }

    public void setHitCount_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setHitCount_Range("hitCount", opLambda, aggsLambda);
    }

    public void setHitCount_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "hitCount");
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

    public void setHitCount_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setHitCount_Count("hitCount", opLambda);
    }

    public void setHitCount_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Cardinality() {
        setHitCount_Cardinality(null);
    }

    public void setHitCount_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setHitCount_Cardinality("hitCount", opLambda);
    }

    public void setHitCount_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHitCount_Missing() {
        setHitCount_Missing(null);
    }

    public void setHitCount_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setHitCount_Missing("hitCount", opLambda, null);
    }

    public void setHitCount_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setHitCount_Missing("hitCount", opLambda, aggsLambda);
    }

    public void setHitCount_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "hitCount");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Integer queryOffset
    public void setQueryOffset_Avg() {
        setQueryOffset_Avg(null);
    }

    public void setQueryOffset_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setQueryOffset_Avg("queryOffset", opLambda);
    }

    public void setQueryOffset_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Max() {
        setQueryOffset_Max(null);
    }

    public void setQueryOffset_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setQueryOffset_Max("queryOffset", opLambda);
    }

    public void setQueryOffset_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Min() {
        setQueryOffset_Min(null);
    }

    public void setQueryOffset_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setQueryOffset_Min("queryOffset", opLambda);
    }

    public void setQueryOffset_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Sum() {
        setQueryOffset_Sum(null);
    }

    public void setQueryOffset_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setQueryOffset_Sum("queryOffset", opLambda);
    }

    public void setQueryOffset_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_ExtendedStats() {
        setQueryOffset_ExtendedStats(null);
    }

    public void setQueryOffset_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setQueryOffset_ExtendedStats("queryOffset", opLambda);
    }

    public void setQueryOffset_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Stats() {
        setQueryOffset_Stats(null);
    }

    public void setQueryOffset_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setQueryOffset_Stats("queryOffset", opLambda);
    }

    public void setQueryOffset_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Percentiles() {
        setQueryOffset_Percentiles(null);
    }

    public void setQueryOffset_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setQueryOffset_Percentiles("queryOffset", opLambda);
    }

    public void setQueryOffset_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_PercentileRanks() {
        setQueryOffset_PercentileRanks(null);
    }

    public void setQueryOffset_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setQueryOffset_PercentileRanks("queryOffset", opLambda);
    }

    public void setQueryOffset_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Histogram() {
        setQueryOffset_Histogram(null);
    }

    public void setQueryOffset_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setQueryOffset_Histogram("queryOffset", opLambda, null);
    }

    public void setQueryOffset_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryOffset_Histogram("queryOffset", opLambda, aggsLambda);
    }

    public void setQueryOffset_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "queryOffset");
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

    public void setQueryOffset_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setQueryOffset_Range("queryOffset", opLambda, null);
    }

    public void setQueryOffset_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryOffset_Range("queryOffset", opLambda, aggsLambda);
    }

    public void setQueryOffset_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "queryOffset");
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

    public void setQueryOffset_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setQueryOffset_Count("queryOffset", opLambda);
    }

    public void setQueryOffset_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Cardinality() {
        setQueryOffset_Cardinality(null);
    }

    public void setQueryOffset_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setQueryOffset_Cardinality("queryOffset", opLambda);
    }

    public void setQueryOffset_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryOffset_Missing() {
        setQueryOffset_Missing(null);
    }

    public void setQueryOffset_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setQueryOffset_Missing("queryOffset", opLambda, null);
    }

    public void setQueryOffset_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryOffset_Missing("queryOffset", opLambda, aggsLambda);
    }

    public void setQueryOffset_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "queryOffset");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Integer queryPageSize
    public void setQueryPageSize_Avg() {
        setQueryPageSize_Avg(null);
    }

    public void setQueryPageSize_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setQueryPageSize_Avg("queryPageSize", opLambda);
    }

    public void setQueryPageSize_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Max() {
        setQueryPageSize_Max(null);
    }

    public void setQueryPageSize_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setQueryPageSize_Max("queryPageSize", opLambda);
    }

    public void setQueryPageSize_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Min() {
        setQueryPageSize_Min(null);
    }

    public void setQueryPageSize_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setQueryPageSize_Min("queryPageSize", opLambda);
    }

    public void setQueryPageSize_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Sum() {
        setQueryPageSize_Sum(null);
    }

    public void setQueryPageSize_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setQueryPageSize_Sum("queryPageSize", opLambda);
    }

    public void setQueryPageSize_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_ExtendedStats() {
        setQueryPageSize_ExtendedStats(null);
    }

    public void setQueryPageSize_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setQueryPageSize_ExtendedStats("queryPageSize", opLambda);
    }

    public void setQueryPageSize_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Stats() {
        setQueryPageSize_Stats(null);
    }

    public void setQueryPageSize_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setQueryPageSize_Stats("queryPageSize", opLambda);
    }

    public void setQueryPageSize_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Percentiles() {
        setQueryPageSize_Percentiles(null);
    }

    public void setQueryPageSize_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setQueryPageSize_Percentiles("queryPageSize", opLambda);
    }

    public void setQueryPageSize_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_PercentileRanks() {
        setQueryPageSize_PercentileRanks(null);
    }

    public void setQueryPageSize_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setQueryPageSize_PercentileRanks("queryPageSize", opLambda);
    }

    public void setQueryPageSize_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Histogram() {
        setQueryPageSize_Histogram(null);
    }

    public void setQueryPageSize_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setQueryPageSize_Histogram("queryPageSize", opLambda, null);
    }

    public void setQueryPageSize_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryPageSize_Histogram("queryPageSize", opLambda, aggsLambda);
    }

    public void setQueryPageSize_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "queryPageSize");
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

    public void setQueryPageSize_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setQueryPageSize_Range("queryPageSize", opLambda, null);
    }

    public void setQueryPageSize_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryPageSize_Range("queryPageSize", opLambda, aggsLambda);
    }

    public void setQueryPageSize_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "queryPageSize");
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

    public void setQueryPageSize_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setQueryPageSize_Count("queryPageSize", opLambda);
    }

    public void setQueryPageSize_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Cardinality() {
        setQueryPageSize_Cardinality(null);
    }

    public void setQueryPageSize_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setQueryPageSize_Cardinality("queryPageSize", opLambda);
    }

    public void setQueryPageSize_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryPageSize_Missing() {
        setQueryPageSize_Missing(null);
    }

    public void setQueryPageSize_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setQueryPageSize_Missing("queryPageSize", opLambda, null);
    }

    public void setQueryPageSize_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryPageSize_Missing("queryPageSize", opLambda, aggsLambda);
    }

    public void setQueryPageSize_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "queryPageSize");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String referer

    public void setReferer_Terms() {
        setReferer_Terms(null);
    }

    public void setReferer_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setReferer_Terms("referer", opLambda, null);
    }

    public void setReferer_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setReferer_Terms("referer", opLambda, aggsLambda);
    }

    public void setReferer_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "referer");
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

    public void setReferer_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setReferer_SignificantTerms("referer", opLambda, null);
    }

    public void setReferer_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setReferer_SignificantTerms("referer", opLambda, aggsLambda);
    }

    public void setReferer_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "referer");
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

    public void setReferer_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setReferer_IpRange("referer", opLambda, null);
    }

    public void setReferer_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setReferer_IpRange("referer", opLambda, aggsLambda);
    }

    public void setReferer_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "referer");
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

    public void setReferer_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setReferer_Count("referer", opLambda);
    }

    public void setReferer_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "referer");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_Cardinality() {
        setReferer_Cardinality(null);
    }

    public void setReferer_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setReferer_Cardinality("referer", opLambda);
    }

    public void setReferer_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "referer");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setReferer_Missing() {
        setReferer_Missing(null);
    }

    public void setReferer_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setReferer_Missing("referer", opLambda, null);
    }

    public void setReferer_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setReferer_Missing("referer", opLambda, aggsLambda);
    }

    public void setReferer_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "referer");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
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

    public void setRequestedAt_DateRange(ConditionOptionCall<DateRangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setRequestedAt_DateRange("requestedAt", opLambda, aggsLambda);
    }

    public void setRequestedAt_DateRange(String name, ConditionOptionCall<DateRangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        DateRangeBuilder builder = regDateRangeA(name, "requestedAt");
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

    public void setRequestedAt_DateHistogram(ConditionOptionCall<DateHistogramBuilder> opLambda) {
        setRequestedAt_DateHistogram("requestedAt", opLambda, null);
    }

    public void setRequestedAt_DateHistogram(ConditionOptionCall<DateHistogramBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setRequestedAt_DateHistogram("requestedAt", opLambda, aggsLambda);
    }

    public void setRequestedAt_DateHistogram(String name, ConditionOptionCall<DateHistogramBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        DateHistogramBuilder builder = regDateHistogramA(name, "requestedAt");
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

    public void setRequestedAt_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setRequestedAt_Missing("requestedAt", opLambda, aggsLambda);
    }

    public void setRequestedAt_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "requestedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Long responseTime
    public void setResponseTime_Avg() {
        setResponseTime_Avg(null);
    }

    public void setResponseTime_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setResponseTime_Avg("responseTime", opLambda);
    }

    public void setResponseTime_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Max() {
        setResponseTime_Max(null);
    }

    public void setResponseTime_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setResponseTime_Max("responseTime", opLambda);
    }

    public void setResponseTime_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Min() {
        setResponseTime_Min(null);
    }

    public void setResponseTime_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setResponseTime_Min("responseTime", opLambda);
    }

    public void setResponseTime_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Sum() {
        setResponseTime_Sum(null);
    }

    public void setResponseTime_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setResponseTime_Sum("responseTime", opLambda);
    }

    public void setResponseTime_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_ExtendedStats() {
        setResponseTime_ExtendedStats(null);
    }

    public void setResponseTime_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setResponseTime_ExtendedStats("responseTime", opLambda);
    }

    public void setResponseTime_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Stats() {
        setResponseTime_Stats(null);
    }

    public void setResponseTime_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setResponseTime_Stats("responseTime", opLambda);
    }

    public void setResponseTime_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Percentiles() {
        setResponseTime_Percentiles(null);
    }

    public void setResponseTime_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setResponseTime_Percentiles("responseTime", opLambda);
    }

    public void setResponseTime_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_PercentileRanks() {
        setResponseTime_PercentileRanks(null);
    }

    public void setResponseTime_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setResponseTime_PercentileRanks("responseTime", opLambda);
    }

    public void setResponseTime_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Histogram() {
        setResponseTime_Histogram(null);
    }

    public void setResponseTime_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setResponseTime_Histogram("responseTime", opLambda, null);
    }

    public void setResponseTime_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setResponseTime_Histogram("responseTime", opLambda, aggsLambda);
    }

    public void setResponseTime_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "responseTime");
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

    public void setResponseTime_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setResponseTime_Range("responseTime", opLambda, null);
    }

    public void setResponseTime_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setResponseTime_Range("responseTime", opLambda, aggsLambda);
    }

    public void setResponseTime_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "responseTime");
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

    public void setResponseTime_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setResponseTime_Count("responseTime", opLambda);
    }

    public void setResponseTime_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Cardinality() {
        setResponseTime_Cardinality(null);
    }

    public void setResponseTime_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setResponseTime_Cardinality("responseTime", opLambda);
    }

    public void setResponseTime_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setResponseTime_Missing() {
        setResponseTime_Missing(null);
    }

    public void setResponseTime_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setResponseTime_Missing("responseTime", opLambda, null);
    }

    public void setResponseTime_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setResponseTime_Missing("responseTime", opLambda, aggsLambda);
    }

    public void setResponseTime_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "responseTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Long queryTime
    public void setQueryTime_Avg() {
        setQueryTime_Avg(null);
    }

    public void setQueryTime_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setQueryTime_Avg("queryTime", opLambda);
    }

    public void setQueryTime_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Max() {
        setQueryTime_Max(null);
    }

    public void setQueryTime_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setQueryTime_Max("queryTime", opLambda);
    }

    public void setQueryTime_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Min() {
        setQueryTime_Min(null);
    }

    public void setQueryTime_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setQueryTime_Min("queryTime", opLambda);
    }

    public void setQueryTime_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Sum() {
        setQueryTime_Sum(null);
    }

    public void setQueryTime_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setQueryTime_Sum("queryTime", opLambda);
    }

    public void setQueryTime_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_ExtendedStats() {
        setQueryTime_ExtendedStats(null);
    }

    public void setQueryTime_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setQueryTime_ExtendedStats("queryTime", opLambda);
    }

    public void setQueryTime_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Stats() {
        setQueryTime_Stats(null);
    }

    public void setQueryTime_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setQueryTime_Stats("queryTime", opLambda);
    }

    public void setQueryTime_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Percentiles() {
        setQueryTime_Percentiles(null);
    }

    public void setQueryTime_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setQueryTime_Percentiles("queryTime", opLambda);
    }

    public void setQueryTime_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_PercentileRanks() {
        setQueryTime_PercentileRanks(null);
    }

    public void setQueryTime_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setQueryTime_PercentileRanks("queryTime", opLambda);
    }

    public void setQueryTime_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Histogram() {
        setQueryTime_Histogram(null);
    }

    public void setQueryTime_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setQueryTime_Histogram("queryTime", opLambda, null);
    }

    public void setQueryTime_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryTime_Histogram("queryTime", opLambda, aggsLambda);
    }

    public void setQueryTime_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "queryTime");
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

    public void setQueryTime_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setQueryTime_Range("queryTime", opLambda, null);
    }

    public void setQueryTime_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryTime_Range("queryTime", opLambda, aggsLambda);
    }

    public void setQueryTime_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "queryTime");
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

    public void setQueryTime_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setQueryTime_Count("queryTime", opLambda);
    }

    public void setQueryTime_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Cardinality() {
        setQueryTime_Cardinality(null);
    }

    public void setQueryTime_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setQueryTime_Cardinality("queryTime", opLambda);
    }

    public void setQueryTime_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setQueryTime_Missing() {
        setQueryTime_Missing(null);
    }

    public void setQueryTime_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setQueryTime_Missing("queryTime", opLambda, null);
    }

    public void setQueryTime_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setQueryTime_Missing("queryTime", opLambda, aggsLambda);
    }

    public void setQueryTime_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "queryTime");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String searchWord

    public void setSearchWord_Terms() {
        setSearchWord_Terms(null);
    }

    public void setSearchWord_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setSearchWord_Terms("searchWord", opLambda, null);
    }

    public void setSearchWord_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setSearchWord_Terms("searchWord", opLambda, aggsLambda);
    }

    public void setSearchWord_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "searchWord");
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

    public void setSearchWord_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setSearchWord_SignificantTerms("searchWord", opLambda, null);
    }

    public void setSearchWord_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setSearchWord_SignificantTerms("searchWord", opLambda, aggsLambda);
    }

    public void setSearchWord_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "searchWord");
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

    public void setSearchWord_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setSearchWord_IpRange("searchWord", opLambda, null);
    }

    public void setSearchWord_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setSearchWord_IpRange("searchWord", opLambda, aggsLambda);
    }

    public void setSearchWord_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "searchWord");
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

    public void setSearchWord_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setSearchWord_Count("searchWord", opLambda);
    }

    public void setSearchWord_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "searchWord");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_Cardinality() {
        setSearchWord_Cardinality(null);
    }

    public void setSearchWord_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setSearchWord_Cardinality("searchWord", opLambda);
    }

    public void setSearchWord_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "searchWord");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchWord_Missing() {
        setSearchWord_Missing(null);
    }

    public void setSearchWord_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setSearchWord_Missing("searchWord", opLambda, null);
    }

    public void setSearchWord_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setSearchWord_Missing("searchWord", opLambda, aggsLambda);
    }

    public void setSearchWord_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "searchWord");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String userAgent

    public void setUserAgent_Terms() {
        setUserAgent_Terms(null);
    }

    public void setUserAgent_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setUserAgent_Terms("userAgent", opLambda, null);
    }

    public void setUserAgent_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserAgent_Terms("userAgent", opLambda, aggsLambda);
    }

    public void setUserAgent_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "userAgent");
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

    public void setUserAgent_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setUserAgent_SignificantTerms("userAgent", opLambda, null);
    }

    public void setUserAgent_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserAgent_SignificantTerms("userAgent", opLambda, aggsLambda);
    }

    public void setUserAgent_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "userAgent");
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

    public void setUserAgent_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setUserAgent_IpRange("userAgent", opLambda, null);
    }

    public void setUserAgent_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserAgent_IpRange("userAgent", opLambda, aggsLambda);
    }

    public void setUserAgent_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "userAgent");
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

    public void setUserAgent_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setUserAgent_Count("userAgent", opLambda);
    }

    public void setUserAgent_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "userAgent");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_Cardinality() {
        setUserAgent_Cardinality(null);
    }

    public void setUserAgent_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setUserAgent_Cardinality("userAgent", opLambda);
    }

    public void setUserAgent_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "userAgent");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUserAgent_Missing() {
        setUserAgent_Missing(null);
    }

    public void setUserAgent_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setUserAgent_Missing("userAgent", opLambda, null);
    }

    public void setUserAgent_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserAgent_Missing("userAgent", opLambda, aggsLambda);
    }

    public void setUserAgent_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "userAgent");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
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

    public void setUserInfoId_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserInfoId_Terms("userInfoId", opLambda, aggsLambda);
    }

    public void setUserInfoId_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "userInfoId");
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

    public void setUserInfoId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setUserInfoId_SignificantTerms("userInfoId", opLambda, null);
    }

    public void setUserInfoId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserInfoId_SignificantTerms("userInfoId", opLambda, aggsLambda);
    }

    public void setUserInfoId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "userInfoId");
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

    public void setUserInfoId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setUserInfoId_IpRange("userInfoId", opLambda, null);
    }

    public void setUserInfoId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserInfoId_IpRange("userInfoId", opLambda, aggsLambda);
    }

    public void setUserInfoId_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "userInfoId");
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

    public void setUserInfoId_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserInfoId_Missing("userInfoId", opLambda, aggsLambda);
    }

    public void setUserInfoId_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "userInfoId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
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

    public void setUserSessionId_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserSessionId_Terms("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "userSessionId");
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

    public void setUserSessionId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setUserSessionId_SignificantTerms("userSessionId", opLambda, null);
    }

    public void setUserSessionId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserSessionId_SignificantTerms("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "userSessionId");
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

    public void setUserSessionId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setUserSessionId_IpRange("userSessionId", opLambda, null);
    }

    public void setUserSessionId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserSessionId_IpRange("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "userSessionId");
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

    public void setUserSessionId_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setUserSessionId_Missing("userSessionId", opLambda, aggsLambda);
    }

    public void setUserSessionId_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "userSessionId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchLogCA ca = new SearchLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String languages

    public void setLanguages_Terms() {
        setLanguages_Terms(null);
    }

    public void setLanguages_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setLanguages_Terms("languages", opLambda, null);
    }

    public void setLanguages_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setLanguages_Terms("languages", opLambda, aggsLambda);
    }

    public void setLanguages_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "languages");
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

    public void setLanguages_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setLanguages_SignificantTerms("languages", opLambda, null);
    }

    public void setLanguages_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setLanguages_SignificantTerms("languages", opLambda, aggsLambda);
    }

    public void setLanguages_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsSearchLogCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "languages");
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

    public void setLanguages_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setLanguages_IpRange("languages", opLambda, null);
    }

    public void setLanguages_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setLanguages_IpRange("languages", opLambda, aggsLambda);
    }

    public void setLanguages_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "languages");
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

    public void setLanguages_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setLanguages_Count("languages", opLambda);
    }

    public void setLanguages_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "languages");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLanguages_Cardinality() {
        setLanguages_Cardinality(null);
    }

    public void setLanguages_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setLanguages_Cardinality("languages", opLambda);
    }

    public void setLanguages_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "languages");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLanguages_Missing() {
        setLanguages_Missing(null);
    }

    public void setLanguages_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setLanguages_Missing("languages", opLambda, null);
    }

    public void setLanguages_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        setLanguages_Missing("languages", opLambda, aggsLambda);
    }

    public void setLanguages_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsSearchLogCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "languages");
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
