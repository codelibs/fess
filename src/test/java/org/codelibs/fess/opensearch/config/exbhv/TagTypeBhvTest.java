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
package org.codelibs.fess.opensearch.config.exbhv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codelibs.fesen.opensearch.action.get.GetRequest;
import org.codelibs.fesen.opensearch.action.get.GetResponse;
import org.codelibs.fesen.opensearch.action.support.PlainActionFuture;
import org.codelibs.fesen.opensearch.common.action.ActionFuture;
import org.codelibs.fesen.opensearch.common.xcontent.json.JsonXContent;
import org.codelibs.fesen.opensearch.core.xcontent.DeprecationHandler;
import org.codelibs.fesen.opensearch.core.xcontent.NamedXContentRegistry;
import org.codelibs.fesen.opensearch.core.xcontent.XContentParser;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Covers {@link TagTypeBhv#selectRealtimeByPK(String)}: a tag type is read with the real-time GET API, which finds a
 * document that is written but not yet searchable, and the entity carries what a conditional write needs.
 */
public class TagTypeBhvTest extends UnitFessTestCase {

    private static final String ID = "0123456789abcdef";

    /** The GET requests the stub client received. */
    private final List<GetRequest> requests = new ArrayList<>();

    private String responseJson;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        requests.clear();
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getIndexConfigIndex() {
                return "fess_config";
            }
        });
        ComponentUtil.register(new SearchEngineClient() {
            @Override
            public ActionFuture<GetResponse> get(final GetRequest request) {
                requests.add(request);
                final PlainActionFuture<GetResponse> future = PlainActionFuture.newFuture();
                try (XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY,
                        DeprecationHandler.IGNORE_DEPRECATIONS, responseJson)) {
                    future.onResponse(GetResponse.fromXContent(parser));
                } catch (final IOException e) {
                    future.onFailure(e);
                }
                return future;
            }
        }, "searchEngineClient");
    }

    @Test
    public void test_selectRealtimeByPK_readsTheDocumentWithItsSequenceNumberAndPrimaryTerm() {
        responseJson = "{\"_index\":\"fess_config.tag_type.2026\",\"_id\":\"" + ID + "\",\"_version\":3,\"_seq_no\":7,\"_primary_term\":2,"
                + "\"found\":true,\"_source\":{\"name\":\"foo\",\"owner\":\"alice\",\"paths\":[\"http://a/\",\"http://b/\"],"
                + "\"permissions\":[\"1alice\",\"Rguest\"],\"virtualHost\":\"\",\"sortOrder\":4,\"createdBy\":\"alice\","
                + "\"createdTime\":1000,\"updatedBy\":\"bob\",\"updatedTime\":2000}}";

        final OptionalEntity<TagType> result = new TagTypeBhv().selectRealtimeByPK(ID);

        assertTrue(result.isPresent());
        final TagType tagType = result.get();
        assertEquals(ID, tagType.getId());
        assertEquals(3L, tagType.getVersionNo());
        assertEquals(7L, tagType.getSeqNo());
        assertEquals(2L, tagType.getPrimaryTerm());
        assertEquals("foo", tagType.getName());
        assertEquals("alice", tagType.getOwner());
        assertEquals(List.of("http://a/", "http://b/"), List.of(tagType.getPaths()));
        assertEquals(List.of("1alice", "Rguest"), List.of(tagType.getPermissions()));
        assertEquals(4, tagType.getSortOrder());
        assertEquals("bob", tagType.getUpdatedBy());
        assertEquals(2000L, tagType.getUpdatedTime());
    }

    @Test
    public void test_selectRealtimeByPK_asksForTheDocumentOfTheConfigIndexByGet() {
        responseJson = "{\"_index\":\"fess_config.tag_type.2026\",\"_id\":\"" + ID + "\",\"found\":false}";

        new TagTypeBhv().selectRealtimeByPK(ID);

        assertEquals(1, requests.size());
        final GetRequest request = requests.get(0);
        assertEquals("fess_config.tag_type", request.index());
        assertEquals(ID, request.id());
        // a GET reads a document that is not refreshed yet from the translog only when it is real-time
        assertTrue(request.realtime());
        assertFalse(request.refresh());
    }

    @Test
    public void test_selectRealtimeByPK_missingDocumentIsEmpty() {
        responseJson = "{\"_index\":\"fess_config.tag_type.2026\",\"_id\":\"" + ID + "\",\"found\":false}";

        assertFalse(new TagTypeBhv().selectRealtimeByPK(ID).isPresent());
    }
}
