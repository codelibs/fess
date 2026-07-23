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
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

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
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

import jakarta.annotation.PostConstruct;

/**
 * Orchestrates the ingestion half of content-chunk vector search: registers
 * the OpenSearch document-mapping rewrite rule that adds the
 * {@code content_chunk_vector} field, performs the per-document
 * CAS chunk+embed read-modify-write ({@link #processDocument(String)} /
 * {@link #processBatch(List)}), and owns the whole scheduled processing run
 * ({@link #executeChunkVectorProcessing()}): pending-document scroll, batch
 * partitioning, bounded-concurrency fan-out, and the per-run mode resolution
 * between full embedding, chunk-only mode (embedding configured off:
 * {@code content_chunker.embedding.name=none} or no client registered), and
 * skipping the run entirely (embedding configured but transiently
 * unreachable).
 */
public class ChunkVectorHelper {

    private static final Logger logger = LogManager.getLogger(ChunkVectorHelper.class);

    /** Sub-field of {@link Constants#CONTENT_CHUNK_VECTOR_FIELD} holding the knn_vector value. */
    public static final String VECTOR_SUBFIELD = "vector";

    /** System property key enabling the semantic chunk search integration. */
    public static final String SEMANTIC_SEARCH_ENABLED_PROPERTY = "content_chunker.search.enabled";

    /** System property key for the ANN method name baked into the knn_vector mapping. */
    public static final String KNN_METHOD_PROPERTY = "content_chunker.search.knn.method";

    /** System property key for the ANN engine baked into the knn_vector mapping. */
    public static final String KNN_ENGINE_PROPERTY = "content_chunker.search.knn.engine";

    /** System property key for the ANN space type baked into the knn_vector mapping. */
    public static final String KNN_SPACE_TYPE_PROPERTY = "content_chunker.search.knn.space_type";

    /** System property key for the HNSW m parameter baked into the knn_vector mapping. */
    public static final String KNN_PARAM_M_PROPERTY = "content_chunker.search.knn.param.m";

    /** System property key for the HNSW ef_construction parameter baked into the knn_vector mapping. */
    public static final String KNN_PARAM_EF_CONSTRUCTION_PROPERTY = "content_chunker.search.knn.param.ef_construction";

    /** System property key for the maximum number of chunks a single document may produce. */
    protected static final String MAX_CHUNKS_PER_DOCUMENT_PROPERTY = "content_chunker.max_chunks_per_document";

    /** Default value of {@link #MAX_CHUNKS_PER_DOCUMENT_PROPERTY}. */
    protected static final int DEFAULT_MAX_CHUNKS_PER_DOCUMENT = 1000;

    /** Number of document IDs fetched per scroll batch. */
    protected static final int SCROLL_SIZE = 100;

    /**
     * System property key for the bounded concurrency of the run's embedding calls: the number of
     * concurrently in-flight batches (each up to {@link #JOB_BULK_SIZE_PROPERTY} documents).
     */
    protected static final String JOB_CONCURRENCY_PROPERTY = "content_chunker.job.concurrency";

    /** System property key for the number of documents grouped into a single embedding call. */
    protected static final String JOB_BULK_SIZE_PROPERTY = "content_chunker.job.bulk_size";

    /**
     * System property key for the maximum number of pending document IDs collected (and therefore
     * processed) in a single run; bounds the memory the scroll phase holds.
     */
    protected static final String JOB_MAX_DOCUMENTS_PER_RUN_PROPERTY = "content_chunker.job.max_documents_per_run";

    /** Default value of {@link #JOB_CONCURRENCY_PROPERTY}. */
    protected static final int DEFAULT_JOB_CONCURRENCY = 2;

    /** Default value of {@link #JOB_BULK_SIZE_PROPERTY}. */
    protected static final int DEFAULT_JOB_BULK_SIZE = 20;

    /** Default value of {@link #JOB_MAX_DOCUMENTS_PER_RUN_PROPERTY}. */
    protected static final int DEFAULT_JOB_MAX_DOCUMENTS_PER_RUN = 10000;

    /**
     * Default constructor.
     */
    public ChunkVectorHelper() {
        // Default constructor
    }

