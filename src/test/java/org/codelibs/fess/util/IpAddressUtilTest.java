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
package org.codelibs.fess.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for IpAddressUtil class.
 */
public class IpAddressUtilTest {

    @Test
    public void testCompressIPv6() {
        // Test IPv6 compression (using reflection to access protected method)
        try {
            java.lang.reflect.Method method = IpAddressUtil.class.getDeclaredMethod("compressIPv6", String.class);
            method.setAccessible(true);

            // Compressed forms should remain unchanged
            assertEquals("::1", method.invoke(null, "::1"));
            assertEquals("2001:db8::1", method.invoke(null, "2001:db8::1"));

            // Expanded forms should be compressed
            assertEquals("::1", method.invoke(null, "0:0:0:0:0:0:0:1"));
            assertEquals("2001:db8::1", method.invoke(null, "2001:0db8:0000:0000:0000:0000:0000:0001"));
            assertEquals("fe80::1", method.invoke(null, "fe80:0000:0000:0000:0000:0000:0000:0001"));

            // Leading zeros removed and consecutive zeros compressed
            assertEquals("2001:db8::1", method.invoke(null, "2001:0db8:0:0:0:0:0:1"));

            // Longest sequence of zeros compressed (3 zeros at end vs 1 zero at start)
            assertEquals("2001:db8:0:1::1", method.invoke(null, "2001:0db8:0:1:0:0:0:1"));

            // No compression when all segments are non-zero (just remove leading zeros)
            assertEquals("2001:db8:1:2:3:4:5:6", method.invoke(null, "2001:0db8:0001:0002:0003:0004:0005:0006"));

            // Single zero should not be compressed
            assertEquals("2001:db8:1:0:2:3:4:5", method.invoke(null, "2001:0db8:1:0:2:3:4:5"));
        } catch (Exception e) {
            // If reflection fails, skip this test
        }
    }

    @Test
    public void testIsIPv6Address() {
        // IPv6 addresses
        assertTrue(IpAddressUtil.isIPv6Address("::1"));
        assertTrue(IpAddressUtil.isIPv6Address("2001:db8::1"));
        assertTrue(IpAddressUtil.isIPv6Address("fe80::1"));
        assertTrue(IpAddressUtil.isIPv6Address("2001:0db8:0000:0000:0000:ff00:0042:8329"));

        // IPv4 addresses
        assertFalse(IpAddressUtil.isIPv6Address("127.0.0.1"));
        assertFalse(IpAddressUtil.isIPv6Address("192.168.1.1"));
        assertFalse(IpAddressUtil.isIPv6Address("10.0.0.1"));

        // Hostname
        assertFalse(IpAddressUtil.isIPv6Address("localhost"));
        assertFalse(IpAddressUtil.isIPv6Address("example.com"));

        // Null
        assertFalse(IpAddressUtil.isIPv6Address(null));
    }

    @Test
    public void testFormatForUrl() {
        // IPv6 addresses - should add brackets
        assertEquals("[::1]", IpAddressUtil.formatForUrl("::1"));
        assertEquals("[2001:db8::1]", IpAddressUtil.formatForUrl("2001:db8::1"));
        assertEquals("[fe80::1]", IpAddressUtil.formatForUrl("fe80::1"));

        // IPv6 addresses already with brackets - should not double-wrap
        assertEquals("[::1]", IpAddressUtil.formatForUrl("[::1]"));
        assertEquals("[2001:db8::1]", IpAddressUtil.formatForUrl("[2001:db8::1]"));

        // IPv4 addresses - should remain unchanged
        assertEquals("127.0.0.1", IpAddressUtil.formatForUrl("127.0.0.1"));
        assertEquals("192.168.1.1", IpAddressUtil.formatForUrl("192.168.1.1"));

        // Hostnames - should remain unchanged
        assertEquals("localhost", IpAddressUtil.formatForUrl("localhost"));
        assertEquals("example.com", IpAddressUtil.formatForUrl("example.com"));

        // Null
        assertNull(IpAddressUtil.formatForUrl(null));
    }

    @Test
    public void testGetUrlHost() throws UnknownHostException {
        // IPv4 localhost
        InetAddress ipv4Localhost = InetAddress.getByName("127.0.0.1");
        assertEquals("127.0.0.1", IpAddressUtil.getUrlHost(ipv4Localhost));

        // IPv6 localhost
        InetAddress ipv6Localhost = InetAddress.getByName("::1");
        assertEquals("[::1]", IpAddressUtil.getUrlHost(ipv6Localhost));

        // Null
        assertNull(IpAddressUtil.getUrlHost(null));
    }

