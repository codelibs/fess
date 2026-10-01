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
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codelibs.fess.entity.AnalyticsCondition;
import org.codelibs.fess.entity.AnalyticsReport;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.CsvUtil;
import org.junit.jupiter.api.Test;

import com.orangesignal.csv.CsvReader;

import tools.jackson.databind.ObjectMapper;

public class SearchLogAnalyticsServiceTest extends UnitFessTestCase {

    @Test
    public void test_ratio() {
        assertEquals(0.25, SearchLogAnalyticsService.ratio(1, 4));
        assertNull(SearchLogAnalyticsService.ratio(1, 0), "zero denominator");
        assertNull(SearchLogAnalyticsService.ratio(1, null), "null denominator");
        assertNull(SearchLogAnalyticsService.ratio(null, 4), "null numerator");
    }

    @Test
    public void test_finite() {
        assertEquals(1.5, SearchLogAnalyticsService.finite(1.5));
        assertNull(SearchLogAnalyticsService.finite(Double.NaN), "NaN");
        assertNull(SearchLogAnalyticsService.finite(Double.POSITIVE_INFINITY), "+Infinity");
        assertNull(SearchLogAnalyticsService.finite(Double.NEGATIVE_INFINITY), "-Infinity");
    }

    @Test
    public void test_kpiChange() {
        assertEquals(50.0, new AnalyticsReport.Kpi("searches", "count", 150, 100).getChange());
        final AnalyticsReport.Kpi rate = new AnalyticsReport.Kpi("ctr", "percent", 0.31, 0.29);
        org.junit.jupiter.api.Assertions.assertEquals(2.0, rate.getChange(), 1e-9);
        assertTrue(rate.isPointChange());
        assertFalse(new AnalyticsReport.Kpi("searches", "count", 150, 100).isPointChange());
        org.junit.jupiter.api.Assertions.assertEquals(1.0, new AnalyticsReport.Kpi("zeroHitRate", "percent", 0.01, 0).getChange(), 1e-9);
        assertNull(new AnalyticsReport.Kpi("searches", "count", 5, 0).getChange(), "previous 0");
        assertNull(new AnalyticsReport.Kpi("searches", "count", 5, null).getChange(), "no compare");
        assertNull(new AnalyticsReport.Kpi("avgRank", "rank", null, 2.0).getChange(), "no value");
    }

    @Test
    public void test_withBars() {
        final List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(row("a", 200L));
        rows.add(row("b", 50L));
        final List<Map<String, Object>> result = SearchLogAnalyticsService.withBars(rows, "count");
        assertEquals(100, result.get(0).get("bar"));
        assertEquals(25, result.get(1).get("bar"));
        assertTrue(SearchLogAnalyticsService.withBars(new ArrayList<>(), "count").isEmpty());
    }

    @Test
    public void test_zeroClickWords_excludesClicked() {
        final List<Map<String, Object>> searched = List.of(row("a", 30L), row("b", 20L), row("c", 10L), row("d", 5L));
        final List<Map<String, Object>> result = SearchLogAnalyticsService.zeroClickWords(searched, Set.of("b"), 2);
        assertEquals(2, result.size());
        assertEquals("a", result.get(0).get("word"));
        assertEquals("c", result.get(1).get("word"));
    }

    @Test
    public void test_foldWeekHour() {
        final ZoneId zone = ZoneId.of("Asia/Tokyo");
        final List<Map.Entry<ZonedDateTime, Long>> hourly = List.of(new SimpleEntry<>(ZonedDateTime.of(2026, 9, 14, 9, 0, 0, 0, zone), 3L), // Monday 09
                new SimpleEntry<>(ZonedDateTime.of(2026, 9, 21, 9, 0, 0, 0, zone), 4L), // Monday 09
                new SimpleEntry<>(ZonedDateTime.of(2026, 9, 20, 23, 0, 0, 0, zone), 1L)); // Sunday 23
        final long[][] grid = SearchLogAnalyticsService.foldWeekHour(hourly);
        assertEquals(7L, grid[0][9]);
        assertEquals(1L, grid[6][23]);
        assertEquals(0L, grid[3][3]);
        final List<Map<String, Object>> rows = SearchLogAnalyticsService.weekHourRows(grid);
        assertEquals(7, rows.size());
        assertEquals(1, rows.get(0).get("day"));
        assertEquals(7, rows.get(6).get("day"));
        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> monday = (List<Map<String, Object>>) rows.get(0).get("cells");
        assertEquals(24, monday.size());
        assertEquals(4, monday.get(9).get("level"));
        assertEquals(7L, monday.get(9).get("count"));
        assertEquals(0, monday.get(0).get("level"));
        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> sunday = (List<Map<String, Object>>) rows.get(6).get("cells");
        assertEquals(1, sunday.get(23).get("level"));
    }

