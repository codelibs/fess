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
package org.codelibs.fess.app.web.api.admin.webauth;

import static org.codelibs.fess.app.web.admin.webauth.AdminWebauthAction.getWebAuthentication;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.pager.WebAuthPager;
import org.codelibs.fess.app.service.WebAuthenticationService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiConfigResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiErrorResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiUpdateResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.opensearch.config.exentity.WebAuthentication;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin web authentication management.
 *
 */
public class ApiAdminWebauthAction extends FessApiAdminAction {

    /** The logger for this class. */
    private static final Logger logger = LogManager.getLogger(ApiAdminWebauthAction.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Default constructor.
     */
    public ApiAdminWebauthAction() {
        super();
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The web authentication service for managing web authentication settings. */
    @Resource
    private WebAuthenticationService webAuthService;
    /** The web config service for validating web configuration references. */
    @Resource
    private WebConfigService webConfigService;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    /**
     * Retrieves web authentication settings with pagination.
     *
     * @param body the search parameters for filtering and pagination
     * @return JSON response containing web authentication settings list
     */
    // GET /api/admin/webauth/settings
    // PUT /api/admin/webauth/settings
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final WebAuthPager pager = copyBeanToNewBean(body, WebAuthPager.class);
        final List<WebAuthentication> list = webAuthService.getWebAuthenticationList(pager);
        return asJson(
                new ApiResult.ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                        .total(pager.getAllRecordCount())
                        .status(ApiResult.Status.OK)
                        .result());
    }

    /**
     * Retrieves a specific web authentication setting by ID.
     *
     * @param id the ID of the web authentication setting to retrieve
     * @return JSON response containing the web authentication setting
     */
    // GET /api/admin/webauth/setting/{id}
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(new ApiConfigResponse().setting(webAuthService.getWebAuthentication(id).map(this::createEditBody).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(Status.OK).result());
    }

    /**
     * Creates a new web authentication setting.
     *
     * @param body the web authentication data to create
     * @return JSON response containing the created web authentication setting ID
     */
    // POST /api/admin/webauth/setting
    @Execute
    public JsonResponse<ApiResult> post$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        if (!isValidWebConfigId(body.webConfigId)) {
            return asJson(new ApiErrorResponse().message("invalid webConfigId").status(Status.BAD_REQUEST).result());
        }

        body.crudMode = CrudMode.CREATE;
        final WebAuthentication webAuth = getWebAuthentication(body).map(entity -> {
            try {
                webAuthService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });

        return asJson(new ApiUpdateResponse().id(webAuth.getId()).created(true).status(Status.OK).result());
    }

    /**
     * Updates an existing web authentication setting.
     *
     * @param body the web authentication data to update
     * @return JSON response containing the updated web authentication setting ID
     */
    // PUT /api/admin/webauth/setting
    @Execute
    public JsonResponse<ApiResult> put$setting(final EditBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final WebAuthentication webAuth = getWebAuthentication(body).map(entity -> {
            try {
                webAuthService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id));
            return null;
        });
        return asJson(new ApiUpdateResponse().id(webAuth.getId()).created(false).status(Status.OK).result());
    }

    /**
     * Deletes a web authentication setting by ID.
     *
     * @param id the ID of the web authentication setting to delete
     * @return JSON response indicating success or failure
     */
    // DELETE /api/admin/webauth/setting/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        webAuthService.getWebAuthentication(id).ifPresent(entity -> {
            try {
                webAuthService.delete(entity);
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
     * Creates an EditBody from a WebAuthentication entity.
     *
     * @param entity the web authentication entity to convert
     * @return the converted EditBody
     */
    protected EditBody createEditBody(final WebAuthentication entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, copyOp -> {
            copyOp.excludeNull();
        });
        return body;
    }

    /**
     * Validates if the given web configuration ID exists.
     *
     * @param webconfigId the web configuration ID to validate
     * @return true if the web configuration exists, false otherwise
     */
    protected Boolean isValidWebConfigId(final String webconfigId) {
        return webConfigService.getWebConfig(webconfigId).isPresent();
    }
}
