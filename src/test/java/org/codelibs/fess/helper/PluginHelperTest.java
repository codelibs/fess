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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.exception.PluginException;
import org.codelibs.fess.helper.PluginHelper.Artifact;
import org.codelibs.fess.helper.PluginHelper.ArtifactType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.lastaflute.di.exception.IORuntimeException;

public class PluginHelperTest extends UnitFessTestCase {
    private PluginHelper pluginHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        pluginHelper = new PluginHelper() {
            @Override
            protected String[] getRepositories() {
                return new String[] { "plugin/repo1/", "plugin/repo2/" };
            }

            @Override
            protected String getRepositoryContent(String url) {
                if (url.endsWith("/")) {
                    url = url + "index.html";
                }
                if (url.contains("plugin/repo1")) {
                    try (InputStream is = ResourceUtil.getResourceAsStream(url)) {
                        return new String(InputStreamUtil.getBytes(is), Constants.UTF_8);
                    } catch (IOException e) {
                        throw new IORuntimeException(e);
                    }
                } else if (url.contains("plugin/repo2")) {
                    try (InputStream is = ResourceUtil.getResourceAsStream(url)) {
                        return new String(InputStreamUtil.getBytes(is), Constants.UTF_8);
                    } catch (IOException e) {
                        throw new IORuntimeException(e);
                    }
                } else if (url.contains("plugin/repo3")) {
                    try (InputStream is = ResourceUtil.getResourceAsStream(url)) {
                        return new String(InputStreamUtil.getBytes(is), Constants.UTF_8);
                    } catch (IOException e) {
                        throw new IORuntimeException(e);
                    }
                } else if (url.contains("plugin/repo.yaml")) {
                    try (InputStream is = ResourceUtil.getResourceAsStream(url)) {
                        return new String(InputStreamUtil.getBytes(is), Constants.UTF_8);
                    } catch (IOException e) {
                        throw new IORuntimeException(e);
                    }
                }
                throw new FessSystemException("unknown");
            }

            @Override
            protected boolean isTargetPluginVersion(final String version) {
                return true;
            }
        };
    }

    public void test_processRepository1() {
        List<Artifact> list = pluginHelper.processRepository(ArtifactType.DATA_STORE, "plugin/repo1/");
        assertEquals(7, list.size());
        assertEquals("fess-ds-atlassian", list.get(0).getName());
        assertEquals("12.2.0", list.get(0).getVersion());
        assertEquals("plugin/repo1/fess-ds-atlassian/12.2.0/fess-ds-atlassian-12.2.0.jar", list.get(0).getUrl());
    }

    public void test_processRepository2() {
        List<Artifact> list = pluginHelper.processRepository(ArtifactType.DATA_STORE, "plugin/repo2/");
        assertEquals(1, list.size());
        assertEquals("fess-ds-atlassian", list.get(0).getName());
        assertEquals("12.2.0-20180814.210714-10", list.get(0).getVersion());
        assertEquals("plugin/repo2/fess-ds-atlassian/12.2.0-SNAPSHOT/fess-ds-atlassian-12.2.0-20180814.210714-10.jar",
                list.get(0).getUrl());
    }

    public void test_processRepository3() {
        List<Artifact> list = pluginHelper.processRepository(ArtifactType.CRAWLER, "plugin/repo3/");
        assertEquals(2, list.size());
        assertEquals("fess-crawler-smbj", list.get(0).getName());
        assertEquals("14.14.0", list.get(0).getVersion());
        assertEquals("plugin/repo3/fess-crawler-smbj/14.14.0/fess-crawler-smbj-14.14.0.jar", list.get(0).getUrl());
        assertEquals("fess-crawler-smbj", list.get(1).getName());
        assertEquals("14.15.0", list.get(1).getVersion());
        assertEquals("plugin/repo3/fess-crawler-smbj/14.15.0/fess-crawler-smbj-14.15.0.jar", list.get(1).getUrl());
    }

    public void test_getArtifactFromFileName1() {
        Artifact artifact = pluginHelper.getArtifactFromFileName(ArtifactType.DATA_STORE, "fess-ds-atlassian-13.2.0.jar");
        assertEquals("fess-ds-atlassian", artifact.getName());
        assertEquals("13.2.0", artifact.getVersion());
    }

    public void test_getArtifactFromFileName2() {
        Artifact artifact = pluginHelper.getArtifactFromFileName(ArtifactType.DATA_STORE, "fess-ds-atlassian-13.2.1-20190708.212247-1.jar");
        assertEquals("fess-ds-atlassian", artifact.getName());
        assertEquals("13.2.1-20190708.212247-1", artifact.getVersion());
    }

    public void test_getArtifactFromFileName3() {
        Artifact artifact = pluginHelper.getArtifactFromFileName(ArtifactType.UNKNOWN, "mysql-connector-java-8.0.17.jar");
        assertEquals("mysql-connector-java", artifact.getName());
        assertEquals("8.0.17", artifact.getVersion());
    }

    public void test_loadYaml() {
        List<Artifact> artifacts = pluginHelper.loadArtifactsFromRepository("plugin/repo.yaml");
        assertEquals(2, artifacts.size());
    }

    public void test_getAvailableArtifacts() {
        Artifact[] artifacts = pluginHelper.getAvailableArtifacts(ArtifactType.DATA_STORE);
        assertNotNull(artifacts);
        assertTrue(artifacts.length > 0);
    }

    public void test_getAvailableArtifacts_exception() {
        PluginHelper errorHelper = new PluginHelper() {
            @Override
            protected String[] getRepositories() {
                return new String[] { "invalid-repo" };
            }

            @Override
            protected String getRepositoryContent(String url) {
                throw new RuntimeException("Repository error");
            }
        };

        try {
            errorHelper.getAvailableArtifacts(ArtifactType.DATA_STORE);
            fail("Expected PluginException");
        } catch (PluginException e) {
            assertTrue(e.getMessage().contains("Failed to access"));
        }
    }

    public void test_getInstalledArtifacts_unknown() {
        Artifact[] artifacts = pluginHelper.getInstalledArtifacts(ArtifactType.UNKNOWN);
        assertNotNull(artifacts);
    }

    public void test_getInstalledArtifacts_dataStore() {
        Artifact[] artifacts = pluginHelper.getInstalledArtifacts(ArtifactType.DATA_STORE);
        assertNotNull(artifacts);
    }

    public void test_getArtifact_found() {
        Artifact result = pluginHelper.getArtifact("fess-ds-atlassian", "12.2.0");
        assertNotNull(result);
        assertEquals("fess-ds-atlassian", result.getName());
        assertEquals("12.2.0", result.getVersion());
    }

    public void test_getArtifact_notFound() {
        Artifact result = pluginHelper.getArtifact("non-existent", "1.0.0");
        assertNull(result);
    }

    public void test_getArtifact_nullInputs() {
        assertNull(pluginHelper.getArtifact(null, "1.0.0"));
        assertNull(pluginHelper.getArtifact("test", null));
        assertNull(pluginHelper.getArtifact("", "1.0.0"));
        assertNull(pluginHelper.getArtifact("test", ""));
    }

    public void test_isExcludedName_crawler() {
        assertTrue(pluginHelper.isExcludedName(ArtifactType.CRAWLER, "fess-crawler"));
        assertTrue(pluginHelper.isExcludedName(ArtifactType.CRAWLER, "fess-crawler-db"));
        assertTrue(pluginHelper.isExcludedName(ArtifactType.CRAWLER, "fess-crawler-db-h2"));
        assertTrue(pluginHelper.isExcludedName(ArtifactType.CRAWLER, "fess-crawler-db-mysql"));
        assertTrue(pluginHelper.isExcludedName(ArtifactType.CRAWLER, "fess-crawler-es"));
        assertTrue(pluginHelper.isExcludedName(ArtifactType.CRAWLER, "fess-crawler-opensearch"));
        assertTrue(pluginHelper.isExcludedName(ArtifactType.CRAWLER, "fess-crawler-lasta"));
        assertTrue(pluginHelper.isExcludedName(ArtifactType.CRAWLER, "fess-crawler-parent"));
        assertTrue(pluginHelper.isExcludedName(ArtifactType.CRAWLER, "fess-crawler-playwright"));
        assertTrue(pluginHelper.isExcludedName(ArtifactType.CRAWLER, "fess-crawler-webdriver"));

        assertFalse(pluginHelper.isExcludedName(ArtifactType.CRAWLER, "fess-crawler-smbj"));
        assertFalse(pluginHelper.isExcludedName(ArtifactType.DATA_STORE, "fess-crawler"));
    }

    public void test_isExcludedName_nonCrawler() {
        assertFalse(pluginHelper.isExcludedName(ArtifactType.DATA_STORE, "fess-crawler"));
        assertFalse(pluginHelper.isExcludedName(ArtifactType.THEME, "fess-crawler"));
        assertFalse(pluginHelper.isExcludedName(ArtifactType.INGEST, "fess-crawler"));
    }

    public void test_loadArtifactsFromRepository_emptyResult() {
        PluginHelper testHelper = new PluginHelper() {
            @Override
            protected String getRepositoryContent(String url) {
                return "[]";
            }
        };

        List<Artifact> result = testHelper.loadArtifactsFromRepository("test.yaml");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    public void test_loadArtifactsFromRepository_nullResult() {
        PluginHelper testHelper = new PluginHelper() {
            @Override
            protected String getRepositoryContent(String url) {
                return "null";
            }
        };

        List<Artifact> result = testHelper.loadArtifactsFromRepository("test.yaml");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    public void test_loadArtifactsFromRepository_invalidYaml() {
        PluginHelper testHelper = new PluginHelper() {
            @Override
            protected String getRepositoryContent(String url) {
                return "invalid yaml content [[[";
            }
        };

        try {
            testHelper.loadArtifactsFromRepository("test.yaml");
            fail("Expected PluginException");
        } catch (PluginException e) {
            assertTrue(e.getMessage().contains("Failed to access"));
        }
    }

    public void test_processRepository_noMatches() {
        PluginHelper testHelper = new PluginHelper() {
            @Override
            protected String getRepositoryContent(String url) {
                return "<html><body>No plugin matches here</body></html>";
            }
        };

        List<Artifact> result = testHelper.processRepository(ArtifactType.DATA_STORE, "test-repo/");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    public void test_processRepository_metadataError() {
        PluginHelper testHelper = new PluginHelper() {
            @Override
            protected String getRepositoryContent(String url) {
                if (url.contains("maven-metadata.xml")) {
                    throw new RuntimeException("Metadata error");
                }
                return "<html><body><a href=\"fess-ds-test/\">fess-ds-test</a></body></html>";
            }
        };

        List<Artifact> result = testHelper.processRepository(ArtifactType.DATA_STORE, "test-repo/");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    public void test_getSnapshotActualVersion_valid() {
        String snapshotXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<metadata>\n" + "  <snapshot>\n"
                + "    <timestamp>20180814.210714</timestamp>\n" + "    <buildNumber>10</buildNumber>\n" + "  </snapshot>\n"
                + "</metadata>";

        PluginHelper testHelper = new PluginHelper() {
            @Override
            protected String getRepositoryContent(String url) {
                return snapshotXml;
            }
        };

        try {
            javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            String result = testHelper.getSnapshotActualVersion(builder, "test-url/", "1.0.0-SNAPSHOT");
            assertEquals("20180814.210714-10", result);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void test_getSnapshotActualVersion_noSnapshot() {
        String noSnapshotXml =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<metadata>\n" + "  <version>1.0.0</version>\n" + "</metadata>";

        PluginHelper testHelper = new PluginHelper() {
            @Override
            protected String getRepositoryContent(String url) {
                return noSnapshotXml;
            }
        };

        try {
            javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            String result = testHelper.getSnapshotActualVersion(builder, "test-url/", "1.0.0-SNAPSHOT");
            assertNull(result);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void test_getSnapshotActualVersion_partialData() {
        String partialXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<metadata>\n" + "  <snapshot>\n"
                + "    <timestamp>20180814.210714</timestamp>\n" + "  </snapshot>\n" + "</metadata>";

        PluginHelper testHelper = new PluginHelper() {
            @Override
            protected String getRepositoryContent(String url) {
                return partialXml;
            }
        };

        try {
            javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            String result = testHelper.getSnapshotActualVersion(builder, "test-url/", "1.0.0-SNAPSHOT");
            assertNull(result);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void test_installArtifact_default() {
        PluginHelper testHelper = new PluginHelper() {
            @Override
            protected void install(Artifact artifact) {
                // Mock install method
            }
        };

        Artifact artifact = new Artifact("fess-ds-test", "1.0.0", "http://test.com/test.jar");

        try {
            testHelper.installArtifact(artifact);
            // If we get here, the method executed without throwing
            assertTrue(true);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void test_install_blankUrl() {
        PluginHelper testHelper = new PluginHelper();
        Artifact artifact = new Artifact("test", "1.0.0", "");

        try {
            testHelper.install(artifact);
            fail("Expected PluginException");
        } catch (PluginException e) {
            assertTrue(e.getMessage().contains("url is blank"));
        }
    }

    public void test_install_nullUrl() {
        PluginHelper testHelper = new PluginHelper();
        Artifact artifact = new Artifact("test", "1.0.0", null);

        try {
            testHelper.install(artifact);
            fail("Expected PluginException");
        } catch (PluginException e) {
            assertTrue(e.getMessage().contains("url is blank"));
        }
    }

    public void test_deleteInstalledArtifact_nonExistent() {
        PluginHelper testHelper = new PluginHelper();
        Artifact artifact = new Artifact("non-existent", "1.0.0");

        try {
            testHelper.deleteInstalledArtifact(artifact);
            fail("Expected PluginException");
        } catch (PluginException e) {
            assertTrue(e.getMessage().contains("does not exist"));
        }
    }

    public void test_getArtifactFromFileName_withUrl() {
        Artifact artifact =
                pluginHelper.getArtifactFromFileName(ArtifactType.DATA_STORE, "test-plugin-1.0.0.jar", "http://test.com/test.jar");
        assertEquals("test-plugin", artifact.getName());
        assertEquals("1.0.0", artifact.getVersion());
        assertEquals("http://test.com/test.jar", artifact.getUrl());
    }

    public void test_getArtifactFromFileName_complexName() {
        Artifact artifact = pluginHelper.getArtifactFromFileName(ArtifactType.DATA_STORE, "fess-ds-web-crawler-1.2.3-beta.jar");
        assertEquals("fess-ds-web-crawler", artifact.getName());
        assertEquals("1.2.3-beta", artifact.getVersion());
    }

    public void test_getArtifactFromFileName_singleVersion() {
        Artifact artifact = pluginHelper.getArtifactFromFileName(ArtifactType.DATA_STORE, "plugin-1.jar");
        assertEquals("plugin", artifact.getName());
        assertEquals("1", artifact.getVersion());
    }

    public void test_getArtifactFromFileName_noVersion() {
        Artifact artifact = pluginHelper.getArtifactFromFileName(ArtifactType.DATA_STORE, "plugin.jar");
        assertEquals("plugin", artifact.getName());
        assertEquals("", artifact.getVersion());
    }

    // Test inner classes
    public void test_Artifact_constructor() {
        Artifact artifact = new Artifact("test", "1.0.0", "http://test.com/test.jar");
        assertEquals("test", artifact.getName());
        assertEquals("1.0.0", artifact.getVersion());
        assertEquals("http://test.com/test.jar", artifact.getUrl());
        assertEquals("test-1.0.0.jar", artifact.getFileName());
        assertEquals(ArtifactType.UNKNOWN, artifact.getType());
    }

    public void test_Artifact_constructorWithoutUrl() {
        Artifact artifact = new Artifact("test", "1.0.0");
        assertEquals("test", artifact.getName());
        assertEquals("1.0.0", artifact.getVersion());
        assertNull(artifact.getUrl());
        assertEquals("test-1.0.0.jar", artifact.getFileName());
    }

    public void test_Artifact_toString() {
        Artifact artifact = new Artifact("test", "1.0.0");
        assertEquals("test:1.0.0", artifact.toString());
    }

    public void test_Artifact_getType() {
        Artifact dsArtifact = new Artifact("fess-ds-test", "1.0.0");
        assertEquals(ArtifactType.DATA_STORE, dsArtifact.getType());

        Artifact themeArtifact = new Artifact("fess-theme-test", "1.0.0");
        assertEquals(ArtifactType.THEME, themeArtifact.getType());

        Artifact ingestArtifact = new Artifact("fess-ingest-test", "1.0.0");
        assertEquals(ArtifactType.INGEST, ingestArtifact.getType());

        Artifact scriptArtifact = new Artifact("fess-script-test", "1.0.0");
        assertEquals(ArtifactType.SCRIPT, scriptArtifact.getType());

        Artifact webappArtifact = new Artifact("fess-webapp-test", "1.0.0");
        assertEquals(ArtifactType.WEBAPP, webappArtifact.getType());

        Artifact thumbnailArtifact = new Artifact("fess-thumbnail-test", "1.0.0");
        assertEquals(ArtifactType.THUMBNAIL, thumbnailArtifact.getType());

        Artifact crawlerArtifact = new Artifact("fess-crawler-test", "1.0.0");
        assertEquals(ArtifactType.CRAWLER, crawlerArtifact.getType());

        Artifact unknownArtifact = new Artifact("unknown-test", "1.0.0");
        assertEquals(ArtifactType.UNKNOWN, unknownArtifact.getType());
    }

    public void test_ArtifactType_getId() {
        assertEquals("fess-ds", ArtifactType.DATA_STORE.getId());
        assertEquals("fess-theme", ArtifactType.THEME.getId());
        assertEquals("fess-ingest", ArtifactType.INGEST.getId());
        assertEquals("fess-script", ArtifactType.SCRIPT.getId());
        assertEquals("fess-webapp", ArtifactType.WEBAPP.getId());
        assertEquals("fess-thumbnail", ArtifactType.THUMBNAIL.getId());
        assertEquals("fess-crawler", ArtifactType.CRAWLER.getId());
        assertEquals("jar", ArtifactType.UNKNOWN.getId());
    }

    public void test_ArtifactType_getType() {
        assertEquals(ArtifactType.DATA_STORE, ArtifactType.getType("fess-ds-test"));
        assertEquals(ArtifactType.THEME, ArtifactType.getType("fess-theme-test"));
        assertEquals(ArtifactType.INGEST, ArtifactType.getType("fess-ingest-test"));
        assertEquals(ArtifactType.SCRIPT, ArtifactType.getType("fess-script-test"));
        assertEquals(ArtifactType.WEBAPP, ArtifactType.getType("fess-webapp-test"));
        assertEquals(ArtifactType.THUMBNAIL, ArtifactType.getType("fess-thumbnail-test"));
        assertEquals(ArtifactType.CRAWLER, ArtifactType.getType("fess-crawler-test"));
        assertEquals(ArtifactType.UNKNOWN, ArtifactType.getType("unknown-test"));
    }

    public void test_processRepository_withVersionFiltering() {
        PluginHelper testHelper = new PluginHelper() {
            @Override
            protected String getRepositoryContent(String url) {
                if (url.contains("maven-metadata.xml")) {
                    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<metadata>\n" + "  <versioning>\n" + "    <versions>\n"
                            + "      <version>1.0.0</version>\n" + "      <version>2.0.0</version>\n" + "    </versions>\n"
                            + "  </versioning>\n" + "</metadata>";
                }
                return "<html><body><a href=\"fess-ds-test/\">fess-ds-test</a></body></html>";
            }

            @Override
            protected boolean isTargetPluginVersion(String version) {
                return "1.0.0".equals(version);
            }
        };

        List<Artifact> result = testHelper.processRepository(ArtifactType.DATA_STORE, "test-repo/");
        assertEquals(1, result.size());
        assertEquals("1.0.0", result.get(0).getVersion());
    }

}
