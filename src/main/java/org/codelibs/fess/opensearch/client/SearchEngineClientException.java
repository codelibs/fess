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
package org.codelibs.fess.opensearch.client;

import org.codelibs.fess.exception.FessSystemException;

/**
 * Exception thrown when search engine client operations fail.
 * This exception wraps underlying search engine errors and provides
 * meaningful error messages for troubleshooting.
 */
public class SearchEngineClientException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new SearchEngineClientException with the specified message and cause.
     *
     * @param message the detail message explaining the exception
     * @param cause   the underlying cause of the exception
     */
    public SearchEngineClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new SearchEngineClientException with the specified message.
     *
     * @param message the detail message explaining the exception
     */
    public SearchEngineClientException(final String message) {
        super(message);
    }

}
