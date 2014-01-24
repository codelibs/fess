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

import jp.sf.fess.db.cbean.RequestHeaderCB;
import jp.sf.fess.db.cbean.cq.RequestHeaderCQ;
import jp.sf.fess.db.cbean.cq.WebCrawlingConfigCQ;
import jp.sf.fess.db.cbean.cq.ciq.RequestHeaderCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of REQUEST_HEADER.
 * @author DBFlute(AutoGenerator)
 */
public class BsRequestHeaderCQ extends AbstractBsRequestHeaderCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected RequestHeaderCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsRequestHeaderCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from REQUEST_HEADER) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public RequestHeaderCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected RequestHeaderCIQ xcreateCIQ() {
        final RequestHeaderCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected RequestHeaderCIQ xnewCIQ() {
        return new RequestHeaderCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join REQUEST_HEADER on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public RequestHeaderCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final RequestHeaderCIQ inlineQuery = inline();
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
    public BsRequestHeaderCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsRequestHeaderCQ addOrderBy_Id_Desc() {
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
    public BsRequestHeaderCQ addOrderBy_Name_Asc() {
        regOBA("NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * NAME: {NotNull, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsRequestHeaderCQ addOrderBy_Name_Desc() {
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
     * VALUE: {NotNull, VARCHAR(1000)}
     * @return this. (NotNull)
     */
    public BsRequestHeaderCQ addOrderBy_Value_Asc() {
        regOBA("VALUE");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * VALUE: {NotNull, VARCHAR(1000)}
     * @return this. (NotNull)
     */
    public BsRequestHeaderCQ addOrderBy_Value_Desc() {
        regOBD("VALUE");
        return this;
    }

    protected ConditionValue _webCrawlingConfigId;

    public ConditionValue getWebCrawlingConfigId() {
        if (_webCrawlingConfigId == null) {
            _webCrawlingConfigId = nCV();
        }
        return _webCrawlingConfigId;
    }

    @Override
    protected ConditionValue getCValueWebCrawlingConfigId() {
        return getWebCrawlingConfigId();
    }

    protected Map<String, WebCrawlingConfigCQ> _webCrawlingConfigId_InScopeRelation_WebCrawlingConfigMap;

    public Map<String, WebCrawlingConfigCQ> getWebCrawlingConfigId_InScopeRelation_WebCrawlingConfig() {
        return _webCrawlingConfigId_InScopeRelation_WebCrawlingConfigMap;
    }

    @Override
    public String keepWebCrawlingConfigId_InScopeRelation_WebCrawlingConfig(
            final WebCrawlingConfigCQ subQuery) {
        if (_webCrawlingConfigId_InScopeRelation_WebCrawlingConfigMap == null) {
            _webCrawlingConfigId_InScopeRelation_WebCrawlingConfigMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_webCrawlingConfigId_InScopeRelation_WebCrawlingConfigMap
                        .size() + 1);
        _webCrawlingConfigId_InScopeRelation_WebCrawlingConfigMap.put(key,
                subQuery);
        return "webCrawlingConfigId_InScopeRelation_WebCrawlingConfig." + key;
    }

    protected Map<String, WebCrawlingConfigCQ> _webCrawlingConfigId_NotInScopeRelation_WebCrawlingConfigMap;

    public Map<String, WebCrawlingConfigCQ> getWebCrawlingConfigId_NotInScopeRelation_WebCrawlingConfig() {
        return _webCrawlingConfigId_NotInScopeRelation_WebCrawlingConfigMap;
    }

    @Override
    public String keepWebCrawlingConfigId_NotInScopeRelation_WebCrawlingConfig(
            final WebCrawlingConfigCQ subQuery) {
        if (_webCrawlingConfigId_NotInScopeRelation_WebCrawlingConfigMap == null) {
            _webCrawlingConfigId_NotInScopeRelation_WebCrawlingConfigMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_webCrawlingConfigId_NotInScopeRelation_WebCrawlingConfigMap
                        .size() + 1);
        _webCrawlingConfigId_NotInScopeRelation_WebCrawlingConfigMap.put(key,
                subQuery);
        return "webCrawlingConfigId_NotInScopeRelation_WebCrawlingConfig."
                + key;
    }

    /** 
     * Add order-by as ascend. <br />
     * WEB_CRAWLING_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsRequestHeaderCQ addOrderBy_WebCrawlingConfigId_Asc() {
        regOBA("WEB_CRAWLING_CONFIG_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * WEB_CRAWLING_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to WEB_CRAWLING_CONFIG}
     * @return this. (NotNull)
     */
    public BsRequestHeaderCQ addOrderBy_WebCrawlingConfigId_Desc() {
        regOBD("WEB_CRAWLING_CONFIG_ID");
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
    public BsRequestHeaderCQ addOrderBy_CreatedBy_Asc() {
        regOBA("CREATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsRequestHeaderCQ addOrderBy_CreatedBy_Desc() {
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
    public BsRequestHeaderCQ addOrderBy_CreatedTime_Asc() {
        regOBA("CREATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsRequestHeaderCQ addOrderBy_CreatedTime_Desc() {
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
    public BsRequestHeaderCQ addOrderBy_UpdatedBy_Asc() {
        regOBA("UPDATED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsRequestHeaderCQ addOrderBy_UpdatedBy_Desc() {
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
    public BsRequestHeaderCQ addOrderBy_UpdatedTime_Asc() {
        regOBA("UPDATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsRequestHeaderCQ addOrderBy_UpdatedTime_Desc() {
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
    public BsRequestHeaderCQ addOrderBy_DeletedBy_Asc() {
        regOBA("DELETED_BY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_BY: {VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsRequestHeaderCQ addOrderBy_DeletedBy_Desc() {
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
    public BsRequestHeaderCQ addOrderBy_DeletedTime_Asc() {
        regOBA("DELETED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsRequestHeaderCQ addOrderBy_DeletedTime_Desc() {
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
    public BsRequestHeaderCQ addOrderBy_VersionNo_Asc() {
        regOBA("VERSION_NO");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @return this. (NotNull)
     */
    public BsRequestHeaderCQ addOrderBy_VersionNo_Desc() {
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
     * }, <span style="color: #FD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] asc</span>
     * cb.<span style="color: #FD4747">addSpecifiedDerivedOrderBy_Asc</span>(<span style="color: #FD4747">aliasName</span>);
     * </pre>
     * @param aliasName The alias name specified at (Specify)DerivedReferrer. (NotNull)
     * @return this. (NotNull)
     */
    public BsRequestHeaderCQ addSpecifiedDerivedOrderBy_Asc(
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
    public BsRequestHeaderCQ addSpecifiedDerivedOrderBy_Desc(
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
        final RequestHeaderCQ baseQuery = (RequestHeaderCQ) baseQueryAsSuper;
        final RequestHeaderCQ unionQuery = (RequestHeaderCQ) unionQueryAsSuper;
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
     * WEB_CRAWLING_CONFIG by my WEB_CRAWLING_CONFIG_ID, named 'webCrawlingConfig'.
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
        final String nrp = resolveNextRelationPath("REQUEST_HEADER",
                "webCrawlingConfig");
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
        joinOnMap.put("WEB_CRAWLING_CONFIG_ID", "ID");
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
    protected Map<String, RequestHeaderCQ> _scalarConditionMap;

    public Map<String, RequestHeaderCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(final RequestHeaderCQ subQuery) {
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
    protected Map<String, RequestHeaderCQ> _specifyMyselfDerivedMap;

    public Map<String, RequestHeaderCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(final RequestHeaderCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, RequestHeaderCQ> _queryMyselfDerivedMap;

    public Map<String, RequestHeaderCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(final RequestHeaderCQ subQuery) {
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
    protected Map<String, RequestHeaderCQ> _myselfExistsMap;

    public Map<String, RequestHeaderCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final RequestHeaderCQ subQuery) {
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
    protected Map<String, RequestHeaderCQ> _myselfInScopeMap;

    public Map<String, RequestHeaderCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(final RequestHeaderCQ subQuery) {
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
        return RequestHeaderCB.class.getName();
    }

    protected String xCQ() {
        return RequestHeaderCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
