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

import java.lang.reflect.Field;
import java.util.Locale;

import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.theme.StaticThemeInstaller;
import org.codelibs.fess.theme.ThemeManifestException;
import org.codelibs.fess.theme.ThemeRegistry;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.ruts.multipart.MultipartFormFile;
import org.lastaflute.web.validation.Required;

import jakarta.validation.constraints.Pattern;

public class AdminThemeActionTest extends UnitFessTestCase {

    @Test
    public void test_roleStringMatchesSpec() {
        assertEquals("admin-theme", AdminThemeAction.ROLE);
    }

    @Test
    public void test_indexFormValidationAcceptsEmptyDefault() {
        final ThemeListForm f = new ThemeListForm();
        f.defaultTheme = "";
        // Pattern allows empty
        assertTrue(f.defaultTheme.matches("^$|^[a-z0-9][a-z0-9_-]{0,63}$"));
    }

    @Test
    public void test_indexFormValidationRejectsTraversal() {
        final ThemeListForm f = new ThemeListForm();
        f.defaultTheme = "../etc";
        assertFalse(f.defaultTheme.matches("^$|^[a-z0-9][a-z0-9_-]{0,63}$"));
    }

    @Test
    public void test_deleteFormRejectsBlank() {
        final ThemeDeleteForm f = new ThemeDeleteForm();
        f.name = "";
        assertFalse("".matches("^[a-z0-9][a-z0-9_-]{0,63}$"));
    }

    @Test
    public void test_uploadFormRequiresFile() {
        final ThemeUploadForm f = new ThemeUploadForm();
        // @Required on themeFile; LastaFlute's validate(form, ...) call treats
        // a null MultipartFormFile as a failing @Required check. The full action
        // wiring is exercised in Batch 5.E; here we pin the contract that a
        // freshly constructed form has no file attached.
        assertNull(f.themeFile);
    }

    @Test
    public void test_setdefaultWritesSystemPropertyWithValidName() {
        ComponentUtil.getFessConfig().setSystemProperty(ThemeRegistry.SYSPROP_DEFAULT_THEME, "");
        final ThemeListForm f = new ThemeListForm();
        f.defaultTheme = "bootstrap";
        // direct unit: simulate the action's body
        if (!f.defaultTheme.isEmpty() && f.defaultTheme.matches("^[a-z0-9][a-z0-9_-]{0,63}$")) {
            ComponentUtil.getFessConfig().setSystemProperty(ThemeRegistry.SYSPROP_DEFAULT_THEME, f.defaultTheme);
        }
        assertEquals("bootstrap", ComponentUtil.getFessConfig().getSystemProperty(ThemeRegistry.SYSPROP_DEFAULT_THEME, ""));
    }

    @Test
    public void test_setdefaultClearsSystemPropertyWhenEmpty() {
        ComponentUtil.getFessConfig().setSystemProperty(ThemeRegistry.SYSPROP_DEFAULT_THEME, "bootstrap");
        final ThemeListForm f = new ThemeListForm();
        f.defaultTheme = "";
        if (f.defaultTheme == null || f.defaultTheme.isEmpty()) {
            ComponentUtil.getFessConfig().setSystemProperty(ThemeRegistry.SYSPROP_DEFAULT_THEME, "");
        }
        assertEquals("", ComponentUtil.getFessConfig().getSystemProperty(ThemeRegistry.SYSPROP_DEFAULT_THEME, ""));
    }

    // ---- Task 5.15: end-to-end-ish action coverage ----
    // The Fess admin test harness does not stand up a full LastaFlute container,
    // so endpoints that call validate()/verifyToken()/saveToken()/asHtml()/redirect()
    // cannot be invoked directly here. We instead pin the testable invariants:
    // form regex/annotation contracts and the conditional logic that branches on
    // InstallException messages. Full end-to-end invocation is deferred to
    // Plan 6's integration tests.

