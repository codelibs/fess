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

import static org.codelibs.fess.app.service.SearchLogAggregationReader.cardinality;
import static org.codelibs.fess.app.service.SearchLogAggregationReader.docCount;
import static org.codelibs.fess.app.service.SearchLogAggregationReader.histogramBucketList;
import static org.codelibs.fess.app.service.SearchLogAggregationReader.histogramBuckets;
import static org.codelibs.fess.app.service.SearchLogAggregationReader.percentile;
import static org.codelibs.fess.app.service.SearchLogAggregationReader.subAggs;
import static org.codelibs.fess.app.service.SearchLogAggregationReader.termsBuckets;
import static org.codelibs.fess.app.service.SearchLogAggregationReader.termsRows;
import static org.codelibs.fess.app.service.SearchLogAggregationReader.totalDocCount;
import static org.codelibs.fess.app.service.SearchLogAggregationReader.value;
import static org.codelibs.fess.app.service.SearchLogAggregationReader.valueCount;

import java.io.IOException;
import java.io.Writer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.entity.AnalyticsCondition;
import org.codelibs.fess.entity.AnalyticsCondition.Interval;
import org.codelibs.fess.entity.AnalyticsReport;
import org.codelibs.fess.entity.AnalyticsReport.Chart;
import org.codelibs.fess.entity.AnalyticsReport.Kpi;
import org.codelibs.fess.entity.AnalyticsReport.Notice;
import org.codelibs.fess.entity.AnalyticsReport.Series;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.log.allcommon.EsPagingResultBean;
import org.codelibs.fess.opensearch.log.cbean.ClickLogCB;
import org.codelibs.fess.opensearch.log.cbean.FavoriteLogCB;
import org.codelibs.fess.opensearch.log.cbean.SearchLogCB;
import org.codelibs.fess.opensearch.log.cbean.UserInfoCB;
import org.codelibs.fess.opensearch.log.exbhv.ClickLogBhv;
import org.codelibs.fess.opensearch.log.exbhv.FavoriteLogBhv;
import org.codelibs.fess.opensearch.log.exbhv.SearchLogBhv;
import org.codelibs.fess.opensearch.log.exbhv.UserInfoBhv;
import org.codelibs.fess.opensearch.log.exentity.ClickLog;
import org.codelibs.fess.opensearch.log.exentity.FavoriteLog;
import org.codelibs.fess.opensearch.log.exentity.SearchLog;
import org.codelibs.fess.opensearch.log.exentity.UserInfo;
import org.codelibs.fess.util.CsvUtil;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;
import org.codelibs.fesen.opensearch.search.aggregations.AggregationBuilders;
import org.codelibs.fesen.opensearch.search.aggregations.Aggregations;
import org.codelibs.fesen.opensearch.search.aggregations.BucketOrder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.histogram.Histogram;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.range.Range;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.terms.Terms;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

import com.orangesignal.csv.CsvWriter;

import jakarta.annotation.Resource;

/**
 * Builds the search log analytics reports (overview, queries, clicks, performance and audience tabs)
 * from size-0 aggregations over the search, click, favorite and user info logs.
 */
public class SearchLogAnalyticsService {

    private static final Logger logger = LogManager.getLogger(SearchLogAnalyticsService.class);

    /** Overview tab. */
    public static final String TAB_OVERVIEW = "overview";

    /** Queries tab. */
    public static final String TAB_QUERIES = "queries";

    /** Clicks tab. */
    public static final String TAB_CLICKS = "clicks";

    /** Performance tab. */
    public static final String TAB_PERFORMANCE = "performance";

    /** Audience tab. */
    public static final String TAB_AUDIENCE = "audience";

    /** All tabs in display order. */
    public static final List<String> TABS = List.of(TAB_OVERVIEW, TAB_QUERIES, TAB_CLICKS, TAB_PERFORMANCE, TAB_AUDIENCE);

    /** The export item that downloads the KPI cards. */
    public static final String ITEM_KPIS = "kpis";

    /** The export item that downloads the weekday by hour table. */
    public static final String ITEM_WEEK_HOUR = "weekHour";

    /** Number of search words with hits checked for clicks when listing zero-click words. */
    protected static final int ZERO_CLICK_CANDIDATES = 1000;

    /** Maximum number of access types listed. */
    protected static final int ACCESS_TYPE_SIZE = 50;

    /** Minimum number of searches for a word to be listed as a slow query. */
    protected static final long SLOW_QUERY_MIN_SEARCHES = 3;

    private static final String INFO = "info";

    private static final String WARNING = "warning";

    private static final String DANGER = "danger";

    private static final String LINE = "line";

    private static final String BAR = "bar";

    private static final String BAR_WIDTH = "bar";

    private static final String PIE = "pie";

    private static final List<String> RANK_BUCKETS = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11-20", "21+");

    private static final List<String> RESPONSE_TIME_BUCKETS = List.of("< 100 ms", "100-300 ms", "300 ms-1 s", "1-3 s", ">= 3 s");

    private static final DateTimeFormatter HOUR_LABEL = DateTimeFormatter.ofPattern("MM-dd HH:00");

    private static final DateTimeFormatter DAY_LABEL = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // field names
    private static final String REQUESTED_AT = "requestedAt";

    private static final String USER_INFO_ID = "userInfoId";

    private static final String HIT_COUNT = "hitCount";

    private static final String RESPONSE_TIME = "responseTime";

    private static final String QUERY_TIME = "queryTime";

    private static final String QUERY_OFFSET = "queryOffset";

    // aggregation names, row keys and metric keys
    private static final String TREND = "trend";

    private static final String HOURLY = "hourly";

    private static final String WORDS = "words";

    private static final String SEARCHES = "searches";

    private static final String USERS = "users";

    private static final String ZERO_HIT = "zeroHit";

    private static final String ZERO_HIT_RATE = "zeroHitRate";

    private static final String ZERO_CLICK = "zeroClick";

    private static final String CTR = "ctr";

    private static final String CLICKS = "clicks";

    private static final String CLICKED_QUERIES = "clickedQueries";

    private static final String AVG_RANK = "avgRank";

    private static final String AVG_HITS = "avgHits";

    private static final String AVG_RESPONSE_TIME = "avgResponseTime";

    private static final String AVG_QUERY_TIME = "avgQueryTime";

    private static final String PERCENTILES = "percentiles";

    private static final String PAGING = "paging";

    private static final String RANKS = "ranks";

    private static final String URLS = "urls";

    private static final String EARLIEST = "earliest";

    private static final String LAST_SEARCHED_AT = "lastSearchedAt";

    private static final String ACCESS_TYPES = "accessTypes";

    private static final String WORD = "word";

    private static final String VALUE = "value";

    private static final String URL = "url";

    private static final String COUNT = "count";

    private static final List<String> KPI_COLUMNS = List.of("metric", "unit", "value", "previous", "change");

    private static final List<String> WORD_COLUMNS = List.of(WORD, COUNT);

    private static final List<String> URL_COLUMNS = List.of(URL, COUNT);

    private static final List<String> VALUE_COLUMNS = List.of(VALUE, COUNT);

