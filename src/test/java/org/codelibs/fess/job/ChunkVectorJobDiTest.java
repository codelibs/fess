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
import org.junit.jupiter.api.Test;
import org.lastaflute.di.core.ComponentDef;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.factory.LaContainerFactory;

/**
 * Verifies the shipped {@code fess_job.xml} really declares {@code chunkVectorJob}: the scheduled
 * job "Content Chunk Vector Indexer" resolves it by name via
 * {@code container.getComponent("chunkVectorJob")}, so a missing declaration breaks the job at
 * runtime only.
 *
 * <p>The test builds a container from the production resource instead of asserting through
 * {@code ComponentUtil}: {@code UnitFessTestCase} boots from {@code test_app.xml}, which never
 * includes {@code fess_job.xml}, so a container-level lookup would only prove that the test
 * fixture declares the component.</p>
 */
public class ChunkVectorJobDiTest extends UnitFessTestCase {

    private static final String JOB_XML = "fess_job.xml";

    private static final String COMPONENT_NAME = "chunkVectorJob";

    @Test
    public void test_chunkVectorJobDeclaredInFessJobXml() {
        final LaContainer container = LaContainerFactory.create(JOB_XML);
        assertTrue(container.hasComponentDef(COMPONENT_NAME), COMPONENT_NAME + " must be declared in " + JOB_XML);
        final ComponentDef componentDef = container.getComponentDef(COMPONENT_NAME);
        assertEquals(ChunkVectorJob.class, componentDef.getComponentClass());
    }

    @Test
    public void test_chunkVectorJobIsPrototypeScopedInFessJobXml() {
        final LaContainer container = LaContainerFactory.create(JOB_XML);
        assertEquals(COMPONENT_NAME + " must be instance=\"prototype\" in " + JOB_XML, "prototype",
                container.getComponentDef(COMPONENT_NAME).getInstanceDef().getName());
        // a job instance carries per-run state (sessionId, jvmOptions, ...), so every lookup by
        // the scheduler has to hand back a fresh instance
        final Object first = container.getComponent(COMPONENT_NAME);
        final Object second = container.getComponent(COMPONENT_NAME);
        assertTrue(first instanceof ChunkVectorJob, "resolved component should be a ChunkVectorJob instance");
        assertTrue(first != second, "instance=\"prototype\" should yield a fresh instance per lookup");
    }
}
