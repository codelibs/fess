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

import javax.servlet.http.HttpServletRequest;

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
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaResponseUtil;
import org.lastaflute.web.util.LaServletContextUtil;
import org.opensearch.common.joda.Joda;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class FessFunctions {
    private static final Logger logger = LogManager.getLogger(FessFunctions.class);

    private static final String GEO_PREFIX = "geo.";

    private static final String FACET_PREFIX = "facet.";

    private static final String PDF_DATE = "pdf_date";

    private static final Pattern EMAIL_ADDRESS_PATTERN =
            Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}", Pattern.CASE_INSENSITIVE);

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

    protected FessFunctions() {
        // nothing
    }

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

    public static Date date(final Long value) {
        if (value == null) {
            return null;
        }
        return new Date(value);
    }

    public static Date parseDate(final String value) {
        return parseDate(value, Constants.DATE_OPTIONAL_TIME);
    }

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

    public static String formatDate(final Date date) {
        if (date == null) {
            return StringUtil.EMPTY;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(Constants.ISO_DATETIME_FORMAT);
        sdf.setTimeZone(Constants.TIMEZONE_UTC);
        return sdf.format(date);
    }

    public static String formatDate(final LocalDateTime date) {
        if (date == null) {
            return StringUtil.EMPTY;
        }
        return date.format(DateTimeFormatter.ofPattern(Constants.ISO_DATETIME_FORMAT, Locale.ROOT));
    }

    public static String formatDate(final ZonedDateTime date, final String format) {
        if (date == null) {
            return StringUtil.EMPTY;
        }
        return date.format(DateTimeFormatter.ofPattern(format, Locale.ROOT));
    }

    public static String formatDuration(final long durationMillis) {
        return DurationFormatUtils.formatDuration(durationMillis, "d 'days' HH:mm:ss.SSS").replace("0 days", StringUtil.EMPTY).trim();

    }

    public static String formatNumber(final long value, final String pattern) {
        final DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(getUserLocale());
        df.applyPattern(pattern);
        return df.format(value);
    }

    private static Locale getUserLocale() {
        final Locale locale = ComponentUtil.getRequestManager().getUserLocale();
        if (locale == null) {
            return Locale.ROOT;
        }
        return locale;
    }

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
                return pagingQueryList.stream().filter(s -> prefix == null || !s.startsWith(prefix))
                        .collect(Collectors.joining("&", "&", StringUtil.EMPTY));
            }
            return null;
        }).orElse(StringUtil.EMPTY);
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

    public static String sdh(final String input) {
        if (StringUtil.isBlank(input)) {
            return input;
        }
        return ComponentUtil.getDocumentHelper().encodeSimilarDocHash(input);
    }

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

    public static String replace(final Object input, final String regex, final String replacement) {
        if (input == null) {
            return StringUtil.EMPTY;
        }
        return input.toString().replaceAll(regex, replacement);
    }

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

    public static String getMessage(final String key, final String defaultValue) {
        final Locale locale = LaRequestUtil.getOptionalRequest().map(HttpServletRequest::getLocale).orElse(Locale.ROOT);
        return ComponentUtil.getMessageManager().findMessage(locale, key).orElse(defaultValue);
    }

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

    public static String maskEmail(final String value) {
        if (value == null) {
            return StringUtil.EMPTY;
        }
        return EMAIL_ADDRESS_PATTERN.matcher(value).replaceAll("******@****.***");
    }
}
