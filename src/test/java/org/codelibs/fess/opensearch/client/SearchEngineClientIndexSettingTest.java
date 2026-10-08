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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.codelibs.fesen.opensearch.cluster.metadata.MappingMetadata;
import org.codelibs.fess.helper.ChunkVectorHelper;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Covers {@code readIndexSetting}'s document-index guard -- document settings rewrite rules
 * (registered via {@code addDocumentSettingRewriteRule}, kept for backward compatibility even
 * though {@code ChunkVectorHelper} no longer registers one itself now that the chunk vector
 * codec/knn settings are defined statically) must only be applied to the document index, exactly
 * like {@code addMapping} already does for the mapping rules. Also covers the static index
 * definitions themselves (JSON validity, cross-variant shape, k-NN settings/mapping content) and
 * {@code substitutePlaceholders}, including the {@code content_chunker.search.knn.*} placeholders
 * that keep the shipped mapping's ANN {@code method} block in sync with
 * {@code ChunkVectorHelper}'s query-time score-scale conversion, and that an unset, blank, or
 * out-of-set operator value for any of {@code dimension}/{@code method}/{@code engine}/
 * {@code space_type} always falls back to the documented default (with a WARN) rather than
 * splicing an unvalidated token into the shipped mapping. Also covers
 * {@code addMissingProperties}, which adds the fields of a bundled non-document mapping that an
 * existing index does not have yet.
 */
public class SearchEngineClientIndexSettingTest extends UnitFessTestCase {

    private static final String DOC_INDEX_CONFIG = "fess_indices/fess.json";

    private static final String CONFIG_INDEX_CONFIG = "fess_indices/fess_config.web_config.json";

    private static final String SEARCH_LOG_MAPPING = "fess_indices/fess_log.search_log/search_log.json";

    private static final String[] SETTINGS_JSON_PATHS =
            { "fess_indices/fess.json", "fess_indices/_aws/fess.json", "fess_indices/_cloud/fess.json" };

