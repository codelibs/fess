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
package org.codelibs.fess.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * A specialized ArrayList for storing document data with additional metadata.
 * This class extends ArrayList to hold document maps while tracking content size
 * and processing time metrics. It's used throughout the Fess search system to
 * manage collections of search results and crawled documents.
 *
 */
public class DocList extends ArrayList<Map<String, Object>> {

    /** Serial version UID for serialization */
    private static final long serialVersionUID = 1L;

    /** Total content size of all documents in this list */
    private long contentSize = 0;

    /** Total processing time for all documents in this list */
    private long processingTime = 0;

    /**
     * Default constructor for DocList.
     * Creates a new empty document list with zero content size and processing time.
     */
    public DocList() {
        super();
    }

    /**
     * Clears all documents from the list and resets metrics.
     * Removes all documents and resets content size and processing time to zero.
     */
    @Override
    public void clear() {
        super.clear();
        contentSize = 0;
        processingTime = 0;
    }

    /**
     * Gets the total content size of all documents in this list.
     *
     * @return the total content size in bytes
     */
    public long getContentSize() {
        return contentSize;
    }

    /**
     * Adds to the total content size of this document list.
     *
     * @param contentSize the content size to add in bytes
     */
    public void addContentSize(final long contentSize) {
        this.contentSize += contentSize;
    }

    /**
     * Gets the total processing time for all documents in this list.
     *
     * @return the total processing time in milliseconds
     */
    public long getProcessingTime() {
        return processingTime;
    }

    /**
     * Adds to the total processing time of this document list.
     *
     * @param processingTime the processing time to add in milliseconds
     */
    public void addProcessingTime(final long processingTime) {
        this.processingTime += processingTime;
    }

    /**
     * Returns a string representation of this DocList including metrics and content.
     *
     * @return a string representation including content size, processing time, and elements
     */
    @Override
    public String toString() {
        return "DocList [contentSize=" + contentSize + ", processingTime=" + processingTime + ", elementData="
                + Arrays.toString(toArray(new Map[size()])) + "]";
    }

}
