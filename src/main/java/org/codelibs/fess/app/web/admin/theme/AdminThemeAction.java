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
package org.codelibs.fess.app.web.admin.theme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.theme.Theme;
import org.codelibs.fess.theme.ThemeManifest;
import org.codelibs.fess.theme.ThemeRegistry;
import org.codelibs.fess.theme.ThemeType;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;

import jakarta.annotation.Resource;

/**
 * Admin action for theme management (spec §8).
 *
 * <p>Provides list, detail inspection, upload, deletion, default-theme
 * assignment, and registry-reload flows for both JSP and static themes.
 * Mutating endpoints require the {@code admin-theme} role; read-only
 * endpoints require {@code admin-theme-view}.</p>
 */
public class AdminThemeAction extends FessAdminAction {

    /** Role identifier for theme administration (spec §5.2). */
    public static final String ROLE = "admin-theme";

    private static final Logger logger = LogManager.getLogger(AdminThemeAction.class);

    /** Default constructor. */
    public AdminThemeAction() {
        super();
    }

    @Resource
    private ThemeRegistry themeRegistry;

    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        // online help link reuses the plugin help page until a dedicated theme
        // help article ships in fess-docs (Plan 6 follow-up).
        runtime.registerData("helpLink", systemHelper.getHelpLink("theme"));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    /**
     * Displays the theme management index page.
     *
     * @param form the list form (carries the pre-populated default-theme value)
     * @return HTML response for the index page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index(final ThemeListForm form) {
        saveToken();
        return asListHtml(form);
    }

    /**
     * Displays the theme details inspector for the given theme name.
     *
     * @param form the details form containing the target theme name
     * @return HTML response for the details page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse details(final ThemeDetailsForm form) {
        validate(form, messages -> {}, () -> asListHtml(new ThemeListForm()));
        saveToken();
        return asHtml(path_AdminTheme_AdminThemeDetailsJsp).renderWith(data -> {
            final Theme t = themeRegistry.getAllThemes().get(form.name);
            if (t == null) {
                throwValidationError(m -> m.addErrorsThemeNotFound(GLOBAL, form.name), () -> asListHtml(new ThemeListForm()));
            }
            RenderDataUtil.register(data, "theme", asThemeRow(t, currentDefault()));
        }).useForm(ThemeDetailsForm.class);
    }

    /**
     * Reloads the in-memory theme registry from disk.
     *
     * @param form the list form (used for re-rendering on error)
     * @return redirect to the theme index
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse reload(final ThemeListForm form) {
        verifyToken(() -> asListHtml(form));
        try {
            themeRegistry.reload();
            saveInfo(m -> m.addSuccessReloadTheme(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to reload ThemeRegistry", e);
            throwValidationError(m -> m.addErrorsFailedToChangeDefaultTheme(GLOBAL), () -> asListHtml(form));
        }
        return redirect(getClass());
    }

    // ---- list rendering ----

    private HtmlResponse asListHtml(final ThemeListForm form) {
        return asHtml(path_AdminTheme_AdminThemeJsp).renderWith(data -> {
            final String def = currentDefault();
            final List<Map<String, Object>> rows = new ArrayList<>();
            themeRegistry.getAllThemes().forEach((name, t) -> rows.add(asThemeRow(t, def)));
            RenderDataUtil.register(data, "themeItems", rows);
            RenderDataUtil.register(data, "currentDefault", def);
        }).useForm(ThemeListForm.class, op -> op.setup(f -> f.defaultTheme = currentDefault()));
    }

    private String currentDefault() {
        return ComponentUtil.getFessConfig().getSystemProperty(ThemeRegistry.SYSPROP_DEFAULT_THEME, "");
    }

    private static Map<String, Object> asThemeRow(final Theme t, final String currentDefault) {
        final Map<String, Object> m = new HashMap<>();
        m.put("name", t.getName());
        m.put("type", t.getType() == ThemeType.STATIC ? "Static" : "JSP");
        m.put("displayName", t.getManifest().map(ThemeManifest::getDisplayName).orElse(t.getName()));
        m.put("version", t.getManifest().map(ThemeManifest::getVersion).orElse(""));
        m.put("isDefault", t.getName().equals(currentDefault));
        m.put("health", "ok"); // TODO Plan 6: surface ThemeRegistry health flag once exposed
        return m;
    }
}
