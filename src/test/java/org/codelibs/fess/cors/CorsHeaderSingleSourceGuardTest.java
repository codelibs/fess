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
package org.codelibs.fess.cors;

import java.io.IOException;
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
 * Guard test: verifies that CORS-control header name string literals appear ONLY in the
 * designated single-source files (cors package + CorsFilter). Any other file emitting
 * a quoted "Access-Control-" or "Timing-Allow-Origin" literal is a regression.
 *
 * Detection strategy: scan for the character sequences {@code "Access-Control-} and
 * {@code "Timing-Allow-Origin} (opening double-quote + header prefix). This matches
 * Java string literals but NOT unquoted occurrences in comments or Javadoc (e.g. the
 * mention in FessConfig.java Javadoc does not start with a double-quote and is therefore
 * not matched).
 */
public class CorsHeaderSingleSourceGuardTest extends UnitFessTestCase {

    /** Quoted prefixes to detect CORS header string literals. */
    private static final String[] NEEDLES = { "\"Access-Control-", "\"Timing-Allow-Origin" };

    /** Path segment that identifies files in the allowed cors package. */
    private static final String CORS_PKG = "/org/codelibs/fess/cors/";

    /** Path suffix that identifies the allowed CorsFilter file. */
    private static final String CORS_FILTER = "/org/codelibs/fess/filter/CorsFilter.java";

    @Test
    public void test_noStrayCorsHeaderEmitters() throws IOException {
        final Path root = Paths.get("src/main/java");
        assertTrue("src/main/java must exist (run from repos/fess root)", Files.isDirectory(root));

        final List<String> offenders = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(p -> p.toString().endsWith(".java")).forEach(p -> {
                final String normalized = p.toString().replace('\\', '/');
                // Skip files that are the legitimate single source of CORS headers.
                if (normalized.contains(CORS_PKG) || normalized.endsWith(CORS_FILTER)) {
                    return;
                }
                final String content;
                try {
                    content = Files.readString(p, StandardCharsets.UTF_8);
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
                for (final String needle : NEEDLES) {
                    if (content.contains(needle)) {
                        offenders.add(normalized + " contains " + needle + "...");
                        break;
                    }
                }
            });
        }

        assertTrue("CORS-control header name literals must only appear in the cors package or CorsFilter. "
                + "Stray emitters found (add them to the cors package or remove the literal): " + offenders, offenders.isEmpty());
    }
}