    /**
     * The items of each tab that can be downloaded as CSV. A table lists its columns (the presentational
     * bar width is not one of them); a KPI list, a chart and the weekday by hour table list none.
     */
    private static final Map<String, Map<String, List<String>>> EXPORT_ITEMS = Map.of(TAB_OVERVIEW,
            Map.of(ITEM_KPIS, List.of(), TREND, List.of(), "topQueries", WORD_COLUMNS, "zeroHitQueries", WORD_COLUMNS), TAB_QUERIES,
            Map.of(TAB_QUERIES, List.of(WORD, COUNT, USERS, AVG_HITS, CLICKS, CTR, AVG_RANK), "zeroHitQueries", List.of(WORD, COUNT, USERS),
                    "zeroClickQueries", List.of(WORD, COUNT, LAST_SEARCHED_AT)),
            TAB_CLICKS,
            Map.of(ITEM_KPIS, List.of(), "rankDistribution", List.of(), "pagingRate", List.of(), "clickedUrls", URL_COLUMNS, "favoriteUrls",
                    URL_COLUMNS),
            TAB_PERFORMANCE,
            Map.of("responseTime", List.of(), "responseTimeDistribution", List.of(), "queryTime", List.of(), "slowQueries",
                    List.of(WORD, COUNT, AVG_RESPONSE_TIME)),
            TAB_AUDIENCE, Map.of(USERS, List.of(), ACCESS_TYPES, List.of(), ITEM_WEEK_HOUR, List.of(), "userAgents", VALUE_COLUMNS,
                    "referers", VALUE_COLUMNS, "languages", VALUE_COLUMNS, "virtualHosts", VALUE_COLUMNS));

    /** Behavior for the search log index. */
    @Resource
    protected SearchLogBhv searchLogBhv;

    /** Behavior for the click log index. */
    @Resource
    protected ClickLogBhv clickLogBhv;

    /** Behavior for the favorite log index. */
    @Resource
    protected FavoriteLogBhv favoriteLogBhv;

    /** Behavior for the user info index. */
    @Resource
    protected UserInfoBhv userInfoBhv;

    /** Fess configuration. */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Default constructor.
     */
    public SearchLogAnalyticsService() {
        // nothing
    }

    /**
     * Builds the report of a tab. Never throws: on failure the report is empty, marked as failed
     * and carries a "danger" notice.
     *
     * @param tab the tab name (unknown or null falls back to the overview)
     * @param cond the analytics condition
     * @return the report
     */
    public AnalyticsReport getReport(final String tab, final AnalyticsCondition cond) {
        final String name = tab == null ? TAB_OVERVIEW : tab;
        try {
            return switch (name) {
            case TAB_QUERIES -> buildQueries(cond);
            case TAB_CLICKS -> buildClicks(cond);
            case TAB_PERFORMANCE -> buildPerformance(cond);
            case TAB_AUDIENCE -> buildAudience(cond);
            default -> buildOverview(cond);
            };
        } catch (final Exception e) {
            logger.warn("Failed to build the search log analytics report: tab={}", name, e);
            final AnalyticsReport report = new AnalyticsReport();
            report.setFailed(true);
            report.addNotice(new Notice(DANGER, "labels.searchlog_notice_failed"));
            return report;
        }
    }

    /**
     * Lists the access types recorded in the search log during the period, most frequent first.
     * The access type filter of the condition is not applied.
     *
     * @param cond the analytics condition
     * @return the access types, or an empty list on failure
     */
    public List<String> getAccessTypes(final AnalyticsCondition cond) {
        try {
            final Aggregations aggs = searchLogAggs(cond.getStart(), cond.getEnd(), null,
                    cb -> cb.aggregation().setAccessType_Terms(ACCESS_TYPES, op -> applyTermsSize(op, ACCESS_TYPE_SIZE), null));
            final List<String> list = new ArrayList<>();
            termsBuckets(aggs, ACCESS_TYPES).forEach(b -> list.add(b.getKeyAsString()));
            return list;
        } catch (final Exception e) {
            logger.warn("Failed to aggregate access types of search logs.", e);
            return new ArrayList<>();
        }
    }

    // ===================================================================================
    //                                                                                Export
    //                                                                                ======

    /**
     * Checks whether an item of a tab can be downloaded as CSV.
     *
     * @param tab the tab name
     * @param item the item: "kpis", "weekHour", a chart name or a table name of the tab
     * @return true if the tab has the item
     */
    public boolean isExportable(final String tab, final String item) {
        final Map<String, List<String>> items = tab == null ? null : EXPORT_ITEMS.get(tab);
        return items != null && item != null && items.containsKey(item);
    }

    /**
     * Writes one item of a report as CSV, with raw numbers (no locale formatting): the KPIs as
     * metric, unit, value, previous and change; the weekday by hour table as day (1 = Monday) and h00 to h23;
     * a chart as x, one column per series and {@code <key>_previous} for a series with a comparison period;
     * any other table as its columns. The writer is flushed but not closed.
     *
     * @param report the report of the tab
     * @param tab the tab name
     * @param item the item, see {@link #isExportable(String, String)}
     * @param writer the writer to write the CSV to
     * @throws IOException if writing fails
     * @throws IllegalArgumentException if the tab does not have the item
     */
    public void exportCsv(final AnalyticsReport report, final String tab, final String item, final Writer writer) throws IOException {
        if (!isExportable(tab, item)) {
            throw new IllegalArgumentException("Unknown export item: " + tab + "/" + item);
        }
        // not closed: that would close the writer of the caller
        @SuppressWarnings("resource")
        final CsvWriter csvWriter = new CsvWriter(writer, CsvUtil.createCsvConfig());
        if (ITEM_KPIS.equals(item)) {
            writeKpis(csvWriter, report);
        } else if (ITEM_WEEK_HOUR.equals(item)) {
            writeWeekHour(csvWriter, report.getTables().get(item));
        } else if (report.getCharts().containsKey(item)) {
            writeChart(csvWriter, report.getCharts().get(item));
        } else {
            writeTable(csvWriter, EXPORT_ITEMS.get(tab).get(item), report.getTables().get(item));
        }
        csvWriter.flush();
    }

    /**
     * Writes the KPIs of a report as metric, unit, value, previous and change.
     *
     * @param csvWriter the CSV writer
     * @param report the report
     * @throws IOException if writing fails
     */
    static void writeKpis(final CsvWriter csvWriter, final AnalyticsReport report) throws IOException {
        csvWriter.writeValues(KPI_COLUMNS);
        for (final Kpi kpi : report.getKpis()) {
            writeRow(csvWriter, kpi.getKey(), kpi.getUnit(), kpi.getValue(), kpi.getPrevious(), kpi.getChange());
        }
    }

