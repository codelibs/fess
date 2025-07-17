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
     * Private constructor to prevent instantiation.
     */
    private WebApiUtil() {
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
