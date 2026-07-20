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

import java.util.List;
import java.util.Map;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.BooleanFunction;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.index.query.QueryBuilder;

public class ChunkVectorJobTest extends UnitFessTestCase {

    @Test
    public void test_buildPendingQuery_excludesAnyStatus() {
        final ChunkVectorJob job = new ChunkVectorJob();
        final QueryBuilder query = job.buildPendingQuery();
        final String json = query.toString();
        assertTrue(json.contains("must_not"), "status-absent condition must be a must_not clause: " + json);
        assertTrue(json.contains("exists"), "should use an exists query on content_chunk_status: " + json);
        assertTrue(json.contains("content_chunk_status"), "should reference content_chunk_status: " + json);
        assertFalse(json.contains("content_chunk_retry_count"), "the retry-count clause was removed with the retry counter: " + json);
    }

    // ===================================================================================
    //                                        scrollPendingIds index alias (Finding 1)
    //                                        ================================================

    @Test
    public void test_scrollPendingIds_queriesUpdateIndexNotSearchIndex() {
        final CapturingSearchEngineClient searchEngineClient = new CapturingSearchEngineClient();
        searchEngineClient.sourcesToReturn = List.of(Map.of("id", "doc-1"), Map.of("id", "doc-2"));

        final TestFessConfig fessConfig = new TestFessConfig();
        fessConfig.updateIndex = "fess.update";
        fessConfig.searchIndex = "fess.search";

        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        ComponentUtil.setFessConfig(fessConfig);
        try {
            final ChunkVectorJob job = new ChunkVectorJob();
            final List<String> ids = job.scrollPendingIds();

            assertEquals(
                    "must scroll the update-index alias, matching PurgeDocJob/UpdateLabelJob -- not the search-index alias, "
                            + "which can diverge from the update alias during a blue-green crawl/reindex swap",
                    "fess.update", searchEngineClient.capturedIndex);
            assertEquals(List.of("doc-1", "doc-2"), ids);
        } finally {
            ComponentUtil.setFessConfig(null);
        }
    }

    @Test
    public void test_scrollPendingIds_capsAtMaxDocumentsPerRun() {
        final CapturingSearchEngineClient searchEngineClient = new CapturingSearchEngineClient();
        final List<Map<String, Object>> sources = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            sources.add(Map.of("id", "doc-" + i));
        }
        searchEngineClient.sourcesToReturn = sources;

        final TestFessConfig fessConfig = new TestFessConfig();
        fessConfig.updateIndex = "fess.update";