    /**
     * Executes one full chunk-vector processing run: resolves the run mode, scroll-collects
     * pending document IDs, partitions them into {@code content_chunker.job.bulk_size}-sized
     * batches, then fans those batches out across a bounded thread pool via
     * {@link #processBatch(List, boolean)}. This is the single entry the
     * {@link org.codelibs.fess.exec.ChunkVectorIndexer} child process (launched by
     * {@link org.codelibs.fess.job.ChunkVectorJob}) runs.
     *
     * <p>Run-mode resolution (only reached when {@link #isContentChunkerEnabled()}):</p>
     * <ul>
     * <li><b>chunk-only</b> -- embedding is configured off ({@code content_chunker.embedding.name}
     * is {@code "none"}, or no matching embedding client is registered; probed quietly via
     * {@link #isEmbeddingConfigured()}): content is chunked and written back as an array with
     * {@code content_chunk_status="chunked"}, and NO vector field is written.</li>
     * <li><b>skip</b> -- embedding is configured but the provider's liveness ping fails right now
     * (a transient outage): the whole run is skipped so pending documents stay pending. A
     * transient outage must NEVER flip processing into chunk-only mode, which would rewrite the
     * corpus without vectors.</li>
     * <li><b>embedding</b> -- embedding is configured and available: chunk + embed + write vectors
     * with {@code content_chunk_status="done"}, including the upgrade path for documents a prior
     * chunk-only run left {@code "chunked"} (their stored array elements are embedded directly,
     * never re-chunked).</li>
     * </ul>
     *
     * <p>The dimension-consistency and vector-mapping-presence guards
     * ({@link #checkDimensionConsistency()}, {@link #checkVectorMappingReady()}) run only when
     * embedding is active -- in chunk-only mode no vector is written, so neither can apply.</p>
     *
     * <p>A pending-document scroll failure is deliberately NOT caught here: it propagates to the
     * caller so the {@link org.codelibs.fess.exec.ChunkVectorIndexer} child process exits non-zero
     * and the parent {@link org.codelibs.fess.job.ChunkVectorJob} raises a
     * {@code JobProcessingException} -- unlike the intentional skips above, which are healthy
     * outcomes returned as a summary message (exit 0).</p>
     *
     * <p>No in-code overlap guard is present, and the scheduler serializes runs of the owning job
     * only within a single JVM. Every scheduled job is registered by {@code JobHelper#register}
     * with {@code uniqueBy(id)} plus {@code fessConfig.getSchedulerConcurrentExecModeAsEnum()}
     * (default {@code scheduler.concurrent.exec.mode=QUIT}), and all three
     * {@code JobConcurrentExec} modes forbid concurrent execution of the same unique job -- but
     * that registry is per-process. In a multi-node deployment where the job's scheduler target is
     * {@code all}, every Fess node starts its own run concurrently: the CAS write
     * ({@code _seq_no}/{@code _primary_term} conditional index) keeps the outcome correct, yet
     * each node still chunks and embeds the same pending documents, multiplying embedding-provider
     * spend. When running multiple Fess nodes, pin this job to a single node via its scheduler
     * target setting.</p>
     *
     * @return a summary result message (processed/succeeded/failed counts, or the skip reason)
     */
    public String executeChunkVectorProcessing() {
        if (!isContentChunkerEnabled()) {
            // content_chunker.enabled is false: the scheduled run must be a complete no-op -- no
            // scroll, no fetch, no chunk, no write. Without this early return, enabling the
            // "Content Chunk Vector Indexer" job (an ordinary Admin > Scheduler action) without also
            // setting the separate content_chunker.enabled system property would run the full chunk
            // pipeline. Logged only at DEBUG so a site that simply does not use this feature never
            // sees WARN/INFO spam on each run.
            if (logger.isDebugEnabled()) {
                logger.debug("Content chunking is disabled; skipping chunk-vector processing.");
            }
            return "Content chunking is disabled. Skipped.";
        }

        final boolean embeddingActive = isEmbeddingConfigured();
        if (embeddingActive) {
            if (!isEmbeddingClientAvailable()) {
                // The embedding provider is configured but unreachable (a transient outage). Skip
                // the whole run WITHOUT scrolling/fetching/processing so pending documents stay
                // pending -- and, critically, do NOT fall back to chunk-only mode: a transient
                // outage must never rewrite the corpus without vectors. Logged at WARN so a genuine
                // outage is visible; the documents are retried once the provider recovers.
                logger.warn("Embedding provider is not available; skipping chunk-vector processing to keep pending documents pending.");
                return "Embedding provider is not available. Skipped.";
            }
            if (!checkDimensionConsistency()) {
                // A confirmed embedding-dimension mismatch between the configured provider/model and
                // the live content_chunk_vector mapping was already logged at ERROR by
                // checkDimensionConsistency() -- skip this run entirely rather than letting every
                // pending document independently fail against OpenSearch's k-NN dimension-mismatch
                // rejection.
                return "Embedding dimension mismatch detected between the configured embedding provider and the existing "
                        + "content_chunk_vector mapping; skipping this run. See the ERROR log for details.";
            }
            if (!checkVectorMappingReady()) {
                // The live index confirmedly has no content_chunk_vector knn_vector mapping (e.g. it
                // was created while embedding was off, so rewriteMapping never spliced it). Writing
                // vectors now would dynamic-map them as a useless non-knn field -- skip the run; the
                // WARN with the remediation was already logged by checkVectorMappingReady().
                return "The live index has no content_chunk_vector (knn_vector) mapping; skipping this run to avoid "
                        + "dynamic-mapping vectors as a non-knn field. See the WARN log for details.";
            }
        } else if (logger.isInfoEnabled()) {
            logger.info(
                    "[ChunkVector] Embedding is not configured ({} is \"{}\" or no embedding client is registered); running in "
                            + "chunk-only mode: content is chunked and marked \"{}\" without vectors.",
                    AbstractEmbeddingClient.EMBEDDING_NAME_PROPERTY, Constants.NONE, Constants.CHUNKED);
        }

        // A scroll failure is a genuine run failure, not an intentional skip: let it propagate so
        // the ChunkVectorIndexer child process exits non-zero and the parent ChunkVectorJob
        // surfaces it as a JobProcessingException, instead of a healthy-looking summary string.
        final List<String> idList = scrollPendingIds(embeddingActive);

        final AtomicInteger processed = new AtomicInteger();
        final AtomicInteger succeeded = new AtomicInteger();
        final AtomicInteger failed = new AtomicInteger();

        final int bulkSize = Math.max(1, getJobBulkSize());
        final List<List<String>> batches = partitionIds(idList, bulkSize);

        final int concurrency = Math.max(1, getConcurrency());
        final ExecutorService executor = Executors.newFixedThreadPool(concurrency);
        try {
            final List<Future<?>> futures = new ArrayList<>(batches.size());
            for (final List<String> batch : batches) {
                futures.add(executor.submit(() -> {
                    try {
                        final Map<String, Boolean> results = processBatch(batch, embeddingActive);
                        for (final String id : batch) {
                            processed.incrementAndGet();
                            if (Boolean.TRUE.equals(results.get(id))) {
                                succeeded.incrementAndGet();
                            } else {
                                failed.incrementAndGet();
                            }
                        }
                    } catch (final Exception e) {
                        for (final String id : batch) {
                            processed.incrementAndGet();
                            failed.incrementAndGet();
                        }
                        logger.warn("Failed to process a batch of {} document(s). ids={}", batch.size(), batch, e);
                    }
                }));
            }
            for (final Future<?> future : futures) {
                try {
                    future.get();
                } catch (final Exception e) {
                    logger.warn("Failed to wait for a batch-processing task.", e);
                }
            }
        } finally {
            executor.shutdown();
        }

        return "Processed " + processed.get() + " documents. Succeeded: " + succeeded.get() + ", Failed/Skipped: " + failed.get() + ".";
    }

