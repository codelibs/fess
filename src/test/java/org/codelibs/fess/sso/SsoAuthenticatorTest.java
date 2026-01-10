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
package org.codelibs.fess.sso;

import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.JsonResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class SsoAuthenticatorTest extends UnitFessTestCase {

    private TestSsoAuthenticator authenticator;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        authenticator = new TestSsoAuthenticator();
    }

    // Mock LoginCredentialResolver for testing
    private static class TestLoginCredentialResolver extends FessLoginAssist.LoginCredentialResolver {
        private boolean resolveCalled = false;
        private Class<?> lastCredentialType;
        private Function<?, ?> lastFunction;

        public TestLoginCredentialResolver() {
            super(null); // Pass null since we're mocking
        }

        @Override
        public <CREDENTIAL extends LoginCredential> void resolve(final Class<CREDENTIAL> credentialType,
                final Function<CREDENTIAL, OptionalEntity<FessUser>> oneArgLambda) {
            this.resolveCalled = true;
            this.lastCredentialType = credentialType;
            this.lastFunction = oneArgLambda;
        }

        public boolean isResolveCalled() {
            return resolveCalled;
        }

        public Class<?> getLastCredentialType() {
            return lastCredentialType;
        }

        public Function<?, ?> getLastFunction() {
            return lastFunction;
        }
    }

    // Inner class for test credential
    private static class TestLoginCredential implements LoginCredential {
        private final String username;
        private final String password;

        public TestLoginCredential(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    // Test getLoginCredential method
    @Test
    public void test_getLoginCredential_returnsValidCredential() {
        // Setup test credential
        TestLoginCredential expectedCredential = new TestLoginCredential("testuser", "testpass");
        authenticator.setLoginCredential(expectedCredential);

        // Execute
        LoginCredential result = authenticator.getLoginCredential();

        // Verify
        assertNotNull(result);
        assertEquals(expectedCredential, result);
        assertEquals("testuser", ((TestLoginCredential) result).getUsername());
        assertEquals("testpass", ((TestLoginCredential) result).getPassword());
    }

    @Test
    public void test_getLoginCredential_returnsNull() {
        // Setup
        authenticator.setLoginCredential(null);

        // Execute
        LoginCredential result = authenticator.getLoginCredential();

        // Verify
        assertNull(result);
    }

    // Test resolveCredential method
    @Test
    public void test_resolveCredential_withValidResolver() {
        // Setup
        TestLoginCredentialResolver resolver = new TestLoginCredentialResolver();

        // Execute
        authenticator.resolveCredential(resolver);

        // Verify
        assertTrue(authenticator.isResolverCalled());
        assertNotNull(authenticator.getLastResolver());
    }

    @Test
    public void test_resolveCredential_withNullResolver() {
        // Execute
        authenticator.resolveCredential(null);

        // Verify
        assertTrue(authenticator.isResolverCalled());
        assertNull(authenticator.getLastResolver());
    }

    @Test
    public void test_resolveCredential_multipleResolvers() {
        // Setup
        TestLoginCredentialResolver resolver1 = new TestLoginCredentialResolver();
        TestLoginCredentialResolver resolver2 = new TestLoginCredentialResolver();

        // Execute
        authenticator.resolveCredential(resolver1);
        authenticator.resolveCredential(resolver2);

        // Verify
        assertTrue(authenticator.isResolverCalled());
        assertNotNull(authenticator.getLastResolver());
    }

    // Test getResponse method with different response types
    @Test
    public void test_getResponse_withMetadataType() {
        // Setup - Test with null response initially
        ActionResponse result = authenticator.getResponse(SsoResponseType.METADATA);

        // Verify initial state
        assertNull(result);

        // Setup with JSON response (easier to create without LastaFlute context)
        JsonResponse<?> expectedResponse = new JsonResponse<>(new HashMap<>());
        authenticator.setResponseForType(SsoResponseType.METADATA, expectedResponse);

        // Execute
        result = authenticator.getResponse(SsoResponseType.METADATA);

        // Verify
        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    public void test_getResponse_withLogoutType() {
        // Setup
        JsonResponse<?> expectedResponse = new JsonResponse<>(new HashMap<>());
        authenticator.setResponseForType(SsoResponseType.LOGOUT, expectedResponse);

        // Execute
        ActionResponse result = authenticator.getResponse(SsoResponseType.LOGOUT);

        // Verify
        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }

    @Test
    public void test_getResponse_withNullType() {
        // Execute
        ActionResponse result = authenticator.getResponse(null);

        // Verify
        assertNull(result);
    }

    @Test
    public void test_getResponse_withUnsetType() {
        // Execute - requesting type without setting response
        ActionResponse result = authenticator.getResponse(SsoResponseType.METADATA);

        // Verify
        assertNull(result);
    }

    // Test logout method
    @Test
    public void test_logout_withValidUser() {
        // Setup
        FessUserBean user = FessUserBean.empty();
        String expectedUrl = "https://example.com/logout?user=testuser123";
        authenticator.setLogoutUrl(expectedUrl);

        // Execute
        String result = authenticator.logout(user);

        // Verify
        assertNotNull(result);
        assertEquals(expectedUrl, result);
        assertEquals(user, authenticator.getLastLogoutUser());
    }

    @Test
    public void test_logout_withNullUser() {
        // Setup
        authenticator.setLogoutUrl("https://example.com/logout");

        // Execute
        String result = authenticator.logout(null);

        // Verify
        assertNotNull(result);
        assertEquals("https://example.com/logout", result);
        assertNull(authenticator.getLastLogoutUser());
    }

    @Test
    public void test_logout_returnsNull() {
        // Setup
        FessUserBean user = FessUserBean.empty();
        authenticator.setLogoutUrl(null);

        // Execute
        String result = authenticator.logout(user);

        // Verify
        assertNull(result);
        assertEquals(user, authenticator.getLastLogoutUser());
    }

    @Test
    public void test_logout_withEmptyString() {
        // Setup
        FessUserBean user = FessUserBean.empty();
        authenticator.setLogoutUrl("");

        // Execute
        String result = authenticator.logout(user);

        // Verify
        assertEquals("", result);
        assertEquals(user, authenticator.getLastLogoutUser());
    }

    // Test complete workflow
    @Test
    public void test_completeAuthenticationFlow() {
        // Setup
        TestLoginCredential credential = new TestLoginCredential("user", "pass");
        authenticator.setLoginCredential(credential);

        HtmlResponse metadataResponse = null; // Would need proper LastaFlute context to create
        authenticator.setResponseForType(SsoResponseType.METADATA, metadataResponse);

        String logoutUrl = "https://sso.example.com/logout";
        authenticator.setLogoutUrl(logoutUrl);

        // Execute authentication flow
        LoginCredential loginResult = authenticator.getLoginCredential();
        assertNotNull(loginResult);

        TestLoginCredentialResolver resolver = new TestLoginCredentialResolver();
        authenticator.resolveCredential(resolver);
        assertTrue(authenticator.isResolverCalled());

        ActionResponse metaResult = authenticator.getResponse(SsoResponseType.METADATA);
        assertEquals(metadataResponse, metaResult);

        FessUserBean user = FessUserBean.empty();
        String logoutResult = authenticator.logout(user);
        assertEquals(logoutUrl, logoutResult);
    }

    // Test implementation of SsoAuthenticator for testing
    private static class TestSsoAuthenticator implements SsoAuthenticator {
        private LoginCredential loginCredential;
        private FessLoginAssist.LoginCredentialResolver lastResolver;
        private boolean resolverCalled = false;
        private Map<SsoResponseType, ActionResponse> responseMap = new HashMap<>();
        private String logoutUrl;

        public void setLoginCredential(LoginCredential credential) {
            this.loginCredential = credential;
        }

        public void setResponseForType(SsoResponseType type, ActionResponse response) {
            responseMap.put(type, response);
        }

        public void setLogoutUrl(String url) {
            this.logoutUrl = url;
        }

        public boolean isResolverCalled() {
            return resolverCalled;
        }

        public FessLoginAssist.LoginCredentialResolver getLastResolver() {
            return lastResolver;
        }

        public FessUserBean getLastLogoutUser() {
            return lastLogoutUser;
        }

        private FessUserBean lastLogoutUser;

        @Override
        public LoginCredential getLoginCredential() {
            return loginCredential;
        }

        @Override
        public void resolveCredential(FessLoginAssist.LoginCredentialResolver resolver) {
            this.lastResolver = resolver;
            this.resolverCalled = true;
        }

        @Override
        public ActionResponse getResponse(SsoResponseType responseType) {
            return responseMap.get(responseType);
        }

        @Override
        public String logout(FessUserBean userBean) {
            this.lastLogoutUser = userBean;
            return logoutUrl;
        }
    }
}