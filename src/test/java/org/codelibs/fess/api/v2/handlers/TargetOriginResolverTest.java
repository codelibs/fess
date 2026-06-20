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

import java.util.Set;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.utflute.mocklet.MockletHttpServletRequest;
import org.dbflute.utflute.mocklet.MockletHttpServletRequestImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.ServletContext;

/**
 * Unit tests for the {@link TargetOriginResolver} component. The trust boundary
 * for forwarded headers is the security-critical behavior pinned here.
 *
 * <p>{@link FessConfig.SimpleImpl} is sub-classed inline to control
 * {@code theme.api.csrf.server.origins} and {@code rate.limit.trusted.proxies}
 * without booting the container; the chosen config is installed via
 * {@link ComponentUtil#setFessConfig} so the component picks it up through
 * {@code ComponentUtil.getFessConfig()}.</p>
 */
public class TargetOriginResolverTest extends UnitFessTestCase {

    private static final String TRUSTED_PROXY = "10.0.0.5";

    private static final String UNTRUSTED_PEER = "203.0.113.9";

    private final TargetOriginResolver resolver = new TargetOriginResolver();

    /** Installs a {@link FessConfig} whose CSRF/trusted-proxy raw values are fixed for the test. */
    private void useConfig(final String serverOrigins, final String trustedProxies) {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getThemeApiCsrfServerOrigins() {
                return serverOrigins;
            }

            @Override
            public String getRateLimitTrustedProxies() {
                return trustedProxies;
            }
        });
    }

    @AfterEach
    public void resetFessConfig() {
        ComponentUtil.setFessConfig(null);
    }

    private MockletHttpServletRequest newRequest(final String remoteAddr) {
        final ServletContext ctx = getMockRequest().getServletContext();
        final MockletHttpServletRequestImpl req = new MockletHttpServletRequestImpl(ctx, "/api/v2/click");
        req.setRemoteAddr(remoteAddr);
        return req;
    }

    // (1) explicit server.origins wins over forwarded headers

    @Test
    public void test_serverOriginsConfigured_ignoresForwardedHeaders() {
        useConfig("https://fess.example.com", TRUSTED_PROXY);
        final MockletHttpServletRequest req = newRequest(TRUSTED_PROXY);
        // These spoofed/legitimate forwarded headers must be ignored when server.origins is set.
        req.addHeader("X-Forwarded-Proto", "https");
        req.addHeader("X-Forwarded-Host", "evil.example.com");

        final Set<String> origins = resolver.resolve(req);
        assertTrue(origins.contains("https://fess.example.com"), origins.toString());
        assertFalse(origins.contains("https://evil.example.com"), origins.toString());
        assertEquals(1, origins.size());
    }

    @Test
    public void test_serverOriginsMultiValueCanonicalized() {
        useConfig("https://a.example.com:443, http://b.example.com:80", TRUSTED_PROXY);
        final MockletHttpServletRequest req = newRequest(TRUSTED_PROXY);

        final Set<String> origins = resolver.resolve(req);
        assertTrue(origins.contains("https://a.example.com"), origins.toString());
        assertTrue(origins.contains("http://b.example.com"), origins.toString());
        assertEquals(2, origins.size());
    }

    // (2) trusted-proxy forwarded reconstruction

    @Test
    public void test_trustedProxy_reconstructsFromForwardedHeaders() {
        useConfig("", TRUSTED_PROXY);
        final MockletHttpServletRequest req = newRequest(TRUSTED_PROXY);
        req.addHeader("X-Forwarded-Proto", "https");
        req.addHeader("X-Forwarded-Host", "public.example.com");

        final Set<String> origins = resolver.resolve(req);
        assertTrue(origins.contains("https://public.example.com"), origins.toString());
        assertEquals(1, origins.size());
    }

    @Test
    public void test_trustedProxy_forwardedPortAppliedAndDefaultNormalized() {
        useConfig("", TRUSTED_PROXY);
        final MockletHttpServletRequest req = newRequest(TRUSTED_PROXY);
        req.addHeader("X-Forwarded-Proto", "https");
        req.addHeader("X-Forwarded-Host", "public.example.com");
        req.addHeader("X-Forwarded-Port", "443");

        // Default https port must be normalized away by canonicalization.
        final Set<String> origins = resolver.resolve(req);
        assertTrue(origins.contains("https://public.example.com"), origins.toString());
    }

    @Test
    public void test_trustedProxy_forwardedNonDefaultPortKept() {
        useConfig("", TRUSTED_PROXY);
        final MockletHttpServletRequest req = newRequest(TRUSTED_PROXY);
        req.addHeader("X-Forwarded-Proto", "https");
        req.addHeader("X-Forwarded-Host", "public.example.com");
        req.addHeader("X-Forwarded-Port", "8443");

        final Set<String> origins = resolver.resolve(req);
        assertTrue(origins.contains("https://public.example.com:8443"), origins.toString());
    }

    @Test
    public void test_trustedProxy_forwardedHostCommaListUsesFirst() {
        useConfig("", TRUSTED_PROXY);
        final MockletHttpServletRequest req = newRequest(TRUSTED_PROXY);
        req.addHeader("X-Forwarded-Proto", "https");
        req.addHeader("X-Forwarded-Host", "public.example.com, internal.example.com");

        final Set<String> origins = resolver.resolve(req);
        assertTrue(origins.contains("https://public.example.com"), origins.toString());
        assertFalse(origins.contains("https://internal.example.com"), origins.toString());
    }

    @Test
    public void test_trustedProxy_bracketedIPv6HostWithoutPort_appliesForwardedPort() {
        // A bracketed IPv6 literal has inner colons but no port; X-Forwarded-Port must
        // still be appended (regression for the IPv6 port-detection bug).
        useConfig("", TRUSTED_PROXY);
        final MockletHttpServletRequest req = newRequest(TRUSTED_PROXY);
        req.addHeader("X-Forwarded-Proto", "https");
        req.addHeader("X-Forwarded-Host", "[::1]");
        req.addHeader("X-Forwarded-Port", "8443");

        final Set<String> origins = resolver.resolve(req);
        assertTrue(origins.contains("https://[::1]:8443"), origins.toString());
    }

    @Test
    public void test_trustedProxy_bracketedIPv6HostWithPort_doesNotDoubleAppend() {
        // Already-ported bracketed IPv6 literal: the existing port wins and a differing
        // X-Forwarded-Port must not be appended a second time.
        useConfig("", TRUSTED_PROXY);
        final MockletHttpServletRequest req = newRequest(TRUSTED_PROXY);
        req.addHeader("X-Forwarded-Proto", "https");
        req.addHeader("X-Forwarded-Host", "[::1]:8443");
        req.addHeader("X-Forwarded-Port", "9999");

        final Set<String> origins = resolver.resolve(req);
        assertTrue(origins.contains("https://[::1]:8443"), origins.toString());
        assertFalse(origins.contains("https://[::1]:9999"), origins.toString());
    }

    // (2 negative) untrusted peer cannot spoof via forwarded headers

    @Test
    public void test_untrustedPeer_forwardedHeadersIgnored_fallsBackToServlet() {
        useConfig("", TRUSTED_PROXY);
        final MockletHttpServletRequest req = newRequest(UNTRUSTED_PEER);
        // Spoofed forwarded headers from an untrusted source must be ignored.
        req.addHeader("X-Forwarded-Proto", "https");
        req.addHeader("X-Forwarded-Host", "evil.example.com");
        req.setScheme("http");
        req.setServerName("realhost.internal");
        req.setServerPort(8080);

        final Set<String> origins = resolver.resolve(req);
        assertFalse(origins.contains("https://evil.example.com"), origins.toString());
        assertTrue(origins.contains("http://realhost.internal:8080"), origins.toString());
    }

    // (3) servlet reconstruction with port normalization

    @Test
    public void test_noForwarded_reconstructsFromServletWithDefaultPortNormalized() {
        useConfig("", TRUSTED_PROXY);
        final MockletHttpServletRequest req = newRequest(UNTRUSTED_PEER);
        req.setScheme("https");
        req.setServerName("fess.example.com");
        req.setServerPort(443);

        final Set<String> origins = resolver.resolve(req);
        assertTrue(origins.contains("https://fess.example.com"), origins.toString());
        assertEquals(1, origins.size());
    }

    @Test
    public void test_noForwarded_reconstructsFromServletWithNonDefaultPort() {
        useConfig("", TRUSTED_PROXY);
        final MockletHttpServletRequest req = newRequest(UNTRUSTED_PEER);
        req.setScheme("http");
        req.setServerName("localhost");
        req.setServerPort(8080);

        final Set<String> origins = resolver.resolve(req);
        assertTrue(origins.contains("http://localhost:8080"), origins.toString());
    }
}
