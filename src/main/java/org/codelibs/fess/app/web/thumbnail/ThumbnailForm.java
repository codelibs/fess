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
package org.codelibs.fess.app.web.thumbnail;

import java.util.HashMap;
import java.util.Map;

import org.lastaflute.web.validation.Required;

import jakarta.validation.constraints.Size;

/**
 * Form class for thumbnail request parameters.
 * Contains the document ID and query parameters needed to retrieve thumbnail images.
 */
public class ThumbnailForm {

    /**
     * The document ID for which to retrieve the thumbnail.
     */
    @Required
    @Size(max = 100)
    public String docId;

    /**
     * The query ID associated with the search request.
     */
    @Required
    public String queryId;

    /**
     * The search query string for error page display.
     */
    public String q;

    /**
     * The number of search results per page for error page display.
     */
    public String num;

    /**
     * The sort criteria for search results for error page display.
     */
    public String sort;

    /**
     * The language setting for error page display.
     */
    public String lang;

    /**
     * Additional search fields for error page display.
     */
    public Map<String, String[]> fields = new HashMap<>();

    /**
     * Default constructor for ThumbnailForm.
     */
    public ThumbnailForm() {
        // Default constructor
    }
}
