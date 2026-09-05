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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * Verifies that every error screen names itself the same way in the browser title
 * bar as it does on the page.
 *
 * <p>The not-found, bad-request and generic error screens each said one thing in
 * their heading and "system error" in their {@code <title>}, so a visitor who
 * mistyped a URL was told the server had broken, and the tab, the window title and
 * any bookmark all disagreed with the page they came from. Nothing catches that: a
 * JSP is not compiled by the build, both strings are valid message keys, and the
 * mismatch is only visible in a browser.</p>
 *
 * <p>The check is the invariant those pages share -- the title key is the heading
 * key -- applied to every error screen in both the served tree and the design-editor
 * originals, so a new error page inherits it too.</p>
 */
public class ErrorPageTitleTest extends UnitFessTestCase {

    /** The served copies and the design-editor "Use Default" copies, which must not drift apart. */
    private static final String[] ERROR_VIEW_DIRS = { "src/main/webapp/WEB-INF/view/error", "src/main/webapp/WEB-INF/orig/view/error" };

    /** Matches the message key of the document title. */
    private static final Pattern TITLE_KEY_PATTERN = Pattern.compile("<title>\\s*<la:message\\s+key=\"([^\"]+)\"");

    /** Matches the message key of the page heading. */
    private static final Pattern HEADING_KEY_PATTERN = Pattern.compile("<h2>\\s*<la:message\\s+key=\"([^\"]+)\"");

    /** Lower bound on the pages compared per tree, so a broken pattern cannot pass vacuously. */
    private static final int MINIMUM_EXPECTED_PAGES = 5;

    @Test
    public void test_titleNamesTheSamePageAsTheHeading() throws Exception {
        final List<String> mismatches = new ArrayList<>();
        for (final String dir : ERROR_VIEW_DIRS) {
            int compared = 0;
            for (final Path jsp : listJspFiles(dir)) {
                final String source = read(jsp);
                final String titleKey = firstGroup(TITLE_KEY_PATTERN, source);
                final String headingKey = firstGroup(HEADING_KEY_PATTERN, source);
                if (titleKey == null && headingKey == null) {
                    // redirect.jsp renders no page of its own; it only forwards.
                    continue;
                }
                assertNotNull(titleKey, jsp + " has a heading but no title");
                assertNotNull(headingKey, jsp + " has a title but no heading");
                if (!headingKey.equals(titleKey)) {
                    mismatches.add(jsp + " titled " + titleKey + " but headed " + headingKey);
                }
                compared++;
            }
            assertTrue(dir + " compared only " + compared + " pages", compared >= MINIMUM_EXPECTED_PAGES);
        }
        assertEquals("every error page must title itself as the page its heading names: " + mismatches, 0, mismatches.size());
    }

    @Test
    public void test_titleKeysAreDefined() throws Exception {
        final Properties properties = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("fess_label.properties")) {
            assertNotNull(in, "fess_label.properties should be on the classpath");
            properties.load(in);
        }
        final List<String> undefined = new ArrayList<>();
        for (final String dir : ERROR_VIEW_DIRS) {
            for (final Path jsp : listJspFiles(dir)) {
                final String titleKey = firstGroup(TITLE_KEY_PATTERN, read(jsp));
                if (titleKey != null && properties.getProperty(titleKey) == null) {
                    undefined.add(jsp + " -> " + titleKey);
                }
            }
        }
        assertEquals("every error page title key must be defined: " + undefined, 0, undefined.size());
    }

    private List<Path> listJspFiles(final String dir) throws IOException {
        final Path root = Paths.get(dir);
        assertTrue(root + " should exist", Files.isDirectory(root));
        try (Stream<Path> paths = Files.list(root)) {
            final List<Path> files = paths.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".jsp")).sorted().toList();
            assertFalse("no JSP found under " + root, files.isEmpty());
            return files;
        }
    }

    private String read(final Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private String firstGroup(final Pattern pattern, final String source) {
        final Matcher matcher = pattern.matcher(source);
        return matcher.find() ? matcher.group(1) : null;
    }
}
