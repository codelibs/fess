/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.es.log.bsentity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.log.allcommon.EsAbstractEntity;
import org.codelibs.fess.es.log.bsentity.dbmeta.EventLogDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsEventLog extends EsAbstractEntity {

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

    /** createdBy */
    protected String createdBy;

    /** eventType */
    protected String eventType;

    /** message */
    protected String message;

    /** path */
    protected String path;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public EventLogDbm asDBMeta() {
        return EventLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "event_log";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (createdAt != null) {
            sourceMap.put("createdAt", createdAt);
        }
        if (createdBy != null) {
            sourceMap.put("createdBy", createdBy);
        }
        if (eventType != null) {
            sourceMap.put("eventType", eventType);
        }
        if (message != null) {
            sourceMap.put("message", message);
        }
        if (path != null) {
            sourceMap.put("path", path);
        }
        return sourceMap;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    protected String doBuildColumnString(String dm) {
        StringBuilder sb = new StringBuilder();
        sb.append(dm).append(createdAt);
        sb.append(dm).append(createdBy);
        sb.append(dm).append(eventType);
        sb.append(dm).append(message);
        sb.append(dm).append(path);
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

    public String getCreatedBy() {
        checkSpecifiedProperty("createdBy");
        return convertEmptyToNull(createdBy);
    }

    public void setCreatedBy(String value) {
        registerModifiedProperty("createdBy");
        this.createdBy = value;
    }

    public String getEventType() {
        checkSpecifiedProperty("eventType");
        return convertEmptyToNull(eventType);
    }

    public void setEventType(String value) {
        registerModifiedProperty("eventType");
        this.eventType = value;
    }

    public String getMessage() {
        checkSpecifiedProperty("message");
        return convertEmptyToNull(message);
    }

    public void setMessage(String value) {
        registerModifiedProperty("message");
        this.message = value;
    }

    public String getPath() {
        checkSpecifiedProperty("path");
        return convertEmptyToNull(path);
    }

    public void setPath(String value) {
        registerModifiedProperty("path");
        this.path = value;
    }
}
