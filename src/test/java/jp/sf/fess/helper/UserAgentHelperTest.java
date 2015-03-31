/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package jp.sf.fess.helper;

import org.seasar.extension.unit.S2TestCase;

public class UserAgentHelperTest extends S2TestCase {

    public UserAgentHelper userAgentHelper;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_getIEMajorVersion_none() {
        assertEquals(0, userAgentHelper.getIEMajorVersion(getRequest()));
    }

    public void test_getIEMajorVersion_empty() {
        assertEquals(0, userAgentHelper.getIEMajorVersion(getRequest()));
    }

    public void test_getIEMajorVersion_ie6() {
        getRequest().addHeader("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
        assertEquals(6, userAgentHelper.getIEMajorVersion(getRequest()));
    }

    public void test_getIEMajorVersion_ie7() {
        getRequest().addHeader("user-agent",
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; SV1)");
        assertEquals(7, userAgentHelper.getIEMajorVersion(getRequest()));
    }

    public void test_getIEMajorVersion_ie8() {
        getRequest()
                .addHeader(
                        "user-agent",
                        "Mozilla/4.0 (compatible; GoogleToolbar 5.0.2124.2070; Windows 6.0; MSIE 8.0.6001.18241)");
        assertEquals(8, userAgentHelper.getIEMajorVersion(getRequest()));
    }

    public void test_getIEMajorVersion_ie9() {
        getRequest()
                .addHeader("user-agent",
                        "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0; Trident/5.0)");
        assertEquals(9, userAgentHelper.getIEMajorVersion(getRequest()));
    }

    public void test_getIEMajorVersion_ie10() {
        getRequest()
                .addHeader("user-agent",
                        "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)");
        assertEquals(10, userAgentHelper.getIEMajorVersion(getRequest()));
    }

    public void test_getIEMajorVersion_ie11() {
        getRequest()
                .addHeader("user-agent",
                        "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko)");
        assertEquals(11, userAgentHelper.getIEMajorVersion(getRequest()));
    }

    public void test_getIEMajorVersion_chome() {
        getRequest()
                .addHeader(
                        "user-agent",
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.52 Safari/537.36");
        assertEquals(0, userAgentHelper.getIEMajorVersion(getRequest()));
    }
}
