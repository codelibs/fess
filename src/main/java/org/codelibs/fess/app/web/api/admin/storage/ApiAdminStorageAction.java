/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import static org.codelibs.fess.app.web.admin.storage.AdminStorageAction.decodeId;
import static org.codelibs.fess.app.web.admin.storage.AdminStorageAction.decodePath;
import static org.codelibs.fess.app.web.admin.storage.AdminStorageAction.getFileItems;
import static org.codelibs.fess.app.web.admin.storage.AdminStorageAction.deleteObject;
import static org.codelibs.fess.app.web.admin.storage.AdminStorageAction.downloadObject;
import static org.codelibs.fess.app.web.admin.storage.AdminStorageAction.getObjectName;
import static org.codelibs.fess.app.web.admin.storage.AdminStorageAction.uploadObject;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.exception.ResultOffsetExceededException;
import org.codelibs.fess.exception.StorageException;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.response.StreamResponse;

public class ApiAdminStorageAction extends FessApiAdminAction {

    private static final Logger logger = LogManager.getLogger(ApiAdminStorageAction.class);

    // GET /api/admin/storage/list/{id}
    // POST /api/admin/storage/list/{id}
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
    @Execute
    public StreamResponse get$download(final String id) {
        final String[] values = decodeId(id);
        if (StringUtil.isEmpty(values[1])) {
            throwValidationErrorApi(messages -> messages.addErrorsStorageFileNotFound(GLOBAL));
        }
        return asStream(values[1]).contentTypeOctetStream().stream(out -> {
            try {
                downloadObject(getObjectName(values[0], values[1]), out);
            } catch (final StorageException e) {
                throwValidationErrorApi(messages -> messages.addErrorsStorageFileDownloadFailure(GLOBAL, values[1]));
            }
        });
    }

    // DELETE /api/admin/storage/delete/{id}/
    @Execute
    public JsonResponse<ApiResult> delete$delete(final String id) {
        final String[] values = decodeId(id);
        if (StringUtil.isEmpty(values[1])) {
            throwValidationErrorApi(messages -> messages.addErrorsStorageAccessError(GLOBAL, "id is invalid"));
        }
        final String objectName = getObjectName(values[0], values[1]);
        try {
            deleteObject(objectName);
            saveInfo(messages -> messages.addSuccessDeleteFile(GLOBAL, values[1]));
            return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
        } catch (final StorageException e) {
            throwValidationErrorApi(messages -> messages.addErrorsFailedToDeleteFile(GLOBAL, values[1]));
        }
        return null;
    }

    // POST /api/admin/storage/upload/{pathId}/
    @Execute
    public JsonResponse<ApiResult> post$upload(final String pathId, final UploadForm form) {
        validateApi(form, messages -> {});
        if (form.uploadFile == null) {
            throwValidationErrorApi(messages -> messages.addErrorsStorageNoUploadFile(GLOBAL));
        }
        try {
            uploadObject(getObjectName(decodeId(pathId)[0], form.uploadFile.getFileName()), form.uploadFile);
            saveInfo(messages -> messages.addSuccessUploadFileToStorage(GLOBAL, form.uploadFile.getFileName()));
            return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
        } catch (final StorageException e) {
            throwValidationErrorApi(messages -> messages.addErrorsStorageFileUploadFailure(GLOBAL, form.uploadFile.getFileName()));
        }
        return null;
    }

}
