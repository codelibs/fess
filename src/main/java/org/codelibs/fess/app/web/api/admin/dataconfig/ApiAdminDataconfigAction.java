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
package org.codelibs.fess.app.web.api.admin.dataconfig;

import static org.codelibs.core.stream.StreamUtil.stream;
import static org.codelibs.fess.app.web.admin.dataconfig.AdminDataconfigAction.getDataConfig;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.DataConfigPager;
import org.codelibs.fess.app.service.DataConfigService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiConfigResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiUpdateResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.opensearch.config.exentity.DataConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin data config.
 *
 */
public class ApiAdminDataconfigAction extends FessApiAdminAction {

    /**
     * Default constructor.
     */
    public ApiAdminDataconfigAction() {
        super();
    }

    private static final Logger logger = LogManager.getLogger(ApiAdminDataconfigAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private DataConfigService dataConfigService;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    /**
     * Retrieves data config settings with pagination support.
     *
     * @param body the search body containing pagination and filter parameters
     * @return JSON response containing list of data config settings
     */
    // GET /api/admin/dataconfig/settings
    // PUT /api/admin/dataconfig/settings
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final DataConfigPager pager = copyBeanToNewBean(body, DataConfigPager.class);
        final List<DataConfig> list = dataConfigService.getDataConfigList(pager);
        return asJson(
                new ApiResult.ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                        .total(pager.getAllRecordCount()).status(ApiResult.Status.OK).result());
    }

    /**
     * Retrieves a specific data config setting by ID.
     *
     * @param id the ID of the data config to retrieve
     * @return JSON response containing the data config setting
     */
    // GET /api/admin/dataconfig/setting/{id}
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(new ApiConfigResponse().setting(dataConfigService.getDataConfig(id).map(this::createEditBody).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(Status.OK).result());
    }

    /**
     * Creates a new data config setting.
     *
     * @param body the request body containing data config information
     * @return JSON response with result status
     */
    // POST /api/admin/dataconfig/setting
    @Execute
    public JsonResponse<ApiResult> post$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final DataConfig dataConfig = getDataConfig(body).map(entity -> {
            try {
                dataConfigService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });

        return asJson(new ApiUpdateResponse().id(dataConfig.getId()).created(true).status(Status.OK).result());
    }

    /**
     * Updates an existing data config setting.
     *
     * @param body the request body containing updated data config information
     * @return JSON response with result status
     */
    // PUT /api/admin/dataconfig/setting
    @Execute
    public JsonResponse<ApiResult> put$setting(final EditBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final DataConfig dataConfig = getDataConfig(body).map(entity -> {
            try {
                dataConfigService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id));
            return null;
        });
        return asJson(new ApiUpdateResponse().id(dataConfig.getId()).created(false).status(Status.OK).result());
    }

    /**
     * Deletes a data config setting by ID.
     *
     * @param id the ID of the data config to delete
     * @return JSON response indicating the deletion status
     */
    // DELETE /api/admin/dataconfig/setting/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        dataConfigService.getDataConfig(id).ifPresent(entity -> {
            try {
                dataConfigService.delete(entity);
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
     * Creates an EditBody from a DataConfig entity for API responses.
     * Handles permission and virtual host encoding.
     *
     * @param entity the DataConfig entity to convert
     * @return the converted EditBody object
     */
    protected EditBody createEditBody(final DataConfig entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, copyOp -> {
            copyOp.excludeNull();
            copyOp.exclude(Constants.PERMISSIONS, Constants.VIRTUAL_HOSTS);
        });
        final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
        body.permissions = stream(entity.getPermissions()).get(stream -> stream.map(s -> permissionHelper.decode(s))
                .filter(StringUtil::isNotBlank).distinct().collect(Collectors.joining("\n")));
        body.virtualHosts = stream(entity.getVirtualHosts())
                .get(stream -> stream.filter(StringUtil::isNotBlank).distinct().map(String::trim).collect(Collectors.joining("\n")));
        return body;
    }
}
