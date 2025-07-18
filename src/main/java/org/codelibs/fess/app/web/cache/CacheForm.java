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
package org.codelibs.fess.app.web.cache;

import java.util.HashMap;
import java.util.Map;

import org.lastaflute.web.validation.Required;

import jakarta.validation.constraints.Size;

/**
 * Form class for cache-related operations.
 * Contains parameters for document caching and error page display.
 */
public class CacheForm {

    /** Document ID for cache operations. */
    @Required
    @Size(max = 100)
    public String docId;

    /** Highlight query parameters. */
    public String[] hq;

    /** Search query parameter for error page. */
    public String q;

    /** Number of results parameter for error page. */
    public String num;

    /** Sort parameter for error page. */
    public String sort;

    /** Language parameter for error page. */
    public String lang;

    /** Additional fields map for cache operations. */
    public Map<String, String[]> fields = new HashMap<>();

    /**
     * Default constructor for CacheForm.
     */
    public CacheForm() {
        super();
    }
}
