/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FacetQueryView;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.di.util.LdiURLUtil;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaServletContextUtil;

public class FessFunctions {

    private static final String GEO_PREFIX = "geo.";

    private static final String FACET_PREFIX = "facet.";

    protected FessFunctions() {
        // nothing
    }

    public static Boolean labelExists(final String value) {
        @SuppressWarnings("unchecked")
        final Map<String, String> labelValueMap = (Map<String, String>) LaRequestUtil.getRequest().getAttribute(Constants.LABEL_VALUE_MAP);
        if (labelValueMap != null) {
            return labelValueMap.get(value) != null;
        }
        return false;
    }

    public static String label(final String value) {
        @SuppressWarnings("unchecked")
        final Map<String, String> labelValueMap = (Map<String, String>) LaRequestUtil.getRequest().getAttribute(Constants.LABEL_VALUE_MAP);
        if (labelValueMap != null) {
            final String name = labelValueMap.get(value);
            if (name != null) {
                return name;
            }
        }
        return value;
    }

    public static Date date(final Long value) {
        if (value == null) {
            return null;
        }
        return new Date(value.longValue());
    }

    public static Date parseDate(final String value) {
        return parseDate(value, Constants.ISO_DATETIME_FORMAT);
    }

    public static Date parseDate(final String value, final String format) {
        if (value == null) {
            return null;
        }
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setTimeZone(Constants.TIMEZONE_UTC);
            return sdf.parse(value);
        } catch (final ParseException e) {
            return null;
        }
    }

    public static String formatDate(final Date date) {
        final SimpleDateFormat sdf = new SimpleDateFormat(Constants.ISO_DATETIME_FORMAT);
        sdf.setTimeZone(Constants.TIMEZONE_UTC);
        return sdf.format(date);
    }

    public static String formatDate(final LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern(Constants.ISO_DATETIME_FORMAT, Locale.ROOT));
    }

    public static String formatNumber(final long value) {
        int ratio = 1;
        String unit = "";
        String format = "0.#";
        if (value < 1024) {
            format = "0";
        } else if (value < (1024 * 1024)) {
            ratio = 1024;
            unit = "K";
        } else if (value < (1024 * 1024 * 1024)) {
            ratio = 1024 * 1024;
            unit = "M";
        } else {
            ratio = 1024 * 1024 * 1024;
            unit = "G";
        }
        final DecimalFormat df = new DecimalFormat(format + unit);
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format((double) value / ratio);
    }

    public static String pagingQuery(final String query) {
        final HttpServletRequest request = LaRequestUtil.getRequest();
        @SuppressWarnings("unchecked")
        final List<String> pagingQueryList = (List<String>) request.getAttribute(Constants.PAGING_QUERY_LIST);
        if (pagingQueryList != null) {
            final String prefix;
            if (query != null) {
                prefix = "ex_q=" + query.split(":")[0] + "%3A";
            } else {
                prefix = null;
            }
            return pagingQueryList.stream().filter(s -> prefix == null || !s.startsWith(prefix))
                    .collect(Collectors.joining("&", "&", StringUtil.EMPTY));
        }
        return StringUtil.EMPTY;
    }

    public static String facetQuery() {
        return createQuery(Constants.FACET_QUERY, FACET_PREFIX);
    }

    public static String geoQuery() {
        return createQuery(Constants.GEO_QUERY, GEO_PREFIX);
    }

    public static String facetForm() {
        return createForm(Constants.FACET_FORM, FACET_PREFIX);
    }

    public static String geoForm() {
        return createForm(Constants.GEO_FORM, GEO_PREFIX);
    }

    public static List<FacetQueryView> facetQueryViewList() {
        final ViewHelper viewHelper = ComponentUtil.getViewHelper();
        return viewHelper.getFacetQueryViewList();
    }

    private static String createQuery(final String key, final String prefix) {
        final HttpServletRequest request = LaRequestUtil.getRequest();
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
    }

    private static String createForm(final String key, final String prefix) {
        final HttpServletRequest request = LaRequestUtil.getRequest();
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
    }

    public static String base64(final String value) {
        if (value == null) {
            return StringUtil.EMPTY;
        }
        return Base64.getUrlEncoder().encodeToString(value.getBytes(Constants.CHARSET_UTF_8));
    }

    public static boolean fileExists(final String path) {
        final File file = new File(LaServletContextUtil.getServletContext().getRealPath(path));
        return file.exists();
    }

}
