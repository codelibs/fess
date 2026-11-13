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
package org.codelibs.fess.app.service;

/**
 * Thrown when a passkey registration or login fails verification. The message is a
 * {@code key=value} reason for the log only and must never reach the client, so that a failed
 * passkey login looks like any other failed login. It carries no cause, since the messages of the
 * underlying parser and verifier may quote parts of the client data.
 */
public class PasskeyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param message the reason, for the log only
     */
    public PasskeyException(final String message) {
        super(message);
    }
}
