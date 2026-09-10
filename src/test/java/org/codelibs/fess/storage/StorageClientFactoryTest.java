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

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * What core still owns is the type-to-name mapping: {@link StorageClientFactory#componentName}
 * and the endpoint detection behind it. The implementations moved to fess-storage-s3 and
 * fess-storage-gcs together with their SDKs, so the component definitions and the prototype
 * contract are verified in those plugins, not here.
 */
public class StorageClientFactoryTest extends UnitFessTestCase {

    @Test
    public void test_componentName_fromAnExplicitType() {
        assertEquals("s3StorageClient", StorageClientFactory.componentName("s3", null));
        assertEquals("gcsStorageClient", StorageClientFactory.componentName("gcs", null));
        assertEquals("gcsStorageClient", StorageClientFactory.componentName("GCS", null));
        assertEquals("s3_compatStorageClient", StorageClientFactory.componentName("s3_compat", null));
        assertEquals("s3StorageClient", StorageClientFactory.componentName("  s3  ", null));
    }

    /**
     * The point of resolving by name: a type nobody in core knows about still maps to a
     * component name, so a plugin registering it is reachable through storage.type alone.
     */
    @Test
    public void test_componentName_fromATypeCoreDoesNotKnow() {
        assertEquals("azureStorageClient", StorageClientFactory.componentName("azure", null));
    }

    @Test
    public void test_componentName_detectsFromTheEndpoint() {
        assertEquals("gcsStorageClient", StorageClientFactory.componentName(null, "https://storage.googleapis.com"));
        assertEquals("gcsStorageClient", StorageClientFactory.componentName("auto", "https://bucket.storage.cloud.google.com"));
        assertEquals("s3StorageClient", StorageClientFactory.componentName("auto", "https://s3.us-east-1.amazonaws.com"));
        assertEquals("s3StorageClient", StorageClientFactory.componentName("", null));
        // MinIO and friends: no vendor host, so the S3-compatible client.
        assertEquals("s3_compatStorageClient", StorageClientFactory.componentName("auto", "http://minio.internal:9000"));
    }

    /**
     * The distribution on its own serves no storage type: every backend ships as a
     * fess-storage-* plugin that contributes its component through fess_storage++.xml, so
     * fess_storage.xml declares nothing and storage.type is a configuration error until the
     * matching plugin is installed. This asserts the absence, because registering any of these
     * names in core again would pull an SDK back into the war - the AWS one for s3 and s3_compat,
     * the Google Cloud Storage one for gcs.
     *
     * <p>test_app.xml includes fess_storage.xml, so this runs against the file the distribution
     * ships rather than against an empty container.</p>
     */
    @Test
    public void test_noTypeIsServedByCore() {
        for (final StorageType type : StorageType.values()) {
            final String name = StorageClientFactory.componentName(type.name(), null);
            assertFalse(org.codelibs.fess.util.ComponentUtil.hasComponent(name),
                    name + " belongs to a fess-storage-* plugin and must not be declared in fess_storage.xml");
        }
        // auto with no endpoint resolves to s3, which is a plugin as well.
        assertFalse(org.codelibs.fess.util.ComponentUtil.hasComponent(StorageClientFactory.componentName("auto", null)));
    }

    /**
     * With the clients gone, the mapping from an endpoint to a type name is what core contributes
     * to reaching them, so it is fixed here as well as through {@link #componentName} above: a
     * type that came out wrong would resolve a component name no installed plugin registers.
     */
    @Test
    public void test_detectStorageType_mapsEndpointsToTypes() {
        // No endpoint at all means AWS, which supplies its own.
        assertEquals(StorageType.S3, StorageClientFactory.detectStorageType(null));
        assertEquals(StorageType.S3, StorageClientFactory.detectStorageType(""));
        assertEquals(StorageType.S3, StorageClientFactory.detectStorageType("   "));

        assertEquals(StorageType.GCS, StorageClientFactory.detectStorageType("https://storage.googleapis.com"));
        assertEquals(StorageType.GCS, StorageClientFactory.detectStorageType("https://STORAGE.GOOGLEAPIS.COM"));
        assertEquals(StorageType.GCS, StorageClientFactory.detectStorageType("https://bucket.storage.cloud.google.com"));

        assertEquals(StorageType.S3, StorageClientFactory.detectStorageType("https://s3.us-east-1.amazonaws.com"));
        assertEquals(StorageType.S3, StorageClientFactory.detectStorageType("https://bucket.s3.amazonaws.com"));
        assertEquals(StorageType.S3, StorageClientFactory.detectStorageType("https://s3-accelerate.amazonaws.com"));

        // No vendor host: MinIO and the like, served by the same client as s3 under another name.
        assertEquals(StorageType.S3_COMPAT, StorageClientFactory.detectStorageType("http://minio.internal:9000"));
        assertEquals(StorageType.S3_COMPAT, StorageClientFactory.detectStorageType("https://objects.example.com"));
    }
}
