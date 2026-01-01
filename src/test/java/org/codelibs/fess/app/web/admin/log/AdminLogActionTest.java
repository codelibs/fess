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
package org.codelibs.fess.app.web.admin.log;

import org.codelibs.fess.unit.UnitFessTestCase;

/**
 * Test class for AdminLogAction.
 * Tests the isLogFilename and sanitizeFilename methods.
 */
public class AdminLogActionTest extends UnitFessTestCase {

    // ==================================================================================
    //                                                                 isLogFilename Tests
    //                                                                 ===================

    public void test_isLogFilename_withLogExtension() {
        assertTrue(AdminLogAction.isLogFilename("fess.log"));
        assertTrue(AdminLogAction.isLogFilename("crawler.log"));
        assertTrue(AdminLogAction.isLogFilename("audit.log"));
        assertTrue(AdminLogAction.isLogFilename("application-2024-01-01.log"));
    }

    public void test_isLogFilename_withLogGzExtension() {
        assertTrue(AdminLogAction.isLogFilename("fess.log.gz"));
        assertTrue(AdminLogAction.isLogFilename("crawler.log.gz"));
        assertTrue(AdminLogAction.isLogFilename("application-2024-01-01.log.gz"));
    }

    public void test_isLogFilename_withInvalidExtension() {
        assertFalse(AdminLogAction.isLogFilename("fess.txt"));
        assertFalse(AdminLogAction.isLogFilename("fess.log.bak"));
        assertFalse(AdminLogAction.isLogFilename("fess.log.zip"));
        assertFalse(AdminLogAction.isLogFilename("fess.logs"));
        assertFalse(AdminLogAction.isLogFilename("fess.log.1"));
        assertFalse(AdminLogAction.isLogFilename("config.xml"));
        assertFalse(AdminLogAction.isLogFilename("data.json"));
    }

    public void test_isLogFilename_withNoExtension() {
        assertFalse(AdminLogAction.isLogFilename("fess"));
        assertFalse(AdminLogAction.isLogFilename("log"));
    }

    public void test_isLogFilename_withOnlyExtension() {
        assertTrue(AdminLogAction.isLogFilename(".log"));
        assertTrue(AdminLogAction.isLogFilename(".log.gz"));
    }

    public void test_isLogFilename_caseSensitive() {
        assertFalse(AdminLogAction.isLogFilename("fess.LOG"));
        assertFalse(AdminLogAction.isLogFilename("fess.Log"));
        assertFalse(AdminLogAction.isLogFilename("fess.LOG.GZ"));
        assertFalse(AdminLogAction.isLogFilename("fess.LOG.gz"));
    }

    public void test_isLogFilename_withEmptyString() {
        assertFalse(AdminLogAction.isLogFilename(""));
    }

    public void test_isLogFilename_withPathSeparators() {
        // isLogFilename only checks the file extension, not path components
        assertTrue(AdminLogAction.isLogFilename("subdir/fess.log"));
        assertTrue(AdminLogAction.isLogFilename("../fess.log"));
    }

    // ==================================================================================
    //                                                              sanitizeFilename Tests
    //                                                              ======================

    public void test_sanitizeFilename_normalFilename() {
        assertEquals("fess.log", AdminLogAction.sanitizeFilename("fess.log"));
        assertEquals("crawler.log.gz", AdminLogAction.sanitizeFilename("crawler.log.gz"));
        assertEquals("app-2024-01-01.log", AdminLogAction.sanitizeFilename("app-2024-01-01.log"));
    }

    public void test_sanitizeFilename_removesDoubleDots() {
        assertEquals("", AdminLogAction.sanitizeFilename(".."));
        assertEquals("/etc/passwd", AdminLogAction.sanitizeFilename("../etc/passwd"));
        assertEquals("//etc/passwd", AdminLogAction.sanitizeFilename("../../etc/passwd"));
        assertEquals("test.log", AdminLogAction.sanitizeFilename("..test.log"));
        assertEquals("test.log", AdminLogAction.sanitizeFilename("test..log"));
    }

    public void test_sanitizeFilename_removesMultipleDoubleDots() {
        // "...." becomes "" (two ".." are removed)
        assertEquals("", AdminLogAction.sanitizeFilename("...."));
        assertEquals("//test.log", AdminLogAction.sanitizeFilename("....//test.log"));
        assertEquals("////test.log", AdminLogAction.sanitizeFilename("....//....//test.log"));
    }

