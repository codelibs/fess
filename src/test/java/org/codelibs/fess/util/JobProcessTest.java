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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.codelibs.fess.unit.UnitFessTestCase;

public class JobProcessTest extends UnitFessTestCase {

    public void test_constructor_processOnly() throws IOException {
        Process mockProcess = createMockProcess("test output\nline2");

        JobProcess jobProcess = new JobProcess(mockProcess);

        assertNotNull(jobProcess);
        assertSame(mockProcess, jobProcess.getProcess());
        assertNotNull(jobProcess.getInputStreamThread());
        assertEquals("InputStreamThread", jobProcess.getInputStreamThread().getName());
    }

    public void test_constructor_withBufferSizeAndCallback() throws IOException {
        Process mockProcess = createMockProcess("callback test\nanother line");
        List<String> callbackResults = new ArrayList<>();
        Consumer<String> callback = line -> callbackResults.add(line);

        JobProcess jobProcess = new JobProcess(mockProcess, 50, callback);

        assertNotNull(jobProcess);
        assertSame(mockProcess, jobProcess.getProcess());
        assertNotNull(jobProcess.getInputStreamThread());
        assertEquals("InputStreamThread", jobProcess.getInputStreamThread().getName());
    }

    public void test_getProcess() throws IOException {
        Process mockProcess = createMockProcess("test");
        JobProcess jobProcess = new JobProcess(mockProcess);

        Process retrievedProcess = jobProcess.getProcess();

        assertSame(mockProcess, retrievedProcess);
    }

    public void test_getInputStreamThread() throws IOException {
        Process mockProcess = createMockProcess("test data");
        JobProcess jobProcess = new JobProcess(mockProcess);

        InputStreamThread thread = jobProcess.getInputStreamThread();

        assertNotNull(thread);
        assertEquals("InputStreamThread", thread.getName());
    }

    public void test_constructor_withZeroBufferSize() throws IOException {
        Process mockProcess = createMockProcess("test");
        JobProcess jobProcess = new JobProcess(mockProcess, 0, null);

        assertNotNull(jobProcess.getInputStreamThread());
    }

    public void test_constructor_withNullCallback() throws IOException {
        Process mockProcess = createMockProcess("test");
        JobProcess jobProcess = new JobProcess(mockProcess, 10, null);

        assertNotNull(jobProcess.getInputStreamThread());
    }

    public void test_integration_withInputStreamThread() throws IOException, InterruptedException {
        Process mockProcess = createMockProcess("integration test\nsecond line");
        List<String> callbackResults = new ArrayList<>();
        Consumer<String> callback = line -> callbackResults.add(line);

        JobProcess jobProcess = new JobProcess(mockProcess, 100, callback);
        InputStreamThread thread = jobProcess.getInputStreamThread();

        thread.start();
        thread.join(1000);

        assertEquals(2, callbackResults.size());
        assertEquals("integration test", callbackResults.get(0));
        assertEquals("second line", callbackResults.get(1));

        String output = thread.getOutput();
        assertTrue(output.contains("integration test"));
        assertTrue(output.contains("second line"));
    }

    public void test_defaultBufferSizeConstant() throws IOException {
        Process mockProcess = createMockProcess("test");
        JobProcess jobProcess = new JobProcess(mockProcess);

        assertNotNull(jobProcess.getInputStreamThread());
    }

    public void test_multipleJobProcesses() throws IOException {
        Process mockProcess1 = createMockProcess("process1 output");
        Process mockProcess2 = createMockProcess("process2 output");

        JobProcess jobProcess1 = new JobProcess(mockProcess1);
        JobProcess jobProcess2 = new JobProcess(mockProcess2);

        assertNotSame(jobProcess1.getProcess(), jobProcess2.getProcess());
        assertNotSame(jobProcess1.getInputStreamThread(), jobProcess2.getInputStreamThread());
    }

    public void test_constructor_withLargeBufferSize() throws IOException {
        Process mockProcess = createMockProcess("large buffer test");
        JobProcess jobProcess = new JobProcess(mockProcess, 10000, null);

        assertNotNull(jobProcess.getInputStreamThread());
    }

    public void test_constructor_withMaxBufferSize() throws IOException {
        Process mockProcess = createMockProcess("max buffer test");
        JobProcess jobProcess = new JobProcess(mockProcess, InputStreamThread.MAX_BUFFER_SIZE, null);

        assertNotNull(jobProcess.getInputStreamThread());
    }

    public void test_inputStreamThreadProcessOutput() throws IOException, InterruptedException {
        Process mockProcess = createMockProcess("line1\nline2\nline3\nline4\nline5");
        JobProcess jobProcess = new JobProcess(mockProcess, 3, null);

        InputStreamThread thread = jobProcess.getInputStreamThread();
        thread.start();
        thread.join(1000);

        String output = thread.getOutput();
        assertTrue("Should contain recent lines", output.contains("line5"));

        assertTrue("Should have line3", thread.contains("line3"));
        assertTrue("Should have line4", thread.contains("line4"));
        assertTrue("Should have line5", thread.contains("line5"));
    }

    public void test_emptyInputStream() throws IOException, InterruptedException {
        Process mockProcess = createMockProcess("");
        JobProcess jobProcess = new JobProcess(mockProcess);

        InputStreamThread thread = jobProcess.getInputStreamThread();
        thread.start();
        thread.join(1000);

        String output = thread.getOutput();
        assertEquals("", output);
    }

    public void test_callbackExceptionHandling() throws IOException, InterruptedException {
        Process mockProcess = createMockProcess("exception test");
        Consumer<String> faultyCallback = line -> {
            throw new RuntimeException("Callback exception");
        };

        JobProcess jobProcess = new JobProcess(mockProcess, 10, faultyCallback);
        InputStreamThread thread = jobProcess.getInputStreamThread();

        thread.start();
        thread.join(1000);

        assertNotNull(thread);
    }

    public void test_inputStreamThreadProperties() throws IOException {
        Process mockProcess = createMockProcess("property test");
        JobProcess jobProcess = new JobProcess(mockProcess, 25, null);

        InputStreamThread thread = jobProcess.getInputStreamThread();

        assertEquals("InputStreamThread", thread.getName());
        assertFalse("Thread should not be started yet", thread.isAlive());
    }

    public void test_processAssignment() throws IOException {
        Process mockProcess = createMockProcess("assignment test");
        JobProcess jobProcess = new JobProcess(mockProcess);

        Process assignedProcess = jobProcess.getProcess();

        assertSame("Process should be exactly the same instance", mockProcess, assignedProcess);
    }

    private Process createMockProcess(String output) {
        return new MockProcess(output);
    }

    private static class MockProcess extends Process {
        private final InputStream inputStream;

        public MockProcess(String output) {
            this.inputStream = new ByteArrayInputStream(output.getBytes());
        }

        @Override
        public OutputStream getOutputStream() {
            return null;
        }

        @Override
        public InputStream getInputStream() {
            return inputStream;
        }

        @Override
        public InputStream getErrorStream() {
            return null;
        }

        @Override
        public int waitFor() throws InterruptedException {
            return 0;
        }

        @Override
        public int exitValue() {
            return 0;
        }

        @Override
        public void destroy() {
        }
    }
}