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
package org.codelibs.fess.tomcat.valve;

import org.codelibs.fess.unit.UnitFessTestCase;

public class SuppressErrorReportValveTest extends UnitFessTestCase {

    private SuppressErrorReportValve valve;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        valve = new SuppressErrorReportValve();
    }

    public void test_constructor() {
        assertNotNull(valve);
    }

    public void test_showReportDisabled() {
        assertFalse(valve.isShowReport());
    }

    public void test_showServerInfoDisabled() {
        assertFalse(valve.isShowServerInfo());
    }

    public void test_defaultSettings() {
        final SuppressErrorReportValve newValve = new SuppressErrorReportValve();
        assertFalse(newValve.isShowReport());
        assertFalse(newValve.isShowServerInfo());
    }

    public void test_setShowReport() {
        valve.setShowReport(true);
        assertTrue(valve.isShowReport());

        valve.setShowReport(false);
        assertFalse(valve.isShowReport());
    }

    public void test_setShowServerInfo() {
        valve.setShowServerInfo(true);
        assertTrue(valve.isShowServerInfo());

        valve.setShowServerInfo(false);
        assertFalse(valve.isShowServerInfo());
    }

    public void test_inheritance() {
        assertTrue(valve instanceof org.apache.catalina.valves.ErrorReportValve);
    }

    public void test_constructorInitializesCorrectDefaultValues() {
        final SuppressErrorReportValve newValve = new SuppressErrorReportValve();

        // Verify both settings are disabled by default
        assertFalse("ShowReport should be disabled by default", newValve.isShowReport());
        assertFalse("ShowServerInfo should be disabled by default", newValve.isShowServerInfo());
    }

    public void test_multipleInstancesHaveIndependentSettings() {
        final SuppressErrorReportValve valve1 = new SuppressErrorReportValve();
        final SuppressErrorReportValve valve2 = new SuppressErrorReportValve();

        valve1.setShowReport(true);
        valve1.setShowServerInfo(true);

        // valve2 should still have default values
        assertFalse("valve2 should have independent ShowReport setting", valve2.isShowReport());
        assertFalse("valve2 should have independent ShowServerInfo setting", valve2.isShowServerInfo());
    }

    public void test_toggleSettingsMultipleTimes() {
        // Test toggling ShowReport multiple times
        assertFalse(valve.isShowReport());

        valve.setShowReport(true);
        assertTrue(valve.isShowReport());

        valve.setShowReport(false);
        assertFalse(valve.isShowReport());

        valve.setShowReport(true);
        assertTrue(valve.isShowReport());

        // Test toggling ShowServerInfo multiple times
        assertFalse(valve.isShowServerInfo());

        valve.setShowServerInfo(true);
        assertTrue(valve.isShowServerInfo());

        valve.setShowServerInfo(false);
        assertFalse(valve.isShowServerInfo());

        valve.setShowServerInfo(true);
        assertTrue(valve.isShowServerInfo());
    }

    public void test_settingsBothTrueAndFalse() {
        // Test all combinations of settings
        valve.setShowReport(true);
        valve.setShowServerInfo(true);
        assertTrue(valve.isShowReport());
        assertTrue(valve.isShowServerInfo());

        valve.setShowReport(true);
        valve.setShowServerInfo(false);
        assertTrue(valve.isShowReport());
        assertFalse(valve.isShowServerInfo());

        valve.setShowReport(false);
        valve.setShowServerInfo(true);
        assertFalse(valve.isShowReport());
        assertTrue(valve.isShowServerInfo());

        valve.setShowReport(false);
        valve.setShowServerInfo(false);
        assertFalse(valve.isShowReport());
        assertFalse(valve.isShowServerInfo());
    }
}