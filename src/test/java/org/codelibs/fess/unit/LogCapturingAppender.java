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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Property;

/**
 * In-memory log4j2 appender for asserting on the messages a class emits.
 *
 * <p><b>Never mutate a {@link LoggerConfig} you do not own.</b> {@code log4j2.xml} declares
 * configs for {@code org.codelibs}, {@code org.dbflute}, {@code org.lastaflute},
 * {@code org.codelibs.fess.llm}, {@code org.codelibs.fess.chat} and
 * {@code org.codelibs.fess.app.web.chat} only, so
 * {@code configuration.getLoggerConfig(SomeFessClass.class.getName())} walks up and hands back
 * the <em>shared</em> {@code org.codelibs} config. Calling {@code setLevel} on that handle
 * rewrites the level for every {@code org.codelibs} logger in the JVM. CI runs surefire with
 * {@code -Dparallel=classes}, so the classes running alongside see it.</p>
 *
 * <p>That is not merely a transient window. {@code AbstractConfiguration#addLoggerAppender}
 * creates a per-logger {@code LoggerConfig} with {@code new LoggerConfig(name, ancestor.getLevel(),
 * ...)} and registers it with {@code putIfAbsent}: the ancestor level is <em>snapshotted</em> and
 * never revisited. An {@code attach} that lands inside somebody else's WARN window therefore pins
 * the target logger at WARN for the rest of the surefire fork, and every later INFO/DEBUG
 * assertion on that class sees an empty list while WARN assertions still pass.</p>
 *
 * <p>This appender avoids both halves of that trap: it installs a {@code LoggerConfig} dedicated
 * to the target logger name at an explicit level before attaching, and removes it again on
 * {@link #detach()}. Ancestor configs are never read for a level and never written.</p>
 */
public final class LogCapturingAppender extends AbstractAppender {

    /** Level forced on the captured logger unless the caller asks for another one. */
    private static final Level DEFAULT_LEVEL = Level.DEBUG;

    /**
     * What {@link #attach} did to the {@link LoggerConfig} of one logger name, shared by every
     * appender attached to that name at the same time.
     *
     * <p>CI runs surefire with {@code -Dparallel=classes}, so two classes can capture the same
     * logger at once. The second {@code attach} then finds the first one's dedicated config and
     * borrows it. Without counting, the first {@code detach} would remove that config together
     * with the second class's appender, and the second class would read an empty capture.</p>
     */
    private static final class Attachment {

        /** Set when the first attach created the dedicated config, which the last detach must remove. */
        final boolean ownsLoggerConfig;

        /** Level to put back when the config was not ours to create. */
        final Level restoredLevel;

        /** Appenders attached to the logger name right now. */
        int count;

        Attachment(final boolean ownsLoggerConfig, final Level restoredLevel) {
            this.ownsLoggerConfig = ownsLoggerConfig;
            this.restoredLevel = restoredLevel;
        }
    }

    private static final Map<String, Attachment> ATTACHMENTS = new HashMap<>();

    private final List<LogEvent> events = new CopyOnWriteArrayList<>();

    private final String loggerName;

    private LogCapturingAppender(final String loggerName) {
        super("LogCapturingAppender-" + UUID.randomUUID(), null, null, true, Property.EMPTY_ARRAY);
        this.loggerName = loggerName;
    }

    /** Captures {@code targetClass}'s logger from {@link #DEFAULT_LEVEL} up. */
    public static LogCapturingAppender attach(final Class<?> targetClass) {
        return attach(targetClass.getName(), DEFAULT_LEVEL);
    }

    /** Captures {@code loggerName} from {@link #DEFAULT_LEVEL} up. */
    public static LogCapturingAppender attach(final String loggerName) {
        return attach(loggerName, DEFAULT_LEVEL);
    }

