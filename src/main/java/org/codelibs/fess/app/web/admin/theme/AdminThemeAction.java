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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.theme.StaticThemeInstaller;
import org.codelibs.fess.theme.Theme;
import org.codelibs.fess.theme.ThemeManifest;
import org.codelibs.fess.theme.ThemeManifestException;
import org.codelibs.fess.theme.ThemeRegistry;
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
 * assignment, and registry-reload flows for static themes. JSP themes are
 * managed via the plugin admin and are out of scope here.
 * Mutating endpoints require the {@code admin-theme} role; read-only
 * endpoints require {@code admin-theme-view}.</p>
 */
public class AdminThemeAction extends FessAdminAction {

    /**
     * Role identifier for theme administration (spec §5.2).
     *
     * <p>The companion {@code admin-theme-view} role is derived by the framework's
     * {@code ROLE + VIEW} convention (inherited from {@link FessAdminAction}).
     * Both roles are granted to the default admin user via the standard Fess
     * {@code Radmin-*} role derivation — no explicit seeding in
     * {@code fess_config.properties} is required.</p>
     */
    public static final String ROLE = "admin-theme";

    private static final Logger logger = LogManager.getLogger(AdminThemeAction.class);

    /** Default constructor. */
    public AdminThemeAction() {
        super();
    }

    @Resource
    private ThemeRegistry themeRegistry;

