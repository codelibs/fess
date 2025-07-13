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

/**
 * Exception thrown when a dictionary has expired and is no longer valid.
 * This runtime exception indicates that a dictionary file or dictionary data
 * has exceeded its lifetime and should be refreshed or reloaded.
 */
public class DictionaryExpiredException extends RuntimeException {

    /** Serial version UID for serialization. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of DictionaryExpiredException.
     * This constructor initializes the exception to indicate that a dictionary
     * has expired and is no longer valid for use.
     */
    public DictionaryExpiredException() {
        // Default constructor with explicit documentation
    }

    //    public DictionaryExpiredException() {
    //        super("errors.expired_dict_id");
    //    }
}
