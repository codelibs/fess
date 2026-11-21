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
package org.codelibs.fess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FessBootAbortTest {

    private final List<LogRecord> records = new ArrayList<>();

    private final Handler handler = new Handler() {
        @Override
        public void publish(final LogRecord record) {
            records.add(record);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    };

    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private PrintStream originalErr;

    private Logger logger;

    /** A boot that records the exit status instead of terminating the JVM. */
    private static class RecordingBoot extends FessBoot {
        int status = -1;

        RecordingBoot() {
            super(0, "");
        }

        @Override
        protected void exit(final int status) {
            this.status = status;
        }
    }

    @BeforeEach
    public void setUp() {
        logger = Logger.getLogger(FessBoot.class.getName());
        logger.addHandler(handler);
        originalErr = System.err;
        System.setErr(new PrintStream(err, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void tearDown() {
        System.setErr(originalErr);
        logger.removeHandler(handler);
    }

    @Test
    public void test_abortStartup_reportsTheReasonOnStderrAndAtSevere() {
        final RecordingBoot boot = new RecordingBoot();

        boot.abortStartup(List.of("/"));

        final String stderr = err.toString(StandardCharsets.UTF_8);
        assertTrue(stderr.contains("Fess aborted the startup because the web application [/] failed to initialize."), stderr);
        assertEquals(1, records.size());
        assertEquals(Level.SEVERE, records.get(0).getLevel());
        assertTrue(stderr.contains(records.get(0).getMessage()), stderr);
        assertEquals(1, boot.status);
    }

    @Test
    public void test_abortStartupOnPorts_reportsTheReasonOnStderrAndAtSevere() {
        final RecordingBoot boot = new RecordingBoot();

        boot.abortStartupOnPorts(List.of(8080));

        final String stderr = err.toString(StandardCharsets.UTF_8);
        assertTrue(stderr.contains("Fess aborted the startup because the port [8080] could not be opened."), stderr);
        assertEquals(1, records.size());
        assertEquals(Level.SEVERE, records.get(0).getLevel());
        assertTrue(stderr.contains(records.get(0).getMessage()), stderr);
        assertEquals(1, boot.status);
    }
}
