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
package org.codelibs.fess.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.codelibs.core.lang.StringUtil;
import org.lastaflute.web.servlet.filter.LastaPrepareFilter;

public class EncodingFilter implements Filter {
    public static final String ENCODING_MAP = "encodingRules";

    protected Map<String, String> encodingMap = new ConcurrentHashMap<>();

    protected String encoding;

    protected ServletContext servletContext;

    protected URLCodec urlCodec = new URLCodec();

    @Override
    public void init(final FilterConfig config) throws ServletException {
        servletContext = config.getServletContext();

        encoding = config.getInitParameter(LastaPrepareFilter.ENCODING_KEY);
        if (encoding == null) {
            encoding = LastaPrepareFilter.DEFAULT_ENCODING;
        }

        // ex. sjis:Shift_JIS,eucjp:EUC-JP
        final String value = config.getInitParameter(ENCODING_MAP);
        if (StringUtil.isNotBlank(value)) {
            final String[] encodingPairs = value.split(",");
            for (final String pair : encodingPairs) {
                final String[] encInfos = pair.trim().split(":");
                if (encInfos.length == 2) {
                    encodingMap.put("/" + encInfos[0] + "/", encInfos[1]);
                }
            }
        }
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final String servletPath = req.getServletPath();
        for (final Map.Entry<String, String> entry : encodingMap.entrySet()) {
            final String path = entry.getKey();
            if (servletPath.startsWith(path)) {
                req.setCharacterEncoding(entry.getValue());
                final StringBuilder locationBuf = new StringBuilder(1000);
                final String contextPath = servletContext.getContextPath();
                if (StringUtil.isNotBlank(contextPath) && !"/".equals(contextPath)) {
                    locationBuf.append(contextPath);
                }
                locationBuf.append('/');
                locationBuf.append(servletPath.substring(path.length()));
                boolean append = false;
                final Map<String, String[]> parameterMap = new HashMap<>(req.getParameterMap());
                parameterMap.putAll(getParameterMapFromQueryString(req, entry.getValue()));
                for (final Map.Entry<String, String[]> paramEntry : parameterMap.entrySet()) {
                    final String[] values = paramEntry.getValue();
                    if (values == null) {
                        continue;
                    }
                    final String key = paramEntry.getKey();
                    for (final String value : values) {
                        if (append) {
                            locationBuf.append('&');
                        } else {
                            locationBuf.append('?');
                            append = true;
                        }
                        locationBuf.append(urlCodec.encode(key, encoding));
                        locationBuf.append('=');
                        locationBuf.append(urlCodec.encode(value, encoding));
                    }

                }
                final HttpServletResponse res = (HttpServletResponse) response;
                res.sendRedirect(locationBuf.toString());
                return;
            }
        }

        chain.doFilter(request, response);
    }

    protected Map<String, String[]> getParameterMapFromQueryString(final HttpServletRequest request, final String enc) throws IOException {
        final String queryString = request.getQueryString();
        if (StringUtil.isNotBlank(queryString)) {
            return parseQueryString(queryString, enc);
        }
        return Collections.emptyMap();
    }

    protected Map<String, String[]> parseQueryString(final String queryString, final String enc) throws IOException {
        final Map<String, List<String>> paramListMap = new HashMap<>();
        final String[] pairs = queryString.split("&");
        try {
            for (final String pair : pairs) {
                final int pos = pair.indexOf('=');
                if (pos >= 0) {
                    final String key = urlCodec.decode(pair.substring(0, pos), enc);
                    List<String> list = paramListMap.get(key);
                    if (list == null) {
                        list = new ArrayList<>();
                        paramListMap.put(key, list);
                    }
                    if (pos + 1 < pair.length()) {
                        list.add(urlCodec.decode(pair.substring(pos + 1), enc));
                    } else {
                        list.add(StringUtil.EMPTY);
                    }
                } else {
                    final String key = urlCodec.decode(pair, enc);
                    List<String> list = paramListMap.get(key);
                    if (list == null) {
                        list = new ArrayList<>();
                        paramListMap.put(key, list);
                    }
                    list.add(StringUtil.EMPTY);
                }
            }
        } catch (final DecoderException e) {
            throw new IOException(e);
        }

        final Map<String, String[]> paramMap = new HashMap<>(paramListMap.size());
        for (final Map.Entry<String, List<String>> entry : paramListMap.entrySet()) {
            final List<String> list = entry.getValue();
            paramMap.put(entry.getKey(), list.toArray(new String[list.size()]));
        }
        return paramMap;
    }

    @Override
    public void destroy() {
        // nothing
    }
}
