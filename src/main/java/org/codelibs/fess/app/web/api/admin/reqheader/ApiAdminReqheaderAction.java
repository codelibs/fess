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
package org.codelibs.fess.app.web.api.admin.reqheader;

import static org.codelibs.fess.app.web.admin.reqheader.AdminReqheaderAction.getRequestHeader;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.pager.ReqHeaderPager;
import org.codelibs.fess.app.service.RequestHeaderService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiConfigResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiErrorResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiUpdateResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.opensearch.config.exentity.RequestHeader;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin request header management.
 *
 * @author Keiichi Watanabe
 */
public class ApiAdminReqheaderAction extends FessApiAdminAction {

    /** The logger for this class. */
    private static final Logger logger = LogManager.getLogger(ApiAdminReqheaderAction.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Default constructor.
     */
    public ApiAdminReqheaderAction() {
        // Default constructor
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The request header service for managing request header settings. */
    @Resource
    private RequestHeaderService reqHeaderService;
    /** The web config service for validating web configuration references. */
    @Resource
    private WebConfigService webConfigService;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    /**
     * Retrieves request header settings with pagination.
     *
     * @param body the search parameters for filtering and pagination
     * @return JSON response containing request header settings list
     */
    // GET /api/admin/reqheader/settings
    // PUT /api/admin/reqheader/settings
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final ReqHeaderPager pager = copyBeanToNewBean(body, ReqHeaderPager.class);
        final List<RequestHeader> list = reqHeaderService.getRequestHeaderList(pager);
        return asJson(
                new ApiResult.ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                        .total(pager.getAllRecordCount()).status(ApiResult.Status.OK).result());
    }

    /**
     * Retrieves a specific request header setting by ID.
     *
     * @param id the ID of the request header setting to retrieve
     * @return JSON response containing the request header setting
     */
    // GET /api/admin/reqheader/setting/{id}
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(new ApiConfigResponse().setting(reqHeaderService.getRequestHeader(id).map(this::createEditBody).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(Status.OK).result());
    }

    /**
     * Creates a new request header setting.
     *
     * @param body the request header data to create
     * @return JSON response containing the created request header setting ID
     */
    // POST /api/admin/reqheader/setting
    @Execute
    public JsonResponse<ApiResult> post$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        if (!isValidWebConfigId(body.webConfigId)) {
            return asJson(new ApiErrorResponse().message("invalid webConfigId").status(Status.BAD_REQUEST).result());
        }

        body.crudMode = CrudMode.CREATE;
        final RequestHeader reqHeader = getRequestHeader(body).map(entity -> {
            try {
                reqHeaderService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });

        return asJson(new ApiUpdateResponse().id(reqHeader.getId()).created(true).status(Status.OK).result());
    }

    /**
     * Updates an existing request header setting.
     *
     * @param body the request header data to update
     * @return JSON response containing the updated request header setting ID
     */
    // PUT /api/admin/reqheader/setting
    @Execute
    public JsonResponse<ApiResult> put$setting(final EditBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final RequestHeader reqHeader = getRequestHeader(body).map(entity -> {
            try {
                reqHeaderService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id));
            return null;
        });
        return asJson(new ApiUpdateResponse().id(reqHeader.getId()).created(false).status(Status.OK).result());
    }

    /**
     * Deletes a request header setting by ID.
     *
     * @param id the ID of the request header setting to delete
     * @return JSON response indicating success or failure
     */
    // DELETE /api/admin/reqheader/setting/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        reqHeaderService.getRequestHeader(id).ifPresent(entity -> {
            try {
                reqHeaderService.delete(entity);
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
     * Creates an EditBody from a RequestHeader entity.
     *
     * @param entity the request header entity to convert
     * @return the converted EditBody
     */
    protected EditBody createEditBody(final RequestHeader entity) {
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
