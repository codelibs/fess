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
 * Exception thrown during SSO (Single Sign-On) processing operations.
 *
 * This exception is used to indicate errors that occur during the execution
 * of SSO authentication and authorization processes. It extends FessSystemException
 * to provide consistent error handling within the Fess system for SSO-related
 * processing failures such as token validation errors, communication failures
 * with SSO providers, or configuration issues.
 */
public class SsoProcessException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new SSO process exception with the specified detailed message.
     *
     * @param message The detailed error message explaining the cause of the exception
     */
    public SsoProcessException(final String message) {
        super(message);
    }

    /**
     * Constructs a new SSO process exception with the specified detailed message and cause.
     *
     * @param message The detailed error message explaining the cause of the exception
     * @param cause The underlying exception that caused this SSO process exception
     */
    public SsoProcessException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
