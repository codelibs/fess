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
package org.codelibs.fess.es.user.exentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.user.bsentity.BsRole;

/**
 * @author FreeGen
 */
public class Role extends BsRole {

    private static final long serialVersionUID = 1L;

    private Map<String, String> attributes;

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    public String getId() {
        return asDocMeta().id();
    }

    public void setId(final String id) {
        asDocMeta().id(id);
    }

    @Override
    public String toString() {
        return "Role [name=" + name + "]";
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(final Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> toSource() {
        final Map<String, Object> sourceMap = new HashMap<>();
        if (name != null) {
            sourceMap.put("name", name);
        }
        if (attributes != null) {
            sourceMap.putAll(attributes);
        }
        return sourceMap;
    }
}
