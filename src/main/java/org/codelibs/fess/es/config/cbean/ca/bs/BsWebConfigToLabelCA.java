/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
import org.elasticsearch.search.aggregations.bucket.global.GlobalAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.missing.MissingAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.IpRangeAggregationBuilder;
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

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
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

    public void setLabelTypeId_Terms() {
        setLabelTypeId_Terms(null);
    }

    public void setLabelTypeId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setLabelTypeId_Terms("labelTypeId", opLambda, null);
    }

    public void setLabelTypeId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        setLabelTypeId_Terms("labelTypeId", opLambda, aggsLambda);
    }

    public void setLabelTypeId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "labelTypeId");
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

    public void setLabelTypeId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setLabelTypeId_SignificantTerms("labelTypeId", opLambda, null);
    }

    public void setLabelTypeId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        setLabelTypeId_SignificantTerms("labelTypeId", opLambda, aggsLambda);
    }

    public void setLabelTypeId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "labelTypeId");
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

    public void setLabelTypeId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setLabelTypeId_IpRange("labelTypeId", opLambda, null);
    }

    public void setLabelTypeId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        setLabelTypeId_IpRange("labelTypeId", opLambda, aggsLambda);
    }

    public void setLabelTypeId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "labelTypeId");
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

    public void setLabelTypeId_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setLabelTypeId_Count("labelTypeId", opLambda);
    }

    public void setLabelTypeId_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_Cardinality() {
        setLabelTypeId_Cardinality(null);
    }

    public void setLabelTypeId_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setLabelTypeId_Cardinality("labelTypeId", opLambda);
    }

    public void setLabelTypeId_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabelTypeId_Missing() {
        setLabelTypeId_Missing(null);
    }

    public void setLabelTypeId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setLabelTypeId_Missing("labelTypeId", opLambda, null);
    }

    public void setLabelTypeId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        setLabelTypeId_Missing("labelTypeId", opLambda, aggsLambda);
    }

    public void setLabelTypeId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToLabelCA ca = new WebConfigToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setWebConfigId_Terms() {
        setWebConfigId_Terms(null);
    }

    public void setWebConfigId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setWebConfigId_Terms("webConfigId", opLambda, null);
    }

    public void setWebConfigId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        setWebConfigId_Terms("webConfigId", opLambda, aggsLambda);
    }

    public void setWebConfigId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "webConfigId");
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

    public void setWebConfigId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setWebConfigId_SignificantTerms("webConfigId", opLambda, null);
    }

    public void setWebConfigId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        setWebConfigId_SignificantTerms("webConfigId", opLambda, aggsLambda);
    }

    public void setWebConfigId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "webConfigId");
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

    public void setWebConfigId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setWebConfigId_IpRange("webConfigId", opLambda, null);
    }

    public void setWebConfigId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        setWebConfigId_IpRange("webConfigId", opLambda, aggsLambda);
    }

    public void setWebConfigId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "webConfigId");
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

    public void setWebConfigId_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setWebConfigId_Count("webConfigId", opLambda);
    }

    public void setWebConfigId_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "webConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Cardinality() {
        setWebConfigId_Cardinality(null);
    }

    public void setWebConfigId_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setWebConfigId_Cardinality("webConfigId", opLambda);
    }

    public void setWebConfigId_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "webConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setWebConfigId_Missing() {
        setWebConfigId_Missing(null);
    }

    public void setWebConfigId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setWebConfigId_Missing("webConfigId", opLambda, null);
    }

    public void setWebConfigId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        setWebConfigId_Missing("webConfigId", opLambda, aggsLambda);
    }

    public void setWebConfigId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToLabelCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "webConfigId");
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