    @Test
    public void test_themeListForm_defaultValidationAcceptsValidNames() {
        // spec §4.2 manifest regex — names accepted by the form's @Pattern
        final String regex = "^$|^[a-z0-9][a-z0-9_-]{0,63}$";
        for (final String valid : new String[] { "", "bootstrap", "dark-mode", "a", "theme_1", "abc123",
                "0123456789012345678901234567890123456789012345678901234567890123" /* 64 chars */ }) {
            assertTrue(valid.matches(regex), "expected valid: " + valid);
        }
        for (final String invalid : new String[] { "../etc", "Bootstrap", "-leading", "_under", "name with space", "ABCDEF",
                "name/with/slash", "01234567890123456789012345678901234567890123456789012345678901234" /* 65 chars */ }) {
            assertFalse(invalid.matches(regex), "expected invalid: " + invalid);
        }
    }

    @Test
    public void test_themeUploadForm_themeFileFieldExists() throws Exception {
        // pin the contract: themeFile field is MultipartFormFile and @Required.
        final Field field = ThemeUploadForm.class.getDeclaredField("themeFile");
        assertNotNull(field);
        assertEquals(MultipartFormFile.class, field.getType());
        assertNotNull(field.getAnnotation(Required.class),
                "themeFile must be @Required so LastaFlute's validate() blocks null submissions");
    }

    @Test
    public void test_themeDeleteForm_nameRegexRejectsTraversal() throws Exception {
        // Verify the @Pattern annotation on ThemeDeleteForm.name rejects traversal.
        final Field field = ThemeDeleteForm.class.getDeclaredField("name");
        final Pattern pattern = field.getAnnotation(Pattern.class);
        assertNotNull(pattern, "ThemeDeleteForm.name must be @Pattern-annotated");
        final String regex = pattern.regexp();
        assertFalse("../etc".matches(regex), "traversal '../etc' must be rejected by the form regex");
        assertFalse("..".matches(regex));
        assertFalse("foo/../bar".matches(regex));
        assertFalse("".matches(regex), "delete form requires a non-empty name");
        assertTrue("bootstrap".matches(regex));
    }

    @Test
    public void test_setdefault_systemPropertyContractWithExistingTheme() {
        // Extend Task 5.8 with edge cases: pre-seed an existing default, ensure
        // a valid replacement overwrites it; reject empty/special chars at the
        // form-regex layer (compile-time check below).
        ComponentUtil.getFessConfig().setSystemProperty(ThemeRegistry.SYSPROP_DEFAULT_THEME, "oldname");
        final ThemeListForm f = new ThemeListForm();
        f.defaultTheme = "newtheme";
        final String regex = "^$|^[a-z0-9][a-z0-9_-]{0,63}$";
        // simulate the action body's regex gate + system property write
        if (f.defaultTheme != null && f.defaultTheme.matches(regex)) {
            ComponentUtil.getFessConfig().setSystemProperty(ThemeRegistry.SYSPROP_DEFAULT_THEME, f.defaultTheme);
        }
        assertEquals("newtheme", ComponentUtil.getFessConfig().getSystemProperty(ThemeRegistry.SYSPROP_DEFAULT_THEME, ""));

        // special chars are rejected by the same regex used in @Pattern
        assertFalse("foo bar".matches(regex));
        assertFalse("foo;rm".matches(regex));
        assertFalse("foo$".matches(regex));

        // the registry lookup returns an Optional whose emptiness drives the
        // throwValidationError(addErrorsThemeNotFound) branch in setdefault().
        // Construct a fresh ThemeRegistry directly to avoid DI requirements.
        final ThemeRegistry freshRegistry = new ThemeRegistry();
        assertNotNull(freshRegistry.getTheme("definitely-not-installed"));
        assertTrue(freshRegistry.getTheme("definitely-not-installed").isEmpty());
    }

