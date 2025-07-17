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
 * Exception thrown when GSA (Google Search Appliance) configuration errors occur.
 * This exception extends FessSystemException and is used to handle specific
 * configuration-related issues when working with GSA integration.
 *
 */
public class GsaConfigException extends FessSystemException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new GSA configuration exception with the specified detail message and cause.
     *
     * @param message the detail message describing the configuration error
     * @param cause the cause of the exception
     */
    public GsaConfigException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new GSA configuration exception with the specified detail message.
     *
     * @param message the detail message describing the configuration error
     */
    public GsaConfigException(final String message) {
        super(message);
    }

}
