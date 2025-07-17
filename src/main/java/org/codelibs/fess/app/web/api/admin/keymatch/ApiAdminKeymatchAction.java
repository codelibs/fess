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
package org.codelibs.fess.app.web.api.admin.keymatch;

import static org.codelibs.fess.app.web.admin.keymatch.AdminKeymatchAction.getKeyMatch;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.pager.KeyMatchPager;
import org.codelibs.fess.app.service.KeyMatchService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiConfigResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiUpdateResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.opensearch.config.exentity.KeyMatch;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin key match management.
 *
 */
public class ApiAdminKeymatchAction extends FessApiAdminAction {

    /** The logger for this class. */
    private static final Logger logger = LogManager.getLogger(ApiAdminKeymatchAction.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Default constructor.
     */
    public ApiAdminKeymatchAction() {
        super();
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The key match service for managing key match settings. */
    @Resource
    private KeyMatchService keyMatchService;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    /**
     * Retrieves key match settings with pagination.
     *
     * @param body the search parameters for filtering and pagination
     * @return JSON response containing key match settings list
     */
    // GET /api/admin/keymatch/settings
    // PUT /api/admin/keymatch/settings
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final KeyMatchPager pager = copyBeanToNewBean(body, KeyMatchPager.class);
        final List<KeyMatch> list = keyMatchService.getKeyMatchList(pager);
        return asJson(
                new ApiResult.ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                        .total(pager.getAllRecordCount()).status(ApiResult.Status.OK).result());
    }

    /**
     * Retrieves a specific key match setting by ID.
     *
     * @param id the ID of the key match setting to retrieve
     * @return JSON response containing the key match setting
     */
    // GET /api/admin/keymatch/setting/{id}
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(new ApiConfigResponse().setting(keyMatchService.getKeyMatch(id).map(this::createEditBody).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(Status.OK).result());
    }

    /**
     * Creates a new key match setting.
     *
     * @param body the key match data to create
     * @return JSON response containing the created key match setting ID
     */
    // POST /api/admin/keymatch/setting
    @Execute
    public JsonResponse<ApiResult> post$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final KeyMatch keyMatch = getKeyMatch(body).map(entity -> {
            try {
                keyMatchService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });

        return asJson(new ApiUpdateResponse().id(keyMatch.getId()).created(true).status(Status.OK).result());
    }

    /**
     * Updates an existing key match setting.
     *
     * @param body the key match data to update
     * @return JSON response containing the updated key match setting ID
     */
    // PUT /api/admin/keymatch/setting
    @Execute
    public JsonResponse<ApiResult> put$setting(final EditBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final KeyMatch keyMatch = getKeyMatch(body).map(entity -> {
            try {
                keyMatchService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id));
            return null;
        });
        return asJson(new ApiUpdateResponse().id(keyMatch.getId()).created(false).status(Status.OK).result());
    }

    /**
     * Deletes a key match setting by ID.
     *
     * @param id the ID of the key match setting to delete
     * @return JSON response indicating success or failure
     */
    // DELETE /api/admin/keymatch/setting/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        keyMatchService.getKeyMatch(id).ifPresent(entity -> {
            try {
                keyMatchService.delete(entity);
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
     * Creates an EditBody from a KeyMatch entity.
     *
     * @param entity the key match entity to convert
     * @return the converted EditBody
     */
    protected EditBody createEditBody(final KeyMatch entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, copyOp -> {
            copyOp.excludeNull();
        });
        return body;
    }
}