    public void test_sanitizeFilename_removesWhitespace() {
        assertEquals("fess.log", AdminLogAction.sanitizeFilename("fess .log"));
        assertEquals("fess.log", AdminLogAction.sanitizeFilename(" fess.log"));
        assertEquals("fess.log", AdminLogAction.sanitizeFilename("fess.log "));
        assertEquals("fess.log", AdminLogAction.sanitizeFilename(" fess.log "));
        assertEquals("fess.log", AdminLogAction.sanitizeFilename("fess\t.log"));
        assertEquals("fess.log", AdminLogAction.sanitizeFilename("fess\n.log"));
        assertEquals("fess.log", AdminLogAction.sanitizeFilename("fess\r.log"));
    }

    public void test_sanitizeFilename_removesDoubleDotsAndWhitespace() {
        assertEquals("/test.log", AdminLogAction.sanitizeFilename(".. /test.log"));
        assertEquals("/test.log", AdminLogAction.sanitizeFilename(" ../test.log"));
        assertEquals("//test.log", AdminLogAction.sanitizeFilename(".. / .. /test.log"));
    }

    public void test_sanitizeFilename_pathTraversalPatterns() {
        // Common path traversal attack patterns
        assertEquals("/etc/passwd.log", AdminLogAction.sanitizeFilename("../etc/passwd.log"));
        assertEquals("//etc/passwd.log", AdminLogAction.sanitizeFilename("../../etc/passwd.log"));
        assertEquals("///etc/passwd.log", AdminLogAction.sanitizeFilename("../../../etc/passwd.log"));

        // With multiple consecutive dots
        assertEquals("//test.log", AdminLogAction.sanitizeFilename("....//test.log"));
        assertEquals("////test.log", AdminLogAction.sanitizeFilename("......//....//test.log"));

        // Mixed patterns
        assertEquals("/var/log/other.log", AdminLogAction.sanitizeFilename("../var/log/other.log"));
    }

    public void test_sanitizeFilename_preservesSingleDot() {
        assertEquals(".", AdminLogAction.sanitizeFilename("."));
        assertEquals("./test.log", AdminLogAction.sanitizeFilename("./test.log"));
        assertEquals(".hidden.log", AdminLogAction.sanitizeFilename(".hidden.log"));
    }

    public void test_sanitizeFilename_preservesTripleDots() {
        // "..." has one ".." removed, leaving "."
        assertEquals(".", AdminLogAction.sanitizeFilename("..."));
        assertEquals("./test.log", AdminLogAction.sanitizeFilename(".../test.log"));
    }

    public void test_sanitizeFilename_emptyString() {
        assertEquals("", AdminLogAction.sanitizeFilename(""));
    }

    public void test_sanitizeFilename_windowsPathSeparators() {
        // Windows path separators are not specifically handled
        assertEquals("test\\file.log", AdminLogAction.sanitizeFilename("test\\file.log"));
        assertEquals("\\test.log", AdminLogAction.sanitizeFilename("..\\test.log"));
    }

    // ==================================================================================
    //                                                         Combined Security Tests
    //                                                         =========================

    public void test_security_pathTraversalWithLogExtension() {
        // These patterns attempt to access files outside the log directory
        // but must end with .log to pass isLogFilename check

        String sanitized1 = AdminLogAction.sanitizeFilename("../../../var/log/auth.log");
        assertEquals("///var/log/auth.log", sanitized1);
        assertTrue(AdminLogAction.isLogFilename(sanitized1));

        String sanitized2 = AdminLogAction.sanitizeFilename("....//....//var/log/syslog.log");
        assertEquals("////var/log/syslog.log", sanitized2);
        assertTrue(AdminLogAction.isLogFilename(sanitized2));
    }

    public void test_security_encodedPathTraversal() {
        // These are literal strings (not URL encoded at this point)
        // URL encoding would be handled before Base64 decoding
        assertEquals("fess.log", AdminLogAction.sanitizeFilename("fess.log"));
        assertEquals("%2e%2e/test.log", AdminLogAction.sanitizeFilename("%2e%2e/test.log"));
    }

    public void test_security_nullBytes() {
        // Null byte injection (the string contains literal null byte)
        assertEquals("fess.log\0.txt", AdminLogAction.sanitizeFilename("fess.log\0.txt"));
    }

    public void test_security_unicodeCharacters() {
        // Full-width dots and other Unicode characters
        assertEquals("\uff0e\uff0e/test.log", AdminLogAction.sanitizeFilename("\uff0e\uff0e/test.log"));
    }
}
