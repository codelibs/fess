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
package org.codelibs.fess.servlet;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codelibs.core.misc.Tuple3;
import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.theme.StaticThemeResponder;
import org.codelibs.fess.theme.Theme;
import org.codelibs.fess.theme.ThemeRegistry;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.utflute.mocklet.MockletHttpServletRequest;
import org.junit.jupiter.api.Test;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;

/**
 * Tests for {@link ErrorPageServlet}. Drives {@code service()} directly with stub request,
 * response, registry and responder implementations, so no DI container is needed (mirrors
 * {@code StaticThemeFilterTest}'s stubbing approach). {@code resolveVirtualHostKey} is tested
 * separately below, against a real {@code FessConfig} and mocklet request.
 */
public class ErrorPageServletTest extends UnitFessTestCase {

    private static final Theme THEME = new Theme("t", Paths.get("/tmp/t"), null);

    @Test
    public void test_service_directRequestIsNotFound() throws Exception {
        final ErrorPageServlet servlet = new ErrorPageServlet();
        final StubRequest req = new StubRequest(DispatcherType.REQUEST);
        final StubResponse res = new StubResponse();
        servlet.service(req, res);
        assertEquals(404, res.status, "a direct (non-error, non-forward) request must be answered 404");
        assertEquals("text/plain; charset=UTF-8", res.contentType);
        assertEquals("Not Found.", res.bodyAsString());
    }

