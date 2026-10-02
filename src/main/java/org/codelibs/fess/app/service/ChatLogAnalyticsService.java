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
import static org.codelibs.fess.app.service.SearchLogAggregationReader.histogramBuckets;
import static org.codelibs.fess.app.service.SearchLogAggregationReader.subAggs;
import static org.codelibs.fess.app.service.SearchLogAggregationReader.termsBuckets;
import static org.codelibs.fess.app.service.SearchLogAggregationReader.totalDocCount;
import static org.codelibs.fess.app.service.SearchLogAggregationReader.value;
import static org.codelibs.fess.app.service.SearchLogAggregationReader.valueCount;

import java.io.IOException;
import java.io.Writer;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.AnalyticsCondition;
import org.codelibs.fess.entity.AnalyticsReport;
import org.codelibs.fess.entity.AnalyticsReport.Chart;
import org.codelibs.fess.entity.AnalyticsReport.Kpi;
import org.codelibs.fess.entity.AnalyticsReport.Notice;
import org.codelibs.fess.entity.AnalyticsReport.Series;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.log.allcommon.EsPagingResultBean;
import org.codelibs.fess.opensearch.log.cbean.ChatLogCB;
import org.codelibs.fess.opensearch.log.cbean.ca.bs.BsChatLogCA;
import org.codelibs.fess.opensearch.log.exbhv.ChatLogBhv;
import org.codelibs.fess.opensearch.log.exentity.ChatLog;
import org.codelibs.fess.util.CsvUtil;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;
import org.codelibs.fesen.opensearch.search.aggregations.AggregationBuilders;
import org.codelibs.fesen.opensearch.search.aggregations.Aggregations;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.histogram.Histogram;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.terms.Terms;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

import com.orangesignal.csv.CsvWriter;

import jakarta.annotation.Resource;

/**
 * Builds the AI chat tab of the search log analytics from size-0 aggregations over the chat log:
 * requests, users, LLM tokens, response time and errors, the users with the most requests and the
 * breakdowns by intent and by model.
 *
 * <p>The chat log holds metadata only, never the question or the answer. Token counts are only
 * recorded when the LLM client reports them, so they may be missing or partial.</p>
 */
public class ChatLogAnalyticsService {

    private static final Logger logger = LogManager.getLogger(ChatLogAnalyticsService.class);

    /** AI chat tab. */
    public static final String TAB_CHAT = "chat";

    /** The export item that downloads the KPI cards. */
    public static final String ITEM_KPIS = SearchLogAnalyticsService.ITEM_KPIS;

    /** The trend chart. */
    public static final String TREND = "trend";

    /** The table of the users with the most requests. */
    public static final String CHAT_USERS = "chatUsers";

    /** The table of the requests by intent. */
    public static final String CHAT_INTENTS = "chatIntents";

    /** The table of the requests by model. */
    public static final String CHAT_MODELS = "chatModels";

    private static final String INFO = "info";

    private static final String WARNING = "warning";

    private static final String DANGER = "danger";

    private static final String LINE = "line";

    // field names
    private static final String USER = "user";

    private static final String USER_SESSION_ID = "userSessionId";

    private static final String STATUS = "status";

    private static final String RESPONSE_TIME = "responseTime";

    // aggregation names, row keys and metric keys
    static final String REQUESTS = "requests";

    static final String USERS = "users";

    static final String PROMPT_TOKENS = "promptTokens";

    static final String COMPLETION_TOKENS = "completionTokens";

    static final String TOTAL_TOKENS = "totalTokens";

    static final String LLM_CALLS = "llmCalls";

    static final String AVG_RESPONSE_TIME = "avgResponseTime";

    static final String ERROR_RATE = "errorRate";

    static final String COUNT = "count";

    static final String VALUE = "value";

    static final String GUEST = "guest";

    private static final String ERRORS = "errors";

    private static final String TOKEN_REPORTS = "tokenReports";

    private static final String GUESTS = "guests";

    private static final List<String> USER_COLUMNS = List.of(USER, COUNT, PROMPT_TOKENS, COMPLETION_TOKENS, TOTAL_TOKENS);

    private static final List<String> INTENT_COLUMNS = List.of(VALUE, COUNT, TOTAL_TOKENS);

