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
package org.codelibs.fess.opensearch.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * Guards the index definitions that a search engine without the CodeLibs plugins ({@code vanilla},
 * {@code aws} and the deprecated {@code cloud}) is created from.
 *
 * <p>Those files are copies of the default ones with the plugin-dependent parts taken out. A copy
 * drifts: an analyzer added to the default file gets a plugin tokenizer, or somebody copies a
 * default file back over the vanilla one, and the index creation then fails on a plain OpenSearch
 * with an error that does not name the cause. Nothing else runs these files against an engine
 * without the plugins, so the check is made on the files themselves. They are read from the source
 * tree, not from the class path: a stale {@code target/classes} must not decide the outcome.</p>
 */
public class PluginlessIndexResourcesTest {

    private static final Path RESOURCES = Paths.get("src/main/resources");

    private static final List<String> VANILLA_FILES = List.of("fess_indices/_vanilla/fess.json", "fess_indices/_vanilla/fess/doc.json",
            "suggest_indices/_vanilla/suggest_analyzer.json");

    /** Types that only a CodeLibs plugin provides. */
    private static final Pattern PLUGIN_ONLY_TYPE =
            Pattern.compile("fess_.*|minhash|min_hash|alphanum_word|seunjeon.*|vietnamese_tokenizer|simplified_chinese_tokenizer");

    /** Names that only a CodeLibs plugin provides, wherever they appear. */
    private static final List<String> PLUGIN_ONLY_NAMES =
            List.of("alphanum_word", "seunjeon", "vietnamese_tokenizer", "minhash_analyzer", "minhash_filter");

    /** The prefix of the type names of the stock analysis plugins and the plugin each belongs to. */
    private static final Map<String, String> STOCK_PLUGIN_BY_PREFIX =
            Map.of("kuromoji", "analysis-kuromoji", "nori", "analysis-nori", "smartcn", "analysis-smartcn", "knn", "knn", "icu",
                    "analysis-icu", "phonetic", "analysis-phonetic", "stempel", "analysis-stempel", "ukrainian", "analysis-ukrainian");

    private static String read(final String resource) throws IOException {
        return new String(Files.readAllBytes(RESOURCES.resolve(resource)), StandardCharsets.UTF_8);
    }

    private static Map<String, Object> parse(final String resource) throws IOException {
        return new ObjectMapper().readValue(read(resource), new TypeReference<Map<String, Object>>() {
        });
    }

    @SuppressWarnings("unchecked")
    private static Set<String> analyzerNames(final String resource) throws IOException {
        final Map<String, Object> settings = (Map<String, Object>) parse(resource).get("settings");
        final Map<String, Object> analysis = (Map<String, Object>) settings.get("analysis");
        return new TreeSet<>(((Map<String, Object>) analysis.get("analyzer")).keySet());
    }

    /** Adds every textual value of a field called {@code type}, and of {@code tokenizer} when asked, at any depth. */
    @SuppressWarnings("unchecked")
    private static void collectTypes(final Object node, final boolean withTokenizers, final Set<String> types) {
        if (node instanceof final Map<?, ?> map) {
            for (final Map.Entry<String, Object> entry : ((Map<String, Object>) map).entrySet()) {
                if (("type".equals(entry.getKey()) || withTokenizers && "tokenizer".equals(entry.getKey()))
                        && entry.getValue() instanceof final String value) {
                    types.add(value);
                } else {
                    collectTypes(entry.getValue(), withTokenizers, types);
                }
            }
        } else if (node instanceof final List<?> list) {
            for (final Object element : list) {
                collectTypes(element, withTokenizers, types);
            }
        }
    }

    /**
     * The types a file uses. A {@code tokenizer} value is either a built-in tokenizer or the name of
     * one defined in the same file, so it counts only where the plugin behind a built-in one is wanted.
     */
    private static Set<String> typesOf(final String resource, final boolean withTokenizers) throws IOException {
        final Set<String> types = new TreeSet<>();
        collectTypes(parse(resource), withTokenizers, types);
        return types;
    }

    @Test
    public void test_vanillaDefinitionsAreValidJson() throws IOException {
        for (final String resource : VANILLA_FILES) {
            assertFalse(parse(resource).isEmpty(), resource);
        }
    }

    @Test
    public void test_vanillaDefinitionsUseNoPluginOnlyTypeOrName() throws IOException {
        for (final String resource : VANILLA_FILES) {
            for (final String type : typesOf(resource, false)) {
                assertFalse(PLUGIN_ONLY_TYPE.matcher(type).matches(), resource + " uses the plugin-only type " + type);
            }
            final String source = read(resource);
            for (final String name : PLUGIN_ONLY_NAMES) {
                assertFalse(source.contains(name), resource + " mentions " + name);
            }
            // whatever the spacing of the file
            assertFalse(Pattern.compile("\"type\"\\s*:\\s*\"fess_").matcher(source).find(),
                    resource + " uses a type of the fess-analysis plugin");
        }
    }

    @Test
    public void test_vanillaDefinitionsOnlyNeedThePluginsThatAreChecked() throws IOException {
        final Set<String> needed = new TreeSet<>();
        for (final String resource : VANILLA_FILES) {
            for (final String type : typesOf(resource, true)) {
                final String prefix = type.contains("_") ? type.substring(0, type.indexOf('_')) : type;
                final String plugin = STOCK_PLUGIN_BY_PREFIX.get(prefix);
                if (plugin != null) {
                    assertTrue(SearchEngineClient.REQUIRED_ENGINE_PLUGINS.contains(plugin),
                            resource + " uses " + type + " of " + plugin + ", which the startup check does not ask for");
                    needed.add(plugin);
                }
            }
        }
        // the scan must find the plugins, or it checks nothing
        assertEquals(new TreeSet<>(SearchEngineClient.REQUIRED_ENGINE_PLUGINS), needed);
    }

    @Test
    public void test_vanillaAnalyzersAreTheDefaultOnesWithoutTheThreeThatNeedPlugins() throws IOException {
        final Set<String> defaultOnly = analyzerNames("fess_indices/fess.json");
        final Set<String> vanilla = analyzerNames("fess_indices/_vanilla/fess.json");

        defaultOnly.removeAll(vanilla);
        // A new language added to the default file must be added to the vanilla file too (see
        // ADDING_NEW_LANGUAGE.md); only the analyzers below depend on a plugin that is not stock.
        assertEquals(Set.of("minhash_analyzer", "traditional_chinese_analyzer", "vietnamese_analyzer"), defaultOnly);
        final Set<String> vanillaOnly = analyzerNames("fess_indices/_vanilla/fess.json");
        vanillaOnly.removeAll(analyzerNames("fess_indices/fess.json"));
        assertEquals(Set.of(), vanillaOnly);
    }

    @Test
    public void test_removedVariantDirectoriesAreNotShipped() {
        // _aws and _cloud are read as _vanilla now; a copy under the old names would be dead weight
        // that the next edit of the vanilla file forgets
        for (final String dir : new String[] { "fess_indices/_aws", "fess_indices/_cloud", "suggest_indices/_aws",
                "suggest_indices/_cloud" }) {
            assertFalse(Files.exists(RESOURCES.resolve(dir)), dir);
        }
        assertTrue(Files.isDirectory(RESOURCES.resolve("fess_indices/_vanilla")));
        assertTrue(Files.isDirectory(RESOURCES.resolve("suggest_indices/_vanilla")));
    }
}
