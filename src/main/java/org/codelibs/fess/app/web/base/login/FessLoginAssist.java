/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.es.user.exbhv.UserBhv;
import org.codelibs.fess.exception.UserRoleLoginException;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.core.magic.async.AsyncManager;
import org.lastaflute.core.time.TimeManager;
import org.lastaflute.web.login.LoginHandlingResource;
import org.lastaflute.web.login.PrimaryLoginManager;
import org.lastaflute.web.login.TypicalLoginAssist;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.login.credential.UserPasswordCredential;
import org.lastaflute.web.login.exception.LoginRequiredException;
import org.lastaflute.web.login.option.LoginSpecifiedOption;

/**
 * @author jflute
 * @author shinsuke
 */
public class FessLoginAssist extends TypicalLoginAssist<String, FessUserBean, FessUser> // #change_it also UserBean
        implements PrimaryLoginManager {

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
    public boolean checkUserLoginable(final LoginCredential credential) {
        throw new UnsupportedOperationException("checkUserLoginable is not supported.");
    }

    @Override
    protected void checkCredential(final TypicalLoginAssist<String, FessUserBean, FessUser>.CredentialChecker checker) {
        throw new UnsupportedOperationException("checkCredential is not supported.");
    }

    @Override
    protected OptionalEntity<FessUser> doFindLoginUser(final String username) {
        return userBhv.selectEntity(cb -> {
            cb.query().setName_Equal(username);
        }).map(user -> (FessUser) user);
    }

    // ===================================================================================
    //                                                                       Login Process
    //                                                                       =============
    @Override
    protected FessUserBean createUserBean(final FessUser user) {
        return new FessUserBean(user);
    }

    @Override
    protected OptionalThing<String> getCookieRememberMeKey() {
        // example to use remember-me
        //return OptionalThing.of(fessConfig.getCookieRememberMeFessKey());
        return OptionalThing.empty();
    }

    @Override
    protected void saveLoginHistory(final FessUser user, final FessUserBean userBean, final LoginSpecifiedOption option) {
        asyncManager.async(() -> {
            insertLogin(user);
        });
    }

    protected void insertLogin(final Object member) {
    }

    @Override
    protected void checkPermission(final LoginHandlingResource resource) throws LoginRequiredException {
        if (resource.getActionClass().getName().startsWith(Constants.ADMIN_PACKAGE)) {
            getSavedUserBean().ifPresent(user -> {
                if (!user.hasRoles(fessConfig.getAuthenticationAdminRolesAsArray())) {
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

    // ===================================================================================
    //                                                                     Login Extention
    //                                                                      ==============

    @Override
    protected void resolveCredential(final CredentialResolver resolver) {
        resolver.resolve(UserPasswordCredential.class, credential -> {
            final UserPasswordCredential userCredential = credential;
            final String username = userCredential.getUser();
            final String password = userCredential.getPassword();
            if (!fessConfig.isAdminUser(username)) {
                final OptionalEntity<FessUser> ldapUser = ComponentUtil.getLdapManager().login(username, password);
                if (ldapUser.isPresent()) {
                    return ldapUser;
                }
            }
            return doFindLoginUser(username, encryptPassword(password));
        });
        resolver.resolve(SpnegoCredential.class, credential -> {
            final String username = credential.getUsername();
            if (!fessConfig.isAdminUser(username)) {
                return ComponentUtil.getLdapManager().login(username);
            }
            return OptionalEntity.empty();
        });
        resolver.resolve(OpenIdConnectCredential.class, credential -> {
            return OptionalEntity.of(credential.getUser());
        });
    }

    protected OptionalEntity<FessUser> doFindLoginUser(final String username, final String cipheredPassword) {
        return userBhv.selectEntity(cb -> {
            cb.query().setName_Equal(username);
            cb.query().setPassword_Equal(cipheredPassword);
        }).map(user -> (FessUser) user);
    }
}
