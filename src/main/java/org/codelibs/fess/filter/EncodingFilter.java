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
package org.codelibs.fess.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.codelibs.core.lang.StringUtil;
import org.lastaflute.web.servlet.filter.LastaPrepareFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet filter for handling character encoding conversion and URL redirection.
 * This filter processes requests with specific encoding requirements and converts
 * character encodings according to configured mapping rules.
 *
 * <p>The filter intercepts requests matching configured path patterns and
 * redirects them with proper character encoding applied to parameters.</p>
 */
public class EncodingFilter implements Filter {
    /** Configuration key for encoding rules mapping */
    public static final String ENCODING_MAP = "encodingRules";

    /** Map of path patterns to their corresponding character encodings */
    protected Map<String, String> encodingMap = new ConcurrentHashMap<>();

    /** Default character encoding to use for requests */
    protected String encoding;

    /** Servlet context for this filter */
    protected ServletContext servletContext;

    /** URL codec for encoding and decoding URL parameters */
    protected URLCodec urlCodec = new URLCodec();

    /**
     * Default constructor for EncodingFilter.
     */
    public EncodingFilter() {
        // Default constructor
    }

    /**
     * Initializes the filter with configuration parameters.
     * Sets up encoding mappings and default encoding from filter configuration.
     *
     * @param config the filter configuration containing initialization parameters
     * @throws ServletException if an error occurs during initialization
     */
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

    /**
     * Processes requests and applies character encoding conversion if needed.
     * Checks if the request path matches any configured encoding rule and
     * performs URL redirection with proper parameter encoding.
     *
     * @param request the servlet request to process
     * @param response the servlet response to use for redirection
     * @param chain the filter chain to continue processing
     * @throws IOException if an I/O error occurs during processing
     * @throws ServletException if a servlet error occurs
     */
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

    /**
     * Extracts and parses parameters from the request query string.
     * Applies the specified character encoding to decode parameter values.
     *
     * @param request the HTTP request containing the query string
     * @param enc the character encoding to use for decoding
     * @return a map of parameter names to their decoded values
     * @throws IOException if an error occurs during parameter parsing
     */
    protected Map<String, String[]> getParameterMapFromQueryString(final HttpServletRequest request, final String enc) throws IOException {
        final String queryString = request.getQueryString();
        if (StringUtil.isNotBlank(queryString)) {
            return parseQueryString(queryString, enc);
        }
        return Collections.emptyMap();
    }

    /**
     * Parses a query string and extracts parameter name-value pairs.
     * Applies URL decoding with the specified character encoding.
     *
     * @param queryString the query string to parse
     * @param enc the character encoding to use for URL decoding
     * @return a map of parameter names to their decoded values
     * @throws IOException if an error occurs during URL decoding
     */
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

    /**
     * Cleans up resources when the filter is destroyed.
     * Currently performs no cleanup operations.
     */
    @Override
    public void destroy() {
        // nothing
    }
}
