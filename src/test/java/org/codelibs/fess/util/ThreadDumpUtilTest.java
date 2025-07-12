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
package org.codelibs.fess.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.codelibs.fess.Constants;
import org.codelibs.fess.unit.UnitFessTestCase;

public class ThreadDumpUtilTest extends UnitFessTestCase {

    public void test_printThreadDump() {
        // This test verifies that printThreadDump() method exists and can be called without exceptions
        try {
            ThreadDumpUtil.printThreadDump();
        } catch (Exception e) {
            fail("printThreadDump() should not throw exceptions: " + e.getMessage());
        }
    }

    public void test_printThreadDumpAsWarn() {
        // This test verifies that printThreadDumpAsWarn() method exists and can be called without exceptions
        try {
            ThreadDumpUtil.printThreadDumpAsWarn();
        } catch (Exception e) {
            fail("printThreadDumpAsWarn() should not throw exceptions: " + e.getMessage());
        }
    }

    public void test_printThreadDumpAsError() {
        // This test verifies that printThreadDumpAsError() method exists and can be called without exceptions
        try {
            ThreadDumpUtil.printThreadDumpAsError();
        } catch (Exception e) {
            fail("printThreadDumpAsError() should not throw exceptions: " + e.getMessage());
        }
    }

    public void test_writeThreadDump() throws IOException {
        // Create a temporary file for testing
        Path tempFile = Files.createTempFile("test-thread-dump", ".txt");

        try {
            ThreadDumpUtil.writeThreadDump(tempFile.toString());

            // Verify the file was created and contains content
            assertTrue("Thread dump file should exist", Files.exists(tempFile));
            assertTrue("Thread dump file should not be empty", Files.size(tempFile) > 0);

            // Verify the content contains thread information
            String content = new String(Files.readAllBytes(tempFile), Constants.CHARSET_UTF_8);
            assertTrue("Content should contain 'Thread:'", content.contains("Thread:"));
            assertTrue("Content should contain stack trace elements", content.contains("\tat "));

        } finally {
            // Clean up
            Files.deleteIfExists(tempFile);
        }
    }

    public void test_writeThreadDump_withInvalidPath() {
        // Test with invalid file path (directory that doesn't exist)
        String invalidPath = "/nonexistent/directory/thread-dump.txt";

        try {
            ThreadDumpUtil.writeThreadDump(invalidPath);
            // Should not throw exception but log warning
        } catch (Exception e) {
            fail("writeThreadDump should handle invalid paths gracefully: " + e.getMessage());
        }
    }

    public void test_writeThreadDump_withNullPath() {
        try {
            ThreadDumpUtil.writeThreadDump(null);
            // Should not throw exception but log warning
        } catch (Exception e) {
            fail("writeThreadDump should handle null path gracefully: " + e.getMessage());
        }
    }

    public void test_writeThreadDump_withEmptyPath() {
        try {
            ThreadDumpUtil.writeThreadDump("");
            // Should not throw exception but log warning
        } catch (Exception e) {
            fail("writeThreadDump should handle empty path gracefully: " + e.getMessage());
        }
    }

    public void test_processThreadDump() {
        List<String> capturedOutput = new ArrayList<>();
        Consumer<String> testConsumer = capturedOutput::add;

        ThreadDumpUtil.processThreadDump(testConsumer);

        // Verify that thread dump was processed
        assertFalse("Should capture some thread dump output", capturedOutput.isEmpty());

        // Verify format - should contain "Thread:" entries
        boolean hasThreadEntry = capturedOutput.stream().anyMatch(line -> line.startsWith("Thread:"));
        assertTrue("Should contain Thread: entries", hasThreadEntry);

        // Verify format - should contain stack trace entries
        boolean hasStackTrace = capturedOutput.stream().anyMatch(line -> line.startsWith("\tat "));
        assertTrue("Should contain stack trace entries", hasStackTrace);
    }

    public void test_processThreadDump_withNullConsumer() {
        try {
            ThreadDumpUtil.processThreadDump(null);
            fail("Should throw NullPointerException for null consumer");
        } catch (NullPointerException e) {
            // Expected behavior
        }
    }

