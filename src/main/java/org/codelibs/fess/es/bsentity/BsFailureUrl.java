package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.FailureUrlDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsFailureUrl extends AbstractEntity {

    private static final long serialVersionUID = 1L;

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

    /** id */
    protected String id;

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
        return configId;
    }

    public void setConfigId(String value) {
        configId = value;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer value) {
        errorCount = value;
    }

    public String getErrorLog() {
        return errorLog;
    }

    public void setErrorLog(String value) {
        errorLog = value;
    }

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String value) {
        errorName = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public Long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Long value) {
        lastAccessTime = value;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String value) {
        threadName = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String value) {
        url = value;
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
        if (id != null) {
            sourceMap.put("id", id);
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
