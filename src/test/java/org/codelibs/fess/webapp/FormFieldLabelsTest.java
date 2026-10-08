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
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * Keeps a name in every label bundle for each field of a form that has a validation constraint.
 *
 * <p>A validation message such as {@code {item} is required.} takes {@code {item}} from
 * {@code labels.<field name>}. When no bundle has that key the message names the field as it is
 * written in the form class ("owner is required."), in every language. The check reads the form
 * classes under {@code app/web} (the API bodies are not forms of the admin console) and the
 * {@code fess_label*.properties} bundles as source files, so it does not depend on compiled
 * classes.</p>
 */
public class FormFieldLabelsTest extends UnitFessTestCase {

    private static final Path WEB_DIR = Paths.get("src/main/java/org/codelibs/fess/app/web");

    private static final Path RESOURCES_DIR = Paths.get("src/main/resources");

    private static final Pattern CONSTRAINT = Pattern.compile(
            "^\\s*@(Required|Size|Min|Max|Pattern|CustomSize|ValidateTypeFailure|UriType|CronExpression|Email|Digits|Length|Range|NotNull|NotBlank|NotEmpty|AssertTrue)\\b");

    private static final Pattern FIELD = Pattern.compile("^\\s*public\\s+(?!static)[\\w<>\\[\\], ?.]+\\s+(\\w+)\\s*(=.*)?;");

    @Test
    public void test_everyConstrainedFieldHasALabelInEveryBundle() throws Exception {
        final Map<String, Set<String>> fields = constrainedFields();
        assertTrue("no constrained form field found under " + WEB_DIR, fields.size() > 100);
        final List<Path> bundles = labelBundles();
        assertEquals("fess_label*.properties", 17, bundles.size());

        final List<String> missing = new ArrayList<>();
        for (final Path bundle : bundles) {
            final Set<String> keys = keysOf(bundle);
            for (final Map.Entry<String, Set<String>> field : fields.entrySet()) {
                if (!keys.contains("labels." + field.getKey())) {
                    missing.add(bundle.getFileName() + ": labels." + field.getKey() + " (" + field.getValue() + ")");
                }
            }
        }
        assertEquals("a form field with a validation constraint needs labels.<field> in every bundle (" + missing.size()
                + " missing, first 10): " + missing.subList(0, Math.min(10, missing.size())), 0, missing.size());
    }

    /** @return field name -> the form classes that declare a constrained field of that name */
    private Map<String, Set<String>> constrainedFields() throws IOException {
        final Map<String, Set<String>> fields = new TreeMap<>();
        try (Stream<Path> paths = Files.walk(WEB_DIR)) {
            for (final Path java : paths.filter(p -> p.toString().endsWith(".java") && !p.toString().contains("/api/")).toList()) {
                boolean constrained = false;
                for (final String line : Files.readAllLines(java, StandardCharsets.UTF_8)) {
                    if (CONSTRAINT.matcher(line).find()) {
                        constrained = true;
                        continue;
                    }
                    final Matcher field = FIELD.matcher(line);
                    if (constrained && field.find()) {
                        fields.computeIfAbsent(field.group(1), k -> new TreeSet<>()).add(WEB_DIR.relativize(java).toString());
                        constrained = false;
                        continue;
                    }
                    final String trimmed = line.trim();
                    if (!(trimmed.isEmpty() || trimmed.startsWith("@") || trimmed.startsWith("*") || trimmed.startsWith("/*")
                            || trimmed.startsWith("//"))) {
                        constrained = false;
                    }
                }
            }
        }
        return fields;
    }

    private List<Path> labelBundles() throws IOException {
        try (Stream<Path> paths = Files.list(RESOURCES_DIR)) {
            return paths.filter(p -> p.getFileName().toString().matches("fess_label(_\\w+)?\\.properties")).sorted().toList();
        }
    }

    private Set<String> keysOf(final Path bundle) throws IOException {
        final Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(bundle, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return new TreeSet<>(properties.stringPropertyNames());
    }
}
