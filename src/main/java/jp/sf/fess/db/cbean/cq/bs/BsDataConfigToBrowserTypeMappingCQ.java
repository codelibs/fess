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

import jp.sf.fess.db.cbean.DataConfigToBrowserTypeMappingCB;
import jp.sf.fess.db.cbean.cq.BrowserTypeCQ;
import jp.sf.fess.db.cbean.cq.DataConfigToBrowserTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.DataCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.ciq.DataConfigToBrowserTypeMappingCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of DATA_CONFIG_TO_BROWSER_TYPE_MAPPING.
 * @author DBFlute(AutoGenerator)
 */
public class BsDataConfigToBrowserTypeMappingCQ extends
        AbstractBsDataConfigToBrowserTypeMappingCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataConfigToBrowserTypeMappingCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsDataConfigToBrowserTypeMappingCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from DATA_CONFIG_TO_BROWSER_TYPE_MAPPING) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public DataConfigToBrowserTypeMappingCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected DataConfigToBrowserTypeMappingCIQ xcreateCIQ() {
        final DataConfigToBrowserTypeMappingCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected DataConfigToBrowserTypeMappingCIQ xnewCIQ() {
        return new DataConfigToBrowserTypeMappingCIQ(xgetReferrerQuery(),
                xgetSqlClause(), xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join DATA_CONFIG_TO_BROWSER_TYPE_MAPPING on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public DataConfigToBrowserTypeMappingCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final DataConfigToBrowserTypeMappingCIQ inlineQuery = inline();
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
    public BsDataConfigToBrowserTypeMappingCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsDataConfigToBrowserTypeMappingCQ addOrderBy_Id_Desc() {
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
    public BsDataConfigToBrowserTypeMappingCQ addOrderBy_DataConfigId_Asc() {
        regOBA("DATA_CONFIG_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DATA_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to DATA_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsDataConfigToBrowserTypeMappingCQ addOrderBy_DataConfigId_Desc() {
        regOBD("DATA_CONFIG_ID");
        return this;
    }

    protected ConditionValue _browserTypeId;

    public ConditionValue getBrowserTypeId() {
        if (_browserTypeId == null) {
            _browserTypeId = nCV();
        }
        return _browserTypeId;
    }

    @Override
    protected ConditionValue getCValueBrowserTypeId() {
        return getBrowserTypeId();
    }

    protected Map<String, BrowserTypeCQ> _browserTypeId_InScopeRelation_BrowserTypeMap;

    public Map<String, BrowserTypeCQ> getBrowserTypeId_InScopeRelation_BrowserType() {
        return _browserTypeId_InScopeRelation_BrowserTypeMap;
    }

    @Override
    public String keepBrowserTypeId_InScopeRelation_BrowserType(
            final BrowserTypeCQ subQuery) {
        if (_browserTypeId_InScopeRelation_BrowserTypeMap == null) {
            _browserTypeId_InScopeRelation_BrowserTypeMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_browserTypeId_InScopeRelation_BrowserTypeMap.size() + 1);
        _browserTypeId_InScopeRelation_BrowserTypeMap.put(key, subQuery);
        return "browserTypeId_InScopeRelation_BrowserType." + key;
    }

    protected Map<String, BrowserTypeCQ> _browserTypeId_NotInScopeRelation_BrowserTypeMap;

    public Map<String, BrowserTypeCQ> getBrowserTypeId_NotInScopeRelation_BrowserType() {
        return _browserTypeId_NotInScopeRelation_BrowserTypeMap;
    }

    @Override
    public String keepBrowserTypeId_NotInScopeRelation_BrowserType(
            final BrowserTypeCQ subQuery) {
        if (_browserTypeId_NotInScopeRelation_BrowserTypeMap == null) {
            _browserTypeId_NotInScopeRelation_BrowserTypeMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_browserTypeId_NotInScopeRelation_BrowserTypeMap.size() + 1);
        _browserTypeId_NotInScopeRelation_BrowserTypeMap.put(key, subQuery);
        return "browserTypeId_NotInScopeRelation_BrowserType." + key;
    }

    /** 
     * Add order-by as ascend. <br />
     * BROWSER_TYPE_ID: {IX, NotNull, BIGINT(19), FK to BROWSER_TYPE}
     * @return this. (NotNull)
     */
    public BsDataConfigToBrowserTypeMappingCQ addOrderBy_BrowserTypeId_Asc() {
        regOBA("BROWSER_TYPE_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * BROWSER_TYPE_ID: {IX, NotNull, BIGINT(19), FK to BROWSER_TYPE}
     * @return this. (NotNull)
     */
    public BsDataConfigToBrowserTypeMappingCQ addOrderBy_BrowserTypeId_Desc() {
        regOBD("BROWSER_TYPE_ID");
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
    public BsDataConfigToBrowserTypeMappingCQ addSpecifiedDerivedOrderBy_Asc(
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
    public BsDataConfigToBrowserTypeMappingCQ addSpecifiedDerivedOrderBy_Desc(
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
        final DataConfigToBrowserTypeMappingCQ baseQuery = (DataConfigToBrowserTypeMappingCQ) baseQueryAsSuper;
        final DataConfigToBrowserTypeMappingCQ unionQuery = (DataConfigToBrowserTypeMappingCQ) unionQueryAsSuper;
        if (baseQuery.hasConditionQueryDataCrawlingConfig()) {
            unionQuery.queryDataCrawlingConfig().reflectRelationOnUnionQuery(
                    baseQuery.queryDataCrawlingConfig(),
                    unionQuery.queryDataCrawlingConfig());
        }
        if (baseQuery.hasConditionQueryBrowserType()) {
            unionQuery.queryBrowserType()
                    .reflectRelationOnUnionQuery(baseQuery.queryBrowserType(),
                            unionQuery.queryBrowserType());
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
                "DATA_CONFIG_TO_BROWSER_TYPE_MAPPING", "dataCrawlingConfig");
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
     * BROWSER_TYPE by my BROWSER_TYPE_ID, named 'browserType'.
     * @return The instance of condition-query. (NotNull)
     */
    public BrowserTypeCQ queryBrowserType() {
        return getConditionQueryBrowserType();
    }

    protected BrowserTypeCQ _conditionQueryBrowserType;

    public BrowserTypeCQ getConditionQueryBrowserType() {
        if (_conditionQueryBrowserType == null) {
            _conditionQueryBrowserType = xcreateQueryBrowserType();
            xsetupOuterJoinBrowserType();
        }
        return _conditionQueryBrowserType;
    }

    protected BrowserTypeCQ xcreateQueryBrowserType() {
        final String nrp = resolveNextRelationPath(
                "DATA_CONFIG_TO_BROWSER_TYPE_MAPPING", "browserType");
        final String jan = resolveJoinAliasName(nrp, xgetNextNestLevel());
        final BrowserTypeCQ cq = new BrowserTypeCQ(this, xgetSqlClause(), jan,
                xgetNextNestLevel());
        cq.xsetBaseCB(_baseCB);
        cq.xsetForeignPropertyName("browserType");
        cq.xsetRelationPath(nrp);
        return cq;
    }

    protected void xsetupOuterJoinBrowserType() {
        final BrowserTypeCQ cq = getConditionQueryBrowserType();
        final Map<String, String> joinOnMap = newLinkedHashMapSized(4);
        joinOnMap.put("BROWSER_TYPE_ID", "ID");
        registerOuterJoin(cq, joinOnMap, "browserType");
    }

    public boolean hasConditionQueryBrowserType() {
        return _conditionQueryBrowserType != null;
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    protected Map<String, DataConfigToBrowserTypeMappingCQ> _scalarConditionMap;

    public Map<String, DataConfigToBrowserTypeMappingCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(
            final DataConfigToBrowserTypeMappingCQ subQuery) {
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
    protected Map<String, DataConfigToBrowserTypeMappingCQ> _specifyMyselfDerivedMap;

    public Map<String, DataConfigToBrowserTypeMappingCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(
            final DataConfigToBrowserTypeMappingCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, DataConfigToBrowserTypeMappingCQ> _queryMyselfDerivedMap;

    public Map<String, DataConfigToBrowserTypeMappingCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(
            final DataConfigToBrowserTypeMappingCQ subQuery) {
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
    protected Map<String, DataConfigToBrowserTypeMappingCQ> _myselfExistsMap;

    public Map<String, DataConfigToBrowserTypeMappingCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(
            final DataConfigToBrowserTypeMappingCQ subQuery) {
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
    protected Map<String, DataConfigToBrowserTypeMappingCQ> _myselfInScopeMap;

    public Map<String, DataConfigToBrowserTypeMappingCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(
            final DataConfigToBrowserTypeMappingCQ subQuery) {
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
        return DataConfigToBrowserTypeMappingCB.class.getName();
    }

    protected String xCQ() {
        return DataConfigToBrowserTypeMappingCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