    @Test
    public void test_foldWeekHour_utcKeys() {
        final ZoneId zone = ZoneId.of("Asia/Tokyo");
        final ZonedDateTime local = SearchLogAnalyticsService.toZone(ZonedDateTime.of(2026, 9, 14, 0, 0, 0, 0, ZoneOffset.UTC), zone);
        assertEquals(ZonedDateTime.of(2026, 9, 14, 9, 0, 0, 0, zone), local);
        final long[][] grid = SearchLogAnalyticsService.foldWeekHour(List.of(new SimpleEntry<>(local, 2L)));
        assertEquals(2L, grid[0][9]);
        assertEquals(0L, grid[0][0]);
    }

    @Test
    public void test_foldRankDistribution() {
        final Map<Long, Long> counts = new HashMap<>();
        counts.put(0L, 9L);
        counts.put(1L, 10L);
        counts.put(10L, 2L);
        counts.put(11L, 1L);
        counts.put(20L, 1L);
        counts.put(35L, 4L);
        final Map<String, Long> dist = SearchLogAnalyticsService.foldRankDistribution(counts);
        assertEquals(12, dist.size());
        assertEquals(Long.valueOf(10), dist.get("1"));
        assertEquals(Long.valueOf(0), dist.get("2"));
        assertEquals(Long.valueOf(2), dist.get("10"));
        assertEquals(Long.valueOf(2), dist.get("11-20"));
        assertEquals(Long.valueOf(4), dist.get("21+"));
        assertEquals(List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11-20", "21+"), new ArrayList<>(dist.keySet()));
    }

    @Test
    public void test_bucketLabels() {
        final ZoneId zone = ZoneId.of("Asia/Tokyo");
        final List<ZonedDateTime> keys = List.of(ZonedDateTime.of(2026, 9, 1, 5, 0, 0, 0, zone));
        assertEquals(List.of("09-01 05:00"), SearchLogAnalyticsService.bucketLabels(keys, AnalyticsCondition.Interval.HOUR));
        assertEquals(List.of("2026-09-01"), SearchLogAnalyticsService.bucketLabels(keys, AnalyticsCondition.Interval.DAY));
    }

    @Test
    public void test_bucketSlots() {
        final ZoneId zone = ZoneId.of("Asia/Tokyo");
        final ZonedDateTime start = ZonedDateTime.of(2026, 9, 1, 0, 0, 0, 0, zone);
        final List<ZonedDateTime> hours = SearchLogAnalyticsService.bucketSlots(start, start.plusDays(1), AnalyticsCondition.Interval.HOUR);
        assertEquals(24, hours.size());
        assertEquals(start, hours.get(0));
        assertEquals(start.plusHours(23), hours.get(23));
        // a period ending "now" keeps the partial last day
        final List<ZonedDateTime> days =
                SearchLogAnalyticsService.bucketSlots(start, start.plusDays(6).plusHours(10), AnalyticsCondition.Interval.DAY);
        assertEquals(7, days.size());
        assertEquals(start.plusDays(6), days.get(6));
    }

    @Test
    public void test_bucketSlots_dst() {
        final ZoneId zone = ZoneId.of("America/New_York");
        // 2026-11-01 has 25 hours in New York
        final ZonedDateTime start = ZonedDateTime.of(2026, 11, 1, 0, 0, 0, 0, zone);
        assertEquals(25, SearchLogAnalyticsService.bucketSlots(start, start.plusDays(1), AnalyticsCondition.Interval.HOUR).size());
        final List<ZonedDateTime> days = SearchLogAnalyticsService.bucketSlots(start, start.plusDays(3), AnalyticsCondition.Interval.DAY);
        assertEquals(3, days.size());
        assertEquals(0, days.get(2).getHour());
    }

    @Test
    public void test_bucketSlots_dayWithoutMidnight() {
        final ZoneId zone = ZoneId.of("Africa/Cairo");
        // 2026-04-24 starts at 01:00 in Cairo (DST begins at midnight)
        final ZonedDateTime start = ZonedDateTime.of(2026, 4, 22, 0, 0, 0, 0, zone);
        final ZonedDateTime end = ZonedDateTime.of(2026, 4, 27, 0, 0, 0, 0, zone);
        final List<ZonedDateTime> days = SearchLogAnalyticsService.bucketSlots(start, end, AnalyticsCondition.Interval.DAY);
        assertEquals(5, days.size());
        assertEquals(ZonedDateTime.of(2026, 4, 24, 1, 0, 0, 0, zone), days.get(2));
        assertEquals(0, days.get(3).getHour());
        assertEquals(25, days.get(3).getDayOfMonth());
        assertEquals(0, days.get(4).getHour());
        assertEquals(26, days.get(4).getDayOfMonth());
    }

    @Test
    public void test_clickCoverageNotice() {
        final ZoneId zone = ZoneId.of("Asia/Tokyo");
        final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        final ZonedDateTime start = ZonedDateTime.of(2026, 9, 1, 0, 0, 0, 0, zone);
        assertNull(SearchLogAnalyticsService.clickCoverageNotice(start, start, fmt), "covered from the start");
        assertNull(SearchLogAnalyticsService.clickCoverageNotice(start, start.minusDays(1), fmt), "covered before the start");
        final AnalyticsReport.Notice since = SearchLogAnalyticsService.clickCoverageNotice(start, start.plusDays(2).plusHours(13), fmt);
        assertEquals("info", since.getLevel());
        assertEquals("labels.searchlog_notice_click_since", since.getKey());
        assertEquals(1, since.getArgs().length);
        assertEquals("2026-09-03 13:00", since.getArgs()[0]);
        final AnalyticsReport.Notice none = SearchLogAnalyticsService.clickCoverageNotice(start, null, fmt);
        assertEquals("info", none.getLevel());
        assertEquals("labels.searchlog_notice_no_click_data", none.getKey());
        assertEquals(0, none.getArgs().length);
    }

    @Test
    public void test_chartJson() {
        final AnalyticsReport.Series series = new AnalyticsReport.Series("searches", List.of(1, 2));
        series.setName("Searches");
        final AnalyticsReport.Chart chart = new AnalyticsReport.Chart("line", "count", List.of("a", "b"), List.of(series)).selectable();
        assertTrue(chart.isSelectable());
        @SuppressWarnings("unchecked")
        final Map<String, Object> json = new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(chart), Map.class);
        assertEquals(Set.of("type", "unit", "x", "series", "selectable"), json.keySet());
        @SuppressWarnings("unchecked")
        final Map<String, Object> seriesJson = ((List<Map<String, Object>>) json.get("series")).get(0);
        assertEquals(Set.of("key", "name", "data", "previous"), seriesJson.keySet());
        assertEquals("Searches", seriesJson.get("name"));
    }

