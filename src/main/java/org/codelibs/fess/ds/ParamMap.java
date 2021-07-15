/*
 * Copyright 2012-2021 CodeLibs Project and the Others.
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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CaseFormat;

public class ParamMap<K, V> implements Map<K, V> {

    private final Map<K, V> parent;

    public ParamMap(final Map<K, V> parent) {
        this.parent = parent;
    }

    protected Object toCamelCase(final Object key) {
        if (key == null) {
            return key;
        }
        String keyStr = key.toString();
        if (keyStr.indexOf('_') < 0) {
            keyStr = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, StringUtils.uncapitalize(keyStr));
        } else {
            keyStr = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, keyStr);
        }
        return keyStr;
    }

    public int size() {
        return parent.size();
    }

    public boolean isEmpty() {
        return parent.isEmpty();
    }

    public boolean containsKey(Object key) {
        if (parent.containsKey(key)) {
            return true;
        }
        return parent.containsKey(toCamelCase(key));
    }

    public boolean containsValue(Object value) {
        return parent.containsValue(value);
    }

    public V get(Object key) {
        final V value = parent.get(key);
        if (value != null) {
            return value;
        }
        return parent.get(toCamelCase(key));
    }

    public V put(K key, V value) {
        return parent.put(key, value);
    }

    public V remove(Object key) {
        final V value = parent.remove(key);
        if (value != null) {
            return value;
        }
        return parent.remove(key);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        parent.putAll(m);
    }

    public void clear() {
        parent.clear();
    }

    public Set<K> keySet() {
        // return original keys
        return parent.keySet();
    }

    public Collection<V> values() {
        return parent.values();
    }

    public Set<Entry<K, V>> entrySet() {
        // return original keys
        return parent.entrySet();
    }

    public boolean equals(Object o) {
        return parent.equals(o);
    }

    public int hashCode() {
        return parent.hashCode();
    }

    public V getOrDefault(Object key, V defaultValue) {
        final V value = parent.get(key);
        if (value != null) {
            return value;
        }
        return parent.getOrDefault(toCamelCase(key), defaultValue);
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        parent.forEach(action);
    }

    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        parent.replaceAll(function);
    }

    public V putIfAbsent(K key, V value) {
        return parent.putIfAbsent(key, value);
    }

    public boolean remove(Object key, Object value) {
        if (parent.remove(key, value)) {
            return true;
        }
        return parent.remove(toCamelCase(key), value);
    }

    public boolean replace(K key, V oldValue, V newValue) {
        if (parent.replace(key, oldValue, newValue)) {
            return true;
        }
        return parent.replace(key, oldValue, newValue);
    }

    public V replace(K key, V value) {
        // use original key
        return parent.replace(key, value);
    }

    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        // use original key
        return parent.computeIfAbsent(key, mappingFunction);
    }

    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        // use original key
        return parent.computeIfPresent(key, remappingFunction);
    }

    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        // use original key
        return parent.compute(key, remappingFunction);
    }

    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        // use original key
        return parent.merge(key, value, remappingFunction);
    }
}
