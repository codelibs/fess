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
import org.codelibs.fess.es.config.bsentity.dbmeta.FileAuthenticationDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsFileAuthentication extends EsAbstractEntity {

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
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public FileAuthenticationDbm asDBMeta() {
        return FileAuthenticationDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "file_authentication";
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
        if (fileConfigId != null) {
            addFieldToSource(sourceMap, "fileConfigId", fileConfigId);
        }
        if (hostname != null) {
            addFieldToSource(sourceMap, "hostname", hostname);
        }
        if (parameters != null) {
            addFieldToSource(sourceMap, "parameters", parameters);
        }
        if (password != null) {
            addFieldToSource(sourceMap, "password", password);
        }
        if (port != null) {
            addFieldToSource(sourceMap, "port", port);
        }
        if (protocolScheme != null) {
            addFieldToSource(sourceMap, "protocolScheme", protocolScheme);
        }
        if (updatedBy != null) {
            addFieldToSource(sourceMap, "updatedBy", updatedBy);
        }
        if (updatedTime != null) {
            addFieldToSource(sourceMap, "updatedTime", updatedTime);
        }
        if (username != null) {
            addFieldToSource(sourceMap, "username", username);
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
        sb.append(dm).append(fileConfigId);
        sb.append(dm).append(hostname);
        sb.append(dm).append(parameters);
        sb.append(dm).append(password);
        sb.append(dm).append(port);
        sb.append(dm).append(protocolScheme);
        sb.append(dm).append(updatedBy);
        sb.append(dm).append(updatedTime);
        sb.append(dm).append(username);
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

    public String getFileConfigId() {
        checkSpecifiedProperty("fileConfigId");
        return convertEmptyToNull(fileConfigId);
    }

    public void setFileConfigId(String value) {
        registerModifiedProperty("fileConfigId");
        this.fileConfigId = value;
    }

    public String getHostname() {
        checkSpecifiedProperty("hostname");
        return convertEmptyToNull(hostname);
    }

    public void setHostname(String value) {
        registerModifiedProperty("hostname");
        this.hostname = value;
    }

    public String getParameters() {
        checkSpecifiedProperty("parameters");
        return convertEmptyToNull(parameters);
    }

    public void setParameters(String value) {
        registerModifiedProperty("parameters");
        this.parameters = value;
    }

    public String getPassword() {
        checkSpecifiedProperty("password");
        return convertEmptyToNull(password);
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
        return convertEmptyToNull(protocolScheme);
    }

    public void setProtocolScheme(String value) {
        registerModifiedProperty("protocolScheme");
        this.protocolScheme = value;
    }

    public String getUpdatedBy() {
        checkSpecifiedProperty("updatedBy");
        return convertEmptyToNull(updatedBy);
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
        return convertEmptyToNull(username);
    }

    public void setUsername(String value) {
        registerModifiedProperty("username");
        this.username = value;
    }
}
