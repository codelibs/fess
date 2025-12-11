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
package org.codelibs.fess.app.web.admin.wizard;

import org.codelibs.fess.helper.ProtocolHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Test class for AdminWizardAction.
 * Tests the convertCrawlingPath method for various protocol types.
 */
public class AdminWizardActionTest extends UnitFessTestCase {

    private TestableAdminWizardAction wizardAction;

    @Override
    public void setUp() throws Exception {
        super.setUp();
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

        wizardAction = new TestableAdminWizardAction();
    }

    @Override
    public void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    // Test class to expose protected method for testing
    private static class TestableAdminWizardAction extends AdminWizardAction {
        @Override
        public String convertCrawlingPath(final String path) {
            return super.convertCrawlingPath(path);
        }
    }

    // ==================================================================================
    //                                                            convertCrawlingPath Tests
    //                                                            =========================

    public void test_convertCrawlingPath_http_protocol() {
        assertEquals("http://example.com", wizardAction.convertCrawlingPath("http://example.com"));
        assertEquals("http://example.com/path", wizardAction.convertCrawlingPath("http://example.com/path"));
        assertEquals("http://localhost:8080/app", wizardAction.convertCrawlingPath("http://localhost:8080/app"));
    }

    public void test_convertCrawlingPath_https_protocol() {
        assertEquals("https://example.com", wizardAction.convertCrawlingPath("https://example.com"));
        assertEquals("https://secure.example.com/path", wizardAction.convertCrawlingPath("https://secure.example.com/path"));
    }

    public void test_convertCrawlingPath_smb_protocol() {
        assertEquals("smb://server/share", wizardAction.convertCrawlingPath("smb://server/share"));
        assertEquals("smb://192.168.1.1/path", wizardAction.convertCrawlingPath("smb://192.168.1.1/path"));
    }

    public void test_convertCrawlingPath_smb1_protocol() {
        assertEquals("smb1://server/share", wizardAction.convertCrawlingPath("smb1://server/share"));
        assertEquals("smb1://192.168.1.1/path", wizardAction.convertCrawlingPath("smb1://192.168.1.1/path"));
    }

    public void test_convertCrawlingPath_ftp_protocol() {
        assertEquals("ftp://ftp.example.com/path", wizardAction.convertCrawlingPath("ftp://ftp.example.com/path"));
        assertEquals("ftp://192.168.1.1/files", wizardAction.convertCrawlingPath("ftp://192.168.1.1/files"));
    }

    public void test_convertCrawlingPath_storage_protocol() {
        assertEquals("storage://container/path", wizardAction.convertCrawlingPath("storage://container/path"));
        assertEquals("storage://bucket/folder/file.txt", wizardAction.convertCrawlingPath("storage://bucket/folder/file.txt"));
    }

    public void test_convertCrawlingPath_s3_protocol() {
        assertEquals("s3://bucket/path", wizardAction.convertCrawlingPath("s3://bucket/path"));
        assertEquals("s3://my-bucket/folder/file.txt", wizardAction.convertCrawlingPath("s3://my-bucket/folder/file.txt"));
        assertEquals("s3://bucket/", wizardAction.convertCrawlingPath("s3://bucket/"));
        assertEquals("s3://my-bucket-name/deep/nested/path/file.txt",
                wizardAction.convertCrawlingPath("s3://my-bucket-name/deep/nested/path/file.txt"));
    }

    public void test_convertCrawlingPath_gcs_protocol() {
        assertEquals("gcs://bucket/path", wizardAction.convertCrawlingPath("gcs://bucket/path"));
        assertEquals("gcs://my-bucket/folder/file.txt", wizardAction.convertCrawlingPath("gcs://my-bucket/folder/file.txt"));
        assertEquals("gcs://bucket/", wizardAction.convertCrawlingPath("gcs://bucket/"));
        assertEquals("gcs://my-bucket-name/deep/nested/path/file.txt",
                wizardAction.convertCrawlingPath("gcs://my-bucket-name/deep/nested/path/file.txt"));
    }

    public void test_convertCrawlingPath_www_prefix() {
        assertEquals("http://www.example.com", wizardAction.convertCrawlingPath("www.example.com"));
        assertEquals("http://www.example.com/path", wizardAction.convertCrawlingPath("www.example.com/path"));
    }

