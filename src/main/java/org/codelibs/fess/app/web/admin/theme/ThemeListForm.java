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

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Form for the theme list page. Carries the selected default theme name when
 * the administrator submits the "set default" dropdown.
 *
 * <p>Note: {@code crudMode} is intentionally absent. Theme management is not a
 * standard CRUD flow (there is no edit page; upload replaces create), so the
 * CRUD-mode convention does not apply here.</p>
 */
public class ThemeListForm {

    /** Default constructor used by the framework form binder. */
    public ThemeListForm() {
        // default constructor
    }

    /**
     * Selected default theme name, or empty string when "no default" is chosen.
     * Validation matches the spec §4.2 manifest regex so an invalid value
     * cannot reach {@code FessConfig.setSystemProperty("theme.default", ...)}.
     */
    @Size(max = 64)
    @Pattern(regexp = "^$|^[a-z0-9][a-z0-9_-]{0,63}$")
    public String defaultTheme;
}
