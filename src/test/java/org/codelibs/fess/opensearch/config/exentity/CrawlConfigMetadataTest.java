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

import java.lang.reflect.Field;
import java.util.Map;

import org.codelibs.fess.app.service.DataConfigService;
import org.codelibs.fess.app.service.FileConfigService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.config.exbhv.DataConfigBhv;
import org.codelibs.fess.opensearch.config.exbhv.FileConfigBhv;
import org.codelibs.fess.opensearch.config.exbhv.WebConfigBhv;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * The crawl state is kept in the {@code metadata} field of the crawl configuration. These tests pin
 * what that relies on: the field is read from and written to the document, a save from the admin
 * screen or the admin API keeps what is stored, and no form or body can carry it.
 */
public class CrawlConfigMetadataTest extends UnitFessTestCase {

    private static final String METADATA = "{\"lastCount\":61}";

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        ComponentUtil.register(new PermissionHelper(), "permissionHelper");
    }

    @Test
    public void test_entityWritesAndReadsTheMetadata() {
        final WebConfig web = new WebConfig();
        web.setMetadata(METADATA);
        assertEquals(METADATA, web.toSource().get("metadata"));
        final FileConfig file = new FileConfig();
        file.setMetadata(METADATA);
        assertEquals(METADATA, file.toSource().get("metadata"));
        final DataConfig data = new DataConfig();
        data.setMetadata(METADATA);
        assertEquals(METADATA, data.toSource().get("metadata"));

        assertFalse(new WebConfig().toSource().containsKey("metadata"));
        assertFalse(new FileConfig().toSource().containsKey("metadata"));
        assertFalse(new DataConfig().toSource().containsKey("metadata"));

        final Map<String, Object> source = Map.of("name", "n", "metadata", METADATA);
        assertEquals(METADATA, new WebConfigBhv() {
            WebConfig load() {
                return createEntity(source, WebConfig.class);
            }
        }.load().getMetadata());
        assertEquals(METADATA, new FileConfigBhv() {
            FileConfig load() {
                return createEntity(source, FileConfig.class);
            }
        }.load().getMetadata());
        assertEquals(METADATA, new DataConfigBhv() {
            DataConfig load() {
                return createEntity(source, DataConfig.class);
            }
        }.load().getMetadata());
    }

    @Test
    public void test_adminSaveOfAWebConfigKeepsTheMetadata() {
        final WebConfig stored = new WebConfig();
        stored.setId("1");
        stored.setName("old");
        stored.setMetadata(METADATA);
        ComponentUtil.register(new WebConfigService() {
            @Override
            public OptionalEntity<WebConfig> getWebConfig(final String id) {
                return OptionalEntity.of(stored);
            }
        }, WebConfigService.class.getCanonicalName());

        final org.codelibs.fess.app.web.admin.webconfig.EditForm form = new org.codelibs.fess.app.web.admin.webconfig.EditForm();
        form.crudMode = CrudMode.EDIT;
        form.id = "1";
        form.name = "new";
        form.urls = "http://localhost/";
        form.permissions = "";
        form.virtualHosts = "";
        final WebConfig saved = org.codelibs.fess.app.web.admin.webconfig.AdminWebconfigAction.getWebConfig(form).get();

        assertEquals("new", saved.getName());
        assertEquals(METADATA, saved.getMetadata());
        // the whole document the screen writes back still has it
        assertEquals(METADATA, saved.toSource().get("metadata"));
    }

    @Test
    public void test_adminSaveOfAFileConfigKeepsTheMetadata() {
        final FileConfig stored = new FileConfig();
        stored.setId("1");
        stored.setName("old");
        stored.setMetadata(METADATA);
        ComponentUtil.register(new FileConfigService() {
            @Override
            public OptionalEntity<FileConfig> getFileConfig(final String id) {
                return OptionalEntity.of(stored);
            }
        }, FileConfigService.class.getCanonicalName());

        final org.codelibs.fess.app.web.admin.fileconfig.EditForm form = new org.codelibs.fess.app.web.admin.fileconfig.EditForm();
        form.crudMode = CrudMode.EDIT;
        form.id = "1";
        form.name = "new";
        form.paths = "file:/tmp/";
        form.permissions = "";
        form.virtualHosts = "";
        final FileConfig saved = org.codelibs.fess.app.web.admin.fileconfig.AdminFileconfigAction.getFileConfig(form).get();

        assertEquals("new", saved.getName());
        assertEquals(METADATA, saved.toSource().get("metadata"));
    }

    @Test
    public void test_adminSaveOfADataConfigKeepsTheMetadata() {
        final DataConfig stored = new DataConfig();
        stored.setId("1");
        stored.setName("old");
        stored.setMetadata(METADATA);
        ComponentUtil.register(new DataConfigService() {
            @Override
            public OptionalEntity<DataConfig> getDataConfig(final String id) {
                return OptionalEntity.of(stored);
            }
        }, DataConfigService.class.getCanonicalName());

        final org.codelibs.fess.app.web.admin.dataconfig.EditForm form = new org.codelibs.fess.app.web.admin.dataconfig.EditForm();
        form.crudMode = CrudMode.EDIT;
        form.id = "1";
        form.name = "new";
        form.handlerName = "CsvDataStore";
        form.permissions = "";
        form.virtualHosts = "";
        final DataConfig saved = org.codelibs.fess.app.web.admin.dataconfig.AdminDataconfigAction.getDataConfig(form).get();

        assertEquals("new", saved.getName());
        assertEquals(METADATA, saved.toSource().get("metadata"));
    }

    @Test
    public void test_progressCounterIsRuntimeStateOnly() {
        final DataConfig data = new DataConfig();
        data.getProgressCounter().set(5);
        // it is not written to the index with the configuration
        assertEquals(new DataConfig().toSource(), data.toSource());
        // every entity has its own counter, as every crawl loads its own configurations
        assertEquals(0L, new DataConfig().getProgressCounter().get());
    }

    @Test
    public void test_noFormOrBodyCarriesTheMetadata() {
        final Class<?>[] classes = { org.codelibs.fess.app.web.admin.webconfig.CreateForm.class,
                org.codelibs.fess.app.web.admin.webconfig.EditForm.class, org.codelibs.fess.app.web.admin.fileconfig.CreateForm.class,
                org.codelibs.fess.app.web.admin.fileconfig.EditForm.class, org.codelibs.fess.app.web.admin.dataconfig.CreateForm.class,
                org.codelibs.fess.app.web.admin.dataconfig.EditForm.class, org.codelibs.fess.app.web.api.admin.webconfig.CreateBody.class,
                org.codelibs.fess.app.web.api.admin.webconfig.EditBody.class,
                org.codelibs.fess.app.web.api.admin.fileconfig.CreateBody.class,
                org.codelibs.fess.app.web.api.admin.fileconfig.EditBody.class,
                org.codelibs.fess.app.web.api.admin.dataconfig.CreateBody.class,
                org.codelibs.fess.app.web.api.admin.dataconfig.EditBody.class };
        for (final Class<?> clazz : classes) {
            for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
                for (final Field field : c.getDeclaredFields()) {
                    assertFalse(clazz.getName() + "." + field.getName(), "metadata".equalsIgnoreCase(field.getName()));
                    assertFalse(clazz.getName() + "." + field.getName(), "progressCounter".equalsIgnoreCase(field.getName()));
                }
            }
        }
    }
}
