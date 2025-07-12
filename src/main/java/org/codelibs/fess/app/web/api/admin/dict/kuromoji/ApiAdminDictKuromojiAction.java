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
package org.codelibs.fess.app.web.api.admin.dict.kuromoji;

import static org.codelibs.fess.app.web.admin.dict.kuromoji.AdminDictKuromojiAction.createKuromojiItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.app.pager.KuromojiPager;
import org.codelibs.fess.app.service.KuromojiService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.admin.dict.kuromoji.UploadForm;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.dict.kuromoji.KuromojiFile;
import org.codelibs.fess.dict.kuromoji.KuromojiItem;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.response.StreamResponse;

import jakarta.annotation.Resource;

/**
 * API action for admin Kuromoji dictionary management.
 * Provides REST endpoints for managing Kuromoji dictionary items in the Fess search engine.
 *
 * @author FessProject
 */
public class ApiAdminDictKuromojiAction extends FessApiAdminAction {

    /**
     * Default constructor.
     */
    public ApiAdminDictKuromojiAction() {
        // Default constructor
    }

    private static final Logger logger = LogManager.getLogger(ApiAdminDictKuromojiAction.class);

    @Resource
    private KuromojiService kuromojiService;

    /**
     * Retrieves Kuromoji dictionary settings with pagination support.
     *
     * @param dictId the dictionary ID
     * @param body the search body containing pagination and filter parameters
     * @return JSON response containing list of Kuromoji dictionary items
     */
    // GET /api/admin/dict/kuromoji/settings/{dictId}
    @Execute
    public JsonResponse<ApiResult> get$settings(final String dictId, final SearchBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        final KuromojiPager pager = copyBeanToNewBean(body, KuromojiPager.class);
        return asJson(new ApiResult.ApiConfigsResponse<EditBody>()
                .settings(kuromojiService.getKuromojiList(body.dictId, pager).stream()
                        .map(protwordsItem -> createEditBody(protwordsItem, dictId)).collect(Collectors.toList()))
                .status(ApiResult.Status.OK).result());
    }

    /**
     * Retrieves a specific Kuromoji dictionary item by ID.
     *
     * @param dictId the dictionary ID
     * @param id the ID of the Kuromoji item to retrieve
     * @return JSON response containing the Kuromoji dictionary item
     */
    // GET /api/admin/dict/kuromoji/setting/{dictId}/{id}
    @Execute
    public JsonResponse<ApiResult> get$setting(final String dictId, final long id) {
        return asJson(new ApiResult.ApiConfigResponse()
                .setting(kuromojiService.getKuromojiItem(dictId, id).map(entity -> createEditBody(entity, dictId)).orElseGet(() -> {
                    throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, String.valueOf(id)));
                    return null;
                })).status(ApiResult.Status.OK).result());
    }

    /**
     * Creates a new Kuromoji dictionary item.
     *
     * @param dictId the dictionary ID
     * @param body the request body containing Kuromoji item information
     * @return JSON response with result status
     */
    // POST /api/admin/dict/kuromoji/setting/{dictId}
    @Execute
    public JsonResponse<ApiResult> post$setting(final String dictId, final CreateBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.CREATE;
        final KuromojiItem entity = createKuromojiItem(this, body, () -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });
        kuromojiService.store(body.dictId, entity);
        return asJson(
                new ApiResult.ApiUpdateResponse().id(String.valueOf(entity.getId())).created(true).status(ApiResult.Status.OK).result());
    }

    /**
     * Updates an existing Kuromoji dictionary item.
     *
     * @param dictId the dictionary ID
     * @param body the request body containing updated Kuromoji item information
     * @return JSON response with result status
     */
    // PUT /api/admin/dict/kuromoji/setting/{dictId}
    @Execute
    public JsonResponse<ApiResult> put$setting(final String dictId, final EditBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        body.crudMode = CrudMode.EDIT;
        final KuromojiItem entity = createKuromojiItem(this, body, () -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, String.valueOf(body.id)));
            return null;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, String.valueOf(body.id)));
            return null;
        });
        kuromojiService.store(body.dictId, entity);
        return asJson(
                new ApiResult.ApiUpdateResponse().id(String.valueOf(entity.getId())).created(false).status(ApiResult.Status.OK).result());
    }

    /**
     * Deletes a Kuromoji dictionary item by ID.
     *
     * @param dictId the dictionary ID
     * @param id the ID of the Kuromoji item to delete
     * @return JSON response indicating the deletion status
     */
    // DELETE /api/admin/dict/kuromoji/setting/{dictId}/{id}
    @Execute
    public JsonResponse<ApiResult> delete$setting(final String dictId, final long id) {
        kuromojiService.getKuromojiItem(dictId, id).ifPresent(entity -> {
            kuromojiService.delete(dictId, entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, String.valueOf(id)));
        });
        return asJson(new ApiResult.ApiUpdateResponse().id(String.valueOf(id)).created(false).status(ApiResult.Status.OK).result());
    }

    /**
     * Uploads Kuromoji dictionary file.
     *
     * @param dictId the dictionary ID
     * @param form the upload form containing the dictionary file
     * @return JSON response with result status
     */
    // PUT /api/admin/dict/kuromoji/upload/{dictId}
    @Execute
    public JsonResponse<ApiResult> put$upload(final String dictId, final UploadForm form) {
        form.dictId = dictId;
        validateApi(form, messages -> {});
        final KuromojiFile file = kuromojiService.getKuromojiFile(form.dictId).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsFailedToUploadProtwordsFile(GLOBAL));
            return null;
        });
        try (InputStream inputStream = form.kuromojiFile.getInputStream()) {
            file.update(inputStream);
        } catch (final IOException e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsFailedToUploadProtwordsFile(GLOBAL));
        }
        return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
    }

    /**
     * Downloads Kuromoji dictionary file.
     *
     * @param dictId the dictionary ID
     * @param body the download request body
     * @return stream response containing the dictionary file data
     */
    // GET /api/admin/dict/kuromoji/download/{dictId}
    @Execute
    public StreamResponse get$download(final String dictId, final DownloadBody body) {
        body.dictId = dictId;
        validateApi(body, messages -> {});
        return kuromojiService.getKuromojiFile(body.dictId)
                .map(file -> asStream(new File(file.getPath()).getName()).contentTypeOctetStream().stream(out -> {
                    file.writeOut(out);
                })).orElseGet(() -> {
                    throwValidationErrorApi(messages -> messages.addErrorsFailedToDownloadProtwordsFile(GLOBAL));
                    return null;
                });
    }

    /**
     * Creates an EditBody from a KuromojiItem entity for API responses.
     *
     * @param entity the KuromojiItem entity to convert
     * @param dictId the dictionary ID
     * @return the converted EditBody object
     */
    protected EditBody createEditBody(final KuromojiItem entity, final String dictId) {
        final EditBody body = new EditBody();
        body.id = entity.getId();
        body.dictId = dictId;
        body.token = entity.getToken();
        body.reading = entity.getReading();
        body.pos = entity.getPos();
        body.segmentation = entity.getSegmentation();
        return body;
    }
}
