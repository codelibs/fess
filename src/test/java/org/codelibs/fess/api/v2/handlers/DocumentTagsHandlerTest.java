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
package org.codelibs.fess.api.v2.handlers;

import static org.codelibs.fess.api.v2.handlers.TagHandlerTestSupport.docTag;
import static org.codelibs.fess.api.v2.handlers.TagHandlerTestSupport.list;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.api.v2.handlers.TagHandlerTestSupport.Env;
import org.codelibs.fess.api.v2.handlers.TagHandlerTestSupport.Response;
import org.codelibs.fess.api.v2.handlers.TagHandlerTestSupport.StubRequest;
import org.codelibs.fess.app.service.TagTypeService;
import org.codelibs.fess.entity.TagChange;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Unit tests for {@link DocumentTagsHandler}: {@code GET/POST /api/v2/documents/{docId}/tags} and
 * {@code DELETE /api/v2/documents/{docId}/tags/{id}}.
 */
public class DocumentTagsHandlerTest extends UnitFessTestCase {

    private static final String DOC_ID = "doc1";

    private static final String URL = "http://example.com/a b%20#x";

    private Env env;

    /** The document the caller can see, or null when it cannot. */
    private Map<String, Object> doc;

    private final List<String> documentLookups = new ArrayList<>();

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        env = new Env();
        doc = new HashMap<>(Map.of("url", URL));
        documentLookups.clear();
    }

    private class StubHandler extends DocumentTagsHandler {
        @Override
        protected OptionalThing<FessUserBean> getUserBean() {
            return env.user;
        }

        @Override
        protected TagTypeService getTagTypeService() {
            return env.service;
        }

        @Override
        protected void pause(final long millis) {
            env.pauses.add(millis);
        }

        @Override
        protected Map<String, Object> getDocument(final String docId, final String[] fields) {
            documentLookups.add(docId);
            return doc;
        }
    }

    private Response call(final StubRequest req, final String tagId) throws IOException {
        final Response res = new Response();
        new StubHandler().handle(req.proxy(), res.proxy(), DOC_ID, tagId);
        return res;
    }

    private static StubRequest request(final String method) {
        return new StubRequest(method);
    }

    // ===================================================================================
    //                                                                      Request gates
    //                                                                      =============

    @Test
    public void test_disabled_returnsInvalidRequestForEveryMethod() throws Exception {
        env.fessConfig.enabled = false;
        env.user("alice");
        final TagType tag = env.put("foo", "alice", false, URL);
        final String[][] requests = { { "GET", null }, { "POST", null }, { "DELETE", tag.getId() } };
        for (final String[] r : requests) {
            final Response res = call(request(r[0]).json("{\"name\":\"foo\"}"), r[1]);
            Assertions.assertEquals(400, res.status, r[0]);
            Assertions.assertEquals("tag feature is not available", res.errorMessage(), r[0]);
        }
        Assertions.assertEquals(List.of(), documentLookups);
        Assertions.assertEquals(List.of(), env.helper.changes);
    }

    @Test
    public void test_anonymous_returnsAuthRequired() throws Exception {
        final TagType tag = env.put("foo", "alice", true, URL);
        final String[][] requests = { { "GET", null }, { "POST", null }, { "DELETE", tag.getId() } };
        for (final String[] r : requests) {
            final Response res = call(request(r[0]).json("{\"name\":\"foo\"}"), r[1]);
            Assertions.assertEquals(401, res.status, r[0]);
            Assertions.assertEquals("auth_required", res.errorCode(), r[0]);
        }
        Assertions.assertEquals(List.of(), documentLookups);
    }

    @Test
    public void test_accessTokenWithoutLogin_isRefused() throws Exception {
        final Response res = call(request("POST").header("Authorization", "Bearer token").json("{\"name\":\"foo\"}"), null);
        Assertions.assertEquals(401, res.status);
        Assertions.assertEquals("auth_required", res.errorCode());
        Assertions.assertEquals(List.of(), env.service.calls);
    }

    @Test
    public void test_unsupportedMethods_return405() throws Exception {
        env.user("alice");
        Response res = call(request("DELETE"), null);
        Assertions.assertEquals(405, res.status);
        Assertions.assertEquals("GET, POST", res.headers.get("Allow"));
        res = call(request("GET"), "abc");
        Assertions.assertEquals(405, res.status);
        Assertions.assertEquals("DELETE", res.headers.get("Allow"));
    }

    @Test
    public void test_invalidDocId_returnsInvalidRequest() throws Exception {
        env.user("alice");
        final Response res = new Response();
        new StubHandler().handle(request("GET").proxy(), res.proxy(), "a b", null);
        Assertions.assertEquals(400, res.status);
        Assertions.assertEquals("invalid doc_id", res.errorMessage());
    }

    @Test
    public void test_invisibleDoc_returnsNotFound() throws Exception {
        env.user("alice");
        doc = null;
        final TagType tag = env.put("foo", "alice", false);
        final String[][] requests = { { "GET", null }, { "POST", null }, { "DELETE", tag.getId() } };
        for (final String[] r : requests) {
            final Response res = call(request(r[0]).json("{\"name\":\"foo\"}"), r[1]);
            Assertions.assertEquals(404, res.status, r[0]);
            Assertions.assertEquals("doc not found: " + DOC_ID, res.errorMessage(), r[0]);
        }
        Assertions.assertEquals(List.of(), env.helper.changes);
        Assertions.assertEquals(0, env.stored("foo", "alice").getPaths() == null ? 0 : env.stored("foo", "alice").getPaths().length);
    }

    // ===================================================================================
    //                                                                                GET
    //                                                                                ===

    @Test
    public void test_get_listsVisibleTagsAndOwnAddableTags() throws Exception {
        env.user("alice");
        final TagType own = env.put("mine", "alice", false, URL);
        final TagType othersShared = env.put("theirs", "bob", true, URL, "http://other/");
        env.put("hidden", "bob", false, URL);
        final TagType addable = env.put("spare", "alice", true, "http://other/");
        env.put("unrelated", "bob", true, "http://other/");
        final Response res = call(request("GET"), null);
        Assertions.assertEquals(200, res.status, res.body());
        final Map<String, Object> payload = res.payload();
        Assertions.assertEquals(DOC_ID, payload.get("doc_id"));
        Assertions.assertEquals(List.of(docTag(own, true, false), docTag(othersShared, false, true)), payload.get("tags"));
        Assertions.assertEquals(List.of(docTag(addable, true, true)), payload.get("addable"));
        Assertions.assertEquals(List.of(DOC_ID), documentLookups);
    }

    // ===================================================================================
    //                                                                               POST
    //                                                                               ====

    @Test
    public void test_postByName_autoCreatesPrivateTagAndEnqueuesAdd() throws Exception {
        env.user("alice");
        final Response res = call(request("POST").json("{\"name\":\"new tag\"}"), null);
        Assertions.assertEquals(200, res.status, res.body());
        final TagType created = env.stored("new tag", "alice");
        Assertions.assertNotNull(created);
        Assertions.assertEquals(list(URL), list(created.getPaths()));
        Assertions.assertEquals(list("1alice"), list(created.getPermissions()));
        final Map<String, Object> payload = res.payload();
        Assertions.assertEquals(Boolean.TRUE, payload.get("added"));
        Assertions.assertEquals(docTag(created, true, false), payload.get("tag"));
        Assertions.assertEquals(List.of(docTag(created, true, false)), payload.get("tags"));
        Assertions.assertEquals(List.of(TagChange.add(created.getTagValue(), URL)), env.helper.changes);
    }

    @Test
    public void test_postByName_existingTagGetsTheUrl() throws Exception {
        env.user("alice");
        final TagType tag = env.put("foo", "alice", true, "http://other/");
        final Response res = call(request("POST").json("{\"name\":\"foo\"}"), null);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(list("http://other/", URL), list(env.service.store.get(tag.getId()).getPaths()));
        Assertions.assertEquals(List.of(TagChange.add(tag.getTagValue(), URL)), env.helper.changes);
        // adding it again changes nothing
        final Response again = call(request("POST").json("{\"id\":\"" + tag.getId() + "\"}"), null);
        Assertions.assertEquals(200, again.status, again.body());
        Assertions.assertEquals(Boolean.FALSE, again.payload().get("added"));
        Assertions.assertEquals(list("http://other/", URL), list(env.service.store.get(tag.getId()).getPaths()));
        Assertions.assertEquals(1, env.helper.changes.size());
    }

    @Test
    public void test_post_bodyThatIsNotAnObject_returnsInvalidRequest() throws Exception {
        env.user("alice");
        for (final String json : new String[] { "null", "[]", "\"text\"", "12", "true" }) {
            final Response res = call(request("POST").json(json), null);
            Assertions.assertEquals(400, res.status, json);
            Assertions.assertEquals("invalid_request", res.errorCode(), json);
            Assertions.assertEquals("request body must be a JSON object", res.errorMessage(), json);
        }
        Assertions.assertEquals(List.of(), env.helper.changes);
    }

    @Test
    public void test_postByName_autoCreateRespectsMaxTags() throws Exception {
        env.user("alice");
        env.fessConfig.maxTags = 1;
        env.put("a", "alice", false);
        final Response res = call(request("POST").json("{\"name\":\"b\"}"), null);
        Assertions.assertEquals(400, res.status);
        Assertions.assertEquals("too many tags: a user can have up to 1 tags", res.errorMessage());
        Assertions.assertEquals(List.of(), env.helper.changes);
    }

    @Test
    public void test_parallelAutoCreations_stayWithinMaxTags() throws Exception {
        env.user("alice");
        env.fessConfig.maxTags = 5;
        final TagHandlerTestSupport.SlowCountTagTypeService service = new TagHandlerTestSupport.SlowCountTagTypeService();
        final Response[] responses = TagHandlerTestSupport.runTogether(20, i -> {
            final Response res = new Response();
            try {
                new StubHandler() {
                    @Override
                    protected TagTypeService getTagTypeService() {
                        return service;
                    }
                }.handle(request("POST").json("{\"name\":\"tag" + i + "\"}").proxy(), res.proxy(), DOC_ID, null);
            } catch (final IOException e) {
                throw new java.io.UncheckedIOException(e);
            }
            return res;
        });
        int created = 0;
        for (final Response res : responses) {
            if (res.status == 200) {
                created++;
            } else {
                Assertions.assertEquals(400, res.status, res.body());
                Assertions.assertEquals("too many tags: a user can have up to 5 tags", res.errorMessage());
            }
        }
        Assertions.assertEquals(5, created);
        Assertions.assertEquals(5, service.store.size());
    }

    @Test
    public void test_post_maxPathsReached_returnsInvalidRequest() throws Exception {
        env.user("alice");
        env.fessConfig.maxPaths = 2;
        final TagType tag = env.put("foo", "alice", false, "http://a/", "http://b/");
        final Response res = call(request("POST").json("{\"id\":\"" + tag.getId() + "\"}"), null);
        Assertions.assertEquals(400, res.status, res.body());
        Assertions.assertEquals("invalid_request", res.errorCode());
        Assertions.assertEquals("too many documents: a tag can be put on up to 2 documents", res.errorMessage());
        Assertions.assertEquals(list("http://a/", "http://b/"), list(env.service.store.get(tag.getId()).getPaths()));
        Assertions.assertEquals(List.of(), env.helper.changes);
    }

    @Test
    public void test_post_retriesAConcurrentUpdate() throws Exception {
        env.user("alice");
        final TagType tag = env.put("foo", "alice", false, "http://a/");
        env.service.updateConflicts = 1;
        final Response res = call(request("POST").json("{\"id\":\"" + tag.getId() + "\"}"), null);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(list("http://a/", URL), list(env.service.store.get(tag.getId()).getPaths()));
    }

    @Test
    public void test_post_survivesSeveralLostRaces() throws Exception {
        // Seven lost races in a row, as when many requests write the same tag at once, are still not an error.
        env.user("alice");
        final TagType tag = env.put("foo", "alice", false, "http://a/");
        env.service.updateConflicts = 7;
        final Response res = call(request("POST").json("{\"id\":\"" + tag.getId() + "\"}"), null);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(Boolean.TRUE, res.payload().get("added"));
        Assertions.assertEquals(7, env.pauses.size());
        Assertions.assertEquals(list("http://a/", URL), list(env.service.store.get(tag.getId()).getPaths()));
        Assertions.assertEquals(List.of(TagChange.add(tag.getTagValue(), URL)), env.helper.changes);
    }

    @Test
    public void test_post_keepsLosingTheRace_backsOffAndReturnsConflict() throws Exception {
        env.user("alice");
        final TagType tag = env.put("foo", "alice", false, "http://a/");
        env.service.updateConflicts = 100;
        final Response res = call(request("POST").json("{\"id\":\"" + tag.getId() + "\"}"), null);
        Assertions.assertEquals(409, res.status, res.body());
        Assertions.assertEquals("conflict", res.errorCode());
        Assertions.assertEquals("the tag was changed concurrently; try again", res.errorMessage());
        // every attempt wrote once, and a wait that doubles up to a cap separates the attempts
        Assertions.assertEquals(AbstractTagHandler.MAX_UPDATE_ATTEMPTS,
                env.service.calls.stream().filter(c -> c.startsWith("update")).count());
        Assertions.assertEquals(AbstractTagHandler.MAX_UPDATE_ATTEMPTS - 1, env.pauses.size());
        long cap = AbstractTagHandler.RETRY_WAIT_MILLIS;
        for (final long pause : env.pauses) {
            Assertions.assertTrue(pause >= cap / 2 && pause <= cap, "wait " + pause + " is not in [" + cap / 2 + ", " + cap + "]");
            cap = Math.min(AbstractTagHandler.RETRY_WAIT_MAX_MILLIS, cap * 2);
        }
        Assertions.assertEquals(list("http://a/"), list(env.service.store.get(tag.getId()).getPaths()));
        Assertions.assertEquals(List.of(), env.helper.changes);
    }

    @Test
    public void test_post_newName_lostCreateAddsToTheTagTheWinnerCreated() throws Exception {
        // Another request created the tag after this one looked for it, so the insert fails: the tag of the winner
        // is read again and the URL is added to it, not answered with "tag not found".
        env.user("alice");
        final TagType tag = env.put("foo", "alice", false, "http://a/");
        env.service.hiddenReads = 1;
        final Response res = call(request("POST").json("{\"name\":\"foo\"}"), null);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(Boolean.TRUE, res.payload().get("added"));
        Assertions.assertEquals(List.of("insert foo/alice", "update foo/alice"), env.service.calls);
        Assertions.assertEquals(list("http://a/", URL), list(env.service.store.get(tag.getId()).getPaths()));
        Assertions.assertEquals(List.of(TagChange.add(tag.getTagValue(), URL)), env.helper.changes);
    }

    @Test
    public void test_post_doesNotReportAnAddAnotherRequestMade() throws Exception {
        // The request loses the race to a request that put the same tag on the same URL: its retry finds the URL
        // on the tag and changes nothing, so it adds nothing and queues nothing.
        env.user("alice");
        final TagType tag = env.put("foo", "alice", false, "http://a/");
        env.service.updateConflicts = 1;
        env.service.onUpdateConflict = stored -> stored.setPaths(new String[] { "http://a/", URL });
        final Response res = call(request("POST").json("{\"id\":\"" + tag.getId() + "\"}"), null);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(Boolean.FALSE, res.payload().get("added"));
        Assertions.assertEquals(list("http://a/", URL), list(env.service.store.get(tag.getId()).getPaths()));
        Assertions.assertEquals(List.of(), env.helper.changes);
    }

    @Test
    public void test_postOthersTag_returnsForbiddenOrNotFound() throws Exception {
        env.user("alice");
        final TagType shared = env.put("foo", "bob", true);
        final TagType hidden = env.put("bar", "bob", false);
        Response res = call(request("POST").json("{\"id\":\"" + shared.getId() + "\"}"), null);
        Assertions.assertEquals(403, res.status, res.body());
        res = call(request("POST").json("{\"id\":\"" + hidden.getId() + "\"}"), null);
        Assertions.assertEquals(404, res.status, res.body());
        Assertions.assertEquals(List.of(), env.helper.changes);
    }

    @Test
    public void test_post_withoutNameOrId_returnsInvalidRequest() throws Exception {
        env.user("alice");
        final Response res = call(request("POST").json("{}"), null);
        Assertions.assertEquals(400, res.status);
        Assertions.assertEquals("name or id is required", res.errorMessage());
    }

    // ===================================================================================
    //                                                                             DELETE
    //                                                                             ======

    @Test
    public void test_delete_removesTheUrlAndEnqueuesRemove() throws Exception {
        env.user("alice");
        final TagType tag = env.put("foo", "alice", false, "http://a/", URL);
        final Response res = call(request("DELETE"), tag.getId());
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(Boolean.TRUE, res.payload().get("removed"));
        Assertions.assertEquals(List.of(), res.payload().get("tags"));
        Assertions.assertEquals(list("http://a/"), list(env.service.store.get(tag.getId()).getPaths()));
        Assertions.assertEquals(List.of(TagChange.remove(tag.getTagValue(), URL)), env.helper.changes);

        final Response again = call(request("DELETE"), tag.getId());
        Assertions.assertEquals(200, again.status, again.body());
        Assertions.assertEquals(Boolean.FALSE, again.payload().get("removed"));
        Assertions.assertEquals(1, env.helper.changes.size());
    }

    @Test
    public void test_delete_doesNotReportARemovalAnotherRequestMade() throws Exception {
        env.user("alice");
        final TagType tag = env.put("foo", "alice", false, "http://a/", URL);
        env.service.updateConflicts = 1;
        env.service.onUpdateConflict = stored -> stored.setPaths(new String[] { "http://a/" });
        final Response res = call(request("DELETE"), tag.getId());
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(Boolean.FALSE, res.payload().get("removed"));
        Assertions.assertEquals(list("http://a/"), list(env.service.store.get(tag.getId()).getPaths()));
        Assertions.assertEquals(List.of(), env.helper.changes);
    }

    @Test
    public void test_deleteOthersTag_returnsForbidden() throws Exception {
        env.user("alice");
        final TagType shared = env.put("foo", "bob", true, URL);
        final Response res = call(request("DELETE"), shared.getId());
        Assertions.assertEquals(403, res.status, res.body());
        Assertions.assertEquals(list(URL), list(env.service.store.get(shared.getId()).getPaths()));
        Assertions.assertEquals(List.of(), env.helper.changes);
    }

    @Test
    public void test_searchEngineOutOfReach_returns503() throws Exception {
        env.user("alice");
        final Response res = new Response();
        new StubHandler() {
            @Override
            protected Map<String, Object> getDocument(final String docId, final String[] fields) {
                throw new org.codelibs.curl.CurlException("Failed to access", new java.net.ConnectException("Connection refused"));
            }
        }.handle(request("POST").json("{\"name\":\"foo\"}").proxy(), res.proxy(), DOC_ID, null);
        Assertions.assertEquals(503, res.status, res.body());
        Assertions.assertEquals("service_unavailable", res.errorCode());
        Assertions.assertEquals("5", res.headers.get("Retry-After"));
    }
}