    public void test_processThreadDump_multipleThreads() {
        AtomicInteger threadCount = new AtomicInteger(0);
        AtomicInteger stackTraceCount = new AtomicInteger(0);

        Consumer<String> countingConsumer = line -> {
            if (line.startsWith("Thread:")) {
                threadCount.incrementAndGet();
            } else if (line.startsWith("\tat ")) {
                stackTraceCount.incrementAndGet();
            }
        };

        ThreadDumpUtil.processThreadDump(countingConsumer);

        // Verify that multiple threads were processed
        assertTrue("Should process at least one thread", threadCount.get() >= 1);
        assertTrue("Should have stack trace elements", stackTraceCount.get() > 0);
        assertTrue("Should have more stack traces than threads", stackTraceCount.get() > threadCount.get());
    }

    public void test_processThreadDump_withException() {
        Consumer<String> exceptionConsumer = line -> {
            throw new RuntimeException("Test exception");
        };

        try {
            ThreadDumpUtil.processThreadDump(exceptionConsumer);
            fail("Should propagate RuntimeException from consumer");
        } catch (RuntimeException e) {
            assertEquals("Test exception", e.getMessage());
        }
    }

    public void test_processThreadDump_withCustomThread() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger customThreadFound = new AtomicInteger(0);

        // Create a custom thread with recognizable name
        Thread customThread = new Thread(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "CustomTestThread");

        customThread.start();

