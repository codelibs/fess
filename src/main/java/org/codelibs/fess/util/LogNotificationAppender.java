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
package org.codelibs.fess.util;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.codelibs.fess.helper.LogNotificationHelper;
import org.codelibs.fess.helper.LogNotificationHelper.LogNotificationEvent;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Custom Log4j2 Appender that captures log events into a buffer for notification purposes.
 */
@Plugin(name = "LogNotificationAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class LogNotificationAppender extends AbstractAppender {

    private static final int MAX_THROWABLE_LENGTH = 500;

    private static final Set<String> EXCLUDED_LOGGERS = Set.of( //
            "org.codelibs.fess.helper.NotificationHelper", //
            "org.codelibs.fess.util.LogNotificationAppender", //
            "org.codelibs.fess.util.LogNotificationBuffer", //
            "org.codelibs.fess.job.LogNotificationJob", //
            "org.codelibs.fess.timer.LogNotificationTarget", //
            "org.codelibs.fess.helper.LogNotificationHelper", //
            "org.codelibs.curl" //
    );

    private final Level minLevel;

    /**
     * Constructs a new LogNotificationAppender.
     *
     * @param name the appender name
     * @param filter the filter to apply
     * @param layout the layout to use
     * @param ignoreExceptions whether to ignore exceptions
     * @param properties the appender properties
     * @param minLevel the minimum log level to capture
     */
    protected LogNotificationAppender(final String name, final Filter filter, final Layout<? extends Serializable> layout,
            final boolean ignoreExceptions, final Property[] properties, final Level minLevel) {
        super(name, filter, layout, ignoreExceptions, properties);
        this.minLevel = minLevel;
    }

    @Override
    public void append(final LogEvent event) {
        final String loggerName = event.getLoggerName();
        if (loggerName != null) {
            for (final String excluded : EXCLUDED_LOGGERS) {
                if (loggerName.startsWith(excluded)) {
                    return;
                }
            }
        }

        if (!event.getLevel().isMoreSpecificThan(getEffectiveMinLevel())) {
            return;
        }

        final LogNotificationHelper helper;
        try {
            helper = ComponentUtil.getLogNotificationHelper();
        } catch (final Exception e) {
            return;
        }

        String throwableStr = null;
        final Throwable thrown = event.getThrown();
        if (thrown != null) {
            final StringWriter sw = new StringWriter();
            thrown.printStackTrace(new PrintWriter(sw));
            throwableStr = sw.toString();
            if (throwableStr.length() > MAX_THROWABLE_LENGTH) {
                throwableStr = throwableStr.substring(0, MAX_THROWABLE_LENGTH);
            }
        }

        final LogNotificationEvent notificationEvent = new LogNotificationEvent( //
                event.getTimeMillis(), //
                event.getLevel().name(), //
                loggerName, //
                event.getMessage().getFormattedMessage(), //
                throwableStr //
        );
        helper.offer(notificationEvent);
    }

    private Level getEffectiveMinLevel() {
        try {
            final String levelStr = ComponentUtil.getSystemProperties().getProperty("fess.log.notification.level");
            if (levelStr != null) {
                return Level.toLevel(levelStr, minLevel);
            }
        } catch (final Exception e) {
            // ComponentUtil not initialized yet
        }
        return minLevel;
    }

    /**
     * Factory method to create a LogNotificationAppender instance.
     *
     * @param name the appender name
     * @param minLevel the minimum log level string (defaults to ERROR)
     * @param filter the filter to apply
     * @param layout the layout to use
     * @return a new LogNotificationAppender instance, or null if name is null
     */
    @PluginFactory
    public static LogNotificationAppender createAppender( //
            @PluginAttribute("name") final String name, //
            @PluginAttribute(value = "minLevel", defaultString = "ERROR") final String minLevel, //
            @PluginElement("Filter") final Filter filter, //
            @PluginElement("Layout") final Layout<? extends Serializable> layout) {
        if (name == null) {
            LOGGER.error("No name provided for LogNotificationAppender");
            return null;
        }
        final Level level = Level.toLevel(minLevel, Level.ERROR);
        return new LogNotificationAppender(name, filter, layout, true, Property.EMPTY_ARRAY, level);
    }
}
