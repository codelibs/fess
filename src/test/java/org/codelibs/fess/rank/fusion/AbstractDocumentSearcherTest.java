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
package org.codelibs.fess.rank.fusion;

import java.util.List;

import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchCondition;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;
import org.codelibs.fesen.opensearch.action.search.SearchAction;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fesen.opensearch.action.search.SearchResponse;
import org.codelibs.fesen.opensearch.action.search.ShardSearchFailure;
import org.codelibs.fesen.opensearch.search.SearchHits;
import org.codelibs.fesen.opensearch.search.internal.InternalSearchResponse;

/**
 * Tests how a response that is not complete - because the query timeout elapsed, or because a
 * shard failed - is reported and logged.
 *
 * <p>The two are different conditions. The engine stops collecting when the query timeout
 * elapses but still counts every shard as successful, so a timeout shows up only in
 * {@code timed_out}. A shard that throws is subtracted from the successful count, and nothing
 * about it is a timeout.</p>
 */
public class AbstractDocumentSearcherTest extends UnitFessTestCase {

    private static final String SHARD_FAILURE_REASON = "Missing value for field [boost]";

    // -------------------------------------------------------------------------------------
    //                                                                          partial result
    //                                                                          --------------

    @Test
    public void test_processResponse_timedOutResponseIsPartial() {
        final SearchResult result = processResponse(response(true, 5, 5));
        assertTrue(result.isPartialResults(), "a search the engine stopped collecting must not be reported as complete");
        assertTrue(result.isTimedOut());
        assertFalse(result.isShardFailed(), "every shard answered");
    }

    @Test
    public void test_processResponse_shardFailureIsPartial() {
        final SearchResult result = processResponse(response(false, 5, 4));
        assertTrue(result.isPartialResults(), "a search that lost a shard must not be reported as complete");
        assertTrue(result.isShardFailed());
        assertFalse(result.isTimedOut(), "a shard failure is not a timeout");
    }

    @Test
    public void test_processResponse_timeoutAndShardFailureAreBothKept() {
        final SearchResult result = processResponse(response(true, 5, 4));
        assertTrue(result.isPartialResults());
        assertTrue(result.isTimedOut());
        assertTrue(result.isShardFailed());
    }

    @Test
    public void test_processResponse_completeResponseIsNotPartial() {
        final SearchResult result = processResponse(response(false, 5, 5));
        assertFalse(result.isPartialResults());
        assertFalse(result.isTimedOut());
        assertFalse(result.isShardFailed());
    }

    // -------------------------------------------------------------------------------------
    //                                                                                 logging
    //                                                                                 -------

    @Test
    public void test_sendRequest_logsATimeoutAsATimeout() {
        final List<String> warnings = sendRequestAndCaptureWarnings(response(true, 5, 5));
        assertEquals(1, warnings.size(), warnings.toString());
        assertTrue(warnings.get(0).startsWith("[SEARCH TIMEOUT] {\"exec_time\":52,"), warnings.get(0));
    }

    @Test
    public void test_sendRequest_logsAShardFailureAsAShardFailure() {
        final List<String> warnings = sendRequestAndCaptureWarnings(response(false, 5, 4));
        assertEquals(1, warnings.size(), warnings.toString());
        // a shard failure used to be logged as [SEARCH TIMEOUT], which sent operators looking at
        // the query timeout for a search that had taken a few milliseconds
        assertTrue(warnings.get(0).startsWith("[SEARCH SHARD FAILURE] {\"exec_time\":52,"), warnings.get(0));
        assertTrue(warnings.get(0).contains(SHARD_FAILURE_REASON),
                "the failure reason is the only record of the cause: " + warnings.get(0));
    }

    @Test
    public void test_sendRequest_logsBothWhenATimeoutAndAShardFailureCoincide() {
        final List<String> warnings = sendRequestAndCaptureWarnings(response(true, 5, 4));
        assertEquals(2, warnings.size(), warnings.toString());
        assertTrue(warnings.stream().anyMatch(w -> w.startsWith("[SEARCH TIMEOUT] ")), warnings.toString());
        assertTrue(warnings.stream().anyMatch(w -> w.startsWith("[SEARCH SHARD FAILURE] ")), warnings.toString());
    }

    @Test
    public void test_sendRequest_logsNothingForACompleteResponse() {
        final List<String> warnings = sendRequestAndCaptureWarnings(response(false, 5, 5));
        assertTrue(warnings.isEmpty(), warnings.toString());
    }

    // -------------------------------------------------------------------------------------
    //                                                                            test helpers
    //                                                                            ------------

    private SearchResult processResponse(final SearchResponse response) {
        ComponentUtil.register(new QueryHelper(), "queryHelper");
        return new DefaultSearcher().processResponse(OptionalEntity.of(response));
    }

    private List<String> sendRequestAndCaptureWarnings(final SearchResponse response) {
        ComponentUtil.register(new SearchEngineClient() {
            @Override
            public <T> T search(final String index, final SearchCondition<SearchRequestBuilder> condition,
                    final SearchEngineClient.SearchResult<T, SearchRequestBuilder, SearchResponse> searchResult) {
                return searchResult.build(new SearchRequestBuilder(this, SearchAction.INSTANCE), 52L, OptionalEntity.of(response));
            }
        }, "searchEngineClient");
        final LogCapturingAppender appender = LogCapturingAppender.attach(AbstractDocumentSearcher.class);
        try {
            new DefaultSearcher().sendRequest(null, requestBuilder -> true);
            return appender.warnings();
        } finally {
            appender.detach();
        }
    }

    private static SearchResponse response(final boolean timedOut, final int totalShards, final int successfulShards) {
        final ShardSearchFailure[] failures = totalShards == successfulShards ? ShardSearchFailure.EMPTY_ARRAY
                : new ShardSearchFailure[] { new ShardSearchFailure(new IllegalStateException(SHARD_FAILURE_REASON)) };
        return new SearchResponse(new InternalSearchResponse(SearchHits.empty(true), null, null, null, timedOut, null, 1), null,
                totalShards, successfulShards, 0, 13L, failures, SearchResponse.Clusters.EMPTY);
    }
}
