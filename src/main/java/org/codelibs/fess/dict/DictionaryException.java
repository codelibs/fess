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
package org.codelibs.fess.dict;

import org.codelibs.fess.exception.FessSystemException;

/**
 * Exception thrown when dictionary operations encounter errors.
 * This exception is typically used for issues during dictionary file
 * reading, writing, parsing, or other dictionary-related operations.
 */
public class DictionaryException extends FessSystemException {

    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new DictionaryException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the underlying cause of the exception
     */
    public DictionaryException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new DictionaryException with the specified message.
     *
     * @param message the detail message
     */
    public DictionaryException(final String message) {
        super(message);
    }

}