    /**
     * Quietly reports whether embedding is configured at all -- {@code content_chunker.embedding.name}
     * is not {@code "none"} AND a matching client is registered -- WITHOUT a liveness ping and
     * WITHOUT the per-miss WARN {@code EmbeddingClientManager#getClient()} logs, so a chunk-only
     * run probing this every execution stays quiet. Deliberately excludes availability: that
     * distinguishes "configured off" (chunk-only mode) from "configured but transiently down"
     * (skip the run). Overridable seam for tests.
     *
     * @return true if an embedding client is configured and registered
     */
    protected boolean isEmbeddingConfigured() {
        return ComponentUtil.getComponent(EmbeddingClientManager.class).hasConfiguredClient();
    }

    /**
     * Partitions {@code idList} into consecutive sub-lists of at most {@code bulkSize} elements
     * each (the last partition may be smaller).
     *
     * @param idList the full list of pending document IDs
     * @param bulkSize the maximum number of IDs per partition
     * @return the list of partitions, in order
     */
    protected List<List<String>> partitionIds(final List<String> idList, final int bulkSize) {
        final List<List<String>> batches = new ArrayList<>();
        for (int i = 0; i < idList.size(); i += bulkSize) {
            batches.add(idList.subList(i, Math.min(i + bulkSize, idList.size())));
        }
        return batches;
    }

