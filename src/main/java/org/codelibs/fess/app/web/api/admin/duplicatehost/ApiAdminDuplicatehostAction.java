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
package org.codelibs.fess.app.web.api.admin.duplicatehost;

import static org.codelibs.fess.app.web.admin.duplicatehost.AdminDuplicatehostAction.getDuplicateHost;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

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
import org.codelibs.fess.es.config.exentity.DuplicateHost;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

/**
 * @author Keiichi Watanabe
 */
public class ApiAdminDuplicatehostAction extends FessApiAdminAction {

    private static final Logger logger = LogManager.getLogger(ApiAdminDuplicatehostAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private DuplicateHostService duplicateHostService;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    // GET /api/admin/duplicatehost/settings
    // POST /api/admin/duplicatehost/settings
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
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(new ApiConfigResponse().setting(duplicateHostService.getDuplicateHost(id).map(this::createEditBody).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(Status.OK).result());
    }

    // PUT /api/admin/duplicatehost/setting
    @Execute
    public JsonResponse<ApiResult> put$setting(final CreateBody body) {
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

    // POST /api/admin/duplicatehost/setting
    @Execute
    public JsonResponse<ApiResult> post$setting(final EditBody body) {
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

    protected EditBody createEditBody(final DuplicateHost entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, copyOp -> {
            copyOp.excludeNull();
        });
        return body;
    }
}
