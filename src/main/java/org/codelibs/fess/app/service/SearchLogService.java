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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.SearchLogPager;
import org.codelibs.fess.es.log.exbhv.ClickLogBhv;
import org.codelibs.fess.es.log.exbhv.FavoriteLogBhv;
import org.codelibs.fess.es.log.exbhv.SearchLogBhv;
import org.codelibs.fess.es.log.exentity.ClickLog;
import org.codelibs.fess.es.log.exentity.FavoriteLog;
import org.codelibs.fess.es.log.exentity.SearchLog;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.taglib.FessFunctions;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

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

    public PagingResultBean<?> getSearchLogList(final SearchLogPager pager) {
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

    public OptionalEntity<?> getSearchLog(final String logType, final String id) {
        if (SearchLogPager.LOG_TYPE_CLICK.equalsIgnoreCase(logType)) {
            return clickLogBhv.selectByPK(id);
        } else if (SearchLogPager.LOG_TYPE_FAVORITE.equalsIgnoreCase(logType)) {
            return favoriteLogBhv.selectByPK(id);
        } else {
            return searchLogBhv.selectByPK(id);
        }
    }

    public Map<String, String> getSearchLogMap(final String logType, final String id) {
        if (SearchLogPager.LOG_TYPE_CLICK.equalsIgnoreCase(logType)) {
            return clickLogBhv.selectByPK(id).map(e -> {
                final Map<String, String> params = new LinkedHashMap<>();
                params.put("ID", e.getId());
                params.put("Query ID", e.getQueryId());
                params.put("Doc ID", e.getDocId());
                params.put("User Session ID", e.getUserSessionId());
                params.put("URL", e.getUrl());
                params.put("URL ID", e.getUrlId());
                params.put("Order", toNumberString(e.getOrder()));
                params.put("Query Requested Time", FessFunctions.formatDate(e.getQueryRequestedAt()));
                params.put("Requested Time", FessFunctions.formatDate(e.getRequestedAt()));
                return params;
            }).get();
        } else if (SearchLogPager.LOG_TYPE_FAVORITE.equalsIgnoreCase(logType)) {
            return favoriteLogBhv.selectByPK(id).map(e -> {
                final Map<String, String> params = new LinkedHashMap<>();
                params.put("ID", e.getId());
                params.put("Query ID", e.getQueryId());
                params.put("Doc ID", e.getDocId());
                params.put("User Info ID", e.getUserInfoId());
                params.put("URL", e.getUrl());
                params.put("Created Time", FessFunctions.formatDate(e.getCreatedAt()));
                params.put("Requested Time", FessFunctions.formatDate(e.getRequestedAt()));
                return params;
            }).get();
        } else {
            return searchLogBhv.selectByPK(id).map(e -> {
                final Map<String, String> params = new LinkedHashMap<>();
                params.put("ID", e.getId());
                params.put("Query ID", e.getQueryId());
                params.put("User Info ID", e.getUserInfoId());
                params.put("User Session ID", e.getUserSessionId());
                params.put("Access Type", e.getAccessType());
                params.put("Search Word", e.getSearchWord());
                params.put("Requested Time", FessFunctions.formatDate(e.getRequestedAt()));
                params.put("Query Time", toNumberString(e.getQueryTime()));
                params.put("Response Time", toNumberString(e.getResponseTime()));
                params.put("Hit Count", toNumberString(e.getHitCount()));
                params.put("Offset", toNumberString(e.getQueryOffset()));
                params.put("Page Size", toNumberString(e.getQueryPageSize()));
                params.put("Client IP", e.getClientIp());
                params.put("Referer", e.getReferer());
                params.put("Languages", e.getLanguages());
                params.put("Virtual Host", e.getVirtualHost());
                params.put("Roles", e.getRoles() != null ? String.join(" ", e.getRoles()) : StringUtil.EMPTY);
                params.put("User Agent", e.getUserAgent());
                e.getSearchFieldLogList().stream().forEach(p -> {
                    params.put(p.getFirst(), p.getSecond());
                });
                return params;
            }).get();
        }
    }

    private String toNumberString(final Number value) {
        return value != null ? value.toString() : StringUtil.EMPTY;
    }

    public void deleteSearchLog(final Object e) {
        if (e instanceof ClickLog) {
            clickLogBhv.delete((ClickLog) e);
        } else if (e instanceof FavoriteLog) {
            favoriteLogBhv.delete((FavoriteLog) e);
        } else if (e instanceof SearchLog) {
            searchLogBhv.delete((SearchLog) e);
        } else {
            throw new FessSystemException("Unknown log entity: " + e);
        }
    }
}
