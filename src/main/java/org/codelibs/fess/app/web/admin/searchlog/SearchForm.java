/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
 * @author shinsuke
 */
public class SearchForm {

    public String logType;

    public String queryId;

    public String userSessionId;

    public String requestedTimeRange;

    public String accessType;

    public String size;

    public void setPageSize(final int size) {
        this.size = Integer.toString(size);
    }

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
