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
package org.codelibs.fess.job;

import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.helper.ChunkVectorHelper;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * {@link ChunkVectorJob} is a thin shell: the whole processing run lives in
 * {@link ChunkVectorHelper#executeChunkVectorProcessing()} (covered by
 * {@code ChunkVectorHelperTest}), so this test only pins the delegation
 * contract the scheduler script relies on. DI registration is covered by
 * {@code ChunkVectorJobDiTest}.
 */
public class ChunkVectorJobTest extends UnitFessTestCase {

    @Test
    public void test_execute_delegatesToChunkVectorHelperAndReturnsItsSummary() {
        final AtomicInteger calls = new AtomicInteger();
        final ChunkVectorHelper fakeHelper = new ChunkVectorHelper() {
            @Override
            public String executeChunkVectorProcessing() {
                calls.incrementAndGet();
                return "summary-from-helper";
            }
        };
        final ChunkVectorJob job = new ChunkVectorJob() {
            @Override
            protected ChunkVectorHelper getChunkVectorHelper() {
                return fakeHelper;
            }
        };

        final String result = job.execute();

        assertEquals("execute() must return the helper's summary unchanged (the scheduler script surfaces it as the job result)",
                "summary-from-helper", result);
        assertEquals("execute() must delegate to the helper exactly once", 1, calls.get());
    }
}
