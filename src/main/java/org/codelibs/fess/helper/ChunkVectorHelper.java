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
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.chunk.ChunkerManager;
import org.codelibs.fess.embedding.AbstractEmbeddingClient;
import org.codelibs.fess.embedding.EmbeddingClientManager;
import org.codelibs.fess.embedding.EmbeddingException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.client.SearchEngineClientException;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.opensearch.cluster.metadata.MappingMetadata;
import org.opensearch.index.query.QueryBuilders;

import jakarta.annotation.PostConstruct;

/**
 * Orchestrates the ingestion half of content-chunk vector search: registers
 * the OpenSearch document-mapping rewrite rule that adds the
 * {@code content_chunk_vector} field, and performs the per-document
 * CAS chunk+embed read-modify-write ({@link #processDocument(String)}).
 */
public class ChunkVectorHelper {

    private static final Logger logger = LogManager.getLogger(ChunkVectorHelper.class);

    /** Sub-field of {@link Constants#CONTENT_CHUNK_VECTOR_FIELD} holding the knn_vector value. */
    public static final String VECTOR_SUBFIELD = "vector";

    /** System property key for the maximum number of chunks a single document may produce. */
    protected static final String MAX_CHUNKS_PER_DOCUMENT_PROPERTY = "content_chunker.max_chunks_per_document";

    /** Default value of {@link #MAX_CHUNKS_PER_DOCUMENT_PROPERTY}. */
    protected static final int DEFAULT_MAX_CHUNKS_PER_DOCUMENT = 1000;

    /**
     * Default constructor.
     */
    public ChunkVectorHelper() {
        // Default constructor
    }

    /**
     * Initializes this helper: registers the mapping rewrite rule.
     */
    @PostConstruct
    public void init() {
        registerMappingRewriteRule();
    }

    /**
     * Registers the document-mapping rewrite rule with {@link SearchEngineClient}.
     * Mirrors {@code SemanticSearchHelper.init()}'s
     * {@code addDocumentMappingRewriteRule(...)} string-splice pattern.
     */
    protected void registerMappingRewriteRule() {
        final SearchEngineClient client = ComponentUtil.getSearchEngineClient();
        client.addDocumentMappingRewriteRule(this::rewriteMapping);
    }

    /**
     * Splices the {@code content_chunk_vector} field definition immediately
     * before the literal {@code "content":} key in the document mapping JSON
     * ({@code content_chunk_status} is mapped statically in {@code doc.json};
     * only the config-dependent knn_vector dimension needs a rewrite). A
     * no-op when {@code content_chunker.enabled} is false or
     * {@code content_chunker.embedding.dimension} is unset -- this reads the
     * static config value only, never a live {@code EmbeddingClient} call,
     * since this rule fires at index-creation time before any HTTP
     * connectivity to an embedding provider is guaranteed (design doc §5.2).
     *
     * @param source the original document mapping JSON source
     * @return the rewritten mapping JSON source
     */
    protected String rewriteMapping(final String source) {
        if (!isContentChunkerEnabled()) {
            return source;
        }
        final int dimension = getConfiguredEmbeddingDimension();
        if (dimension <= 0) {
            // Guard the raw config value against being spliced verbatim into the whole doc.json
            // mapping JSON: a non-numeric or non-positive value (e.g. "768d") would otherwise emit
            // malformed JSON, which SearchEngineClient#addMapping only surfaces at WARN, silently
            // creating the entire index with no proper mapping. Skip the rewrite unchanged instead,
            // mirroring the isBlank fail-open above.
            logger.warn("[ChunkVector] {} is unset or not a positive integer ({}); skipping content_chunk_vector mapping rewrite.",
                    AbstractEmbeddingClient.EMBEDDING_DIMENSION_PROPERTY, getEmbeddingDimensionConfig());
            return source;
        }
        final String fieldDef = "\"" + Constants.CONTENT_CHUNK_VECTOR_FIELD + "\": {\n" //
                + "  \"type\": \"nested\",\n" //
                + "  \"properties\": {\n" //
                + "    \"" + VECTOR_SUBFIELD + "\": {\n" //
                + "      \"type\": \"knn_vector\",\n" //
                + "      \"dimension\": " + dimension + "\n" //
                + "    }\n" //
                + "  }\n" //
                + "},";
        return source.replace("\"content\":", fieldDef + "\n\"content\":");
    }

    /**
     * Checks if content chunking is enabled. Public so
     * {@link org.codelibs.fess.job.ChunkVectorJob#execute()} can gate the entire run on the same
     * flag this helper's own {@link #rewriteMapping}/{@link #checkDimensionConsistency} read, rather
     * than duplicating the config lookup with a second, potentially divergent default.
     *
     * @return the value of {@code content_chunker.enabled} (default false)
     */
    public boolean isContentChunkerEnabled() {
        return Boolean.parseBoolean(
                ComponentUtil.getFessConfig().getSystemProperty(AbstractEmbeddingClient.CONTENT_CHUNKER_ENABLED_PROPERTY, "false"));
    }

    /**
     * Gets the raw, unparsed {@code content_chunker.embedding.dimension} config value.
     *
     * @return the configured dimension string, or null if unset
     */
    protected String getEmbeddingDimensionConfig() {
        return ComponentUtil.getFessConfig().getSystemProperty(AbstractEmbeddingClient.EMBEDDING_DIMENSION_PROPERTY, null);
    }

