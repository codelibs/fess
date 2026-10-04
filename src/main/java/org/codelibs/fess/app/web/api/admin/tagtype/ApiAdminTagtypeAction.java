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
package org.codelibs.fess.app.web.api.admin.tagtype;

import static org.codelibs.fess.app.web.admin.tagtype.AdminTagtypeAction.copyToForm;
import static org.codelibs.fess.app.web.admin.tagtype.AdminTagtypeAction.createTagType;
import static org.codelibs.fess.app.web.admin.tagtype.AdminTagtypeAction.deleteTagType;
import static org.codelibs.fess.app.web.admin.tagtype.AdminTagtypeAction.updateTagType;
import static org.codelibs.fess.app.web.admin.tagtype.AdminTagtypeAction.validateTagType;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.pager.TagTypePager;
import org.codelibs.fess.app.service.TagTypeService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.tagtype.AdminTagtypeAction.TagTypeExistsException;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiConfigResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiUpdateResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.exception.TagTypeConflictException;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import jakarta.annotation.Resource;

/**
 * API action for the admin management of the tags users own. A setting carries the
 * {@code seq_no} and {@code primary_term} it was read with; an update sends them back and is
 * refused when the tag changed since.
 */
public class ApiAdminTagtypeAction extends FessApiAdminAction {

    private static final Logger logger = LogManager.getLogger(ApiAdminTagtypeAction.class);

    /**
     * Default constructor.
     */
    public ApiAdminTagtypeAction() {
    }

    /** Service for tag type operations. */
    @Resource
    private TagTypeService tagTypeService;

    /**
     * Lists tag types, without their paths.
     *
     * @param body the search body
     * @return the tag types
     */
    // GET /api/admin/tagtype/settings
    // PUT /api/admin/tagtype/settings
    @Execute
    public JsonResponse<ApiResult> settings(final SearchBody body) {
        validateApi(body, messages -> {});
        final TagTypePager pager = copyBeanToNewBean(body, TagTypePager.class);
        final List<TagType> list = tagTypeService.getTagTypeList(pager);
        return asJson(
                new ApiResult.ApiConfigsResponse<EditBody>().settings(list.stream().map(this::createEditBody).collect(Collectors.toList()))
                        .total(pager.getAllRecordCount())
                        .status(ApiResult.Status.OK)
                        .result());
    }

    /**
     * Returns a tag type with its paths.
     *
     * @param id the tag type id
     * @return the tag type
     */
    // GET /api/admin/tagtype/setting/{id}
    @Execute
    public JsonResponse<ApiResult> get$setting(final String id) {
        return asJson(new ApiConfigResponse().setting(tagTypeService.getTagType(id).map(this::createEditBody).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(Status.OK).result());
    }

    /**
     * Creates a tag type.
     *
     * @param body the tag type
     * @return the id of the created tag type
     */
    // POST /api/admin/tagtype/setting
    @Execute
    public JsonResponse<ApiResult> post$setting(final CreateBody body) {
        validateApi(body, messages -> validateTagType(body, messages));
        body.crudMode = CrudMode.CREATE;
        TagType tagType = null;
        try {
            tagType = createTagType(tagTypeService, body);
        } catch (final TagTypeExistsException e) {
            throwValidationErrorApi(messages -> messages.addErrorsTagtypeAlreadyExists(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        return asJson(new ApiUpdateResponse().id(tagType.getId()).created(true).status(Status.OK).result());
    }

    /**
     * Updates a tag type. A change of the name or the owner gives the tag type a new id.
     *
     * @param body the tag type with the sequence number and primary term it was read with
     * @return the id of the updated tag type
     */
    // PUT /api/admin/tagtype/setting
    @Execute
    public JsonResponse<ApiResult> put$setting(final EditBody body) {
        validateApi(body, messages -> validateTagType(body, messages));
        body.crudMode = CrudMode.EDIT;
        TagType tagType = null;
        try {
            tagType = updateTagType(tagTypeService, body).orElse(null);
        } catch (final TagTypeExistsException e) {
            throwValidationErrorApi(messages -> messages.addErrorsTagtypeAlreadyExists(GLOBAL));
        } catch (final TagTypeConflictException e) {
            throwValidationErrorApi(messages -> messages.addErrorsTagtypeChangedConcurrently(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
        }
        if (tagType == null) {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.id));
        }
        return asJson(new ApiUpdateResponse().id(tagType.getId()).created(false).status(Status.OK).result());
    }

    /**
     * Deletes a tag type.
     *
     * @param id the tag type id
     * @return the result
     */
    // DELETE /api/admin/tagtype/setting/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String id) {
        tagTypeService.getTagType(id).ifPresent(entity -> {
            try {
                deleteTagType(tagTypeService, entity);
                saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
            } catch (final TagTypeConflictException e) {
                throwValidationErrorApi(messages -> messages.addErrorsTagtypeChangedConcurrently(GLOBAL));
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
     * Creates the body of a tag type.
     *
     * @param entity the tag type
     * @return the body
     */
    protected EditBody createEditBody(final TagType entity) {
        final EditBody body = new EditBody();
        copyToForm(entity, body);
        return body;
    }
}
