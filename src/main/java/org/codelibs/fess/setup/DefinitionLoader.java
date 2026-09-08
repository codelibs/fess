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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Reads the setup definition file.
 */
public final class DefinitionLoader {

    private static final String PREFIX = "component.";

    private DefinitionLoader() {
    }

    /**
     * Parses {@code component.<name>.<attr>=<value>} lines into components. Keys that do not
     * start with {@code component.} are ignored, so the file can carry unrelated settings.
     * An attribute name may itself contain dots: only the first dot after the prefix separates
     * the component name from the attribute.
     *
     * @param in the definition stream, read as UTF-8; the caller closes it
     * @return the components, keyed by name, in name order
     * @throws IOException if the stream cannot be read
     */
    public static Map<String, ComponentDefinition> load(final InputStream in) throws IOException {
        final Properties props = new Properties();
        props.load(new InputStreamReader(in, StandardCharsets.UTF_8));

        final Map<String, Map<String, String>> grouped = new TreeMap<>();
        for (final String key : props.stringPropertyNames()) {
            if (!key.startsWith(PREFIX)) {
                continue;
            }
            final int separator = key.indexOf('.', PREFIX.length());
            if (separator < 0) {
                continue;
            }
            final String name = key.substring(PREFIX.length(), separator);
            final String attr = key.substring(separator + 1);
            grouped.computeIfAbsent(name, k -> new HashMap<>()).put(attr, props.getProperty(key).trim());
        }

        final Map<String, ComponentDefinition> definitions = new LinkedHashMap<>();
        grouped.forEach((name, attrs) -> definitions.put(name, new ComponentDefinition(name, Map.copyOf(attrs))));
        return definitions;
    }
}
