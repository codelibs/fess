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
package org.codelibs.fess.util;

import org.codelibs.fess.unit.UnitFessTestCase;

public class JvmUtilTest extends UnitFessTestCase {
    public void test_getJavaVersion() {
        System.setProperty("java.version", "1.4.2_19");
        assertEquals(4, JvmUtil.getJavaVersion());
        System.setProperty("java.version", "1.5.0_15");
        assertEquals(5, JvmUtil.getJavaVersion());
        System.setProperty("java.version", "1.6.0_34");
        assertEquals(6, JvmUtil.getJavaVersion());
        System.setProperty("java.version", "1.7.0_25");
        assertEquals(7, JvmUtil.getJavaVersion());
        System.setProperty("java.version", "1.8.0_171");
        assertEquals(8, JvmUtil.getJavaVersion());
        System.setProperty("java.version", "9.0.4");
        assertEquals(9, JvmUtil.getJavaVersion());
        System.setProperty("java.version", "10.0.1");
        assertEquals(10, JvmUtil.getJavaVersion());
        System.setProperty("java.version", "11.0.1");
        assertEquals(11, JvmUtil.getJavaVersion());
    }

    public void test_filterJvmOptions() {
        final String[] args = new String[] { //
                "-X111", //
                "8:-X222", //
                "10:-X333", //
                "11:-X444", //
                "8-:-X555", //
                "10-:-X666", //
                "11-:-X777", //
                "12-:-X888", //
                "-X999",//
        };

        System.setProperty("java.version", "1.8.0_171");
        String[] values = JvmUtil.filterJvmOptions(args);
        assertEquals("-X111", values[0]);
        assertEquals("-X222", values[1]);
        assertEquals("-X555", values[2]);
        assertEquals("-X999", values[3]);

        System.setProperty("java.version", "11.0.1");
        values = JvmUtil.filterJvmOptions(args);
        assertEquals("-X111", values[0]);
        assertEquals("-X444", values[1]);
        assertEquals("-X555", values[2]);
        assertEquals("-X666", values[3]);
        assertEquals("-X777", values[4]);
        assertEquals("-X999", values[5]);
    }
}