    @Test
    public void test_chartIsEmpty() {
        final java.util.function.BiFunction<List<Number>, List<Number>, AnalyticsReport.Chart> chart = (data, previous) -> {
            final AnalyticsReport.Series series = new AnalyticsReport.Series("searches", data);
            series.setPrevious(previous);
            return new AnalyticsReport.Chart("line", "count", List.of("a", "b"), List.of(series));
        };
        assertTrue(chart.apply(java.util.Arrays.asList(null, null), null).isEmpty(), "all null");
        assertTrue(chart.apply(List.of(0, 0.0), java.util.Arrays.asList(0L, null)).isEmpty(), "all zero, previous zero or null");
        assertFalse(chart.apply(List.of(0, 0), List.of(0, 3)).isEmpty(), "previous non-zero");
        assertFalse(chart.apply(java.util.Arrays.asList(null, 1), null).isEmpty(), "one non-zero value");
        assertTrue(new AnalyticsReport.Chart("pie", "count", List.of(), List.of()).isEmpty(), "no series");
    }

    @Test
    public void test_getReport_failureReturnsFailedReport() {
        // getReport must survive a behavior that throws (e.g. index missing): failed report, no exception
        final SearchLogAnalyticsService service = new SearchLogAnalyticsService() {
            @Override
            protected AnalyticsReport buildOverview(final AnalyticsCondition cond) {
                throw new IllegalStateException("index_not_found");
            }
        };
        final AnalyticsReport report = service.getReport("overview",
                AnalyticsCondition.create(null, null, null, null, null, null, 10, java.time.Clock.systemDefaultZone()));
        assertTrue(report.isFailed());
        assertEquals("danger", report.getNotices().get(report.getNotices().size() - 1).getLevel());
        assertEquals("labels.searchlog_notice_failed", report.getNotices().get(report.getNotices().size() - 1).getKey());
        assertTrue(report.getKpis().isEmpty());
        assertTrue(report.getCharts().isEmpty());
        assertTrue(report.getTables().isEmpty());
    }

