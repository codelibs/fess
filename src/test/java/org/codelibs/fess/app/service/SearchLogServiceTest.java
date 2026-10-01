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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.fess.app.pager.SearchLogPager;
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
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.CsvUtil;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.junit.jupiter.api.Test;

import com.orangesignal.csv.CsvReader;

public class SearchLogServiceTest extends UnitFessTestCase {
    private static LocalDateTime utc(final LocalDateTime local) {
        return local.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    @Test
    public void test_parseRequestedTimeRange_bothEndsConverted() {
        // a non-UTC default zone makes the conversion assertions meaningful on any JVM
        final TimeZone original = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"));
        try {
            final LocalDateTime[] r = new SearchLogService().parseRequestedTimeRange("2026-09-01 00:00 - 2026-09-02 12:30");
            assertEquals(LocalDateTime.of(2026, 8, 31, 15, 0), r[0]);
            assertEquals(LocalDateTime.of(2026, 9, 2, 3, 30), r[1]);
            assertEquals(utc(LocalDateTime.of(2026, 9, 1, 0, 0)), r[0]);
            assertEquals(utc(LocalDateTime.of(2026, 9, 2, 12, 30)), r[1]);
        } finally {
            TimeZone.setDefault(original);
        }
    }

    @Test
    public void test_parseRequestedTimeRange_partialAndInvalid() {
        final SearchLogService service = new SearchLogService();
        final LocalDateTime[] startOnly = service.parseRequestedTimeRange("2026-09-01 00:00");
        assertEquals(utc(LocalDateTime.of(2026, 9, 1, 0, 0)), startOnly[0]);
        assertNull(startOnly[1], "no end");
        final LocalDateTime[] bad = service.parseRequestedTimeRange("yesterday - tomorrow");
        assertNull(bad[0], "unparsable start");
        assertNull(bad[1], "unparsable end");
        final LocalDateTime[] blank = service.parseRequestedTimeRange("");
        assertNull(blank[0], "blank");
    }

    private static SearchLog searchLog(final String id, final String word) {
        final SearchLog log = new SearchLog();
        log.setId(id);
        log.setSearchWord(word);
        log.setRequestedAt(LocalDateTime.of(2026, 9, 14, 9, 5, 7));
        log.setHitCount(12L);
        log.setHitCountRelation("eq");
        log.setQueryOffset(0);
        log.setQueryPageSize(20);
        log.setQueryTime(5L);
        log.setResponseTime(30L);
        log.setAccessType("web");
        log.setQueryId("q1");
        log.setUserInfoId("ui1");
        log.setUserSessionId("us1");
        log.setUser("alice");
        log.setRoles(new String[] { "role1", "role2" });
        log.setClientIp("192.0.2.1");
        log.setReferer("https://example.com/");
        log.setUserAgent("Mozilla/5.0");
        log.setLanguages("en");
        log.setVirtualHost("vh");
        return log;
    }

    private static SearchLogService serviceWith(final List<SearchLog> logs, final AtomicReference<SearchLogCB> cbHolder) {
        final SearchLogService service = new SearchLogService();
        service.searchLogBhv = new SearchLogBhv() {
            @Override
            public void selectCursor(final CBCall<SearchLogCB> cbLambda, final EntityRowHandler<SearchLog> entityLambda) {
                final SearchLogCB cb = new SearchLogCB();
                cbLambda.callback(cb);
                if (cbHolder != null) {
                    cbHolder.set(cb);
                }
                logs.forEach(entityLambda::handle);
            }
        };
        return service;
    }

    private static List<List<String>> parse(final String csv) throws IOException {
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

    @Test
    public void test_exportCsv_searchLog() throws IOException {
        final AtomicReference<SearchLogCB> cb = new AtomicReference<>();
        final SearchLogService service = serviceWith(List.of(searchLog("id1", "foo"), searchLog("id2", "-bar")), cb);
        final SearchLogPager pager = new SearchLogPager();
        pager.accessType = "web";
        pager.searchWord = "foo";
        pager.requestedTimeRange = "2026-09-01 00:00 - 2026-09-30 23:59";
        final StringWriter out = new StringWriter();
        service.exportCsv(pager, out);

        final List<List<String>> rows = parse(out.toString());
        assertEquals(3, rows.size());
        assertEquals(List.of("id", "requestedAt", "searchWord", "hitCount", "hitCountRelation", "queryOffset", "queryPageSize", "queryTime",
                "responseTime", "accessType", "queryId", "userInfoId", "userSessionId", "user", "roles", "clientIp", "referer", "userAgent",
                "languages", "virtualHost"), rows.get(0));
        // the PII columns are exported
        assertEquals(List.of("id1", "2026-09-14T09:05:07.000Z", "foo", "12", "eq", "0", "20", "5", "30", "web", "q1", "ui1", "us1", "alice",
                "role1 role2", "192.0.2.1", "https://example.com/", "Mozilla/5.0", "en", "vh"), rows.get(1));
        // a leading minus is guarded as well: a spreadsheet reads -bar as a formula
        assertEquals("'-bar", rows.get(2).get(2));
        assertEquals("id2", rows.get(2).get(0));
        assertNotNull(cb.get(), "the condition was built");
    }

    @Test
    public void test_exportCsv_formulaGuardAndQuoting() throws IOException {
        final SearchLog log = searchLog("=cmd", "=1+1");
        log.setUser("@user");
        log.setReferer("+referer");
        log.setLanguages("-2+3+cmd|' /C calc'!A0");
        log.setUserAgent("a,\"b\"");
        log.setRoles(null);
        log.setHitCount(null);
        final StringWriter out = new StringWriter();
        serviceWith(List.of(log), null).exportCsv(new SearchLogPager(), out);
        final List<String> row = parse(out.toString()).get(1);
        assertEquals("'=cmd", row.get(0));
        assertEquals("'=1+1", row.get(2));
        assertEquals("", row.get(3)); // null hitCount
        assertEquals("'@user", row.get(13));
        assertEquals("", row.get(14)); // null roles
        assertEquals("'+referer", row.get(16));
        assertEquals("a,\"b\"", row.get(17));
        assertEquals("'-2+3+cmd|' /C calc'!A0", row.get(18));
    }

    @Test
    public void test_exportCsv_emptyHasHeaderOnly() throws IOException {
        final StringWriter out = new StringWriter();
        serviceWith(List.of(), null).exportCsv(new SearchLogPager(), out);
        final List<List<String>> rows = parse(out.toString());
        assertEquals(1, rows.size());
        assertEquals(SearchLogService.SEARCH_LOG_CSV_COLUMNS, rows.get(0));
    }

    @Test
    public void test_exportCsv_unknownTypeIsSearchLog() throws IOException {
        final StringWriter out = new StringWriter();
        final SearchLogPager pager = new SearchLogPager();
        pager.logType = "aggregate";
        serviceWith(List.of(searchLog("id1", "foo")), null).exportCsv(pager, out);
        assertEquals(SearchLogPager.LOG_TYPE_SEARCH, pager.logType);
        final List<List<String>> rows = parse(out.toString());
        assertEquals(SearchLogService.SEARCH_LOG_CSV_COLUMNS, rows.get(0));
        assertEquals(2, rows.size());
    }

    @Test
    public void test_exportCsv_clickLog() throws IOException {
        final ClickLog click = new ClickLog();
        click.setId("c1");
        click.setRequestedAt(LocalDateTime.of(2026, 9, 14, 9, 6, 0));
        click.setQueryRequestedAt(LocalDateTime.of(2026, 9, 14, 9, 5, 7));
        click.setSearchWord("foo");
        click.setAccessType("json");
        click.setRank(3);
        click.setOrder(2);
        click.setUrl("https://example.com/a");
        click.setUrlId("u1");
        click.setDocId("d1");
        click.setQueryId("q1");
        click.setUserSessionId("us1");
        final SearchLogService service = new SearchLogService();
        service.clickLogBhv = new ClickLogBhv() {
            @Override
            public void selectCursor(final CBCall<ClickLogCB> cbLambda, final EntityRowHandler<ClickLog> entityLambda) {
                cbLambda.callback(new ClickLogCB());
                entityLambda.handle(click);
            }
        };
        final SearchLogPager pager = new SearchLogPager();
        pager.logType = "click";
        final StringWriter out = new StringWriter();
        service.exportCsv(pager, out);
        assertEquals(List.of(
                List.of("id", "requestedAt", "queryRequestedAt", "searchWord", "accessType", "rank", "order", "url", "urlId", "docId",
                        "queryId", "userSessionId"),
                List.of("c1", "2026-09-14T09:06:00.000Z", "2026-09-14T09:05:07.000Z", "foo", "json", "3", "2", "https://example.com/a",
                        "u1", "d1", "q1", "us1")),
                parse(out.toString()));
    }

    @Test
    public void test_exportCsv_favoriteLog() throws IOException {
        final FavoriteLog favorite = new FavoriteLog();
        favorite.setId("f1");
        favorite.setCreatedAt(LocalDateTime.of(2026, 9, 14, 9, 6, 0));
        favorite.setUrl("https://example.com/a");
        favorite.setDocId("d1");
        favorite.setQueryId("q1");
        favorite.setUserInfoId("ui1");
        final SearchLogService service = new SearchLogService();
        service.favoriteLogBhv = new FavoriteLogBhv() {
            @Override
            public void selectCursor(final CBCall<FavoriteLogCB> cbLambda, final EntityRowHandler<FavoriteLog> entityLambda) {
                cbLambda.callback(new FavoriteLogCB());
                entityLambda.handle(favorite);
            }
        };
        final SearchLogPager pager = new SearchLogPager();
        pager.logType = "favorite";
        final StringWriter out = new StringWriter();
        service.exportCsv(pager, out);
        assertEquals(List.of(List.of("id", "createdAt", "url", "docId", "queryId", "userInfoId"),
                List.of("f1", "2026-09-14T09:06:00.000Z", "https://example.com/a", "d1", "q1", "ui1")), parse(out.toString()));
    }

    @Test
    public void test_exportCsv_userInfo() throws IOException {
        final UserInfo info = new UserInfo();
        info.setId("ui1");
        info.setCreatedAt(LocalDateTime.of(2026, 9, 1, 0, 0, 0));
        info.setUpdatedAt(LocalDateTime.of(2026, 9, 14, 9, 6, 0));
        final SearchLogService service = new SearchLogService();
        service.userInfoBhv = new UserInfoBhv() {
            @Override
            public void selectCursor(final CBCall<UserInfoCB> cbLambda, final EntityRowHandler<UserInfo> entityLambda) {
                cbLambda.callback(new UserInfoCB());
                entityLambda.handle(info);
            }
        };
        final SearchLogPager pager = new SearchLogPager();
        pager.logType = "user_info";
        final StringWriter out = new StringWriter();
        service.exportCsv(pager, out);
        assertEquals(
                List.of(List.of("id", "createdAt", "updatedAt"), List.of("ui1", "2026-09-01T00:00:00.000Z", "2026-09-14T09:06:00.000Z")),
                parse(out.toString()));
    }

    @Test
    public void test_exportCsv_writeFailureIsAnIOException() {
        final Writer failing = new Writer() {
            @Override
            public void write(final char[] cbuf, final int off, final int len) throws IOException {
                throw new IOException("closed");
            }

            @Override
            public void flush() throws IOException {
                throw new IOException("closed");
            }

            @Override
            public void close() {
            }
        };
        try {
            serviceWith(List.of(searchLog("id1", "foo")), null).exportCsv(new SearchLogPager(), failing);
            fail();
        } catch (final IOException e) {
            assertEquals("closed", e.getMessage());
        }
    }
}
