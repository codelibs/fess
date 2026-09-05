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
 * Guards the shape of {@code <la:message>} usage across every JSP.
 *
 * <p>A JSP is not compiled by the build, so a mistyped attribute name is only
 * discovered when a visitor opens the page and the container fails to translate it.
 * The administration error screen carried {@code ke=} instead of {@code key=} for
 * years for exactly that reason: it is the page shown when something has already
 * gone wrong, so nobody exercised it, and the TLD rejects the attribute at
 * translation time rather than at build time.</p>
 *
 * <p>These checks are cheap stand-ins for a full JSP compile. They catch the two
 * ways that line was broken -- an attribute the tag does not declare, and a tag
 * preceded by a stray {@code <}.</p>
 */
public class MessageTagUsageTest extends UnitFessTestCase {

    /** Root of the JSP tree, covering both the served views and the design-editor originals. */
    private static final String WEB_INF_DIR = "src/main/webapp/WEB-INF";

    /** Matches a whole {@code <la:message ...>} tag, tolerating quoted attribute values. */
    private static final Pattern MESSAGE_TAG_PATTERN = Pattern.compile("<la:message\\b((?:[^>\"]|\"[^\"]*\")*?)/?>", Pattern.DOTALL);

    /** Matches the {@code key} attribute the tag requires. */
    private static final Pattern KEY_ATTRIBUTE_PATTERN = Pattern.compile("\\bkey\\s*=");

    /** Matches a tag opened with one angle bracket too many, which renders a literal {@code <}. */
    private static final Pattern DOUBLED_ANGLE_BRACKET_PATTERN = Pattern.compile("<<[A-Za-z/]");

    /** Lower bound on the number of tags scanned, so a broken pattern cannot pass vacuously. */
    private static final int MINIMUM_EXPECTED_TAGS = 1000;

    @Test
    public void test_everyMessageTagDeclaresAKey() throws Exception {
        final List<String> offenders = new ArrayList<>();
        int tagCount = 0;
        for (final Path jsp : listJspFiles()) {
            final String source = read(jsp);
            final Matcher matcher = MESSAGE_TAG_PATTERN.matcher(source);
            while (matcher.find()) {
                tagCount++;
                if (!KEY_ATTRIBUTE_PATTERN.matcher(matcher.group(1)).find()) {
                    offenders.add(jsp + ":" + lineOf(source, matcher.start()) + " " + matcher.group());
                }
            }
        }
        assertTrue(tagCount + " message tags scanned, expected at least " + MINIMUM_EXPECTED_TAGS, tagCount >= MINIMUM_EXPECTED_TAGS);
        assertEquals("every <la:message> must declare a key attribute: " + offenders, 0, offenders.size());
    }

    @Test
    public void test_noTagIsOpenedWithAStrayAngleBracket() throws Exception {
        final List<String> offenders = new ArrayList<>();
        for (final Path jsp : listJspFiles()) {
            final String source = read(jsp);
            final Matcher matcher = DOUBLED_ANGLE_BRACKET_PATTERN.matcher(source);
            while (matcher.find()) {
                offenders.add(jsp + ":" + lineOf(source, matcher.start()));
            }
        }
        assertEquals("no tag may be preceded by a stray '<': " + offenders, 0, offenders.size());
    }

    private List<Path> listJspFiles() throws IOException {
        final Path root = Paths.get(WEB_INF_DIR);
        assertTrue(root + " should exist", Files.isDirectory(root));
        try (Stream<Path> paths = Files.walk(root)) {
            final List<Path> files = paths.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".jsp")).sorted().toList();
            assertFalse("no JSP found under " + root, files.isEmpty());
            return files;
        }
    }

    private String read(final Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private int lineOf(final String source, final int offset) {
        return (int) source.substring(0, offset).chars().filter(c -> c == '\n').count() + 1;
    }
}
