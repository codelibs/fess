/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package org.codelibs.fess.pager;

import org.codelibs.fess.Constants;
import org.codelibs.fess.crud.pager.BsSearchLogPager;

public class SearchLogPager extends BsSearchLogPager {

    private static final long serialVersionUID = 1L;

    public String searchWord;

    public String userCode;

    public String sortOrder;

    public String sortField;

    public String startDate;

    public String startHour;

    public String startMin;

    public String endDate;

    public String endHour;

    public String endMin;

    public String startPage;

    public SearchLogPager() {
        super();
    }

    @Override
    public void clear() {
        super.clear();
        searchWord = null;
        userCode = null;
        sortOrder = null;
        sortField = null;
        startDate = null;
        startHour = null;
        startMin = null;
        endDate = null;
        endHour = null;
        endMin = null;
        startPage = null;
    }

    @Override
    protected int getDefaultPageSize() {
        return Constants.DEFAULT_ADMIN_PAGE_SIZE;
    }

    public String getReverseSortOrder() {
        if (Constants.ASC.equals(sortOrder)) {
            return Constants.DESC;
        } else if (Constants.DESC.equals(sortOrder)) {
            return Constants.ASC;
        }
        return Constants.ASC;
    }
}
