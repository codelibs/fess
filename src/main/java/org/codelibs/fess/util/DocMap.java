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
package org.codelibs.fess.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A wrapper implementation of Map&lt;String, Object&gt; that provides special handling
 * for document data. This class wraps an existing map and provides custom behavior
 * for the entrySet method to ensure language fields appear first in iteration order.
 * Used throughout the Fess search system for document data manipulation.
 *
 */
public class DocMap implements Map<String, Object> {

    /** The key used for language field identification */
    private static final String LANG_KEY = "lang";

    /** The underlying map that this DocMap wraps */
    private final Map<String, Object> parent;

    /**
     * Constructor that creates a DocMap wrapping the given parent map.
     *
     * @param parent the map to wrap and delegate operations to
     */
    public DocMap(final Map<String, Object> parent) {
        this.parent = parent;
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    @Override
    public int size() {
        return parent.size();
    }

    /**
     * Returns true if this map contains no key-value mappings.
     *
     * @return true if this map contains no key-value mappings
     */
    @Override
    public boolean isEmpty() {
        return parent.isEmpty();
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     *
     * @param key the key whose presence in this map is to be tested
     * @return true if this map contains a mapping for the specified key
     */
    @Override
    public boolean containsKey(final Object key) {
        return parent.containsKey(key);
    }

    /**
     * Returns true if this map maps one or more keys to the specified value.
     *
     * @param value the value whose presence in this map is to be tested
     * @return true if this map maps one or more keys to the specified value
     */
    @Override
    public boolean containsValue(final Object value) {
        return parent.containsValue(value);
    }

    /**
     * Returns the value to which the specified key is mapped.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if no mapping exists
     */
    @Override
    public Object get(final Object key) {
        return parent.get(key);
    }

    /**
     * Associates the specified value with the specified key in this map.
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with key, or null if no mapping existed
     */
    @Override
    public Object put(final String key, final Object value) {
        return parent.put(key, value);
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     *
     * @param key the key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if no mapping existed
     */
    @Override
    public Object remove(final Object key) {
        return parent.remove(key);
    }

    /**
     * Copies all of the mappings from the specified map to this map.
     *
     * @param m the mappings to be stored in this map
     */
    @Override
    public void putAll(final Map<? extends String, ? extends Object> m) {
        parent.putAll(m);
    }

    /**
     * Removes all of the mappings from this map.
     */
    @Override
    public void clear() {
        parent.clear();
    }

    /**
     * Returns a Set view of the keys contained in this map.
     *
     * @return a set view of the keys contained in this map
     */
    @Override
    public Set<String> keySet() {
        return parent.keySet();
    }

    /**
     * Returns a Collection view of the values contained in this map.
     *
     * @return a collection view of the values contained in this map
     */
    @Override
    public Collection<Object> values() {
        return parent.values();
    }

    /**
     * Returns a Set view of the mappings contained in this map.
     * If the map contains a language key, it will be sorted to appear first.
     *
     * @return a set view of the mappings contained in this map with language key prioritized
     */
    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        if (parent.containsKey(LANG_KEY)) {
            final List<java.util.Map.Entry<String, Object>> list = new ArrayList<>(parent.entrySet());
            Collections.sort(list, (o1, o2) -> {
                final String k1 = o1.getKey();
                if (LANG_KEY.equals(k1)) {
                    return -1;
                }
                final String k2 = o2.getKey();
                if (LANG_KEY.equals(k2)) {
                    return -1;
                }
                return k1.compareTo(k2);
            });
            return new LinkedHashSet<>(list);
        }
        return parent.entrySet();
    }

}
