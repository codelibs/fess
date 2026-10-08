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
package org.codelibs.fess.api.v2.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.fess.api.v2.handlers.AbstractTagHandler.TagRequestException;
import org.codelibs.fess.api.v2.handlers.TagHandlerTestSupport.Env;
import org.codelibs.fess.api.v2.handlers.TagHandlerTestSupport.FakeTagTypeService;
import org.codelibs.fess.app.service.TagTypeService;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Unit tests for {@link AbstractTagHandler#updateTagType}: the updates of one tag run one at a time, and a write that
 * lost a race to another writer is retried after a wait.
 */
public class AbstractTagHandlerTest extends UnitFessTestCase {

    private static final int THREADS = 8;

    private Env env;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        env = new Env();
    }

    /** A store that can be used from several threads, as the search engine can. */
    private static class SharedTagTypeService extends FakeTagTypeService {
        @Override
        public synchronized OptionalEntity<TagType> getTagType(final String id) {
            return super.getTagType(id);
        }

        @Override
        public synchronized void update(final TagType tagType) {
            super.update(tagType);
        }
    }

    private static class TestHandler extends AbstractTagHandler {
        final TagTypeService service;
        final List<Long> pauses = Collections.synchronizedList(new ArrayList<>());
        Runnable onPause;

        TestHandler(final TagTypeService service) {
            this.service = service;
        }

        @Override
        protected TagTypeService getTagTypeService() {
            return service;
        }

        @Override
        protected void pause(final long millis) {
            pauses.add(millis);
            if (onPause != null) {
                onPause.run();
            }
        }

        TagType update(final String id, final String url) throws TagRequestException {
            return updateTagType(id, "alice", t -> {
                final String[] paths = Arrays.copyOf(t.getPaths(), t.getPaths().length + 1);
                paths[paths.length - 1] = url;
                t.setPaths(paths);
                return true;
            });
        }
    }

    private SharedTagTypeService newService() {
        final SharedTagTypeService service = new SharedTagTypeService();
        final TagType tag = env.put("foo", "alice", false, "http://a/");
        service.store.put(tag.getId(), TagHandlerTestSupport.copy(tag, true));
        service.seqNoMap.put(tag.getId(), 0L);
        return service;
    }

    private String tagId(final SharedTagTypeService service) {
        return service.store.keySet().iterator().next();
    }

    @Test
    public void test_updateTagType_updatesOfOneTagRunOneAtATime() throws Exception {
        final SharedTagTypeService service = newService();
        final String id = tagId(service);
        final TestHandler handler = new TestHandler(service);

        final AtomicInteger running = new AtomicInteger();
        final AtomicInteger maxRunning = new AtomicInteger();
        final CountDownLatch start = new CountDownLatch(1);
        final List<Throwable> failures = Collections.synchronizedList(new ArrayList<>());
        final List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            final String url = "http://example.com/" + i;
            final Thread thread = new Thread(() -> {
                try {
                    start.await();
                    handler.updateTagType(id, "alice", t -> {
                        maxRunning.accumulateAndGet(running.incrementAndGet(), Math::max);
                        try {
                            // long enough for the others, started together, to be in the same read-modify-write
                            ThreadUtil.sleep(30);
                            final String[] paths = Arrays.copyOf(t.getPaths(), t.getPaths().length + 1);
                            paths[paths.length - 1] = url;
                            t.setPaths(paths);
                            return true;
                        } finally {
                            running.decrementAndGet();
                        }
                    });
                } catch (final Throwable e) {
                    failures.add(e);
                }
            });
            threads.add(thread);
            thread.start();
        }
        start.countDown();
        for (final Thread thread : threads) {
            thread.join(30000);
            Assertions.assertFalse(thread.isAlive());
        }

        Assertions.assertEquals(List.of(), failures);
        Assertions.assertEquals(1, maxRunning.get());
        // nobody lost a race, so nobody retried, and every URL is on the tag
        Assertions.assertEquals(THREADS, service.calls.size());
        Assertions.assertEquals(List.of(), handler.pauses);
        Assertions.assertEquals(THREADS + 1, service.store.get(id).getPaths().length);
    }

    @Test
    public void test_updateTagType_waitsBeforeARetryWithoutHoldingTheLock() throws Exception {
        final SharedTagTypeService service = newService();
        final String id = tagId(service);
        final TestHandler handler = new TestHandler(service);
        service.updateConflicts = 1;
        final List<Throwable> failures = Collections.synchronizedList(new ArrayList<>());
        // while the first update waits to retry, the update of another request on the same tag goes through
        handler.onPause = () -> {
            final Thread other = new Thread(() -> {
                try {
                    handler.update(id, "http://other/");
                } catch (final Throwable e) {
                    failures.add(e);
                }
            });
            other.start();
            try {
                other.join(10000);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Assertions.assertFalse(other.isAlive(), "the update of the other request is blocked while this one waits");
        };

        handler.update(id, "http://mine/");

        Assertions.assertEquals(List.of(), failures);
        Assertions.assertEquals(1, handler.pauses.size());
        Assertions.assertEquals(List.of("http://a/", "http://other/", "http://mine/"), Arrays.asList(service.store.get(id).getPaths()));
    }

    @Test
    public void test_updateTagType_updatesOfOtherTagsAreNotAffected() throws Exception {
        final SharedTagTypeService service = newService();
        final TestHandler handler = new TestHandler(service);
        final TagType other = env.put("bar", "alice", false, "http://b/");
        service.store.put(other.getId(), TagHandlerTestSupport.copy(other, true));
        service.seqNoMap.put(other.getId(), 0L);

        handler.update(other.getId(), "http://c/");

        Assertions.assertEquals(List.of("http://b/", "http://c/"), Arrays.asList(service.store.get(other.getId()).getPaths()));
        Assertions.assertEquals(List.of("http://a/"), Arrays.asList(service.store.get(tagId(service)).getPaths()));
    }

    @Test
    public void test_updateTagType_unknownOrMalformedIdIsNotFound() throws Exception {
        final TestHandler handler = new TestHandler(newService());
        for (final String id : new String[] { null, "", "x", "0".repeat(64) }) {
            final TagRequestException e = Assertions.assertThrows(TagRequestException.class, () -> handler.update(id, "http://c/"));
            Assertions.assertEquals("tag not found", e.getMessage(), String.valueOf(id));
        }
    }

    @Test
    public void test_getRetryWaitMillis_doublesUpToTheCapWithJitter() {
        final TestHandler handler = new TestHandler(new FakeTagTypeService());
        long cap = AbstractTagHandler.RETRY_WAIT_MILLIS;
        for (int attempt = 1; attempt <= 40; attempt++) {
            for (int i = 0; i < 50; i++) {
                final long wait = handler.getRetryWaitMillis(attempt);
                Assertions.assertTrue(wait >= cap / 2 && wait <= cap,
                        "attempt " + attempt + ": " + wait + " is not in [" + cap / 2 + ", " + cap + "]");
            }
            cap = Math.min(AbstractTagHandler.RETRY_WAIT_MAX_MILLIS, cap * 2);
        }
    }
}
