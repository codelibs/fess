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
package org.codelibs.fess.app.web.api.admin.fileauth;

import static org.codelibs.fess.app.web.admin.fileauth.AdminFileauthAction.getFileAuthentication;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.codelibs.fess.opensearch.config.exentity.FileAuthentication;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin file authentication management.
 * Provides RESTful API endpoints for managing file authentication settings in the Fess search engine.
 * File authentication settings define access credentials and permissions for file-based crawling.
 *
 */
public class ApiAdminFileauthAction extends FessApiAdminAction {

    private static final Logger logger = LogManager.getLogger(ApiAdminFileauthAction.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Default constructor.
     */
    public ApiAdminFileauthAction() {
        super();
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Service for managing file authentication configurations */
    @Resource
    private FileAuthenticationService fileAuthService;
    /** Service for managing file configuration settings */
    @Resource
    private FileConfigService fileConfigService;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    // GET /api/admin/fileauth/settings
    // PUT /api/admin/fileauth/settings
    /**
     * Returns list of file authentication settings.
     * Supports both GET and PUT requests for retrieving paginated file authentication configurations.
     *
     * @param body search parameters for filtering and pagination
     * @return JSON response containing file authentication settings list with pagination info
     */
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final FileAuthPager pager = copyBeanToNewBean(body, FileAuthPager.class);
        final List<FileAuthentication> list = fileAuthService.getFileAuthenticationList(pager);
        return asJson(
                new ApiResult.ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                        .total(pager.getAllRecordCount()).status(ApiResult.Status.OK).result());
    }

    // GET /api/admin/fileauth/setting/{id}
    /**
     * Returns specific file authentication setting by ID.
     *
     * @param id the file authentication setting ID
     * @return JSON response containing the file authentication setting details
     */
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(new ApiConfigResponse().setting(fileAuthService.getFileAuthentication(id).map(this::createEditBody).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(Status.OK).result());
    }

    // POST /api/admin/fileauth/setting
    /**
     * Creates a new file authentication setting.
     * Validates that the associated file config ID is valid before creation.
     *
     * @param body file authentication setting data to create
     * @return JSON response with created setting ID and status
     */
    @Execute
    public JsonResponse<ApiResult> post$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        if (!isValidFileConfigId(body.fileConfigId)) {
            return asJson(new ApiErrorResponse().message("invalid fileConfigId").status(Status.BAD_REQUEST).result());
        }

        body.crudMode = CrudMode.CREATE;
        final FileAuthentication fileAuth = getFileAuthentication(body).map(entity -> {
            try {
                fileAuthService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });

        return asJson(new ApiUpdateResponse().id(fileAuth.getId()).created(true).status(Status.OK).result());
    }

    // PUT /api/admin/fileauth/setting
    /**
     * Updates an existing file authentication setting.
     *
     * @param body file authentication setting data to update
     * @return JSON response with updated setting ID and status
     */
    @Execute
    public JsonResponse<ApiResult> put$setting(final EditBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final FileAuthentication fileAuth = getFileAuthentication(body).map(entity -> {
            try {
                fileAuthService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
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
    /**
     * Deletes a specific file authentication setting.
     *
     * @param id the file authentication setting ID to delete
     * @return JSON response with deletion status
     */
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        fileAuthService.getFileAuthentication(id).ifPresent(entity -> {
            try {
                fileAuthService.delete(entity);
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
     * Creates an edit body from a file authentication entity for API responses.
     *
     * @param entity the file authentication entity to convert
     * @return edit body containing the entity data
     */
    protected EditBody createEditBody(final FileAuthentication entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, copyOp -> {
            copyOp.excludeNull();
        });
        return body;
    }

    /**
     * Validates whether a file configuration ID exists.
     *
     * @param fileconfigId the file configuration ID to validate
     * @return true if the file configuration exists, false otherwise
     */
    protected Boolean isValidFileConfigId(final String fileconfigId) {
        return fileConfigService.getFileConfig(fileconfigId).isPresent();
    }
}
