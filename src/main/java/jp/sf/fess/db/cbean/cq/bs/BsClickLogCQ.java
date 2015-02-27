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

import jp.sf.fess.db.cbean.ClickLogCB;
import jp.sf.fess.db.cbean.cq.ClickLogCQ;
import jp.sf.fess.db.cbean.cq.SearchLogCQ;
import jp.sf.fess.db.cbean.cq.ciq.ClickLogCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.chelper.HpCalculator;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of CLICK_LOG.
 * @author DBFlute(AutoGenerator)
 */
public class BsClickLogCQ extends AbstractBsClickLogCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected ClickLogCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsClickLogCQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from CLICK_LOG) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public ClickLogCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected ClickLogCIQ xcreateCIQ() {
        final ClickLogCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected ClickLogCIQ xnewCIQ() {
        return new ClickLogCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join CLICK_LOG on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public ClickLogCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final ClickLogCIQ inlineQuery = inline();
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
    public BsClickLogCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsClickLogCQ addOrderBy_Id_Desc() {
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

    public Map<String, SearchLogCQ> getSearchId_InScopeRelation_SearchLog() {
        return xgetSQueMap("searchId_InScopeRelation_SearchLog");
    }

    @Override
    public String keepSearchId_InScopeRelation_SearchLog(final SearchLogCQ sq) {
        return xkeepSQue("searchId_InScopeRelation_SearchLog", sq);
    }

    public Map<String, SearchLogCQ> getSearchId_NotInScopeRelation_SearchLog() {
        return xgetSQueMap("searchId_NotInScopeRelation_SearchLog");
    }

    @Override
    public String keepSearchId_NotInScopeRelation_SearchLog(final SearchLogCQ sq) {
        return xkeepSQue("searchId_NotInScopeRelation_SearchLog", sq);
    }

    /**
     * Add order-by as ascend. <br />
     * SEARCH_ID: {IX, NotNull, BIGINT(19), FK to SEARCH_LOG}
     * @return this. (NotNull)
     */
    public BsClickLogCQ addOrderBy_SearchId_Asc() {
        regOBA("SEARCH_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * SEARCH_ID: {IX, NotNull, BIGINT(19), FK to SEARCH_LOG}
     * @return this. (NotNull)
     */
    public BsClickLogCQ addOrderBy_SearchId_Desc() {
        regOBD("SEARCH_ID");
        return this;
    }

    protected ConditionValue _url;

    public ConditionValue getUrl() {
        if (_url == null) {
            _url = nCV();
        }
        return _url;
    }

    @Override
    protected ConditionValue getCValueUrl() {
        return getUrl();
    }

    /**
     * Add order-by as ascend. <br />
     * URL: {IX, NotNull, VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsClickLogCQ addOrderBy_Url_Asc() {
        regOBA("URL");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * URL: {IX, NotNull, VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsClickLogCQ addOrderBy_Url_Desc() {
        regOBD("URL");
        return this;
    }

    protected ConditionValue _requestedTime;

    public ConditionValue getRequestedTime() {
        if (_requestedTime == null) {
            _requestedTime = nCV();
        }
        return _requestedTime;
    }

    @Override
    protected ConditionValue getCValueRequestedTime() {
        return getRequestedTime();
    }

    /**
     * Add order-by as ascend. <br />
     * REQUESTED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsClickLogCQ addOrderBy_RequestedTime_Asc() {
        regOBA("REQUESTED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * REQUESTED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsClickLogCQ addOrderBy_RequestedTime_Desc() {
        regOBD("REQUESTED_TIME");
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
    public BsClickLogCQ addSpecifiedDerivedOrderBy_Asc(final String aliasName) {
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
    public BsClickLogCQ addSpecifiedDerivedOrderBy_Desc(final String aliasName) {
        registerSpecifiedDerivedOrderBy_Desc(aliasName);
        return this;
    }

    // ===================================================================================
    //                                                                         Union Query
    //                                                                         ===========
    @Override
    public void reflectRelationOnUnionQuery(final ConditionQuery bqs,
            final ConditionQuery uqs) {
        final ClickLogCQ bq = (ClickLogCQ) bqs;
        final ClickLogCQ uq = (ClickLogCQ) uqs;
        if (bq.hasConditionQuerySearchLog()) {
            uq.querySearchLog().reflectRelationOnUnionQuery(
                    bq.querySearchLog(), uq.querySearchLog());
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

    public SearchLogCQ getConditionQuerySearchLog() {
        final String prop = "searchLog";
        if (!xhasQueRlMap(prop)) {
            xregQueRl(prop, xcreateQuerySearchLog());
            xsetupOuterJoinSearchLog();
        }
        return xgetQueRlMap(prop);
    }

    protected SearchLogCQ xcreateQuerySearchLog() {
        final String nrp = xresolveNRP("CLICK_LOG", "searchLog");
        final String jan = xresolveJAN(nrp, xgetNNLvl());
        return xinitRelCQ(new SearchLogCQ(this, xgetSqlClause(), jan,
                xgetNNLvl()), _baseCB, "searchLog", nrp);
    }

    protected void xsetupOuterJoinSearchLog() {
        xregOutJo("searchLog");
    }

    public boolean hasConditionQuerySearchLog() {
        return xhasQueRlMap("searchLog");
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    public Map<String, ClickLogCQ> getScalarCondition() {
        return xgetSQueMap("scalarCondition");
    }

    @Override
    public String keepScalarCondition(final ClickLogCQ sq) {
        return xkeepSQue("scalarCondition", sq);
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public Map<String, ClickLogCQ> getSpecifyMyselfDerived() {
        return xgetSQueMap("specifyMyselfDerived");
    }

    @Override
    public String keepSpecifyMyselfDerived(final ClickLogCQ sq) {
        return xkeepSQue("specifyMyselfDerived", sq);
    }

    public Map<String, ClickLogCQ> getQueryMyselfDerived() {
        return xgetSQueMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerived(final ClickLogCQ sq) {
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
    protected Map<String, ClickLogCQ> _myselfExistsMap;

    public Map<String, ClickLogCQ> getMyselfExists() {
        return xgetSQueMap("myselfExists");
    }

    @Override
    public String keepMyselfExists(final ClickLogCQ sq) {
        return xkeepSQue("myselfExists", sq);
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    public Map<String, ClickLogCQ> getMyselfInScope() {
        return xgetSQueMap("myselfInScope");
    }

    @Override
    public String keepMyselfInScope(final ClickLogCQ sq) {
        return xkeepSQue("myselfInScope", sq);
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() {
        return ClickLogCB.class.getName();
    }

    protected String xCQ() {
        return ClickLogCQ.class.getName();
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
