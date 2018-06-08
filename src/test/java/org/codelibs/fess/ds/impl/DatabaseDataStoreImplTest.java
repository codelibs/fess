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
package org.codelibs.fess.ds.impl;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;

public class DatabaseDataStoreImplTest extends UnitFessTestCase {
    public DatabaseDataStoreImpl databaseDataStore;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        databaseDataStore = new DatabaseDataStoreImpl();
    }

    public void test_convertValue() {
        String value;
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("param1", "PARAM1");
        paramMap.put("param2", "PARAM2+");
        paramMap.put("param3", "PARAM3*");

        value = "\"abc\"";
        assertEquals("abc", databaseDataStore.convertValue(value, paramMap));

        value = "param1";
        assertEquals("PARAM1", databaseDataStore.convertValue(value, paramMap));

        value = "param2";
        assertEquals("PARAM2+", databaseDataStore.convertValue(value, paramMap));

        value = "\"123\"+param2+\",\"+param3+\"abc\"";
        assertEquals("123PARAM2+,PARAM3*abc", databaseDataStore.convertValue(value, paramMap));

        value = null;
        assertEquals("", databaseDataStore.convertValue(value, paramMap));

        value = "";
        assertEquals("", databaseDataStore.convertValue(value, paramMap));

        value = " ";
        assertNull(databaseDataStore.convertValue(value, paramMap));
    }
}
