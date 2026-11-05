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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.fesen.opensearch.action.index.IndexRequestBuilder;
import org.codelibs.fess.app.pager.FailureUrlPager;
import org.codelibs.fess.entity.FailureUrlDetails;
import org.codelibs.fess.exception.ContainerNotAvailableException;
import org.codelibs.fess.opensearch.config.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.opensearch.config.cbean.FailureUrlCB;
import org.codelibs.fess.opensearch.config.exbhv.FailureUrlBhv;
import org.codelibs.fess.opensearch.config.exentity.FailureUrl;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.CsvUtil;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.orangesignal.csv.CsvReader;

public class FailureUrlServiceTest extends UnitFessTestCase {

    private static final String URL = "smb-host.example.com-share-a.pdf";

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new SystemHelper(), "systemHelper");
    }

    /** The behavior that holds one failure URL and records what it is asked to index. */
    private static class StubBhv extends FailureUrlBhv {
        FailureUrl current;

        final List<FailureUrl> indexed = new ArrayList<>();

        final List<FailureUrl> rows = new ArrayList<>();

        final AtomicReference<FailureUrlCB> cursorCb = new AtomicReference<>();

        @Override
        public OptionalEntity<FailureUrl> selectEntity(final CBCall<FailureUrlCB> cbLambda) {
            return current == null ? OptionalEntity.empty() : OptionalEntity.of(current);
        }

        @Override
        public void insertOrUpdate(final FailureUrl entity, final RequestOptionCall<IndexRequestBuilder> op) {
            indexed.add(entity);
            current = entity;
        }

        @Override
        public void selectCursor(final CBCall<FailureUrlCB> cbLambda, final EntityRowHandler<FailureUrl> entityLambda) {
            final FailureUrlCB cb = new FailureUrlCB();
            cbLambda.callback(cb);
            cursorCb.set(cb);
            for (final FailureUrl row : rows) {
                if (entityLambda.isBreakCursor()) {
                    return;
                }
                entityLambda.handle(row);
            }
        }
    }

    private static FailureUrlService service(final StubBhv bhv) {
        final FailureUrlService service = new FailureUrlService() {
            @Override
            protected FailureUrlBhv getFailureUrlBhv() {
                return bhv;
            }
        };
        service.failureUrlBhv = bhv;
        return service;
    }

    private static FailureUrl row(final String url, final String errorName) {
        final FailureUrl row = new FailureUrl();
        row.setUrl(url);
        row.setErrorName(errorName);
        row.setErrorCount(2);
        row.setLastAccessTime(1700000000000L);
        row.setErrorLog("java.lang.Exception: the stack trace");
        row.setHttpStatusCode(200);
        row.setMimeType("application/pdf");
        row.setContentLength(2048L);
        return row;
    }

    private static List<List<String>> parse(final String csv) throws IOException {
        final List<List<String>> rows = new ArrayList<>();
        try (CsvReader reader = new CsvReader(new StringReader(csv), CsvUtil.createCsvConfig())) {
            List<String> values;
            while ((values = reader.readValues()) != null) {
                if (!values.isEmpty() && !(values.size() == 1 && values.get(0).isEmpty())) {
                    rows.add(values);
                }
            }
        }
        return rows;
    }

    @Test
    public void test_store_withDetails() throws Exception {
        final StubBhv bhv = new StubBhv();
        final FailureUrlDetails details = new FailureUrlDetails(2048L, "Application/PDF; charset=UTF-8", 200);
        final FailureUrl stored = service(bhv).store(null, "x.Error", URL, new RuntimeException("boom"), details);
        assertEquals(1, bhv.indexed.size());
        assertEquals(Long.valueOf(2048L), stored.getContentLength());
        assertEquals("application/pdf", stored.getMimeType());
        assertEquals(Integer.valueOf(200), stored.getHttpStatusCode());
        assertEquals(Integer.valueOf(1), stored.getErrorCount());
        assertEquals(Long.valueOf(2048L), stored.toSource().get("contentLength"));
        assertEquals("application/pdf", stored.toSource().get("mimeType"));
        assertEquals(Integer.valueOf(200), stored.toSource().get("httpStatusCode"));
    }

    @Test
    public void test_store_withoutDetailsLeavesThemUnset() throws Exception {
        final StubBhv bhv = new StubBhv();
        final FailureUrl stored = service(bhv).store(null, "x.Error", URL, new RuntimeException("boom"));
        assertNull(stored.getContentLength());
        assertNull(stored.getMimeType());
        assertNull(stored.getHttpStatusCode());
        assertFalse(stored.toSource().containsKey("contentLength"));
        assertFalse(stored.toSource().containsKey("mimeType"));
        assertFalse(stored.toSource().containsKey("httpStatusCode"));
    }

    @Test
    public void test_store_secondFailureWithoutDetailsClearsTheStaleOnes() throws Exception {
        final StubBhv bhv = new StubBhv();
        final FailureUrlService service = service(bhv);
        service.store(null, "x.Error", URL, new RuntimeException("first"), new FailureUrlDetails(2048L, "application/pdf", 200));
        final FailureUrl second = service.store(null, "y.Error", URL, new RuntimeException("second"), null);
        assertEquals(2, bhv.indexed.size());
        assertEquals(Integer.valueOf(2), second.getErrorCount());
        // the entity is reused: what is sent to the index must not carry the first failure's values
        assertNull(second.getContentLength());
        assertNull(second.getMimeType());
        assertNull(second.getHttpStatusCode());
        assertFalse(bhv.indexed.get(1).toSource().containsKey("contentLength"));
        assertFalse(bhv.indexed.get(1).toSource().containsKey("mimeType"));
        assertFalse(bhv.indexed.get(1).toSource().containsKey("httpStatusCode"));
    }

    @Test
    public void test_store_ignoresContainerShutdown() throws Exception {
        final StubBhv bhv = new StubBhv();
        final FailureUrl stored =
                service(bhv).store(null, "x.Error", URL, new ContainerNotAvailableException(new RuntimeException()), null);
        assertNull(stored);
        assertEquals(0, bhv.indexed.size());
    }

    @Test
    public void test_exportCsv_columnsAndNoStackTrace() throws Exception {
        final StubBhv bhv = new StubBhv();
        bhv.rows.add(row("doc-a", "x.Error"));
        final StringWriter out = new StringWriter();
        service(bhv).exportCsv(new FailureUrlPager(), out);
        final List<List<String>> rows = parse(out.toString());
        assertEquals(2, rows.size());
        assertEquals(List.of("url", "errorName", "errorCount", "lastAccessTime", "httpStatusCode", "mimeType", "contentLength"),
                rows.get(0));
        assertEquals(List.of("doc-a", "x.Error", "2", "2023-11-14T22:13:20.000Z", "200", "application/pdf", "2048"), rows.get(1));
        assertFalse(out.toString().contains("stack trace"));
        assertFalse(out.toString().contains("errorLog"));
    }

    @Test
    public void test_exportCsv_neutralizesFormulas() throws Exception {
        final StubBhv bhv = new StubBhv();
        final String[] triggers = { "=", "+", "-", "@", "\t", "\r" };
        for (final String trigger : triggers) {
            bhv.rows.add(row(trigger + "cmd-url", trigger + "cmd-error"));
        }
        final StringWriter out = new StringWriter();
        service(bhv).exportCsv(new FailureUrlPager(), out);
        final List<List<String>> rows = parse(out.toString());
        assertEquals(triggers.length + 1, rows.size());
        for (int i = 0; i < triggers.length; i++) {
            // a spreadsheet must read the crawl-controlled url and error name as text
            assertEquals("'" + triggers[i] + "cmd-url", rows.get(i + 1).get(0));
            assertEquals("'" + triggers[i] + "cmd-error", rows.get(i + 1).get(1));
        }
    }

    @Test
    public void test_exportCsv_appliesTheFilters() throws Exception {
        final StubBhv bhv = new StubBhv();
        final FailureUrlPager pager = new FailureUrlPager();
        pager.url = "*docs*";
        pager.errorCountMin = "3";
        pager.errorCountMax = "9";
        pager.errorName = "*Timeout*";
        service(bhv).exportCsv(pager, new StringWriter());
        final String query = String.valueOf(bhv.cursorCb.get().query().getQuery()).replaceAll("\\s+", "");
        assertTrue(query, query.contains("\"wildcard\":{\"url\":{\"wildcard\":\"*docs*\""));
        assertTrue(query, query.contains("\"wildcard\":{\"errorName\":{\"wildcard\":\"*Timeout*\""));
        assertTrue(query, query.contains("\"errorCount\":{\"from\":3,\"to\":9,\"include_lower\":true,\"include_upper\":true"));
    }

    @Test
    public void test_exportCsv_withoutFiltersMatchesEverything() throws Exception {
        final StubBhv bhv = new StubBhv();
        service(bhv).exportCsv(new FailureUrlPager(), new StringWriter());
        final String query = String.valueOf(bhv.cursorCb.get().query().getQuery());
        assertFalse(query, query.contains("wildcard"));
        assertFalse(query, query.contains("errorCount"));
    }

    @Test
    public void test_exportCsv_writeFailureStopsTheCursorAndReleasesIt() throws Exception {
        final int[] handled = { 0 };
        final boolean[] released = { false };
        final StubBhv bhv = new StubBhv() {
            @Override
            public void selectCursor(final CBCall<FailureUrlCB> cbLambda, final EntityRowHandler<FailureUrl> entityLambda) {
                try {
                    for (final FailureUrl row : rows) {
                        handled[0]++;
                        entityLambda.handle(row);
                    }
                } finally {
                    // what the real cursor does for its point in time, whatever the handler throws
                    released[0] = true;
                }
            }
        };
        for (int i = 0; i < 20; i++) {
            bhv.rows.add(row("doc-" + i + "-".repeat(20000), "x.Error"));
        }
        // the client has gone: every write to the response fails
        final Writer gone = new Writer() {
            @Override
            public void write(final char[] cbuf, final int off, final int len) throws IOException {
                throw new IOException("client gone");
            }

            @Override
            public void flush() throws IOException {
                throw new IOException("client gone");
            }

            @Override
            public void close() {
                // nothing
            }
        };
        try {
            service(bhv).exportCsv(new FailureUrlPager(), gone);
            fail("the failure of the writer is reported");
        } catch (final IOException e) {
            assertEquals("client gone", e.getMessage());
        }
        // the first rows that did not fit stopped the read, and the cursor was let go
        assertTrue("stopped early: " + handled[0], handled[0] < 20);
        assertTrue(released[0]);
    }

}
