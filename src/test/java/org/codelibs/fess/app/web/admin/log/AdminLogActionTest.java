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

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for AdminLogAction.
 * Tests the isLogFilename and sanitizeFilename methods.
 */
public class AdminLogActionTest extends UnitFessTestCase {

    // ==================================================================================
    //                                                                 isLogFilename Tests
    //                                                                 ===================

    @Test
    public void test_isLogFilename_withLogExtension() {
        assertTrue(AdminLogAction.isLogFilename("fess.log"));
        assertTrue(AdminLogAction.isLogFilename("crawler.log"));
        assertTrue(AdminLogAction.isLogFilename("audit.log"));
        assertTrue(AdminLogAction.isLogFilename("application-2024-01-01.log"));
    }

    @Test
    public void test_isLogFilename_withLogGzExtension() {
        assertTrue(AdminLogAction.isLogFilename("fess.log.gz"));
        assertTrue(AdminLogAction.isLogFilename("crawler.log.gz"));
        assertTrue(AdminLogAction.isLogFilename("application-2024-01-01.log.gz"));
    }

    @Test
    public void test_isLogFilename_withInvalidExtension() {
        assertFalse(AdminLogAction.isLogFilename("fess.txt"));
        assertFalse(AdminLogAction.isLogFilename("fess.log.bak"));
        assertFalse(AdminLogAction.isLogFilename("fess.log.zip"));
        assertFalse(AdminLogAction.isLogFilename("fess.logs"));
        assertFalse(AdminLogAction.isLogFilename("fess.log.1"));
        assertFalse(AdminLogAction.isLogFilename("config.xml"));
        assertFalse(AdminLogAction.isLogFilename("data.json"));
    }

    @Test
    public void test_isLogFilename_withNoExtension() {
        assertFalse(AdminLogAction.isLogFilename("fess"));
        assertFalse(AdminLogAction.isLogFilename("log"));
    }

    @Test
    public void test_isLogFilename_withOnlyExtension() {
        assertTrue(AdminLogAction.isLogFilename(".log"));
        assertTrue(AdminLogAction.isLogFilename(".log.gz"));
    }

    @Test
    public void test_isLogFilename_caseSensitive() {
        assertFalse(AdminLogAction.isLogFilename("fess.LOG"));
        assertFalse(AdminLogAction.isLogFilename("fess.Log"));
        assertFalse(AdminLogAction.isLogFilename("fess.LOG.GZ"));
        assertFalse(AdminLogAction.isLogFilename("fess.LOG.gz"));
    }

    @Test
    public void test_isLogFilename_withEmptyString() {
        assertFalse(AdminLogAction.isLogFilename(""));
    }

    @Test
    public void test_isLogFilename_withPathSeparators() {
        // isLogFilename only checks the file extension, not path components
        assertTrue(AdminLogAction.isLogFilename("subdir/fess.log"));
        assertTrue(AdminLogAction.isLogFilename("../fess.log"));
    }

    // ==================================================================================
    //                                                              sanitizeFilename Tests
    //                                                              ======================

    @Test
    public void test_sanitizeFilename_normalFilename() {
        assertEquals("fess.log", AdminLogAction.sanitizeFilename("fess.log"));
        assertEquals("crawler.log.gz", AdminLogAction.sanitizeFilename("crawler.log.gz"));
        assertEquals("app-2024-01-01.log", AdminLogAction.sanitizeFilename("app-2024-01-01.log"));
    }

    @Test
    public void test_sanitizeFilename_removesDoubleDots() {
        assertEquals("", AdminLogAction.sanitizeFilename(".."));
        assertEquals("/etc/passwd", AdminLogAction.sanitizeFilename("../etc/passwd"));
        assertEquals("/etc/passwd", AdminLogAction.sanitizeFilename("../../etc/passwd"));
        assertEquals("test.log", AdminLogAction.sanitizeFilename("..test.log"));
        assertEquals("testlog", AdminLogAction.sanitizeFilename("test..log"));
    }

