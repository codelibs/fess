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

import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Factory for creating appropriate StorageClient based on configuration.
 */
public final class StorageClientFactory {

    private static final Logger logger = LogManager.getLogger(StorageClientFactory.class);

    private StorageClientFactory() {
        // Utility class
    }

    /**
     * Auto-detect storage type from endpoint URL.
     *
     * @param endpoint the storage endpoint URL
     * @return detected storage type
     */
    public static StorageType detectStorageType(final String endpoint) {
        if (StringUtil.isBlank(endpoint)) {
            // Default to S3 if no endpoint (uses AWS default)
            return StorageType.S3;
        }

        final String lowerEndpoint = endpoint.toLowerCase(Locale.ROOT);

        // GCS patterns
        if (lowerEndpoint.contains("storage.googleapis.com") || lowerEndpoint.contains(".storage.cloud.google.com")) {
            return StorageType.GCS;
        }

        // S3 patterns
        if (lowerEndpoint.contains(".amazonaws.com") || lowerEndpoint.matches(".*s3[.-].*")) {
            return StorageType.S3;
        }

        // Default to S3-compatible (MinIO, etc.)
        return StorageType.S3_COMPAT;
    }

    /**
     * Creates a StorageClient based on FessConfig.
     *
     * @param fessConfig the Fess configuration
     * @return configured StorageClient
     */
    public static StorageClient createClient(final FessConfig fessConfig) {
        final String endpoint = fessConfig.getStorageEndpoint();
        final String accessKey = fessConfig.getStorageAccessKey();
        final String secretKey = fessConfig.getStorageSecretKey();
        final String bucket = fessConfig.getStorageBucket();

        // Get explicit type or auto-detect
        final String typeStr = fessConfig.getStorageType();
        final StorageType type;
        if (StringUtil.isBlank(typeStr) || "auto".equalsIgnoreCase(typeStr)) {
            type = detectStorageType(endpoint);
            if (logger.isDebugEnabled()) {
                logger.debug("Auto-detected storage type: {} for endpoint: {}", type, endpoint);
            }
        } else {
            type = parseStorageType(typeStr);
        }

        switch (type) {
        case GCS:
            return new GcsStorageClient(fessConfig.getStorageProjectId(), bucket, endpoint, fessConfig.getStorageCredentialsPath());
        case S3:
        case S3_COMPAT:
        default:
            return new S3StorageClient(endpoint, accessKey, secretKey, bucket, fessConfig.getStorageRegion());
        }
    }

    /**
     * Creates a StorageClient using the default FessConfig.
     *
     * @return configured StorageClient
     */
    public static StorageClient createClient() {
        return createClient(ComponentUtil.getFessConfig());
    }

    /**
     * Parses a storage type string to StorageType enum.
     *
     * @param typeStr the type string (s3, gcs, s3_compat, auto)
     * @return parsed StorageType
     */
    private static StorageType parseStorageType(final String typeStr) {
        final String upper = typeStr.toUpperCase(Locale.ROOT);
        try {
            return StorageType.valueOf(upper);
        } catch (final IllegalArgumentException e) {
            logger.warn("Unknown storage type: {}, defaulting to S3_COMPAT", typeStr);
            return StorageType.S3_COMPAT;
        }
    }
}
