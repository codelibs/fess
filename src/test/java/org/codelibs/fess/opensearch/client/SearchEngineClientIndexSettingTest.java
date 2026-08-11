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

import org.codelibs.fess.helper.ChunkVectorHelper;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
 * splicing an unvalidated token into the shipped mapping.
 */
public class SearchEngineClientIndexSettingTest extends UnitFessTestCase {

    private static final String DOC_INDEX_CONFIG = "fess_indices/fess.json";

    private static final String CONFIG_INDEX_CONFIG = "fess_indices/fess_config.web_config.json";

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
        // engine/space_type feed BOTH the static mapping (via this substitution) and
        // ChunkVectorHelper#getKnnEngine/getKnnSpaceType's query-time score-scale conversion --
        // they must resolve from the same system properties so the two sides cannot diverge.
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
    public void test_addMapping_substitutesDimension() {
        ComponentUtil.getFessConfig().setSystemProperty("content_chunker.embedding.dimension", "384");
        final SearchEngineClient client = new SearchEngineClient();

        final String actual = client.substitutePlaceholders("{\"dimension\":\"${fess.content_chunker.embedding.dimension}\"}",
                ComponentUtil.getFessConfig().getIndexNumberOfShards(), ComponentUtil.getFessConfig().getIndexAutoExpandReplicas());

        assertEquals("{\"dimension\":\"384\"}", actual);
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
}