    @Resource
    private StaticThemeInstaller staticThemeInstaller;

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
        // validate is intentionally not called for reload — it has no form fields to validate
        try {
            themeRegistry.reload();
            saveInfo(m -> m.addSuccessReloadTheme(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to reload ThemeRegistry", e);
            throwValidationError(m -> m.addErrorsFailedToReloadTheme(GLOBAL), () -> asListHtml(form));
        }
        return redirect(getClass());
    }

    /**
     * Displays the theme upload form page.
     *
     * @return HTML response for the upload form
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse uploadpage() {
        saveToken();
        return asHtml(path_AdminTheme_AdminThemeUploadJsp).useForm(ThemeUploadForm.class, op -> op.setup(f -> {}));
    }

    /**
     * Handles a static-theme ZIP upload. The file extension and size guard
     * here are pre-flight checks at the Action layer (M-1 defense in depth);
     * the installer enforces structural and size limits authoritatively.
     *
     * <p>If the installer raises a {@link ThemeManifestException} (wrapped
     * inside an {@link StaticThemeInstaller.InstallException} of code
     * {@link StaticThemeInstaller.InstallException.Code#MANIFEST_INVALID
     * MANIFEST_INVALID}), the structured {@link ThemeManifestException.Code
     * code} is used to surface a localized error key
     * (see {@code errors.theme_manifest_*}) instead of the raw English
     * diagnostic (M-14).</p>
     *
     * @param form the multipart upload form
     * @return redirect to the theme index on success
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse upload(final ThemeUploadForm form) {
        verifyToken(() -> asHtml(path_AdminTheme_AdminThemeUploadJsp));
        validate(form, messages -> {}, () -> asHtml(path_AdminTheme_AdminThemeUploadJsp));
        // C-2: normalize null fileName (multipart parts without a filename= attribute return null
        // from getFileName()). Using the normalized local everywhere prevents a literal "null"
        // from appearing in error/success messages and prevents NPE in hasZipExtension.
        final String rawFileName = form.themeFile.getFileName();
        final String fileName = rawFileName != null ? rawFileName : "";
        if (!hasZipExtension(fileName)) {
            throwValidationError(m -> m.addErrorsFileIsNotSupported(GLOBAL, fileName), () -> asHtml(path_AdminTheme_AdminThemeUploadJsp));
        }
        // M-1: server-side size guard. The JSP enforces the same limit client-side
        // but untrusted clients can post past it. The installer also enforces
        // a (post-extraction) size cap, but we reject obviously-oversized uploads
        // here before allocating any IO buffer to read the stream.
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final long maxBytes = fessConfig.getThemeUploadMaxSizeAsInteger().longValue();
        final long actualBytes = form.themeFile.getFileSize();
        if (actualBytes > maxBytes) {
            logger.warn("Theme upload rejected: size limit exceeded. fileName={}, actualBytes={}, maxBytes={}", fileName, actualBytes,
                    maxBytes);
            final String maxStr = Long.toString(maxBytes);
            final String actualStr = Long.toString(actualBytes);
            throwValidationError(m -> m.addErrorsThemeUploadTooLarge(GLOBAL, maxStr, actualStr),
                    () -> asHtml(path_AdminTheme_AdminThemeUploadJsp));
        }
        try (InputStream in = form.themeFile.getInputStream()) {
            staticThemeInstaller.installZip(in);
            saveInfo(m -> m.addSuccessUploadTheme(GLOBAL, fileName));
        } catch (final StaticThemeInstaller.InstallException ex) {
            logger.warn("Theme upload rejected", ex);
            throwValidationError(m -> mapInstallExceptionToMessage(m, ex), () -> asHtml(path_AdminTheme_AdminThemeUploadJsp));
        } catch (final IOException ex) {
            logger.warn("Failed to read upload stream", ex);
            throwValidationError(m -> m.addErrorsFailedToUploadTheme(GLOBAL, String.valueOf(ex.getMessage())),
                    () -> asHtml(path_AdminTheme_AdminThemeUploadJsp));
        }
        return redirect(getClass());
    }

    /**
     * Routes an {@link StaticThemeInstaller.InstallException} to a localized
     * message (M-14). When the cause is a {@link ThemeManifestException},
     * the structured {@link ThemeManifestException.Code} determines the
     * error key; otherwise the generic {@code errors.failed_to_upload_theme}
     * is used.
     *
     * @param messages the message accumulator
     * @param ex the install exception raised by the installer
     */
    static void mapInstallExceptionToMessage(final FessMessages messages, final StaticThemeInstaller.InstallException ex) {
        if (ex.getCause() instanceof ThemeManifestException tme) {
            addErrorForManifestCode(messages, tme.code());
            return;
        }
        switch (ex.code()) {
        case SIZE_LIMIT:
            messages.addErrorsThemeInstallSizeLimit(GLOBAL);
            break;
        case ENTRY_LIMIT:
            messages.addErrorsThemeInstallEntryLimit(GLOBAL);
            break;
        case RATIO_LIMIT:
            messages.addErrorsThemeInstallRatioLimit(GLOBAL);
            break;
        case ZIP_BOMB_RATIO:
            messages.addErrorsThemeInstallZipBombRatio(GLOBAL);
            break;
        default:
            messages.addErrorsFailedToUploadTheme(GLOBAL, String.valueOf(ex.getMessage()));
            break;
        }
    }

    private static void addErrorForManifestCode(final FessMessages messages, final ThemeManifestException.Code code) {
        switch (code) {
        case PARSE_FAILED:
            messages.addErrorsThemeManifestParseFailed(GLOBAL);
            break;
        case EMPTY:
            messages.addErrorsThemeManifestEmpty(GLOBAL);
            break;
        case NOT_MAPPING:
            messages.addErrorsThemeManifestNotMapping(GLOBAL);
            break;
        case FIELD_TOO_LONG:
            messages.addErrorsThemeManifestFieldTooLong(GLOBAL);
            break;
        case UNSUPPORTED_API_VERSION:
            messages.addErrorsThemeManifestUnsupportedApiVersion(GLOBAL);
            break;
        case UNSUPPORTED_KIND:
            messages.addErrorsThemeManifestUnsupportedKind(GLOBAL);
            break;
        case INVALID_NAME:
            messages.addErrorsThemeManifestInvalidName(GLOBAL);
            break;
        case DISPLAY_NAME_REQUIRED:
            messages.addErrorsThemeManifestDisplayNameRequired(GLOBAL);
            break;
        case INVALID_VERSION:
            messages.addErrorsThemeManifestInvalidVersion(GLOBAL);
            break;
        case UNSAFE_ENTRY:
            messages.addErrorsThemeManifestUnsafeEntry(GLOBAL);
            break;
        case OTHER:
        default:
            messages.addErrorsFailedToUploadTheme(GLOBAL, "");
            break;
        }
    }

    /**
     * Deletes a static theme via the installer. The installer enforces that
     * the theme is not the active default.
     *
     * @param form the delete form containing the theme name
     * @return redirect to the theme index
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse delete(final ThemeDeleteForm form) {
        verifyToken(() -> asListHtml(new ThemeListForm()));
        validate(form, messages -> {}, () -> asListHtml(new ThemeListForm()));
        try {
            staticThemeInstaller.delete(form.name);
            saveInfo(m -> m.addSuccessDeleteTheme(GLOBAL, form.name));
        } catch (final StaticThemeInstaller.InstallException ex) {
            logger.warn("Theme delete rejected", ex);
            final String name = form.name;
            throwValidationError(m -> mapDeleteExceptionToMessage(m, ex, name), () -> asListHtml(new ThemeListForm()));
        }
        return redirect(getClass());
    }

    /**
     * Maps a delete-phase {@link StaticThemeInstaller.InstallException} to a localized
     * {@link FessMessages} entry. Called from {@link #delete} and directly testable
     * at the unit level without a full LastaFlute container.
     *
     * @param messages the message accumulator
     * @param ex the install exception raised by the installer during delete
     * @param themeName the theme name from the delete form (used as the message argument)
     */
    static void mapDeleteExceptionToMessage(final FessMessages messages, final StaticThemeInstaller.InstallException ex,
            final String themeName) {
        switch (ex.code()) {
        case ACTIVE_DEFAULT:
            messages.addErrorsThemeIsActive(GLOBAL, themeName);
            break;
        case NOT_FOUND:
            messages.addErrorsThemeNotFound(GLOBAL, themeName);
            break;
        case INVALID_NAME:
            messages.addErrorsThemeNameInvalid(GLOBAL, themeName);
            break;
        default:
            messages.addErrorsFailedToDeleteTheme(GLOBAL, themeName);
            break;
        }
    }

    /**
     * Sets the default theme by writing it through {@link FessConfig#setDefaultTheme(String)}.
     * An empty value clears the default. The registry is reloaded synchronously
     * so the new default takes effect immediately.
     *
     * @param form the list form carrying the selected default theme name
     * @return redirect to the theme index
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse setdefault(final ThemeListForm form) {
        verifyToken(() -> asListHtml(form));
        validate(form, messages -> {}, () -> asListHtml(form));
        final String name = form.defaultTheme == null ? "" : form.defaultTheme.trim();
        if (!name.isEmpty()) {
            // existence check — refuse to point the default theme at a missing theme
            if (themeRegistry.getTheme(name).isEmpty()) {
                throwValidationError(m -> m.addErrorsThemeNotFound(GLOBAL, name), () -> asListHtml(form));
            }
        }
        try {
            ComponentUtil.getFessConfig().setDefaultTheme(name);
            themeRegistry.reload(); // re-resolve in case the new default needs to take effect immediately
            if (name.isEmpty()) {
                saveInfo(m -> m.addSuccessClearDefaultTheme(GLOBAL));
            } else {
                saveInfo(m -> m.addSuccessChangeDefaultTheme(GLOBAL, name));
            }
        } catch (final Exception e) {
            logger.warn("Failed to change default theme", e);
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
        return ComponentUtil.getFessConfig().getDefaultTheme();
    }

    /**
     * Locale-independent ZIP extension check. Uses {@link Locale#ROOT} so the
     * lower-case conversion is unaffected by the JVM's default locale (e.g.
     * the Turkish locale's dotless-{@code i} would otherwise corrupt the
     * comparison).
     *
     * @param filename the candidate file name (may be {@code null})
     * @return {@code true} when {@code filename} ends with {@code .zip} case-insensitively
     */
    static boolean hasZipExtension(final String filename) {
        return filename != null && filename.toLowerCase(Locale.ROOT).endsWith(".zip");
    }

    private static Map<String, Object> asThemeRow(final Theme t, final String currentDefault) {
        final Map<String, Object> m = new HashMap<>();
        m.put("name", t.getName());
        m.put("displayName", t.getManifest().map(ThemeManifest::getDisplayName).orElse(t.getName()));
        m.put("version", t.getManifest().map(ThemeManifest::getVersion).orElse(""));
        m.put("isDefault", t.getName().equals(currentDefault));
        m.put("health", "ok"); // TODO Plan 6: surface ThemeRegistry health flag once exposed
        return m;
    }
}
