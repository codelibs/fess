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
package org.codelibs.fess.app.web.api.admin;

import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Base class for search request body objects in admin API.
 * Provides common pagination parameters for search operations.
 */
public class BaseSearchBody {

    /** The page size for search results. */
    public Integer size = ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();

    /** The page number for search results. */
    public Integer page = Constants.DEFAULT_ADMIN_PAGE_NUMBER;

    /**
     * Default constructor for BaseSearchBody.
     */
    public BaseSearchBody() {
        // Default constructor
    }

    /**
     * Gets the page size for search results. A size beyond the search engine's result window
     * ({@code indexer.max.result.window.size}) can never be answered, so it is cut to the window.
     * @return The page size.
     */
    public int getPageSize() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int pageSize = size != null ? size : fessConfig.getPagingPageSizeAsInteger();
        return Math.min(pageSize, getMaxResultWindow(fessConfig));
    }

    /**
     * Gets the current page number for search results. A page that would end beyond the search
     * engine's result window is replaced by the last page inside it, like the document report
     * screen does, instead of failing the whole request.
     * @return The current page number.
     */
    public int getCurrentPageNumber() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int currentPage = page != null ? page : Constants.DEFAULT_ADMIN_PAGE_NUMBER;
        // a size of 0 or less falls back to the pager's default, which is never larger than the configured paging size
        final int pageSize = getPageSize() > 0 ? getPageSize() : fessConfig.getPagingPageSizeAsInteger();
        final int maxPage = Math.max(1, getMaxResultWindow(fessConfig) / Math.max(1, pageSize));
        return Math.min(currentPage, maxPage);
    }

    private int getMaxResultWindow(final FessConfig fessConfig) {
        return Math.max(1, fessConfig.getIndexerMaxResultWindowSizeAsInteger());
    }
}