    @Test
    public void test_sanitizeFilename_removesMultipleDoubleDots() {
        // "...." becomes "" (two ".." are removed)
        assertEquals("", AdminLogAction.sanitizeFilename("...."));
        assertEquals("/test.log", AdminLogAction.sanitizeFilename("....//test.log"));
        assertEquals("/test.log", AdminLogAction.sanitizeFilename("....//....//test.log"));
    }

    @Test
    public void test_sanitizeFilename_removesWhitespace() {
        assertEquals("fess.log", AdminLogAction.sanitizeFilename("fess .log"));
        assertEquals("fess.log", AdminLogAction.sanitizeFilename(" fess.log"));
        assertEquals("fess.log", AdminLogAction.sanitizeFilename("fess.log "));
        assertEquals("fess.log", AdminLogAction.sanitizeFilename(" fess.log "));
        assertEquals("fess.log", AdminLogAction.sanitizeFilename("fess\t.log"));
        assertEquals("fess.log", AdminLogAction.sanitizeFilename("fess\n.log"));
        assertEquals("fess.log", AdminLogAction.sanitizeFilename("fess\r.log"));
    }

    @Test
    public void test_sanitizeFilename_removesDoubleDotsAndWhitespace() {
        assertEquals("/test.log", AdminLogAction.sanitizeFilename(".. /test.log"));
        assertEquals("/test.log", AdminLogAction.sanitizeFilename(" ../test.log"));
        assertEquals("/test.log", AdminLogAction.sanitizeFilename(".. / .. /test.log"));
        // Whitespace between dots - must be removed before ".." removal
        assertEquals("/test.log", AdminLogAction.sanitizeFilename(". ./test.log"));
        assertEquals("/test.log", AdminLogAction.sanitizeFilename(". . /test.log"));
        assertEquals("/test.log", AdminLogAction.sanitizeFilename(".\t./test.log"));
        assertEquals("/etc/passwd.log", AdminLogAction.sanitizeFilename(". ./. ./etc/passwd.log"));
    }

    @Test
    public void test_sanitizeFilename_pathTraversalPatterns() {
        // Common path traversal attack patterns - multiple slashes are normalized to single
        assertEquals("/etc/passwd.log", AdminLogAction.sanitizeFilename("../etc/passwd.log"));
        assertEquals("/etc/passwd.log", AdminLogAction.sanitizeFilename("../../etc/passwd.log"));
        assertEquals("/etc/passwd.log", AdminLogAction.sanitizeFilename("../../../etc/passwd.log"));

        // With multiple consecutive dots
        assertEquals("/test.log", AdminLogAction.sanitizeFilename("....//test.log"));
        assertEquals("/test.log", AdminLogAction.sanitizeFilename("......//....//test.log"));

        // Mixed patterns
        assertEquals("/var/log/other.log", AdminLogAction.sanitizeFilename("../var/log/other.log"));
    }

    @Test
    public void test_sanitizeFilename_preservesSingleDot() {
        assertEquals(".", AdminLogAction.sanitizeFilename("."));
        assertEquals("./test.log", AdminLogAction.sanitizeFilename("./test.log"));
        assertEquals(".hidden.log", AdminLogAction.sanitizeFilename(".hidden.log"));
    }

    @Test
    public void test_sanitizeFilename_preservesTripleDots() {
        // "..." has one ".." removed, leaving "."
        assertEquals(".", AdminLogAction.sanitizeFilename("..."));
        assertEquals("./test.log", AdminLogAction.sanitizeFilename(".../test.log"));
    }

    @Test
    public void test_sanitizeFilename_emptyString() {
        assertEquals("", AdminLogAction.sanitizeFilename(""));
    }

    @Test
    public void test_sanitizeFilename_normalizesMultipleSlashes() {
        assertEquals("/test.log", AdminLogAction.sanitizeFilename("//test.log"));
        assertEquals("/test.log", AdminLogAction.sanitizeFilename("///test.log"));
        assertEquals("/test.log", AdminLogAction.sanitizeFilename("////test.log"));
        assertEquals("a/b/c.log", AdminLogAction.sanitizeFilename("a//b///c.log"));
    }

