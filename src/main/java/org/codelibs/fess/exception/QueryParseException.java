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

import org.apache.lucene.queryparser.classic.ParseException;

/**
 * Exception thrown when a query parsing error occurs.
 * This exception wraps Lucene's ParseException to provide consistent error handling
 * within the Fess search framework.
 *
 */
public class QueryParseException extends FessSystemException {

    /** Serial version UID for serialization compatibility */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new QueryParseException with the specified ParseException as the cause.
     *
     * @param cause the ParseException that caused this exception
     */
    public QueryParseException(final ParseException cause) {
        super(cause);
    }

}
