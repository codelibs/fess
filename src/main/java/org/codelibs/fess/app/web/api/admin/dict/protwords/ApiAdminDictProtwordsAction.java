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
package org.codelibs.fess.app.web.api.admin.dict.protwords;

import static org.codelibs.fess.app.web.admin.dict.protwords.AdminDictProtwordsAction.createProtwordsItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.pager.ProtwordsPager;
import org.codelibs.fess.app.service.ProtwordsService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.dict.protwords.UploadForm;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.dict.protwords.ProtwordsFile;
import org.codelibs.fess.dict.protwords.ProtwordsItem;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.response.StreamResponse;

import jakarta.annotation.Resource;

/**
 * API action for CRUD and file operations on dictionary protected words (protwords).
 */
public class ApiAdminDictProtwordsAction extends FessApiAdminAction {

    /**
     * Default constructor.
     */
    public ApiAdminDictProtwordsAction() {
        super();
    }

    private static final Logger logger = LogManager.getLogger(ApiAdminDictProtwordsAction.class);

    @Resource
    private ProtwordsService protwordsService;

    /**
     * Retrieve list of protected words entries for the specified dictionary.
     *
     * @param dictId identifier of the dictionary
     * @param body search criteria and paging parameters
     * @return JSON response containing list of entries
     */
    // GET /api/admin/dict/protwords/settings/{dictId}
    @Execute
    public JsonResponse<ApiResult> get$settings(final String dictId, final SearchBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        final ProtwordsPager pager = copyBeanToNewBean(body, ProtwordsPager.class);
        return asJson(new ApiResult.ApiConfigsResponse<EditBody>()
                .settings(protwordsService.getProtwordsList(body.dictId, pager).stream()
                        .map(protwordsItem -> createEditBody(protwordsItem, dictId)).collect(Collectors.toList()))
                .status(ApiResult.Status.OK).result());
    }

    /**
     * Retrieve a single protected words entry by ID for the specified dictionary.
     *
     * @param dictId identifier of the dictionary
     * @param id identifier of the entry
     * @return JSON response containing the entry
     */
    // GET /api/admin/dict/protwords/setting/{dictId}/{id}
    @Execute
    public JsonResponse<ApiResult> get$setting(final String dictId, final long id) {
        return asJson(new ApiResult.ApiConfigResponse()
                .setting(protwordsService.getProtwordsItem(dictId, id).map(entity -> createEditBody(entity, dictId)).orElseGet(() -> {
                    throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, String.valueOf(id)));
                    return null;
                })).status(ApiResult.Status.OK).result());
    }

    /**
     * Create a new protected words entry for the specified dictionary.
     *
     * @param dictId identifier of the dictionary
     * @param body create request payload
     * @return JSON response containing creation result and new entry ID
     */
    // POST /api/admin/dict/protwords/setting/{dictId}
    @Execute
    public JsonResponse<ApiResult> post$setting(final String dictId, final CreateBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final ProtwordsItem entity = createProtwordsItem(this, body, () -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });
        protwordsService.store(body.dictId, entity);
        return asJson(
                new ApiResult.ApiUpdateResponse().id(String.valueOf(entity.getId())).created(true).status(ApiResult.Status.OK).result());
    }

    /**
     * Update an existing protected words entry in the specified dictionary.
     *
     * @param dictId identifier of the dictionary
     * @param body update request payload
     * @return JSON response containing update result and entry ID
     */
    // PUT /api/admin/dict/protwords/setting/{dictId}
    @Execute
    public JsonResponse<ApiResult> put$setting(final String dictId, final EditBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final ProtwordsItem entity = createProtwordsItem(this, body, () -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, String.valueOf(body.id)));
            return null;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, String.valueOf(body.id)));
            return null;
        });
        protwordsService.store(body.dictId, entity);
        return asJson(
                new ApiResult.ApiUpdateResponse().id(String.valueOf(entity.getId())).created(false).status(ApiResult.Status.OK).result());
    }

    /**
     * Delete a protected words entry by ID from the specified dictionary.
     *
     * @param dictId identifier of the dictionary
     * @param id identifier of the entry to delete
     * @return JSON response containing deletion result and entry ID
     */
    // DELETE /api/admin/dict/protwords/setting/{dictId}/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String dictId, final long id) {
        protwordsService.getProtwordsItem(dictId, id).ifPresent(entity -> {
            protwordsService.delete(dictId, entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, String.valueOf(id)));
        });
        return asJson(new ApiResult.ApiUpdateResponse().id(String.valueOf(id)).created(false).status(ApiResult.Status.OK).result());
    }

    /**
     * Upload protected words file for the specified dictionary.
     *
     * @param dictId identifier of the dictionary
     * @param form upload form containing the file and metadata
     * @return JSON response indicating the API result status
     */
    // PUT /api/admin/dict/protwords/upload/{dictId}
    @Execute
    public JsonResponse<ApiResult> put$upload(final String dictId, final UploadForm form) {
        form.dictId = dictId;
        validateApi(form, messages -> {});
        final ProtwordsFile file = protwordsService.getProtwordsFile(form.dictId).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsFailedToUploadProtwordsFile(GLOBAL));
            return null;
        });
        try (InputStream inputStream = form.protwordsFile.getInputStream()) {
            file.update(inputStream);
        } catch (final IOException e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsFailedToUploadProtwordsFile(GLOBAL));
        }
        return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
    }

    /**
     * Download the protected words file for the specified dictionary.
     *
     * @param dictId identifier of the dictionary
     * @param body download request payload
     * @return stream response with file content
     */
    // GET /api/admin/dict/protwords/download/{dictId}
    @Execute
    public StreamResponse get$download(final String dictId, final DownloadBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        return protwordsService.getProtwordsFile(body.dictId)
                .map(file -> asStream(new File(file.getPath()).getName()).contentTypeOctetStream().stream(out -> {
                    file.writeOut(out);
                })).orElseGet(() -> {
                    throwValidationErrorApi(messages -> messages.addErrorsFailedToDownloadProtwordsFile(GLOBAL));
                    return null;
                });
    }

    /**
     * Create an EditBody DTO from a ProtwordsItem entity.
     *
     * @param entity source entity with protected words data
     * @param dictId identifier of the dictionary
     * @return populated EditBody for API responses
     */
    protected EditBody createEditBody(final ProtwordsItem entity, final String dictId) {
        final EditBody body = new EditBody();
        body.id = entity.getId();
        body.dictId = dictId;
        body.input = entity.getInputValue();
        return body;
    }
}
