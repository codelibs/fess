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
package org.codelibs.fess.app.web.api.admin.relatedquery;

import static org.codelibs.core.stream.StreamUtil.stream;
import static org.codelibs.fess.app.web.admin.relatedquery.AdminRelatedqueryAction.getRelatedQuery;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.RelatedQueryPager;
import org.codelibs.fess.app.service.RelatedQueryService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiConfigResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiUpdateResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.opensearch.config.exentity.RelatedQuery;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin related query management.
 * Provides REST endpoints for managing related search queries.
 */
public class ApiAdminRelatedqueryAction extends FessApiAdminAction {

    private static final Logger logger = LogManager.getLogger(ApiAdminRelatedqueryAction.class);

    // ===================================================================================
    //                                                                           Constructor
    //                                                                           ===========

    /**
     * Default constructor.
     */
    public ApiAdminRelatedqueryAction() {
        super();
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Service for managing related query operations. */
    @Resource
    private RelatedQueryService relatedQueryService;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    /**
     * Retrieves a list of related query settings.
     *
     * @param body search parameters for filtering and pagination
     * @return JSON response containing related query configurations
     */
    // GET /api/admin/relatedquery/settings
    // PUT /api/admin/relatedquery/settings
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final RelatedQueryPager pager = copyBeanToNewBean(body, RelatedQueryPager.class);
        final List<RelatedQuery> list = relatedQueryService.getRelatedQueryList(pager);
        return asJson(
                new ApiResult.ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                        .total(pager.getAllRecordCount()).status(ApiResult.Status.OK).result());
    }

    /**
     * Retrieves a specific related query setting by ID.
     *
     * @param id the ID of the related query to retrieve
     * @return JSON response containing the related query configuration
     */
    // GET /api/admin/relatedquery/setting/{id}
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(new ApiConfigResponse().setting(relatedQueryService.getRelatedQuery(id).map(this::createEditBody).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(Status.OK).result());
    }

    /**
     * Creates a new related query setting.
     *
     * @param body the related query configuration data to create
     * @return JSON response containing the created related query ID and status
     */
    // POST /api/admin/relatedquery/setting
    @Execute
    public JsonResponse<ApiResult> post$setting(final CreateBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final RelatedQuery relatedQuery = getRelatedQuery(body).map(entity -> {
            try {
                relatedQueryService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });

        return asJson(new ApiUpdateResponse().id(relatedQuery.getId()).created(true).status(Status.OK).result());
    }

    /**
     * Updates an existing related query setting.
     *
     * @param body the related query configuration data to update
     * @return JSON response containing the updated related query ID and status
     */
    // PUT /api/admin/relatedquery/setting
    @Execute
    public JsonResponse<ApiResult> put$setting(final EditBody body) {
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final RelatedQuery relatedQuery = getRelatedQuery(body).map(entity -> {
            try {
                relatedQueryService.store(entity);
            } catch (final Exception e) {
                logger.warn("Failed to process a request.", e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id));
            return null;
        });
        return asJson(new ApiUpdateResponse().id(relatedQuery.getId()).created(false).status(Status.OK).result());
    }

    /**
     * Deletes a related query setting by ID.
     *
     * @param id the ID of the related query to delete
     * @return JSON response confirming deletion status
     */
    // DELETE /api/admin/relatedquery/setting/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        relatedQueryService.getRelatedQuery(id).ifPresent(entity -> {
            try {
                relatedQueryService.delete(entity);
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
     * Creates an EditBody from a RelatedQuery entity for API responses.
     *
     * @param entity the RelatedQuery entity to convert
     * @return EditBody containing the entity data formatted for editing
     */
    protected EditBody createEditBody(final RelatedQuery entity) {
        final EditBody body = new EditBody();
        copyBeanToBean(entity, body, copyOp -> {
            copyOp.excludeNull();
            copyOp.exclude(Constants.QUERIES);
        });
        body.queries = stream(entity.getQueries()).get(stream -> stream.filter(StringUtil::isNotBlank).collect(Collectors.joining("\n")));
        return body;
    }
}
