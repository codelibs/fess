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
import org.codelibs.fess.es.config.bsentity.dbmeta.FailureUrlDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsFailureUrl extends EsAbstractEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** configId */
    protected String configId;

    /** errorCount */
    protected Integer errorCount;

    /** errorLog */
    protected String errorLog;

    /** errorName */
    protected String errorName;

    /** lastAccessTime */
    protected Long lastAccessTime;

    /** threadName */
    protected String threadName;

    /** url */
    protected String url;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public FailureUrlDbm asDBMeta() {
        return FailureUrlDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "failure_url";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (configId != null) {
            addFieldToSource(sourceMap, "configId", configId);
        }
        if (errorCount != null) {
            addFieldToSource(sourceMap, "errorCount", errorCount);
        }
        if (errorLog != null) {
            addFieldToSource(sourceMap, "errorLog", errorLog);
        }
        if (errorName != null) {
            addFieldToSource(sourceMap, "errorName", errorName);
        }
        if (lastAccessTime != null) {
            addFieldToSource(sourceMap, "lastAccessTime", lastAccessTime);
        }
        if (threadName != null) {
            addFieldToSource(sourceMap, "threadName", threadName);
        }
        if (url != null) {
            addFieldToSource(sourceMap, "url", url);
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
        sb.append(dm).append(configId);
        sb.append(dm).append(errorCount);
        sb.append(dm).append(errorLog);
        sb.append(dm).append(errorName);
        sb.append(dm).append(lastAccessTime);
        sb.append(dm).append(threadName);
        sb.append(dm).append(url);
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getConfigId() {
        checkSpecifiedProperty("configId");
        return convertEmptyToNull(configId);
    }

    public void setConfigId(String value) {
        registerModifiedProperty("configId");
        this.configId = value;
    }

    public Integer getErrorCount() {
        checkSpecifiedProperty("errorCount");
        return errorCount;
    }

    public void setErrorCount(Integer value) {
        registerModifiedProperty("errorCount");
        this.errorCount = value;
    }

    public String getErrorLog() {
        checkSpecifiedProperty("errorLog");
        return convertEmptyToNull(errorLog);
    }

    public void setErrorLog(String value) {
        registerModifiedProperty("errorLog");
        this.errorLog = value;
    }

    public String getErrorName() {
        checkSpecifiedProperty("errorName");
        return convertEmptyToNull(errorName);
    }

    public void setErrorName(String value) {
        registerModifiedProperty("errorName");
        this.errorName = value;
    }

    public Long getLastAccessTime() {
        checkSpecifiedProperty("lastAccessTime");
        return lastAccessTime;
    }

    public void setLastAccessTime(Long value) {
        registerModifiedProperty("lastAccessTime");
        this.lastAccessTime = value;
    }

    public String getThreadName() {
        checkSpecifiedProperty("threadName");
        return convertEmptyToNull(threadName);
    }

    public void setThreadName(String value) {
        registerModifiedProperty("threadName");
        this.threadName = value;
    }

    public String getUrl() {
        checkSpecifiedProperty("url");
        return convertEmptyToNull(url);
    }

    public void setUrl(String value) {
        registerModifiedProperty("url");
        this.url = value;
    }
}