        ComponentUtil.register(searchEngineClient, "searchEngineClient");
        ComponentUtil.setFessConfig(fessConfig);
        try {
            // Cap the per-run collection at 3: a large-corpus scroll must stop accumulating once the
            // cap is reached rather than materializing every matching ID (the MED-3 OOM guard).
            final ChunkVectorJob job = new ChunkVectorJob() {
                @Override
                protected int getJobMaxDocumentsPerRun() {
                    return 3;
                }
            };

            final List<String> ids = job.scrollPendingIds();

            assertEquals(3, ids.size(), "scroll collection must stop once the per-run document cap is reached, bounding memory");
            assertEquals(List.of("doc-0", "doc-1", "doc-2"), ids);
            assertTrue(searchEngineClient.cursorAppliedCount <= 3,
                    "the scroll cursor must not keep being invoked after the cap is reached (it was invoked "
                            + searchEngineClient.cursorAppliedCount
                            + " times) -- the scroll must actually stop, not drain the whole result set");
        } finally {
            ComponentUtil.setFessConfig(null);
        }
    }

    /**
     * Captures the index name {@link ChunkVectorJob#scrollPendingIds()} passes to
     * {@link SearchEngineClient#scrollSearch(String, SearchEngineClient.SearchCondition, BooleanFunction)}
     * and feeds back a fixed set of sources, without needing a live OpenSearch cluster (mirrors
     * {@code PurgeDocJobTest}'s fake-{@code SearchEngineClient} pattern).
     */
    private static final class CapturingSearchEngineClient extends SearchEngineClient {
        String capturedIndex;
        List<Map<String, Object>> sourcesToReturn = List.of();
        int cursorAppliedCount = 0;

        @Override
        public long scrollSearch(final String index, final SearchCondition<SearchRequestBuilder> condition,
                final BooleanFunction<Map<String, Object>> cursor) {
            capturedIndex = index;
            for (final Map<String, Object> source : sourcesToReturn) {
                cursorAppliedCount++;
                if (!cursor.apply(source)) {
                    break;
                }
            }
            return sourcesToReturn.size();
        }
    }

    private static final class TestFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;
        String updateIndex;
        String searchIndex;

        @Override
        public String getIndexDocumentUpdateIndex() {
            return updateIndex;
        }

        @Override
        public String getIndexDocumentSearchIndex() {
            return searchIndex;
        }

        @Override
        public String getIndexFieldId() {
            return "id";
        }
    }

    @Test
    public void test_defaults_matchNamingSummary() {
        final ChunkVectorJob job = new ChunkVectorJob();
        assertEquals(2, job.getConcurrency());
        assertEquals(20, job.getJobBulkSize());
    }

    @Test
    public void test_partitionIds_groupsIntoBulkSizedBatchesWithFinalPartialGroup() {
        final ChunkVectorJob job = new ChunkVectorJob();
        final java.util.List<String> ids = new java.util.ArrayList<>();
        for (int i = 0; i < 7; i++) {
            ids.add("doc-" + i);
        }

        final java.util.List<java.util.List<String>> batches = job.partitionIds(ids, 3);

        assertEquals(3, batches.size(), "7 ids at bulk size 3 should yield 3 batches (3+3+1)");
        assertEquals(java.util.List.of("doc-0", "doc-1", "doc-2"), batches.get(0));
        assertEquals(java.util.List.of("doc-3", "doc-4", "doc-5"), batches.get(1));
        assertEquals("the final partial group must contain the remainder", java.util.List.of("doc-6"), batches.get(2));
    }

    @Test
    public void test_partitionIds_exactMultipleOfBulkSize_noPartialGroup() {
        final ChunkVectorJob job = new ChunkVectorJob();
        final java.util.List<String> ids = java.util.List.of("doc-0", "doc-1", "doc-2", "doc-3");

        final java.util.List<java.util.List<String>> batches = job.partitionIds(ids, 2);

        assertEquals(2, batches.size());
        assertEquals(java.util.List.of("doc-0", "doc-1"), batches.get(0));
        assertEquals(java.util.List.of("doc-2", "doc-3"), batches.get(1));
    }

    @Test
    public void test_execute_aggregatesBatchResultsIntoProcessedSucceededFailedCounters() {
        final TestableChunkVectorJob job = new TestableChunkVectorJob();
        job.idsToReturn = java.util.List.of("doc-1", "doc-2", "doc-3", "doc-4");
        job.helperResult = false;
        final String result = job.execute();
        assertEquals(4, job.processedIds.size());
        assertTrue(result.contains("Processed 4"), "should report four processed documents: " + result);
        assertTrue(result.contains("Succeeded: 0"), "helperResult=false should count all as failed: " + result);
        assertTrue(result.contains("Failed/Skipped: 4"), result);
    }

    @Test
    public void test_execute_noPendingDocuments_returnsZeroSummary() {
        final TestableChunkVectorJob job = new TestableChunkVectorJob();
        job.idsToReturn = java.util.List.of();
        final String result = job.execute();
        assertTrue(result.contains("Processed 0"), "should report zero processed documents: " + result);
    }

    @Test
    public void test_execute_processesEachIdViaHelper() {
        final TestableChunkVectorJob job = new TestableChunkVectorJob();
        job.idsToReturn = java.util.List.of("doc-1", "doc-2", "doc-3");
        job.helperResult = true;
        final String result = job.execute();
        assertEquals(3, job.processedIds.size());
        assertTrue(result.contains("Processed 3"), "should report three processed documents: " + result);
        assertTrue(result.contains("Succeeded: 3"), "all three should succeed per the fake helper: " + result);
    }

    // ---- Beyond-the-brief coverage: genuine bounded-concurrency + fault-isolation, per
    // this task's self-review requirement (not weaker single-threaded/happy-path proxies). ----

    @Test
    public void test_execute_boundsConcurrencyUnderRealContention() {
        final int concurrency = 3;
        final int taskCount = 15;
        final ConcurrencyTrackingJob job = new ConcurrencyTrackingJob();
        job.concurrency = concurrency;
        final java.util.List<String> ids = new java.util.ArrayList<>();
        for (int i = 0; i < taskCount; i++) {
            ids.add("doc-" + i);
        }
        job.idsToReturn = ids;

        final String result = job.execute();

        assertTrue(job.maxObserved.get() <= concurrency, "observed concurrency " + job.maxObserved.get()
                + " must never exceed the configured cap " + concurrency + " -- the pool must be genuinely bounded");
        assertTrue(job.maxObserved.get() > 1,
                "expected genuine overlap under contention (maxObserved=" + job.maxObserved.get()
                        + "); a test that never observes concurrency > 1 cannot distinguish a bounded pool " + "of size " + concurrency
                        + " from an accidentally-serial implementation");
        assertTrue(result.contains("Processed " + taskCount), "should report all " + taskCount + " processed: " + result);
    }

    @Test
    public void test_execute_isolatesExceptionInOneDocumentFromOthers() {
        final FaultInjectingJob job = new FaultInjectingJob();
        job.idsToReturn = java.util.List.of("doc-1", "doc-2", "doc-3");
        job.failingIds.add("doc-2");

        final String result = job.execute();

        assertEquals("all three ids should have been attempted despite one throwing: " + job.processedIds, 3, job.processedIds.size());
        assertTrue(result.contains("Processed 3"), "should report three attempted documents: " + result);
        assertTrue(result.contains("Succeeded: 2"), "the two non-throwing documents should still succeed: " + result);
        assertTrue(result.contains("Failed/Skipped: 1"),
                "the thrown exception must be isolated and counted as failed, not crash the whole job run: " + result);
    }

    // ===================================================================================
    //                                        content-chunker enabled gate (Fix 1)
    //                                        ================================================

    @Test
    public void test_execute_contentChunkerDisabled_isCompleteNoOp() {
        final EnabledAwareJob job = new EnabledAwareJob();
        job.contentChunkerEnabled = false;
        // These ids would flow into processBatch() (and inflate the processed counters) if the
        // disabled gate failed to short-circuit before scrollPendingIds().
        job.idsToReturn = java.util.List.of("doc-1", "doc-2");

        final String result = job.execute();

        assertFalse(job.checkDimensionConsistencyCalled,
                "a disabled content chunker must return before even the dimension-consistency check");
        assertFalse(job.scrollPendingIdsCalled, "a disabled content chunker must never scroll for pending documents");
        assertTrue(job.processedIds.isEmpty(),
                "no document may be fetched/chunked/embedded/written when chunking is disabled: " + job.processedIds);
        assertTrue(result.toLowerCase(java.util.Locale.ROOT).contains("disabled"),
                "the skip result message must indicate chunking is disabled: " + result);
    }

    @Test
    public void test_execute_contentChunkerEnabled_proceedsPastGate() {
        final EnabledAwareJob job = new EnabledAwareJob();
        job.contentChunkerEnabled = true;
        job.idsToReturn = java.util.List.of("doc-1", "doc-2");

        final String result = job.execute();

        assertTrue(job.checkDimensionConsistencyCalled, "an enabled content chunker must run the dimension-consistency check");
        assertTrue(job.scrollPendingIdsCalled, "an enabled content chunker must scroll for pending documents");
        assertEquals(2, job.processedIds.size());
        assertTrue(result.contains("Processed 2"), "should report both documents processed: " + result);
    }

    /**
     * A {@link ChunkVectorJob} whose {@link ChunkVectorHelper#isContentChunkerEnabled()} result is
     * controlled by {@link #contentChunkerEnabled}, tracking whether the run advanced past the
     * enabled gate into {@link ChunkVectorHelper#checkDimensionConsistency()}, {@link #scrollPendingIds()},
     * and {@link ChunkVectorHelper#processBatch(java.util.List)} -- so the disabled case can be proven a
     * complete no-op (nothing scrolled/fetched/embedded/written) and the enabled case proven to proceed.
     */
    private static final class EnabledAwareJob extends ChunkVectorJob {
        boolean contentChunkerEnabled = true;
        java.util.List<String> idsToReturn = java.util.List.of();
        boolean checkDimensionConsistencyCalled = false;
        boolean scrollPendingIdsCalled = false;
        final java.util.List<String> processedIds = new java.util.concurrent.CopyOnWriteArrayList<>();

        @Override
        protected boolean isEmbeddingClientAvailable() {
            return true;
        }

        @Override
        protected java.util.List<String> scrollPendingIds() {
            scrollPendingIdsCalled = true;
            return idsToReturn;
        }

        @Override
        protected org.codelibs.fess.helper.ChunkVectorHelper getChunkVectorHelper() {
            return new org.codelibs.fess.helper.ChunkVectorHelper() {
                @Override
                public boolean isContentChunkerEnabled() {
                    return contentChunkerEnabled;
                }

                @Override
                public boolean checkDimensionConsistency() {
                    checkDimensionConsistencyCalled = true;
                    return true;
                }

                @Override
                public java.util.Map<String, Boolean> processBatch(final java.util.List<String> ids) {
                    final java.util.Map<String, Boolean> results = new java.util.HashMap<>();
                    for (final String id : ids) {
                        processedIds.add(id);
                        results.put(id, true);
                    }
                    return results;
                }
            };
        }
    }

    // ===================================================================================
    //                                        checkDimensionConsistency wiring (Fix 1: write side)
    //                                        ================================================

    @Test
    public void test_execute_dimensionMismatch_skipsRunWithoutScrollingOrProcessing() {
        final DimensionAwareJob job = new DimensionAwareJob();
        job.dimensionOk = false;
        // If scrollPendingIds() were ever called (it must not be), these ids would flow through
        // to processBatch() and inflate the processed/succeeded counters -- a red herring the
        // assertions below would catch even without the explicit scrollPendingIdsCalled flag.
        job.idsToReturn = java.util.List.of("doc-1", "doc-2");

        final String result = job.execute();

        assertFalse(job.scrollPendingIdsCalled, "a confirmed dimension mismatch must skip scrolling entirely");
        assertTrue(job.processedIds.isEmpty(), "no document may be touched when the run is skipped: " + job.processedIds);
        assertTrue(result.toLowerCase(java.util.Locale.ROOT).contains("dimension mismatch"),
                "the skip result message must mention the dimension mismatch: " + result);
    }

    @Test
    public void test_execute_dimensionConsistent_proceedsNormally() {
        final DimensionAwareJob job = new DimensionAwareJob();
        job.dimensionOk = true;
        job.idsToReturn = java.util.List.of("doc-1", "doc-2");

        final String result = job.execute();

        assertTrue(job.scrollPendingIdsCalled, "a consistent dimension must let the job proceed to scrolling");
        assertEquals(2, job.processedIds.size());
        assertTrue(result.contains("Processed 2"), "should report both documents processed: " + result);
    }

    // ===================================================================================
    //                                        embedding-provider availability gate (Fix M2)
    //                                        ================================================

    @Test
    public void test_execute_embeddingProviderUnavailable_skipsRunWithoutTouchingDocuments() {
        final AvailabilityAwareJob job = new AvailabilityAwareJob();
        job.embeddingAvailable = false;
        // If the run were not gated, these ids would be scrolled and flow into processBatch(), whose
        // per-document embed failures during the outage would durably stamp the corpus
        // content_chunk_status=fail (which buildPendingQuery then excludes forever). The gate must
        // keep them pending instead.
        job.idsToReturn = java.util.List.of("doc-1", "doc-2");

        final String result = job.execute();

        assertFalse(job.scrollPendingIdsCalled,
                "an unavailable embedding provider must skip scrolling entirely -- no document may be fetched/processed");
        assertTrue(job.processedIds.isEmpty(),
                "no document may be touched (no status may be written) during a provider outage: " + job.processedIds);
        assertTrue(result.toLowerCase(java.util.Locale.ROOT).contains("not available"),
                "the skip result message must indicate the embedding provider is not available: " + result);
    }

    @Test
    public void test_execute_embeddingProviderAvailable_proceedsNormally() {
        final AvailabilityAwareJob job = new AvailabilityAwareJob();
        job.embeddingAvailable = true;
        job.idsToReturn = java.util.List.of("doc-1", "doc-2");

        final String result = job.execute();

        assertTrue(job.scrollPendingIdsCalled, "an available embedding provider must let the job proceed to scrolling");
        assertEquals(2, job.processedIds.size());
        assertTrue(result.contains("Processed 2"), "should report both documents processed: " + result);
    }

    /**
     * A {@link ChunkVectorJob} whose {@link ChunkVectorJob#isEmbeddingClientAvailable()} result is
     * controlled by {@link #embeddingAvailable}, so the M2 availability gate (skip-before-scroll on a
     * provider outage; proceed-as-normal when available) can be tested independently of
     * {@code EmbeddingClientManager}'s own availability logic. Content chunking is enabled and the
     * dimension is reported consistent so the run reaches the availability gate.
     */
    private static final class AvailabilityAwareJob extends ChunkVectorJob {
        boolean embeddingAvailable = true;
        java.util.List<String> idsToReturn = java.util.List.of();
        boolean scrollPendingIdsCalled = false;
        final java.util.List<String> processedIds = new java.util.concurrent.CopyOnWriteArrayList<>();

        @Override
        protected boolean isEmbeddingClientAvailable() {
            return embeddingAvailable;
        }

        @Override
        protected java.util.List<String> scrollPendingIds() {
            scrollPendingIdsCalled = true;
            return idsToReturn;
        }

        @Override
        protected org.codelibs.fess.helper.ChunkVectorHelper getChunkVectorHelper() {
            return new org.codelibs.fess.helper.ChunkVectorHelper() {
                @Override
                public boolean isContentChunkerEnabled() {
                    return true;
                }

                @Override
                public boolean checkDimensionConsistency() {
                    return true;
                }

                @Override
                public java.util.Map<String, Boolean> processBatch(final java.util.List<String> ids) {
                    final java.util.Map<String, Boolean> results = new java.util.HashMap<>();
                    for (final String id : ids) {
                        processedIds.add(id);
                        results.put(id, true);
                    }
                    return results;
                }
            };
        }
    }

    /**
     * A {@link ChunkVectorJob} whose {@link ChunkVectorHelper#checkDimensionConsistency()} result
     * is controlled by {@link #dimensionOk}, so the job-level wiring (skip-before-scroll on a
     * confirmed mismatch; proceed-as-normal when consistent) can be tested independently of
     * {@code ChunkVectorHelper}'s own dimension-comparison logic (covered by
     * {@code ChunkVectorHelperTest}).
     */
    private static final class DimensionAwareJob extends ChunkVectorJob {
        boolean dimensionOk = true;
        java.util.List<String> idsToReturn = java.util.List.of();
        boolean scrollPendingIdsCalled = false;
        final java.util.List<String> processedIds = new java.util.concurrent.CopyOnWriteArrayList<>();

        @Override
        protected boolean isEmbeddingClientAvailable() {
            return true;
        }

        @Override
        protected java.util.List<String> scrollPendingIds() {
            scrollPendingIdsCalled = true;
            return idsToReturn;
        }

        @Override
        protected org.codelibs.fess.helper.ChunkVectorHelper getChunkVectorHelper() {
            return new org.codelibs.fess.helper.ChunkVectorHelper() {
                @Override
                public boolean isContentChunkerEnabled() {
                    return true;
                }

                @Override
                public boolean checkDimensionConsistency() {
                    return dimensionOk;
                }

                @Override
                public java.util.Map<String, Boolean> processBatch(final java.util.List<String> ids) {
                    final java.util.Map<String, Boolean> results = new java.util.HashMap<>();
                    for (final String id : ids) {
                        processedIds.add(id);
                        results.put(id, true);
                    }
                    return results;
                }
            };
        }
    }

    private static final class TestableChunkVectorJob extends ChunkVectorJob {
        java.util.List<String> idsToReturn = java.util.List.of();
        boolean helperResult = true;
        final java.util.List<String> processedIds = new java.util.concurrent.CopyOnWriteArrayList<>();

        @Override
        protected boolean isEmbeddingClientAvailable() {
            return true;
        }

        @Override
        protected java.util.List<String> scrollPendingIds() {
            return idsToReturn;
        }

        @Override
        protected org.codelibs.fess.helper.ChunkVectorHelper getChunkVectorHelper() {
            return new org.codelibs.fess.helper.ChunkVectorHelper() {
                @Override
                public boolean isContentChunkerEnabled() {
                    return true;
                }

                @Override
                public java.util.Map<String, Boolean> processBatch(final java.util.List<String> ids) {
                    final java.util.Map<String, Boolean> results = new java.util.HashMap<>();
                    for (final String id : ids) {
                        processedIds.add(id);
                        results.put(id, helperResult);
                    }
                    return results;
                }
            };
        }

        @Override
        protected int getConcurrency() {
            return 2;
        }
    }

    /**
     * Tracks the actual number of concurrently in-flight {@code processBatch} calls via a real
     * {@code Thread.sleep}, so the concurrency bound can be verified under genuine thread
     * contention rather than inferred from fast, effectively-sequential calls. Forces
     * {@code getJobBulkSize()} to 1 so each document becomes its own batch/task, preserving this
     * test's original per-document concurrency contract now that batches (not documents) are the
     * unit of concurrent work.
     */
    private static final class ConcurrencyTrackingJob extends ChunkVectorJob {
        java.util.List<String> idsToReturn = java.util.List.of();
        int concurrency = 2;
        final java.util.concurrent.atomic.AtomicInteger current = new java.util.concurrent.atomic.AtomicInteger();
        final java.util.concurrent.atomic.AtomicInteger maxObserved = new java.util.concurrent.atomic.AtomicInteger();

        @Override
        protected boolean isEmbeddingClientAvailable() {
            return true;
        }

        @Override
        protected java.util.List<String> scrollPendingIds() {
            return idsToReturn;
        }

        @Override
        protected int getConcurrency() {
            return concurrency;
        }

        @Override
        protected int getJobBulkSize() {
            return 1;
        }

        @Override
        protected org.codelibs.fess.helper.ChunkVectorHelper getChunkVectorHelper() {
            return new org.codelibs.fess.helper.ChunkVectorHelper() {
                @Override
                public boolean isContentChunkerEnabled() {
                    return true;
                }

                @Override
                public java.util.Map<String, Boolean> processBatch(final java.util.List<String> ids) {
                    final int inFlight = current.incrementAndGet();
                    maxObserved.updateAndGet(prev -> Math.max(prev, inFlight));
                    try {
                        Thread.sleep(50L);
                    } catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        current.decrementAndGet();
                    }
                    final java.util.Map<String, Boolean> results = new java.util.HashMap<>();
                    for (final String id : ids) {
                        results.put(id, true);
                    }
                    return results;
                }
            };
        }
    }

    /**
     * Fake helper that deterministically throws for a configured subset of IDs. Forces
     * {@code getJobBulkSize()} to 1 so a synthetic whole-batch failure for one document's batch
     * is isolated from the other (single-document) batches, preserving this test's original
     * per-document fault-isolation contract.
     */
    private static final class FaultInjectingJob extends ChunkVectorJob {
        java.util.List<String> idsToReturn = java.util.List.of();
        final java.util.Set<String> failingIds = new java.util.HashSet<>();
        final java.util.List<String> processedIds = new java.util.concurrent.CopyOnWriteArrayList<>();

        @Override
        protected boolean isEmbeddingClientAvailable() {
            return true;
        }

        @Override
        protected java.util.List<String> scrollPendingIds() {
            return idsToReturn;
        }

        @Override
        protected int getConcurrency() {
            return 2;
        }

        @Override
        protected int getJobBulkSize() {
            return 1;
        }

        @Override
        protected org.codelibs.fess.helper.ChunkVectorHelper getChunkVectorHelper() {
            return new org.codelibs.fess.helper.ChunkVectorHelper() {
                @Override
                public boolean isContentChunkerEnabled() {
                    return true;
                }

                @Override
                public java.util.Map<String, Boolean> processBatch(final java.util.List<String> ids) {
                    final java.util.Map<String, Boolean> results = new java.util.HashMap<>();
                    for (final String id : ids) {
                        processedIds.add(id);
                        if (failingIds.contains(id)) {
                            throw new RuntimeException("synthetic failure for " + id);
                        }
                        results.put(id, true);
                    }
                    return results;
                }
            };
        }
    }
}
