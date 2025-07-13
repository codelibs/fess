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
package org.codelibs.fess.app.web.api.admin.duplicatehost;

import static org.codelibs.fess.app.web.admin.duplicatehost.AdminDuplicatehostAction.getDuplicateHost;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.pager.DuplicateHostPager;
import org.codelibs.fess.app.service.DuplicateHostService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiConfigResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiUpdateResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.opensearch.config.exentity.DuplicateHost;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin duplicate host management.
 * Provides RESTful API endpoints for managing duplicate host settings in the Fess search engine.
 * Duplicate host settings help prevent indexing the same content from multiple similar URLs.
 *
 */
public class ApiAdminDuplicatehostAction extends FessApiAdminAction {

    private static final Logger logger = LogManager.getLogger(ApiAdminDuplicatehostAction.class);

    // ===================================================================================
    //                                                                           Constructor
    //                                                                           ===========

    /**
     * Default constructor.
     */
    public ApiAdminDuplicatehostAction() {
        // Default constructor
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Service for managing duplicate host configurations */
    @Resource
    private DuplicateHostService duplicateHostService;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    // GET /api/admin/duplicatehost/settings
    // PUT /api/admin/duplicatehost/settings
    /**
     * Returns list of duplicate host settings.
     * Supports both GET and PUT requests for retrieving paginated duplicate host configurations.
     *
     * @param body search parameters for filtering and pagination
     * @return JSON response containing duplicate host settings list with pagination info
     */
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final DuplicateHostPager pager = copyBeanToNewBean(body, DuplicateHostPager.class);
        final List<DuplicateHost> list = duplicateHostService.getDuplicateHostList(pager);
        return asJson(
                new ApiResult.ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                        .total(pager.getAllRecordCount()).status(ApiResult.Status.OK).result());
    }

    // GET /api/admin/duplicatehost/setting/{id}
    /**
     * Returns specific duplicate host setting by ID.
     *
     * @param id the duplicate host setting ID
     * @return JSON response containing the duplicate host setting details
     */
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(new ApiConfigResponse().setting(duplicateHostService.getDuplicateHost(id).map(this::createEditBody).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(Status.OK).result());
    }

    // POST /api/admin/duplicatehost/setting
    /**
     * Creates a new duplicate host setting.
     *
     * @param body duplicate host setting data to create
     * @return JSON response with created setting ID and status
     */
    @Execute
    public JsonResponse<ApiResult> post$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final DuplicateHost duplicateHost = getDuplicateHost(body).map(entity -> {
            try {
                duplicateHostService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });

        return asJson(new ApiUpdateResponse().id(duplicateHost.getId()).created(true).status(Status.OK).result());
    }

    // PUT /api/admin/duplicatehost/setting
    /**
     * Updates an existing duplicate host setting.
     *
     * @param body duplicate host setting data to update
     * @return JSON response with updated setting ID and status
     */
    @Execute
    public JsonResponse<ApiResult> put$setting(final EditBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final DuplicateHost duplicateHost = getDuplicateHost(body).map(entity -> {
            try {
                duplicateHostService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id));
            return null;
        });
        return asJson(new ApiUpdateResponse().id(duplicateHost.getId()).created(false).status(Status.OK).result());
    }

    // DELETE /api/admin/duplicatehost/setting/{id}
    /**
     * Deletes a specific duplicate host setting.
     *
     * @param id the duplicate host setting ID to delete
     * @return JSON response with deletion status
     */
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        duplicateHostService.getDuplicateHost(id).ifPresent(entity -> {
            try {
                duplicateHostService.delete(entity);
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
     * Creates an edit body from a duplicate host entity for API responses.
     *
     * @param entity the duplicate host entity to convert
     * @return edit body containing the entity data
     */
    protected EditBody createEditBody(final DuplicateHost entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, copyOp -> {
            copyOp.excludeNull();
        });
        return body;
    }
}
