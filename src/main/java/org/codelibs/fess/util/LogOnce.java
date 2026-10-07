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

import org.apache.logging.log4j.Logger;

/**
 * Writes a WARN line once per key, for a condition that is hit on every request but needs to be
 * said only once.
 *
 * <p>The typical case is an anonymous endpoint whose backing plugin is not installed: the same
 * miss recurs on every visit, so a line per request is a log an unauthenticated client can fill,
 * while no line at all leaves the operator with a bare 404 and nothing to explain it.</p>
 *
 * <p>Each caller owns its own instance, so there is no static state and two callers never share
 * keys. The memory of what was reported lasts for the life of the JVM.</p>
 *
 * <p>The keys must come from a small fixed set, such as constants or plugin and type names, and
 * never from request data: the set only grows, so a key taken from the request lets a client
 * grow it without bound.</p>
 *
 * <p>The level is WARN and only WARN. ERROR is an operator-notification trigger in Fess, and the
 * conditions reported here are an installation that is not finished, not a fault at run time.</p>
 */
public class LogOnce {

    private final Set<String> reported = ConcurrentHashMap.newKeySet();

    /**
     * Default constructor.
     */
    public LogOnce() {
    }

    /**
     * Logs {@code message} at WARN the first time {@code key} is seen by this instance.
     *
     * <p>WARN being enabled is checked first, so a call made while it is off does not use up the
     * key: the log level can change at runtime, and the condition should still be reported once
     * WARN is switched on.</p>
     *
     * @param logger the logger to write to
     * @param key the identity of the condition, from a small fixed set and never request data
     * @param message the log4j message pattern
     * @param params the message parameters
     * @return true if the line was written, false if WARN is disabled or the key was reported before
     */
    public boolean warn(final Logger logger, final String key, final String message, final Object... params) {
        if (logger.isWarnEnabled() && reported.add(key)) {
            logger.warn(message, params);
            return true;
        }
        return false;
    }
}
