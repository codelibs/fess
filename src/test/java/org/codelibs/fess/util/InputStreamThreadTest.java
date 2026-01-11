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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class InputStreamThreadTest extends UnitFessTestCase {

    @Test
    public void test_constructor() {
        String input = "test line";
        InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        InputStreamThread thread = new InputStreamThread(is, StandardCharsets.UTF_8, 10, null);

        assertEquals("InputStreamThread", thread.getName());
        assertNotNull(thread);
    }

    @Test
    public void test_run_withBuffering() throws InterruptedException {
        String input = "line1\nline2\nline3";
        InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        InputStreamThread thread = new InputStreamThread(is, StandardCharsets.UTF_8, 5, null);
        thread.start();
        thread.join(1000);

        String output = thread.getOutput();
        assertTrue(output.contains("line1"));
        assertTrue(output.contains("line2"));
        assertTrue(output.contains("line3"));
    }

    @Test
    public void test_run_withoutBuffering() throws InterruptedException {
        String input = "line1\nline2";
        InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        InputStreamThread thread = new InputStreamThread(is, StandardCharsets.UTF_8, 0, null);
        thread.start();
        thread.join(1000);

        String output = thread.getOutput();
        assertEquals("", output);
    }

    @Test
    public void test_run_withCallback() throws InterruptedException {
        String input = "callback1\ncallback2";
        InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        List<String> callbackResults = new ArrayList<>();
        Consumer<String> callback = line -> callbackResults.add(line);

        InputStreamThread thread = new InputStreamThread(is, StandardCharsets.UTF_8, 10, callback);
        thread.start();
        thread.join(1000);

        assertEquals(2, callbackResults.size());
        assertEquals("callback1", callbackResults.get(0));
        assertEquals("callback2", callbackResults.get(1));
    }

    @Test
    public void test_run_bufferSizeLimit() throws InterruptedException {
        StringBuilder inputBuilder = new StringBuilder();
        for (int i = 1; i <= 15; i++) {
            inputBuilder.append("line").append(i).append("\n");
        }

        InputStream is = new ByteArrayInputStream(inputBuilder.toString().getBytes(StandardCharsets.UTF_8));

        InputStreamThread thread = new InputStreamThread(is, StandardCharsets.UTF_8, 10, null);
        thread.start();
        thread.join(1000);

        String output = thread.getOutput();
        String[] lines = output.split("\n");

        assertTrue("Buffer should be limited to 10 items", lines.length <= 10);
        assertTrue("Last lines should be kept", output.contains("line15"));
    }

    @Test
    public void test_run_maxBufferSizeConstant() {
        assertEquals(1000, InputStreamThread.MAX_BUFFER_SIZE);
    }

    @Test
    public void test_run_withMaxBufferSize() throws InterruptedException {
        StringBuilder inputBuilder = new StringBuilder();
        for (int i = 1; i <= 1005; i++) {
            inputBuilder.append("line").append(i).append("\n");
        }

        InputStream is = new ByteArrayInputStream(inputBuilder.toString().getBytes(StandardCharsets.UTF_8));

        InputStreamThread thread = new InputStreamThread(is, StandardCharsets.UTF_8, InputStreamThread.MAX_BUFFER_SIZE, null);
        thread.start();
        thread.join(2000);

        String output = thread.getOutput();
        String[] lines = output.split("\n");

        assertTrue("Buffer should respect max size", lines.length <= InputStreamThread.MAX_BUFFER_SIZE + 1);
    }

    @Test
    public void test_getOutput_emptyStream() throws InterruptedException {
        InputStream is = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));

        InputStreamThread thread = new InputStreamThread(is, StandardCharsets.UTF_8, 10, null);
        thread.start();
        thread.join(1000);

        String output = thread.getOutput();
        assertEquals("", output);
    }

    @Test
    public void test_getOutput_multipleLines() throws InterruptedException {
        String input = "first\nsecond\nthird";
        InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        InputStreamThread thread = new InputStreamThread(is, StandardCharsets.UTF_8, 10, null);
        thread.start();
        thread.join(1000);

        String output = thread.getOutput();
        assertEquals("first\nsecond\nthird\n", output);
    }

    @Test
    public void test_contains_exactMatch() throws InterruptedException {
        String input = "exact line\nanother line";
        InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        InputStreamThread thread = new InputStreamThread(is, StandardCharsets.UTF_8, 10, null);
        thread.start();
        thread.join(1000);

        assertTrue(thread.contains("exact line"));
        assertTrue(thread.contains("another line"));
        assertFalse(thread.contains("nonexistent"));
    }

    @Test
    public void test_contains_withTrimming() throws InterruptedException {
        String input = "  trimmed line  \n\tanother\t";
        InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        InputStreamThread thread = new InputStreamThread(is, StandardCharsets.UTF_8, 10, null);
        thread.start();
        thread.join(1000);

        assertTrue(thread.contains("trimmed line"));
        assertTrue(thread.contains("another"));
        assertFalse(thread.contains("  trimmed line  "));
    }

    @Test
    public void test_contains_emptyBuffer() throws InterruptedException {
        String input = "test";
        InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        InputStreamThread thread = new InputStreamThread(is, StandardCharsets.UTF_8, 0, null);
        thread.start();
        thread.join(1000);

        assertFalse(thread.contains("test"));
    }

    @Test
    public void test_run_withDifferentCharsets() throws InterruptedException {
        String input = "UTF-16 test";
        InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_16));

        InputStreamThread thread = new InputStreamThread(is, StandardCharsets.UTF_16, 10, null);
        thread.start();
        thread.join(1000);

        String output = thread.getOutput();
        assertTrue(output.contains("UTF-16 test"));
    }

    @Test
    public void test_run_withIOException() throws InterruptedException {
        InputStream faultyStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Test exception");
            }
        };

        InputStreamThread thread = new InputStreamThread(faultyStream, StandardCharsets.UTF_8, 10, null);
        thread.start();
        thread.join(1000);

        String output = thread.getOutput();
        assertEquals("", output);
    }

    @Test
    public void test_run_callbackWithException() throws InterruptedException {
        String input = "test line";
        InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        Consumer<String> faultyCallback = line -> {
            throw new RuntimeException("Callback exception");
        };

        InputStreamThread thread = new InputStreamThread(is, StandardCharsets.UTF_8, 10, faultyCallback);
        thread.start();
        thread.join(1000);

        String output = thread.getOutput();
        assertTrue("Buffer should contain line before callback exception", output.contains("test line"));
    }

    @Test
    public void test_run_nullCallback() throws InterruptedException {
        String input = "test line";
        InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        InputStreamThread thread = new InputStreamThread(is, StandardCharsets.UTF_8, 10, null);
        thread.start();
        thread.join(1000);

        String output = thread.getOutput();
        assertTrue(output.contains("test line"));
    }

    @Test
    public void test_run_largeInput() throws InterruptedException {
        StringBuilder largeInput = new StringBuilder();
        for (int i = 0; i < 5000; i++) {
            largeInput.append("Line number ").append(i).append("\n");
        }

        InputStream is = new ByteArrayInputStream(largeInput.toString().getBytes(StandardCharsets.UTF_8));

        CountDownLatch latch = new CountDownLatch(1);
        List<String> receivedLines = new ArrayList<>();
        Consumer<String> callback = line -> {
            receivedLines.add(line);
            if (receivedLines.size() == 5000) {
                latch.countDown();
            }
        };

        InputStreamThread thread = new InputStreamThread(is, StandardCharsets.UTF_8, 100, callback);
        thread.start();

        assertTrue("Should process all lines within timeout", latch.await(3, TimeUnit.SECONDS));

        thread.join(1000);

        assertEquals("All lines should be processed", 5000, receivedLines.size());
        assertEquals("First line", "Line number 0", receivedLines.get(0));
        assertEquals("Last line", "Line number 4999", receivedLines.get(4999));
    }

    @Test
    public void test_run_concurrentAccess() throws InterruptedException {
        String input = "concurrent1\nconcurrent2\nconcurrent3";
        InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        InputStreamThread thread = new InputStreamThread(is, StandardCharsets.UTF_8, 10, null);
        thread.start();

        Thread.sleep(50);

        String output1 = thread.getOutput();
        boolean contains1 = thread.contains("concurrent1");

        thread.join(1000);

        String output2 = thread.getOutput();
        boolean contains2 = thread.contains("concurrent1");

        assertNotNull(output1);
        assertNotNull(output2);
        assertTrue(contains2);
    }

    @Test
    public void test_getOutput_specialCharacters() throws InterruptedException {
        String input = "Special: !@#$%^&*()\nUnicode: \u3042\u3044\u3046";
        InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        InputStreamThread thread = new InputStreamThread(is, StandardCharsets.UTF_8, 10, null);
        thread.start();
        thread.join(1000);

        String output = thread.getOutput();
        assertTrue(output.contains("Special: !@#$%^&*()"));
        assertTrue(output.contains("Unicode: \u3042\u3044\u3046"));
    }

    @Test
    public void test_contains_emptyString() throws InterruptedException {
        String input = "\nempty\n";
        InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        InputStreamThread thread = new InputStreamThread(is, StandardCharsets.UTF_8, 10, null);
        thread.start();
        thread.join(1000);

        assertTrue(thread.contains(""));
        assertTrue(thread.contains("empty"));
    }
}