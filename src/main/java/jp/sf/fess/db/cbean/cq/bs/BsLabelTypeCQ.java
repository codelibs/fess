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

package jp.sf.fess.db.cbean.cq.bs;

import java.util.Map;

import jp.sf.fess.db.cbean.LabelTypeCB;
import jp.sf.fess.db.cbean.cq.DataConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.LabelTypeCQ;
import jp.sf.fess.db.cbean.cq.LabelTypeToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.ciq.LabelTypeCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.chelper.HpCalculator;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of LABEL_TYPE.
 * @author DBFlute(AutoGenerator)
 */
public class BsLabelTypeCQ extends AbstractBsLabelTypeCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected LabelTypeCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsLabelTypeCQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from LABEL_TYPE) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public LabelTypeCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected LabelTypeCIQ xcreateCIQ() {
        final LabelTypeCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected LabelTypeCIQ xnewCIQ() {
        return new LabelTypeCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join LABEL_TYPE on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public LabelTypeCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final LabelTypeCIQ inlineQuery = inline();
        inlineQuery.xsetOnClause(true);
        return inlineQuery;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    protected ConditionValue _id;

    public ConditionValue getId() {
        if (_id == null) {
            _id = nCV();
        }
        return _id;
    }

    @Override
    protected ConditionValue getCValueId() {
        return getId();
    }

    public Map<String, DataConfigToLabelTypeMappingCQ> getId_ExistsReferrer_DataConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_ExistsReferrer_DataConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_ExistsReferrer_DataConfigToLabelTypeMappingList(
            final DataConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue("id_ExistsReferrer_DataConfigToLabelTypeMappingList",
                sq);
    }

    public Map<String, FileConfigToLabelTypeMappingCQ> getId_ExistsReferrer_FileConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_ExistsReferrer_FileConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_ExistsReferrer_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue("id_ExistsReferrer_FileConfigToLabelTypeMappingList",
                sq);
    }

    public Map<String, LabelTypeToRoleTypeMappingCQ> getId_ExistsReferrer_LabelTypeToRoleTypeMappingList() {
        return xgetSQueMap("id_ExistsReferrer_LabelTypeToRoleTypeMappingList");
    }

