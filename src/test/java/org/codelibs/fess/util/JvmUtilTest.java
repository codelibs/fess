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
package org.codelibs.fess.util;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class JvmUtilTest extends UnitFessTestCase {
    @Test
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

    @Test
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

    @Test
    public void test_getJavaVersion_edgeCases() {
        // Test with null java.version property
        System.clearProperty("java.version");
        assertEquals(8, JvmUtil.getJavaVersion());

        // Test with empty string
        System.setProperty("java.version", "");
        try {
            JvmUtil.getJavaVersion();
            fail("Should throw NumberFormatException");
        } catch (NumberFormatException e) {
            // Expected
        }

        // Test with invalid format
        System.setProperty("java.version", "invalid");
        try {
            JvmUtil.getJavaVersion();
            fail("Should throw NumberFormatException");
        } catch (NumberFormatException e) {
            // Expected
        }

        // Test with single number
        System.setProperty("java.version", "17");
        assertEquals(17, JvmUtil.getJavaVersion());

        // Test with modern versions
        System.setProperty("java.version", "21.0.1");
        assertEquals(21, JvmUtil.getJavaVersion());

        // Test with early access versions (should fail with NumberFormatException)
        System.setProperty("java.version", "22-ea");
        try {
            JvmUtil.getJavaVersion();
            fail("Should throw NumberFormatException");
        } catch (NumberFormatException e) {
            // Expected
        }

        // Test with build info
        System.setProperty("java.version", "17.0.2+8");
        assertEquals(17, JvmUtil.getJavaVersion());

        // Test with underscore separators in old format
        System.setProperty("java.version", "1.8.0_301");
        assertEquals(8, JvmUtil.getJavaVersion());
    }

    @Test
    public void test_getJavaVersion_variousFormats() {
        // Test different separator combinations
        System.setProperty("java.version", "11.0.15.1");
        assertEquals(11, JvmUtil.getJavaVersion());

        System.setProperty("java.version", "17_0_2");
        assertEquals(17, JvmUtil.getJavaVersion());

        System.setProperty("java.version", "8.0.332");
        assertEquals(8, JvmUtil.getJavaVersion());

        // Edge case: version starts with 1 but not old format
        System.setProperty("java.version", "18.0.1");
        assertEquals(18, JvmUtil.getJavaVersion());

        // Test with extra parts
        System.setProperty("java.version", "11.0.16.1+1");
        assertEquals(11, JvmUtil.getJavaVersion());
    }

    @Test
    public void test_filterJvmOptions_emptyArray() {
        System.setProperty("java.version", "11.0.1");
        String[] result = JvmUtil.filterJvmOptions(new String[0]);
        assertEquals(0, result.length);
    }

    @Test
    public void test_filterJvmOptions_singleElement() {
        System.setProperty("java.version", "11.0.1");

        String[] result = JvmUtil.filterJvmOptions(new String[] { "-Xmx1g" });
        assertEquals(1, result.length);
        assertEquals("-Xmx1g", result[0]);

        result = JvmUtil.filterJvmOptions(new String[] { "11:-Xmx2g" });
        assertEquals(1, result.length);
        assertEquals("-Xmx2g", result[0]);

        result = JvmUtil.filterJvmOptions(new String[] { "8:-Xmx3g" });
        assertEquals(0, result.length);
    }

    @Test
    public void test_filterJvmOptions_versionRanges() {
        System.setProperty("java.version", "15.0.1");

        String[] args = new String[] { "8-:-Xms512m", // From Java 8 onwards
                "11-:-Xmx2g", // From Java 11 onwards
                "17-:-XX:+UseG1GC", // From Java 17 onwards
                "21-:-XX:+UseZGC" // From Java 21 onwards
        };

        String[] result = JvmUtil.filterJvmOptions(args);
        assertEquals(2, result.length);
        assertEquals("-Xms512m", result[0]);
        assertEquals("-Xmx2g", result[1]);
    }

    @Test
    public void test_filterJvmOptions_exactVersionMatching() {
        System.setProperty("java.version", "11.0.1");

        String[] args = new String[] { "8:-Xmx1g", // Exact Java 8
                "11:-Xmx2g", // Exact Java 11
                "17:-Xmx4g", // Exact Java 17
                "11:-XX:+UseG1GC" // Another exact Java 11
        };

        String[] result = JvmUtil.filterJvmOptions(args);
        assertEquals(2, result.length);
        assertEquals("-Xmx2g", result[0]);
        assertEquals("-XX:+UseG1GC", result[1]);
    }

    @Test
    public void test_filterJvmOptions_mixedPatterns() {
        System.setProperty("java.version", "17.0.2");

        String[] args = new String[] { "-server", // No version prefix
                "8-:-Xms1g", // Range from 8
                "17:-Xmx4g", // Exact 17
                "invalid:pattern", // Invalid pattern
                "18-:-XX:+UseZGC", // Range from 18 (should be excluded)
                "11:-XX:+UseG1GC", // Exact 11 (should be excluded)
                "17-:-XX:+UnlockExperimentalVMOptions" // Range from 17
        };

        String[] result = JvmUtil.filterJvmOptions(args);
        assertEquals(5, result.length);
        assertEquals("-server", result[0]);
        assertEquals("-Xms1g", result[1]);
        assertEquals("-Xmx4g", result[2]);
        assertEquals("invalid:pattern", result[3]);
        assertEquals("-XX:+UnlockExperimentalVMOptions", result[4]);
    }

    @Test
    public void test_filterJvmOptions_specialCharacters() {
        System.setProperty("java.version", "11.0.1");

        String[] args = new String[] { "11:-XX:OnOutOfMemoryError=\"kill -9 %p\"", "11:-Djava.awt.headless=true",
                "8-:-Dfile.encoding=UTF-8", "17-:-XX:+UseStringDeduplication" };

        String[] result = JvmUtil.filterJvmOptions(args);
        assertEquals(3, result.length);
        assertEquals("-XX:OnOutOfMemoryError=\"kill -9 %p\"", result[0]);
        assertEquals("-Djava.awt.headless=true", result[1]);
        assertEquals("-Dfile.encoding=UTF-8", result[2]);
    }

    @Test
    public void test_filterJvmOptions_borderlineVersions() {
        // Test with Java 8
        System.setProperty("java.version", "1.8.0_301");
        String[] result = JvmUtil.filterJvmOptions(new String[] { "8-:-Xmx1g", "9-:-Xmx2g" });
        assertEquals(1, result.length);
        assertEquals("-Xmx1g", result[0]);

        // Test with Java 9 (first non-1.x version)
        System.setProperty("java.version", "9.0.4");
        result = JvmUtil.filterJvmOptions(new String[] { "8-:-Xmx1g", "9-:-Xmx2g", "10-:-Xmx3g" });
        assertEquals(2, result.length);
        assertEquals("-Xmx1g", result[0]);
        assertEquals("-Xmx2g", result[1]);
    }

    @Test
    public void test_filterJvmOptions_allFiltered() {
        System.setProperty("java.version", "8.0.1");

        String[] args = new String[] { "11:-Xmx2g", "17:-XX:+UseG1GC", "21-:-XX:+UseZGC" };

        String[] result = JvmUtil.filterJvmOptions(args);
        assertEquals(0, result.length);
    }

    @Test
    public void test_filterJvmOptions_complexVersionPattern() {
        System.setProperty("java.version", "11.0.1");

        // Test version patterns with different separators
        String[] args = new String[] { "11:-Dprop=value:with:colons", "8-:-Xbootclasspath/a:/path/to/jar", "malformed11:-invalid", // Should not match pattern
                "11-malformed:-invalid" // Should not match pattern
        };

        String[] result = JvmUtil.filterJvmOptions(args);
        assertEquals(4, result.length); // All should pass through since invalid patterns don't match
        assertEquals("-Dprop=value:with:colons", result[0]);
        assertEquals("-Xbootclasspath/a:/path/to/jar", result[1]);
        assertEquals("malformed11:-invalid", result[2]);
        assertEquals("11-malformed:-invalid", result[3]);
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        // Restore original java.version property if it exists
        String originalVersion = System.getProperty("java.version");
        if (originalVersion != null) {
            System.setProperty("java.version", originalVersion);
        }
        super.tearDown();
    }
}
