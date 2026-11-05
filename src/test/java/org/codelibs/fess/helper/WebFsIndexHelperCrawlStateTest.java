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

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.CrawlStateService;
import org.codelibs.fess.crawler.Crawler;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class WebFsIndexHelperCrawlStateTest extends UnitFessTestCase {

    private final List<String> calls = new ArrayList<>();

    private boolean stopped;

    private long progressInterval;

    /** False for a service that has only queued the write, as the real one does. */
    private boolean runSuppliers;

    private WebFsIndexHelper helper;

    private String sid1;

    private String sid2;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        calls.clear();
        stopped = false;
        progressInterval = 10000L;
        runSuppliers = true;
        final CrawlStateService crawlStateService = new CrawlStateService() {
            @Override
            public void saveProgressAsync(final String configId, final LongSupplier count, final long ttl) {
                if (!runSuppliers) {
                    calls.add("queued:" + configId);
                    return;
                }
                calls.add("progress:" + configId + ":" + count.getAsLong() + ":" + ttl);
            }

            @Override
            public void saveCompleted(final String configId, final long count) {
                calls.add("completed:" + configId + ":" + count);
            }

            @Override
            public void endProgress(final String configId) {
                calls.add("end:" + configId);
            }
        };
        // the container builds the real service, so the helper is given the stub through its accessors
        helper = new WebFsIndexHelper() {
            @Override
            protected CrawlStateService getCrawlStateService() {
                return crawlStateService;
            }

            @Override
            protected CrawlingConfig getCrawlingConfig(final String sid) {
                if (sid1.equals(sid) || sid2.equals(sid)) {
                    final WebConfig config = new WebConfig();
                    config.setId(sid1.equals(sid) ? "1" : "2");
                    return config;
                }
                return null;
            }

            @Override
            protected long getProgressInterval() {
                return progressInterval;
            }
        };
        sid1 = "test-0";
        sid2 = "test-1";
        ComponentUtil.register(new SystemHelper() {
            @Override
            public boolean isForceStop() {
                return stopped;
            }
        }, "systemHelper");
    }

    private Crawler crawler(final String sessionId) {
        final Crawler crawler = new Crawler();
        crawler.getCrawlerContext().setSessionId(sessionId);
        return crawler;
    }

    private Crawler crawler(final String sessionId, final int accessCount) {
        final Crawler crawler = crawler(sessionId);
        for (int i = 0; i < accessCount; i++) {
            crawler.getCrawlerContext().incrementAndGetAccessCount();
        }
        return crawler;
    }

    @Test
    public void test_saveCrawlResult_savesTheCountOfTheSession() {
        helper.saveCrawlResult(sid1, 7);
        assertEquals(1, calls.size());
        assertEquals("completed:W1:7", calls.get(0));
    }

    @Test
    public void test_saveCrawlResult_stoppedCrawlKeepsThePreviousCount() {
        stopped = true;
        helper.saveCrawlResult(sid1, 7);
        assertEquals(1, calls.size());
        assertEquals("end:W1", calls.get(0));
    }

    @Test
    public void test_saveCrawlResult_unknownSessionIsIgnored() {
        helper.saveCrawlResult("unknown", 7);
        assertEquals(0, calls.size());
    }

    @Test
    public void test_saveProgress_runningCrawlsOnly() {
        helper.crawlerList.add(crawler(sid1, 7));
        helper.crawlerList.add(crawler(sid2, 11));
        helper.crawlerList.add(crawler("unknown", 3));
        final List<String> statuses = List.of(Constants.RUNNING, Constants.DONE, Constants.RUNNING);

        helper.saveProgress(statuses);

        // the finished crawl and the crawl without a stored configuration are not written
        assertEquals(1, calls.size());
        assertEquals("progress:W1:7:" + CrawlStateService.getProgressTtl(progressInterval), calls.get(0));
    }

    @Test
    public void test_saveProgress_readsTheLiveAccessCount() {
        final Crawler crawler = crawler(sid1, 7);
        helper.crawlerList.add(crawler);
        // the service reads the count when it writes, so the value is the one at that time
        runSuppliers = false;
        helper.saveProgress(List.of(Constants.RUNNING));
        assertEquals(List.of("queued:W1"), calls);

        runSuppliers = true;
        helper.setCrawlingExecutionInterval(0L);
        progressInterval = 0L;
        crawler.getCrawlerContext().incrementAndGetAccessCount();
        helper.saveProgress(List.of(Constants.RUNNING));
        assertEquals("progress:W1:8:" + CrawlStateService.getProgressTtl(0L), calls.get(1));
    }

    @Test
    public void test_saveProgress_ttlFollowsTheSlowerOfTheWriteAndTheLoop() {
        helper.crawlerList.add(crawler(sid1, 7));
        // the loop that renews the progress runs once per crawling execution interval at best
        helper.setCrawlingExecutionInterval(60000L);
        progressInterval = 10000L;

        helper.saveProgress(List.of(Constants.RUNNING));

        assertEquals("progress:W1:7:" + CrawlStateService.getProgressTtl(60000L), calls.get(0));
    }

    @Test
    public void test_saveProgress_writesOncePerProgressInterval() {
        helper.crawlerList.add(crawler(sid1, 7));
        final List<String> statuses = List.of(Constants.RUNNING);

        progressInterval = 60000L;
        helper.saveProgress(statuses);
        helper.saveProgress(statuses);
        helper.saveProgress(statuses);
        assertEquals(1, calls.size());

        // the crawling execution interval does not decide how often it is written
        helper.setCrawlingExecutionInterval(0L);
        helper.saveProgress(statuses);
        assertEquals(1, calls.size());

        progressInterval = 0L;
        helper.saveProgress(statuses);
        helper.saveProgress(statuses);
        assertEquals(3, calls.size());
    }

    @Test
    public void test_saveProgress_doesNotWriteOnTheLoopThread() {
        helper.crawlerList.add(crawler(sid1, 7));
        runSuppliers = false;

        helper.saveProgress(List.of(Constants.RUNNING));

        assertEquals(List.of("queued:W1"), calls);
    }
}
