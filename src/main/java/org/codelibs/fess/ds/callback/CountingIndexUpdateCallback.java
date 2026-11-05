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

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.codelibs.fess.entity.DataStoreParams;

/**
 * An {@link IndexUpdateCallback} that counts the documents a data store hands to the callback it wraps.
 * The count is the live progress of the crawl of one data configuration, which the crawl loop reads
 * without a query to the search engine. A document is counted after the wrapped callback has taken it,
 * so a document that is rejected is not counted.
 */
public class CountingIndexUpdateCallback implements IndexUpdateCallback {

    private final IndexUpdateCallback delegate;

    private final AtomicLong counter;

    /**
     * Creates a callback that counts the documents stored through the wrapped callback.
     *
     * @param delegate the callback that stores the documents
     * @param counter the counter to increment for each stored document
     */
    public CountingIndexUpdateCallback(final IndexUpdateCallback delegate, final AtomicLong counter) {
        this.delegate = delegate;
        this.counter = counter;
    }

    @Override
    public void store(final DataStoreParams paramMap, final Map<String, Object> dataMap) {
        delegate.store(paramMap, dataMap);
        counter.incrementAndGet();
    }

    @Override
    public long getDocumentSize() {
        return delegate.getDocumentSize();
    }

    @Override
    public long getExecuteTime() {
        return delegate.getExecuteTime();
    }

    @Override
    public void commit() {
        delegate.commit();
    }
}
