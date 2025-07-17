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
package org.codelibs.fess.app.web.go;

import java.util.HashMap;
import java.util.Map;

import org.lastaflute.web.validation.Required;

import jakarta.validation.constraints.Size;

/**
 * Form class for handling "go" requests that redirect users to specific documents
 * or search results. This form captures the necessary parameters for document
 * access tracking and error page fallback information.
 *
 */
public class GoForm {

    /**
     * Default constructor for GoForm.
     */
    public GoForm() {
        super();
    }

    /**
     * Document identifier for the target document to redirect to.
     * This is required and limited to 100 characters.
     */
    @Required
    @Size(max = 100)
    public String docId;

    /**
     * Redirect target or return URL parameter.
     * This is required and limited to 10000 characters to accommodate long URLs.
     */
    @Size(max = 10000)
    @Required
    public String rt;

    /**
     * Hash value for security or validation purposes.
     * This field is optional and used for request verification.
     */
    public String hash;

    /**
     * Query identifier associated with the search that led to this document access.
     * This is required for tracking and analytics purposes.
     */
    @Required
    public String queryId;

    /**
     * Order or ranking position of the document in search results.
     * This optional field helps track which result position was clicked.
     */
    public Integer order;

    // for error page

    /**
     * Query string parameter for error page fallback.
     * Contains the original search query if redirection fails.
     */
    public String q;

    /**
     * Number of results parameter for error page fallback.
     * Specifies how many results to display if redirection fails.
     */
    public String num;

    /**
     * Sort parameter for error page fallback.
     * Defines the sorting order if redirection fails.
     */
    public String sort;

    /**
     * Language parameter for error page fallback.
     * Specifies the language preference if redirection fails.
     */
    public String lang;

    /**
     * Additional fields map for error page fallback.
     * Contains extra search parameters as key-value pairs if redirection fails.
     */
    public Map<String, String[]> fields = new HashMap<>();
}
