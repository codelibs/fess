/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

public class ProtocolHelperTest extends UnitFessTestCase {
    public void test_add_httpx() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http,https";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file,smb";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();
        assertEquals(2, protocolHelper.getWebProtocols().length);
        assertEquals("http:", protocolHelper.getWebProtocols()[0]);
        assertEquals("https:", protocolHelper.getWebProtocols()[1]);
        assertEquals(2, protocolHelper.getFileProtocols().length);
        assertEquals("file:", protocolHelper.getFileProtocols()[0]);
        assertEquals("smb:", protocolHelper.getFileProtocols()[1]);

        assertFalse(protocolHelper.isValidWebProtocol("httpx://test"));

        protocolHelper.addWebProtocol("httpx");
        assertEquals(3, protocolHelper.getWebProtocols().length);
        assertEquals("http:", protocolHelper.getWebProtocols()[0]);
        assertEquals("https:", protocolHelper.getWebProtocols()[1]);
        assertEquals("httpx:", protocolHelper.getWebProtocols()[2]);
        assertEquals(2, protocolHelper.getFileProtocols().length);
        assertEquals("file:", protocolHelper.getFileProtocols()[0]);
        assertEquals("smb:", protocolHelper.getFileProtocols()[1]);

        assertTrue(protocolHelper.isValidWebProtocol("httpx://test"));

        protocolHelper.addWebProtocol("httpx");
        assertEquals(3, protocolHelper.getWebProtocols().length);
        assertEquals(2, protocolHelper.getFileProtocols().length);
    }

    public void test_add_smbx() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http,https";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file,smb";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();
        assertEquals(2, protocolHelper.getWebProtocols().length);
        assertEquals("http:", protocolHelper.getWebProtocols()[0]);
        assertEquals("https:", protocolHelper.getWebProtocols()[1]);
        assertEquals(2, protocolHelper.getFileProtocols().length);
        assertEquals("file:", protocolHelper.getFileProtocols()[0]);
        assertEquals("smb:", protocolHelper.getFileProtocols()[1]);

        assertFalse(protocolHelper.isValidFileProtocol("smbx://test"));

        protocolHelper.addFileProtocol("smbx");
        assertEquals(2, protocolHelper.getWebProtocols().length);
        assertEquals("http:", protocolHelper.getWebProtocols()[0]);
        assertEquals("https:", protocolHelper.getWebProtocols()[1]);
        assertEquals(3, protocolHelper.getFileProtocols().length);
        assertEquals("file:", protocolHelper.getFileProtocols()[0]);
        assertEquals("smb:", protocolHelper.getFileProtocols()[1]);
        assertEquals("smbx:", protocolHelper.getFileProtocols()[2]);

        assertTrue(protocolHelper.isValidFileProtocol("smbx://test"));

        protocolHelper.addFileProtocol("smbx");
        assertEquals(2, protocolHelper.getWebProtocols().length);
        assertEquals(3, protocolHelper.getFileProtocols().length);
    }

    public void test_loadProtocols() {
        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.loadProtocols("org.codelibs.fess.test.net.protocol");

        assertEquals(1, protocolHelper.getWebProtocols().length);
        assertEquals(1, protocolHelper.getFileProtocols().length);
    }
}
