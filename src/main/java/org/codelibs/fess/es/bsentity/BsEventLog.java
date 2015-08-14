package org.codelibs.fess.es.bsentity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.EventLogDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsEventLog extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public EventLogDbm asDBMeta() {
        return EventLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "event_log";
    }

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
        return createdBy;
    }

    public void setCreatedBy(String value) {
        registerModifiedProperty("createdBy");
        this.createdBy = value;
    }

    public String getEventType() {
        checkSpecifiedProperty("eventType");
        return eventType;
    }

    public void setEventType(String value) {
        registerModifiedProperty("eventType");
        this.eventType = value;
    }

    public String getId() {
        checkSpecifiedProperty("id");
        return asDocMeta().id();
    }

    public void setId(String value) {
        registerModifiedProperty("id");
        asDocMeta().id(value);
    }

    public String getMessage() {
        checkSpecifiedProperty("message");
        return message;
    }

    public void setMessage(String value) {
        registerModifiedProperty("message");
        this.message = value;
    }

    public String getPath() {
        checkSpecifiedProperty("path");
        return path;
    }

    public void setPath(String value) {
        registerModifiedProperty("path");
        this.path = value;
    }

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
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
        }
        if (message != null) {
            sourceMap.put("message", message);
        }
        if (path != null) {
            sourceMap.put("path", path);
        }
        return sourceMap;
    }
}
