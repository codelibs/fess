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
package org.codelibs.fess.ds;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.Constants;
import org.codelibs.fess.ds.callback.IndexUpdateCallback;
import org.codelibs.fess.entity.DataStoreParams;
import org.codelibs.fess.es.config.exentity.DataConfig;
import org.codelibs.fess.exception.JobProcessingException;
import org.codelibs.fess.script.AbstractScriptEngine;
import org.codelibs.fess.script.ScriptEngineFactory;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;

public class AbstractDataStoreTest extends UnitFessTestCase {
    public AbstractDataStore dataStore;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        dataStore = new AbstractDataStore() {
            @Override
            protected String getName() {
                return "Test";
            }

            @Override
            protected void storeData(DataConfig dataConfig, IndexUpdateCallback callback, DataStoreParams paramMap,
                    Map<String, String> scriptMap, Map<String, Object> defaultDataMap) {
                // TODO nothing
            }
        };

        ScriptEngineFactory scriptEngineFactory = new ScriptEngineFactory();
        ComponentUtil.register(scriptEngineFactory, "scriptEngineFactory");
        new AbstractScriptEngine() {

            @Override
            public Object evaluate(String template, Map<String, Object> paramMap) {
                final Map<String, Object> bindingMap = new HashMap<>(paramMap);
                bindingMap.put("container", SingletonLaContainerFactory.getContainer());
                final GroovyShell groovyShell = new GroovyShell(new Binding(bindingMap));
                try {
                    return groovyShell.evaluate(template);
                } catch (final JobProcessingException e) {
                    throw e;
                } catch (final Exception e) {
                    return null;
                } finally {
                    final GroovyClassLoader loader = groovyShell.getClassLoader();
                    loader.clearCache();
                }
            }

            @Override
            protected String getName() {
                return Constants.DEFAULT_SCRIPT;
            }
        }.register();
    }

    public void test_convertValue() {
        String value;
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("param1", "PARAM1");
        paramMap.put("param2", "PARAM2+");
        paramMap.put("param3", "PARAM3*");

        value = "\"abc\"";
        assertEquals("abc", dataStore.convertValue(Constants.DEFAULT_SCRIPT, value, paramMap));

        value = "param1";
        assertEquals("PARAM1", dataStore.convertValue(Constants.DEFAULT_SCRIPT, value, paramMap));

        value = "param2";
        assertEquals("PARAM2+", dataStore.convertValue(Constants.DEFAULT_SCRIPT, value, paramMap));

        value = "\"123\"+param2+\",\"+param3+\"abc\"";
        assertEquals("123PARAM2+,PARAM3*abc", dataStore.convertValue(Constants.DEFAULT_SCRIPT, value, paramMap));

        value = null;
        assertEquals("", dataStore.convertValue(Constants.DEFAULT_SCRIPT, value, paramMap));

        value = "";
        assertEquals("", dataStore.convertValue(Constants.DEFAULT_SCRIPT, value, paramMap));

        value = " ";
        assertNull(dataStore.convertValue(Constants.DEFAULT_SCRIPT, value, paramMap));
    }
}
