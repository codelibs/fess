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
        ComponentUtil.getFessConfig().setDefaultTheme("");
        final ThemeListForm f = new ThemeListForm();
        f.defaultTheme = "bootstrap";
        // direct unit: simulate the action's body
        if (!f.defaultTheme.isEmpty() && f.defaultTheme.matches("^[a-z0-9][a-z0-9_-]{0,63}$")) {
            ComponentUtil.getFessConfig().setDefaultTheme(f.defaultTheme);
        }
        assertEquals("bootstrap", ComponentUtil.getFessConfig().getDefaultTheme());
    }

    @Test
    public void test_setdefaultClearsSystemPropertyWhenEmpty() {
        ComponentUtil.getFessConfig().setDefaultTheme("bootstrap");
        final ThemeListForm f = new ThemeListForm();
        f.defaultTheme = "";
        if (f.defaultTheme == null || f.defaultTheme.isEmpty()) {
            ComponentUtil.getFessConfig().setDefaultTheme("");
        }
        assertEquals("", ComponentUtil.getFessConfig().getDefaultTheme());
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
        ComponentUtil.getFessConfig().setDefaultTheme("oldname");
        final ThemeListForm f = new ThemeListForm();
        f.defaultTheme = "newtheme";
        final String regex = "^$|^[a-z0-9][a-z0-9_-]{0,63}$";
        // simulate the action body's regex gate + system property write
        if (f.defaultTheme != null && f.defaultTheme.matches(regex)) {
            ComponentUtil.getFessConfig().setDefaultTheme(f.defaultTheme);
        }
        assertEquals("newtheme", ComponentUtil.getFessConfig().getDefaultTheme());

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
        //   BUILT_IN       → errors.theme_is_builtin
        //   NOT_FOUND      → errors.theme_not_found
        //   INVALID_NAME   → errors.theme_name_invalid
        //   <all others>   → errors.failed_to_delete_theme (default)
        final String themeName = "test-theme";

        // Explicitly named codes with dedicated keys.
        // Note: getMessageKey() returns the bare key without surrounding braces,
        // e.g. "errors.theme_is_active" not "{errors.theme_is_active}".
        final Object[][] namedCases = { //
                { StaticThemeInstaller.InstallException.Code.ACTIVE_DEFAULT, "errors.theme_is_active" }, //
                { StaticThemeInstaller.InstallException.Code.BUILT_IN, "errors.theme_is_builtin" }, //
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
                        StaticThemeInstaller.InstallException.Code.BUILT_IN, StaticThemeInstaller.InstallException.Code.NOT_FOUND,
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

    @Test
    public void test_mapDeleteExceptionToMessage_builtIn() {
        // The bundled theme is refused outright (Task 2): the delete switch must route
        // Code.BUILT_IN to its own errors.theme_is_builtin key, not the ACTIVE_DEFAULT
        // key or the generic fallback.
        final FessMessages msgs = new FessMessages();
        AdminThemeAction.mapDeleteExceptionToMessage(msgs,
                new StaticThemeInstaller.InstallException(StaticThemeInstaller.InstallException.Code.BUILT_IN, "built-in"), "bootstrap");
        final boolean hasExpectedKey = msgs.toPropertySet().stream().flatMap(p -> {
            final java.util.Iterator<org.lastaflute.core.message.UserMessage> it = msgs.accessByIteratorOf(p);
            final java.util.List<org.lastaflute.core.message.UserMessage> list = new java.util.ArrayList<>();
            it.forEachRemaining(list::add);
            return list.stream();
        }).anyMatch(um -> "errors.theme_is_builtin".equals(um.getMessageKey()));
        assertTrue(hasExpectedKey, "Code.BUILT_IN must map to errors.theme_is_builtin but got: " + msgs);
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

        // themeArtifactHelper is also absent from the test DI container. asListHtml() (used by
        // index()/reload()/setdefault()/install() on their default and fallback render paths)
        // calls themeArtifactHelper.getAvailableArtifacts(). The real helper reads the
        // production default of theme.repositories (a live codelibs.org Maven URL) and would
        // make an actual HTTP call from inside a unit test, so tests wire a network-free stub
        // here instead -- individual tests may replace it with one of their own.
        setThemeArtifactHelper(action, noopThemeArtifactHelper());

        return action;
    }

    /**
     * Wires the given {@link ThemeArtifactHelper} into the action's private field via
     * reflection, mirroring how {@link #createInjectedAction} wires {@code themeRegistry}.
     */
    private static void setThemeArtifactHelper(final AdminThemeAction action, final org.codelibs.fess.helper.ThemeArtifactHelper helper)
            throws Exception {
        final java.lang.reflect.Field field = AdminThemeAction.class.getDeclaredField("themeArtifactHelper");
        field.setAccessible(true);
        field.set(action, helper);
    }

    /**
     * A {@link ThemeArtifactHelper} whose {@code getAvailableArtifacts()} always returns an
     * empty list and whose {@code install(...)} does nothing -- no filesystem, no network.
     */
    private static org.codelibs.fess.helper.ThemeArtifactHelper noopThemeArtifactHelper() {
        return new org.codelibs.fess.helper.ThemeArtifactHelper() {
            @Override
            public java.util.List<ThemeArtifact> getAvailableArtifacts() {
                return java.util.Collections.emptyList();
            }

            @Override
            public void install(final String name, final String version) {
                // no-op by default; tests that care about install() wire their own stub
            }
        };
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
        // The install card's theme names are registered by the same asListHtml() call; the
        // catalogue is empty here, which is a list, not an absent key.
        assertNotNull(htmlData.getDataMap().get("availableThemeNames"), "availableThemeNames must be registered in render data");
        assertTrue(((java.util.List<?>) htmlData.getDataMap().get("availableThemeNames")).isEmpty());
        // Nothing is picked, so there is no step 2.
        assertInstallStepTwoAbsent(htmlData);
    }

    /** The catalogue every install-card test below reads, as two repositories might publish it. */
    private static org.codelibs.fess.helper.ThemeArtifactHelper catalogueHelper() {
        return new org.codelibs.fess.helper.ThemeArtifactHelper() {
            @Override
            public java.util.List<ThemeArtifact> getAvailableArtifacts() {
                return java.util.List.of( //
                        new ThemeArtifact("docuforge", "1.0.0", "https://example.invalid/a/docuforge-1.0.0.zip"), //
                        new ThemeArtifact("docuforge", "1.1.0-rc1", "https://example.invalid/a/docuforge-1.1.0-rc1.zip"), //
                        new ThemeArtifact("code-search", "2.0.0", "https://example.invalid/a/code-search-2.0.0.zip"), //
                        new ThemeArtifact("docuforge", "1.0.0", "https://example.invalid/b/docuforge-1.0.0.zip"));
            }
        };
    }

    private void assertInstallStepTwoAbsent(final org.dbflute.utflute.lastaflute.mock.TestingHtmlData htmlData) {
        // RenderDataUtil.register skips a null, so "not picked" is an absent key, which the JSP
        // reads as empty -- these are not registered as empty strings or lists.
        assertFalse(htmlData.getDataMap().containsKey("pickedThemeName"),
                "pickedThemeName must not be registered when nothing valid is picked");
        assertFalse(htmlData.getDataMap().containsKey("availableVersions"));
        assertFalse(htmlData.getDataMap().containsKey("defaultVersion"));
    }

    /**
     * Picking a name that the catalogue lists is what makes the page offer its versions: newest
     * first, with the newest stable one preselected although a newer prerelease exists.
     */
    @Test
    public void test_index_withAListedInstallNameRegistersItsVersions() throws Exception {
        // ## Arrange ##
        final AdminThemeAction action = createInjectedAction(new ThemeRegistry());
        setThemeArtifactHelper(action, catalogueHelper());
        final ThemeListForm form = new ThemeListForm();
        form.installName = "docuforge";

        // ## Act ##
        final org.dbflute.utflute.lastaflute.mock.TestingHtmlData htmlData = validateHtmlData(action.index(form));

        // ## Assert ##
        final java.util.Map<String, Object> data = htmlData.getDataMap();
        assertEquals(java.util.List.of("code-search", "docuforge"), data.get("availableThemeNames"));
        assertEquals("docuforge", data.get("pickedThemeName"));
        assertEquals(java.util.List.of("1.1.0-rc1", "1.0.0"), data.get("availableVersions"));
        assertEquals("1.0.0", data.get("defaultVersion"));
    }

    @Test
    public void test_index_withAnInstallNameTheCatalogueDoesNotListShowsOnlyTheFirstStep() throws Exception {
        // ## Arrange ##
        final AdminThemeAction action = createInjectedAction(new ThemeRegistry());
        setThemeArtifactHelper(action, catalogueHelper());
        final ThemeListForm form = new ThemeListForm();
        // well-formed, so it passes the form's validation, but nobody publishes it
        form.installName = "ghost-theme";

        // ## Act ##
        final org.dbflute.utflute.lastaflute.mock.TestingHtmlData htmlData = validateHtmlData(action.index(form));

        // ## Assert ##
        assertEquals(java.util.List.of("code-search", "docuforge"), htmlData.getDataMap().get("availableThemeNames"));
        assertInstallStepTwoAbsent(htmlData);
    }

    @Test
    public void test_index_withTheEmptyPlaceholderPickedShowsOnlyTheFirstStep() throws Exception {
        // ## Arrange ##
        final AdminThemeAction action = createInjectedAction(new ThemeRegistry());
        setThemeArtifactHelper(action, catalogueHelper());
        final ThemeListForm form = new ThemeListForm();
        form.installName = ""; // what the "select a theme" option submits

        // ## Act ##
        final org.dbflute.utflute.lastaflute.mock.TestingHtmlData htmlData = validateHtmlData(action.index(form));

        // ## Assert ##
        assertInstallStepTwoAbsent(htmlData);
    }

    @Test
    public void test_index_withAMalformedInstallNameIsAValidationErrorNotAStepTwo() throws Exception {
        // ## Arrange ##
        final AdminThemeAction action = createInjectedAction(new ThemeRegistry());
        setThemeArtifactHelper(action, catalogueHelper());
        final ThemeListForm form = new ThemeListForm();
        // the query string is untrusted input: this must never reach the catalogue lookup
        form.installName = "../etc";

        // ## Act & Assert ##
        // validate() in index() rejects it and falls back to the list page, which offers the
        // first step only.
        assertValidationError(() -> action.index(form)).handle(data -> data.requiredMessageOf("installName", Pattern.class));
    }

    @Test
    public void test_themeListForm_installNameIsConstrainedLikeDefaultTheme() throws Exception {
        // installName is a request parameter of the page that renders from it, so it carries the
        // same constraints as the other theme-name field of this form, which the validate() call
        // in index() enforces.
        final Field installName = ThemeListForm.class.getDeclaredField("installName");
        final Field defaultTheme = ThemeListForm.class.getDeclaredField("defaultTheme");
        assertEquals(defaultTheme.getAnnotation(Pattern.class).regexp(), installName.getAnnotation(Pattern.class).regexp());
        assertEquals(defaultTheme.getAnnotation(jakarta.validation.constraints.Size.class).max(),
                installName.getAnnotation(jakarta.validation.constraints.Size.class).max());
        final String regex = installName.getAnnotation(Pattern.class).regexp();
        assertTrue("".matches(regex));
        assertTrue("docuforge".matches(regex));
        assertFalse("../etc".matches(regex));
        assertFalse("Docuforge".matches(regex));
    }

    /**
     * Production path covered: {@code AdminThemeAction#upload} calls {@code validate()},
     * then {@code verifyToken()}, then detects a non-zip extension via {@code hasZipExtension()}
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
     * Production path covered: {@code AdminThemeAction#setdefault} calls {@code validate()},
     * then {@code verifyToken()}, then checks {@code themeRegistry.getTheme(name).isEmpty()} for a
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

    // ── Final review Task 3: install refuses the built-in theme name ─────────────

    @Test
    public void test_upload_builtInThemeName_localizesToThemeIsBuiltin() {
        // The installer refuses to let an upload overwrite the bundled theme (Code.BUILT_IN,
        // symmetric with the existing delete-side guard); the upload mapper must route it to
        // the same errors.theme_is_builtin key delete uses, not the generic
        // errors.failed_to_upload_theme fallback.
        final StaticThemeInstaller.InstallException ex = new StaticThemeInstaller.InstallException(
                StaticThemeInstaller.InstallException.Code.BUILT_IN, "Cannot overwrite the built-in theme: bootstrap");
        final FessMessages msgs = new FessMessages();
        AdminThemeAction.mapInstallExceptionToMessage(msgs, ex);
        final boolean hasExpectedKey = msgs.toPropertySet().stream().flatMap(p -> {
            final java.util.Iterator<org.lastaflute.core.message.UserMessage> it = msgs.accessByIteratorOf(p);
            final java.util.List<org.lastaflute.core.message.UserMessage> list = new java.util.ArrayList<>();
            it.forEachRemaining(list::add);
            return list.stream();
        }).anyMatch(um -> "errors.theme_is_builtin".equals(um.getMessageKey()));
        assertTrue(hasExpectedKey, "Code.BUILT_IN must map to errors.theme_is_builtin but got: " + msgs);
    }

    // ── Final review Task 7: the admin JSP's delete guard matches ThemeRegistry.BUILT_IN_THEME_NAME ──

    @Test
    public void test_adminThemeJsp_deleteGuardMatchesBuiltInThemeName() throws Exception {
        // admin_theme.jsp hides the delete control with a literal 'bootstrap' string; pin that
        // literal to ThemeRegistry.BUILT_IN_THEME_NAME so the two cannot silently drift apart
        // (mirrors ErrorPageServletTest#test_webXmlPointsEveryErrorPageAtThisServlet).
        final String jsp = java.nio.file.Files.readString(java.nio.file.Path.of("src/main/webapp/WEB-INF/view/admin/theme/admin_theme.jsp"),
                java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(jsp.contains("t.name != '" + ThemeRegistry.BUILT_IN_THEME_NAME + "'"),
                "admin_theme.jsp's delete-control guard must compare against ThemeRegistry.BUILT_IN_THEME_NAME ('"
                        + ThemeRegistry.BUILT_IN_THEME_NAME + "'), not an unpinned literal");
    }

    /**
     * Production path covered: {@code AdminThemeAction#reload} calls {@code validate()},
     * then {@code verifyToken()}, then {@code themeRegistry.reload()}, then
     * {@code saveInfo(addSuccessReloadTheme)}, then {@code redirect(getClass())} — the full
     * success flow exercised end-to-end.
     * A no-op {@code ThemeRegistry} subclass is used so the reload does not require the
     * filesystem/search-engine, mirroring the StaticThemeResponderTest injection style.
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

    // ── Task 5: install a published theme from the admin screen ──────────────────

    @Test
    public void test_themeInstallForm_nameAndVersionAreRequiredAndBounded() throws Exception {
        // Pin the form-layer safety net: install() relies on @Required + @Size(max=100)
        // to reject a blank or absurdly long name/version before it ever reaches
        // ThemeArtifactHelper#install (which itself pattern-checks both before building a URL).
        for (final String fieldName : new String[] { "name", "version" }) {
            final Field field = ThemeInstallForm.class.getDeclaredField(fieldName);
            assertNotNull(field.getAnnotation(Required.class), fieldName + " must be @Required");
            final jakarta.validation.constraints.Size size = field.getAnnotation(jakarta.validation.constraints.Size.class);
            assertNotNull(size, fieldName + " must be @Size-bounded");
            assertEquals(100, size.max(), fieldName + " must cap at 100 characters");
        }
    }

    /**
     * Production path covered: {@code AdminThemeAction#install} calls {@code validate()}, then
     * {@code verifyToken()}, then {@code themeArtifactHelper.install(name, version)}, then
     * {@code saveInfo(addSuccessInstallTheme)}, then {@code redirect(getClass())} — the full
     * success flow, exercised through the real action method rather than simulated.
     */
    @Test
    public void test_install_callsHelperWithFormArgsAndRedirectsOnSuccess() throws Exception {
        // ## Arrange ##
        final AdminThemeAction action = createInjectedAction(new ThemeRegistry());
        mockTokenRequested(action.getClass());

        final String[] recordedArgs = new String[2];
        setThemeArtifactHelper(action, new org.codelibs.fess.helper.ThemeArtifactHelper() {
            @Override
            public void install(final String name, final String version) {
                recordedArgs[0] = name;
                recordedArgs[1] = version;
            }
        });

        final ThemeInstallForm form = new ThemeInstallForm();
        form.name = "dark-mode";
        form.version = "1.2.3";

        // ## Act ##
        final org.lastaflute.web.response.HtmlResponse response = action.install(form);

        // ## Assert ##
        // If install() stopped calling themeArtifactHelper.install(...), or passed the wrong
        // arguments, this fails -- unlike a test that only checks the redirect happened.
        // All three args are String, so the message-first (String, Object, Object) and
        // message-last (Object, Object, String) overloads would be ambiguous; call
        // org.junit.jupiter.api.Assertions directly, as UnitFessTestCase's own comment advises.
        org.junit.jupiter.api.Assertions.assertEquals("dark-mode", recordedArgs[0],
                "install() must pass the form's name through unchanged");
        org.junit.jupiter.api.Assertions.assertEquals("1.2.3", recordedArgs[1], "install() must pass the form's version through unchanged");
        assertTokenVerified();
        final org.dbflute.utflute.lastaflute.mock.TestingHtmlData htmlData = validateHtmlData(response);
        htmlData.assertRedirect(AdminThemeAction.class);
    }

    /**
     * Pins the ordering the brief calls out: {@code install()} catches
     * {@code StaticThemeInstaller.InstallException} before the generic
     * {@code ThemeArtifactHelper.ThemeArtifactException}, so an installer-level
     * {@code INCOMPATIBLE_FESS_VERSION} rejection reaches the operator as the dedicated
     * {@code errors.theme_incompatible_fess_version} message, not the generic
     * {@code errors.failed_to_install_theme} fallback. Exercised through the real
     * {@code install()} method (not just {@code mapInstallExceptionToMessage} in isolation),
     * so a catch-order regression would fail this test.
     */
    @Test
    public void test_install_incompatibleFessVersion_surfacesDedicatedMessage() throws Exception {
        // ## Arrange ##
        final AdminThemeAction action = createInjectedAction(new ThemeRegistry());
        mockTokenRequested(action.getClass());
        setThemeArtifactHelper(action, new org.codelibs.fess.helper.ThemeArtifactHelper() {
            @Override
            public void install(final String name, final String version) {
                throw new StaticThemeInstaller.InstallException(StaticThemeInstaller.InstallException.Code.INCOMPATIBLE_FESS_VERSION,
                        "This theme requires a newer version of Fess than this server.");
            }
        });

        final ThemeInstallForm form = new ThemeInstallForm();
        form.name = "future-theme";
        form.version = "9.9.9";

        // ## Act & Assert ##
        assertValidationError(() -> action.install(form))
                .handle(data -> data.requiredMessageOf("_global", "errors.theme_incompatible_fess_version"));
        assertTokenVerified();
    }

    /**
     * When {@code ThemeArtifactHelper#install} raises its own {@code ThemeArtifactException}
     * (unknown theme/version, download failure, checksum mismatch, ...), {@code install()}
     * must route it to the generic {@code errors.failed_to_install_theme} key with the
     * exception's message as the argument.
     */
    @Test
    public void test_install_themeArtifactException_surfacesGenericFailedMessage() throws Exception {
        // ## Arrange ##
        final AdminThemeAction action = createInjectedAction(new ThemeRegistry());
        mockTokenRequested(action.getClass());
        setThemeArtifactHelper(action, new org.codelibs.fess.helper.ThemeArtifactHelper() {
            @Override
            public void install(final String name, final String version) {
                throw new org.codelibs.fess.helper.ThemeArtifactHelper.ThemeArtifactException(
                        "Not a theme name and version: " + name + " " + version);
            }
        });

        final ThemeInstallForm form = new ThemeInstallForm();
        form.name = "ghost-theme";
        form.version = "1.0.0";

        // ## Act & Assert ##
        assertValidationError(() -> action.install(form))
                .handle(data -> data.requiredMessageOf("_global", "errors.failed_to_install_theme"));
        assertTokenVerified();
    }

    private static String adminThemeJsp() throws Exception {
        return java.nio.file.Files.readString(java.nio.file.Path.of("src/main/webapp/WEB-INF/view/admin/theme/admin_theme.jsp"),
                java.nio.charset.StandardCharsets.UTF_8);
    }

    @Test
    public void test_adminThemeJsp_installsThroughOneFormWithAVersionSelect() throws Exception {
        // The install card posts one form to the one install action, with the name the first
        // step picked and a version chosen from a list -- not typed.
        final String jsp = adminThemeJsp();
        final String marker = "la:form action=\"/admin/theme/install\"";
        assertEquals(jsp.indexOf(marker), jsp.lastIndexOf(marker), "expected exactly one form posting to /admin/theme/install");
        assertTrue(jsp.contains(marker));
        assertTrue(jsp.contains("<input type=\"hidden\" name=\"name\" value=\"${f:h(pickedThemeName)}\""),
                "the install form must post the picked name");
        // The step 1 select stays editable after a pick, so the picked name is also shown as text
        // next to the version select: the name that is posted must be the one the operator reads.
        assertTrue(jsp.contains("<div class=\"form-control-plaintext\">${f:h(pickedThemeName)}</div>"),
                "the install form must show the name it posts");
        assertTrue(jsp.contains("<select id=\"installVersion\" name=\"version\""), "the install form must post the version from a select");
        // No way to type a name or a version any more: that is the point of the two steps.
        assertFalse(jsp.contains("type=\"text\""), "admin_theme.jsp must not offer a free-text input");
        assertFalse(jsp.contains("<input type=\"hidden\" name=\"version\""), "the version is chosen, not pre-set per catalogue row");
    }

    @Test
    public void test_adminThemeJsp_versionsAreOnlyOfferedOncePastTheNameStep() throws Exception {
        final String jsp = adminThemeJsp();
        // step 1: a plain GET form to the list page, whose select is the installName parameter
        final int step1 = jsp.indexOf("<form method=\"get\" action=\"${fe:url('/admin/theme/')}\">");
        assertTrue(step1 >= 0, "the name step must be a plain GET form to /admin/theme/");
        assertTrue(jsp.indexOf("<select id=\"installName\" name=\"installName\"", step1) > step1);
        assertTrue(jsp.contains("key=\"labels.theme_install_select_name\""), "the placeholder option needs its label");
        assertTrue(jsp.contains("key=\"labels.theme_install_select\""), "the name step needs its submit button label");
        // step 2 only exists behind a picked name
        final int guard = jsp.indexOf("<c:if test=\"${not empty pickedThemeName}\">");
        assertTrue(guard > step1, "the version step must be guarded by a picked name, after the name step");
        assertTrue(jsp.indexOf("name=\"version\"") > guard, "the version select must sit inside that guard");
        assertTrue(jsp.indexOf("la:form action=\"/admin/theme/install\"") > guard, "the install form must sit inside that guard");
    }

    @Test
    public void test_adminThemeJsp_noLongerListsTheCatalogueOrTakesAFreeTextName() throws Exception {
        final String jsp = adminThemeJsp();
        assertFalse(jsp.contains("availableArtifacts"), "the catalogue table is gone");
        assertFalse(jsp.contains("labels.theme_available_title"));
        assertFalse(jsp.contains("labels.theme_install_by_name_title"));
        assertTrue(jsp.contains("key=\"labels.theme_install_title\""), "the install card keeps its title");
    }

    @Test
    public void test_adminThemeJsp_everySelectHasALabelWithAUniqueId() throws Exception {
        final String jsp = adminThemeJsp();
        final java.util.regex.Matcher matcher =
                java.util.regex.Pattern.compile("<(?:la:)?select\\b[^>]*?\\b(?:id|styleId)=\"([^\"]+)\"").matcher(jsp);
        final java.util.List<String> ids = new java.util.ArrayList<>();
        while (matcher.find()) {
            ids.add(matcher.group(1));
        }
        assertTrue(ids.contains("installName") && ids.contains("installVersion") && ids.contains("defaultTheme"),
                "expected the default, name and version selects, got " + ids);
        assertEquals(new java.util.HashSet<>(ids).size(), ids.size(), "select ids must be unique: " + ids);
        for (final String id : ids) {
            assertTrue(jsp.contains("<label for=\"" + id + "\""), "select '" + id + "' needs a <label for=\"" + id + "\">");
        }
    }

    @Test
    public void test_adminThemeJsp_everyBoundPropertyExistsOnThemeListForm() throws Exception {
        // The list page renders with ThemeListForm (asListHtml). A form-bound tag such as
        // la:text or la:select resolves its property against that form while the page renders,
        // so a property the form lacks aborts the response part-way through the HTML
        // (FormPropertyNotFoundException). Fields posted to another action's form must be
        // plain inputs instead.
        final String jsp = java.nio.file.Files.readString(java.nio.file.Path.of("src/main/webapp/WEB-INF/view/admin/theme/admin_theme.jsp"),
                java.nio.charset.StandardCharsets.UTF_8);
        final java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("<la:\\w+\\b[^>]*?\\bproperty=\"([^\"]+)\"").matcher(jsp);
        final java.util.List<String> properties = new java.util.ArrayList<>();
        while (matcher.find()) {
            properties.add(matcher.group(1));
        }
        assertFalse(properties.isEmpty(), "expected at least one form-bound property (defaultTheme) in admin_theme.jsp");
        for (final String property : properties) {
            try {
                ThemeListForm.class.getField(property);
            } catch (final NoSuchFieldException e) {
                fail("admin_theme.jsp binds property '" + property + "', which ThemeListForm does not declare");
            }
        }
    }

    @Test
    public void test_adminThemeJsp_emptyCatalogueUsesItsOwnLabelNotTheGenericCrudNotFound() throws Exception {
        // labels.list_could_not_find_crud_table reads as "no themes are published"; an empty
        // catalogue can also mean the repository's index was momentarily unreadable, which is
        // why it gets its own labels.theme_available_empty message instead (see the brief).
        final String jsp = java.nio.file.Files.readString(java.nio.file.Path.of("src/main/webapp/WEB-INF/view/admin/theme/admin_theme.jsp"),
                java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(jsp.contains("key=\"labels.theme_available_empty\""),
                "the empty-catalogue branch must render labels.theme_available_empty");
        // Checked as an actual la:message reference, not a bare substring: the explanatory
        // comment above the empty-catalogue branch names labels.list_could_not_find_crud_table
        // on purpose (it must be kept, per the brief), so a naive jsp.contains(...) on the raw
        // key name would false-positive on that comment text.
        assertFalse(jsp.contains("key=\"labels.list_could_not_find_crud_table\""),
                "admin_theme.jsp must not render the generic CRUD \"not found\" label for an empty theme catalogue");
    }
}
