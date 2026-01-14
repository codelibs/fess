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

import org.codelibs.fess.helper.UserAgentHelper.UserAgentType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class UserAgentHelperTest extends UnitFessTestCase {

    public UserAgentHelper userAgentHelper;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        userAgentHelper = new UserAgentHelper();
    }

    @Test
    public void test_getUserAgentType_IE9() {
        getMockRequest().addHeader("user-agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0; Trident/5.0)");
        assertEquals(UserAgentType.IE, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_IE10() {
        getMockRequest().addHeader("user-agent", "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)");
        assertEquals(UserAgentType.IE, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_IE11() {
        getMockRequest().addHeader("user-agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko)");
        assertEquals(UserAgentType.IE, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_Chrome() {
        getMockRequest().addHeader("user-agent",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.52 Safari/537.36");
        assertEquals(UserAgentType.CHROME, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_FireFox() {
        getMockRequest().addHeader("user-agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:42.0) Gecko/20100101 Firefox/42.0");
        assertEquals(UserAgentType.FIREFOX, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_Safari() {
        getMockRequest().addHeader("user-agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/600.8.9 (KHTML, like Gecko) Version/8.0.8 Safari/600.8.9");
        assertEquals(UserAgentType.SAFARI, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_Opera() {
        getMockRequest().addHeader("user-agent", "Opera/9.80 (X11; U; Linux x86_64) Presto/2.9.181 Version/12.00");
        assertEquals(UserAgentType.OPERA, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_OTHER() {
        getMockRequest().addHeader("user-agent", "Dummy");
        assertEquals(UserAgentType.OTHER, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_nullUserAgent() {
        getMockRequest().addHeader("user-agent", null);
        assertEquals(UserAgentType.OTHER, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_emptyUserAgent() {
        getMockRequest().addHeader("user-agent", "");
        assertEquals(UserAgentType.OTHER, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_noUserAgentHeader() {
        assertEquals(UserAgentType.OTHER, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_multipleDetections() {
        getMockRequest().addHeader("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        assertEquals(UserAgentType.CHROME, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_safariWithoutChrome() {
        getMockRequest().addHeader("user-agent",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Mobile/15E148 Safari/604.1");
        assertEquals(UserAgentType.SAFARI, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_operaModern() {
        getMockRequest().addHeader("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 OPR/77.0.4054.203");
        assertEquals(UserAgentType.CHROME, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_operaClassic() {
        getMockRequest().addHeader("user-agent", "Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14");
        assertEquals(UserAgentType.OPERA, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_edgeWithChrome() {
        getMockRequest().addHeader("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.59");
        assertEquals(UserAgentType.CHROME, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_firefoxMobile() {
        getMockRequest().addHeader("user-agent", "Mozilla/5.0 (Mobile; rv:40.0) Gecko/40.0 Firefox/40.0");
        assertEquals(UserAgentType.FIREFOX, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_caseInsensitive() {
        getMockRequest().addHeader("user-agent", "Mozilla/5.0 (compatible; msie 9.0; Windows NT 6.1; Trident/5.0)");
        assertEquals(UserAgentType.IE, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_caching() {
        getMockRequest().addHeader("user-agent",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.52 Safari/537.36");
        assertEquals(UserAgentType.CHROME, userAgentHelper.getUserAgentType());

        getMockRequest().addHeader("user-agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:42.0) Gecko/20100101 Firefox/42.0");
        assertEquals(UserAgentType.CHROME, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_bot() {
        getMockRequest().addHeader("user-agent", "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");
        assertEquals(UserAgentType.OTHER, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_curl() {
        getMockRequest().addHeader("user-agent", "curl/7.68.0");
        assertEquals(UserAgentType.OTHER, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_wget() {
        getMockRequest().addHeader("user-agent", "Wget/1.20.3 (linux-gnu)");
        assertEquals(UserAgentType.OTHER, userAgentHelper.getUserAgentType());
    }

    @Test
    public void test_getUserAgentType_mobile() {
        getMockRequest().addHeader("user-agent",
                "Mozilla/5.0 (Linux; Android 10; SM-G973F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Mobile Safari/537.36");
        assertEquals(UserAgentType.CHROME, userAgentHelper.getUserAgentType());
    }

}
