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
package org.codelibs.fess.app.web.logout;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.web.base.FessSearchAction;
import org.codelibs.fess.app.web.login.LoginAction;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;

public class LogoutAction extends FessSearchAction {

    // ===================================================================================
    //                                                                            Constant
    //

    // ===================================================================================
    //                                                                           Attribute
    //

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    @Execute
    public HtmlResponse index() {
        final OptionalThing<FessUserBean> userBean = getUserBean();
        activityHelper.logout(userBean);
        final String redirectUrl = userBean.map(user -> ComponentUtil.getSsoManager().logout(user)).orElse(null);
        fessLoginAssist.logout();
        userInfoHelper.deleteUserCodeFromCookie(request);
        if (StringUtil.isNotBlank(redirectUrl)) {
            return HtmlResponse.fromRedirectPathAsIs(redirectUrl);
        }
        return redirect(LoginAction.class);
    }

}