/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.db.bsentity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jp.sf.fess.db.allcommon.DBMetaInstanceHandler;
import jp.sf.fess.db.exentity.WebAuthentication;
import jp.sf.fess.db.exentity.WebCrawlingConfig;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;

/**
 * The entity of WEB_AUTHENTICATION as TABLE. <br />
 * <pre>
 * [primary-key]
 *     ID
 * 
 * [column]
 *     ID, HOSTNAME, PORT, AUTH_REALM, PROTOCOL_SCHEME, USERNAME, PASSWORD, PARAMETERS, WEB_CRAWLING_CONFIG_ID, CREATED_BY, CREATED_TIME, UPDATED_BY, UPDATED_TIME, DELETED_BY, DELETED_TIME, VERSION_NO
 * 
 * [sequence]
 *     
 * 
 * [identity]
 *     ID
 * 
 * [version-no]
 *     VERSION_NO
 * 
 * [foreign table]
 *     WEB_CRAWLING_CONFIG
 * 
 * [referrer table]
 *     
 * 
 * [foreign property]
 *     webCrawlingConfig
 * 
 * [referrer property]
 *     
 * 
 * [get/set template]
 * /= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
 * Long id = entity.getId();
 * String hostname = entity.getHostname();
 * Integer port = entity.getPort();
 * String authRealm = entity.getAuthRealm();
 * String protocolScheme = entity.getProtocolScheme();
 * String username = entity.getUsername();
 * String password = entity.getPassword();
 * String parameters = entity.getParameters();
 * Long webCrawlingConfigId = entity.getWebCrawlingConfigId();
 * String createdBy = entity.getCreatedBy();
 * java.sql.Timestamp createdTime = entity.getCreatedTime();
 * String updatedBy = entity.getUpdatedBy();
 * java.sql.Timestamp updatedTime = entity.getUpdatedTime();
 * String deletedBy = entity.getDeletedBy();
 * java.sql.Timestamp deletedTime = entity.getDeletedTime();
 * Integer versionNo = entity.getVersionNo();
 * entity.setId(id);
 * entity.setHostname(hostname);
 * entity.setPort(port);
 * entity.setAuthRealm(authRealm);
 * entity.setProtocolScheme(protocolScheme);
 * entity.setUsername(username);
 * entity.setPassword(password);
 * entity.setParameters(parameters);
 * entity.setWebCrawlingConfigId(webCrawlingConfigId);
 * entity.setCreatedBy(createdBy);
 * entity.setCreatedTime(createdTime);
 * entity.setUpdatedBy(updatedBy);
 * entity.setUpdatedTime(updatedTime);
 * entity.setDeletedBy(deletedBy);
 * entity.setDeletedTime(deletedTime);
 * entity.setVersionNo(versionNo);
 * = = = = = = = = = =/
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsWebAuthentication implements Entity, Serializable,
        Cloneable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    /** ID: {PK, ID, NotNull, BIGINT(19)} */
    protected Long _id;

    /** HOSTNAME: {VARCHAR(100)} */
    protected String _hostname;

    /** PORT: {NotNull, INTEGER(10)} */
    protected Integer _port;

    /** AUTH_REALM: {VARCHAR(100)} */
    protected String _authRealm;

    /** PROTOCOL_SCHEME: {VARCHAR(10)} */
    protected String _protocolScheme;

    /** USERNAME: {NotNull, VARCHAR(100)} */
    protected String _username;

    /** PASSWORD: {VARCHAR(100)} */
    protected String _password;

    /** PARAMETERS: {VARCHAR(1000)} */
    protected String _parameters;

    /** WEB_CRAWLING_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG} */
    protected Long _webCrawlingConfigId;

    /** CREATED_BY: {NotNull, VARCHAR(255)} */
    protected String _createdBy;

    /** CREATED_TIME: {NotNull, TIMESTAMP(23, 10)} */
    protected java.sql.Timestamp _createdTime;

    /** UPDATED_BY: {VARCHAR(255)} */
    protected String _updatedBy;

    /** UPDATED_TIME: {TIMESTAMP(23, 10)} */
    protected java.sql.Timestamp _updatedTime;

    /** DELETED_BY: {VARCHAR(255)} */
    protected String _deletedBy;

    /** DELETED_TIME: {TIMESTAMP(23, 10)} */
    protected java.sql.Timestamp _deletedTime;

    /** VERSION_NO: {NotNull, INTEGER(10)} */
    protected Integer _versionNo;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    /** The modified properties for this entity. (NotNull) */
    protected final EntityModifiedProperties __modifiedProperties = newModifiedProperties();

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    /**
     * {@inheritDoc}
     */
    @Override
    public String getTableDbName() {
        return "WEB_AUTHENTICATION";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTablePropertyName() { // according to Java Beans rule
        return "webAuthentication";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /**
     * {@inheritDoc}
     */
    @Override
    public DBMeta getDBMeta() {
        return DBMetaInstanceHandler.findDBMeta(getTableDbName());
    }

    // ===================================================================================
    //                                                                         Primary Key
    //                                                                         ===========
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPrimaryKeyValue() {
        if (getId() == null) {
            return false;
        }
        return true;
    }

    // ===================================================================================
    //                                                                    Foreign Property
    //                                                                    ================
    /** WEB_CRAWLING_CONFIG by my WEB_CRAWLING_CONFIG_ID, named 'webCrawlingConfig'. */
    protected WebCrawlingConfig _webCrawlingConfig;

    /**
     * WEB_CRAWLING_CONFIG by my WEB_CRAWLING_CONFIG_ID, named 'webCrawlingConfig'.
     * @return The entity of foreign property 'webCrawlingConfig'. (NullAllowed: when e.g. null FK column, no setupSelect)
     */
    public WebCrawlingConfig getWebCrawlingConfig() {
        return _webCrawlingConfig;
    }

    /**
     * WEB_CRAWLING_CONFIG by my WEB_CRAWLING_CONFIG_ID, named 'webCrawlingConfig'.
     * @param webCrawlingConfig The entity of foreign property 'webCrawlingConfig'. (NullAllowed)
     */
    public void setWebCrawlingConfig(final WebCrawlingConfig webCrawlingConfig) {
        _webCrawlingConfig = webCrawlingConfig;
    }

    // ===================================================================================
    //                                                                   Referrer Property
    //                                                                   =================
    protected <ELEMENT> List<ELEMENT> newReferrerList() {
        return new ArrayList<ELEMENT>();
    }

    // ===================================================================================
    //                                                                 Modified Properties
    //                                                                 ===================
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> modifiedProperties() {
        return __modifiedProperties.getPropertyNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearModifiedInfo() {
        __modifiedProperties.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasModification() {
        return !__modifiedProperties.isEmpty();
    }

    protected EntityModifiedProperties newModifiedProperties() {
        return new EntityModifiedProperties();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * Determine the object is equal with this. <br />
     * If primary-keys or columns of the other are same as this one, returns true.
     * @param other The other entity. (NullAllowed: if null, returns false fixedly)
     * @return Comparing result.
     */
    @Override
    public boolean equals(final Object other) {
        if (other == null || !(other instanceof BsWebAuthentication)) {
            return false;
        }
        final BsWebAuthentication otherEntity = (BsWebAuthentication) other;
        if (!xSV(getId(), otherEntity.getId())) {
            return false;
        }
        return true;
    }

    protected boolean xSV(final Object value1, final Object value2) { // isSameValue()
        return InternalUtil.isSameValue(value1, value2);
    }

    /**
     * Calculate the hash-code from primary-keys or columns.
     * @return The hash-code from primary-key or columns.
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = xCH(result, getTableDbName());
        result = xCH(result, getId());
        return result;
    }

    protected int xCH(final int result, final Object value) { // calculateHashcode()
        return InternalUtil.calculateHashcode(result, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int instanceHash() {
        return super.hashCode();
    }

    /**
     * Convert to display string of entity's data. (no relation data)
     * @return The display string of all columns and relation existences. (NotNull)
     */
    @Override
    public String toString() {
        return buildDisplayString(InternalUtil.toClassTitle(this), true, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toStringWithRelation() {
        final StringBuilder sb = new StringBuilder();
        sb.append(toString());
        final String l = "\n  ";
        if (_webCrawlingConfig != null) {
            sb.append(l).append(xbRDS(_webCrawlingConfig, "webCrawlingConfig"));
        }
        return sb.toString();
    }

    protected String xbRDS(final Entity e, final String name) { // buildRelationDisplayString()
        return e.buildDisplayString(name, true, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String buildDisplayString(final String name, final boolean column,
            final boolean relation) {
        final StringBuilder sb = new StringBuilder();
        if (name != null) {
            sb.append(name).append(column || relation ? ":" : "");
        }
        if (column) {
            sb.append(buildColumnString());
        }
        if (relation) {
            sb.append(buildRelationString());
        }
        sb.append("@").append(Integer.toHexString(hashCode()));
        return sb.toString();
    }

    protected String buildColumnString() {
        final StringBuilder sb = new StringBuilder();
        final String delimiter = ", ";
        sb.append(delimiter).append(getId());
        sb.append(delimiter).append(getHostname());
        sb.append(delimiter).append(getPort());
        sb.append(delimiter).append(getAuthRealm());
        sb.append(delimiter).append(getProtocolScheme());
        sb.append(delimiter).append(getUsername());
        sb.append(delimiter).append(getPassword());
        sb.append(delimiter).append(getParameters());
        sb.append(delimiter).append(getWebCrawlingConfigId());
        sb.append(delimiter).append(getCreatedBy());
        sb.append(delimiter).append(getCreatedTime());
        sb.append(delimiter).append(getUpdatedBy());
        sb.append(delimiter).append(getUpdatedTime());
        sb.append(delimiter).append(getDeletedBy());
        sb.append(delimiter).append(getDeletedTime());
        sb.append(delimiter).append(getVersionNo());
        if (sb.length() > delimiter.length()) {
            sb.delete(0, delimiter.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    protected String buildRelationString() {
        final StringBuilder sb = new StringBuilder();
        final String c = ",";
        if (_webCrawlingConfig != null) {
            sb.append(c).append("webCrawlingConfig");
        }
        if (sb.length() > c.length()) {
            sb.delete(0, c.length()).insert(0, "(").append(")");
        }
        return sb.toString();
    }

    /**
     * Clone entity instance using super.clone(). (shallow copy) 
     * @return The cloned instance of this entity. (NotNull)
     */
    @Override
    public WebAuthentication clone() {
        try {
            return (WebAuthentication) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new IllegalStateException("Failed to clone the entity: "
                    + toString(), e);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * [get] ID: {PK, ID, NotNull, BIGINT(19)} <br />
     * @return The value of the column 'ID'. (basically NotNull if selected: for the constraint)
     */
    public Long getId() {
        return _id;
    }

    /**
     * [set] ID: {PK, ID, NotNull, BIGINT(19)} <br />
     * @param id The value of the column 'ID'. (basically NotNull if update: for the constraint)
     */
    public void setId(final Long id) {
        __modifiedProperties.addPropertyName("id");
        _id = id;
    }

    /**
     * [get] HOSTNAME: {VARCHAR(100)} <br />
     * @return The value of the column 'HOSTNAME'. (NullAllowed even if selected: for no constraint)
     */
    public String getHostname() {
        return _hostname;
    }

    /**
     * [set] HOSTNAME: {VARCHAR(100)} <br />
     * @param hostname The value of the column 'HOSTNAME'. (NullAllowed: null update allowed for no constraint)
     */
    public void setHostname(final String hostname) {
        __modifiedProperties.addPropertyName("hostname");
        _hostname = hostname;
    }

    /**
     * [get] PORT: {NotNull, INTEGER(10)} <br />
     * @return The value of the column 'PORT'. (basically NotNull if selected: for the constraint)
     */
    public Integer getPort() {
        return _port;
    }

    /**
     * [set] PORT: {NotNull, INTEGER(10)} <br />
     * @param port The value of the column 'PORT'. (basically NotNull if update: for the constraint)
     */
    public void setPort(final Integer port) {
        __modifiedProperties.addPropertyName("port");
        _port = port;
    }

    /**
     * [get] AUTH_REALM: {VARCHAR(100)} <br />
     * @return The value of the column 'AUTH_REALM'. (NullAllowed even if selected: for no constraint)
     */
    public String getAuthRealm() {
        return _authRealm;
    }

    /**
     * [set] AUTH_REALM: {VARCHAR(100)} <br />
     * @param authRealm The value of the column 'AUTH_REALM'. (NullAllowed: null update allowed for no constraint)
     */
    public void setAuthRealm(final String authRealm) {
        __modifiedProperties.addPropertyName("authRealm");
        _authRealm = authRealm;
    }

    /**
     * [get] PROTOCOL_SCHEME: {VARCHAR(10)} <br />
     * @return The value of the column 'PROTOCOL_SCHEME'. (NullAllowed even if selected: for no constraint)
     */
    public String getProtocolScheme() {
        return _protocolScheme;
    }

    /**
     * [set] PROTOCOL_SCHEME: {VARCHAR(10)} <br />
     * @param protocolScheme The value of the column 'PROTOCOL_SCHEME'. (NullAllowed: null update allowed for no constraint)
     */
    public void setProtocolScheme(final String protocolScheme) {
        __modifiedProperties.addPropertyName("protocolScheme");
        _protocolScheme = protocolScheme;
    }

    /**
     * [get] USERNAME: {NotNull, VARCHAR(100)} <br />
     * @return The value of the column 'USERNAME'. (basically NotNull if selected: for the constraint)
     */
    public String getUsername() {
        return _username;
    }

    /**
     * [set] USERNAME: {NotNull, VARCHAR(100)} <br />
     * @param username The value of the column 'USERNAME'. (basically NotNull if update: for the constraint)
     */
    public void setUsername(final String username) {
        __modifiedProperties.addPropertyName("username");
        _username = username;
    }

    /**
     * [get] PASSWORD: {VARCHAR(100)} <br />
     * @return The value of the column 'PASSWORD'. (NullAllowed even if selected: for no constraint)
     */
    public String getPassword() {
        return _password;
    }

    /**
     * [set] PASSWORD: {VARCHAR(100)} <br />
     * @param password The value of the column 'PASSWORD'. (NullAllowed: null update allowed for no constraint)
     */
    public void setPassword(final String password) {
        __modifiedProperties.addPropertyName("password");
        _password = password;
    }

    /**
     * [get] PARAMETERS: {VARCHAR(1000)} <br />
     * @return The value of the column 'PARAMETERS'. (NullAllowed even if selected: for no constraint)
     */
    public String getParameters() {
        return _parameters;
    }

    /**
     * [set] PARAMETERS: {VARCHAR(1000)} <br />
     * @param parameters The value of the column 'PARAMETERS'. (NullAllowed: null update allowed for no constraint)
     */
    public void setParameters(final String parameters) {
        __modifiedProperties.addPropertyName("parameters");
        _parameters = parameters;
    }

    /**
     * [get] WEB_CRAWLING_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG} <br />
     * @return The value of the column 'WEB_CRAWLING_CONFIG_ID'. (basically NotNull if selected: for the constraint)
     */
    public Long getWebCrawlingConfigId() {
        return _webCrawlingConfigId;
    }

    /**
     * [set] WEB_CRAWLING_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG} <br />
     * @param webCrawlingConfigId The value of the column 'WEB_CRAWLING_CONFIG_ID'. (basically NotNull if update: for the constraint)
     */
    public void setWebCrawlingConfigId(final Long webCrawlingConfigId) {
        __modifiedProperties.addPropertyName("webCrawlingConfigId");
        _webCrawlingConfigId = webCrawlingConfigId;
    }

    /**
     * [get] CREATED_BY: {NotNull, VARCHAR(255)} <br />
     * @return The value of the column 'CREATED_BY'. (basically NotNull if selected: for the constraint)
     */
    public String getCreatedBy() {
        return _createdBy;
    }

    /**
     * [set] CREATED_BY: {NotNull, VARCHAR(255)} <br />
     * @param createdBy The value of the column 'CREATED_BY'. (basically NotNull if update: for the constraint)
     */
    public void setCreatedBy(final String createdBy) {
        __modifiedProperties.addPropertyName("createdBy");
        _createdBy = createdBy;
    }

    /**
     * [get] CREATED_TIME: {NotNull, TIMESTAMP(23, 10)} <br />
     * @return The value of the column 'CREATED_TIME'. (basically NotNull if selected: for the constraint)
     */
    public java.sql.Timestamp getCreatedTime() {
        return _createdTime;
    }

    /**
     * [set] CREATED_TIME: {NotNull, TIMESTAMP(23, 10)} <br />
     * @param createdTime The value of the column 'CREATED_TIME'. (basically NotNull if update: for the constraint)
     */
    public void setCreatedTime(final java.sql.Timestamp createdTime) {
        __modifiedProperties.addPropertyName("createdTime");
        _createdTime = createdTime;
    }

    /**
     * [get] UPDATED_BY: {VARCHAR(255)} <br />
     * @return The value of the column 'UPDATED_BY'. (NullAllowed even if selected: for no constraint)
     */
    public String getUpdatedBy() {
        return _updatedBy;
    }

    /**
     * [set] UPDATED_BY: {VARCHAR(255)} <br />
     * @param updatedBy The value of the column 'UPDATED_BY'. (NullAllowed: null update allowed for no constraint)
     */
    public void setUpdatedBy(final String updatedBy) {
        __modifiedProperties.addPropertyName("updatedBy");
        _updatedBy = updatedBy;
    }

    /**
     * [get] UPDATED_TIME: {TIMESTAMP(23, 10)} <br />
     * @return The value of the column 'UPDATED_TIME'. (NullAllowed even if selected: for no constraint)
     */
    public java.sql.Timestamp getUpdatedTime() {
        return _updatedTime;
    }

    /**
     * [set] UPDATED_TIME: {TIMESTAMP(23, 10)} <br />
     * @param updatedTime The value of the column 'UPDATED_TIME'. (NullAllowed: null update allowed for no constraint)
     */
    public void setUpdatedTime(final java.sql.Timestamp updatedTime) {
        __modifiedProperties.addPropertyName("updatedTime");
        _updatedTime = updatedTime;
    }

    /**
     * [get] DELETED_BY: {VARCHAR(255)} <br />
     * @return The value of the column 'DELETED_BY'. (NullAllowed even if selected: for no constraint)
     */
    public String getDeletedBy() {
        return _deletedBy;
    }

    /**
     * [set] DELETED_BY: {VARCHAR(255)} <br />
     * @param deletedBy The value of the column 'DELETED_BY'. (NullAllowed: null update allowed for no constraint)
     */
    public void setDeletedBy(final String deletedBy) {
        __modifiedProperties.addPropertyName("deletedBy");
        _deletedBy = deletedBy;
    }

    /**
     * [get] DELETED_TIME: {TIMESTAMP(23, 10)} <br />
     * @return The value of the column 'DELETED_TIME'. (NullAllowed even if selected: for no constraint)
     */
    public java.sql.Timestamp getDeletedTime() {
        return _deletedTime;
    }

    /**
     * [set] DELETED_TIME: {TIMESTAMP(23, 10)} <br />
     * @param deletedTime The value of the column 'DELETED_TIME'. (NullAllowed: null update allowed for no constraint)
     */
    public void setDeletedTime(final java.sql.Timestamp deletedTime) {
        __modifiedProperties.addPropertyName("deletedTime");
        _deletedTime = deletedTime;
    }

    /**
     * [get] VERSION_NO: {NotNull, INTEGER(10)} <br />
     * @return The value of the column 'VERSION_NO'. (basically NotNull if selected: for the constraint)
     */
    public Integer getVersionNo() {
        return _versionNo;
    }

    /**
     * [set] VERSION_NO: {NotNull, INTEGER(10)} <br />
     * @param versionNo The value of the column 'VERSION_NO'. (basically NotNull if update: for the constraint)
     */
    public void setVersionNo(final Integer versionNo) {
        __modifiedProperties.addPropertyName("versionNo");
        _versionNo = versionNo;
    }
}
