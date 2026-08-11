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
package org.codelibs.fess.unit;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.jupiter.api.Test;

/**
 * Pins the contract that keeps {@link LogCapturingAppender} usable while surefire runs with
 * {@code -Dparallel=classes}: the level of the captured logger must not be inherited from an
 * ancestor config another test class can be rewriting at that instant.
 *
 * <p>The ancestor used here is created by this test and lives under {@code fess.test.} rather
 * than {@code org.codelibs}, so the test cannot itself commit the bug it guards against.</p>
 */
public class LogCapturingAppenderTest extends UnitFessTestCase {

    private static final String ANCESTOR_NAME = "fess.test.logcapture";

    private static final String TARGET_NAME = ANCESTOR_NAME + ".Target";

    @Test
    public void test_attach_ignoresAnAncestorLevelRaisedByAnotherThread() {
        withAncestorAt(Level.WARN, () -> {
            final LogCapturingAppender appender = LogCapturingAppender.attach(TARGET_NAME);
            try {
                LogManager.getLogger(TARGET_NAME).info("info survives");
                LogManager.getLogger(TARGET_NAME).warn("warn survives");
                assertEquals("an ancestor at WARN during attach() must not suppress INFO capture", List.of("info survives"),
                        appender.messagesAt(Level.INFO));
                assertEquals("WARN capture must keep working", List.of("warn survives"), appender.warnings());
            } finally {
                appender.detach();
            }
        });
    }

    @Test
    public void test_attach_neverMutatesTheAncestorItResolvedThrough() {
        withAncestorAt(Level.WARN, () -> {
            final LogCapturingAppender appender = LogCapturingAppender.attach(TARGET_NAME);
            try {
                assertEquals("attach() must leave the shared ancestor config untouched", Level.WARN, ancestorLevel());
            } finally {
                appender.detach();
            }
            assertEquals("detach() must leave the shared ancestor config untouched", Level.WARN, ancestorLevel());
        });
    }

    @Test
    public void test_attach_restoresTheLevelOfAConfigDeclaredForTheExactName() {
        // The borrow branch: a config already exists under the captured name, so attach() cannot
        // create its own and has to put the level back. Reading the level after writing it would
        // make that restore a silent no-op.
        withAncestorAt(Level.WARN, () -> {
            final LogCapturingAppender appender = LogCapturingAppender.attach(ANCESTOR_NAME, Level.DEBUG);
            try {
                LogManager.getLogger(ANCESTOR_NAME).debug("borrowed");
                assertEquals("the borrowed config must be lowered to the requested level", List.of("borrowed"),
                        appender.messagesAt(Level.DEBUG));
            } finally {
                appender.detach();
            }
            assertEquals("detach() must put the borrowed config's level back", Level.WARN, ancestorLevel());
        });
    }

    @Test
    public void test_detach_leavesNoPinnedLoggerConfigBehind() {
        withAncestorAt(Level.WARN, () -> {
            LogCapturingAppender.attach(TARGET_NAME).detach();
            assertEquals("detach() must remove the LoggerConfig attach() created, or the next attach() reuses its level", ANCESTOR_NAME,
                    configuration().getLoggerConfig(TARGET_NAME).getName());
        });
    }

    // -------------------------------------------------------------------------------------
    //                                                                                helpers
    //                                                                                -------

    private static Configuration configuration() {
        return ((LoggerContext) LogManager.getContext(false)).getConfiguration();
    }

    private static Level ancestorLevel() {
        return configuration().getLoggerConfig(ANCESTOR_NAME).getLevel();
    }

    /**
     * Installs a {@code fess.test.logcapture} config at {@code level} for the duration of
     * {@code body}. This stands in for the concurrent test class that raises a shared level.
     */
    private static void withAncestorAt(final Level level, final Runnable body) {
        final LoggerContext context = (LoggerContext) LogManager.getContext(false);
        final Configuration configuration = context.getConfiguration();
        configuration.addLogger(ANCESTOR_NAME, new LoggerConfig(ANCESTOR_NAME, level, false));
        context.updateLoggers();
        try {
            body.run();
        } finally {
            configuration.removeLogger(ANCESTOR_NAME);
            context.updateLoggers();
        }
    }
}
