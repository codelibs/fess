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
package org.codelibs.fess.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Checks the shipped {@code log4j2.xml}: the levels that keep credentials out of the application log
 * however verbose the log level is set.
 */
public class ShippedLog4j2ConfigTest {

    private static String levelOf(final String loggerName) throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        try (InputStream in = ShippedLog4j2ConfigTest.class.getResourceAsStream("/log4j2.xml")) {
            assertNotNull(in, "log4j2.xml is on the class path");
            final NodeList loggers = factory.newDocumentBuilder().parse(in).getElementsByTagName("Logger");
            for (int i = 0; i < loggers.getLength(); i++) {
                final Element logger = (Element) loggers.item(i);
                if (loggerName.equals(logger.getAttribute("name"))) {
                    return logger.getAttribute("level");
                }
            }
        }
        return null;
    }

    @Test
    public void test_requestLoggingFilterStaysAtWarn() throws Exception {
        // At debug LastaFlute's request log writes every header (Authorization included), the cookies,
        // the request parameters (the password typed into the login form) and the session attributes;
        // at info it writes the same dump for a request that ended as a client error. The org.lastaflute
        // logger follows ${log.level}, so the filter needs a level of its own, and it must not be below
        // warn: the default log level is warn, and a lower level would make the dump appear there.
        assertEquals("warn", levelOf("org.lastaflute.web.servlet.filter.RequestLoggingFilter"));
    }

    @Test
    public void test_samlLibraryStaysAtInfo() throws Exception {
        assertEquals("info", levelOf("org.codelibs.saml2"));
    }
}
