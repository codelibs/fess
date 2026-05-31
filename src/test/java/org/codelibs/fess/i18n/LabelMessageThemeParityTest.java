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
package org.codelibs.fess.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

/**
 * Parity guard for the theme-admin i18n keys introduced by PR #3136.
 *
 * <p>The default {@code fess_label.properties} / {@code fess_message.properties}
 * bundles define the canonical set of theme keys. Every locale-specific bundle
 * must define the same set of keys (English fallback values are acceptable as a
 * machine-translation-pending stub, per the established precedent for new
 * feature keys).</p>
 */
public class LabelMessageThemeParityTest {

    private static final String[] LOCALES =
            { "", "_en", "_de", "_es", "_fr", "_hi", "_id", "_it", "_ja", "_ko", "_nl", "_pl", "_pt_BR", "_ru", "_tr", "_zh_CN", "_zh_TW" };

    /** Matches both {@code labels.menu_theme} and {@code labels.theme_*} keys. */
    private static final String LABEL_KEY_PATTERN = "labels\\.theme_|labels\\.menu_theme";

    /** Matches {@code errors.*theme*} and {@code success.*theme*} keys. */
    private static final String MESSAGE_KEY_PATTERN = "(errors|success)\\..*theme.*";

    @Test
    public void test_labelThemeKeysParityAcrossLocales() throws Exception {
        final Set<String> baseKeys = loadKeys("fess_label.properties", LABEL_KEY_PATTERN);
        assertFalse(baseKeys.isEmpty(), "expected theme labels in default bundle");
        for (final String loc : LOCALES) {
            if (loc.isEmpty()) {
                continue;
            }
            final String resource = "fess_label" + loc + ".properties";
            final Set<String> keys = loadKeys(resource, LABEL_KEY_PATTERN);
            assertEquals(baseKeys, keys, "label theme keys mismatch in " + resource);
        }
    }

    @Test
    public void test_messageThemeKeysParityAcrossLocales() throws Exception {
        final Set<String> baseKeys = loadKeys("fess_message.properties", MESSAGE_KEY_PATTERN);
        assertFalse(baseKeys.isEmpty(), "expected theme messages in default bundle");
        for (final String loc : LOCALES) {
            if (loc.isEmpty()) {
                continue;
            }
            final String resource = "fess_message" + loc + ".properties";
            final Set<String> keys = loadKeys(resource, MESSAGE_KEY_PATTERN);
            assertEquals(baseKeys, keys, "message theme keys mismatch in " + resource);
        }
    }

    private Set<String> loadKeys(final String resource, final String keyPattern) throws Exception {
        final Properties p = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resource)) {
            assertNotNull(in, "resource not found on classpath: " + resource);
            p.load(in);
        }
        final Pattern re = Pattern.compile(keyPattern);
        final Set<String> keys = new TreeSet<>();
        for (final Object k : p.keySet()) {
            final String key = k.toString();
            if (re.matcher(key).find()) {
                keys.add(key);
            }
        }
        return keys;
    }
}
