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
package org.codelibs.fess.opensearch.client;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.codelibs.curl.Curl;
import org.codelibs.curl.CurlRequest;
import org.codelibs.curl.CurlResponse;
import org.codelibs.curl.io.ContentCache;
import org.codelibs.fesen.opensearch.action.ActionRequest;
import org.codelibs.fesen.opensearch.action.ActionType;
import org.codelibs.fesen.opensearch.action.admin.indices.alias.IndicesAliasesAction;
import org.codelibs.fesen.opensearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.codelibs.fesen.opensearch.action.admin.indices.mapping.get.GetMappingsAction;
import org.codelibs.fesen.opensearch.action.admin.indices.mapping.get.GetMappingsRequestBuilder;
import org.codelibs.fesen.opensearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.codelibs.fesen.opensearch.action.admin.indices.mapping.put.PutMappingAction;
import org.codelibs.fesen.opensearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.codelibs.fesen.opensearch.action.support.PlainActionFuture;
import org.codelibs.fesen.opensearch.action.support.clustermanager.AcknowledgedResponse;
import org.codelibs.fesen.opensearch.cluster.metadata.MappingMetadata;
import org.codelibs.fesen.opensearch.common.action.ActionFuture;
import org.codelibs.fesen.opensearch.core.action.ActionResponse;
import org.codelibs.fesen.opensearch.transport.client.AdminClient;
import org.codelibs.fesen.opensearch.transport.client.Client;
import org.codelibs.fesen.opensearch.transport.client.ClusterAdminClient;
import org.codelibs.fesen.opensearch.transport.client.IndicesAdminClient;
import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.ChunkVectorHelper;
import org.codelibs.fess.helper.CurlHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.lastaflute.di.exception.ContainerInitFailureException;

/**
 * Tests how the search engine type selects the index resources and the plugin-dependent steps of
 * {@link SearchEngineClient}.
 *
 * <p>The tests do not set the {@code fess.<type>.process} system properties: those are
 * JVM-global, and surefire runs the classes of this module in parallel. A client that must act as
 * the webapp process or as a job process overrides {@code isWebappProcess()} instead. Where a
 * test reads the log it keeps to the events of its own thread, because other classes log through
 * the same logger at the same time.</p>
 */
public class SearchEngineClientEngineTypeTest extends UnitFessTestCase {

    /** The search engine types, and the resource type each one reads its index definitions from. */
    private static final String[][] RESOURCE_TYPES = { { "vanilla", "vanilla" }, { "aws", "vanilla" }, { "cloud", "vanilla" },
            { "default", "default" }, { "opensearch", "opensearch" }, { "", "" }, { null, null } };

    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    /** A configuration of the given search engine type that also answers what the index steps read. */
    private static class TypedFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;

        private final String type;

        TypedFessConfig(final String type) {
            this.type = type;
        }

        @Override
        public String getSearchEngineType() {
            return type;
        }

        @Override
        public String getIndexIndicesTimeout() {
            return "30s";
        }

        @Override
        public String getIndexCodec() {
            return "default";
        }

        @Override
        public String getIndexNumberOfShards() {
            return "1";
        }

        @Override
        public String getIndexAutoExpandReplicas() {
            return "0-1";
        }

        @Override
        public String getAppExtensionNames() {
            return "ext";
        }

        @Override
        public String getIndexConfigIndex() {
            return "fess_config";
        }

        @Override
        public String getIndexUserIndex() {
            return "fess_user";
        }

        @Override
        public String getIndexLogIndex() {
            return "fess_log";
        }

        @Override
        public String getIndexDocumentSearchIndex() {
            return "fess.search";
        }

        @Override
        public String getIndexDocumentUpdateIndex() {
            return "fess.update";
        }

        @Override
        public String getIndexDictionaryPrefix() {
            return "";
        }

