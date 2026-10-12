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
package org.codelibs.fess.opensearch.config.exentity;

import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.client.http.config.WebAuthenticationConfig;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.mylasta.direction.FessProp;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ParameterUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.core.security.exception.CipherFailureException;

public class WebAuthenticationTest extends UnitFessTestCase {

    private static final String UNDECRYPTABLE_VALUE = "00112233445566778899aabbccddeeff";

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        FessProp.propMap.clear();
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getAppEncryptPropertyPattern() {
                return ".*password|.*key|.*secret";
            }
        });
    }

    private WebAuthentication newAuthentication(final String password) {
        final WebAuthentication auth = new WebAuthentication();
        auth.setId("abc123");
        auth.setWebConfigId("cfg1");
        auth.setUsername("user");
        auth.setProtocolScheme(Constants.BASIC);
        auth.setPassword(password);
        return auth;
    }

    // The stored password is encrypted, and the crawler gets the plain text.
    @Test
    public void test_getWebAuthenticationConfig_decryptsStoredPassword() {
        final WebAuthenticationConfig config = newAuthentication(ParameterUtil.encryptCredential("s3cret")).getWebAuthenticationConfig();
        assertEquals("s3cret", config.getCredentials().getPassword());
    }

    // A password saved before it was encrypted keeps working as it is.
    @Test
    public void test_getWebAuthenticationConfig_legacyPlainTextPassword() {
        final WebAuthenticationConfig config = newAuthentication("legacy").getWebAuthenticationConfig();
        assertEquals("legacy", config.getCredentials().getPassword());
    }

    @Test
    public void test_getWebAuthenticationConfig_noPassword() {
        assertEquals("", newAuthentication(null).getWebAuthenticationConfig().getCredentials().getPassword());
        assertEquals("", newAuthentication("").getWebAuthenticationConfig().getCredentials().getPassword());
    }

    // A password encrypted with another app.cipher.key must fail, not authenticate with the ciphertext.
    @Test
    public void test_getWebAuthenticationConfig_undecryptablePassword_namesOwnerAndKey() {
        final WebAuthentication auth = newAuthentication("{cipher}" + UNDECRYPTABLE_VALUE);
        final CipherFailureException e = Assertions.assertThrows(CipherFailureException.class, auth::getWebAuthenticationConfig);
        final String message = e.getMessage();
        assertTrue(message, message.contains("web authentication: id=abc123, webConfigId=cfg1"));
        assertTrue(message, message.contains("field=password"));
        assertTrue(message, message.contains("app.cipher.key"));
        assertFalse(message, message.contains(UNDECRYPTABLE_VALUE));
    }
}
