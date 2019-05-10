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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataStoreFactory {
    private static final Logger logger = LoggerFactory.getLogger(DataStoreFactory.class);

    protected Map<String, DataStore> dataStoreMap = new LinkedHashMap<>();

    public void add(final String name, final DataStore dataStore) {
        if (name == null || dataStore == null) {
            throw new IllegalArgumentException("name or dataStore is null.");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Loaded " + name);
        }
        dataStoreMap.put(name, dataStore);
    }

    public DataStore getDataStore(final String name) {
        return dataStoreMap.get(name);
    }

    public List<String> getDataStoreNameList() {
        final Set<String> nameSet = dataStoreMap.keySet();
        final List<String> nameList = new ArrayList<>();
        nameList.addAll(nameSet);
        Collections.sort(nameList);
        return nameList;
    }

}
