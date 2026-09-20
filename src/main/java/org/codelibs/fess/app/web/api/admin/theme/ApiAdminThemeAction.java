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
package org.codelibs.fess.app.web.api.admin.theme;

import static org.codelibs.fess.app.web.admin.theme.AdminThemeAction.mapInstallExceptionToMessage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.theme.StaticThemeInstaller;
import org.codelibs.fess.theme.ThemeArtifactHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;

/**
 * API action for installing static themes published in a theme repository.
 */
public class ApiAdminThemeAction extends FessApiAdminAction {

    /**
     * Default constructor.
     */
    public ApiAdminThemeAction() {
    }

    /**
     * Retrieves the themes available for this Fess line.
     *
     * @return JSON response containing the available themes
     */
    // GET /api/admin/theme/available
    @Execute
    public JsonResponse<ApiResult> get$available() {
        final List<Map<String, String>> list = new ArrayList<>();
        for (final ThemeArtifactHelper.ThemeArtifact a : ComponentUtil.getThemeArtifactHelper().getAvailableArtifacts()) {
            final Map<String, String> item = new LinkedHashMap<>();
            item.put("name", a.getName());
            item.put("version", a.getVersion());
            list.add(item);
        }
        return asJson(new ApiResult.ApiThemeResponse().themes(list).status(ApiResult.Status.OK).result());
    }

    /**
     * Installs the theme with the given name and version.
     *
     * @param body the install body carrying name and version
     * @return JSON response indicating success or failure
     */
    // POST /api/admin/theme
    @Execute
    public JsonResponse<ApiResult> post$index(final InstallBody body) {
        validateApi(body, messages -> {});
        try {
            ComponentUtil.getThemeArtifactHelper().install(body.name, body.version);
        } catch (final StaticThemeInstaller.InstallException e) {
            // Carries the structured Code, so INCOMPATIBLE_FESS_VERSION reaches
            // the caller as its own message rather than a generic failure.
            throwValidationErrorApi(messages -> mapInstallExceptionToMessage(messages, e));
        } catch (final ThemeArtifactHelper.ThemeArtifactException e) {
            throwValidationErrorApi(messages -> messages.addErrorsFailedToInstallTheme(GLOBAL, String.valueOf(e.getMessage())));
        }
        return asJson(new ApiResult.ApiResponse().status(ApiResult.Status.OK).result());
    }
}
