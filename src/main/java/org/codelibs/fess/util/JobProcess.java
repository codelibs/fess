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

import java.util.function.Consumer;

import org.codelibs.fess.Constants;

public class JobProcess {
    protected Process process;

    protected InputStreamThread inputStreamThread;

    public JobProcess(final Process process) {
        this(process, InputStreamThread.MAX_BUFFER_SIZE, null);
    }

    public JobProcess(final Process process, final int bufferSize, final Consumer<String> outputCallback) {
        this.process = process;
        inputStreamThread = new InputStreamThread(process.getInputStream(), Constants.CHARSET_UTF_8, bufferSize, outputCallback);
    }

    public Process getProcess() {
        return process;
    }

    public InputStreamThread getInputStreamThread() {
        return inputStreamThread;
    }

}
