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
package org.codelibs.fess.crawler.transformer;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.ClassUtil;
import org.codelibs.core.lang.FieldUtil;
import org.codelibs.core.misc.ValueHolder;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.es.config.exentity.WebConfig;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.DocumentHelper;
import org.codelibs.fess.helper.FileTypeHelper;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.LabelTypeHelper.LabelTypePattern;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.MemoryUtil;
import org.codelibs.nekohtml.parsers.DOMParser;
import org.lastaflute.di.core.exception.ComponentNotFoundException;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class FessXpathTransformerTest extends UnitFessTestCase {
    private static final Logger logger = LogManager.getLogger(FessXpathTransformerTest.class);

    public void test_transform() throws Exception {
        String data = "<html><head><title>Test</title></head><body><h1>Header1</h1><p>This is a pen.</p></body></html>";

        final FessXpathTransformer fessXpathTransformer = new FessXpathTransformer();
        fessXpathTransformer.init();
        SingletonLaContainerFactory.getContainer().register(CrawlingInfoHelper.class, "crawlingInfoHelper");
        SingletonLaContainerFactory.getContainer().register(PathMappingHelper.class, "pathMappingHelper");
        SingletonLaContainerFactory.getContainer().register(CrawlingConfigHelper.class, "crawlingConfigHelper");
        SingletonLaContainerFactory.getContainer().register(SystemHelper.class, "systemHelper");
        SingletonLaContainerFactory.getContainer().register(FileTypeHelper.class, "fileTypeHelper");
        SingletonLaContainerFactory.getContainer().register(DocumentHelper.class, "documentHelper");
        SingletonLaContainerFactory.getContainer().register(LabelTypeHelper.class, "labelTypeHelper");

        WebConfig webConfig = new WebConfig();
        ComponentUtil.getCrawlingConfigHelper().store("test", webConfig);
        setValueToObject(ComponentUtil.getLabelTypeHelper(), "labelTypePatternList", new ArrayList<LabelTypePattern>());

        System.gc();
        Thread.sleep(1000L);
        long current = MemoryUtil.getUsedMemory();
        for (int i = 0; i < 10000; i++) {
            if (i % 1000 == 0) {
                logger.info("count:" + i + ", " + MemoryUtil.getMemoryUsageLog());
            }
            ResponseData responseData = new ResponseData();
            responseData.setCharSet("UTF-8");
            responseData.setContentLength(data.length());
            responseData.setExecutionTime(1000L);
            responseData.setHttpStatusCode(200);
            responseData.setLastModified(new Date());
            responseData.setMethod("GET");
            responseData.setMimeType("text/html");
            responseData.setParentUrl("http://fess.codelibs.org/");
            responseData.setResponseBody(data.getBytes());
            responseData.setSessionId("test-1");
            responseData.setStatus(0);
            responseData.setUrl("http://fess.codelibs.org/test.html");
            /*ResultData resultData =*/fessXpathTransformer.transform(responseData);
            // System.out.println(resultData.toString());
        }

        long margin = 5000000L;
        System.gc();
        for (int i = 0; i < 30; i++) {
            if (MemoryUtil.getUsedMemory() < current + margin) {
                break;
            }
            System.gc();
            Thread.sleep(1000L);
        }
        final long usedMemory = MemoryUtil.getUsedMemory();
        assertTrue(usedMemory + " < " + current + " + " + margin + ", " + MemoryUtil.getMemoryUsageLog(), usedMemory < current + margin);
    }

    private void setValueToObject(Object obj, String name, Object value) {
        Field field = ClassUtil.getDeclaredField(obj.getClass(), name);
        field.setAccessible(true);
        FieldUtil.set(field, obj, value);
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

        final Node pruneNode = transformer.pruneNode(document.cloneNode(true), null);
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

        final Node pruneNode = transformer.pruneNode(document.cloneNode(true), null);
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

        final Node pruneNode = transformer.pruneNode(document.cloneNode(true), null);
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

        final Node pruneNode = transformer.pruneNode(document.cloneNode(true), null);
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

        final Node pruneNode = transformer.pruneNode(document.cloneNode(true), null);
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
        final String output = getXmlString(pruneNode).replaceAll(".*<BODY[^>]*>", "").replaceAll("</BODY>.*", "");
        assertEquals("foo1<!--googleoff: index--><A href=\"index.html\"/><!--googleon: index-->foo5", output);
    }

    public void test_processXRobotsTags_no() throws Exception {
        final FessXpathTransformer transformer = new FessXpathTransformer() {
            @Override
            protected Map<String, String> getConfigPrameterMap(final ResponseData responseData, final ConfigName config) {
                return Collections.emptyMap();
            }
        };
        transformer.fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCrawlerIgnoreRobotsTags() {
                return false;
            };
        };

        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/");

        transformer.processXRobotsTag(responseData, new ResultData());
        assertFalse(responseData.isNoFollow());
    }

    public void test_processXRobotsTag_noindexnofollow() throws Exception {
        final FessXpathTransformer transformer = new FessXpathTransformer() {
            protected Map<String, String> getConfigPrameterMap(final ResponseData responseData, final ConfigName config) {
                return Collections.emptyMap();
            }
        };
        transformer.fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCrawlerIgnoreRobotsTags() {
                return false;
            };
        };

        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/");
        responseData.addMetaData("X-Robots-Tag", "noindex,nofollow");

        try {
            transformer.processXRobotsTag(responseData, new ResultData());
            fail();
        } catch (ChildUrlsException e) {
            assertTrue(e.getChildUrlList().isEmpty());
        } catch (Exception e) {
            fail();
        }
    }

    public void test_processXRobotsTag_noindex() throws Exception {
        final String data = "<meta name=\"robots\" content=\"noindex\" /><a href=\"index.html\">aaa</a>";

        final FessXpathTransformer transformer = new FessXpathTransformer() {
            protected Map<String, String> getConfigPrameterMap(final ResponseData responseData, final ConfigName config) {
                return Collections.emptyMap();
            }
        };
        transformer.fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCrawlerIgnoreRobotsTags() {
                return false;
            };
        };

        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/");
        responseData.setResponseBody(data.getBytes());
        responseData.addMetaData("X-Robots-Tag", "noindex");

        try {
            transformer.processXRobotsTag(responseData, new ResultData());
            fail();
        } catch (ChildUrlsException e) {
            assertTrue(e.getChildUrlList().isEmpty());
        } catch (Exception e) {
            fail();
        }
    }

    public void test_processXRobotsTag_nofollow() throws Exception {
        final FessXpathTransformer transformer = new FessXpathTransformer() {
            protected Map<String, String> getConfigPrameterMap(final ResponseData responseData, final ConfigName config) {
                return Collections.emptyMap();
            }
        };
        transformer.fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCrawlerIgnoreRobotsTags() {
                return false;
            };
        };

        final ResponseData responseData = new ResponseData();
        responseData.addMetaData("X-Robots-Tag", "nofollow");

        transformer.processXRobotsTag(responseData, new ResultData());
        assertTrue(responseData.isNoFollow());
    }

    public void test_processMetaRobots_no() throws Exception {
        final String data = "<html><body>foo</body></html>";
        final Document document = getDocument(data);

        final FessXpathTransformer transformer = new FessXpathTransformer() {
            @Override
            protected Map<String, String> getConfigPrameterMap(final ResponseData responseData, final ConfigName config) {
                return Collections.emptyMap();
            }
        };
        transformer.fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCrawlerIgnoreRobotsTags() {
                return false;
            };
        };

        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/");

        transformer.processMetaRobots(responseData, new ResultData(), document);
        assertFalse(responseData.isNoFollow());
    }

    public void test_processMetaRobots_none() throws Exception {
        final String data = "<meta name=\"robots\" content=\"none\" />";
        final Document document = getDocument(data);

        final FessXpathTransformer transformer = new FessXpathTransformer() {
            protected Map<String, String> getConfigPrameterMap(final ResponseData responseData, final ConfigName config) {
                return Collections.emptyMap();
            }
        };
        transformer.fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCrawlerIgnoreRobotsTags() {
                return false;
            };
        };

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

        final FessXpathTransformer transformer = new FessXpathTransformer() {
            protected Map<String, String> getConfigPrameterMap(final ResponseData responseData, final ConfigName config) {
                return Collections.emptyMap();
            }
        };
        transformer.fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCrawlerIgnoreRobotsTags() {
                return false;
            };
        };

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

        final FessXpathTransformer transformer = new FessXpathTransformer() {
            protected Map<String, String> getConfigPrameterMap(final ResponseData responseData, final ConfigName config) {
                return Collections.emptyMap();
            }

            protected PathMappingHelper getPathMappingHelper() {
                return new PathMappingHelper();
            }
        };
        transformer.fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCrawlerIgnoreRobotsTags() {
                return false;
            };
        };

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

        final FessXpathTransformer transformer = new FessXpathTransformer() {
            protected Map<String, String> getConfigPrameterMap(final ResponseData responseData, final ConfigName config) {
                return Collections.emptyMap();
            }
        };
        transformer.fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCrawlerIgnoreRobotsTags() {
                return false;
            };
        };

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
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");

        final StringWriter writer = new StringWriter();
        final StreamResult result = new StreamResult(writer);

        final DOMSource source = new DOMSource(node);
        transformer.transform(source, result);

        return writer.toString();

    }

    public void test_convertChildUrlList() {
        final FessXpathTransformer fessXpathTransformer = new FessXpathTransformer() {
            protected PathMappingHelper getPathMappingHelper() {
                return new PathMappingHelper();
            }
        };
        fessXpathTransformer.init();
        fessXpathTransformer.convertUrlMap.put("feed:", "http:");

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
        final FessXpathTransformer fessXpathTransformer = new FessXpathTransformer();
        fessXpathTransformer.init();
        fessXpathTransformer.convertUrlMap.put("feed:", "http:");

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
        final FessXpathTransformer transformer = new FessXpathTransformer() {
            protected Map<String, String> getConfigPrameterMap(final ResponseData responseData, final ConfigName config) {
                return Collections.emptyMap();
            }
        };
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

    public void test_getSingleNodeValue() throws Exception {
        final FessXpathTransformer transformer = new FessXpathTransformer();

        String data = "<html><body>aaa<style>bbb</style>ccc</body></html>";
        Document document = getDocument(data);
        String value = transformer.getSingleNodeValue(document, "//BODY", node -> node);
        assertEquals("aaa bbb ccc", value);

        data = "<html><body> aaa <p> bbb <b>ccc</b> </p> </body></html>";
        document = getDocument(data);
        value = transformer.getSingleNodeValue(document, "//BODY", node -> node);
        assertEquals("aaa bbb ccc", value);

        data = "<html><body> aaa <p> bbb <aaa>ccc</bbb> </p> </body></html>";
        document = getDocument(data);
        value = transformer.getSingleNodeValue(document, "//BODY", node -> node);
        assertEquals("aaa bbb ccc", value);

        data = "<html><body> aaa <p> bbb <!-- test -->ccc<!-- test --> </p> </body></html>";
        document = getDocument(data);
        value = transformer.getSingleNodeValue(document, "//BODY", node -> node);
        assertEquals("aaa bbb ccc", value);
    }

    public void test_contentXpath() throws Exception {
        final FessXpathTransformer transformer = new FessXpathTransformer();

        final String data = "<html><head><meta name=\"keywords\" content=\"bbb\"></head><body>aaa</body></html>";
        final Document document = getDocument(data);
        String value = transformer.getSingleNodeValue(document, "//BODY", node -> node);
        assertEquals("aaa", value);

        value = transformer.getSingleNodeValue(document, "//META[@name='keywords']/@content", node -> node);
        assertEquals("bbb", value);

        value = transformer.getSingleNodeValue(document, "//META[@name='keywords']/@content|//BODY", node -> node);
        assertEquals("bbb aaa", value);
    }

    public void test_getCanonicalUrl() throws Exception {
        final FessXpathTransformer transformer = new FessXpathTransformer() {
            @Override
            protected Map<String, String> getConfigPrameterMap(final ResponseData responseData, final ConfigName config) {
                return Collections.emptyMap();
            }
        };
        transformer.fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            public String getCrawlerDocumentHtmlCanonicalXpath() {
                return "//LINK[@rel='canonical'][1]/@href";
            };
        };

        final ResponseData responseData = new ResponseData();
        responseData.setSessionId("test");
        responseData.setUrl("http://example.com/");

        String data = "<html><head></head><body>aaa</body></html>";
        Document document = getDocument(data);
        String value = transformer.getCanonicalUrl(responseData, document);
        assertNull(value);

        data = "<html><head><link rel=\"canonical\" href=\"http://example.com/\"></head><body>aaa</body></html>";
        document = getDocument(data);
        value = transformer.getCanonicalUrl(responseData, document);
        assertEquals("http://example.com/", value);

        data = "<html><head><link rel=\"canonical\" href=\"http://example1.com/\"><link rel=\"canonical\" href=\"http://example2.com/\"></head><body>aaa</body></html>";
        document = getDocument(data);
        value = transformer.getCanonicalUrl(responseData, document);
        assertEquals("http://example1.com/", value);
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

        value = transformer.normalizeCanonicalUrl("http://hoge.com/bbb", "http://hoge.com/aaa");
        assertEquals("http://hoge.com/aaa", value);

        value = transformer.normalizeCanonicalUrl("http://hoge.com/bbb", "://hoge.com/aaa");
        assertEquals("http://hoge.com/aaa", value);

        value = transformer.normalizeCanonicalUrl("http://hoge.com/bbb", "//hoge.com/aaa");
        assertEquals("http://hoge.com/aaa", value);
    }

    public void test_getBaseUrl() throws Exception {
        final FessXpathTransformer transformer = new FessXpathTransformer();
        URL value;

        value = transformer.getBaseUrl("http://hoge.com/", null);
        assertEquals("http://hoge.com/", value.toExternalForm());

        value = transformer.getBaseUrl("http://hoge.com/", "http://hoge.com/");
        assertEquals("http://hoge.com/", value.toExternalForm());

        value = transformer.getBaseUrl("http://hoge.com/aaa/bbb.html", "http://hoge.com/");
        assertEquals("http://hoge.com/", value.toExternalForm());

        value = transformer.getBaseUrl("http://hoge.com/aaa/bbb.html", "http://hoge.com/ccc/");
        assertEquals("http://hoge.com/ccc/", value.toExternalForm());

        value = transformer.getBaseUrl("http://hoge.com/aaa/bbb.html", null);
        assertEquals("http://hoge.com/aaa/bbb.html", value.toExternalForm());

        value = transformer.getBaseUrl("http://hoge.com/", "://hoge.com/aaa/");
        assertEquals("http://hoge.com/aaa/", value.toExternalForm());

        value = transformer.getBaseUrl("https://hoge.com/", "://hoge.com/aaa/");
        assertEquals("https://hoge.com/aaa/", value.toExternalForm());

        value = transformer.getBaseUrl("http://hoge.com/", "//hoge.com/aaa/");
        assertEquals("http://hoge.com/aaa/", value.toExternalForm());

        value = transformer.getBaseUrl("https://hoge.com/", "//hoge.com/aaa/");
        assertEquals("https://hoge.com/aaa/", value.toExternalForm());

        value = transformer.getBaseUrl("https://hoge.com/", "aaa/");
        assertEquals("https://hoge.com/aaa/", value.toExternalForm());
    }

    public void test_getThumbnailUrl_no() throws Exception {

        final FessXpathTransformer transformer = new FessXpathTransformer();
        transformer.init();
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/");

        String data = "<html><body>foo</body></html>";
        assertNull(transformer.getThumbnailUrl(responseData, getDocument(data)));

        data = "<img src=\"http://example/foo.jpg\" width=\"x\" height=\"x\">";
        assertNull(transformer.getThumbnailUrl(responseData, getDocument(data)));

        data = "<img src=\"http://example/foo.jpg\" width=\"10\" height=\"100\">";
        assertNull(transformer.getThumbnailUrl(responseData, getDocument(data)));

        data = "<img src=\"http://example/foo.jpg\" width=\"100\" height=\"10\">";
        assertNull(transformer.getThumbnailUrl(responseData, getDocument(data)));

        data = "<img src=\"http://example/foo.jpg\" width=\"400\" height=\"100\">";
        assertNull(transformer.getThumbnailUrl(responseData, getDocument(data)));

        data = "<img src=\"http://example/foo.jpg\" width=\"100\" height=\"400\">";
        assertNull(transformer.getThumbnailUrl(responseData, getDocument(data)));
    }

    public void test_getThumbnailUrl() throws Exception {
        String data = "<meta property=\"og:image\" content=\"http://example/foo.jpg\" />";
        String expected = "http://example/foo.jpg";
        assertGetThumbnailUrl(data, expected);

        data = "<meta property=\"og:image\" content=\"://example/foo.jpg\" />";
        expected = "http://example/foo.jpg";
        assertGetThumbnailUrl(data, expected);

        data = "<meta property=\"og:image\" content=\"http://example/foo.jpg\" />";
        expected = "http://example/foo.jpg";
        assertGetThumbnailUrl(data, expected);

        data = "<meta property=\"og:image\" content=\"/foo.jpg\" />";
        expected = "http://example.com/foo.jpg";
        assertGetThumbnailUrl(data, expected);

        data = "<img src=\"http://example/foo.jpg\">";
        expected = "http://example/foo.jpg";
        assertGetThumbnailUrl(data, expected);

        data = "<img src=\"http://example/foo.jpg\">" //
                + "<img src=\"http://example/bar.jpg\">";
        expected = "http://example/foo.jpg";
        assertGetThumbnailUrl(data, expected);

        data = "<img src=\"http://example/foo.jpg\">" //
                + "<img src=\"http://example/bar.jpg\" width=\"100\" height=\"100\">";
        expected = "http://example/bar.jpg";
        assertGetThumbnailUrl(data, expected);

        data = "<img src=\"http://example/foo.jpg\" width=\"100\" height=\"100\">";
        expected = "http://example/foo.jpg";
        assertGetThumbnailUrl(data, expected);

        data = "<img src=\"http://example/foo.jpg\" width=\"100%\" height=\"100%\">";
        expected = "http://example/foo.jpg";
        assertGetThumbnailUrl(data, expected);

        data = "<img src=\"http://example/foo.jpg\" width=\"100px\" height=\"100px\">";
        expected = "http://example/foo.jpg";
        assertGetThumbnailUrl(data, expected);
    }

    private void assertGetThumbnailUrl(String data, String expected) throws Exception {
        final Document document = getDocument(data);

        final FessXpathTransformer transformer = new FessXpathTransformer();
        transformer.init();

        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/");

        assertEquals(expected, transformer.getThumbnailUrl(responseData, document));
    }

    public void test_isValidUrl() {
        final FessXpathTransformer transformer = new FessXpathTransformer();

        assertTrue(transformer.isValidUrl("http://www.example.com"));
        assertTrue(transformer.isValidUrl("http://www.example.com/aaa"));
        assertTrue(transformer.isValidUrl("https://www.example.com"));
        assertTrue(transformer.isValidUrl("://www.example.com"));
        assertTrue(transformer.isValidUrl("//www.example.com"));

        assertFalse(transformer.isValidUrl(null));
        assertFalse(transformer.isValidUrl(" "));
        assertFalse(transformer.isValidUrl("http://"));
        assertFalse(transformer.isValidUrl("http://http://www.example.com"));
    }
}
