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
package org.codelibs.fess.app.web.admin;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * The edit pages must not unbox {@code form.crudMode}: a request without {@code crudMode} would fail
 * with a NullPointerException (HTTP 500). The action needs a session for the token, so the check reads
 * the source.
 */
public class AdminEditCrudModeTest extends UnitFessTestCase {

    private static final Path ADMIN_DIR = Paths.get("src/main/java/org/codelibs/fess/app/web/admin");

    @Test
    public void test_noEditActionUnboxesTheCrudModeOfTheForm() throws Exception {
        final List<String> unboxed = new ArrayList<>();
        int actions = 0;
        try (Stream<Path> paths = Files.walk(ADMIN_DIR)) {
            for (final Path java : paths.filter(p -> p.getFileName().toString().matches("Admin\\w+Action\\.java")).toList()) {
                final String source = Files.readString(java, StandardCharsets.UTF_8);
                actions++;
                if (source.contains("form.crudMode.intValue()") && !java.getFileName().toString().equals("AdminUserAction.java")) {
                    unboxed.add(java.getFileName().toString());
                }
            }
        }
        assertTrue("no admin action found under " + ADMIN_DIR, actions > 20);
        assertEquals("form.crudMode is null when the request has no crudMode: " + unboxed, 0, unboxed.size());
    }
}
