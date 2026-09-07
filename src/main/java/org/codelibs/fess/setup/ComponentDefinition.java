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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * One component of the setup definition file.
 *
 * @param name the component name, for example {@code opensearch}
 * @param attrs its attributes, keyed by the part after {@code component.<name>.}
 */
public record ComponentDefinition(String name, Map<String, String> attrs) {

    private static final Pattern VARIABLE = Pattern.compile("\\$\\{([^}]+)\\}");

    private static final int MAX_EXPANSION_DEPTH = 10;

    /**
     * Returns an attribute verbatim.
     *
     * @param key the attribute key
     * @return the value, or {@code null} when the attribute is absent
     */
    public String get(final String key) {
        return attrs.get(key);
    }

    /**
     * Returns an attribute with {@code ${...}} placeholders expanded. A placeholder resolves
     * against {@code vars} first and then against this component's own attributes. An
     * unresolved placeholder is left verbatim so the caller can report which key was wrong
     * rather than silently producing a URL with a hole in it.
     *
     * @param key the attribute key
     * @param vars additional variables
     * @return the expanded value, or {@code null} when the attribute is absent
     */
    public String resolve(final String key, final Map<String, String> vars) {
        final String raw = attrs.get(key);
        if (raw == null) {
            return null;
        }
        final Map<String, String> scope = new HashMap<>(attrs);
        scope.putAll(vars);
        String value = raw;
        for (int i = 0; i < MAX_EXPANSION_DEPTH; i++) {
            final Matcher matcher = VARIABLE.matcher(value);
            final StringBuilder buf = new StringBuilder();
            boolean expanded = false;
            while (matcher.find()) {
                final String replacement = scope.get(matcher.group(1));
                if (replacement == null) {
                    matcher.appendReplacement(buf, Matcher.quoteReplacement(matcher.group(0)));
                } else {
                    matcher.appendReplacement(buf, Matcher.quoteReplacement(replacement));
                    expanded = true;
                }
            }
            matcher.appendTail(buf);
            value = buf.toString();
            if (!expanded) {
                break;
            }
        }
        return value;
    }

    /**
     * Returns the token this component's publisher uses for an operating system in its artifact
     * names. Projects disagree: OpenSearch says {@code windows} where Node.js says {@code win},
     * and Node.js publishes {@code darwin} builds where OpenSearch publishes none at all, so the
     * mapping belongs to the definition rather than to this tool.
     *
     * @param os the operating system
     * @return the token, or {@code null} when the component publishes no build for it
     */
    public String osToken(final Platform.Os os) {
        return attrs.get("os." + os.name().toLowerCase(java.util.Locale.ROOT));
    }

    /**
     * Returns the path of this component's executable relative to its extracted directory.
     * An {@code executable.<os>} attribute overrides the plain {@code executable} -- Node.js puts
     * {@code node} under {@code bin/} everywhere except Windows, where {@code node.exe} sits at
     * the root of the archive.
     *
     * @param os the operating system
     * @return the relative path, or {@code null} when the component declares no executable
     */
    public String executable(final Platform.Os os) {
        final String specific = attrs.get("executable." + os.name().toLowerCase(java.util.Locale.ROOT));
        return specific != null ? specific : attrs.get("executable");
    }

    /**
     * Returns a comma-separated attribute as a list, trimming each element and dropping empties.
     *
     * @param key the attribute key
     * @return the elements, empty when the attribute is absent
     */
    public List<String> list(final String key) {
        final String raw = attrs.get(key);
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        final List<String> values = new ArrayList<>();
        for (final String element : raw.split(",")) {
            final String trimmed = element.trim();
            if (!trimmed.isEmpty()) {
                values.add(trimmed);
            }
        }
        return values;
    }
}
