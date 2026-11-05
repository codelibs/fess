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

import org.codelibs.fess.app.service.FailureUrlService;
import org.codelibs.fess.crawler.CrawlerContext;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.entity.UrlQueueImpl;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.log.LogType;
import org.codelibs.fess.entity.FailureUrlDetails;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig;
import org.codelibs.fess.opensearch.config.exentity.FailureUrl;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class CrawlerLogHelperTest extends UnitFessTestCase {

    private CrawlerLogHelper crawlerLogHelper;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        crawlerLogHelper = new CrawlerLogHelper();
    }

    private static final String PAGE_URL = "docs.example.com-a.pdf";

    private final List<FailureUrlDetails> storedDetails = new ArrayList<>();

    private CrawlerLogHelper createHelper() {
        storedDetails.clear();
        ComponentUtil.register(new FailureUrlService() {
            @Override
            public FailureUrl store(final CrawlingConfig config, final String errorName, final String url, final Throwable e,
                    final FailureUrlDetails details) {
                storedDetails.add(details);
                return null;
            }
        }, FailureUrlService.class.getCanonicalName());
        return new CrawlerLogHelper() {
            @Override
            protected CrawlingConfig getCrawlingConfig(final String sessionCountId) {
                return null;
            }

            @Override
            protected void processStartCrawling(final Object... objs) {
                // nothing
            }

            @Override
            protected void processCleanupCrawling(final Object... objs) {
                // nothing
            }
        };
    }

    private static UrlQueue<?> urlQueue(final String url) {
        final UrlQueueImpl<Long> urlQueue = new UrlQueueImpl<>();
        urlQueue.setUrl(url);
        return urlQueue;
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        CrawlerLogHelper.clearFailureDetails();
        super.tearDown(testInfo);
    }

    @Test
    public void test_storeFailureUrl_usesTheRememberedDetailsOnce() {
        final CrawlerLogHelper helper = createHelper();
        final FailureUrlDetails details = new FailureUrlDetails(2048L, "application/pdf", 200);
        CrawlerLogHelper.rememberFailureDetails(PAGE_URL, details);
        helper.storeFailureUrl(new CrawlerContext(), urlQueue(PAGE_URL), "E", new RuntimeException());
        helper.storeFailureUrl(new CrawlerContext(), urlQueue(PAGE_URL), "E", new RuntimeException());
        assertEquals(2, storedDetails.size());
        assertSame(details, storedDetails.get(0));
        // the second failure of the thread must not inherit them
        assertNull(storedDetails.get(1));
    }

    @Test
    public void test_storeFailureUrl_ignoresDetailsOfAnotherUrl() {
        final CrawlerLogHelper helper = createHelper();
        CrawlerLogHelper.rememberFailureDetails("other-url", new FailureUrlDetails(2048L, "application/pdf", 200));
        helper.storeFailureUrl(new CrawlerContext(), urlQueue(PAGE_URL), "E", new RuntimeException());
        assertNull(storedDetails.get(0));
        assertNull(CrawlerLogHelper.takeFailureDetails("other-url"));
    }

    @Test
    public void test_storeFailureUrl_readsTheSizeOfAClientRejection() {
        final CrawlerLogHelper helper = createHelper();
        final Throwable e = new MaxLengthExceededException("The content length (2048 byte) is over 1024 byte. The url is x");
        helper.storeFailureUrl(new CrawlerContext(), urlQueue(PAGE_URL), "E", e);
        assertEquals(Long.valueOf(2048L), storedDetails.get(0).contentLength());
        assertNull(storedDetails.get(0).mimeType());
        assertNull(storedDetails.get(0).httpStatusCode());
        // another failure does not guess a size from its message
        helper.storeFailureUrl(new CrawlerContext(), urlQueue(PAGE_URL), "E", new RuntimeException("timed out after (5 byte)"));
        assertNull(storedDetails.get(1));
    }

    @Test
    public void test_log_startAndCleanupForgetTheDetails() {
        final CrawlerLogHelper helper = createHelper();
        CrawlerLogHelper.rememberFailureDetails(PAGE_URL, new FailureUrlDetails(1L, null, null));
        helper.log(LogType.START_CRAWLING, new CrawlerContext(), urlQueue(PAGE_URL));
        assertNull(CrawlerLogHelper.takeFailureDetails(PAGE_URL));

        CrawlerLogHelper.rememberFailureDetails(PAGE_URL, new FailureUrlDetails(1L, null, null));
        helper.log(LogType.CLEANUP_CRAWLING, new CrawlerContext(), urlQueue(PAGE_URL));
        assertNull(CrawlerLogHelper.takeFailureDetails(PAGE_URL));
    }

    @Test
    public void test_instantiation() {
        assertNotNull(crawlerLogHelper);
    }

    @Test
    public void test_log_method_exists() {
        // Test that the log method exists and can be called
        // We can't easily test the actual functionality without complex mocking
        // but we can verify the class structure
        assertNotNull(crawlerLogHelper);
        assertTrue(CrawlerLogHelper.class.getSuperclass().getSimpleName().contains("LogHelper"));
    }
}