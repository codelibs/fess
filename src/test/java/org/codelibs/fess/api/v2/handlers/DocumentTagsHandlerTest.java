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

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.TagHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.json.JsonMapper;

/**
 * Unit tests for {@link DocumentTagsHandler}.
 *
 * <p>The tag log and the document index are replaced by {@link FakeTagHelper}, which keeps the tags in memory and
 * records every write, while the name rules ({@code normalizeName}, {@code toValue}, {@code isVisible},
 * {@code toTagItems}) stay the real ones. The handler's own seams supply the caller, the visible tag types and the
 * document.</p>
 */
public class DocumentTagsHandlerTest extends UnitFessTestCase {

    private static final String DOC_ID = "doc1";

    private static final String URL = "http://example.com/page.html";

    private static final String PATH = "/api/v2/documents/" + DOC_ID + "/tags";

    private FakeTagHelper tagHelper;

    private TagFessConfig fessConfig;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        fessConfig = new TagFessConfig();
        ComponentUtil.setFessConfig(fessConfig);
        tagHelper = new FakeTagHelper();
        ComponentUtil.register(tagHelper, "tagHelper");
    }

    // ===================================================================================
    //                                                                        Method gate
    //                                                                        ===========

    @Test
    public void test_unsupportedMethods_return405WithAllowHeader() throws Exception {
        for (final String method : new String[] { "PUT", "PATCH", "HEAD", "OPTIONS" }) {
            final StubHandler handler = new StubHandler().user("alice");
            final Response res = new Response();
            handler.handle(request(method), res.proxy(), DOC_ID);
            Assertions.assertEquals(405, res.status, method);
            Assertions.assertEquals("method_not_allowed", errorCode(res), method);
            Assertions.assertEquals("GET, POST, DELETE", res.headers.get("Allow"), method);
            Assertions.assertEquals(0, handler.documentLookups, method);
        }
        assertTrue(tagHelper.calls.isEmpty(), tagHelper.calls.toString());
    }

    @Test
    public void test_methodIsCaseInsensitive() throws Exception {
        tagHelper.countMap.put("dept:a", 1L);
        final Response res = new Response();
        new StubHandler().handle(request("get"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(1, tags(res).size(), res.body());
    }

    // ===================================================================================
    //                                                                    Request checks
    //                                                                    ==============

    @Test
    public void test_invalidDocId_returns400() throws Exception {
        for (final String docId : new String[] { "", " ", "a b", "../x", "a/b" }) {
            final StubHandler handler = new StubHandler().user("alice");
            final Response res = new Response();
            handler.handle(request("GET"), res.proxy(), docId);
            Assertions.assertEquals(400, res.status, "docId=" + docId);
            Assertions.assertEquals("invalid_request", errorCode(res));
            Assertions.assertEquals("invalid doc_id", errorMessage(res));
            Assertions.assertEquals(0, handler.documentLookups);
        }
        assertTrue(tagHelper.calls.isEmpty(), tagHelper.calls.toString());
    }

    @Test
    public void test_featureDisabled_returns400ForEveryMethod() throws Exception {
        tagHelper.enabled = false;
        for (final String method : new String[] { "GET", "POST", "DELETE" }) {
            final StubHandler handler = new StubHandler().user("alice");
            final Response res = new Response();
            handler.handle(request(method).json("{\"type\":\"dept\",\"name\":\"x\"}").param("value", "dept:x"), res.proxy(), DOC_ID);
            Assertions.assertEquals(400, res.status, method);
            Assertions.assertEquals("invalid_request", errorCode(res), method);
            Assertions.assertEquals("tag feature is not available", errorMessage(res), method);
            Assertions.assertEquals(0, handler.documentLookups, method);
        }
        assertTrue(tagHelper.calls.isEmpty(), tagHelper.calls.toString());
    }

    @Test
    public void test_anonymousWrites_return401() throws Exception {
        for (final String user : new String[] { null, "", " " }) {
            for (final String method : new String[] { "POST", "DELETE" }) {
                final StubHandler handler = new StubHandler().user(user);
                final Response res = new Response();
                handler.handle(request(method).json("{\"type\":\"dept\",\"name\":\"x\"}").param("value", "dept:x"), res.proxy(), DOC_ID);
                Assertions.assertEquals(401, res.status, method + " user=" + user);
                Assertions.assertEquals("auth_required", errorCode(res));
                Assertions.assertEquals("login required", errorMessage(res));
                Assertions.assertEquals(0, handler.documentLookups);
            }
        }
        assertTrue(tagHelper.calls.isEmpty(), tagHelper.calls.toString());
    }

    @Test
    public void test_anonymousGet_listsTagsWithoutWriteFlags() throws Exception {
        tagHelper.countMap.put("dept:a", 2L);
        tagHelper.userTags.put("alice", new LinkedHashSet<>(List.of("dept:a")));
        final Response res = new Response();
        new StubHandler().handle(request("GET"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        final Map<String, Object> payload = payload(res);
        Assertions.assertEquals(DOC_ID, payload.get("doc_id"));
        Assertions.assertEquals(Boolean.FALSE, payload.get("addable"));
        assertFalse(payload.containsKey("added"));
        assertFalse(payload.containsKey("removed"));
        final List<Map<String, Object>> tags = tags(res);
        Assertions.assertEquals(1, tags.size());
        Assertions.assertEquals(tag("dept:a", "dept", "a", 2, false, false), tags.get(0));
        // No user, so nobody's own tags are looked up.
        assertFalse(tagHelper.calls.stream().anyMatch(c -> c.startsWith("getUserTagSet")), tagHelper.calls.toString());
    }

    @Test
    public void test_documentNotVisible_returns404() throws Exception {
        for (final String method : new String[] { "GET", "POST", "DELETE" }) {
            final StubHandler handler = new StubHandler().user("alice");
            handler.doc = null;
            final Response res = new Response();
            handler.handle(request(method).json("{\"type\":\"dept\",\"name\":\"x\"}").param("value", "dept:x"), res.proxy(), DOC_ID);
            Assertions.assertEquals(404, res.status, method);
            Assertions.assertEquals("not_found", errorCode(res));
            Assertions.assertEquals("doc not found: " + DOC_ID, errorMessage(res));
            // The document is fetched through the caller's roles, asking only for its URL.
            Assertions.assertEquals(DOC_ID, handler.lastDocId);
            Assertions.assertEquals(List.of("url"), List.of(handler.lastFields));
            assertTrue(handler.lastUser.isPresent());
        }
        assertFalse(tagHelper.calls.stream().anyMatch(c -> c.startsWith("addTag") || c.startsWith("removeTag")),
                tagHelper.calls.toString());
    }

    @Test
    public void test_documentWithoutUrl_returns404() throws Exception {
        final StubHandler handler = new StubHandler().user("alice");
        handler.doc = new HashMap<>(Map.of("title", "no url"));
        final Response res = new Response();
        handler.handle(request("GET"), res.proxy(), DOC_ID);
        Assertions.assertEquals(404, res.status, res.body());
        Assertions.assertEquals("not_found", errorCode(res));
    }

    @Test
    public void test_unexpectedFailure_returnsFixedInternalError() throws Exception {
        tagHelper.countFailure = new IllegalStateException("secret backend detail");
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("GET"), res.proxy(), DOC_ID);
        Assertions.assertEquals(500, res.status, res.body());
        Assertions.assertEquals("internal_error", errorCode(res));
        assertFalse(res.body().contains("secret backend detail"), res.body());
    }

    // ===================================================================================
    //                                                                              POST
    //                                                                              ====

    @Test
    public void test_post_invalidType_returns400() throws Exception {
        for (final String body : new String[] { "{\"type\":\"secret\",\"name\":\"x\"}", "{\"name\":\"x\"}", "{\"type\":42,\"name\":\"x\"}",
                "{\"type\":null,\"name\":\"x\"}", "{\"type\":\"\",\"name\":\"x\"}", "{}", "" }) {
            final Response res = new Response();
            new StubHandler().user("alice").handle(request("POST").json(body), res.proxy(), DOC_ID);
            Assertions.assertEquals(400, res.status, body + " -> " + res.body());
            Assertions.assertEquals("invalid_request", errorCode(res), body);
            Assertions.assertEquals("invalid tag type", errorMessage(res), body);
        }
        assertNoWrites();
    }

    @Test
    public void test_post_typeNotVisibleToTheCaller_returns400() throws Exception {
        // The type exists for someone else, but this caller's visible set is empty.
        final StubHandler handler = new StubHandler().user("alice");
        handler.typeSet = Collections.emptySet();
        final Response res = new Response();
        handler.handle(request("POST").json("{\"type\":\"dept\",\"name\":\"x\"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(400, res.status, res.body());
        Assertions.assertEquals("invalid tag type", errorMessage(res));
        assertNoWrites();
    }

    @Test
    public void test_post_invalidName_returns400() throws Exception {
        fessConfig.nameMaxLength = 10;
        final String[] names = { "\"\"", "\"   \"", "\"\\t\\n\"", "\"abcdefghijk\"", "\"a\\\"b\"", "\"a\\\\b\"", "\"a\\u0001b\"",
                "\"a\\u007fb\"", "\"＂quoted＂\"", "42", "null", "[\"x\"]" };
        for (final String name : names) {
            final Response res = new Response();
            new StubHandler().user("alice").handle(request("POST").json("{\"type\":\"dept\",\"name\":" + name + "}"), res.proxy(), DOC_ID);
            Assertions.assertEquals(400, res.status, name + " -> " + res.body());
            Assertions.assertEquals("invalid_request", errorCode(res), name);
            Assertions.assertEquals("invalid tag name: enter 1 to 10 characters without \" or \\", errorMessage(res), name);
        }
        // A missing name is rejected the same way.
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("POST").json("{\"type\":\"dept\"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(400, res.status, res.body());
        assertTrue(errorMessage(res).startsWith("invalid tag name"), res.body());
        assertNoWrites();
    }

    @Test
    public void test_post_nameAtTheMaximumLength_isAccepted() throws Exception {
        fessConfig.nameMaxLength = 10;
        // Ten full-width letters are ten characters after NFKC; the length is counted after normalization.
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("POST").json("{\"type\":\"dept\",\"name\":\"ＡＢＣＤＥＦＧＨＩＪ\"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals("addTag(alice," + URL + "," + DOC_ID + ",dept:ABCDEFGHIJ)", tagHelper.writes().get(0));
    }

    @Test
    public void test_post_normalizesNameAndAddsTag() throws Exception {
        tagHelper.countMap.put("project:p", 4L);
        final Response res = new Response();
        new StubHandler().user("alice")
                .handle(request("POST").json("{\"type\":\"dept\",\"name\":\"  ＡＢＣ \\t def  \"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(List.of("addTag(alice," + URL + "," + DOC_ID + ",dept:ABC def)", "updateDocuments(" + URL + ")"),
                tagHelper.writes());
        final Map<String, Object> payload = payload(res);
        Assertions.assertEquals(DOC_ID, payload.get("doc_id"));
        Assertions.assertEquals(Boolean.TRUE, payload.get("added"));
        Assertions.assertEquals(Boolean.TRUE, payload.get("addable"));
        final List<Map<String, Object>> tags = tags(res);
        Assertions.assertEquals(2, tags.size(), res.body());
        assertTrue(tags.contains(tag("project:p", "project", "p", 4, false, false)), res.body());
        assertTrue(tags.contains(tag("dept:ABC def", "dept", "ABC def", 1, true, true)), res.body());
    }

    @Test
    public void test_post_sameTagOfAnotherUser_isCountedOnce() throws Exception {
        tagHelper.countMap.put("dept:x", 3L);
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("POST").json("{\"type\":\"dept\",\"name\":\"x\"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(List.of(tag("dept:x", "dept", "x", 4, true, true)), tags(res));
    }

    @Test
    public void test_post_duplicate_returnsAddedFalseWithoutWriting() throws Exception {
        tagHelper.countMap.put("dept:x", 1L);
        tagHelper.userTags.put("alice", new LinkedHashSet<>(List.of("dept:x")));
        final Response res = new Response();
        // "ｘ" normalizes to the "x" the user already added.
        new StubHandler().user("alice").handle(request("POST").json("{\"type\":\"dept\",\"name\":\" ｘ \"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(Boolean.FALSE, payload(res).get("added"));
        assertNoWrites();
        Assertions.assertEquals(List.of(tag("dept:x", "dept", "x", 1, true, true)), tags(res));
    }

    @Test
    public void test_post_duplicateIsAcceptedEvenAtTheLimits() throws Exception {
        // Re-adding a tag the user already has is a no-op, not a limit violation.
        fessConfig.maxPerDocument = 1;
        fessConfig.maxDocumentTags = 1;
        tagHelper.countMap.put("dept:x", 1L);
        tagHelper.userTags.put("alice", new LinkedHashSet<>(List.of("dept:x")));
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("POST").json("{\"type\":\"dept\",\"name\":\"x\"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(Boolean.FALSE, payload(res).get("added"));
    }

    @Test
    public void test_post_perUserLimit_returns400() throws Exception {
        fessConfig.maxPerDocument = 2;
        tagHelper.countMap.put("dept:a", 1L);
        tagHelper.countMap.put("dept:b", 1L);
        tagHelper.userTags.put("alice", new LinkedHashSet<>(List.of("dept:a", "dept:b")));
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("POST").json("{\"type\":\"dept\",\"name\":\"c\"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(400, res.status, res.body());
        Assertions.assertEquals("invalid_request", errorCode(res));
        Assertions.assertEquals("too many tags: one user can add up to 2 tags to a document", errorMessage(res));
        assertNoWrites();

        // Another user with fewer tags is not affected.
        final Response bob = new Response();
        new StubHandler().user("bob").handle(request("POST").json("{\"type\":\"dept\",\"name\":\"c\"}"), bob.proxy(), DOC_ID);
        Assertions.assertEquals(200, bob.status, bob.body());
    }

    @Test
    public void test_perDocumentLimit_returns400ForANewTag() throws Exception {
        fessConfig.maxDocumentTags = 2;
        tagHelper.countMap.put("dept:a", 1L);
        tagHelper.countMap.put("project:b", 1L);
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("POST").json("{\"type\":\"dept\",\"name\":\"c\"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(400, res.status, res.body());
        Assertions.assertEquals("invalid_request", errorCode(res));
        Assertions.assertEquals("too many tags: a document can have up to 2 tags", errorMessage(res));
        assertNoWrites();
    }

    @Test
    public void test_perDocumentLimit_allowsAnExistingTag() throws Exception {
        // The limit counts distinct tags, so adding one another user already added does not grow the set.
        fessConfig.maxDocumentTags = 2;
        tagHelper.countMap.put("dept:a", 1L);
        tagHelper.countMap.put("project:b", 1L);
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("POST").json("{\"type\":\"dept\",\"name\":\"a\"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(Boolean.TRUE, payload(res).get("added"));
        assertTrue(tags(res).contains(tag("dept:a", "dept", "a", 2, true, true)), res.body());
    }

    @Test
    public void test_post_addTagReportsExisting_skipsDocumentUpdate() throws Exception {
        // addTag answers false when a concurrent request recorded the same tag first.
        tagHelper.addResult = false;
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("POST").json("{\"type\":\"dept\",\"name\":\"x\"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(Boolean.FALSE, payload(res).get("added"));
        Assertions.assertEquals(List.of("addTag(alice," + URL + "," + DOC_ID + ",dept:x)"), tagHelper.writes());
    }

    @Test
    public void test_post_documentUpdateFailure_isSwallowed() throws Exception {
        tagHelper.updateFailure = new IllegalStateException("index unavailable");
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("POST").json("{\"type\":\"dept\",\"name\":\"x\"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(Boolean.TRUE, payload(res).get("added"));
        Assertions.assertEquals(List.of("addTag(alice," + URL + "," + DOC_ID + ",dept:x)", "updateDocuments(" + URL + ")"),
                tagHelper.writes());
        // The counts fall back to the tag log, which already has the new tag.
        Assertions.assertEquals(List.of(tag("dept:x", "dept", "x", 1, true, true)), tags(res));
    }

    @Test
    public void test_post_bodyErrors() throws Exception {
        final Response wrongType = new Response();
        new StubHandler().user("alice")
                .handle(request("POST").body("text/plain", "{\"type\":\"dept\",\"name\":\"x\"}"), wrongType.proxy(), DOC_ID);
        Assertions.assertEquals(415, wrongType.status, wrongType.body());
        Assertions.assertEquals("unsupported_media_type", errorCode(wrongType));

        final Response malformed = new Response();
        new StubHandler().user("alice").handle(request("POST").json("{\"type\":"), malformed.proxy(), DOC_ID);
        Assertions.assertEquals(400, malformed.status, malformed.body());
        Assertions.assertEquals("invalid_request", errorCode(malformed));

        final Response tooLarge = new Response();
        new StubHandler().user("alice")
                .handle(request("POST").json("{\"type\":\"dept\",\"name\":\"" + "x".repeat(2100) + "\"}"), tooLarge.proxy(), DOC_ID);
        Assertions.assertEquals(413, tooLarge.status, tooLarge.body());
        Assertions.assertEquals("payload_too_large", errorCode(tooLarge));
        assertNoWrites();
    }

    // ===================================================================================
    //                                                                            DELETE
    //                                                                            ======

    @Test
    public void test_delete_ownTag() throws Exception {
        tagHelper.countMap.put("dept:x", 2L);
        tagHelper.userTags.put("alice", new LinkedHashSet<>(List.of("dept:x")));
        tagHelper.userTags.put("bob", new LinkedHashSet<>(List.of("dept:x")));
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("DELETE").param("value", "dept:x"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(List.of("removeTag(alice," + URL + ",dept:x)", "updateDocuments(" + URL + ")"), tagHelper.writes());
        final Map<String, Object> payload = payload(res);
        Assertions.assertEquals(1, payload.get("removed"));
        Assertions.assertEquals(List.of(tag("dept:x", "dept", "x", 1, false, false)), tags(res));
    }

    @Test
    public void test_delete_nothingRemoved_skipsDocumentUpdate() throws Exception {
        tagHelper.countMap.put("dept:x", 1L);
        tagHelper.userTags.put("bob", new LinkedHashSet<>(List.of("dept:x")));
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("DELETE").param("value", "dept:x"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(0, payload(res).get("removed"));
        Assertions.assertEquals(List.of("removeTag(alice," + URL + ",dept:x)"), tagHelper.writes());
        Assertions.assertEquals(List.of(tag("dept:x", "dept", "x", 1, false, false)), tags(res));
    }

    @Test
    public void test_delete_byAdministrator_removesEveryUsersTag() throws Exception {
        tagHelper.countMap.put("dept:x", 2L);
        tagHelper.userTags.put("alice", new LinkedHashSet<>(List.of("dept:x")));
        tagHelper.userTags.put("bob", new LinkedHashSet<>(List.of("dept:x")));
        final StubHandler handler = new StubHandler().user("admin");
        handler.administrator = Boolean.TRUE;
        final Response res = new Response();
        handler.handle(request("DELETE").param("value", "dept:x"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(List.of("removeTag(null," + URL + ",dept:x)", "updateDocuments(" + URL + ")"), tagHelper.writes());
        Assertions.assertEquals(2, payload(res).get("removed"));
        Assertions.assertEquals(List.of(), tags(res));
    }

    @Test
    public void test_delete_invalidValue_returns400() throws Exception {
        final String[] values = { null, "", "dept", "dept:", ":x", "secret:x", "de pt:x", "dept-x" };
        for (final String value : values) {
            final StubHandler handler = new StubHandler().user("alice");
            handler.administrator = Boolean.TRUE;
            final Response res = new Response();
            final StubRequest req = request("DELETE");
            if (value != null) {
                req.param("value", value);
            }
            handler.handle(req, res.proxy(), DOC_ID);
            Assertions.assertEquals(400, res.status, value + " -> " + res.body());
            Assertions.assertEquals("invalid_request", errorCode(res), value);
            Assertions.assertEquals("invalid tag value", errorMessage(res), value);
        }
        assertNoWrites();
    }

    // ===================================================================================
    //                                                                               GET
    //                                                                               ===

    @Test
    public void test_get_listsOnlyVisibleTypes() throws Exception {
        tagHelper.countMap.put("secret:hidden", 9L);
        tagHelper.countMap.put("dept:a", 3L);
        tagHelper.countMap.put("not a tag", 7L);
        tagHelper.countMap.put("project:b", 1L);
        tagHelper.userTags.put("alice", new LinkedHashSet<>(List.of("project:b", "secret:hidden")));
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("GET"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(Boolean.TRUE, payload(res).get("addable"));
        Assertions.assertEquals(List.of(tag("dept:a", "dept", "a", 3, false, false), tag("project:b", "project", "b", 1, true, true)),
                tags(res));
        assertFalse(res.body().contains("secret"), res.body());
        assertNoWrites();
    }

    @Test
    public void test_get_administrator_canRemoveEveryTag() throws Exception {
        tagHelper.countMap.put("dept:a", 3L);
        tagHelper.countMap.put("project:b", 1L);
        final StubHandler handler = new StubHandler().user("admin");
        handler.administrator = Boolean.TRUE;
        final Response res = new Response();
        handler.handle(request("GET"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(List.of(tag("dept:a", "dept", "a", 3, false, true), tag("project:b", "project", "b", 1, false, true)),
                tags(res));
    }

    @Test
    public void test_get_noVisibleType_isNotAddable() throws Exception {
        tagHelper.countMap.put("dept:a", 3L);
        final StubHandler handler = new StubHandler().user("alice");
        handler.typeSet = Collections.emptySet();
        final Response res = new Response();
        handler.handle(request("GET"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(Boolean.FALSE, payload(res).get("addable"));
        Assertions.assertEquals(List.of(), tags(res));
    }

    // ===================================================================================
    //                                                                    Administrator
    //                                                                    =============

    @Test
    public void test_isTagAdministrator_adminAndLabelTypeRoles() {
        final DocumentTagsHandler handler = new DocumentTagsHandler();
        assertFalse(handler.isTagAdministrator(OptionalThing.empty()));
        assertFalse(handler.isTagAdministrator(OptionalThing.of(new FessUserBean(new StubFessUser("u", "user")))));
        assertTrue(handler.isTagAdministrator(OptionalThing.of(new FessUserBean(new StubFessUser("a", "admin")))));
        assertTrue(handler.isTagAdministrator(OptionalThing.of(new FessUserBean(new StubFessUser("l", "user", "admin-labeltype")))));
        // A view-only label role cannot moderate.
        assertFalse(handler.isTagAdministrator(OptionalThing.of(new FessUserBean(new StubFessUser("v", "admin-labeltype-view")))));
    }

    @Test
    public void test_delete_byLabelTypeAdministratorThroughTheRealRoleCheck() throws Exception {
        tagHelper.countMap.put("dept:x", 1L);
        final StubHandler handler = new StubHandler();
        handler.user = OptionalThing.of(new FessUserBean(new StubFessUser("mod", "admin-labeltype")));
        final Response res = new Response();
        handler.handle(request("DELETE").param("value", "dept:x"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals("removeTag(null," + URL + ",dept:x)", tagHelper.writes().get(0));

        tagHelper.calls.clear();
        final StubHandler plain = new StubHandler();
        plain.user = OptionalThing.of(new FessUserBean(new StubFessUser("carol", "user")));
        plain.handle(request("DELETE").param("value", "dept:x"), new Response().proxy(), DOC_ID);
        Assertions.assertEquals("removeTag(carol," + URL + ",dept:x)", tagHelper.writes().get(0));
    }

    // ===================================================================================
    //                                                                           Helpers
    //                                                                           =======

    private void assertNoWrites() {
        Assertions.assertEquals(List.of(), tagHelper.writes());
    }

    private static Map<String, Object> tag(final String value, final String type, final String name, final long count, final boolean mine,
            final boolean removable) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("value", value);
        map.put("type", type);
        map.put("name", name);
        map.put("count", count);
        map.put("mine", mine);
        map.put("removable", removable);
        return map;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> envelope(final Response res) {
        final Map<String, Object> root = JsonMapper.builder().build().readValue(res.body(), Map.class);
        return (Map<String, Object>) root.get("response");
    }

    private static Map<String, Object> payload(final Response res) {
        final Map<String, Object> envelope = envelope(res);
        Assertions.assertEquals(0, envelope.get("status"), res.body());
        return envelope;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> tags(final Response res) {
        final List<Map<String, Object>> tags = new ArrayList<>();
        for (final Object item : (List<Object>) payload(res).get("tags")) {
            final Map<String, Object> map = new LinkedHashMap<>((Map<String, Object>) item);
            // JSON numbers come back as Integer; compare counts as long.
            map.computeIfPresent("count", (k, v) -> ((Number) v).longValue());
            tags.add(map);
        }
        return tags;
    }

    @SuppressWarnings("unchecked")
    private static String errorCode(final Response res) {
        return (String) ((Map<String, Object>) envelope(res).get("error")).get("code");
    }

    @SuppressWarnings("unchecked")
    private static String errorMessage(final Response res) {
        return (String) ((Map<String, Object>) envelope(res).get("error")).get("message");
    }

    private static StubRequest request(final String method) {
        return new StubRequest(method);
    }

    /** A handler whose caller, visible tag types and document are set by the test. */
    private static class StubHandler extends DocumentTagsHandler {
        OptionalThing<FessUserBean> user = OptionalThing.empty();
        Set<String> typeSet = new LinkedHashSet<>(List.of("dept", "project"));
        Map<String, Object> doc = new HashMap<>(Map.of("url", URL));
        Boolean administrator;
        int documentLookups;
        String lastDocId;
        String[] lastFields;
        OptionalThing<FessUserBean> lastUser;

        StubHandler user(final String name) {
            user = name == null ? OptionalThing.empty() : OptionalThing.of(new FessUserBean(new StubFessUser(name, "user")));
            return this;
        }

        void handle(final StubRequest req, final HttpServletResponse res, final String docId) throws java.io.IOException {
            handle(req.proxy(), res, docId);
        }

        @Override
        protected Map<String, Object> getDocument(final String docId, final String[] fields, final OptionalThing<FessUserBean> userBean) {
            documentLookups++;
            lastDocId = docId;
            lastFields = fields;
            lastUser = userBean;
            return doc;
        }

        @Override
        protected Set<String> getTagTypeValueSet(final HttpServletRequest req) {
            return typeSet;
        }

        @Override
        protected OptionalThing<FessUserBean> getUserBean() {
            return user;
        }

        @Override
        protected boolean isTagAdministrator(final OptionalThing<FessUserBean> userBean) {
            return administrator == null ? super.isTagAdministrator(userBean) : administrator;
        }
    }

    /**
     * Keeps the tag log in memory. The tag rules (name normalization, value parsing, visibility) are the real ones;
     * only the storage methods are replaced, and each call is recorded.
     */
    private static class FakeTagHelper extends TagHelper {
        boolean enabled = true;
        boolean addResult = true;
        RuntimeException updateFailure;
        RuntimeException countFailure;
        final Map<String, Long> countMap = new LinkedHashMap<>();
        final Map<String, Set<String>> userTags = new HashMap<>();
        final List<String> calls = new ArrayList<>();

        List<String> writes() {
            return calls.stream()
                    .filter(c -> c.startsWith("addTag") || c.startsWith("removeTag") || c.startsWith("updateDocuments"))
                    .toList();
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public Map<String, Long> getTagCountMap(final String url) {
            calls.add("getTagCountMap(" + url + ")");
            if (countFailure != null) {
                throw countFailure;
            }
            return new LinkedHashMap<>(countMap);
        }

        @Override
        public Set<String> getUserTagSet(final String user, final String url) {
            calls.add("getUserTagSet(" + user + "," + url + ")");
            return new LinkedHashSet<>(userTags.getOrDefault(user, Collections.emptySet()));
        }

        @Override
        public boolean addTag(final String user, final String url, final String docId, final String value) {
            calls.add("addTag(" + user + "," + url + "," + docId + "," + value + ")");
            if (!addResult) {
                return false;
            }
            userTags.computeIfAbsent(user, k -> new LinkedHashSet<>()).add(value);
            countMap.merge(value, 1L, Long::sum);
            return true;
        }

        @Override
        public int removeTag(final String user, final String url, final String value) {
            calls.add("removeTag(" + user + "," + url + "," + value + ")");
            int removed = 0;
            for (final Map.Entry<String, Set<String>> entry : userTags.entrySet()) {
                if ((user == null || user.equals(entry.getKey())) && entry.getValue().remove(value)) {
                    removed++;
                }
            }
            if (removed > 0) {
                final long left = countMap.getOrDefault(value, 0L) - removed;
                if (left > 0) {
                    countMap.put(value, left);
                } else {
                    countMap.remove(value);
                }
            }
            return removed;
        }

        @Override
        public Map<String, Long> updateDocuments(final String url) {
            calls.add("updateDocuments(" + url + ")");
            if (updateFailure != null) {
                throw updateFailure;
            }
            return new LinkedHashMap<>(countMap);
        }
    }

    /** A configuration whose user tag limits are set by the test. */
    private static class TagFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;
        int nameMaxLength = 50;
        int maxPerDocument = 10;
        int maxDocumentTags = 100;

        @Override
        public boolean isUserTagEnabled() {
            return true;
        }

        @Override
        public Integer getUserTagNameMaxLengthAsInteger() {
            return nameMaxLength;
        }

        @Override
        public Integer getUserTagMaxPerDocumentAsInteger() {
            return maxPerDocument;
        }

        @Override
        public Integer getUserTagMaxDocumentTagsAsInteger() {
            return maxDocumentTags;
        }

        @Override
        public String getIndexFieldUrl() {
            return "url";
        }

        @Override
        public String[] getAuthenticationAdminRolesAsArray() {
            return new String[] { "admin" };
        }
    }

    /** Minimal {@link FessUser} with a name and roles. */
    private static class StubFessUser implements FessUser {
        private static final long serialVersionUID = 1L;
        private final String name;
        private final String[] roles;

        StubFessUser(final String name, final String... roles) {
            this.name = name;
            this.roles = roles;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String[] getRoleNames() {
            return roles;
        }

        @Override
        public String[] getGroupNames() {
            return new String[0];
        }

        @Override
        public String[] getPermissions() {
            return new String[0];
        }
    }

    /** A request built through a dynamic proxy: method, query parameters and an optional body. */
    private static class StubRequest {
        private final String method;
        private final Map<String, String> params = new HashMap<>();
        private byte[] body = new byte[0];
        private String contentType;

        StubRequest(final String method) {
            this.method = method;
        }

        StubRequest param(final String name, final String value) {
            params.put(name, value);
            return this;
        }

        StubRequest json(final String json) {
            return body("application/json", json);
        }

        StubRequest body(final String type, final String content) {
            contentType = type;
            body = content.getBytes(StandardCharsets.UTF_8);
            return this;
        }

        HttpServletRequest proxy() {
            final ByteArrayInputStream in = new ByteArrayInputStream(body);
            final ServletInputStream stream = new ServletInputStream() {
                @Override
                public int read() {
                    return in.read();
                }

                @Override
                public boolean isFinished() {
                    return in.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(final ReadListener listener) {
                    // not used
                }
            };
            final InvocationHandler h = (proxy, m, args) -> switch (m.getName()) {
            case "getMethod" -> method;
            case "getParameter" -> params.get(args[0]);
            case "getContentType" -> contentType;
            case "getContentLength" -> body.length;
            case "getContentLengthLong" -> (long) body.length;
            case "getInputStream" -> stream;
            case "getLocale" -> Locale.ENGLISH;
            case "getRequestURI", "getServletPath" -> PATH;
            case "hashCode" -> System.identityHashCode(proxy);
            case "equals" -> proxy == args[0];
            case "toString" -> "StubRequest[" + method + " " + PATH + "]";
            default -> defaultValue(m.getReturnType());
            };
            return (HttpServletRequest) Proxy.newProxyInstance(DocumentTagsHandlerTest.class.getClassLoader(),
                    new Class<?>[] { HttpServletRequest.class }, h);
        }
    }

    /** Captures the status, the headers and the body written through a dynamic proxy. */
    private static class Response {
        int status = 200;
        final Map<String, String> headers = new HashMap<>();
        private final StringWriter sw = new StringWriter();
        private final PrintWriter writer = new PrintWriter(sw);

        String body() {
            writer.flush();
            return sw.toString();
        }

        HttpServletResponse proxy() {
            final InvocationHandler h = (proxy, m, args) -> switch (m.getName()) {
            case "setStatus" -> {
                status = (Integer) args[0];
                yield null;
            }
            case "getStatus" -> status;
            case "setHeader", "addHeader" -> {
                headers.put((String) args[0], (String) args[1]);
                yield null;
            }
            case "getHeader" -> headers.get(args[0]);
            case "getWriter" -> writer;
            case "resetBuffer" -> {
                writer.flush();
                sw.getBuffer().setLength(0);
                yield null;
            }
            case "getCharacterEncoding" -> "UTF-8";
            case "hashCode" -> System.identityHashCode(proxy);
            case "equals" -> proxy == args[0];
            case "toString" -> "Response[" + status + "]";
            default -> defaultValue(m.getReturnType());
            };
            return (HttpServletResponse) Proxy.newProxyInstance(DocumentTagsHandlerTest.class.getClassLoader(),
                    new Class<?>[] { HttpServletResponse.class }, h);
        }
    }

    private static Object defaultValue(final Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) {
            return Boolean.FALSE;
        }
        if (type == int.class) {
            return 0;
        }
        if (type == long.class) {
            return 0L;
        }
        return null;
    }
}
