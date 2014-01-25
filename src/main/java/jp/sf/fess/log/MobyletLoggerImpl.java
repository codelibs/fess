/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.log;

import org.mobylet.core.log.MobyletLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobyletLoggerImpl implements MobyletLogger {
    private static final Logger logger = LoggerFactory.getLogger("org.mobylet");

    private final boolean loggable;

    public MobyletLoggerImpl() {
        final String value = System.getProperty("mobylet.log");
        loggable = "true".equalsIgnoreCase(value);
    }

    @Override
    public boolean isLoggable() {
        return loggable;
    }

    @Override
    public void log(final String msg) {
        if (loggable) {
            logger.info(msg);
        }
    }

}
