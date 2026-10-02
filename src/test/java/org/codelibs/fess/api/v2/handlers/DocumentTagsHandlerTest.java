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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.LabelTypeHelper.LabelTypeItem;
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
 * <p>The label types of the kind tag are kept in memory by {@link FakeTagHelper}, which replaces the writes
 * ({@code addTag}, {@code removeTag}) and the index updates ({@code addTagToDocuments},
 * {@code removeTagFromDocuments}) and records every call, while the name and permission rules
 * ({@code normalizeName}, {@code toValue}, {@code toUserPermission}, {@code isMine}) stay the real ones. The handler's
 * seams supply the caller, the tags the caller can see (those whose permissions meet the caller's) and the
 * document.</p>
 */
public class DocumentTagsHandlerTest extends UnitFessTestCase {

    private static final String DOC_ID = "doc1";

    private static final String URL = "http://example.com/page.html";

    private static final String OTHER_URL = "http://example.com/other.html";

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
            Assertions.assertEquals(0, handler.tagListLookups, method);
        }
        Assertions.assertEquals(List.of(), tagHelper.calls);
    }

    @Test
    public void test_methodIsCaseInsensitive() throws Exception {
        tagHelper.put("a", List.of(URL), "1alice");
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("get"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(List.of(tag("a", true)), tags(res));
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
        Assertions.assertEquals(List.of(), tagHelper.calls);
    }

    @Test
    public void test_featureDisabled_returns400ForEveryMethod() throws Exception {
        tagHelper.enabled = false;
        final String value = tagHelper.put("x", List.of(URL), "1alice").getValue();
        for (final String method : new String[] { "GET", "POST", "DELETE" }) {
            final StubHandler handler = new StubHandler().user("alice");
            final Response res = new Response();
            handler.handle(request(method).json("{\"name\":\"x\"}").param("value", value), res.proxy(), DOC_ID);
            Assertions.assertEquals(400, res.status, method);
            Assertions.assertEquals("invalid_request", errorCode(res), method);
            Assertions.assertEquals("tag feature is not available", errorMessage(res), method);
            Assertions.assertEquals(0, handler.documentLookups, method);
            Assertions.assertEquals(0, handler.tagListLookups, method);
        }
        Assertions.assertEquals(List.of(), tagHelper.calls);
    }

    @Test
    public void test_anonymousWrites_return401() throws Exception {
        final String value = tagHelper.put("x", List.of(URL), "Rguest").getValue();
        for (final String user : new String[] { null, "", " " }) {
            for (final String method : new String[] { "POST", "DELETE" }) {
                final StubHandler handler = new StubHandler().user(user).permissions("Rguest");
                final Response res = new Response();
                handler.handle(request(method).json("{\"name\":\"x\"}").param("value", value), res.proxy(), DOC_ID);
                Assertions.assertEquals(401, res.status, method + " user=" + user);
                Assertions.assertEquals("auth_required", errorCode(res));
                Assertions.assertEquals("login required", errorMessage(res));
                Assertions.assertEquals(0, handler.documentLookups);
            }
        }
        Assertions.assertEquals(List.of(), tagHelper.calls);
    }

    @Test
    public void test_anonymousGet_listsVisibleTagsAndIsNotAddable() throws Exception {
        tagHelper.put("public", List.of(URL), "1alice", "Rguest");
        tagHelper.put("private", List.of(URL), "1alice");
        final Response res = new Response();
        new StubHandler().permissions("Rguest").handle(request("GET"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        final Map<String, Object> payload = payload(res);
        Assertions.assertEquals(DOC_ID, payload.get("doc_id"));
        Assertions.assertEquals(Boolean.FALSE, payload.get("addable"));
        assertFalse(payload.containsKey("added"));
        assertFalse(payload.containsKey("removed"));
        // Nobody is logged in, so no tag is the caller's.
        Assertions.assertEquals(List.of(tag("public", false)), tags(res));
        Assertions.assertEquals(List.of(), tagHelper.calls);
    }

    @Test
    public void test_documentNotVisible_returns404() throws Exception {
        final String value = tagHelper.put("x", List.of(URL), "1alice").getValue();
        for (final String method : new String[] { "GET", "POST", "DELETE" }) {
            final StubHandler handler = new StubHandler().user("alice");
            handler.doc = null;
            final Response res = new Response();
            handler.handle(request(method).json("{\"name\":\"x\"}").param("value", value), res.proxy(), DOC_ID);
            Assertions.assertEquals(404, res.status, method);
            Assertions.assertEquals("not_found", errorCode(res));
            Assertions.assertEquals("doc not found: " + DOC_ID, errorMessage(res));
            // The document is fetched through the caller's roles, asking only for its URL.
            Assertions.assertEquals(DOC_ID, handler.lastDocId);
            Assertions.assertEquals(List.of("url"), List.of(handler.lastFields));
        }
        Assertions.assertEquals(List.of(), tagHelper.calls);
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
        final StubHandler handler = new StubHandler().user("alice");
        handler.tagListFailure = new IllegalStateException("secret backend detail");
        final Response res = new Response();
        handler.handle(request("GET"), res.proxy(), DOC_ID);
        Assertions.assertEquals(500, res.status, res.body());
        Assertions.assertEquals("internal_error", errorCode(res));
        assertFalse(res.body().contains("secret backend detail"), res.body());
    }

    // ===================================================================================
    //                                                                              POST
    //                                                                              ====

    @Test
    public void test_post_invalidName_returns400() throws Exception {
        fessConfig.nameMaxLength = 10;
        final String[] names = { "\"\"", "\"   \"", "\"\\t\\n\"", "\"abcdefghijk\"", "\"a\\u0001b\"", "\"a\\u007fb\"", "\"a\\u200bb\"",
                "\"a\\u202eb\"", "42", "null", "true", "[\"x\"]", "{\"x\":\"y\"}" };
        for (final String name : names) {
            final Response res = new Response();
            new StubHandler().user("alice").handle(request("POST").json("{\"name\":" + name + "}"), res.proxy(), DOC_ID);
            Assertions.assertEquals(400, res.status, name + " -> " + res.body());
            Assertions.assertEquals("invalid_request", errorCode(res), name);
            Assertions.assertEquals("invalid tag name: enter 1 to 10 characters", errorMessage(res), name);
        }
        // A missing name and an empty body are rejected the same way.
        for (final String body : new String[] { "{}", "{\"type\":\"dept\"}", "" }) {
            final Response res = new Response();
            new StubHandler().user("alice").handle(request("POST").json(body), res.proxy(), DOC_ID);
            Assertions.assertEquals(400, res.status, body + " -> " + res.body());
            Assertions.assertEquals("invalid tag name: enter 1 to 10 characters", errorMessage(res), body);
        }
        assertNoWrites();
    }

    @Test
    public void test_post_nameAtTheMaximumLength_isAccepted() throws Exception {
        fessConfig.nameMaxLength = 10;
        // Ten full-width letters are ten characters after NFKC; the length is counted after normalization.
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("POST").json("{\"name\":\"ＡＢＣＤＥＦＧＨＩＪ\"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals("addTag(alice," + URL + ",ABCDEFGHIJ)", tagHelper.writes().get(0));
    }

    @Test
    public void test_post_quotesAndBackslashesAreAllowed() throws Exception {
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("POST").json("{\"name\":\"a\\\"b\\\\c\"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals("addTag(alice," + URL + ",a\"b\\c)", tagHelper.writes().get(0));
        Assertions.assertEquals(List.of(tag("a\"b\\c", true)), tags(res));
    }

    @Test
    public void test_post_normalizesNameAndAddsTag() throws Exception {
        tagHelper.put("existing", List.of(URL), "1bob", "2dev");
        final Response res = new Response();
        new StubHandler().user("alice")
                .permissions("2dev")
                .handle(request("POST").json("{\"name\":\"  ＡＢＣ \\t def  \"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        final String value = tagHelper.toValue("ABC def");
        Assertions.assertEquals(List.of("addTag(alice," + URL + ",ABC def)", "addTagToDocuments(" + URL + "," + value + ")"),
                tagHelper.writes());
        final Map<String, Object> payload = payload(res);
        Assertions.assertEquals(DOC_ID, payload.get("doc_id"));
        Assertions.assertEquals(Boolean.TRUE, payload.get("added"));
        Assertions.assertEquals(Boolean.TRUE, payload.get("addable"));
        // The response lists the tags after the change.
        Assertions.assertEquals(List.of(tag("existing", false), tag("ABC def", true)), tags(res));
        // A new tag is a label type of the kind tag that only its creator can see.
        final LabelTypeItem created = tagHelper.find(value);
        Assertions.assertEquals("ABC def", created.getLabel());
        Assertions.assertEquals(List.of("1alice"), List.of(created.getPermissions()));
        Assertions.assertEquals(Set.of(URL), created.getUrlSet());
    }

    @Test
    public void test_post_tagOfAnotherUser_addsTheCaller() throws Exception {
        // bob shared the tag with the dev group, so alice can see it and add it to the document too.
        tagHelper.put("shared", List.of(OTHER_URL), "1bob", "2dev");
        final Response res = new Response();
        new StubHandler().user("alice").permissions("2dev").handle(request("POST").json("{\"name\":\"shared\"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(Boolean.TRUE, payload(res).get("added"));
        Assertions.assertEquals(List.of(tag("shared", true)), tags(res));
        Assertions.assertEquals(
                List.of("addTag(alice," + URL + ",shared)", "addTagToDocuments(" + URL + "," + tagHelper.toValue("shared") + ")"),
                tagHelper.writes());
    }

    @Test
    public void test_post_alreadyAdded_returnsAddedFalseWithoutUpdatingDocuments() throws Exception {
        tagHelper.put("x", List.of(URL), "1alice");
        final Response res = new Response();
        // "ｘ" normalizes to the "x" the user already added.
        new StubHandler().user("alice").handle(request("POST").json("{\"name\":\" ｘ \"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(Boolean.FALSE, payload(res).get("added"));
        Assertions.assertEquals(List.of("addTag(alice," + URL + ",x)"), tagHelper.writes());
        Assertions.assertEquals(List.of(tag("x", true)), tags(res));
    }

    @Test
    public void test_post_tooManyTags_returns400() throws Exception {
        fessConfig.maxDocumentTags = 2;
        tagHelper.addResult = TagHelper.AddResult.TOO_MANY_TAGS;
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("POST").json("{\"name\":\"c\"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(400, res.status, res.body());
        Assertions.assertEquals("invalid_request", errorCode(res));
        Assertions.assertEquals("too many tags: a document can have up to 2 tags", errorMessage(res));
        Assertions.assertEquals(List.of("addTag(alice," + URL + ",c)"), tagHelper.writes());
    }

    @Test
    public void test_post_tooManyLabels_returns400() throws Exception {
        tagHelper.addResult = TagHelper.AddResult.TOO_MANY_LABELS;
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("POST").json("{\"name\":\"c\"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(400, res.status, res.body());
        Assertions.assertEquals("invalid_request", errorCode(res));
        Assertions.assertEquals("no more tags can be created: the number of labels reached page.labeltype.max.fetch.size",
                errorMessage(res));
        Assertions.assertEquals(List.of("addTag(alice," + URL + ",c)"), tagHelper.writes());
    }

    @Test
    public void test_post_documentUpdateFailure_isSwallowed() throws Exception {
        tagHelper.updateFailure = new IllegalStateException("index unavailable");
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("POST").json("{\"name\":\"x\"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(Boolean.TRUE, payload(res).get("added"));
        Assertions.assertEquals(List.of("addTag(alice," + URL + ",x)", "addTagToDocuments(" + URL + "," + tagHelper.toValue("x") + ")"),
                tagHelper.writes());
        // The label type is stored, so the tag is listed; the crawler or the label updater fixes the index later.
        Assertions.assertEquals(List.of(tag("x", true)), tags(res));
    }

    @Test
    public void test_post_addTagFailure_returnsInternalError() throws Exception {
        tagHelper.addFailure = new IllegalStateException("store failed");
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("POST").json("{\"name\":\"x\"}"), res.proxy(), DOC_ID);
        Assertions.assertEquals(500, res.status, res.body());
        Assertions.assertEquals("internal_error", errorCode(res));
        Assertions.assertEquals(List.of("addTag(alice," + URL + ",x)"), tagHelper.writes());
    }

    @Test
    public void test_post_bodyErrors() throws Exception {
        final Response wrongType = new Response();
        new StubHandler().user("alice").handle(request("POST").body("text/plain", "{\"name\":\"x\"}"), wrongType.proxy(), DOC_ID);
        Assertions.assertEquals(415, wrongType.status, wrongType.body());
        Assertions.assertEquals("unsupported_media_type", errorCode(wrongType));

        final Response malformed = new Response();
        new StubHandler().user("alice").handle(request("POST").json("{\"name\":"), malformed.proxy(), DOC_ID);
        Assertions.assertEquals(400, malformed.status, malformed.body());
        Assertions.assertEquals("invalid_request", errorCode(malformed));

        final Response tooLarge = new Response();
        new StubHandler().user("alice").handle(request("POST").json("{\"name\":\"" + "x".repeat(2100) + "\"}"), tooLarge.proxy(), DOC_ID);
        Assertions.assertEquals(413, tooLarge.status, tooLarge.body());
        Assertions.assertEquals("payload_too_large", errorCode(tooLarge));
        assertNoWrites();
    }

    // ===================================================================================
    //                                                                            DELETE
    //                                                                            ======

    @Test
    public void test_delete_ownTagSharedWithOthers_keepsTheLabelType() throws Exception {
        final String value = tagHelper.put("x", List.of(URL), "1alice", "1bob").getValue();
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("DELETE").param("value", value), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        // removeTag answered false (bob is left), so the indexed documents keep the value.
        Assertions.assertEquals(List.of("removeTag(alice," + value + ")"), tagHelper.writes());
        final Map<String, Object> payload = payload(res);
        Assertions.assertEquals(Boolean.TRUE, payload.get("removed"));
        Assertions.assertEquals(Boolean.TRUE, payload.get("addable"));
        // alice can no longer see the tag.
        Assertions.assertEquals(List.of(), tags(res));
        Assertions.assertEquals(List.of("1bob"), List.of(tagHelper.find(value).getPermissions()));
    }

    @Test
    public void test_delete_lastPermission_removesTheValueFromDocuments() throws Exception {
        final String value = tagHelper.put("x", List.of(URL, OTHER_URL), "1alice").getValue();
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("DELETE").param("value", value), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(List.of("removeTag(alice," + value + ")", "removeTagFromDocuments(" + value + ")"), tagHelper.writes());
        Assertions.assertEquals(Boolean.TRUE, payload(res).get("removed"));
        Assertions.assertEquals(List.of(), tags(res));
        Assertions.assertNull(tagHelper.find(value));
    }

    @Test
    public void test_delete_documentUpdateFailure_isSwallowed() throws Exception {
        final String value = tagHelper.put("x", List.of(URL), "1alice").getValue();
        tagHelper.updateFailure = new IllegalStateException("index unavailable");
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("DELETE").param("value", value), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(Boolean.TRUE, payload(res).get("removed"));
        Assertions.assertEquals(List.of("removeTag(alice," + value + ")", "removeTagFromDocuments(" + value + ")"), tagHelper.writes());
    }

    @Test
    public void test_delete_visibleTagOfOthers_returns403() throws Exception {
        // alice sees the tag through the dev group but is not in its permissions herself.
        final String value = tagHelper.put("x", List.of(URL), "1bob", "2dev").getValue();
        final Response res = new Response();
        new StubHandler().user("alice").permissions("2dev").handle(request("DELETE").param("value", value), res.proxy(), DOC_ID);
        Assertions.assertEquals(403, res.status, res.body());
        Assertions.assertEquals("forbidden", errorCode(res));
        Assertions.assertEquals("the tag was not added by the user", errorMessage(res));
        assertNoWrites();
        Assertions.assertEquals(List.of("1bob", "2dev"), List.of(tagHelper.find(value).getPermissions()));
    }

    @Test
    public void test_delete_invalidValue_returns400() throws Exception {
        tagHelper.put("mine", List.of(URL), "1alice");
        // A tag that exists but that alice cannot see is not revealed: it is answered like an unknown value.
        final String hidden = tagHelper.put("hidden", List.of(URL), "1bob").getValue();
        final String[] values = { null, "", " ", "unknown", tagHelper.toValue("nothing"), hidden, "mine" };
        for (final String value : values) {
            final StubHandler handler = new StubHandler().user("alice");
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

    @Test
    public void test_delete_ownTagOnAnotherDocument_isAllowed() throws Exception {
        // The value only has to be a tag the caller can see; the handler does not require it on this document.
        final String value = tagHelper.put("x", List.of(OTHER_URL), "1alice").getValue();
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("DELETE").param("value", value), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(List.of("removeTag(alice," + value + ")", "removeTagFromDocuments(" + value + ")"), tagHelper.writes());
    }

    // ===================================================================================
    //                                                                               GET
    //                                                                               ===

    @Test
    public void test_get_listsVisibleTagsOfTheUrlAndMarksMine() throws Exception {
        tagHelper.put("own", List.of(OTHER_URL, URL), "1alice");
        tagHelper.put("group", List.of(URL), "1bob", "2dev");
        tagHelper.put("own elsewhere", List.of(OTHER_URL), "1alice");
        tagHelper.put("hidden", List.of(URL), "1carol");
        tagHelper.put("shared", List.of(URL), "1bob", "1alice", "2dev");
        // A URL that only starts with the document URL does not match.
        tagHelper.put("prefix", List.of(URL + "?x=1"), "1alice");
        final Response res = new Response();
        new StubHandler().user("alice").permissions("2dev").handle(request("GET"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(Boolean.TRUE, payload(res).get("addable"));
        Assertions.assertEquals(List.of(tag("own", true), tag("group", false), tag("shared", true)), tags(res));
        assertFalse(res.body().contains("hidden"), res.body());
        assertNoWrites();
    }

    @Test
    public void test_get_noTags_returnsAnEmptyList() throws Exception {
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("GET"), res.proxy(), DOC_ID);
        Assertions.assertEquals(200, res.status, res.body());
        Assertions.assertEquals(Boolean.TRUE, payload(res).get("addable"));
        Assertions.assertEquals(List.of(), tags(res));
    }

    @Test
    public void test_get_payloadShape() throws Exception {
        final String value = tagHelper.put("x", List.of(URL), "1alice").getValue();
        final Response res = new Response();
        new StubHandler().user("alice").handle(request("GET"), res.proxy(), DOC_ID);
        final Map<String, Object> payload = payload(res);
        Assertions.assertEquals(List.of("doc_id", "addable", "tags"),
                payload.keySet().stream().filter(k -> List.of("doc_id", "addable", "tags", "added", "removed").contains(k)).toList());
        @SuppressWarnings("unchecked")
        final Map<String, Object> first = ((List<Map<String, Object>>) payload.get("tags")).get(0);
        Assertions.assertEquals(List.of("value", "name", "mine"), List.copyOf(first.keySet()));
        // The value is the SHA-256 of the name in hex.
        Assertions.assertEquals(64, value.length());
        Assertions.assertEquals(value, first.get("value"));
    }

    // ===================================================================================
    //                                                                           Helpers
    //                                                                           =======

    private void assertNoWrites() {
        Assertions.assertEquals(List.of(), tagHelper.writes());
    }

    /** The JSON of a tag: its value is derived from its name. */
    private Map<String, Object> tag(final String name, final boolean mine) {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("value", tagHelper.toValue(name));
        map.put("name", name);
        map.put("mine", mine);
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
            tags.add(new LinkedHashMap<>((Map<String, Object>) item));
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

    /**
     * A handler whose caller and document are set by the test. The visible tags are those of {@link FakeTagHelper}
     * whose permissions include the caller's user permission or one of the extra permissions (a group or a role).
     */
    private class StubHandler extends DocumentTagsHandler {
        OptionalThing<FessUserBean> user = OptionalThing.empty();
        final Set<String> extraPermissions = new LinkedHashSet<>();
        Map<String, Object> doc = new HashMap<>(Map.of("url", URL));
        RuntimeException tagListFailure;
        int documentLookups;
        int tagListLookups;
        String lastDocId;
        String[] lastFields;

        StubHandler user(final String name) {
            user = name == null ? OptionalThing.empty() : OptionalThing.of(new FessUserBean(new StubFessUser(name)));
            return this;
        }

        StubHandler permissions(final String... permissions) {
            extraPermissions.addAll(Arrays.asList(permissions));
            return this;
        }

        void handle(final StubRequest req, final HttpServletResponse res, final String docId) throws java.io.IOException {
            handle(req.proxy(), res, docId);
        }

        @Override
        protected Map<String, Object> getDocument(final String docId, final String[] fields) {
            documentLookups++;
            lastDocId = docId;
            lastFields = fields;
            return doc;
        }

        @Override
        protected List<LabelTypeItem> getTagItemList(final HttpServletRequest req) {
            tagListLookups++;
            if (tagListFailure != null) {
                throw tagListFailure;
            }
            final Set<String> callerPermissions = new LinkedHashSet<>(extraPermissions);
            final String userId = user.map(FessUserBean::getUserId).orElse(null);
            if (StringUtil.isNotBlank(userId)) {
                callerPermissions.add(tagHelper.toUserPermission(userId));
            }
            return tagHelper.store.stream()
                    .filter(item -> Arrays.stream(item.getPermissions()).anyMatch(callerPermissions::contains))
                    .toList();
        }

        @Override
        protected OptionalThing<FessUserBean> getUserBean() {
            return user;
        }
    }

    /**
     * Keeps the label types of the kind tag in memory. The writes and the index updates are replaced and recorded;
     * the name and permission rules are the real ones.
     */
    private static class FakeTagHelper extends TagHelper {
        boolean enabled = true;
        /** When set, addTag answers this without changing the store. */
        TagHelper.AddResult addResult;
        RuntimeException addFailure;
        RuntimeException updateFailure;
        final List<LabelTypeItem> store = new ArrayList<>();
        final List<String> calls = new ArrayList<>();

        /** Stores a tag of the name that tags the URLs and that the permissions can see. */
        LabelTypeItem put(final String name, final List<String> urls, final String... permissions) {
            final LabelTypeItem item = new LabelTypeItem();
            item.setValue(toValue(name));
            item.setLabel(name);
            item.setTag(true);
            item.setPermissions(permissions);
            item.setUrlSet(new LinkedHashSet<>(urls));
            store.add(item);
            return item;
        }

        LabelTypeItem find(final String value) {
            return store.stream().filter(i -> i.getValue().equals(value)).findFirst().orElse(null);
        }

        List<String> writes() {
            return calls.stream().filter(c -> c.startsWith("addTag") || c.startsWith("removeTag")).toList();
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public synchronized AddResult addTag(final String userId, final String url, final String name) {
            calls.add("addTag(" + userId + "," + url + "," + name + ")");
            if (addFailure != null) {
                throw addFailure;
            }
            if (addResult != null) {
                return addResult;
            }
            final String permission = toUserPermission(userId);
            final LabelTypeItem item = find(toValue(name));
            if (item == null) {
                put(name, List.of(url), permission);
                return AddResult.ADDED;
            }
            final List<String> permissions = new ArrayList<>(Arrays.asList(item.getPermissions()));
            if (item.getUrlSet().contains(url) && permissions.contains(permission)) {
                return AddResult.ALREADY_ADDED;
            }
            final Set<String> urlSet = new LinkedHashSet<>(item.getUrlSet());
            urlSet.add(url);
            item.setUrlSet(urlSet);
            if (!permissions.contains(permission)) {
                permissions.add(permission);
            }
            item.setPermissions(permissions.toArray(new String[0]));
            return AddResult.ADDED;
        }

        @Override
        public synchronized boolean removeTag(final String userId, final String value) {
            calls.add("removeTag(" + userId + "," + value + ")");
            final LabelTypeItem item = find(value);
            if (item == null) {
                return false;
            }
            final List<String> permissions = new ArrayList<>(Arrays.asList(item.getPermissions()));
            if (!permissions.remove(toUserPermission(userId))) {
                return false;
            }
            if (permissions.isEmpty()) {
                store.remove(item);
                return true;
            }
            item.setPermissions(permissions.toArray(new String[0]));
            return false;
        }

        @Override
        public void addTagToDocuments(final String url, final String value) {
            calls.add("addTagToDocuments(" + url + "," + value + ")");
            if (updateFailure != null) {
                throw updateFailure;
            }
        }

        @Override
        public void removeTagFromDocuments(final String value) {
            calls.add("removeTagFromDocuments(" + value + ")");
            if (updateFailure != null) {
                throw updateFailure;
            }
        }
    }

    /** A configuration whose user tag limits are set by the test. */
    private static class TagFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;
        int nameMaxLength = 50;
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
        public Integer getUserTagMaxDocumentTagsAsInteger() {
            return maxDocumentTags;
        }

        @Override
        public String getRoleSearchUserPrefix() {
            return "1";
        }

        @Override
        public String getIndexFieldUrl() {
            return "url";
        }
    }

    /** Minimal {@link FessUser} with a name. */
    private static class StubFessUser implements FessUser {
        private static final long serialVersionUID = 1L;
        private final String name;

        StubFessUser(final String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String[] getRoleNames() {
            return new String[0];
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
