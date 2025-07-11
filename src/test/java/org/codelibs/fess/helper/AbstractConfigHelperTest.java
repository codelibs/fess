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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.unit.UnitFessTestCase;

public class AbstractConfigHelperTest extends UnitFessTestCase {

    private TestConfigHelper configHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        configHelper = new TestConfigHelper();
    }

    public void test_setReloadInterval() {
        assertEquals(1000L, configHelper.reloadInterval);

        configHelper.setReloadInterval(5000L);
        assertEquals(5000L, configHelper.reloadInterval);

        configHelper.setReloadInterval(0L);
        assertEquals(0L, configHelper.reloadInterval);

        configHelper.setReloadInterval(-1L);
        assertEquals(-1L, configHelper.reloadInterval);
    }

    public void test_waitForNext_withPositiveInterval() throws InterruptedException {
        configHelper.setReloadInterval(20L);

        long startTime = System.currentTimeMillis();
        configHelper.waitForNext();
        long endTime = System.currentTimeMillis();

        long elapsed = endTime - startTime;
        assertTrue("Expected at least 15ms sleep, got " + elapsed + "ms", elapsed >= 15);
        // Generous tolerance for CI environments like GitHub Actions
        assertTrue("Expected less than 100ms sleep, got " + elapsed + "ms", elapsed < 100);
    }

    public void test_waitForNext_withZeroInterval() {
        configHelper.setReloadInterval(0L);

        long startTime = System.currentTimeMillis();
        configHelper.waitForNext();
        long endTime = System.currentTimeMillis();

        long elapsed = endTime - startTime;
        assertTrue("Expected minimal sleep time, got " + elapsed + "ms", elapsed < 50);
    }

    public void test_waitForNext_withNegativeInterval() {
        configHelper.setReloadInterval(-100L);

        long startTime = System.currentTimeMillis();
        configHelper.waitForNext();
        long endTime = System.currentTimeMillis();

        long elapsed = endTime - startTime;
        assertTrue("Expected minimal sleep time with negative interval, got " + elapsed + "ms", elapsed < 50);
    }

    public void test_update_callsLoad() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        configHelper.setLoadCallback(() -> {
            latch.countDown();
            return 1;
        });

        configHelper.update();

        assertTrue("Load method should be called within 5 seconds", latch.await(5, TimeUnit.SECONDS));
        assertEquals(1, configHelper.getLoadCallCount());
    }

    public void test_update_multipleCallsIncrementLoadCount() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        configHelper.setLoadCallback(() -> {
            latch.countDown();
            return 1;
        });

        configHelper.update();
        configHelper.update();
        configHelper.update();

        assertTrue("All load methods should be called within 5 seconds", latch.await(5, TimeUnit.SECONDS));
        assertEquals(3, configHelper.getLoadCallCount());
    }

    public void test_load_returnsExpectedValue() {
        configHelper.setLoadCallback(() -> 42);
        assertEquals(42, configHelper.load());

        configHelper.setLoadCallback(() -> 0);
        assertEquals(0, configHelper.load());

        configHelper.setLoadCallback(() -> -1);
        assertEquals(-1, configHelper.load());
    }

    static class TestConfigHelper extends AbstractConfigHelper {

        private final AtomicInteger loadCallCount = new AtomicInteger(0);
        private LoadCallback loadCallback = () -> 0;

        @Override
        public int load() {
            loadCallCount.incrementAndGet();
            return loadCallback.call();
        }

        public void setLoadCallback(LoadCallback callback) {
            this.loadCallback = callback;
        }

        public int getLoadCallCount() {
            return loadCallCount.get();
        }
    }

    @FunctionalInterface
    interface LoadCallback {
        int call();
    }
}