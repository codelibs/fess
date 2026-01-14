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
package org.codelibs.fess.job;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.job.JobExecutor.ShutdownListener;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class JobExecutorTest extends UnitFessTestCase {

    private JobExecutor jobExecutor;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        // Create a concrete implementation for testing
        jobExecutor = new TestJobExecutor();
    }

    @Test
    public void test_execute() {
        // Test execute method
        String scriptType = "test";
        String script = "test script";
        Object result = jobExecutor.execute(scriptType, script);
        assertEquals("Executed: test test script", result);

        // Test with null values
        result = jobExecutor.execute(null, "script");
        assertEquals("Executed: null script", result);

        result = jobExecutor.execute("type", null);
        assertEquals("Executed: type null", result);

        result = jobExecutor.execute(null, null);
        assertEquals("Executed: null null", result);

        // Test with empty strings
        result = jobExecutor.execute("", "");
        assertEquals("Executed:  ", result);
    }

    @Test
    public void test_addShutdownListener() {
        // Test adding shutdown listener
        AtomicBoolean shutdownCalled = new AtomicBoolean(false);
        ShutdownListener listener = new ShutdownListener() {
            @Override
            public void onShutdown() {
                shutdownCalled.set(true);
            }
        };

        jobExecutor.addShutdownListener(listener);
        assertNotNull(jobExecutor.shutdownListener);
        assertEquals(listener, jobExecutor.shutdownListener);

        // Verify listener is called on shutdown
        jobExecutor.shutdown();
        assertTrue(shutdownCalled.get());
    }

    @Test
    public void test_addShutdownListener_replace() {
        // Test replacing shutdown listener
        AtomicInteger callCount = new AtomicInteger(0);
        ShutdownListener listener1 = new ShutdownListener() {
            @Override
            public void onShutdown() {
                callCount.incrementAndGet();
            }
        };

        ShutdownListener listener2 = new ShutdownListener() {
            @Override
            public void onShutdown() {
                callCount.addAndGet(10);
            }
        };

        // Add first listener
        jobExecutor.addShutdownListener(listener1);
        assertEquals(listener1, jobExecutor.shutdownListener);

        // Replace with second listener
        jobExecutor.addShutdownListener(listener2);
        assertEquals(listener2, jobExecutor.shutdownListener);

        // Verify only second listener is called
        jobExecutor.shutdown();
        assertEquals(10, callCount.get());
    }

    @Test
    public void test_shutdown() {
        // Test shutdown with listener
        AtomicBoolean shutdownCalled = new AtomicBoolean(false);
        jobExecutor.addShutdownListener(new ShutdownListener() {
            @Override
            public void onShutdown() {
                shutdownCalled.set(true);
            }
        });

        assertFalse(shutdownCalled.get());
        jobExecutor.shutdown();
        assertTrue(shutdownCalled.get());
    }

    @Test
    public void test_shutdown_withoutListener() {
        // Test shutdown without listener should throw NullPointerException
        try {
            jobExecutor.shutdown();
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected exception
            assertNotNull(e);
        }
    }

    @Test
    public void test_shutdown_multipleCalls() {
        // Test multiple shutdown calls
        AtomicInteger shutdownCount = new AtomicInteger(0);
        jobExecutor.addShutdownListener(new ShutdownListener() {
            @Override
            public void onShutdown() {
                shutdownCount.incrementAndGet();
            }
        });

        jobExecutor.shutdown();
        assertEquals(1, shutdownCount.get());

        jobExecutor.shutdown();
        assertEquals(2, shutdownCount.get());

        jobExecutor.shutdown();
        assertEquals(3, shutdownCount.get());
    }

    @Test
    public void test_shutdownListener_interface() {
        // Test anonymous ShutdownListener implementation
        final AtomicBoolean flag = new AtomicBoolean(false);
        ShutdownListener listener = new ShutdownListener() {
            @Override
            public void onShutdown() {
                flag.set(true);
            }
        };

        assertFalse(flag.get());
        listener.onShutdown();
        assertTrue(flag.get());
    }

    @Test
    public void test_shutdownListener_lambda() {
        // Test lambda implementation of ShutdownListener
        final AtomicBoolean flag = new AtomicBoolean(false);
        ShutdownListener listener = () -> flag.set(true);

        assertFalse(flag.get());
        listener.onShutdown();
        assertTrue(flag.get());
    }

    @Test
    public void test_shutdownListener_exceptionHandling() {
        // Test exception thrown in shutdown listener
        jobExecutor.addShutdownListener(new ShutdownListener() {
            @Override
            public void onShutdown() {
                throw new RuntimeException("Test exception");
            }
        });

        try {
            jobExecutor.shutdown();
            fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            assertEquals("Test exception", e.getMessage());
        }
    }

    @Test
    public void test_addShutdownListener_null() {
        // Test adding null listener
        jobExecutor.addShutdownListener(null);
        assertNull(jobExecutor.shutdownListener);

        // Shutdown with null listener should throw NullPointerException
        try {
            jobExecutor.shutdown();
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected exception
            assertNotNull(e);
        }
    }

    @Test
    public void test_multipleJobExecutors() {
        // Test multiple JobExecutor instances
        JobExecutor executor1 = new TestJobExecutor();
        JobExecutor executor2 = new TestJobExecutor();

        AtomicInteger counter1 = new AtomicInteger(0);
        AtomicInteger counter2 = new AtomicInteger(0);

        executor1.addShutdownListener(() -> counter1.incrementAndGet());
        executor2.addShutdownListener(() -> counter2.incrementAndGet());

        executor1.shutdown();
        assertEquals(1, counter1.get());
        assertEquals(0, counter2.get());

        executor2.shutdown();
        assertEquals(1, counter1.get());
        assertEquals(1, counter2.get());
    }

    @Test
    public void test_execute_differentScriptTypes() {
        // Test different script types
        Object result = jobExecutor.execute("javascript", "console.log('test')");
        assertEquals("Executed: javascript console.log('test')", result);

        result = jobExecutor.execute("python", "print('test')");
        assertEquals("Executed: python print('test')", result);

        result = jobExecutor.execute("groovy", "println 'test'");
        assertEquals("Executed: groovy println 'test'", result);
    }

    @Test
    public void test_execute_specialCharacters() {
        // Test scripts with special characters
        Object result = jobExecutor.execute("test", "script with\nnewline");
        assertEquals("Executed: test script with\nnewline", result);

        result = jobExecutor.execute("test", "script with\ttab");
        assertEquals("Executed: test script with\ttab", result);

        result = jobExecutor.execute("test", "script with \"quotes\"");
        assertEquals("Executed: test script with \"quotes\"", result);

        result = jobExecutor.execute("test", "script with 'single quotes'");
        assertEquals("Executed: test script with 'single quotes'", result);
    }

    @Test
    public void test_execute_longScript() {
        // Test with long script
        StringBuilder longScript = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longScript.append("line ").append(i).append("\n");
        }
        Object result = jobExecutor.execute("test", longScript.toString());
        assertEquals("Executed: test " + longScript.toString(), result);
    }

    // Test implementation of JobExecutor for testing purposes
    private static class TestJobExecutor extends JobExecutor {
        @Override
        public Object execute(String scriptType, String script) {
            // Simple implementation for testing
            return "Executed: " + scriptType + " " + script;
        }
    }
}