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

import java.io.StringReader;
import java.io.StringWriter;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.codelibs.fess.entity.AnalyticsCondition;
import org.codelibs.fess.entity.AnalyticsReport;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.log.cbean.ChatLogCB;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.CsvUtil;
import org.codelibs.fesen.opensearch.search.aggregations.AbstractAggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.Aggregations;
import org.junit.jupiter.api.Test;

import com.orangesignal.csv.CsvReader;

public class ChatLogAnalyticsServiceTest extends UnitFessTestCase {

    private static AnalyticsCondition condition(final String compare) {
        final Clock clock =
                Clock.fixed(ZonedDateTime.of(2026, 9, 30, 10, 0, 0, 0, ZoneId.of("Asia/Tokyo")).toInstant(), ZoneId.of("Asia/Tokyo"));
        return AnalyticsCondition.create("custom", "2026-09-01", "2026-09-07", compare, "json", null, 25, clock);
    }

    private static ChatLogAnalyticsService service(final boolean chatLogEnabled, final boolean userInfo, final List<String> aggNames) {
        final ChatLogAnalyticsService service = new ChatLogAnalyticsService() {
            @Override
            protected Aggregations chatLogAggs(final ZonedDateTime start, final ZonedDateTime end, final Consumer<ChatLogCB> setup) {
                // build the aggregations against a real condition bean, but answer as an empty index would
                final ChatLogCB cb = new ChatLogCB();
                setup.accept(cb);
                for (final AbstractAggregationBuilder<?> builder : cb.aggregation().getAggregationBuilderList()) {
                    aggNames.add(builder.getName());
                }
                return null;
            }
        };
        service.fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isRagChatLogEnabled() {
                return chatLogEnabled;
            }

            @Override
            public boolean isUserInfo() {
                return userInfo;
            }

            @Override
            public Integer getSearchlogAggShardSizeAsInteger() {
                return -1;
            }
        };
        return service;
    }

    @Test
    public void test_getReport_emptyIndex() {
        final List<String> aggNames = new ArrayList<>();
        final AnalyticsReport report = service(true, true, aggNames).getReport(condition(null));

        assertFalse(report.isFailed());
        assertEquals(List.of("trend", "users", "totalTokens", "tokenReports", "avgResponseTime", "errors", "chatUsers", "guests",
                "chatIntents", "chatModels"), aggNames);
        assertEquals(List.of("requests", "users", "totalTokens", "avgResponseTime", "errorRate"),
                report.getKpis().stream().map(AnalyticsReport.Kpi::getKey).toList());
        assertEquals(0L, report.getKpis().get(0).getValue());
        assertNull(report.getKpis().get(2).getValue(), "no token was reported");
        assertNull(report.getKpis().get(4).getValue(), "no error rate without requests");
        assertNull(report.getKpis().get(0).getPrevious());

        final AnalyticsReport.Chart trend = report.getCharts().get("trend");
        assertTrue(trend.isSelectable());
        assertEquals(7, trend.getX().size());
        assertEquals("2026-09-01", trend.getX().get(0));
        assertEquals(List.of("requests", "users", "totalTokens", "avgResponseTime", "errorRate"),
                trend.getSeries().stream().map(AnalyticsReport.Series::getKey).toList());
        assertTrue(trend.isEmpty());

        assertTrue(report.getTables().get("chatUsers").isEmpty());
        assertTrue(report.getTables().get("chatIntents").isEmpty());
        assertTrue(report.getTables().get("chatModels").isEmpty());
        assertEquals(List.of("labels.searchlog_notice_chat_tokens"),
                report.getNotices().stream().map(AnalyticsReport.Notice::getKey).toList());
    }

    @Test
    public void test_getReport_compareAndNotices() {
        final List<String> aggNames = new ArrayList<>();
        final AnalyticsReport report = service(false, false, aggNames).getReport(condition("true"));

        // the previous period needs no tables
        assertEquals(16, aggNames.size());
        assertEquals(0L, report.getKpis().get(0).getPrevious());
        assertEquals(7, report.getCharts().get("trend").getSeries().get(0).getPrevious().size());
        assertEquals(List.of("labels.searchlog_notice_chat_log_disabled", "labels.searchlog_notice_user_info_disabled",
                "labels.searchlog_notice_chat_tokens"), report.getNotices().stream().map(AnalyticsReport.Notice::getKey).toList());
        assertEquals("warning", report.getNotices().get(0).getLevel());
    }

    @Test
    public void test_getReport_failure() {
        final ChatLogAnalyticsService service = new ChatLogAnalyticsService() {
            @Override
            protected AnalyticsReport buildChat(final AnalyticsCondition cond) {
                throw new IllegalStateException("index_not_found");
            }
        };
        final AnalyticsReport report = service.getReport(condition(null));
        assertTrue(report.isFailed());
        assertEquals("labels.searchlog_notice_failed", report.getNotices().get(0).getKey());
        assertEquals("danger", report.getNotices().get(0).getLevel());
    }

    @Test
    public void test_addKpis() {
        final AnalyticsReport report = new AnalyticsReport();
        ChatLogAnalyticsService.addKpis(report, new ChatLogAnalyticsService.ChatPeriod(200, 40, 50_000L, 1500.0, 10, Map.of()),
                new ChatLogAnalyticsService.ChatPeriod(100, 20, null, 1000.0, 0, Map.of()));
        final List<AnalyticsReport.Kpi> kpis = report.getKpis();
        assertEquals(200L, kpis.get(0).getValue());
        assertEquals(100.0, kpis.get(0).getChange());
        assertEquals(40L, kpis.get(1).getValue());
        assertEquals(50_000L, kpis.get(2).getValue());
        assertNull(kpis.get(2).getPrevious());
        assertNull(kpis.get(2).getChange());
        assertEquals("ms", kpis.get(3).getUnit());
        assertEquals(1500.0, kpis.get(3).getValue());
        assertEquals("percent", kpis.get(4).getUnit());
        assertEquals(0.05, kpis.get(4).getValue());
        assertEquals(0.0, kpis.get(4).getPrevious());
    }

    @Test
    public void test_userRowAndTokens() {
        final Map<String, Object> row = ChatLogAnalyticsService.userRow("taro", 3L, null, false);
        assertEquals("taro", row.get("user"));
        assertEquals(3L, row.get("count"));
        assertNull(row.get("promptTokens"), "unknown when no request reported tokens");
        assertNull(row.get("totalTokens"));
        assertEquals(false, row.get("guest"));
        assertNull(ChatLogAnalyticsService.tokens(null));
        assertNull(ChatLogAnalyticsService.toLong(null));
        assertEquals(Long.valueOf(13L), ChatLogAnalyticsService.toLong(12.6));
    }

    @Test
    public void test_isExportable() {
        final ChatLogAnalyticsService service = new ChatLogAnalyticsService();
        assertTrue(service.isExportable("kpis"));
        assertTrue(service.isExportable("trend"));
        assertTrue(service.isExportable("chatUsers"));
        assertTrue(service.isExportable("chatIntents"));
        assertTrue(service.isExportable("chatModels"));
        assertFalse(service.isExportable("topQueries"), "item of another tab");
        assertFalse(service.isExportable("../x"));
        assertFalse(service.isExportable(null));
    }

    @Test
    public void test_exportCsv_rejectsUnknownItem() throws Exception {
        try {
            new ChatLogAnalyticsService().exportCsv(new AnalyticsReport(), "weekHour", new StringWriter());
            fail();
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void test_exportCsv_users() throws Exception {
        final AnalyticsReport report = new AnalyticsReport();
        final List<Map<String, Object>> rows = new ArrayList<>();
        final Map<String, Object> taro = ChatLogAnalyticsService.userRow("=taro", 8L, null, false);
        taro.put("promptTokens", 800L);
        taro.put("completionTokens", 200L);
        taro.put("totalTokens", 1000L);
        rows.add(taro);
        rows.add(ChatLogAnalyticsService.userRow("guest", 2L, null, true));
        report.putTable("chatUsers", SearchLogAnalyticsService.withBars(rows, "count"));
        assertEquals(List.of(List.of("user", "count", "promptTokens", "completionTokens", "totalTokens"),
                List.of("'=taro", "8", "800", "200", "1000"), List.of("guest", "2", "", "", "")), export(report, "chatUsers"));
        // a table the report does not carry is an empty one
        assertEquals(List.of(List.of("value", "count", "llmCalls", "totalTokens")), export(new AnalyticsReport(), "chatModels"));
    }

    @Test
    public void test_exportCsv_kpisAndTrend() throws Exception {
        final AnalyticsReport report = new AnalyticsReport();
        ChatLogAnalyticsService.addKpis(report, new ChatLogAnalyticsService.ChatPeriod(4, 2, 300L, 900.0, 1, Map.of()), null);
        final AnalyticsReport.Series requests = new AnalyticsReport.Series("requests", List.of(1L, 3L));
        report.putChart("trend", new AnalyticsReport.Chart("line", "mixed", List.of("2026-09-01", "2026-09-02"), List.of(requests)));
        final List<List<String>> kpis = export(report, "kpis");
        assertEquals(List.of("metric", "unit", "value", "previous", "change"), kpis.get(0));
        assertEquals(List.of("requests", "count", "4", "", ""), kpis.get(1));
        assertEquals(List.of("errorRate", "percent", "0.25", "", ""), kpis.get(5));
        assertEquals(List.of(List.of("x", "requests"), List.of("2026-09-01", "1"), List.of("2026-09-02", "3")), export(report, "trend"));
        // a failed report has no chart
        assertEquals(List.of(), export(new AnalyticsReport(), "trend"));
    }

    private static List<List<String>> export(final AnalyticsReport report, final String item) throws java.io.IOException {
        final StringWriter out = new StringWriter();
        new ChatLogAnalyticsService().exportCsv(report, item, out);
        final List<List<String>> rows = new ArrayList<>();
        try (CsvReader reader = new CsvReader(new StringReader(out.toString()), CsvUtil.createCsvConfig())) {
            List<String> row;
            while ((row = reader.readValues()) != null) {
                if (!row.isEmpty() && !(row.size() == 1 && row.get(0).isEmpty())) {
                    rows.add(row);
                }
            }
        }
        return rows;
    }
}
