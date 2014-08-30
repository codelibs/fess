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
import jp.sf.fess.db.exentity.FavoriteLog;
import jp.sf.fess.db.exentity.SearchLog;
import jp.sf.fess.db.exentity.UserInfo;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;

/**
 * The entity of USER_INFO as TABLE. <br />
 * <pre>
 * [primary-key]
 *     ID
 *
 * [column]
 *     ID, CODE, CREATED_TIME, UPDATED_TIME
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
 *
 *
 * [referrer table]
 *     FAVORITE_LOG, SEARCH_LOG
 *
 * [foreign property]
 *
 *
 * [referrer property]
 *     favoriteLogList, searchLogList
 *
 * [get/set template]
 * /= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
 * Long id = entity.getId();
 * String code = entity.getCode();
 * java.sql.Timestamp createdTime = entity.getCreatedTime();
 * java.sql.Timestamp updatedTime = entity.getUpdatedTime();
 * entity.setId(id);
 * entity.setCode(code);
 * entity.setCreatedTime(createdTime);
 * entity.setUpdatedTime(updatedTime);
 * = = = = = = = = = =/
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsUserInfo implements Entity, Serializable, Cloneable {

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

    /** CODE: {NotNull, VARCHAR(1000)} */
    protected String _code;

    /** CREATED_TIME: {NotNull, TIMESTAMP(23, 10)} */
    protected java.sql.Timestamp _createdTime;

    /** UPDATED_TIME: {NotNull, TIMESTAMP(23, 10)} */
    protected java.sql.Timestamp _updatedTime;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    /** The unique-driven properties for this entity. (NotNull) */
    protected final EntityUniqueDrivenProperties __uniqueDrivenProperties = newUniqueDrivenProperties();

    /** The modified properties for this entity. (NotNull) */
    protected final EntityModifiedProperties __modifiedProperties = newModifiedProperties();

    /** Is the entity created by DBFlute select process? */
    protected boolean __createdBySelect;

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    /**
     * {@inheritDoc}
     */
    @Override
    public String getTableDbName() {
        return "USER_INFO";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTablePropertyName() { // according to Java Beans rule
        return "userInfo";
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> myuniqueDrivenProperties() {
        return __uniqueDrivenProperties.getPropertyNames();
    }

    protected EntityUniqueDrivenProperties newUniqueDrivenProperties() {
        return new EntityUniqueDrivenProperties();
    }

    // ===================================================================================
    //                                                                    Foreign Property
    //                                                                    ================
    // ===================================================================================
    //                                                                   Referrer Property
    //                                                                   =================
    /** FAVORITE_LOG by USER_ID, named 'favoriteLogList'. */
    protected List<FavoriteLog> _favoriteLogList;

    /**
     * [get] FAVORITE_LOG by USER_ID, named 'favoriteLogList'.
     * @return The entity list of referrer property 'favoriteLogList'. (NotNull: even if no loading, returns empty list)
     */
    public List<FavoriteLog> getFavoriteLogList() {
        if (_favoriteLogList == null) {
            _favoriteLogList = newReferrerList();
        }
        return _favoriteLogList;
    }

    /**
     * [set] FAVORITE_LOG by USER_ID, named 'favoriteLogList'.
     * @param favoriteLogList The entity list of referrer property 'favoriteLogList'. (NullAllowed)
     */
    public void setFavoriteLogList(final List<FavoriteLog> favoriteLogList) {
        _favoriteLogList = favoriteLogList;
    }

    /** SEARCH_LOG by USER_ID, named 'searchLogList'. */
    protected List<SearchLog> _searchLogList;

    /**
     * [get] SEARCH_LOG by USER_ID, named 'searchLogList'.
     * @return The entity list of referrer property 'searchLogList'. (NotNull: even if no loading, returns empty list)
     */
    public List<SearchLog> getSearchLogList() {
        if (_searchLogList == null) {
            _searchLogList = newReferrerList();
        }
        return _searchLogList;
    }

    /**
     * [set] SEARCH_LOG by USER_ID, named 'searchLogList'.
     * @param searchLogList The entity list of referrer property 'searchLogList'. (NullAllowed)
     */
    public void setSearchLogList(final List<SearchLog> searchLogList) {
        _searchLogList = searchLogList;
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
    //                                                                     Birthplace Mark
    //                                                                     ===============
    /**
     * {@inheritDoc}
     */
    @Override
    public void markAsSelect() {
        __createdBySelect = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createdBySelect() {
        return __createdBySelect;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * Determine the object is equal with this. <br />
     * If primary-keys or columns of the other are same as this one, returns true.
     * @param obj The object as other entity. (NullAllowed: if null, returns false fixedly)
     * @return Comparing result.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof BsUserInfo)) {
            return false;
        }
        final BsUserInfo other = (BsUserInfo) obj;
        if (!xSV(getId(), other.getId())) {
            return false;
        }
        return true;
    }

    protected boolean xSV(final Object v1, final Object v2) {
        return FunCustodial.isSameValue(v1, v2);
    }

    /**
     * Calculate the hash-code from primary-keys or columns.
     * @return The hash-code from primary-key or columns.
     */
    @Override
    public int hashCode() {
        int hs = 17;
        hs = xCH(hs, getTableDbName());
        hs = xCH(hs, getId());
        return hs;
    }

    protected int xCH(final int hs, final Object vl) {
        return FunCustodial.calculateHashcode(hs, vl);
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
        return buildDisplayString(FunCustodial.toClassTitle(this), true, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toStringWithRelation() {
        final StringBuilder sb = new StringBuilder();
        sb.append(toString());
        final String li = "\n  ";
        if (_favoriteLogList != null) {
            for (final Entity et : _favoriteLogList) {
                if (et != null) {
                    sb.append(li).append(xbRDS(et, "favoriteLogList"));
                }
            }
        }
        if (_searchLogList != null) {
            for (final Entity et : _searchLogList) {
                if (et != null) {
                    sb.append(li).append(xbRDS(et, "searchLogList"));
                }
            }
        }
        return sb.toString();
    }

    protected String xbRDS(final Entity et, final String name) { // buildRelationDisplayString()
        return et.buildDisplayString(name, true, true);
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
        final String dm = ", ";
        sb.append(dm).append(getId());
        sb.append(dm).append(getCode());
        sb.append(dm).append(getCreatedTime());
        sb.append(dm).append(getUpdatedTime());
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    protected String buildRelationString() {
        final StringBuilder sb = new StringBuilder();
        final String cm = ",";
        if (_favoriteLogList != null && !_favoriteLogList.isEmpty()) {
            sb.append(cm).append("favoriteLogList");
        }
        if (_searchLogList != null && !_searchLogList.isEmpty()) {
            sb.append(cm).append("searchLogList");
        }
        if (sb.length() > cm.length()) {
            sb.delete(0, cm.length()).insert(0, "(").append(")");
        }
        return sb.toString();
    }

    /**
     * Clone entity instance using super.clone(). (shallow copy)
     * @return The cloned instance of this entity. (NotNull)
     */
    @Override
    public UserInfo clone() {
        try {
            return (UserInfo) super.clone();
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
     * [get] CODE: {NotNull, VARCHAR(1000)} <br />
     * @return The value of the column 'CODE'. (basically NotNull if selected: for the constraint)
     */
    public String getCode() {
        return _code;
    }

    /**
     * [set] CODE: {NotNull, VARCHAR(1000)} <br />
     * @param code The value of the column 'CODE'. (basically NotNull if update: for the constraint)
     */
    public void setCode(final String code) {
        __modifiedProperties.addPropertyName("code");
        _code = code;
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
     * [get] UPDATED_TIME: {NotNull, TIMESTAMP(23, 10)} <br />
     * @return The value of the column 'UPDATED_TIME'. (basically NotNull if selected: for the constraint)
     */
    public java.sql.Timestamp getUpdatedTime() {
        return _updatedTime;
    }

    /**
     * [set] UPDATED_TIME: {NotNull, TIMESTAMP(23, 10)} <br />
     * @param updatedTime The value of the column 'UPDATED_TIME'. (basically NotNull if update: for the constraint)
     */
    public void setUpdatedTime(final java.sql.Timestamp updatedTime) {
        __modifiedProperties.addPropertyName("updatedTime");
        _updatedTime = updatedTime;
    }
}
