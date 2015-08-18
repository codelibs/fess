/*
 * Copyright 2012 the CodeLibs Project and the Others.
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

import org.codelibs.fess.app.web.LoginAction;
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
 */
public class FessLoginAssist extends TypicalLoginAssist<String, FessUserBean, Object> // #change_it also UserBean
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

    // ===================================================================================
    //                                                                           Find User
    //                                                                           =========
    @Override
    protected boolean doCheckUserLoginable(String email, String cipheredPassword) {
        //return memberBhv.selectCount(cb -> {
        //    cb.query().arrangeLogin(email, cipheredPassword);
        //}) > 0;
        return false;
    }

    @Override
    protected OptionalEntity<Object> doFindLoginUser(String email, String cipheredPassword) {
        //return memberBhv.selectEntity(cb -> {
        //    cb.query().arrangeLogin(email, cipheredPassword);
        //});
        return null;
    }

    @Override
    protected OptionalEntity<Object> doFindLoginUser(String userId) {
        //return memberBhv.selectEntity(cb -> {
        //    cb.query().arrangeLoginByIdentity(userId);
        //});
        return null;
    }

    // ===================================================================================
    //                                                                       Login Process
    //                                                                       =============
    @Override
    protected FessUserBean createUserBean(Object userEntity) {
        return new FessUserBean();
    }

    @Override
    protected OptionalThing<String> getCookieRememberMeKey() {
        // example to use remember-me
        //return OptionalThing.of(fessConfig.getCookieRememberMeFessKey());
        return OptionalThing.empty();
    }

    @Override
    protected void saveLoginHistory(Object member, FessUserBean userBean, LoginSpecifiedOption option) {
        asyncManager.async(() -> {
            insertLogin(member);
        });
    }

    protected void insertLogin(Object member) {
    }

    @Override
    protected void checkPermission(LoginHandlingResource resource) throws LoginRequiredException {
        super.checkPermission(resource);
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
}