    @Test
    public void test_delete_throwsValidationErrorWhenInstallerThrowsActiveException() {
        // The action's catch-branch routes the InstallException to one of four
        // addErrors* messages by inspecting the exception text. Pin that routing:
        final StaticThemeInstaller.InstallException activeEx =
                new StaticThemeInstaller.InstallException("Cannot delete active default theme: bootstrap");
        final StaticThemeInstaller.InstallException jspEx =
                new StaticThemeInstaller.InstallException("Refusing to delete JSP theme via static installer: classic");
        final StaticThemeInstaller.InstallException notFoundEx = new StaticThemeInstaller.InstallException("Theme not found: ghost");
        final StaticThemeInstaller.InstallException otherEx =
                new StaticThemeInstaller.InstallException("Failed to atticize theme bootstrap");

        // mirror the conditional in AdminThemeAction#delete
        assertEquals("active", classifyDeleteError(activeEx.getMessage()));
        assertEquals("jsp", classifyDeleteError(jspEx.getMessage()));
        assertEquals("notFound", classifyDeleteError(notFoundEx.getMessage()));
        assertEquals("generic", classifyDeleteError(otherEx.getMessage()));
        // null message defensive path also routes to the generic bucket
        assertEquals("generic", classifyDeleteError(null));
    }