    @Test
    public void test_getReport_dispatch() {
        final List<String> called = new ArrayList<>();
        final SearchLogAnalyticsService service = new SearchLogAnalyticsService() {
            @Override
            protected AnalyticsReport buildOverview(final AnalyticsCondition cond) {
                called.add(TAB_OVERVIEW);
                return new AnalyticsReport();
            }

            @Override
            protected AnalyticsReport buildQueries(final AnalyticsCondition cond) {
                called.add(TAB_QUERIES);
                return new AnalyticsReport();
            }

            @Override
            protected AnalyticsReport buildClicks(final AnalyticsCondition cond) {
                called.add(TAB_CLICKS);
                return new AnalyticsReport();
            }

            @Override
            protected AnalyticsReport buildPerformance(final AnalyticsCondition cond) {
                called.add(TAB_PERFORMANCE);
                return new AnalyticsReport();
            }

            @Override
            protected AnalyticsReport buildAudience(final AnalyticsCondition cond) {
                called.add(TAB_AUDIENCE);
                return new AnalyticsReport();
            }
        };
        final AnalyticsCondition cond =
                AnalyticsCondition.create(null, null, null, null, null, null, 25, java.time.Clock.systemDefaultZone());
        for (final String tab : SearchLogAnalyticsService.TABS) {
            assertFalse(service.getReport(tab, cond).isFailed());
        }
        service.getReport("unknown", cond);
        service.getReport(null, cond);
        assertEquals(List.of("overview", "queries", "clicks", "performance", "audience", "overview", "overview"), called);
    }

