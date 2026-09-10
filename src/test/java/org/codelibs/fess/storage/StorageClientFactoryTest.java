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
     * Every name {@link #test_componentName_detectsFromTheEndpoint} and the storage.type values
     * the distribution serves can produce has to exist in fess_storage.xml, or the admin storage
     * screen fails at runtime for a configuration the UI offers.
     */
    @Test
    public void test_everyTypeTheDistributionServesHasAComponent() {
        for (final StorageType type : new StorageType[] { StorageType.S3, StorageType.S3_COMPAT }) {
            final String name = StorageClientFactory.componentName(type.name(), null);
            assertTrue(org.codelibs.fess.util.ComponentUtil.hasComponent(name), name + " is not registered in fess_storage.xml");
        }
        assertTrue(org.codelibs.fess.util.ComponentUtil.hasComponent(StorageClientFactory.componentName("auto", null)));
    }

    /**
     * GCS is deliberately not in that list. Its client ships in fess-storage-gcs, together with the
     * Google Cloud Storage SDK the distribution no longer carries, so what core keeps is the name
     * the plugin registers under and the endpoint detection that produces it. Registering a
     * component here again would pull the SDK back into the war.
     */
    @Test
    public void test_gcsIsServedByAPluginRatherThanCore() {
        assertEquals("gcsStorageClient", StorageClientFactory.componentName(StorageType.GCS.name(), null));
        assertFalse(org.codelibs.fess.util.ComponentUtil.hasComponent("gcsStorageClient"),
                "gcsStorageClient belongs to fess-storage-gcs, which contributes it through fess_storage++.xml");
    }

    /**
     * A prototype, not a singleton: every caller closes the client it was handed, so a shared
     * instance would be shut down for everybody by the first caller to finish.
     */
    @Test
    public void test_theComponentsArePrototypes() {
        final Object first = org.codelibs.fess.util.ComponentUtil.getComponent("s3StorageClient");
        final Object second = org.codelibs.fess.util.ComponentUtil.getComponent("s3StorageClient");
        assertNotNull(first);
        assertNotNull(second);
        assertFalse(first == second, "s3StorageClient must be instance=\"prototype\"");
    }
}
