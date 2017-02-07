/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.log.bsentity.BsClickLog;

/**
 * @author FreeGen
 */
public class ClickLog extends BsClickLog {

    private static final long serialVersionUID = 1L;

    private Map<String, String> attributes;

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

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (queryRequestedAt != null) {
            sourceMap.put("queryRequestedAt", queryRequestedAt);
        }
        if (requestedAt != null) {
            sourceMap.put("requestedAt", requestedAt);
        }
        if (queryId != null) {
            sourceMap.put("queryId", queryId);
        }
        if (docId != null) {
            sourceMap.put("docId", docId);
        }
        if (userSessionId != null) {
            sourceMap.put("userSessionId", userSessionId);
        }
        if (url != null) {
            sourceMap.put("url", url);
        }
        if (order != null) {
            sourceMap.put("order", order);
        }
        if (attributes != null) {
            sourceMap.putAll(attributes);
        }
        return sourceMap;
    }

    @Override
    public String toString() {
        return "ClickLog [queryRequestedAt=" + queryRequestedAt + ", requestedAt=" + requestedAt + ", queryId=" + queryId + ", docId="
                + docId + ", userSessionId=" + userSessionId + ", url=" + url + ", order=" + order + ", docMeta=" + docMeta + "]";
    }
}
