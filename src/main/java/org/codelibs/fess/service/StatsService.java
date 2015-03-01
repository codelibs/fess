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

package org.codelibs.fess.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.db.bsbhv.BsClickLogBhv;
import org.codelibs.fess.db.bsbhv.BsFavoriteLogBhv;
import org.codelibs.fess.db.bsbhv.BsSearchLogBhv;
import org.codelibs.fess.db.exbhv.ClickLogBhv;
import org.codelibs.fess.db.exbhv.FavoriteLogBhv;
import org.codelibs.fess.db.exbhv.SearchLogBhv;
import org.codelibs.fess.db.exbhv.pmbean.ClickUrlRankingPmb;
import org.codelibs.fess.db.exbhv.pmbean.ClientIpRankingPmb;
import org.codelibs.fess.db.exbhv.pmbean.FavoriteUrlRankingPmb;
import org.codelibs.fess.db.exbhv.pmbean.RefererRankingPmb;
import org.codelibs.fess.db.exbhv.pmbean.SearchFieldRankingPmb;
import org.codelibs.fess.db.exbhv.pmbean.SearchQueryRankingPmb;
import org.codelibs.fess.db.exbhv.pmbean.SearchWordRankingPmb;
import org.codelibs.fess.db.exbhv.pmbean.SolrQueryRankingPmb;
import org.codelibs.fess.db.exbhv.pmbean.UserAgentRankingPmb;
import org.codelibs.fess.db.exentity.customize.ClickUrlRanking;
import org.codelibs.fess.db.exentity.customize.ClientIpRanking;
import org.codelibs.fess.db.exentity.customize.FavoriteUrlRanking;
import org.codelibs.fess.db.exentity.customize.RefererRanking;
import org.codelibs.fess.db.exentity.customize.SearchFieldRanking;
import org.codelibs.fess.db.exentity.customize.SearchQueryRanking;
import org.codelibs.fess.db.exentity.customize.SearchWordRanking;
import org.codelibs.fess.db.exentity.customize.SolrQueryRanking;
import org.codelibs.fess.db.exentity.customize.UserAgentRanking;
import org.codelibs.fess.pager.StatsPager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public class StatsService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected SearchLogBhv searchLogBhv;

    @Resource
    protected ClickLogBhv clicklogBhv;

    @Resource
    protected SearchFieldLogService searchFieldLogService;

    @Resource
    protected FavoriteLogBhv favoriteLogBhv;

    public List<Map<String, Object>> getStatsList(final StatsPager statsPager) {
        if ("searchWord".equals(statsPager.reportType)) {
            return getSearchWordStatsList(statsPager);
        } else if ("query".equals(statsPager.reportType)) {
            return getSearchQueryStatsList(statsPager);
        } else if ("solrQuery".equals(statsPager.reportType)) {
            return getSolrQueryStatsList(statsPager);
        } else if ("userAgent".equals(statsPager.reportType)) {
            return getUserAgentStatsList(statsPager);
        } else if ("referer".equals(statsPager.reportType)) {
            return getRefererStatsList(statsPager);
        } else if ("clientIp".equals(statsPager.reportType)) {
            return getClientIpStatsList(statsPager);
        } else if ("clickUrl".equals(statsPager.reportType)) {
            return getClickUrlStatsList(statsPager);
        } else if ("favoriteUrl".equals(statsPager.reportType)) {
            return getFavoriteUrlStatsList(statsPager);
        } else if (validReportType(statsPager.reportType)) {
            return getGroupedFieldStatsList(statsPager);
        } else {
            statsPager.reportType = "searchWord";
            return getSearchWordStatsList(statsPager);
        }
    }

    protected boolean validReportType(final String reportType) {
        for (final String type : searchFieldLogService.getGroupedFieldNames()) {
            if (type.equals(reportType)) {
                return true;
            }
        }
        return false;
    }

    protected List<Map<String, Object>> getSearchWordStatsList(
            final StatsPager statsPager) {

        final SearchWordRankingPmb pmb = new SearchWordRankingPmb();

        pmb.paging(statsPager.getPageSize(), statsPager.getCurrentPageNumber());

        pmb.setFromRequestedTime(statsPager.getFromRequestedTime());
        pmb.setToRequestedTime(statsPager.getToRequestedTime());

        final String path = BsSearchLogBhv.PATH_selectSearchWordRanking;
        final PagingResultBean<SearchWordRanking> statsList = searchLogBhv
                .outsideSql().selectPage(pmb);

        // update pager
        Beans.copy(statsList, statsPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        statsPager.setPageNumberList(statsList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        final List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        for (final SearchWordRanking entity : statsList) {
            final Map<String, Object> map = new HashMap<String, Object>();
            Beans.copy(entity, map).execute();
            mapList.add(map);
        }

        return mapList;
    }

    protected List<Map<String, Object>> getSearchQueryStatsList(
            final StatsPager statsPager) {

        final SearchQueryRankingPmb pmb = new SearchQueryRankingPmb();

        pmb.paging(statsPager.getPageSize(), statsPager.getCurrentPageNumber());

        pmb.setFromRequestedTime(statsPager.getFromRequestedTime());
        pmb.setToRequestedTime(statsPager.getToRequestedTime());

        final String path = BsSearchLogBhv.PATH_selectSearchQueryRanking;
        final PagingResultBean<SearchQueryRanking> statsList = searchLogBhv
                .outsideSql().selectPage(pmb);

        // update pager
        Beans.copy(statsList, statsPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        statsPager.setPageNumberList(statsList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        final List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        for (final SearchQueryRanking entity : statsList) {
            final Map<String, Object> map = new HashMap<String, Object>();
            Beans.copy(entity, map).execute();
            mapList.add(map);
        }

        return mapList;
    }

    protected List<Map<String, Object>> getSolrQueryStatsList(
            final StatsPager statsPager) {

        final SolrQueryRankingPmb pmb = new SolrQueryRankingPmb();

        pmb.paging(statsPager.getPageSize(), statsPager.getCurrentPageNumber());

        pmb.setFromRequestedTime(statsPager.getFromRequestedTime());
        pmb.setToRequestedTime(statsPager.getToRequestedTime());

        final String path = BsSearchLogBhv.PATH_selectSolrQueryRanking;
        final PagingResultBean<SolrQueryRanking> statsList = searchLogBhv
                .outsideSql().selectPage(pmb);

        // update pager
        Beans.copy(statsList, statsPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        statsPager.setPageNumberList(statsList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        final List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        for (final SolrQueryRanking entity : statsList) {
            final Map<String, Object> map = new HashMap<String, Object>();
            Beans.copy(entity, map).execute();
            mapList.add(map);
        }

        return mapList;
    }

    protected List<Map<String, Object>> getUserAgentStatsList(
            final StatsPager statsPager) {

        final UserAgentRankingPmb pmb = new UserAgentRankingPmb();

        pmb.paging(statsPager.getPageSize(), statsPager.getCurrentPageNumber());

        pmb.setFromRequestedTime(statsPager.getFromRequestedTime());
        pmb.setToRequestedTime(statsPager.getToRequestedTime());

        final String path = BsSearchLogBhv.PATH_selectUserAgentRanking;
        final PagingResultBean<UserAgentRanking> statsList = searchLogBhv
                .outsideSql().selectPage(pmb);

        // update pager
        Beans.copy(statsList, statsPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        statsPager.setPageNumberList(statsList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        final List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        for (final UserAgentRanking entity : statsList) {
            final Map<String, Object> map = new HashMap<String, Object>();
            Beans.copy(entity, map).execute();
            mapList.add(map);
        }

        return mapList;
    }

    protected List<Map<String, Object>> getRefererStatsList(
            final StatsPager statsPager) {

        final RefererRankingPmb pmb = new RefererRankingPmb();

        pmb.paging(statsPager.getPageSize(), statsPager.getCurrentPageNumber());

        pmb.setFromRequestedTime(statsPager.getFromRequestedTime());
        pmb.setToRequestedTime(statsPager.getToRequestedTime());

        final String path = BsSearchLogBhv.PATH_selectRefererRanking;
        final PagingResultBean<RefererRanking> statsList = searchLogBhv
                .outsideSql().selectPage(pmb);

        // update pager
        Beans.copy(statsList, statsPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        statsPager.setPageNumberList(statsList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        final List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        for (final RefererRanking entity : statsList) {
            final Map<String, Object> map = new HashMap<String, Object>();
            Beans.copy(entity, map).execute();
            mapList.add(map);
        }

        return mapList;
    }

    protected List<Map<String, Object>> getClientIpStatsList(
            final StatsPager statsPager) {

        final ClientIpRankingPmb pmb = new ClientIpRankingPmb();

        pmb.paging(statsPager.getPageSize(), statsPager.getCurrentPageNumber());

        pmb.setFromRequestedTime(statsPager.getFromRequestedTime());
        pmb.setToRequestedTime(statsPager.getToRequestedTime());

        final String path = BsSearchLogBhv.PATH_selectClientIpRanking;
        final PagingResultBean<ClientIpRanking> statsList = searchLogBhv
                .outsideSql().selectPage(pmb);

        // update pager
        Beans.copy(statsList, statsPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        statsPager.setPageNumberList(statsList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        final List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        for (final ClientIpRanking entity : statsList) {
            final Map<String, Object> map = new HashMap<String, Object>();
            Beans.copy(entity, map).execute();
            mapList.add(map);
        }

        return mapList;
    }

    protected List<Map<String, Object>> getClickUrlStatsList(
            final StatsPager statsPager) {

        final ClickUrlRankingPmb pmb = new ClickUrlRankingPmb();

        pmb.paging(statsPager.getPageSize(), statsPager.getCurrentPageNumber());

        pmb.setFromRequestedTime(statsPager.getFromRequestedTime());
        pmb.setToRequestedTime(statsPager.getToRequestedTime());

        final String path = BsClickLogBhv.PATH_selectClickUrlRanking;
        final PagingResultBean<ClickUrlRanking> statsList = clicklogBhv
                .outsideSql().selectPage(pmb);

        // update pager
        Beans.copy(statsList, statsPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        statsPager.setPageNumberList(statsList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        final List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        for (final ClickUrlRanking entity : statsList) {
            final Map<String, Object> map = new HashMap<String, Object>();
            Beans.copy(entity, map).execute();
            mapList.add(map);
        }

        return mapList;
    }

    protected List<Map<String, Object>> getFavoriteUrlStatsList(
            final StatsPager statsPager) {

        final FavoriteUrlRankingPmb pmb = new FavoriteUrlRankingPmb();

        pmb.paging(statsPager.getPageSize(), statsPager.getCurrentPageNumber());

        pmb.setFromCreatedTime(statsPager.getFromRequestedTime());
        pmb.setToCreatedTime(statsPager.getToRequestedTime());

        final String path = BsFavoriteLogBhv.PATH_selectFavoriteUrlRanking;
        final PagingResultBean<FavoriteUrlRanking> statsList = favoriteLogBhv
                .outsideSql().selectPage(pmb);

        // update pager
        Beans.copy(statsList, statsPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        statsPager.setPageNumberList(statsList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        final List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        for (final FavoriteUrlRanking entity : statsList) {
            final Map<String, Object> map = new HashMap<String, Object>();
            Beans.copy(entity, map).execute();
            mapList.add(map);
        }

        return mapList;
    }

    protected List<Map<String, Object>> getGroupedFieldStatsList(
            final StatsPager statsPager) {

        final SearchFieldRankingPmb pmb = new SearchFieldRankingPmb();

        pmb.paging(statsPager.getPageSize(), statsPager.getCurrentPageNumber());

        pmb.setSearchFieldName(statsPager.reportType);
        pmb.setFromRequestedTime(statsPager.getFromRequestedTime());
        pmb.setToRequestedTime(statsPager.getToRequestedTime());

        final String path = BsSearchLogBhv.PATH_selectSearchFieldRanking;
        final PagingResultBean<SearchFieldRanking> statsList = searchLogBhv
                .outsideSql().selectPage(pmb);

        // update pager
        Beans.copy(statsList, statsPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        statsPager.setPageNumberList(statsList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        final List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        for (final SearchFieldRanking entity : statsList) {
            final Map<String, Object> map = new HashMap<String, Object>();
            Beans.copy(entity, map).execute();
            mapList.add(map);
        }

        return mapList;
    }

}