    private static final String[] DOC_JSON_PATHS =
            { "fess_indices/fess/doc.json", "fess_indices/_aws/fess/doc.json", "fess_indices/_cloud/fess/doc.json" };

    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    @Override
    public void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        // substitutePlaceholders delegates dimension/method/engine/space_type validation to
        // ChunkVectorHelper (CRITICAL 1 fix); the test container does not auto-register it (unlike
        // production's fess_content_chunk.xml), so every test needs a real instance registered,
        // mirroring SemanticChunkSearcherTest's setup for the same component.
        ComponentUtil.register(new ChunkVectorHelper(), ChunkVectorHelper.class.getCanonicalName());
    }

    @Test
    public void test_readIndexSetting_appliesRewriteOnlyToDocIndex() {
        final SearchEngineClient client = new SearchEngineClient();
        client.addDocumentSettingRewriteRule(source -> source + "/*REWRITE_MARKER*/");

        final String docSource = client.readIndexSetting("fess", "opensearch", DOC_INDEX_CONFIG, "5", "0-1");
        assertTrue(docSource.contains("/*REWRITE_MARKER*/"), "the document index must still receive the rewrite rules");

        // fess_indices holds 34 top-level settings files; applying the document rules to the other
        // 33 makes every anchor-miss warning point at fess.json, an unrelated file
        final String configSource = client.readIndexSetting("fess_config.web_config", "opensearch", CONFIG_INDEX_CONFIG, "5", "0-1");
        assertFalse(configSource.contains("/*REWRITE_MARKER*/"), "a non-document index must not receive the document rewrite rules");
    }

    @Test
    public void test_readIndexSetting_stillSubstitutesPlaceholders() {
        final SearchEngineClient client = new SearchEngineClient();
        // placeholder substitution is index-independent and must survive the guard
        final String docSource = client.readIndexSetting("fess", "opensearch", DOC_INDEX_CONFIG, "7", "0-2").replaceAll("\\s", "");
        assertTrue(docSource.contains("\"number_of_shards\":\"7\""), docSource);
        assertTrue(docSource.contains("\"auto_expand_replicas\":\"0-2\""), docSource);
        assertFalse(docSource.contains("${fess.index."), docSource);
        final String configSource = client.readIndexSetting("fess_config.web_config", "opensearch", CONFIG_INDEX_CONFIG, "7", "0-2");
        assertFalse(configSource.contains("${fess.index."), configSource);
    }

    @Test
    public void test_substitutePlaceholders_dimensionFromSystemProperty() {
        ComponentUtil.getFessConfig().setSystemProperty("content_chunker.embedding.dimension", "1024");
        final SearchEngineClient client = new SearchEngineClient();
        final String source = "{\"vector\":{\"type\":\"knn_vector\",\"dimension\":\"${fess.content_chunker.embedding.dimension}\"}}";

        final String actual = client.substitutePlaceholders(source, "5", "0-1");

        assertEquals("{\"vector\":{\"type\":\"knn_vector\",\"dimension\":\"1024\"}}", actual);
    }

    @Test
    public void test_substitutePlaceholders_dimensionFallsBackTo768() {
        final SearchEngineClient client = new SearchEngineClient();
        final String source = "{\"dimension\":\"${fess.content_chunker.embedding.dimension}\"}";

        final String actual = client.substitutePlaceholders(source, "5", "0-1");

        assertEquals("{\"dimension\":\"768\"}", actual);
    }

    @Test
    public void test_substitutePlaceholders_dimensionPresentButEmpty_fallsBackTo768WithWarn() {
        // CRITICAL 1 regression test: FessProp#getSystemProperty's default only fires when the key
        // is ABSENT (java.util.Properties#getProperty semantics) -- a PRESENT but empty value (an
        // operator clearing the property rather than deleting it) must still be rejected, not
        // spliced into the mapping as "dimension": "", which would 400 preparePutMapping and leave
        // the index with no proper mapping.
        ComponentUtil.getFessConfig().setSystemProperty("content_chunker.embedding.dimension", "");
        final SearchEngineClient client = new SearchEngineClient();
        final String source = "{\"dimension\":\"${fess.content_chunker.embedding.dimension}\"}";
        final LogCapturingAppender capture = LogCapturingAppender.attach(ChunkVectorHelper.class);

        try {
            final String actual = client.substitutePlaceholders(source, "5", "0-1");

            assertEquals("{\"dimension\":\"768\"}", actual);
            assertTrue(capture.warnings().stream().anyMatch(m -> m.contains("content_chunker.embedding.dimension")),
                    "a present-but-empty dimension must be WARNed, not silently accepted: " + capture.warnings());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_substitutePlaceholders_dimensionWithRegexMetacharacters_fallsBackSafely() {
        // A value containing $ or \ would corrupt (or throw from) a bare String#replaceAll
        // replacement without Matcher.quoteReplacement. Validation closes this: a value that fails
        // the positive-integer check is replaced by the literal default before it ever reaches
        // replaceAll, so it can never carry a regex metacharacter.
        ComponentUtil.getFessConfig().setSystemProperty("content_chunker.embedding.dimension", "$1\\2");
        final SearchEngineClient client = new SearchEngineClient();
        final String source = "{\"dimension\":\"${fess.content_chunker.embedding.dimension}\"}";

        final String actual = client.substitutePlaceholders(source, "5", "0-1");

        assertEquals("{\"dimension\":\"768\"}", actual);
    }

    @Test
    public void test_substitutePlaceholders_knnEngineSpaceTypeFromSystemProperty() {
        // space_type feeds BOTH the static mapping (via this substitution) and
        // ChunkVectorHelper#getKnnSpaceType's query-time score-scale conversion --
        // it must resolve from the same system property so the two sides cannot diverge.
        // method is NOT exercised here with a non-default override: "hnsw" is currently the only
        // value getKnnMethod() accepts (doc.json's method.parameters block is hardcoded to hnsw's
        // {m, ef_construction}; any other method 400s regardless of engine -- see
        // test_substitutePlaceholders_methodOutOfSet_fallsBackToHnswWithWarn), so there is no valid
        // non-default value left to demonstrate here.
        ComponentUtil.getFessConfig().setSystemProperty("content_chunker.search.knn.engine", "faiss");
        ComponentUtil.getFessConfig().setSystemProperty("content_chunker.search.knn.space_type", "l2");
        final SearchEngineClient client = new SearchEngineClient();
        final String source = "{\"method\":{\"name\":\"${fess.content_chunker.search.knn.method}\","
                + "\"engine\":\"${fess.content_chunker.search.knn.engine}\","
                + "\"space_type\":\"${fess.content_chunker.search.knn.space_type}\"}}";

        final String actual = client.substitutePlaceholders(source, "5", "0-1");

        assertEquals("{\"method\":{\"name\":\"hnsw\",\"engine\":\"faiss\",\"space_type\":\"l2\"}}", actual);
    }

    @Test
    public void test_substitutePlaceholders_methodOutOfSet_fallsBackToHnswWithWarn() {
        // R1 regression test: doc.json's method.parameters block is hardcoded to {m, ef_construction}
        // (hnsw-only -- ivf's parameter is nlist), so any method other than hnsw would 400
        // preparePutMapping regardless of engine. "ivf" was accepted by the allow-set before this fix
        // even though it could never actually succeed.
        ComponentUtil.getFessConfig().setSystemProperty("content_chunker.search.knn.method", "ivf");
        final SearchEngineClient client = new SearchEngineClient();
        final String source = "{\"method\":\"${fess.content_chunker.search.knn.method}\"}";
        final LogCapturingAppender capture = LogCapturingAppender.attach(ChunkVectorHelper.class);

        try {
            final String actual = client.substitutePlaceholders(source, "5", "0-1");

            assertEquals("{\"method\":\"hnsw\"}", actual);
            assertTrue(capture.warnings().stream().anyMatch(m -> m.contains("content_chunker.search.knn.method")),
                    "an out-of-set method must be WARNed, not silently accepted: " + capture.warnings());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_substitutePlaceholders_spaceTypeOutOfSet_fallsBackToCosinesimilWithWarn() {
        // R1 regression test: l1/linf are not in either Lucene's or Faiss's HNSW SUPPORTED_SPACES at
        // all, and hamming requires a binary data_type this float knn_vector field never sets -- all
        // three were accepted by the allow-set before this fix even though none could ever succeed.
        ComponentUtil.getFessConfig().setSystemProperty("content_chunker.search.knn.space_type", "hamming");
        final SearchEngineClient client = new SearchEngineClient();
        final String source = "{\"space_type\":\"${fess.content_chunker.search.knn.space_type}\"}";
        final LogCapturingAppender capture = LogCapturingAppender.attach(ChunkVectorHelper.class);

        try {
            final String actual = client.substitutePlaceholders(source, "5", "0-1");

            assertEquals("{\"space_type\":\"cosinesimil\"}", actual);
            assertTrue(capture.warnings().stream().anyMatch(m -> m.contains("content_chunker.search.knn.space_type")),
                    "an out-of-set space_type must be WARNed, not silently accepted: " + capture.warnings());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_substitutePlaceholders_dimensionAboveMax_fallsBackTo768WithWarn() {
        // R1 regression test: the k-NN plugin caps dimension at 16000 for every engine
        // (KNNEngine.MAX_DIMENSIONS_BY_ENGINE); a positive integer above that cap was accepted by the
        // old positive-integer-only check even though it could never actually succeed.
        ComponentUtil.getFessConfig().setSystemProperty("content_chunker.embedding.dimension", "20000");
        final SearchEngineClient client = new SearchEngineClient();
        final String source = "{\"dimension\":\"${fess.content_chunker.embedding.dimension}\"}";
        final LogCapturingAppender capture = LogCapturingAppender.attach(ChunkVectorHelper.class);

        try {
            final String actual = client.substitutePlaceholders(source, "5", "0-1");

            assertEquals("{\"dimension\":\"768\"}", actual);
            assertTrue(capture.warnings().stream().anyMatch(m -> m.contains("content_chunker.embedding.dimension")),
                    "a dimension above the k-NN plugin's own cap must be WARNed, not silently accepted: " + capture.warnings());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_substitutePlaceholders_knnMethodEngineSpaceTypeFallBackToDefaults() {
        final SearchEngineClient client = new SearchEngineClient();
        final String source = "{\"method\":{\"name\":\"${fess.content_chunker.search.knn.method}\","
                + "\"engine\":\"${fess.content_chunker.search.knn.engine}\","
                + "\"space_type\":\"${fess.content_chunker.search.knn.space_type}\"}}";

        final String actual = client.substitutePlaceholders(source, "5", "0-1");

        assertEquals("{\"method\":{\"name\":\"hnsw\",\"engine\":\"lucene\",\"space_type\":\"cosinesimil\"}}", actual);
    }

    @Test
    public void test_substitutePlaceholders_engineOutOfSet_fallsBackToLuceneWithWarn() {
        // CRITICAL 1 regression test: an out-of-set (typo'd, or otherwise invalid) engine must fall
        // back to the documented default with a WARN, never reach the mapping as an unaccepted
        // token (which would 400 preparePutMapping the same way an empty dimension does).
        ComponentUtil.getFessConfig().setSystemProperty("content_chunker.search.knn.engine", "not-a-real-engine");
        final SearchEngineClient client = new SearchEngineClient();
        final String source = "{\"engine\":\"${fess.content_chunker.search.knn.engine}\"}";
        final LogCapturingAppender capture = LogCapturingAppender.attach(ChunkVectorHelper.class);

        try {
            final String actual = client.substitutePlaceholders(source, "5", "0-1");

            assertEquals("{\"engine\":\"lucene\"}", actual);
            assertTrue(capture.warnings().stream().anyMatch(m -> m.contains("content_chunker.search.knn.engine")),
                    "an out-of-set engine must be WARNed, not silently accepted: " + capture.warnings());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_substitutePlaceholders_repeatedInvalidValue_warnsOnlyOnce() {
        // WARN-amplification guard: getKnnConfigToken/getKnnConfigPositiveInt are also reached from
        // the query path (SemanticChunkSearcher#resolveEngineMinScore) on every ann-mode search
        // request with no caching of their own, so the same invalid key=value combination must not
        // re-WARN on every call -- otherwise tightening the allow-sets above would make a single
        // stale misconfiguration far noisier than before.
        ComponentUtil.getFessConfig().setSystemProperty("content_chunker.search.knn.engine", "not-a-real-engine");
        final SearchEngineClient client = new SearchEngineClient();
        final String source = "{\"engine\":\"${fess.content_chunker.search.knn.engine}\"}";
        final LogCapturingAppender capture = LogCapturingAppender.attach(ChunkVectorHelper.class);

        try {
            client.substitutePlaceholders(source, "5", "0-1");
            client.substitutePlaceholders(source, "5", "0-1");
            client.substitutePlaceholders(source, "5", "0-1");

            final long matchingWarnings = capture.warnings().stream().filter(m -> m.contains("content_chunker.search.knn.engine")).count();
            assertEquals(1, (int) matchingWarnings, "the same invalid value must WARN only once: " + capture.warnings());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_substitutePlaceholders_changedInvalidValueAndOtherKeys_warnSeparately() {
        // The guard is keyed on key=value: a different bad value for the same key is a new
        // misconfiguration and is reported, and another key is independent of both.
        final SearchEngineClient client = new SearchEngineClient();
        final String engineSource = "{\"engine\":\"${fess.content_chunker.search.knn.engine}\"}";
        final String dimensionSource = "{\"dimension\":\"${fess.content_chunker.embedding.dimension}\"}";
        final LogCapturingAppender capture = LogCapturingAppender.attach(ChunkVectorHelper.class);

        try {
            ComponentUtil.getFessConfig().setSystemProperty("content_chunker.search.knn.engine", "not-a-real-engine");
            client.substitutePlaceholders(engineSource, "5", "0-1");
            client.substitutePlaceholders(engineSource, "5", "0-1");
            ComponentUtil.getFessConfig().setSystemProperty("content_chunker.search.knn.engine", "another-bad-engine");
            client.substitutePlaceholders(engineSource, "5", "0-1");
            client.substitutePlaceholders(engineSource, "5", "0-1");
            ComponentUtil.getFessConfig().setSystemProperty("content_chunker.embedding.dimension", "not-a-number");
            client.substitutePlaceholders(dimensionSource, "5", "0-1");
            client.substitutePlaceholders(dimensionSource, "5", "0-1");

            final List<String> engineWarnings =
                    capture.warnings().stream().filter(m -> m.contains("content_chunker.search.knn.engine")).toList();
            assertEquals(2, engineWarnings.size(), capture.warnings().toString());
            assertTrue(engineWarnings.get(0).contains("not-a-real-engine"), engineWarnings.get(0));
            assertTrue(engineWarnings.get(1).contains("another-bad-engine"), engineWarnings.get(1));
            final List<String> dimensionWarnings =
                    capture.warnings().stream().filter(m -> m.contains("content_chunker.embedding.dimension")).toList();
            assertEquals(1, dimensionWarnings.size(), capture.warnings().toString());
            assertTrue(dimensionWarnings.get(0).contains("not-a-number"), dimensionWarnings.get(0));
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_addMapping_substitutesDimension() {
        ComponentUtil.getFessConfig().setSystemProperty("content_chunker.embedding.dimension", "384");
        final SearchEngineClient client = new SearchEngineClient();

        final String actual = client.substitutePlaceholders("{\"dimension\":\"${fess.content_chunker.embedding.dimension}\"}",
                ComponentUtil.getFessConfig().getIndexNumberOfShards(), ComponentUtil.getFessConfig().getIndexAutoExpandReplicas());

        assertEquals("{\"dimension\":\"384\"}", actual);
    }

    @Test
    public void test_getMissingProperties_returnsOnlyAbsentNamesWithBundledDefinitions() {
        final SearchEngineClient client = new SearchEngineClient();
        final Map<String, Object> existing = Map.of("user", Map.of("type", "text"));
        final Map<String, Object> bundled = new LinkedHashMap<>();
        bundled.put("user", Map.of("type", "keyword"));
        bundled.put("virtualHost", Map.of("type", "keyword"));
        bundled.put("hitCount", Map.of("type", "long"));

        final Map<String, Object> missing = client.getMissingProperties(existing, bundled);

        assertEquals(List.of("virtualHost", "hitCount"), new ArrayList<>(missing.keySet()));
        assertEquals(Map.of("type", "keyword"), missing.get("virtualHost"));
        assertEquals(Map.of("type", "long"), missing.get("hitCount"));
    }

    @Test
    public void test_getMissingProperties_emptyWhenNothingIsMissing() {
        final SearchEngineClient client = new SearchEngineClient();
        final Map<String, Object> properties = Map.of("user", Map.of("type", "keyword"));

        assertTrue(client.getMissingProperties(properties, properties).isEmpty());
        assertTrue(client.getMissingProperties(properties, Map.of()).isEmpty());
    }

    @Test
    public void test_addMissingProperties_putsOnlyMissingFields() throws Exception {
        final PutRecordingClient client = new PutRecordingClient();
        final Map<String, Object> existing = readBundledProperties(SEARCH_LOG_MAPPING);
        existing.remove("virtualHost");
        existing.remove("languages");
        // an existing field whose definition differs from the bundled one must be left alone
        existing.put("user", Map.of("type", "text"));

        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            client.addMissingProperties("fess_log.search_log", "search_log", "fess_log.search_log", toMappingMetadata(existing));

            assertEquals(1, client.putSources.size());
            assertEquals("fess_log.search_log", client.putIndexNames.get(0));
            final Map<String, Object> put =
                    new ObjectMapper().readValue(client.putSources.get(0), new TypeReference<Map<String, Object>>() {
                    });
            assertEquals(Map.of("properties", Map.of("virtualHost", Map.of("type", "keyword"), "languages", Map.of("type", "keyword"))),
                    put);
            final List<String> infos = capture.messagesAt(Level.INFO);
            assertEquals(1, infos.size(), infos.toString());
            assertTrue(infos.get(0).contains("fess_log.search_log"), infos.get(0));
            assertTrue(infos.get(0).contains("virtualHost"), infos.get(0));
            assertTrue(infos.get(0).contains("languages"), infos.get(0));
            assertTrue(capture.warnings().isEmpty(), capture.warnings().toString());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_addMissingProperties_noPutWhenNothingIsMissing() throws Exception {
        final PutRecordingClient client = new PutRecordingClient();
        final Map<String, Object> existing = readBundledProperties(SEARCH_LOG_MAPPING);

        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            client.addMissingProperties("fess_log.search_log", "search_log", "fess_log.search_log", toMappingMetadata(existing));

            assertTrue(client.putSources.isEmpty(), client.putSources.toString());
            assertTrue(capture.messagesAt(Level.INFO).isEmpty(), capture.messagesAt(Level.INFO).toString());
            assertTrue(capture.warnings().isEmpty(), capture.warnings().toString());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_addMissingProperties_documentIndexGetsOnlyTheTagField() {
        // The document index is upgraded by a reindex, so none of its other bundled fields are
        // added; the tag field is the one exception, because the first tag write would otherwise
        // map it dynamically as text and break the terms facet and term filters on it.
        final PutRecordingClient client = new PutRecordingClient();

        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            client.addMissingProperties("fess", "doc", "fess.20260101000000000",
                    toMappingMetadata(Map.of("url", Map.of("type", "keyword"))));

            assertEquals(List.of("fess.20260101000000000"), client.putIndexNames);
            assertEquals(List.of("{\"properties\":{\"tag\":{\"type\":\"keyword\"}}}"), client.putSources);
            final List<String> infos = capture.messagesAt(Level.INFO);
            assertEquals(1, infos.size(), infos.toString());
            assertTrue(infos.get(0).contains("fess.20260101000000000"), infos.get(0));
            assertTrue(capture.warnings().isEmpty(), capture.warnings().toString());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_addMissingProperties_documentIndexWithKeywordTagIsLeftAlone() {
        final PutRecordingClient client = new PutRecordingClient();

        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            client.addMissingProperties("fess", "doc", "fess.20260101000000000",
                    toMappingMetadata(Map.of("url", Map.of("type", "keyword"), "tag", Map.of("type", "keyword"))));

            assertTrue(client.putSources.isEmpty(), client.putSources.toString());
            assertTrue(capture.messagesAt(Level.INFO).isEmpty(), capture.messagesAt(Level.INFO).toString());
            assertTrue(capture.warnings().isEmpty(), capture.warnings().toString());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_addMissingProperties_documentIndexWithOtherTagTypeWarnsAndIsLeftAlone() {
        // A tag field mapped dynamically before the upgrade cannot be changed in place; it is
        // reported so that the operator reindexes, and never overwritten.
        final PutRecordingClient client = new PutRecordingClient();

        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            client.addMissingProperties("fess", "doc", "fess.20260101000000000",
                    toMappingMetadata(Map.of("tag", Map.of("type", "text", "fields", Map.of("keyword", Map.of("type", "keyword"))))));

            assertTrue(client.putSources.isEmpty(), client.putSources.toString());
            final List<String> warnings = capture.warnings();
            assertEquals(1, warnings.size(), warnings.toString());
            assertTrue(warnings.get(0).contains("fess.20260101000000000"), warnings.get(0));
            assertTrue(warnings.get(0).contains("text"), warnings.get(0));
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_addMissingProperties_documentIndexTagSkippedInJobProcesses() {
        final String[] processProperties =
                { "fess.crawler.process", "fess.thumbnail.process", "fess.suggest.process", "fess.chunk.process" };
        for (final String property : processProperties) {
            final PutRecordingClient client = new PutRecordingClient();
            System.setProperty(property, "true");
            try {
                client.addMissingProperties("fess", "doc", "fess.20260101000000000",
                        toMappingMetadata(Map.of("url", Map.of("type", "keyword"))));

                assertTrue(client.putSources.isEmpty(), property + ": " + client.putSources);
            } finally {
                System.clearProperty(property);
            }
        }
    }

    @Test
    public void test_addMissingProperties_documentIndexTagPutFailureLogsWarnAndDoesNotThrow() {
        final PutRecordingClient client = new PutRecordingClient();
        client.putFailure = new IllegalStateException("put failed");

        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            client.addMissingProperties("fess", "doc", "fess.20260101000000000",
                    toMappingMetadata(Map.of("url", Map.of("type", "keyword"))));

            assertEquals(1, client.putSources.size());
            assertEquals(1, capture.warnings().size(), capture.warnings().toString());
            assertTrue(capture.warnings().get(0).contains("fess.20260101000000000"), capture.warnings().get(0));
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_addMissingProperties_putFailureLogsWarnAndDoesNotThrow() {
        final PutRecordingClient client = new PutRecordingClient();
        client.putFailure = new IllegalStateException("put failed");

        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            client.addMissingProperties("fess_log.search_log", "search_log", "fess_log.search_log",
                    toMappingMetadata(Map.of("user", Map.of("type", "keyword"))));

            assertEquals(1, client.putSources.size());
            assertTrue(capture.messagesAt(Level.INFO).isEmpty(), capture.messagesAt(Level.INFO).toString());
            assertEquals(1, capture.warnings().size(), capture.warnings().toString());
            assertTrue(capture.warnings().get(0).contains("fess_log.search_log"), capture.warnings().get(0));
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_addMissingProperties_notAcknowledgedLogsWarn() {
        final PutRecordingClient client = new PutRecordingClient();
        client.acknowledged = false;

        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            client.addMissingProperties("fess_log.search_log", "search_log", "fess_log.search_log",
                    toMappingMetadata(Map.of("user", Map.of("type", "keyword"))));

            assertEquals(1, client.putSources.size());
            assertTrue(capture.messagesAt(Level.INFO).isEmpty(), capture.messagesAt(Level.INFO).toString());
            assertEquals(1, capture.warnings().size(), capture.warnings().toString());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_addMissingProperties_missingBundledFileLogsWarnAndSkips() {
        final PutRecordingClient client = new PutRecordingClient();

        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            client.addMissingProperties("fess_log.no_such_index", "no_such_index", "fess_log.no_such_index",
                    toMappingMetadata(Map.of("user", Map.of("type", "keyword"))));

            assertTrue(client.putSources.isEmpty(), client.putSources.toString());
            assertEquals(1, capture.warnings().size(), capture.warnings().toString());
            assertTrue(capture.warnings().get(0).contains("is not found"), capture.warnings().get(0));
            assertNull(capture.eventsAt(Level.WARN).get(0).getThrown(), "a missing file needs no stack trace");
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_addMissingProperties_readFailureIsNotReportedAsMissingFile() {
        final PutRecordingClient client = new PutRecordingClient() {
            @Override
            protected String substitutePlaceholders(final String source, final String numberOfShards, final String autoExpandReplicas) {
                throw new IllegalStateException("placeholder failure");
            }
        };

        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            client.addMissingProperties("fess_log.search_log", "search_log", "fess_log.search_log",
                    toMappingMetadata(Map.of("user", Map.of("type", "keyword"))));

            assertTrue(client.putSources.isEmpty(), client.putSources.toString());
            final List<String> warnings = capture.warnings();
            assertEquals(1, warnings.size(), warnings.toString());
            assertTrue(warnings.get(0).startsWith("Failed to read the bundled mapping"), warnings.get(0));
            assertTrue(warnings.get(0).contains("fess_log.search_log/search_log"), warnings.get(0));
            assertFalse(warnings.get(0).contains("is not found"), warnings.get(0));
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_addMissingProperties_skippedInJobProcesses() throws Exception {
        // A crawler, thumbnail, suggest or chunk process has no ChunkVectorHelper, so reading a
        // bundled mapping there failed and logged a WARN with a stack trace for every index at
        // every start. Only the webapp adds missing fields; it runs before any job process. The
        // helper is registered here, so without the gate the field below would be put.
        final Map<String, Object> existing = readBundledProperties(SEARCH_LOG_MAPPING);
        existing.remove("virtualHost");
        final String[] processProperties =
                { "fess.crawler.process", "fess.thumbnail.process", "fess.suggest.process", "fess.chunk.process" };
        for (final String property : processProperties) {
            final PutRecordingClient client = new PutRecordingClient();
            final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
            System.setProperty(property, "true");
            try {
                client.addMissingProperties("fess_log.search_log", "search_log", "fess_log.search_log", toMappingMetadata(existing));

                assertTrue(client.putSources.isEmpty(), property + ": " + client.putSources);
                assertTrue(capture.warnings().isEmpty(), property + ": " + capture.warnings());
                assertTrue(capture.messagesAt(Level.INFO).isEmpty(), property + ": " + capture.messagesAt(Level.INFO));
            } finally {
                System.clearProperty(property);
                capture.detach();
            }
        }
    }

    @Test
    public void test_indexDefinitions_areValidJson() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final String[] paths = { "fess_indices/fess.json", "fess_indices/_aws/fess.json", "fess_indices/_cloud/fess.json",
                "fess_indices/fess/doc.json", "fess_indices/_aws/fess/doc.json", "fess_indices/_cloud/fess/doc.json" };
        for (final String path : paths) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(path)) {
                assertNotNull(in, path + " must exist");
                mapper.readTree(in);
            }
        }
    }

    @Test
    public void test_settingsJson_indexBlockIdenticalAcrossVariantsAndCarriesKnnSettings() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        JsonNode firstIndexNode = null;
        String firstPath = null;
        for (final String path : SETTINGS_JSON_PATHS) {
            final JsonNode indexNode = readJsonResource(mapper, path).path("settings").path("index");
            assertTrue(indexNode.isObject(), path + " must have a settings.index object");
            if (firstIndexNode == null) {
                firstIndexNode = indexNode;
                firstPath = path;
            } else {
                // A single structural-equality assertion catches a missing variant, a shape drift
                // (e.g. one variant losing the merge.policy block), and a value drift (e.g. one
                // variant keeping "engine": "lucene" hardcoded while another uses the placeholder)
                // all at once -- substring checks on each file individually cannot.
                assertEquals(path + "'s settings.index must be identical to " + firstPath + "'s", firstIndexNode, indexNode);
            }
        }
        assertTrue(firstIndexNode.path("knn").asBoolean(false), "index.knn must be true: " + firstIndexNode);
        assertFalse(firstIndexNode.path("knn.derived_source.enabled").asBoolean(true),
                "knn.derived_source.enabled must be false: " + firstIndexNode);
        assertEquals("index.merge.policy.floor_segment must be 16mb: " + firstIndexNode, "16mb",
                firstIndexNode.path("merge").path("policy").path("floor_segment").asText());
        assertEquals(30, firstIndexNode.path("merge").path("policy").path("max_merge_at_once").asInt(),
                "index.merge.policy.max_merge_at_once must be 30: " + firstIndexNode);
    }

    @Test
    public void test_docJson_chunkVectorMappingIdenticalAcrossVariantsAndHasAnnMethodShape() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        JsonNode firstVectorFieldNode = null;
        String firstPath = null;
        for (final String path : DOC_JSON_PATHS) {
            final JsonNode vectorFieldNode = readJsonResource(mapper, path).path("properties").path("content_chunk_vector");
            assertTrue(vectorFieldNode.isObject(), path + " must define content_chunk_vector");
            if (firstVectorFieldNode == null) {
                firstVectorFieldNode = vectorFieldNode;
                firstPath = path;
            } else {
                // Same rationale as the settings check above: one assertion catches a missing
                // variant, a shape drift, and a value drift (e.g. one variant hardcoding "engine":
                // "faiss" while the others use the placeholder) all at once.
                assertEquals(path + "'s content_chunk_vector mapping must be identical to " + firstPath + "'s", firstVectorFieldNode,
                        vectorFieldNode);
            }
        }
        final JsonNode vectorNode = firstVectorFieldNode.path("properties").path("vector");
        assertTrue(vectorNode.path("dimension").isTextual(), "dimension must stay a quoted placeholder, not a bare number: " + vectorNode);
        final JsonNode methodNode = vectorNode.path("method");
        assertTrue(methodNode.path("name").isTextual(), "method.name must be a string (placeholder): " + methodNode);
        assertTrue(methodNode.path("engine").isTextual(), "method.engine must be a string (placeholder): " + methodNode);
        assertTrue(methodNode.path("space_type").isTextual(), "method.space_type must be a string (placeholder): " + methodNode);
        final JsonNode parametersNode = methodNode.path("parameters");
        // OpenSearch rejects a quoted string for these two ("value is not an instance of Integer
        // for Integer parameter [ef_construction]") -- unlike dimension/engine/space_type, they
        // must be bare integer literals, never placeholders.
        assertTrue(parametersNode.path("m").isInt(), "m must be a bare integer, not a quoted string: " + parametersNode);
        assertTrue(parametersNode.path("ef_construction").isInt(),
                "ef_construction must be a bare integer, not a quoted string: " + parametersNode);
    }

    @Test
    public void test_docJson_isValidJsonAfterSubstitution() throws Exception {
        ComponentUtil.getFessConfig().setSystemProperty("content_chunker.embedding.dimension", "1024");
        final SearchEngineClient client = new SearchEngineClient();
        final ObjectMapper mapper = new ObjectMapper();
        for (final String path : DOC_JSON_PATHS) {
            final String source = readResourceAsString(path);
            final String substituted = client.substitutePlaceholders(source, "5", "0-1");
            assertFalse(substituted.contains("${fess."), path + " must have no leftover placeholder: " + substituted);
            final JsonNode root = mapper.readTree(substituted);
            final JsonNode vectorNode = root.path("properties").path("content_chunk_vector").path("properties").path("vector");
            assertEquals(path + "'s dimension must resolve to the configured value", "1024", vectorNode.path("dimension").asText());
            // content_chunker.search.knn.{method,engine,space_type} were left unset for this test,
            // so all three must resolve to their documented defaults, matching
            // ChunkVectorHelper#getKnnEngine()/getKnnSpaceType()'s own defaults exactly -- this is
            // the wiring finding 1 restored: the mapping and the query-time score-scale conversion
            // must read the same values.
            final JsonNode methodNode = vectorNode.path("method");
            assertEquals(path + "'s method.name must default to hnsw", "hnsw", methodNode.path("name").asText());
            assertEquals(path + "'s method.engine must default to lucene", "lucene", methodNode.path("engine").asText());
            assertEquals(path + "'s method.space_type must default to cosinesimil", "cosinesimil", methodNode.path("space_type").asText());
        }
    }

    @Test
    public void test_docJson_tagIsKeywordInEveryVariant() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        for (final String path : DOC_JSON_PATHS) {
            final JsonNode root = mapper.readTree(readResourceAsString(path));
            assertEquals(path, "keyword", root.path("properties").path("tag").path("type").asText());
        }
    }

    @Test
    public void test_docJson_engineAndSpaceTypeResolveFromConfiguredNonDefaultValue() throws Exception {
        // The decisive regression test for a hardcoded (rather than placeholder-driven) mapping:
        // Docker/external OpenSearch legitimately supports faiss (only the embedded,
        // JNI-library-free zip build is lucene-only). If doc.json hardcoded "lucene"/"cosinesimil"
        // instead of reading these placeholders, this test would fail because the configured
        // "faiss"/"l2" would never reach the mapping.
        ComponentUtil.getFessConfig().setSystemProperty("content_chunker.search.knn.engine", "faiss");
        ComponentUtil.getFessConfig().setSystemProperty("content_chunker.search.knn.space_type", "l2");
        final SearchEngineClient client = new SearchEngineClient();
        final ObjectMapper mapper = new ObjectMapper();
        for (final String path : DOC_JSON_PATHS) {
            final String substituted = client.substitutePlaceholders(readResourceAsString(path), "5", "0-1");
            final JsonNode methodNode = mapper.readTree(substituted)
                    .path("properties")
                    .path("content_chunk_vector")
                    .path("properties")
                    .path("vector")
                    .path("method");
            assertEquals(path + "'s method.engine must reflect the configured (non-default) engine", "faiss",
                    methodNode.path("engine").asText());
            assertEquals(path + "'s method.space_type must reflect the configured (non-default) space_type", "l2",
                    methodNode.path("space_type").asText());
        }
    }

    private JsonNode readJsonResource(final ObjectMapper mapper, final String path) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(path)) {
            assertNotNull(in, path + " must exist");
            return mapper.readTree(in);
        }
    }

    private String readResourceAsString(final String path) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(path)) {
            assertNotNull(in, path + " must exist");
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private Map<String, Object> readBundledProperties(final String path) throws IOException {
        final Map<String, Object> mapping =
                new ObjectMapper().readValue(readResourceAsString(path), new TypeReference<Map<String, Object>>() {
                });
        @SuppressWarnings("unchecked")
        final Map<String, Object> properties = (Map<String, Object>) mapping.get("properties");
        return new LinkedHashMap<>(properties);
    }

    /** Builds the "properties" entry the way the HTTP client's get-mappings response does. */
    private static MappingMetadata toMappingMetadata(final Map<String, Object> properties) {
        return new MappingMetadata("properties", properties);
    }

    private static class PutRecordingClient extends SearchEngineClient {
        final List<String> putIndexNames = new ArrayList<>();
        final List<String> putSources = new ArrayList<>();
        boolean acknowledged = true;
        RuntimeException putFailure;

        @Override
        protected boolean putMapping(final String indexName, final String source) {
            putIndexNames.add(indexName);
            putSources.add(source);
            if (putFailure != null) {
                throw putFailure;
            }
            return acknowledged;
        }
    }
}
