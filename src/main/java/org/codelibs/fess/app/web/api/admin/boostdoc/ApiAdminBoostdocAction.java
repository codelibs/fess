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
package org.codelibs.fess.app.web.api.admin.boostdoc;

import static org.codelibs.fess.app.web.admin.boostdoc.AdminBoostdocAction.getBoostDocumentRule;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.util.CopyOptions;
import org.codelibs.fess.app.pager.BoostDocPager;
import org.codelibs.fess.app.service.BoostDocumentRuleService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiConfigResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiConfigsResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiUpdateResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.es.config.exentity.BoostDocumentRule;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

/**
 * @author shinsuke
 */
public class ApiAdminBoostdocAction extends FessApiAdminAction {

    private static final Logger logger = LogManager.getLogger(ApiAdminBoostdocAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private BoostDocumentRuleService boostDocumentRuleService;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    // GET /api/admin/boostdoc
    // POST /api/admin/boostdoc
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final BoostDocPager pager = copyBeanToNewBean(body, BoostDocPager.class);
        final List<BoostDocumentRule> list = boostDocumentRuleService.getBoostDocumentRuleList(pager);
        return asJson(new ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                .total(pager.getAllRecordCount()).status(Status.OK).result());
    }

    // GET /api/admin/boostdoc/setting/{id}
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(new ApiConfigResponse()
                .setting(boostDocumentRuleService.getBoostDocumentRule(id).map(this::createEditBody).orElseGet(() -> {
                    throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
                    return null;
                })).status(Status.OK).result());
    }

    // PUT /api/admin/boostdoc/setting
    @Execute
    public JsonResponse<ApiResult> put$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final BoostDocumentRule boostDoc = getBoostDocumentRule(body).map(entity -> {
            try {
                boostDocumentRuleService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });
        return asJson(new ApiUpdateResponse().id(boostDoc.getId()).created(true).status(Status.OK).result());
    }

    // POST /api/admin/boostdoc/setting
    @Execute
    public JsonResponse<ApiResult> post$setting(final EditBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final BoostDocumentRule boostDoc = getBoostDocumentRule(body).map(entity -> {
            try {
                boostDocumentRuleService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id));
            return null;
        });
        return asJson(new ApiUpdateResponse().id(boostDoc.getId()).created(false).status(Status.OK).result());
    }

    // DELETE /api/admin/boostdoc/setting/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        boostDocumentRuleService.getBoostDocumentRule(id).ifPresent(entity -> {
            try {
                boostDocumentRuleService.delete(entity);
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

    protected EditBody createEditBody(final BoostDocumentRule entity) {
        final EditBody form = new EditBody();
        copyBeanToBean(entity, form, CopyOptions::excludeNull);
        form.crudMode = null;
        return form;
    }
}
