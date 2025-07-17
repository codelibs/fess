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
package org.codelibs.fess.entity;

import java.util.Map;

/**
 * Interface for search log events in the Fess search system.
 *
 * This interface defines the contract for search log event objects that can be
 * written to log files or stored in the search index. Implementations include
 * search logs, click logs, favorite logs, and user information logs.
 */
public interface SearchLogEvent {
    /**
     * Gets the unique identifier for this search log event.
     *
     * @return The event ID
     */
    String getId();

    /**
     * Gets the version number for this search log event.
     *
     * @return The version number, or null if not versioned
     */
    Long getVersionNo();

    /**
     * Converts this search log event to a source map for indexing or logging.
     *
     * @return Map representation of the event data
     */
    Map<String, Object> toSource();

    /**
     * Gets the type of this search log event.
     *
     * @return The event type (e.g., "search", "click", "favorite", "user_info")
     */
    String getEventType();
}
