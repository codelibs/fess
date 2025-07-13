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
package org.codelibs.fess.app.web.api.admin.webconfig;

import static org.codelibs.core.stream.StreamUtil.stream;
import static org.codelibs.fess.app.web.admin.webconfig.AdminWebconfigAction.getWebConfig;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.WebConfigPager;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiConfigResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiUpdateResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin web configuration management.
 *
 */
public class ApiAdminWebconfigAction extends FessApiAdminAction {

    /** The logger for this class. */
    private static final Logger logger = LogManager.getLogger(ApiAdminWebconfigAction.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Default constructor.
     */
    public ApiAdminWebconfigAction() {
        // Default constructor
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The web config service for managing web configuration settings. */
    @Resource
    private WebConfigService webConfigService;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    /**
     * Retrieves web configuration settings with pagination.
     *
     * @param body the search parameters for filtering and pagination
     * @return JSON response containing web configuration settings list
     */
    // GET /api/admin/webconfig/settings
    // PUT /api/admin/webconfig/settings
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final WebConfigPager pager = copyBeanToNewBean(body, WebConfigPager.class);
        final List<WebConfig> list = webConfigService.getWebConfigList(pager);
        return asJson(
                new ApiResult.ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                        .total(pager.getAllRecordCount()).status(ApiResult.Status.OK).result());
    }

    /**
     * Retrieves a specific web configuration setting by ID.
     *
     * @param id the ID of the web configuration setting to retrieve
     * @return JSON response containing the web configuration setting
     */
    // GET /api/admin/webconfig/setting/{id}
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(new ApiConfigResponse().setting(webConfigService.getWebConfig(id).map(this::createEditBody).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(Status.OK).result());
    }

    /**
     * Creates a new web configuration setting.
     *
     * @param body the web configuration data to create
     * @return JSON response containing the created web configuration setting ID
     */
    // POST /api/admin/webconfig/setting
    @Execute
    public JsonResponse<ApiResult> post$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final WebConfig webConfig = getWebConfig(body).map(entity -> {
            try {
                webConfigService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });

        return asJson(new ApiUpdateResponse().id(webConfig.getId()).created(true).status(Status.OK).result());
    }

    /**
     * Updates an existing web configuration setting.
     *
     * @param body the web configuration data to update
     * @return JSON response containing the updated web configuration setting ID
     */
    // PUT /api/admin/webconfig/setting
    @Execute
    public JsonResponse<ApiResult> put$setting(final EditBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final WebConfig webConfig = getWebConfig(body).map(entity -> {
            try {
                webConfigService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id));
            return null;
        });
        return asJson(new ApiUpdateResponse().id(webConfig.getId()).created(false).status(Status.OK).result());
    }

    /**
     * Deletes a web configuration setting by ID.
     *
     * @param id the ID of the web configuration setting to delete
     * @return JSON response indicating success or failure
     */
    // DELETE /api/admin/webconfig/setting/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        webConfigService.getWebConfig(id).ifPresent(entity -> {
            try {
                webConfigService.delete(entity);
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
     * Creates an EditBody from a WebConfig entity.
     *
     * @param entity the web configuration entity to convert
     * @return the converted EditBody
     */
    protected EditBody createEditBody(final WebConfig entity) {
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
