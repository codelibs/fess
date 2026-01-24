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
package org.codelibs.fess.crawler.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FessMimeTypeHelperTest extends UnitFessTestCase {

    private static final String SQL_REM_CONTENT = "rem utldtree.sql\nrem\nCREATE TABLE test (id NUMBER);\n";

    private FessMimeTypeHelper mimeTypeHelper;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        mimeTypeHelper = new FessMimeTypeHelper();
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    @Test
    public void test_init_emptyConfig() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentMimetypeExtensionOverrides() {
                return "";
            }
        });

        mimeTypeHelper.init();

        // With empty config, no override is applied; content-based detection runs
        try (InputStream is = new ByteArrayInputStream(SQL_REM_CONTENT.getBytes(StandardCharsets.UTF_8))) {
            final String contentType = mimeTypeHelper.getContentType(is, "test.sql");
            // Without override, Tika detects based on content+filename
            assertNotNull(contentType);
        }
    }

    @Test
    public void test_init_nullConfig() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentMimetypeExtensionOverrides() {
                return null;
            }
        });

        mimeTypeHelper.init();

        // Null config means no override
        try (InputStream is = new ByteArrayInputStream("Hello world".getBytes(StandardCharsets.UTF_8))) {
            assertEquals("text/plain", mimeTypeHelper.getContentType(is, "test.txt"));
        }
    }

    @Test
    public void test_init_singleMapping() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentMimetypeExtensionOverrides() {
                return ".sql=text/x-sql";
            }
        });

        mimeTypeHelper.init();

        try (InputStream is = new ByteArrayInputStream(SQL_REM_CONTENT.getBytes(StandardCharsets.UTF_8))) {
            assertEquals("text/x-sql", mimeTypeHelper.getContentType(is, "test.sql"));
        }
    }

    @Test
    public void test_init_multipleMappings() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentMimetypeExtensionOverrides() {
                return ".sql=text/x-sql\n.plsql=text/x-plsql\n.pls=text/x-plsql";
            }
        });

        mimeTypeHelper.init();

        try (InputStream is = new ByteArrayInputStream(SQL_REM_CONTENT.getBytes(StandardCharsets.UTF_8))) {
            assertEquals("text/x-sql", mimeTypeHelper.getContentType(is, "test.sql"));
        }
        try (InputStream is = new ByteArrayInputStream(SQL_REM_CONTENT.getBytes(StandardCharsets.UTF_8))) {
            assertEquals("text/x-plsql", mimeTypeHelper.getContentType(is, "package.plsql"));
        }
        try (InputStream is = new ByteArrayInputStream(SQL_REM_CONTENT.getBytes(StandardCharsets.UTF_8))) {
            assertEquals("text/x-plsql", mimeTypeHelper.getContentType(is, "body.pls"));
        }
    }

    @Test
    public void test_init_withBlankLines() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentMimetypeExtensionOverrides() {
                return ".sql=text/x-sql\n\n\n.plsql=text/x-plsql\n";
            }
        });

        mimeTypeHelper.init();

        try (InputStream is = new ByteArrayInputStream(SQL_REM_CONTENT.getBytes(StandardCharsets.UTF_8))) {
            assertEquals("text/x-sql", mimeTypeHelper.getContentType(is, "test.sql"));
        }
        try (InputStream is = new ByteArrayInputStream(SQL_REM_CONTENT.getBytes(StandardCharsets.UTF_8))) {
            assertEquals("text/x-plsql", mimeTypeHelper.getContentType(is, "test.plsql"));
        }
    }

    @Test
    public void test_init_malformedEntry_noEquals() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentMimetypeExtensionOverrides() {
                return ".sql=text/x-sql\ninvalid_entry\n.pls=text/x-plsql";
            }
        });

        mimeTypeHelper.init();

        // Valid entries should still be loaded
        try (InputStream is = new ByteArrayInputStream(SQL_REM_CONTENT.getBytes(StandardCharsets.UTF_8))) {
            assertEquals("text/x-sql", mimeTypeHelper.getContentType(is, "test.sql"));
        }
        try (InputStream is = new ByteArrayInputStream(SQL_REM_CONTENT.getBytes(StandardCharsets.UTF_8))) {
            assertEquals("text/x-plsql", mimeTypeHelper.getContentType(is, "test.pls"));
        }
    }

    @Test
    public void test_init_withWhitespace() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentMimetypeExtensionOverrides() {
                return " .sql = text/x-sql \n .bat = application/x-bat ";
            }
        });

        mimeTypeHelper.init();

        try (InputStream is = new ByteArrayInputStream(SQL_REM_CONTENT.getBytes(StandardCharsets.UTF_8))) {
            assertEquals("text/x-sql", mimeTypeHelper.getContentType(is, "test.sql"));
        }
        try (InputStream is = new ByteArrayInputStream("echo hello".getBytes(StandardCharsets.UTF_8))) {
            assertEquals("application/x-bat", mimeTypeHelper.getContentType(is, "test.bat"));
        }
    }

    @Test
    public void test_getContentType_withOverride_sqlFile() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentMimetypeExtensionOverrides() {
                return ".sql=text/x-sql";
            }
        });

        mimeTypeHelper.init();

        // Content that would normally be detected as application/x-bat
        try (InputStream is = new ByteArrayInputStream(SQL_REM_CONTENT.getBytes(StandardCharsets.UTF_8))) {
            assertEquals("text/x-sql", mimeTypeHelper.getContentType(is, "oracle_script.sql"));
        }
    }

    @Test
    public void test_getContentType_withOverride_upperCaseExtension() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentMimetypeExtensionOverrides() {
                return ".sql=text/x-sql";
            }
        });

        mimeTypeHelper.init();

        try (InputStream is = new ByteArrayInputStream(SQL_REM_CONTENT.getBytes(StandardCharsets.UTF_8))) {
            assertEquals("text/x-sql", mimeTypeHelper.getContentType(is, "test.SQL"));
        }
        try (InputStream is = new ByteArrayInputStream(SQL_REM_CONTENT.getBytes(StandardCharsets.UTF_8))) {
            assertEquals("text/x-sql", mimeTypeHelper.getContentType(is, "test.Sql"));
        }
    }

    @Test
    public void test_getContentType_unmappedExtension_fallsBack() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentMimetypeExtensionOverrides() {
                return ".sql=text/x-sql";
            }
        });

        mimeTypeHelper.init();

        // .txt is not in the override map, should use normal detection
        try (InputStream is = new ByteArrayInputStream("Hello, world!".getBytes(StandardCharsets.UTF_8))) {
            assertEquals("text/plain", mimeTypeHelper.getContentType(is, "test.txt"));
        }
    }

    @Test
    public void test_getContentType_noFilename_overrideNotApplied() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentMimetypeExtensionOverrides() {
                return ".sql=text/x-sql";
            }
        });

        mimeTypeHelper.init();

        // Without a filename, the override cannot be applied
        try (InputStream is = new ByteArrayInputStream(SQL_REM_CONTENT.getBytes(StandardCharsets.UTF_8))) {
            final String contentType = mimeTypeHelper.getContentType(is, "");
            // Falls back to content-based detection (application/x-bat)
            assertEquals("application/x-bat", contentType);
        }
    }

    @Test
    public void test_getContentType_pathFilename_overrideApplied() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentMimetypeExtensionOverrides() {
                return ".sql=text/x-sql";
            }
        });

        mimeTypeHelper.init();

        // Filename with path should still have extension extracted correctly
        try (InputStream is = new ByteArrayInputStream(SQL_REM_CONTENT.getBytes(StandardCharsets.UTF_8))) {
            assertEquals("text/x-sql", mimeTypeHelper.getContentType(is, "/path/to/script.sql"));
        }
    }

    @Test
    public void test_getContentType_nullStream_overrideApplied() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentMimetypeExtensionOverrides() {
                return ".sql=text/x-sql";
            }
        });

        mimeTypeHelper.init();

        // Override works even without an input stream
        assertEquals("text/x-sql", mimeTypeHelper.getContentType(null, "test.sql"));
    }

    @Test
    public void test_getContentType_overridePriority() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentMimetypeExtensionOverrides() {
                return ".txt=application/custom-text";
            }
        });

        mimeTypeHelper.init();

        // Override takes priority over both content and filename detection
        try (InputStream is = new ByteArrayInputStream("plain text content".getBytes(StandardCharsets.UTF_8))) {
            assertEquals("application/custom-text", mimeTypeHelper.getContentType(is, "readme.txt"));
        }
    }
}