    @Test
    public void test_sanitizeFilename_windowsPathSeparators() {
        // Windows path separators are normalized to forward slashes
        assertEquals("test/file.log", AdminLogAction.sanitizeFilename("test\\file.log"));
        assertEquals("/test.log", AdminLogAction.sanitizeFilename("..\\test.log"));
        assertEquals("/test.log", AdminLogAction.sanitizeFilename("..\\..\\test.log"));
        assertEquals("/etc/passwd.log", AdminLogAction.sanitizeFilename("..\\..\\etc\\passwd.log"));
        // Mixed slashes
        assertEquals("/test.log", AdminLogAction.sanitizeFilename("..\\/../test.log"));
        assertEquals("/test.log", AdminLogAction.sanitizeFilename("../..\\test.log"));
    }

    // ==================================================================================
    //                                                         Combined Security Tests
    //                                                         =========================

    @Test
    public void test_security_pathTraversalWithLogExtension() {
        // These patterns attempt to access files outside the log directory
        // but must end with .log to pass isLogFilename check

        String sanitized1 = AdminLogAction.sanitizeFilename("../../../var/log/auth.log");
        assertEquals("/var/log/auth.log", sanitized1);
        assertTrue(AdminLogAction.isLogFilename(sanitized1));

        String sanitized2 = AdminLogAction.sanitizeFilename("....//....//var/log/syslog.log");
        assertEquals("/var/log/syslog.log", sanitized2);
        assertTrue(AdminLogAction.isLogFilename(sanitized2));
    }

    @Test
    public void test_security_encodedPathTraversal() {
        // These are literal strings (not URL encoded at this point)
        // URL encoding would be handled before Base64 decoding
        assertEquals("fess.log", AdminLogAction.sanitizeFilename("fess.log"));
        assertEquals("%2e%2e/test.log", AdminLogAction.sanitizeFilename("%2e%2e/test.log"));
    }

    @Test
    public void test_security_nullBytes() {
        // Null byte injection (the string contains literal null byte)
        assertEquals("fess.log\0.txt", AdminLogAction.sanitizeFilename("fess.log\0.txt"));
    }

    @Test
    public void test_security_unicodeCharacters() {
        // Full-width dots and other Unicode characters
        assertEquals("\uff0e\uff0e/test.log", AdminLogAction.sanitizeFilename("\uff0e\uff0e/test.log"));
    }

    //                                                                    decodeLogFileId
    //                                                                    ===============

    @Test
    public void test_decodeLogFileId_decodesTheIdsOfTheLogList() {
        for (final String name : new String[] { "fess.log", "ab?.log", "fess~~.log", "fess>>>.log", "fess-\u00e9?.log.gz" }) {
            final String id = Base64.getUrlEncoder().encodeToString(name.getBytes(StandardCharsets.UTF_8));
            assertEquals(name, AdminLogAction.decodeLogFileId(id));
        }
    }

    @Test
    public void test_decodeLogFileId_urlSafeCharacters() {
        // "ab?.log" -> "YWI_LmxvZw==" and "fess~~.log" -> "ZmVzc35-LmxvZw==" use "_" and "-"
        assertEquals("ab?.log", AdminLogAction.decodeLogFileId("YWI_LmxvZw=="));
        assertEquals("fess~~.log", AdminLogAction.decodeLogFileId("ZmVzc35-LmxvZw=="));
    }

    @Test
    public void test_decodeLogFileId_standardAlphabetIsStillAccepted() {
        assertEquals("ab?.log", AdminLogAction.decodeLogFileId("YWI/LmxvZw=="));
        assertEquals("fess~~.log", AdminLogAction.decodeLogFileId("ZmVzc35+LmxvZw=="));
    }

    @Test
    public void test_decodeLogFileId_plusDecodedToBlankByThePathIsAccepted() {
        assertEquals("fess~~.log", AdminLogAction.decodeLogFileId("ZmVzc35 LmxvZw=="));
    }

