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
 *
 * <p>Each instance carries a stable {@link Code structured code} so that
 * callers (notably the admin theme action) can dispatch to a localized
 * message instead of surfacing the raw English diagnostic to end users.</p>
 */
public class ThemeManifestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Structured error codes for theme.yml failures.
     *
     * <p>Codes form a stable contract with the admin UI and the i18n
     * message catalog (see {@code errors.theme_manifest_*} keys). When
     * adding a new code, also add a corresponding {@code addErrorsTheme*}
     * helper and message in {@code fess_message*.properties}.</p>
     */
    public enum Code {
        /** SnakeYAML failed to parse {@code theme.yml}. */
        PARSE_FAILED,
        /** {@code theme.yml} contained no document (empty file or only comments). */
        EMPTY,
        /** {@code theme.yml} root node is not a YAML mapping. */
        NOT_MAPPING,
        /** A field value exceeds the maximum allowed length. */
        FIELD_TOO_LONG,
        /** {@code apiVersion} is missing or does not match the expected constant. */
        UNSUPPORTED_API_VERSION,
        /** {@code kind} is missing or does not match the expected constant. */
        UNSUPPORTED_KIND,
        /** {@code name} is missing, blank, or fails the name regex. */
        INVALID_NAME,
        /** {@code displayName} is missing or blank. */
        DISPLAY_NAME_REQUIRED,
        /** {@code version} is missing or not a supported SemVer. */
        INVALID_VERSION,
        /** {@code entry} is absolute, contains traversal, or otherwise unsafe. */
        UNSAFE_ENTRY,
        /** Fallback for failures that do not fit any other category. */
        OTHER
    }

    private final Code code;

    /**
     * Constructs an exception with the supplied diagnostic message and
     * {@link Code#OTHER OTHER} as the structured code.
     *
     * @param message human-readable description of the failure
     */
    public ThemeManifestException(final String message) {
        super(message);
        this.code = Code.OTHER;
    }

    /**
     * Constructs an exception with the supplied message and underlying cause,
     * defaulting the structured code to {@link Code#OTHER OTHER}.
     *
     * @param message human-readable description of the failure
     * @param cause original throwable that triggered the failure
     */
    public ThemeManifestException(final String message, final Throwable cause) {
        super(message, cause);
        this.code = Code.OTHER;
    }

    /**
     * Constructs an exception with the supplied structured code and message.
     *
     * @param code structured error code (non-{@code null})
     * @param message human-readable description of the failure
     */
    public ThemeManifestException(final Code code, final String message) {
        super(message);
        this.code = code == null ? Code.OTHER : code;
    }

    /**
     * Constructs an exception with the supplied structured code, message, and cause.
     *
     * @param code structured error code (non-{@code null})
     * @param message human-readable description of the failure
     * @param cause original throwable that triggered the failure
     */
    public ThemeManifestException(final Code code, final String message, final Throwable cause) {
        super(message, cause);
        this.code = code == null ? Code.OTHER : code;
    }

    /**
     * Returns the structured error code associated with this exception.
     *
     * @return the structured error code (never {@code null})
     */
    public Code code() {
        return code;
    }
}
