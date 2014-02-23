/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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
import java.sql.Timestamp;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.fess.crud.service.BsUserInfoService;
import jp.sf.fess.db.cbean.FavoriteLogCB;
import jp.sf.fess.db.cbean.SearchLogCB;
import jp.sf.fess.db.cbean.UserInfoCB;
import jp.sf.fess.db.exbhv.FavoriteLogBhv;
import jp.sf.fess.db.exbhv.SearchLogBhv;
import jp.sf.fess.db.exentity.SearchLog;
import jp.sf.fess.db.exentity.UserInfo;
import jp.sf.fess.pager.UserInfoPager;

import org.seasar.dbflute.bhv.DeleteOption;
import org.seasar.dbflute.bhv.UpdateOption;
import org.codelibs.core.util.StringUtil;

public class UserInfoService extends BsUserInfoService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected SearchLogBhv searchLogBhv;

    @Resource
    protected FavoriteLogBhv favoriteLogBhv;

    @Override
    protected void setupListCondition(final UserInfoCB cb,
            final UserInfoPager userInfoPager) {
        // setup condition
        cb.query().addOrderBy_UpdatedTime_Desc();

        // search
        if (StringUtil.isNotBlank(userInfoPager.code)) {
            cb.query().setCode_Equal(userInfoPager.code);
        }
    }

    @Override
    protected void setupEntityCondition(final UserInfoCB cb,
            final Map<String, String> keys) {
        super.setupEntityCondition(cb, keys);

        // setup condition

    }

    @Override
    protected void setupStoreCondition(final UserInfo userInfo) {
        super.setupStoreCondition(userInfo);

        // setup condition

    }

    @Override
    protected void setupDeleteCondition(final UserInfo userInfo) {
        final Long userInfoId = userInfo.getId();
        final SearchLogCB cb1 = new SearchLogCB();
        cb1.query().setUserId_Equal(userInfoId);
        final SearchLog searchLog = new SearchLog();
        searchLog.setUserId(null);
        searchLogBhv.queryUpdate(searchLog, cb1);

        final FavoriteLogCB cb2 = new FavoriteLogCB();
        cb2.query().setUserId_Equal(userInfoId);
        favoriteLogBhv.queryDelete(cb2);

        super.setupDeleteCondition(userInfo);

        // setup condition

    }

    public UserInfo getUserInfo(final String userCode) {
        final UserInfoCB cb = new UserInfoCB();
        cb.query().setCode_Equal(userCode);
        return userInfoBhv.selectEntity(cb);
    }

    public void deleteAll(final UserInfoPager userInfoPager) {
        deleteInternal(userInfoPager);
    }

    public void deleteBefore(final int days) {
        final UserInfoPager userInfoPager = new UserInfoPager();
        userInfoPager.updatedTimeBefore = new Timestamp(
                System.currentTimeMillis() - days * 24L * 60L * 60L * 1000L);
        deleteInternal(userInfoPager);
    }

    protected void deleteInternal(final UserInfoPager userInfoPager) {
        final UserInfoCB cb = new UserInfoCB();
        final SearchLogCB cb1 = new SearchLogCB();
        final FavoriteLogCB cb2 = new FavoriteLogCB();

        boolean hasCb = false;
        // search
        if (StringUtil.isNotBlank(userInfoPager.code)) {
            cb.query().setCode_Equal(userInfoPager.code);
            cb1.query().queryUserInfo().setCode_Equal(userInfoPager.code);
            cb2.query().queryUserInfo().setCode_Equal(userInfoPager.code);
            hasCb = true;
        }

        if (userInfoPager.updatedTimeBefore != null) {
            cb.query()
                    .setUpdatedTime_LessEqual(userInfoPager.updatedTimeBefore);
            cb1.query().queryUserInfo()
                    .setUpdatedTime_LessEqual(userInfoPager.updatedTimeBefore);
            cb2.query().queryUserInfo()
                    .setUpdatedTime_LessEqual(userInfoPager.updatedTimeBefore);
            hasCb = true;
        }

        if (hasCb) {
            final SearchLog searchLog = new SearchLog();
            searchLog.setUserId(null);
            searchLogBhv.queryUpdate(searchLog, cb1);

            favoriteLogBhv.queryDelete(cb2);
        } else {
            final SearchLog searchLog = new SearchLog();
            searchLog.setUserId(null);
            searchLogBhv.varyingQueryUpdate(searchLog, cb1,
                    new UpdateOption<SearchLogCB>().allowNonQueryUpdate());

            favoriteLogBhv.varyingQueryDelete(cb2,
                    new DeleteOption<FavoriteLogCB>().allowNonQueryDelete());
        }

        userInfoBhv.varyingQueryDelete(cb,
                new DeleteOption<UserInfoCB>().allowNonQueryDelete());
    }

}
