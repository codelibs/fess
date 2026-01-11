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
package org.codelibs.fess.crawler.transformer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.groovy.util.Maps;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.serializer.DataSerializer;
import org.codelibs.fess.crawler.util.FieldConfigs;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.ProtocolHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FessFileTransformerTest extends UnitFessTestCase {

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new DataSerializer(), "dataSerializer");
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

            @Override
            public String getCrawlerDocumentFileNameEncoding() {
                return "";
            }

            @Override
            public String getCrawlerDocumentSiteEncoding() {
                return "";
            }

            @Override
            public boolean isCrawlerDocumentUseSiteEncodingOnEnglish() {
                return false;
            }

            @Override
            public String getCrawlerDocumentUnknownHostname() {
                return "unknown";
            }

            @Override
            public Integer getCrawlerDocumentMaxSiteLengthAsInteger() {
                return -1;
            }
        });
        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();
        ComponentUtil.register(protocolHelper, "protocolHelper");
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    private String encodeUrl(final String url) {
        try {
            return URLEncoder.encode(url, Constants.UTF_8);
        } catch (final UnsupportedEncodingException e) {
            throw new FessSystemException("Unsupported encoding.", e);
        }
    }

    @Test
    public void test_decodeUrl_ok() throws Exception {
        String url, exp;
        final FessFileTransformer transformer = createInstance();

        url = "";
        exp = "";
        assertEquals(exp, transformer.decodeUrlAsName(url, true));

        url = "http://example.com/";
        exp = "http://example.com/";
        assertEquals(exp, transformer.decodeUrlAsName(url, false));

        url = "http://example.com/index.html";
        exp = "http://example.com/index.html";
        assertEquals(exp, transformer.decodeUrlAsName(url, false));

        url = "http://example.com/" + encodeUrl("テスト ") + ".html";
        exp = "http://example.com/テスト .html";
        assertEquals(exp, transformer.decodeUrlAsName(url, false));

        url = "file://C++.doc";
        exp = "file://C++.doc";
        assertEquals(exp, transformer.decodeUrlAsName(url, true));

        url = "file://C .doc";
        exp = "file://C .doc";
        assertEquals(exp, transformer.decodeUrlAsName(url, true));

        url = "http://example.com/foo/" + encodeUrl("#") + "/@@bar/index.html#fragment?foo=bar";
        exp = "http://example.com/foo/#/@@bar/index.html#fragment?foo=bar";
        assertEquals(exp, transformer.decodeUrlAsName(url, false));
    }

    @Test
    public void test_getFileName_ok() throws Exception {
        String url, exp;
        final FessFileTransformer transformer = createInstance();

        url = "https://example.com/" + encodeUrl("#") + "/@@bar/index.html#fragment?foo=bar";
        exp = "index.html";
        assertEquals(exp, transformer.getFileName(url, Constants.UTF_8));

        url = "http://example.com/" + encodeUrl("#") + "/@@folder/test.txt";
        exp = "test.txt";
        assertEquals(exp, transformer.getFileName(url, Constants.UTF_8));

        url = "http://example.com/test%20+%2B.txt";
        exp = "test  +.txt";
        assertEquals(exp, transformer.getFileName(url, Constants.UTF_8));

        url = "file://example.com/test%20+%2B.txt";
        exp = "test ++.txt";
        assertEquals(exp, transformer.getFileName(url, Constants.UTF_8));

        url = "file://example.com/test#.txt";
        exp = "test#.txt";
        assertEquals(exp, transformer.getFileName(url, Constants.UTF_8));

        url = "smb://example.com/test?.txt";
        exp = "test?.txt";
        assertEquals(exp, transformer.getFileName(url, Constants.UTF_8));

        url = "https://example.com/test%E3%81%82.txt";
        exp = "testあ.txt";
        assertEquals(exp, transformer.getFileName(url, Constants.UTF_8));

        url = "file://example.com/test%E3%81%82.txt";
        exp = "testあ.txt";
        assertEquals(exp, transformer.getFileName(url, Constants.UTF_8));

        url = "storage://example.com/test%E3%81%82.txt";
        exp = "testあ.txt";
        assertEquals(exp, transformer.getFileName(url, Constants.UTF_8));

        url = "smb://example.com/test%E3%81%82.txt";
        exp = "test%E3%81%82.txt";
        assertEquals(exp, transformer.getFileName(url, Constants.UTF_8));

        url = "smb1://example.com/test%E3%81%82.txt";
        exp = "test%E3%81%82.txt";
        assertEquals(exp, transformer.getFileName(url, Constants.UTF_8));

        url = "ftp://example.com/test%E3%81%82.txt";
        exp = "test%E3%81%82.txt";
        assertEquals(exp, transformer.getFileName(url, Constants.UTF_8));

        url = "s3://bucket/path/test%E3%81%82.txt";
        exp = "test%E3%81%82.txt";
        assertEquals(exp, transformer.getFileName(url, Constants.UTF_8));

        url = "gcs://bucket/path/test%E3%81%82.txt";
        exp = "test%E3%81%82.txt";
        assertEquals(exp, transformer.getFileName(url, Constants.UTF_8));

        url = "s3://my-bucket/folder/document.pdf";
        exp = "document.pdf";
        assertEquals(exp, transformer.getFileName(url, Constants.UTF_8));

        url = "gcs://my-bucket/folder/document.pdf";
        exp = "document.pdf";
        assertEquals(exp, transformer.getFileName(url, Constants.UTF_8));
    }

    @Test
    public void test_decodeUrl_null() throws Exception {
        final FessFileTransformer transformer = createInstance();
        assertNull(transformer.decodeUrlAsName(null, true));
    }

    @Test
    public void test_getHost_ok() {
        String url, exp;
        final FessFileTransformer transformer = createInstance();

        url = "";
        exp = "";
        assertEquals(exp, transformer.getHostOnFile(url));

        url = "http://server/home/user";
        exp = "server";
        assertEquals(exp, transformer.getHostOnFile(url));

        url = "file:/home/user";
        exp = "localhost";
        assertEquals(exp, transformer.getHostOnFile(url));

        url = "file:/c:/home/user";
        exp = "localhost";
        assertEquals(exp, transformer.getHostOnFile(url));

        url = "file:////server/home/user";
        exp = "server";
        assertEquals(exp, transformer.getHostOnFile(url));

        url = "file:/" + encodeUrl("ホーム") + "/user";
        exp = "localhost";
        assertEquals(exp, transformer.getHostOnFile(url));

        url = "file:/c:/" + encodeUrl("ホーム") + "/user";
        exp = "localhost";
        assertEquals(exp, transformer.getHostOnFile(url));

        url = "file:////" + encodeUrl("サーバー") + "/home/user";
        exp = "サーバー";
        assertEquals(exp, transformer.getHostOnFile(url));

        url = "s3://my-bucket/path/to/file";
        exp = "my-bucket";
        assertEquals(exp, transformer.getHostOnFile(url));

        url = "gcs://my-bucket/path/to/file";
        exp = "my-bucket";
        assertEquals(exp, transformer.getHostOnFile(url));

        url = "s3://bucket-with-dashes/file";
        exp = "bucket-with-dashes";
        assertEquals(exp, transformer.getHostOnFile(url));

        url = "gcs://bucket_with_underscores/file";
        exp = "bucket_with_underscores";
        assertEquals(exp, transformer.getHostOnFile(url));

    }

    @Test
    public void test_getHost_unexpected() {
        String url, exp;
        final FessFileTransformer transformer = createInstance();

        url = null;
        exp = "";
        assertEquals(exp, transformer.getHostOnFile(url));

        url = "example:";
        exp = "unknown";
        assertEquals(exp, transformer.getHostOnFile(url));

        url = "file:";
        exp = "localhost";
        assertEquals(exp, transformer.getHostOnFile(url));

        url = "file://";
        exp = "localhost";
        assertEquals(exp, transformer.getHostOnFile(url));

        url = "file:///";
        exp = "localhost";
        assertEquals(exp, transformer.getHostOnFile(url));

        url = "file://///";
        exp = "localhost";
        assertEquals(exp, transformer.getHostOnFile(url));

        url = "file://///example";
        exp = "localhost";
        assertEquals(exp, transformer.getHostOnFile(url));

        url = "file:/c:";
        exp = "localhost";
        assertEquals(exp, transformer.getHostOnFile(url));

    }

    @Test
    public void test_getSite_ok() {
        String url, exp;
        final FessFileTransformer transformer = createInstance();

        url = "";
        exp = "";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

        url = "http://example.com/";
        exp = "example.com/";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

        url = "http://example.com/index.html";
        exp = "example.com/index.html";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

        url = "file:/home/user";
        exp = "/home/user";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

        url = "file:/c:/home/user";
        exp = "c:\\home\\user";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

        url = "file:/c:/";
        exp = "c:\\";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

        url = "file:////server/user";
        exp = "\\\\server\\user";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

        url = "smb://server/user";
        exp = "\\\\server\\user";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

        url = "smb1://server/user";
        exp = "\\\\server\\user";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

        url = "ftp://example.com/file";
        exp = "example.com/file";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

        url = "s3://my-bucket/path/to/file";
        exp = "my-bucket/path/to/file";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

        url = "gcs://my-bucket/path/to/file";
        exp = "my-bucket/path/to/file";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

        url = "s3://bucket/";
        exp = "bucket/";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

        url = "gcs://bucket/";
        exp = "bucket/";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));
    }

    @Test
    public void test_getSite_ok_len10() {
        String url, exp;
        final FessFileTransformer transformer = new FessFileTransformer() {
            @Override
            public int getMaxSiteLength() {
                return 10;
            }
        };
        transformer.init();

        url = "file:/home/user/foo";
        exp = "/home/u...";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

    }

    @Test
    public void test_getSite_unexpected() {
        String url, exp;
        final FessFileTransformer transformer = createInstance();

        url = "file:";
        exp = "";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

        url = "file";
        exp = "file";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

        url = "file:/";
        exp = "/";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

        url = "file:/c:";
        exp = "c:";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

        url = "file://";
        exp = "//";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

        url = "file:///";
        exp = "///";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));

        url = "file://///";
        exp = "\\\\\\";
        assertEquals(exp, transformer.getSiteOnFile(url, "UTF-8"));
    }

    @Test
    public void test_processFieldConfigs() {
        final FessFileTransformer transformer = createInstance();
        final Map<String, String> params = Maps.of("foo", "cache", "bar", "overwrite", "baz", "cache|overwrite");
        FieldConfigs fieldConfigs = new FieldConfigs(params);
        final Map<String, Object> dataMap = Map.of(//
                "foo", new String[] { "aaa", "bbb" }, //
                "bar", new String[] { "ccc", "ddd" }, //
                "baz", new String[] { "eee", "fff" });
        final Map<String, Object> resultMap = transformer.processFieldConfigs(dataMap, fieldConfigs);
        assertEquals("aaa", ((String[]) resultMap.get("foo"))[0]);
        assertEquals("bbb", ((String[]) resultMap.get("foo"))[1]);
        assertEquals("ddd", resultMap.get("bar"));
        assertEquals("fff", resultMap.get("baz"));
    }

    private FessFileTransformer createInstance() {
        final FessFileTransformer transformer = new FessFileTransformer();
        transformer.init();
        return transformer;
    }

}
