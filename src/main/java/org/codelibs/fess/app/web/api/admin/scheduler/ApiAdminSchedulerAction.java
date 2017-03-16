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
package org.codelibs.fess.app.web.api.admin.scheduler;

import javax.annotation.Resource;

import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.JsonResponse;

public class ApiAdminSchedulerAction extends FessApiAdminAction {

    @Resource
    private ScheduledJobService scheduledJobService;

    @Execute
    public HtmlResponse index() {
        throw new UnsupportedOperationException();
    }

    // POST /api/admin/scheduler/{id}/start
    @Execute(urlPattern = "{}/@word")
    public JsonResponse<ApiResult> post$start(String id) {
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

    // POST /api/admin/scheduler/{id}/stop
    @Execute(urlPattern = "{}/@word")
    public JsonResponse<ApiResult> post$stop(String id) {
        scheduledJobService.getScheduledJob(id).ifPresent(entity -> {
            try {
                entity.stop();
            } catch (final Exception e) {
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

}
