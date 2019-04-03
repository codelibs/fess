/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import java.util.HashMap;
import java.util.Map;

import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;

public final class GroovyUtil {
    private static final Logger logger = LoggerFactory.getLogger(GroovyUtil.class);

    private GroovyUtil() {
        // nothing
    }

    public static Object evaluate(final String template, final Map<String, Object> paramMap) {
        final Map<String, Object> bindingMap = new HashMap<>(paramMap);
        bindingMap.put("container", SingletonLaContainerFactory.getContainer());
        final GroovyShell groovyShell = new GroovyShell(new Binding(bindingMap));
        try {
            return groovyShell.evaluate(template);
        } catch (final Exception e) {
            logger.warn("Failed to evalue groovy script: " + template + " => " + paramMap, e);
            return null;
        } finally {
            final GroovyClassLoader loader = groovyShell.getClassLoader();
            //            StreamUtil.of(loader.getLoadedClasses()).forEach(c -> {
            //                try {
            //                    GroovySystem.getMetaClassRegistry().removeMetaClass(c);
            //                } catch (Throwable t) {
            //                    logger.warn("Failed to delete " + c, t);
            //                }
            //            });
            loader.clearCache();
        }
    }
}
