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
 * Exception thrown when there are issues with search query processing.
 *
 * This exception is typically thrown when query parsing, serialization,
 * or processing fails during search operations. It extends FessSystemException
 * to provide specific handling for search query-related errors.
 */
public class SearchQueryException extends FessSystemException {

    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new SearchQueryException with the specified detail message and cause.
     *
     * @param message The detail message explaining the exception
     * @param cause The cause of this exception
     */
    public SearchQueryException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new SearchQueryException with the specified detail message.
     *
     * @param message The detail message explaining the exception
     */
    public SearchQueryException(final String message) {
        super(message);
    }

    /**
     * Constructs a new SearchQueryException with the specified cause.
     *
     * @param cause The cause of this exception
     */
    public SearchQueryException(final Throwable cause) {
        super(cause);
    }

}
