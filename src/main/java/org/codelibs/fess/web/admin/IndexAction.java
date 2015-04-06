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

package org.codelibs.fess.web.admin;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.sastruts.core.util.ActivityUtil;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.RequestUtil;

public class IndexAction {

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("wizard");
    }

    @Execute(validator = false)
    public String index() {
        return "/admin/wizard/index?redirect=true";
    }

    @Execute(validator = false)
    public String logout() {
        final HttpServletRequest request = RequestUtil.getRequest();
        ActivityUtil.logout(request.getRemoteUser(), request);

        request.getSession().invalidate();
        return "/admin/wizard/index?redirect=true";
    }
}