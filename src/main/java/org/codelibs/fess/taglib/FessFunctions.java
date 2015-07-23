/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FacetQueryView;
import org.codelibs.fess.helper.HotSearchWordHelper;
import org.codelibs.fess.helper.HotSearchWordHelper.Range;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.util.LaRequestUtil;

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

    public static List<String> hsw(final String value, final Integer size) {
        if (!isSupportHotSearchWord()) {
            return Collections.emptyList();
        }

        Range range;
        if (value == null) {
            range = Range.ENTIRE;
        } else if ("day".equals(value) || "1".equals(value)) {
            range = Range.ONE_DAY;
        } else if ("week".equals(value) || "7".equals(value)) {
            range = Range.ONE_DAY;
        } else if ("month".equals(value) || "30".equals(value)) {
            range = Range.ONE_DAY;
        } else if ("year".equals(value) || "365".equals(value)) {
            range = Range.ONE_DAY;
        } else {
            range = Range.ENTIRE;
        }

        final HotSearchWordHelper hotSearchWordHelper = ComponentUtil.getHotSearchWordHelper();
        final List<String> wordList = hotSearchWordHelper.getHotSearchWordList(range);
        if (wordList.size() > size) {
            return wordList.subList(0, size);
        }
        return wordList;
    }

    public static Integer hswsize(final String value) {
        if (!isSupportHotSearchWord()) {
            return 0;
        }

        Range range;
        if (value == null) {
            range = Range.ENTIRE;
        } else if ("day".equals(value) || "1".equals(value)) {
            range = Range.ONE_DAY;
        } else if ("week".equals(value) || "7".equals(value)) {
            range = Range.ONE_DAY;
        } else if ("month".equals(value) || "30".equals(value)) {
            range = Range.ONE_DAY;
        } else if ("year".equals(value) || "365".equals(value)) {
            range = Range.ONE_DAY;
        } else {
            range = Range.ENTIRE;
        }

        final HotSearchWordHelper hotSearchWordHelper = ComponentUtil.getHotSearchWordHelper();
        return hotSearchWordHelper.getHotSearchWordList(range).size();
    }

    private static boolean isSupportHotSearchWord() {
        final DynamicProperties crawlerProperties = ComponentUtil.getCrawlerProperties();
        return crawlerProperties != null
                && Constants.TRUE.equals(crawlerProperties.getProperty(Constants.WEB_API_HOT_SEARCH_WORD_PROPERTY, Constants.TRUE));
    }

    public static Date date(final Long value) {
        if (value == null) {
            return null;
        }
        return new Date(value.longValue());
    }

    public static Date parseDate(final String value) {
        if (value == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(Constants.ISO_DATETIME_FORMAT).parse(value);
        } catch (final ParseException e) {
            return null;
        }
    }

    public static String formatDate(final Date date) {
        return new SimpleDateFormat(Constants.ISO_DATETIME_FORMAT).format(date);
    }

    public static String formatDate(final LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern(Constants.ISO_DATETIME_FORMAT, Locale.ROOT));
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
                            buf.append(URLEncoderUtil.encode(name));
                            buf.append('=');
                            buf.append(URLEncoderUtil.encode(value));
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
                            buf.append(StringEscapeUtils.escapeHtml(name));
                            buf.append("\" value=\"");
                            buf.append(StringEscapeUtils.escapeHtml(value));
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

}