        try {
            Consumer<String> searchConsumer = line -> {
                if (line.contains("CustomTestThread")) {
                    customThreadFound.incrementAndGet();
                }
            };

            ThreadDumpUtil.processThreadDump(searchConsumer);

            // Verify that our custom thread was captured
            assertTrue("Should find custom thread in dump", customThreadFound.get() > 0);

        } finally {
            latch.countDown();
            customThread.join(1000); // Wait up to 1 second for thread to finish
        }
    }

    public void test_writeThreadDump_fileContent() throws IOException {
        Path tempFile = Files.createTempFile("test-thread-dump-content", ".txt");

        try {
            ThreadDumpUtil.writeThreadDump(tempFile.toString());

            // Read and verify file content
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader =
                    new BufferedReader(new InputStreamReader(new FileInputStream(tempFile.toFile()), Constants.CHARSET_UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }

            assertFalse("File should contain lines", lines.isEmpty());

            // Verify content structure
            boolean hasThreadLine = lines.stream().anyMatch(line -> line.startsWith("Thread:"));
            boolean hasStackTraceLine = lines.stream().anyMatch(line -> line.startsWith("\tat "));

            assertTrue("Should contain thread information", hasThreadLine);
            assertTrue("Should contain stack trace information", hasStackTraceLine);

        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    public void test_constructor_isProtected() {
        // Verify that ThreadDumpUtil has a protected constructor (utility class pattern)
        try {
            Constructor<ThreadDumpUtil> constructor = ThreadDumpUtil.class.getDeclaredConstructor();
            assertTrue("Constructor should be protected", java.lang.reflect.Modifier.isProtected(constructor.getModifiers()));

            // Test that constructor is accessible when made accessible
            constructor.setAccessible(true);
            ThreadDumpUtil instance = constructor.newInstance();
            assertNotNull(instance);

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            fail("Should be able to access protected constructor: " + e.getMessage());
        }
    }

    public void test_static_method_signatures() {
        // Verify all public static methods exist with correct signatures
        try {
            // printThreadDump()
            java.lang.reflect.Method printMethod = ThreadDumpUtil.class.getMethod("printThreadDump");
            assertTrue("printThreadDump should be static", java.lang.reflect.Modifier.isStatic(printMethod.getModifiers()));
            assertTrue("printThreadDump should be public", java.lang.reflect.Modifier.isPublic(printMethod.getModifiers()));
            assertEquals("printThreadDump should return void", void.class, printMethod.getReturnType());

            // printThreadDumpAsWarn()
            java.lang.reflect.Method warnMethod = ThreadDumpUtil.class.getMethod("printThreadDumpAsWarn");
            assertTrue("printThreadDumpAsWarn should be static", java.lang.reflect.Modifier.isStatic(warnMethod.getModifiers()));
            assertTrue("printThreadDumpAsWarn should be public", java.lang.reflect.Modifier.isPublic(warnMethod.getModifiers()));
            assertEquals("printThreadDumpAsWarn should return void", void.class, warnMethod.getReturnType());

            // printThreadDumpAsError()
            java.lang.reflect.Method errorMethod = ThreadDumpUtil.class.getMethod("printThreadDumpAsError");
            assertTrue("printThreadDumpAsError should be static", java.lang.reflect.Modifier.isStatic(errorMethod.getModifiers()));
            assertTrue("printThreadDumpAsError should be public", java.lang.reflect.Modifier.isPublic(errorMethod.getModifiers()));
            assertEquals("printThreadDumpAsError should return void", void.class, errorMethod.getReturnType());

            // writeThreadDump(String)
            java.lang.reflect.Method writeMethod = ThreadDumpUtil.class.getMethod("writeThreadDump", String.class);
            assertTrue("writeThreadDump should be static", java.lang.reflect.Modifier.isStatic(writeMethod.getModifiers()));
            assertTrue("writeThreadDump should be public", java.lang.reflect.Modifier.isPublic(writeMethod.getModifiers()));
            assertEquals("writeThreadDump should return void", void.class, writeMethod.getReturnType());

            // processThreadDump(Consumer)
            java.lang.reflect.Method processMethod = ThreadDumpUtil.class.getMethod("processThreadDump", Consumer.class);
            assertTrue("processThreadDump should be static", java.lang.reflect.Modifier.isStatic(processMethod.getModifiers()));
            assertTrue("processThreadDump should be public", java.lang.reflect.Modifier.isPublic(processMethod.getModifiers()));
            assertEquals("processThreadDump should return void", void.class, processMethod.getReturnType());

        } catch (NoSuchMethodException e) {
            fail("All expected public methods should exist: " + e.getMessage());
        }
    }

    public void test_utilityClass_pattern() {
        // Verify utility class design pattern compliance

        // 1. Class should have protected constructor
        Constructor<?>[] constructors = ThreadDumpUtil.class.getDeclaredConstructors();
        assertEquals("Should have exactly one constructor", 1, constructors.length);
        assertTrue("Constructor should be protected", java.lang.reflect.Modifier.isProtected(constructors[0].getModifiers()));

        // 2. All public methods should be static
        java.lang.reflect.Method[] methods = ThreadDumpUtil.class.getDeclaredMethods();
        for (java.lang.reflect.Method method : methods) {
            if (java.lang.reflect.Modifier.isPublic(method.getModifiers()) && !method.isSynthetic()) {
                assertTrue("Public method " + method.getName() + " should be static",
                        java.lang.reflect.Modifier.isStatic(method.getModifiers()));
            }
        }
    }

    public void test_thread_safety() throws InterruptedException {
        // Test concurrent access to thread dump methods
        final AtomicInteger successCount = new AtomicInteger(0);
        final AtomicInteger errorCount = new AtomicInteger(0);
        final int threadCount = 10;
        final CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    // Test different methods concurrently
                    ThreadDumpUtil.printThreadDump();
                    ThreadDumpUtil.printThreadDumpAsWarn();
                    ThreadDumpUtil.printThreadDumpAsError();

                    List<String> output = new ArrayList<>();
                    ThreadDumpUtil.processThreadDump(output::add);

                    if (!output.isEmpty()) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();

        assertEquals("All threads should succeed", threadCount, successCount.get());
        assertEquals("No threads should have errors", 0, errorCount.get());
    }

    public void test_writeThreadDump_withSpecialCharacters() throws IOException {
        // Test with file path containing special characters
        Path tempDir = Files.createTempDirectory("test-スレッドダンプ-dir");
        Path tempFile = tempDir.resolve("thread-dump-テスト.txt");

        try {
            ThreadDumpUtil.writeThreadDump(tempFile.toString());

            assertTrue("File with special characters should be created", Files.exists(tempFile));
            assertTrue("File should contain content", Files.size(tempFile) > 0);

        } finally {
            Files.deleteIfExists(tempFile);
            Files.deleteIfExists(tempDir);
        }
    }
}