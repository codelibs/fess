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
import org.seasar.dbflute.cbean.chelper.HpCalculator;
import org.seasar.dbflute.cbean.coption.ConditionOption;
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
    public BsCrawlingSessionInfoCQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from CRAWLING_SESSION_INFO) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">inline()</span>.setFoo...;
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
     * cb.query().queryMemberStatus().<span style="color: #DD4747">on()</span>.setFoo...;
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

    public Map<String, CrawlingSessionCQ> getCrawlingSessionId_InScopeRelation_CrawlingSession() {
        return xgetSQueMap("crawlingSessionId_InScopeRelation_CrawlingSession");
    }

    @Override
    public String keepCrawlingSessionId_InScopeRelation_CrawlingSession(
            final CrawlingSessionCQ sq) {
        return xkeepSQue("crawlingSessionId_InScopeRelation_CrawlingSession",
                sq);
    }

    public Map<String, CrawlingSessionCQ> getCrawlingSessionId_NotInScopeRelation_CrawlingSession() {
        return xgetSQueMap("crawlingSessionId_NotInScopeRelation_CrawlingSession");
    }

    @Override
    public String keepCrawlingSessionId_NotInScopeRelation_CrawlingSession(
            final CrawlingSessionCQ sq) {
        return xkeepSQue(
                "crawlingSessionId_NotInScopeRelation_CrawlingSession", sq);
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
     * }, <span style="color: #DD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] asc</span>
     * cb.<span style="color: #DD4747">addSpecifiedDerivedOrderBy_Asc</span>(<span style="color: #DD4747">aliasName</span>);
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
     * }, <span style="color: #DD4747">aliasName</span>);
     * <span style="color: #3F7E5E">// order by [alias-name] desc</span>
     * cb.<span style="color: #DD4747">addSpecifiedDerivedOrderBy_Desc</span>(<span style="color: #DD4747">aliasName</span>);
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
    public void reflectRelationOnUnionQuery(final ConditionQuery bqs,
            final ConditionQuery uqs) {
        final CrawlingSessionInfoCQ bq = (CrawlingSessionInfoCQ) bqs;
        final CrawlingSessionInfoCQ uq = (CrawlingSessionInfoCQ) uqs;
        if (bq.hasConditionQueryCrawlingSession()) {
            uq.queryCrawlingSession().reflectRelationOnUnionQuery(
                    bq.queryCrawlingSession(), uq.queryCrawlingSession());
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

    public CrawlingSessionCQ getConditionQueryCrawlingSession() {
        final String prop = "crawlingSession";
        if (!xhasQueRlMap(prop)) {
            xregQueRl(prop, xcreateQueryCrawlingSession());
            xsetupOuterJoinCrawlingSession();
        }
        return xgetQueRlMap(prop);
    }

    protected CrawlingSessionCQ xcreateQueryCrawlingSession() {
        final String nrp = xresolveNRP("CRAWLING_SESSION_INFO",
                "crawlingSession");
        final String jan = xresolveJAN(nrp, xgetNNLvl());
        return xinitRelCQ(new CrawlingSessionCQ(this, xgetSqlClause(), jan,
                xgetNNLvl()), _baseCB, "crawlingSession", nrp);
    }

    protected void xsetupOuterJoinCrawlingSession() {
        xregOutJo("crawlingSession");
    }

    public boolean hasConditionQueryCrawlingSession() {
        return xhasQueRlMap("crawlingSession");
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    public Map<String, CrawlingSessionInfoCQ> getScalarCondition() {
        return xgetSQueMap("scalarCondition");
    }

    @Override
    public String keepScalarCondition(final CrawlingSessionInfoCQ sq) {
        return xkeepSQue("scalarCondition", sq);
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public Map<String, CrawlingSessionInfoCQ> getSpecifyMyselfDerived() {
        return xgetSQueMap("specifyMyselfDerived");
    }

    @Override
    public String keepSpecifyMyselfDerived(final CrawlingSessionInfoCQ sq) {
        return xkeepSQue("specifyMyselfDerived", sq);
    }

    public Map<String, CrawlingSessionInfoCQ> getQueryMyselfDerived() {
        return xgetSQueMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerived(final CrawlingSessionInfoCQ sq) {
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
    protected Map<String, CrawlingSessionInfoCQ> _myselfExistsMap;

    public Map<String, CrawlingSessionInfoCQ> getMyselfExists() {
        return xgetSQueMap("myselfExists");
    }

    @Override
    public String keepMyselfExists(final CrawlingSessionInfoCQ sq) {
        return xkeepSQue("myselfExists", sq);
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    public Map<String, CrawlingSessionInfoCQ> getMyselfInScope() {
        return xgetSQueMap("myselfInScope");
    }

    @Override
    public String keepMyselfInScope(final CrawlingSessionInfoCQ sq) {
        return xkeepSQue("myselfInScope", sq);
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
