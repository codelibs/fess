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
 * Exception thrown when the search engine refuses a search for lack of capacity, for example a
 * tripped circuit breaker (HTTP 429) or shards that cannot serve it yet (HTTP 503).
 *
 * <p>Unlike {@link InvalidQueryException} this says nothing about the query: the same search can
 * succeed a moment later. It is therefore neither retried with an escaped query nor reported to
 * the caller as a bad request.</p>
 */
public class SearchEngineUnavailableException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new SearchEngineUnavailableException.
     *
     * @param message the detail message
     * @param cause the failure the search engine answered with
     */
    public SearchEngineUnavailableException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
