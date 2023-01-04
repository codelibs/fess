/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import javax.servlet.http.HttpServletRequest;

import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.util.CharUtil;
import org.codelibs.fess.taglib.FessFunctions;
import org.lastaflute.web.util.LaRequestUtil;

public final class DocumentUtil {

    private DocumentUtil() {
    }

    public static <T> T getValue(final Map<String, Object> doc, final String key, final Class<T> clazz, final T defaultValue) {
        final T value = getValue(doc, key, clazz);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

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
