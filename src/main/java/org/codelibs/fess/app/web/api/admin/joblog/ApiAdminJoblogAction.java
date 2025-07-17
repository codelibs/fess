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
package org.codelibs.fess.app.web.api.admin.joblog;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.pager.JobLogPager;
import org.codelibs.fess.app.service.JobLogService;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiLogResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.opensearch.config.exentity.JobLog;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin job log management.
 * Provides RESTful API endpoints for viewing and managing job execution logs in the Fess search engine.
 * Job logs contain information about crawling jobs, indexing tasks, and system maintenance operations.
 *
 */
public class ApiAdminJoblogAction extends FessApiAdminAction {

    private static final Logger logger = LogManager.getLogger(ApiAdminJoblogAction.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Default constructor.
     */
    public ApiAdminJoblogAction() {
        super();
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Service for managing job log data */
    @Resource
    private JobLogService jobLogService;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    // GET /api/admin/joblog/logs
    /**
     * Returns list of job logs.
     * Supports filtering and pagination for job execution history.
     *
     * @param body search parameters for filtering and pagination
     * @return JSON response containing job logs list with pagination info
     */
    @Execute
    public JsonResponse<ApiResult> logs(final SearchBody body) {
        validateApi(body, messages -> {});
        final JobLogPager pager = copyBeanToNewBean(body, JobLogPager.class);
        final List<JobLog> list = jobLogService.getJobLogList(pager);
        return asJson(new ApiResult.ApiLogsResponse<EditBody>().logs(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                .total(pager.getAllRecordCount()).status(ApiResult.Status.OK).result());
    }

    // GET /api/admin/joblog/log/{id}
    /**
     * Returns specific job log by ID.
     * Provides detailed information about a particular job execution.
     *
     * @param id the job log ID
     * @return JSON response containing the job log details
     */
    @Execute
    public JsonResponse<ApiResult> get$log(final String id) {
        return asJson(new ApiLogResponse().log(jobLogService.getJobLog(id).map(this::createEditBody).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(Status.OK).result());
    }

    // DELETE /api/admin/joblog/log/{id}
    /**
     * Deletes a specific job log.
     * Useful for cleaning up old job execution records.
     *
     * @param id the job log ID to delete
     * @return JSON response with deletion status
     */
    @Execute
    public JsonResponse<ApiResult> delete$log(final String id) {
        jobLogService.getJobLog(id).ifPresent(entity -> {
            try {
                jobLogService.delete(entity);
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
     * Creates an edit body from a job log entity for API responses.
     *
     * @param entity the job log entity to convert
     * @return edit body containing the entity data
     */
    protected EditBody createEditBody(final JobLog entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, copyOp -> {
            copyOp.excludeNull();
        });
        return body;
    }
}
