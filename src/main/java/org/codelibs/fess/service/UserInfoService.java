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
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.db.cbean.UserInfoCB;
import org.codelibs.fess.db.exbhv.FavoriteLogBhv;
import org.codelibs.fess.db.exbhv.SearchLogBhv;
import org.codelibs.fess.db.exbhv.UserInfoBhv;
import org.codelibs.fess.db.exentity.SearchLog;
import org.codelibs.fess.db.exentity.UserInfo;
import org.codelibs.fess.pager.UserInfoPager;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public class UserInfoService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Resource
    protected SearchLogBhv searchLogBhv;

    @Resource
    protected FavoriteLogBhv favoriteLogBhv;

    @Resource
    protected UserInfoBhv userInfoBhv;

    public UserInfoService() {
        super();
    }

    public List<UserInfo> getUserInfoList(final UserInfoPager userInfoPager) {

        final PagingResultBean<UserInfo> userInfoList = userInfoBhv.selectPage(cb -> {
            cb.paging(userInfoPager.getPageSize(), userInfoPager.getCurrentPageNumber());
            setupListCondition(cb, userInfoPager);
        });

        // update pager
        Beans.copy(userInfoList, userInfoPager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        userInfoPager.setPageNumberList(userInfoList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return userInfoList;
    }

    public UserInfo getUserInfo(final Map<String, String> keys) {
        final UserInfo userInfo = userInfoBhv.selectEntity(cb -> {
            cb.query().setId_Equal(Long.parseLong(keys.get("id")));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (userInfo == null) {
            // TODO exception?
            return null;
        }

        return userInfo;
    }

    public void store(final UserInfo userInfo) throws CrudMessageException {
        setupStoreCondition(userInfo);

        userInfoBhv.insertOrUpdate(userInfo);

    }

    public void delete(final UserInfo userInfo) throws CrudMessageException {
        setupDeleteCondition(userInfo);

        userInfoBhv.delete(userInfo);

    }

    protected void setupListCondition(final UserInfoCB cb, final UserInfoPager userInfoPager) {
        // setup condition
        cb.query().addOrderBy_UpdatedTime_Desc();

        // search
        if (StringUtil.isNotBlank(userInfoPager.code)) {
            cb.query().setCode_Equal(userInfoPager.code);
        }
    }

    protected void setupEntityCondition(final UserInfoCB cb, final Map<String, String> keys) {

        // setup condition

    }

    protected void setupStoreCondition(final UserInfo userInfo) {

        // setup condition

    }

    protected void setupDeleteCondition(final UserInfo userInfo) {
        final Long userInfoId = userInfo.getId();
        final SearchLog searchLog = new SearchLog();
        searchLog.setUserId(null);
        searchLogBhv.queryUpdate(searchLog, cb1 -> {
            cb1.query().setUserId_Equal(userInfoId);
        });

        favoriteLogBhv.queryDelete(cb2 -> {
            cb2.query().setUserId_Equal(userInfoId);
        });

        // setup condition

    }

    public UserInfo getUserInfo(final String userCode) {
        return userInfoBhv.selectEntity(cb -> {
            cb.query().setCode_Equal(userCode);
        }).orElse(null);//TODO
    }

    public void deleteAll(final UserInfoPager userInfoPager) {
        deleteInternal(userInfoPager);
    }

    public void deleteBefore(final int days) {
        final UserInfoPager userInfoPager = new UserInfoPager();
        userInfoPager.updatedTimeBefore = ComponentUtil.getSystemHelper().getCurrentTime().minusDays(days);
        deleteInternal(userInfoPager);
    }

    protected void deleteInternal(final UserInfoPager userInfoPager) {

        final boolean hasCb = StringUtil.isNotBlank(userInfoPager.code) || userInfoPager.updatedTimeBefore != null;

        if (hasCb) {
            final SearchLog searchLog = new SearchLog();
            searchLog.setUserId(null);
            searchLogBhv.queryUpdate(searchLog, cb1 -> {
                if (StringUtil.isNotBlank(userInfoPager.code)) {
                    cb1.query().queryUserInfo().setCode_Equal(userInfoPager.code);
                }

                if (userInfoPager.updatedTimeBefore != null) {
                    cb1.query().queryUserInfo().setUpdatedTime_LessEqual(userInfoPager.updatedTimeBefore);
                }
            });

            favoriteLogBhv.queryDelete(cb2 -> {
                if (StringUtil.isNotBlank(userInfoPager.code)) {
                    cb2.query().queryUserInfo().setCode_Equal(userInfoPager.code);
                }

                if (userInfoPager.updatedTimeBefore != null) {
                    cb2.query().queryUserInfo().setUpdatedTime_LessEqual(userInfoPager.updatedTimeBefore);
                }
            });
        } else {
            final SearchLog searchLog = new SearchLog();
            searchLog.setUserId(null);
            searchLogBhv.varyingQueryUpdate(searchLog, cb1 -> {
                if (StringUtil.isNotBlank(userInfoPager.code)) {
                    cb1.query().queryUserInfo().setCode_Equal(userInfoPager.code);
                }

                if (userInfoPager.updatedTimeBefore != null) {
                    cb1.query().queryUserInfo().setUpdatedTime_LessEqual(userInfoPager.updatedTimeBefore);
                }
            }, op -> op.allowNonQueryUpdate());

            favoriteLogBhv.varyingQueryDelete(cb2 -> {
                if (StringUtil.isNotBlank(userInfoPager.code)) {
                    cb2.query().queryUserInfo().setCode_Equal(userInfoPager.code);
                }

                if (userInfoPager.updatedTimeBefore != null) {
                    cb2.query().queryUserInfo().setUpdatedTime_LessEqual(userInfoPager.updatedTimeBefore);
                }
            }, op -> op.allowNonQueryDelete());
        }

        userInfoBhv.varyingQueryDelete(cb -> {
            if (StringUtil.isNotBlank(userInfoPager.code)) {
                cb.query().setCode_Equal(userInfoPager.code);
            }

            if (userInfoPager.updatedTimeBefore != null) {
                cb.query().setUpdatedTime_LessEqual(userInfoPager.updatedTimeBefore);
            }
        }, op -> op.allowNonQueryDelete());
    }
}
