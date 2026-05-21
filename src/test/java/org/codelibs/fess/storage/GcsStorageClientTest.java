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

public class GcsStorageClientTest extends UnitFessTestCase {

    private static final class TestableGcsStorageClient extends GcsStorageClient {
        TestableGcsStorageClient() {
            super();
        }
    }

    @Test
    public void test_isDefaultEndpoint() {
        try (GcsStorageClient client = new TestableGcsStorageClient()) {
            assertTrue(client.isDefaultEndpoint("https://storage.googleapis.com"));
            assertTrue(client.isDefaultEndpoint("https://storage.googleapis.com/"));
            assertTrue(client.isDefaultEndpoint("storage.googleapis.com"));
            assertTrue(client.isDefaultEndpoint("https://storage.googleapis.com/storage/v1"));
            assertTrue(client.isDefaultEndpoint("https://storage.googleapis.com:443"));

            assertFalse(client.isDefaultEndpoint(""));
            assertFalse(client.isDefaultEndpoint("http://storage.googleapis.com"));
            assertFalse(client.isDefaultEndpoint("https://storage.googleapis.com:4443"));
            assertFalse(client.isDefaultEndpoint("http://localhost:4443"));
            assertFalse(client.isDefaultEndpoint("https://example.com/storage.googleapis.com"));
        }
    }
}
