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
package org.codelibs.fess.es.config.bsentity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.config.allcommon.EsAbstractEntity;
import org.codelibs.fess.es.config.bsentity.dbmeta.ThumbnailQueueDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsThumbnailQueue extends EsAbstractEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** createdBy */
    protected String createdBy;

    /** createdTime */
    protected Long createdTime;

    /** generator */
    protected String generator;

    /** path */
    protected String path;

    /** target */
    protected String target;

    /** thumbnail_id */
    protected String thumbnailId;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public ThumbnailQueueDbm asDBMeta() {
        return ThumbnailQueueDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "thumbnail_queue";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (createdBy != null) {
            addFieldToSource(sourceMap, "createdBy", createdBy);
        }
        if (createdTime != null) {
            addFieldToSource(sourceMap, "createdTime", createdTime);
        }
        if (generator != null) {
            addFieldToSource(sourceMap, "generator", generator);
        }
        if (path != null) {
            addFieldToSource(sourceMap, "path", path);
        }
        if (target != null) {
            addFieldToSource(sourceMap, "target", target);
        }
        if (thumbnailId != null) {
            addFieldToSource(sourceMap, "thumbnail_id", thumbnailId);
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
        sb.append(dm).append(createdBy);
        sb.append(dm).append(createdTime);
        sb.append(dm).append(generator);
        sb.append(dm).append(path);
        sb.append(dm).append(target);
        sb.append(dm).append(thumbnailId);
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getCreatedBy() {
        checkSpecifiedProperty("createdBy");
        return convertEmptyToNull(createdBy);
    }

    public void setCreatedBy(String value) {
        registerModifiedProperty("createdBy");
        this.createdBy = value;
    }

    public Long getCreatedTime() {
        checkSpecifiedProperty("createdTime");
        return createdTime;
    }

    public void setCreatedTime(Long value) {
        registerModifiedProperty("createdTime");
        this.createdTime = value;
    }

    public String getGenerator() {
        checkSpecifiedProperty("generator");
        return convertEmptyToNull(generator);
    }

    public void setGenerator(String value) {
        registerModifiedProperty("generator");
        this.generator = value;
    }

    public String getPath() {
        checkSpecifiedProperty("path");
        return convertEmptyToNull(path);
    }

    public void setPath(String value) {
        registerModifiedProperty("path");
        this.path = value;
    }

    public String getTarget() {
        checkSpecifiedProperty("target");
        return convertEmptyToNull(target);
    }

    public void setTarget(String value) {
        registerModifiedProperty("target");
        this.target = value;
    }

    public String getThumbnailId() {
        checkSpecifiedProperty("thumbnailId");
        return convertEmptyToNull(thumbnailId);
    }

    public void setThumbnailId(String value) {
        registerModifiedProperty("thumbnailId");
        this.thumbnailId = value;
    }
}
