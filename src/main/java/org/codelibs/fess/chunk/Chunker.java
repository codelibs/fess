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
package org.codelibs.fess.chunk;

import java.util.List;

/**
 * Interface for content chunking strategies.
 * Implementations split a document's extracted text content into a list of
 * chunk strings suitable for independent embedding.
 *
 * Unlike {@link org.codelibs.fess.ingest.Ingester}, which runs every registered
 * instance in priority order, exactly one {@link Chunker} is active at a time,
 * selected by name via {@link ChunkerManager} — chunking strategies are not
 * cumulative.
 */
public interface Chunker {

    /**
     * Splits the given content into a list of chunk strings.
     *
     * @param content the document content to split
     * @return the list of chunks, in order; an empty list if content is blank
     */
    List<String> split(String content);

    /**
     * Returns the name of this chunker (e.g. "length"), used for resolution
     * via the {@code content_chunker.chunker.name} system property.
     *
     * @return the chunker name
     */
    String getName();

    /**
     * Registers this chunker with the {@link ChunkerManager}.
     * Called via postConstruct.
     */
    void register();
}
