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
package org.codelibs.fess.mylasta.direction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.core.direction.exception.ConfigPropertyNotFoundException;

public class FessConfigTest extends UnitFessTestCase {

    private FessConfig fessConfig;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        fessConfig = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String get(String propertyKey) {
                // Return test values for specific properties
                switch (propertyKey) {
                case FessConfig.DOMAIN_TITLE:
                    return "Fess";
                case FessConfig.search_engine_TYPE:
                    return "default";
                case FessConfig.search_engine_HTTP_URL:
                    return "http://localhost:9201";
                case FessConfig.search_engine_heartbeat_interval:
                    return "10000";
                case FessConfig.APP_CIPHER_ALGORITHM:
                    return "aes";
                case FessConfig.APP_CIPHER_KEY:
                    return "___change__me___";
                case FessConfig.APP_ENCRYPT_PROPERTY_PATTERN:
                    return ".*password|.*key|.*token|.*secret";
                case FessConfig.APP_EXTENSION_NAMES:
                    return "jpg,jpeg,gif,png";

                case FessConfig.JOB_MAX_CRAWLER_PROCESSES:
                    return "3";
                case FessConfig.MAX_LOG_OUTPUT_LENGTH:
                    return "4000";

                // Crawler hotthread properties

                // Index field constants
                case FessConfig.INDEX_FIELD_favorite_count:
                    return "favorite_count";
                case FessConfig.INDEX_FIELD_click_count:
                    return "click_count";
                case FessConfig.INDEX_FIELD_config_id:
                    return "config_id";
                case FessConfig.INDEX_FIELD_EXPIRES:
                    return "expires";
                case FessConfig.INDEX_FIELD_URL:
                    return "url";
                case FessConfig.INDEX_FIELD_doc_id:
                    return "doc_id";
                case FessConfig.INDEX_FIELD_ID:
                    return "id";
                case FessConfig.INDEX_FIELD_VERSION:
                    return "_version";
                case FessConfig.INDEX_FIELD_seq_no:
                    return "_seq_no";
                case FessConfig.INDEX_FIELD_primary_term:
                    return "_primary_term";
                case FessConfig.INDEX_FIELD_LANG:
                    return "lang";
                case FessConfig.INDEX_FIELD_has_cache:
                    return "has_cache";
                case FessConfig.INDEX_FIELD_last_modified:
                    return "last_modified";
                case FessConfig.INDEX_FIELD_ANCHOR:
                    return "anchor";
                case FessConfig.INDEX_FIELD_SEGMENT:
                    return "segment";
                case FessConfig.INDEX_FIELD_ROLE:
                    return "role";
                case FessConfig.INDEX_FIELD_BOOST:
                    return "boost";
                case FessConfig.INDEX_FIELD_TITLE:
                    return "title";
                case FessConfig.INDEX_FIELD_CONTENT:
                    return "content";
                // Index admin fields
                case FessConfig.INDEX_ADMIN_INTEGER_FIELDS:
                    return "boost";
                case FessConfig.INDEX_ADMIN_LONG_FIELDS:
                    return "";
                case FessConfig.INDEX_ADMIN_FLOAT_FIELDS:
                    return "";
                case FessConfig.INDEX_ADMIN_DOUBLE_FIELDS:
                    return "";
                case FessConfig.INDEX_ADMIN_DATE_FIELDS:
                    return "expires,start_time,end_time";
                case FessConfig.INDEX_ADMIN_REQUIRED_FIELDS:
                    return "url,title,role,boost";
                case FessConfig.INDEX_ADMIN_ARRAY_FIELDS:
                    return "alerts,roles";

                case FessConfig.INDEX_FIELD_CACHE:
                    return "cache";
                case FessConfig.INDEX_FIELD_DIGEST:
                    return "digest";
                case FessConfig.INDEX_FIELD_HOST:
                    return "host";
                case FessConfig.INDEX_FIELD_SITE:
                    return "site";
                case FessConfig.INDEX_FIELD_content_length:
                    return "content_length";
                case FessConfig.INDEX_FIELD_FILETYPE:
                    return "filetype";
                case FessConfig.INDEX_FIELD_FILENAME:
                    return "filename";
                case FessConfig.INDEX_FIELD_THUMBNAIL:
                    return "thumbnail";
                case FessConfig.INDEX_FIELD_MIMETYPE:
                    return "mimetype";

                // Crawler settings
                case FessConfig.CRAWLER_DATA_SERIALIZER:
                    return "org.codelibs.fess.crawler.client.AccessTimeoutTarget";
                case FessConfig.CRAWLER_DATA_ENV_PARAM_KEY_PATTERN:
                    return "^FESS_ENV_.*";

                // Crawler hotthread properties - actual keys
                case "crawler.hotthread.ignore_idle_threads":
                    return "true";
                case "crawler.hotthread.interval":
                    return "500ms";
                case "crawler.hotthread.snapshots":
                    return "10";
                case "crawler.hotthread.threads":
                    return "3";
                case "crawler.hotthread.timeout":
                    return "30s";
                case "crawler.hotthread.type":
                    return "cpu";

                default:
                    throw new ConfigPropertyNotFoundException(propertyKey);
                }
            }

