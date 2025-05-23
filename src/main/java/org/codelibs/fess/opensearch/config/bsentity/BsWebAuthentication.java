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
package org.codelibs.fess.opensearch.config.bsentity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.opensearch.config.allcommon.EsAbstractEntity;
import org.codelibs.fess.opensearch.config.bsentity.dbmeta.WebAuthenticationDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsWebAuthentication extends EsAbstractEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** authRealm */
    protected String authRealm;

    /** createdBy */
    protected String createdBy;

    /** createdTime */
    protected Long createdTime;

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

    /** webConfigId */
    protected String webConfigId;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public WebAuthenticationDbm asDBMeta() {
        return WebAuthenticationDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "web_authentication";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (authRealm != null) {
            addFieldToSource(sourceMap, "authRealm", authRealm);
        }
        if (createdBy != null) {
            addFieldToSource(sourceMap, "createdBy", createdBy);
        }
        if (createdTime != null) {
            addFieldToSource(sourceMap, "createdTime", createdTime);
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
        if (webConfigId != null) {
            addFieldToSource(sourceMap, "webConfigId", webConfigId);
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
        sb.append(dm).append(authRealm);
        sb.append(dm).append(createdBy);
        sb.append(dm).append(createdTime);
        sb.append(dm).append(hostname);
        sb.append(dm).append(parameters);
        sb.append(dm).append(password);
        sb.append(dm).append(port);
        sb.append(dm).append(protocolScheme);
        sb.append(dm).append(updatedBy);
        sb.append(dm).append(updatedTime);
        sb.append(dm).append(username);
        sb.append(dm).append(webConfigId);
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getAuthRealm() {
        checkSpecifiedProperty("authRealm");
        return convertEmptyToNull(authRealm);
    }

    public void setAuthRealm(String value) {
        registerModifiedProperty("authRealm");
        this.authRealm = value;
    }

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

    public String getWebConfigId() {
        checkSpecifiedProperty("webConfigId");
        return convertEmptyToNull(webConfigId);
    }

    public void setWebConfigId(String value) {
        registerModifiedProperty("webConfigId");
        this.webConfigId = value;
    }
}
