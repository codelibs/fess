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
 * Exception thrown when SSO (Single Sign-On) login operations fail.
 *
 * This exception is used to indicate various SSO authentication failures
 * including configuration errors, authentication token validation failures,
 * communication issues with SSO providers, and other SSO-related problems.
 */
public class SsoLoginException extends FessSystemException {

    /** Serial version UID for serialization compatibility. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new SsoLoginException with the specified detail message.
     *
     * @param message The detail message explaining the SSO login failure
     */
    public SsoLoginException(final String message) {
        super(message);
    }

    /**
     * Constructs a new SsoLoginException with the specified detail message and cause.
     *
     * @param message The detail message explaining the SSO login failure
     * @param cause The underlying exception that caused this SSO login failure
     */
    public SsoLoginException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
