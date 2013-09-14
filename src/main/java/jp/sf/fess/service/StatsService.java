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

package jp.sf.fess.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.db.bsbhv.BsClickLogBhv;
import jp.sf.fess.db.bsbhv.BsFavoriteLogBhv;
import jp.sf.fess.db.bsbhv.BsSearchLogBhv;
import jp.sf.fess.db.exbhv.ClickLogBhv;
import jp.sf.fess.db.exbhv.FavoriteLogBhv;
import jp.sf.fess.db.exbhv.SearchLogBhv;
import jp.sf.fess.db.exbhv.pmbean.ClickUrlRankingPmb;
import jp.sf.fess.db.exbhv.pmbean.ClientIpRankingPmb;
import jp.sf.fess.db.exbhv.pmbean.FavoriteUrlRankingPmb;
import jp.sf.fess.db.exbhv.pmbean.RefererRankingPmb;
import jp.sf.fess.db.exbhv.pmbean.SearchFieldRankingPmb;
import jp.sf.fess.db.exbhv.pmbean.SearchQueryRankingPmb;
import jp.sf.fess.db.exbhv.pmbean.SearchWordRankingPmb;
import jp.sf.fess.db.exbhv.pmbean.SolrQueryRankingPmb;
import jp.sf.fess.db.exbhv.pmbean.UserAgentRankingPmb;
import jp.sf.fess.db.exentity.customize.ClickUrlRanking;
import jp.sf.fess.db.exentity.customize.ClientIpRanking;
import jp.sf.fess.db.exentity.customize.FavoriteUrlRanking;
import jp.sf.fess.db.exentity.customize.RefererRanking;
import jp.sf.fess.db.exentity.customize.SearchFieldRanking;
import jp.sf.fess.db.exentity.customize.SearchQueryRanking;
import jp.sf.fess.db.exentity.customize.SearchWordRanking;
import jp.sf.fess.db.exentity.customize.SolrQueryRanking;
import jp.sf.fess.db.exentity.customize.UserAgentRanking;
import jp.sf.fess.pager.StatsPager;

