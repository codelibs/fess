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
import org.codelibs.fess.es.log.cbean.ca.SearchFieldLogCA;
import org.codelibs.fess.es.log.cbean.cq.SearchFieldLogCQ;
import org.codelibs.fess.es.log.cbean.cq.bs.BsSearchFieldLogCQ;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.global.GlobalAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.missing.MissingAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.ip.IpRangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.sampler.SamplerAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.significant.SignificantTermsAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.scripted.ScriptedMetricAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHitsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsSearchFieldLogCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsSearchFieldLogCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        SearchFieldLogCQ cq = new SearchFieldLogCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchFieldLogCA ca = new SearchFieldLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchFieldLogCA ca = new SearchFieldLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchFieldLogCA ca = new SearchFieldLogCA();
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

    public void setName_Terms() {
        setName_Terms(null);
    }

    public void setName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setName_Terms("name", opLambda, null);
    }

    public void setName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        setName_Terms("name", opLambda, aggsLambda);
    }

    public void setName_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchFieldLogCA ca = new SearchFieldLogCA();
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
            OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        setName_SignificantTerms("name", opLambda, aggsLambda);
    }

    public void setName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchFieldLogCA ca = new SearchFieldLogCA();
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

    public void setName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        setName_IpRange("name", opLambda, aggsLambda);
    }

    public void setName_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchFieldLogCA ca = new SearchFieldLogCA();
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

    public void setName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        setName_Missing("name", opLambda, aggsLambda);
    }

    public void setName_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchFieldLogCA ca = new SearchFieldLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSearchLogId_Terms() {
        setSearchLogId_Terms(null);
    }

    public void setSearchLogId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setSearchLogId_Terms("searchLogId", opLambda, null);
    }

    public void setSearchLogId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        setSearchLogId_Terms("searchLogId", opLambda, aggsLambda);
    }

    public void setSearchLogId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "searchLogId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchFieldLogCA ca = new SearchFieldLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSearchLogId_SignificantTerms() {
        setSearchLogId_SignificantTerms(null);
    }

    public void setSearchLogId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setSearchLogId_SignificantTerms("searchLogId", opLambda, null);
    }

    public void setSearchLogId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        setSearchLogId_SignificantTerms("searchLogId", opLambda, aggsLambda);
    }

    public void setSearchLogId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "searchLogId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchFieldLogCA ca = new SearchFieldLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSearchLogId_IpRange() {
        setSearchLogId_IpRange(null);
    }

    public void setSearchLogId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setSearchLogId_IpRange("searchLogId", opLambda, null);
    }

    public void setSearchLogId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        setSearchLogId_IpRange("searchLogId", opLambda, aggsLambda);
    }

    public void setSearchLogId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "searchLogId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchFieldLogCA ca = new SearchFieldLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSearchLogId_Count() {
        setSearchLogId_Count(null);
    }

    public void setSearchLogId_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setSearchLogId_Count("searchLogId", opLambda);
    }

    public void setSearchLogId_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "searchLogId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchLogId_Cardinality() {
        setSearchLogId_Cardinality(null);
    }

    public void setSearchLogId_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setSearchLogId_Cardinality("searchLogId", opLambda);
    }

    public void setSearchLogId_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "searchLogId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSearchLogId_Missing() {
        setSearchLogId_Missing(null);
    }

    public void setSearchLogId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setSearchLogId_Missing("searchLogId", opLambda, null);
    }

    public void setSearchLogId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        setSearchLogId_Missing("searchLogId", opLambda, aggsLambda);
    }

    public void setSearchLogId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "searchLogId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchFieldLogCA ca = new SearchFieldLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setValue_Terms() {
        setValue_Terms(null);
    }

    public void setValue_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setValue_Terms("value", opLambda, null);
    }

    public void setValue_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        setValue_Terms("value", opLambda, aggsLambda);
    }

    public void setValue_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "value");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchFieldLogCA ca = new SearchFieldLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setValue_SignificantTerms() {
        setValue_SignificantTerms(null);
    }

    public void setValue_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setValue_SignificantTerms("value", opLambda, null);
    }

    public void setValue_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        setValue_SignificantTerms("value", opLambda, aggsLambda);
    }

    public void setValue_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "value");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchFieldLogCA ca = new SearchFieldLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setValue_IpRange() {
        setValue_IpRange(null);
    }

    public void setValue_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setValue_IpRange("value", opLambda, null);
    }

    public void setValue_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        setValue_IpRange("value", opLambda, aggsLambda);
    }

    public void setValue_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "value");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchFieldLogCA ca = new SearchFieldLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setValue_Count() {
        setValue_Count(null);
    }

    public void setValue_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setValue_Count("value", opLambda);
    }

    public void setValue_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "value");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_Cardinality() {
        setValue_Cardinality(null);
    }

    public void setValue_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setValue_Cardinality("value", opLambda);
    }

    public void setValue_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "value");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setValue_Missing() {
        setValue_Missing(null);
    }

    public void setValue_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setValue_Missing("value", opLambda, null);
    }

    public void setValue_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        setValue_Missing("value", opLambda, aggsLambda);
    }

    public void setValue_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsSearchFieldLogCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "value");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            SearchFieldLogCA ca = new SearchFieldLogCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
