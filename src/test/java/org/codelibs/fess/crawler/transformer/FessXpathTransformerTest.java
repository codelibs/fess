/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.codelibs.core.misc.ValueHolder;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.cyberneko.html.parsers.DOMParser;
import org.lastaflute.di.core.exception.ComponentNotFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class FessXpathTransformerTest extends UnitFessTestCase {
    public FessXpathTransformer fessXpathTransformer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        fessXpathTransformer = new FessXpathTransformer();
        fessXpathTransformer.init();
        fessXpathTransformer.convertUrlMap.put("feed:", "http:");
    }

    public void test_pruneNode() throws Exception {
        final String data = "<html><body><br/><script>foo</script><noscript>bar</noscript></body></html>";
        final Document document = getDocument(data);

        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentHtmlPrunedTags() {
                return "";
            }
        });
        final FessXpathTransformer transformer = new FessXpathTransformer();
        transformer.init();

        final Node pruneNode = transformer.pruneNode(document.cloneNode(true));
        assertEquals(getXmlString(document), getXmlString(pruneNode));
        ComponentUtil.setFessConfig(null);
    }

    public void test_pruneNode_removeNoScript() throws Exception {
        final String data = "<html><body><br/><script>foo</script><noscript>bar</noscript></body></html>";
        final Document document = getDocument(data);

        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentHtmlPrunedTags() {
                return "noscript";
            }
        });
        final FessXpathTransformer transformer = new FessXpathTransformer();
        transformer.init();

        final Node pruneNode = transformer.pruneNode(document.cloneNode(true));
        final String docString = getXmlString(document);
        final String pnString = getXmlString(pruneNode);
        assertTrue(docString.contains("<SCRIPT>"));
        assertTrue(docString.contains("foo"));
        assertTrue(docString.contains("<NOSCRIPT>"));
        assertTrue(docString.contains("bar"));
        assertTrue(pnString.contains("<SCRIPT>"));
        assertTrue(pnString.contains("foo"));
        assertFalse(pnString.contains("<NOSCRIPT>"));
        assertFalse(pnString.contains("bar"));
        ComponentUtil.setFessConfig(null);
    }

    public void test_pruneNode_removeScriptAndNoscript() throws Exception {
        final String data = "<html><body><br/><script>foo</script><noscript>bar</noscript></body></html>";
        final Document document = getDocument(data);

        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentHtmlPrunedTags() {
                return "script,noscript";
            }
        });
        final FessXpathTransformer transformer = new FessXpathTransformer();
        transformer.init();

        final Node pruneNode = transformer.pruneNode(document.cloneNode(true));
        final String docString = getXmlString(document);
        final String pnString = getXmlString(pruneNode);
        assertTrue(docString.contains("<SCRIPT>"));
        assertTrue(docString.contains("foo"));
        assertTrue(docString.contains("<NOSCRIPT>"));
        assertTrue(docString.contains("bar"));
        assertFalse(pnString.contains("<SCRIPT>"));
        assertFalse(pnString.contains("foo"));
        assertFalse(pnString.contains("<NOSCRIPT>"));
        assertFalse(pnString.contains("bar"));
        ComponentUtil.setFessConfig(null);
    }

    public void test_pruneNode_removeDivId() throws Exception {
        final String data = "<html><body><br/><div>foo</div><div id=\"barid\">bar</div></body></html>";
        final Document document = getDocument(data);

        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentHtmlPrunedTags() {
                return "div#barid";
            }
        });
        final FessXpathTransformer transformer = new FessXpathTransformer();
        transformer.init();

        final Node pruneNode = transformer.pruneNode(document.cloneNode(true));
        final String docString = getXmlString(document);
        final String pnString = getXmlString(pruneNode);
        assertTrue(docString.contains("<DIV>"));
        assertTrue(docString.contains("foo"));
        assertTrue(docString.contains("<DIV id=\"barid\">"));
        assertTrue(docString.contains("bar"));
        assertTrue(pnString.contains("<DIV>"));
        assertTrue(pnString.contains("foo"));
        assertFalse(pnString.contains("<DIV id=\"barid\">"));
        assertFalse(pnString.contains("bar"));
        ComponentUtil.setFessConfig(null);
    }

    public void test_pruneNode_removeDivClass() throws Exception {
        final String data = "<html><body><br/><div>foo</div><div class=\"barcls\">bar</div></body></html>";
        final Document document = getDocument(data);

        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCrawlerDocumentHtmlPrunedTags() {
                return "div.barcls";
            }
        });
        final FessXpathTransformer transformer = new FessXpathTransformer();
        transformer.init();

        final Node pruneNode = transformer.pruneNode(document.cloneNode(true));
        final String docString = getXmlString(document);
        final String pnString = getXmlString(pruneNode);
        assertTrue(docString.contains("<DIV>"));
        assertTrue(docString.contains("foo"));
        assertTrue(docString.contains("<DIV class=\"barcls\">"));
        assertTrue(docString.contains("bar"));
        assertTrue(pnString.contains("<DIV>"));
        assertTrue(pnString.contains("foo"));
        assertFalse(pnString.contains("<DIV class=\"barcls\">"));
        assertFalse(pnString.contains("bar"));
        ComponentUtil.setFessConfig(null);
    }

    public void test_processGoogleOffOn() throws Exception {
        final String data =
                "<html><body>foo1<!--googleoff: index-->foo2<a href=\"index.html\">foo3</a>foo4<!--googleon: index-->foo5</body></html>";
        final Document document = getDocument(data);

        final FessXpathTransformer transformer = new FessXpathTransformer();

        final Node pruneNode = transformer.processGoogleOffOn(document, new ValueHolder<>(true));
        final String output = getXmlString(pruneNode).replaceAll(".*<BODY>", "").replaceAll("</BODY>.*", "");
        assertEquals("foo1<!--googleoff: index--><A href=\"index.html\"></A><!--googleon: index-->foo5", output);
    }

    public void test_processMetaRobots_no() throws Exception {
        final String data = "<html><body>foo</body></html>";
        final Document document = getDocument(data);

        final FessXpathTransformer transformer = new FessXpathTransformer();

        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/");

        transformer.processMetaRobots(responseData, new ResultData(), document);
        assertFalse(responseData.isNoFollow());
    }

    public void test_processMetaRobots_none() throws Exception {
        final String data = "<meta name=\"robots\" content=\"none\" />";
        final Document document = getDocument(data);

        final FessXpathTransformer transformer = new FessXpathTransformer();

        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/");

        try {
            transformer.processMetaRobots(responseData, new ResultData(), document);
            fail();
        } catch (ChildUrlsException e) {
            assertTrue(e.getChildUrlList().isEmpty());
        } catch (Exception e) {
            fail();
        }
    }

    public void test_processMetaRobots_noindexnofollow() throws Exception {
        final String data = "<meta name=\"ROBOTS\" content=\"NOINDEX,NOFOLLOW\" />";
        final Document document = getDocument(data);

        final FessXpathTransformer transformer = new FessXpathTransformer();

        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/");

        try {
            transformer.processMetaRobots(responseData, new ResultData(), document);
            fail();
        } catch (ChildUrlsException e) {
            assertTrue(e.getChildUrlList().isEmpty());
        } catch (Exception e) {
            fail();
        }
    }

    public void test_processMetaRobots_noindex() throws Exception {
        final String data = "<meta name=\"robots\" content=\"noindex\" /><a href=\"index.html\">aaa</a>";
        final Document document = getDocument(data);

        final FessXpathTransformer transformer = new FessXpathTransformer();

        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/");
        responseData.setResponseBody(data.getBytes());

        try {
            transformer.processMetaRobots(responseData, new ResultData(), document);
            fail();
        } catch (ChildUrlsException e) {
            assertTrue(e.getChildUrlList().isEmpty());
        } catch (Exception e) {
            fail();
        }
    }

    public void test_processMetaRobots_nofollow() throws Exception {
        final String data = "<meta name=\"robots\" content=\"nofollow\" />";
        final Document document = getDocument(data);

        final FessXpathTransformer transformer = new FessXpathTransformer();

        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/");

        transformer.processMetaRobots(responseData, new ResultData(), document);
        assertTrue(responseData.isNoFollow());
    }

    private Document getDocument(final String data) throws Exception {
        final DOMParser parser = new DOMParser();
        final ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes("UTF-8"));
        parser.parse(new InputSource(is));
        return parser.getDocument();
    }

    private String getXmlString(final Node node) throws Exception {
        final TransformerFactory tf = TransformerFactory.newInstance();
        final javax.xml.transform.Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        //        transformer.setOutputProperty(OutputKeys.METHOD, "xml");

        final StringWriter writer = new StringWriter();
        final StreamResult result = new StreamResult(writer);

        final DOMSource source = new DOMSource(node);
        transformer.transform(source, result);

        return writer.toString();

    }

    public void test_isValidPath_valid() {
        String value;

        value = "foo.html";
        assertTrue(fessXpathTransformer.isValidPath(value));

        value = "./foo.html";
        assertTrue(fessXpathTransformer.isValidPath(value));

        value = "/foo.html";
        assertTrue(fessXpathTransformer.isValidPath(value));

        value = "http://www.seasar.org/foo.html";
        assertTrue(fessXpathTransformer.isValidPath(value));

        value = "a javascript:...";
        assertTrue(fessXpathTransformer.isValidPath(value));

    }

    public void test_isValidPath_invalid() {
        String value;

        value = "javascript:...";
        assertFalse(fessXpathTransformer.isValidPath(value));

        value = "mailto:...";
        assertFalse(fessXpathTransformer.isValidPath(value));

        value = "irc:...";
        assertFalse(fessXpathTransformer.isValidPath(value));

        value = " javascript:...";
        assertFalse(fessXpathTransformer.isValidPath(value));

        value = " mailto:...";
        assertFalse(fessXpathTransformer.isValidPath(value));

        value = " irc:...";
        assertFalse(fessXpathTransformer.isValidPath(value));

        value = "JAVASCRIPT:...";
        assertFalse(fessXpathTransformer.isValidPath(value));

        value = "MAILTO:...";
        assertFalse(fessXpathTransformer.isValidPath(value));

        value = "IRC:...";
        assertFalse(fessXpathTransformer.isValidPath(value));

        value = "skype:...";
        assertFalse(fessXpathTransformer.isValidPath(value));
    }

    public void test_convertChildUrlList() {
        List<RequestData> urlList = new ArrayList<>();

        urlList = fessXpathTransformer.convertChildUrlList(urlList);
        assertEquals(0, urlList.size());

        urlList.clear();
        urlList.add(RequestDataBuilder.newRequestData().get().url("http://www.example.com").build());
        urlList = fessXpathTransformer.convertChildUrlList(urlList);
        assertEquals(1, urlList.size());
        assertEquals("http://www.example.com", urlList.get(0).getUrl());

        urlList.clear();
        urlList.add(RequestDataBuilder.newRequestData().get().url("http://www.example.com").build());
        urlList.add(RequestDataBuilder.newRequestData().get().url("http://www.test.com").build());
        urlList = fessXpathTransformer.convertChildUrlList(urlList);
        assertEquals(2, urlList.size());
        assertEquals("http://www.example.com", urlList.get(0).getUrl());
        assertEquals("http://www.test.com", urlList.get(1).getUrl());

        urlList.clear();
        urlList.add(RequestDataBuilder.newRequestData().get().url("feed://www.example.com").build());
        urlList.add(RequestDataBuilder.newRequestData().get().url("http://www.test.com").build());
        urlList = fessXpathTransformer.convertChildUrlList(urlList);
        assertEquals(2, urlList.size());
        assertEquals("http://www.example.com", urlList.get(0).getUrl());
        assertEquals("http://www.test.com", urlList.get(1).getUrl());

    }

    public void test_removeCommentTag() {
        assertEquals("", fessXpathTransformer.removeCommentTag(""));
        assertEquals(" ", fessXpathTransformer.removeCommentTag("<!-- - -->"));
        assertEquals("abc", fessXpathTransformer.removeCommentTag("abc"));
        assertEquals("abc ", fessXpathTransformer.removeCommentTag("abc<!-- foo -->"));
        assertEquals("abc 123", fessXpathTransformer.removeCommentTag("abc<!-- fo\no -->123"));
        assertEquals("abc 123", fessXpathTransformer.removeCommentTag("abc<!--\n foo -->123"));
        assertEquals("abc 123", fessXpathTransformer.removeCommentTag("abc<!-- foo -->123"));
        assertEquals("abc 123 ", fessXpathTransformer.removeCommentTag("abc<!-- foo1 -->123<!-- foo2 -->"));
        assertEquals("abc 123 xyz", fessXpathTransformer.removeCommentTag("abc<!-- foo1 -->123<!-- foo2 -->xyz"));
        assertEquals("abc ", fessXpathTransformer.removeCommentTag("abc<!---->"));
        assertEquals("abc -->", fessXpathTransformer.removeCommentTag("abc<!-- foo-->-->"));
        assertEquals("abc<!-- foo", fessXpathTransformer.removeCommentTag("abc<!-- foo"));
        assertEquals("abc  -->123", fessXpathTransformer.removeCommentTag("abc<!-- <!-- foo --> -->123"));
    }

    public void test_canonicalXpath() throws Exception {
        final FessXpathTransformer transformer = new FessXpathTransformer();
        transformer.init();

        final Map<String, Object> dataMap = new HashMap<String, Object>();
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/");

        String data = "<html><body>aaa</body></html>";
        Document document = getDocument(data);
        try {
            transformer.putAdditionalData(dataMap, responseData, document);
            fail();
        } catch (final ComponentNotFoundException e) {
            // ignore
        }

        data = "<html><head><link rel=\"canonical\" href=\"http://example.com/\"></head><body>aaa</body></html>";
        document = getDocument(data);
        try {
            transformer.putAdditionalData(dataMap, responseData, document);
            fail();
        } catch (final ComponentNotFoundException e) {
            // ignore
        }

        data = "<html><head><link rel=\"canonical\" href=\"http://example.com/foo\"></head><body>aaa</body></html>";
        document = getDocument(data);
        try {
            transformer.putAdditionalData(dataMap, responseData, document);
            fail();
        } catch (final ChildUrlsException e) {
            final Set<RequestData> childUrlList = e.getChildUrlList();
            assertEquals(1, childUrlList.size());
            assertEquals("http://example.com/foo", childUrlList.iterator().next().getUrl());
        }

        data = "<html><link rel=\"canonical\" href=\"http://example.com/foo\"><body>aaa</body></html>";
        document = getDocument(data);
        try {
            transformer.putAdditionalData(dataMap, responseData, document);
            fail();
        } catch (final ChildUrlsException e) {
            final Set<RequestData> childUrlList = e.getChildUrlList();
            assertEquals(1, childUrlList.size());
            assertEquals("http://example.com/foo", childUrlList.iterator().next().getUrl());
        }
    }

    public void test_contentXpath() throws Exception {
        final FessXpathTransformer transformer = new FessXpathTransformer();

        final String data = "<html><head><meta name=\"keywords\" content=\"bbb\"></head><body>aaa</body></html>";
        final Document document = getDocument(data);
        String value = transformer.getSingleNodeValue(document, "//BODY", false);
        assertEquals("aaa", value);

        value = transformer.getSingleNodeValue(document, "//META[@name='keywords']/@content", false);
        assertEquals("bbb", value);

        value = transformer.getSingleNodeValue(document, "//META[@name='keywords']/@content|//BODY", false);
        assertEquals("bbb aaa", value);
    }

    public void test_normalizeCanonicalUrl() throws Exception {
        final FessXpathTransformer transformer = new FessXpathTransformer();
        String value;

        value = transformer.normalizeCanonicalUrl("http://hoge.com/", "a");
        assertEquals("http://hoge.com/a", value);

        value = transformer.normalizeCanonicalUrl("http://hoge.com/", "aaa");
        assertEquals("http://hoge.com/aaa", value);

        value = transformer.normalizeCanonicalUrl("http://hoge.com/", "/aaa");
        assertEquals("http://hoge.com/aaa", value);

        value = transformer.normalizeCanonicalUrl("http://hoge.com/bbb", "aaa");
        assertEquals("http://hoge.com/aaa", value);

        value = transformer.normalizeCanonicalUrl("http://hoge.com/bbb/", "aaa");
        assertEquals("http://hoge.com/bbb/aaa", value);

        value = transformer.normalizeCanonicalUrl("http://hoge.com/bbb/", "/aaa");
        assertEquals("http://hoge.com/aaa", value);

        value = transformer.normalizeCanonicalUrl("http://hoge.com/bbb", "/aaa");
        assertEquals("http://hoge.com/aaa", value);
    }
}
