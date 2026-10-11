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
package org.codelibs.fess.opensearch.config.exentity;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.mylasta.direction.FessProp;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ParameterUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.core.security.exception.CipherFailureException;

public class DataConfigTest extends UnitFessTestCase {

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        FessProp.propMap.clear();
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getAppEncryptPropertyPattern() {
                return ".*password|.*key|.*secret";
            }
        });
    }

    // A value encrypted with another app.cipher.key must fail naming the crawl configuration, so that
    // the operator can tell which setting holds it.
    @Test
    public void test_getHandlerParameterMap_undecryptableValue_namesConfig() {
        final DataConfig config = new DataConfig();
        config.setId("abc123");
        config.setName("Intranet");
        config.setHandlerParameter("client.secret={cipher}00112233445566778899aabbccddeeff");
        final CipherFailureException e = Assertions.assertThrows(CipherFailureException.class, () -> config.getHandlerParameterMap());
        final String message = e.getMessage();
        assertTrue(message, message.contains("data config: name=Intranet, id=abc123"));
        assertTrue(message, message.contains("key=client.secret"));
        assertTrue(message, message.contains("app.cipher.key"));
        assertFalse(message, message.contains("00112233445566778899aabbccddeeff"));
    }

    @Test
    public void test_getHandlerParameterMap_decryptableValue() {
        final DataConfig config = new DataConfig();
        config.setId("abc123");
        config.setName("Intranet");
        config.setHandlerParameter(ParameterUtil.encrypt("client.secret=value"));
        assertEquals("value", config.getHandlerParameterMap().get("client.secret"));
    }
}
