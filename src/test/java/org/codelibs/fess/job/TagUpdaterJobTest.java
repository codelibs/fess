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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.codelibs.fesen.opensearch.action.DocWriteRequest;
import org.codelibs.fesen.opensearch.action.bulk.BulkAction;
import org.codelibs.fesen.opensearch.action.bulk.BulkRequestBuilder;
import org.codelibs.fesen.opensearch.action.search.SearchAction;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fesen.opensearch.action.update.UpdateRequest;
import org.codelibs.fesen.opensearch.script.Script;
import org.codelibs.fess.helper.LanguageHelper;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.helper.TagTypeHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchCondition;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.BooleanFunction;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class TagUpdaterJobTest extends UnitFessTestCase {

    /** The documents the walk hands out, in order. */
    private List<Map<String, Object>> docList;

    /** The tags of each URL. */
    private Map<String, Set<String>> tagMap;

    /** The URLs of each findTagValuesByUrls call. */
    private List<List<String>> lookups;

    private List<UpdateRequest> updates;

    private List<String> searchedIndices;

    private boolean userTagEnabled;

    private boolean failBulk;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        docList = new ArrayList<>();
        tagMap = new HashMap<>();
        lookups = new ArrayList<>();
        updates = new ArrayList<>();
        searchedIndices = new ArrayList<>();
        userTagEnabled = true;
        failBulk = false;
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isUserTagEnabled() {
                return userTagEnabled;
            }

            @Override
            public Integer getUserTagProcessBatchSizeAsInteger() {
                return 2;
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
        final LanguageHelper languageHelper = new LanguageHelper() {
            {
                langFields = new String[] { "content" };
            }
        };
        ComponentUtil.register(languageHelper, "languageHelper");
        ComponentUtil.register(new SearchEngineClient() {
            @Override
            public long scrollSearch(final String index, final SearchCondition<SearchRequestBuilder> condition,
                    final BooleanFunction<Map<String, Object>> cursor) {
                final SearchRequestBuilder builder = new SearchRequestBuilder(null, SearchAction.INSTANCE);
                condition.build(builder);
                final String[] includes = builder.request().source().fetchSource().includes();
                assertEquals(Set.of("url", "lang", "tag"), Set.of(includes));
                searchedIndices.add(index);
                for (final Map<String, Object> doc : docList) {
                    if (!cursor.apply(new HashMap<>(doc))) {
                        break;
                    }
                }
                return docList.size();
            }
        }, "searchEngineClient");
        ComponentUtil.register(new SearchHelper() {
            @Override
            public boolean bulkUpdate(final Consumer<BulkRequestBuilder> consumer) {
                final BulkRequestBuilder builder = new BulkRequestBuilder(null, BulkAction.INSTANCE);
                consumer.accept(builder);
                if (failBulk) {
                    throw new IllegalStateException("bulk failed");
                }
                for (final DocWriteRequest<?> request : builder.request().requests()) {
                    updates.add((UpdateRequest) request);
                }
                return true;
            }
        }, "searchHelper");
        ComponentUtil.register(new TagTypeHelper() {
            @Override
            public Map<String, Set<String>> findTagValuesByUrls(final Collection<String> urls) {
                lookups.add(new ArrayList<>(urls));
                final Map<String, Set<String>> result = new HashMap<>();
                for (final String url : urls) {
                    if (tagMap.containsKey(url)) {
                        result.put(url, tagMap.get(url));
                    }
                }
                return result;
            }
        }, "tagTypeHelper");
    }

    private void addDoc(final String id, final String url, final Object tag, final String lang) {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("_id", id);
        doc.put("url", url);
        if (tag != null) {
            doc.put("tag", tag);
        }
        if (lang != null) {
            doc.put("lang", lang);
        }
        docList.add(doc);
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

    private UpdateRequest updateOf(final String id) {
        return updates.stream().filter(r -> id.equals(r.id())).findFirst().orElse(null);
    }

    @Test
    public void test_execute_setsAndRemovesTags() {
        tagMap.put("http://a/", new LinkedHashSet<>(List.of("v1", "v2")));
        tagMap.put("http://c/", new LinkedHashSet<>(List.of("v3")));
        tagMap.put("http://d/", new LinkedHashSet<>(List.of("v4")));
        addDoc("d1", "http://a/", null, "ja"); // gains v1, v2
        addDoc("d2", "http://b/", "old", null); // loses its tag
        addDoc("d3", "http://c/", List.of("v3"), null); // unchanged
        addDoc("d4", "http://d/", "v4", null); // unchanged, single string
        addDoc("d5", "http://e/", null, null); // no tag, none wanted
        addDoc("d6", "http://c/", List.of("v3", "gone"), null); // drops gone

        final String result = new TagUpdaterJob().execute();

        assertEquals(List.of("fess.update"), searchedIndices);
        // one lookup per page of user.tag.process.batch.size documents
        assertEquals(List.of(List.of("http://a/", "http://b/"), List.of("http://c/", "http://d/"), List.of("http://e/", "http://c/")),
                lookups);
        assertEquals(3, updates.size());

        final UpdateRequest d1 = updateOf("d1");
        assertEquals("fess.update", d1.index());
        assertEquals(List.of("v1", "v2"), paramsOf(d1.script()).get("tag"));
        assertTrue(d1.script().getIdOrCode().startsWith("ctx._source.tag=params.tag;"));
        assertTrue(d1.script().getIdOrCode().endsWith(";ctx._source.content_ja=ctx._source.content"));
        assertTrue(d1.retryOnConflict() > 0);

        final UpdateRequest d2 = updateOf("d2");
        assertEquals("ctx._source.remove('tag')", d2.script().getIdOrCode());
        assertTrue(d2.retryOnConflict() > 0);

        assertEquals(List.of("v3"), paramsOf(updateOf("d6").script()).get("tag"));
        assertNull(updateOf("d3"));
        assertNull(updateOf("d4"));
        assertNull(updateOf("d5"));

        assertTrue(result.contains("3 documents updated"), result);
        assertTrue(result.contains("6 documents checked"), result);
    }

    @Test
    public void test_execute_bulkFailureContinues() {
        addDoc("d1", "http://b/", "old", null);
        addDoc("d2", "http://b/", "old", null);
        addDoc("d3", "http://b/", "old", null);
        failBulk = true;

        final String result = new TagUpdaterJob().execute();

        assertEquals(2, lookups.size());
        assertTrue(result.contains("0 documents updated"), result);
        assertTrue(result.contains("3 failed"), result);
    }

    @Test
    public void test_execute_disabled() {
        userTagEnabled = false;
        addDoc("d1", "http://b/", "old", null);

        final String result = new TagUpdaterJob().execute();

        assertTrue(searchedIndices.isEmpty());
        assertTrue(updates.isEmpty());
        assertTrue(result.contains("disabled"), result);
    }
}
