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
package org.codelibs.fess.app.web.api.admin.log;

import static org.codelibs.fess.app.web.admin.log.AdminLogAction.decodeLogFileId;
import static org.codelibs.fess.app.web.admin.log.AdminLogAction.findLogFile;
import static org.codelibs.fess.app.web.admin.log.AdminLogAction.getLogFileItems;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.response.StreamResponse;

/**
 * API action for admin log management.
 *
 */
public class ApiAdminLogAction extends FessApiAdminAction {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Default constructor.
     */
    public ApiAdminLogAction() {
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    /**
     * Retrieves the list of available log files.
     *
     * @return JSON response containing log file list
     */
    // GET /api/admin/log/files
    @Execute
    public JsonResponse<ApiResult> files() {
        final List<Map<String, Object>> list = getLogFileItems();
        return asJson(new ApiResult.ApiLogFilesResponse().files(list).total(list.size()).status(ApiResult.Status.OK).result());
    }

    /**
     * Downloads a specific log file by ID.
     *
     * @param id the base64-encoded filename of the log file to download
     * @return stream response containing the log file content
     */
    // GET /api/admin/log/file/{id}
    @Execute
    public StreamResponse get$file(final String id) {
        final String decoded;
        try {
            decoded = decodeLogFileId(id);
        } catch (final IllegalArgumentException e) {
            throwValidationErrorApi(messages -> messages.addErrorsCouldNotFindLogFile(GLOBAL, id));
            return null; // ignore
        }
        final Path path = findLogFile(systemHelper.getLogFilePath(), decoded);
        if (path != null) {
            return asStream(path.getFileName().toString()).contentTypeOctetStream().stream(out -> {
                try (InputStream in = Files.newInputStream(path)) {
                    out.write(in);
                }
            });
        }
        throwValidationErrorApi(messages -> messages.addErrorsCouldNotFindLogFile(GLOBAL, id));
        return null; // ignore
    }
}
