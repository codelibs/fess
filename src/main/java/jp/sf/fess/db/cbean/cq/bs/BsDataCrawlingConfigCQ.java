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

import jp.sf.fess.db.cbean.DataCrawlingConfigCB;
import jp.sf.fess.db.cbean.cq.DataConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.DataConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.DataCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.ciq.DataCrawlingConfigCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.chelper.HpCalculator;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of DATA_CRAWLING_CONFIG.
 * @author DBFlute(AutoGenerator)
 */
public class BsDataCrawlingConfigCQ extends AbstractBsDataCrawlingConfigCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataCrawlingConfigCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsDataCrawlingConfigCQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from DATA_CRAWLING_CONFIG) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public DataCrawlingConfigCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected DataCrawlingConfigCIQ xcreateCIQ() {
        final DataCrawlingConfigCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected DataCrawlingConfigCIQ xnewCIQ() {
        return new DataCrawlingConfigCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join DATA_CRAWLING_CONFIG on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public DataCrawlingConfigCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final DataCrawlingConfigCIQ inlineQuery = inline();
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

    public Map<String, DataConfigToRoleTypeMappingCQ> getId_ExistsReferrer_DataConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_ExistsReferrer_DataConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_ExistsReferrer_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("id_ExistsReferrer_DataConfigToRoleTypeMappingList",
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

    public Map<String, DataConfigToRoleTypeMappingCQ> getId_NotExistsReferrer_DataConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_NotExistsReferrer_DataConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_NotExistsReferrer_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotExistsReferrer_DataConfigToRoleTypeMappingList", sq);
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

    public Map<String, DataConfigToRoleTypeMappingCQ> getId_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_SpecifyDerivedReferrer_DataConfigToRoleTypeMappingList", sq);
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

    public Map<String, DataConfigToRoleTypeMappingCQ> getId_InScopeRelation_DataConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_InScopeRelation_DataConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_InScopeRelation_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue("id_InScopeRelation_DataConfigToRoleTypeMappingList",
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

    public Map<String, DataConfigToRoleTypeMappingCQ> getId_NotInScopeRelation_DataConfigToRoleTypeMappingList() {
        return xgetSQueMap("id_NotInScopeRelation_DataConfigToRoleTypeMappingList");
    }

    @Override
    public String keepId_NotInScopeRelation_DataConfigToRoleTypeMappingList(
            final DataConfigToRoleTypeMappingCQ sq) {
        return xkeepSQue(
                "id_NotInScopeRelation_DataConfigToRoleTypeMappingList", sq);
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

    /**
     * Add order-by as ascend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_Id_Desc() {
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
     * NAME: {NotNull, VARCHAR(200)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_Name_Asc() {
        regOBA("NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * NAME: {NotNull, VARCHAR(200)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_Name_Desc() {
        regOBD("NAME");
        return this;
    }

    protected ConditionValue _handlerName;

    public ConditionValue getHandlerName() {
        if (_handlerName == null) {
            _handlerName = nCV();
        }
        return _handlerName;
    }

    @Override
    protected ConditionValue getCValueHandlerName() {
        return getHandlerName();
    }

    /**
     * Add order-by as ascend. <br />
     * HANDLER_NAME: {NotNull, VARCHAR(200)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_HandlerName_Asc() {
        regOBA("HANDLER_NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * HANDLER_NAME: {NotNull, VARCHAR(200)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_HandlerName_Desc() {
        regOBD("HANDLER_NAME");
        return this;
    }

    protected ConditionValue _handlerParameter;

    public ConditionValue getHandlerParameter() {
        if (_handlerParameter == null) {
            _handlerParameter = nCV();
        }
        return _handlerParameter;
    }

    @Override
    protected ConditionValue getCValueHandlerParameter() {
        return getHandlerParameter();
    }

    /**
     * Add order-by as ascend. <br />
     * HANDLER_PARAMETER: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_HandlerParameter_Asc() {
        regOBA("HANDLER_PARAMETER");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * HANDLER_PARAMETER: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_HandlerParameter_Desc() {
        regOBD("HANDLER_PARAMETER");
        return this;
    }

    protected ConditionValue _handlerScript;

    public ConditionValue getHandlerScript() {
        if (_handlerScript == null) {
            _handlerScript = nCV();
        }
        return _handlerScript;
    }

    @Override
    protected ConditionValue getCValueHandlerScript() {
        return getHandlerScript();
    }

    /**
     * Add order-by as ascend. <br />
     * HANDLER_SCRIPT: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_HandlerScript_Asc() {
        regOBA("HANDLER_SCRIPT");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * HANDLER_SCRIPT: {VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_HandlerScript_Desc() {
        regOBD("HANDLER_SCRIPT");
        return this;
    }

    protected ConditionValue _boost;

    public ConditionValue getBoost() {
        if (_boost == null) {
            _boost = nCV();
        }
        return _boost;
    }

    @Override
    protected ConditionValue getCValueBoost() {
        return getBoost();
    }

    /**
     * Add order-by as ascend. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_Boost_Asc() {
        regOBA("BOOST");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * BOOST: {NotNull, DOUBLE(17)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_Boost_Desc() {
        regOBD("BOOST");
        return this;
    }

    protected ConditionValue _available;

    public ConditionValue getAvailable() {
        if (_available == null) {
            _available = nCV();
        }
        return _available;
    }

    @Override
    protected ConditionValue getCValueAvailable() {
        return getAvailable();
    }

    /**
     * Add order-by as ascend. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_Available_Asc() {
        regOBA("AVAILABLE");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_Available_Desc() {
        regOBD("AVAILABLE");
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
    public BsDataCrawlingConfigCQ addOrderBy_SortOrder_Asc() {
        regOBA("SORT_ORDER");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_SortOrder_Desc() {
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
    public BsDataCrawlingConfigCQ addOrderBy_CreatedBy_Asc() {
        regOBA("CREATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_CreatedBy_Desc() {
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
    public BsDataCrawlingConfigCQ addOrderBy_CreatedTime_Asc() {
        regOBA("CREATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_CreatedTime_Desc() {
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
    public BsDataCrawlingConfigCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("UPDATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_UpdatedBy_Desc() {
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
    public BsDataCrawlingConfigCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("UPDATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_UpdatedTime_Desc() {
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
    public BsDataCrawlingConfigCQ addOrderBy_DeletedBy_Asc() {
        regOBA("DELETED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_DeletedBy_Desc() {
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
    public BsDataCrawlingConfigCQ addOrderBy_DeletedTime_Asc() {
        regOBA("DELETED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_DeletedTime_Desc() {
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
    public BsDataCrawlingConfigCQ addOrderBy_VersionNo_Asc() {
        regOBA("VERSION_NO");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsDataCrawlingConfigCQ addOrderBy_VersionNo_Desc() {
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
    public BsDataCrawlingConfigCQ addSpecifiedDerivedOrderBy_Asc(
            final String aliasName) {
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
    public BsDataCrawlingConfigCQ addSpecifiedDerivedOrderBy_Desc(
            final String aliasName) {
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
    public Map<String, DataCrawlingConfigCQ> getScalarCondition() {
        return xgetSQueMap("scalarCondition");
    }

    @Override
    public String keepScalarCondition(final DataCrawlingConfigCQ sq) {
        return xkeepSQue("scalarCondition", sq);
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public Map<String, DataCrawlingConfigCQ> getSpecifyMyselfDerived() {
        return xgetSQueMap("specifyMyselfDerived");
    }

    @Override
    public String keepSpecifyMyselfDerived(final DataCrawlingConfigCQ sq) {
        return xkeepSQue("specifyMyselfDerived", sq);
    }

    public Map<String, DataCrawlingConfigCQ> getQueryMyselfDerived() {
        return xgetSQueMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerived(final DataCrawlingConfigCQ sq) {
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
    protected Map<String, DataCrawlingConfigCQ> _myselfExistsMap;

    public Map<String, DataCrawlingConfigCQ> getMyselfExists() {
        return xgetSQueMap("myselfExists");
    }

    @Override
    public String keepMyselfExists(final DataCrawlingConfigCQ sq) {
        return xkeepSQue("myselfExists", sq);
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    public Map<String, DataCrawlingConfigCQ> getMyselfInScope() {
        return xgetSQueMap("myselfInScope");
    }

    @Override
    public String keepMyselfInScope(final DataCrawlingConfigCQ sq) {
        return xkeepSQue("myselfInScope", sq);
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() {
        return DataCrawlingConfigCB.class.getName();
    }

    protected String xCQ() {
        return DataCrawlingConfigCQ.class.getName();
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
