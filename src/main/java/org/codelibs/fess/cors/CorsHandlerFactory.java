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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Factory for managing CORS handlers based on origin.
 * Maintains a registry of CORS handlers for different origins and provides lookup functionality.
 * This class is thread-safe.
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
     * Adds a CORS handler for the specified origin.
     *
     * @param origin the origin pattern (can be "*" for wildcard)
     * @param handler the CORS handler to associate with the origin
     */
    public void add(final String origin, final CorsHandler handler) {
        // ConcurrentHashMap does not allow null keys or values
        if (origin == null || handler == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Skipping null origin or handler: origin={}, handler={}", origin, handler);
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
        // Return null for null origin (ConcurrentHashMap does not allow null keys)
        if (origin == null) {
            return handlerMap.get("*");
        }

        final CorsHandler handler = handlerMap.get(origin);
        if (handler != null) {
            return handler;
        }
        return handlerMap.get("*");
    }
}
