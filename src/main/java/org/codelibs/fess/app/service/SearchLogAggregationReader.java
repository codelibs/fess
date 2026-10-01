/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fesen.opensearch.search.aggregations.Aggregations;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.SingleBucketAggregation;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.histogram.Histogram;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.terms.Terms;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.Cardinality;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.NumericMetricsAggregation;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.Percentile;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.Percentiles;
import org.codelibs.fesen.opensearch.search.aggregations.metrics.ValueCount;

/**
 * Null-safe readers of parsed aggregation results used by {@link SearchLogAnalyticsService}.
 * A missing aggregation (or a missing parent bucket, passed as null) reads as zero, null or an empty list.
 */
final class SearchLogAggregationReader {

    private static final String COUNT = "count";

    private SearchLogAggregationReader() {
        // static only
    }

    static Aggregations subAggs(final Aggregations aggs, final String name) {
        if (aggs == null) {
            return null;
        }
        final SingleBucketAggregation agg = aggs.get(name);
        return agg == null ? null : agg.getAggregations();
    }

    static long docCount(final Aggregations aggs, final String name) {
        if (aggs == null) {
            return 0L;
        }
        final SingleBucketAggregation agg = aggs.get(name);
        return agg == null ? 0L : agg.getDocCount();
    }

    static long cardinality(final Aggregations aggs, final String name) {
        if (aggs == null) {
            return 0L;
        }
        final Cardinality agg = aggs.get(name);
        return agg == null ? 0L : agg.getValue();
    }

    static long valueCount(final Aggregations aggs, final String name) {
        if (aggs == null) {
            return 0L;
        }
        final ValueCount agg = aggs.get(name);
        return agg == null ? 0L : agg.getValue();
    }

    static Number value(final Aggregations aggs, final String name) {
        if (aggs == null) {
            return null;
        }
        final NumericMetricsAggregation.SingleValue agg = aggs.get(name);
        return agg == null ? null : SearchLogAnalyticsService.finite(agg.value());
    }

    static Number percentile(final Aggregations aggs, final String name, final double percent) {
        if (aggs == null) {
            return null;
        }
        final Percentiles agg = aggs.get(name);
        if (agg == null) {
            return null;
        }
        for (final Percentile p : agg) {
            if (p.getPercent() == percent) {
                return SearchLogAnalyticsService.finite(p.getValue());
            }
        }
        return null;
    }

    static List<? extends Terms.Bucket> termsBuckets(final Aggregations aggs, final String name) {
        if (aggs == null) {
            return Collections.emptyList();
        }
        final Terms agg = aggs.get(name);
        return agg == null ? Collections.emptyList() : agg.getBuckets();
    }

    static List<Map<String, Object>> termsRows(final Aggregations aggs, final String name, final String keyName) {
        final List<Map<String, Object>> rows = new ArrayList<>();
        for (final Terms.Bucket bucket : termsBuckets(aggs, name)) {
            final Map<String, Object> row = new LinkedHashMap<>();
            row.put(keyName, bucket.getKeyAsString());
            row.put(COUNT, bucket.getDocCount());
            rows.add(row);
        }
        return rows;
    }

    static List<? extends Histogram.Bucket> histogramBucketList(final Aggregations aggs, final String name) {
        if (aggs == null) {
            return Collections.emptyList();
        }
        final Histogram agg = aggs.get(name);
        return agg == null ? Collections.emptyList() : agg.getBuckets();
    }

    // indexes date histogram buckets by the epoch milliseconds of their keys
    static Map<Long, Histogram.Bucket> histogramBuckets(final Aggregations aggs, final String name) {
        final Map<Long, Histogram.Bucket> map = new LinkedHashMap<>();
        for (final Histogram.Bucket bucket : histogramBucketList(aggs, name)) {
            map.put(((ZonedDateTime) bucket.getKey()).toInstant().toEpochMilli(), bucket);
        }
        return map;
    }

    static long totalDocCount(final Map<Long, Histogram.Bucket> buckets) {
        long total = 0L;
        for (final Histogram.Bucket bucket : buckets.values()) {
            total += bucket.getDocCount();
        }
        return total;
    }
}
