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
package org.codelibs.fess.app.web.api.admin.pathmap;

import static org.codelibs.fess.app.web.admin.pathmap.AdminPathmapAction.getPathMapping;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.PathMapPager;
import org.codelibs.fess.app.service.PathMappingService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.opensearch.config.exentity.PathMapping;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin path mapping management.
 * Provides RESTful API endpoints for managing path mapping settings in the Fess search engine.
 * Path mappings define URL path transformations and redirections for crawling and indexing.
 */
public class ApiAdminPathmapAction extends FessApiAdminAction {

    private static final Logger logger = LogManager.getLogger(ApiAdminPathmapAction.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Default constructor.
     */
    public ApiAdminPathmapAction() {
        super();
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========

    /** Service for managing path mapping configurations */
    @Resource
    private PathMappingService pathMappingService;

    // GET /api/admin/pathmap
    // PUT /api/admin/pathmap
    /**
     * Returns list of path mapping settings.
     * Supports both GET and PUT requests for retrieving paginated path mapping configurations.
     *
     * @param body search parameters for filtering and pagination
     * @return JSON response containing path mapping settings list with pagination info
     */
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final PathMapPager pager = copyBeanToNewBean(body, PathMapPager.class);
        final List<PathMapping> list = pathMappingService.getPathMappingList(pager);
        return asJson(
                new ApiResult.ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                        .total(pager.getAllRecordCount())
                        .status(ApiResult.Status.OK)
                        .result());
    }

    // GET /api/admin/pathmap/setting/{id}
    /**
     * Returns specific path mapping setting by ID.
     *
     * @param id the path mapping setting ID
     * @return JSON response containing the path mapping setting details
     */
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(
                new ApiResult.ApiConfigResponse().setting(pathMappingService.getPathMapping(id).map(this::createEditBody).orElseGet(() -> {
                    throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
                    return null;
                })).status(ApiResult.Status.OK).result());
    }

    // POST /api/admin/pathmap/setting
    /**
     * Creates a new path mapping setting.
     *
     * @param body path mapping setting data to create
     * @return JSON response with created setting ID and status
     */
    @Execute
    public JsonResponse<ApiResult> post$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final PathMapping entity = getPathMapping(body).orElseGet(() -> {
            throwValidationErrorApi(messages -> {
                messages.addErrorsCrudFailedToCreateInstance(GLOBAL);
            });
            return null;
        });
        try {
            pathMappingService.store(entity);
            saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResult.ApiUpdateResponse().id(entity.getId()).created(true).status(ApiResult.Status.OK).result());
    }

    // PUT /api/admin/pathmap/setting
    /**
     * Updates an existing path mapping setting.
     *
     * @param body path mapping setting data to update
     * @return JSON response with updated setting ID and status
     */
    @Execute
    public JsonResponse<ApiResult> put$setting(final EditBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final PathMapping entity = getPathMapping(body).orElseGet(() -> {
            throwValidationErrorApi(messages -> {
                messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id);
            });
            return null;
        });
        try {
            pathMappingService.store(entity);
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResult.ApiUpdateResponse().id(entity.getId()).created(false).status(ApiResult.Status.OK).result());
    }

    // DELETE /api/admin/pathmap/setting/{id}
    /**
     * Deletes a specific path mapping setting.
     *
     * @param id the path mapping setting ID to delete
     * @return JSON response with deletion status
     */
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        final PathMapping entity = pathMappingService.getPathMapping(id).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        });
        try {
            pathMappingService.delete(entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResult.ApiUpdateResponse().id(id).created(false).status(ApiResult.Status.OK).result());
    }

    /**
     * Creates an edit body from a path mapping entity for API responses.
     *
     * @param entity the path mapping entity to convert
     * @return edit body containing the entity data
     */
    protected EditBody createEditBody(final PathMapping entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
        return body;
    }
}
