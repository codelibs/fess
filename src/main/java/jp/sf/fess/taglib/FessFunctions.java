/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.taglib;

import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jp.sf.fess.Constants;
import jp.sf.fess.entity.FacetQueryView;
import jp.sf.fess.helper.HotSearchWordHelper;
import jp.sf.fess.helper.HotSearchWordHelper.Range;
import jp.sf.fess.helper.ViewHelper;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.solr.common.util.DateUtil;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.URLEncoderUtil;

public class FessFunctions {
    private static final String GEO_PREFIX = "geo.";

    private static final String FACET_PREFIX = "facet.";

    private static final String MLT_PREFIX = "mlt.";

    protected FessFunctions() {
        // nothing
    }

    public static String label(final String value) {
        final Map<String, String> labelValueMap = (Map<String, String>) RequestUtil
                .getRequest().getAttribute(Constants.LABEL_VALUE_MAP);
        if (labelValueMap != null) {
            final String name = labelValueMap.get(value);
            if (name != null) {
                return name;
            }
        }
        return value;
    }

    public static List<String> hsw(final String value, final Integer size) {
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

        final HotSearchWordHelper hotSearchWordHelper = SingletonS2Container
                .getComponent(HotSearchWordHelper.class);
        final List<String> wordList = hotSearchWordHelper
                .getHotSearchWordList(range);
        if (wordList.size() > size) {
            return wordList.subList(0, size);
        }
        return wordList;
    }

    public static Integer hswsize(final String value) {
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

        final HotSearchWordHelper hotSearchWordHelper = SingletonS2Container
                .getComponent(HotSearchWordHelper.class);
        return hotSearchWordHelper.getHotSearchWordList(range).size();
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
            return DateUtil.parseDate(value);
        } catch (final ParseException e) {
            return null;
        }
    }

    public static String formatDate(final Date date) {
        return DateUtil.getThreadLocalDateFormat().format(date);
    }

    public static String facetQuery() {
        return createQuery(Constants.FACET_QUERY, FACET_PREFIX);
    }

    public static String mltQuery() {
        return createQuery(Constants.MLT_QUERY, MLT_PREFIX);
    }

    public static String geoQuery() {
        return createQuery(Constants.GEO_QUERY, GEO_PREFIX);
    }

    public static String facetForm() {
        return createForm(Constants.FACET_FORM, FACET_PREFIX);
    }

    public static String mltForm() {
        return createForm(Constants.MLT_FORM, MLT_PREFIX);
    }

    public static String geoForm() {
        return createForm(Constants.GEO_FORM, GEO_PREFIX);
    }

    public static List<FacetQueryView> facetQueryViewList() {
        final ViewHelper viewHelper = SingletonS2Container
                .getComponent("viewHelper");
        return viewHelper.getFacetQueryViewList();
    }

    private static String createQuery(final String key, final String prefix) {
        final HttpServletRequest request = RequestUtil.getRequest();
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
        final HttpServletRequest request = RequestUtil.getRequest();
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
