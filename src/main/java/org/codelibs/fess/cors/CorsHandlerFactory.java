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
package org.codelibs.fess.cors;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Factory for managing CORS handlers based on origin.
 * Maintains a registry of CORS handlers for different origins and provides lookup functionality.
 * This class is thread-safe and supports null origins.
 */
public class CorsHandlerFactory {

    /**
     * Creates a new instance of CorsHandlerFactory.
     */
    public CorsHandlerFactory() {
        // Default constructor
    }

    private static final Logger logger = LogManager.getLogger(CorsHandlerFactory.class);

    /**
     * Map of origin patterns to their corresponding CORS handlers.
     * Thread-safe to support dynamic handler registration.
     */
    protected Map<String, CorsHandler> handlerMap = new ConcurrentHashMap<>();

    /**
     * Handler for null origin.
     * Since ConcurrentHashMap does not support null keys, we store null origin separately.
     */
    protected final AtomicReference<CorsHandler> nullOriginHandler = new AtomicReference<>();

    /**
     * Adds a CORS handler for the specified origin.
     *
     * @param origin the origin pattern (can be "*" for wildcard, or null)
     * @param handler the CORS handler to associate with the origin (can be null to remove)
     */
    public void add(final String origin, final CorsHandler handler) {
        if (origin == null) {
            // Handle null origin separately since ConcurrentHashMap does not support null keys
            nullOriginHandler.set(handler);
            if (logger.isDebugEnabled()) {
                logger.debug("Loaded null origin");
            }
            return;
        }

        if (handler == null) {
            // ConcurrentHashMap does not support null values, remove the entry instead
            handlerMap.remove(origin);
            if (logger.isDebugEnabled()) {
                logger.debug("Removed {}", origin);
            }
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Loaded {}", origin);
        }
        handlerMap.put(origin, handler);
    }

    /**
     * Gets the CORS handler for the specified origin.
     * If no specific handler is found, returns the wildcard handler.
     *
     * @param origin the origin to look up
     * @return the CORS handler for the origin, or null if none found
     */
    public CorsHandler get(final String origin) {
        if (origin == null) {
            // Check null origin handler first
            final CorsHandler handler = nullOriginHandler.get();
            if (handler != null) {
                return handler;
            }
            // Fall back to wildcard
            return handlerMap.get("*");
        }

        final CorsHandler handler = handlerMap.get(origin);
        if (handler != null) {
            return handler;
        }
        return handlerMap.get("*");
    }
}
