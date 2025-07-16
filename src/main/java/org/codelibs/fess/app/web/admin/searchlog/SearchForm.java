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
package org.codelibs.fess.app.web.admin.searchlog;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.pager.SearchLogPager;
import org.codelibs.fess.util.ComponentUtil;

/**
 * The search form for Search Log.
 */
public class SearchForm {

    /**
     * Default constructor for SearchForm.
     */
    public SearchForm() {
    }

    /**
     * The log type field for filtering search logs.
     */
    public String logType;

    /**
     * The query ID field for searching specific queries.
     */
    public String queryId;

    /**
     * The user session ID field for filtering logs by session.
     */
    public String userSessionId;

    /**
     * The requested time range field for filtering logs by date.
     */
    public String requestedTimeRange;

    /**
     * The access type field for filtering logs by access method.
     */
    public String accessType;

    /**
     * The size field for controlling page size.
     */
    public String size;

    /**
     * Sets the page size for search log results.
     *
     * @param size the page size to set
     */
    public void setPageSize(final int size) {
        this.size = Integer.toString(size);
    }

    /**
     * Gets the page size for search log results with validation.
     * Returns the default page size if the current size is invalid.
     *
     * @return the validated page size
     */
    public int getPageSize() {
        if (StringUtil.isBlank(size)) {
            return SearchLogPager.DEFAULT_PAGE_SIZE;
        }
        try {
            final int value = Integer.parseInt(size);
            if (value <= 0 || value > ComponentUtil.getFessConfig().getPageSearchlogMaxFetchSizeAsInteger()) {
                return SearchLogPager.DEFAULT_PAGE_SIZE;
            }
            return value;
        } catch (final NumberFormatException e) {
            // ignore
            return SearchLogPager.DEFAULT_PAGE_SIZE;
        }
    }
}
