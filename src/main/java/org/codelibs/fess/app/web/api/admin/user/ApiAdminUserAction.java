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
package org.codelibs.fess.app.web.api.admin.user;

import static org.codelibs.fess.app.web.admin.user.AdminUserAction.getUser;
import static org.codelibs.fess.app.web.admin.user.AdminUserAction.validateAttributes;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.pager.UserPager;
import org.codelibs.fess.app.service.UserService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin user management.
 */
public class ApiAdminUserAction extends FessApiAdminAction {

    /** The logger for this class. */
    private static final Logger logger = LogManager.getLogger(ApiAdminUserAction.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Default constructor.
     */
    public ApiAdminUserAction() {
        // Default constructor
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========

    /** The user service for managing user settings. */
    @Resource
    private UserService userService;

    /**
     * Retrieves user settings with pagination.
     *
     * @param body the search parameters for filtering and pagination
     * @return JSON response containing user settings list
     */
    // GET /api/admin/user/settings
    // PUT /api/admin/user/settings
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final UserPager pager = copyBeanToNewBean(body, UserPager.class);
        final List<User> list = userService.getUserList(pager);
        return asJson(
                new ApiResult.ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                        .total(pager.getAllRecordCount()).status(ApiResult.Status.OK).result());
    }

    /**
     * Retrieves a specific user setting by ID.
     *
     * @param id the ID of the user setting to retrieve
     * @return JSON response containing the user setting
     */
    // GET /api/admin/user/setting/{id}
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(new ApiResult.ApiConfigResponse().setting(userService.getUser(id).map(this::createEditBody).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(ApiResult.Status.OK).result());
    }

    /**
     * Creates a new user setting.
     *
     * @param body the user data to create
     * @return JSON response containing the created user setting ID
     */
    // POST /api/admin/user/setting
    @Execute
    public JsonResponse<ApiResult> post$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final User entity = getUser(body).orElseGet(() -> {
            throwValidationErrorApi(messages -> {
                messages.addErrorsCrudFailedToCreateInstance(GLOBAL);
            });
            return null;
        });
        try {
            userService.store(entity);
            saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResult.ApiUpdateResponse().id(entity.getId()).created(true).status(ApiResult.Status.OK).result());
    }

    /**
     * Updates an existing user setting.
     *
     * @param body the user data to update
     * @return JSON response containing the updated user setting ID
     */
    // PUT /api/admin/user/setting
    @Execute
    public JsonResponse<ApiResult> put$setting(final EditBody body) {
        validateApi(body, messages -> {});
        validateAttributes(body.attributes, this::throwValidationErrorApi);
        body.crudMode = CrudMode.EDIT;
        final User entity = getUser(body).orElseGet(() -> {
            throwValidationErrorApi(messages -> {
                messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id);
            });
            return null;
        });
        try {
            userService.store(entity);
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResult.ApiUpdateResponse().id(entity.getId()).created(false).status(ApiResult.Status.OK).result());
    }

    /**
     * Deletes a user setting by ID.
     *
     * @param id the ID of the user setting to delete
     * @return JSON response indicating success or failure
     */
    // DELETE /api/admin/user/setting/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        final User entity = userService.getUser(id).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        });
        getUserBean().ifPresent(u -> {
            if (u.getFessUser() instanceof User && entity.getName().equals(u.getUserId())) {
                throwValidationErrorApi(messages -> messages.addErrorsCouldNotDeleteLoggedInUser(GLOBAL));
            }
        });
        try {
            userService.delete(entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResult.ApiUpdateResponse().id(id).created(false).status(ApiResult.Status.OK).result());
    }

    /**
     * Creates an EditBody from a User entity.
     *
     * @param entity the user entity to convert
     * @return the converted EditBody
     */
    protected EditBody createEditBody(final User entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, copyOp -> {
            copyOp.excludeNull();
        });
        body.password = null;
        body.confirmPassword = null;
        return body;
    }
}