    /**
     * Scroll-queries {@code fessConfig.getIndexDocumentUpdateIndex()} for
     * documents matching {@link #buildPendingQuery(boolean)}, restricted to just
     * the ID field, and collects matching IDs before returning (the
     * scroll context is fully closed by the time this method returns). Uses the
     * update-index alias -- not the search-index alias -- so this run never diverges
     * from this helper's own read/write index during a blue-green
     * crawl/reindex swap, matching the sibling {@code PurgeDocJob}/{@code UpdateLabelJob}
     * convention.
     *
     * <p>Collection stops once {@link #getJobMaxDocumentsPerRun()} IDs have been gathered, bounding
     * the memory a single run holds so a large-corpus first run cannot OOM by materializing the
     * entire pending-ID set at once. The remaining pending documents stay pending and are picked up
     * by the next (idempotent) scheduled run. The cap is enforced by throwing a private
     * {@link ScrollLimitReachedException} from the scroll cursor rather than by returning
     * {@code false} from it: {@code SearchEngineClient#scrollSearch} only stops iterating the
     * <em>current</em> page when the cursor returns {@code false} and then keeps fetching further
     * pages, so a bare {@code false} would drain the whole (potentially millions-of-pages) scroll
     * instead of stopping it -- the throw exits the scroll immediately, with its {@code finally}
     * still releasing the scroll context.</p>
     *
     * @param embeddingActive whether embedding is active for this run (widens the pending query to
     *            include {@code "chunked"} upgrade documents)
     * @return the list of pending document IDs, at most {@link #getJobMaxDocumentsPerRun()} entries
     */
    protected List<String> scrollPendingIds(final boolean embeddingActive) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        final int maxDocuments = Math.max(1, getJobMaxDocumentsPerRun());
        final List<String> idList = new ArrayList<>();
        try {
            searchEngineClient.scrollSearch(fessConfig.getIndexDocumentUpdateIndex(), requestBuilder -> {
                requestBuilder.setQuery(buildPendingQuery(embeddingActive))
                        .setSize(SCROLL_SIZE)
                        .setFetchSource(new String[] { fessConfig.getIndexFieldId() }, null);
                return true;
            }, source -> {
                final Object idObj = source.get(fessConfig.getIndexFieldId());
                if (idObj != null) {
                    idList.add(idObj.toString());
                }
                if (idList.size() >= maxDocuments) {
                    throw new ScrollLimitReachedException();
                }
                return true;
            });
        } catch (final ScrollLimitReachedException e) {
            if (logger.isInfoEnabled()) {
                logger.info("[ChunkVector] Reached the per-run document cap ({}); the remaining pending documents will be "
                        + "processed on the next scheduled run.", maxDocuments);
            }
        }
        return idList;
    }

    /**
     * Builds the query matching documents pending processing for this run's mode:
     * {@code content_chunk_status} absent always matches; when embedding is active, documents a
     * prior chunk-only run left {@code content_chunk_status="chunked"} also match, so they are
     * upgraded in place (their stored chunk arrays embedded directly) once embedding is enabled.
     * In chunk-only mode {@code "chunked"} documents are already in their terminal state for that
     * mode and must NOT be reprocessed every run.
     *
     * @param embeddingActive whether embedding is active for this run
     * @return the query builder
     */
    protected QueryBuilder buildPendingQuery(final boolean embeddingActive) {
        final QueryBuilder statusAbsent =
                QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(Constants.CONTENT_CHUNK_STATUS_FIELD));
        if (!embeddingActive) {
            return statusAbsent;
        }
        return QueryBuilders.boolQuery()
                .should(statusAbsent)
                .should(QueryBuilders.termQuery(Constants.CONTENT_CHUNK_STATUS_FIELD, Constants.CHUNKED))
                .minimumShouldMatch(1);
    }

    /**
     * Internal control-flow signal thrown from {@link #scrollPendingIds(boolean)}'s scroll cursor
     * once {@link #getJobMaxDocumentsPerRun()} document IDs have been collected, to stop the scroll
     * early without materializing the whole corpus. Never escapes {@link #scrollPendingIds(boolean)};
     * stack trace and suppression are disabled since it carries no diagnostic value.
     */
    private static final class ScrollLimitReachedException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        ScrollLimitReachedException() {
            super(null, null, false, false);
        }
    }

    /**
     * Gets the configured run concurrency.
     *
     * @return the value of {@code content_chunker.job.concurrency} (default 2)
     */
    protected int getConcurrency() {
        return getConfigInt(JOB_CONCURRENCY_PROPERTY, DEFAULT_JOB_CONCURRENCY);
    }

    /**
     * Gets the configured number of documents grouped into a single embedding call.
     *
     * @return the value of {@code content_chunker.job.bulk_size} (default 20)
     */
    protected int getJobBulkSize() {
        return getConfigInt(JOB_BULK_SIZE_PROPERTY, DEFAULT_JOB_BULK_SIZE);
    }

    /**
     * Gets the configured maximum number of pending documents collected (and processed) per run.
     *
     * @return the value of {@code content_chunker.job.max_documents_per_run} (default 10000)
     */
    protected int getJobMaxDocumentsPerRun() {
        return getConfigInt(JOB_MAX_DOCUMENTS_PER_RUN_PROPERTY, DEFAULT_JOB_MAX_DOCUMENTS_PER_RUN);
    }

    /**
     * Initializes this helper: registers the mapping rewrite rule.
     */
    @PostConstruct
    public void init() {
        registerMappingRewriteRule();
        registerSettingRewriteRule();
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
     * Registers the document-setting rewrite rule that turns on {@code index.knn}
     * when semantic chunk search is enabled, so knn_vector fields are indexed into
     * an ANN structure (HNSW) and the {@code knn} query can be used at scale.
     * Mirrors {@code SemanticSearchHelper.init()}'s {@code addDocumentSettingRewriteRule}
     * string-splice pattern.
     */
    protected void registerSettingRewriteRule() {
        final SearchEngineClient client = ComponentUtil.getSearchEngineClient();
        client.addDocumentSettingRewriteRule(this::rewriteSetting);
    }

    /**
     * Splices {@code "knn": true} immediately before the literal {@code "codec":}
     * key in the document index settings JSON. A no-op unless content chunking,
     * a valid embedding dimension, and {@code content_chunker.search.enabled} are
     * all configured — plain chunk/RAG deployments keep the stock settings and pay
     * no ANN indexing cost.
     *
     * @param source the original document settings JSON source
     * @return the rewritten settings JSON source
     */
    protected String rewriteSetting(final String source) {
        if (!isContentChunkerEnabled() || !isSemanticSearchEnabled() || getConfiguredEmbeddingDimension() <= 0) {
            return source;
        }
        // Each splice is guarded independently so the rule stays idempotent and composes with
        // another rule (e.g. an older plugin) that already enabled index.knn -- strict
        // duplicate-key detection would otherwise fail the whole index creation, and skipping
        // both splices together would leave derived source enabled on such indexes.
        String result = source;
        if (!result.contains("\"knn\": true")) {
            final String spliced = result.replace("\"codec\":", "\"knn\": true,\"codec\":");
            if (spliced.equals(result)) {
                // String.replace is silent when the anchor is absent (e.g. a customized fess.json
                // without a "codec" key): the index would be created with index.knn off and every
                // ANN query would fail later with no hint why. Surface the miss loudly instead.
                logger.warn(
                        "[ChunkVector] Setting rewrite anchor \"codec\": not found in the index settings source; \"knn\": true was NOT added. ANN (knn) queries will fail on indexes created from this settings file. Fix the settings file (fess.json) and recreate the index.");
            }
            result = spliced;
        }
        // knn.derived_source (default true on OpenSearch 3.x) strips knn_vector values out of
        // the stored _source and reconstructs them on read -- but the reconstruction is broken
        // for _source-FILTERED reads of nested vectors (observed on 3.7: content_chunk_vector
        // entries come back as {"vector": 1}). Fess reads chunk vectors through _source
        // filtering (e.g. the RAG chat's chunk selection), so keep vectors stored verbatim.
        if (!result.contains("\"knn.derived_source.enabled\"")) {
            final String spliced = result.replace("\"codec\":", "\"knn.derived_source.enabled\": false,\"codec\":");
            if (spliced.equals(result)) {
                logger.warn(
                        "[ChunkVector] Setting rewrite anchor \"codec\": not found in the index settings source; \"knn.derived_source.enabled\": false was NOT added. _source-filtered reads of chunk vectors may return corrupted values on indexes created from this settings file. Fix the settings file (fess.json) and recreate the index.");
            }
            result = spliced;
        }
        return result;
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
        if (source.contains("\"" + Constants.CONTENT_CHUNK_VECTOR_FIELD + "\"")) {
            // Idempotence/coexistence guard, mirroring rewriteSetting's contains-guards: the
            // field is already mapped (e.g. a customized doc.json that defines it statically, or
            // another rewrite rule ran first). Splicing again would emit a duplicate key.
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
                + "      \"dimension\": " + dimension + buildKnnMethodDef() + "\n" //
                + "    }\n" //
                + "  }\n" //
                + "},";
        final String result = source.replace("\"content\":", fieldDef + "\n\"content\":");
        if (result.equals(source)) {
            // String.replace is silent when the anchor is absent (e.g. a customized doc.json
            // whose content field is renamed or formatted differently): the index would be
            // created without the chunk vector mapping and every vector write would fail later
            // with no hint why. Surface the miss loudly instead.
            logger.warn(
                    "[ChunkVector] Mapping rewrite anchor \"content\": not found in the document mapping source; the {} mapping was NOT added. Chunk vectors cannot be indexed into indexes created from this mapping file. Fix the mapping file (doc.json) and recreate the index.",
                    Constants.CONTENT_CHUNK_VECTOR_FIELD);
        }
        return result;
    }

    /**
     * Builds the ANN {@code method} block appended to the knn_vector field mapping
     * when semantic chunk search is enabled, so vectors are indexed into an HNSW
     * graph and the {@code knn} query stays fast on large indexes. Returns an empty
     * string when semantic search is disabled (vectors are stored for the RAG chat
     * only) or when any configured value fails validation — an invalid value must
     * never be spliced into the mapping JSON.
     *
     * @return the {@code ,"method": {...}} JSON fragment, or an empty string
     */
    protected String buildKnnMethodDef() {
        if (!isSemanticSearchEnabled()) {
            return "";
        }
        final String method = getKnnMethod();
        final String engine = getKnnEngine();
        final String spaceType = getKnnSpaceType();
        final int m = getKnnConfigInt(KNN_PARAM_M_PROPERTY, 16);
        final int efConstruction = getKnnConfigInt(KNN_PARAM_EF_CONSTRUCTION_PROPERTY, 100);
        return ",\n" //
                + "      \"method\": {\n" //
                + "        \"name\": \"" + method + "\",\n" //
                + "        \"engine\": \"" + engine + "\",\n" //
                + "        \"space_type\": \"" + spaceType + "\",\n" //
                + "        \"parameters\": {\n" //
                + "          \"m\": " + m + ",\n" //
                + "          \"ef_construction\": " + efConstruction + "\n" //
                + "        }\n" //
                + "      }";
    }

    /**
     * Checks whether the semantic chunk search integration is enabled
     * ({@code content_chunker.search.enabled}). Owned here rather than in the
     * searcher so the index-creation-time rewrite rules and the search-time
     * consumer read one flag.
     *
     * @return true if semantic chunk search is enabled
     */
    public boolean isSemanticSearchEnabled() {
        return Boolean.parseBoolean(ComponentUtil.getFessConfig().getSystemProperty(SEMANTIC_SEARCH_ENABLED_PROPERTY, "false"));
    }

    /**
     * Gets the configured ANN method name for the knn_vector mapping.
     *
     * @return the method name (default {@code hnsw})
     */
    public String getKnnMethod() {
        return getKnnConfigToken(KNN_METHOD_PROPERTY, "hnsw", Set.of("hnsw", "ivf"));
    }

    /**
     * Gets the configured ANN engine for the knn_vector mapping.
     *
     * @return the engine name (default {@code lucene})
     */
    public String getKnnEngine() {
        return getKnnConfigToken(KNN_ENGINE_PROPERTY, "lucene", Set.of("lucene", "faiss", "nmslib"));
    }

    /**
     * Gets the configured ANN space type for the knn_vector mapping.
     *
     * @return the space type (default {@code cosinesimil})
     */
    public String getKnnSpaceType() {
        return getKnnConfigToken(KNN_SPACE_TYPE_PROPERTY, "cosinesimil",
                Set.of("cosinesimil", "innerproduct", "l2", "l1", "linf", "hamming"));
    }

    /**
     * Reads a token-style knn mapping config value, rejecting anything outside the
     * allowed set — an unknown token would be spliced into the mapping JSON and make
     * the whole document mapping fail at index creation (surfaced only at WARN).
     *
     * @param key the system property key
     * @param defaultValue the fallback value
     * @param allowedValues the accepted values
     * @return the validated value, or the default when unset or invalid
     */
    protected String getKnnConfigToken(final String key, final String defaultValue, final Set<String> allowedValues) {
        final String value = ComponentUtil.getFessConfig().getSystemProperty(key, defaultValue);
        if (value == null || !allowedValues.contains(value)) {
            logger.warn("[ChunkVector] Invalid value for {}: {}; using {}.", key, value, defaultValue);
            return defaultValue;
        }
        return value;
    }

    /**
     * Reads a positive-integer knn mapping config value.
     *
     * @param key the system property key
     * @param defaultValue the fallback value
     * @return the parsed value, or the default when unset, non-numeric, or not positive
     */
    protected int getKnnConfigInt(final String key, final int defaultValue) {
        final String value = ComponentUtil.getFessConfig().getSystemProperty(key, null);
        if (value == null) {
            return defaultValue;
        }
        try {
            final int parsed = Integer.parseInt(value.trim());
            if (parsed > 0) {
                return parsed;
            }
        } catch (final NumberFormatException e) {
            // fall through
        }
        logger.warn("[ChunkVector] Invalid value for {}: {}; using {}.", key, value, defaultValue);
        return defaultValue;
    }

    /**
     * Checks if content chunking is enabled. Public so callers (e.g.
     * {@code DefaultChatContentFetcher}) can gate on the same
     * flag this helper's own {@link #rewriteMapping}/{@link #executeChunkVectorProcessing} read,
     * rather than duplicating the config lookup with a second, potentially divergent default.
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
     * Fail-fast guard, run ONCE per {@link #executeChunkVectorProcessing()} run (not
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
     * Fail-fast guard, run once per {@link #executeChunkVectorProcessing()} run and only when
     * embedding is active: confirms the live index actually carries the
     * {@code content_chunk_vector} nested knn_vector mapping before any vector is written. That
     * mapping is only ever spliced by {@link #rewriteMapping(String)} at index-creation time, so an
     * index created while embedding was off (chunk-only mode, or a pre-feature version) has no such
     * mapping -- and writing vectors into it would make OpenSearch dynamic-map
     * {@code content_chunk_vector} as an ordinary (non-knn) object field: useless for kNN search
     * and unfixable without a reindex. Skipping the run with a clear WARN instead surfaces the real
     * remediation (recreate/reindex with embedding enabled).
     *
     * <p>Fails open (returns true, proceed) whenever presence cannot be confirmed either way --
     * the mapping readback fails, or no index exists yet behind the update alias (index creation
     * will splice the mapping, since embedding is enabled). Fails closed (returns false, caller
     * must skip the run) only when an index's mapping confirmedly lacks a proper knn
     * {@code content_chunk_vector} field: the field key is absent, or present without an
     * extractable knn dimension (i.e. already dynamic-mapped junk).</p>
     *
     * @return true if the run may write vectors; false if the mapping is confirmedly absent or
     *         malformed and the caller must skip this run entirely
     */
    public boolean checkVectorMappingReady() {
        if (!isContentChunkerEnabled()) {
            return true;
        }
        final Map<String, MappingMetadata> mappings;
        try {
            mappings = readLiveMappings();
        } catch (final Exception e) {
            logger.warn("[ChunkVector] Failed to read back the live document mapping; skipping the {} mapping-presence check for "
                    + "this run.", Constants.CONTENT_CHUNK_VECTOR_FIELD, e);
            return true;
        }
        if (mappings == null || mappings.isEmpty()) {
            // No index exists yet behind the update alias: index creation will splice the vector
            // mapping via rewriteMapping (embedding is active on this run), so nothing to guard.
            return true;
        }
        boolean confirmedNotReady = false;
        for (final MappingMetadata metadata : mappings.values()) {
            if (extractDimension(metadata) != null) {
                // A proper nested knn_vector mapping with a dimension is present.
                return true;
            }
            if (resolveMappingFields(metadata) != null) {
                // This index has a real properties map but no extractable knn vector dimension:
                // the field is either absent or present as non-knn junk -- a confirmed miss, not
                // an unknown.
                confirmedNotReady = true;
            }
        }
        if (confirmedNotReady) {
            logger.warn("[ChunkVector] The live index has no usable {} knn_vector mapping (the index was likely created while "
                    + "embedding was disabled/chunk-only, or by a version without this feature). Writing vectors now would "
                    + "dynamic-map them as a non-knn field, so this run is skipped. Recreate or reindex the index with "
                    + "embedding enabled so the mapping is created, then re-run.", Constants.CONTENT_CHUNK_VECTOR_FIELD);
            return false;
        }
        return true;
    }

    /**
     * Reads back the live document mappings for the update-index alias from OpenSearch, mirroring
     * {@code SearchEngineClient#addMapping}'s own {@code prepareGetMappings} usage so this reuses
     * the same client/API convention rather than introducing a new one. Overridable seam for tests
     * (constructing a real {@link GetMappingsResponse} requires a live cluster, which unit tests
     * here do not stand up).
     *
     * @return the per-index mapping metadata behind the update alias, possibly empty
     */
    protected Map<String, MappingMetadata> readLiveMappings() {
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final GetMappingsResponse response = searchEngineClient.admin()
                .indices()
                .prepareGetMappings(fessConfig.getIndexDocumentUpdateIndex())
                .execute()
                .actionGet(fessConfig.getIndexIndicesTimeout());
        return response.mappings();
    }

    /**
     * Reads back the {@code dimension} already baked into the live {@code content_chunk_vector}
     * mapping. Overridable seam for tests.
     *
     * @return the live mapping's vector dimension, or null if the {@code content_chunk_vector}
     *         field is not present in any concrete index behind the update alias (e.g. no index
     *         has been created yet)
     */
    protected Integer readLiveMappingDimension() {
        final Map<String, MappingMetadata> mappings = readLiveMappings();
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
        final Map<?, ?> properties = resolveMappingFields(metadata);
        if (properties == null) {
            return null;
        }
        final Object vectorField = properties.get(Constants.CONTENT_CHUNK_VECTOR_FIELD);
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
     * Resolves the field-name map ({@code properties} level) of one index's mapping
     * metadata, handling both mapping shapes seen at runtime: the native transport
     * keys metadata by index/type and nests the fields under {@code properties},
     * while fesen-httpclient's {@code GetMappingsResponse} parsing keys metadata by
     * top-level mapping section and hands the section value back directly — its
     * {@code "properties"} entry's source map IS the field map.
     *
     * @param metadata one index's mapping metadata
     * @return the field-name map, or null when no field map is present
     */
    public Map<?, ?> resolveMappingFields(final MappingMetadata metadata) {
        if (metadata == null) {
            return null;
        }
        final Map<String, Object> sourceMap = metadata.getSourceAsMap();
        if (sourceMap == null) {
            return null;
        }
        final Object properties = sourceMap.get("properties");
        if (properties instanceof Map) {
            return (Map<?, ?>) properties;
        }
        if ("properties".equals(metadata.type())) {
            return sourceMap;
        }
        return null;
    }

    /**
     * Processes one document: splits its content into chunks, embeds them,
     * and writes the result back with a CAS (compare-and-swap) guard against
     * a concurrent recrawl. A document a prior chunk-only run left
     * {@code content_chunk_status="chunked"} already stores its chunks as the
     * content array, so its stored elements are embedded directly via the same
     * upgrade path {@link #processBatch(List, boolean)} uses
     * ({@link #extractExistingChunks}) -- never re-chunked and never joined.
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
            // Upgrade-path parity with processBatch(List, boolean): a status="chunked" document's
            // stored content-array elements ARE its chunks -- embed them directly rather than
            // re-chunking the stringifyContent() join (chunk boundaries could shift mid-word).
            final List<String> existingChunks = extractExistingChunks(fessConfig, doc);
            final List<String> chunks;
            if (existingChunks != null) {
                chunks = existingChunks;
            } else {
                final List<String> freshChunks = extractChunks(fessConfig, doc, id);
                if (freshChunks == null) {
                    return markSkipped(searchEngineClient, fessConfig, doc, id);
                }
                chunks = freshChunks;
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
            // must never throw for a single document's processing failure: this class's
            // processing run (executed by the ChunkVectorIndexer child process) and every
            // other caller rely on each document resolving to a plain boolean.
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
        return processBatch(ids, true);
    }

    /**
     * Processes a batch of documents in the given run mode. With {@code embeddingActive} this is
     * the full chunk+embed pipeline described on {@link #processBatch(List)}, plus the upgrade
     * path: a document a prior chunk-only run left {@code content_chunk_status="chunked"} already
     * stores its chunks as the content array, so those array elements are embedded DIRECTLY --
     * never re-chunked, never joined ({@link #extractExistingChunks}). Without
     * {@code embeddingActive} (chunk-only mode: embedding configured off), each document's content
     * is chunked and written back as an array with {@code content_chunk_status="chunked"} and NO
     * vector field, and no embedding call is ever made; a per-document failure is marked
     * {@code "fail"} immediately (the mid-run provider-outage leniency in
     * {@link #handleFailure(SearchEngineClient, FessConfig, Map, String, boolean)} cannot apply --
     * there is no provider involved whose transient outage could explain the failure).
     *
     * @param ids the OpenSearch document {@code _id}s to process as one batch
     * @param embeddingActive whether embedding is active for this run; false selects chunk-only mode
     * @return a map from each input ID to whether it was successfully processed (or a terminal
     *         failure durably recorded), using the same boolean semantics as
     *         {@link #processDocument(String)}'s return value
     */
    public Map<String, Boolean> processBatch(final List<String> ids, final boolean embeddingActive) {
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
                // Upgrade path (embedding runs only): a document a prior chunk-only run left
                // status="chunked" already stores its chunks as the content array -- embed those
                // elements directly, never re-chunk (chunk boundaries could shift) and never join.
                final List<String> existingChunks = embeddingActive ? extractExistingChunks(fessConfig, doc) : null;
                if (existingChunks != null) {
                    chunks = existingChunks;
                } else {
                    final List<String> freshChunks = extractChunks(fessConfig, doc, id);
                    if (freshChunks == null) {
                        results.put(id, markSkipped(searchEngineClient, fessConfig, doc, id));
                        continue;
                    }
                    chunks = freshChunks;
                }
            } catch (final Exception e) {
                logger.warn("[ChunkVector] Failed to process document. id={}", id, e);
                try {
                    results.put(id, handleFailure(searchEngineClient, fessConfig, doc, id, embeddingActive));
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

        if (!embeddingActive) {
            // Chunk-only mode: write each document's chunk array with status="chunked" -- no
            // vector field, no embedding call. A later embedding-enabled run picks these documents
            // up via the pending query's status="chunked" clause and embeds the stored array.
            for (final BatchEntry entry : entries) {
                results.put(entry.id, storeChunkOnlyDocument(searchEngineClient, fessConfig, entry));
            }
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
                results.put(entry.id, recordFailure(searchEngineClient, fessConfig, entry, true));
            }
        }
        return results;
    }

    /**
     * Extracts the already-stored chunk array from a document a prior chunk-only run left
     * {@code content_chunk_status="chunked"}: for such a document the stored content array
     * elements ARE the chunks, so an embedding-enabled run must embed them directly rather than
     * re-chunking (chunk boundaries could shift) or joining them back into one string.
     *
     * @param fessConfig the fess config
     * @param doc the fetched document map
     * @return the stored chunk list, or null if this document is not an upgradable chunked
     *         document (status not {@code "chunked"}, or -- defensively -- its content is not a
     *         non-empty array, in which case the caller falls back to normal re-chunking)
     */
    private List<String> extractExistingChunks(final FessConfig fessConfig, final Map<String, Object> doc) {
        if (!Constants.CHUNKED.equals(doc.get(Constants.CONTENT_CHUNK_STATUS_FIELD))) {
            return null;
        }
        final Object contentObj = doc.get(fessConfig.getIndexFieldContent());
        if (!(contentObj instanceof List<?>) || ((List<?>) contentObj).isEmpty()) {
            return null;
        }
        final List<?> contentList = (List<?>) contentObj;
        final List<String> chunks = new ArrayList<>(contentList.size());
        for (final Object element : contentList) {
            chunks.add(String.valueOf(element));
        }
        return chunks;
    }

    /**
     * CAS-writes one document back in chunk-only mode: content replaced by its chunk array,
     * {@code content_chunk_status="chunked"}, and deliberately NO {@code content_chunk_vector}
     * key -- no vector mapping may exist in this mode, and writing one would dynamic-map it as
     * non-knn junk. Mirrors {@link #storeChunkedDocument}'s copy-then-mutate discipline so a store
     * failure's {@link #handleFailure} write still operates on the originally-fetched content. A
     * non-conflict store failure is marked {@code "fail"} immediately (embedding inactive: the
     * provider-outage leniency cannot apply); even that failure write is guarded so this always
     * resolves to a plain boolean.
     *
     * @param searchEngineClient the search engine client
     * @param fessConfig the fess config
     * @param entry the batch entry (originally-fetched doc + its own chunk list)
     * @return true if the chunk-only write succeeded (or a terminal failure was durably
     *         recorded); false if it lost a CAS race or even the failure write failed
     */
    private boolean storeChunkOnlyDocument(final SearchEngineClient searchEngineClient, final FessConfig fessConfig,
            final BatchEntry entry) {
        try {
            final Map<String, Object> updatedDoc = new HashMap<>(entry.doc);
            updatedDoc.put(fessConfig.getIndexFieldContent(), entry.chunks);
            updatedDoc.put(Constants.CONTENT_CHUNK_STATUS_FIELD, Constants.CHUNKED);
            return storeSafely(searchEngineClient, fessConfig, updatedDoc, entry.id);
        } catch (final Exception e) {
            logger.warn("[ChunkVector] Failed to store chunk-only document. id={}", entry.id, e);
            return recordFailure(searchEngineClient, fessConfig, entry, false);
        }
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
            return recordFailure(searchEngineClient, fessConfig, entry, true);
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
     * Records a per-document processing failure via
     * {@link #handleFailure(SearchEngineClient, FessConfig, Map, String, boolean)} (its own CAS
     * write), never letting handleFailure's own write failure escape --
     * {@link #processBatch(List, boolean)} must resolve every document to a plain boolean.
     *
     * @param searchEngineClient the search engine client
     * @param fessConfig the fess config
     * @param entry the batch entry whose failure is being recorded
     * @param embeddingActive whether embedding is active for this run (gates the mid-run
     *            provider-outage leniency in handleFailure)
     * @return the result of handleFailure, or false if even that write failed
     */
    private boolean recordFailure(final SearchEngineClient searchEngineClient, final FessConfig fessConfig, final BatchEntry entry,
            final boolean embeddingActive) {
        try {
            return handleFailure(searchEngineClient, fessConfig, entry.doc, entry.id, embeddingActive);
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
        return handleFailure(searchEngineClient, fessConfig, doc, id, true);
    }

    /**
     * Mode-aware variant of {@link #handleFailure(SearchEngineClient, FessConfig, Map, String)}:
     * the mid-run provider-outage leniency (leave the document pending, no write) only applies
     * when embedding is active -- in chunk-only mode no embedding provider is involved, so a
     * transient provider outage can never explain a failure and every failure is a genuine
     * per-document one, marked {@code content_chunk_status="fail"} immediately.
     *
     * @param searchEngineClient the search engine client
     * @param fessConfig the fess config
     * @param doc the document map read before the failure occurred
     * @param id the document ID
     * @param embeddingActive whether embedding is active for this run
     * @return the result of {@link #storeSafely}
     */
    protected boolean handleFailure(final SearchEngineClient searchEngineClient, final FessConfig fessConfig, final Map<String, Object> doc,
            final String id, final boolean embeddingActive) {
        if (embeddingActive && !isEmbeddingClientAvailable()) {
            // The embedding provider is unreachable RIGHT NOW -- it died mid-run, after
            // executeChunkVectorProcessing()'s pre-flight availability gate had already passed.
            // Every pending document's embed call is now failing purely because of this transient
            // outage, not because the document itself is unprocessable. Marking these outage
            // failures would durably stamp the whole corpus content_chunk_status=fail (which
            // the pending query then excludes forever, recoverable only by a full
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
     * {@link #buildPendingQuery(boolean)}'s pending set (which excludes any document whose
     * {@code content_chunk_status} is set, other than "chunked" on embedding runs), so it is no longer re-fetched every run — preventing
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
     * format, which would be nonsense as re-chunking input. Documents a chunk-only
     * run left {@code "chunked"} normally never reach this join on an embedding run
     * (both {@link #processBatch(List, boolean)} and {@link #processDocument(String)} embed their
     * stored array elements directly via the {@link #extractExistingChunks} upgrade path), so this
     * remains a defensive fallback for a malformed chunked document or a future re-embed
     * maintenance job.
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
     * {@link #executeChunkVectorProcessing()}'s pre-flight gate use. Overridable seam for tests.
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
