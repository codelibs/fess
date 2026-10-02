/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.opensearch.log.cbean.ca.bs;

import java.util.List;

import org.codelibs.fess.opensearch.log.allcommon.EsAbstractConditionAggregation;
import org.codelibs.fess.opensearch.log.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.opensearch.log.cbean.ca.TagLogCA;
import org.codelibs.fess.opensearch.log.cbean.cq.TagLogCQ;
import org.codelibs.fess.opensearch.log.cbean.cq.bs.BsTagLogCQ;

import org.codelibs.fesen.opensearch.search.aggregations.AbstractAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.AggregationBuilders;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.global.GlobalAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.histogram.HistogramAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.missing.MissingAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.range.DateRangeAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.range.IpRangeAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.sampler.SamplerAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.terms.SignificantTermsAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.*;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsTagLogCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsTagLogCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        TagLogCQ cq = new TagLogCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
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

    public void setCreatedAt_DateRange(ConditionOptionCall<DateRangeAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        setCreatedAt_DateRange("createdAt", opLambda, aggsLambda);
    }

    public void setCreatedAt_DateRange(String name, ConditionOptionCall<DateRangeAggregationBuilder> opLambda,
            OperatorCall<BsTagLogCA> aggsLambda) {
        DateRangeAggregationBuilder builder = regDateRangeA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
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
            OperatorCall<BsTagLogCA> aggsLambda) {
        setCreatedAt_DateHistogram("createdAt", opLambda, aggsLambda);
    }

    public void setCreatedAt_DateHistogram(String name, ConditionOptionCall<DateHistogramAggregationBuilder> opLambda,
            OperatorCall<BsTagLogCA> aggsLambda) {
        DateHistogramAggregationBuilder builder = regDateHistogramA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
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

    public void setCreatedAt_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        setCreatedAt_Missing("createdAt", opLambda, aggsLambda);
    }

    public void setCreatedAt_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsTagLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
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

    public void setDocId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        setDocId_Terms("docId", opLambda, aggsLambda);
    }

    public void setDocId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
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
            OperatorCall<BsTagLogCA> aggsLambda) {
        setDocId_SignificantTerms("docId", opLambda, aggsLambda);
    }

    public void setDocId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsTagLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
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

    public void setDocId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        setDocId_IpRange("docId", opLambda, aggsLambda);
    }

    public void setDocId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsTagLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
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

    public void setDocId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        setDocId_Missing("docId", opLambda, aggsLambda);
    }

    public void setDocId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsTagLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "docId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTag_Terms() {
        setTag_Terms(null);
    }

    public void setTag_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setTag_Terms("tag", opLambda, null);
    }

    public void setTag_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        setTag_Terms("tag", opLambda, aggsLambda);
    }

    public void setTag_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "tag");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTag_SignificantTerms() {
        setTag_SignificantTerms(null);
    }

    public void setTag_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setTag_SignificantTerms("tag", opLambda, null);
    }

    public void setTag_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsTagLogCA> aggsLambda) {
        setTag_SignificantTerms("tag", opLambda, aggsLambda);
    }

    public void setTag_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsTagLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "tag");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTag_IpRange() {
        setTag_IpRange(null);
    }

    public void setTag_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setTag_IpRange("tag", opLambda, null);
    }

    public void setTag_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        setTag_IpRange("tag", opLambda, aggsLambda);
    }

    public void setTag_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "tag");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTag_Count() {
        setTag_Count(null);
    }

    public void setTag_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setTag_Count("tag", opLambda);
    }

    public void setTag_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "tag");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTag_Cardinality() {
        setTag_Cardinality(null);
    }

    public void setTag_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setTag_Cardinality("tag", opLambda);
    }

    public void setTag_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "tag");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTag_Missing() {
        setTag_Missing(null);
    }

    public void setTag_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setTag_Missing("tag", opLambda, null);
    }

    public void setTag_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        setTag_Missing("tag", opLambda, aggsLambda);
    }

    public void setTag_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "tag");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
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

    public void setUrl_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        setUrl_Terms("url", opLambda, aggsLambda);
    }

    public void setUrl_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
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
            OperatorCall<BsTagLogCA> aggsLambda) {
        setUrl_SignificantTerms("url", opLambda, aggsLambda);
    }

    public void setUrl_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsTagLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
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

    public void setUrl_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        setUrl_IpRange("url", opLambda, aggsLambda);
    }

    public void setUrl_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
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

    public void setUrl_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        setUrl_Missing("url", opLambda, aggsLambda);
    }

    public void setUrl_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "url");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
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

    public void setUser_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        setUser_Terms("user", opLambda, aggsLambda);
    }

    public void setUser_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
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
            OperatorCall<BsTagLogCA> aggsLambda) {
        setUser_SignificantTerms("user", opLambda, aggsLambda);
    }

    public void setUser_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsTagLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
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

    public void setUser_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        setUser_IpRange("user", opLambda, aggsLambda);
    }

    public void setUser_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
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

    public void setUser_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        setUser_Missing("user", opLambda, aggsLambda);
    }

    public void setUser_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsTagLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "user");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            TagLogCA ca = new TagLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
