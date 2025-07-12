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

public class RelatedContentHelperTest extends UnitFessTestCase {

    private RelatedContentHelper relatedContentHelper;
    private MockRelatedContentBhv mockBhv;
    private VirtualHostHelper virtualHostHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();

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

    @Override
    public void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

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

    public void test_load_emptyList() {
        mockBhv.setTestData(new ArrayList<>());

        int count = relatedContentHelper.load();
        assertEquals(0, count);

        String[] results = relatedContentHelper.getRelatedContents("anyterm");
        assertEquals(0, results.length);
    }

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

    public void test_load_invalidRegex() {
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("regex:", "Invalid Regex", ""));
        mockBhv.setTestData(testData);

        // Should not throw exception, just log warning
        relatedContentHelper.load();

        String[] results = relatedContentHelper.getRelatedContents("anything");
        assertEquals(0, results.length);
    }

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

    public void test_load_virtualHosts() {
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("term1", "Content for host1", "host1"));
        testData.add(createRelatedContent("term1", "Content for host2", "host2"));
        testData.add(createRelatedContent("term2", "Content for default", ""));
        mockBhv.setTestData(testData);

        int count = relatedContentHelper.load();
        assertEquals(3, count); // Three virtual host keys: "host1", "host2", ""
    }

    public void test_getHostKey_emptyVirtualHost() {
        RelatedContent entity = createRelatedContent("term", "content", "");
        String key = relatedContentHelper.getHostKey(entity);
        assertEquals("", key);
    }

    public void test_getHostKey_nullVirtualHost() {
        RelatedContent entity = createRelatedContent("term", "content", null);
        String key = relatedContentHelper.getHostKey(entity);
        assertEquals("", key);
    }

    public void test_getHostKey_withVirtualHost() {
        RelatedContent entity = createRelatedContent("term", "content", "example.com");
        String key = relatedContentHelper.getHostKey(entity);
        assertEquals("example.com", key);
    }

    public void test_getRelatedContents_noMatch() {
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("java", "Java Content", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        String[] results = relatedContentHelper.getRelatedContents("python");
        assertEquals(0, results.length);
    }

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

    public void test_getRelatedContents_nullQuery() {
        List<RelatedContent> testData = new ArrayList<>();
        testData.add(createRelatedContent("test", "Test Content", ""));
        mockBhv.setTestData(testData);

        relatedContentHelper.load();

        String[] results = relatedContentHelper.getRelatedContents(null);
        assertEquals(0, results.length);
    }

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

    public void test_inheritance_setReloadInterval() {
        assertEquals(1000L, relatedContentHelper.reloadInterval);

        relatedContentHelper.setReloadInterval(5000L);
        assertEquals(5000L, relatedContentHelper.reloadInterval);
    }

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