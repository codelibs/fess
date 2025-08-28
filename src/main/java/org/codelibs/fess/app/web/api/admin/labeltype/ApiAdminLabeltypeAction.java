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
package org.codelibs.fess.app.web.api.admin.labeltype;

import static org.codelibs.core.stream.StreamUtil.stream;
import static org.codelibs.fess.app.web.admin.labeltype.AdminLabeltypeAction.getLabelType;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.LabelTypePager;
import org.codelibs.fess.app.service.LabelTypeService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiConfigResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiUpdateResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.opensearch.config.exentity.LabelType;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin label type management.
 *
 */
public class ApiAdminLabeltypeAction extends FessApiAdminAction {

    /** The logger for this class. */
    private static final Logger logger = LogManager.getLogger(ApiAdminLabeltypeAction.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Default constructor.
     */
    public ApiAdminLabeltypeAction() {
        super();
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The label type service for managing label type settings. */
    @Resource
    private LabelTypeService labelTypeService;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    /**
     * Retrieves label type settings with pagination.
     *
     * @param body the search parameters for filtering and pagination
     * @return JSON response containing label type settings list
     */
    // GET /api/admin/labeltype/settings
    // PUT /api/admin/labeltype/settings
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final LabelTypePager pager = copyBeanToNewBean(body, LabelTypePager.class);
        final List<LabelType> list = labelTypeService.getLabelTypeList(pager);
        return asJson(
                new ApiResult.ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                        .total(pager.getAllRecordCount())
                        .status(ApiResult.Status.OK)
                        .result());
    }

    /**
     * Retrieves a specific label type setting by ID.
     *
     * @param id the ID of the label type setting to retrieve
     * @return JSON response containing the label type setting
     */
    // GET /api/admin/labeltype/setting/{id}
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(new ApiConfigResponse().setting(labelTypeService.getLabelType(id).map(this::createEditBody).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(Status.OK).result());
    }

    /**
     * Creates a new label type setting.
     *
     * @param body the label type data to create
     * @return JSON response containing the created label type setting ID
     */
    // POST /api/admin/labeltype/setting
    @Execute
    public JsonResponse<ApiResult> post$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final LabelType labelType = getLabelType(body).map(entity -> {
            try {
                labelTypeService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });

        return asJson(new ApiUpdateResponse().id(labelType.getId()).created(true).status(Status.OK).result());
    }

    /**
     * Updates an existing label type setting.
     *
     * @param body the label type data to update
     * @return JSON response containing the updated label type setting ID
     */
    // PUT /api/admin/labeltype/setting
    @Execute
    public JsonResponse<ApiResult> put$setting(final EditBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final LabelType labelType = getLabelType(body).map(entity -> {
            try {
                labelTypeService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id));
            return null;
        });
        return asJson(new ApiUpdateResponse().id(labelType.getId()).created(false).status(Status.OK).result());
    }

    /**
     * Deletes a label type setting by ID.
     *
     * @param id the ID of the label type setting to delete
     * @return JSON response indicating success or failure
     */
    // DELETE /api/admin/labeltype/setting/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        labelTypeService.getLabelType(id).ifPresent(entity -> {
            try {
                labelTypeService.delete(entity);
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
     * Creates an EditBody from a LabelType entity.
     *
     * @param entity the label type entity to convert
     * @return the converted EditBody
     */
    protected EditBody createEditBody(final LabelType entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, copyOp -> {
            copyOp.excludeNull();
            copyOp.exclude(Constants.PERMISSIONS);
        });
        final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
        body.permissions = stream(entity.getPermissions()).get(stream -> stream.map(s -> permissionHelper.decode(s))
                .filter(StringUtil::isNotBlank)
                .distinct()
                .collect(Collectors.joining("\n")));
        return body;
    }
}
