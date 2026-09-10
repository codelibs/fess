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
import org.codelibs.fess.exception.StorageException;
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
        if (lowerEndpoint.contains(".amazonaws.com") || lowerEndpoint.contains("s3.") || lowerEndpoint.contains("s3-")) {
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
        final String componentName = componentName(fessConfig.getStorageType(), fessConfig.getStorageEndpoint());
        if (!ComponentUtil.hasComponent(componentName)) {
            throw new StorageException("No storage client is registered as " + componentName + " for storage.type="
                    + fessConfig.getStorageType() + ". Install the plugin that provides it, such as fess-storage-gcs for gcs.");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Creating {} for endpoint: {}", componentName, fessConfig.getStorageEndpoint());
        }
        final StorageClient client = ComponentUtil.getComponent(componentName);
        client.init();
        return client;
    }

    /**
     * Returns the name of the DI component that serves a storage type.
     *
     * <p>The mapping from a {@code storage.type} value to an implementation lives in the DI
     * definition rather than here, which is what lets the clients ship as plugins: core no longer
     * names GcsStorageClient or S3StorageClient, and a plugin registering
     * {@code <type>StorageClient} is reachable by setting {@code storage.type=<type>}. The
     * components are prototypes because every caller closes the client it was handed.</p>
     *
     * @param typeStr the configured type, blank or {@code auto} to detect from the endpoint
     * @param endpoint the storage endpoint, used only when detecting
     * @return the component name
     */
    static String componentName(final String typeStr, final String endpoint) {
        final String type;
        if (StringUtil.isBlank(typeStr) || "auto".equalsIgnoreCase(typeStr)) {
            type = detectStorageType(endpoint).name().toLowerCase(Locale.ROOT);
        } else {
            type = typeStr.trim().toLowerCase(Locale.ROOT);
        }
        return type + "StorageClient";
    }

    /**
     * Creates a StorageClient using the default FessConfig.
     *
     * @return configured StorageClient
     */
    public static StorageClient createClient() {
        return createClient(ComponentUtil.getFessConfig());
    }

}
