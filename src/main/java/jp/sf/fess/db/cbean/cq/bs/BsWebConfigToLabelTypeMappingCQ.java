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

import jp.sf.fess.db.cbean.WebConfigToLabelTypeMappingCB;
import jp.sf.fess.db.cbean.cq.LabelTypeCQ;
import jp.sf.fess.db.cbean.cq.WebConfigToLabelTypeMappingCQ;
import jp.sf.fess.db.cbean.cq.WebCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.ciq.WebConfigToLabelTypeMappingCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of WEB_CONFIG_TO_LABEL_TYPE_MAPPING.
 * @author DBFlute(AutoGenerator)
 */
public class BsWebConfigToLabelTypeMappingCQ extends
        AbstractBsWebConfigToLabelTypeMappingCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected WebConfigToLabelTypeMappingCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsWebConfigToLabelTypeMappingCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from WEB_CONFIG_TO_LABEL_TYPE_MAPPING) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public WebConfigToLabelTypeMappingCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected WebConfigToLabelTypeMappingCIQ xcreateCIQ() {
        final WebConfigToLabelTypeMappingCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected WebConfigToLabelTypeMappingCIQ xnewCIQ() {
        return new WebConfigToLabelTypeMappingCIQ(xgetReferrerQuery(),
                xgetSqlClause(), xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join WEB_CONFIG_TO_LABEL_TYPE_MAPPING on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public WebConfigToLabelTypeMappingCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final WebConfigToLabelTypeMappingCIQ inlineQuery = inline();
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
    public BsWebConfigToLabelTypeMappingCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsWebConfigToLabelTypeMappingCQ addOrderBy_Id_Desc() {
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
    public BsWebConfigToLabelTypeMappingCQ addOrderBy_WebConfigId_Asc() {
        regOBA("WEB_CONFIG_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * WEB_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsWebConfigToLabelTypeMappingCQ addOrderBy_WebConfigId_Desc() {
        regOBD("WEB_CONFIG_ID");
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
    public BsWebConfigToLabelTypeMappingCQ addOrderBy_LabelTypeId_Asc() {
        regOBA("LABEL_TYPE_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * LABEL_TYPE_ID: {IX, NotNull, BIGINT(19), FK to LABEL_TYPE}
     * @return this. (NotNull)
     */
    public BsWebConfigToLabelTypeMappingCQ addOrderBy_LabelTypeId_Desc() {
        regOBD("LABEL_TYPE_ID");
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
    public BsWebConfigToLabelTypeMappingCQ addSpecifiedDerivedOrderBy_Asc(
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
    public BsWebConfigToLabelTypeMappingCQ addSpecifiedDerivedOrderBy_Desc(
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
        final WebConfigToLabelTypeMappingCQ baseQuery = (WebConfigToLabelTypeMappingCQ) baseQueryAsSuper;
        final WebConfigToLabelTypeMappingCQ unionQuery = (WebConfigToLabelTypeMappingCQ) unionQueryAsSuper;
        if (baseQuery.hasConditionQueryLabelType()) {
            unionQuery.queryLabelType().reflectRelationOnUnionQuery(
                    baseQuery.queryLabelType(), unionQuery.queryLabelType());
        }
        if (baseQuery.hasConditionQueryWebCrawlingConfig()) {
            unionQuery.queryWebCrawlingConfig().reflectRelationOnUnionQuery(
                    baseQuery.queryWebCrawlingConfig(),
                    unionQuery.queryWebCrawlingConfig());
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
                "WEB_CONFIG_TO_LABEL_TYPE_MAPPING", "labelType");
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
                "WEB_CONFIG_TO_LABEL_TYPE_MAPPING", "webCrawlingConfig");
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

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    protected Map<String, WebConfigToLabelTypeMappingCQ> _scalarConditionMap;

    public Map<String, WebConfigToLabelTypeMappingCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(
            final WebConfigToLabelTypeMappingCQ subQuery) {
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
    protected Map<String, WebConfigToLabelTypeMappingCQ> _specifyMyselfDerivedMap;

    public Map<String, WebConfigToLabelTypeMappingCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(
            final WebConfigToLabelTypeMappingCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, WebConfigToLabelTypeMappingCQ> _queryMyselfDerivedMap;

    public Map<String, WebConfigToLabelTypeMappingCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(
            final WebConfigToLabelTypeMappingCQ subQuery) {
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
    protected Map<String, WebConfigToLabelTypeMappingCQ> _myselfExistsMap;

    public Map<String, WebConfigToLabelTypeMappingCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final WebConfigToLabelTypeMappingCQ subQuery) {
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
    protected Map<String, WebConfigToLabelTypeMappingCQ> _myselfInScopeMap;

    public Map<String, WebConfigToLabelTypeMappingCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(final WebConfigToLabelTypeMappingCQ subQuery) {
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
        return WebConfigToLabelTypeMappingCB.class.getName();
    }

    protected String xCQ() {
        return WebConfigToLabelTypeMappingCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
