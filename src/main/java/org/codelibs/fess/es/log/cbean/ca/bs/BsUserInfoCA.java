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
package org.codelibs.fess.es.log.cbean.ca.bs;

import org.codelibs.fess.es.log.allcommon.EsAbstractConditionAggregation;
import org.codelibs.fess.es.log.allcommon.EsAbstractConditionQuery;
import org.codelibs.fess.es.log.cbean.ca.UserInfoCA;
import org.codelibs.fess.es.log.cbean.cq.UserInfoCQ;
import org.codelibs.fess.es.log.cbean.cq.bs.BsUserInfoCQ;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.global.GlobalBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.missing.MissingBuilder;
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeBuilder;
import org.elasticsearch.search.aggregations.bucket.sampler.SamplerAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityBuilder;
import org.elasticsearch.search.aggregations.metrics.scripted.ScriptedMetricBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHitsBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsUserInfoCA extends EsAbstractConditionAggregation {

    // ===================================================================================
    //                                                                     Aggregation Set
    //                                                                           =========

    public void filter(String name, EsAbstractConditionQuery.OperatorCall<BsUserInfoCQ> queryLambda,
            ConditionOptionCall<FilterAggregationBuilder> opLambda, OperatorCall<BsUserInfoCA> aggsLambda) {
        UserInfoCQ cq = new UserInfoCQ();
        if (queryLambda != null) {
            queryLambda.callback(cq);
        }
        FilterAggregationBuilder builder = regFilterA(name, cq.getQuery());
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserInfoCA ca = new UserInfoCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void global(String name, ConditionOptionCall<GlobalBuilder> opLambda, OperatorCall<BsUserInfoCA> aggsLambda) {
        GlobalBuilder builder = regGlobalA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserInfoCA ca = new UserInfoCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void sampler(String name, ConditionOptionCall<SamplerAggregationBuilder> opLambda, OperatorCall<BsUserInfoCA> aggsLambda) {
        SamplerAggregationBuilder builder = regSamplerA(name);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserInfoCA ca = new UserInfoCA();
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

    // LocalDateTime createdAt

    public void setCreatedAt_DateRange() {
        setCreatedAt_DateRange(null);
    }

    public void setCreatedAt_DateRange(ConditionOptionCall<DateRangeBuilder> opLambda) {
        setCreatedAt_DateRange("createdAt", opLambda, null);
    }

    public void setCreatedAt_DateRange(ConditionOptionCall<DateRangeBuilder> opLambda, OperatorCall<BsUserInfoCA> aggsLambda) {
        setCreatedAt_DateRange("createdAt", opLambda, aggsLambda);
    }

    public void setCreatedAt_DateRange(String name, ConditionOptionCall<DateRangeBuilder> opLambda, OperatorCall<BsUserInfoCA> aggsLambda) {
        DateRangeBuilder builder = regDateRangeA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserInfoCA ca = new UserInfoCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCreatedAt_DateHistogram() {
        setCreatedAt_DateHistogram(null);
    }

    public void setCreatedAt_DateHistogram(ConditionOptionCall<DateHistogramBuilder> opLambda) {
        setCreatedAt_DateHistogram("createdAt", opLambda, null);
    }

    public void setCreatedAt_DateHistogram(ConditionOptionCall<DateHistogramBuilder> opLambda, OperatorCall<BsUserInfoCA> aggsLambda) {
        setCreatedAt_DateHistogram("createdAt", opLambda, aggsLambda);
    }

    public void setCreatedAt_DateHistogram(String name, ConditionOptionCall<DateHistogramBuilder> opLambda,
            OperatorCall<BsUserInfoCA> aggsLambda) {
        DateHistogramBuilder builder = regDateHistogramA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserInfoCA ca = new UserInfoCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setCreatedAt_Count() {
        setCreatedAt_Count(null);
    }

    public void setCreatedAt_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setCreatedAt_Count("createdAt", opLambda);
    }

    public void setCreatedAt_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Cardinality() {
        setCreatedAt_Cardinality(null);
    }

    public void setCreatedAt_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setCreatedAt_Cardinality("createdAt", opLambda);
    }

    public void setCreatedAt_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setCreatedAt_Missing() {
        setCreatedAt_Missing(null);
    }

    public void setCreatedAt_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setCreatedAt_Missing("createdAt", opLambda, null);
    }

    public void setCreatedAt_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserInfoCA> aggsLambda) {
        setCreatedAt_Missing("createdAt", opLambda, aggsLambda);
    }

    public void setCreatedAt_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserInfoCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "createdAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserInfoCA ca = new UserInfoCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    // LocalDateTime updatedAt

    public void setUpdatedAt_DateRange() {
        setUpdatedAt_DateRange(null);
    }

    public void setUpdatedAt_DateRange(ConditionOptionCall<DateRangeBuilder> opLambda) {
        setUpdatedAt_DateRange("updatedAt", opLambda, null);
    }

    public void setUpdatedAt_DateRange(ConditionOptionCall<DateRangeBuilder> opLambda, OperatorCall<BsUserInfoCA> aggsLambda) {
        setUpdatedAt_DateRange("updatedAt", opLambda, aggsLambda);
    }

    public void setUpdatedAt_DateRange(String name, ConditionOptionCall<DateRangeBuilder> opLambda, OperatorCall<BsUserInfoCA> aggsLambda) {
        DateRangeBuilder builder = regDateRangeA(name, "updatedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserInfoCA ca = new UserInfoCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUpdatedAt_DateHistogram() {
        setUpdatedAt_DateHistogram(null);
    }

    public void setUpdatedAt_DateHistogram(ConditionOptionCall<DateHistogramBuilder> opLambda) {
        setUpdatedAt_DateHistogram("updatedAt", opLambda, null);
    }

    public void setUpdatedAt_DateHistogram(ConditionOptionCall<DateHistogramBuilder> opLambda, OperatorCall<BsUserInfoCA> aggsLambda) {
        setUpdatedAt_DateHistogram("updatedAt", opLambda, aggsLambda);
    }

    public void setUpdatedAt_DateHistogram(String name, ConditionOptionCall<DateHistogramBuilder> opLambda,
            OperatorCall<BsUserInfoCA> aggsLambda) {
        DateHistogramBuilder builder = regDateHistogramA(name, "updatedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserInfoCA ca = new UserInfoCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

    public void setUpdatedAt_Count() {
        setUpdatedAt_Count(null);
    }

    public void setUpdatedAt_Count(ConditionOptionCall<ValueCountBuilder> opLambda) {
        setUpdatedAt_Count("updatedAt", opLambda);
    }

    public void setUpdatedAt_Count(String name, ConditionOptionCall<ValueCountBuilder> opLambda) {
        ValueCountBuilder builder = regCountA(name, "updatedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedAt_Cardinality() {
        setUpdatedAt_Cardinality(null);
    }

    public void setUpdatedAt_Cardinality(ConditionOptionCall<CardinalityBuilder> opLambda) {
        setUpdatedAt_Cardinality("updatedAt", opLambda);
    }

    public void setUpdatedAt_Cardinality(String name, ConditionOptionCall<CardinalityBuilder> opLambda) {
        CardinalityBuilder builder = regCardinalityA(name, "updatedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
    }

    public void setUpdatedAt_Missing() {
        setUpdatedAt_Missing(null);
    }

    public void setUpdatedAt_Missing(ConditionOptionCall<MissingBuilder> opLambda) {
        setUpdatedAt_Missing("updatedAt", opLambda, null);
    }

    public void setUpdatedAt_Missing(ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserInfoCA> aggsLambda) {
        setUpdatedAt_Missing("updatedAt", opLambda, aggsLambda);
    }

    public void setUpdatedAt_Missing(String name, ConditionOptionCall<MissingBuilder> opLambda, OperatorCall<BsUserInfoCA> aggsLambda) {
        MissingBuilder builder = regMissingA(name, "updatedAt");
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        if (aggsLambda != null) {
            UserInfoCA ca = new UserInfoCA();
            aggsLambda.callback(ca);
            ca.getAggregationBuilderList().forEach(builder::subAggregation);
        }
    }

}
