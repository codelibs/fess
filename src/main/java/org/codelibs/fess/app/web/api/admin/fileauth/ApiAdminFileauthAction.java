/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.api.admin.fileauth;

import static org.codelibs.fess.app.web.admin.fileauth.AdminFileauthAction.getFileAuthentication;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.codelibs.fess.app.pager.FileAuthPager;
import org.codelibs.fess.app.service.FileAuthenticationService;
import org.codelibs.fess.app.service.FileConfigService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiConfigResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiErrorResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiUpdateResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.es.config.exentity.FileAuthentication;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

/**
 * @author Keiichi Watanabe
 */
public class ApiAdminFileauthAction extends FessApiAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private FileAuthenticationService fileAuthService;
    @Resource
    private FileConfigService fileConfigService;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    // GET /api/admin/fileauth/settings
    // POST /api/admin/fileauth/settings
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final FileAuthPager pager = copyBeanToNewBean(body, FileAuthPager.class);
        final List<FileAuthentication> list = fileAuthService.getFileAuthenticationList(pager);
        return asJson(new ApiResult.ApiConfigsResponse<EditBody>()
                .settings(list.stream().map(entity -> createEditBody(entity)).collect(Collectors.toList()))
                .total(pager.getAllRecordCount()).status(ApiResult.Status.OK).result());
    }

    // GET /api/admin/fileauth/setting/{id}
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(new ApiConfigResponse()
                .setting(fileAuthService.getFileAuthentication(id).map(entity -> createEditBody(entity)).orElseGet(() -> {
                    throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
                    return null;
                })).status(Status.OK).result());
    }

    // PUT /api/admin/fileauth/setting
    @Execute
    public JsonResponse<ApiResult> put$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        if (!isValidFileConfigId(body.fileConfigId)) {
            return asJson(new ApiErrorResponse().message("invalid fileConfigId").status(Status.BAD_REQUEST).result());
        }

        body.crudMode = CrudMode.CREATE;
        final FileAuthentication fileAuth = getFileAuthentication(body).map(entity -> {
            try {
                fileAuthService.store(entity);
            } catch (final Exception e) {
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });

        return asJson(new ApiUpdateResponse().id(fileAuth.getId()).created(true).status(Status.OK).result());
    }

    // POST /api/admin/fileauth/setting
    @Execute
    public JsonResponse<ApiResult> post$setting(final EditBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final FileAuthentication fileAuth = getFileAuthentication(body).map(entity -> {
            try {
                fileAuthService.store(entity);
            } catch (final Exception e) {
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id));
            return null;
        });
        return asJson(new ApiUpdateResponse().id(fileAuth.getId()).created(false).status(Status.OK).result());
    }

    // DELETE /api/admin/fileauth/setting/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        fileAuthService.getFileAuthentication(id).ifPresent(entity -> {
            try {
                fileAuthService.delete(entity);
                saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
            } catch (final Exception e) {
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
        }).orElse(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
        });
        return asJson(new ApiResponse().status(Status.OK).result());
    }

    protected EditBody createEditBody(final FileAuthentication entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, copyOp -> {
            copyOp.excludeNull();
        });
        return body;
    }

    protected Boolean isValidFileConfigId(final String fileconfigId) {
        return fileConfigService.getFileConfig(fileconfigId).isPresent();
    }
}
