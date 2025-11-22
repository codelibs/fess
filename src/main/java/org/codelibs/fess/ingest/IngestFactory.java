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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Factory class for managing and organizing document ingesters.
 * The factory maintains a sorted array of ingesters based on their priority.
 *
 * <p>Ingesters are automatically sorted by priority, with lower numbers having higher priority.
 * This class is designed for initialization-time registration only and is not thread-safe.</p>
 *
 * <p><strong>IMPORTANT:</strong> The {@code add()} method should only be called during
 * the initialization phase (typically via DI container) before the factory is accessed
 * by multiple threads. Runtime modification is not supported.</p>
 */
public class IngestFactory {
    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(IngestFactory.class);

    /** Comparator for sorting ingesters by priority */
    private static final Comparator<Ingester> PRIORITY_COMPARATOR = Comparator.comparingInt(Ingester::getPriority);

    /** Array of registered ingesters, sorted by priority */
    private Ingester[] ingesters = new Ingester[0];

    /**
     * Default constructor.
     */
    public IngestFactory() {
        // Default constructor
    }

    /**
     * Adds an ingester to the factory.
     * If an ingester with the same class already exists, it will be replaced.
     *
     * <p><strong>IMPORTANT:</strong> This method is NOT thread-safe. It should only
     * be called during the initialization phase before the factory is used.</p>
     *
     * @param ingester the ingester to add (must not be null)
     * @throws IllegalArgumentException if ingester is null
     */
    public void add(final Ingester ingester) {
        if (ingester == null) {
            throw new IllegalArgumentException("Ingester cannot be null");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Loading {}", ingester.getClass().getSimpleName());
        }

        // Convert to list for manipulation
        final List<Ingester> list = new ArrayList<>(Arrays.asList(ingesters));

        // Remove existing ingester of the same class if present
        list.removeIf(existing -> existing.getClass().equals(ingester.getClass()));

        // Add new ingester and sort by priority
        list.add(ingester);
        list.sort(PRIORITY_COMPARATOR);

        // Update array
        ingesters = list.toArray(new Ingester[0]);

        if (logger.isDebugEnabled()) {
            logger.debug("Loaded {} with priority {}", ingester.getClass().getSimpleName(), ingester.getPriority());
        }
    }

    /**
     * Returns the array of registered ingesters sorted by priority.
     * The returned array contains all ingesters in priority order
     * (lower priority numbers first).
     *
     * <p>The array is returned directly for read-only access. Modifications
     * to the array will not affect future calls to this method, but callers
     * should treat the array as immutable.</p>
     *
     * @return the array of ingesters
     */
    public Ingester[] getIngesters() {
        return ingesters;
    }
}