    private static void writeWeekHour(final CsvWriter csvWriter, final List<Map<String, Object>> rows) throws IOException {
        final List<String> header = new ArrayList<>();
        header.add("day");
        for (int hour = 0; hour < 24; hour++) {
            header.add(String.format("h%02d", hour));
        }
        csvWriter.writeValues(header);
        if (rows == null) {
            return;
        }
        for (final Map<String, Object> row : rows) {
            final List<Object> values = new ArrayList<>();
            values.add(row.get("day"));
            for (final Object cell : (List<?>) row.get("cells")) {
                values.add(((Map<?, ?>) cell).get(COUNT));
            }
            writeRow(csvWriter, values.toArray());
        }
    }

    /**
     * Writes a chart as x, one column per series and {@code <key>_previous} for a series with a comparison period.
     *
     * @param csvWriter the CSV writer
     * @param chart the chart
     * @throws IOException if writing fails
     */
    static void writeChart(final CsvWriter csvWriter, final Chart chart) throws IOException {
        final List<String> header = new ArrayList<>();
        header.add("x");
        for (final Series series : chart.getSeries()) {
            header.add(series.getKey());
            if (series.getPrevious() != null) {
                header.add(series.getKey() + "_previous");
            }
        }
        csvWriter.writeValues(header);
        for (int i = 0; i < chart.getX().size(); i++) {
            final List<Object> values = new ArrayList<>();
            values.add(chart.getX().get(i));
            for (final Series series : chart.getSeries()) {
                values.add(valueAt(series.getData(), i));
                if (series.getPrevious() != null) {
                    values.add(valueAt(series.getPrevious(), i));
                }
            }
            writeRow(csvWriter, values.toArray());
        }
    }

    private static Number valueAt(final List<Number> values, final int index) {
        return values != null && index < values.size() ? values.get(index) : null;
    }

    /**
     * Writes a table as the given columns.
     *
     * @param csvWriter the CSV writer
     * @param columns the columns
     * @param rows the rows (null writes the header only)
     * @throws IOException if writing fails
     */
    static void writeTable(final CsvWriter csvWriter, final List<String> columns, final List<Map<String, Object>> rows) throws IOException {
        csvWriter.writeValues(columns);
        if (rows == null) {
            return;
        }
        for (final Map<String, Object> row : rows) {
            writeRow(csvWriter, columns.stream().map(row::get).toArray());
        }
    }

    private static void writeRow(final CsvWriter csvWriter, final Object... values) throws IOException {
        csvWriter.writeValues(Arrays.stream(values).map(CsvUtil::toCell).toList());
    }

    // ===================================================================================
    //                                                                             Overview
    //                                                                            ========

    /**
     * Builds the overview tab: KPIs, the trend chart, top queries and zero-hit queries.
     *
     * @param cond the analytics condition
     * @return the report
     */
    protected AnalyticsReport buildOverview(final AnalyticsCondition cond) {
        final AnalyticsReport report = newReport(TAB_OVERVIEW, cond);
        final List<ZonedDateTime> slots = bucketSlots(cond.getStart(), cond.getEnd(), cond.getInterval());
        final Aggregations search = overviewSearchAggs(cond, cond.getStart(), cond.getEnd(), true);
        final OverviewPeriod current = readOverview(search, overviewClickAggs(cond, cond.getStart(), cond.getEnd()), slots);
        OverviewPeriod previous = null;
        if (cond.isCompare()) {
            previous = readOverview(overviewSearchAggs(cond, cond.getPrevStart(), cond.getPrevEnd(), false),
                    overviewClickAggs(cond, cond.getPrevStart(), cond.getPrevEnd()),
                    bucketSlots(cond.getPrevStart(), cond.getPrevEnd(), cond.getInterval()));
        }

        report.addKpi(new Kpi(SEARCHES, AnalyticsReport.UNIT_COUNT, current.searches(), previous == null ? null : previous.searches()));
        report.addKpi(new Kpi(USERS, AnalyticsReport.UNIT_COUNT, current.users(), previous == null ? null : previous.users()));
        report.addKpi(new Kpi(ZERO_HIT_RATE, AnalyticsReport.UNIT_PERCENT, ratio(current.zeroHits(), current.searches()),
                previous == null ? null : ratio(previous.zeroHits(), previous.searches())));
        report.addKpi(new Kpi(CTR, AnalyticsReport.UNIT_PERCENT, ratio(current.clickedQueries(), current.searches()),
                previous == null ? null : ratio(previous.clickedQueries(), previous.searches())));
        report.addKpi(new Kpi(AVG_RESPONSE_TIME, AnalyticsReport.UNIT_MS, current.avgResponseTime(),
                previous == null ? null : previous.avgResponseTime()));

        final List<Series> series = new ArrayList<>();
        for (final Map.Entry<String, List<Number>> entry : current.series().entrySet()) {
            final Series s = new Series(entry.getKey(), entry.getValue());
            if (previous != null) {
                s.setPrevious(alignTo(previous.series().get(entry.getKey()), entry.getValue().size()));
            }
            series.add(s);
        }
        report.putChart(TREND, new Chart(LINE, AnalyticsReport.UNIT_MIXED, bucketLabels(slots, cond.getInterval()), series).selectable());

        report.putTable("topQueries", withBars(termsRows(search, "topQueries", WORD), COUNT));
        report.putTable("zeroHitQueries", withBars(termsRows(subAggs(search, ZERO_HIT), WORDS, WORD), COUNT));
        return report;
    }

    private Aggregations overviewSearchAggs(final AnalyticsCondition cond, final ZonedDateTime start, final ZonedDateTime end,
            final boolean withTables) {
        return searchLogAggs(start, end, cond.getAccessType(), cb -> {
            cb.aggregation().setRequestedAt_DateHistogram(TREND, op -> {
                applyInterval(op, cond.getInterval(), cond.getZoneId());
                op.subAggregation(AggregationBuilders.cardinality(USERS).field(USER_INFO_ID));
                op.subAggregation(AggregationBuilders.filter(ZERO_HIT, QueryBuilders.termQuery(HIT_COUNT, 0)));
                op.subAggregation(AggregationBuilders.avg(AVG_RESPONSE_TIME).field(RESPONSE_TIME));
            }, null);
            cb.aggregation().setUserInfoId_Cardinality(USERS, null);
            cb.aggregation().setResponseTime_Avg(AVG_RESPONSE_TIME, null);
            if (withTables) {
                cb.aggregation().setSearchWord_Terms("topQueries", op -> applyTermsSize(op, cond.getSize()), null);
                cb.aggregation()
                        .filter(ZERO_HIT, cq -> cq.setHitCount_Equal(0L), null,
                                ca -> ca.setSearchWord_Terms(WORDS, op -> applyTermsSize(op, cond.getSize()), null));
            } else {
                cb.aggregation().filter(ZERO_HIT, cq -> cq.setHitCount_Equal(0L), null, null);
            }
        });
    }

    private Aggregations overviewClickAggs(final AnalyticsCondition cond, final ZonedDateTime start, final ZonedDateTime end) {
        return clickLogAggs(start, end, cond.getAccessType(), cb -> {
            cb.aggregation().setRequestedAt_DateHistogram(TREND, op -> {
                applyInterval(op, cond.getInterval(), cond.getZoneId());
                op.subAggregation(AggregationBuilders.cardinality(CLICKED_QUERIES).field("queryId"));
            }, null);
            cb.aggregation().setQueryId_Cardinality(CLICKED_QUERIES, null);
        });
    }

