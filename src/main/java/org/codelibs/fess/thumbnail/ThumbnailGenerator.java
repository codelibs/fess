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
package org.codelibs.fess.thumbnail;

import java.io.File;
import java.util.Map;

import org.codelibs.core.misc.Tuple3;

/**
 * Interface for thumbnail generation implementations.
 * Provides methods for creating thumbnails from various document types.
 */
public interface ThumbnailGenerator {

    /**
     * Gets the name of this thumbnail generator.
     * @return The generator name.
     */
    String getName();

    boolean generate(String thumbnailId, File outputFile);

    /**
     * Checks if this generator can handle the given document.
     * @param docMap The document map containing metadata.
     * @return True if this generator can handle the document, false otherwise.
     */
    boolean isTarget(Map<String, Object> docMap);

    /**
     * Checks if this thumbnail generator is available for use.
     * @return True if available, false otherwise.
     */
    boolean isAvailable();

    void destroy();

    /**
     * Creates a thumbnail generation task.
     * @param path The path to the source document.
     * @param docMap The document map containing metadata.
     * @return A tuple containing task information.
     */
    Tuple3<String, String, String> createTask(String path, Map<String, Object> docMap);
}
