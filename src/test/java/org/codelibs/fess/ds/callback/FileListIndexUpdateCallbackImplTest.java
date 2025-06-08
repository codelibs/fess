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
package org.codelibs.fess.ds.callback;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;

public class FileListIndexUpdateCallbackImplTest extends UnitFessTestCase {
    public FileListIndexUpdateCallbackImpl indexUpdateCallback;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        indexUpdateCallback = new FileListIndexUpdateCallbackImpl(null, null, 1);
    }

    /** Case 1: Normal merge (no duplicates) */
    public void test_mergeResponseData_noOverwrite() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("a", "A0");
        Map<String, Object> responseDataMap = new HashMap<>();
        responseDataMap.put("b", "B1");

        indexUpdateCallback.mergeResponseData(dataMap, responseDataMap);

        assertEquals(2, dataMap.size());
        assertEquals("A0", dataMap.get("a"));
        assertEquals("B1", dataMap.get("b"));
    }

    /** Case 2: Key conflict (without .overwrite) → Overwrite with value from responseDataMap */
    public void test_mergeResponseData_keyConflict() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("x", "X0");
        Map<String, Object> responseDataMap = new HashMap<>();
        responseDataMap.put("x", "X1");

        indexUpdateCallback.mergeResponseData(dataMap, responseDataMap);

        assertEquals(1, dataMap.size());
        assertEquals("X1", dataMap.get("x"));
    }

    /** Case 3: Only overwrite key (baseKey not set) → generate baseKey, remove overwrite key */
    public void test_mergeResponseData_overwriteOnly() {
        Map<String, Object> dataMap = new HashMap<>();
        Map<String, Object> responseDataMap = new HashMap<>();
        responseDataMap.put("y.overwrite", "Y1");

        indexUpdateCallback.mergeResponseData(dataMap, responseDataMap);

        assertFalse(dataMap.containsKey("y.overwrite"));
        assertEquals(1, dataMap.size());
        assertEquals("Y1", dataMap.get("y"));
    }

    /** Case 4: Both baseKey and baseKey.overwrite exist → Overwrite with the value of .overwrite */
    public void test_mergeResponseData_baseAndOverwrite() {
        Map<String, Object> dataMap = new HashMap<>();
        Map<String, Object> responseDataMap = new HashMap<>();
        responseDataMap.put("z", "Z0");
        responseDataMap.put("z.overwrite", "Z1");

        indexUpdateCallback.mergeResponseData(dataMap, responseDataMap);

        assertFalse(dataMap.containsKey("z.overwrite"));
        assertEquals(1, dataMap.size());
        assertEquals("Z1", dataMap.get("z"));
    }

    /** Case 5: Overwrite processing for multiple fields, existing overwrite keys are also properly removed */
    public void test_mergeResponseData_multipleOverwrite() {
        Map<String, Object> dataMap = new HashMap<>();
        // Case where the initial dataMap also contains overwrite keys
        dataMap.put("m", "M0");
        dataMap.put("n.overwrite", "N0_old");

        Map<String, Object> responseDataMap = new HashMap<>();
        responseDataMap.put("m.overwrite", "M1");
        responseDataMap.put("n.overwrite", "N1");
        responseDataMap.put("p", "P1");

        indexUpdateCallback.mergeResponseData(dataMap, responseDataMap);

        // All overwrite keys should be removed
        assertFalse(dataMap.containsKey("m.overwrite"));
        assertFalse(dataMap.containsKey("n.overwrite"));

        // m and n are updated with overwrite values, p is merged normally
        assertEquals("M1", dataMap.get("m"));
        assertEquals("N1", dataMap.get("n"));
        assertEquals("P1", dataMap.get("p"));
        assertEquals(3, dataMap.size());
    }
}
