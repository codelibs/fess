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
package org.codelibs.fess.api;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;

import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.fess.Constants;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Base implementation for API managers providing common functionality.
 * Abstract class that provides format detection and response handling for web APIs.
 */
public abstract class BaseApiManager implements WebApiManager {

    private static final String API_FORMAT_TYPE = "apiFormatType";

    /** Path prefix for API endpoints. */
    protected String pathPrefix;

    /**
     * Enumeration of supported API format types.
     */
    protected enum FormatType {
        /** Search API format. */
        SEARCH,
        /** Label API format. */
        LABEL,
        /** Popular word API format. */
        POPULARWORD,
        /** Favorite API format. */
        FAVORITE,
        /** Favorites API format. */
        FAVORITES,
        /** Ping API format. */
        PING,
        /** Scroll API format. */
        SCROLL,
        /** Suggest API format. */
        SUGGEST,
        /** Other API format. */
        OTHER;
    }

    /**
     * Default constructor for BaseApiManager.
     */
    public BaseApiManager() {
        // Default constructor
    }

    /**
     * Gets the path prefix for API endpoints.
     * @return The path prefix.
     */
    public String getPathPrefix() {
        return pathPrefix;
    }

    /**
     * Sets the path prefix for API endpoints.
     * @param pathPrefix The path prefix to set.
     */
    public void setPathPrefix(final String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    /**
     * Gets the format type for the request.
     * @param request The HTTP servlet request.
     * @return The format type.
     */
    protected FormatType getFormatType(final HttpServletRequest request) {
        FormatType formatType = (FormatType) request.getAttribute(API_FORMAT_TYPE);
        if (formatType != null) {
            return formatType;
        }

        formatType = detectFormatType(request);
        request.setAttribute(API_FORMAT_TYPE, formatType);
        return formatType;
    }

    /**
     * Detects the format type from the request parameters.
     * @param request The HTTP servlet request.
     * @return The detected format type.
     */
    protected FormatType detectFormatType(final HttpServletRequest request) {
        String value = request.getParameter("type");
        if (value == null) {
            final String servletPath = request.getServletPath();
            final String[] values = servletPath.replaceAll("/+", "/").split("/");
            if (values.length > 2) {
                value = values[2];
            }
        }
        if (value == null) {
            return FormatType.SEARCH;
        }
        final String type = value.toUpperCase(Locale.ROOT);
        if (FormatType.SEARCH.name().equals(type)) {
            return FormatType.SEARCH;
        }
        if (FormatType.LABEL.name().equals(type)) {
            return FormatType.LABEL;
        }
        if (FormatType.POPULARWORD.name().equals(type)) {
            return FormatType.POPULARWORD;
        }
        if (FormatType.FAVORITE.name().equals(type)) {
            return FormatType.FAVORITE;
        }
        if (FormatType.FAVORITES.name().equals(type)) {
            return FormatType.FAVORITES;
        }
        if (FormatType.PING.name().equals(type)) {
            return FormatType.PING;
        }
        if (FormatType.SCROLL.name().equals(type)) {
            return FormatType.SCROLL;
        }
        if (FormatType.SUGGEST.name().equals(type)) {
            return FormatType.SUGGEST;
        }

        // default
        return FormatType.OTHER;
    }

    /**
     * Writes text content to the HTTP response with specified content type and encoding.
     * @param text The text content to write.
     * @param contentType The content type for the response.
     * @param encoding The character encoding for the response.
     */
    protected void write(final String text, final String contentType, final String encoding) {
        final StringBuilder buf = new StringBuilder(50);
        if (contentType == null) {
            buf.append("text/plain");
        } else {
            buf.append(contentType);
        }
        buf.append("; charset=");
        final String enc;
        if (encoding == null) {
            enc = LaRequestUtil.getOptionalRequest().map(HttpServletRequest::getCharacterEncoding).orElse(Constants.UTF_8);
        } else {
            enc = encoding;
        }
        buf.append(enc);
        final HttpServletResponse response = LaResponseUtil.getResponse();
        response.setContentType(buf.toString());
        writeHeaders(response);
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), enc))) {
            out.print(text);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Writes custom headers to the HTTP response.
     * @param response The HTTP servlet response.
     */
    protected abstract void writeHeaders(final HttpServletResponse response);
}