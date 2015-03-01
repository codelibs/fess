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

package org.codelibs.fess.crud.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.db.cbean.FavoriteLogCB;
import org.codelibs.fess.db.exbhv.FavoriteLogBhv;
import org.codelibs.fess.db.exentity.FavoriteLog;
import org.codelibs.fess.pager.FavoriteLogPager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsFavoriteLogService {

    @Resource
    protected FavoriteLogBhv favoriteLogBhv;

    public BsFavoriteLogService() {
        super();
    }

    public List<FavoriteLog> getFavoriteLogList(
            final FavoriteLogPager favoriteLogPager) {

        final PagingResultBean<FavoriteLog> favoriteLogList = favoriteLogBhv
                .selectPage(cb -> {
                    cb.paging(favoriteLogPager.getPageSize(),
                            favoriteLogPager.getCurrentPageNumber());
                    setupListCondition(cb, favoriteLogPager);
                });

        // update pager
        Beans.copy(favoriteLogList, favoriteLogPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        favoriteLogPager.setPageNumberList(favoriteLogList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return favoriteLogList;
    }

    public FavoriteLog getFavoriteLog(final Map<String, String> keys) {
        final FavoriteLog favoriteLog = favoriteLogBhv.selectEntity(cb -> {
            cb.query().setId_Equal(Long.parseLong(keys.get("id")));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (favoriteLog == null) {
            // TODO exception?
            return null;
        }

        return favoriteLog;
    }

    public void store(final FavoriteLog favoriteLog)
            throws CrudMessageException {
        setupStoreCondition(favoriteLog);

        favoriteLogBhv.insertOrUpdate(favoriteLog);

    }

    public void delete(final FavoriteLog favoriteLog)
            throws CrudMessageException {
        setupDeleteCondition(favoriteLog);

        favoriteLogBhv.delete(favoriteLog);

    }

    protected void setupListCondition(final FavoriteLogCB cb,
            final FavoriteLogPager favoriteLogPager) {

        if (favoriteLogPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(favoriteLogPager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final FavoriteLogCB cb,
            final Map<String, String> keys) {
    }

    protected void setupStoreCondition(final FavoriteLog favoriteLog) {
    }

    protected void setupDeleteCondition(final FavoriteLog favoriteLog) {
    }
}