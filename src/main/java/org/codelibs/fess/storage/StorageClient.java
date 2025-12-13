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
package org.codelibs.fess.storage;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Interface for cloud storage operations.
 * Implementations provide access to S3-compatible storage, GCS, or other cloud storage systems.
 */
public interface StorageClient extends AutoCloseable {

    /**
     * Uploads an object to storage.
     *
     * @param objectName the name/path for the object
     * @param inputStream the input stream of data to upload
     * @param size the size of the data in bytes
     * @param contentType the MIME type of the content
     */
    void uploadObject(String objectName, InputStream inputStream, long size, String contentType);

    /**
     * Downloads an object from storage.
     *
     * @param objectName the name/path of the object to download
     * @param outputStream the output stream to write data to
     */
    void downloadObject(String objectName, OutputStream outputStream);

    /**
     * Deletes an object from storage.
     *
     * @param objectName the name/path of the object to delete
     */
    void deleteObject(String objectName);

    /**
     * Lists objects in storage with the given prefix.
     *
     * @param prefix the path prefix to list objects under (null or empty for root)
     * @param maxItems maximum number of items to return
     * @return list of storage items
     */
    List<StorageItem> listObjects(String prefix, int maxItems);

    /**
     * Gets tags/metadata for an object.
     *
     * @param objectName the name/path of the object
     * @return map of tag key-value pairs
     */
    Map<String, String> getObjectTags(String objectName);

    /**
     * Sets tags/metadata for an object.
     *
     * @param objectName the name/path of the object
     * @param tags the tags to set
     */
    void setObjectTags(String objectName, Map<String, String> tags);

    /**
     * Ensures the bucket exists, creating it if necessary.
     */
    void ensureBucketExists();

    /**
     * Checks if storage is properly configured and accessible.
     *
     * @return true if storage is available
     */
    boolean isAvailable();

    /**
     * Closes the client and releases resources.
     */
    @Override
    void close();
}