        @Override
        public String getFesenHttpUrl() {
            return "http://localhost:9200";
        }
    }

    private static FessConfig fessConfigOfType(final String type) {
        return new TypedFessConfig(type);
    }

    /** Acts as the webapp process (or as a job process) without touching the process system properties. */
    private static class ProcessAwareClient extends SearchEngineClient {
        private final boolean webapp;

        ProcessAwareClient(final boolean webapp) {
            this.webapp = webapp;
        }

        @Override
        protected boolean isWebappProcess() {
            return webapp;
        }
    }

    // -------------------------------------------------------------------------------------
    //                                                                       resource path
    //                                                                       -------------

    private String resolveSettingsPath(final String type) {
        final SearchEngineClient client = new SearchEngineClient();
        return client.getResourcePath(client.indexConfigPath, fessConfigOfType(type).getFesenResourceType(), "/fess.json");
    }

    @Test
    public void test_resourcePath_pluginlessTypesUseTheVanillaCopy() {
        for (final String type : new String[] { "vanilla", "aws", "cloud" }) {
            assertEquals(type, "fess_indices/_vanilla/fess.json", resolveSettingsPath(type));
        }
    }

    @Test
    public void test_resourcePath_otherTypesUseTheDefaultFiles() {
        // default has no resource directory, and a custom type falls back when its directory is absent
        for (final String type : new String[] { "default", "opensearch", "" }) {
            assertEquals(type, "fess_indices/fess.json", resolveSettingsPath(type));
        }
    }

    @Test
    public void test_resourcePath_aCustomTypeKeepsItsOverrideDirectory() {
        // src/test/resources holds fess_indices/_testtype/fess.json; no production configuration names that type
        assertEquals("fess_indices/_testtype/fess.json", resolveSettingsPath("testtype"));
        // an override directory has the files it overrides only: the others come from the default tree
        final SearchEngineClient client = new SearchEngineClient();
        assertEquals("fess_indices/fess/doc.json", client.getResourcePath(client.indexConfigPath, "testtype", "/fess/doc.json"));
    }

    @Test
    public void test_createIndex_aCustomTypeReadsItsOverrideDirectory() {
        assertEquals("fess_indices/_testtype/fess.json", createIndexProbe("testtype", false).indexConfigFile);
    }

    // -------------------------------------------------------------------------------------
    //                                                          wiring of the resource type
    //                                                          ---------------------------

    /** Stops {@code createIndex} at the point where it hands the index definition file on. */
    private static class CreateIndexProbe extends SearchEngineClient {
        final List<String> calls = new ArrayList<>();

        String indexConfigFile;

        @Override
        protected void waitForConfigSyncStatus() {
            calls.add("waitForConfigSyncStatus");
        }

        @Override
        protected void sendConfigFiles(final String index) {
            calls.add("sendConfigFiles:" + index);
        }

        @Override
        protected String readIndexSetting(final String index, final String fesenType, final String indexConfigFile,
                final String numberOfShards, final String autoExpandReplicas) {
            this.indexConfigFile = indexConfigFile;
            // no engine is connected: stop before the create request
            throw new IllegalStateException("stop: " + indexConfigFile);
        }
    }

    private CreateIndexProbe createIndexProbe(final String type, final boolean uploadConfig) {
        ComponentUtil.setFessConfig(fessConfigOfType(type));
        final CreateIndexProbe client = new CreateIndexProbe();
        assertFalse(client.createIndex("fess", "fess.20260101000000000", "1", "0-1", uploadConfig, Collections.emptyMap()));
        return client;
    }

    @Test
    public void test_createIndex_sendsConfigFilesOnlyWhenPluginsAreAvailable() {
        for (final String type : new String[] { "vanilla", "aws", "cloud" }) {
            assertEquals(type, List.of(), createIndexProbe(type, true).calls);
        }
        for (final String type : new String[] { "default", "opensearch" }) {
            assertEquals(type, List.of("waitForConfigSyncStatus", "sendConfigFiles:fess"), createIndexProbe(type, true).calls);
        }
        assertEquals(List.of(), createIndexProbe("default", false).calls);
    }

    @Test
    public void test_createIndex_readsTheIndexDefinitionOfTheResourceType() {
        for (final String[] types : RESOURCE_TYPES) {
            final String expected = "vanilla".equals(types[1]) ? "fess_indices/_vanilla/fess.json" : "fess_indices/fess.json";
            assertEquals(String.valueOf(types[0]), expected, createIndexProbe(types[0], false).indexConfigFile);
        }
    }

    /**
     * Plays the HTTP client that the search engine client delegates to: it answers the get-mappings,
     * put-mapping and aliases requests that the index steps send, and nothing else.
     */
    private static class EngineAnswers extends SearchEngineClient {
        /** What the get-mappings request answers. */
        Map<String, MappingMetadata> mappings = Map.of();

        private final IndicesAdminClient indices = (IndicesAdminClient) Proxy.newProxyInstance(IndicesAdminClient.class.getClassLoader(),
                new Class<?>[] { IndicesAdminClient.class }, (proxy, method, args) -> {
                    switch (method.getName()) {
                    case "prepareGetMappings":
                        return new GetMappingsRequestBuilder(this, GetMappingsAction.INSTANCE, (String[]) args[0]);
                    case "preparePutMapping":
                        return new PutMappingRequestBuilder(this, PutMappingAction.INSTANCE).setIndices((String[]) args[0]);
                    case "prepareAliases":
                        return new IndicesAliasesRequestBuilder(this, IndicesAliasesAction.INSTANCE);
                    default:
                        throw new UnsupportedOperationException(method.getName());
                    }
                });

        private final AdminClient admin = new AdminClient() {
            @Override
            public ClusterAdminClient cluster() {
                throw new UnsupportedOperationException("cluster");
            }

            @Override
            public IndicesAdminClient indices() {
                return indices;
            }
        };

        @Override
        public AdminClient admin() {
            return admin;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <Request extends ActionRequest, Response extends ActionResponse> ActionFuture<Response> execute(
                final ActionType<Response> action, final Request request) {
            final PlainActionFuture<Response> future = PlainActionFuture.newFuture();
            if (GetMappingsAction.INSTANCE.equals(action)) {
                future.onResponse((Response) new GetMappingsResponse(mappings));
            } else {
                future.onResponse((Response) new AcknowledgedResponse(true));
            }
            return future;
        }
    }

    /** A search engine client that records the resource type of every index definition it looks up. */
    private static class ResourceTypeRecorder extends SearchEngineClient {
        /** The resource type of each getResourcePath call, in call order. */
        final List<String> resourceTypes = new ArrayList<>();

        final List<String> bulkLoads = new ArrayList<>();

        final EngineAnswers engine = new EngineAnswers();

        ResourceTypeRecorder() {
            this.client = engine;
        }

        @Override
        protected String getResourcePath(final String basePath, final String type, final String path) {
            resourceTypes.add(type);
            return super.getResourcePath(basePath, type, path);
        }

        @Override
        protected boolean isWebappProcess() {
            return true;
        }

        @Override
        protected boolean putMapping(final String indexName, final String source) {
            return true;
        }

        @Override
        protected void insertBulkData(final FessConfig fessConfig, final String configIndex, final String dataPath,
                final boolean createOnly) {
            bulkLoads.add(dataPath);
        }
    }

    private ResourceTypeRecorder recorderOfType(final String type) {
        ComponentUtil.setFessConfig(fessConfigOfType(type));
        // substitutePlaceholders delegates to ChunkVectorHelper, which the test container does not register
        ComponentUtil.register(new ChunkVectorHelper(), ChunkVectorHelper.class.getCanonicalName());
        return new ResourceTypeRecorder();
    }

    /** Asserts that {@code calls} lookups were made and every one of them got the resource type. */
    private void assertResourceType(final String type, final String expected, final List<String> recorded, final int calls) {
        final List<String> expectedTypes = new ArrayList<>();
        for (int i = 0; i < calls; i++) {
            expectedTypes.add(expected);
        }
        assertEquals(type, expectedTypes, recorded);
    }

    @Test
    public void test_addMapping_newMappingAndItsBulkDataUseTheResourceType() {
        for (final String[] types : RESOURCE_TYPES) {
            final ResourceTypeRecorder client = recorderOfType(types[0]);

            client.addMapping("fess_config.scheduled_job", "scheduled_job", "fess_config.scheduled_job");

            // the mapping file, the bulk data file and the bulk data file of the one app extension
            assertResourceType(String.valueOf(types[0]), types[1], client.resourceTypes, 3);
            assertFalse(String.valueOf(types[0]), client.bulkLoads.isEmpty());
        }
    }

    @Test
    public void test_addMapping_existingMappingChecksAndReloadsFromTheResourceType() {
        for (final String[] types : RESOURCE_TYPES) {
            final ResourceTypeRecorder client = recorderOfType(types[0]);
            client.engine.mappings = Map.of("properties", new MappingMetadata("properties", Map.of()));

            client.addMapping("fess_config.scheduled_job", "scheduled_job", "fess_config.scheduled_job");

            // the mapping file that addMissingProperties compares with, and the startup reload of the bulk data
            assertResourceType(String.valueOf(types[0]), types[1], client.resourceTypes, 2);
            assertFalse(String.valueOf(types[0]), client.bulkLoads.isEmpty());
        }
    }

    @Test
    public void test_addMissingProperties_readsTheMappingOfTheResourceType() {
        for (final String[] types : RESOURCE_TYPES) {
            final ResourceTypeRecorder client = recorderOfType(types[0]);

            client.addMissingProperties("fess_config.scheduled_job", "scheduled_job", "fess_config.scheduled_job",
                    new MappingMetadata("properties", Map.of()));

            assertResourceType(String.valueOf(types[0]), types[1], client.resourceTypes, 1);
        }
    }

    @Test
    public void test_createAlias_readsTheAliasConfigOfTheResourceType() {
        for (final String[] types : RESOURCE_TYPES) {
            final ResourceTypeRecorder client = recorderOfType(types[0]);

            client.createAlias("fess_config.scheduled_job", "fess_config.scheduled_job.20260101000000000");

            assertResourceType(String.valueOf(types[0]), types[1], client.resourceTypes, 1);
        }
    }

    @Test
    public void test_switchAliases_readsTheAliasConfigOfTheResourceType() {
        for (final String[] types : RESOURCE_TYPES) {
            final ResourceTypeRecorder client = recorderOfType(types[0]);

            assertTrue(String.valueOf(types[0]),
                    client.switchAliases("fess_config.scheduled_job", "fess_config.scheduled_job.1", "fess_config.scheduled_job.2"));

            assertResourceType(String.valueOf(types[0]), types[1], client.resourceTypes, 1);
        }
    }

    @Test
    public void test_getDocumentIndexAliases_readsTheAliasConfigOfTheResourceType() {
        for (final String[] types : RESOURCE_TYPES) {
            final ResourceTypeRecorder client = recorderOfType(types[0]);

            assertFalse(String.valueOf(types[0]), client.getDocumentIndexAliases("fess").isEmpty());

            assertResourceType(String.valueOf(types[0]), types[1], client.resourceTypes, 1);
        }
    }

    /**
     * Every {@code getResourcePath} call must pass {@code getFesenResourceType()}: the canonical
     * {@code getFesenType()} of {@code aws} would look for a {@code _aws} directory that is gone.
     * The behavioural tests above drive each call site; this one stops a new call site from
     * passing the wrong type without anybody driving it.
     */
    @Test
    public void test_noGetResourcePathCallPassesTheSearchEngineType() throws IOException {
        final String source =
                new String(Files.readAllBytes(Paths.get("src/main/java/org/codelibs/fess/opensearch/client/SearchEngineClient.java")),
                        StandardCharsets.UTF_8);
        final Matcher call = Pattern.compile("getResourcePath\\(").matcher(source);
        final List<String> argumentLists = new ArrayList<>();
        while (call.find()) {
            final int open = call.end();
            int depth = 1;
            int i = open;
            while (depth > 0) {
                final char c = source.charAt(i++);
                if (c == '(') {
                    depth++;
                } else if (c == ')') {
                    depth--;
                }
            }
            final String arguments = source.substring(open, i - 1);
            // the declaration "protected String getResourcePath(final String basePath, ..." is no call
            if (!arguments.contains("final String")) {
                argumentLists.add(arguments);
            }
        }
        assertTrue(argumentLists.size() >= 11, "only " + argumentLists.size() + " calls found, the scan is broken");
        for (final String arguments : argumentLists) {
            assertTrue(arguments, arguments.contains("getFesenResourceType()"));
            assertFalse(arguments, arguments.contains("getFesenType()"));
            assertFalse(arguments, arguments.contains("getSearchEngineType()"));
            assertFalse(arguments, arguments.contains("fesenType"));
        }
    }

    // -------------------------------------------------------------------------------------
    //                                                                    configuration warnings
    //                                                                    ----------------------

    @Test
    public void test_warnDeprecatedSearchEngineType_warnsOnceForCloud() {
        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            new ProcessAwareClient(true).warnDeprecatedSearchEngineType(fessConfigOfType("cloud"));

            final List<String> warnings = capture.messagesOnThisThreadAt(Level.WARN);
            assertEquals(1, warnings.size());
            assertTrue(warnings.get(0), warnings.get(0).contains("search_engine.type=cloud is deprecated"));
            assertTrue(warnings.get(0), warnings.get(0).contains("search_engine.type=vanilla"));
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_warnDeprecatedSearchEngineType_silentForOtherTypesAndInJobProcesses() {
        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            final SearchEngineClient client = new ProcessAwareClient(true);
            for (final String type : new String[] { "default", "vanilla", "aws", "opensearch", "" }) {
                client.warnDeprecatedSearchEngineType(fessConfigOfType(type));
            }
            client.warnDeprecatedSearchEngineType(fessConfigOfType(null));
            // a job process opens the client too, and must not repeat the warning on every run
            new ProcessAwareClient(false).warnDeprecatedSearchEngineType(fessConfigOfType("cloud"));

            assertEquals(List.of(), capture.messagesOnThisThreadAt(Level.WARN));
        } finally {
            capture.detach();
        }
    }

    /** A client whose class path holds exactly the given resources, so a test need not depend on the build output. */
    private static class ClassPathClient extends ProcessAwareClient {
        private final Set<String> resources;

        ClassPathClient(final boolean webapp, final String... resources) {
            super(webapp);
            this.resources = Set.of(resources);
        }

        @Override
        protected boolean isOnClassPath(final String resource) {
            return resources.contains(resource);
        }
    }

    @Test
    public void test_warnUnknownSearchEngineType_warnsOnceWhenNoOverrideDirectoryExists() {
        // type names are case-sensitive and are not trimmed
        for (final String type : new String[] { "Vanilla", "VANILLA", "AWS", " vanilla", "vanilla ", "opensearch", "" }) {
            final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
            try {
                new ClassPathClient(true).warnUnknownSearchEngineType(fessConfigOfType(type));

                final List<String> warnings = capture.messagesOnThisThreadAt(Level.WARN);
                assertEquals("'" + type + "'", 1, warnings.size());
                final String warning = warnings.get(0);
                assertTrue(warning, warning.contains("search_engine.type=" + type + " is not a known type (default, vanilla, aws)"));
                assertTrue(warning, warning.contains("no fess_indices/_" + type + " directory"));
                assertTrue(warning, warning.contains("the default index definitions are used"));
                assertTrue(warning, warning.contains("CodeLibs plugins"));
                assertTrue(warning, warning.contains("set search_engine.type=vanilla"));
                assertTrue(warning, warning.contains("case-sensitive"));
            } finally {
                capture.detach();
            }
        }
    }

    @Test
    public void test_warnUnknownSearchEngineType_silentForKnownTypes() {
        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            final SearchEngineClient client = new ClassPathClient(true);
            for (final String type : new String[] { "default", "vanilla", "aws", "cloud" }) {
                client.warnUnknownSearchEngineType(fessConfigOfType(type));
            }
            client.warnUnknownSearchEngineType(fessConfigOfType(null));

            assertEquals(List.of(), capture.messagesOnThisThreadAt(Level.WARN));
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_warnUnknownSearchEngineType_silentWhenTheTypeHasAnOverrideDirectory() {
        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            // either of the two files that an override directory is known by is enough
            new ClassPathClient(true, "fess_indices/_mine/fess.json").warnUnknownSearchEngineType(fessConfigOfType("mine"));
            new ClassPathClient(true, "fess_indices/_mine/fess/doc.json").warnUnknownSearchEngineType(fessConfigOfType("mine"));
            // the directory of another type does not count
            new ClassPathClient(true, "fess_indices/_other/fess.json").warnUnknownSearchEngineType(fessConfigOfType("mine"));

            assertEquals(1, capture.messagesOnThisThreadAt(Level.WARN).size());
            assertTrue(capture.messagesOnThisThreadAt(Level.WARN).get(0).contains("no fess_indices/_mine directory"));
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_warnUnknownSearchEngineType_silentInJobProcesses() {
        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            new ClassPathClient(false).warnUnknownSearchEngineType(fessConfigOfType("Vanilla"));

            assertEquals(List.of(), capture.messagesOnThisThreadAt(Level.WARN));
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_warnUnknownSearchEngineType_looksUpTheRealClassPath() {
        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            final SearchEngineClient client = new ProcessAwareClient(true);
            // src/test/resources holds fess_indices/_testtype/fess.json
            client.warnUnknownSearchEngineType(fessConfigOfType("testtype"));
            assertEquals(List.of(), capture.messagesOnThisThreadAt(Level.WARN));

            client.warnUnknownSearchEngineType(fessConfigOfType("no_such_type"));
            assertEquals(1, capture.messagesOnThisThreadAt(Level.WARN).size());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_warnIgnoredIndexOverrides_namesTheFilesThatAreNoLongerRead() {
        final String[] removed =
                { "fess_indices/_aws/fess.json", "fess_indices/_cloud/fess/doc.json", "suggest_indices/_aws/suggest_analyzer.json" };
        for (final String type : new String[] { "aws", "cloud" }) {
            final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
            try {
                new ClassPathClient(true, removed).warnIgnoredIndexOverrides(fessConfigOfType(type));

                final List<String> warnings = capture.messagesOnThisThreadAt(Level.WARN);
                assertEquals(type, 1, warnings.size());
                final String warning = warnings.get(0);
                assertTrue(warning, warning.contains("search_engine.type=" + type + " reads the vanilla index definitions"));
                for (final String file : removed) {
                    assertTrue(warning, warning.contains(file));
                }
                assertFalse(warning, warning.contains("fess_indices/_cloud/fess.json"));
                assertTrue(warning, warning.contains("Move them to fess_indices/_vanilla and suggest_indices/_vanilla"));
            } finally {
                capture.detach();
            }
        }
    }

    @Test
    public void test_warnIgnoredIndexOverrides_probesEveryFileThatTheRemovedDirectoriesHeld() {
        for (final String file : SearchEngineClient.REMOVED_OVERRIDE_FILES) {
            final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
            try {
                new ClassPathClient(true, file).warnIgnoredIndexOverrides(fessConfigOfType("aws"));

                final List<String> warnings = capture.messagesOnThisThreadAt(Level.WARN);
                assertEquals(file, 1, warnings.size());
                assertTrue(warnings.get(0), warnings.get(0).contains("[" + file + "]"));
            } finally {
                capture.detach();
            }
        }
        assertEquals(Set.of("fess_indices/_aws/fess.json", "fess_indices/_aws/fess/doc.json", "fess_indices/_cloud/fess.json",
                "fess_indices/_cloud/fess/doc.json", "suggest_indices/_aws/suggest_analyzer.json",
                "suggest_indices/_cloud/suggest_analyzer.json"), new HashSet<>(SearchEngineClient.REMOVED_OVERRIDE_FILES));
    }

    @Test
    public void test_warnIgnoredIndexOverrides_silentWhenThereIsNothingToIgnore() {
        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            final String[] removed = { "fess_indices/_aws/fess.json", "suggest_indices/_cloud/suggest_analyzer.json" };
            // no such file on the class path
            new ClassPathClient(true).warnIgnoredIndexOverrides(fessConfigOfType("aws"));
            new ClassPathClient(true).warnIgnoredIndexOverrides(fessConfigOfType("cloud"));
            // the other types never read those directories
            for (final String type : new String[] { "vanilla", "default", "opensearch", "" }) {
                new ClassPathClient(true, removed).warnIgnoredIndexOverrides(fessConfigOfType(type));
            }
            new ClassPathClient(true, removed).warnIgnoredIndexOverrides(fessConfigOfType(null));
            // a job process does not repeat the warning
            new ClassPathClient(false, removed).warnIgnoredIndexOverrides(fessConfigOfType("aws"));

            assertEquals(List.of(), capture.messagesOnThisThreadAt(Level.WARN));
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_warnIgnoredIndexOverrides_nothingIsShippedInTheRemovedDirectoriesOfTheRealClassPath() {
        // the shipped build must not carry them, or every aws and cloud start would warn
        final SearchEngineClient client = new ProcessAwareClient(true);
        for (final String file : SearchEngineClient.REMOVED_OVERRIDE_FILES) {
            assertFalse(file, client.isOnClassPath(file));
        }
    }

    // -------------------------------------------------------------------------------------
    //                                                                              open order
    //                                                                              ----------

    /** Records the steps of {@code open()} in the order they run, with the steps that need an engine stubbed out. */
    private static class OpenProbe extends SearchEngineClient {
        final List<String> calls = new ArrayList<>();

        RuntimeException yellowFailure;

        OpenProbe() {
            addIndexConfig("fess/doc");
        }

        @Override
        protected void warnDeprecatedSearchEngineType(final FessConfig fessConfig) {
            calls.add("warnDeprecatedSearchEngineType");
        }

        @Override
        protected void warnUnknownSearchEngineType(final FessConfig fessConfig) {
            calls.add("warnUnknownSearchEngineType");
        }

        @Override
        protected void warnIgnoredIndexOverrides(final FessConfig fessConfig) {
            calls.add("warnIgnoredIndexOverrides");
        }

        @Override
        protected Client createHttpClient(final FessConfig fessConfig, final String host) {
            calls.add("createHttpClient");
            return null;
        }

        @Override
        protected void waitForYellowStatus(final FessConfig fessConfig) {
            calls.add("waitForYellowStatus");
            if (yellowFailure != null) {
                throw yellowFailure;
            }
        }

        @Override
        protected void verifyEngineVersion() {
            calls.add("verifyEngineVersion");
        }

        @Override
        protected void checkEnginePlugins(final FessConfig fessConfig) {
            calls.add("checkEnginePlugins");
        }

        @Override
        protected String setUpDocumentIndex(final FessConfig fessConfig, final String configIndex) {
            calls.add("setUpDocumentIndex:" + configIndex);
            return "fess.20260101000000000";
        }

        @Override
        public void addMapping(final String index, final String docType, final String indexName) {
            calls.add("addMapping:" + index + "/" + docType);
        }
    }

    /** Runs {@code open()}, which publishes the engine address as a system property: put it back as it was. */
    private static void openKeepingTheAddressProperty(final SearchEngineClient client) {
        final String before = System.getProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);
        try {
            client.open();
        } finally {
            if (before == null) {
                System.clearProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS);
            } else {
                System.setProperty(Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS, before);
            }
        }
    }

    @Test
    public void test_open_runsTheChecksBeforeAnyIndexIsTouched() {
        ComponentUtil.setFessConfig(fessConfigOfType("vanilla"));
        final OpenProbe client = new OpenProbe();

        openKeepingTheAddressProperty(client);

        // the plugin check needs the engine to answer, so it comes after the status and version checks,
        // and it must come before an index is created from definitions that rely on the plugins
        assertEquals(List.of("warnDeprecatedSearchEngineType", "warnUnknownSearchEngineType", "warnIgnoredIndexOverrides",
                "createHttpClient", "waitForYellowStatus", "verifyEngineVersion", "checkEnginePlugins", "setUpDocumentIndex:fess",
                "addMapping:fess/doc"), client.calls);
    }

    @Test
    public void test_open_doesNotCheckThePluginsOfAnEngineThatNeverBecameAvailable() {
        ComponentUtil.setFessConfig(fessConfigOfType("vanilla"));
        final OpenProbe client = new OpenProbe();
        client.yellowFailure = new ContainerInitFailureException("not available", null);

        try {
            openKeepingTheAddressProperty(client);
            fail("an engine that does not become available must fail the startup");
        } catch (final ContainerInitFailureException e) {
            assertSame(client.yellowFailure, e);
        }

        assertEquals(List.of("warnDeprecatedSearchEngineType", "warnUnknownSearchEngineType", "warnIgnoredIndexOverrides",
                "createHttpClient", "waitForYellowStatus"), client.calls);
    }

    // -------------------------------------------------------------------------------------
    //                                                                        flush of config
    //                                                                        ---------------

    private static CurlResponse curlResponse(final int statusCode, final String body) {
        final CurlResponse response = new CurlResponse();
        response.setHttpStatusCode(statusCode);
        response.setEncoding("UTF-8");
        response.setContentCache(new ContentCache(body.getBytes(StandardCharsets.UTF_8)));
        return response;
    }

    /** Registers a curl helper that answers every GET with {@code answer} and records "path?key=value&..." of each request. */
    private List<String> registerCurlHelper(final Supplier<CurlResponse> answer) {
        final List<String> requests = new ArrayList<>();
        ComponentUtil.register(new CurlHelper() {
            @Override
            public CurlRequest get(final String path) {
                final StringBuilder request = new StringBuilder(path);
                return new CurlRequest(Curl.Method.GET, "http://localhost:9200") {
                    @Override
                    public CurlRequest param(final String key, final String value) {
                        request.append(request.indexOf("?") < 0 ? '?' : '&').append(key).append('=').append(value);
                        return this;
                    }

                    @Override
                    public CurlResponse execute() {
                        requests.add(request.toString());
                        return answer.get();
                    }
                };
            }
        }, "curlHelper");
        return requests;
    }

    /** Registers a curl helper whose POST either answers 200 or fails, and records "POST path" of each request. */
    private List<String> registerPostingCurlHelper(final boolean fails) {
        final List<String> requests = new ArrayList<>();
        ComponentUtil.register(new CurlHelper() {
            @Override
            public CurlRequest post(final String path) {
                return new CurlRequest(Curl.Method.POST, "http://localhost:9200") {
                    @Override
                    public void execute(final Consumer<CurlResponse> actionListener, final Consumer<Exception> exceptionListener) {
                        requests.add("POST " + path);
                        if (fails) {
                            exceptionListener.accept(new IllegalStateException("Connection refused"));
                        } else {
                            actionListener.accept(curlResponse(200, "{}"));
                        }
                    }
                };
            }
        }, "curlHelper");
        return requests;
    }

    @Test
    public void test_flushConfigFiles_postsToConfigSyncWhenThePluginsAreAvailable() {
        for (final boolean fails : new boolean[] { false, true }) {
            for (final String type : new String[] { "default", "opensearch", "", null }) {
                final List<String> requests = registerPostingCurlHelper(fails);
                ComponentUtil.setFessConfig(fessConfigOfType(type));
                final int[] callbackCalls = { 0 };

                new SearchEngineClient().flushConfigFiles(() -> callbackCalls[0]++);

                final String name = type + (fails ? " (flush fails)" : "");
                assertEquals(name, List.of("POST /_configsync/flush"), requests);
                // the callback runs once whether the flush worked or not
                assertEquals(name, 1, callbackCalls[0]);
            }
        }
    }

    @Test
    public void test_flushConfigFiles_isSkippedWhenPluginless() {
        for (final String type : new String[] { "vanilla", "aws", "cloud" }) {
            final List<String> requests = registerPostingCurlHelper(false);
            ComponentUtil.setFessConfig(fessConfigOfType(type));
            final int[] callbackCalls = { 0 };

            new SearchEngineClient().flushConfigFiles(() -> callbackCalls[0]++);

            assertEquals(type, List.of(), requests);
            assertEquals(type, 1, callbackCalls[0]);
        }
    }

    // -------------------------------------------------------------------------------------
    //                                                                      plugin pre-flight
    //                                                                      -----------------

    private static final String STOCK_PLUGINS_JSON =
            "[{\"component\":\"analysis-kuromoji\"},{\"component\":\"analysis-nori\"},{\"component\":\"analysis-smartcn\"},"
                    + "{\"component\":\"opensearch-knn\"},{\"component\":\"opensearch-security\"}]";

    @Test
    public void test_parseInstalledPlugins_dropsTheOpenSearchPrefix() {
        assertEquals(List.of("analysis-kuromoji", "analysis-nori", "analysis-smartcn", "knn", "security"),
                SearchEngineClient.parseInstalledPlugins(STOCK_PLUGINS_JSON));
        // only a leading prefix goes: a CodeLibs plugin reported under its descriptor name is kept as is
        assertEquals(List.of("analysis-fess", "opensearch-x"), SearchEngineClient
                .parseInstalledPlugins("[{\"component\":\"opensearch-analysis-fess\"},{\"component\":\"opensearch-opensearch-x\"}]"));
    }

    @Test
    public void test_parseInstalledPlugins_skipsEntriesWithoutAName() {
        assertEquals(List.of("knn"), SearchEngineClient
                .parseInstalledPlugins("[{\"other\":\"x\"},{\"component\":null},{\"component\":1},null,{\"component\":\"knn\"}]"));
        assertEquals(List.of(), SearchEngineClient.parseInstalledPlugins("[]"));
    }

    @Test
    public void test_parseInstalledPlugins_rejectsAnAnswerThatIsNotAnArrayOfObjects() {
        for (final String content : new String[] { "", "not json", "{\"error\":\"denied\"}", "[\"knn\"]" }) {
            try {
                SearchEngineClient.parseInstalledPlugins(content);
                fail("must reject: " + content);
            } catch (final RuntimeException e) {
                // the caller turns it into a skipped check
            }
        }
    }

    @Test
    public void test_findMissingPlugins() {
        assertEquals(List.of(), SearchEngineClient.findMissingPlugins(SearchEngineClient.parseInstalledPlugins(STOCK_PLUGINS_JSON)));
        assertEquals(List.of("analysis-nori", "knn"),
                SearchEngineClient.findMissingPlugins(List.of("analysis-kuromoji", "analysis-smartcn", "security")));
        assertEquals(List.of("analysis-kuromoji", "analysis-nori", "analysis-smartcn", "knn"),
                SearchEngineClient.findMissingPlugins(List.of("analysis-fess", "configsync")));
        assertEquals(List.of("analysis-kuromoji", "analysis-nori", "analysis-smartcn", "knn"),
                SearchEngineClient.findMissingPlugins(List.of()));
        // the prefixed spelling is only understood after parseInstalledPlugins dropped it
        assertEquals(List.of("knn"),
                SearchEngineClient.findMissingPlugins(List.of("analysis-kuromoji", "analysis-nori", "analysis-smartcn", "opensearch-knn")));
    }

    @Test
    public void test_findMissingEnginePlugins_listsWhatTheEngineDoesNotReport() {
        final List<String> requests =
                registerCurlHelper(() -> curlResponse(200, "[{\"component\":\"analysis-kuromoji\"},{\"component\":\"analysis-smartcn\"}]"));

        assertEquals(List.of("analysis-nori", "knn"), new SearchEngineClient().findMissingEnginePlugins());
        assertEquals(List.of("/_cat/plugins?h=component&format=json"), requests);
    }

    @Test
    public void test_findMissingEnginePlugins_emptyWhenEveryPluginIsReported() {
        registerCurlHelper(() -> curlResponse(200, STOCK_PLUGINS_JSON));

        assertEquals(List.of(), new SearchEngineClient().findMissingEnginePlugins());
    }

    @Test
    public void test_findMissingEnginePlugins_anEmptyListMeansNoPluginIsInstalled() {
        registerCurlHelper(() -> curlResponse(200, "[]"));

        assertEquals(List.of("analysis-kuromoji", "analysis-nori", "analysis-smartcn", "knn"),
                new SearchEngineClient().findMissingEnginePlugins());
    }

    @Test
    public void test_findMissingEnginePlugins_isNullWhenTheCheckCannotBeMade() {
        final List<Supplier<CurlResponse>> answers = new ArrayList<>();
        answers.add(() -> curlResponse(403, "{\"Message\":\"not authorized to perform es:ESHttpGet\"}"));
        answers.add(() -> curlResponse(404, "{\"error\":\"no handler found for uri [/_cat/plugins]\"}"));
        answers.add(() -> curlResponse(200, "<html>proxy</html>"));
        answers.add(() -> curlResponse(200, "{\"error\":\"not an array\"}"));
        answers.add(() -> curlResponse(200, ""));
        answers.add(() -> {
            throw new IllegalStateException("Connection refused");
        });
        for (int i = 0; i < answers.size(); i++) {
            registerCurlHelper(answers.get(i));

            assertNull(new SearchEngineClient().findMissingEnginePlugins(), "answer " + i);
        }
    }

    /** Stands in for the engine query, so that the gates of {@code checkEnginePlugins} can be seen from the queries it makes. */
    private static class MissingPluginsClient extends ProcessAwareClient {
        final List<String> queries = new ArrayList<>();

        private final List<String> missing;

        MissingPluginsClient(final boolean webapp, final List<String> missing) {
            super(webapp);
            this.missing = missing;
        }

        @Override
        protected List<String> findMissingEnginePlugins() {
            queries.add("findMissingEnginePlugins");
            return missing;
        }
    }

    @Test
    public void test_checkEnginePlugins_asksOnlyThePluginlessEnginesOfTheWebappProcess() {
        for (final String type : new String[] { "vanilla", "aws", "cloud" }) {
            final MissingPluginsClient client = new MissingPluginsClient(true, List.of());
            client.checkEnginePlugins(fessConfigOfType(type));
            assertEquals(type, 1, client.queries.size());

            final MissingPluginsClient jobProcess = new MissingPluginsClient(false, List.of());
            jobProcess.checkEnginePlugins(fessConfigOfType(type));
            assertEquals(type, List.of(), jobProcess.queries);
        }
        // an engine with the CodeLibs plugins brings what the index definitions need
        for (final String type : new String[] { "default", "opensearch", "" }) {
            final MissingPluginsClient client = new MissingPluginsClient(true, List.of("knn"));
            client.checkEnginePlugins(fessConfigOfType(type));
            assertEquals(type, List.of(), client.queries);
        }
        final MissingPluginsClient client = new MissingPluginsClient(true, List.of("knn"));
        client.checkEnginePlugins(fessConfigOfType(null));
        assertEquals(List.of(), client.queries);
    }

    @Test
    public void test_checkEnginePlugins_warnsOnceNamingTheMissingPlugins() {
        for (final String type : new String[] { "vanilla", "aws", "cloud" }) {
            final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
            try {
                new MissingPluginsClient(true, List.of("analysis-nori", "knn")).checkEnginePlugins(fessConfigOfType(type));

                final List<String> warnings = capture.messagesOnThisThreadAt(Level.WARN);
                assertEquals(type, 1, warnings.size());
                final String warning = warnings.get(0);
                assertTrue(warning, warning.contains("does not report the plugins [analysis-nori, knn]"));
                assertTrue(warning, warning.contains("search_engine.type=" + ("cloud".equals(type) ? "vanilla" : type) + " relies on"));
                assertFalse(warning, warning.contains("analysis-kuromoji"));
                assertTrue(warning, warning.contains("Amazon OpenSearch Service"));
                assertTrue(warning, warning.endsWith("The startup continues."));
            } finally {
                capture.detach();
            }
        }
    }

    @Test
    public void test_checkEnginePlugins_warnsNamingAllFourWhenTheEngineReportsNoPlugin() {
        registerCurlHelper(() -> curlResponse(200, "[]"));
        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            new ProcessAwareClient(true).checkEnginePlugins(fessConfigOfType("vanilla"));

            final List<String> warnings = capture.messagesOnThisThreadAt(Level.WARN);
            assertEquals(1, warnings.size());
            assertTrue(warnings.get(0), warnings.get(0).contains("[analysis-kuromoji, analysis-nori, analysis-smartcn, knn]"));
            assertEquals(List.of(), capture.messagesOnThisThreadAt(Level.INFO));
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_checkEnginePlugins_silentWhenNothingIsMissingOrTheCheckWasSkipped() {
        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            new MissingPluginsClient(true, List.of()).checkEnginePlugins(fessConfigOfType("vanilla"));
            new MissingPluginsClient(true, null).checkEnginePlugins(fessConfigOfType("vanilla"));

            assertEquals(List.of(), capture.messagesOnThisThreadAt(Level.WARN));
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_checkEnginePlugins_neverFailsTheStartupAndSaysWhatIsLost() {
        final List<Supplier<CurlResponse>> answers = new ArrayList<>();
        answers.add(() -> curlResponse(403, "{\"Message\":\"not authorized to perform es:ESHttpGet\"}"));
        answers.add(() -> curlResponse(200, "<html>proxy</html>"));
        answers.add(() -> {
            throw new IllegalStateException("Connection refused");
        });
        final List<String> reasons = List.of("GET /_cat/plugins returned HTTP 403.", "GET /_cat/plugins failed: ",
                "GET /_cat/plugins failed: java.lang.IllegalStateException: Connection refused.");
        for (int i = 0; i < answers.size(); i++) {
            registerCurlHelper(answers.get(i));
            final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
            try {
                new ProcessAwareClient(true).checkEnginePlugins(fessConfigOfType("vanilla"));

                assertEquals("answer " + i, List.of(), capture.messagesOnThisThreadAt(Level.WARN));
                final List<String> infos = capture.messagesOnThisThreadAt(Level.INFO);
                assertEquals("answer " + i, 1, infos.size());
                final String info = infos.get(0);
                assertTrue(info, info.startsWith("Skipped the search engine plugin check: " + reasons.get(i).replaceAll("\\.$", "")));
                assertTrue(info, info.contains("Index creation fails with an unspecific error if the search engine lacks the plugins"));
                assertTrue(info, info.contains("[analysis-kuromoji, analysis-nori, analysis-smartcn, knn]"));
            } finally {
                capture.detach();
            }
        }
    }

    @Test
    public void test_checkEnginePlugins_logsTheStackTraceOfAFailedRequestAtDebug() {
        registerCurlHelper(() -> {
            throw new IllegalStateException("Connection refused");
        });
        final LogCapturingAppender capture = LogCapturingAppender.attach(SearchEngineClient.class);
        try {
            new ProcessAwareClient(true).checkEnginePlugins(fessConfigOfType("vanilla"));

            final List<LogEvent> debugs = capture.eventsOnThisThreadAt(Level.DEBUG);
            assertEquals(1, debugs.size());
            assertEquals("The search engine plugin check failed.", debugs.get(0).getMessage().getFormattedMessage());
            assertNotNull(debugs.get(0).getThrown());
            assertEquals("Connection refused", debugs.get(0).getThrown().getMessage());
        } finally {
            capture.detach();
        }
    }

    // -------------------------------------------------------------------------------------
    //                                                                 configsync availability
    //                                                                 -----------------------

    @Test
    public void test_buildConfigSyncUnavailableMessage_isAConditionalHintWithTheLastStatus() {
        final String message = new SearchEngineClient().buildConfigSyncUnavailableMessage(404, new FessSystemException("x"));

        assertEquals("Configsync is not available (HTTP 404). If this search engine does not have the CodeLibs plugins,"
                + " set search_engine.type=vanilla (aws on Amazon OpenSearch Service); otherwise check the cause below.", message);
    }

    @Test
    public void test_buildConfigSyncUnavailableMessage_namesTheExceptionWhenThereWasNoAnswer() {
        final String message = new SearchEngineClient().buildConfigSyncUnavailableMessage(0, new java.net.ConnectException("refused"));

        assertTrue(message, message.startsWith("Configsync is not available (java.net.ConnectException)."));
        assertTrue(message, message.contains("If this search engine does not have the CodeLibs plugins"));
        assertFalse(message, message.contains("seem to be missing"));
    }

    @Test
    public void test_waitForConfigSyncStatus_failureCarriesTheStatusAndTheHint() {
        final List<String> requests = registerCurlHelper(() -> curlResponse(404, "{\"error\":\"no handler found\"}"));
        final SearchEngineClient client = new SearchEngineClient();
        client.setMaxConfigSyncStatusRetry(1);
        try {
            client.waitForConfigSyncStatus();
            fail("an engine without configsync must fail");
        } catch (final FessSystemException e) {
            assertEquals(List.of("/_configsync/wait?status=green"), requests);
            assertTrue(e.getMessage(), e.getMessage().startsWith("Configsync is not available (HTTP 404)."));
            assertTrue(e.getMessage(), e.getMessage().contains("search_engine.type=vanilla"));
            assertTrue(String.valueOf(e.getCause()), e.getCause().getMessage().contains("HTTP Status is 404"));
        }
    }

    @Test
    public void test_waitForConfigSyncStatus_failureWithoutAnAnswerNamesTheExceptionClass() {
        registerCurlHelper(() -> {
            throw new IllegalStateException("Connection refused");
        });
        final SearchEngineClient client = new SearchEngineClient();
        client.setMaxConfigSyncStatusRetry(1);
        try {
            client.waitForConfigSyncStatus();
            fail("an unreachable engine must fail");
        } catch (final FessSystemException e) {
            assertTrue(e.getMessage(), e.getMessage().startsWith("Configsync is not available (java.lang.IllegalStateException)."));
            assertEquals("Connection refused", e.getCause().getMessage());
        }
    }

    @Test
    public void test_waitForConfigSyncStatus_returnsOnceConfigSyncIsReady() {
        final List<String> requests = registerCurlHelper(() -> curlResponse(200, "{}"));
        final SearchEngineClient client = new SearchEngineClient();
        client.setMaxConfigSyncStatusRetry(1);

        client.waitForConfigSyncStatus();

        assertEquals(List.of("/_configsync/wait?status=green"), requests);
    }
}
