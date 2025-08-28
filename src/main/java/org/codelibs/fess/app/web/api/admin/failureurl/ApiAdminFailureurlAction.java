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
package org.codelibs.fess.app.web.api.admin.failureurl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.pager.FailureUrlPager;
import org.codelibs.fess.app.service.FailureUrlService;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiLogResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.helper.ProcessHelper;
import org.codelibs.fess.opensearch.config.exentity.FailureUrl;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin failure URL.
 *
 */
public class ApiAdminFailureurlAction extends FessApiAdminAction {

    private static final Logger logger = LogManager.getLogger(ApiAdminFailureurlAction.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Default constructor.
     */
    public ApiAdminFailureurlAction() {
        super();
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private FailureUrlService failureUrlService;
    @Resource
    private FailureUrlPager failureUrlPager;
    /** Helper for managing crawler processes */
    @Resource
    protected ProcessHelper processHelper;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    // GET /api/admin/failureurl/logs
    // PUT /api/admin/failureurl/logs
    /**
     * Retrieves failure URL logs with pagination.
     *
     * @param body the search criteria
     * @return JSON response containing the failure URL logs
     */
    @Execute
    public JsonResponse<ApiResult> logs(final SearchBody body) {
        validateApi(body, messages -> {});
        final FailureUrlPager pager = copyBeanToNewBean(body, FailureUrlPager.class);
        final List<FailureUrl> list = failureUrlService.getFailureUrlList(pager);
        return asJson(new ApiResult.ApiLogsResponse<EditBody>().logs(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                .total(pager.getAllRecordCount())
                .status(ApiResult.Status.OK)
                .result());
    }

    // GET /api/admin/failureurl/log/{id}
    /**
     * Retrieves a specific failure URL log by ID.
     *
     * @param id the failure URL log ID
     * @return JSON response containing the failure URL log
     */
    @Execute
    public JsonResponse<ApiResult> get$log(final String id) {
        return asJson(new ApiLogResponse().log(failureUrlService.getFailureUrl(id).map(this::createEditBody).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(Status.OK).result());
    }

    // DELETE /api/admin/failureurl/log/{id}
    /**
     * Deletes a failure URL log by ID.
     *
     * @param id the failure URL log ID to delete
     * @return JSON response with result status
     */
    @Execute
    public JsonResponse<ApiResult> delete$log(final String id) {
        failureUrlService.getFailureUrl(id).ifPresent(entity -> {
            try {
                failureUrlService.delete(entity);
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

    // DELETE /api/admin/failureurl/all
    /**
     * Deletes all failure URL logs.
     *
     * @return JSON response with result status
     */
    @Execute
    public JsonResponse<ApiResult> delete$all() {
        try {
            failureUrlService.deleteAll(failureUrlPager);
            failureUrlPager.clear();
            saveInfo(messages -> messages.addSuccessFailureUrlDeleteAll(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResponse().status(Status.OK).result());
    }

    /**
     * Creates an EditBody from a FailureUrl entity.
     *
     * @param entity the FailureUrl entity
     * @return the EditBody representation
     */
    protected EditBody createEditBody(final FailureUrl entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, copyOp -> {
            copyOp.excludeNull();
        });
        return body;
    }
}
