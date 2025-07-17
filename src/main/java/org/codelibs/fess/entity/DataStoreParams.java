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
package org.codelibs.fess.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Parameter container class for data store configurations and runtime parameters.
 * This class provides a convenient wrapper around a Map to store and retrieve
 * data store specific parameters with type-safe access methods.
 *
 * <p>The class uses a ParamMap internally which provides case format conversion
 * between camelCase and snake_case parameter names for flexible parameter access.
 * Parameters can be stored as any Object type and retrieved with type conversion
 * support for common types like String.</p>
 *
 * <p>This class is commonly used to pass configuration parameters to data store
 * implementations during crawling operations, allowing for flexible parameter
 * handling without tight coupling to specific parameter schemas.</p>
 */
public class DataStoreParams {

    /**
     * Internal map storing parameter key-value pairs.
     * Uses ParamMap for automatic case format conversion between camelCase and snake_case.
     */
    protected final Map<String, Object> params;

    /**
     * Creates a new empty DataStoreParams instance.
     * Initializes the internal parameter map with a ParamMap wrapper.
     */
    public DataStoreParams() {
        params = new ParamMap<>(new HashMap<>());
    }

    /**
     * Creates a new DataStoreParams instance with a copy of the provided parameters.
     * This protected constructor is used for creating new instances from existing parameter maps.
     *
     * @param params the parameter map to copy, must not be null
     */
    protected DataStoreParams(final Map<String, Object> params) {
        this.params = new ParamMap<>(new HashMap<>(getDataMap(params)));
    }

    /**
     * Stores a parameter value with the specified key.
     *
     * @param key the parameter key, must not be null
     * @param value the parameter value, may be null
     */
    public void put(final String key, final Object value) {
        params.put(key, value);
    }

    /**
     * Retrieves a parameter value by key.
     *
     * @param key the parameter key to look up
     * @return the parameter value if found, null otherwise
     */
    public Object get(final String key) {
        return params.get(key);
    }

    /**
     * Retrieves a parameter value as a String.
     * If the stored value is already a String, it is returned directly.
     * Otherwise, the toString() method is called on the value.
     *
     * @param key the parameter key to look up
     * @return the parameter value as a String, null if not found or value is null
     */
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

    /**
     * Retrieves a parameter value as a String with a default value fallback.
     *
     * @param key the parameter key to look up
     * @param defaultValue the default value to return if key is not found or value is null
     * @return the parameter value as a String, or defaultValue if not found
     */
    public String getAsString(final String key, final String defaultValue) {
        final String value = getAsString(key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    /**
     * Creates a new DataStoreParams instance with a copy of the current parameters.
     * This provides an independent copy that can be modified without affecting the original.
     *
     * @return a new DataStoreParams instance containing a copy of the current parameters
     */
    public DataStoreParams newInstance() {
        return new DataStoreParams(params);
    }

    /**
     * Adds all key-value pairs from the specified map to this parameter container.
     *
     * @param map the map containing parameters to add, must not be null
     */
    public void putAll(final Map<String, String> map) {
        params.putAll(map);
    }

    /**
     * Checks if the specified key exists in the parameter map.
     *
     * @param key the key to check for existence
     * @return true if the key exists, false otherwise
     */
    public boolean containsKey(final String key) {
        return params.containsKey(key);
    }

    /**
     * Returns a copy of the internal parameter map as a standard Map.
     * The returned map is a copy and modifications will not affect this instance.
     *
     * @return a new Map containing all current parameters
     */
    public Map<String, Object> asMap() {
        return new ParamMap<>(new HashMap<>(getDataMap(params)));
    }

    /**
     * Extracts the underlying data map from a parameter map.
     * If the provided map is a ParamMap instance, returns its parent map.
     * Otherwise, returns the map as-is.
     *
     * @param params the parameter map to extract data from
     * @return the underlying data map
     */
    protected static Map<String, Object> getDataMap(final Map<String, Object> params) {
        if (params instanceof final ParamMap<String, Object> paramMap) {
            return paramMap.getParent();
        }
        return params;
    }
}
