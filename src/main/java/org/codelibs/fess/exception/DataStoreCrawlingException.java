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

import org.codelibs.fess.crawler.exception.CrawlingAccessException;

/**
 * Exception thrown when an error occurs during data store crawling operations.
 * This exception provides information about the URL where the error occurred
 * and whether the crawling process should be aborted.
 */
public class DataStoreCrawlingException extends CrawlingAccessException {

    private static final long serialVersionUID = 1L;

    /**
     * The URL where the crawling error occurred.
     */
    private final String url;

    /**
     * Flag indicating whether the crawling process should be aborted.
     */
    private final boolean abort;

    /**
     * Creates a new DataStoreCrawlingException with the specified URL, message, and cause.
     * The abort flag is set to false by default.
     *
     * @param url the URL where the crawling error occurred
     * @param message the error message
     * @param cause the underlying exception that caused this error
     */
    public DataStoreCrawlingException(final String url, final String message, final Throwable cause) {
        this(url, message, cause, false);
    }

    /**
     * Creates a new DataStoreCrawlingException with the specified URL, message, cause, and abort flag.
     *
     * @param url the URL where the crawling error occurred
     * @param message the error message
     * @param cause the underlying exception that caused this error
     * @param abort whether the crawling process should be aborted due to this error
     */
    public DataStoreCrawlingException(final String url, final String message, final Throwable cause, final boolean abort) {
        super(message, cause);
        this.url = url;
        this.abort = abort;
    }

    /**
     * Gets the URL where the crawling error occurred.
     *
     * @return the URL associated with this exception
     */
    public String getUrl() {
        return url;
    }

    /**
     * Checks whether the crawling process should be aborted due to this exception.
     *
     * @return true if the crawling should be aborted, false otherwise
     */
    public boolean aborted() {
        return abort;
    }
}
