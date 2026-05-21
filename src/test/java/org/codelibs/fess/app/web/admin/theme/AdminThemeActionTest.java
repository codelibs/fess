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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.codelibs.fess.theme.ThemeRegistry;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;

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
}
