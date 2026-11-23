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

import java.util.List;
import java.util.Map;

import org.codelibs.fess.Constants;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class ConfigEnvUtilTest extends UnitFessTestCase {

    private static final String PASSWORD_ENV = "FESS_CONFIG_SEARCH_ENGINE_PASSWORD";

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        ConfigEnvUtil.setEnvReader(null);
        System.clearProperty("fess.config.search_engine.password");
        System.clearProperty("fess.system.storage.secretkey");
        System.clearProperty("fess.config.search_engine.username");
        System.clearProperty("fess.config.rag.llm.testprov.api.key");
        System.clearProperty("fess.config.rag.llm.testprov2.api.key");
        super.tearDown(testInfo);
    }

    private void env(final Map<String, String> env) {
        ConfigEnvUtil.setEnvReader(env::get);
    }

    @Test
    public void test_toEnvName() {
        assertEquals(PASSWORD_ENV, ConfigEnvUtil.toEnvName(Constants.FESS_CONFIG_PREFIX, "search_engine.password"));
        assertEquals("FESS_CONFIG_RAG_LLM_GEMINI_API_KEY", ConfigEnvUtil.toEnvName(Constants.FESS_CONFIG_PREFIX, "rag.llm.gemini.api.key"));
        assertEquals("FESS_SYSTEM_STORAGE_SECRETKEY", ConfigEnvUtil.toEnvName(Constants.SYSTEM_PROP_PREFIX, "storage.secretkey"));
    }

    @Test
    public void test_getEnv_emptyCountsAsUnset() {
        env(Map.of(PASSWORD_ENV, "from-env"));
        assertEquals("from-env", ConfigEnvUtil.getEnv(Constants.FESS_CONFIG_PREFIX, "search_engine.password"));
        assertNull(ConfigEnvUtil.getEnv(Constants.FESS_CONFIG_PREFIX, "search_engine.username"));

        env(Map.of(PASSWORD_ENV, ""));
        assertNull(ConfigEnvUtil.getEnv(Constants.FESS_CONFIG_PREFIX, "search_engine.password"));
    }

    @Test
    public void test_getOverride_jvmOptionWinsOverEnvironment() {
        env(Map.of(PASSWORD_ENV, "from-env"));
        assertEquals("from-env", ConfigEnvUtil.getOverride(Constants.FESS_CONFIG_PREFIX, "search_engine.password"));

        System.setProperty("fess.config.search_engine.password", "from-d");
        assertEquals("from-d", ConfigEnvUtil.getOverride(Constants.FESS_CONFIG_PREFIX, "search_engine.password"));
    }

    @Test
    public void test_getOverride_emptyJvmOptionDoesNotHideTheVariable() {
        env(Map.of(PASSWORD_ENV, "from-env"));
        System.setProperty("fess.config.search_engine.password", "");
        assertEquals("from-env", ConfigEnvUtil.getOverride(Constants.FESS_CONFIG_PREFIX, "search_engine.password"));

        // with no variable the empty option stays, so that it still overrides the file
        env(Map.of());
        assertEquals("", ConfigEnvUtil.getOverride(Constants.FESS_CONFIG_PREFIX, "search_engine.password"));
    }

    @Test
    public void test_getSystemProperty_readsTheVariableOfASystemKey() {
        env(Map.of("FESS_SYSTEM_STORAGE_SECRETKEY", "from-env"));
        assertEquals("from-env", ComponentUtil.getFessConfig().getSystemProperty("storage.secretkey", "default"));
        env(Map.of());
        assertEquals("default", ComponentUtil.getFessConfig().getSystemProperty("storage.secretkey", "default"));
    }

    @Test
    public void test_getOverride_neitherGiven() {
        env(Map.of());
        assertNull(ConfigEnvUtil.getOverride(Constants.FESS_CONFIG_PREFIX, "search_engine.password"));
    }

    /**
     * A key that fess_config.properties does not have (a plugin's key) is read from the environment too.
     */
    @Test
    public void test_fessConfig_readsAKeyOfAPluginFromTheEnvironment() {
        env(Map.of("FESS_CONFIG_RAG_LLM_TESTPROV_API_KEY", "from-env"));
        assertEquals("from-env", ComponentUtil.getFessConfig().getOrDefault("rag.llm.testprov.api.key", "default"));

        // a value is cached per key, so the -D option is tried on another key
        env(Map.of("FESS_CONFIG_RAG_LLM_TESTPROV2_API_KEY", "from-env"));
        System.setProperty("fess.config.rag.llm.testprov2.api.key", "from-d");
        assertEquals("from-d", ComponentUtil.getFessConfig().getOrDefault("rag.llm.testprov2.api.key", "default"));
    }

    @Test
    public void test_isConfigEnvName() {
        assertTrue(ConfigEnvUtil.isConfigEnvName("FESS_CONFIG_SLACK_WEBHOOK_URLS"));
        assertTrue(ConfigEnvUtil.isConfigEnvName("FESS_SYSTEM_STORAGE_ACCESSKEY"));
        assertFalse(ConfigEnvUtil.isConfigEnvName("FESS_CONF_PATH"));
        assertFalse(ConfigEnvUtil.isConfigEnvName("PATH"));
        assertFalse(ConfigEnvUtil.isConfigEnvName(null));
    }

    /**
     * The name of the variable of a setting says nothing about its value (a webhook URL carries its own
     * credential), so every such variable is masked in System Info and in the DEBUG dump of a child process.
     */
    @Test
    public void test_variablesOfSettingsAreMasked() {
        assertTrue(org.codelibs.fess.app.web.admin.systeminfo.AdminSysteminfoAction.isMaskedValue("FESS_CONFIG_SLACK_WEBHOOK_URLS"));
        assertTrue(org.codelibs.fess.app.web.admin.systeminfo.AdminSysteminfoAction.isMaskedValue("FESS_SYSTEM_STORAGE_ACCESSKEY"));
        assertFalse(org.codelibs.fess.app.web.admin.systeminfo.AdminSysteminfoAction.isMaskedValue("FESS_CONF_PATH"));
        assertEquals("********", SystemUtil.maskSensitiveValue("FESS_CONFIG_SLACK_WEBHOOK_URLS", "https://hooks.example/x"));
        assertEquals("/etc/fess", SystemUtil.maskSensitiveValue("FESS_CONF_PATH", "/etc/fess"));
    }

    @Test
    public void test_secretsOnCommandLine_namesTheKeyAndTheVariableButNotTheValue() {
        System.setProperty("fess.config.search_engine.password", "s3cr3t-value");
        System.setProperty("fess.system.storage.secretkey", "another-value");
        System.setProperty("fess.config.search_engine.username", "not-a-secret");

        final List<String> messages = ConfigEnvUtil.secretsOnCommandLine();

        assertEquals(2, messages.size());
        assertTrue(messages.get(0).contains("-Dfess.config.search_engine.password"));
        assertTrue(messages.get(0).contains(PASSWORD_ENV));
        assertTrue(messages.get(1).contains("FESS_SYSTEM_STORAGE_SECRETKEY"));
        messages.forEach(m -> {
            assertFalse(m.contains("s3cr3t-value"));
            assertFalse(m.contains("another-value"));
        });
    }

    @Test
    public void test_secretsOnCommandLine_aClientIdIsNotASecret() {
        System.setProperty("fess.system.oic.client.id", "client-1");
        System.setProperty("fess.system.oic.client.secret", "client-secret-1");
        try {
            final List<String> messages = ConfigEnvUtil.secretsOnCommandLine();
            assertEquals(1, messages.size());
            assertTrue(messages.get(0).contains("oic.client.secret"));
        } finally {
            System.clearProperty("fess.system.oic.client.id");
            System.clearProperty("fess.system.oic.client.secret");
        }
    }

    @Test
    public void test_secretsOnCommandLine_emptyValueIsNotWarnedAbout() {
        System.setProperty("fess.config.search_engine.password", "");
        assertTrue(ConfigEnvUtil.secretsOnCommandLine().isEmpty());
    }
}
