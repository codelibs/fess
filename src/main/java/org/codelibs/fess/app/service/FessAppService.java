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
package org.codelibs.fess.app.service;

/**
 * Abstract base class for Fess application services.
 * This class provides common functionality and utilities for service implementations
 * throughout the Fess search application.
 */
public abstract class FessAppService {

    /**
     * Default constructor.
     */
    public FessAppService() {
        // Default constructor
    }

    /**
     * Wraps a query string with wildcard characters to enable partial matching.
     * This method ensures that the query string is surrounded by asterisks (*)
     * to support prefix and suffix matching in search operations.
     *
     * @param query the query string to wrap with wildcards
     * @return the wrapped query string with leading and trailing asterisks
     */
    protected static String wrapQuery(final String query) {
        final StringBuilder sb = new StringBuilder();
        if (!query.startsWith("*")) {
            sb.append("*");
        }
        sb.append(query);
        if (!query.endsWith("*")) {
            sb.append("*");
        }
        return sb.toString();
    }

}