            @Override
            public Integer getAsInteger(String propertyKey) {
                String value = get(propertyKey);
                if (value == null) {
                    return null;
                }
                try {
                    return Integer.valueOf(value);
                } catch (NumberFormatException e) {
                    return null;
                }
            }

            @Override
            public Long getAsLong(String propertyKey) {
                String value = get(propertyKey);
                if (value == null) {
                    return null;
                }
                try {
                    return Long.valueOf(value);
                } catch (NumberFormatException e) {
                    return null;
                }
            }

            @Override
            public BigDecimal getAsDecimal(String propertyKey) {
                String value = get(propertyKey);
                if (value == null) {
                    return null;
                }
                try {
                    return new BigDecimal(value);
                } catch (NumberFormatException e) {
                    return null;
                }
            }

            @Override
            public boolean is(String propertyKey) {
                String value = get(propertyKey);
                return "true".equalsIgnoreCase(value);
            }

            // Override getDayForCleanup to avoid ComponentUtil dependency
            public int getDayForCleanup() {
                return 3;
            }
        };
        ComponentUtil.register(fessConfig, FessConfig.class.getCanonicalName());
    }

    // Test basic configuration properties
    public void test_domainTitle() {
        assertEquals("Fess", fessConfig.getDomainTitle());
    }

    public void test_searchEngineType() {
        assertEquals("default", fessConfig.getSearchEngineType());
    }

    public void test_searchEngineHttpUrl() {
        assertEquals("http://localhost:9201", fessConfig.getSearchEngineHttpUrl());
    }

    public void test_searchEngineHeartbeatInterval() {
        assertEquals(Integer.valueOf(10000), fessConfig.getSearchEngineHeartbeatIntervalAsInteger());
    }

    public void test_appCipherAlgorithm() {
        assertEquals("aes", fessConfig.getAppCipherAlgorithm());
    }

    public void test_appCipherKey() {
        assertEquals("___change__me___", fessConfig.getAppCipherKey());
    }

    public void test_appEncryptPropertyPattern() {
        assertEquals(".*password|.*key|.*token|.*secret", fessConfig.getAppEncryptPropertyPattern());
    }

    public void test_appExtensionNames() {
        assertEquals("jpg,jpeg,gif,png", fessConfig.getAppExtensionNames());
    }

    public void test_jobMaxCrawlerProcesses() {
        assertEquals(Integer.valueOf(3), fessConfig.getJobMaxCrawlerProcessesAsInteger());
    }

    public void test_maxLogOutputLength() {
        assertEquals(Integer.valueOf(4000), fessConfig.getMaxLogOutputLengthAsInteger());
    }

    // Test index field configurations
    public void test_indexFieldFavoriteCount() {
        assertEquals("favorite_count", fessConfig.getIndexFieldFavoriteCount());
    }

    public void test_indexFieldClickCount() {
        assertEquals("click_count", fessConfig.getIndexFieldClickCount());
    }

    public void test_indexFieldConfigId() {
        assertEquals("config_id", fessConfig.getIndexFieldConfigId());
    }

    public void test_indexFieldExpires() {
        assertEquals("expires", fessConfig.getIndexFieldExpires());
    }

    public void test_indexFieldUrl() {
        assertEquals("url", fessConfig.getIndexFieldUrl());
    }

    public void test_indexFieldDocId() {
        assertEquals("doc_id", fessConfig.getIndexFieldDocId());
    }

    public void test_indexFieldId() {
        assertEquals("id", fessConfig.getIndexFieldId());
    }

    public void test_indexFieldVersion() {
        assertEquals("_version", fessConfig.getIndexFieldVersion());
    }

    public void test_indexFieldSeqNo() {
        assertEquals("_seq_no", fessConfig.getIndexFieldSeqNo());
    }

    public void test_indexFieldPrimaryTerm() {
        assertEquals("_primary_term", fessConfig.getIndexFieldPrimaryTerm());
    }

    public void test_indexFieldLang() {
        assertEquals("lang", fessConfig.getIndexFieldLang());
    }

    public void test_indexFieldHasCache() {
        assertEquals("has_cache", fessConfig.getIndexFieldHasCache());
    }

    public void test_indexFieldLastModified() {
        assertEquals("last_modified", fessConfig.getIndexFieldLastModified());
    }

    public void test_indexFieldAnchor() {
        assertEquals("anchor", fessConfig.getIndexFieldAnchor());
    }

    public void test_indexFieldSegment() {
        assertEquals("segment", fessConfig.getIndexFieldSegment());
    }

    public void test_indexFieldRole() {
        assertEquals("role", fessConfig.getIndexFieldRole());
    }

    public void test_indexFieldBoost() {
        assertEquals("boost", fessConfig.getIndexFieldBoost());
    }

    public void test_indexFieldTitle() {
        assertEquals("title", fessConfig.getIndexFieldTitle());
    }

    public void test_indexFieldContent() {
        assertEquals("content", fessConfig.getIndexFieldContent());
    }

    public void test_indexFieldCache() {
        assertEquals("cache", fessConfig.getIndexFieldCache());
    }

    public void test_indexFieldDigest() {
        assertEquals("digest", fessConfig.getIndexFieldDigest());
    }

    public void test_indexFieldHost() {
        assertEquals("host", fessConfig.getIndexFieldHost());
    }

    public void test_indexFieldSite() {
        assertEquals("site", fessConfig.getIndexFieldSite());
    }

    public void test_indexFieldContentLength() {
        assertEquals("content_length", fessConfig.getIndexFieldContentLength());
    }

    public void test_indexFieldFiletype() {
        assertEquals("filetype", fessConfig.getIndexFieldFiletype());
    }

    public void test_indexFieldFilename() {
        assertEquals("filename", fessConfig.getIndexFieldFilename());
    }

    public void test_indexFieldThumbnail() {
        assertEquals("thumbnail", fessConfig.getIndexFieldThumbnail());
    }

    public void test_indexFieldMimetype() {
        assertEquals("mimetype", fessConfig.getIndexFieldMimetype());
    }

    // Test admin field configurations
    public void test_indexAdminArrayFields() {
        String[] fields = fessConfig.getIndexAdminArrayFields().split(",");
        assertEquals(2, fields.length);
        assertEquals("alerts", fields[0]);
        assertEquals("roles", fields[1]);
    }

    public void test_indexAdminDateFields() {
        String[] fields = fessConfig.getIndexAdminDateFields().split(",");
        assertEquals(3, fields.length);
        assertEquals("expires", fields[0]);
        assertEquals("start_time", fields[1]);
        assertEquals("end_time", fields[2]);
    }

    public void test_indexAdminIntegerFields() {
        String[] fields = fessConfig.getIndexAdminIntegerFields().split(",");
        assertEquals(1, fields.length);
        assertEquals("boost", fields[0]);
    }

    public void test_indexAdminLongFields() {
        String fieldsStr = fessConfig.getIndexAdminLongFields();
        assertEquals("", fieldsStr);
        String[] fields = fieldsStr.isEmpty() ? new String[0] : fieldsStr.split(",");
        assertEquals(0, fields.length);
    }

    public void test_indexAdminFloatFields() {
        String fieldsStr = fessConfig.getIndexAdminFloatFields();
        assertEquals("", fieldsStr);
        String[] fields = fieldsStr.isEmpty() ? new String[0] : fieldsStr.split(",");
        assertEquals(0, fields.length);
    }

    public void test_indexAdminDoubleFields() {
        String fieldsStr = fessConfig.getIndexAdminDoubleFields();
        assertEquals("", fieldsStr);
        String[] fields = fieldsStr.isEmpty() ? new String[0] : fieldsStr.split(",");
        assertEquals(0, fields.length);
    }

    public void test_indexAdminRequiredFields() {
        String[] fields = fessConfig.getIndexAdminRequiredFields().split(",");
        assertEquals(4, fields.length);
        assertEquals("url", fields[0]);
        assertEquals("title", fields[1]);
        assertEquals("role", fields[2]);
        assertEquals("boost", fields[3]);
    }

    // Test crawler configurations
    public void test_crawlerDataSerializer() {
        assertEquals("org.codelibs.fess.crawler.client.AccessTimeoutTarget", fessConfig.getCrawlerDataSerializer());
    }

    public void test_crawlerDataEnvParamKeyPattern() {
        assertEquals("^FESS_ENV_.*", fessConfig.getCrawlerDataEnvParamKeyPattern());
    }

    public void test_crawlerHotthreadIgnoreIdleThreads() {
        assertTrue(fessConfig.isCrawlerHotthreadIgnoreIdleThreads());
    }

    public void test_crawlerHotthreadInterval() {
        assertEquals("500ms", fessConfig.getCrawlerHotthreadInterval());
    }

    public void test_crawlerHotthreadSnapshots() {
        assertEquals(Integer.valueOf(10), fessConfig.getCrawlerHotthreadSnapshotsAsInteger());
    }

    public void test_crawlerHotthreadThreads() {
        assertEquals(Integer.valueOf(3), fessConfig.getCrawlerHotthreadThreadsAsInteger());
    }

    public void test_crawlerHotthreadTimeout() {
        assertEquals("30s", fessConfig.getCrawlerHotthreadTimeout());
    }

    public void test_crawlerHotthreadType() {
        assertEquals("cpu", fessConfig.getCrawlerHotthreadType());
    }

    // Test property not found exception
    public void test_propertyNotFound() {
        try {
            fessConfig.get("non.existent.property");
            fail("Should throw ConfigPropertyNotFoundException");
        } catch (ConfigPropertyNotFoundException e) {
            assertEquals("non.existent.property", e.getMessage());
        }
    }

    // Test isExtensionAllowed method
    /*
    // Commented out - methods don't exist in FessConfig
    public void test_isExtensionAllowed() {
        assertTrue(fessConfig.isExtensionAllowed("jpg"));
        assertTrue(fessConfig.isExtensionAllowed("jpeg"));
        assertTrue(fessConfig.isExtensionAllowed("gif"));
        assertTrue(fessConfig.isExtensionAllowed("png"));
        assertFalse(fessConfig.isExtensionAllowed("pdf"));
        assertFalse(fessConfig.isExtensionAllowed("doc"));
    }

    // Test available extensions
    public void test_getAvailableExtensions() {
        String[] extensions = fessConfig.getAvailableExtensions();
        assertEquals(4, extensions.length);
        assertEquals("jpg", extensions[0]);
        assertEquals("jpeg", extensions[1]);
        assertEquals("gif", extensions[2]);
        assertEquals("png", extensions[3]);
    }
    */

    // Test crawler process limit
    public void test_getCrawlerProcessLimit() {
        assertEquals(3, fessConfig.getJobMaxCrawlerProcessesAsInteger().intValue());
    }

    // Test log output length limit
    public void test_getMaxLogOutputLength() {
        assertEquals(4000, fessConfig.getMaxLogOutputLengthAsInteger().intValue());
    }

    // Test configuration with empty values
    public void test_emptyStringArrayFields() {
        String longFieldsStr = fessConfig.getIndexAdminLongFields();
        assertEquals("", longFieldsStr);
        String[] longFields = longFieldsStr.isEmpty() ? new String[0] : longFieldsStr.split(",");
        assertNotNull(longFields);
        assertEquals(0, longFields.length);

        String floatFieldsStr = fessConfig.getIndexAdminFloatFields();
        assertEquals("", floatFieldsStr);
        String[] floatFields = floatFieldsStr.isEmpty() ? new String[0] : floatFieldsStr.split(",");
        assertNotNull(floatFields);
        assertEquals(0, floatFields.length);

        String doubleFieldsStr = fessConfig.getIndexAdminDoubleFields();
        assertEquals("", doubleFieldsStr);
        String[] doubleFields = doubleFieldsStr.isEmpty() ? new String[0] : doubleFieldsStr.split(",");
        assertNotNull(doubleFields);
        assertEquals(0, doubleFields.length);
    }

    // Test configuration key constants
    public void test_configKeyConstants() {
        // Verify that constants are properly defined
        assertNotNull(FessConfig.DOMAIN_TITLE);
        assertEquals("domain.title", FessConfig.DOMAIN_TITLE);

        assertNotNull(FessConfig.search_engine_TYPE);
        assertEquals("search_engine.type", FessConfig.search_engine_TYPE);

        assertNotNull(FessConfig.search_engine_HTTP_URL);
        assertEquals("search_engine.http.url", FessConfig.search_engine_HTTP_URL);

        assertNotNull(FessConfig.INDEX_FIELD_favorite_count);
        assertEquals("index.field.favorite_count", FessConfig.INDEX_FIELD_favorite_count);

        assertNotNull(FessConfig.CRAWLER_DATA_SERIALIZER);
        assertEquals("crawler.data.serializer", FessConfig.CRAWLER_DATA_SERIALIZER);
    }

    // Test the get method directly
    public void test_getMethod() {
        assertEquals("Fess", fessConfig.get(FessConfig.DOMAIN_TITLE));
        assertEquals("default", fessConfig.get(FessConfig.search_engine_TYPE));
        assertEquals("http://localhost:9201", fessConfig.get(FessConfig.search_engine_HTTP_URL));
    }

    // Test numeric conversions
    public void test_numericConversions() {
        // Test integer conversion
        Integer heartbeatInterval = fessConfig.getSearchEngineHeartbeatIntervalAsInteger();
        assertNotNull(heartbeatInterval);
        assertEquals(10000, heartbeatInterval.intValue());

        // Test integer conversion for cleanup days
        int cleanupDays = fessConfig.getDayForCleanup();
        // cleanupDays is primitive int, not Integer
        assertEquals(3, cleanupDays);

        // Test integer conversion for crawler processes
        Integer crawlerProcesses = fessConfig.getJobMaxCrawlerProcessesAsInteger();
        assertNotNull(crawlerProcesses);
        assertEquals(3, crawlerProcesses.intValue());
    }

    // Test boolean conversions
    public void test_booleanConversions() {
        // Test boolean conversion for ignore idle threads
        boolean ignoreIdleThreads = fessConfig.isCrawlerHotthreadIgnoreIdleThreads();
        assertTrue(ignoreIdleThreads);
    }

    // Test pattern matching
    public void test_patternMatching() {
        String pattern = fessConfig.getAppEncryptPropertyPattern();
        assertNotNull(pattern);
        assertTrue(pattern.contains("password"));
        assertTrue(pattern.contains("key"));
        assertTrue(pattern.contains("token"));
        assertTrue(pattern.contains("secret"));
    }

    // Test array field parsing
    public void test_arrayFieldParsing() {
        // Test parsing of comma-separated values
        String[] arrayFields = fessConfig.getIndexAdminArrayFields().split(",");
        assertNotNull(arrayFields);
        assertEquals(2, arrayFields.length);

        String[] dateFields = fessConfig.getIndexAdminDateFields().split(",");
        assertNotNull(dateFields);
        assertEquals(3, dateFields.length);

        String[] requiredFields = fessConfig.getIndexAdminRequiredFields().split(",");
        assertNotNull(requiredFields);
        assertEquals(4, requiredFields.length);
    }

    // Test hotthread configuration
    public void test_hotthreadConfiguration() {
        // Test all hotthread settings
        assertTrue(fessConfig.isCrawlerHotthreadIgnoreIdleThreads());
        assertEquals("500ms", fessConfig.getCrawlerHotthreadInterval());
        assertEquals(Integer.valueOf(10), fessConfig.getCrawlerHotthreadSnapshotsAsInteger());
        assertEquals(Integer.valueOf(3), fessConfig.getCrawlerHotthreadThreadsAsInteger());
        assertEquals("30s", fessConfig.getCrawlerHotthreadTimeout());
        assertEquals("cpu", fessConfig.getCrawlerHotthreadType());
    }

    // Test field name configurations
    public void test_fieldNameConfigurations() {
        // Test all field name configurations are properly returned
        assertNotNull(fessConfig.getIndexFieldFavoriteCount());
        assertNotNull(fessConfig.getIndexFieldClickCount());
        assertNotNull(fessConfig.getIndexFieldConfigId());
        assertNotNull(fessConfig.getIndexFieldExpires());
        assertNotNull(fessConfig.getIndexFieldUrl());
        assertNotNull(fessConfig.getIndexFieldDocId());
        assertNotNull(fessConfig.getIndexFieldId());
        assertNotNull(fessConfig.getIndexFieldVersion());
        assertNotNull(fessConfig.getIndexFieldSeqNo());
        assertNotNull(fessConfig.getIndexFieldPrimaryTerm());
        assertNotNull(fessConfig.getIndexFieldLang());
        assertNotNull(fessConfig.getIndexFieldHasCache());
        assertNotNull(fessConfig.getIndexFieldLastModified());
        assertNotNull(fessConfig.getIndexFieldAnchor());
        assertNotNull(fessConfig.getIndexFieldSegment());
        assertNotNull(fessConfig.getIndexFieldRole());
        assertNotNull(fessConfig.getIndexFieldBoost());
        assertNotNull(fessConfig.getIndexFieldTitle());
        assertNotNull(fessConfig.getIndexFieldContent());
        assertNotNull(fessConfig.getIndexFieldCache());
        assertNotNull(fessConfig.getIndexFieldDigest());
        assertNotNull(fessConfig.getIndexFieldHost());
        assertNotNull(fessConfig.getIndexFieldSite());
        assertNotNull(fessConfig.getIndexFieldContentLength());
        assertNotNull(fessConfig.getIndexFieldFiletype());
        assertNotNull(fessConfig.getIndexFieldFilename());
        assertNotNull(fessConfig.getIndexFieldThumbnail());
        assertNotNull(fessConfig.getIndexFieldMimetype());
    }
}