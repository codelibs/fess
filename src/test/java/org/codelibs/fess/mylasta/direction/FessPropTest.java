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
package org.codelibs.fess.mylasta.direction;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.codelibs.core.io.FileUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.PrunedTag;
import org.codelibs.nekohtml.parsers.DOMParser;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class FessPropTest extends UnitFessTestCase {

    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    public void test_maxUsernameLength() throws IOException {
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public Integer getLdapMaxUsernameLengthAsInteger() {
                return Integer.valueOf(-1);
            }
        };
        File file = File.createTempFile("test", ".properties");
        file.deleteOnExit();
        FileUtil.writeBytes(file.getAbsolutePath(), "ldap.security.principal=%s@fess.codelibs.local".getBytes("UTF-8"));
        DynamicProperties systemProps = new DynamicProperties(file);
        SingletonLaContainerFactory.getContainer().register(systemProps, "systemProperties");

        assertEquals("@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal(null));
        assertEquals("@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal(""));
        assertEquals("123456789@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal("123456789"));
        assertEquals("1234567890@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal("1234567890"));
        assertEquals("12345678901@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal("12345678901"));
    }

    public void test_maxUsernameLength10() throws IOException {
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public Integer getLdapMaxUsernameLengthAsInteger() {
                return Integer.valueOf(10);
            }
        };

        File file = File.createTempFile("test", ".properties");
        file.deleteOnExit();
        FileUtil.writeBytes(file.getAbsolutePath(), "ldap.security.principal=%s@fess.codelibs.local".getBytes("UTF-8"));
        DynamicProperties systemProps = new DynamicProperties(file);
        SingletonLaContainerFactory.getContainer().register(systemProps, "systemProperties");

        assertEquals("@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal(null));
        assertEquals("@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal(""));
        assertEquals("123456789@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal("123456789"));
        assertEquals("1234567890@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal("1234567890"));
        assertEquals("1234567890@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal("12345678901"));
    }

    public void test_validateIndexRequiredFields() {
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getIndexAdminRequiredFields() {
                return "aaa,bbb";
            }
        };

        HashMap<String, Object> source = new HashMap<>();
        assertFalse(fessConfig.validateIndexRequiredFields(source));
        source.put("aaa", null);
        assertFalse(fessConfig.validateIndexRequiredFields(source));
        source.put("aaa", null);
        source.put("bbb", null);
        assertFalse(fessConfig.validateIndexRequiredFields(source));
        source.put("aaa", "");
        source.put("bbb", "");
        assertFalse(fessConfig.validateIndexRequiredFields(source));
        source.put("aaa", "");
        source.put("bbb", "a");
        assertFalse(fessConfig.validateIndexRequiredFields(source));
        source.put("aaa", " ");
        source.put("bbb", "a");
        assertFalse(fessConfig.validateIndexRequiredFields(source));
        source.put("aaa", "a");
        source.put("bbb", "a");
        assertTrue(fessConfig.validateIndexRequiredFields(source));
    }

    public void test_getCrawlerDocumentSpaceCharsAsArray() {
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerDocumentSpaceChars() {
                return "u0020u3000";
            }
        };

        int[] chars = fessConfig.getCrawlerDocumentSpaceCharsAsArray();
        assertEquals(2, chars.length);
        assertEquals(32, chars[0]);
        assertEquals(12288, chars[1]);
    }

    public void test_getCrawlerDocumentFullstopCharsAsArray() {
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerDocumentFullstopChars() {
                return "u0020u3000";
            }
        };

        int[] chars = fessConfig.getCrawlerDocumentFullstopCharsAsArray();
        assertEquals(2, chars.length);
        assertEquals(32, chars[0]);
        assertEquals(12288, chars[1]);
    }

    public void test_getCrawlerDocumentHtmlPrunedTagsAsArray() throws Exception {
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerDocumentHtmlPrunedTags() {
                return "script,div#main,p.image,a[rel=nofollow],div[x-y=a-.:_0]";
            }
        };

        PrunedTag[] tags = fessConfig.getCrawlerDocumentHtmlPrunedTagsAsArray();
        assertTrue(matchesTag(tags[0], "<script></script>"));
        assertTrue(matchesTag(tags[0], "<script id=\\\"main\\\"></script>"));
        assertFalse(matchesTag(tags[0], "<a></a>"));

        assertTrue(matchesTag(tags[1], "<div id=\"main\"></div>"));
        assertFalse(matchesTag(tags[1], "<div></div>"));

        assertTrue(matchesTag(tags[2], "<p class=\"image\"></p>"));
        assertFalse(matchesTag(tags[2], "<p></p>"));

        assertTrue(matchesTag(tags[3], "<a rel=\"nofollow\"></a>"));
        assertFalse(matchesTag(tags[3], "<a></a>"));

        assertTrue(matchesTag(tags[4], "<div x-y=\"a-.:_0\"></div>"));
        assertFalse(matchesTag(tags[4], "<div x-y=\"a 0\"></div>"));
    }

    public void test_getAvailableSmbSidType() throws Exception {
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getSmbAvailableSidTypes() {
                return "1,2,5:2";
            }
        };

        assertNull(fessConfig.getAvailableSmbSidType(0));
        assertEquals(1, fessConfig.getAvailableSmbSidType(1));
        assertEquals(2, fessConfig.getAvailableSmbSidType(2));
        assertNull(fessConfig.getAvailableSmbSidType(3));
        assertNull(fessConfig.getAvailableSmbSidType(4));
        assertEquals(2, fessConfig.getAvailableSmbSidType(5));
    }

    private boolean matchesTag(final PrunedTag tag, final String text) throws Exception {
        final DOMParser parser = new DOMParser();
        final String html = "<html><body>" + text + "</body></html>";
        final ByteArrayInputStream is = new ByteArrayInputStream(html.getBytes("UTF-8"));
        parser.parse(new InputSource(is));
        Node node = parser.getDocument().getFirstChild().getLastChild().getFirstChild();
        return tag.matches(node);
    }

    public void test_normalizeQueryLanguages() {
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getQueryLanguageMapping() {
                return "ja=ja\nzh_cn=zh-cn\nzh_TW=zh-tw\nzh=zh-cn";
            }
        };

        assertArrays(new String[] {}, fessConfig.normalizeQueryLanguages(new String[] {}));
        assertArrays(new String[] {}, fessConfig.normalizeQueryLanguages(new String[] { "unknown" }));
        assertArrays(new String[] { "ja" }, fessConfig.normalizeQueryLanguages(new String[] { "ja" }));
        assertArrays(new String[] { "ja" }, fessConfig.normalizeQueryLanguages(new String[] { "ja", "ja" }));
        assertArrays(new String[] { "ja" }, fessConfig.normalizeQueryLanguages(new String[] { "ja-jp" }));
        assertArrays(new String[] { "ja" }, fessConfig.normalizeQueryLanguages(new String[] { "ja_JP" }));
        assertArrays(new String[] { "ja", "zh-cn" }, fessConfig.normalizeQueryLanguages(new String[] { "ja", "zh" }));
        assertArrays(new String[] { "ja", "zh-cn" }, fessConfig.normalizeQueryLanguages(new String[] { "ja", "zh_CN" }));
        assertArrays(new String[] { "ja", "zh-cn" }, fessConfig.normalizeQueryLanguages(new String[] { "ja", "zh-cn" }));
        assertArrays(new String[] { "zh-cn" }, fessConfig.normalizeQueryLanguages(new String[] { "zh", "zh-cn" }));
        assertArrays(new String[] { "zh-tw" }, fessConfig.normalizeQueryLanguages(new String[] { "zh_TW" }));
    }

    public void test_getQueryLocaleFromName() {
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getQueryLanguageMapping() {
                return "ja=ja\nzh_cn=zh-cn\nzh_TW=zh-tw\nzh=zh-cn";
            }
        };

        assertEquals(Locale.ROOT, fessConfig.getQueryLocaleFromName(null));
        assertEquals(Locale.ROOT, fessConfig.getQueryLocaleFromName(""));
        assertEquals(Locale.ROOT, fessConfig.getQueryLocaleFromName("ja"));
        assertEquals(Locale.JAPANESE, fessConfig.getQueryLocaleFromName("test_ja"));
        assertEquals(Locale.CHINESE, fessConfig.getQueryLocaleFromName("test_zh"));
        assertEquals(Locale.SIMPLIFIED_CHINESE, fessConfig.getQueryLocaleFromName("test_zh_cn"));
        assertEquals(Locale.TRADITIONAL_CHINESE, fessConfig.getQueryLocaleFromName("test_zh_TW"));
    }

    public void test_isValidUserCode() {
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public Integer getUserCodeMinLengthAsInteger() {
                return 10;
            }

            @Override
            public Integer getUserCodeMaxLengthAsInteger() {
                return 20;
            }

            @Override
            public String getUserCodePattern() {
                return "[a-zA-Z0-9_]+";
            }
        };

        assertTrue(fessConfig.isValidUserCode("1234567890"));
        assertTrue(fessConfig.isValidUserCode("12345678901234567890"));
        assertTrue(fessConfig.isValidUserCode("1234567890abcdeABCD_"));

        assertFalse(fessConfig.isValidUserCode("123456789"));
        assertFalse(fessConfig.isValidUserCode("123456789012345678901"));
        assertFalse(fessConfig.isValidUserCode("123456789?"));
    }

    public void test_getUserAgentName() throws IOException {
        final Map<String, String> systemPropMap = new HashMap<>();
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getSystemProperty(final String key, final String defaultValue) {
                return systemPropMap.getOrDefault(key, defaultValue);
            }
        };
        ComponentUtil.setFessConfig(fessConfig);
        SystemHelper systemHelper = new SystemHelper() {
            @Override
            public String getProductVersion() {
                return "98.76";
            }
        };
        ComponentUtil.register(systemHelper, "systemHelper");

        assertEquals("Mozilla/5.0 (compatible; Fess/98.76; +http://fess.codelibs.org/bot.html)", fessConfig.getUserAgentName());

        systemPropMap.put(Constants.CRAWLING_USER_AGENT_PROPERTY, "TestAgent");
        assertEquals("TestAgent", fessConfig.getUserAgentName());
    }

    private void assertArrays(final String[] expected, final String[] actual) {
        Arrays.sort(expected);
        Arrays.sort(actual);
        assertEquals(String.join(",", expected), String.join(",", actual));
    }
}
