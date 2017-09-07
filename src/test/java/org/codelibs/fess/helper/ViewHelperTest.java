/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.core.io.FileUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.es.config.exentity.PathMapping;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

public class ViewHelperTest extends UnitFessTestCase {
    public ViewHelper viewHelper;

    public void setUp() throws Exception {
        super.setUp();
        viewHelper = new ViewHelper();
        viewHelper.init();
    }

    public void test_getUrlLink() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            public boolean isAppendQueryParameter() {
                return false;
            }

            public String getIndexFieldUrl() {
                return "url";
            }

            public String getIndexFieldDocId() {
                return "doc_id";
            }
        });

        // http
        assertUrlLink("http://www.codelibs.org/", //
                "http://www.codelibs.org/");
        assertUrlLink("http://www.codelibs.org/あ", //
                "http://www.codelibs.org/あ");
        assertUrlLink("http://www.codelibs.org/%E3%81%82", //
                "http://www.codelibs.org/%E3%81%82");
        assertUrlLink("http://www.codelibs.org/%z", //
                "http://www.codelibs.org/%z");
        assertUrlLink("http://www.codelibs.org/?a=1&b=2", //
                "http://www.codelibs.org/?a=1&b=2");

        // https
        assertUrlLink("https://www.codelibs.org/", //
                "https://www.codelibs.org/");
        assertUrlLink("https://www.codelibs.org/あ", //
                "https://www.codelibs.org/あ");
        assertUrlLink("https://www.codelibs.org/%E3%81%82", //
                "https://www.codelibs.org/%E3%81%82");
        assertUrlLink("https://www.codelibs.org/%z", //
                "https://www.codelibs.org/%z");
        assertUrlLink("https://www.codelibs.org/?a=1&b=2", //
                "https://www.codelibs.org/?a=1&b=2");

        // ftp
        assertUrlLink("ftp://www.codelibs.org/", //
                "ftp://www.codelibs.org/");
        assertUrlLink("ftp://www.codelibs.org/あ", //
                "ftp://www.codelibs.org/あ");
        assertUrlLink("ftp://www.codelibs.org/%E3%81%82", //
                "ftp://www.codelibs.org/%E3%81%82");
        assertUrlLink("ftp://www.codelibs.org/%z", //
                "ftp://www.codelibs.org/%z");

        viewHelper.userAgentHelper = new UserAgentHelper() {
            public UserAgentType getUserAgentType() {
                return UserAgentType.IE;
            }
        };
        File tempFile = File.createTempFile("test", ".properties");
        FileUtil.writeBytes(tempFile.getAbsolutePath(), new byte[0]);
        tempFile.deleteOnExit();
        viewHelper.systemProperties = new DynamicProperties(tempFile);

        // file
        assertUrlLink("file:/home/taro/test.txt", //
                "file://home/taro/test.txt");
        assertUrlLink("file:/home/taro/あ.txt", //
                "file://home/taro/あ.txt");
        assertUrlLink("file:/home/taro/%E3%81%82.txt", //
                "file://home/taro/あ.txt");

        // smb->file
        assertUrlLink("smb:/home/taro/test.txt", //
                "file://home/taro/test.txt");
        assertUrlLink("smb:/home/taro/あ.txt", //
                "file://home/taro/あ.txt");
        assertUrlLink("smb:/home/taro/%E3%81%82.txt", //
                "file://home/taro/%E3%81%82.txt");

        PathMapping pathMapping = new PathMapping();
        pathMapping.setRegex("ftp:");
        pathMapping.setReplacement("file:");
        viewHelper.pathMappingHelper = new PathMappingHelper();
        viewHelper.pathMappingHelper.cachedPathMappingList = new ArrayList<>();
        viewHelper.pathMappingHelper.cachedPathMappingList.add(pathMapping);
        // ftp->file
        assertUrlLink("ftp:/home/taro/test.txt", //
                "file://home/taro/test.txt");
        assertUrlLink("ftp:/home/taro/あ.txt", //
                "file://home/taro/あ.txt");
        assertUrlLink("ftp:/home/taro/%E3%81%82.txt", //
                "file://home/taro/%E3%81%82.txt");

        assertUrlLink(null, "#not-found-docId");
        assertUrlLink("", "#not-found-docId");
        assertUrlLink(" ", "#not-found-docId");
    }

    private void assertUrlLink(String url, String expected) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("doc_id", "docId");
        doc.put("url", url);
        assertEquals(expected, viewHelper.getUrlLink(doc));
    }

    public void test_replaceHighlightQueries() {
        String text;
        String[] queries;

        text = "<a>123<b>456<c>";
        queries = new String[] { "123", "456" };
        assertEquals("<a><strong>123</strong><b><strong>456</strong><c>", viewHelper.replaceHighlightQueries(text, queries));

        text = "<123><456>";
        queries = new String[] { "123", "456" };
        assertEquals("<123><456>", viewHelper.replaceHighlightQueries(text, queries));

        text = "123<aaa 123>456<bbb 456>123";
        queries = new String[] { "123", "456" };
        assertEquals("<strong>123</strong><aaa 123><strong>456</strong><bbb 456><strong>123</strong>",
                viewHelper.replaceHighlightQueries(text, queries));

        text = "abc";
        queries = new String[] { "123" };
        assertEquals("abc", viewHelper.replaceHighlightQueries(text, queries));

        text = "123";
        queries = new String[] { "123" };
        assertEquals("<strong>123</strong>", viewHelper.replaceHighlightQueries(text, queries));

        text = "abc123efg";
        queries = new String[] { "123" };
        assertEquals("abc<strong>123</strong>efg", viewHelper.replaceHighlightQueries(text, queries));

        text = "123";
        queries = new String[] { "123", "456" };
        assertEquals("<strong>123</strong>", viewHelper.replaceHighlightQueries(text, queries));

        text = "123456";
        queries = new String[] { "123", "456" };
        assertEquals("<strong>123</strong><strong>456</strong>", viewHelper.replaceHighlightQueries(text, queries));

        text = "a123b456c";
        queries = new String[] { "123", "456" };
        assertEquals("a<strong>123</strong>b<strong>456</strong>c", viewHelper.replaceHighlightQueries(text, queries));

        text = "abc";
        queries = new String[] { "abc" };
        assertEquals("<strong>abc</strong>", viewHelper.replaceHighlightQueries(text, queries));

        text = "1ABC2";
        queries = new String[] { "abc" };
        assertEquals("1<strong>abc</strong>2", viewHelper.replaceHighlightQueries(text, queries));
    }

    public void test_escapeHighlight() {
        viewHelper = new ViewHelper();
        viewHelper.init();

        String text = "";
        assertEquals("", viewHelper.escapeHighlight(text));

        text = "aaa";
        assertEquals("aaa", viewHelper.escapeHighlight(text));

        text = viewHelper.originalHighlightTagPre + "aaa" + viewHelper.originalHighlightTagPost;
        assertEquals(viewHelper.highlightTagPre + "aaa" + viewHelper.highlightTagPost, viewHelper.escapeHighlight(text));

        text = viewHelper.originalHighlightTagPre + "aaa" + viewHelper.originalHighlightTagPost + "<b>bbb</b>";
        assertEquals(viewHelper.highlightTagPre + "aaa" + viewHelper.highlightTagPost + "&lt;b&gt;bbb&lt;/b&gt;",
                viewHelper.escapeHighlight(text));

    }

    public void test_getSitePath() {
        String urlLink;
        String sitePath;
        final Map<String, Object> docMap = new HashMap<>();

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String fieldName = fessConfig.getResponseFieldUrlLink();

        urlLink = "http://www.google.com";
        sitePath = "www.google.com";
        docMap.put(fieldName, urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));

        urlLink = "https://www.jp.websecurity.symantec.com/";
        sitePath = "www.jp.websecurity.symantec.com/";
        docMap.put(fieldName, urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));

        urlLink = "http://www.qwerty.jp";
        sitePath = "www.qwerty.jp";
        docMap.put(fieldName, urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));

        urlLink = "://www.qwerty.jp";
        sitePath = "www.qwerty.jp";
        docMap.put(fieldName, urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));

        urlLink = "www.google.com";
        sitePath = "www.google.com";
        docMap.put(fieldName, urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));

        urlLink = "smb://123.45.678.91/share1";
        sitePath = "123.45.678.91/share1";
        docMap.put(fieldName, urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));

        urlLink = "file:/home/user/";
        sitePath = "/home/user/";
        docMap.put(fieldName, urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));

        urlLink = "file://home/user/";
        sitePath = "/home/user/";
        docMap.put(fieldName, urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));

        urlLink = "file://c:/home/user/";
        sitePath = "c:/home/user/";
        docMap.put(fieldName, urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));

        urlLink = "file://1.2.3.4/user/";
        sitePath = "1.2.3.4/user/";
        docMap.put(fieldName, urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));
    }
}
