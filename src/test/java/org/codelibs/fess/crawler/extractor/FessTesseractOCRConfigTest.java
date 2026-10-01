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
package org.codelibs.fess.crawler.extractor;

import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FessTesseractOCRConfigTest extends UnitFessTestCase {

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    static FessConfig createFessConfig(final boolean enabled, final String language, final String timeout) {
        return new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCrawlerDocumentOcrEnabled() {
                return enabled;
            }

            @Override
            public String getCrawlerDocumentOcrLanguage() {
                return language;
            }

            @Override
            public String getCrawlerDocumentOcrTimeout() {
                return timeout;
            }

            @Override
            public Integer getCrawlerDocumentOcrTimeoutAsInteger() {
                return timeout == null || timeout.isEmpty() ? null : Integer.valueOf(timeout);
            }
        };
    }

    @Test
    public void test_apply_disabled() {
        final FessTesseractOCRConfig config = new FessTesseractOCRConfig();
        config.apply(createFessConfig(false, "jpn", "60"));
        assertTrue(config.isSkipOcr());
        assertEquals("eng", config.getLanguage());
    }

    @Test
    public void test_apply_enabled() {
        final FessTesseractOCRConfig config = new FessTesseractOCRConfig();
        config.apply(createFessConfig(true, "jpn+eng", "60"));
        assertFalse(config.isSkipOcr());
        assertEquals("jpn+eng", config.getLanguage());
        assertEquals(60, config.getTimeoutSeconds());
        assertTrue(config.isPreserveInterwordSpacing());
    }

    @Test
    public void test_apply_blankTimeout() {
        final FessTesseractOCRConfig config = new FessTesseractOCRConfig();
        config.apply(createFessConfig(true, "jpn", ""));
        assertFalse(config.isSkipOcr());
        assertEquals("jpn", config.getLanguage());
        assertEquals(new TesseractOCRConfig().getTimeoutSeconds(), config.getTimeoutSeconds());
    }

    @Test
    public void test_apply_invalidLanguage() {
        final FessTesseractOCRConfig config = new FessTesseractOCRConfig();
        config.apply(createFessConfig(true, "jpn;rm", "60"));
        assertTrue(config.isSkipOcr());
    }

    @Test
    public void test_init() {
        ComponentUtil.setFessConfig(createFessConfig(true, "jpn", "60"));
        final FessTesseractOCRConfig config = new FessTesseractOCRConfig();
        config.init();
        assertFalse(config.isSkipOcr());
        assertEquals("jpn", config.getLanguage());
    }
}
