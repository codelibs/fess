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
package org.codelibs.fess.app.web.api.admin.backup;

import static org.codelibs.core.stream.StreamUtil.stream;
import static org.codelibs.fess.app.web.admin.backup.AdminBackupAction.NDJSON_EXTENTION;
import static org.codelibs.fess.app.web.admin.backup.AdminBackupAction.getBackupItems;
import static org.codelibs.fess.app.web.admin.backup.AdminBackupAction.getClickLogNdjsonWriteCall;
import static org.codelibs.fess.app.web.admin.backup.AdminBackupAction.getFavoriteLogNdjsonWriteCall;
import static org.codelibs.fess.app.web.admin.backup.AdminBackupAction.getSearchLogNdjsonWriteCall;
import static org.codelibs.fess.app.web.admin.backup.AdminBackupAction.getUserInfoNdjsonWriteCall;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiBackupFilesResponse;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.response.StreamResponse;

/**
 * @author Keiichi Watanabe
 */
public class ApiAdminBackupAction extends FessApiAdminAction {

    // GET /api/admin/backup/files
    @Execute
    public JsonResponse<ApiResult> files() {
        final List<Map<String, String>> list = getBackupItems();
        return asJson(new ApiBackupFilesResponse().files(list).total(list.size()).status(ApiResult.Status.OK).result());
    }

    // GET /api/admin/backup/file/{id}
    @Execute
    public StreamResponse get$file(final String id) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (stream(fessConfig.getIndexBackupAllTargets()).get(stream -> stream.anyMatch(s -> s.equals(id)))) {
            if (id.equals("system.properties")) {
                return asStream(id).contentTypeOctetStream().stream(out -> {
                    try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        ComponentUtil.getSystemProperties().store(baos, id);
                        try (final InputStream in = new ByteArrayInputStream(baos.toByteArray())) {
                            out.write(in);
                        }
                    }
                });
            } else if (id.endsWith(NDJSON_EXTENTION)) {
                final String name = id.substring(0, id.length() - NDJSON_EXTENTION.length());
                if ("search_log".equals(name)) {
                    return writeNdjsonResponse(id, getSearchLogNdjsonWriteCall());
                } else if ("user_info".equals(name)) {
                    return writeNdjsonResponse(id, getUserInfoNdjsonWriteCall());
                } else if ("click_log".equals(name)) {
                    return writeNdjsonResponse(id, getClickLogNdjsonWriteCall());
                } else if ("favorite_log".equals(name)) {
                    return writeNdjsonResponse(id, getFavoriteLogNdjsonWriteCall());
                }
            } else {
                final String index;
                final String filename;
                if (id.endsWith(".bulk")) {
                    index = id.substring(0, id.length() - 5);
                    filename = id;
                } else {
                    index = id;
                    filename = id + ".bulk";
                }
                return asStream(filename).contentTypeOctetStream().stream(
                        out -> {
                            try (CurlResponse response =
                                    ComponentUtil.getCurlHelper().get("/" + index + "/_data").param("format", "json").execute()) {
                                out.write(response.getContentAsStream());
                            }
                        });
            }
        }

        throwValidationErrorApi(messages -> messages.addErrorsCouldNotFindBackupIndex(GLOBAL));
        return StreamResponse.asEmptyBody(); // no-op
    }

    private StreamResponse writeNdjsonResponse(final String id, final Consumer<Writer> writeCall) {
        return asStream(id)//
                .header("Pragma", "no-cache")//
                .header("Cache-Control", "no-cache")//
                .header("Expires", "Thu, 01 Dec 1994 16:00:00 GMT")//
                .header("Content-Type", "application/x-ndjson")//
                .stream(out -> {
                    try (final Writer writer = new BufferedWriter(new OutputStreamWriter(out.stream(), Constants.CHARSET_UTF_8))) {
                        writeCall.accept(writer);
                        writer.flush();
                    }
                });
    }
}
