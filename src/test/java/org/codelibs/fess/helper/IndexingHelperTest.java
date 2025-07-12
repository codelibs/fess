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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;
import org.codelibs.fess.thumbnail.ThumbnailManager;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocList;
import org.dbflute.optional.OptionalEntity;
import org.opensearch.action.bulk.BulkItemResponse;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.index.IndexAction;
import org.opensearch.action.index.IndexRequestBuilder;
import org.opensearch.action.search.SearchAction;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.TermQueryBuilder;
import org.opensearch.index.query.TermsQueryBuilder;

public class IndexingHelperTest extends UnitFessTestCase {
    private IndexingHelper indexingHelper;

    private long documentSizeByQuery = 0L;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        indexingHelper = new IndexingHelper() {
            @Override
            protected long getDocumentSizeByQuery(final SearchEngineClient searchEngineClient, final QueryBuilder queryBuilder,
                    final FessConfig fessConfig) {
                return documentSizeByQuery;
            }

            @Override
            protected int refreshIndex(final SearchEngineClient searchEngineClient, final String index) {
                return 200;
            }
        };
        indexingHelper.setMaxRetryCount(5);
        indexingHelper.setDefaultRowSize(100);
        indexingHelper.setRequestInterval(500);
        ComponentUtil.register(indexingHelper, "indexingHelper");
        ComponentUtil.register(new ThumbnailManager(), "thumbnailManager");
        final CrawlingConfigHelper crawlingConfigHelper = new CrawlingConfigHelper();
        crawlingConfigHelper.init();
        ComponentUtil.register(crawlingConfigHelper, "crawlingConfigHelper");
        ComponentUtil.register(new WebConfigService() {
            @Override
            public OptionalEntity<WebConfig> getWebConfig(final String id) {
                final WebConfig webConfig = new WebConfig();
                webConfig.setId(id);
                return OptionalEntity.of(webConfig);
            }
        }, WebConfigService.class.getCanonicalName());
    }

    @Override
    public void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    public void test_sendDocuments() {
        documentSizeByQuery = 0L;
        final AtomicReference<String> sentIndex = new AtomicReference<>();
        final List<Map<String, Object>> sentDocList = new ArrayList<>();
        final SearchEngineClient client = new SearchEngineClient() {
            @Override
            public BulkResponse addAll(final String index, final List<Map<String, Object>> docList,
                    final BiConsumer<Map<String, Object>, IndexRequestBuilder> options) {
                sentIndex.set(index);
                docList.forEach(x -> options.accept(x, new IndexRequestBuilder(this, IndexAction.INSTANCE)));
                sentDocList.addAll(docList);
                return new BulkResponse(new BulkItemResponse[0], documentSizeByQuery);
            }

            @Override
            public List<Map<String, Object>> getDocumentList(final String index, final SearchCondition<SearchRequestBuilder> condition) {
                return Collections.emptyList();
            }
        };
        ComponentUtil.register(client, "searchEngineClient");
        final DocList docList = new DocList();
        indexingHelper.sendDocuments(client, docList);
        assertEquals(0, docList.size());
        assertEquals(0, sentDocList.size());
        assertNull(sentIndex.get());

        sentIndex.set(null);
        sentDocList.clear();
        docList.add(new HashMap<>(Map.of(//
                "_id", "001", //
                "config_id", "W01", //
                "url", "http://test.com/001"//
        )));
        docList.add(new HashMap<>(Map.of(//
                "_id", "002", //
                "thumbnail", "http://test.com/002", //
                "url", "http://test.com/002"//
        )));
        indexingHelper.sendDocuments(client, docList);
        assertEquals(0, docList.size());
        assertEquals(2, sentDocList.size());
        assertEquals("fess.update", sentIndex.get());
    }

    public void test_deleteOldDocuments() {
        documentSizeByQuery = 0L;
        final List<String> deletedDocIdList = new ArrayList<>();
        final List<Map<String, Object>> oldDocList = new ArrayList<>();
        final SearchEngineClient client = new SearchEngineClient() {
            @Override
            public List<Map<String, Object>> getDocumentList(final String index, final SearchCondition<SearchRequestBuilder> condition) {
                return oldDocList;
            }

            @Override
            public long deleteByQuery(final String index, final QueryBuilder queryBuilder) {
                if (queryBuilder instanceof final TermsQueryBuilder termsQueryBuilder) {
                    termsQueryBuilder.values().stream().forEach(o -> deletedDocIdList.add(o.toString()));
                }
                return deletedDocIdList.size();
            }
        };
        ComponentUtil.register(client, "searchEngineClient");
        final DocList docList = new DocList();
        assertEquals(0, indexingHelper.deleteOldDocuments(client, docList));
        assertEquals(0, docList.size());
        assertEquals(0, deletedDocIdList.size());

        docList.clear();
        deletedDocIdList.clear();
        docList.add(new HashMap<>(Map.of(//
                "config_id", "W01", //
                "doc_id", "1", //
                "url", "http://test.com/001"//
        )));
        documentSizeByQuery = oldDocList.size();
        assertEquals(0, indexingHelper.deleteOldDocuments(client, docList));
        assertEquals(1, docList.size());
        assertEquals(0, deletedDocIdList.size());

        docList.clear();
        deletedDocIdList.clear();
        docList.add(new HashMap<>(Map.of(//
                "_id", "001", //
                "doc_id", "1", //
                "url", "http://test.com/001"//
        )));
        documentSizeByQuery = oldDocList.size();
        assertEquals(0, indexingHelper.deleteOldDocuments(client, docList));
        assertEquals(1, docList.size());
        assertEquals(0, deletedDocIdList.size());

        docList.clear();
        deletedDocIdList.clear();
        docList.add(new HashMap<>(Map.of(//
                "_id", "001", //
                "config_id", "W01", //
                "doc_id", "1", //
                "url", "http://test.com/001"//
        )));
        documentSizeByQuery = oldDocList.size();
        assertEquals(0, indexingHelper.deleteOldDocuments(client, docList));
        assertEquals(1, docList.size());
        assertEquals(0, deletedDocIdList.size());

        deletedDocIdList.clear();
        oldDocList.add(new HashMap<>(Map.of(//
                "_id", "001", // same id
                "config_id", "W01", //
                "doc_id", "1", //
                "url", "http://test.com/001"//
        )));
        documentSizeByQuery = oldDocList.size();
        assertEquals(0, indexingHelper.deleteOldDocuments(client, docList));
        assertEquals(1, docList.size());
        assertEquals(0, deletedDocIdList.size());

        deletedDocIdList.clear();
        oldDocList.clear();
        oldDocList.add(new HashMap<>(Map.of(//
                "_id", "001_OLD", // same id
                "config_id", "W01", //
                "doc_id", "1", //
                "url", "http://test.com/001"//
        )));
        documentSizeByQuery = oldDocList.size();
        assertEquals(1, indexingHelper.deleteOldDocuments(client, docList));
        assertEquals(1, docList.size());
        assertEquals(1, deletedDocIdList.size());
        assertEquals("1", deletedDocIdList.get(0));
    }

    public void test_updateDocument() {
        final Map<String, String> resultMap = new HashMap<>();
        final SearchEngineClient client = new SearchEngineClient() {
            @Override
            public boolean update(final String index, final String id, final String field, final Object value) {
                resultMap.put("index", index);
                resultMap.put("id", id);
                resultMap.put("field", field);
                resultMap.put("value", value.toString());
                return true;
            }
        };
        ComponentUtil.register(client, "searchEngineClient");

        final String id = "001";
        final String field = "content";
        final String value = "test";

        assertTrue(indexingHelper.updateDocument(client, id, field, value));
        assertEquals("fess.update", resultMap.get("index"));
        assertEquals(id, resultMap.get("id"));
        assertEquals(field, resultMap.get("field"));
        assertEquals(value, resultMap.get("value"));
    }

    public void test_deleteDocument() {
        final Map<String, String> resultMap = new HashMap<>();
        final SearchEngineClient client = new SearchEngineClient() {
            @Override
            public boolean delete(final String index, final String id) {
                resultMap.put("index", index);
                resultMap.put("id", id);
                return true;
            }
        };
        ComponentUtil.register(client, "searchEngineClient");

        final String id = "001";

        assertTrue(indexingHelper.deleteDocument(client, id));
        assertEquals("fess.update", resultMap.get("index"));
        assertEquals(id, resultMap.get("id"));
    }

    public void test_deleteDocumentByUrl() {
        final Map<String, String> resultMap = new HashMap<>();
        final SearchEngineClient client = new SearchEngineClient() {
            @Override
            public long deleteByQuery(final String index, final QueryBuilder queryBuilder) {
                if (queryBuilder instanceof final TermQueryBuilder termQueryBuilder) {
                    resultMap.put("index", index);
                    resultMap.put("url", termQueryBuilder.value().toString());
                    return 1;
                }
                return 0;
            }
        };
        ComponentUtil.register(client, "searchEngineClient");

        final String url = "http://test.com/";

        assertEquals(1, indexingHelper.deleteDocumentByUrl(client, url));
        assertEquals("fess.update", resultMap.get("index"));
        assertEquals(url, resultMap.get("url"));
    }

    public void test_deleteDocumentsByDocId() {
        final Map<String, Object> resultMap = new HashMap<>();
        final SearchEngineClient client = new SearchEngineClient() {
            @Override
            public long deleteByQuery(final String index, final QueryBuilder queryBuilder) {
                if (queryBuilder instanceof final TermsQueryBuilder termsQueryBuilder) {
                    resultMap.put("index", index);
                    resultMap.put("ids", termsQueryBuilder.values().stream().map(Object::toString).collect(Collectors.toList()));
                    return 1;
                }
                return 0;
            }
        };
        ComponentUtil.register(client, "searchEngineClient");

        final List<String> docList = new ArrayList<>();

        indexingHelper.deleteDocumentsByDocId(client, docList);
        assertEquals("fess.update", resultMap.get("index"));
        assertEquals(0, ((List<String>) resultMap.get("ids")).size());

        docList.add("001");
        indexingHelper.deleteDocumentsByDocId(client, docList);
        assertEquals("fess.update", resultMap.get("index"));
        assertEquals(1, ((List<String>) resultMap.get("ids")).size());
        assertEquals("001", ((List<String>) resultMap.get("ids")).get(0));
    }

    public void test_deleteDocumentByQuery() {
        final Map<String, String> resultMap = new HashMap<>();
        final SearchEngineClient client = new SearchEngineClient() {
            @Override
            public long deleteByQuery(final String index, final QueryBuilder queryBuilder) {
                if (queryBuilder instanceof final TermQueryBuilder termQueryBuilder) {
                    resultMap.put("index", index);
                    resultMap.put("url", termQueryBuilder.value().toString());
                    return 1;
                }
                return 0;
            }
        };
        ComponentUtil.register(client, "searchEngineClient");

        final String url = "http://test.com/";

        assertEquals(1, indexingHelper.deleteDocumentByQuery(client, QueryBuilders.termQuery("fess.update", url)));
        assertEquals("fess.update", resultMap.get("index"));
        assertEquals(url, resultMap.get("url"));
    }

    public void test_getDocument() {
        final Map<String, String> resultMap = new HashMap<>();
        final SearchEngineClient client = new SearchEngineClient() {

            @Override
            public OptionalEntity<Map<String, Object>> getDocument(final String index,
                    final SearchCondition<SearchRequestBuilder> condition) {
                final SearchRequestBuilder builder = new SearchRequestBuilder(this, SearchAction.INSTANCE);
                condition.build(builder);
                resultMap.put("index", index);
                resultMap.put("query", builder.toString());
                return OptionalEntity.of(Map.of("_id", "001", "title", "test1", "content", "test2"));
            }
        };
        ComponentUtil.register(client, "searchEngineClient");

        final Map<String, Object> document = indexingHelper.getDocument(client, "001", new String[] { "title", "content" });
        assertEquals("fess.update", resultMap.get("index"));
        assertEquals(
                "{\"query\":{\"ids\":{\"values\":[\"001\"],\"boost\":1.0}},\"_source\":{\"includes\":[\"title\",\"content\"],\"excludes\":[]}}",
                resultMap.get("query"));
        assertEquals(3, document.size());
    }

    public void test_getDocumentListByPrefixId() {
        documentSizeByQuery = 1L;
        final Map<String, String> resultMap = new HashMap<>();
        final SearchEngineClient client = new SearchEngineClient() {
            @Override
            public List<Map<String, Object>> getDocumentList(final String index, final SearchCondition<SearchRequestBuilder> condition) {
                final SearchRequestBuilder builder = new SearchRequestBuilder(this, SearchAction.INSTANCE);
                condition.build(builder);
                resultMap.put("index", index);
                resultMap.put("query", builder.toString());
                final List<Map<String, Object>> docList = new ArrayList<>();
                docList.add(Map.of("_id", "001", "title", "test1", "content", "test2"));
                return docList;
            }
        };
        ComponentUtil.register(client, "searchEngineClient");

        final List<Map<String, Object>> documents =
                indexingHelper.getDocumentListByPrefixId(client, "001", new String[] { "title", "content" });
        assertEquals("fess.update", resultMap.get("index"));
        assertEquals(
                "{\"size\":1,\"query\":{\"prefix\":{\"_id\":{\"value\":\"001\",\"boost\":1.0}}},\"_source\":{\"includes\":[\"title\",\"content\"],\"excludes\":[]}}",
                resultMap.get("query"));
        assertEquals(1, documents.size());
    }

    public void test_deleteChildDocument() {
        final Map<String, String> resultMap = new HashMap<>();
        final SearchEngineClient client = new SearchEngineClient() {
            @Override
            public long deleteByQuery(final String index, final QueryBuilder queryBuilder) {
                if (queryBuilder instanceof final TermQueryBuilder termQueryBuilder) {
                    resultMap.put("index", index);
                    resultMap.put("id", termQueryBuilder.value().toString());
                    resultMap.put("field", termQueryBuilder.fieldName());
                    return 1;
                }
                return 0;
            }
        };
        ComponentUtil.register(client, "searchEngineClient");

        final String id = "001";

        assertEquals(1, indexingHelper.deleteChildDocument(client, id));
        assertEquals("fess.update", resultMap.get("index"));
        assertEquals(id, resultMap.get("id"));
        assertEquals("parent_id", resultMap.get("field"));
    }

    public void test_getChildDocumentList() {
        documentSizeByQuery = 1L;
        final Map<String, String> resultMap = new HashMap<>();
        final SearchEngineClient client = new SearchEngineClient() {
            @Override
            public List<Map<String, Object>> getDocumentList(final String index, final SearchCondition<SearchRequestBuilder> condition) {
                final SearchRequestBuilder builder = new SearchRequestBuilder(this, SearchAction.INSTANCE);
                condition.build(builder);
                resultMap.put("index", index);
                resultMap.put("query", builder.toString());
                final List<Map<String, Object>> docList = new ArrayList<>();
                docList.add(Map.of("_id", "001", "title", "test1", "content", "test2"));
                return docList;
            }
        };
        ComponentUtil.register(client, "searchEngineClient");

        final List<Map<String, Object>> documents = indexingHelper.getChildDocumentList(client, "001", new String[] { "title", "content" });
        assertEquals("fess.update", resultMap.get("index"));
        assertEquals(
                "{\"size\":1,\"query\":{\"term\":{\"parent_id\":{\"value\":\"001\",\"boost\":1.0}}},\"_source\":{\"includes\":[\"title\",\"content\"],\"excludes\":[]}}",
                resultMap.get("query"));
        assertEquals(1, documents.size());
    }

    public void test_getDocumentListByQuery() {
        documentSizeByQuery = 1L;
        final Map<String, String> resultMap = new HashMap<>();
        final SearchEngineClient client = new SearchEngineClient() {
            @Override
            public List<Map<String, Object>> getDocumentList(final String index, final SearchCondition<SearchRequestBuilder> condition) {
                final SearchRequestBuilder builder = new SearchRequestBuilder(this, SearchAction.INSTANCE);
                condition.build(builder);
                resultMap.put("index", index);
                resultMap.put("query", builder.toString());
                final List<Map<String, Object>> docList = new ArrayList<>();
                docList.add(Map.of("_id", "001", "title", "test1", "content", "test2"));
                return docList;
            }
        };
        ComponentUtil.register(client, "searchEngineClient");

        final List<Map<String, Object>> documents =
                indexingHelper.getDocumentListByQuery(client, QueryBuilders.idsQuery().addIds("001"), new String[] { "title", "content" });
        assertEquals("fess.update", resultMap.get("index"));
        assertEquals(
                "{\"size\":1,\"query\":{\"ids\":{\"values\":[\"001\"],\"boost\":1.0}},\"_source\":{\"includes\":[\"title\",\"content\"],\"excludes\":[]}}",
                resultMap.get("query"));
        assertEquals(1, documents.size());
    }

    public void test_deleteBySessionId() {
        final Map<String, String> resultMap = new HashMap<>();
        final SearchEngineClient client = new SearchEngineClient() {
            @Override
            public long deleteByQuery(final String index, final QueryBuilder queryBuilder) {
                if (queryBuilder instanceof final TermQueryBuilder termQueryBuilder) {
                    resultMap.put("index", index);
                    resultMap.put("field", termQueryBuilder.fieldName());
                    resultMap.put("value", termQueryBuilder.value().toString());
                    return 1;
                }
                return 0;
            }
        };
        ComponentUtil.register(client, "searchEngineClient");

        final String sessionId = "session1";

        assertEquals(1, indexingHelper.deleteBySessionId(sessionId));
        assertEquals("fess.update", resultMap.get("index"));
        assertEquals("segment", resultMap.get("field"));
        assertEquals(sessionId, resultMap.get("value"));
    }

    public void test_deleteByConfigId() {
        final Map<String, String> resultMap = new HashMap<>();
        final SearchEngineClient client = new SearchEngineClient() {
            @Override
            public long deleteByQuery(final String index, final QueryBuilder queryBuilder) {
                if (queryBuilder instanceof final TermQueryBuilder termQueryBuilder) {
                    resultMap.put("index", index);
                    resultMap.put("field", termQueryBuilder.fieldName());
                    resultMap.put("value", termQueryBuilder.value().toString());
                    return 1;
                }
                return 0;
            }
        };
        ComponentUtil.register(client, "searchEngineClient");

        final String configId = "W01";

        assertEquals(1, indexingHelper.deleteByConfigId(configId));
        assertEquals("fess.update", resultMap.get("index"));
        assertEquals("config_id", resultMap.get("field"));
        assertEquals(configId, resultMap.get("value"));
    }

    public void test_deleteByVirtualHost() {
        final Map<String, String> resultMap = new HashMap<>();
        final SearchEngineClient client = new SearchEngineClient() {
            @Override
            public long deleteByQuery(final String index, final QueryBuilder queryBuilder) {
                if (queryBuilder instanceof final TermQueryBuilder termQueryBuilder) {
                    resultMap.put("index", index);
                    resultMap.put("field", termQueryBuilder.fieldName());
                    resultMap.put("value", termQueryBuilder.value().toString());
                    return 1;
                }
                return 0;
            }
        };
        ComponentUtil.register(client, "searchEngineClient");

        final String virtualHost = "aaa";

        assertEquals(1, indexingHelper.deleteByVirtualHost(virtualHost));
        assertEquals("fess.update", resultMap.get("index"));
        assertEquals("virtual_host", resultMap.get("field"));
        assertEquals(virtualHost, resultMap.get("value"));
    }

    public void test_calculateDocumentSize() {
        assertEquals(0, indexingHelper.calculateDocumentSize(Collections.emptyMap()));
        assertEquals(118, indexingHelper.calculateDocumentSize(Map.of("id", "test")));
        assertEquals(249, indexingHelper.calculateDocumentSize(Map.of("id", "test", "url", "http://test.com/")));
    }

    public void test_setMaxRetryCount() {
        indexingHelper.setMaxRetryCount(10);
        assertEquals(10, indexingHelper.maxRetryCount);

        indexingHelper.setMaxRetryCount(0);
        assertEquals(0, indexingHelper.maxRetryCount);
    }

    public void test_setDefaultRowSize() {
        indexingHelper.setDefaultRowSize(50);
        assertEquals(50, indexingHelper.defaultRowSize);

        indexingHelper.setDefaultRowSize(1000);
        assertEquals(1000, indexingHelper.defaultRowSize);
    }

    public void test_setRequestInterval() {
        indexingHelper.setRequestInterval(1000L);
        assertEquals(1000L, indexingHelper.requestInterval);

        indexingHelper.setRequestInterval(0L);
        assertEquals(0L, indexingHelper.requestInterval);
    }

    public void test_deleteBySessionId_withClient() {
        final Map<String, String> resultMap = new HashMap<>();
        final SearchEngineClient client = new SearchEngineClient() {
            @Override
            public long deleteByQuery(final String index, final QueryBuilder queryBuilder) {
                if (queryBuilder instanceof final TermQueryBuilder termQueryBuilder) {
                    resultMap.put("index", index);
                    resultMap.put("field", termQueryBuilder.fieldName());
                    resultMap.put("value", termQueryBuilder.value().toString());
                    return 1;
                }
                return 0;
            }
        };

        final String sessionId = "session1";
        final String index = "test.index";

        assertEquals(1, indexingHelper.deleteBySessionId(client, index, sessionId));
        assertEquals(index, resultMap.get("index"));
        assertEquals("segment", resultMap.get("field"));
        assertEquals(sessionId, resultMap.get("value"));
    }

    public void test_deleteByConfigId_withClient() {
        final Map<String, String> resultMap = new HashMap<>();
        final SearchEngineClient client = new SearchEngineClient() {
            @Override
            public long deleteByQuery(final String index, final QueryBuilder queryBuilder) {
                if (queryBuilder instanceof final TermQueryBuilder termQueryBuilder) {
                    resultMap.put("index", index);
                    resultMap.put("field", termQueryBuilder.fieldName());
                    resultMap.put("value", termQueryBuilder.value().toString());
                    return 1;
                }
                return 0;
            }
        };

        final String configId = "W01";
        final String index = "test.index";

        assertEquals(1, indexingHelper.deleteByConfigId(client, index, configId));
        assertEquals(index, resultMap.get("index"));
        assertEquals("config_id", resultMap.get("field"));
        assertEquals(configId, resultMap.get("value"));
    }

    public void test_deleteByVirtualHost_withClient() {
        final Map<String, String> resultMap = new HashMap<>();
        final SearchEngineClient client = new SearchEngineClient() {
            @Override
            public long deleteByQuery(final String index, final QueryBuilder queryBuilder) {
                if (queryBuilder instanceof final TermQueryBuilder termQueryBuilder) {
                    resultMap.put("index", index);
                    resultMap.put("field", termQueryBuilder.fieldName());
                    resultMap.put("value", termQueryBuilder.value().toString());
                    return 1;
                }
                return 0;
            }
        };

        final String virtualHost = "aaa";
        final String index = "test.index";

        assertEquals(1, indexingHelper.deleteByVirtualHost(client, index, virtualHost));
        assertEquals(index, resultMap.get("index"));
        assertEquals("virtual_host", resultMap.get("field"));
        assertEquals(virtualHost, resultMap.get("value"));
    }

    public void test_getDocument_notFound() {
        final SearchEngineClient client = new SearchEngineClient() {
            @Override
            public OptionalEntity<Map<String, Object>> getDocument(final String index,
                    final SearchCondition<SearchRequestBuilder> condition) {
                return OptionalEntity.empty();
            }
        };
        ComponentUtil.register(client, "searchEngineClient");

        final Map<String, Object> document = indexingHelper.getDocument(client, "999", new String[] { "title" });
        assertNull(document);
    }

    public void test_getDocumentListByQuery_withNullFields() {
        documentSizeByQuery = 1L;
        final Map<String, String> resultMap = new HashMap<>();
        final SearchEngineClient client = new SearchEngineClient() {
            @Override
            public List<Map<String, Object>> getDocumentList(final String index, final SearchCondition<SearchRequestBuilder> condition) {
                final SearchRequestBuilder builder = new SearchRequestBuilder(this, SearchAction.INSTANCE);
                condition.build(builder);
                resultMap.put("index", index);
                resultMap.put("query", builder.toString());
                final List<Map<String, Object>> docList = new ArrayList<>();
                docList.add(Map.of("_id", "001", "title", "test1", "content", "test2"));
                return docList;
            }
        };
        ComponentUtil.register(client, "searchEngineClient");

        final List<Map<String, Object>> documents = indexingHelper.getDocumentListByQuery(client, QueryBuilders.matchAllQuery(), null);
        assertEquals("fess.update", resultMap.get("index"));
        assertEquals("{\"size\":1,\"query\":{\"match_all\":{\"boost\":1.0}}}", resultMap.get("query"));
        assertEquals(1, documents.size());
    }

    public void test_calculateDocumentSize_withNullValue() {
        final Map<String, Object> docMap = new HashMap<>();
        docMap.put("id", "test");
        docMap.put("nullField", null);

        long size = indexingHelper.calculateDocumentSize(docMap);
        assertTrue(size > 0);
    }
}
