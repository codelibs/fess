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

import java.io.File;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codelibs.core.io.FileUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.core.misc.Tuple3;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.response.HtmlResponse;

public class SystemHelperTest extends UnitFessTestCase {

    public SystemHelper systemHelper;

    private final Map<String, String> envMap = new HashMap<>();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        final File propFile = File.createTempFile("project", ".properties");
        propFile.deleteOnExit();
        FileUtil.writeBytes(propFile.getAbsolutePath(), "fess.version=98.76.5".getBytes());
        final File desginJspRootFile = File.createTempFile("jsp", "");
        desginJspRootFile.delete();
        desginJspRootFile.deleteOnExit();
        systemHelper = new SystemHelper() {
            @Override
            protected void parseProjectProperties(final Path propPath) {
                super.parseProjectProperties(propFile.toPath());
            }

            @Override
            public void updateSystemProperties() {
            }

            @Override
            protected Map<String, String> getEnvMap() {
                return envMap;
            }

            @Override
            protected File getDesignJspFile(String path) {
                return new File(desginJspRootFile, path);
            }
        };
        envMap.clear();
        systemHelper.init();
        systemHelper.addShutdownHook(() -> {});
        ComponentUtil.register(systemHelper, "systemHelper");
    }

    public void test_getUsername() {
        assertEquals("guest", systemHelper.getUsername());
    }

    public void test_getCurrentTimeAsLocalDateTime() {
        final long current =
                1000 * systemHelper.getCurrentTimeAsLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
        final long now = System.currentTimeMillis();
        assertTrue(now + ">=" + current + " : " + (now - current), now >= current);
        assertTrue(now - 1000 + "<" + current + " : " + (current - now + 1000), now - 1000 < current);
    }

    public void test_getLogFilePath() {
        final File logFile = new File(systemHelper.getLogFilePath());
        assertEquals("logs", logFile.getName());
        assertEquals("target", logFile.getParentFile().getName());
        try {
            System.setProperty("fess.log.path", "logpath");
            assertEquals("logpath", systemHelper.getLogFilePath());
        } finally {
            System.clearProperty("fess.log.path");
        }
    }

    public void test_getForumLink() {
        getMockRequest().setLocale(Locale.ENGLISH);
        assertEquals("https://discuss.codelibs.org/c/FessEN/", systemHelper.getForumLink());
        getMockRequest().setLocale(Locale.JAPANESE);
        assertEquals("https://discuss.codelibs.org/c/FessJA/", systemHelper.getForumLink());
        getMockRequest().setLocale(Locale.ITALIAN);
        assertEquals("https://discuss.codelibs.org/c/FessEN/", systemHelper.getForumLink());
        getMockRequest().setLocale(null);
        assertEquals("https://discuss.codelibs.org/c/FessEN/", systemHelper.getForumLink());
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getForumLink() {
                return StringUtil.EMPTY;
            }
        });
        getMockRequest().setLocale(Locale.ENGLISH);
        assertNull(systemHelper.getForumLink());
    }

    public void test_getHelpLink() {
        getMockRequest().setLocale(Locale.ENGLISH);
        assertEquals("https://fess.codelibs.org/98.76/admin/xxx-guide.html", systemHelper.getHelpLink("xxx"));
        getMockRequest().setLocale(Locale.JAPANESE);
        assertEquals("https://fess.codelibs.org/ja/98.76/admin/xxx-guide.html", systemHelper.getHelpLink("xxx"));
        getMockRequest().setLocale(Locale.ITALIAN);
        assertEquals("https://fess.codelibs.org/98.76/admin/xxx-guide.html", systemHelper.getHelpLink("xxx"));
        getMockRequest().setLocale(null);
        assertEquals("https://fess.codelibs.org/98.76/admin/xxx-guide.html", systemHelper.getHelpLink("xxx"));
    }

    public void test_getDesignJspFileName() {
        assertNull(systemHelper.getDesignJspFileName("xxx"));
        systemHelper.addDesignJspFileName("xxx", "yyy");
        assertEquals("yyy", systemHelper.getDesignJspFileName("xxx"));
        final Pair<String, String>[] designJspFileNames = systemHelper.getDesignJspFileNames();
        assertEquals(1, designJspFileNames.length);
        assertEquals("xxx", designJspFileNames[0].getFirst());
        assertEquals("yyy", designJspFileNames[0].getSecond());
    }

    public void test_setForceStop() {
        assertFalse(systemHelper.isForceStop());
        systemHelper.setForceStop(true);
        assertTrue(systemHelper.isForceStop());
        systemHelper.setForceStop(false);
        assertFalse(systemHelper.isForceStop());
    }

    public void test_generateDocId() {
        final String docId = systemHelper.generateDocId(Collections.emptyMap());
        assertNotNull(docId);
        assertEquals(32, docId.length());
    }

    public void test_abbreviateLongText() {
        assertEquals("", systemHelper.abbreviateLongText(""));
        assertEquals(4000, systemHelper.abbreviateLongText(Stream.generate(() -> "a").limit(4000).collect(Collectors.joining())).length());
        assertEquals(4000, systemHelper.abbreviateLongText(Stream.generate(() -> "a").limit(4001).collect(Collectors.joining())).length());
    }

    public void test_getLanguageItems() {
        final List<Map<String, String>> enItems = systemHelper.getLanguageItems(Locale.ENGLISH);
        assertEquals(55, enItems.size());
        final List<Map<String, String>> jaItems = systemHelper.getLanguageItems(Locale.JAPANESE);
        assertEquals(55, jaItems.size());
    }

    public void test_getHostnamet() {
        assertNotNull(systemHelper.getHostname());
        try {
            envMap.put("COMPUTERNAME", "xxx");
            assertEquals("xxx", systemHelper.getHostname());
        } finally {
            envMap.remove("COMPUTERNAME");
        }
        try {
            envMap.put("HOSTNAME", "yyy");
            assertEquals("yyy", systemHelper.getHostname());
        } finally {
            envMap.remove("HOSTNAME");
        }
    }

    public void test_isEoled() {
        assertEquals(systemHelper.getCurrentTimeAsLong() > systemHelper.eolTime, systemHelper.isEoled());
        final SystemHelper helper1 = new SystemHelper() {
            @Override
            public long getCurrentTimeAsLong() {
                return systemHelper.eolTime + 1000L;
            }
        };
        helper1.eolTime = systemHelper.eolTime;
        assertTrue(helper1.isEoled());
        final SystemHelper helper2 = new SystemHelper() {
            @Override
            public long getCurrentTimeAsLong() {
                return systemHelper.eolTime - 1000L;
            }
        };
        helper2.eolTime = systemHelper.eolTime;
        assertFalse(helper2.isEoled());
    }

    public void test_updateConfiguration() {
        assertNotNull(systemHelper.updateConfiguration());
        systemHelper.addUpdateConfigListener("XXX", () -> "OK");
        assertTrue(systemHelper.updateConfiguration().contains("XXX: OK"));
    }

    public void test_isChangedClusterState() {
        systemHelper.isChangedClusterState(0);
        assertFalse(systemHelper.isChangedClusterState(0));
        assertTrue(systemHelper.isChangedClusterState(1));
        assertTrue(systemHelper.isChangedClusterState(2));
        assertFalse(systemHelper.isChangedClusterState(2));
    }

    public void test_getRedirectResponseToLogin() {
        final HtmlResponse response = HtmlResponse.fromForwardPath("/");
        assertEquals(response, systemHelper.getRedirectResponseToLogin(response));
    }

    public void test_getRedirectResponseToRoot() {
        final HtmlResponse response = HtmlResponse.fromForwardPath("/");
        assertEquals(response, systemHelper.getRedirectResponseToRoot(response));
    }

    public void test_getLogLevel() {
        final String logLevel = systemHelper.getLogLevel();
        try {
            systemHelper.setLogLevel("DEBUG");
            assertEquals("DEBUG", systemHelper.getLogLevel());
            systemHelper.setLogLevel("INFO");
            assertEquals("INFO", systemHelper.getLogLevel());
            systemHelper.setLogLevel("WARN");
            assertEquals("WARN", systemHelper.getLogLevel());
            systemHelper.setLogLevel("ERROR");
            assertEquals("ERROR", systemHelper.getLogLevel());
        } finally {
            systemHelper.setLogLevel(logLevel);
        }
    }

    public void test_createTempFile() {
        assertNotNull(systemHelper.createTempFile("test", ".txt"));
    }

    public void test_calibrateCpuLoad() {
        systemHelper.setSystemCpuCheckInterval(0L);
        systemHelper.calibrateCpuLoad();
        assertEquals(0, systemHelper.waitingThreadNames.size());
        systemHelper.waitForNoWaitingThreads();
    }

    public void test_getVersion() {
        assertEquals("98.76.5", systemHelper.getVersion());
        assertEquals(98, systemHelper.getMajorVersion());
        assertEquals(76, systemHelper.getMinorVersion());
        assertEquals("98.76", systemHelper.getProductVersion());
    }

    public void test_getEnvMap() {
        assertNotNull(new SystemHelper().getEnvMap());
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

        systemHelper.filterPathEncoding = null;
        path = "あいう";
        assertEquals(path, systemHelper.encodeUrlFilter(path));

        systemHelper.filterPathEncoding = "xxx";
        path = "あいう";
        assertEquals(path, systemHelper.encodeUrlFilter(path));
    }

    public void test_normalizeHtmlLang() {
        assertEquals("ja", systemHelper.normalizeHtmlLang("ja"));

        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentHtmlDefaultLang() {
                return "en";
            }
        });
        assertEquals("en", systemHelper.normalizeHtmlLang("ja"));
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

            @Override
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
        assertEquals("", systemHelper.normalizeConfigPath("#hash"));
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

    public void test_parseProjectProperties() {
        try {
            new SystemHelper().parseProjectProperties(null);
            assertTrue(false);
        } catch (final FessSystemException e) {
            // ok
        }
    }

    public void test_refreshDesignJspFiles() {
        final VirtualHostHelper virtualHostHelper = new VirtualHostHelper();
        ComponentUtil.register(virtualHostHelper, "virtualHostHelper");
        final List<Tuple3<String, String, String>> virtualHostList = new ArrayList<>();
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unchecked")
            @Override
            public Tuple3<String, String, String>[] getVirtualHosts() {
                return virtualHostList.toArray(n -> new Tuple3[n]);
            }
        });

        List<Path> fileList = systemHelper.refreshDesignJspFiles();
        assertEquals(0, fileList.size());

        virtualHostList.add(new Tuple3<>("abc.example.com", "8080", "host1"));
        fileList = systemHelper.refreshDesignJspFiles();
        assertEquals(0, fileList.size());

        systemHelper.addDesignJspFileName("xxx", "yyy.jsp");
        final File designJspFile = systemHelper.getDesignJspFile("/WEB-INF/view/yyy.jsp");
        designJspFile.getParentFile().mkdirs();
        FileUtil.writeBytes(designJspFile.getAbsolutePath(), "ok".getBytes());
        fileList = systemHelper.refreshDesignJspFiles();
        assertEquals(1, fileList.size());
        assertEquals("ok", FileUtil.readText(fileList.get(0).toFile()));
    }

    public void test_updateSystemProperties() {
        final SystemHelper helper = new SystemHelper();
        final AtomicReference<String> appValue = new AtomicReference<>(StringUtil.EMPTY);
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getAppValue() {
                return appValue.get();
            }

            @Override
            public String getAppEncryptPropertyPattern() {
                return ".*password|.*key|.*token|.*secret";
            }
        });
        final String now = String.valueOf(System.currentTimeMillis());
        helper.updateSystemProperties();
        assertNull(System.getProperty("fess." + now));
        assertNull(System.getProperty("test." + now));
        appValue.set("=abc\nfess." + now + "=test1\ntest." + now + "=test2");
        helper.updateSystemProperties();
        assertEquals("test1", System.getProperty("fess." + now));
        assertEquals("test2", System.getProperty("test." + now));
    }
}
