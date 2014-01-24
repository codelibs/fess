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

import jp.sf.fess.db.allcommon.DBMetaInstanceHandler;
import jp.sf.fess.db.exentity.CrawlingSession;
import jp.sf.fess.db.exentity.CrawlingSessionInfo;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;

/**
 * The entity of CRAWLING_SESSION_INFO as TABLE. <br />
 * <pre>
 * [primary-key]
 *     ID
 * 
 * [column]
 *     ID, CRAWLING_SESSION_ID, KEY, VALUE, CREATED_TIME
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
 *     CRAWLING_SESSION
 * 
 * [referrer table]
 *     
 * 
 * [foreign property]
 *     crawlingSession
 * 
 * [referrer property]
 *     
 * 
 * [get/set template]
 * /= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
 * Long id = entity.getId();
 * Long crawlingSessionId = entity.getCrawlingSessionId();
 * String key = entity.getKey();
 * String value = entity.getValue();
 * java.sql.Timestamp createdTime = entity.getCreatedTime();
 * entity.setId(id);
 * entity.setCrawlingSessionId(crawlingSessionId);
 * entity.setKey(key);
 * entity.setValue(value);
 * entity.setCreatedTime(createdTime);
 * = = = = = = = = = =/
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsCrawlingSessionInfo implements Entity, Serializable,
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

    /** CRAWLING_SESSION_ID: {IX, NotNull, BIGINT(19), FK to CRAWLING_SESSION} */
    protected Long _crawlingSessionId;

    /** KEY: {NotNull, VARCHAR(20)} */
    protected String _key;

    /** VALUE: {NotNull, VARCHAR(100)} */
    protected String _value;

    /** CREATED_TIME: {NotNull, TIMESTAMP(23, 10)} */
    protected java.sql.Timestamp _createdTime;

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
        return "CRAWLING_SESSION_INFO";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTablePropertyName() { // according to Java Beans rule
        return "crawlingSessionInfo";
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
    /** CRAWLING_SESSION by my CRAWLING_SESSION_ID, named 'crawlingSession'. */
    protected CrawlingSession _crawlingSession;

    /**
     * CRAWLING_SESSION by my CRAWLING_SESSION_ID, named 'crawlingSession'.
     * @return The entity of foreign property 'crawlingSession'. (NullAllowed: when e.g. null FK column, no setupSelect)
     */
    public CrawlingSession getCrawlingSession() {
        return _crawlingSession;
    }

    /**
     * CRAWLING_SESSION by my CRAWLING_SESSION_ID, named 'crawlingSession'.
     * @param crawlingSession The entity of foreign property 'crawlingSession'. (NullAllowed)
     */
    public void setCrawlingSession(final CrawlingSession crawlingSession) {
        _crawlingSession = crawlingSession;
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
        if (other == null || !(other instanceof BsCrawlingSessionInfo)) {
            return false;
        }
        final BsCrawlingSessionInfo otherEntity = (BsCrawlingSessionInfo) other;
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
        if (_crawlingSession != null) {
            sb.append(l).append(xbRDS(_crawlingSession, "crawlingSession"));
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
        sb.append(delimiter).append(getCrawlingSessionId());
        sb.append(delimiter).append(getKey());
        sb.append(delimiter).append(getValue());
        sb.append(delimiter).append(getCreatedTime());
        if (sb.length() > delimiter.length()) {
            sb.delete(0, delimiter.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    protected String buildRelationString() {
        final StringBuilder sb = new StringBuilder();
        final String c = ",";
        if (_crawlingSession != null) {
            sb.append(c).append("crawlingSession");
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
    public CrawlingSessionInfo clone() {
        try {
            return (CrawlingSessionInfo) super.clone();
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
     * [get] CRAWLING_SESSION_ID: {IX, NotNull, BIGINT(19), FK to CRAWLING_SESSION} <br />
     * @return The value of the column 'CRAWLING_SESSION_ID'. (basically NotNull if selected: for the constraint)
     */
    public Long getCrawlingSessionId() {
        return _crawlingSessionId;
    }

    /**
     * [set] CRAWLING_SESSION_ID: {IX, NotNull, BIGINT(19), FK to CRAWLING_SESSION} <br />
     * @param crawlingSessionId The value of the column 'CRAWLING_SESSION_ID'. (basically NotNull if update: for the constraint)
     */
    public void setCrawlingSessionId(final Long crawlingSessionId) {
        __modifiedProperties.addPropertyName("crawlingSessionId");
        _crawlingSessionId = crawlingSessionId;
    }

    /**
     * [get] KEY: {NotNull, VARCHAR(20)} <br />
     * @return The value of the column 'KEY'. (basically NotNull if selected: for the constraint)
     */
    public String getKey() {
        return _key;
    }

    /**
     * [set] KEY: {NotNull, VARCHAR(20)} <br />
     * @param key The value of the column 'KEY'. (basically NotNull if update: for the constraint)
     */
    public void setKey(final String key) {
        __modifiedProperties.addPropertyName("key");
        _key = key;
    }

    /**
     * [get] VALUE: {NotNull, VARCHAR(100)} <br />
     * @return The value of the column 'VALUE'. (basically NotNull if selected: for the constraint)
     */
    public String getValue() {
        return _value;
    }

    /**
     * [set] VALUE: {NotNull, VARCHAR(100)} <br />
     * @param value The value of the column 'VALUE'. (basically NotNull if update: for the constraint)
     */
    public void setValue(final String value) {
        __modifiedProperties.addPropertyName("value");
        _value = value;
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
}
