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

import java.util.Map;

import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.entity.DataStoreParams;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Abstract base class for document ingesters that process and transform documents
 * before they are indexed. Ingesters can be used to modify document content,
 * extract additional metadata, or perform other transformations during the
 * indexing process.
 *
 * Ingesters are processed in priority order, with lower numbers having higher priority.
 */
public abstract class Ingester {

    /** Priority of this ingester (lower numbers = higher priority) */
    protected int priority = 99;

    /**
     * Default constructor.
     */
    public Ingester() {
        // Default constructor
    }

    /**
     * Gets the priority of this ingester.
     * Lower numbers indicate higher priority.
     *
     * @return the priority value
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the priority of this ingester.
     * Lower numbers indicate higher priority.
     *
     * @param priority the priority value to set
     */
    public void setPriority(final int priority) {
        this.priority = priority;
    }

    /**
     * Registers this ingester with the ingest factory.
     * This makes the ingester available for processing documents.
     */
    public void register() {
        getIngestFactory().add(this);
    }

    /**
     * Gets the ingest factory instance for managing ingesters.
     *
     * @return the ingest factory instance
     */
    protected IngestFactory getIngestFactory() {
        return ComponentUtil.getIngestFactory();
    }

    /**
     * Processes a result data object for web/file crawling.
     * Default implementation returns the target unchanged.
     *
     * @param target the result data to process
     * @param responseData the response data from crawling
     * @return the processed result data
     */
    public ResultData process(final ResultData target, final ResponseData responseData) {
        return target;
    }

    /**
     * Processes a document map for web/file crawling with access result.
     * Default implementation delegates to the basic process method.
     *
     * @param target the document data to process
     * @param accessResult the access result from crawling
     * @return the processed document data
     */
    public Map<String, Object> process(final Map<String, Object> target, final AccessResult<String> accessResult) {
        return process(target);
    }

    /**
     * Processes a document map for datastore operations.
     * Default implementation delegates to the basic process method.
     *
     * @param target the document data to process
     * @param params the data store parameters
     * @return the processed document data
     */
    public Map<String, Object> process(final Map<String, Object> target, final DataStoreParams params) {
        return process(target);
    }

    /**
     * Basic processing method that other process methods delegate to.
     * Default implementation returns the target unchanged.
     * Subclasses should override this method to implement specific processing logic.
     *
     * @param target the document data to process
     * @return the processed document data
     */
    protected Map<String, Object> process(final Map<String, Object> target) {
        return target;
    }

}
