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
package org.codelibs.fess.app.web.admin.backup;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.client.SearchEngineClientException;
import org.codelibs.fess.opensearch.log.exbhv.SearchLogBhv;
import org.codelibs.fess.opensearch.log.exentity.SearchLog;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.BooleanFunction;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fesen.opensearch.action.search.SearchResponse;
import org.codelibs.fesen.opensearch.search.SearchHit;

public class AdminBackupActionTest extends UnitFessTestCase {

    /**
     * Closing a single member of the alias, which reindexing and rolling restarts both do, makes
     * the point in time fail. That failure used to happen inside the stream callback, once the
     * framework had already committed 200 and the Content-Disposition header, so the browser saved
     * a named attachment of 0 bytes where the same request had served 93,858 bytes a moment
     * earlier, and fess.log gained no line at all. The download must instead fail while the
     * response can still say so.
     */
    @Test
    public void test_download_failsBeforeTheResponseIsCommitted() throws Exception {
        registerFailingClient("[fess_config] Failed to open a point in time.");

        final SearchEngineClientException e =
                Assertions.assertThrows(SearchEngineClientException.class, () -> newAction().download("fess_config.bulk"));
        assertTrue(e.getMessage().contains("fess_config"), "the failure must name the index: " + e.getMessage());
    }

    /**
     * Whatever else happens, the failure has to be diagnosable: the log must carry the index that
     * could not be read and the cause underneath it.
     */
    @Test
    public void test_download_logsTheIndexAndTheCause() throws Exception {
        registerFailingClient("[fess_config] Failed to open a point in time.");
        final CapturingAppender appender = new CapturingAppender();
        final org.apache.logging.log4j.core.Logger logger =
                (org.apache.logging.log4j.core.Logger) LogManager.getLogger(AdminBackupAction.class);
        appender.start();
        logger.addAppender(appender);
        try {
            Assertions.assertThrows(SearchEngineClientException.class, () -> newAction().download("fess_config.bulk"));
        } finally {
            logger.removeAppender(appender);
            appender.stop();
        }

        final List<LogEvent> warnings = appender.warnings();
        assertEquals(1, warnings.size(), "exactly one warning must be reported: " + warnings);
        final LogEvent event = warnings.get(0);
        Assertions.assertEquals(org.apache.logging.log4j.Level.WARN, event.getLevel(),
                "a backup that cannot be read does not stop the system, so it is a warning, not an error");
        assertTrue(event.getMessage().getFormattedMessage().contains("fess_config"),
                "the log line must name the index: " + event.getMessage().getFormattedMessage());
        assertNotNull(event.getThrown(), "the log line must carry the cause");
        assertEquals(SearchEngineClientException.class, event.getThrown().getClass());
    }

    /**
     * Restoring a 15.8 backup onto a 15.9 install must not put the 15.8 copy of a bundled job
     * (updatedTime 0, Groovy) over the 15.9 one, but must still bring back what was changed or
     * created after the install, and documents that carry no updatedTime.
     */
    @Test
    public void test_writeNewerThanStored_importsOnlyWhatIsNewer() throws Exception {
        final Path dir = Files.createTempDirectory("restore_test");
        try {
            final Path src = dir.resolve("src.bulk");
            Files.writeString(src, String.join("\n", "{\"index\":{\"_index\":\".fess_config.scheduled_job\",\"_id\":\"bundled\"}}",
                    "{\"name\":\"b\",\"updatedTime\":0}", "{\"index\":{\"_index\":\"fess_config.scheduled_job\",\"_id\":\"edited\"}}",
                    "{\"name\":\"e\",\"updatedTime\":200}", "{\"index\":{\"_index\":\"fess_config.scheduled_job\",\"_id\":\"older\"}}",
                    "{\"name\":\"o\",\"updatedTime\":100}", "{\"index\":{\"_index\":\"fess_config.scheduled_job\",\"_id\":\"new\"}}",
                    "{\"name\":\"n\",\"updatedTime\":50}", "{\"index\":{\"_index\":\"fess_config.label_type\",\"_id\":\"nostamp\"}}",
                    "{\"name\":\"l\"}", ""));
            final Path dst = dir.resolve("dst.bulk");
            final AdminBackupAction action = new AdminBackupAction() {
                @Override
                protected Long getStoredUpdatedTime(final String index, final String id) {
                    switch (id) {
                    case "bundled":
                        return 0L;
                    case "edited":
                        return 100L;
                    case "older":
                        return 300L;
                    default:
                        return null;
                    }
                }
            };
            final AtomicBoolean resetJobs = new AtomicBoolean(false);

            final int count = action.writeNewerThanStored(new ObjectMapper(), src.toFile(), dst.toFile(), resetJobs);

            final String out = Files.readString(dst);
            assertEquals(3, count);
            assertFalse(out.contains("\"bundled\""), out);
            assertFalse(out.contains("\"older\""), out);
            assertTrue(out.contains("\"edited\""), out);
            assertTrue(out.contains("\"new\""), out);
            assertTrue(out.contains("\"nostamp\""), out);
            assertTrue(resetJobs.get());
        } finally {
            deleteRecursively(dir);
        }
    }

