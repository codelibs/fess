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
package org.codelibs.fess.taglib;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.util.DateConverter;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.LastaWebKey;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaResponseUtil;
import org.lastaflute.web.util.LaServletContextUtil;
import org.codelibs.fesen.opensearch.common.joda.Joda;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility class providing static functions for Fess JSP/JSTL expressions and tag libraries.
 * This class contains various helper methods for formatting, parsing, and manipulating data
 * in Fess web templates, including date formatting, localization, file operations, and
 * query parameter handling.
 *
 * @since 1.0
 */
public class FessFunctions {
    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(FessFunctions.class);

    /** Format identifier for PDF date parsing */
    private static final String PDF_DATE = "pdf_date";

    /**
     * Cache for storing resource file modification timestamps to enable cache busting.
     * The cache expires after 10 minutes and has a maximum size of 1000 entries.
     */
    private static LoadingCache<String, Long> resourceHashCache =
            CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(10, TimeUnit.MINUTES).build(new CacheLoader<String, Long>() {
                @Override
                public Long load(final String key) throws Exception {
                    try {
                        final Path path = Paths.get(LaServletContextUtil.getServletContext().getRealPath(key));
                        if (Files.isRegularFile(path)) {
                            return Files.getLastModifiedTime(path).toMillis();
                        }
                    } catch (final Exception e) {
                        logger.debug("Failed to access resource file: path={}", key, e);
                    }
                    return 0L;
                }
            });

    /**
     * Private constructor to prevent instantiation of this utility class.
     * This class is intended to be used only through its static methods.
     */
    protected FessFunctions() {
        // nothing
    }

    /**
     * Generates an HTML opening or closing tag with appropriate language attribute.
     *
     * @param isOpen true to generate opening HTML tag, false for closing tag
     * @return HTML opening tag with language attribute or closing tag
     */
    public static String html(final boolean isOpen) {
        if (isOpen) {
            return "<html lang=\"" + LaRequestUtil.getOptionalRequest().map(req -> {
                if (req.getAttribute(LastaWebKey.USER_LOCALE_KEY) instanceof final Locale locale) {
                    return locale;
                }
                return Locale.ENGLISH;
            }).orElse(Locale.ENGLISH).getLanguage() + "\">";
        }
        return "</html>";
    }

    /**
     * Converts a Long timestamp to a Date object.
     *
     * @param value the timestamp in milliseconds
     * @return Date object representing the timestamp, or null if value is null
     */
    public static Date date(final Long value) {
        if (value == null) {
            return null;
        }
        return new Date(value);
    }

    /**
     * Parses a date string using the default date format.
     *
     * @param value the date string to parse
     * @return parsed Date object, or null if parsing fails
     */
    public static Date parseDate(final String value) {
        return parseDate(value, Constants.DATE_OPTIONAL_TIME);
    }

