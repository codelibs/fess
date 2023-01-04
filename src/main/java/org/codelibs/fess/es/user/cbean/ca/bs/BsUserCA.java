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
package org.codelibs.fess.es.user.cbean.ca.bs;

import org.codelibs.fess.es.user.allcommon.EsAbstractConditionAggregation;
import org.codelibs.fess.es.user.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.user.cbean.ca.UserCA;
import org.codelibs.fess.es.user.cbean.cq.UserCQ;
import org.codelibs.fess.es.user.cbean.cq.bs.BsUserCQ;
import org.opensearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.opensearch.search.aggregations.bucket.global.GlobalAggregationBuilder;
import org.opensearch.search.aggregations.bucket.histogram.HistogramAggregationBuilder;
import org.opensearch.search.aggregations.bucket.missing.MissingAggregationBuilder;
import org.opensearch.search.aggregations.bucket.range.IpRangeAggregationBuilder;
import org.opensearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.opensearch.search.aggregations.bucket.sampler.SamplerAggregationBuilder;
import org.opensearch.search.aggregations.bucket.terms.SignificantTermsAggregationBuilder;
import org.opensearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.opensearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.opensearch.search.aggregations.metrics.CardinalityAggregationBuilder;
import org.opensearch.search.aggregations.metrics.ExtendedStatsAggregationBuilder;
import org.opensearch.search.aggregations.metrics.MaxAggregationBuilder;
import org.opensearch.search.aggregations.metrics.MinAggregationBuilder;
import org.opensearch.search.aggregations.metrics.PercentileRanksAggregationBuilder;
import org.opensearch.search.aggregations.metrics.PercentilesAggregationBuilder;
import org.opensearch.search.aggregations.metrics.ScriptedMetricAggregationBuilder;
import org.opensearch.search.aggregations.metrics.StatsAggregationBuilder;
import org.opensearch.search.aggregations.metrics.SumAggregationBuilder;
import org.opensearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.opensearch.search.aggregations.metrics.ValueCountAggregationBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsUserCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsUserCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        UserCQ cq = new UserCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        GlobalAggregationBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
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

    public void setBusinessCategory_Terms() {
        setBusinessCategory_Terms(null);
    }

    public void setBusinessCategory_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setBusinessCategory_Terms("businessCategory", opLambda, null);
    }

    public void setBusinessCategory_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setBusinessCategory_Terms("businessCategory", opLambda, aggsLambda);
    }

    public void setBusinessCategory_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "businessCategory");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setBusinessCategory_SignificantTerms() {
        setBusinessCategory_SignificantTerms(null);
    }

    public void setBusinessCategory_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setBusinessCategory_SignificantTerms("businessCategory", opLambda, null);
    }

    public void setBusinessCategory_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setBusinessCategory_SignificantTerms("businessCategory", opLambda, aggsLambda);
    }

    public void setBusinessCategory_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "businessCategory");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setBusinessCategory_IpRange() {
        setBusinessCategory_IpRange(null);
    }

    public void setBusinessCategory_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setBusinessCategory_IpRange("businessCategory", opLambda, null);
    }

    public void setBusinessCategory_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setBusinessCategory_IpRange("businessCategory", opLambda, aggsLambda);
    }

    public void setBusinessCategory_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "businessCategory");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setBusinessCategory_Count() {
        setBusinessCategory_Count(null);
    }

    public void setBusinessCategory_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setBusinessCategory_Count("businessCategory", opLambda);
    }

    public void setBusinessCategory_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "businessCategory");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBusinessCategory_Cardinality() {
        setBusinessCategory_Cardinality(null);
    }

    public void setBusinessCategory_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setBusinessCategory_Cardinality("businessCategory", opLambda);
    }

    public void setBusinessCategory_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "businessCategory");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBusinessCategory_Missing() {
        setBusinessCategory_Missing(null);
    }

    public void setBusinessCategory_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setBusinessCategory_Missing("businessCategory", opLambda, null);
    }

    public void setBusinessCategory_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setBusinessCategory_Missing("businessCategory", opLambda, aggsLambda);
    }

    public void setBusinessCategory_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "businessCategory");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCarLicense_Terms() {
        setCarLicense_Terms(null);
    }

    public void setCarLicense_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setCarLicense_Terms("carLicense", opLambda, null);
    }

    public void setCarLicense_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setCarLicense_Terms("carLicense", opLambda, aggsLambda);
    }

    public void setCarLicense_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "carLicense");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCarLicense_SignificantTerms() {
        setCarLicense_SignificantTerms(null);
    }

    public void setCarLicense_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setCarLicense_SignificantTerms("carLicense", opLambda, null);
    }

    public void setCarLicense_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setCarLicense_SignificantTerms("carLicense", opLambda, aggsLambda);
    }

    public void setCarLicense_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "carLicense");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCarLicense_IpRange() {
        setCarLicense_IpRange(null);
    }

    public void setCarLicense_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setCarLicense_IpRange("carLicense", opLambda, null);
    }

    public void setCarLicense_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setCarLicense_IpRange("carLicense", opLambda, aggsLambda);
    }

    public void setCarLicense_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "carLicense");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCarLicense_Count() {
        setCarLicense_Count(null);
    }

    public void setCarLicense_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setCarLicense_Count("carLicense", opLambda);
    }

    public void setCarLicense_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "carLicense");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCarLicense_Cardinality() {
        setCarLicense_Cardinality(null);
    }

    public void setCarLicense_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setCarLicense_Cardinality("carLicense", opLambda);
    }

    public void setCarLicense_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "carLicense");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCarLicense_Missing() {
        setCarLicense_Missing(null);
    }

    public void setCarLicense_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setCarLicense_Missing("carLicense", opLambda, null);
    }

    public void setCarLicense_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setCarLicense_Missing("carLicense", opLambda, aggsLambda);
    }

    public void setCarLicense_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "carLicense");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCity_Terms() {
        setCity_Terms(null);
    }

    public void setCity_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setCity_Terms("city", opLambda, null);
    }

    public void setCity_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setCity_Terms("city", opLambda, aggsLambda);
    }

    public void setCity_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "city");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCity_SignificantTerms() {
        setCity_SignificantTerms(null);
    }

    public void setCity_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setCity_SignificantTerms("city", opLambda, null);
    }

    public void setCity_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setCity_SignificantTerms("city", opLambda, aggsLambda);
    }

    public void setCity_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "city");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCity_IpRange() {
        setCity_IpRange(null);
    }

    public void setCity_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setCity_IpRange("city", opLambda, null);
    }

    public void setCity_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setCity_IpRange("city", opLambda, aggsLambda);
    }

    public void setCity_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "city");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCity_Count() {
        setCity_Count(null);
    }

    public void setCity_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setCity_Count("city", opLambda);
    }

    public void setCity_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "city");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCity_Cardinality() {
        setCity_Cardinality(null);
    }

    public void setCity_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setCity_Cardinality("city", opLambda);
    }

    public void setCity_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "city");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCity_Missing() {
        setCity_Missing(null);
    }

    public void setCity_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setCity_Missing("city", opLambda, null);
    }

    public void setCity_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setCity_Missing("city", opLambda, aggsLambda);
    }

    public void setCity_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "city");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDepartmentNumber_Terms() {
        setDepartmentNumber_Terms(null);
    }

    public void setDepartmentNumber_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setDepartmentNumber_Terms("departmentNumber", opLambda, null);
    }

    public void setDepartmentNumber_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDepartmentNumber_Terms("departmentNumber", opLambda, aggsLambda);
    }

    public void setDepartmentNumber_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "departmentNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDepartmentNumber_SignificantTerms() {
        setDepartmentNumber_SignificantTerms(null);
    }

    public void setDepartmentNumber_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setDepartmentNumber_SignificantTerms("departmentNumber", opLambda, null);
    }

    public void setDepartmentNumber_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setDepartmentNumber_SignificantTerms("departmentNumber", opLambda, aggsLambda);
    }

    public void setDepartmentNumber_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "departmentNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDepartmentNumber_IpRange() {
        setDepartmentNumber_IpRange(null);
    }

    public void setDepartmentNumber_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setDepartmentNumber_IpRange("departmentNumber", opLambda, null);
    }

    public void setDepartmentNumber_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDepartmentNumber_IpRange("departmentNumber", opLambda, aggsLambda);
    }

    public void setDepartmentNumber_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "departmentNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDepartmentNumber_Count() {
        setDepartmentNumber_Count(null);
    }

    public void setDepartmentNumber_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setDepartmentNumber_Count("departmentNumber", opLambda);
    }

    public void setDepartmentNumber_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "departmentNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepartmentNumber_Cardinality() {
        setDepartmentNumber_Cardinality(null);
    }

    public void setDepartmentNumber_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setDepartmentNumber_Cardinality("departmentNumber", opLambda);
    }

    public void setDepartmentNumber_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "departmentNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepartmentNumber_Missing() {
        setDepartmentNumber_Missing(null);
    }

    public void setDepartmentNumber_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setDepartmentNumber_Missing("departmentNumber", opLambda, null);
    }

    public void setDepartmentNumber_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDepartmentNumber_Missing("departmentNumber", opLambda, aggsLambda);
    }

    public void setDepartmentNumber_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "departmentNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDescription_Terms() {
        setDescription_Terms(null);
    }

    public void setDescription_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setDescription_Terms("description", opLambda, null);
    }

    public void setDescription_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDescription_Terms("description", opLambda, aggsLambda);
    }

    public void setDescription_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "description");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDescription_SignificantTerms() {
        setDescription_SignificantTerms(null);
    }

    public void setDescription_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setDescription_SignificantTerms("description", opLambda, null);
    }

    public void setDescription_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setDescription_SignificantTerms("description", opLambda, aggsLambda);
    }

    public void setDescription_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "description");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDescription_IpRange() {
        setDescription_IpRange(null);
    }

    public void setDescription_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setDescription_IpRange("description", opLambda, null);
    }

    public void setDescription_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDescription_IpRange("description", opLambda, aggsLambda);
    }

    public void setDescription_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "description");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDescription_Count() {
        setDescription_Count(null);
    }

    public void setDescription_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setDescription_Count("description", opLambda);
    }

    public void setDescription_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "description");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_Cardinality() {
        setDescription_Cardinality(null);
    }

    public void setDescription_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setDescription_Cardinality("description", opLambda);
    }

    public void setDescription_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "description");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_Missing() {
        setDescription_Missing(null);
    }

    public void setDescription_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setDescription_Missing("description", opLambda, null);
    }

    public void setDescription_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDescription_Missing("description", opLambda, aggsLambda);
    }

    public void setDescription_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "description");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDestinationIndicator_Terms() {
        setDestinationIndicator_Terms(null);
    }

    public void setDestinationIndicator_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setDestinationIndicator_Terms("destinationIndicator", opLambda, null);
    }

    public void setDestinationIndicator_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDestinationIndicator_Terms("destinationIndicator", opLambda, aggsLambda);
    }

    public void setDestinationIndicator_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "destinationIndicator");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDestinationIndicator_SignificantTerms() {
        setDestinationIndicator_SignificantTerms(null);
    }

    public void setDestinationIndicator_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setDestinationIndicator_SignificantTerms("destinationIndicator", opLambda, null);
    }

    public void setDestinationIndicator_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setDestinationIndicator_SignificantTerms("destinationIndicator", opLambda, aggsLambda);
    }

    public void setDestinationIndicator_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "destinationIndicator");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDestinationIndicator_IpRange() {
        setDestinationIndicator_IpRange(null);
    }

    public void setDestinationIndicator_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setDestinationIndicator_IpRange("destinationIndicator", opLambda, null);
    }

    public void setDestinationIndicator_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setDestinationIndicator_IpRange("destinationIndicator", opLambda, aggsLambda);
    }

    public void setDestinationIndicator_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "destinationIndicator");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDestinationIndicator_Count() {
        setDestinationIndicator_Count(null);
    }

    public void setDestinationIndicator_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setDestinationIndicator_Count("destinationIndicator", opLambda);
    }

    public void setDestinationIndicator_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "destinationIndicator");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDestinationIndicator_Cardinality() {
        setDestinationIndicator_Cardinality(null);
    }

    public void setDestinationIndicator_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setDestinationIndicator_Cardinality("destinationIndicator", opLambda);
    }

    public void setDestinationIndicator_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "destinationIndicator");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDestinationIndicator_Missing() {
        setDestinationIndicator_Missing(null);
    }

    public void setDestinationIndicator_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setDestinationIndicator_Missing("destinationIndicator", opLambda, null);
    }

    public void setDestinationIndicator_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setDestinationIndicator_Missing("destinationIndicator", opLambda, aggsLambda);
    }

    public void setDestinationIndicator_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "destinationIndicator");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDisplayName_Terms() {
        setDisplayName_Terms(null);
    }

    public void setDisplayName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setDisplayName_Terms("displayName", opLambda, null);
    }

    public void setDisplayName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDisplayName_Terms("displayName", opLambda, aggsLambda);
    }

    public void setDisplayName_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "displayName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDisplayName_SignificantTerms() {
        setDisplayName_SignificantTerms(null);
    }

    public void setDisplayName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setDisplayName_SignificantTerms("displayName", opLambda, null);
    }

    public void setDisplayName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setDisplayName_SignificantTerms("displayName", opLambda, aggsLambda);
    }

    public void setDisplayName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "displayName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDisplayName_IpRange() {
        setDisplayName_IpRange(null);
    }

    public void setDisplayName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setDisplayName_IpRange("displayName", opLambda, null);
    }

    public void setDisplayName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDisplayName_IpRange("displayName", opLambda, aggsLambda);
    }

    public void setDisplayName_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "displayName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setDisplayName_Count() {
        setDisplayName_Count(null);
    }

    public void setDisplayName_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setDisplayName_Count("displayName", opLambda);
    }

    public void setDisplayName_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "displayName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDisplayName_Cardinality() {
        setDisplayName_Cardinality(null);
    }

    public void setDisplayName_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setDisplayName_Cardinality("displayName", opLambda);
    }

    public void setDisplayName_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "displayName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDisplayName_Missing() {
        setDisplayName_Missing(null);
    }

    public void setDisplayName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setDisplayName_Missing("displayName", opLambda, null);
    }

    public void setDisplayName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDisplayName_Missing("displayName", opLambda, aggsLambda);
    }

    public void setDisplayName_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "displayName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setEmployeeNumber_Terms() {
        setEmployeeNumber_Terms(null);
    }

    public void setEmployeeNumber_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setEmployeeNumber_Terms("employeeNumber", opLambda, null);
    }

    public void setEmployeeNumber_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setEmployeeNumber_Terms("employeeNumber", opLambda, aggsLambda);
    }

    public void setEmployeeNumber_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "employeeNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setEmployeeNumber_SignificantTerms() {
        setEmployeeNumber_SignificantTerms(null);
    }

    public void setEmployeeNumber_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setEmployeeNumber_SignificantTerms("employeeNumber", opLambda, null);
    }

    public void setEmployeeNumber_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setEmployeeNumber_SignificantTerms("employeeNumber", opLambda, aggsLambda);
    }

    public void setEmployeeNumber_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "employeeNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setEmployeeNumber_IpRange() {
        setEmployeeNumber_IpRange(null);
    }

    public void setEmployeeNumber_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setEmployeeNumber_IpRange("employeeNumber", opLambda, null);
    }

    public void setEmployeeNumber_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setEmployeeNumber_IpRange("employeeNumber", opLambda, aggsLambda);
    }

    public void setEmployeeNumber_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "employeeNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setEmployeeNumber_Count() {
        setEmployeeNumber_Count(null);
    }

    public void setEmployeeNumber_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setEmployeeNumber_Count("employeeNumber", opLambda);
    }

    public void setEmployeeNumber_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "employeeNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeNumber_Cardinality() {
        setEmployeeNumber_Cardinality(null);
    }

    public void setEmployeeNumber_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setEmployeeNumber_Cardinality("employeeNumber", opLambda);
    }

    public void setEmployeeNumber_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "employeeNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeNumber_Missing() {
        setEmployeeNumber_Missing(null);
    }

    public void setEmployeeNumber_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setEmployeeNumber_Missing("employeeNumber", opLambda, null);
    }

    public void setEmployeeNumber_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setEmployeeNumber_Missing("employeeNumber", opLambda, aggsLambda);
    }

    public void setEmployeeNumber_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "employeeNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setEmployeeType_Terms() {
        setEmployeeType_Terms(null);
    }

    public void setEmployeeType_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setEmployeeType_Terms("employeeType", opLambda, null);
    }

    public void setEmployeeType_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setEmployeeType_Terms("employeeType", opLambda, aggsLambda);
    }

    public void setEmployeeType_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "employeeType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setEmployeeType_SignificantTerms() {
        setEmployeeType_SignificantTerms(null);
    }

    public void setEmployeeType_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setEmployeeType_SignificantTerms("employeeType", opLambda, null);
    }

    public void setEmployeeType_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setEmployeeType_SignificantTerms("employeeType", opLambda, aggsLambda);
    }

    public void setEmployeeType_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "employeeType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setEmployeeType_IpRange() {
        setEmployeeType_IpRange(null);
    }

    public void setEmployeeType_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setEmployeeType_IpRange("employeeType", opLambda, null);
    }

    public void setEmployeeType_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setEmployeeType_IpRange("employeeType", opLambda, aggsLambda);
    }

    public void setEmployeeType_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "employeeType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setEmployeeType_Count() {
        setEmployeeType_Count(null);
    }

    public void setEmployeeType_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setEmployeeType_Count("employeeType", opLambda);
    }

    public void setEmployeeType_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "employeeType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeType_Cardinality() {
        setEmployeeType_Cardinality(null);
    }

    public void setEmployeeType_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setEmployeeType_Cardinality("employeeType", opLambda);
    }

    public void setEmployeeType_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "employeeType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeType_Missing() {
        setEmployeeType_Missing(null);
    }

    public void setEmployeeType_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setEmployeeType_Missing("employeeType", opLambda, null);
    }

    public void setEmployeeType_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setEmployeeType_Missing("employeeType", opLambda, aggsLambda);
    }

    public void setEmployeeType_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "employeeType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setFacsimileTelephoneNumber_Terms() {
        setFacsimileTelephoneNumber_Terms(null);
    }

    public void setFacsimileTelephoneNumber_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setFacsimileTelephoneNumber_Terms("facsimileTelephoneNumber", opLambda, null);
    }

    public void setFacsimileTelephoneNumber_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setFacsimileTelephoneNumber_Terms("facsimileTelephoneNumber", opLambda, aggsLambda);
    }

    public void setFacsimileTelephoneNumber_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "facsimileTelephoneNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setFacsimileTelephoneNumber_SignificantTerms() {
        setFacsimileTelephoneNumber_SignificantTerms(null);
    }

    public void setFacsimileTelephoneNumber_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setFacsimileTelephoneNumber_SignificantTerms("facsimileTelephoneNumber", opLambda, null);
    }

    public void setFacsimileTelephoneNumber_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setFacsimileTelephoneNumber_SignificantTerms("facsimileTelephoneNumber", opLambda, aggsLambda);
    }

    public void setFacsimileTelephoneNumber_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "facsimileTelephoneNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setFacsimileTelephoneNumber_IpRange() {
        setFacsimileTelephoneNumber_IpRange(null);
    }

    public void setFacsimileTelephoneNumber_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setFacsimileTelephoneNumber_IpRange("facsimileTelephoneNumber", opLambda, null);
    }

    public void setFacsimileTelephoneNumber_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setFacsimileTelephoneNumber_IpRange("facsimileTelephoneNumber", opLambda, aggsLambda);
    }

    public void setFacsimileTelephoneNumber_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "facsimileTelephoneNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setFacsimileTelephoneNumber_Count() {
        setFacsimileTelephoneNumber_Count(null);
    }

    public void setFacsimileTelephoneNumber_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setFacsimileTelephoneNumber_Count("facsimileTelephoneNumber", opLambda);
    }

    public void setFacsimileTelephoneNumber_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "facsimileTelephoneNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFacsimileTelephoneNumber_Cardinality() {
        setFacsimileTelephoneNumber_Cardinality(null);
    }

    public void setFacsimileTelephoneNumber_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setFacsimileTelephoneNumber_Cardinality("facsimileTelephoneNumber", opLambda);
    }

    public void setFacsimileTelephoneNumber_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "facsimileTelephoneNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFacsimileTelephoneNumber_Missing() {
        setFacsimileTelephoneNumber_Missing(null);
    }

    public void setFacsimileTelephoneNumber_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setFacsimileTelephoneNumber_Missing("facsimileTelephoneNumber", opLambda, null);
    }

    public void setFacsimileTelephoneNumber_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setFacsimileTelephoneNumber_Missing("facsimileTelephoneNumber", opLambda, aggsLambda);
    }

    public void setFacsimileTelephoneNumber_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "facsimileTelephoneNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setGidNumber_Avg() {
        setGidNumber_Avg(null);
    }

    public void setGidNumber_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setGidNumber_Avg("gidNumber", opLambda);
    }

    public void setGidNumber_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Max() {
        setGidNumber_Max(null);
    }

    public void setGidNumber_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setGidNumber_Max("gidNumber", opLambda);
    }

    public void setGidNumber_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Min() {
        setGidNumber_Min(null);
    }

    public void setGidNumber_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setGidNumber_Min("gidNumber", opLambda);
    }

    public void setGidNumber_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Sum() {
        setGidNumber_Sum(null);
    }

    public void setGidNumber_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setGidNumber_Sum("gidNumber", opLambda);
    }

    public void setGidNumber_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_ExtendedStats() {
        setGidNumber_ExtendedStats(null);
    }

    public void setGidNumber_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setGidNumber_ExtendedStats("gidNumber", opLambda);
    }

    public void setGidNumber_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Stats() {
        setGidNumber_Stats(null);
    }

    public void setGidNumber_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setGidNumber_Stats("gidNumber", opLambda);
    }

    public void setGidNumber_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Percentiles() {
        setGidNumber_Percentiles(null);
    }

    public void setGidNumber_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setGidNumber_Percentiles("gidNumber", opLambda);
    }

    public void setGidNumber_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_PercentileRanks(double[] values) {
        setGidNumber_PercentileRanks(values, null);
    }

    public void setGidNumber_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setGidNumber_PercentileRanks("gidNumber", values, opLambda);
    }

    public void setGidNumber_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "gidNumber", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Histogram() {
        setGidNumber_Histogram(null);
    }

    public void setGidNumber_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setGidNumber_Histogram("gidNumber", opLambda, null);
    }

    public void setGidNumber_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGidNumber_Histogram("gidNumber", opLambda, aggsLambda);
    }

    public void setGidNumber_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setGidNumber_Range() {
        setGidNumber_Range(null);
    }

    public void setGidNumber_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setGidNumber_Range("gidNumber", opLambda, null);
    }

    public void setGidNumber_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGidNumber_Range("gidNumber", opLambda, aggsLambda);
    }

    public void setGidNumber_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setGidNumber_Count() {
        setGidNumber_Count(null);
    }

    public void setGidNumber_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setGidNumber_Count("gidNumber", opLambda);
    }

    public void setGidNumber_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Cardinality() {
        setGidNumber_Cardinality(null);
    }

    public void setGidNumber_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setGidNumber_Cardinality("gidNumber", opLambda);
    }

    public void setGidNumber_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Missing() {
        setGidNumber_Missing(null);
    }

    public void setGidNumber_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setGidNumber_Missing("gidNumber", opLambda, null);
    }

    public void setGidNumber_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGidNumber_Missing("gidNumber", opLambda, aggsLambda);
    }

    public void setGidNumber_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setGivenName_Terms() {
        setGivenName_Terms(null);
    }

    public void setGivenName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setGivenName_Terms("givenName", opLambda, null);
    }

    public void setGivenName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGivenName_Terms("givenName", opLambda, aggsLambda);
    }

    public void setGivenName_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "givenName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setGivenName_SignificantTerms() {
        setGivenName_SignificantTerms(null);
    }

    public void setGivenName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setGivenName_SignificantTerms("givenName", opLambda, null);
    }

    public void setGivenName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setGivenName_SignificantTerms("givenName", opLambda, aggsLambda);
    }

    public void setGivenName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "givenName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setGivenName_IpRange() {
        setGivenName_IpRange(null);
    }

    public void setGivenName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setGivenName_IpRange("givenName", opLambda, null);
    }

    public void setGivenName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGivenName_IpRange("givenName", opLambda, aggsLambda);
    }

    public void setGivenName_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "givenName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setGivenName_Count() {
        setGivenName_Count(null);
    }

    public void setGivenName_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setGivenName_Count("givenName", opLambda);
    }

    public void setGivenName_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "givenName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGivenName_Cardinality() {
        setGivenName_Cardinality(null);
    }

    public void setGivenName_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setGivenName_Cardinality("givenName", opLambda);
    }

    public void setGivenName_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "givenName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGivenName_Missing() {
        setGivenName_Missing(null);
    }

    public void setGivenName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setGivenName_Missing("givenName", opLambda, null);
    }

    public void setGivenName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGivenName_Missing("givenName", opLambda, aggsLambda);
    }

    public void setGivenName_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "givenName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setGroups_Terms() {
        setGroups_Terms(null);
    }

    public void setGroups_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setGroups_Terms("groups", opLambda, null);
    }

    public void setGroups_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGroups_Terms("groups", opLambda, aggsLambda);
    }

    public void setGroups_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "groups");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setGroups_SignificantTerms() {
        setGroups_SignificantTerms(null);
    }

    public void setGroups_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setGroups_SignificantTerms("groups", opLambda, null);
    }

    public void setGroups_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setGroups_SignificantTerms("groups", opLambda, aggsLambda);
    }

    public void setGroups_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "groups");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setGroups_IpRange() {
        setGroups_IpRange(null);
    }

    public void setGroups_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setGroups_IpRange("groups", opLambda, null);
    }

    public void setGroups_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGroups_IpRange("groups", opLambda, aggsLambda);
    }

    public void setGroups_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "groups");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setGroups_Count() {
        setGroups_Count(null);
    }

    public void setGroups_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setGroups_Count("groups", opLambda);
    }

    public void setGroups_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "groups");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroups_Cardinality() {
        setGroups_Cardinality(null);
    }

    public void setGroups_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setGroups_Cardinality("groups", opLambda);
    }

    public void setGroups_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "groups");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroups_Missing() {
        setGroups_Missing(null);
    }

    public void setGroups_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setGroups_Missing("groups", opLambda, null);
    }

    public void setGroups_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGroups_Missing("groups", opLambda, aggsLambda);
    }

    public void setGroups_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "groups");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHomeDirectory_Terms() {
        setHomeDirectory_Terms(null);
    }

    public void setHomeDirectory_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setHomeDirectory_Terms("homeDirectory", opLambda, null);
    }

    public void setHomeDirectory_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomeDirectory_Terms("homeDirectory", opLambda, aggsLambda);
    }

    public void setHomeDirectory_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "homeDirectory");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHomeDirectory_SignificantTerms() {
        setHomeDirectory_SignificantTerms(null);
    }

    public void setHomeDirectory_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setHomeDirectory_SignificantTerms("homeDirectory", opLambda, null);
    }

    public void setHomeDirectory_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setHomeDirectory_SignificantTerms("homeDirectory", opLambda, aggsLambda);
    }

    public void setHomeDirectory_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "homeDirectory");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHomeDirectory_IpRange() {
        setHomeDirectory_IpRange(null);
    }

    public void setHomeDirectory_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setHomeDirectory_IpRange("homeDirectory", opLambda, null);
    }

    public void setHomeDirectory_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomeDirectory_IpRange("homeDirectory", opLambda, aggsLambda);
    }

    public void setHomeDirectory_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "homeDirectory");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHomeDirectory_Count() {
        setHomeDirectory_Count(null);
    }

    public void setHomeDirectory_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setHomeDirectory_Count("homeDirectory", opLambda);
    }

    public void setHomeDirectory_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "homeDirectory");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomeDirectory_Cardinality() {
        setHomeDirectory_Cardinality(null);
    }

    public void setHomeDirectory_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setHomeDirectory_Cardinality("homeDirectory", opLambda);
    }

    public void setHomeDirectory_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "homeDirectory");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomeDirectory_Missing() {
        setHomeDirectory_Missing(null);
    }

    public void setHomeDirectory_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setHomeDirectory_Missing("homeDirectory", opLambda, null);
    }

    public void setHomeDirectory_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomeDirectory_Missing("homeDirectory", opLambda, aggsLambda);
    }

    public void setHomeDirectory_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "homeDirectory");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHomePhone_Terms() {
        setHomePhone_Terms(null);
    }

    public void setHomePhone_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setHomePhone_Terms("homePhone", opLambda, null);
    }

    public void setHomePhone_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomePhone_Terms("homePhone", opLambda, aggsLambda);
    }

    public void setHomePhone_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "homePhone");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHomePhone_SignificantTerms() {
        setHomePhone_SignificantTerms(null);
    }

    public void setHomePhone_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setHomePhone_SignificantTerms("homePhone", opLambda, null);
    }

    public void setHomePhone_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setHomePhone_SignificantTerms("homePhone", opLambda, aggsLambda);
    }

    public void setHomePhone_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "homePhone");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHomePhone_IpRange() {
        setHomePhone_IpRange(null);
    }

    public void setHomePhone_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setHomePhone_IpRange("homePhone", opLambda, null);
    }

    public void setHomePhone_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomePhone_IpRange("homePhone", opLambda, aggsLambda);
    }

    public void setHomePhone_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "homePhone");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHomePhone_Count() {
        setHomePhone_Count(null);
    }

    public void setHomePhone_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setHomePhone_Count("homePhone", opLambda);
    }

    public void setHomePhone_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "homePhone");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePhone_Cardinality() {
        setHomePhone_Cardinality(null);
    }

    public void setHomePhone_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setHomePhone_Cardinality("homePhone", opLambda);
    }

    public void setHomePhone_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "homePhone");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePhone_Missing() {
        setHomePhone_Missing(null);
    }

    public void setHomePhone_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setHomePhone_Missing("homePhone", opLambda, null);
    }

    public void setHomePhone_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomePhone_Missing("homePhone", opLambda, aggsLambda);
    }

    public void setHomePhone_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "homePhone");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHomePostalAddress_Terms() {
        setHomePostalAddress_Terms(null);
    }

    public void setHomePostalAddress_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setHomePostalAddress_Terms("homePostalAddress", opLambda, null);
    }

    public void setHomePostalAddress_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomePostalAddress_Terms("homePostalAddress", opLambda, aggsLambda);
    }

    public void setHomePostalAddress_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "homePostalAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHomePostalAddress_SignificantTerms() {
        setHomePostalAddress_SignificantTerms(null);
    }

    public void setHomePostalAddress_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setHomePostalAddress_SignificantTerms("homePostalAddress", opLambda, null);
    }

    public void setHomePostalAddress_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setHomePostalAddress_SignificantTerms("homePostalAddress", opLambda, aggsLambda);
    }

    public void setHomePostalAddress_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "homePostalAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHomePostalAddress_IpRange() {
        setHomePostalAddress_IpRange(null);
    }

    public void setHomePostalAddress_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setHomePostalAddress_IpRange("homePostalAddress", opLambda, null);
    }

    public void setHomePostalAddress_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomePostalAddress_IpRange("homePostalAddress", opLambda, aggsLambda);
    }

    public void setHomePostalAddress_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "homePostalAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setHomePostalAddress_Count() {
        setHomePostalAddress_Count(null);
    }

    public void setHomePostalAddress_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setHomePostalAddress_Count("homePostalAddress", opLambda);
    }

    public void setHomePostalAddress_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "homePostalAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePostalAddress_Cardinality() {
        setHomePostalAddress_Cardinality(null);
    }

    public void setHomePostalAddress_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setHomePostalAddress_Cardinality("homePostalAddress", opLambda);
    }

    public void setHomePostalAddress_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "homePostalAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePostalAddress_Missing() {
        setHomePostalAddress_Missing(null);
    }

    public void setHomePostalAddress_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setHomePostalAddress_Missing("homePostalAddress", opLambda, null);
    }

    public void setHomePostalAddress_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomePostalAddress_Missing("homePostalAddress", opLambda, aggsLambda);
    }

    public void setHomePostalAddress_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "homePostalAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setInitials_Terms() {
        setInitials_Terms(null);
    }

    public void setInitials_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setInitials_Terms("initials", opLambda, null);
    }

    public void setInitials_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setInitials_Terms("initials", opLambda, aggsLambda);
    }

    public void setInitials_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "initials");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setInitials_SignificantTerms() {
        setInitials_SignificantTerms(null);
    }

    public void setInitials_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setInitials_SignificantTerms("initials", opLambda, null);
    }

    public void setInitials_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setInitials_SignificantTerms("initials", opLambda, aggsLambda);
    }

    public void setInitials_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "initials");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setInitials_IpRange() {
        setInitials_IpRange(null);
    }

    public void setInitials_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setInitials_IpRange("initials", opLambda, null);
    }

    public void setInitials_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setInitials_IpRange("initials", opLambda, aggsLambda);
    }

    public void setInitials_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "initials");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setInitials_Count() {
        setInitials_Count(null);
    }

    public void setInitials_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setInitials_Count("initials", opLambda);
    }

    public void setInitials_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "initials");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInitials_Cardinality() {
        setInitials_Cardinality(null);
    }

    public void setInitials_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setInitials_Cardinality("initials", opLambda);
    }

    public void setInitials_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "initials");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInitials_Missing() {
        setInitials_Missing(null);
    }

    public void setInitials_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setInitials_Missing("initials", opLambda, null);
    }

    public void setInitials_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setInitials_Missing("initials", opLambda, aggsLambda);
    }

    public void setInitials_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "initials");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setInternationaliSDNNumber_Terms() {
        setInternationaliSDNNumber_Terms(null);
    }

    public void setInternationaliSDNNumber_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setInternationaliSDNNumber_Terms("internationaliSDNNumber", opLambda, null);
    }

    public void setInternationaliSDNNumber_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setInternationaliSDNNumber_Terms("internationaliSDNNumber", opLambda, aggsLambda);
    }

    public void setInternationaliSDNNumber_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "internationaliSDNNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setInternationaliSDNNumber_SignificantTerms() {
        setInternationaliSDNNumber_SignificantTerms(null);
    }

    public void setInternationaliSDNNumber_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setInternationaliSDNNumber_SignificantTerms("internationaliSDNNumber", opLambda, null);
    }

    public void setInternationaliSDNNumber_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setInternationaliSDNNumber_SignificantTerms("internationaliSDNNumber", opLambda, aggsLambda);
    }

    public void setInternationaliSDNNumber_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "internationaliSDNNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setInternationaliSDNNumber_IpRange() {
        setInternationaliSDNNumber_IpRange(null);
    }

    public void setInternationaliSDNNumber_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setInternationaliSDNNumber_IpRange("internationaliSDNNumber", opLambda, null);
    }

    public void setInternationaliSDNNumber_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setInternationaliSDNNumber_IpRange("internationaliSDNNumber", opLambda, aggsLambda);
    }

    public void setInternationaliSDNNumber_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "internationaliSDNNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setInternationaliSDNNumber_Count() {
        setInternationaliSDNNumber_Count(null);
    }

    public void setInternationaliSDNNumber_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setInternationaliSDNNumber_Count("internationaliSDNNumber", opLambda);
    }

    public void setInternationaliSDNNumber_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "internationaliSDNNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInternationaliSDNNumber_Cardinality() {
        setInternationaliSDNNumber_Cardinality(null);
    }

    public void setInternationaliSDNNumber_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setInternationaliSDNNumber_Cardinality("internationaliSDNNumber", opLambda);
    }

    public void setInternationaliSDNNumber_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "internationaliSDNNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInternationaliSDNNumber_Missing() {
        setInternationaliSDNNumber_Missing(null);
    }

    public void setInternationaliSDNNumber_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setInternationaliSDNNumber_Missing("internationaliSDNNumber", opLambda, null);
    }

    public void setInternationaliSDNNumber_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setInternationaliSDNNumber_Missing("internationaliSDNNumber", opLambda, aggsLambda);
    }

    public void setInternationaliSDNNumber_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "internationaliSDNNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLabeledURI_Terms() {
        setLabeledURI_Terms(null);
    }

    public void setLabeledURI_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setLabeledURI_Terms("labeledURI", opLambda, null);
    }

    public void setLabeledURI_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setLabeledURI_Terms("labeledURI", opLambda, aggsLambda);
    }

    public void setLabeledURI_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "labeledURI");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLabeledURI_SignificantTerms() {
        setLabeledURI_SignificantTerms(null);
    }

    public void setLabeledURI_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setLabeledURI_SignificantTerms("labeledURI", opLambda, null);
    }

    public void setLabeledURI_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setLabeledURI_SignificantTerms("labeledURI", opLambda, aggsLambda);
    }

    public void setLabeledURI_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "labeledURI");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLabeledURI_IpRange() {
        setLabeledURI_IpRange(null);
    }

    public void setLabeledURI_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setLabeledURI_IpRange("labeledURI", opLambda, null);
    }

    public void setLabeledURI_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setLabeledURI_IpRange("labeledURI", opLambda, aggsLambda);
    }

    public void setLabeledURI_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "labeledURI");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setLabeledURI_Count() {
        setLabeledURI_Count(null);
    }

    public void setLabeledURI_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setLabeledURI_Count("labeledURI", opLambda);
    }

    public void setLabeledURI_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "labeledURI");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabeledURI_Cardinality() {
        setLabeledURI_Cardinality(null);
    }

    public void setLabeledURI_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setLabeledURI_Cardinality("labeledURI", opLambda);
    }

    public void setLabeledURI_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "labeledURI");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabeledURI_Missing() {
        setLabeledURI_Missing(null);
    }

    public void setLabeledURI_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setLabeledURI_Missing("labeledURI", opLambda, null);
    }

    public void setLabeledURI_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setLabeledURI_Missing("labeledURI", opLambda, aggsLambda);
    }

    public void setLabeledURI_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "labeledURI");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setMail_Terms() {
        setMail_Terms(null);
    }

    public void setMail_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setMail_Terms("mail", opLambda, null);
    }

    public void setMail_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setMail_Terms("mail", opLambda, aggsLambda);
    }

    public void setMail_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "mail");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setMail_SignificantTerms() {
        setMail_SignificantTerms(null);
    }

    public void setMail_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setMail_SignificantTerms("mail", opLambda, null);
    }

    public void setMail_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setMail_SignificantTerms("mail", opLambda, aggsLambda);
    }

    public void setMail_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "mail");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setMail_IpRange() {
        setMail_IpRange(null);
    }

    public void setMail_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setMail_IpRange("mail", opLambda, null);
    }

    public void setMail_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setMail_IpRange("mail", opLambda, aggsLambda);
    }

    public void setMail_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "mail");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setMail_Count() {
        setMail_Count(null);
    }

    public void setMail_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setMail_Count("mail", opLambda);
    }

    public void setMail_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "mail");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMail_Cardinality() {
        setMail_Cardinality(null);
    }

    public void setMail_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setMail_Cardinality("mail", opLambda);
    }

    public void setMail_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "mail");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMail_Missing() {
        setMail_Missing(null);
    }

    public void setMail_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setMail_Missing("mail", opLambda, null);
    }

    public void setMail_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setMail_Missing("mail", opLambda, aggsLambda);
    }

    public void setMail_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "mail");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setMobile_Terms() {
        setMobile_Terms(null);
    }

    public void setMobile_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setMobile_Terms("mobile", opLambda, null);
    }

    public void setMobile_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setMobile_Terms("mobile", opLambda, aggsLambda);
    }

    public void setMobile_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "mobile");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setMobile_SignificantTerms() {
        setMobile_SignificantTerms(null);
    }

    public void setMobile_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setMobile_SignificantTerms("mobile", opLambda, null);
    }

    public void setMobile_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setMobile_SignificantTerms("mobile", opLambda, aggsLambda);
    }

    public void setMobile_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "mobile");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setMobile_IpRange() {
        setMobile_IpRange(null);
    }

    public void setMobile_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setMobile_IpRange("mobile", opLambda, null);
    }

    public void setMobile_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setMobile_IpRange("mobile", opLambda, aggsLambda);
    }

    public void setMobile_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "mobile");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setMobile_Count() {
        setMobile_Count(null);
    }

    public void setMobile_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setMobile_Count("mobile", opLambda);
    }

    public void setMobile_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "mobile");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMobile_Cardinality() {
        setMobile_Cardinality(null);
    }

    public void setMobile_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setMobile_Cardinality("mobile", opLambda);
    }

    public void setMobile_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "mobile");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMobile_Missing() {
        setMobile_Missing(null);
    }

    public void setMobile_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setMobile_Missing("mobile", opLambda, null);
    }

    public void setMobile_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setMobile_Missing("mobile", opLambda, aggsLambda);
    }

    public void setMobile_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "mobile");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setName_Terms() {
        setName_Terms(null);
    }

    public void setName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setName_Terms("name", opLambda, null);
    }

    public void setName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setName_Terms("name", opLambda, aggsLambda);
    }

    public void setName_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
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
            OperatorCall<BsUserCA> aggsLambda) {
        setName_SignificantTerms("name", opLambda, aggsLambda);
    }

    public void setName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
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

    public void setName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setName_IpRange("name", opLambda, aggsLambda);
    }

    public void setName_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
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

    public void setName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setName_Missing("name", opLambda, aggsLambda);
    }

    public void setName_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPager_Terms() {
        setPager_Terms(null);
    }

    public void setPager_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setPager_Terms("pager", opLambda, null);
    }

    public void setPager_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPager_Terms("pager", opLambda, aggsLambda);
    }

    public void setPager_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "pager");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPager_SignificantTerms() {
        setPager_SignificantTerms(null);
    }

    public void setPager_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setPager_SignificantTerms("pager", opLambda, null);
    }

    public void setPager_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setPager_SignificantTerms("pager", opLambda, aggsLambda);
    }

    public void setPager_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "pager");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPager_IpRange() {
        setPager_IpRange(null);
    }

    public void setPager_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setPager_IpRange("pager", opLambda, null);
    }

    public void setPager_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPager_IpRange("pager", opLambda, aggsLambda);
    }

    public void setPager_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "pager");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPager_Count() {
        setPager_Count(null);
    }

    public void setPager_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setPager_Count("pager", opLambda);
    }

    public void setPager_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "pager");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPager_Cardinality() {
        setPager_Cardinality(null);
    }

    public void setPager_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setPager_Cardinality("pager", opLambda);
    }

    public void setPager_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "pager");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPager_Missing() {
        setPager_Missing(null);
    }

    public void setPager_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setPager_Missing("pager", opLambda, null);
    }

    public void setPager_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPager_Missing("pager", opLambda, aggsLambda);
    }

    public void setPager_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "pager");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPassword_Terms() {
        setPassword_Terms(null);
    }

    public void setPassword_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setPassword_Terms("password", opLambda, null);
    }

    public void setPassword_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPassword_Terms("password", opLambda, aggsLambda);
    }

    public void setPassword_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPassword_SignificantTerms() {
        setPassword_SignificantTerms(null);
    }

    public void setPassword_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setPassword_SignificantTerms("password", opLambda, null);
    }

    public void setPassword_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setPassword_SignificantTerms("password", opLambda, aggsLambda);
    }

    public void setPassword_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPassword_IpRange() {
        setPassword_IpRange(null);
    }

    public void setPassword_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setPassword_IpRange("password", opLambda, null);
    }

    public void setPassword_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPassword_IpRange("password", opLambda, aggsLambda);
    }

    public void setPassword_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPassword_Count() {
        setPassword_Count(null);
    }

    public void setPassword_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setPassword_Count("password", opLambda);
    }

    public void setPassword_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Cardinality() {
        setPassword_Cardinality(null);
    }

    public void setPassword_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setPassword_Cardinality("password", opLambda);
    }

    public void setPassword_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Missing() {
        setPassword_Missing(null);
    }

    public void setPassword_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setPassword_Missing("password", opLambda, null);
    }

    public void setPassword_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPassword_Missing("password", opLambda, aggsLambda);
    }

    public void setPassword_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPhysicalDeliveryOfficeName_Terms() {
        setPhysicalDeliveryOfficeName_Terms(null);
    }

    public void setPhysicalDeliveryOfficeName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setPhysicalDeliveryOfficeName_Terms("physicalDeliveryOfficeName", opLambda, null);
    }

    public void setPhysicalDeliveryOfficeName_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setPhysicalDeliveryOfficeName_Terms("physicalDeliveryOfficeName", opLambda, aggsLambda);
    }

    public void setPhysicalDeliveryOfficeName_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "physicalDeliveryOfficeName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPhysicalDeliveryOfficeName_SignificantTerms() {
        setPhysicalDeliveryOfficeName_SignificantTerms(null);
    }

    public void setPhysicalDeliveryOfficeName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setPhysicalDeliveryOfficeName_SignificantTerms("physicalDeliveryOfficeName", opLambda, null);
    }

    public void setPhysicalDeliveryOfficeName_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setPhysicalDeliveryOfficeName_SignificantTerms("physicalDeliveryOfficeName", opLambda, aggsLambda);
    }

    public void setPhysicalDeliveryOfficeName_SignificantTerms(String name,
            ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "physicalDeliveryOfficeName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPhysicalDeliveryOfficeName_IpRange() {
        setPhysicalDeliveryOfficeName_IpRange(null);
    }

    public void setPhysicalDeliveryOfficeName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setPhysicalDeliveryOfficeName_IpRange("physicalDeliveryOfficeName", opLambda, null);
    }

    public void setPhysicalDeliveryOfficeName_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setPhysicalDeliveryOfficeName_IpRange("physicalDeliveryOfficeName", opLambda, aggsLambda);
    }

    public void setPhysicalDeliveryOfficeName_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "physicalDeliveryOfficeName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPhysicalDeliveryOfficeName_Count() {
        setPhysicalDeliveryOfficeName_Count(null);
    }

    public void setPhysicalDeliveryOfficeName_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setPhysicalDeliveryOfficeName_Count("physicalDeliveryOfficeName", opLambda);
    }

    public void setPhysicalDeliveryOfficeName_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "physicalDeliveryOfficeName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPhysicalDeliveryOfficeName_Cardinality() {
        setPhysicalDeliveryOfficeName_Cardinality(null);
    }

    public void setPhysicalDeliveryOfficeName_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setPhysicalDeliveryOfficeName_Cardinality("physicalDeliveryOfficeName", opLambda);
    }

    public void setPhysicalDeliveryOfficeName_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "physicalDeliveryOfficeName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPhysicalDeliveryOfficeName_Missing() {
        setPhysicalDeliveryOfficeName_Missing(null);
    }

    public void setPhysicalDeliveryOfficeName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setPhysicalDeliveryOfficeName_Missing("physicalDeliveryOfficeName", opLambda, null);
    }

    public void setPhysicalDeliveryOfficeName_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setPhysicalDeliveryOfficeName_Missing("physicalDeliveryOfficeName", opLambda, aggsLambda);
    }

    public void setPhysicalDeliveryOfficeName_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "physicalDeliveryOfficeName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPostOfficeBox_Terms() {
        setPostOfficeBox_Terms(null);
    }

    public void setPostOfficeBox_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setPostOfficeBox_Terms("postOfficeBox", opLambda, null);
    }

    public void setPostOfficeBox_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostOfficeBox_Terms("postOfficeBox", opLambda, aggsLambda);
    }

    public void setPostOfficeBox_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "postOfficeBox");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPostOfficeBox_SignificantTerms() {
        setPostOfficeBox_SignificantTerms(null);
    }

    public void setPostOfficeBox_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setPostOfficeBox_SignificantTerms("postOfficeBox", opLambda, null);
    }

    public void setPostOfficeBox_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setPostOfficeBox_SignificantTerms("postOfficeBox", opLambda, aggsLambda);
    }

    public void setPostOfficeBox_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "postOfficeBox");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPostOfficeBox_IpRange() {
        setPostOfficeBox_IpRange(null);
    }

    public void setPostOfficeBox_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setPostOfficeBox_IpRange("postOfficeBox", opLambda, null);
    }

    public void setPostOfficeBox_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostOfficeBox_IpRange("postOfficeBox", opLambda, aggsLambda);
    }

    public void setPostOfficeBox_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "postOfficeBox");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPostOfficeBox_Count() {
        setPostOfficeBox_Count(null);
    }

    public void setPostOfficeBox_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setPostOfficeBox_Count("postOfficeBox", opLambda);
    }

    public void setPostOfficeBox_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "postOfficeBox");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostOfficeBox_Cardinality() {
        setPostOfficeBox_Cardinality(null);
    }

    public void setPostOfficeBox_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setPostOfficeBox_Cardinality("postOfficeBox", opLambda);
    }

    public void setPostOfficeBox_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "postOfficeBox");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostOfficeBox_Missing() {
        setPostOfficeBox_Missing(null);
    }

    public void setPostOfficeBox_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setPostOfficeBox_Missing("postOfficeBox", opLambda, null);
    }

    public void setPostOfficeBox_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostOfficeBox_Missing("postOfficeBox", opLambda, aggsLambda);
    }

    public void setPostOfficeBox_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "postOfficeBox");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPostalAddress_Terms() {
        setPostalAddress_Terms(null);
    }

    public void setPostalAddress_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setPostalAddress_Terms("postalAddress", opLambda, null);
    }

    public void setPostalAddress_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostalAddress_Terms("postalAddress", opLambda, aggsLambda);
    }

    public void setPostalAddress_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "postalAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPostalAddress_SignificantTerms() {
        setPostalAddress_SignificantTerms(null);
    }

    public void setPostalAddress_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setPostalAddress_SignificantTerms("postalAddress", opLambda, null);
    }

    public void setPostalAddress_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setPostalAddress_SignificantTerms("postalAddress", opLambda, aggsLambda);
    }

    public void setPostalAddress_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "postalAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPostalAddress_IpRange() {
        setPostalAddress_IpRange(null);
    }

    public void setPostalAddress_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setPostalAddress_IpRange("postalAddress", opLambda, null);
    }

    public void setPostalAddress_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostalAddress_IpRange("postalAddress", opLambda, aggsLambda);
    }

    public void setPostalAddress_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "postalAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPostalAddress_Count() {
        setPostalAddress_Count(null);
    }

    public void setPostalAddress_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setPostalAddress_Count("postalAddress", opLambda);
    }

    public void setPostalAddress_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "postalAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalAddress_Cardinality() {
        setPostalAddress_Cardinality(null);
    }

    public void setPostalAddress_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setPostalAddress_Cardinality("postalAddress", opLambda);
    }

    public void setPostalAddress_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "postalAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalAddress_Missing() {
        setPostalAddress_Missing(null);
    }

    public void setPostalAddress_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setPostalAddress_Missing("postalAddress", opLambda, null);
    }

    public void setPostalAddress_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostalAddress_Missing("postalAddress", opLambda, aggsLambda);
    }

    public void setPostalAddress_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "postalAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPostalCode_Terms() {
        setPostalCode_Terms(null);
    }

    public void setPostalCode_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setPostalCode_Terms("postalCode", opLambda, null);
    }

    public void setPostalCode_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostalCode_Terms("postalCode", opLambda, aggsLambda);
    }

    public void setPostalCode_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "postalCode");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPostalCode_SignificantTerms() {
        setPostalCode_SignificantTerms(null);
    }

    public void setPostalCode_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setPostalCode_SignificantTerms("postalCode", opLambda, null);
    }

    public void setPostalCode_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setPostalCode_SignificantTerms("postalCode", opLambda, aggsLambda);
    }

    public void setPostalCode_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "postalCode");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPostalCode_IpRange() {
        setPostalCode_IpRange(null);
    }

    public void setPostalCode_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setPostalCode_IpRange("postalCode", opLambda, null);
    }

    public void setPostalCode_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostalCode_IpRange("postalCode", opLambda, aggsLambda);
    }

    public void setPostalCode_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "postalCode");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPostalCode_Count() {
        setPostalCode_Count(null);
    }

    public void setPostalCode_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setPostalCode_Count("postalCode", opLambda);
    }

    public void setPostalCode_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "postalCode");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalCode_Cardinality() {
        setPostalCode_Cardinality(null);
    }

    public void setPostalCode_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setPostalCode_Cardinality("postalCode", opLambda);
    }

    public void setPostalCode_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "postalCode");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalCode_Missing() {
        setPostalCode_Missing(null);
    }

    public void setPostalCode_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setPostalCode_Missing("postalCode", opLambda, null);
    }

    public void setPostalCode_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostalCode_Missing("postalCode", opLambda, aggsLambda);
    }

    public void setPostalCode_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "postalCode");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPreferredLanguage_Terms() {
        setPreferredLanguage_Terms(null);
    }

    public void setPreferredLanguage_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setPreferredLanguage_Terms("preferredLanguage", opLambda, null);
    }

    public void setPreferredLanguage_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPreferredLanguage_Terms("preferredLanguage", opLambda, aggsLambda);
    }

    public void setPreferredLanguage_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "preferredLanguage");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPreferredLanguage_SignificantTerms() {
        setPreferredLanguage_SignificantTerms(null);
    }

    public void setPreferredLanguage_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setPreferredLanguage_SignificantTerms("preferredLanguage", opLambda, null);
    }

    public void setPreferredLanguage_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setPreferredLanguage_SignificantTerms("preferredLanguage", opLambda, aggsLambda);
    }

    public void setPreferredLanguage_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "preferredLanguage");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPreferredLanguage_IpRange() {
        setPreferredLanguage_IpRange(null);
    }

    public void setPreferredLanguage_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setPreferredLanguage_IpRange("preferredLanguage", opLambda, null);
    }

    public void setPreferredLanguage_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPreferredLanguage_IpRange("preferredLanguage", opLambda, aggsLambda);
    }

    public void setPreferredLanguage_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "preferredLanguage");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setPreferredLanguage_Count() {
        setPreferredLanguage_Count(null);
    }

    public void setPreferredLanguage_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setPreferredLanguage_Count("preferredLanguage", opLambda);
    }

    public void setPreferredLanguage_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "preferredLanguage");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPreferredLanguage_Cardinality() {
        setPreferredLanguage_Cardinality(null);
    }

    public void setPreferredLanguage_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setPreferredLanguage_Cardinality("preferredLanguage", opLambda);
    }

    public void setPreferredLanguage_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "preferredLanguage");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPreferredLanguage_Missing() {
        setPreferredLanguage_Missing(null);
    }

    public void setPreferredLanguage_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setPreferredLanguage_Missing("preferredLanguage", opLambda, null);
    }

    public void setPreferredLanguage_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPreferredLanguage_Missing("preferredLanguage", opLambda, aggsLambda);
    }

    public void setPreferredLanguage_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "preferredLanguage");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRegisteredAddress_Terms() {
        setRegisteredAddress_Terms(null);
    }

    public void setRegisteredAddress_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setRegisteredAddress_Terms("registeredAddress", opLambda, null);
    }

    public void setRegisteredAddress_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRegisteredAddress_Terms("registeredAddress", opLambda, aggsLambda);
    }

    public void setRegisteredAddress_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "registeredAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRegisteredAddress_SignificantTerms() {
        setRegisteredAddress_SignificantTerms(null);
    }

    public void setRegisteredAddress_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setRegisteredAddress_SignificantTerms("registeredAddress", opLambda, null);
    }

    public void setRegisteredAddress_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setRegisteredAddress_SignificantTerms("registeredAddress", opLambda, aggsLambda);
    }

    public void setRegisteredAddress_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "registeredAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRegisteredAddress_IpRange() {
        setRegisteredAddress_IpRange(null);
    }

    public void setRegisteredAddress_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setRegisteredAddress_IpRange("registeredAddress", opLambda, null);
    }

    public void setRegisteredAddress_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRegisteredAddress_IpRange("registeredAddress", opLambda, aggsLambda);
    }

    public void setRegisteredAddress_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "registeredAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRegisteredAddress_Count() {
        setRegisteredAddress_Count(null);
    }

    public void setRegisteredAddress_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setRegisteredAddress_Count("registeredAddress", opLambda);
    }

    public void setRegisteredAddress_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "registeredAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegisteredAddress_Cardinality() {
        setRegisteredAddress_Cardinality(null);
    }

    public void setRegisteredAddress_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setRegisteredAddress_Cardinality("registeredAddress", opLambda);
    }

    public void setRegisteredAddress_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "registeredAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegisteredAddress_Missing() {
        setRegisteredAddress_Missing(null);
    }

    public void setRegisteredAddress_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setRegisteredAddress_Missing("registeredAddress", opLambda, null);
    }

    public void setRegisteredAddress_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRegisteredAddress_Missing("registeredAddress", opLambda, aggsLambda);
    }

    public void setRegisteredAddress_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "registeredAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRoles_Terms() {
        setRoles_Terms(null);
    }

    public void setRoles_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setRoles_Terms("roles", opLambda, null);
    }

    public void setRoles_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRoles_Terms("roles", opLambda, aggsLambda);
    }

    public void setRoles_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRoles_SignificantTerms() {
        setRoles_SignificantTerms(null);
    }

    public void setRoles_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setRoles_SignificantTerms("roles", opLambda, null);
    }

    public void setRoles_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setRoles_SignificantTerms("roles", opLambda, aggsLambda);
    }

    public void setRoles_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRoles_IpRange() {
        setRoles_IpRange(null);
    }

    public void setRoles_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setRoles_IpRange("roles", opLambda, null);
    }

    public void setRoles_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRoles_IpRange("roles", opLambda, aggsLambda);
    }

    public void setRoles_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRoles_Count() {
        setRoles_Count(null);
    }

    public void setRoles_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setRoles_Count("roles", opLambda);
    }

    public void setRoles_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_Cardinality() {
        setRoles_Cardinality(null);
    }

    public void setRoles_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setRoles_Cardinality("roles", opLambda);
    }

    public void setRoles_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoles_Missing() {
        setRoles_Missing(null);
    }

    public void setRoles_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setRoles_Missing("roles", opLambda, null);
    }

    public void setRoles_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRoles_Missing("roles", opLambda, aggsLambda);
    }

    public void setRoles_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "roles");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRoomNumber_Terms() {
        setRoomNumber_Terms(null);
    }

    public void setRoomNumber_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setRoomNumber_Terms("roomNumber", opLambda, null);
    }

    public void setRoomNumber_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRoomNumber_Terms("roomNumber", opLambda, aggsLambda);
    }

    public void setRoomNumber_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "roomNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRoomNumber_SignificantTerms() {
        setRoomNumber_SignificantTerms(null);
    }

    public void setRoomNumber_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setRoomNumber_SignificantTerms("roomNumber", opLambda, null);
    }

    public void setRoomNumber_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setRoomNumber_SignificantTerms("roomNumber", opLambda, aggsLambda);
    }

    public void setRoomNumber_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "roomNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRoomNumber_IpRange() {
        setRoomNumber_IpRange(null);
    }

    public void setRoomNumber_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setRoomNumber_IpRange("roomNumber", opLambda, null);
    }

    public void setRoomNumber_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRoomNumber_IpRange("roomNumber", opLambda, aggsLambda);
    }

    public void setRoomNumber_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "roomNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setRoomNumber_Count() {
        setRoomNumber_Count(null);
    }

    public void setRoomNumber_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setRoomNumber_Count("roomNumber", opLambda);
    }

    public void setRoomNumber_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "roomNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoomNumber_Cardinality() {
        setRoomNumber_Cardinality(null);
    }

    public void setRoomNumber_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setRoomNumber_Cardinality("roomNumber", opLambda);
    }

    public void setRoomNumber_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "roomNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoomNumber_Missing() {
        setRoomNumber_Missing(null);
    }

    public void setRoomNumber_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setRoomNumber_Missing("roomNumber", opLambda, null);
    }

    public void setRoomNumber_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRoomNumber_Missing("roomNumber", opLambda, aggsLambda);
    }

    public void setRoomNumber_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "roomNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setState_Terms() {
        setState_Terms(null);
    }

    public void setState_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setState_Terms("state", opLambda, null);
    }

    public void setState_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setState_Terms("state", opLambda, aggsLambda);
    }

    public void setState_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "state");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setState_SignificantTerms() {
        setState_SignificantTerms(null);
    }

    public void setState_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setState_SignificantTerms("state", opLambda, null);
    }

    public void setState_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setState_SignificantTerms("state", opLambda, aggsLambda);
    }

    public void setState_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "state");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setState_IpRange() {
        setState_IpRange(null);
    }

    public void setState_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setState_IpRange("state", opLambda, null);
    }

    public void setState_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setState_IpRange("state", opLambda, aggsLambda);
    }

    public void setState_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "state");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setState_Count() {
        setState_Count(null);
    }

    public void setState_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setState_Count("state", opLambda);
    }

    public void setState_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "state");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setState_Cardinality() {
        setState_Cardinality(null);
    }

    public void setState_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setState_Cardinality("state", opLambda);
    }

    public void setState_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "state");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setState_Missing() {
        setState_Missing(null);
    }

    public void setState_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setState_Missing("state", opLambda, null);
    }

    public void setState_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setState_Missing("state", opLambda, aggsLambda);
    }

    public void setState_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "state");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setStreet_Terms() {
        setStreet_Terms(null);
    }

    public void setStreet_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setStreet_Terms("street", opLambda, null);
    }

    public void setStreet_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setStreet_Terms("street", opLambda, aggsLambda);
    }

    public void setStreet_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "street");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setStreet_SignificantTerms() {
        setStreet_SignificantTerms(null);
    }

    public void setStreet_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setStreet_SignificantTerms("street", opLambda, null);
    }

    public void setStreet_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setStreet_SignificantTerms("street", opLambda, aggsLambda);
    }

    public void setStreet_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "street");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setStreet_IpRange() {
        setStreet_IpRange(null);
    }

    public void setStreet_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setStreet_IpRange("street", opLambda, null);
    }

    public void setStreet_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setStreet_IpRange("street", opLambda, aggsLambda);
    }

    public void setStreet_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "street");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setStreet_Count() {
        setStreet_Count(null);
    }

    public void setStreet_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setStreet_Count("street", opLambda);
    }

    public void setStreet_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "street");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStreet_Cardinality() {
        setStreet_Cardinality(null);
    }

    public void setStreet_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setStreet_Cardinality("street", opLambda);
    }

    public void setStreet_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "street");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStreet_Missing() {
        setStreet_Missing(null);
    }

    public void setStreet_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setStreet_Missing("street", opLambda, null);
    }

    public void setStreet_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setStreet_Missing("street", opLambda, aggsLambda);
    }

    public void setStreet_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "street");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSurname_Terms() {
        setSurname_Terms(null);
    }

    public void setSurname_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setSurname_Terms("surname", opLambda, null);
    }

    public void setSurname_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setSurname_Terms("surname", opLambda, aggsLambda);
    }

    public void setSurname_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "surname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSurname_SignificantTerms() {
        setSurname_SignificantTerms(null);
    }

    public void setSurname_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setSurname_SignificantTerms("surname", opLambda, null);
    }

    public void setSurname_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setSurname_SignificantTerms("surname", opLambda, aggsLambda);
    }

    public void setSurname_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "surname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSurname_IpRange() {
        setSurname_IpRange(null);
    }

    public void setSurname_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setSurname_IpRange("surname", opLambda, null);
    }

    public void setSurname_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setSurname_IpRange("surname", opLambda, aggsLambda);
    }

    public void setSurname_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "surname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setSurname_Count() {
        setSurname_Count(null);
    }

    public void setSurname_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setSurname_Count("surname", opLambda);
    }

    public void setSurname_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "surname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSurname_Cardinality() {
        setSurname_Cardinality(null);
    }

    public void setSurname_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setSurname_Cardinality("surname", opLambda);
    }

    public void setSurname_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "surname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSurname_Missing() {
        setSurname_Missing(null);
    }

    public void setSurname_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setSurname_Missing("surname", opLambda, null);
    }

    public void setSurname_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setSurname_Missing("surname", opLambda, aggsLambda);
    }

    public void setSurname_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "surname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTelephoneNumber_Terms() {
        setTelephoneNumber_Terms(null);
    }

    public void setTelephoneNumber_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setTelephoneNumber_Terms("telephoneNumber", opLambda, null);
    }

    public void setTelephoneNumber_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setTelephoneNumber_Terms("telephoneNumber", opLambda, aggsLambda);
    }

    public void setTelephoneNumber_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "telephoneNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTelephoneNumber_SignificantTerms() {
        setTelephoneNumber_SignificantTerms(null);
    }

    public void setTelephoneNumber_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setTelephoneNumber_SignificantTerms("telephoneNumber", opLambda, null);
    }

    public void setTelephoneNumber_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setTelephoneNumber_SignificantTerms("telephoneNumber", opLambda, aggsLambda);
    }

    public void setTelephoneNumber_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "telephoneNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTelephoneNumber_IpRange() {
        setTelephoneNumber_IpRange(null);
    }

    public void setTelephoneNumber_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setTelephoneNumber_IpRange("telephoneNumber", opLambda, null);
    }

    public void setTelephoneNumber_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setTelephoneNumber_IpRange("telephoneNumber", opLambda, aggsLambda);
    }

    public void setTelephoneNumber_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "telephoneNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTelephoneNumber_Count() {
        setTelephoneNumber_Count(null);
    }

    public void setTelephoneNumber_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setTelephoneNumber_Count("telephoneNumber", opLambda);
    }

    public void setTelephoneNumber_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "telephoneNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTelephoneNumber_Cardinality() {
        setTelephoneNumber_Cardinality(null);
    }

    public void setTelephoneNumber_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setTelephoneNumber_Cardinality("telephoneNumber", opLambda);
    }

    public void setTelephoneNumber_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "telephoneNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTelephoneNumber_Missing() {
        setTelephoneNumber_Missing(null);
    }

    public void setTelephoneNumber_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setTelephoneNumber_Missing("telephoneNumber", opLambda, null);
    }

    public void setTelephoneNumber_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setTelephoneNumber_Missing("telephoneNumber", opLambda, aggsLambda);
    }

    public void setTelephoneNumber_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "telephoneNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTeletexTerminalIdentifier_Terms() {
        setTeletexTerminalIdentifier_Terms(null);
    }

    public void setTeletexTerminalIdentifier_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setTeletexTerminalIdentifier_Terms("teletexTerminalIdentifier", opLambda, null);
    }

    public void setTeletexTerminalIdentifier_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setTeletexTerminalIdentifier_Terms("teletexTerminalIdentifier", opLambda, aggsLambda);
    }

    public void setTeletexTerminalIdentifier_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "teletexTerminalIdentifier");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTeletexTerminalIdentifier_SignificantTerms() {
        setTeletexTerminalIdentifier_SignificantTerms(null);
    }

    public void setTeletexTerminalIdentifier_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setTeletexTerminalIdentifier_SignificantTerms("teletexTerminalIdentifier", opLambda, null);
    }

    public void setTeletexTerminalIdentifier_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setTeletexTerminalIdentifier_SignificantTerms("teletexTerminalIdentifier", opLambda, aggsLambda);
    }

    public void setTeletexTerminalIdentifier_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "teletexTerminalIdentifier");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTeletexTerminalIdentifier_IpRange() {
        setTeletexTerminalIdentifier_IpRange(null);
    }

    public void setTeletexTerminalIdentifier_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setTeletexTerminalIdentifier_IpRange("teletexTerminalIdentifier", opLambda, null);
    }

    public void setTeletexTerminalIdentifier_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setTeletexTerminalIdentifier_IpRange("teletexTerminalIdentifier", opLambda, aggsLambda);
    }

    public void setTeletexTerminalIdentifier_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "teletexTerminalIdentifier");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTeletexTerminalIdentifier_Count() {
        setTeletexTerminalIdentifier_Count(null);
    }

    public void setTeletexTerminalIdentifier_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setTeletexTerminalIdentifier_Count("teletexTerminalIdentifier", opLambda);
    }

    public void setTeletexTerminalIdentifier_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "teletexTerminalIdentifier");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTeletexTerminalIdentifier_Cardinality() {
        setTeletexTerminalIdentifier_Cardinality(null);
    }

    public void setTeletexTerminalIdentifier_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setTeletexTerminalIdentifier_Cardinality("teletexTerminalIdentifier", opLambda);
    }

    public void setTeletexTerminalIdentifier_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "teletexTerminalIdentifier");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTeletexTerminalIdentifier_Missing() {
        setTeletexTerminalIdentifier_Missing(null);
    }

    public void setTeletexTerminalIdentifier_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setTeletexTerminalIdentifier_Missing("teletexTerminalIdentifier", opLambda, null);
    }

    public void setTeletexTerminalIdentifier_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setTeletexTerminalIdentifier_Missing("teletexTerminalIdentifier", opLambda, aggsLambda);
    }

    public void setTeletexTerminalIdentifier_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "teletexTerminalIdentifier");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTitle_Terms() {
        setTitle_Terms(null);
    }

    public void setTitle_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setTitle_Terms("title", opLambda, null);
    }

    public void setTitle_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setTitle_Terms("title", opLambda, aggsLambda);
    }

    public void setTitle_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "title");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTitle_SignificantTerms() {
        setTitle_SignificantTerms(null);
    }

    public void setTitle_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setTitle_SignificantTerms("title", opLambda, null);
    }

    public void setTitle_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setTitle_SignificantTerms("title", opLambda, aggsLambda);
    }

    public void setTitle_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "title");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTitle_IpRange() {
        setTitle_IpRange(null);
    }

    public void setTitle_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setTitle_IpRange("title", opLambda, null);
    }

    public void setTitle_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setTitle_IpRange("title", opLambda, aggsLambda);
    }

    public void setTitle_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "title");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setTitle_Count() {
        setTitle_Count(null);
    }

    public void setTitle_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setTitle_Count("title", opLambda);
    }

    public void setTitle_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "title");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTitle_Cardinality() {
        setTitle_Cardinality(null);
    }

    public void setTitle_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setTitle_Cardinality("title", opLambda);
    }

    public void setTitle_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "title");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTitle_Missing() {
        setTitle_Missing(null);
    }

    public void setTitle_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setTitle_Missing("title", opLambda, null);
    }

    public void setTitle_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setTitle_Missing("title", opLambda, aggsLambda);
    }

    public void setTitle_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "title");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUidNumber_Avg() {
        setUidNumber_Avg(null);
    }

    public void setUidNumber_Avg(ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        setUidNumber_Avg("uidNumber", opLambda);
    }

    public void setUidNumber_Avg(String name, ConditionOptionCall<AvgAggregationBuilder> opLambda) {
        AvgAggregationBuilder builder = regAvgA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_Max() {
        setUidNumber_Max(null);
    }

    public void setUidNumber_Max(ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        setUidNumber_Max("uidNumber", opLambda);
    }

    public void setUidNumber_Max(String name, ConditionOptionCall<MaxAggregationBuilder> opLambda) {
        MaxAggregationBuilder builder = regMaxA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_Min() {
        setUidNumber_Min(null);
    }

    public void setUidNumber_Min(ConditionOptionCall<MinAggregationBuilder> opLambda) {
        setUidNumber_Min("uidNumber", opLambda);
    }

    public void setUidNumber_Min(String name, ConditionOptionCall<MinAggregationBuilder> opLambda) {
        MinAggregationBuilder builder = regMinA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_Sum() {
        setUidNumber_Sum(null);
    }

    public void setUidNumber_Sum(ConditionOptionCall<SumAggregationBuilder> opLambda) {
        setUidNumber_Sum("uidNumber", opLambda);
    }

    public void setUidNumber_Sum(String name, ConditionOptionCall<SumAggregationBuilder> opLambda) {
        SumAggregationBuilder builder = regSumA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_ExtendedStats() {
        setUidNumber_ExtendedStats(null);
    }

    public void setUidNumber_ExtendedStats(ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        setUidNumber_ExtendedStats("uidNumber", opLambda);
    }

    public void setUidNumber_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsAggregationBuilder> opLambda) {
        ExtendedStatsAggregationBuilder builder = regExtendedStatsA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_Stats() {
        setUidNumber_Stats(null);
    }

    public void setUidNumber_Stats(ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        setUidNumber_Stats("uidNumber", opLambda);
    }

    public void setUidNumber_Stats(String name, ConditionOptionCall<StatsAggregationBuilder> opLambda) {
        StatsAggregationBuilder builder = regStatsA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_Percentiles() {
        setUidNumber_Percentiles(null);
    }

    public void setUidNumber_Percentiles(ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        setUidNumber_Percentiles("uidNumber", opLambda);
    }

    public void setUidNumber_Percentiles(String name, ConditionOptionCall<PercentilesAggregationBuilder> opLambda) {
        PercentilesAggregationBuilder builder = regPercentilesA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_PercentileRanks(double[] values) {
        setUidNumber_PercentileRanks(values, null);
    }

    public void setUidNumber_PercentileRanks(double[] values, ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        setUidNumber_PercentileRanks("uidNumber", values, opLambda);
    }

    public void setUidNumber_PercentileRanks(String name, double[] values,
            ConditionOptionCall<PercentileRanksAggregationBuilder> opLambda) {
        PercentileRanksAggregationBuilder builder = regPercentileRanksA(name, "uidNumber", values);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_Histogram() {
        setUidNumber_Histogram(null);
    }

    public void setUidNumber_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda) {
        setUidNumber_Histogram("uidNumber", opLambda, null);
    }

    public void setUidNumber_Histogram(ConditionOptionCall<HistogramAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setUidNumber_Histogram("uidNumber", opLambda, aggsLambda);
    }

    public void setUidNumber_Histogram(String name, ConditionOptionCall<HistogramAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        HistogramAggregationBuilder builder = regHistogramA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUidNumber_Range() {
        setUidNumber_Range(null);
    }

    public void setUidNumber_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda) {
        setUidNumber_Range("uidNumber", opLambda, null);
    }

    public void setUidNumber_Range(ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setUidNumber_Range("uidNumber", opLambda, aggsLambda);
    }

    public void setUidNumber_Range(String name, ConditionOptionCall<RangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        RangeAggregationBuilder builder = regRangeA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUidNumber_Count() {
        setUidNumber_Count(null);
    }

    public void setUidNumber_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setUidNumber_Count("uidNumber", opLambda);
    }

    public void setUidNumber_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_Cardinality() {
        setUidNumber_Cardinality(null);
    }

    public void setUidNumber_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setUidNumber_Cardinality("uidNumber", opLambda);
    }

    public void setUidNumber_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_Missing() {
        setUidNumber_Missing(null);
    }

    public void setUidNumber_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setUidNumber_Missing("uidNumber", opLambda, null);
    }

    public void setUidNumber_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setUidNumber_Missing("uidNumber", opLambda, aggsLambda);
    }

    public void setUidNumber_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setX121Address_Terms() {
        setX121Address_Terms(null);
    }

    public void setX121Address_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda) {
        setX121Address_Terms("x121Address", opLambda, null);
    }

    public void setX121Address_Terms(ConditionOptionCall<TermsAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setX121Address_Terms("x121Address", opLambda, aggsLambda);
    }

    public void setX121Address_Terms(String name, ConditionOptionCall<TermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsAggregationBuilder builder = regTermsA(name, "x121Address");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setX121Address_SignificantTerms() {
        setX121Address_SignificantTerms(null);
    }

    public void setX121Address_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda) {
        setX121Address_SignificantTerms("x121Address", opLambda, null);
    }

    public void setX121Address_SignificantTerms(ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setX121Address_SignificantTerms("x121Address", opLambda, aggsLambda);
    }

    public void setX121Address_SignificantTerms(String name, ConditionOptionCall<SignificantTermsAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsAggregationBuilder builder = regSignificantTermsA(name, "x121Address");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setX121Address_IpRange() {
        setX121Address_IpRange(null);
    }

    public void setX121Address_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda) {
        setX121Address_IpRange("x121Address", opLambda, null);
    }

    public void setX121Address_IpRange(ConditionOptionCall<IpRangeAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setX121Address_IpRange("x121Address", opLambda, aggsLambda);
    }

    public void setX121Address_IpRange(String name, ConditionOptionCall<IpRangeAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IpRangeAggregationBuilder builder = regIpRangeA(name, "x121Address");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setX121Address_Count() {
        setX121Address_Count(null);
    }

    public void setX121Address_Count(ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        setX121Address_Count("x121Address", opLambda);
    }

    public void setX121Address_Count(String name, ConditionOptionCall<ValueCountAggregationBuilder> opLambda) {
        ValueCountAggregationBuilder builder = regCountA(name, "x121Address");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setX121Address_Cardinality() {
        setX121Address_Cardinality(null);
    }

    public void setX121Address_Cardinality(ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        setX121Address_Cardinality("x121Address", opLambda);
    }

    public void setX121Address_Cardinality(String name, ConditionOptionCall<CardinalityAggregationBuilder> opLambda) {
        CardinalityAggregationBuilder builder = regCardinalityA(name, "x121Address");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setX121Address_Missing() {
        setX121Address_Missing(null);
    }

    public void setX121Address_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda) {
        setX121Address_Missing("x121Address", opLambda, null);
    }

    public void setX121Address_Missing(ConditionOptionCall<MissingAggregationBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setX121Address_Missing("x121Address", opLambda, aggsLambda);
    }

    public void setX121Address_Missing(String name, ConditionOptionCall<MissingAggregationBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingAggregationBuilder builder = regMissingA(name, "x121Address");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
