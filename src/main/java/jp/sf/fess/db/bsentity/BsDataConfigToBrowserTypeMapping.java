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
import jp.sf.fess.db.exentity.BrowserType;
import jp.sf.fess.db.exentity.DataConfigToBrowserTypeMapping;
import jp.sf.fess.db.exentity.DataCrawlingConfig;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;

/**
 * The entity of DATA_CONFIG_TO_BROWSER_TYPE_MAPPING as TABLE. <br />
 * <pre>
 * [primary-key]
 *     ID
 * 
 * [column]
 *     ID, DATA_CONFIG_ID, BROWSER_TYPE_ID
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
 *     DATA_CRAWLING_CONFIG, BROWSER_TYPE
 * 
 * [referrer table]
 *     
 * 
 * [foreign property]
 *     dataCrawlingConfig, browserType
 * 
 * [referrer property]
 *     
 * 
 * [get/set template]
 * /= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
 * Long id = entity.getId();
 * Long dataConfigId = entity.getDataConfigId();
 * Long browserTypeId = entity.getBrowserTypeId();
 * entity.setId(id);
 * entity.setDataConfigId(dataConfigId);
 * entity.setBrowserTypeId(browserTypeId);
 * = = = = = = = = = =/
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsDataConfigToBrowserTypeMapping implements Entity,
        Serializable, Cloneable {

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

    /** DATA_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to DATA_CRAWLING_CONFIG} */
    protected Long _dataConfigId;

    /** BROWSER_TYPE_ID: {IX, NotNull, BIGINT(19), FK to BROWSER_TYPE} */
    protected Long _browserTypeId;

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
        return "DATA_CONFIG_TO_BROWSER_TYPE_MAPPING";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTablePropertyName() { // according to Java Beans rule
        return "dataConfigToBrowserTypeMapping";
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
    /** DATA_CRAWLING_CONFIG by my DATA_CONFIG_ID, named 'dataCrawlingConfig'. */
    protected DataCrawlingConfig _dataCrawlingConfig;

    /**
     * DATA_CRAWLING_CONFIG by my DATA_CONFIG_ID, named 'dataCrawlingConfig'.
     * @return The entity of foreign property 'dataCrawlingConfig'. (NullAllowed: when e.g. null FK column, no setupSelect)
     */
    public DataCrawlingConfig getDataCrawlingConfig() {
        return _dataCrawlingConfig;
    }

    /**
     * DATA_CRAWLING_CONFIG by my DATA_CONFIG_ID, named 'dataCrawlingConfig'.
     * @param dataCrawlingConfig The entity of foreign property 'dataCrawlingConfig'. (NullAllowed)
     */
    public void setDataCrawlingConfig(
            final DataCrawlingConfig dataCrawlingConfig) {
        _dataCrawlingConfig = dataCrawlingConfig;
    }

    /** BROWSER_TYPE by my BROWSER_TYPE_ID, named 'browserType'. */
    protected BrowserType _browserType;

    /**
     * BROWSER_TYPE by my BROWSER_TYPE_ID, named 'browserType'.
     * @return The entity of foreign property 'browserType'. (NullAllowed: when e.g. null FK column, no setupSelect)
     */
    public BrowserType getBrowserType() {
        return _browserType;
    }

    /**
     * BROWSER_TYPE by my BROWSER_TYPE_ID, named 'browserType'.
     * @param browserType The entity of foreign property 'browserType'. (NullAllowed)
     */
    public void setBrowserType(final BrowserType browserType) {
        _browserType = browserType;
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
        if (other == null
                || !(other instanceof BsDataConfigToBrowserTypeMapping)) {
            return false;
        }
        final BsDataConfigToBrowserTypeMapping otherEntity = (BsDataConfigToBrowserTypeMapping) other;
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
        if (_dataCrawlingConfig != null) {
            sb.append(l).append(
                    xbRDS(_dataCrawlingConfig, "dataCrawlingConfig"));
        }
        if (_browserType != null) {
            sb.append(l).append(xbRDS(_browserType, "browserType"));
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
        sb.append(delimiter).append(getDataConfigId());
        sb.append(delimiter).append(getBrowserTypeId());
        if (sb.length() > delimiter.length()) {
            sb.delete(0, delimiter.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    protected String buildRelationString() {
        final StringBuilder sb = new StringBuilder();
        final String c = ",";
        if (_dataCrawlingConfig != null) {
            sb.append(c).append("dataCrawlingConfig");
        }
        if (_browserType != null) {
            sb.append(c).append("browserType");
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
    public DataConfigToBrowserTypeMapping clone() {
        try {
            return (DataConfigToBrowserTypeMapping) super.clone();
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
     * [get] DATA_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to DATA_CRAWLING_CONFIG} <br />
     * @return The value of the column 'DATA_CONFIG_ID'. (basically NotNull if selected: for the constraint)
     */
    public Long getDataConfigId() {
        return _dataConfigId;
    }

    /**
     * [set] DATA_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to DATA_CRAWLING_CONFIG} <br />
     * @param dataConfigId The value of the column 'DATA_CONFIG_ID'. (basically NotNull if update: for the constraint)
     */
    public void setDataConfigId(final Long dataConfigId) {
        __modifiedProperties.addPropertyName("dataConfigId");
        _dataConfigId = dataConfigId;
    }

    /**
     * [get] BROWSER_TYPE_ID: {IX, NotNull, BIGINT(19), FK to BROWSER_TYPE} <br />
     * @return The value of the column 'BROWSER_TYPE_ID'. (basically NotNull if selected: for the constraint)
     */
    public Long getBrowserTypeId() {
        return _browserTypeId;
    }

    /**
     * [set] BROWSER_TYPE_ID: {IX, NotNull, BIGINT(19), FK to BROWSER_TYPE} <br />
     * @param browserTypeId The value of the column 'BROWSER_TYPE_ID'. (basically NotNull if update: for the constraint)
     */
    public void setBrowserTypeId(final Long browserTypeId) {
        __modifiedProperties.addPropertyName("browserTypeId");
        _browserTypeId = browserTypeId;
    }
}
