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
    }

    @Test
    public void test_getBugReportItems_masksSensitiveKeysInsteadOfLeakingCleartext() {
        ComponentUtil.getSystemProperties().setProperty("content_chunker.embedding.openai.api.key", "sk-super-secret");
        ComponentUtil.getSystemProperties().setProperty("content_chunker.embedding.gemini.api.key", "gm-super-secret");
        ComponentUtil.getSystemProperties().setProperty("content_chunker.embedding.opensearch.password", "hunter2");
        ComponentUtil.getSystemProperties().setProperty("rag.llm.openai.api.key", "sk-chat-secret");
        ComponentUtil.getSystemProperties().setProperty("content_chunker.embedding.dimension", "768");

        final List<Map<String, String>> itemList = AdminSysteminfoAction.getBugReportItems();

        // Sensitive keys still show up (so a bug report can confirm they are configured),
        // but with the same masked placeholder used on the admin screen, not their real value.
        assertEquals(MASKED_VALUE, findValue(itemList, "content_chunker.embedding.openai.api.key"));
        assertEquals(MASKED_VALUE, findValue(itemList, "content_chunker.embedding.gemini.api.key"));
        assertEquals(MASKED_VALUE, findValue(itemList, "content_chunker.embedding.opensearch.password"));
        assertEquals(MASKED_VALUE, findValue(itemList, "rag.llm.openai.api.key"));

        // An ordinary diagnostic key is unaffected and keeps its real value.
        assertEquals("768", findValue(itemList, "content_chunker.embedding.dimension"));
    }

    private String findValue(final List<Map<String, String>> itemList, final String label) {
        return itemList.stream()
                .filter(item -> label.equals(item.get(Constants.ITEM_LABEL)))
                .map(item -> item.get(Constants.ITEM_VALUE))
                .findFirst()
                .orElse(null);
    }
}
