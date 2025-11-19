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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.InvalidAccessTokenException;
import org.codelibs.fess.util.ComponentUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Utility class for building and writing JSON responses in the API layer.
 * Provides methods to construct JSON responses with proper error handling and formatting.
 */
public class JsonResponseUtil {

    /** ObjectMapper instance for JSON serialization */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // Configure ObjectMapper for consistent JSON output
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /** Private constructor to prevent instantiation */
    private JsonResponseUtil() {
        // Utility class
    }

    /**
     * Creates a success response map with the given data.
     *
     * @param data The data to include in the response
     * @return A map containing the success response structure
     */
    public static Map<String, Object> success(final Object data) {
        final Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        return response;
    }

    /**
     * Creates an error response map with the given message.
     *
     * @param message The error message
     * @return A map containing the error response structure
     */
    public static Map<String, Object> error(final String message) {
        final Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        return response;
    }

    /**
     * Creates an error response map with error code and message.
     *
     * @param errorCode The error code
     * @param message The error message
     * @return A map containing the error response structure
     */
    public static Map<String, Object> error(final String errorCode, final String message) {
        final Map<String, Object> response = new HashMap<>();
        response.put("error_code", errorCode);
        response.put("message", message);
        return response;
    }

    /**
     * Creates an error response from an exception.
     *
     * @param throwable The exception
     * @param logger The logger to use for logging
     * @return A map containing the error response structure
     */
    public static Map<String, Object> fromException(final Throwable throwable, final Logger logger) {
        final String errorCode = UUID.randomUUID().toString();
        final String stacktraceString = getStackTraceString(throwable);

        if (Constants.TRUE.equalsIgnoreCase(ComponentUtil.getFessConfig().getApiJsonResponseExceptionIncluded())) {
            if (logger.isDebugEnabled()) {
                logger.debug("[{}] {}", errorCode, stacktraceString.replace("\n", "\\n"), throwable);
            } else {
                logger.warn("[{}] {}", errorCode, throwable.getMessage());
            }
            return error(stacktraceString);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("[{}] {}", errorCode, stacktraceString.replace("\n", "\\n"), throwable);
        } else {
            logger.warn("[{}] {}", errorCode, throwable.getMessage());
        }
        return error(errorCode, "An error occurred. Please check the logs for details.");
    }

    /**
     * Gets the stack trace as a string from a throwable.
     *
     * @param throwable The throwable
     * @return The stack trace as a string
     */
    private static String getStackTraceString(final Throwable throwable) {
        if (throwable == null) {
            return "Unknown error";
        }

        final StringBuilder buf = new StringBuilder(1024);
        if (StringUtil.isBlank(throwable.getMessage())) {
            buf.append(throwable.getClass().getName());
        } else {
            buf.append(throwable.getMessage());
        }

        try (final StringWriter sw = new StringWriter(); final PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            pw.flush();
            buf.append(" [ ").append(sw.toString()).append(" ]");
        } catch (final Exception e) {
            // Ignore
        }
        return buf.toString();
    }

    /**
     * Converts an object to JSON string.
     *
     * @param object The object to convert
     * @return The JSON string representation
     * @throws JsonProcessingException if conversion fails
     */
    public static String toJson(final Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    /**
     * Sets appropriate headers for an error response with authentication challenge.
     *
     * @param response The HTTP servlet response
     * @param exception The InvalidAccessTokenException
     */
    public static void setAuthenticationChallenge(final HttpServletResponse response, final InvalidAccessTokenException exception) {
        response.setHeader("WWW-Authenticate", "Bearer error=\"" + exception.getType() + "\"");
    }

    /**
     * Escapes a callback name for JSONP responses.
     * Only allows alphanumeric characters and underscore to prevent
     * prototype pollution attacks in JavaScript environments.
     *
     * @param callbackName The callback name
     * @return The escaped callback name
     */
    public static String escapeCallbackName(final String callbackName) {
        if (callbackName == null) {
            return null;
        }
        // Only allow alphanumeric and underscore characters (no dots for security)
        return "/**/" + callbackName.replaceAll("[^0-9a-zA-Z_]", StringUtil.EMPTY);
    }

    /**
     * Gets the ObjectMapper instance.
     *
     * @return The ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}
