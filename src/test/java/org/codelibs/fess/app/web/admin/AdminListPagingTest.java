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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * Keeps the list screens of the admin console from passing a page number to the search engine as it
 * came in the URL.
 *
 * <p>{@code /admin/<screen>/list/<page>} takes the page from the path. A page that would end beyond
 * the search engine's result window fails the whole request, so every action that stores the page in
 * its pager must go through {@code FessAdminAction#limitPageNumber}. The dictionary screens read
 * their words from files, not from the search engine, and show every page.</p>
 */
public class AdminListPagingTest extends UnitFessTestCase {

    private static final Path ADMIN_DIR = Paths.get("src/main/java/org/codelibs/fess/app/web/admin");

    private static final Pattern SET_PAGE = Pattern.compile("\\w+Pager\\.setCurrentPageNumber\\(([^;]*)\\);");

    @Test
    public void test_listScreensLimitThePageNumberTheyStore() throws Exception {
        final List<String> unlimited = new ArrayList<>();
        int limited = 0;
        try (Stream<Path> paths = Files.walk(ADMIN_DIR)) {
            for (final Path java : paths.filter(p -> p.getFileName().toString().matches("Admin\\w+Action\\.java"))
                    .filter(p -> !p.toString().contains("/dict/"))
                    .toList()) {
                final Matcher matcher = SET_PAGE.matcher(Files.readString(java, StandardCharsets.UTF_8));
                while (matcher.find()) {
                    final String argument = matcher.group(1).trim();
                    if (argument.startsWith("limitPageNumber(")) {
                        limited++;
                    } else if (!argument.equals("0")) {
                        unlimited.add(java.getFileName() + ": " + matcher.group());
                    }
                }
            }
        }
        assertEquals("a pager must store limitPageNumber(page, pageSize), not the page as requested: " + unlimited, 0, unlimited.size());
        assertTrue("no list screen found under " + ADMIN_DIR, limited > 20);
    }
}
