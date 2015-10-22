package org.codelibs.fess.app.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.codelibs.fess.es.exbhv.FavoriteLogBhv;
import org.codelibs.fess.es.exbhv.UserInfoBhv;
import org.codelibs.fess.es.exentity.FavoriteLog;
import org.codelibs.fess.helper.SystemHelper;
import org.dbflute.cbean.result.ListResultBean;

public class FavoriteLogService {
    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected UserInfoBhv userInfoBhv;

    @Resource
    protected FavoriteLogBhv favoriteLogBhv;

    public boolean addUrl(final String userCode, final String url) {
        return userInfoBhv.selectEntity(cb -> {
            cb.query().setCode_Equal(userCode);
        }).map(userInfo -> {
            final FavoriteLog favoriteLog = new FavoriteLog();
            favoriteLog.setUserInfoId(userInfo.getId());
            favoriteLog.setUrl(url);
            favoriteLog.setCreatedTime(systemHelper.getCurrentTimeAsLong());
            favoriteLogBhv.insert(favoriteLog);
            return true;
        }).orElse(false);
    }

    public List<String> getUrlList(final String userCode, final List<String> urlList) {
        if (urlList.isEmpty()) {
            return urlList;
        }

        return userInfoBhv.selectEntity(cb -> {
            cb.query().setCode_Equal(userCode);
        }).map(userInfo -> {
            final ListResultBean<FavoriteLog> list = favoriteLogBhv.selectList(cb2 -> {
                cb2.query().setUserInfoId_Equal(userInfo.getId());
                cb2.query().setUrl_InScope(urlList);
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
