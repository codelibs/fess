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
package org.codelibs.fess.ingest;

import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;

public class IngestFactoryTest extends UnitFessTestCase {

    public void test_add_sortedOrder() {
        IngestFactory factory = new IngestFactory();
        factory.add(new TestIngester(1));
        factory.add(new TestIngester(2));
        factory.add(new TestIngester(3));
        Ingester[] ingesters = factory.getIngesters();
        assertEquals(3, ingesters.length);
        assertEquals(1, ingesters[0].getPriority());
        assertEquals(2, ingesters[1].getPriority());
        assertEquals(3, ingesters[2].getPriority());
    }

    public void test_add_reversedOrder() {
        IngestFactory factory = new IngestFactory();
        factory.add(new TestIngester(3));
        factory.add(new TestIngester(2));
        factory.add(new TestIngester(1));
        Ingester[] ingesters = factory.getIngesters();
        assertEquals(3, ingesters.length);
        assertEquals(1, ingesters[0].getPriority());
        assertEquals(2, ingesters[1].getPriority());
        assertEquals(3, ingesters[2].getPriority());
    }

    public void test_add_duplicateClass() {
        IngestFactory factory = new IngestFactory();
        factory.add(new TestIngester(1));
        factory.add(new TestIngester(2)); // Same class, should replace
        Ingester[] ingesters = factory.getIngesters();
        assertEquals(1, ingesters.length); // Only one ingester should remain
        assertEquals(2, ingesters[0].getPriority()); // Latest one should be kept
    }

    public void test_add_null() {
        IngestFactory factory = new IngestFactory();
        try {
            factory.add(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Ingester cannot be null", e.getMessage());
        }
    }

    public void test_remove() {
        IngestFactory factory = new IngestFactory();
        TestIngester ingester1 = new TestIngester(1);
        TestIngester ingester2 = new TestIngester(2);
        factory.add(ingester1);
        factory.add(ingester2);
        assertEquals(2, factory.size());

        boolean removed = factory.remove(ingester1);
        assertTrue(removed);
        assertEquals(1, factory.size());
        assertEquals(2, factory.getIngesters()[0].getPriority());
    }

    public void test_remove_null() {
        IngestFactory factory = new IngestFactory();
        boolean removed = factory.remove(null);
        assertFalse(removed);
    }

    public void test_remove_nonExistent() {
        IngestFactory factory = new IngestFactory();
        factory.add(new TestIngester(1));
        boolean removed = factory.remove(new TestIngester(2));
        assertFalse(removed);
        assertEquals(1, factory.size());
    }

    public void test_removeByClass() {
        IngestFactory factory = new IngestFactory();
        factory.add(new TestIngester(1));
        factory.add(new AnotherTestIngester(2));
        assertEquals(2, factory.size());

        boolean removed = factory.removeByClass(TestIngester.class);
        assertTrue(removed);
        assertEquals(1, factory.size());
        assertEquals(2, factory.getIngesters()[0].getPriority());
    }

    public void test_removeByClass_null() {
        IngestFactory factory = new IngestFactory();
        boolean removed = factory.removeByClass(null);
        assertFalse(removed);
    }

    public void test_clear() {
        IngestFactory factory = new IngestFactory();
        factory.add(new TestIngester(1));
        factory.add(new TestIngester(2));
        assertEquals(2, factory.size());

        factory.clear();
        assertEquals(0, factory.size());
        assertEquals(0, factory.getIngesters().length);
    }

    public void test_size() {
        IngestFactory factory = new IngestFactory();
        assertEquals(0, factory.size());

        factory.add(new TestIngester(1));
        assertEquals(1, factory.size());

        factory.add(new AnotherTestIngester(2));
        assertEquals(2, factory.size());

        factory.clear();
        assertEquals(0, factory.size());
    }

    public void test_getIngesters_defensiveCopy() {
        IngestFactory factory = new IngestFactory();
        factory.add(new TestIngester(1));

        Ingester[] ingesters1 = factory.getIngesters();
        Ingester[] ingesters2 = factory.getIngesters();

        // Should be different array instances (defensive copy)
        assertNotSame(ingesters1, ingesters2);
        // But contain same elements
        assertEquals(ingesters1.length, ingesters2.length);
        assertEquals(ingesters1[0].getPriority(), ingesters2[0].getPriority());
    }

    public void test_addMultiple_samePriority() {
        IngestFactory factory = new IngestFactory();
        factory.add(new TestIngester(1));
        factory.add(new AnotherTestIngester(1));
        factory.add(new ThirdTestIngester(1));

        Ingester[] ingesters = factory.getIngesters();
        assertEquals(3, ingesters.length);
        // All should have same priority
        assertEquals(1, ingesters[0].getPriority());
        assertEquals(1, ingesters[1].getPriority());
        assertEquals(1, ingesters[2].getPriority());
    }

    private static class TestIngester extends Ingester {
        public TestIngester(int priority) {
            this.priority = priority;
        }

        @Override
        protected Map<String, Object> process(Map<String, Object> target) {
            target.put("test", "value");
            return target;
        }
    }

    private static class AnotherTestIngester extends Ingester {
        public AnotherTestIngester(int priority) {
            this.priority = priority;
        }

        @Override
        protected Map<String, Object> process(Map<String, Object> target) {
            target.put("another", "value");
            return target;
        }
    }

    private static class ThirdTestIngester extends Ingester {
        public ThirdTestIngester(int priority) {
            this.priority = priority;
        }
    }
}
