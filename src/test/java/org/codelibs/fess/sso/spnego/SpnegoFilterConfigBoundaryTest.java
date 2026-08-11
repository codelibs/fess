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
package org.codelibs.fess.sso.spnego;

import java.io.File;
import java.lang.reflect.Field;

import javax.security.auth.login.Configuration;

import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.spnego.SpnegoFilterConfig;
import org.codelibs.spnego.SpnegoHttpFilter.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Boundary test that actually runs the SPNEGO library's configuration constructor against the
 * values Fess produces.
 *
 * <p>
 * A library regression once made the {@code SpnegoFilterConfig} constructor resolve the configured
 * login.conf location with {@code new File(new URI(loginConfPath))}. Fess hands it a plain absolute
 * file system path, because {@code SpnegoConfig#getResourcePath} resolves a packaged resource with
 * {@code File#getAbsolutePath()}, so the constructor threw
 * {@code IllegalArgumentException: URI is not absolute} and every SPNEGO login failed. Neither the
 * existing Fess tests nor the library's own suite ever executed that constructor, so both builds
 * stayed green while SSO was completely broken.
 * </p>
 *
 * <p>
 * This test crosses the boundary on purpose: it feeds a real {@link SpnegoAuthenticator.SpnegoConfig}
 * into {@link SpnegoFilterConfig#getInstance(jakarta.servlet.FilterConfig)} with JAAS fixtures under
 * {@code src/test/resources/spnego/} and asserts the resulting configuration. The first method pins
 * the plain-path contract that broke; the second pins that a {@code file:} URI is still accepted, so
 * the library fix cannot later be "simplified" into a path-only parser.
 * </p>
 *
 * <p>
 * The whole Fess suite shares one JVM, and both {@code SpnegoFilterConfig} and
 * {@link Configuration} are JVM-wide cached singletons, so this class saves and restores that global
 * state itself rather than relying on any other test to leave it clean.
 * </p>
 */
public class SpnegoFilterConfigBoundaryTest extends UnitFessTestCase {

    /** Fess system property naming the JAAS login configuration resource. */
    private static final String SPNEGO_LOGIN_CONF = "spnego.login.conf";

    /** Fess system property naming the Kerberos configuration resource. */
    private static final String SPNEGO_KRB5_CONF = "spnego.krb5.conf";

    /** Classpath location of the JAAS fixture, namespaced so it cannot shadow Fess's own defaults. */
    private static final String TEST_LOGIN_CONF = "spnego/test_auth_login.conf";

    /** Classpath location of the krb5 fixture (only set as a system property, never parsed here). */
    private static final String TEST_KRB5_CONF = "spnego/test_krb5.conf";

    /** JVM system property the library sets from the login.conf init parameter. */
    private static final String JAAS_CONFIG_PROPERTY = "java.security.auth.login.config";

    /** JVM system property the library sets from the krb5.conf init parameter. */
    private static final String KRB5_CONFIG_PROPERTY = "java.security.krb5.conf";

    /** The library singleton captured before the test replaced it. */
    private Object savedFilterConfigInstance;

    /** The JAAS configuration captured before the test forced a reload. */
    private Configuration savedJaasConfiguration;

    /** The {@value #KRB5_CONFIG_PROPERTY} value captured before the library overwrote it. */
    private String savedKrb5ConfigProperty;

    /** The {@value #JAAS_CONFIG_PROPERTY} value captured before the library overwrote it. */
    private String savedJaasConfigProperty;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);

        // SpnegoFilterConfig caches its instance in a private static field and offers no reset, so
        // the constructor under test would never run a second time in this JVM.
        final Field instanceField = getInstanceField();
        savedFilterConfigInstance = instanceField.get(null);
        instanceField.set(null, null);

        // Configuration is a JVM-wide cached singleton that reads java.security.auth.login.config
        // only on first use. If an earlier test touched JAAS, the library's module lookup would see
        // a stale configuration and fail with "The client module name was not found in the login
        // file". Clearing it forces a reload from the property the library is about to set.
        try {
            savedJaasConfiguration = Configuration.getConfiguration();
        } catch (final Exception | Error e) {
            savedJaasConfiguration = null;
        }
        Configuration.setConfiguration(null);

        savedKrb5ConfigProperty = System.getProperty(KRB5_CONFIG_PROPERTY);
        savedJaasConfigProperty = System.getProperty(JAAS_CONFIG_PROPERTY);

        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        systemProperties.setProperty(SPNEGO_LOGIN_CONF, TEST_LOGIN_CONF);
        systemProperties.setProperty(SPNEGO_KRB5_CONF, TEST_KRB5_CONF);
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        try {
            final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
            systemProperties.remove(SPNEGO_KRB5_CONF);
            systemProperties.remove(SPNEGO_LOGIN_CONF);

            restoreSystemProperty(JAAS_CONFIG_PROPERTY, savedJaasConfigProperty);
            restoreSystemProperty(KRB5_CONFIG_PROPERTY, savedKrb5ConfigProperty);

            Configuration.setConfiguration(savedJaasConfiguration);

            getInstanceField().set(null, savedFilterConfigInstance);
        } finally {
            super.tearDown(testInfo);
        }
    }

    /**
     * Returns the library's private static singleton field, made accessible.
     *
     * <p>
     * The field lives on a class loaded from the classpath (unnamed module), so no
     * {@code --add-opens} is required.
     * </p>
     *
     * @return the accessible {@code SpnegoFilterConfig.instance} field
     * @throws Exception if the field no longer exists
     */
    private static Field getInstanceField() throws Exception {
        final Field field = SpnegoFilterConfig.class.getDeclaredField("instance");
        field.setAccessible(true);
        return field;
    }

    /**
     * Restores a JVM system property, removing it when it was not set before.
     *
     * @param key the property name
     * @param value the captured value, or null if the property was absent
     */
    private static void restoreSystemProperty(final String key, final String value) {
        if (value == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, value);
        }
    }

    /**
     * The regression discriminator: the library must accept the plain absolute path Fess supplies.
     *
     * @throws Exception if the library rejects the configuration
     */
    @Test
    public void test_getInstance_acceptsPlainAbsolutePathFromClasspath() throws Exception {
        final SpnegoAuthenticator.SpnegoConfig config = new SpnegoAuthenticator.SpnegoConfig();

        // Contract: Fess hands the library a plain absolute path, not a file: URI. Pinning this
        // here makes a future library that quietly re-requires a URI fail readably.
        final String loginConf = config.getInitParameter(Constants.LOGIN_CONF);
        assertFalse(loginConf.startsWith("file:"));
        assertTrue(new File(loginConf).isAbsolute());

        // The boundary: this constructor is what the regression broke.
        final SpnegoFilterConfig result = SpnegoFilterConfig.getInstance(config);
        assertNotNull(result);

        // toString() is the only public view of the parsed state.
        final String s = result.toString();
        assertTrue(s.contains("clientLoginModule=spnego-client"));
        assertTrue(s.contains("serverLoginModule=spnego-server"));
        assertTrue(s.contains("canUseKeyTab=true"));
        assertTrue(s.contains("allowBasic=true"));
        assertTrue(s.contains("allowUnsecure=false"));
        assertTrue(s.contains("allowLocalhost=false"));

        // The library forwards the same value to JAAS, which accepts a path as well as a URL.
        assertEquals(loginConf, System.getProperty(JAAS_CONFIG_PROPERTY));
    }

    /**
     * Control: a {@code file:} URI must keep working too, so the fix is not narrowed to paths only.
     *
     * @throws Exception if the library rejects the configuration
     */
    @Test
    public void test_getInstance_alsoAcceptsFileUri() throws Exception {
        final SpnegoAuthenticator.SpnegoConfig config = new SpnegoAuthenticator.SpnegoConfig() {
            @Override
            protected String getResourcePath(final String path) {
                return new File(super.getResourcePath(path)).toURI().toString();
            }
        };

        final String loginConf = config.getInitParameter(Constants.LOGIN_CONF);
        assertTrue(loginConf.startsWith("file:"));

        final SpnegoFilterConfig result = SpnegoFilterConfig.getInstance(config);
        assertNotNull(result);

        final String s = result.toString();
        assertTrue(s.contains("clientLoginModule=spnego-client"));
        assertTrue(s.contains("serverLoginModule=spnego-server"));
        assertTrue(s.contains("canUseKeyTab=true"));

        assertEquals(loginConf, System.getProperty(JAAS_CONFIG_PROPERTY));
    }
}
