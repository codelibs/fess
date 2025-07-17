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
package org.codelibs.fess.thumbnail.impl;

import java.io.File;

/**
 * Empty implementation of thumbnail generator that does not generate any thumbnails.
 * This class is used as a no-op thumbnail generator when thumbnail generation is disabled
 * or when no specific thumbnail generator is configured.
 */
public class EmptyGenerator extends BaseThumbnailGenerator {

    /**
     * Default constructor.
     */
    public EmptyGenerator() {
        super();
    }

    /**
     * Generates a thumbnail for the specified ID and output file.
     * This implementation always returns false, indicating no thumbnail was generated.
     *
     * @param thumbnailId the ID of the thumbnail to generate
     * @param outputFile the output file where the thumbnail should be saved
     * @return false always, as this generator does not create thumbnails
     */
    @Override
    public boolean generate(final String thumbnailId, final File outputFile) {
        return false;
    }

    /**
     * Destroys this generator and releases any resources.
     * This implementation does nothing as no resources are held.
     */
    @Override
    public void destroy() {
        // nothing
    }
}