    private static final List<String> MODEL_COLUMNS = List.of(VALUE, COUNT, LLM_CALLS, TOTAL_TOKENS);

    /**
     * The items of the tab that can be downloaded as CSV. A table lists its columns (the presentational
     * bar width and the guest flag are not among them); the KPI list and the chart list none.
     */
    private static final Map<String, List<String>> EXPORT_ITEMS = Map.of(ITEM_KPIS, List.of(), TREND, List.of(), CHAT_USERS, USER_COLUMNS,
            CHAT_INTENTS, INTENT_COLUMNS, CHAT_MODELS, MODEL_COLUMNS);

    /** Behavior for the chat log index. */
    @Resource
    protected ChatLogBhv chatLogBhv;

    /** Fess configuration. */
    @Resource
    protected FessConfig fessConfig;

    /**
     * Default constructor.
     */
    public ChatLogAnalyticsService() {
        // nothing
    }

    /**
     * Builds the report of the AI chat tab. Never throws: on failure the report is empty, marked as
     * failed and carries a "danger" notice.
     *
     * @param cond the analytics condition; its access type filter is not applied to chat logs
     * @return the report
     */
    public AnalyticsReport getReport(final AnalyticsCondition cond) {
        try {
            return buildChat(cond);
        } catch (final Exception e) {
            logger.warn("Failed to build the chat log analytics report.", e);
            final AnalyticsReport report = new AnalyticsReport();
            report.setFailed(true);
            report.addNotice(new Notice(DANGER, "labels.searchlog_notice_failed"));
            return report;
        }
    }

    /**
     * Checks whether an item of the AI chat tab can be downloaded as CSV.
     *
     * @param item the item: "kpis", the chart name or a table name of the tab
     * @return true if the tab has the item
     */
    public boolean isExportable(final String item) {
        return item != null && EXPORT_ITEMS.containsKey(item);
    }

    /**
     * Writes one item of the report as CSV, with raw numbers, in the same layout as the other
     * analytics tabs. The writer is flushed but not closed.
     *
     * @param report the report of the tab
     * @param item the item, see {@link #isExportable(String)}
     * @param writer the writer to write the CSV to
     * @throws IOException if writing fails
     * @throws IllegalArgumentException if the tab does not have the item
     */
    public void exportCsv(final AnalyticsReport report, final String item, final Writer writer) throws IOException {
        if (!isExportable(item)) {
            throw new IllegalArgumentException("Unknown export item: " + TAB_CHAT + "/" + item);
        }
        // not closed: that would close the writer of the caller
        @SuppressWarnings("resource")
        final CsvWriter csvWriter = new CsvWriter(writer, CsvUtil.createCsvConfig());
        if (ITEM_KPIS.equals(item)) {
            SearchLogAnalyticsService.writeKpis(csvWriter, report);
        } else if (TREND.equals(item)) {
            final Chart chart = report.getCharts().get(item);
            if (chart != null) {
                SearchLogAnalyticsService.writeChart(csvWriter, chart);
            }
        } else {
            SearchLogAnalyticsService.writeTable(csvWriter, EXPORT_ITEMS.get(item), report.getTables().get(item));
        }
        csvWriter.flush();
    }

