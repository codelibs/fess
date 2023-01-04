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
package org.codelibs.fess.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DocMap implements Map<String, Object> {

    private static final String LANG_KEY = "lang";
    private final Map<String, Object> parent;

    public DocMap(final Map<String, Object> parent) {
        this.parent = parent;
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
        return parent.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return parent.containsValue(value);
    }

    @Override
    public Object get(final Object key) {
        return parent.get(key);
    }

    @Override
    public Object put(final String key, final Object value) {
        return parent.put(key, value);
    }

    @Override
    public Object remove(final Object key) {
        return parent.remove(key);
    }

    @Override
    public void putAll(final Map<? extends String, ? extends Object> m) {
        parent.putAll(m);
    }

    @Override
    public void clear() {
        parent.clear();
    }

    @Override
    public Set<String> keySet() {
        return parent.keySet();
    }

    @Override
    public Collection<Object> values() {
        return parent.values();
    }

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
