/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.transformer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jp.sf.fess.Constants;
import jp.sf.fess.FessSystemException;

import org.seasar.extension.unit.S2TestCase;

public class FessFileTransformerTest extends S2TestCase {
    private String encodeUrl(final String url) {
        try {
            return URLEncoder.encode(url, Constants.UTF_8);
        } catch (final UnsupportedEncodingException e) {
            throw new FessSystemException("Unsupported encoding.", e);
        }
    }

    public void test_decodeUrl_ok() throws Exception {
        String url, exp;
        final FessFileTransformer transformer = new FessFileTransformer();

        url = "";
        exp = "";
        assertEquals(exp, transformer.decodeUrlAsName(url, true));

        url = "http://hoge.com/";
        exp = "http://hoge.com/";
        assertEquals(exp, transformer.decodeUrlAsName(url, false));

        url = "http://hoge.com/index.html";
        exp = "http://hoge.com/index.html";
        assertEquals(exp, transformer.decodeUrlAsName(url, false));

        url = "http://hoge.com/" + encodeUrl("テスト ") + ".html";
        exp = "http://hoge.com/テスト .html";
        assertEquals(exp, transformer.decodeUrlAsName(url, false));

        url = "file://C++.doc";
        exp = "file://C++.doc";
        assertEquals(exp, transformer.decodeUrlAsName(url, true));

        url = "file://C .doc";
        exp = "file://C .doc";
        assertEquals(exp, transformer.decodeUrlAsName(url, true));
    }

    public void test_decodeUrl_null() throws Exception {
        final FessFileTransformer transformer = new FessFileTransformer();
        assertNull(transformer.decodeUrlAsName(null, true));
    }

    public void test_getHost_ok() {
        String url, exp;
        final FessFileTransformer transformer = new FessFileTransformer();

        url = "";
        exp = "";
        assertEquals(exp, transformer.getHost(url));

        url = "http://server/home/taro";
        exp = "server";
        assertEquals(exp, transformer.getHost(url));

        url = "file:/home/taro";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

        url = "file:/c:/home/taro";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

        url = "file:////server/home/taro";
        exp = "server";
        assertEquals(exp, transformer.getHost(url));

        url = "file:/" + encodeUrl("ホーム") + "/taro";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

        url = "file:/c:/" + encodeUrl("ホーム") + "/taro";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

        url = "file:////" + encodeUrl("サーバー") + "/home/taro";
        exp = "サーバー";
        assertEquals(exp, transformer.getHost(url));

    }

    public void test_getHost_unexpected() {
        String url, exp;
        final FessFileTransformer transformer = new FessFileTransformer();

        url = null;
        exp = "";
        assertEquals(exp, transformer.getHost(url));

        url = "hoge:";
        exp = "unknown";
        assertEquals(exp, transformer.getHost(url));

        url = "file:";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

        url = "file://";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

        url = "file:///";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

        url = "file://///";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

        url = "file://///hoge";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

        url = "file:/c:";
        exp = "localhost";
        assertEquals(exp, transformer.getHost(url));

    }

    public void test_getSite_ok() {
        String url, exp;
        final FessFileTransformer transformer = new FessFileTransformer();

        url = "";
        exp = "";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "http://hoge.com/";
        exp = "hoge.com/";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "http://hoge.com/index.html";
        exp = "hoge.com/index.html";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file:/home/taro";
        exp = "/home/taro";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file:/c:/home/taro";
        exp = "c:\\home\\taro";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file:/c:/";
        exp = "c:\\";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file:////server/taro";
        exp = "\\\\server\\taro";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        transformer.maxSiteLength = 10;

        url = "file:/home/taro/hanako";
        exp = "/home/t...";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

    }

    public void test_getSite_unexpected() {
        String url, exp;
        final FessFileTransformer transformer = new FessFileTransformer();

        url = "file:";
        exp = "";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file";
        exp = "file";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file:/";
        exp = "/";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file:/c:";
        exp = "c:";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file://";
        exp = "//";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file:///";
        exp = "///";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));

        url = "file://///";
        exp = "\\\\\\";
        assertEquals(exp, transformer.getSite(url, "UTF-8"));
    }
}
