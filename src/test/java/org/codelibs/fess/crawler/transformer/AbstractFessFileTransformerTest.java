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

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.ClassUtil;
import org.codelibs.core.lang.FieldUtil;
import org.codelibs.core.misc.Tuple3;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.client.fs.FileSystemClient;
import org.codelibs.fess.crawler.client.ftp.FtpClient;
import org.codelibs.fess.crawler.client.smb.SmbClient;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.DocumentHelper;
import org.codelibs.fess.helper.FileTypeHelper;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.LanguageHelper;
import org.codelibs.fess.helper.LabelTypeHelper.LabelTypePattern;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.ProtocolHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.exentity.FileConfig;
import org.codelibs.fess.unit.LogCapturingAppender;
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
        // Verify the config returns the expected default value (false)
        assertFalse(config.isCrawlerDocumentFileIgnoreEmptyContent());
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
    /**
     * A document whose text cannot be extracted -- because it exceeds the maximum content
     * length, most often -- is still indexed, with no text at all. The crawl is not failed, so
     * no failure url is recorded either, and nothing downstream reports the loss. The warning is
     * the only record of it.
     */
    @Test
    public void test_getExtractData_reportsADocumentIndexedWithoutContent() {
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.URL, "file:/share/huge.pdf");
        final Extractor failing = (in, p) -> {
            throw new MaxLengthExceededException("Content length (20000000 bytes) exceeds the maximum allowed length (10485760 bytes).");
        };
        final LogCapturingAppender capture = LogCapturingAppender.attach(AbstractFessFileTransformer.class);
        try {
            final ExtractData extractData = transformer.getExtractData(failing, new ByteArrayInputStream(new byte[0]), params);
            assertTrue(extractData.getContent() == null || extractData.getContent().isEmpty());
            assertTrue(capture.warnings().stream().anyMatch(m -> m.contains("file:/share/huge.pdf")),
                    "the document that lost its content must be named: " + capture.warnings());
        } finally {
            capture.detach();
        }
    }

    private Map<String, Object> generateDataWithHeaders(final Map<String, Object> headers) {
        return generateDataWith(headers, null, Map.of(), null);
    }

    private Map<String, Object> generateDataWith(final Map<String, Object> headers, final String configParameter,
            final Map<String, String[]> extractedMetadata, final FessConfig fessConfig) {
        ComponentUtil.register(new CrawlingInfoHelper(), "crawlingInfoHelper");
        ComponentUtil.register(new PathMappingHelper(), "pathMappingHelper");
        ComponentUtil.register(new CrawlingConfigHelper(), "crawlingConfigHelper");
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        ComponentUtil.register(new FileTypeHelper(), "fileTypeHelper");
        ComponentUtil.register(new DocumentHelper(), "documentHelper");
        final LabelTypeHelper labelTypeHelper = new LabelTypeHelper();
        final Field patternList = ClassUtil.getDeclaredField(LabelTypeHelper.class, "labelTypePatternList");
        patternList.setAccessible(true);
        FieldUtil.set(patternList, labelTypeHelper, new ArrayList<LabelTypePattern>());
        ComponentUtil.register(labelTypeHelper, "labelTypeHelper");
        ComponentUtil.register(new ProtocolHelper(), "protocolHelper");
        ComponentUtil.register(new LanguageHelper() {
            @Override
            public String detectLanguage(final String content) {
                return null;
            }
        }, "languageHelper");
        final FileConfig fileConfig = new FileConfig();
        fileConfig.setId("1");
        fileConfig.setConfigParameter(configParameter);
        final String sessionId = ComponentUtil.getCrawlingConfigHelper().store("test", fileConfig);

        final TestableAbstractFessFileTransformer extracting = new TestableAbstractFessFileTransformer() {
            @Override
            protected Extractor getExtractor(final ResponseData responseData) {
                return (in, params) -> {
                    final ExtractData extractData = new ExtractData("body text");
                    extractedMetadata.forEach(extractData::putValues);
                    return extractData;
                };
            }

            @Override
            protected List<String> getRoleTypes(final ResponseData responseData) {
                return new ArrayList<>();
            }
        };
        extracting.fessConfig = fessConfig != null ? fessConfig : ComponentUtil.getFessConfig();

        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/doc.pdf");
        responseData.setMimeType("application/pdf");
        responseData.setCharSet("UTF-8");
        responseData.setContentLength(9L);
        responseData.setSessionId(sessionId);
        responseData.setResponseBody("body text".getBytes());
        headers.forEach(responseData::addMetaData);
        return extracting.generateData(responseData);
    }

    @Test
    public void test_generateData_storesEtagVerbatim() {
        assertEquals("W/\"abc\"", generateDataWithHeaders(Map.of("ETag", "W/\"abc\"")).get("etag"));
    }

    @Test
    public void test_generateData_storesEtagFromLowerCaseHeader() {
        assertEquals("\"v1\"", generateDataWithHeaders(Map.of("etag", "\"v1\"")).get("etag"));
    }

    @Test
    public void test_generateData_noEtagHeader_noEtagField() {
        assertFalse(generateDataWithHeaders(Map.of()).containsKey("etag"));
    }

    @Test
    public void test_generateData_smbOwner_indexesAccountName() {
        final Map<String, Object> dataMap =
                generateDataWith(Map.of(SmbClient.SMB_OWNER_ATTRIBUTES, new String[] { "alice", "CORP" }), null, Map.of(), null);
        assertEquals("alice", dataMap.get("owner"));
        // no last author in the document metadata, so the owner is the fallback
        assertEquals("alice", dataMap.get("last_modifier"));
    }

    @Test
    public void test_generateData_smb1Owner_indexesAccountName() {
        final Map<String, Object> dataMap = generateDataWith(
                Map.of(org.codelibs.fess.crawler.client.smb1.SmbClient.SMB_OWNER_ATTRIBUTES, new String[] { "bob", "CORP" }), null,
                Map.of(), null);
        assertEquals("bob", dataMap.get("owner"));
    }

    @Test
    public void test_generateData_fileOwner_removesDomainPrefix() {
        final Map<String, Object> dataMap = generateDataWith(Map.of(FileSystemClient.FS_FILE_USER, "CORP\\carol"), null, Map.of(), null);
        assertEquals("carol", dataMap.get("owner"));
    }

    @Test
    public void test_generateData_posixFileOwner() {
        assertEquals("dave", generateDataWith(Map.of(FileSystemClient.FS_FILE_USER, "dave"), null, Map.of(), null).get("owner"));
    }

    @Test
    public void test_generateData_ftpOwner() {
        assertEquals("erin", generateDataWith(Map.of(FtpClient.FTP_FILE_USER, "erin"), null, Map.of(), null).get("owner"));
    }

    @Test
    public void test_generateData_blankOwner_noOwnerField() {
        final Map<String, Object> dataMap = generateDataWith(Map.of(FileSystemClient.FS_FILE_USER, " "), null, Map.of(), null);
        assertFalse(dataMap.containsKey("owner"));
        assertFalse(dataMap.containsKey("last_modifier"));
    }

    @Test
    public void test_generateData_noOwnerMetadata_noOwnerFields() {
        final Map<String, Object> dataMap = generateDataWithHeaders(Map.of());
        assertFalse(dataMap.containsKey("owner"));
        assertFalse(dataMap.containsKey("last_modifier"));
    }

    @Test
    public void test_generateData_lastAuthorMetadata_isLastModifier() {
        final Map<String, Object> dataMap = generateDataWith(Map.of(FileSystemClient.FS_FILE_USER, "dave"), null,
                Map.of("meta:last-author", new String[] { "", " Taro Yamada " }), null);
        assertEquals("dave", dataMap.get("owner"));
        assertEquals("Taro Yamada", dataMap.get("last_modifier"));
    }

    @Test
    public void test_generateData_lastAuthorMetadata_withoutOwner() {
        final Map<String, Object> dataMap =
                generateDataWith(Map.of(), null, Map.of("meta:last-author", new String[] { "Taro Yamada" }), null);
        assertFalse(dataMap.containsKey("owner"));
        assertEquals("Taro Yamada", dataMap.get("last_modifier"));
    }

    @Test
    public void test_generateData_ownerDisabledByConfigParameter() {
        final Map<String, Object> dataMap =
                generateDataWith(Map.of(FileSystemClient.FS_FILE_USER, "dave"), "config.owner.enabled=false", Map.of(), null);
        assertFalse(dataMap.containsKey("owner"));
        // a disabled owner is not used as the fallback either
        assertFalse(dataMap.containsKey("last_modifier"));
    }

    @Test
    public void test_generateData_ownerDisabled_lastAuthorStillIndexed() {
        final Map<String, Object> dataMap = generateDataWith(Map.of(FileSystemClient.FS_FILE_USER, "dave"), "config.owner.enabled=false",
                Map.of("meta:last-author", new String[] { "Taro Yamada" }), null);
        assertFalse(dataMap.containsKey("owner"));
        assertEquals("Taro Yamada", dataMap.get("last_modifier"));
    }

    @Test
    public void test_generateData_lastModifierDisabledByConfigParameter() {
        final Map<String, Object> dataMap = generateDataWith(Map.of(FileSystemClient.FS_FILE_USER, "dave"),
                "config.last.modifier.enabled=false", Map.of("meta:last-author", new String[] { "Taro Yamada" }), null);
        assertEquals("dave", dataMap.get("owner"));
        assertFalse(dataMap.containsKey("last_modifier"));
    }

    @Test
    public void test_generateData_disabledByFessConfig() {
        final FessConfig fessConfig = createFessConfig(false, false, Map.of());
        final Map<String, Object> dataMap = generateDataWith(Map.of(FileSystemClient.FS_FILE_USER, "dave"), null,
                Map.of("meta:last-author", new String[] { "Taro Yamada" }), fessConfig);
        assertFalse(dataMap.containsKey("owner"));
        assertFalse(dataMap.containsKey("last_modifier"));
    }

    @Test
    public void test_generateData_configParameterOverridesFessConfig() {
        final FessConfig fessConfig = createFessConfig(false, false, Map.of());
        final Map<String, Object> dataMap = generateDataWith(Map.of(FileSystemClient.FS_FILE_USER, "dave"),
                "config.owner.enabled=true\nconfig.last.modifier.enabled=true", Map.of(), fessConfig);
        assertEquals("dave", dataMap.get("owner"));
        assertEquals("dave", dataMap.get("last_modifier"));
    }

    @Test
    public void test_generateData_mappedMetadataIsKept() {
        final FessConfig fessConfig =
                createFessConfig(true, true, Map.of("dc:creator", new Tuple3<>("owner", Constants.MAPPING_TYPE_STRING, null),
                        "custom:modifier", new Tuple3<>("last_modifier", Constants.MAPPING_TYPE_STRING, null)));
        final Map<String, Object> dataMap = generateDataWith(Map.of(FileSystemClient.FS_FILE_USER, "dave"), null,
                Map.of("dc:creator", new String[] { "Hanako" }, "custom:modifier", new String[] { "Jiro" }), fessConfig);
        assertEquals("Hanako", dataMap.get("owner"));
        assertEquals("Jiro", dataMap.get("last_modifier"));
    }

    private static FessConfig createFessConfig(final boolean ownerEnabled, final boolean lastModifierEnabled,
            final Map<String, Tuple3<String, String, String>> metadataNameMappings) {
        // Delegate every call to the real configuration except the ones under test.
        final FessConfig real = ComponentUtil.getFessConfig();
        return (FessConfig) Proxy.newProxyInstance(FessConfig.class.getClassLoader(), new Class<?>[] { FessConfig.class },
                (proxy, method, args) -> {
                    switch (method.getName()) {
                    case "isCrawlerDocumentFileOwnerEnabled":
                        return ownerEnabled;
                    case "isCrawlerDocumentFileLastModifierEnabled":
                        return lastModifierEnabled;
                    case "getCrawlerMetadataNameMapping":
                        return metadataNameMappings.get(args[0]);
                    default:
                        try {
                            return method.invoke(real, args);
                        } catch (final InvocationTargetException e) {
                            throw e.getCause();
                        }
                    }
                });
    }

    @Test
    public void test_normalizeOwner() {
        final TestableAbstractFessFileTransformer transformer = new TestableAbstractFessFileTransformer();
        assertNull(transformer.normalizeOwner(null));
        assertNull(transformer.normalizeOwner(""));
        assertNull(transformer.normalizeOwner("CORP\\"));
        assertEquals("alice", transformer.normalizeOwner(" alice "));
        assertEquals("alice", transformer.normalizeOwner("CORP\\alice"));
        assertEquals("1000", transformer.normalizeOwner("1000"));
    }

    @Test
    public void test_isConfigEnabled() {
        final TestableAbstractFessFileTransformer transformer = new TestableAbstractFessFileTransformer();
        assertTrue(transformer.isConfigEnabled(null, "owner.enabled", true));
        assertFalse(transformer.isConfigEnabled(Map.of(), "owner.enabled", false));
        assertFalse(transformer.isConfigEnabled(Map.of("owner.enabled", "false"), "owner.enabled", true));
        assertTrue(transformer.isConfigEnabled(Map.of("owner.enabled", " TRUE "), "owner.enabled", false));
        assertTrue(transformer.isConfigEnabled(Map.of("owner.enabled", " "), "owner.enabled", true));
    }

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