    /**
     * When every scheduled job in the backup is skipped, the jobs are not reset.
     */
    @Test
    public void test_writeNewerThanStored_doesNotResetJobsWhenAllSkipped() throws Exception {
        final Path dir = Files.createTempDirectory("restore_test");
        try {
            final Path src = dir.resolve("src.bulk");
            Files.writeString(src, "{\"index\":{\"_index\":\"fess_config.scheduled_job\",\"_id\":\"bundled\"}}\n"
                    + "{\"name\":\"b\",\"updatedTime\":0}\n");
            final Path dst = dir.resolve("dst.bulk");
            final AdminBackupAction action = new AdminBackupAction() {
                @Override
                protected Long getStoredUpdatedTime(final String index, final String id) {
                    return 0L;
                }
            };
            final AtomicBoolean resetJobs = new AtomicBoolean(false);

            assertEquals(0, action.writeNewerThanStored(new ObjectMapper(), src.toFile(), dst.toFile(), resetJobs));
            assertEquals("", Files.readString(dst));
            assertFalse(resetJobs.get());
        } finally {
            deleteRecursively(dir);
        }
    }

    // ===================================================================================
    //                                                                            Helpers
    //                                                                            =======

    private AdminBackupAction newAction() throws Exception {
        final AdminBackupAction action = new AdminBackupAction();
        final Field field = FessBaseAction.class.getDeclaredField("fessConfig");
        field.setAccessible(true);
        field.set(action, ComponentUtil.getFessConfig());
        return action;
    }

    /**
     * Registers a search engine client that cannot walk anything, the way a closed member index
     * makes the real one behave.
     */
    private void registerFailingClient(final String message) {
        ComponentUtil.register(new SearchEngineClient() {
            @Override
            public <T> long scrollSearch(final String index, final SearchCondition<SearchRequestBuilder> condition,
                    final EntityCreator<T, SearchResponse, SearchHit> creator, final BooleanFunction<T> cursor) {
                throw new SearchEngineClientException(message);
            }
        }, "searchEngineClient");
    }

    /**
     * The mapping files the backup carries are the ones the search engine client creates the index
     * from, which for the plugin-less types are the files under {@code fess_indices/_vanilla}.
     */
    @Test
    public void test_resolveIndexConfigPath_usesTheDirectoryOfTheResourceType() throws Exception {
        final Path classesDir = Files.createTempDirectory("fess_backup_classes");
        try {
            final Path indicesDir = Files.createDirectories(classesDir.resolve("fess_indices"));
            Files.createDirectories(indicesDir.resolve("_vanilla").resolve("fess"));
            Files.writeString(indicesDir.resolve("_vanilla").resolve("fess.json"), "{}");
            Files.writeString(indicesDir.resolve("_vanilla").resolve("fess").resolve("doc.json"), "{}");

            assertEquals(indicesDir.resolve("_vanilla").resolve("fess.json"),
                    AdminBackupAction.resolveIndexConfigPath(classesDir, "vanilla", "fess.json"));
            assertEquals(indicesDir.resolve("_vanilla").resolve("fess").resolve("doc.json"),
                    AdminBackupAction.resolveIndexConfigPath(classesDir, "vanilla", "fess", "doc.json"));
        } finally {
            deleteRecursively(classesDir);
        }
    }

    @Test
    public void test_resolveIndexConfigPath_fallsBackToTheDefaultFiles() throws Exception {
        final Path classesDir = Files.createTempDirectory("fess_backup_classes");
        try {
            final Path indicesDir = Files.createDirectories(classesDir.resolve("fess_indices"));
            // no _default directory, as in the distribution; _vanilla holds only fess.json
            Files.createDirectories(indicesDir.resolve("_vanilla"));
            Files.writeString(indicesDir.resolve("_vanilla").resolve("fess.json"), "{}");

            assertEquals(indicesDir.resolve("fess.json"), AdminBackupAction.resolveIndexConfigPath(classesDir, "default", "fess.json"));
            assertEquals(indicesDir.resolve("fess").resolve("doc.json"),
                    AdminBackupAction.resolveIndexConfigPath(classesDir, "vanilla", "fess", "doc.json"));
            assertEquals(indicesDir.resolve("fess.json"), AdminBackupAction.resolveIndexConfigPath(classesDir, "opensearch", "fess.json"));
        } finally {
            deleteRecursively(classesDir);
        }
    }

