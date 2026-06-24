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

/**
 * Result of resolving a request {@code Origin} against the CORS allow list.
 * Carries both the handler to invoke and the {@link CorsMatchType} that decides
 * whether the Origin is reflected (EXACT) or a literal {@code "*"} is returned (WILDCARD).
 */
public class CorsResolution {

    private final CorsHandler handler;

    private final CorsMatchType matchType;

    /**
     * Creates a resolution.
     *
     * @param handler the handler to process the request
     * @param matchType how the origin matched the allow list
     */
    public CorsResolution(final CorsHandler handler, final CorsMatchType matchType) {
        this.handler = handler;
        this.matchType = matchType;
    }

    /**
     * Returns the handler to process the request.
     *
     * @return the CORS handler
     */
    public CorsHandler getHandler() {
        return handler;
    }

    /**
     * Returns how the origin matched the allow list.
     *
     * @return the match type
     */
    public CorsMatchType getMatchType() {
        return matchType;
    }
}
