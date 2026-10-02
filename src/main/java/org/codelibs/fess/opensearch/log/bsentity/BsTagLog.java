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
package org.codelibs.fess.opensearch.log.bsentity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.opensearch.log.allcommon.EsAbstractEntity;
import org.codelibs.fess.opensearch.log.bsentity.dbmeta.TagLogDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsTagLog extends EsAbstractEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** createdAt */
    protected LocalDateTime createdAt;

    /** docId */
    protected String docId;

    /** tag */
    protected String tag;

    /** url */
    protected String url;

    /** user */
    protected String user;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public TagLogDbm asDBMeta() {
        return TagLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "tag_log";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (createdAt != null) {
            addFieldToSource(sourceMap, "createdAt", createdAt);
        }
        if (docId != null) {
            addFieldToSource(sourceMap, "docId", docId);
        }
        if (tag != null) {
            addFieldToSource(sourceMap, "tag", tag);
        }
        if (url != null) {
            addFieldToSource(sourceMap, "url", url);
        }
        if (user != null) {
            addFieldToSource(sourceMap, "user", user);
        }
        return sourceMap;
    }

    protected void addFieldToSource(Map<String, Object> sourceMap, String field, Object value) {
        sourceMap.put(field, value);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    protected String doBuildColumnString(String dm) {
        StringBuilder sb = new StringBuilder();
        sb.append(dm).append(createdAt);
        sb.append(dm).append(docId);
        sb.append(dm).append(tag);
        sb.append(dm).append(url);
        sb.append(dm).append(user);
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public LocalDateTime getCreatedAt() {
        checkSpecifiedProperty("createdAt");
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime value) {
        registerModifiedProperty("createdAt");
        this.createdAt = value;
    }

    public String getDocId() {
        checkSpecifiedProperty("docId");
        return convertEmptyToNull(docId);
    }

    public void setDocId(String value) {
        registerModifiedProperty("docId");
        this.docId = value;
    }

    public String getTag() {
        checkSpecifiedProperty("tag");
        return convertEmptyToNull(tag);
    }

    public void setTag(String value) {
        registerModifiedProperty("tag");
        this.tag = value;
    }

    public String getUrl() {
        checkSpecifiedProperty("url");
        return convertEmptyToNull(url);
    }

    public void setUrl(String value) {
        registerModifiedProperty("url");
        this.url = value;
    }

    public String getUser() {
        checkSpecifiedProperty("user");
        return convertEmptyToNull(user);
    }

    public void setUser(String value) {
        registerModifiedProperty("user");
        this.user = value;
    }
}
