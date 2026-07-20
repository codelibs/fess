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
package org.codelibs.fess.embedding;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.helper.ContentChunkConstants;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Manager class for coordinating {@link EmbeddingClient} operations.
 *
 * Mirrors {@code org.codelibs.fess.llm.LlmClientManager} exactly: resolves
 * the configured embedding provider by name (component lookup, then
 * registered-list fallback), selected via the
 * {@code content_chunker.embedding.name} system property (default
 * {@code "ollama"}) — an independent setting from {@code rag.llm.name}.
 */
public class EmbeddingClientManager {

    private static final Logger logger = LogManager.getLogger(EmbeddingClientManager.class);

    /** The list of registered embedding clients. */
    protected final List<EmbeddingClient> clientList = new CopyOnWriteArrayList<>();

    /**
     * Default constructor.
     */
    public EmbeddingClientManager() {
        // Default constructor
    }

    /**
     * Checks whether embedding functionality is available and configured.
     *
     * @return true if content chunking is enabled and a client is available
     */
    public boolean available() {
        final String embeddingType = getEmbeddingType();
        if (Constants.NONE.equals(embeddingType)) {
            return false;
        }
        if (!isContentChunkerEnabled()) {
            return false;
        }
        final EmbeddingClient client = getClient();
        return client != null && client.isAvailable();
    }

    /**
     * Gets the embedding client instance for the configured embedding type.
     *
     * @return the embedding client instance, or null if not found
     */
    public EmbeddingClient getClient() {
        final String embeddingType = getEmbeddingType();
        final String name = embeddingType + "EmbeddingClient";
        if (ComponentUtil.hasComponent(name)) {
            final EmbeddingClient client = ComponentUtil.getComponent(name);
            if (logger.isTraceEnabled()) {
                logger.trace("[Embedding] EmbeddingClient found via DI. componentName={}, clientName={}", name, client.getName());
            }
            return client;
        }
        for (final EmbeddingClient client : clientList) {
            if (embeddingType.equals(client.getName())) {
                if (logger.isTraceEnabled()) {
                    logger.trace("[Embedding] EmbeddingClient found via registration. name={}", client.getName());
                }
                return client;
            }
        }
        logger.warn("[Embedding] EmbeddingClient not found. componentName={}", name);
        return null;
    }

    /**
     * Gets the configured embedding type from the system configuration.
     *
     * @return the embedding type string (e.g. "ollama")
     */
    protected String getEmbeddingType() {
        return ComponentUtil.getFessConfig().getSystemProperty(ContentChunkConstants.EMBEDDING_NAME, "ollama");
    }

    /**
     * Checks if content chunking is enabled.
     *
     * @return true if enabled
     */
    protected boolean isContentChunkerEnabled() {
        return Boolean.parseBoolean(ComponentUtil.getFessConfig().getSystemProperty(ContentChunkConstants.ENABLED, "false"));
    }

    /**
     * Gets all registered embedding clients.
     *
     * @return array of all registered embedding clients
     */
    public EmbeddingClient[] getClients() {
        return clientList.toArray(new EmbeddingClient[clientList.size()]);
    }

    /**
     * Registers an embedding client with this manager.
     *
     * @param client the embedding client to register
     */
    public void register(final EmbeddingClient client) {
        if (logger.isDebugEnabled()) {
            logger.debug("Loaded EmbeddingClient: {}", client.getClass().getSimpleName());
        }
        clientList.add(client);
    }

    /**
     * Generates embedding vectors for document/chunk texts using the configured embedding client.
     *
     * @param texts the document/chunk texts to embed
     * @return the embedding vectors
     * @throws EmbeddingException if the embedding client is not available or the call fails
     */
    public List<float[]> embedDocuments(final List<String> texts) {
        final EmbeddingClient client = getAvailableClient();
        return client.embedDocuments(texts);
    }

    /**
     * Generates an embedding vector for a search/chat query using the configured embedding client.
     *
     * @param query the query text to embed
     * @return the embedding vector
     * @throws EmbeddingException if the query is blank, the embedding client is not available, or the call fails
     */
    public float[] embedQuery(final String query) {
        if (StringUtil.isBlank(query)) {
            // Guard before the List.of(query) call below, whose immutable-list factory rejects a null
            // element with a raw NullPointerException. A null/blank query is a caller misuse; report
            // it the same way the class reports its other misuse (EmbeddingException), not as an NPE.
            throw new EmbeddingException("Query text for embedding must not be blank");
        }
        final EmbeddingClient client = getAvailableClient();
        final List<float[]> vectors = client.embedQuery(List.of(query));
        if (vectors.isEmpty()) {
            throw new EmbeddingException("Embedding client returned no vector for query");
        }
        return vectors.get(0);
    }

    /**
     * Gets the available embedding client, performing a single lookup.
     *
     * @return the available embedding client
     * @throws EmbeddingException if no embedding client is available
     */
    protected EmbeddingClient getAvailableClient() {
        final String embeddingType = getEmbeddingType();
        if (Constants.NONE.equals(embeddingType)) {
            throw new EmbeddingException("Embedding client is not available");
        }
        if (!isContentChunkerEnabled()) {
            throw new EmbeddingException("Embedding client is not available");
        }
        final EmbeddingClient client = getClient();
        if (client == null || !client.isAvailable()) {
            throw new EmbeddingException("Embedding client is not available");
        }
        return client;
    }
}
