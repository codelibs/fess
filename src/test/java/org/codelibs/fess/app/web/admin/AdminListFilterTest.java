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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * Keeps every admin list screen whose filter can be refused by the search engine able to show the
 * refusal on the list.
 *
 * <p>A service that builds its list filter with {@code FessAppService#wrapQuery} or
 * {@code checkWildcard} throws InvalidQueryException for a filter the search engine cannot run. The
 * list query runs after the action method has returned, so the action must override
 * {@code FessAdminAction#asListHtmlWithoutFilter}; otherwise the screen answers an error page, and
 * keeps answering it while the filter stays in the pager of the session.</p>
 */
public class AdminListFilterTest extends UnitFessTestCase {

    private static final Path SERVICE_DIR = Paths.get("src/main/java/org/codelibs/fess/app/service");

    private static final Path ADMIN_DIR = Paths.get("src/main/java/org/codelibs/fess/app/web/admin");

    private static final Pattern SERVICE_FIELD = Pattern.compile("private (\\w+Service) (\\w+);");

    private static final Pattern LIST_QUERY = Pattern.compile("(\\w+)\\.get\\w+List\\(\\w+\\)");

    @Test
    public void test_listScreensOfAFilterTheSearchEngineCanRefuseShowTheRefusal() throws Exception {
        final Set<String> checkedServices = new HashSet<>();
        try (Stream<Path> paths = Files.list(SERVICE_DIR)) {
            for (final Path java : paths.filter(p -> p.getFileName().toString().endsWith("Service.java")).toList()) {
                final String name = java.getFileName().toString().replace(".java", "");
                final String source = Files.readString(java, StandardCharsets.UTF_8);
                if (!"FessAppService".equals(name) && (source.contains("wrapQuery(") || source.contains("checkWildcard("))) {
                    checkedServices.add(name);
                }
            }
        }
        assertTrue("no service with a checked filter found: " + checkedServices, checkedServices.size() >= 12);

        final List<String> covered = new ArrayList<>();
        final List<String> missing = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(ADMIN_DIR)) {
            for (final Path java : paths.filter(p -> p.getFileName().toString().matches("Admin\\w+Action\\.java")).toList()) {
                final String source = Files.readString(java, StandardCharsets.UTF_8);
                final Map<String, String> fieldTypes = new HashMap<>();
                final Matcher field = SERVICE_FIELD.matcher(source);
                while (field.find()) {
                    fieldTypes.put(field.group(2), field.group(1));
                }
                boolean listsCheckedFilter = false;
                final Matcher query = LIST_QUERY.matcher(source);
                while (query.find()) {
                    if (checkedServices.contains(fieldTypes.get(query.group(1)))) {
                        listsCheckedFilter = true;
                    }
                }
                if (!listsCheckedFilter) {
                    continue;
                }
                if (source.contains("protected OptionalThing<HtmlResponse> asListHtmlWithoutFilter()")) {
                    covered.add(java.getFileName().toString());
                } else {
                    missing.add(java.getFileName().toString());
                }
            }
        }
        assertEquals("list actions that do not override asListHtmlWithoutFilter(): " + missing, 0, missing.size());
        assertTrue("no list screen found: " + covered, covered.size() >= 12);
    }
}
