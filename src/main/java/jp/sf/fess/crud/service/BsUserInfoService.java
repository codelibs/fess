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

package jp.sf.fess.crud.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.fess.crud.CommonConstants;
import jp.sf.fess.crud.CrudMessageException;
import jp.sf.fess.db.cbean.UserInfoCB;
import jp.sf.fess.db.exbhv.UserInfoBhv;
import jp.sf.fess.db.exentity.UserInfo;
import jp.sf.fess.pager.UserInfoPager;

import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsUserInfoService {

    @Resource
    protected UserInfoBhv userInfoBhv;

    public BsUserInfoService() {
        super();
    }

    public List<UserInfo> getUserInfoList(final UserInfoPager userInfoPager) {

        final UserInfoCB cb = new UserInfoCB();

        cb.fetchFirst(userInfoPager.getPageSize());
        cb.fetchPage(userInfoPager.getCurrentPageNumber());

        setupListCondition(cb, userInfoPager);

        final PagingResultBean<UserInfo> userInfoList = userInfoBhv
                .selectPage(cb);

        // update pager
        Beans.copy(userInfoList, userInfoPager)
                .includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        userInfoList.setPageRangeSize(5);
        userInfoPager.setPageNumberList(userInfoList.pageRange()
                .createPageNumberList());

        return userInfoList;
    }

    public UserInfo getUserInfo(final Map<String, String> keys) {
        final UserInfoCB cb = new UserInfoCB();

        cb.query().setId_Equal(Long.parseLong(keys.get("id")));
        // TODO Long, Integer, String supported only.

        setupEntityCondition(cb, keys);

        final UserInfo userInfo = userInfoBhv.selectEntity(cb);
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

    protected void setupListCondition(final UserInfoCB cb,
            final UserInfoPager userInfoPager) {

        if (userInfoPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(userInfoPager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final UserInfoCB cb,
            final Map<String, String> keys) {
    }

    protected void setupStoreCondition(final UserInfo userInfo) {
    }

    protected void setupDeleteCondition(final UserInfo userInfo) {
    }
}