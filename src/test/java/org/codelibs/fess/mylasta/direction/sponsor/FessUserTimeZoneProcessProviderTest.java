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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.util.TimeZone;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.servlet.request.RequestManager;
import org.lastaflute.web.servlet.request.SimpleRequestManager;

public class FessUserTimeZoneProcessProviderTest extends UnitFessTestCase {

    private FessUserTimeZoneProcessProvider provider;
    private RequestManager requestManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        provider = new FessUserTimeZoneProcessProvider();
        requestManager = createMockRequestManager();
    }

    @Override
    public void tearDown() throws Exception {
        provider = null;
        requestManager = null;
        super.tearDown();
    }

    // Test isUseTimeZoneHandling() method
    public void test_isUseTimeZoneHandling() {
        // Verify that time zone handling is disabled
        assertFalse(provider.isUseTimeZoneHandling());
    }

    // Test isAcceptCookieTimeZone() method
    public void test_isAcceptCookieTimeZone() {
        // Verify that cookie time zone is not accepted
        assertFalse(provider.isAcceptCookieTimeZone());
    }

    // Test findBusinessTimeZone() method
    public void test_findBusinessTimeZone() {
        // Setup
        ActionRuntime runtimeMeta = null; // Can be null as it's not used in the implementation

        // Execute
        OptionalThing<TimeZone> result = provider.findBusinessTimeZone(runtimeMeta, requestManager);

        // Verify that empty optional is returned
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    // Test getRequestedTimeZone() method
    public void test_getRequestedTimeZone() {
        // Execute
        TimeZone result = provider.getRequestedTimeZone(requestManager);

        // Verify that the central time zone is returned
        assertNotNull(result);
        assertEquals(FessUserTimeZoneProcessProvider.centralTimeZone, result);
        assertEquals(TimeZone.getDefault(), result);
    }

    // Test getRequestedTimeZone() with different default time zone
    public void test_getRequestedTimeZone_withDifferentDefaultTimeZone() {
        // Store original time zone
        TimeZone originalTimeZone = TimeZone.getDefault();

        try {
            // Change default time zone temporarily
            TimeZone newTimeZone = TimeZone.getTimeZone("America/New_York");
            TimeZone.setDefault(newTimeZone);

            // Create new provider instance to pick up the new default
            FessUserTimeZoneProcessProvider newProvider = new FessUserTimeZoneProcessProvider();

            // Execute
            TimeZone result = newProvider.getRequestedTimeZone(requestManager);

            // Verify that the provider still returns the static centralTimeZone
            assertNotNull(result);
            assertEquals(FessUserTimeZoneProcessProvider.centralTimeZone, result);
            assertNotSame(newTimeZone, result);
        } finally {
            // Restore original time zone
            TimeZone.setDefault(originalTimeZone);
        }
    }

    // Test toString() method
    public void test_toString() {
        // Execute
        String result = provider.toString();

        // Verify the string format
        assertNotNull(result);
        assertTrue(result.contains("FessUserTimeZoneProcessProvider"));
        assertTrue(result.contains("useTimeZoneHandling=false"));
        assertTrue(result.contains("acceptCookieTimeZone=false"));
        assertTrue(result.contains("@"));

        // Verify that hashCode is included in hex format
        String expectedHashCode = Integer.toHexString(provider.hashCode());
        assertTrue(result.contains(expectedHashCode));
    }

    // Test toString() format consistency
    public void test_toString_formatConsistency() {
        // Create multiple instances and verify consistent format
        FessUserTimeZoneProcessProvider provider1 = new FessUserTimeZoneProcessProvider();
        FessUserTimeZoneProcessProvider provider2 = new FessUserTimeZoneProcessProvider();

        String result1 = provider1.toString();
        String result2 = provider2.toString();

        // Both should have the same format structure
        assertTrue(result1.startsWith("FessUserTimeZoneProcessProvider:{"));
        assertTrue(result2.startsWith("FessUserTimeZoneProcessProvider:{"));

        // But different hash codes
        assertFalse(result1.equals(result2));
    }

    // Test static field initialization
    public void test_centralTimeZone_initialization() {
        // Verify that centralTimeZone is properly initialized
        assertNotNull(FessUserTimeZoneProcessProvider.centralTimeZone);
        assertEquals(TimeZone.getDefault(), FessUserTimeZoneProcessProvider.centralTimeZone);

        // Verify it's the same instance for all provider instances
        FessUserTimeZoneProcessProvider provider1 = new FessUserTimeZoneProcessProvider();
        FessUserTimeZoneProcessProvider provider2 = new FessUserTimeZoneProcessProvider();

        TimeZone tz1 = provider1.getRequestedTimeZone(requestManager);
        TimeZone tz2 = provider2.getRequestedTimeZone(requestManager);

        assertSame(tz1, tz2);
        assertSame(FessUserTimeZoneProcessProvider.centralTimeZone, tz1);
    }

    // Test with null RequestManager (edge case)
    public void test_getRequestedTimeZone_withNullRequestManager() {
        // Execute with null request manager
        TimeZone result = provider.getRequestedTimeZone(null);

        // Should still return the central time zone
        assertNotNull(result);
        assertEquals(FessUserTimeZoneProcessProvider.centralTimeZone, result);
    }

    // Test findBusinessTimeZone with null parameters
    public void test_findBusinessTimeZone_withNullParameters() {
        // Execute with null parameters
        OptionalThing<TimeZone> result = provider.findBusinessTimeZone(null, null);

        // Should still return empty optional
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    // Helper method to create mock RequestManager
    private RequestManager createMockRequestManager() {
        return new SimpleRequestManager() {
            // No additional overrides needed for testing
        };
    }
}