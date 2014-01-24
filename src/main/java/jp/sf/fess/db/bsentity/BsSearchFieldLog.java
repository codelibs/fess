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
import jp.sf.fess.db.exentity.SearchFieldLog;
import jp.sf.fess.db.exentity.SearchLog;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;

/**
 * The entity of SEARCH_FIELD_LOG as TABLE. <br />
 * <pre>
 * [primary-key]
 *     ID
 * 
 * [column]
 *     ID, SEARCH_ID, NAME, VALUE
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
 *     SEARCH_LOG
 * 
 * [referrer table]
 *     
 * 
 * [foreign property]
 *     searchLog
 * 
 * [referrer property]
 *     
 * 
 * [get/set template]
 * /= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
 * Long id = entity.getId();
 * Long searchId = entity.getSearchId();
 * String name = entity.getName();
 * String value = entity.getValue();
 * entity.setId(id);
 * entity.setSearchId(searchId);
 * entity.setName(name);
 * entity.setValue(value);
 * = = = = = = = = = =/
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsSearchFieldLog implements Entity, Serializable,
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

    /** SEARCH_ID: {IX, NotNull, BIGINT(19), FK to SEARCH_LOG} */
    protected Long _searchId;

    /** NAME: {IX, NotNull, VARCHAR(255)} */
    protected String _name;

    /** VALUE: {NotNull, VARCHAR(1000)} */
    protected String _value;

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
        return "SEARCH_FIELD_LOG";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTablePropertyName() { // according to Java Beans rule
        return "searchFieldLog";
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
    /** SEARCH_LOG by my SEARCH_ID, named 'searchLog'. */
    protected SearchLog _searchLog;

    /**
     * SEARCH_LOG by my SEARCH_ID, named 'searchLog'.
     * @return The entity of foreign property 'searchLog'. (NullAllowed: when e.g. null FK column, no setupSelect)
     */
    public SearchLog getSearchLog() {
        return _searchLog;
    }

    /**
     * SEARCH_LOG by my SEARCH_ID, named 'searchLog'.
     * @param searchLog The entity of foreign property 'searchLog'. (NullAllowed)
     */
    public void setSearchLog(final SearchLog searchLog) {
        _searchLog = searchLog;
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
        if (other == null || !(other instanceof BsSearchFieldLog)) {
            return false;
        }
        final BsSearchFieldLog otherEntity = (BsSearchFieldLog) other;
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
        if (_searchLog != null) {
            sb.append(l).append(xbRDS(_searchLog, "searchLog"));
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
        sb.append(delimiter).append(getSearchId());
        sb.append(delimiter).append(getName());
        sb.append(delimiter).append(getValue());
        if (sb.length() > delimiter.length()) {
            sb.delete(0, delimiter.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    protected String buildRelationString() {
        final StringBuilder sb = new StringBuilder();
        final String c = ",";
        if (_searchLog != null) {
            sb.append(c).append("searchLog");
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
    public SearchFieldLog clone() {
        try {
            return (SearchFieldLog) super.clone();
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
     * [get] SEARCH_ID: {IX, NotNull, BIGINT(19), FK to SEARCH_LOG} <br />
     * @return The value of the column 'SEARCH_ID'. (basically NotNull if selected: for the constraint)
     */
    public Long getSearchId() {
        return _searchId;
    }

    /**
     * [set] SEARCH_ID: {IX, NotNull, BIGINT(19), FK to SEARCH_LOG} <br />
     * @param searchId The value of the column 'SEARCH_ID'. (basically NotNull if update: for the constraint)
     */
    public void setSearchId(final Long searchId) {
        __modifiedProperties.addPropertyName("searchId");
        _searchId = searchId;
    }

    /**
     * [get] NAME: {IX, NotNull, VARCHAR(255)} <br />
     * @return The value of the column 'NAME'. (basically NotNull if selected: for the constraint)
     */
    public String getName() {
        return _name;
    }

    /**
     * [set] NAME: {IX, NotNull, VARCHAR(255)} <br />
     * @param name The value of the column 'NAME'. (basically NotNull if update: for the constraint)
     */
    public void setName(final String name) {
        __modifiedProperties.addPropertyName("name");
        _name = name;
    }

    /**
     * [get] VALUE: {NotNull, VARCHAR(1000)} <br />
     * @return The value of the column 'VALUE'. (basically NotNull if selected: for the constraint)
     */
    public String getValue() {
        return _value;
    }

    /**
     * [set] VALUE: {NotNull, VARCHAR(1000)} <br />
     * @param value The value of the column 'VALUE'. (basically NotNull if update: for the constraint)
     */
    public void setValue(final String value) {
        __modifiedProperties.addPropertyName("value");
        _value = value;
    }
}
