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
    public void test_insert_otherFailureIsRethrownAsIs() {
        final RecordingTagTypeBhv bhv = new RecordingTagTypeBhv();
        bhv.failure = new IllegalStateException("mapper_parsing_exception");

        final IllegalStateException e = assertThrows(IllegalStateException.class, () -> createService(bhv).insert(createTagType()));
        assertSame(bhv.failure, e);
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
                return 7;
            }
        };

        // no aggregations in the response: no counts
        assertTrue(service.getPathCountMapByOwner("alice").isEmpty());
        assertTrue(dsl[0].contains("\"owner\":{\"value\":\"alice\""), dsl[0]);
        assertTrue(dsl[0].contains("\"terms\":{\"field\":\"name\",\"size\":7"), dsl[0]);
        assertTrue(dsl[0].contains("\"value_count\":{\"field\":\"paths\"}"), dsl[0]);
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
