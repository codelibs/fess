package org.codelibs.fess.es.bsentity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.FileAuthenticationDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsFileAuthentication extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public FileAuthenticationDbm asDBMeta() {
        return FileAuthenticationDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "file_authentication";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** createdBy */
    protected String createdBy;

    /** createdTime */
    protected Long createdTime;

    /** fileConfigId */
    protected String fileConfigId;

    /** hostname */
    protected String hostname;

    /** parameters */
    protected String parameters;

    /** password */
    protected String password;

    /** port */
    protected Integer port;

    /** protocolScheme */
    protected String protocolScheme;

    /** updatedBy */
    protected String updatedBy;

    /** updatedTime */
    protected Long updatedTime;

    /** username */
    protected String username;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getCreatedBy() {
        checkSpecifiedProperty("createdBy");
        return createdBy;
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

    public String getFileConfigId() {
        checkSpecifiedProperty("fileConfigId");
        return fileConfigId;
    }

    public void setFileConfigId(String value) {
        registerModifiedProperty("fileConfigId");
        this.fileConfigId = value;
    }

    public String getHostname() {
        checkSpecifiedProperty("hostname");
        return hostname;
    }

    public void setHostname(String value) {
        registerModifiedProperty("hostname");
        this.hostname = value;
    }

    public String getId() {
        checkSpecifiedProperty("id");
        return asDocMeta().id();
    }

    public void setId(String value) {
        registerModifiedProperty("id");
        asDocMeta().id(value);
    }

    public String getParameters() {
        checkSpecifiedProperty("parameters");
        return parameters;
    }

    public void setParameters(String value) {
        registerModifiedProperty("parameters");
        this.parameters = value;
    }

    public String getPassword() {
        checkSpecifiedProperty("password");
        return password;
    }

    public void setPassword(String value) {
        registerModifiedProperty("password");
        this.password = value;
    }

    public Integer getPort() {
        checkSpecifiedProperty("port");
        return port;
    }

    public void setPort(Integer value) {
        registerModifiedProperty("port");
        this.port = value;
    }

    public String getProtocolScheme() {
        checkSpecifiedProperty("protocolScheme");
        return protocolScheme;
    }

    public void setProtocolScheme(String value) {
        registerModifiedProperty("protocolScheme");
        this.protocolScheme = value;
    }

    public String getUpdatedBy() {
        checkSpecifiedProperty("updatedBy");
        return updatedBy;
    }

    public void setUpdatedBy(String value) {
        registerModifiedProperty("updatedBy");
        this.updatedBy = value;
    }

    public Long getUpdatedTime() {
        checkSpecifiedProperty("updatedTime");
        return updatedTime;
    }

    public void setUpdatedTime(Long value) {
        registerModifiedProperty("updatedTime");
        this.updatedTime = value;
    }

    public String getUsername() {
        checkSpecifiedProperty("username");
        return username;
    }

    public void setUsername(String value) {
        registerModifiedProperty("username");
        this.username = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (createdBy != null) {
            sourceMap.put("createdBy", createdBy);
        }
        if (createdTime != null) {
            sourceMap.put("createdTime", createdTime);
        }
        if (fileConfigId != null) {
            sourceMap.put("fileConfigId", fileConfigId);
        }
        if (hostname != null) {
            sourceMap.put("hostname", hostname);
        }
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
        }
        if (parameters != null) {
            sourceMap.put("parameters", parameters);
        }
        if (password != null) {
            sourceMap.put("password", password);
        }
        if (port != null) {
            sourceMap.put("port", port);
        }
        if (protocolScheme != null) {
            sourceMap.put("protocolScheme", protocolScheme);
        }
        if (updatedBy != null) {
            sourceMap.put("updatedBy", updatedBy);
        }
        if (updatedTime != null) {
            sourceMap.put("updatedTime", updatedTime);
        }
        if (username != null) {
            sourceMap.put("username", username);
        }
        return sourceMap;
    }
}
