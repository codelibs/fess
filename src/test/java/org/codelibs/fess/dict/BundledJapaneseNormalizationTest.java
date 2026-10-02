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
package org.codelibs.fess.dict;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.charfilter.MappingCharFilter;
import org.apache.lucene.analysis.charfilter.NormalizeCharMap;
import org.apache.lucene.analysis.pattern.PatternReplaceCharFilter;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Covers the Japanese spelling normalization that the bundled document index ships with: the
 * character mappings of {@code mapping.txt} (title, content and important_content) and
 * {@code ja/mapping.txt} (the {@code *_ja} fields), their inline copies in the {@code _aws} and
 * {@code _cloud} index settings, and the {@code prolonged_sound_mark_filter} that turns dash-like
 * characters written after kana into a long vowel mark. The mappings are applied with Lucene's own
 * {@link MappingCharFilter} and the long vowel pattern with {@link PatternReplaceCharFilter}, the
 * classes the search engine runs for those char filters.
 */
public class BundledJapaneseNormalizationTest extends UnitFessTestCase {

    private static final String ROOT_MAPPING = "fess_indices/fess/mapping.txt";

    private static final String JA_MAPPING = "fess_indices/fess/ja/mapping.txt";

    private static final String[] SETTINGS_JSON_PATHS =
            { "fess_indices/fess.json", "fess_indices/_aws/fess.json", "fess_indices/_cloud/fess.json" };

    private static final String[] INLINE_SETTINGS_JSON_PATHS = { "fess_indices/_aws/fess.json", "fess_indices/_cloud/fess.json" };

    private static final String PROLONGED_SOUND_MARK_FILTER = "prolonged_sound_mark_filter";

    private static final Pattern RULE_PATTERN = Pattern.compile("(.*)\\s*=>\\s*(.*)\\s*$", Pattern.DOTALL);

    private static final char COMBINING_VOICED_MARK = '\u3099';

    private static final String SMALL_KATAKANA = "ァィゥェォッャュョヮヵヶ";

    @Test
    public void test_rootMapping_foldsEveryKanaToFullSizeKatakana() throws IOException {
        final NormalizeCharMap map = toCharMap(readRules(ROOT_MAPPING));
        for (final String input : kanaInputs()) {
            final String output = apply(map, input);
            for (int i = 0; i < output.length(); i++) {
                final char c = output.charAt(i);
                final String message = "input=" + input + " (" + toCodePoints(input) + "), output=" + output;
                assertFalse(message, c >= 'ぁ' && c <= 'ゖ' || c == 'ゝ' || c == 'ゞ');
                assertFalse(message, SMALL_KATAKANA.indexOf(c) >= 0 || c >= 'ㇰ' && c <= 'ㇿ');
                assertFalse(message, c == 'ヴ' || c == COMBINING_VOICED_MARK);
            }
        }
    }

    @Test
    public void test_rootMapping_unifiesSpellingsOfTheSameWord() throws IOException {
        final NormalizeCharMap map = toCharMap(readRules(ROOT_MAPPING));
        assertSameOutput(map, "リンゴ", "りんご", "ﾘﾝｺﾞ");
        assertSameOutput(map, "キツテ", "きって", "キッテ", "ｷｯﾃ");
        assertSameOutput(map, "イル", "ゐる", "ヰル", "いる");
        assertSameOutput(map, "エ", "ゑ", "ヱ", "え");
        assertSameOutput(map, "バイオリン", "ヴァイオリン", "ゔぁいおりん", "ｳﾞｧｲｵﾘﾝ", "ウ\u3099ァイオリン", "う\u3099ぁいおりん");
        assertSameOutput(map, "レビユー", "レヴュー", "ﾚｳﾞｭｰ", "れゔゅー", "レビュー");
        assertSameOutput(map, "ラブ", "ラヴ", "ﾗｳﾞ", "らゔ");
        assertSameOutput(map, "ケ", "ヶ", "ゖ");
        assertSameOutput(map, "カ", "ヵ", "ゕ");
        assertSameOutput(map, "ワ", "ヮ", "ゎ");
        assertSameOutput(map, "コヽロ", "こゝろ");
        final String[] expected = { "バ", "ビ", "ブ", "ベ", "ボ", "ビヤ", "ビユ", "ビヨ" };
        final String[][] spellings =
                { { "ヴ", "ァィゥェォャュョ" }, { "ウ\u3099", "ァィゥェォャュョ" }, { "ゔ", "ぁぃぅぇぉゃゅょ" }, { "う\u3099", "ぁぃぅぇぉゃゅょ" }, { "ｳﾞ", "ｧｨｩｪｫｬｭｮ" } };
        for (final String[] spelling : spellings) {
            for (int i = 0; i < expected.length; i++) {
                final String input = spelling[0] + spelling[1].charAt(i);
                assertEquals(input + " (" + toCodePoints(input) + ")", expected[i], apply(map, input));
            }
        }
    }

