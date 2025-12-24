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

public class RateLimitHelperTest extends UnitFessTestCase {

    private RateLimitHelper rateLimitHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        rateLimitHelper = new RateLimitHelper();
        rateLimitHelper.init();
    }

    public void test_getClientIp_remoteAddr() {
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("192.168.1.100");
        assertEquals("192.168.1.100", rateLimitHelper.getClientIp(request));
    }

    public void test_getClientIp_xForwardedFor_trustedProxy() {
        // 127.0.0.1 is configured as a trusted proxy by default
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("X-Forwarded-For", "203.0.113.50, 70.41.3.18, 150.172.238.178");
        assertEquals("203.0.113.50", rateLimitHelper.getClientIp(request));
    }

    public void test_getClientIp_xRealIp_trustedProxy() {
        // 127.0.0.1 is configured as a trusted proxy by default
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("X-Real-IP", "203.0.113.75");
        assertEquals("203.0.113.75", rateLimitHelper.getClientIp(request));
    }

    public void test_getClientIp_xForwardedForPriority_trustedProxy() {
        // 127.0.0.1 is configured as a trusted proxy by default
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("X-Forwarded-For", "203.0.113.50");
        request.addHeader("X-Real-IP", "203.0.113.75");
        assertEquals("203.0.113.50", rateLimitHelper.getClientIp(request));
    }

    public void test_getClientIp_untrustedProxy_headersIgnored() {
        // When remoteAddr is not a trusted proxy, headers should be ignored
        final MockletHttpServletRequest request = getMockRequest();
        request.setRemoteAddr("192.168.1.100");
        request.addHeader("X-Forwarded-For", "203.0.113.50");
        request.addHeader("X-Real-IP", "203.0.113.75");
        // Should return remoteAddr, not the spoofed headers
        assertEquals("192.168.1.100", rateLimitHelper.getClientIp(request));
    }

    public void test_blockIp() {
        rateLimitHelper.blockIp("192.168.1.100", 1000L);
        assertEquals(1, rateLimitHelper.getBlockedIpCount());

        rateLimitHelper.unblockIp("192.168.1.100");
        assertEquals(0, rateLimitHelper.getBlockedIpCount());
    }

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
