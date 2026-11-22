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

import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.SimpleMessage;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class CredentialMaskRewritePolicyTest extends UnitFessTestCase {

    private static final String WIRE = "org.apache.hc.client5.http.wire";

    private LogEvent event(final String loggerName, final String message) {
        return Log4jLogEvent.newBuilder().setLoggerName(loggerName).setLevel(Level.DEBUG).setMessage(new SimpleMessage(message)).build();
    }

    @Test
    public void test_mask_userinfo() {
        assertEquals("http-outgoing-0 << \"Location: http://host:8080/a?b=c[\\r][\\n]\"",
                CredentialMaskRewritePolicy.mask("http-outgoing-0 << \"Location: http://user:pa$$w0rd@host:8080/a?b=c[\\r][\\n]\""));
        assertEquals("<a href=\"https://host/\">x</a>", CredentialMaskRewritePolicy.mask("<a href=\"https://user:pw@host/\">x</a>"));
        assertEquals("smb://host/share", CredentialMaskRewritePolicy.mask("smb://dom;user:pw@host/share"));
        assertEquals("ftp://host/", CredentialMaskRewritePolicy.mask("ftp://user@host/"));
    }

    @Test
    public void test_mask_userinfo_keeps_other_text() {
        for (final String text : new String[] { "http://host/path?mail=a@b.example", "http://host?mail=a@b.example",
                "mail me at a@b.example", "http://host/a@b", "no credentials here" }) {
            assertEquals(text, CredentialMaskRewritePolicy.mask(text));
        }
    }

    @Test
    public void test_mask_authorization() {
        assertEquals("http-outgoing-0 >> Authorization: Basic ****",
                CredentialMaskRewritePolicy.mask("http-outgoing-0 >> Authorization: Basic dXNlcjpwYXNzd29yZA=="));
        assertEquals("http-outgoing-0 >> \"authorization: Bearer ****[\\r][\\n]\"",
                CredentialMaskRewritePolicy.mask("http-outgoing-0 >> \"authorization: Bearer abc.def.ghi[\\r][\\n]\""));
        assertEquals("http-outgoing-0 >> Proxy-Authorization: Basic ****",
                CredentialMaskRewritePolicy.mask("http-outgoing-0 >> Proxy-Authorization: Basic dXNlcjpwYXNzd29yZA=="));
        assertEquals("http-outgoing-0 >> \"Authorization: Digest ****[\\r][\\n]\"",
                CredentialMaskRewritePolicy.mask("http-outgoing-0 >> \"Authorization: Digest username=\"u\", response=\"x\"[\\r][\\n]\""));
        assertEquals("http-outgoing-0 >> User-Agent: test", CredentialMaskRewritePolicy.mask("http-outgoing-0 >> User-Agent: test"));
    }

    @Test
    public void test_mask_null() {
        assertNull(CredentialMaskRewritePolicy.mask(null));
    }

    @Test
    public void test_rewrite_matching_logger() {
        final CredentialMaskRewritePolicy policy = new CredentialMaskRewritePolicy(new String[] { WIRE });
        final LogEvent event = event(WIRE, "http-outgoing-0 << \"Location: http://user:pw@host/[\\r][\\n]\"");

        final LogEvent result = policy.rewrite(event);

        assertNotSame(event, result);
        assertEquals(WIRE, result.getLoggerName());
        assertEquals(Level.DEBUG, result.getLevel());
        assertEquals("http-outgoing-0 << \"Location: http://host/[\\r][\\n]\"", result.getMessage().getFormattedMessage());
    }

    @Test
    public void test_rewrite_parameterized_message() {
        final CredentialMaskRewritePolicy policy = new CredentialMaskRewritePolicy(new String[] { WIRE });
        final LogEvent event = Log4jLogEvent.newBuilder()
                .setLoggerName(WIRE)
                .setLevel(Level.DEBUG)
                .setMessage(new ParameterizedMessage("{} >> {}", "http-outgoing-0", "Authorization: Basic dXNlcjpwdw=="))
                .build();

        assertEquals("http-outgoing-0 >> Authorization: Basic ****", policy.rewrite(event).getMessage().getFormattedMessage());
    }

    @Test
    public void test_rewrite_other_logger_is_untouched() {
        final CredentialMaskRewritePolicy policy = new CredentialMaskRewritePolicy(new String[] { WIRE });
        final LogEvent event = event("org.codelibs.fess.crawler", "http://user:pw@host/");

        assertSame(event, policy.rewrite(event));
    }

    @Test
    public void test_rewrite_message_without_credentials_is_untouched() {
        final CredentialMaskRewritePolicy policy = new CredentialMaskRewritePolicy(new String[] { WIRE });
        final LogEvent event = event(WIRE, "http-outgoing-0 >> GET /robots.txt HTTP/1.1");

        assertSame(event, policy.rewrite(event));
    }

    @Test
    public void test_rewrite_null_logger_name() {
        final CredentialMaskRewritePolicy policy = new CredentialMaskRewritePolicy(new String[] { WIRE });
        final LogEvent event = event(null, "http://user:pw@host/");

        assertSame(event, policy.rewrite(event));
    }

    @Test
    public void test_createPolicy() {
        final CredentialMaskRewritePolicy policy = CredentialMaskRewritePolicy.createPolicy(" " + WIRE + " , ,x.headers");
        final LogEvent event = event("x.headers.sub", "http://user:pw@host/");

        assertEquals("http://host/", policy.rewrite(event).getMessage().getFormattedMessage());
        assertSame(event, CredentialMaskRewritePolicy.createPolicy(null).rewrite(event));
    }
}
