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

/**
 * Test class for IngestFactory.
 * Tests basic functionality of ingester registration and sorting.
 */
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

    public void test_addSameClassDifferentPriorities() {
        IngestFactory factory = new IngestFactory();
        factory.add(new TestIngester(10));
        assertEquals(1, factory.getIngesters().length);
        assertEquals(10, factory.getIngesters()[0].getPriority());

        // Add same class with different priority - should replace
        factory.add(new TestIngester(5));
        assertEquals(1, factory.getIngesters().length);
        assertEquals(5, factory.getIngesters()[0].getPriority());

        // Add again with even different priority - should replace again
        factory.add(new TestIngester(20));
        assertEquals(1, factory.getIngesters().length);
        assertEquals(20, factory.getIngesters()[0].getPriority());
    }

    public void test_multipleDifferentClassesSamePriorityStability() {
        IngestFactory factory = new IngestFactory();

        // Add multiple ingesters with same priority
        TestIngester t1 = new TestIngester(5);
        AnotherTestIngester t2 = new AnotherTestIngester(5);
        ThirdTestIngester t3 = new ThirdTestIngester(5);

        factory.add(t1);
        factory.add(t2);
        factory.add(t3);

        Ingester[] ingesters = factory.getIngesters();
        assertEquals(3, ingesters.length);

        // All should have same priority
        assertEquals(5, ingesters[0].getPriority());
        assertEquals(5, ingesters[1].getPriority());
        assertEquals(5, ingesters[2].getPriority());
    }

    public void test_largeNumberOfIngesters() {
        IngestFactory factory = new IngestFactory();

        // Add 100 ingesters with different priorities
        for (int i = 0; i < 100; i++) {
            if (i % 3 == 0) {
                factory.add(new TestIngester(i));
            } else if (i % 3 == 1) {
                factory.add(new AnotherTestIngester(i));
            } else {
                factory.add(new ThirdTestIngester(i));
            }
        }

        // Verify sorting and that ingesters were added
        Ingester[] ingesters = factory.getIngesters();
        assertTrue(ingesters.length > 0);
        assertTrue(ingesters.length <= 100);

        for (int i = 1; i < ingesters.length; i++) {
            assertTrue("Ingesters not sorted at index " + i, ingesters[i - 1].getPriority() <= ingesters[i].getPriority());
        }
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
