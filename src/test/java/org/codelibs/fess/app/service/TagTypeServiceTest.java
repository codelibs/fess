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
package org.codelibs.fess.app.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.codelibs.fesen.opensearch.action.DocWriteRequest.OpType;
import org.codelibs.fesen.opensearch.action.index.IndexAction;
import org.codelibs.fesen.opensearch.action.index.IndexRequest;
import org.codelibs.fesen.opensearch.action.index.IndexRequestBuilder;
import org.codelibs.fesen.opensearch.action.support.WriteRequest.RefreshPolicy;
import org.codelibs.fess.exception.TagTypeConflictException;
import org.codelibs.fess.opensearch.config.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.opensearch.config.exbhv.TagTypeBhv;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * Covers the write paths of {@link TagTypeService}: a tag type is created only when its id is
 * free, updated only when nobody changed it since it was read, and a lost race on either is
 * reported as a {@link TagTypeConflictException}.
 */
public class TagTypeServiceTest extends UnitFessTestCase {

    private TagType createTagType() {
        final TagType tagType = new TagType();
        tagType.setId("0123456789abcdef");
        tagType.setName("foo");
        tagType.setOwner("alice");
        tagType.setPaths(new String[] { "http://example.com/" });
        tagType.setPermissions(new String[] { "1alice" });
        return tagType;
    }

    private static IndexRequest apply(final RequestOptionCall<IndexRequestBuilder> opLambda) {
        final IndexRequestBuilder builder = new IndexRequestBuilder(null, IndexAction.INSTANCE);
        if (opLambda != null) {
            opLambda.callback(builder);
        }
        return builder.request();
    }

    private static final class RecordingTagTypeBhv extends TagTypeBhv {
        IndexRequest insertRequest;
        IndexRequest updateRequest;
        RuntimeException failure;

        @Override
        public void insert(final TagType entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
            insertRequest = apply(opLambda);
            if (failure != null) {
                throw failure;
            }
        }

        org.codelibs.fesen.opensearch.action.delete.DeleteRequest deleteRequest;

        @Override
        public void delete(final TagType entity,
                final RequestOptionCall<org.codelibs.fesen.opensearch.action.delete.DeleteRequestBuilder> opLambda) {
            final org.codelibs.fesen.opensearch.action.delete.DeleteRequestBuilder builder =
                    new org.codelibs.fesen.opensearch.action.delete.DeleteRequestBuilder(null,
                            org.codelibs.fesen.opensearch.action.delete.DeleteAction.INSTANCE, null);
            if (opLambda != null) {
                opLambda.callback(builder);
            }
            deleteRequest = builder.request();
            if (failure != null) {
                throw failure;
            }
        }

        @Override
        public void update(final TagType entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
            updateRequest = apply(opLambda);
            if (failure != null) {
                throw failure;
            }
        }
    }

    private static TagTypeService createService(final TagTypeBhv bhv) {
        final TagTypeService service = new TagTypeService();
        service.tagTypeBhv = bhv;
        return service;
    }

    @Test
    public void test_getTagType_readsTheTagTypeByGetNotBySearch() {
        // A search finds a tag type only once the index is refreshed, which a request that just lost a race for it
        // cannot wait for; selectByPK stands for the search here and finds nothing yet.
        final TagType stored = createTagType();
        final TagTypeBhv bhv = new TagTypeBhv() {
            @Override
            public org.dbflute.optional.OptionalEntity<TagType> selectByPK(final String id) {
                return org.dbflute.optional.OptionalEntity.empty();
            }

            @Override
            public org.dbflute.optional.OptionalEntity<TagType> selectRealtimeByPK(final String id) {
                return stored.getId().equals(id) ? org.dbflute.optional.OptionalEntity.of(stored)
                        : org.dbflute.optional.OptionalEntity.empty();
            }
        };

        assertSame(stored, createService(bhv).getTagType(stored.getId()).get());
        assertFalse(createService(bhv).getTagType("other").isPresent());
    }

    @Test
    public void test_insert_usesCreateOpType() {
        final RecordingTagTypeBhv bhv = new RecordingTagTypeBhv();

        createService(bhv).insert(createTagType());

        assertNotNull(bhv.insertRequest);
        assertEquals(OpType.CREATE, bhv.insertRequest.opType());
        assertEquals(RefreshPolicy.IMMEDIATE, bhv.insertRequest.getRefreshPolicy());
    }

