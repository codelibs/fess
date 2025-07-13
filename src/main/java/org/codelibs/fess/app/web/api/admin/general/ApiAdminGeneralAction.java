/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

import jakarta.annotation.Resource;

/**
 * API action for admin general settings management.
 * Provides RESTful API endpoints for managing general system configuration settings in the Fess search engine.
 * General settings include system-wide parameters, LDAP configuration, and core application settings.
 *
 */
public class ApiAdminGeneralAction extends FessApiAdminAction {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Default constructor.
     */
    public ApiAdminGeneralAction() {
        // Default constructor
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** System properties for dynamic configuration management */
    @Resource
    protected DynamicProperties systemProperties;

    // ===================================================================================
    //

    // GET /api/admin/general
    /**
     * Returns the current general system settings.
     * Excludes sensitive information like LDAP security credentials from the response.
     *
     * @return JSON response containing the general settings configuration
     */
    @Execute
    public JsonResponse<ApiResult> get$index() {
        final EditBody form = new EditBody();
        AdminGeneralAction.updateForm(fessConfig, form);
        form.ldapAdminSecurityCredentials = null;
        return asJson(new ApiConfigResponse().setting(form).status(Status.OK).result());
    }

    // PUT /api/admin/general
    /**
     * Updates the general system settings.
     * Merges the provided settings with existing configuration and applies changes.
     *
     * @param body the general settings data to update
     * @return JSON response with update status
     */
    @Execute
    public JsonResponse<ApiResult> put$index(final EditBody body) {
        validateApi(body, messages -> {});
        final EditBody newBody = new EditBody();
        AdminGeneralAction.updateForm(fessConfig, newBody);
        BeanUtil.copyBeanToBean(body, newBody, CopyOptions::excludeNull);
        AdminGeneralAction.updateConfig(fessConfig, newBody);
        return asJson(new ApiResponse().status(Status.OK).result());
    }

}