    private static OverviewPeriod readOverview(final Aggregations search, final Aggregations click, final List<ZonedDateTime> slots) {
        final Map<Long, Histogram.Bucket> searchBuckets = histogramBuckets(search, TREND);
        final Map<Long, Histogram.Bucket> clickBuckets = histogramBuckets(click, TREND);
        final List<Number> searches = new ArrayList<>();
        final List<Number> users = new ArrayList<>();
        final List<Number> zeroHitRate = new ArrayList<>();
        final List<Number> ctr = new ArrayList<>();
        final List<Number> avgResponseTime = new ArrayList<>();
        for (final ZonedDateTime slot : slots) {
            final long key = slot.toInstant().toEpochMilli();
            final Histogram.Bucket bucket = searchBuckets.get(key);
            final Histogram.Bucket clickBucket = clickBuckets.get(key);
            final long count = bucket == null ? 0L : bucket.getDocCount();
            final Aggregations aggs = bucket == null ? null : bucket.getAggregations();
            searches.add(count);
            users.add(cardinality(aggs, USERS));
            zeroHitRate.add(ratio(docCount(aggs, ZERO_HIT), count));
            ctr.add(ratio(cardinality(clickBucket == null ? null : clickBucket.getAggregations(), CLICKED_QUERIES), count));
            avgResponseTime.add(value(aggs, AVG_RESPONSE_TIME));
        }
        final Map<String, List<Number>> series = new LinkedHashMap<>();
        series.put(SEARCHES, searches);
        series.put(USERS, users);
        series.put(ZERO_HIT_RATE, zeroHitRate);
        series.put(CTR, ctr);
        series.put(AVG_RESPONSE_TIME, avgResponseTime);
        return new OverviewPeriod(totalDocCount(searchBuckets), cardinality(search, USERS), docCount(search, ZERO_HIT),
                cardinality(click, CLICKED_QUERIES), value(search, AVG_RESPONSE_TIME), series);
    }

    private record OverviewPeriod(long searches, long users, long zeroHits, long clickedQueries, Number avgResponseTime,
            Map<String, List<Number>> series) {
    }

    // ===================================================================================
    //                                                                              Queries
    //                                                                             =======

    /**
     * Builds the queries tab: the query table with click metrics, zero-hit queries and zero-click queries.
     *
     * @param cond the analytics condition
     * @return the report
     */
    protected AnalyticsReport buildQueries(final AnalyticsCondition cond) {
        final AnalyticsReport report = newReport(TAB_QUERIES, cond);
        final ZonedDateTime earliestClick = earliestEnrichedClick(cond);
        addNotice(report, clickCoverageNotice(cond.getStart(), earliestClick, DATE_TIME));
        final boolean hasClickData = earliestClick != null;
        final ZonedDateTime zeroClickStart = hasClickData && earliestClick.isAfter(cond.getStart()) ? earliestClick : cond.getStart();
        final int size = cond.getSize();

        final Aggregations search = searchLogAggs(cond.getStart(), cond.getEnd(), cond.getAccessType(), cb -> {
            cb.aggregation().setSearchWord_Terms(WORDS, op -> applyTermsSize(op, size), ca -> {
                ca.setUserInfoId_Cardinality(USERS, null);
                ca.setHitCount_Avg(AVG_HITS, null);
            });
            cb.aggregation()
                    .filter(ZERO_HIT, cq -> cq.setHitCount_Equal(0L), null, ca -> ca.setSearchWord_Terms(WORDS,
                            op -> applyTermsSize(op, size), sub -> sub.setUserInfoId_Cardinality(USERS, null)));
            if (hasClickData) {
                cb.aggregation().filter(ZERO_CLICK, cq -> {
                    cq.setHitCount_GreaterThan(0L);
                    cq.setRequestedAt_GreaterEqual(AnalyticsCondition.toUtc(zeroClickStart));
                }, null, ca -> ca.setSearchWord_Terms(WORDS, op -> applyTermsSize(op, ZERO_CLICK_CANDIDATES),
                        sub -> sub.addAggregation(AggregationBuilders.max(LAST_SEARCHED_AT).field(REQUESTED_AT))));
            }
        });

        final List<String> words = new ArrayList<>();
        termsBuckets(search, WORDS).forEach(b -> words.add(b.getKeyAsString()));
        final List<Map<String, Object>> candidates = new ArrayList<>();
        for (final Terms.Bucket bucket : termsBuckets(subAggs(search, ZERO_CLICK), WORDS)) {
            final Map<String, Object> row = new LinkedHashMap<>();
            row.put(WORD, bucket.getKeyAsString());
            row.put(COUNT, bucket.getDocCount());
            final Number last = value(bucket.getAggregations(), LAST_SEARCHED_AT);
            row.put(LAST_SEARCHED_AT,
                    last == null ? null : DATE_TIME.format(Instant.ofEpochMilli(last.longValue()).atZone(cond.getZoneId())));
            candidates.add(row);
        }
        final List<String> candidateWords = new ArrayList<>();
        candidates.forEach(row -> candidateWords.add((String) row.get(WORD)));

        Aggregations click = null;
        if (hasClickData && (!words.isEmpty() || !candidateWords.isEmpty())) {
            click = clickLogAggs(cond.getStart(), cond.getEnd(), cond.getAccessType(), cb -> {
                if (!words.isEmpty()) {
                    cb.aggregation()
                            .filter(WORDS, cq -> cq.setSearchWord_Terms(words), null,
                                    ca -> ca.setSearchWord_Terms(WORDS, op -> applyTermsSize(op, words.size()), sub -> {
                                        sub.setQueryId_Cardinality(CLICKED_QUERIES, null);
                                        sub.setRank_Avg(AVG_RANK, null);
                                    }));
                }
                if (!candidateWords.isEmpty()) {
                    cb.aggregation().filter(ZERO_CLICK, cq -> {
                        cq.setSearchWord_Terms(candidateWords);
                        cq.setRequestedAt_GreaterEqual(AnalyticsCondition.toUtc(zeroClickStart));
                    }, null, ca -> ca.setSearchWord_Terms(WORDS, op -> applyTermsSize(op, candidateWords.size()), null));
                }
            });
        }

        final Map<String, Terms.Bucket> clicksByWord = new HashMap<>();
        termsBuckets(subAggs(click, WORDS), WORDS).forEach(b -> clicksByWord.put(b.getKeyAsString(), b));
        final List<Map<String, Object>> queries = new ArrayList<>();
        for (final Terms.Bucket bucket : termsBuckets(search, WORDS)) {
            final Map<String, Object> row = new LinkedHashMap<>();
            row.put(WORD, bucket.getKeyAsString());
            row.put(COUNT, bucket.getDocCount());
            row.put(USERS, cardinality(bucket.getAggregations(), USERS));
            row.put(AVG_HITS, value(bucket.getAggregations(), AVG_HITS));
            if (hasClickData) {
                final Terms.Bucket clickBucket = clicksByWord.get(bucket.getKeyAsString());
                final Aggregations clickAggs = clickBucket == null ? null : clickBucket.getAggregations();
                row.put(CLICKS, clickBucket == null ? 0L : clickBucket.getDocCount());
                row.put(CTR, ratio(cardinality(clickAggs, CLICKED_QUERIES), bucket.getDocCount()));
                row.put(AVG_RANK, value(clickAggs, AVG_RANK));
            } else {
                row.put(CLICKS, null);
                row.put(CTR, null);
                row.put(AVG_RANK, null);
            }
            queries.add(row);
        }
        report.putTable(TAB_QUERIES, queries);

        final List<Map<String, Object>> zeroHits = new ArrayList<>();
        for (final Terms.Bucket bucket : termsBuckets(subAggs(search, ZERO_HIT), WORDS)) {
            final Map<String, Object> row = new LinkedHashMap<>();
            row.put(WORD, bucket.getKeyAsString());
            row.put(COUNT, bucket.getDocCount());
            row.put(USERS, cardinality(bucket.getAggregations(), USERS));
            zeroHits.add(row);
        }
        report.putTable("zeroHitQueries", zeroHits);

        final Set<String> clickedWords = new HashSet<>();
        termsBuckets(subAggs(click, ZERO_CLICK), WORDS).forEach(b -> clickedWords.add(b.getKeyAsString()));
        report.putTable("zeroClickQueries", hasClickData ? zeroClickWords(candidates, clickedWords, size) : new ArrayList<>());
        return report;
    }

