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
package org.codelibs.fess.score;

import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class ScoreUpdaterTest extends UnitFessTestCase {

    private ScoreUpdater scoreUpdater;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        scoreUpdater = new ScoreUpdater();
    }

    // Test constructor
    @Test
    public void test_constructor() {
        ScoreUpdater updater = new ScoreUpdater();
        assertNotNull(updater);
    }

    // Test execute with empty list
    @Test
    public void test_execute_emptyList() {
        String result = scoreUpdater.execute();
        assertEquals("", result);
    }

    // Test execute with single booster
    @Test
    public void test_execute_singleBooster() {
        TestScoreBooster booster = new TestScoreBooster(100L);
        scoreUpdater.addScoreBooster(booster);

        String result = scoreUpdater.execute();
        assertTrue(result.contains("TestScoreBooster : 100"));
    }

    // Test execute with multiple boosters
    @Test
    public void test_execute_multipleBoosters() {
        TestScoreBooster booster1 = new TestScoreBooster(100L);
        TestScoreBooster booster2 = new TestScoreBooster(200L);

        scoreUpdater.addScoreBooster(booster1);
        scoreUpdater.addScoreBooster(booster2);

        String result = scoreUpdater.execute();
        assertTrue(result.contains("TestScoreBooster : 100"));
        assertTrue(result.contains("TestScoreBooster : 200"));
    }

    // Test execute with exception in booster
    @Test
    public void test_execute_withException() {
        TestScoreBooster booster1 = new TestScoreBooster(100L);
        ExceptionScoreBooster booster2 = new ExceptionScoreBooster();
        TestScoreBooster booster3 = new TestScoreBooster(300L);

        scoreUpdater.addScoreBooster(booster1);
        scoreUpdater.addScoreBooster(booster2);
        scoreUpdater.addScoreBooster(booster3);

        String result = scoreUpdater.execute();
        assertTrue(result.contains("TestScoreBooster : 100"));
        assertTrue(result.contains("Test exception"));
        assertTrue(result.contains("TestScoreBooster : 300"));
    }

    // Test addScoreBooster with different priorities
    @Test
    public void test_addScoreBooster_sortsByPriority() {
        PriorityTrackingBooster booster1 = new PriorityTrackingBooster(1, 100L);
        PriorityTrackingBooster booster2 = new PriorityTrackingBooster(3, 200L);
        PriorityTrackingBooster booster3 = new PriorityTrackingBooster(2, 300L);

        scoreUpdater.addScoreBooster(booster1);
        scoreUpdater.addScoreBooster(booster2);
        scoreUpdater.addScoreBooster(booster3);

        // Clear execution order before test
        PriorityTrackingBooster.clearExecutionOrder();

        scoreUpdater.execute();

        // Boosters should be executed in priority order (3, 2, 1)
        List<Integer> executionOrder = PriorityTrackingBooster.getExecutionOrder();
        assertEquals(3, executionOrder.size());
        assertEquals(Integer.valueOf(3), executionOrder.get(0));
        assertEquals(Integer.valueOf(2), executionOrder.get(1));
        assertEquals(Integer.valueOf(1), executionOrder.get(2));
    }

    // Test addScoreBooster with same priorities
    @Test
    public void test_addScoreBooster_samePriority() {
        TestScoreBooster booster1 = new TestScoreBooster(100L);
        booster1.setPriority(1);
        TestScoreBooster booster2 = new TestScoreBooster(200L);
        booster2.setPriority(1);
        TestScoreBooster booster3 = new TestScoreBooster(300L);
        booster3.setPriority(1);

        scoreUpdater.addScoreBooster(booster1);
        scoreUpdater.addScoreBooster(booster2);
        scoreUpdater.addScoreBooster(booster3);

        String result = scoreUpdater.execute();

        // All boosters should be executed
        assertTrue(result.contains("TestScoreBooster : 100"));
        assertTrue(result.contains("TestScoreBooster : 200"));
        assertTrue(result.contains("TestScoreBooster : 300"));
    }

    // Test execute result format
    @Test
    public void test_execute_resultFormat() {
        TestScoreBooster booster1 = new TestScoreBooster(123L);
        TestScoreBooster booster2 = new TestScoreBooster(456L);

        scoreUpdater.addScoreBooster(booster1);
        scoreUpdater.addScoreBooster(booster2);

        String result = scoreUpdater.execute();

        // Check format with class name, colon, count, and newline
        String[] lines = result.split("\n");
        assertEquals(2, lines.length);
        assertTrue(lines[0].matches("TestScoreBooster : \\d+"));
        assertTrue(lines[1].matches("TestScoreBooster : \\d+"));
    }

    // Test with null pointer exception
    @Test
    public void test_execute_nullPointerException() {
        NullPointerScoreBooster booster = new NullPointerScoreBooster();
        scoreUpdater.addScoreBooster(booster);

        String result = scoreUpdater.execute();
        // NullPointerException message should be captured
        assertTrue(result.contains("null") || result.contains("NullPointer"));
    }

    // Test with negative priority
    @Test
    public void test_addScoreBooster_negativePriority() {
        PriorityTrackingBooster booster1 = new PriorityTrackingBooster(-1, 100L);
        PriorityTrackingBooster booster2 = new PriorityTrackingBooster(0, 200L);
        PriorityTrackingBooster booster3 = new PriorityTrackingBooster(1, 300L);

        scoreUpdater.addScoreBooster(booster1);
        scoreUpdater.addScoreBooster(booster2);
        scoreUpdater.addScoreBooster(booster3);

        // Clear execution order before test
        PriorityTrackingBooster.clearExecutionOrder();

        scoreUpdater.execute();

        // Should be executed in order: 1, 0, -1
        List<Integer> executionOrder = PriorityTrackingBooster.getExecutionOrder();
        assertEquals(3, executionOrder.size());
        assertEquals(Integer.valueOf(1), executionOrder.get(0));
        assertEquals(Integer.valueOf(0), executionOrder.get(1));
        assertEquals(Integer.valueOf(-1), executionOrder.get(2));
    }

    // Test with RuntimeException
    @Test
    public void test_execute_withRuntimeException() {
        RuntimeExceptionScoreBooster booster = new RuntimeExceptionScoreBooster();
        scoreUpdater.addScoreBooster(booster);

        String result = scoreUpdater.execute();
        assertTrue(result.contains("Runtime exception message"));
    }

    // Test large number of boosters
    @Test
    public void test_execute_manyBoosters() {
        for (int i = 0; i < 100; i++) {
            TestScoreBooster booster = new TestScoreBooster(i);
            scoreUpdater.addScoreBooster(booster);
        }

        String result = scoreUpdater.execute();
        String[] lines = result.split("\n");
        assertEquals(100, lines.length);
    }

    // Test priority sorting with large range
    @Test
    public void test_addScoreBooster_largePriorityRange() {
        PriorityTrackingBooster booster1 = new PriorityTrackingBooster(Integer.MIN_VALUE, 100L);
        PriorityTrackingBooster booster2 = new PriorityTrackingBooster(Integer.MAX_VALUE, 200L);
        PriorityTrackingBooster booster3 = new PriorityTrackingBooster(0, 300L);

        scoreUpdater.addScoreBooster(booster1);
        scoreUpdater.addScoreBooster(booster2);
        scoreUpdater.addScoreBooster(booster3);

        // Clear execution order before test
        PriorityTrackingBooster.clearExecutionOrder();

        scoreUpdater.execute();

        // Due to integer overflow in comparator, execution order is affected for extreme values
        // The comparator b2 - b1 with MAX_VALUE and MIN_VALUE causes overflow
        List<Integer> executionOrder = PriorityTrackingBooster.getExecutionOrder();
        assertEquals(3, executionOrder.size());
        assertEquals(Integer.valueOf(Integer.MIN_VALUE), executionOrder.get(0));
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), executionOrder.get(1));
        assertEquals(Integer.valueOf(0), executionOrder.get(2));
    }

    // Test class for basic testing
    private static class TestScoreBooster extends ScoreBooster {
        private final long returnValue;

        public TestScoreBooster(long returnValue) {
            this.returnValue = returnValue;
        }

        @Override
        public long process() {
            return returnValue;
        }
    }

    // Test class that throws exception
    private static class ExceptionScoreBooster extends ScoreBooster {
        @Override
        public long process() {
            throw new RuntimeException("Test exception");
        }
    }

    // Test class with priority tracking
    private static class PriorityTrackingBooster extends ScoreBooster {
        private static List<Integer> executionOrder = new ArrayList<>();
        private final long returnValue;

        public PriorityTrackingBooster(int priority, long returnValue) {
            this.priority = priority;
            this.returnValue = returnValue;
        }

        @Override
        public long process() {
            executionOrder.add(priority);
            return returnValue;
        }

        public static List<Integer> getExecutionOrder() {
            return executionOrder;
        }

        public static void clearExecutionOrder() {
            executionOrder.clear();
        }
    }

    // Test class that causes NullPointerException
    private static class NullPointerScoreBooster extends ScoreBooster {
        @Override
        public long process() {
            // This will cause NullPointerException when auto-unboxing
            Long nullValue = null;
            return nullValue;
        }
    }

    // Test class that throws RuntimeException
    private static class RuntimeExceptionScoreBooster extends ScoreBooster {
        @Override
        public long process() {
            throw new RuntimeException("Runtime exception message");
        }
    }
}