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
package org.codelibs.fess.es.config.cbean.ca.bs;

import org.codelibs.fess.es.config.allcommon.EsAbstractConditionAggregation;
import org.codelibs.fess.es.config.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.config.cbean.ca.WebConfigToRoleCA;
import org.codelibs.fess.es.config.cbean.cq.WebConfigToRoleCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsWebConfigToRoleCQ;
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
public abstract class BsWebConfigToRoleCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsWebConfigToRoleCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsWebConfigToRoleCA> aggsLambda) {
        WebConfigToRoleCQ cq = new WebConfigToRoleCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToRoleCA ca = new WebConfigToRoleCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsWebConfigToRoleCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToRoleCA ca = new WebConfigToRoleCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsWebConfigToRoleCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToRoleCA ca = new WebConfigToRoleCA();
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

    public void setRoleTypeId_Terms() {
        setRoleTypeId_Terms(null);
    }

    public void setRoleTypeId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setRoleTypeId_Terms("roleTypeId", opLambda, null);
    }

    public void setRoleTypeId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsWebConfigToRoleCA> aggsLambda) {
        setRoleTypeId_Terms("roleTypeId", opLambda, aggsLambda);
    }

    public void setRoleTypeId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToRoleCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "roleTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToRoleCA ca = new WebConfigToRoleCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRoleTypeId_SignificantTerms() {
        setRoleTypeId_SignificantTerms(null);
    }

    public void setRoleTypeId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setRoleTypeId_SignificantTerms("roleTypeId", opLambda, null);
    }

    public void setRoleTypeId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToRoleCA> aggsLambda) {
        setRoleTypeId_SignificantTerms("roleTypeId", opLambda, aggsLambda);
    }

    public void setRoleTypeId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToRoleCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "roleTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToRoleCA ca = new WebConfigToRoleCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRoleTypeId_IpRange() {
        setRoleTypeId_IpRange(null);
    }

    public void setRoleTypeId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setRoleTypeId_IpRange("roleTypeId", opLambda, null);
    }

    public void setRoleTypeId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsWebConfigToRoleCA> aggsLambda) {
        setRoleTypeId_IpRange("roleTypeId", opLambda, aggsLambda);
    }

    public void setRoleTypeId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToRoleCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "roleTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToRoleCA ca = new WebConfigToRoleCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRoleTypeId_Count() {
        setRoleTypeId_Count(null);
    }

    public void setRoleTypeId_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setRoleTypeId_Count("roleTypeId", opLambda);
    }

    public void setRoleTypeId_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "roleTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_Cardinality() {
        setRoleTypeId_Cardinality(null);
    }

    public void setRoleTypeId_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setRoleTypeId_Cardinality("roleTypeId", opLambda);
    }

    public void setRoleTypeId_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "roleTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoleTypeId_Missing() {
        setRoleTypeId_Missing(null);
    }

    public void setRoleTypeId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setRoleTypeId_Missing("roleTypeId", opLambda, null);
    }

    public void setRoleTypeId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsWebConfigToRoleCA> aggsLambda) {
        setRoleTypeId_Missing("roleTypeId", opLambda, aggsLambda);
    }

    public void setRoleTypeId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToRoleCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "roleTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToRoleCA ca = new WebConfigToRoleCA();
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

    public void setWebConfigId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsWebConfigToRoleCA> aggsLambda) {
        setWebConfigId_Terms("webConfigId", opLambda, aggsLambda);
    }

    public void setWebConfigId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToRoleCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "webConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToRoleCA ca = new WebConfigToRoleCA();
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
            OperatorCall<BsWebConfigToRoleCA> aggsLambda) {
        setWebConfigId_SignificantTerms("webConfigId", opLambda, aggsLambda);
    }

    public void setWebConfigId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToRoleCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "webConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToRoleCA ca = new WebConfigToRoleCA();
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

    public void setWebConfigId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsWebConfigToRoleCA> aggsLambda) {
        setWebConfigId_IpRange("webConfigId", opLambda, aggsLambda);
    }

    public void setWebConfigId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToRoleCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "webConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToRoleCA ca = new WebConfigToRoleCA();
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

    public void setWebConfigId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsWebConfigToRoleCA> aggsLambda) {
        setWebConfigId_Missing("webConfigId", opLambda, aggsLambda);
    }

    public void setWebConfigId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsWebConfigToRoleCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "webConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            WebConfigToRoleCA ca = new WebConfigToRoleCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