    /**
     * Parses {@code content_chunker.embedding.dimension} as a positive integer, using the same
     * {@code Integer.parseInt} pattern as {@link #checkDimensionConsistency()}. Shared by
     * {@link #rewriteMapping(String)} (to avoid splicing a non-numeric value into the mapping JSON)
     * and {@link #validateVectorDimensions(List)} (to reject wrong-length vectors before storing).
     *
     * @return the configured dimension if it is a valid positive integer; otherwise {@code 0},
     *         meaning unset, unparsable, or non-positive
     */
    protected int getConfiguredEmbeddingDimension() {
        final String dimensionStr = getEmbeddingDimensionConfig();
        if (StringUtil.isBlank(dimensionStr)) {
            return 0;
        }
        try {
            final int dimension = Integer.parseInt(dimensionStr.trim());
            return dimension > 0 ? dimension : 0;
        } catch (final NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Fail-fast guard, run ONCE per {@link org.codelibs.fess.job.ChunkVectorJob} execution (not
     * once per document): compares the currently configured embedding dimension against the
     * dimension already baked into the live {@code content_chunk_vector} mapping. Both
     * {@link #getEmbeddingDimensionConfig()} and every {@code EmbeddingClient#getDimension()}
     * read the same live {@code content_chunker.embedding.dimension} config key, so they can
     * never diverge from each other at any single point in time -- the actual failure mode is an
     * operator switching the embedding provider/model (and therefore its native dimension)
     * without recreating the index, so the mapping baked in at index-creation time silently goes
     * stale relative to the now-current config. Without this check every pending document would
     * independently fail against OpenSearch's k-NN dimension-mismatch rejection and end up
     * {@code content_chunk_status=fail}, with the real cause visible only in a per-document WARN
     * log line.
     *
     * <p>Fails open (returns true, proceed) whenever the check cannot meaningfully run --
     * chunking disabled, dimension unset, mapping not created yet, or the mapping readback itself
     * fails -- so this new guard never introduces a regression on top of today's behavior. Fails
     * closed (returns false, caller must skip the run) only on a confirmed, live mismatch.</p>
     *
     * @return true if the job may proceed; false if a confirmed mismatch was detected and the
     *         caller must skip this run entirely without processing any documents
     */
    public boolean checkDimensionConsistency() {
        if (!isContentChunkerEnabled()) {
            return true;
        }
        final String configuredDimensionStr = getEmbeddingDimensionConfig();
        if (StringUtil.isBlank(configuredDimensionStr)) {
            return true;
        }
        final int configuredDimension;
        try {
            configuredDimension = Integer.parseInt(configuredDimensionStr.trim());
        } catch (final NumberFormatException e) {
            logger.warn("[ChunkVector] Invalid integer for {}: {}", AbstractEmbeddingClient.EMBEDDING_DIMENSION_PROPERTY,
                    configuredDimensionStr);
            return true;
        }
        final Integer liveDimension;
        try {
            liveDimension = readLiveMappingDimension();
        } catch (final Exception e) {
            logger.warn("[ChunkVector] Failed to read back the live {} mapping dimension; skipping the fail-fast dimension "
                    + "consistency check for this run.", Constants.CONTENT_CHUNK_VECTOR_FIELD, e);
            return true;
        }
        if (liveDimension == null) {
            // No content_chunk_vector mapping exists yet (e.g. first run before any index has been
            // created) -- nothing to compare the current config against.
            return true;
        }
        if (liveDimension.intValue() != configuredDimension) {
            logger.error(
                    "[ChunkVector] Embedding dimension mismatch: configured {}={} but the live {} mapping was created with "
                            + "dimension={}. This looks like the embedding provider/model was switched without recreating the index. "
                            + "Skipping this job run -- recreate the index (or restore the prior dimension) before re-running.",
                    AbstractEmbeddingClient.EMBEDDING_DIMENSION_PROPERTY, configuredDimension, Constants.CONTENT_CHUNK_VECTOR_FIELD,
                    liveDimension);
            return false;
        }
        return true;
    }

    /**
     * Reads back the {@code dimension} already baked into the live {@code content_chunk_vector}
     * mapping from OpenSearch, mirroring {@code SearchEngineClient#addMapping}'s own
     * {@code prepareGetMappings} usage so this reuses the same client/API convention rather than
     * introducing a new one. Overridable seam for tests (constructing a real
     * {@link GetMappingsResponse} requires a live cluster, which unit tests here do not stand up).
     *
     * @return the live mapping's vector dimension, or null if the {@code content_chunk_vector}
     *         field is not present in any concrete index behind the update alias (e.g. no index
     *         has been created yet)
     */
    protected Integer readLiveMappingDimension() {
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final GetMappingsResponse response = searchEngineClient.admin()
                .indices()
                .prepareGetMappings(fessConfig.getIndexDocumentUpdateIndex())
                .execute()
                .actionGet(fessConfig.getIndexIndicesTimeout());
        final Map<String, MappingMetadata> mappings = response.mappings();
        if (mappings == null) {
            return null;
        }
        for (final MappingMetadata metadata : mappings.values()) {
            final Integer dimension = extractDimension(metadata);
            if (dimension != null) {
                return dimension;
            }
        }
        return null;
    }

    /**
     * Extracts {@code properties.content_chunk_vector.properties.vector.dimension} from one
     * index's mapping metadata.
     *
     * @param metadata one concrete index's mapping metadata
     * @return the vector dimension, or null if the field is absent from this mapping
     */
    protected Integer extractDimension(final MappingMetadata metadata) {
        if (metadata == null) {
            return null;
        }
        final Object properties = metadata.getSourceAsMap().get("properties");
        if (!(properties instanceof Map)) {
            return null;
        }
        final Object vectorField = ((Map<?, ?>) properties).get(Constants.CONTENT_CHUNK_VECTOR_FIELD);
        if (!(vectorField instanceof Map)) {
            return null;
        }
        final Object vectorFieldProperties = ((Map<?, ?>) vectorField).get("properties");
        if (!(vectorFieldProperties instanceof Map)) {
            return null;
        }
        final Object vectorSubField = ((Map<?, ?>) vectorFieldProperties).get(VECTOR_SUBFIELD);
        if (!(vectorSubField instanceof Map)) {
            return null;
        }
        final Object dimensionObj = ((Map<?, ?>) vectorSubField).get("dimension");
        if (dimensionObj instanceof Number) {
            return ((Number) dimensionObj).intValue();
        }
        return null;
    }

    /**
     * Processes one document: splits its content into chunks, embeds them,
     * and writes the result back with a CAS (compare-and-swap) guard against
     * a concurrent recrawl.
     *
     * @param id the OpenSearch document {@code _id}
     * @return true if the document was successfully processed, or a
     *         terminal failure was durably recorded (marked failed); false
     *         if the document was not found, had
     *         blank/unchunkable content, the document could not be fetched,
     *         or a CAS write lost a race to a concurrent recrawl (or, on
     *         persistent write failure, could not even record the failure)
     */
    public boolean processDocument(final String id) {
        final FessConfig fessConfig = getFessConfigForContentField();
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        final Map<String, Object> doc;
        try {
            doc = fetchDocument(searchEngineClient, fessConfig, id);
        } catch (final Exception e) {
            // A transient read failure (e.g. SearchEngineClient#getDocument's underlying
            // search() throwing InvalidQueryException on a cluster/timeout issue) is a
            // different exception shape than the CAS-write failures guarded below, but
            // processDocument() must never throw for a single document either way -- there
            // is no document map here to route into handleFailure(), so simply skip this
            // document for this run; the next scheduled run will retry the fetch.
            logger.warn("[ChunkVector] Failed to fetch document, skipping for this run. id={}", id, e);
            return false;
        }
        if (doc == null) {
            if (logger.isInfoEnabled()) {
                logger.info("[ChunkVector] Document not found, skipping. id={}", id);
            }
            return false;
        }
        try {
            // Content extraction lives inside this try (rather than before it) so that even
            // a pathological Object#toString() on a document field value cannot escape
            // processDocument() uncaught -- every per-document, data-dependent operation is
            // guarded uniformly.
            final List<String> chunks = extractChunks(fessConfig, doc, id);
            if (chunks == null) {
                return markSkipped(searchEngineClient, fessConfig, doc, id);
            }
            final List<float[]> vectors = embedChunks(chunks);
            if (vectors.size() != chunks.size()) {
                throw new EmbeddingException("Embedding count mismatch: chunks=" + chunks.size() + ", vectors=" + vectors.size());
            }
            validateVectorDimensions(vectors);
            final List<Map<String, Object>> vectorList = new ArrayList<>(vectors.size());
            for (final float[] vector : vectors) {
                final Map<String, Object> vectorMap = new HashMap<>();
                vectorMap.put(VECTOR_SUBFIELD, vector);
                vectorList.add(vectorMap);
            }
            // Mutate a COPY, never the shared `doc` map read above -- if storeSafely() below
            // throws (a genuine, non-conflict SearchEngineClientException; see its Javadoc),
            // the outer catch falls into handleFailure(doc, ...), which must operate on a
            // document whose content is still exactly as originally fetched. If these
            // success-path fields were applied to `doc` itself, a document whose
            // content_chunk_status never reached "done" could still end up with its `content`
            // permanently overwritten by the chunk array (plus a premature content_chunk_vector)
            // once handleFailure's own write succeeds.
            final Map<String, Object> updatedDoc = new HashMap<>(doc);
            // Stored as the native List<String> (not joined into one string): LengthChunker's
            // fixed-character-count splitting has no word-boundary awareness, so a separator
            // joined between chunks would frequently land mid-word. OpenSearch/Elasticsearch
            // text-type fields natively accept a JSON array value for the same field.
            updatedDoc.put(fessConfig.getIndexFieldContent(), chunks);
            updatedDoc.put(Constants.CONTENT_CHUNK_VECTOR_FIELD, vectorList);
            updatedDoc.put(Constants.CONTENT_CHUNK_STATUS_FIELD, Constants.DONE);
            return storeSafely(searchEngineClient, fessConfig, updatedDoc, id);
        } catch (final Exception e) {
            logger.warn("[ChunkVector] Failed to process document. id={}", id, e);
            // handleFailure() performs its own CAS write via storeSafely(); if that write
            // hits a genuine (non-version-conflict) OpenSearch error it is rethrown (see
            // storeSafely()'s Javadoc), so this must be guarded too -- processDocument()
            // must never throw for a single document's processing failure (Task 11's
            // scroll-query job loop relies on a plain boolean return per document).
            try {
                return handleFailure(searchEngineClient, fessConfig, doc, id);
            } catch (final Exception inner) {
                logger.warn("[ChunkVector] Failed to record failure state for document; giving up for this run. id={}", id, inner);
                return false;
            }
        }
    }

    /**
     * Extracts and stringifies a document's content field and splits it into chunks. Shared by
     * {@link #processDocument(String)} and {@link #processBatch(List)}. In practice OpenSearch
     * source-map values are always JSON-deserialized String/Number/Boolean/List/Map, whose
     * {@code toString()} is total, so a pathological value cannot throw here in a way that isn't
     * already guarded by each caller's own try/catch. A List value (this document was already
     * chunked into a content array by a prior run) is reconstructed via {@link #stringifyContent}
     * rather than {@code Object#toString()}, which would otherwise produce Java's
     * bracket-comma-space collection format instead of usable re-chunking input.
     *
     * @param fessConfig the fess config
     * @param doc the fetched document map
     * @param id the document ID (log context only)
     * @return the list of chunks, or null if the content is blank, the chunker produced no chunks, or the
     *         chunk count exceeds {@link #getMaxChunksPerDocument()} -- in every null case the caller marks
     *         the document terminally skipped via {@link #markSkipped}
     */
    private List<String> extractChunks(final FessConfig fessConfig, final Map<String, Object> doc, final String id) {
        final Object contentObj = doc.get(fessConfig.getIndexFieldContent());
        final String content = stringifyContent(contentObj);
        if (StringUtil.isBlank(content)) {
            if (logger.isInfoEnabled()) {
                logger.info("[ChunkVector] Blank content, skipping. id={}", id);
            }
            return null;
        }
        final List<String> chunks = splitContent(content);
        if (chunks.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("[ChunkVector] Chunker produced no chunks, skipping. id={}", id);
            }
            return null;
        }
        final int maxChunks = getMaxChunksPerDocument();
        if (chunks.size() > maxChunks) {
            logger.warn(
                    "[ChunkVector] Document produced {} chunks, exceeding the {}={} cap; marking it skipped and leaving "
                            + "its content intact for keyword search. Raise {} and recrawl to include it in semantic chunking. id={}",
                    chunks.size(), MAX_CHUNKS_PER_DOCUMENT_PROPERTY, maxChunks, MAX_CHUNKS_PER_DOCUMENT_PROPERTY, id);
            return null;
        }
        return chunks;
    }

    /**
     * Processes a batch of documents, embedding all of their chunks in a single
     * {@link #embedChunks(List)} call spanning every document in the batch, rather than one
     * embedding call per document. Each document's fetch is still performed individually (only
     * the embed step is batched), and each document's CAS write via {@link #storeSafely} remains
     * fully independent -- a version conflict or write failure for one document never affects the
     * others in the batch.
     *
     * <p>Documents that are not found, have blank content, or produce zero chunks are skipped
     * (recorded {@code false}) exactly as {@link #processDocument(String)} does for a single
     * document, without needing an embedding call. If every document in the batch is skipped this
     * way, {@link #embedChunks(List)} is never called at all.</p>
     *
     * <p>If the single {@link #embedChunks(List)} call spanning the whole batch throws, the batch is
     * not failed as a unit: each document is retried with its own {@link #embedChunks(List)} call
     * ({@link #embedAndStoreIndividually}), so a provider limit tripped by one oversized (or one
     * malformed) document does not drag its healthy siblings to {@code fail}. Only a document that
     * still fails on its individual retry is marked failed via {@link #handleFailure}, mirroring
     * {@link #processDocument(String)}'s own catch block per document.</p>
     *
     * <p>The request text list sent to {@link #embedChunks(List)} is derived directly from a single
     * ordered {@link ChunkRef} list built while chunking (one entry per chunk, in send order,
     * tagging which document and which chunk-index-within-that-document it belongs to), rather than
     * being accumulated in parallel with independently-computed per-document offset bookkeeping.
     * After the embedding call returns, that same {@link ChunkRef} list is walked once, positionally
     * paired against the returned vectors, and each vector is written directly into its owning
     * document's own pre-sized vector slot -- so request-order and response-order are associated in
     * exactly one place, and a document's reassembled vectors cannot silently drift out of sync with
     * its own chunk list the way independently-computed {@code {start,end}} integer ranges could.
     * This closes a purely internal bookkeeping risk; it does not (and cannot) verify that the
     * embedding provider's HTTP response itself preserves request order -- see {@link #embedChunks}.</p>
     *
     * @param ids the OpenSearch document {@code _id}s to process as one batch
     * @return a map from each input ID to whether it was successfully processed (or a terminal
     *         failure durably recorded), using the same boolean semantics as
     *         {@link #processDocument(String)}'s return value
     */
    public Map<String, Boolean> processBatch(final List<String> ids) {
        final FessConfig fessConfig = getFessConfigForContentField();
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        final Map<String, Boolean> results = new HashMap<>();
        final List<BatchEntry> entries = new ArrayList<>();
        final List<ChunkRef> chunkRefs = new ArrayList<>();

        for (final String id : ids) {
            final Map<String, Object> doc;
            try {
                doc = fetchDocument(searchEngineClient, fessConfig, id);
            } catch (final Exception e) {
                logger.warn("[ChunkVector] Failed to fetch document, skipping for this run. id={}", id, e);
                results.put(id, false);
                continue;
            }
            if (doc == null) {
                if (logger.isInfoEnabled()) {
                    logger.info("[ChunkVector] Document not found, skipping. id={}", id);
                }
                results.put(id, false);
                continue;
            }
            final List<String> chunks;
            try {
                chunks = extractChunks(fessConfig, doc, id);
                if (chunks == null) {
                    results.put(id, markSkipped(searchEngineClient, fessConfig, doc, id));
                    continue;
                }
            } catch (final Exception e) {
                logger.warn("[ChunkVector] Failed to process document. id={}", id, e);
                try {
                    results.put(id, handleFailure(searchEngineClient, fessConfig, doc, id));
                } catch (final Exception inner) {
                    logger.warn("[ChunkVector] Failed to record failure state for document; giving up for this run. id={}", id, inner);
                    results.put(id, false);
                }
                continue;
            }
            final BatchEntry entry = new BatchEntry(id, doc, chunks);
            entries.add(entry);
            for (int chunkIndex = 0; chunkIndex < chunks.size(); chunkIndex++) {
                chunkRefs.add(new ChunkRef(entry, chunkIndex, chunks.get(chunkIndex)));
            }
        }

        if (entries.isEmpty()) {
            return results;
        }

        final List<String> allChunks = new ArrayList<>(chunkRefs.size());
        for (final ChunkRef ref : chunkRefs) {
            allChunks.add(ref.chunkText);
        }

        final List<float[]> vectors;
        try {
            vectors = embedChunks(allChunks);
            if (vectors.size() != allChunks.size()) {
                throw new EmbeddingException("Embedding count mismatch: chunks=" + allChunks.size() + ", vectors=" + vectors.size());
            }
        } catch (final Exception e) {
            // The single batched embed call spanning every document in this batch failed. A provider's
            // request-size / token / timeout limit can make one oversized (or one malformed) document
            // fail the whole combined call, so do NOT drag every document in the batch into a shared
            // failure. Fall back to embedding each document on its own: healthy documents still
            // succeed, and only a genuinely failing document is marked failed.
            // Each fallback store goes through the same CAS-aware storeSafely() path, so a real
            // version conflict on an individual retry is handled coherently rather than bypassed.
            logger.warn("[ChunkVector] Batch embedding of {} document(s) failed; retrying each document individually.", entries.size(), e);
            for (final BatchEntry entry : entries) {
                results.put(entry.id, embedAndStoreIndividually(searchEngineClient, fessConfig, entry));
            }
            return results;
        }

        // The single point where request-order (chunkRefs, built directly from each document's own
        // chunk list, in the exact order sent to embedChunks) and response-order (vectors, returned
        // by that one combined call) are paired: each chunk's own recorded chunkIndex places its
        // vector directly into that document's pre-sized slot, rather than a document's whole vector
        // range being sliced out via separately-computed {start,end} offsets.
        for (int i = 0; i < chunkRefs.size(); i++) {
            final ChunkRef ref = chunkRefs.get(i);
            ref.entry.vectors.set(ref.chunkIndex, vectors.get(i));
        }

        for (final BatchEntry entry : entries) {
            try {
                if (!hasAllVectorsAssigned(entry)) {
                    throw new EmbeddingException("Per-document embedding count mismatch after batch reassembly: id=" + entry.id
                            + ", chunks=" + entry.chunks.size());
                }
                results.put(entry.id, storeChunkedDocument(searchEngineClient, fessConfig, entry, entry.vectors));
            } catch (final Exception e) {
                logger.warn("[ChunkVector] Failed to store document after batch embedding. id={}", entry.id, e);
                results.put(entry.id, recordFailure(searchEngineClient, fessConfig, entry));
            }
        }
        return results;
    }

    /**
     * Checks whether every chunk-slot in this document's {@link BatchEntry#vectors} was actually
     * assigned a vector by the pairing loop in {@link #processBatch}. Since {@link BatchEntry#vectors}
     * is pre-sized to exactly {@code entry.chunks.size()} at construction and never resized, this is a
     * stronger check than merely comparing sizes (which would always trivially pass): it catches a
     * chunk-index that was never paired with a response vector, which a mere count comparison would
     * miss if, hypothetically, some other slot were paired twice.
     *
     * @param entry the batch entry to check
     * @return true if every slot in {@code entry.vectors} is non-null
     */
    private boolean hasAllVectorsAssigned(final BatchEntry entry) {
        for (final float[] vector : entry.vectors) {
            if (vector == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Embeds a single document's own chunks and stores the result. Used by {@link #processBatch}'s
     * fallback when the batched embed call for the whole batch failed. Mirrors
     * {@link #processDocument(String)}'s per-document embed+store+failure handling: this document's
     * own embed failure, embedding-count mismatch, or (post-{@link #storeSafely}) non-conflict store
     * failure is routed through {@link #recordFailure} individually, so one document's persistent
     * failure never affects its siblings.
     *
     * @param searchEngineClient the search engine client
     * @param fessConfig the fess config
     * @param entry the batch entry (originally-fetched doc + its own chunk list)
     * @return true if the document was stored (or a terminal failure durably recorded); false otherwise
     */
    private boolean embedAndStoreIndividually(final SearchEngineClient searchEngineClient, final FessConfig fessConfig,
            final BatchEntry entry) {
        try {
            final List<float[]> vectors = embedChunks(entry.chunks);
            if (vectors.size() != entry.chunks.size()) {
                throw new EmbeddingException("Embedding count mismatch: chunks=" + entry.chunks.size() + ", vectors=" + vectors.size());
            }
            return storeChunkedDocument(searchEngineClient, fessConfig, entry, vectors);
        } catch (final Exception e) {
            logger.warn("[ChunkVector] Failed to embed/store document individually after batch embedding failed. id={}", entry.id, e);
            return recordFailure(searchEngineClient, fessConfig, entry);
        }
    }

    /**
     * Builds this document's vector list and CAS-writes it back as a successfully-chunked document.
     * Shared by {@link #processBatch}'s batched-success loop (vectors reassembled via {@link ChunkRef})
     * and its per-document fallback (vectors from a single-document embed call). Any non-conflict
     * store failure is propagated so the caller can route it through {@link #recordFailure}.
     *
     * @param searchEngineClient the search engine client
     * @param fessConfig the fess config
     * @param entry the batch entry (originally-fetched doc + its own chunk list)
     * @param vectors this document's embedding vectors, in chunk order
     * @return true if the CAS write succeeded; false if it lost a CAS race
     */
    private boolean storeChunkedDocument(final SearchEngineClient searchEngineClient, final FessConfig fessConfig, final BatchEntry entry,
            final List<float[]> vectors) {
        validateVectorDimensions(vectors);
        final List<Map<String, Object>> vectorList = new ArrayList<>(vectors.size());
        for (final float[] vector : vectors) {
            final Map<String, Object> vectorMap = new HashMap<>();
            vectorMap.put(VECTOR_SUBFIELD, vector);
            vectorList.add(vectorMap);
        }
        // Mutate a COPY, never the shared entry.doc map -- see processDocument()'s identical rationale
        // for why the failure path must operate on the original fetch.
        final Map<String, Object> updatedDoc = new HashMap<>(entry.doc);
        updatedDoc.put(fessConfig.getIndexFieldContent(), entry.chunks);
        updatedDoc.put(Constants.CONTENT_CHUNK_VECTOR_FIELD, vectorList);
        updatedDoc.put(Constants.CONTENT_CHUNK_STATUS_FIELD, Constants.DONE);
        return storeSafely(searchEngineClient, fessConfig, updatedDoc, entry.id);
    }

    /**
     * Records a per-document processing failure via {@link #handleFailure} (its own CAS write),
     * never letting handleFailure's own write failure escape --
     * {@link #processBatch} must resolve every document to a plain boolean.
     *
     * @param searchEngineClient the search engine client
     * @param fessConfig the fess config
     * @param entry the batch entry whose failure is being recorded
     * @return the result of {@link #handleFailure}, or false if even that write failed
     */
    private boolean recordFailure(final SearchEngineClient searchEngineClient, final FessConfig fessConfig, final BatchEntry entry) {
        try {
            return handleFailure(searchEngineClient, fessConfig, entry.doc, entry.id);
        } catch (final Exception inner) {
            logger.warn("[ChunkVector] Failed to record failure state for document; giving up for this run. id={}", entry.id, inner);
            return false;
        }
    }

    /**
     * One document's originally-fetched document map, its own chunk list, and the vectors
     * reassembled for it from a batch's single combined {@link #embedChunks(List)} call. {@code
     * vectors} is pre-sized to {@code chunks.size()} at construction (every slot initially {@code
     * null}) and filled in by {@link #processBatch}'s single pairing loop via {@link ChunkRef#chunkIndex}
     * -- this document's slice of the batch is never computed via separate {@code {start,end}} integer
     * offset arithmetic. Used only by {@link #processBatch}.
     */
    private static final class BatchEntry {
        final String id;
        final Map<String, Object> doc;
        final List<String> chunks;
        final List<float[]> vectors;

        BatchEntry(final String id, final Map<String, Object> doc, final List<String> chunks) {
            this.id = id;
            this.doc = doc;
            this.chunks = chunks;
            this.vectors = new ArrayList<>(chunks.size());
            for (int i = 0; i < chunks.size(); i++) {
                this.vectors.add(null);
            }
        }
    }

    /**
     * One chunk within a {@link #processBatch} call's single combined embedding request: which
     * {@link BatchEntry} (document) it belongs to, its index within that document's own chunk list,
     * and its text. One {@link ChunkRef} is created per chunk, in the exact order its text is sent to
     * {@link #embedChunks(List)}; after that call returns, this same list is walked once, positionally
     * paired against the returned vectors, to place each vector directly into its owning document's
     * {@link BatchEntry#vectors} slot -- the single place where request-order and response-order are
     * associated for the whole batch.
     */
    private static final class ChunkRef {
        final BatchEntry entry;
        final int chunkIndex;
        final String chunkText;

        ChunkRef(final BatchEntry entry, final int chunkIndex, final String chunkText) {
            this.entry = entry;
            this.chunkIndex = chunkIndex;
            this.chunkText = chunkText;
        }
    }

    /**
     * Fetches the current document by ID, populating {@code _seq_no}/{@code _primary_term}.
     *
     * @param searchEngineClient the search engine client
     * @param fessConfig the fess config
     * @param id the document ID
     * @return the document map, or null if not found
     */
    protected Map<String, Object> fetchDocument(final SearchEngineClient searchEngineClient, final FessConfig fessConfig, final String id) {
        return searchEngineClient.getDocument(fessConfig.getIndexDocumentUpdateIndex(), builder -> {
            builder.setQuery(QueryBuilders.idsQuery().addIds(id));
            // Request the real _seq_no/_primary_term. Without this the search returns the
            // "unassigned" sentinels (SequenceNumbers.UNASSIGNED_SEQ_NO=-2, UNASSIGNED_PRIMARY_TERM=0),
            // which SearchEngineClient#store() forwards to setIfSeqNo/setIfPrimaryTerm as a no-op CAS
            // (an unconditional overwrite) -- silently disabling the compare-and-swap that guards
            // against a concurrent recrawl overwriting this document between fetch and store.
            builder.seqNoAndPrimaryTerm(true);
            return true;
        }).orElse(null);
    }

    /**
     * Marks a document terminally failed ({@code content_chunk_status="fail"})
     * after a processing failure, writing via the same CAS path as the success
     * path -- unless the embedding provider is unreachable right now, in which
     * case the document is left pending (no write at all) so a transient
     * outage never stamps the corpus failed.
     *
     * @param searchEngineClient the search engine client
     * @param fessConfig the fess config
     * @param doc the document map read before the failure occurred
     * @param id the document ID
     * @return the result of {@link #storeSafely}
     */
    protected boolean handleFailure(final SearchEngineClient searchEngineClient, final FessConfig fessConfig, final Map<String, Object> doc,
            final String id) {
        if (!isEmbeddingClientAvailable()) {
            // The embedding provider is unreachable RIGHT NOW -- it died mid-run, after
            // ChunkVectorJob#execute()'s pre-flight availability gate had already passed. Every
            // pending document's embed call is now failing purely because of this transient outage,
            // not because the document itself is unprocessable. Marking these outage failures would
            // durably stamp the whole corpus content_chunk_status=fail (which
            // ChunkVectorJob#buildPendingQuery then excludes forever, recoverable only by a full
            // recrawl). Leave the document pending instead -- no status write, no CAS write at all --
            // so it is retried unchanged once the provider recovers; the next run's pre-flight gate
            // then skips the run entirely while the outage persists. A genuine poison document
            // (HTTP 400, count/dimension mismatch) fails while the provider is still reachable, so
            // available() is true and it still flows through the failure write below. Re-probing the
            // same cached availability the pre-flight gate reads keeps the two gates consistent; the
            // periodic refresher bounds how long the cache can lag a real outage.
            if (logger.isInfoEnabled()) {
                logger.info("[ChunkVector] Embedding provider is unavailable; leaving document pending without marking it failed. id={}",
                        id);
            }
            return false;
        }
        doc.put(Constants.CONTENT_CHUNK_STATUS_FIELD, Constants.FAIL);
        return storeSafely(searchEngineClient, fessConfig, doc, id);
    }

    /**
     * Marks a document terminally "skipped": it has no embeddable content (blank/whitespace), produced
     * zero chunks, or produced more chunks than {@link #getMaxChunksPerDocument()}. Writes
     * {@code content_chunk_status="skipped"} — a DISTINCT terminal status, never "done" (the RAG read
     * path keys on "done" to treat {@code content} as a chunk array) — via the same CAS path as the
     * success/failure writes, and never overwrites the document's content, so
     * it stays available to keyword search and display. This takes the document out of
     * {@code ChunkVectorJob#buildPendingQuery}'s pending set (which excludes any document whose
     * {@code content_chunk_status} is set), so it is no longer re-fetched every run — preventing
     * perpetual reprocessing and, once such documents exceed the per-run cap, starvation of genuinely
     * pending documents. A later recrawl clears the status (full {@code _source} replace), so a document
     * whose content later becomes chunkable is reprocessed.
     *
     * <p>Never throws and never routes into {@link #handleFailure}: "skipped" is a permanent property of
     * the document's current content, not a transient failure, so it must not be marked failed. A lost CAS
     * race (concurrent recrawl) or any other store error simply leaves the document pending, to be
     * re-evaluated on the next run.</p>
     *
     * @param searchEngineClient the search engine client
     * @param fessConfig the fess config
     * @param doc the document map read before chunking (carries {@code _seq_no}/{@code _primary_term})
     * @param id the document ID
     * @return true if the skip status was durably recorded; false if it lost a CAS race or the store failed
     */
    protected boolean markSkipped(final SearchEngineClient searchEngineClient, final FessConfig fessConfig, final Map<String, Object> doc,
            final String id) {
        final Map<String, Object> updatedDoc = new HashMap<>(doc);
        updatedDoc.put(Constants.CONTENT_CHUNK_STATUS_FIELD, Constants.SKIPPED);
        try {
            return storeSafely(searchEngineClient, fessConfig, updatedDoc, id);
        } catch (final Exception e) {
            logger.warn("[ChunkVector] Failed to mark document skipped; leaving it pending for the next run. id={}", id, e);
            return false;
        }
    }

    /**
     * Writes {@code doc} back via {@link SearchEngineClient#store}, treating
     * a genuine version-conflict {@link SearchEngineClientException} as an
     * expected, self-healing skip: logged at INFO, not WARN/ERROR, per
     * design doc §8.2.
     *
     * <p>{@link SearchEngineClient#store} wraps <em>every</em>
     * {@code OpenSearchException} into {@link SearchEngineClientException} --
     * not just version conflicts (mapping errors, a read-only index from
     * disk-pressure, timeouts, and circuit-breaker trips all surface the
     * same way). Only an exception whose cause chain genuinely looks like a
     * version conflict ({@link #isVersionConflict}) is swallowed here; any
     * other {@link SearchEngineClientException} is rethrown so it reaches
     * the same failure-handling path as a chunking/embedding error (i.e.
     * marked failed via {@link #handleFailure}, logged at WARN) instead of
     * being silently treated as benign forever.
     *
     * @param searchEngineClient the search engine client
     * @param fessConfig the fess config
     * @param doc the document map to store (must carry {@code _seq_no}/{@code _primary_term})
     * @param id the document ID (log context only)
     * @return true if the write succeeded; false if it lost a CAS race
     * @throws SearchEngineClientException if the store failed for a reason other than a version conflict
     */
    protected boolean storeSafely(final SearchEngineClient searchEngineClient, final FessConfig fessConfig, final Map<String, Object> doc,
            final String id) {
        try {
            return searchEngineClient.store(fessConfig.getIndexDocumentUpdateIndex(), doc);
        } catch (final SearchEngineClientException e) {
            if (!isVersionConflict(e)) {
                throw e;
            }
            if (logger.isInfoEnabled()) {
                logger.info("[ChunkVector] Skipped due to concurrent update (version conflict). id={}", id);
            }
            return false;
        }
    }

    /**
     * Tests whether the given throwable (or any of its causes) represents an
     * OpenSearch optimistic-concurrency conflict. Walks the cause chain and
     * inspects class name / message so this does not have to depend on a
     * specific OpenSearch exception type -- {@link SearchEngineClient#store}
     * wraps every {@code OpenSearchException} into
     * {@link SearchEngineClientException} indiscriminately, so the wrapper
     * type alone cannot distinguish a version conflict from any other
     * OpenSearch write failure. Mirrors {@code UserService.isVersionConflict}.
     *
     * <p>The message check is load-bearing, not merely a belt-and-braces backup: over the HTTP
     * client (fesen-httpclient, Fess's production transport), a version conflict is NOT reconstructed
     * as a real {@code org.opensearch.index.engine.VersionConflictEngineException}. The client parses
     * the server's error body into a generic {@code OpenSearchStatusException} whose class name does
     * not end with {@code VersionConflictEngineException} but whose message is
     * {@code "OpenSearch exception [type=version_conflict_engine_exception, reason=...]"}. The
     * class-name-suffix check therefore only catches the in-process transport-client shape; the HTTP
     * shape is caught solely by this message check.
     *
     * <p>The class-name-suffix check runs on every frame, including {@code t}
     * itself -- that check is safe there, since it inspects the exception's
     * Java class, not attacker/crawl-controlled data. The message check,
     * however, is deliberately skipped on {@code t} itself and only
     * applied starting from {@code t.getCause()}: {@link SearchEngineClient#store}
     * builds its own (outermost) {@link SearchEngineClientException} message
     * as {@code "Failed to store: " + doc}, which embeds the full document
     * map -- including arbitrary crawled {@code content} text. Checking the
     * message on that outer frame would let a document whose crawled content
     * happens to contain the conflict token get a genuine, unrelated write
     * failure misclassified as a benign conflict. OpenSearch's own
     * server-controlled reason string only ever appears on the wrapped cause,
     * never on this outer wrapper. The check also matches the structured
     * {@code type=version_conflict_engine_exception} token rather than the bare
     * {@code version_conflict_engine_exception} word, so crawled content that
     * merely mentions the word in an unrelated error's reason string does not
     * collide -- the server only ever emits the token in that structured form.
     *
     * @param t the throwable to inspect
     * @return true if {@code t} looks like a version conflict
     */
    protected boolean isVersionConflict(final Throwable t) {
        Throwable cur = t;
        boolean checkMessage = false;
        while (cur != null) {
            final String name = cur.getClass().getName();
            if (name.endsWith("VersionConflictEngineException")) {
                return true;
            }
            if (checkMessage) {
                final String msg = cur.getMessage();
                if (msg != null && msg.contains("type=version_conflict_engine_exception")) {
                    return true;
                }
            }
            final Throwable next = cur.getCause();
            if (next == cur) {
                break;
            }
            cur = next;
            checkMessage = true;
        }
        return false;
    }

    /**
     * Converts a {@code content} field value read back from OpenSearch into a
     * plain string suitable for re-chunking. Handles the case where the
     * document was already processed by a prior run and its {@code content}
     * is now a chunk array rather than a single string -- joining the
     * elements directly avoids Java's bracket-comma-space {@code List#toString()}
     * format, which would be nonsense as re-chunking input. This path is not
     * reachable in practice today ({@code ChunkVectorJob#buildPendingQuery()}
     * excludes any document with {@code content_chunk_status} set), but is
     * kept defensive for a future re-embed maintenance job.
     *
     * @param contentObj the raw {@code content} field value
     * @return the reconstructed content string
     */
    protected String stringifyContent(final Object contentObj) {
        if (contentObj == null) {
            return StringUtil.EMPTY;
        }
        if (contentObj instanceof String) {
            return (String) contentObj;
        }
        if (contentObj instanceof List<?>) {
            final StringBuilder sb = new StringBuilder();
            for (final Object element : (List<?>) contentObj) {
                sb.append(String.valueOf(element));
            }
            return sb.toString();
        }
        return contentObj.toString();
    }

    /**
     * Splits content into chunks via {@link ChunkerManager}. Overridable seam for tests.
     *
     * @param content the document content
     * @return the list of chunks
     */
    protected List<String> splitContent(final String content) {
        return ComponentUtil.getComponent(ChunkerManager.class).split(content);
    }

    /**
     * Embeds chunks via {@link EmbeddingClientManager}. Overridable seam for tests.
     *
     * @param chunks the chunks to embed
     * @return the embedding vectors
     */
    protected List<float[]> embedChunks(final List<String> chunks) {
        return ComponentUtil.getComponent(EmbeddingClientManager.class).embedDocuments(chunks);
    }

    /**
     * Reports whether the configured embedding provider is currently available, so
     * {@link #handleFailure} can tell a transient provider outage (leave the document pending) apart
     * from a genuine per-document failure (mark it failed). Delegates to
     * {@link EmbeddingClientManager#available()} -- the same cached availability the RAG read path and
     * {@link org.codelibs.fess.job.ChunkVectorJob}'s pre-flight gate use. Overridable seam for tests.
     *
     * @return true if the embedding provider is available
     */
    protected boolean isEmbeddingClientAvailable() {
        return ComponentUtil.getComponent(EmbeddingClientManager.class).available();
    }

    /**
     * Fails the document loudly if any embedding vector's length does not match the configured
     * {@code content_chunker.embedding.dimension} (when that dimension is a valid positive integer).
     * Without this, a provider that returns a wrong-length vector (a model/config mismatch) would be
     * stored and rejected by OpenSearch's {@code knn_vector} dimension check with only
     * a cryptic mapping error; failing here instead produces a clear
     * diagnostic and still routes the document through the normal failure handling (the caller's
     * catch -> {@code handleFailure}/{@code recordFailure} path). Fails open (no check) when the
     * dimension is unset/unparsable, mirroring {@link #rewriteMapping(String)} and
     * {@link #checkDimensionConsistency()}. Complements the {@code vectors.size() != chunks.size()}
     * count check, which does not inspect each vector's length.
     *
     * @param vectors the embedding vectors to validate
     * @throws EmbeddingException if any vector's length differs from the configured dimension
     */
    protected void validateVectorDimensions(final List<float[]> vectors) {
        final int expectedDimension = getConfiguredEmbeddingDimension();
        if (expectedDimension <= 0) {
            return;
        }
        for (int i = 0; i < vectors.size(); i++) {
            final float[] vector = vectors.get(i);
            if (vector == null || vector.length != expectedDimension) {
                throw new EmbeddingException("Embedding vector dimension mismatch: expected " + expectedDimension + " but vector at index "
                        + i + " has length " + (vector == null ? "null" : Integer.toString(vector.length)));
            }
        }
    }

    /**
     * Parses {@code content_chunker.max_chunks_per_document} (default
     * {@link #DEFAULT_MAX_CHUNKS_PER_DOCUMENT}), clamped to at least 1. A document
     * that splits into more chunks than this is marked terminally skipped by {@link #extractChunks}
     * (its content preserved for keyword search) rather than embedded and stored as one enormous
     * document: {@code content_chunk_vector} is a nested field, so more than the index's
     * nested_objects limit (OpenSearch default 10000) of chunks would be rejected at store time and
     * turn into a permanent failure.
     *
     * @return the configured cap, at least 1
     */
    protected int getMaxChunksPerDocument() {
        return Math.max(1, getConfigInt(MAX_CHUNKS_PER_DOCUMENT_PROPERTY, DEFAULT_MAX_CHUNKS_PER_DOCUMENT));
    }

    private int getConfigInt(final String key, final int defaultValue) {
        final String value = ComponentUtil.getFessConfig().getSystemProperty(key, null);
        if (value != null) {
            try {
                return Integer.parseInt(value.trim());
            } catch (final NumberFormatException e) {
                logger.warn("[ChunkVector] Invalid integer for {}: {}", key, value);
            }
        }
        return defaultValue;
    }

    /**
     * Gets the {@link FessConfig}, isolated behind a seam name distinct from
     * {@code ComponentUtil.getFessConfig()} calls elsewhere in this class
     * purely so tests can assert on the exact call site if ever needed; the
     * default implementation just delegates.
     *
     * @return the current FessConfig
     */
    protected FessConfig getFessConfigForContentField() {
        return ComponentUtil.getFessConfig();
    }
}
