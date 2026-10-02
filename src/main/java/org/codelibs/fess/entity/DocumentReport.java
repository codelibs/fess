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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The data of one tab of the document report: the duplicate groups or a page of dormant documents.
 * A document is a map with the keys {@code DOC_*} of {@link org.codelibs.fess.app.service.DocumentReportService}.
 */
public class DocumentReport {

    /**
     * A group of documents whose content has the same signature.
     */
    public static class DuplicateGroup {
        private final String hash;

        private final long count;

        private final List<Map<String, Object>> docs;

        /**
         * Creates a duplicate group.
         *
         * @param hash the content signature the documents share
         * @param count the number of documents in the group
         * @param docs the listed documents, at most the configured number
         */
        public DuplicateGroup(final String hash, final long count, final List<Map<String, Object>> docs) {
            this.hash = hash;
            this.count = count;
            this.docs = docs;
        }

        /**
         * Returns the content signature the documents share.
         *
         * @return the signature
         */
        public String getHash() {
            return hash;
        }

        /**
         * Returns the number of documents in the group.
         *
         * @return the number of documents
         */
        public long getCount() {
            return count;
        }

        /**
         * Returns the listed documents.
         *
         * @return the documents
         */
        public List<Map<String, Object>> getDocs() {
            return docs;
        }
    }

    private final List<DuplicateGroup> groups = new ArrayList<>();

    private final List<Map<String, Object>> docs = new ArrayList<>();

    private long total;

    private long totalSize;

    private boolean failed;

    private boolean unavailable;

    /**
     * Creates an empty report.
     */
    public DocumentReport() {
    }

    /**
     * Returns the duplicate groups, largest first.
     *
     * @return the groups
     */
    public List<DuplicateGroup> getGroups() {
        return groups;
    }

    /**
     * Returns the dormant documents of the requested page.
     *
     * @return the documents
     */
    public List<Map<String, Object>> getDocs() {
        return docs;
    }

    /**
     * Returns the number of dormant documents.
     *
     * @return the number of documents
     */
    public long getTotal() {
        return total;
    }

    /**
     * Sets the number of dormant documents.
     *
     * @param total the number of documents
     */
    public void setTotal(final long total) {
        this.total = total;
    }

    /**
     * Returns the sum of the content length of the dormant documents.
     *
     * @return the size in bytes
     */
    public long getTotalSize() {
        return totalSize;
    }

    /**
     * Sets the sum of the content length of the dormant documents.
     *
     * @param totalSize the size in bytes
     */
    public void setTotalSize(final long totalSize) {
        this.totalSize = totalSize;
    }

    /**
     * Returns whether the search engine failed to answer.
     *
     * @return true when the report could not be built
     */
    public boolean isFailed() {
        return failed;
    }

    /**
     * Sets whether the search engine failed to answer.
     *
     * @param failed true when the report could not be built
     */
    public void setFailed(final boolean failed) {
        this.failed = failed;
    }

    /**
     * Returns whether the report cannot be built with the index of this search engine type.
     *
     * @return true when the index has no content signature
     */
    public boolean isUnavailable() {
        return unavailable;
    }

    /**
     * Sets whether the report cannot be built with the index of this search engine type.
     *
     * @param unavailable true when the index has no content signature
     */
    public void setUnavailable(final boolean unavailable) {
        this.unavailable = unavailable;
    }
}
