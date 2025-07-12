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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.helper.CrawlerStatsHelper.StatsAction;
import org.codelibs.fess.helper.CrawlerStatsHelper.StatsKeyObject;
import org.codelibs.fess.helper.CrawlerStatsHelper.StatsObject;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

public class CrawlerStatsHelperTest extends UnitFessTestCase {

    private static final Logger logger = LogManager.getLogger(CrawlerStatsHelperTest.class);

    private CrawlerStatsHelper crawlerStatsHelper;

    private ThreadLocal<String> localLogMsg = new ThreadLocal<>();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        crawlerStatsHelper = new CrawlerStatsHelper() {
            @Override
            protected void log(final StringBuilder buf) {
                localLogMsg.set(buf.toString());
            }
        };
        crawlerStatsHelper.init();
    }

    @Override
    public void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    public void test_beginDone() {
        String key = "test";
        crawlerStatsHelper.begin(key);
        crawlerStatsHelper.done(key);
        logger.info(localLogMsg.get());
        String[] values = localLogMsg.get().split("\t");
        assertEquals(3, values.length);
        assertEquals("url:test", values[0]);
        assertTrue(values[1].startsWith("time:"));
        assertTrue(values[2].startsWith("done:"));

        localLogMsg.remove();
        crawlerStatsHelper.done(key);
        assertNull(localLogMsg.get());
    }

    public void test_beginDoneWithRecord1() {
        String key = "test";
        crawlerStatsHelper.begin(key);
        crawlerStatsHelper.record(key, "aaa");
        crawlerStatsHelper.done(key);
        logger.info(localLogMsg.get());
        String[] values = localLogMsg.get().split("\t");
        assertEquals(4, values.length);
        assertEquals("url:test", values[0]);
        assertTrue(values[1].startsWith("time:"));
        assertTrue(values[2].startsWith("done:"));
        assertTrue(values[3].startsWith("aaa:"));

        localLogMsg.remove();
        crawlerStatsHelper.done(key);
        assertNull(localLogMsg.get());
    }

    public void test_beginDoneWithRecord2() {
        String key = "test";
        crawlerStatsHelper.begin(key);
        crawlerStatsHelper.record(key, "aaa");
        crawlerStatsHelper.record(key, "bbb");
        crawlerStatsHelper.done(key);
        logger.info(localLogMsg.get());
        String[] values = localLogMsg.get().split("\t");
        assertEquals(5, values.length);
        assertEquals("url:test", values[0]);
        assertTrue(values[1].startsWith("time:"));
        assertTrue(values[2].startsWith("done:"));
        assertTrue(values[3].startsWith("aaa:"));
        assertTrue(values[4].startsWith("bbb:"));

        localLogMsg.remove();
        crawlerStatsHelper.done(key);
        assertNull(localLogMsg.get());
    }

    public void test_beginDoneWithRecord1WithStatsKeyObject() {
        StatsKeyObject key = new StatsKeyObject("id");
        crawlerStatsHelper.begin(key);
        key.setUrl("test");
        crawlerStatsHelper.record(key, "aaa");
        crawlerStatsHelper.done(key);
        logger.info(localLogMsg.get());
        String[] values = localLogMsg.get().split("\t");
        assertEquals(4, values.length);
        assertEquals("url:test", values[0]);
        assertTrue(values[1].startsWith("time:"));
        assertTrue(values[2].startsWith("done:"));
        assertTrue(values[3].startsWith("aaa:"));

        localLogMsg.remove();
        crawlerStatsHelper.done(key);
        assertNull(localLogMsg.get());
    }

    public void test_beginDoneWithRecordOnLazy() {
        String key = "test";
        crawlerStatsHelper.begin(key);
        crawlerStatsHelper.record(key, "aaa");
        crawlerStatsHelper.runOnThread(key);
        crawlerStatsHelper.done(key);
        logger.info(localLogMsg.get());
        assertNull(localLogMsg.get());
        crawlerStatsHelper.done(key);
        logger.info(localLogMsg.get());
        String[] values = localLogMsg.get().split("\t");
        assertEquals(4, values.length);
        assertEquals("url:test", values[0]);
        assertTrue(values[1].startsWith("time:"));
        assertTrue(values[2].startsWith("done:"));
        assertTrue(values[3].startsWith("aaa:"));

        localLogMsg.remove();
        crawlerStatsHelper.done(key);
        assertNull(localLogMsg.get());
    }

    public void test_beginWithRecordAndDiscard() {
        String key = "test";
        crawlerStatsHelper.begin(key);
        crawlerStatsHelper.record(key, "aaa");
        crawlerStatsHelper.discard(key);
        logger.info(localLogMsg.get());
        assertNull(localLogMsg.get());

        crawlerStatsHelper.done(key);
        logger.info(localLogMsg.get());
        assertNull(localLogMsg.get());

        localLogMsg.remove();
    }

    public void test_beginWithRecordOnDestroy() {
        String key = "test";
        crawlerStatsHelper.begin(key);
        crawlerStatsHelper.record(key, "aaa");
        logger.info(localLogMsg.get());
        assertNull(localLogMsg.get());

        crawlerStatsHelper.destroy();
        logger.info(localLogMsg.get());
        String[] values = localLogMsg.get().split("\t");
        assertEquals(3, values.length);
        assertEquals("url:test", values[0]);
        assertTrue(values[1].startsWith("time:"));
        assertTrue(values[2].startsWith("aaa:"));

        localLogMsg.remove();
    }

    public void test_setLoggerName() {
        String originalLoggerName = crawlerStatsHelper.loggerName;
        crawlerStatsHelper.setLoggerName("test.stats.logger");
        assertEquals("test.stats.logger", crawlerStatsHelper.loggerName);
        crawlerStatsHelper.setLoggerName(originalLoggerName);
    }

    public void test_setMaxCacheSize() {
        crawlerStatsHelper.setMaxCacheSize(500);
        assertEquals(500, crawlerStatsHelper.maxCacheSize);
    }

    public void test_setCacheExpireAfterWrite() {
        crawlerStatsHelper.setCacheExpireAfterWrite(30000);
        assertEquals(30000, crawlerStatsHelper.cacheExpireAfterWrite);
    }

    public void test_recordWithStatsAction() {
        String key = "test";
        crawlerStatsHelper.begin(key);
        crawlerStatsHelper.record(key, StatsAction.ACCESSED);
        crawlerStatsHelper.record(key, StatsAction.FINISHED);
        crawlerStatsHelper.done(key);

        String[] values = localLogMsg.get().split("\t");
        assertEquals(5, values.length);
        assertEquals("url:test", values[0]);
        assertTrue(values[1].startsWith("time:"));
        assertTrue(values[2].startsWith("done:"));
        assertTrue(values[3].startsWith("accessed:"));
        assertTrue(values[4].startsWith("finished:"));
    }

    public void test_numberKeyObject() {
        Integer key = 12345;
        crawlerStatsHelper.begin(key);
        crawlerStatsHelper.record(key, "test_action");
        crawlerStatsHelper.done(key);

        String[] values = localLogMsg.get().split("\t");
        assertEquals(4, values.length);
        assertEquals("url:12345", values[0]);
        assertTrue(values[1].startsWith("time:"));
        assertTrue(values[2].startsWith("done:"));
        assertTrue(values[3].startsWith("test_action:"));
    }

    public void test_longKeyObject() {
        Long key = 67890L;
        crawlerStatsHelper.begin(key);
        crawlerStatsHelper.record(key, "test_action");
        crawlerStatsHelper.done(key);

        String[] values = localLogMsg.get().split("\t");
        assertEquals(4, values.length);
        assertEquals("url:67890", values[0]);
        assertTrue(values[1].startsWith("time:"));
        assertTrue(values[2].startsWith("done:"));
        assertTrue(values[3].startsWith("test_action:"));
    }

    public void test_escapeValue() {
        String input = "test\twith\ttabs";
        String escaped = crawlerStatsHelper.escapeValue(input);
        assertEquals("test with tabs", escaped);
        assertFalse(escaped.contains("\t"));
    }

    public void test_statsKeyObjectWithoutUrl() {
        StatsKeyObject key = new StatsKeyObject("test_id");
        crawlerStatsHelper.begin(key);
        crawlerStatsHelper.record(key, "action");
        crawlerStatsHelper.done(key);

        String[] values = localLogMsg.get().split("\t");
        assertEquals(4, values.length);
        assertEquals("url:test_id", values[0]);
        assertTrue(values[1].startsWith("time:"));
        assertTrue(values[2].startsWith("done:"));
        assertTrue(values[3].startsWith("action:"));
    }

    public void test_recordWithTabCharacters() {
        String key = "test";
        crawlerStatsHelper.begin(key);
        crawlerStatsHelper.record(key, "action\twith\ttabs");
        crawlerStatsHelper.done(key);

        String logMessage = localLogMsg.get();
        assertTrue(logMessage.contains("action with tabs:"));
        assertFalse(logMessage.contains("action\twith\ttabs:"));
    }

    public void test_unsupportedKeyType() {
        Object key = new Object();
        crawlerStatsHelper.begin(key);
        crawlerStatsHelper.record(key, "test");
        crawlerStatsHelper.done(key);

        assertNull(localLogMsg.get());
    }

    public void test_statsObjectIncrementDecrement() {
        StatsObject statsObject = new StatsObject();
        assertEquals(1, statsObject.count.get());

        assertEquals(2, statsObject.increment());
        assertEquals(2, statsObject.count.get());

        assertEquals(1, statsObject.decrement());
        assertEquals(1, statsObject.count.get());

        assertEquals(0, statsObject.decrement());
        assertEquals(0, statsObject.count.get());
    }

    public void test_multipleRecordsOfSameAction() {
        String key = "test";
        crawlerStatsHelper.begin(key);
        crawlerStatsHelper.record(key, "same_action");
        crawlerStatsHelper.record(key, "same_action");
        crawlerStatsHelper.record(key, "different_action");
        crawlerStatsHelper.done(key);

        String logMessage = localLogMsg.get();
        String[] values = logMessage.split("\t");
        assertEquals(5, values.length);
        assertEquals("url:test", values[0]);
        assertTrue(values[1].startsWith("time:"));
        assertTrue(values[2].startsWith("done:"));

        // CrawlerStatsHelper uses LinkedHashMap which overwrites values with same key
        // Recording the same action twice will overwrite the first one
        assertTrue(values[3].startsWith("same_action:"));
        assertTrue(values[4].startsWith("different_action:"));
    }

    public void test_getUrlWithSpecialCharacters() {
        String urlWithTabs = "http://example.com/path\twith\ttabs";
        StatsKeyObject key = new StatsKeyObject("test");
        key.setUrl(urlWithTabs);

        crawlerStatsHelper.begin(key);
        crawlerStatsHelper.done(key);

        String logMessage = localLogMsg.get();
        assertTrue(logMessage.contains("url:http://example.com/path with tabs"));
        assertFalse(logMessage.contains("url:http://example.com/path\twith\ttabs"));
    }

    public void test_recordOnNonExistentKey() {
        String key = "non_existent";
        crawlerStatsHelper.record(key, "test_action");

        assertNull(localLogMsg.get());
    }

    public void test_runOnThreadOnNonExistentKey() {
        String key = "non_existent";
        crawlerStatsHelper.runOnThread(key);

        assertNull(localLogMsg.get());
    }

    public void test_doneOnNonExistentKey() {
        String key = "non_existent";
        crawlerStatsHelper.done(key);

        assertNull(localLogMsg.get());
    }

    public void test_discardOnNonExistentKey() {
        String key = "non_existent";
        crawlerStatsHelper.discard(key);

        assertNull(localLogMsg.get());
    }

    static class TestUrlQueue implements UrlQueue<String> {
        private String id;
        private String url;

        public TestUrlQueue(String id, String url) {
            this.id = id;
            this.url = url;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public String getMetaData() {
            return null;
        }

        @Override
        public String getEncoding() {
            return null;
        }

        @Override
        public String getParentUrl() {
            return null;
        }

        @Override
        public Integer getDepth() {
            return 0;
        }

        @Override
        public String getMethod() {
            return null;
        }

        @Override
        public Long getLastModified() {
            return null;
        }

        @Override
        public String getSessionId() {
            return null;
        }

        @Override
        public Long getCreateTime() {
            return null;
        }

        @Override
        public void setSessionId(String sessionId) {
        }

        @Override
        public void setWeight(float weight) {
        }

        @Override
        public float getWeight() {
            return 0.0f;
        }

        @Override
        public void setCreateTime(Long createTime) {
        }

        @Override
        public void setLastModified(Long lastModified) {
        }

        @Override
        public void setDepth(Integer depth) {
        }

        @Override
        public void setParentUrl(String parentUrl) {
        }

        @Override
        public void setUrl(String url) {
        }

        @Override
        public void setEncoding(String encoding) {
        }

        @Override
        public void setMethod(String method) {
        }

        @Override
        public void setMetaData(String metaData) {
        }

        @Override
        public void setId(String id) {
        }
    }

    public void test_urlQueueKeyObject() {
        TestUrlQueue urlQueue = new TestUrlQueue("queue_123", "http://example.com/test");
        crawlerStatsHelper.begin(urlQueue);
        crawlerStatsHelper.record(urlQueue, "crawled");
        crawlerStatsHelper.done(urlQueue);

        String[] values = localLogMsg.get().split("\t");
        assertEquals(4, values.length);
        assertEquals("url:http://example.com/test", values[0]);
        assertTrue(values[1].startsWith("time:"));
        assertTrue(values[2].startsWith("done:"));
        assertTrue(values[3].startsWith("crawled:"));
    }
}