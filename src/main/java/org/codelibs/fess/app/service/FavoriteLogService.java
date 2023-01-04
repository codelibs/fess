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
package org.codelibs.fess.app.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import javax.annotation.Resource;

import org.codelibs.fess.es.log.exbhv.FavoriteLogBhv;
import org.codelibs.fess.es.log.exbhv.UserInfoBhv;
import org.codelibs.fess.es.log.exentity.FavoriteLog;
import org.codelibs.fess.es.log.exentity.UserInfo;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.ListResultBean;

public class FavoriteLogService {
    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected UserInfoBhv userInfoBhv;

    @Resource
    protected FavoriteLogBhv favoriteLogBhv;

    @Resource
    protected FessConfig fessConfig;

    public boolean addUrl(final String userCode, final BiConsumer<UserInfo, FavoriteLog> favoriteLogLambda) {
        return userInfoBhv.selectByPK(userCode).map(userInfo -> {
            final FavoriteLog favoriteLog = new FavoriteLog();
            favoriteLogLambda.accept(userInfo, favoriteLog);
            favoriteLogBhv.insert(favoriteLog);
            if (fessConfig.isLoggingSearchUseLogfile()) {
                ComponentUtil.getSearchLogHelper().writeSearchLogEvent(favoriteLog);
            }
            return true;
        }).orElse(false);
    }

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
