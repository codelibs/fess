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
package org.codelibs.fess.app.web.api.admin.dict.stemmeroverride;

import static org.codelibs.fess.app.web.admin.dict.stemmeroverride.AdminDictStemmeroverrideAction.createStemmerOverrideItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.pager.StemmerOverridePager;
import org.codelibs.fess.app.service.StemmerOverrideService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.dict.stemmeroverride.UploadForm;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.dict.stemmeroverride.StemmerOverrideFile;
import org.codelibs.fess.dict.stemmeroverride.StemmerOverrideItem;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.response.StreamResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin Stemmer Override dictionary management.
 * Provides REST endpoints for managing stemmer override dictionary items in the Fess search engine.
 */
public class ApiAdminDictStemmeroverrideAction extends FessApiAdminAction {

    /**
     * Default constructor.
     */
    public ApiAdminDictStemmeroverrideAction() {
        super();
    }

    private static final Logger logger = LogManager.getLogger(ApiAdminDictStemmeroverrideAction.class);

    @Resource
    private StemmerOverrideService stemmerOverrideService;

    /**
     * Retrieves stemmer override dictionary settings with pagination support.
     *
     * @param dictId the dictionary ID
     * @param body the search body containing pagination and filter parameters
     * @return JSON response containing list of stemmer override dictionary items
     */
    // GET /api/admin/dict/stemmerOverride/settings/{dictId}
    @Execute
    public JsonResponse<ApiResult> get$settings(final String dictId, final SearchBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        final StemmerOverridePager pager = copyBeanToNewBean(body, StemmerOverridePager.class);
        return asJson(
                new ApiResult.ApiConfigsResponse<EditBody>().settings(stemmerOverrideService.getStemmerOverrideList(body.dictId, pager)
                        .stream()
                        .map(protwordsItem -> createEditBody(protwordsItem, dictId))
                        .collect(Collectors.toList())).status(ApiResult.Status.OK).result());
    }

    /**
     * Retrieves a specific stemmer override dictionary item by ID.
     *
     * @param dictId the dictionary ID
     * @param id the ID of the stemmer override item to retrieve
     * @return JSON response containing the stemmer override dictionary item
     */
    // GET /api/admin/dict/stemmerOverride/setting/{dictId}/{id}
    @Execute
    public JsonResponse<ApiResult> get$setting(final String dictId, final long id) {
        return asJson(new ApiResult.ApiConfigResponse().setting(
                stemmerOverrideService.getStemmerOverrideItem(dictId, id).map(entity -> createEditBody(entity, dictId)).orElseGet(() -> {
                    throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, String.valueOf(id)));
                    return null;
                })).status(ApiResult.Status.OK).result());
    }

    /**
     * Creates a new stemmer override dictionary item.
     *
     * @param dictId the dictionary ID
     * @param body the request body containing stemmer override item information
     * @return JSON response with result status
     */
    // POST /api/admin/dict/stemmerOverride/setting/{dictId}
    @Execute
    public JsonResponse<ApiResult> post$setting(final String dictId, final CreateBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final StemmerOverrideItem entity = createStemmerOverrideItem(this, body, () -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });
        stemmerOverrideService.store(body.dictId, entity);
        return asJson(
                new ApiResult.ApiUpdateResponse().id(String.valueOf(entity.getId())).created(true).status(ApiResult.Status.OK).result());
    }

    /**
     * Updates an existing stemmer override dictionary item.
     *
     * @param dictId the dictionary ID
     * @param body the request body containing updated stemmer override item information
     * @return JSON response with result status
     */
    // PUT /api/admin/dict/stemmerOverride/setting/{dictId}
    @Execute
    public JsonResponse<ApiResult> put$setting(final String dictId, final EditBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final StemmerOverrideItem entity = createStemmerOverrideItem(this, body, () -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, String.valueOf(body.id)));
            return null;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, String.valueOf(body.id)));
            return null;
        });
        stemmerOverrideService.store(body.dictId, entity);
        return asJson(
                new ApiResult.ApiUpdateResponse().id(String.valueOf(entity.getId())).created(false).status(ApiResult.Status.OK).result());
    }

    /**
     * Deletes a stemmer override dictionary item by ID.
     *
     * @param dictId the dictionary ID
     * @param id the ID of the stemmer override item to delete
     * @return JSON response indicating the deletion status
     */
    // DELETE /api/admin/dict/stemmerOverride/setting/{dictId}/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String dictId, final long id) {
        stemmerOverrideService.getStemmerOverrideItem(dictId, id).ifPresent(entity -> {
            stemmerOverrideService.delete(dictId, entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, String.valueOf(id)));
        });
        return asJson(new ApiResult.ApiUpdateResponse().id(String.valueOf(id)).created(false).status(ApiResult.Status.OK).result());
    }

    /**
     * Uploads stemmer override dictionary file.
     *
     * @param dictId the dictionary ID
     * @param form the upload form containing the dictionary file
     * @return JSON response with result status
     */
    // PUT /api/admin/dict/stemmerOverride/upload/{dictId}
    @Execute
    public JsonResponse<ApiResult> put$upload(final String dictId, final UploadForm form) {
        form.dictId = dictId;
        validateApi(form, messages -> {});
        final StemmerOverrideFile file = stemmerOverrideService.getStemmerOverrideFile(form.dictId).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsFailedToUploadProtwordsFile(GLOBAL));
            return null;
        });
        try (InputStream inputStream = form.stemmerOverrideFile.getInputStream()) {
            file.update(inputStream);
        } catch (final IOException e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsFailedToUploadProtwordsFile(GLOBAL));
        }
        return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
    }

    /**
     * Downloads stemmer override dictionary file.
     *
     * @param dictId the dictionary ID
     * @param body the download request body
     * @return stream response containing the dictionary file data
     */
    // GET /api/admin/dict/stemmerOverride/download/{dictId}
    @Execute
    public StreamResponse get$download(final String dictId, final DownloadBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        return stemmerOverrideService.getStemmerOverrideFile(body.dictId)
                .map(file -> asStream(new File(file.getPath()).getName()).contentTypeOctetStream().stream(out -> {
                    file.writeOut(out);
                }))
                .orElseGet(() -> {
                    throwValidationErrorApi(messages -> messages.addErrorsFailedToDownloadProtwordsFile(GLOBAL));
                    return null;
                });
    }

    /**
     * Creates an EditBody from a StemmerOverrideItem entity for API responses.
     *
     * @param entity the StemmerOverrideItem entity to convert
     * @param dictId the dictionary ID
     * @return the converted EditBody object
     */
    protected EditBody createEditBody(final StemmerOverrideItem entity, final String dictId) {
        final EditBody body = new EditBody();
        body.id = entity.getId();
        body.dictId = dictId;
        body.input = entity.getInput();
        body.output = entity.getOutput();
        return body;
    }
}
