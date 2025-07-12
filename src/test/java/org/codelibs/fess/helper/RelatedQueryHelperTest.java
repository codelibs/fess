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
import org.codelibs.fess.opensearch.config.exbhv.RelatedQueryBhv;
import org.codelibs.fess.opensearch.config.exentity.RelatedQuery;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

public class RelatedQueryHelperTest extends UnitFessTestCase {

    private RelatedQueryHelper relatedQueryHelper;
    private MockRelatedQueryBhv mockBhv;
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

        relatedQueryHelper = new RelatedQueryHelper() {
            @Override
            public List<RelatedQuery> getAvailableRelatedQueryList() {
                return mockBhv.getTestData();
            }
        };

        mockBhv = new MockRelatedQueryBhv();
        virtualHostHelper = new VirtualHostHelper();

        ComponentUtil.register(mockBhv, "relatedQueryBhv");
        ComponentUtil.register(virtualHostHelper, "virtualHostHelper");
        ComponentUtil.register(new MockFessConfig(), "fessConfig");

        inject(relatedQueryHelper);
        inject(virtualHostHelper);
    }

    @Override
    public void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    public void test_init() {
        // Setup test data
        List<RelatedQuery> testData = new ArrayList<>();
        testData.add(createRelatedQuery("test", new String[] { "related1", "related2" }, ""));
        mockBhv.setTestData(testData);

        relatedQueryHelper.init();

        // Verify that load was called and data is processed
        String[] results = relatedQueryHelper.getRelatedQueries("test");
        assertEquals(2, results.length);
        assertEquals("related1", results[0]);
        assertEquals("related2", results[1]);
    }

    public void test_getAvailableRelatedQueryList() {
        List<RelatedQuery> testData = new ArrayList<>();
        testData.add(createRelatedQuery("term1", new String[] { "query1" }, ""));
        testData.add(createRelatedQuery("term2", new String[] { "query2" }, "host1"));
        mockBhv.setTestData(testData);

        List<RelatedQuery> result = relatedQueryHelper.getAvailableRelatedQueryList();
        assertEquals(2, result.size());
        assertEquals("term1", result.get(0).getTerm());
        assertEquals("term2", result.get(1).getTerm());
    }

    public void test_load_emptyList() {
        mockBhv.setTestData(new ArrayList<>());

        int count = relatedQueryHelper.load();
        assertEquals(0, count);

        String[] results = relatedQueryHelper.getRelatedQueries("anyterm");
        assertEquals(0, results.length);
    }

    public void test_load_singleTerm() {
        List<RelatedQuery> testData = new ArrayList<>();
        testData.add(createRelatedQuery("java", new String[] { "programming", "tutorial", "language" }, ""));
        mockBhv.setTestData(testData);

        int count = relatedQueryHelper.load();
        assertEquals(1, count); // One virtual host key (empty)

        String[] results = relatedQueryHelper.getRelatedQueries("java");
        assertEquals(3, results.length);
        assertEquals("programming", results[0]);
        assertEquals("tutorial", results[1]);
        assertEquals("language", results[2]);
    }

    public void test_load_multipleTerms() {
        List<RelatedQuery> testData = new ArrayList<>();
        testData.add(createRelatedQuery("java", new String[] { "programming", "tutorial" }, ""));
        testData.add(createRelatedQuery("python", new String[] { "scripting", "data science" }, ""));
        mockBhv.setTestData(testData);

        int count = relatedQueryHelper.load();
        assertEquals(1, count); // One virtual host key (empty)

        String[] results = relatedQueryHelper.getRelatedQueries("java");
        assertEquals(2, results.length);
        assertEquals("programming", results[0]);
        assertEquals("tutorial", results[1]);

        results = relatedQueryHelper.getRelatedQueries("python");
        assertEquals(2, results.length);
        assertEquals("scripting", results[0]);
        assertEquals("data science", results[1]);
    }

    public void test_load_caseInsensitiveTerms() {
        List<RelatedQuery> testData = new ArrayList<>();
        testData.add(createRelatedQuery("Java", new String[] { "Programming", "Tutorial" }, ""));
        mockBhv.setTestData(testData);

        relatedQueryHelper.load();

        // Test case insensitive matching
        String[] results = relatedQueryHelper.getRelatedQueries("java");
        assertEquals(2, results.length);
        assertEquals("Programming", results[0]);
        assertEquals("Tutorial", results[1]);

        results = relatedQueryHelper.getRelatedQueries("JAVA");
        assertEquals(2, results.length);
        assertEquals("Programming", results[0]);
        assertEquals("Tutorial", results[1]);

        results = relatedQueryHelper.getRelatedQueries("Java");
        assertEquals(2, results.length);
        assertEquals("Programming", results[0]);
        assertEquals("Tutorial", results[1]);
    }

    public void test_load_virtualHosts() {
        List<RelatedQuery> testData = new ArrayList<>();
        testData.add(createRelatedQuery("term1", new String[] { "query1" }, "host1"));
        testData.add(createRelatedQuery("term1", new String[] { "query2" }, "host2"));
        testData.add(createRelatedQuery("term2", new String[] { "query3" }, ""));
        mockBhv.setTestData(testData);

        int count = relatedQueryHelper.load();
        assertEquals(3, count); // Three virtual host keys: "host1", "host2", ""
    }

    public void test_load_sameTermDifferentHosts() {
        List<RelatedQuery> testData = new ArrayList<>();
        testData.add(createRelatedQuery("search", new String[] { "find", "lookup" }, "site1.com"));
        testData.add(createRelatedQuery("search", new String[] { "query", "discover" }, "site2.com"));
        mockBhv.setTestData(testData);

        relatedQueryHelper.load();

        // Mock virtual host helper to return site1.com
        virtualHostHelper = new VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return "site1.com";
            }
        };
        ComponentUtil.register(virtualHostHelper, "virtualHostHelper");

        String[] results = relatedQueryHelper.getRelatedQueries("search");
        assertEquals(2, results.length);
        assertEquals("find", results[0]);
        assertEquals("lookup", results[1]);
    }

    public void test_load_overwriteSameTerm() {
        List<RelatedQuery> testData = new ArrayList<>();
        testData.add(createRelatedQuery("test", new String[] { "first", "set" }, ""));
        testData.add(createRelatedQuery("test", new String[] { "second", "set" }, ""));
        mockBhv.setTestData(testData);

        relatedQueryHelper.load();

        // The second entry should overwrite the first one
        String[] results = relatedQueryHelper.getRelatedQueries("test");
        assertEquals(2, results.length);
        assertEquals("second", results[0]);
        assertEquals("set", results[1]);
    }

    public void test_getHostKey_emptyVirtualHost() {
        RelatedQuery entity = createRelatedQuery("term", new String[] { "query" }, "");
        String key = relatedQueryHelper.getHostKey(entity);
        assertEquals("", key);
    }

    public void test_getHostKey_nullVirtualHost() {
        RelatedQuery entity = createRelatedQuery("term", new String[] { "query" }, null);
        String key = relatedQueryHelper.getHostKey(entity);
        assertEquals("", key);
    }

    public void test_getHostKey_withVirtualHost() {
        RelatedQuery entity = createRelatedQuery("term", new String[] { "query" }, "example.com");
        String key = relatedQueryHelper.getHostKey(entity);
        assertEquals("example.com", key);
    }

    public void test_getRelatedQueries_noMatch() {
        List<RelatedQuery> testData = new ArrayList<>();
        testData.add(createRelatedQuery("java", new String[] { "programming" }, ""));
        mockBhv.setTestData(testData);

        relatedQueryHelper.load();

        String[] results = relatedQueryHelper.getRelatedQueries("python");
        assertEquals(0, results.length);
    }

    public void test_getRelatedQueries_nullQuery() {
        List<RelatedQuery> testData = new ArrayList<>();
        testData.add(createRelatedQuery("test", new String[] { "related" }, ""));
        mockBhv.setTestData(testData);

        relatedQueryHelper.load();

        String[] results = relatedQueryHelper.getRelatedQueries(null);
        assertEquals(0, results.length);
    }

    public void test_getRelatedQueries_emptyQuery() {
        List<RelatedQuery> testData = new ArrayList<>();
        testData.add(createRelatedQuery("test", new String[] { "related" }, ""));
        mockBhv.setTestData(testData);

        relatedQueryHelper.load();

        String[] results = relatedQueryHelper.getRelatedQueries("");
        assertEquals(0, results.length);
    }

    public void test_getRelatedQueries_differentVirtualHost() {
        List<RelatedQuery> testData = new ArrayList<>();
        testData.add(createRelatedQuery("test", new String[] { "related" }, "host1"));
        mockBhv.setTestData(testData);

        relatedQueryHelper.load();

        // Mock virtual host helper to return empty key (different from host1)
        virtualHostHelper = new VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return "";
            }
        };
        ComponentUtil.register(virtualHostHelper, "virtualHostHelper");

        String[] results = relatedQueryHelper.getRelatedQueries("test");
        assertEquals(0, results.length);
    }

    public void test_getRelatedQueries_singleQuery() {
        List<RelatedQuery> testData = new ArrayList<>();
        testData.add(createRelatedQuery("search", new String[] { "find" }, ""));
        mockBhv.setTestData(testData);

        relatedQueryHelper.load();

        String[] results = relatedQueryHelper.getRelatedQueries("search");
        assertEquals(1, results.length);
        assertEquals("find", results[0]);
    }

    public void test_getRelatedQueries_emptyQueriesArray() {
        List<RelatedQuery> testData = new ArrayList<>();
        testData.add(createRelatedQuery("test", new String[] {}, ""));
        mockBhv.setTestData(testData);

        relatedQueryHelper.load();

        String[] results = relatedQueryHelper.getRelatedQueries("test");
        assertEquals(0, results.length);
    }

    public void test_getRelatedQueries_nullQueriesArray() {
        List<RelatedQuery> testData = new ArrayList<>();
        testData.add(createRelatedQuery("test", null, ""));
        mockBhv.setTestData(testData);

        relatedQueryHelper.load();

        String[] results = relatedQueryHelper.getRelatedQueries("test");
        assertEquals(0, results.length);
    }

    public void test_toLowerCase_nullInput() {
        // Test private method indirectly through public methods
        List<RelatedQuery> testData = new ArrayList<>();
        testData.add(createRelatedQuery(null, new String[] { "query" }, ""));
        mockBhv.setTestData(testData);

        // Should not throw exception
        relatedQueryHelper.load();

        String[] results = relatedQueryHelper.getRelatedQueries("anyterm");
        assertEquals(0, results.length);
    }

    public void test_inheritance_setReloadInterval() {
        assertEquals(1000L, relatedQueryHelper.reloadInterval);

        relatedQueryHelper.setReloadInterval(5000L);
        assertEquals(5000L, relatedQueryHelper.reloadInterval);
    }

    public void test_inheritance_update() {
        List<RelatedQuery> testData = new ArrayList<>();
        testData.add(createRelatedQuery("test", new String[] { "initial" }, ""));
        mockBhv.setTestData(testData);

        relatedQueryHelper.load();
        String[] results = relatedQueryHelper.getRelatedQueries("test");
        assertEquals("initial", results[0]);

        // Update test data
        testData.clear();
        testData.add(createRelatedQuery("test", new String[] { "updated" }, ""));
        mockBhv.setTestData(testData);

        relatedQueryHelper.update();
        // Give a moment for async execution
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // ignore
        }

        results = relatedQueryHelper.getRelatedQueries("test");
        assertEquals("updated", results[0]);
    }

    public void test_relatedQueryMap_volatile() {
        // Test that the map is properly replaced when load() is called
        List<RelatedQuery> testData1 = new ArrayList<>();
        testData1.add(createRelatedQuery("term1", new String[] { "query1" }, ""));
        mockBhv.setTestData(testData1);

        relatedQueryHelper.load();
        String[] results = relatedQueryHelper.getRelatedQueries("term1");
        assertEquals(1, results.length);
        assertEquals("query1", results[0]);

        // Replace with different data
        List<RelatedQuery> testData2 = new ArrayList<>();
        testData2.add(createRelatedQuery("term2", new String[] { "query2" }, ""));
        mockBhv.setTestData(testData2);

        relatedQueryHelper.load();

        // Old term should not be found
        results = relatedQueryHelper.getRelatedQueries("term1");
        assertEquals(0, results.length);

        // New term should be found
        results = relatedQueryHelper.getRelatedQueries("term2");
        assertEquals(1, results.length);
        assertEquals("query2", results[0]);
    }

    public void test_multipleVirtualHostsWithSameTerm() {
        List<RelatedQuery> testData = new ArrayList<>();
        testData.add(createRelatedQuery("common", new String[] { "host1_query1", "host1_query2" }, "host1"));
        testData.add(createRelatedQuery("common", new String[] { "host2_query1", "host2_query2" }, "host2"));
        testData.add(createRelatedQuery("common", new String[] { "default_query1", "default_query2" }, ""));
        mockBhv.setTestData(testData);

        relatedQueryHelper.load();

        // Test default virtual host (empty string)
        virtualHostHelper = new VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return "";
            }
        };
        ComponentUtil.register(virtualHostHelper, "virtualHostHelper");

        String[] results = relatedQueryHelper.getRelatedQueries("common");
        assertEquals(2, results.length);
        assertEquals("default_query1", results[0]);
        assertEquals("default_query2", results[1]);
    }

    // Helper methods

    private RelatedQuery createRelatedQuery(String term, String[] queries, String virtualHost) {
        RelatedQuery entity = new RelatedQuery();
        entity.setTerm(term);
        entity.setQueries(queries);
        entity.setVirtualHost(virtualHost);
        return entity;
    }

    // Mock classes

    static class MockRelatedQueryBhv extends RelatedQueryBhv {
        private List<RelatedQuery> testData = new ArrayList<>();

        public void setTestData(List<RelatedQuery> testData) {
            this.testData = testData;
        }

        public List<RelatedQuery> getTestData() {
            return testData;
        }
    }

    static class MockFessConfig extends FessConfig.SimpleImpl {
        @Override
        public Integer getPageRelatedqueryMaxFetchSizeAsInteger() {
            return 1000;
        }
    }
}