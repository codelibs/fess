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

import jp.sf.fess.db.cbean.SearchFieldLogCB;
import jp.sf.fess.db.cbean.cq.SearchFieldLogCQ;
import jp.sf.fess.db.cbean.cq.SearchLogCQ;
import jp.sf.fess.db.cbean.cq.ciq.SearchFieldLogCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of SEARCH_FIELD_LOG.
 * @author DBFlute(AutoGenerator)
 */
public class BsSearchFieldLogCQ extends AbstractBsSearchFieldLogCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected SearchFieldLogCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsSearchFieldLogCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from SEARCH_FIELD_LOG) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public SearchFieldLogCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected SearchFieldLogCIQ xcreateCIQ() {
        final SearchFieldLogCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected SearchFieldLogCIQ xnewCIQ() {
        return new SearchFieldLogCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join SEARCH_FIELD_LOG on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public SearchFieldLogCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final SearchFieldLogCIQ inlineQuery = inline();
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
    public BsSearchFieldLogCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsSearchFieldLogCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _searchId;

    public ConditionValue getSearchId() {
        if (_searchId == null) {
            _searchId = nCV();
        }
        return _searchId;
    }

    @Override
    protected ConditionValue getCValueSearchId() {
        return getSearchId();
    }

    protected Map<String, SearchLogCQ> _searchId_InScopeRelation_SearchLogMap;

    public Map<String, SearchLogCQ> getSearchId_InScopeRelation_SearchLog() {
        return _searchId_InScopeRelation_SearchLogMap;
    }

    @Override
    public String keepSearchId_InScopeRelation_SearchLog(
            final SearchLogCQ subQuery) {
        if (_searchId_InScopeRelation_SearchLogMap == null) {
            _searchId_InScopeRelation_SearchLogMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_searchId_InScopeRelation_SearchLogMap.size() + 1);
        _searchId_InScopeRelation_SearchLogMap.put(key, subQuery);
        return "searchId_InScopeRelation_SearchLog." + key;
    }

    protected Map<String, SearchLogCQ> _searchId_NotInScopeRelation_SearchLogMap;

    public Map<String, SearchLogCQ> getSearchId_NotInScopeRelation_SearchLog() {
        return _searchId_NotInScopeRelation_SearchLogMap;
    }

    @Override
    public String keepSearchId_NotInScopeRelation_SearchLog(
            final SearchLogCQ subQuery) {
        if (_searchId_NotInScopeRelation_SearchLogMap == null) {
            _searchId_NotInScopeRelation_SearchLogMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_searchId_NotInScopeRelation_SearchLogMap.size() + 1);
        _searchId_NotInScopeRelation_SearchLogMap.put(key, subQuery);
        return "searchId_NotInScopeRelation_SearchLog." + key;
    }

    /**
     * Add order-by as ascend. <br />
     * SEARCH_ID: {IX, NotNull, BIGINT(19), FK to SEARCH_LOG}
     * @return this. (NotNull)
     */
    public BsSearchFieldLogCQ addOrderBy_SearchId_Asc() {
        regOBA("SEARCH_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * SEARCH_ID: {IX, NotNull, BIGINT(19), FK to SEARCH_LOG}
     * @return this. (NotNull)
     */
    public BsSearchFieldLogCQ addOrderBy_SearchId_Desc() {
        regOBD("SEARCH_ID");
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
     * NAME: {IX, NotNull, VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsSearchFieldLogCQ addOrderBy_Name_Asc() {
        regOBA("NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * NAME: {IX, NotNull, VARCHAR(255)}
     * @return this. (NotNull)
     */
    public BsSearchFieldLogCQ addOrderBy_Name_Desc() {
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
    public BsSearchFieldLogCQ addOrderBy_Value_Asc() {
        regOBA("VALUE");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * VALUE: {NotNull, VARCHAR(1000)}
     * @return this. (NotNull)
     */
    public BsSearchFieldLogCQ addOrderBy_Value_Desc() {
        regOBD("VALUE");
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
    public BsSearchFieldLogCQ addSpecifiedDerivedOrderBy_Asc(
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
    public BsSearchFieldLogCQ addSpecifiedDerivedOrderBy_Desc(
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
        final SearchFieldLogCQ baseQuery = (SearchFieldLogCQ) baseQueryAsSuper;
        final SearchFieldLogCQ unionQuery = (SearchFieldLogCQ) unionQueryAsSuper;
        if (baseQuery.hasConditionQuerySearchLog()) {
            unionQuery.querySearchLog().reflectRelationOnUnionQuery(
                    baseQuery.querySearchLog(), unionQuery.querySearchLog());
        }
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    /**
     * Get the condition-query for relation table. <br />
     * SEARCH_LOG by my SEARCH_ID, named 'searchLog'.
     * @return The instance of condition-query. (NotNull)
     */
    public SearchLogCQ querySearchLog() {
        return getConditionQuerySearchLog();
    }

    protected SearchLogCQ _conditionQuerySearchLog;

    public SearchLogCQ getConditionQuerySearchLog() {
        if (_conditionQuerySearchLog == null) {
            _conditionQuerySearchLog = xcreateQuerySearchLog();
            xsetupOuterJoinSearchLog();
        }
        return _conditionQuerySearchLog;
    }

    protected SearchLogCQ xcreateQuerySearchLog() {
        final String nrp = resolveNextRelationPath("SEARCH_FIELD_LOG",
                "searchLog");
        final String jan = resolveJoinAliasName(nrp, xgetNextNestLevel());
        final SearchLogCQ cq = new SearchLogCQ(this, xgetSqlClause(), jan,
                xgetNextNestLevel());
        cq.xsetBaseCB(_baseCB);
        cq.xsetForeignPropertyName("searchLog");
        cq.xsetRelationPath(nrp);
        return cq;
    }

    protected void xsetupOuterJoinSearchLog() {
        final SearchLogCQ cq = getConditionQuerySearchLog();
        final Map<String, String> joinOnMap = newLinkedHashMapSized(4);
        joinOnMap.put("SEARCH_ID", "ID");
        registerOuterJoin(cq, joinOnMap, "searchLog");
    }

    public boolean hasConditionQuerySearchLog() {
        return _conditionQuerySearchLog != null;
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    protected Map<String, SearchFieldLogCQ> _scalarConditionMap;

    public Map<String, SearchFieldLogCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(final SearchFieldLogCQ subQuery) {
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
    protected Map<String, SearchFieldLogCQ> _specifyMyselfDerivedMap;

    public Map<String, SearchFieldLogCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(final SearchFieldLogCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, SearchFieldLogCQ> _queryMyselfDerivedMap;

    public Map<String, SearchFieldLogCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(final SearchFieldLogCQ subQuery) {
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
    protected Map<String, SearchFieldLogCQ> _myselfExistsMap;

    public Map<String, SearchFieldLogCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final SearchFieldLogCQ subQuery) {
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
    protected Map<String, SearchFieldLogCQ> _myselfInScopeMap;

    public Map<String, SearchFieldLogCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(final SearchFieldLogCQ subQuery) {
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
        return SearchFieldLogCB.class.getName();
    }

    protected String xCQ() {
        return SearchFieldLogCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
