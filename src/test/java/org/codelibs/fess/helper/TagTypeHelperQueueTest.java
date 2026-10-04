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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.codelibs.fesen.opensearch.action.DocWriteRequest;
import org.codelibs.fesen.opensearch.action.bulk.BulkAction;
import org.codelibs.fesen.opensearch.action.bulk.BulkRequestBuilder;
import org.codelibs.fesen.opensearch.action.search.SearchAction;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fesen.opensearch.action.update.UpdateAction;
import org.codelibs.fesen.opensearch.action.update.UpdateRequest;
import org.codelibs.fesen.opensearch.action.update.UpdateRequestBuilder;
import org.codelibs.fesen.opensearch.core.common.bytes.BytesArray;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.index.query.TermQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.TermsQueryBuilder;
import org.codelibs.fesen.opensearch.script.Script;
import org.codelibs.fesen.opensearch.search.SearchHit;
import org.codelibs.fess.entity.TagChange;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.BooleanFunction;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchCondition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class TagTypeHelperQueueTest extends UnitFessTestCase {

    private TagTypeHelper tagTypeHelper;

    /** Documents of the fess index, keyed by URL. */
    private Map<String, List<Map<String, Object>>> docMap;

    /** What the search engine was asked to do, in order. */
    private List<String> events;

    /** Update requests of each bulk call. */
    private List<List<UpdateRequest>> bulkCalls;

    /** Update requests built by each updateByQuery call. */
    private List<UpdateRequest> updateByQueryRequests;

    /** The bulk calls, counted from 1, that throw. */
    private List<Integer> failingBulkCalls;

    private int queueMaxSize;

    /** Tag values that no document holds, so updateByQuery visits no document. */
    private List<String> unusedValues;

    private boolean failRefresh;

    private int batchSize;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        docMap = new LinkedHashMap<>();
        events = new ArrayList<>();
        bulkCalls = new ArrayList<>();
        updateByQueryRequests = new ArrayList<>();
        failingBulkCalls = new ArrayList<>();
        queueMaxSize = 10000;
        unusedValues = new ArrayList<>();
        failRefresh = false;
        batchSize = 100;
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isUserTagEnabled() {
                return true;
            }

            @Override
            public Integer getUserTagQueueMaxSizeAsInteger() {
                return queueMaxSize;
            }

            @Override
            public Integer getUserTagProcessBatchSizeAsInteger() {
                return batchSize;
            }

            @Override
            public String getIndexFieldTag() {
                return "tag";
            }

            @Override
            public String getIndexFieldUrl() {
                return "url";
            }

            @Override
            public String getIndexFieldLang() {
                return "lang";
            }

            @Override
            public String getIndexFieldId() {
                return "_id";
            }

            @Override
            public String getIndexDocumentUpdateIndex() {
                return "fess.update";
            }
        });
        final LanguageHelper languageHelper = new LanguageHelper();
        languageHelper.langFields = new String[] { "content", "title" };
        ComponentUtil.register(languageHelper, "languageHelper");
        ComponentUtil.register(new SearchEngineClient() {
            @Override
            public long scrollSearch(final String index, final SearchCondition<SearchRequestBuilder> condition,
                    final BooleanFunction<Map<String, Object>> cursor) {
                final SearchRequestBuilder builder = new SearchRequestBuilder(null, SearchAction.INSTANCE);
                condition.build(builder);
                final QueryBuilder query = builder.request().source().query();
                final TermsQueryBuilder termsQuery = (TermsQueryBuilder) query;
                assertEquals("url", termsQuery.fieldName());
                events.add("search:" + index + ":" + termsQuery.values());
                long count = 0;
                for (final Object url : termsQuery.values()) {
                    for (final Map<String, Object> doc : docMap.getOrDefault(url.toString(), List.of())) {
                        count++;
                        cursor.apply(new HashMap<>(doc));
                    }
                }
                return count;
            }

            @Override
            public long updateByQuery(final String index, final Function<SearchRequestBuilder, SearchRequestBuilder> option,
                    final BiFunction<UpdateRequestBuilder, SearchHit, UpdateRequestBuilder> builder) {
                final SearchRequestBuilder searchBuilder = option.apply(new SearchRequestBuilder(null, SearchAction.INSTANCE));
                final TermQueryBuilder termQuery = (TermQueryBuilder) searchBuilder.request().source().query();
                events.add("updateByQuery:" + index + ":" + termQuery.fieldName() + "=" + termQuery.value());
                if (unusedValues.contains(termQuery.value())) {
                    return 0;
                }
                final SearchHit hit = new SearchHit(0, "d1", null, null);
                hit.sourceRef(new BytesArray("{\"lang\":\"ja\"}"));
                final UpdateRequestBuilder requestBuilder =
                        builder.apply(new UpdateRequestBuilder(null, UpdateAction.INSTANCE, index, hit.getId()), hit);
                updateByQueryRequests.add(requestBuilder.request());
                return 1;
            }
        }, "searchEngineClient");
        ComponentUtil.register(new SearchHelper() {
            @Override
            public boolean bulkUpdate(final Consumer<BulkRequestBuilder> consumer) {
                final BulkRequestBuilder builder = new BulkRequestBuilder(null, BulkAction.INSTANCE);
                consumer.accept(builder);
                final List<UpdateRequest> requests = new ArrayList<>();
                for (final DocWriteRequest<?> request : builder.request().requests()) {
                    requests.add((UpdateRequest) request);
                }
                bulkCalls.add(requests);
                events.add("bulk:" + requests.size());
                if (failingBulkCalls.contains(bulkCalls.size())) {
                    throw new IllegalStateException("bulk failed");
                }
                return true;
            }
        }, "searchHelper");
        tagTypeHelper = new TagTypeHelper() {
            @Override
            protected void refreshUpdateIndex(final String index) {
                events.add("refresh:" + index);
                if (failRefresh) {
                    throw new IllegalStateException("refresh failed");
                }
            }
        };
    }

    private void addDoc(final String id, final String url, final String lang) {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("_id", id);
        doc.put("url", url);
        if (lang != null) {
            doc.put("lang", lang);
        }
        docMap.computeIfAbsent(url, k -> new ArrayList<>()).add(doc);
    }

    private static Object scriptField(final Script script, final String name) {
        try {
            final java.lang.reflect.Field field = Script.class.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(script);
        } catch (final ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> paramsOf(final Script script) {
        return (Map<String, Object>) scriptField(script, "params");
    }

    @SuppressWarnings("unchecked")
    private static List<String> param(final UpdateRequest request, final String name) {
        return (List<String>) paramsOf(request.script()).get(name);
    }

    @Test
    public void test_processQueue_addThenRemove() {
        addDoc("d1", "http://example.com/a", null);
        assertTrue(tagTypeHelper.enqueue(TagChange.add("v", "http://example.com/a")));
        assertTrue(tagTypeHelper.enqueue(TagChange.remove("v", "http://example.com/a")));

        assertEquals(2, tagTypeHelper.processQueue());

        assertEquals(1, bulkCalls.size());
        assertEquals(1, bulkCalls.get(0).size());
        final UpdateRequest request = bulkCalls.get(0).get(0);
        assertEquals("fess.update", request.index());
        assertEquals("d1", request.id());
        assertEquals(List.of(), param(request, "add"));
        assertEquals(List.of("v"), param(request, "remove"));
        assertTrue(request.retryOnConflict() > 0);
        assertEquals("painless", scriptField(request.script(), "lang"));
        // the queue is drained
        assertEquals(0, tagTypeHelper.processQueue());
        assertEquals(1, bulkCalls.size());
    }

    @Test
    public void test_processQueue_deleteThenAdd() {
        addDoc("d1", "http://example.com/a", null);
        assertTrue(tagTypeHelper.enqueue(TagChange.remove("v", "http://example.com/a")));
        assertTrue(tagTypeHelper.enqueue(TagChange.delete("v")));
        assertTrue(tagTypeHelper.enqueue(TagChange.add("v", "http://example.com/a")));

        assertEquals(3, tagTypeHelper.processQueue());

        assertEquals(List.of("search:fess.update:[http://example.com/a]", "bulk:1", "refresh:fess.update",
                "updateByQuery:fess.update:tag=v", "search:fess.update:[http://example.com/a]", "bulk:1"), events);
        assertEquals(List.of("v"), param(bulkCalls.get(0).get(0), "remove"));
        final UpdateRequest deleteRequest = updateByQueryRequests.get(0);
        assertEquals(List.of(), param(deleteRequest, "add"));
        assertEquals(List.of("v"), param(deleteRequest, "remove"));
        assertTrue(deleteRequest.retryOnConflict() > 0);
        assertTrue(deleteRequest.script().getIdOrCode().contains(";ctx._source.content_ja=ctx._source.content"));
        final UpdateRequest addRequest = bulkCalls.get(1).get(0);
        assertEquals(List.of("v"), param(addRequest, "add"));
        assertEquals(List.of(), param(addRequest, "remove"));
    }

    @Test
    public void test_processQueue_rename() {
        assertTrue(tagTypeHelper.enqueue(TagChange.rename("old", "new")));

        assertEquals(1, tagTypeHelper.processQueue());

        assertEquals(List.of("updateByQuery:fess.update:tag=old"), events);
        final UpdateRequest request = updateByQueryRequests.get(0);
        assertEquals(List.of("new"), param(request, "add"));
        assertEquals(List.of("old"), param(request, "remove"));
    }

    @Test
    public void test_flush_updatesAllDocsOfUrl() {
        final String url = "http://example.com/a b%20c#frag";
        addDoc("d1", url, null);
        addDoc("d2", url, "en");
        addDoc("d3", "http://example.com/other", null);
        assertTrue(tagTypeHelper.enqueue(TagChange.add("v", url)));

        assertEquals(1, tagTypeHelper.processQueue());

        assertEquals(List.of("search:fess.update:[" + url + "]", "bulk:2"), events);
        final List<UpdateRequest> requests = bulkCalls.get(0);
        assertEquals("d1", requests.get(0).id());
        assertEquals("d2", requests.get(1).id());
        for (final UpdateRequest request : requests) {
            assertEquals(List.of("v"), param(request, "add"));
            assertEquals(List.of(), param(request, "remove"));
        }
    }

    @Test
    public void test_flush_appendsLanguageScript() {
        addDoc("d1", "http://example.com/a", "ja");
        addDoc("d2", "http://example.com/b", null);
        assertTrue(tagTypeHelper.enqueue(TagChange.add("v", "http://example.com/a")));
        assertTrue(tagTypeHelper.enqueue(TagChange.add("v", "http://example.com/b")));

        tagTypeHelper.processQueue();

        final List<UpdateRequest> requests = bulkCalls.get(0);
        final String jaSource = requests.get(0).script().getIdOrCode();
        assertTrue(jaSource.startsWith(TagTypeHelper.buildUpdateTagScript("tag")), jaSource);
        assertTrue(jaSource.endsWith(";ctx._source.content_ja=ctx._source.content;ctx._source.title_ja=ctx._source.title"), jaSource);
        assertEquals(TagTypeHelper.buildUpdateTagScript("tag"), requests.get(1).script().getIdOrCode());
    }

    @Test
    public void test_enqueue_overflowDropped() {
        queueMaxSize = 2;
        addDoc("d1", "http://example.com/a", null);
        assertTrue(tagTypeHelper.enqueue(TagChange.add("v1", "http://example.com/a")));
        assertTrue(tagTypeHelper.enqueue(TagChange.add("v2", "http://example.com/a")));
        assertFalse(tagTypeHelper.enqueue(TagChange.add("v3", "http://example.com/a")));
        assertFalse(tagTypeHelper.enqueue(null));

        assertEquals(2, tagTypeHelper.processQueue());
        assertEquals(List.of("v1", "v2"), param(bulkCalls.get(0).get(0), "add"));
        // room again after the queue is drained
        assertTrue(tagTypeHelper.enqueue(TagChange.add("v3", "http://example.com/a")));
    }

    @Test
    public void test_processQueue_failureContinues() {
        batchSize = 1;
        addDoc("d1", "http://example.com/a", null);
        addDoc("d2", "http://example.com/b", null);
        failingBulkCalls.add(1);
        assertTrue(tagTypeHelper.enqueue(TagChange.add("v", "http://example.com/a")));
        assertTrue(tagTypeHelper.enqueue(TagChange.add("v", "http://example.com/b")));

        assertEquals(2, tagTypeHelper.processQueue());

        assertEquals(List.of("search:fess.update:[http://example.com/a]", "bulk:1", "search:fess.update:[http://example.com/b]", "bulk:1"),
                events);
        assertEquals("d2", bulkCalls.get(1).get(0).id());
    }

    @Test
    public void test_processQueue_updateByQueryFailureContinues() {
        addDoc("d1", "http://example.com/a", null);
        ComponentUtil.register(new SearchEngineClient() {
            @Override
            public long scrollSearch(final String index, final SearchCondition<SearchRequestBuilder> condition,
                    final BooleanFunction<Map<String, Object>> cursor) {
                events.add("search");
                return cursor.apply(new HashMap<>(docMap.get("http://example.com/a").get(0))) ? 1 : 0;
            }

            @Override
            public long updateByQuery(final String index, final Function<SearchRequestBuilder, SearchRequestBuilder> option,
                    final BiFunction<UpdateRequestBuilder, SearchHit, UpdateRequestBuilder> builder) {
                events.add("updateByQuery");
                throw new IllegalStateException("item failed");
            }
        }, "searchEngineClient");
        assertTrue(tagTypeHelper.enqueue(TagChange.delete("v")));
        assertTrue(tagTypeHelper.enqueue(TagChange.add("w", "http://example.com/a")));

        assertEquals(2, tagTypeHelper.processQueue());

        assertEquals(List.of("updateByQuery", "search", "bulk:1"), events);
    }

    @Test
    public void test_processQueue_concurrentEnqueue() throws Exception {
        addDoc("d1", "http://example.com/a", null);
        final Thread thread = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                tagTypeHelper.enqueue(TagChange.add("v" + i, "http://example.com/a"));
            }
        });
        thread.start();
        int processed = 0;
        while (thread.isAlive()) {
            processed += tagTypeHelper.processQueue();
            Thread.sleep(1L);
        }
        thread.join();
        processed += tagTypeHelper.processQueue();
        assertEquals(50, processed);
    }

    @Test
    public void test_processQueue_addThenDelete_refreshesFirst() {
        addDoc("d1", "http://example.com/a", null);
        assertTrue(tagTypeHelper.enqueue(TagChange.add("v", "http://example.com/a")));
        assertTrue(tagTypeHelper.enqueue(TagChange.delete("v")));

        assertEquals(2, tagTypeHelper.processQueue());

        assertEquals(
                List.of("search:fess.update:[http://example.com/a]", "bulk:1", "refresh:fess.update", "updateByQuery:fess.update:tag=v"),
                events);
    }

    @Test
    public void test_processQueue_renameChain_refreshesBetween() {
        assertTrue(tagTypeHelper.enqueue(TagChange.rename("A", "B")));
        assertTrue(tagTypeHelper.enqueue(TagChange.rename("B", "C")));

        assertEquals(2, tagTypeHelper.processQueue());

        assertEquals(List.of("updateByQuery:fess.update:tag=A", "refresh:fess.update", "updateByQuery:fess.update:tag=B"), events);
        assertEquals(List.of("C"), param(updateByQueryRequests.get(1), "add"));
        assertEquals(List.of("B"), param(updateByQueryRequests.get(1), "remove"));
    }

    @Test
    public void test_processQueue_addThenRename_refreshesFirst() {
        addDoc("d1", "http://example.com/a", null);
        assertTrue(tagTypeHelper.enqueue(TagChange.add("old", "http://example.com/a")));
        assertTrue(tagTypeHelper.enqueue(TagChange.rename("old", "new")));

        assertEquals(2, tagTypeHelper.processQueue());

        assertEquals(
                List.of("search:fess.update:[http://example.com/a]", "bulk:1", "refresh:fess.update", "updateByQuery:fess.update:tag=old"),
                events);
    }

    @Test
    public void test_processQueue_noWrite_skipsRefresh() {
        // the URL has no document, so nothing is written before the deletions
        assertTrue(tagTypeHelper.enqueue(TagChange.add("v", "http://example.com/none")));
        unusedValues.add("v");
        assertTrue(tagTypeHelper.enqueue(TagChange.delete("v")));
        assertTrue(tagTypeHelper.enqueue(TagChange.delete("w")));

        assertEquals(3, tagTypeHelper.processQueue());

        assertEquals(List.of("search:fess.update:[http://example.com/none]", "updateByQuery:fess.update:tag=v",
                "updateByQuery:fess.update:tag=w"), events);
    }

    @Test
    public void test_processQueue_refreshOncePerWrite() {
        assertTrue(tagTypeHelper.enqueue(TagChange.rename("A", "B")));
        unusedValues.add("B");
        assertTrue(tagTypeHelper.enqueue(TagChange.delete("B")));
        assertTrue(tagTypeHelper.enqueue(TagChange.delete("C")));

        tagTypeHelper.processQueue();

        assertEquals(List.of("updateByQuery:fess.update:tag=A", "refresh:fess.update", "updateByQuery:fess.update:tag=B",
                "updateByQuery:fess.update:tag=C"), events);
    }

    @Test
    public void test_processQueue_refreshFailureContinues() {
        failRefresh = true;
        assertTrue(tagTypeHelper.enqueue(TagChange.rename("A", "B")));
        assertTrue(tagTypeHelper.enqueue(TagChange.rename("B", "C")));

        assertEquals(2, tagTypeHelper.processQueue());

        assertEquals(List.of("updateByQuery:fess.update:tag=A", "refresh:fess.update", "updateByQuery:fess.update:tag=B"), events);
    }
}
