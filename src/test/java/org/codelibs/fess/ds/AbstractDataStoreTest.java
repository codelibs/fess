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
package org.codelibs.fess.ds;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.ds.callback.IndexUpdateCallback;
import org.codelibs.fess.es.config.exentity.DataConfig;
import org.codelibs.fess.unit.UnitFessTestCase;

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
            protected void storeData(DataConfig dataConfig, IndexUpdateCallback callback, Map<String, String> paramMap,
                    Map<String, String> scriptMap, Map<String, Object> defaultDataMap) {
                // TODO nothing
            }
        };
    }

    public void test_convertValue() {
        String value;
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("param1", "PARAM1");
        paramMap.put("param2", "PARAM2+");
        paramMap.put("param3", "PARAM3*");

        value = "\"abc\"";
        assertEquals("abc", dataStore.convertValue(value, paramMap));

        value = "param1";
        assertEquals("PARAM1", dataStore.convertValue(value, paramMap));

        value = "param2";
        assertEquals("PARAM2+", dataStore.convertValue(value, paramMap));

        value = "\"123\"+param2+\",\"+param3+\"abc\"";
        assertEquals("123PARAM2+,PARAM3*abc", dataStore.convertValue(value, paramMap));

        value = null;
        assertEquals("", dataStore.convertValue(value, paramMap));

        value = "";
        assertEquals("", dataStore.convertValue(value, paramMap));

        value = " ";
        assertNull(dataStore.convertValue(value, paramMap));
    }
}
