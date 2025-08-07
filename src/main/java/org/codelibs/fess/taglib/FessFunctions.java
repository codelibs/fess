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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.io.File;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.util.DateConverter;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.entity.FacetQueryView;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.di.util.LdiURLUtil;
import org.lastaflute.web.LastaWebKey;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaResponseUtil;
import org.lastaflute.web.util.LaServletContextUtil;
import org.opensearch.common.joda.Joda;

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

    /** Prefix for geographic query parameters */
    private static final String GEO_PREFIX = "geo.";

    /** Prefix for facet query parameters */
    private static final String FACET_PREFIX = "facet.";

    /** Format identifier for PDF date parsing */
    private static final String PDF_DATE = "pdf_date";

    /** Regular expression pattern for matching email addresses */
    private static final Pattern EMAIL_ADDRESS_PATTERN =
            Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}", Pattern.CASE_INSENSITIVE);

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
                        logger.debug("Failed to access {}", key, e);
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
                if (req.getAttribute(LastaWebKey.USER_LOCALE_KEY) instanceof Locale locale) {
                    return locale;
                }
                return Locale.ENGLISH;
            }).orElse(Locale.ENGLISH).getLanguage() + "\">";
        }
        return "</html>";
    }

    /**
     * Checks if a label with the specified key exists in the current request's label map.
     *
     * @param value the label key to check
     * @return true if the label exists, false otherwise
     */
    public static Boolean labelExists(final String value) {
        return LaRequestUtil.getOptionalRequest().map(req -> {
            @SuppressWarnings("unchecked")
            final Map<String, String> labelValueMap = (Map<String, String>) req.getAttribute(Constants.LABEL_VALUE_MAP);
            if (labelValueMap != null) {
                return labelValueMap.get(value) != null;
            }
            return false;
        }).orElse(false);
    }

    /**
     * Retrieves the localized label value for the given key from the current request's label map.
     *
     * @param value the label key to retrieve
     * @return the localized label value, or the key itself if not found
     */
    public static String label(final String value) {
        return LaRequestUtil.getOptionalRequest().map(req -> {
            @SuppressWarnings("unchecked")
            final Map<String, String> labelValueMap = (Map<String, String>) req.getAttribute(Constants.LABEL_VALUE_MAP);
            if (labelValueMap != null) {
                return labelValueMap.get(value);
            }
            return null;
        }).orElse(value);
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
     * Formats a duration in milliseconds to a human-readable string.
     *
     * @param durationMillis the duration in milliseconds
     * @return formatted duration string (e.g., "2 days 14:30:25.123" or "14:30:25.123")
     */
    public static String formatDuration(final long durationMillis) {
        return DurationFormatUtils.formatDuration(durationMillis, "d 'days' HH:mm:ss.SSS").replace("0 days", StringUtil.EMPTY).trim();

    }

    /**
     * Formats a number using the specified pattern and user's locale.
     *
     * @param value the number to format
     * @param pattern the number format pattern
     * @return formatted number string
     */
    public static String formatNumber(final long value, final String pattern) {
        final DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(getUserLocale());
        df.applyPattern(pattern);
        return df.format(value);
    }

    /**
     * Retrieves the current user's locale from the request manager.
     *
     * @return the user's locale, or Locale.ROOT if not available
     */
    private static Locale getUserLocale() {
        final Locale locale = ComponentUtil.getRequestManager().getUserLocale();
        if (locale == null) {
            return Locale.ROOT;
        }
        return locale;
    }

    /**
     * Formats a file size in bytes to a human-readable string with appropriate units.
     *
     * @param value the file size in bytes
     * @return formatted file size string (e.g., "1.5M", "2.3G", "512K")
     */
    public static String formatFileSize(final long value) {
        double target = value;
        String unit = ""; // TODO l10n?
        String format = "0.#";
        if (value < 1024) {
            format = "0";
        } else if (value < 1024L * 1024L) {
            target /= 1024;
            unit = "K";
        } else if (value < 1024L * 1024L * 1024L) {
            target /= 1024;
            target /= 1024;
            unit = "M";
        } else if (value < 1024L * 1024L * 1024L * 1024L) {
            target /= 1024;
            target /= 1024;
            target /= 1024;
            unit = "G";
        } else {
            target /= 1024;
            target /= 1024;
            target /= 1024;
            target /= 1024;
            unit = "T";
        }
        final DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(getUserLocale());
        df.applyPattern(format);
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(target) + unit;
    }

    /**
     * Generates URL query parameters for pagination, excluding the specified query parameter.
     *
     * @param query the query parameter to exclude from paging
     * @return URL-encoded query string for pagination
     */
    public static String pagingQuery(final String query) {
        return LaRequestUtil.getOptionalRequest().map(req -> {
            @SuppressWarnings("unchecked")
            final List<String> pagingQueryList = (List<String>) req.getAttribute(Constants.PAGING_QUERY_LIST);
            if (pagingQueryList != null) {
                final String prefix;
                if (query != null) {
                    prefix = "ex_q=" + query.split(":")[0] + "%3A";
                } else {
                    prefix = null;
                }
                return pagingQueryList.stream()
                        .filter(s -> prefix == null || !s.startsWith(prefix))
                        .collect(Collectors.joining("&", "&", StringUtil.EMPTY));
            }
            return null;
        }).orElse(StringUtil.EMPTY);
    }

    /**
     * Generates URL query parameters for facet filtering.
     *
     * @return URL-encoded query string containing facet parameters
     */
    public static String facetQuery() {
        return createQuery(Constants.FACET_QUERY, FACET_PREFIX);
    }

    /**
     * Generates URL query parameters for geographic filtering.
     *
     * @return URL-encoded query string containing geographic parameters
     */
    public static String geoQuery() {
        return createQuery(Constants.GEO_QUERY, GEO_PREFIX);
    }

    /**
     * Generates hidden HTML form fields for facet filtering.
     *
     * @return HTML string containing hidden input fields for facet parameters
     */
    public static String facetForm() {
        return createForm(Constants.FACET_FORM, FACET_PREFIX);
    }

    /**
     * Generates hidden HTML form fields for geographic filtering.
     *
     * @return HTML string containing hidden input fields for geographic parameters
     */
    public static String geoForm() {
        return createForm(Constants.GEO_FORM, GEO_PREFIX);
    }

    /**
     * Retrieves the list of facet query view objects for display.
     *
     * @return list of FacetQueryView objects
     */
    public static List<FacetQueryView> facetQueryViewList() {
        final ViewHelper viewHelper = ComponentUtil.getViewHelper();
        return viewHelper.getFacetQueryViewList();
    }

    /**
     * Creates a URL query string from request parameters that start with the specified prefix.
     *
     * @param key the request attribute key to cache the result
     * @param prefix the parameter name prefix to filter by
     * @return URL-encoded query string
     */
    private static String createQuery(final String key, final String prefix) {
        return LaRequestUtil.getOptionalRequest().map(request -> {
            String query = (String) request.getAttribute(key);
            if (query == null) {
                final StringBuilder buf = new StringBuilder(100);
                final Enumeration<String> names = request.getParameterNames();
                while (names.hasMoreElements()) {
                    final String name = names.nextElement();
                    if (name.startsWith(prefix)) {
                        final String[] values = request.getParameterValues(name);
                        if (values != null) {
                            for (final String value : values) {
                                buf.append('&');
                                buf.append(LdiURLUtil.encode(name, Constants.UTF_8));
                                buf.append('=');
                                buf.append(LdiURLUtil.encode(value, Constants.UTF_8));
                            }
                        }
                    }
                }
                query = buf.toString();
                request.setAttribute(key, query);
            }
            return query;
        }).orElse(null);
    }

    /**
     * Creates HTML hidden form fields from request parameters that start with the specified prefix.
     *
     * @param key the request attribute key to cache the result
     * @param prefix the parameter name prefix to filter by
     * @return HTML string containing hidden input fields
     */
    private static String createForm(final String key, final String prefix) {
        return LaRequestUtil.getOptionalRequest().map(request -> {
            String query = (String) request.getAttribute(key);
            if (query == null) {
                final StringBuilder buf = new StringBuilder(100);
                final Enumeration<String> names = request.getParameterNames();
                while (names.hasMoreElements()) {
                    final String name = names.nextElement();
                    if (name.startsWith(prefix)) {
                        final String[] values = request.getParameterValues(name);
                        if (values != null) {
                            for (final String value : values) {
                                buf.append("<input type=\"hidden\" name=\"");
                                buf.append(StringEscapeUtils.escapeHtml4(name));
                                buf.append("\" value=\"");
                                buf.append(StringEscapeUtils.escapeHtml4(value));
                                buf.append("\"/>");
                            }
                        }
                    }
                }
                query = buf.toString();
                request.setAttribute(key, query);
            }
            return query;
        }).orElse(null);
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
                logger.debug("Failed to access {}", input, e);
            }
        }
        return LaResponseUtil.getResponse().encodeURL(sb.toString());
    }

    /**
     * Encodes a string for similar document hash processing.
     *
     * @param input the string to encode
     * @return encoded string, or the original input if blank
     */
    public static String sdh(final String input) {
        if (StringUtil.isBlank(input)) {
            return input;
        }
        return ComponentUtil.getDocumentHelper().encodeSimilarDocHash(input);
    }

    /**
     * Joins array or list elements into a single space-separated string.
     *
     * @param input the input object (String[], List, or String)
     * @return joined string with elements separated by spaces, or empty string if invalid input
     */
    public static String join(final Object input) {
        String[] values = null;
        if (input instanceof String[]) {
            values = (String[]) input;
        } else if (input instanceof List) {
            values = ((List<?>) input).stream().filter(Objects::nonNull).map(Object::toString).toArray(n -> new String[n]);
        } else if (input instanceof String) {
            return input.toString();
        }
        if (values != null) {
            return stream(values).get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.joining(" ")));
        }
        return StringUtil.EMPTY;
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
     * Formats code content with syntax highlighting and line numbers.
     *
     * @param prefix the line number prefix pattern
     * @param style the CSS class name for styling
     * @param mimetype the MIME type of the content (currently unused)
     * @param input the code content to format
     * @return HTML formatted code with line numbers and styling
     */
    public static String formatCode(final String prefix, final String style, final String mimetype, final String input) {
        if (input == null) {
            return StringUtil.EMPTY;
        }
        final Pattern pattern = Pattern.compile("^" + prefix + "([0-9]+):(.*)$");
        final String[] values = input.split("\n");
        final List<String> list = new ArrayList<>(values.length);
        int lineNum = 0;
        for (final String line : values) {
            final Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                if (lineNum == 0) {
                    lineNum = Integer.parseInt(matcher.group(1));
                    list.clear();
                }
                list.add(matcher.group(2));
            } else {
                list.add(line);
            }
        }
        if (lineNum == 0 || list.isEmpty()) {
            return "<pre class=\"" + style + "\">" + input + "</pre>";
        }
        int lastIndex = list.size();
        if (list.get(list.size() - 1).endsWith("...")) {
            lastIndex--;
        }
        if (lastIndex <= 0) {
            lastIndex = 1;
        }
        final String content = list.subList(0, lastIndex).stream().collect(Collectors.joining("\n"));
        if (StringUtil.isBlank(content)) {
            return "<pre class=\"" + style + "\">" + input.replaceAll("L[0-9]+:", StringUtil.EMPTY).trim() + "</pre>";
        }
        return "<pre class=\"" + style + " linenums:" + lineNum + "\">" + content + "</pre>";
    }

    /**
     * Retrieves a localized message for the given key.
     *
     * @param key the message key
     * @param defaultValue the default value to return if message not found
     * @return localized message or default value
     */
    public static String getMessage(final String key, final String defaultValue) {
        final Locale locale = LaRequestUtil.getOptionalRequest().map(HttpServletRequest::getLocale).orElse(Locale.ROOT);
        return ComponentUtil.getMessageManager().findMessage(locale, key).orElse(defaultValue);
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
        final FessLoginAssist loginAssist = ComponentUtil.getComponent(FessLoginAssist.class);
        return loginAssist.getSavedUserBean()
                .map(user -> user.hasRoles(roles) || user.hasRoles(ComponentUtil.getFessConfig().getAuthenticationAdminRolesAsArray()))
                .orElse(false);
    }

    /**
     * Masks email addresses in the input string for privacy protection.
     *
     * @param value the string that may contain email addresses
     * @return string with email addresses replaced by masked pattern
     */
    public static String maskEmail(final String value) {
        if (value == null) {
            return StringUtil.EMPTY;
        }
        return EMAIL_ADDRESS_PATTERN.matcher(value).replaceAll("******@****.***");
    }
}
