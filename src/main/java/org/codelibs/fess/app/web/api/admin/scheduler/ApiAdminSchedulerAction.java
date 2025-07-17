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
package org.codelibs.fess.app.web.api.admin.scheduler;

import static org.codelibs.fess.app.web.admin.scheduler.AdminSchedulerAction.getScheduledJob;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.SchedulerPager;
import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.opensearch.config.exentity.ScheduledJob;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.JsonResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin scheduler management.
 */
public class ApiAdminSchedulerAction extends FessApiAdminAction {

    /** The logger for this class. */
    private static final Logger logger = LogManager.getLogger(ApiAdminSchedulerAction.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Default constructor.
     */
    public ApiAdminSchedulerAction() {
        super();
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========

    /** The scheduled job service for managing scheduler settings. */
    @Resource
    private ScheduledJobService scheduledJobService;

    /**
     * Index page (not supported for API).
     *
     * @return throws UnsupportedOperationException
     */
    @Execute
    public HtmlResponse index() {
        throw new UnsupportedOperationException();
    }

    /**
     * Starts a scheduled job by ID.
     *
     * @param id the ID of the scheduled job to start
     * @return JSON response indicating success or failure
     */
    // PUT /api/admin/scheduler/{id}/start
    @Execute(urlPattern = "{}/@word")
    public JsonResponse<ApiResult> put$start(final String id) {
        scheduledJobService.getScheduledJob(id).ifPresent(entity -> {
            if (!entity.isEnabled() || entity.isRunning()) {
                throwValidationErrorApi(messages -> {
                    messages.addErrorsFailedToStartJob(GLOBAL, entity.getName());
                });
            }
            try {
                entity.start();
            } catch (final Exception e) {
                throwValidationErrorApi(messages -> {
                    messages.addErrorsFailedToStartJob(GLOBAL, entity.getName());
                });
            }
        }).orElse(() -> {
            throwValidationErrorApi(messages -> {
                messages.addErrorsFailedToStartJob(GLOBAL, id);
            });
        });
        return asJson(new ApiResponse().status(Status.OK).result());
    }

    /**
     * Stops a scheduled job by ID.
     *
     * @param id the ID of the scheduled job to stop
     * @return JSON response indicating success or failure
     */
    // PUT /api/admin/scheduler/{id}/stop
    @Execute(urlPattern = "{}/@word")
    public JsonResponse<ApiResult> put$stop(final String id) {
        scheduledJobService.getScheduledJob(id).ifPresent(entity -> {
            try {
                entity.stop();
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> {
                    messages.addErrorsFailedToStopJob(GLOBAL, entity.getName());
                });
            }
        }).orElse(() -> {
            throwValidationErrorApi(messages -> {
                messages.addErrorsFailedToStartJob(GLOBAL, id);
            });
        });
        return asJson(new ApiResponse().status(Status.OK).result());
    }

    /**
     * Retrieves scheduler settings with pagination.
     *
     * @param body the search parameters for filtering and pagination
     * @return JSON response containing scheduler settings list
     */
    // GET /api/admin/scheduler
    // PUT /api/admin/scheduler
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final SchedulerPager pager = copyBeanToNewBean(body, SchedulerPager.class);
        final List<ScheduledJob> list = scheduledJobService.getScheduledJobList(pager);
        return asJson(
                new ApiResult.ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                        .total(pager.getAllRecordCount()).status(ApiResult.Status.OK).result());
    }

    /**
     * Retrieves a specific scheduler setting by ID.
     *
     * @param id the ID of the scheduler setting to retrieve
     * @return JSON response containing the scheduler setting
     */
    // GET /api/admin/scheduler/setting/{id}
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(new ApiResult.ApiConfigResponse()
                .setting(scheduledJobService.getScheduledJob(id).map(this::createEditBody).orElseGet(() -> {
                    throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
                    return null;
                })).status(ApiResult.Status.OK).result());
    }

    /**
     * Creates a new scheduler setting.
     *
     * @param body the scheduler data to create
     * @return JSON response containing the created scheduler setting ID
     */
    // POST /api/admin/scheduler/setting
    @Execute
    public JsonResponse<ApiResult> post$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final ScheduledJob entity = getScheduledJob(body).orElseGet(() -> {
            throwValidationErrorApi(messages -> {
                messages.addErrorsCrudFailedToCreateInstance(GLOBAL);
            });
            return null;
        });
        try {
            scheduledJobService.store(entity);
            saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResult.ApiUpdateResponse().id(entity.getId()).created(true).status(ApiResult.Status.OK).result());
    }

    /**
     * Updates an existing scheduler setting.
     *
     * @param body the scheduler data to update
     * @return JSON response containing the updated scheduler setting ID
     */
    // PUT /api/admin/scheduler/setting
    @Execute
    public JsonResponse<ApiResult> put$setting(final EditBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final ScheduledJob entity = getScheduledJob(body).orElseGet(() -> {
            throwValidationErrorApi(messages -> {
                messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id);
            });
            return null;
        });
        try {
            scheduledJobService.store(entity);
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResult.ApiUpdateResponse().id(entity.getId()).created(false).status(ApiResult.Status.OK).result());
    }

    /**
     * Deletes a scheduler setting by ID.
     *
     * @param id the ID of the scheduler setting to delete
     * @return JSON response indicating success or failure
     */
    // DELETE /api/admin/scheduler/setting/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        final ScheduledJob entity = scheduledJobService.getScheduledJob(id).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        });
        try {
            scheduledJobService.delete(entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResult.ApiUpdateResponse().id(id).created(false).status(ApiResult.Status.OK).result());
    }

    /**
     * Creates an EditBody from a ScheduledJob entity.
     *
     * @param entity the scheduled job entity to convert
     * @return the converted EditBody
     */
    protected EditBody createEditBody(final ScheduledJob entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
        body.running = entity.isRunning();
        return body;
    }

}
