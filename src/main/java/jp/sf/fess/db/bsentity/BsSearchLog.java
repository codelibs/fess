/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

import jp.sf.fess.db.allcommon.CDef;
import jp.sf.fess.db.allcommon.DBMetaInstanceHandler;
import jp.sf.fess.db.exentity.ClickLog;
import jp.sf.fess.db.exentity.SearchFieldLog;
import jp.sf.fess.db.exentity.SearchLog;
import jp.sf.fess.db.exentity.UserInfo;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;

/**
 * The entity of SEARCH_LOG as TABLE. <br />
 * <pre>
 * [primary-key]
 *     ID
 *
 * [column]
 *     ID, SEARCH_WORD, REQUESTED_TIME, RESPONSE_TIME, HIT_COUNT, QUERY_OFFSET, QUERY_PAGE_SIZE, USER_AGENT, REFERER, CLIENT_IP, USER_SESSION_ID, ACCESS_TYPE, USER_ID
 *
 * [sequence]
 *
 *
 * [identity]
 *     ID
 *
 * [version-no]
 *
 *
 * [foreign table]
 *     USER_INFO
 *
 * [referrer table]
 *     CLICK_LOG, SEARCH_FIELD_LOG
 *
 * [foreign property]
 *     userInfo
 *
 * [referrer property]
 *     clickLogList, searchFieldLogList
 *
 * [get/set template]
 * /= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
 * Long id = entity.getId();
 * String searchWord = entity.getSearchWord();
 * java.sql.Timestamp requestedTime = entity.getRequestedTime();
 * Integer responseTime = entity.getResponseTime();
 * Long hitCount = entity.getHitCount();
 * Integer queryOffset = entity.getQueryOffset();
 * Integer queryPageSize = entity.getQueryPageSize();
 * String userAgent = entity.getUserAgent();
 * String referer = entity.getReferer();
 * String clientIp = entity.getClientIp();
 * String userSessionId = entity.getUserSessionId();
 * String accessType = entity.getAccessType();
 * Long userId = entity.getUserId();
 * entity.setId(id);
 * entity.setSearchWord(searchWord);
 * entity.setRequestedTime(requestedTime);
 * entity.setResponseTime(responseTime);
 * entity.setHitCount(hitCount);
 * entity.setQueryOffset(queryOffset);
 * entity.setQueryPageSize(queryPageSize);
 * entity.setUserAgent(userAgent);
 * entity.setReferer(referer);
 * entity.setClientIp(clientIp);
 * entity.setUserSessionId(userSessionId);
 * entity.setAccessType(accessType);
 * entity.setUserId(userId);
 * = = = = = = = = = =/
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsSearchLog implements Entity, Serializable, Cloneable {

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

    /** SEARCH_WORD: {IX, VARCHAR(1000)} */
    protected String _searchWord;

    /** REQUESTED_TIME: {IX, NotNull, TIMESTAMP(23, 10)} */
    protected java.sql.Timestamp _requestedTime;

    /** RESPONSE_TIME: {IX, NotNull, INTEGER(10)} */
    protected Integer _responseTime;

    /** HIT_COUNT: {IX, NotNull, BIGINT(19)} */
    protected Long _hitCount;

    /** QUERY_OFFSET: {NotNull, INTEGER(10)} */
    protected Integer _queryOffset;

    /** QUERY_PAGE_SIZE: {NotNull, INTEGER(10)} */
    protected Integer _queryPageSize;

    /** USER_AGENT: {VARCHAR(255)} */
    protected String _userAgent;

    /** REFERER: {VARCHAR(1000)} */
    protected String _referer;

    /** CLIENT_IP: {VARCHAR(50)} */
    protected String _clientIp;

    /** USER_SESSION_ID: {IX+, VARCHAR(100)} */
    protected String _userSessionId;

    /** ACCESS_TYPE: {NotNull, VARCHAR(1), classification=AccessType} */
    protected String _accessType;

    /** USER_ID: {IX, BIGINT(19), FK to USER_INFO} */
    protected Long _userId;

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
        return "SEARCH_LOG";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTablePropertyName() { // according to Java Beans rule
        return "searchLog";
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
    //                                                             Classification Property
    //                                                             =======================
    /**
     * Get the value of accessType as the classification of AccessType. <br />
     * ACCESS_TYPE: {NotNull, VARCHAR(1), classification=AccessType} <br />
     * Access Type
     * <p>It's treated as case insensitive and if the code value is null, it returns null.</p>
     * @return The instance of classification definition (as ENUM type). (NullAllowed: when the column value is null)
     */
    public CDef.AccessType getAccessTypeAsAccessType() {
        return CDef.AccessType.codeOf(getAccessType());
    }

    /**
     * Set the value of accessType as the classification of AccessType. <br />
     * ACCESS_TYPE: {NotNull, VARCHAR(1), classification=AccessType} <br />
     * Access Type
     * @param cdef The instance of classification definition (as ENUM type). (NullAllowed: if null, null value is set to the column)
     */
    public void setAccessTypeAsAccessType(final CDef.AccessType cdef) {
        setAccessType(cdef != null ? cdef.code() : null);
    }

    // ===================================================================================
    //                                                              Classification Setting
    //                                                              ======================
    /**
     * Set the value of accessType as Web (W). <br />
     * Web: Web
     */
    public void setAccessType_Web() {
        setAccessTypeAsAccessType(CDef.AccessType.Web);
    }

    /**
     * Set the value of accessType as Xml (X). <br />
     * Xml: Xml
     */
    public void setAccessType_Xml() {
        setAccessTypeAsAccessType(CDef.AccessType.Xml);
    }

    /**
     * Set the value of accessType as Json (J). <br />
     * Json: Json
     */
    public void setAccessType_Json() {
        setAccessTypeAsAccessType(CDef.AccessType.Json);
    }

    /**
     * Set the value of accessType as Others (O). <br />
     * Others: Others
     */
    public void setAccessType_Others() {
        setAccessTypeAsAccessType(CDef.AccessType.Others);
    }

    // ===================================================================================
    //                                                        Classification Determination
    //                                                        ============================
    /**
     * Is the value of accessType Web? <br />
     * Web: Web
     * <p>It's treated as case insensitive and if the code value is null, it returns false.</p>
     * @return The determination, true or false.
     */
    public boolean isAccessTypeWeb() {
        final CDef.AccessType cdef = getAccessTypeAsAccessType();
        return cdef != null ? cdef.equals(CDef.AccessType.Web) : false;
    }

    /**
     * Is the value of accessType Xml? <br />
     * Xml: Xml
     * <p>It's treated as case insensitive and if the code value is null, it returns false.</p>
     * @return The determination, true or false.
     */
    public boolean isAccessTypeXml() {
        final CDef.AccessType cdef = getAccessTypeAsAccessType();
        return cdef != null ? cdef.equals(CDef.AccessType.Xml) : false;
    }

    /**
     * Is the value of accessType Json? <br />
     * Json: Json
     * <p>It's treated as case insensitive and if the code value is null, it returns false.</p>
     * @return The determination, true or false.
     */
    public boolean isAccessTypeJson() {
        final CDef.AccessType cdef = getAccessTypeAsAccessType();
        return cdef != null ? cdef.equals(CDef.AccessType.Json) : false;
    }

    /**
     * Is the value of accessType Others? <br />
     * Others: Others
     * <p>It's treated as case insensitive and if the code value is null, it returns false.</p>
     * @return The determination, true or false.
     */
    public boolean isAccessTypeOthers() {
        final CDef.AccessType cdef = getAccessTypeAsAccessType();
        return cdef != null ? cdef.equals(CDef.AccessType.Others) : false;
    }

    // ===================================================================================
    //                                                           Classification Name/Alias
    //                                                           =========================
    /**
     * Get the value of the column 'accessType' as classification name.
     * @return The string of classification name. (NullAllowed: when the column value is null)
     */
    public String getAccessTypeName() {
        final CDef.AccessType cdef = getAccessTypeAsAccessType();
        return cdef != null ? cdef.name() : null;
    }

    // ===================================================================================
    //                                                                    Foreign Property
    //                                                                    ================
    /** USER_INFO by my USER_ID, named 'userInfo'. */
    protected UserInfo _userInfo;

    /**
     * USER_INFO by my USER_ID, named 'userInfo'.
     * @return The entity of foreign property 'userInfo'. (NullAllowed: when e.g. null FK column, no setupSelect)
     */
    public UserInfo getUserInfo() {
        return _userInfo;
    }

    /**
     * USER_INFO by my USER_ID, named 'userInfo'.
     * @param userInfo The entity of foreign property 'userInfo'. (NullAllowed)
     */
    public void setUserInfo(final UserInfo userInfo) {
        _userInfo = userInfo;
    }

    // ===================================================================================
    //                                                                   Referrer Property
    //                                                                   =================
    /** CLICK_LOG by SEARCH_ID, named 'clickLogList'. */
    protected List<ClickLog> _clickLogList;

    /**
     * CLICK_LOG by SEARCH_ID, named 'clickLogList'.
     * @return The entity list of referrer property 'clickLogList'. (NotNull: even if no loading, returns empty list)
     */
    public List<ClickLog> getClickLogList() {
        if (_clickLogList == null) {
            _clickLogList = newReferrerList();
        }
        return _clickLogList;
    }

    /**
     * CLICK_LOG by SEARCH_ID, named 'clickLogList'.
     * @param clickLogList The entity list of referrer property 'clickLogList'. (NullAllowed)
     */
    public void setClickLogList(final List<ClickLog> clickLogList) {
        _clickLogList = clickLogList;
    }

    /** SEARCH_FIELD_LOG by SEARCH_ID, named 'searchFieldLogList'. */
    protected List<SearchFieldLog> _searchFieldLogList;

    /**
     * SEARCH_FIELD_LOG by SEARCH_ID, named 'searchFieldLogList'.
     * @return The entity list of referrer property 'searchFieldLogList'. (NotNull: even if no loading, returns empty list)
     */
    public List<SearchFieldLog> getSearchFieldLogList() {
        if (_searchFieldLogList == null) {
            _searchFieldLogList = newReferrerList();
        }
        return _searchFieldLogList;
    }

    /**
     * SEARCH_FIELD_LOG by SEARCH_ID, named 'searchFieldLogList'.
     * @param searchFieldLogList The entity list of referrer property 'searchFieldLogList'. (NullAllowed)
     */
    public void setSearchFieldLogList(
            final List<SearchFieldLog> searchFieldLogList) {
        _searchFieldLogList = searchFieldLogList;
    }

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
        if (other == null || !(other instanceof BsSearchLog)) {
            return false;
        }
        final BsSearchLog otherEntity = (BsSearchLog) other;
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
        if (_userInfo != null) {
            sb.append(l).append(xbRDS(_userInfo, "userInfo"));
        }
        if (_clickLogList != null) {
            for (final Entity e : _clickLogList) {
                if (e != null) {
                    sb.append(l).append(xbRDS(e, "clickLogList"));
                }
            }
        }
        if (_searchFieldLogList != null) {
            for (final Entity e : _searchFieldLogList) {
                if (e != null) {
                    sb.append(l).append(xbRDS(e, "searchFieldLogList"));
                }
            }
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
        sb.append(delimiter).append(getSearchWord());
        sb.append(delimiter).append(getRequestedTime());
        sb.append(delimiter).append(getResponseTime());
        sb.append(delimiter).append(getHitCount());
        sb.append(delimiter).append(getQueryOffset());
        sb.append(delimiter).append(getQueryPageSize());
        sb.append(delimiter).append(getUserAgent());
        sb.append(delimiter).append(getReferer());
        sb.append(delimiter).append(getClientIp());
        sb.append(delimiter).append(getUserSessionId());
        sb.append(delimiter).append(getAccessType());
        sb.append(delimiter).append(getUserId());
        if (sb.length() > delimiter.length()) {
            sb.delete(0, delimiter.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    protected String buildRelationString() {
        final StringBuilder sb = new StringBuilder();
        final String c = ",";
        if (_userInfo != null) {
            sb.append(c).append("userInfo");
        }
        if (_clickLogList != null && !_clickLogList.isEmpty()) {
            sb.append(c).append("clickLogList");
        }
        if (_searchFieldLogList != null && !_searchFieldLogList.isEmpty()) {
            sb.append(c).append("searchFieldLogList");
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
    public SearchLog clone() {
        try {
            return (SearchLog) super.clone();
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
     * [get] SEARCH_WORD: {IX, VARCHAR(1000)} <br />
     * @return The value of the column 'SEARCH_WORD'. (NullAllowed even if selected: for no constraint)
     */
    public String getSearchWord() {
        return _searchWord;
    }

    /**
     * [set] SEARCH_WORD: {IX, VARCHAR(1000)} <br />
     * @param searchWord The value of the column 'SEARCH_WORD'. (NullAllowed: null update allowed for no constraint)
     */
    public void setSearchWord(final String searchWord) {
        __modifiedProperties.addPropertyName("searchWord");
        _searchWord = searchWord;
    }

    /**
     * [get] REQUESTED_TIME: {IX, NotNull, TIMESTAMP(23, 10)} <br />
     * @return The value of the column 'REQUESTED_TIME'. (basically NotNull if selected: for the constraint)
     */
    public java.sql.Timestamp getRequestedTime() {
        return _requestedTime;
    }

    /**
     * [set] REQUESTED_TIME: {IX, NotNull, TIMESTAMP(23, 10)} <br />
     * @param requestedTime The value of the column 'REQUESTED_TIME'. (basically NotNull if update: for the constraint)
     */
    public void setRequestedTime(final java.sql.Timestamp requestedTime) {
        __modifiedProperties.addPropertyName("requestedTime");
        _requestedTime = requestedTime;
    }

    /**
     * [get] RESPONSE_TIME: {IX, NotNull, INTEGER(10)} <br />
     * @return The value of the column 'RESPONSE_TIME'. (basically NotNull if selected: for the constraint)
     */
    public Integer getResponseTime() {
        return _responseTime;
    }

    /**
     * [set] RESPONSE_TIME: {IX, NotNull, INTEGER(10)} <br />
     * @param responseTime The value of the column 'RESPONSE_TIME'. (basically NotNull if update: for the constraint)
     */
    public void setResponseTime(final Integer responseTime) {
        __modifiedProperties.addPropertyName("responseTime");
        _responseTime = responseTime;
    }

    /**
     * [get] HIT_COUNT: {IX, NotNull, BIGINT(19)} <br />
     * @return The value of the column 'HIT_COUNT'. (basically NotNull if selected: for the constraint)
     */
    public Long getHitCount() {
        return _hitCount;
    }

    /**
     * [set] HIT_COUNT: {IX, NotNull, BIGINT(19)} <br />
     * @param hitCount The value of the column 'HIT_COUNT'. (basically NotNull if update: for the constraint)
     */
    public void setHitCount(final Long hitCount) {
        __modifiedProperties.addPropertyName("hitCount");
        _hitCount = hitCount;
    }

    /**
     * [get] QUERY_OFFSET: {NotNull, INTEGER(10)} <br />
     * @return The value of the column 'QUERY_OFFSET'. (basically NotNull if selected: for the constraint)
     */
    public Integer getQueryOffset() {
        return _queryOffset;
    }

    /**
     * [set] QUERY_OFFSET: {NotNull, INTEGER(10)} <br />
     * @param queryOffset The value of the column 'QUERY_OFFSET'. (basically NotNull if update: for the constraint)
     */
    public void setQueryOffset(final Integer queryOffset) {
        __modifiedProperties.addPropertyName("queryOffset");
        _queryOffset = queryOffset;
    }

    /**
     * [get] QUERY_PAGE_SIZE: {NotNull, INTEGER(10)} <br />
     * @return The value of the column 'QUERY_PAGE_SIZE'. (basically NotNull if selected: for the constraint)
     */
    public Integer getQueryPageSize() {
        return _queryPageSize;
    }

    /**
     * [set] QUERY_PAGE_SIZE: {NotNull, INTEGER(10)} <br />
     * @param queryPageSize The value of the column 'QUERY_PAGE_SIZE'. (basically NotNull if update: for the constraint)
     */
    public void setQueryPageSize(final Integer queryPageSize) {
        __modifiedProperties.addPropertyName("queryPageSize");
        _queryPageSize = queryPageSize;
    }

    /**
     * [get] USER_AGENT: {VARCHAR(255)} <br />
     * @return The value of the column 'USER_AGENT'. (NullAllowed even if selected: for no constraint)
     */
    public String getUserAgent() {
        return _userAgent;
    }

    /**
     * [set] USER_AGENT: {VARCHAR(255)} <br />
     * @param userAgent The value of the column 'USER_AGENT'. (NullAllowed: null update allowed for no constraint)
     */
    public void setUserAgent(final String userAgent) {
        __modifiedProperties.addPropertyName("userAgent");
        _userAgent = userAgent;
    }

    /**
     * [get] REFERER: {VARCHAR(1000)} <br />
     * @return The value of the column 'REFERER'. (NullAllowed even if selected: for no constraint)
     */
    public String getReferer() {
        return _referer;
    }

    /**
     * [set] REFERER: {VARCHAR(1000)} <br />
     * @param referer The value of the column 'REFERER'. (NullAllowed: null update allowed for no constraint)
     */
    public void setReferer(final String referer) {
        __modifiedProperties.addPropertyName("referer");
        _referer = referer;
    }

    /**
     * [get] CLIENT_IP: {VARCHAR(50)} <br />
     * @return The value of the column 'CLIENT_IP'. (NullAllowed even if selected: for no constraint)
     */
    public String getClientIp() {
        return _clientIp;
    }

    /**
     * [set] CLIENT_IP: {VARCHAR(50)} <br />
     * @param clientIp The value of the column 'CLIENT_IP'. (NullAllowed: null update allowed for no constraint)
     */
    public void setClientIp(final String clientIp) {
        __modifiedProperties.addPropertyName("clientIp");
        _clientIp = clientIp;
    }

    /**
     * [get] USER_SESSION_ID: {IX+, VARCHAR(100)} <br />
     * @return The value of the column 'USER_SESSION_ID'. (NullAllowed even if selected: for no constraint)
     */
    public String getUserSessionId() {
        return _userSessionId;
    }

    /**
     * [set] USER_SESSION_ID: {IX+, VARCHAR(100)} <br />
     * @param userSessionId The value of the column 'USER_SESSION_ID'. (NullAllowed: null update allowed for no constraint)
     */
    public void setUserSessionId(final String userSessionId) {
        __modifiedProperties.addPropertyName("userSessionId");
        _userSessionId = userSessionId;
    }

    /**
     * [get] ACCESS_TYPE: {NotNull, VARCHAR(1), classification=AccessType} <br />
     * @return The value of the column 'ACCESS_TYPE'. (basically NotNull if selected: for the constraint)
     */
    public String getAccessType() {
        return _accessType;
    }

    /**
     * [set] ACCESS_TYPE: {NotNull, VARCHAR(1), classification=AccessType} <br />
     * @param accessType The value of the column 'ACCESS_TYPE'. (basically NotNull if update: for the constraint)
     */
    public void setAccessType(final String accessType) {
        __modifiedProperties.addPropertyName("accessType");
        _accessType = accessType;
    }

    /**
     * [get] USER_ID: {IX, BIGINT(19), FK to USER_INFO} <br />
     * @return The value of the column 'USER_ID'. (NullAllowed even if selected: for no constraint)
     */
    public Long getUserId() {
        return _userId;
    }

    /**
     * [set] USER_ID: {IX, BIGINT(19), FK to USER_INFO} <br />
     * @param userId The value of the column 'USER_ID'. (NullAllowed: null update allowed for no constraint)
     */
    public void setUserId(final Long userId) {
        __modifiedProperties.addPropertyName("userId");
        _userId = userId;
    }
}
