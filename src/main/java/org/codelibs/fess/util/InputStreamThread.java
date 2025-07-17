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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A thread that reads from an input stream line by line and maintains a buffer of recent lines.
 * This class provides functionality to read input stream data asynchronously,
 * optionally process each line with a callback function, and maintain a circular buffer
 * of recent lines for retrieval.
 */
public class InputStreamThread extends Thread {
    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(InputStreamThread.class);

    /** Buffered reader for reading from the input stream */
    private final BufferedReader br;

    /** Maximum buffer size constant */
    public static final int MAX_BUFFER_SIZE = 1000;

    /** List storing recent lines from the input stream */
    private final List<String> list = new LinkedList<>();

    /** Maximum number of lines to keep in the buffer */
    private final int bufferSize;

    /** Callback function to process each line as it's read */
    private final Consumer<String> outputCallback;

    /**
     * Creates a new input stream thread.
     *
     * @param is the input stream to read from
     * @param charset the character encoding to use for reading
     * @param bufferSize the maximum number of lines to keep in the buffer (0 to disable buffering)
     * @param outputCallback optional callback function to process each line (can be null)
     */
    public InputStreamThread(final InputStream is, final Charset charset, final int bufferSize, final Consumer<String> outputCallback) {
        super("InputStreamThread");
        this.bufferSize = bufferSize;
        this.outputCallback = outputCallback;

        br = new BufferedReader(new InputStreamReader(is, charset));
    }

    /**
     * Runs the thread to continuously read lines from the input stream.
     * Each line is processed by the output callback (if provided) and added to the buffer.
     * The buffer is maintained as a circular buffer with the specified size.
     */
    @Override
    public void run() {
        boolean running = true;
        while (running) {
            try {
                final String line = br.readLine();
                if (line == null) {
                    running = false;
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug(line);
                    }
                    if (bufferSize > 0) {
                        list.add(line);
                    }
                    if (outputCallback != null) {
                        outputCallback.accept(line);
                    }
                    if (list.size() > bufferSize) {
                        list.remove(0);
                    }
                }
            } catch (final Exception e) {
                running = false;
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to process an input stream.", e);
                }
            }
        }
    }

    /**
     * Returns all buffered lines as a single string, separated by newlines.
     *
     * @return the concatenated output of all buffered lines
     */
    public String getOutput() {
        final StringBuilder buf = new StringBuilder(100);
        for (final String value : list) {
            buf.append(value).append("\n");
        }
        return buf.toString();
    }

    /**
     * Checks if the buffer contains a line that matches the specified value (after trimming).
     *
     * @param value the value to search for in the buffered lines
     * @return true if a matching line is found, false otherwise
     */
    public boolean contains(final String value) {
        for (final String line : list) {
            if (line.trim().equals(value)) {
                return true;
            }
        }
        return false;
    }
}