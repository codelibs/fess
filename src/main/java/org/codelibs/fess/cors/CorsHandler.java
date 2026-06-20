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
package org.codelibs.fess.cors;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * Abstract base class for handling CORS (Cross-Origin Resource Sharing) requests.
 * Provides common CORS header constants and defines the processing interface.
 */
public abstract class CorsHandler {

    /**
     * Creates a new instance of CorsHandler.
     */
    public CorsHandler() {
        // Default constructor
    }

    /**
     * CORS header for specifying allowed origin.
     */
    protected static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    /**
     * CORS header for specifying allowed headers.
     */
    protected static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

    /**
     * CORS header for specifying allowed HTTP methods.
     */
    protected static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

    /**
     * CORS header for allowing private network access.
     */
    protected static final String ACCESS_CONTROL_ALLOW_PRIVATE_NETWORK = "Access-Control-Allow-Private-Network";

    /**
     * CORS header for allowing credentials.
     */
    protected static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

    /**
     * CORS header for specifying cache duration for preflight requests.
     */
    protected static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";

    /**
     * Literal value returned in {@code Access-Control-Allow-Origin} for wildcard (non-credentialed) CORS.
     */
    protected static final String ALLOW_ORIGIN_ALL = "*";

    /**
     * HTTP {@code Vary} response header name.
     */
    protected static final String VARY = "Vary";

    /**
     * HTTP {@code Origin} header name. Appended to {@code Vary} for every origin-bearing request.
     */
    protected static final String ORIGIN = "Origin";

    /**
     * Processes the CORS request by setting appropriate headers.
     *
     * @param origin the origin of the request
     * @param matchType how the origin matched the allow list (EXACT reflects the origin and may send credentials; WILDCARD returns literal "*")
     * @param request the servlet request
     * @param response the servlet response
     */
    public abstract void process(String origin, CorsMatchType matchType, ServletRequest request, ServletResponse response);

}
