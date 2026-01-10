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

public class StorageTypeTest extends UnitFessTestCase {

    public void test_enumValues() {
        final StorageType[] types = StorageType.values();
        assertNotNull(types);
        assertEquals(3, types.length);
    }

    public void test_s3Type() {
        assertEquals(StorageType.S3, StorageType.valueOf("S3"));
    }

    public void test_gcsType() {
        assertEquals(StorageType.GCS, StorageType.valueOf("GCS"));
    }

    public void test_s3CompatType() {
        assertEquals(StorageType.S3_COMPAT, StorageType.valueOf("S3_COMPAT"));
    }

    public void test_valueOf() {
        for (final StorageType type : StorageType.values()) {
            assertEquals(type, StorageType.valueOf(type.name()));
        }
    }

    public void test_ordinal() {
        assertEquals(0, StorageType.S3.ordinal());
        assertEquals(1, StorageType.GCS.ordinal());
        assertEquals(2, StorageType.S3_COMPAT.ordinal());
    }

    public void test_name() {
        assertEquals("S3", StorageType.S3.name());
        assertEquals("GCS", StorageType.GCS.name());
        assertEquals("S3_COMPAT", StorageType.S3_COMPAT.name());
    }
}
