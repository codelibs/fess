/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import static org.codelibs.fess.app.web.admin.log.AdminLogAction.getLogFileItems;
import static org.codelibs.fess.app.web.admin.log.AdminLogAction.isLogFilename;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.response.StreamResponse;

/**
 * @author Keiichi Watanabe
 */
public class ApiAdminLogAction extends FessApiAdminAction {

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    // GET /api/admin/log/files
    @Execute
    public JsonResponse<ApiResult> files() {
        final List<Map<String, Object>> list = getLogFileItems();
        return asJson(new ApiResult.ApiLogFilesResponse().files(list).total(list.size()).status(ApiResult.Status.OK).result());
    }

    // GET /api/admin/log/file/{id}
    @Execute
    public StreamResponse get$file(final String id) {
        final String filename = new String(Base64.getDecoder().decode(id), StandardCharsets.UTF_8).replace("..", "").replaceAll("\\s", "");
        final String logFilePath = systemHelper.getLogFilePath();
        if (StringUtil.isNotBlank(logFilePath) && isLogFilename(filename)) {
            final Path path = Paths.get(logFilePath, filename);
            return asStream(filename).contentTypeOctetStream().stream(out -> {
                try (InputStream in = Files.newInputStream(path)) {
                    out.write(in);
                }
            });
        }
        return StreamResponse.asEmptyBody();
    }
}