    @Override
    public String keepId_ExistsReferrer_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ sq) {
        return xkeepSQue("id_ExistsReferrer_LabelTypeToRoleTypeMappingList", sq);
    }

    public Map<String, WebConfigToLabelTypeMappingCQ> getId_ExistsReferrer_WebConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_ExistsReferrer_WebConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_ExistsReferrer_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue("id_ExistsReferrer_WebConfigToLabelTypeMappingList",
                sq);
    }

    public Map<String, DataConfigToLabelTypeMappingCQ> getId_NotExistsReferrer_DataConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_NotExistsReferrer_DataConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_NotExistsReferrer_DataConfigToLabelTypeMappingList(
            final DataConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotExistsReferrer_DataConfigToLabelTypeMappingList", sq);
    }

    public Map<String, FileConfigToLabelTypeMappingCQ> getId_NotExistsReferrer_FileConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_NotExistsReferrer_FileConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_NotExistsReferrer_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotExistsReferrer_FileConfigToLabelTypeMappingList", sq);
    }

    public Map<String, LabelTypeToRoleTypeMappingCQ> getId_NotExistsReferrer_LabelTypeToRoleTypeMappingList() {
        return xgetSQueMap("id_NotExistsReferrer_LabelTypeToRoleTypeMappingList");
    }

    @Override
    public String keepId_NotExistsReferrer_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ sq) {
        return xkeepSQue("id_NotExistsReferrer_LabelTypeToRoleTypeMappingList",
                sq);
    }

    public Map<String, WebConfigToLabelTypeMappingCQ> getId_NotExistsReferrer_WebConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_NotExistsReferrer_WebConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_NotExistsReferrer_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotExistsReferrer_WebConfigToLabelTypeMappingList", sq);
    }

    public Map<String, DataConfigToLabelTypeMappingCQ> getId_SpecifyDerivedReferrer_DataConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_DataConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_DataConfigToLabelTypeMappingList(
            final DataConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_SpecifyDerivedReferrer_DataConfigToLabelTypeMappingList",
                sq);
    }

    public Map<String, FileConfigToLabelTypeMappingCQ> getId_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_SpecifyDerivedReferrer_FileConfigToLabelTypeMappingList",
                sq);
    }

    public Map<String, LabelTypeToRoleTypeMappingCQ> getId_SpecifyDerivedReferrer_LabelTypeToRoleTypeMappingList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_LabelTypeToRoleTypeMappingList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_SpecifyDerivedReferrer_LabelTypeToRoleTypeMappingList", sq);
    }

    public Map<String, WebConfigToLabelTypeMappingCQ> getId_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_SpecifyDerivedReferrer_WebConfigToLabelTypeMappingList", sq);
    }

    public Map<String, DataConfigToLabelTypeMappingCQ> getId_InScopeRelation_DataConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_InScopeRelation_DataConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_InScopeRelation_DataConfigToLabelTypeMappingList(
            final DataConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue("id_InScopeRelation_DataConfigToLabelTypeMappingList",
                sq);
    }

    public Map<String, FileConfigToLabelTypeMappingCQ> getId_InScopeRelation_FileConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_InScopeRelation_FileConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_InScopeRelation_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue("id_InScopeRelation_FileConfigToLabelTypeMappingList",
                sq);
    }

    public Map<String, LabelTypeToRoleTypeMappingCQ> getId_InScopeRelation_LabelTypeToRoleTypeMappingList() {
        return xgetSQueMap("id_InScopeRelation_LabelTypeToRoleTypeMappingList");
    }

    @Override
    public String keepId_InScopeRelation_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ sq) {
        return xkeepSQue("id_InScopeRelation_LabelTypeToRoleTypeMappingList",
                sq);
    }

    public Map<String, WebConfigToLabelTypeMappingCQ> getId_InScopeRelation_WebConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_InScopeRelation_WebConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_InScopeRelation_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue("id_InScopeRelation_WebConfigToLabelTypeMappingList",
                sq);
    }

    public Map<String, DataConfigToLabelTypeMappingCQ> getId_NotInScopeRelation_DataConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_NotInScopeRelation_DataConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_NotInScopeRelation_DataConfigToLabelTypeMappingList(
            final DataConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotInScopeRelation_DataConfigToLabelTypeMappingList", sq);
    }

    public Map<String, FileConfigToLabelTypeMappingCQ> getId_NotInScopeRelation_FileConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_NotInScopeRelation_FileConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_NotInScopeRelation_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotInScopeRelation_FileConfigToLabelTypeMappingList", sq);
    }

    public Map<String, LabelTypeToRoleTypeMappingCQ> getId_NotInScopeRelation_LabelTypeToRoleTypeMappingList() {
        return xgetSQueMap("id_NotInScopeRelation_LabelTypeToRoleTypeMappingList");
    }

    @Override
    public String keepId_NotInScopeRelation_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotInScopeRelation_LabelTypeToRoleTypeMappingList", sq);
    }

    public Map<String, WebConfigToLabelTypeMappingCQ> getId_NotInScopeRelation_WebConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_NotInScopeRelation_WebConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_NotInScopeRelation_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotInScopeRelation_WebConfigToLabelTypeMappingList", sq);
    }

    public Map<String, DataConfigToLabelTypeMappingCQ> getId_QueryDerivedReferrer_DataConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_QueryDerivedReferrer_DataConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_DataConfigToLabelTypeMappingList(
            final DataConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_QueryDerivedReferrer_DataConfigToLabelTypeMappingList", sq);
    }

    public Map<String, Object> getId_QueryDerivedReferrer_DataConfigToLabelTypeMappingListParameter() {
        return xgetSQuePmMap("id_QueryDerivedReferrer_DataConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_DataConfigToLabelTypeMappingListParameter(
            final Object pm) {
        return xkeepSQuePm(
                "id_QueryDerivedReferrer_DataConfigToLabelTypeMappingList", pm);
    }

    public Map<String, FileConfigToLabelTypeMappingCQ> getId_QueryDerivedReferrer_FileConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_QueryDerivedReferrer_FileConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToLabelTypeMappingList(
            final FileConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_QueryDerivedReferrer_FileConfigToLabelTypeMappingList", sq);
    }

    public Map<String, Object> getId_QueryDerivedReferrer_FileConfigToLabelTypeMappingListParameter() {
        return xgetSQuePmMap("id_QueryDerivedReferrer_FileConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToLabelTypeMappingListParameter(
            final Object pm) {
        return xkeepSQuePm(
                "id_QueryDerivedReferrer_FileConfigToLabelTypeMappingList", pm);
    }

    public Map<String, LabelTypeToRoleTypeMappingCQ> getId_QueryDerivedReferrer_LabelTypeToRoleTypeMappingList() {
        return xgetSQueMap("id_QueryDerivedReferrer_LabelTypeToRoleTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_LabelTypeToRoleTypeMappingList(
            final LabelTypeToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_QueryDerivedReferrer_LabelTypeToRoleTypeMappingList", sq);
    }

    public Map<String, Object> getId_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListParameter() {
        return xgetSQuePmMap("id_QueryDerivedReferrer_LabelTypeToRoleTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_LabelTypeToRoleTypeMappingListParameter(
            final Object pm) {
        return xkeepSQuePm(
                "id_QueryDerivedReferrer_LabelTypeToRoleTypeMappingList", pm);
    }

    public Map<String, WebConfigToLabelTypeMappingCQ> getId_QueryDerivedReferrer_WebConfigToLabelTypeMappingList() {
        return xgetSQueMap("id_QueryDerivedReferrer_WebConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToLabelTypeMappingList(
            final WebConfigToLabelTypeMappingCQ sq) {
        return xkeepSQue(
                "id_QueryDerivedReferrer_WebConfigToLabelTypeMappingList", sq);
    }

    public Map<String, Object> getId_QueryDerivedReferrer_WebConfigToLabelTypeMappingListParameter() {
        return xgetSQuePmMap("id_QueryDerivedReferrer_WebConfigToLabelTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToLabelTypeMappingListParameter(
            final Object pm) {
        return xkeepSQuePm(
                "id_QueryDerivedReferrer_WebConfigToLabelTypeMappingList", pm);
    }

    /**
     * Add order-by as ascend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _name;

    public ConditionValue getName() {
        if (_name == null) {
            _name = nCV();
        }
        return _name;
    }

    @Override
    protected ConditionValue getCValueName() {
        return getName();
    }

    /**
     * Add order-by as ascend. <br />
     * NAME: {NotNull, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_Name_Asc() {
        regOBA("NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * NAME: {NotNull, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_Name_Desc() {
        regOBD("NAME");
        return this;
    }

    protected ConditionValue _value;

    public ConditionValue getValue() {
        if (_value == null) {
            _value = nCV();
        }
        return _value;
    }

    @Override
    protected ConditionValue getCValueValue() {
        return getValue();
    }

    /**
     * Add order-by as ascend. <br />
     * VALUE: {NotNull, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_Value_Asc() {
        regOBA("VALUE");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * VALUE: {NotNull, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_Value_Desc() {
        regOBD("VALUE");
        return this;
    }

    protected ConditionValue _includedPaths;

    public ConditionValue getIncludedPaths() {
        if (_includedPaths == null) {
            _includedPaths = nCV();
        }
        return _includedPaths;
    }

    @Override
    protected ConditionValue getCValueIncludedPaths() {
        return getIncludedPaths();
    }

    /**
     * Add order-by as ascend. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_IncludedPaths_Asc() {
        regOBA("INCLUDED_PATHS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * INCLUDED_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_IncludedPaths_Desc() {
        regOBD("INCLUDED_PATHS");
        return this;
    }

    protected ConditionValue _excludedPaths;

    public ConditionValue getExcludedPaths() {
        if (_excludedPaths == null) {
            _excludedPaths = nCV();
        }
        return _excludedPaths;
    }

    @Override
    protected ConditionValue getCValueExcludedPaths() {
        return getExcludedPaths();
    }

    /**
     * Add order-by as ascend. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_ExcludedPaths_Asc() {
        regOBA("EXCLUDED_PATHS");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_ExcludedPaths_Desc() {
        regOBD("EXCLUDED_PATHS");
        return this;
    }

    protected ConditionValue _sortOrder;

    public ConditionValue getSortOrder() {
        if (_sortOrder == null) {
            _sortOrder = nCV();
        }
        return _sortOrder;
    }

    @Override
    protected ConditionValue getCValueSortOrder() {
        return getSortOrder();
    }

    /**
     * Add order-by as ascend. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_SortOrder_Asc() {
        regOBA("SORT_ORDER");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_SortOrder_Desc() {
        regOBD("SORT_ORDER");
        return this;
    }

    protected ConditionValue _createdBy;

    public ConditionValue getCreatedBy() {
        if (_createdBy == null) {
            _createdBy = nCV();
        }
        return _createdBy;
    }

    @Override
    protected ConditionValue getCValueCreatedBy() {
        return getCreatedBy();
    }

    /**
     * Add order-by as ascend. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_CreatedBy_Asc() {
        regOBA("CREATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_CreatedBy_Desc() {
        regOBD("CREATED_BY");
        return this;
    }

    protected ConditionValue _createdTime;

    public ConditionValue getCreatedTime() {
        if (_createdTime == null) {
            _createdTime = nCV();
        }
        return _createdTime;
    }

    @Override
    protected ConditionValue getCValueCreatedTime() {
        return getCreatedTime();
    }

    /**
     * Add order-by as ascend. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_CreatedTime_Asc() {
        regOBA("CREATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_CreatedTime_Desc() {
        regOBD("CREATED_TIME");
        return this;
    }

    protected ConditionValue _updatedBy;

    public ConditionValue getUpdatedBy() {
        if (_updatedBy == null) {
            _updatedBy = nCV();
        }
        return _updatedBy;
    }

    @Override
    protected ConditionValue getCValueUpdatedBy() {
        return getUpdatedBy();
    }

    /**
     * Add order-by as ascend. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("UPDATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_UpdatedBy_Desc() {
        regOBD("UPDATED_BY");
        return this;
    }

    protected ConditionValue _updatedTime;

    public ConditionValue getUpdatedTime() {
        if (_updatedTime == null) {
            _updatedTime = nCV();
        }
        return _updatedTime;
    }

    @Override
    protected ConditionValue getCValueUpdatedTime() {
        return getUpdatedTime();
    }

    /**
     * Add order-by as ascend. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("UPDATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_UpdatedTime_Desc() {
        regOBD("UPDATED_TIME");
        return this;
    }

    protected ConditionValue _deletedBy;

    public ConditionValue getDeletedBy() {
        if (_deletedBy == null) {
            _deletedBy = nCV();
        }
        return _deletedBy;
    }

    @Override
    protected ConditionValue getCValueDeletedBy() {
        return getDeletedBy();
    }

    /**
     * Add order-by as ascend. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_DeletedBy_Asc() {
        regOBA("DELETED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_DeletedBy_Desc() {
        regOBD("DELETED_BY");
        return this;
    }

    protected ConditionValue _deletedTime;

    public ConditionValue getDeletedTime() {
        if (_deletedTime == null) {
            _deletedTime = nCV();
        }
        return _deletedTime;
    }

    @Override
    protected ConditionValue getCValueDeletedTime() {
        return getDeletedTime();
    }

    /**
     * Add order-by as ascend. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_DeletedTime_Asc() {
        regOBA("DELETED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_DeletedTime_Desc() {
        regOBD("DELETED_TIME");
        return this;
    }

    protected ConditionValue _versionNo;

    public ConditionValue getVersionNo() {
        if (_versionNo == null) {
            _versionNo = nCV();
        }
        return _versionNo;
    }

    @Override
    protected ConditionValue getCValueVersionNo() {
        return getVersionNo();
    }

    /**
     * Add order-by as ascend. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_VersionNo_Asc() {
        regOBA("VERSION_NO");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addOrderBy_VersionNo_Desc() {
        regOBD("VERSION_NO");
        return this;
    }

    // ===================================================================================
    //                                                             SpecifiedDerivedOrderBy
    //                                                             =======================
    /**
     * Add order-by for specified derived column as ascend.
     * <pre>
     * cb.specify().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseDatetime();
     *     }
     * }, <span style="color: #DD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] asc</span>
     * cb.<span style="color: #DD4747">addSpecifiedDerivedOrderBy_Asc</span>(<span style="color: #DD4747">aliasName</span>);
     * </pre>
     * @param aliasName The alias name specified at (Specify)DerivedReferrer. (NotNull)
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addSpecifiedDerivedOrderBy_Asc(final String aliasName) {
        registerSpecifiedDerivedOrderBy_Asc(aliasName);
        return this;
    }

    /**
     * Add order-by for specified derived column as descend.
     * <pre>
     * cb.specify().derivedPurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseDatetime();
     *     }
     * }, <span style="color: #DD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] desc</span>
     * cb.<span style="color: #DD4747">addSpecifiedDerivedOrderBy_Desc</span>(<span style="color: #DD4747">aliasName</span>);
     * </pre>
     * @param aliasName The alias name specified at (Specify)DerivedReferrer. (NotNull)
     * @return this. (NotNull)
     */
    public BsLabelTypeCQ addSpecifiedDerivedOrderBy_Desc(final String aliasName) {
        registerSpecifiedDerivedOrderBy_Desc(aliasName);
        return this;
    }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    @Override
    public void reflectRelationOnUnionQuery(final ConditionQuery bqs,
            final ConditionQuery uqs) {
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    public Map<String, LabelTypeCQ> getScalarCondition() {
        return xgetSQueMap("scalarCondition");
    }

    @Override
    public String keepScalarCondition(final LabelTypeCQ sq) {
        return xkeepSQue("scalarCondition", sq);
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public Map<String, LabelTypeCQ> getSpecifyMyselfDerived() {
        return xgetSQueMap("specifyMyselfDerived");
    }

    @Override
    public String keepSpecifyMyselfDerived(final LabelTypeCQ sq) {
        return xkeepSQue("specifyMyselfDerived", sq);
    }

    public Map<String, LabelTypeCQ> getQueryMyselfDerived() {
        return xgetSQueMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerived(final LabelTypeCQ sq) {
        return xkeepSQue("queryMyselfDerived", sq);
    }

    public Map<String, Object> getQueryMyselfDerivedParameter() {
        return xgetSQuePmMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerivedParameter(final Object pm) {
        return xkeepSQuePm("queryMyselfDerived", pm);
    }

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    protected Map<String, LabelTypeCQ> _myselfExistsMap;

    public Map<String, LabelTypeCQ> getMyselfExists() {
        return xgetSQueMap("myselfExists");
    }

    @Override
    public String keepMyselfExists(final LabelTypeCQ sq) {
        return xkeepSQue("myselfExists", sq);
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    public Map<String, LabelTypeCQ> getMyselfInScope() {
        return xgetSQueMap("myselfInScope");
    }

    @Override
    public String keepMyselfInScope(final LabelTypeCQ sq) {
        return xkeepSQue("myselfInScope", sq);
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() {
        return LabelTypeCB.class.getName();
    }

    protected String xCQ() {
        return LabelTypeCQ.class.getName();
    }

    protected String xCHp() {
        return HpCalculator.class.getName();
    }

    protected String xCOp() {
        return ConditionOption.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
