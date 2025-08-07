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
package org.codelibs.fess.app.web.api.admin.group;

import static org.codelibs.fess.app.web.admin.group.AdminGroupAction.getGroup;
import static org.codelibs.fess.app.web.admin.group.AdminGroupAction.validateAttributes;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.pager.GroupPager;
import org.codelibs.fess.app.service.GroupService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.opensearch.user.exentity.Group;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin group management.
 * Provides RESTful API endpoints for managing user group settings in the Fess search engine.
 * Groups define user permissions and access controls for search and administrative functions.
 */
public class ApiAdminGroupAction extends FessApiAdminAction {

    private static final Logger logger = LogManager.getLogger(ApiAdminGroupAction.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Default constructor.
     */
    public ApiAdminGroupAction() {
        super();
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========

    /** Service for managing group configurations */
    @Resource
    private GroupService groupService;

    // GET /api/admin/group
    // PUT /api/admin/group
    /**
     * Returns list of group settings.
     * Supports both GET and PUT requests for retrieving paginated group configurations.
     *
     * @param body search parameters for filtering and pagination
     * @return JSON response containing group settings list with pagination info
     */
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final GroupPager pager = copyBeanToNewBean(body, GroupPager.class);
        final List<Group> list = groupService.getGroupList(pager);
        return asJson(
                new ApiResult.ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                        .total(pager.getAllRecordCount())
                        .status(ApiResult.Status.OK)
                        .result());
    }

    // GET /api/admin/group/setting/{id}
    /**
     * Returns specific group setting by ID.
     *
     * @param id the group setting ID
     * @return JSON response containing the group setting details
     */
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(new ApiResult.ApiConfigResponse().setting(groupService.getGroup(id).map(this::createEditBody).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(ApiResult.Status.OK).result());
    }

    // POST /api/admin/group/setting
    /**
     * Creates a new group setting.
     *
     * @param body group setting data to create
     * @return JSON response with created setting ID and status
     */
    @Execute
    public JsonResponse<ApiResult> post$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        validateAttributes(body.attributes, this::throwValidationErrorApi);
        body.crudMode = CrudMode.CREATE;
        final Group entity = getGroup(body).orElseGet(() -> {
            throwValidationErrorApi(messages -> {
                messages.addErrorsCrudFailedToCreateInstance(GLOBAL);
            });
            return null;
        });
        try {
            groupService.store(entity);
            saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResult.ApiUpdateResponse().id(entity.getId()).created(true).status(ApiResult.Status.OK).result());
    }

    // PUT /api/admin/group/setting
    /**
     * Updates an existing group setting.
     *
     * @param body group setting data to update
     * @return JSON response with updated setting ID and status
     */
    @Execute
    public JsonResponse<ApiResult> put$setting(final EditBody body) {
        validateApi(body, messages -> {});
        validateAttributes(body.attributes, this::throwValidationErrorApi);
        body.crudMode = CrudMode.EDIT;
        final Group entity = getGroup(body).orElseGet(() -> {
            throwValidationErrorApi(messages -> {
                messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id);
            });
            return null;
        });
        try {
            groupService.store(entity);
        } catch (final Exception e) {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResult.ApiUpdateResponse().id(entity.getId()).created(false).status(ApiResult.Status.OK).result());
    }

    // DELETE /api/admin/group/setting/{id}
    /**
     * Deletes a specific group setting.
     * Prevents deletion of the currently logged-in user's group for security.
     *
     * @param id the group setting ID to delete
     * @return JSON response with deletion status
     */
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        final Group entity = groupService.getGroup(id).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        });
        getUserBean().ifPresent(u -> {
            if (u.getFessUser() instanceof User && entity.getName().equals(u.getUserId())) {
                throwValidationErrorApi(messages -> messages.addErrorsCouldNotDeleteLoggedInUser(GLOBAL));
            }
        });
        try {
            groupService.delete(entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToDeleteCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiResult.ApiUpdateResponse().id(id).created(false).status(ApiResult.Status.OK).result());
    }

    /**
     * Creates an edit body from a group entity for API responses.
     *
     * @param entity the group entity to convert
     * @return edit body containing the entity data
     */
    protected EditBody createEditBody(final Group entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, copyOp -> {
            copyOp.excludeNull();
        });
        return body;
    }
}
