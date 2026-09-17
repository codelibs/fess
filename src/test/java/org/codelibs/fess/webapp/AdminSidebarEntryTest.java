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
package org.codelibs.fess.webapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

/**
 * Guards the administration sidebar against entries for screens that no longer exist.
 *
 * <p>A JSP is not compiled by the build, so a menu item that still points at a removed
 * action is only noticed when an administrator clicks it. Every {@code /admin/<name>/}
 * link and every {@code admin-<name>-view} permission in the sidebar must name the action
 * class {@code org.codelibs.fess.app.web.admin.<name>.Admin<Name>Action}.</p>
 */
public class AdminSidebarEntryTest {

    private static final Path SIDEBAR_JSP = Paths.get("src/main/webapp/WEB-INF/view/common/admin/sidebar.jsp");

    /** Matches {@code fe:url('/admin/<name>/')}. */
    private static final Pattern LINK_PATTERN = Pattern.compile("fe:url\\('/admin/([a-z]+)/'\\)");

    /** Matches {@code fe:permission('admin-<name>-view')}. */
    private static final Pattern PERMISSION_PATTERN = Pattern.compile("fe:permission\\('admin-([a-z]+)-view'\\)");

    /** Lower bound on the names found, so a broken pattern cannot pass vacuously. */
    private static final int MINIMUM_EXPECTED_NAMES = 30;

    @Test
    public void test_everyLinkNamesAnExistingAction() throws Exception {
        assertEveryNameHasAnAction(LINK_PATTERN);
    }

    @Test
    public void test_everyPermissionNamesAnExistingAction() throws Exception {
        assertEveryNameHasAnAction(PERMISSION_PATTERN);
    }

    private void assertEveryNameHasAnAction(final Pattern pattern) throws Exception {
        final String source = new String(Files.readAllBytes(SIDEBAR_JSP), StandardCharsets.UTF_8);
        final Set<String> names = new TreeSet<>();
        final Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            names.add(matcher.group(1));
        }
        assertTrue(names.size() >= MINIMUM_EXPECTED_NAMES,
                names.size() + " names found in " + SIDEBAR_JSP + ", expected at least " + MINIMUM_EXPECTED_NAMES);
        final List<String> missing = new ArrayList<>();
        for (final String name : names) {
            final String className = "org.codelibs.fess.app.web.admin." + name + ".Admin" + name.substring(0, 1).toUpperCase(Locale.ROOT)
                    + name.substring(1) + "Action";
            try {
                Class.forName(className, false, getClass().getClassLoader());
            } catch (final ClassNotFoundException e) {
                missing.add(name + " -> " + className);
            }
        }
        assertEquals(List.of(), missing, "sidebar entries whose action class does not exist");
    }
}
