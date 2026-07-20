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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.helper.ContentChunkConstants;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Manager class for resolving the active {@link Chunker} implementation.
 *
 * Mirrors {@code org.codelibs.fess.llm.LlmClientManager}'s single-active-
 * implementation-selected-by-name pattern: exactly one chunker is active,
 * chosen via the {@code content_chunker.chunker.name} system property
 * (default {@code "length"}).
 */
public class ChunkerManager {

    private static final Logger logger = LogManager.getLogger(ChunkerManager.class);

    /** The list of registered chunkers. */
    protected final List<Chunker> chunkerList = new CopyOnWriteArrayList<>();

    /**
     * Default constructor.
     */
    public ChunkerManager() {
        // Default constructor
    }

    /**
     * Gets the chunker instance for the configured chunker type.
     *
     * @return the chunker instance, or null if not found
     */
    public Chunker getChunker() {
        final String chunkerType = getChunkerType();
        final String name = chunkerType + "Chunker";
        if (ComponentUtil.hasComponent(name)) {
            final Chunker chunker = ComponentUtil.getComponent(name);
            if (logger.isTraceEnabled()) {
                logger.trace("[Chunk] Chunker found via DI. componentName={}, chunkerName={}", name, chunker.getName());
            }
            return chunker;
        }
        for (final Chunker chunker : chunkerList) {
            if (chunkerType.equals(chunker.getName())) {
                if (logger.isTraceEnabled()) {
                    logger.trace("[Chunk] Chunker found via registration. name={}", chunker.getName());
                }
                return chunker;
            }
        }
        logger.warn("[Chunk] Chunker not found. componentName={}", name);
        return null;
    }

    /**
     * Gets the configured chunker type from the system configuration.
     *
     * @return the chunker type string (e.g. "length")
     */
    protected String getChunkerType() {
        return ComponentUtil.getFessConfig().getSystemProperty(ContentChunkConstants.CHUNKER_NAME, "length");
    }

    /**
     * Gets all registered chunkers.
     *
     * @return array of all registered chunkers
     */
    public Chunker[] getChunkers() {
        return chunkerList.toArray(new Chunker[chunkerList.size()]);
    }

    /**
     * Registers a chunker with this manager.
     *
     * @param chunker the chunker to register
     */
    public void register(final Chunker chunker) {
        if (logger.isDebugEnabled()) {
            logger.debug("Loaded Chunker: {}", chunker.getClass().getSimpleName());
        }
        chunkerList.add(chunker);
    }

    /**
     * Splits content using the configured chunker.
     *
     * @param content the content to split
     * @return the list of chunks, or an empty list if no chunker is resolved
     */
    public List<String> split(final String content) {
        final Chunker chunker = getChunker();
        if (chunker == null) {
            return Collections.emptyList();
        }
        return chunker.split(content);
    }
}
