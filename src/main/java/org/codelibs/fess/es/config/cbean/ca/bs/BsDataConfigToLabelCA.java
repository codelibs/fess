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
import org.codelibs.fess.es.config.cbean.ca.DataConfigToLabelCA;
import org.codelibs.fess.es.config.cbean.cq.DataConfigToLabelCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsDataConfigToLabelCQ;
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
public abstract class BsDataConfigToLabelCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsDataConfigToLabelCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsDataConfigToLabelCA> aggsLambda) {
        DataConfigToLabelCQ cq = new DataConfigToLabelCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigToLabelCA ca = new DataConfigToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsDataConfigToLabelCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigToLabelCA ca = new DataConfigToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsDataConfigToLabelCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigToLabelCA ca = new DataConfigToLabelCA();
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

    public void setDataConfigId_Terms() {
        setDataConfigId_Terms(null);
    }

    public void setDataConfigId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setDataConfigId_Terms("dataConfigId", opLambda, null);
    }

    public void setDataConfigId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsDataConfigToLabelCA> aggsLambda) {
        setDataConfigId_Terms("dataConfigId", opLambda, aggsLambda);
    }

    public void setDataConfigId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigToLabelCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "dataConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigToLabelCA ca = new DataConfigToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDataConfigId_SignificantTerms() {
        setDataConfigId_SignificantTerms(null);
    }

    public void setDataConfigId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setDataConfigId_SignificantTerms("dataConfigId", opLambda, null);
    }

    public void setDataConfigId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigToLabelCA> aggsLambda) {
        setDataConfigId_SignificantTerms("dataConfigId", opLambda, aggsLambda);
    }

    public void setDataConfigId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigToLabelCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "dataConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigToLabelCA ca = new DataConfigToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDataConfigId_IpRange() {
        setDataConfigId_IpRange(null);
    }

    public void setDataConfigId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setDataConfigId_IpRange("dataConfigId", opLambda, null);
    }

    public void setDataConfigId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigToLabelCA> aggsLambda) {
        setDataConfigId_IpRange("dataConfigId", opLambda, aggsLambda);
    }

    public void setDataConfigId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigToLabelCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "dataConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigToLabelCA ca = new DataConfigToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDataConfigId_Count() {
        setDataConfigId_Count(null);
    }

    public void setDataConfigId_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setDataConfigId_Count("dataConfigId", opLambda);
    }

    public void setDataConfigId_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "dataConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_Cardinality() {
        setDataConfigId_Cardinality(null);
    }

    public void setDataConfigId_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setDataConfigId_Cardinality("dataConfigId", opLambda);
    }

    public void setDataConfigId_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "dataConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDataConfigId_Missing() {
        setDataConfigId_Missing(null);
    }

    public void setDataConfigId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setDataConfigId_Missing("dataConfigId", opLambda, null);
    }

    public void setDataConfigId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigToLabelCA> aggsLambda) {
        setDataConfigId_Missing("dataConfigId", opLambda, aggsLambda);
    }

    public void setDataConfigId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigToLabelCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "dataConfigId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigToLabelCA ca = new DataConfigToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLabelTypeId_Terms() {
        setLabelTypeId_Terms(null);
    }

    public void setLabelTypeId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setLabelTypeId_Terms("labelTypeId", opLambda, null);
    }

    public void setLabelTypeId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsDataConfigToLabelCA> aggsLambda) {
        setLabelTypeId_Terms("labelTypeId", opLambda, aggsLambda);
    }

    public void setLabelTypeId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigToLabelCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigToLabelCA ca = new DataConfigToLabelCA();
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
            OperatorCall<BsDataConfigToLabelCA> aggsLambda) {
        setLabelTypeId_SignificantTerms("labelTypeId", opLambda, aggsLambda);
    }

    public void setLabelTypeId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigToLabelCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigToLabelCA ca = new DataConfigToLabelCA();
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
            OperatorCall<BsDataConfigToLabelCA> aggsLambda) {
        setLabelTypeId_IpRange("labelTypeId", opLambda, aggsLambda);
    }

    public void setLabelTypeId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigToLabelCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigToLabelCA ca = new DataConfigToLabelCA();
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
            OperatorCall<BsDataConfigToLabelCA> aggsLambda) {
        setLabelTypeId_Missing("labelTypeId", opLambda, aggsLambda);
    }

    public void setLabelTypeId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsDataConfigToLabelCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            DataConfigToLabelCA ca = new DataConfigToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
