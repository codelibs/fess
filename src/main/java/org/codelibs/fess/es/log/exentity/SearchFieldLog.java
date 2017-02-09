/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.es.log.exentity;

import java.util.Map;

import org.codelibs.fess.es.log.bsentity.BsSearchFieldLog;

/**
 * @author FreeGen
 */
public class SearchFieldLog extends BsSearchFieldLog {

    private static final long serialVersionUID = 1L;

    private Map<String, Object> fields;

    public String getId() {
        return asDocMeta().id();
    }

    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    public void addField(final String key, final Object value) {
        fields.put(key, value);
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = super.toSource();
        if (fields != null) {
            sourceMap.putAll(fields);
        }
        return sourceMap;
    }

    @Override
    public String toString() {
        return "SearchFieldLog [name=" + name + ", searchLogId=" + searchLogId + ", value=" + value + ", docMeta=" + docMeta + "]";
    }
}
