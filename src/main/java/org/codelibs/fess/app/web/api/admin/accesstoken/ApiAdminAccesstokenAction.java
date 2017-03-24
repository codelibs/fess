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
package org.codelibs.fess.app.web.api.admin.accesstoken;

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.AccessTokenPager;
import org.codelibs.fess.app.service.AccessTokenService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.accesstoken.AdminAccesstokenAction;
import org.codelibs.fess.app.web.admin.accesstoken.CreateForm;
import org.codelibs.fess.app.web.admin.accesstoken.EditForm;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiConfigResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiConfigsResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiUpdateResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.es.config.exentity.AccessToken;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

/**
 * @author shinsuke
 */
public class ApiAdminAccesstokenAction extends FessApiAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private AccessTokenService accessTokenService;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    // GET /api/admin/accesstoken
    // POST /api/admin/accesstoken
    @Execute
    public JsonResponse<ApiResult> settings(SearchBody body) {
        validateApi(body, messages -> {});
        final AccessTokenPager pager = new AccessTokenPager();
        pager.setPageSize(body.size);
        pager.setCurrentPageNumber(body.page);
        final List<AccessToken> list = accessTokenService.getAccessTokenList(pager);
        return asJson(new ApiConfigsResponse<EditBody>()
                .settings(list.stream().map(entity -> createEditBody(entity)).collect(Collectors.toList()))
                .total(pager.getAllRecordCount()).status(Status.OK).result());
    }

    // GET /api/admin/accesstoken/setting/{id}
    @Execute
    public JsonResponse<ApiResult> get$setting(String id) {
        return asJson(new ApiConfigResponse()
                .setting(accessTokenService.getAccessToken(id).map(entity -> createEditBody(entity)).orElseGet(() -> {
                    throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
                    return null;
                })).status(Status.OK).result());
    }

    // PUT /api/admin/accesstoken/setting
    @Execute
    public JsonResponse<ApiResult> put$setting(CreateBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final AccessToken accessToken = getAccessToken(body).map(entity -> {
            entity.setToken(systemHelper.generateAccessToken());
            try {
                accessTokenService.store(entity);
            } catch (final Exception e) {
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });
        return asJson(new ApiUpdateResponse().id(accessToken.getId()).created(true).status(Status.OK).result());
    }

    // POST /api/admin/accesstoken/setting
    @Execute
    public JsonResponse<ApiResult> post$setting(final EditBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final AccessToken accessToken = getAccessToken(body).map(entity -> {
            try {
                accessTokenService.store(entity);
            } catch (final Exception e) {
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id));
            return null;
        });
        return asJson(new ApiUpdateResponse().id(accessToken.getId()).created(false).status(Status.OK).result());
    }

    // DELETE /api/admin/accesstoken/setting/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(String id) {
        accessTokenService.getAccessToken(id).ifPresent(entity -> {
            try {
                accessTokenService.delete(entity);
                saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
            } catch (final Exception e) {
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
        }).orElse(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
        });
        return asJson(new ApiResponse().status(Status.OK).result());
    }

    protected EditBody createEditBody(final AccessToken entity) {
        final EditBody form = new EditBody();
        copyBeanToBean(entity, form, copyOp -> copyOp.exclude(Constants.PERMISSIONS, AdminAccesstokenAction.EXPIRED_TIME).excludeNull()
                .dateConverter(Constants.DEFAULT_DATETIME_FORMAT, AdminAccesstokenAction.EXPIRES));
        final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
        form.permissions =
                stream(entity.getPermissions()).get(
                        stream -> stream.map(permissionHelper::decode).filter(StringUtil::isNotBlank).distinct()
                                .collect(Collectors.joining("\n")));
        form.crudMode = null;
        return form;

    }

    protected OptionalEntity<AccessToken> getAccessToken(final CreateForm body) {
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        return getEntity(body, username, currentTime).map(
                entity -> {
                    entity.setUpdatedBy(username);
                    entity.setUpdatedTime(currentTime);
                    copyBeanToBean(
                            body,
                            entity,
                            op -> op.exclude(Constants.COMMON_API_CONVERSION_RULE)
                                    .exclude(AdminAccesstokenAction.TOKEN, Constants.PERMISSIONS, AdminAccesstokenAction.EXPIRED_TIME)
                                    .dateConverter(Constants.DEFAULT_DATETIME_FORMAT, AdminAccesstokenAction.EXPIRES));
                    final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
                    entity.setPermissions(split(body.permissions, "\n").get(
                            stream -> stream.map(s -> permissionHelper.encode(s)).filter(StringUtil::isNotBlank).distinct()
                                    .toArray(n -> new String[n])));
                    return entity;
                });
    }

    private OptionalEntity<AccessToken> getEntity(final CreateForm form, final String username, final long currentTime) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            return OptionalEntity.of(new AccessToken()).map(entity -> {
                entity.setCreatedBy(username);
                entity.setCreatedTime(currentTime);
                return entity;
            });
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return accessTokenService.getAccessToken(((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }
}
