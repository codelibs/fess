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
package org.codelibs.fess.es.config.cbean.ca.bs;

import org.codelibs.fess.es.config.allcommon.EsAbstractConditionAggregation;
import org.codelibs.fess.es.config.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.config.cbean.ca.WebConfigToLabelCA;
import org.codelibs.fess.es.config.cbean.cq.WebConfigToLabelCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsWebConfigToLabelCQ;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.global.GlobalBuilder;
import org.elasticsearch.search.aggregations.bucket.missing.MissingBuilder;
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
public abstract class BsWebConfigToLabelCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsWebConfigToLabelCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        WebConfigToLabelCQ cq = new WebConfigToLabelCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToLabelCA ca = new WebConfigToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalBuilder> opLambda, OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        GlobalBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToLabelCA ca = new WebConfigToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToLabelCA ca = new WebConfigToLabelCA();
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

    // String labelTypeId

    public void setLabelTypeId_Terms() {
        setLabelTypeId_Terms(null);
    }

    public void setLabelTypeId_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setLabelTypeId_Terms("labelTypeId", opLambda, null);
    }

    public void setLabelTypeId_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        setLabelTypeId_Terms("labelTypeId", opLambda, aggsLambda);
    }

    public void setLabelTypeId_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToLabelCA ca = new WebConfigToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLabelTypeId_SignificantTerms() {
        setLabelTypeId_SignificantTerms(null);
    }

    public void setLabelTypeId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setLabelTypeId_SignificantTerms("labelTypeId", opLambda, null);
    }

    public void setLabelTypeId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        setLabelTypeId_SignificantTerms("labelTypeId", opLambda, aggsLambda);
    }

    public void setLabelTypeId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToLabelCA ca = new WebConfigToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLabelTypeId_IpRange() {
        setLabelTypeId_IpRange(null);
    }

    public void setLabelTypeId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setLabelTypeId_IpRange("labelTypeId", opLambda, null);
    }

    public void setLabelTypeId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        setLabelTypeId_IpRange("labelTypeId", opLambda, aggsLambda);
    }

    public void setLabelTypeId_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToLabelCA ca = new WebConfigToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLabelTypeId_Count() {
        setLabelTypeId_Count(null);
    }

    public void setLabelTypeId_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setLabelTypeId_Count("labelTypeId", opLambda);
    }

    public void setLabelTypeId_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_Cardinality() {
        setLabelTypeId_Cardinality(null);
    }

    public void setLabelTypeId_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setLabelTypeId_Cardinality("labelTypeId", opLambda);
    }

    public void setLabelTypeId_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_Missing() {
        setLabelTypeId_Missing(null);
    }

    public void setLabelTypeId_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setLabelTypeId_Missing("labelTypeId", opLambda, null);
    }

    public void setLabelTypeId_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        setLabelTypeId_Missing("labelTypeId", opLambda, aggsLambda);
    }

    public void setLabelTypeId_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToLabelCA ca = new WebConfigToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String webConfigId

    public void setWebConfigId_Terms() {
        setWebConfigId_Terms(null);
    }

    public void setWebConfigId_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setWebConfigId_Terms("webConfigId", opLambda, null);
    }

    public void setWebConfigId_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        setWebConfigId_Terms("webConfigId", opLambda, aggsLambda);
    }

    public void setWebConfigId_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "webConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToLabelCA ca = new WebConfigToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setWebConfigId_SignificantTerms() {
        setWebConfigId_SignificantTerms(null);
    }

    public void setWebConfigId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setWebConfigId_SignificantTerms("webConfigId", opLambda, null);
    }

    public void setWebConfigId_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        setWebConfigId_SignificantTerms("webConfigId", opLambda, aggsLambda);
    }

    public void setWebConfigId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "webConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToLabelCA ca = new WebConfigToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setWebConfigId_IpRange() {
        setWebConfigId_IpRange(null);
    }

    public void setWebConfigId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setWebConfigId_IpRange("webConfigId", opLambda, null);
    }

    public void setWebConfigId_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        setWebConfigId_IpRange("webConfigId", opLambda, aggsLambda);
    }

    public void setWebConfigId_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "webConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToLabelCA ca = new WebConfigToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setWebConfigId_Count() {
        setWebConfigId_Count(null);
    }

    public void setWebConfigId_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setWebConfigId_Count("webConfigId", opLambda);
    }

    public void setWebConfigId_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "webConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Cardinality() {
        setWebConfigId_Cardinality(null);
    }

    public void setWebConfigId_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setWebConfigId_Cardinality("webConfigId", opLambda);
    }

    public void setWebConfigId_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "webConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Missing() {
        setWebConfigId_Missing(null);
    }

    public void setWebConfigId_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setWebConfigId_Missing("webConfigId", opLambda, null);
    }

    public void setWebConfigId_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        setWebConfigId_Missing("webConfigId", opLambda, aggsLambda);
    }

    public void setWebConfigId_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "webConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToLabelCA ca = new WebConfigToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
