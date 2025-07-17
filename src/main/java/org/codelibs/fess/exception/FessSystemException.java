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
 * System exception class for the Fess search engine.
 * This exception is thrown when system-level errors occur in the Fess application,
 * such as configuration errors, initialization failures, or critical runtime issues.
 */
public class FessSystemException extends RuntimeException {

    /** Serial version UID for serialization compatibility */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new FessSystemException with the specified detail message and cause.
     *
     * @param message the detail message describing the exception
     * @param cause the cause of this exception
     */
    public FessSystemException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new FessSystemException with the specified detail message.
     *
     * @param message the detail message describing the exception
     */
    public FessSystemException(final String message) {
        super(message);
    }

    /**
     * Constructs a new FessSystemException with the specified cause.
     *
     * @param cause the cause of this exception
     */
    public FessSystemException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new FessSystemException with the specified detail message and suppression settings.
     *
     * @param message the detail message describing the exception
     * @param enableSuppression whether suppression is enabled or disabled
     * @param writableStackTrace whether the stack trace should be writable
     */
    protected FessSystemException(final String message, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, null, enableSuppression, writableStackTrace);
    }

}
