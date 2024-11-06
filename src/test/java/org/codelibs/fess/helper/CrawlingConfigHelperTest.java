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

public class CrawlingConfigHelperTest extends UnitFessTestCase {
    private CrawlingConfigHelper crawlingConfigHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
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

    public void test_sessionCountId() {
        final String sessionId = "12345";
        final String sessionCountId = sessionId + "-1";
        assertNull(crawlingConfigHelper.get(sessionCountId));
        crawlingConfigHelper.store(sessionId, crawlingConfigHelper.getCrawlingConfig("W1"));
        assertEquals("1", crawlingConfigHelper.get(sessionCountId).getId());
        crawlingConfigHelper.remove(sessionCountId);
        assertNull(crawlingConfigHelper.get(sessionCountId));
    }

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

    public void test_getDefaultConfig() {
        assertTrue(crawlingConfigHelper.getDefaultConfig(null).isEmpty());
        assertEquals("01T", crawlingConfigHelper.getDefaultConfig(ConfigType.WEB).get().getId());
        assertEquals("11T", crawlingConfigHelper.getDefaultConfig(ConfigType.FILE).get().getId());
        assertEquals("21T", crawlingConfigHelper.getDefaultConfig(ConfigType.DATA).get().getId());
    }
}