    public void test_convertCrawlingPath_double_slash_prefix() {
        assertEquals("file:////server/share", wizardAction.convertCrawlingPath("//server/share"));
        assertEquals("file:////192.168.1.1/path", wizardAction.convertCrawlingPath("//192.168.1.1/path"));
    }

    public void test_convertCrawlingPath_single_slash_prefix() {
        assertEquals("file:/home/user", wizardAction.convertCrawlingPath("/home/user"));
        assertEquals("file:/var/log/app.log", wizardAction.convertCrawlingPath("/var/log/app.log"));
    }

    public void test_convertCrawlingPath_windows_path() {
        assertEquals("file:/C:/Users/test", wizardAction.convertCrawlingPath("C:\\Users\\test"));
        assertEquals("file:/D:/Data/files", wizardAction.convertCrawlingPath("D:\\Data\\files"));
    }

    public void test_convertCrawlingPath_file_protocol() {
        assertEquals("file:/path/to/file", wizardAction.convertCrawlingPath("file:/path/to/file"));
        assertEquals("file:///home/user", wizardAction.convertCrawlingPath("file:///home/user"));
    }

    public void test_convertCrawlingPath_s3_not_converted() {
        // S3 paths should be returned as-is, not converted
        String s3Path = "s3://my-bucket/path/to/object";
        assertEquals(s3Path, wizardAction.convertCrawlingPath(s3Path));

        // Verify it's not being converted to file: protocol
        assertFalse(wizardAction.convertCrawlingPath(s3Path).startsWith("file:"));
    }

    public void test_convertCrawlingPath_gcs_not_converted() {
        // GCS paths should be returned as-is, not converted
        String gcsPath = "gcs://my-bucket/path/to/object";
        assertEquals(gcsPath, wizardAction.convertCrawlingPath(gcsPath));

        // Verify it's not being converted to file: protocol
        assertFalse(wizardAction.convertCrawlingPath(gcsPath).startsWith("file:"));
    }

    public void test_convertCrawlingPath_s3_various_formats() {
        // Various S3 bucket and path formats
        assertEquals("s3://bucket/", wizardAction.convertCrawlingPath("s3://bucket/"));
        assertEquals("s3://my-bucket/", wizardAction.convertCrawlingPath("s3://my-bucket/"));
        assertEquals("s3://bucket.with.dots/path", wizardAction.convertCrawlingPath("s3://bucket.with.dots/path"));
        assertEquals("s3://bucket-with-dashes/path", wizardAction.convertCrawlingPath("s3://bucket-with-dashes/path"));
        assertEquals("s3://123bucket/path", wizardAction.convertCrawlingPath("s3://123bucket/path"));
    }

    public void test_convertCrawlingPath_gcs_various_formats() {
        // Various GCS bucket and path formats
        assertEquals("gcs://bucket/", wizardAction.convertCrawlingPath("gcs://bucket/"));
        assertEquals("gcs://my-bucket/", wizardAction.convertCrawlingPath("gcs://my-bucket/"));
        assertEquals("gcs://bucket.with.dots/path", wizardAction.convertCrawlingPath("gcs://bucket.with.dots/path"));
        assertEquals("gcs://bucket-with-dashes/path", wizardAction.convertCrawlingPath("gcs://bucket-with-dashes/path"));
        assertEquals("gcs://bucket_with_underscores/path", wizardAction.convertCrawlingPath("gcs://bucket_with_underscores/path"));
    }

    public void test_convertCrawlingPath_s3_with_special_characters() {
        // S3 paths with special characters in object keys
        assertEquals("s3://bucket/path/file%20with%20spaces.txt",
                wizardAction.convertCrawlingPath("s3://bucket/path/file%20with%20spaces.txt"));
        assertEquals("s3://bucket/path/ファイル.txt", wizardAction.convertCrawlingPath("s3://bucket/path/ファイル.txt"));
    }

    public void test_convertCrawlingPath_gcs_with_special_characters() {
        // GCS paths with special characters in object keys
        assertEquals("gcs://bucket/path/file%20with%20spaces.txt",
                wizardAction.convertCrawlingPath("gcs://bucket/path/file%20with%20spaces.txt"));
        assertEquals("gcs://bucket/path/ファイル.txt", wizardAction.convertCrawlingPath("gcs://bucket/path/ファイル.txt"));
    }
}
