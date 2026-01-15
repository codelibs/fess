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
package org.codelibs.fess.app.service;

import java.io.StringReader;
import java.io.StringWriter;

import org.codelibs.fess.app.pager.CrawlingInfoPager;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.opensearch.config.exentity.CrawlingInfo;
import org.codelibs.fess.opensearch.config.exentity.CrawlingInfoParam;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Unit tests for {@link CrawlingInfoService}.
 * Tests crawling info management and CSV import/export functionality.
 */
public class CrawlingInfoServiceTest extends UnitFessTestCase {

    private CrawlingInfoService crawlingInfoService;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        crawlingInfoService = new CrawlingInfoService();
    }

    @Test
    public void test_defaultConstructor() {
        final CrawlingInfoService service = new CrawlingInfoService();
        assertNotNull(service);
    }

    @Test
    public void test_setupStoreCondition_nullInput() {
        try {
            crawlingInfoService.setupStoreCondition(null);
            fail("Should throw FessSystemException for null input");
        } catch (final FessSystemException e) {
            assertEquals("Crawling Session is null.", e.getMessage());
        }
    }

    @Test
    public void test_setupStoreCondition_setsCreatedTime() {
        final CrawlingInfo crawlingInfo = new CrawlingInfo();
        assertNull(crawlingInfo.getCreatedTime());

        crawlingInfoService.setupStoreCondition(crawlingInfo);

        assertNotNull(crawlingInfo.getCreatedTime());
        assertTrue(crawlingInfo.getCreatedTime() > 0);
    }

    @Test
    public void test_setupStoreCondition_preservesExistingCreatedTime() {
        final CrawlingInfo crawlingInfo = new CrawlingInfo();
        final Long existingTime = 12345678L;
        crawlingInfo.setCreatedTime(existingTime);

        crawlingInfoService.setupStoreCondition(crawlingInfo);

        assertEquals(existingTime, crawlingInfo.getCreatedTime());
    }

    @Test
    public void test_crawlingInfoPager_initialization() {
        final CrawlingInfoPager pager = new CrawlingInfoPager();
        assertNull(pager.id);
        assertNull(pager.sessionId);
        assertEquals(1, pager.getCurrentPageNumber());
    }

    @Test
    public void test_crawlingInfoPager_setSessionId() {
        final CrawlingInfoPager pager = new CrawlingInfoPager();
        pager.sessionId = "test-session-123";
        assertEquals("test-session-123", pager.sessionId);
    }

    @Test
    public void test_crawlingInfoPager_setId() {
        final CrawlingInfoPager pager = new CrawlingInfoPager();
        pager.id = "crawling-id-456";
        assertEquals("crawling-id-456", pager.id);
    }

    @Test
    public void test_crawlingInfoPager_pageSize() {
        final CrawlingInfoPager pager = new CrawlingInfoPager();
        assertEquals(25, pager.getPageSize()); // Default page size
    }

    @Test
    public void test_crawlingInfo_entity() {
        final CrawlingInfo info = new CrawlingInfo();
        assertNull(info.getId());
        assertNull(info.getSessionId());
        assertNull(info.getName());
        assertNull(info.getExpiredTime());
        assertNull(info.getCreatedTime());

        info.setId("info-id");
        info.setSessionId("session-123");
        info.setName("Test Crawl");
        info.setExpiredTime(999999999L);
        info.setCreatedTime(888888888L);

        assertEquals("info-id", info.getId());
        assertEquals("session-123", info.getSessionId());
        assertEquals("Test Crawl", info.getName());
        assertEquals(Long.valueOf(999999999L), info.getExpiredTime());
        assertEquals(Long.valueOf(888888888L), info.getCreatedTime());
    }

    @Test
    public void test_crawlingInfoParam_entity() {
        final CrawlingInfoParam param = new CrawlingInfoParam();
        assertNull(param.getId());
        assertNull(param.getCrawlingInfoId());
        assertNull(param.getKey());
        assertNull(param.getValue());
        assertNull(param.getCreatedTime());

        param.setId("param-id");
        param.setCrawlingInfoId("crawling-info-id");
        param.setKey("docs_processed");
        param.setValue("1000");
        param.setCreatedTime(777777777L);

        assertEquals("param-id", param.getId());
        assertEquals("crawling-info-id", param.getCrawlingInfoId());
        assertEquals("docs_processed", param.getKey());
        assertEquals("1000", param.getValue());
        assertEquals(Long.valueOf(777777777L), param.getCreatedTime());
    }

    @Test
    public void test_exportCsv_headers() {
        final StringWriter writer = new StringWriter();
        crawlingInfoService.exportCsv(writer);
        
        final String output = writer.toString();
        // Check for CSV headers
        assertTrue(output.contains("SessionId"));
        assertTrue(output.contains("SessionCreatedTime"));
        assertTrue(output.contains("Key"));
        assertTrue(output.contains("Value"));
        assertTrue(output.contains("CreatedTime"));
    }

    @Test
    public void test_importCsv_emptyInput() {
        final String csvContent = "SessionId,SessionCreatedTime,Key,Value,CreatedTime\n";
        final StringReader reader = new StringReader(csvContent);
        
        // Should not throw exception for empty CSV (only header)
        crawlingInfoService.importCsv(reader);
        assertTrue(true);
    }

    @Test
    public void test_crawlingInfoPager_pagination() {
        final CrawlingInfoPager pager = new CrawlingInfoPager();
        
        pager.setCurrentPageNumber(5);
        assertEquals(5, pager.getCurrentPageNumber());
        
        pager.setAllRecordCount(100);
        assertEquals(100, pager.getAllRecordCount());
        
        pager.setAllPageCount(4);
        assertEquals(4, pager.getAllPageCount());
    }

    @Test
    public void test_crawlingInfoPager_clear() {
        final CrawlingInfoPager pager = new CrawlingInfoPager();
        pager.id = "some-id";
        pager.sessionId = "some-session";
        pager.setCurrentPageNumber(5);
        
        pager.clear();
        
        assertNull(pager.id);
        assertNull(pager.sessionId);
    }

    @Test
    public void test_crawlingInfo_toString() {
        final CrawlingInfo info = new CrawlingInfo();
        info.setId("test-id");
        info.setSessionId("test-session");
        
        final String str = info.toString();
        assertNotNull(str);
    }

    @Test
    public void test_crawlingInfoParam_toString() {
        final CrawlingInfoParam param = new CrawlingInfoParam();
        param.setId("param-id");
        param.setKey("test-key");
        param.setValue("test-value");
        
        final String str = param.toString();
        assertNotNull(str);
    }
}
