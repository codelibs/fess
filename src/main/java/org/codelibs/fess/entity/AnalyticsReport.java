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
package org.codelibs.fess.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Result of one search log analytics tab: KPI cards, ranking tables, charts and notices.
 */
public class AnalyticsReport {

    /** Unit of a count value. */
    public static final String UNIT_COUNT = "count";

    /** Unit of a rate value, stored as a fraction between 0 and 1. */
    public static final String UNIT_PERCENT = "percent";

    /** Unit of a duration in milliseconds. */
    public static final String UNIT_MS = "ms";

    /** Unit of a result rank. */
    public static final String UNIT_RANK = "rank";

    /** Unit of a chart whose series have different units. */
    public static final String UNIT_MIXED = "mixed";

    /**
     * A KPI card value with its value of the comparison period.
     */
    public static class Kpi {
        private final String key;

        private final String unit;

        private final Number value;

        private final Number previous;

        /**
         * Creates a KPI.
         *
         * @param key the metric key
         * @param unit the unit: "count", "percent" (fraction 0..1), "ms" or "rank"
         * @param value the value of the period, or null when unavailable
         * @param previous the value of the comparison period, or null when not compared
         */
        public Kpi(final String key, final String unit, final Number value, final Number previous) {
            this.key = key;
            this.unit = unit;
            this.value = value;
            this.previous = previous;
        }

        /**
         * Gets the metric key.
         *
         * @return the key
         */
        public String getKey() {
            return key;
        }

        /**
         * Gets the unit.
         *
         * @return "count", "percent", "ms" or "rank"
         */
        public String getUnit() {
            return unit;
        }

        /**
         * Gets the value of the period.
         *
         * @return the value, or null when unavailable
         */
        public Number getValue() {
            return value;
        }

        /**
         * Gets the value of the comparison period.
         *
         * @return the previous value, or null when not compared
         */
        public Number getPrevious() {
            return previous;
        }

        /**
         * Gets the change from the comparison period: a percent change for counts, durations and ranks,
         * or a point change for rates.
         *
         * @return the change, or null when it cannot be computed
         */
        public Double getChange() {
            if (value == null || previous == null) {
                return null;
            }
            final double v = value.doubleValue();
            final double p = previous.doubleValue();
            if (isPointChange()) {
                return (v - p) * 100;
            }
            if (p == 0) {
                return null;
            }
            return (v - p) / p * 100;
        }

        /**
         * Checks whether {@link #getChange()} is a point change.
         *
         * @return true for a rate metric
         */
        public boolean isPointChange() {
            return UNIT_PERCENT.equals(unit);
        }
    }

    /**
     * A data series of a chart.
     */
    public static class Series {
        private final String key;

        private String name;

        private final List<Number> data;

        private List<Number> previous;

        /**
         * Creates a series.
         *
         * @param key the metric key
         * @param data the values, one per x-axis entry (null for no value)
         */
        public Series(final String key, final List<Number> data) {
            this.key = key;
            this.data = data;
        }

        /**
         * Gets the metric key.
         *
         * @return the key
         */
        public String getKey() {
            return key;
        }

        /**
         * Gets the display name.
         *
         * @return the name, or null when not localized yet
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the display name.
         *
         * @param name the name
         */
        public void setName(final String name) {
            this.name = name;
        }

        /**
         * Gets the values.
         *
         * @return the values
         */
        public List<Number> getData() {
            return data;
        }

        /**
         * Gets the values of the comparison period, aligned by index with {@link #getData()}.
         *
         * @return the previous values, or null when not compared
         */
        public List<Number> getPrevious() {
            return previous;
        }

        /**
         * Sets the values of the comparison period.
         *
         * @param previous the previous values
         */
        public void setPrevious(final List<Number> previous) {
            this.previous = previous;
        }
    }

    /**
     * A chart definition rendered by the admin chart script.
     */
    public static class Chart {
        private final String type;

        private final String unit;

        private final List<String> x;

        private final List<Series> series;

        private boolean selectable;

        /**
         * Creates a chart.
         *
         * @param type "line", "bar" or "pie"
         * @param unit the unit of the values, or "mixed" when it depends on the series
         * @param x the x-axis labels (category names for a pie)
         * @param series the series
         */
        public Chart(final String type, final String unit, final List<String> x, final List<Series> series) {
            this.type = type;
            this.unit = unit;
            this.x = x;
            this.series = series;
        }

