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

import jp.sf.fess.db.cbean.DataConfigToRoleTypeMappingCB;
import jp.sf.fess.db.cbean.cq.DataConfigToRoleTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.DataCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.RoleTypeCQ;
import jp.sf.fess.db.cbean.cq.ciq.DataConfigToRoleTypeMappingCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of DATA_CONFIG_TO_ROLE_TYPE_MAPPING.
 * @author DBFlute(AutoGenerator)
 */
public class BsDataConfigToRoleTypeMappingCQ extends
        AbstractBsDataConfigToRoleTypeMappingCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataConfigToRoleTypeMappingCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsDataConfigToRoleTypeMappingCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from DATA_CONFIG_TO_ROLE_TYPE_MAPPING) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public DataConfigToRoleTypeMappingCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected DataConfigToRoleTypeMappingCIQ xcreateCIQ() {
        final DataConfigToRoleTypeMappingCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected DataConfigToRoleTypeMappingCIQ xnewCIQ() {
        return new DataConfigToRoleTypeMappingCIQ(xgetReferrerQuery(),
                xgetSqlClause(), xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join DATA_CONFIG_TO_ROLE_TYPE_MAPPING on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public DataConfigToRoleTypeMappingCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final DataConfigToRoleTypeMappingCIQ inlineQuery = inline();
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
    public BsDataConfigToRoleTypeMappingCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsDataConfigToRoleTypeMappingCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _dataConfigId;

    public ConditionValue getDataConfigId() {
        if (_dataConfigId == null) {
            _dataConfigId = nCV();
        }
        return _dataConfigId;
    }

    @Override
    protected ConditionValue getCValueDataConfigId() {
        return getDataConfigId();
    }

    protected Map<String, DataCrawlingConfigCQ> _dataConfigId_InScopeRelation_DataCrawlingConfigMap;

    public Map<String, DataCrawlingConfigCQ> getDataConfigId_InScopeRelation_DataCrawlingConfig() {
        return _dataConfigId_InScopeRelation_DataCrawlingConfigMap;
    }

    @Override
    public String keepDataConfigId_InScopeRelation_DataCrawlingConfig(
            final DataCrawlingConfigCQ subQuery) {
        if (_dataConfigId_InScopeRelation_DataCrawlingConfigMap == null) {
            _dataConfigId_InScopeRelation_DataCrawlingConfigMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_dataConfigId_InScopeRelation_DataCrawlingConfigMap.size() + 1);
        _dataConfigId_InScopeRelation_DataCrawlingConfigMap.put(key, subQuery);
        return "dataConfigId_InScopeRelation_DataCrawlingConfig." + key;
    }

    protected Map<String, DataCrawlingConfigCQ> _dataConfigId_NotInScopeRelation_DataCrawlingConfigMap;

    public Map<String, DataCrawlingConfigCQ> getDataConfigId_NotInScopeRelation_DataCrawlingConfig() {
        return _dataConfigId_NotInScopeRelation_DataCrawlingConfigMap;
    }

    @Override
    public String keepDataConfigId_NotInScopeRelation_DataCrawlingConfig(
            final DataCrawlingConfigCQ subQuery) {
        if (_dataConfigId_NotInScopeRelation_DataCrawlingConfigMap == null) {
            _dataConfigId_NotInScopeRelation_DataCrawlingConfigMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_dataConfigId_NotInScopeRelation_DataCrawlingConfigMap
                        .size() + 1);
        _dataConfigId_NotInScopeRelation_DataCrawlingConfigMap.put(key,
                subQuery);
        return "dataConfigId_NotInScopeRelation_DataCrawlingConfig." + key;
    }

    /**
     * Add order-by as ascend. <br />
     * DATA_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to DATA_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsDataConfigToRoleTypeMappingCQ addOrderBy_DataConfigId_Asc() {
        regOBA("DATA_CONFIG_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DATA_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to DATA_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsDataConfigToRoleTypeMappingCQ addOrderBy_DataConfigId_Desc() {
        regOBD("DATA_CONFIG_ID");
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
    public BsDataConfigToRoleTypeMappingCQ addOrderBy_RoleTypeId_Asc() {
        regOBA("ROLE_TYPE_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ROLE_TYPE_ID: {IX, NotNull, BIGINT(19), FK to ROLE_TYPE}
     * @return this. (NotNull)
     */
    public BsDataConfigToRoleTypeMappingCQ addOrderBy_RoleTypeId_Desc() {
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
    public BsDataConfigToRoleTypeMappingCQ addSpecifiedDerivedOrderBy_Asc(
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
    public BsDataConfigToRoleTypeMappingCQ addSpecifiedDerivedOrderBy_Desc(
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
        final DataConfigToRoleTypeMappingCQ baseQuery = (DataConfigToRoleTypeMappingCQ) baseQueryAsSuper;
        final DataConfigToRoleTypeMappingCQ unionQuery = (DataConfigToRoleTypeMappingCQ) unionQueryAsSuper;
        if (baseQuery.hasConditionQueryDataCrawlingConfig()) {
            unionQuery.queryDataCrawlingConfig().reflectRelationOnUnionQuery(
                    baseQuery.queryDataCrawlingConfig(),
                    unionQuery.queryDataCrawlingConfig());
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
     * DATA_CRAWLING_CONFIG by my DATA_CONFIG_ID, named 'dataCrawlingConfig'.
     * @return The instance of condition-query. (NotNull)
     */
    public DataCrawlingConfigCQ queryDataCrawlingConfig() {
        return getConditionQueryDataCrawlingConfig();
    }

    protected DataCrawlingConfigCQ _conditionQueryDataCrawlingConfig;

    public DataCrawlingConfigCQ getConditionQueryDataCrawlingConfig() {
        if (_conditionQueryDataCrawlingConfig == null) {
            _conditionQueryDataCrawlingConfig = xcreateQueryDataCrawlingConfig();
            xsetupOuterJoinDataCrawlingConfig();
        }
        return _conditionQueryDataCrawlingConfig;
    }

    protected DataCrawlingConfigCQ xcreateQueryDataCrawlingConfig() {
        final String nrp = resolveNextRelationPath(
                "DATA_CONFIG_TO_ROLE_TYPE_MAPPING", "dataCrawlingConfig");
        final String jan = resolveJoinAliasName(nrp, xgetNextNestLevel());
        final DataCrawlingConfigCQ cq = new DataCrawlingConfigCQ(this,
                xgetSqlClause(), jan, xgetNextNestLevel());
        cq.xsetBaseCB(_baseCB);
        cq.xsetForeignPropertyName("dataCrawlingConfig");
        cq.xsetRelationPath(nrp);
        return cq;
    }

    protected void xsetupOuterJoinDataCrawlingConfig() {
        final DataCrawlingConfigCQ cq = getConditionQueryDataCrawlingConfig();
        final Map<String, String> joinOnMap = newLinkedHashMapSized(4);
        joinOnMap.put("DATA_CONFIG_ID", "ID");
        registerOuterJoin(cq, joinOnMap, "dataCrawlingConfig");
    }

    public boolean hasConditionQueryDataCrawlingConfig() {
        return _conditionQueryDataCrawlingConfig != null;
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
                "DATA_CONFIG_TO_ROLE_TYPE_MAPPING", "roleType");
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
    protected Map<String, DataConfigToRoleTypeMappingCQ> _scalarConditionMap;

    public Map<String, DataConfigToRoleTypeMappingCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(
            final DataConfigToRoleTypeMappingCQ subQuery) {
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
    protected Map<String, DataConfigToRoleTypeMappingCQ> _specifyMyselfDerivedMap;

    public Map<String, DataConfigToRoleTypeMappingCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(
            final DataConfigToRoleTypeMappingCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, DataConfigToRoleTypeMappingCQ> _queryMyselfDerivedMap;

    public Map<String, DataConfigToRoleTypeMappingCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(
            final DataConfigToRoleTypeMappingCQ subQuery) {
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
    protected Map<String, DataConfigToRoleTypeMappingCQ> _myselfExistsMap;

    public Map<String, DataConfigToRoleTypeMappingCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final DataConfigToRoleTypeMappingCQ subQuery) {
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
    protected Map<String, DataConfigToRoleTypeMappingCQ> _myselfInScopeMap;

    public Map<String, DataConfigToRoleTypeMappingCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(final DataConfigToRoleTypeMappingCQ subQuery) {
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
        return DataConfigToRoleTypeMappingCB.class.getName();
    }

    protected String xCQ() {
        return DataConfigToRoleTypeMappingCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
