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
 * Keeps the controls of the admin console that are only an icon, and the close buttons of its
 * dialogs, from losing the name a screen reader reads.
 *
 * <p>A link or button whose content is a Font Awesome icon marked {@code aria-hidden} has no name
 * unless it carries an {@code aria-label} (or a {@code title}). The close button of a dialog gets
 * its name from the label bundles like any other text, not from an English word in the page.</p>
 */
public class AdminAccessibleNamesTest extends UnitFessTestCase {

    private static final Path VIEW_DIR = Paths.get("src/main/webapp/WEB-INF/view");

    /** An {@code <a>} or {@code <button>} that holds nothing but one icon; group 2 is its attributes. */
    private static final Pattern ICON_ONLY =
            Pattern.compile("<(a|button)\\b((?:[^>\"]|\"[^\"]*\")*)>\\s*<i\\s+class=\"[^\"]*\"\\s+aria-hidden=\"true\"\\s*></i>\\s*</\\1>");

    @Test
    public void test_iconOnlyControlsHaveAName() throws Exception {
        final List<String> nameless = new ArrayList<>();
        int found = 0;
        for (final Path jsp : jsps()) {
            final Matcher matcher = ICON_ONLY.matcher(Files.readString(jsp, StandardCharsets.UTF_8));
            while (matcher.find()) {
                found++;
                final String attributes = matcher.group(2);
                if (!attributes.contains("aria-label=") && !attributes.contains("aria-labelledby=") && !attributes.contains("title=")) {
                    nameless.add(VIEW_DIR.relativize(jsp) + ": " + matcher.group().replaceAll("\\s+", " "));
                }
            }
        }
        assertTrue("no icon-only control found under " + VIEW_DIR, found > 15);
        assertEquals("an icon-only link or button needs an aria-label: " + nameless, 0, nameless.size());
    }

    @Test
    public void test_dialogCloseButtonsAreLabelledFromTheBundles() throws Exception {
        final List<String> english = new ArrayList<>();
        for (final Path jsp : jsps()) {
            if (Files.readString(jsp, StandardCharsets.UTF_8).contains("aria-label=\"Close\"")) {
                english.add(VIEW_DIR.relativize(jsp).toString());
            }
        }
        assertEquals("aria-label=\"Close\" must be <la:message key=\"labels.crud_button_close\"/>: " + english, 0, english.size());
    }

    private List<Path> jsps() throws Exception {
        try (Stream<Path> paths = Files.walk(VIEW_DIR)) {
            return paths.filter(p -> p.toString().endsWith(".jsp")).toList();
        }
    }
}
