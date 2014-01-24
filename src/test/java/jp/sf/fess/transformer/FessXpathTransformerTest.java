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

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.cyberneko.html.parsers.DOMParser;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.container.ComponentNotFoundRuntimeException;
import org.seasar.robot.client.fs.ChildUrlsException;
import org.seasar.robot.entity.ResponseData;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class FessXpathTransformerTest extends S2TestCase {
    public FessXpathTransformer fessXpathTransformer;

    @Override
    protected String getRootDicon() throws Throwable {
        return "s2robot_transformer.dicon";
    }

    public void test_pruneNode() throws Exception {
        final String data = "<html><body><br/><script>hoge</script><noscript>fuga</noscript></body></html>";
        final Document document = getDocument(data);

        final FessXpathTransformer transformer = new FessXpathTransformer();

        final Node pruneNode = transformer.pruneNode(document.cloneNode(true));
        assertEquals(getXmlString(document), getXmlString(pruneNode));
    }

    public void test_pruneNode_removeNoScript() throws Exception {
        final String data = "<html><body><br/><script>hoge</script><noscript>fuga</noscript></body></html>";
        final Document document = getDocument(data);

        final FessXpathTransformer transformer = new FessXpathTransformer();
        transformer.prunedTagList.add("noscript");

        final Node pruneNode = transformer.pruneNode(document.cloneNode(true));
        final String docString = getXmlString(document);
        final String pnString = getXmlString(pruneNode);
        assertTrue(docString.contains("<SCRIPT>"));
        assertTrue(docString.contains("hoge"));
        assertTrue(docString.contains("<NOSCRIPT>"));
        assertTrue(docString.contains("fuga"));
        assertTrue(pnString.contains("<SCRIPT>"));
        assertTrue(pnString.contains("hoge"));
        assertFalse(pnString.contains("<NOSCRIPT>"));
        assertFalse(pnString.contains("fuga"));
    }

    public void test_pruneNode_removeScriptAndNoscript() throws Exception {
        final String data = "<html><body><br/><script>hoge</script><noscript>fuga</noscript></body></html>";
        final Document document = getDocument(data);

        final FessXpathTransformer transformer = new FessXpathTransformer();
        transformer.prunedTagList.add("script");
        transformer.prunedTagList.add("noscript");

        final Node pruneNode = transformer.pruneNode(document.cloneNode(true));
        final String docString = getXmlString(document);
        final String pnString = getXmlString(pruneNode);
        assertTrue(docString.contains("<SCRIPT>"));
        assertTrue(docString.contains("hoge"));
        assertTrue(docString.contains("<NOSCRIPT>"));
        assertTrue(docString.contains("fuga"));
        assertFalse(pnString.contains("<SCRIPT>"));
        assertFalse(pnString.contains("hoge"));
        assertFalse(pnString.contains("<NOSCRIPT>"));
        assertFalse(pnString.contains("fuga"));
    }

    private Document getDocument(final String data) throws Exception {
        final DOMParser parser = new DOMParser();
        final ByteArrayInputStream is = new ByteArrayInputStream(
                data.getBytes("UTF-8"));
        parser.parse(new InputSource(is));
        return parser.getDocument();
    }

    private String getXmlString(final Node node) throws Exception {
        final TransformerFactory tf = TransformerFactory.newInstance();
        final Transformer transformer = tf.newTransformer();
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

        value = "hoge.html";
        assertTrue(fessXpathTransformer.isValidPath(value));

        value = "./hoge.html";
        assertTrue(fessXpathTransformer.isValidPath(value));

        value = "/hoge.html";
        assertTrue(fessXpathTransformer.isValidPath(value));

        value = "http://www.seasar.org/hoge.html";
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
        List<String> urlList = new ArrayList<String>();

        urlList = fessXpathTransformer.convertChildUrlList(urlList);
        assertEquals(0, urlList.size());

        urlList.clear();
        urlList.add("http://www.example.com");
        urlList = fessXpathTransformer.convertChildUrlList(urlList);
        assertEquals(1, urlList.size());
        assertEquals("http://www.example.com", urlList.get(0));

        urlList.clear();
        urlList.add("http://www.example.com");
        urlList.add("http://www.test.com");
        urlList = fessXpathTransformer.convertChildUrlList(urlList);
        assertEquals(2, urlList.size());
        assertEquals("http://www.example.com", urlList.get(0));
        assertEquals("http://www.test.com", urlList.get(1));

        urlList.clear();
        urlList.add("feed://www.example.com");
        urlList.add("http://www.test.com");
        urlList = fessXpathTransformer.convertChildUrlList(urlList);
        assertEquals(2, urlList.size());
        assertEquals("http://www.example.com", urlList.get(0));
        assertEquals("http://www.test.com", urlList.get(1));

    }

    public void test_normalizeContent() {
        assertEquals("", fessXpathTransformer.normalizeContent(""));
        assertEquals(" ", fessXpathTransformer.normalizeContent(" "));
        assertEquals(" ", fessXpathTransformer.normalizeContent("  "));
        assertEquals(" ", fessXpathTransformer.normalizeContent("\t"));
        assertEquals(" ", fessXpathTransformer.normalizeContent("\t\t"));
        assertEquals(" ", fessXpathTransformer.normalizeContent("\t \t"));
    }

    public void test_removeCommentTag() {
        assertEquals("", fessXpathTransformer.removeCommentTag(""));
        assertEquals(" ", fessXpathTransformer.removeCommentTag("<!-- - -->"));
        assertEquals("abc", fessXpathTransformer.removeCommentTag("abc"));
        assertEquals("abc ",
                fessXpathTransformer.removeCommentTag("abc<!-- hoge -->"));
        assertEquals("abc 123",
                fessXpathTransformer.removeCommentTag("abc<!-- ho\nge -->123"));
        assertEquals("abc 123",
                fessXpathTransformer.removeCommentTag("abc<!--\n hoge -->123"));
        assertEquals("abc 123",
                fessXpathTransformer.removeCommentTag("abc<!-- hoge -->123"));
        assertEquals("abc 123 ",
                fessXpathTransformer
                        .removeCommentTag("abc<!-- hoge1 -->123<!-- hoge2 -->"));
        assertEquals(
                "abc 123 xyz",
                fessXpathTransformer
                        .removeCommentTag("abc<!-- hoge1 -->123<!-- hoge2 -->xyz"));
        assertEquals("abc ",
                fessXpathTransformer.removeCommentTag("abc<!---->"));
        assertEquals("abc -->",
                fessXpathTransformer.removeCommentTag("abc<!-- hoge-->-->"));
        assertEquals("abc<!-- hoge",
                fessXpathTransformer.removeCommentTag("abc<!-- hoge"));
        assertEquals("abc  -->123",
                fessXpathTransformer
                        .removeCommentTag("abc<!-- <!-- hoge --> -->123"));
    }

    public void test_canonicalXpath() throws Exception {
        final FessXpathTransformer transformer = new FessXpathTransformer();

        final Map<String, Object> dataMap = new HashMap<String, Object>();
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/");

        String data = "<html><body>aaa</body></html>";
        Document document = getDocument(data);
        try {
            transformer.putAdditionalData(dataMap, responseData, document);
            fail();
        } catch (final ComponentNotFoundRuntimeException e) {
            // ignore
        }

        data = "<html><head><link rel=\"canonical\" href=\"http://example.com/\"></head><body>aaa</body></html>";
        document = getDocument(data);
        try {
            transformer.putAdditionalData(dataMap, responseData, document);
            fail();
        } catch (final ComponentNotFoundRuntimeException e) {
            // ignore
        }

        data = "<html><head><link rel=\"canonical\" href=\"http://example.com/hoge\"></head><body>aaa</body></html>";
        document = getDocument(data);
        try {
            transformer.putAdditionalData(dataMap, responseData, document);
            fail();
        } catch (final ChildUrlsException e) {
            final Set<String> childUrlList = e.getChildUrlList();
            assertEquals(1, childUrlList.size());
            assertEquals("http://example.com/hoge", childUrlList.iterator()
                    .next());
        }

        data = "<html><link rel=\"canonical\" href=\"http://example.com/hoge\"><body>aaa</body></html>";
        document = getDocument(data);
        try {
            transformer.putAdditionalData(dataMap, responseData, document);
            fail();
        } catch (final ChildUrlsException e) {
            final Set<String> childUrlList = e.getChildUrlList();
            assertEquals(1, childUrlList.size());
            assertEquals("http://example.com/hoge", childUrlList.iterator()
                    .next());
        }
    }
}
