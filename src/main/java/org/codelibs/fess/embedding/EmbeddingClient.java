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

/**
 * Interface for embedding-vector provider clients.
 * Implementations call an external or local embedding model to convert text
 * chunks into fixed-dimension float vectors.
 *
 * Mirrors {@code org.codelibs.fess.llm.LlmClient}'s shape; none of that
 * interface's RAG-workflow methods have an embedding analog.
 */
public interface EmbeddingClient {

    /**
     * Generates embedding vectors for the given document/chunk texts, in order.
     * Implementations may apply a provider- or model-specific prefix or other
     * preprocessing convention appropriate for embedding a passage that will be
     * indexed and later searched (e.g. Ollama's {@code nomic-embed-text} expects
     * a {@code "search_document: "} prefix); regardless of any such
     * preprocessing, the returned vectors are always {@link #getDimension()}
     * long.
     *
     * @param texts the document/chunk texts to embed
     * @return the list of vectors, one per input text, in the same order
     * @throws EmbeddingException if the provider call fails or returns an unusable response
     */
    List<float[]> embedDocuments(List<String> texts);

    /**
     * Generates embedding vectors for the given search/chat query texts, in
     * order. Implementations may apply a different (or no) prefix or other
     * preprocessing convention than {@link #embedDocuments(List)}, since a
     * query is not itself an indexed passage (e.g. Ollama's
     * {@code nomic-embed-text} expects a {@code "search_query: "} prefix
     * instead); regardless of any such preprocessing, the returned vectors are
     * always {@link #getDimension()} long, comparable against vectors produced
     * by {@link #embedDocuments(List)}.
     *
     * @param texts the query texts to embed
     * @return the list of vectors, one per input text, in the same order
     * @throws EmbeddingException if the provider call fails or returns an unusable response
     */
    List<float[]> embedQuery(List<String> texts);

    /**
     * Returns the vector dimension this client's vectors have.
     * Reads the required {@code content_chunker.embedding.dimension} config
     * value; does not make a live network probe call.
     *
     * @return the configured vector dimension
     * @throws EmbeddingException if the dimension is not configured or not a valid integer
     */
    int getDimension();

    /**
     * Returns the name of this embedding client (e.g. "ollama").
     *
     * @return the client name
     */
    String getName();

    /**
     * Checks if this embedding client is available and properly configured.
     *
     * @return true if available, false otherwise
     */
    boolean isAvailable();
}
