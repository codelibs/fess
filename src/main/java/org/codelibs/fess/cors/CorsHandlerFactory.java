/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CorsHandlerFactory {
    private static final Logger logger = LogManager.getLogger(CorsHandlerFactory.class);

    protected Map<String, CorsHandler> handerMap = new HashMap<>();

    public void add(final String origin, final CorsHandler handler) {
        if (logger.isDebugEnabled()) {
            logger.debug("Loaded {}", origin);
        }
        handerMap.put(origin, handler);
    }

    public CorsHandler get(final String origin) {
        final CorsHandler handler = handerMap.get(origin);
        if (handler != null) {
            return handler;
        }
        return handerMap.get("*");
    }
}
