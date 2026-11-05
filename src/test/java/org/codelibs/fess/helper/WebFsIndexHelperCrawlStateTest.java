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

    private boolean countFails;

    /** False for a service that has only queued the write, as the real one does. */
    private boolean runSuppliers;

    private int countCalls;

    private WebFsIndexHelper helper;

    private String sid1;

    private String sid2;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        calls.clear();
        stopped = false;
        countFails = false;
        runSuppliers = true;
        countCalls = 0;
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
            protected int getAccessResultCount(final String sid) {
                countCalls++;
                if (countFails) {
                    throw new IllegalStateException("search engine is down");
                }
                return sid1.equals(sid) ? 7 : 11;
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

    @Test
    public void test_saveCrawlResult_savesTheCountOfTheSession() {
        helper.saveCrawlResult(sid1);
        assertEquals(1, calls.size());
        assertEquals("completed:W1:7", calls.get(0));
    }

    @Test
    public void test_saveCrawlResult_stoppedCrawlKeepsThePreviousCount() {
        stopped = true;
        helper.saveCrawlResult(sid1);
        assertEquals(1, calls.size());
        assertEquals("end:W1", calls.get(0));
    }

    @Test
    public void test_saveCrawlResult_unknownSessionAndFailureAreIgnored() {
        helper.saveCrawlResult("unknown");
        assertEquals(0, calls.size());

        countFails = true;
        helper.saveCrawlResult(sid1);
        assertEquals(0, calls.size());
    }

    @Test
    public void test_saveProgress_runningCrawlsOnly() {
        helper.crawlerList.add(crawler(sid1));
        helper.crawlerList.add(crawler(sid2));
        helper.crawlerList.add(crawler("unknown"));
        final List<String> statuses = List.of(Constants.RUNNING, Constants.DONE, Constants.RUNNING);

        helper.saveProgress(statuses);

        // the finished crawl and the crawl without a stored configuration are not written
        assertEquals(1, calls.size());
        assertEquals("progress:W1:7:" + CrawlStateService.getProgressTtl(Constants.DEFAULT_CRAWLING_EXECUTION_INTERVAL), calls.get(0));
    }

    @Test
    public void test_saveProgress_writesOncePerInterval() {
        helper.crawlerList.add(crawler(sid1));
        final List<String> statuses = List.of(Constants.RUNNING);

        helper.setCrawlingExecutionInterval(60000L);
        helper.saveProgress(statuses);
        helper.saveProgress(statuses);
        helper.saveProgress(statuses);
        assertEquals(1, calls.size());

        // the loops that renew the progress run once per crawler, so a short interval writes each time
        helper.setCrawlingExecutionInterval(0L);
        helper.saveProgress(statuses);
        helper.saveProgress(statuses);
        assertEquals(3, calls.size());
    }

    @Test
    public void test_saveProgress_doesNotCountOrWriteOnTheLoopThread() {
        helper.crawlerList.add(crawler(sid1));
        runSuppliers = false;

        helper.saveProgress(List.of(Constants.RUNNING));

        // the count is a request to the search engine too: it is left to the writer thread
        assertEquals(List.of("queued:W1"), calls);
        assertEquals(0, countCalls);
    }

    @Test
    public void test_saveProgress_failureDoesNotReachTheCrawl() {
        helper.crawlerList.add(crawler(sid1));
        countFails = true;
        helper.saveProgress(List.of(Constants.RUNNING));
        assertEquals(0, calls.size());
    }
}
