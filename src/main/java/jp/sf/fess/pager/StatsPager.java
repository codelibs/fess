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

package jp.sf.fess.pager;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import jp.sf.fess.Constants;

import org.codelibs.core.util.StringUtil;

import com.ibm.icu.text.SimpleDateFormat;

public class StatsPager implements Serializable {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_PAGE_SIZE = 20;

    public static final int DEFAULT_CURRENT_PAGE_NUMBER = 1;

    private int allRecordCount;

    private int allPageCount;

    private boolean existPrePage;

    private boolean existNextPage;

    private List<Integer> pageNumberList;

    private int pageSize;

    private int currentPageNumber;

    public String reportType;

    public String startDate;

    public String startHour;

    public String startMin;

    public String endDate;

    public String endHour;

    public String endMin;

    public void clear() {
        pageSize = getDefaultPageSize();
        currentPageNumber = getDefaultCurrentPageNumber();
        reportType = null;
        resetStartDate();
        resetEndDate();
    }

    public Timestamp getFromRequestedTime() {
        if (StringUtil.isBlank(startDate)) {
            return null;
        }

        try {
            if (StringUtil.isBlank(startHour)) {
                startHour = null;
                startMin = null;
                final Date date = new SimpleDateFormat(DATE_TIME_FORMAT)
                        .parse(startDate + " 00:00");
                if (date != null) {
                    return new Timestamp(date.getTime());
                }
            } else {
                if (StringUtil.isBlank(startMin)) {
                    startMin = "0";
                }
                final Date date = new SimpleDateFormat(DATE_TIME_FORMAT)
                        .parse(startDate + " " + startHour + ":" + startMin);
                if (date != null) {
                    return new Timestamp(date.getTime());
                }
            }
        } catch (final ParseException e) {
            resetStartDate();
        }
        return null;
    }

    private void resetStartDate() {
        startDate = null;
        startHour = null;
        startMin = null;
    }

    public Timestamp getToRequestedTime() {
        if (StringUtil.isBlank(endDate)) {
            return null;
        }

        try {
            if (StringUtil.isBlank(endHour)) {
                endHour = null;
                endMin = null;
                final Date date = new SimpleDateFormat(DATE_TIME_FORMAT)
                        .parse(endDate + " 00:00");
                if (date != null) {
                    return new Timestamp(date.getTime());
                }
            } else {
                if (StringUtil.isBlank(endMin)) {
                    endMin = "0";
                }
                final Date date = new SimpleDateFormat(DATE_TIME_FORMAT)
                        .parse(endDate + " " + endHour + ":" + endMin);
                if (date != null) {
                    return new Timestamp(date.getTime());
                }
            }
        } catch (final ParseException e) {
            resetEndDate();
        }
        return null;
    }

    private void resetEndDate() {
        endDate = null;
        endHour = null;
        endMin = null;
    }

    protected int getDefaultPageSize() {
        return Constants.DEFAULT_ADMIN_PAGE_SIZE;
    }

    protected int getDefaultCurrentPageNumber() {
        return DEFAULT_CURRENT_PAGE_NUMBER;
    }

    public int getAllRecordCount() {
        return allRecordCount;
    }

    public void setAllRecordCount(final int allRecordCount) {
        this.allRecordCount = allRecordCount;
    }

    public int getAllPageCount() {
        return allPageCount;
    }

    public void setAllPageCount(final int allPageCount) {
        this.allPageCount = allPageCount;
    }

    public boolean isExistPrePage() {
        return existPrePage;
    }

    public void setExistPrePage(final boolean existPrePage) {
        this.existPrePage = existPrePage;
    }

    public boolean isExistNextPage() {
        return existNextPage;
    }

    public void setExistNextPage(final boolean existNextPage) {
        this.existNextPage = existNextPage;
    }

    public int getPageSize() {
        if (pageSize <= 0) {
            pageSize = getDefaultPageSize();
        }
        return pageSize;
    }

    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPageNumber() {
        if (currentPageNumber <= 0) {
            currentPageNumber = getDefaultCurrentPageNumber();
        }
        return currentPageNumber;
    }

    public void setCurrentPageNumber(final int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    public List<Integer> getPageNumberList() {
        return pageNumberList;
    }

    public void setPageNumberList(final List<Integer> pageNumberList) {
        this.pageNumberList = pageNumberList;
    }

}
