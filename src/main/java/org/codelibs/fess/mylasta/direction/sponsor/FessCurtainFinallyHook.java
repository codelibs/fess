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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lastaflute.core.direction.CurtainFinallyHook;
import org.lastaflute.core.direction.FwAssistantDirector;

/**
 * @author jflute
 */
public class FessCurtainFinallyHook implements CurtainFinallyHook {

    private static final Logger logger = LogManager.getLogger(FessCurtainFinallyHook.class);

    @Override
    public void hook(final FwAssistantDirector assistantDirector) {
        shutdownCommonsHttpClient();
    }

    private void shutdownCommonsHttpClient() { // from Tomcat7ConfigServlet (old class)
        try {
            final Class<?> clazz = Class.forName("org.apache.commons.httpclient.MultiThreadedHttpConnectionManager");
            final Method method = clazz.getMethod("shutdownAll", (Class<?>[]) null);
            method.invoke(null, (Object[]) null);
        } catch (final ClassNotFoundException e) {
            // ignore
        } catch (final Exception e) {
            logger.warn("Could not shutdown Commons HttpClient.", e);
        }
    }
}
