/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.base.login;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.RootAction;
import org.codelibs.fess.app.web.login.LoginAction;
import org.codelibs.fess.es.user.exbhv.UserBhv;
import org.codelibs.fess.es.user.exentity.User;
import org.codelibs.fess.exception.UserRoleLoginException;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.core.magic.async.AsyncManager;
import org.lastaflute.core.time.TimeManager;
import org.lastaflute.web.login.LoginHandlingResource;
import org.lastaflute.web.login.LoginManager;
import org.lastaflute.web.login.TypicalLoginAssist;
import org.lastaflute.web.login.exception.LoginRequiredException;
import org.lastaflute.web.login.option.LoginSpecifiedOption;

/**
 * @author jflute
 * @author shinsuke
 */
public class FessLoginAssist extends TypicalLoginAssist<String, FessUserBean, User> // #change_it also UserBean
        implements LoginManager {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private TimeManager timeManager;
    @Resource
    private AsyncManager asyncManager;
    @Resource
    private FessConfig fessConfig;
    @Resource
    private UserBhv userBhv;

    // ===================================================================================
    //                                                                           Find User
    //                                                                           =========
    @Override
    protected boolean doCheckUserLoginable(final String username, final String cipheredPassword) {
        return userBhv.selectCount(cb -> {
            cb.query().setName_Equal(username);
            cb.query().setPassword_Equal(cipheredPassword);
        }) > 0;
    }

    @Override
    protected OptionalEntity<User> doFindLoginUser(final String username, final String cipheredPassword) {
        return userBhv.selectEntity(cb -> {
            cb.query().setName_Equal(username);
            cb.query().setPassword_Equal(cipheredPassword);
        });
    }

    @Override
    protected OptionalEntity<User> doFindLoginUser(final String username) {
        return userBhv.selectEntity(cb -> {
            cb.query().setName_Equal(username);
        });
    }

    // ===================================================================================
    //                                                                       Login Process
    //                                                                       =============
    @Override
    protected FessUserBean createUserBean(final User user) {
        return new FessUserBean(user);
    }

    @Override
    protected OptionalThing<String> getCookieRememberMeKey() {
        // example to use remember-me
        //return OptionalThing.of(fessConfig.getCookieRememberMeFessKey());
        return OptionalThing.empty();
    }

    @Override
    protected void saveLoginHistory(final User user, final FessUserBean userBean, final LoginSpecifiedOption option) {
        asyncManager.async(() -> {
            insertLogin(user);
        });
    }

    protected void insertLogin(final Object member) {
    }

    @Override
    protected void checkPermission(final LoginHandlingResource resource) throws LoginRequiredException {
        if (resource.getActionClass().getName().startsWith(Constants.ADMIN_PACKAGE)) {
            getSessionUserBean().ifPresent(user -> {
                if (!user.hasRoles(fessConfig.getAuthenticationAdminRoles().split(","))) {
                    throw new UserRoleLoginException(RootAction.class);
                }
            });
        }
    }

    // ===================================================================================
    //                                                                      Login Resource
    //                                                                      ==============
    @Override
    protected Class<FessUserBean> getUserBeanType() {
        return FessUserBean.class;
    }

    @Override
    protected Class<?> getLoginActionType() {
        return LoginAction.class;
    }

    @Override
    protected String toTypedUserId(final String userKey) {
        return userKey;
    }
}
