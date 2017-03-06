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
package org.codelibs.fess.es.user.cbean.ca.bs;

import org.codelibs.fess.es.user.allcommon.EsAbstractConditionAggregation;
import org.codelibs.fess.es.user.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.user.cbean.ca.UserCA;
import org.codelibs.fess.es.user.cbean.cq.UserCQ;
import org.codelibs.fess.es.user.cbean.cq.bs.BsUserCQ;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.global.GlobalBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.HistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.missing.MissingBuilder;
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder;
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

    public void global(String name, ConditionOptionCall<GlobalBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        GlobalBuilder builder = regGlobalA(name);
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

    // String name

    public void setName_Terms() {
        setName_Terms(null);
    }

    public void setName_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setName_Terms("name", opLambda, null);
    }

    public void setName_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setName_Terms("name", opLambda, aggsLambda);
    }

    public void setName_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "name");
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

    public void setName_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setName_SignificantTerms("name", opLambda, null);
    }

    public void setName_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setName_SignificantTerms("name", opLambda, aggsLambda);
    }

    public void setName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "name");
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

    public void setName_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setName_IpRange("name", opLambda, null);
    }

    public void setName_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setName_IpRange("name", opLambda, aggsLambda);
    }

    public void setName_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "name");
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

    public void setName_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setName_Count("name", opLambda);
    }

    public void setName_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Cardinality() {
        setName_Cardinality(null);
    }

    public void setName_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setName_Cardinality("name", opLambda);
    }

    public void setName_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setName_Missing() {
        setName_Missing(null);
    }

    public void setName_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setName_Missing("name", opLambda, null);
    }

    public void setName_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setName_Missing("name", opLambda, aggsLambda);
    }

    public void setName_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "name");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String password

    public void setPassword_Terms() {
        setPassword_Terms(null);
    }

    public void setPassword_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setPassword_Terms("password", opLambda, null);
    }

    public void setPassword_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPassword_Terms("password", opLambda, aggsLambda);
    }

    public void setPassword_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "password");
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

    public void setPassword_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setPassword_SignificantTerms("password", opLambda, null);
    }

    public void setPassword_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPassword_SignificantTerms("password", opLambda, aggsLambda);
    }

    public void setPassword_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "password");
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

    public void setPassword_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setPassword_IpRange("password", opLambda, null);
    }

    public void setPassword_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPassword_IpRange("password", opLambda, aggsLambda);
    }

    public void setPassword_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "password");
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

    public void setPassword_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setPassword_Count("password", opLambda);
    }

    public void setPassword_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Cardinality() {
        setPassword_Cardinality(null);
    }

    public void setPassword_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setPassword_Cardinality("password", opLambda);
    }

    public void setPassword_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPassword_Missing() {
        setPassword_Missing(null);
    }

    public void setPassword_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setPassword_Missing("password", opLambda, null);
    }

    public void setPassword_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPassword_Missing("password", opLambda, aggsLambda);
    }

    public void setPassword_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "password");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String surname

    public void setSurname_Terms() {
        setSurname_Terms(null);
    }

    public void setSurname_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setSurname_Terms("surname", opLambda, null);
    }

    public void setSurname_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setSurname_Terms("surname", opLambda, aggsLambda);
    }

    public void setSurname_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "surname");
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

    public void setSurname_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setSurname_SignificantTerms("surname", opLambda, null);
    }

    public void setSurname_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setSurname_SignificantTerms("surname", opLambda, aggsLambda);
    }

    public void setSurname_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "surname");
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

    public void setSurname_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setSurname_IpRange("surname", opLambda, null);
    }

    public void setSurname_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setSurname_IpRange("surname", opLambda, aggsLambda);
    }

    public void setSurname_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "surname");
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

    public void setSurname_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setSurname_Count("surname", opLambda);
    }

    public void setSurname_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "surname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSurname_Cardinality() {
        setSurname_Cardinality(null);
    }

    public void setSurname_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setSurname_Cardinality("surname", opLambda);
    }

    public void setSurname_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "surname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setSurname_Missing() {
        setSurname_Missing(null);
    }

    public void setSurname_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setSurname_Missing("surname", opLambda, null);
    }

    public void setSurname_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setSurname_Missing("surname", opLambda, aggsLambda);
    }

    public void setSurname_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "surname");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String givenName

    public void setGivenName_Terms() {
        setGivenName_Terms(null);
    }

    public void setGivenName_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setGivenName_Terms("givenName", opLambda, null);
    }

    public void setGivenName_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGivenName_Terms("givenName", opLambda, aggsLambda);
    }

    public void setGivenName_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "givenName");
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

    public void setGivenName_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setGivenName_SignificantTerms("givenName", opLambda, null);
    }

    public void setGivenName_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGivenName_SignificantTerms("givenName", opLambda, aggsLambda);
    }

    public void setGivenName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "givenName");
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

    public void setGivenName_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setGivenName_IpRange("givenName", opLambda, null);
    }

    public void setGivenName_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGivenName_IpRange("givenName", opLambda, aggsLambda);
    }

    public void setGivenName_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "givenName");
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

    public void setGivenName_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setGivenName_Count("givenName", opLambda);
    }

    public void setGivenName_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "givenName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGivenName_Cardinality() {
        setGivenName_Cardinality(null);
    }

    public void setGivenName_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setGivenName_Cardinality("givenName", opLambda);
    }

    public void setGivenName_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "givenName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGivenName_Missing() {
        setGivenName_Missing(null);
    }

    public void setGivenName_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setGivenName_Missing("givenName", opLambda, null);
    }

    public void setGivenName_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGivenName_Missing("givenName", opLambda, aggsLambda);
    }

    public void setGivenName_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "givenName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String employeeNumber

    public void setEmployeeNumber_Terms() {
        setEmployeeNumber_Terms(null);
    }

    public void setEmployeeNumber_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setEmployeeNumber_Terms("employeeNumber", opLambda, null);
    }

    public void setEmployeeNumber_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setEmployeeNumber_Terms("employeeNumber", opLambda, aggsLambda);
    }

    public void setEmployeeNumber_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "employeeNumber");
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

    public void setEmployeeNumber_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setEmployeeNumber_SignificantTerms("employeeNumber", opLambda, null);
    }

    public void setEmployeeNumber_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setEmployeeNumber_SignificantTerms("employeeNumber", opLambda, aggsLambda);
    }

    public void setEmployeeNumber_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "employeeNumber");
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

    public void setEmployeeNumber_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setEmployeeNumber_IpRange("employeeNumber", opLambda, null);
    }

    public void setEmployeeNumber_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setEmployeeNumber_IpRange("employeeNumber", opLambda, aggsLambda);
    }

    public void setEmployeeNumber_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "employeeNumber");
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

    public void setEmployeeNumber_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setEmployeeNumber_Count("employeeNumber", opLambda);
    }

    public void setEmployeeNumber_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "employeeNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeNumber_Cardinality() {
        setEmployeeNumber_Cardinality(null);
    }

    public void setEmployeeNumber_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setEmployeeNumber_Cardinality("employeeNumber", opLambda);
    }

    public void setEmployeeNumber_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "employeeNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeNumber_Missing() {
        setEmployeeNumber_Missing(null);
    }

    public void setEmployeeNumber_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setEmployeeNumber_Missing("employeeNumber", opLambda, null);
    }

    public void setEmployeeNumber_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setEmployeeNumber_Missing("employeeNumber", opLambda, aggsLambda);
    }

    public void setEmployeeNumber_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "employeeNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String mail

    public void setMail_Terms() {
        setMail_Terms(null);
    }

    public void setMail_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setMail_Terms("mail", opLambda, null);
    }

    public void setMail_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setMail_Terms("mail", opLambda, aggsLambda);
    }

    public void setMail_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "mail");
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

    public void setMail_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setMail_SignificantTerms("mail", opLambda, null);
    }

    public void setMail_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setMail_SignificantTerms("mail", opLambda, aggsLambda);
    }

    public void setMail_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "mail");
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

    public void setMail_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setMail_IpRange("mail", opLambda, null);
    }

    public void setMail_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setMail_IpRange("mail", opLambda, aggsLambda);
    }

    public void setMail_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "mail");
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

    public void setMail_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setMail_Count("mail", opLambda);
    }

    public void setMail_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "mail");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMail_Cardinality() {
        setMail_Cardinality(null);
    }

    public void setMail_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setMail_Cardinality("mail", opLambda);
    }

    public void setMail_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "mail");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMail_Missing() {
        setMail_Missing(null);
    }

    public void setMail_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setMail_Missing("mail", opLambda, null);
    }

    public void setMail_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setMail_Missing("mail", opLambda, aggsLambda);
    }

    public void setMail_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "mail");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String telephoneNumber

    public void setTelephoneNumber_Terms() {
        setTelephoneNumber_Terms(null);
    }

    public void setTelephoneNumber_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setTelephoneNumber_Terms("telephoneNumber", opLambda, null);
    }

    public void setTelephoneNumber_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setTelephoneNumber_Terms("telephoneNumber", opLambda, aggsLambda);
    }

    public void setTelephoneNumber_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "telephoneNumber");
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

    public void setTelephoneNumber_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setTelephoneNumber_SignificantTerms("telephoneNumber", opLambda, null);
    }

    public void setTelephoneNumber_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setTelephoneNumber_SignificantTerms("telephoneNumber", opLambda, aggsLambda);
    }

    public void setTelephoneNumber_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "telephoneNumber");
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

    public void setTelephoneNumber_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setTelephoneNumber_IpRange("telephoneNumber", opLambda, null);
    }

    public void setTelephoneNumber_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setTelephoneNumber_IpRange("telephoneNumber", opLambda, aggsLambda);
    }

    public void setTelephoneNumber_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "telephoneNumber");
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

    public void setTelephoneNumber_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setTelephoneNumber_Count("telephoneNumber", opLambda);
    }

    public void setTelephoneNumber_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "telephoneNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTelephoneNumber_Cardinality() {
        setTelephoneNumber_Cardinality(null);
    }

    public void setTelephoneNumber_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setTelephoneNumber_Cardinality("telephoneNumber", opLambda);
    }

    public void setTelephoneNumber_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "telephoneNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTelephoneNumber_Missing() {
        setTelephoneNumber_Missing(null);
    }

    public void setTelephoneNumber_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setTelephoneNumber_Missing("telephoneNumber", opLambda, null);
    }

    public void setTelephoneNumber_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setTelephoneNumber_Missing("telephoneNumber", opLambda, aggsLambda);
    }

    public void setTelephoneNumber_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "telephoneNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String homePhone

    public void setHomePhone_Terms() {
        setHomePhone_Terms(null);
    }

    public void setHomePhone_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setHomePhone_Terms("homePhone", opLambda, null);
    }

    public void setHomePhone_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomePhone_Terms("homePhone", opLambda, aggsLambda);
    }

    public void setHomePhone_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "homePhone");
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

    public void setHomePhone_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setHomePhone_SignificantTerms("homePhone", opLambda, null);
    }

    public void setHomePhone_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomePhone_SignificantTerms("homePhone", opLambda, aggsLambda);
    }

    public void setHomePhone_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "homePhone");
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

    public void setHomePhone_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setHomePhone_IpRange("homePhone", opLambda, null);
    }

    public void setHomePhone_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomePhone_IpRange("homePhone", opLambda, aggsLambda);
    }

    public void setHomePhone_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "homePhone");
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

    public void setHomePhone_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setHomePhone_Count("homePhone", opLambda);
    }

    public void setHomePhone_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "homePhone");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePhone_Cardinality() {
        setHomePhone_Cardinality(null);
    }

    public void setHomePhone_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setHomePhone_Cardinality("homePhone", opLambda);
    }

    public void setHomePhone_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "homePhone");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePhone_Missing() {
        setHomePhone_Missing(null);
    }

    public void setHomePhone_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setHomePhone_Missing("homePhone", opLambda, null);
    }

    public void setHomePhone_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomePhone_Missing("homePhone", opLambda, aggsLambda);
    }

    public void setHomePhone_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "homePhone");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String homePostalAddress

    public void setHomePostalAddress_Terms() {
        setHomePostalAddress_Terms(null);
    }

    public void setHomePostalAddress_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setHomePostalAddress_Terms("homePostalAddress", opLambda, null);
    }

    public void setHomePostalAddress_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomePostalAddress_Terms("homePostalAddress", opLambda, aggsLambda);
    }

    public void setHomePostalAddress_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "homePostalAddress");
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

    public void setHomePostalAddress_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setHomePostalAddress_SignificantTerms("homePostalAddress", opLambda, null);
    }

    public void setHomePostalAddress_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setHomePostalAddress_SignificantTerms("homePostalAddress", opLambda, aggsLambda);
    }

    public void setHomePostalAddress_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "homePostalAddress");
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

    public void setHomePostalAddress_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setHomePostalAddress_IpRange("homePostalAddress", opLambda, null);
    }

    public void setHomePostalAddress_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomePostalAddress_IpRange("homePostalAddress", opLambda, aggsLambda);
    }

    public void setHomePostalAddress_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "homePostalAddress");
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

    public void setHomePostalAddress_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setHomePostalAddress_Count("homePostalAddress", opLambda);
    }

    public void setHomePostalAddress_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "homePostalAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePostalAddress_Cardinality() {
        setHomePostalAddress_Cardinality(null);
    }

    public void setHomePostalAddress_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setHomePostalAddress_Cardinality("homePostalAddress", opLambda);
    }

    public void setHomePostalAddress_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "homePostalAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomePostalAddress_Missing() {
        setHomePostalAddress_Missing(null);
    }

    public void setHomePostalAddress_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setHomePostalAddress_Missing("homePostalAddress", opLambda, null);
    }

    public void setHomePostalAddress_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomePostalAddress_Missing("homePostalAddress", opLambda, aggsLambda);
    }

    public void setHomePostalAddress_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "homePostalAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String labeledURI

    public void setLabeledURI_Terms() {
        setLabeledURI_Terms(null);
    }

    public void setLabeledURI_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setLabeledURI_Terms("labeledURI", opLambda, null);
    }

    public void setLabeledURI_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setLabeledURI_Terms("labeledURI", opLambda, aggsLambda);
    }

    public void setLabeledURI_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "labeledURI");
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

    public void setLabeledURI_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setLabeledURI_SignificantTerms("labeledURI", opLambda, null);
    }

    public void setLabeledURI_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setLabeledURI_SignificantTerms("labeledURI", opLambda, aggsLambda);
    }

    public void setLabeledURI_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "labeledURI");
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

    public void setLabeledURI_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setLabeledURI_IpRange("labeledURI", opLambda, null);
    }

    public void setLabeledURI_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setLabeledURI_IpRange("labeledURI", opLambda, aggsLambda);
    }

    public void setLabeledURI_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "labeledURI");
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

    public void setLabeledURI_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setLabeledURI_Count("labeledURI", opLambda);
    }

    public void setLabeledURI_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "labeledURI");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabeledURI_Cardinality() {
        setLabeledURI_Cardinality(null);
    }

    public void setLabeledURI_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setLabeledURI_Cardinality("labeledURI", opLambda);
    }

    public void setLabeledURI_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "labeledURI");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setLabeledURI_Missing() {
        setLabeledURI_Missing(null);
    }

    public void setLabeledURI_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setLabeledURI_Missing("labeledURI", opLambda, null);
    }

    public void setLabeledURI_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setLabeledURI_Missing("labeledURI", opLambda, aggsLambda);
    }

    public void setLabeledURI_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "labeledURI");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String roomNumber

    public void setRoomNumber_Terms() {
        setRoomNumber_Terms(null);
    }

    public void setRoomNumber_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setRoomNumber_Terms("roomNumber", opLambda, null);
    }

    public void setRoomNumber_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRoomNumber_Terms("roomNumber", opLambda, aggsLambda);
    }

    public void setRoomNumber_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "roomNumber");
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

    public void setRoomNumber_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setRoomNumber_SignificantTerms("roomNumber", opLambda, null);
    }

    public void setRoomNumber_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRoomNumber_SignificantTerms("roomNumber", opLambda, aggsLambda);
    }

    public void setRoomNumber_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "roomNumber");
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

    public void setRoomNumber_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setRoomNumber_IpRange("roomNumber", opLambda, null);
    }

    public void setRoomNumber_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRoomNumber_IpRange("roomNumber", opLambda, aggsLambda);
    }

    public void setRoomNumber_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "roomNumber");
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

    public void setRoomNumber_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setRoomNumber_Count("roomNumber", opLambda);
    }

    public void setRoomNumber_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "roomNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoomNumber_Cardinality() {
        setRoomNumber_Cardinality(null);
    }

    public void setRoomNumber_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setRoomNumber_Cardinality("roomNumber", opLambda);
    }

    public void setRoomNumber_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "roomNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRoomNumber_Missing() {
        setRoomNumber_Missing(null);
    }

    public void setRoomNumber_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setRoomNumber_Missing("roomNumber", opLambda, null);
    }

    public void setRoomNumber_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRoomNumber_Missing("roomNumber", opLambda, aggsLambda);
    }

    public void setRoomNumber_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "roomNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String description

    public void setDescription_Terms() {
        setDescription_Terms(null);
    }

    public void setDescription_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setDescription_Terms("description", opLambda, null);
    }

    public void setDescription_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDescription_Terms("description", opLambda, aggsLambda);
    }

    public void setDescription_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "description");
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

    public void setDescription_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setDescription_SignificantTerms("description", opLambda, null);
    }

    public void setDescription_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDescription_SignificantTerms("description", opLambda, aggsLambda);
    }

    public void setDescription_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "description");
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

    public void setDescription_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setDescription_IpRange("description", opLambda, null);
    }

    public void setDescription_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDescription_IpRange("description", opLambda, aggsLambda);
    }

    public void setDescription_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "description");
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

    public void setDescription_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setDescription_Count("description", opLambda);
    }

    public void setDescription_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "description");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_Cardinality() {
        setDescription_Cardinality(null);
    }

    public void setDescription_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setDescription_Cardinality("description", opLambda);
    }

    public void setDescription_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "description");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDescription_Missing() {
        setDescription_Missing(null);
    }

    public void setDescription_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setDescription_Missing("description", opLambda, null);
    }

    public void setDescription_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDescription_Missing("description", opLambda, aggsLambda);
    }

    public void setDescription_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "description");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String title

    public void setTitle_Terms() {
        setTitle_Terms(null);
    }

    public void setTitle_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setTitle_Terms("title", opLambda, null);
    }

    public void setTitle_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setTitle_Terms("title", opLambda, aggsLambda);
    }

    public void setTitle_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "title");
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

    public void setTitle_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setTitle_SignificantTerms("title", opLambda, null);
    }

    public void setTitle_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setTitle_SignificantTerms("title", opLambda, aggsLambda);
    }

    public void setTitle_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "title");
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

    public void setTitle_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setTitle_IpRange("title", opLambda, null);
    }

    public void setTitle_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setTitle_IpRange("title", opLambda, aggsLambda);
    }

    public void setTitle_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "title");
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

    public void setTitle_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setTitle_Count("title", opLambda);
    }

    public void setTitle_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "title");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTitle_Cardinality() {
        setTitle_Cardinality(null);
    }

    public void setTitle_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setTitle_Cardinality("title", opLambda);
    }

    public void setTitle_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "title");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTitle_Missing() {
        setTitle_Missing(null);
    }

    public void setTitle_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setTitle_Missing("title", opLambda, null);
    }

    public void setTitle_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setTitle_Missing("title", opLambda, aggsLambda);
    }

    public void setTitle_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "title");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String pager

    public void setPager_Terms() {
        setPager_Terms(null);
    }

    public void setPager_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setPager_Terms("pager", opLambda, null);
    }

    public void setPager_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPager_Terms("pager", opLambda, aggsLambda);
    }

    public void setPager_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "pager");
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

    public void setPager_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setPager_SignificantTerms("pager", opLambda, null);
    }

    public void setPager_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPager_SignificantTerms("pager", opLambda, aggsLambda);
    }

    public void setPager_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "pager");
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

    public void setPager_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setPager_IpRange("pager", opLambda, null);
    }

    public void setPager_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPager_IpRange("pager", opLambda, aggsLambda);
    }

    public void setPager_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "pager");
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

    public void setPager_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setPager_Count("pager", opLambda);
    }

    public void setPager_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "pager");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPager_Cardinality() {
        setPager_Cardinality(null);
    }

    public void setPager_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setPager_Cardinality("pager", opLambda);
    }

    public void setPager_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "pager");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPager_Missing() {
        setPager_Missing(null);
    }

    public void setPager_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setPager_Missing("pager", opLambda, null);
    }

    public void setPager_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPager_Missing("pager", opLambda, aggsLambda);
    }

    public void setPager_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "pager");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String street

    public void setStreet_Terms() {
        setStreet_Terms(null);
    }

    public void setStreet_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setStreet_Terms("street", opLambda, null);
    }

    public void setStreet_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setStreet_Terms("street", opLambda, aggsLambda);
    }

    public void setStreet_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "street");
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

    public void setStreet_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setStreet_SignificantTerms("street", opLambda, null);
    }

    public void setStreet_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setStreet_SignificantTerms("street", opLambda, aggsLambda);
    }

    public void setStreet_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "street");
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

    public void setStreet_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setStreet_IpRange("street", opLambda, null);
    }

    public void setStreet_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setStreet_IpRange("street", opLambda, aggsLambda);
    }

    public void setStreet_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "street");
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

    public void setStreet_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setStreet_Count("street", opLambda);
    }

    public void setStreet_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "street");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStreet_Cardinality() {
        setStreet_Cardinality(null);
    }

    public void setStreet_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setStreet_Cardinality("street", opLambda);
    }

    public void setStreet_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "street");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setStreet_Missing() {
        setStreet_Missing(null);
    }

    public void setStreet_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setStreet_Missing("street", opLambda, null);
    }

    public void setStreet_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setStreet_Missing("street", opLambda, aggsLambda);
    }

    public void setStreet_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "street");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String postalCode

    public void setPostalCode_Terms() {
        setPostalCode_Terms(null);
    }

    public void setPostalCode_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setPostalCode_Terms("postalCode", opLambda, null);
    }

    public void setPostalCode_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostalCode_Terms("postalCode", opLambda, aggsLambda);
    }

    public void setPostalCode_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "postalCode");
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

    public void setPostalCode_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setPostalCode_SignificantTerms("postalCode", opLambda, null);
    }

    public void setPostalCode_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostalCode_SignificantTerms("postalCode", opLambda, aggsLambda);
    }

    public void setPostalCode_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "postalCode");
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

    public void setPostalCode_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setPostalCode_IpRange("postalCode", opLambda, null);
    }

    public void setPostalCode_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostalCode_IpRange("postalCode", opLambda, aggsLambda);
    }

    public void setPostalCode_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "postalCode");
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

    public void setPostalCode_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setPostalCode_Count("postalCode", opLambda);
    }

    public void setPostalCode_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "postalCode");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalCode_Cardinality() {
        setPostalCode_Cardinality(null);
    }

    public void setPostalCode_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setPostalCode_Cardinality("postalCode", opLambda);
    }

    public void setPostalCode_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "postalCode");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalCode_Missing() {
        setPostalCode_Missing(null);
    }

    public void setPostalCode_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setPostalCode_Missing("postalCode", opLambda, null);
    }

    public void setPostalCode_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostalCode_Missing("postalCode", opLambda, aggsLambda);
    }

    public void setPostalCode_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "postalCode");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String physicalDeliveryOfficeName

    public void setPhysicalDeliveryOfficeName_Terms() {
        setPhysicalDeliveryOfficeName_Terms(null);
    }

    public void setPhysicalDeliveryOfficeName_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setPhysicalDeliveryOfficeName_Terms("physicalDeliveryOfficeName", opLambda, null);
    }

    public void setPhysicalDeliveryOfficeName_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPhysicalDeliveryOfficeName_Terms("physicalDeliveryOfficeName", opLambda, aggsLambda);
    }

    public void setPhysicalDeliveryOfficeName_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "physicalDeliveryOfficeName");
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

    public void setPhysicalDeliveryOfficeName_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setPhysicalDeliveryOfficeName_SignificantTerms("physicalDeliveryOfficeName", opLambda, null);
    }

    public void setPhysicalDeliveryOfficeName_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setPhysicalDeliveryOfficeName_SignificantTerms("physicalDeliveryOfficeName", opLambda, aggsLambda);
    }

    public void setPhysicalDeliveryOfficeName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "physicalDeliveryOfficeName");
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

    public void setPhysicalDeliveryOfficeName_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setPhysicalDeliveryOfficeName_IpRange("physicalDeliveryOfficeName", opLambda, null);
    }

    public void setPhysicalDeliveryOfficeName_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPhysicalDeliveryOfficeName_IpRange("physicalDeliveryOfficeName", opLambda, aggsLambda);
    }

    public void setPhysicalDeliveryOfficeName_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "physicalDeliveryOfficeName");
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

    public void setPhysicalDeliveryOfficeName_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setPhysicalDeliveryOfficeName_Count("physicalDeliveryOfficeName", opLambda);
    }

    public void setPhysicalDeliveryOfficeName_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "physicalDeliveryOfficeName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPhysicalDeliveryOfficeName_Cardinality() {
        setPhysicalDeliveryOfficeName_Cardinality(null);
    }

    public void setPhysicalDeliveryOfficeName_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setPhysicalDeliveryOfficeName_Cardinality("physicalDeliveryOfficeName", opLambda);
    }

    public void setPhysicalDeliveryOfficeName_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "physicalDeliveryOfficeName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPhysicalDeliveryOfficeName_Missing() {
        setPhysicalDeliveryOfficeName_Missing(null);
    }

    public void setPhysicalDeliveryOfficeName_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setPhysicalDeliveryOfficeName_Missing("physicalDeliveryOfficeName", opLambda, null);
    }

    public void setPhysicalDeliveryOfficeName_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPhysicalDeliveryOfficeName_Missing("physicalDeliveryOfficeName", opLambda, aggsLambda);
    }

    public void setPhysicalDeliveryOfficeName_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "physicalDeliveryOfficeName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String destinationIndicator

    public void setDestinationIndicator_Terms() {
        setDestinationIndicator_Terms(null);
    }

    public void setDestinationIndicator_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setDestinationIndicator_Terms("destinationIndicator", opLambda, null);
    }

    public void setDestinationIndicator_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDestinationIndicator_Terms("destinationIndicator", opLambda, aggsLambda);
    }

    public void setDestinationIndicator_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "destinationIndicator");
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

    public void setDestinationIndicator_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setDestinationIndicator_SignificantTerms("destinationIndicator", opLambda, null);
    }

    public void setDestinationIndicator_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setDestinationIndicator_SignificantTerms("destinationIndicator", opLambda, aggsLambda);
    }

    public void setDestinationIndicator_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "destinationIndicator");
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

    public void setDestinationIndicator_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setDestinationIndicator_IpRange("destinationIndicator", opLambda, null);
    }

    public void setDestinationIndicator_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDestinationIndicator_IpRange("destinationIndicator", opLambda, aggsLambda);
    }

    public void setDestinationIndicator_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "destinationIndicator");
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

    public void setDestinationIndicator_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setDestinationIndicator_Count("destinationIndicator", opLambda);
    }

    public void setDestinationIndicator_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "destinationIndicator");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDestinationIndicator_Cardinality() {
        setDestinationIndicator_Cardinality(null);
    }

    public void setDestinationIndicator_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setDestinationIndicator_Cardinality("destinationIndicator", opLambda);
    }

    public void setDestinationIndicator_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "destinationIndicator");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDestinationIndicator_Missing() {
        setDestinationIndicator_Missing(null);
    }

    public void setDestinationIndicator_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setDestinationIndicator_Missing("destinationIndicator", opLambda, null);
    }

    public void setDestinationIndicator_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDestinationIndicator_Missing("destinationIndicator", opLambda, aggsLambda);
    }

    public void setDestinationIndicator_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "destinationIndicator");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String internationaliSDNNumber

    public void setInternationaliSDNNumber_Terms() {
        setInternationaliSDNNumber_Terms(null);
    }

    public void setInternationaliSDNNumber_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setInternationaliSDNNumber_Terms("internationaliSDNNumber", opLambda, null);
    }

    public void setInternationaliSDNNumber_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setInternationaliSDNNumber_Terms("internationaliSDNNumber", opLambda, aggsLambda);
    }

    public void setInternationaliSDNNumber_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "internationaliSDNNumber");
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

    public void setInternationaliSDNNumber_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setInternationaliSDNNumber_SignificantTerms("internationaliSDNNumber", opLambda, null);
    }

    public void setInternationaliSDNNumber_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setInternationaliSDNNumber_SignificantTerms("internationaliSDNNumber", opLambda, aggsLambda);
    }

    public void setInternationaliSDNNumber_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "internationaliSDNNumber");
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

    public void setInternationaliSDNNumber_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setInternationaliSDNNumber_IpRange("internationaliSDNNumber", opLambda, null);
    }

    public void setInternationaliSDNNumber_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setInternationaliSDNNumber_IpRange("internationaliSDNNumber", opLambda, aggsLambda);
    }

    public void setInternationaliSDNNumber_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "internationaliSDNNumber");
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

    public void setInternationaliSDNNumber_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setInternationaliSDNNumber_Count("internationaliSDNNumber", opLambda);
    }

    public void setInternationaliSDNNumber_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "internationaliSDNNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInternationaliSDNNumber_Cardinality() {
        setInternationaliSDNNumber_Cardinality(null);
    }

    public void setInternationaliSDNNumber_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setInternationaliSDNNumber_Cardinality("internationaliSDNNumber", opLambda);
    }

    public void setInternationaliSDNNumber_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "internationaliSDNNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInternationaliSDNNumber_Missing() {
        setInternationaliSDNNumber_Missing(null);
    }

    public void setInternationaliSDNNumber_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setInternationaliSDNNumber_Missing("internationaliSDNNumber", opLambda, null);
    }

    public void setInternationaliSDNNumber_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setInternationaliSDNNumber_Missing("internationaliSDNNumber", opLambda, aggsLambda);
    }

    public void setInternationaliSDNNumber_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "internationaliSDNNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String state

    public void setState_Terms() {
        setState_Terms(null);
    }

    public void setState_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setState_Terms("state", opLambda, null);
    }

    public void setState_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setState_Terms("state", opLambda, aggsLambda);
    }

    public void setState_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "state");
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

    public void setState_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setState_SignificantTerms("state", opLambda, null);
    }

    public void setState_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setState_SignificantTerms("state", opLambda, aggsLambda);
    }

    public void setState_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "state");
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

    public void setState_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setState_IpRange("state", opLambda, null);
    }

    public void setState_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setState_IpRange("state", opLambda, aggsLambda);
    }

    public void setState_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "state");
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

    public void setState_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setState_Count("state", opLambda);
    }

    public void setState_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "state");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setState_Cardinality() {
        setState_Cardinality(null);
    }

    public void setState_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setState_Cardinality("state", opLambda);
    }

    public void setState_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "state");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setState_Missing() {
        setState_Missing(null);
    }

    public void setState_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setState_Missing("state", opLambda, null);
    }

    public void setState_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setState_Missing("state", opLambda, aggsLambda);
    }

    public void setState_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "state");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String employeeType

    public void setEmployeeType_Terms() {
        setEmployeeType_Terms(null);
    }

    public void setEmployeeType_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setEmployeeType_Terms("employeeType", opLambda, null);
    }

    public void setEmployeeType_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setEmployeeType_Terms("employeeType", opLambda, aggsLambda);
    }

    public void setEmployeeType_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "employeeType");
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

    public void setEmployeeType_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setEmployeeType_SignificantTerms("employeeType", opLambda, null);
    }

    public void setEmployeeType_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setEmployeeType_SignificantTerms("employeeType", opLambda, aggsLambda);
    }

    public void setEmployeeType_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "employeeType");
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

    public void setEmployeeType_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setEmployeeType_IpRange("employeeType", opLambda, null);
    }

    public void setEmployeeType_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setEmployeeType_IpRange("employeeType", opLambda, aggsLambda);
    }

    public void setEmployeeType_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "employeeType");
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

    public void setEmployeeType_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setEmployeeType_Count("employeeType", opLambda);
    }

    public void setEmployeeType_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "employeeType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeType_Cardinality() {
        setEmployeeType_Cardinality(null);
    }

    public void setEmployeeType_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setEmployeeType_Cardinality("employeeType", opLambda);
    }

    public void setEmployeeType_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "employeeType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setEmployeeType_Missing() {
        setEmployeeType_Missing(null);
    }

    public void setEmployeeType_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setEmployeeType_Missing("employeeType", opLambda, null);
    }

    public void setEmployeeType_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setEmployeeType_Missing("employeeType", opLambda, aggsLambda);
    }

    public void setEmployeeType_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "employeeType");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String facsimileTelephoneNumber

    public void setFacsimileTelephoneNumber_Terms() {
        setFacsimileTelephoneNumber_Terms(null);
    }

    public void setFacsimileTelephoneNumber_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setFacsimileTelephoneNumber_Terms("facsimileTelephoneNumber", opLambda, null);
    }

    public void setFacsimileTelephoneNumber_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setFacsimileTelephoneNumber_Terms("facsimileTelephoneNumber", opLambda, aggsLambda);
    }

    public void setFacsimileTelephoneNumber_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "facsimileTelephoneNumber");
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

    public void setFacsimileTelephoneNumber_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setFacsimileTelephoneNumber_SignificantTerms("facsimileTelephoneNumber", opLambda, null);
    }

    public void setFacsimileTelephoneNumber_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setFacsimileTelephoneNumber_SignificantTerms("facsimileTelephoneNumber", opLambda, aggsLambda);
    }

    public void setFacsimileTelephoneNumber_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "facsimileTelephoneNumber");
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

    public void setFacsimileTelephoneNumber_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setFacsimileTelephoneNumber_IpRange("facsimileTelephoneNumber", opLambda, null);
    }

    public void setFacsimileTelephoneNumber_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setFacsimileTelephoneNumber_IpRange("facsimileTelephoneNumber", opLambda, aggsLambda);
    }

    public void setFacsimileTelephoneNumber_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "facsimileTelephoneNumber");
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

    public void setFacsimileTelephoneNumber_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setFacsimileTelephoneNumber_Count("facsimileTelephoneNumber", opLambda);
    }

    public void setFacsimileTelephoneNumber_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "facsimileTelephoneNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFacsimileTelephoneNumber_Cardinality() {
        setFacsimileTelephoneNumber_Cardinality(null);
    }

    public void setFacsimileTelephoneNumber_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setFacsimileTelephoneNumber_Cardinality("facsimileTelephoneNumber", opLambda);
    }

    public void setFacsimileTelephoneNumber_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "facsimileTelephoneNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setFacsimileTelephoneNumber_Missing() {
        setFacsimileTelephoneNumber_Missing(null);
    }

    public void setFacsimileTelephoneNumber_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setFacsimileTelephoneNumber_Missing("facsimileTelephoneNumber", opLambda, null);
    }

    public void setFacsimileTelephoneNumber_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setFacsimileTelephoneNumber_Missing("facsimileTelephoneNumber", opLambda, aggsLambda);
    }

    public void setFacsimileTelephoneNumber_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "facsimileTelephoneNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String postOfficeBox

    public void setPostOfficeBox_Terms() {
        setPostOfficeBox_Terms(null);
    }

    public void setPostOfficeBox_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setPostOfficeBox_Terms("postOfficeBox", opLambda, null);
    }

    public void setPostOfficeBox_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostOfficeBox_Terms("postOfficeBox", opLambda, aggsLambda);
    }

    public void setPostOfficeBox_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "postOfficeBox");
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

    public void setPostOfficeBox_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setPostOfficeBox_SignificantTerms("postOfficeBox", opLambda, null);
    }

    public void setPostOfficeBox_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostOfficeBox_SignificantTerms("postOfficeBox", opLambda, aggsLambda);
    }

    public void setPostOfficeBox_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "postOfficeBox");
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

    public void setPostOfficeBox_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setPostOfficeBox_IpRange("postOfficeBox", opLambda, null);
    }

    public void setPostOfficeBox_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostOfficeBox_IpRange("postOfficeBox", opLambda, aggsLambda);
    }

    public void setPostOfficeBox_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "postOfficeBox");
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

    public void setPostOfficeBox_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setPostOfficeBox_Count("postOfficeBox", opLambda);
    }

    public void setPostOfficeBox_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "postOfficeBox");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostOfficeBox_Cardinality() {
        setPostOfficeBox_Cardinality(null);
    }

    public void setPostOfficeBox_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setPostOfficeBox_Cardinality("postOfficeBox", opLambda);
    }

    public void setPostOfficeBox_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "postOfficeBox");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostOfficeBox_Missing() {
        setPostOfficeBox_Missing(null);
    }

    public void setPostOfficeBox_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setPostOfficeBox_Missing("postOfficeBox", opLambda, null);
    }

    public void setPostOfficeBox_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostOfficeBox_Missing("postOfficeBox", opLambda, aggsLambda);
    }

    public void setPostOfficeBox_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "postOfficeBox");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String initials

    public void setInitials_Terms() {
        setInitials_Terms(null);
    }

    public void setInitials_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setInitials_Terms("initials", opLambda, null);
    }

    public void setInitials_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setInitials_Terms("initials", opLambda, aggsLambda);
    }

    public void setInitials_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "initials");
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

    public void setInitials_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setInitials_SignificantTerms("initials", opLambda, null);
    }

    public void setInitials_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setInitials_SignificantTerms("initials", opLambda, aggsLambda);
    }

    public void setInitials_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "initials");
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

    public void setInitials_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setInitials_IpRange("initials", opLambda, null);
    }

    public void setInitials_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setInitials_IpRange("initials", opLambda, aggsLambda);
    }

    public void setInitials_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "initials");
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

    public void setInitials_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setInitials_Count("initials", opLambda);
    }

    public void setInitials_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "initials");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInitials_Cardinality() {
        setInitials_Cardinality(null);
    }

    public void setInitials_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setInitials_Cardinality("initials", opLambda);
    }

    public void setInitials_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "initials");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setInitials_Missing() {
        setInitials_Missing(null);
    }

    public void setInitials_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setInitials_Missing("initials", opLambda, null);
    }

    public void setInitials_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setInitials_Missing("initials", opLambda, aggsLambda);
    }

    public void setInitials_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "initials");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String carLicense

    public void setCarLicense_Terms() {
        setCarLicense_Terms(null);
    }

    public void setCarLicense_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setCarLicense_Terms("carLicense", opLambda, null);
    }

    public void setCarLicense_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setCarLicense_Terms("carLicense", opLambda, aggsLambda);
    }

    public void setCarLicense_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "carLicense");
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

    public void setCarLicense_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setCarLicense_SignificantTerms("carLicense", opLambda, null);
    }

    public void setCarLicense_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setCarLicense_SignificantTerms("carLicense", opLambda, aggsLambda);
    }

    public void setCarLicense_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "carLicense");
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

    public void setCarLicense_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setCarLicense_IpRange("carLicense", opLambda, null);
    }

    public void setCarLicense_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setCarLicense_IpRange("carLicense", opLambda, aggsLambda);
    }

    public void setCarLicense_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "carLicense");
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

    public void setCarLicense_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setCarLicense_Count("carLicense", opLambda);
    }

    public void setCarLicense_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "carLicense");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCarLicense_Cardinality() {
        setCarLicense_Cardinality(null);
    }

    public void setCarLicense_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setCarLicense_Cardinality("carLicense", opLambda);
    }

    public void setCarLicense_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "carLicense");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCarLicense_Missing() {
        setCarLicense_Missing(null);
    }

    public void setCarLicense_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setCarLicense_Missing("carLicense", opLambda, null);
    }

    public void setCarLicense_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setCarLicense_Missing("carLicense", opLambda, aggsLambda);
    }

    public void setCarLicense_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "carLicense");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String mobile

    public void setMobile_Terms() {
        setMobile_Terms(null);
    }

    public void setMobile_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setMobile_Terms("mobile", opLambda, null);
    }

    public void setMobile_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setMobile_Terms("mobile", opLambda, aggsLambda);
    }

    public void setMobile_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "mobile");
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

    public void setMobile_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setMobile_SignificantTerms("mobile", opLambda, null);
    }

    public void setMobile_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setMobile_SignificantTerms("mobile", opLambda, aggsLambda);
    }

    public void setMobile_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "mobile");
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

    public void setMobile_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setMobile_IpRange("mobile", opLambda, null);
    }

    public void setMobile_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setMobile_IpRange("mobile", opLambda, aggsLambda);
    }

    public void setMobile_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "mobile");
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

    public void setMobile_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setMobile_Count("mobile", opLambda);
    }

    public void setMobile_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "mobile");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMobile_Cardinality() {
        setMobile_Cardinality(null);
    }

    public void setMobile_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setMobile_Cardinality("mobile", opLambda);
    }

    public void setMobile_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "mobile");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setMobile_Missing() {
        setMobile_Missing(null);
    }

    public void setMobile_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setMobile_Missing("mobile", opLambda, null);
    }

    public void setMobile_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setMobile_Missing("mobile", opLambda, aggsLambda);
    }

    public void setMobile_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "mobile");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String postalAddress

    public void setPostalAddress_Terms() {
        setPostalAddress_Terms(null);
    }

    public void setPostalAddress_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setPostalAddress_Terms("postalAddress", opLambda, null);
    }

    public void setPostalAddress_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostalAddress_Terms("postalAddress", opLambda, aggsLambda);
    }

    public void setPostalAddress_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "postalAddress");
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

    public void setPostalAddress_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setPostalAddress_SignificantTerms("postalAddress", opLambda, null);
    }

    public void setPostalAddress_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostalAddress_SignificantTerms("postalAddress", opLambda, aggsLambda);
    }

    public void setPostalAddress_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "postalAddress");
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

    public void setPostalAddress_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setPostalAddress_IpRange("postalAddress", opLambda, null);
    }

    public void setPostalAddress_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostalAddress_IpRange("postalAddress", opLambda, aggsLambda);
    }

    public void setPostalAddress_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "postalAddress");
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

    public void setPostalAddress_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setPostalAddress_Count("postalAddress", opLambda);
    }

    public void setPostalAddress_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "postalAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalAddress_Cardinality() {
        setPostalAddress_Cardinality(null);
    }

    public void setPostalAddress_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setPostalAddress_Cardinality("postalAddress", opLambda);
    }

    public void setPostalAddress_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "postalAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPostalAddress_Missing() {
        setPostalAddress_Missing(null);
    }

    public void setPostalAddress_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setPostalAddress_Missing("postalAddress", opLambda, null);
    }

    public void setPostalAddress_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPostalAddress_Missing("postalAddress", opLambda, aggsLambda);
    }

    public void setPostalAddress_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "postalAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String city

    public void setCity_Terms() {
        setCity_Terms(null);
    }

    public void setCity_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setCity_Terms("city", opLambda, null);
    }

    public void setCity_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setCity_Terms("city", opLambda, aggsLambda);
    }

    public void setCity_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "city");
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

    public void setCity_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setCity_SignificantTerms("city", opLambda, null);
    }

    public void setCity_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setCity_SignificantTerms("city", opLambda, aggsLambda);
    }

    public void setCity_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "city");
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

    public void setCity_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setCity_IpRange("city", opLambda, null);
    }

    public void setCity_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setCity_IpRange("city", opLambda, aggsLambda);
    }

    public void setCity_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "city");
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

    public void setCity_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setCity_Count("city", opLambda);
    }

    public void setCity_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "city");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCity_Cardinality() {
        setCity_Cardinality(null);
    }

    public void setCity_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setCity_Cardinality("city", opLambda);
    }

    public void setCity_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "city");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCity_Missing() {
        setCity_Missing(null);
    }

    public void setCity_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setCity_Missing("city", opLambda, null);
    }

    public void setCity_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setCity_Missing("city", opLambda, aggsLambda);
    }

    public void setCity_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "city");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String teletexTerminalIdentifier

    public void setTeletexTerminalIdentifier_Terms() {
        setTeletexTerminalIdentifier_Terms(null);
    }

    public void setTeletexTerminalIdentifier_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setTeletexTerminalIdentifier_Terms("teletexTerminalIdentifier", opLambda, null);
    }

    public void setTeletexTerminalIdentifier_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setTeletexTerminalIdentifier_Terms("teletexTerminalIdentifier", opLambda, aggsLambda);
    }

    public void setTeletexTerminalIdentifier_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "teletexTerminalIdentifier");
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

    public void setTeletexTerminalIdentifier_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setTeletexTerminalIdentifier_SignificantTerms("teletexTerminalIdentifier", opLambda, null);
    }

    public void setTeletexTerminalIdentifier_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setTeletexTerminalIdentifier_SignificantTerms("teletexTerminalIdentifier", opLambda, aggsLambda);
    }

    public void setTeletexTerminalIdentifier_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "teletexTerminalIdentifier");
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

    public void setTeletexTerminalIdentifier_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setTeletexTerminalIdentifier_IpRange("teletexTerminalIdentifier", opLambda, null);
    }

    public void setTeletexTerminalIdentifier_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setTeletexTerminalIdentifier_IpRange("teletexTerminalIdentifier", opLambda, aggsLambda);
    }

    public void setTeletexTerminalIdentifier_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "teletexTerminalIdentifier");
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

    public void setTeletexTerminalIdentifier_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setTeletexTerminalIdentifier_Count("teletexTerminalIdentifier", opLambda);
    }

    public void setTeletexTerminalIdentifier_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "teletexTerminalIdentifier");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTeletexTerminalIdentifier_Cardinality() {
        setTeletexTerminalIdentifier_Cardinality(null);
    }

    public void setTeletexTerminalIdentifier_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setTeletexTerminalIdentifier_Cardinality("teletexTerminalIdentifier", opLambda);
    }

    public void setTeletexTerminalIdentifier_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "teletexTerminalIdentifier");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setTeletexTerminalIdentifier_Missing() {
        setTeletexTerminalIdentifier_Missing(null);
    }

    public void setTeletexTerminalIdentifier_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setTeletexTerminalIdentifier_Missing("teletexTerminalIdentifier", opLambda, null);
    }

    public void setTeletexTerminalIdentifier_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setTeletexTerminalIdentifier_Missing("teletexTerminalIdentifier", opLambda, aggsLambda);
    }

    public void setTeletexTerminalIdentifier_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "teletexTerminalIdentifier");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String x121Address

    public void setX121Address_Terms() {
        setX121Address_Terms(null);
    }

    public void setX121Address_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setX121Address_Terms("x121Address", opLambda, null);
    }

    public void setX121Address_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setX121Address_Terms("x121Address", opLambda, aggsLambda);
    }

    public void setX121Address_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "x121Address");
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

    public void setX121Address_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setX121Address_SignificantTerms("x121Address", opLambda, null);
    }

    public void setX121Address_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setX121Address_SignificantTerms("x121Address", opLambda, aggsLambda);
    }

    public void setX121Address_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "x121Address");
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

    public void setX121Address_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setX121Address_IpRange("x121Address", opLambda, null);
    }

    public void setX121Address_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setX121Address_IpRange("x121Address", opLambda, aggsLambda);
    }

    public void setX121Address_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "x121Address");
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

    public void setX121Address_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setX121Address_Count("x121Address", opLambda);
    }

    public void setX121Address_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "x121Address");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setX121Address_Cardinality() {
        setX121Address_Cardinality(null);
    }

    public void setX121Address_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setX121Address_Cardinality("x121Address", opLambda);
    }

    public void setX121Address_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "x121Address");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setX121Address_Missing() {
        setX121Address_Missing(null);
    }

    public void setX121Address_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setX121Address_Missing("x121Address", opLambda, null);
    }

    public void setX121Address_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setX121Address_Missing("x121Address", opLambda, aggsLambda);
    }

    public void setX121Address_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "x121Address");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String businessCategory

    public void setBusinessCategory_Terms() {
        setBusinessCategory_Terms(null);
    }

    public void setBusinessCategory_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setBusinessCategory_Terms("businessCategory", opLambda, null);
    }

    public void setBusinessCategory_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setBusinessCategory_Terms("businessCategory", opLambda, aggsLambda);
    }

    public void setBusinessCategory_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "businessCategory");
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

    public void setBusinessCategory_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setBusinessCategory_SignificantTerms("businessCategory", opLambda, null);
    }

    public void setBusinessCategory_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setBusinessCategory_SignificantTerms("businessCategory", opLambda, aggsLambda);
    }

    public void setBusinessCategory_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "businessCategory");
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

    public void setBusinessCategory_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setBusinessCategory_IpRange("businessCategory", opLambda, null);
    }

    public void setBusinessCategory_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setBusinessCategory_IpRange("businessCategory", opLambda, aggsLambda);
    }

    public void setBusinessCategory_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "businessCategory");
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

    public void setBusinessCategory_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setBusinessCategory_Count("businessCategory", opLambda);
    }

    public void setBusinessCategory_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "businessCategory");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBusinessCategory_Cardinality() {
        setBusinessCategory_Cardinality(null);
    }

    public void setBusinessCategory_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setBusinessCategory_Cardinality("businessCategory", opLambda);
    }

    public void setBusinessCategory_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "businessCategory");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setBusinessCategory_Missing() {
        setBusinessCategory_Missing(null);
    }

    public void setBusinessCategory_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setBusinessCategory_Missing("businessCategory", opLambda, null);
    }

    public void setBusinessCategory_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setBusinessCategory_Missing("businessCategory", opLambda, aggsLambda);
    }

    public void setBusinessCategory_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "businessCategory");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String registeredAddress

    public void setRegisteredAddress_Terms() {
        setRegisteredAddress_Terms(null);
    }

    public void setRegisteredAddress_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setRegisteredAddress_Terms("registeredAddress", opLambda, null);
    }

    public void setRegisteredAddress_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRegisteredAddress_Terms("registeredAddress", opLambda, aggsLambda);
    }

    public void setRegisteredAddress_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "registeredAddress");
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

    public void setRegisteredAddress_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setRegisteredAddress_SignificantTerms("registeredAddress", opLambda, null);
    }

    public void setRegisteredAddress_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setRegisteredAddress_SignificantTerms("registeredAddress", opLambda, aggsLambda);
    }

    public void setRegisteredAddress_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "registeredAddress");
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

    public void setRegisteredAddress_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setRegisteredAddress_IpRange("registeredAddress", opLambda, null);
    }

    public void setRegisteredAddress_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRegisteredAddress_IpRange("registeredAddress", opLambda, aggsLambda);
    }

    public void setRegisteredAddress_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "registeredAddress");
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

    public void setRegisteredAddress_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setRegisteredAddress_Count("registeredAddress", opLambda);
    }

    public void setRegisteredAddress_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "registeredAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegisteredAddress_Cardinality() {
        setRegisteredAddress_Cardinality(null);
    }

    public void setRegisteredAddress_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setRegisteredAddress_Cardinality("registeredAddress", opLambda);
    }

    public void setRegisteredAddress_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "registeredAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setRegisteredAddress_Missing() {
        setRegisteredAddress_Missing(null);
    }

    public void setRegisteredAddress_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setRegisteredAddress_Missing("registeredAddress", opLambda, null);
    }

    public void setRegisteredAddress_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRegisteredAddress_Missing("registeredAddress", opLambda, aggsLambda);
    }

    public void setRegisteredAddress_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "registeredAddress");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String displayName

    public void setDisplayName_Terms() {
        setDisplayName_Terms(null);
    }

    public void setDisplayName_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setDisplayName_Terms("displayName", opLambda, null);
    }

    public void setDisplayName_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDisplayName_Terms("displayName", opLambda, aggsLambda);
    }

    public void setDisplayName_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "displayName");
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

    public void setDisplayName_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setDisplayName_SignificantTerms("displayName", opLambda, null);
    }

    public void setDisplayName_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDisplayName_SignificantTerms("displayName", opLambda, aggsLambda);
    }

    public void setDisplayName_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "displayName");
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

    public void setDisplayName_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setDisplayName_IpRange("displayName", opLambda, null);
    }

    public void setDisplayName_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDisplayName_IpRange("displayName", opLambda, aggsLambda);
    }

    public void setDisplayName_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "displayName");
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

    public void setDisplayName_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setDisplayName_Count("displayName", opLambda);
    }

    public void setDisplayName_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "displayName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDisplayName_Cardinality() {
        setDisplayName_Cardinality(null);
    }

    public void setDisplayName_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setDisplayName_Cardinality("displayName", opLambda);
    }

    public void setDisplayName_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "displayName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDisplayName_Missing() {
        setDisplayName_Missing(null);
    }

    public void setDisplayName_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setDisplayName_Missing("displayName", opLambda, null);
    }

    public void setDisplayName_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDisplayName_Missing("displayName", opLambda, aggsLambda);
    }

    public void setDisplayName_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "displayName");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String preferredLanguage

    public void setPreferredLanguage_Terms() {
        setPreferredLanguage_Terms(null);
    }

    public void setPreferredLanguage_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setPreferredLanguage_Terms("preferredLanguage", opLambda, null);
    }

    public void setPreferredLanguage_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPreferredLanguage_Terms("preferredLanguage", opLambda, aggsLambda);
    }

    public void setPreferredLanguage_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "preferredLanguage");
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

    public void setPreferredLanguage_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setPreferredLanguage_SignificantTerms("preferredLanguage", opLambda, null);
    }

    public void setPreferredLanguage_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setPreferredLanguage_SignificantTerms("preferredLanguage", opLambda, aggsLambda);
    }

    public void setPreferredLanguage_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "preferredLanguage");
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

    public void setPreferredLanguage_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setPreferredLanguage_IpRange("preferredLanguage", opLambda, null);
    }

    public void setPreferredLanguage_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPreferredLanguage_IpRange("preferredLanguage", opLambda, aggsLambda);
    }

    public void setPreferredLanguage_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "preferredLanguage");
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

    public void setPreferredLanguage_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setPreferredLanguage_Count("preferredLanguage", opLambda);
    }

    public void setPreferredLanguage_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "preferredLanguage");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPreferredLanguage_Cardinality() {
        setPreferredLanguage_Cardinality(null);
    }

    public void setPreferredLanguage_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setPreferredLanguage_Cardinality("preferredLanguage", opLambda);
    }

    public void setPreferredLanguage_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "preferredLanguage");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setPreferredLanguage_Missing() {
        setPreferredLanguage_Missing(null);
    }

    public void setPreferredLanguage_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setPreferredLanguage_Missing("preferredLanguage", opLambda, null);
    }

    public void setPreferredLanguage_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setPreferredLanguage_Missing("preferredLanguage", opLambda, aggsLambda);
    }

    public void setPreferredLanguage_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "preferredLanguage");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String departmentNumber

    public void setDepartmentNumber_Terms() {
        setDepartmentNumber_Terms(null);
    }

    public void setDepartmentNumber_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setDepartmentNumber_Terms("departmentNumber", opLambda, null);
    }

    public void setDepartmentNumber_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDepartmentNumber_Terms("departmentNumber", opLambda, aggsLambda);
    }

    public void setDepartmentNumber_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "departmentNumber");
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

    public void setDepartmentNumber_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setDepartmentNumber_SignificantTerms("departmentNumber", opLambda, null);
    }

    public void setDepartmentNumber_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        setDepartmentNumber_SignificantTerms("departmentNumber", opLambda, aggsLambda);
    }

    public void setDepartmentNumber_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "departmentNumber");
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

    public void setDepartmentNumber_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setDepartmentNumber_IpRange("departmentNumber", opLambda, null);
    }

    public void setDepartmentNumber_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDepartmentNumber_IpRange("departmentNumber", opLambda, aggsLambda);
    }

    public void setDepartmentNumber_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "departmentNumber");
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

    public void setDepartmentNumber_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setDepartmentNumber_Count("departmentNumber", opLambda);
    }

    public void setDepartmentNumber_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "departmentNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepartmentNumber_Cardinality() {
        setDepartmentNumber_Cardinality(null);
    }

    public void setDepartmentNumber_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setDepartmentNumber_Cardinality("departmentNumber", opLambda);
    }

    public void setDepartmentNumber_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "departmentNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setDepartmentNumber_Missing() {
        setDepartmentNumber_Missing(null);
    }

    public void setDepartmentNumber_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setDepartmentNumber_Missing("departmentNumber", opLambda, null);
    }

    public void setDepartmentNumber_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setDepartmentNumber_Missing("departmentNumber", opLambda, aggsLambda);
    }

    public void setDepartmentNumber_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "departmentNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Long uidNumber
    public void setUidNumber_Avg() {
        setUidNumber_Avg(null);
    }

    public void setUidNumber_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setUidNumber_Avg("uidNumber", opLambda);
    }

    public void setUidNumber_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_Max() {
        setUidNumber_Max(null);
    }

    public void setUidNumber_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setUidNumber_Max("uidNumber", opLambda);
    }

    public void setUidNumber_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_Min() {
        setUidNumber_Min(null);
    }

    public void setUidNumber_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setUidNumber_Min("uidNumber", opLambda);
    }

    public void setUidNumber_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_Sum() {
        setUidNumber_Sum(null);
    }

    public void setUidNumber_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setUidNumber_Sum("uidNumber", opLambda);
    }

    public void setUidNumber_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_ExtendedStats() {
        setUidNumber_ExtendedStats(null);
    }

    public void setUidNumber_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setUidNumber_ExtendedStats("uidNumber", opLambda);
    }

    public void setUidNumber_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_Stats() {
        setUidNumber_Stats(null);
    }

    public void setUidNumber_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setUidNumber_Stats("uidNumber", opLambda);
    }

    public void setUidNumber_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_Percentiles() {
        setUidNumber_Percentiles(null);
    }

    public void setUidNumber_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setUidNumber_Percentiles("uidNumber", opLambda);
    }

    public void setUidNumber_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_PercentileRanks() {
        setUidNumber_PercentileRanks(null);
    }

    public void setUidNumber_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setUidNumber_PercentileRanks("uidNumber", opLambda);
    }

    public void setUidNumber_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_Histogram() {
        setUidNumber_Histogram(null);
    }

    public void setUidNumber_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setUidNumber_Histogram("uidNumber", opLambda, null);
    }

    public void setUidNumber_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setUidNumber_Histogram("uidNumber", opLambda, aggsLambda);
    }

    public void setUidNumber_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "uidNumber");
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

    public void setUidNumber_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setUidNumber_Range("uidNumber", opLambda, null);
    }

    public void setUidNumber_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setUidNumber_Range("uidNumber", opLambda, aggsLambda);
    }

    public void setUidNumber_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "uidNumber");
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

    public void setUidNumber_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setUidNumber_Count("uidNumber", opLambda);
    }

    public void setUidNumber_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_Cardinality() {
        setUidNumber_Cardinality(null);
    }

    public void setUidNumber_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setUidNumber_Cardinality("uidNumber", opLambda);
    }

    public void setUidNumber_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUidNumber_Missing() {
        setUidNumber_Missing(null);
    }

    public void setUidNumber_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setUidNumber_Missing("uidNumber", opLambda, null);
    }

    public void setUidNumber_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setUidNumber_Missing("uidNumber", opLambda, aggsLambda);
    }

    public void setUidNumber_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "uidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // Long gidNumber
    public void setGidNumber_Avg() {
        setGidNumber_Avg(null);
    }

    public void setGidNumber_Avg(ConditionOptionCall<AvgBuilder> opLambda) {
        setGidNumber_Avg("gidNumber", opLambda);
    }

    public void setGidNumber_Avg(String name, ConditionOptionCall<AvgBuilder> opLambda) {
        AvgBuilder builder = regAvgA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Max() {
        setGidNumber_Max(null);
    }

    public void setGidNumber_Max(ConditionOptionCall<MaxBuilder> opLambda) {
        setGidNumber_Max("gidNumber", opLambda);
    }

    public void setGidNumber_Max(String name, ConditionOptionCall<MaxBuilder> opLambda) {
        MaxBuilder builder = regMaxA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Min() {
        setGidNumber_Min(null);
    }

    public void setGidNumber_Min(ConditionOptionCall<MinBuilder> opLambda) {
        setGidNumber_Min("gidNumber", opLambda);
    }

    public void setGidNumber_Min(String name, ConditionOptionCall<MinBuilder> opLambda) {
        MinBuilder builder = regMinA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Sum() {
        setGidNumber_Sum(null);
    }

    public void setGidNumber_Sum(ConditionOptionCall<SumBuilder> opLambda) {
        setGidNumber_Sum("gidNumber", opLambda);
    }

    public void setGidNumber_Sum(String name, ConditionOptionCall<SumBuilder> opLambda) {
        SumBuilder builder = regSumA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_ExtendedStats() {
        setGidNumber_ExtendedStats(null);
    }

    public void setGidNumber_ExtendedStats(ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        setGidNumber_ExtendedStats("gidNumber", opLambda);
    }

    public void setGidNumber_ExtendedStats(String name, ConditionOptionCall<ExtendedStatsBuilder> opLambda) {
        ExtendedStatsBuilder builder = regExtendedStatsA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Stats() {
        setGidNumber_Stats(null);
    }

    public void setGidNumber_Stats(ConditionOptionCall<StatsBuilder> opLambda) {
        setGidNumber_Stats("gidNumber", opLambda);
    }

    public void setGidNumber_Stats(String name, ConditionOptionCall<StatsBuilder> opLambda) {
        StatsBuilder builder = regStatsA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Percentiles() {
        setGidNumber_Percentiles(null);
    }

    public void setGidNumber_Percentiles(ConditionOptionCall<PercentilesBuilder> opLambda) {
        setGidNumber_Percentiles("gidNumber", opLambda);
    }

    public void setGidNumber_Percentiles(String name, ConditionOptionCall<PercentilesBuilder> opLambda) {
        PercentilesBuilder builder = regPercentilesA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_PercentileRanks() {
        setGidNumber_PercentileRanks(null);
    }

    public void setGidNumber_PercentileRanks(ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        setGidNumber_PercentileRanks("gidNumber", opLambda);
    }

    public void setGidNumber_PercentileRanks(String name, ConditionOptionCall<PercentileRanksBuilder> opLambda) {
        PercentileRanksBuilder builder = regPercentileRanksA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Histogram() {
        setGidNumber_Histogram(null);
    }

    public void setGidNumber_Histogram(ConditionOptionCall<HistogramBuilder> opLambda) {
        setGidNumber_Histogram("gidNumber", opLambda, null);
    }

    public void setGidNumber_Histogram(ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGidNumber_Histogram("gidNumber", opLambda, aggsLambda);
    }

    public void setGidNumber_Histogram(String name, ConditionOptionCall<HistogramBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        HistogramBuilder builder = regHistogramA(name, "gidNumber");
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

    public void setGidNumber_Range(ConditionOptionCall<RangeBuilder> opLambda) {
        setGidNumber_Range("gidNumber", opLambda, null);
    }

    public void setGidNumber_Range(ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGidNumber_Range("gidNumber", opLambda, aggsLambda);
    }

    public void setGidNumber_Range(String name, ConditionOptionCall<RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        RangeBuilder builder = regRangeA(name, "gidNumber");
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

    public void setGidNumber_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setGidNumber_Count("gidNumber", opLambda);
    }

    public void setGidNumber_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Cardinality() {
        setGidNumber_Cardinality(null);
    }

    public void setGidNumber_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setGidNumber_Cardinality("gidNumber", opLambda);
    }

    public void setGidNumber_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGidNumber_Missing() {
        setGidNumber_Missing(null);
    }

    public void setGidNumber_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setGidNumber_Missing("gidNumber", opLambda, null);
    }

    public void setGidNumber_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGidNumber_Missing("gidNumber", opLambda, aggsLambda);
    }

    public void setGidNumber_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "gidNumber");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String homeDirectory

    public void setHomeDirectory_Terms() {
        setHomeDirectory_Terms(null);
    }

    public void setHomeDirectory_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setHomeDirectory_Terms("homeDirectory", opLambda, null);
    }

    public void setHomeDirectory_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomeDirectory_Terms("homeDirectory", opLambda, aggsLambda);
    }

    public void setHomeDirectory_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "homeDirectory");
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

    public void setHomeDirectory_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setHomeDirectory_SignificantTerms("homeDirectory", opLambda, null);
    }

    public void setHomeDirectory_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomeDirectory_SignificantTerms("homeDirectory", opLambda, aggsLambda);
    }

    public void setHomeDirectory_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "homeDirectory");
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

    public void setHomeDirectory_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setHomeDirectory_IpRange("homeDirectory", opLambda, null);
    }

    public void setHomeDirectory_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomeDirectory_IpRange("homeDirectory", opLambda, aggsLambda);
    }

    public void setHomeDirectory_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "homeDirectory");
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

    public void setHomeDirectory_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setHomeDirectory_Count("homeDirectory", opLambda);
    }

    public void setHomeDirectory_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "homeDirectory");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomeDirectory_Cardinality() {
        setHomeDirectory_Cardinality(null);
    }

    public void setHomeDirectory_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setHomeDirectory_Cardinality("homeDirectory", opLambda);
    }

    public void setHomeDirectory_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "homeDirectory");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setHomeDirectory_Missing() {
        setHomeDirectory_Missing(null);
    }

    public void setHomeDirectory_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setHomeDirectory_Missing("homeDirectory", opLambda, null);
    }

    public void setHomeDirectory_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setHomeDirectory_Missing("homeDirectory", opLambda, aggsLambda);
    }

    public void setHomeDirectory_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "homeDirectory");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // String groups

    public void setGroups_Terms() {
        setGroups_Terms(null);
    }

    public void setGroups_Terms(ConditionOptionCall<TermsBuilder> opLambda) {
        setGroups_Terms("groups", opLambda, null);
    }

    public void setGroups_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGroups_Terms("groups", opLambda, aggsLambda);
    }

    public void setGroups_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "groups");
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

    public void setGroups_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setGroups_SignificantTerms("groups", opLambda, null);
    }

    public void setGroups_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGroups_SignificantTerms("groups", opLambda, aggsLambda);
    }

    public void setGroups_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "groups");
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

    public void setGroups_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setGroups_IpRange("groups", opLambda, null);
    }

    public void setGroups_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGroups_IpRange("groups", opLambda, aggsLambda);
    }

    public void setGroups_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "groups");
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

    public void setGroups_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setGroups_Count("groups", opLambda);
    }

    public void setGroups_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "groups");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroups_Cardinality() {
        setGroups_Cardinality(null);
    }

    public void setGroups_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setGroups_Cardinality("groups", opLambda);
    }

    public void setGroups_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "groups");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setGroups_Missing() {
        setGroups_Missing(null);
    }

    public void setGroups_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setGroups_Missing("groups", opLambda, null);
    }

    public void setGroups_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setGroups_Missing("groups", opLambda, aggsLambda);
    }

    public void setGroups_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "groups");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserCA ca = new UserCA();
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

    public void setRoles_Terms(ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRoles_Terms("roles", opLambda, aggsLambda);
    }

    public void setRoles_Terms(String name, ConditionOptionCall<TermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        TermsBuilder builder = regTermsA(name, "roles");
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

    public void setRoles_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda) {
        setRoles_SignificantTerms("roles", opLambda, null);
    }

    public void setRoles_SignificantTerms(ConditionOptionCall<SignificantTermsBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRoles_SignificantTerms("roles", opLambda, aggsLambda);
    }

    public void setRoles_SignificantTerms(String name, ConditionOptionCall<SignificantTermsBuilder> opLambda,
            OperatorCall<BsUserCA> aggsLambda) {
        SignificantTermsBuilder builder = regSignificantTermsA(name, "roles");
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

    public void setRoles_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda) {
        setRoles_IpRange("roles", opLambda, null);
    }

    public void setRoles_IpRange(ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRoles_IpRange("roles", opLambda, aggsLambda);
    }

    public void setRoles_IpRange(String name, ConditionOptionCall<IPv4RangeBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        IPv4RangeBuilder builder = regIpRangeA(name, "roles");
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

    public void setRoles_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        setRoles_Missing("roles", opLambda, aggsLambda);
    }

    public void setRoles_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "roles");
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