import org.seasar.dbflute.cbean.PagingResultBean;
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

        pmb.fetchFirst(statsPager.getPageSize());
        pmb.fetchPage(statsPager.getCurrentPageNumber());

        pmb.setFromRequestedTime(statsPager.getFromRequestedTime());
        pmb.setToRequestedTime(statsPager.getToRequestedTime());

        final String path = BsSearchLogBhv.PATH_selectSearchWordRanking;
        final PagingResultBean<SearchWordRanking> statsList = searchLogBhv
                .outsideSql().autoPaging()
                .selectPage(path, pmb, SearchWordRanking.class);

        // update pager
        Beans.copy(statsList, statsPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        statsList.setPageRangeSize(5);
        statsPager.setPageNumberList(statsList.pageRange()
                .createPageNumberList());

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

        pmb.fetchFirst(statsPager.getPageSize());
        pmb.fetchPage(statsPager.getCurrentPageNumber());

        pmb.setFromRequestedTime(statsPager.getFromRequestedTime());
        pmb.setToRequestedTime(statsPager.getToRequestedTime());

        final String path = BsSearchLogBhv.PATH_selectSearchQueryRanking;
        final PagingResultBean<SearchQueryRanking> statsList = searchLogBhv
                .outsideSql().autoPaging()
                .selectPage(path, pmb, SearchQueryRanking.class);

        // update pager
        Beans.copy(statsList, statsPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        statsList.setPageRangeSize(5);
        statsPager.setPageNumberList(statsList.pageRange()
                .createPageNumberList());

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

        pmb.fetchFirst(statsPager.getPageSize());
        pmb.fetchPage(statsPager.getCurrentPageNumber());

        pmb.setFromRequestedTime(statsPager.getFromRequestedTime());
        pmb.setToRequestedTime(statsPager.getToRequestedTime());

        final String path = BsSearchLogBhv.PATH_selectSolrQueryRanking;
        final PagingResultBean<SolrQueryRanking> statsList = searchLogBhv
                .outsideSql().autoPaging()
                .selectPage(path, pmb, SolrQueryRanking.class);

        // update pager
        Beans.copy(statsList, statsPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        statsList.setPageRangeSize(5);
        statsPager.setPageNumberList(statsList.pageRange()
                .createPageNumberList());

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

        pmb.fetchFirst(statsPager.getPageSize());
        pmb.fetchPage(statsPager.getCurrentPageNumber());

        pmb.setFromRequestedTime(statsPager.getFromRequestedTime());
        pmb.setToRequestedTime(statsPager.getToRequestedTime());

        final String path = BsSearchLogBhv.PATH_selectUserAgentRanking;
        final PagingResultBean<UserAgentRanking> statsList = searchLogBhv
                .outsideSql().autoPaging()
                .selectPage(path, pmb, UserAgentRanking.class);

        // update pager
        Beans.copy(statsList, statsPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        statsList.setPageRangeSize(5);
        statsPager.setPageNumberList(statsList.pageRange()
                .createPageNumberList());

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

        pmb.fetchFirst(statsPager.getPageSize());
        pmb.fetchPage(statsPager.getCurrentPageNumber());

        pmb.setFromRequestedTime(statsPager.getFromRequestedTime());
        pmb.setToRequestedTime(statsPager.getToRequestedTime());

        final String path = BsSearchLogBhv.PATH_selectRefererRanking;
        final PagingResultBean<RefererRanking> statsList = searchLogBhv
                .outsideSql().autoPaging()
                .selectPage(path, pmb, RefererRanking.class);

        // update pager
        Beans.copy(statsList, statsPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        statsList.setPageRangeSize(5);
        statsPager.setPageNumberList(statsList.pageRange()
                .createPageNumberList());

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

        pmb.fetchFirst(statsPager.getPageSize());
        pmb.fetchPage(statsPager.getCurrentPageNumber());

        pmb.setFromRequestedTime(statsPager.getFromRequestedTime());
        pmb.setToRequestedTime(statsPager.getToRequestedTime());

        final String path = BsSearchLogBhv.PATH_selectClientIpRanking;
        final PagingResultBean<ClientIpRanking> statsList = searchLogBhv
                .outsideSql().autoPaging()
                .selectPage(path, pmb, ClientIpRanking.class);

        // update pager
        Beans.copy(statsList, statsPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        statsList.setPageRangeSize(5);
        statsPager.setPageNumberList(statsList.pageRange()
                .createPageNumberList());

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

        pmb.fetchFirst(statsPager.getPageSize());
        pmb.fetchPage(statsPager.getCurrentPageNumber());

        pmb.setFromRequestedTime(statsPager.getFromRequestedTime());
        pmb.setToRequestedTime(statsPager.getToRequestedTime());

        final String path = BsClickLogBhv.PATH_selectClickUrlRanking;
        final PagingResultBean<ClickUrlRanking> statsList = clicklogBhv
                .outsideSql().autoPaging()
                .selectPage(path, pmb, ClickUrlRanking.class);

        // update pager
        Beans.copy(statsList, statsPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        statsList.setPageRangeSize(5);
        statsPager.setPageNumberList(statsList.pageRange()
                .createPageNumberList());

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

        pmb.fetchFirst(statsPager.getPageSize());
        pmb.fetchPage(statsPager.getCurrentPageNumber());

        pmb.setFromCreatedTime(statsPager.getFromRequestedTime());
        pmb.setToCreatedTime(statsPager.getToRequestedTime());

        final String path = BsFavoriteLogBhv.PATH_selectFavoriteUrlRanking;
        final PagingResultBean<FavoriteUrlRanking> statsList = favoriteLogBhv
                .outsideSql().autoPaging()
                .selectPage(path, pmb, FavoriteUrlRanking.class);

        // update pager
        Beans.copy(statsList, statsPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        statsList.setPageRangeSize(5);
        statsPager.setPageNumberList(statsList.pageRange()
                .createPageNumberList());

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

        pmb.fetchFirst(statsPager.getPageSize());
        pmb.fetchPage(statsPager.getCurrentPageNumber());

        pmb.setSearchFieldName(statsPager.reportType);
        pmb.setFromRequestedTime(statsPager.getFromRequestedTime());
        pmb.setToRequestedTime(statsPager.getToRequestedTime());

        final String path = BsSearchLogBhv.PATH_selectSearchFieldRanking;
        final PagingResultBean<SearchFieldRanking> statsList = searchLogBhv
                .outsideSql().autoPaging()
                .selectPage(path, pmb, SearchFieldRanking.class);

        // update pager
        Beans.copy(statsList, statsPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        statsList.setPageRangeSize(5);
        statsPager.setPageNumberList(statsList.pageRange()
                .createPageNumberList());

        final List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        for (final SearchFieldRanking entity : statsList) {
            final Map<String, Object> map = new HashMap<String, Object>();
            Beans.copy(entity, map).execute();
            mapList.add(map);
        }

        return mapList;
    }

}
