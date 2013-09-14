/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.crud.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.crud.CrudMessageException;
import jp.sf.fess.db.cbean.SearchLogCB;
import jp.sf.fess.db.exbhv.SearchLogBhv;
import jp.sf.fess.db.exentity.SearchLog;
import jp.sf.fess.pager.SearchLogPager;

import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsSearchLogService {

    @Resource
    protected SearchLogBhv searchLogBhv;

    public BsSearchLogService() {
        super();
    }

    public List<SearchLog> getSearchLogList(final SearchLogPager searchLogPager) {

        final SearchLogCB cb = new SearchLogCB();

        cb.fetchFirst(searchLogPager.getPageSize());
        cb.fetchPage(searchLogPager.getCurrentPageNumber());

        setupListCondition(cb, searchLogPager);

        final PagingResultBean<SearchLog> searchLogList = searchLogBhv
                .selectPage(cb);

        // update pager
        Beans.copy(searchLogList, searchLogPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        searchLogList.setPageRangeSize(5);
        searchLogPager.setPageNumberList(searchLogList.pageRange()
                .createPageNumberList());

        return searchLogList;
    }

    public SearchLog getSearchLog(final Map<String, String> keys) {
        final SearchLogCB cb = new SearchLogCB();

        cb.query().setId_Equal(Long.parseLong(keys.get("id")));
        // TODO Long, Integer, String supported only.

        setupEntityCondition(cb, keys);

        final SearchLog searchLog = searchLogBhv.selectEntity(cb);
        if (searchLog == null) {
            // TODO exception?
            return null;
        }

        return searchLog;
    }

    public void store(final SearchLog searchLog) throws CrudMessageException {
        setupStoreCondition(searchLog);

        searchLogBhv.insertOrUpdate(searchLog);

    }

    public void delete(final SearchLog searchLog) throws CrudMessageException {
        setupDeleteCondition(searchLog);

        searchLogBhv.delete(searchLog);

    }

    protected void setupListCondition(final SearchLogCB cb,
            final SearchLogPager searchLogPager) {

        if (searchLogPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(searchLogPager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final SearchLogCB cb,
            final Map<String, String> keys) {
    }

    protected void setupStoreCondition(final SearchLog searchLog) {
    }

    protected void setupDeleteCondition(final SearchLog searchLog) {
    }
}