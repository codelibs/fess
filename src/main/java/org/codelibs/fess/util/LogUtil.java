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
package org.codelibs.fess.util;

import java.util.regex.Pattern;

/**
 * Helper for writing values that a client supplied into the application log.
 *
 * <p>The application log pattern is one event per line ({@code %msg%n}), so a line break inside a
 * logged value ends the event and lets the client start one of its own.</p>
 */
public final class LogUtil {

    /** The longest value logged as is; a longer one is cut and marked. */
    protected static final int MAX_LOGGED_LENGTH = 100;

    /** Control characters, and the Unicode line and paragraph separators that {@code \p{Cntrl}} does not cover. */
    private static final Pattern UNSAFE_PATTERN = Pattern.compile("[\\p{Cntrl}\\u0085\\u2028\\u2029]");

    private LogUtil() {
    }

    /**
     * Makes a client-supplied value safe to embed in a log message: its control characters are replaced by
     * {@code ?} and a value longer than {@value #MAX_LOGGED_LENGTH} characters is cut.
     *
     * @param value the client-supplied value (may be null)
     * @return the value to log, or null if the value is null
     */
    public static String sanitize(final String value) {
        if (value == null) {
            return null;
        }
        final String bounded = value.length() > MAX_LOGGED_LENGTH ? value.substring(0, MAX_LOGGED_LENGTH) + "..." : value;
        return UNSAFE_PATTERN.matcher(bounded).replaceAll("?");
    }
}
