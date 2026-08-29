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
package org.codelibs.fess.opensearch.config.allcommon;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.config.cbean.BadWordCB;
import org.codelibs.fess.opensearch.config.exbhv.BadWordBhv;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.opensearch.action.ActionRequest;
import org.opensearch.action.ActionType;
import org.opensearch.action.search.CreatePitAction;
import org.opensearch.action.search.CreatePitRequest;
import org.opensearch.action.search.CreatePitResponse;
import org.opensearch.action.search.DeletePitAction;
import org.opensearch.action.search.DeletePitRequest;
import org.opensearch.action.search.DeletePitResponse;
import org.opensearch.action.search.SearchAction;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.support.PlainActionFuture;
import org.opensearch.common.action.ActionFuture;
import org.opensearch.core.action.ActionListener;
import org.opensearch.common.xcontent.json.JsonXContent;
import org.opensearch.core.action.ActionResponse;
import org.opensearch.core.xcontent.DeprecationHandler;
import org.opensearch.core.xcontent.NamedXContentRegistry;
import org.opensearch.core.xcontent.XContentParser;
import org.opensearch.action.search.ShardSearchFailure;
import org.opensearch.search.SearchHits;

/**
 * Covers the point-in-time pager the esflute-generated behaviors walk their cursor selects and
 * their query delete with. The generated classes had no test at all, and a misused point in time
 * does not fail over HTTP -- it hangs -- so the contract is pinned here rather than left to a
 * live cluster to discover.
 */
public class EsAbstractBehaviorPitTest extends UnitFessTestCase {

    /** Requests the stub client was asked to execute, in order. */
    private final List<ActionRequest> executed = new ArrayList<>();

    /** Search responses the stub client hands back, one per search. */
    private final List<SearchResponse> pages = new ArrayList<>();

    /**
     * The search_after value each search carried, snapshotted at execute time. The pager reuses one
     * request object across pages, so inspecting it afterwards would only ever show the last state.
     */
    private final List<Object[]> searchAfters = new ArrayList<>();

    private BadWordBhv createBehavior() throws Exception {
        final BadWordBhv behavior = new BadWordBhv();
        final SearchEngineClient stub = new SearchEngineClient() {
            @Override
            public SearchRequestBuilder prepareSearch(final String... indices) {
                // Propagate the indices so a search that wrongly names one is visible to the test.
                return new SearchRequestBuilder(this, SearchAction.INSTANCE).setIndices(indices);
            }

            @Override
            public void deletePits(final DeletePitRequest request, final ActionListener<DeletePitResponse> listener) {
                executed.add(request);
                listener.onResponse(new DeletePitResponse(List.of()));
            }

            @SuppressWarnings("unchecked")
            @Override
            public <Request extends ActionRequest, Response extends ActionResponse> ActionFuture<Response> execute(
                    final ActionType<Response> action, final Request request) {
                executed.add(request);
                final PlainActionFuture<Response> future = PlainActionFuture.newFuture();
                if (CreatePitAction.INSTANCE.equals(action)) {
                    future.onResponse(
                            (Response) new CreatePitResponse("pit-1", System.currentTimeMillis(), 1, 1, 0, 0, new ShardSearchFailure[0]));
                } else if (SearchAction.INSTANCE.equals(action)) {
                    searchAfters.add(((SearchRequest) request).source().searchAfter());
                    final int index = (int) executed.stream().filter(r -> r instanceof SearchRequest).count() - 1;
                    future.onResponse((Response) (index < pages.size() ? pages.get(index) : emptyPage()));
                } else {
                    future.onResponse(null);
                }
                return future;
            }
        };
        final Field field = EsAbstractBehavior.class.getDeclaredField("client");
        field.setAccessible(true);
        field.set(behavior, stub);
        return behavior;
    }

