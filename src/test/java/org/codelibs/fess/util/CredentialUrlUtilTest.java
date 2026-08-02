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

import java.net.URISyntaxException;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class CredentialUrlUtilTest extends UnitFessTestCase {

    // ========== hasUserInfo() ==========

    @Test
    public void test_hasUserInfo_detectsUserinfoAuthority() {
        assertTrue(CredentialUrlUtil.hasUserInfo("https://user:pass@gw.example.com/v1"));
        assertTrue(CredentialUrlUtil.hasUserInfo("http://user:pass@gw.example.com:8080/v1"));
        assertTrue(CredentialUrlUtil.hasUserInfo("https://user@gw.example.com/v1"), "userinfo without a password still counts");
        assertTrue(CredentialUrlUtil.hasUserInfo("https://user:pass@gw.example.com"), "no path after the authority");
        assertTrue(CredentialUrlUtil.hasUserInfo("https://user:pass@gw.example.com?key=K"), "authority ends at '?'");
        assertTrue(CredentialUrlUtil.hasUserInfo("https://user:pass@gw.example.com#frag"), "authority ends at '#'");
    }

    // The masking pattern excludes whitespace from both userinfo segments, so it cannot see this
    // value at all - which is exactly why refusal, not masking, is what a URL's safety rests on.
    @Test
    public void test_hasUserInfo_detectsCredentialContainingWhitespace() {
        assertTrue(CredentialUrlUtil.hasUserInfo("https://user:pw x@gw.example.com/v1"));
        // Precondition: masking genuinely cannot handle this value, so hasUserInfo must.
        assertEquals("https://user:pw x@gw.example.com/v1", CredentialUrlUtil.maskCredentialInUrl("https://user:pw x@gw.example.com/v1"));
    }

    // Each of these was missed by at least one of the four implementations this class replaces.
    @Test
    public void test_hasUserInfo_casesTheSeparateImplementationsDisagreedOn() {
        // Protocol-relative: missed by any rule requiring "://".
        assertTrue(CredentialUrlUtil.hasUserInfo("//user:pass@gw.example.com/v1"));
        // Scheme-less: not a valid absolute URI, but it is a credential typed into a URL setting.
        assertTrue(CredentialUrlUtil.hasUserInfo("user:pass@gw.example.com"));
        assertTrue(CredentialUrlUtil.hasUserInfo("user:pass@gw.example.com:9200"));
        // A scheme whose name is not strictly RFC 3986 must still have its authority scanned.
        assertTrue(CredentialUrlUtil.hasUserInfo("ht tp://user:pass@gw.example.com/v1"));
        // An empty userinfo still carries the forbidden delimiter, and an '@' inside the credential
        // must not hide the one that delimits it.
        assertTrue(CredentialUrlUtil.hasUserInfo("http://@host"));
        assertTrue(CredentialUrlUtil.hasUserInfo("http://user:p@ss@host/api/chat"));
        // A "://" appearing after a path character is not a scheme delimiter, so what follows it
        // is not an authority.
        assertFalse(CredentialUrlUtil.hasUserInfo("foo/bar://user:pass@gw.example.com"));
    }

    @Test
    public void test_hasUserInfo_rejectsNonCredentialUrls() {
        assertFalse(CredentialUrlUtil.hasUserInfo(null));
        assertFalse(CredentialUrlUtil.hasUserInfo(""));
        assertFalse(CredentialUrlUtil.hasUserInfo("   "));
        assertFalse(CredentialUrlUtil.hasUserInfo("http://localhost:9200"), "host:port is not userinfo");
        assertFalse(CredentialUrlUtil.hasUserInfo("https://gw.example.com/v1beta"));
        assertFalse(CredentialUrlUtil.hasUserInfo("https://gw.example.com/v1beta/a:b@c"), "'@' in the path is not userinfo");
        assertFalse(CredentialUrlUtil.hasUserInfo("https://gw.example.com/v1?to=a@b"), "'@' in the query is not userinfo");
        assertFalse(CredentialUrlUtil.hasUserInfo("https://gw.example.com/v1#a@b"), "'@' in the fragment is not userinfo");
        assertFalse(CredentialUrlUtil.hasUserInfo("localhost:9200"), "scheme-less host:port carries no credential");
        assertFalse(CredentialUrlUtil.hasUserInfo("/v1/models"), "a bare path has no authority");
        assertFalse(CredentialUrlUtil.hasUserInfo("http://[::1]:9200"), "an IPv6 literal authority carries no userinfo");
        assertFalse(CredentialUrlUtil.hasUserInfo("https://gw.example.com/v1?next=https://u:p@other.example"),
                "a second '://' inside the query is not this URL's authority");
    }

    // A whitespace-padded configuration value must not defeat the check.
    @Test
    public void test_hasUserInfo_trimsInput() {
        assertTrue(CredentialUrlUtil.hasUserInfo("  https://user:pass@gw.example.com/v1  "));
        assertTrue(CredentialUrlUtil.hasUserInfo("\t//user:pass@gw.example.com\n"));
    }

    // ========== maskCredentialInUrl() ==========

    @Test
    public void test_maskCredentialInUrl_masksQueryParameters() {
        assertEquals("https://host/v1?key=***", CredentialUrlUtil.maskCredentialInUrl("https://host/v1?key=AIzaSecret"));
        assertEquals("https://host/v1?key=***&alt=json", CredentialUrlUtil.maskCredentialInUrl("https://host/v1?key=AIzaSecret&alt=json"));
        assertEquals("https://host/v1?alt=json&key=***", CredentialUrlUtil.maskCredentialInUrl("https://host/v1?alt=json&key=AIzaSecret"));
        // Parameter names are matched case-insensitively.
        assertEquals("https://host/v1?KEY=***", CredentialUrlUtil.maskCredentialInUrl("https://host/v1?KEY=AIzaSecret"));
        assertEquals("https://host/v1?api_key=***&token=***&access_token=***",
                CredentialUrlUtil.maskCredentialInUrl("https://host/v1?api_key=s1&token=s2&access_token=s3"));
        assertEquals("https://host/v1?api-key=***&access-token=***",
                CredentialUrlUtil.maskCredentialInUrl("https://host/v1?api-key=s1&access-token=s2"));
        assertEquals("https://host/v1?apikey=***", CredentialUrlUtil.maskCredentialInUrl("https://host/v1?apikey=s1"));
        assertEquals("https://host/v1?a=1&API_KEY=***&b=2", CredentialUrlUtil.maskCredentialInUrl("https://host/v1?a=1&API_KEY=zzz&b=2"));
    }

    @Test
    public void test_maskCredentialInUrl_masksUserinfo() {
        assertEquals("https://***:***@gw.example.com/v1beta",
                CredentialUrlUtil.maskCredentialInUrl("https://user:pass@gw.example.com/v1beta"));
        assertEquals("http://***:***@gw.example.com:8080/v1beta",
                CredentialUrlUtil.maskCredentialInUrl("http://user:pass@gw.example.com:8080/v1beta"));
        // Both rules apply to the same URL.
        assertEquals("https://***:***@gw.example.com/v1beta?key=***",
                CredentialUrlUtil.maskCredentialInUrl("https://user:pass@gw.example.com/v1beta?key=AIzaSecret"));
    }

    @Test
    public void test_maskCredentialInUrl_leavesInnocuousUrlsAlone() {
        assertNull(CredentialUrlUtil.maskCredentialInUrl(null));
        assertEquals("", CredentialUrlUtil.maskCredentialInUrl(""));
        assertEquals("https://host/v1?alt=json", CredentialUrlUtil.maskCredentialInUrl("https://host/v1?alt=json"));
        assertEquals("https://host/v1beta", CredentialUrlUtil.maskCredentialInUrl("https://host/v1beta"));
        // host:port must not be mistaken for userinfo, and '@' in the path must not be masked.
        assertEquals("http://localhost:8080/v1beta/models", CredentialUrlUtil.maskCredentialInUrl("http://localhost:8080/v1beta/models"));
        assertEquals("https://gw.example.com/v1beta/a:b@c", CredentialUrlUtil.maskCredentialInUrl("https://gw.example.com/v1beta/a:b@c"));
        // A parameter name that merely contains a secret-ish substring must not be masked: the
        // pattern anchors on '?' or '&', so "monkey" and "keyword" are left alone.
        assertEquals("https://host/v1?monkey=1", CredentialUrlUtil.maskCredentialInUrl("https://host/v1?monkey=1"));
        assertEquals("https://host/v1?keyword=1", CredentialUrlUtil.maskCredentialInUrl("https://host/v1?keyword=1"));
    }

    // ========== invalidUrlException() ==========

    @Test
    public void test_invalidUrlException_carriesNeitherUrlNorCause() {
        final String secret = "s3cr3t";
        final URISyntaxException syntax =
                new URISyntaxException("https://user:" + secret + "@host/v1", "Illegal character in authority", 13);
        final IllegalArgumentException original = new IllegalArgumentException(syntax.getMessage(), syntax);

        final IllegalArgumentException replacement = CredentialUrlUtil.invalidUrlException("rag.llm.example.api.url", original);

        assertFalse(replacement.getMessage().contains(secret), "the replacement must not quote the URL back: " + replacement.getMessage());
        assertNull(replacement.getCause(), "no cause, so nothing downstream can recover the raw URI");
        assertTrue(replacement.getMessage().contains("rag.llm.example.api.url"), "the setting to inspect must be named");
        assertTrue(replacement.getMessage().contains("Illegal character in authority"));
        assertTrue(replacement.getMessage().contains("13"), "the parser's index is safe to carry");
    }

    @Test
    public void test_invalidUrlException_toleratesMissingDetail() {
        assertEquals("Invalid URL configured in a.b.url", CredentialUrlUtil.invalidUrlException("a.b.url", null).getMessage());
        // A non-URISyntaxException cause contributes nothing, since its message may hold the URL.
        assertEquals("Invalid URL configured in a.b.url",
                CredentialUrlUtil.invalidUrlException("a.b.url", new IllegalArgumentException("boom")).getMessage());
        // A negative index is omitted rather than printed.
        final URISyntaxException noIndex = new URISyntaxException("input", "Expected scheme name");
        assertEquals("Invalid URL configured in a.b.url: Expected scheme name",
                CredentialUrlUtil.invalidUrlException("a.b.url", new IllegalArgumentException("m", noIndex)).getMessage());
    }
}
