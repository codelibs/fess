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
package org.codelibs.fess.app.web.api.admin.relatedcontent;

import static org.codelibs.fess.app.web.admin.relatedcontent.AdminRelatedcontentAction.getRelatedContent;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.pager.RelatedContentPager;
import org.codelibs.fess.app.service.RelatedContentService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiConfigResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiUpdateResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.opensearch.config.exentity.RelatedContent;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin related content management.
 * Provides RESTful API endpoints for managing related content settings in the Fess search engine.
 * Related content settings define content relationships and associations for search results.
 */
public class ApiAdminRelatedcontentAction extends FessApiAdminAction {

    private static final Logger logger = LogManager.getLogger(ApiAdminRelatedcontentAction.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Default constructor.
     */
    public ApiAdminRelatedcontentAction() {
        super();
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Service for managing related content configurations */
    @Resource
    private RelatedContentService relatedContentService;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    // GET /api/admin/relatedcontent/settings
    // PUT /api/admin/relatedcontent/settings
    /**
     * Returns list of related content settings.
     * Supports both GET and PUT requests for retrieving paginated related content configurations.
     *
     * @param body search parameters for filtering and pagination
     * @return JSON response containing related content settings list with pagination info
     */
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final RelatedContentPager pager = copyBeanToNewBean(body, RelatedContentPager.class);
        final List<RelatedContent> list = relatedContentService.getRelatedContentList(pager);
        return asJson(
                new ApiResult.ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                        .total(pager.getAllRecordCount()).status(ApiResult.Status.OK).result());
    }

    // GET /api/admin/relatedcontent/setting/{id}
    /**
     * Returns specific related content setting by ID.
     *
     * @param id the related content setting ID
     * @return JSON response containing the related content setting details
     */
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(
                new ApiConfigResponse().setting(relatedContentService.getRelatedContent(id).map(this::createEditBody).orElseGet(() -> {
                    throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
                    return null;
                })).status(Status.OK).result());
    }

    // POST /api/admin/relatedcontent/setting
    /**
     * Creates a new related content setting.
     *
     * @param body related content setting data to create
     * @return JSON response with created setting ID and status
     */
    @Execute
    public JsonResponse<ApiResult> post$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final RelatedContent relatedContent = getRelatedContent(body).map(entity -> {
            try {
                relatedContentService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });

        return asJson(new ApiUpdateResponse().id(relatedContent.getId()).created(true).status(Status.OK).result());
    }

    // PUT /api/admin/relatedcontent/setting
    /**
     * Updates an existing related content setting.
     *
     * @param body related content setting data to update
     * @return JSON response with updated setting ID and status
     */
    @Execute
    public JsonResponse<ApiResult> put$setting(final EditBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final RelatedContent relatedContent = getRelatedContent(body).map(entity -> {
            try {
                relatedContentService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id));
            return null;
        });
        return asJson(new ApiUpdateResponse().id(relatedContent.getId()).created(false).status(Status.OK).result());
    }

    // DELETE /api/admin/relatedcontent/setting/{id}
    /**
     * Deletes a specific related content setting.
     *
     * @param id the related content setting ID to delete
     * @return JSON response with deletion status
     */
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        relatedContentService.getRelatedContent(id).ifPresent(entity -> {
            try {
                relatedContentService.delete(entity);
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
     * Creates an edit body from a related content entity for API responses.
     *
     * @param entity the related content entity to convert
     * @return edit body containing the entity data
     */
    protected EditBody createEditBody(final RelatedContent entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, copyOp -> {
            copyOp.excludeNull();
        });
        return body;
    }
}
