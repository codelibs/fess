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

import jp.sf.fess.db.cbean.CrawlingSessionInfoCB;
import jp.sf.fess.db.cbean.cq.CrawlingSessionCQ;
import jp.sf.fess.db.cbean.cq.CrawlingSessionInfoCQ;
import jp.sf.fess.db.cbean.cq.ciq.CrawlingSessionInfoCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of CRAWLING_SESSION_INFO.
 * @author DBFlute(AutoGenerator)
 */
public class BsCrawlingSessionInfoCQ extends AbstractBsCrawlingSessionInfoCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected CrawlingSessionInfoCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsCrawlingSessionInfoCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from CRAWLING_SESSION_INFO) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public CrawlingSessionInfoCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected CrawlingSessionInfoCIQ xcreateCIQ() {
        final CrawlingSessionInfoCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected CrawlingSessionInfoCIQ xnewCIQ() {
        return new CrawlingSessionInfoCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join CRAWLING_SESSION_INFO on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #FD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public CrawlingSessionInfoCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final CrawlingSessionInfoCIQ inlineQuery = inline();
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
    public BsCrawlingSessionInfoCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsCrawlingSessionInfoCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _crawlingSessionId;

    public ConditionValue getCrawlingSessionId() {
        if (_crawlingSessionId == null) {
            _crawlingSessionId = nCV();
        }
        return _crawlingSessionId;
    }

    @Override
    protected ConditionValue getCValueCrawlingSessionId() {
        return getCrawlingSessionId();
    }

    protected Map<String, CrawlingSessionCQ> _crawlingSessionId_InScopeRelation_CrawlingSessionMap;

    public Map<String, CrawlingSessionCQ> getCrawlingSessionId_InScopeRelation_CrawlingSession() {
        return _crawlingSessionId_InScopeRelation_CrawlingSessionMap;
    }

    @Override
    public String keepCrawlingSessionId_InScopeRelation_CrawlingSession(
            final CrawlingSessionCQ subQuery) {
        if (_crawlingSessionId_InScopeRelation_CrawlingSessionMap == null) {
            _crawlingSessionId_InScopeRelation_CrawlingSessionMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_crawlingSessionId_InScopeRelation_CrawlingSessionMap.size() + 1);
        _crawlingSessionId_InScopeRelation_CrawlingSessionMap
                .put(key, subQuery);
        return "crawlingSessionId_InScopeRelation_CrawlingSession." + key;
    }

    protected Map<String, CrawlingSessionCQ> _crawlingSessionId_NotInScopeRelation_CrawlingSessionMap;

    public Map<String, CrawlingSessionCQ> getCrawlingSessionId_NotInScopeRelation_CrawlingSession() {
        return _crawlingSessionId_NotInScopeRelation_CrawlingSessionMap;
    }

    @Override
    public String keepCrawlingSessionId_NotInScopeRelation_CrawlingSession(
            final CrawlingSessionCQ subQuery) {
        if (_crawlingSessionId_NotInScopeRelation_CrawlingSessionMap == null) {
            _crawlingSessionId_NotInScopeRelation_CrawlingSessionMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_crawlingSessionId_NotInScopeRelation_CrawlingSessionMap
                        .size() + 1);
        _crawlingSessionId_NotInScopeRelation_CrawlingSessionMap.put(key,
                subQuery);
        return "crawlingSessionId_NotInScopeRelation_CrawlingSession." + key;
    }

    /**
     * Add order-by as ascend. <br />
     * CRAWLING_SESSION_ID: {IX, NotNull, BIGINT(19), FK to CRAWLING_SESSION}
     * @return this. (NotNull)
     */
    public BsCrawlingSessionInfoCQ addOrderBy_CrawlingSessionId_Asc() {
        regOBA("CRAWLING_SESSION_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CRAWLING_SESSION_ID: {IX, NotNull, BIGINT(19), FK to CRAWLING_SESSION}
     * @return this. (NotNull)
     */
    public BsCrawlingSessionInfoCQ addOrderBy_CrawlingSessionId_Desc() {
        regOBD("CRAWLING_SESSION_ID");
        return this;
    }

    protected ConditionValue _key;

    public ConditionValue getKey() {
        if (_key == null) {
            _key = nCV();
        }
        return _key;
    }

    @Override
    protected ConditionValue getCValueKey() {
        return getKey();
    }

    /**
     * Add order-by as ascend. <br />
     * KEY: {NotNull, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsCrawlingSessionInfoCQ addOrderBy_Key_Asc() {
        regOBA("KEY");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * KEY: {NotNull, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsCrawlingSessionInfoCQ addOrderBy_Key_Desc() {
        regOBD("KEY");
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
     * VALUE: {NotNull, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsCrawlingSessionInfoCQ addOrderBy_Value_Asc() {
        regOBA("VALUE");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * VALUE: {NotNull, VARCHAR(100)}
     * @return this. (NotNull)
     */
    public BsCrawlingSessionInfoCQ addOrderBy_Value_Desc() {
        regOBD("VALUE");
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
    public BsCrawlingSessionInfoCQ addOrderBy_CreatedTime_Asc() {
        regOBA("CREATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsCrawlingSessionInfoCQ addOrderBy_CreatedTime_Desc() {
        regOBD("CREATED_TIME");
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
    public BsCrawlingSessionInfoCQ addSpecifiedDerivedOrderBy_Asc(
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
    public BsCrawlingSessionInfoCQ addSpecifiedDerivedOrderBy_Desc(
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
        final CrawlingSessionInfoCQ baseQuery = (CrawlingSessionInfoCQ) baseQueryAsSuper;
        final CrawlingSessionInfoCQ unionQuery = (CrawlingSessionInfoCQ) unionQueryAsSuper;
        if (baseQuery.hasConditionQueryCrawlingSession()) {
            unionQuery.queryCrawlingSession().reflectRelationOnUnionQuery(
                    baseQuery.queryCrawlingSession(),
                    unionQuery.queryCrawlingSession());
        }
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    /**
     * Get the condition-query for relation table. <br />
     * CRAWLING_SESSION by my CRAWLING_SESSION_ID, named 'crawlingSession'.
     * @return The instance of condition-query. (NotNull)
     */
    public CrawlingSessionCQ queryCrawlingSession() {
        return getConditionQueryCrawlingSession();
    }

    protected CrawlingSessionCQ _conditionQueryCrawlingSession;

    public CrawlingSessionCQ getConditionQueryCrawlingSession() {
        if (_conditionQueryCrawlingSession == null) {
            _conditionQueryCrawlingSession = xcreateQueryCrawlingSession();
            xsetupOuterJoinCrawlingSession();
        }
        return _conditionQueryCrawlingSession;
    }

    protected CrawlingSessionCQ xcreateQueryCrawlingSession() {
        final String nrp = resolveNextRelationPath("CRAWLING_SESSION_INFO",
                "crawlingSession");
        final String jan = resolveJoinAliasName(nrp, xgetNextNestLevel());
        final CrawlingSessionCQ cq = new CrawlingSessionCQ(this,
                xgetSqlClause(), jan, xgetNextNestLevel());
        cq.xsetBaseCB(_baseCB);
        cq.xsetForeignPropertyName("crawlingSession");
        cq.xsetRelationPath(nrp);
        return cq;
    }

    protected void xsetupOuterJoinCrawlingSession() {
        final CrawlingSessionCQ cq = getConditionQueryCrawlingSession();
        final Map<String, String> joinOnMap = newLinkedHashMapSized(4);
        joinOnMap.put("CRAWLING_SESSION_ID", "ID");
        registerOuterJoin(cq, joinOnMap, "crawlingSession");
    }

    public boolean hasConditionQueryCrawlingSession() {
        return _conditionQueryCrawlingSession != null;
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    protected Map<String, CrawlingSessionInfoCQ> _scalarConditionMap;

    public Map<String, CrawlingSessionInfoCQ> getScalarCondition() {
        return _scalarConditionMap;
    }

    @Override
    public String keepScalarCondition(final CrawlingSessionInfoCQ subQuery) {
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
    protected Map<String, CrawlingSessionInfoCQ> _specifyMyselfDerivedMap;

    public Map<String, CrawlingSessionInfoCQ> getSpecifyMyselfDerived() {
        return _specifyMyselfDerivedMap;
    }

    @Override
    public String keepSpecifyMyselfDerived(final CrawlingSessionInfoCQ subQuery) {
        if (_specifyMyselfDerivedMap == null) {
            _specifyMyselfDerivedMap = newLinkedHashMapSized(4);
        }
        final String key = "subQueryMapKey"
                + (_specifyMyselfDerivedMap.size() + 1);
        _specifyMyselfDerivedMap.put(key, subQuery);
        return "specifyMyselfDerived." + key;
    }

    protected Map<String, CrawlingSessionInfoCQ> _queryMyselfDerivedMap;

    public Map<String, CrawlingSessionInfoCQ> getQueryMyselfDerived() {
        return _queryMyselfDerivedMap;
    }

    @Override
    public String keepQueryMyselfDerived(final CrawlingSessionInfoCQ subQuery) {
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
    protected Map<String, CrawlingSessionInfoCQ> _myselfExistsMap;

    public Map<String, CrawlingSessionInfoCQ> getMyselfExists() {
        return _myselfExistsMap;
    }

    @Override
    public String keepMyselfExists(final CrawlingSessionInfoCQ subQuery) {
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
    protected Map<String, CrawlingSessionInfoCQ> _myselfInScopeMap;

    public Map<String, CrawlingSessionInfoCQ> getMyselfInScope() {
        return _myselfInScopeMap;
    }

    @Override
    public String keepMyselfInScope(final CrawlingSessionInfoCQ subQuery) {
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
        return CrawlingSessionInfoCB.class.getName();
    }

    protected String xCQ() {
        return CrawlingSessionInfoCQ.class.getName();
    }

    protected String xMap() {
        return Map.class.getName();
    }
}
