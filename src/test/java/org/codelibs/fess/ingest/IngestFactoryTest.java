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
        // Use different class type to ensure it's truly non-existent
        boolean removed = factory.remove(new AnotherTestIngester(2));
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

    public void test_getIngesters_modificationDoesNotAffectFactory() {
        IngestFactory factory = new IngestFactory();
        factory.add(new TestIngester(1));
        factory.add(new AnotherTestIngester(2));

        Ingester[] ingesters = factory.getIngesters();
        assertEquals(2, ingesters.length);

        // Modify the returned array
        ingesters[0] = new ThirdTestIngester(999);

        // Factory should still have original ingesters
        Ingester[] factoryIngesters = factory.getIngesters();
        assertEquals(2, factoryIngesters.length);
        assertEquals(1, factoryIngesters[0].getPriority());
        assertEquals(TestIngester.class, factoryIngesters[0].getClass());
    }

    public void test_concurrentAdd() throws Exception {
        final IngestFactory factory = new IngestFactory();
        final int threadCount = 10;
        final Thread[] threads = new Thread[threadCount];

        // Create threads that add different ingesters concurrently
        for (int i = 0; i < threadCount; i++) {
            final int priority = i;
            threads[i] = new Thread(() -> {
                if (priority % 2 == 0) {
                    factory.add(new TestIngester(priority));
                } else {
                    factory.add(new AnotherTestIngester(priority));
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join(5000); // 5 second timeout
        }

        // Verify all ingesters were added (some may have been replaced due to same class)
        assertTrue(factory.size() > 0);
        assertTrue(factory.size() <= threadCount);

        // Verify sorting is maintained
        Ingester[] ingesters = factory.getIngesters();
        for (int i = 1; i < ingesters.length; i++) {
            assertTrue(ingesters[i - 1].getPriority() <= ingesters[i].getPriority());
        }
    }

    public void test_concurrentReadWrite() throws Exception {
        final IngestFactory factory = new IngestFactory();
        factory.add(new TestIngester(1));
        factory.add(new AnotherTestIngester(2));

        final Thread[] writers = new Thread[5];
        final Thread[] readers = new Thread[5];
        final boolean[] readersSucceeded = new boolean[5];

        // Create writer threads
        for (int i = 0; i < 5; i++) {
            final int priority = i + 10;
            writers[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    factory.add(new ThirdTestIngester(priority + j));
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        // Create reader threads
        for (int i = 0; i < 5; i++) {
            final int index = i;
            readers[i] = new Thread(() -> {
                for (int j = 0; j < 20; j++) {
                    Ingester[] ingesters = factory.getIngesters();
                    // Verify array is properly sorted
                    for (int k = 1; k < ingesters.length; k++) {
                        if (ingesters[k - 1].getPriority() > ingesters[k].getPriority()) {
                            readersSucceeded[index] = false;
                            return;
                        }
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                readersSucceeded[index] = true;
            });
        }

        // Start all threads
        for (Thread writer : writers) {
            writer.start();
        }
        for (Thread reader : readers) {
            reader.start();
        }

        // Wait for all threads to complete
        for (Thread writer : writers) {
            writer.join(10000);
        }
        for (Thread reader : readers) {
            reader.join(10000);
        }

        // Verify readers all succeeded
        for (int i = 0; i < 5; i++) {
            assertTrue("Reader " + i + " failed", readersSucceeded[i]);
        }
    }

    public void test_removeWhileIterating() {
        IngestFactory factory = new IngestFactory();
        factory.add(new TestIngester(1));
        factory.add(new AnotherTestIngester(2));
        factory.add(new ThirdTestIngester(3));

        Ingester[] ingesters = factory.getIngesters();
        assertEquals(3, ingesters.length);

        // Remove while we have a reference to the array
        factory.removeByClass(AnotherTestIngester.class);

        // Original array should still have 3 elements (defensive copy)
        assertEquals(3, ingesters.length);

        // But factory should have 2 elements
        assertEquals(2, factory.size());
        Ingester[] newIngesters = factory.getIngesters();
        assertEquals(2, newIngesters.length);
    }

    public void test_clearWhileHoldingReference() {
        IngestFactory factory = new IngestFactory();
        factory.add(new TestIngester(1));
        factory.add(new AnotherTestIngester(2));

        Ingester[] ingesters = factory.getIngesters();
        assertEquals(2, ingesters.length);

        // Clear factory
        factory.clear();

        // Original array should still have 2 elements (defensive copy)
        assertEquals(2, ingesters.length);
        assertEquals(0, factory.size());
    }

    public void test_addSameClassDifferentPriorities() {
        IngestFactory factory = new IngestFactory();
        factory.add(new TestIngester(10));
        assertEquals(1, factory.size());
        assertEquals(10, factory.getIngesters()[0].getPriority());

        // Add same class with different priority - should replace
        factory.add(new TestIngester(5));
        assertEquals(1, factory.size());
        assertEquals(5, factory.getIngesters()[0].getPriority());

        // Add again with even different priority - should replace again
        factory.add(new TestIngester(20));
        assertEquals(1, factory.size());
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

    public void test_removeByClassThenAdd() {
        IngestFactory factory = new IngestFactory();
        factory.add(new TestIngester(1));
        factory.add(new AnotherTestIngester(2));

        // Remove by class
        assertTrue(factory.removeByClass(TestIngester.class));
        assertEquals(1, factory.size());

        // Add same class back
        factory.add(new TestIngester(3));
        assertEquals(2, factory.size());

        Ingester[] ingesters = factory.getIngesters();
        assertEquals(AnotherTestIngester.class, ingesters[0].getClass());
        assertEquals(TestIngester.class, ingesters[1].getClass());
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

        // Should have 100 ingesters (no duplicates since each has different class or replaced)
        assertTrue(factory.size() <= 100);

        // Verify sorting
        Ingester[] ingesters = factory.getIngesters();
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
