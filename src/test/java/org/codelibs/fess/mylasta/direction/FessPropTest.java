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
import org.junit.jupiter.api.Test;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class FessPropTest extends UnitFessTestCase {

    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    @Test
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

        // Get existing component and update it instead of registering new one
        DynamicProperties existingProps = SingletonLaContainerFactory.getContainer().getComponent("systemProperties");
        existingProps.setProperty("ldap.security.principal", "%s@fess.codelibs.local");

        assertEquals("@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal(null));
        assertEquals("@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal(""));
        assertEquals("123456789@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal("123456789"));
        assertEquals("1234567890@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal("1234567890"));
        assertEquals("12345678901@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal("12345678901"));
    }

    @Test
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

        // Get existing component and update it instead of registering new one
        DynamicProperties existingProps = SingletonLaContainerFactory.getContainer().getComponent("systemProperties");
        existingProps.setProperty("ldap.security.principal", "%s@fess.codelibs.local");

        assertEquals("@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal(null));
        assertEquals("@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal(""));
        assertEquals("123456789@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal("123456789"));
        assertEquals("1234567890@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal("1234567890"));
        assertEquals("1234567890@fess.codelibs.local", fessConfig.getLdapSecurityPrincipal("12345678901"));
    }

    /**
     * Builds a config whose only answers are the two keys the admin-user comparison reads.
     *
     * @param adminUsers the value of authentication.admin.users
     * @param ignoreCase the value of authentication.admin.users.ignore.case
     * @return the config
     */
    private FessConfig createAdminUsersConfig(final String adminUsers, final String ignoreCase) {
        return new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getAuthenticationAdminUsers() {
                return adminUsers;
            }

            @Override
            public String getAuthenticationAdminUsersIgnoreCase() {
                return ignoreCase;
            }

            @Override
            public boolean isLdapAdminEnabled() {
                return true;
            }
        };
    }

    /**
     * Sets ldap.provider.url on the running container, which is what auto reads.
     *
     * @param url the URL, or null to leave the setting empty
     */
    private void setLdapProviderUrl(final String url) {
        final DynamicProperties systemProps = SingletonLaContainerFactory.getContainer().getComponent("systemProperties");
        if (url == null) {
            systemProps.remove(Constants.LDAP_PROVIDER_URL);
        } else {
            systemProps.setProperty(Constants.LDAP_PROVIDER_URL, url);
        }
    }

    /**
     * Without a directory the comparison cannot change any outcome: both branches of
     * FessLoginAssist#resolveCredential end at doAuthenticateLocal, and SpnegoAuthenticator resolves
     * nothing either way. So auto leaves such an installation comparing exactly, as it always did.
     */
    @Test
    public void test_isAdminUser_autoComparesExactlyWithoutLdap() {
        FessProp.propMap.clear();
        setLdapProviderUrl(null);
        final FessConfig fessConfig = createAdminUsersConfig("admin,operator", "auto");

        assertTrue(fessConfig.isAdminUser("admin"));
        assertTrue(fessConfig.isAdminUser("operator"));
        assertFalse(fessConfig.isAdminUser("ADMIN"));
        assertFalse(fessConfig.isAdminUser("Admin"));
        assertFalse(fessConfig.isAdminUser("OPERATOR"));
        assertFalse(fessConfig.isAdminUser("admin2"));
        assertFalse(fessConfig.isAdminUser(""));
        assertFalse(fessConfig.isAdminUser(null));
    }

    /**
     * A directory does not distinguish case in an account name -- Active Directory issues a ticket for
     * any casing of one -- so once ldap.provider.url names one, auto compares the reserved names the
     * way that directory does.
     */
    @Test
    public void test_isAdminUser_autoIgnoresCaseWithLdap() {
        FessProp.propMap.clear();
        setLdapProviderUrl("ldap://localhost:389/");
        final FessConfig fessConfig = createAdminUsersConfig("admin,operator", "auto");

        assertTrue(fessConfig.isAdminUser("admin"));
        assertTrue(fessConfig.isAdminUser("ADMIN"));
        assertTrue(fessConfig.isAdminUser("Admin"));
        assertTrue(fessConfig.isAdminUser("aDmIn"));
        assertTrue(fessConfig.isAdminUser("OPERATOR"));

        // Only the names it lists, whatever their case: a longer or shorter name is a different one.
        assertFalse(fessConfig.isAdminUser("admin2"));
        assertFalse(fessConfig.isAdminUser("adm"));
        assertFalse(fessConfig.isAdminUser("alice"));
        assertFalse(fessConfig.isAdminUser(""));
        assertFalse(fessConfig.isAdminUser(null));
    }

    /**
     * true asks for the directory comparison whether or not this Fess resolves names through one.
     */
    @Test
    public void test_isAdminUser_trueIgnoresCaseWithoutLdap() {
        FessProp.propMap.clear();
        setLdapProviderUrl(null);
        final FessConfig fessConfig = createAdminUsersConfig("admin,operator", "true");

        assertTrue(fessConfig.isAdminUser("ADMIN"));
        assertTrue(fessConfig.isAdminUser("OPERATOR"));
        assertFalse(fessConfig.isAdminUser("admin2"));
    }

    /**
     * false is the way out for an installation that has an account whose name differs from a reserved
     * one only in case and that must keep logging in.
     */
    @Test
    public void test_isAdminUser_falseComparesExactlyWithLdap() {
        FessProp.propMap.clear();
        setLdapProviderUrl("ldap://localhost:389/");
        final FessConfig fessConfig = createAdminUsersConfig("admin,operator", "false");

        assertTrue(fessConfig.isAdminUser("admin"));
        assertFalse(fessConfig.isAdminUser("ADMIN"));
        assertFalse(fessConfig.isAdminUser("Admin"));
    }

    /**
     * A comma-separated list is ordinarily written with a space after the comma. Untrimmed, every
     * entry but the first is compared as " name" and reserves nothing -- and for SSO that is the
     * fail-open direction: the account logs in and is handed the permission named after it.
     */
    @Test
    public void test_isAdminUser_trimsEachEntry() {
        FessProp.propMap.clear();
        setLdapProviderUrl("ldap://localhost:389/");
        final FessConfig fessConfig = createAdminUsersConfig("admin, operator , backup", "auto");

        assertTrue(fessConfig.isAdminUser("admin"));
        assertTrue(fessConfig.isAdminUser("operator"));
        assertTrue(fessConfig.isAdminUser("backup"));
        // The space is not part of any name, so it must not become part of one either.
        assertFalse(fessConfig.isAdminUser(" operator"));
        assertFalse(fessConfig.isAdminUser("operator "));
    }

    /**
     * The exact comparison reads the same list, so it has to trim the same way.
     */
    @Test
    public void test_isAdminUser_trimsEachEntryWithExactComparison() {
        FessProp.propMap.clear();
        setLdapProviderUrl(null);
        final FessConfig fessConfig = createAdminUsersConfig("admin, operator", "false");

        assertTrue(fessConfig.isAdminUser("operator"));
        assertFalse(fessConfig.isAdminUser("OPERATOR"));
        assertFalse(fessConfig.isAdminUser(" operator"));
    }

    /**
     * A trailing comma leaves an empty entry, which must reserve nothing rather than the empty name.
     */
    @Test
    public void test_isAdminUser_dropsBlankEntries() {
        FessProp.propMap.clear();
        setLdapProviderUrl("ldap://localhost:389/");
        final FessConfig fessConfig = createAdminUsersConfig("admin,,  ,", "auto");

        assertTrue(fessConfig.isAdminUser("admin"));
        assertFalse(fessConfig.isAdminUser(""));
        assertFalse(fessConfig.isAdminUser(" "));
        assertFalse(fessConfig.isAdminUser(null));
    }

    /**
     * The other half of authentication.admin.*: a role list written with a space after the comma
     * lost every entry but the first, and with it the administrator's access to every admin screen.
     */
    @Test
    public void test_getAuthenticationAdminRolesAsArray_trimsEachEntry() {
        FessProp.propMap.clear();
        final FessConfig fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getAuthenticationAdminRoles() {
                return "admin, operator ,, ";
            }
        };

        final String[] roles = fessConfig.getAuthenticationAdminRolesAsArray();

        assertEquals(2, roles.length);
        assertEquals("admin", roles[0]);
        assertEquals("operator", roles[1]);
    }

    /**
     * isLdapAdminEnabled refuses to make a reserved name editable in the directory, and reads the same
     * comparison -- so the switch moves that decision too.
     */
    @Test
    public void test_isLdapAdminEnabled_readsTheSameComparison() {
        FessProp.propMap.clear();
        setLdapProviderUrl("ldap://localhost:389/");
        assertFalse(createAdminUsersConfig("admin", "auto").isLdapAdminEnabled("ADMIN"));
        assertTrue(createAdminUsersConfig("admin", "false").isLdapAdminEnabled("ADMIN"));
        assertTrue(createAdminUsersConfig("admin", "auto").isLdapAdminEnabled("alice"));
        assertFalse(createAdminUsersConfig("admin", "auto").isLdapAdminEnabled("admin"));
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
    public void test_getEntraIdPermissionFields_withNewKey() {
        final Map<String, String> systemPropMap = new HashMap<>();
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getSystemProperty(final String key, final String defaultValue) {
                return systemPropMap.getOrDefault(key, defaultValue);
            }

            @Override
            public String getSystemProperty(final String key) {
                return systemPropMap.get(key);
            }
        };

        // Test with new entraid.permission.fields key
        systemPropMap.put("entraid.permission.fields", "displayName,userPrincipalName");
        String[] fields = fessConfig.getEntraIdPermissionFields();
        assertEquals(2, fields.length);
        assertEquals("displayName", fields[0]);
        assertEquals("userPrincipalName", fields[1]);
    }

    @Test
    public void test_getEntraIdPermissionFields_withLegacyFallback() {
        final Map<String, String> systemPropMap = new HashMap<>();
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getSystemProperty(final String key, final String defaultValue) {
                return systemPropMap.getOrDefault(key, defaultValue);
            }

            @Override
            public String getSystemProperty(final String key) {
                return systemPropMap.get(key);
            }
        };

        // Test fallback to legacy aad.permission.fields key
        systemPropMap.put("aad.permission.fields", "mail,displayName");
        String[] fields = fessConfig.getEntraIdPermissionFields();
        assertEquals(2, fields.length);
        assertEquals("mail", fields[0]);
        assertEquals("displayName", fields[1]);
    }

    @Test
    public void test_getEntraIdPermissionFields_withDefault() {
        final Map<String, String> systemPropMap = new HashMap<>();
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getSystemProperty(final String key, final String defaultValue) {
                return systemPropMap.getOrDefault(key, defaultValue);
            }

            @Override
            public String getSystemProperty(final String key) {
                return systemPropMap.get(key);
            }
        };

        // Test default value when no key is set
        String[] fields = fessConfig.getEntraIdPermissionFields();
        assertEquals(1, fields.length);
        assertEquals("mail", fields[0]);
    }

    @Test
    public void test_getEntraIdPermissionFields_newKeyTakesPrecedence() {
        final Map<String, String> systemPropMap = new HashMap<>();
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getSystemProperty(final String key, final String defaultValue) {
                return systemPropMap.getOrDefault(key, defaultValue);
            }

            @Override
            public String getSystemProperty(final String key) {
                return systemPropMap.get(key);
            }
        };

        // Test that new key takes precedence over legacy key
        systemPropMap.put("entraid.permission.fields", "newField");
        systemPropMap.put("aad.permission.fields", "legacyField");
        String[] fields = fessConfig.getEntraIdPermissionFields();
        assertEquals(1, fields.length);
        assertEquals("newField", fields[0]);
    }

    @Test
    public void test_getEntraIdPermissionFields_blankLegacyKeyStillYieldsTheDefault() {
        final Map<String, String> systemPropMap = new HashMap<>();
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getSystemProperty(final String key, final String defaultValue) {
                return systemPropMap.getOrDefault(key, defaultValue);
            }

            @Override
            public String getSystemProperty(final String key) {
                return systemPropMap.get(key);
            }
        };

        // getSystemProperty substitutes the default only when the key is absent, so a legacy key
        // that is present but empty -- what the admin screen leaves behind for a cleared field --
        // came back as "" and split into no fields at all. The only permissions such a user then
        // gets are the raw group object IDs, so every document ACL'd by group mail turns
        // invisible to them.
        systemPropMap.put("aad.permission.fields", "");
        String[] fields = fessConfig.getEntraIdPermissionFields();
        assertEquals(1, fields.length);
        assertEquals("mail", fields[0]);

        // Both keys present and blank, which is what saving the admin screen twice produces.
        systemPropMap.put("entraid.permission.fields", "  ");
        fields = fessConfig.getEntraIdPermissionFields();
        assertEquals(1, fields.length);
        assertEquals("mail", fields[0]);
    }

    @Test
    public void test_isEntraIdUseDomainServices_withNewKey() {
        final Map<String, String> systemPropMap = new HashMap<>();
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getSystemProperty(final String key, final String defaultValue) {
                return systemPropMap.getOrDefault(key, defaultValue);
            }

            @Override
            public String getSystemProperty(final String key) {
                return systemPropMap.get(key);
            }
        };

        // Test with new entraid.use.ds key set to false
        systemPropMap.put("entraid.use.ds", "false");
        assertFalse(fessConfig.isEntraIdUseDomainServices());

        // Test with new entraid.use.ds key set to true
        systemPropMap.put("entraid.use.ds", "true");
        assertTrue(fessConfig.isEntraIdUseDomainServices());
    }

    @Test
    public void test_isEntraIdUseDomainServices_withLegacyFallback() {
        final Map<String, String> systemPropMap = new HashMap<>();
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getSystemProperty(final String key, final String defaultValue) {
                return systemPropMap.getOrDefault(key, defaultValue);
            }

            @Override
            public String getSystemProperty(final String key) {
                return systemPropMap.get(key);
            }
        };

        // Test fallback to legacy aad.use.ds key
        systemPropMap.put("aad.use.ds", "false");
        assertFalse(fessConfig.isEntraIdUseDomainServices());
    }

    @Test
    public void test_isEntraIdUseDomainServices_withDefault() {
        final Map<String, String> systemPropMap = new HashMap<>();
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getSystemProperty(final String key, final String defaultValue) {
                return systemPropMap.getOrDefault(key, defaultValue);
            }

            @Override
            public String getSystemProperty(final String key) {
                return systemPropMap.get(key);
            }
        };

        // Test default value (true) when no key is set
        assertTrue(fessConfig.isEntraIdUseDomainServices());
    }

    @Test
    public void test_isEntraIdUseDomainServices_blankLegacyKeyKeepsTheDocumentedDefault() {
        final Map<String, String> systemPropMap = new HashMap<>();
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getSystemProperty(final String key, final String defaultValue) {
                return systemPropMap.getOrDefault(key, defaultValue);
            }

            @Override
            public String getSystemProperty(final String key) {
                return systemPropMap.get(key);
            }
        };

        // A present-but-empty legacy key is not a configured "false": getSystemProperty hands
        // back "", which never equals "true", so the documented default of true silently flipped.
        systemPropMap.put("aad.use.ds", "");
        assertTrue(fessConfig.isEntraIdUseDomainServices());

        // Both keys present and blank, which is what saving the admin screen twice produces.
        systemPropMap.put("entraid.use.ds", "  ");
        assertTrue(fessConfig.isEntraIdUseDomainServices());
    }

    @Test
    public void test_isEntraIdUseDomainServices_newKeyTakesPrecedence() {
        final Map<String, String> systemPropMap = new HashMap<>();
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getSystemProperty(final String key, final String defaultValue) {
                return systemPropMap.getOrDefault(key, defaultValue);
            }

            @Override
            public String getSystemProperty(final String key) {
                return systemPropMap.get(key);
            }
        };

        // Test that new key takes precedence over legacy key
        systemPropMap.put("entraid.use.ds", "false");
        systemPropMap.put("aad.use.ds", "true");
        assertFalse(fessConfig.isEntraIdUseDomainServices());
    }

    @Test
    public void test_getLdapSecurityPrincipal_escapesSpecialChars() throws IOException {
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public Integer getLdapMaxUsernameLengthAsInteger() {
                return Integer.valueOf(-1);
            }
        };

        DynamicProperties existingProps = SingletonLaContainerFactory.getContainer().getComponent("systemProperties");
        existingProps.setProperty("ldap.security.principal", "cn=%s,dc=example,dc=com");

        // Normal username
        assertEquals("cn=admin,dc=example,dc=com", fessConfig.getLdapSecurityPrincipal("admin"));

        // Asterisk injection attempt
        assertEquals("cn=admin\\2a,dc=example,dc=com", fessConfig.getLdapSecurityPrincipal("admin*"));

        // Parentheses injection attempt
        assertEquals("cn=admin\\29\\28cn=\\2a,dc=example,dc=com", fessConfig.getLdapSecurityPrincipal("admin)(cn=*"));

        // Backslash
        assertEquals("cn=admin\\5ctest,dc=example,dc=com", fessConfig.getLdapSecurityPrincipal("admin\\test"));
    }

    @Test
    public void test_getLdapAdminUserFilter_escapesSpecialChars() {
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getLdapAdminUserFilter() {
                return "(uid=%s)";
            }
        };

        // Normal name
        assertEquals("(uid=testuser)", fessConfig.getLdapAdminUserFilter("testuser"));

        // Asterisk injection attempt
        assertEquals("(uid=test\\2a)", fessConfig.getLdapAdminUserFilter("test*"));

        // LDAP injection attempt
        assertEquals("(uid=\\2a\\29\\28|\\28uid=\\2a)", fessConfig.getLdapAdminUserFilter("*)(|(uid=*"));
    }

    @Test
    public void test_getLdapAdminUserSecurityPrincipal_escapesSpecialChars() {
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getLdapAdminUserFilter() {
                return "uid=%s";
            }

            @Override
            public String getLdapAdminUserBaseDn() {
                return "ou=users,dc=example,dc=com";
            }
        };

        // Normal name
        assertEquals("uid=testuser,ou=users,dc=example,dc=com", fessConfig.getLdapAdminUserSecurityPrincipal("testuser"));

        // Asterisk injection attempt
        assertEquals("uid=test\\2a,ou=users,dc=example,dc=com", fessConfig.getLdapAdminUserSecurityPrincipal("test*"));
    }

    @Test
    public void test_getLdapAdminRoleFilter_escapesSpecialChars() {
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getLdapAdminRoleFilter() {
                return "(cn=%s)";
            }
        };

        // Normal name
        assertEquals("(cn=admin)", fessConfig.getLdapAdminRoleFilter("admin"));

        // Asterisk injection attempt
        assertEquals("(cn=admin\\2a)", fessConfig.getLdapAdminRoleFilter("admin*"));

        // LDAP injection attempt
        assertEquals("(cn=\\29\\28cn=\\2a\\29\\29\\28|\\28cn=admin)", fessConfig.getLdapAdminRoleFilter(")(cn=*))(|(cn=admin"));
    }

    @Test
    public void test_getLdapAdminRoleSecurityPrincipal_escapesSpecialChars() {
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getLdapAdminRoleFilter() {
                return "cn=%s";
            }

            @Override
            public String getLdapAdminRoleBaseDn() {
                return "ou=roles,dc=example,dc=com";
            }
        };

        // Normal name
        assertEquals("cn=admin,ou=roles,dc=example,dc=com", fessConfig.getLdapAdminRoleSecurityPrincipal("admin"));

        // Asterisk injection attempt
        assertEquals("cn=admin\\2a,ou=roles,dc=example,dc=com", fessConfig.getLdapAdminRoleSecurityPrincipal("admin*"));
    }

    @Test
    public void test_getLdapAdminGroupFilter_escapesSpecialChars() {
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getLdapAdminGroupFilter() {
                return "(cn=%s)";
            }
        };

        // Normal name
        assertEquals("(cn=developers)", fessConfig.getLdapAdminGroupFilter("developers"));

        // Asterisk injection attempt
        assertEquals("(cn=dev\\2a)", fessConfig.getLdapAdminGroupFilter("dev*"));

        // LDAP injection attempt
        assertEquals("(cn=\\2a\\29\\28|\\28cn=\\2a)", fessConfig.getLdapAdminGroupFilter("*)(|(cn=*"));
    }

    @Test
    public void test_getLdapAdminGroupSecurityPrincipal_escapesSpecialChars() {
        FessProp.propMap.clear();
        FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getLdapAdminGroupFilter() {
                return "cn=%s";
            }

            @Override
            public String getLdapAdminGroupBaseDn() {
                return "ou=groups,dc=example,dc=com";
            }
        };

        // Normal name
        assertEquals("cn=developers,ou=groups,dc=example,dc=com", fessConfig.getLdapAdminGroupSecurityPrincipal("developers"));

        // Asterisk injection attempt
        assertEquals("cn=dev\\2a,ou=groups,dc=example,dc=com", fessConfig.getLdapAdminGroupSecurityPrincipal("dev*"));
    }

    @Test
    public void test_getChatRateLimitPerMinute_defaultValue() {
        // When the system property is not set, default is 30
        final FessConfig fessConfig = new FessConfig.SimpleImpl();
        assertEquals(30, fessConfig.getChatRateLimitPerMinute());
    }

    @Test
    public void test_getChatRateLimitPerMinute_zeroDisablesLimit() {
        // A return value <= 0 signals "disabled"
        final FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getSystemProperty(final String key, final String defaultValue) {
                if ("api.chat.rate.limit.per.user.per.minute".equals(key)) {
                    return "0";
                }
                return defaultValue;
            }
        };
        assertTrue(fessConfig.getChatRateLimitPerMinute() <= 0);
    }

    @Test
    public void test_getChatRateLimitPerMinute_invalidValue_returnsDefault() {
        // Non-numeric config value -> NumberFormatException -> fallback 30
        final FessConfig fessConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getSystemProperty(final String key, final String defaultValue) {
                if ("api.chat.rate.limit.per.user.per.minute".equals(key)) {
                    return "bad-value";
                }
                return defaultValue;
            }
        };
        assertEquals(30, fessConfig.getChatRateLimitPerMinute());
    }

    @Test
    public void test_apiV2InputBoundGetters_defaults() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        assertEquals(1000, fessConfig.getApiParamMaxLengthOrDefault());
        assertEquals(100, fessConfig.getApiParamMaxArraySizeOrDefault());
        assertEquals(100, fessConfig.getPasswordMaxLengthOrDefault());
        assertEquals(1000, fessConfig.getQueryFacetFieldsSizeMaxOrDefault());
        assertEquals(2147483647L, fessConfig.getQueryFacetFieldsMinDocCountMaxOrDefault());
        assertEquals(9999999999999L, fessConfig.getApiClickMaxTimestampOrDefault());
    }

    /**
     * A blank value in fess_config.properties reads back as null (getAsInteger) or as an
     * unparsable string (get), and every caller unboxes the result into a primitive. These
     * getters therefore must fall back to their documented default rather than hand a null
     * to the caller. See the naming note on the FessProp methods: an ...AsInteger/...AsLong
     * name here would be overridden by the generated FessConfig and drop the fallback.
     */
    @Test
    public void test_apiV2InputBoundGetters_fallBackOnBlankValue() {
        final FessConfig fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer getAsInteger(final String propertyKey) {
                return null; // DfTypeUtil.toInteger("") is null
            }

            @Override
            public String get(final String propertyKey) {
                return ""; // blank value, as written by a hand-edited properties file
            }
        };
        assertEquals(1000, fessConfig.getApiParamMaxLengthOrDefault());
        assertEquals(100, fessConfig.getApiParamMaxArraySizeOrDefault());
        assertEquals(100, fessConfig.getPasswordMaxLengthOrDefault());
        assertEquals(1000, fessConfig.getQueryFacetFieldsSizeMaxOrDefault());
        assertEquals(2147483647L, fessConfig.getQueryFacetFieldsMinDocCountMaxOrDefault());
        assertEquals(9999999999999L, fessConfig.getApiClickMaxTimestampOrDefault());
    }

    @Test
    public void test_isSessionCookieSecureEnabled() {
        assertFalse(createSessionCookieSecureConfig("").isSessionCookieSecureEnabled());
        assertFalse(createSessionCookieSecureConfig(" ").isSessionCookieSecureEnabled());
        assertTrue(createSessionCookieSecureConfig("true").isSessionCookieSecureEnabled());
        assertTrue(createSessionCookieSecureConfig("TRUE").isSessionCookieSecureEnabled());
        assertTrue(createSessionCookieSecureConfig("True").isSessionCookieSecureEnabled());
        assertFalse(createSessionCookieSecureConfig("false").isSessionCookieSecureEnabled());
        assertFalse(createSessionCookieSecureConfig("yes").isSessionCookieSecureEnabled());
        assertFalse(createSessionCookieSecureConfig("1").isSessionCookieSecureEnabled());
    }

    private FessConfig createSessionCookieSecureConfig(final String value) {
        return new FessConfig.SimpleImpl() {
            @Override
            public String getSessionCookieSecure() {
                return value;
            }
        };
    }

    // ------------------------------------------------------------------
    // Trusted-proxy address canonicalisation

    @Test
    public void test_normalizeIpAddress_ipv6LoopbackSpellingsAgree() {
        // The whole point: the configuration writes "::1", getRemoteAddr() reports
        // "0:0:0:0:0:0:0:1", and a plain string comparison between them is false.
        assertEquals(FessProp.normalizeIpAddress("0:0:0:0:0:0:0:1"), FessProp.normalizeIpAddress("::1"));
    }

    @Test
    public void test_normalizeIpAddress_ipv4IsUnchanged() {
        assertEquals("127.0.0.1", FessProp.normalizeIpAddress("127.0.0.1"));
        assertEquals("192.168.1.100", FessProp.normalizeIpAddress("192.168.1.100"));
    }

    @Test
    public void test_normalizeIpAddress_distinctAddressesStayDistinct() {
        assertFalse(FessProp.normalizeIpAddress("::1").equals(FessProp.normalizeIpAddress("2001:db8::1")));
        assertFalse(FessProp.normalizeIpAddress("::1").equals(FessProp.normalizeIpAddress("::2")));
        assertFalse(FessProp.normalizeIpAddress("127.0.0.1").equals(FessProp.normalizeIpAddress("127.0.0.2")));
    }

    @Test
    public void test_normalizeIpAddress_nonLiteralIsLeftAlone() {
        // A hostname must be returned untouched rather than resolved: this runs on the request
        // path, and a DNS lookup there would be a far worse problem than the one being fixed.
        assertEquals("proxy.example.com", FessProp.normalizeIpAddress("proxy.example.com"));
        assertEquals("not an address", FessProp.normalizeIpAddress("not an address"));
        assertEquals("", FessProp.normalizeIpAddress(""));
        assertNull(FessProp.normalizeIpAddress(null));
    }

    @Test
    public void test_normalizeIpAddress_malformedDottedQuadIsLeftAlone() {
        // Shaped like IPv4 but not valid; it must come back unchanged, not throw.
        assertEquals("999.999.999.999", FessProp.normalizeIpAddress("999.999.999.999"));
    }

    @Test
    public void test_normalizeIpAddress_trimsSurroundingSpace() {
        assertEquals("127.0.0.1", FessProp.normalizeIpAddress("  127.0.0.1  "));
    }

}
