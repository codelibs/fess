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
package org.codelibs.fess.app.web.api.admin.webconfig;

import org.codelibs.fess.app.web.api.admin.BaseSearchBody;

/**
 * Search request body for web crawling configuration administration API.
 */
public class SearchBody extends BaseSearchBody {

    /**
     * Default constructor.
     */
    public SearchBody() {
        super();
    }

    /** Name of the web crawling configuration */
    public String name;

    /** URLs to crawl */
    public String urls;

    /** Description of the web crawling configuration */
    public String description;
}
