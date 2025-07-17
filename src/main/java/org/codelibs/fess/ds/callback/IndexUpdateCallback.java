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
package org.codelibs.fess.ds.callback;

import java.util.Map;

import org.codelibs.fess.entity.DataStoreParams;

/**
 * Callback interface for handling index update operations during data store processing.
 * This interface provides methods for storing documents, tracking processing metrics,
 * and committing changes to the search index.
 */
public interface IndexUpdateCallback {

    /**
     * Stores a document in the search index with the specified parameters and data.
     *
     * @param paramMap the data store parameters containing configuration and metadata
     * @param dataMap the document data to be indexed as key-value pairs
     */
    void store(DataStoreParams paramMap, Map<String, Object> dataMap);

    /**
     * Returns the total number of documents processed by this callback.
     *
     * @return the document count
     */
    long getDocumentSize();

    /**
     * Returns the total execution time for index update operations.
     *
     * @return the execution time in milliseconds
     */
    long getExecuteTime();

    /**
     * Commits all pending index update operations to ensure data persistence.
     * This method should be called after all documents have been stored.
     */
    void commit();

}