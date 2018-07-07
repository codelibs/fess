/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.service;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.SearchLogPager;
import org.codelibs.fess.es.log.exbhv.ClickLogBhv;
import org.codelibs.fess.es.log.exbhv.FavoriteLogBhv;
import org.codelibs.fess.es.log.exbhv.SearchLogBhv;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.cbean.result.PagingResultBean;

public class SearchLogService {

    @Resource
    private SearchLogBhv searchLogBhv;

    @Resource
    private ClickLogBhv clickLogBhv;

    @Resource
    private FavoriteLogBhv favoriteLogBhv;

    @Resource
    private SystemHelper systemHelper;

    @Resource
    protected FessConfig fessConfig;

    public void deleteBefore(final int days) {
        searchLogBhv.queryDelete(cb -> {
            cb.query().setRequestedAt_LessEqual(systemHelper.getCurrentTimeAsLocalDateTime().minusDays(days));
        });
    }

    public PagingResultBean<?> getSearchLogList(SearchLogPager pager) {
        final PagingResultBean<?> list;
        if (SearchLogPager.LOG_TYPE_CLICK.equalsIgnoreCase(pager.logType)) {
            list = clickLogBhv.selectPage(cb -> {
                cb.paging(pager.getPageSize(), pager.getCurrentPageNumber());
                cb.query().addOrderBy_RequestedAt_Desc();
            });
        } else if (SearchLogPager.LOG_TYPE_FAVORITE.equalsIgnoreCase(pager.logType)) {
            list = favoriteLogBhv.selectPage(cb -> {
                cb.paging(pager.getPageSize(), pager.getCurrentPageNumber());
                cb.query().addOrderBy_CreatedAt_Desc();
            });
        } else {
            list = searchLogBhv.selectPage(cb -> {
                cb.paging(pager.getPageSize(), pager.getCurrentPageNumber());
                cb.query().addOrderBy_RequestedAt_Desc();
            });
        }

        // update pager
        BeanUtil.copyBeanToBean(list, pager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        pager.setPageNumberList(list.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return list;
    }
}
