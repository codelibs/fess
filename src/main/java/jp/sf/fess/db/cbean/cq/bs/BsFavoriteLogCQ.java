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

import jp.sf.fess.db.cbean.FavoriteLogCB;
import jp.sf.fess.db.cbean.cq.FavoriteLogCQ;
import jp.sf.fess.db.cbean.cq.UserInfoCQ;
import jp.sf.fess.db.cbean.cq.ciq.FavoriteLogCIQ;

import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.chelper.HpCalculator;
import org.seasar.dbflute.cbean.coption.ConditionOption;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The base condition-query of FAVORITE_LOG.
 * @author DBFlute(AutoGenerator)
 */
public class BsFavoriteLogCQ extends AbstractBsFavoriteLogCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected FavoriteLogCIQ _inlineQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsFavoriteLogCQ(final ConditionQuery referrerQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        super(referrerQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                 InlineView/OrClause
    //                                                                 ===================
    /**
     * Prepare InlineView query. <br />
     * {select ... from ... left outer join (select * from FAVORITE_LOG) where FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">inline()</span>.setFoo...;
     * </pre>
     * @return The condition-query for InlineView query. (NotNull)
     */
    public FavoriteLogCIQ inline() {
        if (_inlineQuery == null) {
            _inlineQuery = xcreateCIQ();
        }
        _inlineQuery.xsetOnClause(false);
        return _inlineQuery;
    }

    protected FavoriteLogCIQ xcreateCIQ() {
        final FavoriteLogCIQ ciq = xnewCIQ();
        ciq.xsetBaseCB(_baseCB);
        return ciq;
    }

    protected FavoriteLogCIQ xnewCIQ() {
        return new FavoriteLogCIQ(xgetReferrerQuery(), xgetSqlClause(),
                xgetAliasName(), xgetNestLevel(), this);
    }

    /**
     * Prepare OnClause query. <br />
     * {select ... from ... left outer join FAVORITE_LOG on ... and FOO = [value] ...}
     * <pre>
     * cb.query().queryMemberStatus().<span style="color: #DD4747">on()</span>.setFoo...;
     * </pre>
     * @return The condition-query for OnClause query. (NotNull)
     * @throws IllegalConditionBeanOperationException When this condition-query is base query.
     */
    public FavoriteLogCIQ on() {
        if (isBaseQuery()) {
            throw new IllegalConditionBeanOperationException(
                    "OnClause for local table is unavailable!");
        }
        final FavoriteLogCIQ inlineQuery = inline();
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
    public BsFavoriteLogCQ addOrderBy_Id_Asc() {
        regOBA("ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return this. (NotNull)
     */
    public BsFavoriteLogCQ addOrderBy_Id_Desc() {
        regOBD("ID");
        return this;
    }

    protected ConditionValue _userId;

    public ConditionValue getUserId() {
        if (_userId == null) {
            _userId = nCV();
        }
        return _userId;
    }

    @Override
    protected ConditionValue getCValueUserId() {
        return getUserId();
    }

    public Map<String, UserInfoCQ> getUserId_InScopeRelation_UserInfo() {
        return xgetSQueMap("userId_InScopeRelation_UserInfo");
    }

    @Override
    public String keepUserId_InScopeRelation_UserInfo(final UserInfoCQ sq) {
        return xkeepSQue("userId_InScopeRelation_UserInfo", sq);
    }

    public Map<String, UserInfoCQ> getUserId_NotInScopeRelation_UserInfo() {
        return xgetSQueMap("userId_NotInScopeRelation_UserInfo");
    }

    @Override
    public String keepUserId_NotInScopeRelation_UserInfo(final UserInfoCQ sq) {
        return xkeepSQue("userId_NotInScopeRelation_UserInfo", sq);
    }

    /**
     * Add order-by as ascend. <br />
     * USER_ID: {UQ+, IX, NotNull, BIGINT(19), FK to USER_INFO}
     * @return this. (NotNull)
     */
    public BsFavoriteLogCQ addOrderBy_UserId_Asc() {
        regOBA("USER_ID");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * USER_ID: {UQ+, IX, NotNull, BIGINT(19), FK to USER_INFO}
     * @return this. (NotNull)
     */
    public BsFavoriteLogCQ addOrderBy_UserId_Desc() {
        regOBD("USER_ID");
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
     * URL: {+UQ, NotNull, VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFavoriteLogCQ addOrderBy_Url_Asc() {
        regOBA("URL");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * URL: {+UQ, NotNull, VARCHAR(4000)}
     * @return this. (NotNull)
     */
    public BsFavoriteLogCQ addOrderBy_Url_Desc() {
        regOBD("URL");
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
    public BsFavoriteLogCQ addOrderBy_CreatedTime_Asc() {
        regOBA("CREATED_TIME");
        return this;
    }

    /**
     * Add order-by as descend. <br />
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return this. (NotNull)
     */
    public BsFavoriteLogCQ addOrderBy_CreatedTime_Desc() {
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
    public BsFavoriteLogCQ addSpecifiedDerivedOrderBy_Asc(final String aliasName) {
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
    public BsFavoriteLogCQ addSpecifiedDerivedOrderBy_Desc(
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
        final FavoriteLogCQ bq = (FavoriteLogCQ) bqs;
        final FavoriteLogCQ uq = (FavoriteLogCQ) uqs;
        if (bq.hasConditionQueryUserInfo()) {
            uq.queryUserInfo().reflectRelationOnUnionQuery(bq.queryUserInfo(),
                    uq.queryUserInfo());
        }
    }

    // ===================================================================================
    //                                                                       Foreign Query
    //                                                                       =============
    /**
     * Get the condition-query for relation table. <br />
     * USER_INFO by my USER_ID, named 'userInfo'.
     * @return The instance of condition-query. (NotNull)
     */
    public UserInfoCQ queryUserInfo() {
        return getConditionQueryUserInfo();
    }

    public UserInfoCQ getConditionQueryUserInfo() {
        final String prop = "userInfo";
        if (!xhasQueRlMap(prop)) {
            xregQueRl(prop, xcreateQueryUserInfo());
            xsetupOuterJoinUserInfo();
        }
        return xgetQueRlMap(prop);
    }

    protected UserInfoCQ xcreateQueryUserInfo() {
        final String nrp = xresolveNRP("FAVORITE_LOG", "userInfo");
        final String jan = xresolveJAN(nrp, xgetNNLvl());
        return xinitRelCQ(new UserInfoCQ(this, xgetSqlClause(), jan,
                xgetNNLvl()), _baseCB, "userInfo", nrp);
    }

    protected void xsetupOuterJoinUserInfo() {
        xregOutJo("userInfo");
    }

    public boolean hasConditionQueryUserInfo() {
        return xhasQueRlMap("userInfo");
    }

    @Override
    protected Map<String, Object> xfindFixedConditionDynamicParameterMap(
            final String property) {
        return null;
    }

    // ===================================================================================
    //                                                                     ScalarCondition
    //                                                                     ===============
    public Map<String, FavoriteLogCQ> getScalarCondition() {
        return xgetSQueMap("scalarCondition");
    }

    @Override
    public String keepScalarCondition(final FavoriteLogCQ sq) {
        return xkeepSQue("scalarCondition", sq);
    }

    // ===================================================================================
    //                                                                       MyselfDerived
    //                                                                       =============
    public Map<String, FavoriteLogCQ> getSpecifyMyselfDerived() {
        return xgetSQueMap("specifyMyselfDerived");
    }

    @Override
    public String keepSpecifyMyselfDerived(final FavoriteLogCQ sq) {
        return xkeepSQue("specifyMyselfDerived", sq);
    }

    public Map<String, FavoriteLogCQ> getQueryMyselfDerived() {
        return xgetSQueMap("queryMyselfDerived");
    }

    @Override
    public String keepQueryMyselfDerived(final FavoriteLogCQ sq) {
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
    protected Map<String, FavoriteLogCQ> _myselfExistsMap;

    public Map<String, FavoriteLogCQ> getMyselfExists() {
        return xgetSQueMap("myselfExists");
    }

    @Override
    public String keepMyselfExists(final FavoriteLogCQ sq) {
        return xkeepSQue("myselfExists", sq);
    }

    // ===================================================================================
    //                                                                       MyselfInScope
    //                                                                       =============
    public Map<String, FavoriteLogCQ> getMyselfInScope() {
        return xgetSQueMap("myselfInScope");
    }

    @Override
    public String keepMyselfInScope(final FavoriteLogCQ sq) {
        return xkeepSQue("myselfInScope", sq);
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xCB() {
        return FavoriteLogCB.class.getName();
    }

    protected String xCQ() {
        return FavoriteLogCQ.class.getName();
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