    @Test
    public void test_jaMapping_keepsKanaForTheMorphologicalAnalyzer() throws IOException {
        final NormalizeCharMap map = toCharMap(readRules(JA_MAPPING));
        // the morphological analyzer needs hiragana and small kana as they are written
        assertEquals("りんごときって", apply(map, "りんごときって"));
        assertEquals("キャッシュ", apply(map, "ｷｬｯｼｭ"));
        assertEquals("いる", apply(map, "ゐる"));

        assertSameOutput(map, "バイオリン", "ヴァイオリン", "ｳﾞｧｲｵﾘﾝ", "ウ\u3099ァイオリン");
        assertSameOutput(map, "ばいおりん", "ゔぁいおりん", "う\u3099ぁいおりん");
        assertSameOutput(map, "レビュー", "レヴュー", "ﾚｳﾞｭｰ", "レウ\u3099ュー");
        assertSameOutput(map, "ラブ", "ラヴ", "ﾗｳﾞ");
        for (final String input : kanaInputs()) {
            final String output = apply(map, input);
            assertFalse("input=" + input + ", output=" + output,
                    output.indexOf('ヴ') >= 0 || output.indexOf('ゔ') >= 0 || output.indexOf(COMBINING_VOICED_MARK) >= 0);
        }
    }

    @Test
    public void test_inlineMappings_matchDictionaryFiles() throws IOException {
        final Map<String, String> rootRules = kanaRules(readRules(ROOT_MAPPING));
        final Map<String, String> jaRules = kanaRules(readRules(JA_MAPPING));
        for (final String path : INLINE_SETTINGS_JSON_PATHS) {
            final JsonNode charFilters = readJson(path).path("settings").path("analysis").path("char_filter");
            assertEquals(path, rootRules, kanaRules(inlineRules(charFilters.path("mapping_filter").path("mappings"))));
            assertEquals(path, jaRules, kanaRules(inlineRules(charFilters.path("mapping_ja_filter").path("mappings"))));
        }
    }

    @Test
    public void test_prolongedSoundMarkFilter_isTheFirstCharFilterOfTheStandardAnalyzers() throws IOException {
        for (final String path : SETTINGS_JSON_PATHS) {
            final JsonNode analysis = readJson(path).path("settings").path("analysis");
            final JsonNode filter = analysis.path("char_filter").path(PROLONGED_SOUND_MARK_FILTER);
            assertEquals(path, "pattern_replace", filter.path("type").asText());
            assertEquals(path, "ー", filter.path("replacement").asText());
            for (final String analyzer : new String[] { "standard_analyzer", "standard_search_analyzer" }) {
                // it has to see the raw text: mapping.txt turns a full-width hyphen-minus into "-",
                // which the filter leaves alone
                assertEquals(path + " " + analyzer, PROLONGED_SOUND_MARK_FILTER,
                        analysis.path("analyzer").path(analyzer).path("char_filter").path(0).asText());
            }
            // a dash between two katakana words would join them into one unknown word for the
            // morphological analyzer, so the *_ja fields do not use it
            for (final JsonNode charFilter : analysis.path("analyzer").path("japanese_analyzer").path("char_filter")) {
                assertFalse(path, PROLONGED_SOUND_MARK_FILTER.equals(charFilter.asText()));
            }
        }
    }

    @Test
    public void test_prolongedSoundMarkFilter_replacesDashesAfterKana() throws IOException {
        for (final String path : SETTINGS_JSON_PATHS) {
            final Pattern pattern = Pattern.compile(readJson(path).path("settings")
                    .path("analysis")
                    .path("char_filter")
                    .path(PROLONGED_SOUND_MARK_FILTER)
                    .path("pattern")
                    .asText());
            assertEquals("サーバー", replace(pattern, "サ―バ－"));
            assertEquals("サーバー", replace(pattern, "サ‐バ−"));
            assertEquals("サーバー", replace(pattern, "サ—バ–"));
            assertEquals("さーばー", replace(pattern, "さ―ば－"));
            assertEquals("ｻーﾊﾞー", replace(pattern, "ｻ―ﾊﾞ－"));
            assertEquals("サーービス", replace(pattern, "サ――ビス"));
            assertEquals("サーーーーーバ", replace(pattern, "サ‐‐‐‐‐バ"));
            assertEquals("ハ\u3099ー", replace(pattern, "ハ\u3099―"));
            assertEquals("ㇰー", replace(pattern, "ㇰ―"));
            assertEquals("テストーケース", replace(pattern, "テスト－ケース"));
            // an ASCII hyphen-minus separates words more often than it stands for a long vowel
            assertEquals("サ-バ-", replace(pattern, "サ-バ-"));
            assertEquals("テスト-ケース", replace(pattern, "テスト-ケース"));
            // only kana take a long vowel mark
            assertEquals("東京－大阪", replace(pattern, "東京－大阪"));
            assertEquals("A―B", replace(pattern, "A―B"));
            assertEquals("2026−10−02", replace(pattern, "2026−10−02"));
            assertEquals("ー", replace(pattern, "ー"));
            // a middle dot or a spacing voiced mark is not kana
            assertEquals("ジョン・−", replace(pattern, "ジョン・−"));
            assertEquals("゛―", replace(pattern, "゛―"));
        }
    }

