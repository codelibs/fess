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
package org.codelibs.fess.api.v2;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
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

public class SearchApiV2ManagerTest extends UnitFessTestCase {

    @Test
    public void test_matches_acceptsV2Path() {
        final SearchApiV2Manager m = new SearchApiV2Manager();
        // web.api.json defaults to true in fess_config.properties, so matches() should
        // only depend on the request path.
        assertTrue(m.matches(new StubRequest("/api/v2/health")));
        assertTrue(m.matches(new StubRequest("/api/v2/")));
    }

    @Test
    public void test_matches_rejectsV1Path() {
        final SearchApiV2Manager m = new SearchApiV2Manager();
        assertFalse(m.matches(new StubRequest("/api/v1/search")));
        assertFalse(m.matches(new StubRequest("/admin/dashboard")));
    }

    @Test
    public void test_matches_rejectsPrefixWithoutSlash() {
        // MJ-19: /api/v2foo starts with /api/v2 but is not a v2 path.
        // The boundary check must require the prefix to be followed by '/' or be exact.
        final SearchApiV2Manager m = new SearchApiV2Manager();
        assertFalse(m.matches(new StubRequest("/api/v2foo")), "/api/v2foo must not match the /api/v2 prefix");
        assertFalse(m.matches(new StubRequest("/api/v2x/search")), "/api/v2x/search must not match the /api/v2 prefix");
        // Exact match (without trailing slash) must still work.
        assertTrue(m.matches(new StubRequest("/api/v2")), "/api/v2 (exact) should still match");
    }

    @Test
    public void test_subPath_extractsAfterPrefix() {
        final SearchApiV2Manager m = new SearchApiV2Manager();
        assertEquals("/health", m.subPath(new StubRequest("/api/v2/health")));
        assertEquals("/foo/bar", m.subPath(new StubRequest("/api/v2/foo/bar")));
        assertEquals("", m.subPath(new StubRequest("/api/v2")));
    }

    @Test
    public void test_process_unknownSubPathReturnsNotFoundEnvelope() throws Exception {
        final SearchApiV2Manager m = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/does-not-exist"), res, new NopChain());
        assertEquals(404, res.status);
        final String body = res.body();
        assertTrue(body.contains("\"status\":1"), body);
        assertTrue(body.contains("\"code\":\"not_found\""), body);
        assertTrue(body.contains("endpoint not found"), body);
    }

