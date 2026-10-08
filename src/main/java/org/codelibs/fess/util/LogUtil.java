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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;

/**
 * Helper for writing values that a client supplied into the application log, and for writing a
 * condition that recurs on every request only once.
 *
 * <p>The application log pattern is one event per line ({@code %msg%n}), so a line break inside a
 * logged value ends the event and lets the client start one of its own.</p>
 */
public final class LogUtil {

    /** The longest value logged as is; a longer one is cut and marked. */
    protected static final int MAX_LOGGED_LENGTH = 100;

    /** Control characters, and the Unicode line and paragraph separators that {@code \p{Cntrl}} does not cover. */
    private static final Pattern UNSAFE_PATTERN = Pattern.compile("[\\p{Cntrl}\\u0085\\u2028\\u2029]");

    /** The {@code logger name:key} pairs already reported by {@link #warnOnce}. */
    private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();

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

    /**
     * Writes {@code message} at WARN the first time {@code key} is seen for {@code logger}, for a
     * condition that is hit on every request but needs to be said only once.
     *
     * <p>The typical case is an anonymous endpoint whose backing plugin is not installed: the same
     * miss recurs on every visit, so a line per request is a log an unauthenticated client can
     * fill, while no line at all leaves the operator with a bare 404 and nothing to explain it.</p>
     *
     * <p>What was reported is remembered for the life of the JVM and shared by every caller, so
     * the key is scoped by the logger name: two classes can use the same key without silencing
     * each other. A webapp redeploy reloads this class and starts over. There is no way to
     * report a key again, so a condition that recovers and comes back is not reported twice.</p>
     *
     * <p>The keys must come from a small fixed set, such as constants, plugin and type names, or
     * a configured value, and never from request data: the set only grows, so a key taken from
     * the request lets a client grow it without bound.</p>
     *
     * <p>The level is WARN and only WARN. ERROR is an operator-notification trigger in Fess, and
     * the conditions reported this way are an installation that is not finished or a setting that
     * is wrong, not a fault at run time. WARN being enabled is checked first, so a call made
     * while it is off does not use up the key: the log level can change at runtime, and the
     * condition should still be reported once WARN is switched on.</p>
     *
     * <p>As with {@link Logger#warn(String, Object...)}, a {@link Throwable} given as the last
     * of {@code params} after all the placeholders are filled is logged as the exception.</p>
     *
     * @param logger the logger to write to
     * @param key the identity of the condition within the logger, from a small fixed set and never request data
     * @param message the log4j message pattern
     * @param params the message parameters
     * @return true if the line was written, false if WARN is disabled or the key was reported before
     */
    public static boolean warnOnce(final Logger logger, final String key, final String message, final Object... params) {
        if (logger.isWarnEnabled() && WARNED.add(logger.getName() + ":" + key)) {
            logger.warn(message, params);
            return true;
        }
        return false;
    }

    /**
     * Forgets everything {@link #warnOnce} has reported, so that a test can start from a clean
     * state. Production code must not call this: a reported condition is meant to stay reported.
     */
    public static void resetWarnOnce() {
        WARNED.clear();
    }
}
