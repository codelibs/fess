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
package org.codelibs.fess.app.web.api.admin.storage;

import static org.codelibs.fess.app.web.admin.storage.AdminStorageAction.convertToItem;
import static org.codelibs.fess.app.web.admin.storage.AdminStorageAction.decodePath;
import static org.codelibs.fess.app.web.admin.storage.AdminStorageAction.deleteObject;
import static org.codelibs.fess.app.web.admin.storage.AdminStorageAction.downloadObject;
import static org.codelibs.fess.app.web.admin.storage.AdminStorageAction.getFileItems;
import static org.codelibs.fess.app.web.admin.storage.AdminStorageAction.getObjectName;
import static org.codelibs.fess.app.web.admin.storage.AdminStorageAction.uploadObject;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.web.admin.storage.AdminStorageAction.PathInfo;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.exception.ResultOffsetExceededException;
import org.codelibs.fess.exception.StorageException;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.response.StreamResponse;

/**
 * Handles API requests for storage management in the Fess application.
 * Provides endpoints for listing, downloading, deleting, and uploading files.
 */
public class ApiAdminStorageAction extends FessApiAdminAction {

    private static final Logger logger = LogManager.getLogger(ApiAdminStorageAction.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Default constructor.
     */
    public ApiAdminStorageAction() {
        // Default constructor
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    // GET /api/admin/storage/list/{id}
    // PUT /api/admin/storage/list/{id}
    /**
     * Lists files and directories in storage.
     * @param id The ID of the directory to list.
     * @return A JSON response containing the list of files and directories.
     */
    @Execute
    public JsonResponse<ApiResult> list(final OptionalThing<String> id) {
        final List<Map<String, Object>> list = getFileItems(id.isPresent() ? decodePath(id.get()) : null);
        try {
            return asJson(new ApiResult.ApiStorageResponse().items(list).status(ApiResult.Status.OK).result());
        } catch (final ResultOffsetExceededException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throwValidationErrorApi(messages -> messages.addErrorsResultSizeExceeded(GLOBAL));
        }

        return null;
    }

    // GET /api/admin/storage/download/{id}/
    /**
     * Downloads a file from storage.
     * @param id The ID of the file to download.
     * @return A StreamResponse containing the file content.
     */
    @Execute
    public StreamResponse get$download(final String id) {
        final PathInfo pi = convertToItem(id);
        if (StringUtil.isEmpty(pi.getName())) {
            throwValidationErrorApi(messages -> messages.addErrorsStorageFileNotFound(GLOBAL));
        }
        return asStream(pi.getName()).contentTypeOctetStream().stream(out -> {
            try {
                downloadObject(getObjectName(pi.getPath(), pi.getName()), out);
            } catch (final StorageException e) {
                logger.warn("Failed to download {}", id, e);
                throwValidationErrorApi(messages -> messages.addErrorsStorageFileDownloadFailure(GLOBAL, pi.getName()));
            }
        });
    }

    // DELETE /api/admin/storage/delete/{id}/
    /**
     * Deletes a file from storage.
     * @param id The ID of the file to delete.
     * @return A JSON response indicating the success or failure of the operation.
     */
    @Execute
    public JsonResponse<ApiResult> delete$delete(final String id) {
        final PathInfo pi = convertToItem(id);
        if (StringUtil.isEmpty(pi.getName())) {
            throwValidationErrorApi(messages -> messages.addErrorsStorageAccessError(GLOBAL, "id is invalid"));
        }
        final String objectName = getObjectName(pi.getPath(), pi.getName());
        try {
            deleteObject(objectName);
            saveInfo(messages -> messages.addSuccessDeleteFile(GLOBAL, pi.getName()));
            return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
        } catch (final StorageException e) {
            logger.warn("Failed to delete {}", id, e);
            throwValidationErrorApi(messages -> messages.addErrorsFailedToDeleteFile(GLOBAL, pi.getName()));
        }
        return null;
    }

    // curl -XPOST -H "Authorization: CHANGEME" localhost:8080/api/admin/storage/upload/ -F path=/ -F file=@...
    // PUT /api/admin/storage/upload/{pathId}/
    /**
     * Uploads a file to storage.
     * @param form The form containing the file to upload and the target path.
     * @return A JSON response indicating the success or failure of the operation.
     */
    @Execute
    public JsonResponse<ApiResult> put$upload(final UploadForm form) {
        validateApi(form, messages -> {});
        if (form.file == null) {
            throwValidationErrorApi(messages -> messages.addErrorsStorageNoUploadFile(GLOBAL));
        }
        final String fileName = form.file.getFileName();
        try {
            uploadObject(getObjectName(form.path, fileName), form.file);
            saveInfo(messages -> messages.addSuccessUploadFileToStorage(GLOBAL, fileName));
            return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
        } catch (final StorageException e) {
            logger.warn("Failed to upload {}", fileName, e);
            throwValidationErrorApi(messages -> messages.addErrorsStorageFileUploadFailure(GLOBAL, fileName));
        }
        return null;
    }

}
