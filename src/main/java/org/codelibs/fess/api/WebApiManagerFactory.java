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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Factory class for managing and retrieving web API managers.
 * This factory maintains a thread-safe collection of web API managers and provides
 * functionality to find the appropriate manager for incoming requests.
 *
 * Uses CopyOnWriteArrayList for thread-safe iteration during request matching
 * while allowing concurrent additions during initialization.
 */
public class WebApiManagerFactory {

    /**
     * Thread-safe list of registered web API managers.
     * Uses CopyOnWriteArrayList for safe iteration during request matching.
     */
    protected final List<WebApiManager> webApiManagers = new CopyOnWriteArrayList<>();

    /**
     * Default constructor.
     */
    public WebApiManagerFactory() {
        // Default constructor
    }

    /**
     * Adds a web API manager to the factory.
     * This method is thread-safe and can be called during initialization.
     *
     * @param webApiManager The web API manager to add
     * @throws IllegalArgumentException if webApiManager is null
     */
    public void add(final WebApiManager webApiManager) {
        if (webApiManager == null) {
            throw new IllegalArgumentException("webApiManager must not be null");
        }
        webApiManagers.add(webApiManager);
    }

    /**
     * Gets the appropriate web API manager for the given request.
     * Returns the first manager that matches the request.
     *
     * @param request The HTTP servlet request
     * @return The matching web API manager, or null if no match found
     */
    public WebApiManager get(final HttpServletRequest request) {
        for (final WebApiManager webApiManager : webApiManagers) {
            if (webApiManager.matches(request)) {
                return webApiManager;
            }
        }
        return null;
    }

    /**
     * Gets the number of registered API managers.
     *
     * @return The number of registered managers
     */
    public int size() {
        return webApiManagers.size();
    }

}
