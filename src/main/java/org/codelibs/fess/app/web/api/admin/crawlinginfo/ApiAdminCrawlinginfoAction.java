/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.api.admin.crawlinginfo;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.pager.CrawlingInfoPager;
import org.codelibs.fess.app.service.CrawlingInfoService;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiLogResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.es.config.exentity.CrawlingInfo;
import org.codelibs.fess.helper.ProcessHelper;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

/**
 * @author Keiichi Watanabe
 */
public class ApiAdminCrawlinginfoAction extends FessApiAdminAction {

    private static final Logger logger = LogManager.getLogger(ApiAdminCrawlinginfoAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private CrawlingInfoService crawlingInfoService;
    @Resource
    protected ProcessHelper processHelper;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    // GET /api/admin/crawlinginfo/logs
    // POST /api/admin/crawlinginfo/logs
    @Execute
    public JsonResponse<ApiResult> logs(final SearchBody body) {
        validateApi(body, messages -> {});
        final CrawlingInfoPager pager = copyBeanToNewBean(body, CrawlingInfoPager.class);
        final List<CrawlingInfo> list = crawlingInfoService.getCrawlingInfoList(pager);
        return asJson(new ApiResult.ApiLogsResponse<EditBody>().logs(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                .total(pager.getAllRecordCount()).status(ApiResult.Status.OK).result());
    }

    // GET /api/admin/crawlinginfo/log/{id}
    @Execute
    public JsonResponse<ApiResult> get$log(final String id) {
        return asJson(new ApiLogResponse().log(crawlingInfoService.getCrawlingInfo(id).map(this::createEditBody).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(Status.OK).result());
    }

    // DELETE /api/admin/crawlinginfo/log/{id}
    @Execute
    public JsonResponse<ApiResult> delete$log(final String id) {
        crawlingInfoService.getCrawlingInfo(id).ifPresent(entity -> {
            try {
                crawlingInfoService.delete(entity);
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

    // DELETE /api/admin/crawlinginfo/all
    @Execute
    public JsonResponse<ApiResult> delete$all() {
        try {
            crawlingInfoService.deleteOldSessions(processHelper.getRunningSessionIdSet());
            saveInfo(messages -> messages.addSuccessCrawlingInfoDeleteAll(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResponse().status(Status.OK).result());
    }

    protected EditBody createEditBody(final CrawlingInfo entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, copyOp -> {
            copyOp.excludeNull();
        });
        return body;
    }
}