    /**
     * Builds the AI chat tab: KPIs, the trend chart, the top users and the breakdowns by intent and by model.
     *
     * @param cond the analytics condition
     * @return the report
     */
    protected AnalyticsReport buildChat(final AnalyticsCondition cond) {
        final AnalyticsReport report = newReport();
        final List<ZonedDateTime> slots = SearchLogAnalyticsService.bucketSlots(cond.getStart(), cond.getEnd(), cond.getInterval());
        final Aggregations aggs = chatAggs(cond, cond.getStart(), cond.getEnd(), true);
        final ChatPeriod current = readPeriod(aggs, slots);
        ChatPeriod previous = null;
        if (cond.isCompare()) {
            previous = readPeriod(chatAggs(cond, cond.getPrevStart(), cond.getPrevEnd(), false),
                    SearchLogAnalyticsService.bucketSlots(cond.getPrevStart(), cond.getPrevEnd(), cond.getInterval()));
        }
        addKpis(report, current, previous);

        final List<Series> series = new ArrayList<>();
        for (final Map.Entry<String, List<Number>> entry : current.series().entrySet()) {
            final Series s = new Series(entry.getKey(), entry.getValue());
            if (previous != null) {
                s.setPrevious(SearchLogAnalyticsService.alignTo(previous.series().get(entry.getKey()), entry.getValue().size()));
            }
            series.add(s);
        }
        report.putChart(TREND,
                new Chart(LINE, AnalyticsReport.UNIT_MIXED, SearchLogAnalyticsService.bucketLabels(slots, cond.getInterval()), series)
                        .selectable());

        final List<Map<String, Object>> users = new ArrayList<>();
        for (final Terms.Bucket bucket : termsBuckets(aggs, CHAT_USERS)) {
            users.add(userRow(bucket.getKeyAsString(), bucket.getDocCount(), bucket.getAggregations(), false));
        }
        final long guestRequests = docCount(aggs, GUESTS);
        if (guestRequests > 0) {
            users.add(userRow(Constants.GUEST_USER, guestRequests, subAggs(aggs, GUESTS), true));
        }
        report.putTable(CHAT_USERS, SearchLogAnalyticsService.withBars(users, COUNT));

        final List<Map<String, Object>> intents = new ArrayList<>();
        for (final Terms.Bucket bucket : termsBuckets(aggs, CHAT_INTENTS)) {
            final Map<String, Object> row = new LinkedHashMap<>();
            row.put(VALUE, bucket.getKeyAsString());
            row.put(COUNT, bucket.getDocCount());
            row.put(TOTAL_TOKENS, tokens(bucket.getAggregations()));
            intents.add(row);
        }
        report.putTable(CHAT_INTENTS, SearchLogAnalyticsService.withBars(intents, COUNT));

        final List<Map<String, Object>> models = new ArrayList<>();
        for (final Terms.Bucket bucket : termsBuckets(aggs, CHAT_MODELS)) {
            final Map<String, Object> row = new LinkedHashMap<>();
            row.put(VALUE, bucket.getKeyAsString());
            row.put(COUNT, bucket.getDocCount());
            row.put(LLM_CALLS, toLong(value(bucket.getAggregations(), LLM_CALLS)));
            row.put(TOTAL_TOKENS, tokens(bucket.getAggregations()));
            models.add(row);
        }
        report.putTable(CHAT_MODELS, SearchLogAnalyticsService.withBars(models, COUNT));
        return report;
    }

    /**
     * Creates the report with the notices of the tab.
     *
     * @return the report
     */
    protected AnalyticsReport newReport() {
        final AnalyticsReport report = new AnalyticsReport();
        if (!fessConfig.isRagChatLogEnabled()) {
            report.addNotice(new Notice(WARNING, "labels.searchlog_notice_chat_log_disabled"));
        }
        if (!fessConfig.isUserInfo()) {
            report.addNotice(new Notice(INFO, "labels.searchlog_notice_user_info_disabled"));
        }
        report.addNotice(new Notice(INFO, "labels.searchlog_notice_chat_tokens"));
        return report;
    }

