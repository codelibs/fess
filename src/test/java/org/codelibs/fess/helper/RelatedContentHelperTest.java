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
package org.codelibs.fess.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codelibs.core.io.FileUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.exbhv.RelatedContentBhv;
import org.codelibs.fess.opensearch.config.exentity.RelatedContent;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class RelatedContentHelperTest extends UnitFessTestCase {

    private RelatedContentHelper relatedContentHelper;
    private MockRelatedContentBhv mockBhv;
    private VirtualHostHelper virtualHostHelper;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);

        // Setup system properties for DI container
        File file = File.createTempFile("test", ".properties");
        file.deleteOnExit();
        FileUtil.writeBytes(file.getAbsolutePath(), "test.property=test".getBytes("UTF-8"));
        DynamicProperties systemProps = new DynamicProperties(file);
        ComponentUtil.register(systemProps, "systemProperties");

        relatedContentHelper = new RelatedContentHelper() {
            @Override
            public List<RelatedContent> getAvailableRelatedContentList() {
                return mockBhv.getTestData();
            }
        };

        mockBhv = new MockRelatedContentBhv();
        virtualHostHelper = new VirtualHostHelper();

        ComponentUtil.register(mockBhv, "relatedContentBhv");
        ComponentUtil.register(virtualHostHelper, "virtualHostHelper");
        ComponentUtil.register(new MockFessConfig(), "fessConfig");

        inject(relatedContentHelper);
        inject(virtualHostHelper);
    }

    @Test
    public void test_init() {
        // Setup test data
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("test", "Test Content", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.init();

        // Verify that load was called and data is processed
        String[] results = relatedContentHelper.getRelatedContents("test");
        assertEquals(1, results.length);
        assertEquals("Test Content", results[0]);
    }

    @Test
    public void test_getAvailableRelatedContentList() {
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("term1", "content1", ""));
        testData.add(createRelatedContent("term2", "content2", "host1"));
        mockBhv.setTestData(testData);

        List<RelatedContent> result = relatedContentHelper.getAvailableRelatedContentList();
        assertEquals(2, result.size());
        assertEquals("term1", result.get(0).getTerm());
        assertEquals("term2", result.get(1).getTerm());
    }

    @Test
    public void test_load_emptyList() {
        mockBhv.setTestData(new ArrayList<>());

        int count = relatedContentHelper.load();
        assertEquals(0, count);

        String[] results = relatedContentHelper.getRelatedContents("anyterm");
        assertEquals(0, results.length);
    }

    @Test
    public void test_load_simpleTerms() {
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("java", "Java Programming", ""));
        testData.add(createRelatedContent("python", "Python Programming", ""));
        mockBhv.setTestData(testData);

        int count = relatedContentHelper.load();
        assertEquals(1, count); // One virtual host key (empty)

        String[] results = relatedContentHelper.getRelatedContents("java");
        assertEquals(1, results.length);
        assertEquals("Java Programming", results[0]);

        results = relatedContentHelper.getRelatedContents("python");
        assertEquals(1, results.length);
        assertEquals("Python Programming", results[0]);
    }

    @Test
    public void test_load_caseInsensitiveTerms() {
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("Java", "Java Programming", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        // Test case insensitive matching
        String[] results = relatedContentHelper.getRelatedContents("java");
        assertEquals(1, results.length);
        assertEquals("Java Programming", results[0]);

        results = relatedContentHelper.getRelatedContents("JAVA");
        assertEquals(1, results.length);
        assertEquals("Java Programming", results[0]);

        results = relatedContentHelper.getRelatedContents("Java");
        assertEquals(1, results.length);
        assertEquals("Java Programming", results[0]);
    }

    @Test
    public void test_load_regexTerms() {
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("regex:test.*", "Test Content for __QUERY__", ""));
        testData.add(createRelatedContent("regex:[0-9]+", "Number: __QUERY__", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        String[] results = relatedContentHelper.getRelatedContents("testing");
        assertEquals(1, results.length);
        assertEquals("Test Content for testing", results[0]);

        results = relatedContentHelper.getRelatedContents("123");
        assertEquals(1, results.length);
        assertEquals("Number: 123", results[0]);

        results = relatedContentHelper.getRelatedContents("abc");
        assertEquals(0, results.length);
    }

    @Test
    public void test_load_invalidRegex() {
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("regex:", "Invalid Regex", ""));
        mockBhv.setTestData(testData);

        // Should not throw exception, just log warning
        relatedContentHelper.load();

        String[] results = relatedContentHelper.getRelatedContents("anything");
        assertEquals(0, results.length);
    }

    @Test
    public void test_load_mixedTermTypes() {
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("exact", "Exact Match", ""));
        testData.add(createRelatedContent("regex:test.*", "Regex Match: __QUERY__", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        // Test exact match
        String[] results = relatedContentHelper.getRelatedContents("exact");
        assertEquals(1, results.length);
        assertEquals("Exact Match", results[0]);

        // Test regex match
        results = relatedContentHelper.getRelatedContents("testing");
        assertEquals(1, results.length);
        assertEquals("Regex Match: testing", results[0]);
    }

    @Test
    public void test_load_virtualHosts() {
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("term1", "Content for host1", "host1"));
        testData.add(createRelatedContent("term1", "Content for host2", "host2"));
        testData.add(createRelatedContent("term2", "Content for default", ""));
        mockBhv.setTestData(testData);

        int count = relatedContentHelper.load();
        assertEquals(3, count); // Three virtual host keys: "host1", "host2", ""
    }

    @Test
    public void test_getHostKey_emptyVirtualHost() {
        RelatedContent entity = createRelatedContent("term", "content", "");
        String key = relatedContentHelper.getHostKey(entity);
        assertEquals("", key);
    }

    @Test
    public void test_getHostKey_nullVirtualHost() {
        RelatedContent entity = createRelatedContent("term", "content", null);
        String key = relatedContentHelper.getHostKey(entity);
        assertEquals("", key);
    }

    @Test
    public void test_getHostKey_withVirtualHost() {
        RelatedContent entity = createRelatedContent("term", "content", "example.com");
        String key = relatedContentHelper.getHostKey(entity);
        assertEquals("example.com", key);
    }

    @Test
    public void test_getRelatedContents_noMatch() {
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("java", "Java Content", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        String[] results = relatedContentHelper.getRelatedContents("python");
        assertEquals(0, results.length);
    }

    @Test
    public void test_getRelatedContents_multipleMatches() {
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("test", "Exact Match", ""));
        testData.add(createRelatedContent("regex:test.*", "Regex Match: __QUERY__", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        String[] results = relatedContentHelper.getRelatedContents("test");
        assertEquals(2, results.length);
        assertEquals("Exact Match", results[0]);
        assertEquals("Regex Match: test", results[1]);
    }

    @Test
    public void test_getRelatedContents_differentVirtualHost() {
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("test", "Content for host1", "host1"));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        // Mock virtual host helper to return empty key (different from host1)
        virtualHostHelper = new VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return "";
            }
        };
        ComponentUtil.register(virtualHostHelper, "virtualHostHelper");

        String[] results = relatedContentHelper.getRelatedContents("test");
        assertEquals(0, results.length);
    }

    @Test
    public void test_getRelatedContents_nullQuery() {
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("test", "Test Content", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        String[] results = relatedContentHelper.getRelatedContents(null);
        assertEquals(0, results.length);
    }

    @Test
    public void test_getRelatedContents_emptyQuery() {
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("emptytest", "Empty Term Content", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        String[] results = relatedContentHelper.getRelatedContents("");
        assertEquals(0, results.length);

        // Test with the actual term
        results = relatedContentHelper.getRelatedContents("emptytest");
        assertEquals(1, results.length);
        assertEquals("Empty Term Content", results[0]);
    }

    @Test
    public void test_setRegexPrefix() {
        assertEquals("regex:", relatedContentHelper.regexPrefix);

        relatedContentHelper.setRegexPrefix("pattern:");
        assertEquals("pattern:", relatedContentHelper.regexPrefix);

        // Test with the new prefix
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("pattern:test.*", "Pattern Match: __QUERY__", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        String[] results = relatedContentHelper.getRelatedContents("testing");
        assertEquals(1, results.length);
        assertEquals("Pattern Match: testing", results[0]);
    }

    @Test
    public void test_setQueryPlaceHolder() {
        assertEquals("__QUERY__", relatedContentHelper.queryPlaceHolder);

        relatedContentHelper.setQueryPlaceHolder("{{QUERY}}");
        assertEquals("{{QUERY}}", relatedContentHelper.queryPlaceHolder);

        // Test with the new placeholder
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("regex:test.*", "Content for {{QUERY}}", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        String[] results = relatedContentHelper.getRelatedContents("testing");
        assertEquals(1, results.length);
        assertEquals("Content for testing", results[0]);
    }

    @Test
    public void test_getRelatedContents_xssEscaped() {
        // Security regression test for reflected XSS via __QUERY__ substitution.
        // The query placeholder substitution feature must be preserved (no degradation from 15.6),
        // but the substituted query value must be HTML-escaped to prevent reflected XSS.
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("regex:.*", "Related: __QUERY__", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        // The script payload must be HTML-escaped (placeholder still substituted = no regression).
        String[] results = relatedContentHelper.getRelatedContents("<script>alert(1)</script>");
        assertEquals(1, results.length);
        assertEquals("Related: &lt;script&gt;alert(1)&lt;/script&gt;", results[0]);
        // Guard against the previous regression (placeholder removed) and escaping bypass.
        assertFalse(results[0].contains("<script>"));
        assertFalse(results[0].contains("</script>"));
        assertTrue(results[0].contains("&lt;script&gt;"));

        // Each special character is converted to the exact entity produced by LaFunctions.h.
        assertEscapedQuery("\"", "&#034;");
        assertEscapedQuery("'", "&#039;");
        assertEscapedQuery("<", "&lt;");
        assertEscapedQuery(">", "&gt;");
        assertEscapedQuery("&", "&amp;");
    }

    @Test
    public void test_getRelatedContents_normalQueryNotRegressed() {
        // Normal queries (no special characters) must still be substituted exactly as in 15.6.
        // HTML-escaping is an identity transform for such input, so there is no degradation.
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("regex:.*", "Related: __QUERY__", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        String[] results = relatedContentHelper.getRelatedContents("laptop");
        assertEquals(1, results.length);
        assertEquals("Related: laptop", results[0]);
    }

    @Test
    public void test_getRelatedContents_exactMatchNotAffected() {
        // Exact-match entries return the admin-registered content verbatim (no user input reflected),
        // so the admin HTML must not be escaped or altered.
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("test", "<a href=\"/foo\">Admin HTML</a>", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        String[] results = relatedContentHelper.getRelatedContents("test");
        assertEquals(1, results.length);
        assertEquals("<a href=\"/foo\">Admin HTML</a>", results[0]);
    }

    @Test
    public void test_getRelatedContents_urlQueryEncoded() {
        // __URL_QUERY__ must substitute the URL-encoded query so it is safe inside URL parameters.
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("regex:.*", "<a href=\"/s?q=__URL_QUERY__\">link</a>", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        String[] results = relatedContentHelper.getRelatedContents("a b&c<d");
        assertEquals(1, results.length);
        assertEquals("<a href=\"/s?q=" + java.net.URLEncoder.encode("a b&c<d", java.nio.charset.StandardCharsets.UTF_8) + "\">link</a>",
                results[0]);
        // The raw (unencoded) special characters must not appear in the output.
        assertFalse(results[0].contains(" b&c<d"));
    }

    @Test
    public void test_getRelatedContents_jsQueryEscaped() {
        // __JS_QUERY__ must substitute the JavaScript-escaped query so it is safe inside a JS string literal.
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("regex:.*", "<script>var q='__JS_QUERY__';</script>", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        String[] results = relatedContentHelper.getRelatedContents("';alert(1)//</script>");
        assertEquals(1, results.length);
        assertEquals(
                "<script>var q='" + org.apache.commons.text.StringEscapeUtils.escapeEcmaScript("';alert(1)//</script>") + "';</script>",
                results[0]);
        // The query-derived quote must be escaped and the closing tag must be neutralized (/ -> \/).
        assertTrue(results[0].contains("\\'"));
        assertTrue(results[0].contains("<\\/script>"));
        // The raw, unescaped query-derived closing tag (which would break out of the script block)
        // must not appear; it is neutralized to "<\/script>".
        assertFalse(results[0].contains("//</script>"));
    }

    @Test
    public void test_getRelatedContents_singlePassNoReSubstitution() {
        // Regression guard for the sequential-replace vulnerability: substitution must be a single pass,
        // so an encoded value (here a URL-encoded query) is never re-scanned and re-substituted through
        // another placeholder. If sequential replace() were used, the literal __JS_QUERY__ left in the
        // URL-encoded value would be replaced by escapeJs(query), turning \" into a raw " and breaking
        // out of the attribute (attribute injection).
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("regex:.*", "<a href=\"/s?q=__URL_QUERY__\">x</a>", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        final String payload = "__JS_QUERY__\" onmouseover=alert(1) x=\"";
        String[] results = relatedContentHelper.getRelatedContents(payload);
        assertEquals(1, results.length);
        // The raw attribute-injection sequence must not appear (it is URL-encoded instead).
        assertFalse(results[0].contains("\" onmouseover=alert(1) x=\""));
        // The literal __JS_QUERY__ must survive (URL-encoded value was not re-scanned/re-substituted).
        assertTrue(results[0].contains("__JS_QUERY__"));
        // Exact expected output: only __URL_QUERY__ is replaced, with the whole payload URL-encoded.
        assertEquals("<a href=\"/s?q=" + java.net.URLEncoder.encode(payload, java.nio.charset.StandardCharsets.UTF_8) + "\">x</a>",
                results[0]);
    }

    @Test
    public void test_getRelatedContents_multiplePlaceholders() {
        // A template may use all three placeholders at once; for a normal query (no special characters)
        // every context-encoding is an identity transform, so each is substituted with the query verbatim.
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("regex:.*",
                "<a href=\"/s?q=__URL_QUERY__\" title=\"__QUERY__\">__QUERY__</a><script>var q='__JS_QUERY__';</script>", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        String[] results = relatedContentHelper.getRelatedContents("hello");
        assertEquals(1, results.length);
        assertEquals("<a href=\"/s?q=hello\" title=\"hello\">hello</a><script>var q='hello';</script>", results[0]);
    }

    @Test
    public void test_getRelatedContents_blankPlaceHoldersReturnTemplate() {
        // When all placeholders are configured as blank, putPlaceHolder's isNotBlank guard skips
        // every entry, so replacements.isEmpty() short-circuits and the template is returned verbatim.
        // This also guards against compiling an empty-alternation regex (which would match at every
        // position) when a placeholder is blank.
        relatedContentHelper.setQueryPlaceHolder("");
        relatedContentHelper.setUrlQueryPlaceHolder("");
        relatedContentHelper.setJsQueryPlaceHolder("");

        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("regex:.*", "Related: __QUERY__", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        String[] results = relatedContentHelper.getRelatedContents("anything");
        assertEquals(1, results.length);
        // The template is returned unchanged: no substitution occurs for blank placeholders.
        assertEquals("Related: __QUERY__", results[0]);
    }

    @Test
    public void test_getRelatedContents_quoteReplacementLiteral() {
        // Matcher.quoteReplacement must neutralize regex-replacement metacharacters (\ and $) in the
        // substituted query value, so they are inserted literally rather than interpreted as escape
        // sequences or capturing-group back-references.

        // Case A (JS context, backslash): a single backslash query is escaped to two backslashes by
        // escapeEcmaScript, and both must survive as literal backslashes (not consumed as an escape).
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("regex:.*", "<script>var q='__JS_QUERY__';</script>", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        String[] results = relatedContentHelper.getRelatedContents("\\");
        assertEquals(1, results.length);
        assertEquals("<script>var q='" + org.apache.commons.text.StringEscapeUtils.escapeEcmaScript("\\") + "';</script>", results[0]);

        // Case B (HTML context, dollar sign): a "$1" query must be inserted literally and must not be
        // misinterpreted as a "$1" capturing-group back-reference during regex replacement.
        testData = new ArrayList<>();
        testData.add(createRelatedContent("regex:.*", "X__QUERY__X", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        results = relatedContentHelper.getRelatedContents("$1");
        assertEquals(1, results.length);
        assertEquals("X$1X", results[0]);
    }

    @Test
    public void test_getRelatedContents_singlePassReverse() {
        // Reverse-direction single-pass guard: a JS template substituted with a query that contains the
        // literal __URL_QUERY__ placeholder must remain single-pass. Only __JS_QUERY__ is replaced, and
        // the escaped value (which contains the literal __URL_QUERY__) is never re-scanned/re-substituted.
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("regex:.*", "<script>var q='__JS_QUERY__';</script>", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        final String payload = "__URL_QUERY__';alert(1)//";
        String[] results = relatedContentHelper.getRelatedContents(payload);
        assertEquals(1, results.length);
        // Only __JS_QUERY__ is replaced, with the whole payload JS-escaped.
        assertEquals("<script>var q='" + org.apache.commons.text.StringEscapeUtils.escapeEcmaScript(payload) + "';</script>", results[0]);
        // The literal __URL_QUERY__ embedded in the escaped value survives, proving it was not re-scanned.
        assertTrue(results[0].contains("__URL_QUERY__"));
    }

    // Asserts that a single special-character query is substituted into the template
    // with the query part HTML-escaped to the expected entity reference.
    private void assertEscapedQuery(final String payload, final String expectedEntity) {
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("regex:.*", "X__QUERY__X", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        String[] results = relatedContentHelper.getRelatedContents(payload);
        assertEquals(1, results.length);
        assertEquals("X" + expectedEntity + "X", results[0]);
    }

    @Test
    public void test_inheritance_setReloadInterval() {
        assertEquals(1000L, relatedContentHelper.reloadInterval);

        relatedContentHelper.setReloadInterval(5000L);
        assertEquals(5000L, relatedContentHelper.reloadInterval);
    }

    @Test
    public void test_inheritance_update() {
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("test", "Initial Content", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();
        String[] results = relatedContentHelper.getRelatedContents("test");
        assertEquals("Initial Content", results[0]);

        // Update test data
        testData.clear();
        testData.add(createRelatedContent("test", "Updated Content", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.update();
        // Give a moment for async execution
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // ignore
        }

        results = relatedContentHelper.getRelatedContents("test");
        assertEquals("Updated Content", results[0]);
    }

    // Helper methods

    private RelatedContent createRelatedContent(String term, String content, String virtualHost) {
        RelatedContent entity = new RelatedContent();
        entity.setTerm(term);
        entity.setContent(content);
        entity.setVirtualHost(virtualHost);
        return entity;
    }

    // Mock classes

    static class MockRelatedContentBhv extends RelatedContentBhv {
        private List<RelatedContent> testData = new ArrayList<>();

        public void setTestData(List<RelatedContent> testData) {
            this.testData = testData;
        }

        public List<RelatedContent> getTestData() {
            return testData;
        }
    }

    static class MockFessConfig extends FessConfig.SimpleImpl {
        @Override
        public Integer getPageRelatedcontentMaxFetchSizeAsInteger() {
            return 1000;
        }
    }
}