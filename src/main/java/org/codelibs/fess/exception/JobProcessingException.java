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
 * Exception thrown when an error occurs during job processing.
 * This exception is used to indicate problems that arise during
 * the execution or processing of jobs in the system.
 */
public class JobProcessingException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new JobProcessingException with the specified cause.
     *
     * @param e the cause of the exception
     */
    public JobProcessingException(final Throwable e) {
        super(e);
    }

    /**
     * Constructs a new JobProcessingException with the specified detail message and cause.
     *
     * @param message the detail message explaining the exception
     * @param e the cause of the exception
     */
    public JobProcessingException(final String message, final Throwable e) {
        super(message, e);
    }

    /**
     * Constructs a new JobProcessingException with the specified detail message.
     *
     * @param message the detail message explaining the exception
     */
    public JobProcessingException(final String message) {
        super(message);
    }

}
