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
 * Exception thrown when an invalid access token is encountered.
 * This exception is typically used in authentication and authorization contexts
 * where a provided access token is invalid, expired, or malformed.
 */
public class InvalidAccessTokenException extends FessSystemException {

    /** Serial version UID for serialization */
    private static final long serialVersionUID = 1L;

    /** Type of the invalid access token */
    private final String type;

    /**
     * Creates a new InvalidAccessTokenException with the specified type and message.
     *
     * @param type the type of the invalid access token
     * @param message the detailed error message
     */
    public InvalidAccessTokenException(final String type, final String message) {
        super(message);
        this.type = type;
    }

    /**
     * Returns the type of the invalid access token.
     *
     * @return the type of the invalid access token
     */
    public String getType() {
        return type;
    }

}
