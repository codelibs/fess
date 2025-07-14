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
package org.codelibs.fess.app.web.error;

import java.util.HashMap;
import java.util.Map;

/**
 * Form class for handling error page data and search parameters.
 * This form captures the state of a search request when an error occurs,
 * allowing the error page to display relevant information and preserve search context.
 */
public class ErrorForm {

    /** Map of form fields and their validation error messages */
    public Map<String, String[]> fields = new HashMap<>();

    /** Search query parameter that caused the error */
    public String q;

    /** URL parameter associated with the error */
    public String url;

    /** Number of results parameter */
    public String num;

    /** Sort order parameter for search results */
    public String sort;

    /** Language parameter for search interface */
    public String lang;

    /**
     * Default constructor for ErrorForm.
     */
    public ErrorForm() {
        // Default constructor
    }
}
