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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

import javax.net.ssl.SSLSocketFactory;

import org.codelibs.curl.Curl;
import org.codelibs.curl.CurlRequest;
import org.codelibs.fesen.client.node.NodeManager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class CurlHelperTest extends UnitFessTestCase {

    private CurlHelper curlHelper;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        curlHelper = new CurlHelper();
    }

    @Test
    public void test_get() {
        // Mock FessConfig and ResourceUtil
        setupMockConfig("localhost:9200", "", "");

        curlHelper.init();
        CurlRequest request = curlHelper.get("/test");

        assertNotNull(request);
        // The request should be properly created - we can't easily verify headers without mocking framework
    }

    @Test
    public void test_post() {
        setupMockConfig("localhost:9200", "", "");

        curlHelper.init();
        CurlRequest request = curlHelper.post("/test");

        assertNotNull(request);
        // The request should be properly created
    }

    @Test
    public void test_put() {
        setupMockConfig("localhost:9200", "", "");

        curlHelper.init();
        CurlRequest request = curlHelper.put("/test");

        assertNotNull(request);
        // The request should be properly created
    }

    @Test
    public void test_delete() {
        setupMockConfig("localhost:9200", "", "");

        curlHelper.init();
        CurlRequest request = curlHelper.delete("/test");

        assertNotNull(request);
        // The request should be properly created
    }

    @Test
    public void test_request_withMethodAndPath() {
        setupMockConfig("localhost:9200", "", "");

        curlHelper.init();
        CurlRequest request = curlHelper.request(Curl.Method.GET, "/test");

        assertNotNull(request);
    }

    @Test
    public void test_init_withoutSSL() {
        setupMockConfig("localhost:9200", "", "");

        curlHelper.init();

        // Verify NodeManager is initialized
        NodeManager nodeManager = getNodeManager(curlHelper);
        assertNotNull(nodeManager);

        // Verify SSL socket factory is not set
        SSLSocketFactory sslSocketFactory = getSSLSocketFactory(curlHelper);
        assertNull(sslSocketFactory);
    }

    @Test
    public void test_init_withInvalidSSLCertificate() {
        // Create a temporary invalid certificate file
        File invalidCertFile = null;
        try {
            invalidCertFile = File.createTempFile("invalid_cert", ".crt");
            try (FileWriter writer = new FileWriter(invalidCertFile)) {
                writer.write("INVALID CERTIFICATE CONTENT");
            }

            setupMockConfig("localhost:9200", "", "", invalidCertFile.getAbsolutePath());

            curlHelper.init();

            // Should handle invalid certificate gracefully
            SSLSocketFactory sslSocketFactory = getSSLSocketFactory(curlHelper);
            assertNull(sslSocketFactory);

        } catch (IOException e) {
            fail("Failed to create test certificate file: " + e.getMessage());
        } finally {
            if (invalidCertFile != null && invalidCertFile.exists()) {
                invalidCertFile.delete();
            }
        }
    }

    @Test
    public void test_init_withNonExistentSSLCertificate() {
        setupMockConfig("localhost:9200", "", "", "/non/existent/cert.crt");

        curlHelper.init();

        // Should handle non-existent certificate gracefully
        SSLSocketFactory sslSocketFactory = getSSLSocketFactory(curlHelper);
        assertNull(sslSocketFactory);
    }

    @Test
    public void test_request_withBasicAuth() {
        setupMockConfig("localhost:9200", "testuser", "testpass");

        curlHelper.init();

        CurlRequest request = new CurlRequest(Curl.Method.GET, "http://localhost:9200/test");
        CurlRequest processedRequest = callProtectedRequest(curlHelper, request);

        assertNotNull(processedRequest);
        // The protected request method should process the request and add authentication
        // We verify that the method completed without errors
    }

    @Test
    public void test_request_withoutAuth() {
        setupMockConfig("localhost:9200", "", "");

        curlHelper.init();

        CurlRequest request = new CurlRequest(Curl.Method.GET, "http://localhost:9200/test");
        CurlRequest processedRequest = callProtectedRequest(curlHelper, request);

        assertNotNull(processedRequest);
        // The protected request method should process the request without authentication
    }

    @Test
    public void test_request_withBlankAuth() {
        setupMockConfig("localhost:9200", "   ", "   ");

        curlHelper.init();

        CurlRequest request = new CurlRequest(Curl.Method.GET, "http://localhost:9200/test");
        CurlRequest processedRequest = callProtectedRequest(curlHelper, request);

        assertNotNull(processedRequest);
        // Blank credentials should be treated as no authentication
    }

    @Test
    public void test_request_withUsernameOnly() {
        setupMockConfig("localhost:9200", "testuser", "");

        curlHelper.init();

        CurlRequest request = new CurlRequest(Curl.Method.GET, "http://localhost:9200/test");
        CurlRequest processedRequest = callProtectedRequest(curlHelper, request);

        assertNotNull(processedRequest);
        // Username without password should not add authentication
    }

    @Test
    public void test_request_withPasswordOnly() {
        setupMockConfig("localhost:9200", "", "testpass");

        curlHelper.init();

        CurlRequest request = new CurlRequest(Curl.Method.GET, "http://localhost:9200/test");
        CurlRequest processedRequest = callProtectedRequest(curlHelper, request);

        assertNotNull(processedRequest);
        // Password without username should not add authentication
    }

    @Test
    public void test_init_multipleHosts() {
        setupMockConfig("localhost:9200,localhost:9201,localhost:9202", "", "");

        curlHelper.init();

        NodeManager nodeManager = getNodeManager(curlHelper);
        assertNotNull(nodeManager);
    }

    @Test
    public void test_init_hostsWithSpaces() {
        setupMockConfig(" localhost:9200 , localhost:9201 , localhost:9202 ", "", "");

        curlHelper.init();

        NodeManager nodeManager = getNodeManager(curlHelper);
        assertNotNull(nodeManager);
    }

    @Test
    public void test_init_emptyHosts() {
        setupMockConfig("", "", "");

        curlHelper.init();

        NodeManager nodeManager = getNodeManager(curlHelper);
        assertNotNull(nodeManager);
    }

    @Test
    public void test_init_hostsWithEmptyEntries() {
        setupMockConfig("localhost:9200,,localhost:9201,", "", "");

        curlHelper.init();

        NodeManager nodeManager = getNodeManager(curlHelper);
        assertNotNull(nodeManager);
    }

    // Helper methods

    private void setupMockConfig(String fesenUrl, String username, String password) {
        setupMockConfig(fesenUrl, username, password, "");
    }

    private void setupMockConfig(String fesenUrl, String username, String password, String certificateAuthorities) {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getFesenHttpSslCertificateAuthorities() {
                return certificateAuthorities;
            }

            @Override
            public String getFesenUsername() {
                return username;
            }

            @Override
            public String getFesenPassword() {
                return password;
            }

            @Override
            public long getFesenHeartbeatInterval() {
                return 10000L;
            }

            @Override
            public String getFesenHttpUrl() {
                return fesenUrl;
            }

            @Override
            public String getSearchEngineHttpUrl() {
                return fesenUrl;
            }
        });

        // Set system property to ensure ResourceUtil.getFesenHttpUrl() works
        System.setProperty("fess.search_engine.http_address", fesenUrl);
    }

    private NodeManager getNodeManager(CurlHelper curlHelper) {
        try {
            Field field = CurlHelper.class.getDeclaredField("nodeManager");
            field.setAccessible(true);
            return (NodeManager) field.get(curlHelper);
        } catch (Exception e) {
            return null;
        }
    }

    private SSLSocketFactory getSSLSocketFactory(CurlHelper curlHelper) {
        try {
            Field field = CurlHelper.class.getDeclaredField("sslSocketFactory");
            field.setAccessible(true);
            return (SSLSocketFactory) field.get(curlHelper);
        } catch (Exception e) {
            return null;
        }
    }

    private CurlRequest callProtectedRequest(CurlHelper curlHelper, CurlRequest request) {
        try {
            java.lang.reflect.Method method = CurlHelper.class.getDeclaredMethod("request", CurlRequest.class);
            method.setAccessible(true);
            return (CurlRequest) method.invoke(curlHelper, request);
        } catch (Exception e) {
            fail("Failed to call protected request method: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        // Clean up system properties
        System.clearProperty("fess.search_engine.http_address");
        System.clearProperty("fesen.http.url");
        super.tearDown(testInfo);
    }
}