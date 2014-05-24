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

import jp.sf.fess.db.cbean.LabelTypeToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.cq.LabelTypeCQ;
import jp.sf.fess.db.cbean.cq.LabelTypeToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.RoleTypeCQ;
import jp.sf.fess.db.cbean.cq.ciq.LabelTypeToRoleTypeMappingCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of LABEL_TYPE_TO_ROLE_TYPE_MAPPING.
 * @author DBFlute(AutoGenerator)
 */
public class BsLabelTypeToRoleTypeMappingCQ extends
        AbstractBsLabelTypeToRoleTypeMappingCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected LabelTypeToRoleTypeMappingCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsLabelTypeToRoleTypeMappingCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from LABEL_TYPE_TO_ROLE_TYPE_MAPPING) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public LabelTypeToRoleTypeMappingCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected LabelTypeToRoleTypeMappingCIQ xcreateCIQ() {
        final LabelTypeToRoleTypeMappingCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected LabelTypeToRoleTypeMappingCIQ xnewCIQ() {
        return new LabelTypeToRoleTypeMappingCIQ(xgetReferrerQuery(),
                xgetSqlClause(), xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join LABEL_TYPE_TO_ROLE_TYPE_MAPPING on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public LabelTypeToRoleTypeMappingCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final LabelTypeToRoleTypeMappingCIQ inlineQuery = inline();
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

    /**
     * Add order-by as ascend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsLabelTypeToRoleTypeMappingCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsLabelTypeToRoleTypeMappingCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _labelTypeId;

    public ConditionValue getLabelTypeId() {
        if (_labelTypeId == null) {
            _labelTypeId = nCV();
        }
        return _labelTypeId;
    }

    @Override
    protected ConditionValue getCValueLabelTypeId() {
        return getLabelTypeId();
    }

    protected Map<String, LabelTypeCQ> _labelTypeId_InScopeRelation_LabelTypeMap;

    public Map<String, LabelTypeCQ> getLabelTypeId_InScopeRelation_LabelType() {
        return _labelTypeId_InScopeRelation_LabelTypeMap;
    }

    @Override
    public String keepLabelTypeId_InScopeRelation_LabelType(
            final LabelTypeCQ subQuery) {
        if (_labelTypeId_InScopeRelation_LabelTypeMap == null) {
            _labelTypeId_InScopeRelation_LabelTypeMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_labelTypeId_InScopeRelation_LabelTypeMap.size() + 1);
        _labelTypeId_InScopeRelation_LabelTypeMap.put(key, subQuery);
        return "labelTypeId_InScopeRelation_LabelType." + key;
    }

    protected Map<String, LabelTypeCQ> _labelTypeId_NotInScopeRelation_LabelTypeMap;

    public Map<String, LabelTypeCQ> getLabelTypeId_NotInScopeRelation_LabelType() {
        return _labelTypeId_NotInScopeRelation_LabelTypeMap;
    }

    @Override
    public String keepLabelTypeId_NotInScopeRelation_LabelType(
            final LabelTypeCQ subQuery) {
        if (_labelTypeId_NotInScopeRelation_LabelTypeMap == null) {
            _labelTypeId_NotInScopeRelation_LabelTypeMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_labelTypeId_NotInScopeRelation_LabelTypeMap.size() + 1);
        _labelTypeId_NotInScopeRelation_LabelTypeMap.put(key, subQuery);
        return "labelTypeId_NotInScopeRelation_LabelType." + key;
    }

    /**
     * Add order-by as ascend. <br />
     * LABEL_TYPE_ID: {IX, NotNull, BIGINT(19), FK to LABEL_TYPE}
     * @return this. (NotNull)
     */
    public BsLabelTypeToRoleTypeMappingCQ addOrderBy_LabelTypeId_Asc() {
        regOBA("LABEL_TYPE_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * LABEL_TYPE_ID: {IX, NotNull, BIGINT(19), FK to LABEL_TYPE}
     * @return this. (NotNull)
     */
    public BsLabelTypeToRoleTypeMappingCQ addOrderBy_LabelTypeId_Desc() {
        regOBD("LABEL_TYPE_ID");
        return this;
    }

    protected ConditionValue _roleTypeId;

    public ConditionValue getRoleTypeId() {
        if (_roleTypeId == null) {
            _roleTypeId = nCV();
        }
        return _roleTypeId;
    }

    @Override
    protected ConditionValue getCValueRoleTypeId() {
        return getRoleTypeId();
    }

    protected Map<String, RoleTypeCQ> _roleTypeId_InScopeRelation_RoleTypeMap;

    public Map<String, RoleTypeCQ> getRoleTypeId_InScopeRelation_RoleType() {
        return _roleTypeId_InScopeRelation_RoleTypeMap;
    }

    @Override
    public String keepRoleTypeId_InScopeRelation_RoleType(
            final RoleTypeCQ subQuery) {
        if (_roleTypeId_InScopeRelation_RoleTypeMap == null) {
            _roleTypeId_InScopeRelation_RoleTypeMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_roleTypeId_InScopeRelation_RoleTypeMap.size() + 1);
        _roleTypeId_InScopeRelation_RoleTypeMap.put(key, subQuery);
        return "roleTypeId_InScopeRelation_RoleType." + key;
    }

    protected Map<String, RoleTypeCQ> _roleTypeId_NotInScopeRelation_RoleTypeMap;

    public Map<String, RoleTypeCQ> getRoleTypeId_NotInScopeRelation_RoleType() {
        return _roleTypeId_NotInScopeRelation_RoleTypeMap;
    }

    @Override
    public String keepRoleTypeId_NotInScopeRelation_RoleType(
            final RoleTypeCQ subQuery) {
        if (_roleTypeId_NotInScopeRelation_RoleTypeMap == null) {
            _roleTypeId_NotInScopeRelation_RoleTypeMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_roleTypeId_NotInScopeRelation_RoleTypeMap.size() + 1);
        _roleTypeId_NotInScopeRelation_RoleTypeMap.put(key, subQuery);
        return "roleTypeId_NotInScopeRelation_RoleType." + key;
    }

    /**
     * Add order-by as ascend. <br />
     * ROLE_TYPE_ID: {IX, NotNull, BIGINT(19), FK to ROLE_TYPE}
     * @return this. (NotNull)
     */
    public BsLabelTypeToRoleTypeMappingCQ addOrderBy_RoleTypeId_Asc() {
        regOBA("ROLE_TYPE_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ROLE_TYPE_ID: {IX, NotNull, BIGINT(19), FK to ROLE_TYPE}
     * @return this. (NotNull)
     */
    public BsLabelTypeToRoleTypeMappingCQ addOrderBy_RoleTypeId_Desc() {
        regOBD("ROLE_TYPE_ID");
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
     * }, <span style="color: #FD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] asc</span>
     * cb.<span style="color: #FD4747">addSpecifiedDerivedOrderBy_Asc</span>(<span style="color: #FD4747">aliasName</span>);
     * </pre>
     * @param aliasName The alias name specified at (Specify)DerivedReferrer. (NotNull)
     * @return this. (NotNull)
     */
    public BsLabelTypeToRoleTypeMappingCQ addSpecifiedDerivedOrderBy_Asc(
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
     * }, <span style="color: #FD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] desc</span>
     * cb.<span style="color: #FD4747">addSpecifiedDerivedOrderBy_Desc</span>(<span style="color: #FD4747">aliasName</span>);
     * </pre>
     * @param aliasName The alias name specified at (Specify)DerivedReferrer. (NotNull)
     * @return this. (NotNull)
     */
    public BsLabelTypeToRoleTypeMappingCQ addSpecifiedDerivedOrderBy_Desc(
            final String aliasName) {
        registerSpecifiedDerivedOrderBy_Desc(aliasName);
        return this;
    }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    @Override
    protected void reflectRelationOnUnionQuery(
            final ConditionQuery baseQueryAsSuper,
            final ConditionQuery unionQueryAsSuper) {
        final LabelTypeToRoleTypeMappingCQ baseQuery = (LabelTypeToRoleTypeMappingCQ) baseQueryAsSuper;
        final LabelTypeToRoleTypeMappingCQ unionQuery = (LabelTypeToRoleTypeMappingCQ) unionQueryAsSuper;
        if (baseQuery.hasConditionQueryLabelType()) {
            unionQuery.queryLabelType().reflectRelationOnUnionQuery(
                    baseQuery.queryLabelType(), unionQuery.queryLabelType());
        }
        if (baseQuery.hasConditionQueryRoleType()) {
            unionQuery.queryRoleType().reflectRelationOnUnionQuery(
                    baseQuery.queryRoleType(), unionQuery.queryRoleType());
        }
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    /**
     * Get the condition-query for relation table. <br />
     * LABEL_TYPE by my LABEL_TYPE_ID, named 'labelType'.
     * @return The instance of condition-query. (NotNull)
     */
    public LabelTypeCQ queryLabelType() {
        return getConditionQueryLabelType();
    }

    protected LabelTypeCQ _conditionQueryLabelType;

    public LabelTypeCQ getConditionQueryLabelType() {
        if (_conditionQueryLabelType == null) {
            _conditionQueryLabelType = xcreateQueryLabelType();
            xsetupOuterJoinLabelType();
        }
        return _conditionQueryLabelType;
    }

    protected LabelTypeCQ xcreateQueryLabelType() {
        final String nrp = resolveNextRelationPath(
                "LABEL_TYPE_TO_ROLE_TYPE_MAPPING", "labelType");
        final String jan = resolveJoinAliasName(nrp, xgetNextNestLevel());
        final LabelTypeCQ cq = new LabelTypeCQ(this, xgetSqlClause(), jan,
                xgetNextNestLevel());
        cq.xsetBaseCB(_baseCB);
        cq.xsetForeignPropertyName("labelType");
        cq.xsetRelationPath(nrp);
        return cq;
    }

    protected void xsetupOuterJoinLabelType() {
        final LabelTypeCQ cq = getConditionQueryLabelType();
        final Map<String, String> joinOnMap = newLinkedHashMapSized(4);
        joinOnMap.put("LABEL_TYPE_ID", "ID");
        registerOuterJoin(cq, joinOnMap, "labelType");
    }

    public boolean hasConditionQueryLabelType() {
        return _conditionQueryLabelType != null;
    }

    /**
     * Get the condition-query for relation table. <br />
     * ROLE_TYPE by my ROLE_TYPE_ID, named 'roleType'.
     * @return The instance of condition-query. (NotNull)
     */
    public RoleTypeCQ queryRoleType() {
        return getConditionQueryRoleType();
    }

    protected RoleTypeCQ _conditionQueryRoleType;

    public RoleTypeCQ getConditionQueryRoleType() {
        if (_conditionQueryRoleType == null) {
            _conditionQueryRoleType = xcreateQueryRoleType();
            xsetupOuterJoinRoleType();
        }
        return _conditionQueryRoleType;
    }

    protected RoleTypeCQ xcreateQueryRoleType() {
        final String nrp = resolveNextRelationPath(
                "LABEL_TYPE_TO_ROLE_TYPE_MAPPING", "roleType");
        final String jan = resolveJoinAliasName(nrp, xgetNextNestLevel());
        final RoleTypeCQ cq = new RoleTypeCQ(this, xgetSqlClause(), jan,
                xgetNextNestLevel());
        cq.xsetBaseCB(_baseCB);
        cq.xsetForeignPropertyName("roleType");
        cq.xsetRelationPath(nrp);
        return cq;
    }

    protected void xsetupOuterJoinRoleType() {
        final RoleTypeCQ cq = getConditionQueryRoleType();
        final Map<String, String> joinOnMap = newLinkedHashMapSized(4);
        joinOnMap.put("ROLE_TYPE_ID", "ID");
        registerOuterJoin(cq, joinOnMap, "roleType");
    }

    public boolean hasConditionQueryRoleType() {
        return _conditionQueryRoleType != null;
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    protected Map<String, LabelTypeToRoleTypeMappingCQ> _scalarConditionMap;

    public Map<String, LabelTypeToRoleTypeMappingCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(
            final LabelTypeToRoleTypeMappingCQ subQuery) {
        if (_scalarConditionMap == null) {
            _scalarConditionMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey" + (_scalarConditionMap.size() + 1);
        _scalarConditionMap.put(key, subQuery);
        return "scalarCondition." + key;
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    protected Map<String, LabelTypeToRoleTypeMappingCQ> _specifyMyselfDerivedMap;

    public Map<String, LabelTypeToRoleTypeMappingCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(
            final LabelTypeToRoleTypeMappingCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, LabelTypeToRoleTypeMappingCQ> _queryMyselfDerivedMap;

    public Map<String, LabelTypeToRoleTypeMappingCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(
            final LabelTypeToRoleTypeMappingCQ subQuery) {
        if (_queryMyselfDerivedMap == null) {
            _queryMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_queryMyselfDerivedMap.size() + 1);
        _queryMyselfDerivedMap.put(key, subQuery);
        return "queryMyselfDerived." + key;
    }

    protected Map<String, Object> _qyeryMyselfDerivedParameterMap;

    public Map<String, Object> getQueryMyselfDerivedParameter() {
        return _qyeryMyselfDerivedParameterMap;
    }

    @Override
    public String keepQueryMyselfDerivedParameter(final Object parameterValue) {
        if (_qyeryMyselfDerivedParameterMap == null) {
            _qyeryMyselfDerivedParameterMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryParameterKey"
                + (_qyeryMyselfDerivedParameterMap.size() + 1);
        _qyeryMyselfDerivedParameterMap.put(key, parameterValue);
        return "queryMyselfDerivedParameter." + key;
    }

    // ===================================================================================
    //                                                                        MyselfExists
    //                                                                        ============
    protected Map<String, LabelTypeToRoleTypeMappingCQ> _myselfExistsMap;

    public Map<String, LabelTypeToRoleTypeMappingCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final LabelTypeToRoleTypeMappingCQ subQuery) {
        if (_myselfExistsMap == null) {
            _myselfExistsMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey" + (_myselfExistsMap.size() + 1);
        _myselfExistsMap.put(key, subQuery);
        return "myselfExists." + key;
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    protected Map<String, LabelTypeToRoleTypeMappingCQ> _myselfInScopeMap;

    public Map<String, LabelTypeToRoleTypeMappingCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(final LabelTypeToRoleTypeMappingCQ subQuery) {
        if (_myselfInScopeMap == null) {
            _myselfInScopeMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey" + (_myselfInScopeMap.size() + 1);
        _myselfInScopeMap.put(key, subQuery);
        return "myselfInScope." + key;
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() {
        return LabelTypeToRoleTypeMappingCB.class.getName();
    }

    protected String xCQ() {
        return LabelTypeToRoleTypeMappingCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
