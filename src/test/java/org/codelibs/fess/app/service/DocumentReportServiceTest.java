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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.codelibs.fess.entity.DocumentReport;
import org.codelibs.fess.entity.DocumentReport.DuplicateGroup;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.CsvUtil;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.search.sort.SortBuilder;
import org.junit.jupiter.api.Test;

import com.orangesignal.csv.CsvReader;

public class DocumentReportServiceTest extends UnitFessTestCase {

    private static final long NOW = 1_790_000_000_000L;

    /**
     * Resolves every key from the generated default map, which SimpleImpl never loads in unit tests,
     * so the hand-spliced docreport defaults are exercised too.
     */
    private static class TestFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;

        private final Map<String, String> overrides = new HashMap<>();

        @Override
        public String get(final String propertyKey) {
            if (overrides.containsKey(propertyKey)) {
                return overrides.get(propertyKey);
            }
            return prepareGeneratedDefaultMap().get(propertyKey);
        }

        @Override
        public Integer getAsInteger(final String propertyKey) {
            return Integer.valueOf(get(propertyKey));
        }
    }

    /** A service whose search engine access is replaced by fixtures. */
    private static class TestService extends DocumentReportService {
        final List<Map<String, Long>> signaturePages = new ArrayList<>();
        final List<Map<String, Object>> documents = new ArrayList<>();
        final List<QueryBuilder> scannedQueries = new ArrayList<>();
        final List<List<SortBuilder<?>>> scannedSorts = new ArrayList<>();
        RuntimeException groupFailure;
        List<DuplicateGroup> groups = new ArrayList<>();
        int requestedGroupSize;
        int requestedDocsSize;
        int requestedPageSize;

        TestService(final TestFessConfig fessConfig) {
            this.fessConfig = fessConfig;
        }

        @Override
        protected long getCurrentTimeAsLong() {
            return NOW;
        }

        @Override
        protected List<DuplicateGroup> searchDuplicateGroups(final QueryBuilder query, final int groupSize, final int docsSize) {
            requestedGroupSize = groupSize;
            requestedDocsSize = docsSize;
            if (groupFailure != null) {
                throw groupFailure;
            }
            return groups;
        }

        @Override
        protected void scanDuplicateSignatures(final QueryBuilder query, final int pageSize,
                final Consumer<Map<String, Long>> pageHandler) {
            requestedPageSize = pageSize;
            signaturePages.forEach(pageHandler);
        }

        @Override
        protected void scanDocuments(final QueryBuilder query, final List<SortBuilder<?>> sorts, final boolean withHash,
                final Consumer<Map<String, Object>> handler) {
            scannedQueries.add(query);
            scannedSorts.add(sorts);
            // like the search engine, only the documents of the requested signatures come back
            final String json = query.toString();
            documents.stream().filter(doc -> !withHash || json.contains((String) doc.get(DOC_HASH))).forEach(handler);
        }
    }

    private static Map<String, Object> doc(final String hash, final String url, final String title) {
        final Map<String, Object> doc = new HashMap<>();
        doc.put(DocumentReportService.DOC_HASH, hash);
        doc.put(DocumentReportService.DOC_URL, url);
        doc.put(DocumentReportService.DOC_TITLE, title);
        doc.put(DocumentReportService.DOC_FILENAME, "a.docx");
        doc.put(DocumentReportService.DOC_CONTENT_LENGTH, 1234L);
        doc.put(DocumentReportService.DOC_LAST_MODIFIED, new Date(0L));
        doc.put(DocumentReportService.DOC_OWNER, "alice");
        doc.put(DocumentReportService.DOC_LAST_MODIFIER, "bob");
        doc.put(DocumentReportService.DOC_CLICK_COUNT, 3L);
        doc.put(DocumentReportService.DOC_DOC_ID, "id-" + url);
        return doc;
    }

    private static List<String[]> parse(final String csv) throws IOException {
        final List<String[]> rows = new ArrayList<>();
        try (CsvReader reader = new CsvReader(new StringReader(csv), CsvUtil.createCsvConfig())) {
            List<String> values;
            while ((values = reader.readValues()) != null) {
                // the reader reports the end of the last line as an empty record
                if (!values.isEmpty() && !(values.size() == 1 && values.get(0).isEmpty())) {
                    rows.add(values.toArray(new String[0]));
                }
            }
        }
        return rows;
    }

    @Test
    public void test_buildBaseQuery() {
        final TestService service = new TestService(new TestFessConfig());
        assertTrue(service.buildBaseQuery(null).toString().contains("match_all"));
        assertTrue(service.buildBaseQuery("  ").toString().contains("match_all"));
        final String json = service.buildBaseQuery(" smb://server/share/ ").toString();
        assertTrue(json.contains("\"prefix\""), json);
        assertTrue(json.contains("\"smb://server/share/\""), json);
    }

    @Test
    public void test_buildDuplicateQuery() {
        final TestService service = new TestService(new TestFessConfig());
        final String json = service.buildDuplicateQuery("smb://server/").toString();
        assertTrue(json.contains("\"must_not\""), json);
        assertTrue(json.contains("\"regexp\""), json);
        assertTrue(json.contains("\"content_minhash_bits\""), json);
        assertTrue(json.contains("\"1+\""), json);
        assertTrue(json.contains("\"smb://server/\""), json);
    }

    @Test
    public void test_buildDormantQuery() {
        final TestService service = new TestService(new TestFessConfig());
        final String json = service.buildDormantQuery(null, 30, false).toString();
        assertTrue(json.contains("\"last_modified\""), json);
        assertTrue(json.contains("\"to\" : " + (NOW - 30L * 24 * 60 * 60 * 1000)), json);
        assertTrue(json.contains("\"include_upper\" : false"), json);
        assertTrue(json.contains("\"format\" : \"epoch_millis\""), json);
        assertFalse(json.contains("click_count"), json);
        assertFalse(json.contains("prefix"), json);

        final String unclicked = service.buildDormantQuery("file:/data/", 1, true).toString();
        assertTrue(unclicked.contains("\"must_not\""), unclicked);
        assertTrue(unclicked.contains("\"click_count\""), unclicked);
        assertTrue(unclicked.contains("\"file:/data/\""), unclicked);
    }

    @Test
    public void test_getDuplicateReport() {
        final TestService service = new TestService(new TestFessConfig());
        service.groups = List.of(new DuplicateGroup("0101", 3, List.of(doc("0101", "u1", "t1"))));
        final DocumentReport report = service.getDuplicateReport(null);
        assertFalse(report.isFailed());
        assertFalse(report.isUnavailable());
        assertEquals(1, report.getGroups().size());
        assertEquals(3L, report.getGroups().get(0).getCount());
        assertEquals(100, service.requestedGroupSize);
        assertEquals(10, service.requestedDocsSize);
    }

    @Test
    public void test_getDuplicateReport_failed() {
        final TestService service = new TestService(new TestFessConfig());
        service.groupFailure = new IllegalStateException("test");
        final DocumentReport report = service.getDuplicateReport(null);
        assertTrue(report.isFailed());
        assertTrue(report.getGroups().isEmpty());
    }

    @Test
    public void test_getDuplicateReport_unavailableOnManagedServices() {
        for (final String type : new String[] { "cloud", "aws" }) {
            final TestFessConfig fessConfig = new TestFessConfig();
            fessConfig.overrides.put(FessConfig.search_engine_TYPE, type);
            final TestService service = new TestService(fessConfig);
            assertFalse(service.isDuplicateReportAvailable(), type);
            final DocumentReport report = service.getDuplicateReport(null);
            assertTrue(report.isUnavailable(), type);
            assertEquals(0, service.requestedGroupSize, type);
        }
        assertTrue(new TestService(new TestFessConfig()).isDuplicateReportAvailable());
    }

    @Test
    public void test_exportDuplicateCsv() throws IOException {
        final TestService service = new TestService(new TestFessConfig());
        final Map<String, Long> page1 = new LinkedHashMap<>();
        page1.put("0001", 2L);
        page1.put("0002", 3L);
        final Map<String, Long> page2 = new LinkedHashMap<>();
        page2.put("1000", 2L);
        service.signaturePages.add(page1);
        service.signaturePages.add(page2);
        service.documents.add(doc("0001", "u1", "=HYPERLINK(\"x\")"));
        service.documents.add(doc("0001", "u2", "t2"));
        service.documents.add(doc("0002", "u3", "t3"));
        service.documents.add(doc("0002", "u4", "t4"));
        service.documents.add(doc("0002", "u5", "t5"));
        service.documents.add(doc("1000", "u6", "t6"));
        service.documents.add(doc("1000", "u7", "t7"));

        final StringWriter writer = new StringWriter();
        service.exportDuplicateCsv(null, writer);
        final List<String[]> rows = parse(writer.toString());

        assertEquals(8, rows.size());
        assertEquals(DocumentReportService.DUPLICATE_CSV_COLUMNS, List.of(rows.get(0)));
        assertEquals(
                List.of("1", "2", "u1", "'=HYPERLINK(\"x\")", "a.docx", "1234", "1970-01-01T00:00:00.000Z", "alice", "bob", "3", "id-u1"),
                List.of(rows.get(1)));
        assertEquals("1", rows.get(2)[0]);
        assertEquals("2", rows.get(3)[0]);
        assertEquals("3", rows.get(3)[1]);
        assertEquals("2", rows.get(5)[0]);
        assertEquals("3", rows.get(6)[0]);
        assertEquals("u7", rows.get(7)[2]);
        assertEquals(10000, service.requestedPageSize);
        // one document scan per page of signatures, ordered by signature first
        assertEquals(2, service.scannedQueries.size());
        assertTrue(service.scannedSorts.get(0).get(0).toString().contains("content_minhash_bits"));
        assertTrue(service.scannedQueries.get(1).toString().contains("1000"));
        assertFalse(service.scannedQueries.get(1).toString().contains("0002"));
    }

    @Test
    public void test_exportDuplicateCsv_unavailable() throws IOException {
        final TestFessConfig fessConfig = new TestFessConfig();
        fessConfig.overrides.put(FessConfig.search_engine_TYPE, "aws");
        final TestService service = new TestService(fessConfig);
        service.signaturePages.add(Map.of("0001", 2L));
        final StringWriter writer = new StringWriter();
        service.exportDuplicateCsv(null, writer);
        final List<String[]> rows = parse(writer.toString());
        assertEquals(1, rows.size());
        assertEquals(DocumentReportService.DUPLICATE_CSV_COLUMNS, List.of(rows.get(0)));
        assertTrue(service.scannedQueries.isEmpty());
    }

    @Test
    public void test_exportDormantCsv() throws IOException {
        final TestService service = new TestService(new TestFessConfig());
        final Map<String, Object> noDate = doc(null, "u2", "t2");
        noDate.remove(DocumentReportService.DOC_LAST_MODIFIED);
        service.documents.add(doc(null, "u1", "t1"));
        service.documents.add(noDate);

        final StringWriter writer = new StringWriter();
        service.exportDormantCsv("smb://server/", 365, true, writer);
        final List<String[]> rows = parse(writer.toString());

        assertEquals(3, rows.size());
        assertEquals(DocumentReportService.DORMANT_CSV_COLUMNS, List.of(rows.get(0)));
        assertEquals(List.of("u1", "t1", "a.docx", "1234", "1970-01-01T00:00:00.000Z", "alice", "bob", "3", "id-u1"), List.of(rows.get(1)));
        assertEquals("", rows.get(2)[4]);
        final String query = service.scannedQueries.get(0).toString();
        assertTrue(query.contains("smb://server/"), query);
        assertTrue(query.contains("click_count"), query);
        assertTrue(service.scannedSorts.get(0).get(0).toString().contains("last_modified"));
    }

    @Test
    public void test_toReportDocument() {
        final TestService service = new TestService(new TestFessConfig());
        final Map<String, Object> source = new HashMap<>();
        source.put("url", "smb://server/share/a.docx");
        source.put("title", "A");
        source.put("filename", "a.docx");
        source.put("content_length", 10);
        source.put("last_modified", "2020-01-02T03:04:05.000Z");
        source.put("owner", "alice");
        source.put("last_modifier", "bob");
        source.put("click_count", 0);
        source.put("doc_id", "d1");
        final Map<String, Object> doc = service.toReportDocument(source);
        assertEquals("smb://server/share/a.docx", doc.get(DocumentReportService.DOC_URL));
        assertEquals("A", doc.get(DocumentReportService.DOC_TITLE));
        assertEquals(10, doc.get(DocumentReportService.DOC_CONTENT_LENGTH));
        assertEquals(1577934245000L, ((Date) doc.get(DocumentReportService.DOC_LAST_MODIFIED)).getTime());
        assertEquals("alice", doc.get(DocumentReportService.DOC_OWNER));
        assertEquals("bob", doc.get(DocumentReportService.DOC_LAST_MODIFIER));
        assertEquals(0, doc.get(DocumentReportService.DOC_CLICK_COUNT));
        assertEquals("d1", doc.get(DocumentReportService.DOC_DOC_ID));

        assertNull(service.toReportDocument(Map.of()).get(DocumentReportService.DOC_LAST_MODIFIED));
        assertTrue(service.toReportDocument(null).isEmpty());
    }
}
