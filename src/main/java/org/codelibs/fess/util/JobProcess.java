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

import java.util.function.Consumer;

import org.codelibs.fess.Constants;

/**
 * A wrapper class that manages a system process for job execution.
 * This class provides access to the underlying process and manages
 * the input stream thread for capturing process output.
 */
public class JobProcess {
    /**
     * The underlying system process.
     */
    protected Process process;

    /**
     * The thread that handles reading from the process input stream.
     */
    protected InputStreamThread inputStreamThread;

    /**
     * Constructs a new JobProcess with the specified process.
     * Uses the default buffer size and no output callback.
     *
     * @param process the system process to wrap
     */
    public JobProcess(final Process process) {
        this(process, InputStreamThread.MAX_BUFFER_SIZE, null);
    }

    /**
     * Constructs a new JobProcess with the specified process, buffer size, and output callback.
     *
     * @param process the system process to wrap
     * @param bufferSize the buffer size for reading process output
     * @param outputCallback the callback function to handle process output lines
     */
    public JobProcess(final Process process, final int bufferSize, final Consumer<String> outputCallback) {
        this.process = process;
        inputStreamThread = new InputStreamThread(process.getInputStream(), Constants.CHARSET_UTF_8, bufferSize, outputCallback);
    }

    /**
     * Returns the underlying system process.
     *
     * @return the wrapped process
     */
    public Process getProcess() {
        return process;
    }

    /**
     * Returns the input stream thread that handles process output.
     *
     * @return the input stream thread
     */
    public InputStreamThread getInputStreamThread() {
        return inputStreamThread;
    }

}
