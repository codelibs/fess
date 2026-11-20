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
package org.codelibs.fess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.junit.jupiter.api.Test;

public class FessBootCompressionTest {

    private AbstractHttp11Protocol<?> protocol(final Connector connector) {
        return (AbstractHttp11Protocol<?>) connector.getProtocolHandler();
    }

    @Test
    public void test_compressionIsOnWithoutAConfigurationFile() {
        final Connector connector = new Connector("HTTP/1.1");
        FessBoot.setupCompression(connector, null);
        assertEquals("on", protocol(connector).getCompression());
        assertEquals(1024, protocol(connector).getCompressionMinSize());
    }

    @Test
    public void test_compressionCoversTheStaticTextTypesOnly() {
        final Connector connector = new Connector("HTTP/1.1");
        FessBoot.setupCompression(connector, new Properties());
        final List<String> types = Arrays.asList(protocol(connector).getCompressibleMimeTypes());
        for (final String type : new String[] { "text/css", "text/javascript", "application/javascript", "image/svg+xml" }) {
            assertTrue(types.contains(type), type);
        }
        // pages and JSON can carry a session token; event streams, NDJSON and CSV exports are written as they are produced
        for (final String type : new String[] { "text/html", "application/json", "text/plain", "text/xml", "application/xml",
                "text/event-stream", "application/x-ndjson", "text/csv", "application/octet-stream" }) {
            assertFalse(types.contains(type), type);
        }
    }

    @Test
    public void test_theConfigurationFileCanAddTypes() {
        final Connector connector = new Connector("HTTP/1.1");
        final Properties props = new Properties();
        props.setProperty("tomcat.compression.mime", " text/html,application/json ");
        FessBoot.setupCompression(connector, props);
        final List<String> types = Arrays.asList(protocol(connector).getCompressibleMimeTypes());
        assertTrue(types.contains("text/html"));
        assertTrue(types.contains("application/json"));
        assertTrue(types.contains("text/css"));
    }

    @Test
    public void test_aBlankTypeListKeepsTheDefault() {
        final Connector connector = new Connector("HTTP/1.1");
        final Properties props = new Properties();
        props.setProperty("tomcat.compression.mime", " ");
        FessBoot.setupCompression(connector, props);
        assertEquals(4, protocol(connector).getCompressibleMimeTypes().length);
    }

    @Test
    public void test_theConfigurationFileCanTurnCompressionOff() {
        final Connector connector = new Connector("HTTP/1.1");
        final Properties props = new Properties();
        props.setProperty("tomcat.compression", " off ");
        FessBoot.setupCompression(connector, props);
        assertEquals("off", protocol(connector).getCompression());
    }

    @Test
    public void test_aBlankValueKeepsTheDefault() {
        final Connector connector = new Connector("HTTP/1.1");
        final Properties props = new Properties();
        props.setProperty("tomcat.compression", " ");
        FessBoot.setupCompression(connector, props);
        assertEquals("on", protocol(connector).getCompression());
    }
}
