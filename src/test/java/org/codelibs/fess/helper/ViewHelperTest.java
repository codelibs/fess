/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

import java.util.HashMap;
import java.util.Map;

import org.seasar.extension.unit.S2TestCase;

public class ViewHelperTest extends S2TestCase {
    public ViewHelper viewHelper;

    @Override
    protected String getRootDicon() throws Throwable {
        return "org/codelibs/fess/helper/view.dicon";
    }

    public void test_replaceHighlightQueries() {
        String text;
        String[] queries;

        text = "<a>123<b>456<c>";
        queries = new String[] { "123", "456" };
        assertEquals("<a><em>123</em><b><em>456</em><c>",
                viewHelper.replaceHighlightQueries(text, queries));

        text = "<123><456>";
        queries = new String[] { "123", "456" };
        assertEquals("<123><456>",
                viewHelper.replaceHighlightQueries(text, queries));

        text = "123<aaa 123>456<bbb 456>123";
        queries = new String[] { "123", "456" };
        assertEquals("<em>123</em><aaa 123><em>456</em><bbb 456><em>123</em>",
                viewHelper.replaceHighlightQueries(text, queries));

        text = "abc";
        queries = new String[] { "123" };
        assertEquals("abc", viewHelper.replaceHighlightQueries(text, queries));

        text = "123";
        queries = new String[] { "123" };
        assertEquals("<em>123</em>",
                viewHelper.replaceHighlightQueries(text, queries));

        text = "abc123efg";
        queries = new String[] { "123" };
        assertEquals("abc<em>123</em>efg",
                viewHelper.replaceHighlightQueries(text, queries));

        text = "123";
        queries = new String[] { "123", "456" };
        assertEquals("<em>123</em>",
                viewHelper.replaceHighlightQueries(text, queries));

        text = "123456";
        queries = new String[] { "123", "456" };
        assertEquals("<em>123</em><em>456</em>",
                viewHelper.replaceHighlightQueries(text, queries));

        text = "a123b456c";
        queries = new String[] { "123", "456" };
        assertEquals("a<em>123</em>b<em>456</em>c",
                viewHelper.replaceHighlightQueries(text, queries));

        text = "abc";
        queries = new String[] { "abc" };
        assertEquals("<em>abc</em>",
                viewHelper.replaceHighlightQueries(text, queries));

        text = "1ABC2";
        queries = new String[] { "abc" };
        assertEquals("1<em>abc</em>2",
                viewHelper.replaceHighlightQueries(text, queries));
    }

    public void test_escapeHighlight() {
        viewHelper = new ViewHelper();
        viewHelper.useSolrHighlight = true;
        viewHelper.init();

        String text = "";
        assertEquals("", viewHelper.escapeHighlight(text));

        text = "aaa";
        assertEquals("aaa", viewHelper.escapeHighlight(text));

        text = "<em>aaa</em>";
        assertEquals("<em>aaa</em>", viewHelper.escapeHighlight(text));

        text = "<em>aaa</em><b>bbb</b>";
        assertEquals("<em>aaa</em>&lt;b&gt;bbb&lt;/b&gt;",
                viewHelper.escapeHighlight(text));

    }

    public void test_getSitePath() {
        String urlLink;
        String sitePath;
        final Map<String, Object> docMap = new HashMap<>();

        urlLink = "http://www.yahoo.co.jp";
        sitePath = "www.yahoo.co.jp";
        docMap.put("urlLink", urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));

        urlLink = "https://www.jp.websecurity.symantec.com/";
        sitePath = "www.jp.websecurity.symantec.com/";
        docMap.put("urlLink", urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));

        urlLink = "http://www.dfasdfaskdfkakdfadfsd.jp";
        sitePath = "www.dfasdfaskdfkakdfadfsd.jp";
        docMap.put("urlLink", urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));

        urlLink = "www.yahoo.co.jp";
        sitePath = "www.yahoo.co.jp";
        docMap.put("urlLink", urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));

        urlLink = "smb://123.45.678.91/kyoyu1";
        sitePath = "123.45.678.91/kyoyu1";
        docMap.put("urlLink", urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));

        urlLink = "file://home/taro/";
        sitePath = "home/taro/";
        docMap.put("urlLink", urlLink);
        assertEquals(sitePath, viewHelper.getSitePath(docMap));
    }

}
