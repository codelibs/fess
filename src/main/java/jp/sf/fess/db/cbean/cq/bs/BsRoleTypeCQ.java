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

package jp.sf.fess.db.cbean.cq.bs;

import java.util.Map;

import jp.sf.fess.db.cbean.RoleTypeCB;
import jp.sf.fess.db.cbean.cq.DataConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.FileConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.LabelTypeToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.RoleTypeCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.ciq.RoleTypeCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.chelper.HpCalculator;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of ROLE_TYPE.
 * @author DBFlute(AutoGenerator)
 */
public class BsRoleTypeCQ extends AbstractBsRoleTypeCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected RoleTypeCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsRoleTypeCQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from ROLE_TYPE) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public RoleTypeCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected RoleTypeCIQ xcreateCIQ() {
        final RoleTypeCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected RoleTypeCIQ xnewCIQ() {
        return new RoleTypeCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join ROLE_TYPE on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public RoleTypeCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final RoleTypeCIQ inlineQuery = inline();
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

    public Map<String, DataConfigToRoleTypeMappingCQ> getId_ExistsReferrer_DataConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_ExistsReferrer_DataConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_ExistsReferrer_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("id_ExistsReferrer_DataConfigToRoleTypeMappingList",
                sq);
    }

    public Map<String, FileConfigToRoleTypeMappingCQ> getId_ExistsReferrer_FileConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_ExistsReferrer_FileConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_ExistsReferrer_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("id_ExistsReferrer_FileConfigToRoleTypeMappingList",
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

    public Map<String, WebConfigToRoleTypeMappingCQ> getId_ExistsReferrer_WebConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_ExistsReferrer_WebConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_ExistsReferrer_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("id_ExistsReferrer_WebConfigToRoleTypeMappingList", sq);
    }

