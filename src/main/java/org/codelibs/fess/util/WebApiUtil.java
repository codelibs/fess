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

import org.codelibs.fess.exception.WebApiException;
import org.lastaflute.web.util.LaRequestUtil;

/**
 * Utility class for web API operations.
 * Provides functionality for setting and retrieving objects from request attributes,
 * error handling, and validation in web API context.
 */
public final class WebApiUtil {

    /**
     * Request attribute key for storing web API exceptions.
     */
    private static final String WEB_API_EXCEPTION = "webApiException";

    /**
     * Path prefix claimed by {@code SearchApiV2Manager}.
     */
    private static final String API_V2_PATH_PREFIX = "/api/v2";

    /**
     * Path prefix claimed by {@code SearchEngineApiManager}.
     */
    private static final String SEARCH_ENGINE_API_PATH_PREFIX = "/admin/server_";

    /**
     * Private constructor to prevent instantiation.
     */
    private WebApiUtil() {
    }

    /**
     * Determines whether a request URI addresses one of the web API endpoints.
     *
     * <p>This approximates the {@code WebApiManager#matches(HttpServletRequest)} implementations,
     * which are the established way Fess tells an API request from a browser request: each
     * manager claims a path prefix and matches on the servlet path. This method exists for
     * callers that cannot consult the managers directly because the servlet path no longer
     * describes the original request -- notably the container error page, where the path
     * elements describe the error page itself and only the
     * {@code jakarta.servlet.error.request_uri} attribute still carries the original URI.
     *
     * <p>An approximation, not a faithful copy, in two ways. It knows only the prefixes claimed
     * by the managers bundled here; plugins register further ones ({@code /api/v1},
     * {@code /json}, {@code /suggest}, {@code /mcp}) that it cannot see. And it matches the raw,
     * undecoded URI, whereas the managers compare the decoded servlet path, so a percent-encoded
     * prefix reads as a browser request. Both are tolerable because the only caller decides
     * whether to replace an error status with an HTML page: every manager that can reach a
     * container error page issues {@code sendError} under {@code /admin/server_}, and a miss
     * merely falls back to the redirect that preceded this check.
     *
     * <p>Deliberately dependency-free: it performs no component lookup and reads no
     * configuration, so it stays safe to call while rendering an error response.
     *
     * @param requestUri The original request URI, including the context path (may be null)
     * @param contextPath The context path to strip, as returned by {@code getContextPath()} (may be null)
     * @return true if the URI addresses a web API endpoint
     */
    public static boolean isApiRequestUri(final String requestUri, final String contextPath) {
        if (requestUri == null) {
            return false;
        }
        String path = requestUri;
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        // SearchApiV2Manager#matches accepts the prefix itself or any sub-path of it.
        if (path.equals(API_V2_PATH_PREFIX) || path.startsWith(API_V2_PATH_PREFIX + "/")) {
            return true;
        }
        // SearchEngineApiManager#matches accepts a plain prefix match: the access token is
        // appended directly to the prefix, so there is no separator to anchor on.
        return path.startsWith(SEARCH_ENGINE_API_PATH_PREFIX);
    }

    /**
     * Sets an object in the current request attributes.
     *
     * @param name The attribute name
     * @param value The attribute value
     */
    public static void setObject(final String name, final Object value) {
        LaRequestUtil.getOptionalRequest().ifPresent(req -> req.setAttribute(name, value));
    }

    /**
     * Gets an object from the current request attributes.
     *
     * @param <T> The type of the object
     * @param name The attribute name
     * @return The attribute value, or null if not found
     */
    @SuppressWarnings("unchecked")
    public static <T> T getObject(final String name) {
        return LaRequestUtil.getOptionalRequest().map(req -> (T) req.getAttribute(name)).orElse(null);
    }

    /**
     * Sets an error in the current request with the specified status code and message.
     *
     * @param statusCode The HTTP status code
     * @param message The error message
     */
    public static void setError(final int statusCode, final String message) {
        LaRequestUtil.getOptionalRequest().ifPresent(req -> req.setAttribute(WEB_API_EXCEPTION, new WebApiException(statusCode, message)));
    }

    /**
     * Sets an error in the current request with the specified status code and exception.
     *
     * @param statusCode The HTTP status code
     * @param e The exception that caused the error
     */
    public static void setError(final int statusCode, final Exception e) {
        LaRequestUtil.getOptionalRequest().ifPresent(req -> req.setAttribute(WEB_API_EXCEPTION, new WebApiException(statusCode, e)));
    }

    /**
     * Validates the current request by checking for stored web API exceptions.
     * Throws any stored WebApiException if found.
     *
     * @throws WebApiException If a web API exception was previously stored
     */
    public static void validate() {
        LaRequestUtil.getOptionalRequest().map(req -> (WebApiException) req.getAttribute(WEB_API_EXCEPTION)).ifPresent(e -> {
            throw e;
        });
    }

}
