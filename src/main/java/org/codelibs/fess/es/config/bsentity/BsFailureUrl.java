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

    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    @Override
    public FailureUrlDbm asDBMeta() {
        return FailureUrlDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "failure_url";
    }

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
    //                                                                            Accessor
    //                                                                            ========
    public String getConfigId() {
        checkSpecifiedProperty("configId");
        return configId;
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
        return errorLog;
    }

    public void setErrorLog(String value) {
        registerModifiedProperty("errorLog");
        this.errorLog = value;
    }

    public String getErrorName() {
        checkSpecifiedProperty("errorName");
        return errorName;
    }

    public void setErrorName(String value) {
        registerModifiedProperty("errorName");
        this.errorName = value;
    }

    public String getId() {
        checkSpecifiedProperty("id");
        return asDocMeta().id();
    }

    public void setId(String value) {
        registerModifiedProperty("id");
        asDocMeta().id(value);
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
        return threadName;
    }

    public void setThreadName(String value) {
        registerModifiedProperty("threadName");
        this.threadName = value;
    }

    public String getUrl() {
        checkSpecifiedProperty("url");
        return url;
    }

    public void setUrl(String value) {
        registerModifiedProperty("url");
        this.url = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (configId != null) {
            sourceMap.put("configId", configId);
        }
        if (errorCount != null) {
            sourceMap.put("errorCount", errorCount);
        }
        if (errorLog != null) {
            sourceMap.put("errorLog", errorLog);
        }
        if (errorName != null) {
            sourceMap.put("errorName", errorName);
        }
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
        }
        if (lastAccessTime != null) {
            sourceMap.put("lastAccessTime", lastAccessTime);
        }
        if (threadName != null) {
            sourceMap.put("threadName", threadName);
        }
        if (url != null) {
            sourceMap.put("url", url);
        }
        return sourceMap;
    }
}
