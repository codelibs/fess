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
import org.codelibs.fess.es.log.cbean.ca.FavoriteLogCA;
import org.codelibs.fess.es.log.cbean.cq.FavoriteLogCQ;
import org.codelibs.fess.es.log.cbean.cq.bs.BsFavoriteLogCQ;
import org.opensearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.opensearch.search.aggregations.bucket.global.GlobalAggregationBuilder;
import org.opensearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.opensearch.search.aggregations.bucket.missing.MissingAggregationBuilder;
import org.opensearch.search.aggregations.bucket.range.DateRangeAggregationBuilder;
import org.opensearch.search.aggregations.bucket.range.IpRangeAggregationBuilder;
import org.opensearch.search.aggregations.bucket.sampler.SamplerAggregationBuilder;
import org.opensearch.search.aggregations.bucket.terms.SignificantTermsAggregationBuilder;
import org.opensearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.opensearch.search.aggregations.metrics.CardinalityAggregationBuilder;
import org.opensearch.search.aggregations.metrics.ScriptedMetricAggregationBuilder;
import org.opensearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.opensearch.search.aggregations.metrics.ValueCountAggregationBuilder;

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

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
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

    public void setCreatedAt_DateRange() {
        setCreatedAt_DateRange(null);
    }

    public void setCreatedAt_DateRange(ConditionOptionCall<DateRangeAggregationBuilder> opLambda) {
        setCreatedAt_DateRange("createdAt", opLambda, null);
    }

    public void setCreatedAt_DateRange(ConditionOptionCall<DateRangeAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setCreatedAt_DateRange("createdAt", opLambda, aggsLambda);
    }

    public void setCreatedAt_DateRange(String name, ConditionOptionCall<DateRangeAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        DateRangeAggregationBuilder builder = regDateRangeA(name, "createdAt");
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

    public void setCreatedAt_DateHistogram(ConditionOptionCall<DateHistogramAggregationBuilder> opLambda) {
        setCreatedAt_DateHistogram("createdAt", opLambda, null);
    }

    public void setCreatedAt_DateHistogram(ConditionOptionCall<DateHistogramAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setCreatedAt_DateHistogram("createdAt", opLambda, aggsLambda);
    }

    public void setCreatedAt_DateHistogram(String name, ConditionOptionCall<DateHistogramAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        DateHistogramAggregationBuilder builder = regDateHistogramA(name, "createdAt");
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

    public void setCreatedAt_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setCreatedAt_Count("createdAt", opLambda);
    }

    public void setCreatedAt_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Cardinality() {
        setCreatedAt_Cardinality(null);
    }

    public void setCreatedAt_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setCreatedAt_Cardinality("createdAt", opLambda);
    }

    public void setCreatedAt_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Missing() {
        setCreatedAt_Missing(null);
    }

    public void setCreatedAt_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setCreatedAt_Missing("createdAt", opLambda, null);
    }

    public void setCreatedAt_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setCreatedAt_Missing("createdAt", opLambda, aggsLambda);
    }

    public void setCreatedAt_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
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

    public void setDocId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setDocId_Terms("docId", opLambda, aggsLambda);
    }

    public void setDocId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "docId");
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

    public void setDocId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setDocId_SignificantTerms("docId", opLambda, null);
    }

    public void setDocId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setDocId_SignificantTerms("docId", opLambda, aggsLambda);
    }

    public void setDocId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "docId");
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

    public void setDocId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setDocId_IpRange("docId", opLambda, null);
    }

    public void setDocId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setDocId_IpRange("docId", opLambda, aggsLambda);
    }

    public void setDocId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "docId");
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

    public void setDocId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setDocId_Missing("docId", opLambda, aggsLambda);
    }

    public void setDocId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
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

    public void setQueryId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setQueryId_Terms("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "queryId");
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

    public void setQueryId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setQueryId_SignificantTerms("queryId", opLambda, null);
    }

    public void setQueryId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setQueryId_SignificantTerms("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "queryId");
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

    public void setQueryId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setQueryId_IpRange("queryId", opLambda, null);
    }

    public void setQueryId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setQueryId_IpRange("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "queryId");
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

    public void setQueryId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setQueryId_Missing("queryId", opLambda, aggsLambda);
    }

    public void setQueryId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "queryId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
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

    public void setUrl_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setUrl_Terms("url", opLambda, aggsLambda);
    }

    public void setUrl_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "url");
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

    public void setUrl_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setUrl_SignificantTerms("url", opLambda, null);
    }

    public void setUrl_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setUrl_SignificantTerms("url", opLambda, aggsLambda);
    }

    public void setUrl_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "url");
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

    public void setUrl_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setUrl_IpRange("url", opLambda, null);
    }

    public void setUrl_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setUrl_IpRange("url", opLambda, aggsLambda);
    }

    public void setUrl_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "url");
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

    public void setUrl_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setUrl_Missing("url", opLambda, aggsLambda);
    }

    public void setUrl_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            FavoriteLogCA ca = new FavoriteLogCA();
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

    public void setUserInfoId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setUserInfoId_Terms("userInfoId", opLambda, aggsLambda);
    }

    public void setUserInfoId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "userInfoId");
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

    public void setUserInfoId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setUserInfoId_SignificantTerms("userInfoId", opLambda, null);
    }

    public void setUserInfoId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setUserInfoId_SignificantTerms("userInfoId", opLambda, aggsLambda);
    }

    public void setUserInfoId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "userInfoId");
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

    public void setUserInfoId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setUserInfoId_IpRange("userInfoId", opLambda, null);
    }

    public void setUserInfoId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setUserInfoId_IpRange("userInfoId", opLambda, aggsLambda);
    }

    public void setUserInfoId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "userInfoId");
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

    public void setUserInfoId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsFavoriteLogCA> aggsLambda) {
        setUserInfoId_Missing("userInfoId", opLambda, aggsLambda);
    }

    public void setUserInfoId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsFavoriteLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "userInfoId");
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
