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
package org.codelibs.fess.crawler.service;

import org.codelibs.fess.crawler.order.UrlQueueOrder;
import org.codelibs.fess.crawler.order.impl.DepthFirstUrlQueueOrder;
import org.codelibs.fess.crawler.order.impl.RandomUrlQueueOrder;
import org.codelibs.fess.crawler.order.impl.SequentialUrlQueueOrder;
import org.codelibs.fess.crawler.util.OpenSearchCrawlerConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class FessUrlQueueServiceTest extends UnitFessTestCase {

    /** Resolves the order for a fixed crawl.order value without touching a crawling config. */
    private static class TestFessUrlQueueService extends FessUrlQueueService {
        private final String crawlOrder;

        TestFessUrlQueueService(final String crawlOrder) {
            // OpenSearchUrlQueueService's constructor reads getQueueIndex(), so a real config
            // is required; its default (".crawler.queue") is never queried by these tests.
            super(new OpenSearchCrawlerConfig());
            this.crawlOrder = crawlOrder;
        }

        @Override
        protected String getConfiguredCrawlOrder(final String sessionId) {
            return crawlOrder;
        }
    }

    @Test
    public void test_resolvesComponentName() {
        final UrlQueueOrder order = new TestFessUrlQueueService("depthFirstUrlQueueOrder").getUrlQueueOrder("s1");
        assertTrue(order instanceof DepthFirstUrlQueueOrder);
    }

    @Test
    public void test_resolvesLegacySequential() {
        final UrlQueueOrder order = new TestFessUrlQueueService("sequential").getUrlQueueOrder("s1");
        assertTrue(order instanceof SequentialUrlQueueOrder);
    }

    @Test
    public void test_resolvesLegacyRandom() {
        final UrlQueueOrder order = new TestFessUrlQueueService("random").getUrlQueueOrder("s1");
        assertTrue(order instanceof RandomUrlQueueOrder);
    }

    @Test
    public void test_blankFallsBackToDefault() {
        final UrlQueueOrder order = new TestFessUrlQueueService("").getUrlQueueOrder("s1");
        assertTrue(order instanceof SequentialUrlQueueOrder);
        assertEquals(2, order.buildSorts("s1").length);
    }

    @Test
    public void test_unknownNameFallsBackToDefault() {
        final UrlQueueOrder order = new TestFessUrlQueueService("noSuchOrder").getUrlQueueOrder("s1");
        assertTrue(order instanceof SequentialUrlQueueOrder);
    }

    @Test
    public void test_wrongTypeFallsBackToDefault() {
        // systemProperties is a registered component (test_app.xml) that is not a UrlQueueOrder.
        final UrlQueueOrder order = new TestFessUrlQueueService("systemProperties").getUrlQueueOrder("s1");
        assertTrue(order instanceof SequentialUrlQueueOrder);
    }
}
