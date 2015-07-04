package org.codelibs.fess.es.bsentity;

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

    /** id */
    protected String id;

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
        return createdBy;
    }

    public void setCreatedBy(String value) {
        createdBy = value;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long value) {
        createdTime = value;
    }

    public String getFileConfigId() {
        return fileConfigId;
    }

    public void setFileConfigId(String value) {
        fileConfigId = value;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String value) {
        hostname = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String value) {
        parameters = value;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String value) {
        password = value;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer value) {
        port = value;
    }

    public String getProtocolScheme() {
        return protocolScheme;
    }

    public void setProtocolScheme(String value) {
        protocolScheme = value;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String value) {
        updatedBy = value;
    }

    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long value) {
        updatedTime = value;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String value) {
        username = value;
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
        if (id != null) {
            sourceMap.put("id", id);
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