    @Test
    public void test_service_apiUriGetsPlainText() throws Exception {
        final ErrorPageServlet servlet = new ErrorPageServlet();
        final StubRequest req = new StubRequest(DispatcherType.ERROR);
        req.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 401);
        req.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "/api/v2/search");
        req.header = "text/html";
        final StubResponse res = new StubResponse();
        servlet.service(req, res);
        assertEquals(401, res.status);
        assertEquals("text/plain; charset=UTF-8", res.contentType);
        assertEquals("Bad Authentication.", res.bodyAsString());
        assertEquals("no-store", res.headers.get("Cache-Control"));
        assertEquals("nosniff", res.headers.get("X-Content-Type-Options"));
        assertEquals(String.valueOf("Bad Authentication.".getBytes(StandardCharsets.UTF_8).length), res.headers.get("Content-Length"));
    }

    @Test
    public void test_service_acceptWithoutHtmlGetsPlainText() throws Exception {
        final ErrorPageServlet servlet = new ErrorPageServlet();
        final StubRequest req = new StubRequest(DispatcherType.ERROR);
        req.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 404);
        req.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "/foo");
        req.header = "application/json";
        final StubResponse res = new StubResponse();
        servlet.service(req, res);
        assertEquals(404, res.status);
        assertEquals("text/plain; charset=UTF-8", res.contentType);
        assertEquals("Not Found.", res.bodyAsString());
    }

    @Test
    public void test_service_browserGetsThemeErrorPage() throws Exception {
        // A virtual host configured against a header this request carries, so resolveTheme must
        // hand the *matched* key to the registry -- not silently fall back to "" the way reading
        // the current request through LaRequestUtil would, since that binding is unset on this
        // ERROR dispatch.
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Tuple3<String, String, String>[] getVirtualHosts() {
                return new Tuple3[] { new Tuple3<>("Host", "vh1.example.com", "vh1") };
            }
        });
        final ErrorPageServlet servlet = new ErrorPageServlet();
        final StubRegistry registry = new StubRegistry(THEME);
        servlet.setThemeRegistry(registry);
        final StubResponder responder = new StubResponder(true);
        servlet.setStaticThemeResponder(responder);
        final StubRequest req = new StubRequest(DispatcherType.ERROR);
        req.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 404);
        req.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "/search");
        req.header = "text/html";
        req.addHeader("Host", "vh1.example.com");
        final StubResponse res = new StubResponse();
        servlet.service(req, res);
        assertTrue(responder.called, "the responder must be asked to render the theme error page");
        assertSame(THEME, responder.lastTheme);
        assertEquals(404, responder.lastHttpStatus);
        assertEquals(404, responder.lastDisplayCode);
        assertNull(responder.lastDetailKey, "no detail key was supplied");
        // Tomcat's HttpHeaderSecurityFilter (which normally adds this) is REQUEST-dispatch only
        // and never runs on this ERROR/FORWARD-dispatch response, so the servlet must set it.
        assertEquals("nosniff", res.headers.get("X-Content-Type-Options"));
        // resolveTheme must hand the request's own virtual-host key to the registry, matched
        // from the request's own headers rather than defaulting to "".
        assertEquals("the virtual host key from the request must reach the registry", "vh1", registry.lastHostKey);
    }

    @Test
    public void test_service_unauthorizedIsShownAsForbidden() throws Exception {
        final ErrorPageServlet servlet = new ErrorPageServlet();
        servlet.setThemeRegistry(new StubRegistry(THEME));
        final StubResponder responder = new StubResponder(true);
        servlet.setStaticThemeResponder(responder);
        final StubRequest req = new StubRequest(DispatcherType.ERROR);
        req.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 401);
        req.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "/search");
        req.header = "text/html";
        final StubResponse res = new StubResponse();
        servlet.service(req, res);
        assertTrue(responder.called);
        assertEquals(401, responder.lastHttpStatus, "the real HTTP status stays 401");
        assertEquals(403, responder.lastDisplayCode, "401 is displayed as 403");
    }

    @Test
    public void test_service_passesTheDetailKeyFromTheRequestAttribute() throws Exception {
        final ErrorPageServlet servlet = new ErrorPageServlet();
        servlet.setThemeRegistry(new StubRegistry(THEME));
        final StubResponder responder = new StubResponder(true);
        servlet.setStaticThemeResponder(responder);
        final StubRequest req = new StubRequest(DispatcherType.ERROR);
        req.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 404);
        req.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "/search");
        req.setAttribute(Constants.ERROR_DETAIL_KEY, "errors.docid_not_found");
        req.header = "text/html";
        final StubResponse res = new StubResponse();
        servlet.service(req, res);
        assertEquals("errors.docid_not_found", responder.lastDetailKey);
    }

    @Test
    public void test_service_dropsAnUnsafeDetailKey() throws Exception {
        final ErrorPageServlet servlet = new ErrorPageServlet();
        servlet.setThemeRegistry(new StubRegistry(THEME));
        final StubResponder responder = new StubResponder(true);
        servlet.setStaticThemeResponder(responder);
        final StubRequest req = new StubRequest(DispatcherType.ERROR);
        req.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 404);
        req.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "/search");
        req.setAttribute(Constants.ERROR_DETAIL_KEY, "not safe; <script>");
        req.header = "text/html";
        final StubResponse res = new StubResponse();
        servlet.service(req, res);
        assertTrue(responder.called, "the responder must still be reached -- an unsafe key is dropped, not treated as no theme");
        assertNull(responder.lastDetailKey, "an unsafe detail key must be dropped, not forwarded");
    }

    @Test
    public void test_service_browserUnauthorizedDefaultsDetailKeyToBadAuthentication() throws Exception {
        // Task 5 (final review): the removed redirect.jsp sent browsers
        // message_key=errors.bad_authentication for a 401, and every i18n bundle still
        // translates it (error.detail_bad_authentication). Nothing set Constants.ERROR_DETAIL_KEY
        // for this case (e.g. a login-challenge failure), so the servlet must default it itself.
        final ErrorPageServlet servlet = new ErrorPageServlet();
        servlet.setThemeRegistry(new StubRegistry(THEME));
        final StubResponder responder = new StubResponder(true);
        servlet.setStaticThemeResponder(responder);
        final StubRequest req = new StubRequest(DispatcherType.ERROR);
        req.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 401);
        req.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, "/search");
        req.header = "text/html";
        final StubResponse res = new StubResponse();
        servlet.service(req, res);
        assertTrue(responder.called);
        assertEquals("errors.bad_authentication", responder.lastDetailKey);
    }

    @Test
    public void test_service_forwardDispatchIsFiveHundred() throws Exception {
        final ErrorPageServlet servlet = new ErrorPageServlet();
        servlet.setThemeRegistry(new StubRegistry(THEME));
        final StubResponder responder = new StubResponder(true);
        servlet.setStaticThemeResponder(responder);
        final StubRequest req = new StubRequest(DispatcherType.FORWARD);
        req.header = "text/html";
        final StubResponse res = new StubResponse();
        servlet.service(req, res);
        assertTrue(responder.called);
        assertEquals(500, responder.lastHttpStatus, "a FORWARD dispatch with no error attributes means the action failed to render");
        assertEquals(500, responder.lastDisplayCode);
    }

    @Test
    public void test_service_forwardDispatchHonoursAnExistingResponseStatus() throws Exception {
        // The show-errors forward target (wired in the next task) reaches this servlet on a
        // FORWARD dispatch with no jakarta.servlet.error.* attributes at all; the only way it can
        // report anything but 500 is by reading a status the action already set on the response
        // (e.g. a validation failure that called response.setStatus(400) before forwarding here).
        final ErrorPageServlet servlet = new ErrorPageServlet();
        servlet.setThemeRegistry(new StubRegistry(THEME));
        final StubResponder responder = new StubResponder(true);
        servlet.setStaticThemeResponder(responder);
        final StubRequest req = new StubRequest(DispatcherType.FORWARD);
        req.header = "text/html";
        final StubResponse res = new StubResponse();
        res.status = 400;
        servlet.service(req, res);
        assertTrue(responder.called);
        assertEquals(400, responder.lastHttpStatus, "a status already set on the response must be honoured, not overridden to 500");
        assertEquals(400, responder.lastDisplayCode);
    }

    @Test
    public void test_service_missingStatusAttributeIsFiveHundred() throws Exception {
        final ErrorPageServlet servlet = new ErrorPageServlet();
        servlet.setThemeRegistry(new StubRegistry(THEME));
        final StubResponder responder = new StubResponder(true);
        servlet.setStaticThemeResponder(responder);
        final StubRequest req = new StubRequest(DispatcherType.ERROR);
        // No ERROR_STATUS_CODE attribute set at all.
        req.header = "text/html";
        final StubResponse res = new StubResponse();
        servlet.service(req, res);
        assertEquals(500, responder.lastHttpStatus);
        assertEquals(500, responder.lastDisplayCode);
    }

    @Test
    public void test_service_fallsBackToMinimalHtmlWhenNoThemeResolves() throws Exception {
        final ErrorPageServlet servlet = new ErrorPageServlet();
        servlet.setThemeRegistry(new StubRegistry(null));
        final StubResponder responder = new StubResponder(true);
        servlet.setStaticThemeResponder(responder);
        final StubRequest req = new StubRequest(DispatcherType.ERROR);
        req.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 404);
        req.header = "text/html";
        final StubResponse res = new StubResponse();
        servlet.service(req, res);
        assertFalse(responder.called, "the responder must not be consulted when no theme resolved");
        assertEquals(404, res.status);
        assertEquals("text/html; charset=UTF-8", res.contentType);
        assertEquals("404", res.headers.get("X-Fess-Error-Code"));
        assertTrue(res.bodyAsString().contains("404"));
    }

    @Test
    public void test_service_fallsBackToMinimalHtmlWhenTheThemeCannotAnswer() throws Exception {
        final ErrorPageServlet servlet = new ErrorPageServlet();
        servlet.setThemeRegistry(new StubRegistry(THEME));
        final StubResponder responder = new StubResponder(false);
        servlet.setStaticThemeResponder(responder);
        final StubRequest req = new StubRequest(DispatcherType.ERROR);
        req.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 500);
        req.header = "text/html";
        final StubResponse res = new StubResponse();
        servlet.service(req, res);
        assertTrue(responder.called);
        assertEquals(500, res.status);
        assertEquals("text/html; charset=UTF-8", res.contentType);
        assertTrue(res.bodyAsString().contains("System Error."));
        // The failed theme attempt may have set its own headers before returning false; the
        // response must be reset before the fallback page writes its own.
        assertTrue(res.resetCalled, "the response must be reset before the minimal-HTML fallback is written");
    }

    @Test
    public void test_service_fallsBackToMinimalHtmlWhenTheRegistryThrows() throws Exception {
        final ErrorPageServlet servlet = new ErrorPageServlet();
        servlet.setThemeRegistry(new ThrowingRegistry());
        final StubResponder responder = new StubResponder(true);
        servlet.setStaticThemeResponder(responder);
        final StubRequest req = new StubRequest(DispatcherType.ERROR);
        req.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 503);
        req.header = "text/html";
        final StubResponse res = new StubResponse();
        servlet.service(req, res);
        assertFalse(responder.called, "the responder must not be reached when the registry itself throws");
        assertEquals(503, res.status);
        assertEquals("text/html; charset=UTF-8", res.contentType);
        assertTrue(res.bodyAsString().contains("Service Unavailable."));
    }

    @Test
    public void test_service_committedResponseGetsNothingAppended() throws Exception {
        // Simulates the theme attempt having already flushed part of the body (and its own
        // headers) to the client before failing: isCommitted() reports true from that point on,
        // exactly as a real container response would, so nothing further may be written or reset.
        final ErrorPageServlet servlet = new ErrorPageServlet();
        servlet.setThemeRegistry(new StubRegistry(THEME));
        final StubResponder responder = new StubResponder(true) {
            @Override
            public boolean serveErrorPage(final HttpServletRequest req, final HttpServletResponse res, final Theme theme,
                    final int httpStatus, final int displayCode, final String detailKey) {
                super.serveErrorPage(req, res, theme, httpStatus, displayCode, detailKey);
                throw new IllegalStateException("partial theme write failed after headers were already sent");
            }
        };
        servlet.setStaticThemeResponder(responder);
        final StubRequest req = new StubRequest(DispatcherType.ERROR);
        req.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 500);
        req.header = "text/html";
        final StubResponse res = new StubResponse();
        res.committed = true;
        servlet.service(req, res);
        assertTrue(responder.called, "the responder must have been reached before it failed");
        assertFalse(res.resetCalled, "a committed response must never be reset (a real container throws from reset() otherwise)");
        assertEquals(0, res.bodyAsString().length(), "nothing may be appended once the response is committed");
    }

    @Test
    public void test_service_minimalHtmlCarriesNoScriptAndLocksTheCsp() throws Exception {
        final ErrorPageServlet servlet = new ErrorPageServlet();
        servlet.setThemeRegistry(new StubRegistry(null));
        servlet.setStaticThemeResponder(new StubResponder(true));
        final StubRequest req = new StubRequest(DispatcherType.ERROR);
        // 401, not 404: also pins that the page text is paired with the *displayed* code (403
        // Forbidden.), not the real status's meaning (401 Bad Authentication.) -- a status where
        // status == displayCode could not tell the two apart.
        req.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 401);
        req.header = "text/html";
        final StubResponse res = new StubResponse();
        servlet.service(req, res);
        assertEquals(401, res.status, "the real status is used, not the displayed one");
        assertEquals("text/html; charset=UTF-8", res.contentType);
        assertEquals("default-src 'none'", res.headers.get("Content-Security-Policy"));
        assertEquals("nosniff", res.headers.get("X-Content-Type-Options"));
        assertEquals("no-store", res.headers.get("Cache-Control"));
        assertEquals("401 is displayed (and headered) as 403, same as the themed page", "403", res.headers.get("X-Fess-Error-Code"));
        // Exact body, not a substring check, so the whole fallback template is pinned.
        final String expectedHtml = "<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n<meta charset=\"utf-8\">\n<title>403 Forbidden."
                + "</title>\n</head>\n<body>\n<h1>403 Forbidden.</h1>\n</body>\n</html>\n";
        assertEquals(expectedHtml, res.bodyAsString());
        assertFalse(res.bodyAsString().contains("<script"), "the fallback page must carry no script");
        assertFalse(res.bodyAsString().toLowerCase(java.util.Locale.ROOT).contains("src="),
                "no element may reference an external resource");
        assertFalse(res.bodyAsString().toLowerCase(java.util.Locale.ROOT).contains("href="),
                "no element may reference an external resource");
    }

    @Test
    public void test_displayCode() {
        assertEquals(400, ErrorPageServlet.displayCode(400));
        assertEquals(403, ErrorPageServlet.displayCode(403));
        assertEquals(404, ErrorPageServlet.displayCode(404));
        assertEquals(429, ErrorPageServlet.displayCode(429));
        assertEquals(500, ErrorPageServlet.displayCode(500));
        assertEquals(503, ErrorPageServlet.displayCode(503));
        assertEquals(403, ErrorPageServlet.displayCode(401), "401 is shown as 403");
        assertEquals(400, ErrorPageServlet.displayCode(418), "an unmapped 4xx folds to 400");
        assertEquals(500, ErrorPageServlet.displayCode(502), "an unmapped 5xx folds to 500");
    }

    @Test
    public void test_reasonText() {
        assertEquals("Bad Authentication.", ErrorPageServlet.reasonText(401));
    }

    @Test
    public void test_resolveDetailKey() {
        final StubRequest req = new StubRequest(DispatcherType.ERROR);
        // No attribute set: 401 defaults to errors.bad_authentication; anything else gets null.
        assertEquals("errors.bad_authentication", ErrorPageServlet.resolveDetailKey(req, 401));
        assertNull(ErrorPageServlet.resolveDetailKey(req, 404));
        // An attribute set by an action wins over the 401 default.
        req.setAttribute(Constants.ERROR_DETAIL_KEY, "errors.docid_not_found");
        assertEquals("errors.docid_not_found", ErrorPageServlet.resolveDetailKey(req, 401));
        // An unsafe attribute is dropped, falling back to the 401 default rather than the raw value.
        req.setAttribute(Constants.ERROR_DETAIL_KEY, "not safe; <script>");
        assertEquals("errors.bad_authentication", ErrorPageServlet.resolveDetailKey(req, 401));
        assertNull(ErrorPageServlet.resolveDetailKey(req, 404));
    }

    // ===== resolveVirtualHostKey =====
    //
    // Moved here from VirtualHostHelperTest: this behaviour lives on ErrorPageServlet now, not
    // on VirtualHostHelper -- see ErrorPageServlet.resolveVirtualHostKey's javadoc for why.

    @Test
    public void test_resolveVirtualHostKey_fromRequest() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Tuple3<String, String, String>[] getVirtualHosts() {
                return new Tuple3[] { new Tuple3<>("Host", "fess.example.com", "site1") };
            }
        });

        final MockletHttpServletRequest request = getMockRequest();
        request.addHeader("Host", "fess.example.com");

        assertEquals("site1", ErrorPageServlet.resolveVirtualHostKey(request));
        assertEquals("site1", request.getAttribute(FessConfig.VIRTUAL_HOST_VALUE));
    }

    @Test
    public void test_resolveVirtualHostKey_cached() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Tuple3<String, String, String>[] getVirtualHosts() {
                return new Tuple3[] { new Tuple3<>("Host", "fess.example.com", "site1") };
            }
        });

        final MockletHttpServletRequest request = getMockRequest();
        request.setAttribute(FessConfig.VIRTUAL_HOST_VALUE, "cached_value");

        // No matching header is configured for this call, but the cached attribute wins.
        assertEquals("cached_value", ErrorPageServlet.resolveVirtualHostKey(request));
    }

    @Test
    public void test_resolveVirtualHostKey_noMatch() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Tuple3<String, String, String>[] getVirtualHosts() {
                return new Tuple3[] { new Tuple3<>("Host", "fess.example.com", "site1") };
            }
        });

        assertEquals("", ErrorPageServlet.resolveVirtualHostKey(getMockRequest()));
    }

    @Test
    public void test_resolveVirtualHostKey_nullRequest() {
        assertEquals("", ErrorPageServlet.resolveVirtualHostKey(null));
    }

    @Test
    public void test_resolveVirtualHostKey_caseInsensitiveHeaderValueMatching() {
        // Pins that matchVirtualHost() uses equalsIgnoreCase and does not regress to a
        // case-sensitive equals -- which would silently break virtual hosts for any request
        // whose Host header case differs from the configured value. Mutation-checked: reverting
        // to equals() here must fail this test.
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Tuple3<String, String, String>[] getVirtualHosts() {
                return new Tuple3[] { new Tuple3<>("Host", "Example.Com", "site1") };
            }
        });

        final MockletHttpServletRequest request = getMockRequest();
        request.addHeader("Host", "EXAMPLE.COM");

        assertEquals("site1", ErrorPageServlet.resolveVirtualHostKey(request));
    }

    @Test
    public void test_webXmlPointsEveryErrorPageAtThisServlet() throws Exception {
        final String webXml = Files.readString(Path.of("src/main/webapp/WEB-INF/web.xml"), StandardCharsets.UTF_8);

        final Matcher servletMatcher = Pattern.compile("<servlet>\\s*<servlet-name>([^<]+)</servlet-name>\\s*<servlet-class>"
                + Pattern.quote(ErrorPageServlet.class.getName()) + "</servlet-class>\\s*</servlet>").matcher(webXml);
        assertTrue(servletMatcher.find(), "a <servlet> element must declare " + ErrorPageServlet.class.getName());
        final String servletName = servletMatcher.group(1);

        // Anchored to <servlet-mapping> specifically: a <filter-mapping> with the same
        // url-pattern would satisfy a loose webXml.contains(...) check but would not route the
        // container's error dispatch to this servlet at all.
        final Matcher mappingMatcher = Pattern.compile("<servlet-mapping>\\s*<servlet-name>([^<]+)</servlet-name>\\s*<url-pattern>"
                + Pattern.quote(ErrorPageServlet.SERVLET_PATH) + "</url-pattern>\\s*</servlet-mapping>").matcher(webXml);
        assertTrue(mappingMatcher.find(),
                "the " + ErrorPageServlet.SERVLET_PATH + " url-pattern must be inside a <servlet-mapping>, not e.g. a <filter-mapping>");
        assertEquals("the <servlet-name> in <servlet> and <servlet-mapping> must agree, or the mapping silently binds nothing", servletName,
                mappingMatcher.group(1));

        final Matcher m =
                Pattern.compile("<error-page>\\s*<error-code>(\\d+)</error-code>\\s*<location>([^<]+)</location>").matcher(webXml);
        int found = 0;
        while (m.find()) {
            found++;
            assertEquals("error-code " + m.group(1), ErrorPageServlet.SERVLET_PATH, m.group(2));
        }
        assertEquals(7, found);
        assertFalse(webXml.contains("redirect.jsp"));
    }

    // ===== Stubs =====

    static class StubRegistry extends ThemeRegistry {
        private final Theme theme;
        /** Records the hostKey argument the servlet passed in, so a test can assert on it. */
        String lastHostKey;

        StubRegistry(final Theme theme) {
            this.theme = theme;
        }

        @Override
        public Optional<Theme> resolveActiveTheme(final String hostKey) {
            this.lastHostKey = hostKey;
            return Optional.ofNullable(theme);
        }
    }

    static class ThrowingRegistry extends ThemeRegistry {
        @Override
        public Optional<Theme> resolveActiveTheme(final String hostKey) {
            throw new IllegalStateException("registry unavailable");
        }
    }

    static class StubResponder extends StaticThemeResponder {
        private final boolean answers;
        boolean called;
        Theme lastTheme;
        int lastHttpStatus;
        int lastDisplayCode;
        String lastDetailKey;

        StubResponder(final boolean answers) {
            this.answers = answers;
        }

        @Override
        public boolean serveErrorPage(final HttpServletRequest req, final HttpServletResponse res, final Theme theme, final int httpStatus,
                final int displayCode, final String detailKey) {
            this.called = true;
            this.lastTheme = theme;
            this.lastHttpStatus = httpStatus;
            this.lastDisplayCode = displayCode;
            this.lastDetailKey = detailKey;
            return answers;
        }
    }

    /**
     * A minimal {@link HttpServletRequest} stub. Supports {@code DispatcherType} directly
     * (constructor argument) rather than through a wrapper, since this stub -- unlike a mocklet
     * request built by the test harness -- is written for this test and can simply implement the
     * method.
     */
    static class StubRequest implements HttpServletRequest {
        private final DispatcherType dispatcherType;
        private final Map<String, Object> attrs = new HashMap<>();
        // Headers other than Accept (below), added on demand by a test that needs to drive
        // virtual-host header matching.
        private final Map<String, String> extraHeaders = new HashMap<>();
        String header;
        // Fallback used by the servlet whenever the ERROR_REQUEST_URI attribute is absent (a
        // FORWARD dispatch, or a container that omits it); a plain default is enough since no
        // test needs a specific value here.
        String requestUri = "/";

        StubRequest(final DispatcherType dispatcherType) {
            this.dispatcherType = dispatcherType;
        }

        @Override
        public DispatcherType getDispatcherType() {
            return dispatcherType;
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
        public String getHeader(final String name) {
            return "Accept".equalsIgnoreCase(name) ? header : extraHeaders.get(name);
        }

        void addHeader(final String name, final String value) {
            extraHeaders.put(name, value);
        }

        @Override
        public String getContextPath() {
            return "";
        }

        // Below: every other HttpServletRequest method throws UnsupportedOperationException --
        // narrow the surface to what the servlet actually calls. If a new test fails because the
        // servlet calls something here, implement it then.
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
        public String getMethod() {
            return "GET";
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
        public String getRequestURI() {
            return requestUri;
        }

        @Override
        public StringBuffer getRequestURL() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getServletPath() {
            throw new UnsupportedOperationException();
        }

        @Override
        public RequestDispatcher getRequestDispatcher(final String path) {
            throw new UnsupportedOperationException();
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

    /**
     * A minimal {@link HttpServletResponse} stub that records the status, content type, headers
     * and body written by the servlet.
     */
    static class StubResponse implements HttpServletResponse {
        int status = 200;
        String contentType;
        final Map<String, String> headers = new HashMap<>();
        /** Settable by a test to simulate a response already committed by a partial write. */
        boolean committed;
        /** Records whether {@link #reset()} was called, to pin item 1's fallback-safety fix. */
        boolean resetCalled;
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        private final ServletOutputStream out = new ServletOutputStream() {
            @Override
            public void write(final int b) {
                buffer.write(b);
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(final WriteListener writeListener) {
                // no-op
            }
        };

        String bodyAsString() {
            return buffer.toString(StandardCharsets.UTF_8);
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
        public void setHeader(final String name, final String value) {
            headers.put(name, value);
        }

        @Override
        public void addHeader(final String name, final String value) {
            headers.put(name, value);
        }

        @Override
        public String getHeader(final String name) {
            return headers.get(name);
        }

        @Override
        public java.util.Collection<String> getHeaders(final String name) {
            final String v = headers.get(name);
            return v == null ? Collections.emptyList() : Collections.singletonList(v);
        }

        @Override
        public java.util.Collection<String> getHeaderNames() {
            return headers.keySet();
        }

        @Override
        public boolean containsHeader(final String name) {
            return headers.containsKey(name);
        }

        @Override
        public void setContentLength(final int len) {
            headers.put("Content-Length", String.valueOf(len));
        }

        @Override
        public void setContentLengthLong(final long len) {
            headers.put("Content-Length", String.valueOf(len));
        }

        @Override
        public ServletOutputStream getOutputStream() {
            return out;
        }

        @Override
        public java.io.PrintWriter getWriter() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCommitted() {
            return committed;
        }

        @Override
        public void addCookie(final jakarta.servlet.http.Cookie cookie) {
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
            throw new UnsupportedOperationException("the servlet must never call sendError from within the error dispatch");
        }

        @Override
        public void sendError(final int sc) {
            throw new UnsupportedOperationException("the servlet must never call sendError from within the error dispatch");
        }

        @Override
        public void sendRedirect(final String location) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendRedirect(final String location, final int sc, final boolean clearBuffer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendRedirect(final String location, final int sc) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendRedirect(final String location, final boolean clearBuffer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setDateHeader(final String name, final long date) {
        }

        @Override
        public void addDateHeader(final String name, final long date) {
        }

        @Override
        public void setIntHeader(final String name, final int value) {
        }

        @Override
        public void addIntHeader(final String name, final int value) {
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setCharacterEncoding(final String s) {
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
        public void reset() {
            // Mirrors a real container response: reset() on an already-committed response is
            // illegal and throws, it does not silently no-op.
            if (committed) {
                throw new IllegalStateException("cannot reset a committed response");
            }
            resetCalled = true;
            status = 200;
            contentType = null;
            headers.clear();
            buffer.reset();
        }

        @Override
        public void setLocale(final java.util.Locale loc) {
        }

        @Override
        public java.util.Locale getLocale() {
            return java.util.Locale.ROOT;
        }
    }
}
