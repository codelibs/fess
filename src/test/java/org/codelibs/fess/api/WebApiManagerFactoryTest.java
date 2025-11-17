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
package org.codelibs.fess.api;

import org.codelibs.fess.unit.UnitFessTestCase;

import jakarta.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.*;

/**
 * Test class for WebApiManagerFactory.
 * Tests API manager registration and request matching.
 */
public class WebApiManagerFactoryTest extends UnitFessTestCase {

    private WebApiManagerFactory factory;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        factory = new WebApiManagerFactory();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        factory = null;
    }

    public void test_add() {
        WebApiManager manager = mock(WebApiManager.class);

        assertEquals(0, factory.size());

        factory.add(manager);

        assertEquals(1, factory.size());
    }

    public void test_add_multiple() {
        WebApiManager manager1 = mock(WebApiManager.class);
        WebApiManager manager2 = mock(WebApiManager.class);
        WebApiManager manager3 = mock(WebApiManager.class);

        factory.add(manager1);
        factory.add(manager2);
        factory.add(manager3);

        assertEquals(3, factory.size());
    }

    public void test_add_nullThrowsException() {
        try {
            factory.add(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("webApiManager must not be null", e.getMessage());
        }
    }

    public void test_get_matchingManager() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        WebApiManager manager1 = mock(WebApiManager.class);
        WebApiManager manager2 = mock(WebApiManager.class);

        when(manager1.matches(request)).thenReturn(false);
        when(manager2.matches(request)).thenReturn(true);

        factory.add(manager1);
        factory.add(manager2);

        WebApiManager result = factory.get(request);

        assertSame(manager2, result);
        verify(manager1).matches(request);
        verify(manager2).matches(request);
    }

    public void test_get_firstMatchingManager() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        WebApiManager manager1 = mock(WebApiManager.class);
        WebApiManager manager2 = mock(WebApiManager.class);
        WebApiManager manager3 = mock(WebApiManager.class);

        when(manager1.matches(request)).thenReturn(false);
        when(manager2.matches(request)).thenReturn(true);
        when(manager3.matches(request)).thenReturn(true);

        factory.add(manager1);
        factory.add(manager2);
        factory.add(manager3);

        WebApiManager result = factory.get(request);

        // Should return the first matching manager (manager2)
        assertSame(manager2, result);
        verify(manager1).matches(request);
        verify(manager2).matches(request);
        verify(manager3, never()).matches(request); // Should not check manager3
    }

    public void test_get_noMatchingManager() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        WebApiManager manager1 = mock(WebApiManager.class);
        WebApiManager manager2 = mock(WebApiManager.class);

        when(manager1.matches(request)).thenReturn(false);
        when(manager2.matches(request)).thenReturn(false);

        factory.add(manager1);
        factory.add(manager2);

        WebApiManager result = factory.get(request);

        assertNull(result);
        verify(manager1).matches(request);
        verify(manager2).matches(request);
    }

    public void test_get_emptyFactory() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        WebApiManager result = factory.get(request);

        assertNull(result);
    }

    public void test_threadSafety() throws Exception {
        final int threadCount = 10;
        final Thread[] threads = new Thread[threadCount];
        final WebApiManager[] managers = new WebApiManager[threadCount];

        // Create managers
        for (int i = 0; i < threadCount; i++) {
            managers[i] = mock(WebApiManager.class);
        }

        // Add managers concurrently
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> factory.add(managers[index]));
            threads[i].start();
        }

        // Wait for all threads
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify all managers were added
        assertEquals(threadCount, factory.size());
    }

    public void test_threadSafety_addAndGet() throws Exception {
        final int threadCount = 5;
        final Thread[] addThreads = new Thread[threadCount];
        final Thread[] getThreads = new Thread[threadCount];

        HttpServletRequest request = mock(HttpServletRequest.class);

        // Add managers in separate threads
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            addThreads[i] = new Thread(() -> {
                WebApiManager manager = mock(WebApiManager.class);
                when(manager.matches(any())).thenReturn(index == 0);
                factory.add(manager);
            });
            addThreads[i].start();
        }

        // Wait for add operations
        for (Thread thread : addThreads) {
            thread.join();
        }

        // Get managers in separate threads
        final WebApiManager[] results = new WebApiManager[threadCount];
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            getThreads[i] = new Thread(() -> {
                results[index] = factory.get(request);
            });
            getThreads[i].start();
        }

        // Wait for get operations
        for (Thread thread : getThreads) {
            thread.join();
        }

        // Verify get operations completed without errors
        // All should return the same manager (first matching one)
        WebApiManager firstResult = results[0];
        for (WebApiManager result : results) {
            assertSame(firstResult, result);
        }
    }

    public void test_size_initiallyZero() {
        assertEquals(0, factory.size());
    }

    public void test_size_afterAdditions() {
        for (int i = 1; i <= 5; i++) {
            factory.add(mock(WebApiManager.class));
            assertEquals(i, factory.size());
        }
    }
}
