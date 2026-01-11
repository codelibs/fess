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
package org.codelibs.fess.helper;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.core.io.FileUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.DataConfigService;
import org.codelibs.fess.app.service.FileConfigService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.opensearch.config.cbean.DataConfigCB;
import org.codelibs.fess.opensearch.config.cbean.FailureUrlCB;
import org.codelibs.fess.opensearch.config.cbean.FileConfigCB;
import org.codelibs.fess.opensearch.config.cbean.WebConfigCB;
import org.codelibs.fess.opensearch.config.exbhv.DataConfigBhv;
import org.codelibs.fess.opensearch.config.exbhv.FailureUrlBhv;
import org.codelibs.fess.opensearch.config.exbhv.FileConfigBhv;
import org.codelibs.fess.opensearch.config.exbhv.WebConfigBhv;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.ConfigType;
import org.codelibs.fess.opensearch.config.exentity.DataConfig;
import org.codelibs.fess.opensearch.config.exentity.FailureUrl;
import org.codelibs.fess.opensearch.config.exentity.FileConfig;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.optional.OptionalEntity;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.search.sort.FieldSortBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class CrawlingConfigHelperTest extends UnitFessTestCase {
    private CrawlingConfigHelper crawlingConfigHelper;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        final File propFile = File.createTempFile("system", ".properties");
        propFile.deleteOnExit();
        FileUtil.writeBytes(propFile.getAbsolutePath(), "".getBytes());
        ComponentUtil.register(new DynamicProperties(propFile), "systemProperties");
        crawlingConfigHelper = new CrawlingConfigHelper();
        crawlingConfigHelper.init();
        ComponentUtil.register(crawlingConfigHelper, "crawlingConfigHelper");
        ComponentUtil.register(new WebConfigService() {
            @Override
            public OptionalEntity<WebConfig> getWebConfig(final String id) {
                final WebConfig webConfig = new WebConfig();
                webConfig.setId(id);
                if (id.endsWith("P")) {
                    webConfig.setConfigParameter("config.pipeline=wp");
                }
                return OptionalEntity.of(webConfig);
            }

            @Override
            public OptionalEntity<WebConfig> getWebConfigByName(String name) {
                final WebConfig webConfig = new WebConfig();
                webConfig.setId("01T");
                webConfig.setName("__TEMPLATE__");
                return OptionalEntity.of(webConfig);
            }
        }, WebConfigService.class.getCanonicalName());
        ComponentUtil.register(new FileConfigService() {
            @Override
            public OptionalEntity<FileConfig> getFileConfig(final String id) {
                final FileConfig fileConfig = new FileConfig();
                fileConfig.setId(id);
                if (id.endsWith("P")) {
                    fileConfig.setConfigParameter("config.pipeline=fp");
                }
                return OptionalEntity.of(fileConfig);
            }

            @Override
            public OptionalEntity<FileConfig> getFileConfigByName(String name) {
                final FileConfig fileConfig = new FileConfig();
                fileConfig.setId("11T");
                fileConfig.setName("__TEMPLATE__");
                return OptionalEntity.of(fileConfig);
            }
        }, FileConfigService.class.getCanonicalName());
        ComponentUtil.register(new DataConfigService() {
            @Override
            public OptionalEntity<DataConfig> getDataConfig(final String id) {
                final DataConfig dataConfig = new DataConfig();
                dataConfig.setId(id);
                if (id.endsWith("P")) {
                    dataConfig.setHandlerParameter("config.pipeline=dp");
                }
                return OptionalEntity.of(dataConfig);
            }

            @Override
            public OptionalEntity<DataConfig> getDataConfigByName(String name) {
                final DataConfig dataConfig = new DataConfig();
                dataConfig.setId("21T");
                dataConfig.setName("__TEMPLATE__");
                return OptionalEntity.of(dataConfig);
            }
        }, DataConfigService.class.getCanonicalName());
    }

    @Test
    public void test_getConfigType() {
        assertNull(crawlingConfigHelper.getConfigType(null));
        assertNull(crawlingConfigHelper.getConfigType(""));
        assertNull(crawlingConfigHelper.getConfigType("XXX"));
        assertNull(crawlingConfigHelper.getConfigType("W"));
        assertNull(crawlingConfigHelper.getConfigType("F"));
        assertNull(crawlingConfigHelper.getConfigType("D"));
        assertEquals(ConfigType.WEB, crawlingConfigHelper.getConfigType("WX"));
        assertEquals(ConfigType.FILE, crawlingConfigHelper.getConfigType("FX"));
        assertEquals(ConfigType.DATA, crawlingConfigHelper.getConfigType("DX"));
    }

    @Test
    public void test_getCrawlingConfig() {
        crawlingConfigHelper.refresh();
        assertNull(crawlingConfigHelper.getCrawlingConfig(null));
        assertNull(crawlingConfigHelper.getCrawlingConfig(""));
        assertNull(crawlingConfigHelper.getCrawlingConfig("XXX"));
        final CrawlingConfig webConfig = crawlingConfigHelper.getCrawlingConfig("W01");
        assertEquals("01", webConfig.getId());
        assertTrue(webConfig instanceof WebConfig);
        final CrawlingConfig fileConfig = crawlingConfigHelper.getCrawlingConfig("F11");
        assertEquals("11", fileConfig.getId());
        assertTrue(fileConfig instanceof FileConfig);
        final CrawlingConfig dataConfig = crawlingConfigHelper.getCrawlingConfig("D21");
        assertEquals("21", dataConfig.getId());
        assertTrue(dataConfig instanceof DataConfig);
    }

    @Test
    public void test_getPipeline() {
        crawlingConfigHelper.refresh();
        assertTrue(crawlingConfigHelper.getPipeline(null).isEmpty());
        assertTrue(crawlingConfigHelper.getPipeline("").isEmpty());
        assertTrue(crawlingConfigHelper.getPipeline("XXX").isEmpty());
        assertTrue(crawlingConfigHelper.getPipeline("W1").isEmpty());
        assertTrue(crawlingConfigHelper.getPipeline("F1").isEmpty());
        assertTrue(crawlingConfigHelper.getPipeline("D1").isEmpty());
        assertEquals("wp", crawlingConfigHelper.getPipeline("W1P").get());
        assertEquals("fp", crawlingConfigHelper.getPipeline("F1P").get());
        assertEquals("dp", crawlingConfigHelper.getPipeline("D1P").get());
    }

    @Test
    public void test_sessionCountId() {
        final String sessionId = "12345";
        final String sessionCountId = sessionId + "-1";
        assertNull(crawlingConfigHelper.get(sessionCountId));
        crawlingConfigHelper.store(sessionId, crawlingConfigHelper.getCrawlingConfig("W1"));
        assertEquals("1", crawlingConfigHelper.get(sessionCountId).getId());
        crawlingConfigHelper.remove(sessionCountId);
        assertNull(crawlingConfigHelper.get(sessionCountId));
    }

    @Test
    public void test_getAllWebConfigList() {
        final WebConfigCB cb = new WebConfigCB();
        ComponentUtil.register(new WebConfigBhv() {
            @Override
            public ListResultBean<WebConfig> selectList(final CBCall<WebConfigCB> cbLambda) {
                cbLambda.callback(cb);
                final ListResultBean<WebConfig> list = new ListResultBean<>();
                list.add((WebConfig) crawlingConfigHelper.getCrawlingConfig("W1"));
                list.add((WebConfig) crawlingConfigHelper.getCrawlingConfig("W2"));
                list.add((WebConfig) crawlingConfigHelper.getCrawlingConfig("W3"));
                return list;
            }
        }, WebConfigBhv.class.getCanonicalName());
        final List<WebConfig> configList = crawlingConfigHelper.getAllWebConfigList();
        assertEquals(3, configList.size());
        final List<QueryBuilder> queryBuilderList = cb.query().getQueryBuilderList();
        assertEquals(2, queryBuilderList.size());
        assertEquals("TermQueryBuilder", queryBuilderList.get(0).getClass().getSimpleName());
        assertEquals("BoolQueryBuilder", queryBuilderList.get(1).getClass().getSimpleName());
        final List<FieldSortBuilder> fieldSortBuilderList = cb.query().getFieldSortBuilderList();
        assertEquals(2, fieldSortBuilderList.size());
        assertEquals("sortOrder", fieldSortBuilderList.get(0).getFieldName());
        assertEquals("name", fieldSortBuilderList.get(1).getFieldName());
    }

    @Test
    public void test_getWebConfigListByIds_null() {
        final WebConfigCB cb = new WebConfigCB();
        ComponentUtil.register(new WebConfigBhv() {
            @Override
            public ListResultBean<WebConfig> selectList(final CBCall<WebConfigCB> cbLambda) {
                cbLambda.callback(cb);
                final ListResultBean<WebConfig> list = new ListResultBean<>();
                list.add((WebConfig) crawlingConfigHelper.getCrawlingConfig("W1"));
                list.add((WebConfig) crawlingConfigHelper.getCrawlingConfig("W2"));
                list.add((WebConfig) crawlingConfigHelper.getCrawlingConfig("W3"));
                return list;
            }
        }, WebConfigBhv.class.getCanonicalName());
        final List<WebConfig> configList = crawlingConfigHelper.getWebConfigListByIds(null);
        assertEquals(3, configList.size());
        final List<QueryBuilder> queryBuilderList = cb.query().getQueryBuilderList();
        assertEquals(2, queryBuilderList.size());
        assertEquals("TermQueryBuilder", queryBuilderList.get(0).getClass().getSimpleName());
        assertEquals("BoolQueryBuilder", queryBuilderList.get(1).getClass().getSimpleName());
        final List<FieldSortBuilder> fieldSortBuilderList = cb.query().getFieldSortBuilderList();
        assertEquals(2, fieldSortBuilderList.size());
        assertEquals("sortOrder", fieldSortBuilderList.get(0).getFieldName());
        assertEquals("name", fieldSortBuilderList.get(1).getFieldName());
    }

    @Test
    public void test_getWebConfigListByIds() {
        final WebConfigCB cb = new WebConfigCB();
        ComponentUtil.register(new WebConfigBhv() {
            @Override
            public ListResultBean<WebConfig> selectList(final CBCall<WebConfigCB> cbLambda) {
                cbLambda.callback(cb);
                final ListResultBean<WebConfig> list = new ListResultBean<>();
                list.add((WebConfig) crawlingConfigHelper.getCrawlingConfig("W1"));
                list.add((WebConfig) crawlingConfigHelper.getCrawlingConfig("W2"));
                list.add((WebConfig) crawlingConfigHelper.getCrawlingConfig("W3"));
                return list;
            }
        }, WebConfigBhv.class.getCanonicalName());
        final List<WebConfig> configList = crawlingConfigHelper.getWebConfigListByIds(List.of("W1", "W2", "W3"));
        assertEquals(3, configList.size());
        final List<QueryBuilder> queryBuilderList = cb.query().getQueryBuilderList();
        assertEquals(2, queryBuilderList.size());
        assertEquals("IdsQueryBuilder", queryBuilderList.get(0).getClass().getSimpleName());
        assertEquals("BoolQueryBuilder", queryBuilderList.get(1).getClass().getSimpleName());
        final List<FieldSortBuilder> fieldSortBuilderList = cb.query().getFieldSortBuilderList();
        assertEquals(2, fieldSortBuilderList.size());
        assertEquals("sortOrder", fieldSortBuilderList.get(0).getFieldName());
        assertEquals("name", fieldSortBuilderList.get(1).getFieldName());
    }

    @Test
    public void test_getAllFileConfigList() {
        final FileConfigCB cb = new FileConfigCB();
        ComponentUtil.register(new FileConfigBhv() {
            @Override
            public ListResultBean<FileConfig> selectList(final CBCall<FileConfigCB> cbLambda) {
                cbLambda.callback(cb);
                final ListResultBean<FileConfig> list = new ListResultBean<>();
                list.add((FileConfig) crawlingConfigHelper.getCrawlingConfig("F1"));
                list.add((FileConfig) crawlingConfigHelper.getCrawlingConfig("F2"));
                list.add((FileConfig) crawlingConfigHelper.getCrawlingConfig("F3"));
                return list;
            }
        }, FileConfigBhv.class.getCanonicalName());
        final List<FileConfig> configList = crawlingConfigHelper.getAllFileConfigList();
        assertEquals(3, configList.size());
        final List<QueryBuilder> queryBuilderList = cb.query().getQueryBuilderList();
        assertEquals(2, queryBuilderList.size());
        assertEquals("TermQueryBuilder", queryBuilderList.get(0).getClass().getSimpleName());
        assertEquals("BoolQueryBuilder", queryBuilderList.get(1).getClass().getSimpleName());
        final List<FieldSortBuilder> fieldSortBuilderList = cb.query().getFieldSortBuilderList();
        assertEquals(2, fieldSortBuilderList.size());
        assertEquals("sortOrder", fieldSortBuilderList.get(0).getFieldName());
        assertEquals("name", fieldSortBuilderList.get(1).getFieldName());
    }

    @Test
    public void test_getFileConfigListByIds_null() {
        final FileConfigCB cb = new FileConfigCB();
        ComponentUtil.register(new FileConfigBhv() {
            @Override
            public ListResultBean<FileConfig> selectList(final CBCall<FileConfigCB> cbLambda) {
                cbLambda.callback(cb);
                final ListResultBean<FileConfig> list = new ListResultBean<>();
                list.add((FileConfig) crawlingConfigHelper.getCrawlingConfig("F1"));
                list.add((FileConfig) crawlingConfigHelper.getCrawlingConfig("F2"));
                list.add((FileConfig) crawlingConfigHelper.getCrawlingConfig("F3"));
                return list;
            }
        }, FileConfigBhv.class.getCanonicalName());
        final List<FileConfig> configList = crawlingConfigHelper.getFileConfigListByIds(null);
        assertEquals(3, configList.size());
        final List<QueryBuilder> queryBuilderList = cb.query().getQueryBuilderList();
        assertEquals(2, queryBuilderList.size());
        assertEquals("TermQueryBuilder", queryBuilderList.get(0).getClass().getSimpleName());
        assertEquals("BoolQueryBuilder", queryBuilderList.get(1).getClass().getSimpleName());
        final List<FieldSortBuilder> fieldSortBuilderList = cb.query().getFieldSortBuilderList();
        assertEquals(2, fieldSortBuilderList.size());
        assertEquals("sortOrder", fieldSortBuilderList.get(0).getFieldName());
        assertEquals("name", fieldSortBuilderList.get(1).getFieldName());
    }

    @Test
    public void test_getFileConfigListByIds() {
        final FileConfigCB cb = new FileConfigCB();
        ComponentUtil.register(new FileConfigBhv() {
            @Override
            public ListResultBean<FileConfig> selectList(final CBCall<FileConfigCB> cbLambda) {
                cbLambda.callback(cb);
                final ListResultBean<FileConfig> list = new ListResultBean<>();
                list.add((FileConfig) crawlingConfigHelper.getCrawlingConfig("F1"));
                list.add((FileConfig) crawlingConfigHelper.getCrawlingConfig("F2"));
                list.add((FileConfig) crawlingConfigHelper.getCrawlingConfig("F3"));
                return list;
            }
        }, FileConfigBhv.class.getCanonicalName());
        final List<FileConfig> configList = crawlingConfigHelper.getFileConfigListByIds(List.of("F1", "F2", "F3"));
        assertEquals(3, configList.size());
        final List<QueryBuilder> queryBuilderList = cb.query().getQueryBuilderList();
        assertEquals(2, queryBuilderList.size());
        assertEquals("IdsQueryBuilder", queryBuilderList.get(0).getClass().getSimpleName());
        assertEquals("BoolQueryBuilder", queryBuilderList.get(1).getClass().getSimpleName());
        final List<FieldSortBuilder> fieldSortBuilderList = cb.query().getFieldSortBuilderList();
        assertEquals(2, fieldSortBuilderList.size());
        assertEquals("sortOrder", fieldSortBuilderList.get(0).getFieldName());
        assertEquals("name", fieldSortBuilderList.get(1).getFieldName());
    }

    @Test
    public void test_getAllDataConfigList() {
        final DataConfigCB cb = new DataConfigCB();
        ComponentUtil.register(new DataConfigBhv() {
            @Override
            public ListResultBean<DataConfig> selectList(final CBCall<DataConfigCB> cbLambda) {
                cbLambda.callback(cb);
                final ListResultBean<DataConfig> list = new ListResultBean<>();
                list.add((DataConfig) crawlingConfigHelper.getCrawlingConfig("D1"));
                list.add((DataConfig) crawlingConfigHelper.getCrawlingConfig("D2"));
                list.add((DataConfig) crawlingConfigHelper.getCrawlingConfig("D3"));
                return list;
            }
        }, DataConfigBhv.class.getCanonicalName());
        final List<DataConfig> configList = crawlingConfigHelper.getAllDataConfigList();
        assertEquals(3, configList.size());
        final List<QueryBuilder> queryBuilderList = cb.query().getQueryBuilderList();
        assertEquals(2, queryBuilderList.size());
        assertEquals("TermQueryBuilder", queryBuilderList.get(0).getClass().getSimpleName());
        assertEquals("BoolQueryBuilder", queryBuilderList.get(1).getClass().getSimpleName());
        final List<FieldSortBuilder> fieldSortBuilderList = cb.query().getFieldSortBuilderList();
        assertEquals(2, fieldSortBuilderList.size());
        assertEquals("sortOrder", fieldSortBuilderList.get(0).getFieldName());
        assertEquals("name", fieldSortBuilderList.get(1).getFieldName());
    }

    @Test
    public void test_getDataConfigListByIds_null() {
        final DataConfigCB cb = new DataConfigCB();
        ComponentUtil.register(new DataConfigBhv() {
            @Override
            public ListResultBean<DataConfig> selectList(final CBCall<DataConfigCB> cbLambda) {
                cbLambda.callback(cb);
                final ListResultBean<DataConfig> list = new ListResultBean<>();
                list.add((DataConfig) crawlingConfigHelper.getCrawlingConfig("D1"));
                list.add((DataConfig) crawlingConfigHelper.getCrawlingConfig("D2"));
                list.add((DataConfig) crawlingConfigHelper.getCrawlingConfig("D3"));
                return list;
            }
        }, DataConfigBhv.class.getCanonicalName());
        final List<DataConfig> configList = crawlingConfigHelper.getDataConfigListByIds(null);
        assertEquals(3, configList.size());
        final List<QueryBuilder> queryBuilderList = cb.query().getQueryBuilderList();
        assertEquals(2, queryBuilderList.size());
        assertEquals("TermQueryBuilder", queryBuilderList.get(0).getClass().getSimpleName());
        assertEquals("BoolQueryBuilder", queryBuilderList.get(1).getClass().getSimpleName());
        final List<FieldSortBuilder> fieldSortBuilderList = cb.query().getFieldSortBuilderList();
        assertEquals(2, fieldSortBuilderList.size());
        assertEquals("sortOrder", fieldSortBuilderList.get(0).getFieldName());
        assertEquals("name", fieldSortBuilderList.get(1).getFieldName());
    }

    @Test
    public void test_getDataConfigListByIds() {
        final DataConfigCB cb = new DataConfigCB();
        ComponentUtil.register(new DataConfigBhv() {
            @Override
            public ListResultBean<DataConfig> selectList(final CBCall<DataConfigCB> cbLambda) {
                cbLambda.callback(cb);
                final ListResultBean<DataConfig> list = new ListResultBean<>();
                list.add((DataConfig) crawlingConfigHelper.getCrawlingConfig("D1"));
                list.add((DataConfig) crawlingConfigHelper.getCrawlingConfig("D2"));
                list.add((DataConfig) crawlingConfigHelper.getCrawlingConfig("D3"));
                return list;
            }
        }, DataConfigBhv.class.getCanonicalName());
        final List<DataConfig> configList = crawlingConfigHelper.getDataConfigListByIds(List.of("D1", "D2", "D3"));
        assertEquals(3, configList.size());
        final List<QueryBuilder> queryBuilderList = cb.query().getQueryBuilderList();
        assertEquals(2, queryBuilderList.size());
        assertEquals("IdsQueryBuilder", queryBuilderList.get(0).getClass().getSimpleName());
        assertEquals("BoolQueryBuilder", queryBuilderList.get(1).getClass().getSimpleName());
        final List<FieldSortBuilder> fieldSortBuilderList = cb.query().getFieldSortBuilderList();
        assertEquals(2, fieldSortBuilderList.size());
        assertEquals("sortOrder", fieldSortBuilderList.get(0).getFieldName());
        assertEquals("name", fieldSortBuilderList.get(1).getFieldName());
    }

    @Test
    public void test_getExcludedUrlList() {
        final AtomicInteger errorCount = new AtomicInteger(0);
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        final FailureUrlCB cb = new FailureUrlCB();
        ComponentUtil.register(new FailureUrlBhv() {
            @Override
            public ListResultBean<FailureUrl> selectList(final CBCall<FailureUrlCB> cbLambda) {
                cbLambda.callback(cb);
                final ListResultBean<FailureUrl> list = new ListResultBean<>();
                for (int i = 0; i < errorCount.get(); i++) {
                    FailureUrl failureUrl = new FailureUrl();
                    failureUrl.setId("E" + i);
                    failureUrl.setUrl("http://test.com/" + i + ".html");
                    failureUrl.setErrorCount(i);
                    failureUrl.setErrorName("TestError" + i);
                    list.add(failureUrl);
                }
                return list;
            }
        }, FailureUrlBhv.class.getCanonicalName());

        assertEquals(0, crawlingConfigHelper.getExcludedUrlList("123").size());
        systemProperties.setProperty(Constants.FAILURE_COUNT_THRESHOLD_PROPERTY, "0");
        assertEquals(0, crawlingConfigHelper.getExcludedUrlList("123").size());
        errorCount.set(5);
        assertEquals(5, crawlingConfigHelper.getExcludedUrlList("123").size());
        systemProperties.setProperty(Constants.IGNORE_FAILURE_TYPE_PROPERTY, "TestError0");
        assertEquals(4, crawlingConfigHelper.getExcludedUrlList("123").size());
    }

    @Test
    public void test_getDefaultConfig() {
        assertTrue(crawlingConfigHelper.getDefaultConfig(null).isEmpty());
        assertEquals("01T", crawlingConfigHelper.getDefaultConfig(ConfigType.WEB).get().getId());
        assertEquals("11T", crawlingConfigHelper.getDefaultConfig(ConfigType.FILE).get().getId());
        assertEquals("21T", crawlingConfigHelper.getDefaultConfig(ConfigType.DATA).get().getId());
    }

    @Test
    public void test_getId() {
        // Test getId method through reflection since it's protected
        try {
            java.lang.reflect.Method getIdMethod = CrawlingConfigHelper.class.getDeclaredMethod("getId", String.class);
            getIdMethod.setAccessible(true);

            assertNull(getIdMethod.invoke(crawlingConfigHelper, (String) null));
            assertNull(getIdMethod.invoke(crawlingConfigHelper, ""));
            assertNull(getIdMethod.invoke(crawlingConfigHelper, "W"));
            assertEquals("123", getIdMethod.invoke(crawlingConfigHelper, "W123"));
            assertEquals("abc", getIdMethod.invoke(crawlingConfigHelper, "Fabc"));
            assertEquals("xyz", getIdMethod.invoke(crawlingConfigHelper, "Dxyz"));
        } catch (Exception e) {
            fail("Failed to test getId method: " + e.getMessage());
        }
    }

    @Test
    public void test_getCrawlingConfig_cacheExceptionHandling() {
        // Register a service that throws exception
        ComponentUtil.register(new WebConfigService() {
            @Override
            public OptionalEntity<WebConfig> getWebConfig(final String id) {
                throw new RuntimeException("Test exception");
            }

            @Override
            public OptionalEntity<WebConfig> getWebConfigByName(String name) {
                return OptionalEntity.empty();
            }
        }, WebConfigService.class.getCanonicalName());

        // Should return null when exception occurs
        assertNull(crawlingConfigHelper.getCrawlingConfig("W123"));
    }

    @Test
    public void test_store_counterIncrement() {
        final String sessionId = "testSession";

        // Test counter increment
        String sessionCountId1 = crawlingConfigHelper.store(sessionId, crawlingConfigHelper.getCrawlingConfig("W1"));
        assertEquals("testSession-1", sessionCountId1);

        String sessionCountId2 = crawlingConfigHelper.store(sessionId, crawlingConfigHelper.getCrawlingConfig("W2"));
        assertEquals("testSession-2", sessionCountId2);

        String sessionCountId3 = crawlingConfigHelper.store(sessionId, crawlingConfigHelper.getCrawlingConfig("W3"));
        assertEquals("testSession-3", sessionCountId3);

        // Verify stored configs
        assertEquals("1", crawlingConfigHelper.get(sessionCountId1).getId());
        assertEquals("2", crawlingConfigHelper.get(sessionCountId2).getId());
        assertEquals("3", crawlingConfigHelper.get(sessionCountId3).getId());
    }

    @Test
    public void test_getAllWebConfigList_withDifferentFlags() {
        final WebConfigCB cb = new WebConfigCB();
        ComponentUtil.register(new WebConfigBhv() {
            @Override
            public ListResultBean<WebConfig> selectList(final CBCall<WebConfigCB> cbLambda) {
                cbLambda.callback(cb);
                final ListResultBean<WebConfig> list = new ListResultBean<>();
                list.add((WebConfig) crawlingConfigHelper.getCrawlingConfig("W1"));
                return list;
            }
        }, WebConfigBhv.class.getCanonicalName());

        // Test with available=false (should not add available query)
        List<WebConfig> configList = crawlingConfigHelper.getAllWebConfigList(true, true, false, null);
        assertEquals(1, configList.size());

        // Test with idList parameter
        List<String> idList = List.of("W1", "W2");
        configList = crawlingConfigHelper.getAllWebConfigList(true, true, true, idList);
        assertEquals(1, configList.size());
    }

    @Test
    public void test_getAllFileConfigList_withDifferentFlags() {
        final FileConfigCB cb = new FileConfigCB();
        ComponentUtil.register(new FileConfigBhv() {
            @Override
            public ListResultBean<FileConfig> selectList(final CBCall<FileConfigCB> cbLambda) {
                cbLambda.callback(cb);
                final ListResultBean<FileConfig> list = new ListResultBean<>();
                list.add((FileConfig) crawlingConfigHelper.getCrawlingConfig("F1"));
                return list;
            }
        }, FileConfigBhv.class.getCanonicalName());

        // Test with available=false
        List<FileConfig> configList = crawlingConfigHelper.getAllFileConfigList(true, true, false, null);
        assertEquals(1, configList.size());

        // Test with idList parameter
        List<String> idList = List.of("F1", "F2");
        configList = crawlingConfigHelper.getAllFileConfigList(true, true, true, idList);
        assertEquals(1, configList.size());
    }

    @Test
    public void test_getAllDataConfigList_withDifferentFlags() {
        final DataConfigCB cb = new DataConfigCB();
        ComponentUtil.register(new DataConfigBhv() {
            @Override
            public ListResultBean<DataConfig> selectList(final CBCall<DataConfigCB> cbLambda) {
                cbLambda.callback(cb);
                final ListResultBean<DataConfig> list = new ListResultBean<>();
                list.add((DataConfig) crawlingConfigHelper.getCrawlingConfig("D1"));
                return list;
            }
        }, DataConfigBhv.class.getCanonicalName());

        // Test with available=false
        List<DataConfig> configList = crawlingConfigHelper.getAllDataConfigList(true, true, false, null);
        assertEquals(1, configList.size());

        // Test with idList parameter
        List<String> idList = List.of("D1", "D2");
        configList = crawlingConfigHelper.getAllDataConfigList(true, true, true, idList);
        assertEquals(1, configList.size());
    }

    @Test
    public void test_getExcludedUrlList_withNegativeFailureCount() {
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        systemProperties.setProperty(Constants.FAILURE_COUNT_THRESHOLD_PROPERTY, "-1");

        List<String> excludedUrls = crawlingConfigHelper.getExcludedUrlList("123");
        assertEquals(0, excludedUrls.size());
    }

    @Test
    public void test_getExcludedUrlList_withIgnoreFailureTypePattern() {
        final AtomicInteger errorCount = new AtomicInteger(3);
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        final FailureUrlCB cb = new FailureUrlCB();

        ComponentUtil.register(new FailureUrlBhv() {
            @Override
            public ListResultBean<FailureUrl> selectList(final CBCall<FailureUrlCB> cbLambda) {
                cbLambda.callback(cb);
                final ListResultBean<FailureUrl> list = new ListResultBean<>();
                for (int i = 0; i < errorCount.get(); i++) {
                    FailureUrl failureUrl = new FailureUrl();
                    failureUrl.setId("E" + i);
                    failureUrl.setUrl("http://test.com/" + i + ".html");
                    failureUrl.setErrorCount(i + 1);
                    // Mix different error names
                    if (i == 0) {
                        failureUrl.setErrorName("java.net.ConnectException");
                    } else if (i == 1) {
                        failureUrl.setErrorName("java.net.SocketTimeoutException");
                    } else {
                        failureUrl.setErrorName("java.io.IOException");
                    }
                    list.add(failureUrl);
                }
                return list;
            }
        }, FailureUrlBhv.class.getCanonicalName());

        systemProperties.setProperty(Constants.FAILURE_COUNT_THRESHOLD_PROPERTY, "1");
        systemProperties.setProperty(Constants.IGNORE_FAILURE_TYPE_PROPERTY, ".*ConnectException.*");

        List<String> excludedUrls = crawlingConfigHelper.getExcludedUrlList("123");
        // Should exclude ConnectException but include the other 2
        assertEquals(2, excludedUrls.size());
        assertTrue(excludedUrls.contains("http://test.com/1.html"));
        assertTrue(excludedUrls.contains("http://test.com/2.html"));
        assertFalse(excludedUrls.contains("http://test.com/0.html"));
    }

    @Test
    public void test_getExcludedUrlList_emptyList() {
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        final FailureUrlCB cb = new FailureUrlCB();

        ComponentUtil.register(new FailureUrlBhv() {
            @Override
            public ListResultBean<FailureUrl> selectList(final CBCall<FailureUrlCB> cbLambda) {
                cbLambda.callback(cb);
                return new ListResultBean<>(); // Empty list
            }
        }, FailureUrlBhv.class.getCanonicalName());

        systemProperties.setProperty(Constants.FAILURE_COUNT_THRESHOLD_PROPERTY, "1");

        List<String> excludedUrls = crawlingConfigHelper.getExcludedUrlList("123");
        assertEquals(0, excludedUrls.size());
    }

    @Test
    public void test_refresh_clearsCache() {
        // First get a config to cache it
        CrawlingConfig config1 = crawlingConfigHelper.getCrawlingConfig("W01");
        assertNotNull(config1);

        // Change the service to return different data
        ComponentUtil.register(new WebConfigService() {
            @Override
            public OptionalEntity<WebConfig> getWebConfig(final String id) {
                final WebConfig webConfig = new WebConfig();
                webConfig.setId(id + "_refreshed");
                return OptionalEntity.of(webConfig);
            }

            @Override
            public OptionalEntity<WebConfig> getWebConfigByName(String name) {
                return OptionalEntity.empty();
            }
        }, WebConfigService.class.getCanonicalName());

        // Without refresh, should still get cached version
        CrawlingConfig config2 = crawlingConfigHelper.getCrawlingConfig("W01");
        assertEquals("01", config2.getId());

        // After refresh, should get new version
        crawlingConfigHelper.refresh();
        CrawlingConfig config3 = crawlingConfigHelper.getCrawlingConfig("W01");
        assertEquals("01_refreshed", config3.getId());
    }

    @Test
    public void test_init_cacheConfiguration() {
        // Test that init properly configures the cache
        CrawlingConfigHelper newHelper = new CrawlingConfigHelper();
        newHelper.init();
        assertNotNull(newHelper);
        // Cache should be initialized and functional
        assertNull(newHelper.getCrawlingConfig("invalid"));
    }

    @Test
    public void test_getPipeline_withBlankPipeline() {
        // Register service that returns blank pipeline
        ComponentUtil.register(new WebConfigService() {
            @Override
            public OptionalEntity<WebConfig> getWebConfig(final String id) {
                final WebConfig webConfig = new WebConfig();
                webConfig.setId(id);
                webConfig.setConfigParameter("config.pipeline= "); // Blank space
                return OptionalEntity.of(webConfig);
            }

            @Override
            public OptionalEntity<WebConfig> getWebConfigByName(String name) {
                return OptionalEntity.empty();
            }
        }, WebConfigService.class.getCanonicalName());

        assertTrue(crawlingConfigHelper.getPipeline("W1").isEmpty());
    }

    @Test
    public void test_getPipeline_withEmptyStringPipeline() {
        // Register service that returns empty string pipeline
        ComponentUtil.register(new WebConfigService() {
            @Override
            public OptionalEntity<WebConfig> getWebConfig(final String id) {
                final WebConfig webConfig = new WebConfig();
                webConfig.setId(id);
                webConfig.setConfigParameter("config.pipeline="); // Empty string
                return OptionalEntity.of(webConfig);
            }

            @Override
            public OptionalEntity<WebConfig> getWebConfigByName(String name) {
                return OptionalEntity.empty();
            }
        }, WebConfigService.class.getCanonicalName());

        assertTrue(crawlingConfigHelper.getPipeline("W1").isEmpty());
    }
}
