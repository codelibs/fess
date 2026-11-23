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

import java.util.Locale;

/**
 * Tells which settings hold a secret, by the key of the setting (the key of
 * {@code fess_config.properties} or {@code system.properties}, such as {@code search_engine.password}).
 *
 * <p>This class has no dependency on the web application or on the container, because the crawler,
 * suggest, thumbnail and chunk processes load the classes that use it and run without the servlet API.
 */
public class SecretKeyUtil {

    private SecretKeyUtil() {
    }

    /**
     * Checks whether the setting of a key holds a secret that must neither be shown nor be written on a
     * command line.
     *
     * @param key the key of the setting, without the {@code fess.config.} or {@code fess.system.} prefix
     * @return true if the value of the setting is a secret
     */
    public static boolean isSecretKey(final String key) {
        return "http.proxy.password".equals(key) //
                || "search_engine.password".equals(key) //
                || "index.user.initial_password".equals(key) //
                || "ldap.admin.security.credentials".equals(key) //
                || "spnego.preauth.password".equals(key) //
                || "app.cipher.key".equals(key) //
                || "content_chunker.embedding.opensearch.password".equals(key) //
                || isSsoClientCredential(key) //
                || isPrivateKeyMaterial(key) //
                || isEmbeddingApiKey(key) //
                || isLlmApiKey(key) //
                || isWebhookUrls(key) //
                || isSecretName(key);
    }

    /**
     * Checks if a key holds private key material, e.g. {@code saml.sp.privatekey} or
     * {@code saml.keystore.key.password}. Matching on the {@code .privatekey} /
     * {@code .key.password} shape, instead of listing every key by name, means a future
     * setting that carries a private key is masked by default.
     *
     * <p>The SAML SP private key was rendered in cleartext under System Info &gt; Config Info,
     * and was also copied verbatim into the bug report that users paste into public issues.
     * That key signs this SP's AuthnRequests and decrypts the assertions an IdP encrypts for
     * it, so disclosing it is worse than disclosing a client secret: whoever holds it can mint
     * messages this SP will treat as its own and read assertions intended for it.
     *
     * @param key the property key to check
     * @return true if the key matches the private key material shape
     */
    public static boolean isPrivateKeyMaterial(final String key) {
        return key != null && (key.endsWith(".privatekey") || key.endsWith(".key.password"));
    }

    /**
     * Checks if a key is an SSO provider's client credential, e.g. {@code oic.client.secret} or
     * {@code entraid.client.secret}. Matching on the {@code <provider>.client.id} /
     * {@code <provider>.client.secret} shape, instead of listing every provider by name, means a
     * future SSO provider's credential is masked by default.
     *
     * <p>Only OpenID Connect was listed by name before, so the Entra ID client secret was rendered
     * in cleartext under System Info &gt; Config Info, and was also copied verbatim into the bug
     * report that users paste into public issues. The legacy {@code aad.*} keys are covered by the
     * same shape because {@code EntraIdAuthenticator}, now in the fess-sso-entraid plugin,
     * still reads them as a fallback.
     *
     * @param key the property key to check
     * @return true if the key matches the SSO client credential shape
     */
    public static boolean isSsoClientCredential(final String key) {
        return key != null && (key.endsWith(".client.id") || key.endsWith(".client.secret"));
    }

    /**
     * Checks if a key is an embedding provider's API key, e.g.
     * {@code content_chunker.embedding.openai.api.key} or
     * {@code content_chunker.embedding.gemini.api.key}. Matching on the
     * {@code content_chunker.embedding.<provider>.api.key} shape, instead of listing every
     * provider by name, means a future embedding provider's API key is masked by default.
     *
     * @param key the property key to check
     * @return true if the key matches the embedding provider API key shape
     */
    public static boolean isEmbeddingApiKey(final String key) {
        return key != null && key.startsWith("content_chunker.embedding.") && key.endsWith(".api.key");
    }

    /**
     * Checks if a key is a RAG LLM provider's API key, e.g. {@code rag.llm.openai.api.key} or
     * {@code rag.llm.gemini.api.key}. Matching on the {@code rag.llm.<provider>.api.key} shape,
     * instead of listing every provider by name, means a future LLM provider's API key is masked
     * by default.
     *
     * <p>These live in {@code fess_config.properties} rather than {@code conf/system.properties},
     * which is why they need their own rule: the embedding-side rule above is anchored on the
     * {@code content_chunker.embedding.} prefix and never matched them, so every
     * {@code fess-llm-*} plugin's chat API key was rendered in cleartext under
     * System Info &gt; Config Info &gt; App Properties while its embedding counterpart one
     * section away was masked.
     *
     * @param key the property key to check
     * @return true if the key matches the LLM provider API key shape
     */
    public static boolean isLlmApiKey(final String key) {
        return key != null && key.startsWith("rag.llm.") && key.endsWith(".api.key");
    }

    /**
     * Checks if a key holds notification webhook URLs, e.g. {@code slack.webhook.urls} or
     * {@code google.chat.webhook.urls}. A webhook URL carries its own credential in the path, so
     * whoever reads it can post to the channel.
     *
     * @param key the property key to check
     * @return true if the key matches the webhook URLs shape
     */
    public static boolean isWebhookUrls(final String key) {
        return key != null && key.endsWith(".webhook.urls");
    }

    /**
     * Checks if a name says that its value is a secret, e.g. the {@code storage.secretkey} setting
     * or the {@code AWS_SECRET_ACCESS_KEY} environment variable. Matching on the word, ignoring
     * case, instead of listing every name, means the variables other SDKs read, such as
     * {@code AZURE_CLIENT_SECRET}, are masked by default.
     *
     * <p>{@code storage.secretkey} was rendered in cleartext under System Info &gt; Config Info,
     * and so was the {@code AWS_SECRET_ACCESS_KEY} variable under Environment Variables; both were
     * also copied verbatim into the bug report that users paste into public issues. The words
     * {@code password}, {@code key} and {@code token} are deliberately not matched like this: many
     * settings whose names contain them configure a rule or a flag and would be hidden from the
     * page an administrator reads to diagnose the configuration. No Fess setting is named
     * {@code secret} unless it holds one.
     *
     * @param key the property key or environment variable name to check
     * @return true if the name contains the word secret
     */
    public static boolean isSecretName(final String key) {
        return key != null && key.toLowerCase(Locale.ROOT).contains("secret");
    }
}