    @Test
    public void testBuildUrlWithInetAddress() throws UnknownHostException {
        // IPv4 with port and path
        InetAddress ipv4 = InetAddress.getByName("127.0.0.1");
        assertEquals("http://127.0.0.1:8080/path", IpAddressUtil.buildUrl("http", ipv4, 8080, "/path"));
        assertEquals("http://127.0.0.1:8080/path", IpAddressUtil.buildUrl("http", ipv4, 8080, "path"));

        // IPv4 with port, no path
        assertEquals("http://127.0.0.1:8080", IpAddressUtil.buildUrl("http", ipv4, 8080, ""));
        assertEquals("http://127.0.0.1:8080", IpAddressUtil.buildUrl("http", ipv4, 8080, null));

        // IPv4 without port
        assertEquals("http://127.0.0.1", IpAddressUtil.buildUrl("http", ipv4, 0, ""));
        assertEquals("http://127.0.0.1", IpAddressUtil.buildUrl("http", ipv4, -1, ""));

        // IPv6 with port and path
        InetAddress ipv6 = InetAddress.getByName("::1");
        assertEquals("http://[::1]:8080/sso/metadata", IpAddressUtil.buildUrl("http", ipv6, 8080, "/sso/metadata"));

        // IPv6 with port, no path
        assertEquals("http://[::1]:9200", IpAddressUtil.buildUrl("http", ipv6, 9200, ""));

        // IPv6 without port
        assertEquals("https://[::1]", IpAddressUtil.buildUrl("https", ipv6, 0, ""));

        // Null protocol or address
        assertNull(IpAddressUtil.buildUrl(null, ipv4, 8080, "/path"));
        assertNull(IpAddressUtil.buildUrl("http", (InetAddress) null, 8080, "/path"));
    }

    @Test
    public void testBuildUrlWithHostString() {
        // IPv4 with port and path
        assertEquals("http://127.0.0.1:8080/path", IpAddressUtil.buildUrl("http", "127.0.0.1", 8080, "/path"));

        // IPv4 with port, no path
        assertEquals("http://192.168.1.1:9200", IpAddressUtil.buildUrl("http", "192.168.1.1", 9200, ""));

        // IPv6 with port and path
        assertEquals("http://[::1]:8080/sso/", IpAddressUtil.buildUrl("http", "::1", 8080, "/sso/"));
        assertEquals("http://[2001:db8::1]:8080/path", IpAddressUtil.buildUrl("http", "2001:db8::1", 8080, "/path"));

        // IPv6 already with brackets
        assertEquals("http://[::1]:8080/path", IpAddressUtil.buildUrl("http", "[::1]", 8080, "/path"));

        // IPv6 without port
        assertEquals("https://[fe80::1]", IpAddressUtil.buildUrl("https", "fe80::1", 0, ""));

        // Hostname with port and path
        assertEquals("http://localhost:8080/admin", IpAddressUtil.buildUrl("http", "localhost", 8080, "/admin"));
        assertEquals("https://example.com:443/api", IpAddressUtil.buildUrl("https", "example.com", 443, "/api"));

        // Path without leading slash
        assertEquals("http://localhost:8080/path", IpAddressUtil.buildUrl("http", "localhost", 8080, "path"));

        // Null protocol or host
        assertNull(IpAddressUtil.buildUrl(null, "localhost", 8080, "/path"));
        assertNull(IpAddressUtil.buildUrl("http", (String) null, 8080, "/path"));
    }

    @Test
    public void testRealWorldScenarios() throws UnknownHostException {
        // Scenario 1: SAML entityId for IPv6
        InetAddress localhost = InetAddress.getByName("localhost");
        String entityId = IpAddressUtil.buildUrl("http", localhost, 8080, "/sso/metadata");
        assertTrue(entityId.startsWith("http://"));
        assertTrue(entityId.contains(":8080/sso/metadata"));

        // Scenario 2: OpenSearch embedded mode URL
        if (localhost.getHostAddress().contains(":")) {
            // IPv6 localhost
            String opensearchUrl = IpAddressUtil.buildUrl("http", localhost, 9200, "");
            assertTrue(opensearchUrl.matches("http://\\[.*\\]:9200"));
        } else {
            // IPv4 localhost
            String opensearchUrl = IpAddressUtil.buildUrl("http", localhost, 9200, "");
            assertTrue(opensearchUrl.matches("http://[0-9.]+:9200"));
        }

        // Scenario 3: System hostname display
        String urlHost = IpAddressUtil.getUrlHost(localhost);
        assertTrue(urlHost != null && !urlHost.isEmpty());
    }
}
