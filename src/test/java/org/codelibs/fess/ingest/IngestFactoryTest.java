/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.ingest;

import org.codelibs.fess.unit.UnitFessTestCase;

public class IngestFactoryTest extends UnitFessTestCase {

    public void test_add_1() {
        IngestFactory factory = new IngestFactory();
        factory.add(new TestIngester(1));
        factory.add(new TestIngester(2));
        factory.add(new TestIngester(3));
        Ingester[] ingesters = factory.getIngesters();
        assertEquals(1, ingesters[0].getPriority());
        assertEquals(2, ingesters[1].getPriority());
        assertEquals(3, ingesters[2].getPriority());
    }

    public void test_add_2() {
        IngestFactory factory = new IngestFactory();
        factory.add(new TestIngester(3));
        factory.add(new TestIngester(2));
        factory.add(new TestIngester(1));
        Ingester[] ingesters = factory.getIngesters();
        assertEquals(1, ingesters[0].getPriority());
        assertEquals(2, ingesters[1].getPriority());
        assertEquals(3, ingesters[2].getPriority());
    }

    private static class TestIngester extends Ingester {
        public TestIngester(int priority) {
            this.priority = priority;
        }
    }
}
