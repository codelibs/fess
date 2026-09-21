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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.helper.ThemeArtifactHelper;
import org.codelibs.fess.theme.StaticThemeInstaller;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.validation.Required;

/**
 * Contract tests for {@code GET /api/admin/theme/available} and {@code POST /api/admin/theme}.
 *
 * <p>Each test drives the real {@code get$available}/{@code post$index} execute methods with a
 * stubbed {@link ThemeArtifactHelper} registered under its component name (the action looks it up
 * via {@code ComponentUtil.getThemeArtifactHelper()} on every call rather than through an injected
 * field), so nothing here reaches the network -- the un-stubbed helper reads the production
 * {@code theme.repositories} default and would otherwise make a live HTTP call. Every test
 * registers its own stub before calling the action, following the same
 * register-before-use convention as {@code ApiAdminSearchlistActionTest}; {@code ComponentUtil}
 * offers no per-test scoping to undo it afterwards.</p>
 */
public class ApiAdminThemeActionTest extends UnitFessTestCase {

    // ===================================================================================
    //                                                                  get$available
    //                                                                  ===============

    @Test
    public void test_get$available_returnsTheCatalogueAsNameVersionPairs() throws Exception {
        final ThemeArtifactHelper stub = new ThemeArtifactHelper() {
            @Override
            public List<ThemeArtifact> getAvailableArtifacts() {
                final List<ThemeArtifact> list = new ArrayList<>();
                list.add(new ThemeArtifact("docuforge", "15.8.1", "https://example.invalid/docuforge/15.8.1/docuforge-15.8.1.zip"));
                list.add(new ThemeArtifact("code-search", "15.8.0", "https://example.invalid/code-search/15.8.0/code-search-15.8.0.zip"));
                return list;
            }
        };
        ComponentUtil.register(stub, "themeArtifactHelper");

        final ApiAdminThemeAction action = createInjectedAction();
        final List<Map<String, String>> themes = themesOf(action.get$available());

        assertEquals(2, themes.size());
        assertEquals("docuforge", themes.get(0).get("name"));
        assertEquals("15.8.1", themes.get(0).get("version"));
        assertEquals("code-search", themes.get(1).get("name"));
        assertEquals("15.8.0", themes.get(1).get("version"));
    }

    @Test
    public void test_get$available_emptyCatalogue_returnsEmptyListNotNull() throws Exception {
        final ThemeArtifactHelper stub = new ThemeArtifactHelper() {
            @Override
            public List<ThemeArtifact> getAvailableArtifacts() {
                return List.of();
            }
        };
        ComponentUtil.register(stub, "themeArtifactHelper");

        final ApiAdminThemeAction action = createInjectedAction();
        final List<Map<String, String>> themes = themesOf(action.get$available());

        assertNotNull(themes);
        assertTrue(themes.isEmpty());
    }

    // ===================================================================================
    //                                                                     post$index
    //                                                                     ============

    @Test
    public void test_post$index_installsThroughTheHelper_withoutConsultingTheCatalogue() throws Exception {
        // The decisive behaviour for this endpoint: install() is given the name/version straight
        // from the request and never routes through getAvailableArtifacts(). An operator naming a
        // published theme must not be blocked by a catalogue that is momentarily unreadable, so
        // getAvailableArtifacts() here throws -- if post$index ever "tidies" the two together, this
        // test starts failing instead of silently passing for the wrong reason.
        final AtomicReference<String> installedName = new AtomicReference<>();
        final AtomicReference<String> installedVersion = new AtomicReference<>();
        final AtomicBoolean catalogueConsulted = new AtomicBoolean(false);
        final ThemeArtifactHelper stub = new ThemeArtifactHelper() {
            @Override
            public List<ThemeArtifact> getAvailableArtifacts() {
                catalogueConsulted.set(true);
                throw new IllegalStateException("the catalogue must not be consulted by install");
            }

            @Override
            public void install(final String name, final String version) {
                installedName.set(name);
                installedVersion.set(version);
            }
        };
        ComponentUtil.register(stub, "themeArtifactHelper");

        final ApiAdminThemeAction action = createInjectedAction();
        final InstallBody body = new InstallBody();
        body.name = "docuforge";
        body.version = "15.8.1";

        assertOkResponse(action.post$index(body));

        assertFalse(catalogueConsulted.get(), "post$index must not call getAvailableArtifacts()");
        assertEquals("docuforge", installedName.get());
        assertEquals("15.8.1", installedVersion.get());
    }

    @Test
    public void test_post$index_missingRequiredFields_isRejected() throws Exception {
        final ThemeArtifactHelper stub = new ThemeArtifactHelper() {
            @Override
            public void install(final String name, final String version) {
                fail("install() must not be reached when the request fails validation");
            }
        };
        ComponentUtil.register(stub, "themeArtifactHelper");

        final ApiAdminThemeAction action = createInjectedAction();
        final InstallBody body = new InstallBody();

        assertValidationError(() -> action.post$index(body)).handle(data -> {
            data.requiredMessageOf("name", Required.class);
            data.requiredMessageOf("version", Required.class);
        });
    }

