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
package org.codelibs.fess.ds.callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.annotation.PostConstruct;

import org.codelibs.fess.entity.DataStoreParams;
import org.codelibs.fess.exception.DataStoreException;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.LanguageHelper;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.ingest.IngestFactory;
import org.codelibs.fess.ingest.Ingester;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocList;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class IndexUpdateCallbackImplTest extends UnitFessTestCase {

    private IndexUpdateCallbackImpl indexUpdateCallback;
    private TestSystemHelper systemHelper;
    private TestIndexingHelper indexingHelper;
    private TestCrawlingInfoHelper crawlingInfoHelper;
    private TestSearchLogHelper searchLogHelper;
    private TestLabelTypeHelper labelTypeHelper;
    private TestLanguageHelper languageHelper;
    private TestSearchEngineClient searchEngineClient;
    private TestIngestFactory ingestFactory;
    private FessConfig.SimpleImpl fessConfig;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);

        // Clear cached components in ComponentUtil using reflection
        try {
            java.lang.reflect.Field systemHelperField = ComponentUtil.class.getDeclaredField("systemHelper");
            systemHelperField.setAccessible(true);
            systemHelperField.set(null, null);

            java.lang.reflect.Field indexingHelperField = ComponentUtil.class.getDeclaredField("indexingHelper");
            indexingHelperField.setAccessible(true);
            indexingHelperField.set(null, null);

            java.lang.reflect.Field crawlingConfigHelperField = ComponentUtil.class.getDeclaredField("crawlingConfigHelper");
            crawlingConfigHelperField.setAccessible(true);
            crawlingConfigHelperField.set(null, null);
        } catch (Exception e) {
            // Ignore
        }

        // Initialize test helpers
        systemHelper = new TestSystemHelper();
        indexingHelper = new TestIndexingHelper();
        crawlingInfoHelper = new TestCrawlingInfoHelper();
        searchLogHelper = new TestSearchLogHelper();
        labelTypeHelper = new TestLabelTypeHelper();
        languageHelper = new TestLanguageHelper();
        searchEngineClient = new TestSearchEngineClient();
        ingestFactory = new TestIngestFactory();

        // Setup FessConfig
        fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getIndexerDataMaxDocumentRequestSize() {
                return "1048576"; // 1MB
            }

            @Override
            public Integer getIndexerDataMaxDocumentCacheSizeAsInteger() {
                return 10000;
            }

            @Override
            public String getIndexFieldUrl() {
                return "url";
            }

            @Override
            public String getIndexFieldId() {
                return "id";
            }

            @Override
            public String getIndexFieldDocId() {
                return "doc_id";
            }

            @Override
            public String getIndexFieldLabel() {
                return "label";
            }

            @Override
            public String getIndexFieldClickCount() {
                return "click_count";
            }

            @Override
            public String getIndexFieldFavoriteCount() {
                return "favorite_count";
            }

            @Override
            public boolean getIndexerClickCountEnabledAsBoolean() {
                return true;
            }

            @Override
            public boolean getIndexerFavoriteCountEnabledAsBoolean() {
                return true;
            }
        };

        // Register components
        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.register(indexingHelper, "indexingHelper");
        ComponentUtil.register(crawlingInfoHelper, "crawlingInfoHelper");
        ComponentUtil.register(searchLogHelper, "searchLogHelper");
        ComponentUtil.register(labelTypeHelper, "labelTypeHelper");
        ComponentUtil.register(languageHelper, "languageHelper");
        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        ComponentUtil.register(ingestFactory, "ingestFactory");
        ComponentUtil.register(fessConfig, "fessConfig");

        // Create test implementation of IndexUpdateCallbackImpl
        indexUpdateCallback = new TestableIndexUpdateCallbackImpl();
        indexUpdateCallback.init();
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        super.tearDown(testInfo);
    }

    @Test
    public void test_init() {
        // Test initialization
        assertEquals(1048576L, indexUpdateCallback.maxDocumentRequestSize);
        assertEquals(10000, indexUpdateCallback.maxDocumentCacheSize);
    }

    @Test
    public void test_store_withValidData() {
        // Prepare test data
        DataStoreParams paramMap = new DataStoreParams();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("url", "http://example.com/test");
        dataMap.put("title", "Test Document");

        // Execute
        indexUpdateCallback.store(paramMap, dataMap);

        // Verify
        assertEquals(1, indexUpdateCallback.getDocumentSize());
        // Check if fields were added (they should be added by the store method)
        assertNotNull(dataMap.get("id"), "ID should be set");
        assertNotNull(dataMap.get("doc_id"), "Doc ID should be set");
        assertNotNull(dataMap.get("click_count"), "Click count should be set");
        assertNotNull(dataMap.get("favorite_count"), "Favorite count should be set");
        assertEquals(1, indexUpdateCallback.docList.size());
    }

    @Test
    public void test_store_withoutUrl_throwsException() {
        // Prepare test data without URL
        DataStoreParams paramMap = new DataStoreParams();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("title", "Test Document");

        // Execute and verify exception
        try {
            indexUpdateCallback.store(paramMap, dataMap);
            fail("Should throw DataStoreException");
        } catch (DataStoreException e) {
            assertTrue(e.getMessage().contains("url is null"));
        }
    }

    @Test
    public void test_store_withLabels() {
        // Setup label matching
        labelTypeHelper.matchedLabels.add("label1");
        labelTypeHelper.matchedLabels.add("label2");

        // Prepare test data
        DataStoreParams paramMap = new DataStoreParams();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("url", "http://example.com/test");
        dataMap.put("label", new String[] { "existing" });

        // Execute
        indexUpdateCallback.store(paramMap, dataMap);

        // Verify labels are merged
        String[] labels = (String[]) dataMap.get("label");
        assertEquals(3, labels.length);
        Set<String> labelSet = new HashSet<>();
        for (String label : labels) {
            labelSet.add(label);
        }
        assertTrue(labelSet.contains("existing"));
        assertTrue(labelSet.contains("label1"));
        assertTrue(labelSet.contains("label2"));
    }

    @Test
    public void test_store_triggersIndexing_whenCacheSizeExceeded() {
        // Set small cache size
        indexUpdateCallback.maxDocumentCacheSize = 1;

        DataStoreParams paramMap = new DataStoreParams();

        // Add first document - should trigger indexing because cache size is 1
        Map<String, Object> dataMap1 = new HashMap<>();
        dataMap1.put("url", "http://example.com/test1");
        indexUpdateCallback.store(paramMap, dataMap1);
        assertEquals(1, indexingHelper.sendDocumentsCalled);

        // Add second document - should trigger indexing again
        Map<String, Object> dataMap2 = new HashMap<>();
        dataMap2.put("url", "http://example.com/test2");
        indexUpdateCallback.store(paramMap, dataMap2);
        assertEquals(2, indexingHelper.sendDocumentsCalled);
    }

    @Test
    public void test_store_triggersIndexing_whenRequestSizeExceeded() {
        // Set small request size
        indexUpdateCallback.maxDocumentRequestSize = 100;
        indexingHelper.documentSize = 101; // Return size > limit

        DataStoreParams paramMap = new DataStoreParams();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("url", "http://example.com/test");

        // Execute
        indexUpdateCallback.store(paramMap, dataMap);

        // Verify indexing was triggered
        assertEquals(1, indexingHelper.sendDocumentsCalled);
    }

    @Test
    public void test_commit() {
        // Add documents to cache
        DataStoreParams paramMap = new DataStoreParams();
        Map<String, Object> dataMap1 = new HashMap<>();
        dataMap1.put("url", "http://example.com/test1");
        indexUpdateCallback.store(paramMap, dataMap1);

        assertEquals(0, indexingHelper.sendDocumentsCalled);
        assertEquals(1, indexUpdateCallback.docList.size());

        // Execute commit
        indexUpdateCallback.commit();

        // Verify documents were sent
        assertEquals(1, indexingHelper.sendDocumentsCalled);
        assertEquals(0, indexUpdateCallback.docList.size());
    }

    @Test
    public void test_commit_withEmptyCache() {
        // Execute commit with no documents
        indexUpdateCallback.commit();

        // Verify no indexing happened
        assertEquals(0, indexingHelper.sendDocumentsCalled);
    }

    @Test
    public void test_ingest_withNoFactory() {
        // No ingest factory set
        DataStoreParams paramMap = new DataStoreParams();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("test", "value");

        // Execute
        Map<String, Object> result = indexUpdateCallback.ingest(paramMap, dataMap);

        // Verify data is returned unchanged
        assertSame(dataMap, result);
    }

    @Test
    public void test_ingest_withFactory() {
        // Setup ingest factory
        TestIngester ingester = new TestIngester();
        ingestFactory.ingesters.add(ingester);
        ComponentUtil.register(ingestFactory, "ingestFactory");

        // Reinitialize to pick up factory
        indexUpdateCallback.init();

        DataStoreParams paramMap = new DataStoreParams();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("test", "value");

        // Execute
        Map<String, Object> result = indexUpdateCallback.ingest(paramMap, dataMap);

        // Verify ingester was called
        assertEquals(1, ingester.processCalled);
        assertEquals("processed", result.get("status"));
    }

    @Test
    public void test_ingest_withException() {
        // Setup ingester that throws exception
        TestIngester failingIngester = new TestIngester() {
            @Override
            public Map<String, Object> process(Map<String, Object> target, DataStoreParams paramMap) {
                throw new RuntimeException("Test exception");
            }
        };
        ingestFactory.ingesters.add(failingIngester);
        ComponentUtil.register(ingestFactory, "ingestFactory");

        // Reinitialize to pick up factory
        indexUpdateCallback.init();

        DataStoreParams paramMap = new DataStoreParams();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("test", "value");

        // Execute - should not throw
        Map<String, Object> result = indexUpdateCallback.ingest(paramMap, dataMap);

        // Verify original data is returned
        assertSame(dataMap, result);
    }

    @Test
    public void test_addClickCountField() {
        searchLogHelper.clickCount = 42;

        Map<String, Object> doc = new HashMap<>();
        indexUpdateCallback.addClickCountField(doc, "http://example.com", "click_count");

        assertEquals(42, doc.get("click_count"));
    }

    @Test
    public void test_addFavoriteCountField() {
        searchLogHelper.favoriteCount = 99L;

        Map<String, Object> doc = new HashMap<>();
        indexUpdateCallback.addFavoriteCountField(doc, "http://example.com", "favorite_count");

        assertEquals(99L, doc.get("favorite_count"));
    }

    @Test
    public void test_getDocumentSize() {
        assertEquals(0, indexUpdateCallback.getDocumentSize());

        DataStoreParams paramMap = new DataStoreParams();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("url", "http://example.com/test");

        indexUpdateCallback.store(paramMap, dataMap);
        assertEquals(1, indexUpdateCallback.getDocumentSize());

        dataMap.put("url", "http://example.com/test2");
        indexUpdateCallback.store(paramMap, dataMap);
        assertEquals(2, indexUpdateCallback.getDocumentSize());
    }

    @Test
    public void test_getExecuteTime() {
        assertEquals(0, indexUpdateCallback.getExecuteTime());

        systemHelper.processingTime = 100L;

        DataStoreParams paramMap = new DataStoreParams();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("url", "http://example.com/test");

        indexUpdateCallback.store(paramMap, dataMap);
        assertEquals(100L, indexUpdateCallback.getExecuteTime());

        dataMap.put("url", "http://example.com/test2");
        indexUpdateCallback.store(paramMap, dataMap);
        assertEquals(200L, indexUpdateCallback.getExecuteTime());
    }

    @Test
    public void test_store_withClickCountDisabled() {
        // Create new config with click/favorite count disabled
        fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getIndexerDataMaxDocumentRequestSize() {
                return "1048576";
            }

            @Override
            public Integer getIndexerDataMaxDocumentCacheSizeAsInteger() {
                return 10000;
            }

            @Override
            public String getIndexFieldUrl() {
                return "url";
            }

            @Override
            public String getIndexFieldId() {
                return "id";
            }

            @Override
            public String getIndexFieldDocId() {
                return "doc_id";
            }

            @Override
            public boolean getIndexerClickCountEnabledAsBoolean() {
                return false;
            }

            @Override
            public boolean getIndexerFavoriteCountEnabledAsBoolean() {
                return false;
            }

            @Override
            public String getIndexFieldLabel() {
                return "label";
            }

            @Override
            public String getIndexFieldClickCount() {
                return "click_count";
            }

            @Override
            public String getIndexFieldFavoriteCount() {
                return "favorite_count";
            }
        };

        // Re-initialize to pick up new config
        indexUpdateCallback = new TestableIndexUpdateCallbackImpl();
        indexUpdateCallback.init();

        DataStoreParams paramMap = new DataStoreParams();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("url", "http://example.com/test");

        indexUpdateCallback.store(paramMap, dataMap);

        // When disabled, the fields should not be added
        assertFalse(dataMap.containsKey("click_count"));
        assertFalse(dataMap.containsKey("favorite_count"));
    }

    @Test
    public void test_store_withExistingDocId() {
        DataStoreParams paramMap = new DataStoreParams();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("url", "http://example.com/test");
        dataMap.put("doc_id", "existing-doc-id");

        indexUpdateCallback.store(paramMap, dataMap);

        // Should keep existing doc_id
        assertEquals("existing-doc-id", dataMap.get("doc_id"));
    }

    @Test
    public void test_concurrentStore() throws Exception {
        // Test thread safety
        final int threadCount = 10;
        final int docsPerThread = 10;
        final List<Thread> threads = new ArrayList<>();
        final AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                for (int j = 0; j < docsPerThread; j++) {
                    DataStoreParams paramMap = new DataStoreParams();
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("url", "http://example.com/thread" + threadId + "/doc" + j);

                    try {
                        indexUpdateCallback.store(paramMap, dataMap);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        // Log error
                    }
                }
            });
            threads.add(thread);
            thread.start();
        }

        // Wait for all threads
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify all documents were processed
        assertEquals(threadCount * docsPerThread, successCount.get());
        assertEquals(threadCount * docsPerThread, indexUpdateCallback.getDocumentSize());
    }

    // Test helper classes

    private static class TestSystemHelper extends SystemHelper {
        long processingTime = 0;
        long currentTime = System.currentTimeMillis();

        @Override
        public boolean calibrateCpuLoad() {
            // Do nothing
            return true;
        }

        @Override
        public long getCurrentTimeAsLong() {
            long time = currentTime;
            currentTime += processingTime;
            return time;
        }

        @Override
        public String generateDocId(Map<String, Object> dataMap) {
            return "doc-id-" + dataMap.get("url");
        }
    }

    private static class TestIndexingHelper extends IndexingHelper {
        int sendDocumentsCalled = 0;
        long documentSize = 1000;

        @Override
        public void sendDocuments(SearchEngineClient searchEngineClient, DocList docList) {
            sendDocumentsCalled++;
            docList.clear();
        }

        @Override
        public long calculateDocumentSize(Map<String, Object> dataMap) {
            return documentSize;
        }
    }

    private static class TestCrawlingInfoHelper extends CrawlingInfoHelper {
        @Override
        public String generateId(Map<String, Object> dataMap) {
            return "id-" + dataMap.get("url");
        }
    }

    private static class TestSearchLogHelper extends SearchLogHelper {
        int clickCount = 0;
        long favoriteCount = 0L;

        @Override
        public int getClickCount(String url) {
            return clickCount;
        }

        @Override
        public long getFavoriteCount(String url) {
            return favoriteCount;
        }
    }

    private static class TestLabelTypeHelper extends LabelTypeHelper {
        Set<String> matchedLabels = new HashSet<>();

        @Override
        public Set<String> getMatchedLabelValueSet(String url) {
            return matchedLabels;
        }
    }

    private static class TestLanguageHelper extends LanguageHelper {
        @Override
        public void updateDocument(Map<String, Object> dataMap) {
            // Do nothing
        }
    }

    private static class TestSearchEngineClient extends SearchEngineClient {
        // Empty implementation for testing
    }

    private static class TestIngestFactory extends IngestFactory {
        List<Ingester> ingesters = new ArrayList<>();

        @Override
        public Ingester[] getIngesters() {
            return ingesters.toArray(new Ingester[0]);
        }
    }

    private static class TestIngester extends Ingester {
        int processCalled = 0;

        @Override
        public Map<String, Object> process(Map<String, Object> target, DataStoreParams paramMap) {
            processCalled++;
            Map<String, Object> result = new HashMap<>(target);
            result.put("status", "processed");
            return result;
        }
    }

    private class TestableIndexUpdateCallbackImpl extends IndexUpdateCallbackImpl {
        @PostConstruct
        @Override
        public void init() {
            // Override init to use our test config directly
            maxDocumentRequestSize = 1048576L;
            maxDocumentCacheSize = 10000;
            // Set ingestFactory using reflection since it's private
            if (ComponentUtil.hasComponent("ingestFactory")) {
                try {
                    java.lang.reflect.Field field = IndexUpdateCallbackImpl.class.getDeclaredField("ingestFactory");
                    field.setAccessible(true);
                    field.set(this, ComponentUtil.getComponent("ingestFactory"));
                } catch (Exception e) {
                    // Ignore for tests
                }
            }
        }

        @Override
        public void store(final DataStoreParams paramMap, final Map<String, Object> dataMap) {
            // Simplified store for testing
            systemHelper.calibrateCpuLoad();
            final long startTime = systemHelper.getCurrentTimeAsLong();

            // Required check
            final Object urlObj = dataMap.get(fessConfig.getIndexFieldUrl());
            if (urlObj == null) {
                throw new DataStoreException("url is null. dataMap=" + dataMap);
            }

            // Add fields
            dataMap.put(fessConfig.getIndexFieldId(), crawlingInfoHelper.generateId(dataMap));

            final String url = dataMap.get(fessConfig.getIndexFieldUrl()).toString();

            if (fessConfig.getIndexerClickCountEnabledAsBoolean()) {
                addClickCountField(dataMap, url, fessConfig.getIndexFieldClickCount());
            }

            if (fessConfig.getIndexerFavoriteCountEnabledAsBoolean()) {
                addFavoriteCountField(dataMap, url, fessConfig.getIndexFieldFavoriteCount());
            }

            final Set<String> matchedLabelSet = labelTypeHelper.getMatchedLabelValueSet(url);
            if (!matchedLabelSet.isEmpty()) {
                final Set<String> newLabelSet = new HashSet<>();
                final String[] oldLabels = (String[]) dataMap.get(fessConfig.getIndexFieldLabel());
                if (oldLabels != null) {
                    for (String label : oldLabels) {
                        newLabelSet.add(label);
                    }
                }
                matchedLabelSet.stream().forEach(newLabelSet::add);
                dataMap.put(fessConfig.getIndexFieldLabel(), newLabelSet.toArray(new String[newLabelSet.size()]));
            }

            if (!dataMap.containsKey(fessConfig.getIndexFieldDocId())) {
                dataMap.put(fessConfig.getIndexFieldDocId(), systemHelper.generateDocId(dataMap));
            }

            languageHelper.updateDocument(dataMap);

            synchronized (docList) {
                docList.add(ingest(paramMap, dataMap));
                final long contentSize = indexingHelper.calculateDocumentSize(dataMap);
                docList.addContentSize(contentSize);
                final long processingTime = systemHelper.getCurrentTimeAsLong() - startTime;
                docList.addProcessingTime(processingTime);

                if (docList.getContentSize() >= maxDocumentRequestSize || docList.size() >= maxDocumentCacheSize) {
                    indexingHelper.sendDocuments(searchEngineClient, docList);
                }
                executeTime += processingTime;
            }

            documentSize.getAndIncrement();
        }
    }
}