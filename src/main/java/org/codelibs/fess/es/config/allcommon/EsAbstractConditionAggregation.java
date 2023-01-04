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
package org.codelibs.fess.es.config.allcommon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.dbflute.exception.InvalidQueryRegisteredException;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.search.aggregations.AbstractAggregationBuilder;
import org.opensearch.search.aggregations.AggregationBuilders;
import org.opensearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.opensearch.search.aggregations.bucket.global.GlobalAggregationBuilder;
import org.opensearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.opensearch.search.aggregations.bucket.histogram.HistogramAggregationBuilder;
import org.opensearch.search.aggregations.bucket.missing.MissingAggregationBuilder;
import org.opensearch.search.aggregations.bucket.range.DateRangeAggregationBuilder;
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
public abstract class EsAbstractConditionAggregation {

    protected static final String CA_PROPERTY = "conditionAggregation";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<AbstractAggregationBuilder<?>> aggregationAggregationBuilderList;

    // ===================================================================================
    //                                                                             Control
    //                                                                             =======

    public boolean hasAggregations() {
        return aggregationAggregationBuilderList != null && !aggregationAggregationBuilderList.isEmpty();
    }

    public List<AbstractAggregationBuilder<?>> getAggregationBuilderList() {
        return aggregationAggregationBuilderList != null ? aggregationAggregationBuilderList : Collections.emptyList();
    }

    // ===================================================================================
    //                                                                         Aggregation
    //                                                                         ===========
    public void addAggregation(AbstractAggregationBuilder<?> aggregationAggregationBuilder) {
        assertObjectNotNull("aggregationAggregationBuilder", aggregationAggregationBuilder);
        regA(aggregationAggregationBuilder);
    }

    // ===================================================================================
    //                                                                            Register
    //                                                                            ========

    protected AvgAggregationBuilder regAvgA(String name, String field) {
        AvgAggregationBuilder builder = AggregationBuilders.avg(name).field(field);
        regA(builder);
        return builder;
    }

    protected MaxAggregationBuilder regMaxA(String name, String field) {
        MaxAggregationBuilder builder = AggregationBuilders.max(name).field(field);
        regA(builder);
        return builder;
    }

    protected MinAggregationBuilder regMinA(String name, String field) {
        MinAggregationBuilder builder = AggregationBuilders.min(name).field(field);
        regA(builder);
        return builder;
    }

    protected SumAggregationBuilder regSumA(String name, String field) {
        SumAggregationBuilder builder = AggregationBuilders.sum(name).field(field);
        regA(builder);
        return builder;
    }

    protected ExtendedStatsAggregationBuilder regExtendedStatsA(String name, String field) {
        ExtendedStatsAggregationBuilder builder = AggregationBuilders.extendedStats(name).field(field);
        regA(builder);
        return builder;
    }

    protected StatsAggregationBuilder regStatsA(String name, String field) {
        StatsAggregationBuilder builder = AggregationBuilders.stats(name).field(field);
        regA(builder);
        return builder;
    }

    protected PercentilesAggregationBuilder regPercentilesA(String name, String field) {
        PercentilesAggregationBuilder builder = AggregationBuilders.percentiles(name).field(field);
        regA(builder);
        return builder;
    }

    protected PercentileRanksAggregationBuilder regPercentileRanksA(String name, String field, double[] values) {
        PercentileRanksAggregationBuilder builder = AggregationBuilders.percentileRanks(name, values).field(field);
        regA(builder);
        return builder;
    }

    protected CardinalityAggregationBuilder regCardinalityA(String name, String field) {
        CardinalityAggregationBuilder builder = AggregationBuilders.cardinality(name).field(field);
        regA(builder);
        return builder;
    }

    protected ValueCountAggregationBuilder regCountA(String name, String field) {
        ValueCountAggregationBuilder builder = AggregationBuilders.count(name).field(field);
        regA(builder);
        return builder;
    }

    protected TermsAggregationBuilder regTermsA(String name, String field) {
        TermsAggregationBuilder builder = AggregationBuilders.terms(name).field(field);
        regA(builder);
        return builder;
    }

    protected SignificantTermsAggregationBuilder regSignificantTermsA(String name, String field) {
        SignificantTermsAggregationBuilder builder = AggregationBuilders.significantTerms(name).field(field);
        regA(builder);
        return builder;
    }

    protected HistogramAggregationBuilder regHistogramA(String name, String field) {
        HistogramAggregationBuilder builder = AggregationBuilders.histogram(name).field(field);
        regA(builder);
        return builder;
    }

    protected DateHistogramAggregationBuilder regDateHistogramA(String name, String field) {
        DateHistogramAggregationBuilder builder = AggregationBuilders.dateHistogram(name).field(field);
        regA(builder);
        return builder;
    }

    protected RangeAggregationBuilder regRangeA(String name, String field) {
        RangeAggregationBuilder builder = AggregationBuilders.range(name).field(field);
        regA(builder);
        return builder;
    }

    protected DateRangeAggregationBuilder regDateRangeA(String name, String field) {
        DateRangeAggregationBuilder builder = AggregationBuilders.dateRange(name).field(field);
        regA(builder);
        return builder;
    }

    protected IpRangeAggregationBuilder regIpRangeA(String name, String field) {
        IpRangeAggregationBuilder builder = AggregationBuilders.ipRange(name).field(field);
        regA(builder);
        return builder;
    }

    protected MissingAggregationBuilder regMissingA(String name, String field) {
        MissingAggregationBuilder builder = AggregationBuilders.missing(name).field(field);
        regA(builder);
        return builder;
    }

    protected FilterAggregationBuilder regFilterA(String name, QueryBuilder filter) {
        FilterAggregationBuilder builder = AggregationBuilders.filter(name, filter);
        regA(builder);
        return builder;
    }

    protected GlobalAggregationBuilder regGlobalA(String name) {
        GlobalAggregationBuilder builder = AggregationBuilders.global(name);
        regA(builder);
        return builder;
    }

    protected SamplerAggregationBuilder regSamplerA(String name) {
        SamplerAggregationBuilder builder = AggregationBuilders.sampler(name);
        regA(builder);
        return builder;
    }

    protected ScriptedMetricAggregationBuilder regScriptedMetricA(String name) {
        ScriptedMetricAggregationBuilder builder = AggregationBuilders.scriptedMetric(name);
        regA(builder);
        return builder;
    }

    protected TopHitsAggregationBuilder regTopHitsA(String name) {
        TopHitsAggregationBuilder builder = AggregationBuilders.topHits(name);
        regA(builder);
        return builder;
    }

    protected void regA(AbstractAggregationBuilder<?> builder) {
        assertObjectNotNull("builder", builder);
        if (aggregationAggregationBuilderList == null) {
            aggregationAggregationBuilderList = new ArrayList<>();
        }
        aggregationAggregationBuilderList.add(builder);
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
    public interface ConditionOptionCall<OP extends AbstractAggregationBuilder<?>> {

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
