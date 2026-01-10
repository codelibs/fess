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
package org.codelibs.fess.crawler.interval;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Test class for FessIntervalController.
 * Tests the error handling improvements and delay functionality.
 */
public class FessIntervalControllerTest extends UnitFessTestCase {

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test that FessIntervalController can be instantiated correctly
     */
    @Test
    public void test_constructor() {
        FessIntervalController controller = new FessIntervalController();
        assertNotNull(controller);
    }

    /**
     * Test getter and setter for delayMillisAfterProcessing
     */
    @Test
    public void test_delayMillisAfterProcessing() {
        FessIntervalController controller = new FessIntervalController();

        controller.setDelayMillisAfterProcessing(1000L);
        assertEquals(1000L, controller.getDelayMillisAfterProcessing());

        controller.setDelayMillisAfterProcessing(5000L);
        assertEquals(5000L, controller.getDelayMillisAfterProcessing());

        controller.setDelayMillisAfterProcessing(0L);
        assertEquals(0L, controller.getDelayMillisAfterProcessing());
    }

    /**
     * Test getter and setter for delayMillisAtNoUrlInQueue
     */
    @Test
    public void test_delayMillisAtNoUrlInQueue() {
        FessIntervalController controller = new FessIntervalController();

        controller.setDelayMillisAtNoUrlInQueue(2000L);
        assertEquals(2000L, controller.getDelayMillisAtNoUrlInQueue());

        controller.setDelayMillisAtNoUrlInQueue(10000L);
        assertEquals(10000L, controller.getDelayMillisAtNoUrlInQueue());

        controller.setDelayMillisAtNoUrlInQueue(0L);
        assertEquals(0L, controller.getDelayMillisAtNoUrlInQueue());
    }

    /**
     * Test getter and setter for delayMillisBeforeProcessing
     */
    @Test
    public void test_delayMillisBeforeProcessing() {
        FessIntervalController controller = new FessIntervalController();

        controller.setDelayMillisBeforeProcessing(500L);
        assertEquals(500L, controller.getDelayMillisBeforeProcessing());

        controller.setDelayMillisBeforeProcessing(3000L);
        assertEquals(3000L, controller.getDelayMillisBeforeProcessing());

        controller.setDelayMillisBeforeProcessing(0L);
        assertEquals(0L, controller.getDelayMillisBeforeProcessing());
    }

    /**
     * Test getter and setter for delayMillisForWaitingNewUrl
     */
    @Test
    public void test_delayMillisForWaitingNewUrl() {
        FessIntervalController controller = new FessIntervalController();

        controller.setDelayMillisForWaitingNewUrl(1500L);
        assertEquals(1500L, controller.getDelayMillisForWaitingNewUrl());

        controller.setDelayMillisForWaitingNewUrl(8000L);
        assertEquals(8000L, controller.getDelayMillisForWaitingNewUrl());

        controller.setDelayMillisForWaitingNewUrl(0L);
        assertEquals(0L, controller.getDelayMillisForWaitingNewUrl());
    }

    /**
     * Test that all delay values can be set together without conflicts
     */
    @Test
    public void test_multipleDelaySettings() {
        FessIntervalController controller = new FessIntervalController();

        controller.setDelayMillisAfterProcessing(1000L);
        controller.setDelayMillisAtNoUrlInQueue(2000L);
        controller.setDelayMillisBeforeProcessing(500L);
        controller.setDelayMillisForWaitingNewUrl(1500L);

        assertEquals(1000L, controller.getDelayMillisAfterProcessing());
        assertEquals(2000L, controller.getDelayMillisAtNoUrlInQueue());
        assertEquals(500L, controller.getDelayMillisBeforeProcessing());
        assertEquals(1500L, controller.getDelayMillisForWaitingNewUrl());
    }

    /**
     * Test that delayForWaitingNewUrl doesn't throw exceptions
     * This tests the improved error handling with proper logging
     * Note: This is a basic smoke test since full integration testing
     * requires ComponentUtil and other dependencies
     */
    @Test
    public void test_delayForWaitingNewUrl_noExceptions() {
        FessIntervalController controller = new FessIntervalController();
        controller.setDelayMillisForWaitingNewUrl(0L); // Set to 0 to avoid actual delay

        // This should not throw any exceptions even if helpers are not available
        try {
            // Note: In unit test environment, ComponentUtil may not be fully initialized
            // The improved error handling should catch and log any exceptions
            // without propagating them
            controller.delayForWaitingNewUrl();
        } catch (Exception e) {
            fail("delayForWaitingNewUrl should not throw exceptions: " + e.getMessage());
        }
    }

    /**
     * Test boundary values for delay settings
     */
    @Test
    public void test_delayBoundaryValues() {
        FessIntervalController controller = new FessIntervalController();

        // Test with very large values
        controller.setDelayMillisAfterProcessing(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, controller.getDelayMillisAfterProcessing());

        // Test with minimum value (0)
        controller.setDelayMillisAtNoUrlInQueue(0L);
        assertEquals(0L, controller.getDelayMillisAtNoUrlInQueue());

        // Test with typical values
        controller.setDelayMillisBeforeProcessing(100L);
        assertEquals(100L, controller.getDelayMillisBeforeProcessing());
    }
}
