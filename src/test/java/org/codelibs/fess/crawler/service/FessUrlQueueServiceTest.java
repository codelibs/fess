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

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.codelibs.fess.crawler.order.UrlQueueOrder;
import org.codelibs.fess.crawler.order.impl.DepthFirstUrlQueueOrder;
import org.codelibs.fess.crawler.order.impl.RandomUrlQueueOrder;
import org.codelibs.fess.crawler.order.impl.SequentialUrlQueueOrder;
import org.codelibs.fess.crawler.util.OpenSearchCrawlerConfig;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;

public class FessUrlQueueServiceTest extends UnitFessTestCase {

    /** Resolves the order for a fixed crawl.order value without touching a crawling config. */
    private static class TestFessUrlQueueService extends FessUrlQueueService {
        private String crawlOrder;

        TestFessUrlQueueService(final String crawlOrder) {
            // OpenSearchUrlQueueService's constructor reads getQueueIndex(), so a real config
            // is required; its default (".crawler.queue") is never queried by these tests.
            super(new OpenSearchCrawlerConfig());
            this.crawlOrder = crawlOrder;
        }

        void setCrawlOrder(final String crawlOrder) {
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
        // Assert identity against the container instance so the alias is actually exercised:
        // the fallback path (LEGACY_ORDER_NAMES not containing "sequential") also returns a
        // SequentialUrlQueueOrder, but a different instance, so only an identity check catches
        // the alias being removed.
        assertSame(ComponentUtil.getComponent("sequentialUrlQueueOrder"), order);
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
    public void test_invalidNameIsReportedOnceNotPerPoll() {
        final TestFessUrlQueueService service = new TestFessUrlQueueService("noSuchOrder");
        final LogCapturingAppender appender = LogCapturingAppender.attach(FessUrlQueueService.class.getName(), Level.INFO);
        try {
            // getUrlQueueOrder runs once per queue poll; the warning must not.
            for (int i = 0; i < 5; i++) {
                assertTrue(service.getUrlQueueOrder("s1") instanceof SequentialUrlQueueOrder);
            }
            final List<LogEvent> warns = appender.eventsAt(Level.WARN);
            assertEquals(1, warns.size(), appender.warnings().toString());
            assertTrue(warns.get(0).getMessage().getFormattedMessage().contains("noSuchOrder"));
            assertNull(warns.get(0).getThrown(), "the stack trace is only for DEBUG");
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_changedInvalidValueIsReportedAgain() {
        final TestFessUrlQueueService service = new TestFessUrlQueueService("noSuchOrder");
        final LogCapturingAppender appender = LogCapturingAppender.attach(FessUrlQueueService.class.getName(), Level.INFO);
        try {
            service.getUrlQueueOrder("s1");
            service.setCrawlOrder("otherOrder");
            service.getUrlQueueOrder("s1");
            service.getUrlQueueOrder("s1");
            // Each distinct bad value is reported once; going back to a value already reported is not.
            service.setCrawlOrder("noSuchOrder");
            service.getUrlQueueOrder("s1");
            final List<String> warnings = appender.warnings();
            assertEquals(2, warnings.size(), warnings.toString());
            assertTrue(warnings.get(0).contains("noSuchOrder"), warnings.get(0));
            assertTrue(warnings.get(1).contains("otherOrder"), warnings.get(1));
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_invalidNameDetailIsLoggedAtDebugWithTheWarn() {
        final TestFessUrlQueueService service = new TestFessUrlQueueService("noSuchOrder");
        final LogCapturingAppender appender = LogCapturingAppender.attach(FessUrlQueueService.class.getName(), Level.DEBUG);
        try {
            for (int i = 0; i < 3; i++) {
                service.getUrlQueueOrder("s1");
            }
            assertEquals(1, appender.eventsAt(Level.WARN).size());
            // The detail goes out only with the WARN, never once per poll.
            final List<LogEvent> details = appender.eventsAt(Level.DEBUG)
                    .stream()
                    .filter(e -> e.getMessage().getFormattedMessage().contains("Failed to resolve crawl order component"))
                    .toList();
            assertEquals(1, details.size());
            assertNotNull(details.get(0).getThrown());
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_wrongTypeFallsBackToDefault() {
        // systemProperties is a registered component (test_app.xml) that is not a UrlQueueOrder.
        final UrlQueueOrder order = new TestFessUrlQueueService("systemProperties").getUrlQueueOrder("s1");
        assertTrue(order instanceof SequentialUrlQueueOrder);
    }

    @Test
    public void test_wrongTypeIsReportedOnceNotPerPoll() {
        final TestFessUrlQueueService service = new TestFessUrlQueueService("systemProperties");
        final LogCapturingAppender appender = LogCapturingAppender.attach(FessUrlQueueService.class.getName(), Level.INFO);
        try {
            for (int i = 0; i < 3; i++) {
                assertTrue(service.getUrlQueueOrder("s1") instanceof SequentialUrlQueueOrder);
            }
            final List<String> warnings = appender.warnings();
            assertEquals(1, warnings.size(), warnings.toString());
            assertTrue(warnings.get(0).contains("Component systemProperties is not a UrlQueueOrder"), warnings.get(0));
        } finally {
            appender.detach();
        }
    }
}
