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

import java.util.List;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.UserPager;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.es.user.cbean.UserCB;
import org.codelibs.fess.es.user.exbhv.UserBhv;
import org.codelibs.fess.es.user.exentity.User;
import org.codelibs.fess.exception.FessUserNotFoundException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;

public class UserService {

    @Resource
    protected UserBhv userBhv;

    @Resource
    protected FessLoginAssist fessLoginAssist;

    @Resource
    protected FessConfig fessConfig;

    public List<User> getUserList(final UserPager userPager) {

        final PagingResultBean<User> userList = userBhv.selectPage(cb -> {
            cb.paging(userPager.getPageSize(), userPager.getCurrentPageNumber());
            setupListCondition(cb, userPager);
        });

        // update pager
        BeanUtil.copyBeanToBean(userList, userPager, option -> option.include(Constants.PAGER_CONVERSION_RULE));
        userPager.setPageNumberList(userList.pageRange(op -> {
            op.rangeSize(fessConfig.getPagingPageRangeSizeAsInteger());
        }).createPageNumberList());

        return userList;
    }

    public OptionalEntity<User> getUser(final String id) {
        return userBhv.selectByPK(id).map(u -> ComponentUtil.getAuthenticationManager().load(u));
    }

    public OptionalEntity<User> getUserByName(final String username) {
        return userBhv.selectEntity(cb -> {
            cb.query().setName_Equal(username);
        });
    }

    public void store(final User user) {
        if (StringUtil.isBlank(user.getSurname())) {
            user.setSurname(user.getName());
        }

        ComponentUtil.getAuthenticationManager().insert(user);

        userBhv.insertOrUpdate(user, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    public void changePassword(final String username, final String password) {
        final boolean changed = ComponentUtil.getAuthenticationManager().changePassword(username, password);
        if (changed) {
            userBhv.selectEntity(cb -> cb.query().setName_Equal(username)).ifPresent(entity -> {
                final String encodedPassword = fessLoginAssist.encryptPassword(password);
                entity.setPassword(encodedPassword);
                userBhv.insertOrUpdate(entity, op -> op.setRefreshPolicy(Constants.TRUE));
            }).orElse(() -> {
                throw new FessUserNotFoundException(username);
            });
        }

    }

    public void delete(final User user) {
        ComponentUtil.getAuthenticationManager().delete(user);

        userBhv.delete(user, op -> {
            op.setRefreshPolicy(Constants.TRUE);
        });

    }

    protected void setupListCondition(final UserCB cb, final UserPager userPager) {
        if (userPager.id != null) {
            cb.query().docMeta().setId_Equal(userPager.id);
        }
        // TODO Long, Integer, String supported only.

        // setup condition
        cb.query().addOrderBy_Name_Asc();

        // search

    }

    public List<User> getAvailableUserList() {
        return userBhv.selectList(cb -> {
            cb.query().matchAll();
            cb.fetchFirst(fessConfig.getPageUserMaxFetchSizeAsInteger());
        });
    }

}
