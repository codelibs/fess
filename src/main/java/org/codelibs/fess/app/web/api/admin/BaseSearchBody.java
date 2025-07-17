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
     * Gets the page size for search results.
     * @return The page size.
     */
    public int getPageSize() {
        if (size != null) {
            return size;
        }
        return ComponentUtil.getFessConfig().getPagingPageSizeAsInteger();
    }

    /**
     * Gets the current page number for search results.
     * @return The current page number.
     */
    public int getCurrentPageNumber() {
        if (page != null) {
            return page;
        }
        return Constants.DEFAULT_ADMIN_PAGE_NUMBER;
    }
}