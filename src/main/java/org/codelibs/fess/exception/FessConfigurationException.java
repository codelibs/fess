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
 * Thrown when a setting that Fess needs is missing, empty or not usable, such as an empty
 * <code>app.cipher.key</code> or a password algorithm that is not configured.
 */
public class FessConfigurationException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message The detail message, which names the setting.
     */
    public FessConfigurationException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message The detail message, which names the setting.
     * @param cause The cause of this exception.
     */
    public FessConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
