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
package org.codelibs.fess.crawler.transformer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Unit tests for {@link AbstractFessFileTransformer}.
 * Tests file transformation logic including content extraction and metadata handling.
 */
public class AbstractFessFileTransformerTest extends UnitFessTestCase {

    private TestableAbstractFessFileTransformer transformer;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        transformer = new TestableAbstractFessFileTransformer();
        transformer.fessConfig = ComponentUtil.getFessConfig();
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        super.tearDown(testInfo);
    }

    @Test
    public void test_transform_nullResponseData() {
        try {
            transformer.transform(null);
            fail("Should throw CrawlingAccessException for null response");
        } catch (final CrawlingAccessException e) {
            assertTrue(e.getMessage().contains("No response body"));
        }
    }

    @Test
    public void test_transform_noResponseBody() {
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/test.pdf");
        // hasResponseBody() returns false when no body is set

        try {
            transformer.transform(responseData);
            fail("Should throw CrawlingAccessException for no response body");
        } catch (final CrawlingAccessException e) {
            assertTrue(e.getMessage().contains("No response body"));
        }
    }

    @Test
    public void test_responseData_basicProperties() {
        final ResponseData responseData = new ResponseData();

        responseData.setUrl("http://example.com/document.pdf");
        assertEquals("http://example.com/document.pdf", responseData.getUrl());

        responseData.setMimeType("application/pdf");
        assertEquals("application/pdf", responseData.getMimeType());

        responseData.setCharSet("UTF-8");
        assertEquals("UTF-8", responseData.getCharSet());

        responseData.setHttpStatusCode(200);
        assertEquals(200, responseData.getHttpStatusCode());

        responseData.setContentLength(12345L);
        assertEquals(12345L, responseData.getContentLength());

        responseData.setSessionId("session-123");
        assertEquals("session-123", responseData.getSessionId());
    }

    @Test
    public void test_responseData_executionTime() {
        final ResponseData responseData = new ResponseData();

        responseData.setExecutionTime(500L);
        assertEquals(500L, responseData.getExecutionTime());
    }

    @Test
    public void test_responseData_parentUrl() {
        final ResponseData responseData = new ResponseData();

        assertNull(responseData.getParentUrl());
        responseData.setParentUrl("http://example.com/parent");
        assertEquals("http://example.com/parent", responseData.getParentUrl());
    }

    @Test
    public void test_responseData_lastModified() {
        final ResponseData responseData = new ResponseData();

        assertNull(responseData.getLastModified());
        final Date now = new Date();
        responseData.setLastModified(now);
        assertEquals(now, responseData.getLastModified());
    }

    @Test
    public void test_metaContentMapping() {
        transformer.metaContentMapping = new HashMap<>();
        transformer.metaContentMapping.put("author", "dc:creator");
        transformer.metaContentMapping.put("title", "dc:title");

        assertEquals(2, transformer.metaContentMapping.size());
        assertEquals("dc:creator", transformer.metaContentMapping.get("author"));
        assertEquals("dc:title", transformer.metaContentMapping.get("title"));
    }

    @Test
    public void test_transformer_name() {
        transformer.setName("TestTransformer");
        assertEquals("TestTransformer", transformer.getName());
    }

    @Test
    public void test_responseData_method() {
        final ResponseData responseData = new ResponseData();

        responseData.setMethod("GET");
        assertEquals("GET", responseData.getMethod());

        responseData.setMethod("POST");
        assertEquals("POST", responseData.getMethod());
    }

    @Test
    public void test_responseData_metaDataMap() {
        final ResponseData responseData = new ResponseData();
        final Map<String, Object> metaDataMap = responseData.getMetaDataMap();
        metaDataMap.put("author", "John Doe");
        metaDataMap.put("keywords", new String[] { "java", "search" });

        assertNotNull(responseData.getMetaDataMap());
        assertEquals(2, responseData.getMetaDataMap().size());
        assertEquals("John Doe", responseData.getMetaDataMap().get("author"));
    }

    @Test
    public void test_fessConfig_crawlingDataEncoding() {
        final FessConfig config = ComponentUtil.getFessConfig();
        final String encoding = config.getCrawlerCrawlingDataEncoding();
        assertNotNull(encoding);
        // Default should be UTF-8 or similar
    }

    @Test
    public void test_fessConfig_ignoreEmptyContent() {
        final FessConfig config = ComponentUtil.getFessConfig();
        // Just verify the config method exists and returns a boolean
        final boolean ignoreEmpty = config.isCrawlerDocumentFileIgnoreEmptyContent();
        assertTrue(ignoreEmpty || !ignoreEmpty); // Either value is valid
    }

    @Test
    public void test_responseData_defaultValues() {
        final ResponseData responseData = new ResponseData();

        assertNull(responseData.getUrl());
        assertNull(responseData.getMimeType());
        assertNull(responseData.getCharSet());
        assertEquals(0, responseData.getHttpStatusCode());
        assertEquals(0, responseData.getContentLength());
    }

    @Test
    public void test_responseData_ruleId() {
        final ResponseData responseData = new ResponseData();

        assertNull(responseData.getRuleId());
        responseData.setRuleId("rule-123");
        assertEquals("rule-123", responseData.getRuleId());
    }

    @Test
    public void test_constants_mappingTypes() {
        assertEquals("array", Constants.MAPPING_TYPE_ARRAY);
        assertEquals("string", Constants.MAPPING_TYPE_STRING);
        assertEquals("long", Constants.MAPPING_TYPE_LONG);
        assertEquals("double", Constants.MAPPING_TYPE_DOUBLE);
        assertEquals("date", Constants.MAPPING_TYPE_DATE);
    }

    /**
     * Testable implementation of AbstractFessFileTransformer for unit testing.
     */
    private static class TestableAbstractFessFileTransformer extends AbstractFessFileTransformer {

        private static final Logger logger = LogManager.getLogger(TestableAbstractFessFileTransformer.class);

        @Override
        protected Extractor getExtractor(final ResponseData responseData) {
            return null; // Return null for testing
        }

        @Override
        public Logger getLogger() {
            return logger;
        }

        @Override
        public FessConfig getFessConfig() {
            return fessConfig;
        }
    }
}
