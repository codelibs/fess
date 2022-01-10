/*
 * Copyright 2012-2022 CodeLibs Project and the Others.
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

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

public class SystemHelperTest extends UnitFessTestCase {

    public SystemHelper systemHelper;

    private Map<String, String> envMap = new HashMap<>();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        systemHelper = new SystemHelper() {
            @Override
            protected void parseProjectProperties() {
            }

            @Override
            public void updateSystemProperties() {
            }

            @Override
            protected Map<String, String> getEnvMap() {
                return envMap;
            }
        };
        envMap.clear();
        systemHelper.init();
    }

    public void test_encodeUrlFilter() {
        String path = null;
        assertNull(systemHelper.encodeUrlFilter(path));

        path = "abc";
        assertEquals(path, systemHelper.encodeUrlFilter(path));

        path = "あいう";
        assertEquals("%E3%81%82%E3%81%84%E3%81%86", systemHelper.encodeUrlFilter(path));

        path = "[]^$.*+?,{}|%\\";
        assertEquals(path, systemHelper.encodeUrlFilter(path));
    }

    public void test_normalizeLang() {
        String value = null;
        assertNull(systemHelper.normalizeLang(value));

        value = "";
        assertNull(systemHelper.normalizeLang(value));

        value = "_ja";
        assertNull(systemHelper.normalizeLang(value));

        value = "ja";
        assertEquals("ja", systemHelper.normalizeLang(value));

        value = " ja ";
        assertEquals("ja", systemHelper.normalizeLang(value));

        value = "ja-JP";
        assertEquals("ja", systemHelper.normalizeLang(value));

        value = "ja_JP";
        assertEquals("ja", systemHelper.normalizeLang(value));

        value = "ja_JP_AAA";
        assertEquals("ja", systemHelper.normalizeLang(value));

        value = "zh";
        assertEquals("zh", systemHelper.normalizeLang(value));

        value = "zh-cn";
        assertEquals("zh_CN", systemHelper.normalizeLang(value));

        value = "zh_CN";
        assertEquals("zh_CN", systemHelper.normalizeLang(value));

        value = "zh-tw";
        assertEquals("zh_TW", systemHelper.normalizeLang(value));

        value = "zh_TW";
        assertEquals("zh_TW", systemHelper.normalizeLang(value));
    }

    public void test_createSearchRole() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            public boolean isLdapIgnoreNetbiosName() {
                return true;
            }
        });

        assertEquals("", systemHelper.createSearchRole("", ""));
        assertEquals("aaa", systemHelper.createSearchRole("", "aaa"));
        assertEquals("bbb", systemHelper.createSearchRole("", "aaa\\bbb"));
        assertEquals("bbb\\ccc", systemHelper.createSearchRole("", "aaa\\bbb\\ccc"));
    }

    public void test_normalizeConfigPath() {
        assertEquals("", systemHelper.normalizeConfigPath(""));
        assertEquals(".*\\Qwww.domain.com/test\\E.*", systemHelper.normalizeConfigPath("contains:www.domain.com/test"));
        assertEquals(".*\\Q/test/\\E.*", systemHelper.normalizeConfigPath("contains:/test/"));
        assertEquals("www.domain.com/test", systemHelper.normalizeConfigPath("www.domain.com/test"));
        assertEquals(".*domain.com/.*", systemHelper.normalizeConfigPath(".*domain.com/.*"));
        assertEquals("aaa", systemHelper.normalizeConfigPath("regexp:aaa"));
        assertEquals("aaa", systemHelper.normalizeConfigPath("regexpCase:aaa"));
        assertEquals("(?i)aaa", systemHelper.normalizeConfigPath("regexpIgnoreCase:aaa"));
    }

    public void test_getFilteredEnvMap() {
        Map<String, String> filteredEnvMap = systemHelper.getFilteredEnvMap("^FESS_ENV.*");
        assertEquals(0, filteredEnvMap.size());

        envMap.put("FESS_ENV_TEST", "123");
        filteredEnvMap = systemHelper.getFilteredEnvMap("^FESS_ENV.*");
        assertEquals(1, filteredEnvMap.size());
        assertEquals("123", filteredEnvMap.get("FESS_ENV_TEST"));

        filteredEnvMap = systemHelper.getFilteredEnvMap("^XFESS_ENV.*");
        assertEquals(0, filteredEnvMap.size());

        envMap.put("", "123");
        filteredEnvMap = systemHelper.getFilteredEnvMap("^FESS_ENV.*");
        assertEquals(1, filteredEnvMap.size());
        assertEquals("123", filteredEnvMap.get("FESS_ENV_TEST"));
    }

    public void test_isUserPermission() {
        assertTrue(systemHelper.isUserPermission("1test"));

        assertFalse(systemHelper.isUserPermission(null));
        assertFalse(systemHelper.isUserPermission(""));
        assertFalse(systemHelper.isUserPermission(" "));
        assertFalse(systemHelper.isUserPermission("2test"));
        assertFalse(systemHelper.isUserPermission("Rtest"));
    }

    public void test_getSearchRole() {
        assertEquals("1test", systemHelper.getSearchRoleByUser("test"));
        assertEquals("Rtest", systemHelper.getSearchRoleByRole("test"));
        assertEquals("2test", systemHelper.getSearchRoleByGroup("test"));

        assertEquals("1", systemHelper.getSearchRoleByUser(""));
        assertEquals("R", systemHelper.getSearchRoleByRole(""));
        assertEquals("2", systemHelper.getSearchRoleByGroup(""));
    }
}
