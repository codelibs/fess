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
package org.codelibs.fess.entity;

import java.util.HashMap;
import java.util.Map;

public class DataStoreParams {

    protected final Map<String, Object> params;

    public DataStoreParams() {
        params = new ParamMap<>(new HashMap<>());
    }

    protected DataStoreParams(final Map<String, Object> params) {
        this.params = new ParamMap<>(new HashMap<>(getDataMap(params)));
    }

    public void put(final String key, final Object value) {
        params.put(key, value);
    }

    public Object get(final String key) {
        return params.get(key);
    }

    public String getAsString(final String key) {
        if (params.get(key) instanceof final String strValue) {
            return strValue;
        }
        final Object value = params.get(key);
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    public String getAsString(final String key, final String defaultValue) {
        final String value = getAsString(key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public DataStoreParams newInstance() {
        return new DataStoreParams(params);
    }

    public void putAll(final Map<String, String> map) {
        params.putAll(map);
    }

    public boolean containsKey(final String key) {
        return params.containsKey(key);
    }

    public Map<String, Object> asMap() {
        return new ParamMap<>(new HashMap<>(getDataMap(params)));
    }

    protected static Map<String, Object> getDataMap(final Map<String, Object> params) {
        if (params instanceof final ParamMap<String, Object> paramMap) {
            return paramMap.getParent();
        }
        return params;
    }
}