    /**
     * Builds a search response holding one hit per id, each carrying a sort value, as one would
     * arrive over HTTP.
     */
    private SearchResponse page(final String... ids) throws Exception {
        final StringBuilder buf = new StringBuilder();
        buf.append("{\"took\":1,\"timed_out\":false,\"_shards\":{\"total\":1,\"successful\":1,\"skipped\":0,\"failed\":0},");
        buf.append("\"hits\":{\"total\":{\"value\":").append(ids.length).append(",\"relation\":\"eq\"},\"max_score\":null,\"hits\":[");
        for (int i = 0; i < ids.length; i++) {
            if (i > 0) {
                buf.append(',');
            }
            buf.append("{\"_index\":\"t\",\"_id\":\"")
                    .append(ids[i])
                    .append("\",\"_score\":null,\"_source\":{},\"sort\":[")
                    .append(100 + i)
                    .append("]}");
        }
        buf.append("]}}");
        try (XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY,
                DeprecationHandler.IGNORE_DEPRECATIONS, buf.toString())) {
            return SearchResponse.fromXContent(parser);
        }
    }

    private SearchResponse emptyPage() {
        try {
            return page();
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private CreatePitRequest createPitRequest() {
        return (CreatePitRequest) executed.stream().filter(r -> r instanceof CreatePitRequest).findFirst().orElseThrow();
    }

    private List<SearchRequest> searchRequests() {
        return executed.stream().filter(r -> r instanceof SearchRequest).map(r -> (SearchRequest) r).toList();
    }

    /**
     * The walk pages with search_after until a page comes back empty, and every page is handed to
     * the handler exactly once.
     */
    @Test
    public void test_pitSearch_pagesUntilExhausted() throws Exception {
        pages.add(page("a", "b"));
        pages.add(page("c", "d"));
        pages.add(page());
        final BadWordBhv behavior = createBehavior();

        final List<String> seen = new ArrayList<>();
        behavior.pitSearch(new BadWordCB(), 2, "1m", (final SearchHits hits) -> {
            hits.forEach(hit -> seen.add(hit.getId()));
            return true;
        });

        assertEquals(List.of("a", "b", "c", "d"), seen);
        // Three searches: two full pages and the empty one that ends the walk.
        assertEquals(3, searchRequests().size());
    }

    /**
     * The second and later searches carry the previous page's last sort value as search_after,
     * which is what makes the walk advance instead of repeating the first page forever.
     */
    @Test
    public void test_pitSearch_feedsSearchAfterFromPreviousPage() throws Exception {
        pages.add(page("a", "b"));
        pages.add(page());
        final BadWordBhv behavior = createBehavior();

        behavior.pitSearch(new BadWordCB(), 2, "1m", hits -> true);

        assertEquals(2, searchAfters.size());
        assertNull(searchAfters.get(0));
        assertNotNull(searchAfters.get(1));
        // The first page's last hit was given sort value 101.
        assertEquals(101, ((Number) searchAfters.get(1)[0]).intValue());
    }

    /**
     * The index is bound by the point in time, never by the search. OpenSearch answers 400
     * "[indices] cannot be used with point in time", and over HTTP that 400 hangs.
     */
    @Test
    public void test_pitSearch_indexGoesOnThePitNotTheSearch() throws Exception {
        pages.add(page());
        final BadWordBhv behavior = createBehavior();

        behavior.pitSearch(new BadWordCB(), 10, "1m", hits -> true);

        assertEquals(1, createPitRequest().indices().length);
        assertEquals("fess_config.bad_word", createPitRequest().indices()[0]);
        for (final SearchRequest search : searchRequests()) {
            assertEquals(0, search.indices().length);
        }
    }

    /**
     * A preference set on the condition bean is moved onto the create request and cleared from the
     * search, which OpenSearch would otherwise reject.
     */
    @Test
    public void test_pitSearch_preferenceMovesOntoThePit() throws Exception {
        pages.add(page());
        final BadWordBhv behavior = createBehavior();
        final BadWordCB cb = new BadWordCB();
        cb.setPreference("_local");

        behavior.pitSearch(cb, 10, "1m", hits -> true);

        assertEquals("_local", createPitRequest().getPreference());
        for (final SearchRequest search : searchRequests()) {
            assertNull(search.preference());
        }
    }

    /**
     * The sort ends with the _shard_doc tiebreaker, without which search_after has no total order
     * to walk and can repeat or skip documents.
     */
    @Test
    public void test_pitSearch_appendsShardDocTiebreaker() throws Exception {
        pages.add(page());
        final BadWordBhv behavior = createBehavior();

        behavior.pitSearch(new BadWordCB(), 10, "1m", hits -> true);

        final SearchRequest search = searchRequests().get(0);
        assertNotNull(search.source().sorts());
        assertFalse(search.source().sorts().isEmpty());
        final String last = search.source().sorts().get(search.source().sorts().size() - 1).getWriteableName();
        assertEquals("_shard_doc", last);
    }

    /**
     * The point in time is released once the walk ends, so a run cannot leave contexts behind.
     */
    @Test
    public void test_pitSearch_releasesThePit() throws Exception {
        pages.add(page("a"));
        pages.add(page());
        final BadWordBhv behavior = createBehavior();

        behavior.pitSearch(new BadWordCB(), 1, "1m", hits -> true);

        final DeletePitRequest delete =
                (DeletePitRequest) executed.stream().filter(r -> r instanceof DeletePitRequest).findFirst().orElseThrow();
        assertEquals(List.of("pit-1"), delete.getPitIds());
    }

    /**
     * A handler returning false ends the walk immediately, and the point in time is still released.
     */
    @Test
    public void test_pitSearch_handlerReturningFalseStopsAndStillReleases() throws Exception {
        pages.add(page("a", "b"));
        pages.add(page("c", "d"));
        pages.add(page());
        final BadWordBhv behavior = createBehavior();

        final List<String> seen = new ArrayList<>();
        behavior.pitSearch(new BadWordCB(), 2, "1m", (final SearchHits hits) -> {
            hits.forEach(hit -> seen.add(hit.getId()));
            return false;
        });

        assertEquals(List.of("a", "b"), seen);
        assertEquals(1, searchRequests().size());
        assertTrue(executed.stream().anyMatch(r -> r instanceof DeletePitRequest));
    }
}