    /**
     * Parses a date string using the specified format.
     *
     * @param value the date string to parse
     * @param format the date format pattern or "pdf_date" for PDF date format
     * @return parsed Date object, or null if parsing fails
     */
    public static Date parseDate(final String value, final String format) {
        if (value == null) {
            return null;
        }

        try {
            if (PDF_DATE.equals(format)) {
                final Calendar cal = DateConverter.toCalendar(value);
                return cal != null ? cal.getTime() : null;
            }

            final long time = Joda.forPattern(format).parseMillis(value);
            return new Date(time);
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * Formats a Date object to ISO datetime string format in UTC timezone.
     *
     * @param date the date to format
     * @return formatted date string, or empty string if date is null
     */
    public static String formatDate(final Date date) {
        if (date == null) {
            return StringUtil.EMPTY;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(Constants.ISO_DATETIME_FORMAT);
        sdf.setTimeZone(Constants.TIMEZONE_UTC);
        return sdf.format(date);
    }

    /**
     * Formats a LocalDateTime object to ISO datetime string format.
     *
     * @param date the LocalDateTime to format
     * @return formatted date string, or empty string if date is null
     */
    public static String formatDate(final LocalDateTime date) {
        if (date == null) {
            return StringUtil.EMPTY;
        }
        return date.format(DateTimeFormatter.ofPattern(Constants.ISO_DATETIME_FORMAT, Locale.ROOT));
    }

    /**
     * Formats a ZonedDateTime object using the specified format pattern.
     *
     * @param date the ZonedDateTime to format
     * @param format the date format pattern
     * @return formatted date string, or empty string if date is null
     */
    public static String formatDate(final ZonedDateTime date, final String format) {
        if (date == null) {
            return StringUtil.EMPTY;
        }
        return date.format(DateTimeFormatter.ofPattern(format, Locale.ROOT));
    }

    /**
     * Encodes a string to URL-safe Base64 format.
     *
     * @param value the string to encode
     * @return Base64 encoded string, or empty string if value is null
     */
    public static String base64(final String value) {
        if (value == null) {
            return StringUtil.EMPTY;
        }
        return Base64.getUrlEncoder().encodeToString(value.getBytes(Constants.CHARSET_UTF_8));
    }

    /**
     * Checks if a file exists at the specified path within the servlet context.
     *
     * @param path the file path relative to the servlet context
     * @return true if the file exists, false otherwise
     */
    public static boolean fileExists(final String path) {
        final File file = new File(LaServletContextUtil.getServletContext().getRealPath(path));
        return file.exists();
    }

    /**
     * Generates a complete URL with context path and cache-busting timestamp.
     *
     * @param input the relative URL path starting with '/'
     * @return complete URL with context path and optional timestamp parameter
     * @throws IllegalArgumentException if input is null or doesn't start with '/'
     */
    public static String url(final String input) {
        if (input == null) {
            final String msg = "The argument 'input' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        if (!input.startsWith("/")) {
            final String msg = "The argument 'input' should start with slash '/': " + input;
            throw new IllegalArgumentException(msg);
        }
        final StringBuilder sb = new StringBuilder();
        LaRequestUtil.getOptionalRequest().map(HttpServletRequest::getContextPath).filter(s -> s.length() > 1).ifPresent(s -> sb.append(s));
        sb.append(input);
        if (input.indexOf('?') == -1) {
            try {
                final Long value = resourceHashCache.get(input);
                if (value.longValue() > 0) {
                    sb.append("?t=").append(value.toString());
                }
            } catch (final ExecutionException e) {
                logger.debug("Failed to access resource hash cache: path={}", input, e);
            }
        }
        return LaResponseUtil.getResponse().encodeURL(sb.toString());
    }

    /**
     * Escapes a string so it can be safely embedded inside a JavaScript string literal.
     * Single quotes, double quotes, backslashes, and control characters are escaped.
     *
     * @param input the input string to escape
     * @return JavaScript-safe escaped string, or empty string if input is null
     */
    public static String escapeJs(final String input) {
        if (input == null) {
            return StringUtil.EMPTY;
        }
        return StringEscapeUtils.escapeEcmaScript(input);
    }

    /**
     * Replaces all occurrences of a regular expression pattern in the input string.
     *
     * @param input the input object to process
     * @param regex the regular expression pattern to match
     * @param replacement the replacement string
     * @return string with all matches replaced, or empty string if input is null
     */
    public static String replace(final Object input, final String regex, final String replacement) {
        if (input == null) {
            return StringUtil.EMPTY;
        }
        return input.toString().replaceAll(regex, replacement);
    }

    /**
     * Checks if the current user has the specified action role or administrative privileges.
     *
     * @param role the role to check (supports both view and edit variants)
     * @return true if the user has the role or admin privileges, false otherwise
     */
    public static boolean hasActionRole(final String role) {
        final String[] roles;
        if (role.endsWith(FessAdminAction.VIEW)) {
            roles = new String[] { role, role.substring(0, role.length() - FessAdminAction.VIEW.length()) };
        } else {
            roles = new String[] { role };
        }
        final FessLoginAssist loginAssist = ComponentUtil.getFessLoginAssist();
        return loginAssist.getSavedUserBean()
                .map(user -> user.hasRoles(roles) || user.hasRoles(ComponentUtil.getFessConfig().getAuthenticationAdminRolesAsArray()))
                .orElse(false);
    }
}