    /**
     * Finds the oldest click before the end of the period that carries the search word, which tells since when
     * click logs are enriched. There is no lower time bound and the access type filter is not applied.
     *
     * @param cond the analytics condition
     * @return the time in the condition zone, or null when no such click was recorded before the end of the period
     */
    protected ZonedDateTime earliestEnrichedClick(final AnalyticsCondition cond) {
        final EsPagingResultBean<ClickLog> result = (EsPagingResultBean<ClickLog>) clickLogBhv.selectPage(cb -> {
            cb.fetchFirst(0);
            cb.query().setRequestedAt_LessThan(AnalyticsCondition.toUtc(cond.getEnd()));
            cb.query().setSearchWord_Exists();
            cb.aggregation().addAggregation(AggregationBuilders.min(EARLIEST).field(REQUESTED_AT));
        });
        final Number millis = value(result.getAggregations(), EARLIEST);
        return millis == null ? null : Instant.ofEpochMilli(millis.longValue()).atZone(cond.getZoneId());
    }

    // ===================================================================================
    //                                                                               Clicks
    //                                                                              ======

    /**
     * Builds the clicks tab: click KPIs, the rank distribution, the paging rate and the URL rankings.
     *
     * @param cond the analytics condition
     * @return the report
     */
    protected AnalyticsReport buildClicks(final AnalyticsCondition cond) {
        final AnalyticsReport report = newReport(TAB_CLICKS, cond);
        addNotice(report, clickCoverageNotice(cond.getStart(), earliestEnrichedClick(cond), DATE_TIME));
        final List<ZonedDateTime> slots = bucketSlots(cond.getStart(), cond.getEnd(), cond.getInterval());

        final Aggregations click = clickLogAggs(cond.getStart(), cond.getEnd(), cond.getAccessType(), cb -> {
            addClickTotals(cb);
            cb.aggregation().setRank_Histogram(RANKS, op -> {
                op.interval(1);
                op.minDocCount(1);
            }, null);
            cb.aggregation().setUrl_Terms(URLS, op -> applyTermsSize(op, cond.getSize()), null);
        });
        final Aggregations search = searchLogAggs(cond.getStart(), cond.getEnd(), cond.getAccessType(), cb -> {
            cb.aggregation().setRequestedAt_DateHistogram(TREND, op -> {
                applyInterval(op, cond.getInterval(), cond.getZoneId());
                op.subAggregation(AggregationBuilders.filter(PAGING, QueryBuilders.rangeQuery(QUERY_OFFSET).gt(0)));
            }, null);
        });
        final Map<Long, Histogram.Bucket> searchBuckets = histogramBuckets(search, TREND);
        final long searches = totalDocCount(searchBuckets);

        Aggregations prevClick = null;
        long prevSearches = 0;
        if (cond.isCompare()) {
            prevClick = clickLogAggs(cond.getPrevStart(), cond.getPrevEnd(), cond.getAccessType(), this::addClickTotals);
            prevSearches = valueCount(searchLogAggs(cond.getPrevStart(), cond.getPrevEnd(), cond.getAccessType(),
                    cb -> cb.aggregation().setRequestedAt_Count(SEARCHES, null)), SEARCHES);
        }
        final boolean compare = cond.isCompare();
        report.addKpi(
                new Kpi(CLICKS, AnalyticsReport.UNIT_COUNT, valueCount(click, CLICKS), compare ? valueCount(prevClick, CLICKS) : null));
        report.addKpi(new Kpi(CTR, AnalyticsReport.UNIT_PERCENT, ratio(cardinality(click, CLICKED_QUERIES), searches),
                compare ? ratio(cardinality(prevClick, CLICKED_QUERIES), prevSearches) : null));
        report.addKpi(new Kpi(AVG_RANK, AnalyticsReport.UNIT_RANK, value(click, AVG_RANK), compare ? value(prevClick, AVG_RANK) : null));

        final Map<Long, Long> countsByRank = new HashMap<>();
        for (final Histogram.Bucket bucket : histogramBucketList(click, RANKS)) {
            countsByRank.merge(((Number) bucket.getKey()).longValue(), bucket.getDocCount(), Long::sum);
        }
        final Map<String, Long> distribution = foldRankDistribution(countsByRank);
        report.putChart("rankDistribution", new Chart(BAR, AnalyticsReport.UNIT_COUNT, new ArrayList<>(distribution.keySet()),
                List.of(new Series(CLICKS, new ArrayList<>(distribution.values())))));

        final List<Number> pagingRate = new ArrayList<>();
        for (final ZonedDateTime slot : slots) {
            final Histogram.Bucket bucket = searchBuckets.get(slot.toInstant().toEpochMilli());
            pagingRate.add(bucket == null ? null : ratio(docCount(bucket.getAggregations(), PAGING), bucket.getDocCount()));
        }
        report.putChart("pagingRate", new Chart(LINE, AnalyticsReport.UNIT_PERCENT, bucketLabels(slots, cond.getInterval()),
                List.of(new Series("pagingRate", pagingRate))));

        report.putTable("clickedUrls", withBars(termsRows(click, URLS, URL), COUNT));
        final Aggregations favorite = favoriteLogAggs(cond.getStart(), cond.getEnd(),
                cb -> cb.aggregation().setUrl_Terms(URLS, op -> applyTermsSize(op, cond.getSize()), null));
        report.putTable("favoriteUrls", withBars(termsRows(favorite, URLS, URL), COUNT));
        return report;
    }

