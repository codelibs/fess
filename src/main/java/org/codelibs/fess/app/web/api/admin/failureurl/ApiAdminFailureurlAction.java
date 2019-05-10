/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import javax.annotation.Resource;

import org.codelibs.fess.app.pager.FailureUrlPager;
import org.codelibs.fess.app.service.FailureUrlService;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiLogResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.es.config.exentity.FailureUrl;
import org.codelibs.fess.helper.ProcessHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

/**
 * @author Keiichi Watanabe
 */
public class ApiAdminFailureurlAction extends FessApiAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private FailureUrlService failureUrlService;
    @Resource
    private FailureUrlPager failureUrlPager;
    @Resource
    protected ProcessHelper processHelper;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    // GET /api/admin/failureurl/logs
    // POST /api/admin/failureurl/logs
    @Execute
    public JsonResponse<ApiResult> logs(final SearchBody body) {
        validateApi(body, messages -> {});
        final FailureUrlPager pager = copyBeanToNewBean(body, FailureUrlPager.class);
        final List<FailureUrl> list = failureUrlService.getFailureUrlList(pager);
        return asJson(new ApiResult.ApiLogsResponse<EditBody>()
                .logs(list.stream().map(entity -> createEditBody(entity)).collect(Collectors.toList())).total(pager.getAllRecordCount())
                .status(ApiResult.Status.OK).result());
    }

    // GET /api/admin/failureurl/log/{id}
    @Execute
    public JsonResponse<ApiResult> get$log(final String id) {
        return asJson(new ApiLogResponse().log(failureUrlService.getFailureUrl(id).map(entity -> createEditBody(entity)).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(Status.OK).result());
    }

    // DELETE /api/admin/failureurl/log/{id}
    @Execute
    public JsonResponse<ApiResult> delete$log(final String id) {
        failureUrlService.getFailureUrl(id).ifPresent(entity -> {
            try {
                failureUrlService.delete(entity);
                saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
            } catch (final Exception e) {
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
        }).orElse(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
        });
        return asJson(new ApiResponse().status(Status.OK).result());
    }

    // DELETE /api/admin/failureurl/all
    @Execute
    public JsonResponse<ApiResult> delete$all() {
        try {
            failureUrlService.deleteAll(failureUrlPager);
            failureUrlPager.clear();
            saveInfo(messages -> messages.addSuccessFailureUrlDeleteAll(GLOBAL));
        } catch (final Exception e) {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResponse().status(Status.OK).result());
    }

    protected EditBody createEditBody(final FailureUrl entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, copyOp -> {
            copyOp.excludeNull();
        });
        return body;
    }
}
