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

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * Verifies that the "page not found" screen names itself in the browser title bar,
 * tab and bookmark.
 *
 * <p>The body of {@code notFound.jsp} says "page not found" while its {@code <title>}
 * used to say "system error", so a visitor who mistyped a URL was told the server had
 * broken. Nothing catches that: a JSP is not compiled by the build, both strings are
 * valid message keys, and the mismatch is only visible in a browser. This test pins the
 * key the title uses, in both the served copy and the design-editor original.</p>
 */
public class NotFoundJspTitleTest extends UnitFessTestCase {

    /** The message key the not-found title must use. */
    private static final String EXPECTED_TITLE_KEY = "labels.page_not_found_title";

    /** The served copy and the design-editor "Use Default" copy, which must not drift apart. */
    private static final String[] NOT_FOUND_JSP_PATHS =
            { "src/main/webapp/WEB-INF/view/error/notFound.jsp", "src/main/webapp/WEB-INF/orig/view/error/notFound.jsp" };

    /** Matches the message key of the document title. */
    private static final Pattern TITLE_KEY_PATTERN = Pattern.compile("<title>\\s*<la:message\\s+key=\"([^\"]+)\"");

    @Test
    public void test_titleNamesTheNotFoundPage() throws Exception {
        for (final String path : NOT_FOUND_JSP_PATHS) {
            final File file = new File(path);
            assertTrue(path + " should exist", file.exists());
            final String source = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            final Matcher matcher = TITLE_KEY_PATTERN.matcher(source);
            assertTrue(path + " should declare a message key for its title", matcher.find());
            assertEquals(path + " should title itself as the not-found page", EXPECTED_TITLE_KEY, matcher.group(1));
        }
    }

    @Test
    public void test_titleKeyIsDefined() throws Exception {
        final Properties properties = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("fess_label.properties")) {
            assertNotNull(in, "fess_label.properties should be on the classpath");
            properties.load(in);
        }
        assertNotNull(properties.getProperty(EXPECTED_TITLE_KEY), EXPECTED_TITLE_KEY + " should be defined");
    }
}