    private void addClickTotals(final ClickLogCB cb) {
        cb.aggregation().setRequestedAt_Count(CLICKS, null);
        cb.aggregation().setQueryId_Cardinality(CLICKED_QUERIES, null);
        cb.aggregation().setRank_Avg(AVG_RANK, null);
    }

    // ===================================================================================
    //                                                                          Performance
    //                                                                         ===========

    /**
     * Builds the performance tab: response and query time trends, the response time distribution and slow queries.
     *
     * @param cond the analytics condition
     * @return the report
     */
    protected AnalyticsReport buildPerformance(final AnalyticsCondition cond) {
        final AnalyticsReport report = newReport(TAB_PERFORMANCE, cond);
        final List<ZonedDateTime> slots = bucketSlots(cond.getStart(), cond.getEnd(), cond.getInterval());
        final Aggregations search = searchLogAggs(cond.getStart(), cond.getEnd(), cond.getAccessType(), cb -> {
            cb.aggregation().setRequestedAt_DateHistogram(TREND, op -> {
                applyInterval(op, cond.getInterval(), cond.getZoneId());
                op.subAggregation(AggregationBuilders.avg(AVG_RESPONSE_TIME).field(RESPONSE_TIME));
                op.subAggregation(AggregationBuilders.percentiles(PERCENTILES).field(RESPONSE_TIME).percentiles(50, 95, 99));
                op.subAggregation(AggregationBuilders.avg(AVG_QUERY_TIME).field(QUERY_TIME));
            }, null);
            cb.aggregation().setResponseTime_Range("responseTimes", op -> {
                op.addUnboundedTo(100);
                op.addRange(100, 300);
                op.addRange(300, 1000);
                op.addRange(1000, 3000);
                op.addUnboundedFrom(3000);
            }, null);
            cb.aggregation().setSearchWord_Terms("slowQueries", op -> {
                applyTermsSize(op, cond.getSize());
                op.minDocCount(SLOW_QUERY_MIN_SEARCHES);
                op.order(BucketOrder.aggregation(AVG_RESPONSE_TIME, false));
            }, ca -> ca.setResponseTime_Avg(AVG_RESPONSE_TIME, null));
        });

        final Map<Long, Histogram.Bucket> buckets = histogramBuckets(search, TREND);
        final List<Number> avg = new ArrayList<>();
        final List<Number> p50 = new ArrayList<>();
        final List<Number> p95 = new ArrayList<>();
        final List<Number> p99 = new ArrayList<>();
        final List<Number> avgQueryTime = new ArrayList<>();
        for (final ZonedDateTime slot : slots) {
            final Histogram.Bucket bucket = buckets.get(slot.toInstant().toEpochMilli());
            final Aggregations aggs = bucket == null ? null : bucket.getAggregations();
            avg.add(value(aggs, AVG_RESPONSE_TIME));
            p50.add(percentile(aggs, PERCENTILES, 50));
            p95.add(percentile(aggs, PERCENTILES, 95));
            p99.add(percentile(aggs, PERCENTILES, 99));
            avgQueryTime.add(value(aggs, AVG_QUERY_TIME));
        }
        final List<String> labels = bucketLabels(slots, cond.getInterval());
        report.putChart("responseTime", new Chart(LINE, AnalyticsReport.UNIT_MS, labels,
                List.of(new Series("avg", avg), new Series("p50", p50), new Series("p95", p95), new Series("p99", p99))));
        report.putChart("queryTime", new Chart(LINE, AnalyticsReport.UNIT_MS, labels, List.of(new Series(AVG_QUERY_TIME, avgQueryTime))));

        final List<Number> distribution = new ArrayList<>();
        final Range range = search == null ? null : search.get("responseTimes");
        if (range != null) {
            range.getBuckets().forEach(b -> distribution.add(b.getDocCount()));
        }
        report.putChart("responseTimeDistribution", new Chart(BAR, AnalyticsReport.UNIT_COUNT, RESPONSE_TIME_BUCKETS,
                List.of(new Series(SEARCHES, alignTo(distribution, RESPONSE_TIME_BUCKETS.size(), 0L)))));

        final List<Map<String, Object>> slowQueries = new ArrayList<>();
        for (final Terms.Bucket bucket : termsBuckets(search, "slowQueries")) {
            final Map<String, Object> row = new LinkedHashMap<>();
            row.put(WORD, bucket.getKeyAsString());
            row.put(COUNT, bucket.getDocCount());
            row.put(AVG_RESPONSE_TIME, value(bucket.getAggregations(), AVG_RESPONSE_TIME));
            slowQueries.add(row);
        }
        report.putTable("slowQueries", slowQueries);
        return report;
    }

    // ===================================================================================
    //                                                                             Audience
    //                                                                            ========

    /**
     * Builds the audience tab: new and returning users, access types, the weekday-hour heatmap and environment rankings.
     *
     * @param cond the analytics condition
     * @return the report
     */
    protected AnalyticsReport buildAudience(final AnalyticsCondition cond) {
        final AnalyticsReport report = newReport(TAB_AUDIENCE, cond);
        final List<ZonedDateTime> slots = bucketSlots(cond.getStart(), cond.getEnd(), cond.getInterval());
        final int size = cond.getSize();
        final Aggregations search = searchLogAggs(cond.getStart(), cond.getEnd(), cond.getAccessType(), cb -> {
            cb.aggregation().setRequestedAt_DateHistogram(TREND, op -> {
                applyInterval(op, cond.getInterval(), cond.getZoneId());
                op.subAggregation(AggregationBuilders.cardinality(USERS).field(USER_INFO_ID));
            }, null);
            cb.aggregation().setRequestedAt_DateHistogram(HOURLY, op -> {
                op.calendarInterval(DateHistogramInterval.HOUR);
                op.timeZone(cond.getZoneId());
                op.minDocCount(1);
            }, null);
            cb.aggregation().setAccessType_Terms(ACCESS_TYPES, op -> applyTermsSize(op, ACCESS_TYPE_SIZE), null);
            cb.aggregation().setUserAgent_Terms("userAgents", op -> applyTermsSize(op, size), null);
            cb.aggregation().setReferer_Terms("referers", op -> applyTermsSize(op, size), null);
            cb.aggregation().setLanguages_Terms("languages", op -> applyTermsSize(op, size), null);
            cb.aggregation().setVirtualHost_Terms("virtualHosts", op -> applyTermsSize(op, size), null);
        });
        final Aggregations userInfo = userInfoAggs(cond.getStart(), cond.getEnd(), cb -> cb.aggregation()
                .setCreatedAt_DateHistogram(TREND, op -> applyInterval(op, cond.getInterval(), cond.getZoneId()), null));

        final Map<Long, Histogram.Bucket> searchBuckets = histogramBuckets(search, TREND);
        final Map<Long, Histogram.Bucket> userBuckets = histogramBuckets(userInfo, TREND);
        final List<Number> newUsers = new ArrayList<>();
        final List<Number> returningUsers = new ArrayList<>();
        for (final ZonedDateTime slot : slots) {
            final long key = slot.toInstant().toEpochMilli();
            final Histogram.Bucket searchBucket = searchBuckets.get(key);
            final Histogram.Bucket userBucket = userBuckets.get(key);
            final long created = userBucket == null ? 0L : userBucket.getDocCount();
            final long users = cardinality(searchBucket == null ? null : searchBucket.getAggregations(), USERS);
            newUsers.add(created);
            returningUsers.add(Math.max(0L, users - created));
        }
        report.putChart(USERS, new Chart(LINE, AnalyticsReport.UNIT_COUNT, bucketLabels(slots, cond.getInterval()),
                List.of(new Series("newUsers", newUsers), new Series("returningUsers", returningUsers))));

        final List<String> accessTypes = new ArrayList<>();
        final List<Number> accessTypeCounts = new ArrayList<>();
        termsBuckets(search, ACCESS_TYPES).forEach(b -> {
            accessTypes.add(b.getKeyAsString());
            accessTypeCounts.add(b.getDocCount());
        });
        report.putChart(ACCESS_TYPES,
                new Chart(PIE, AnalyticsReport.UNIT_COUNT, accessTypes, List.of(new Series(SEARCHES, accessTypeCounts))));

        final List<Map.Entry<ZonedDateTime, Long>> hourly = new ArrayList<>();
        histogramBucketList(search, HOURLY)
                .forEach(b -> hourly.add(new SimpleEntry<>(toZone(b.getKey(), cond.getZoneId()), b.getDocCount())));
        report.putTable("weekHour", weekHourRows(foldWeekHour(hourly)));

        report.putTable("userAgents", withBars(termsRows(search, "userAgents", VALUE), COUNT));
        report.putTable("referers", withBars(termsRows(search, "referers", VALUE), COUNT));
        report.putTable("languages", withBars(termsRows(search, "languages", VALUE), COUNT));
        report.putTable("virtualHosts", withBars(termsRows(search, "virtualHosts", VALUE), COUNT));
        return report;
    }

