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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.util.CharUtil;
import org.codelibs.fess.taglib.FessFunctions;
import org.lastaflute.web.util.LaRequestUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility class for document data manipulation and type conversion.
 * This class provides static methods for extracting typed values from document maps,
 * URL encoding, and other document-related operations. It's designed as a final
 * utility class with only static methods.
 *
 */
public final class DocumentUtil {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private DocumentUtil() {
        // Utility class - no instantiation
    }

    /**
     * Gets a typed value from a document map with a default value.
     *
     * @param <T> the type to convert the value to
     * @param doc the document map to extract the value from
     * @param key the key to look up in the document map
     * @param clazz the class type to convert the value to
     * @param defaultValue the default value to return if the key is not found or conversion fails
     * @return the converted value or the default value if not found
     */
    public static <T> T getValue(final Map<String, Object> doc, final String key, final Class<T> clazz, final T defaultValue) {
        final T value = getValue(doc, key, clazz);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Gets a typed value from a document map.
     * Supports conversion to String, Date, Long, Integer, Double, Float, Boolean,
     * List, and String array types. Handles both single values and arrays/lists.
     *
     * @param <T> the type to convert the value to
     * @param doc the document map to extract the value from
     * @param key the key to look up in the document map
     * @param clazz the class type to convert the value to
     * @return the converted value or null if not found or conversion fails
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValue(final Map<String, Object> doc, final String key, final Class<T> clazz) {
        if (doc == null || key == null) {
            return null;
        }

        final Object value = doc.get(key);
        if (value == null) {
            return null;
        }

        if (value instanceof List) {
            if (clazz.isAssignableFrom(List.class)) {
                return (T) value;
            }
            if (clazz.isAssignableFrom(String[].class)) {
                return (T) ((List<?>) value).stream().filter(s -> s != null).map(Object::toString).toArray(n -> new String[n]);
            }

            if (((List<?>) value).isEmpty()) {
                return null;
            }

            return convertObj(((List<?>) value).get(0), clazz);
        }
        if (value instanceof String[]) {
            if (clazz.isAssignableFrom(String[].class)) {
                return (T) value;
            }
            if (clazz.isAssignableFrom(List.class)) {
                final List<String> list = new ArrayList<>();
                Collections.addAll(list, (String[]) value);
                return (T) list;
            }

            if (((String[]) value).length == 0) {
                return null;
            }

            return convertObj(((String[]) value)[0], clazz);
        }

        return convertObj(value, clazz);
    }

    /**
     * Converts an object to the specified type.
     * Supports conversion to String, Date, Long, Integer, Double, Float, and Boolean types.
     *
     * @param <T> the type to convert the value to
     * @param value the value to convert
     * @param clazz the target class type
     * @return the converted value or null if conversion is not supported
     */
    @SuppressWarnings("unchecked")
    private static <T> T convertObj(final Object value, final Class<T> clazz) {
        if (value == null) {
            return null;
        }

        if (clazz.isAssignableFrom(String.class)) {
            return (T) value.toString();
        }
        if (clazz.isAssignableFrom(Date.class)) {
            if (value instanceof Date) {
                return (T) value;
            }
            return (T) FessFunctions.parseDate(value.toString());
        }
        if (clazz.isAssignableFrom(Long.class)) {
            if (value instanceof Long) {
                return (T) value;
            }
            return (T) Long.valueOf(value.toString());
        }
        if (clazz.isAssignableFrom(Integer.class)) {
            if (value instanceof Integer) {
                return (T) value;
            }
            return (T) Integer.valueOf(value.toString());
        }
        if (clazz.isAssignableFrom(Double.class)) {
            if (value instanceof Double) {
                return (T) value;
            }
            return (T) Double.valueOf(value.toString());
        }
        if (clazz.isAssignableFrom(Float.class)) {
            if (value instanceof Float) {
                return (T) value;
            }
            return (T) Float.valueOf(value.toString());
        }
        if (clazz.isAssignableFrom(Boolean.class)) {
            if (value instanceof Boolean) {
                return (T) value;
            }
            return (T) Boolean.valueOf(value.toString());
        }
        return null;
    }

    /**
     * Encodes a URL by encoding non-URL-safe characters.
     * Uses the request's character encoding if available, otherwise defaults to UTF-8.
     * Only encodes characters that are not considered URL-safe according to CharUtil.
     *
     * @param url the URL to encode
     * @return the encoded URL with non-URL-safe characters properly encoded
     */
    public static String encodeUrl(final String url) {
        final String enc = LaRequestUtil.getOptionalRequest().filter(req -> req.getCharacterEncoding() != null)
                .map(HttpServletRequest::getCharacterEncoding).orElse(Constants.UTF_8);
        final StringBuilder buf = new StringBuilder(url.length() + 100);
        for (final char c : url.toCharArray()) {
            if (CharUtil.isUrlChar(c)) {
                buf.append(c);
            } else {
                try {
                    buf.append(URLEncoder.encode(String.valueOf(c), enc));
                } catch (final UnsupportedEncodingException e) {
                    buf.append(c);
                }
            }
        }
        return buf.toString();
    }
}
