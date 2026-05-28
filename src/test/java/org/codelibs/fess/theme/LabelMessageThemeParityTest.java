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
package org.codelibs.fess.theme;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Verifies cross-locale key parity for the bundled bootstrap theme i18n bundles.
 *
 * <p>Every key present in {@code messages.en.json} must also be present in
 * {@code messages.ja.json} and all six other supported locale bundles (de, es,
 * fr, ko, pt-BR, zh-CN). An additional sanity check ensures all {@code
 * labels.chat_*} keys introduced for the chat feature are present in all 8
 * bundles.</p>
 */
public class LabelMessageThemeParityTest {

    private static final Path I18N_DIR = Paths.get("src/main/webapp/themes/bootstrap/i18n");

    /** All non-English locale bundles that must mirror en's key set. */
    private static final List<String> OTHER_LOCALES = Arrays.asList("ja", "de", "es", "fr", "ko", "pt-BR", "zh-CN");

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadBundle(final String locale) throws Exception {
        final Path path = I18N_DIR.resolve("messages." + locale + ".json");
        assertTrue(Files.isRegularFile(path), "i18n bundle missing: " + path.toAbsolutePath());
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(Files.readAllBytes(path), LinkedHashMap.class);
    }

    /**
     * Every key in {@code messages.en.json} must also exist in {@code messages.ja.json}.
     */
    @Test
    public void test_enJaParity() throws Exception {
        final Set<String> enKeys = new TreeSet<>(loadBundle("en").keySet());
        final Set<String> jaKeys = new TreeSet<>(loadBundle("ja").keySet());

        final Set<String> missingInJa = new TreeSet<>(enKeys);
        missingInJa.removeAll(jaKeys);
        assertTrue(missingInJa.isEmpty(), "Keys present in messages.en.json but missing from messages.ja.json: " + missingInJa);
    }

    /**
     * Every key in {@code messages.en.json} must also exist in each of the six
     * other locale bundles (de, es, fr, ko, pt-BR, zh-CN).
     */
    @Test
    public void test_allOtherLocalesHaveAllEnKeys() throws Exception {
        final Set<String> enKeys = new TreeSet<>(loadBundle("en").keySet());

        for (final String locale : OTHER_LOCALES) {
            final Set<String> localeKeys = new TreeSet<>(loadBundle(locale).keySet());
            final Set<String> missing = new TreeSet<>(enKeys);
            missing.removeAll(localeKeys);
            assertTrue(missing.isEmpty(), "Keys present in messages.en.json but missing from messages." + locale + ".json: " + missing);
        }
    }

    /**
     * Sanity-check: all {@code labels.chat_*} keys are present in every bundle
     * (en, ja, de, es, fr, ko, pt-BR, zh-CN).
     */
    @Test
    public void test_chatKeysInAllBundles() throws Exception {
        final Set<String> enKeys = loadBundle("en").keySet();
        final Set<String> chatKeys =
                enKeys.stream().filter(k -> k.startsWith("labels.chat_")).collect(Collectors.toCollection(TreeSet::new));

        assertTrue(!chatKeys.isEmpty(), "No labels.chat_* keys found in messages.en.json — something is wrong");

        final List<String> allLocales = Arrays.asList("en", "ja", "de", "es", "fr", "ko", "pt-BR", "zh-CN");
        for (final String locale : allLocales) {
            final Set<String> localeKeys = new TreeSet<>(loadBundle(locale).keySet());
            final Set<String> missing = new TreeSet<>(chatKeys);
            missing.removeAll(localeKeys);
            assertTrue(missing.isEmpty(), "labels.chat_* keys missing from messages." + locale + ".json: " + missing);
        }
    }
}
