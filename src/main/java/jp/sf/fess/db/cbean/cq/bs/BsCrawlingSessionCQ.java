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

import jp.sf.fess.db.cbean.CrawlingSessionCB;
import jp.sf.fess.db.cbean.cq.CrawlingSessionCQ;
import jp.sf.fess.db.cbean.cq.CrawlingSessionInfoCQ;
import jp.sf.fess.db.cbean.cq.ciq.CrawlingSessionCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.chelper.HpCalculator;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of CRAWLING_SESSION.
 * @author DBFlute(AutoGenerator)
 */
public class BsCrawlingSessionCQ extends AbstractBsCrawlingSessionCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected CrawlingSessionCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsCrawlingSessionCQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from CRAWLING_SESSION) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public CrawlingSessionCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected CrawlingSessionCIQ xcreateCIQ() {
        final CrawlingSessionCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected CrawlingSessionCIQ xnewCIQ() {
        return new CrawlingSessionCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join CRAWLING_SESSION on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public CrawlingSessionCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final CrawlingSessionCIQ inlineQuery = inline();
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

    public Map<String, CrawlingSessionInfoCQ> getId_ExistsReferrer_CrawlingSessionInfoList() {
        return xgetSQueMap("id_ExistsReferrer_CrawlingSessionInfoList");
    }

    @Override
    public String keepId_ExistsReferrer_CrawlingSessionInfoList(
            final CrawlingSessionInfoCQ sq) {
        return xkeepSQue("id_ExistsReferrer_CrawlingSessionInfoList", sq);
    }

    public Map<String, CrawlingSessionInfoCQ> getId_NotExistsReferrer_CrawlingSessionInfoList() {
        return xgetSQueMap("id_NotExistsReferrer_CrawlingSessionInfoList");
    }

    @Override
    public String keepId_NotExistsReferrer_CrawlingSessionInfoList(
            final CrawlingSessionInfoCQ sq) {
        return xkeepSQue("id_NotExistsReferrer_CrawlingSessionInfoList", sq);
    }

    public Map<String, CrawlingSessionInfoCQ> getId_SpecifyDerivedReferrer_CrawlingSessionInfoList() {
        return xgetSQueMap("id_SpecifyDerivedReferrer_CrawlingSessionInfoList");
    }

    @Override
    public String keepId_SpecifyDerivedReferrer_CrawlingSessionInfoList(
            final CrawlingSessionInfoCQ sq) {
        return xkeepSQue("id_SpecifyDerivedReferrer_CrawlingSessionInfoList",
                sq);
    }

    public Map<String, CrawlingSessionInfoCQ> getId_InScopeRelation_CrawlingSessionInfoList() {
        return xgetSQueMap("id_InScopeRelation_CrawlingSessionInfoList");
    }

    @Override
    public String keepId_InScopeRelation_CrawlingSessionInfoList(
            final CrawlingSessionInfoCQ sq) {
        return xkeepSQue("id_InScopeRelation_CrawlingSessionInfoList", sq);
    }

    public Map<String, CrawlingSessionInfoCQ> getId_NotInScopeRelation_CrawlingSessionInfoList() {
        return xgetSQueMap("id_NotInScopeRelation_CrawlingSessionInfoList");
    }

    @Override
    public String keepId_NotInScopeRelation_CrawlingSessionInfoList(
            final CrawlingSessionInfoCQ sq) {
        return xkeepSQue("id_NotInScopeRelation_CrawlingSessionInfoList", sq);
    }

    public Map<String, CrawlingSessionInfoCQ> getId_QueryDerivedReferrer_CrawlingSessionInfoList() {
        return xgetSQueMap("id_QueryDerivedReferrer_CrawlingSessionInfoList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_CrawlingSessionInfoList(
            final CrawlingSessionInfoCQ sq) {
        return xkeepSQue("id_QueryDerivedReferrer_CrawlingSessionInfoList", sq);
    }

    public Map<String, Object> getId_QueryDerivedReferrer_CrawlingSessionInfoListParameter() {
        return xgetSQuePmMap("id_QueryDerivedReferrer_CrawlingSessionInfoList");
    }

    @Override
    public String keepId_QueryDerivedReferrer_CrawlingSessionInfoListParameter(
            final Object pm) {
        return xkeepSQuePm("id_QueryDerivedReferrer_CrawlingSessionInfoList",
                pm);
    }

    /**
     * Add order-by as ascend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsCrawlingSessionCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsCrawlingSessionCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _sessionId;

    public ConditionValue getSessionId() {
        if (_sessionId == null) {
            _sessionId = nCV();
        }
        return _sessionId;
    }

    @Override
    protected ConditionValue getCValueSessionId() {
        return getSessionId();
    }

    /**
     * Add order-by as ascend. <br />
     * SESSION_ID: {NotNull, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsCrawlingSessionCQ addOrderBy_SessionId_Asc() {
        regOBA("SESSION_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * SESSION_ID: {NotNull, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsCrawlingSessionCQ addOrderBy_SessionId_Desc() {
        regOBD("SESSION_ID");
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
     * NAME: {IX+, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsCrawlingSessionCQ addOrderBy_Name_Asc() {
        regOBA("NAME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * NAME: {IX+, VARCHAR(20)}
     * @return this. (NotNull)
     */
    public BsCrawlingSessionCQ addOrderBy_Name_Desc() {
        regOBD("NAME");
        return this;
    }

    protected ConditionValue _expiredTime;

    public ConditionValue getExpiredTime() {
        if (_expiredTime == null) {
            _expiredTime = nCV();
        }
        return _expiredTime;
    }

    @Override
    protected ConditionValue getCValueExpiredTime() {
        return getExpiredTime();
    }

    /**
     * Add order-by as ascend. <br />
     * EXPIRED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsCrawlingSessionCQ addOrderBy_ExpiredTime_Asc() {
        regOBA("EXPIRED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * EXPIRED_TIME: {TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsCrawlingSessionCQ addOrderBy_ExpiredTime_Desc() {
        regOBD("EXPIRED_TIME");
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
    public BsCrawlingSessionCQ addOrderBy_CreatedTime_Asc() {
        regOBA("CREATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsCrawlingSessionCQ addOrderBy_CreatedTime_Desc() {
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
    public BsCrawlingSessionCQ addSpecifiedDerivedOrderBy_Asc(
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
    public BsCrawlingSessionCQ addSpecifiedDerivedOrderBy_Desc(
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
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    public Map<String, CrawlingSessionCQ> getScalarCondition() {
        return xgetSQueMap("scalarCondition");
    }

    @Override
    public String keepScalarCondition(final CrawlingSessionCQ sq) {
        return xkeepSQue("scalarCondition", sq);
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public Map<String, CrawlingSessionCQ> getSpecifyMyselfDerived() {
        return xgetSQueMap("specifyMyselfDerived");
    }

    @Override
    public String keepSpecifyMyselfDerived(final CrawlingSessionCQ sq) {
        return xkeepSQue("specifyMyselfDerived", sq);
    }

    public Map<String, CrawlingSessionCQ> getQueryMyselfDerived() {
        return xgetSQueMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerived(final CrawlingSessionCQ sq) {
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
    protected Map<String, CrawlingSessionCQ> _myselfExistsMap;

    public Map<String, CrawlingSessionCQ> getMyselfExists() {
        return xgetSQueMap("myselfExists");
    }

    @Override
    public String keepMyselfExists(final CrawlingSessionCQ sq) {
        return xkeepSQue("myselfExists", sq);
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    public Map<String, CrawlingSessionCQ> getMyselfInScope() {
        return xgetSQueMap("myselfInScope");
    }

    @Override
    public String keepMyselfInScope(final CrawlingSessionCQ sq) {
        return xkeepSQue("myselfInScope", sq);
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() {
        return CrawlingSessionCB.class.getName();
    }

    protected String xCQ() {
        return CrawlingSessionCQ.class.getName();
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
