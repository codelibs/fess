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
package org.codelibs.fess.app.web.admin.maintenance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.response.HtmlResponse;

public class AdminMaintenanceActionTest extends UnitFessTestCase {

    /**
     * The diagnostic tests write secrets into the shared system properties, so they need their own
     * container to keep those values from leaking into other test classes.
     *
     * @return true to create the container for each test
     */
    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    // ===================================================================================
    //                                                                          Diagnostic
    //                                                                          ==========

    private static Properties readEntry(final byte[] zip, final String name) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zip))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals(name)) {
                    final Properties prop = new Properties();
                    prop.load(zis);
                    return prop;
                }
            }
        }
        throw new AssertionError("no entry: " + name);
    }

    @Test
    public void test_writeSystemProperties_masksSecretsAndKeepsKeys() throws Exception {
        ComponentUtil.getSystemProperties().setProperty("storage.secretkey", "s3-secret-value");
        ComponentUtil.getSystemProperties().setProperty("slack.webhook.urls", "https://hooks.example.com/services/T0/B0/token-value");
        ComponentUtil.getSystemProperties().setProperty("storage.bucket", "fess-bucket");

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(out)) {
            new AdminMaintenanceAction().writeSystemProperties(zos, "diag");
        }

        final Properties prop = readEntry(out.toByteArray(), "diag/system.properties");
        assertEquals("XXXXXXXX", prop.getProperty("storage.secretkey"));
        assertEquals("XXXXXXXX", prop.getProperty("slack.webhook.urls"));
        assertEquals("fess-bucket", prop.getProperty("storage.bucket"));
    }

    @Test
    public void test_writeFessConfig_masksTheCipherKeyAndKeepsKeys() throws Exception {
        final FessConfig config = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Set<String> keySet() {
                return Set.of("app.cipher.key", "search_engine.password", "index.backup.log.load.timeout");
            }

            @Override
            public String get(final String propertyKey) {
                return switch (propertyKey) {
                case "app.cipher.key" -> "my-cipher-key";
                case "search_engine.password" -> "hunter2";
                default -> "60000";
                };
            }
        };
        final AdminMaintenanceAction action = new AdminMaintenanceAction() {
            {
                fessConfig = config;
            }
        };

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(out)) {
            action.writeFessConfig(zos, "diag");
        }

        final Properties prop = readEntry(out.toByteArray(), "diag/fess_config.properties");
        assertEquals("XXXXXXXX", prop.getProperty("app.cipher.key"));
        assertEquals("XXXXXXXX", prop.getProperty("search_engine.password"));
        assertEquals("60000", prop.getProperty("index.backup.log.load.timeout"));
    }

    @Test
    public void test_isAccessTokenIndex() {
        assertTrue(AdminMaintenanceAction.isAccessTokenIndex("fess_config.access_token"));
        assertTrue(AdminMaintenanceAction.isAccessTokenIndex("custom_config.access_token"));
        assertFalse(AdminMaintenanceAction.isAccessTokenIndex("fess_config.web_config"));
        assertFalse(AdminMaintenanceAction.isAccessTokenIndex("fess_config.role_type"));
        assertFalse(AdminMaintenanceAction.isAccessTokenIndex(null));
    }

    // ===================================================================================
    //                                                                     isLogFilename
    //                                                                     ==============

    @Test
    public void test_isLogFilename_logExtension() {
        final AdminMaintenanceAction action = new AdminMaintenanceAction();
        assertTrue(action.isLogFilename("fess.log"));
        assertTrue(action.isLogFilename("crawler.log"));
        assertTrue(action.isLogFilename("audit.log"));
    }

    @Test
    public void test_isLogFilename_logGzExtension() {
        final AdminMaintenanceAction action = new AdminMaintenanceAction();
        assertTrue(action.isLogFilename("fess.log.gz"));
        assertTrue(action.isLogFilename("crawler.log.gz"));
    }

    @Test
    public void test_isLogFilename_invalidExtension() {
        final AdminMaintenanceAction action = new AdminMaintenanceAction();
        assertFalse(action.isLogFilename("fess.txt"));
        assertFalse(action.isLogFilename("fess.xml"));
        assertFalse(action.isLogFilename("fess.log.bak"));
    }

    // ===================================================================================
    //                                                                    reloadDocIndex
    //                                                                    ==============

    @Test
    public void test_reloadDocIndex_isIgnoredWithoutThePlugins() {
        // the button is hidden without the plugins, so only a hand-made request gets here
        for (final String type : new String[] { "vanilla", "aws", "cloud" }) {
            final List<Class<?>> redirects = new ArrayList<>();
            final AdminMaintenanceAction action = new AdminMaintenanceAction() {
                {
                    fessConfig = new FessConfig.SimpleImpl() {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public String getSearchEngineType() {
                            return type;
                        }
                    };
                    searchEngineClient = new SearchEngineClient() {
                        @Override
                        public void flushConfigFiles(final Runnable callback) {
                            fail("the index must not be closed and reopened");
                        }
                    };
                }

                @Override
                protected HtmlResponse redirect(final Class<?> actionType) {
                    redirects.add(actionType);
                    return HtmlResponse.undefined();
                }
            };

            action.reloadDocIndex(new ActionForm());

            assertEquals(type, 1, redirects.size());
            assertTrue(type, AdminMaintenanceAction.class.isAssignableFrom(redirects.get(0)));
        }
    }

    // ===================================================================================
    //                                                                     FessMessages
    //                                                                     ==============

    @Test
    public void test_operationAlreadyRunningMessageKey() {
        assertEquals("{errors.operation_already_running}", FessMessages.ERRORS_operation_already_running);
    }

    @Test
    public void test_addErrorsOperationAlreadyRunning() {
        final FessMessages messages = new FessMessages();
        final FessMessages result = messages.addErrorsOperationAlreadyRunning("testProperty", "host1");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.hasMessageOf("testProperty"));
    }

    @Test
    public void test_addErrorsOperationAlreadyRunning_withDifferentHosts() {
        final FessMessages messages1 = new FessMessages();
        messages1.addErrorsOperationAlreadyRunning("prop", "host1");
        assertFalse(messages1.isEmpty());

        final FessMessages messages2 = new FessMessages();
        messages2.addErrorsOperationAlreadyRunning("prop", "node1@host2");
        assertFalse(messages2.isEmpty());
    }

    // ===================================================================================
    //                                                                         Constants
    //                                                                         ==========

    @Test
    public void test_roleConstant() {
        assertEquals("admin-maintenance", AdminMaintenanceAction.ROLE);
    }

    // ===================================================================================
    //                                                                         ActionForm
    //                                                                         ===========

    @Test
    public void test_actionForm_defaults() {
        final ActionForm form = new ActionForm();
        assertNull(form.replaceAliases);
        assertNull(form.resetDictionaries);
        // numberOfShardsForDoc and autoExpandReplicasForDoc have defaults from FessConfig
        assertNotNull(form.numberOfShardsForDoc);
        assertNotNull(form.autoExpandReplicasForDoc);
        assertNull(form.loadBulkData);
        // rebuildConfigIndex defaults to Constants.ON
        assertEquals("on", form.rebuildConfigIndex);
        assertNull(form.rebuildUserIndex);
        assertNull(form.rebuildLogIndex);
    }

    @Test
    public void test_actionForm_setValues() {
        final ActionForm form = new ActionForm();
        form.replaceAliases = "on";
        form.resetDictionaries = "on";
        form.numberOfShardsForDoc = "5";
        form.autoExpandReplicasForDoc = "0-1";
        form.loadBulkData = "on";
        form.rebuildConfigIndex = "on";
        form.rebuildUserIndex = "on";
        form.rebuildLogIndex = "on";

        assertEquals("on", form.replaceAliases);
        assertEquals("on", form.resetDictionaries);
        assertEquals("5", form.numberOfShardsForDoc);
        assertEquals("0-1", form.autoExpandReplicasForDoc);
        assertEquals("on", form.loadBulkData);
        assertEquals("on", form.rebuildConfigIndex);
        assertEquals("on", form.rebuildUserIndex);
        assertEquals("on", form.rebuildLogIndex);
    }
}
