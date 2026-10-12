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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.FileAuthenticationService;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.client.ftp.FtpAuthentication;
import org.codelibs.fess.crawler.client.smb.SmbAuthentication;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.mylasta.direction.FessProp;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.Param;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ParameterUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.core.security.exception.CipherFailureException;

public class FileConfigAuthenticationTest extends UnitFessTestCase {

    private static final String UNDECRYPTABLE_VALUE = "00112233445566778899aabbccddeeff";

    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

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

    private FileAuthentication newAuthentication(final String scheme, final String password) {
        final FileAuthentication auth = new FileAuthentication();
        auth.setId("a1");
        auth.setFileConfigId("f1");
        auth.setProtocolScheme(scheme);
        auth.setHostname("host");
        auth.setUsername("user");
        auth.setPassword(password);
        return auth;
    }

    private Map<String, Object> initialize(final FileAuthentication... auths) {
        ComponentUtil.register(new FileAuthenticationService() {
            @Override
            public List<FileAuthentication> getFileAuthenticationList(final String fileConfigId) {
                return Arrays.asList(auths);
            }
        }, FileAuthenticationService.class.getCanonicalName());
        final FileConfig config = new FileConfig();
        config.setId("f1");
        final Object[] holder = new Object[1];
        config.initializeClientFactory(() -> new CrawlerClientFactory() {
            @Override
            public void setInitParameterMap(final Map<String, Object> params) {
                holder[0] = params;
            }
        });
        @SuppressWarnings("unchecked")
        final Map<String, Object> paramMap = (Map<String, Object>) holder[0];
        return paramMap;
    }

    // The stored passwords are encrypted, and the crawler clients get the plain text.
    @Test
    public void test_initializeClientFactory_decryptsStoredPasswords() {
        final String stored = ParameterUtil.encryptCredential("s3cret");
        final Map<String, Object> params = initialize(newAuthentication(Constants.SAMBA, stored), newAuthentication(Constants.FTP, stored));

        final SmbAuthentication[] smb = (SmbAuthentication[]) params.get(Param.Client.SMB_AUTHENTICATIONS);
        assertEquals(1, smb.length);
        assertEquals("s3cret", smb[0].getPassword());
        final org.codelibs.fess.crawler.client.smb1.SmbAuthentication[] smb1 =
                (org.codelibs.fess.crawler.client.smb1.SmbAuthentication[]) params.get(Param.Client.SMB1_AUTHENTICATIONS);
        assertEquals("s3cret", smb1[0].getPassword());
        final FtpAuthentication[] ftp = (FtpAuthentication[]) params.get(Param.Client.FTP_AUTHENTICATIONS);
        assertEquals("s3cret", ftp[0].getPassword());
    }

    // A password saved before it was encrypted keeps working as it is.
    @Test
    public void test_initializeClientFactory_legacyPlainTextPassword() {
        final Map<String, Object> params =
                initialize(newAuthentication(Constants.SAMBA, "legacy"), newAuthentication(Constants.FTP, "legacy"));

        assertEquals("legacy", ((SmbAuthentication[]) params.get(Param.Client.SMB_AUTHENTICATIONS))[0].getPassword());
        assertEquals("legacy", ((FtpAuthentication[]) params.get(Param.Client.FTP_AUTHENTICATIONS))[0].getPassword());
    }

    // A password encrypted with another app.cipher.key must fail, not authenticate with the ciphertext.
    @Test
    public void test_initializeClientFactory_undecryptablePassword_namesOwnerAndKey() {
        for (final String scheme : new String[] { Constants.SAMBA, Constants.FTP }) {
            final FileAuthentication auth = newAuthentication(scheme, "{cipher}" + UNDECRYPTABLE_VALUE);
            final CipherFailureException e = Assertions.assertThrows(CipherFailureException.class, () -> initialize(auth));
            final String message = e.getMessage();
            assertTrue(message, message.contains("file authentication: id=a1, fileConfigId=f1"));
            assertTrue(message, message.contains("field=password"));
            assertTrue(message, message.contains("app.cipher.key"));
            assertFalse(message, message.contains(UNDECRYPTABLE_VALUE));
        }
    }
}
