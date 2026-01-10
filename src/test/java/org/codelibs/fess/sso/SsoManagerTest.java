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

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.login.FessLoginAssist.LoginCredentialResolver;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class SsoManagerTest extends UnitFessTestCase {

    private SsoManager ssoManager;
    private TestSsoAuthenticator testAuthenticator;
    private String currentSsoType = Constants.NONE;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ssoManager = new SsoManager() {
            @Override
            protected String getSsoType() {
                return currentSsoType;
            }
        };
        testAuthenticator = new TestSsoAuthenticator();
    }

    @Override
    protected void tearDown() throws Exception {
        currentSsoType = Constants.NONE;
        super.tearDown();
    }

    // Test available() method
    @Test
    public void test_available_withNoneSsoType() {
        currentSsoType = Constants.NONE;
        assertFalse(ssoManager.available());
    }

    @Test
    public void test_available_withValidSsoType() {
        currentSsoType = "saml";
        assertTrue(ssoManager.available());
    }

    @Test
    public void test_available_withEmptySsoType() {
        currentSsoType = "";
        assertTrue(ssoManager.available());
    }

    @Test
    public void test_available_withNullSsoType() {
        currentSsoType = null;
        assertTrue(ssoManager.available());
    }

    // Test getLoginCredential() method
    @Test
    public void test_getLoginCredential_whenNotAvailable() {
        currentSsoType = Constants.NONE;
        assertNull(ssoManager.getLoginCredential());
    }

    @Test
    public void test_getLoginCredential_whenAvailableButNoAuthenticator() {
        currentSsoType = "invalid";
        ssoManager = new SsoManager() {
            @Override
            protected String getSsoType() {
                return currentSsoType;
            }

            @Override
            protected SsoAuthenticator getAuthenticator() {
                return null;
            }
        };
        assertNull(ssoManager.getLoginCredential());
    }

    @Test
    public void test_getLoginCredential_withValidAuthenticator() {
        currentSsoType = "test";
        final LoginCredential expectedCredential = new TestLoginCredential("testuser");
        testAuthenticator.setLoginCredential(expectedCredential);

        ssoManager = new SsoManager() {
            @Override
            protected String getSsoType() {
                return currentSsoType;
            }

            @Override
            protected SsoAuthenticator getAuthenticator() {
                return testAuthenticator;
            }
        };

        LoginCredential credential = ssoManager.getLoginCredential();
        assertNotNull(credential);
        assertEquals("testuser", ((TestLoginCredential) credential).username);
    }

    // Test getResponse() method
    @Test
    public void test_getResponse_whenNotAvailable() {
        currentSsoType = Constants.NONE;
        assertNull(ssoManager.getResponse(SsoResponseType.METADATA));
        assertNull(ssoManager.getResponse(SsoResponseType.LOGOUT));
    }

    @Test
    public void test_getResponse_whenAvailableButNoAuthenticator() {
        currentSsoType = "invalid";
        ssoManager = new SsoManager() {
            @Override
            protected String getSsoType() {
                return currentSsoType;
            }

            @Override
            protected SsoAuthenticator getAuthenticator() {
                return null;
            }
        };
        assertNull(ssoManager.getResponse(SsoResponseType.METADATA));
    }

    @Test
    public void test_getResponse_withValidAuthenticator() {
        currentSsoType = "test";
        final ActionResponse expectedResponse = HtmlResponse.asEmptyBody();
        testAuthenticator.setActionResponse(expectedResponse);

        ssoManager = new SsoManager() {
            @Override
            protected String getSsoType() {
                return currentSsoType;
            }

            @Override
            protected SsoAuthenticator getAuthenticator() {
                return testAuthenticator;
            }
        };

        ActionResponse response = ssoManager.getResponse(SsoResponseType.METADATA);
        assertNotNull(response);
        assertEquals(expectedResponse, response);
    }

    @Test
    public void test_getResponse_withNullResponseType() {
        currentSsoType = "test";
        ActionResponse expectedResponse = HtmlResponse.asEmptyBody();
        testAuthenticator.setActionResponse(expectedResponse);

        ssoManager = new SsoManager() {
            @Override
            protected String getSsoType() {
                return currentSsoType;
            }

            @Override
            protected SsoAuthenticator getAuthenticator() {
                return testAuthenticator;
            }
        };

        ActionResponse response = ssoManager.getResponse(null);
        assertNotNull(response);
        assertEquals(expectedResponse, response);
    }

    // Test logout() method
    @Test
    public void test_logout_whenNotAvailable() {
        currentSsoType = Constants.NONE;
        FessUserBean user = FessUserBean.empty();
        assertNull(ssoManager.logout(user));
    }

    @Test
    public void test_logout_whenAvailableButNoAuthenticator() {
        currentSsoType = "invalid";
        ssoManager = new SsoManager() {
            @Override
            protected String getSsoType() {
                return currentSsoType;
            }

            @Override
            protected SsoAuthenticator getAuthenticator() {
                return null;
            }
        };
        FessUserBean user = FessUserBean.empty();
        assertNull(ssoManager.logout(user));
    }

    @Test
    public void test_logout_withValidAuthenticator() {
        currentSsoType = "test";
        final String expectedLogoutUrl = "https://example.com/logout";
        testAuthenticator.setLogoutUrl(expectedLogoutUrl);

        ssoManager = new SsoManager() {
            @Override
            protected String getSsoType() {
                return currentSsoType;
            }

            @Override
            protected SsoAuthenticator getAuthenticator() {
                return testAuthenticator;
            }
        };

        FessUserBean user = FessUserBean.empty();
        String logoutUrl = ssoManager.logout(user);
        assertEquals(expectedLogoutUrl, logoutUrl);
    }

    @Test
    public void test_logout_withNullUser() {
        currentSsoType = "test";
        final String expectedLogoutUrl = "https://example.com/logout";
        testAuthenticator.setLogoutUrl(expectedLogoutUrl);

        ssoManager = new SsoManager() {
            @Override
            protected String getSsoType() {
                return currentSsoType;
            }

            @Override
            protected SsoAuthenticator getAuthenticator() {
                return testAuthenticator;
            }
        };

        String logoutUrl = ssoManager.logout(null);
        assertEquals(expectedLogoutUrl, logoutUrl);
    }

    // Test getAuthenticator() method
    @Test
    public void test_getAuthenticator_whenComponentExists() {
        currentSsoType = "test";
        ComponentUtil.register(testAuthenticator, "testAuthenticator");

        ssoManager = new SsoManager() {
            @Override
            protected String getSsoType() {
                return currentSsoType;
            }
        };

        SsoAuthenticator authenticator = ssoManager.getAuthenticator();
        assertNotNull(authenticator);
        assertEquals(testAuthenticator, authenticator);
    }

    @Test
    public void test_getAuthenticator_whenComponentDoesNotExist() {
        currentSsoType = "nonexistent";
        ssoManager = new SsoManager() {
            @Override
            protected String getSsoType() {
                return currentSsoType;
            }
        };

        SsoAuthenticator authenticator = ssoManager.getAuthenticator();
        assertNull(authenticator);
    }

    // Test register() and getAuthenticators() methods
    @Test
    public void test_register_singleAuthenticator() {
        ssoManager.register(testAuthenticator);
        SsoAuthenticator[] authenticators = ssoManager.getAuthenticators();
        assertEquals(1, authenticators.length);
        assertEquals(testAuthenticator, authenticators[0]);
    }

    @Test
    public void test_register_multipleAuthenticators() {
        TestSsoAuthenticator authenticator1 = new TestSsoAuthenticator();
        TestSsoAuthenticator authenticator2 = new TestSsoAuthenticator();
        TestSsoAuthenticator authenticator3 = new TestSsoAuthenticator();

        ssoManager.register(authenticator1);
        ssoManager.register(authenticator2);
        ssoManager.register(authenticator3);

        SsoAuthenticator[] authenticators = ssoManager.getAuthenticators();
        assertEquals(3, authenticators.length);
        assertEquals(authenticator1, authenticators[0]);
        assertEquals(authenticator2, authenticators[1]);
        assertEquals(authenticator3, authenticators[2]);
    }

    @Test
    public void test_getAuthenticators_withNoRegisteredAuthenticators() {
        SsoAuthenticator[] authenticators = ssoManager.getAuthenticators();
        assertNotNull(authenticators);
        assertEquals(0, authenticators.length);
    }

    // Test getSsoType() with actual FessConfig
    @Test
    public void test_getSsoType_withFessConfig() {
        final String expectedSsoType = "openid";
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getSsoType() {
                return expectedSsoType;
            }
        };
        ComponentUtil.setFessConfig(fessConfig);

        ssoManager = new SsoManager();
        assertEquals(expectedSsoType, ssoManager.getSsoType());
    }

    // Test full integration scenarios
    @Test
    public void test_fullScenario_ssoEnabled() {
        currentSsoType = "saml";
        final LoginCredential expectedCredential = new TestLoginCredential("samluser");
        final ActionResponse expectedResponse = HtmlResponse.asEmptyBody();
        final String expectedLogoutUrl = "https://saml.example.com/logout";

        testAuthenticator.setLoginCredential(expectedCredential);
        testAuthenticator.setActionResponse(expectedResponse);
        testAuthenticator.setLogoutUrl(expectedLogoutUrl);

        ssoManager = new SsoManager() {
            @Override
            protected String getSsoType() {
                return currentSsoType;
            }

            @Override
            protected SsoAuthenticator getAuthenticator() {
                return testAuthenticator;
            }
        };

        // Verify SSO is available
        assertTrue(ssoManager.available());

        // Verify login credential retrieval
        LoginCredential credential = ssoManager.getLoginCredential();
        assertNotNull(credential);
        assertEquals("samluser", ((TestLoginCredential) credential).username);

        // Verify response retrieval
        ActionResponse metadataResponse = ssoManager.getResponse(SsoResponseType.METADATA);
        assertEquals(expectedResponse, metadataResponse);

        // Verify logout
        FessUserBean user = FessUserBean.empty();
        String logoutUrl = ssoManager.logout(user);
        assertEquals(expectedLogoutUrl, logoutUrl);
    }

    @Test
    public void test_fullScenario_ssoDisabled() {
        currentSsoType = Constants.NONE;

        // Verify SSO is not available
        assertFalse(ssoManager.available());

        // Verify all operations return null
        assertNull(ssoManager.getLoginCredential());
        assertNull(ssoManager.getResponse(SsoResponseType.METADATA));
        assertNull(ssoManager.getResponse(SsoResponseType.LOGOUT));
        assertNull(ssoManager.logout(FessUserBean.empty()));
    }

    // Test backward compatibility: aad -> entraid mapping
    @Test
    public void test_getAuthenticator_aadMapsToEntraid() {
        // Register an authenticator with the new "entraid" name
        ComponentUtil.register(testAuthenticator, "entraidAuthenticator");

        // Use legacy "aad" SSO type
        currentSsoType = "aad";
        ssoManager = new SsoManager() {
            @Override
            protected String getSsoType() {
                return currentSsoType;
            }
        };

        // Verify that "aad" SSO type resolves to "entraidAuthenticator"
        SsoAuthenticator authenticator = ssoManager.getAuthenticator();
        assertNotNull(authenticator);
        assertEquals(testAuthenticator, authenticator);
    }

    @Test
    public void test_getAuthenticator_entraidDirectAccess() {
        // Register an authenticator with "entraid" name
        ComponentUtil.register(testAuthenticator, "entraidAuthenticator");

        // Use new "entraid" SSO type
        currentSsoType = "entraid";
        ssoManager = new SsoManager() {
            @Override
            protected String getSsoType() {
                return currentSsoType;
            }
        };

        // Verify that "entraid" SSO type resolves to "entraidAuthenticator"
        SsoAuthenticator authenticator = ssoManager.getAuthenticator();
        assertNotNull(authenticator);
        assertEquals(testAuthenticator, authenticator);
    }

    @Test
    public void test_getLoginCredential_withAadSsoType() {
        final LoginCredential expectedCredential = new TestLoginCredential("entraiduser");
        testAuthenticator.setLoginCredential(expectedCredential);

        // Register authenticator with entraid name
        ComponentUtil.register(testAuthenticator, "entraidAuthenticator");

        // Use legacy "aad" SSO type
        currentSsoType = "aad";
        ssoManager = new SsoManager() {
            @Override
            protected String getSsoType() {
                return currentSsoType;
            }
        };

        // Verify login credential is retrieved correctly via backward compatibility
        LoginCredential credential = ssoManager.getLoginCredential();
        assertNotNull(credential);
        assertEquals("entraiduser", ((TestLoginCredential) credential).username);
    }

    // Helper classes for testing
    private static class TestLoginCredential implements LoginCredential {
        private final String username;

        public TestLoginCredential(String username) {
            this.username = username;
        }

        @Override
        public String toString() {
            return username;
        }
    }

    private static class TestSsoAuthenticator implements SsoAuthenticator {
        private LoginCredential loginCredential;
        private ActionResponse actionResponse;
        private String logoutUrl;

        public void setLoginCredential(LoginCredential loginCredential) {
            this.loginCredential = loginCredential;
        }

        public void setActionResponse(ActionResponse actionResponse) {
            this.actionResponse = actionResponse;
        }

        public void setLogoutUrl(String logoutUrl) {
            this.logoutUrl = logoutUrl;
        }

        @Override
        public LoginCredential getLoginCredential() {
            return loginCredential;
        }

        @Override
        public void resolveCredential(LoginCredentialResolver resolver) {
            // Not used in these tests
        }

        @Override
        public ActionResponse getResponse(SsoResponseType responseType) {
            return actionResponse;
        }

        @Override
        public String logout(FessUserBean user) {
            return logoutUrl;
        }
    }
}