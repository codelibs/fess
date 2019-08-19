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
package org.codelibs.fess.app.web.api.admin.plugin;

import static org.codelibs.fess.app.web.admin.plugin.AdminPluginAction.getAllInstalledArtifacts;
import static org.codelibs.fess.app.web.admin.plugin.AdminPluginAction.getAllAvailableArtifacts;
import static org.codelibs.fess.app.web.admin.plugin.AdminPluginAction.installArtifact;
import static org.codelibs.fess.app.web.admin.plugin.AdminPluginAction.deleteArtifact;

import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.helper.PluginHelper.Artifact;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

import java.util.List;
import java.util.Map;

public class ApiAdminPluginAction extends FessApiAdminAction {
    @Execute
    public JsonResponse<ApiResult> installedplugins() {
        final List<Map<String, String>> list = getAllInstalledArtifacts();
        return asJson(new ApiResult.ApiPluginResponse().plugins(list).status(ApiResult.Status.OK).result());
    }

    @Execute
    public JsonResponse<ApiResult> availableplugins() {
        final List<Map<String, String>> list = getAllAvailableArtifacts();
        return asJson(new ApiResult.ApiPluginResponse().plugins(list).status(ApiResult.Status.OK).result());
    }

    @Execute
    public JsonResponse<ApiResult> install(final InstallBody body) {
        validateApi(body, messages -> {});
        installArtifact(new Artifact(body.name, body.version, body.url));
        return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
    }

    @Execute
    public JsonResponse<ApiResult> delete(final DeleteBody body) {
        validateApi(body, messages -> {});
        deleteArtifact(new Artifact(body.name, body.version, null));
        return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
    }
}
