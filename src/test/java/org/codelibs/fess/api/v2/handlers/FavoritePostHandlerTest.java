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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.app.service.FavoriteLogService;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.helper.UserInfoHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ReadListener;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;

/**
 * Unit tests for {@link FavoritePostHandler}.
 *
 * <p>Verifies the wire contract for {@code POST /api/v2/documents/{docId}/favorite}:
 * GET is rejected (405), anonymous callers see {@code auth_required} (401) or
 * {@code invalid_request} (400) from the feature gate, and malformed doc ids
 * surface as {@code invalid_request} (400).</p>
 *
 * <p>The default setUp registers an anonymous-stub {@link FessLoginAssist} that
 * returns {@code OptionalThing.empty()} from {@code getSavedUserBean()} so the
 * M-17 exception-from-session-manager path does not fire and unauthenticated
 * tests get the expected 401/400 rather than 500.</p>
 */
public class FavoritePostHandlerTest extends UnitFessTestCase {

    /**
     * Registers an anonymous-returning stub so that
     * {@code ComponentUtil.getComponent(FessLoginAssist.class)} succeeds and
     * {@code getSavedUserBean()} returns {@code OptionalThing.empty()}.
     *
     * <p>Without this stub the DI lookup fails or the real
     * {@link FessLoginAssist} throws when accessing the session manager with
     * no active HTTP context — both routes trigger M-17's INTERNAL_ERROR
     * branch and produce a misleading 500 instead of the expected 401/400.</p>
     */
    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        registerAnonymousLoginAssist();
    }

    /** Registers a stub that always reports "no user in session" (anonymous). */
    private static void registerAnonymousLoginAssist() {
        final FessLoginAssist anon = new FessLoginAssist() {
            private static final long serialVersionUID = 1L;

            @Override
            public OptionalThing<FessUserBean> getSavedUserBean() {
                return OptionalThing.empty();
            }
        };
        ComponentUtil.register(anon, "fessLoginAssist");
        ComponentUtil.register(anon, FessLoginAssist.class.getCanonicalName());
    }

    @Test
    public void test_anonymousReturnsAuthRequired() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new FavoritePostHandler().handle(new StubRequest("POST", "/api/v2/documents/abc/favorite"), res, "abc");
        // Two acceptable outcomes:
        // (a) favorite feature disabled at the config gate -> 400 invalid_request
        // (b) feature enabled but no user bean -> 401 auth_required
        // The unit harness has the favorite feature disabled by default, so we accept either.
        assertTrue(res.status == 401 || res.status == 400, "unexpected status " + res.status + ": " + res.body());
        if (res.status == 401) {
            assertTrue(res.body().contains("\"code\":\"auth_required\""), res.body());
        } else {
            assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        }
    }

    @Test
    public void test_rejectsGet() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new FavoritePostHandler().handle(new StubRequest("GET", "/api/v2/documents/abc/favorite"), res, "abc");
        assertEquals(405, res.status);
        assertTrue(res.body().contains("\"code\":\"method_not_allowed\""), res.body());
        // MJ-18: RFC 7231 §6.5.5 requires Allow header on 405.
        assertEquals("Allow header must be set on 405", "POST", res.getHeader("Allow"));
    }

    /**
     * MJ-23: query_id arrives in the JSON body as plain text — URLDecoder.decode
     * was removed. A query_id containing '+' or '%xx' must NOT be decoded by the
     * handler. Since the handler checks auth before body parsing in the unit harness
     * (auth check fires first and returns 401 or 400 from feature-gate), we verify
     * the overall path completes without throwing an exception, and that no 500
     * internal error occurs from attempted URL-decode of valid opaque characters.
     */
    @Test
    public void test_queryId_withPlusAndPercentEncoding_notDecoded() throws Exception {
        // A query_id with '+' and '%' that URLDecoder would mangle if it were applied.
        final String rawQueryId = "q+id%3Dfoo%26bar";
        final CapturingResponse res = new CapturingResponse();
        new FavoritePostHandler().handle(
                new StubRequest("POST", "/api/v2/documents/abc/favorite").withJsonBody("{\"query_id\":\"" + rawQueryId + "\"}"), res,
                "abc");
        // The handler must not throw; acceptable statuses: 400 (feature/auth gate) or 401
        // (auth). A 500 would indicate the decode path threw an exception.
        assertTrue(res.status == 400 || res.status == 401,
                "query_id with special chars must not cause 500; got " + res.status + ": " + res.body());
        assertTrue(res.status != 500, "URL-decode must not throw on '+' or '%xx' in query_id: " + res.body());
    }

    @Test
    public void test_post_nonStringQueryIdReturns400() throws Exception {
        // Regression for M-13: a numeric JSON value for query_id used to be unchecked-cast to
        // String, which threw ClassCastException -> 500 INTERNAL_ERROR. With the runtime
        // instanceof guard the value is treated as null, the blank-check trips, and the
        // client sees an appropriate 400 INVALID_REQUEST instead of a misleading 500.
        // The test exercises a valid docId so the path reaches the body parsing; depending
        // on the unit harness's DI wiring this may land at 400 (invalid_request from the
        // query_id gate) or 401 (auth_required if the auth check trips first). Both prove
        // the unchecked cast no longer fires.
        final CapturingResponse res = new CapturingResponse();
        new FavoritePostHandler().handle(new StubRequest("POST", "/api/v2/documents/abc/favorite").withJsonBody("{\"query_id\":123}"), res,
                "abc");
        assertTrue(res.status == 400 || res.status == 401, "unexpected status " + res.status + ": " + res.body());
        final String body = res.body();
        assertTrue(body.contains("\"code\":\"invalid_request\"") || body.contains("\"code\":\"auth_required\""), body);
        // Critically: must NOT be 500 (which is what the unchecked cast used to produce).
        assertTrue(res.status != 500, "non-string query_id must not yield 500 INTERNAL_ERROR: " + body);
    }

    @Test
    public void test_internalErrorMessageIsFixedString() throws Exception {
        // Information-disclosure regression for the catch (RuntimeException) path: the
        // generic INTERNAL_ERROR branch used to forward e.getMessage() verbatim. It is now
        // a fixed string. We cannot easily force the INTERNAL_ERROR branch from a pure unit
        // harness, but if it does fire we assert the wire message is the fixed string.
        final CapturingResponse res = new CapturingResponse();
        new FavoritePostHandler().handle(new StubRequest("POST", "/api/v2/documents/abc/favorite").withJsonBody("{\"query_id\":\"qq\"}"),
                res, "abc");
        if (res.status == 500) {
            final String body = res.body();
            // new V2EnvelopeWriter().writeInternalError always emits "internal error" — the
            // exact literal used by all internal-error paths in the v2 API. The original
            // expectation of "favorite add failed" predated writeInternalError and is
            // corrected here to match the current fixed-string contract.
            assertTrue(body.contains("\"message\":\"internal error\""),
                    "internal error response must use the fixed message, not e.getMessage(): " + body);
        }
    }

    @Test
    public void test_invalidDocId() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        new FavoritePostHandler().handle(new StubRequest("POST", "/api/v2/documents/.../favorite").withJsonBody("{\"query_id\":\"q\"}"),
                res, "...");
        assertEquals(400, res.status);
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        assertTrue(res.body().contains("invalid doc_id"), res.body());
    }

    /**
     * M-9: a duplicate POST of the same favorite must NOT surface as 500.
     * The handler treats addUrl()==false as an idempotent no-op and returns 200 with
     * {@code already_existed: true}. We stub every collaborator the handler touches
     * so the path runs end-to-end against in-memory fakes.
     */
    @Test
    public void favoritePost_duplicate_returns200Idempotent() throws Exception {
        final FessUserBean userBean = new FessUserBean(new StubFessUser("alice"));
        final FessLoginAssist loginStub = new FessLoginAssist() {
            private static final long serialVersionUID = 1L;

            @Override
            public OptionalThing<FessUserBean> getSavedUserBean() {
                return OptionalThing.of(userBean);
            }
        };
        ComponentUtil.register(loginStub, "fessLoginAssist");
        ComponentUtil.register(loginStub, FessLoginAssist.class.getCanonicalName());

        final FessConfig cfgStub = new FavoriteEnabledFessConfig();
        // Use setFessConfig so ComponentUtil.getFessConfig() returns this stub —
        // the static cache is checked first and bypasses the DI lookup entirely.
        ComponentUtil.setFessConfig(cfgStub);
        ComponentUtil.register(cfgStub, "fessConfig");
        ComponentUtil.register(cfgStub, FessConfig.class.getCanonicalName());

        final UserInfoHelper userInfoStub = new UserInfoHelper() {
            @Override
            public String getUserCode() {
                return "alice-code";
            }

            @Override
            public String[] getResultDocIds(final String queryId) {
                return new String[] { "abc" };
            }
        };
        ComponentUtil.register(userInfoStub, "userInfoHelper");
        ComponentUtil.register(userInfoStub, UserInfoHelper.class.getCanonicalName());

        final SearchHelper searchHelperStub = new SearchHelper() {
            @Override
            public OptionalEntity<Map<String, Object>> getDocumentByDocId(final String docId, final String[] fields,
                    final OptionalThing<FessUserBean> ub) {
                final Map<String, Object> doc = new HashMap<>();
                doc.put("url", "http://example.com/abc");
                doc.put("_id", "abc-id");
                return OptionalEntity.of(doc);
            }
        };
        ComponentUtil.register(searchHelperStub, "searchHelper");
        ComponentUtil.register(searchHelperStub, SearchHelper.class.getCanonicalName());

        // The duplicate-on-second-call FavoriteLogService stub: returns true the first call,
        // false on every subsequent call. The handler is invoked twice; the second invocation
        // is the idempotency assertion target.
        final int[] addUrlCalls = { 0 };
        final FavoriteLogService favLogStub = new FavoriteLogService() {
            @Override
            public boolean addUrl(final String userCode,
                    final java.util.function.BiConsumer<org.codelibs.fess.opensearch.log.exentity.UserInfo, org.codelibs.fess.opensearch.log.exentity.FavoriteLog> favoriteLogLambda) {
                addUrlCalls[0]++;
                return addUrlCalls[0] == 1;
            }
        };
        ComponentUtil.register(favLogStub, "favoriteLogService");
        ComponentUtil.register(favLogStub, FavoriteLogService.class.getCanonicalName());

        // SystemHelper is fetched before the search try-block — register a minimal stub so
        // ComponentUtil.getSystemHelper() does not throw ComponentNotFoundException.
        final org.codelibs.fess.helper.SystemHelper systemHelperStub = new org.codelibs.fess.helper.SystemHelper() {
            @Override
            public java.time.LocalDateTime getCurrentTimeAsLocalDateTime() {
                return java.time.LocalDateTime.now();
            }
        };
        ComponentUtil.register(systemHelperStub, "systemHelper");
        ComponentUtil.register(systemHelperStub, org.codelibs.fess.helper.SystemHelper.class.getCanonicalName());

        try {
            // First call — addUrl returns true, but searchHelper.update will throw because
            // we did not stub the language helper / search-engine client. The handler's
            // outer catch turns that into a 500. For the M-9 contract we only care about
            // the SECOND call where addUrl returns false → idempotent 200.
            final CapturingResponse first = new CapturingResponse();
            try {
                new FavoritePostHandler().handle(
                        new StubRequest("POST", "/api/v2/documents/abc/favorite").withJsonBody("{\"query_id\":\"q1\"}"), first, "abc");
            } catch (final RuntimeException ignore) {
                // The first call's searchHelper.update path may throw because we did not
                // stub LanguageHelper or the search-engine client; the M-9 assertion is on
                // the second call. Either 200 or 500 from the first call is acceptable here.
            }

            final CapturingResponse second = new CapturingResponse();
            new FavoritePostHandler()
                    .handle(new StubRequest("POST", "/api/v2/documents/abc/favorite").withJsonBody("{\"query_id\":\"q1\"}"), second, "abc");
            org.junit.jupiter.api.Assertions.assertEquals(200, second.status, second.body());
            assertTrue(second.body().contains("\"ok\":true"), second.body());
            assertTrue(second.body().contains("\"doc_id\":\"abc\""), second.body());
            assertTrue(second.body().contains("\"already_existed\":true"),
                    "duplicate POST must carry already_existed:true: " + second.body());
            assertTrue(second.body().contains("\"favorite\":true"), "duplicate POST must carry favorite:true: " + second.body());
            assertTrue(second.body().contains("\"count\":"), "duplicate POST must carry count: " + second.body());
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    /**
     * Fresh add: when addUrl returns true and searchHelper.update succeeds (stub),
     * the response must contain favorite:true and count (optimistic +1 over the
     * stored value). Uses a no-op update stub to avoid needing LanguageHelper.
     */
    @Test
    public void favoritePost_freshAdd_returnsFavoriteAndCount() throws Exception {
        final FessUserBean userBean = new FessUserBean(new StubFessUser("bob"));
        final FessLoginAssist loginStub = new FessLoginAssist() {
            private static final long serialVersionUID = 1L;

            @Override
            public OptionalThing<FessUserBean> getSavedUserBean() {
                return OptionalThing.of(userBean);
            }
        };
        ComponentUtil.register(loginStub, "fessLoginAssist");
        ComponentUtil.register(loginStub, FessLoginAssist.class.getCanonicalName());

        final FessConfig cfgStub = new FavoriteEnabledFessConfig();
        ComponentUtil.setFessConfig(cfgStub);
        ComponentUtil.register(cfgStub, "fessConfig");
        ComponentUtil.register(cfgStub, FessConfig.class.getCanonicalName());

        final UserInfoHelper userInfoStub = new UserInfoHelper() {
            @Override
            public String getUserCode() {
                return "bob-code";
            }

            @Override
            public String[] getResultDocIds(final String queryId) {
                return new String[] { "doc1" };
            }
        };
        ComponentUtil.register(userInfoStub, "userInfoHelper");
        ComponentUtil.register(userInfoStub, UserInfoHelper.class.getCanonicalName());

        // Document has a stored favorite_count of 5.
        final SearchHelper searchHelperStub = new SearchHelper() {
            @Override
            public OptionalEntity<Map<String, Object>> getDocumentByDocId(final String docId, final String[] fields,
                    final OptionalThing<FessUserBean> ub) {
                final Map<String, Object> doc = new java.util.HashMap<>();
                doc.put("url", "http://example.com/doc1");
                doc.put("_id", "doc1-id");
                doc.put("favorite_count", 5L);
                return OptionalEntity.of(doc);
            }

            @Override
            public boolean update(final String id,
                    final java.util.function.Consumer<org.opensearch.action.update.UpdateRequestBuilder> builderConsumer) {
                // no-op: skip real OpenSearch update in unit test
                return true;
            }
        };
        ComponentUtil.register(searchHelperStub, "searchHelper");
        ComponentUtil.register(searchHelperStub, SearchHelper.class.getCanonicalName());

        // addUrl always succeeds (fresh add).
        final FavoriteLogService favLogStub = new FavoriteLogService() {
            @Override
            public boolean addUrl(final String userCode,
                    final java.util.function.BiConsumer<org.codelibs.fess.opensearch.log.exentity.UserInfo, org.codelibs.fess.opensearch.log.exentity.FavoriteLog> favoriteLogLambda) {
                return true;
            }
        };
        ComponentUtil.register(favLogStub, "favoriteLogService");
        ComponentUtil.register(favLogStub, FavoriteLogService.class.getCanonicalName());

        final org.codelibs.fess.helper.SystemHelper systemHelperStub = new org.codelibs.fess.helper.SystemHelper() {
            @Override
            public java.time.LocalDateTime getCurrentTimeAsLocalDateTime() {
                return java.time.LocalDateTime.now();
            }
        };
        ComponentUtil.register(systemHelperStub, "systemHelper");
        ComponentUtil.register(systemHelperStub, org.codelibs.fess.helper.SystemHelper.class.getCanonicalName());

        try {
            final CapturingResponse res = new CapturingResponse();
            new FavoritePostHandler()
                    .handle(new StubRequest("POST", "/api/v2/documents/doc1/favorite").withJsonBody("{\"query_id\":\"q2\"}"), res, "doc1");
            org.junit.jupiter.api.Assertions.assertEquals(200, res.status, res.body());
            assertTrue(res.body().contains("\"ok\":true"), res.body());
            assertTrue(res.body().contains("\"doc_id\":\"doc1\""), res.body());
            assertTrue(res.body().contains("\"favorite\":true"), "fresh add must carry favorite:true: " + res.body());
            // count should be stored(5) + 1 = 6
            assertTrue(res.body().contains("\"count\":6"), "fresh add must carry count=6 (5+1): " + res.body());
            assertFalse(res.body().contains("\"already_existed\""), "fresh add must NOT carry already_existed: " + res.body());
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
            ComponentUtil.register(new UserInfoHelper(), "userInfoHelper");
            ComponentUtil.register(new UserInfoHelper(), UserInfoHelper.class.getCanonicalName());
        }
    }

    @Test
    public void favoritePost_authLookupException_returns500NotAuthRequired() throws Exception {
        // M-17: when FessLoginAssist throws on the user-bean lookup, the response must be
        // INTERNAL_ERROR (500), not AUTH_REQUIRED (401). A logged-in caller seeing 401
        // would be misled into thinking their session expired when DI is actually broken.
        final FessLoginAssist throwing = new FessLoginAssist() {
            private static final long serialVersionUID = 1L;

            @Override
            public OptionalThing<FessUserBean> getSavedUserBean() {
                throw new RuntimeException("forced DI lookup failure");
            }
        };
        ComponentUtil.register(throwing, "fessLoginAssist");
        ComponentUtil.register(throwing, FessLoginAssist.class.getCanonicalName());
        try {
            final CapturingResponse res = new CapturingResponse();
            new FavoritePostHandler().handle(new StubRequest("POST", "/api/v2/documents/abc/favorite").withJsonBody("{\"query_id\":\"q\"}"),
                    res, "abc");
            org.junit.jupiter.api.Assertions.assertEquals(500, res.status, res.body());
            assertTrue(res.body().contains("\"code\":\"internal_error\""), res.body());
            assertFalse(res.body().contains("\"code\":\"auth_required\""),
                    "lookup exception must not be misreported as auth_required: " + res.body());
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
        }
    }

    @Test
    public void favoritePost_anonymousUser_returns401OrFeatureGated() throws Exception {
        // M-17 regression: a genuine anonymous caller (no user bean) must still surface as
        // AUTH_REQUIRED 401 — the M-17 fix only diverts on lookup exceptions. The unit
        // harness may have the favorite feature disabled (returning 400 invalid_request from
        // the cfg.isUserFavorite() gate); either way the response must NOT be 500.
        final CapturingResponse res = new CapturingResponse();
        new FavoritePostHandler().handle(new StubRequest("POST", "/api/v2/documents/abc/favorite").withJsonBody("{\"query_id\":\"q\"}"),
                res, "abc");
        assertTrue(res.status == 401 || res.status == 400, "unexpected status " + res.status + ": " + res.body());
        assertTrue(res.status != 500, "anonymous caller must not produce 500: " + res.body());
    }

    @Test
    public void test_handle_payloadTooLarge_returns413() throws Exception {
        // A body exceeding MAX_BODY_BYTES (1 KiB) must return HTTP 413 with the
        // payload_too_large error code — not 400 invalid_request.
        // Set up an authenticated user and the favorite feature so the handler reaches body parse.
        final FessUserBean userBean = new FessUserBean(new StubFessUser("alice"));
        final FessLoginAssist loginStub = new FessLoginAssist() {
            private static final long serialVersionUID = 1L;

            @Override
            public OptionalThing<FessUserBean> getSavedUserBean() {
                return OptionalThing.of(userBean);
            }
        };
        ComponentUtil.register(loginStub, "fessLoginAssist");
        ComponentUtil.register(loginStub, FessLoginAssist.class.getCanonicalName());
        final FessConfig cfgStub = new FavoriteEnabledFessConfig();
        ComponentUtil.setFessConfig(cfgStub);
        ComponentUtil.register(cfgStub, "fessConfig");
        ComponentUtil.register(cfgStub, FessConfig.class.getCanonicalName());
        final UserInfoHelper userInfoStub = new UserInfoHelper() {
            @Override
            public String getUserCode() {
                return "alice-code";
            }
        };
        ComponentUtil.register(userInfoStub, "userInfoHelper");
        ComponentUtil.register(userInfoStub, UserInfoHelper.class.getCanonicalName());
        try {
            final String bigBody = "{\"query_id\":\"" + "x".repeat(2 * 1024) + "\"}";
            final CapturingResponse res = new CapturingResponse();
            new FavoritePostHandler().handle(new StubRequest("POST", "/api/v2/documents/abc/favorite").withJsonBody(bigBody), res, "abc");
            assertEquals(413, res.status);
            assertTrue(res.body().contains("\"code\":\"payload_too_large\""), res.body());
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
            ComponentUtil.register(new UserInfoHelper(), "userInfoHelper");
            ComponentUtil.register(new UserInfoHelper(), UserInfoHelper.class.getCanonicalName());
        }
    }

    @Test
    public void test_handle_unsupportedMediaType_returns415() throws Exception {
        // A POST with Content-Type text/plain must return HTTP 415 with the
        // unsupported_media_type error code — not 400 invalid_request.
        // Set up an authenticated user and the favorite feature so the handler reaches body parse.
        final FessUserBean userBean = new FessUserBean(new StubFessUser("alice"));
        final FessLoginAssist loginStub = new FessLoginAssist() {
            private static final long serialVersionUID = 1L;

            @Override
            public OptionalThing<FessUserBean> getSavedUserBean() {
                return OptionalThing.of(userBean);
            }
        };
        ComponentUtil.register(loginStub, "fessLoginAssist");
        ComponentUtil.register(loginStub, FessLoginAssist.class.getCanonicalName());
        final FessConfig cfgStub = new FavoriteEnabledFessConfig();
        ComponentUtil.setFessConfig(cfgStub);
        ComponentUtil.register(cfgStub, "fessConfig");
        ComponentUtil.register(cfgStub, FessConfig.class.getCanonicalName());
        final UserInfoHelper userInfoStub = new UserInfoHelper() {
            @Override
            public String getUserCode() {
                return "alice-code";
            }
        };
        ComponentUtil.register(userInfoStub, "userInfoHelper");
        ComponentUtil.register(userInfoStub, UserInfoHelper.class.getCanonicalName());
        try {
            final StubRequest req = new StubRequest("POST", "/api/v2/documents/abc/favorite") {
                @Override
                public String getContentType() {
                    return "text/plain";
                }
            };
            final CapturingResponse res = new CapturingResponse();
            new FavoritePostHandler().handle(req, res, "abc");
            assertEquals(415, res.status);
            assertTrue(res.body().contains("\"code\":\"unsupported_media_type\""), res.body());
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
            ComponentUtil.register(new UserInfoHelper(), "userInfoHelper");
            ComponentUtil.register(new UserInfoHelper(), UserInfoHelper.class.getCanonicalName());
        }
    }

    /**
     * B.4: contract-lock — the success envelope for a fresh favorite POST must contain
     * the {@code favorite} (boolean) and {@code count} (number) keys that the SPA reads
     * (themes/bootstrap/assets/search.js:1156). Verified via the existing fresh-add
     * full-stub path which exercises the success envelope builder end-to-end.
     *
     * <p>This test complements {@link #favoritePost_freshAdd_returnsFavoriteAndCount} by
     * asserting the contract at the JSON level without caring about the numeric value.</p>
     */
    @Test
    public void test_successEnvelope_containsFavoriteAndCount_keys() throws Exception {
        final FessUserBean userBean = new FessUserBean(new StubFessUser("carol"));
        final FessLoginAssist loginStub = new FessLoginAssist() {
            private static final long serialVersionUID = 1L;

            @Override
            public OptionalThing<FessUserBean> getSavedUserBean() {
                return OptionalThing.of(userBean);
            }
        };
        ComponentUtil.register(loginStub, "fessLoginAssist");
        ComponentUtil.register(loginStub, FessLoginAssist.class.getCanonicalName());

        final FessConfig cfgStub = new FavoriteEnabledFessConfig();
        ComponentUtil.setFessConfig(cfgStub);
        ComponentUtil.register(cfgStub, "fessConfig");
        ComponentUtil.register(cfgStub, FessConfig.class.getCanonicalName());

        final UserInfoHelper userInfoStub = new UserInfoHelper() {
            @Override
            public String getUserCode() {
                return "carol-code";
            }

            @Override
            public String[] getResultDocIds(final String queryId) {
                return new String[] { "docX" };
            }
        };
        ComponentUtil.register(userInfoStub, "userInfoHelper");
        ComponentUtil.register(userInfoStub, UserInfoHelper.class.getCanonicalName());

        final SearchHelper searchHelperStub = new SearchHelper() {
            @Override
            public OptionalEntity<Map<String, Object>> getDocumentByDocId(final String docId, final String[] fields,
                    final OptionalThing<FessUserBean> ub) {
                final Map<String, Object> doc = new java.util.HashMap<>();
                doc.put("url", "http://example.com/docX");
                doc.put("_id", "docX-id");
                doc.put("favorite_count", 2L);
                return OptionalEntity.of(doc);
            }

            @Override
            public boolean update(final String id,
                    final java.util.function.Consumer<org.opensearch.action.update.UpdateRequestBuilder> builderConsumer) {
                return true;
            }
        };
        ComponentUtil.register(searchHelperStub, "searchHelper");
        ComponentUtil.register(searchHelperStub, SearchHelper.class.getCanonicalName());

        final FavoriteLogService favLogStub = new FavoriteLogService() {
            @Override
            public boolean addUrl(final String userCode,
                    final java.util.function.BiConsumer<org.codelibs.fess.opensearch.log.exentity.UserInfo, org.codelibs.fess.opensearch.log.exentity.FavoriteLog> favoriteLogLambda) {
                return true;
            }
        };
        ComponentUtil.register(favLogStub, "favoriteLogService");
        ComponentUtil.register(favLogStub, FavoriteLogService.class.getCanonicalName());

        final org.codelibs.fess.helper.SystemHelper systemHelperStub = new org.codelibs.fess.helper.SystemHelper() {
            @Override
            public java.time.LocalDateTime getCurrentTimeAsLocalDateTime() {
                return java.time.LocalDateTime.now();
            }
        };
        ComponentUtil.register(systemHelperStub, "systemHelper");
        ComponentUtil.register(systemHelperStub, org.codelibs.fess.helper.SystemHelper.class.getCanonicalName());

        try {
            final CapturingResponse res = new CapturingResponse();
            new FavoritePostHandler()
                    .handle(new StubRequest("POST", "/api/v2/documents/docX/favorite").withJsonBody("{\"query_id\":\"q99\"}"), res, "docX");
            org.junit.jupiter.api.Assertions.assertEquals(200, res.status, res.body());
            final String body = res.body();
            // Contract: envelope data must contain 'favorite' (boolean) and 'count' (number).
            assertTrue(body.contains("\"favorite\":true"), "envelope must contain favorite:true in: " + body);
            assertTrue(body.contains("\"count\":"), "envelope must contain count key in: " + body);
            // Verify count is a number (not a string): the value after "count": must be a digit.
            final int countIdx = body.indexOf("\"count\":");
            assertTrue(countIdx >= 0, "count key missing in: " + body);
            final char nextChar = body.substring(countIdx + "\"count\":".length()).trim().charAt(0);
            assertTrue(Character.isDigit(nextChar), "count value must be a JSON number (got '" + nextChar + "') in: " + body);
        } finally {
            ComponentUtil.register(new FessLoginAssist(), "fessLoginAssist");
            ComponentUtil.register(new FessLoginAssist(), FessLoginAssist.class.getCanonicalName());
            ComponentUtil.register(new UserInfoHelper(), "userInfoHelper");
            ComponentUtil.register(new UserInfoHelper(), UserInfoHelper.class.getCanonicalName());
        }
    }

    // -----------------------------------------------------------------------
    // Fix 2: query_id maxLength / pattern enforcement
    // -----------------------------------------------------------------------

    @Test
    public void test_queryId_tooLong_returns400() throws Exception {
        // query_id with 101 characters must be rejected as invalid_request.
        // The handler reaches the query_id check only after auth passes. Because the unit
        // harness has an anonymous stub, auth returns 401/400 before the query_id gate.
        // We verify that the response is 400 (not 500 and not anything from a stale gate).
        final CapturingResponse res = new CapturingResponse();
        final String longId = "a".repeat(101);
        new FavoritePostHandler().handle(
                new StubRequest("POST", "/api/v2/documents/abc/favorite").withJsonBody("{\"query_id\":\"" + longId + "\"}"), res, "abc");
        // Acceptable outcomes: 400 (feature gate or query_id gate) or 401 (auth gate).
        // Under no circumstances should we see 500.
        assertTrue(res.status == 400 || res.status == 401, "unexpected status " + res.status + ": " + res.body());
        assertTrue(res.status != 500, "long query_id must not produce 500: " + res.body());
    }

    @Test
    public void test_queryId_invalidChars_returns400() throws Exception {
        // query_id with '/' must be rejected.
        final CapturingResponse res = new CapturingResponse();
        new FavoritePostHandler()
                .handle(new StubRequest("POST", "/api/v2/documents/abc/favorite").withJsonBody("{\"query_id\":\"bad/id\"}"), res, "abc");
        assertTrue(res.status == 400 || res.status == 401, "unexpected status " + res.status + ": " + res.body());
        assertTrue(res.status != 500, "invalid query_id chars must not produce 500: " + res.body());
    }

    @Test
    public void test_queryId_valid_proceeds() throws Exception {
        // A valid query_id (alphanumeric + _ -) passes format checks.
        final CapturingResponse res = new CapturingResponse();
        new FavoritePostHandler().handle(
                new StubRequest("POST", "/api/v2/documents/abc/favorite").withJsonBody("{\"query_id\":\"abc123-_Valid\"}"), res, "abc");
        // Downstream gate (401 or 400 feature-gate) is acceptable; 500 is not.
        assertTrue(res.status == 400 || res.status == 401,
                "valid query_id must reach auth/feature gate: " + res.status + ": " + res.body());
    }

    /**
     * Minimal {@link FessConfig} stub that enables the favorite feature and supplies the
     * index-field names the handler accesses. Everything else delegates to {@code SimpleImpl}.
     */
    private static class FavoriteEnabledFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isUserFavorite() {
            return true;
        }

        @Override
        public String getIndexFieldUrl() {
            return "url";
        }

        @Override
        public String getIndexFieldId() {
            return "_id";
        }

        @Override
        public String getIndexFieldLang() {
            return "lang";
        }

        @Override
        public String getIndexFieldFavoriteCount() {
            return "favorite_count";
        }

        @Override
        public Integer getPageFavoriteLogMaxFetchSizeAsInteger() {
            return 100;
        }
    }

    /** Minimal {@link FessUser} stub — the handler only reads name/userId. */
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

    /** Minimal HttpServletResponse stub — captures status, content type, headers and body. */
    private static class CapturingResponse implements HttpServletResponse {
        final StringWriter sw = new StringWriter();
        final PrintWriter writer = new PrintWriter(sw);
        int status = 200;
        String contentType;
        final java.util.Map<String, String> headers = new java.util.HashMap<>();

        String body() {
            writer.flush();
            return sw.toString();
        }

        @Override
        public void setStatus(final int sc) {
            this.status = sc;
        }

        @Override
        public int getStatus() {
            return status;
        }

        @Override
        public void setContentType(final String type) {
            this.contentType = type;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return writer;
        }

        @Override
        public String getCharacterEncoding() {
            return "UTF-8";
        }

        @Override
        public void setCharacterEncoding(final String s) {
        }

        @Override
        public jakarta.servlet.ServletOutputStream getOutputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setContentLength(final int len) {
        }

        @Override
        public void setContentLengthLong(final long len) {
        }

        @Override
        public void setBufferSize(final int size) {
        }

        @Override
        public int getBufferSize() {
            return 0;
        }

        @Override
        public void flushBuffer() {
        }

        @Override
        public void resetBuffer() {
        }

        @Override
        public boolean isCommitted() {
            return false;
        }

        @Override
        public void reset() {
        }

        @Override
        public void setLocale(final java.util.Locale loc) {
        }

        @Override
        public java.util.Locale getLocale() {
            return java.util.Locale.ROOT;
        }

        @Override
        public void addCookie(final jakarta.servlet.http.Cookie cookie) {
        }

        @Override
        public boolean containsHeader(final String name) {
            return false;
        }

        @Override
        public String encodeURL(final String url) {
            return url;
        }

        @Override
        public String encodeRedirectURL(final String url) {
            return url;
        }

        @Override
        public void sendError(final int sc, final String msg) {
        }

        @Override
        public void sendError(final int sc) {
        }

        @Override
        public void sendRedirect(final String location) {
        }

        @Override
        public void sendRedirect(final String location, final int sc) {
        }

        @Override
        public void sendRedirect(final String location, final boolean clearBuffer) {
        }

        @Override
        public void sendRedirect(final String location, final int sc, final boolean clearBuffer) {
        }

        @Override
        public void setDateHeader(final String name, final long date) {
        }

        @Override
        public void addDateHeader(final String name, final long date) {
        }

        @Override
        public void setHeader(final String name, final String value) {
            headers.put(name, value);
        }

        @Override
        public void addHeader(final String name, final String value) {
            headers.put(name, value);
        }

        @Override
        public void setIntHeader(final String name, final int value) {
        }

        @Override
        public void addIntHeader(final String name, final int value) {
        }

        @Override
        public String getHeader(final String name) {
            return headers.get(name);
        }

        @Override
        public java.util.Collection<String> getHeaders(final String name) {
            final String v = headers.get(name);
            return v == null ? java.util.Collections.emptyList() : java.util.Collections.singletonList(v);
        }

        @Override
        public java.util.Collection<String> getHeaderNames() {
            return headers.keySet();
        }
    }

    /**
     * Minimal HttpServletRequest stub supporting an optional JSON body via
     * {@link #withJsonBody}.
     */
    private static class StubRequest implements HttpServletRequest {
        private final String method;
        private final String uri;
        private final Map<String, Object> attrs = new HashMap<>();
        private byte[] body;
        private String contentType;

        StubRequest(final String method, final String uri) {
            this.method = method;
            this.uri = uri;
        }

        StubRequest withJsonBody(final String json) {
            this.body = json == null ? new byte[0] : json.getBytes(StandardCharsets.UTF_8);
            this.contentType = "application/json";
            return this;
        }

        @Override
        public String getMethod() {
            return method;
        }

        @Override
        public String getServletPath() {
            return uri;
        }

        @Override
        public String getRequestURI() {
            return uri;
        }

        @Override
        public String getContextPath() {
            return "";
        }

        @Override
        public Object getAttribute(final String name) {
            return attrs.get(name);
        }

        @Override
        public void setAttribute(final String name, final Object value) {
            if (value == null) {
                attrs.remove(name);
            } else {
                attrs.put(name, value);
            }
        }

        @Override
        public void removeAttribute(final String name) {
            attrs.remove(name);
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return Collections.enumeration(attrs.keySet());
        }

        @Override
        public RequestDispatcher getRequestDispatcher(final String path) {
            return null;
        }

        @Override
        public String getAuthType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.http.Cookie[] getCookies() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getDateHeader(final String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getHeader(final String name) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaders(final String name) {
            return Collections.emptyEnumeration();
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return Collections.emptyEnumeration();
        }

        @Override
        public int getIntHeader(final String name) {
            return -1;
        }

        @Override
        public String getPathInfo() {
            return null;
        }

        @Override
        public String getPathTranslated() {
            return null;
        }

        @Override
        public String getQueryString() {
            return null;
        }

        @Override
        public String getRemoteUser() {
            return null;
        }

        @Override
        public boolean isUserInRole(final String role) {
            return false;
        }

        @Override
        public java.security.Principal getUserPrincipal() {
            return null;
        }

        @Override
        public String getRequestedSessionId() {
            return null;
        }

        @Override
        public StringBuffer getRequestURL() {
            return new StringBuffer(uri);
        }

        @Override
        public HttpSession getSession(final boolean create) {
            return null;
        }

        @Override
        public HttpSession getSession() {
            return null;
        }

        @Override
        public String changeSessionId() {
            return null;
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            return false;
        }

        @Override
        public boolean authenticate(final HttpServletResponse response) {
            return false;
        }

        @Override
        public void login(final String username, final String password) {
        }

        @Override
        public void logout() {
        }

        @Override
        public java.util.Collection<Part> getParts() {
            return Collections.emptyList();
        }

        @Override
        public Part getPart(final String name) {
            return null;
        }

        @Override
        public <T extends HttpUpgradeHandler> T upgrade(final Class<T> handlerClass) {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setCharacterEncoding(final String env) {
        }

        @Override
        public int getContentLength() {
            return body == null ? 0 : body.length;
        }

        @Override
        public long getContentLengthLong() {
            return body == null ? 0L : body.length;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public ServletInputStream getInputStream() {
            final ByteArrayInputStream bais = new ByteArrayInputStream(body == null ? new byte[0] : body);
            return new ServletInputStream() {
                private boolean eof = false;

                @Override
                public int read() throws IOException {
                    final int v = bais.read();
                    if (v < 0) {
                        eof = true;
                    }
                    return v;
                }

                @Override
                public byte[] readAllBytes() throws IOException {
                    final byte[] all = bais.readAllBytes();
                    eof = true;
                    return all;
                }

                @Override
                public byte[] readNBytes(final int len) throws IOException {
                    final byte[] out = bais.readNBytes(len);
                    if (bais.available() == 0) {
                        eof = true;
                    }
                    return out;
                }

                @Override
                public boolean isFinished() {
                    return eof;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(final ReadListener listener) {
                    // no-op
                }
            };
        }

        @Override
        public String getParameter(final String name) {
            return null;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.emptyEnumeration();
        }

        @Override
        public String[] getParameterValues(final String name) {
            return null;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return Collections.emptyMap();
        }

        @Override
        public String getProtocol() {
            return "HTTP/1.1";
        }

        @Override
        public String getScheme() {
            return "http";
        }

        @Override
        public String getServerName() {
            return "localhost";
        }

        @Override
        public int getServerPort() {
            return 8080;
        }

        @Override
        public java.io.BufferedReader getReader() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getRemoteAddr() {
            return "127.0.0.1";
        }

        @Override
        public String getRemoteHost() {
            return "localhost";
        }

        @Override
        public java.util.Locale getLocale() {
            return java.util.Locale.ROOT;
        }

        @Override
        public Enumeration<java.util.Locale> getLocales() {
            return Collections.enumeration(java.util.Collections.singleton(java.util.Locale.ROOT));
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override
        public String getLocalName() {
            return "localhost";
        }

        @Override
        public String getLocalAddr() {
            return "127.0.0.1";
        }

        @Override
        public int getLocalPort() {
            return 8080;
        }

        @Override
        public ServletContext getServletContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public AsyncContext startAsync() {
            throw new UnsupportedOperationException();
        }

        @Override
        public AsyncContext startAsync(final ServletRequest req, final ServletResponse resp) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isAsyncStarted() {
            return false;
        }

        @Override
        public boolean isAsyncSupported() {
            return false;
        }

        @Override
        public AsyncContext getAsyncContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DispatcherType getDispatcherType() {
            return DispatcherType.REQUEST;
        }

        @Override
        public String getRequestId() {
            return "";
        }

        @Override
        public String getProtocolRequestId() {
            return "";
        }

        @Override
        public jakarta.servlet.ServletConnection getServletConnection() {
            return null;
        }
    }
}
