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
package org.codelibs.fess.es.log.allcommon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.dbflute.exception.InvalidQueryRegisteredException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.global.GlobalBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.HistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.missing.MissingBuilder;
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder;
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeBuilder;
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
public abstract class EsAbstractConditionAggregation {

    protected static final String CA_PROPERTY = "conditionAggregation";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<AbstractAggregationBuilder> aggregationBuilderList;

    // ===================================================================================
    //                                                                             Control
    //                                                                             =======

    public boolean hasAggregations() {
        return aggregationBuilderList != null && !aggregationBuilderList.isEmpty();
    }

    public List<AbstractAggregationBuilder> getAggregationBuilderList() {
        return aggregationBuilderList != null ? aggregationBuilderList : Collections.emptyList();
    }

    // ===================================================================================
    //                                                                         Aggregation
    //                                                                               =====
    public void addAggregation(AbstractAggregationBuilder aggregationBuilder) {
        assertObjectNotNull("aggregationBuilder", aggregationBuilder);
        regA(aggregationBuilder);
    }

    // ===================================================================================
    //                                                                            Register
    //                                                                            ========

    protected AvgBuilder regAvgA(String name, String field) {
        AvgBuilder builder = AggregationBuilders.avg(name).field(field);
        regA(builder);
        return builder;
    }

    protected MaxBuilder regMaxA(String name, String field) {
        MaxBuilder builder = AggregationBuilders.max(name).field(field);
        regA(builder);
        return builder;
    }

    protected MinBuilder regMinA(String name, String field) {
        MinBuilder builder = AggregationBuilders.min(name).field(field);
        regA(builder);
        return builder;
    }

    protected SumBuilder regSumA(String name, String field) {
        SumBuilder builder = AggregationBuilders.sum(name).field(field);
        regA(builder);
        return builder;
    }

    protected ExtendedStatsBuilder regExtendedStatsA(String name, String field) {
        ExtendedStatsBuilder builder = AggregationBuilders.extendedStats(name).field(field);
        regA(builder);
        return builder;
    }

    protected StatsBuilder regStatsA(String name, String field) {
        StatsBuilder builder = AggregationBuilders.stats(name).field(field);
        regA(builder);
        return builder;
    }

    protected PercentilesBuilder regPercentilesA(String name, String field) {
        PercentilesBuilder builder = AggregationBuilders.percentiles(name).field(field);
        regA(builder);
        return builder;
    }

    protected PercentileRanksBuilder regPercentileRanksA(String name, String field) {
        PercentileRanksBuilder builder = AggregationBuilders.percentileRanks(name).field(field);
        regA(builder);
        return builder;
    }

    protected CardinalityBuilder regCardinalityA(String name, String field) {
        CardinalityBuilder builder = AggregationBuilders.cardinality(name).field(field);
        regA(builder);
        return builder;
    }

    protected ValueCountBuilder regCountA(String name, String field) {
        ValueCountBuilder builder = AggregationBuilders.count(name).field(field);
        regA(builder);
        return builder;
    }

    protected TermsBuilder regTermsA(String name, String field) {
        TermsBuilder builder = AggregationBuilders.terms(name).field(field);
        regA(builder);
        return builder;
    }

    protected SignificantTermsBuilder regSignificantTermsA(String name, String field) {
        SignificantTermsBuilder builder = AggregationBuilders.significantTerms(name).field(field);
        regA(builder);
        return builder;
    }

    protected HistogramBuilder regHistogramA(String name, String field) {
        HistogramBuilder builder = AggregationBuilders.histogram(name).field(field);
        regA(builder);
        return builder;
    }

    protected DateHistogramBuilder regDateHistogramA(String name, String field) {
        DateHistogramBuilder builder = AggregationBuilders.dateHistogram(name).field(field);
        regA(builder);
        return builder;
    }

    protected RangeBuilder regRangeA(String name, String field) {
        RangeBuilder builder = AggregationBuilders.range(name).field(field);
        regA(builder);
        return builder;
    }

    protected DateRangeBuilder regDateRangeA(String name, String field) {
        DateRangeBuilder builder = AggregationBuilders.dateRange(name).field(field);
        regA(builder);
        return builder;
    }

    protected IPv4RangeBuilder regIpRangeA(String name, String field) {
        IPv4RangeBuilder builder = AggregationBuilders.ipRange(name).field(field);
        regA(builder);
        return builder;
    }

    protected MissingBuilder regMissingA(String name, String field) {
        MissingBuilder builder = AggregationBuilders.missing(name).field(field);
        regA(builder);
        return builder;
    }

    protected FilterAggregationBuilder regFilterA(String name, QueryBuilder filter) {
        FilterAggregationBuilder builder = AggregationBuilders.filter(name).filter(filter);
        regA(builder);
        return builder;
    }

    protected GlobalBuilder regGlobalA(String name) {
        GlobalBuilder builder = AggregationBuilders.global(name);
        regA(builder);
        return builder;
    }

    protected SamplerAggregationBuilder regSamplerA(String name) {
        SamplerAggregationBuilder builder = AggregationBuilders.sampler(name);
        regA(builder);
        return builder;
    }

    protected ScriptedMetricBuilder regScriptedMetricA(String name) {
        ScriptedMetricBuilder builder = AggregationBuilders.scriptedMetric(name);
        regA(builder);
        return builder;
    }

    protected TopHitsBuilder regTopHitsA(String name) {
        TopHitsBuilder builder = AggregationBuilders.topHits(name);
        regA(builder);
        return builder;
    }

    protected void regA(AbstractAggregationBuilder builder) {
        assertObjectNotNull("builder", builder);
        if (aggregationBuilderList == null) {
            aggregationBuilderList = new ArrayList<>();
        }
        aggregationBuilderList.add(builder);
    }

    // ===================================================================================
    //                                                                 Invalid Aggregation
    //                                                                       =============
    protected void checkEsInvalidAggregation(String name, Object value) {
        if (value == null || (value instanceof String && ((String) value).isEmpty())) {
            String msg = "Cannot register null or empty aggregation: name=" + name + " value=" + value;
            throw new InvalidQueryRegisteredException(msg);
        }
    }

    protected void checkEsInvalidAggregationCollection(String name, Collection<?> values) {
        if (values == null || values.isEmpty()) {
            String msg = "Cannot register null or empty query collection: name=" + name + " values=" + values;
            throw new InvalidQueryRegisteredException(msg);
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                        Assist Class
    //                                                                        ============

    @FunctionalInterface
    public interface ConditionOptionCall<OP extends AbstractAggregationBuilder> {

        /**
         * @param op The option of condition to be set up. (NotNull)
         */
        void callback(OP op);
    }

    @FunctionalInterface
    public interface OperatorCall<CA extends EsAbstractConditionAggregation> {

        void callback(CA query);
    }
}
