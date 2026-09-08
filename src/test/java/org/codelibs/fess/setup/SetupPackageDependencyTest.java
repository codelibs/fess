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
package org.codelibs.fess.setup;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/**
 * fess-setup ships as a standalone jar and runs with only the JDK on its classpath, before
 * Fess itself starts. Anything this package imports beyond the JDK would be missing at
 * runtime. commons-compress is the specific hazard: it is on the webapp classpath only as a
 * transitive dependency of poi-ooxml, so reaching for it here would work in the IDE and fail
 * in the distribution -- and would break silently if POI ever changed its own dependencies.
 */
public class SetupPackageDependencyTest {

    /**
     * org.w3c.dom and org.xml.sax belong to the JDK's java.xml module despite not living under
     * java.* or javax.*. test_allowedNonJavaxPackagesAreReallyJdkSupplied proves that.
     *
     * @param imported the imported type name
     * @return whether the JDK supplies it
     */
    private static boolean isJdkPackage(final String imported) {
        return imported.startsWith("java.") || imported.startsWith("javax.") || imported.startsWith("org.w3c.dom")
                || imported.startsWith("org.xml.sax");
    }

    @Test
    public void test_onlyJdkImports() throws IOException {
        final Path dir = Path.of("src/main/java/org/codelibs/fess/setup");
        assertTrue(Files.isDirectory(dir), dir.toAbsolutePath() + " must exist");

        final List<String> violations = new ArrayList<>();
        final List<Path> sources;
        try (Stream<Path> files = Files.list(dir)) {
            sources = files.filter(path -> path.toString().endsWith(".java")).sorted().toList();
        }
        assertFalse(sources.isEmpty(), "no sources found in " + dir.toAbsolutePath());

        for (final Path source : sources) {
            for (final String line : Files.readAllLines(source)) {
                final String trimmed = line.trim();
                if (!trimmed.startsWith("import ")) {
                    continue;
                }
                final String imported = trimmed.substring("import ".length()).replace("static ", "").replace(";", "").trim();
                if (isJdkPackage(imported) || imported.startsWith("org.codelibs.fess.setup.")) {
                    continue;
                }
                violations.add(source.getFileName() + ": " + imported);
            }
        }
        assertTrue(violations.isEmpty(), "org.codelibs.fess.setup must depend on the JDK only, but found: " + violations);
    }

    /**
     * Guards the allowance above: were any of these to stop being JDK-supplied, the setup jar
     * would break at runtime while the import check still passed.
     */
    @Test
    public void test_allowedNonJavaxPackagesAreReallyJdkSupplied() throws ClassNotFoundException {
        for (final String name : new String[] { "org.w3c.dom.Document", "org.xml.sax.SAXException" }) {
            final Class<?> type = Class.forName(name);
            assertTrue(type.getClassLoader() == null, name + " is not loaded by the bootstrap loader");
            assertTrue("java.xml".equals(type.getModule().getName()), name + " is not in java.xml");
        }
    }
}
