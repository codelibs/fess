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
package org.codelibs.fess.llm;

import org.codelibs.fess.exception.FessSystemException;

/**
 * Exception thrown when an error occurs during LLM operations.
 *
 * @author FessProject
 */
public class LlmException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    /** Error code for rate limit (HTTP 429). */
    public static final String ERROR_RATE_LIMIT = "rate_limit";

    /** Error code for authentication failure (HTTP 401/403). */
    public static final String ERROR_AUTH = "auth_error";

    /** Error code for service unavailable (HTTP 502/503). */
    public static final String ERROR_SERVICE_UNAVAILABLE = "service_unavailable";

    /** Error code for unknown errors. */
    public static final String ERROR_UNKNOWN = "unknown";

    /** The error code indicating the type of LLM error. */
    private final String errorCode;

    /**
     * Creates a new exception with the specified message.
     *
     * @param message the error message
     */
    public LlmException(final String message) {
        super(message);
        errorCode = ERROR_UNKNOWN;
    }

    /**
     * Creates a new exception with the specified message and cause.
     *
     * @param message the error message
     * @param cause the cause of the exception
     */
    public LlmException(final String message, final Throwable cause) {
        super(message, cause);
        errorCode = ERROR_UNKNOWN;
    }

    /**
     * Creates a new exception with the specified message and error code.
     *
     * @param message the error message
     * @param errorCode the error code
     */
    public LlmException(final String message, final String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Creates a new exception with the specified message, error code and cause.
     *
     * @param message the error message
     * @param errorCode the error code
     * @param cause the cause of the exception
     */
    public LlmException(final String message, final String errorCode, final Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Gets the error code.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
}