    private void assertSameOutput(final NormalizeCharMap map, final String expected, final String... inputs) throws IOException {
        for (final String input : inputs) {
            assertEquals("input=" + input + " (" + toCodePoints(input) + ")", expected, apply(map, input));
        }
    }

    private static List<String> kanaInputs() {
        final List<String> inputs = new ArrayList<>();
        for (char c = 'ぁ'; c <= 'ゖ'; c++) {
            inputs.add(String.valueOf(c));
        }
        inputs.add("ゝ");
        inputs.add("ゞ");
        for (char c = 'ァ'; c <= 'ヺ'; c++) {
            inputs.add(String.valueOf(c));
        }
        for (char c = 'ㇰ'; c <= 'ㇿ'; c++) {
            inputs.add(String.valueOf(c));
        }
        for (char c = 'ｦ'; c <= 'ﾝ'; c++) {
            inputs.add(String.valueOf(c));
        }
        for (final char c : "ァィゥェォャュョ".toCharArray()) {
            inputs.add("ヴ" + c);
            inputs.add("ウ" + COMBINING_VOICED_MARK + c);
        }
        for (final char c : "ぁぃぅぇぉゃゅょ".toCharArray()) {
            inputs.add("ゔ" + c);
            inputs.add("う" + COMBINING_VOICED_MARK + c);
        }
        for (final char c : "ｧｨｩｪｫｬｭｮ".toCharArray()) {
            inputs.add("ｳﾞ" + c);
        }
        inputs.add("ウ" + COMBINING_VOICED_MARK);
        inputs.add("う" + COMBINING_VOICED_MARK);
        inputs.add("ｳﾞ");
        return inputs;
    }

    private static Map<String, String> kanaRules(final Map<String, String> rules) {
        final Map<String, String> result = new HashMap<>();
        rules.forEach((key, value) -> {
            if (key.chars().anyMatch(c -> c >= '぀' && c <= 'ㇿ' || c >= 'ｦ' && c <= 'ﾟ')) {
                result.put(key, value);
            }
        });
        return result;
    }

    private Map<String, String> readRules(final String path) throws IOException {
        final Map<String, String> rules = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getResourceAsStream(path), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                addRule(rules, line);
            }
        }
        return rules;
    }

    private Map<String, String> inlineRules(final JsonNode mappings) {
        final Map<String, String> rules = new LinkedHashMap<>();
        for (final JsonNode mapping : mappings) {
            addRule(rules, mapping.asText());
        }
        return rules;
    }

    private void addRule(final Map<String, String> rules, final String line) {
        // parsed as the mapping char filter does: one key per rule, trimmed before it is unescaped
        final Matcher matcher = RULE_PATTERN.matcher(line);
        assertTrue(line, matcher.matches());
        final String key = unescape(matcher.group(1).trim());
        // the search engine refuses an index whose mapping has the same key twice
        assertFalse("duplicate key: " + line, rules.containsKey(key));
        rules.put(key, unescape(matcher.group(2).trim()));
    }

    private static String unescape(final String s) {
        final StringBuilder buf = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            if (c == '\\' && i + 5 < s.length() && s.charAt(i + 1) == 'u') {
                buf.append((char) Integer.parseInt(s.substring(i + 2, i + 6), 16));
                i += 5;
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    private static NormalizeCharMap toCharMap(final Map<String, String> rules) {
        final NormalizeCharMap.Builder builder = new NormalizeCharMap.Builder();
        rules.forEach((key, value) -> {
            if (!key.isEmpty()) {
                builder.add(key, value);
            }
        });
        return builder.build();
    }

    private static String apply(final NormalizeCharMap map, final String input) throws IOException {
        return read(new MappingCharFilter(map, new StringReader(input)));
    }

    private static String replace(final Pattern pattern, final String input) throws IOException {
        return read(new PatternReplaceCharFilter(pattern, "ー", new StringReader(input)));
    }

    private static String read(final Reader reader) throws IOException {
        try (Reader r = reader) {
            final StringBuilder buf = new StringBuilder();
            final char[] chars = new char[256];
            int len;
            while ((len = r.read(chars)) != -1) {
                buf.append(chars, 0, len);
            }
            return buf.toString();
        }
    }

    private static String toCodePoints(final String s) {
        final StringBuilder buf = new StringBuilder();
        s.codePoints().forEach(c -> buf.append(String.format("U+%04X ", c)));
        return buf.toString().trim();
    }

    private InputStream getResourceAsStream(final String path) {
        final InputStream in = getClass().getClassLoader().getResourceAsStream(path);
        assertTrue(path, in != null);
        return in;
    }

    private JsonNode readJson(final String path) throws IOException {
        try (InputStream in = getResourceAsStream(path)) {
            return new ObjectMapper().readTree(in);
        }
    }
}
