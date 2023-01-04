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
package org.codelibs.fess.helper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codelibs.core.io.FileUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.FacetQueryView;
import org.codelibs.fess.es.config.exentity.PathMapping;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;

public class ViewHelperTest extends UnitFessTestCase {
    public ViewHelper viewHelper;

    private UserAgentHelper userAgentHelper;

    private PathMappingHelper pathMappingHelper;

    private File propertiesFile;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        propertiesFile = File.createTempFile("test", ".properties");
        FileUtil.writeBytes(propertiesFile.getAbsolutePath(), new byte[0]);
        propertiesFile.deleteOnExit();
        DynamicProperties systemProps = new DynamicProperties(propertiesFile);
        ComponentUtil.register(systemProps, "systemProperties");
        userAgentHelper = new UserAgentHelper();
        ComponentUtil.register(userAgentHelper, "userAgentHelper");
        pathMappingHelper = new PathMappingHelper();
        pathMappingHelper.init();
        ComponentUtil.register(pathMappingHelper, "pathMappingHelper");
        FileTypeHelper fileTypeHelper = new FileTypeHelper();
        ComponentUtil.register(fileTypeHelper, "fileTypeHelper");
        FacetInfo facetInfo = new FacetInfo();
        ComponentUtil.register(facetInfo, "facetInfo");
        viewHelper = new ViewHelper();
        viewHelper.init();
    }

    @Override
    public void tearDown() throws Exception {
        propertiesFile.delete();
        super.tearDown();
    }

    public void test_facetQueries() {
        final List<FacetQueryView> list = viewHelper.getFacetQueryViewList();
        assertEquals(3, list.size());
        FacetQueryView view1 = list.get(0);
        assertEquals("labels.facet_timestamp_title", view1.getTitle());
        assertEquals(4, view1.getQueryMap().size());
        FacetQueryView view2 = list.get(1);
        assertEquals("labels.facet_contentLength_title", view2.getTitle());
        assertEquals(5, view2.getQueryMap().size());
        FacetQueryView view3 = list.get(2);
        assertEquals("labels.facet_filetype_title", view3.getTitle());
        assertEquals(10, view3.getQueryMap().size());
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

        userAgentHelper = new UserAgentHelper() {
            public UserAgentType getUserAgentType() {
                return UserAgentType.IE;
            }
        };
        ComponentUtil.register(userAgentHelper, "userAgentHelper");
        FileUtil.writeBytes(propertiesFile.getAbsolutePath(), new byte[0]);

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
        ComponentUtil.getPathMappingHelper().cachedPathMappingList.add(pathMapping);
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

        text = "abc on exy";
        queries = new String[] { "on" };
        assertEquals("abc <strong>on</strong> exy", viewHelper.replaceHighlightQueries(text, queries));
    }

    public void test_escapeHighlight() {
        ViewHelper viewHelper = new ViewHelper();
        viewHelper.init();
        String text;

        text = "111 222" + viewHelper.originalHighlightTagPre + "aaa" + viewHelper.originalHighlightTagPost;
        assertEquals("111 222" + viewHelper.highlightTagPre + "aaa" + viewHelper.highlightTagPost, viewHelper.escapeHighlight(text));

        text = "111.222" + viewHelper.originalHighlightTagPre + "aaa" + viewHelper.originalHighlightTagPost;
        assertEquals("222" + viewHelper.highlightTagPre + "aaa" + viewHelper.highlightTagPost, viewHelper.escapeHighlight(text));

        text = "111\n222" + viewHelper.originalHighlightTagPre + "aaa" + viewHelper.originalHighlightTagPost;
        assertEquals("222" + viewHelper.highlightTagPre + "aaa" + viewHelper.highlightTagPost, viewHelper.escapeHighlight(text));

        text = "あああ。いいい" + viewHelper.originalHighlightTagPre + "aaa" + viewHelper.originalHighlightTagPost;
        assertEquals("いいい" + viewHelper.highlightTagPre + "aaa" + viewHelper.highlightTagPost, viewHelper.escapeHighlight(text));

        text = "";
        assertEquals("", viewHelper.escapeHighlight(text));

        text = "aaa";
        assertEquals("aaa", viewHelper.escapeHighlight(text));

        text = viewHelper.originalHighlightTagPre + "aaa" + viewHelper.originalHighlightTagPost;
        assertEquals(viewHelper.highlightTagPre + "aaa" + viewHelper.highlightTagPost, viewHelper.escapeHighlight(text));

        text = viewHelper.originalHighlightTagPre + "aaa" + viewHelper.originalHighlightTagPost + "<b>bbb</b>";
        assertEquals(viewHelper.highlightTagPre + "aaa" + viewHelper.highlightTagPost + "&lt;b&gt;bbb&lt;/b&gt;",
                viewHelper.escapeHighlight(text));

        text = "111" + viewHelper.originalHighlightTagPre + "aaa" + viewHelper.originalHighlightTagPost;
        assertEquals("111" + viewHelper.highlightTagPre + "aaa" + viewHelper.highlightTagPost, viewHelper.escapeHighlight(text));
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

    public void test_getContentTitle() {
        final Set<String> querySet = new HashSet<>();
        ViewHelper viewHelper = new ViewHelper() {
            @Override
            protected OptionalThing<Set<String>> getQuerySet() {
                return OptionalThing.of(querySet);
            }
        };
        viewHelper.init();

        querySet.add("aaa");

        final Map<String, Object> document = new HashMap<>();
        document.put("title", "");
        assertEquals("", viewHelper.getContentTitle(document));

        document.put("title", "111");
        assertEquals("111", viewHelper.getContentTitle(document));

        document.put("title", "aaa");
        assertEquals("<strong>aaa</strong>", viewHelper.getContentTitle(document));

        document.put("title", "AAA");
        assertEquals("<strong>AAA</strong>", viewHelper.getContentTitle(document));

        document.put("title", "111AaA222bbb");
        assertEquals("111<strong>AaA</strong>222bbb", viewHelper.getContentTitle(document));

        document.put("title", "aaaAAA");
        assertEquals("<strong>aaa</strong><strong>AAA</strong>", viewHelper.getContentTitle(document));

        querySet.add("BBB");

        document.put("title", "111AaA222bbb");
        assertEquals("111<strong>AaA</strong>222<strong>bbb</strong>", viewHelper.getContentTitle(document));

        querySet.add("on");
        document.put("title", "on 111 strong on aaaa");
        assertEquals("<strong>on</strong> 111 str<strong>on</strong>g <strong>on</strong> <strong>aaa</strong>a",
                viewHelper.getContentTitle(document));

        querySet.add("$");
        document.put("title", "$test");
        assertEquals("<strong>$</strong>test", viewHelper.getContentTitle(document));
    }
}
