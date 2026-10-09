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

import static org.codelibs.fess.api.v2.handlers.TagHandlerTestSupport.list;

import java.io.IOException;
import java.util.LinkedHashMap;
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
 * Unit tests for {@link TagsHandler}: {@code GET/POST /api/v2/tags} and {@code PUT/DELETE /api/v2/tags/{id}}.
 */
public class TagsHandlerTest extends UnitFessTestCase {

    private Env env;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        env = new Env();
    }

    private class StubHandler extends TagsHandler {
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
    }

    private Response call(final StubRequest req, final String id) throws IOException {
        final Response res = new Response();
        new StubHandler().handle(req.proxy(), res.proxy(), id);
        return res;
    }

    private static StubRequest request(final String method) {
        return new StubRequest(method);
    }

    private static Map<String, Object> listItem(final TagType tagType, final boolean shared, final long pathCount) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", tagType.getId());
        map.put("value", tagType.getTagValue());
        map.put("name", tagType.getName());
        map.put("shared", shared);
        map.put("sort_order", 0);
        map.put("path_count", (int) pathCount);
        return map;
    }

    // ===================================================================================
    //                                                                      Request gates
    //                                                                      =============

    @Test
    public void test_parallelCreations_stayWithinMaxTags() throws Exception {
        // Requests that arrive together all counted fewer tags than the limit and all stored theirs, which left the
        // owner with more tags than the limit.
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
                }.handle(request("POST").json("{\"name\":\"tag" + i + "\"}").proxy(), res.proxy(), null);
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
    public void test_disabled_returnsInvalidRequestForEveryMethod() throws Exception {
        env.fessConfig.enabled = false;
        env.user("alice");
        final TagType tag = env.put("foo", "alice", false);
        final String[][] requests = { { "GET", null }, { "POST", null }, { "PUT", tag.getId() }, { "DELETE", tag.getId() } };
        for (final String[] r : requests) {
            final Response res = call(request(r[0]).json("{\"name\":\"bar\"}"), r[1]);
            Assertions.assertEquals(400, res.status, r[0]);
            Assertions.assertEquals("invalid_request", res.errorCode(), r[0]);
            Assertions.assertEquals("tag feature is not available", res.errorMessage(), r[0]);
        }
        Assertions.assertEquals(List.of(), env.service.calls);
        Assertions.assertEquals(List.of(), env.helper.changes);
    }

    @Test
    public void test_anonymous_returnsAuthRequired() throws Exception {
        final TagType tag = env.put("foo", "alice", true);
        final String[][] requests = { { "GET", null }, { "POST", null }, { "PUT", tag.getId() }, { "DELETE", tag.getId() } };
        for (final String[] r : requests) {
            final Response res = call(request(r[0]).json("{\"name\":\"bar\"}"), r[1]);
            Assertions.assertEquals(401, res.status, r[0]);
            Assertions.assertEquals("auth_required", res.errorCode(), r[0]);
            Assertions.assertEquals("login required", res.errorMessage(), r[0]);
        }
        Assertions.assertEquals(List.of(), env.service.calls);
    }

    @Test
    public void test_accessTokenWithoutLogin_isRefused() throws Exception {
        // An access token gives a request the token's roles, never a user; the tags need a user.
        final Response res = call(request("POST").header("Authorization", "Bearer token").json("{\"name\":\"bar\"}"), null);
        Assertions.assertEquals(401, res.status);
        Assertions.assertEquals("auth_required", res.errorCode());
        Assertions.assertEquals(List.of(), env.service.calls);
    }

    @Test
    public void test_unsupportedMethods_return405() throws Exception {
        env.user("alice");
        for (final String method : new String[] { "PUT", "DELETE", "PATCH" }) {
            final Response res = call(request(method), null);
            Assertions.assertEquals(405, res.status, method);
            Assertions.assertEquals("GET, POST", res.headers.get("Allow"), method);
        }
        for (final String method : new String[] { "GET", "POST", "PATCH" }) {
            final Response res = call(request(method), "0123");
            Assertions.assertEquals(405, res.status, method);
            Assertions.assertEquals("PUT, DELETE", res.headers.get("Allow"), method);
        }
    }

    // ===================================================================================
    //                                                                        List/Create
    //                                                                        ===========

    @Test
    public void test_get_listsOwnTagsWithPathCounts() throws Exception {
        env.user("alice");
        final TagType foo = env.put("foo", "alice", false, "http://a/", "http://b/");
        final TagType bar = env.put("bar", "alice", true);
        env.put("baz", "bob", true, "http://a/");
        final Response res = call(request("GET"), null);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(List.of(listItem(bar, true, 0), listItem(foo, false, 2)), res.payload().get("tags"));
    }

    @Test
    public void test_post_createsPrivateOrSharedTag() throws Exception {
        env.user("alice");
        final Response res = call(request("POST").json("{\"name\":\"  ＦＯＯ  \"}"), null);
        Assertions.assertEquals(200, res.status, res.body());
        final TagType stored = env.stored("FOO", "alice");
        Assertions.assertNotNull(stored);
        Assertions.assertEquals(env.helper.toId(stored.getTagValue()), stored.getId());
        Assertions.assertEquals(list("1alice"), list(stored.getPermissions()));
        Assertions.assertEquals(0, stored.getSortOrder());
        // the entity reads an empty virtual host as null
        Assertions.assertNull(stored.getVirtualHost());
        Assertions.assertEquals("alice", stored.getCreatedBy());
        Assertions.assertNotNull(stored.getCreatedTime());
        Assertions.assertEquals(listItem(stored, false, 0), res.payload().get("tag"));

        final Response shared = call(request("POST").json("{\"name\":\"bar\",\"shared\":true}"), null);
        Assertions.assertEquals(200, shared.status, shared.body());
        Assertions.assertEquals(list("1alice", "Rguest"), list(env.stored("bar", "alice").getPermissions()));
        // creating a tag puts it on no document
        Assertions.assertEquals(List.of(), env.helper.changes);
    }

    @Test
    public void test_post_takesTheVirtualHostOfTheRequest() throws Exception {
        env.user("alice");
        env.virtualHostKey = "site1";
        Assertions.assertEquals(200, call(request("POST").json("{\"name\":\"foo\"}"), null).status);
        Assertions.assertEquals("site1", env.stored("foo", "alice").getVirtualHost());
    }

    @Test
    public void test_post_invalidName_returnsInvalidRequest() throws Exception {
        env.user("alice");
        for (final String body : new String[] { "{}", "{\"name\":\"   \"}", "{\"name\":1}", "{\"name\":\"" + "a".repeat(51) + "\"}" }) {
            final Response res = call(request("POST").json(body), null);
            Assertions.assertEquals(400, res.status, body);
            Assertions.assertEquals("invalid tag name: enter 1 to 50 characters", res.errorMessage(), body);
        }
        final Response res = call(request("POST").json("{\"name\":\"foo\",\"shared\":\"yes\"}"), null);
        Assertions.assertEquals(400, res.status);
        Assertions.assertEquals("shared must be a boolean", res.errorMessage());
        Assertions.assertEquals(List.of(), env.service.calls);
    }

    @Test
    public void test_post_duplicate_returnsConflict() throws Exception {
        env.user("alice");
        env.put("foo", "alice", false, "http://a/");
        final Response res = call(request("POST").json("{\"name\":\"foo\"}"), null);
        Assertions.assertEquals(409, res.status, res.body());
        Assertions.assertEquals("conflict", res.errorCode());
        Assertions.assertEquals("a tag with the name already exists", res.errorMessage());
        // the existing tag is untouched
        Assertions.assertEquals(list("http://a/"), list(env.stored("foo", "alice").getPaths()));
    }

    @Test
    public void test_post_sameNameOfAnotherOwner_isAnotherTag() throws Exception {
        env.user("alice");
        env.put("foo", "bob", true);
        Assertions.assertEquals(200, call(request("POST").json("{\"name\":\"foo\"}"), null).status);
        Assertions.assertNotNull(env.stored("foo", "alice"));
    }

    @Test
    public void test_post_maxTagsReached_returnsInvalidRequest() throws Exception {
        env.user("alice");
        env.fessConfig.maxTags = 2;
        env.put("a", "alice", false);
        env.put("b", "alice", false);
        env.put("c", "bob", false);
        final Response res = call(request("POST").json("{\"name\":\"d\"}"), null);
        Assertions.assertEquals(400, res.status, res.body());
        Assertions.assertEquals("invalid_request", res.errorCode());
        Assertions.assertEquals("too many tags: a user can have up to 2 tags", res.errorMessage());
        Assertions.assertNull(env.stored("d", "alice"));
    }

    // ===================================================================================
    //                                                                       Update/Delete
    //                                                                       =============

    @Test
    public void test_put_rename_enqueuesRenameAndReturnsNewId() throws Exception {
        env.user("alice");
        final TagType old = env.put("foo", "alice", true, "http://a/", "http://b/");
        final Response res = call(request("PUT").json("{\"name\":\"bar\"}"), old.getId());
        Assertions.assertEquals(200, res.status, res.body());
        final TagType renamed = env.stored("bar", "alice");
        Assertions.assertNotNull(renamed);
        Assertions.assertNull(env.service.store.get(old.getId()));
        Assertions.assertEquals(list("http://a/", "http://b/"), list(renamed.getPaths()));
        Assertions.assertEquals(list("1alice", "Rguest"), list(renamed.getPermissions()));
        Assertions.assertEquals(listItem(renamed, true, 2), res.payload().get("tag"));
        Assertions.assertEquals(Boolean.TRUE, res.payload().get("renamed"));
        Assertions.assertNotEquals(old.getId(), renamed.getId());
        Assertions.assertEquals(List.of(TagChange.rename(old.getTagValue(), renamed.getTagValue())), env.helper.changes);
    }

    @Test
    public void test_put_rename_retriesWhenTheOldTagChangedConcurrently() throws Exception {
        env.user("alice");
        final TagType old = env.put("foo", "alice", false, "http://a/");
        env.service.deleteConflicts = 1;
        // a concurrent attach to the old tag between the read and the delete
        env.service.onDeleteConflict = stored -> stored.setPaths(new String[] { "http://a/", "http://b/" });
        final Response res = call(request("PUT").json("{\"name\":\"bar\"}"), old.getId());
        Assertions.assertEquals(200, res.status, res.body());
        final TagType renamed = env.stored("bar", "alice");
        Assertions.assertEquals(list("http://a/", "http://b/"), list(renamed.getPaths()));
        Assertions.assertNull(env.service.store.get(old.getId()));
        Assertions.assertEquals(List.of(TagChange.rename(old.getTagValue(), renamed.getTagValue())), env.helper.changes);
    }

    @Test
    public void test_put_rename_keepsLosingTheRace_returnsConflictAndRollsBack() throws Exception {
        env.user("alice");
        final TagType old = env.put("foo", "alice", false, "http://a/");
        env.service.deleteConflicts = 100;
        env.service.deleteConflictOn = t -> "foo".equals(t.getName());
        final Response res = call(request("PUT").json("{\"name\":\"bar\"}"), old.getId());
        Assertions.assertEquals(409, res.status, res.body());
        Assertions.assertEquals("the tag was changed concurrently; try again", res.errorMessage());
        Assertions.assertNull(env.stored("bar", "alice"), "the new tag is rolled back");
        Assertions.assertNotNull(env.service.store.get(old.getId()));
        Assertions.assertEquals(List.of(), env.helper.changes);
    }

    @Test
    public void test_put_rename_deleteFailure_rollsBackTheNewTag() throws Exception {
        env.user("alice");
        final TagType old = env.put("foo", "alice", false, "http://a/");
        env.service.deleteFailure = t -> "foo".equals(t.getName());
        final Response res = call(request("PUT").json("{\"name\":\"bar\"}"), old.getId());
        Assertions.assertEquals(500, res.status, res.body());
        Assertions.assertNull(env.stored("bar", "alice"), "the new tag is rolled back");
        Assertions.assertNotNull(env.service.store.get(old.getId()));
        Assertions.assertEquals(List.of(), env.helper.changes);
    }

    @Test
    public void test_delete_retriesWhenTheTagChangedConcurrently() throws Exception {
        env.user("alice");
        final TagType tag = env.put("foo", "alice", false, "http://a/");
        env.service.deleteConflicts = 1;
        final Response res = call(request("DELETE"), tag.getId());
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertNull(env.service.store.get(tag.getId()));
        Assertions.assertEquals(List.of(TagChange.delete(tag.getTagValue())), env.helper.changes);
    }

    @Test
    public void test_delete_keepsLosingTheRace_returnsConflict() throws Exception {
        env.user("alice");
        final TagType tag = env.put("foo", "alice", false, "http://a/");
        env.service.deleteConflicts = 100;
        final Response res = call(request("DELETE"), tag.getId());
        Assertions.assertEquals(409, res.status, res.body());
        Assertions.assertEquals("conflict", res.errorCode());
        Assertions.assertNotNull(env.service.store.get(tag.getId()));
        Assertions.assertEquals(List.of(), env.helper.changes);
    }

    @Test
    public void test_put_renameToExistingName_returnsConflictAndKeepsTheOldTag() throws Exception {
        env.user("alice");
        final TagType old = env.put("foo", "alice", false, "http://a/");
        env.put("bar", "alice", false);
        final Response res = call(request("PUT").json("{\"name\":\"bar\"}"), old.getId());
        Assertions.assertEquals(409, res.status, res.body());
        Assertions.assertEquals("a tag with the name already exists", res.errorMessage());
        Assertions.assertNotNull(env.service.store.get(old.getId()));
        Assertions.assertEquals(List.of(), env.helper.changes);
    }

    @Test
    public void test_put_shareToggle_changesPermissionsOnly() throws Exception {
        env.user("alice");
        final TagType tag = env.put("foo", "alice", false, "http://a/");
        Response res = call(request("PUT").json("{\"shared\":true}"), tag.getId());
        Assertions.assertEquals(200, res.status, res.body());
        TagType stored = env.service.store.get(tag.getId());
        Assertions.assertEquals(list("1alice", "Rguest"), list(stored.getPermissions()));
        Assertions.assertEquals(list("http://a/"), list(stored.getPaths()));
        Assertions.assertEquals(Boolean.FALSE, res.payload().get("renamed"));
        Assertions.assertEquals(listItem(stored, true, 1), res.payload().get("tag"));

        res = call(request("PUT").json("{\"name\":\"foo\",\"shared\":false}"), tag.getId());
        Assertions.assertEquals(200, res.status, res.body());
        stored = env.service.store.get(tag.getId());
        Assertions.assertEquals(list("1alice"), list(stored.getPermissions()));
        Assertions.assertEquals(list("http://a/"), list(stored.getPaths()));
        // sharing changes who sees the tag, not which documents carry it
        Assertions.assertEquals(List.of(), env.helper.changes);
    }

    @Test
    public void test_put_shareToggle_retriesAConcurrentUpdate() throws Exception {
        env.user("alice");
        final TagType tag = env.put("foo", "alice", false, "http://a/");
        env.service.updateConflicts = 2;
        final Response res = call(request("PUT").json("{\"shared\":true}"), tag.getId());
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(list("1alice", "Rguest"), list(env.service.store.get(tag.getId()).getPermissions()));

        env.service.updateConflicts = 10;
        final Response failed = call(request("PUT").json("{\"shared\":false}"), tag.getId());
        Assertions.assertEquals(409, failed.status, failed.body());
        Assertions.assertEquals("conflict", failed.errorCode());
    }

    @Test
    public void test_put_sameNameOnly_changesNothing() throws Exception {
        env.user("alice");
        final TagType tag = env.put("foo", "alice", true, "http://a/");
        final Response res = call(request("PUT").json("{\"name\":\"foo\"}"), tag.getId());
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(Boolean.FALSE, res.payload().get("renamed"));
        Assertions.assertEquals(list("1alice", "Rguest"), list(env.service.store.get(tag.getId()).getPermissions()));
        Assertions.assertEquals(List.of(), env.service.calls);
    }

    @Test
    public void test_put_shareToggle_keepsOtherPermissions() throws Exception {
        // an administrator may have given the tag to a group as well
        env.user("alice");
        final TagType tag = env.put("foo", "alice", false, "http://a/");
        env.service.store.get(tag.getId()).setPermissions(new String[] { "1alice", "2staff" });
        Assertions.assertEquals(200, call(request("PUT").json("{\"shared\":true}"), tag.getId()).status);
        Assertions.assertEquals(list("1alice", "2staff", "Rguest"), list(env.service.store.get(tag.getId()).getPermissions()));
        Assertions.assertEquals(200, call(request("PUT").json("{\"shared\":false}"), tag.getId()).status);
        Assertions.assertEquals(list("1alice", "2staff"), list(env.service.store.get(tag.getId()).getPermissions()));
    }

    @Test
    public void test_put_withoutChanges_returnsInvalidRequest() throws Exception {
        env.user("alice");
        final TagType tag = env.put("foo", "alice", false);
        final Response res = call(request("PUT").json("{}"), tag.getId());
        Assertions.assertEquals(400, res.status);
        Assertions.assertEquals("name or shared is required", res.errorMessage());
    }

    @Test
    public void test_delete_removesTheTagAndEnqueuesDelete() throws Exception {
        env.user("alice");
        final TagType tag = env.put("foo", "alice", false, "http://a/");
        final Response res = call(request("DELETE"), tag.getId());
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(tag.getId(), res.payload().get("id"));
        Assertions.assertEquals(Boolean.TRUE, res.payload().get("deleted"));
        Assertions.assertNull(env.service.store.get(tag.getId()));
        Assertions.assertEquals(List.of(TagChange.delete(tag.getTagValue())), env.helper.changes);
    }

    @Test
    public void test_deleteOthersTag_returnsForbidden() throws Exception {
        env.user("alice");
        final TagType shared = env.put("foo", "bob", true, "http://a/");
        for (final String method : new String[] { "DELETE", "PUT" }) {
            final Response res = call(request(method).json("{\"name\":\"x\"}"), shared.getId());
            Assertions.assertEquals(403, res.status, method + " " + res.body());
            Assertions.assertEquals("forbidden", res.errorCode());
        }
        Assertions.assertNotNull(env.service.store.get(shared.getId()));
        Assertions.assertEquals(List.of(), env.helper.changes);
    }

    @Test
    public void test_invisibleOrUnknownTag_returnsNotFound() throws Exception {
        env.user("alice");
        final TagType hidden = env.put("foo", "bob", false);
        for (final String id : new String[] { hidden.getId(), "0".repeat(64), "not-an-id" }) {
            final Response res = call(request("DELETE"), id);
            Assertions.assertEquals(404, res.status, id);
            Assertions.assertEquals("not_found", res.errorCode());
            Assertions.assertEquals("tag not found", res.errorMessage());
        }
        Assertions.assertNotNull(env.service.store.get(hidden.getId()));
    }
}