    // ===================================================================================
    //                                                                      Request Helper
    //                                                                      ==============

    /**
     * Creates a report with the notices shared by the tabs.
     *
     * @param tab the tab name
     * @param cond the analytics condition
     * @return the report
     */
    protected AnalyticsReport newReport(final String tab, final AnalyticsCondition cond) {
        final AnalyticsReport report = new AnalyticsReport();
        if (!fessConfig.isSearchLog()) {
            report.addNotice(new Notice(WARNING, "labels.searchlog_notice_search_log_disabled"));
        }
        if (!fessConfig.isUserInfo() && (TAB_OVERVIEW.equals(tab) || TAB_QUERIES.equals(tab) || TAB_AUDIENCE.equals(tab))) {
            report.addNotice(new Notice(INFO, "labels.searchlog_notice_user_info_disabled"));
        }
        if (cond.getAccessType() != null && (TAB_OVERVIEW.equals(tab) || TAB_QUERIES.equals(tab) || TAB_CLICKS.equals(tab))) {
            report.addNotice(new Notice(INFO, "labels.searchlog_notice_accesstype_clicks"));
        }
        return report;
    }

    private Aggregations searchLogAggs(final ZonedDateTime start, final ZonedDateTime end, final String accessType,
            final Consumer<SearchLogCB> setup) {
        final EsPagingResultBean<SearchLog> result = (EsPagingResultBean<SearchLog>) searchLogBhv.selectPage(cb -> {
            cb.fetchFirst(0);
            cb.query().setRequestedAt_GreaterEqual(AnalyticsCondition.toUtc(start));
            cb.query().setRequestedAt_LessThan(AnalyticsCondition.toUtc(end));
            if (accessType != null) {
                cb.query().setAccessType_Term(accessType);
            }
            setup.accept(cb);
        });
        return result.getAggregations();
    }

    private Aggregations clickLogAggs(final ZonedDateTime start, final ZonedDateTime end, final String accessType,
            final Consumer<ClickLogCB> setup) {
        final EsPagingResultBean<ClickLog> result = (EsPagingResultBean<ClickLog>) clickLogBhv.selectPage(cb -> {
            cb.fetchFirst(0);
            cb.query().setRequestedAt_GreaterEqual(AnalyticsCondition.toUtc(start));
            cb.query().setRequestedAt_LessThan(AnalyticsCondition.toUtc(end));
            if (accessType != null) {
                cb.query().setAccessType_Term(accessType);
            }
            setup.accept(cb);
        });
        return result.getAggregations();
    }

    private Aggregations favoriteLogAggs(final ZonedDateTime start, final ZonedDateTime end, final Consumer<FavoriteLogCB> setup) {
        final EsPagingResultBean<FavoriteLog> result = (EsPagingResultBean<FavoriteLog>) favoriteLogBhv.selectPage(cb -> {
            cb.fetchFirst(0);
            cb.query().setCreatedAt_GreaterEqual(AnalyticsCondition.toUtc(start));
            cb.query().setCreatedAt_LessThan(AnalyticsCondition.toUtc(end));
            setup.accept(cb);
        });
        return result.getAggregations();
    }

    private Aggregations userInfoAggs(final ZonedDateTime start, final ZonedDateTime end, final Consumer<UserInfoCB> setup) {
        final EsPagingResultBean<UserInfo> result = (EsPagingResultBean<UserInfo>) userInfoBhv.selectPage(cb -> {
            cb.fetchFirst(0);
            cb.query().setCreatedAt_GreaterEqual(AnalyticsCondition.toUtc(start));
            cb.query().setCreatedAt_LessThan(AnalyticsCondition.toUtc(end));
            setup.accept(cb);
        });
        return result.getAggregations();
    }

    /**
     * Sets the bucket interval and the time zone of a date histogram; empty buckets are kept.
     *
     * @param op the date histogram
     * @param interval the interval
     * @param zone the time zone
     */
    static void applyInterval(final DateHistogramAggregationBuilder op, final Interval interval, final ZoneId zone) {
        op.calendarInterval(interval == Interval.HOUR ? DateHistogramInterval.HOUR : DateHistogramInterval.DAY);
        op.timeZone(zone);
        op.minDocCount(0);
    }

    private void applyTermsSize(final TermsAggregationBuilder op, final int size) {
        op.size(size);
        final Integer shardSize = fessConfig.getSearchlogAggShardSizeAsInteger();
        if (shardSize != null && shardSize >= 0) {
            op.shardSize(shardSize);
        }
    }

    private static void addNotice(final AnalyticsReport report, final Notice notice) {
        if (notice != null) {
            report.addNotice(notice);
        }
    }

    /**
     * Pads or cuts values to a size with nulls.
     *
     * @param values the values (may be null)
     * @param size the size
     * @return the values of the size
     */
    static List<Number> alignTo(final List<Number> values, final int size) {
        return alignTo(values, size, null);
    }

