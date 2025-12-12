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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.exception.StorageException;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.StorageOptions;

/**
 * Google Cloud Storage client implementation.
 */
public class GcsStorageClient implements StorageClient {

    private static final Logger logger = LogManager.getLogger(GcsStorageClient.class);

    private final Storage storage;
    private final String bucket;

    /**
     * Creates a new GcsStorageClient instance.
     *
     * @param projectId the GCS project ID
     * @param bucket the bucket name
     * @param credentialsPath the path to the credentials JSON file (optional)
     */
    public GcsStorageClient(final String projectId, final String bucket, final String credentialsPath) {
        this.bucket = bucket;

        final StorageOptions.Builder builder = StorageOptions.newBuilder();

        if (StringUtil.isNotBlank(projectId)) {
            builder.setProjectId(projectId);
        }

        if (StringUtil.isNotBlank(credentialsPath)) {
            try (FileInputStream fis = new FileInputStream(credentialsPath)) {
                final GoogleCredentials credentials = GoogleCredentials.fromStream(fis);
                builder.setCredentials(credentials);
            } catch (final IOException e) {
                throw new StorageException("Failed to load GCS credentials from " + credentialsPath, e);
            }
        }
        // If no credentials path, uses default credentials (GOOGLE_APPLICATION_CREDENTIALS env var)

        this.storage = builder.build().getService();
    }

    @Override
    public void uploadObject(final String objectName, final InputStream inputStream, final long size, final String contentType) {
        try {
            final BlobId blobId = BlobId.of(bucket, objectName);
            final BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
            storage.createFrom(blobInfo, inputStream);
        } catch (final Exception e) {
            throw new StorageException("Failed to upload " + objectName, e);
        }
    }

    @Override
    public void downloadObject(final String objectName, final OutputStream outputStream) {
        try {
            final Blob blob = storage.get(BlobId.of(bucket, objectName));
            if (blob == null) {
                throw new StorageException("Object not found: " + objectName);
            }
            blob.downloadTo(outputStream);
        } catch (final StorageException e) {
            throw e;
        } catch (final Exception e) {
            throw new StorageException("Failed to download " + objectName, e);
        }
    }

    @Override
    public void deleteObject(final String objectName) {
        try {
            final boolean deleted = storage.delete(BlobId.of(bucket, objectName));
            if (!deleted && logger.isDebugEnabled()) {
                logger.debug("Object may not exist: {}", objectName);
            }
        } catch (final Exception e) {
            throw new StorageException("Failed to delete " + objectName, e);
        }
    }

    @Override
    public List<StorageItem> listObjects(final String prefix, final int maxItems) {
        final List<StorageItem> items = new ArrayList<>();
        final List<StorageItem> fileItems = new ArrayList<>();

        try {
            final String searchPrefix = StringUtil.isNotBlank(prefix) ? (prefix.endsWith("/") ? prefix : prefix + "/") : "";

            final Page<Blob> blobs = storage.list(bucket, BlobListOption.prefix(searchPrefix), BlobListOption.currentDirectory(),
                    BlobListOption.pageSize(maxItems));

            for (final Blob blob : blobs.iterateAll()) {
                final String blobName = blob.getName();

                // Skip the prefix itself
                if (blobName.equals(searchPrefix)) {
                    continue;
                }

                final boolean isDirectory = blobName.endsWith("/");
                final String name = getName(blobName);

                if (StringUtil.isBlank(name)) {
                    continue;
                }

                final ZonedDateTime lastModified =
                        blob.getUpdateTimeOffsetDateTime() != null ? blob.getUpdateTimeOffsetDateTime().toZonedDateTime() : null;

                final StorageItem item = new StorageItem(name, prefix, isDirectory, isDirectory ? 0 : blob.getSize(),
                        isDirectory ? null : lastModified, encodeId(blobName));

                if (isDirectory) {
                    items.add(item);
                } else {
                    fileItems.add(item);
                }

                if (items.size() + fileItems.size() >= maxItems) {
                    break;
                }
            }
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to list objects in {}", bucket, e);
            }
        }

        items.addAll(fileItems);
        return items;
    }

    @Override
    public Map<String, String> getObjectTags(final String objectName) {
        try {
            final Blob blob = storage.get(BlobId.of(bucket, objectName));
            if (blob == null) {
                return Collections.emptyMap();
            }
            // GCS uses metadata instead of tags
            final Map<String, String> metadata = blob.getMetadata();
            return metadata != null ? new HashMap<>(metadata) : Collections.emptyMap();
        } catch (final Exception e) {
            throw new StorageException("Failed to get tags from " + objectName, e);
        }
    }

    @Override
    public void setObjectTags(final String objectName, final Map<String, String> tags) {
        try {
            final Blob blob = storage.get(BlobId.of(bucket, objectName));
            if (blob != null) {
                // GCS uses metadata instead of tags
                blob.toBuilder().setMetadata(tags).build().update();
            } else {
                throw new StorageException("Object not found: " + objectName);
            }
        } catch (final StorageException e) {
            throw e;
        } catch (final Exception e) {
            throw new StorageException("Failed to update tags for " + objectName, e);
        }
    }

    @Override
    public void ensureBucketExists() {
        try {
            final Bucket existingBucket = storage.get(bucket);
            if (existingBucket == null) {
                storage.create(BucketInfo.newBuilder(bucket).build());
                logger.info("Created storage bucket: {}", bucket);
            }
        } catch (final Exception e) {
            logger.warn("Failed to ensure bucket exists: {}", bucket, e);
        }
    }

    @Override
    public boolean isAvailable() {
        try {
            return storage.get(bucket) != null;
        } catch (final Exception e) {
            return false;
        }
    }

    @Override
    public void close() {
        // GCS Storage client doesn't require explicit close
        // but we can try to close it if needed
        try {
            storage.close();
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to close GCS storage client", e);
            }
        }
    }

    /**
     * Extracts the file/directory name from a full object path.
     *
     * @param objectName the full object path
     * @return the name portion of the path
     */
    private String getName(final String objectName) {
        if (StringUtil.isBlank(objectName)) {
            return StringUtil.EMPTY;
        }
        // Remove trailing slash if present
        String name = objectName;
        if (name.endsWith("/")) {
            name = name.substring(0, name.length() - 1);
        }
        final String[] values = name.split("/");
        if (values.length == 0) {
            return StringUtil.EMPTY;
        }
        return values[values.length - 1];
    }

    /**
     * Encodes an object name to a URL-safe base64 string.
     *
     * @param objectName the object name to encode
     * @return base64 encoded string
     */
    private String encodeId(final String objectName) {
        if (objectName == null) {
            return StringUtil.EMPTY;
        }
        return new String(Base64.getUrlEncoder().encode(objectName.getBytes(Constants.UTF_8_CHARSET)), Constants.UTF_8_CHARSET);
    }
}