        /**
         * Gets the chart type.
         *
         * @return "line", "bar" or "pie"
         */
        public String getType() {
            return type;
        }

        /**
         * Gets the unit.
         *
         * @return the unit
         */
        public String getUnit() {
            return unit;
        }

        /**
         * Gets the x-axis labels.
         *
         * @return the labels
         */
        public List<String> getX() {
            return x;
        }

        /**
         * Gets the series.
         *
         * @return the series
         */
        public List<Series> getSeries() {
            return series;
        }

        /**
         * Checks whether one series is shown at a time with a selector.
         *
         * @return true when selectable
         */
        public boolean isSelectable() {
            return selectable;
        }

        /**
         * Checks whether the chart has nothing to draw: every value of every series, and of its comparison
         * period, is null or zero. Not part of the chart JSON.
         *
         * @return true when there is no non-zero value
         */
        @JsonIgnore
        public boolean isEmpty() {
            for (final Series s : series) {
                if (hasValue(s.getData()) || hasValue(s.getPrevious())) {
                    return false;
                }
            }
            return true;
        }

        private static boolean hasValue(final List<Number> values) {
            return values != null && values.stream().anyMatch(v -> v != null && v.doubleValue() != 0);
        }

        /**
         * Marks this chart as selectable.
         *
         * @return this chart
         */
        public Chart selectable() {
            selectable = true;
            return this;
        }
    }

    /**
     * A message shown above the report.
     */
    public static class Notice {
        private final String level;

        private final String key;

        private final String[] args;

        /**
         * Creates a notice.
         *
         * @param level "info", "warning" or "danger"
         * @param key the label key
         * @param args the message arguments
         */
        public Notice(final String level, final String key, final String... args) {
            this.level = level;
            this.key = key;
            this.args = args;
        }

        /**
         * Gets the level.
         *
         * @return "info", "warning" or "danger"
         */
        public String getLevel() {
            return level;
        }

        /**
         * Gets the label key.
         *
         * @return the key
         */
        public String getKey() {
            return key;
        }

        /**
         * Gets the message arguments.
         *
         * @return the arguments
         */
        public String[] getArgs() {
            return args;
        }
    }

    private final List<Kpi> kpis = new ArrayList<>();

    private final Map<String, List<Map<String, Object>>> tables = new LinkedHashMap<>();

    private final Map<String, Chart> charts = new LinkedHashMap<>();

    private final List<Notice> notices = new ArrayList<>();

    private boolean failed;

    /**
     * Creates an empty report.
     */
    public AnalyticsReport() {
        // nothing
    }

    /**
     * Gets the KPIs in display order.
     *
     * @return the KPIs
     */
    public List<Kpi> getKpis() {
        return kpis;
    }

    /**
     * Gets the tables by name.
     *
     * @return the tables
     */
    public Map<String, List<Map<String, Object>>> getTables() {
        return tables;
    }

    /**
     * Gets the charts by name.
     *
     * @return the charts
     */
    public Map<String, Chart> getCharts() {
        return charts;
    }

    /**
     * Gets the notices.
     *
     * @return the notices
     */
    public List<Notice> getNotices() {
        return notices;
    }

    /**
     * Checks whether building the report failed.
     *
     * @return true when failed
     */
    public boolean isFailed() {
        return failed;
    }

    /**
     * Sets whether building the report failed.
     *
     * @param failed true when failed
     */
    public void setFailed(final boolean failed) {
        this.failed = failed;
    }

    /**
     * Adds a KPI.
     *
     * @param kpi the KPI
     */
    public void addKpi(final Kpi kpi) {
        kpis.add(kpi);
    }

    /**
     * Puts a table.
     *
     * @param name the table name
     * @param rows the rows
     */
    public void putTable(final String name, final List<Map<String, Object>> rows) {
        tables.put(name, rows);
    }

    /**
     * Puts a chart.
     *
     * @param name the chart name
     * @param chart the chart
     */
    public void putChart(final String name, final Chart chart) {
        charts.put(name, chart);
    }

    /**
     * Adds a notice.
     *
     * @param notice the notice
     */
    public void addNotice(final Notice notice) {
        notices.add(notice);
    }
}
