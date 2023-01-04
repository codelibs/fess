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

    public Map<K, V> getParent() {
        return parent;
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

    @Override
    public int size() {
        return parent.size();
    }

    @Override
    public boolean isEmpty() {
        return parent.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        if (parent.containsKey(key)) {
            return true;
        }
        return parent.containsKey(toCamelCase(key));
    }

    @Override
    public boolean containsValue(final Object value) {
        return parent.containsValue(value);
    }

    @Override
    public V get(final Object key) {
        final V value = parent.get(key);
        if (value != null) {
            return value;
        }
        return parent.get(toCamelCase(key));
    }

    @Override
    public V put(final K key, final V value) {
        return parent.put(key, value);
    }

    @Override
    public V remove(final Object key) {
        final V value = parent.remove(key);
        if (value != null) {
            return value;
        }
        return parent.remove(key);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        parent.putAll(m);
    }

    @Override
    public void clear() {
        parent.clear();
    }

    @Override
    public Set<K> keySet() {
        // return original keys
        return parent.keySet();
    }

    @Override
    public Collection<V> values() {
        return parent.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        // return original keys
        return parent.entrySet();
    }

    @Override
    public boolean equals(final Object o) {
        return parent.equals(o);
    }

    @Override
    public int hashCode() {
        return parent.hashCode();
    }

    @Override
    public V getOrDefault(final Object key, final V defaultValue) {
        final V value = parent.get(key);
        if (value != null) {
            return value;
        }
        return parent.getOrDefault(toCamelCase(key), defaultValue);
    }

    @Override
    public void forEach(final BiConsumer<? super K, ? super V> action) {
        parent.forEach(action);
    }

    @Override
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        parent.replaceAll(function);
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        return parent.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        if (parent.remove(key, value)) {
            return true;
        }
        return parent.remove(toCamelCase(key), value);
    }

    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        if (parent.replace(key, oldValue, newValue)) {
            return true;
        }
        return parent.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(final K key, final V value) {
        // use original key
        return parent.replace(key, value);
    }

    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        // use original key
        return parent.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        // use original key
        return parent.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        // use original key
        return parent.compute(key, remappingFunction);
    }

    @Override
    public V merge(final K key, final V value, final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        // use original key
        return parent.merge(key, value, remappingFunction);
    }
}
