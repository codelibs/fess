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
package org.codelibs.fess.ds.callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.codelibs.fess.entity.DataStoreParams;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;

public class CountingIndexUpdateCallbackTest extends PlainTestCase {

    private final List<String> calls = new ArrayList<>();

    private boolean storeFails;

    private final IndexUpdateCallback delegate = new IndexUpdateCallback() {
        @Override
        public void store(final DataStoreParams paramMap, final Map<String, Object> dataMap) {
            if (storeFails) {
                throw new IllegalStateException("rejected");
            }
            calls.add("store");
        }

        @Override
        public long getDocumentSize() {
            return 42L;
        }

        @Override
        public long getExecuteTime() {
            return 99L;
        }

        @Override
        public void commit() {
            calls.add("commit");
        }
    };

    @Test
    public void test_store_countsEachDocumentAfterTheDelegateTookIt() {
        final AtomicLong counter = new AtomicLong();
        final IndexUpdateCallback callback = new CountingIndexUpdateCallback(delegate, counter);

        callback.store(new DataStoreParams(), new HashMap<>());
        callback.store(new DataStoreParams(), new HashMap<>());

        assertEquals(2L, counter.get());
        assertEquals(List.of("store", "store"), calls);
    }

    @Test
    public void test_store_rejectedDocumentIsNotCounted() {
        final AtomicLong counter = new AtomicLong();
        final IndexUpdateCallback callback = new CountingIndexUpdateCallback(delegate, counter);
        storeFails = true;

        try {
            callback.store(new DataStoreParams(), new HashMap<>());
            fail();
        } catch (final IllegalStateException e) {
            // expected
        }

        assertEquals(0L, counter.get());
    }

    @Test
    public void test_otherMethodsAreTheDelegates() {
        final IndexUpdateCallback callback = new CountingIndexUpdateCallback(delegate, new AtomicLong());

        callback.commit();

        assertEquals(List.of("commit"), calls);
        assertEquals(42L, callback.getDocumentSize());
        assertEquals(99L, callback.getExecuteTime());
    }
}
