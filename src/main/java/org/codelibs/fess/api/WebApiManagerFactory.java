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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Factory class for managing and retrieving web API managers.
 * This factory maintains a collection of web API managers and provides
 * functionality to find the appropriate manager for incoming requests.
 */
public class WebApiManagerFactory {

    /**
     * Default constructor.
     */
    public WebApiManagerFactory() {
        // Default constructor
    }

    /**
     * Array of registered web API managers.
     */
    protected WebApiManager[] webApiManagers = {};

    /**
     * Adds a web API manager to the factory.
     *
     * @param webApiManager The web API manager to add
     */
    public void add(final WebApiManager webApiManager) {
        final List<WebApiManager> list = new ArrayList<>();
        Collections.addAll(list, webApiManagers);
        list.add(webApiManager);
        webApiManagers = list.toArray(new WebApiManager[list.size()]);
    }

    /**
     * Gets the appropriate web API manager for the given request.
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

}
