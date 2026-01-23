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
package org.codelibs.fess.job;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.BooleanFunction;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

public class IndexExportJobTest extends UnitFessTestCase {

    private IndexExportJob indexExportJob;
    private Path tempDir;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        indexExportJob = new IndexExportJob();
        tempDir = Files.createTempDirectory("indexExportJobTest");
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        deleteRecursive(tempDir);
        super.tearDown(testInfo);
    }

    private void deleteRecursive(final Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.list(path).forEach(child -> {
                try {
                    deleteRecursive(child);
                } catch (final IOException e) {
                    // ignore
                }
            });
        }
        Files.deleteIfExists(path);
    }

    private void setupMockComponents(final List<Map<String, Object>> documents) {
        final SearchEngineClient searchEngineClient = new SearchEngineClient() {
            @Override
            public long scrollSearch(final String index, final SearchCondition<org.opensearch.action.search.SearchRequestBuilder> condition,
                    final BooleanFunction<Map<String, Object>> cursor) {
                long count = 0;
                for (final Map<String, Object> doc : documents) {
                    count++;
                    if (!cursor.apply(doc)) {
                        break;
                    }
                }
                return count;
            }
        };

        final FessConfig fessConfig = new TestFessConfig(tempDir.toString(), "cache", "100");
        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        ComponentUtil.setFessConfig(fessConfig);
    }

    // --- query() tests ---

    @Test
    public void test_query_returnsThis() {
        final IndexExportJob result = indexExportJob.query(QueryBuilders.matchAllQuery());
        assertSame(indexExportJob, result);
    }

    @Test
    public void test_query_acceptsNull() {
        final IndexExportJob result = indexExportJob.query(null);
        assertSame(indexExportJob, result);
    }

    // --- execute() tests ---

    @Test
    public void test_execute_success_noDocuments() {
        setupMockComponents(Collections.emptyList());

        final String result = indexExportJob.execute();

        assertEquals("Exported 0 documents.", result);
    }

    @Test
    public void test_execute_success_singleDocument() {
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("url", "https://example.com/page.html");
        doc.put("title", "Test Page");
        doc.put("content", "Hello World");
        setupMockComponents(Collections.singletonList(doc));

        final String result = indexExportJob.execute();

        assertEquals("Exported 1 documents.", result);
        final Path expectedFile = tempDir.resolve("example.com/page.html");
        assertTrue(Files.exists(expectedFile));
    }

    @Test
    public void test_execute_success_multipleDocuments() {
        final List<Map<String, Object>> docs = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            final Map<String, Object> doc = new LinkedHashMap<>();
            doc.put("url", "https://example.com/page" + i + ".html");
            doc.put("title", "Page " + i);
            doc.put("content", "Content " + i);
            docs.add(doc);
        }
        setupMockComponents(docs);

        final String result = indexExportJob.execute();

        assertEquals("Exported 3 documents.", result);
        for (int i = 0; i < 3; i++) {
            assertTrue(Files.exists(tempDir.resolve("example.com/page" + i + ".html")));
        }
    }

    @Test
    public void test_execute_withCustomQuery() {
        final List<QueryBuilder> capturedQueries = new ArrayList<>();
        final SearchEngineClient searchEngineClient = new SearchEngineClient() {
            @Override
            public long scrollSearch(final String index, final SearchCondition<org.opensearch.action.search.SearchRequestBuilder> condition,
                    final BooleanFunction<Map<String, Object>> cursor) {
                return 0;
            }
        };
        final FessConfig fessConfig = new TestFessConfig(tempDir.toString(), "cache", "100");
        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        ComponentUtil.setFessConfig(fessConfig);

        final QueryBuilder customQuery = QueryBuilders.termQuery("host", "example.com");
        indexExportJob.query(customQuery).execute();

        // Verifies no exception thrown with custom query
    }

    @Test
    public void test_execute_withDefaultQuery() {
        setupMockComponents(Collections.emptyList());

        // No query set, should use matchAllQuery
        final String result = indexExportJob.execute();

        assertEquals("Exported 0 documents.", result);
    }

    @Test
    public void test_execute_withException() {
        final SearchEngineClient searchEngineClient = new SearchEngineClient() {
            @Override
            public long scrollSearch(final String index, final SearchCondition<org.opensearch.action.search.SearchRequestBuilder> condition,
                    final BooleanFunction<Map<String, Object>> cursor) {
                throw new RuntimeException("Search engine error");
            }
        };
        final FessConfig fessConfig = new TestFessConfig(tempDir.toString(), "cache", "100");
        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        ComponentUtil.setFessConfig(fessConfig);

        final String result = indexExportJob.execute();

        assertTrue(result.contains("Search engine error"));
        assertTrue(result.endsWith("\n"));
    }

    @Test
    public void test_execute_skipsDocumentsWithoutUrl() {
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("title", "No URL Doc");
        doc.put("content", "Content without URL");
        setupMockComponents(Collections.singletonList(doc));

        final String result = indexExportJob.execute();

        assertEquals("Exported 1 documents.", result);
        // No file should be created
        assertFalse(Files.exists(tempDir.resolve("_invalid")));
    }

    @Test
    public void test_execute_excludeFieldsFromConfig() {
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("url", "https://example.com/page.html");
        doc.put("title", "Test");
        doc.put("content", "Body");
        doc.put("cache", "cached content");
        doc.put("host", "example.com");

        final FessConfig fessConfig = new TestFessConfig(tempDir.toString(), "cache,internal_field", "100");
        final SearchEngineClient searchEngineClient = new SearchEngineClient() {
            @Override
            public long scrollSearch(final String index, final SearchCondition<org.opensearch.action.search.SearchRequestBuilder> condition,
                    final BooleanFunction<Map<String, Object>> cursor) {
                cursor.apply(doc);
                return 1;
            }
        };
        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        ComponentUtil.setFessConfig(fessConfig);

        indexExportJob.execute();

        final Path file = tempDir.resolve("example.com/page.html");
        assertTrue(Files.exists(file));
        try {
            final String html = Files.readString(file, StandardCharsets.UTF_8);
            assertFalse(html.contains("fess:cache"));
            assertTrue(html.contains("fess:host"));
        } catch (final IOException e) {
            fail("Failed to read exported file: " + e.getMessage());
        }
    }

    @Test
    public void test_execute_usesCorrectIndex() {
        final List<String> capturedIndices = new ArrayList<>();
        final SearchEngineClient searchEngineClient = new SearchEngineClient() {
            @Override
            public long scrollSearch(final String index, final SearchCondition<org.opensearch.action.search.SearchRequestBuilder> condition,
                    final BooleanFunction<Map<String, Object>> cursor) {
                capturedIndices.add(index);
                return 0;
            }
        };
        final FessConfig fessConfig = new TestFessConfig(tempDir.toString(), "cache", "50");
        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        ComponentUtil.setFessConfig(fessConfig);

        indexExportJob.execute();

        assertEquals(1, capturedIndices.size());
        assertEquals("fess.search", capturedIndices.get(0));
    }

    // --- buildFilePath() tests ---

    @Test
    public void test_buildFilePath_simpleUrl() {
        final Path result = indexExportJob.buildFilePath("/export", "https://example.com/docs/guide.html");
        assertEquals(Path.of("/export/example.com/docs/guide.html"), result);
    }

    @Test
    public void test_buildFilePath_rootUrl() {
        final Path result = indexExportJob.buildFilePath("/export", "https://example.com/");
        assertEquals(Path.of("/export/example.com/index.html"), result);
    }

    @Test
    public void test_buildFilePath_noPath() {
        final Path result = indexExportJob.buildFilePath("/export", "https://example.com");
        assertEquals(Path.of("/export/example.com/index.html"), result);
    }

    @Test
    public void test_buildFilePath_noExtension() {
        final Path result = indexExportJob.buildFilePath("/export", "https://example.com/docs/guide");
        assertEquals(Path.of("/export/example.com/docs/guide.html"), result);
    }

    @Test
    public void test_buildFilePath_trailingSlash() {
        final Path result = indexExportJob.buildFilePath("/export", "https://example.com/docs/");
        assertEquals(Path.of("/export/example.com/docs/index.html"), result);
    }

    @Test
    public void test_buildFilePath_deepPath() {
        final Path result = indexExportJob.buildFilePath("/export", "https://example.com/a/b/c/d/page.html");
        assertEquals(Path.of("/export/example.com/a/b/c/d/page.html"), result);
    }

    @Test
    public void test_buildFilePath_httpScheme() {
        final Path result = indexExportJob.buildFilePath("/export", "http://example.com/page.html");
        assertEquals(Path.of("/export/example.com/page.html"), result);
    }

    @Test
    public void test_buildFilePath_fileScheme() {
        final Path result = indexExportJob.buildFilePath("/export", "file:///home/user/doc.html");
        assertEquals(Path.of("/export/_local/home/user/doc.html"), result);
    }

    @Test
    public void test_buildFilePath_specialCharacters() {
        // < in URL makes it an invalid URI, so it falls back to hash-based path
        final Path result = indexExportJob.buildFilePath("/export", "https://example.com/path/file<name>.html");
        assertTrue(result.toString().startsWith("/export/_invalid/"));
        assertTrue(result.toString().endsWith(".html"));
    }

    @Test
    public void test_buildFilePath_colonInFilename() {
        // Colon is valid in URI path but should be sanitized in filesystem path
        final Path result = indexExportJob.buildFilePath("/export", "https://example.com/path/file%3Aname.html");
        assertEquals(Path.of("/export/example.com/path/file_name.html"), result);
    }

    @Test
    public void test_buildFilePath_questionMarkInPath() {
        final Path result = indexExportJob.buildFilePath("/export", "https://example.com/page?query=1");
        // URI.getPath() returns /page for this URL (query is separate)
        assertEquals(Path.of("/export/example.com/page.html"), result);
    }

    @Test
    public void test_buildFilePath_longPathComponent() {
        final StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 250; i++) {
            longName.append("a");
        }
        final Path result = indexExportJob.buildFilePath("/export", "https://example.com/" + longName + ".html");
        final String fileName = result.getFileName().toString();
        assertTrue(fileName.length() <= 200);
    }

    @Test
    public void test_buildFilePath_invalidUrl() {
        final Path result = indexExportJob.buildFilePath("/export", "not a valid url %%%");
        assertTrue(result.toString().startsWith("/export/_invalid/"));
        assertTrue(result.toString().endsWith(".html"));
    }

    @Test
    public void test_buildFilePath_dotInDirectoryName() {
        final Path result = indexExportJob.buildFilePath("/export", "https://example.com/v1.0/page");
        // v1.0 is a directory, page has no extension
        assertEquals(Path.of("/export/example.com/v1.0/page.html"), result);
    }

    @Test
    public void test_buildFilePath_multipleDotsInFilename() {
        final Path result = indexExportJob.buildFilePath("/export", "https://example.com/file.name.html");
        assertEquals(Path.of("/export/example.com/file.name.html"), result);
    }

    @Test
    public void test_buildFilePath_pdfExtension() {
        final Path result = indexExportJob.buildFilePath("/export", "https://example.com/doc.pdf");
        assertEquals(Path.of("/export/example.com/doc.pdf"), result);
    }

    @Test
    public void test_buildFilePath_portInUrl() {
        final Path result = indexExportJob.buildFilePath("/export", "https://example.com:8080/page.html");
        assertEquals(Path.of("/export/example.com/page.html"), result);
    }

    @Test
    public void test_buildFilePath_encodedCharacters() {
        final Path result = indexExportJob.buildFilePath("/export", "https://example.com/path%20with%20spaces/file.html");
        assertEquals(Path.of("/export/example.com/path with spaces/file.html"), result);
    }

    // --- buildHtml() tests ---

    @Test
    public void test_buildHtml_basicDocument() {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("title", "Test Title");
        source.put("content", "Test Content");
        source.put("lang", "en");
        source.put("url", "https://example.com/page.html");

        final String html = indexExportJob.buildHtml(source, Collections.emptySet());

        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("<html lang=\"en\">"));
        assertTrue(html.contains("<title>Test Title</title>"));
        assertTrue(html.contains("<meta charset=\"UTF-8\">"));
        assertTrue(html.contains("Test Content"));
        assertTrue(html.contains("<meta name=\"fess:url\" content=\"https://example.com/page.html\">"));
    }

    @Test
    public void test_buildHtml_htmlEscaping() {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("title", "Title with <script>alert('xss')</script>");
        source.put("content", "Content with & < > \" '");
        source.put("lang", "en");

        final String html = indexExportJob.buildHtml(source, Collections.emptySet());

        assertTrue(html.contains("<title>Title with &lt;script&gt;alert(&#39;xss&#39;)&lt;/script&gt;</title>"));
        assertTrue(html.contains("Content with &amp; &lt; &gt; &quot; &#39;"));
        assertFalse(html.contains("<script>"));
    }

    @Test
    public void test_buildHtml_excludeFields() {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("title", "Test");
        source.put("content", "Body");
        source.put("cache", "cached data");
        source.put("host", "example.com");

        final Set<String> excludeFields = Set.of("cache");
        final String html = indexExportJob.buildHtml(source, excludeFields);

        assertFalse(html.contains("fess:cache"));
        assertTrue(html.contains("fess:host"));
    }

    @Test
    public void test_buildHtml_collectionField() {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("title", "Test");
        source.put("content", "Body");
        source.put("anchor", Arrays.asList("http://a.com", "http://b.com", "http://c.com"));

        final String html = indexExportJob.buildHtml(source, Collections.emptySet());

        int count = 0;
        int idx = 0;
        while ((idx = html.indexOf("fess:anchor", idx)) != -1) {
            count++;
            idx++;
        }
        assertEquals(3, count);
        assertTrue(html.contains("content=\"http://a.com\""));
        assertTrue(html.contains("content=\"http://b.com\""));
        assertTrue(html.contains("content=\"http://c.com\""));
    }

    @Test
    public void test_buildHtml_emptyCollection() {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("title", "Test");
        source.put("content", "Body");
        source.put("anchor", Collections.emptyList());

        final String html = indexExportJob.buildHtml(source, Collections.emptySet());

        assertFalse(html.contains("fess:anchor"));
    }

    @Test
    public void test_buildHtml_nullValue() {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("title", "Test");
        source.put("content", "Body");
        source.put("field_with_null", null);

        final String html = indexExportJob.buildHtml(source, Collections.emptySet());

        assertFalse(html.contains("fess:field_with_null"));
    }

    @Test
    public void test_buildHtml_missingTitle() {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("content", "Body");
        source.put("url", "https://example.com/");

        final String html = indexExportJob.buildHtml(source, Collections.emptySet());

        assertTrue(html.contains("<title></title>"));
    }

    @Test
    public void test_buildHtml_missingContent() {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("title", "Test");
        source.put("url", "https://example.com/");

        final String html = indexExportJob.buildHtml(source, Collections.emptySet());

        assertTrue(html.contains("<body>"));
        assertTrue(html.contains("</body>"));
    }

    @Test
    public void test_buildHtml_missingLang() {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("title", "Test");
        source.put("content", "Body");

        final String html = indexExportJob.buildHtml(source, Collections.emptySet());

        assertTrue(html.contains("<html lang=\"\">"));
    }

    @Test
    public void test_buildHtml_numericFieldValue() {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("title", "Test");
        source.put("content", "Body");
        source.put("boost", 1.5);
        source.put("content_length", 12345);

        final String html = indexExportJob.buildHtml(source, Collections.emptySet());

        assertTrue(html.contains("<meta name=\"fess:boost\" content=\"1.5\">"));
        assertTrue(html.contains("<meta name=\"fess:content_length\" content=\"12345\">"));
    }

    @Test
    public void test_buildHtml_titleContentLangNotInMeta() {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("title", "My Title");
        source.put("content", "My Content");
        source.put("lang", "ja");
        source.put("url", "https://example.com/");

        final String html = indexExportJob.buildHtml(source, Collections.emptySet());

        assertFalse(html.contains("fess:title"));
        assertFalse(html.contains("fess:content"));
        assertFalse(html.contains("fess:lang"));
        assertTrue(html.contains("fess:url"));
    }

    @Test
    public void test_buildHtml_multipleExcludeFields() {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("title", "Test");
        source.put("content", "Body");
        source.put("cache", "cached");
        source.put("segment", "seg1");
        source.put("host", "example.com");

        final Set<String> excludeFields = Set.of("cache", "segment");
        final String html = indexExportJob.buildHtml(source, excludeFields);

        assertFalse(html.contains("fess:cache"));
        assertFalse(html.contains("fess:segment"));
        assertTrue(html.contains("fess:host"));
    }

    // --- exportDocument() tests ---

    @Test
    public void test_exportDocument_createsFile() {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("url", "https://example.com/test.html");
        source.put("title", "Test");
        source.put("content", "Hello");

        indexExportJob.exportDocument(source, tempDir.toString(), Collections.emptySet());

        final Path expectedFile = tempDir.resolve("example.com/test.html");
        assertTrue(Files.exists(expectedFile));
    }

    @Test
    public void test_exportDocument_fileContent() throws IOException {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("url", "https://example.com/test.html");
        source.put("title", "My Title");
        source.put("content", "My Content");
        source.put("lang", "ja");

        indexExportJob.exportDocument(source, tempDir.toString(), Collections.emptySet());

        final Path file = tempDir.resolve("example.com/test.html");
        final String content = Files.readString(file, StandardCharsets.UTF_8);
        assertTrue(content.contains("<title>My Title</title>"));
        assertTrue(content.contains("My Content"));
        assertTrue(content.contains("<html lang=\"ja\">"));
    }

    @Test
    public void test_exportDocument_createsDirectories() {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("url", "https://example.com/a/b/c/deep.html");
        source.put("title", "Deep");
        source.put("content", "Content");

        indexExportJob.exportDocument(source, tempDir.toString(), Collections.emptySet());

        assertTrue(Files.exists(tempDir.resolve("example.com/a/b/c/deep.html")));
    }

    @Test
    public void test_exportDocument_skipWithoutUrl() {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("title", "No URL");
        source.put("content", "Content");

        indexExportJob.exportDocument(source, tempDir.toString(), Collections.emptySet());

        // No files created
        try {
            assertEquals(0, Files.list(tempDir).count());
        } catch (final IOException e) {
            fail("Failed to list temp directory: " + e.getMessage());
        }
    }

    @Test
    public void test_exportDocument_skipWithNullUrl() {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("url", null);
        source.put("title", "Null URL");

        indexExportJob.exportDocument(source, tempDir.toString(), Collections.emptySet());

        try {
            assertEquals(0, Files.list(tempDir).count());
        } catch (final IOException e) {
            fail("Failed to list temp directory: " + e.getMessage());
        }
    }

    @Test
    public void test_exportDocument_overwritesExistingFile() throws IOException {
        final Map<String, Object> source1 = new LinkedHashMap<>();
        source1.put("url", "https://example.com/page.html");
        source1.put("title", "First");
        source1.put("content", "First content");

        final Map<String, Object> source2 = new LinkedHashMap<>();
        source2.put("url", "https://example.com/page.html");
        source2.put("title", "Second");
        source2.put("content", "Second content");

        indexExportJob.exportDocument(source1, tempDir.toString(), Collections.emptySet());
        indexExportJob.exportDocument(source2, tempDir.toString(), Collections.emptySet());

        final Path file = tempDir.resolve("example.com/page.html");
        final String content = Files.readString(file, StandardCharsets.UTF_8);
        assertTrue(content.contains("Second"));
        assertFalse(content.contains("First"));
    }

    @Test
    public void test_exportDocument_excludeFields() throws IOException {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("url", "https://example.com/page.html");
        source.put("title", "Test");
        source.put("content", "Body");
        source.put("cache", "should be excluded");
        source.put("host", "example.com");

        indexExportJob.exportDocument(source, tempDir.toString(), Set.of("cache"));

        final Path file = tempDir.resolve("example.com/page.html");
        final String content = Files.readString(file, StandardCharsets.UTF_8);
        assertFalse(content.contains("should be excluded"));
        assertTrue(content.contains("fess:host"));
    }

    @Test
    public void test_exportDocument_withCollectionField() throws IOException {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("url", "https://example.com/page.html");
        source.put("title", "Test");
        source.put("content", "Body");
        source.put("role", Arrays.asList("admin", "user"));

        indexExportJob.exportDocument(source, tempDir.toString(), Collections.emptySet());

        final Path file = tempDir.resolve("example.com/page.html");
        final String content = Files.readString(file, StandardCharsets.UTF_8);
        assertTrue(content.contains("<meta name=\"fess:role\" content=\"admin\">"));
        assertTrue(content.contains("<meta name=\"fess:role\" content=\"user\">"));
    }

    // --- Integration-style tests ---

    @Test
    public void test_execute_fullFlow() throws IOException {
        final List<Map<String, Object>> docs = new ArrayList<>();
        final Map<String, Object> doc1 = new LinkedHashMap<>();
        doc1.put("url", "https://example.com/page1.html");
        doc1.put("title", "Page 1");
        doc1.put("content", "Content 1");
        doc1.put("lang", "en");
        doc1.put("host", "example.com");
        doc1.put("cache", "cached1");
        docs.add(doc1);

        final Map<String, Object> doc2 = new LinkedHashMap<>();
        doc2.put("url", "https://other.com/docs/guide");
        doc2.put("title", "Guide");
        doc2.put("content", "Guide content");
        doc2.put("lang", "ja");
        doc2.put("anchor", Arrays.asList("https://a.com", "https://b.com"));
        docs.add(doc2);

        setupMockComponents(docs);

        final String result = indexExportJob.execute();

        assertEquals("Exported 2 documents.", result);

        // Verify first document
        final Path file1 = tempDir.resolve("example.com/page1.html");
        assertTrue(Files.exists(file1));
        final String html1 = Files.readString(file1, StandardCharsets.UTF_8);
        assertTrue(html1.contains("<title>Page 1</title>"));
        assertTrue(html1.contains("<html lang=\"en\">"));
        assertFalse(html1.contains("fess:cache"));
        assertTrue(html1.contains("fess:host"));

        // Verify second document (no extension in URL -> .html appended)
        final Path file2 = tempDir.resolve("other.com/docs/guide.html");
        assertTrue(Files.exists(file2));
        final String html2 = Files.readString(file2, StandardCharsets.UTF_8);
        assertTrue(html2.contains("<title>Guide</title>"));
        assertTrue(html2.contains("<html lang=\"ja\">"));
        assertTrue(html2.contains("fess:anchor"));
    }

    @Test
    public void test_execute_withFluentApi() {
        setupMockComponents(Collections.emptyList());

        final String result = indexExportJob.query(QueryBuilders.termQuery("host", "example.com")).execute();

        assertEquals("Exported 0 documents.", result);
    }

    // --- Edge cases ---

    @Test
    public void test_buildFilePath_emptyString() {
        // Empty string is a valid relative URI with null host and empty path
        final Path result = indexExportJob.buildFilePath("/export", "");
        assertEquals(Path.of("/export/_local/index.html"), result);
    }

    @Test
    public void test_buildFilePath_fragmentUrl() {
        final Path result = indexExportJob.buildFilePath("/export", "https://example.com/page.html#section1");
        assertEquals(Path.of("/export/example.com/page.html"), result);
    }

    @Test
    public void test_buildFilePath_consistentHashForSameUrl() {
        final Path result1 = indexExportJob.buildFilePath("/export", "not valid %%%");
        final Path result2 = indexExportJob.buildFilePath("/export", "not valid %%%");
        assertEquals(result1, result2);
    }

    @Test
    public void test_buildFilePath_differentHashForDifferentUrls() {
        final Path result1 = indexExportJob.buildFilePath("/export", "invalid url 1 %%%");
        final Path result2 = indexExportJob.buildFilePath("/export", "invalid url 2 %%%");
        assertFalse(result1.equals(result2));
    }

    @Test
    public void test_buildHtml_emptyMap() {
        final String html = indexExportJob.buildHtml(Collections.emptyMap(), Collections.emptySet());

        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("<title></title>"));
        assertTrue(html.contains("<html lang=\"\">"));
    }

    @Test
    public void test_buildHtml_specialCharactersInFieldName() {
        final Map<String, Object> source = new LinkedHashMap<>();
        source.put("title", "Test");
        source.put("content", "Body");
        source.put("field<with>special", "value");

        final String html = indexExportJob.buildHtml(source, Collections.emptySet());

        assertTrue(html.contains("fess:field&lt;with&gt;special"));
    }

    @Test
    public void test_execute_emptyExcludeFields() {
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("url", "https://example.com/page.html");
        doc.put("title", "Test");
        doc.put("content", "Body");
        doc.put("cache", "should appear");

        final SearchEngineClient searchEngineClient = new SearchEngineClient() {
            @Override
            public long scrollSearch(final String index, final SearchCondition<org.opensearch.action.search.SearchRequestBuilder> condition,
                    final BooleanFunction<Map<String, Object>> cursor) {
                cursor.apply(doc);
                return 1;
            }
        };
        // Empty exclude fields
        final FessConfig fessConfig = new TestFessConfig(tempDir.toString(), "", "100");
        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        ComponentUtil.setFessConfig(fessConfig);

        indexExportJob.execute();

        final Path file = tempDir.resolve("example.com/page.html");
        assertTrue(Files.exists(file));
        try {
            final String html = Files.readString(file, StandardCharsets.UTF_8);
            assertTrue(html.contains("fess:cache"));
        } catch (final IOException e) {
            fail("Failed to read file: " + e.getMessage());
        }
    }

    private static class TestFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;

        private final String exportPath;
        private final String excludeFields;
        private final String scrollSize;

        TestFessConfig(final String exportPath, final String excludeFields, final String scrollSize) {
            this.exportPath = exportPath;
            this.excludeFields = excludeFields;
            this.scrollSize = scrollSize;
        }

        @Override
        public String getIndexExportPath() {
            return exportPath;
        }

        @Override
        public String getIndexExportExcludeFields() {
            return excludeFields;
        }

        @Override
        public String getIndexExportScrollSize() {
            return scrollSize;
        }

        @Override
        public Integer getIndexExportScrollSizeAsInteger() {
            return Integer.valueOf(scrollSize);
        }

        @Override
        public String getIndexDocumentSearchIndex() {
            return "fess.search";
        }
    }
}
