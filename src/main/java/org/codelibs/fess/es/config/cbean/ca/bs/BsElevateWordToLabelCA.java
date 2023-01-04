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
package org.codelibs.fess.es.config.cbean.ca.bs;

import org.codelibs.fess.es.config.allcommon.EsAbstractConditionAggregation;
import org.codelibs.fess.es.config.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.config.cbean.ca.ElevateWordToLabelCA;
import org.codelibs.fess.es.config.cbean.cq.ElevateWordToLabelCQ;
import org.codelibs.fess.es.config.cbean.cq.bs.BsElevateWordToLabelCQ;
import org.opensearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.opensearch.search.aggregations.bucket.global.GlobalAggregationBuilder;
import org.opensearch.search.aggregations.bucket.missing.MissingAggregationBuilder;
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
public abstract class BsElevateWordToLabelCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsElevateWordToLabelCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsElevateWordToLabelCA> aggsLambda) {
        ElevateWordToLabelCQ cq = new ElevateWordToLabelCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordToLabelCA ca = new ElevateWordToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordToLabelCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordToLabelCA ca = new ElevateWordToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordToLabelCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordToLabelCA ca = new ElevateWordToLabelCA();
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

    public void setElevateWordId_Terms() {
        setElevateWordId_Terms(null);
    }

    public void setElevateWordId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setElevateWordId_Terms("elevateWordId", opLambda, null);
    }

    public void setElevateWordId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordToLabelCA> aggsLambda) {
        setElevateWordId_Terms("elevateWordId", opLambda, aggsLambda);
    }

    public void setElevateWordId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordToLabelCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "elevateWordId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordToLabelCA ca = new ElevateWordToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setElevateWordId_SignificantTerms() {
        setElevateWordId_SignificantTerms(null);
    }

    public void setElevateWordId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setElevateWordId_SignificantTerms("elevateWordId", opLambda, null);
    }

    public void setElevateWordId_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordToLabelCA> aggsLambda) {
        setElevateWordId_SignificantTerms("elevateWordId", opLambda, aggsLambda);
    }

    public void setElevateWordId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordToLabelCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "elevateWordId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordToLabelCA ca = new ElevateWordToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setElevateWordId_IpRange() {
        setElevateWordId_IpRange(null);
    }

    public void setElevateWordId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setElevateWordId_IpRange("elevateWordId", opLambda, null);
    }

    public void setElevateWordId_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordToLabelCA> aggsLambda) {
        setElevateWordId_IpRange("elevateWordId", opLambda, aggsLambda);
    }

    public void setElevateWordId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordToLabelCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "elevateWordId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordToLabelCA ca = new ElevateWordToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setElevateWordId_Count() {
        setElevateWordId_Count(null);
    }

    public void setElevateWordId_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setElevateWordId_Count("elevateWordId", opLambda);
    }

    public void setElevateWordId_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "elevateWordId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setElevateWordId_Cardinality() {
        setElevateWordId_Cardinality(null);
    }

    public void setElevateWordId_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setElevateWordId_Cardinality("elevateWordId", opLambda);
    }

    public void setElevateWordId_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "elevateWordId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setElevateWordId_Missing() {
        setElevateWordId_Missing(null);
    }

    public void setElevateWordId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setElevateWordId_Missing("elevateWordId", opLambda, null);
    }

    public void setElevateWordId_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordToLabelCA> aggsLambda) {
        setElevateWordId_Missing("elevateWordId", opLambda, aggsLambda);
    }

    public void setElevateWordId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordToLabelCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "elevateWordId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordToLabelCA ca = new ElevateWordToLabelCA();
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

    public void setLabelTypeId_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordToLabelCA> aggsLambda) {
        setLabelTypeId_Terms("labelTypeId", opLambda, aggsLambda);
    }

    public void setLabelTypeId_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordToLabelCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordToLabelCA ca = new ElevateWordToLabelCA();
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
            OperatorCall<BsElevateWordToLabelCA> aggsLambda) {
        setLabelTypeId_SignificantTerms("labelTypeId", opLambda, aggsLambda);
    }

    public void setLabelTypeId_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordToLabelCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordToLabelCA ca = new ElevateWordToLabelCA();
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
            OperatorCall<BsElevateWordToLabelCA> aggsLambda) {
        setLabelTypeId_IpRange("labelTypeId", opLambda, aggsLambda);
    }

    public void setLabelTypeId_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordToLabelCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordToLabelCA ca = new ElevateWordToLabelCA();
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
            OperatorCall<BsElevateWordToLabelCA> aggsLambda) {
        setLabelTypeId_Missing("labelTypeId", opLambda, aggsLambda);
    }

    public void setLabelTypeId_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsElevateWordToLabelCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "labelTypeId");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            ElevateWordToLabelCA ca = new ElevateWordToLabelCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
