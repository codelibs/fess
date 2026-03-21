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
package org.codelibs.fess.webapp;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WebXmlTest extends UnitFessTestCase {

    private Document webXmlDocument;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        final DocumentBuilder builder = factory.newDocumentBuilder();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("../webapp/WEB-INF/web.xml")) {
            if (is == null) {
                try (InputStream is2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("web.xml")) {
                    if (is2 != null) {
                        webXmlDocument = builder.parse(is2);
                    }
                }
            } else {
                webXmlDocument = builder.parse(is);
            }
        }
        if (webXmlDocument == null) {
            final java.io.File file = new java.io.File("src/main/webapp/WEB-INF/web.xml");
            if (file.exists()) {
                webXmlDocument = builder.parse(file);
            }
        }
    }

    @Test
    public void test_webXmlExists() {
        assertNotNull(webXmlDocument, "web.xml should be parseable");
    }

    @Test
    public void test_servletVersion() {
        if (webXmlDocument == null) {
            return;
        }
        final Element root = webXmlDocument.getDocumentElement();
        assertEquals("web-app", root.getLocalName());
        assertEquals("6.1", root.getAttribute("version"));
    }

    @Test
    public void test_namespace() {
        if (webXmlDocument == null) {
            return;
        }
        final Element root = webXmlDocument.getDocumentElement();
        assertEquals("https://jakarta.ee/xml/ns/jakartaee", root.getNamespaceURI());
    }

    @Test
    public void test_schemaLocation() {
        if (webXmlDocument == null) {
            return;
        }
        final Element root = webXmlDocument.getDocumentElement();
        final String schemaLocation = root.getAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
        assertTrue("schemaLocation should reference web-app_6_1.xsd", schemaLocation.contains("web-app_6_1.xsd"));
    }
}