    private Aggregations chatAggs(final AnalyticsCondition cond, final ZonedDateTime start, final ZonedDateTime end,
            final boolean withTables) {
        return chatLogAggs(start, end, cb -> {
            cb.aggregation().setRequestedAt_DateHistogram(TREND, op -> {
                SearchLogAnalyticsService.applyInterval(op, cond.getInterval(), cond.getZoneId());
                op.subAggregation(AggregationBuilders.cardinality(USERS).field(USER_SESSION_ID));
                op.subAggregation(AggregationBuilders.sum(TOTAL_TOKENS).field(TOTAL_TOKENS));
                op.subAggregation(AggregationBuilders.avg(AVG_RESPONSE_TIME).field(RESPONSE_TIME));
                op.subAggregation(AggregationBuilders.filter(ERRORS, QueryBuilders.termQuery(STATUS, ChatLog.STATUS_ERROR)));
            }, null);
            cb.aggregation().setUserSessionId_Cardinality(USERS, null);
            cb.aggregation().setTotalTokens_Sum(TOTAL_TOKENS, null);
            cb.aggregation().setTotalTokens_Count(TOKEN_REPORTS, null);
            cb.aggregation().setResponseTime_Avg(AVG_RESPONSE_TIME, null);
            cb.aggregation().filter(ERRORS, cq -> cq.setStatus_Term(ChatLog.STATUS_ERROR), null, null);
            if (withTables) {
                cb.aggregation().setUser_Terms(CHAT_USERS, op -> applyTermsSize(op, cond.getSize()), this::addTokenSums);
                cb.aggregation()
                        .addAggregation(AggregationBuilders.missing(GUESTS)
                                .field(USER)
                                .subAggregation(AggregationBuilders.sum(PROMPT_TOKENS).field(PROMPT_TOKENS))
                                .subAggregation(AggregationBuilders.sum(COMPLETION_TOKENS).field(COMPLETION_TOKENS))
                                .subAggregation(AggregationBuilders.sum(TOTAL_TOKENS).field(TOTAL_TOKENS))
                                .subAggregation(AggregationBuilders.count(TOKEN_REPORTS).field(TOTAL_TOKENS)));
                cb.aggregation().setIntent_Terms(CHAT_INTENTS, op -> applyTermsSize(op, cond.getSize()), ca -> {
                    ca.setTotalTokens_Sum(TOTAL_TOKENS, null);
                    ca.setTotalTokens_Count(TOKEN_REPORTS, null);
                });
                cb.aggregation().setModel_Terms(CHAT_MODELS, op -> applyTermsSize(op, cond.getSize()), ca -> {
                    ca.setLlmCalls_Sum(LLM_CALLS, null);
                    ca.setTotalTokens_Sum(TOTAL_TOKENS, null);
                    ca.setTotalTokens_Count(TOKEN_REPORTS, null);
                });
            }
        });
    }

    private void addTokenSums(final BsChatLogCA ca) {
        ca.setPromptTokens_Sum(PROMPT_TOKENS, null);
        ca.setCompletionTokens_Sum(COMPLETION_TOKENS, null);
        ca.setTotalTokens_Sum(TOTAL_TOKENS, null);
        ca.setTotalTokens_Count(TOKEN_REPORTS, null);
    }

    /**
     * Runs a size-0 aggregation over the chat logs requested in a period.
     *
     * @param start the inclusive start
     * @param end the exclusive end
     * @param setup sets up the aggregations
     * @return the aggregations
     */
    protected Aggregations chatLogAggs(final ZonedDateTime start, final ZonedDateTime end, final Consumer<ChatLogCB> setup) {
        final EsPagingResultBean<ChatLog> result = (EsPagingResultBean<ChatLog>) chatLogBhv.selectPage(cb -> {
            cb.fetchFirst(0);
            cb.query().setRequestedAt_GreaterEqual(AnalyticsCondition.toUtc(start));
            cb.query().setRequestedAt_LessThan(AnalyticsCondition.toUtc(end));
            setup.accept(cb);
        });
        return result.getAggregations();
    }

    private void applyTermsSize(final TermsAggregationBuilder op, final int size) {
        op.size(size);
        final Integer shardSize = fessConfig.getSearchlogAggShardSizeAsInteger();
        if (shardSize != null && shardSize >= 0) {
            op.shardSize(shardSize);
        }
    }

    private static ChatPeriod readPeriod(final Aggregations aggs, final List<ZonedDateTime> slots) {
        final Map<Long, Histogram.Bucket> buckets = histogramBuckets(aggs, TREND);
        final List<Number> requests = new ArrayList<>();
        final List<Number> users = new ArrayList<>();
        final List<Number> totalTokens = new ArrayList<>();
        final List<Number> avgResponseTime = new ArrayList<>();
        final List<Number> errorRate = new ArrayList<>();
        for (final ZonedDateTime slot : slots) {
            final Histogram.Bucket bucket = buckets.get(slot.toInstant().toEpochMilli());
            final long count = bucket == null ? 0L : bucket.getDocCount();
            final Aggregations bucketAggs = bucket == null ? null : bucket.getAggregations();
            requests.add(count);
            users.add(cardinality(bucketAggs, USERS));
            totalTokens.add(toLong(value(bucketAggs, TOTAL_TOKENS)));
            avgResponseTime.add(value(bucketAggs, AVG_RESPONSE_TIME));
            errorRate.add(SearchLogAnalyticsService.ratio(docCount(bucketAggs, ERRORS), count));
        }
        final Map<String, List<Number>> series = new LinkedHashMap<>();
        series.put(REQUESTS, requests);
        series.put(USERS, users);
        series.put(TOTAL_TOKENS, totalTokens);
        series.put(AVG_RESPONSE_TIME, avgResponseTime);
        series.put(ERROR_RATE, errorRate);
        return new ChatPeriod(totalDocCount(buckets), cardinality(aggs, USERS), tokens(aggs), value(aggs, AVG_RESPONSE_TIME),
                docCount(aggs, ERRORS), series);
    }