    @Test
    public void test_post$index_incompatibleFessVersion_reportsTheStructuredMessage() throws Exception {
        // Task 1's whole point: a theme needing a newer Fess says so, not "install failed". This
        // pins that the API reaches the same mapInstallExceptionToMessage the admin screen uses,
        // rather than a duplicated (and possibly drifted) copy.
        final ThemeArtifactHelper stub = new ThemeArtifactHelper() {
            @Override
            public void install(final String name, final String version) {
                throw new StaticThemeInstaller.InstallException(StaticThemeInstaller.InstallException.Code.INCOMPATIBLE_FESS_VERSION,
                        "Theme docuforge requires Fess 99.0 or later, but this server is 15.8");
            }
        };
        ComponentUtil.register(stub, "themeArtifactHelper");

        final ApiAdminThemeAction action = createInjectedAction();
        final InstallBody body = new InstallBody();
        body.name = "docuforge";
        body.version = "15.8.1";

        assertValidationError(() -> action.post$index(body)).handle(data -> {
            data.requiredMessageOf("_global", "errors.theme_incompatible_fess_version");
        });
    }

    @Test
    public void test_post$index_themeArtifactException_reportsTheGenericFailureMessage() throws Exception {
        final ThemeArtifactHelper stub = new ThemeArtifactHelper() {
            @Override
            public void install(final String name, final String version) {
                throw new ThemeArtifactHelper.ThemeArtifactException(
                        "HTTP 404 for https://example.invalid/docuforge/9.9.9/docuforge-9.9.9.zip");
            }
        };
        ComponentUtil.register(stub, "themeArtifactHelper");

        final ApiAdminThemeAction action = createInjectedAction();
        final InstallBody body = new InstallBody();
        body.name = "docuforge";
        body.version = "9.9.9";

        assertValidationError(() -> action.post$index(body)).handle(data -> {
            data.requiredMessageOf("_global", "errors.failed_to_install_theme");
        });
    }

    // ===================================================================================
    //                                                                             Helpers
    //                                                                             =======

    /**
     * Wires an {@link ApiAdminThemeAction} through UTFlute's {@code inject()} for the framework
     * fields that {@code validateApi()}/{@code asJson()} need. Mirrors
     * {@code ApiAdminSearchlistActionTest#createInjectedAction}.
     *
     * @return the action, ready to have its execute methods called
     * @throws Exception if the reflective wiring fails
     */
    private ApiAdminThemeAction createInjectedAction() throws Exception {
        suppressBindingOf(org.codelibs.fess.app.web.base.login.FessLoginAssist.class);
        // FessApiAction declares @Resource AccessTokenService, whose own AccessTokenBhv @Resource
        // cannot be assembled in the unit container; only isAccessAllowed() (never reached when the
        // execute method is called directly) uses it.
        suppressBindingOf(org.codelibs.fess.app.service.AccessTokenService.class);
        final ApiAdminThemeAction action = new ApiAdminThemeAction();
        inject(action);

        final org.codelibs.fess.helper.SystemHelper systemHelperInstance = new org.codelibs.fess.helper.SystemHelper();
        final Field systemHelperField = org.codelibs.fess.app.web.base.FessBaseAction.class.getDeclaredField("systemHelper");
        systemHelperField.setAccessible(true);
        if (systemHelperField.get(action) == null) {
            systemHelperField.set(action, systemHelperInstance);
        }
        ComponentUtil.register(systemHelperInstance, "systemHelper");

        final Field fessConfigField = org.codelibs.fess.app.web.base.FessBaseAction.class.getDeclaredField("fessConfig");
        fessConfigField.setAccessible(true);
        if (fessConfigField.get(action) == null) {
            fessConfigField.set(action, ComponentUtil.getFessConfig());
        }

        return action;
    }

    /**
     * Reads {@code ApiThemeResponse.themes} out of a {@code get$available()} response via
     * reflection ({@code ApiResult} exposes no getter for it).
     *
     * @param response the response returned by {@code get$available}
     * @return the themes list
     * @throws Exception if the reflective read fails
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, String>> themesOf(final JsonResponse<ApiResult> response) throws Exception {
        final ApiResult result = response.getJsonResult();
        assertNotNull(result, "get$available must return a JSON result");

        final Field responseField = ApiResult.class.getDeclaredField("response");
        responseField.setAccessible(true);
        final Object apiResponse = responseField.get(result);
        assertTrue(apiResponse instanceof ApiResult.ApiThemeResponse);

        final Field themesField = ApiResult.ApiThemeResponse.class.getDeclaredField("themes");
        themesField.setAccessible(true);
        return (List<Map<String, String>>) themesField.get(apiResponse);
    }

    /**
     * Asserts the endpoint answered with the OK status. The status is read reflectively because
     * {@code ApiResponse} exposes no getter for it.
     *
     * @param response the response returned by {@code post$index}
     * @throws Exception if the reflective read fails
     */
    private void assertOkResponse(final JsonResponse<ApiResult> response) throws Exception {
        assertNotNull(response, "post$index must return a response");
        final ApiResult result = response.getJsonResult();
        assertNotNull(result, "post$index must return a JSON result");

        final Field responseField = ApiResult.class.getDeclaredField("response");
        responseField.setAccessible(true);
        final Object apiResponse = responseField.get(result);
        assertNotNull(apiResponse, "the JSON result must carry a response");

        final Field statusField = ApiResult.ApiResponse.class.getDeclaredField("status");
        statusField.setAccessible(true);
        assertEquals(ApiResult.Status.OK.getId(), ((Integer) statusField.get(apiResponse)).intValue());
    }
}
