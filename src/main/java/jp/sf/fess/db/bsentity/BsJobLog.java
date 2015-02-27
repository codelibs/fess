/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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
import jp.sf.fess.db.exentity.JobLog;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;

/**
 * The entity of JOB_LOG as TABLE. <br />
 * <pre>
 * [primary-key]
 *     ID
 *
 * [column]
 *     ID, JOB_NAME, JOB_STATUS, TARGET, SCRIPT_TYPE, SCRIPT_DATA, SCRIPT_RESULT, START_TIME, END_TIME
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
 *
 *
 * [foreign property]
 *
 *
 * [referrer property]
 *
 *
 * [get/set template]
 * /= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
 * Long id = entity.getId();
 * String jobName = entity.getJobName();
 * String jobStatus = entity.getJobStatus();
 * String target = entity.getTarget();
 * String scriptType = entity.getScriptType();
 * String scriptData = entity.getScriptData();
 * String scriptResult = entity.getScriptResult();
 * java.sql.Timestamp startTime = entity.getStartTime();
 * java.sql.Timestamp endTime = entity.getEndTime();
 * entity.setId(id);
 * entity.setJobName(jobName);
 * entity.setJobStatus(jobStatus);
 * entity.setTarget(target);
 * entity.setScriptType(scriptType);
 * entity.setScriptData(scriptData);
 * entity.setScriptResult(scriptResult);
 * entity.setStartTime(startTime);
 * entity.setEndTime(endTime);
 * = = = = = = = = = =/
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsJobLog implements Entity, Serializable, Cloneable {

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

    /** JOB_NAME: {NotNull, VARCHAR(100)} */
    protected String _jobName;

    /** JOB_STATUS: {NotNull, VARCHAR(10)} */
    protected String _jobStatus;

    /** TARGET: {NotNull, VARCHAR(100)} */
    protected String _target;

    /** SCRIPT_TYPE: {NotNull, VARCHAR(100)} */
    protected String _scriptType;

    /** SCRIPT_DATA: {VARCHAR(4000)} */
    protected String _scriptData;

    /** SCRIPT_RESULT: {VARCHAR(4000)} */
    protected String _scriptResult;

    /** START_TIME: {NotNull, TIMESTAMP(23, 10)} */
    protected java.sql.Timestamp _startTime;

    /** END_TIME: {TIMESTAMP(23, 10)} */
    protected java.sql.Timestamp _endTime;

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
        return "JOB_LOG";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTablePropertyName() { // according to Java Beans rule
        return "jobLog";
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
        if (obj == null || !(obj instanceof BsJobLog)) {
            return false;
        }
        final BsJobLog other = (BsJobLog) obj;
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
        return sb.toString();
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
        sb.append(dm).append(getJobName());
        sb.append(dm).append(getJobStatus());
        sb.append(dm).append(getTarget());
        sb.append(dm).append(getScriptType());
        sb.append(dm).append(getScriptData());
        sb.append(dm).append(getScriptResult());
        sb.append(dm).append(getStartTime());
        sb.append(dm).append(getEndTime());
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    protected String buildRelationString() {
        return "";
    }

    /**
     * Clone entity instance using super.clone(). (shallow copy)
     * @return The cloned instance of this entity. (NotNull)
     */
    @Override
    public JobLog clone() {
        try {
            return (JobLog) super.clone();
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
     * [get] JOB_NAME: {NotNull, VARCHAR(100)} <br />
     * @return The value of the column 'JOB_NAME'. (basically NotNull if selected: for the constraint)
     */
    public String getJobName() {
        return _jobName;
    }

    /**
     * [set] JOB_NAME: {NotNull, VARCHAR(100)} <br />
     * @param jobName The value of the column 'JOB_NAME'. (basically NotNull if update: for the constraint)
     */
    public void setJobName(final String jobName) {
        __modifiedProperties.addPropertyName("jobName");
        _jobName = jobName;
    }

    /**
     * [get] JOB_STATUS: {NotNull, VARCHAR(10)} <br />
     * @return The value of the column 'JOB_STATUS'. (basically NotNull if selected: for the constraint)
     */
    public String getJobStatus() {
        return _jobStatus;
    }

    /**
     * [set] JOB_STATUS: {NotNull, VARCHAR(10)} <br />
     * @param jobStatus The value of the column 'JOB_STATUS'. (basically NotNull if update: for the constraint)
     */
    public void setJobStatus(final String jobStatus) {
        __modifiedProperties.addPropertyName("jobStatus");
        _jobStatus = jobStatus;
    }

    /**
     * [get] TARGET: {NotNull, VARCHAR(100)} <br />
     * @return The value of the column 'TARGET'. (basically NotNull if selected: for the constraint)
     */
    public String getTarget() {
        return _target;
    }

    /**
     * [set] TARGET: {NotNull, VARCHAR(100)} <br />
     * @param target The value of the column 'TARGET'. (basically NotNull if update: for the constraint)
     */
    public void setTarget(final String target) {
        __modifiedProperties.addPropertyName("target");
        _target = target;
    }

    /**
     * [get] SCRIPT_TYPE: {NotNull, VARCHAR(100)} <br />
     * @return The value of the column 'SCRIPT_TYPE'. (basically NotNull if selected: for the constraint)
     */
    public String getScriptType() {
        return _scriptType;
    }

    /**
     * [set] SCRIPT_TYPE: {NotNull, VARCHAR(100)} <br />
     * @param scriptType The value of the column 'SCRIPT_TYPE'. (basically NotNull if update: for the constraint)
     */
    public void setScriptType(final String scriptType) {
        __modifiedProperties.addPropertyName("scriptType");
        _scriptType = scriptType;
    }

    /**
     * [get] SCRIPT_DATA: {VARCHAR(4000)} <br />
     * @return The value of the column 'SCRIPT_DATA'. (NullAllowed even if selected: for no constraint)
     */
    public String getScriptData() {
        return _scriptData;
    }

    /**
     * [set] SCRIPT_DATA: {VARCHAR(4000)} <br />
     * @param scriptData The value of the column 'SCRIPT_DATA'. (NullAllowed: null update allowed for no constraint)
     */
    public void setScriptData(final String scriptData) {
        __modifiedProperties.addPropertyName("scriptData");
        _scriptData = scriptData;
    }

    /**
     * [get] SCRIPT_RESULT: {VARCHAR(4000)} <br />
     * @return The value of the column 'SCRIPT_RESULT'. (NullAllowed even if selected: for no constraint)
     */
    public String getScriptResult() {
        return _scriptResult;
    }

    /**
     * [set] SCRIPT_RESULT: {VARCHAR(4000)} <br />
     * @param scriptResult The value of the column 'SCRIPT_RESULT'. (NullAllowed: null update allowed for no constraint)
     */
    public void setScriptResult(final String scriptResult) {
        __modifiedProperties.addPropertyName("scriptResult");
        _scriptResult = scriptResult;
    }

    /**
     * [get] START_TIME: {NotNull, TIMESTAMP(23, 10)} <br />
     * @return The value of the column 'START_TIME'. (basically NotNull if selected: for the constraint)
     */
    public java.sql.Timestamp getStartTime() {
        return _startTime;
    }

    /**
     * [set] START_TIME: {NotNull, TIMESTAMP(23, 10)} <br />
     * @param startTime The value of the column 'START_TIME'. (basically NotNull if update: for the constraint)
     */
    public void setStartTime(final java.sql.Timestamp startTime) {
        __modifiedProperties.addPropertyName("startTime");
        _startTime = startTime;
    }

    /**
     * [get] END_TIME: {TIMESTAMP(23, 10)} <br />
     * @return The value of the column 'END_TIME'. (NullAllowed even if selected: for no constraint)
     */
    public java.sql.Timestamp getEndTime() {
        return _endTime;
    }

    /**
     * [set] END_TIME: {TIMESTAMP(23, 10)} <br />
     * @param endTime The value of the column 'END_TIME'. (NullAllowed: null update allowed for no constraint)
     */
    public void setEndTime(final java.sql.Timestamp endTime) {
        __modifiedProperties.addPropertyName("endTime");
        _endTime = endTime;
    }
}
