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
package org.codelibs.fess.ingest;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Factory class for managing and organizing document ingesters.
 * The factory maintains a sorted collection of ingesters based on their priority
 * and provides methods to add new ingesters and retrieve the current collection.
 *
 * Ingesters are automatically sorted by priority, with lower numbers having higher priority.
 */
public class IngestFactory {
    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(IngestFactory.class);

    /** Array of registered ingesters, sorted by priority */
    private Ingester[] ingesters = {};

    /**
     * Default constructor.
     */
    public IngestFactory() {
        // Default constructor
    }

    /**
     * Adds an ingester to the factory.
     * The ingester is inserted into the collection and the array is re-sorted by priority.
     * This method is thread-safe.
     *
     * @param ingester the ingester to add
     */
    public synchronized void add(final Ingester ingester) {
        if (logger.isDebugEnabled()) {
            logger.debug("Loaded Ingester: {}", ingester.getClass().getSimpleName());
        }
        final Ingester[] newIngesters = Arrays.copyOf(ingesters, ingesters.length + 1);
        newIngesters[ingesters.length] = ingester;
        Arrays.sort(newIngesters, (o1, o2) -> o1.priority - o2.priority);
        ingesters = newIngesters;
    }

    /**
     * Returns the array of registered ingesters sorted by priority.
     * The returned array contains all ingesters in priority order
     * (lower priority numbers first).
     *
     * @return the sorted array of ingesters
     */
    public Ingester[] getIngesters() {
        return ingesters;
    }
}
