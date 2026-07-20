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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.client.SearchEngineClientException;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.opensearch.action.search.SearchAction;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.cluster.metadata.MappingMetadata;
import org.opensearch.index.seqno.SequenceNumbers;
import org.opensearch.search.builder.SearchSourceBuilder;

public class ChunkVectorHelperTest extends UnitFessTestCase {

    private CapturingSearchEngineClient searchEngineClient;
    private TestableChunkVectorHelper helper;

    @Override
    public void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        searchEngineClient = new CapturingSearchEngineClient();
        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        helper = new TestableChunkVectorHelper();
    }

    @Override
    public void tearDown(final TestInfo testInfo) throws Exception {
        // The max-chunks cap tests set this real system property (getMaxChunksPerDocument() reads it
        // through the shared DynamicProperties, not a test-double seam); clear it so it never leaks
        // into another test in this class run.
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (fessConfig != null) {
            fessConfig.setSystemProperty(ContentChunkConstants.MAX_CHUNKS_PER_DOCUMENT, null);
        }
        super.tearDown(testInfo);
    }

    @Test
    public void test_registerMappingRewriteRule_addsOneRule() {
        helper.registerMappingRewriteRule();
        assertEquals(1, searchEngineClient.mappingRules.size());
    }

    @Test
    public void test_rewriteMapping_noopWhenDisabled() {
        helper.setTestEnabled(false);
        helper.setTestDimension("768");
        final String source = "{\"properties\":{\"content\":{\"type\":\"text\"}}}";
        assertEquals(source, helper.rewriteMapping(source));
    }

    @Test
    public void test_rewriteMapping_noopWhenDimensionUnset() {
        helper.setTestEnabled(true);
        helper.setTestDimension(null);
        final String source = "{\"properties\":{\"content\":{\"type\":\"text\"}}}";
        assertEquals(source, helper.rewriteMapping(source));
    }

    @Test
    public void test_rewriteMapping_noopWhenDimensionNonNumeric() {
        // A non-numeric configured dimension (e.g. "768d") must NOT be spliced verbatim into the
        // mapping JSON: doing so would emit malformed JSON ("dimension": 768d), which
        // SearchEngineClient#addMapping only surfaces at WARN -- silently creating the whole index
        // with no proper mapping. The rewrite must instead leave the source unchanged.
        helper.setTestEnabled(true);
        helper.setTestDimension("768d");
        final String source = "{\"properties\":{\"content\":{\"type\":\"text\"}}}";
        final String result = helper.rewriteMapping(source);
        assertEquals("a non-numeric dimension must leave the mapping unchanged", source, result);
        assertFalse(result.contains("content_chunk_vector"), "no content_chunk_vector field may be spliced for a non-numeric dimension");
        assertFalse(result.contains("768d"), "the invalid raw dimension value must never be emitted into the mapping JSON");
    }

    @Test
    public void test_rewriteMapping_noopWhenDimensionNonPositive() {
        // A zero/negative dimension is not a valid knn_vector dimension either; treat it the same
        // as unset/invalid and leave the mapping unchanged rather than emitting "dimension": 0.
        helper.setTestEnabled(true);
        helper.setTestDimension("0");
        final String source = "{\"properties\":{\"content\":{\"type\":\"text\"}}}";
        final String result = helper.rewriteMapping(source);
        assertEquals("a non-positive dimension must leave the mapping unchanged", source, result);
        assertFalse(result.contains("content_chunk_vector"), "no content_chunk_vector field may be spliced for a non-positive dimension");
    }

    @Test
    public void test_rewriteMapping_splicesFieldsBeforeContent() {
        helper.setTestEnabled(true);
        helper.setTestDimension("768");
        final String source = "{\"properties\":{\"title\":{\"type\":\"text\"},\"content\":{\"type\":\"text\"}}}";
        final String result = helper.rewriteMapping(source);
        assertTrue(result.contains("\"content_chunk_vector\""), "should splice in content_chunk_vector: " + result);
        assertTrue(result.contains("\"nested\""), "content_chunk_vector should be nested: " + result);
        assertTrue(result.contains("\"knn_vector\""), "vector sub-field should be knn_vector: " + result);
        assertTrue(result.contains("\"dimension\": 768"), "dimension should come from config: " + result);
        assertTrue(result.contains("\"content_chunk_status\""), "should splice in content_chunk_status: " + result);
        assertTrue(result.contains("\"content_chunk_retry_count\""), "should splice in content_chunk_retry_count: " + result);
        assertTrue(result.indexOf("\"content_chunk_vector\"") < result.indexOf("\"content\":"),
                "new fields must be spliced before the content field: " + result);
        assertTrue(result.contains("\"content\":{\"type\":\"text\"}"), "original content field must be preserved: " + result);
    }

    // ===================================================================================
    //                                                      checkDimensionConsistency (Fix 1: write side)
    //                                                      ================================================

    @Test
    public void test_checkDimensionConsistency_disabled_returnsTrue() {
        helper.setTestEnabled(false);
        helper.testLiveDimension = 384; // even a live mismatch must not matter while disabled
        helper.setTestDimension("768");
        assertTrue(helper.checkDimensionConsistency(), "disabled content chunking must never block a job run");
    }

    @Test
    public void test_checkDimensionConsistency_dimensionUnset_returnsTrue() {
        helper.setTestEnabled(true);
        helper.setTestDimension(null);
        helper.testLiveDimension = 384;
        assertTrue(helper.checkDimensionConsistency(), "an unset configured dimension must fail open, not block the run");
    }

    @Test
    public void test_checkDimensionConsistency_invalidConfiguredDimension_returnsTrue() {
        helper.setTestEnabled(true);
        helper.setTestDimension("not-a-number");
        helper.testLiveDimension = 384;
        assertTrue(helper.checkDimensionConsistency(), "an unparsable configured dimension must fail open, not block the run");
    }

    @Test
    public void test_checkDimensionConsistency_mappingNotYetCreated_returnsTrue() {
        helper.setTestEnabled(true);
        helper.setTestDimension("768");
        helper.testLiveDimension = null; // no content_chunk_vector mapping exists yet
        assertTrue(helper.checkDimensionConsistency(), "no live mapping to compare against must fail open");
    }

    @Test
    public void test_checkDimensionConsistency_readbackFails_returnsTrue() {
        helper.setTestEnabled(true);
        helper.setTestDimension("768");
        helper.testReadLiveMappingDimensionFailure = new RuntimeException("cluster unreachable");
        assertTrue(helper.checkDimensionConsistency(), "a failed mapping readback must fail open, not block the run");
    }

    @Test
    public void test_checkDimensionConsistency_dimensionsMatch_returnsTrue() {
        helper.setTestEnabled(true);
        helper.setTestDimension("768");
        helper.testLiveDimension = 768;
        assertTrue(helper.checkDimensionConsistency(), "matching dimensions must let the job proceed");
    }

    @Test
    public void test_checkDimensionConsistency_dimensionsMismatch_returnsFalse() {
        helper.setTestEnabled(true);
        helper.setTestDimension("768");
        helper.testLiveDimension = 384;
        assertFalse(helper.checkDimensionConsistency(),
                "a confirmed mismatch between the configured and live-mapping dimension must block the run");
    }

    @Test
    public void test_extractDimension_navigatesRealMappingMetadataStructure() {
        // A real MappingMetadata (built via OpenSearch's own String/Map constructor, not a mock)
        // so this pins the JSON-navigation logic against the actual shape getSourceAsMap() returns,
        // not just an assumed one.
        final Map<String, Object> vectorSubField = new HashMap<>();
        vectorSubField.put("type", "knn_vector");
        vectorSubField.put("dimension", 768);
        final Map<String, Object> vectorFieldProperties = new HashMap<>();
        vectorFieldProperties.put(ContentChunkConstants.VECTOR_SUBFIELD, vectorSubField);
        final Map<String, Object> vectorField = new HashMap<>();
        vectorField.put("type", "nested");
        vectorField.put("properties", vectorFieldProperties);
        final Map<String, Object> properties = new HashMap<>();
        properties.put(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD, vectorField);
        properties.put("content", Map.of("type", "text"));
        final Map<String, Object> source = new HashMap<>();
        source.put("properties", properties);

        final MappingMetadata metadata = new MappingMetadata("_doc", source);

        assertEquals(Integer.valueOf(768), helper.extractDimension(metadata));
    }

    @Test
    public void test_extractDimension_noContentChunkVectorField_returnsNull() {
        final Map<String, Object> properties = new HashMap<>();
        properties.put("content", Map.of("type", "text"));
        final Map<String, Object> source = new HashMap<>();
        source.put("properties", properties);

        final MappingMetadata metadata = new MappingMetadata("_doc", source);

        assertNull(helper.extractDimension(metadata), "a mapping without content_chunk_vector must yield null, not throw");
    }

    @Test
    public void test_extractDimension_nullMetadata_returnsNull() {
        assertNull(helper.extractDimension(null));
    }

    @Test
    public void test_processDocument_documentNotFound_returnsFalse() {
        searchEngineClient.documentToReturn = null;
        assertFalse(helper.processDocument("missing-id"), "should return false when the document is not found");
        assertEquals(0, searchEngineClient.storeCallCount);
    }

    // ===================================================================================
    //                                        terminal "skipped" status for un-chunkable / over-cap docs
    //                                        ================================================
    // A document with blank content, that produces zero chunks, or that produces more chunks than
    // content_chunker.max_chunks_per_document must be marked content_chunk_status="skipped" (a DISTINCT
    // terminal status, never "done") with its content LEFT INTACT -- not left silently pending forever
    // (which, once such docs fill the per-run cap, starves genuinely-pending documents).

    @Test
    public void test_processDocument_blankContent_marksSkipped() {
        final Map<String, Object> doc = baseDoc();
        doc.put("content", "   ");
        searchEngineClient.documentToReturn = doc;

        final boolean result = helper.processDocument("doc-1");

        assertTrue(result, "a blank-content document must be durably marked skipped, not left perpetually pending");
        assertEquals("exactly one (skip status) write must happen", 1, searchEngineClient.storeCallCount);
        final Map<String, Object> stored = searchEngineClient.lastStoredDoc;
        assertNotNull(stored, "the skip status must be persisted");
        assertEquals(ContentChunkConstants.STATUS_SKIPPED, stored.get(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD));
        assertEquals("blank content must be left intact for keyword search, never replaced", "   ", stored.get("content"));
        assertFalse(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD),
                "a skipped document must not persist any vectors");
        assertFalse(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD),
                "skipping is terminal, not a retry -- no retry count may be written");
        assertTrue(helper.embedCalls.isEmpty(), "embedding must never be attempted for blank content");
    }

    @Test
    public void test_processDocument_chunkCountOverCap_marksSkippedWithContentPreserved() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        fessConfig.setSystemProperty(ContentChunkConstants.MAX_CHUNKS_PER_DOCUMENT, "3");
        final Map<String, Object> doc = baseDoc();
        doc.put("content", "original full content");
        searchEngineClient.documentToReturn = doc;
        helper.testChunks = List.of("c1", "c2", "c3", "c4"); // 4 chunks > cap of 3

        final boolean result = helper.processDocument("doc-1");

        assertTrue(result, "a document producing more chunks than the cap must be marked skipped");
        assertEquals("only the skip write -- no embed, no done write", 1, searchEngineClient.storeCallCount);
        final Map<String, Object> stored = searchEngineClient.lastStoredDoc;
        assertEquals(ContentChunkConstants.STATUS_SKIPPED, stored.get(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD));
        assertEquals("the ORIGINAL content must be preserved for keyword search, NOT replaced by the chunk array", "original full content",
                stored.get("content"));
        assertFalse(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD),
                "an over-cap document must not persist any vectors");
        assertFalse(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD), "skipping is terminal, not a retry");
        assertTrue(helper.embedCalls.isEmpty(), "an over-cap document must be skipped BEFORE any embedding call");
    }

    @Test
    public void test_processDocument_chunkCountExactlyAtCap_proceedsNormally() {
        // Boundary: > cap skips, but == cap must be processed normally (embedded + stored as done).
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        fessConfig.setSystemProperty(ContentChunkConstants.MAX_CHUNKS_PER_DOCUMENT, "3");
        final Map<String, Object> doc = baseDoc();
        doc.put("content", "original content");
        searchEngineClient.documentToReturn = doc;
        helper.testChunks = List.of("c1", "c2", "c3"); // exactly the cap of 3
        helper.testVectors = List.of(new float[] { 1f }, new float[] { 2f }, new float[] { 3f });

        final boolean result = helper.processDocument("doc-1");

        assertTrue(result, "a document at exactly the cap must be processed normally, not skipped");
        assertEquals(1, searchEngineClient.storeCallCount);
        final Map<String, Object> stored = searchEngineClient.lastStoredDoc;
        assertEquals(ContentChunkConstants.STATUS_DONE, stored.get(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD));
        assertEquals("the done path replaces content with the chunk array", List.of("c1", "c2", "c3"), stored.get("content"));
        assertTrue(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD), "the done path must persist vectors");
        assertEquals("the batch of exactly the cap must have been embedded", List.of(List.of("c1", "c2", "c3")), helper.embedCalls);
    }

    @Test
    public void test_processBatch_blankContentDoc_marksSkippedOthersUnaffected() {
        final Map<String, Object> docA = baseDoc();
        docA.put("content", "   ");
        final Map<String, Object> docB = baseDoc();
        docB.put("content", "content-B");
        helper.testDocumentsById.put("doc-A", docA);
        helper.testDocumentsById.put("doc-B", docB);
        helper.testChunksByContent.put("content-B", List.of("b1"));
        helper.testVectorsByChunk.put("b1", new float[] { 2f });

        final Map<String, Boolean> results = helper.processBatch(List.of("doc-A", "doc-B"));

        assertTrue(results.get("doc-A"), "the blank-content document must be durably marked skipped");
        assertTrue(results.get("doc-B"), "the healthy document must be processed normally, unaffected by its skipped sibling");
        assertEquals(2, searchEngineClient.storedDocs.size(), "both the skip write and the done write must be persisted");

        final Map<String, Object> storedA = findStoredDocByContent("   ");
        assertEquals(ContentChunkConstants.STATUS_SKIPPED, storedA.get(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD));
        assertFalse(storedA.containsKey(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD), "the skipped document must not persist vectors");
        assertFalse(storedA.containsKey(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD), "skipping is terminal, not a retry");

        final Map<String, Object> storedB = findStoredDocByContent(List.of("b1"));
        assertEquals(ContentChunkConstants.STATUS_DONE, storedB.get(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD));
        assertEquals("only the healthy document's chunks may be embedded", List.of(List.of("b1")), helper.embedCalls);
    }

    @Test
    public void test_markSkipped_versionConflict_returnsFalseWithoutThrowing() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Map<String, Object> doc = baseDoc();
        doc.put("content", "   ");
        // A concurrent recrawl wins the CAS race: storeSafely detects the version conflict and returns
        // false WITHOUT throwing. markSkipped must simply propagate that false -- skipping is not a
        // failure, so the document just stays pending for the next run.
        searchEngineClient.throwOnStore = new SearchEngineClientException("Failed to store: " + doc,
                new VersionConflictEngineException("[_doc]: version conflict, required seqNo [5], primary term [1], current [7]"));

        final boolean result = helper.markSkipped(searchEngineClient, fessConfig, doc, "doc-1");

        assertFalse(result, "a lost CAS race on the skip write must return false, not throw");
        assertEquals("the skip write must have been attempted exactly once", 1, searchEngineClient.storeCallCount);
        assertNull(searchEngineClient.lastStoredDoc, "a conflicting skip write must not persist anything");
    }

    @Test
    public void test_markSkipped_nonConflictStoreError_returnsFalseWithoutThrowingOrRetrying() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Map<String, Object> doc = baseDoc();
        doc.put("content", "   ");
        doc.put(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD, 2);
        // A genuine (non-conflict) store error -- storeSafely rethrows it. markSkipped must swallow it
        // and return false (never let it escape, never route into the retry-count accounting): a store
        // error on a skip write just leaves the document pending, to be re-evaluated next run.
        searchEngineClient.throwOnStore = new SearchEngineClientException("Failed to store: " + doc,
                new RuntimeException("cluster_block_exception: index read-only-allow-delete"));
        searchEngineClient.throwOnStoreAlways = true;

        final boolean result = helper.markSkipped(searchEngineClient, fessConfig, doc, "doc-1");

        assertFalse(result, "a non-conflict store error on the skip write must be swallowed and return false, not throw");
        assertEquals("the skip write must have been attempted exactly once -- skipping never retries", 1,
                searchEngineClient.storeCallCount);
        assertNull(searchEngineClient.lastStoredDoc, "nothing may be persisted when the skip write failed");
        assertEquals("skipping must never touch the retry count on the original document", 2,
                doc.get(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD));
    }

    @Test
    public void test_getMaxChunksPerDocument_defaultConfiguredAndClamped() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        assertEquals("the default cap is 1000 when unset", ContentChunkConstants.DEFAULT_MAX_CHUNKS_PER_DOCUMENT,
                helper.getMaxChunksPerDocument());

        fessConfig.setSystemProperty(ContentChunkConstants.MAX_CHUNKS_PER_DOCUMENT, "50");
        assertEquals("a configured cap is honored", 50, helper.getMaxChunksPerDocument());

        fessConfig.setSystemProperty(ContentChunkConstants.MAX_CHUNKS_PER_DOCUMENT, "not-a-number");
        assertEquals("a non-numeric cap falls back to the default", ContentChunkConstants.DEFAULT_MAX_CHUNKS_PER_DOCUMENT,
                helper.getMaxChunksPerDocument());

        fessConfig.setSystemProperty(ContentChunkConstants.MAX_CHUNKS_PER_DOCUMENT, "0");
        assertEquals("a zero cap clamps up to at least 1", 1, helper.getMaxChunksPerDocument());

        fessConfig.setSystemProperty(ContentChunkConstants.MAX_CHUNKS_PER_DOCUMENT, "-5");
        assertEquals("a negative cap clamps up to at least 1", 1, helper.getMaxChunksPerDocument());
    }

    @Test
    public void test_processDocument_success_writesContentVectorAndStatus() {
        final Map<String, Object> doc = baseDoc();
        doc.put("content", "original content");
        searchEngineClient.documentToReturn = doc;
        helper.testChunks = List.of("chunk-a", "chunk-b");
        helper.testVectors = List.of(new float[] { 1f, 2f }, new float[] { 3f, 4f });

        final boolean result = helper.processDocument("doc-1");

        assertTrue(result, "should return true on successful CAS write");
        assertEquals(1, searchEngineClient.storeCallCount);
        final Map<String, Object> stored = searchEngineClient.lastStoredDoc;
        assertEquals(List.of("chunk-a", "chunk-b"), stored.get("content"));
        assertEquals(ContentChunkConstants.STATUS_DONE, stored.get(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD));
        assertFalse(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD), "retry count should be cleared on success");
        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> vectorList =
                (List<Map<String, Object>>) stored.get(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD);
        assertEquals(2, vectorList.size());
        // assertEquals(Object, Object) compares float[] by reference identity, not
        // contents, so use JUnit 5's delta-based float[] comparison directly.
        final float[] v0 = (float[]) vectorList.get(0).get(ContentChunkConstants.VECTOR_SUBFIELD);
        org.junit.jupiter.api.Assertions.assertArrayEquals(new float[] { 1f, 2f }, v0, 0.0f);
    }

    @Test
    public void test_processDocument_listContent_reconstructedAsPlainStringForRechunking() {
        // Simulates a document whose `content` field is already a chunk array (e.g. re-fetched
        // by a future re-embed maintenance job after a prior run stored it as a List<String>).
        // stringifyContent() must join the elements directly rather than falling through to
        // Object#toString(), which would produce Java's bracket-comma-space collection format
        // (e.g. "[chunk-a, chunk-b]") as nonsensical re-chunking input.
        final Map<String, Object> doc = baseDoc();
        doc.put("content", List.of("chunk-a", "chunk-b"));
        searchEngineClient.documentToReturn = doc;
        helper.testChunks = List.of("re-chunk-a", "re-chunk-b");
        helper.testVectors = List.of(new float[] { 1f }, new float[] { 2f });

        final boolean result = helper.processDocument("doc-1");

        assertTrue(result, "should successfully re-chunk a document whose content is already an array");
        assertEquals("list elements must be joined without brackets/commas", "chunk-achunk-b", helper.lastSplitInput);
    }

    @Test
    public void test_processDocument_versionConflict_returnsFalseWithoutRethrowing() {
        final Map<String, Object> doc = baseDoc();
        doc.put("content", "original content");
        searchEngineClient.documentToReturn = doc;
        helper.testChunks = List.of("chunk-a");
        helper.testVectors = List.of(new float[] { 1f });
        // Cause class name genuinely ends with "VersionConflictEngineException" -- the same
        // shape SearchEngineClient#store() produces for a real optimistic-concurrency
        // conflict (it wraps the thrown OpenSearchException, e.g. the real
        // org.opensearch.index.engine.VersionConflictEngineException, as the cause).
        searchEngineClient.throwOnStore = new SearchEngineClientException("Failed to store: " + doc,
                new VersionConflictEngineException("[content_ids]: version conflict, required seqNo [5], primary term [1], current [7]"));

        final boolean result = helper.processDocument("doc-1");

        assertFalse(result, "a version conflict must be treated as a skip, not propagated");
        assertEquals("should attempt the store exactly once", 1, searchEngineClient.storeCallCount);
        assertNull(searchEngineClient.lastStoredDoc, "a version conflict must never be recorded as a persisted failure");
    }

    @Test
    public void test_processDocument_versionConflictViaMessageOnCause_returnsFalseWithoutRethrowing() {
        final Map<String, Object> doc = baseDoc();
        doc.put("content", "original content");
        searchEngineClient.documentToReturn = doc;
        helper.testChunks = List.of("chunk-a");
        helper.testVectors = List.of(new float[] { 1f });
        // The cause is a GENERIC exception class (not named *VersionConflictEngineException) whose
        // MESSAGE contains "version_conflict_engine_exception" -- the shape OpenSearch's HTTP
        // client layer (fesen-httpclient) actually produces when it reconstructs a version
        // conflict from a server response as a generic OpenSearchException carrying the reason
        // string, rather than a real VersionConflictEngineException instance. This exercises the
        // message-substring branch of isVersionConflict on the CAUSE, not the outer
        // SearchEngineClientException frame (whose own message is never message-checked, since it
        // embeds arbitrary crawled document content -- see isVersionConflict's Javadoc).
        searchEngineClient.throwOnStore = new SearchEngineClientException("Failed to store: " + doc,
                new RuntimeException(
                        "OpenSearch exception [type=version_conflict_engine_exception, reason=[content_ids]: version conflict, "
                                + "required seqNo [5], primary term [1]]"));

        final boolean result = helper.processDocument("doc-1");

        assertFalse(result, "a version conflict detected via the cause's message must be treated as a skip, not propagated");
        assertEquals("should attempt the store exactly once", 1, searchEngineClient.storeCallCount);
        assertNull(searchEngineClient.lastStoredDoc, "a version conflict must never be recorded as a persisted failure");
    }

    @Test
    public void test_processDocument_conflictLikeSubstringInOuterMessageOnly_notMisclassifiedAsConflict() {
        final Map<String, Object> doc = baseDoc();
        doc.put("content", "original content");
        searchEngineClient.documentToReturn = doc;
        helper.testChunks = List.of("chunk-a");
        helper.testVectors = List.of(new float[] { 1f });
        helper.setTestMaxRetry(3);
        // The OUTER SearchEngineClientException's own message happens to embed the literal
        // substring "version_conflict_engine_exception" -- simulating SearchEngineClient#store()'s
        // real "Failed to store: " + doc message-building, where the document's own (crawled)
        // content could coincidentally contain this substring. The CAUSE is a genuine, unrelated,
        // non-conflict error whose class name and message do NOT match. isVersionConflict() must
        // not be fooled by the outer frame's message -- only the cause chain (getCause() onward)
        // is message-checked -- so this must flow into the retry-count path, not be swallowed.
        searchEngineClient.throwOnStore = new SearchEngineClientException(
                "Failed to store: {content=... version_conflict_engine_exception ...}", new RuntimeException("mapper_parsing_exception"));

        final boolean result = helper.processDocument("doc-1");

        assertTrue(result, "a substring coincidence in the outer wrapper message must not cause a misclassification");
        assertEquals("must attempt both the failed success write and the successful retry write", 2, searchEngineClient.storeCallCount);
        final Map<String, Object> stored = searchEngineClient.lastStoredDoc;
        assertNotNull(stored, "the genuine failure must be recorded, not silently swallowed as a benign conflict");
        assertEquals(1, stored.get(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD));
    }

    @Test
    public void test_processDocument_bareConflictWordInCauseReason_notMisclassifiedAsConflict() {
        final Map<String, Object> doc = baseDoc();
        doc.put("content", "original content");
        searchEngineClient.documentToReturn = doc;
        helper.testChunks = List.of("chunk-a");
        helper.testVectors = List.of(new float[] { 1f });
        helper.setTestMaxRetry(3);
        // A GENUINE non-conflict store error (mapper_parsing_exception) whose server reason string
        // echoes crawled document content that merely MENTIONS the bare word
        // "version_conflict_engine_exception" -- but NOT the structured
        // "type=version_conflict_engine_exception" token OpenSearch actually emits for a real
        // conflict. isVersionConflict() matches only the structured token, so this must flow into the
        // retry-count path rather than be swallowed as a benign conflict (which would livelock the
        // document, retried forever). The class-name-suffix branch alone cannot save this: over the
        // HTTP client a real conflict is a generic OpenSearchStatusException, so the message check is
        // the load-bearing one and must be the narrower structured-token form.
        searchEngineClient.throwOnStore = new SearchEngineClientException("Failed to store: " + doc,
                new RuntimeException("OpenSearch exception [type=mapper_parsing_exception, reason=failed to parse field [content]; "
                        + "the crawled text happened to contain the word version_conflict_engine_exception here]"));

        final boolean result = helper.processDocument("doc-1");

        assertTrue(result, "a bare conflict-word mention in an unrelated error's reason must not be misclassified as a conflict");
        assertEquals("must attempt both the failed success write and the successful retry write", 2, searchEngineClient.storeCallCount);
        final Map<String, Object> stored = searchEngineClient.lastStoredDoc;
        assertNotNull(stored, "the genuine failure must be recorded, not silently swallowed as a benign conflict");
        assertEquals(1, stored.get(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD));
    }

    @Test
    public void test_processDocument_fetchFailure_returnsFalseWithoutPropagating() {
        // Simulates SearchEngineClient#getDocument's underlying search() throwing on a
        // transient read failure (e.g. InvalidQueryException from a cluster/timeout issue) --
        // a different exception shape than the CAS-write failures covered by the other tests,
        // occurring before any document map is even available. processDocument() must still
        // resolve to a plain boolean rather than letting this escape uncaught.
        searchEngineClient.throwOnGetDocument = new RuntimeException("cluster timeout");

        final boolean result = helper.processDocument("doc-1");

        assertFalse(result, "a fetch failure must resolve to false, not throw");
        assertEquals("no store attempt is possible without a fetched document", 0, searchEngineClient.storeCallCount);
    }

    @Test
    public void test_processDocument_nonConflictStoreFailure_flowsToRetryCountPathNotSwallowed() {
        final Map<String, Object> doc = baseDoc();
        doc.put("content", "original content");
        searchEngineClient.documentToReturn = doc;
        helper.testChunks = List.of("chunk-a");
        helper.testVectors = List.of(new float[] { 1f });
        helper.setTestMaxRetry(3);
        // A generic OpenSearch write failure (mapping error / read-only index / timeout /
        // circuit breaker) -- NOT a version conflict: neither the class name nor the message
        // matches. Single-use: the first store() attempt (the success-path write) fails, but
        // the second attempt (handleFailure()'s retry-accounting write) succeeds, mirroring a
        // transient systemic error.
        searchEngineClient.throwOnStore = new SearchEngineClientException("Failed to store: " + doc,
                new RuntimeException("mapper_parsing_exception: failed to parse field [content_chunk_vector]"));

        final boolean result = helper.processDocument("doc-1");

        assertTrue(result, "the retry-accounting write should still succeed and return true");
        assertEquals("must attempt: 1) the success write (fails), 2) the retry write (succeeds)", 2, searchEngineClient.storeCallCount);
        final Map<String, Object> stored = searchEngineClient.lastStoredDoc;
        assertNotNull(stored, "a non-conflict failure must not be silently swallowed as a benign skip");
        // a non-conflict store failure must be counted toward the retry budget, exactly like a chunk/embed failure
        assertEquals(1, stored.get(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD));
        assertFalse(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD), "status must stay absent below max_retry");
        // Invariant: a document whose content_chunk_status never reached "done" must never have
        // its content field permanently changed from the original -- the failed success-path
        // write must not have leaked its half-applied chunk/vector mutations into the document
        // that handleFailure() ultimately persists.
        assertEquals("original content must survive a recovered non-conflict store failure unchanged", "original content",
                stored.get("content"));
        assertFalse(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD),
                "a premature content_chunk_vector must not be persisted when content_chunk_status never reached done");
    }

    @Test
    public void test_processDocument_nonConflictStoreFailure_withExistingRetryCount_advancesNotResets() {
        final Map<String, Object> doc = baseDoc();
        doc.put("content", "original content");
        doc.put(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD, 2);
        searchEngineClient.documentToReturn = doc;
        helper.testChunks = List.of("chunk-a");
        helper.testVectors = List.of(new float[] { 1f });
        helper.setTestMaxRetry(3);
        // Same bug class as the content-preservation invariant above, on the adjacent
        // content_chunk_retry_count field: the success path's own doc.remove(RETRY_COUNT_FIELD)
        // mutation must not leak into what handleFailure() reads. If it did, a document already
        // at retry_count=2 that hits a recovered non-conflict store failure would incorrectly
        // reset to 1 (looping forever, never reaching max_retry) instead of correctly advancing
        // to 3 and being marked failed.
        searchEngineClient.throwOnStore = new SearchEngineClientException("Failed to store: " + doc,
                new RuntimeException("mapper_parsing_exception: failed to parse field [content_chunk_vector]"));

        helper.processDocument("doc-1");

        final Map<String, Object> stored = searchEngineClient.lastStoredDoc;
        assertNotNull(stored, "a non-conflict failure must not be silently swallowed as a benign skip");
        assertEquals("retry_count must advance from its existing value (2 -> 3), not reset to 1", 3,
                stored.get(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD));
        assertEquals(ContentChunkConstants.STATUS_FAILED, stored.get(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD));
        assertEquals("original content must survive unchanged here too", "original content", stored.get("content"));
    }

    @Test
    public void test_processDocument_nonConflictStoreFailure_persistentlyFails_returnsFalseWithoutPropagating() {
        final Map<String, Object> doc = baseDoc();
        doc.put("content", "original content");
        searchEngineClient.documentToReturn = doc;
        helper.testChunks = List.of("chunk-a");
        helper.testVectors = List.of(new float[] { 1f });
        helper.setTestMaxRetry(3);
        // Every store() attempt fails with a genuine (non-conflict) OpenSearch error -- including
        // handleFailure()'s own retry-accounting write. processDocument() must still return a
        // plain boolean rather than letting the second failure escape uncaught.
        searchEngineClient.throwOnStore = new SearchEngineClientException("Failed to store: " + doc,
                new RuntimeException("cluster_block_exception: index read-only-allow-delete"));
        searchEngineClient.throwOnStoreAlways = true;

        final boolean result = helper.processDocument("doc-1");

        assertFalse(result, "a persistent non-conflict failure must resolve to false, not throw");
        assertEquals("both the success write and the retry write must have been attempted", 2, searchEngineClient.storeCallCount);
        assertNull(searchEngineClient.lastStoredDoc, "neither write succeeded, so nothing should have been persisted");
    }

    @Test
    public void test_processDocument_embeddingFailure_incrementsRetryCountAndLeavesStatusAbsent() {
        final Map<String, Object> doc = baseDoc();
        doc.put("content", "original content");
        searchEngineClient.documentToReturn = doc;
        helper.testChunks = List.of("chunk-a");
        helper.testEmbedFailure = new RuntimeException("embedding API down");
        helper.setTestMaxRetry(3);

        final boolean result = helper.processDocument("doc-1");

        assertTrue(result, "storeSafely succeeding on the failure-path write should still return true");
        final Map<String, Object> stored = searchEngineClient.lastStoredDoc;
        assertEquals(1, stored.get(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD));
        assertFalse(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD), "status must stay absent below max_retry");
    }

    // ===================================================================================
    //                                        mid-run provider outage preserves retry budget (MED-4)
    //                                        ================================================

    @Test
    public void test_processDocument_embeddingFailureWhileProviderUnavailable_leavesDocumentPendingWithoutCountingRetry() {
        final Map<String, Object> doc = baseDoc();
        doc.put("content", "original content");
        searchEngineClient.documentToReturn = doc;
        helper.testChunks = List.of("chunk-a");
        helper.setTestMaxRetry(3);
        // Identical failing-embed setup to
        // test_processDocument_embeddingFailure_incrementsRetryCountAndLeavesStatusAbsent above, EXCEPT
        // the embedding provider has died mid-run (available() == false, after ChunkVectorJob's
        // pre-flight gate had already passed). This is a transient outage, not a poison document:
        // counting it toward content_chunk_retry_count would, across max_retry outage-affected runs,
        // durably stamp the whole corpus content_chunk_status=failed. The document must instead stay
        // pending -- no CAS write at all -- so it is retried unchanged once the provider recovers.
        helper.testEmbedFailure = new RuntimeException("connection refused");
        helper.testEmbeddingAvailable = false;

        final boolean result = helper.processDocument("doc-1");

        assertFalse(result, "an outage failure must resolve to a pending skip, not a durably-recorded terminal state");
        assertEquals("no failure state may be written during a provider outage -- the retry budget must be preserved", 0,
                searchEngineClient.storeCallCount);
        assertNull(searchEngineClient.lastStoredDoc, "the document must stay pending: no content_chunk_retry_count / status write");
    }

    @Test
    public void test_processBatch_embeddingOutage_leavesAllDocumentsPendingWithoutCountingRetry() {
        final Map<String, Object> docA = baseDoc();
        docA.put("content", "content-A");
        final Map<String, Object> docB = baseDoc();
        docB.put("content", "content-B");
        helper.testDocumentsById.put("doc-A", docA);
        helper.testDocumentsById.put("doc-B", docB);
        helper.testChunksByContent.put("content-A", List.of("a1"));
        helper.testChunksByContent.put("content-B", List.of("b1"));
        helper.setTestMaxRetry(3);
        // The provider dies mid-run: the batched embed call throws, each per-document fallback embed
        // call throws too, and available() reports the outage. The real job path (processBatch) must
        // therefore advance no document's retry count -- every document stays pending.
        helper.testEmbedFailure = new RuntimeException("connection refused");
        helper.testEmbeddingAvailable = false;

        final Map<String, Boolean> results = helper.processBatch(List.of("doc-A", "doc-B"));

        assertFalse(results.get("doc-A"), "doc-A must stay pending during a provider outage");
        assertFalse(results.get("doc-B"), "doc-B must stay pending during a provider outage");
        assertEquals("no store may be attempted for any document during a provider outage", 0, searchEngineClient.storeCallCount);
    }

    @Test
    public void test_processDocument_embeddingFailure_marksFailedAtMaxRetry() {
        final Map<String, Object> doc = baseDoc();
        doc.put("content", "original content");
        doc.put(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD, 2);
        searchEngineClient.documentToReturn = doc;
        helper.testChunks = List.of("chunk-a");
        helper.testEmbedFailure = new RuntimeException("embedding API down");
        helper.setTestMaxRetry(3);

        helper.processDocument("doc-1");

        final Map<String, Object> stored = searchEngineClient.lastStoredDoc;
        assertEquals(3, stored.get(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD));
        assertEquals(ContentChunkConstants.STATUS_FAILED, stored.get(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD));
    }

    // ===================================================================================
    //                                        per-vector dimension validation on ingest (Minor A)
    //                                        ================================================

    @Test
    public void test_processDocument_vectorLengthMismatch_recordsFailureNotStoredAsDone() {
        // The provider returns a vector whose LENGTH does not match the configured embedding
        // dimension (a model/config mismatch). Without the ingest-side length guard this vector would
        // be stored and then rejected by OpenSearch's knn_vector dimension check on every retry with
        // only a cryptic mapping error, burning the whole retry budget. The guard must instead fail
        // the document loudly BEFORE the (doomed) success store, routing it through the normal
        // retry-count accounting -- so exactly one (failure-path) store happens, no vector is
        // persisted, and the content is left unchanged.
        final Map<String, Object> doc = baseDoc();
        doc.put("content", "original content");
        searchEngineClient.documentToReturn = doc;
        helper.setTestDimension("3"); // configured dimension = 3
        helper.testChunks = List.of("chunk-a");
        helper.testVectors = List.of(new float[] { 1f, 2f }); // length 2 != 3
        helper.setTestMaxRetry(3);

        final boolean result = helper.processDocument("doc-1");

        assertTrue(result, "the retry-accounting write should still succeed and return true");
        assertEquals("only the failure-path retry write should have been attempted (no doomed success store)", 1,
                searchEngineClient.storeCallCount);
        final Map<String, Object> stored = searchEngineClient.lastStoredDoc;
        assertNotNull(stored, "a wrong-length vector must be recorded as a failure, not silently stored");
        assertEquals(1, stored.get(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD));
        assertFalse(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD), "status must stay absent below max_retry");
        assertFalse(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD), "a wrong-length vector must never be persisted");
        assertEquals("original content must survive unchanged", "original content", stored.get("content"));
    }

    @Test
    public void test_processDocument_vectorLengthMatches_storesSuccessfully() {
        // Control case: when the returned vector length equals the configured dimension, the guard is
        // a no-op and the document is stored as done -- proving the guard does not reject valid vectors.
        final Map<String, Object> doc = baseDoc();
        doc.put("content", "original content");
        searchEngineClient.documentToReturn = doc;
        helper.setTestDimension("2");
        helper.testChunks = List.of("chunk-a");
        helper.testVectors = List.of(new float[] { 1f, 2f }); // length 2 == 2

        final boolean result = helper.processDocument("doc-1");

        assertTrue(result, "a correct-length vector must store successfully");
        assertEquals(1, searchEngineClient.storeCallCount);
        assertEquals(ContentChunkConstants.STATUS_DONE,
                searchEngineClient.lastStoredDoc.get(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD));
    }

    @Test
    public void test_processBatch_vectorLengthMismatch_recordsFailureNotStoredAsDone() {
        // Same wrong-length scenario on the batch path: the batched embed returns a length-2 vector
        // for a configured dimension of 3. The count check passes (1 chunk, 1 vector), so the length
        // guard in storeChunkedDocument is what must catch it -- routing the document through
        // recordFailure without persisting the vector, and without re-embedding the whole batch.
        final Map<String, Object> docA = baseDoc();
        docA.put("content", "content-A");
        helper.testDocumentsById.put("doc-A", docA);
        helper.setTestDimension("3"); // configured dimension = 3
        helper.testChunksByContent.put("content-A", List.of("a1"));
        helper.testVectorsByChunk.put("a1", new float[] { 1f, 2f }); // length 2 != 3
        helper.setTestMaxRetry(3);

        final Map<String, Boolean> results = helper.processBatch(List.of("doc-A"));

        assertTrue(results.get("doc-A"), "the retry-accounting write should still succeed and return true");
        assertEquals(1, searchEngineClient.storedDocs.size(), "only the failure write should have been persisted");
        final Map<String, Object> stored = searchEngineClient.storedDocs.get(0);
        assertEquals(1, stored.get(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD));
        assertFalse(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD), "status must stay absent below max_retry");
        assertFalse(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD),
                "a wrong-length vector must never be persisted on the batch path either");
    }

    // ===================================================================================
    //                                                              CAS (seq_no/primary_term) wiring
    //                                                              ================================
    // These use CasSearchEngineClient (which simulates real OpenSearch CAS semantics by inspecting
    // whether the search requested seqNoAndPrimaryTerm) rather than the canned CapturingSearchEngineClient,
    // so they exercise the REAL fetchDocument -> store CAS path and fail against the unfixed code.

    @Test
    public void test_fetchDocument_requestsSeqNoAndPrimaryTerm_returningRealValuesNotSentinel() {
        // Directly proves the one-line fix: without builder.seqNoAndPrimaryTerm(true), the search returns
        // the -2/0 "unassigned" sentinels and store() silently performs an unconditional overwrite. The
        // fake asserts the flag was requested AND that the real seq_no/primary_term flow back into the doc.
        final Map<String, Object> source = new HashMap<>();
        source.put("content", "original content");
        final CasSearchEngineClient cas = new CasSearchEngineClient(7L, 3L, source);
        ComponentUtil.register(cas, "searchEngineClient");
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final Map<String, Object> doc = new ChunkVectorHelper().fetchDocument(cas, fessConfig, "doc-1");

        assertTrue(cas.lastFetchRequestedSeqNoAndPrimaryTerm, "fetchDocument must request seqNoAndPrimaryTerm(true)");
        assertEquals("the real _seq_no (not the -2 sentinel) must flow back", 7L, doc.get(fessConfig.getIndexFieldSeqNo()));
        assertEquals("the real _primary_term (not the 0 sentinel) must flow back", 3L, doc.get(fessConfig.getIndexFieldPrimaryTerm()));
    }

    @Test
    public void test_processDocument_realCas_cleanRunStoresWithCurrentSeqNoAndSucceeds() {
        final Map<String, Object> source = new HashMap<>();
        source.put("content", "original content");
        final CasSearchEngineClient cas = new CasSearchEngineClient(7L, 3L, source);
        ComponentUtil.register(cas, "searchEngineClient");
        helper.testChunks = List.of("chunk-a");
        helper.testVectors = List.of(new float[] { 1f });

        final boolean result = helper.processDocument("doc-1");

        assertTrue(result, "a CAS write with the current seq_no/primary_term must succeed");
        assertTrue(cas.lastFetchRequestedSeqNoAndPrimaryTerm, "the real fetchDocument path must have requested seq_no/primary_term");
        assertEquals("exactly one store, no failure-path retry", 1, cas.storeCallCount);
        assertNotNull(cas.lastStoredDoc);
        assertEquals(ContentChunkConstants.STATUS_DONE, cas.lastStoredDoc.get(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD));
    }

    @Test
    public void test_processDocument_realConcurrentWrite_lostCasRaceDetectedAsVersionConflict() {
        // The crown-jewel regression guard: a concurrent recrawl writes the same document WHILE we are
        // embedding (onEmbed advances the tracked _seq_no from 7 to 8), so our store carrying the fetched
        // _seq_no=7 must lose a REAL CAS race and surface a genuine version-conflict-shaped exception --
        // handled as a benign skip via isVersionConflict, not recorded as a failure. Against the unfixed
        // fetchDocument this fails: the sentinel seq_no makes store() an unconditional overwrite that
        // "succeeds", so the conflict is never detected (result would be true, lastStoredDoc non-null).
        final Map<String, Object> source = new HashMap<>();
        source.put("content", "original content");
        final CasSearchEngineClient cas = new CasSearchEngineClient(7L, 3L, source);
        ComponentUtil.register(cas, "searchEngineClient");
        helper.testChunks = List.of("chunk-a");
        helper.testVectors = List.of(new float[] { 1f });
        helper.setTestMaxRetry(3);
        helper.onEmbed = () -> cas.currentSeqNo++;

        final boolean result = helper.processDocument("doc-1");

        assertFalse(result, "a real lost CAS race must be treated as a skip, not a success");
        assertEquals("only the conflicting success-path store should have been attempted", 1, cas.storeCallCount);
        assertNull(cas.lastStoredDoc, "the conflicting store must not persist, and a version conflict is not a recorded failure");
    }

    @Test
    public void test_processBatch_multipleDocuments_slicesFlattenedVectorsBackCorrectly() {
        // Documents with DIFFERENT chunk counts (3 vs 1): an off-by-one slicing bug in
        // processBatch's start/end bookkeeping would show up here as a wrong vector count or a
        // vector belonging to the wrong document.
        final Map<String, Object> docA = baseDoc();
        docA.put("content", "content-A");
        final Map<String, Object> docB = baseDoc();
        docB.put("content", "content-B");
        helper.testDocumentsById.put("doc-A", docA);
        helper.testDocumentsById.put("doc-B", docB);
        helper.testChunksByContent.put("content-A", List.of("a1", "a2", "a3"));
        helper.testChunksByContent.put("content-B", List.of("b1"));
        helper.testVectorsByChunk.put("a1", new float[] { 1f });
        helper.testVectorsByChunk.put("a2", new float[] { 2f });
        helper.testVectorsByChunk.put("a3", new float[] { 3f });
        helper.testVectorsByChunk.put("b1", new float[] { 100f });

        final Map<String, Boolean> results = helper.processBatch(List.of("doc-A", "doc-B"));

        assertTrue(results.get("doc-A"), "doc-A should have been stored successfully");
        assertTrue(results.get("doc-B"), "doc-B should have been stored successfully");
        assertEquals(2, searchEngineClient.storedDocs.size(), "both documents must be stored independently");

        final Map<String, Object> storedA = findStoredDocByContent(List.of("a1", "a2", "a3"));
        final Map<String, Object> storedB = findStoredDocByContent(List.of("b1"));
        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> vectorsA =
                (List<Map<String, Object>>) storedA.get(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD);
        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> vectorsB =
                (List<Map<String, Object>>) storedB.get(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD);
        assertEquals(3, vectorsA.size(), "doc-A has 3 chunks and must get exactly 3 sliced vectors");
        assertEquals(1, vectorsB.size(), "doc-B has 1 chunk and must get exactly 1 sliced vector");
        org.junit.jupiter.api.Assertions.assertArrayEquals(new float[] { 1f },
                (float[]) vectorsA.get(0).get(ContentChunkConstants.VECTOR_SUBFIELD), 0.0f);
        org.junit.jupiter.api.Assertions.assertArrayEquals(new float[] { 2f },
                (float[]) vectorsA.get(1).get(ContentChunkConstants.VECTOR_SUBFIELD), 0.0f);
        org.junit.jupiter.api.Assertions.assertArrayEquals(new float[] { 3f },
                (float[]) vectorsA.get(2).get(ContentChunkConstants.VECTOR_SUBFIELD), 0.0f);
        org.junit.jupiter.api.Assertions.assertArrayEquals(new float[] { 100f },
                (float[]) vectorsB.get(0).get(ContentChunkConstants.VECTOR_SUBFIELD), 0.0f);
        assertEquals(ContentChunkConstants.STATUS_DONE, storedA.get(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD));
        assertEquals(ContentChunkConstants.STATUS_DONE, storedB.get(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD));
    }

    @Test
    public void test_processBatch_batchEmbedFails_retriesEachDocumentIndividually_allStillFail() {
        // When the batched embed call fails AND every per-document retry also fails, each document is
        // still retry-counted on its own. The key new-behavior assertion is embedCalls: the batched call
        // is followed by one per-document fallback call each (it is NOT failed as a single unit).
        final Map<String, Object> docA = baseDoc();
        docA.put("content", "content-A");
        final Map<String, Object> docB = baseDoc();
        docB.put("content", "content-B");
        helper.testDocumentsById.put("doc-A", docA);
        helper.testDocumentsById.put("doc-B", docB);
        helper.testChunksByContent.put("content-A", List.of("a1"));
        helper.testChunksByContent.put("content-B", List.of("b1"));
        helper.testEmbedFailure = new RuntimeException("embedding API down");
        helper.setTestMaxRetry(3);

        final Map<String, Boolean> results = helper.processBatch(List.of("doc-A", "doc-B"));

        assertTrue(results.get("doc-A"), "the retry-accounting write should still succeed and return true");
        assertTrue(results.get("doc-B"), "the retry-accounting write should still succeed and return true");
        assertEquals("the batched call must be followed by one per-document fallback call each",
                List.of(List.of("a1", "b1"), List.of("a1"), List.of("b1")), helper.embedCalls);
        assertEquals(2, searchEngineClient.storedDocs.size(), "each document's failure must be recorded independently");
        for (final Map<String, Object> stored : searchEngineClient.storedDocs) {
            assertEquals("a still-failing document must be counted toward its own retry budget", 1,
                    stored.get(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD));
            assertFalse(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD), "status must stay absent below max_retry");
            assertFalse(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD),
                    "no vector should be persisted when neither the batch nor the individual retry embedded successfully");
        }
    }

    @Test
    public void test_processBatch_batchEmbedFails_fallsBackToPerDocumentEmbedding_bothSucceed() {
        // The discriminating test for Fix 2: the batched call (2 chunks) trips a provider size limit and
        // throws, but each document embeds fine on its own. Both documents must end up STATUS_DONE with
        // their vectors and NO retry increment. Against the old processBatch (which failed the whole batch
        // as a unit) both documents would instead be retry-counted with no vectors -- so this test is red
        // without the fix.
        final Map<String, Object> docA = baseDoc();
        docA.put("content", "content-A");
        final Map<String, Object> docB = baseDoc();
        docB.put("content", "content-B");
        helper.testDocumentsById.put("doc-A", docA);
        helper.testDocumentsById.put("doc-B", docB);
        helper.testChunksByContent.put("content-A", List.of("a1"));
        helper.testChunksByContent.put("content-B", List.of("b1"));
        helper.testVectorsByChunk.put("a1", new float[] { 1f });
        helper.testVectorsByChunk.put("b1", new float[] { 2f });
        helper.embedFailIfSizeAtLeast = 2; // the combined [a1, b1] call fails; each single-chunk call succeeds
        helper.setTestMaxRetry(3);

        final Map<String, Boolean> results = helper.processBatch(List.of("doc-A", "doc-B"));

        assertTrue(results.get("doc-A"), "a healthy document must succeed via the per-document fallback");
        assertTrue(results.get("doc-B"), "a healthy document must succeed via the per-document fallback");
        assertEquals("the batched call must be followed by one per-document fallback call each",
                List.of(List.of("a1", "b1"), List.of("a1"), List.of("b1")), helper.embedCalls);
        assertEquals(2, searchEngineClient.storedDocs.size(), "both documents must be stored");
        final Map<String, Object> storedA = findStoredDocByContent(List.of("a1"));
        final Map<String, Object> storedB = findStoredDocByContent(List.of("b1"));
        assertEquals(ContentChunkConstants.STATUS_DONE, storedA.get(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD));
        assertEquals(ContentChunkConstants.STATUS_DONE, storedB.get(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD));
        assertFalse(storedA.containsKey(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD),
                "a recovered document must not be retry-counted");
        assertFalse(storedB.containsKey(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD),
                "a recovered document must not be retry-counted");
    }

    @Test
    public void test_processBatch_batchEmbedFails_onlyGenuinelyFailingDocIncrementsRetry() {
        // One poisoned document (docB) fails both in the batch (dragging the combined call down) and on its
        // own retry, while its healthy sibling (docA) succeeds via the fallback. Only docB may be
        // retry-counted; docA must be fully processed. Old behavior would have retry-counted BOTH.
        final Map<String, Object> docA = baseDoc();
        docA.put("content", "content-A");
        final Map<String, Object> docB = baseDoc();
        docB.put("content", "content-B");
        helper.testDocumentsById.put("doc-A", docA);
        helper.testDocumentsById.put("doc-B", docB);
        helper.testChunksByContent.put("content-A", List.of("a1"));
        helper.testChunksByContent.put("content-B", List.of("b1"));
        helper.testVectorsByChunk.put("a1", new float[] { 1f });
        helper.poisonChunks.add("b1"); // b1 fails the batch and its own retry; a1 is fine on its own
        helper.setTestMaxRetry(3);

        final Map<String, Boolean> results = helper.processBatch(List.of("doc-A", "doc-B"));

        assertTrue(results.get("doc-A"), "the healthy document must not be dragged down by its poisoned sibling");
        assertTrue(results.get("doc-B"), "docB's retry-accounting write still succeeds and returns true");
        assertEquals(2, searchEngineClient.storedDocs.size(), "docA's success and docB's failure are both persisted");

        final Map<String, Object> storedA = findStoredDocByContent(List.of("a1"));
        assertEquals(ContentChunkConstants.STATUS_DONE, storedA.get(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD));
        assertFalse(storedA.containsKey(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD),
                "the healthy document must not be retry-counted");

        final Map<String, Object> storedB = findStoredDocByContent("content-B");
        assertEquals("only the genuinely failing document is retry-counted", 1,
                storedB.get(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD));
        assertFalse(storedB.containsKey(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD), "status must stay absent below max_retry");
        assertFalse(storedB.containsKey(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD), "a failed document must not persist vectors");
    }

    @Test
    public void test_processBatch_notFoundDocumentSkipped_doesNotCorruptSlicingForOthers() {
        final Map<String, Object> docA = baseDoc();
        docA.put("content", "content-A");
        final Map<String, Object> docC = baseDoc();
        docC.put("content", "content-C");
        helper.testDocumentsById.put("doc-A", docA);
        helper.testDocumentsById.put("missing-doc", null);
        helper.testDocumentsById.put("doc-C", docC);
        helper.testChunksByContent.put("content-A", List.of("a1", "a2"));
        helper.testChunksByContent.put("content-C", List.of("c1"));
        helper.testVectorsByChunk.put("a1", new float[] { 1f });
        helper.testVectorsByChunk.put("a2", new float[] { 2f });
        helper.testVectorsByChunk.put("c1", new float[] { 9f });

        final Map<String, Boolean> results = helper.processBatch(List.of("doc-A", "missing-doc", "doc-C"));

        assertTrue(results.get("doc-A"));
        assertFalse(results.get("missing-doc"), "a not-found document must be skipped, not stored");
        assertTrue(results.get("doc-C"));
        assertEquals(2, searchEngineClient.storedDocs.size(), "only the two found documents should have been stored");

        final Map<String, Object> storedA = findStoredDocByContent(List.of("a1", "a2"));
        final Map<String, Object> storedC = findStoredDocByContent(List.of("c1"));
        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> vectorsA =
                (List<Map<String, Object>>) storedA.get(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD);
        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> vectorsC =
                (List<Map<String, Object>>) storedC.get(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD);
        assertEquals(2, vectorsA.size(), "doc-A's slice must not be shifted by the skipped document in between");
        assertEquals(1, vectorsC.size());
        org.junit.jupiter.api.Assertions.assertArrayEquals(new float[] { 1f },
                (float[]) vectorsA.get(0).get(ContentChunkConstants.VECTOR_SUBFIELD), 0.0f);
        org.junit.jupiter.api.Assertions.assertArrayEquals(new float[] { 2f },
                (float[]) vectorsA.get(1).get(ContentChunkConstants.VECTOR_SUBFIELD), 0.0f);
        org.junit.jupiter.api.Assertions.assertArrayEquals(new float[] { 9f },
                (float[]) vectorsC.get(0).get(ContentChunkConstants.VECTOR_SUBFIELD), 0.0f);
    }

    @Test
    public void test_processBatch_allDocumentsSkipped_returnsWithoutCallingEmbed() {
        final Map<String, Object> docBlank = baseDoc();
        docBlank.put("content", "   ");
        helper.testDocumentsById.put("blank-doc", docBlank);
        helper.testDocumentsById.put("missing-doc", null);
        helper.testEmbedFailure = new RuntimeException("must not be called");

        final Map<String, Boolean> results = helper.processBatch(List.of("blank-doc", "missing-doc"));

        // The blank document is now durably marked skipped (its own status write); the not-found
        // document is still simply skipped. Embedding is still never attempted -- the batch has no
        // entries -- so testEmbedFailure never fires.
        assertTrue(results.get("blank-doc"), "a blank-content document is now durably marked skipped");
        assertFalse(results.get("missing-doc"), "a not-found document is skipped, not stored");
        assertEquals("only the blank doc's skip status write may happen; embedding must never be attempted", 1,
                searchEngineClient.storeCallCount);
        assertTrue(helper.embedCalls.isEmpty(), "embedding must never be attempted when no document produced chunks");
        assertEquals(ContentChunkConstants.STATUS_SKIPPED,
                searchEngineClient.lastStoredDoc.get(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD));
    }

    @Test
    public void test_processBatch_varyingChunkCounts_eachDocumentGetsExactlyItsOwnVectors() {
        // Three documents with DIFFERENT chunk counts (1, 5, 2) in one batch. Each chunk's vector
        // encodes a recognizable per-source-chunk marker (100s=doc-A, 200s=doc-B, 300s=doc-C), so a
        // boundary/offset reassembly bug shows up as a WRONG VECTOR VALUE assertion failure -- a
        // document silently receiving a neighbor's vector -- not merely a wrong vector COUNT, which a
        // less careful test could pass by accident even with a shifted assignment.
        final Map<String, Object> docA = baseDoc();
        docA.put("content", "content-A");
        final Map<String, Object> docB = baseDoc();
        docB.put("content", "content-B");
        final Map<String, Object> docC = baseDoc();
        docC.put("content", "content-C");
        helper.testDocumentsById.put("doc-A", docA);
        helper.testDocumentsById.put("doc-B", docB);
        helper.testDocumentsById.put("doc-C", docC);
        helper.testChunksByContent.put("content-A", List.of("a1"));
        helper.testChunksByContent.put("content-B", List.of("b1", "b2", "b3", "b4", "b5"));
        helper.testChunksByContent.put("content-C", List.of("c1", "c2"));
        helper.testVectorsByChunk.put("a1", new float[] { 100f });
        helper.testVectorsByChunk.put("b1", new float[] { 201f });
        helper.testVectorsByChunk.put("b2", new float[] { 202f });
        helper.testVectorsByChunk.put("b3", new float[] { 203f });
        helper.testVectorsByChunk.put("b4", new float[] { 204f });
        helper.testVectorsByChunk.put("b5", new float[] { 205f });
        helper.testVectorsByChunk.put("c1", new float[] { 301f });
        helper.testVectorsByChunk.put("c2", new float[] { 302f });

        final Map<String, Boolean> results = helper.processBatch(List.of("doc-A", "doc-B", "doc-C"));

        assertTrue(results.get("doc-A"));
        assertTrue(results.get("doc-B"));
        assertTrue(results.get("doc-C"));
        assertEquals(3, searchEngineClient.storedDocs.size());
        assertVectorMarkers(findStoredDocByContent(List.of("a1")), 100f);
        assertVectorMarkers(findStoredDocByContent(List.of("b1", "b2", "b3", "b4", "b5")), 201f, 202f, 203f, 204f, 205f);
        assertVectorMarkers(findStoredDocByContent(List.of("c1", "c2")), 301f, 302f);
    }

    @Test
    public void test_processBatch_adjacentBoundary_offByOneWouldMisassignNeighborVector() {
        // Deliberately constructed adjacent-boundary case: doc-Q sits between doc-P (2 chunks) and
        // doc-R (2 chunks) in the flattened request, contributing exactly ONE chunk. This is the
        // classic off-by-one shape: a start/end offset scheme that computes doc-Q's window as
        // [start, start] instead of [start, start+1) (or vice versa, [start-1, start) ) would hand
        // doc-Q either doc-P's LAST vector (11) or doc-R's FIRST vector (30) instead of its own (20)
        // -- and would simultaneously leave doc-P or doc-R one vector short or with an extra one.
        // Asserting doc-Q's exact single value (not just its count) directly catches either direction
        // of that classic boundary bug, and asserting doc-P's/doc-R's own untouched values catches the
        // corresponding neighbor-side corruption.
        final Map<String, Object> docP = baseDoc();
        docP.put("content", "content-P");
        final Map<String, Object> docQ = baseDoc();
        docQ.put("content", "content-Q");
        final Map<String, Object> docR = baseDoc();
        docR.put("content", "content-R");
        helper.testDocumentsById.put("doc-P", docP);
        helper.testDocumentsById.put("doc-Q", docQ);
        helper.testDocumentsById.put("doc-R", docR);
        helper.testChunksByContent.put("content-P", List.of("p1", "p2"));
        helper.testChunksByContent.put("content-Q", List.of("q1"));
        helper.testChunksByContent.put("content-R", List.of("r1", "r2"));
        helper.testVectorsByChunk.put("p1", new float[] { 10f });
        helper.testVectorsByChunk.put("p2", new float[] { 11f });
        helper.testVectorsByChunk.put("q1", new float[] { 20f });
        helper.testVectorsByChunk.put("r1", new float[] { 30f });
        helper.testVectorsByChunk.put("r2", new float[] { 31f });

        final Map<String, Boolean> results = helper.processBatch(List.of("doc-P", "doc-Q", "doc-R"));

        assertTrue(results.get("doc-P"));
        assertTrue(results.get("doc-Q"));
        assertTrue(results.get("doc-R"));
        assertVectorMarkers(findStoredDocByContent(List.of("p1", "p2")), 10f, 11f);
        assertVectorMarkers(findStoredDocByContent(List.of("q1")), 20f);
        assertVectorMarkers(findStoredDocByContent(List.of("r1", "r2")), 30f, 31f);
    }

    @Test
    public void test_processBatch_globalEmbeddingCountMismatch_throwsAndFallsBackToPerDocumentEmbedding() {
        // The combined embed call does NOT throw, but returns a vector list whose SIZE does not
        // match the combined chunk count -- the vectors.size() != allChunks.size() hard-fail check
        // (distinct from an embedChunks() exception, exercised by the retriesEachDocumentIndividually
        // test above) must still catch this and route into the same per-document fallback. Each
        // individual fallback call ALSO returns the same wrong-sized list, so the per-document count
        // check fires again there too -- proving the count guard is not bypassed on the fallback path.
        final Map<String, Object> docA = baseDoc();
        docA.put("content", "content-A");
        final Map<String, Object> docB = baseDoc();
        docB.put("content", "content-B");
        helper.testDocumentsById.put("doc-A", docA);
        helper.testDocumentsById.put("doc-B", docB);
        helper.testChunksByContent.put("content-A", List.of("a1"));
        helper.testChunksByContent.put("content-B", List.of("b1"));
        // No testVectorsByChunk entries -> embedChunks() always returns this fixed 3-element list,
        // regardless of how many chunks it was actually given (2 for the combined call, 1 for each
        // individual fallback call) -- simulating a provider that returns the wrong COUNT without
        // throwing, at both the combined AND per-document-retry stages.
        helper.testVectors = List.of(new float[] { 9f }, new float[] { 8f }, new float[] { 7f });
        helper.setTestMaxRetry(3);

        final Map<String, Boolean> results = helper.processBatch(List.of("doc-A", "doc-B"));

        assertTrue(results.get("doc-A"), "the retry-accounting write should still succeed and return true");
        assertTrue(results.get("doc-B"), "the retry-accounting write should still succeed and return true");
        assertEquals("the batched call must be followed by one per-document fallback call each",
                List.of(List.of("a1", "b1"), List.of("a1"), List.of("b1")), helper.embedCalls);
        assertEquals(2, searchEngineClient.storedDocs.size(), "each document's failure must be recorded independently");
        for (final Map<String, Object> stored : searchEngineClient.storedDocs) {
            assertEquals("a still-mismatched document must be counted toward its own retry budget", 1,
                    stored.get(ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD));
            assertFalse(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD), "status must stay absent below max_retry");
            assertFalse(stored.containsKey(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD),
                    "no vector should be persisted when the embedding count never matched");
        }
    }

    /**
     * Asserts that a stored document's {@code content_chunk_vector} list has exactly
     * {@code expectedMarkers.length} single-float vectors, equal in order to {@code expectedMarkers}.
     * Comparing exact marker VALUES (not just list size) is what makes a boundary/offset reassembly
     * bug show up as a wrong-value failure here, rather than passing vacuously on count alone.
     */
    private void assertVectorMarkers(final Map<String, Object> stored, final float... expectedMarkers) {
        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> vectorList =
                (List<Map<String, Object>>) stored.get(ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD);
        assertEquals(expectedMarkers.length, vectorList.size(), "unexpected vector count for stored doc content=" + stored.get("content"));
        for (int i = 0; i < expectedMarkers.length; i++) {
            org.junit.jupiter.api.Assertions.assertArrayEquals(new float[] { expectedMarkers[i] },
                    (float[]) vectorList.get(i).get(ContentChunkConstants.VECTOR_SUBFIELD), 0.0f,
                    "vector at index " + i + " for stored doc content=" + stored.get("content"));
        }
    }

    private Map<String, Object> findStoredDocByContent(final Object expectedContent) {
        return searchEngineClient.storedDocs.stream()
                .filter(d -> expectedContent.equals(d.get("content")))
                .findFirst()
                .orElseThrow(() -> new AssertionError("no stored doc found with content=" + expectedContent));
    }

    private Map<String, Object> baseDoc() {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("_seq_no", 5L);
        doc.put("_primary_term", 1L);
        return doc;
    }

    private static final class CapturingSearchEngineClient extends SearchEngineClient {
        final List<UnaryOperator<String>> mappingRules = new ArrayList<>();
        Map<String, Object> documentToReturn;
        Map<String, Object> lastStoredDoc;
        /** Every stored doc, in call order -- {@link #processBatch} tests need more than just the last. */
        final List<Map<String, Object>> storedDocs = new ArrayList<>();
        int storeCallCount = 0;
        RuntimeException throwOnStore;
        /** If true, {@link #throwOnStore} fires on every call; otherwise it fires once and is cleared. */
        boolean throwOnStoreAlways = false;
        RuntimeException throwOnGetDocument;

        @Override
        public void addDocumentMappingRewriteRule(final UnaryOperator<String> rule) {
            mappingRules.add(rule);
        }

        @Override
        public OptionalEntity<Map<String, Object>> getDocument(final String index,
                final org.codelibs.fess.opensearch.client.SearchEngineClient.SearchCondition<org.opensearch.action.search.SearchRequestBuilder> condition) {
            if (throwOnGetDocument != null) {
                throw throwOnGetDocument;
            }
            return documentToReturn == null ? OptionalEntity.empty() : OptionalEntity.of(new HashMap<>(documentToReturn));
        }

        @Override
        public boolean store(final String index, final Object obj) {
            storeCallCount++;
            if (throwOnStore != null) {
                final RuntimeException toThrow = throwOnStore;
                if (!throwOnStoreAlways) {
                    throwOnStore = null;
                }
                throw toThrow;
            }
            @SuppressWarnings("unchecked")
            final Map<String, Object> map = (Map<String, Object>) obj;
            lastStoredDoc = map;
            storedDocs.add(map);
            return true;
        }
    }

    /**
     * A fake that reproduces real OpenSearch optimistic-concurrency (CAS) semantics closely enough to
     * exercise {@link ChunkVectorHelper}'s fetch -> store -> conflict-detection path end to end without
     * a live cluster. Unlike {@link CapturingSearchEngineClient} (which returns a canned document and
     * never inspects the search request), this fake:
     * <ul>
     * <li>runs the caller's {@code SearchCondition} against a REAL {@link SearchRequestBuilder}, so it
     * can tell whether {@link ChunkVectorHelper#fetchDocument} requested {@code seqNoAndPrimaryTerm(true)}.
     * When it was NOT requested it returns the "unassigned" sentinels
     * ({@link SequenceNumbers#UNASSIGNED_SEQ_NO}=-2, {@link SequenceNumbers#UNASSIGNED_PRIMARY_TERM}=0),
     * exactly as a real search does -- which is precisely what silently disabled CAS in the unfixed
     * code.</li>
     * <li>enforces CAS on {@link #store}: a write whose {@code _seq_no}/{@code _primary_term} match the
     * currently-tracked values succeeds and advances {@code _seq_no} (as a real successful index would);
     * a write carrying the sentinels is treated as an unconditional overwrite (real "no CAS" semantics,
     * so the unfixed code's store succeeds and the conflict is never seen); a write carrying stale
     * (mismatched, non-sentinel) values is rejected with a version-conflict-shaped exception.</li>
     * </ul>
     * This makes the CAS tests fail against the unfixed {@code fetchDocument} rather than passing on a
     * synthesized exception the way the {@link CapturingSearchEngineClient}-based tests do.
     */
    private static final class CasSearchEngineClient extends SearchEngineClient {
        long currentSeqNo;
        long currentPrimaryTerm;
        final Map<String, Object> source;
        Map<String, Object> lastStoredDoc;
        int storeCallCount = 0;
        boolean lastFetchRequestedSeqNoAndPrimaryTerm = false;

        CasSearchEngineClient(final long seqNo, final long primaryTerm, final Map<String, Object> source) {
            this.currentSeqNo = seqNo;
            this.currentPrimaryTerm = primaryTerm;
            this.source = source;
        }

        @Override
        public OptionalEntity<Map<String, Object>> getDocument(final String index,
                final org.codelibs.fess.opensearch.client.SearchEngineClient.SearchCondition<SearchRequestBuilder> condition) {
            final SearchRequestBuilder builder = new SearchRequestBuilder(this, SearchAction.INSTANCE);
            condition.build(builder);
            final SearchSourceBuilder sourceBuilder = builder.request().source();
            lastFetchRequestedSeqNoAndPrimaryTerm = sourceBuilder != null && Boolean.TRUE.equals(sourceBuilder.seqNoAndPrimaryTerm());
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final Map<String, Object> doc = new HashMap<>(source);
            if (lastFetchRequestedSeqNoAndPrimaryTerm) {
                doc.put(fessConfig.getIndexFieldSeqNo(), currentSeqNo);
                doc.put(fessConfig.getIndexFieldPrimaryTerm(), currentPrimaryTerm);
            } else {
                doc.put(fessConfig.getIndexFieldSeqNo(), SequenceNumbers.UNASSIGNED_SEQ_NO);
                doc.put(fessConfig.getIndexFieldPrimaryTerm(), SequenceNumbers.UNASSIGNED_PRIMARY_TERM);
            }
            return OptionalEntity.of(doc);
        }

        @Override
        public boolean store(final String index, final Object obj) {
            storeCallCount++;
            @SuppressWarnings("unchecked")
            final Map<String, Object> map = (Map<String, Object>) obj;
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final Number seqNo = (Number) map.get(fessConfig.getIndexFieldSeqNo());
            final Number primaryTerm = (Number) map.get(fessConfig.getIndexFieldPrimaryTerm());
            final boolean noCas = seqNo == null || primaryTerm == null || (seqNo.longValue() == SequenceNumbers.UNASSIGNED_SEQ_NO
                    && primaryTerm.longValue() == SequenceNumbers.UNASSIGNED_PRIMARY_TERM);
            if (!noCas && (seqNo.longValue() != currentSeqNo || primaryTerm.longValue() != currentPrimaryTerm)) {
                throw new SearchEngineClientException("Failed to store: " + obj,
                        new VersionConflictEngineException("[_doc]: version conflict, required seqNo [" + seqNo + "], primary term ["
                                + primaryTerm + "], current [" + currentSeqNo + "]"));
            }
            currentSeqNo++;
            lastStoredDoc = map;
            return true;
        }
    }

    /**
     * Test double whose simple (and thus fully-qualified) class name ends with
     * {@code "VersionConflictEngineException"}, matching the class-name-suffix branch of
     * {@link ChunkVectorHelper#isVersionConflict(Throwable)} without depending on the real
     * {@code org.opensearch.index.engine.VersionConflictEngineException}, which is
     * inconvenient to construct directly in a unit test.
     */
    private static final class VersionConflictEngineException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        VersionConflictEngineException(final String message) {
            super(message);
        }
    }

    private static final class TestableChunkVectorHelper extends ChunkVectorHelper {
        private boolean testEnabled = false;
        private String testDimension = null;
        private int testMaxRetry = ContentChunkConstants.DEFAULT_JOB_MAX_RETRY;
        /**
         * Seam for {@link #isEmbeddingClientAvailable()}. Defaults to {@code true} so every existing
         * failure-path test keeps its retry-count-accounting behavior; a test sets it false to
         * simulate a mid-run provider outage (handleFailure must then leave the document pending).
         */
        boolean testEmbeddingAvailable = true;
        List<String> testChunks = List.of();
        List<float[]> testVectors = List.of();
        RuntimeException testEmbedFailure;
        String lastSplitInput;

        /** Every {@link #embedChunks} invocation's input, in call order -- lets a test assert the
         * batched call happened first and was then followed by one per-document fallback call each. */
        final List<List<String>> embedCalls = new ArrayList<>();
        /** Makes {@link #embedChunks} throw when given at least this many chunks -- simulates a
         * provider request-size limit that fails the whole combined batch but not a single document. */
        int embedFailIfSizeAtLeast = Integer.MAX_VALUE;
        /** Makes {@link #embedChunks} throw whenever the input contains one of these chunks --
         * simulates a single genuinely-bad document that fails both in the batch and on its own retry. */
        final Set<String> poisonChunks = new HashSet<>();
        /** Invoked at the start of every {@link #embedChunks} call -- lets a test simulate a concurrent
         * recrawl writing the document (advancing its _seq_no) between the fetch and the store. */
        Runnable onEmbed;

        /**
         * Per-ID documents for {@link #processBatch} tests, looked up ahead of the real
         * {@link SearchEngineClient}-backed fetch -- a value of {@code null} for a present key
         * simulates a not-found document, distinct from a key that is simply absent (which falls
         * back to the {@link CapturingSearchEngineClient}-backed single-document tests' fetch path).
         */
        final Map<String, Map<String, Object>> testDocumentsById = new HashMap<>();
        /** Per-exact-content chunk lists for {@link #processBatch} tests; falls back to {@link #testChunks} otherwise. */
        final Map<String, List<String>> testChunksByContent = new HashMap<>();
        /** Per-exact-chunk-text vectors for {@link #processBatch} tests; falls back to {@link #testVectors} when empty. */
        final Map<String, float[]> testVectorsByChunk = new HashMap<>();

        void setTestEnabled(final boolean enabled) {
            this.testEnabled = enabled;
        }

        void setTestDimension(final String dimension) {
            this.testDimension = dimension;
        }

        void setTestMaxRetry(final int maxRetry) {
            this.testMaxRetry = maxRetry;
        }

        @Override
        public boolean isContentChunkerEnabled() {
            return testEnabled;
        }

        @Override
        protected boolean isEmbeddingClientAvailable() {
            return testEmbeddingAvailable;
        }

        @Override
        protected String getEmbeddingDimensionConfig() {
            return testDimension;
        }

        /** Seam value for {@link #readLiveMappingDimension}; null simulates no mapping created yet. */
        Integer testLiveDimension;
        /** When set, makes {@link #readLiveMappingDimension} throw instead of returning {@link #testLiveDimension}. */
        RuntimeException testReadLiveMappingDimensionFailure;

        @Override
        protected Integer readLiveMappingDimension() {
            if (testReadLiveMappingDimensionFailure != null) {
                throw testReadLiveMappingDimensionFailure;
            }
            return testLiveDimension;
        }

        @Override
        protected Map<String, Object> fetchDocument(final SearchEngineClient searchEngineClient, final FessConfig fessConfig,
                final String id) {
            if (testDocumentsById.containsKey(id)) {
                final Map<String, Object> doc = testDocumentsById.get(id);
                return doc == null ? null : new HashMap<>(doc);
            }
            return super.fetchDocument(searchEngineClient, fessConfig, id);
        }

        @Override
        protected List<String> splitContent(final String content) {
            lastSplitInput = content;
            if (testChunksByContent.containsKey(content)) {
                return testChunksByContent.get(content);
            }
            return testChunks;
        }

        @Override
        protected List<float[]> embedChunks(final List<String> chunks) {
            embedCalls.add(new ArrayList<>(chunks));
            if (onEmbed != null) {
                onEmbed.run();
            }
            if (testEmbedFailure != null) {
                throw testEmbedFailure;
            }
            if (chunks.size() >= embedFailIfSizeAtLeast) {
                throw new RuntimeException("simulated batch-size embedding failure: " + chunks.size() + " chunks");
            }
            for (final String chunk : chunks) {
                if (poisonChunks.contains(chunk)) {
                    throw new RuntimeException("simulated per-chunk embedding failure: " + chunk);
                }
            }
            if (!testVectorsByChunk.isEmpty()) {
                final List<float[]> vectors = new ArrayList<>(chunks.size());
                for (final String chunk : chunks) {
                    vectors.add(testVectorsByChunk.get(chunk));
                }
                return vectors;
            }
            return testVectors;
        }

        @Override
        protected int getJobMaxRetry() {
            return testMaxRetry;
        }

        @Override
        protected FessConfig getFessConfigForContentField() {
            return super.getFessConfigForContentField();
        }
    }
}
