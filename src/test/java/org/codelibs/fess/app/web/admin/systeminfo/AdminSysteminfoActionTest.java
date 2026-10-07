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
package org.codelibs.fess.app.web.admin.systeminfo;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.Constants;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;

public class AdminSysteminfoActionTest extends UnitFessTestCase {

    // Mirrors AdminSysteminfoAction.MASKED_VALUE, which is private to that class.
    private static final String MASKED_VALUE = "XXXXXXXX";

    /**
     * getBugReportItems reads back whatever this test writes into the shared system
     * properties, so this test needs its own container to keep those values from leaking
     * into other test classes.
     *
     * @return true to create the container for each test
     */
    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    @Test
    public void test_isMaskedValue_masksPreExistingExactKeys() {
        // Regression guard: every key that was already masked before this change must
        // still be masked after adding the embedding-provider API key rule.
        assertTrue(AdminSysteminfoAction.isMaskedValue("http.proxy.password"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("ldap.admin.security.credentials"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("spnego.preauth.password"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("app.cipher.key"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("oic.client.id"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("oic.client.secret"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("content_chunker.embedding.opensearch.password"));
    }

    @Test
    public void test_isMaskedValue_masksEmbeddingProviderApiKeys() {
        assertTrue(AdminSysteminfoAction.isMaskedValue("content_chunker.embedding.openai.api.key"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("content_chunker.embedding.gemini.api.key"));
        // The rule matches on shape, not on a fixed provider list, so an as-yet-unknown
        // provider is masked by default too.
        assertTrue(AdminSysteminfoAction.isMaskedValue("content_chunker.embedding.anthropic.api.key"));
    }

    @Test
    public void test_isMaskedValue_masksLlmProviderApiKeys() {
        // The RAG chat API keys live in fess_config.properties, so the embedding-side rule -
        // anchored on the content_chunker.embedding. prefix - never matched them and every
        // fess-llm-* plugin's chat key was rendered in cleartext one section away from its
        // masked embedding counterpart.
        assertTrue(AdminSysteminfoAction.isMaskedValue("rag.llm.openai.api.key"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("rag.llm.gemini.api.key"));
        // Shape, not a fixed provider list, so a future provider is masked by default.
        assertTrue(AdminSysteminfoAction.isMaskedValue("rag.llm.ollama.api.key"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("rag.llm.anthropic.api.key"));
    }

    @Test
    public void test_isMaskedValue_masksSsoClientCredentials() {
        // Entra ID stores its client secret in conf/system.properties just like OpenID
        // Connect does, so it has to be masked on the same screens.
        assertTrue(AdminSysteminfoAction.isMaskedValue("entraid.client.id"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("entraid.client.secret"));
        // The legacy Azure AD keys are still read as a fallback by EntraIdAuthenticator,
        // so an upgraded installation can still hold values under these names.
        assertTrue(AdminSysteminfoAction.isMaskedValue("aad.client.id"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("aad.client.secret"));
        // The rule matches on shape, so an SSO provider added later is masked by default.
        assertTrue(AdminSysteminfoAction.isMaskedValue("okta.client.secret"));
    }

    @Test
    public void test_isMaskedValue_masksPrivateKeyMaterial() {
        // The SAML SP private key signs this SP's AuthnRequests and decrypts the assertions an
        // IdP encrypts for it. It lives in conf/system.properties next to saml.sp.x509cert and
        // was rendered in cleartext on the admin screen and in the bug report.
        assertTrue(AdminSysteminfoAction.isMaskedValue("saml.sp.privatekey"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("saml.keystore.key.password"));
        // The rule matches on shape, so a private key added under another name is masked too.
        assertTrue(AdminSysteminfoAction.isMaskedValue("something.else.privatekey"));
    }

    @Test
    public void test_isMaskedValue_masksSearchEngineAndInitialUserPasswords() {
        // index.user.initial_password (default "admin") and search_engine.password are
        // fess_config.properties settings like http.proxy.password, and were listed in cleartext.
        assertTrue(AdminSysteminfoAction.isMaskedValue("index.user.initial_password"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("search_engine.password"));
        // The JVM argument spelling of the same settings.
        assertTrue(AdminSysteminfoAction.isMaskedValue("fess.config.index.user.initial_password"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("fess.system.index.user.initial_password"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("fess.config.search_engine.password"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("fess.system.search_engine.password"));
    }

    @Test
    public void test_isMaskedValue_keepsPasswordRelatedSettingsThatAreNotSecretsVisible() {
        // A user name is not a secret, as for content_chunker.embedding.opensearch.username.
        assertFalse(AdminSysteminfoAction.isMaskedValue("search_engine.username"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("http.proxy.username"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("index.user.index"));
        // Settings whose name merely contains "password" or "key" configure a rule or a flag.
        // The log pattern app.log.sensitive.property.pattern (.*password.*|.*key.*|...) would
        // hide all of these on the page an administrator reads to diagnose the configuration.
        assertFalse(AdminSysteminfoAction.isMaskedValue("password.min.length"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("password.require.digit"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("app.password.algorithm"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("ldap.admin.sync.password"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("cookie.search.parameter.keys"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("authentication.admin.roles"));
    }

    @Test
    public void test_getFessPropItems_masksTheInitialUserPassword() {
        ComponentUtil.getSystemProperties().setProperty("search_engine.password", "hunter2");

        final List<Map<String, String>> itemList = AdminSysteminfoAction.getFessPropItems(ComponentUtil.getFessConfig());

        // index.user.initial_password comes from fess_config.properties, where its value is "admin".
        assertEquals(MASKED_VALUE, findValue(itemList, "index.user.initial_password"));
        assertEquals(MASKED_VALUE, findValue(itemList, "search_engine.password"));
        assertEquals("8", findValue(itemList, "password.min.length"));
        itemList.forEach(item -> assertFalse(item.get(Constants.ITEM_VALUE).contains("hunter2")));
    }

    @Test
    public void test_isMaskedValue_doesNotMaskOrdinaryKeys() {
        // Other content_chunker.embedding.* keys are plain diagnostic config, not credentials,
        // and must stay visible on the admin screen.
        assertFalse(AdminSysteminfoAction.isMaskedValue("content_chunker.embedding.name"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("content_chunker.embedding.dimension"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("content_chunker.embedding.opensearch.api.url"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("content_chunker.embedding.opensearch.username"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("content_chunker.embedding.opensearch.model.id"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("content_chunker.enabled"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("some.unrelated.key"));
        // The rag.llm.* rule is likewise narrow: only the API key is a credential. Endpoint,
        // model and tuning knobs stay readable, and rag.llm.name selects the provider.
        assertFalse(AdminSysteminfoAction.isMaskedValue("rag.llm.name"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("rag.llm.openai.api.url"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("rag.llm.openai.model"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("rag.llm.openai.retry.max"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("rag.chat.enabled"));

        // Certificates are public by definition -- the IdP's is published in its metadata and
        // the SP's is published in /sso/metadata -- so masking them would only make the admin
        // screen less useful for diagnosing a trust problem.
        assertFalse(AdminSysteminfoAction.isMaskedValue("saml.idp.x509cert"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("saml.sp.x509cert"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("saml.sp.base.url"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("saml.keystore.alias"));

        // The rest of the Entra ID settings are plain configuration, not credentials.
        assertFalse(AdminSysteminfoAction.isMaskedValue("entraid.tenant"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("entraid.authority"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("entraid.reply.url"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("entraid.permission.fields"));
    }

    @Test
    public void test_isMaskedValue_masksJvmArgumentSpellingOfSensitiveKeys() {
        // -Dfess.system.<key> and -Dfess.config.<key> carry the value of <key>, so the JVM
        // property list has to mask them under the same rule as the setting itself.
        assertTrue(AdminSysteminfoAction.isMaskedValue("fess.system.content_chunker.embedding.opensearch.password"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("fess.system.content_chunker.embedding.openai.api.key"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("fess.system.oic.client.secret"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("fess.config.rag.llm.openai.api.key"));
        assertTrue(AdminSysteminfoAction.isMaskedValue("fess.config.app.cipher.key"));

        // The prefix alone does not make a key sensitive.
        assertFalse(AdminSysteminfoAction.isMaskedValue("fess.system.content_chunker.embedding.name"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("fess.config.rag.llm.name"));
        assertFalse(AdminSysteminfoAction.isMaskedValue("fess.system."));
        assertFalse(AdminSysteminfoAction.isMaskedValue("fess.home"));
    }

    @Test
    public void test_getPropItems_masksSensitiveJvmArguments() {
        final String passwordKey = "fess.system.content_chunker.embedding.opensearch.password";
        final String apiKeyKey = "fess.config.rag.llm.openai.api.key";
        final String plainKey = "fess.system.content_chunker.embedding.dimension";
        System.setProperty(passwordKey, "hunter2");
        System.setProperty(apiKeyKey, "sk-chat-secret");
        System.setProperty(plainKey, "384");
        try {
            final List<Map<String, String>> itemList = AdminSysteminfoAction.getPropItems();

            // The key stays listed so an operator can see it is set, but not its value.
            assertEquals(MASKED_VALUE, findValue(itemList, passwordKey));
            assertEquals(MASKED_VALUE, findValue(itemList, apiKeyKey));
            // An ordinary diagnostic property is unaffected.
            assertEquals("384", findValue(itemList, plainKey));
            assertEquals(System.getProperty("java.vm.name"), findValue(itemList, "java.vm.name"));
            itemList.forEach(item -> {
                assertFalse(item.get(Constants.ITEM_VALUE).contains("hunter2"));
                assertFalse(item.get(Constants.ITEM_VALUE).contains("sk-chat-secret"));
            });
        } finally {
            System.clearProperty(passwordKey);
            System.clearProperty(apiKeyKey);
            System.clearProperty(plainKey);
        }
    }

    @Test
    public void test_createMaskedItem_masksSensitiveLabelsOnly() {
        // The environment variable list builds its items the same way as the JVM property list.
        final Map<String, String> secret = AdminSysteminfoAction.createMaskedItem("app.cipher.key", "hunter2");
        assertEquals("app.cipher.key", secret.get(Constants.ITEM_LABEL));
        assertEquals(MASKED_VALUE, secret.get(Constants.ITEM_VALUE));

        final Map<String, String> plain = AdminSysteminfoAction.createMaskedItem("PATH", "/usr/bin");
        assertEquals("PATH", plain.get(Constants.ITEM_LABEL));
        assertEquals("/usr/bin", plain.get(Constants.ITEM_VALUE));

        final Map<String, String> nullValue = AdminSysteminfoAction.createMaskedItem("PATH", null);
        assertEquals("", nullValue.get(Constants.ITEM_VALUE));
        assertEquals("", AdminSysteminfoAction.createMaskedItem(null, "x").get(Constants.ITEM_LABEL));
    }

    @Test
    public void test_getBugReportItems_masksSensitiveKeysInsteadOfLeakingCleartext() {
        ComponentUtil.getSystemProperties().setProperty("content_chunker.embedding.openai.api.key", "sk-super-secret");
        ComponentUtil.getSystemProperties().setProperty("content_chunker.embedding.gemini.api.key", "gm-super-secret");
        ComponentUtil.getSystemProperties().setProperty("content_chunker.embedding.opensearch.password", "hunter2");
        ComponentUtil.getSystemProperties().setProperty("rag.llm.openai.api.key", "sk-chat-secret");
        ComponentUtil.getSystemProperties().setProperty("entraid.client.secret", "entraid-super-secret");
        ComponentUtil.getSystemProperties().setProperty("entraid.tenant", "contoso.onmicrosoft.com");
        ComponentUtil.getSystemProperties().setProperty("saml.sp.privatekey", "MIIEvgIBADANBgkq-private-key");
        ComponentUtil.getSystemProperties().setProperty("saml.sp.x509cert", "MIIDGTCCAgGg-public-cert");
        ComponentUtil.getSystemProperties().setProperty("content_chunker.embedding.dimension", "768");

        final List<Map<String, String>> itemList = AdminSysteminfoAction.getBugReportItems();

        // Sensitive keys still show up (so a bug report can confirm they are configured),
        // but with the same masked placeholder used on the admin screen, not their real value.
        assertEquals(MASKED_VALUE, findValue(itemList, "content_chunker.embedding.openai.api.key"));
        assertEquals(MASKED_VALUE, findValue(itemList, "content_chunker.embedding.gemini.api.key"));
        assertEquals(MASKED_VALUE, findValue(itemList, "content_chunker.embedding.opensearch.password"));
        assertEquals(MASKED_VALUE, findValue(itemList, "rag.llm.openai.api.key"));
        assertEquals(MASKED_VALUE, findValue(itemList, "entraid.client.secret"));
        assertEquals(MASKED_VALUE, findValue(itemList, "saml.sp.privatekey"));

        // An ordinary diagnostic key is unaffected and keeps its real value.
        assertEquals("MIIDGTCCAgGg-public-cert", findValue(itemList, "saml.sp.x509cert"));
        assertEquals("768", findValue(itemList, "content_chunker.embedding.dimension"));
        assertEquals("contoso.onmicrosoft.com", findValue(itemList, "entraid.tenant"));
    }

    private String findValue(final List<Map<String, String>> itemList, final String label) {
        return itemList.stream()
                .filter(item -> label.equals(item.get(Constants.ITEM_LABEL)))
                .map(item -> item.get(Constants.ITEM_VALUE))
                .findFirst()
                .orElse(null);
    }
}
