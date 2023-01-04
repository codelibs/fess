/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

public class UserAgentHelperTest extends UnitFessTestCase {

    public UserAgentHelper userAgentHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        userAgentHelper = new UserAgentHelper();
    }

    public void test_getUserAgentType_IE9() {
        getMockRequest().addHeader("user-agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0; Trident/5.0)");
        assertEquals(UserAgentType.IE, userAgentHelper.getUserAgentType());
    }

    public void test_getUserAgentType_IE10() {
        getMockRequest().addHeader("user-agent", "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)");
        assertEquals(UserAgentType.IE, userAgentHelper.getUserAgentType());
    }

    public void test_getUserAgentType_IE11() {
        getMockRequest().addHeader("user-agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko)");
        assertEquals(UserAgentType.IE, userAgentHelper.getUserAgentType());
    }

    public void test_getUserAgentType_Chrome() {
        getMockRequest().addHeader("user-agent",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.52 Safari/537.36");
        assertEquals(UserAgentType.CHROME, userAgentHelper.getUserAgentType());
    }

    public void test_getUserAgentType_FireFox() {
        getMockRequest().addHeader("user-agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:42.0) Gecko/20100101 Firefox/42.0");
        assertEquals(UserAgentType.FIREFOX, userAgentHelper.getUserAgentType());
    }

    public void test_getUserAgentType_Safari() {
        getMockRequest().addHeader("user-agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/600.8.9 (KHTML, like Gecko) Version/8.0.8 Safari/600.8.9");
        assertEquals(UserAgentType.SAFARI, userAgentHelper.getUserAgentType());
    }

    public void test_getUserAgentType_Opera() {
        getMockRequest().addHeader("user-agent", "Opera/9.80 (X11; U; Linux x86_64) Presto/2.9.181 Version/12.00");
        assertEquals(UserAgentType.OPERA, userAgentHelper.getUserAgentType());
    }

    public void test_getUserAgentType_OTHER() {
        getMockRequest().addHeader("user-agent", "Dummy");
        assertEquals(UserAgentType.OTHER, userAgentHelper.getUserAgentType());
    }

}
