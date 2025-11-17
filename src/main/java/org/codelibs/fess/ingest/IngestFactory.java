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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Factory class for managing and organizing document ingesters.
 * The factory maintains a sorted collection of ingesters based on their priority
 * and provides methods to add new ingesters and retrieve the current collection.
 *
 * <p>Ingesters are automatically sorted by priority, with lower numbers having higher priority.
 * This class is thread-safe for concurrent access.</p>
 */
public class IngestFactory {
    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(IngestFactory.class);

    /** Comparator for sorting ingesters by priority */
    private static final Comparator<Ingester> PRIORITY_COMPARATOR = Comparator.comparingInt(Ingester::getPriority);

    /** List of registered ingesters, sorted by priority */
    private final List<Ingester> ingesterList = new ArrayList<>();

    /** Cached array of ingesters for efficient retrieval */
    private volatile Ingester[] cachedIngesters = new Ingester[0];

    /**
     * Default constructor.
     */
    public IngestFactory() {
        // Default constructor
    }

    /**
     * Adds an ingester to the factory.
     * The ingester is inserted into the collection and sorted by priority.
     * If an ingester with the same class already exists, it will be replaced.
     * This method is thread-safe.
     *
     * @param ingester the ingester to add (must not be null)
     * @throws IllegalArgumentException if ingester is null
     */
    public synchronized void add(final Ingester ingester) {
        if (ingester == null) {
            throw new IllegalArgumentException("Ingester cannot be null");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Loading {}", ingester.getClass().getSimpleName());
        }

        // Remove existing ingester of the same class if present
        ingesterList.removeIf(existing -> existing.getClass().equals(ingester.getClass()));

        // Add new ingester and sort by priority
        ingesterList.add(ingester);
        Collections.sort(ingesterList, PRIORITY_COMPARATOR);

        // Update cached array
        cachedIngesters = ingesterList.toArray(new Ingester[0]);

        if (logger.isDebugEnabled()) {
            logger.debug("Loaded {} with priority {}", ingester.getClass().getSimpleName(), ingester.getPriority());
        }
    }

    /**
     * Removes an ingester from the factory.
     * This method is thread-safe.
     *
     * @param ingester the ingester to remove
     * @return true if the ingester was removed, false otherwise
     */
    public synchronized boolean remove(final Ingester ingester) {
        if (ingester == null) {
            return false;
        }

        final boolean removed = ingesterList.remove(ingester);
        if (removed) {
            // Update cached array
            cachedIngesters = ingesterList.toArray(new Ingester[0]);

            if (logger.isDebugEnabled()) {
                logger.debug("Removed {}", ingester.getClass().getSimpleName());
            }
        }
        return removed;
    }

    /**
     * Removes an ingester by class type.
     * This method is thread-safe.
     *
     * @param ingesterClass the class of the ingester to remove
     * @return true if an ingester was removed, false otherwise
     */
    public synchronized boolean removeByClass(final Class<? extends Ingester> ingesterClass) {
        if (ingesterClass == null) {
            return false;
        }

        final boolean removed = ingesterList.removeIf(ingester -> ingester.getClass().equals(ingesterClass));
        if (removed) {
            // Update cached array
            cachedIngesters = ingesterList.toArray(new Ingester[0]);

            if (logger.isDebugEnabled()) {
                logger.debug("Removed ingester of type {}", ingesterClass.getSimpleName());
            }
        }
        return removed;
    }

    /**
     * Clears all registered ingesters.
     * This method is thread-safe.
     */
    public synchronized void clear() {
        ingesterList.clear();
        cachedIngesters = new Ingester[0];

        if (logger.isDebugEnabled()) {
            logger.debug("Cleared all ingesters");
        }
    }

    /**
     * Returns the number of registered ingesters.
     *
     * @return the number of ingesters
     */
    public int size() {
        return cachedIngesters.length;
    }

    /**
     * Returns a defensive copy of the array of registered ingesters sorted by priority.
     * The returned array contains all ingesters in priority order
     * (lower priority numbers first).
     *
     * <p>The returned array is a copy and modifications to it will not affect
     * the internal state of the factory.</p>
     *
     * @return a copy of the sorted array of ingesters
     */
    public Ingester[] getIngesters() {
        // Return a defensive copy of the cached array
        return cachedIngesters.clone();
    }
}