    private static List<List<String>> parseCsv(final String csv) throws java.io.IOException {
        final List<List<String>> rows = new ArrayList<>();
        try (CsvReader reader = new CsvReader(new StringReader(csv), CsvUtil.createCsvConfig())) {
            List<String> row;
            while ((row = reader.readValues()) != null) {
                // the reader reports the end of the last line as an empty record
                if (!row.isEmpty() && !(row.size() == 1 && row.get(0).isEmpty())) {
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    private static List<List<String>> export(final AnalyticsReport report, final String tab, final String item) throws java.io.IOException {
        final StringWriter out = new StringWriter();
        new SearchLogAnalyticsService().exportCsv(report, tab, item, out);
        return parseCsv(out.toString());
    }

    @Test
    public void test_isExportable() {
        final SearchLogAnalyticsService service = new SearchLogAnalyticsService();
        assertTrue(service.isExportable("overview", "kpis"));
        assertTrue(service.isExportable("overview", "trend"));
        assertTrue(service.isExportable("queries", "queries"));
        assertTrue(service.isExportable("queries", "zeroClickQueries"));
        assertTrue(service.isExportable("clicks", "favoriteUrls"));
        assertTrue(service.isExportable("performance", "slowQueries"));
        assertTrue(service.isExportable("audience", "weekHour"));
        assertTrue(service.isExportable("audience", "accessTypes"));
        assertFalse(service.isExportable("overview", "weekHour"), "item of another tab");
        assertFalse(service.isExportable("queries", "kpis"), "tab without KPIs");
        assertFalse(service.isExportable("overview", "unknown"));
        assertFalse(service.isExportable("unknown", "kpis"));
        assertFalse(service.isExportable("logs", "kpis"));
        assertFalse(service.isExportable("../x", "../y"));
        assertFalse(service.isExportable(null, "kpis"));
        assertFalse(service.isExportable("overview", null));
    }

    @Test
    public void test_exportCsv_rejectsUnknownItem() throws java.io.IOException {
        try {
            export(new AnalyticsReport(), "overview", "weekHour");
            fail();
        } catch (final IllegalArgumentException e) {
            // expected
        }
        try {
            export(new AnalyticsReport(), "nothing", "kpis");
            fail();
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void test_exportCsv_kpis() throws java.io.IOException {
        final AnalyticsReport report = new AnalyticsReport();
        report.addKpi(new AnalyticsReport.Kpi("searches", "count", 150L, 100L));
        report.addKpi(new AnalyticsReport.Kpi("ctr", "percent", 0.31, 0.29));
        report.addKpi(new AnalyticsReport.Kpi("avgResponseTime", "ms", null, null));
        final List<List<String>> rows = export(report, "overview", "kpis");
        assertEquals(List.of("metric", "unit", "value", "previous", "change"), rows.get(0));
        assertEquals(List.of("searches", "count", "150", "100", "50.0"), rows.get(1));
        assertEquals("ctr", rows.get(2).get(0));
        assertEquals("percent", rows.get(2).get(1));
        assertEquals("0.31", rows.get(2).get(2));
        assertEquals("0.29", rows.get(2).get(3));
        assertEquals(List.of("avgResponseTime", "ms", "", "", ""), rows.get(3));
        assertEquals(4, rows.size());
    }

    @Test
    public void test_exportCsv_tableDropsBarAndGuardsFormulas() throws java.io.IOException {
        final AnalyticsReport report = new AnalyticsReport();
        report.putTable("topQueries",
                SearchLogAnalyticsService.withBars(new ArrayList<>(List.of(row("=1+1", 200L), row("-foo", 50L))), "count"));
        final List<List<String>> rows = export(report, "overview", "topQueries");
        assertEquals(List.of(List.of("word", "count"), List.of("'=1+1", "200"), List.of("'-foo", "50")), rows);
    }

    @Test
    public void test_exportCsv_emptyTableStillHasHeader() throws java.io.IOException {
        final AnalyticsReport report = new AnalyticsReport();
        report.putTable("zeroClickQueries", new ArrayList<>());
        assertEquals(List.of(List.of("word", "count", "lastSearchedAt")), export(report, "queries", "zeroClickQueries"));
        // a table the report does not carry at all (e.g. no click data) is an empty one too
        assertEquals(List.of(List.of("word", "count", "users")), export(new AnalyticsReport(), "queries", "zeroHitQueries"));
    }

    @Test
    public void test_exportCsv_tableWithNullCells() throws java.io.IOException {
        final AnalyticsReport report = new AnalyticsReport();
        final Map<String, Object> r = row("foo", 3L);
        r.put("users", 2L);
        r.put("avgHits", 12.5);
        r.put("clicks", null);
        r.put("ctr", null);
        r.put("avgRank", null);
        report.putTable("queries", List.of(r));
        final List<List<String>> rows = export(report, "queries", "queries");
        assertEquals(List.of("word", "count", "users", "avgHits", "clicks", "ctr", "avgRank"), rows.get(0));
        assertEquals(List.of("foo", "3", "2", "12.5", "", "", ""), rows.get(1));
    }

    @Test
    public void test_exportCsv_weekHour() throws java.io.IOException {
        final long[][] grid = new long[7][24];
        grid[0][9] = 7;
        grid[6][23] = 1;
        final AnalyticsReport report = new AnalyticsReport();
        report.putTable("weekHour", SearchLogAnalyticsService.weekHourRows(grid));
        final List<List<String>> rows = export(report, "audience", "weekHour");
        assertEquals(8, rows.size());
        assertEquals(25, rows.get(0).size());
        assertEquals("day", rows.get(0).get(0));
        assertEquals("h00", rows.get(0).get(1));
        assertEquals("h23", rows.get(0).get(24));
        assertEquals("1", rows.get(1).get(0));
        assertEquals("7", rows.get(1).get(10)); // Monday 09
        assertEquals("0", rows.get(1).get(1));
        assertEquals("7", rows.get(7).get(0));
        assertEquals("1", rows.get(7).get(24)); // Sunday 23
    }

    @Test
    public void test_exportCsv_chartWithPrevious() throws java.io.IOException {
        final AnalyticsReport.Series searches = new AnalyticsReport.Series("searches", List.of(10L, 20L, 30L));
        searches.setPrevious(List.of(5L, 15L));
        final AnalyticsReport.Series ctr = new AnalyticsReport.Series("ctr", java.util.Arrays.asList(0.5, null, 0.25));
        final AnalyticsReport report = new AnalyticsReport();
        report.putChart("trend", new AnalyticsReport.Chart("line", "mixed", List.of("09-14", "09-15", "09-16"), List.of(searches, ctr)));
        final List<List<String>> rows = export(report, "overview", "trend");
        assertEquals(List.of("x", "searches", "searches_previous", "ctr"), rows.get(0));
        assertEquals(List.of("09-14", "10", "5", "0.5"), rows.get(1));
        assertEquals(List.of("09-15", "20", "15", ""), rows.get(2));
        // a previous period shorter than the period leaves an empty cell
        assertEquals(List.of("09-16", "30", "", "0.25"), rows.get(3));
        assertEquals(4, rows.size());
    }

    @Test
    public void test_exportCsv_chartWithoutPrevious() throws java.io.IOException {
        final AnalyticsReport report = new AnalyticsReport();
        report.putChart("accessTypes", new AnalyticsReport.Chart("pie", "count", List.of("web", "=json"),
                List.of(new AnalyticsReport.Series("searches", List.of(8L, 2L)))));
        assertEquals(List.of(List.of("x", "searches"), List.of("web", "8"), List.of("'=json", "2")),
                export(report, "audience", "accessTypes"));
    }

    private static Map<String, Object> row(final String word, final long count) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("word", word);
        map.put("count", count);
        return map;
    }
}
