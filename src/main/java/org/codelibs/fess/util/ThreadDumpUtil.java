/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.fess.Constants;

public class ThreadDumpUtil {
    private static final Logger logger = LogManager.getLogger(ThreadDumpUtil.class);

    protected ThreadDumpUtil() {
        // noop
    }

    public static void printThreadDump() {
        processThreadDump(logger::info);
    }

    public static void printThreadDumpAsWarn() {
        processThreadDump(logger::warn);
    }

    public static void printThreadDumpAsError() {
        processThreadDump(logger::error);
    }

    public static void writeThreadDump(final String file) {
        try (final Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Constants.CHARSET_UTF_8))) {
            processThreadDump(s -> {
                try {
                    writer.write(s);
                    writer.write('\n');
                } catch (final IOException e) {
                    throw new IORuntimeException(e);
                }
            });
        } catch (final Exception e) {
            logger.warn("Failed to write a thread dump to {}", file, e);
        }
    }

    public static void processThreadDump(final Consumer<String> writer) {
        for (final Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            writer.accept("Thread: " + entry.getKey());
            final StackTraceElement[] trace = entry.getValue();
            for (final StackTraceElement element : trace) {
                writer.accept("\tat " + element);
            }
        }
    }
}
