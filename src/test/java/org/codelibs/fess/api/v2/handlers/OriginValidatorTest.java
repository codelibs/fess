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

import java.util.Collections;
import java.util.Set;

import org.codelibs.fess.cors.CorsHandler;
import org.codelibs.fess.cors.CorsHandlerFactory;
import org.codelibs.fess.cors.CorsMatchType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.utflute.mocklet.MockletHttpServletRequest;
import org.dbflute.utflute.mocklet.MockletHttpServletRequestImpl;
import org.junit.jupiter.api.Test;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Unit tests for the {@link OriginValidator} component. A stub
 * {@link TargetOriginResolver} is injected so the trusted self-origin set is
 * controlled directly, and a real {@link CorsHandlerFactory} (populated with an
 * EXACT origin and a {@code "*"} wildcard) is registered via {@link ComponentUtil}
 * to pin EXACT-vs-WILDCARD trust. Extends {@link UnitFessTestCase} so UTFlute mock
 * requests are available for exercising {@code Origin}/{@code Referer} headers.
 */
public class OriginValidatorTest extends UnitFessTestCase {

    private static final String SELF = "https://fess.example.com";

    /** Allow-listed (EXACT) cross-origin SPA. */
    private static final String ALLOWED_ORIGIN = "https://spa.example.com";

    /**
     * Builds an {@link OriginValidator} whose {@link TargetOriginResolver} always
     * returns the single {@link #SELF} origin, and registers a CORS allow list with
     * one EXACT origin plus a {@code "*"} wildcard fallback.
     */
    private OriginValidator validator() {
        final CorsHandlerFactory factory = new CorsHandlerFactory();
        // EXACT allow-listed origin and a wildcard fallback. The wildcard must NOT be
        // treated as trusted for CSRF.
        factory.add(ALLOWED_ORIGIN, new NopCorsHandler());
        factory.add("*", new NopCorsHandler());
        ComponentUtil.register(factory, "corsHandlerFactory");

        final OriginValidator validator = new OriginValidator();
        validator.targetOriginResolver = new TargetOriginResolver() {
            @Override
            public Set<String> resolve(final HttpServletRequest request) {
                return Collections.singleton(SELF);
            }
        };
        return validator;
    }

    private MockletHttpServletRequest newRequest() {
        final ServletContext ctx = getMockRequest().getServletContext();
        return new MockletHttpServletRequestImpl(ctx, "/api/v2/click");
    }

    private boolean allowed(final MockletHttpServletRequest request) {
        return validator().isAllowed(request);
    }

    // same-origin

    @Test
    public void test_sameOriginAllowed() {
        final MockletHttpServletRequest req = newRequest();
        req.addHeader("Origin", SELF);
        assertTrue(allowed(req));
    }

    @Test
    public void test_sameOriginWithDefaultPortNormalizedAllowed() {
        final MockletHttpServletRequest req = newRequest();
        req.addHeader("Origin", "https://fess.example.com:443");
        assertTrue(allowed(req));
    }

    // cross-site Origin

    @Test
    public void test_crossSiteOriginRejected() {
        final MockletHttpServletRequest req = newRequest();
        req.addHeader("Origin", "https://evil.example.com");
        assertFalse(allowed(req));
    }

    // Origin priority over Referer

    @Test
    public void test_crossSiteOriginWithSameOriginRefererStillRejected() {
        // Origin is present and cross-site; a same-origin Referer must NOT rescue it.
        final MockletHttpServletRequest req = newRequest();
        req.addHeader("Origin", "https://evil.example.com");
        req.addHeader("Referer", SELF + "/search");
        assertFalse(allowed(req));
    }

    // Referer fallback

    @Test
    public void test_originAbsentSameOriginRefererAllowed() {
        final MockletHttpServletRequest req = newRequest();
        req.addHeader("Referer", SELF + "/search?q=x");
        assertTrue(allowed(req));
    }

    @Test
    public void test_originAbsentCrossSiteRefererRejected() {
        final MockletHttpServletRequest req = newRequest();
        req.addHeader("Referer", "https://evil.example.com/page");
        assertFalse(allowed(req));
    }

    // CORS allow list

    @Test
    public void test_corsExactAllowed() {
        final MockletHttpServletRequest req = newRequest();
        req.addHeader("Origin", ALLOWED_ORIGIN);
        assertTrue(allowed(req));
    }

    @Test
    public void test_wildcardNotTrusted() {
        // An origin that only matches via the "*" wildcard must be rejected for CSRF.
        final MockletHttpServletRequest req = newRequest();
        req.addHeader("Origin", "https://random-unlisted.example.org");
        assertFalse(allowed(req));
    }

    // missing source

    @Test
    public void test_bothMissingAllowed() {
        // No Origin and no Referer → non-browser API client → allow.
        final MockletHttpServletRequest req = newRequest();
        assertTrue(allowed(req));
    }

    // malformed Origin values

    @Test
    public void test_originLiteralNullRejected() {
        final MockletHttpServletRequest req = newRequest();
        req.addHeader("Origin", "null");
        assertFalse(allowed(req));
    }

    @Test
    public void test_originEmptyFallsBackToRefererAndAllowsWhenSameOrigin() {
        // An empty Origin is blank → fall back to Referer (same-origin) → allow.
        final MockletHttpServletRequest req = newRequest();
        req.addHeader("Origin", "");
        req.addHeader("Referer", SELF + "/x");
        assertTrue(allowed(req));
    }

    @Test
    public void test_originEmptyAndNoRefererAllowed() {
        final MockletHttpServletRequest req = newRequest();
        req.addHeader("Origin", "");
        assertTrue(allowed(req));
    }

    @Test
    public void test_originMultiValueRejected() {
        final MockletHttpServletRequest req = newRequest();
        req.addHeader("Origin", "https://a.example.com https://b.example.com");
        assertFalse(allowed(req));
    }

    @Test
    public void test_originControlCharRejected() {
        final MockletHttpServletRequest req = newRequest();
        req.addHeader("Origin", "https://evil.example.com\r\nX: y");
        assertFalse(allowed(req));
    }

    @Test
    public void test_originUnparseableRejected() {
        final MockletHttpServletRequest req = newRequest();
        req.addHeader("Origin", "httpsexample");
        assertFalse(allowed(req));
    }

    /** No-op CORS handler used only to populate the allow-list registry. */
    private static final class NopCorsHandler extends CorsHandler {
        @Override
        public void process(final String origin, final CorsMatchType matchType, final ServletRequest request,
                final ServletResponse response) {
            // no-op
        }
    }
}
