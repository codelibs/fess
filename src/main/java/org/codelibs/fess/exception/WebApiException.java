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
 * Exception thrown when web API operations encounter errors.
 * This exception includes an HTTP status code to indicate the nature of the error.
 */
public class WebApiException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    /**
     * The HTTP status code associated with this exception.
     */
    private final int statusCode;

    /**
     * Gets the HTTP status code associated with this exception.
     *
     * @return The HTTP status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Constructs a WebApiException with the specified status code, message, and cause.
     *
     * @param statusCode The HTTP status code
     * @param message The detail message
     * @param cause The cause of this exception
     */
    public WebApiException(final int statusCode, final String message, final Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /**
     * Constructs a WebApiException with the specified status code and message.
     *
     * @param statusCode The HTTP status code
     * @param message The detail message
     */
    public WebApiException(final int statusCode, final String message) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Constructs a WebApiException with the specified status code and exception.
     *
     * @param statusCode The HTTP status code
     * @param e The exception that caused this WebApiException
     */
    public WebApiException(final int statusCode, final Exception e) {
        this(statusCode, e.getMessage(), e);
    }
}
