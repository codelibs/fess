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

import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;

public class ChunkVectorJobDiTest extends UnitFessTestCase {

    @Test
    public void test_chunkVectorJobRegisteredInFessJobXml() {
        assertTrue(ComponentUtil.hasComponent("chunkVectorJob"), "chunkVectorJob component should be registered directly in fess_job.xml");
        final Object job = ComponentUtil.getComponent("chunkVectorJob");
        assertTrue(job instanceof ChunkVectorJob, "resolved component should be a ChunkVectorJob instance");
    }

    @Test
    public void test_chunkVectorJobIsPrototypeScoped() {
        final ChunkVectorJob first = ComponentUtil.getComponent("chunkVectorJob");
        final ChunkVectorJob second = ComponentUtil.getComponent("chunkVectorJob");
        assertTrue(first != second, "instance=\"prototype\" should yield a fresh instance per lookup");
    }
}
