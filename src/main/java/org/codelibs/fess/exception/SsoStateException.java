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
 * Exception thrown when an SSO callback does not match a login this server started.
 *
 * <p>Separate from {@link SsoLoginException} because the caller decides whether it happens: the
 * SSO endpoint is anonymous, so anyone can send a callback carrying a state this server never
 * issued, or replay one it has already consumed. That is a rejected request rather than a fault,
 * and logging a full stack trace for each one lets an unauthenticated client fill the log. A
 * genuine failure -- a token endpoint that will not answer, an unreachable directory, an
 * unparsable response -- stays an {@link SsoLoginException} and keeps its stack trace.
 */
public class SsoStateException extends SsoLoginException {

    /** Serial version UID for serialization compatibility. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new SsoStateException with the specified detail message.
     *
     * @param message The detail message explaining which check rejected the callback
     */
    public SsoStateException(final String message) {
        super(message);
    }

    /**
     * Constructs a new SsoStateException with the specified detail message and cause.
     *
     * @param message The detail message explaining which check rejected the callback
     * @param cause The underlying exception that caused this rejection
     */
    public SsoStateException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