    @Test
    public void test_update_passesSeqNoAndPrimaryTerm() {
        final RecordingTagTypeBhv bhv = new RecordingTagTypeBhv();
        final TagType tagType = createTagType();
        tagType.setSeqNo(5L);
        tagType.setPrimaryTerm(2L);

        createService(bhv).update(tagType);

        assertNotNull(bhv.updateRequest);
        assertEquals(5L, bhv.updateRequest.ifSeqNo());
        assertEquals(2L, bhv.updateRequest.ifPrimaryTerm());
        assertEquals(OpType.INDEX, bhv.updateRequest.opType());
        assertEquals(RefreshPolicy.IMMEDIATE, bhv.updateRequest.getRefreshPolicy());
    }

    @Test
    public void test_update_withoutSeqNoIsRefused() {
        // An update without the sequence number would overwrite a concurrent change unseen.
        final RecordingTagTypeBhv bhv = new RecordingTagTypeBhv();

        assertThrows(IllegalArgumentException.class, () -> createService(bhv).update(createTagType()));
        assertNull(bhv.updateRequest);
    }

    @Test
    public void test_insert_conflictThrowsTagTypeConflictException() {
        final RecordingTagTypeBhv bhv = new RecordingTagTypeBhv();
        bhv.failure = new IllegalStateException("insert failed",
                new VersionConflictEngineException("[0123456789abcdef]: version conflict, document already exists"));

        final TagTypeConflictException e = assertThrows(TagTypeConflictException.class, () -> createService(bhv).insert(createTagType()));
        assertSame(bhv.failure, e.getCause());
    }

    @Test
    public void test_update_conflictThrowsTagTypeConflictException() {
        final RecordingTagTypeBhv bhv = new RecordingTagTypeBhv();
        bhv.failure = new VersionConflictEngineException("[0123456789abcdef]: version conflict, required seqNo [5]");
        final TagType tagType = createTagType();
        tagType.setSeqNo(5L);
        tagType.setPrimaryTerm(2L);

        assertThrows(TagTypeConflictException.class, () -> createService(bhv).update(tagType));
    }

    @Test
    public void test_update_conflictOverHttpThrowsTagTypeConflictException() {
        // Over the HTTP client the conflict arrives as a generic exception carrying the error type.
        final RecordingTagTypeBhv bhv = new RecordingTagTypeBhv();
        bhv.failure = new IllegalStateException(
                "OpenSearch exception [type=version_conflict_engine_exception, reason=[0123456789abcdef]: version conflict]");
        final TagType tagType = createTagType();
        tagType.setSeqNo(5L);
        tagType.setPrimaryTerm(2L);

        assertThrows(TagTypeConflictException.class, () -> createService(bhv).update(tagType));
    }

    @Test
    public void test_delete_passesSeqNoAndPrimaryTerm() {
        final RecordingTagTypeBhv bhv = new RecordingTagTypeBhv();
        final TagType tagType = createTagType();
        tagType.setSeqNo(5L);
        tagType.setPrimaryTerm(2L);

        createService(bhv).delete(tagType);

        assertEquals(5L, bhv.deleteRequest.ifSeqNo());
        assertEquals(2L, bhv.deleteRequest.ifPrimaryTerm());
        assertEquals(RefreshPolicy.IMMEDIATE, bhv.deleteRequest.getRefreshPolicy());
    }

    @Test
    public void test_delete_withoutSeqNoIsRefused() {
        // A delete without the sequence number would drop a concurrent change unseen.
        final RecordingTagTypeBhv bhv = new RecordingTagTypeBhv();

        assertThrows(IllegalArgumentException.class, () -> createService(bhv).delete(createTagType()));
        assertNull(bhv.deleteRequest);
    }

    @Test
    public void test_delete_conflictThrowsTagTypeConflictException() {
        final RecordingTagTypeBhv bhv = new RecordingTagTypeBhv();
        bhv.failure = new VersionConflictEngineException("[0123456789abcdef]: version conflict, required seqNo [5]");
        final TagType tagType = createTagType();
        tagType.setSeqNo(5L);
        tagType.setPrimaryTerm(2L);

        assertThrows(TagTypeConflictException.class, () -> createService(bhv).delete(tagType));
    }

