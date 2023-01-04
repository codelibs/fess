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
package org.codelibs.fess.app.web.api.admin.plugin;

import static org.codelibs.fess.app.web.admin.plugin.AdminPluginAction.deleteArtifact;
import static org.codelibs.fess.app.web.admin.plugin.AdminPluginAction.getAllAvailableArtifacts;
import static org.codelibs.fess.app.web.admin.plugin.AdminPluginAction.getAllInstalledArtifacts;
import static org.codelibs.fess.app.web.admin.plugin.AdminPluginAction.installArtifact;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.helper.PluginHelper.Artifact;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

public class ApiAdminPluginAction extends FessApiAdminAction {

    // GET /api/admin/plugin/installed
    @Execute
    public JsonResponse<ApiResult> get$installed() {
        final List<Map<String, String>> list = getAllInstalledArtifacts();
        return asJson(new ApiResult.ApiPluginResponse().plugins(list).status(ApiResult.Status.OK).result());
    }

    // GET /api/admin/plugin/available
    @Execute
    public JsonResponse<ApiResult> get$available() {
        final List<Map<String, String>> list = getAllAvailableArtifacts();
        return asJson(new ApiResult.ApiPluginResponse().plugins(list).status(ApiResult.Status.OK).result());
    }

    // PUT /api/admin/plugin
    @Execute
    public JsonResponse<ApiResult> put$index(final InstallBody body) {
        validateApi(body, messages -> {});
        final Artifact artifact = ComponentUtil.getPluginHelper().getArtifact(body.name, body.version);
        if (artifact == null) {
            return asJson(
                    new ApiResult.ApiErrorResponse().message("invalid name or version").status(ApiResult.Status.BAD_REQUEST).result());
        }
        installArtifact(artifact);
        return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
    }

    // DELETE /api/admin/plugin
    @Execute
    public JsonResponse<ApiResult> delete$index(final DeleteBody body) {
        validateApi(body, messages -> {});
        deleteArtifact(new Artifact(body.name, body.version));
        return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
    }

}