    /**
     * Adds the KPI cards of the tab: requests, users, total tokens, average response time and error rate.
     *
     * @param report the report
     * @param current the current period
     * @param previous the previous period, or null when not compared
     */
    static void addKpis(final AnalyticsReport report, final ChatPeriod current, final ChatPeriod previous) {
        report.addKpi(new Kpi(REQUESTS, AnalyticsReport.UNIT_COUNT, current.requests(), previous == null ? null : previous.requests()));
        report.addKpi(new Kpi(USERS, AnalyticsReport.UNIT_COUNT, current.users(), previous == null ? null : previous.users()));
        report.addKpi(
                new Kpi(TOTAL_TOKENS, AnalyticsReport.UNIT_COUNT, current.totalTokens(), previous == null ? null : previous.totalTokens()));
        report.addKpi(new Kpi(AVG_RESPONSE_TIME, AnalyticsReport.UNIT_MS, current.avgResponseTime(),
                previous == null ? null : previous.avgResponseTime()));
        report.addKpi(
                new Kpi(ERROR_RATE, AnalyticsReport.UNIT_PERCENT, SearchLogAnalyticsService.ratio(current.errors(), current.requests()),
                        previous == null ? null : SearchLogAnalyticsService.ratio(previous.errors(), previous.requests())));
    }

    /**
     * Creates a row of the user table.
     *
     * @param user the user, or {@link Constants#GUEST_USER} for the row of the requests without a signed-in user
     * @param requests the number of requests
     * @param aggs the token sums of the user
     * @param guest true for the row of the requests without a signed-in user
     * @return the row
     */
    static Map<String, Object> userRow(final String user, final long requests, final Aggregations aggs, final boolean guest) {
        final Map<String, Object> row = new LinkedHashMap<>();
        row.put(USER, user);
        row.put(COUNT, requests);
        final boolean reported = valueCount(aggs, TOKEN_REPORTS) > 0;
        row.put(PROMPT_TOKENS, reported ? toLong(value(aggs, PROMPT_TOKENS)) : null);
        row.put(COMPLETION_TOKENS, reported ? toLong(value(aggs, COMPLETION_TOKENS)) : null);
        row.put(TOTAL_TOKENS, reported ? toLong(value(aggs, TOTAL_TOKENS)) : null);
        row.put(GUEST, guest);
        return row;
    }

    /**
     * Reads the sum of the total tokens, which is unknown (null) when no request reported tokens.
     *
     * @param aggs the aggregations with the total token sum and count
     * @return the sum, or null when no request reported tokens
     */
    static Long tokens(final Aggregations aggs) {
        return valueCount(aggs, TOKEN_REPORTS) > 0 ? toLong(value(aggs, TOTAL_TOKENS)) : null;
    }

    /**
     * Converts a metric value to a long.
     *
     * @param value the value (may be null)
     * @return the rounded value, or null
     */
    static Long toLong(final Number value) {
        return value == null ? null : Math.round(value.doubleValue());
    }

    /**
     * The values of the AI chat tab for one period.
     *
     * @param requests the number of chat requests
     * @param users the number of distinct users
     * @param totalTokens the sum of the total tokens, or null when no request reported tokens
     * @param avgResponseTime the average response time in milliseconds, or null without requests
     * @param errors the number of failed requests
     * @param series the trend series by metric key
     */
    record ChatPeriod(long requests, long users, Long totalTokens, Number avgResponseTime, long errors, Map<String, List<Number>> series) {
    }
}