    @Test
    public void test_insert_otherFailureIsRethrownAsIs() {
        final RecordingTagTypeBhv bhv = new RecordingTagTypeBhv();
        bhv.failure = new IllegalStateException("mapper_parsing_exception");

        final IllegalStateException e = assertThrows(IllegalStateException.class, () -> createService(bhv).insert(createTagType()));
        assertSame(bhv.failure, e);
    }

    /** Records the writes of {@link TagTypeService#replace}; the delete of the given id fails as configured. */
    private static final class ReplaceTagTypeService extends TagTypeService {
        final java.util.List<String> calls = new java.util.ArrayList<>();
        final java.util.Map<String, TagType> store = new java.util.HashMap<>();
        RuntimeException insertFailure;
        RuntimeException deleteFailure;
        String deleteFailureId;

        @Override
        public void insert(final TagType tagType) {
            calls.add("insert " + tagType.getId());
            if (insertFailure != null) {
                throw insertFailure;
            }
            store.put(tagType.getId(), tagType);
        }

        @Override
        public void delete(final TagType tagType) {
            calls.add("delete " + tagType.getId());
            if (deleteFailure != null && tagType.getId().equals(deleteFailureId)) {
                throw deleteFailure;
            }
            store.remove(tagType.getId());
        }

        @Override
        public org.dbflute.optional.OptionalEntity<TagType> getTagType(final String id) {
            final TagType tagType = store.get(id);
            return tagType == null ? org.dbflute.optional.OptionalEntity.empty() : org.dbflute.optional.OptionalEntity.of(tagType);
        }
    }

    private static TagType tagTypeWithId(final String id) {
        final TagType tagType = createTagTypeStatic();
        tagType.setId(id);
        tagType.setSeqNo(1L);
        tagType.setPrimaryTerm(1L);
        return tagType;
    }

    private static TagType createTagTypeStatic() {
        final TagType tagType = new TagType();
        tagType.setName("foo");
        tagType.setOwner("alice");
        return tagType;
    }

    @Test
    public void test_replace_insertsThenDeletesTheCurrentTagType() {
        final ReplaceTagTypeService service = new ReplaceTagTypeService();
        final TagType current = tagTypeWithId("old");
        service.store.put("old", current);

        assertTrue(service.replace(current, tagTypeWithId("new")));

        assertEquals(java.util.List.of("insert new", "delete old"), service.calls);
        assertEquals(java.util.Set.of("new"), service.store.keySet());
    }

    @Test
    public void test_replace_existingReplacementThrowsAndKeepsTheCurrentTagType() {
        final ReplaceTagTypeService service = new ReplaceTagTypeService();
        final TagType current = tagTypeWithId("old");
        service.store.put("old", current);
        service.insertFailure = new TagTypeConflictException("exists", null);

        assertThrows(TagTypeConflictException.class, () -> service.replace(current, tagTypeWithId("new")));

        assertEquals(java.util.List.of("insert new"), service.calls);
        assertEquals(java.util.Set.of("old"), service.store.keySet());
    }

    @Test
    public void test_replace_changedCurrentRemovesTheReplacementAndReturnsFalse() {
        final ReplaceTagTypeService service = new ReplaceTagTypeService();
        final TagType current = tagTypeWithId("old");
        service.store.put("old", current);
        service.deleteFailure = new TagTypeConflictException("changed", null);
        service.deleteFailureId = "old";

        assertFalse(service.replace(current, tagTypeWithId("new")));

        assertEquals(java.util.List.of("insert new", "delete old", "delete new"), service.calls);
        assertEquals(java.util.Set.of("old"), service.store.keySet());
    }

    @Test
    public void test_replace_otherDeleteFailureRemovesTheReplacementAndRethrows() {
        final ReplaceTagTypeService service = new ReplaceTagTypeService();
        final TagType current = tagTypeWithId("old");
        service.store.put("old", current);
        service.deleteFailure = new IllegalStateException("down");
        service.deleteFailureId = "old";

        final IllegalStateException e = assertThrows(IllegalStateException.class, () -> service.replace(current, tagTypeWithId("new")));

        assertSame(service.deleteFailure, e);
        assertEquals(java.util.Set.of("old"), service.store.keySet());
    }