    /**
     * The files the backup carries are the ones the index is created from, so they follow the
     * resource type of the search engine type. The classes directory is the source tree here,
     * which holds the shipped index definitions and cannot be stale.
     */
    @Test
    public void test_getFessJsonPathAndGetDocJsonPath_followTheResourceTypeOfTheSearchEngine() {
        final Path classesDir = Paths.get("src/main/resources");
        for (final String type : new String[] { "vanilla", "aws", "cloud" }) {
            useSearchEngineType(type);

            final Path fessJson = AdminBackupAction.getFessJsonPath(classesDir);
            final Path docJson = AdminBackupAction.getDocJsonPath(classesDir);

            assertTrue(type + ": " + fessJson, fessJson.endsWith(Paths.get("fess_indices", "_vanilla", "fess.json")));
            assertTrue(type + ": " + docJson, docJson.endsWith(Paths.get("fess_indices", "_vanilla", "fess", "doc.json")));
            assertTrue(type, Files.exists(fessJson));
            assertTrue(type, Files.exists(docJson));
        }
        for (final String type : new String[] { "default", "opensearch", "" }) {
            useSearchEngineType(type);

            final Path fessJson = AdminBackupAction.getFessJsonPath(classesDir);
            final Path docJson = AdminBackupAction.getDocJsonPath(classesDir);

            assertTrue(type + ": " + fessJson, fessJson.endsWith(Paths.get("fess_indices", "fess.json")));
            assertTrue(type + ": " + docJson, docJson.endsWith(Paths.get("fess_indices", "fess", "doc.json")));
            assertTrue(type, Files.exists(fessJson));
            assertTrue(type, Files.exists(docJson));
        }
    }

    /**
     * The response of a log export is committed before the rows are read, so a cut-off caused by the
     * CPU load leaves the client a 200 with a shortened file. The warning is the only trace of it.
     */
    @Test
    public void test_searchLogExport_warnsWhenCutShortByCpuLoad() throws Exception {
        final String warnings = exportSearchLogs(false);

        assertTrue(warnings.contains("search_log"), "the log line must name the export: " + warnings);
        assertTrue(warnings.contains("after 1 rows"), "the log line must carry the number of rows written: " + warnings);
        assertTrue(warnings.contains("adaptive.load.control"), "the log line must name the setting: " + warnings);
        assertFalse(warnings.contains("secret-query"), "the log line must not carry the exported data: " + warnings);
    }

    @Test
    public void test_searchLogExport_doesNotWarnWhenComplete() throws Exception {
        assertEquals("", exportSearchLogs(true));
    }

    /**
     * Exports three search logs through a Bhv that honors the cursor break, and returns the formatted
     * warnings logged meanwhile.
     */
    private String exportSearchLogs(final boolean loadWithinLimit) throws Exception {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer getIndexBackupLogLoadTimeoutAsInteger() {
                return 100;
            }
        });
        ComponentUtil.register(new SystemHelper() {
            @Override
            public boolean calibrateCpuLoad(final long timeoutInMillis) {
                return loadWithinLimit;
            }
        }, "systemHelper");
        ComponentUtil.register(new SearchLogBhv() {
            @Override
            public void selectCursor(final org.dbflute.bhv.readable.CBCall<org.codelibs.fess.opensearch.log.cbean.SearchLogCB> cbLambda,
                    final org.dbflute.bhv.readable.EntityRowHandler<SearchLog> entityLambda) {
                for (int i = 0; i < 3; i++) {
                    final SearchLog log = new SearchLog();
                    log.setSearchWord("secret-query");
                    entityLambda.handle(log);
                    if (entityLambda.isBreakCursor()) {
                        break;
                    }
                }
            }
        }, SearchLogBhv.class.getCanonicalName());

        final CapturingAppender appender = new CapturingAppender();
        final org.apache.logging.log4j.core.Logger logger =
                (org.apache.logging.log4j.core.Logger) LogManager.getLogger(AdminBackupAction.class);
        appender.start();
        logger.addAppender(appender);
        final StringWriter writer = new StringWriter();
        try {
            AdminBackupAction.getSearchLogNdjsonWriteCall().accept(writer);
        } finally {
            logger.removeAppender(appender);
            appender.stop();
        }

        final long lines = writer.toString().lines().count();
        assertEquals(loadWithinLimit ? 3 : 1, lines);
        final StringBuilder buf = new StringBuilder();
        for (final LogEvent event : appender.warnings()) {
            buf.append(event.getMessage().getFormattedMessage());
        }
        return buf.toString();
    }

    private static void useSearchEngineType(final String type) {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getSearchEngineType() {
                return type;
            }
        });
    }

    private static void deleteRecursively(final Path dir) throws IOException {
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> path.toFile().delete());
        }
    }

    private static class CapturingAppender extends AbstractAppender {

        private final List<LogEvent> events = new ArrayList<>();

        CapturingAppender() {
            super("capturing", (Filter) null, (Layout<? extends Serializable>) null, true, Property.EMPTY_ARRAY);
        }

        @Override
        public void append(final LogEvent event) {
            events.add(event.toImmutable());
        }

        List<LogEvent> warnings() {
            final List<LogEvent> list = new ArrayList<>();
            for (final LogEvent event : events) {
                if (event.getLevel().isMoreSpecificThan(org.apache.logging.log4j.Level.WARN)) {
                    list.add(event);
                }
            }
            return list;
        }
    }
}
