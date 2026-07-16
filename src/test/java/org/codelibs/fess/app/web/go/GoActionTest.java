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
package org.codelibs.fess.app.web.go;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.codelibs.fess.helper.ProtocolHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.system.DBFluteSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Test class for GoAction.
 * Tests the isFileSystemPath method for various protocol types.
 */
public class GoActionTest extends UnitFessTestCase {

    private TestableGoAction goAction;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        // Setup protocolHelper with test configuration
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http,https";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file,smb,smb1,ftp,storage,s3,gcs";
            }
        });
        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();
        ComponentUtil.register(protocolHelper, "protocolHelper");

        goAction = new TestableGoAction();
        goAction.setSystemHelper(new FixedSystemHelper());
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    // Test class to expose protected methods for testing
    private static class TestableGoAction extends GoAction {
        @Override
        public boolean isFileSystemPath(final String url) {
            return super.isFileSystemPath(url);
        }

        @Override
        public LocalDateTime parseQueryRequestedAt(final String rt) {
            return super.parseQueryRequestedAt(rt);
        }

        // systemHelper is injected via @Resource in production; set it directly for unit tests.
        void setSystemHelper(final SystemHelper systemHelper) {
            this.systemHelper = systemHelper;
        }
    }

    /** Fixed instant used as "now" so the fallback is deterministic. */
    private static final LocalDateTime FIXED_NOW = LocalDateTime.of(2020, 1, 2, 3, 4, 5);

    /** SystemHelper returning a fixed "current time" so the fallback branch is assertable. */
    private static class FixedSystemHelper extends SystemHelper {
        @Override
        public LocalDateTime getCurrentTimeAsLocalDateTime() {
            return FIXED_NOW;
        }
    }

    // ==================================================================================
    //                                                            parseQueryRequestedAt Tests
    //                                                            ===========================

    /**
     * Regression test: {@code rt} is an unconstrained request parameter, so a non-numeric
     * value used to reach {@code Long.parseLong} unguarded and raise NumberFormatException,
     * failing the user's navigation with an HTTP 500. A malformed value must instead be
     * treated as absent and fall back to the current time.
     */
    @Test
    public void test_parseQueryRequestedAt_malformed_fallsBackToCurrentTime() {
        assertEquals(FIXED_NOW, goAction.parseQueryRequestedAt("not-a-number"));
        assertEquals(FIXED_NOW, goAction.parseQueryRequestedAt(""));
        assertEquals(FIXED_NOW, goAction.parseQueryRequestedAt(" "));
        assertEquals(FIXED_NOW, goAction.parseQueryRequestedAt("123abc"));
        assertEquals(FIXED_NOW, goAction.parseQueryRequestedAt("1.5"));
        assertEquals(FIXED_NOW, goAction.parseQueryRequestedAt("1,000"));
        // Numeric but outside long range: parseLong also rejects these.
        assertEquals(FIXED_NOW, goAction.parseQueryRequestedAt("99999999999999999999999"));
    }

    /**
     * A missing {@code rt} must not throw either. {@code @Required} normally rejects this before
     * the action body runs, so this guards the method's own contract rather than the request flow.
     */
    @Test
    public void test_parseQueryRequestedAt_null_fallsBackToCurrentTime() {
        assertEquals(FIXED_NOW, goAction.parseQueryRequestedAt(null));
    }

    /**
     * A well-formed {@code rt} must still be honoured, not silently replaced by the fallback.
     *
     * <p>DfTypeUtil converts via {@code DBFluteSystem.getFinalTimeZone()} (Fess registers a
     * provider for it in FessCurtainBeforeHook), not via TimeZone.getDefault() at call time,
     * so the expectation is read back through that same zone to keep this test independent of
     * the host's time zone. That conversion zone differs from the v2 ClickHandler's UTC
     * conversion; aligning the two would change stored timestamps and is out of scope here.
     */
    @Test
    public void test_parseQueryRequestedAt_valid_usesRtValue() {
        final long rtMs = 1718454896789L; // 2024-06-15T12:34:56.789Z (mid-year: no DST-overlap ambiguity)
        final LocalDateTime actual = goAction.parseQueryRequestedAt(Long.toString(rtMs));
        assertFalse(FIXED_NOW.equals(actual));
        final ZoneId zone = DBFluteSystem.getFinalTimeZone().toZoneId();
        assertEquals(rtMs, ZonedDateTime.of(actual, zone).toInstant().toEpochMilli());
    }

    /**
     * Epoch 0 is a valid timestamp and must be parsed, not treated as absent.
     */
    @Test
    public void test_parseQueryRequestedAt_zero_isParsed() {
        assertFalse(FIXED_NOW.equals(goAction.parseQueryRequestedAt("0")));
    }

    // ==================================================================================
    //                                                                 isFileSystemPath Tests
    //                                                                 ====================

    @Test
    public void test_isFileSystemPath_file_protocol() {
        assertTrue(goAction.isFileSystemPath("file:///path/to/file.txt"));
        assertTrue(goAction.isFileSystemPath("file://localhost/path/to/file.txt"));
        assertTrue(goAction.isFileSystemPath("file:/path/to/file.txt"));
        assertTrue(goAction.isFileSystemPath("file:C:/Users/test/file.txt"));
    }

    @Test
    public void test_isFileSystemPath_smb_protocol() {
        assertTrue(goAction.isFileSystemPath("smb://server/share/path/file.txt"));
        assertTrue(goAction.isFileSystemPath("smb://192.168.1.1/share/file.txt"));
        assertTrue(goAction.isFileSystemPath("smb://server/"));
    }

    @Test
    public void test_isFileSystemPath_smb1_protocol() {
        assertTrue(goAction.isFileSystemPath("smb1://server/share/path/file.txt"));
        assertTrue(goAction.isFileSystemPath("smb1://192.168.1.1/share/file.txt"));
        assertTrue(goAction.isFileSystemPath("smb1://server/"));
    }

    @Test
    public void test_isFileSystemPath_ftp_protocol() {
        assertTrue(goAction.isFileSystemPath("ftp://ftp.example.com/path/file.txt"));
        assertTrue(goAction.isFileSystemPath("ftp://user:pass@ftp.example.com/file.txt"));
        assertTrue(goAction.isFileSystemPath("ftp://192.168.1.1/file.txt"));
    }

    @Test
    public void test_isFileSystemPath_storage_protocol() {
        assertTrue(goAction.isFileSystemPath("storage://container/path/file.txt"));
        assertTrue(goAction.isFileSystemPath("storage://bucket/folder/document.pdf"));
        assertTrue(goAction.isFileSystemPath("storage://my-storage/"));
    }

    @Test
    public void test_isFileSystemPath_s3_protocol() {
        assertTrue(goAction.isFileSystemPath("s3://bucket/path/to/file.txt"));
        assertTrue(goAction.isFileSystemPath("s3://my-bucket/folder/document.pdf"));
        assertTrue(goAction.isFileSystemPath("s3://bucket/"));
        assertTrue(goAction.isFileSystemPath("s3://my-bucket-name/deep/nested/path/file.txt"));
        assertTrue(goAction.isFileSystemPath("s3://bucket-with-dashes/file"));
    }

    @Test
    public void test_isFileSystemPath_gcs_protocol() {
        assertTrue(goAction.isFileSystemPath("gcs://bucket/path/to/file.txt"));
        assertTrue(goAction.isFileSystemPath("gcs://my-bucket/folder/document.pdf"));
        assertTrue(goAction.isFileSystemPath("gcs://bucket/"));
        assertTrue(goAction.isFileSystemPath("gcs://my-bucket-name/deep/nested/path/file.txt"));
        assertTrue(goAction.isFileSystemPath("gcs://bucket_with_underscores/file"));
    }

    @Test
    public void test_isFileSystemPath_http_protocol_not_file_system() {
        assertFalse(goAction.isFileSystemPath("http://example.com/path/file.txt"));
        assertFalse(goAction.isFileSystemPath("http://localhost:8080/file.txt"));
    }

    @Test
    public void test_isFileSystemPath_https_protocol_not_file_system() {
        assertFalse(goAction.isFileSystemPath("https://example.com/path/file.txt"));
        assertFalse(goAction.isFileSystemPath("https://secure.example.com/file.txt"));
    }

    @Test
    public void test_isFileSystemPath_other_protocols_not_file_system() {
        assertFalse(goAction.isFileSystemPath("mailto:test@example.com"));
        assertFalse(goAction.isFileSystemPath("ldap://server/path"));
        assertFalse(goAction.isFileSystemPath("ssh://server/path"));
        assertFalse(goAction.isFileSystemPath("data:text/plain;base64,SGVsbG8="));
    }

    @Test
    public void test_isFileSystemPath_empty_and_invalid() {
        assertFalse(goAction.isFileSystemPath(""));
        assertFalse(goAction.isFileSystemPath("not-a-url"));
        assertFalse(goAction.isFileSystemPath("/local/path"));
        assertFalse(goAction.isFileSystemPath("C:\\Windows\\System32"));
    }

    @Test
    public void test_isFileSystemPath_case_sensitivity() {
        // URLs are case-sensitive for protocol
        assertFalse(goAction.isFileSystemPath("FILE://path"));
        assertFalse(goAction.isFileSystemPath("S3://bucket/path"));
        assertFalse(goAction.isFileSystemPath("GCS://bucket/path"));
        assertFalse(goAction.isFileSystemPath("FTP://server/path"));
        assertFalse(goAction.isFileSystemPath("SMB://server/share"));
    }

    @Test
    public void test_isFileSystemPath_s3_various_bucket_names() {
        // S3 bucket names can contain lowercase letters, numbers, hyphens, and periods
        assertTrue(goAction.isFileSystemPath("s3://my-bucket/file"));
        assertTrue(goAction.isFileSystemPath("s3://my.bucket/file"));
        assertTrue(goAction.isFileSystemPath("s3://mybucket123/file"));
        assertTrue(goAction.isFileSystemPath("s3://123bucket/file"));
    }

    @Test
    public void test_isFileSystemPath_gcs_various_bucket_names() {
        // GCS bucket names can contain lowercase letters, numbers, hyphens, underscores, and periods
        assertTrue(goAction.isFileSystemPath("gcs://my-bucket/file"));
        assertTrue(goAction.isFileSystemPath("gcs://my.bucket/file"));
        assertTrue(goAction.isFileSystemPath("gcs://my_bucket/file"));
        assertTrue(goAction.isFileSystemPath("gcs://mybucket123/file"));
    }

    @Test
    public void test_isFileSystemPath_s3_with_special_characters_in_path() {
        assertTrue(goAction.isFileSystemPath("s3://bucket/path/file%20with%20spaces.txt"));
        assertTrue(goAction.isFileSystemPath("s3://bucket/path/ファイル.txt"));
        assertTrue(goAction.isFileSystemPath("s3://bucket/path/file+name.txt"));
    }

    @Test
    public void test_isFileSystemPath_gcs_with_special_characters_in_path() {
        assertTrue(goAction.isFileSystemPath("gcs://bucket/path/file%20with%20spaces.txt"));
        assertTrue(goAction.isFileSystemPath("gcs://bucket/path/ファイル.txt"));
        assertTrue(goAction.isFileSystemPath("gcs://bucket/path/file+name.txt"));
    }
}
