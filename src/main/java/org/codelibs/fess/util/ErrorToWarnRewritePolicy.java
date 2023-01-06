/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import java.util.Arrays;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;

@Plugin(name = "ErrorToWarnRewritePolicy", category = Core.CATEGORY_NAME, elementType = "rewritePolicy", printObject = true)
public class ErrorToWarnRewritePolicy implements RewritePolicy {

    private final String[] loggerNames;

    public ErrorToWarnRewritePolicy(final String[] loggerNames) {
        this.loggerNames = loggerNames;
    }

    @Override
    public LogEvent rewrite(final LogEvent event) {
        final String loggerName = event.getLoggerName();
        if (loggerName == null) {
            return event;
        }
        for (final String name : loggerNames) {
            if (loggerName.startsWith(name)) {
                final Level sourceLevel = event.getLevel();
                if (sourceLevel != Level.ERROR) {
                    return event;
                }
                return new Log4jLogEvent.Builder(event).setLevel(Level.WARN).build();
            }
        }
        return event;
    }

    @PluginFactory
    public static ErrorToWarnRewritePolicy createPolicy(@PluginAttribute("loggers") final String loggerNamePrefix) {
        final String[] loggerNames = loggerNamePrefix != null
                ? Arrays.stream(loggerNamePrefix.split(",")).map(String::trim).filter(s -> s.length() > 0).toArray(n -> new String[n])
                : new String[0];
        return new ErrorToWarnRewritePolicy(loggerNames);
    }

}
