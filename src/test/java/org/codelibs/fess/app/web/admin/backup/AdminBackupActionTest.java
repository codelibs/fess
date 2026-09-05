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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.codelibs.fess.app.web.base.FessBaseAction;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.client.SearchEngineClientException;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.BooleanFunction;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.search.SearchHit;

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
