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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interface for managing web API request processing.
 * Implementations of this interface handle specific types of web API requests
 * by matching incoming requests and processing them accordingly.
 */
public interface WebApiManager {

    /**
     * Checks if the request matches this API manager.
     * @param request The HTTP servlet request.
     * @return True if the request matches, false otherwise.
     */
    boolean matches(HttpServletRequest request);

    /**
     * Processes the request through this API manager.
     * @param request The HTTP servlet request.
     * @param response The HTTP servlet response.
     * @param chain The filter chain.
     * @throws IOException If an input/output error occurs.
     * @throws ServletException If a servlet error occurs.
     */
    void process(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException;

}