    /**
     * Captures {@code loggerName} from {@code level} up. The level is applied to a
     * {@code LoggerConfig} dedicated to {@code loggerName}, so no other logger is affected.
     */
    public static LogCapturingAppender attach(final String loggerName, final Level level) {
        synchronized (ATTACHMENTS) {
            final LoggerContext context = (LoggerContext) LogManager.getContext(false);
            final Configuration configuration = context.getConfiguration();
            final LoggerConfig resolved = configuration.getLoggerConfig(loggerName);
            Attachment attachment = ATTACHMENTS.get(loggerName);
            final LoggerConfig dedicated;
            if (attachment != null && resolved.getName().equals(loggerName)) {
                // Another capture of the same logger is active: share its config.
                dedicated = resolved;
                dedicated.setLevel(level);
            } else if (!resolved.getName().equals(loggerName)) {
                // The ancestor's level is deliberately NOT copied: it may be mid-rewrite in another thread.
                dedicated = new LoggerConfig(loggerName, level, resolved.isAdditive());
                dedicated.setParent(resolved);
                configuration.addLogger(loggerName, dedicated);
                attachment = new Attachment(true, null);
                ATTACHMENTS.put(loggerName, attachment);
            } else {
                // A config declared for this exact name, or a leftover: borrow it and put its level back later.
                // Read before the setLevel below, or detach() would restore the level we just wrote.
                attachment = new Attachment(false, resolved.getLevel());
                ATTACHMENTS.put(loggerName, attachment);
                dedicated = resolved;
                dedicated.setLevel(level);
            }
            attachment.count++;
            final LogCapturingAppender appender = new LogCapturingAppender(loggerName);
            appender.start();
            dedicated.addAppender(appender, null, null);
            context.updateLoggers();
            return appender;
        }
    }

    /**
     * Detaches the appender and, once the last capture of the logger is gone, undoes the
     * {@link LoggerConfig} change {@link #attach} made.
     */
    public void detach() {
        synchronized (ATTACHMENTS) {
            final LoggerContext context = (LoggerContext) LogManager.getContext(false);
            final Configuration configuration = context.getConfiguration();
            final LoggerConfig dedicated = configuration.getLoggerConfig(loggerName);
            final Attachment attachment = ATTACHMENTS.get(loggerName);
            if (dedicated.getName().equals(loggerName)) {
                dedicated.removeAppender(getName());
                if (attachment != null && --attachment.count == 0) {
                    ATTACHMENTS.remove(loggerName);
                    if (attachment.ownsLoggerConfig) {
                        configuration.removeLogger(loggerName);
                    } else if (attachment.restoredLevel != null) {
                        dedicated.setLevel(attachment.restoredLevel);
                    }
                }
            }
            context.updateLoggers();
            stop();
        }
    }

    @Override
    public void append(final LogEvent event) {
        events.add(event.toImmutable());
    }

    /** Every captured event, in emission order. */
    public List<LogEvent> events() {
        return List.copyOf(events);
    }

    /** Captured events at exactly {@code level}. */
    public List<LogEvent> eventsAt(final Level level) {
        return events.stream().filter(e -> e.getLevel() == level).toList();
    }

    /** Formatted messages of the captured events at exactly {@code level}. */
    public List<String> messagesAt(final Level level) {
        return eventsAt(level).stream().map(e -> e.getMessage().getFormattedMessage()).toList();
    }

    /**
     * Captured events at exactly {@code level} that the calling thread logged. Another test class
     * that runs at the same time under {@code -Dparallel=classes} can log through the same logger
     * into the same capture, and its events are not the caller's.
     */
    public List<LogEvent> eventsOnThisThreadAt(final Level level) {
        final String threadName = Thread.currentThread().getName();
        return eventsAt(level).stream().filter(e -> threadName.equals(e.getThreadName())).toList();
    }

    /** Formatted messages of {@link #eventsOnThisThreadAt(Level)}. */
    public List<String> messagesOnThisThreadAt(final Level level) {
        return eventsOnThisThreadAt(level).stream().map(e -> e.getMessage().getFormattedMessage()).toList();
    }

    /** Formatted messages captured at WARN. */
    public List<String> warnings() {
        return messagesAt(Level.WARN);
    }

    /** Formatted messages captured at ERROR. */
    public List<String> errors() {
        return messagesAt(Level.ERROR);
    }

    /**
     * Renders every captured event the way an appender's layout would: the formatted message
     * <em>and</em> the attached throwable's stack trace, cause chain included.
     *
     * <p>Asserting only on {@link #messagesAt(Level)} is a trap for credential leaks: a leak
     * carried by {@code logger.warn(pattern, args, throwable)} lives entirely in
     * {@link LogEvent#getThrown()}, so a message-only assertion goes green while the rendered
     * log line still prints the offending value.</p>
     */
    public List<String> renderedEvents() {
        return events.stream().map(e -> {
            final Throwable thrown = e.getThrown();
            return e.getMessage().getFormattedMessage() + (thrown != null ? System.lineSeparator() + renderThrowable(thrown) : "");
        }).toList();
    }

    /** Renders a throwable and its whole cause chain the way a log layout would. */
    private static String renderThrowable(final Throwable throwable) {
        final StringWriter writer = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(writer)) {
            throwable.printStackTrace(printWriter);
        }
        return writer.toString();
    }
}
