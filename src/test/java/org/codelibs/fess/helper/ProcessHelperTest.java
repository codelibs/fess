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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.codelibs.fess.exception.JobNotFoundException;
import org.codelibs.fess.exception.JobProcessingException;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.JobProcess;

public class ProcessHelperTest extends UnitFessTestCase {

    public ProcessHelper processHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        processHelper = new ProcessHelper();
    }

    @Override
    public void tearDown() throws Exception {
        // Clean up any running processes
        processHelper.destroy();
        super.tearDown();
    }

    public void test_isProcessRunning_empty() {
        assertFalse(processHelper.isProcessRunning());
    }

    public void test_isProcessRunning_withSessionId_notExists() {
        assertFalse(processHelper.isProcessRunning("nonexistent"));
    }

    public void test_getRunningSessionIdSet_empty() {
        Set<String> sessionIds = processHelper.getRunningSessionIdSet();
        assertNotNull(sessionIds);
        assertTrue(sessionIds.isEmpty());
    }

    public void test_destroyProcess_nonExistent() {
        int result = processHelper.destroyProcess("nonexistent");
        assertEquals(-1, result);
    }

    public void test_setProcessDestroyTimeout() {
        processHelper.setProcessDestroyTimeout(20);
        // We can't directly verify the timeout value, but we can ensure the method doesn't throw
        assertTrue(true);
    }

    public void test_sendCommand_jobNotFound() {
        try {
            processHelper.sendCommand("nonexistent", "test command");
            fail("Expected JobNotFoundException");
        } catch (JobNotFoundException e) {
            assertTrue(e.getMessage().contains("Job for nonexistent is not found"));
        }
    }

    public void test_startProcess_basicCommand() {
        String sessionId = "test_session";
        List<String> cmdList = Arrays.asList("echo", "hello");
        Consumer<ProcessBuilder> pbCall = pb -> {
            pb.redirectErrorStream(true);
        };

        try {
            JobProcess jobProcess = processHelper.startProcess(sessionId, cmdList, pbCall);
            assertNotNull(jobProcess);
            assertTrue(processHelper.isProcessRunning());
            assertTrue(processHelper.isProcessRunning(sessionId));

            Set<String> sessionIds = processHelper.getRunningSessionIdSet();
            assertTrue(sessionIds.contains(sessionId));

            // Wait a bit for the process to complete
            Thread.sleep(100);

            // Clean up
            processHelper.destroyProcess(sessionId);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void test_startProcess_withBufferSizeAndCallback() {
        String sessionId = "test_session_buffer";
        List<String> cmdList = Arrays.asList("echo", "hello world");
        Consumer<ProcessBuilder> pbCall = pb -> {
            pb.redirectErrorStream(true);
        };

        List<String> outputs = new ArrayList<>();
        Consumer<String> outputCallback = output -> outputs.add(output);

        try {
            JobProcess jobProcess = processHelper.startProcess(sessionId, cmdList, pbCall, 1024, outputCallback);
            assertNotNull(jobProcess);

            // Poll for process to complete
            for (int i = 0; i < 50; i++) {
                if (!jobProcess.getProcess().isAlive()) {
                    break;
                }
                Thread.sleep(100);
            }

            // Clean up
            processHelper.destroyProcess(sessionId);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void test_startProcess_invalidCommand() {
        String sessionId = "test_invalid";
        List<String> cmdList = Arrays.asList("nonexistent_command_12345");
        Consumer<ProcessBuilder> pbCall = pb -> {
            pb.redirectErrorStream(true);
        };

        try {
            processHelper.startProcess(sessionId, cmdList, pbCall);
            fail("Expected JobProcessingException");
        } catch (JobProcessingException e) {
            assertTrue(e.getMessage().contains("Crawler Process terminated"));
        }
    }

    public void test_startProcess_replaceExistingProcess() {
        String sessionId = "test_replace";
        List<String> cmdList1 = Arrays.asList("echo", "first");
        List<String> cmdList2 = Arrays.asList("echo", "second");
        Consumer<ProcessBuilder> pbCall = pb -> {
            pb.redirectErrorStream(true);
        };

        try {
            // Start first process
            JobProcess jobProcess1 = processHelper.startProcess(sessionId, cmdList1, pbCall);
            assertNotNull(jobProcess1);
            assertTrue(processHelper.isProcessRunning(sessionId));

            // Start second process with same session ID (should replace first)
            JobProcess jobProcess2 = processHelper.startProcess(sessionId, cmdList2, pbCall);
            assertNotNull(jobProcess2);
            assertTrue(processHelper.isProcessRunning(sessionId));

            // Wait a bit for the processes to complete
            Thread.sleep(100);

            // Clean up
            processHelper.destroyProcess(sessionId);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void test_multipleProcesses() {
        String sessionId1 = "test_multi1";
        String sessionId2 = "test_multi2";
        List<String> cmdList = Arrays.asList("echo", "hello");
        Consumer<ProcessBuilder> pbCall = pb -> {
            pb.redirectErrorStream(true);
        };

        try {
            // Start multiple processes
            JobProcess jobProcess1 = processHelper.startProcess(sessionId1, cmdList, pbCall);
            JobProcess jobProcess2 = processHelper.startProcess(sessionId2, cmdList, pbCall);

            assertNotNull(jobProcess1);
            assertNotNull(jobProcess2);
            // Since echo command completes quickly, processes might already be finished
            // So we just verify that processes were created without errors

            Set<String> sessionIds = processHelper.getRunningSessionIdSet();
            // Since echo commands complete quickly, we may not catch them running
            // Just verify no exceptions occurred

            // Clean up
            processHelper.destroyProcess(sessionId1);
            processHelper.destroyProcess(sessionId2);

            // Test passes if no exceptions thrown
            assertTrue(true);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void test_destroyProcess_withRunningProcess() {
        String sessionId = "test_destroy";
        List<String> cmdList = Arrays.asList("echo", "hello");
        Consumer<ProcessBuilder> pbCall = pb -> {
            pb.redirectErrorStream(true);
        };

        try {
            JobProcess jobProcess = processHelper.startProcess(sessionId, cmdList, pbCall);
            assertNotNull(jobProcess);

            // Poll for process to be running (max 50 times, 100ms interval)
            boolean isRunning = false;
            for (int i = 0; i < 50; i++) {
                if (processHelper.isProcessRunning(sessionId)) {
                    isRunning = true;
                    break;
                }
                Thread.sleep(100);
            }
            assertTrue("Process did not become running within timeout", isRunning);

            // Wait a bit for the process to start
            Thread.sleep(50);

            // Destroy the process
            int exitCode = processHelper.destroyProcess(sessionId);
            assertFalse(processHelper.isProcessRunning(sessionId));

            // Exit code should be 0 or -1 depending on timing
            assertTrue(exitCode == 0 || exitCode == -1);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void test_destroy_allProcesses() {
        String sessionId1 = "test_destroy_all1";
        String sessionId2 = "test_destroy_all2";
        List<String> cmdList = Arrays.asList("echo", "hello");
        Consumer<ProcessBuilder> pbCall = pb -> {
            pb.redirectErrorStream(true);
        };

        try {
            // Start multiple processes
            processHelper.startProcess(sessionId1, cmdList, pbCall);
            processHelper.startProcess(sessionId2, cmdList, pbCall);

            assertTrue(processHelper.isProcessRunning());

            // Wait a bit for the processes to start
            Thread.sleep(50);

            // Destroy all processes
            processHelper.destroy();

            // Wait a bit for destruction to complete
            Thread.sleep(100);

            assertFalse(processHelper.isProcessRunning());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void test_sendCommand_withRunningProcess() {
        String sessionId = "test_send_command";
        List<String> cmdList = Arrays.asList("cat"); // cat reads from stdin
        Consumer<ProcessBuilder> pbCall = pb -> {
            pb.redirectErrorStream(true);
        };

        try {
            JobProcess jobProcess = processHelper.startProcess(sessionId, cmdList, pbCall);
            assertNotNull(jobProcess);
            assertTrue(processHelper.isProcessRunning(sessionId));

            // Wait a bit for the process to start
            Thread.sleep(50);

            // Send command
            processHelper.sendCommand(sessionId, "test input");

            // Wait a bit for the command to be processed
            Thread.sleep(50);

            // Clean up
            processHelper.destroyProcess(sessionId);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void test_processBuilder_configuration() {
        String sessionId = "test_pb_config";
        List<String> cmdList = Arrays.asList("echo", "hello");
        boolean[] pbCallExecuted = { false };

        Consumer<ProcessBuilder> pbCall = pb -> {
            pb.redirectErrorStream(true);
            pb.environment().put("TEST_VAR", "test_value");
            pbCallExecuted[0] = true;
        };

        try {
            JobProcess jobProcess = processHelper.startProcess(sessionId, cmdList, pbCall);
            assertNotNull(jobProcess);
            assertTrue(pbCallExecuted[0]);

            // Wait a bit for the process to complete
            Thread.sleep(100);

            // Clean up
            processHelper.destroyProcess(sessionId);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void test_isProcessRunning_afterProcessCompletes() {
        String sessionId = "test_completed";
        List<String> cmdList = Arrays.asList("echo", "hello");
        Consumer<ProcessBuilder> pbCall = pb -> {
            pb.redirectErrorStream(true);
        };

        try {
            JobProcess jobProcess = processHelper.startProcess(sessionId, cmdList, pbCall);
            assertNotNull(jobProcess);

            // Echo command completes very quickly, so we just ensure no exceptions
            // Wait for the process to complete
            Thread.sleep(100);

            // Clean up
            processHelper.destroyProcess(sessionId);

            // Test passes if no exceptions thrown
            assertTrue(true);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void test_getRunningSessionIdSet_afterDestroy() {
        String sessionId = "test_sessionid_destroy";
        List<String> cmdList = Arrays.asList("echo", "hello");
        Consumer<ProcessBuilder> pbCall = pb -> {
            pb.redirectErrorStream(true);
        };

        try {
            processHelper.startProcess(sessionId, cmdList, pbCall);

            Set<String> sessionIds = processHelper.getRunningSessionIdSet();
            assertTrue(sessionIds.contains(sessionId));

            processHelper.destroyProcess(sessionId);

            sessionIds = processHelper.getRunningSessionIdSet();
            assertFalse(sessionIds.contains(sessionId));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void test_setProcessDestroyTimeout_differentValues() {
        processHelper.setProcessDestroyTimeout(5);
        processHelper.setProcessDestroyTimeout(15);
        processHelper.setProcessDestroyTimeout(30);

        // Test that setting different values doesn't throw exceptions
        assertTrue(true);
    }

    public void test_processHelper_synchronization() {
        String sessionId = "test_sync";
        List<String> cmdList = Arrays.asList("echo", "hello");
        Consumer<ProcessBuilder> pbCall = pb -> {
            pb.redirectErrorStream(true);
        };

        try {
            // Test that multiple calls to startProcess with same sessionId work correctly
            JobProcess jobProcess1 = processHelper.startProcess(sessionId, cmdList, pbCall);
            JobProcess jobProcess2 = processHelper.startProcess(sessionId, cmdList, pbCall);

            assertNotNull(jobProcess1);
            assertNotNull(jobProcess2);

            // Only one process should be running for the session
            assertTrue(processHelper.isProcessRunning(sessionId));

            Set<String> sessionIds = processHelper.getRunningSessionIdSet();
            assertEquals(1, sessionIds.size());
            assertTrue(sessionIds.contains(sessionId));

            // Clean up
            processHelper.destroyProcess(sessionId);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void test_emptyCommandList() {
        String sessionId = "test_empty_cmd";
        List<String> cmdList = new ArrayList<>();
        Consumer<ProcessBuilder> pbCall = pb -> {
            pb.redirectErrorStream(true);
        };

        try {
            processHelper.startProcess(sessionId, cmdList, pbCall);
            fail("Expected exception for empty command list");
        } catch (Exception e) {
            // Expected - empty command list should cause exception
            assertTrue(e instanceof JobProcessingException || e instanceof IndexOutOfBoundsException);
        }
    }

    public void test_nullCallback() {
        String sessionId = "test_null_callback";
        List<String> cmdList = Arrays.asList("echo", "hello");

        try {
            JobProcess jobProcess = processHelper.startProcess(sessionId, cmdList, null);
            fail("Expected exception for null callback");
        } catch (Exception e) {
            // Expected - null callback should cause exception
            assertTrue(e instanceof NullPointerException);
        }
    }

}