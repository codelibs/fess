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
package org.codelibs.fess.app.web.api.admin.backup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.codelibs.core.io.CopyUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.admin.backup.AdminBackupAction;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.BooleanFunction;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.servlet.request.stream.WrittenStreamOut;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.core.common.bytes.BytesArray;
import org.opensearch.core.index.Index;
import org.opensearch.core.index.shard.ShardId;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchShardTarget;

public class ApiAdminBackupActionTest extends UnitFessTestCase {

    // ===================================================================================
    //                                                        Concrete index in the bulk file
    //                                                        ================================

    /**
     * The bulk file must name the index each document actually lives in, not the alias that was
     * requested. A file naming the alias cannot be replayed through _bulk ("no write index is
     * defined for alias"), and it also collapses documents that share an id across the members of
     * the alias: fess_user.user and fess_user.role both hold "YWRtaW4=" (the admin account and the
     * admin role), so an alias-named dump loses one of them on restore.
     */
    @Test
    public void test_get$file_writesTheConcreteIndexNotTheRequestedAlias() throws Exception {
        registerWalkingClient(hit("fess_user.user", "YWRtaW4=", "{\"name\":\"admin\"}"),
                hit("fess_user.role", "YWRtaW4=", "{\"name\":\"admin\"}"));

        final String[] lines = drain(new ApiAdminBackupAction().get$file("fess_user.bulk")).split("\n");

        assertEquals(4, lines.length);
        assertEquals("{\"index\":{\"_index\":\"fess_user.user\",\"_id\":\"YWRtaW4=\"}}", lines[0]);
        assertEquals("{\"index\":{\"_index\":\"fess_user.role\",\"_id\":\"YWRtaW4=\"}}", lines[2]);

        // The pair (index, id) is what a restore keys on, so the two lines must stay distinct.
        // With the alias written instead, both action lines are identical and one document is lost.
        final Set<String> actions = new LinkedHashSet<>();
        actions.add(lines[0]);
        actions.add(lines[2]);
        assertEquals(2, actions.size(), "the two documents must not collapse onto one (index, id) pair: " + actions);
    }

    // ===================================================================================
    //                                                                       Mapping files
    //                                                                       =============

    /**
     * fess.json is an index mapping shipped with the product, not an index that can be walked. The
     * API routed it into the bulk branch, which searched for an index literally called "fess.json"
     * and answered 200 with an empty body while the admin screen served 41,137 bytes.
     */
    @Test
    public void test_get$file_fessJsonIsServedAsTheMappingFile() throws Exception {
        final List<String> walked = registerWalkingClient();

        final StreamResponse response = new ApiAdminBackupAction().get$file("fess.json");

        // The bulk branch appends ".bulk" to an id that does not already carry it.
        assertEquals("fess.json", response.getFileName());
        final String served = served(response);
        assertTrue(walked.isEmpty(), "a mapping file must not be looked up in the search engine: " + walked);
        assertEquals(served(adminDownload("fess.json")), served);
    }

    /**
     * The same holds for doc.json, which the admin screen served as 12,754 bytes while the API
     * answered 200 with an empty body.
     */
    @Test
    public void test_get$file_docJsonIsServedAsTheMappingFile() throws Exception {
        final List<String> walked = registerWalkingClient();

        final StreamResponse response = new ApiAdminBackupAction().get$file("doc.json");

        assertEquals("doc.json", response.getFileName());
        final String served = served(response);
        assertTrue(walked.isEmpty(), "a mapping file must not be looked up in the search engine: " + walked);
        assertEquals(served(adminDownload("doc.json")), served);
    }

    // ===================================================================================
    //                                                                            Helpers
    //                                                                            =======

    /**
     * Builds a hit that reports the concrete index it came from, the way a real walk over an alias
     * does.
     */
    private SearchHit hit(final String index, final String id, final String source) {
        final SearchHit hit = new SearchHit(0, id, null, null);
        hit.shard(new SearchShardTarget("node", new ShardId(new Index(index, "_na_"), 0), null, null));
        hit.sourceRef(new BytesArray(source));
        return hit;
    }

    /**
     * Registers a search engine client whose walk hands out the given hits, and returns the list of
     * indices it was asked to walk.
     */
    private List<String> registerWalkingClient(final SearchHit... hits) {
        final List<String> walked = new ArrayList<>();
        ComponentUtil.register(new SearchEngineClient() {
            @Override
            public <T> long scrollSearch(final String index, final SearchCondition<SearchRequestBuilder> condition,
                    final EntityCreator<T, SearchResponse, SearchHit> creator, final BooleanFunction<T> cursor) {
                walked.add(index);
                long count = 0;
                for (final SearchHit hit : hits) {
                    count++;
                    @SuppressWarnings("unchecked")
                    final T entity = (T) hit;
                    if (!cursor.apply(entity)) {
                        break;
                    }
                }
                return count;
            }
        }, "searchEngineClient");
        return walked;
    }

    /**
     * Runs the admin screen download for the same id, which is the behaviour the API has to match.
     */
    private StreamResponse adminDownload(final String id) throws Exception {
        final AdminBackupAction action = new AdminBackupAction();
        final Field field = FessBaseAction.class.getDeclaredField("fessConfig");
        field.setAccessible(true);
        field.set(action, ComponentUtil.getFessConfig());
        return (StreamResponse) action.download(id);
    }

    /**
     * Returns what the client would actually receive: the streamed text, or the failure that
     * reached it instead. The mapping files are read from the built webapp rather than from the
     * unit test tree, so both paths may legitimately fail to find one here; what must never differ
     * is which of the two happens.
     */
    private String served(final StreamResponse response) {
        try {
            return drain(response);
        } catch (final Exception e) {
            return e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }

    /**
     * Runs the stream callback of the response and returns what it wrote, which is what the client
     * downloads.
     */
    private String drain(final StreamResponse response) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        response.getStreamCall().callback(new WrittenStreamOut() {
            @Override
            public OutputStream stream() {
                return out;
            }

            @Override
            public void write(final InputStream ins) throws IOException {
                CopyUtil.copy(ins, out);
            }
        });
        return new String(out.toByteArray(), Constants.CHARSET_UTF_8);
    }
}