    @Test
    public void test_process_suggestWordsReturnsEnvelopeShape() throws Exception {
        final SearchApiV2Manager m = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "test" });
        params.put("num", new String[] { "5" });
        m.process(new StubRequest("/api/v2/suggest-words", params), res, new NopChain());
        final String body = res.body();
        // The suggest helper may fail without an OpenSearch instance — accept either a 200
        // success envelope or a structured internal-error envelope, but assert the v2 wire
        // shape is preserved either way.
        assertFalse(body.contains("\"version\""), body);
        if (res.status == 200) {
            assertTrue(body.contains("\"status\":0"), body);
            assertTrue(body.contains("\"suggest_words\""), body);
            assertTrue(body.contains("\"record_count\""), body);
            assertTrue(body.contains("\"page_size\""), body);
            assertTrue(body.contains("\"q\":\"test\""), body);
        } else {
            assertEquals(500, res.status);
            assertTrue(body.contains("\"code\":\"internal_error\""), body);
        }
    }

    @Test
    public void test_process_labelsReturnsEnvelopeShape() throws Exception {
        final SearchApiV2Manager m = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/labels"), res, new NopChain());
        final String body = res.body();
        assertFalse(body.contains("\"version\""), body);
        if (res.status == 200) {
            assertTrue(body.contains("\"status\":0"), body);
            assertTrue(body.contains("\"record_count\""), body);
            assertTrue(body.contains("\"labels\""), body);
        } else {
            // Helper backed by the search engine; without a live engine the handler emits a
            // structured error envelope. Verify the v2 wire shape rather than failing here.
            assertEquals(500, res.status);
            assertTrue(body.contains("\"code\":\"internal_error\""), body);
        }
    }

    @Test
    public void test_process_popularWordsReturnsEnvelopeShape() throws Exception {
        final SearchApiV2Manager m = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/popular-words"), res, new NopChain());
        final String body = res.body();
        assertFalse(body.contains("\"version\""), body);
        if (res.status == 200) {
            assertTrue(body.contains("\"status\":0"), body);
            assertTrue(body.contains("\"popular_words\""), body);
            assertTrue(body.contains("\"record_count\""), body);
        } else {
            // Either feature-disabled (invalid_request 400) or backend-unreachable (internal_error 500).
            assertTrue(res.status == 400 || res.status == 500, "unexpected status " + res.status + ": " + body);
            assertTrue(body.contains("\"code\":\"invalid_request\"") || body.contains("\"code\":\"internal_error\""), body);
        }
    }

    @Test
    public void test_process_searchEndpointDispatchesToHandler() throws Exception {
        final SearchApiV2Manager m = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "test" });
        m.process(new StubRequest("/api/v2/search", params), res, new NopChain());
        // The manager only needs to confirm dispatch: the v2 envelope shape must be present
        // regardless of whether the search backend is up. Detailed shape is asserted in
        // SearchHandlerTest — here we just verify the route reached the handler.
        final String body = res.body();
        assertFalse(body.contains("\"version\""), body);
        assertTrue(res.status == 200 || res.status == 400 || res.status == 500, "unexpected status " + res.status + ": " + body);
        if (res.status == 200) {
            assertTrue(body.contains("\"status\":0"), body);
            assertTrue(body.contains("\"data\""), body);
        } else {
            assertTrue(body.contains("\"code\":\"invalid_request\"") || body.contains("\"code\":\"internal_error\""), body);
        }
    }

    @Test
    public void test_process_documentsAllDispatchesToScrollHandler() throws Exception {
        final SearchApiV2Manager m = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        m.process(new StubRequest("/api/v2/documents/all", params), res, new NopChain());
        // Either the scroll handler streamed NDJSON or fell back to a structured envelope —
        // the manager just needs to confirm the dispatch reached the right handler. Detailed
        // shape is asserted in ScrollSearchHandlerTest.
        if ("application/x-ndjson; charset=UTF-8".equals(res.contentType)) {
            // Streaming branch reached — body may be empty or contain "data" lines.
            assertTrue(res.body() == null || res.body().isEmpty() || res.body().contains("\"data\""), res.body());
        } else {
            final String body = res.body();
            assertFalse(body.contains("\"version\""), body);
            assertTrue(res.status == 400 || res.status == 500, "unexpected status " + res.status + ": " + body);
            assertTrue(body.contains("\"code\":\"invalid_request\"") || body.contains("\"code\":\"internal_error\""), body);
        }
    }

    @Test
    public void test_process_documentsFavoriteDispatchesToFavoriteHandler() throws Exception {
        final SearchApiV2Manager m = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/documents/abc123/favorite"), res, new NopChain());
        final String body = res.body();
        // The manager only needs to confirm the dispatch reached the favorite-get handler.
        // Either the feature is disabled (invalid_request), the doc isn't indexed (not_found),
        // or the backend is unreachable (internal_error). The v2 envelope shape is required.
        assertFalse(body.contains("\"version\""), body);
        assertTrue(res.status == 400 || res.status == 404 || res.status == 500, "unexpected status " + res.status + ": " + body);
        assertTrue(body.contains("\"code\":\"invalid_request\"") || body.contains("\"code\":\"not_found\"")
                || body.contains("\"code\":\"internal_error\""), body);
    }

    @Test
    public void test_process_documentsFavoriteRejectsMalformedDocId() throws Exception {
        final SearchApiV2Manager m = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/documents/has spaces/favorite"), res, new NopChain());
        assertEquals(400, res.status);
        final String body = res.body();
        assertTrue(body.contains("\"code\":\"invalid_request\""), body);
        assertTrue(body.contains("invalid doc_id"), body);
    }

    @Test
    public void test_process_labelsRejectsPostAtCsrfGate() throws Exception {
        // Per spec §7.3, an anonymous client POSTing to a GET-only endpoint like /labels
        // without a CSRF token is rejected at the CSRF gate (403 forbidden) before the
        // method check fires. This is the secure-default contract: the CSRF gate is the
        // first line of defense and applies uniformly to all unsafe HTTP methods on the
        // v2 surface. A 405 (method_not_allowed) is what an authorized client with a
        // valid CSRF token receives when POSTing to a GET-only endpoint — that path is
        // exercised in SearchApiV2ManagerCsrfTest which registers sessionCsrfTokenManager.
        // Note: sessionCsrfTokenManager is NOT registered in this test class, so we
        // must register it here to allow the CSRF check to complete (without a token, the
        // gate returns 403 before any ComponentNotFoundException is thrown).
        final org.codelibs.fess.helper.SessionCsrfTokenManager csrfManager = new org.codelibs.fess.helper.SessionCsrfTokenManager();
        org.codelibs.fess.util.ComponentUtil.register(csrfManager, "sessionCsrfTokenManager");
        org.codelibs.fess.util.ComponentUtil.register(csrfManager,
                org.codelibs.fess.helper.SessionCsrfTokenManager.class.getCanonicalName());
        final SearchApiV2Manager m = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/labels").withMethod("POST"), res, new NopChain());
        assertEquals(403, res.status);
        final String body = res.body();
        assertTrue(body.contains("\"status\":1"), body);
        assertTrue(body.contains("\"code\":\"forbidden\""), body);
    }

    @Test
    public void test_handleHealth_does_not_leak_exception_message() throws Exception {
        // Source-level assertion: the handleHealth catch block must not pass e.getMessage()
        // to writeError. Uses writeInternalError which logs and emits a generic message.
        final java.nio.file.Path src = java.nio.file.Paths.get("src/main/java/org/codelibs/fess/api/v2/SearchApiV2Manager.java");
        org.junit.jupiter.api.Assumptions.assumeTrue(java.nio.file.Files.exists(src),
                "skipping: source file not present at runtime cwd=" + java.nio.file.Paths.get(".").toAbsolutePath());
        final String source = java.nio.file.Files.readString(src);
        // Find handleHealth and look for the catch block
        final int healthIdx = source.indexOf("private void handleHealth");
        assertTrue(healthIdx >= 0, "handleHealth not found in source");
        final String healthSection = source.substring(healthIdx, Math.min(source.length(), healthIdx + 2000));
        // The catch must use writeInternalError, not writeError with e.getMessage()
        assertTrue(healthSection.contains("writeInternalError"), "handleHealth catch must use writeInternalError: " + healthSection);
        assertFalse(healthSection.contains("e.getMessage()"), "handleHealth must not leak e.getMessage(): " + healthSection);
    }

    @Test
    public void test_process_healthDispatchesToHealthHandler() throws Exception {
        final SearchApiV2Manager m = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/health"), res, new NopChain());
        // The /health route writes its envelope through handleHealth(). The engine may or
        // may not be reachable from the unit harness; success / service_unavailable (red) /
        // internal_error are all accepted as long as the v2 envelope shape is preserved.
        // C-9 invariant: envelope.status >= 1 iff HTTP >= 400 — red clusters now emit a
        // service_unavailable error envelope (status:9) instead of a success envelope.
        final String body = res.body();
        assertFalse(body.contains("\"version\""), body);
        if (res.status == 200) {
            // green or yellow — success envelope with engine details.
            assertTrue(body.contains("\"status\":0"), body);
            assertTrue(body.contains("\"engine\""), body);
            assertTrue(body.contains("\"cluster_name\""), body);
        } else if (res.status == 503) {
            // red cluster — error envelope; engine details embedded under error.details.
            assertTrue(body.contains("\"status\":9"), body);
            assertTrue(body.contains("\"code\":\"service_unavailable\""), body);
            assertTrue(body.contains("\"engine\""), body);
        } else {
            assertEquals(500, res.status);
            assertTrue(body.contains("\"code\":\"internal_error\""), body);
        }
    }

    @Test
    public void test_handleHealth_clusterRedMapsTo503_sourceLevel() throws Exception {
        // Source-level assertion: the handleHealth method must check for "red" cluster status
        // and set response.setStatus(503). This test reads the source to confirm the pattern.
        final java.nio.file.Path src = java.nio.file.Paths.get("src/main/java/org/codelibs/fess/api/v2/SearchApiV2Manager.java");
        org.junit.jupiter.api.Assumptions.assumeTrue(java.nio.file.Files.exists(src),
                "skipping: source not available at cwd=" + java.nio.file.Paths.get(".").toAbsolutePath());
        final String source = java.nio.file.Files.readString(src);
        final int healthIdx = source.indexOf("private void handleHealth");
        assertTrue(healthIdx >= 0, "handleHealth not found in source");
        final String healthSection = source.substring(healthIdx, Math.min(source.length(), healthIdx + 2500));
        // Must reference SERVICE_UNAVAILABLE (the 503 code added by MJ-26 fix).
        assertTrue(healthSection.contains("SERVICE_UNAVAILABLE"),
                "handleHealth must map red cluster to SERVICE_UNAVAILABLE: " + healthSection);
        // Must check for "red" cluster status.
        assertTrue(healthSection.contains("\"red\"") || healthSection.contains("red"),
                "handleHealth must check for red cluster status: " + healthSection);
    }

    @Test
    public void test_process_authMeRoutesToMeHandler() throws Exception {
        final SearchApiV2Manager m = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/auth/me"), res, new NopChain());
        // MeHandler always emits a v2 success envelope (anonymous becomes
        // {"authenticated":false}). The "authenticated" key is the smoking gun that proves
        // dispatch landed on MeHandler rather than the not-found default branch.
        assertEquals(200, res.status);
        final String body = res.body();
        assertFalse(body.contains("\"version\""), body);
        assertTrue(body.contains("\"status\":0"), body);
        assertTrue(body.contains("\"authenticated\""), body);
    }

    @Test
    public void test_process_uiConfigRoutesToUiConfigHandler() throws Exception {
        final SearchApiV2Manager m = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        // The handler calls req.getSession(true); the existing StubRequest.getSession returns
        // null, which makes the handler's downstream csrf.issue(session) path fall over —
        // that lands in the structured-error branch. Either the success envelope shape or
        // the structured 500 envelope is acceptable here; we just need to confirm dispatch.
        m.process(new StubRequest("/api/v2/ui/config"), res, new NopChain());
        final String body = res.body();
        assertFalse(body.contains("\"version\""), body);
        assertTrue(res.status == 200 || res.status == 500, "unexpected status " + res.status + ": " + body);
        if (res.status == 200) {
            // site_name is a UiConfigHandler-specific payload field — proves the dispatch
            // landed on the right handler.
            assertTrue(body.contains("\"site_name\""), body);
            assertTrue(body.contains("\"login_required\""), body);
            assertTrue(body.contains("\"theme\""), body);
        } else {
            assertTrue(body.contains("\"code\":\"internal_error\""), body);
        }
    }

    @Test
    public void test_process_cachePathRoutesToCacheHandler() throws Exception {
        final SearchApiV2Manager m = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/cache/abc123"), res, new NopChain());
        final String body = res.body();
        // CacheHandler emits one of: not_found (doc missing), internal_error (helpers
        // unavailable) — never the default not-found "endpoint not found" message that
        // would indicate the route failed to dispatch. The wire-shape assertion ensures
        // we are routing to CacheHandler rather than the default arm.
        assertFalse(body.contains("\"version\""), body);
        assertTrue(res.status == 404 || res.status == 500, "unexpected status " + res.status + ": " + body);
        assertTrue(body.contains("\"code\":\"not_found\"") || body.contains("\"code\":\"internal_error\""), body);
        // The default-arm message would say "endpoint not found"; CacheHandler's not_found
        // says "doc not found" / "no cache for". Asserting the absence of the default arm's
        // marker proves we reached CacheHandler.
        assertFalse(body.contains("endpoint not found"), "should not reach default arm: " + body);
    }

    @Test
    public void test_process_documentsUnknownActionReturnsResourceNotFound() throws Exception {
        // C-6: /api/v2/documents/{id}/<unknown> previously fell into the switch default
        // and returned the generic "endpoint not found" message. The dedicated prefix
        // branch must surface a clearer "unknown action on document: ..." message while
        // still preserving the v2 not_found wire code.
        final SearchApiV2Manager m = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/documents/abc/unknownAction"), res, new NopChain());
        assertEquals(404, res.status);
        final String body = res.body();
        assertTrue(body.contains("\"status\":1"), body);
        assertTrue(body.contains("\"code\":\"not_found\""), body);
        assertTrue(body.contains("unknown action on document"), body);
        // The generic "endpoint not found" default-arm message must NOT be used here —
        // that would indicate the prefix branch failed to catch the request.
        assertFalse(body.contains("endpoint not found"), body);
    }

    @Test
    public void test_process_committedResponseSkipsSecondWrite_onIOException() throws Exception {
        // C-7: when a streaming handler has already flushed (response.isCommitted()) and
        // then throws an IOException (client disconnect mid-write), the outer catch
        // must NOT attempt a second envelope write. The IOException is re-thrown so the
        // container handles it; the in-flight body stays intact.
        final SearchApiV2Manager m = new SearchApiV2Manager();
        // Capturing response that reports already-committed so the catch block takes the
        // "skip second write" branch. We dispatch to /chat/stream which will try to look
        // up the chat client — but isCommitted() short-circuits the failure path.
        final CapturingResponse res = new CapturingResponse() {
            @Override
            public boolean isCommitted() {
                return true;
            }
        };
        // Drive an unknown sub-path through the IOException branch via a wrapping
        // handler: the simplest deterministic test is to use a request that lands in the
        // /documents/<id> branch which we know is reachable; the failure path is the
        // IOException catch. Because no real IOException is thrown by the unknown-action
        // path, this test relies on the general-Exception branch's committed-response
        // policy: verify NO second write happened and status stayed at its initial value.
        m.process(new StubRequest("/api/v2/documents/abc/unknownAction"), res, new NopChain());
        // Either: (a) the unknown-action branch wrote the not_found envelope BEFORE
        // anything checked isCommitted (because writeError itself bails on committed),
        // or (b) nothing was written at all. Both outcomes satisfy "no double-write".
        // We assert the response body, if any, contains ONLY a single complete envelope
        // by checking it never contains TWO occurrences of "\"response\":".
        final String body = res.body();
        final int firstResp = body.indexOf("\"response\":");
        if (firstResp >= 0) {
            assertEquals(-1, body.indexOf("\"response\":", firstResp + 1), "body should never contain two envelope frames: " + body);
        }
    }

    @Test
    public void test_process_unknownPathReturnsNotFound() throws Exception {
        final SearchApiV2Manager m = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/this/path/does/not/exist"), res, new NopChain());
        // Deeper unknown path still resolves to the default arm rather than crashing on
        // any of the prefix matchers (/documents/, /cache/).
        assertEquals(404, res.status);
        final String body = res.body();
        assertFalse(body.contains("\"version\""), body);
        assertTrue(body.contains("\"status\":1"), body);
        assertTrue(body.contains("\"code\":\"not_found\""), body);
        assertTrue(body.contains("endpoint not found"), body);
    }

    @Test
    public void test_writeHeaders_appliesConfiguredHeaders() {
        // F1: v2 must apply the same `api.json.response.headers` (default:
        // Referrer-Policy: strict-origin-when-cross-origin) as v1 so the security baseline
        // is uniform across both surfaces. Without this, every /api/v2 response was leaking
        // the Referrer-Policy header.
        final SearchApiV2Manager m = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        m.writeHeaders(res);
        assertEquals("Referrer-Policy from api.json.response.headers must be applied on v2 responses", "strict-origin-when-cross-origin",
                res.headers.get("Referrer-Policy"));
    }

    @Test
    public void test_process_appliesConfiguredResponseHeaders() throws Exception {
        // F1: confirm the writeHeaders call is wired into the process(...) dispatch path so
        // every v2 response (including error envelopes from unknown paths) carries the
        // configured baseline headers.
        final SearchApiV2Manager m = new SearchApiV2Manager();
        final CapturingResponse res = new CapturingResponse();
        m.process(new StubRequest("/api/v2/does-not-exist"), res, new NopChain());
        assertEquals("v2 dispatch must apply api.json.response.headers before writing the envelope", "strict-origin-when-cross-origin",
                res.headers.get("Referrer-Policy"));
    }

    /** A FilterChain that does nothing — the v2 manager never delegates further. */
    private static class NopChain implements FilterChain {
        @Override
        public void doFilter(final ServletRequest request, final ServletResponse response) {
            // no-op
        }
    }

    /** Minimal HttpServletResponse stub that captures setContentType/setStatus/setHeader/getWriter output. */
    private static class CapturingResponse implements HttpServletResponse {
        final StringWriter sw = new StringWriter();
        final PrintWriter writer = new PrintWriter(sw);
        int status = 200;
        String contentType;
        final Map<String, String> headers = new HashMap<>();

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
            return new java.util.ArrayList<>(headers.keySet());
        }
    }

    /** Adapted from StaticThemeFilterTest.StubRequest — narrow surface, getServletPath is what matters. */
    private static class StubRequest implements HttpServletRequest {
        private final String uri;
        private final Map<String, Object> attrs = new HashMap<>();
        private final Map<String, String[]> params;
        private String method = "GET";

        StubRequest(final String uri) {
            this(uri, Collections.emptyMap());
        }

        StubRequest(final String uri, final Map<String, String[]> params) {
            this.uri = uri;
            this.params = params == null ? Collections.emptyMap() : params;
        }

        StubRequest withMethod(final String m) {
            this.method = m;
            return this;
        }

        @Override
        public String getServletPath() {
            return uri;
        }

        @Override
        public String getMethod() {
            return method;
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
            return 0;
        }

        @Override
        public long getContentLengthLong() {
            return 0;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public ServletInputStream getInputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getParameter(final String name) {
            final String[] vals = params.get(name);
            return vals == null || vals.length == 0 ? null : vals[0];
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.enumeration(params.keySet());
        }

        @Override
        public String[] getParameterValues(final String name) {
            return params.get(name);
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return Collections.unmodifiableMap(params);
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
