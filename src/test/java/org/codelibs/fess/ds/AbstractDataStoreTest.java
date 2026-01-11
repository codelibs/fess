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
package org.codelibs.fess.ds;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.codelibs.fess.Constants;
import org.codelibs.fess.ds.callback.IndexUpdateCallback;
import org.codelibs.fess.entity.DataStoreParams;
import org.codelibs.fess.exception.JobProcessingException;
import org.codelibs.fess.opensearch.config.exentity.DataConfig;
import org.codelibs.fess.script.AbstractScriptEngine;
import org.codelibs.fess.script.ScriptEngineFactory;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class AbstractDataStoreTest extends UnitFessTestCase {
    public AbstractDataStore dataStore;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        dataStore = new AbstractDataStore() {
            @Override
            protected String getName() {
                return "Test";
            }

            @Override
            protected void storeData(DataConfig dataConfig, IndexUpdateCallback callback, DataStoreParams paramMap,
                    Map<String, String> scriptMap, Map<String, Object> defaultDataMap) {
                // TODO nothing
            }
        };

        ScriptEngineFactory scriptEngineFactory = new ScriptEngineFactory();
        ComponentUtil.register(scriptEngineFactory, "scriptEngineFactory");
        new AbstractScriptEngine() {

            @Override
            public Object evaluate(String template, Map<String, Object> paramMap) {
                final Map<String, Object> bindingMap = new HashMap<>(paramMap);
                bindingMap.put("container", SingletonLaContainerFactory.getContainer());
                final GroovyShell groovyShell = new GroovyShell(new Binding(bindingMap));
                try {
                    return groovyShell.evaluate(template);
                } catch (final JobProcessingException e) {
                    throw e;
                } catch (final Exception e) {
                    return null;
                } finally {
                    final GroovyClassLoader loader = groovyShell.getClassLoader();
                    loader.clearCache();
                }
            }

            @Override
            protected String getName() {
                return Constants.DEFAULT_SCRIPT;
            }
        }.register();
    }

    @Test
    public void test_convertValue() {
        String value;
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("param1", "PARAM1");
        paramMap.put("param2", "PARAM2+");
        paramMap.put("param3", "PARAM3*");

        value = "\"abc\"";
        assertEquals("abc", dataStore.convertValue(Constants.DEFAULT_SCRIPT, value, paramMap));

        value = "param1";
        assertEquals("PARAM1", dataStore.convertValue(Constants.DEFAULT_SCRIPT, value, paramMap));

        value = "param2";
        assertEquals("PARAM2+", dataStore.convertValue(Constants.DEFAULT_SCRIPT, value, paramMap));

        value = "\"123\"+param2+\",\"+param3+\"abc\"";
        assertEquals("123PARAM2+,PARAM3*abc", dataStore.convertValue(Constants.DEFAULT_SCRIPT, value, paramMap));

        value = null;
        assertEquals("", dataStore.convertValue(Constants.DEFAULT_SCRIPT, value, paramMap));

        value = "";
        assertEquals("", dataStore.convertValue(Constants.DEFAULT_SCRIPT, value, paramMap));

        value = " ";
        assertNull(dataStore.convertValue(Constants.DEFAULT_SCRIPT, value, paramMap));
    }

    // ========== Thread Safety Tests ==========

    /**
     * Test that the volatile alive field is visible across threads.
     * One thread sets alive to false, other threads should see the change immediately.
     */
    @Test
    public void test_aliveField_volatileVisibility() throws Exception {
        // Ensure alive starts as true
        assertTrue(dataStore.alive);

        final int readerThreadCount = 5;
        final long testDurationMs = 200; // Run for 200ms
        final Thread[] readerThreads = new Thread[readerThreadCount];
        final java.util.List<Boolean>[] observations = new java.util.List[readerThreadCount];
        final CountDownLatch startLatch = new CountDownLatch(1);

        // Initialize observation lists
        for (int i = 0; i < readerThreadCount; i++) {
            observations[i] = new java.util.ArrayList<>();
        }

        // Start reader threads that continuously check alive field for a fixed duration
        for (int i = 0; i < readerThreadCount; i++) {
            final int threadIndex = i;
            readerThreads[i] = new Thread(() -> {
                try {
                    startLatch.await(); // Wait for start signal
                    long endTime = System.currentTimeMillis() + testDurationMs;
                    while (System.currentTimeMillis() < endTime) {
                        observations[threadIndex].add(dataStore.alive);
                        Thread.yield(); // Allow other threads to run
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            readerThreads[i].start();
        }

        // Writer thread sets alive to false early
        Thread writerThread = new Thread(() -> {
            try {
                startLatch.await(); // Wait for start signal
                // Set alive to false very early - no delay
                dataStore.alive = false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        writerThread.start();

        // Give threads a moment to reach the latch
        Thread.sleep(50);

        // Release all threads to start simultaneously
        startLatch.countDown();

        // Wait for writer thread to complete
        writerThread.join(1000);

        // Wait for all reader threads to complete
        for (Thread thread : readerThreads) {
            thread.join(testDurationMs + 1000);
        }

        // Verify that alive was changed to false
        assertFalse("alive should be false after writer thread", dataStore.alive);

        // At least some observations from reader threads should have seen false
        // due to volatile ensuring visibility
        int falseCount = 0;
        for (int i = 0; i < readerThreadCount; i++) {
            for (Boolean observed : observations[i]) {
                if (!observed) {
                    falseCount++;
                }
            }
        }
        assertTrue("Some threads should have observed alive=false", falseCount > 0);
    }

    /**
     * Test stop() method sets alive to false and is visible to other threads.
     */
    @Test
    public void test_stop_volatileVisibility() throws Exception {
        assertTrue(dataStore.alive);

        final boolean[] observedValues = new boolean[10];
        final Thread[] threads = new Thread[10];

        // Call stop() in main thread
        dataStore.stop();

        // Multiple threads read the alive field
        for (int i = 0; i < 10; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                observedValues[index] = dataStore.alive;
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // All threads should observe alive=false due to volatile
        for (int i = 0; i < 10; i++) {
            assertFalse("Thread " + i + " should see alive=false", observedValues[i]);
        }
    }

    /**
     * Test concurrent access to stop() method.
     * Multiple threads call stop() simultaneously - should be safe.
     */
    @Test
    public void test_stop_concurrentAccess() throws Exception {
        final int threadCount = 10;
        final Thread[] threads = new Thread[threadCount];
        final Exception[] exceptions = new Exception[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    dataStore.stop();
                } catch (Exception e) {
                    exceptions[index] = e;
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify no exceptions occurred
        for (int i = 0; i < threadCount; i++) {
            assertNull(exceptions[i], "Thread " + i + " threw exception");
        }

        // Verify alive is false
        assertFalse("alive should be false after all stop() calls", dataStore.alive);
    }

    /**
     * Test that multiple threads can safely read alive field while one writes.
     */
    @Test
    public void test_aliveField_concurrentReadWrite() throws Exception {
        dataStore.alive = true;

        final int readerCount = 5;
        final int iterations = 1000;
        final Thread[] readers = new Thread[readerCount];
        final Exception[] exceptions = new Exception[readerCount + 1];
        final int[] trueCount = new int[readerCount];
        final int[] falseCount = new int[readerCount];

        // Start reader threads
        for (int i = 0; i < readerCount; i++) {
            final int index = i;
            readers[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < iterations; j++) {
                        if (dataStore.alive) {
                            trueCount[index]++;
                        } else {
                            falseCount[index]++;
                        }
                    }
                } catch (Exception e) {
                    exceptions[index] = e;
                }
            });
        }

        // Start writer thread that toggles alive
        Thread writer = new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    dataStore.alive = !dataStore.alive;
                    Thread.yield();
                }
            } catch (Exception e) {
                exceptions[readerCount] = e;
            }
        });

        // Start all threads
        for (Thread reader : readers) {
            reader.start();
        }
        writer.start();

        // Wait for completion
        for (Thread reader : readers) {
            reader.join();
        }
        writer.join();

        // Verify no exceptions
        for (int i = 0; i <= readerCount; i++) {
            assertNull(exceptions[i], "Thread " + i + " threw exception");
        }

        // Verify all readers completed all iterations
        for (int i = 0; i < readerCount; i++) {
            assertEquals("Reader " + i + " should complete all iterations", iterations, trueCount[i] + falseCount[i]);
        }
    }

    /**
     * Test getName() method can be called concurrently without issues.
     */
    @Test
    public void test_getName_concurrentAccess() throws Exception {
        final int threadCount = 10;
        final Thread[] threads = new Thread[threadCount];
        final String[] results = new String[threadCount];
        final Exception[] exceptions = new Exception[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 100; j++) {
                        results[index] = dataStore.getName();
                    }
                } catch (Exception e) {
                    exceptions[index] = e;
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Verify no exceptions
        for (int i = 0; i < threadCount; i++) {
            assertNull(exceptions[i], "Thread " + i + " threw exception");
            assertEquals("Thread " + i + " got wrong name", "Test", results[i]);
        }
    }
}
