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
package org.codelibs.fess.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.embedding.EmbeddingClientManager;
import org.codelibs.fess.helper.ChunkVectorHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * In-process job that processes documents pending chunk/embedding
 * generation. Shaped like the sibling {@code org.codelibs.fess.job.AggregateLogJob}: a
 * plain class (no interface), a no-arg {@link #execute()} returning a result
 * message, registered as a {@code instance="prototype"} DI component.
 *
 * <p>The scroll phase collects only pending document IDs (a fast,
 * {@code _source}-restricted scroll, fully closed before any slow work
 * starts); the actual {@code _seq_no}/{@code _primary_term} CAS read happens
 * per-document inside {@link ChunkVectorHelper#processBatch(List)},
 * immediately before the slow chunk+embed work -- see this task's header
 * note on scroll-timeout avoidance.</p>
 *
 * <p>The ID list is partitioned into {@code content_chunker.job.bulk_size}-sized batches, and
 * each batch is submitted as a single thread-pool task that embeds all of its documents' chunks
 * in one {@link ChunkVectorHelper#processBatch(List)} call, rather than one embedding call per
 * document. {@code content_chunker.job.concurrency} therefore now bounds the number of
 * concurrently in-flight <em>batches</em>, not individual documents.</p>
 */
public class ChunkVectorJob {

    private static final Logger logger = LogManager.getLogger(ChunkVectorJob.class);

    /** Number of document IDs fetched per scroll batch. */
    protected static final int SCROLL_SIZE = 100;

    /**
     * System property key for the bounded concurrency of the job's embedding calls: the number of
     * concurrently in-flight batches (each up to {@link #JOB_BULK_SIZE_PROPERTY} documents).
     */
    protected static final String JOB_CONCURRENCY_PROPERTY = "content_chunker.job.concurrency";

    /** System property key for the number of documents grouped into a single embedding call. */
    protected static final String JOB_BULK_SIZE_PROPERTY = "content_chunker.job.bulk_size";

    /**
     * System property key for the maximum number of pending document IDs collected (and therefore
     * processed) in a single job run; bounds the memory the scroll phase holds.
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
    public ChunkVectorJob() {
        // Default constructor
    }

    /**
     * Executes the job: scroll-collects pending document IDs, partitions them into
     * {@code content_chunker.job.bulk_size}-sized batches, then fans those batches out across a
     * bounded thread pool, delegating each batch to {@link ChunkVectorHelper#processBatch(List)}
     * (one embedding call per batch, spanning every document in it, rather than one call per
     * document).
     *
     * <p>No in-code overlap guard is needed: the scheduler serializes runs of this same job. Every
     * scheduled job is registered by {@code JobHelper#register} with {@code uniqueBy(id)} plus
     * {@code fessConfig.getSchedulerConcurrentExecModeAsEnum()} (default {@code scheduler.concurrent.exec.mode=QUIT}),
     * so LastaJob's {@code ConcurrentJobStopper} silently quits a fresh trigger while a prior run of
     * the same unique job is still executing. All three {@code JobConcurrentExec} modes (WAIT/QUIT/ERROR)
     * forbid concurrent execution of the same unique job, so a second {@code execute()} can never start
     * mid-run regardless of that setting -- there is no wasted duplicate embedding spend to guard against
     * here, and no sibling job (AggregateLogJob/PurgeLogJob/PurgeDocJob) carries such a guard either.</p>
     *
     * @return a summary result message (processed/succeeded/failed counts)
     */
    public String execute() {
        final ChunkVectorHelper chunkVectorHelper = getChunkVectorHelper();
        if (!chunkVectorHelper.isContentChunkerEnabled()) {
            // content_chunker.enabled is false: this scheduled job must be a complete no-op -- no
            // scroll, no fetch, no chunk, no write. Without this early return, enabling the
            // "Content Chunk Vector Indexer" job (an ordinary Admin > Scheduler action) without also
            // setting the separate content_chunker.enabled system property would let every pending
            // document run the full chunk pipeline and fail only at the embedding step (no available
            // client when disabled), which ChunkVectorHelper#handleFailure would durably stamp
            // content_chunk_status=fail via a real CAS write. Logged only at DEBUG so a
            // site that simply does not use this feature never sees WARN/INFO spam on each run.
            if (logger.isDebugEnabled()) {
                logger.debug("Content chunking is disabled; skipping ChunkVectorJob.");
            }
            return "Content chunking is disabled. Skipped.";
        }

        if (!isEmbeddingClientAvailable()) {
            // The embedding provider is unreachable (e.g. a transient outage). Skip the whole run
            // WITHOUT scrolling/fetching/processing so pending documents stay pending: otherwise
            // every batch's embed call would throw, the per-document fallback would throw, and
            // handleFailure would stamp the entire corpus content_chunk_status=fail (which
            // buildPendingQuery then excludes forever, recoverable only by a full recrawl). Logged
            // at WARN so a genuine outage is visible; the documents stay pending and are retried
            // once the provider recovers.
            logger.warn("Embedding provider is not available; skipping ChunkVectorJob to keep pending documents pending.");
            return "Embedding provider is not available. Skipped.";
        }

        final StringBuilder resultBuf = new StringBuilder();
        if (!chunkVectorHelper.checkDimensionConsistency()) {
            // A confirmed embedding-dimension mismatch between the configured provider/model and
            // the live content_chunk_vector mapping was already logged at ERROR by
            // checkDimensionConsistency() -- skip this run entirely rather than letting every
            // pending document independently fail against OpenSearch's k-NN dimension-mismatch
            // rejection.
            resultBuf.append("Embedding dimension mismatch detected between the configured embedding provider and the existing ")
                    .append("content_chunk_vector mapping; skipping this run. See the ERROR log for details.");
            return resultBuf.toString();
        }

        final List<String> idList;
        try {
            idList = scrollPendingIds();
        } catch (final Exception e) {
            logger.warn("Failed to scroll-search pending documents.", e);
            resultBuf.append(e.getMessage()).append("\n");
            return resultBuf.toString();
        }

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
                        final Map<String, Boolean> results = chunkVectorHelper.processBatch(batch);
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

        resultBuf.append("Processed ")
                .append(processed.get())
                .append(" documents. Succeeded: ")
                .append(succeeded.get())
                .append(", Failed/Skipped: ")
                .append(failed.get())
                .append(".");
        return resultBuf.toString();
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
     * documents matching {@link #buildPendingQuery()}, restricted to just
     * the ID field, and collects matching IDs before returning (the
     * scroll context is fully closed by the time this method returns). Uses the
     * update-index alias -- not the search-index alias -- so this job never diverges
     * from {@link ChunkVectorHelper}'s own read/write index during a blue-green
     * crawl/reindex swap, matching the sibling {@code PurgeDocJob}/{@code UpdateLabelJob}
     * convention.
     *
     * <p>Collection stops once {@link #getJobMaxDocumentsPerRun()} IDs have been gathered, bounding
     * the memory a single run holds so a large-corpus first run cannot OOM by materializing the
     * entire pending-ID set at once. The remaining pending documents keep {@code content_chunk_status}
     * absent and are picked up by the next (idempotent) scheduled run. The cap is enforced by
     * throwing a private {@link ScrollLimitReachedException} from the scroll cursor rather than by
     * returning {@code false} from it: {@code SearchEngineClient#scrollSearch} only stops iterating
     * the <em>current</em> page when the cursor returns {@code false} and then keeps fetching further
     * pages, so a bare {@code false} would drain the whole (potentially millions-of-pages) scroll
     * instead of stopping it -- the throw exits the scroll immediately, with its {@code finally}
     * still releasing the scroll context.</p>
     *
     * @return the list of pending document IDs, at most {@link #getJobMaxDocumentsPerRun()} entries
     */
    protected List<String> scrollPendingIds() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        final int maxDocuments = Math.max(1, getJobMaxDocumentsPerRun());
        final List<String> idList = new ArrayList<>();
        try {
            searchEngineClient.scrollSearch(fessConfig.getIndexDocumentUpdateIndex(), requestBuilder -> {
                requestBuilder.setQuery(buildPendingQuery())
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
     * Internal control-flow signal thrown from {@link #scrollPendingIds()}'s scroll cursor once
     * {@link #getJobMaxDocumentsPerRun()} document IDs have been collected, to stop the scroll early
     * without materializing the whole corpus. Never escapes {@link #scrollPendingIds()}; stack trace
     * and suppression are disabled since it carries no diagnostic value.
     */
    private static final class ScrollLimitReachedException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        ScrollLimitReachedException() {
            super(null, null, false, false);
        }
    }

    /**
     * Builds the query matching documents pending chunk/embedding
     * generation: {@code content_chunk_status} absent.
     *
     * @return the query builder
     */
    protected QueryBuilder buildPendingQuery() {
        return QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(Constants.CONTENT_CHUNK_STATUS_FIELD));
    }

    /**
     * Gets the {@link ChunkVectorHelper} component. Overridable seam for tests.
     *
     * @return the chunk vector helper
     */
    protected ChunkVectorHelper getChunkVectorHelper() {
        return ComponentUtil.getComponent(ChunkVectorHelper.class);
    }

    /**
     * Reports whether the configured embedding provider is currently available, gating the run so a
     * transient provider outage does not burn every pending document's retry budget. Delegates to
     * {@link EmbeddingClientManager#available()} (the same availability check the RAG read path uses).
     * Overridable seam for tests.
     *
     * @return true if the embedding provider is available and the job may proceed
     */
    protected boolean isEmbeddingClientAvailable() {
        return ComponentUtil.getComponent(EmbeddingClientManager.class).available();
    }

    /**
     * Gets the configured job concurrency.
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

    private int getConfigInt(final String key, final int defaultValue) {
        final String value = ComponentUtil.getFessConfig().getSystemProperty(key, null);
        if (value != null) {
            try {
                return Integer.parseInt(value.trim());
            } catch (final NumberFormatException e) {
                logger.warn("Invalid integer for {}: {}", key, value);
            }
        }
        return defaultValue;
    }
}
