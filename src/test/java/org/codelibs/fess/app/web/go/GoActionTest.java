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

import org.codelibs.fess.helper.ProtocolHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
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
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    // Test class to expose protected method for testing
    private static class TestableGoAction extends GoAction {
        @Override
        public boolean isFileSystemPath(final String url) {
            return super.isFileSystemPath(url);
        }
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