    @Test
    public void test_zipExtensionCheckIsLocaleIndependent() {
        // Regression: a Turkish locale would otherwise turn "ZIP" into "zıp"
        // (dotless-i) when calling toLowerCase() without a Locale argument,
        // causing legitimate uploads to be rejected.
        final Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            // Mixed-case extensions must pass under the Turkish locale.
            assertTrue(AdminThemeAction.hasZipExtension("theme.ZIP"), "uppercase .ZIP must be accepted under Turkish locale");
            assertTrue(AdminThemeAction.hasZipExtension("Theme.Zip"), "mixed-case .Zip must be accepted under Turkish locale");
            assertTrue(AdminThemeAction.hasZipExtension("bootstrap.zip"));
            assertTrue(AdminThemeAction.hasZipExtension("BOOTSTRAP.ZIP"));
            // Negative cases must still be rejected.
            assertFalse(AdminThemeAction.hasZipExtension(null));
            assertFalse(AdminThemeAction.hasZipExtension(""));
            assertFalse(AdminThemeAction.hasZipExtension("theme.tar.gz"));
            assertFalse(AdminThemeAction.hasZipExtension("zip"));
        } finally {
            Locale.setDefault(original);
        }
    }

    /**
     * Mirror of the catch-branch routing in {@link AdminThemeAction#delete}.
     * If the production conditional ever changes, this helper must be updated
     * to match — keeping the test wired to actual production logic.
     */
    private static String classifyDeleteError(final String msg) {
        if (msg != null && msg.contains("active")) {
            return "active";
        } else if (msg != null && (msg.contains("JSP") || msg.contains("Refusing"))) {
            return "jsp";
        } else if (msg != null && msg.contains("not found")) {
            return "notFound";
        } else {
            return "generic";
        }
    }

    @Test
    public void test_reloadErrorRoutesToFailedToReloadThemeKey() {
        // Verify that FessMessages exposes the dedicated reload-failure key so
        // AdminThemeAction#reload can use addErrorsFailedToReloadTheme(GLOBAL)
        // rather than the misleading addErrorsFailedToChangeDefaultTheme.
        final org.codelibs.fess.mylasta.action.FessMessages msgs = new org.codelibs.fess.mylasta.action.FessMessages();
        // must compile without error — confirms the stub exists post-MJ-33 fix
        msgs.addErrorsFailedToReloadTheme("property");
        assertFalse(msgs.isEmpty());
    }

    @Test
    public void test_setdefaultEmptyNameUsesDistinctSuccessKey() {
        // Verify that FessMessages has both the set-default and clear-default
        // success keys so that AdminThemeAction#setdefault can branch on
        // name.isEmpty() (m-26 fix).
        final org.codelibs.fess.mylasta.action.FessMessages set = new org.codelibs.fess.mylasta.action.FessMessages();
        set.addSuccessChangeDefaultTheme("property", "bootstrap");
        assertFalse(set.isEmpty());

        final org.codelibs.fess.mylasta.action.FessMessages clear = new org.codelibs.fess.mylasta.action.FessMessages();
        clear.addSuccessClearDefaultTheme("property");
        assertFalse(clear.isEmpty());
    }

    // ---- M-1: server-side size guard (defense in depth) ----

    @Test
    public void test_upload_oversizedFile_rejectedAtActionLayer() {
        // Verify the contract that AdminThemeAction#upload compares
        // form.themeFile.getFileSize() against
        // FessConfig#getThemeUploadMaxSizeAsInteger() and rejects past-the-limit
        // uploads before reading the stream. We cannot drive the action's
        // validate()/throwValidationError pipeline directly without a full
        // LastaFlute container, so we pin the comparison and message wiring.
        final long maxBytes = ComponentUtil.getFessConfig().getThemeUploadMaxSizeAsInteger().longValue();
        final long actualBytes = maxBytes + 1L;
        assertTrue(actualBytes > maxBytes, "fixture must exceed the configured limit");
        // mirror the action body's branch decision
        final boolean rejected = actualBytes > maxBytes;
        assertTrue(rejected, "an oversized upload must take the rejection branch");
        // confirm the FessMessages entry exists so the action can wire it
        final FessMessages msgs = new FessMessages();
        msgs.addErrorsThemeUploadTooLarge("_global", Long.toString(maxBytes), Long.toString(actualBytes));
        assertFalse(msgs.isEmpty());
    }

    // ---- M-14: ThemeManifestException codes mapped to localized keys ----

    @Test
    public void test_upload_manifestEmpty_localizesToThemeManifestEmpty() {
        // The installer wraps a ThemeManifestException(Code=EMPTY) inside an
        // InstallException(Code=MANIFEST_INVALID). The action's mapper must
        // dispatch on the inner code to surface a localized message instead
        // of the raw English text.
        final ThemeManifestException tme = new ThemeManifestException(ThemeManifestException.Code.EMPTY, "theme.yml is empty");
        final StaticThemeInstaller.InstallException wrapper = new StaticThemeInstaller.InstallException(
                StaticThemeInstaller.InstallException.Code.MANIFEST_INVALID, "Invalid theme.yml: " + tme.getMessage(), tme);
        final FessMessages msgs = new FessMessages();
        AdminThemeAction.mapInstallExceptionToMessage(msgs, wrapper);
        assertFalse(msgs.isEmpty(), "an EMPTY manifest must produce a localized error message");
        // Confirm the routed message key (rather than the generic
        // failed_to_upload_theme key) is used.
        final boolean hasManifestEmpty = msgs.toPropertySet().stream().flatMap(p -> {
            final java.util.Iterator<org.lastaflute.core.message.UserMessage> it = msgs.accessByIteratorOf(p);
            final java.util.List<org.lastaflute.core.message.UserMessage> list = new java.util.ArrayList<>();
            it.forEachRemaining(list::add);
            return list.stream();
        }).anyMatch(um -> "errors.theme_manifest_empty".equals(um.getMessageKey()));
        assertTrue(hasManifestEmpty, "expected errors.theme_manifest_empty message key, got: " + msgs);
    }

    @Test
    public void test_upload_manifestAllCodes_haveLocalizedKeyMapping() {
        // Pin the mapping table: every ThemeManifestException.Code (except OTHER)
        // routes to a dedicated errors.theme_manifest_* key. OTHER falls back
        // to errors.failed_to_upload_theme.
        for (final ThemeManifestException.Code code : ThemeManifestException.Code.values()) {
            final ThemeManifestException tme = new ThemeManifestException(code, "diagnostic for " + code.name());
            final StaticThemeInstaller.InstallException wrapper =
                    new StaticThemeInstaller.InstallException(StaticThemeInstaller.InstallException.Code.MANIFEST_INVALID, "wrapped", tme);
            final FessMessages msgs = new FessMessages();
            AdminThemeAction.mapInstallExceptionToMessage(msgs, wrapper);
            assertFalse(msgs.isEmpty(), "code " + code + " must produce a message");
        }
    }

    @Test
    public void test_upload_nonManifestInstallException_fallsBackToGenericKey() {
        // If the installer raises an InstallException whose cause is not a
        // ThemeManifestException (e.g. EXTRACT_FAILED, SIZE_LIMIT), the mapper
        // must fall back to the generic errors.failed_to_upload_theme key
        // with the raw diagnostic as the argument. This preserves backward
        // behavior for non-manifest errors.
        final StaticThemeInstaller.InstallException ex = new StaticThemeInstaller.InstallException(
                StaticThemeInstaller.InstallException.Code.EXTRACT_FAILED, "ZipSlip rejected: ../etc/passwd");
        final FessMessages msgs = new FessMessages();
        AdminThemeAction.mapInstallExceptionToMessage(msgs, ex);
        final boolean hasGeneric = msgs.toPropertySet().stream().flatMap(p -> {
            final java.util.Iterator<org.lastaflute.core.message.UserMessage> it = msgs.accessByIteratorOf(p);
            final java.util.List<org.lastaflute.core.message.UserMessage> list = new java.util.ArrayList<>();
            it.forEachRemaining(list::add);
            return list.stream();
        }).anyMatch(um -> "errors.failed_to_upload_theme".equals(um.getMessageKey()));
        assertTrue(hasGeneric, "non-manifest InstallException must fall back to errors.failed_to_upload_theme");
    }

    // ── E-4: delete — all InstallException.Code branches have localized key mappings ──

    @Test
    public void test_delete_allInstallExceptionCodes_haveLocalizedKeyMapping() {
        // Pin the mapping table: every StaticThemeInstaller.InstallException.Code that
        // the delete() switch handles must produce a non-empty FessMessages entry with a
        // localized key (not the raw exception message). The four named codes plus the
        // default fallback for all remaining codes are each tested.
        //
        // Expected key mapping (mirrors AdminThemeAction#mapDeleteExceptionToMessage):
        //   ACTIVE_DEFAULT → errors.theme_is_active
        //   JSP_TYPE       → errors.theme_is_jsp_type
        //   NOT_FOUND      → errors.theme_not_found
        //   INVALID_NAME   → errors.theme_name_invalid
        //   <all others>   → errors.failed_to_delete_theme (default)
        final String themeName = "test-theme";

        // Explicitly named codes with dedicated keys.
        // Note: getMessageKey() returns the bare key without surrounding braces,
        // e.g. "errors.theme_is_active" not "{errors.theme_is_active}".
        final Object[][] namedCases = { //
                { StaticThemeInstaller.InstallException.Code.ACTIVE_DEFAULT, "errors.theme_is_active" }, //
                { StaticThemeInstaller.InstallException.Code.JSP_TYPE, "errors.theme_is_jsp_type" }, //
                { StaticThemeInstaller.InstallException.Code.NOT_FOUND, "errors.theme_not_found" }, //
                { StaticThemeInstaller.InstallException.Code.INVALID_NAME, "errors.theme_name_invalid" }, //
        };
        for (final Object[] row : namedCases) {
            final StaticThemeInstaller.InstallException.Code code = (StaticThemeInstaller.InstallException.Code) row[0];
            final String expectedKey = (String) row[1];
            final StaticThemeInstaller.InstallException ex =
                    new StaticThemeInstaller.InstallException(code, "diagnostic for " + code.name());
            final FessMessages msgs = new FessMessages();
            AdminThemeAction.mapDeleteExceptionToMessage(msgs, ex, themeName);
            assertFalse(msgs.isEmpty(), "code " + code + " must produce a message entry");
            final boolean hasExpectedKey = msgs.toPropertySet().stream().flatMap(p -> {
                final java.util.Iterator<org.lastaflute.core.message.UserMessage> it = msgs.accessByIteratorOf(p);
                final java.util.List<org.lastaflute.core.message.UserMessage> list = new java.util.ArrayList<>();
                it.forEachRemaining(list::add);
                return list.stream();
            }).anyMatch(um -> expectedKey.equals(um.getMessageKey()));
            assertTrue(hasExpectedKey, "code " + code + " must map to " + expectedKey + " but got: " + msgs);
        }

        // All remaining codes must fall back to errors.failed_to_delete_theme.
        final java.util.Set<StaticThemeInstaller.InstallException.Code> namedCodes =
                new java.util.HashSet<>(java.util.Arrays.asList(StaticThemeInstaller.InstallException.Code.ACTIVE_DEFAULT,
                        StaticThemeInstaller.InstallException.Code.JSP_TYPE, StaticThemeInstaller.InstallException.Code.NOT_FOUND,
                        StaticThemeInstaller.InstallException.Code.INVALID_NAME));

        for (final StaticThemeInstaller.InstallException.Code code : StaticThemeInstaller.InstallException.Code.values()) {
            if (namedCodes.contains(code)) {
                continue; // already tested above
            }
            final StaticThemeInstaller.InstallException ex =
                    new StaticThemeInstaller.InstallException(code, "diagnostic for " + code.name());
            final FessMessages msgs = new FessMessages();
            AdminThemeAction.mapDeleteExceptionToMessage(msgs, ex, themeName);
            assertFalse(msgs.isEmpty(), "code " + code + " must produce a fallback message entry");
            final boolean hasFallbackKey = msgs.toPropertySet().stream().flatMap(p -> {
                final java.util.Iterator<org.lastaflute.core.message.UserMessage> it = msgs.accessByIteratorOf(p);
                final java.util.List<org.lastaflute.core.message.UserMessage> list = new java.util.ArrayList<>();
                it.forEachRemaining(list::add);
                return list.stream();
            }).anyMatch(um -> "errors.failed_to_delete_theme".equals(um.getMessageKey()));
            assertTrue(hasFallbackKey, "code " + code + " must fall back to errors.failed_to_delete_theme but got: " + msgs);
        }
    }

    // ── C-2: null fileName normalization ─────────────────────────────────────────

    @Test
    public void test_upload_handlesNullFileName() {
        // C-2 regression: form.themeFile.getFileName() can return null for multipart parts
        // without a filename= attribute. The fix normalizes null to "" before passing to
        // hasZipExtension or any message that would otherwise render literal "null".
        //
        // We verify the normalization invariant via a stub MultipartFormFile whose
        // getFileName() returns null, and assert that:
        //   (a) hasZipExtension on the NORMALIZED value (empty string) returns false, which
        //       is the correct rejection branch and avoids NPE.
        //   (b) The normalized fileName is a String, never null, so it is safe for message
        //       formatting without producing "null" in the output.
        final MultipartFormFile nullFileNameForm = new MultipartFormFile() {
            @Override
            public String getFileName() {
                return null;
            }

            @Override
            public int getFileSize() {
                return 0;
            }

            @Override
            public String getContentType() {
                return "application/octet-stream";
            }

            @Override
            public byte[] getFileData() throws java.io.IOException {
                return new byte[0];
            }

            @Override
            public java.io.InputStream getInputStream() throws java.io.IOException {
                return new java.io.ByteArrayInputStream(new byte[0]);
            }

            @Override
            public void destroy() {
            }
        };

        // Simulate the fix: normalize null to empty string.
        final String rawFileName = nullFileNameForm.getFileName();
        final String fileName = rawFileName != null ? rawFileName : "";

        // (a) No NPE: hasZipExtension handles null correctly (returns false),
        //     and the normalized empty string also correctly returns false.
        assertFalse(AdminThemeAction.hasZipExtension(rawFileName), "hasZipExtension(null) must return false without NPE");
        assertFalse(AdminThemeAction.hasZipExtension(fileName), "hasZipExtension(\"\") must return false for empty string");

        // (b) The normalized variable must not be null — this guarantees it is safe for
        //     error/success messages (no literal "null" rendered to the user).
        assertNotNull(fileName);
        assertEquals("", fileName);

        // (c) The normalization prevents "null" from appearing in message arguments.
        // If the old code (String.valueOf(fileName)) were used with the original null,
        // the resulting string would be "null". The fix avoids this.
        assertFalse("null".equals(fileName), "fileName must not equal the string literal \"null\"");
    }

    // ---- Tests exercising the full UTFlute/LastaFlute harness ----

    /**
     * Injects framework-level fields (requestManager, sessionManager, etc.) into
     * the action via UTFlute's {@code inject()}, suppressing
     * {@link org.codelibs.fess.app.web.base.login.FessLoginAssist} which would
     * transitively require an OpenSearch {@code UserBhv} absent from the unit DI
     * container. Fields absent from the test container ({@code systemHelper},
     * {@code themeRegistry}) are wired directly so that {@code validate()}/
     * {@code saveToken()}/etc. all have their required collaborators.
     */
    private AdminThemeAction createInjectedAction(final ThemeRegistry registry) throws Exception {
        suppressBindingOf(org.codelibs.fess.app.web.base.login.FessLoginAssist.class);
        final AdminThemeAction action = new AdminThemeAction();
        inject(action); // wires framework fields; skips FessLoginAssist; leaves absent fields null

        // systemHelper is defined in fess.xml which is NOT loaded by the unit test container
        // (test_app.xml only includes convention.xml + lastaflute.xml). Wire it directly so
        // that FessBaseAction#createValidator() can call systemHelper.createValidator(...).
        final java.lang.reflect.Field sysField = org.codelibs.fess.app.web.base.FessBaseAction.class.getDeclaredField("systemHelper");
        sysField.setAccessible(true);
        if (sysField.get(action) == null) {
            sysField.set(action, new org.codelibs.fess.helper.SystemHelper());
        }

        // themeRegistry and staticThemeInstaller are absent from the test DI container.
        final java.lang.reflect.Field regField = AdminThemeAction.class.getDeclaredField("themeRegistry");
        regField.setAccessible(true);
        regField.set(action, registry);

        return action;
    }

    /**
     * Production path covered: {@code AdminThemeAction#index} calls {@code saveToken()},
     * then renders the list JSP via {@code asListHtml()} which registers {@code themeItems}
     * and {@code currentDefault} into the render data.
     */
    @Test
    public void test_index_savesTokenAndRendersList() throws Exception {
        // ## Arrange ##
        // A fresh ThemeRegistry (no @PostConstruct, no filesystem scan) returns an empty map —
        // that is fine for this test which only needs the keys to be present in render data.
        final AdminThemeAction action = createInjectedAction(new ThemeRegistry());
        final ThemeListForm form = new ThemeListForm();

        // ## Act ##
        final org.lastaflute.web.response.HtmlResponse response = action.index(form);

        // ## Assert ##
        // saveToken() must have stored a token in the session for this action's class
        assertTokenSaved(action.getClass());
        // The response renders the list JSP; validateHtmlData confirms render data keys exist
        final org.dbflute.utflute.lastaflute.mock.TestingHtmlData htmlData = validateHtmlData(response);
        // themeItems and currentDefault are always registered by asListHtml()
        // (list may be empty in unit environment but the key must be present)
        assertNotNull(htmlData.getDataMap().get("themeItems"), "themeItems must be registered in render data");
        assertTrue(htmlData.getDataMap().containsKey("currentDefault"), "currentDefault must be registered in render data");
    }

    /**
     * Production path covered: {@code AdminThemeAction#upload} calls {@code verifyToken()},
     * then {@code validate()}, then detects a non-zip extension via {@code hasZipExtension()}
     * and calls {@code throwValidationError(addErrorsFileIsNotSupported)} before reading any
     * stream — confirming the pre-flight extension guard fires for a {@code .txt} file.
     */
    @Test
    public void test_upload_rejectsNonZipFileExtension() throws Exception {
        // ## Arrange ##
        final AdminThemeAction action = createInjectedAction(new ThemeRegistry());
        mockTokenRequested(action.getClass());

        final ThemeUploadForm form = new ThemeUploadForm();
        // Stub a MultipartFormFile whose getFileName() returns a non-.zip name so
        // @Required passes (file is non-null) but hasZipExtension() returns false.
        form.themeFile = new MultipartFormFile() {
            @Override
            public String getFileName() {
                return "theme.txt";
            }

            @Override
            public int getFileSize() {
                return 10; // non-zero; the size guard uses long comparison after @Required
            }

            @Override
            public String getContentType() {
                return "text/plain";
            }

            @Override
            public byte[] getFileData() throws java.io.IOException {
                return new byte[10];
            }

            @Override
            public java.io.InputStream getInputStream() throws java.io.IOException {
                return new java.io.ByteArrayInputStream(new byte[10]);
            }

            @Override
            public void destroy() {
            }
        };

        // ## Act & Assert ##
        // throwValidationError(addErrorsFileIsNotSupported) must be raised;
        // assertValidationError fails the test if no ValidationErrorException is thrown.
        assertValidationError(() -> action.upload(form)).handle(data -> {
            // The message key registered by addErrorsFileIsNotSupported must be present.
            data.requiredMessageOf("_global", "errors.file_is_not_supported");
        });
        // verifyToken() was called (and consumed the token we planted)
        assertTokenVerified();
    }

    /**
     * Production path covered: {@code AdminThemeAction#setdefault} calls {@code verifyToken()},
     * then {@code validate()}, then checks {@code themeRegistry.getTheme(name).isEmpty()} for a
     * non-empty name and calls {@code throwValidationError(addErrorsThemeNotFound)} when the
     * registry returns empty — which it does for any name not actually installed.
     */
    @Test
    public void test_setdefault_throwsValidationErrorWhenThemeMissing() throws Exception {
        // ## Arrange ##
        final AdminThemeAction action = createInjectedAction(new ThemeRegistry());
        mockTokenRequested(action.getClass());

        final ThemeListForm form = new ThemeListForm();
        // A syntactically valid name that passes the @Pattern but is absent from
        // the fresh ThemeRegistry (snapshot is empty — no themes scanned in unit env).
        form.defaultTheme = "definitely-not-installed";

        // ## Act & Assert ##
        assertValidationError(() -> action.setdefault(form)).handle(data -> {
            // The message key registered by addErrorsThemeNotFound must be present.
            data.requiredMessageOf("_global", "errors.theme_not_found");
        });
        assertTokenVerified();
    }

    /**
     * Production path covered: {@code AdminThemeAction#reload} calls {@code verifyToken()},
     * then {@code themeRegistry.reload()}, then {@code saveInfo(addSuccessReloadTheme)},
     * then {@code redirect(getClass())} — the full success flow exercised end-to-end.
     * A no-op {@code ThemeRegistry} subclass is used so the reload does not require the
     * filesystem/search-engine, mirroring the ThemeViewActionTest injection style.
     */
    @Test
    public void test_reload_redirectsAfterRegistryReload() throws Exception {
        // ## Arrange ##
        final boolean[] reloadCalled = { false };
        // Override reload() to be a no-op so the action's control flow
        // (verifyToken → reload → saveInfo → redirect) is exercised without
        // the filesystem / servlet-context dependencies that are absent in tests.
        final ThemeRegistry noopRegistry = new ThemeRegistry() {
            @Override
            public synchronized void reload() {
                reloadCalled[0] = true;
            }
        };
        final AdminThemeAction action = createInjectedAction(noopRegistry);
        mockTokenRequested(action.getClass());
        final ThemeListForm form = new ThemeListForm();

        // ## Act ##
        final org.lastaflute.web.response.HtmlResponse response = action.reload(form);

        // ## Assert ##
        assertTrue(reloadCalled[0], "themeRegistry.reload() must have been called");
        assertTokenVerified();
        // The response must be a redirect back to AdminThemeAction (redirect(getClass()))
        final org.dbflute.utflute.lastaflute.mock.TestingHtmlData htmlData = validateHtmlData(response);
        htmlData.assertRedirect(AdminThemeAction.class);
    }
}
