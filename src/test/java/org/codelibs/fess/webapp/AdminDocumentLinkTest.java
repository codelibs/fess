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
 * Keeps the admin JSPs from printing the URL of an indexed document as it is.
 *
 * <p>The URL of a document comes from the crawled data, so it can hold a double quote or use any
 * scheme. A JSP is not compiled into a test, so this reads the JSP source: every expression that
 * prints {@code url_link} must be HTML-escaped with {@code f:h}, and one used as the {@code href}
 * of a link must also go through {@code fe:safeHref}.</p>
 */
public class AdminDocumentLinkTest extends UnitFessTestCase {

    private static final Path ADMIN_VIEW_DIR = Paths.get("src/main/webapp/WEB-INF/view/admin");

    private static final Pattern URL_LINK_EXPRESSION = Pattern.compile("\\$\\{[^}]*url_link[^}]*\\}");

    private static final Pattern HREF_ATTRIBUTE = Pattern.compile("href=\"([^\"]*)\"");

    @Test
    public void test_documentUrlIsEscapedWherePrinted() throws Exception {
        final List<String> unescaped = new ArrayList<>();
        int found = 0;
        for (final Path jsp : jsps()) {
            final Matcher matcher = URL_LINK_EXPRESSION.matcher(Files.readString(jsp, StandardCharsets.UTF_8));
            while (matcher.find()) {
                found++;
                if (!matcher.group().startsWith("${f:h(")) {
                    unescaped.add(jsp + ": " + matcher.group());
                }
            }
        }
        assertTrue("no url_link expression found under " + ADMIN_VIEW_DIR, found > 0);
        assertEquals("url_link must be printed through f:h: " + unescaped, 0, unescaped.size());
    }

    @Test
    public void test_documentLinkHrefChecksTheScheme() throws Exception {
        final List<String> unchecked = new ArrayList<>();
        int found = 0;
        for (final Path jsp : jsps()) {
            final Matcher matcher = HREF_ATTRIBUTE.matcher(Files.readString(jsp, StandardCharsets.UTF_8));
            while (matcher.find()) {
                if (matcher.group(1).contains("url_link")) {
                    found++;
                    if (!matcher.group(1).contains("fe:safeHref(")) {
                        unchecked.add(jsp + ": " + matcher.group());
                    }
                }
            }
        }
        assertTrue("no href printing url_link found under " + ADMIN_VIEW_DIR, found > 0);
        assertEquals("an href printing url_link must use fe:safeHref: " + unchecked, 0, unchecked.size());
    }

    private List<Path> jsps() throws Exception {
        try (Stream<Path> paths = Files.walk(ADMIN_VIEW_DIR)) {
            return paths.filter(p -> p.toString().endsWith(".jsp")).toList();
        }
    }
}
