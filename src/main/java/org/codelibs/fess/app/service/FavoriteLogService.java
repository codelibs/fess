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
package org.codelibs.fess.app.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.log.exbhv.FavoriteLogBhv;
import org.codelibs.fess.opensearch.log.exbhv.UserInfoBhv;
import org.codelibs.fess.opensearch.log.exentity.FavoriteLog;
import org.codelibs.fess.opensearch.log.exentity.UserInfo;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.ListResultBean;

import jakarta.annotation.Resource;

/**
 * Service class for managing favorite log operations.
 * Provides functionality to add URLs to user favorites and retrieve favorite URL lists.
 * This service handles the persistence and retrieval of favorite log entries in the search system.
 */
public class FavoriteLogService {

    /**
     * Default constructor.
     */
    public FavoriteLogService() {
        // Default constructor
    }

    /** System helper for common system operations and utilities. */
    @Resource
    protected SystemHelper systemHelper;

    /** Behavior class for user information database operations. */
    @Resource
    protected UserInfoBhv userInfoBhv;

    /** Behavior class for favorite log database operations. */
    @Resource
    protected FavoriteLogBhv favoriteLogBhv;

    /** Configuration settings for the Fess search system. */
    @Resource
    protected FessConfig fessConfig;

    /**
     * The outcome of adding a URL to a user's favorite list.
     */
    public enum FavoriteResult {
        /** The URL was added to the user's favorites. */
        ADDED,
        /** The user already had the URL in their favorites, and nothing was written. */
        ALREADY_ADDED,
        /** The user code resolved to no user, and nothing was written. */
        NO_SUCH_USER
    }

    /**
     * Adds a URL to a user's favorite list.
     * This method looks up the user by their code and creates a new favorite log entry
     * using the provided lambda function to populate the favorite log data.
     * <p>
     * A URL the user has already marked is not written again. favorite_count is a documented
     * sort key, so a repeated request -- a double click, or a client retrying -- would otherwise
     * move a document up the ranking without anyone else favoriting it. The index is refreshed
     * after an insert so that a repeat sees the entry that was just written.
     * </p>
     *
     * @param userCode the unique code identifying the user
     * @param favoriteLogLambda a lambda function that accepts UserInfo and FavoriteLog to populate the favorite log data
     * @return what happened: the URL was added, the user already had it, or the user was not found
     */
    public FavoriteResult addUrl(final String userCode, final BiConsumer<UserInfo, FavoriteLog> favoriteLogLambda) {
        return userInfoBhv.selectByPK(userCode).map(userInfo -> {
            final FavoriteLog favoriteLog = new FavoriteLog();
            favoriteLogLambda.accept(userInfo, favoriteLog);
            if (favoriteLogBhv.selectCount(cb -> {
                cb.query().setUserInfoId_Equal(userInfo.getId());
                cb.query().setUrl_Equal(favoriteLog.getUrl());
            }) > 0) {
                return FavoriteResult.ALREADY_ADDED;
            }
            favoriteLogBhv.insert(favoriteLog);
            favoriteLogBhv.refresh();
            if (fessConfig.isLoggingSearchUseLogfile()) {
                ComponentUtil.getSearchLogHelper().writeSearchLogEvent(favoriteLog);
            }
            return FavoriteResult.ADDED;
        }).orElse(FavoriteResult.NO_SUCH_USER);
    }

    /**
     * Retrieves a list of URLs that are in the user's favorites from the provided URL list.
     * This method filters the input URL list to return only those URLs that the specified user
     * has marked as favorites.
     *
     * @param userCode the unique code identifying the user
     * @param urlList the list of URLs to check against the user's favorites
     * @return a list of URLs from the input list that are in the user's favorites, or an empty list if the user is not found or has no matching favorites
     */
    public List<String> getUrlList(final String userCode, final List<String> urlList) {
        if (urlList.isEmpty()) {
            return urlList;
        }

        return userInfoBhv.selectByPK(userCode).map(userInfo -> {
            final ListResultBean<FavoriteLog> list = favoriteLogBhv.selectList(cb2 -> {
                cb2.query().setUserInfoId_Equal(userInfo.getId());
                cb2.query().setUrl_InScope(urlList);
                cb2.fetchFirst(fessConfig.getPageFavoriteLogMaxFetchSizeAsInteger());
            });
            if (!list.isEmpty()) {
                final List<String> newUrlList = new ArrayList<>(list.size());
                for (final FavoriteLog favoriteLog : list) {
                    newUrlList.add(favoriteLog.getUrl());
                }
                return newUrlList;
            }
            return Collections.<String> emptyList();
        }).orElse(Collections.<String> emptyList());

    }

}
