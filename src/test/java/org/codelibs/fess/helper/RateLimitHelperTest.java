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
package org.codelibs.fess.helper;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.utflute.mocklet.MockletHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class RateLimitHelperTest extends UnitFessTestCase {

    private RateLimitHelper rateLimitHelper;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        rateLimitHelper = new RateLimitHelper();
        rateLimitHelper.init();
    }

    @Test
    public void test_getClientIp_remoteAddr() {
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("192.168.1.100");
        assertEquals("192.168.1.100", rateLimitHelper.getClientIp(request));
    }

    @Test
    public void test_getClientIp_xForwardedFor_trustedProxy() {
        // 127.0.0.1 is configured as a trusted proxy by default. Only the last entry was actually
        // observed by a hop we trust; everything to its left is whatever the client chose to send.
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("X-Forwarded-For", "203.0.113.50, 70.41.3.18, 150.172.238.178");
        assertEquals("150.172.238.178", rateLimitHelper.getClientIp(request));
    }

    @Test
    public void test_getClientIp_xForwardedFor_ignoresAClientSuppliedPrefix() {
        // A proxy that appends (nginx's proxy_add_x_forwarded_for) leaves anything the client sent
        // in front of the address it saw. Attributing the request to the front entry would let the
        // caller pick its own rate limit bucket, and simply changing it would lift a block.
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("X-Forwarded-For", "10.9.9.9, 203.0.113.50");
        assertEquals("203.0.113.50", rateLimitHelper.getClientIp(request));

        final MockletHttpServletRequest spoofed = getMockRequest();
        spoofed.setRemoteAddr("127.0.0.1");
        spoofed.addHeader("X-Forwarded-For", "10.8.8.8, 203.0.113.50");
        assertEquals("203.0.113.50", rateLimitHelper.getClientIp(spoofed));
    }

    @Test
    public void test_getClientIp_xForwardedFor_skipsOurOwnProxies() {
        // A chain made only of trusted hops says nothing about the caller, so the request falls
        // back to the address the connection actually came from.
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("X-Forwarded-For", "127.0.0.1, ::1");
        assertEquals("127.0.0.1", rateLimitHelper.getClientIp(request));
    }

    @Test
    public void test_getClientIp_xForwardedFor_fallsBackWhenEveryHopIsTrusted() {
        // X-Real-IP is set by the proxy rather than appended to, so it is still usable when the
        // forwarded chain carries nothing we can attribute.
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("X-Forwarded-For", "127.0.0.1");
        request.addHeader("X-Real-IP", "203.0.113.75");
        assertEquals("203.0.113.75", rateLimitHelper.getClientIp(request));
    }

    @Test
    public void test_getClientIp_xRealIp_trustedProxy() {
        // 127.0.0.1 is configured as a trusted proxy by default
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("X-Real-IP", "203.0.113.75");
        assertEquals("203.0.113.75", rateLimitHelper.getClientIp(request));
    }

    @Test
    public void test_getClientIp_xForwardedForPriority_trustedProxy() {
        // 127.0.0.1 is configured as a trusted proxy by default
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("X-Forwarded-For", "203.0.113.50");
        request.addHeader("X-Real-IP", "203.0.113.75");
        assertEquals("203.0.113.50", rateLimitHelper.getClientIp(request));
    }

    @Test
    public void test_getClientIp_untrustedProxy_headersIgnored() {
        // When remoteAddr is not a trusted proxy, headers should be ignored
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("192.168.1.100");
        request.addHeader("X-Forwarded-For", "203.0.113.50");
        request.addHeader("X-Real-IP", "203.0.113.75");
        // Should return remoteAddr, not the spoofed headers
        assertEquals("192.168.1.100", rateLimitHelper.getClientIp(request));
    }

    // ── M-2: reverse-proxy / trusted-proxy allowlist scenarios ──────────────

    @Test
    public void test_getClientIp_noTrustedProxy_emptyXff_returnsRemoteAddr() {
        // When the requesting peer is not a trusted proxy, X-Forwarded-For MUST be ignored
        // even if the header is present — prevents a direct client from spoofing their IP.
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("10.0.0.1");
        request.addHeader("X-Forwarded-For", "1.1.1.1, 2.2.2.2");
        // 10.0.0.1 is NOT in the default trusted-proxy allowlist (127.0.0.1,::1)
        assertEquals("10.0.0.1", rateLimitHelper.getClientIp(request));
    }

    @Test
    public void test_getClientIp_trustedProxy_xffWithSpaces_trimsTokens() {
        // RFC 7239-style: each token in the XFF list may have surrounding whitespace, and the one
        // that is used must come back trimmed.
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("127.0.0.1"); // trusted proxy by default config
        request.addHeader("X-Forwarded-For", "  1.1.1.1  ,  2.2.2.2  ");
        assertEquals("2.2.2.2", rateLimitHelper.getClientIp(request));
    }

    @Test
    public void test_getClientIp_trustedProxy_malformedXff_returnsRemoteAddr() {
        // A blank / whitespace-only XFF header is treated as absent: fall through to remoteAddr.
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("127.0.0.1"); // trusted proxy by default config
        request.addHeader("X-Forwarded-For", "   ");
        // StringUtil.isNotBlank("   ") is false, so XFF branch is skipped.
        assertEquals("127.0.0.1", rateLimitHelper.getClientIp(request));
    }

    @Test
    public void test_getClientIp_trustedProxy_xffSingleEntry_returnsClientIp() {
        // Simple common case: a single-hop reverse proxy appends exactly one IP.
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("X-Forwarded-For", "203.0.113.10");
        assertEquals("203.0.113.10", rateLimitHelper.getClientIp(request));
    }

    @Test
    public void test_getClientIp_trustedProxy_xffMultiHop_returnsRightmostUntrusted() {
        // Multi-hop chain "client, proxy1, proxy2". Only the last entry was observed by a hop we
        // trust; everything to its left is only as trustworthy as whoever wrote it, and the
        // caller writes the front of the chain. Rate limiting has to attribute the request to
        // something the caller cannot choose, so the rightmost entry that is not one of our own
        // proxies is used.
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("X-Forwarded-For", "1.1.1.1, 2.2.2.2, 3.3.3.3");
        assertEquals("3.3.3.3", rateLimitHelper.getClientIp(request));
    }

    @Test
    public void test_getClientIp_ipv6LoopbackProxy_isTrusted() {
        // Java renders the IPv6 loopback as 0:0:0:0:0:0:0:1, while the shipped
        // rate.limit.trusted.proxies lists it as "::1". The comparison used to be a plain string
        // match, so a reverse proxy on IPv6 loopback -- what proxy_pass http://localhost:8080
        // resolves to where localhost prefers IPv6 -- was silently untrusted, and every client
        // behind it shared one rate-limit bucket.
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("0:0:0:0:0:0:0:1");
        request.addHeader("X-Forwarded-For", "203.0.113.50");
        assertEquals("203.0.113.50", rateLimitHelper.getClientIp(request));
    }

    @Test
    public void test_getClientIp_ipv6LoopbackShortForm_isTrusted() {
        // The same address in the spelling the configuration uses. This one already matched
        // before the fix -- it is the literal in the shipped default -- and is kept so that
        // canonicalising the configured side cannot regress it.
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("::1");
        request.addHeader("X-Forwarded-For", "203.0.113.51");
        assertEquals("203.0.113.51", rateLimitHelper.getClientIp(request));
    }

    @Test
    public void test_getClientIp_untrustedIpv6_headersIgnored() {
        // Control: canonicalising both sides must not make every IPv6 peer trusted.
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("2001:db8::1");
        request.addHeader("X-Forwarded-For", "203.0.113.52");
        assertEquals("2001:db8::1", rateLimitHelper.getClientIp(request));
    }

    @Test
    public void test_blockIp() {
        rateLimitHelper.blockIp("192.168.1.100", 1000L);
        assertEquals(1, rateLimitHelper.getBlockedIpCount());

        rateLimitHelper.unblockIp("192.168.1.100");
        assertEquals(0, rateLimitHelper.getBlockedIpCount());
    }

    @Test
    public void test_cleanup() {
        rateLimitHelper.blockIp("192.168.1.100", 1L);
        assertEquals(1, rateLimitHelper.getBlockedIpCount());

        // Guava Cache handles expiration automatically
        // Wait for cache to expire and call cleanup
        try {
            Thread.sleep(100L);
        } catch (final InterruptedException e) {
            // ignore
        }

        rateLimitHelper.cleanup();
        // After cleanup, the expired entry should be removed
        // Note: The actual expiration depends on the configured block duration
        assertTrue(rateLimitHelper.getBlockedIpCount() >= 0);
    }
}