    public Map<String, DataConfigToRoleTypeMappingCQ> getId_NotExistsReferrer_DataConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_NotExistsReferrer_DataConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_NotExistsReferrer_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotExistsReferrer_DataConfigToRoleTypeMappingList", sq);
    }

    public Map<String, FileConfigToRoleTypeMappingCQ> getId_NotExistsReferrer_FileConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_NotExistsReferrer_FileConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_NotExistsReferrer_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotExistsReferrer_FileConfigToRoleTypeMappingList", sq);
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

    public Map<String, WebConfigToRoleTypeMappingCQ> getId_NotExistsReferrer_WebConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_NotExistsReferrer_WebConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_NotExistsReferrer_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("id_NotExistsReferrer_WebConfigToRoleTypeMappingList",
                sq);
    }

    public Map<String, DataConfigToRoleTypeMappingCQ> getId_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingList", sq);
    }

    public Map<String, FileConfigToRoleTypeMappingCQ> getId_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_SpecifyDerivedReferrer_FileConfigToRoleTypeMappingList", sq);
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

    public Map<String, WebConfigToRoleTypeMappingCQ> getId_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_SpecifyDerivedReferrer_WebConfigToRoleTypeMappingList", sq);
    }

    public Map<String, DataConfigToRoleTypeMappingCQ> getId_InScopeRelation_DataConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_InScopeRelation_DataConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_InScopeRelation_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("id_InScopeRelation_DataConfigToRoleTypeMappingList",
                sq);
    }

    public Map<String, FileConfigToRoleTypeMappingCQ> getId_InScopeRelation_FileConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_InScopeRelation_FileConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_InScopeRelation_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("id_InScopeRelation_FileConfigToRoleTypeMappingList",
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

    public Map<String, WebConfigToRoleTypeMappingCQ> getId_InScopeRelation_WebConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_InScopeRelation_WebConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_InScopeRelation_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("id_InScopeRelation_WebConfigToRoleTypeMappingList",
                sq);
    }

    public Map<String, DataConfigToRoleTypeMappingCQ> getId_NotInScopeRelation_DataConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_NotInScopeRelation_DataConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_NotInScopeRelation_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotInScopeRelation_DataConfigToRoleTypeMappingList", sq);
    }

    public Map<String, FileConfigToRoleTypeMappingCQ> getId_NotInScopeRelation_FileConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_NotInScopeRelation_FileConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_NotInScopeRelation_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotInScopeRelation_FileConfigToRoleTypeMappingList", sq);
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

    public Map<String, WebConfigToRoleTypeMappingCQ> getId_NotInScopeRelation_WebConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_NotInScopeRelation_WebConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_NotInScopeRelation_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotInScopeRelation_WebConfigToRoleTypeMappingList", sq);
    }

    public Map<String, DataConfigToRoleTypeMappingCQ> getId_QueryDerivedReferrer_DataConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_QueryDerivedReferrer_DataConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_QueryDerivedReferrer_DataConfigToRoleTypeMappingList", sq);
    }

    public Map<String, Object> getId_QueryDerivedReferrer_DataConfigToRoleTypeMappingListParameter() {
        return xgetSQuePmMap("id_QueryDerivedReferrer_DataConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_DataConfigToRoleTypeMappingListParameter(
            final Object pm) {
        return xkeepSQuePm(
                "id_QueryDerivedReferrer_DataConfigToRoleTypeMappingList", pm);
    }

    public Map<String, FileConfigToRoleTypeMappingCQ> getId_QueryDerivedReferrer_FileConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_QueryDerivedReferrer_FileConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToRoleTypeMappingList(
            final FileConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_QueryDerivedReferrer_FileConfigToRoleTypeMappingList", sq);
    }

    public Map<String, Object> getId_QueryDerivedReferrer_FileConfigToRoleTypeMappingListParameter() {
        return xgetSQuePmMap("id_QueryDerivedReferrer_FileConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_FileConfigToRoleTypeMappingListParameter(
            final Object pm) {
        return xkeepSQuePm(
                "id_QueryDerivedReferrer_FileConfigToRoleTypeMappingList", pm);
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

    public Map<String, WebConfigToRoleTypeMappingCQ> getId_QueryDerivedReferrer_WebConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_QueryDerivedReferrer_WebConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToRoleTypeMappingList(
            final WebConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_QueryDerivedReferrer_WebConfigToRoleTypeMappingList", sq);
    }

    public Map<String, Object> getId_QueryDerivedReferrer_WebConfigToRoleTypeMappingListParameter() {
        return xgetSQuePmMap("id_QueryDerivedReferrer_WebConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_WebConfigToRoleTypeMappingListParameter(
            final Object pm) {
        return xkeepSQuePm(
                "id_QueryDerivedReferrer_WebConfigToRoleTypeMappingList", pm);
    }

    /**
     * Add order-by as ascend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_Id_Desc() {
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
    public BsRoleTypeCQ addOrderBy_Name_Asc() {
        regOBA("NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * NAME: {NotNull, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_Name_Desc() {
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
    public BsRoleTypeCQ addOrderBy_Value_Asc() {
        regOBA("VALUE");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * VALUE: {NotNull, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_Value_Desc() {
        regOBD("VALUE");
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
    public BsRoleTypeCQ addOrderBy_SortOrder_Asc() {
        regOBA("SORT_ORDER");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_SortOrder_Desc() {
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
    public BsRoleTypeCQ addOrderBy_CreatedBy_Asc() {
        regOBA("CREATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_CreatedBy_Desc() {
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
    public BsRoleTypeCQ addOrderBy_CreatedTime_Asc() {
        regOBA("CREATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_CreatedTime_Desc() {
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
    public BsRoleTypeCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("UPDATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_UpdatedBy_Desc() {
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
    public BsRoleTypeCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("UPDATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_UpdatedTime_Desc() {
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
    public BsRoleTypeCQ addOrderBy_DeletedBy_Asc() {
        regOBA("DELETED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_DeletedBy_Desc() {
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
    public BsRoleTypeCQ addOrderBy_DeletedTime_Asc() {
        regOBA("DELETED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_DeletedTime_Desc() {
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
    public BsRoleTypeCQ addOrderBy_VersionNo_Asc() {
        regOBA("VERSION_NO");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsRoleTypeCQ addOrderBy_VersionNo_Desc() {
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
    public BsRoleTypeCQ addSpecifiedDerivedOrderBy_Asc(final String aliasName) {
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
    public BsRoleTypeCQ addSpecifiedDerivedOrderBy_Desc(final String aliasName) {
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
    public Map<String, RoleTypeCQ> getScalarCondition() {
        return xgetSQueMap("scalarCondition");
    }

    @Override
    public String keepScalarCondition(final RoleTypeCQ sq) {
        return xkeepSQue("scalarCondition", sq);
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public Map<String, RoleTypeCQ> getSpecifyMyselfDerived() {
        return xgetSQueMap("specifyMyselfDerived");
    }

    @Override
    public String keepSpecifyMyselfDerived(final RoleTypeCQ sq) {
        return xkeepSQue("specifyMyselfDerived", sq);
    }

    public Map<String, RoleTypeCQ> getQueryMyselfDerived() {
        return xgetSQueMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerived(final RoleTypeCQ sq) {
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
    protected Map<String, RoleTypeCQ> _myselfExistsMap;

    public Map<String, RoleTypeCQ> getMyselfExists() {
        return xgetSQueMap("myselfExists");
    }

    @Override
    public String keepMyselfExists(final RoleTypeCQ sq) {
        return xkeepSQue("myselfExists", sq);
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    public Map<String, RoleTypeCQ> getMyselfInScope() {
        return xgetSQueMap("myselfInScope");
    }

    @Override
    public String keepMyselfInScope(final RoleTypeCQ sq) {
        return xkeepSQue("myselfInScope", sq);
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() {
        return RoleTypeCB.class.getName();
    }

    protected String xCQ() {
        return RoleTypeCQ.class.getName();
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
