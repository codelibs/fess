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
package org.codelibs.fess.app.web.api.admin.general;

import javax.annotation.Resource;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.beans.util.CopyOptions;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.app.web.admin.general.AdminGeneralAction;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiConfigResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

/**
 * @author shinsuke
 */
public class ApiAdminGeneralAction extends FessApiAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    protected DynamicProperties systemProperties;

    // ===================================================================================
    //

    // GET /api/admin/general
    @Execute
    public JsonResponse<ApiResult> get$index() {
        final EditBody form = new EditBody();
        AdminGeneralAction.updateForm(fessConfig, form);
        form.ldapAdminSecurityCredentials = null;
        return asJson(new ApiConfigResponse().setting(form).status(Status.OK).result());
    }

    // POST /api/admin/general
    @Execute
    public JsonResponse<ApiResult> post$index(final EditBody body) {
        validateApi(body, messages -> {});
        final EditBody newBody = new EditBody();
        AdminGeneralAction.updateForm(fessConfig, newBody);
        BeanUtil.copyBeanToBean(body, newBody, CopyOptions::excludeNull);
        AdminGeneralAction.updateConfig(fessConfig, newBody);
        return asJson(new ApiResponse().status(Status.OK).result());
    }

}
