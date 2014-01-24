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

import jp.sf.fess.db.cbean.WebConfigToBrowserTypeMappingCB;
import jp.sf.fess.db.cbean.cq.BrowserTypeCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToBrowserTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.ciq.WebConfigToBrowserTypeMappingCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of WEB_CONFIG_TO_BROWSER_TYPE_MAPPING.
 * @author DBFlute(AutoGenerator)
 */
public class BsWebConfigToBrowserTypeMappingCQ extends
        AbstractBsWebConfigToBrowserTypeMappingCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected WebConfigToBrowserTypeMappingCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsWebConfigToBrowserTypeMappingCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from WEB_CONFIG_TO_BROWSER_TYPE_MAPPING) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public WebConfigToBrowserTypeMappingCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected WebConfigToBrowserTypeMappingCIQ xcreateCIQ() {
        final WebConfigToBrowserTypeMappingCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected WebConfigToBrowserTypeMappingCIQ xnewCIQ() {
        return new WebConfigToBrowserTypeMappingCIQ(xgetReferrerQuery(),
                xgetSqlClause(), xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join WEB_CONFIG_TO_BROWSER_TYPE_MAPPING on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public WebConfigToBrowserTypeMappingCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final WebConfigToBrowserTypeMappingCIQ inlineQuery = inline();
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
    public BsWebConfigToBrowserTypeMappingCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsWebConfigToBrowserTypeMappingCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _webConfigId;

    public ConditionValue getWebConfigId() {
        if (_webConfigId == null) {
            _webConfigId = nCV();
        }
        return _webConfigId;
    }

    @Override
    protected ConditionValue getCValueWebConfigId() {
        return getWebConfigId();
    }

    protected Map<String, WebCrawlingConfigCQ> _webConfigId_InScopeRelation_WebCrawlingConfigMap;

    public Map<String, WebCrawlingConfigCQ> getWebConfigId_InScopeRelation_WebCrawlingConfig() {
        return _webConfigId_InScopeRelation_WebCrawlingConfigMap;
    }

    @Override
    public String keepWebConfigId_InScopeRelation_WebCrawlingConfig(
            final WebCrawlingConfigCQ subQuery) {
        if (_webConfigId_InScopeRelation_WebCrawlingConfigMap == null) {
            _webConfigId_InScopeRelation_WebCrawlingConfigMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_webConfigId_InScopeRelation_WebCrawlingConfigMap.size() + 1);
        _webConfigId_InScopeRelation_WebCrawlingConfigMap.put(key, subQuery);
        return "webConfigId_InScopeRelation_WebCrawlingConfig." + key;
    }

    protected Map<String, WebCrawlingConfigCQ> _webConfigId_NotInScopeRelation_WebCrawlingConfigMap;

    public Map<String, WebCrawlingConfigCQ> getWebConfigId_NotInScopeRelation_WebCrawlingConfig() {
        return _webConfigId_NotInScopeRelation_WebCrawlingConfigMap;
    }

    @Override
    public String keepWebConfigId_NotInScopeRelation_WebCrawlingConfig(
            final WebCrawlingConfigCQ subQuery) {
        if (_webConfigId_NotInScopeRelation_WebCrawlingConfigMap == null) {
            _webConfigId_NotInScopeRelation_WebCrawlingConfigMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_webConfigId_NotInScopeRelation_WebCrawlingConfigMap.size() + 1);
        _webConfigId_NotInScopeRelation_WebCrawlingConfigMap.put(key, subQuery);
        return "webConfigId_NotInScopeRelation_WebCrawlingConfig." + key;
    }

    /** 
     * Add order-by as ascend. <br />
     * WEB_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsWebConfigToBrowserTypeMappingCQ addOrderBy_WebConfigId_Asc() {
        regOBA("WEB_CONFIG_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * WEB_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsWebConfigToBrowserTypeMappingCQ addOrderBy_WebConfigId_Desc() {
        regOBD("WEB_CONFIG_ID");
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
    public BsWebConfigToBrowserTypeMappingCQ addOrderBy_BrowserTypeId_Asc() {
        regOBA("BROWSER_TYPE_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * BROWSER_TYPE_ID: {IX, NotNull, BIGINT(19), FK to BROWSER_TYPE}
     * @return this. (NotNull)
     */
    public BsWebConfigToBrowserTypeMappingCQ addOrderBy_BrowserTypeId_Desc() {
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
    public BsWebConfigToBrowserTypeMappingCQ addSpecifiedDerivedOrderBy_Asc(
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
    public BsWebConfigToBrowserTypeMappingCQ addSpecifiedDerivedOrderBy_Desc(
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
        final WebConfigToBrowserTypeMappingCQ baseQuery = (WebConfigToBrowserTypeMappingCQ) baseQueryAsSuper;
        final WebConfigToBrowserTypeMappingCQ unionQuery = (WebConfigToBrowserTypeMappingCQ) unionQueryAsSuper;
        if (baseQuery.hasConditionQueryWebCrawlingConfig()) {
            unionQuery.queryWebCrawlingConfig().reflectRelationOnUnionQuery(
                    baseQuery.queryWebCrawlingConfig(),
                    unionQuery.queryWebCrawlingConfig());
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
     * WEB_CRAWLING_CONFIG by my WEB_CONFIG_ID, named 'webCrawlingConfig'.
     * @return The instance of condition-query. (NotNull)
     */
    public WebCrawlingConfigCQ queryWebCrawlingConfig() {
        return getConditionQueryWebCrawlingConfig();
    }

    protected WebCrawlingConfigCQ _conditionQueryWebCrawlingConfig;

    public WebCrawlingConfigCQ getConditionQueryWebCrawlingConfig() {
        if (_conditionQueryWebCrawlingConfig == null) {
            _conditionQueryWebCrawlingConfig = xcreateQueryWebCrawlingConfig();
            xsetupOuterJoinWebCrawlingConfig();
        }
        return _conditionQueryWebCrawlingConfig;
    }

    protected WebCrawlingConfigCQ xcreateQueryWebCrawlingConfig() {
        final String nrp = resolveNextRelationPath(
                "WEB_CONFIG_TO_BROWSER_TYPE_MAPPING", "webCrawlingConfig");
        final String jan = resolveJoinAliasName(nrp, xgetNextNestLevel());
        final WebCrawlingConfigCQ cq = new WebCrawlingConfigCQ(this,
                xgetSqlClause(), jan, xgetNextNestLevel());
        cq.xsetBaseCB(_baseCB);
        cq.xsetForeignPropertyName("webCrawlingConfig");
        cq.xsetRelationPath(nrp);
        return cq;
    }

    protected void xsetupOuterJoinWebCrawlingConfig() {
        final WebCrawlingConfigCQ cq = getConditionQueryWebCrawlingConfig();
        final Map<String, String> joinOnMap = newLinkedHashMapSized(4);
        joinOnMap.put("WEB_CONFIG_ID", "ID");
        registerOuterJoin(cq, joinOnMap, "webCrawlingConfig");
    }

    public boolean hasConditionQueryWebCrawlingConfig() {
        return _conditionQueryWebCrawlingConfig != null;
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
                "WEB_CONFIG_TO_BROWSER_TYPE_MAPPING", "browserType");
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
    protected Map<String, WebConfigToBrowserTypeMappingCQ> _scalarConditionMap;

    public Map<String, WebConfigToBrowserTypeMappingCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(
            final WebConfigToBrowserTypeMappingCQ subQuery) {
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
    protected Map<String, WebConfigToBrowserTypeMappingCQ> _specifyMyselfDerivedMap;

    public Map<String, WebConfigToBrowserTypeMappingCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(
            final WebConfigToBrowserTypeMappingCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, WebConfigToBrowserTypeMappingCQ> _queryMyselfDerivedMap;

    public Map<String, WebConfigToBrowserTypeMappingCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(
            final WebConfigToBrowserTypeMappingCQ subQuery) {
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
    protected Map<String, WebConfigToBrowserTypeMappingCQ> _myselfExistsMap;

    public Map<String, WebConfigToBrowserTypeMappingCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(
            final WebConfigToBrowserTypeMappingCQ subQuery) {
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
    protected Map<String, WebConfigToBrowserTypeMappingCQ> _myselfInScopeMap;

    public Map<String, WebConfigToBrowserTypeMappingCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(
            final WebConfigToBrowserTypeMappingCQ subQuery) {
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
        return WebConfigToBrowserTypeMappingCB.class.getName();
    }

    protected String xCQ() {
        return WebConfigToBrowserTypeMappingCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
