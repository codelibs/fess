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
package org.codelibs.fess.app.web.api.admin.fileconfig;

import static org.codelibs.core.stream.StreamUtil.stream;
import static org.codelibs.fess.app.web.admin.fileconfig.AdminFileconfigAction.getFileConfig;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.FileConfigPager;
import org.codelibs.fess.app.service.FileConfigService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiConfigResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiUpdateResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.opensearch.config.exentity.FileConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin file configuration management.
 * Provides RESTful API endpoints for managing file crawling configuration settings in the Fess search engine.
 * File configurations define settings for crawling file systems, FTP servers, and other file-based data sources.
 *
 */
public class ApiAdminFileconfigAction extends FessApiAdminAction {

    private static final Logger logger = LogManager.getLogger(ApiAdminFileconfigAction.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Default constructor.
     */
    public ApiAdminFileconfigAction() {
        // Default constructor
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Service for managing file configuration settings */
    @Resource
    private FileConfigService fileConfigService;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    // GET /api/admin/fileconfig/settings
    // PUT /api/admin/fileconfig/settings
    /**
     * Returns list of file configuration settings.
     * Supports both GET and PUT requests for retrieving paginated file configuration settings.
     *
     * @param body search parameters for filtering and pagination
     * @return JSON response containing file configuration settings list with pagination info
     */
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final FileConfigPager pager = copyBeanToNewBean(body, FileConfigPager.class);
        final List<FileConfig> list = fileConfigService.getFileConfigList(pager);
        return asJson(
                new ApiResult.ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                        .total(pager.getAllRecordCount()).status(ApiResult.Status.OK).result());
    }

    // GET /api/admin/fileconfig/setting/{id}
    /**
     * Returns specific file configuration setting by ID.
     *
     * @param id the file configuration setting ID
     * @return JSON response containing the file configuration setting details
     */
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(new ApiConfigResponse().setting(fileConfigService.getFileConfig(id).map(this::createEditBody).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(Status.OK).result());
    }

    // POST /api/admin/fileconfig/setting
    /**
     * Creates a new file configuration setting.
     *
     * @param body file configuration setting data to create
     * @return JSON response with created setting ID and status
     */
    @Execute
    public JsonResponse<ApiResult> post$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final FileConfig fileConfig = getFileConfig(body).map(entity -> {
            try {
                fileConfigService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });

        return asJson(new ApiUpdateResponse().id(fileConfig.getId()).created(true).status(Status.OK).result());
    }

    // PUT /api/admin/fileconfig/setting
    /**
     * Updates an existing file configuration setting.
     *
     * @param body file configuration setting data to update
     * @return JSON response with updated setting ID and status
     */
    @Execute
    public JsonResponse<ApiResult> put$setting(final EditBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final FileConfig fileConfig = getFileConfig(body).map(entity -> {
            try {
                fileConfigService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id));
            return null;
        });
        return asJson(new ApiUpdateResponse().id(fileConfig.getId()).created(false).status(Status.OK).result());
    }

    // DELETE /api/admin/fileconfig/setting/{id}
    /**
     * Deletes a specific file configuration setting.
     *
     * @param id the file configuration setting ID to delete
     * @return JSON response with deletion status
     */
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        fileConfigService.getFileConfig(id).ifPresent(entity -> {
            try {
                fileConfigService.delete(entity);
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
     * Creates an edit body from a file configuration entity for API responses.
     * Processes permissions and virtual hosts for proper display formatting.
     *
     * @param entity the file configuration entity to convert
     * @return edit body containing the entity data
     */
    protected EditBody createEditBody(final FileConfig entity) {
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
