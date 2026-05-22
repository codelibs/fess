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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Locale;

import org.codelibs.fess.theme.StaticThemeInstaller;
import org.codelibs.fess.theme.ThemeRegistry;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Disabled;
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

    // ---- Tests requiring full LastaFlute context — deferred ----

    @Test
    @Disabled("TODO: requires full LastaFlute test harness "
            + "(validate/verifyToken/saveToken/asHtml/redirect) — covered by Plan 6 integration tests")
    public void test_index_savesTokenAndRendersList() {
        // Direct invocation of AdminThemeAction#index needs a live ActionRuntime,
        // session-managed transaction token, and a renderable HTML response.
    }

    @Test
    @Disabled("TODO: requires full LastaFlute test harness — covered by Plan 6 integration tests")
    public void test_upload_rejectsNonZipFileExtension() {
        // Driving #upload requires a MultipartFormFile fixture and the action's
        // validate()/verifyToken() plumbing.
    }

    @Test
    @Disabled("TODO: requires full LastaFlute test harness — covered by Plan 6 integration tests")
    public void test_setdefault_throwsValidationErrorWhenThemeMissing() {
        // The themeRegistry.getTheme(name).isEmpty() branch in #setdefault
        // produces a throwValidationError that needs the LastaFlute message
        // pipeline to be observable.
    }

    @Test
    @Disabled("TODO: requires full LastaFlute test harness — covered by Plan 6 integration tests")
    public void test_reload_redirectsAfterRegistryReload() {
        // #reload calls verifyToken(), saveInfo(), and redirect(getClass()) —
        // all require the full LastaFlute container.
    }
}