    @Test
    public void test_getTagValue_encodesNameAndOwner() {
        final TagType tagType = new TagType();
        tagType.setName("a:b");
        tagType.setOwner("user/1");

        // Base64URL without padding, UTF-8, joined by ':'
        assertEquals("YTpi:dXNlci8x", tagType.getTagValue());

        tagType.setName("日本語");
        assertEquals("5pel5pys6Kqe:dXNlci8x", tagType.getTagValue());

        tagType.setOwner(null);
        assertNull(tagType.getTagValue());
    }

    @Test
    public void test_getPathCountMapByOwner_aggregatesWithoutReadingPaths() {
        final String[] dsl = new String[1];
        final TagTypeBhv bhv = new TagTypeBhv() {
            @Override
            public org.dbflute.cbean.result.PagingResultBean<TagType> selectPage(
                    final org.dbflute.bhv.readable.CBCall<org.codelibs.fess.opensearch.config.cbean.TagTypeCB> cbLambda) {
                final org.codelibs.fess.opensearch.config.cbean.TagTypeCB cb = new org.codelibs.fess.opensearch.config.cbean.TagTypeCB();
                cbLambda.callback(cb);
                final org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder builder =
                        new org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder(null,
                                org.codelibs.fesen.opensearch.action.search.SearchAction.INSTANCE);
                cb.build(builder);
                dsl[0] = builder.toString().replaceAll("\\s+", "");
                return new org.codelibs.fess.opensearch.config.allcommon.EsPagingResultBean<>(builder);
            }
        };
        final TagTypeService service = createService(bhv);
        service.fessConfig = new org.codelibs.fess.mylasta.direction.FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer getUserTagMaxTagsAsInteger() {
                return 3;
            }

            @Override
            public Integer getIndexerMaxResultWindowSizeAsInteger() {
                return 7;
            }
        };

        // no aggregations in the response: no counts. The buckets are not cut at user.tag.max.tags (3).
        assertTrue(service.getPathCountMapByOwner("alice").isEmpty());
        assertTrue(dsl[0].contains("\"owner\":{\"value\":\"alice\""), dsl[0]);
        assertTrue(dsl[0].contains("\"terms\":{\"field\":\"name\",\"size\":7"), dsl[0]);
        assertTrue(dsl[0].contains("\"value_count\":{\"field\":\"paths\"}"), dsl[0]);
    }

    @Test
    public void test_getTagTypeListByOwner_isNotCutAtTheMaxTags() {
        // An owner can have more tags than user.tag.max.tags (the limit was lowered, an administrator created
        // tags, or two nodes raced), and a tag cut from the list can neither be seen nor deleted by its owner.
        final org.codelibs.fess.opensearch.config.cbean.TagTypeCB[] condition = new org.codelibs.fess.opensearch.config.cbean.TagTypeCB[1];
        final TagTypeBhv bhv = new TagTypeBhv() {
            @Override
            public org.dbflute.cbean.result.ListResultBean<TagType> selectList(
                    final org.dbflute.bhv.readable.CBCall<org.codelibs.fess.opensearch.config.cbean.TagTypeCB> cbLambda) {
                condition[0] = new org.codelibs.fess.opensearch.config.cbean.TagTypeCB();
                cbLambda.callback(condition[0]);
                return new org.dbflute.cbean.result.ListResultBean<>();
            }
        };
        final TagTypeService service = createService(bhv);
        service.fessConfig = new org.codelibs.fess.mylasta.direction.FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer getUserTagMaxTagsAsInteger() {
                return 3;
            }

            @Override
            public Integer getIndexerMaxResultWindowSizeAsInteger() {
                return 7;
            }
        };

        assertTrue(service.getTagTypeListByOwner("alice").isEmpty());
        assertEquals(7, condition[0].getFetchSize());
        assertEquals(1, condition[0].getFetchPageNumber());
    }

    /**
     * Stand-in for {@code org.codelibs.fesen.opensearch.index.engine.VersionConflictEngineException},
     * recognized by its class name.
     */
    private static final class VersionConflictEngineException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        VersionConflictEngineException(final String message) {
            super(message);
        }
    }
}