    @Test
    public void test_decodeLogFileId_invalidIdThrows() {
        for (final String id : new String[] { "a", "not base64!", "@@@@" }) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> AdminLogAction.decodeLogFileId(id), id);
        }
    }

    // ==================================================================================
    //                                                                       findLogFile
    //                                                                       ===========

    private Path newLogDir() throws Exception {
        // <root>/etc/hosts.log and <root>/secret.log are outside of the log directory <root>/a/b
        final Path root = Files.createTempDirectory("fess_admin_log");
        final Path logDir = Files.createDirectories(root.resolve("a").resolve("b"));
        Files.createDirectories(root.resolve("etc"));
        Files.write(root.resolve("etc").resolve("hosts.log"), "outside".getBytes(StandardCharsets.UTF_8));
        Files.write(root.resolve("secret.log"), "outside".getBytes(StandardCharsets.UTF_8));
        return logDir;
    }

    @Test
    public void test_findLogFile_findsTheFileByTheNameTheListShows() throws Exception {
        final Path logDir = newLogDir();
        for (final String name : new String[] { "fess.log", "fess.log.gz", "r13 sp ace.log", " lead.log", "trail .log", "tab\there.log",
                "a..b.log", "..hidden.log", "ログ 2026.log.gz" }) {
            Files.write(logDir.resolve(name), name.getBytes(StandardCharsets.UTF_8));
            assertEquals(name, logDir.resolve(name), AdminLogAction.findLogFile(logDir.toString(), name));
        }
    }

    @Test
    public void test_findLogFile_neverRewritesTheName() throws Exception {
        final Path logDir = newLogDir();
        Files.write(logDir.resolve("fess.log"), "plain".getBytes(StandardCharsets.UTF_8));
        Files.write(logDir.resolve("ab.log"), "dots".getBytes(StandardCharsets.UTF_8));
        for (final String name : new String[] { "fe ss.log", " fess.log", "fess.log ", "fess\t.log", "a..b.log", "f.. ess.log" }) {
            assertNull(AdminLogAction.findLogFile(logDir.toString(), name), name);
        }
    }

    @Test
    public void test_findLogFile_refusesWhatIsNotARegularLogFileOfTheLogDirectory() throws Exception {
        final Path logDir = newLogDir();
        Files.write(logDir.resolve("fess.txt"), "text".getBytes(StandardCharsets.UTF_8));
        Files.createDirectories(logDir.resolve("dir.log"));
        for (final String name : new String[] { null, "", "..", ".", "missing.log", "fess.txt", "dir.log" }) {
            assertNull(AdminLogAction.findLogFile(logDir.toString(), name), String.valueOf(name));
        }
        assertNull(AdminLogAction.findLogFile(null, "fess.log"), "no log directory");
        assertNull(AdminLogAction.findLogFile("", "fess.log"), "no log directory");
    }

    @Test
    public void test_findLogFile_noSpellingReachesAFileOutsideTheLogDirectory() throws Exception {
        final Path logDir = newLogDir();
        for (final String name : new String[] { "../../etc/hosts.log", "../../../b/../../etc/hosts.log", "../secret.log",
                "../../secret.log", "a/../../../secret.log", "/../../secret.log", "..\\..\\etc\\hosts.log", "..\\..\\secret.log",
                ". ./. ./etc/hosts.log", ".\t./.\t./etc/hosts.log", ".\n./.\n./etc/hosts.log", "....//....//etc/hosts.log",
                "%2e%2e/%2e%2e/secret.log", "\uff0e\uff0e/\uff0e\uff0e/secret.log", "fess.log\0.txt", "x\0../../secret.log",
                "/etc/passwd.log" }) {
            assertNull(AdminLogAction.findLogFile(logDir.toString(), name), name);
        }
    }

    @Test
    public void test_findLogFile_aLeadingSlashStaysInTheLogDirectory() throws Exception {
        final Path logDir = newLogDir();
        Files.write(Files.createDirectories(logDir.resolve("etc")).resolve("hosts.log"), "inside".getBytes(StandardCharsets.UTF_8));
        assertEquals(logDir.resolve("etc").resolve("hosts.log"), AdminLogAction.findLogFile(logDir.toString(), "/etc/hosts.log"));
    }
}
