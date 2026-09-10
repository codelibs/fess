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

import org.apache.logging.log4j.Level;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.login.FessLoginAssist.LoginCredentialResolver;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;

public class SsoManagerTest extends UnitFessTestCase {

    private SsoManager ssoManager;
    private TestSsoAuthenticator testAuthenticator;
    private String currentSsoType = Constants.NONE;

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
    protected void tearDown(TestInfo testInfo) throws Exception {
        currentSsoType = Constants.NONE;
        super.tearDown(testInfo);
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

    // Test the report for an sso.type no installed plugin serves
    /**
     * The failure this reports used to be silent: a configured sso.type whose authenticator is not
     * registered made every caller answer null, SsoAction turned that into errors.sso_login_error
     * and a redirect, and nothing was logged above debug. The whole symptom was a GET /sso/ that
     * answered 302 to /login/, which no amount of reading the configuration explains -- the
     * configuration is right and a plugin is missing. Splitting the authenticators out of core made
     * that the ordinary state of an upgraded installation.
     */
    @Test
    public void test_getAuthenticator_unservedType_warnsNamingTheComponentAndThePlugin() {
        currentSsoType = "saml";
        ssoManager = newManager();

        final LogCapturingAppender capture = LogCapturingAppender.attach(SsoManager.class);
        try {
            assertNull(ssoManager.getAuthenticator(), "no samlAuthenticator is registered here");
            assertEquals(1, capture.warnings().size());
            final String warning = capture.warnings().get(0);
            assertTrue(warning.contains("samlAuthenticator"), warning);
            assertTrue(warning.contains("sso.type=saml"), warning);
            assertTrue(warning.contains("fess-sso-saml"), warning);
            // ERROR is a notification trigger in Fess, and an unfinished installation is not a fault.
            assertTrue(capture.errors().isEmpty(), "must not be reported at ERROR: " + capture.errors());
        } finally {
            capture.detach();
        }
    }

    /**
     * /sso/ is anonymous and the miss is hit on every visit, so a warning per attempt is a log an
     * unauthenticated client can fill.
     */
    @Test
    public void test_getAuthenticator_unservedType_warnsOnlyOncePerType() {
        currentSsoType = "saml";
        ssoManager = newManager();

        final LogCapturingAppender capture = LogCapturingAppender.attach(SsoManager.class);
        try {
            for (int i = 0; i < 5; i++) {
                assertNull(ssoManager.getAuthenticator(), "attempt " + i);
            }
            assertEquals(1, capture.warnings().size());

            // A different type is a different miss and is worth its own line.
            currentSsoType = "spnego";
            assertNull(ssoManager.getAuthenticator(), "no spnegoAuthenticator is registered here");
            assertEquals(2, capture.warnings().size());
            assertTrue(capture.warnings().get(1).contains("sso.type=spnego"), capture.warnings().get(1));
        } finally {
            capture.detach();
        }
    }

    /**
     * The legacy type maps to entraid before the component name is built, so the plugin the warning
     * names is the one that actually serves it.
     */
    @Test
    public void test_getAuthenticator_unservedAadType_namesTheEntraidPlugin() {
        currentSsoType = "aad";
        ssoManager = newManager();

        final LogCapturingAppender capture = LogCapturingAppender.attach(SsoManager.class);
        try {
            assertNull(ssoManager.getAuthenticator(), "no entraidAuthenticator is registered here");
            assertEquals(1, capture.warnings().size());
            final String warning = capture.warnings().get(0);
            assertTrue(warning.contains("entraidAuthenticator"), warning);
            assertTrue(warning.contains("fess-sso-entraid"), warning);
        } finally {
            capture.detach();
        }
    }

    /**
     * An installation that does not use SSO must stay quiet: /sso/ is reachable whether or not it
     * is configured, so a warning here would be a line per anonymous request on a deployment that
     * has nothing wrong with it.
     */
    @Test
    public void test_getAuthenticator_ssoNotConfigured_saysNothing() {
        final LogCapturingAppender capture = LogCapturingAppender.attach(SsoManager.class.getName(), Level.WARN);
        try {
            for (final String type : new String[] { Constants.NONE, "", "   ", null }) {
                currentSsoType = type;
                ssoManager = newManager();
                assertNull(ssoManager.getAuthenticator(), "type=" + type);
            }
            assertTrue(capture.events().isEmpty(), "nothing to report for an unconfigured sso.type: " + capture.warnings());
        } finally {
            capture.detach();
        }
    }

    /**
     * Core must not ship a fess_sso++.xml. The plugins contribute their authenticators through a
     * file of that name, and the ++ suffix merges every copy on the classpath: one in the war and
     * one in a plugin jar define the same component twice, which makes getComponent throw
     * TooManyRegistrationComponentException and runs the @PostConstruct that calls
     * {@link SsoManager#register} twice. Overriding one from the other is not available either --
     * a redefinition file is named fess_sso+&lt;component&gt;.xml, and a base path containing "+"
     * is what RedefinableComponentTagHandler.redefine() refuses -- so the only workable division is
     * for core to ship none. fess_sso.xml itself has to stay: it is what the plugins merge into.
     */
    @Test
    public void test_coreShipsTheBaseFileAndNoPlusPlusFile() {
        final ClassLoader loader = getClass().getClassLoader();
        assertNotNull(loader.getResource("fess_sso.xml"), "fess_sso.xml is the file the plugins merge into");
        assertNull(loader.getResource("fess_sso++.xml"),
                "fess_sso++.xml belongs to the fess-sso-* plugins; a copy in core collides with theirs. "
                        + "On an incremental build this also fails on a stale target/classes copy left by a build "
                        + "from before the file was deleted -- which a war built from that directory really would ship. "
                        + "mvn clean test settles which one it is.");
    }

    /** An SsoManager whose sso.type is the field the tests set. */
    private SsoManager newManager() {
        return new SsoManager() {
            @Override
            protected String getSsoType() {
                return currentSsoType;
            }
        };
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