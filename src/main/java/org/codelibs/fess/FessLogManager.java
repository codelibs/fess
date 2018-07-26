/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
package org.codelibs.fess;

import java.util.logging.LogManager;
import java.util.logging.Logger;

public class FessLogManager extends LogManager {

    private static final String ORG_APACHE_LOGGING_LOG4J_JUL_LOG_MANAGER = "org.apache.logging.log4j.jul.LogManager";

    @Override
    public Logger getLogger(final String name) {
        final LogManager logManager = getLogManager(ORG_APACHE_LOGGING_LOG4J_JUL_LOG_MANAGER);
        if (logManager != null) {
            return logManager.getLogger(name);
        }
        return LogManager.getLogManager().getLogger(name);
    }

    private LogManager getLogManager(final String cname) {
        try {
            try {
                final Class<?> clz = ClassLoader.getSystemClassLoader().loadClass(cname);
                return (LogManager) clz.newInstance();
            } catch (final ClassNotFoundException ex) {
                final Class<?> clz = Thread.currentThread().getContextClassLoader().loadClass(cname);
                return (LogManager) clz.newInstance();
            }
        } catch (final Exception ex) {
            return null;
        }
    }
}