    private static List<Number> alignTo(final List<Number> values, final int size, final Number filler) {
        final List<Number> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(values != null && i < values.size() ? values.get(i) : filler);
        }
        return list;
    }

    // ===================================================================================
    //                                                                         Pure Helper
    //                                                                         ===========

    /**
     * Divides two numbers.
     *
     * @param numerator the numerator
     * @param denominator the denominator
     * @return the ratio, or null when either is null or the denominator is 0
     */
    static Double ratio(final Number numerator, final Number denominator) {
        if (numerator == null || denominator == null || denominator.doubleValue() == 0) {
            return null;
        }
        return numerator.doubleValue() / denominator.doubleValue();
    }

    /**
     * Converts a metric value to a number, dropping the NaN or infinite value of an empty bucket.
     *
     * @param value the metric value
     * @return the value, or null when it is not finite
     */
    static Number finite(final double value) {
        return Double.isFinite(value) ? value : null;
    }

    /**
     * Converts a date histogram bucket key (UTC) to the given zone.
     *
     * @param key the bucket key
     * @param zone the zone
     * @return the key in the zone
     */
    static ZonedDateTime toZone(final Object key, final ZoneId zone) {
        return ((ZonedDateTime) key).withZoneSameInstant(zone);
    }

    /**
     * Adds a "bar" width (0 to 100, relative to the largest count) to each row.
     *
     * @param rows the rows
     * @param countKey the key of the count
     * @return the rows
     */
    static List<Map<String, Object>> withBars(final List<Map<String, Object>> rows, final String countKey) {
        long max = 0L;
        for (final Map<String, Object> row : rows) {
            max = Math.max(max, ((Number) row.get(countKey)).longValue());
        }
        for (final Map<String, Object> row : rows) {
            final long count = ((Number) row.get(countKey)).longValue();
            row.put(BAR_WIDTH, max == 0 ? 0 : (int) Math.round(count * 100.0 / max));
        }
        return rows;
    }

    /**
     * Picks the searched words that were never clicked, keeping the order of the searched rows.
     *
     * @param searchedRows the rows with a "word" key, most searched first
     * @param clickedWords the words that were clicked
     * @param size the maximum number of rows
     * @return the zero-click rows
     */
    static List<Map<String, Object>> zeroClickWords(final List<Map<String, Object>> searchedRows, final Set<String> clickedWords,
            final int size) {
        final List<Map<String, Object>> rows = new ArrayList<>();
        for (final Map<String, Object> row : searchedRows) {
            if (rows.size() >= size) {
                break;
            }
            final Object word = row.get(WORD);
            if (word != null && !clickedWords.contains(word)) {
                rows.add(row);
            }
        }
        return rows;
    }

    /**
     * Folds hourly counts into a weekday by hour grid.
     *
     * @param hourly the hourly counts keyed by times in the target zone
     * @return the [7][24] grid, row 0 = Monday
     */
    static long[][] foldWeekHour(final List<Map.Entry<ZonedDateTime, Long>> hourly) {
        final long[][] grid = new long[7][24];
        for (final Map.Entry<ZonedDateTime, Long> entry : hourly) {
            final ZonedDateTime time = entry.getKey();
            grid[time.getDayOfWeek().getValue() - 1][time.getHour()] += entry.getValue();
        }
        return grid;
    }

    /**
     * Converts a weekday by hour grid to heatmap rows.
     *
     * @param grid the [7][24] grid, row 0 = Monday
     * @return 7 rows of {"day": 1..7, "cells": 24 cells of {"count", "level": 0..4}}
     */
    static List<Map<String, Object>> weekHourRows(final long[][] grid) {
        long max = 0L;
        for (final long[] row : grid) {
            for (final long count : row) {
                max = Math.max(max, count);
            }
        }
        final List<Map<String, Object>> rows = new ArrayList<>();
        for (int day = 0; day < grid.length; day++) {
            final List<Map<String, Object>> cells = new ArrayList<>();
            for (final long count : grid[day]) {
                final Map<String, Object> cell = new LinkedHashMap<>();
                cell.put(COUNT, count);
                cell.put("level", count == 0 ? 0 : (int) Math.ceil(count * 4.0 / max));
                cells.add(cell);
            }
            final Map<String, Object> row = new LinkedHashMap<>();
            row.put("day", day + 1);
            row.put("cells", cells);
            rows.add(row);
        }
        return rows;
    }

    /**
     * Folds click counts by rank into the rank distribution buckets.
     *
     * @param countsByRank the click counts by rank (ranks below 1 are ignored)
     * @return the counts keyed "1" to "10", "11-20" and "21+", in that order
     */
    static Map<String, Long> foldRankDistribution(final Map<Long, Long> countsByRank) {
        final Map<String, Long> distribution = new LinkedHashMap<>();
        RANK_BUCKETS.forEach(key -> distribution.put(key, 0L));
        countsByRank.forEach((rank, count) -> {
            if (rank == null || rank <= 0 || count == null) {
                return;
            }
            final String key;
            if (rank <= 10) {
                key = String.valueOf(rank);
            } else if (rank <= 20) {
                key = "11-20";
            } else {
                key = "21+";
            }
            distribution.merge(key, count, Long::sum);
        });
        return distribution;
    }

    /**
     * Lists the bucket start times of the period.
     *
     * @param start the inclusive start
     * @param end the exclusive end
     * @param interval the interval
     * @return the bucket start times in the zone of start
     */
    static List<ZonedDateTime> bucketSlots(final ZonedDateTime start, final ZonedDateTime end, final Interval interval) {
        final List<ZonedDateTime> slots = new ArrayList<>();
        if (interval == Interval.HOUR) {
            for (ZonedDateTime time = start; time.isBefore(end); time = time.plusHours(1)) {
                slots.add(time);
            }
            return slots;
        }
        // each day starts at its own local start of day, so a day without a local midnight does not shift later days
        for (int i = 0;; i++) {
            final ZonedDateTime time = start.toLocalDate().plusDays(i).atStartOfDay(start.getZone());
            if (!time.isBefore(end)) {
                return slots;
            }
            slots.add(time);
        }
    }

    /**
     * Formats bucket start times as x-axis labels.
     *
     * @param keys the bucket start times in the target zone
     * @param interval the interval
     * @return "MM-dd HH:00" labels for hours, "yyyy-MM-dd" labels for days
     */
    static List<String> bucketLabels(final List<ZonedDateTime> keys, final Interval interval) {
        final DateTimeFormatter formatter = interval == Interval.HOUR ? HOUR_LABEL : DAY_LABEL;
        final List<String> labels = new ArrayList<>(keys.size());
        keys.forEach(key -> labels.add(formatter.format(key)));
        return labels;
    }

    /**
     * Creates the notice about the coverage of clicks that carry the search word and rank.
     *
     * @param start the period start
     * @param earliestEnrichedClick the oldest such click before the period end, or null when there is none
     * @param formatter the formatter of the notice argument
     * @return the notice, or null when the whole period is covered
     */
    static Notice clickCoverageNotice(final ZonedDateTime start, final ZonedDateTime earliestEnrichedClick,
            final DateTimeFormatter formatter) {
        if (earliestEnrichedClick == null) {
            return new Notice(INFO, "labels.searchlog_notice_no_click_data");
        }
        if (earliestEnrichedClick.isAfter(start)) {
            return new Notice(INFO, "labels.searchlog_notice_click_since", formatter.format(earliestEnrichedClick));
        }
        return null;
    }
}
