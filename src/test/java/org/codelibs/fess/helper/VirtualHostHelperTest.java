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

import org.codelibs.core.misc.Tuple3;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.utflute.mocklet.MockletHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class VirtualHostHelperTest extends UnitFessTestCase {

    private VirtualHostHelper virtualHostHelper;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        virtualHostHelper = new VirtualHostHelper();
    }

    @Test
    public void test_getVirtualHostKey() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Tuple3<String, String, String>[] getVirtualHosts() {
                return new Tuple3[] { new Tuple3<>("Host", "example.com", "site1"), new Tuple3<>("X-Forwarded-Host", "test.com", "site2") };
            }
        });

        MockletHttpServletRequest request = getMockRequest();

        // No matching host header
        String key = virtualHostHelper.getVirtualHostKey();
        assertEquals("", key);

        // Matching host header
        request.addHeader("Host", "example.com");
        key = virtualHostHelper.getVirtualHostKey();
        assertEquals("", key); // Still empty due to caching

        // Test caching by calling again - should return cached value
        key = virtualHostHelper.getVirtualHostKey();
        assertEquals("", key); // Still cached value
    }

    @Test
    public void test_getVirtualHostKey_withCaching() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Tuple3<String, String, String>[] getVirtualHosts() {
                return new Tuple3[] { new Tuple3<>("Host", "example.com", "site1") };
            }
        });

        MockletHttpServletRequest request = getMockRequest();
        request.setAttribute(FessConfig.VIRTUAL_HOST_VALUE, "cached_value");

        String key = virtualHostHelper.getVirtualHostKey();
        assertEquals("cached_value", key);
    }

    @Test
    public void test_processVirtualHost() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Tuple3<String, String, String>[] getVirtualHosts() {
                return new Tuple3[] { new Tuple3<>("Host", "example.com", "site1"), new Tuple3<>("X-Forwarded-Host", "test.com", "site2"),
                        new Tuple3<>("Custom-Header", "custom.value", "site3") };
            }
        });

        MockletHttpServletRequest request = getMockRequest();

        // Test with no matching headers
        String result = virtualHostHelper.processVirtualHost(s -> "processed_" + s, "default");
        assertEquals("default", result);

        // Test with matching first header
        request.addHeader("Host", "example.com");
        result = virtualHostHelper.processVirtualHost(s -> "processed_" + s, "default");
        assertEquals("processed_site1", result);

        // Headers persist in same request, so subsequent headers are additive
        // Test with case insensitive matching
        request.addHeader("X-Forwarded-Host", "TEST.COM");
        result = virtualHostHelper.processVirtualHost(s -> "processed_" + s, "default");
        assertEquals("processed_site1", result); // Still site1 due to header precedence

        // Test with custom header
        request.addHeader("Custom-Header", "custom.value");
        result = virtualHostHelper.processVirtualHost(s -> "processed_" + s, "default");
        assertEquals("processed_site1", result); // Still site1 due to header precedence
    }

    @Test
    public void test_processVirtualHost_withEmptyVirtualHosts() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Tuple3<String, String, String>[] getVirtualHosts() {
                return new Tuple3[0];
            }
        });

        MockletHttpServletRequest request = getMockRequest();
        request.addHeader("Host", "example.com");

        String result = virtualHostHelper.processVirtualHost(s -> "processed_" + s, "default");
        assertEquals("default", result);
    }

    @Test
    public void test_processVirtualHost_withNullHeaderValue() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Tuple3<String, String, String>[] getVirtualHosts() {
                return new Tuple3[] { new Tuple3<>("Host", "example.com", "site1") };
            }
        });

        MockletHttpServletRequest request = getMockRequest();
        // Don't add any headers, so getHeader will return null

        String result = virtualHostHelper.processVirtualHost(s -> "processed_" + s, "default");
        assertEquals("default", result);
    }

    @Test
    public void test_multipleHeaderMatching() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Tuple3<String, String, String>[] getVirtualHosts() {
                return new Tuple3[] { new Tuple3<>("Host", "example.com", "site1"), new Tuple3<>("Host", "test.com", "site2") };
            }
        });

        MockletHttpServletRequest request = getMockRequest();
        request.addHeader("Host", "test.com");

        String key = virtualHostHelper.getVirtualHostKey();
        assertEquals("site2", key);
    }

    @Test
    public void test_caseInsensitiveHeaderValueMatching() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Tuple3<String, String, String>[] getVirtualHosts() {
                return new Tuple3[] { new Tuple3<>("Host", "Example.Com", "site1") };
            }
        });

        MockletHttpServletRequest request = getMockRequest();
        request.addHeader("Host", "example.com");

        String key = virtualHostHelper.getVirtualHostKey();
        assertEquals("site1", key);

        request.addHeader("Host", "EXAMPLE.COM");
        String key2 = virtualHostHelper.getVirtualHostKey();
        assertEquals("site1", key2); // Cached value
    }
}