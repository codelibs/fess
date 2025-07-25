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
package org.codelibs.fess.app.web.api.admin.accesstoken;

import static org.codelibs.core.stream.StreamUtil.stream;
import static org.codelibs.fess.app.web.admin.accesstoken.AdminAccesstokenAction.getAccessToken;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.AccessTokenPager;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.accesstoken.AdminAccesstokenAction;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiConfigResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiConfigsResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiUpdateResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.opensearch.config.exentity.AccessToken;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

/**
 * API action for admin access token.
 *
 */
public class ApiAdminAccesstokenAction extends FessApiAdminAction {

    /**
     * Default constructor.
     */
    public ApiAdminAccesstokenAction() {
        super();
    }

    private static final Logger logger = LogManager.getLogger(ApiAdminAccesstokenAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    // GET /api/admin/accesstoken
    // PUT /api/admin/accesstoken
    /**
     * Retrieves a list of access token settings.
     *
     * @param body the search body containing filter criteria
     * @return JSON response with access token list
     */
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final AccessTokenPager pager = copyBeanToNewBean(body, AccessTokenPager.class);
        final List<AccessToken> list = accessTokenService.getAccessTokenList(pager);
        return asJson(new ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                .total(pager.getAllRecordCount()).status(Status.OK).result());
    }

    // GET /api/admin/accesstoken/setting/{id}
    /**
     * Retrieves a specific access token setting by ID.
     *
     * @param id the access token ID to retrieve
     * @return JSON response with the access token setting
     */
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(new ApiConfigResponse().setting(accessTokenService.getAccessToken(id).map(this::createEditBody).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(Status.OK).result());
    }

    // POST /api/admin/accesstoken/setting
    /**
     * Creates a new access token setting.
     *
     * @param body the create body containing access token data
     * @return JSON response with the created access token ID
     */
    @Execute
    public JsonResponse<ApiResult> post$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final AccessToken accessToken = getAccessToken(body).map(entity -> {
            entity.setToken(accessTokenHelper.generateAccessToken());
            try {
                accessTokenService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });
        return asJson(new ApiUpdateResponse().id(accessToken.getId()).created(true).status(Status.OK).result());
    }

    // PUT /api/admin/accesstoken/setting
    /**
     * Updates an existing access token setting.
     *
     * @param body the edit body containing updated access token data
     * @return JSON response with the updated access token ID
     */
    @Execute
    public JsonResponse<ApiResult> put$setting(final EditBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final AccessToken accessToken = getAccessToken(body).map(entity -> {
            try {
                accessTokenService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
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
    /**
     * Deletes an access token setting by ID.
     *
     * @param id the access token ID to delete
     * @return JSON response confirming deletion
     */
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        accessTokenService.getAccessToken(id).ifPresent(entity -> {
            try {
                accessTokenService.delete(entity);
                saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
        }).orElse(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
        });
        return asJson(new ApiResponse().status(Status.OK).result());
    }

    /**
     * Creates an EditBody from an AccessToken entity for API responses.
     * Converts permissions and handles date formatting.
     *
     * @param entity the AccessToken entity to convert
     * @return the EditBody representation of the entity
     */
    protected EditBody createEditBody(final AccessToken entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, copyOp -> copyOp.exclude(Constants.PERMISSIONS, AdminAccesstokenAction.EXPIRED_TIME).excludeNull()
                .dateConverter(Constants.DEFAULT_DATETIME_FORMAT, AdminAccesstokenAction.EXPIRES));
        final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
        body.permissions = stream(entity.getPermissions()).get(
                stream -> stream.map(permissionHelper::decode).filter(StringUtil::isNotBlank).distinct().collect(Collectors.joining("\n")));
        body.crudMode = null;
        return body;

    }
}
