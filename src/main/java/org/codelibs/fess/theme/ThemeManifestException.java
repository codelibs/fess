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
package org.codelibs.fess.theme;

/**
 * Thrown when theme.yml fails parsing or validation.
 */
public class ThemeManifestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs an exception with the supplied diagnostic message.
     *
     * @param message human-readable description of the failure
     */
    public ThemeManifestException(final String message) {
        super(message);
    }

    /**
     * Constructs an exception with the supplied message and underlying cause.
     *
     * @param message human-readable description of the failure
     * @param cause original throwable that triggered the failure
     */
    public ThemeManifestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
