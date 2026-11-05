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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;

/**
 * Parses and validates the theme settings (theme.settings): key=value lines that an
 * administrator passes to the active theme.
 *
 * <p>Fess does not interpret them. The theme defines the keys and what its values mean, so a value is
 * opaque data and a theme must never use it as HTML or as a navigation target. A line is kept only if
 * its key is letters, digits, underscore, dot or hyphen (at most 64 characters, starting with a letter
 * or digit, and not a name that is special in JavaScript objects), and its value has at most
 * {@link #MAX_VALUE_LENGTH} characters. A line with a control character is invalid. When a
 * key is repeated, the later line wins. At most {@link #MAX_ENTRIES} entries are kept.</p>
 *
 * <p>The same rules serve the admin save, which rejects a value with an invalid line, and the read side,
 * which drops invalid lines: a hand-edited system.properties or a backup import runs no setter and
 * so no save-time check.</p>
 */
public final class ThemeSettingsUtil {

    private static final Logger logger = LogManager.getLogger(ThemeSettingsUtil.class);

    /** The maximum number of entries kept. */
    public static final int MAX_ENTRIES = 100;

    /** The maximum length of a value. */
    public static final int MAX_VALUE_LENGTH = 1000;

    /** Separates the lines of a stored value. */
    public static final String LF = String.valueOf((char) 10);

    private static final Set<String> RESERVED_KEYS = Set.of("__proto__", "constructor", "prototype");

    private ThemeSettingsUtil() {
    }

    private static final char SEPARATOR = (char) 61;

    private static final Pattern KEY_PATTERN = Pattern.compile("[A-Za-z0-9][A-Za-z0-9_.-]{0,63}");

    /** Splits a value into lines; only CR and LF separate them, any other control character makes its line invalid. */
    private static final Pattern LINE_SEPARATOR = Pattern.compile("[\\x0D\\x0A]+");

    /**
     * The result of parsing a value.
     *
     * @param settings the valid entries in input order; for a repeated key the later line's value
     * @param invalid the number of non-blank lines that were not valid entries
     */
    public record Parsed(Map<String, String> settings, int invalid) {

        /**
         * Returns whether the value may be saved: every line is valid and there are at most {@link #MAX_ENTRIES} entries.
         *
         * @return true if the value may be saved
         */
        public boolean isAcceptable() {
            return invalid == 0 && settings.size() <= MAX_ENTRIES;
        }
    }

    /**
     * Parses a value, one key=value per line.
     *
     * @param value the value; null is treated as empty
     * @return the valid entries and the number of invalid lines
     */
    public static Parsed parse(final String value) {
        final Map<String, String> settings = new LinkedHashMap<>();
        int invalid = 0;
        if (value != null) {
            for (final String line : LINE_SEPARATOR.split(value)) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                if (!put(settings, line)) {
                    invalid++;
                }
            }
        }
        return new Parsed(settings, invalid);
    }

    /**
     * Returns the valid entries of a stored value, at most {@link #MAX_ENTRIES}, dropping invalid lines.
     *
     * @param value the stored value
     * @return the entries to serve, in input order; never null
     */
    public static Map<String, String> sanitize(final String value) {
        if (StringUtil.isBlank(value)) {
            return Map.of();
        }
        final Parsed parsed = parse(value);
        logIgnored(parsed.invalid());
        final Map<String, String> limited = new LinkedHashMap<>();
        for (final Map.Entry<String, String> entry : parsed.settings().entrySet()) {
            if (limited.size() >= MAX_ENTRIES) {
                break;
            }
            limited.put(entry.getKey(), entry.getValue());
        }
        return Collections.unmodifiableMap(limited);
    }

    private static void logIgnored(final int count) {
        if (count == 0) {
            return;
        }
        logger.warn("Ignored invalid theme settings: count={}", count);
    }

    /**
     * Returns whether the form value may be saved.
     *
     * @param value the form value
     * @return true if every line is valid and the number of entries is within the limit
     */
    public static boolean isAcceptable(final String value) {
        return parse(value).isAcceptable();
    }

    /**
     * Returns the value to store for the given form value: the valid entries as key=value lines,
     * or null for no entry so that the property is removed instead of stored empty.
     *
     * @param value the form value, already accepted by {@link #isAcceptable(String)}
     * @return the value to store, or null
     */
    public static String toStoredValue(final String value) {
        final Map<String, String> settings = parse(value).settings();
        return settings.isEmpty() ? null : toText(settings);
    }

    /**
     * Returns the settings as key=value lines.
     *
     * @param settings the settings
     * @return the lines joined by a line feed; empty for no setting
     */
    public static String toText(final Map<String, String> settings) {
        return settings.entrySet()
                .stream()
                .map(e -> e.getKey() + SEPARATOR + e.getValue())
                .collect(java.util.stream.Collectors.joining(LF));
    }

    private static boolean put(final Map<String, String> settings, final String line) {
        final int separator = line.indexOf(SEPARATOR);
        if (separator <= 0 || hasControl(line)) {
            return false;
        }
        final String key = line.substring(0, separator).trim();
        final String val = line.substring(separator + 1).trim();
        if (!KEY_PATTERN.matcher(key).matches() || RESERVED_KEYS.contains(key) || val.length() > MAX_VALUE_LENGTH) {
            return false;
        }
        settings.put(key, val);
        return true;
    }

    private static boolean hasControl(final String line) {
        return line.chars().anyMatch(Character::isISOControl);
    }
}
