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
     * Processes the CORS request by setting appropriate headers.
     *
     * @param origin the origin of the request
     * @param request the servlet request
     * @param response the servlet response
     */
    public abstract void process(String origin, ServletRequest request, ServletResponse response);

}
