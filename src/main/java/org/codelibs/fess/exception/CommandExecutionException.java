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
package org.codelibs.fess.exception;

/**
 * Exception thrown when command execution fails.
 * This exception indicates that an external command or process execution encountered an error.
 */
public class CommandExecutionException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor with error message.
     * @param message The error message describing the command execution failure.
     */
    public CommandExecutionException(final String message) {
        super(message);
    }

    /**
     * Constructor with error message and cause.
     * @param message The error message describing the command execution failure.
     * @param e The cause of the exception.
     */
    public CommandExecutionException(final String message, final Throwable e) {
        super(message, e);
    }

}
